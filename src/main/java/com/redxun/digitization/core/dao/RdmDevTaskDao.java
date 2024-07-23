package com.redxun.digitization.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

@Repository
public interface RdmDevTaskDao {
    void createRdmDevTask(JSONObject params);

    void updateRdmDevTask(JSONObject params);

    List<JSONObject> queryDevTaskFileList(Map<String, Object> param);

    JSONObject getDevInfoById(String applyId);

    void addDevTaskFileInfos(JSONObject fileInfo);

    List<JSONObject> queryDevTaskList(Map<String, Object> param);

    int countDevTaskFileList(Map<String, Object> param);

    void deleteDevTaskFile(Map<String, Object> param);

    void deleteDevTask(Map<String, Object> param);

    void updateRdmDevApplyNum(Map<String, Object> param);
}
