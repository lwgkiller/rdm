package com.redxun.portrait.core.manager;

import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSONArray;
import com.redxun.core.util.DateUtil;
import com.redxun.portrait.core.dao.PortraitDocumentDao;
import com.redxun.portrait.core.dao.PortraitStandardDao;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.standardManager.core.dao.StandardDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.portrait.core.dao.PortraitNoticeDao;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.standardManager.core.util.ResultUtil;
import com.redxun.sys.org.dao.OsUserDao;

/**
 * @author zhangzhen
 */
@Service
public class PortraitStandardManager {
    private static final Logger logger = LoggerFactory.getLogger(PortraitStandardManager.class);
    @Resource
    PortraitStandardDao portraitStandardDao;
    @Autowired
    OsUserDao osUserDao;
    @Autowired
    private StandardDao standardDao;
    @Resource
    PortraitDocumentDao portraitDocumentDao;
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
            JSONObject resultJson = commonInfoManager.hasPermission("standardAdmin");
            if("admin".equals(ContextUtil.getCurrentUser().getUserNo())) {
                Map<String,String> deptMap=commonInfoManager.queryDeptUnderJSZX();
                idList.addAll(deptMap.keySet());
            }else{
                if(resultJson.getBoolean(RdmConst.ALLDATA_QUERY_KEY)||resultJson.getBoolean("HX-GLY")){
                    //分管领导看所有数据
                    Map<String,String> deptMap=commonInfoManager.queryDeptUnderJSZX();
                    idList.addAll(deptMap.keySet());
                }else if(resultJson.getBoolean("standardAdmin")){
                    Map<String,String> deptMap=commonInfoManager.queryDeptUnderJSZX();
                    idList.addAll(deptMap.keySet());
                }else if(resultJson.getBoolean("isLeader")){
                    //部门领导看自己部门的
                    idList.add(ContextUtil.getCurrentUser().getMainGroupId());
                }else{
                    //普通员工看自己的
                    params.put("userId",ContextUtil.getCurrentUserId());
                    idList.add(ContextUtil.getCurrentUser().getMainGroupId());
                }
            }
            params.put("ids", idList);
            list = portraitStandardDao.query(params);
            params = new HashMap<>(16);
            params = CommonFuns.getSearchParam(params, request, false);
            params.put("ids", idList);
            if(!resultJson.getBoolean(RdmConst.ALLDATA_QUERY_KEY)&&!resultJson.getBoolean("isLeader")&&!resultJson.getBoolean("HX-GLY")
                    &&!resultJson.getBoolean("standardAdmin")&&!"admin".equals(ContextUtil.getCurrentUser().getUserNo())){
                params.put("userId",ContextUtil.getCurrentUserId());
            }
            totalList = portraitStandardDao.query(params);
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
                if (mapKey.equals("publishDate")) {
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
                portraitStandardDao.addObject(objBody);
            }
        } catch (Exception e) {
            logger.error("Exception in add 添加标准异常！", e);
            return ResultUtil.result(false, "添加标准异常！", "");
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
                if (mapKey.equals("publishDate")) {
                    if (mapValue != null && !"".equals(mapValue)) {
                        mapValue = CommonFuns.convertDateToStr(new Date(mapValue), "yyyy-MM-dd");
                    }
                }
                objBody.put(mapKey, mapValue);
            }
            objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            portraitStandardDao.updateObject(objBody);
        } catch (Exception e) {
            logger.error("Exception in update 更新标准异常", e);
            return ResultUtil.result(false, "更新标准异常！", "");
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
            portraitStandardDao.batchDelete(params);
        } catch (Exception e) {
            logger.error("Exception in update 删除标准", e);
            return ResultUtil.result(false, "删除标准异常！", "");
        }
        return ResultUtil.result(true, "删除成功", resultJson);
    }
    public JSONObject getObjectById(String id){
        return portraitStandardDao.getObjectById(id);
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
                    if ("publishDate".equals(key)) {
                        if (oneApply.get(key) != null) {
                            oneApply.put(key, DateUtil.formatDate((Date)oneApply.get(key), "yyyy-MM-dd"));
                        }
                    }
                }
            }
        }
    }
    public List<Map<String,Object>> getPersonStandardList(JSONObject jsonObject){
        List<Map<String,Object>> list = portraitStandardDao.getPersonStandardList(jsonObject);
        convertDate(list);
        return list;
    }
    public JSONObject asyncStandard(){
        try{
            List<Map<String,Object>> list = portraitStandardDao.getAllStandard();
            Map<String, Object> objBody = new HashMap<>(16);
            if(list!=null&&list.size()>0){
                portraitStandardDao.deleteAllData();
            }
            convertDate(list);
            JSONArray rankArray = portraitStandardDao.getRankInfo();
            Map<String, String> userId2UserName = new HashMap<>();
            getUserId2Name(userId2UserName);
            for(Map<String,Object> map:list){
                String publisherId = CommonFuns.nullToString(map.get("publisherId"));
                String []idArray = publisherId.split(",");
                objBody = new HashMap<>(16);
                objBody.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                objBody.put("CREATE_TIME_", new Date());
                objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                objBody.put("UPDATE_TIME_", new Date());
                objBody.put("standardCode",map.get("standardNumber"));
                objBody.put("standardName",map.get("standardName"));
                objBody.put("standardType",map.get("categoryName"));
                objBody.put("publishDate",map.get("publishTime"));
                if (map.get("publisherId") != null) {
                    objBody.put("author", toGetUserNameByIds(map.get("publisherId").toString(), userId2UserName));
                }
                for(int i=0;i<idArray.length;i++){
                    objBody.put("id",IdUtil.getId());
                    if(i<rankArray.size()){
                        objBody.put("userId",idArray[i]);
                        objBody.put("score",rankArray.getJSONObject(i).getFloatValue("VALUE_"));
                        objBody.put("standardSort",rankArray.getJSONObject(i).getString("KEY_"));
                        portraitStandardDao.addObject(objBody);
                    }
                }
            }
        }catch (Exception e){
            logger.error("Exception in getAllStandard ", e);
            return ResultUtil.result(false,"同步失败",null);
        }
        return ResultUtil.result(true,"同步成功",null);
    }
    private void getUserId2Name(Map<String, String> userId2UserName) {
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        List<Map<String, String>> userList = standardDao.queryUserList(params);
        if (userList != null && !userList.isEmpty()) {
            for (Map<String, String> oneMap : userList) {
                if (StringUtils.isNotBlank(oneMap.get("userName")) && StringUtils.isNotBlank(oneMap.get("userId"))) {
                    userId2UserName.put(oneMap.get("userId"), oneMap.get("userName"));
                }
            }
        }
    }
    // 通过userId逗号分隔字符串获得userName逗号分隔字符串
    private String toGetUserNameByIds(String userIdStr, Map<String, String> userId2UserName) {
        StringBuilder userNameSb = new StringBuilder();
        if (StringUtils.isBlank(userIdStr)) {
            return "";
        }
        String[] userIdArr = userIdStr.split(",", -1);
        for (String userId : userIdArr) {
            if (userId2UserName.containsKey(userId)) {
                userNameSb.append(userId2UserName.get(userId)).append("，");
            }
        }
        return userNameSb.substring(0, userNameSb.length() - 1);
    }
}
