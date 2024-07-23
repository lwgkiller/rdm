package com.redxun.zlgjNPI.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ManageImproveDao {

    List <JSONObject> manageImproveListQuery(JSONObject params);

    Integer countManageImproveQuery(JSONObject params);

    JSONObject getManageImproveDetail(String manageImproveId);

    List<JSONObject> subTypeList(JSONObject params);

    List<JSONObject> queryManageImproveFileList(JSONObject params);

    void addFiles(JSONObject fileInfo);

    void delManageImproveFileById(String id);

    void delFileByBelongId(Map <String, Object> param);

    void createManageImprove(JSONObject formDataJson);

    void updateManageImprove(JSONObject formDataJson);

    JSONObject queryNowYearMaxNum(Map<String, Object> param);
    
    void deleteManageImprove(JSONObject param);
}
