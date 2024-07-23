package com.redxun.environment.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ZjhmDao {

    List<JSONObject> queryZjhm(Map<String, Object> params);

    int countZjhmList(Map<String, Object> param);

    JSONObject queryZjhmById(String projectId);

    void deleteZjhmFile(Map<String, Object> param);

    void deleteZjhm(Map<String, Object> param);

    void createZjhm(JSONObject messageObj);

    void updateZjhm(JSONObject messageObj);

    void delTaskData(String instId);

    List<JSONObject> getZjhmFileList(Map<String, Object> params);

    void addFileInfos(JSONObject fileInfo);

    void deleteFileById(String id);
}
