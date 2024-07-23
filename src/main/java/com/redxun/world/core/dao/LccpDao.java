package com.redxun.world.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface LccpDao {
    List<JSONObject> queryLccp(Map<String, Object> params);

    void createLccp(JSONObject messageObj);

    void updateLccp(JSONObject messageObj);

    JSONObject queryLccpById(String qbgzId);

    void addFileInfos(JSONObject fileInfo);

    int countLccpList(Map<String, Object> param);

    List<JSONObject> queryLccpFileList(Map<String, Object> param);

    List<JSONObject> queryLccpFileLists(Map<String, Object> param);

    void deleteLccpFile(Map<String, Object> param);

    void deleteLccp(Map<String, Object> param);

    List<Map<String, Object>> queryCountryList(JSONObject param);

}
