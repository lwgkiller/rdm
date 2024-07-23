package com.redxun.portrait.core.manager;

import java.text.DecimalFormat;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.redxun.core.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.portrait.core.dao.PortraitDocumentDao;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.standardManager.core.util.ResultUtil;

/**
 * @author zhangzhen
 */
@Service
public class PortraitDocumentManager {
    private static final Logger logger = LoggerFactory.getLogger(PortraitDocumentManager.class);
    @Resource
    PortraitDocumentDao portraitDocumentDao;
    @Resource
    CommonInfoManager commonInfoManager;


    public JSONArray getUserList(HttpServletRequest request){
        JSONArray resultArray = new JSONArray();
        try {
            Map<String, Object> params = new HashMap<>(16);
            params = CommonFuns.getSearchParam(params, request, true);
            String userIds = CommonFuns.nullToString(params.get("userIds"));
            if(!StringUtil.isEmpty(userIds)){
                String []userIdArray = userIds.split(",",-1);
                List<String> userIdList = Arrays.asList(userIdArray);
                params.put("userIds",userIdList);
            }
            List<String> idList = new ArrayList<>();
            //用户权限
            if("admin".equals(ContextUtil.getCurrentUser().getUserNo())){
                Map<String,String> deptMap=commonInfoManager.queryDeptUnderJSZX();
                idList.addAll(deptMap.keySet());
            }else{
                JSONObject resultJson = commonInfoManager.hasPermission("YGHXZGLY");
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

            resultArray = portraitDocumentDao.getUserList(params);
        } catch (Exception e) {
            logger.error("Exception in getUserList ", e);
         }
        return resultArray;
    }

    public JSONObject getPersonScore(HttpServletRequest request){
            JSONObject resultJson = new JSONObject();
        try {
            String userId = request.getParameter("userId");
            String reportYear = request.getParameter("reportYear");
            JSONObject paramJson = new JSONObject();
            paramJson.put("userId",userId);
            paramJson.put("reportYear",reportYear);
            resultJson = portraitDocumentDao.getPersonScore(paramJson);
        } catch (Exception e) {
            logger.error("Exception in getUserList ", e);
            return ResultUtil.result(false,"获取个人分数异常",null);
        }
        return ResultUtil.result(true,"获取成功",resultJson);
    }
    public JSONArray getCompareScore(HttpServletRequest request){
        JSONArray resultJson = new JSONArray();
        try {
            String userId1 = request.getParameter("userId1");
            String userId2 = request.getParameter("userId2");
            String reportYear = request.getParameter("reportYear");
            JSONObject paramJson1 = new JSONObject();
            JSONObject paramJson2 = new JSONObject();
            paramJson1.put("userId",userId1);
            paramJson2.put("userId",userId2);
            paramJson1.put("reportYear",reportYear);
            paramJson2.put("reportYear",reportYear);
            JSONObject jsonObject1 = portraitDocumentDao.getPersonScore(paramJson1);
            JSONObject jsonObject2 = portraitDocumentDao.getPersonScore(paramJson2);
            JSONObject jsonObject3 = new JSONObject();
            jsonObject3.put("userName","项差");
            jsonObject3.put("projectScore",new DecimalFormat("#.##").format((jsonObject1.getFloatValue("projectScore")-jsonObject2.getFloatValue("projectScore"))));
            jsonObject3.put("standardScore",new DecimalFormat("#.##").format(jsonObject1.getFloatValue("standardScore")-jsonObject2.getFloatValue("standardScore")));
            jsonObject3.put("knowledgeScore",new DecimalFormat("#.##").format(jsonObject1.getFloatValue("knowledgeScore")-jsonObject2.getFloatValue("knowledgeScore")));
            jsonObject3.put("rewardScore",new DecimalFormat("#.##").format(jsonObject1.getFloatValue("rewardScore")-jsonObject2.getFloatValue("rewardScore")));
            jsonObject3.put("secretScore",new DecimalFormat("#.##").format(jsonObject1.getFloatValue("secretScore")-jsonObject2.getFloatValue("secretScore")));
            jsonObject3.put("technologyScore",new DecimalFormat("#.##").format(jsonObject1.getFloatValue("technologyScore")-jsonObject2.getFloatValue("technologyScore")));
            jsonObject3.put("bbsScore",new DecimalFormat("#.##").format(jsonObject1.getFloatValue("bbsScore")-jsonObject2.getFloatValue("bbsScore")));
            jsonObject3.put("patentReadScore",new DecimalFormat("#.##").format(jsonObject1.getFloatValue("patentReadScore")-jsonObject2.getFloatValue("patentReadScore")));
            jsonObject3.put("informationScore",new DecimalFormat("#.##").format(jsonObject1.getFloatValue("informationScore")-jsonObject2.getFloatValue("informationScore")));
            jsonObject3.put("analysisImproveScore",new DecimalFormat("#.##").format(jsonObject1.getFloatValue("analysisImproveScore")-jsonObject2.getFloatValue("analysisImproveScore")));
            jsonObject3.put("contractScore",new DecimalFormat("#.##").format(jsonObject1.getFloatValue("contractScore")-jsonObject2.getFloatValue("contractScore")));
            jsonObject3.put("attendanceScore",new DecimalFormat("#.##").format(jsonObject1.getFloatValue("attendanceScore")-jsonObject2.getFloatValue("attendanceScore")));
            jsonObject3.put("notificationScore",new DecimalFormat("#.##").format(jsonObject1.getFloatValue("notificationScore")-jsonObject2.getFloatValue("notificationScore")));
            jsonObject3.put("performanceScore",new DecimalFormat("#.##").format(jsonObject1.getFloatValue("performanceScore")-jsonObject2.getFloatValue("performanceScore")));
            jsonObject3.put("courseScore",new DecimalFormat("#.##").format(jsonObject1.getFloatValue("courseScore")-jsonObject2.getFloatValue("courseScore")));
            jsonObject3.put("cultureScore",new DecimalFormat("#.##").format(jsonObject1.getFloatValue("cultureScore")-jsonObject2.getFloatValue("cultureScore")));
            resultJson.add(jsonObject1);
            resultJson.add(jsonObject2);
            resultJson.add(jsonObject3);
        } catch (Exception e) {
            logger.error("Exception in getUserList ", e);
        }
        return resultJson;
    }

}
