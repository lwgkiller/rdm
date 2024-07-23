package com.redxun.rdmZhgl.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

/**
 * @author zhangzhen
 */
@Repository
public interface YfjbBaseInfoDao {

    /**
     * 查询
     *
     * @param params
     * @return List
     * */
    List<Map<String, Object>> query(Map<String, Object> params);
    /**
    * 获取条目列表
     *
     * @return list
    * */
    List<JSONObject> getItems();

    /**
     * 保存
     * @param param
     * @return
     * */
    int addObject(Map<String, Object> param);

    /**
     * 更新
     *
     * @param params
     * @return
     */
    int updateObject(Map<String, Object> params);
    /**
     * 更新
     *
     * @param params
     * @return
     */
    int updateInfoStatus(Map<String, Object> params);
    /**
     * 批量删除
     *
     * @param params
     * */
    void batchDelete(Map<String, Object> params);
    /**
     * 根据id获取项目列表
     *
     * @param params
     * */
    List<Map<String,Object>> getBaseInfoListByIds(Map<String, Object> params);
    /**
     * 根据id获取详情信息
     *
     * @return JSONObject
     * @param id
     * */
    JSONObject getObjectById(String id);

    /**
     * 保存
     * @param param
     * @return
     * */
    int addItem(Map<String, Object> param);

    /**
     * 更新
     *
     * @param params
     * @return
     */
    int updateItem(Map<String, Object> params);

    /**
     * 删除
     *
     * @param id
     * @return
     */
    int delItem(String id);
    /**
     * 保存
     * @param param
     * @return
     * */
    int addMember(Map<String, Object> param);

    /**
     * 更新
     *
     * @param params
     * @return
     */
    int updateMember(Map<String, Object> params);

    /**
     * 删除
     *
     * @param id
     * @return
     */
    int delMember(String id);

    /**
     * 查询进度信息
     *
     * @param params
     * @return List
     * */
    List<Map<String, Object>> processList(Map<String, Object> params);
    /**
     * 查询项目成员列表
     *
     * @param params
     * @return List
     * */
    List<Map<String, Object>> getMemberList(Map<String, Object> params);
    /**
     * 提交
     *
     * @param params
     * @return
     */
    void infoSubmit(Map<String, Object> params);

    /**
     * 根据型号查询列表
     *
     * @param jsonObject
     * @return list
     * */
    List<JSONObject> getInfoListByModel(JSONObject jsonObject);

    /**
     * 获取策划降本和
     *
     * @param jsonObject
     * @return f
     * */
    Float getPerCost(JSONObject jsonObject);
    /**
     * 获取以实现单台降本
     *
     * @param jsonObject
     * @return f
     * */
    Float getAchieveCost(JSONObject jsonObject);
    /**
     * 获取月度已实现单台降本
     *
     * @param jsonObject
     * @return f
     * */
    Float getMonthAchieveCost(JSONObject jsonObject);
    /**
     * 获取计划切换项目列表
     *
     * @param paramMap
     * @return f
     * */
    List<Map<String,Object>> getPlanChangeList(Map<String,Object> paramMap);
    /**
     * 获取实际切换项目列表
     *
     * @param paramMap
     * @return f
     * */
    List<Map<String,Object>> getActualChangeList(Map<String,Object> paramMap);
    /**
     * 获取计划下发切换通知单项目
     *
     * @param paramMap
     * @return f
     * */
    List<Map<String,Object>> getPlanChangeNoticeList(Map<String,Object> paramMap);
    /**
     * 获取实际下发切换通知单项目
     *
     * @param paramMap
     * @return f
     * */
    List<Map<String,Object>> getActualChangeNoticeList(Map<String,Object> paramMap);
    /**
     * 获取计划下发试制通知单项目
     *
     * @param paramMap
     * @return f
     * */
    List<Map<String,Object>> getPlanProduceNoticeList(Map<String,Object> paramMap);
    /**
     * 获取实际下发试制通知单项目
     *
     * @param paramMap
     * @return f
     * */
    List<Map<String,Object>> getActualProduceNoticeList(Map<String,Object> paramMap);
    /**
     * 切换通知单已下发但未切换项目列表
     *
     * @param paramMap
     * @return f
     * */
    List<Map<String,Object>> getUnChangeList(Map<String,Object> paramMap);
    /**
     * 项目维度报表报表
     *
     * @param paramMap
     * @return f
     * */
    List<Map<String,Object>> getCostCategoryList(Map<String,Object> paramMap);
    /**
     * 项目维度报表报表-直接降本
     *
     * @param paramMap
     * @return f
     * */
    List<Map<String,Object>> getZjjbList(Map<String,Object> paramMap);
    /**
     * 降本增效零部件变更汇总表
     *
     * @param paramMap
     * @return f
     * */
    List<Map<String,Object>> getJbzxList(Map<String,Object> paramMap);

    /**
     * 按年度获取降本机型
     * */
    List<JSONObject> getModelsByYear(JSONObject jsonObject);
    /**
     * 根据id获取项目
     *
     * @param mainId
     * @return f
     * */
    List<Map<String,Object>> getInfoListByMainId(String mainId);

    /**
     * 查询所有未切换的项目
     *
     * @return f
     * */
    List<JSONObject> getInfoListByReplace();
    /**
     * 查询所有未切换的项目
     *
     * @param jsonObject
     * @return f
     * */
    void updateIsNewProcess(JSONObject jsonObject);
    /**
     * 查询各所降本项目
     *
     * @return f
     * */
    List<JSONObject> getDeptProjectNum();
    /**
     * 获取切换项目个数
     *
     * @param deptId
     * @return
     * */
    Integer getDeptChangeNum(String deptId);
    /**
     * 专业统计数据
     *
     * @return
     * */
    List<JSONObject> getMajorData();
    /**
     * 根据部门ID获取所有的降本项目
     *
     * @param deptId
     * @return
     * */
    List<JSONObject> getProjectListByDeptId(String deptId);
    /**
     * 保存
     * @param param
     * @return
     * */
    int addDelayData(JSONObject param);

    /**
     * 更新
     *
     * @param param
     * @return
     */
    int updateDelayData(JSONObject param);
    /**
     *获取对象数据
     *
     * @param deptId
     * @return
     */
    JSONObject getDelayData(String deptId);
    /**
     * 延迟统计数据
     *
     * @return
     * */
    List<JSONObject> getDelayDataList();
    /**
     * 季度统计数据
     *
     * @return
     * */
    List<JSONObject> getQuarterDataList(String year);
    /**
     * 批量新增延期数据
     *
     * @param params
     */
    void batchAddDelay(List<JSONObject> params);
    /**
     * 删除所有的延期数据
     * */
    void delDelayInfo();
    /**
     * 获取延期项目
     *
     * @param paramJson
     * @return
     * */
    List<JSONObject> getDelayProductList(JSONObject paramJson);
    /**
     * 获取审批人
     *
     * @param majorKey
     * @return
     * */
    JSONObject getAuditRelation(String majorKey);

}
