package com.redxun.environment.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ClhbDao {
    List<Map<String, Object>> queryWj(Map<String, Object> params);

    void deleteWj(Map<String, Object> param);

    void insertWj(JSONObject messageObj);


    void updateWj(JSONObject messageObj);


    JSONObject queryWjById(String wjId);


    JSONObject queryOldWjById(String wjId);


    JSONObject queryFileById(String fileId);


    void addFileInfos(JSONObject fileInfo);


    int countWjList(Map<String, Object> param);


    List<JSONObject> queryWjFileList(Map<String, Object> param);


    void deleteWjFile(Map<String, Object> param);


    void updateNumber(JSONObject messageObj);


    JSONObject queryMaxClhbNum(Map<String, Object> param);


    List<JSONObject> queryMsgInfo();


    List<JSONObject> queryBox(Map<String, Object> param);

    List<JSONObject> queryApiList(String searchValue);

    void statusChange(JSONObject param);

}
