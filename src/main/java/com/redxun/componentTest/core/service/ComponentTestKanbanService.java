package com.redxun.componentTest.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.componentTest.core.dao.ComponentTestKanbanDao;
import com.redxun.core.excel.ExcelReaderUtil;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.ExcelUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.materielextend.core.service.MaterielService;
import com.redxun.rdmCommon.core.util.RdmCommonUtil;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.sys.core.entity.SysDic;
import com.redxun.sys.core.entity.SysTree;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.sys.core.manager.SysSeqIdManager;
import com.redxun.sys.core.manager.SysTreeManager;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.persistence.criteria.CriteriaBuilder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.*;

@Service
public class ComponentTestKanbanService {
    private static Logger logger = LoggerFactory.getLogger(ComponentTestKanbanService.class);
    @Autowired
    private ComponentTestKanbanDao componentTestKanbanDao;
    @Autowired
    private SysDicManager sysDicManager;
    @Autowired
    private SysSeqIdManager sysSeqIdManager;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private SysTreeManager sysTreeManager;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;

    //..
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        getListParams(params, request);
        //--以下数据权限处理
        //admin/分管领导/零部件试验特权人员(qinqiang,xiaoyunbo)看所有
        //普通人看自己相关的
        String currentUserId = ContextUtil.getCurrentUserId();
        String currentUserNo = ContextUtil.getCurrentUser().getUserNo();
        boolean isComponentTestResultAll = false;
        String componentTestResultAll = sysDicManager.getBySysTreeKeyAndDicKey(
                "componentTestManGroups", "ComponentTestResultAll").getValue();
        List<String> componentTestResultAllList = Arrays.asList(componentTestResultAll.split(","));
        for (String userNo : componentTestResultAllList) {
            if (currentUserNo.equalsIgnoreCase(userNo)) {
                isComponentTestResultAll = true;
                break;
            }
        }
        if (!currentUserNo.equalsIgnoreCase("admin") && !isComponentTestResultAll) {
            params.put("currentUserId", currentUserId);
            // 查询角色是否为分管领导
            Map<String, Object> queryUserParam = new HashMap<>();
            queryUserParam.put("userId", currentUserId);
            queryUserParam.put("groupName", RdmConst.FGLD);
            List<Map<String, Object>> queryRoleResult = xcmgProjectOtherDao.queryUserRoles(queryUserParam);
            if (queryRoleResult != null && !queryRoleResult.isEmpty()) {//分管领导
                params.put("roleName", "fgld");
            } else if (rdmZhglUtil.judgeIsPointRoleUser(currentUserId, RdmConst.AllDATA_QUERY_NAME)) {
                params.put("roleName", "fgld");
            } else {
                params.put("roleName", "ptyg");
            }
        }
        //--以上数据权限处理

