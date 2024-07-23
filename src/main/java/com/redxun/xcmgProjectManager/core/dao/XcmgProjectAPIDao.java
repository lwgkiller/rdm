package com.redxun.xcmgProjectManager.core.dao;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

@Repository
public interface XcmgProjectAPIDao {
    List<Map<String, String>> xcmghrGetProjects(Map<String, Object> params);

    //结构化文档
    List<JSONObject> queryApiList(String searchValue);

    //结构化文档
    List<JSONObject> queryCpxhApiList(String searchValue);

    /**
     * 查询产品型谱信息
     *@param paramJson applyStatus 参数查询流程状态数据
     * @return list
     * */
    List<JSONObject> queryProductApiList(JSONObject paramJson);

}
