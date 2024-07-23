package com.redxun.serviceEngineering.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface MultiLanguageBuildDao {
    //..
    List<Map<String, Object>> dataListQuery(Map<String, Object> params);

    //..
    Integer countDataListQuery(Map<String, Object> params);

    //..
    List<Map<String, Object>> glossaryListQuery(Map<String, Object> params);


    //..
    void deleteBusiness(Map<String, Object> param);

    //..
    void deleteItem(Map<String, Object> param);

    void deleteItemByMainId(String mainId);
    //..
    JSONObject queryDetailById(String businessId);

    //..
    List<JSONObject> queryItemList(String businessId);

    //..
    List<JSONObject> queryItemListByIds(Map<String, Object> param);

    //..
    List<JSONObject> queryApplyitemsListByMaterialCodes(Map<String, Object> param);

    //..
    void insertBusiness(JSONObject formData);

    //..
    void updateBusiness(JSONObject formData);

    //..
    void insertItem(JSONObject itemObj);

    //..
    void updateItem(JSONObject itemObj);

    //..
    void insertGlossary(JSONObject glossaryData);

    //..
    void updateGlossary(JSONObject glossaryData);

    void addChineseLbj(JSONObject param);
    void addEnglishLbj(JSONObject param);
    void addPortugueseLbj(JSONObject param);
    void addGermanyLbj(JSONObject param);
    void addSpanishLbj(JSONObject param);
    void addFrenchLbj(JSONObject param);
    void addItalianLbj(JSONObject param);
    void addRussianLbj(JSONObject param);
    void addPolishLbj(JSONObject param);
    void addTurkishLbj(JSONObject param);
    void addSwedishLbj(JSONObject param);
    void addDanishLbj(JSONObject param);
    void addDutchLbj(JSONObject param);
    void addSloveniaLbj(JSONObject param);
    void addRomaniaLbj(JSONObject param);

    void updateChineseLbj(JSONObject param);
    void updateEnglishLbj(JSONObject param);
    void updatePortugueseLbj(JSONObject param);
    void updateGermanyLbj(JSONObject param);
    void updateSpanishLbj(JSONObject param);
    void updateFrenchLbj(JSONObject param);
    void updateItalianLbj(JSONObject param);
    void updateRussianLbj(JSONObject param);
    void updatePolishLbj(JSONObject param);
    void updateTurkishLbj(JSONObject param);
    void updateSwedishLbj(JSONObject param);
    void updateDanishLbj(JSONObject param);
    void updateDutchLbj(JSONObject param);
    void updateChineseTLbj(JSONObject param);
    void updateSloveniaLbj(JSONObject param);
    void updateRomaniaLbj(JSONObject param);

}
