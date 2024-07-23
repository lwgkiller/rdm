package com.redxun.rdmZhgl.core.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

@Repository
public interface AwardGroupProgressDao {

    List<Map<String, Object>> queryAgpList(Map<String, Object> param);

    List<Map<String, Object>> queryLbjfyLists(Map<String, Object> param);

    int countAgpfyList(Map<String, Object> param);

    Map<String, Object> queryAgpById(Map<String, Object> queryParam);

    @Select("SELECT fileId FROM award_group_progress WHERE id = #{id} ")
    String selectFjId(@Param("id") String id);

    @Select("SELECT id from award_group_progress  WHERE fileId = #{fileId} ")
    String selectAgpByFjId(@Param("fileId") String fileId);

    void addFileInfos(JSONObject param);

    void saveApgList(JSONObject param);

    void updateFileInfos(JSONObject param);

    void updateAgpList(JSONObject param);

    void deleteAgp(Map<String, Object> param);

    void deleteAgpFileIds(Map<String, Object> param);

    List<JSONObject> queryNameCount(Map<String, Object> param);

    void addAgpList(Map<String, Object> param);

}
