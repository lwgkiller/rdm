package com.redxun.rdmZhgl.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface AwardNewProductDao {
    List<Map<String, Object>> queryAnpList(Map<String, Object> param);
    int countAnpfyList(Map<String, Object> param);
    Map<String, Object> queryAnpById(Map<String, Object> queryParam);

    @Select("SELECT fileId FROM award_newe_product WHERE id = #{id} ")
    String selectFjId(@Param("id") String id);
    @Select("SELECT id from award_newe_product  WHERE fileId = #{fileId} ")
    String selectAnpByFjId(@Param("fileId") String fileId);

    void addFileInfos(JSONObject param);
    void saveAnpList(JSONObject param);
    void updateFileInfos(JSONObject param);
    void updateAnpList(JSONObject param);
    void deleteAnp(Map<String, Object> param);
    void deleteAnpFileIds(Map<String, Object> param);
    void addAnpList(Map<String, Object> param);
}
