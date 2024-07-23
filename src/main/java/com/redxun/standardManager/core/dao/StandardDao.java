package com.redxun.standardManager.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface StandardDao {
    void deleteStandard(Map<String, Object> param);

    void updateStandard(Map<String, Object> param);

    void insertStandard(Map<String, Object> param);

    List<Map<String, Object>> queryStandardList(Map<String, Object> queryParam);

    int countStandardList(Map<String, Object> queryParam);

    Map<String, Object> queryStandard(Map<String, Object> queryParam);

    void batchInsertStandard(List<Map<String, Object>> dataList);

    List<Map<String, Object>> queryStandardIdNumber(Map<String, Object> param);

    void insertStandardRecord(Map<String, Object> param);

    List<Map<String, String>> queryUserList(Map<String, Object> queryParam);

    List<Map<String, Object>> queryStandardBorrowList(Map<String, Object> params);

    void batchUpdateBorrowedList(Map<String, Object> param);

    JSONObject queryStandardById(String standardId);

    Map<String, Object> queryPublicStandard(Map<String, Object> queryParam);

    List<Map<String, Object>> queryPublicStandardList(Map<String, Object> queryParam);

    int countPublicStandardList(Map<String, Object> queryParam);

    void deletePublicStandard(Map<String, Object> param);

    void insertPublicStandard(Map<String, Object> param);

    void updatePublicStandard(Map<String, Object> param);

    List<JSONObject> queryUserBelongGroups(String userId);

    List<Map<String, Object>> queryFzsjbzList(Map<String, Object> queryParam);

    int countFzsjbzList(Map<String, Object> queryParam);

    void createCollect(JSONObject messageObj);
    
    void deleteCollect(Map<String, Object> param);

    List<JSONObject> queryCollect(Map<String, Object> params);


    List<String> getCurrentSystemStandardSeqNum(Map<String, Object> param);
}
