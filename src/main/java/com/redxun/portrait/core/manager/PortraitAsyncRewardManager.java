package com.redxun.portrait.core.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.redxun.core.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.portrait.core.dao.PortraitAsyncRewardDao;
import com.redxun.portrait.core.dao.PortraitRewardDao;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;


/**
 * @author zhangzhen
 */
@Service
public class PortraitAsyncRewardManager {
    private static final Logger logger = LoggerFactory.getLogger(PortraitAsyncRewardManager.class);
    @Resource
    PortraitAsyncRewardDao portraitAsyncRewardDao;
    @Resource
    PortraitRewardDao portraitRewardDao;
    public void asyncCityReward(){
        try {
            List<JSONObject> list = portraitAsyncRewardDao.getCityRewardList();
            Map<String, Object> objBody = new HashMap<>(16);
            for(JSONObject temp:list){
                String rewardName = "市级及以上科技进步奖："+temp.getString("honor");
                String userIds = temp.getString("portrayal_point_person_id");
                objBody.put("rewardName", rewardName);
                String rewardLevel = temp.getString("award_type");
                objBody.put("rewardLevel", rewardLevel);
                double levelRate = 1;
                if("gjj".equals(rewardLevel)){
                    levelRate = 1.2;
                }
                objBody.put("userIds", userIds);
                objBody.put("fromId", temp.getString("id"));
                objBody.put("rewardYear", temp.getString("rewardYear"));
                splitRewardUser(objBody,levelRate);
                portraitAsyncRewardDao.updateCityReward(objBody);
            }
        }catch (Exception e){
            logger.error("Exception in add asyncCityReward！", e);
        }
    }
    public void asyncGroupReward(){
        try {
            List<JSONObject> list = portraitAsyncRewardDao.getGroupRewardList();
            Map<String, Object> objBody = new HashMap<>(16);
            for(JSONObject temp:list){
                String rewardName = "集团科技进步奖："+temp.getString("honor");
                String userIds = temp.getString("portrayal_point_person_id");
                objBody.put("rewardName", rewardName);
                String rewardLevel = temp.getString("award_type");
                objBody.put("rewardLevel", rewardLevel);
                double levelRate = 1;
                if("gjj".equals(rewardLevel)){
                    levelRate = 1.2;
                }
                objBody.put("userIds", userIds);
                objBody.put("fromId", temp.getString("id"));
                objBody.put("rewardYear", temp.getString("rewardYear"));
                splitRewardUser(objBody,levelRate);
                portraitAsyncRewardDao.updateGroupReward(objBody);
            }
        }catch (Exception e){
            logger.error("Exception in add asyncGroupReward！", e);
        }
    }
    public void asyncScienceReward(){
        try {
            List<JSONObject> list = portraitAsyncRewardDao.getScienceRewardList();
            Map<String, Object> objBody = new HashMap<>(16);
            for(JSONObject temp:list){
                String rewardName = "科技计划项目："+temp.getString("project_name");
                String userIds = temp.getString("portrayal_point_person_id");
                objBody.put("rewardName", rewardName);
                String rewardLevel = temp.getString("award_type");
                objBody.put("rewardLevel", rewardLevel);
                double levelRate = 1;
                if("gjj".equals(rewardLevel)){
                    levelRate = 1.2;
                }
                objBody.put("userIds", userIds);
                objBody.put("fromId", temp.getString("id"));
                objBody.put("rewardYear", temp.getString("rewardYear"));
                splitRewardUser(objBody,levelRate);
                portraitAsyncRewardDao.updateScienceReward(objBody);
            }
        }catch (Exception e){
            logger.error("Exception in add asyncScienceReward！", e);
        }
    }
    public void asyncPatentReward(){
        try {
            List<JSONObject> list = portraitAsyncRewardDao.getPatentRewardList();
            Map<String, Object> objBody = new HashMap<>(16);
            for(JSONObject temp:list){
                String rewardName = "专利奖："+temp.getString("honor");
                String userIds = temp.getString("portrayal_point_person_id");
                objBody.put("rewardName", rewardName);
                String rewardLevel = temp.getString("award_type");
                objBody.put("rewardLevel", rewardLevel);
                double levelRate = 1;
                if("gjj".equals(rewardLevel)){
                    levelRate = 1.2;
                }
                objBody.put("userIds", userIds);
                objBody.put("fromId", temp.getString("id"));
                objBody.put("rewardYear", temp.getString("rewardYear"));
                splitRewardUser(objBody,levelRate);
                portraitAsyncRewardDao.updatePatentReward(objBody);
            }
        }catch (Exception e){
            logger.error("Exception in add asyncScienceReward！", e);
        }
    }
    public void asyncHeightReward(){
        try {
            List<JSONObject> list = portraitAsyncRewardDao.getHeightRewardList();
            Map<String, Object> objBody = new HashMap<>(16);
            for(JSONObject temp:list){
                String rewardName = "高品："+temp.getString("honor");
                String userIds = temp.getString("portrayal_point_person_id");
                objBody.put("rewardName", rewardName);
                String rewardLevel = temp.getString("award_type");
                objBody.put("rewardLevel", rewardLevel);
                double levelRate = 1;
                if("gjj".equals(rewardLevel)){
                    levelRate = 1.2;
                }
                objBody.put("userIds", userIds);
                objBody.put("fromId", temp.getString("id"));
                objBody.put("rewardYear", temp.getString("rewardYear"));
                splitRewardUser(objBody,levelRate);
                portraitAsyncRewardDao.updateHeightReward(objBody);
            }
        }catch (Exception e){
            logger.error("Exception in add asyncHeightReward！", e);
        }
    }
    public void asyncNewProductReward(){
        try {
            List<JSONObject> list = portraitAsyncRewardDao.getNewProductRewardList();
            Map<String, Object> objBody = new HashMap<>(16);
            for(JSONObject temp:list){
                String rewardName = "新产品："+temp.getString("honor");
                String userIds = temp.getString("portrayal_point_person_id");
                objBody.put("rewardName", rewardName);
                String rewardLevel = temp.getString("award_type");
                objBody.put("rewardLevel", rewardLevel);
                double levelRate = 1;
                if("gjj".equals(rewardLevel)){
                    levelRate = 1.2;
                }
                objBody.put("userIds", userIds);
                objBody.put("fromId", temp.getString("id"));
                objBody.put("rewardYear", temp.getString("rewardYear"));
                splitRewardUser(objBody,levelRate);
                portraitAsyncRewardDao.updateNewProductReward(objBody);
            }
        }catch (Exception e){
            logger.error("Exception in add asyncNewProductReward！", e);
        }
    }
    public void asyncManageReward(){
        try {
            List<JSONObject> list = portraitAsyncRewardDao.getManageRewardList();
            Map<String, Object> objBody = new HashMap<>(16);
            for(JSONObject temp:list){
                String rewardName = "管理奖："+temp.getString("honor");
                String userIds = temp.getString("portrayal_point_person_id");
                objBody.put("rewardName", rewardName);
                String rewardLevel = temp.getString("award_type");
                objBody.put("rewardLevel", rewardLevel);
                double levelRate = 1;
                if("gjj".equals(rewardLevel)){
                    levelRate = 1.2;
                }
                objBody.put("userIds", userIds);
                objBody.put("fromId", temp.getString("id"));
                objBody.put("rewardYear", temp.getString("rewardYear"));
                splitRewardUser(objBody,levelRate);
                portraitAsyncRewardDao.updateManageReward(objBody);
            }
        }catch (Exception e){
            logger.error("Exception in add asyncManageReward！", e);
        }
    }
    public void asyncTalentReward(){
        try {
            List<JSONObject> list = portraitAsyncRewardDao.getTalentRewardList();
            Map<String, Object> objBody = new HashMap<>(16);
            for(JSONObject temp:list){
                String rewardName = "人才奖："+temp.getString("project_name");
                String userIds = temp.getString("portrayal_point_person_id");
                objBody.put("rewardName", rewardName);
                String rewardLevel = temp.getString("award_type");
                objBody.put("rewardLevel", rewardLevel);
                double levelRate = 1;
                if("gjj".equals(rewardLevel)){
                    levelRate = 1.2;
                }
                objBody.put("userIds", userIds);
                objBody.put("fromId", temp.getString("id"));
                objBody.put("rewardYear", temp.getString("rewardYear"));
                splitRewardUser(objBody,levelRate);
                portraitAsyncRewardDao.updateTalentReward(objBody);
            }
        }catch (Exception e){
            logger.error("Exception in add asyncTalentReward！", e);
        }
    }
    public void asyncOtherReward(){
        try {
            List<JSONObject> list = portraitAsyncRewardDao.getOtherRewardList();
            Map<String, Object> objBody = new HashMap<>(16);
            for(JSONObject temp:list){
                String rewardName = "其他奖："+temp.getString("honor");
                String userIds = temp.getString("portrayal_point_person_id");
                objBody.put("rewardName", rewardName);
                String rewardLevel = temp.getString("award_type");
                objBody.put("rewardLevel", rewardLevel);
                double levelRate = 1;
                if("gjj".equals(rewardLevel)){
                    levelRate = 1.2;
                }
                objBody.put("userIds", userIds);
                objBody.put("fromId", temp.getString("id"));
                objBody.put("rewardYear", temp.getString("rewardYear"));
                splitRewardUser(objBody,levelRate);
                portraitAsyncRewardDao.updateOtherReward(objBody);
            }
        }catch (Exception e){
            logger.error("Exception in add asyncOtherReward！", e);
        }
    }

