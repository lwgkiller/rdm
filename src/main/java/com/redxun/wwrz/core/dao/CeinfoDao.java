package com.redxun.wwrz.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface CeinfoDao {
    List<JSONObject> queryCeinfo(Map<String, Object> params);

    void deleteCeinfo(Map<String, Object> param);

    void createCeinfo(JSONObject messageObj);

    void updateCeinfo(JSONObject messageObj);

    void updateNoteStatus(JSONObject messageObj);


    JSONObject queryCeinfoById(String rapId);


    int countCeinfoList(Map<String, Object> param);

    JSONObject queryMaxNum(JSONObject params);

    List<JSONObject> queryAuto(String rapId);

    List<JSONObject> queryCeinfoByOnlyNum(String rapId);
}
