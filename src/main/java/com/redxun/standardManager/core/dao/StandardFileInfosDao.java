package com.redxun.standardManager.core.dao;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * @author zhangzhen
 */
@Repository
public interface StandardFileInfosDao {
    /**
     * 获取文件列表
     *
     * @param params
     * @return jsonArray
     */
    JSONArray getFiles(Map<String,Object> params);

    int countAttachFileNum(Map<String,Object> params);

    /**
     * 添加文件信息
     *
     * @param map
     */
    void addFileInfos(JSONObject map);

    /**
     * 更新文件状态信息
     * @param params
     */
    void updateFileInfos(Map<String, Object> params);
    /**
     * 删除项目文件
     *
     * @param params
     */
    void deleteFileByIds(Map<String, Object> params);

    /**
     * 通过标准删除附表文件
     * 
     * @param params
     */
    void deleteFileByStandardIds(Map<String, Object> params);
}
