package com.redxun.strategicPlanning.core.dao;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface XgZlghDao {
    List<Map<String, Object>> queryZlghList(Map<String, Object> param);
    int countZlghList(Map<String, Object> param);
    Map<String, Object> queryZlghById(Map<String, Object> queryParam);
    void saveZlghList(JSONObject param);
    void updateZlghList(JSONObject param);
    JSONArray getFiles(Map<String, Object> params);
    void addFileInfos(JSONObject map);
    void deleteFileByIds(Map<String, Object> params);
    @Select("SELECT ghfzrId FROM zlgh_baseinfo WHERE zlghId = #{zlghId} ")
    String selectFzrId(@Param("zlghId") String zlghId);
    // 查询各部门负责人
    List<Map<String, String>> getDepManById(Map<String, Object> params);
    List<JSONObject> queryZlghFileList(Map<String, Object> param);
    void deleteZlghFile(Map<String, Object> param);
    void deleteZlgh(Map<String, Object> param);
}
