package com.redxun.rdmZhgl.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface AwardManagementDao {
    List<Map<String, Object>> queryAmgList(Map<String, Object> param);
    int countAmgfyList(Map<String, Object> param);
    Map<String, Object> queryAmgById(Map<String, Object> queryParam);

    @Select("SELECT fileId FROM award_manage WHERE id = #{id} ")
    String selectFjId(@Param("id") String id);
    @Select("SELECT id from award_manage  WHERE fileId = #{fileId} ")
    String selectAnpByFjId(@Param("fileId") String fileId);

    void addFileInfos(JSONObject param);
    void saveAmgList(JSONObject param);
    void updateFileInfos(JSONObject param);
    void updateAmgList(JSONObject param);
    void deleteAmg(Map<String, Object> param);
    void deleteAmgFileIds(Map<String, Object> param);
    void addAmgList(Map<String, Object> param);
}
