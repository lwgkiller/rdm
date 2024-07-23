package com.redxun.serviceEngineering.core.dao;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;
import com.alibaba.fastjson.JSONObject;

@Repository
public interface ZdwjDao {
    // 表单的新增、更新和删除

    void insertZdwj(JSONObject param);

    void updateZdwj(JSONObject param);

    void updateZdwjNumber(JSONObject param);

    void deleteZdwj(JSONObject param);

    // 列表数据
    List<JSONObject> queryApplyList(Map<String, Object> params);

    // 某个表单的基本信息
    JSONObject queryApplyDetail(JSONObject params);


    JSONObject getUserInfoByPartsType(Map<String, Object> param);

    JSONObject queryDataById(String id);

    //增删改

    void updateBusiness(Map<String, Object> objBody);



}
