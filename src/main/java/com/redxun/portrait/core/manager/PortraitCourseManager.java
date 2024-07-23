package com.redxun.portrait.core.manager;

import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSONArray;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.info.core.util.HttpRequestUtil;
import com.redxun.portrait.core.dao.PortraitCourseDao;
import com.redxun.portrait.core.dao.PortraitDocumentDao;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.standardManager.core.util.ResultUtil;
import com.redxun.sys.org.dao.OsUserDao;

/**
 * @author zhangzhen
 */
@Service
public class PortraitCourseManager {
    private static final Logger logger = LoggerFactory.getLogger(PortraitCourseManager.class);
    @Resource
    PortraitCourseDao portraitCourseDao;
    @Autowired
    OsUserDao osUserDao;
    @Resource
    PortraitDocumentDao portraitDocumentDao;
    @Resource
    CommonInfoDao commonInfoDao;
    @Resource
    CommonInfoManager commonInfoManager;
    public JsonPageResult<?> query(HttpServletRequest request) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            Map<String, Object> params = new HashMap<>(16);
            List<Map<String, Object>> list = new ArrayList<>();
            List<Map<String, Object>> totalList = new ArrayList<>();
            params = CommonFuns.getSearchParam(params, request, true);
            List<String> idList = new ArrayList<>();
            //用户权限
            JSONObject resultJson = commonInfoManager.hasPermission("courseAdmin");
            if("admin".equals(ContextUtil.getCurrentUser().getUserNo())) {
                Map<String,String> deptMap=commonInfoManager.queryDeptUnderJSZX();
                idList.addAll(deptMap.keySet());
            }else{
                if(resultJson.getBoolean(RdmConst.ALLDATA_QUERY_KEY)||resultJson.getBoolean("HX-GLY")){
                    //分管领导看所有数据
                    Map<String,String> deptMap=commonInfoManager.queryDeptUnderJSZX();
                    idList.addAll(deptMap.keySet());
                }else if(resultJson.getBoolean("courseAdmin")){
                    Map<String,String> deptMap=commonInfoManager.queryDeptUnderJSZX();
                    idList.addAll(deptMap.keySet());
                } else if(resultJson.getBoolean("isLeader")){
                    //部门领导看自己部门的
                    idList.add(ContextUtil.getCurrentUser().getMainGroupId());
                }else{
                    //普通员工看自己的
                    params.put("userId",ContextUtil.getCurrentUserId());
                    idList.add(ContextUtil.getCurrentUser().getMainGroupId());
                }
            }
            params.put("ids", idList);
            list = portraitCourseDao.query(params);
            params = new HashMap<>(16);
            params = CommonFuns.getSearchParam(params, request, false);
            params.put("ids", idList);
            if(!resultJson.getBoolean(RdmConst.ALLDATA_QUERY_KEY)&&!resultJson.getBoolean("isLeader")&&!resultJson.getBoolean("HX-GLY")
                    &&!resultJson.getBoolean("courseAdmin")&&!"admin".equals(ContextUtil.getCurrentUser().getUserNo())){
                params.put("userId",ContextUtil.getCurrentUserId());
            }
            totalList = portraitCourseDao.query(params);
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
                if (mapKey.equals("courseDate")) {
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
            String []userArray = userIds.split(",");
            for(String userId:userArray){
                String id = IdUtil.getId();
                objBody.put("id", id);
                objBody.put("userId",userId);
                portraitCourseDao.addObject(objBody);
            }
        } catch (Exception e) {
            logger.error("Exception in add 添加课程异常！", e);
            return ResultUtil.result(false, "添加课程异常！", "");
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
                if (mapKey.equals("courseDate")) {
                    if (mapValue != null && !"".equals(mapValue)) {
                        mapValue = CommonFuns.convertDateToStr(new Date(mapValue), "yyyy-MM-dd");
                    }
                }
                objBody.put(mapKey, mapValue);
            }
            objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("UPDATE_TIME_", new Date());
            portraitCourseDao.updateObject(objBody);
        } catch (Exception e) {
            logger.error("Exception in update 更新课程异常", e);
            return ResultUtil.result(false, "更新课程异常！", "");
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
            portraitCourseDao.batchDelete(params);
        } catch (Exception e) {
            logger.error("Exception in update 删除课程", e);
            return ResultUtil.result(false, "删除课程异常！", "");
        }
        return ResultUtil.result(true, "删除成功", resultJson);
    }
    public JSONObject getObjectById(String id){
        return portraitCourseDao.getObjectById(id);
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
                    if ("courseDate".equals(key)) {
                        if (oneApply.get(key) != null) {
                            oneApply.put(key, DateUtil.formatDate((Date)oneApply.get(key), "yyyy-MM-dd"));
                        }
                    }
                }
            }
        }
    }
    public List<Map<String,Object>> getPersonCourseList(JSONObject jsonObject){
        List<Map<String,Object>> list = portraitCourseDao.getPersonCourseList(jsonObject);
        convertDate(list);
        return list;
    }
    public JSONObject asyncCourse(){
        JSONObject paramJson = new JSONObject();
        try{
            String portUrl = portraitDocumentDao.getPortraitConfig("coursePort");
            JSONObject resultJson = HttpRequestUtil.doPost(portUrl, paramJson);
            Map<String, String> userCertNo2UserId = new HashMap<>(16);
            getUserCert2Id(userCertNo2UserId);
            if (resultJson.getBoolean("success")) {
                JSONArray resultArray = resultJson.getJSONArray("data");
                Map<String, Object> objBody ;
                JSONObject temp;
                List<Map<String, Object>> dataInsert = new ArrayList<>();
                if(resultArray!=null&&resultArray.size()>0){
                    //先清空数据
                    portraitCourseDao.delAllAsyncData();
                    for(int i=0;i<resultArray.size();i++){
                        temp = resultArray.getJSONObject(i);
                        objBody = new HashMap<>(16);
                        //先判断 根据身份证是否能找到用户
                        String userId = userCertNo2UserId.get(temp.getString("cert_no"));
                        if(StringUtil.isEmpty(userId)){
                            continue;
                        }
                        objBody.put("id",IdUtil.getId());
                        objBody.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                        objBody.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                        objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                        objBody.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                        objBody.put("userId",userId);
                        objBody.put("courseDate",temp.getDate("open_time"));
                        objBody.put("courseName",temp.getString("title"));
                        objBody.put("studentNum",temp.getIntValue("num"));
                        objBody.put("courseHour",temp.getFloatValue("course_time")/60);
                        Float score = 0f;
                        if(temp.getIntValue("num")>=20&&temp.getFloatValue("course_time")/60>=1.5){
                            score = 0.2f;
                        }else{
                            score = 0.1f;
                        }
                        objBody.put("score",score);
                        objBody.put("async","1");
                        dataInsert.add(objBody);
                    }
                    // 分批写入数据库(20条一次)
                    List<Map<String, Object>> tempInsert = new ArrayList<>();
                    for (int i = 0; i < dataInsert.size(); i++) {
                        tempInsert.add(dataInsert.get(i));
                        if (tempInsert.size() % 20 == 0) {
                            portraitCourseDao.batchInsertCourse(tempInsert);
                            tempInsert.clear();
                        }
                    }
                    if (!tempInsert.isEmpty()) {
                        portraitCourseDao.batchInsertCourse(tempInsert);
                        tempInsert.clear();
                    }
                }

            }else{
                return ResultUtil.result(false,resultJson.getString("resultMsg"),null);
            }
        }catch (Exception e){
            logger.error("Exception in asyncCourse ", e);
            return ResultUtil.result(false,"同步失败",null);
        }
        return ResultUtil.result(true,"同步成功",null);
    }
    private void getUserCert2Id(Map<String, String> userCertNo2UserId) {
        Map<String, Object> params = new HashMap<>(16);
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        List<Map<String,String>> userIdAndCertNoList = commonInfoDao.getUserIdAndCerNo();
        if (userIdAndCertNoList != null && !userIdAndCertNoList.isEmpty()) {
            for (Map<String, String> oneMap : userIdAndCertNoList) {
                if (StringUtils.isNotBlank(oneMap.get("USER_ID_")) && StringUtils.isNotBlank(oneMap.get("CERT_NO_"))) {
                    userCertNo2UserId.put(oneMap.get("CERT_NO_"), oneMap.get("USER_ID_"));
                }
            }
        }
    }
}
