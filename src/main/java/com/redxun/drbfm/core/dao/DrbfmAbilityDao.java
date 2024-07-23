package com.redxun.drbfm.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

@Repository
public interface DrbfmAbilityDao {
    List<JSONObject> queryAbilityList(Map<String, Object> param);

    int countAbilityList(Map<String, Object> param);

    void createAbility(JSONObject param);

    void updateAbility(JSONObject param);

    void deleteAbility(Map<String, Object> param);
}
