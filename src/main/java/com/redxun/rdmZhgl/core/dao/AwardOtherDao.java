package com.redxun.rdmZhgl.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface AwardOtherDao {
    List<Map<String,Object>> queryAotList(Map<String, Object> param);
    int countAotfyList(Map<String, Object> param);
    Map<String, Object> queryAotById(Map<String, Object> queryParam);

    @Select("SELECT fileId FROM award_other WHERE id = #{id} ")
    String selectFjId(@Param("id") String id);
    @Select("SELECT id from award_other  WHERE fileId = #{fileId} ")
    String selectAgpByFjId(@Param("fileId") String fileId);

    void addFileInfos(JSONObject param);
    void saveAotList(JSONObject param);
    void updateFileInfos(JSONObject param);
    void updateAotList(JSONObject param);
    void deleteAot(Map<String, Object> param);
    void deleteAotFileIds(Map<String, Object> param);
    void addAotList(Map<String, Object> param);
}
