package com.redxun.matPriceReview.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

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
public interface MatPriceReviewDao {
    List<JSONObject> queryMatPriceReviewList(JSONObject param);

    int countMatPriceReviewList(JSONObject param);

    List<JSONObject> queryMatPriceReviewById(String id);

    // 新增物料申请单
    void addMatPriceReviewApply(JSONObject param);

    // 更新物料申请单
    void updateMatPriceReviewApply(JSONObject param);

    void deleteApplyBaseInfos(Map<String, Object> params);

    void deleteFiles(Map<String, Object> params);
    
    List<JSONObject> getFileList(Map<String, Object> params);

    void addFileInfos(Map<String, Object> params);

    // 新增物料
    void addMatDetail(JSONObject param);

    // 更新物料
    void updateMatDetail(JSONObject param);

    List<JSONObject> queryMatDetailList(JSONObject param);

    void deleteMatDetails(Map<String, Object> params);

    void addRecord(JSONObject param);

    void updateRecord(JSONObject param);

    List<JSONObject> queryRecordList(JSONObject param);

    void deleteRecords(Map<String, Object> params);

    List<JSONObject> queryMatExtendSuccess(Map<String, Object> params);
}
