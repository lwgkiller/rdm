package com.redxun.xcmgProjectManager.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

@Repository
public interface XcmgProjectOtherDao {
    List<Map<String, String>> queryProjectSources(Map<String, Object> params);

    List<Map<String, String>> queryProjectCategory(Map<String, Object> params);

    List<Map<String, String>> queryProjectLevel(Map<String, Object> params);

    List<Map<String, Object>> queryProjectList(Map<String, Object> params);

    List<Map<String, Object>> queryDepNameByIds(Map<String, Object> params);

    List<Map<String, String>> queryProjectMemRole(Map<String, Object> params);

    List<Map<String, Object>> queryProjectLevelDivide(Map<String, Object> params);

    List<Map<String, Object>> queryProjectKnot2Rating(Map<String, Object> params);

    List<Map<String, String>> queryProjectRoleRankRequire(Map<String, Object> params);

    // 查询主部门、岗位、职级
    List<Map<String, Object>> getUserInfoById(Map<String, Object> params);

    List<Map<String, Object>> queryInstInfosByBusKeys(Map<String, Object> params);

    // 查询部门负责人
    List<Map<String, String>> getDepRespManById(Map<String, Object> params);

    // 查询项目成员
    List<Map<String, Object>> getUserIdOfProject(Map<String, Object> params);

    void updateProjectInstId(Map<String, Object> params);

    List<Map<String, Object>> queryDelivery(Map<String, Object> params);

    List<Map<String, String>> queryStage(Map<String, Object> params);

    Map<String, String> queryStageByCategoryAndNo(Map<String, Object> params);

    void updateProjectStageInfo(Map<String, Object> params);

    void updateProjectPlanTime(Map<String, Object> params);

    void updateProjectFinish(Map<String, Object> params);

    List<Map<String, String>> queryTaskExecutorsByTaskIds(Map<String, Object> params);

    List<Map<String, Object>> queryProjectFileInfos(Map<String, Object> params);

    List<Map<String, Object>> queryPdmProjectFileInfos(Map<String, Object> params);

    void createProjectFileInfos(Map<String, String> params);

    void batchInsertPdmfile(List<Map<String, Object>> dataList);

    void updateProjectFileInfos(Map<String, String> params);

    void deleteProjectFileInfos(Map<String, Object> params);

    void updateProjectBuild(Map<String, Object> params);

    List<Map<String, String>> queryForProjectNo(Map<String, Object> params);

    Integer countForProjectNo(Map<String, Object> params);

    List<Map<String, Object>> queryUserDeps(Map<String, Object> params);

    List<Map<String, Object>> queryUserRoles(Map<String, Object> params);

    List<Map<String, Object>> queryUserZJ(Map<String, Object> params);

    List<Map<String, Object>> queryUserGW(Map<String, Object> params);

    List<Map<String, Object>> getRoleRatioList(Map<String, Object> params);

    List<Map<String, Object>> queryZjList(Map<String, Object> params);

    List<Map<String, Object>> queryRespManByProjectIds(Map<String, Object> params);

    List<Map<String, Object>> queryXcmgDocMgrList(Map<String, Object> params);

    List<Map<String, Object>> queryXcmgDocMgrSameNameList(Map<String, Object> params);

    List<Map<String, Object>> queryProjectStatus(Map<String, Object> params);

    List<Map<String, Object>> queryStandardStoreByPid(Map<String, Object> params);

    void updateProjectPlanDeductScore(Map<String, Object> params);

    void insertMemberStageScore(Map<String, Object> memberStageScore);

    void deleteMemberStageScore(Map<String, Object> params);

    List<Map<String, Object>> queryMemberScoreByPid(Map<String, Object> params);

    List<Map<String, String>> queryUserByGroupName(Map<String, Object> params);

    List<Map<String, String>> queryDepParentById(Map<String, Object> params);

    /**
     * 查询变更申请信息
     *
     * @param params
     * @return
     */
    List<Map<String, String>> queryChangeInfo(Map<String, Object> params);

    /**
     * 查询项目成果类别
     *
     * @param params
     * @return
     */
    List<Map<String, String>> queryProjectAchieveType(Map<String, Object> params);

    // 更新项目基本信息表中的进度追赶状态
    void updateProgressRunStatus(Map<String, Object> params);

    // 插入项目进度追赶状态设置记录
    void insertProgressRunSetRecord(Map<String, Object> params);

    void deleteProgressRunSetByProjectId(String projectId);

    /**
     * 查询室主任
     *
     * @param params
     * @return
     */
    List<Map<String, String>> getOfficeManager(Map<String, Object> params);

    /**
     * 查询本部门的部长、所长、或者副所长
     *
     * @param params
     * @return
     */
    List<Map<String, String>> getDepartmentManager(Map<String, Object> params);

    /**
     * 查询方案类别
     *
     * @param params
     * @return
     */
    List<Map<String, String>> getSolutions(Map<String, Object> params);

    // 清理本阶段和之后阶段的扣分、实际结束时间
    void updatePlanDeductScoreAndEndTime(Map<String, Object> params);

    // 清理之后阶段的实际开始时间
    void updatePlanStartTime(Map<String, Object> params);

    // 清理本阶段和之后阶段的个人积分
    void deleteUserScore(Map<String, Object> params);

    // 根据项目ID查询所有成员信息
    List<JSONObject> queryMemberInfosByProjectId(Map<String, Object> params);

    // 通过项目id查询项目阶段划分
    List<JSONObject> queryStageDivideByProjectId(String projectId);

    void updateActTaskRespman(Map<String, Object> params);

    void updateActIdentityLinkRespman(Map<String, Object> params);

    List<Map<String, Object>> queryMemberScores(Map<String, Object> params);

    // 通过项目id查询计划
    List<JSONObject> queryPlanByProjectId(Map<String, Object> params);

    List<JSONObject> queryDeliveryByIds(Map<String, Object> params);

    List<JSONObject> queryProjectDeliverys(Map<String, Object> params);

    List<JSONObject> queryValidMembersByDeliverys(Map<String, Object> params);

    List<Map<String, Object>> queryRunningProjectList(Map<String, Object> params);

    List<JSONObject> queryInputList(String projectId);

    void insertInput(JSONObject param);

    void updateInput(JSONObject param);

    void deleteInput(JSONObject param);

    List<JSONObject> queryOutList(Map<String, Object> param);

    void insertOut(JSONObject param);

    void updateOut(JSONObject param);

    void deleteOut(JSONObject param);

    void insertAchievement(JSONObject param);

    void updateAchievement(JSONObject param);

    List<JSONObject> queryUserByIds(JSONObject param);

    List<JSONObject> queryExistOutByReferId(JSONObject param);

    String queryFormIdbyStandard(String standardId);

    List<JSONObject> getProductList(JSONObject paramJson);

    List<JSONObject> getProjectDelivery(Map<String, Object> param);

    List<Map<String, Object>> getOtherNeedDelivery(Map<String, Object> params);

    JSONObject getExistProjectDelivery(JSONObject xcmgProjectMember);

    String getUnFinishDelivery(String projectId);

    List<JSONObject> getProductProjectDelivery(JSONObject paramJson);

    List<JSONObject> getDeptDeliveryIds(String deptId);

    List<JSONObject> queryStageEvaluateList(Map<String, Object> params);

    void productUpdate(JSONObject param);

    List<JSONObject> queryTimeByProjectId();
}
