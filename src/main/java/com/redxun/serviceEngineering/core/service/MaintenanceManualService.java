package com.redxun.serviceEngineering.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.excel.ExcelReaderUtil;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.ExcelUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.materielextend.core.service.MaterielService;
import com.redxun.org.api.model.IGroup;
import com.redxun.org.api.model.IUser;
import com.redxun.org.api.service.GroupService;
import com.redxun.org.api.service.UserService;
import com.redxun.restApi.orguser.entity.Group;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.serviceEngineering.core.dao.MaintenanceManualDao;
import com.redxun.serviceEngineering.core.dao.PartsAtlasDao;
import com.redxun.sys.core.entity.SysDic;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.sys.org.entity.OsGroup;
import com.redxun.sys.org.entity.OsUser;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.*;

@Service
public class MaintenanceManualService {
    private static Logger logger = LoggerFactory.getLogger(MaintenanceManualService.class);
    @Autowired
    private MaintenanceManualDao maintenanceManualDao;
    @Autowired
    private PartsAtlasDao partsAtlasDao;
    @Autowired
    private SysDicManager sysDicManager;
    @Autowired
    private UserService userService;
    @Autowired
    private GroupService groupService;

    //..
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        getListParams(params, request);
        List<JSONObject> businessList = maintenanceManualDao.dataListQuery(params);
        int businessListCount = maintenanceManualDao.countDataListQuery(params);
        result.setData(businessList);
        result.setTotal(businessListCount);
        return result;
    }

    //
    private void getListParams(Map<String, Object> params, HttpServletRequest request) {
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)) {
            params.put("sortField", sortField);
            params.put("sortOrder", sortOrder);
        }
        if (StringUtils.isEmpty(sortField)) {
            params.put("sortField", "manualCode");
            params.put("sortOrder", "desc");
        }
        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    // 数据库中存储的时间是UTC时间，因此需要将前台传递的北京时间转化
//                    if ("communicateStartTime".equalsIgnoreCase(name)) {
//                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), -8));
//                    }
//                    if ("communicateEndTime".equalsIgnoreCase(name)) {
//                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), 16));
//                    }
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
    public JsonResult deleteData(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> businessIds = Arrays.asList(ids);
        Map<String, Object> param = new HashMap<>();
        param.put("businessIds", businessIds);
        maintenanceManualDao.deleteData(param);
        return result;
    }

    //..
    public ResponseEntity<byte[]> importTemplateDownload() {
        try {
            String fileName = "结构化操作与保养手册总看板数据导入模板.xls";
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

    //..
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
            //todo：韩修远，注意此处允许物料重复后，覆盖率看板数据似乎不用改算法，先提交看看情况
            for (int i = 0; i < itemList.size(); i++) {
//                if (maintenanceManualDao.getListByCode(itemList.get(i).get("materialCode").toString()).size() > 0
//                        && !itemList.get(i).get("materialCode").toString().equalsIgnoreCase("")) {
//                    itemList.get(i).put("storageQuantity", partsAtlasDao.
//                            getStorageCountByMaterialCode(itemList.get(i).get("materialCode").toString()));
//                    itemList.get(i).put("shipmentQuantity", partsAtlasDao.
//                            getShipmentCountByMaterialCode(itemList.get(i).get("materialCode").toString()));
//                    maintenanceManualDao.updateDataByCode(itemList.get(i));
                if (itemList.get(i).get("materialCode").toString().equalsIgnoreCase("")) {

                } else {
                    //入库/发运数量按物料编码提取零件图册表中的PIN码数量
                    itemList.get(i).put("storageQuantity", partsAtlasDao.
                            getStorageCountByMaterialCode(itemList.get(i).get("materialCode").toString()));
                    itemList.get(i).put("shipmentQuantity", partsAtlasDao.
                            getShipmentCountByMaterialCode(itemList.get(i).get("materialCode").toString()));
                    maintenanceManualDao.insertData(itemList.get(i));
                }
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
                case "物料编码":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "物料编码为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("materialCode", cellValue);
                    break;
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
                case "机型图册状态":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "机型图册状态为空");
                        return oneRowCheck;
                    }
                    if (StringUtils.isNotBlank(cellValue)) {
                        List<SysDic> sysDicList = sysDicManager.getByTreeKey("serviceEngineeringMaintenanceManualAtlasStatus");
                        boolean isok = false;
                        for (SysDic sysDic : sysDicList) {
                            if (cellValue.equalsIgnoreCase(sysDic.getName())) {
                                isok = true;
                            }
                        }
                        if (isok == false) {
                            oneRowCheck.put("message", "机型图册状态必须为字典要求的值");
                            return oneRowCheck;
                        }
                    }
                    oneRowMap.put("atlasStatus", cellValue);
                    break;
                case "系统手册编码":
                    oneRowMap.put("manualCode", cellValue);
                    break;
                case "手册名称":
                    oneRowMap.put("manualDescription", cellValue);
                    break;
                case "手册语言":
                    oneRowMap.put("manualLanguage", cellValue);
                    break;
                case "手册状态":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "手册状态状态为空");
                        return oneRowCheck;
                    }
                    if (StringUtils.isNotBlank(cellValue)) {
                        List<SysDic> sysDicList = sysDicManager.getByTreeKey("serviceEngineeringMaintenanceManualStatus");
                        boolean isok = false;
                        for (SysDic sysDic : sysDicList) {
                            if (cellValue.equalsIgnoreCase(sysDic.getName())) {
                                isok = true;
                            }
                        }
                        if (isok == false) {
                            oneRowCheck.put("message", "手册状态必须为字典要求的值");
                            return oneRowCheck;
                        }
                    }
                    oneRowMap.put("manualStatus", cellValue);
                    break;
                case "部门":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "部门为空");
                        return oneRowCheck;
                    }
                    JSONObject department = maintenanceManualDao.getGroupByName(cellValue);
                    if (department == null) {
                        oneRowCheck.put("message", "部门名称不存在");
                        return oneRowCheck;
                    } else {
                        oneRowMap.put("departmentId", department.getString("GROUP_ID_"));
                        oneRowMap.put("department", cellValue);
                    }
                    break;
                case "产品主管":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "产品主管为空");
                        return oneRowCheck;
                    }
                    if (StringUtils.isNotBlank(cellValue)) {
                        IUser productSupervisor = userService.getByUsername(cellValue);
                        if (productSupervisor == null) {
                            oneRowCheck.put("message", "该产品主管账号不存在");
                            return oneRowCheck;
                        } else {
                            oneRowMap.put("productSupervisorId", productSupervisor.getUserId());
                            oneRowMap.put("productSupervisor", productSupervisor.getFullname());
                        }
                    }
                    break;
                case "关键用户":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "关键用户为空");
                        return oneRowCheck;
                    }
                    IUser keyUser = userService.getByUsername(cellValue);
                    if (keyUser == null) {
                        oneRowCheck.put("message", "该关键用户账号不存在");
                        return oneRowCheck;
                    } else {
                        oneRowMap.put("keyUserId", keyUser.getUserId());
                        oneRowMap.put("keyUser", keyUser.getFullname());
                    }
                    break;
                case "编制/整改完成百分比":
