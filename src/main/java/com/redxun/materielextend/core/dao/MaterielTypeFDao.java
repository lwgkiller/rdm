package com.redxun.materielextend.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface MaterielTypeFDao {
    List<JSONObject> queryMaterielTypeF(Map<String, Object> params);

    int countMaterielTypeFList(Map<String, Object> param);

    List<JSONObject> queryMaterielTypeFDetail(Map<String, Object> params);

    List<JSONObject> queryMaterielTypeFallDetail();

    JSONObject queryMaterielTypeFById(String id);

    JSONObject setWlinfo(String id);

    List<JSONObject> queryMaterielTypeFByApplyNo(String applyNo);

    List<JSONObject> queryMaterielTypeFByUserId(String userId);

    JSONObject queryMaterielTypeFDetailById(String id);

    void createMaterielTypeF(JSONObject messageObj);

    void updateMaterielTypeF(JSONObject messageObj);

    void deleteMaterielTypeF(Map<String, Object> param);

    void createMaterielTypeFDetail(JSONObject messageObj);

    void updateMaterielTypeFDetail(JSONObject messageObj);

    void deleteMaterielTypeFDetail(Map<String, Object> param);

    void updateMaterielError(JSONObject messageObj);

    void updateTypeFNotice(Map<String, Object> param);
}
