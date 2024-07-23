package com.redxun.rdmZhgl.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

/**
 * @author zz
 * */
@Repository
public interface MonthWorkFilesDao {

    /**
     * 添加文件
     * @param param
     * @return
     * */
    void addFileInfos(JSONObject param);
    /**
     * @param applyId
     * @return list
     * */
    List<JSONObject> getFilesById(String applyId);
    /**
     * 删除文件信息
     *
     *@param param
     * */
    void delFile(Map<String, Object> param);

    /**
     * 删除文件信息
     * @param applyId
     * */
    void delFileByApplyId(String applyId);

}
