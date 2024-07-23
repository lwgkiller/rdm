package com.redxun.rdmZhgl.core.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

/**
 * 专利和发明-报表的数据传输层
 * @author douhongli
 * @date 2021年6月11日14:36:21
 */
@Repository
public interface ZhglJsjdsOverviewDao {

    /**
     * 查询参数时间段内 提交技术交底书所有部门
     * @return JsonResult
     * @author douhongli
     * @since 2021年6月16日20:00:48
     */
    List<JSONObject> listJsjdsDept(JSONObject queryParam);

    /**
     * 查询参数时间段内 部门技术交底书计划数据
     * @param param 参数
     * @return List
     * @author douhongli
     * @since 2021年6月17日09:46:35
     */
    List<JSONObject> listJsjdsPlan(JSONObject param);

    /**
     * 查询参数时间段内 部门技术交底书计划总数
     * @param deptId 部门id
     * @param endMonth 结束月份
     * @return Integer
     * @author douhongli
     * @since 2021年6月17日09:46:35
     */
    Integer totalJsjdsPlanByParam(@Param("deptId") String deptId, @Param("endMonth") Integer endMonth);

    /**
     * 当前年 部门技术交底书计划总数
     * @param param 参数
     * @return Integer
     * @author douhongli
     * @since 2021年6月17日15:44:06
     */
    Integer totalJsjdsPlan(JSONObject param);

    /**
     * 根据月份查询所有的技术交底书
     * @param month 月份
     * @return int
     * @author douhogli
     * @since 2021年6月17日16:01:49
     */
    int countApprovedJsjdsActual(@Param("month") Integer month);

    /**
     * 当前年 部门发明类交底书计划总数
     * @param param 参数
     * @return Integer
     * @author zwt
     * @since 2021年6月17日15:44:06
     */
    Integer inventTotalJsjdsPlan(JSONObject param);

    /**
     * 根据月份查询所有的发明交底书
     * @param month 月份
     * @return int
     * @author zwt
     * @since 2021年6月17日16:01:49
     */
    int countInventJsjdsActual(@Param("month") Integer month);

}
