package com.redxun.portrait.core.manager;

import java.io.File;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.materielextend.core.service.MaterielService;
import com.redxun.portrait.core.dao.PortraitDocumentDao;
import com.redxun.portrait.core.dao.PortraitKnowledgeDao;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.standardManager.core.dao.StandardDao;
import com.redxun.standardManager.core.util.ResultUtil;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

/**
 * @author zhangzhen
 */
@Service
public class PortraitKnowledgeManager {
    private static final Logger logger = LoggerFactory.getLogger(PortraitKnowledgeManager.class);
    @Resource
    PortraitKnowledgeDao portraitKnowledgeDao;
    @Resource
    CommonInfoManager commonInfoManager;
    @Resource
    CommonInfoDao commonInfoDao;
    @Resource
    StandardDao standardDao;
    @Resource
    PortraitDocumentDao portraitDocumentDao;

    public static void convertDate(List<Map<String, Object>> list) {
        if (list != null && !list.isEmpty()) {
            for (Map<String, Object> oneApply : list) {
                for (String key : oneApply.keySet()) {
                    if (key.endsWith("_TIME_") || key.endsWith("_time") || key.endsWith("_date")) {
                        if (oneApply.get(key) != null) {
                            oneApply.put(key, DateUtil.formatDate((Date)oneApply.get(key), "yyyy-MM-dd  HH:mm:ss"));
                        }
                    }
                    if ("applyDate".equals(key)) {
                        if (oneApply.get(key) != null) {
                            oneApply.put(key, DateUtil.formatDate((Date)oneApply.get(key), "yyyy-MM-dd"));
                        }
                    }
                    if ("authorizeDate".equals(key)) {
                        if (oneApply.get(key) != null) {
                            oneApply.put(key, DateUtil.formatDate((Date)oneApply.get(key), "yyyy-MM-dd"));
                        }
                    }
                }
            }
        }
    }

    public static String parseTime2Utc(String timeStr) {
        if (StringUtils.isBlank(timeStr)) {
            return null;
        }
        Date date = DateUtil.parseDate(timeStr, DateUtil.DATE_FORMAT_FULL);
        if (date == null) {
            return null;
        }
        return DateUtil.formatDate(DateUtil.addHour(date, -8));
    }

