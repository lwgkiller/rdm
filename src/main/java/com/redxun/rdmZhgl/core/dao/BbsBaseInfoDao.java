package com.redxun.rdmZhgl.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

/**
 * @author zhangzhen
 */
@Repository
public interface BbsBaseInfoDao {
    /**
     * 查询
     *
     * @param params
     * @return j
     * */
    List<Map<String, Object>> query(Map<String, Object> params);
    /**
     * 获取对象
     *
     * @param id
     * @return JSON
     * */
    JSONObject getObject(String id);
    /**
     * 保存
     * @param param
     * @return
     * */
    int add(Map<String, Object> param);
    /**
     * 更新
     * @param param
     * @return
     * */
    int updateObject(Map<String, Object> param);
    /**
     * 批量删除
     *
     * @param params
     * */
    void batchDelete(Map<String, Object> params);
    /**
     * 保存图片信息
     * @param param
     * @return
     * */
    int addPic(Map<String, Object> param);
    /**
     * 批量删除图片
     *
     * @param params
     * */
    void batchDeletePic(Map<String, Object> params);
    /**
     * 获取图片列表
     *
     * @param mainId
     * @return list
     * */
    List<JSONObject> getPicList(String mainId);
    /**
     * 查询
     *
     * @param params
     * @return j
     * */
    List<Map<String, Object>> getBbsList(Map<String, Object> params);
    /**
     * 更新
     * @param param
     * @return
     * */
    int removePost(Map<String, Object> param);
}
