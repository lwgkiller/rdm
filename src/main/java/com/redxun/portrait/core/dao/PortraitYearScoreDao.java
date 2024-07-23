package com.redxun.portrait.core.dao;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Map;

/**
 * @author zhangzhen
 */
@Repository
public interface PortraitYearScoreDao {

    /**
     * 查询年度绩效列表
     * */
    List<JSONObject> query(Map<String,Object> paramMap);

    /**
     * 批量导入
     *
     * @param paramMap
     * */
    void batchInsertOrgScore(Map<String,Object> paramMap);
    /**
     * 根据年份删除
     *
     * @param reportYear
     * */
    void delByYear(String reportYear);

    /**
     * 获取排名
     *
     * @param paramJson
     * @return list
     * */
    List<JSONObject> getScoreRank(JSONObject paramJson);
    /**
     * 更新
     *
     * @param jsonObject
     * */
    void updateYearScore(JSONObject jsonObject);

    /**
     * 计算总得分
     *
     * @param reportYear
     * */
    void calculateTotalScore(String reportYear);
}
