package com.redxun.portrait.core.manager;

import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.redxun.standardManager.core.util.ResultUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.util.StringUtil;
import com.redxun.info.core.util.HttpRequestUtil;
import com.redxun.portrait.core.dao.PortraitAttendanceDao;
import com.redxun.portrait.core.dao.PortraitDocumentDao;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.sys.org.dao.OsUserDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

/**
 * @author zhangzhen
 */
@Service
public class PortraitAttendanceManager {
    private static final Logger logger = LoggerFactory.getLogger(PortraitAttendanceManager.class);
    @Resource
    PortraitAttendanceDao portraitAttendanceDao;
    @Autowired
    OsUserDao osUserDao;
    @Resource
    PortraitDocumentDao portraitDocumentDao;
    @Resource
    CommonInfoManager commonInfoManager;
    @Resource
    CommonInfoDao commonInfoDao;
    public JsonPageResult<?> query(HttpServletRequest request) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            Map<String, Object> params = new HashMap<>(16);
            List<Map<String, Object>> list = new ArrayList<>();
            List<Map<String, Object>> totalList = new ArrayList<>();
            params = CommonFuns.getSearchParam(params, request, true);

            List<String> idList = new ArrayList<>();
            //用户权限
            JSONObject resultJson = commonInfoManager.hasPermission("YGHXZGLY");
            if("admin".equals(ContextUtil.getCurrentUser().getUserNo())) {
                Map<String,String> deptMap=commonInfoManager.queryDeptUnderJSZX();
                idList.addAll(deptMap.keySet());
            }else{
                if(resultJson.getBoolean(RdmConst.ALLDATA_QUERY_KEY)||resultJson.getBoolean("HX-GLY")){
                    //分管领导看所有数据
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

            list = portraitAttendanceDao.query(params);
            params = new HashMap<>(16);
            params = CommonFuns.getSearchParam(params, request, false);
            params.put("ids", idList);
            if(!resultJson.getBoolean(RdmConst.ALLDATA_QUERY_KEY)&&!resultJson.getBoolean("isLeader")&&!resultJson.getBoolean("HX-GLY")
                    &&!resultJson.getBoolean("YGHXZGLY")&&!"admin".equals(ContextUtil.getCurrentUser().getUserNo())){
                params.put("userId",ContextUtil.getCurrentUserId());
            }
            totalList = portraitAttendanceDao.query(params);
            CommonFuns.convertDate(list);
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
                if (mapKey.equals("yearMonth")) {
                    if (mapValue != null && !"".equals(mapValue)) {
                        mapValue = CommonFuns.convertDateToStr(new Date(mapValue), "yyyy-MM");
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
                portraitAttendanceDao.addObject(objBody);
            }
        } catch (Exception e) {
            logger.error("Exception in add 添加出勤异常！", e);
            return ResultUtil.result(false, "添加出勤异常！", "");
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
                if (mapKey.equals("yearMonth")) {
                    if (mapValue != null && !"".equals(mapValue)) {
                        mapValue = CommonFuns.convertDateToStr(new Date(mapValue), "yyyy-MM");
                    }
                }
                objBody.put(mapKey, mapValue);
            }
            objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            portraitAttendanceDao.updateObject(objBody);
        } catch (Exception e) {
            logger.error("Exception in update 更新出勤异常", e);
            return ResultUtil.result(false, "更新出勤异常！", "");
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
            portraitAttendanceDao.batchDelete(params);
        } catch (Exception e) {
            logger.error("Exception in update 删除出勤", e);
            return ResultUtil.result(false, "删除出勤异常！", "");
        }
        return ResultUtil.result(true, "删除成功", resultJson);
    }
    public JSONObject getObjectById(String id){
        return portraitAttendanceDao.getObjectById(id);
    }
    public List<Map<String,Object>> getPersonAttendanceList(JSONObject jsonObject){
        List<Map<String,Object>> list = portraitAttendanceDao.getPersonAttendanceList(jsonObject);
        CommonFuns.convertDate(list);
        return list;
    }


