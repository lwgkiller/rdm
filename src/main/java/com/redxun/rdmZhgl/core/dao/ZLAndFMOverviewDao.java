package com.redxun.rdmZhgl.core.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;

/**
 * 专利和发明-报表的数据传输层
 * 
 * @author douhongli
 * @date 2021年6月11日14:36:21
 */
@Repository
public interface ZLAndFMOverviewDao {

    /**
     * 根据类型获取名称
     * 
     * @param enumType
     *            枚举类型
     * @return List<JSONObject>
     * @author douhongli
     * @since 2021年6月15日10:34:13
     */
    List<JSONObject> listZltzEnumtable(@Param("enumType") String enumType);

    /**
     * 有效授权中国专利量
     * 
     * @param param
     *            参数
     * @return List<JSONObject>
     * @author douhongli
     * @since 2021年6月11日10:04:28
     */
    List<JSONObject> queryEffectiveAuthorizedPieChart(JSONObject param);

    /**
     * 有效授权中国专利量 - bar
     * 
     * @param param
     *            参数
     * @return List<JSONObject>
     * @author douhongli
     * @since 2021年6月11日10:04:28
     */
    List<JSONObject> queryEffectiveAuthorizedBarChart(JSONObject param);

    /**
     * 中国专利申请情况 pie
     * 
     * @param param
     *            参数
     * @return List
     * @author douhongli
     * @since 2021年6月10日19:06:58
     */
    List<JSONObject> queryPatenApplyPieChart(JSONObject param);

    /**
     * 中国发明专利申请情况 pie
     * 
     * @param param
     *            参数
     * @return JsonResult
     * @author douhongli
     * @since 2021年6月10日19:06:58
     */
    List<JSONObject> queryInventApplyPieChart(JSONObject param);

    /**
     * 中国专利申请情况-bar
     * 
     * @param param
     *            参数
     * @return JsonResult
     * @author douhongli
     * @since 2021年6月15日16:21:06
     */
    List<JSONObject> queryPatenApplyBarChart(JSONObject param);
}