    public void splitRewardUser( Map<String, Object> objBody,double levelRate){
        try {
            String userIds = CommonFuns.nullToString(objBody.get("userIds"));
            if(StringUtil.isNotEmpty(userIds)){
                String []userArray = userIds.split(",");
                String rewardRank="";
                double score=0;
                for(int i=0;i<userArray.length;i++){
                    if(i<3){
                        rewardRank = "third";
                        score = levelRate*5;
                    }else if(i>=3&&i<5){
                        rewardRank = "five";
                        score = levelRate*3;
                    }else if(i>=5&&i<10){
                        rewardRank = "ten";
                        score = levelRate*2;
                    }else if(i>=10&&i<15){
                        rewardRank = "fifteen";
                        score = levelRate*1;
                    }else{
                        rewardRank = "";
                    }
                    if(StringUtil.isNotEmpty(rewardRank)){
                        objBody.put("id",IdUtil.getId());
                        objBody.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                        objBody.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                        objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                        objBody.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                        objBody.put("rewardRank",rewardRank);
                        objBody.put("score",score);
                        objBody.put("userId",userArray[i]);
                        portraitRewardDao.addObject(objBody);
                    }
                }
            }

        }catch (Exception e){
            logger.error("Exception in add splitRewardUser！", e);
        }
    }

}
