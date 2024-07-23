package com.redxun.standardManager.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

/**
 * @author zhangzhen
 */
@Repository
public interface StandardFieldManagerDao {
    // 查询专业领域
    List<JSONObject> queryFieldObject(JSONObject params);

    // 保存专业领域
    void saveField(JSONObject params);

    // 更新专业领域
    void updateField(JSONObject params);

    // 删除专业领域
    void deleteField(JSONObject params);

    // 删除专业领域-标准对应关系（通过专业领域ID）
    void deleteStandardFieldRelaByField(JSONObject params);

    // 删除专业领域-标准对应关系（通过标准ID）
    void deleteStandardFieldRelaByStandard(JSONObject params);

    // 删除专业领域-人员对应关系（通过专业领域ID）
    void deleteUserFieldRelaByField(JSONObject params);

    // 删除专业领域-人员对应关系（通过人员ID）
    void deleteUserFieldRelaByUser(JSONObject params);

    // 插入标准-专业领域对应关系
    void batchInsertStandardFieldRela(List<JSONObject> params);

    // 插入人员-专业领域对应关系
    void batchInsertUserFieldRela(List<JSONObject> params);

    // 通过领域名称查询是否存在专业领域
    List<JSONObject> queryFieldByName(String fieldName);

    // 通过标准ID查询所属的专业领域
    List<JSONObject> queryFieldObjectByStandardId(String standardId);

    List<JSONObject> queryFieldUserRela(Map<String, Object> params);

    Integer countFieldUserRela(Map<String, Object> params);

    List<JSONObject> batchQueryFieldByStandardIds(Map<String, Object> params);

    List<JSONObject> groupStandardByField(Map<String, Object> params);
}
