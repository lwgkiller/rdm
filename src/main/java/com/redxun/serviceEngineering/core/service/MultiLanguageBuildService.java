package com.redxun.serviceEngineering.core.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.excel.ExcelReaderUtil;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.ExcelUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.materielextend.core.service.MaterielService;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmZhgl.core.service.PatentInterpretationService;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.serviceEngineering.core.dao.MulitilingualTranslationDao;
import com.redxun.serviceEngineering.core.dao.MultiLanguageBuildDao;
import com.redxun.sys.core.entity.SysDic;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.sys.core.util.SysPropertiesUtil;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.SerializationUtils;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class MultiLanguageBuildService {
    private static Logger logger = LoggerFactory.getLogger(PatentInterpretationService.class);
    @Autowired
    MultiLanguageBuildDao multiLanguageBuildDao;
    @Autowired
    private BpmInstManager bpmInstManager;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;
    @Autowired
    SysDicManager sysDicManager;

    //..
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        getListParams(params, request);
        List<Map<String, Object>> businessList = multiLanguageBuildDao.dataListQuery(params);
        for (Map<String, Object> business : businessList) {
            if (business.get("CREATE_TIME_") != null) {
                business.put("CREATE_TIME_", DateUtil.formatDate((Date) business.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        //查询当前处理人--非会签
        xcmgProjectManager.setTaskCurrentUser(businessList);
        //查询当前处理人--会签
        //rdmZhglUtil.setTaskInfo2Data(businessList, ContextUtil.getCurrentUserId());
        int businessListCount = multiLanguageBuildDao.countDataListQuery(params);
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
            params.put("sortField", "businessStatus");
            params.put("sortOrder", "asc");
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
    public JsonResult deleteBusiness(String[] ids, String[] instIds) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> businessIds = Arrays.asList(ids);
        Map<String, Object> param = new HashMap<>();
        param.put("businessIds", businessIds);
        multiLanguageBuildDao.deleteBusiness(param);
        multiLanguageBuildDao.deleteItem(param);
        for (String oneInstId : instIds) {
            // 删除实例,不是同步删除，但是总量是能一对一的
            bpmInstManager.deleteCascade(oneInstId, "");
        }
        return result;
    }

    //..
    public JSONObject getDetail(String businessId) {
        JSONObject jsonObject = multiLanguageBuildDao.queryDetailById(businessId);
        if (jsonObject == null) {
            return new JSONObject();
        }
        return jsonObject;
    }

    //..
    public List<JSONObject> getItemList(String businessId) {
        List<JSONObject> itemList = multiLanguageBuildDao.queryItemList(businessId);
        return itemList;
    }

    //..
    public void createBusiness(JSONObject formData) {
        formData.put("id", IdUtil.getId());
        formData.put("applyUserId", ContextUtil.getCurrentUserId());
        formData.put("applyUser", ContextUtil.getCurrentUser().getFullname());
        formData.put("applyDate", DateUtil.formatDate(new Date(), DateUtil.DATE_FORMAT_YMD));
        //@lwgkiller:此处是因为(草稿状态和空状态)无节点，提交后首节点会跳过，因此默认将首节点（编制中）的编号进行初始化写入
        formData.put("businessStatus", "A-editing");
        //是否跳过英语给初始值否
        formData.put("jumpEnglish","false");
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        formData.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        formData.put("manualType", RdmConst.PARTS_ATLAS);
        multiLanguageBuildDao.insertBusiness(formData);
        // 如果有明细的变化则处理
        if (formData.getJSONArray("itemChangeData") != null && !formData.getJSONArray("itemChangeData").isEmpty()) {
            this.saveOrUpdateItems(formData.getJSONArray("itemChangeData"), formData.getString("id"));
        }
        //语言匹配处理
        this.glossaryApplyitemsHitTagMatching(formData);
    }

    //..
    public void updateBusiness(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        String multilingualSign = formData.getString("multilingualSign");
        if (StringUtils.isNotBlank(multilingualSign)) {
            List<SysDic> sysDicList = sysDicManager.getByTreeKey("multilingualKey");
            for (SysDic sysDic : sysDicList) {
                if (multilingualSign.equalsIgnoreCase(sysDic.getValue())) {
                    formData.put("multilingualText",sysDic.getKey());
                }
            }

        }
        if(StringUtils.isBlank(formData.getString("manualType"))){
            formData.put("manualType",RdmConst.PARTS_ATLAS);
        }
        multiLanguageBuildDao.updateBusiness(formData);
        // 如果有明细的变化则处理
        if (formData.getJSONArray("itemChangeData") != null && !formData.getJSONArray("itemChangeData").isEmpty()) {
            this.saveOrUpdateItems(formData.getJSONArray("itemChangeData"), formData.getString("id"));
        }
        //语言匹配处理
        if("A-editing".equals(formData.getString("businessStatus")))
        {
            this.glossaryApplyitemsHitTagMatching(formData);
        }

    }

    //..
    public void saveOrUpdateItems(JSONArray changeGridDataJson, String mainId) {
        for (int i = 0; i < changeGridDataJson.size(); i++) {
            JSONObject oneObject = changeGridDataJson.getJSONObject(i);
            String state = oneObject.getString("_state");
            String id = oneObject.getString("id");
            oneObject.put("mainId", mainId);
            JSONObject jsonObject = multiLanguageBuildDao.queryDetailById(mainId);
            if (jsonObject !=null) {
                oneObject.put("multilingualSign", jsonObject.getString("multilingualSign"));
            }
            if ("added".equals(state) || StringUtils.isBlank(id)) {
                //新增
                oneObject.put("id", IdUtil.getId());
                oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                oneObject.put("CREATE_TIME_", new Date());
                oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                oneObject.put("UPDATE_TIME_", new Date());
                oneObject.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
                multiLanguageBuildDao.insertItem(oneObject);
            } else if ("modified".equals(state)) {
                oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                oneObject.put("UPDATE_TIME_", new Date());
                multiLanguageBuildDao.updateItem(oneObject);
            } else if ("removed".equals(state)) {
                JSONObject params = new JSONObject();
                params.put("id", id);
                multiLanguageBuildDao.deleteItem(params);
            }
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
            Sheet sheet = wb.getSheet("sheet0");
            if (sheet == null) {
                logger.error("找不到导入模板");
                result.put("message", "数据导入失败，找不到导入模板导入页！");
                return;
            }
            int rowNum = sheet.getPhysicalNumberOfRows();

            if (rowNum < 1) {
                logger.error("没有数据");
                result.put("message", "数据导入失败，找不到数据！");
                return;
            }

             //解析标题部分
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

            if (rowNum < 1) {
                logger.info("数据行为空");
                result.put("message", "数据导入完成，数据行为空！");
                return;
            }
            // 解析验证数据部分，任何一行的任何一项不满足条件，则直接返回失败
            List<Map<String, Object>> itemList = new ArrayList<>();
            String id = IdUtil.getId();
            String mainId = RequestUtil.getString(request, "mainId");
            for (int i = 1; i < rowNum; i++) {
                Row row = sheet.getRow(i);
                JSONObject rowParse = generateDataFromRow(row, itemList, titleList, mainId);
                if (!rowParse.getBoolean("result")) {
                    result.put("message", "数据导入失败，第" + (i + 1) + "行数据错误：" + rowParse.getString("message"));
                    return;
                }
            }

            for (int i = 0; i < itemList.size(); i++) {
                Map<String, Object> map = itemList.get(i);
                String json = JSON.toJSONString(map);
                JSONObject jsonObject = JSON.parseObject(json);
                jsonObject.put("mainId", mainId);
                multiLanguageBuildDao.insertItem(jsonObject);
            }
            //语言匹配处理
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", mainId);
            this.glossaryApplyitemsHitTagMatching(jsonObject);
            result.put("message", "数据导入成功！");
        } catch (Exception e) {
            logger.error("Exception in importExcel", e);
            result.put("message", "数据导入失败，系统异常！");
        }
    }
    public void importTransExcel(JSONObject result, HttpServletRequest request) {
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
            Sheet sheet = wb.getSheet("sheet0");
            if (sheet == null) {
                logger.error("找不到导入模板");
                result.put("message", "数据导入失败，找不到导入模板导入页！");
                return;
            }
            int rowNum = sheet.getPhysicalNumberOfRows();

            if (rowNum < 2) {
                logger.error("没有数据");
                result.put("message", "数据导入完成，数据行为空！！");
                return;
            }

            //解析标题部分
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

            if (rowNum < 2) {
                logger.info("数据行为空");
                result.put("message", "数据导入完成，数据行为空！");
                return;
            }
            // 解析验证数据部分，任何一行的任何一项不满足条件，则直接返回失败
            List<Map<String, Object>> itemList = new ArrayList<>();
            String mainId = RequestUtil.getString(request, "mainId");
            for (int i = 2; i < rowNum; i++) {
                Row row = sheet.getRow(i);
                JSONObject rowParse = generateDataFromRowTrans(row, itemList, titleList, mainId);
                if (!rowParse.getBoolean("result")) {
                    result.put("message", "数据导入失败，第" + (i + 1) + "行数据错误：" + rowParse.getString("message"));
                    return;
                }
            }
            if(StringUtils.isNotBlank(mainId)){
                multiLanguageBuildDao.deleteItemByMainId(mainId);
            }

            for (int i = 0; i < itemList.size(); i++) {
                Map<String, Object> map = itemList.get(i);
                String json = JSON.toJSONString(map);
                JSONObject jsonObject = JSON.parseObject(json);
                jsonObject.put("mainId", mainId);
                multiLanguageBuildDao.insertItem(jsonObject);
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
                case "名称":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "名称为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("originChinese", cellValue);
                    break;
//                case "建议中文":
//                    oneRowMap.put("chinese", cellValue);
//                    break;
                case "英文":
                    oneRowMap.put("en", cellValue);
                    break;
                case "语言":
                    JSONObject jsonObject = multiLanguageBuildDao.queryDetailById(mainId);
                    if (jsonObject !=null) {
                        if(jsonObject.getString("multilingualSign").equalsIgnoreCase(cellValue)||jsonObject.getString("multilingualSign").equalsIgnoreCase("0")){
                            break;
                        }else {

                            oneRowCheck.put("message", "检测到导入表中与网页多语言标识不一致");
                            return oneRowCheck;
                        }
                    }
                    break;
                case "错误提示":
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
    private JSONObject generateDataFromRowTrans(Row row, List<Map<String, Object>> itemList, List<String> titleList, String mainId) {
        JSONObject oneRowCheck = new JSONObject();
        oneRowCheck.put("result", false);
        Map<String, Object> oneRowMap = new HashMap<>();
        JSONObject jsonObject = multiLanguageBuildDao.queryDetailById(mainId);
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
                case "原始中文":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "名称为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("originChinese", cellValue);
                    break;
                case "建议中文":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "建议中文为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("chinese", cellValue);
                    break;
                case "英文":
                    oneRowMap.put("en", cellValue);
                    break;
                case "多语言key":

                    if (jsonObject !=null) {
                        if(jsonObject.getString("multilingualSign").equalsIgnoreCase(cellValue)||jsonObject.getString("multilingualSign").equalsIgnoreCase("0")){
                            break;
                        }else {

                            oneRowCheck.put("message", "检测到导入表中与网页多语言标识不一致");
                            return oneRowCheck;
                        }
                    }
                    break;
                case "多语言文本":
                    oneRowMap.put("multilingualText", cellValue);
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
        itemList.add(oneRowMap);
        oneRowCheck.put("result", true);
        return oneRowCheck;
    }

    //..
    public ResponseEntity<byte[]> importTemplateDownload() {
        try {
            String fileName = "零件图册缺词补充导入模板.xlsx";
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
    public ResponseEntity<byte[]> importTransTemplateDownload() {
        try {
            String fileName = "零件图册译文明细导入模板.xls";
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
            logger.error("Exception in importTransTemplateDownload", e);
            return null;
        }
    }
    //..
    public void exportItem(HttpServletRequest request, HttpServletResponse response) {
        String filterParams = request.getParameter("filter");
        JSONArray jsonArray = JSONArray.parseArray(filterParams);
        List<String> listids = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            listids.add(jsonArray.getJSONObject(i).getString("value"));
        }
        JSONObject param = new JSONObject();
        param.put("ids", listids);
        List<JSONObject> listData = multiLanguageBuildDao.queryItemListByIds(param);
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "术语明细";
        String excelName = nowDate + title;
        String[] fieldNames = {"物料编码", "原始中文","建议中文", "英文", "多语言key", "多语言文本", "备注"};
        String[] fieldCodes = {"materialCode","originChinese" ,"chinese", "en", "multilingualSign", "multilingualText", "remark"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(listData, fieldNames, fieldCodes, title);
        //输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }

    //..
    public void exportItemGss(HttpServletRequest request, HttpServletResponse response) {
        String filterParams = request.getParameter("filterGss");
        JSONArray jsonArray = JSONArray.parseArray(filterParams);
        List<String> listMaterialCodes = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            listMaterialCodes.add(jsonArray.getJSONObject(i).getString("value"));
        }
        JSONObject param = new JSONObject();
        param.put("materialCodes", listMaterialCodes);
        List<JSONObject> listData = multiLanguageBuildDao.queryApplyitemsListByMaterialCodes(param);
        for (JSONObject jsonObject : listData) {
            jsonObject.put("factoryNo", SysPropertiesUtil.getGlobalProperty("factoryNo"));
            jsonObject.put(jsonObject.getString("multilingualSign"),jsonObject.getString("multilingualText"));
            jsonObject.put("zh-CN",jsonObject.getString("originChinese"));
        }
        List<JSONObject> chineseListData = new ArrayList<>();
        for (JSONObject jsonObject : listData) {
            chineseListData.add((JSONObject) jsonObject.clone());
        }

        for (JSONObject jsonObject : chineseListData){
            jsonObject.put("zh-CN",jsonObject.getString("chinese"));
        }
        listData.addAll(chineseListData);
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "术语明细-导GSS用";
        String excelName = nowDate + title;
        String[] fieldNames = {"zh-CN", "en", "ru", "pt", "de", "es",
                                "fr", "it", "da", "nl", "pl", "tr",
                                "sv","mn","zh-TW","sl", "ro","no",
                                "hu","ko","ar",
                                "工厂代码"};

        String[] fieldCodes = {"zh-CN", "en","ru", "pt", "de", "es",
                                "fr", "it", "da", "nl", "pl", "tr",
                                "sv","mn","zh-TW","sl", "ro","no",
                                "hu","ko","ar",
                                "factoryNo"};
//        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(listData, fieldNames, fieldCodes, title);
        XSSFWorkbook wbObj = ExcelUtils.exportXlsxExcelWBWithoutTitle(listData, fieldNames, fieldCodes);

        //输出
        ExcelUtils.writeXlsxWorkBook2Stream(excelName, wbObj, response);
    }

    /**
     * 语言匹配处理,处理是否物料匹配,是否原始中文匹配等标记字段
     *
     *  service_engineering_multilingualglossary_apply的行对象
     */
    private void glossaryApplyitemsHitTagMatching(JSONObject jsonObject) {
        List<JSONObject> itemList = this.getItemList(jsonObject.getString("id"));
        String  multilingualSign  =this.getDetail(jsonObject.getString("id")).getString("multilingualSign");
        for (JSONObject item : itemList) {
            //物料是否匹配
            JSONObject params = new JSONObject();
            params.put("materialCode", StringUtil.isEmpty(item.getString("materialCode")) ? "materialCode" : item.getString("materialCode"));
            List<Map<String, Object>> glossaryList = multiLanguageBuildDao.glossaryListQuery(params);
            if (glossaryList.size() > 0) {
                item.put("chineseMaterialHit", "true");
                //lihui的要求原始中文不要变
//                item.put("originChinese", glossaryList.get(0).get("originChinese").toString());
                item.put("chinese", glossaryList.get(0).get("chineseName").toString());
                item.put("en", glossaryList.get(0).get("englishName").toString());
                item.put("multilingualSign",multilingualSign);
                if(multilingualSign.equalsIgnoreCase("ru") &&(glossaryList.get(0).get("russianName") !=null) )
                {
                    item.put("multilingualText", glossaryList.get(0).get("russianName").toString());
                }
                if(multilingualSign.equalsIgnoreCase("pt") &&(glossaryList.get(0).get("portugueseName") !=null))
                {
                        item.put("multilingualText", glossaryList.get(0).get("portugueseName").toString());
                }
                if(multilingualSign.equalsIgnoreCase("de") &&(glossaryList.get(0).get("germanyName")!=null))
                {
                    item.put("multilingualText", glossaryList.get(0).get("germanyName").toString());
                }
                if(multilingualSign.equalsIgnoreCase("es") &&(glossaryList.get(0).get("spanishName")!=null))
                {
                    item.put("multilingualText", glossaryList.get(0).get("spanishName").toString());
                }
                if(multilingualSign.equalsIgnoreCase("fr") &&(glossaryList.get(0).get("frenchName")!=null))
                {
                    item.put("multilingualText", glossaryList.get(0).get("frenchName").toString());
                }
                if(multilingualSign.equalsIgnoreCase("it") &&(glossaryList.get(0).get("italianName")!=null))
                {
                    item.put("multilingualText", glossaryList.get(0).get("italianName").toString());
                }
                if(multilingualSign.equalsIgnoreCase("da") &&(glossaryList.get(0).get("danishName")!=null))
                {
                    item.put("multilingualText", glossaryList.get(0).get("danishName").toString());
                }
                if(multilingualSign.equalsIgnoreCase("nl") &&(glossaryList.get(0).get("dutchName")!=null))
                {
                    item.put("multilingualText", glossaryList.get(0).get("dutchName").toString());
                }
                if(multilingualSign.equalsIgnoreCase("pl") &&(glossaryList.get(0).get("polishName")!=null))
                {
                    item.put("multilingualText", glossaryList.get(0).get("polishName").toString());
                }
                if(multilingualSign.equalsIgnoreCase("tr") &&(glossaryList.get(0).get("turkishName")!=null))
                {
                    item.put("multilingualText", glossaryList.get(0).get("turkishName").toString());
                }
                if(multilingualSign.equalsIgnoreCase("sl") &&(glossaryList.get(0).get("sloveniaName")!=null))
                {
                    item.put("multilingualText", glossaryList.get(0).get("sloveniaName").toString());
                }
                if(multilingualSign.equalsIgnoreCase("ro") &&(glossaryList.get(0).get("romaniaName")!=null))
                {
                    item.put("multilingualText", glossaryList.get(0).get("romaniaName").toString());
                }
                multiLanguageBuildDao.updateItem(item);
            } else {
                params.clear();
                params.put("originChinese", StringUtil.isEmpty(item.getString("originChinese")) ? "originChinese" : item.getString("originChinese"));
                glossaryList.clear();
                glossaryList = multiLanguageBuildDao.glossaryListQuery(params);
                if (glossaryList.size() > 0) {
                    item.put("chineseMaterialHit", "false");
                    item.put("chineseTextHit", "true");
                    item.put("chinese", glossaryList.get(0).get("chineseName").toString());
                    item.put("en", glossaryList.get(0).get("englishName").toString());
                    item.put("multilingualSign",multilingualSign);
                    if(multilingualSign.equalsIgnoreCase("ru") &&(glossaryList.get(0).get("russianName") !=null) )
                    {
                        item.put("multilingualText", glossaryList.get(0).get("russianName").toString());
                    }
                    if(multilingualSign.equalsIgnoreCase("pt") &&(glossaryList.get(0).get("portugueseName") !=null))
                    {
                        item.put("multilingualText", glossaryList.get(0).get("portugueseName").toString());
                    }
                    if(multilingualSign.equalsIgnoreCase("de") &&(glossaryList.get(0).get("germanyName")!=null))
                    {
                        item.put("multilingualText", glossaryList.get(0).get("germanyName").toString());
                    }
                    if(multilingualSign.equalsIgnoreCase("es") &&(glossaryList.get(0).get("spanishName")!=null))
                    {
                        item.put("multilingualText", glossaryList.get(0).get("spanishName").toString());
                    }
                    if(multilingualSign.equalsIgnoreCase("fr") &&(glossaryList.get(0).get("frenchName")!=null))
                    {
                        item.put("multilingualText", glossaryList.get(0).get("frenchName").toString());
                    }
                    if(multilingualSign.equalsIgnoreCase("it") &&(glossaryList.get(0).get("italianName")!=null))
                    {
                        item.put("multilingualText", glossaryList.get(0).get("italianName").toString());
                    }
                    if(multilingualSign.equalsIgnoreCase("da") &&(glossaryList.get(0).get("danishName")!=null))
                    {
                        item.put("multilingualText", glossaryList.get(0).get("danishName").toString());
                    }
                    if(multilingualSign.equalsIgnoreCase("nl") &&(glossaryList.get(0).get("dutchName")!=null))
                    {
                        item.put("multilingualText", glossaryList.get(0).get("dutchName").toString());
                    }
                    if(multilingualSign.equalsIgnoreCase("pl") &&(glossaryList.get(0).get("polishName")!=null))
                    {
                        item.put("multilingualText", glossaryList.get(0).get("polishName").toString());
                    }
                    if(multilingualSign.equalsIgnoreCase("tr") &&(glossaryList.get(0).get("turkishName")!=null))
                    {
                        item.put("multilingualText", glossaryList.get(0).get("turkishName").toString());
                    }
                    if(multilingualSign.equalsIgnoreCase("sl") &&(glossaryList.get(0).get("sloveniaName")!=null))
                    {
                        item.put("multilingualText", glossaryList.get(0).get("sloveniaName").toString());
                    }
                    if(multilingualSign.equalsIgnoreCase("ro") &&(glossaryList.get(0).get("romaniaName")!=null))
                    {
                        item.put("multilingualText", glossaryList.get(0).get("romaniaName").toString());
                    }
                } else {
                    item.put("chineseMaterialHit", "false");
                    item.put("chineseTextHit", "false");
                    item.put("chineseHit", "");
                    item.put("enHit", "");
                }
                multiLanguageBuildDao.updateItem(item);
            }
        }
    }

    /**
     * 语言日期处理
     *
     * @param key        处理那种语言的更新日期,service_engineering_multilingualglossary的字段名
     * @param jsonObject service_engineering_multilingualglossary的行对象
     */
    private void glossaryDateTrasToGss(String key, JSONObject jsonObject) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.M.d");
        jsonObject.put(key, jsonObject.getString(key) == null ? "" :
                simpleDateFormat.format(DateUtil.parseDate(jsonObject.getString(key))
                ));
    }

    /**
     * 根据全对象里的明细对象,分别将语言库对应语种进行更新
     *
     * @param jsonObject service_engineering_multilingualglossary_apply的行对象
     */
    public void glossaryUpdateProcessing(JSONObject jsonObject) {
        List<JSONObject> itemList = this.getItemList(jsonObject.getString("id"));
        JSONObject params = new JSONObject();
        String chineseId = "";
        for (JSONObject item : itemList) {
            params.put("materialCode", item.getString("materialCode"));
            params.put("originChinese", item.getString("originChinese"));
            List<Map<String, Object>> glossaryList = multiLanguageBuildDao.glossaryListQuery(params);
            if (glossaryList.size() > 0) {
                JSONObject glossary = new JSONObject(glossaryList.get(0));
                chineseId = glossary.getString("chineseId");

                //中文修改
                glossary.put("materialCode", item.getString("materialCode"));
                glossary.put("originChinese",item.getString("originChinese"));
                glossary.put("chineseName", item.getString("chinese"));
                glossary.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                glossary.put("UPDATE_TIME_", new Date());
                glossary.put("id", chineseId);
                multiLanguageBuildDao.updateChineseLbj(glossary);
                //英文修改
                JSONObject englishInfo = new JSONObject();
                englishInfo.put("englishName", item.getString("en"));
                englishInfo.put("chineseId", chineseId);
                englishInfo.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                englishInfo.put("UPDATE_TIME_", new Date());
                if(glossary.getString("englishName") !=null)
                {
                    multiLanguageBuildDao.updateEnglishLbj(englishInfo);
                }else
                {
                    englishInfo.put("id", chineseId);
                    multiLanguageBuildDao.addEnglishLbj(englishInfo);
                }

                String multilingualSign = item.getString("multilingualSign");
                if (multilingualSign != "0") {
                    if(multilingualSign.equals( "ru")){
                        //俄文修改
                        JSONObject russianInfo = new JSONObject();
                        russianInfo.put("russianName", item.getString("multilingualText"));
                        russianInfo.put("chineseId", chineseId);
                        russianInfo.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                        russianInfo.put("UPDATE_TIME_", new Date());
                        if(glossary.getString("russianName") !=null){
                            multiLanguageBuildDao.updateRussianLbj(russianInfo);
                        }else{
                            russianInfo.put("id", chineseId);
                            multiLanguageBuildDao.addRussianLbj(russianInfo);
                        }
                    }
                    if(multilingualSign.equals("pt") ){
                        //葡文修改
                        JSONObject multilInfo = new JSONObject();
                        multilInfo.put("portugueseName", item.getString("multilingualText"));
                        multilInfo.put("chineseId", chineseId);
                        multilInfo.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                        multilInfo.put("UPDATE_TIME_", new Date());
                        if(glossary.getString("portugueseName") !=null){
                            multiLanguageBuildDao.updatePortugueseLbj(multilInfo);
                        }else{
                            multilInfo.put("id",chineseId);
                            multiLanguageBuildDao.addPortugueseLbj(multilInfo);
                        }

                    }
                    if(multilingualSign.equals("de")){
                        //德文修改
                        JSONObject germanyInfo = new JSONObject();
                        germanyInfo.put("germanyName", item.getString("multilingualText"));
                        germanyInfo.put("chineseId", chineseId);
                        germanyInfo.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                        germanyInfo.put("UPDATE_TIME_", new Date());
                        if(glossary.getString("germanyName") !=null){
                            multiLanguageBuildDao.updateGermanyLbj(germanyInfo);
                        }else{
                            germanyInfo.put("id",chineseId);
                            multiLanguageBuildDao.addGermanyLbj(germanyInfo);
                        }

                    }
                    if(multilingualSign.equals("es")){
                        //西文修改
                        JSONObject spanishInfo = new JSONObject();
                        spanishInfo.put("spanishName", item.getString("multilingualText"));
                        spanishInfo.put("chineseId", chineseId);
                        spanishInfo.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                        spanishInfo.put("UPDATE_TIME_", new Date());

                        if(glossary.getString("spanishName") !=null){
                            multiLanguageBuildDao.updateSpanishLbj(spanishInfo);
                        }else{
                            spanishInfo.put("id",chineseId);
                            multiLanguageBuildDao.addSpanishLbj(spanishInfo);
                        }
                    }
                    if(multilingualSign.equals("fr") ){
                        //法文修改
                        JSONObject frenchInfo = new JSONObject();
                        frenchInfo.put("frenchName", item.getString("multilingualText"));
                        frenchInfo.put("chineseId", chineseId);
                        frenchInfo.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                        frenchInfo.put("UPDATE_TIME_", new Date());

                        if(glossary.getString("frenchName") !=null){
                            multiLanguageBuildDao.updateFrenchLbj(frenchInfo);
                        }else{
                            frenchInfo.put("id",chineseId);
                            multiLanguageBuildDao.addFrenchLbj(frenchInfo);
                        }
                    }
                    if(multilingualSign.equals("it")){
                        //意文修改
                        JSONObject italianInfo = new JSONObject();
                        italianInfo.put("italianName", item.getString("multilingualText"));
                        italianInfo.put("chineseId", chineseId);
                        italianInfo.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                        italianInfo.put("UPDATE_TIME_", new Date());

                        if(glossary.getString("italianName") !=null){
                            multiLanguageBuildDao.updateItalianLbj(italianInfo);
                        }else{
                            italianInfo.put("id",chineseId);
                            multiLanguageBuildDao.addItalianLbj(italianInfo);
                        }
                    }
                    if(multilingualSign.equals("da")){
                        //丹文修改
                        JSONObject danishInfo = new JSONObject();
                        danishInfo.put("danishName", item.getString("multilingualText"));
                        danishInfo.put("chineseId", chineseId);
                        danishInfo.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                        danishInfo.put("UPDATE_TIME_", new Date());
                        if(glossary.getString("danishName") !=null){
                            multiLanguageBuildDao.updateDanishLbj(danishInfo);
                        }else{
                            danishInfo.put("id",chineseId);
                            multiLanguageBuildDao.addDanishLbj(danishInfo);
                        }
                    }
                    if(multilingualSign.equals("pl")){
                        //波文修改
                        JSONObject polishInfo = new JSONObject();
                        polishInfo.put("polishName", item.getString("multilingualText"));
                        polishInfo.put("chineseId", chineseId);
                        polishInfo.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                        polishInfo.put("UPDATE_TIME_", new Date());
                        if(glossary.getString("polishName") !=null){
                            multiLanguageBuildDao.updatePolishLbj(polishInfo);
                        }else{
                            polishInfo.put("id",chineseId);
                            multiLanguageBuildDao.addPolishLbj(polishInfo);
                        }
                    }
                    if(multilingualSign.equals("tr")){
                        //土文修改
                        JSONObject turkishInfo = new JSONObject();
                        turkishInfo.put("turkishName", item.getString("multilingualText"));
                        turkishInfo.put("chineseId", chineseId);
                        turkishInfo.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                        turkishInfo.put("UPDATE_TIME_", new Date());
                        if(glossary.getString("turkishName") !=null){
                            multiLanguageBuildDao.updateTurkishLbj(turkishInfo);
                        }else{
                            turkishInfo.put("id",chineseId);
                            multiLanguageBuildDao.addTurkishLbj(turkishInfo);
                        }
                    }
                    if(multilingualSign.equals("sl")){
                        //斯洛文尼亚语
                        JSONObject sloveniaInfo = new JSONObject();
                        sloveniaInfo.put("sloveniaName", item.getString("multilingualText"));
                        sloveniaInfo.put("chineseId", chineseId);
                        sloveniaInfo.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                        sloveniaInfo.put("UPDATE_TIME_", new Date());
                        if(glossary.getString("sloveniaName") !=null){
                            multiLanguageBuildDao.updateSloveniaLbj(sloveniaInfo);
                        }else{
                            sloveniaInfo.put("id",chineseId);
                            multiLanguageBuildDao.addSloveniaLbj(sloveniaInfo);
                        }
                    }
                    if(multilingualSign.equals("ro")){
                        //罗马尼亚语
                        JSONObject romaniaInfo = new JSONObject();
                        romaniaInfo.put("romaniaName", item.getString("multilingualText"));
                        romaniaInfo.put("chineseId", chineseId);
                        romaniaInfo.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                        romaniaInfo.put("UPDATE_TIME_", new Date());
                        if(glossary.getString("romaniaName") !=null){
                            multiLanguageBuildDao.updateRomaniaLbj(romaniaInfo);
                        }else{
                            romaniaInfo.put("id",chineseId);
                            multiLanguageBuildDao.addRomaniaLbj(romaniaInfo);
                        }
                    }

                }

            } else {
                JSONObject glossary = new JSONObject();
                Date now = new Date();
                glossary.put("id",item.getString("id"));
                glossary.put("materialCode", item.getString("materialCode"));
                glossary.put("originChinese", item.getString("originChinese"));
                glossary.put("chineseName", item.getString("chinese"));
                glossary.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                glossary.put("CREATE_TIME_", new Date());
                multiLanguageBuildDao.addChineseLbj(glossary);
                chineseId =item.getString("id");
                //英文修改
                JSONObject englishInfo = new JSONObject();
                englishInfo.put("id", IdUtil.getId());
                englishInfo.put("englishName", item.getString("en"));
                englishInfo.put("chineseId", chineseId);
                englishInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                englishInfo.put("CREATE_TIME_", new Date());
                multiLanguageBuildDao.addEnglishLbj(englishInfo);
                String multilingualSign = item.getString("multilingualSign");
                if (!multilingualSign.equalsIgnoreCase("0") ) {
                    if(multilingualSign.equalsIgnoreCase("ru")){
                        //俄文添加
                        JSONObject russianInfo = new JSONObject();
                        russianInfo.put("id", IdUtil.getId());
                        russianInfo.put("russianName", item.getString("multilingualText"));
                        russianInfo.put("chineseId", chineseId);
                        russianInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                        russianInfo.put("CREATE_TIME_", new Date());
                        multiLanguageBuildDao.addRussianLbj(russianInfo);
                    } else if(multilingualSign.equalsIgnoreCase("pt")){
                        //葡文添加
                        JSONObject multilInfo = new JSONObject();
                        multilInfo.put("id", IdUtil.getId());
                        multilInfo.put("portugueseName", item.getString("multilingualText"));
                        multilInfo.put("chineseId", chineseId);
                        multilInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                        multilInfo.put("CREATE_TIME_", new Date());
                        multiLanguageBuildDao.addPortugueseLbj(multilInfo);
                    } else if(multilingualSign.equalsIgnoreCase("de") ){
                        //德文添加
                        JSONObject germanyInfo = new JSONObject();
                        germanyInfo.put("id", IdUtil.getId());
                        germanyInfo.put("germanyName", item.getString("multilingualText"));
                        germanyInfo.put("chineseId", chineseId);
                        germanyInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                        germanyInfo.put("CREATE_TIME_", new Date());
                        multiLanguageBuildDao.addGermanyLbj(germanyInfo);
                    }else if(multilingualSign.equalsIgnoreCase("es")){
                        //西文添加
                        JSONObject spanishInfo = new JSONObject();
                        spanishInfo.put("id", IdUtil.getId());
                        spanishInfo.put("spanishName", item.getString("multilingualText"));
                        spanishInfo.put("chineseId", chineseId);
                        spanishInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                        spanishInfo.put("CREATE_TIME_", new Date());
                        multiLanguageBuildDao.addSpanishLbj(spanishInfo);
                    }else if(multilingualSign.equalsIgnoreCase("fr")){
                        //法文修改
                        JSONObject frenchInfo = new JSONObject();
                        frenchInfo.put("id", IdUtil.getId());
                        frenchInfo.put("frenchName", item.getString("multilingualText"));
                        frenchInfo.put("chineseId", chineseId);
                        frenchInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                        frenchInfo.put("CREATE_TIME_", new Date());
                        multiLanguageBuildDao.addFrenchLbj(frenchInfo);
                    }else if(multilingualSign.equalsIgnoreCase("it")){
                        //意文修改
                        JSONObject italianInfo = new JSONObject();
                        italianInfo.put("id", IdUtil.getId());
                        italianInfo.put("italianName", item.getString("multilingualText"));
                        italianInfo.put("chineseId", chineseId);
                        italianInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                        italianInfo.put("CREATE_TIME_", new Date());
                        multiLanguageBuildDao.addItalianLbj(italianInfo);
                    }else if(multilingualSign.equalsIgnoreCase("da")){
                        //丹文修改
                        JSONObject danishInfo = new JSONObject();
                        danishInfo.put("id", IdUtil.getId());
                        danishInfo.put("danishName", item.getString("multilingualText"));
                        danishInfo.put("chineseId", chineseId);
                        danishInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                        danishInfo.put("CREATE_TIME_", new Date());
                        multiLanguageBuildDao.addDanishLbj(danishInfo);
                    }else if(multilingualSign.equalsIgnoreCase("pl")){
                        //波文修改
                        JSONObject polishInfo = new JSONObject();
                        polishInfo.put("id", IdUtil.getId());
                        polishInfo.put("polishName", item.getString("multilingualText"));
                        polishInfo.put("chineseId", chineseId);
                        polishInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                        polishInfo.put("CREATE_TIME_", new Date());
                        multiLanguageBuildDao.addPolishLbj(polishInfo);
                    }else if(multilingualSign.equalsIgnoreCase("tr")){
                        //土文修改
                        JSONObject turkishInfo = new JSONObject();
                        turkishInfo.put("id", IdUtil.getId());
                        turkishInfo.put("turkishName", item.getString("multilingualText"));
                        turkishInfo.put("chineseId", chineseId);
                        turkishInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                        turkishInfo.put("CREATE_TIME_", new Date());
                        multiLanguageBuildDao.addTurkishLbj(turkishInfo);
                    } else if(multilingualSign.equalsIgnoreCase("sl")){
                        //斯洛文尼亚语
                        JSONObject sloveniaInfo = new JSONObject();
                        sloveniaInfo.put("id", IdUtil.getId());
                        sloveniaInfo.put("sloveniaName", item.getString("multilingualText"));
                        sloveniaInfo.put("chineseId", chineseId);
                        sloveniaInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                        sloveniaInfo.put("CREATE_TIME_", new Date());
                        multiLanguageBuildDao.addSloveniaLbj(sloveniaInfo);
                    } else if(multilingualSign.equalsIgnoreCase("ro")){
                        //罗马尼亚语
                        JSONObject romaniaInfo = new JSONObject();
                        romaniaInfo.put("id", IdUtil.getId());
                        romaniaInfo.put("romaniaName", item.getString("multilingualText"));
                        romaniaInfo.put("chineseId", chineseId);
                        romaniaInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                        romaniaInfo.put("CREATE_TIME_", new Date());
                        multiLanguageBuildDao.addRomaniaLbj(romaniaInfo);
                    }

                }

            }
        }
    }



    //导出列表
    public void exportList(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> params = new HashMap<>();
        getListParams(params, request);
        params.remove("startIndex");
        params.remove("pageSize");
        List<Map<String, Object>> businessList = multiLanguageBuildDao.dataListQuery(params);
        for (Map<String, Object> business : businessList) {
            if (business.get("CREATE_TIME_") != null) {
                business.put("CREATE_TIME_", DateUtil.formatDate((Date) business.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        xcmgProjectManager.setTaskCurrentUser(businessList);
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "零件图册缺词补充流程明细";
        String excelName = nowDate + title;
        String[] fieldNames = {"设计型号", "发起人", "中文审核人", "英文审核人", "多语言翻译人", "当前处理人", "当前流程任务"};
        String[] fieldCodes = {"designModel", "applyUser", "chReviewer", "enReviewer", "multilingualReviewer", "currentProcessUser", "currentProcessTask"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(businessList, fieldNames, fieldCodes, excelName);
        //输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }
}

