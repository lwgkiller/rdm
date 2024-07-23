package com.redxun.rdmCommon.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

/**
 * @author zz
 */
@Repository
public interface PdmApiDao {
    List<JSONObject> queryRdmUserByPdmUser(Map<String, Object> param);

//    查询某用户负责或者参与的，当前正在运行中的科技项目
    List<JSONObject> pdmQueryProjectList(Map<String, Object> param);

    List<JSONObject> queryStageDeliveryByCategoryIds(Map<String, Object> param);
}
