package com.redxun.serviceEngineering.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface JxbzzbshDao {
    //..
    List<JSONObject> jxbzzbshListQuery(Map <String, Object> params);

    //..
    Integer countJxbzzbshQuery(Map <String, Object> params);

    List<JSONObject> queryFileList(Map <String, Object> params);

    List<JSONObject> queryFileListByMaterialCode(Map <String, Object> params);

    void insertJxbzzbsh(Map <String, Object> params);

    void updateJxbzzbsh(JSONObject formDataJson);
    //..
    void deleteJxbzzbsh(Map <String, Object> param);

    JSONObject queryJxbzzbshById(String id);

    JSONObject getOldJxbzzbshDetail(String id);


    JSONObject getTestJxbzzbshDetailByMaterialCode(String materialCode);

    String queryMaxApplicationNumber(JSONObject jsonObject);

    JSONObject checkShipmentnotmadeIdUnique(JSONObject jsonObject);

    void updateStatus(JSONObject formDataJson);
}
