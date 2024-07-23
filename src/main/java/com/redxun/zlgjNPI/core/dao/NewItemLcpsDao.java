package com.redxun.zlgjNPI.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface NewItemLcpsDao {
    List<JSONObject> queryXplcList(Map<String, Object> params);
    void insertLcps(JSONObject param);
    void updateLcps(JSONObject param);
    JSONObject queryXplcById(String xpsxId);
    List<JSONObject> queryXplcFileList(Map<String, Object> param);
    void addXplcFileInfos(JSONObject param);
    void deleteXplcFile(Map<String, Object> param);
    void saveXplcxx(JSONObject param);
    void updateXplcxx(JSONObject param);
    List<JSONObject> getXplcxxList(@Param("xplcId")String xplcId);
    Map<String, Object>  getXplcxx (@Param("id") String id);
    void delXplcxxListById(Map<String, Object> param);
    void delXplcxxList(Map<String, Object> param);
    void deleteXplc(Map<String, Object> param);
}