        List<JSONObject> businessList = componentTestKanbanDao.dataListQuery(params);
        int businessListCount = componentTestKanbanDao.countDataListQuery(params);
        for (JSONObject oneObj : businessList) {
            //获取合同名称列表
            List<String> testContractNameList = componentTestKanbanDao.getTestContractNameListByBusinessId(oneObj.getString("id"));
            //如果有合同名称
            if (testContractNameList.size() > 0) {
                StringBuilder stringBuilder = new StringBuilder();
                for (String fileName : testContractNameList) {
                    stringBuilder.append("《").append(fileName).append("》");
                }
                oneObj.put("testContract", stringBuilder.toString());
            }
            //获取检测报告名称列表
            List<String> testReportNameList = componentTestKanbanDao.getTestReportNameListByBusinessId(oneObj.getString("id"));
            //如果有检测报告名称
            if (testReportNameList.size() > 0) {
                StringBuilder stringBuilder = new StringBuilder();
                for (String fileName : testReportNameList) {
                    stringBuilder.append("《").append(fileName).append("》");
                }
                oneObj.put("testReport", stringBuilder.toString());
            }
            //2022-07-07领料人关联显示
            if (StringUtil.isNotEmpty(oneObj.getString("sampleSource")) &&
                    oneObj.getString("sampleSource").equalsIgnoreCase("厂内领料")) {
                oneObj.put("sampleSource", oneObj.getString("sampleSource") + "(" + oneObj.getString("receiver") + ")");
            }
            //以上领料人关联显示
        }
        result.setData(businessList);
        result.setTotal(businessListCount);
        return result;
    }

    //..
    private void getListParams(Map<String, Object> params, HttpServletRequest request) {
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)) {
            params.put("sortField", sortField);
            params.put("sortOrder", sortOrder);
        }
        if (StringUtils.isEmpty(sortField)) {
            params.put("sortField", "testNo");
            params.put("sortOrder", "desc");
        }
        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    params.put(name, value);
                }
            }
        }
        // 增加分页条件
        params.put("startIndex", 0);
        params.put("pageSize", 20);
        String pageIndex = request.getParameter("pageIndex");
        String pageSize = request.getParameter("pageSize");
        if (StringUtils.isNotBlank(pageIndex) && StringUtils.isNotBlank(pageSize)) {
            params.put("startIndex", Integer.parseInt(pageSize) * Integer.parseInt(pageIndex));
            params.put("pageSize", Integer.parseInt(pageSize));
        }
    }

    //..
    public JSONObject queryDataById(String businessId) {
        JSONObject result = new JSONObject();
        if (StringUtils.isBlank(businessId)) {
            return result;
        }
        JSONObject jsonObject = componentTestKanbanDao.queryDataById(businessId);
        if (jsonObject == null) {
            return result;
        }
        return jsonObject;
    }

    //..
    public void saveBusiness(JsonResult result, String dataStr) {
        JSONObject dataObj = JSONObject.parseObject(dataStr);
        if (dataObj == null || dataObj.isEmpty()) {
            result.setSuccess(false);
            result.setMessage("表单内容为空，操作失败！");
            return;
        }
        iniStatusAndProgress(dataObj);
        iniSameTest(dataObj);
        if (StringUtils.isBlank(dataObj.getString("id"))) {
            dataObj.put("id", IdUtil.getId());
            dataObj.put("componentTestAbnormalTag", "true");
            if (StringUtil.isEmpty(dataObj.getString("testNo")) &&
                    StringUtil.isNotEmpty(dataObj.getString("actualTestMonth"))) {
                dataObj.put("testNo", getSysSeq(dataObj));
            }
            if (StringUtil.isNotEmpty(dataObj.getString("applyUserId"))) {
                JSONObject jsonObject = componentTestKanbanDao.getDeptByUserId(dataObj.getString("applyUserId"));
                dataObj.put("applyDepId", jsonObject.getString("GROUP_ID_"));
                dataObj.put("applyDep", jsonObject.getString("NAME_"));
            } else {
                dataObj.put("applyDepId", "");
                dataObj.put("applyDep", "");
            }
            dataObj.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            dataObj.put("CREATE_TIME_", new Date());
            componentTestKanbanDao.insertBusiness(dataObj);
            result.setData(dataObj.getString("id"));
        } else {
            //todo:这里改为和新的流程驱动一致的规则
            if (StringUtil.isEmpty(dataObj.getString("testNo")) &&
                    StringUtil.isNotEmpty(dataObj.getString("actualTestMonth"))) {
                dataObj.put("testNo", getSysSeq(dataObj));
            }
            if (StringUtil.isNotEmpty(dataObj.getString("applyUserId"))) {
                JSONObject jsonObject = componentTestKanbanDao.getDeptByUserId(dataObj.getString("applyUserId"));
                dataObj.put("applyDepId", jsonObject.getString("GROUP_ID_"));
                dataObj.put("applyDep", jsonObject.getString("NAME_"));
            } else {
                dataObj.put("applyDepId", "");
                dataObj.put("applyDep", "");
            }
            dataObj.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            dataObj.put("UPDATE_TIME_", new Date());
            componentTestKanbanDao.updateBusiness(dataObj);
            result.setData(dataObj.getString("id"));
        }
    }

    //..p
    private void iniStatusAndProgress(JSONObject dataObj) {
        if (StringUtils.isBlank(dataObj.getString("id")) &&
                StringUtils.isBlank(dataObj.getString("testStatus"))) {
            dataObj.put("testStatus", "未开展");
            dataObj.put("testProgress", "0");
        }
        if (StringUtils.isNotEmpty(dataObj.getString("id"))) {
            List<String> mainKanbanIdList = new ArrayList<>();
            mainKanbanIdList.add(dataObj.getString("id"));
            List<JSONObject> contractFileList = this.getTestContractFileList(mainKanbanIdList);
            List<JSONObject> reportFileList = this.getTestReportFileList(mainKanbanIdList);
            if (contractFileList.size() > 0) {
                //有合同
                if (reportFileList.size() > 0) {
                    //有报告
                    dataObj.put("testStatus", "已完成");
                    dataObj.put("testProgress", "1");
                } else {
                    //无报告
                    if (!dataObj.getString("testProgress").equals("0.5") && !dataObj.getString("testProgress").equals("0.75")) {
                        //屏蔽掉手动更新的两个特殊值
                        dataObj.put("testStatus", "进行中");
                        dataObj.put("testProgress", "0.25");
                    }
                }
            } else {
                //无合同
                dataObj.put("testStatus", "未开展");
                dataObj.put("testProgress", "0");
            }
        }
    }

    //..p
    private void iniSameTest(JSONObject dataObj) {
        if (dataObj.getString("testResult").equals("合格")) {
            JSONObject params = new JSONObject();
            params.put("materialCode", dataObj.getString("materialCode"));
            params.put("componentModel", dataObj.getString("componentModel"));
            List<JSONObject> businessList = componentTestKanbanDao.dataListQuery(params);
            for (JSONObject business : businessList) {
                business.put("componentTestAbnormalTag", "false");
                componentTestKanbanDao.updateBusiness(business);
            }
            dataObj.put("componentTestAbnormalTag", "false");
        }
    }

    //..
    public JsonResult deleteBusiness(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> businessIds = Arrays.asList(ids);
        List<JSONObject> filesContract = this.getTestContractFileList(businessIds);
        List<JSONObject> filesReport = this.getTestReportFileList(businessIds);
        String filePathBaseContract = sysDicManager.getBySysTreeKeyAndDicKey(
                "componentTestUploadPosition", "testContract").getValue();
        String filePathBaseReport = sysDicManager.getBySysTreeKeyAndDicKey(
                "componentTestUploadPosition", "testReport").getValue();
        for (JSONObject oneFile : filesContract) {
            rdmZhglFileManager.deleteOneFileFromDisk(oneFile.getString("id"),
                    oneFile.getString("fileName"), oneFile.getString("mainKanbanId"), filePathBaseContract);
        }
        for (JSONObject oneFile : filesReport) {
            rdmZhglFileManager.deleteOneFileFromDisk(oneFile.getString("id"),
                    oneFile.getString("fileName"), oneFile.getString("mainKanbanId"), filePathBaseReport);
        }
        for (String oneBusinessId : businessIds) {
            rdmZhglFileManager.deleteDirFromDisk(oneBusinessId, filePathBaseContract);
            rdmZhglFileManager.deleteDirFromDisk(oneBusinessId, filePathBaseReport);
        }
        Map<String, Object> param = new HashMap<>();
        param.put("businessIds", businessIds);
        componentTestKanbanDao.deleteContractFile(param);
        componentTestKanbanDao.deleteReportFile(param);
        componentTestKanbanDao.deleteStandard(param);
        List<JSONObject> msgReadyToDeleteList = componentTestKanbanDao.queryBindingStandardMsgList(param);
        List<String> msgReadyToDeleteIdList = new ArrayList<>();
        for (JSONObject msgReadyToDelete : msgReadyToDeleteList) {
            msgReadyToDeleteIdList.add(msgReadyToDelete.getString("id"));
        }
        param.put("msgIds", msgReadyToDeleteIdList.size() > 0 ? msgReadyToDeleteIdList : "");
        componentTestKanbanDao.deleteBindingStandardMsgItem(param);
        componentTestKanbanDao.deleteBindingStandardMsg(param);
        componentTestKanbanDao.deleteBusiness(param);
        return result;
    }

    //..
    public void exportExcel(HttpServletRequest request, HttpServletResponse response) {
        String filterParams = request.getParameter("filter");
        JSONArray jsonArray = JSONArray.parseArray(filterParams);
        List<String> ids = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            ids.add(jsonArray.getJSONObject(i).getString("value"));
        }
        JSONObject param = new JSONObject();
        param.put("ids", ids);
        List<JSONObject> businessList = componentTestKanbanDao.dataListQuery(param);

        for (JSONObject oneObj : businessList) {
            //获取合同名称列表
            List<String> testContractNameList = componentTestKanbanDao.getTestContractNameListByBusinessId(oneObj.getString("id"));
            //如果有合同名称
            if (testContractNameList.size() > 0) {
                StringBuilder stringBuilder = new StringBuilder();
                for (String fileName : testContractNameList) {
                    stringBuilder.append("《").append(fileName).append("》");
                }
                oneObj.put("testContract", stringBuilder.toString());
            }
            //获取检测报告名称列表
            List<String> testReportNameList = componentTestKanbanDao.getTestReportNameListByBusinessId(oneObj.getString("id"));
            //如果有检测报告名称
            if (testReportNameList.size() > 0) {
                StringBuilder stringBuilder = new StringBuilder();
                for (String fileName : testReportNameList) {
                    stringBuilder.append("《").append(fileName).append("》");
                }
                oneObj.put("testReport", stringBuilder.toString());
            }
        }

        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "零部件试验";
        String excelName = nowDate + title;
        String[] fieldNames = {"试验编号", "零部件类别", "零部件名称", "零部件型号", "物料编码", "样品类型", "配套主机型号", "配套主机名称",
                "配套主机单位", "供应商名称", "承担单位", "试验类型", "计划试验时间", "申请人", "申请部门", "试验计划类型",
                "试验负责人", "试验运行状态", "实际试验时间", "试验进度", "试验完成时间", "试验结果", "不合格项说明", "不合格零部件当前状态",
                "检测报告", "检测合同", "检测合同状态", "检测费用", "试验次数", "样件来源", "样件处理方式", "备注"};
        String[] fieldCodes = {"testNo", "componentCategory", "componentName", "componentModel", "materialCode", "sampleType", "machineModel", "machineName",
                "machineCompany", "supplierName", "laboratory", "testType", "plannedTestMonth", "applyUser", "applyDep", "testCategory",
                "testLeader", "testStatus", "actualTestMonth", "testProgress", "completeTestMonth", "testResult", "nonconformingDescription", "unqualifiedStatus",
                "testReport", "testContract", "testContractStatus", "testCost", "testRounds", "sampleSource", "sampleProcessingMethod", "remark"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(businessList, fieldNames, fieldCodes, title);
        //输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);

    }

    //..
    public void importExcel(JSONObject result, HttpServletRequest request) {
        int count = 0;
        try {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            MultipartFile fileObj = multipartRequest.getFile("importFile");
            if (fileObj == null) {
                result.put("message", "数据导入失败，内容为空！");
                return;
            }
            String fileName = ((CommonsMultipartFile) fileObj).getFileItem().getName();
            ((CommonsMultipartFile) fileObj).getFileItem().getName().endsWith(ExcelReaderUtil.EXCEL03_EXTENSION);
            Workbook wb = null;
            if (fileName.endsWith(ExcelReaderUtil.EXCEL03_EXTENSION)) {
                wb = new HSSFWorkbook(fileObj.getInputStream());
            } else if (fileName.endsWith(ExcelReaderUtil.EXCEL07_EXTENSION)) {
                wb = new XSSFWorkbook(fileObj.getInputStream());
            }
            Sheet sheet = wb.getSheet("模板");
            if (sheet == null) {
                logger.error("找不到导入模板");
                result.put("message", "数据导入失败，找不到导入模板导入页！");
                return;
            }
            int rowNum = sheet.getPhysicalNumberOfRows();
            if (rowNum < 2) {
                logger.error("找不到标题行");
                result.put("message", "数据导入失败，找不到标题行！");
                return;
            }

            // 解析标题部分
            Row titleRow = sheet.getRow(1);
            if (titleRow == null) {
                logger.error("找不到标题行");
                result.put("message", "数据导入失败，找不到标题行！");
                return;
            }
            List<String> titleList = new ArrayList<>();
            for (int i = 0; i < titleRow.getLastCellNum(); i++) {
                titleList.add(StringUtils.trim(titleRow.getCell(i).getStringCellValue()));
            }

            if (rowNum < 3) {
                logger.info("数据行为空");
                result.put("message", "数据导入完成，数据行为空！");
                return;
            }
            // 解析验证数据部分，任何一行的任何一项不满足条件，则直接返回失败
            List<Map<String, Object>> itemList = new ArrayList<>();
            String id = IdUtil.getId();
            for (int i = 2; i < rowNum; i++) {
                count = i;
                Row row = sheet.getRow(i);
                JSONObject rowParse = generateDataFromRow(row, itemList, titleList, id);
                if (!rowParse.getBoolean("result")) {
                    result.put("message", "数据导入失败，第" + (i + 1) + "行数据错误：" + rowParse.getString("message"));
                    return;
                }
            }
            for (int i = 0; i < itemList.size(); i++) {
                itemList.get(i).put("componentTestAbnormalTag", "true");
                componentTestKanbanDao.insertBusiness(itemList.get(i));
            }
            result.put("message", "数据导入成功！");
        } catch (Exception e) {
            logger.error("Exception in importExcel", e);
            result.put("message", "数据导入失败，系统异常！");
        }
    }

    //..
    private JSONObject generateDataFromRow(Row row, List<Map<String, Object>> itemList, List<String> titleList, String mainId) {
        JSONObject oneRowCheck = new JSONObject();
        oneRowCheck.put("result", false);
        Map<String, Object> oneRowMap = new HashMap<>();
        for (int i = 0; i < titleList.size(); i++) {
            String title = titleList.get(i);
            title = title.replaceAll(" ", "");
            Cell cell = row.getCell(i);
            String cellValue = null;
            if (cell != null) {
                cellValue = ExcelUtil.getCellFormatValue(cell);
            }
            switch (title) {
                case "零部件类别"://必输,字典约束
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "零部件类别为空");
                        return oneRowCheck;
                    }
                    if (StringUtils.isNotBlank(cellValue)) {
                        String sysDicCheckResult = sysDicCheck(cellValue, "componentCategory");
                        if (!sysDicCheckResult.equalsIgnoreCase("ok")) {
                            oneRowCheck.put("message", "零部件类别必须为字典要求的值:" + sysDicCheckResult);
                            return oneRowCheck;
                        }
                    }
                    oneRowMap.put("componentCategory", cellValue);
                    break;
                case "零部件名称":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "零部件名称为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("componentName", cellValue);
                    break;
                case "零部件型号":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "零部件型号为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("componentModel", cellValue);
                    break;
                case "物料编码":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "物料编码为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("materialCode", cellValue);
                    break;
                case "样品类型"://必输,字典约束
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "样品类型为空");
                        return oneRowCheck;
                    }
                    if (StringUtils.isNotBlank(cellValue)) {
                        String sysDicCheckResult = sysDicCheck(cellValue, "sampleType");
                        if (!sysDicCheckResult.equalsIgnoreCase("ok")) {
                            oneRowCheck.put("message", "样品类型必须为字典要求的值:" + sysDicCheckResult);
                            return oneRowCheck;
                        }
                    }
                    oneRowMap.put("sampleType", cellValue);
                    break;
                case "配套主机名称":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "配套主机名称为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("machineName", cellValue);
                    break;
                case "配套主机型号":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "配套主机型号为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("machineModel", cellValue);
                    break;
                case "配套主机单位":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "配套主机单位为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("machineCompany", cellValue);
                    break;
                case "供应商名称":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "供应商名称为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("supplierName", cellValue);
                    break;
                case "承担单位":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "承担单位为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("laboratory", cellValue);
                    break;
                case "试验类型"://必输,字典约束
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "试验类型为空");
                        return oneRowCheck;
                    }
                    if (StringUtils.isNotBlank(cellValue)) {
                        String sysDicCheckResult = sysDicCheck(cellValue, "testType");
                        if (!sysDicCheckResult.equalsIgnoreCase("ok")) {
                            oneRowCheck.put("message", "试验类型必须为字典要求的值:" + sysDicCheckResult);
                            return oneRowCheck;
                        }
                    }
                    oneRowMap.put("testType", cellValue);
                    break;
                case "计划试验时间":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "计划试验时间为空");
                        return oneRowCheck;
                    }
                    if (StringUtils.isNotBlank(cellValue)) {
                        if ((StringUtil.getAppearNumber(cellValue, "-") != 1) || cellValue.length() != 7) {
                            oneRowCheck.put("message", "计划试验时间格式必须为YYYY-MM");
                            return oneRowCheck;
                        }
                    }
                    oneRowMap.put("plannedTestMonth", cellValue);
                    break;
                case "申请人"://不必输,但必须有用户且不重名，一次性将部门信息也查出来了
                    if (StringUtils.isNotBlank(cellValue)) {
                        List<JSONObject> jsonObjects = componentTestKanbanDao.getUserByFullName(cellValue);
                        if (jsonObjects.size() == 0) {
                            oneRowCheck.put("message", "用户不存在，建议先不导入姓名，后期手工维护");
                            return oneRowCheck;
                        } else if (jsonObjects.size() > 1) {
                            oneRowCheck.put("message", "系统中存在同名用户，建议先不导入姓名，后期手工维护");
                            return oneRowCheck;
                        }
                        oneRowMap.put("applyUserId", jsonObjects.get(0).getString("USER_ID_"));
                        oneRowMap.put("applyUser", cellValue);
                        JSONObject jsonObject = componentTestKanbanDao.getDeptByUserId(jsonObjects.get(0).getString("USER_ID_"));
                        oneRowMap.put("applyDepId", jsonObject.getString("GROUP_ID_"));
                        oneRowMap.put("applyDep", jsonObject.getString("NAME_"));
                    }
                    break;
                case "试验计划类型"://必输,字典约束
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "试验计划类型为空");
                        return oneRowCheck;
                    }
                    if (StringUtils.isNotBlank(cellValue)) {
                        String sysDicCheckResult = sysDicCheck(cellValue, "testCategory");
                        if (!sysDicCheckResult.equalsIgnoreCase("ok")) {
                            oneRowCheck.put("message", "试验计划类型必须为字典要求的值:" + sysDicCheckResult);
                            return oneRowCheck;
                        }
                    }
                    oneRowMap.put("testCategory", cellValue);
                    break;
                case "试验负责人"://不必输,但必须有用户且不重名，一次性将部门信息也查出来了
                    if (StringUtils.isNotBlank(cellValue)) {
                        List<JSONObject> jsonObjects = componentTestKanbanDao.getUserByFullName(cellValue);
                        if (jsonObjects.size() == 0) {
                            oneRowCheck.put("message", "用户不存在，建议先不导入姓名，后期手工维护");
                            return oneRowCheck;
                        } else if (jsonObjects.size() > 1) {
                            oneRowCheck.put("message", "系统中存在同名用户，建议先不导入姓名，后期手工维护");
                            return oneRowCheck;
                        }
                        oneRowMap.put("testLeaderId", jsonObjects.get(0).getString("USER_ID_"));
                        oneRowMap.put("testLeader", cellValue);
                    }
                    break;
                case "试验运行状态"://必输,字典约束
                    if (StringUtils.isNotBlank(cellValue)) {
                        String sysDicCheckResult = sysDicCheck(cellValue, "testStatus");
                        if (!sysDicCheckResult.equalsIgnoreCase("ok")) {
                            oneRowCheck.put("message", "试验运行状态必须为字典要求的值:" + sysDicCheckResult);
                            return oneRowCheck;
                        }
                    }
                    oneRowMap.put("testStatus", cellValue);
                    break;
                case "实际试验时间":
                    if (StringUtils.isNotBlank(cellValue)) {
                        if ((StringUtil.getAppearNumber(cellValue, "-") != 1) || cellValue.length() != 7) {
                            oneRowCheck.put("message", "实际试验时间格式必须为YYYY-MM");
                            return oneRowCheck;
                        }
                        oneRowMap.put("actualTestMonth", cellValue);
                        oneRowMap.put("testNo", getSysSeq(oneRowMap));
                    }
                    oneRowMap.put("actualTestMonth", cellValue);
                    break;
                case "试验进度":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowMap.put("testProgress", "0");
                    } else {
                        Boolean strResult = cellValue.matches("[0-9]+.?[0-9]*");
                        if (strResult == true) {
                            oneRowMap.put("testProgress", cellValue);
                        } else {
                            oneRowCheck.put("message", "试验进度必须为一个0-1之间的小数");
                            return oneRowCheck;
                        }
                    }
                    break;
                case "试验完成时间":
                    if (StringUtils.isNotBlank(cellValue)) {
                        if ((StringUtil.getAppearNumber(cellValue, "-") != 1) || cellValue.length() != 7) {
                            oneRowCheck.put("message", "试验完成时间格式必须为YYYY-MM");
                            return oneRowCheck;
                        }
                    }
                    oneRowMap.put("completeTestMonth", cellValue);
                    break;
                case "试验结果"://值约束
                    if (StringUtils.isNotBlank(cellValue)) {
                        if (!cellValue.equalsIgnoreCase("合格") &&
                                !cellValue.equalsIgnoreCase("不合格") &&
                                !cellValue.equalsIgnoreCase("研究性试验数据")) {
                            oneRowCheck.put("message", "试验结果只能是合格、不合格、研究性试验数据");
                            return oneRowCheck;
                        }
                    }
                    oneRowMap.put("testResult", cellValue);
                    break;
                case "不合格项说明":
                    oneRowMap.put("nonconformingDescription", cellValue);
                    break;
                case "不合格零部件当前状态"://字典约束
                    if (StringUtils.isNotBlank(cellValue)) {
                        String sysDicCheckResult = sysDicCheck(cellValue, "unqualifiedStatus");
                        if (!sysDicCheckResult.equalsIgnoreCase("ok")) {
                            oneRowCheck.put("message", "不合格零部件当前状态必须为字典要求的值:" + sysDicCheckResult);
                            return oneRowCheck;
                        }
                    }
                    oneRowMap.put("unqualifiedStatus", cellValue);
                    break;
                case "检测合同状态"://字典约束
                    if (StringUtils.isNotBlank(cellValue)) {
                        String sysDicCheckResult = sysDicCheck(cellValue, "testContractStatus");
                        if (!sysDicCheckResult.equalsIgnoreCase("ok")) {
                            oneRowCheck.put("message", "检测合同状态必须为字典要求的值:" + sysDicCheckResult);
                            return oneRowCheck;
                        }
                    }
                    oneRowMap.put("testContractStatus", cellValue);
                    break;
                case "检测费用":
                    if (StringUtils.isNotBlank(cellValue)) {
                        Boolean strResult = cellValue.matches("[0-9]+.?[0-9]*");
                        if (strResult == false) {
                            oneRowCheck.put("message", "检测费用必须为数字");
                            return oneRowCheck;
                        }
                    }
                    oneRowMap.put("testCost", cellValue);
                    break;
                case "试验次数"://字典约束
                    if (StringUtils.isNotBlank(cellValue)) {
                        String sysDicCheckResult = sysDicCheck(cellValue, "testRounds");
                        if (!sysDicCheckResult.equalsIgnoreCase("ok")) {
                            oneRowCheck.put("message", "试验次数必须为字典要求的值:" + sysDicCheckResult);
                            return oneRowCheck;
                        }
                    }
                    oneRowMap.put("testRounds", cellValue);
                    break;
                case "备注":
                    oneRowMap.put("remark", cellValue);
                    break;
                default:
                    oneRowCheck.put("message", "列“" + title + "”不存在");
                    return oneRowCheck;
            }
        }
        oneRowMap.put("id", IdUtil.getId());
        oneRowMap.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        oneRowMap.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        oneRowMap.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        oneRowMap.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        oneRowMap.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        itemList.add(oneRowMap);
        oneRowCheck.put("result", true);
        return oneRowCheck;
    }

    //..
    public ResponseEntity<byte[]> importTemplateDownload() {
        try {
            String fileName = "零部件试验项目导入模板.xlsx";
            // 创建文件实例
            File file = new File(
                    MaterielService.class.getClassLoader().getResource("templates/componentTest/" + fileName).toURI());
            String finalDownloadFileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");

            // 设置httpHeaders,使浏览器响应下载
            HttpHeaders headers = new HttpHeaders();
            // 告诉浏览器执行下载的操作，“attachment”告诉了浏览器进行下载,下载的文件 文件名为 finalDownloadFileName
            headers.setContentDispositionFormData("attachment", finalDownloadFileName);
            // 设置响应方式为二进制，以二进制流传输
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file), headers, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Exception in importTemplateDownload", e);
            return null;
        }
    }

    //..
    public List<JSONObject> getTestContractFileList(List<String> mainKanbanIdList) {
        List<JSONObject> businessFileList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("mainKanbanIds", mainKanbanIdList);
        businessFileList = componentTestKanbanDao.queryTestContractFileList(param);
        return businessFileList;
    }

    //..
    public List<JSONObject> getTestReportFileList(List<String> mainKanbanIdList) {
        List<JSONObject> businessFileList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("mainKanbanIds", mainKanbanIdList);
        businessFileList = componentTestKanbanDao.queryTestReportFileList(param);
        return businessFileList;
    }

    //..
    public List<JSONObject> getTestMPTFileList(List<String> mainKanbanIdList) {
        List<JSONObject> businessFileList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("mainKanbanIds", mainKanbanIdList);
        businessFileList = componentTestKanbanDao.queryTestMPTFileList(param);
        return businessFileList;
    }

    //..
    public void deleteOneContractFile(String fileId, String fileName, String mainId) {
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "componentTestUploadPosition", "testContract").getValue();
        rdmZhglFileManager.deleteOneFileFromDisk(fileId, fileName, mainId, filePathBase);
        Map<String, Object> param = new HashMap<>();
        param.put("id", fileId);
        componentTestKanbanDao.deleteContractFile(param);
    }

    //..
    public void deleteOneReportFile(String fileId, String fileName, String mainId) {
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "componentTestUploadPosition", "testReport").getValue();
        rdmZhglFileManager.deleteOneFileFromDisk(fileId, fileName, mainId, filePathBase);
        Map<String, Object> param = new HashMap<>();
        param.put("id", fileId);
        componentTestKanbanDao.deleteReportFile(param);
    }

    //..
    public void deleteOneMPTFile(String fileId, String fileName, String mainId) {
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "componentTestUploadPosition", "MPT").getValue();
        rdmZhglFileManager.deleteOneFileFromDisk(fileId, fileName, mainId, filePathBase);
        Map<String, Object> param = new HashMap<>();
        param.put("id", fileId);
        componentTestKanbanDao.deleteMPTFile(param);
    }

    //..
    public void saveContractUploadFiles(HttpServletRequest request) {
        Map<String, String[]> parameters = request.getParameterMap();
        if (parameters == null || parameters.isEmpty()) {
            logger.warn("没有找到上传的参数");
            return;
        }
        // 多附件上传需要用到的MultipartHttpServletRequest
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        if (fileMap == null || fileMap.isEmpty()) {
            logger.warn("没有找到上传的文件");
            return;
        }
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "componentTestUploadPosition", "testContract").getValue();
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find FilePathBase");
            return;
        }
        try {
            String mainKanbanId = toGetParamVal(parameters.get("mainKanbanId"));
            String id = IdUtil.getId();
            String fileName = toGetParamVal(parameters.get("fileName"));
            String fileSize = toGetParamVal(parameters.get("fileSize"));
            String fileDesc = toGetParamVal(parameters.get("fileDesc"));

            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();

            // 向下载目录中写入文件
            String filePath = filePathBase + File.separator + mainKanbanId;
            File pathFile = new File(filePath);
            if (!pathFile.exists()) {
                pathFile.mkdirs();
            }
            String suffix = CommonFuns.toGetFileSuffix(fileName);
            String fileFullPath = filePath + File.separator + id + "." + suffix;
            File file = new File(fileFullPath);
            FileCopyUtils.copy(mf.getBytes(), file);

            // 写入数据库
            JSONObject fileInfo = new JSONObject();
            fileInfo.put("id", id);
            fileInfo.put("fileName", fileName);
            fileInfo.put("fileSize", fileSize);
            fileInfo.put("mainKanbanId", mainKanbanId);
            fileInfo.put("fileDesc", fileDesc);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            componentTestKanbanDao.addContractFileInfos(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
    }

    //..保存合同编号2022-06-30
    public void contractFileNameSave(JsonResult result, String businessDataStr) {
        JSONArray businessObjs = JSONObject.parseArray(businessDataStr);
        if (businessObjs == null || businessObjs.isEmpty()) {
            result.setSuccess(false);
            result.setMessage("内容为空，操作失败！");
            return;
        }
        for (Object object : businessObjs) {
            JSONObject businessObj = (JSONObject) object;
            businessObj.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            businessObj.put("UPDATE_TIME_", new Date());
            componentTestKanbanDao.updateContractFileInfos(businessObj);
        }
    }

    //..
    public void saveReportUploadFiles(HttpServletRequest request) {
        Map<String, String[]> parameters = request.getParameterMap();
        if (parameters == null || parameters.isEmpty()) {
            logger.warn("没有找到上传的参数");
            return;
        }
        // 多附件上传需要用到的MultipartHttpServletRequest
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        if (fileMap == null || fileMap.isEmpty()) {
            logger.warn("没有找到上传的文件");
            return;
        }
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "componentTestUploadPosition", "testReport").getValue();
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find FilePathBase");
            return;
        }
        try {
            String mainKanbanId = toGetParamVal(parameters.get("mainKanbanId"));
            String id = IdUtil.getId();
            String fileName = toGetParamVal(parameters.get("fileName"));
            String fileSize = toGetParamVal(parameters.get("fileSize"));
            String fileDesc = toGetParamVal(parameters.get("fileDesc"));

            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();

            // 向下载目录中写入文件
            String filePath = filePathBase + File.separator + mainKanbanId;
            File pathFile = new File(filePath);
            if (!pathFile.exists()) {
                pathFile.mkdirs();
            }
            String suffix = CommonFuns.toGetFileSuffix(fileName);
            String fileFullPath = filePath + File.separator + id + "." + suffix;
            File file = new File(fileFullPath);
            FileCopyUtils.copy(mf.getBytes(), file);

            // 写入数据库
            JSONObject fileInfo = new JSONObject();
            fileInfo.put("id", id);
            fileInfo.put("fileName", fileName);
            fileInfo.put("fileSize", fileSize);
            fileInfo.put("mainKanbanId", mainKanbanId);
            fileInfo.put("fileDesc", fileDesc);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            componentTestKanbanDao.addReportFileInfos(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
    }

    //..保存报告编号2022-06-30
    public void reportFileNameSave(JsonResult result, String businessDataStr) {
        JSONArray businessObjs = JSONObject.parseArray(businessDataStr);
        if (businessObjs == null || businessObjs.isEmpty()) {
            result.setSuccess(false);
            result.setMessage("内容为空，操作失败！");
            return;
        }
        for (Object object : businessObjs) {
            JSONObject businessObj = (JSONObject) object;
            businessObj.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            businessObj.put("UPDATE_TIME_", new Date());
            componentTestKanbanDao.updateReportFileInfos(businessObj);
        }
    }

    //..
    public void saveMPTUploadFiles(HttpServletRequest request) {
        Map<String, String[]> parameters = request.getParameterMap();
        if (parameters == null || parameters.isEmpty()) {
            logger.warn("没有找到上传的参数");
            return;
        }
        // 多附件上传需要用到的MultipartHttpServletRequest
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        if (fileMap == null || fileMap.isEmpty()) {
            logger.warn("没有找到上传的文件");
            return;
        }
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "componentTestUploadPosition", "MPT").getValue();
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find FilePathBase");
            return;
        }
        try {
            String mainKanbanId = toGetParamVal(parameters.get("mainKanbanId"));
            String id = IdUtil.getId();
            String fileName = toGetParamVal(parameters.get("fileName"));
            String fileSize = toGetParamVal(parameters.get("fileSize"));
            String fileDesc = toGetParamVal(parameters.get("fileDesc"));

            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();

            // 向下载目录中写入文件
            String filePath = filePathBase + File.separator + mainKanbanId;
            File pathFile = new File(filePath);
            if (!pathFile.exists()) {
                pathFile.mkdirs();
            }
            String suffix = CommonFuns.toGetFileSuffix(fileName);
            String fileFullPath = filePath + File.separator + id + "." + suffix;
            File file = new File(fileFullPath);
            FileCopyUtils.copy(mf.getBytes(), file);

            // 写入数据库
            JSONObject fileInfo = new JSONObject();
            fileInfo.put("id", id);
            fileInfo.put("fileName", fileName);
            fileInfo.put("fileSize", fileSize);
            fileInfo.put("mainKanbanId", mainKanbanId);
            fileInfo.put("fileDesc", fileDesc);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            componentTestKanbanDao.addMPTFileInfos(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
    }

    //..以下标准相关
    public JsonResult bindingStandard(String mainId, String[] standardIds) {
        JsonResult result = new JsonResult(true, "操作成功");
        JSONObject params = new JSONObject();
        params.put("mainId", mainId);
        List<JSONObject> standards = componentTestKanbanDao.queryBindingStandardList(params);
        JSONObject standardObj = new JSONObject();
        for (String standardId : standardIds) {
            boolean link = true;
            for (JSONObject standard : standards) {
                if (standard.getString("standardId").equals(standardId)) {
                    link = false;
                    break;
                }
            }
            if (link) {
                standardObj.clear();
                standardObj.put("standardId", standardId);
                standardObj.put("mainId", mainId);
                this.createBindingStandard(standardObj);
            }
        }
        return result;
    }

    //..
    public void createBindingStandardMsg(JSONObject formData) {
        formData.put("id", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        formData.put("status", "未关联");
        if (StringUtils.isNotBlank(formData.getString("subListChanges"))) {
            JSONArray subListChangesDataJson = JSONObject.parseArray(formData.getString("subListChanges"));
            for (int i = 0; i < subListChangesDataJson.size(); i++) {
                JSONObject oneObject = subListChangesDataJson.getJSONObject(i);
                String state = oneObject.getString("_state");
                String id = oneObject.getString("id");
                if ("added".equals(state) || StringUtils.isBlank(id)) {
                    //新增
                    oneObject.put("id", IdUtil.getId());
                    oneObject.put("msgId", formData.getString("id"));
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", new Date());
                    componentTestKanbanDao.createBindingStandardMsgItem(oneObject);
                } else if ("modified".equals(state)) {
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", new Date());
                    componentTestKanbanDao.updateBindingStandardMsgItem(oneObject);
                } else if ("removed".equals(state)) {
                    Map<String, Object> param = new HashMap<>();
                    param.put("id", oneObject.getString("id"));
                    componentTestKanbanDao.deleteBindingStandardMsgItem(param);
                }
            }
        }
        componentTestKanbanDao.createBindingStandardMsg(formData);
    }

    //..
    public JsonPageResult<?> queryBindingStandardMsgList(HttpServletRequest request, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)) {
            params.put("sortField", sortField);
            params.put("sortOrder", sortOrder);
        }
        if (StringUtils.isEmpty(sortField)) {
            params.put("sortField", "CREATE_TIME_");
            params.put("sortOrder", "desc");
        }
        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    params.put(name, value);
                }
            }
        }
        if (doPage) {
            // 增加分页条件
            params.put("startIndex", 0);
            params.put("pageSize", 20);
            String pageIndex = request.getParameter("pageIndex");
            String pageSize = request.getParameter("pageSize");
            if (StringUtils.isNotBlank(pageIndex) && StringUtils.isNotBlank(pageSize)) {
                params.put("startIndex", Integer.parseInt(pageSize) * Integer.parseInt(pageIndex));
                params.put("pageSize", Integer.parseInt(pageSize));
            }
        }
        params.put("currentUserId", ContextUtil.getCurrentUserId());
        params.put("mainId", RequestUtil.getString(request, "mainId", ""));
        List<JSONObject> msgList = componentTestKanbanDao.queryBindingStandardMsgList(params);
        for (JSONObject msg : msgList) {
            if (msg.getDate("CREATE_TIME_") != null) {
                msg.put("CREATE_TIME_", DateUtil.formatDate(msg.getDate("CREATE_TIME_"), "yyyy-MM-dd"));
            }

        }
        result.setData(msgList);
        int countMsgDataList = componentTestKanbanDao.countBindingStandardMsgList(params);
        result.setTotal(countMsgDataList);
        return result;
    }

    //..
    public JsonResult deleteBindingStandardMsg(String id) {
        JsonResult result = new JsonResult(true, "操作成功");
        Map<String, Object> param = new HashMap<>();
        param.put("msgId", id);
        componentTestKanbanDao.deleteBindingStandardMsgItem(param);
        param.clear();
        param.put("id", id);
        componentTestKanbanDao.deleteBindingStandardMsg(param);
        return result;
    }

    //..
    public JSONObject getBindingStandardMsg(String id) {
        JSONObject jsonObject = componentTestKanbanDao.getBindingStandardMsg(id);
        if (jsonObject == null) {
            return new JSONObject();
        }
        return jsonObject;
    }

    //..
    public List<JSONObject> queryBindingStandardMsgItems(HttpServletRequest request) {
        String msgId = RequestUtil.getString(request, "msgId", "");
        Map<String, Object> param = new HashMap<>();
        param.put("msgId", msgId);
        List<JSONObject> msgItems = componentTestKanbanDao.queryBindingStandardMsgItems(param);
        for (Map<String, Object> map : msgItems) {
            if (map.get("CREATE_TIME_") != null) {
                map.put("CREATE_TIME_", DateUtil.formatDate((Date) map.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        return msgItems;
    }

    //..
    public void updateBindingStandardMsg(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        if (StringUtils.isNotBlank(formData.getString("subListChanges"))) {
            JSONArray subListChangesDataJson = JSONObject.parseArray(formData.getString("subListChanges"));
            for (int i = 0; i < subListChangesDataJson.size(); i++) {
                JSONObject oneObject = subListChangesDataJson.getJSONObject(i);
                String state = oneObject.getString("_state");
                String id = oneObject.getString("id");
                if ("added".equals(state) || StringUtils.isBlank(id)) {
                    //新增
                    oneObject.put("id", IdUtil.getId());
                    oneObject.put("msgId", formData.getString("id"));
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", new Date());
                    componentTestKanbanDao.createBindingStandardMsgItem(oneObject);
                } else if ("modified".equals(state)) {
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", new Date());
                    componentTestKanbanDao.updateBindingStandardMsgItem(oneObject);
                } else if ("removed".equals(state)) {
                    Map<String, Object> param = new HashMap<>();
                    param.put("id", oneObject.getString("id"));
                    componentTestKanbanDao.deleteBindingStandardMsgItem(param);
                }
            }
        }
        //关联状态判定
        JSONObject params = new JSONObject();
        params.put("msgId", formData.getString("id"));
        List<JSONObject> items = componentTestKanbanDao.queryBindingStandardMsgItems(params);
        int already = 0;
        Set<String> readyToBinding = new HashSet<>();//用set是为了去重，会有误选重复的标准的情况
        for (JSONObject item : items) {
            if (StringUtil.isNotEmpty(item.getString("standardId"))) {
                already++;
                readyToBinding.add(item.getString("standardId"));
            }
        }
        if (already == 0) {
            formData.put("status", "未关联");
        } else if (already == items.size()) {
            formData.put("status", "已关联");
        } else {
            formData.put("status", "部分关联");
        }
        //注意：在这里直接进行实际的绑定动作
        String[] arr = readyToBinding.toArray(new String[readyToBinding.size()]);
        this.bindingStandard(formData.getString("mainId"), arr);
        componentTestKanbanDao.updateBindingStandardMsg(formData);
    }

    //..
    public void createBindingStandard(JSONObject standardObj) {
        standardObj.put("id", IdUtil.getId());
        standardObj.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        standardObj.put("CREATE_TIME_", new Date());
        componentTestKanbanDao.createBindingStandard(standardObj);
    }

    //..以上标准相关
    public JsonResult deleteStandard(String mainId, String[] standardIds) {
        JsonResult result = new JsonResult(true, "操作成功");
        Map<String, Object> param = new HashMap<>();
        for (String standardId : standardIds) {
            // 删除基本信息
            param.clear();
            param.put("standardId", standardId);
            param.put("mainId", mainId);
            componentTestKanbanDao.deleteStandard(param);
        }
        return result;
    }

    //..
    private String toGetParamVal(String[] paramValArr) {
        if (paramValArr == null || paramValArr.length == 0) {
            return null;
        }
        return paramValArr[0];
    }

    //..检查字典值
    private String sysDicCheck(String cellValue, String treeKey) {
        List<SysDic> sysDicList = sysDicManager.getByTreeKey(treeKey);
        boolean isok = false;
        StringBuilder stringBuilder = new StringBuilder();
        for (SysDic sysDic : sysDicList) {
            if (cellValue.equalsIgnoreCase(sysDic.getName())) {
                isok = true;
            }
            stringBuilder.append(sysDic.getName()).append(",");
        }
        if (isok == false) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            return stringBuilder.toString();
        } else {
            return "ok";
        }
    }

    //..获取时间字符yyyyMM
    private String getDateString(String dateString) {
        String[] arrNow;
        if (dateString == null) {
            arrNow = DateUtil.formatDate(new Date(), DateUtil.DATE_FORMAT_MON).split("-");
        } else {
            arrNow = dateString.split("-");
        }

        StringBuffer sb = new StringBuffer();
        for (String a : arrNow) {
            sb.append(a);
        }
        return sb.toString();
    }

    //..定位流水号-1
    private String getSysSeq(Map<String, Object> componentObj) {
//        switch (componentObj.get("componentCategory").toString()) {
//            case "电气元件":
//                return getSysSeq2("dq", componentObj);
//            case "液压元件":
//                return getSysSeq2("yy", componentObj);
//            case "传动元件":
//                return getSysSeq2("dl", componentObj);
//            case "结构件":
//                return getSysSeq2("jg", componentObj);
//            case "材料类":
//                return getSysSeq2("cl", componentObj);
//            case "油品类":
//                return getSysSeq2("yp", componentObj);
//            case "其他":
//                return getSysSeq2("qt", componentObj);
//            default:
//                return "";
//        }
        switch (componentObj.get("componentCategory").toString()) {
            case "电气":
                return this.getSysSeq2("dq", componentObj);
            case "液压":
                return this.getSysSeq2("yy", componentObj);
            case "动力":
                return this.getSysSeq2("dl", componentObj);
            case "结构":
                return this.getSysSeq2("jg", componentObj);
            case "控制":
                return this.getSysSeq2("kz", componentObj);
            case "多学科":
                return this.getSysSeq2("dxk", componentObj);
            default:
                return "";
        }
    }

    //..定位流水号-2
    private String getSysSeq2(String seqKey, Map<String, Object> componentObj) {
//        if (componentObj.get("testCategory").toString().equalsIgnoreCase("年度计划试验")) {
//            return getSysSeq3(seqKey + "-1", componentObj);
//        } else if (componentObj.get("testCategory").toString().equalsIgnoreCase("临时新增试验")) {
//            return getSysSeq3(seqKey + "-2", componentObj);
//        } else if (componentObj.get("testCategory").toString().equalsIgnoreCase("改进后再次试验")) {
//            return getSysSeq3(seqKey + "-3", componentObj);
//        } else {
//            return "";
//        }
        if (componentObj.get("testCategory").toString().equalsIgnoreCase("年度计划试验")) {
            return this.getSysSeq3(seqKey + "-1", componentObj);
        } else if (componentObj.get("testCategory").toString().equalsIgnoreCase("临时新增试验")) {
            return this.getSysSeq3(seqKey + "-2", componentObj);
        } else {
            return "";
        }
    }

    //..生成会议编号:实验对象，编号key（如yy-1，yy-1，yy-1，dl-1，dl-2，dl-3等）
    private synchronized String getSysSeq3(String seqKey, Map<String, Object> componentObj) {
        JSONObject params = new JSONObject();
        params.put("key", seqKey);
        params.put("date", getDateString(componentObj.get("actualTestMonth").toString()));
        JSONObject seq = componentTestKanbanDao.getSeq(params);
        if (seq == null) {
            seq = new JSONObject();
            seq.put("id", IdUtil.getId());
            seq.put("KEY_", seqKey);
            seq.put("DATE_", getDateString(componentObj.get("actualTestMonth").toString()));
            seq.put("INIT_VAL_", "1");
            componentTestKanbanDao.insertSeq(seq);
        } else {
            int i = seq.getIntValue("INIT_VAL_");
            i++;
            seq.put("INIT_VAL_", i);
            componentTestKanbanDao.updateSeq(seq);
        }
        String[] keys = seqKey.split("-");
        String seqvalue = seq.getString("INIT_VAL_").length() < 2 ? "00" + seq.getString("INIT_VAL_") :
                seq.getString("INIT_VAL_").length() < 3 ? "0" + seq.getString("INIT_VAL_") :
                        seq.getString("INIT_VAL_");
        return keys[0] + "-" + seq.getString("DATE_") + seqvalue + "-" + keys[1];
    }

    //////////////////////////////..kanban
    //..
    private List<JSONObject> dataListQueryKanban(JSONObject params) {
        List<JSONObject> businessList = componentTestKanbanDao.dataListQuery(params);
        for (JSONObject oneObj : businessList) {
            //获取合同名称列表
            List<String> testContractNameList = componentTestKanbanDao.getTestContractNameListByBusinessId(oneObj.getString("id"));
            //如果有合同名称
            if (testContractNameList.size() > 0) {
                StringBuilder stringBuilder = new StringBuilder();
                for (String fileName : testContractNameList) {
                    stringBuilder.append("《").append(fileName).append("》");
                }
                oneObj.put("testContract", stringBuilder.toString());
            }
            //获取检测报告名称列表
            List<String> testReportNameList = componentTestKanbanDao.getTestReportNameListByBusinessId(oneObj.getString("id"));
            //如果有检测报告名称
            if (testReportNameList.size() > 0) {
                StringBuilder stringBuilder = new StringBuilder();
                for (String fileName : testReportNameList) {
                    stringBuilder.append("《").append(fileName).append("》");
                }
                oneObj.put("testReport", stringBuilder.toString());
            }
        }
        return businessList;
    }

    //..零部件试验完成情况
    public JSONObject getComponentTestCompletionStatusData(HttpServletRequest request, HttpServletResponse response,
                                                           String postDataStr) {
        int monthnow = DateUtil.getCurMonth();
        int yearnow = DateUtil.getCurYear();
        JSONObject postDataJson = JSONObject.parseObject(postDataStr);
        if (Integer.parseInt(postDataJson.getString("signYear")) < yearnow) {
            monthnow = 11;//所选年份小于当前年，序时月份默认最大
        } else if (Integer.parseInt(postDataJson.getString("signYear")) > yearnow) {
            monthnow = 0;//所选年份大于当前年，序时月份默认最小
        }
        //2022-07-06增加承担单位条件
        postDataJson.put("laboratory", postDataJson.getString("laboratory"));
        //以上增加承担单位条件
        postDataJson.put("plannedTestMonthBegin", postDataJson.getString("signYear") + "-01");
        postDataJson.put("plannedTestMonthEnd", postDataJson.getString("signYear") + "-12");
        List<JSONObject> businessListPlan = dataListQueryKanban(postDataJson);
        postDataJson.put("actualTestMonthBegin", postDataJson.getString("signYear") + "-01");
        postDataJson.put("actualTestMonthEnd", postDataJson.getString("signYear") + "-12");
        List<JSONObject> businessListActual = dataListQueryKanban(postDataJson);
        //计划数
        Integer planGaijin = 0, planLinshi = 0, planNiandu = 0, planXushi = 0, planQuannian = 0;
        List<Integer> planValueList = new ArrayList<>();
        //完成数
        Integer actualGaijin = 0, actualLinshi = 0, actualNiandu = 0, actualXushi = 0, actualQuannian = 0;
        List<Integer> actualValueList = new ArrayList<>();
        for (JSONObject business : businessListPlan) {
            //全年计划
            planQuannian++;
            if (StringUtil.isNotEmpty(business.getString("actualTestMonth"))) {
                actualQuannian++;
            }
            //序时计数
            if (DateUtil.getMonth(DateUtil.parseDate(business.getString("plannedTestMonth"), DateUtil.DATE_FORMAT_MON)) <= monthnow) {
                planXushi++;
                if (StringUtil.isNotEmpty(business.getString("actualTestMonth"))) {
                    actualXushi++;
                }
            }
            //年度计划，临时新增，改进后再次
            switch (business.getString("testCategory")) {
                case "年度计划试验":
                    planNiandu++;
                    if (StringUtil.isNotEmpty(business.getString("actualTestMonth"))) {
                        actualNiandu++;
                    }
                    break;
                case "临时新增试验":
                    planLinshi++;
                    if (StringUtil.isNotEmpty(business.getString("actualTestMonth"))) {
                        actualLinshi++;
                    }
                    break;
                case "改进后再次试验":
                    planGaijin++;
                    if (StringUtil.isNotEmpty(business.getString("actualTestMonth"))) {
                        actualGaijin++;
                    }
                    break;
                default:
            }
        }
        planValueList.add(planGaijin);
        planValueList.add(planLinshi);
        planValueList.add(planNiandu);
        planValueList.add(planXushi);
        planValueList.add(planQuannian);
        actualValueList.add(actualGaijin);
        actualValueList.add(actualLinshi);
        actualValueList.add(actualNiandu);
        actualValueList.add(actualXushi);
        actualValueList.add(actualQuannian);
        JSONObject resultJson = new JSONObject();
        //计划数，按照"改进后再次", "临时新增", "年度计划", '序时进度', '全年计划'排序
        resultJson.put("planValueArr", planValueList);
        //完成数，按照"改进后再次", "临时新增", "年度计划", '序时进度', '全年计划'排序
        resultJson.put("actualValueArr", actualValueList);
        return resultJson;
    }

    //..零部件试验结果
    public List<JSONObject> getComponentTestRestltData(HttpServletRequest request, HttpServletResponse response,
                                                       String postDataStr) {
        JSONObject postDataJson = JSONObject.parseObject(postDataStr);
        postDataJson.put("actualTestMonthBegin", postDataJson.getString("signYear") + "-01");
        postDataJson.put("actualTestMonthEnd", postDataJson.getString("signYear") + "-12");
        postDataJson.put("componentCategory", postDataJson.getString("componentCategory"));
        //2022-07-06增加承担单位条件
        postDataJson.put("laboratory", postDataJson.getString("laboratory"));
        //以上增加承担单位条件
        List<JSONObject> businessListActual = dataListQueryKanban(postDataJson);
        Integer hege = 0, buhege = 0, yanjiuxing = 0;
        for (JSONObject business : businessListActual) {
            if (business.getString("testResult").equalsIgnoreCase("合格")) {
                hege++;
            } else if (business.getString("testResult").equalsIgnoreCase("不合格")) {
                buhege++;
            } else if (business.getString("testResult").equalsIgnoreCase("研究性试验数据")) {
                yanjiuxing++;
            }
        }
        JSONObject jsonObjectHege = new JSONObject();
        jsonObjectHege.put("value", hege);
        jsonObjectHege.put("name", "合格");
        JSONObject jsonObjectBuhege = new JSONObject();
        jsonObjectBuhege.put("value", buhege);
        jsonObjectBuhege.put("name", "不合格");
        JSONObject jsonObjectYanjiuxing = new JSONObject();
        jsonObjectYanjiuxing.put("value", yanjiuxing);
        jsonObjectYanjiuxing.put("name", "研究性试验数据");
        List<JSONObject> resultList = new ArrayList<>();
        resultList.add(jsonObjectHege);
        resultList.add(jsonObjectBuhege);
        resultList.add(jsonObjectYanjiuxing);
        return resultList;
    }

    //..零部件试验计划及进度
    public JSONObject getComponentTestScheduleData(HttpServletRequest request, HttpServletResponse response,
                                                   String postDataStr) {
        JSONObject postDataJson = JSONObject.parseObject(postDataStr);
        postDataJson.put("plannedTestMonthBegin", postDataJson.getString("signYear") + "-01");
        postDataJson.put("plannedTestMonthEnd", postDataJson.getString("signYear") + "-12");
        //2022-07-06增加承担单位条件
        postDataJson.put("laboratory", postDataJson.getString("laboratory"));
        //以上增加承担单位条件
        List<JSONObject> businessListPlan = dataListQueryKanban(postDataJson);
        //dataset.dimensions
        List<String> dimensions = new ArrayList<>();
        if (postDataJson.getString("action").equalsIgnoreCase("leibie")) {
            dimensions.add("类别");
        } else if (postDataJson.getString("action").equalsIgnoreCase("suobu")) {
            dimensions.add("所部");
        }
        dimensions.add("试验计划");
        dimensions.add("已开展试验");
        //dataset.source
        List<JSONObject> sources = new ArrayList<>();
        JSONObject Statistics = new JSONObject(new LinkedHashMap());
        ////----------------------
        if (postDataJson.getString("action").equalsIgnoreCase("leibie")) {
            SysTree sysTree = sysTreeManager.getByKey("componentCategory");
            List<SysDic> sysDics = new ArrayList<SysDic>();
            sysDics = sysDicManager.getByTreeId(sysTree.getTreeId());
            //初始化各类试验
            for (SysDic sysDic : sysDics) {
                JSONObject source = new JSONObject();
                source.put("类别", sysDic.getValue());
                source.put("试验计划", 0);
                source.put("已开展试验", 0);
                Statistics.put(sysDic.getValue(), source);
            }
            //初始化合计
            JSONObject source = new JSONObject();
            source.put("类别", "合计");
            source.put("试验计划", 0);
            source.put("已开展试验", 0);
            Statistics.put("合计", source);
        } else if (postDataJson.getString("action").equalsIgnoreCase("suobu")) {
            SysTree sysTree = sysTreeManager.getByKey("applyDep");
            List<SysDic> sysDics = new ArrayList<SysDic>();
            sysDics = sysDicManager.getByTreeId(sysTree.getTreeId());
            //初始化各所部
            for (SysDic sysDic : sysDics) {
                JSONObject source = new JSONObject();
                source.put("所部", sysDic.getValue());
                source.put("试验计划", 0);
                source.put("已开展试验", 0);
                Statistics.put(sysDic.getValue(), source);
            }
        }
        ////----------------------
        for (JSONObject business : businessListPlan) {
            if (postDataJson.getString("action").equalsIgnoreCase("leibie")) {
                //处理各类试验
                String componentCategory = business.getString("componentCategory");
                JSONObject source = Statistics.getJSONObject(componentCategory);
                if (source != null) {
                    source.put("试验计划", source.getIntValue("试验计划") + 1);
                    if (StringUtil.isNotEmpty(business.getString("actualTestMonth"))) {
                        source.put("已开展试验", source.getIntValue("已开展试验") + 1);
                    }
                    Statistics.put(componentCategory, source);
                }
                //处理合计
                source = Statistics.getJSONObject("合计");
                if (source != null) {
                    source.put("试验计划", source.getIntValue("试验计划") + 1);
                    if (StringUtil.isNotEmpty(business.getString("actualTestMonth"))) {
                        source.put("已开展试验", source.getIntValue("已开展试验") + 1);
                    }
                    Statistics.put("合计", source);
                }
                ////----------------------
            } else if (postDataJson.getString("action").equalsIgnoreCase("suobu")) {
                //处理各所部
                String applyDep = business.getString("applyDep");
                JSONObject source = Statistics.getJSONObject(applyDep);
                if (source != null) {
                    source.put("试验计划", source.getIntValue("试验计划") + 1);
                    if (StringUtil.isNotEmpty(business.getString("actualTestMonth"))) {
                        source.put("已开展试验", source.getIntValue("已开展试验") + 1);
                    }
                    Statistics.put(applyDep, source);
                }
            }
        }
        for (Map.Entry entry : Statistics.entrySet()) {
            sources.add((JSONObject) entry.getValue());
        }
        JSONObject resultJson = new JSONObject();
        resultJson.put("dimensions", dimensions);
        resultJson.put("source", sources);
        return resultJson;
    }

    //..零部件试验报告出具情况
    public JSONObject getComponentTestReportData(HttpServletRequest request, HttpServletResponse response,
                                                 String postDataStr) {
        JSONObject postDataJson = JSONObject.parseObject(postDataStr);
        postDataJson.put("actualTestMonthBegin", postDataJson.getString("signYear") + "-01");
        postDataJson.put("actualTestMonthEnd", postDataJson.getString("signYear") + "-12");
        //2022-07-06增加承担单位条件
        postDataJson.put("laboratory", postDataJson.getString("laboratory"));
        //以上增加承担单位条件
        List<JSONObject> businessListActual = dataListQueryKanban(postDataJson);
        //dataset.dimensions
        List<String> dimensions = new ArrayList<>();
        if (postDataJson.getString("action").equalsIgnoreCase("leibie")) {
            dimensions.add("类别");
        } else if (postDataJson.getString("action").equalsIgnoreCase("suobu")) {
            dimensions.add("所部");
        }
        dimensions.add("已开展试验");
        dimensions.add("已出报告");
        //dataset.source
        List<JSONObject> sources = new ArrayList<>();
        JSONObject Statistics = new JSONObject(new LinkedHashMap());
        ////----------------------
        if (postDataJson.getString("action").equalsIgnoreCase("leibie")) {
            SysTree sysTree = sysTreeManager.getByKey("componentCategory");
            List<SysDic> sysDics = new ArrayList<SysDic>();
            sysDics = sysDicManager.getByTreeId(sysTree.getTreeId());
            //初始化各类试验
            for (SysDic sysDic : sysDics) {
                JSONObject source = new JSONObject();
                source.put("类别", sysDic.getValue());
                source.put("已开展试验", 0);
                source.put("已出报告", 0);
                Statistics.put(sysDic.getValue(), source);
            }
            //初始化合计
            JSONObject source = new JSONObject();
            source.put("类别", "合计");
            source.put("已开展试验", 0);
            source.put("已出报告", 0);
            Statistics.put("合计", source);
        } else if (postDataJson.getString("action").equalsIgnoreCase("suobu")) {
            SysTree sysTree = sysTreeManager.getByKey("applyDep");
            List<SysDic> sysDics = new ArrayList<SysDic>();
            sysDics = sysDicManager.getByTreeId(sysTree.getTreeId());
            //初始化各所部
            for (SysDic sysDic : sysDics) {
                JSONObject source = new JSONObject();
                source.put("所部", sysDic.getValue());
                source.put("已开展试验", 0);
                source.put("已出报告", 0);
                Statistics.put(sysDic.getValue(), source);
            }
        }
        ////----------------------
        for (JSONObject business : businessListActual) {
            if (postDataJson.getString("action").equalsIgnoreCase("leibie")) {
                //处理各类试验
                String componentCategory = business.getString("componentCategory");
                JSONObject source = Statistics.getJSONObject(componentCategory);
                if (source != null) {
                    source.put("已开展试验", source.getIntValue("已开展试验") + 1);
                    if (StringUtil.isNotEmpty(business.getString("testReport"))) {
                        source.put("已出报告", source.getIntValue("已出报告") + 1);
                    }
                    Statistics.put(componentCategory, source);
                }
                //处理合计
                source = Statistics.getJSONObject("合计");
                if (source != null) {
                    source.put("已开展试验", source.getIntValue("已开展试验") + 1);
                    if (StringUtil.isNotEmpty(business.getString("testReport"))) {
                        source.put("已出报告", source.getIntValue("已出报告") + 1);
                    }
                    Statistics.put("合计", source);
                }
            } else if (postDataJson.getString("action").equalsIgnoreCase("suobu")) {
                //处理各所部
                String applyDep = business.getString("applyDep");
                JSONObject source = Statistics.getJSONObject(applyDep);
                if (source != null) {
                    source.put("已开展试验", source.getIntValue("已开展试验") + 1);
                    if (StringUtil.isNotEmpty(business.getString("testReport"))) {
                        source.put("已出报告", source.getIntValue("已出报告") + 1);
                    }
                    Statistics.put(applyDep, source);
                }
            }
        }
        for (Map.Entry entry : Statistics.entrySet()) {
            sources.add((JSONObject) entry.getValue());
        }
        JSONObject resultJson = new JSONObject();
        resultJson.put("dimensions", dimensions);
        resultJson.put("source", sources);
        return resultJson;
    }

}
