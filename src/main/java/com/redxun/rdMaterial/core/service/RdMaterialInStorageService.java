package com.redxun.rdMaterial.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.excel.ExcelReaderUtil;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.ExcelUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.rdMaterial.core.dao.RdMaterialInStorageDao;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.sys.org.entity.OsGroup;
import com.redxun.sys.org.manager.OsGroupManager;
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
public class RdMaterialInStorageService {
    private static Logger logger = LoggerFactory.getLogger(RdMaterialInStorageService.class);
    private static int EXCEL_ROW_OFFSET = 1;
    @Autowired
    private RdMaterialInStorageDao rdMaterialInStorageDao;
    @Autowired
    private RdMaterialFileService rdMaterialFileService;
    @Autowired
    private SysDicManager sysDicManager;
    @Autowired
    private OsGroupManager osGroupManager;

    //..
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JsonPageResult result = new JsonPageResult(true);
        JSONObject params = new JSONObject();
        CommonFuns.getSearchParam(params, request, true);
        List<JSONObject> businessList = rdMaterialInStorageDao.dataListQuery(params);
        int businessListCount = rdMaterialInStorageDao.countDataListQuery(params);
        result.setData(businessList);
        result.setTotal(businessListCount);
        return result;
    }

    //..
    public JSONObject getDataById(String id) throws Exception {
        return rdMaterialInStorageDao.getDataById(id);
    }

    //..
    public JsonResult deleteBusiness(String[] ids) throws Exception {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> idList = Arrays.asList(ids);
        JSONObject params = new JSONObject();
        params.put("businessIds", idList);
        List<JSONObject> files = rdMaterialFileService.getFileListInfos(params);//现获取到主档所有的文件信息备用
        rdMaterialFileService.deleteFileInfos(params);//删主档文件信息
        params.clear();
        params.put("ids", idList);
        rdMaterialInStorageDao.deleteBusiness(params);//删主档
        params.clear();
        params.put("mainIds", idList);
        List<JSONObject> itemList = rdMaterialInStorageDao.getItemList(params);//先获取到所有的明细备用
        List<String> itemIdList = new ArrayList<>();//获取到所有明细的id
        for (JSONObject item : itemList) {
            itemIdList.add(item.getString("id"));
        }
        rdMaterialInStorageDao.deleteItems(params);//删明细
        params.clear();
        params.put("businessIds", itemIdList);
        List<JSONObject> itemFiles = rdMaterialFileService.getFileListInfos(params);//现获取到明细所有的文件信息备用
        rdMaterialFileService.deleteFileInfos(params);//删明细文件信息
        /////////////////////////////////////////////////////////////////////////////////////////////////////////
        //删除评审单相关文件
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey("rdMaterialUploadPosition",
                "yanFaWuLiaoRuKuPingShen").getValue();
        for (JSONObject oneFile : files) {
            rdMaterialFileService.deleteOneFileFromDisk(oneFile.getString("id"),
                    oneFile.getString("fileName"), oneFile.getString("businessId"), filePathBase);
        }
        //删除评审单目录
        for (String oneBusinessId : idList) {
            rdMaterialFileService.deleteDirFromDisk(oneBusinessId, filePathBase);
        }
        //删除入库依据相关文件
        filePathBase = sysDicManager.getBySysTreeKeyAndDicKey("rdMaterialUploadPosition",
                "yanFaWuLiaoRuKuYiJu").getValue();
        for (JSONObject oneFile : files) {
            rdMaterialFileService.deleteOneFileFromDisk(oneFile.getString("id"),
                    oneFile.getString("fileName"), oneFile.getString("businessId"), filePathBase);
        }
        //删除入库依据目录
        for (String oneBusinessId : idList) {
            rdMaterialFileService.deleteDirFromDisk(oneBusinessId, filePathBase);
        }
        /////////////////////////////////////////////////////////////////////////////////////////////////////////
        //删除明细相关文件
        filePathBase = sysDicManager.getBySysTreeKeyAndDicKey("rdMaterialUploadPosition",
                "yanFaWuLiaoMingXi").getValue();
        for (JSONObject oneFile : itemFiles) {
            rdMaterialFileService.deleteOneFileFromDisk(oneFile.getString("id"),
                    oneFile.getString("fileName"), oneFile.getString("businessId"), filePathBase);
        }
        //删除明细目录
        for (String oneBusinessId : itemIdList) {
            rdMaterialFileService.deleteDirFromDisk(oneBusinessId, filePathBase);
        }
        return result;
    }

    //..
    public JsonResult saveBusiness(JSONObject jsonObject) throws Exception {
        JsonResult result = new JsonResult(true, "操作成功！");
        if (jsonObject.containsKey("responsibleUserId") &&
                StringUtil.isNotEmpty(jsonObject.getString("responsibleUserId"))) {
            OsGroup mainDep = osGroupManager.getMainDeps(jsonObject.getString("responsibleUserId"), "1");
            jsonObject.put("responsibleDepId", mainDep.getGroupId());
            jsonObject.put("responsibleDep", mainDep.getName());
        }
        if (StringUtil.isEmpty(jsonObject.getString("id"))) {
            jsonObject.put("id", IdUtil.getId());
            jsonObject.put("businessStatus", "编辑中");
            jsonObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            jsonObject.put("CREATE_TIME_", new Date());
            this.processItems(jsonObject);
            rdMaterialInStorageDao.insertBusiness(jsonObject);
        } else {
            jsonObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            jsonObject.put("UPDATE_TIME_", new Date());
            this.processItems(jsonObject);
            rdMaterialInStorageDao.updateBusiness(jsonObject);
        }
        result.setData(jsonObject.getString("id"));
        return result;
    }

    //..
    public JsonResult doCommitBusiness(JSONObject jsonObject) throws Exception {
        JsonResult result = new JsonResult(true, "操作成功！");
        if (jsonObject.containsKey("responsibleUserId") &&
                StringUtil.isNotEmpty(jsonObject.getString("responsibleUserId"))) {
            OsGroup mainDep = osGroupManager.getMainDeps(jsonObject.getString("responsibleUserId"), "1");
            jsonObject.put("responsibleDepId", mainDep.getGroupId());
            jsonObject.put("responsibleDep", mainDep.getName());
        }
        if (StringUtil.isEmpty(jsonObject.getString("id"))) {
            jsonObject.put("id", IdUtil.getId());
            jsonObject.put("businessStatus", "已提交");
            jsonObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            jsonObject.put("CREATE_TIME_", new Date());
            this.processItems(jsonObject);
            rdMaterialInStorageDao.insertBusiness(jsonObject);
        } else {
            jsonObject.put("businessStatus", "已提交");
            jsonObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            jsonObject.put("UPDATE_TIME_", new Date());
            this.processItems(jsonObject);
            rdMaterialInStorageDao.updateBusiness(jsonObject);
        }
        result.setData(jsonObject.getString("id"));
        return result;
    }

    //..
    private void processItems(JSONObject jsonObject) throws Exception {
        if (jsonObject.containsKey("items")
                && StringUtil.isNotEmpty(jsonObject.getString("items"))) {
            JSONArray jsonArray = new JSONArray();
            JSONArray jsonArrayItemData = jsonObject.getJSONArray("items");
            List<String> itemIds = new ArrayList<>();
            for (Object item : jsonArrayItemData) {
                JSONObject itemJson = (JSONObject) item;
                if (itemJson.containsKey("inQuantity")) {
                    itemJson.put("untreatedQuantity", itemJson.getString("inQuantity"));
                }
                if (itemJson.containsKey("_state")) {//处理新增和修改
                    if (itemJson.getString("_state").equalsIgnoreCase("added")) {
                        itemJson.put("id", IdUtil.getId());
                        itemJson.put("mainId", jsonObject.getString("id"));
                        itemJson.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                        itemJson.put("CREATE_TIME_", new Date());
                        rdMaterialInStorageDao.insertItem(itemJson);
                    } else if (itemJson.getString("_state").equalsIgnoreCase("modified")) {
                        jsonObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                        jsonObject.put("UPDATE_TIME_", new Date());
                        rdMaterialInStorageDao.updateItem(itemJson);
                    } else if (itemJson.getString("_state").equalsIgnoreCase("removed")) {
                        itemIds.add(itemJson.getString("id"));
                    }
                }
            }
            JSONObject params = new JSONObject();
            params.put("ids", itemIds);
            rdMaterialInStorageDao.deleteItems(params);//统一处理删除
            params.clear();
            params.put("businessIds", itemIds);
            List<JSONObject> itemFiles = rdMaterialFileService.getFileListInfos(params);//现获取到明细所有的文件信息备用
            rdMaterialFileService.deleteFileInfos(params);//删明细文件信息
            //删除明细相关文件
            String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey("rdMaterialUploadPosition",
                    "yanFaWuLiaoMingXi").getValue();
            for (JSONObject oneFile : itemFiles) {
                rdMaterialFileService.deleteOneFileFromDisk(oneFile.getString("id"),
                        oneFile.getString("fileName"), oneFile.getString("businessId"), filePathBase);
            }
            //删除明细目录
            for (String oneBusinessId : itemIds) {
                rdMaterialFileService.deleteDirFromDisk(oneBusinessId, filePathBase);
            }
        }
    }

    //..
    public List<JSONObject> getItemList(JSONObject params) throws Exception {
        return rdMaterialInStorageDao.getItemList(params);
    }

    //..
    public JsonPageResult<?> itemListQuery(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JsonPageResult result = new JsonPageResult(true);
        JSONObject params = new JSONObject();
        CommonFuns.getSearchParam(params, request, true);
        if (StringUtil.isNotEmpty(RequestUtil.getString(request, "businessStatus"))) {
            params.put("businessStatus", RequestUtil.getString(request, "businessStatus"));
        }
        List<JSONObject> businessList = rdMaterialInStorageDao.itemListQuery(params);
        int businessListCount = rdMaterialInStorageDao.countItemListQuery(params);
        result.setData(businessList);
        result.setTotal(businessListCount);
        return result;
    }

    //..
    public ResponseEntity<byte[]> importItemTDownload() {
        try {
            String fileName = "研发物料管理明细导入模板.xlsx";
            // 创建文件实例
            File file = new File(RdMaterialInStorageService.class.getClassLoader().
                    getResource("templates/rdMaterial/" + fileName).toURI());
            String finalDownloadFileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");
            // 设置httpHeaders,使浏览器响应下载
            HttpHeaders headers = new HttpHeaders();
            // 告诉浏览器执行下载的操作，“attachment”告诉了浏览器进行下载,下载的文件 文件名为 finalDownloadFileName
            headers.setContentDispositionFormData("attachment", finalDownloadFileName);
            // 设置响应方式为二进制，以二进制流传输
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file), headers, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Exception in importItemTDownload", e);
            return null;
        }
    }

    //..
    public void importItem(JSONObject result, HttpServletRequest request) {
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
//            for (int i = 2; i < rowNum; i++) {
            for (int i = EXCEL_ROW_OFFSET; i < rowNum; i++) {
                Row row = sheet.getRow(i);
                JSONObject rowParse = generateDataFromRow(row, itemList, titleList);
                if (!rowParse.getBoolean("result")) {
                    result.put("message", "数据导入失败，第" + (i + 1) + "行数据错误：" + rowParse.getString("message"));
                    return;
                }
            }
            String businessId = RequestUtil.getString(request, "businessId");
            this.importItemProcess(result, itemList, businessId);
        } catch (Exception e) {
            logger.error("Exception in importExcel", e);
            result.put("message", "数据导入失败，系统异常！:" + (e.getCause() == null ? e.getMessage() : e.getCause().getMessage()));
            System.out.println("");
        }
    }

    //..
    private JSONObject generateDataFromRow(Row row, List<Map<String, Object>> itemList, List<String> titleList) {
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
                case "物料号":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "物料号为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("materialCode", cellValue.split("\\.")[0].trim());
                    break;
                case "物料类型":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "物料类型为空");
                        return oneRowCheck;
                    } else if (!cellValue.equalsIgnoreCase("液压类") &&
                            !cellValue.equalsIgnoreCase("电气类") &&
                            !cellValue.equalsIgnoreCase("动力类") &&
                            !cellValue.equalsIgnoreCase("结构件类")) {
                        oneRowCheck.put("message", "物料类型必须为 液压类、电气类、动力类、结构件类 这几个值");
                        return oneRowCheck;
                    }
                    oneRowMap.put("materialType", cellValue);
                    break;
                case "入库数量":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "入库数量为空");
                        return oneRowCheck;
                    }
                    String _cellValue = cellValue.split("\\.")[0];
                    if (StringUtils.isNotBlank(cellValue)) {
                        Boolean strResult = _cellValue.matches("^[1-9]\\d*$");
                        if (strResult == false) {
                            oneRowCheck.put("message", "入库数量必须是正整数");
                            return oneRowCheck;
                        }
                    }
                    oneRowMap.put("inQuantity", _cellValue);
                    break;
                case "物料描述":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "物料描述为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("materialDescription", cellValue.trim());
                    break;
                default:
                    break;
            }
        }
        oneRowMap.put("id", IdUtil.getId());
        oneRowMap.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        oneRowMap.put("CREATE_TIME_", DateUtil.formatDate(new Date(), DateUtil.DATE_FORMAT_FULL));
        oneRowMap.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        oneRowMap.put("UPDATE_TIME_", DateUtil.formatDate(new Date(), DateUtil.DATE_FORMAT_FULL));
        oneRowMap.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        itemList.add(oneRowMap);
        oneRowCheck.put("result", true);
        return oneRowCheck;
    }

    //..
    private void importItemProcess(JSONObject result, List<Map<String, Object>> itemList,
                                   String businessId) {
        for (Map<String, Object> map : itemList) {
            JSONObject itemJson = new JSONObject(map);
            if (itemJson.containsKey("inQuantity")) {
                itemJson.put("untreatedQuantity", itemJson.getString("inQuantity"));
            }
            itemJson.put("id", IdUtil.getId());
            itemJson.put("mainId", businessId);
            itemJson.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            itemJson.put("CREATE_TIME_", new Date());
            rdMaterialInStorageDao.insertItem(itemJson);
        }
        result.put("message", "数据导入成功！");
    }
}
