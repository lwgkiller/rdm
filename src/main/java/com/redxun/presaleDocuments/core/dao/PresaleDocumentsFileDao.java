package com.redxun.presaleDocuments.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PresaleDocumentsFileDao {
    //..
    List<JSONObject> getFileListInfos(JSONObject params);

    //..
    void insertFileInfos(JSONObject jsonObject);

    //..
    void deleteFileInfos(JSONObject params);
}
