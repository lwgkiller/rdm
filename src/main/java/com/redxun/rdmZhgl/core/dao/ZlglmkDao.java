package com.redxun.rdmZhgl.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@Repository
public interface ZlglmkDao {
    List<JSONObject> queryZgzlList(Map<String, Object> param);

    List<JSONObject> queryZgzlEXCList(Map<String, Object> param);

    List<JSONObject> queryGjzlList(Map<String, Object> param);

    List<JSONObject> queryGjzlEXCList(Map<String, Object> param);

    List<JSONObject> queryFYId(Map<String, Object> param);

    int countZgzlList(Map<String, Object> param);

    int countGjzlList(Map<String, Object> param);

    List<Map<String, Object>> queryZgzlId(Map<String, Object> params);

    List<Map<String, Object>> queryGjzlId(Map<String, Object> params);

    List<JSONObject> enumList(JSONObject jsonObject);

    void addFileInfos(JSONObject map);

    void addfyFileInfos(JSONObject map);

    JSONArray getFiles(Map<String, Object> params);

    JSONArray getfyFiles(Map<String, Object> params);

    List<JSONObject> getJiaoFeiList(JSONObject jsonObject);

    void insertJiaoFeiData(JSONObject meetingPlanObj);

    void updateJiaoFeiData(JSONObject meetingPlanObj);

    void insertZgzlData(JSONObject param);

    void insertGjzlData(JSONObject param);

    void updateZgzlData(JSONObject param);

    void updateGjzlData(JSONObject param);

    void deleteFileByIds(Map<String, Object> params);

    void deleteJfpjFileByIds(Map<String, Object> params);

    List<JSONObject> queryZlglFileList(Map<String, Object> param);

    List<JSONObject> queryJiaoFeiFileList(Map<String, Object> param);

    void deleteZlglFile(Map<String, Object> param);

    void deleteJiaoFeiFile(Map<String, Object> param);

    void deleteZgzl(Map<String, Object> param);

    void deleteJiaoFei(Map<String, Object> param);

    void deleteGjzl(Map<String, Object> param);

    void insertZgzlDataExc(Map<String, Object> param);

    List<JSONObject> getEnumList(JSONObject jsonObject);

    List<JSONObject> queryDeptInfos();

    List<JSONObject> getUserDeptInfoById(Map<String, Object> param);

    void insertZgzlByJsjds(JSONObject param);

    JSONObject getPatentById(String zgzlId);

    void updatePatentAsyncStatus(String zgzlId);

    void updateLastUpdateTime(String param );

    void updateJdsProject(JSONObject param);

    List<JSONObject> checkProject(JSONObject jsonObject);
}
