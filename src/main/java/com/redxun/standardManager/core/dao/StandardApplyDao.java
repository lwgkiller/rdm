package com.redxun.standardManager.core.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface StandardApplyDao {
    List<Map<String, Object>> queryStandardApplyList(Map<String, Object> params);

    Map<String, Object> queryStandardApplyById(Map<String, Object> params);

    void addStandardApply(Map<String, Object> params);

    void updateStandardApply(Map<String, Object> params);

    void deleteStandardApplyById(String applyId);

    List<Map<String, Object>> queryUserOperateApply(Map<String, Object> params);

    void updateApplyUseStatus(Map<String, Object> params);

    @Select("select count(*) from standard_reviseEnclosure where reviseInfoId = #{applyId} and fileTypeId = '1'")
    String selectIsBzcn(@Param("applyId") String applyId);


    @Select("select DISTINCT t2.typename  from standard_reviseEnclosure  t1 left join standard_revisedeliverable t2 on t1.fileTypeId= t2.id\n" +
            "where t1.reviseInfoId = #{applyId}  and t2.stageKey =  #{stageKey}")
    List<String> selectCurrentNum(@Param("applyId") String applyId,@Param("stageKey") String stageKey);

    @Select("SELECT typename  FROM standard_revisedeliverable WHERE stageKey =  #{stageKey} and is_required = '1'")
    List<String> selectCountNum(@Param("stageKey") String stageKey);

    @Select("select referStandardIds from standard_revisebaseinfo WHERE id =   #{applyId}  ")
    String selectReferStandardIds(@Param("applyId") String applyId);
}
