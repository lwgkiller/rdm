package com.redxun.keyDesign.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface JsbzDao {
    List<JSONObject> queryJsbz(Map<String, Object> params);


    void createJsbz(JSONObject messageObj);
    
    

    int countJsbzList(Map<String, Object> param);



    void deleteJsbz(Map<String, Object> param);


    List<JSONObject> queryMsg(Map<String, Object> params);


    List<JSONObject> queryMsgDetail(Map<String, Object> params);


    void createMsg(JSONObject messageObj);


    void updateMsg(JSONObject messageObj);


    void createMsgDetail(JSONObject messageObj);


    void updateMsgDetail(JSONObject messageObj);



    int countMsgList(Map<String, Object> param);



    void deleteMsgDetail(Map<String, Object> param);


    void deleteMsg(Map<String, Object> param);


    JSONObject queryMsgById(String zId);

}
