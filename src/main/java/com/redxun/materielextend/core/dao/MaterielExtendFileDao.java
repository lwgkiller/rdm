package com.redxun.materielextend.core.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
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
public interface MaterielExtendFileDao {
    void deleteFileById(String fileId);

    List<JSONObject> listData();

    void addFileInfos(Map<String, String> fileInfo);
}
