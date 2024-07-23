package com.redxun.productDataManagement.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

@Repository
public interface ProductSpectrumDao {
    // 列表查询
    List<JSONObject> dataListQuery(Map<String, Object> params);

    // 列表导出
    List<JSONObject> dataListExport(Map<String, Object> params);

    // 数量查询
    Integer countDataListQuery(Map<String, Object> params);

    // 标签查询
    List<JSONObject> tagListQuery(Map<String, Object> params);

    // 标签数量查询
    Integer countTagListQuery(Map<String, Object> params);

    // ..
    JSONObject queryDataById(String id);

    // 增删改
    void deleteBusiness(String id);

    void deleteSpectrum(JSONObject param);

    void insertBusiness(Map<String, Object> objBody);

    void updateBusiness(Map<String, Object> objBody);

    // 月度状态子表项的增删改查
    List<JSONObject> queryMonthStatusList(JSONObject params);

    void insertMonthStatus(JSONObject param);

    void updateMonthStatus(JSONObject param);

    void deleteMonthStatus(JSONObject param);

    // 主要配置表子表项的增删改查
    List<JSONObject> queryMainSettingList(JSONObject params);

    void insertMainSetting(JSONObject param);

    void updateMainSetting(JSONObject param);

    void deleteMainSetting(JSONObject param);

    // 主要参数表子表项的增删改查
    List<JSONObject> queryMainParamList(JSONObject params);

    void insertMainParam(JSONObject param);

    void updateMainParam(JSONObject param);

    void deleteMainParam(JSONObject param);

    void deleteMainParamWhenImport(JSONObject param);

    void deleteMainSettingWhenImport(JSONObject param);

    // 手工更改表子表项的增删改查
    List<JSONObject> queryManualChangeList(JSONObject params);

    List<JSONObject> queryWorkDeviceList(JSONObject params);

    void insertManualChange(JSONObject param);

    void updateManualChange(JSONObject param);

    void deleteManualChange(JSONObject param);

    void insertWorkDevice(JSONObject param);

    void updateWorkDevice(JSONObject param);

    void deleteWorkDevice(JSONObject param);

    // 查标签类别
    List<Map<String, Object>> queryTagType(Map<String, String> params);

    List<Map<String, Object>> queryTagName(Map<String, String> params);

    // 配置表查询
    List<JSONObject> settingListQuery(Map<String, Object> params);

    // 参数表查询
    List<JSONObject> paramListQuery(Map<String, Object> params);

    // 查询编辑权限
    List<JSONObject> checkEditPermition(JSONObject params);

    // 查询设计型号是否存在，如果传了id则查询不等于自己的
    List<JSONObject> checkDesignModelExist(JSONObject params);

    // 批量插参数表
    void batchInsertMainParam(List<JSONObject> params);

    // 批量插配置表
    void batchInsertMainSetting(List<JSONObject> params);

    // 查询产品型谱数据for API
    List<JSONObject> queryDesignSpectrumForApi(Map<String, Object> params);

    // 查询产品型谱数量for API
    Integer countDesignSpectrumForApi(Map<String, Object> params);
}
