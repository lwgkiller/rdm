package com.redxun.wwrz.report.dao;


import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * @author zhangzhen
 */
@Repository
public interface WwrzReportDao {

    /**
     * 获取报告年份统计
     *
     * @param reportYear
     * @return int
     * */
    Integer getReportData(Integer reportYear);
    /**
     * 获取认证报告类别数据
     *
     * @param paramJson
     * @return json
     * */
    List<JSONObject> getReportTypeData(JSONObject paramJson);
    /**
     * 获取有项目的部门ids
     *
     * @param paramJson
     * @return list
     * */
    List<JSONObject> getProjectDepts(JSONObject paramJson);

    /**
     * 获取部门项目数量
     *
     * @param paramJson
     * @return
     * */
    Integer getDeptProjectData(JSONObject paramJson);
    /**
     * 获取委外认证项目的个数
     *
     * @param paramJson
     * @return int
     * */
    Integer getWwrzProjectData(JSONObject paramJson);
    /**
     * 获取委外认证项目签订合同的个数
     *
     * @param paramJson
     * @return int
     * */
    Integer getSignContractProjectData(JSONObject paramJson);
    /**
     * 获取委外认证费用报表
     *
     * @param paramJson
     * @return int
     * */
    Double getMoneyData(JSONObject paramJson);
    /**
     * 获取计划类报表数据
     *
     * @param paramJson
     * @return inter
     * */
    Integer getPlanData(JSONObject paramJson);
    /**
     * 获取非计划类统计
     *
     * @param paramJson
     * @return int
     * */
    Integer getUnPlanData(JSONObject paramJson);
    /**
     * 获取认证计划涉及部门
     *
     * @param paramJson
     * @return list
     * */
    List<JSONObject> getPlanDeptList(JSONObject paramJson);
    /**
     * 获取部门计划数量
     *
     * @param paramJson
     * @return int
     * */
    Integer getDeptPlanData(JSONObject paramJson);
    /**
     * 获取部门计划外数量
     *
     * @param paramJson
     * @return int
     * */
    Integer getUnDeptPlanData(JSONObject paramJson);
}
