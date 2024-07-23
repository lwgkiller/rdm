package com.redxun.rdmZhgl.core.service;

import java.io.File;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSONArray;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.rdmZhgl.core.dao.NdkfjhBudgetDao;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.excel.ExcelReaderUtil;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.util.ExcelUtil;
import com.redxun.materielextend.core.service.MaterielService;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.standardManager.core.util.ResultUtil;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

/**
 * @author zhangzhen
 */
@Service
public class NdkfjhBudgetService {
    private static final Logger logger = LoggerFactory.getLogger(NdkfjhBudgetService.class);
    @Resource
    CommonInfoManager commonInfoManager;
    @Resource
    private NdkfjhBudgetDao ndkfjhBudgetDao;
    @Resource
    private CommonInfoDao commonInfoDao;

    public JsonPageResult<?> query(HttpServletRequest request) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            Map<String, Object> params = new HashMap<>(16);
            List<Map<String, Object>> list = new ArrayList<>();
            List<Map<String, Object>> totalList = new ArrayList<>();
            params = CommonFuns.getSearchParam(params, request, true);
            list = ndkfjhBudgetDao.query(params);
            params = new HashMap<>(16);
            params = CommonFuns.getSearchParam(params, request, false);
            totalList = ndkfjhBudgetDao.query(params);
            convertDate(list);
            // 返回结果
            result.setData(list);
            result.setTotal(totalList.size());
        } catch (Exception e) {
            logger.error("Exception in 查询异常", e);
        }
        return result;
    }

    public JSONObject add(JSONObject formDataJson) {
        JSONObject resultJson = new JSONObject();
        try {
            // 判断是否已经存在
            JSONObject obj = ndkfjhBudgetDao.getObjByInfo(formDataJson);
            if (obj != null) {
                return ResultUtil.result(false, "本年度已存在此项目预算！", "");
            }
            String id = IdUtil.getId();
            formDataJson.put("id", id);
            formDataJson.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            formDataJson.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            formDataJson.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            formDataJson.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            formDataJson.put("deptId", ContextUtil.getCurrentUser().getMainGroupId());
            resultJson.put("id", id);
            ndkfjhBudgetDao.addObject(formDataJson);
            if (StringUtils.isNotBlank(formDataJson.getString("SUB_product"))) {
                JSONArray productDataJson = JSONObject.parseArray(formDataJson.getString("SUB_product"));
                for (int i = 0; i < productDataJson.size(); i++) {
                    JSONObject oneObject = productDataJson.getJSONObject(i);
                    String state = oneObject.getString("_state");
                    String productId = oneObject.getString("id");
                    if ("added".equals(state) || StringUtils.isBlank(productId)) {
                        // 新增
                        oneObject.put("id", IdUtil.getId());
                        oneObject.put("mainId", id);
                        oneObject.put("reason", oneObject.getString("reason"));
                        oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                        oneObject.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                        oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                        oneObject.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                        if(oneObject.get("charge")!=null){
                            String deptId = commonInfoDao.getDeptIdByUserId(oneObject.getString("charge"));
                            oneObject.put("deptId", deptId);
                        }
                        ndkfjhBudgetDao.addBudgetDetail(oneObject);
                    } else if ("modified".equals(state)) {
                        oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                        oneObject.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                        if(oneObject.get("charge")!=null){
                            String deptId = commonInfoDao.getDeptIdByUserId(oneObject.getString("charge"));
                            oneObject.put("deptId", deptId);
                        }
                        ndkfjhBudgetDao.updateBudgetDetail(oneObject);
                    } else if ("removed".equals(state)) {
                        // 删除
                        ndkfjhBudgetDao.delBudgetDetail(oneObject.getString("id"));
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Exception in add 添加异常！", e);
            return ResultUtil.result(false, "添加异常！", "");
        }
        return ResultUtil.result(true, "新增成功", resultJson);
    }

    public JSONObject update(JSONObject formDataJson) {
        JSONObject resultJson = new JSONObject();
        try {
            JSONObject obj = ndkfjhBudgetDao.getObjByInfo(formDataJson);
            String mainId = formDataJson.getString("id");
            if (obj != null && !mainId.equals(obj.getString("id"))) {
                return ResultUtil.result(false, "本年度已存在此项目预算！", "");
            }
            formDataJson.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            formDataJson.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            ndkfjhBudgetDao.updateObject(formDataJson);
            if (StringUtils.isNotBlank(formDataJson.getString("productData"))) {
                JSONArray productDataJson = JSONObject.parseArray(formDataJson.getString("productData"));
                for (int i = 0; i < productDataJson.size(); i++) {
                    JSONObject oneObject = productDataJson.getJSONObject(i);
                    String state = oneObject.getString("_state");
                    String productId = oneObject.getString("id");
                    if ("added".equals(state) || StringUtils.isBlank(productId)) {
                        // 新增
                        oneObject.put("id", IdUtil.getId());
                        oneObject.put("mainId", mainId);
                        oneObject.put("reason", oneObject.getString("reason"));
                        oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                        oneObject.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                        oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                        oneObject.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                        if(oneObject.get("charge")!=null){
                            String deptId = commonInfoDao.getDeptIdByUserId(oneObject.getString("charge"));
                            oneObject.put("deptId", deptId);
                        }
                        ndkfjhBudgetDao.addBudgetDetail(oneObject);
                    } else if ("modified".equals(state)) {
                        oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                        oneObject.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                        if(oneObject.get("charge")!=null){
                            String deptId = commonInfoDao.getDeptIdByUserId(oneObject.getString("charge"));
                            oneObject.put("deptId", deptId);
                        }
                        ndkfjhBudgetDao.updateBudgetDetail(oneObject);
                    } else if ("removed".equals(state)) {
                        // 删除
                        ndkfjhBudgetDao.delBudgetDetail(oneObject.getString("id"));
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Exception in update 更新异常", e);
            return ResultUtil.result(false, "更新异常！", "");
        }
        return ResultUtil.result(true, "更新成功", resultJson);
    }

    public JSONObject remove(HttpServletRequest request) {
        JSONObject resultJson = new JSONObject();
        try {
            Map<String, Object> params = new HashMap<>(16);
            String ids = request.getParameter("ids");
            String[] idArr = ids.split(",", -1);
            List<String> idList = Arrays.asList(idArr);
            params.put("ids", idList);
            ndkfjhBudgetDao.batchDelete(params);
            ndkfjhBudgetDao.batchDeleteDetail(params);
        } catch (Exception e) {
            logger.error("Exception in update 删除信息异常", e);
            return ResultUtil.result(false, "删除信息异常！", "");
        }
        return ResultUtil.result(true, "删除成功", resultJson);
    }

    public JSONObject getObjectById(String id) {
        JSONObject jsonObject = ndkfjhBudgetDao.getObjectById(id);
        return jsonObject;
    }

    /**
     * 模板下载
     *
     * @return
     */
    public ResponseEntity<byte[]> importTemplateDownload() {
        try {
            String fileName = "年度预算导入模板.xlsx";
            // 创建文件实例
            File file =
                new File(MaterielService.class.getClassLoader().getResource("templates/zhgl/" + fileName).toURI());
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

    public void importBudget(JSONObject result, HttpServletRequest request) {
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
            Sheet sheet = wb.getSheet("项目预算");
            if (sheet == null) {
                logger.error("找不到项目预算");
                result.put("message", "数据导入失败，找不到项目预算导入页！");
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
            List<Map<String, Object>> mainList = new ArrayList<>();
            List<Map<String, Object>> detailList = new ArrayList<>();
            String budgetYear = request.getParameter("budgetYear");
            String mainId = "";
            for (int i = 2; i < rowNum; i++) {
                Row row = sheet.getRow(i);
                JSONObject rowParse = generateDataFromRow(row, mainList, detailList, titleList, budgetYear, mainId);
                mainId = rowParse.getString("mainId");
                if (!rowParse.getBoolean("result")) {
                    result.put("message", "数据导入失败，第" + (i + 1) + "行数据错误：" + rowParse.getString("message"));
                    return;
                }
            }
            List<Map<String, Object>> tempInsert = new ArrayList<>();
            //为防止重复导入数据，根据项目编号和年份进行判断
            List<String> mainIdList = new ArrayList<>();
            for (int i = 0; i < mainList.size(); i++) {
                Map<String, Object> map = mainList.get(i);
                JSONObject obj = ndkfjhBudgetDao.getBudgetByInfo(map);
                if(obj==null){
                    ndkfjhBudgetDao.addMapObject(mainList.get(i));
                }else{
                    mainIdList.add(map.get("id").toString());
                }
            }
            for (int i = 0; i < detailList.size(); i++) {
                Map<String, Object> map = detailList.get(i);
                mainId = CommonFuns.nullToString(map.get("mainId"));
                boolean flag = isExist(mainId,mainIdList);
                if(!flag){
                    ndkfjhBudgetDao.addMapBudgetDetail(detailList.get(i));
                }
            }
            result.put("message", "数据导入成功！");
        } catch (Exception e) {
            logger.error("Exception in importProduct", e);
            result.put("message", "数据导入失败，系统异常！");
        }
    }
    public Boolean isExist(String mainId,List<String> list){
        boolean flag = false;
        for(String temp:list){
            if(mainId.equals(temp)){
                flag = true;
                break;
            }
        }
        return flag;
    }

    private JSONObject generateDataFromRow(Row row, List<Map<String, Object>> mainList,
        List<Map<String, Object>> detailList, List<String> titleList, String budgetYear, String mainId) {
        JSONObject oneRowCheck = new JSONObject();
        oneRowCheck.put("result", false);
        Map<String, Object> mainRowMap = new HashMap<>(16);
        Map<String, Object> detailRowMap = new HashMap<>(16);
        Boolean firstRow = false;
        for (int i = 0; i < titleList.size(); i++) {
            String title = titleList.get(i);
            title = title.replaceAll(" ", "");
            Cell cell = row.getCell(i);
            String cellValue = null;
            if (cell != null) {
                cellValue = ExcelUtil.getCellFormatValue(cell);
            }
            switch (title) {
                case "序号":
                    break;
                case "项目编号":
                    if (StringUtils.isNotBlank(cellValue)) {
                        mainRowMap.put("projectCode", cellValue);
                        firstRow = true;
                    }
                    break;
                case "项目名称":
                    if (StringUtils.isNotBlank(cellValue)) {
                        mainRowMap.put("projectName", cellValue);
                    }
                    break;
                case "工作目标":
                    if (StringUtils.isNotBlank(cellValue)) {
                        mainRowMap.put("target", cellValue);
                    }
                    break;
                case "工作内容（预算当年工作内容）":
                    if (StringUtils.isNotBlank(cellValue)) {
                        mainRowMap.put("content", cellValue);
                    }
                    break;
                case "关键/核心技术":
                    if (StringUtils.isNotBlank(cellValue)) {
                        mainRowMap.put("coreTechnology", cellValue);
                    }
                    break;
                case "产出新产品合计（台套数）":
                    if (StringUtils.isNotBlank(cellValue)) {
                        mainRowMap.put("newProductNum", cellValue);
                    }
                    break;
                case "其中：首台套（名称）":
                    if (StringUtils.isNotBlank(cellValue)) {
                        mainRowMap.put("firstName", cellValue);
                    }
                    break;
                case "其中：升级产品（名称）":
                    if (StringUtils.isNotBlank(cellValue)) {
                        mainRowMap.put("upProductName", cellValue);
                    }
                    break;
                case "其中：改进产品（名称）":
                    if (StringUtils.isNotBlank(cellValue)) {
                        mainRowMap.put("improveName", cellValue);
                    }
                    break;
                case "首席工程师":
                    if (StringUtils.isNotBlank(cellValue)) {
                        mainRowMap.put("chiefEngineer", cellValue);
                    }
                    break;
                case "发明专利（预算当年数）":
                    if (StringUtils.isNotBlank(cellValue)) {
                        mainRowMap.put("patentNum", cellValue);
                    }
                    break;
                case "项目开始日期":
                    if (StringUtils.isNotBlank(cellValue)) {
                        mainRowMap.put("startDate", cellValue);
                    }
                    break;
                case "项目结束日期":
                    if (StringUtils.isNotBlank(cellValue)) {
                        mainRowMap.put("endDate", cellValue);
                    }
                    break;
                case "产品列表":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "产品列表为空");
                    }
                    detailRowMap.put("products", cellValue);
                    break;
                case "部门":
                    break;
                case "负责人":
                    if (!StringUtils.isBlank(cellValue)) {
                        List<JSONObject> list = commonInfoManager.getUserIdByUserName(cellValue);
                        if(list!=null && list.isEmpty()){
                            oneRowCheck.put("message", "用户："+cellValue+"在系统中找不到对应的账号！");
                            return oneRowCheck;
                        }else if(list != null && list.size()>1){
                            oneRowCheck.put("message", "用户："+cellValue+"在系统中找到多个账号！");
                            return oneRowCheck;
                        }else if(list != null && list.size()==1){
                            detailRowMap.put("charge", list.get(0).getString("USER_ID_"));
                            String deptId = commonInfoDao.getDeptIdByUserId(list.get(0).getString("USER_ID_"));
                            detailRowMap.put("deptId", deptId);
                        }else{
                            oneRowCheck.put("message", "根据用户名查找用户信息出错！");
                            return oneRowCheck;
                        }
                    }

                    break;
                default:
                    oneRowCheck.put("message", "列“" + title + "”不存在");
                    return oneRowCheck;
            }
        }
        if (firstRow) {
            mainId = IdUtil.getId();
            mainRowMap.put("id", mainId);
            mainRowMap.put("budgetYear", budgetYear);
            mainRowMap.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            mainRowMap.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            mainRowMap.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            mainRowMap.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            mainRowMap.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            mainList.add(mainRowMap);
        }
        detailRowMap.put("id", IdUtil.getId());
        detailRowMap.put("mainId", mainId);
        detailRowMap.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        detailRowMap.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        detailRowMap.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        detailRowMap.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        detailRowMap.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        detailList.add(detailRowMap);
        oneRowCheck.put("result", true);
        oneRowCheck.put("mainId", mainId);
        return oneRowCheck;
    }
    public static void convertDate(List<Map<String, Object>> list) {
        if (list != null && !list.isEmpty()) {
            for (Map<String, Object> oneApply : list) {
                for (String key : oneApply.keySet()) {
                    if (key.endsWith("_TIME_") || key.endsWith("_time") || key.endsWith("_date")) {
                        if (oneApply.get(key) != null) {
                            oneApply.put(key, DateUtil.formatDate((Date)oneApply.get(key), "yyyy-MM-dd  HH:mm:ss"));
                        }
                    }
                    if ("startDate".equals(key)) {
                        if (oneApply.get(key) != null) {
                            oneApply.put(key, DateUtil.formatDate((Date)oneApply.get(key), "yyyy-MM-dd"));
                        }
                    }
                    if ("endDate".equals(key)) {
                        if (oneApply.get(key) != null) {
                            oneApply.put(key, DateUtil.formatDate((Date)oneApply.get(key), "yyyy-MM-dd"));
                        }
                    }
                }
            }
        }
    }
}