    public JsonPageResult<?> query(HttpServletRequest request) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            Map<String, Object> params = new HashMap<>(16);
            List<Map<String, Object>> list = new ArrayList<>();
            List<Map<String, Object>> totalList = new ArrayList<>();
            params = CommonFuns.getSearchParam(params, request, true);
            List<String> idList = new ArrayList<>();
            // 用户权限
            JSONObject resultJson = commonInfoManager.hasPermission("knowledgeAdmin");
            if ("admin".equals(ContextUtil.getCurrentUser().getUserNo())) {
                Map<String,String> deptMap=commonInfoManager.queryDeptUnderJSZX();
                idList.addAll(deptMap.keySet());
            } else {
                if (resultJson.getBoolean(RdmConst.ALLDATA_QUERY_KEY)||resultJson.getBoolean("HX-GLY")) {
                    // 分管领导看所有数据
                    Map<String,String> deptMap=commonInfoManager.queryDeptUnderJSZX();
                    idList.addAll(deptMap.keySet());
                }else if(resultJson.getBoolean("knowledgeAdmin")){
                    Map<String,String> deptMap=commonInfoManager.queryDeptUnderJSZX();
                    idList.addAll(deptMap.keySet());
                } else if (resultJson.getBoolean("isLeader")) {
                    // 部门领导看自己部门的
                    idList.add(ContextUtil.getCurrentUser().getMainGroupId());
                } else {
                    // 普通员工看自己的
                    params.put("userId", ContextUtil.getCurrentUserId());
                    idList.add(ContextUtil.getCurrentUser().getMainGroupId());
                }
            }
            params.put("ids", idList);
            list = portraitKnowledgeDao.query(params);
            params = new HashMap<>(16);
            params = CommonFuns.getSearchParam(params, request, false);
            params.put("ids", idList);
            if (!resultJson.getBoolean(RdmConst.ALLDATA_QUERY_KEY) && !resultJson.getBoolean("isLeader")&&!resultJson.getBoolean("HX-GLY")
                && !resultJson.getBoolean("knowledgeAdmin") && !"admin".equals(ContextUtil.getCurrentUser().getUserNo())) {
                params.put("userId", ContextUtil.getCurrentUserId());
            }
            totalList = portraitKnowledgeDao.query(params);
            convertDate(list);
            // 返回结果
            result.setData(list);
            result.setTotal(totalList.size());
        } catch (Exception e) {
            logger.error("Exception in 查询异常", e);
            result.setSuccess(false);
            result.setMessage("系统异常");
        }
        return result;
    }
    public JSONObject asyncKnowledge(){
        try{
            //1.同步科技论文
            asyncPaper();
            //2.同步软著
            asyncSoft();
            //3.同步授权的专利
            asyncAuthorizePatent();
        } catch (Exception e) {
            logger.error("Exception in add asyncKnowledge！", e);
            return ResultUtil.result(false,"同步失败",null);
        }
        return ResultUtil.result(true,"同步成功",null);
    }
    public JSONObject asyncPaper() {
        try {
            List<JSONObject> list = portraitKnowledgeDao.getPaperList();
            Map<String, Object> objBody = new HashMap<>(16);
            for (JSONObject temp : list) {
                objBody = new HashMap<>(16);
                objBody.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                objBody.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                objBody.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                objBody.put("knowledgeCode", temp.getString("kjlwNum"));
                objBody.put("knowledgeName", temp.getString("kjlwName"));
                objBody.put("knowledgeType", "paper");
                objBody.put("applyDate", temp.getDate("CREATE_TIME_"));
                objBody.put("knowledgeStatus", "authorize");
                objBody.put("authorizeDate", temp.getDate("fbTime"));
                objBody.put("orgId", temp.getString("kjlwId"));
                String writerId = temp.getString("writerId");
                if(StringUtil.isNotEmpty(writerId)){
                    String []idArray = writerId.split(",");
                    for(int i=0;i<idArray.length;i++){
                        if(i==0){
                            objBody.put("userId", idArray[i]);
                            objBody.put("score", 1);
                            objBody.put("ranking", i+1);
                        }else if(i==1||i==2){
                            objBody.put("userId", idArray[i]);
                            objBody.put("score", 0.5);
                            objBody.put("ranking", i+1);
                        }else{
                            continue;
                        }
                        objBody.put("id", IdUtil.getId());
                        portraitKnowledgeDao.addObject(objBody);
                    }
                }
                portraitKnowledgeDao.updatePaperStatus(temp.getString("kjlwId"));
            }
        } catch (Exception e) {
            logger.error("Exception in add asyncPaper！", e);
            return ResultUtil.result(false,"同步失败",null);
        }
        return ResultUtil.result(true,"同步成功",null);
    }

    public JSONObject asyncSoft() {
        try {
            List<JSONObject> list = portraitKnowledgeDao.getSoftList();
            Map<String, Object> objBody = new HashMap<>(16);
            for (JSONObject temp : list) {
                objBody = new HashMap<>(16);
                objBody.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                objBody.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                objBody.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                objBody.put("knowledgeCode", temp.getString("zsNum"));
                objBody.put("knowledgeName", temp.getString("rjmqc"));
                objBody.put("knowledgeType", "soft");
                objBody.put("applyDate", temp.getDate("CREATE_TIME_"));
                objBody.put("knowledgeStatus", "authorize");
                objBody.put("authorizeDate", temp.getDate("djTime"));
                objBody.put("orgId", temp.getString("rjzzId"));
                String rjkfIds = temp.getString("rjkfIds");
                if(StringUtil.isNotEmpty(rjkfIds)){
                    String []idArray = rjkfIds.split(",");
                    for(int i=0;i<idArray.length;i++){
                        if(i==0){
                            objBody.put("userId", idArray[i]);
                            objBody.put("score", 2);
                            objBody.put("ranking", i+1);
                        }else if(i==1){
                            objBody.put("userId", idArray[i]);
                            objBody.put("score", 1);
                            objBody.put("ranking", i+1);
                        }else if(i==2){
                            objBody.put("userId", idArray[i]);
                            objBody.put("score", 0.5);
                            objBody.put("ranking", i+1);
                        }else{
                            continue;
                        }
                        objBody.put("id", IdUtil.getId());
                        portraitKnowledgeDao.addObject(objBody);
                    }
                }
                portraitKnowledgeDao.updateSoftStatus(temp.getString("rjzzId"));
            }
        } catch (Exception e) {
            logger.error("Exception in add asyncSoft！", e);
            return ResultUtil.result(false,"同步失败",null);
        }
        return ResultUtil.result(true,"同步成功",null);
    }

    public JSONObject asyncAuthorizePatent() {
        try {
            List<JSONObject> list = portraitKnowledgeDao.getAuthorizePatentList();
            Map<String, Object> objBody = new HashMap<>(16);
            for (JSONObject temp : list) {
                objBody = new HashMap<>(16);
                objBody.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                objBody.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                objBody.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                objBody.put("knowledgeCode", temp.getString("specifyCountry"));
                objBody.put("knowledgeName", temp.getString("patentName"));
                String patentType = temp.getString("patentType");
                objBody.put("knowledgeType", changePatentType(patentType));
                objBody.put("applyDate", temp.getDate("CREATE_TIME_"));
                objBody.put("knowledgeStatus", "authorize");
                objBody.put("authorizeDate", temp.getDate("authionization"));
                objBody.put("orgId", temp.getString("zgzlId"));
                portraitKnowledgeDao.delPatentByOrgId(temp.getString("zgzlId"));
                String myCompanyUserIds = temp.getString("myCompanyUserIds");
                if(StringUtil.isNotEmpty(myCompanyUserIds)){
                    String []idArray = myCompanyUserIds.split(",");
                    for(int i=0;i<idArray.length;i++){
                        if(i==0){
                            objBody.put("userId", idArray[i]);
                            objBody.put("score", 3);
                            objBody.put("ranking", i+1);
                        }else if(i==1){
                            objBody.put("userId", idArray[i]);
                            objBody.put("score", 1);
                            objBody.put("ranking", i+1);
                        }else if(i==2){
                            objBody.put("userId", idArray[i]);
                            objBody.put("score", 0.5);
                            objBody.put("ranking", i+1);
                        }else{
                            continue;
                        }
                        objBody.put("id", IdUtil.getId());
                        portraitKnowledgeDao.addObject(objBody);
                    }
                }
                portraitKnowledgeDao.updateAuthorizePatent(temp.getString("zgzlId"));
            }
        } catch (Exception e) {
            logger.error("Exception in add asyncAuthorizePatent！", e);
            return ResultUtil.result(false,"同步失败",null);
        }
        return ResultUtil.result(true,"同步成功",null);
    }
    public String changePatentType(String patentName){
        String patentType = "";
       switch (patentName){
           case "发明" : patentType = "invention";
           break;
           case "实用新型" : patentType = "utility";
           break;
           case "外观设计" : patentType = "viewDesign";
           break;
       }
       return patentType;
    }



    public JSONObject add(HttpServletRequest request) {
        JSONObject resultJson = new JSONObject();
        try {
            Map<String, String[]> parameters = request.getParameterMap();
            if (parameters == null || parameters.isEmpty()) {
                logger.error("表单内容为空！");
                return ResultUtil.result(false, "操作失败，表单内容为空！", "");
            }
            Map<String, Object> objBody = new HashMap<>(16);
            for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
                String mapKey = entry.getKey();
                String mapValue = entry.getValue()[0];
                if (mapKey.equals("applyDate")) {
                    if (mapValue != null && !"".equals(mapValue)) {
                        mapValue = CommonFuns.convertDateToStr(new Date(mapValue), "yyyy-MM-dd");
                    }
                }
                if (mapKey.equals("authorizeDate")) {
                    if (mapValue != null && !"".equals(mapValue)) {
                        mapValue = CommonFuns.convertDateToStr(new Date(mapValue), "yyyy-MM-dd");
                    }
                }
                objBody.put(mapKey, mapValue);
            }
            objBody.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            String userIds = CommonFuns.nullToString(objBody.get("userId"));
            String[] userArray = userIds.split(",");
            for (String userId : userArray) {
                String id = IdUtil.getId();
                objBody.put("id", id);
                objBody.put("userId", userId);
                portraitKnowledgeDao.addObject(objBody);
            }
        } catch (Exception e) {
            logger.error("Exception in add 添加知识产权异常！", e);
            return ResultUtil.result(false, "添加知识产权异常！", "");
        }
        return ResultUtil.result(true, "新增成功", resultJson);
    }

    public JSONObject update(HttpServletRequest request) {
        JSONObject resultJson = new JSONObject();
        try {
            Map<String, String[]> parameters = request.getParameterMap();
            Map<String, Object> objBody = new HashMap<>(16);
            for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
                String mapKey = entry.getKey();
                String mapValue = entry.getValue()[0];
                if (mapKey.equals("applyDate")) {
                    if (mapValue != null && !"".equals(mapValue)) {
                        mapValue = CommonFuns.convertDateToStr(new Date(mapValue), "yyyy-MM-dd");
                    }
                }
                if (mapKey.equals("authorizeDate")) {
                    if (mapValue != null && !"".equals(mapValue)) {
                        mapValue = CommonFuns.convertDateToStr(new Date(mapValue), "yyyy-MM-dd");
                    }
                }
                objBody.put(mapKey, mapValue);
            }
            objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            portraitKnowledgeDao.updateObject(objBody);
        } catch (Exception e) {
            logger.error("Exception in update 更新知识产权异常", e);
            return ResultUtil.result(false, "更新知识产权异常！", "");
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
            portraitKnowledgeDao.batchDelete(params);
        } catch (Exception e) {
            logger.error("Exception in update 删除知识产权", e);
            return ResultUtil.result(false, "删除知识产权异常！", "");
        }
        return ResultUtil.result(true, "删除成功", resultJson);
    }

    public JSONObject getObjectById(String id) {
        return portraitKnowledgeDao.getObjectById(id);
    }

    public List<Map<String, Object>> getPersonKnowledgeList(JSONObject jsonObject) {
        List<Map<String, Object>> list = portraitKnowledgeDao.getPersonKnowledgeList(jsonObject);
        convertDate(list);
        return list;
    }

    /**
     * 知识产权模板下载
     *
     * @return
     */
    public ResponseEntity<byte[]> importTemplateDownload() {
        try {
            String fileName = "知识产权导入模板.xls";
            // 创建文件实例
            File file =
                new File(MaterielService.class.getClassLoader().getResource("templates/portrait/" + fileName).toURI());
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

    public void importKnowledge(JSONObject result, HttpServletRequest request) {
        try {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
            MultipartFile fileObj = multipartRequest.getFile("knowledgeImportFile");
            if (fileObj == null) {
                result.put("message", "数据导入失败，内容为空！");
                return;
            }
            HSSFWorkbook wb = new HSSFWorkbook(fileObj.getInputStream());
            HSSFSheet sheet = wb.getSheet("知识产权");
            if (sheet == null) {
                logger.error("找不到知识产权导入页");
                result.put("message", "数据导入失败，找不到知识产权导入页！");
                return;
            }
            int rowNum = sheet.getPhysicalNumberOfRows();
            if (rowNum < 1) {
                logger.error("找不到标题行");
                result.put("message", "数据导入失败，找不到标题行！");
                return;
            }

            // 解析标题部分
            HSSFRow titleRow = sheet.getRow(0);
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
            Map<String, Object> params = new HashMap<>(16);
            Map<String, String> type2Id = new HashMap<>(16);
            Map<String, String> status2Id = new HashMap<>(16);
            Map<String, String> userNo2UserId = new HashMap<>(16);
            params = new HashMap<>(16);
            params.put("key", "knowledgeType");
            getCategoryName2Id(type2Id, commonInfoDao.getDicValues(params));
            params = new HashMap<>(16);
            params.put("key", "knowledgeStatus");
            getCategoryName2Id(status2Id, commonInfoDao.getDicValues(params));
            getUserCert2Id(userNo2UserId);
            // 解析验证数据部分，任何一行的任何一项不满足条件，则直接返回失败
            List<Map<String, Object>> dataInsert = new ArrayList<>();
            for (int i = 1; i < rowNum; i++) {
                HSSFRow row = sheet.getRow(i);
                JSONObject rowParse =
                    generateDataFromRow(row, dataInsert, titleList, type2Id, status2Id, userNo2UserId);
                if (!rowParse.getBoolean("result")) {
                    result.put("message", "数据导入失败，第" + (i + 1) + "行数据错误：" + rowParse.getString("message"));
                    return;
                }
            }
            // 分批写入数据库(20条一次)
            List<Map<String, Object>> tempInsert = new ArrayList<>();
            for (int i = 0; i < dataInsert.size(); i++) {
                tempInsert.add(dataInsert.get(i));
                if (tempInsert.size() % 20 == 0) {
                    portraitKnowledgeDao.batchInsertKnowledge(tempInsert);
                    tempInsert.clear();
                }
            }
            if (!tempInsert.isEmpty()) {
                portraitKnowledgeDao.batchInsertKnowledge(tempInsert);
                tempInsert.clear();
            }
            result.put("message", "数据导入成功！");
        } catch (Exception e) {
            logger.error("Exception in importKnowledge", e);
            result.put("message", "数据导入失败，系统异常！");
        }
    }

    private JSONObject generateDataFromRow(HSSFRow row, List<Map<String, Object>> dataInsert, List<String> titleList,
        Map<String, String> type2Id, Map<String, String> status2Id, Map<String, String> userNo2UserId) {
        JSONObject oneRowCheck = new JSONObject();
        oneRowCheck.put("result", false);
        Map<String, Object> oneRowMap = new HashMap<>();
        DataFormatter dataFormatter = new DataFormatter();
        for (int i = 0; i < titleList.size(); i++) {
            String title = titleList.get(i);
            HSSFCell cell = row.getCell(i);
            String cellValue = null;
            if (cell != null) {
                cellValue = StringUtils.trim(dataFormatter.formatCellValue(cell));
            }
            switch (title) {
                case "产权号":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "产权号为空");
                        return oneRowCheck;
                    }
                    // 判断是否已经存在
                    JSONArray tempJson = portraitKnowledgeDao.getObjectByCode(cellValue);
                    if (tempJson != null && tempJson.size() > 0) {
                        oneRowCheck.put("message", "产权号" + cellValue + "已存在");
                        return oneRowCheck;
                    }
                    oneRowMap.put("knowledgeCode", cellValue);
                    break;
                case "产权名称":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "产权名称为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("knowledgeName", cellValue);
                    break;
                case "第一发明人":
                    if (StringUtils.isNotBlank(cellValue)) {
                        List<JSONObject> list = commonInfoManager.getUserIdByUserName(cellValue);
                        if(list!=null && list.isEmpty()){
                            oneRowMap.put("firstAuthor", "");
                        }else if(list != null && list.size()>1){
                            oneRowCheck.put("message", "用户："+cellValue+"在系统中找到多个账号！");
                            return oneRowCheck;
                        }else if(list != null && list.size()==1){
                            oneRowMap.put("firstAuthor", list.get(0).getString("USER_ID_"));
                        }else{
                            oneRowCheck.put("message", "根据用户名查找用户信息出错！");
                            return oneRowCheck;
                        }
                    }else{
                        oneRowMap.put("firstAuthor", "");
                    }
                    break;
                case "第二发明人":
                    if (StringUtils.isNotBlank(cellValue)) {
                        List<JSONObject> list = commonInfoManager.getUserIdByUserName(cellValue);
                        if(list!=null && list.isEmpty()){
                            oneRowMap.put("secondAuthor", "");
                        }else if(list != null && list.size()>1){
                            oneRowCheck.put("message", "用户："+cellValue+"在系统中找到多个账号！");
                            return oneRowCheck;
                        }else if(list != null && list.size()==1){
                            oneRowMap.put("secondAuthor", list.get(0).getString("USER_ID_"));
                        }else{
                            oneRowCheck.put("message", "根据用户名查找用户信息出错！");
                            return oneRowCheck;
                        }
                    }else{
                        oneRowMap.put("secondAuthor", "");
                    }
                    break;
                case "第三发明人":
                    if (StringUtils.isNotBlank(cellValue)) {
                        List<JSONObject> list = commonInfoManager.getUserIdByUserName(cellValue);
                        if(list!=null && list.isEmpty()){
                            oneRowMap.put("thirdAuthor", "");
                        }else if(list != null && list.size()>1){
                            oneRowCheck.put("message", "用户："+cellValue+"在系统中找到多个账号！");
                            return oneRowCheck;
                        }else if(list != null && list.size()==1){
                            oneRowMap.put("thirdAuthor", list.get(0).getString("USER_ID_"));
                        }else{
                            oneRowCheck.put("message", "根据用户名查找用户信息出错！");
                            return oneRowCheck;
                        }
                    }else{
                        oneRowMap.put("thirdAuthor", "");
                    }
                    break;
                case "产权类型":
                    if (StringUtils.isNotBlank(cellValue) && !type2Id.containsKey(cellValue)) {
                        oneRowCheck.put("message", "产权类型不存在");
                        return oneRowCheck;
                    }
                    oneRowMap.put("knowledgeType", type2Id.get(cellValue));
                    break;
                case "申请日期":
                    if (StringUtils.isNotBlank(cellValue)) {
                        cellValue += " 00:00:00";
                        cellValue = parseTime2Utc(cellValue);
                        if (StringUtils.isBlank(cellValue)) {
                            oneRowCheck.put("message", "申请日期格式不正确");
                            return oneRowCheck;
                        }
                    }
                    if (StringUtils.isBlank(cellValue)) {
                        cellValue = null;
                    }
                    oneRowMap.put("applyDate", cellValue);
                    break;
                case "产权状态":
                    if (StringUtils.isNotBlank(cellValue) && !status2Id.containsKey(cellValue)) {
                        oneRowCheck.put("message", "产权状态不存在");
                        return oneRowCheck;
                    }
                    oneRowMap.put("knowledgeStatus", status2Id.get(cellValue));
                    break;
                case "登记或授权日期":
                    if (StringUtils.isNotBlank(cellValue)) {
                        cellValue += " 00:00:00";
                        cellValue = parseTime2Utc(cellValue);
                        if (StringUtils.isBlank(cellValue)) {
                            oneRowCheck.put("message", "受理或授权日期格式不正确");
                            return oneRowCheck;
                        }
                    }
                    if (StringUtils.isBlank(cellValue)) {
                        cellValue = null;
                    }
                    oneRowMap.put("authorizeDate", cellValue);
                    break;
                default:
                    oneRowCheck.put("message", "列“" + title + "”不存在");
                    return oneRowCheck;
            }
        }
        oneRowMap.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        oneRowMap.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        oneRowMap.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        oneRowMap.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        oneRowMap.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        /**
         * 说明： 发明专利 受理第一名得1分 授权 第一名3分 第二名 1分 第三名0.5 其余0分 实用新型：授权 第一名 1分其余 0分 软著：授权 第一名 2分 其余0分
         */
        String knowledgeType = CommonFuns.nullToString(oneRowMap.get("knowledgeType"));
        String knowledgeStatus = CommonFuns.nullToString(oneRowMap.get("knowledgeStatus"));
        String firstAuthor = CommonFuns.nullToString(oneRowMap.get("firstAuthor"));
        String secondAuthor = CommonFuns.nullToString(oneRowMap.get("secondAuthor"));
        String thirdAuthor = CommonFuns.nullToString(oneRowMap.get("thirdAuthor"));
        if(!StringUtil.isEmpty(firstAuthor)){
            Map<String, Object> tempMap = new HashMap<>(16);
            tempMap.putAll(oneRowMap);
            tempMap.put("id", IdUtil.getId());
            tempMap.put("userId", firstAuthor);
            tempMap.put("score", calculateScore(0, knowledgeType, knowledgeStatus));
            tempMap.put("ranking", 1);
            dataInsert.add(tempMap);
        }
        if(!StringUtil.isEmpty(secondAuthor)){
            Map<String, Object> tempMap = new HashMap<>(16);
            tempMap.putAll(oneRowMap);
            tempMap.put("id", IdUtil.getId());
            tempMap.put("userId", secondAuthor);
            tempMap.put("score", calculateScore(1, knowledgeType, knowledgeStatus));
            tempMap.put("ranking", 2);
            dataInsert.add(tempMap);
        }
        if(!StringUtil.isEmpty(thirdAuthor)){
            Map<String, Object> tempMap = new HashMap<>(16);
            tempMap.putAll(oneRowMap);
            tempMap.put("id", IdUtil.getId());
            tempMap.put("userId", thirdAuthor);
            tempMap.put("score", calculateScore(2, knowledgeType, knowledgeStatus));
            tempMap.put("ranking", 3);
            dataInsert.add(tempMap);
        }
        oneRowCheck.put("result", true);
        return oneRowCheck;
    }

    public float calculateScore(int rank, String knowledgeType, String knowledgeStatus) {
        float score = 0;
        if (rank == 0) {
            if ("invention".equals(knowledgeType)) {
                if ("accept".equals(knowledgeStatus)) {
                    score = 1;
                } else if ("authorize".equals(knowledgeStatus)) {
                    score = 3;
                }
            } else if ("utility".equals(knowledgeType)||"viewDesign".equals(knowledgeType)) {
                if ("authorize".equals(knowledgeStatus)) {
                    score = 1;
                }
            } else if ("soft".equals(knowledgeType)) {
                if ("authorize".equals(knowledgeStatus)) {
                    score = 2;
                }
            }
        } else if (rank == 1) {
            if ("invention".equals(knowledgeType)) {
                if ("accept".equals(knowledgeStatus)) {
                    score = 0;
                } else if ("authorize".equals(knowledgeStatus)) {
                    score = 1;
                }
            }
        } else if (rank == 2) {
            if ("invention".equals(knowledgeType)) {
                if ("accept".equals(knowledgeStatus)) {
                    score = 0;
                } else if ("authorize".equals(knowledgeStatus)) {
                    score = 0.5f;
                }
            }
        }
        return score;
    }

    /**
     * 构造字典项
     */
    private void getCategoryName2Id(Map<String, String> categoryName2Id, List<Map<String, Object>> categoryInfos) {
        if (categoryInfos != null && !categoryInfos.isEmpty()) {
            for (Map<String, Object> oneMap : categoryInfos) {
                categoryName2Id.put(oneMap.get("text").toString(), oneMap.get("key_").toString());
            }
        }
    }

    private void getUserCert2Id(Map<String, String> userCertNo2UserId) {
        Map<String, Object> params = new HashMap<>(16);
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        List<Map<String, String>> userIdAndCertNoList = commonInfoDao.getUserIdAndCerNo();
        if (userIdAndCertNoList != null && !userIdAndCertNoList.isEmpty()) {
            for (Map<String, String> oneMap : userIdAndCertNoList) {
                if (StringUtils.isNotBlank(oneMap.get("USER_ID_")) && StringUtils.isNotBlank(oneMap.get("CERT_NO_"))) {
                    userCertNo2UserId.put(oneMap.get("CERT_NO_"), oneMap.get("USER_ID_"));
                }
            }
        }
    }

    private void getUserNo2Id(Map<String, String> userNo2UserId) {
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        List<Map<String, String>> userList = standardDao.queryUserList(params);
        if (userList != null && !userList.isEmpty()) {
            for (Map<String, String> oneMap : userList) {
                if (StringUtils.isNotBlank(oneMap.get("userNo")) && StringUtils.isNotBlank(oneMap.get("userId"))) {
                    userNo2UserId.put(oneMap.get("userNo"), oneMap.get("userId"));
                }
            }
        }
    }

}
