package com.redxun.bjjszc.core.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import com.redxun.bjjszc.core.dao.BjqdDao;
import com.redxun.core.excel.ExcelReaderUtil;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.environment.core.service.OilService;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

import groovy.util.logging.Slf4j;

@Service
@Slf4j
public class BjqdService {
    private Logger logger = LogManager.getLogger(BjqdService.class);

    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private BjqdDao bjqdDao;

    public JsonPageResult<?> queryBjqdList(HttpServletRequest request, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
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
        if (doPage) {
            rdmZhglUtil.addPage(request, params);
        }
        List<JSONObject> bjqdList = bjqdDao.queryBjqdList(params);
        for (JSONObject bjqd : bjqdList) {
            if (bjqd.get("CREATE_TIME_") != null) {
                bjqd.put("CREATE_TIME_", DateUtil.formatDate((Date)bjqd.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        result.setData(bjqdList);
        if (doPage) {
            int countBjqdDataList = bjqdDao.countBjqd(params);
            result.setTotal(countBjqdDataList);
        } else {
            result.setTotal(bjqdList.size());
        }
        return result;
    }

    public JsonPageResult<?> getBjqdDetail(HttpServletRequest request, String id) {
        JsonPageResult result = new JsonPageResult(true);
        List<JSONObject> bjqdList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("projectId", id);
        rdmZhglUtil.addPage(request, param);
        bjqdList = bjqdDao.queryBjqdFlow(param);
        result.setData(bjqdList);
        int totalSize = bjqdDao.countBjqdById(param);
        result.setTotal(totalSize);
        return result;
    }

    public JsonPageResult<?> queryBjqdFlowList(HttpServletRequest request, boolean doPage) {
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
        if (doPage) {
            rdmZhglUtil.addPage(request, params);
        }
        params.put("currentUserId", ContextUtil.getCurrentUserId());
        List<JSONObject> bjqdFlowList = bjqdDao.queryBjqdFlowList(params);
        for (JSONObject bjqd : bjqdFlowList) {
            if (bjqd.get("CREATE_TIME_") != null) {
                bjqd.put("CREATE_TIME_", DateUtil.formatDate((Date)bjqd.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        setTaskCurrentUser(bjqdFlowList);
        List<JSONObject> finalList = filterList(bjqdFlowList);
        result.setData(finalList);
        // int countBjqdFlowList = bjqdDao.countBjqdFlowList(params);
        int countBjqdFlowList = finalList.size();
        result.setTotal(countBjqdFlowList);
        return result;
    }

    public List<JSONObject> filterList(List<JSONObject> list) {
        List<JSONObject> result = new ArrayList<JSONObject>();
        if (list == null || list.isEmpty()) {
            return result;
        }
        // 管理员查看所有的包括草稿数据
        if ("admin".equalsIgnoreCase(ContextUtil.getCurrentUser().getUserNo())) {
            return list;
        }
        // 当前用户id
        String currentUserId = ContextUtil.getCurrentUserId();
        // 依次过滤每个数据
        for (JSONObject one : list) {
            // 创建人
            if (one.get("CREATE_BY_") != null && one.get("CREATE_BY_").toString().equals(currentUserId)) {
                result.add(one);
                continue;
            }
            // 如果是草稿状态，后面角色跳过，都不可见
            if (one.get("status") == null || "DRAFTED".equalsIgnoreCase(one.get("status").toString())) {
                continue;
            }
            // 任务处理人
            if (one.get("currentProcessUserId") != null
                && one.get("currentProcessUserId").toString().contains(currentUserId)) {
                result.add(one);
                continue;
            }
            // 历史数据处理人
            String userIds = bjqdDao.queryUserIdsById(one.getString("id"));
            if (StringUtils.isNotEmpty(userIds) && userIds.contains(currentUserId)) {
                result.add(one);
                continue;
            }
        }

        return result;
    }

    public void setTaskCurrentUser(List<JSONObject> objList) {
        Map<String, Map<String, Object>> taskId2Pro = new HashMap<>();
        for (JSONObject onePro : objList) {
            if (onePro.get("taskId") != null && StringUtils.isNotBlank(onePro.getString("taskId"))) {
                taskId2Pro.put(onePro.getString("taskId"), onePro);
            }
        }
        if (taskId2Pro.isEmpty()) {
            return;
        }
        Map<String, Object> queryTaskExecutors = new HashMap<>();
        queryTaskExecutors.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        queryTaskExecutors.put("taskIds", new ArrayList<String>(taskId2Pro.keySet()));
        List<Map<String, String>> task2Executors = xcmgProjectOtherDao.queryTaskExecutorsByTaskIds(queryTaskExecutors);
        if (task2Executors == null || task2Executors.isEmpty()) {
            logger.warn("can't find task executors");
            return;
        }
        for (Map<String, String> oneTaskExecutor : task2Executors) {
            String taskId = oneTaskExecutor.get("taskId");
            String executorName = oneTaskExecutor.get("currentProcessUser");
            String executorId = oneTaskExecutor.get("currentProcessUserId");
            Map<String, Object> needPutPro = taskId2Pro.get(taskId);
            if (needPutPro.get("currentProcessUser") != null
                && StringUtils.isNotBlank(needPutPro.get("currentProcessUser").toString())) {
                executorName += "," + needPutPro.get("currentProcessUser").toString();
                executorId += "," + needPutPro.get("currentProcessUserId").toString();
            }
            needPutPro.put("currentProcessUser", executorName);
            needPutPro.put("currentProcessUserId", executorId);
        }
    }

    public ResponseEntity<byte[]> importTemplateDownload() {
        try {
            String fileName = "备件供应形式清单导入模板.xlsx";
            // 创建文件实例
            File file = new File(OilService.class.getClassLoader().getResource("templates/bjqd/" + fileName).toURI());
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

    public void importBjqd(JSONObject result, HttpServletRequest request) {
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
            Sheet sheet = wb.getSheet("Sheet0");
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
            List<JSONObject> itemList = new ArrayList<>();
            for (int i = 1; i < rowNum; i++) {
                Row row = sheet.getRow(i);
                JSONObject rowParse = generateDataFromRow(row, itemList, titleList);
                if (!rowParse.getBoolean("result")) {
                    result.put("message", "数据导入失败，第" + (i + 1) + "行数据错误：" + rowParse.getString("message"));
                    return;
                }
            }
            // 物料编码重复性校验
            List<String> matIdInDb = bjqdDao.queryAllWlId();
            for (int i = 0; i < itemList.size(); i++) {
                JSONObject one = itemList.get(i);
                if (matIdInDb.contains(one.getString("wlId"))) {
                    result.put("message", "数据导入失败，第" + (i + 1) + "行数据错误：物料编码 “" + one.getString("wlId") + "” 已存在");
                    return;
                }
            }
            // 批量插入数据
            List<JSONObject> temp = new ArrayList<>();
            for (JSONObject oneData : itemList) {
                temp.add(oneData);
                if (temp.size() % 50 == 0) {
                    bjqdDao.batchInsertBjqd(temp);
                    temp.clear();
                }
            }
            if (!temp.isEmpty()) {
                bjqdDao.batchInsertBjqd(temp);
                temp.clear();
            }
            result.put("message", "数据导入成功！");
        } catch (Exception e) {
            logger.error("Exception in importBjqd", e);
            result.put("message", "数据导入失败，系统异常！");
        }
    }

    // 总清单导入【服务工程所】
    private JSONObject generateDataFromRow(Row row, List<JSONObject> itemList, List<String> titleList) {
        JSONObject oneRowCheck = new JSONObject();
        oneRowCheck.put("result", false);
        JSONObject oneRowMap = new JSONObject();
        DataFormatter dataFormatter = new DataFormatter();
        for (int i = 0; i < titleList.size(); i++) {
            String title = titleList.get(i);
            title = title.replaceAll(" ", "");
            Cell cell = row.getCell(i);
            String cellValue = null;
            if (cell != null) {
                cellValue = StringUtils.trim(dataFormatter.formatCellValue(cell));
            }
            switch (title) {
                case "总成物料编码":
                    oneRowMap.put("zcId", cellValue);
                    break;
                case "总成物料描述":
                    oneRowMap.put("zcName", cellValue);
                    break;
                case "物料类型":
                    oneRowMap.put("wlType", cellValue);
                    break;
                case "物料编码":
                    oneRowMap.put("wlId", cellValue);
                    break;
                case "物料描述":
                    oneRowMap.put("wlName", cellValue);
                    break;
                case "供应商":
                    oneRowMap.put("gys", cellValue);
                    break;
                default:
                    break;
            }
        }
        oneRowMap.put("id", IdUtil.getId());
        oneRowMap.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        oneRowMap.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        itemList.add(oneRowMap);
        oneRowCheck.put("result", true);
        return oneRowCheck;
    }

    // 流程中导入“可购买属性”
    public void importBjqdFlow(JSONObject result, HttpServletRequest request) {
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
            Sheet sheet = wb.getSheet("Sheet0");
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
            String projectId = RequestUtil.getString(request, "projectId");
            // 解析验证数据部分，任何一行的任何一项不满足条件，则直接返回失败
            List<JSONObject> itemList = new ArrayList<>();
            for (int i = 1; i < rowNum; i++) {
                Row row = sheet.getRow(i);
                JSONObject rowParse = generateDataFlowFromRow(row, itemList, titleList, projectId);
                if (!rowParse.getBoolean("result")) {
                    result.put("message", "数据导入失败，第" + (i + 1) + "行数据错误：" + rowParse.getString("message"));
                    return;
                }
            }
            // 批量导入更新数据【只更新projectId为空的或者等于自己这个projectId的】
            List<JSONObject> temp = new ArrayList<>();
            for (JSONObject oneData : itemList) {
                temp.add(oneData);
                if (temp.size() % 50 == 0) {
                    bjqdDao.batchUpdateBjqd(temp);
                    temp.clear();
                }
            }
            if (!temp.isEmpty()) {
                bjqdDao.batchUpdateBjqd(temp);
                temp.clear();
            }

            result.put("message", "数据导入成功！");
        } catch (Exception e) {
            logger.error("Exception in importProduct", e);
            result.put("message", "数据导入失败，系统异常！");
        }
    }

//    可购买属性
    private JSONObject generateDataFlowFromRow(Row row, List<JSONObject> itemList, List<String> titleList,
        String projectId) {
        JSONObject oneRowCheck = new JSONObject();
        oneRowCheck.put("result", false);
        JSONObject oneRowMap = new JSONObject();
        DataFormatter dataFormatter = new DataFormatter();
        for (int i = 0; i < titleList.size(); i++) {
            String title = titleList.get(i);
            title = title.replaceAll(" ", "");
            Cell cell = row.getCell(i);
            String cellValue = null;
            if (cell != null) {
                cellValue = StringUtils.trim(dataFormatter.formatCellValue(cell));
            }
            switch (title) {
                case "物料编码":
                    oneRowMap.put("wlId", cellValue);
                    break;
                case "供应商":
                    oneRowMap.put("gys", cellValue);
                    break;
                case "可购买属性":
                    oneRowMap.put("kgmsx", cellValue);
                    break;
                case "价格":
                    oneRowMap.put("price", cellValue);
                    break;
                case "备注":
                    oneRowMap.put("remark", cellValue);
                    break;
                default:
                    break;
            }
        }
        oneRowMap.put("pms", "");
        oneRowMap.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        oneRowMap.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        oneRowMap.put("inputTime", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd"));
        oneRowMap.put("projectId", projectId);
        itemList.add(oneRowMap);
        oneRowCheck.put("result", true);
        return oneRowCheck;
    }

    public void importBjqdFlowPMS(JSONObject result, HttpServletRequest request) {
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
            Sheet sheet = wb.getSheet("Sheet0");
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
            String projectId = RequestUtil.getString(request, "projectId");
            // 解析验证数据部分，任何一行的任何一项不满足条件，则直接返回失败
            List<JSONObject> itemList = new ArrayList<>();
            for (int i = 1; i < rowNum; i++) {
                Row row = sheet.getRow(i);
                JSONObject rowParse = generateDataFlowPMSFromRow(row, itemList, titleList, projectId);
                if (!rowParse.getBoolean("result")) {
                    result.put("message", "数据导入失败，第" + (i + 1) + "行数据错误：" + rowParse.getString("message"));
                    return;
                }
            }

            // 批量导入更新数据【只更新projectId等于自己这个projectId的】
            List<JSONObject> temp = new ArrayList<>();
            for (JSONObject oneData : itemList) {
                temp.add(oneData);
                if (temp.size() % 50 == 0) {
                    bjqdDao.batchUpdateBjqdPMS(temp);
                    temp.clear();
                }
            }
            if (!temp.isEmpty()) {
                bjqdDao.batchUpdateBjqdPMS(temp);
                temp.clear();
            }

            result.put("message", "数据导入成功！");
        } catch (Exception e) {
            logger.error("Exception in importBjqdFlowPMS", e);
            result.put("message", "数据导入失败，系统异常！");
        }
    }

    // PMS价格维护确认解析
    private JSONObject generateDataFlowPMSFromRow(Row row, List<JSONObject> itemList, List<String> titleList,
        String projectId) {
        JSONObject oneRowCheck = new JSONObject();
        oneRowCheck.put("result", false);
        JSONObject oneRowMap = new JSONObject();
        DataFormatter dataFormatter = new DataFormatter();
        for (int i = 0; i < titleList.size(); i++) {
            String title = titleList.get(i);
            title = title.replaceAll(" ", "");
            Cell cell = row.getCell(i);
            String cellValue = null;
            if (cell != null) {
                cellValue = StringUtils.trim(dataFormatter.formatCellValue(cell));
            }
            switch (title) {
                case "物料编码":
                    oneRowMap.put("wlId", cellValue);
                    break;
                case "PMS价格维护":
                    oneRowMap.put("pms", cellValue);
                    break;
                case "备注":
                    oneRowMap.put("remark", cellValue);
                    break;
                default:
                    break;
            }
        }
        oneRowMap.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        oneRowMap.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        oneRowMap.put("projectId", projectId);
        itemList.add(oneRowMap);
        oneRowCheck.put("result", true);
        return oneRowCheck;
    }

    public void createBjqd(JSONObject formData) {
        formData.put("id", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        bjqdDao.createBjqd(formData);
    }

    /**
     * 总清单中的导出，不导出价格
     * 
     * @param request
     * @param response
     */
    public void exportBjqdList(HttpServletRequest request, HttpServletResponse response) {
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
        int totalSize = bjqdDao.countBjqd(params);
        int pageSize = (int)Math.ceil(totalSize / 5000.0);

        try {
            List<XSSFWorkbook> wbList = new ArrayList<>();
            List<String> excelNameList = new ArrayList<>();
            for (int pageIndex = 0; pageIndex < pageSize; pageIndex++) {
                params.put("startIndex", 5000 * pageIndex);
                params.put("pageSize", 5000);
                List<JSONObject> listData = bjqdDao.queryBjqd(params);
                String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
                String title = "备件供应形式确认清单";
                String excelName = nowDate + title + "_" + pageIndex + ".xlsx";
                excelNameList.add(excelName);
                String[] fieldNames = {"总成物料编码", "总成物料描述", "物料类型", "物料编码", "物料描述", "供应商", "可购买属性", "价格", "PMS价格维护"};
                String[] fieldCodes = {"zcId", "zcName", "wlType", "wlId", "wlName", "gys", "kgmsx", "blank", "pms"};
                // 不带标题导出，作为同样式的导入模板
                XSSFWorkbook wbObj = ExcelUtils.exportXlsxExcelWBWithoutTitle(listData, fieldNames, fieldCodes);
                wbList.add(wbObj);
            }
            if (excelNameList.size() == 1) {
                ExcelUtils.writeXlsxWorkBook2Stream(
                    excelNameList.get(0).substring(0, excelNameList.get(0).indexOf(".")), wbList.get(0), response);
            } else if (excelNameList.size() > 1) {
                // 创建Zip文件对象
                ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream());
                response.setContentType("application/octet-stream");
                response.setHeader("Content-Disposition",
                    "attachment;filename=" + java.net.URLEncoder.encode("备件供应形式确认清单.zip", "UTF-8"));
                response.setStatus(HttpServletResponse.SC_OK);
                for (int index = 0; index < wbList.size(); index++) {
                    XSSFWorkbook wbObj = wbList.get(index);
                    // 将工作簿写入字节数组
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    wbObj.write(outputStream);
                    byte[] bytes = outputStream.toByteArray();
                    // 将Excel文件写入Zip文件
                    ZipEntry entry1 = new ZipEntry(excelNameList.get(index));
                    zipOutputStream.putNextEntry(entry1);
                    zipOutputStream.write(bytes);
                    zipOutputStream.closeEntry();
                }
                // 关闭Zip文件输出流
                zipOutputStream.close();
            }

        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            logger.error("系统异常", e);
        }

    }

    /**
     * 某一个流程表单中的物料导出，包含价格
     * 
     * @param request
     * @param response
     */
    public void exportBjqdFlowList(HttpServletRequest request, HttpServletResponse response) {
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
        int totalSize = bjqdDao.countBjqdById(params);
        int pageSize = (int)Math.ceil(totalSize / 5000.0);

        try {
            List<XSSFWorkbook> wbList = new ArrayList<>();
            List<String> excelNameList = new ArrayList<>();
            for (int pageIndex = 0; pageIndex < pageSize; pageIndex++) {
                params.put("startIndex", 5000 * pageIndex);
                params.put("pageSize", 5000);
                List<JSONObject> listData = bjqdDao.queryBjqdById(params);
                String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
                String title = "备件供应形式确认清单";
                String excelName = nowDate + title + "_" + pageIndex + ".xlsx";
                excelNameList.add(excelName);
                String[] fieldNames =
                    {"总成物料编码", "总成物料描述", "物料类型", "物料编码", "物料描述", "供应商", "可购买属性", "价格", "PMS价格维护", "备注"};
                String[] fieldCodes =
                    {"zcId", "zcName", "wlType", "wlId", "wlName", "gys", "kgmsx", "price", "pms", "remark"};
                // 不带标题导出，作为同样式的导入模板
                XSSFWorkbook wbObj = ExcelUtils.exportXlsxExcelWBWithoutTitle(listData, fieldNames, fieldCodes);
                wbList.add(wbObj);
            }
            if (excelNameList.size() == 1) {
                ExcelUtils.writeXlsxWorkBook2Stream(
                    excelNameList.get(0).substring(0, excelNameList.get(0).indexOf(".")), wbList.get(0), response);
            } else if (excelNameList.size() > 1) {
                // 创建Zip文件对象
                ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream());
                response.setContentType("application/octet-stream");
                response.setHeader("Content-Disposition",
                    "attachment;filename=" + java.net.URLEncoder.encode("备件供应形式确认清单.zip", "UTF-8"));
                response.setStatus(HttpServletResponse.SC_OK);
                for (int index = 0; index < wbList.size(); index++) {
                    XSSFWorkbook wbObj = wbList.get(index);
                    // 将工作簿写入字节数组
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    wbObj.write(outputStream);
                    byte[] bytes = outputStream.toByteArray();
                    // 将Excel文件写入Zip文件
                    ZipEntry entry1 = new ZipEntry(excelNameList.get(index));
                    zipOutputStream.putNextEntry(entry1);
                    zipOutputStream.write(bytes);
                    zipOutputStream.closeEntry();
                }
                // 关闭Zip文件输出流
                zipOutputStream.close();
            }

        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            logger.error("系统异常", e);
        }

    }

    // 删除关联的所有信息
    public JsonResult delBjqd(String[] idArr) {
        for (int i = 0; i < idArr.length; i++) {
            String id = idArr[i];
            // 删除申报流程数据
            bjqdDao.delBjqd(id);
        }
        return new JsonResult(true, "成功删除!");
    }

    // 还原关联的所有信息
    public JsonResult delBjqdFlow(String[] idArr) {
        for (int i = 0; i < idArr.length; i++) {
            String id = idArr[i];
            // 还原数据
            JSONObject param = new JSONObject();
            param.put("id", id);
            param.put("projectId", null);
            param.put("kgmsx", null);
            param.put("price", null);
            param.put("pms", null);
            bjqdDao.updateBjqdFlow(param);
        }
        return new JsonResult(true, "成功删除!");
    }

    public JSONObject getBjqdCompletionData(HttpServletRequest request, HttpServletResponse response,
        String postDataStr) {
        // dataset.dimensions
        List<String> dimensions = new ArrayList<>();
        dimensions.add("统计数据");
        dimensions.add("生产装配件");
        dimensions.add("外购件子件");
        // dataset.source
        List<JSONObject> sources = new ArrayList<>();
        JSONObject Statistics = new JSONObject(new LinkedHashMap());
        // 根据字典配置的销售区域进行各柱名称和数据初始化
        JSONObject source;
        JSONObject param;
        // 初始化数据
        // 1.历史可购买属性（“是"和"否"都做统计，空和null不做统计）
        source = new JSONObject();
        param = new JSONObject();

        source.put("统计数据", "历史可购买属性");
        param.put("wlType", "生产装配件");
        source.put("生产装配件", bjqdDao.countKgmsx(param));
        param.put("wlType", "外购件子件");
        source.put("外购件子件", bjqdDao.countKgmsx(param));
        Statistics.put("历史可购买属性", source);
        // 2.历史PMS价格维护（仅“是”作为统计数据）
        source = new JSONObject();
        source.put("统计数据", "历史PMS价格维护");
        param.put("wlType", "生产装配件");
        source.put("生产装配件", bjqdDao.countKMS(param));
        param.put("wlType", "外购件子件");
        source.put("外购件子件", bjqdDao.countKMS(param));
        Statistics.put("历史PMS价格维护", source);
        // 3.4周可购买属性
        Calendar calendar4 = Calendar.getInstance();
        calendar4.add(Calendar.DAY_OF_MONTH, -28);
        String create_startTime = new SimpleDateFormat("yyyy-MM-dd").format(calendar4.getTime());
        source = new JSONObject();
        source.put("统计数据", "4周可购买属性");
        param.put("wlType", "生产装配件");
        param.put("create_startTime", create_startTime);
        source.put("生产装配件", bjqdDao.countKgmsx(param));
        param.put("wlType", "外购件子件");
        param.put("create_startTime", create_startTime);
        source.put("外购件子件", bjqdDao.countKgmsx(param));
        Statistics.put("4周可购买属性", source);
        // 4.6周PMS价格维护数
        Calendar calendar6 = Calendar.getInstance();
        calendar6.add(Calendar.DAY_OF_MONTH, -42);
        create_startTime = new SimpleDateFormat("yyyy-MM-dd").format(calendar6.getTime());
        source = new JSONObject();
        source.put("统计数据", "6周PMS价格维护数");
        param.put("wlType", "生产装配件");
        param.put("create_startTime", create_startTime);
        source.put("生产装配件", bjqdDao.countKMS(param));
        param.put("wlType", "外购件子件");
        param.put("create_startTime", create_startTime);
        source.put("外购件子件", bjqdDao.countKMS(param));
        Statistics.put("6周PMS价格维护数", source);
        // 5.合计(全表数据量统计)
        param.clear();
        source = new JSONObject();
        source.put("统计数据", "合计");
        param.put("wlType", "生产装配件");
        source.put("生产装配件", bjqdDao.countAll(param));
        param.put("wlType", "外购件子件");
        source.put("外购件子件", bjqdDao.countAll(param));
        Statistics.put("合计", source);

        // 数据整合放入
        for (Map.Entry entry : Statistics.entrySet()) {
            sources.add((JSONObject)entry.getValue());
        }
        JSONObject resultJson = new JSONObject();
        resultJson.put("dimensions", dimensions);
        resultJson.put("source", sources);
        return resultJson;
    }

    // ..数量类统计-通用/////////////////////////////////////////////////////////////////
    public JSONObject getAmount(HttpServletRequest request, HttpServletResponse response, String postDataStr) {
        JSONObject resultJson = new JSONObject();
        resultJson.put("bjqdAmount", bjqdDao.countKgmsxAmount());
        resultJson.put("bjqdPMSAmount", bjqdDao.countKMSAmount());
        return resultJson;
    }

    public JSONObject getBjqdCompletionLineData(HttpServletRequest request, HttpServletResponse response,
        String postDataStr) {
        List<String> dimensions = new ArrayList<>();
        dimensions.add("时间");
        dimensions.add("需求数量");
        dimensions.add("可购买属性确认数量");
        dimensions.add("PMS价格维护数量");
        // dataset.source
        List<JSONObject> sources = new ArrayList<>();
        JSONObject Statistics = new JSONObject(new LinkedHashMap());
        // 根据字典配置的销售区域进行各柱名称和数据初始化
        // JSONObject source;
        // 初始化数据
        // source = new JSONObject();

        List<JSONObject> listData = bjqdDao.countAllLine();
        for (JSONObject one : listData) {
            JSONObject source = new JSONObject();
            JSONObject params = new JSONObject();
            params.put("time", one.getString("time"));
            source.put("时间", one.getString("time"));
            source.put("需求数量", one.getString("countAll"));
            source.put("可购买属性确认数量", bjqdDao.countKgmsxLine(params));
            source.put("PMS价格维护数量", bjqdDao.countKMSLine(params));
            Statistics.put(one.getString("time"), source);
        }
        // 数据整合放入
        for (Map.Entry entry : Statistics.entrySet()) {
            sources.add((JSONObject)entry.getValue());
        }
        JSONObject resultJson = new JSONObject();
        resultJson.put("dimensions", dimensions);
        resultJson.put("source", sources);
        return resultJson;
    }
}
