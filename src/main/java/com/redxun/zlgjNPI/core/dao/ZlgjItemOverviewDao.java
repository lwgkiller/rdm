package com.redxun.zlgjNPI.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 质量改进项目总览-报表的数据传输层
 * 
 * @author douhongli
 * @date 2021年6月5日14:18:05
 */
@Repository
public interface ZlgjItemOverviewDao {
    // 按照月份和其他条件查询
    List<JSONObject> selectListByMonth(JSONObject paramObj);

    /**
     * 根据日期查询各部门已完成和运行中的需改进的问题
     * 
     */
    List<JSONObject> selectByDate(JSONObject paramObj);

    /**
     * 根据日期查询各部门ABC的需改进的问题
     *
     */
    List<JSONObject> selectByDateType(JSONObject paramObj);



    //各月份ABC问题完成情况
    List<JSONObject> selectDataByMonth(JSONObject paramObj);

    /**
     * 根据问题类型和时间段查询完成率
     * 
     * @param paramObj
     *            参数
     * @return List
     * @author douhongli
     * @since 2021年6月8日15:57:31
     */
    List<JSONObject> selectByWtlxs(JSONObject paramObj);

    // 超期
    List<JSONObject> queryZrrTime(Map<String, Object> param);

    //2022-10-27增加
    List<JSONObject> selectListByDay(JSONObject paramObj);
}
