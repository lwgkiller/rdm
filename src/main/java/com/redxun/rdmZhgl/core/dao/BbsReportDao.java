package com.redxun.rdmZhgl.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

/**
 * @author zhangzhen
 */
@Repository
public interface BbsReportDao {
    /**
     * 获取帖子分类统计
     *
     * @param paramJson
     * @return json
     * */
    List<JSONObject> getBbsType(JSONObject paramJson);
    /**
     * 获取改进提案关键节点数据
     *
     * @param paramJson
     * @return json
     * */
    JSONObject getBbsData(JSONObject paramJson);
    /**
     * 改进提案类发帖数/人（TOP10）
     *
     * @param paramJson
     * @return json
     * */
    List<JSONObject> getBbsGjtaData(JSONObject paramJson);
    /**
     * 发帖数/人（TOP10）
     *
     * @param paramJson
     * @return json
     * */
    List<JSONObject> getBbsPostData(JSONObject paramJson);
}
