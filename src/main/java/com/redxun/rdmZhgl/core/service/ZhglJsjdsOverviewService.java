package com.redxun.rdmZhgl.core.service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonResult;
import com.redxun.core.json.JsonResultUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.rdmZhgl.core.dao.ZhglJsjdsOverviewDao;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;

/**
 * 专利和发明-报表服务层
 *
 * @author douhongli
 * @date 2021年6月11日14:38:31
 */
@Service
public class ZhglJsjdsOverviewService {
    private static final Logger logger = LoggerFactory.getLogger(ZhglJsjdsOverviewService.class);
    @Resource
    private XcmgProjectOtherDao xcmgProjectOtherDao;

    @Resource
    private ZhglJsjdsOverviewDao zhglJsjdsOverviewDao;

    /**
     * 通过开始月份和结束月份参数获取x轴数据
     *
     * @param startMonth
     *            参数
     * @param endMonth
     *            参数
     * @return List<String>
     * @author douhongli
     * @since 2021年6月7日11:16:06
     */
    private List<String> getXAxisDataByTime(int startMonth, int endMonth) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM");
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM", Locale.CHINA);
        List<String> xAxisData = new ArrayList<>();
        for (int i = startMonth; i <= endMonth; i++) {
            try {
                String chinaMonthName = sdf.format(simpleDateFormat.parse(String.valueOf(i)));
                xAxisData.add(chinaMonthName);
            } catch (ParseException e) {
                logger.debug("解析时间格式出错: 异常信息{}", e.getMessage());
            }
        }
        return xAxisData;
    }

    /**
     * 将月份数值转化为中文
     * 
     * @param monthInt
     *            月份数值
     * @return String
     * @author douhongli
     * @since 2021年6月7日11:32:34
     */
    private String getMonthName(int monthInt) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM");
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM", Locale.CHINA);
        String chinaMonthName = null;
        try {
            chinaMonthName = sdf.format(simpleDateFormat.parse(String.valueOf(monthInt)));
        } catch (ParseException e) {
            logger.debug("解析时间格式出错: 异常信息{}", e.getMessage());
        }
        return chinaMonthName;
    }

    /**
     * 各部门提交技术交底书数量
     * 
     * @param queryParam
     *            参数
     * @return JsonResult
     * @author douhongli
     * @since 2021年6月16日20:00:48
     */
    public JsonResult queryJsjdsByDept(JSONObject queryParam) {
        Map<String, Object> resultMap = new HashMap<>();
        JSONObject labelObject = new JSONObject();
        labelObject.put("show", "true");
        labelObject.put("position", "top");
        // 图表数据
        List<JSONObject> seriesData = new ArrayList<>();
        // legend
        List<String> legendData = new ArrayList<>();
        // x轴数据
        List<String> xAxisData = new ArrayList<>();
        // y轴数据
        List<Integer> yAxisData = new ArrayList<>();
        List<Integer> yAxisPercentageData = new ArrayList<>();
        // 差值数
        List<Integer> diffData = new ArrayList<>();
        List<Integer> totalData = new ArrayList<>();
        if (StringUtils.isNotBlank(queryParam.getString("startTime"))) {
            queryParam.put("startTime", queryParam.getString("startTime") + " 00:00:00");
        }
        if (StringUtils.isNotBlank(queryParam.getString("endTime"))) {
            queryParam.put("endTime", queryParam.getString("endTime") + " 23:59:59");
        }
        List<JSONObject> jsjdsDeptList = zhglJsjdsOverviewDao.listJsjdsDept(queryParam);
        for (JSONObject jsonObject : jsjdsDeptList) {
            xAxisData.add(jsonObject.getString("deptName"));
            yAxisData.add(jsonObject.getIntValue("count"));

            queryParam.put("deptId", jsonObject.getString("deptId"));
            // 参数时间段内当前部门的技术交底书总数
            List<JSONObject> listJsjdsPlan = zhglJsjdsOverviewDao.listJsjdsPlan(new JSONObject() {
                {
                    put("deptId", jsonObject.getString("deptId"));
                }
            });
            int total = listJsjdsPlan.stream().filter(item -> {
                String month = item.getString("month");
                Integer monthInt = Integer.valueOf(StringUtil.trimSuffix(month, "月"));
                return monthInt <= DateUtil.getMonth(queryParam.getDate("endTime")) + 1;
            }).mapToInt(value -> value.getIntValue("total")).sum();
            totalData.add(total);
            if (total > 0) {
                int diffValue =
                    total - jsonObject.getIntValue("count") < 0 ? 0 : total - jsonObject.getIntValue("count");
                diffData.add(diffValue);
                BigDecimal result =
                    jsonObject.getBigDecimal("count").divide(new BigDecimal(total), 2, BigDecimal.ROUND_HALF_UP);
                yAxisPercentageData.add(result.multiply(new BigDecimal(100)).intValue());
            } else {
                diffData.add(0);
                yAxisPercentageData.add(0);
            }
        }
        JSONObject oneSerie = new JSONObject();
        oneSerie.put("name", "已审批完成数");
        oneSerie.put("type", "bar");
        oneSerie.put("stack", "bar1");
        // oneSerie.put("label", labelObject);
        oneSerie.put("data", yAxisData);
        legendData.add(oneSerie.getString("name"));
        seriesData.add(oneSerie);
        JSONObject twoSerie = new JSONObject();
        twoSerie.put("name", "总量完成率");
        twoSerie.put("type", "line");
        twoSerie.put("yAxisIndex", 1);
        twoSerie.put("label", new JSONObject() {
            {
                put("show", true);
                put("position", "top");
                put("color", "black");
                put("formatter", "{c}%");
            }
        });
        twoSerie.put("data", yAxisPercentageData);
        JSONObject sumSerie = new JSONObject();
        sumSerie.put("name", "未审批完成数");
        sumSerie.put("type", "bar");
        sumSerie.put("stack", "bar1");
        // sumSerie.put("label", labelObject);
        sumSerie.put("data", diffData);
        legendData.add(sumSerie.getString("name"));
        seriesData.add(sumSerie);
        legendData.add(twoSerie.getString("name"));
        seriesData.add(twoSerie);
        resultMap.put("legendData", legendData);
        resultMap.put("xAxisData", xAxisData);
        resultMap.put("series", seriesData);
        resultMap.put("totalData", totalData);
        return JsonResultUtil.success(resultMap);
    }

    /**
     * 各部门提交技术发明类交底书数量
     * 
     * @return JsonResult
     * @author douhongli
     * @since 2021年6月17日14:54:16
     */
    public JsonResult queryInventJsjdsByDept(JSONObject queryParam) {
        Map<String, Object> resultMap = new HashMap<>();
        JSONObject labelObject = new JSONObject();
        labelObject.put("show", "true");
        labelObject.put("position", "top");
        // 图表数据
        List<JSONObject> seriesData = new ArrayList<>();
        // legend
        List<String> legendData = new ArrayList<>();
        // x轴数据
        List<String> xAxisData = new ArrayList<>();
        // y轴数据
        List<Integer> yAxisData = new ArrayList<>();
        List<Integer> yAxisPercentageData = new ArrayList<>();
        // 差值数
        List<Integer> diffData = new ArrayList<>();
        // 总数
        List<Integer> totalData = new ArrayList<>();
        if (StringUtils.isNotBlank(queryParam.getString("startTime"))) {
            queryParam.put("startTime", queryParam.getString("startTime") + " 00:00:00");
        }
        if (StringUtils.isNotBlank(queryParam.getString("endTime"))) {
            queryParam.put("endTime", queryParam.getString("endTime") + " 23:59:59");
        }
        List<JSONObject> jsjdsDeptList = zhglJsjdsOverviewDao.listJsjdsDept(queryParam);
        for (JSONObject jsonObject : jsjdsDeptList) {
            xAxisData.add(jsonObject.getString("deptName"));
            yAxisData.add(jsonObject.getIntValue("count"));
            // 参数时间段内当前部门的发明类技术交底书总数
            Integer total = zhglJsjdsOverviewDao.totalJsjdsPlanByParam(jsonObject.getString("deptId"),
                DateUtil.getMonth(queryParam.getDate("endTime")) + 1);
            if (total == null) {
                total = 0;
            }
            totalData.add(total);
            if (total != null && total > 0) {
                int diffValue =
                    total - jsonObject.getIntValue("count") < 0 ? 0 : total - jsonObject.getIntValue("count");
                diffData.add(diffValue);
                BigDecimal result =
                    jsonObject.getBigDecimal("count").divide(new BigDecimal(total), 2, BigDecimal.ROUND_HALF_UP);
                yAxisPercentageData.add(result.multiply(new BigDecimal(100)).intValue());
            } else {
                diffData.add(0);
                yAxisPercentageData.add(0);
            }
        }
        JSONObject oneSerie = new JSONObject();
        oneSerie.put("name", "已审批完成数");
        oneSerie.put("type", "bar");
        oneSerie.put("stack", "bar1");
        // oneSerie.put("label", labelObject);
        oneSerie.put("data", yAxisData);
        legendData.add(oneSerie.getString("name"));
        seriesData.add(oneSerie);
        JSONObject twoSerie = new JSONObject();
        twoSerie.put("name", "总量完成率");
        twoSerie.put("type", "line");
        twoSerie.put("yAxisIndex", 1);
        twoSerie.put("label", new JSONObject() {
            {
                put("show", true);
                put("position", "top");
                put("color", "black");
                put("formatter", "{c}%");
            }
        });
        twoSerie.put("data", yAxisPercentageData);
        JSONObject sumSerie = new JSONObject();
        sumSerie.put("name", "未审批完成数");
        sumSerie.put("type", "bar");
        sumSerie.put("stack", "bar1");
        sumSerie.put("data", diffData);
        legendData.add(sumSerie.getString("name"));
        seriesData.add(sumSerie);
        legendData.add(twoSerie.getString("name"));
        seriesData.add(twoSerie);
        resultMap.put("legendData", legendData);
        resultMap.put("xAxisData", xAxisData);
        resultMap.put("series", seriesData);
        resultMap.put("totalData", totalData);
        return JsonResultUtil.success(resultMap);
    }

    /**
     * 技术交底书计划数报表
     * 
     * @param param
     *            参数
     * @return JsonResult
     * @author douhongli
     * @since 2021年6月17日15:17:07
     */
    public JsonResult queryJsjdsPlanBarBarChart(JSONObject param) {
        Map<String, Object> resultMap = new HashMap<>();
        // 组合数据
        List<JSONObject> seriesData = new ArrayList<>();
        // legend
        List<String> legendData = new ArrayList<>();
        // y轴月份总计
        List<Integer> yAxisDataByMonth = new ArrayList<>();
        // 根据开始月份和结束月份将数字转化为中文月份
        List<String> xAxisData = getXAxisDataByTime(1, DateUtil.getCurMonth() + 1);
        xAxisData.add("总计");
        JSONObject labelObject = new JSONObject();
        labelObject.put("show", "true");
        labelObject.put("position", "top");
        // 计划总数
        Integer jsjdsPlanTotal = zhglJsjdsOverviewDao.totalJsjdsPlan(param);
        if (jsjdsPlanTotal == null) {
            jsjdsPlanTotal = 0;
        }
        // 当前年 一到当前月所有的已提交的数量 按月统计
        int total = 0;
        for (int i = 1; i <= DateUtil.getCurMonth() + 1; i++) {
            int actualTotal = zhglJsjdsOverviewDao.countApprovedJsjdsActual(i);
            total += actualTotal;
            yAxisDataByMonth.add(actualTotal);
        }
        // 今年总数
        yAxisDataByMonth.add(total);
        // serieData
        JSONObject oneSerie = new JSONObject();
        oneSerie.put("name", "技术交底书数量");
        oneSerie.put("type", "bar");
        oneSerie.put("label", labelObject);
        oneSerie.put("data", yAxisDataByMonth);
        seriesData.add(oneSerie);
        // legendData
        legendData.add(oneSerie.getString("name"));
        // 返回值
        if (jsjdsPlanTotal > 0) {
            BigDecimal progressNum = new BigDecimal(total)
                .divide(new BigDecimal(jsjdsPlanTotal), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
            resultMap.put("progress", progressNum);
        } else {
            resultMap.put("progress", 0);
        }
        resultMap.put("jsjdsPlanTotal", jsjdsPlanTotal);
        resultMap.put("legendData", legendData);
        resultMap.put("xAxisData", xAxisData);
        resultMap.put("series", seriesData);
        return JsonResultUtil.success(resultMap);
    }


    /**
     * 发明技术交底书计划数报表
     *
     * @param param
     *            参数
     * @return JsonResult
     * @author zwt
     * @since
     */
    public JsonResult queryInventJsjdsPlanBarChart(JSONObject param) {
        Map<String, Object> resultMap = new HashMap<>();
        // 组合数据
        List<JSONObject> seriesData = new ArrayList<>();
        // legend
        List<String> legendData = new ArrayList<>();
        // y轴月份总计
        List<Integer> yAxisDataByMonth = new ArrayList<>();
        // 根据开始月份和结束月份将数字转化为中文月份
        List<String> xAxisData = getXAxisDataByTime(1, DateUtil.getCurMonth() + 1);
        xAxisData.add("总计");
        JSONObject labelObject = new JSONObject();
        labelObject.put("show", "true");
        labelObject.put("position", "top");
        // 发明类计划总数
        Integer inventJsjdsPlanTotal = zhglJsjdsOverviewDao.inventTotalJsjdsPlan(param);
        if (inventJsjdsPlanTotal == null) {
            inventJsjdsPlanTotal = 0;
        }
        // 当前年 一到当前月所有的已提交的数量 按月统计
        int total = 0;
        for (int i = 1; i <= DateUtil.getCurMonth() + 1; i++) {
            int actualTotal = zhglJsjdsOverviewDao.countInventJsjdsActual(i);
            total += actualTotal;
            yAxisDataByMonth.add(actualTotal);
        }
        // 今年总数
        yAxisDataByMonth.add(total);
        // serieData
        JSONObject oneSerie = new JSONObject();
        oneSerie.put("name", "发明类技术交底书数量");
        oneSerie.put("type", "bar");
        oneSerie.put("label", labelObject);
        oneSerie.put("data", yAxisDataByMonth);
        seriesData.add(oneSerie);
        // legendData
        legendData.add(oneSerie.getString("name"));
        // 返回值
        if (inventJsjdsPlanTotal > 0) {
            BigDecimal progressNum = new BigDecimal(total)
                    .divide(new BigDecimal(inventJsjdsPlanTotal), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
            resultMap.put("progress", progressNum);
        } else {
            resultMap.put("progress", 0);
        }
        resultMap.put("inventJsjdsPlanTotal", inventJsjdsPlanTotal);
        resultMap.put("legendData", legendData);
        resultMap.put("xAxisData", xAxisData);
        resultMap.put("series", seriesData);
        return JsonResultUtil.success(resultMap);
    }
}
