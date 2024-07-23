package com.redxun.bjjszc.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

@Repository
public interface BjqdDao {
    List<JSONObject> queryBjqd(Map<String, Object> params);

    List<JSONObject> queryBjqdList(Map<String, Object> params);

    List<JSONObject> queryBjqdById(Map<String, Object> params);

    List<JSONObject> queryBjqdFlow(Map<String, Object> params);

    List<JSONObject> queryBjqdFlowList(Map<String, Object> params);

    JSONObject queryByWlId(JSONObject params);

    int countBjqd(Map<String, Object> param);

    int countBjqdById(Map<String, Object> param);

    void insertBjqd(JSONObject object);

    int judgeWlId(JSONObject object);

    void createBjqd(JSONObject object);

    void delBjqd(String id);

    void updateBjqdFlow(JSONObject object);

    String queryInstIdById(String id);

    String queryUserIdsById(String id);

    int countKgmsx(JSONObject object);

    int countKMS(JSONObject object);

    int countAll(JSONObject object);

    int countKgmsxAmount();

    int countKMSAmount();

    List<JSONObject> countAllLine();

    int countKgmsxLine(JSONObject object);

    int countKMSLine(JSONObject object);

    // 批量更新可购买属性
    void batchUpdateBjqd(List<JSONObject> params);

    // 批量更新PMS价格维护
    void batchUpdateBjqdPMS(List<JSONObject> params);

    // 查询所有的物料code
    List<String> queryAllWlId();

    void batchInsertBjqd(List<JSONObject> params);
}
