package com.redxun.portrait.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

/**
 * @author zhangzhen
 */
@Repository
public interface PortraitAttendanceDao {
    /**
     * 查询
     *
     * @param params
     * @return List
     * */
    List<Map<String, Object>> query(Map<String, Object> params);

    /**
     * 保存
     * @param param
     * @return
     * */
    int addObject(Map<String, Object> param);
    /**
     * 批量导入
     *
     * @param dataList
     * */
    void batchInsertAttendance(List<Map<String, Object>> dataList);
    /**
     * 更新
     *
     * @param params
     * @return
     */
    int updateObject(Map<String, Object> params);
    /**
     * 批量删除
     *
     * @param params
     * */
    void batchDelete(Map<String, Object> params);

    /**
     * 根据id获取详情信息
     *
     * @return JSONObject
     * @param id
     * */
    JSONObject getObjectById(String id);
    /**
     *获取个人考勤信息
     *
     * @param jsonObject
     * @return json
     * */
    List<Map<String,Object>> getPersonAttendanceList(JSONObject jsonObject);

    /**
     * 批量导入考勤原始数据
     *
     * @param dataList
     * */
    void batchInsertOrgData(List<Map<String, Object>> dataList);
    /**
     * 根据部门查询人员列表
     *
     * @param params
     * @return List
     * */
    List<JSONObject> getUserByDeptId(Map<String, Object> params);
    /**
     * 根据月份删除原始数据
     *
     * @param yearMonth
     * */
    void delByMonth(String yearMonth);
    /**
     * 获取基准线上的数据
     *
     * @param jsonObject
     * @return list
     * */
    List<JSONObject> getTopUserList(JSONObject jsonObject);
    /**
     * 获取基准线下的数据
     *
     * @param jsonObject
     * @return list
     * */
    List<JSONObject> getDownUserList(JSONObject jsonObject);
    /**
     * 根据月份删除考勤数据
     *
     * @param yearMonth
     * */
    void delAttendanceByMonth(String yearMonth);


}
