package com.redxun.rdMaterial.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RdMaterialFileDao {
    //..
    List<JSONObject> getFileListInfos(JSONObject params);

    //..
    void insertFileInfos(JSONObject jsonObject);

    //..
    void deleteFileInfos(JSONObject params);
}
