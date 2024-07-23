package com.redxun.drbfm.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

@Repository
public interface DrbfmTotalDao {
    List<Map<String, Object>> queryTotalList(Map<String, Object> param);

    int countTotalList(Map<String, Object> param);

    void createTotal(JSONObject param);

    void updateTotal(JSONObject param);

    JSONObject getTotalDetail(String totalId);

    List<JSONObject> queryStructByParam(JSONObject param);

    void createStruct(JSONObject param);

    void updateStruct(JSONObject param);

    void updateNeedAnalysis(JSONObject param);

    void updateStructChilds(JSONObject param);

    int countStructByParentId(String parentId);

    void deleteTotalBaseInfo(Map<String, Object> param);

    void deleteTotalStructByParam(Map<String, Object> param);

    void updateParentChildsMinus1(JSONObject param);

    List<JSONObject> getYanzhongxingPage();

    List<JSONObject> getChuangxinxingPage();

    void updateStageStatus(JSONObject param);

    void updateStageStatusById(JSONObject param);
}
