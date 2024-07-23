package com.redxun.portrait.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

/**
 * @author zhangzhen
 */
@Repository
public interface PortraitAsyncRewardDao {
    /**
     * 查询市级及以上科技进步奖
     *
     * @return List
     * */
    List<JSONObject> getCityRewardList();

    /**
     * 更新
     *
     * @param map
     * */
    void updateCityReward(Map<String,Object> map);
    /**
     * 查询集团科技进步奖
     *
     * @return List
     * */
    List<JSONObject> getGroupRewardList();

    /**
     * 更新集团科技进步奖
     *
     * @param map
     * */
    void updateGroupReward(Map<String,Object> map);
    /**
     * 查询科技计划项目
     *
     * @return List
     * */
    List<JSONObject> getScienceRewardList();

    /**
     * 更新科技计划项目
     *
     * @param map
     * */
    void updateScienceReward(Map<String,Object> map);
    /**
     * 查询专利奖
     *
     * @return List
     * */
    List<JSONObject> getPatentRewardList();

    /**
     * 更新专利奖
     *
     * @param map
     * */
    void updatePatentReward(Map<String,Object> map);
    /**
     * 查询高品
     *
     * @return List
     * */
    List<JSONObject> getHeightRewardList();

    /**
     * 更新高品
     *
     * @param map
     * */
    void updateHeightReward(Map<String,Object> map);
    /**
     * 查询新产品
     *
     * @return List
     * */
    List<JSONObject> getNewProductRewardList();

    /**
     * 更新新产品
     *
     * @param map
     * */
    void updateNewProductReward(Map<String,Object> map);
    /**
     * 查询管理奖
     *
     * @return List
     * */
    List<JSONObject> getManageRewardList();

    /**
     * 更新管理奖
     *
     * @param map
     * */
    void updateManageReward(Map<String,Object> map);
    /**
     * 查询人才奖
     *
     * @return List
     * */
    List<JSONObject> getTalentRewardList();

    /**
     * 更新人才奖
     *
     * @param map
     * */
    void updateTalentReward(Map<String,Object> map);
    /**
     * 查询其他奖
     *
     * @return List
     * */
    List<JSONObject> getOtherRewardList();

    /**
     * 更新其他奖
     *
     * @param map
     * */
    void updateOtherReward(Map<String,Object> map);
    /**
     * 删除奖项信息
     *
     * @param param
     * */
    void delRewardInfoByFromId(Map<String, Object> param);
}
