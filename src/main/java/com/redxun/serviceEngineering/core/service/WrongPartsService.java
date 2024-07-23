package com.redxun.serviceEngineering.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.excel.ExcelReaderUtil;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.ExcelUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.materielextend.core.service.MaterielService;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.serviceEngineering.core.dao.WrongPartsDao;
import com.redxun.sys.core.entity.SysDic;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class WrongPartsService {
    private static Logger logger = LoggerFactory.getLogger(WrongPartsService.class);
    @Autowired
    private WrongPartsDao wrongPartsDao;
    @Autowired
    private SysDicManager sysDicManager;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private CommonBpmManager commonBpmManager;

    //..
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

        // 增加角色过滤的条件
        this.addCjzgRoleParam(params, ContextUtil.getCurrentUserId(), ContextUtil.getCurrentUser().getUserNo());

        List<Map<String, Object>> ccbgList = wrongPartsDao.dataListQuery(params);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (Map<String, Object> ccbg : ccbgList) {
            if (ccbg.get("CREATE_TIME_") != null) {
                ccbg.put("CREATE_TIME_", sdf.format(ccbg.get("CREATE_TIME_")));
            }
        }
        // 查询当前处理人
        xcmgProjectManager.setTaskCurrentUser(ccbgList);
        result.setData(ccbgList);
        int countCcbgDataList = wrongPartsDao.countDataListQuery(params);
        result.setTotal(countCcbgDataList);
        return result;
    }

    //..
    private void addCjzgRoleParam(Map<String, Object> params, String userId, String userNo) {
        params.put("currentUserId", userId);
        if (userNo.equalsIgnoreCase("admin")) {
            return;
        }
        boolean isFgld = rdmZhglUtil.judgeUserIsFgld(userId);
        if (isFgld) {
            params.put("roleName", "fgld");
            return;
        }
        params.put("roleName", "ptyg");
    }

    //..
    public JsonResult deleteData(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> businessIds = Arrays.asList(ids);
        Map<String, Object> param = new HashMap<>();
        param.put("businessIds", businessIds);
        param.put("wrongPartsIds", businessIds);
        List<JSONObject> files = wrongPartsDao.queryCjzgFileList(param);
        String cjzgFilePathBase = WebAppUtil.getProperty("cjzgFilePathBase");
        if (files.size() > 0) {
            for (JSONObject oneFile : files) {
                rdmZhglFileManager.deleteOneFileFromDisk(oneFile.getString("id"), oneFile.getString("fileName"),
                        oneFile.getString("wrongPartsId"), cjzgFilePathBase);
            }
            for (String cjzgId : ids) {

                rdmZhglFileManager.deleteDirFromDisk(cjzgId, cjzgFilePathBase);
            }
            wrongPartsDao.delFileByWrongPartsId(param);
        }
        wrongPartsDao.deleteData(param);
        return result;
    }

    //..作废
    public ResponseEntity<byte[]> importTemplateDownload() {
        try {
            String fileName = "错件导入模板.xls";
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

    //..作废
    public void importExcel(JSONObject result, HttpServletRequest request) {
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
                Row row = sheet.getRow(i);
                JSONObject rowParse = generateDataFromRow(row, itemList, titleList, id);
                if (!rowParse.getBoolean("result")) {
                    result.put("message", "数据导入失败，第" + (i + 1) + "行数据错误：" + rowParse.getString("message"));
                    return;
                }
            }
            for (int i = 0; i < itemList.size(); i++) {
                wrongPartsDao.insertData(itemList.get(i));
            }
            result.put("message", "数据导入成功！");
        } catch (Exception e) {
            logger.error("Exception in importExcel", e);
            result.put("message", "数据导入失败，系统异常！");
        }
    }

    //..作废
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
                case "销售型号":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "销售型号为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("salesModel", cellValue);
                    break;
                case "设计型号":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "设计型号为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("designModel", cellValue);
                    break;
                case "错件名称":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "错件名称为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("wrongPartName", cellValue);
                    break;
                case "问题描述":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "问题描述为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("problemDescription", cellValue);
                    break;
                case "原因分析":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "原因分析为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("causeAnalysis", cellValue);
                    break;
                case "整改方案":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "整改方案为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("rectificationPlan", cellValue);
                    break;
                case "X-GSS整改方案":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "X-GSS整改方案为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("XGSSRectificationPlan", cellValue);
                    break;
                case "错件类型":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "错件类型为空");
                        return oneRowCheck;
                    }
                    if (StringUtils.isNotBlank(cellValue)) {
                        List<SysDic> sysDicList = sysDicManager.getByTreeKey("serviceEngineeringWrongPartsErrorType");
                        boolean isok = false;
                        for (SysDic sysDic : sysDicList) {
                            if (cellValue.equalsIgnoreCase(sysDic.getName())) {
                                isok = true;
                            }
                        }
                        if (isok == false) {
                            oneRowCheck.put("message", "错件类型必须为字典要求的值");
                            return oneRowCheck;
                        }
                    }
                    oneRowMap.put("typeOfWrongPart", cellValue);
                    break;
                case "责任部门":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "责任部门为空");
                        return oneRowCheck;
                    }
                    if (wrongPartsDao.getGroupCountByName(cellValue) != 1) {
                        oneRowCheck.put("message", "责任部门名称不存在,或者存在多个");
                        return oneRowCheck;
                    }
                    String departmentId = wrongPartsDao.getGroupIdByName(cellValue);
                    oneRowMap.put("responsibleDepartmentId", departmentId);
                    break;
                case "状态":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "状态为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("status", cellValue);
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
    public JSONObject getCjzgDetail(String cjzgId) {
        JSONObject detailObj = wrongPartsDao.queryCjzgById(cjzgId);
        if (detailObj == null) {
            return new JSONObject();
        }
        return detailObj;
    }

    //..
    public void createCjzg(JSONObject formData) {
        formData.put("id", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        formData.put("businessStatus", "A");
        wrongPartsDao.insertCjzg(formData);
    }

    //..
    public void updateCjzg(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        if (formData.containsKey("promiseTime") && StringUtil.isNotEmpty(formData.getString("promiseTime"))) {
            String promiseTime = formData.getString("promiseTime").substring(0, 10) + " 23:59:59";
            formData.put("promiseTime", promiseTime);//承诺时间默认为选择日期的凌晨最后时刻
        }
        wrongPartsDao.updateData(formData);
    }

    //..
    public void saveUploadFiles(HttpServletRequest request) {
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
        String filePathBase = WebAppUtil.getProperty("cjzgFilePathBase");
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find cjzgFilePathBase");
            return;
        }
        try {
            String cjzgId = toGetParamVal(parameters.get("cjzgId"));
            String id = IdUtil.getId();
            String fileName = toGetParamVal(parameters.get("fileName"));
            String fileSize = toGetParamVal(parameters.get("fileSize"));

            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();

            // 向下载目录中写入文件
            String filePath = filePathBase + File.separator + cjzgId;
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
            fileInfo.put("wrongPartsId", cjzgId);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            wrongPartsDao.addFileInfos(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
    }

    //..
    private String toGetParamVal(String[] paramValArr) {
        if (paramValArr == null || paramValArr.length == 0) {
            return null;
        }
        return paramValArr[0];
    }

    //..
    public List<JSONObject> queryCjzgFileList(Map<String, Object> params) {
        return wrongPartsDao.queryCjzgFileList(params);
    }

    //..
    public void delCjzgUploadFile(String id, String fileName, String cjzgId) {
        String cjzgFilePathBase = WebAppUtil.getProperty("cjzgFilePathBase");
        rdmZhglFileManager.deleteOneFileFromDisk(id, fileName, cjzgId, cjzgFilePathBase);
        wrongPartsDao.deleteFileById(id);
    }

    //..
    public boolean isFwgcjswdzy() {
        Map<String, Object> params = new HashMap<>();
        params.put("groupName", "服务工程技术文档专员");
        List<Map<String, String>> userInfos = xcmgProjectOtherDao.queryUserByGroupName(params);
        String currentUserId = ContextUtil.getCurrentUserId();
        if (userInfos.size() > 0) {
            for (Map<String, String> userInfo : userInfos) {
                if (currentUserId.equals(userInfo.get("USER_ID_"))) {
                    return true;
                }
            }
        }
        return false;
    }

    //..
    public void exportWrongParts(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> params = new HashMap<>();
        //传入条件(不包括分页)
        params = CommonFuns.getSearchParam(params, request, false);
        List<Map<String, Object>> ccbgList = wrongPartsDao.dataListQuery(params);
        //补充流程处理人信息
        xcmgProjectManager.setTaskCurrentUser(ccbgList);
        List<JSONObject> nodeSetListWithName =
                commonBpmManager.getNodeSetListWithName("CJZG", "userTask", "endEvent");
        int i = 0;
        for (Map<String, Object> one : ccbgList) {
            //序号
            one.put("index", i + 1);
            //错件类型
            if (one.get("typeOfWrongPart").toString().equals("sjzzcw")) {
                one.put("typeOfWrongPart", "实例数据制作错误");
            } else if (one.get("typeOfWrongPart").toString().equals("htbhh")) {
                one.put("typeOfWrongPart", "换图不换号");
            } else if (one.get("typeOfWrongPart").toString().equals("ebomcw")) {
                one.put("typeOfWrongPart", "EBOM错误");
            } else if (one.get("typeOfWrongPart").toString().equals("pbom错误")) {
                one.put("typeOfWrongPart", "PBOM错误");
            } else if (one.get("typeOfWrongPart").toString().equals("scsjcw")) {
                one.put("typeOfWrongPart", "生产数据错误");
            } else if (one.get("typeOfWrongPart").toString().equals("wgjsjcw")) {
                one.put("typeOfWrongPart", "外购件制作错误");
            } else if (one.get("typeOfWrongPart").toString().equals("wgjsjcw2")) {
                one.put("typeOfWrongPart", "外购件图纸错误");
            } else if (one.get("typeOfWrongPart").toString().equals("bjfycw")) {
                one.put("typeOfWrongPart", "备件发运错误");
            }
            //提交时间
            if (!one.containsKey("CREATE_TIME_")) {
                one.put("CREATE_TIME_", "");
            } else {
                one.put("CREATE_TIME_", DateUtil.formatDate((Date) one.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
            //是否延期
            if (!one.containsKey("promiseTime") || !one.containsKey("actualTime")
                    || StringUtil.isEmpty(one.get("promiseTime").toString())
                    || StringUtil.isEmpty(one.get("actualTime").toString())) {
                one.put("isPostpone", "否");
            } else {
                Date promiseTime = DateUtil.parseDate(one.get("promiseTime").toString(), DateUtil.DATE_FORMAT_FULL);
                Date actualTime = DateUtil.parseDate(one.get("actualTime").toString(), DateUtil.DATE_FORMAT_FULL);
                if (DateUtil.judgeEndBigThanStart(promiseTime, actualTime)) {
                    one.put("isPostpone", "是");
                } else {
                    one.put("isPostpone", "否");
                }
            }
            //任务状态
            if (!one.containsKey("taskStatus")) {
                one.put("taskStatus", "");
            } else if (one.get("taskStatus").toString().equals("DRAFTED")) {
                one.put("taskStatus", "草稿");
            } else if (one.get("taskStatus").toString().equals("RUNNING")) {
                one.put("taskStatus", "运行中");
            } else if (one.get("taskStatus").toString().equals("SUCCESS_END")) {
                one.put("taskStatus", "成功结束");
            } else if (one.get("taskStatus").toString().equals("DISCARD_END")) {
                one.put("taskStatus", "作废");
            } else if (one.get("taskStatus").toString().equals("ABORT_END")) {
                one.put("taskStatus", "异常中止结束");
            } else if (one.get("taskStatus").toString().equals("PENDING")) {
                one.put("taskStatus", "挂起");
            }
            //业务状态
            if (!one.containsKey("businessStatus")) {
                one.put("businessStatus", "");
            } else {
                String statusCode = one.get("businessStatus").toString();
                for (JSONObject jsonObject : nodeSetListWithName) {
                    if (jsonObject.getString("key").equalsIgnoreCase(statusCode)) {
                        one.put("businessStatus", jsonObject.getString("value"));
                    }
                }
            }
            i++;
        }


        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = nowDate + "市场反馈错件整改列表";
        String[] fieldNames = {"序号", "错件名称", "错件类型", "提交时间", "到达当前节点时间", "预计完成时间", "实际完成时间", "销售型号", "设计型号",
                "整机编码", "所属代理商", "是否历史遗留问题", "责任部门", "责任人", "X-GSS整改人",
                "问题描述", "原因分析", "涉及车辆(车号范围)", "历史车辆整改方案", "业务规则整改措施",
                "后市场风险提示", "X-GSS整改方案", "业务状态", "是否延期", "当前处理人", "任务状态"};
        String[] fieldCodes = {"index", "wrongPartName", "typeOfWrongPart", "CREATE_TIME_", "currentNodeBeginTime", "promiseTime", "actualTime", "salesModel", "designModel",
                "machineCode", "agent", "isHistory", "respDepName", "principalUserName", "XGSSRespUserName",
                "problemDescription", "causeAnalysis", "involvedCar", "rectificationPlan", "newRectificationMeasures",
                "riskWarning", "XGSSRectificationPlan", "businessStatus", "isPostpone", "currentProcessUser", "taskStatus"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(ccbgList, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(title, wbObj, response);
    }

}
