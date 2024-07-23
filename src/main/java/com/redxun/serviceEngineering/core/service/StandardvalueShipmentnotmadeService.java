package com.redxun.serviceEngineering.core.service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.excel.ExcelReaderUtil;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.ExcelUtil;
import com.redxun.materielextend.core.service.MaterielService;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.serviceEngineering.core.dao.StandardvalueShipmentnotmadeDao;

@Service
public class StandardvalueShipmentnotmadeService {
    private static Logger logger = LoggerFactory.getLogger(StandardvalueShipmentnotmadeService.class);
    @Autowired
    private StandardvalueShipmentnotmadeDao standardvalueShipmentnotmadeDao;

    // ..
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        getListParams(params, request);
        List<JSONObject> businessList = standardvalueShipmentnotmadeDao.dataListQuery(params);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (Map<String, Object> standardvalueShipmentnotmade : businessList) {
            if (standardvalueShipmentnotmade.get("CREATE_TIME_") != null) {
                standardvalueShipmentnotmade.put("CREATE_TIME_",
                    sdf.format(standardvalueShipmentnotmade.get("CREATE_TIME_")));
            }
            if (standardvalueShipmentnotmade.get("responseTime") != null) {
                standardvalueShipmentnotmade.put("responseTime",
                    sdf.format(standardvalueShipmentnotmade.get("responseTime")));
            }
        }
        int businessListCount = standardvalueShipmentnotmadeDao.countDataListQuery(params);
        result.setData(businessList);
        result.setTotal(businessListCount);
        return result;
    }

    // ..
    private void getListParams(Map<String, Object> params, HttpServletRequest request) {
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
                    // if ("communicateStartTime".equalsIgnoreCase(name)) {
                    // value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), -8));
                    // }
                    // if ("communicateEndTime".equalsIgnoreCase(name)) {
                    // value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), 16));
                    // }
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

    // ..
    public JsonResult deleteData(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> businessIds = Arrays.asList(ids);
        Map<String, Object> param = new HashMap<>();
        param.put("businessIds", businessIds);
        standardvalueShipmentnotmadeDao.deleteData(param);
        return result;
    }

    // ..
    public void saveBusiness(JsonResult result, String businessDataStr) {
        JSONObject businessObjs = JSONObject.parseObject(businessDataStr);
        if (businessObjs == null || businessObjs.isEmpty()) {
            result.setSuccess(false);
            result.setMessage("内容为空，操作失败！");
            return;
        }
        if (StringUtils.isBlank(businessObjs.getString("id"))) {
            businessObjs.put("id", IdUtil.getId());
            businessObjs.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            businessObjs.put("CREATE_TIME_", new Date());
            standardvalueShipmentnotmadeDao.insertData(businessObjs);
        } else {
            businessObjs.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            businessObjs.put("UPDATE_TIME_", new Date());
            standardvalueShipmentnotmadeDao.updateData(businessObjs);
        }
    }

    public JSONObject getStandardvalueShipmentnotmadeDetail(String zzcsbId) {
        return standardvalueShipmentnotmadeDao.queryById(zzcsbId);
    }

    public ResponseEntity<byte[]> downloadTemplate() {
        try {
            String fileName = "待产品制作标准值表模板.xlsx";
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

    public void importStandardvalueShipmentnotmade(JSONObject result, HttpServletRequest request) {
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
            if (rowNum < 1) {
                logger.error("找不到标题行");
                result.put("message", "数据导入失败，找不到标题行！");
                return;
            }

            // 解析标题部分
            Row titleRow = sheet.getRow(0);
            if (titleRow == null) {
                logger.error("找不到标题行");
                result.put("message", "数据导入失败，找不到标题行！");
                return;
            }
            List<String> titleList = new ArrayList<>();
            for (int i = 0; i < titleRow.getLastCellNum(); i++) {
                titleList.add(StringUtils.trim(titleRow.getCell(i).getStringCellValue()));
            }

            if (rowNum < 2) {
                logger.info("数据行为空");
                result.put("message", "数据导入完成，数据行为空！");
                return;
            }
            // 解析验证数据部分，任何一行的任何一项不满足条件，则直接返回失败
            List<Map<String, Object>> itemList = new ArrayList<>();
            for (int i = 1; i < rowNum; i++) {
                Row row = sheet.getRow(i);
                JSONObject rowParse = generateDataFromRow(row, itemList, titleList);
                if (!rowParse.getBoolean("result")) {
                    result.put("message", "数据导入失败，第" + (i + 1) + "行数据错误：" + rowParse.getString("message"));
                    return;
                }
            }
            for (int i = 0; i < itemList.size(); i++) {
                standardvalueShipmentnotmadeDao.insertData(itemList.get(i));
            }
            result.put("message", "数据导入成功！");
        } catch (Exception e) {
            logger.error("Exception in import", e);
            result.put("message", "数据导入失败，系统异常！");
        }
    }

    private JSONObject generateDataFromRow(Row row, List<Map<String, Object>> itemList, List<String> titleList) {
        JSONObject oneRowCheck = new JSONObject();
        oneRowCheck.put("result", false);
        Map<String, Object> oneRowMap = new HashMap<>(16);
        DataFormatter dataFormatter = new DataFormatter();
        for (int i = 0; i < titleList.size(); i++) {
            String title = titleList.get(i);
            title = title.replaceAll(" ", "");
            Cell cell = row.getCell(i);
            String cellValue = dataFormatter.formatCellValue(cell);
            switch (title) {
                case "产品所":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "产品所为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("department", cellValue);
                    JSONObject paramJson = new JSONObject();
                    paramJson.put("deptName", cellValue);
                    paramJson.put("DimId", "1");
                    JSONObject groupJson = standardvalueShipmentnotmadeDao.getGroupById(paramJson);
                    if (groupJson == null) {
                        oneRowCheck.put("message", "系统中不存在此部门");
                        return oneRowCheck;
                    } else {
                        oneRowMap.put("departmentId", groupJson.getString("groupId"));
                    }
                    break;
                case "销售型号":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "销售型号为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("salesModel", cellValue);
                    break;
                case "物料编码":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "物料编码为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("materialCode", cellValue);
                    break;
                case "设计型号":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "设计型号为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("materialName", cellValue);
                    break;
                case "版本类型":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "版本类型为空");
                        return oneRowCheck;
                    }
                    String versionType = "";
                    if (cellValue.equals("常规版")) {
                        versionType = "cgb";
                    } else if (cellValue.equals("测试版")) {
                        versionType = "csb";
                    } else {
                        versionType = "wzb";
                    }
                    oneRowMap.put("versionType", versionType);
                    break;
                case "适用第四位PIN码":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "适用第四位PIN码为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("pinFour", cellValue);
                    break;
                case "产品主管编制负责人":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "产品主管编制负责人为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("principal", cellValue);
                    JSONObject object = standardvalueShipmentnotmadeDao.queryUserByFullName(cellValue);
                    if (object == null || object.isEmpty()) {
                        oneRowCheck.put("message", "系统不存在此人");
                        return oneRowCheck;
                    }
                    oneRowMap.put("principalId", object.getString("USER_ID_"));
                    break;
                default:
                    oneRowCheck.put("message", "列“" + title + "”不存在");
                    return oneRowCheck;
            }
        }
        oneRowMap.put("id", IdUtil.getId());
        oneRowMap.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        oneRowMap.put("CREATE_TIME_", new Date());
        oneRowMap.put("responseStatus", "");
        oneRowMap.put("responseTime", "");
        oneRowMap.put("betaCompletion", "dzz");
        itemList.add(oneRowMap);
        oneRowCheck.put("result", true);
        return oneRowCheck;
    }

    public boolean queryByMaterialCode(JSONObject formDataJson) {
        String id = StringUtils.isBlank(formDataJson.getString("id")) ? "-1" : formDataJson.getString("id");
        JSONObject standard = standardvalueShipmentnotmadeDao.queryByMaterialCode(formDataJson);
        if (standard != null && !formDataJson.isEmpty() && !id.equals(standard.getString("id"))) {
            return false;
        }
        return true;
    }

    /**
     * 查询用户作为负责人的待制作数据
     * 
     * @param userId
     * @return
     */
    public List<JSONObject> queryNeedTodoByUserId(String userId) {
        Map<String, Object> oneRowMap = new HashMap<>(16);
        oneRowMap.put("principalId", userId);
        List<JSONObject> needTodos = standardvalueShipmentnotmadeDao.queryNeedTodoByUserId(oneRowMap);
        return needTodos;
    }
}
