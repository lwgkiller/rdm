package com.redxun.newProductAssembly.core.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.excel.ExcelReaderUtil;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.ExcelUtil;
import com.redxun.materielextend.core.service.MaterielService;
import com.redxun.newProductAssembly.core.dao.NewproductAssemblyMaterialDao;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.commons.collections.map.HashedMap;
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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class NewproductAssemblyMaterialService {
    private static Logger logger = LoggerFactory.getLogger(NewproductAssemblyMaterialService.class);
    private static int EXCEL_ROW_OFFSET = 1;
    @Autowired
    private NewproductAssemblyMaterialDao newproductAssemblyMaterialDao;

    //..
    public JsonPageResult<?> modelListQuery(HttpServletRequest request, HttpServletResponse response) {
        this.refreshAllProgress();//更新进度
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        CommonFuns.getSearchParam(params, request, true);
        List<JSONObject> businessList = newproductAssemblyMaterialDao.modelListQuery(params);
        int businessListCount = newproductAssemblyMaterialDao.countModelListQuery(params);
        result.setData(businessList);
        result.setTotal(businessListCount);
        return result;
    }

    //..更新进度
    private void refreshAllProgress() {
        JSONObject params = new JSONObject();
        List<JSONObject> modelList = newproductAssemblyMaterialDao.modelListQuery(null);
        for (JSONObject model : modelList) {
            params.put("mainId", model.getString("id"));
            List<JSONObject> materialList = newproductAssemblyMaterialDao.materialListQuery(params);
            Double total = 0d;
            Double already = 0d;
            for (JSONObject material : materialList) {
                total += material.getInteger("deliveryQuantity");
                already += material.getInteger("receivedQuantity");
            }
            BigDecimal bigDecimal = new BigDecimal(total == 0d ? 0d : already / total).setScale(4, BigDecimal.ROUND_HALF_UP);
            model.put("progress", bigDecimal.toString());
            newproductAssemblyMaterialDao.updateModel(model);
        }
    }

    //..
    public JsonPageResult<?> materialListQuery(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        this.getSearchParam(params, request, true);
        String mainId = RequestUtil.getString(request, "mainId");
        params.put("mainId", mainId);
        List<JSONObject> businessList = newproductAssemblyMaterialDao.materialListQuery(params);
        int businessListCount = newproductAssemblyMaterialDao.countMaterialListQuery(params);
        result.setData(businessList);
        result.setTotal(businessListCount);
        return result;
    }

    //特殊定制
    private Map<String, Object> getSearchParam(Map<String, Object> params, HttpServletRequest request, boolean doPage) {
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        String pageIndex = request.getParameter("pageIndex");
        String pageSize = request.getParameter("pageSize");
        if (doPage) {
            params.put("currentIndex", Integer.parseInt(pageIndex) * Integer.parseInt(pageSize));
            params.put("pageSize", pageSize);
        }
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
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
                    if (name.equalsIgnoreCase("others")) {
                        String[] values = value.split(",");
                        for (String field : values) {
                            params.put(field, "true");
                        }
                    } else {
                        params.put(name, value);
                    }
                }
            }
        }
        return params;
    }

    //..
    public void saveModel(JsonResult result, String businessDataStr) {
        JSONArray businessObjs = JSONObject.parseArray(businessDataStr);
        if (businessObjs == null || businessObjs.isEmpty()) {
            result.setSuccess(false);
            result.setMessage("内容为空，操作失败！");
            return;
        }
        for (Object object : businessObjs) {
            JSONObject businessObj = (JSONObject) object;
            if (StringUtils.isBlank(businessObj.getString("id"))) {
                businessObj.put("id", IdUtil.getId());
                businessObj.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                businessObj.put("CREATE_TIME_", new Date());
                businessObj.put("lastTime", DateUtil.formatDate(businessObj.getDate("CREATE_TIME_"), DateUtil.DATE_FORMAT_YMD));
                newproductAssemblyMaterialDao.insertModel(businessObj);
            } else {
                businessObj.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                businessObj.put("UPDATE_TIME_", new Date());
                businessObj.put("lastTime", DateUtil.formatDate(businessObj.getDate("UPDATE_TIME_"), DateUtil.DATE_FORMAT_YMD));
                newproductAssemblyMaterialDao.updateModel(businessObj);
            }
        }
    }

    //..
    public void saveMaterial(JsonResult result, String businessDataStr, String mainId) {
        JSONArray businessObjs = JSONObject.parseArray(businessDataStr);
        if (businessObjs == null || businessObjs.isEmpty()) {
            result.setSuccess(false);
            result.setMessage("内容为空，操作失败！");
            return;
        }
        for (Object object : businessObjs) {
            JSONObject businessObj = (JSONObject) object;
            if (StringUtils.isBlank(businessObj.getString("id"))) {
                //判重
                JSONObject params = new JSONObject();
                params.put("mainId", mainId);
                params.put("wbs", businessObj.getString("wbs"));
                params.put("materialCode", businessObj.getString("materialCode"));
                params.put("shortDescription", businessObj.getString("shortDescription"));
                List<JSONObject> materialList = newproductAssemblyMaterialDao.materialListQuery(params);
                if (materialList.isEmpty()) {
                    businessObj.put("id", IdUtil.getId());
                    businessObj.put("mainId", mainId);
                    businessObj.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    businessObj.put("CREATE_TIME_", new Date());
                    businessObj.put("lastTime", DateUtil.formatDate(businessObj.getDate("CREATE_TIME_"), DateUtil.DATE_FORMAT_YMD));
                    businessObj.put("exceptionQuantity",
                            businessObj.getInteger("deliveryQuantity") - businessObj.getInteger("receivedQuantity"));
                    newproductAssemblyMaterialDao.insertMaterial(businessObj);
                } else {
                    result.setSuccess(false);
                    result.setMessage("已经存在相同记录，操作失败！");
                    return;
                }
            } else {
                businessObj.put("mainId", mainId);
                businessObj.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                businessObj.put("UPDATE_TIME_", new Date());
                businessObj.put("lastTime", DateUtil.formatDate(businessObj.getDate("UPDATE_TIME_"), DateUtil.DATE_FORMAT_YMD));
                businessObj.put("exceptionQuantity",
                        businessObj.getInteger("deliveryQuantity") - businessObj.getInteger("receivedQuantity"));
                newproductAssemblyMaterialDao.updateMaterial(businessObj);
            }
        }
        //更新主信息
        JSONObject modelDetail = newproductAssemblyMaterialDao.getModelDetail(mainId);
        modelDetail.put("lastTime", DateUtil.formatDate(new Date(), DateUtil.DATE_FORMAT_YMD));
        newproductAssemblyMaterialDao.updateModel(modelDetail);
    }

    //..
    public JsonResult deleteModel(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> businessIds = Arrays.asList(ids);
        Map<String, Object> param = new HashMap<>();
        param.put("businessIds", businessIds);
        newproductAssemblyMaterialDao.deleteModel(param);
        param.clear();
        param.put("mainIds", businessIds);
        newproductAssemblyMaterialDao.deleteMaterial(param);
        newproductAssemblyMaterialDao.deleteSubMaterial(param);
        return result;
    }

    //..
    public JsonResult deleteMaterial(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> businessIds = Arrays.asList(ids);
        Map<String, Object> param = new HashMap<>();
        for (String businessId : businessIds) {
            JSONObject material = newproductAssemblyMaterialDao.getMaterialDetail(businessId);
            param.put("wbs", material.getString("wbs"));
            param.put("materialCode", material.getString("materialCode"));
            param.put("shortDescription", material.getString("shortDescription"));
            newproductAssemblyMaterialDao.deleteSubMaterialBy3(param);
        }
        param.clear();
        param.put("businessIds", businessIds);
        newproductAssemblyMaterialDao.deleteMaterial(param);
        return result;
    }

    //..
    public JSONObject getModelDetail(String businessId) {
        JSONObject jsonObject = newproductAssemblyMaterialDao.getModelDetail(businessId);
        if (jsonObject == null) {
            return new JSONObject();
        }
        return jsonObject;
    }

    //..
    public ResponseEntity<byte[]> importMaterialTDownload() {
        try {
            String fileName = "新品试制物料管理主表导入模板.xlsx";
            // 创建文件实例
            File file = new File(
                    MaterielService.class.getClassLoader().getResource("templates/newproductAssembly/" + fileName).toURI());
            String finalDownloadFileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");

            // 设置httpHeaders,使浏览器响应下载
            HttpHeaders headers = new HttpHeaders();
            // 告诉浏览器执行下载的操作，“attachment”告诉了浏览器进行下载,下载的文件 文件名为 finalDownloadFileName
            headers.setContentDispositionFormData("attachment", finalDownloadFileName);
            // 设置响应方式为二进制，以二进制流传输
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file), headers, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Exception in importMaterialTDownload", e);
            return null;
        }
    }

    //..
    public ResponseEntity<byte[]> importSubMaterialTDownload() {
        try {
            String fileName = "新品试制物料管理分表导入模板.xlsx";
            // 创建文件实例
            File file = new File(
                    MaterielService.class.getClassLoader().getResource("templates/newproductAssembly/" + fileName).toURI());
            String finalDownloadFileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");

            // 设置httpHeaders,使浏览器响应下载
            HttpHeaders headers = new HttpHeaders();
            // 告诉浏览器执行下载的操作，“attachment”告诉了浏览器进行下载,下载的文件 文件名为 finalDownloadFileName
            headers.setContentDispositionFormData("attachment", finalDownloadFileName);
            // 设置响应方式为二进制，以二进制流传输
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file), headers, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Exception in importSubMaterialTDownload", e);
            return null;
        }
    }

    //..todo:mark处理主表
    public void importMaterial(JSONObject result, HttpServletRequest request) {
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
            Sheet sheet = wb.getSheet("主表");
            if (sheet == null) {
                logger.error("找不到导入模板");
                result.put("message", "数据导入失败，找不到导入模板导入页！");
                return;
            }
            int rowNum = sheet.getPhysicalNumberOfRows();
            //if (rowNum < 2) {
            if (rowNum < EXCEL_ROW_OFFSET) {
                logger.error("找不到标题行");
                result.put("message", "数据导入失败，找不到标题行！");
                return;
            }

            // 解析标题部分
//            Row titleRow = sheet.getRow(1);
            Row titleRow = sheet.getRow(EXCEL_ROW_OFFSET - 1);
            if (titleRow == null) {
                logger.error("找不到标题行");
                result.put("message", "数据导入失败，找不到标题行！");
                return;
            }
            List<String> titleList = new ArrayList<>();
            for (int i = 0; i < titleRow.getLastCellNum(); i++) {
                titleList.add(StringUtils.trim(titleRow.getCell(i).getStringCellValue()));
            }

//            if (rowNum < 3) {
            if (rowNum < EXCEL_ROW_OFFSET + 1) {
                logger.info("数据行为空");
                result.put("message", "数据导入完成，数据行为空！");
                return;
            }
            // 解析验证数据部分，任何一行的任何一项不满足条件，则直接返回失败
            List<Map<String, Object>> itemList = new ArrayList<>();
            String id = IdUtil.getId();
//            for (int i = 2; i < rowNum; i++) {
            for (int i = EXCEL_ROW_OFFSET; i < rowNum; i++) {
                Row row = sheet.getRow(i);
                JSONObject rowParse = generateDataFromRow(row, itemList, titleList, id);
                if (!rowParse.getBoolean("result")) {
                    result.put("message", "数据导入失败，第" + (i + 1) + "行数据错误：" + rowParse.getString("message"));
                    return;
                }
            }
            String mainId = RequestUtil.getString(request, "mainId");
            this.importMaterialProcess(result, itemList, mainId);
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
                case "预留生产订单WBS":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "预留生产订单WBS为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("wbs", cellValue);
                    break;
                case "物料号":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "物料号为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("materialCode", cellValue.split("\\.")[0]);
                    break;
                case "物料描述":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "物料描述为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("materialDescription", cellValue);
                    break;
                case "配送数量":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "配送数量为空");
                        return oneRowCheck;
                    }
                    String _cellValue = cellValue.split("\\.")[0];
                    if (StringUtils.isNotBlank(cellValue)) {
                        Boolean strResult = _cellValue.matches("^[1-9]\\d*$");
                        if (strResult == false) {
                            oneRowCheck.put("message", "配送数量必须是正整数");
                            return oneRowCheck;
                        }
                    }
                    oneRowMap.put("deliveryQuantity", _cellValue);
                    break;
                case "实收数量":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "实收数量为空");
                        return oneRowCheck;
                    }
                    String _cellValue2 = cellValue.split("\\.")[0];
                    if (StringUtils.isNotBlank(cellValue)) {
                        Boolean strResult = _cellValue2.matches("^[0-9]\\d*$");
                        if (strResult == false) {
                            oneRowCheck.put("message", "实收数量必须是0或正整数");
                            return oneRowCheck;
                        }
                    }
                    oneRowMap.put("receivedQuantity", _cellValue2);
                    //原来的实收数量不支持导入
                    //oneRowMap.put("receivedQuantity", "0");
                    break;
                case "异常数量":
                    break;
                case "短描述":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "短描述为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("shortDescription", cellValue);
                    break;
                case "采购组":
                    oneRowMap.put("procurementGroup", cellValue);
                    break;
                case "供应商":
                    oneRowMap.put("supplier", cellValue);
                    break;
                default:
