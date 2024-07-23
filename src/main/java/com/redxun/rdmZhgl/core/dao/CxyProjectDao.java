package com.redxun.rdmZhgl.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 产学研<p>
 * 产学研模块<p>
 * Copyright: Copyright (C) 2021 XXX, Inc. All rights reserved. <p>
 * Company: 徐工挖掘机械有限公司<p>
 *
 * @author liwenguang
 * @since 2021/2/25
 */
@Repository
public interface CxyProjectDao {
    List<JSONObject> dataListQuery(Map<String, Object> params);

    Integer countDataListQuery(Map<String, Object> params);

    void insertCxyProjectData(JSONObject param);

    void updateCxyProjectData(JSONObject param);

    JSONObject queryCxyProjectById(String cxyProjectId);

    void deleteCxyProject(Map<String, Object> param);

    List<JSONObject> selectAllJson();

    List<String> getFileNamesListByMainId(String id);

    List<JSONObject> queryDepInfosByIds(List<String> userIds);

    Integer getTotal(JSONObject jsonObject);
}
