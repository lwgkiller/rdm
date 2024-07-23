package com.redxun.xcmgProjectManager.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

@Repository
public interface XcmgProjectConfigDao {
    List<Map<String, Object>> levelDivideList(Map<String, Object> params);

    void updateLevelDivide(JSONObject jsonObject);

    List<Map<String, Object>> standardScoreList(Map<String, Object> params);

    void updateStandardScore(JSONObject jsonObject);

    List<Map<String, Object>> deliveryList(Map<String, Object> params);

    void delDelivery(String id);

    List<Map<String, String>> queryStageByCategory(Map<String, Object> params);

    Map<String, String> queryDeliveryById(Map<String, Object> params);

    void saveDelivery(JSONObject jsonObject);

    void updateDelivery(JSONObject jsonObject);

    List<Map<String, Object>> ratingScoreList(Map<String, Object> params);

    void saveRatingScore(JSONObject jsonObject);

    void updateRatingScore(JSONObject jsonObject);

    void delRatingScore(String ratingId);

    List<Map<String, Object>> memRoleRatioList(Map<String, Object> params);

    void updatememRoleRatio(JSONObject jsonObject);

    List<Map<String, Object>> memRoleRankList(Map<String, Object> params);

    void updatememRoleRank(JSONObject jsonObject);

    /**
     * 获取项目成果类别
     * 
     * @param params
     * @return
     */
    List<Map<String, Object>> achievementTypeList(Map<String, Object> params);

    /**
     * 添加项目成果类别
     * 
     * @param jsonObject
     */
    void saveAchievementType(JSONObject jsonObject);

    /**
     * 修改项目成果类别
     * 
     * @param jsonObject
     */
    void updateAchievementType(JSONObject jsonObject);

    /**
     * 删除项目成果类别
     * 
     * @param id
     */
    void delAchievementType(String id);

    /**
     * 获取文件审批流程
     * 
     * @return
     */
    List<Map<String, Object>> getBpmSolutions();

    // 通过项目id和交付物类型名称查询交付物类型信息
    List<JSONObject> queryDeliveryByNameAndProjectId(Map<String, Object> param);
}