//                    oneRowCheck.put("message", "列“" + title + "”不存在");
//                    return oneRowCheck;
                    break;
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
    private void importMaterialProcess(JSONObject result, List<Map<String, Object>> itemList, String mainId) {
        //1.检查本次待导入的数据有没有重复项
        if (!this.isAllUnique(result, itemList)) {
            return;
        }
        //2.检查本次待导入的数据有没有和已经导入的数据存在重复项
        if (!this.isAllNotInMain(result, itemList, mainId)) {
            return;
        }
        //3.将待导入的数据进行导入和库存处理
        this.finalFight(result, itemList, mainId);
    }

    //..检查本次待导入的数据有没有重复项
    private boolean isAllUnique(JSONObject result, List<Map<String, Object>> itemList) {
        Set<String> set = new HashSet<>();
        int index = 0;
        for (int i = 0; i < itemList.size(); i++) {
            index = i + EXCEL_ROW_OFFSET + 1;//修正行数
            if (set.contains(itemList.get(i).get("wbs").toString() +
                    itemList.get(i).get("materialCode").toString() +
                    itemList.get(i).get("shortDescription").toString())) {
                break;
            } else {
                set.add(itemList.get(i).get("wbs").toString() +
                        itemList.get(i).get("materialCode").toString() +
                        itemList.get(i).get("shortDescription").toString());
            }
        }
        if (set.size() != itemList.size()) {
            result.put("message", "数据导入失败，第" + index + "行和前面记录存在重复数据！物料号:" +
                    itemList.get(index - EXCEL_ROW_OFFSET - 1).get("materialCode").toString());
            return false;
        }
        return true;
    }

    //..检查本次待导入的数据有没有和已经导入的数据存在重复项
    private boolean isAllNotInMain(JSONObject result, List<Map<String, Object>> itemList, String mainId) {
        JSONObject params = new JSONObject();
        params.put("mainId", mainId);
        List<JSONObject> materialList = newproductAssemblyMaterialDao.materialListQuery(params);
        Map<String, Integer> map = new HashedMap();
        int indexAlready = 0;
        for (JSONObject material : materialList) {
            map.put(material.get("wbs").toString() +
                    material.get("materialCode").toString() +
                    material.get("shortDescription").toString(), ++indexAlready);
        }
        int indexNow = 0;
        for (int i = 0; i < itemList.size(); i++) {
            indexNow = i + EXCEL_ROW_OFFSET + 1;//修正行数
            if (map.containsKey(itemList.get(i).get("wbs").toString() +
                    itemList.get(i).get("materialCode").toString() +
                    itemList.get(i).get("shortDescription").toString())) {
                result.put("message", "数据导入失败，第" + indexNow + "行已经包含在主表信息中！主表序号:" +
                        map.get(itemList.get(i).get("wbs").toString() +
                                itemList.get(i).get("materialCode").toString() +
                                itemList.get(i).get("shortDescription").toString()) + " 物料号:" +
                        itemList.get(indexNow - EXCEL_ROW_OFFSET - 1).get("materialCode").toString());
                return false;
            }
        }
        return true;
    }

    //..将待导入的数据进行导入和库存处理
    private void finalFight(JSONObject result, List<Map<String, Object>> itemList, String mainId) {
        for (int i = 0; i < itemList.size(); i++) {
            Map<String, Object> map = itemList.get(i);
            String json = JSON.toJSONString(map);
            JSONObject jsonObject = JSON.parseObject(json);
            jsonObject.put("mainId", mainId);
            jsonObject.put("lastTime", DateUtil.formatDate(jsonObject.getDate("CREATE_TIME_"), DateUtil.DATE_FORMAT_YMD));
            jsonObject.put("exceptionQuantity",
                    jsonObject.getInteger("deliveryQuantity") - jsonObject.getInteger("receivedQuantity"));
            newproductAssemblyMaterialDao.insertMaterial(jsonObject);
        }
        //更新主信息
        JSONObject modelDetail = newproductAssemblyMaterialDao.getModelDetail(mainId);
        modelDetail.put("lastTime", DateUtil.formatDate(new Date(), DateUtil.DATE_FORMAT_YMD));
        newproductAssemblyMaterialDao.updateModel(modelDetail);
        result.put("message", "数据导入成功！");
    }

    //..todo:mark处理分表
    public void importSubMaterial(JSONObject result, HttpServletRequest request) {
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
            Sheet sheet = wb.getSheet("分表");
            if (sheet == null) {
                logger.error("找不到导入模板");
                result.put("message", "数据导入失败，找不到导入模板导入页！");
                return;
            }
            int rowNum = sheet.getPhysicalNumberOfRows();
            if (rowNum < EXCEL_ROW_OFFSET) {
                logger.error("找不到标题行");
                result.put("message", "数据导入失败，找不到标题行！");
                return;
            }

            // 解析标题部分
            Row titleRow = sheet.getRow(EXCEL_ROW_OFFSET - 1);
            if (titleRow == null) {
                logger.error("找不到标题行");
                result.put("message", "数据导入失败，找不到标题行！");
                return;
            }
            List<String> titleList = new ArrayList<>();
            for (int i = 0; i < titleRow.getLastCellNum(); i++) {
                titleList.add(StringUtils.trim(titleRow.getCell(i).getStringCellValue()));
            }

            if (rowNum < EXCEL_ROW_OFFSET + 1) {
                logger.info("数据行为空");
                result.put("message", "数据导入完成，数据行为空！");
                return;
            }
            // 解析验证数据部分，任何一行的任何一项不满足条件，则直接返回失败
            List<Map<String, Object>> itemList = new ArrayList<>();
            String id = IdUtil.getId();
            for (int i = EXCEL_ROW_OFFSET; i < rowNum; i++) {
                Row row = sheet.getRow(i);
                JSONObject rowParse = this.generateSubDataFromRow(row, itemList, titleList, id);
                if (!rowParse.getBoolean("result")) {
                    result.put("message", "数据导入失败，第" + (i + 1) + "行数据错误：" + rowParse.getString("message"));
                    return;
                }
            }
            String mainId = RequestUtil.getString(request, "mainId");
            this.importSubMaterialProcess(result, itemList, mainId);
        } catch (Exception e) {
            logger.error("Exception in importExcel", e);
            result.put("message", "数据导入失败，系统异常！");
        }
    }

    //..
    private JSONObject generateSubDataFromRow(Row row, List<Map<String, Object>> itemList, List<String> titleList, String mainId) {
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
                case "分表号":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "分表号为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("subNo", cellValue.split("\\.")[0]);
                    break;
                case "序号":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "序号为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("orderNo", cellValue.split("\\.")[0]);
                    break;
                case "预留生产订单WBS":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "预留生产订单WBS为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("wbs", cellValue);
                    break;
                case "物料号":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "物料号为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("materialCode", cellValue.split("\\.")[0]);
                    break;
                case "物料描述":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "物料描述为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("materialDescription", cellValue);
                    break;
                case "配送数量":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "配送数量为空");
                        return oneRowCheck;
                    }
                    String _cellValue = cellValue.split("\\.")[0];
                    if (StringUtils.isNotBlank(cellValue)) {
                        Boolean strResult = _cellValue.matches("^[1-9]\\d*$");
                        if (strResult == false) {
                            oneRowCheck.put("message", "配送数量必须是正整数");
                            return oneRowCheck;
                        }
                    }
                    oneRowMap.put("deliveryQuantity", _cellValue);
                    break;
                case "短描述":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "短描述为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("shortDescription", cellValue);
                    break;
                case "手写标识号":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowMap.put("handwrittenSign", "0");
                        break;
                    } else if (cellValue.substring(0, 1).equalsIgnoreCase("1")) {
                        oneRowMap.put("handwrittenSign", "1");
                        break;
                    } else {
                        oneRowCheck.put("message", "手写标识号只能为1或空");
                        return oneRowCheck;
                    }
                default:
                    break;
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
    private void importSubMaterialProcess(JSONObject result, List<Map<String, Object>> itemList, String mainId) {
        //1.检查本次待导入的数据的subNo是否都一致，不一致返回错误信息
        if (!this.isAllSubNoSame(result, itemList)) {
            return;
        }
        //2.检查本次待导入的数据导有没有重复项
        if (!this.isAllSubUnique(result, itemList)) {
            return;
        }
        //3.检查本次待导入的数据是否全部在总表内，如果不在，返回错误信息
        if (!this.isAllSubInMain(result, itemList, mainId)) {
            return;
        }
        //4.找到已经存在的分表信息，将其中handwrittenSign为1的记录对应的记录在本次待导入信息中剔除
        this.makeSubYetExistsGetout(itemList, mainId);
        //5.在本次待导入信息剔除剩下的记录中找到handwrittenSign为1的记录，这才是待处理精确记录
        this.makeTheRestSubAllSign(itemList);
        //6.将待处理精确记录进行库存处理，然后将待处理精确记录更新回相应的分表信息，有改，无增。
        this.finalFightSub(result, itemList, mainId);
    }

    //..检查本次待导入的数据的subNo是否都一致，不一致返回错误信息
    private boolean isAllSubNoSame(JSONObject result, List<Map<String, Object>> itemList) {
        Set<String> set = new HashSet<>();
        for (Map<String, Object> map : itemList) {
            set.add(map.get("subNo").toString());
        }
        if (set.size() > 1) {
            result.put("message", "数据导入失败，分表号不唯一");
            return false;
        }
        return true;
    }

    //..检查本次待导入的数据导有没有重复项
    private boolean isAllSubUnique(JSONObject result, List<Map<String, Object>> itemList) {
        Set<String> set = new HashSet<>();
        int index = 0;
        for (int i = 0; i < itemList.size(); i++) {
            index = i + EXCEL_ROW_OFFSET + 1;//修正行数
            if (set.contains(itemList.get(i).get("subNo").toString() + "-" + itemList.get(i).get("orderNo").toString())) {
                break;
            } else {
                set.add(itemList.get(i).get("subNo").toString() + "-" + itemList.get(i).get("orderNo").toString());
            }
        }
        if (set.size() != itemList.size()) {
            result.put("message", "数据导入失败，第" + index + "行和前面记录存在重复数据！序号:" +
                    itemList.get(index - EXCEL_ROW_OFFSET - 1).get("orderNo").toString());
            return false;
        }
        return true;
    }

    //..检查本次待导入的数据是否全部在总表内，如果不在，返回错误信息
    private boolean isAllSubInMain(JSONObject result, List<Map<String, Object>> itemList, String mainId) {
        JSONObject params = new JSONObject();
        params.put("mainId", mainId);
        List<JSONObject> materialList = newproductAssemblyMaterialDao.materialListQuery(params);
        Set<String> set = new HashSet<>();
        for (JSONObject material : materialList) {
            set.add(material.get("wbs").toString() +
                    material.get("materialCode").toString() +
                    material.get("shortDescription").toString());
        }
        int index = 0;
        for (int i = 0; i < itemList.size(); i++) {
            index = i + EXCEL_ROW_OFFSET + 1;//修正行数
            if (!set.contains(itemList.get(i).get("wbs").toString() +
                    itemList.get(i).get("materialCode").toString() +
                    itemList.get(i).get("shortDescription").toString())) {
                result.put("message", "数据导入失败，第" + index + "行不包含在主表信息中！序号:" +
                        itemList.get(index - EXCEL_ROW_OFFSET - 1).get("orderNo").toString());
                return false;
            }
        }
        return true;
    }

    //..找到已经存在的分表信息，将其中handwrittenSign为1的记录对应的记录在本次待导入信息中剔除
    private void makeSubYetExistsGetout(List<Map<String, Object>> itemList, String mainId) {
        JSONObject params = new JSONObject();
        params.put("mainId", mainId);
        params.put("handwrittenSign", "1");
        List<JSONObject> materialSubList = newproductAssemblyMaterialDao.materialSubListQuery(params);
        Set<String> set = new HashSet<>();
        /**
         * 理论上同样的subNo+orderNo只会存在1条sub记录，但是不排除会出现在两次分表导入情况下相同的subNo+orderNo
         * 被挂接了不同的wbs+materialCode+shortDescription，这种属于输入错误，但是子表也会记录一个subNo+orderNo
         * 重复的记录，因此，剔除也要精确剔除
         */
        for (JSONObject materialSub : materialSubList) {
            set.add(materialSub.getString("subNo") + "-" + materialSub.getString("orderNo") + "-" +
                    materialSub.getString("wbs") + "-" + materialSub.getString("materialCode") + "-" +
                    materialSub.getString("shortDescription"));
        }
        //循环内同时清理，需要用迭代器
        Iterator<Map<String, Object>> iterator = itemList.iterator();
        while (iterator.hasNext()) {
            Map<String, Object> map = iterator.next();
            if (set.contains(map.get("subNo").toString() + "-" + map.get("orderNo").toString() + "-" +
                    map.get("wbs").toString() + "-" + map.get("materialCode").toString() + "-" +
                    map.get("shortDescription").toString())) {
                iterator.remove();
            }
        }
    }

    //..在本次待导入信息剔除剩下的记录中找到handwrittenSign为1的记录，这才是待处理精确记录
    private void makeTheRestSubAllSign(List<Map<String, Object>> itemList) {
        //循环内同时清理，需要用迭代器
        Iterator<Map<String, Object>> iterator = itemList.iterator();
        while (iterator.hasNext()) {
            Map<String, Object> map = iterator.next();
            if (map.get("handwrittenSign").toString().equalsIgnoreCase("0")) {
                iterator.remove();
            }
        }
    }

    //..将待处理精确记录进行库存处理，然后将待处理精确记录更新回相应的分表信息，有改，无增
    private void finalFightSub(JSONObject result, List<Map<String, Object>> itemList, String mainId) {
        if (itemList.isEmpty()) {
            result.put("message", "入库无变化，无需更新！");
        } else {
            JSONObject params = new JSONObject();
            params.put("mainId", mainId);
            List<JSONObject> materialList = newproductAssemblyMaterialDao.materialListQuery(params);
            Map<String, Object> materialListMap = new HashMap<>();
            for (JSONObject material : materialList) {
                materialListMap.put(material.getString("wbs") +
                        material.getString("materialCode") +
                        material.getString("shortDescription"), material);
            }
            for (Map<String, Object> map : itemList) {
                String uniqueKey = map.get("wbs").toString() +
                        map.get("materialCode").toString() +
                        map.get("shortDescription").toString();
                if (materialListMap.containsKey(uniqueKey)) {
                    //主表对象
                    JSONObject material = (JSONObject) materialListMap.get(uniqueKey);
                    //子表对象
                    JSONObject materialSub = new JSONObject(map);
                    materialSub.put("mainId", mainId);
                    //
                    material.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    material.put("UPDATE_TIME_", new Date());
                    material.put("lastTime", DateUtil.formatDate(material.getDate("UPDATE_TIME_"), DateUtil.DATE_FORMAT_YMD));
                    material.put("receivedQuantity",
                            material.getInteger("receivedQuantity") + materialSub.getInteger("deliveryQuantity"));
                    material.put("exceptionQuantity",
                            material.getInteger("deliveryQuantity") - material.getInteger("receivedQuantity"));
                    newproductAssemblyMaterialDao.updateMaterial(material);
                    //
                    params.put("subNo", materialSub.getString("subNo"));
                    params.put("orderNo", materialSub.getString("orderNo"));
                    params.put("wbs", materialSub.getString("wbs"));
                    params.put("materialCode", materialSub.getString("materialCode"));
                    params.put("shortDescription", materialSub.getString("shortDescription"));
                    List<JSONObject> materialSubList = newproductAssemblyMaterialDao.materialSubListQuery(params);
                    if (materialSubList.isEmpty()) {
                        newproductAssemblyMaterialDao.insertSubMaterial(materialSub);
                    } else {
                        /**
                         * 理论上同样的subNo+orderNo只会存在1条sub记录，但是不排除会出现在两次分表导入情况下相同的subNo+orderNo
                         * 被挂接了不同的wbs+materialCode+shortDescription，这种属于输入错误，但是子表也会记录一个subNo+orderNo
                         * 重复的记录，更新sub时用了5个属性进行精确匹配，就是为了准确定位分表信息，保证入库数据更新的精准性
                         */
                        for (JSONObject sub : materialSubList) {
                            sub.put("handwrittenSign", 1);
                            newproductAssemblyMaterialDao.updateSubMaterial(sub);
                        }
                    }
                }
            }
            result.put("message", "数据导入成功！");
        }
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
        String mainId = request.getParameter("mainId");
        param.put("mainId", mainId);
        List<JSONObject> businessList = newproductAssemblyMaterialDao.materialListQuery(param);

        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = request.getParameter("salesModel") + "物料明细总表";
        String excelName = nowDate + title;
        String[] fieldNames = {"预留生产订单WBS", "物料号", "物料描述", "配送数量", "实收数量", "异常数量",
                "短描述", "更新日期", "采购组", "供应商", "备注"};
        String[] fieldCodes = {"wbs", "materialCode", "materialDescription", "deliveryQuantity", "receivedQuantity", "exceptionQuantity",
                "shortDescription", "lastTime", "procurementGroup", "supplier", "remark"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(businessList, fieldNames, fieldCodes, title);
        //输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);

    }
}
