package com.redxun.rdmZhgl.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 应用模块名称
 * <p>
 * 代码描述
 * <p>
 * Copyright: Copyright (C) 2021 XXX, Inc. All rights reserved.
 * <p>
 * Company: 徐工挖掘机械有限公司
 * <p>
 *
 * @author liangchuanjiang
 * @since 2021/2/23 10:43
 */
@Repository
public interface RdmZhglDao {
    List<JSONObject> queryTaskByActInstIds(Map<String,Object> param);

    List<JSONObject> queryUserByDeptNameAndRel(Map<String,Object> param);

    /**
     * 根据财务订单号，获取项目负责人
     *
     * @param orderCode
     * @return json
     * */
    JSONObject getProjectManager(String orderCode);
}
