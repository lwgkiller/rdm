package com.redxun.rdmZhgl.core.dao;


import com.alibaba.fastjson.JSONObject;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface AwardSciencePlanProjectDao {

    List<Map<String,Object>> queryAsppList(Map<String, Object> param);
    List<Map<String,Object>> queryLbjfyLists(Map<String, Object> param);

    int countAgpfyList(Map<String, Object> param);

    Map<String, Object> queryAsppeById(Map<String, Object> queryParam);

    @Select("SELECT fileId FROM award_science_plan_project WHERE id = #{id} ")
    String selectFjId(@Param("id") String id);

    @Select("SELECT id from award_science_plan_project  WHERE fileId = #{fileId} ")
    String selectAsppeByFjId(@Param("fileId") String fileId);

    void addFileInfos(JSONObject param);

    void saveAsppeList(JSONObject param);

    void updateFileInfos(JSONObject param);

    void updateAgpList(JSONObject param);
    void deleteAsppe(Map<String, Object> param);
    void deleteAsppeFileIds(Map<String, Object> param);

    List<Map<String,Object>> queryNameCount(Map<String, Object> param);

    void addAgpList(Map<String, Object> param);


}
