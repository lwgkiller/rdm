package com.redxun.rdmZhgl.core.service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonResult;
import com.redxun.core.json.JsonResultUtil;
import com.redxun.rdmZhgl.core.dao.ZLAndFMOverviewDao;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;

/**
 * 专利和发明-报表服务层
 *
 * @author douhongli
 * @date 2021年6月11日14:38:31
 */
@Service
public class ZLAndFWOverviewService {
    private static final Logger logger = LoggerFactory.getLogger(ZLAndFWOverviewService.class);
    @Resource
    private XcmgProjectOtherDao xcmgProjectOtherDao;

    @Resource
    private ZLAndFMOverviewDao zlAndFMOverviewDao;

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
     * 有效授权中国专利量
     * 
     * @param queryParam
     *            参数
     * @return JsonResult
     * @author douhongli
     * @since 2021年6月10日19:06:58
     */
    public JsonResult queryEffectiveAuthorizedPieChart(String queryParam) {
        JSONObject param = JSONObject.parseObject(queryParam);
        Map<String, Object> resultMap = new HashMap<>();
        // total
        int total = 0;
        // x数据
        List<String> legendData = new ArrayList<>();
        List<JSONObject> seriesData = new ArrayList<>();
        List<JSONObject> listZltzEnumtable = zlAndFMOverviewDao.listZltzEnumtable("专利类型");
        for (JSONObject jsonObject : listZltzEnumtable) {
            legendData.add(jsonObject.getString("enumName"));
            param.put("zllxId", jsonObject.getString("id"));
            List<JSONObject> chinaPatents = zlAndFMOverviewDao.queryEffectiveAuthorizedPieChart(param);
            total += chinaPatents.size();
            seriesData.add(new JSONObject() {
                {
                    put("value", chinaPatents.size());
                    put("name", jsonObject.getString("enumName"));
                }
            });
        }
        resultMap.put("legendData", legendData);
        resultMap.put("seriesData", seriesData);
        resultMap.put("total", total);
        return JsonResultUtil.success(resultMap);
    }

    /**
     * 中国专利申请情况
     * 
     * @param queryParam
     *            参数
     * @return JsonResult
     * @author douhongli
     * @since 2021年6月10日19:06:58
     */
    public JsonResult queryPatenApplyPieChart(String queryParam) {
        JSONObject param = JSONObject.parseObject(queryParam);
        Map<String, Object> resultMap = new HashMap<>();
        // total
        int total = 0;
        // x数据
        List<String> legendData = new ArrayList<>();
        List<JSONObject> seriesData = new ArrayList<>();
        List<JSONObject> listZltzEnumtable = zlAndFMOverviewDao.listZltzEnumtable("专利类型");
        for (JSONObject jsonObject : listZltzEnumtable) {
            legendData.add(jsonObject.getString("enumName"));
            param.put("zllxId", jsonObject.getString("id"));
            List<JSONObject> chinaPatents = zlAndFMOverviewDao.queryPatenApplyPieChart(param);
            total += chinaPatents.size();
            seriesData.add(new JSONObject() {
                {
                    put("value", chinaPatents.size());
                    put("name", jsonObject.getString("enumName"));
                }
            });
        }
        resultMap.put("seriesData", seriesData);
        resultMap.put("legendData", legendData);
        resultMap.put("total", total);
        return JsonResultUtil.success(resultMap);
    }

