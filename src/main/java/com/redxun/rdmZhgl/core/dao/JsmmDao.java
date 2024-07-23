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
public interface JsmmDao {
    void updateRDTime(Map<String, Object> param);

    List<JSONObject> queryDeptRespByUserId(String userId);

    List<Map<String,Object>> queryJsmmList(Map<String, Object> param);

    int countJsmmList(Map<String, Object> param);

    JSONObject queryMaxJsmmNum(Map<String, Object> param);

    void updateNumber(Map<String, Object> param);

    void insertJsmm(JSONObject param);

    void updateJsmm(JSONObject param);

    void updateJlInfo(JSONObject param);

    JSONObject queryJsmmById(String jsmmId);

    List<JSONObject> queryJsmmFileList(Map<String, Object> param);

    List<JSONObject> queryJsmmFileTypes();

    void addJsmmFileInfos(JSONObject param);

    void deleteJsmmFile(Map<String, Object> param);

    void deleteJsmm(Map<String, Object> param);
}
