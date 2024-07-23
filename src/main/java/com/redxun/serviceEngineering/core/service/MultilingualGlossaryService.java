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
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.excel.ExcelReaderUtil;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.ExcelUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.materielextend.core.service.MaterielService;
import com.redxun.rdmZhgl.core.service.PatentInterpretationService;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.serviceEngineering.core.dao.MultilingualGlossaryDao;
import com.redxun.sys.core.util.SysPropertiesUtil;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

@Service
public class MultilingualGlossaryService {
    private static Logger logger = LoggerFactory.getLogger(PatentInterpretationService.class);
    @Autowired
    MultilingualGlossaryDao multilingualGlossaryDao;
    @Autowired
    private BpmInstManager bpmInstManager;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;

    // ..
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        getListParams(params, request);
        List<Map<String, Object>> businessList = multilingualGlossaryDao.dataListQuery(params);
        for (Map<String, Object> business : businessList) {
            if (business.get("CREATE_TIME_") != null) {
                business.put("CREATE_TIME_", DateUtil.formatDate((Date)business.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        // 查询当前处理人--非会签
        xcmgProjectManager.setTaskCurrentUser(businessList);
        // 查询当前处理人--会签
        // rdmZhglUtil.setTaskInfo2Data(businessList, ContextUtil.getCurrentUserId());
        int businessListCount = multilingualGlossaryDao.countDataListQuery(params);
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
    public JsonResult deleteBusiness(String[] ids, String[] instIds) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> businessIds = Arrays.asList(ids);
        Map<String, Object> param = new HashMap<>();
        param.put("businessIds", businessIds);
        multilingualGlossaryDao.deleteBusiness(param);
        multilingualGlossaryDao.deleteItem(param);
        for (String oneInstId : instIds) {
            // 删除实例,不是同步删除，但是总量是能一对一的
            bpmInstManager.deleteCascade(oneInstId, "");
        }
        return result;
    }

    // ..
    public JSONObject getDetail(String businessId) {
        JSONObject jsonObject = multilingualGlossaryDao.queryDetailById(businessId);
        if (jsonObject == null) {
            return new JSONObject();
        }
        return jsonObject;
    }

    // ..
    public List<JSONObject> getItemList(String businessId) {
        List<JSONObject> itemList = multilingualGlossaryDao.queryItemList(businessId);
        return itemList;
    }

    // ..
    public void createBusiness(JSONObject formData) {
        formData.put("id", IdUtil.getId());
        formData.put("applyUserId", ContextUtil.getCurrentUserId());
        formData.put("applyUser", ContextUtil.getCurrentUser().getFullname());
        formData.put("applyDate", DateUtil.formatDate(new Date(), DateUtil.DATE_FORMAT_YMD));
        // @lwgkiller:此处是因为(草稿状态和空状态)无节点，提交后首节点会跳过，因此默认将首节点（编制中）的编号进行初始化写入
        formData.put("businessStatus", "A-editing");
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        formData.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        multilingualGlossaryDao.insertBusiness(formData);
        // 如果有明细的变化则处理
        if (formData.getJSONArray("itemChangeData") != null && !formData.getJSONArray("itemChangeData").isEmpty()) {
            this.saveOrUpdateItems(formData.getJSONArray("itemChangeData"), formData.getString("id"));
        }
        // 语言匹配处理
        this.glossaryApplyitemsHitTagMatching(formData);
    }

    // ..
    public void updateBusiness(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        multilingualGlossaryDao.updateBusiness(formData);
        // 如果有明细的变化则处理
        if (formData.getJSONArray("itemChangeData") != null && !formData.getJSONArray("itemChangeData").isEmpty()) {
            this.saveOrUpdateItems(formData.getJSONArray("itemChangeData"), formData.getString("id"));
        }
        // 语言匹配处理
        this.glossaryApplyitemsHitTagMatching(formData);
    }

    // ..
    public void saveOrUpdateItems(JSONArray changeGridDataJson, String mainId) {
        for (int i = 0; i < changeGridDataJson.size(); i++) {
            JSONObject oneObject = changeGridDataJson.getJSONObject(i);
            String state = oneObject.getString("_state");
            String id = oneObject.getString("id");
            oneObject.put("mainId", mainId);
            if ("added".equals(state) || StringUtils.isBlank(id)) {
                // 新增
                oneObject.put("id", IdUtil.getId());
                oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                oneObject.put("CREATE_TIME_", new Date());
                oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                oneObject.put("UPDATE_TIME_", new Date());
                oneObject.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
                multilingualGlossaryDao.insertItem(oneObject);
            } else if ("modified".equals(state)) {
                oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                oneObject.put("UPDATE_TIME_", new Date());
                multilingualGlossaryDao.updateItem(oneObject);
            } else if ("removed".equals(state)) {
                JSONObject params = new JSONObject();
                params.put("id", id);
                multilingualGlossaryDao.deleteItem(params);
            }
        }
    }

    // ..
    public void createGlossary(JSONObject glossaryData) {
        glossaryData.put("id", IdUtil.getId());
        glossaryData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        glossaryData.put("CREATE_TIME_", new Date());
        glossaryData.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        multilingualGlossaryDao.insertGlossary(glossaryData);
    }

    // ..
    public void updateGlossary(JSONObject glossaryData) {
        glossaryData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        glossaryData.put("UPDATE_TIME_", new Date());
        multilingualGlossaryDao.updateGlossary(glossaryData);
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
            String mainId = RequestUtil.getString(request, "mainId");
            for (int i = 0; i < itemList.size(); i++) {
                Map<String, Object> map = itemList.get(i);
                String json = JSON.toJSONString(map);
                JSONObject jsonObject = JSON.parseObject(json);
                jsonObject.put("mainId", mainId);
                multilingualGlossaryDao.insertItem(jsonObject);
            }
            // 语言匹配处理
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", mainId);
            this.glossaryApplyitemsHitTagMatching(jsonObject);
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
                    oneRowMap.put("chinese", cellValue);
                    break;
                case "英文":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "英文为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("en", cellValue);
                    break;
                case "语言":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "语言为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("multilingualKey", cellValue);
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

    // ..
    public ResponseEntity<byte[]> importTemplateDownload() {
        try {
            String fileName = "多语言术语库扩充导入模板.xlsx";
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
    public void exportItem(HttpServletRequest request, HttpServletResponse response) {
        String filterParams = request.getParameter("filter");
        JSONArray jsonArray = JSONArray.parseArray(filterParams);
        List<String> listids = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            listids.add(jsonArray.getJSONObject(i).getString("value"));
        }
        JSONObject param = new JSONObject();
        param.put("ids", listids);
        List<JSONObject> listData = multilingualGlossaryDao.queryItemListByIds(param);
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "术语明细";
        String excelName = nowDate + title;
        String[] fieldNames = {"物料编码", "中文", "英文", "多语言key", "多语言文本", "备注"};
        String[] fieldCodes = {"materialCode", "chinese", "en", "multilingualKey", "multilingualText", "remark"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(listData, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }

    // ..
    public void exportItemGss(HttpServletRequest request, HttpServletResponse response) {
        String filterParams = request.getParameter("filterGss");
        JSONArray jsonArray = JSONArray.parseArray(filterParams);
        List<String> listMaterialCodes = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            listMaterialCodes.add(jsonArray.getJSONObject(i).getString("value"));
        }
        JSONObject param = new JSONObject();
        param.put("materialCodes", listMaterialCodes);
        List<JSONObject> listData = multilingualGlossaryDao.queryGlossaryListByMaterialCodes(param);
        for (JSONObject jsonObject : listData) {
            jsonObject.put("factoryNo", SysPropertiesUtil.getGlobalProperty("factoryNo"));
            // 日期匹配处理
            this.glossaryDateTrasToGss("enUpdateTime", jsonObject);
            this.glossaryDateTrasToGss("ruUpdateTime", jsonObject);
            this.glossaryDateTrasToGss("ptUpdateTime", jsonObject);
            this.glossaryDateTrasToGss("deUpdateTime", jsonObject);
            this.glossaryDateTrasToGss("esUpdateTime", jsonObject);
            this.glossaryDateTrasToGss("frUpdateTime", jsonObject);
            this.glossaryDateTrasToGss("itUpdateTime", jsonObject);
            this.glossaryDateTrasToGss("daUpdateTime", jsonObject);
            this.glossaryDateTrasToGss("nlUpdateTime", jsonObject);
            this.glossaryDateTrasToGss("plUpdateTime", jsonObject);
            this.glossaryDateTrasToGss("trUpdateTime", jsonObject);
        }
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "术语明细-导GSS用";
        String excelName = nowDate + title;
        String[] fieldNames = {"zh-CN", "en", "ru", "pt", "de", "es", "fr", "it", "da", "nl", "pl", "tr", "工厂代码",
            "英文修改者", "英文修改时间", "俄文修改者", "俄文修改时间", "葡文修改者", "葡文修改时间", "德文修改者", "德文修改时间", "西文修改者", "西文修改时间", "法文修改者",
            "法文修改时间", "意文修改者", "意文修改时间", "丹文修改者", "丹文修改时间", "荷文修改者", "荷文修改时间", "波文修改者", "波文修改时间", "土文修改者", "土文修改时间"};
        String[] fieldCodes = {"chinese", "en", "ru", "pt", "de", "es", "fr", "it", "da", "nl", "pl", "tr", "factoryNo",
            "enUpdateBy", "enUpdateTime", "ruUpdateBy", "ruUpdateTime", "ptUpdateBy", "ptUpdateTime", "deUpdateBy",
            "deUpdateTime", "esUpdateBy", "esUpdateTime", "frUpdateBy", "frUpdateTime", "itUpdateBy", "itUpdateTime",
            "daUpdateBy", "daUpdateTime", "nlUpdateBy", "nlUpdateTime", "plUpdateBy", "plUpdateTime", "trUpdateBy",
            "trUpdateTime"};
        // HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(listData, fieldNames, fieldCodes, title);
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWBWithoutTitle(listData, fieldNames, fieldCodes);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }

    /**
     * 语言匹配处理,处理是否物料匹配,是否中文匹配等标记字段
     *
     * @param oneObject
     *            service_engineering_multilingualglossary_apply的行对象
     */
    private void glossaryApplyitemsHitTagMatching(JSONObject jsonObject) {
        List<JSONObject> itemList = this.getItemList(jsonObject.getString("id"));
        for (JSONObject item : itemList) {
            // 物料是否匹配
            JSONObject params = new JSONObject();
            params.put("materialCode",
                StringUtil.isEmpty(item.getString("materialCode")) ? "materialCode" : item.getString("materialCode"));
            List<Map<String, Object>> glossaryList = multilingualGlossaryDao.glossaryListQuery(params);
            if (glossaryList.size() > 0) {
                item.put("chineseMaterialHit", "true");
                item.put("chineseHit", glossaryList.get(0).get("chinese").toString());
                item.put("enHit", glossaryList.get(0).get("en").toString());
                multilingualGlossaryDao.updateItem(item);
            } else {
                params.clear();
                params.put("chinese",
                    StringUtil.isEmpty(item.getString("chinese")) ? "chinese" : item.getString("chinese"));
                glossaryList.clear();
                glossaryList = multilingualGlossaryDao.glossaryListQuery(params);
                if (glossaryList.size() > 0) {
                    item.put("chineseMaterialHit", "false");
                    item.put("chineseTextHit", "true");
                    item.put("chineseHit", glossaryList.get(0).get("chinese").toString());
                    item.put("enHit", glossaryList.get(0).get("en").toString());
                } else {
                    item.put("chineseMaterialHit", "false");
                    item.put("chineseTextHit", "false");
                    item.put("chineseHit", "");
                    item.put("enHit", "");
                }
                multilingualGlossaryDao.updateItem(item);
            }
        }
    }

    /**
     * 语言日期处理
     *
     * @param key
     *            处理那种语言的更新日期,service_engineering_multilingualglossary的字段名
     * @param jsonObject
     *            service_engineering_multilingualglossary的行对象
     */
    private void glossaryDateTrasToGss(String key, JSONObject jsonObject) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.M.d");
        jsonObject.put(key, jsonObject.getString(key) == null ? ""
            : simpleDateFormat.format(DateUtil.parseDate(jsonObject.getString(key))));
    }

    /**
     * 根据全对象里的明细对象,分别将语言库对应语种进行更新
     *
     * @param jsonObject
     *            service_engineering_multilingualglossary_apply的行对象
     */
    public void glossaryUpdateProcessing(JSONObject jsonObject) {
        List<JSONObject> itemList = this.getItemList(jsonObject.getString("id"));
        JSONObject params = new JSONObject();
        for (JSONObject item : itemList) {
            params.put("materialCode", item.getString("materialCode"));
            List<Map<String, Object>> glossaryList = multilingualGlossaryDao.glossaryListQuery(params);
            if (glossaryList.size() > 0) {
                JSONObject glossary = new JSONObject(glossaryList.get(0));
                Date now = new Date();
                glossary.put("chinese", item.getString("chinese"));
                glossary.put("chineseUpdateBy", item.getString("chineseUpdateBy"));
                glossary.put("chineseUpdateTime", DateUtil.formatDate(now, DateUtil.DATE_FORMAT_YMD));
                glossary.put("en", item.getString("en"));
                glossary.put("enUpdateBy", item.getString("enUpdateBy"));
                glossary.put("enUpdateTime", DateUtil.formatDate(now, DateUtil.DATE_FORMAT_YMD));
                String multilingualKey = item.getString("multilingualKey");
                if (StringUtil.isNotEmpty(multilingualKey)) {
                    glossary.put(multilingualKey, item.getString("multilingualText"));
                    glossary.put(multilingualKey + "UpdateBy", item.getString("multilingualUpdateBy"));
                    glossary.put(multilingualKey + "UpdateTime", DateUtil.formatDate(now, DateUtil.DATE_FORMAT_YMD));
                }
                this.updateGlossary(glossary);
            } else {
                JSONObject glossary = new JSONObject();
                Date now = new Date();
                glossary.put("materialCode", item.getString("materialCode"));
                glossary.put("chinese", item.getString("chinese"));
                glossary.put("chineseUpdateBy", item.getString("chineseUpdateBy"));
                glossary.put("chineseUpdateTime", DateUtil.formatDate(now, DateUtil.DATE_FORMAT_YMD));
                glossary.put("en", item.getString("en"));
                glossary.put("enUpdateBy", item.getString("enUpdateBy"));
                glossary.put("enUpdateTime", DateUtil.formatDate(now, DateUtil.DATE_FORMAT_YMD));
                String multilingualKey = item.getString("multilingualKey");
                if (StringUtil.isNotEmpty(multilingualKey)) {
                    glossary.put(multilingualKey, item.getString("multilingualText"));
                    glossary.put(multilingualKey + "UpdateBy", item.getString("multilingualUpdateBy"));
                    glossary.put(multilingualKey + "UpdateTime", DateUtil.formatDate(now, DateUtil.DATE_FORMAT_YMD));
                }
                this.createGlossary(glossary);
            }
        }
    }
}
