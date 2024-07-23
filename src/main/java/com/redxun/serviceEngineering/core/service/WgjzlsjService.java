package com.redxun.serviceEngineering.core.service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.componentTest.core.dao.ComponentTestKanbanDao;
import com.redxun.core.excel.ExcelReaderUtil;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.ExcelUtil;
import com.redxun.materielextend.core.service.MaterielService;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.serviceEngineering.core.dao.WgjzlsjDao;
import com.redxun.sys.org.entity.OsUser;
import com.redxun.sys.org.manager.OsUserManager;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

@Service
public class WgjzlsjService {
    private static Logger logger = LoggerFactory.getLogger(WgjzlsjService.class);
    @Autowired
    private WgjzlsjDao wgjzlsjDao;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;
    @Autowired
    private CommonInfoManager commonInfoManager;
    @Autowired
    private ComponentTestKanbanDao componentTestKanbanDao;
    @Autowired
    private OsUserManager osUserManager;

    // ..
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)) {
            params.put("sortField", sortField);
            params.put("sortOrder", sortOrder);
        }
        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    // 数据库中存储的时间是UTC时间，因此需要将前台传递的北京时间转化
                    if ("filingStartTime".equalsIgnoreCase(name)) {
                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), -8));
                    }
                    if ("filingEndTime".equalsIgnoreCase(name)) {
                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), 16));
                    }
                    if ("makeSartTime".equalsIgnoreCase(name)) {
                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), -8));
                    }
                    if ("makeEndTime".equalsIgnoreCase(name)) {
                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), 16));
                    }
                    params.put(name, value);
                }
            }
        }
        // 增加分页条件
        if (doPage) {
            params.put("startIndex", 0);
            params.put("pageSize", 20);
            String pageIndex = request.getParameter("pageIndex");
            String pageSize = request.getParameter("pageSize");
            if (StringUtils.isNotBlank(pageIndex) && StringUtils.isNotBlank(pageSize)) {
                params.put("startIndex", Integer.parseInt(pageSize) * Integer.parseInt(pageIndex));
                params.put("pageSize", Integer.parseInt(pageSize));
            }
        }
        // 增加角色过滤的条件（需要自己办理的目前已包含在下面的条件中）
        addWgjzlsjRoleParam(params, ContextUtil.getCurrentUserId(), ContextUtil.getCurrentUser().getUserNo());
        List<Map<String, Object>> wgjzlsjList = wgjzlsjDao.dataListQuery(params);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        for (Map<String, Object> wgjzlsj : wgjzlsjList) {
            if (wgjzlsj.get("CREATE_TIME_") != null) {
                wgjzlsj.put("CREATE_TIME_", sdf.format(wgjzlsj.get("CREATE_TIME_")));
            }
            if (wgjzlsj.get("submitDate") != null) {
                wgjzlsj.put("submitDate", format.format(wgjzlsj.get("submitDate")));
            }
            if (wgjzlsj.get("filingTimeExp") != null) {
                wgjzlsj.put("filingTimeExp", format.format(wgjzlsj.get("filingTimeExp")));
            }
            if (wgjzlsj.get("filingTime") != null) {
                wgjzlsj.put("filingTime", format.format(wgjzlsj.get("filingTime")));
            }
            if (wgjzlsj.get("yjwcsj") != null) {
                wgjzlsj.put("yjwcsj", format.format(wgjzlsj.get("yjwcsj")));
            }
            if (wgjzlsj.get("makeTimePlan") != null) {
                wgjzlsj.put("makeTimePlan", format.format(wgjzlsj.get("makeTimePlan")));
            }
            if (wgjzlsj.get("makeTime") != null) {
                wgjzlsj.put("makeTime", format.format(wgjzlsj.get("makeTime")));
            }

        }
        // 查询当前处理人
        xcmgProjectManager.setTaskCurrentUser(wgjzlsjList);
        result.setData(wgjzlsjList);
        int count = wgjzlsjDao.countDataListQuery(params);
        result.setTotal(count);
        return result;
    }

    // ..
    private void addWgjzlsjRoleParam(Map<String, Object> params, String userId, String userNo) {
        params.put("currentUserId", userId);
        if (userNo.equalsIgnoreCase("admin")) {
            return;
        }
        if (commonInfoManager.judgeUserIsPointRole("外购件流程负责人", ContextUtil.getCurrentUserId())) {
            return;
        }
        boolean isFgld = rdmZhglUtil.judgeUserIsFgld(userId);
        if (isFgld) {
            params.put("roleName", "fgld");
            return;
        }
        params.put("roleName", "ptyg");
    }

    // ..
    public JsonResult deleteData(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> businessIds = Arrays.asList(ids);
        Map<String, Object> param = new HashMap<>();
        param.put("ids", businessIds);
        wgjzlsjDao.deleteData(param);
        return result;
    }

    // ..
    public JSONObject getWgjzlsjDetail(String wgjzlsjId) {
        JSONObject detailObj = wgjzlsjDao.queryById(wgjzlsjId);
        if (detailObj == null) {
            return new JSONObject();
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (detailObj.getDate("submitDate") != null) {
            detailObj.put("submitDate", simpleDateFormat.format(detailObj.getDate("submitDate")));
        }
        if (detailObj.getDate("filingTimeExp") != null) {
            detailObj.put("filingTimeExp", simpleDateFormat.format(detailObj.getDate("filingTimeExp")));
        }
        if (detailObj.getDate("filingTime") != null) {
            detailObj.put("filingTime", simpleDateFormat.format(detailObj.getDate("filingTime")));
        }
        if (detailObj.getDate("yjwcsj") != null) {
            detailObj.put("yjwcsj", simpleDateFormat.format(detailObj.getDate("yjwcsj")));
        }
        if (detailObj.getDate("makeTimePlan") != null) {
            detailObj.put("makeTimePlan", simpleDateFormat.format(detailObj.getDate("makeTimePlan")));
        }

        if (detailObj.getDate("makeTime") != null) {
            detailObj.put("makeTime", simpleDateFormat.format(detailObj.getDate("makeTime")));
        }
        return detailObj;
    }

    // ..
    public void createWgjzlsj(JSONObject formData) {
        formData.put("id", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        if (formData.getString("businessNo").equalsIgnoreCase("")
            && !formData.getString("dataType").equalsIgnoreCase("")) {
            formData.put("businessNo", this.genSequenceNo(formData, formData.getString("dataType")));
        }
        wgjzlsjDao.insertData(formData);
    }

    // ..
    public void updateWgjzlsj(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        wgjzlsjDao.updateData(formData);
    }

    // ..
    public JSONObject queryByMaterialCodeAndSupplier(JSONObject formDataJson) {
        return wgjzlsjDao.queryByMaterialCodeAndSupplier(formDataJson);
    }

    // 李文光大改未涉及
    public JsonPageResult<?> queryDepartmentReport(HttpServletRequest request, HttpServletResponse response,
        boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)) {
            params.put("sortField", sortField);
            params.put("sortOrder", sortOrder);
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
        if (doPage) {
            params.put("startIndex", 0);
            params.put("pageSize", 20);
            String pageIndex = request.getParameter("pageIndex");
            String pageSize = request.getParameter("pageSize");
            if (StringUtils.isNotBlank(pageIndex) && StringUtils.isNotBlank(pageSize)) {
                params.put("startIndex", Integer.parseInt(pageSize) * Integer.parseInt(pageIndex));
                params.put("pageSize", Integer.parseInt(pageSize));
            }
        }

        List<Map<String, Object>> wgjzlsjList = wgjzlsjDao.queryDepartmentReport(params);
        result.setData(wgjzlsjList);
        int count = wgjzlsjDao.queryDepartmentReportCount(params);
        result.setTotal(count);
        return result;
    }

    // 李文光大改未涉及
    public JsonPageResult<?> querySupplierReport(HttpServletRequest request, HttpServletResponse response,
        boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)) {
            params.put("sortField", sortField);
            params.put("sortOrder", sortOrder);
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
        if (doPage) {
            params.put("startIndex", 0);
            params.put("pageSize", 20);
            String pageIndex = request.getParameter("pageIndex");
            String pageSize = request.getParameter("pageSize");
            if (StringUtils.isNotBlank(pageIndex) && StringUtils.isNotBlank(pageSize)) {
                params.put("startIndex", Integer.parseInt(pageSize) * Integer.parseInt(pageIndex));
                params.put("pageSize", Integer.parseInt(pageSize));
            }
        }

        List<Map<String, Object>> wgjzlsjList = wgjzlsjDao.querySupplierReport(params);
        result.setData(wgjzlsjList);
        int count = wgjzlsjDao.querySupplierReportCount(params);
        result.setTotal(count);
        return result;
    }

    // 李文光大改未涉及
    public JSONObject queryWgjzlsjChart() {
        return wgjzlsjDao.queryWgjzlsjChart();
    }

    // ..
    public void exportWgj(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> params = new HashMap<>();
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
        // 查询列表
        List<Map<String, Object>> wgjzlsjList = wgjzlsjDao.dataListQuery(params);
        // 写入审批意见
        Set<String> instIdSet = new HashSet<>();
        Map<String, Map<String, Object>> instId2Obj = new HashMap<>();
        for (Map<String, Object> oneData : wgjzlsjList) {
            String instId = oneData.get("instId")==null?"":oneData.get("instId").toString();
            if (StringUtils.isNotBlank(instId)) {
                instIdSet.add(instId);
                instId2Obj.put(instId, oneData);
            }
        }
        if (!instIdSet.isEmpty()) {
            JSONObject queryJson = new JSONObject();
            queryJson.put("instIdSet", instIdSet);
            queryJson.put("nodeName", "所内收集");
            List<JSONObject> nodeRemark = wgjzlsjDao.queryNodeRemark(queryJson);
            for (JSONObject oneData : nodeRemark) {
                String instId = oneData.getString("INST_ID_");
                Map<String, Object> wgjzlsjObj = instId2Obj.get(instId);
                wgjzlsjObj.put("nodeRemark", oneData.getString("REMARK_"));
            }
        }
        // 格式处理
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        for (Map<String, Object> wgj : wgjzlsjList) {
            if (wgj.get("submitDate") != null) {
                wgj.put("submitDate", simpleDateFormat.format(wgj.get("submitDate")));
            }
            if (wgj.get("filingTimeExp") != null) {
                wgj.put("filingTimeExp", simpleDateFormat.format(wgj.get("filingTimeExp")));
            }
            if (wgj.get("filingTime") != null) {
                wgj.put("filingTime", simpleDateFormat.format(wgj.get("filingTime")));
            }
            if (wgj.get("yjwcsj") != null) {
                wgj.put("yjwcsj", simpleDateFormat.format(wgj.get("yjwcsj")));
            }
            if (wgj.get("makeTimePlan") != null) {
                wgj.put("makeTimePlan", simpleDateFormat.format(wgj.get("makeTimePlan")));
            }
            if (wgj.get("makeTime") != null) {
                wgj.put("makeTime", simpleDateFormat.format(wgj.get("makeTime")));
            }

            if (!wgj.containsKey("taskStatus")) {
                wgj.put("taskStatus", "");
            } else if (wgj.get("taskStatus").toString().equals("DRAFTED")) {
                wgj.put("taskStatus", "草稿");
            } else if (wgj.get("taskStatus").toString().equals("DISCARD_END")) {
                wgj.put("taskStatus", "作废");
            } else if (wgj.get("taskStatus").toString().equals("ABORT_END")) {
                wgj.put("taskStatus", "异常中止结束");
            } else if (wgj.get("taskStatus").toString().equals("PENDING")) {
                wgj.put("taskStatus", "挂起");
            } else if (wgj.get("taskStatus").toString().equals("RUNNING")) {
                wgj.put("taskStatus", "运行中");
            } else if (wgj.get("taskStatus").toString().equals("SUCCESS_END")) {
                wgj.put("taskStatus", "成功结束");
            }
        }
        // 查询当前处理人
        xcmgProjectManager.setTaskCurrentUser(wgjzlsjList);
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "外购件资料收集";
        String excelName = nowDate + title;
        String[] fieldNames = {"资料类型", "物料编码", "物料名称", "物料描述", "机型", "物料所属部门（产品所）", "通知单号", "供应商", "供应商联系人", "供应商联系方式",
            "反馈日期", "产品所责任人", "是否已归档", "预计归档完成时间", "归档完成时间", "预计制作完成时间", "计划制作完成时间", "是否已制作", "制作完成时间",
            "外购件图册版本号/手册TOPIC号", "创建人", "服务工程责任人", "当前处理人", "所内收集签审意见(最新)", "流程状态"};
        String[] fieldCodes = {"dataType", "materialCode", "materialName", "materialDescription", "designModel",
            "materialDepartment", "orderNo", "supplier", "supplierContact", "supplieContactWay", "submitDate",
            "cpsPrincipal", "filing", "filingTimeExp", "filingTime", "yjwcsj", "makeTimePlan", "thirdMake", "makeTime",
            "VTNo", "creator", "fwgcPrincipal", "currentProcessUser", "nodeRemark", "taskStatus"};

        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(wgjzlsjList, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }

    // ..
    private synchronized String genSequenceNo(JSONObject formData, String keyValue) {
        String key = "";
        if (keyValue.equals("维修手册类")) {
            key = "02";
        } else if (keyValue.equals("零件图册类")) {
            key = "01";
        } else if (keyValue.equals("使用说明")) {
            key = "03";
        }
        JSONObject params = new JSONObject();
        params.put("key", key);
        params.put("date", DateUtil.getCurYear());
        JSONObject seq = wgjzlsjDao.getSeq(params);
        if (seq == null) {
            seq = new JSONObject();
            seq.put("id", IdUtil.getId());
            seq.put("KEY_", key);
            seq.put("DATE_", DateUtil.getCurYear());
            seq.put("INIT_VAL_", "1");
            wgjzlsjDao.insertSeq(seq);
        } else {
            int i = seq.getIntValue("INIT_VAL_");
            i++;
            seq.put("INIT_VAL_", i);
            wgjzlsjDao.updateSeq(seq);
        }
        return "WGSJ" + seq.getString("DATE_") + seq.getString("KEY_")
            + String.format("%04d", seq.getIntValue("INIT_VAL_"));
    }

    // 李文光大改未涉及
    public JSONObject getApiList(JSONObject result, String searchValue) {

        // 以searchValue为查询条件 返回查询到的列表
        String resUrlBase =
            "http://wjrdm.xcmg.com/rdm/serviceEngineering/core/wgjzlsj/wgjzlsjEditPage.do?action=detail&wgjzlsjId=";
        List<JSONObject> resList = wgjzlsjDao.queryApiList(searchValue);
        for (JSONObject oneRes : resList) {
            oneRes.put("url", resUrlBase + oneRes.getString("id"));
        }
        if (resList.size() > 0) {
            result.put("result", resList);
        } else {
            result.put("success", false);
            result.put("message", "无查询结果，请检查搜索条件！");
        }
        return result;
    }

    // ..
    public ResponseEntity<byte[]> importTemplateDownload() {
        try {
            String fileName = "外购件资料收集导入模板.xlsx";
            // 创建文件实例
            File file = new File(
                MaterielService.class.getClassLoader().getResource("templates/serviceEngineering/" + fileName).toURI());
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

    // ..
    public void importExcel(JSONObject result, HttpServletRequest request) {
        try {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
            MultipartFile fileObj = multipartRequest.getFile("importFile");
            if (fileObj == null) {
                result.put("message", "数据导入失败，内容为空！");
                return;
            }
            String fileName = ((CommonsMultipartFile)fileObj).getFileItem().getName();
            ((CommonsMultipartFile)fileObj).getFileItem().getName().endsWith(ExcelReaderUtil.EXCEL03_EXTENSION);
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
                Row row = sheet.getRow(i);
                JSONObject rowParse = generateDataFromRow(row, itemList, titleList, id);
                if (!rowParse.getBoolean("result")) {
                    result.put("message", "数据导入失败，第" + (i + 1) + "行数据错误：" + rowParse.getString("message"));
                    return;
                }
            }
            for (int i = 0; i < itemList.size(); i++) {
                Map<String, Object> map = itemList.get(i);
                String json = JSON.toJSONString(map);
                JSONObject jsonObject = JSON.parseObject(json);
                wgjzlsjDao.insertData(jsonObject);
            }
            result.put("message", "数据导入成功！");
        } catch (Exception e) {
            logger.error("Exception in importExcel", e);
            result.put("message", "数据导入失败，系统异常！");
        }
    }

    // ..
    private JSONObject generateDataFromRow(Row row, List<Map<String, Object>> itemList, List<String> titleList,
        String mainId) {
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
                case "资料类型":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "资料类型为空");
                        return oneRowCheck;
                    }
                    if (!cellValue.equalsIgnoreCase("维修手册类") && !cellValue.equalsIgnoreCase("零件图册类")
                        && !cellValue.equalsIgnoreCase("使用说明")) {
                        oneRowCheck.put("message", "资料类型必须为指定的值");
                        return oneRowCheck;
                    }
                    oneRowMap.put("dataType", cellValue);
                    break;
                case "物料编码":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "物料编码为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("materialCode", cellValue);
                    break;
                case "物料名称":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "物料名称为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("materialName", cellValue);
                    break;
                case "物料描述":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "物料描述为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("materialDescription", cellValue);
                    break;
                case "供应商":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "供应商为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("supplier", cellValue);
                    break;
                case "供应商联系人":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "供应商联系人为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("supplierContact", cellValue);
                    break;
                case "供应商联系方式":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "供应商联系方式为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("supplieContactWay", cellValue);
                    break;
                case "反馈日期":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "反馈日期为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("submitDate", cellValue);
                    break;
                case "服务工程责任人":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "服务工程责任人为空");
                        return oneRowCheck;
                    }
                    if (cellValue.matches("[\\u4E00-\\u9FA5]+")) {
                        List<JSONObject> jsonObjects = componentTestKanbanDao.getUserByFullName(cellValue);
                        if (jsonObjects.size() == 0) {
                            oneRowCheck.put("message", "用户不存在，请在导入表格中输入具体账号");
                            return oneRowCheck;
                        } else if (jsonObjects.size() > 1) {
                            oneRowCheck.put("message", "系统中存在同名用户，请在导入表格中输入具体账号");
                            return oneRowCheck;
                        }
                        oneRowMap.put("fwgcPrincipalId", jsonObjects.get(0).getString("USER_ID_"));
                        oneRowMap.put("fwgcPrincipal", cellValue);
                    } else {
                        OsUser osUser = osUserManager.getByUserName(cellValue);
                        if (osUser == null) {
                            oneRowCheck.put("message", "系统中不存在此账号");
                            return oneRowCheck;
                        }
                        oneRowMap.put("fwgcPrincipalId", osUser.getUserId());
                        oneRowMap.put("fwgcPrincipal", osUser.getFullname());
                    }
                    break;
                case "制作完成时间":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "制作完成时间为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("makeTime", cellValue);
                    break;
                case "归档完成时间":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "归档完成时间为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("filingTime", cellValue);
                    break;
                case "外购件资料网盘路径":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "外购件资料网盘路径为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("networkDiskPath", cellValue);
                    break;
                default:
                    oneRowCheck.put("message", "列“" + title + "”不存在");
                    return oneRowCheck;
            }
        }
        oneRowMap.put("id", IdUtil.getId());
        oneRowMap.put("filing", "yes");
        oneRowMap.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        oneRowMap.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        oneRowMap.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        oneRowMap.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        oneRowMap.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        itemList.add(oneRowMap);
        oneRowCheck.put("result", true);
        return oneRowCheck;
    }
}
