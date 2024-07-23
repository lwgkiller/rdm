package com.redxun.strategicPlanning.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface MonthlyDao {
    List<JSONObject> queryMonthlyList(Map<String, Object> param);

    int countMonthlyFileList(Map<String, Object> param);

    List<Map<String, Object>> queryMonthlyFileByName(Map<String, Object> params);


    void createMonthlyFile(Map<String, Object> fileInfo);


    void deleteMonthlyFile(Map<String, Object> param);

}