    /**
     * 同步考勤数据
     * */
    public JSONObject asyncAttendance(String yearMonth){
        JSONObject paramJson = new JSONObject();
        try{
            paramJson.put("st",39);
            paramJson.put("month",yearMonth);
            String certNos = getCertNos();
            paramJson.put("ssn",certNos);
            String portUrl = portraitDocumentDao.getPortraitConfig("attendancePort");
            JSONObject resultJson = HttpRequestUtil.doPost(portUrl, paramJson);
            Map<String, String> userCertNo2UserId = new HashMap<>(16);
            getUserCert2Id(userCertNo2UserId);
            if ("001".equals(resultJson.getString("state"))) {
                JSONArray resultArray = resultJson.getJSONArray("data");
                Map<String, Object> objBody ;
                JSONObject temp;
                List<Map<String, Object>> dataInsert = new ArrayList<>();
                if(resultArray!=null&&resultArray.size()>0){
                    //先清空数据
                    portraitAttendanceDao.delByMonth(yearMonth);
                    for(int i=0;i<resultArray.size();i++){
                        temp = resultArray.getJSONObject(i);
                        objBody = new HashMap<>(16);
                        //先判断 根据身份证是否能找到用户
                        String userId = userCertNo2UserId.get(temp.getString("ssn"));
                        if(StringUtil.isEmpty(userId)){
                            continue;
                        }
                        objBody.put("id",IdUtil.getId());
                        objBody.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                        objBody.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                        objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                        objBody.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                        objBody.put("userId",userId);
                        objBody.put("yearMonth",yearMonth);
                        objBody.put("overTime",temp.getFloatValue("overTime"));
                        objBody.put("overDay",temp.getFloatValue("overTime")/(60*8));
                        dataInsert.add(objBody);
                    }
                    // 分批写入数据库(20条一次)
                    List<Map<String, Object>> tempInsert = new ArrayList<>();
                    for (int i = 0; i < dataInsert.size(); i++) {
                        tempInsert.add(dataInsert.get(i));
                        if (tempInsert.size() % 20 == 0) {
                            portraitAttendanceDao.batchInsertOrgData(tempInsert);
                            tempInsert.clear();
                        }
                    }
                    if (!tempInsert.isEmpty()) {
                        portraitAttendanceDao.batchInsertOrgData(tempInsert);
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
        String overLine = portraitDocumentDao.getPortraitConfig("overLine");
        portraitAttendanceDao.delAttendanceByMonth(yearMonth);
        dealTopData(yearMonth,overLine);
        dealDownData(yearMonth,overLine);
        return ResultUtil.result(true,"同步成功",null);
    }
    public String getCertNos(){
        StringBuffer certNos = new StringBuffer();
        try{
            Map<String,String> deptMap=commonInfoManager.queryDeptUnderJSZX();
            List<String> idList = new ArrayList<>(deptMap.keySet());
            Map<String,Object> params = new HashMap(16);
            params.put("ids", idList);
            List<JSONObject> list = portraitAttendanceDao.getUserByDeptId(params);
            if(list!=null && list.size()>0){
                for(JSONObject temp:list){
                    if(!CommonFuns.nullToString(temp.getString("CERT_NO_")).isEmpty()){
                        certNos.append(temp.getString("CERT_NO_")).append(",");
                    }
                }
            }
            certNos.deleteCharAt(certNos.length() - 1);
        }catch (Exception e){
            logger.error("获取人员身份证异常",e);
        }
        return certNos.toString();
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
    /**
     * 处理基准线上的数据
     * N≤80%  0.4
     * 80%＜N≤90% 0.45
     * 90%＜N≤100% 0.5
     * */
    public void dealTopData(String yearMonth,String overLine){
        try{
            JSONObject paramJson = new JSONObject();
            paramJson.put("yearMonth",yearMonth);
            paramJson.put("overDay",overLine);
            List<JSONObject> list = portraitAttendanceDao.getTopUserList(paramJson);
            List<Map<String, Object>> dataInsert = new ArrayList<>();
            Map<String,Object> objBody;
            if(list!=null && list.size()>0){
                int total = list.size();
                float score = 0;
                String attendanceRank = "";
                //90%＜N≤100% 0.5 人数
                int first = (int)Math.round(total*0.8)-1;
                int second = (int)Math.round(total*0.9)-1;
                for(int i=0;i<list.size();i++){
                    if(i<=first){
                        score = 0.4f;
                        attendanceRank = "third";
                    }else if(i>first&&i<=second){
                        score = 0.45f;
                        attendanceRank = "second";
                    }else{
                        score = 0.5f;
                        attendanceRank = "first";
                    }
                    objBody = new HashMap<>(16);
                    objBody.put("id", IdUtil.getId());
                    objBody.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    objBody.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                    objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    objBody.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                    objBody.put("userId", list.get(i).getString("userId"));
                    objBody.put("yearMonth", list.get(i).getString("yearMonth"));
                    objBody.put("attendanceRank",attendanceRank);
                    objBody.put("score", score);
                    dataInsert.add(objBody);
                }
                // 分批写入数据库(20条一次)
                List<Map<String, Object>> tempInsert = new ArrayList<>();
                for (int i = 0; i < dataInsert.size(); i++) {
                    tempInsert.add(dataInsert.get(i));
                    if (tempInsert.size() % 20 == 0) {
                        portraitAttendanceDao.batchInsertAttendance(tempInsert);
                        tempInsert.clear();
                    }
                }
                if (!tempInsert.isEmpty()) {
                    portraitAttendanceDao.batchInsertAttendance(tempInsert);
                    tempInsert.clear();
                }
            }
        }catch (Exception e){
            logger.error("处理基准线上数据异常",e);
        }
    }
    /**
     * 处理基准线下的数据
     * 0＜N≤10%
     * 10%＜N≤30%
     * 30%＜N≤60%
     * 60%＜N≤100%
     * */
    public void dealDownData(String yearMonth,String overLine){
        try{
            JSONObject paramJson = new JSONObject();
            paramJson.put("yearMonth",yearMonth);
            paramJson.put("overDay",overLine);
            List<JSONObject> list = portraitAttendanceDao.getDownUserList(paramJson);
            List<Map<String, Object>> dataInsert = new ArrayList<>();
            Map<String,Object> objBody;
            if(list!=null && list.size()>0){
                int total = list.size();
                float score = 0;
                String attendanceRank = "";
                int first = (int)Math.round(total*0.1)-1;
                int second = (int)Math.round(total*0.3)-1;
                int third = (int)Math.round(total*0.6)-1;
                for(int i=0;i<list.size();i++){
                    if(i<=first){
                        score = 0;
                        attendanceRank = "seven";
                    }else if(i>first&&i<=second){
                        score = 0.1f;
                        attendanceRank = "sixty";
                    }else if(i>second&&i<=third){
                        score = 0.2f;
                        attendanceRank = "fifty";
                    }else{
                        score = 0.3f;
                        attendanceRank = "forth";
                    }
                    objBody = new HashMap<>(16);
                    objBody.put("id", IdUtil.getId());
                    objBody.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    objBody.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                    objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    objBody.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                    objBody.put("userId", list.get(i).getString("userId"));
                    objBody.put("yearMonth", list.get(i).getString("yearMonth"));
                    objBody.put("attendanceRank",attendanceRank);
                    objBody.put("score", score);
                    dataInsert.add(objBody);
                }
                // 分批写入数据库(20条一次)
                List<Map<String, Object>> tempInsert = new ArrayList<>();
                for (int i = 0; i < dataInsert.size(); i++) {
                    tempInsert.add(dataInsert.get(i));
                    if (tempInsert.size() % 20 == 0) {
                        portraitAttendanceDao.batchInsertAttendance(tempInsert);
                        tempInsert.clear();
                    }
                }
                if (!tempInsert.isEmpty()) {
                    portraitAttendanceDao.batchInsertAttendance(tempInsert);
                    tempInsert.clear();
                }
            }
        }catch (Exception e){
            logger.error("处理基准线下数据异常",e);
        }
    }
}
