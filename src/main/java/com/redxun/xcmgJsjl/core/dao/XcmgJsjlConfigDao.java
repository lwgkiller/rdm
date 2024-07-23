package com.redxun.xcmgJsjl.core.dao;

import com.alibaba.fastjson.JSONObject;
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
 * @since 2020/11/23 19:02
 */
@Repository
public interface XcmgJsjlConfigDao {
    List<JSONObject> dimensionList(JSONObject param);

    void saveDimension(JSONObject param);

    void updateDimension(JSONObject param);

    List<JSONObject> queryUsingDimensions(Map<String, Object> params);

    void delDimensionByIds(Map<String, Object> params);
}
