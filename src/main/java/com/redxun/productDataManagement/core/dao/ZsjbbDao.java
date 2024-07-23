package com.redxun.productDataManagement.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

@Repository
public interface ZsjbbDao {

    // 表单的新增、更新和删除
    void insertZsjbb(JSONObject param);

    void updateZsjbb(JSONObject param);

//    void chooseYxal(JSONObject param);
//
//    void cancelYxal(JSONObject param);
//
    void deleteZsjbb(JSONObject param);

    // 列表数据
    List<JSONObject> queryApplyList(Map<String, Object> params);

    // 列表数据
    List<JSONObject> queryExist(Map<String, Object> params);

    // 某个表单的基本信息
    JSONObject queryApplyDetail(JSONObject params);

    // 文件的增删改查
    List<JSONObject> queryFileList(JSONObject params);

    void insertFile(JSONObject param);

    void deleteFile(JSONObject param);

    // 查询没评优秀案例的部门
    List<JSONObject> queryUndoDepartList(JSONObject params);

    // 查询部门总评定数量
    int queryDepartTotalNumber(JSONObject params);

    // 查询部门已评定数
    int queryDepartChoseNumber(JSONObject params);

//    // 查询某个月份人为取消通知的
//    List<JSONObject> queryRemindCancel(JSONObject params);
//
//    // 插入某个人的取消通知
//    void insertRemindCancel(JSONObject params);
//
//    void setOverTime(JSONObject param);

}
