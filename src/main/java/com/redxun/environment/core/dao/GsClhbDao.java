package com.redxun.environment.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface GsClhbDao {
    List<Map<String, Object>> queryCx(Map<String, Object> params);

    void deleteCx(Map<String, Object> param);

    void insertCx(JSONObject messageObj);


    void updateCx(JSONObject messageObj);


    void updateFdjNum(Map<String, Object> param);


    JSONObject queryCxById(String rapId);

    List<JSONObject> countFdjNumber(Map<String, Object> param);

    void addFileInfos(JSONObject fileInfo);


    int countCxList(Map<String, Object> param);


    List<JSONObject> queryCxFileList(Map<String, Object> param);


    void deleteCxFile(Map<String, Object> param);


    void updateNumber(JSONObject messageObj);


    JSONObject queryMaxClhbNum(Map<String, Object> param);


    List<JSONObject> queryMsgInfo();

    List<JSONObject> queryMaxFdjNumber(Map<String, Object> param);


    JSONObject queryOldCxById(String cxId);

    List<JSONObject> queryApiList(String searchValue);


    void statusChange(JSONObject param);

    JSONObject queryFileById(String fileId);

    List<JSONObject> getEnvironmentByMaterial(JSONObject paramJson);

    List<JSONObject> gsEnvProductInfo(JSONObject param);
}
