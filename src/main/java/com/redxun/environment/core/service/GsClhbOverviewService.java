package com.redxun.environment.core.service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonResult;
import com.redxun.core.json.JsonResultUtil;
import com.redxun.environment.core.dao.GsClhbOverviewDao;
import com.redxun.saweb.util.RequestUtil;
import groovy.util.logging.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class GsClhbOverviewService {
    private Logger logger = LogManager.getLogger(GsClhbOverviewService.class);
    @Autowired
    private GsClhbOverviewDao gsClhbOverviewDao;


    //每个产品所申请信息公开的数据汇总
    public JsonResult queryNumByDept(HttpServletRequest request) {
        String startTime = RequestUtil.getString(request, "startTime", "");
        String endTime = RequestUtil.getString(request, "endTime", "");
        Map<String, Object> resultMap = new HashMap<>();
        JSONObject labelObject = new JSONObject();
        labelObject.put("show", "true");
        labelObject.put("position", "top");
        Map<String, List<JSONObject>> deptNameToData = new LinkedHashMap<>();
        Map<String, Object> params = new HashMap<>();
        params.put("startTime", startTime + " 00:00:00");
        params.put("endTime", endTime + " 23:59:59");
        List<JSONObject> cxList = gsClhbOverviewDao.queryNumByDept(params);
        if (cxList != null&& !cxList.isEmpty()) {
            Stream<JSONObject> stream = cxList.stream();
            deptNameToData = stream.collect(Collectors.groupingBy(j -> j.getString("deptName")));
        }
        // 图表数据
        List<JSONObject> seriesData = new ArrayList<>();
        // x轴数据
        List<String> xAxisData = new ArrayList<>();
        // y轴数据(完成数量)
        List<Integer> finishYAxisData = new ArrayList<>();
        // y轴数据(未完成)
        List<Integer> unfinishYAxisData = new ArrayList<>();
        // y轴数据(作废)
        List<Integer> discardYAxisData = new ArrayList<>();
        // y轴数据(引用)
        List<Integer> copyYAxisData = new ArrayList<>();

       deptNameToData.forEach((key,value)->{
           xAxisData.add(key);
           List<JSONObject> oneDataList = value;
           int finishNum=0;
           int unfinishNum=0;
           int discardNum=0;
           int copyNum=0;
           for(JSONObject oneData:oneDataList){
               String status = oneData.getString("STATUS");
               String oldCxId = oneData.getString("oldCxId");
               if("SUCCESS_END".equals(status)){
                   finishNum++;
               }else if("RUNNING".equals(status)||"DRAFTED".equals(status)) {
                   unfinishNum++;
               }else {
                   discardNum++;
               }
               if(oldCxId!=null&& StringUtils.isNotBlank(oldCxId)){
                   copyNum++;
               }
           }
           finishYAxisData.add(finishNum);
           unfinishYAxisData.add(unfinishNum);
           discardYAxisData.add(discardNum);
           copyYAxisData.add(copyNum);
       });
        List<String> legendData = new ArrayList<>();
        JSONObject oneSerie = new JSONObject();
        oneSerie.put("name", "已完成数量");
        oneSerie.put("type", "bar");
        oneSerie.put("label", labelObject);
        oneSerie.put("data", finishYAxisData);
        legendData.add(oneSerie.getString("name"));
        seriesData.add(oneSerie);
        JSONObject twoSerie = new JSONObject();
        twoSerie.put("name", "未完成数量");
        twoSerie.put("type", "bar");
        twoSerie.put("barGap", "0");
        twoSerie.put("label", labelObject);
        twoSerie.put("data", unfinishYAxisData);
        legendData.add(twoSerie.getString("name"));
        seriesData.add(twoSerie);
        JSONObject threeSerie = new JSONObject();
        threeSerie.put("name", "已作废数量");
        threeSerie.put("type", "bar");
        threeSerie.put("barGap", "0");
        threeSerie.put("label", labelObject);
        threeSerie.put("data", discardYAxisData);
        legendData.add(threeSerie.getString("name"));
        seriesData.add(threeSerie);
        JSONObject fourSerie = new JSONObject();
        fourSerie.put("name", "引用数量");
        fourSerie.put("type", "bar");
        fourSerie.put("barGap", "0");
        fourSerie.put("label", labelObject);
        fourSerie.put("data", copyYAxisData);
        legendData.add(fourSerie.getString("name"));
        seriesData.add(fourSerie);

        resultMap.put("legendData", legendData);
        resultMap.put("xAxisData", xAxisData);
        resultMap.put("series", seriesData);
        return JsonResultUtil.success(resultMap);
    }


    //每个产品所超期30天未关闭的流程数量
    public JsonResult queryDelayByDept() {
        Map<String, Object> resultMap = new HashMap<>();
        JSONObject labelObject = new JSONObject();
        labelObject.put("show", "true");
        labelObject.put("position", "top");
        List<JSONObject> delayNumList = gsClhbOverviewDao.queryDelayByDept();
        // 图表数据
        List<JSONObject> seriesData = new ArrayList<>();
        // x轴数据
        List<String> xAxisData = new ArrayList<>();
        // y轴数据(延迟数量)
        List<Integer> delayYAxisData = new ArrayList<>();
            for(JSONObject delayNumJson:delayNumList){
                int delayNum = delayNumJson.getInteger("delayNum");
                String deptName = delayNumJson.getString("deptName");
                xAxisData.add(deptName);
                delayYAxisData.add(delayNum);
            }
        List<String> legendData = new ArrayList<>();
        JSONObject oneSerie = new JSONObject();
        oneSerie.put("name", "延迟数量");
        oneSerie.put("type", "bar");
        oneSerie.put("label", labelObject);
        oneSerie.put("data", delayYAxisData);
        legendData.add(oneSerie.getString("name"));
        seriesData.add(oneSerie);

        resultMap.put("legendData", legendData);
        resultMap.put("xAxisData", xAxisData);
        resultMap.put("series", seriesData);
        return JsonResultUtil.success(resultMap);
    }

    //年度数量
    public JsonResult queryYearNum() {
        Map<String, Object> resultMap = new HashMap<>();
        JSONObject labelObject = new JSONObject();
        labelObject.put("show", "true");
        labelObject.put("position", "top");
        List<JSONObject> yearNumList = gsClhbOverviewDao.queryYearNum();
        // 图表数据
        List<JSONObject> seriesData = new ArrayList<>();
        List<String> legendData = new ArrayList<>();
        for(JSONObject yearNumJson:yearNumList){
            int yearNum = yearNumJson.getInteger("yearNum");
            String yearName = yearNumJson.getString("yearName")+"数量";
            JSONObject oneSerie = new JSONObject();
            oneSerie.put("name", yearName);
            oneSerie.put("value", yearNum);
            legendData.add(oneSerie.getString("name"));
            seriesData.add(oneSerie);
        }
        resultMap.put("legendData", legendData);
        resultMap.put("series", seriesData);
        return JsonResultUtil.success(resultMap);
    }

    //前12个月数量
    public JsonResult queryMonthNum(HttpServletRequest request) {
        String startTime = RequestUtil.getString(request, "startTime", "");
        String endTime = RequestUtil.getString(request, "endTime", "");
        Map<String, Object> params = new HashMap<>();
        params.put("startTime", startTime + " 00:00:00");
        params.put("endTime", endTime + " 23:59:59");
        Map<String, Object> resultMap = new HashMap<>();
        JSONObject labelObject = new JSONObject();
        labelObject.put("show", "true");
        labelObject.put("position", "top");
        List<JSONObject> monthNumList = gsClhbOverviewDao.queryMonthNum(params);
        // 图表数据
        List<JSONObject> seriesData = new ArrayList<>();
        // x轴数据
        List<String> xAxisData = new ArrayList<>();
        // y轴数据
        List<Integer> monthYAxisData = new ArrayList<>();
        for(JSONObject monthNumJson:monthNumList){
            int monthNum = monthNumJson.getInteger("monthNum");
            String monthName = monthNumJson.getString("monthName");
            xAxisData.add(monthName);
            monthYAxisData.add(monthNum);
        }
        List<String> legendData = new ArrayList<>();
        JSONObject oneSerie = new JSONObject();
        oneSerie.put("name", "月度数量");
        oneSerie.put("type", "bar");
        oneSerie.put("label", labelObject);
        oneSerie.put("data", monthYAxisData);
        legendData.add(oneSerie.getString("name"));
        seriesData.add(oneSerie);

        resultMap.put("legendData", legendData);
        resultMap.put("xAxisData", xAxisData);
        resultMap.put("series", seriesData);
        return JsonResultUtil.success(resultMap);
    }

    //各品牌数量
    public JsonResult queryNumByBrand() {
        Map<String, Object> resultMap = new HashMap<>();
        JSONObject labelObject = new JSONObject();
        labelObject.put("show", "true");
        labelObject.put("position", "top");
        List<JSONObject> monthNumList = gsClhbOverviewDao.queryNumByBrand();
        // 图表数据
        List<JSONObject> seriesData = new ArrayList<>();
        // x轴数据
        List<String> xAxisData = new ArrayList<>();
        // y轴数据(延迟数量)
        List<Integer> monthYAxisData = new ArrayList<>();
        for(JSONObject monthNumJson:monthNumList){
            int modelNum = monthNumJson.getInteger("modelNum");
            String brand = monthNumJson.getString("brand");
            xAxisData.add(brand);
            monthYAxisData.add(modelNum);
        }
        List<String> legendData = new ArrayList<>();
        JSONObject oneSerie = new JSONObject();
        oneSerie.put("name", "机型数量");
        oneSerie.put("type", "bar");
        oneSerie.put("label", labelObject);
        oneSerie.put("data", monthYAxisData);
        legendData.add(oneSerie.getString("name"));
        seriesData.add(oneSerie);

        resultMap.put("legendData", legendData);
        resultMap.put("xAxisData", xAxisData);
        resultMap.put("series", seriesData);
        return JsonResultUtil.success(resultMap);
    }
}
