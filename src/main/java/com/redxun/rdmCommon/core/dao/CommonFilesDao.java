package com.redxun.rdmCommon.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

/**
 * @author zz
 * */
@Repository
public interface CommonFilesDao {



    /**
     * 添加文件
     * @param param
     * @return
     * */
    void addFileInfos(JSONObject param);
    /**
     * @param mainId
     * @return list
     * */
    List<JSONObject> getFileListByMainId(String mainId);
    /**
     * 删除文件信息
     * @param id
     * */
    void delFileById(String id);
    /**
     * 查询
     *
     * @param params
     * @return List
     * */
    List<Map<String, Object>> getFileList(Map<String, Object> params);
    /**
     * 根据条件查询文件列表
     *
     * @param jsonObject
     * @return list
     * */
    List<JSONObject> getFileListByParam(JSONObject jsonObject);

    /**
     * 获取文件信息
     *
     * @param fileId
     * @return json
     * */
    JSONObject getFileObj(String fileId);

}
