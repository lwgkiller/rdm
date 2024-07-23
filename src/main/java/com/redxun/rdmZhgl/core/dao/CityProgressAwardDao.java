package com.redxun.rdmZhgl.core.dao;


import com.alibaba.fastjson.JSONObject;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface CityProgressAwardDao {

    List<Map<String,Object>> queryCpaList(Map<String, Object> param);
    List<Map<String,Object>> queryLbjfyLists(Map<String, Object> param);

    int countCpsfyList(Map<String, Object> param);

    Map<String, Object> queryCpaById(Map<String, Object> queryParam);

    @Select("SELECT fileId FROM award_city_level_science_progress WHERE id = #{id} ")
    String selectFjId(@Param("id") String id);

    @Select("SELECT id from award_city_level_science_progress  WHERE fileId = #{fileId} ")
    String selectCpaByFjId(@Param("fileId") String fileId);

    void addFileInfos(JSONObject param);

    void saveCpaList(JSONObject param);

    void updateFileInfos(JSONObject param);

    void updateCpaList(JSONObject param);
    void deleteCpa(Map<String, Object> param);
    void deleteCpaFileIds(Map<String, Object> param);

    List<Map<String,Object>> queryNameCount(Map<String, Object> param);

    void addCpaList(Map<String, Object> param);


}
