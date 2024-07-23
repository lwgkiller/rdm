package com.redxun.wwrz.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author zz
 * */
@Repository
public interface WwrzFilesDao {



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
     * 查询
     *
     * @param params
     * @return List
     * */
    List<Map<String, Object>> getReportFileList(Map<String, Object> params);

    /**
     * 获取文件信息
     *
     * @param fileId
     * @return json
     * */
    JSONObject getFileObj(String fileId);
    /**
     * 跟新报告文件信息
     *
     * @param params
     * */
    void updateReportFile(Map<String, Object> params);
    /**
     * 通过文件编号、产品型号、文件类型获取文件信息
     *
     * @param paramJson
     * @return json
     * */
    JSONObject getFileByReportCode(JSONObject paramJson);
    /**
     * 批量作废
     *
     * @param params
     * */
    void batchSetValid(Map<String, Object> params);

    /**
     * 添加报告信息
     *
     * @param params
     * */
    void addReportFileInfos(Map<String, Object> params);
    /**
     * 更新排序号
     *
     * @param params
     * */
    void updateIndexSort(Map<String, Object> params);
    /**
     * 查询超过有效期的报告
     *
     * @return  list
     * */
    List<JSONObject> getOutDateReportList();
    /**
     * 更新报告证书状态
     *
     * @return  list
     * */
    void updateReportStatus(String id);

    void updateReportSend1Msg(String id);

    void updateReportSend3Msg(String id);

    List<Map<String, Object>> getReport(Map<String, Object> params);
}
