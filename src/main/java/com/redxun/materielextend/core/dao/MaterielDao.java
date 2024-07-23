package com.redxun.materielextend.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 应用模块名称
 * <p>
 * 代码描述
 * <p>
 * Copyright: Copyright (C) 2020 XXX, Inc. All rights reserved.
 * <p>
 * Company: 徐工挖掘机械有限公司
 * <p>
 *
 * @author liangchuanjiang
 * @since 2020/6/6 15:12
 */
@Repository
public interface MaterielDao {
    /**
     * 查询物料字典
     *
     * @return
     */
    JSONArray queryMaterielDic();

    /**
     * 批量新增物料
     *
     * @param params
     */
    void batchAddMateriels(List<JSONObject> params);

    /**
     * 更新一条物料
     *
     * @param param
     */
    void updateOneMateriel(JSONObject param);

    /**
     * 通过物料id查询物料详情
     *
     * @param materielId
     * @return
     */
    JSONObject queryMaterielById(String materielId);

    //..通过applyNo查找申请单关联的物料明细
    List<JSONObject> queryMaterielsByApplyNo(Map<String, Object> param);

    //..查询物料字段属性
    List<JSONObject> queryMaterielProperties(Map<String, Object> params);

    /**
     * 通过申请单的id批量删除申请单关联的物料
     *
     * @param applyNos
     */
    void batchDeleteMaterielsByApplyNos(List<String> applyNos);

    /**
     * 通过物料的id批量删除物料
     *
     * @param materielIds
     */
    void batchDeleteMaterielsByIds(List<String> materielIds);

    // 更新物料为问题物料
    void updateMateriel2Error(JSONObject materiel);

    // 更新物料的最终扩展状态
    void updateMaterielExtendResult(JSONObject materiel);

    // 批量更新物料信息，字段不固定
    void updateMaterielBatch(List<Map<String, String>> materiels);

    // 查询正在扩充或者扩充成功的物料
    List<JSONObject> queryOkOrDoingMatByIds(Map<String, Object> param);
}
