package com.redxun.xcmgJsjl.core.dao;

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
 * @since 2020/11/23 19:02
 */
@Repository
public interface XcmgJsjlFileDao {
    void addFileInfos(JSONObject fileInfo);

    void deleteFileById(String fileId);

    void deleteFileByJsjlIds(Map<String,Object> param);

    List<JSONObject> queryFilesByJsjlIds(Map<String, Object> params);
}
