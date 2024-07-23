package com.redxun.rdmZhgl.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface HyglGeneralKanBanDao {
    //..获取内部会议总数
    Integer getInternalTotal(JSONObject jsonObject);

    //..获取外部会议总数
    Integer getExternalTotal(JSONObject jsonObject);
}
