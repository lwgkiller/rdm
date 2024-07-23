package com.redxun.serviceEngineering.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface JxcshcDao {
    //..
    List<Map<String, Object>> jxcshcListQuery(Map <String, Object> params);

    //..
    Integer countJxcshcQuery(Map <String, Object> params);

    void insertJxcshc(Map <String, Object> params);

    void updateJxcshc(JSONObject formDataJson);
    //..
    void deleteJxcshc(Map <String, Object> param);

    JSONObject queryJxcshcById(String id);

    void addFileInfos(JSONObject fileInfo);

    List<JSONObject> queryFileList(Map <String, Object> params);


    void delFileByMasterId(Map <String, Object> param);

    void deleteFileById(String id);

    JSONObject queryCreatorDep(String userId);

    JSONObject queryFileByMasterIdAndFileName(Map <String, Object> params);
}
