package com.redxun.portrait.core.dao;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

/**
 * @author zhangzhen
 */
@Repository
public interface PortraitDocumentDao {
    /**
     * 查询
     *
     * @param params
     * @return List
     * */
    List<Map<String, Object>> query(Map<String, Object> params);

    /**
     * 获取基本信息
     *
     * @param userId
     * @return json
     * */
    JSONObject getUserInfoById(String userId);
    /**
     *获取配置信息
     *
     * @param configKey
     * @return string
     * */
    String getPortraitConfig(String configKey);
    /**
     * 获取用户列表
     *
     * @param map
     * @return json
     * */
    JSONArray getUserList(Map<String, Object> map);
    /**
     * 三高一可
     *
     * @param map
     * @return float
     * */
    Float getSGYKScore(Map<String, Object> map);
    /**
     * 科技项目
     *
     * @param map
     * @return float
     * */
    Float getProjectScore(Map<String, Object> map);
    /**
     * 标准
     *
     * @param map
     * @return float
     * */
    Float getStandardScore(Map<String, Object> map);
    /**
     * 知识产权
     *
     * @param map
     * @return float
     * */
    Float getKnowledgeScore(Map<String, Object> map);
    /**
     * 协同事项
     *
     * @param map
     * @return float
     * */
    Float getTeamWorkScore(Map<String, Object> map);
    /**
     * 考勤
     *
     * @param map
     * @return float
     * */
    Float getAttentionScore(Map<String, Object> map);
    /**
     * 通报
     *
     * @param map
     * @return float
     * */
    Float getNotificationScore(Map<String, Object> map);
    /**
     * 月度绩效
     *
     * @param map
     * @return float
     * */
    Float getPerformanceScore(Map<String, Object> map);
    /**
     * 荣誉奖项
     *
     * @param map
     * @return float
     * */
    Float getRewardScore(Map<String, Object> map);
    /**
     * 招标规划
     *
     * @param map
     * @return float
     * */
    Float getBidPlanScore(Map<String, Object> map);
    /**
     * 培训课程
     *
     * @param map
     * @return float
     * */
    Float getCourseScore(Map<String, Object> map);
    /**
     * 导师培养
     *
     * @param map
     * @return float
     * */
    Float getCultureScore(Map<String, Object> map);
    /**
     * 获取个人各项得分
     *
     * @param jsonObject
     * @return float
     * */
    JSONObject getPersonScore(JSONObject jsonObject);
}
