package com.redxun.materielextend.core.dao;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

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
public interface MaterielApplyDao {
    // 通过Id查询申请单详情
    JSONObject queryMaterielApplyById(String applyNo);

    // 查询用户的物料扩充角色
    List<JSONObject> queryMaterielOpRoles(Map<String, String> param);

    // 通过用户ID查询用户信息
    List<JSONObject> queryUserInfosByIds(List<String> userIds);

    // 通过参数查询物料扩充申请单列表
    List<JSONObject> queryMaterielApplyList(Map<String, Object> params);

    // 通过参数查询物料扩充申请单列表总数
    int queryMaterielApplyListTotal(Map<String, Object> params);

    // 通过申请单的id批量删除申请单
    void batchDeleteApplyByIds(List<String> applyNos);

    // 通过userId更新联系电话
    void updateApplyerMobile(Map<String, Object> params);

    // 新增物料申请单
    void addMaterielApply(Map<String, Object> params);

    // 更新物料申请单
    void updateMaterielApply(@Param("params") Map<String, Object> params);

    /**
     * 通过申请单号查询关联的物料明细
     *
     * @param param
     * @return
     */
    List<JSONObject> queryMaterielsByApplyNos(Map<String, Object> param);

    void updateApplyFailConfirm(String applyNo);

    // 查询该申请人提交的申请单中状态是“流程结束（有错误物料）”的
    List<JSONObject> queryFailEndApplyBySQR(String sqrId);

    // 通过身份证号查询用户和部门的信息
    List<JSONObject> queryUserInfoByCertNo(String certNo);

    List<JSONObject> queryCodeList(Map<String, Object> params);

    int countCodeList(Map<String, Object> param);
}