//                    if (StringUtils.isNotBlank(cellValue) && StringUtil.getAppearNumber(cellValue, "%") != 0) {
//                        oneRowCheck.put("message", "编制/整改完成百分比要去掉%号");
//                        return oneRowCheck;
//                    }
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "编制/整改完成百分比为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("percentComplete", cellValue);
                    break;
                case "是否切换打印":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "是否切换打印为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("isPrint", cellValue);
                    break;
                case "是否CE版手册":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "是否CE版手册为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("isCE", cellValue);
                    break;
                case "预计切换打印时间":
                    if (StringUtils.isNotBlank(cellValue) && StringUtil.getAppearNumber(cellValue, "-") != 2) {
                        oneRowCheck.put("message", "日期格式必须为YYYY-mm-dd");
                        return oneRowCheck;
                    }
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowMap.put("estimatedPrintTime", "9999-12-31");
                    } else {
                        oneRowMap.put("estimatedPrintTime", cellValue);
                    }
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
    public void saveBusiness(JsonResult result, String businessDataStr) {
        JSONArray businessObjs = JSONObject.parseArray(businessDataStr);
        if (businessObjs == null || businessObjs.isEmpty()) {
            result.setSuccess(false);
            result.setMessage("内容为空，操作失败！");
            return;
        }
        for (Object object : businessObjs) {
            JSONObject businessObj = (JSONObject) object;
            //入库/发运数量按物料编码提取零件图册表中的PIN码数量
            businessObj.put("storageQuantity", partsAtlasDao.
                    getStorageCountByMaterialCode(businessObj.get("materialCode").toString()));
            businessObj.put("shipmentQuantity", partsAtlasDao.
                    getShipmentCountByMaterialCode(businessObj.get("materialCode").toString()));
            businessObj.put("estimatedPrintTime",
                    businessObj.get("estimatedPrintTime").toString().substring(0, 10));
            //--此处处理复制添加，只有id没有冗余名称情况。后来发现傻了，前端顺便把冗余也复制了不就行了，因此不需要了
//            if(StringUtil.isEmpty(businessObj.getString("department")) ){
//                OsGroup department = (OsGroup)groupService.getById(businessObj.getString("departmentId"));
//                businessObj.put("department",department.getName());
//            }
//            if(StringUtil.isEmpty(businessObj.getString("productSupervisor")) ){
//                OsUser productSupervisor = (OsUser)userService.getByUserId(businessObj.getString("productSupervisorId"));
//                businessObj.put("productSupervisor",productSupervisor.getFullname());
//            }
//            if(StringUtil.isEmpty(businessObj.getString("keyUser")) ){
//                OsUser keyUser = (OsUser)userService.getByUserId(businessObj.getString("keyUserId"));
//                businessObj.put("keyUser",keyUser.getFullname());
//            }
            //--
            if (StringUtils.isBlank(businessObj.getString("id"))) {
                businessObj.put("id", IdUtil.getId());
                businessObj.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                businessObj.put("CREATE_TIME_", new Date());
                maintenanceManualDao.insertData(businessObj);
            } else {
                businessObj.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                businessObj.put("UPDATE_TIME_", new Date());
                maintenanceManualDao.updateData(businessObj);
            }
        }
    }

    //..操保手册制作总数
    public Integer getMaintenanceManualTotal() {
        return maintenanceManualDao.getMaintenanceManualTotal();
    }
}
