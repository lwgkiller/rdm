package com.redxun.rdmZhgl.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface AwardTalentsDao {
    List<Map<String, Object>> queryAtsList(Map<String, Object> param);
    int countAtsfyList(Map<String, Object> param);
    Map<String, Object> queryAtsById(Map<String, Object> queryParam);

    @Select("SELECT fileId FROM award_talents WHERE id = #{id} ")
    String selectFjId(@Param("id") String id);
    @Select("SELECT id from award_talents  WHERE fileId = #{fileId} ")
    String selectAnpByFjId(@Param("fileId") String fileId);

    void addFileInfos(JSONObject param);
    void saveAtsList(JSONObject param);
    void updateFileInfos(JSONObject param);
    void updateAtsList(JSONObject param);
    void deleteAts(Map<String, Object> param);
    void deleteAtsFileIds(Map<String, Object> param);
    void addAtsList(Map<String, Object> param);
}
