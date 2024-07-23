package com.redxun.rdmCommon.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

/**
 * @author zz
 */
@Repository
public interface RdmDao {
    List<JSONObject> queryUserByNo(JSONObject param);

    List<JSONObject> queryNewUser(JSONObject params);

    void insertNewUser(JSONObject param);

    void deleteUser(Map<String, Object> param);

    List<JSONObject> queryOsDimension(JSONObject params);

    void updateConfirmStatus(JSONObject param);

    List<JSONObject> queryInJobUserByNo(JSONObject param);

    List<Map<String, Object>> queryUserInfoByIds(Map<String, Object> param);

    void updateUserPwdStatus(JSONObject param);


    List<String> getDistinctGrantMenu(JSONObject param);

    List<String> getDistinctGrantSys(JSONObject param);

    //..清空表行数
    void deleteCountTableRows();

    //..生成表行数
    void callCountTableRows(String dataBaseName);

    //..查询表行数
    List<JSONObject> selectCountTableRows(JSONObject params);
}
