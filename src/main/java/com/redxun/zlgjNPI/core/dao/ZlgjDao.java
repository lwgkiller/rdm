package com.redxun.zlgjNPI.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

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
public interface ZlgjDao {
    List<JSONObject> queryGjllFileList(Map<String, Object> param);

    int countGjllFileList(Map<String, Object> param);

    void insertGjllFile(JSONObject param);

    void deleteGjllFile(Map<String, Object> param);


}
