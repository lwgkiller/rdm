package com.redxun.rdmZhgl.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface CxyProjectFileDao {
    void addFileInfos(JSONObject fileInfo);

    void deleteFileById(String id);

    void deleteFileByCxyProjectIds(Map<String, Object> param);

    List<JSONObject> queryFilesByCxyProjectIds(Map<String, Object> param);
}
