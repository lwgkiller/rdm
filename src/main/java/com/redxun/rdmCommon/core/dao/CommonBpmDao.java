package com.redxun.rdmCommon.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

/**
 * 与流程相关的公共函数抽取
 */
@Repository
public interface CommonBpmDao {
    // 根据节点id(SCOPE_)、解决方案KEY(SOL_KEY_)、解决方案ID(SOL_ID_)查询节点流程变量
    List<Map<String, String>> queryNodeVarsByParam(Map<String, Object> params);

    // 根据流程实例id(INST_ID_)查找该实例当前的任务列表
    List<JSONObject> queryTaskListByInstId(Map<String, Object> params);

    List<JSONObject> querySolByBusKey(JSONObject param);
}
