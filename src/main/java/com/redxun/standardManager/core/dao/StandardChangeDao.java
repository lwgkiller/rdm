package com.redxun.standardManager.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface StandardChangeDao {
    List<Map<String, Object>> queryStandardChange(Map<String, Object> params);

    void deleteStandard(Map<String, Object> param);

    void insertstandardChange(JSONObject messageObj);


    void updatestandardChange(JSONObject messageObj);


    JSONObject querystandardChangeById(String standardId);



    void addFileInfos(JSONObject fileInfo);


    int countJsglList(Map<String, Object> param);


    List<JSONObject> querystandardChangeFileList(Map<String, Object> param);


    void deletestandardChangeFile(Map<String, Object> param);


    void deletestandardChange(Map<String, Object> param);

    void cancelJstb(Map<String, Object> param);

    List<JSONObject> jSXZtoDep(Map<String, Object> param);


    List<JSONObject> isJSGLB(Map<String, Object> param);
}
