package com.redxun.standardManager.core.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

@Repository
public interface StandardManagementDao {
    List<JSONObject> queryStandardList(Map<String, Object> params);

    JSONObject queryDemandDetailById(String formId);

    void addStandardDemand(Map<String, Object> params);

    void updateStandardDemand(Map<String, Object> params);

    void deleteStandardById(String applyId);

    List<JSONObject> queryStandardFileTypes(@Param("stageKey") String stageKey);

    List<JSONObject> queryStandardFileList(Map<String, Object> param);

    void addStandardFileInfos(JSONObject param);

    List<JSONObject> getStandardList(@Param("applyId") String applyId, @Param("user") String user,
        @Param("isZqyj") String isZqyj);

    List<JSONObject> getTeamDraftList(@Param("applyId") String applyId, @Param("user") String user,
        @Param("isMkfzr") String isMkfzr);

    Map<String, Object> getStandard(@Param("standardId") String standardId);

    void saveUserList(JSONObject param);

    void updateUserList(JSONObject param);

    void delUserListById(Map<String, Object> param);

    @Select("SELECT reviseInfoId from standard_reviseEnclosure  WHERE id = #{id} ")
    String selectStandardByFjId(@Param("id") String id);

    void delStandardById(Map<String, Object> param);

    List<Map<String, Object>> queryTaskAll(Map<String, Object> param);

    void updateFinalStandardId2Task(Map<String, Object> param);

    @Select("SELECT * FROM standard_revisebaseinfo WHERE id =#{applyId} ")
    JSONObject selectRight(@Param("currentUserId") String currentUserId, @Param("applyId") String applyId);

    void insertBzxdssyj(JSONObject param);

    void updateBzxdssyj(JSONObject param);

    void deleteBzxdssyj(Map<String, Object> param);

    //@mh  团队成员意见
    void insertTeamDraft(JSONObject param);

    void updateTeamDraft(JSONObject param);

    void deleteTeamDraft(Map<String, Object> param);

    List<JSONObject> getSsyjList(JSONObject jsonObject);

    void updateDqzt(Map<String, Object> params);

    List<JSONObject> queryRevisePlan(Map<String, Object> param);

    void insertRevisePlan(JSONObject param);

    void updateRevisePlan(JSONObject param);

    List<JSONObject> queryRevisePlanTotal(Map<String, Object> param);

    List<JSONObject> queryYearTotal(Map<String, Object> param);

    List<JSONObject> queryReviseDeptFinish(Map<String, Object> param);

    List<JSONObject> queryJdTotal(Map<String, Object> param);

    List<JSONObject> queryJdFinish(Map<String, Object> param);

    List<JSONObject> queryDept(Map<String, Object> param);

    List<JSONObject> queryStandardByIds(Map<String, Object> param);
}
