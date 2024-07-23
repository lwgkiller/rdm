package com.redxun.serviceEngineering.core.dao;


import com.alibaba.fastjson.JSONObject;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface MulitilingualTranslationDao {
    //零件图册
    List<Map<String,Object>> queryLjtcList(Map<String, Object> param);

    int countLjtcList(Map<String, Object> param);

    Map<String, Object> queryLjtcById(Map<String, Object> queryParam);

    void addLjtc(JSONObject param);
    void updateLjtc(JSONObject param);
    void updateLjtcByCode(JSONObject param);
    void deleteLjtc(Map<String, Object> param);

    int getLjtcExist(@Param("materialCode") String materialCode,@Param("chineseId") String chineseId);
    //仪表
    List<Map<String,Object>> queryYbList(Map<String, Object> param);

    int countYbList(Map<String, Object> param);

    Map<String, Object> queryYbById(Map<String, Object> queryParam);

    void addYb(JSONObject param);

    void updateYb(JSONObject param);

    void deleteYb(Map<String, Object> param);

    int getYbExist(@Param("originChinese") String originChinese,@Param("chineseId") String chineseId);

    //查询推荐中文的英文和小语种翻译
    JSONObject getRecommend(JSONObject param);
}
