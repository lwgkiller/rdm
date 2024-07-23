package com.redxun.xcmgNpi.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

@Repository
public interface NpiFileDao {

    List<JSONObject> queryNpiFileList(JSONObject param);

    int countNpiFileList(JSONObject param);

    List<JSONObject> querySystem(JSONObject param);

    JSONObject queryNpiFileById(JSONObject param);

    void insertNpiFile(JSONObject param);

    void updateNpiFile(JSONObject param);

    void deleteNpiFileByIds(JSONObject param);

    Integer queryNpiFileBySystemIds(Map<String, Object> params);

    void delSystemNode(String nodeId);

    void updateSystemNode(JSONObject jsonObject);

    void saveSystemNode(JSONObject jsonObject);
}
