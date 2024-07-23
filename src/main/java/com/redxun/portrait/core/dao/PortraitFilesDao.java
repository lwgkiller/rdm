package com.redxun.portrait.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

/**
 * @author zz
 * */
@Repository
public interface PortraitFilesDao {



    /**
     * 添加文件
     * @param param
     * @return
     * */
    void addFileInfos(JSONObject param);
    /**
     * @param detailId
     * @return list
     * */
    List<JSONObject> getFileListByMainId(String detailId);
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

}
