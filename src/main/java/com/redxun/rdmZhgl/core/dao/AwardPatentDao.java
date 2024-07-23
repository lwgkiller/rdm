package com.redxun.rdmZhgl.core.dao;


import com.alibaba.fastjson.JSONObject;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface AwardPatentDao {

    List<Map<String,Object>> queryApeList(Map<String, Object> param);
    List<Map<String,Object>> queryLbjfyLists(Map<String, Object> param);

    int countAgpfyList(Map<String, Object> param);

    Map<String, Object> queryAgpById(Map<String, Object> queryParam);

    @Select("SELECT fileId FROM award_patent WHERE id = #{id} ")
    String selectFjId(@Param("id") String id);

    @Select("SELECT id from award_patent  WHERE fileId = #{fileId} ")
    String selectAgpByFjId(@Param("fileId") String fileId);

    void addFileInfos(JSONObject param);

    void saveApgList(JSONObject param);

    void updateFileInfos(JSONObject param);

    void updateAgpList(JSONObject param);
    void deleteAgp(Map<String, Object> param);
    void deleteAgpFileIds(Map<String, Object> param);

    List<Map<String,Object>> queryNameCount(Map<String, Object> param);

    void addAgpList(Map<String, Object> param);


}
