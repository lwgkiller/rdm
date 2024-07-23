package com.redxun.rdmZhgl.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

/**
 * @author zz
 * */
@Repository
public interface XpszApplyDao {
    /**
     * 获取申请信息
     * @param id
     * @return
     * */
    Map<String, Object> getObjectById(String id);

    /**
     * 新增申请单
     * @param params
     * */
    void add(Map<String, Object> params);

    /**
     * 更新申请单
     * @param params
     * */
    void update(Map<String, Object> params);


}
