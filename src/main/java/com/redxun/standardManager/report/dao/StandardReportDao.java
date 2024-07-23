package com.redxun.standardManager.report.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

@Repository
public interface StandardReportDao {
    List<Map<String,Object>> queryPublishChart(Map<String,Object> params);

    List<Map<String, Object>> queryCategoryChart(Map<String,Object> params);

    List<Map<String, Object>> queryStandardCheckChart(Map<String,Object> params);

    List<Map<String, Object>> queryDepCheckStandardChart(Map<String,Object> params);

    List<Map<String, Object>> groupByPublish(Map<String,Object> params);

    List<Map<String, Object>> groupByPublishCount(Map<String,Object> params);

    List<Map<String, Object>> groupBySystem(Map<String,Object> params);

    List<Map<String, Object>> groupBySystemCount(Map<String,Object> params);

    List<Map<String, Object>> groupByProject(Map<String,Object> params);

    Integer groupByProjectCount(Map<String,Object> params);

}