    /**
     * 当前年度中国有效授权专利量<br/>
     * 专利类型: 发明,案件状态为参数<br/>
     * 未公开发明（受理），已公开未审结发明（公开、实审、一~五通），已审结未授权发明（视为撤回、驳回），已审结已授权发明（授权、失效、放弃）对应的案件状态
     * 
     * @param queryParam
     *            参数
     * @return JsonResult
     * @author douhongli
     * @since 2021年6月10日19:06:58
     */
    public JsonResult queryInventAuthorizedApplyPieChart(String queryParam) {
        JSONObject param = JSONObject.parseObject(queryParam);
        Map<String, Object> resultMap = new HashMap<>();
        // total
        Long total = 0L;
        // legend
        List<String> legendData = new ArrayList<>();
        // series
        List<JSONObject> seriesData = new ArrayList<>();
        // 公开/(有申请日,状态为公开或实审)
        param.put("list", Arrays.asList("033", "018"));
        List<JSONObject> categoryOneList = zlAndFMOverviewDao.queryInventApplyPieChart(param);
        JSONObject categoryOne = new JSONObject() {
            {
                put("value", categoryOneList.size());
                put("name", "公开");
            }
        };
        total += categoryOneList.size();
//        seriesData.add(categoryOne);
        // 受理:案件状态 in (有申请日,状态为受理)参数
        param.put("list", Arrays.asList("016"));
        List<JSONObject> categoryTwoList = zlAndFMOverviewDao.queryInventApplyPieChart(param);
        JSONObject categoryTwo = new JSONObject() {
            {
                put("value", categoryTwoList.size());
                put("name", "受理");
            }
        };
        total += categoryTwoList.size();
//        seriesData.add(categoryTwo);
        // 撤回、驳回:案件状态 in (有申请日,状态为驳回或撤回)参数
        param.put("list", Arrays.asList("024", "027"));
        List<JSONObject> categoryThreeList = zlAndFMOverviewDao.queryInventApplyPieChart(param);
        JSONObject categoryThree = new JSONObject() {
            {
                put("value", categoryThreeList.size());
                put("name", "视撤/驳回");
            }
        };
        total += categoryThreeList.size();
//        seriesData.add(categoryThree);
        // 有效授权:案件状态 in (有授权日期,状态为授权)参数
        String sqDate = "yes";
        param.put("sqDate", sqDate);
        param.put("list", Arrays.asList("026"));
        List<JSONObject> categoryFourList = zlAndFMOverviewDao.queryInventApplyPieChart(param);
        JSONObject categoryFour = new JSONObject() {
            {
                put("value", categoryFourList.size());
                put("name", "有效授权");
            }
        };
        total += categoryFourList.size();
//        seriesData.add(categoryFour);
        // 失效授权/(有授权日期,状态为失效)
        param.put("list", Arrays.asList("034"));
        List<JSONObject> categoryFiveList = zlAndFMOverviewDao.queryInventApplyPieChart(param);
        JSONObject categoryFive = new JSONObject() {
            {
                put("value", categoryFiveList.size());
                put("name", "失效授权");
            }
        };
        total += categoryFiveList.size();
//        seriesData.add(categoryFive);
        sqDate = null;
        param.put("sqDate", sqDate);
        // 审查/(有有申请日,状态为123通)
        param.put("list", Arrays.asList("019", "020", "021"));
        List<JSONObject> categorySixList = zlAndFMOverviewDao.queryInventApplyPieChart(param);
        JSONObject categorySix = new JSONObject() {
            {
                put("value", categorySixList.size());
                put("name", "审查");
            }
        };
        total += categorySixList.size();
//        seriesData.add(categorySix);
        // 办登/(有授权通知发文日,状态为办登)
        String sqfwDate = "yes";
        param.put("sqfwDate", sqfwDate);
        param.put("list", Arrays.asList("025"));
        List<JSONObject> categorySevenList = zlAndFMOverviewDao.queryInventApplyPieChart(param);
        JSONObject categorySeven = new JSONObject() {
            {
                put("value", categorySevenList.size());
                put("name", "办登");
            }
        };
        total += categorySevenList.size();
//        seriesData.add(categorySeven);
//        seriesData.forEach(jsonObject -> {
//            legendData.add(jsonObject.getString("name"));
//        });
        // 图表顺序 原顺序{1：公开 2:受理 3：驳回/视撤 4：有效授权 5：失效授权 6：审查 7：办登}
        // 改为 2-1-6-7-3-4-5

        seriesData.add(categoryTwo);
        seriesData.add(categoryOne);
        seriesData.add(categorySix);
        seriesData.add(categorySeven);
        seriesData.add(categoryThree);
        seriesData.add(categoryFour);
        seriesData.add(categoryFive);

        //@mh 图例顺序：受理--公开--审查--办登--驳回/视撤--有效授权--失效授权
        legendData.add("受理");
        legendData.add("公开");
        legendData.add("审查");
        legendData.add("办登");
        legendData.add("视撤/驳回");
        legendData.add("有效授权");
        legendData.add("失效授权");
        // 图标顺序



        if ((categoryThreeList.size() + categoryFourList.size()) > 0) {
            BigDecimal patentTotal = new BigDecimal((categoryThreeList.size() + categoryFourList.size()));
            BigDecimal patentAuthorized = new BigDecimal(categoryFourList.size());
            resultMap.put("inventApplyPercentage",
                patentAuthorized.divide(patentTotal, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)));
        } else {
            resultMap.put("inventApplyPercentage", 0);
        }
        resultMap.put("legendData", legendData);
        resultMap.put("seriesData", seriesData);
        resultMap.put("total", total);
        return JsonResultUtil.success(resultMap);
    }

    /**
     * 有效授权中国专利量-bar
     * 
     * @param queryParam
     *            参数
     * @return JsonResult
     * @author douhongli
     * @since 2021年6月15日11:13:18
     */
    public JsonResult queryEffectiveAuthorizedBarChart(String queryParam) {
        Map<String, Object> resultMap = new HashMap<>();
        // 组合数据
        List<JSONObject> seriesData = new ArrayList<>();
        // legend
        List<String> legendData = new ArrayList<>();
        // 根据开始月份和结束月份将数字转化为中文月份
        List<String> xAxisData = getXAxisDataByTime(1, 12);
        xAxisData.add("总计");
        JSONObject labelObject = new JSONObject();
        labelObject.put("show", "true");
        labelObject.put("position", "top");
        // 查询本年度所有
        JSONObject param = JSONObject.parseObject(queryParam);
        List<JSONObject> queryEffectiveAuthorizedBarChartList =
            zlAndFMOverviewDao.queryEffectiveAuthorizedBarChart(param);
        // 分组
        Map<String, List<JSONObject>> listMap = queryEffectiveAuthorizedBarChartList.stream()
            .collect(Collectors.groupingBy(jsonObject -> jsonObject.getString("enumName")));
        // 组合
        for (String s : listMap.keySet()) {
            List<Integer> yAxisData = new ArrayList<>();
            legendData.add(s);
            // 获取当前专利类型的所有数据
            List<JSONObject> jsonObjectList = listMap.get(s);
            // 按照月份筛选
            for (int i = 1; i <= 12; i++) {
                int sum = 0;
                for (JSONObject jsonObject : jsonObjectList) {
                    int month = jsonObject.getIntValue("month");
                    if (i == month) {
                        sum++;
                    }
                }
                yAxisData.add(sum);
            }
            // 总计值
            yAxisData.add(jsonObjectList.size());
            JSONObject oneSerie = new JSONObject();
            oneSerie.put("name", s);
            oneSerie.put("type", "bar");
            oneSerie.put("label", labelObject);
            oneSerie.put("data", yAxisData);
            seriesData.add(oneSerie);
        }
        // 月份总计
        List<Integer> yAxisDataByMonth = new ArrayList<>();
        for (int i = 0; i < 13; i++) {
            int total = 0;
            for (JSONObject jsonObject : seriesData) {
                JSONArray data = jsonObject.getJSONArray("data");
                total += data.getIntValue(i);
            }
            yAxisDataByMonth.add(total);
        }
        JSONObject oneSerie = new JSONObject();
        oneSerie.put("name", "总量");
        oneSerie.put("type", "bar");
        oneSerie.put("label", labelObject);
        oneSerie.put("data", yAxisDataByMonth);
        seriesData.add(oneSerie);
        legendData.add(oneSerie.getString("name"));
        resultMap.put("legendData", legendData);
        resultMap.put("xAxisData", xAxisData);
        resultMap.put("series", seriesData);
        return JsonResultUtil.success(resultMap);
    }

    /**
     * 中国专利申请情况-bar
     * 
     * @param queryParam
     *            参数
     * @return JsonResult
     * @author douhongli
     * @since 2021年6月15日16:14:39
     */
    public JsonResult queryPatenApplyBarChart(String queryParam) {
        Map<String, Object> resultMap = new HashMap<>();
        // 组合数据
        List<JSONObject> seriesData = new ArrayList<>();
        // legend
        List<String> legendData = new ArrayList<>();
        // 月份总计
        List<Integer> yAxisDataByMonth = new ArrayList<>();
        // 根据开始月份和结束月份将数字转化为中文月份
        List<String> xAxisData = getXAxisDataByTime(1, 12);
        xAxisData.add("总计");
        JSONObject labelObject = new JSONObject();
        labelObject.put("show", "true");
        labelObject.put("position", "top");
        // 查询本年度所有
        JSONObject param = JSONObject.parseObject(queryParam);
        List<JSONObject> queryPatenApplyBarChartList = zlAndFMOverviewDao.queryPatenApplyBarChart(param);
        // 分组
        Map<String, List<JSONObject>> listMap = queryPatenApplyBarChartList.stream()
            .collect(Collectors.groupingBy(jsonObject -> jsonObject.getString("enumName")));
        // 组合
        for (String s : listMap.keySet()) {
            List<Integer> yAxisData = new ArrayList<>();
            legendData.add(s);
            // 获取当前专利类型的所有数据
            List<JSONObject> jsonObjectList = listMap.get(s);
            // 按照月份筛选
            for (int i = 1; i <= 12; i++) {
                int sum = 0;
                for (JSONObject jsonObject : jsonObjectList) {
                    int month = jsonObject.getIntValue("month");
                    if (i == month) {
                        sum++;
                    }
                }
                yAxisData.add(sum);
            }
            // 总计值
            yAxisData.add(jsonObjectList.size());
            JSONObject oneSerie = new JSONObject();
            oneSerie.put("name", s);
            oneSerie.put("type", "bar");
            oneSerie.put("label", labelObject);
            oneSerie.put("data", yAxisData);
            seriesData.add(oneSerie);
        }
        // 月份总计
        for (int i = 0; i < 13; i++) {
            int total = 0;
            for (JSONObject jsonObject : seriesData) {
                JSONArray data = jsonObject.getJSONArray("data");
                total += data.getIntValue(i);
            }
            yAxisDataByMonth.add(total);
        }
        JSONObject oneSerie = new JSONObject();
        oneSerie.put("name", "总量");
        oneSerie.put("type", "bar");
        oneSerie.put("label", labelObject);
        oneSerie.put("data", yAxisDataByMonth);
        seriesData.add(oneSerie);
        legendData.add(oneSerie.getString("name"));
        resultMap.put("legendData", legendData);
        resultMap.put("xAxisData", xAxisData);
        resultMap.put("series", seriesData);
        return JsonResultUtil.success(resultMap);
    }
}
