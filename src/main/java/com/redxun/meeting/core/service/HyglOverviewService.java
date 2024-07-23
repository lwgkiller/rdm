package com.redxun.meeting.core.service;

import java.text.DecimalFormat;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonResult;
import com.redxun.core.json.JsonResultUtil;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.meeting.core.dao.HyglOverviewDao;
import com.redxun.saweb.util.RequestUtil;

@Service
public class HyglOverviewService {
    private static Logger logger = LoggerFactory.getLogger(HyglOverviewService.class);
    @Autowired
    private HyglOverviewDao hyglOverviewDao;

    public JsonResult queryMeetingType(HttpServletRequest request) {
        String startTime = RequestUtil.getString(request, "startTime", "");
        String endTime = RequestUtil.getString(request, "endTime", "");
        Map<String, Object> resultMap = new HashMap<>();
        JSONObject labelObject = new JSONObject();
        labelObject.put("show", "true");
        labelObject.put("position", "top");
        Map<String, Object> params = new HashMap<>();
        params.put("startTime", startTime);
        params.put("endTime", endTime);
        List<JSONObject> typeList = hyglOverviewDao.queryMeetingType(params);
        // legend
        // 图表数据
        List<JSONObject> seriesData = new ArrayList<>();
        List<String> legendData = new ArrayList<>();
        int totalMeeting = 0;
        for (JSONObject oneData : typeList) {
            JSONObject oneSerie = new JSONObject();
            oneSerie.put("name", oneData.getString("meetingModel"));
            oneSerie.put("value", oneData.getString("countNum"));
            legendData.add(oneSerie.getString("name"));
            seriesData.add(oneSerie);
            totalMeeting = totalMeeting + oneData.getIntValue("countNum");
        }
        resultMap.put("totalMeeting", totalMeeting);
        resultMap.put("legendData", legendData);
        resultMap.put("series", seriesData);
        return JsonResultUtil.success(resultMap);
    }

    public JsonResult queryMeetingDelay(HttpServletRequest request) {
        String startTime = RequestUtil.getString(request, "startTime", "");
        String endTime = RequestUtil.getString(request, "endTime", "");
        Map<String, Object> resultMap = new HashMap<>();
        JSONObject labelObject = new JSONObject();
        labelObject.put("show", "true");
        labelObject.put("position", "top");
        Map<String, Object> params = new HashMap<>();
        params.put("startTime", startTime);
        params.put("endTime", endTime);
        // 总数
        List<JSONObject> delayList = hyglOverviewDao.queryMeetingDelay(params);
        // 轻微
        params.put("qw", "yes");
        params.put("yb", "");
        params.put("yz", "");
        List<JSONObject> delayListQw = hyglOverviewDao.queryMeetingDelayLevel(params);
        // 一般
        params.put("qw", "");
        params.put("yz", "");
        params.put("yb", "yes");
        List<JSONObject> delayListYb = hyglOverviewDao.queryMeetingDelayLevel(params);
        // 严重
        params.put("qw", "");
        params.put("yb", "");
        params.put("yz", "yes");
        List<JSONObject> delayListYz = hyglOverviewDao.queryMeetingDelayLevel(params);
        // 根据超期数量由多到少排序
        Collections.sort(delayList, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject o1, JSONObject o2) {
                return o2.getIntValue("countNum") - o1.getIntValue("countNum");
            }
        });

        Map<String, Integer> deptNameToQw = new HashMap<>();
        for (JSONObject oneData : delayListQw) {
            deptNameToQw.put(oneData.getString("deptName"), oneData.getIntValue("countNum"));
        }
        Map<String, Integer> deptNameToYb = new HashMap<>();
        for (JSONObject oneData : delayListYb) {
            deptNameToYb.put(oneData.getString("deptName"), oneData.getIntValue("countNum"));
        }
        Map<String, Integer> deptNameToYz = new HashMap<>();
        for (JSONObject oneData : delayListYz) {
            deptNameToYz.put(oneData.getString("deptName"), oneData.getIntValue("countNum"));
        }

        // 图表数据
        List<JSONObject> seriesData = new ArrayList<>();
        // x轴数据
        List<String> xAxisData = new ArrayList<String>();
        // y轴数据（总）
        List<Integer> yAxisData = new ArrayList<>();
        List<Integer> yAxisQwData = new ArrayList<>();
        List<Integer> yAxisYbData = new ArrayList<>();
        List<Integer> yAxisYzData = new ArrayList<>();
        List<String> legendData = new ArrayList<>();
        for (JSONObject oneData : delayList) {
            xAxisData.add(oneData.getString("deptName"));
            yAxisData.add(oneData.getIntValue("countNum"));
        }

        for (JSONObject oneData : delayList) {
            String deptName = oneData.getString("deptName");
            if (deptNameToQw.containsKey(deptName)) {
                yAxisQwData.add(deptNameToQw.get(deptName));
            } else {
                yAxisQwData.add(0);
            }
            if (deptNameToYb.containsKey(deptName)) {
                yAxisYbData.add(deptNameToYb.get(deptName));
            } else {
                yAxisYbData.add(0);
            }
            if (deptNameToYz.containsKey(deptName)) {
                yAxisYzData.add(deptNameToYz.get(deptName));
            } else {
                yAxisYzData.add(0);
            }
        }

        JSONObject oneSerie = new JSONObject();
        oneSerie.put("name", "轻微");
        oneSerie.put("type", "bar");
        oneSerie.put("data", yAxisQwData);
        oneSerie.put("stack", "会议纪要");
        oneSerie.put("barGap", "0");
        legendData.add(oneSerie.getString("name"));
        seriesData.add(oneSerie);

        JSONObject twoSerie = new JSONObject();
        twoSerie.put("name", "一般");
        twoSerie.put("type", "bar");
        twoSerie.put("data", yAxisYbData);
        twoSerie.put("stack", "会议纪要");
        twoSerie.put("barGap", "0");
        legendData.add(twoSerie.getString("name"));
        seriesData.add(twoSerie);

        JSONObject threeSerie = new JSONObject();
        threeSerie.put("name", "严重");
        threeSerie.put("type", "bar");
        threeSerie.put("data", yAxisYzData);
        threeSerie.put("stack", "会议纪要");
        threeSerie.put("barGap", "0");
        legendData.add(threeSerie.getString("name"));
        seriesData.add(threeSerie);

        resultMap.put("legendData", legendData);
        resultMap.put("xAxisData", xAxisData);
        resultMap.put("series", seriesData);
        resultMap.put("sumData", yAxisData);
        return JsonResultUtil.success(resultMap);
    }

    public JsonResult queryZRMeetingDelay(HttpServletRequest request) {
        String startTime = RequestUtil.getString(request, "startTime", "");
        String endTime = RequestUtil.getString(request, "endTime", "");
        Map<String, Object> resultMap = new HashMap<>();
        JSONObject labelObject = new JSONObject();
        labelObject.put("show", "true");
        labelObject.put("position", "top");
        Map<String, Object> params = new HashMap<>();
        params.put("startTime", startTime);
        params.put("endTime", endTime);
        // 总数
        List<JSONObject> delayList = hyglOverviewDao.queryZRMeetingDelay(params);
        // 轻微
        params.put("qw", "yes");
        params.put("yb", "");
        params.put("yz", "");
        List<JSONObject> delayListQw = hyglOverviewDao.queryZRMeetingDelayLevel(params);
        // 一般
        params.put("qw", "");
        params.put("yz", "");
        params.put("yb", "yes");
        List<JSONObject> delayListYb = hyglOverviewDao.queryZRMeetingDelayLevel(params);
        // 严重
        params.put("qw", "");
        params.put("yb", "");
        params.put("yz", "yes");
        List<JSONObject> delayListYz = hyglOverviewDao.queryZRMeetingDelayLevel(params);
        // 根据超期数量由多到少排序
        Collections.sort(delayList, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject o1, JSONObject o2) {
                return o2.getIntValue("countNum") - o1.getIntValue("countNum");
            }
        });

        Map<String, Integer> deptNameToQw = new HashMap<>();
        for (JSONObject oneData : delayListQw) {
            deptNameToQw.put(oneData.getString("deptName"), oneData.getIntValue("countNum"));
        }
        Map<String, Integer> deptNameToYb = new HashMap<>();
        for (JSONObject oneData : delayListYb) {
            deptNameToYb.put(oneData.getString("deptName"), oneData.getIntValue("countNum"));
        }
        Map<String, Integer> deptNameToYz = new HashMap<>();
        for (JSONObject oneData : delayListYz) {
            deptNameToYz.put(oneData.getString("deptName"), oneData.getIntValue("countNum"));
        }

        // 图表数据
        List<JSONObject> seriesData = new ArrayList<>();
        // x轴数据
        List<String> xAxisData = new ArrayList<String>();
        // y轴数据
        List<Integer> yAxisData = new ArrayList<>();
        List<Integer> yAxisQwData = new ArrayList<>();
        List<Integer> yAxisYbData = new ArrayList<>();
        List<Integer> yAxisYzData = new ArrayList<>();
        List<String> legendData = new ArrayList<>();

        for (JSONObject oneData : delayList) {
            xAxisData.add(oneData.getString("deptName"));
            yAxisData.add(oneData.getIntValue("countNum"));
        }

        for (JSONObject oneData : delayList) {
            String deptName = oneData.getString("deptName");
            if (deptNameToQw.containsKey(deptName)) {
                yAxisQwData.add(deptNameToQw.get(deptName));
            } else {
                yAxisQwData.add(0);
            }
            if (deptNameToYb.containsKey(deptName)) {
                yAxisYbData.add(deptNameToYb.get(deptName));
            } else {
                yAxisYbData.add(0);
            }
            if (deptNameToYz.containsKey(deptName)) {
                yAxisYzData.add(deptNameToYz.get(deptName));
            } else {
                yAxisYzData.add(0);
            }
        }

        JSONObject oneSerie = new JSONObject();
        oneSerie.put("name", "轻微");
        oneSerie.put("type", "bar");
        oneSerie.put("data", yAxisQwData);
        oneSerie.put("stack", "会议纪要");
        oneSerie.put("barGap", "0");
        legendData.add(oneSerie.getString("name"));
        seriesData.add(oneSerie);

        JSONObject twoSerie = new JSONObject();
        twoSerie.put("name", "一般");
        twoSerie.put("type", "bar");
        twoSerie.put("data", yAxisYbData);
        twoSerie.put("stack", "会议纪要");
        twoSerie.put("barGap", "0");
        legendData.add(twoSerie.getString("name"));
        seriesData.add(twoSerie);

        JSONObject threeSerie = new JSONObject();
        threeSerie.put("name", "严重");
        threeSerie.put("type", "bar");
        threeSerie.put("data", yAxisYzData);
        threeSerie.put("stack", "会议纪要");
        threeSerie.put("barGap", "0");
        legendData.add(threeSerie.getString("name"));
        seriesData.add(threeSerie);

        resultMap.put("legendData", legendData);
        resultMap.put("xAxisData", xAxisData);
        resultMap.put("series", seriesData);
        resultMap.put("sumData", yAxisData);
        return JsonResultUtil.success(resultMap);
    }

    public JsonResult queryRwfjFinish(HttpServletRequest request) {
        String startTime = RequestUtil.getString(request, "startTime", "");
        String endTime = RequestUtil.getString(request, "endTime", "");
        Map<String, Object> resultMap = new HashMap<>();
        JSONObject labelObject = new JSONObject();
        labelObject.put("show", "true");
        labelObject.put("position", "top");
        Map<String, Object> params = new HashMap<>();
        params.put("startTime", startTime);
        params.put("endTime", endTime);
        // 图表数据
        List<JSONObject> seriesData = new ArrayList<>();
        // x轴数据
        List<String> xAxisData = new ArrayList<String>();
        // y轴数据(已完成会议纪要)
        List<Integer> yFinishAxisData = new ArrayList<>();
        // y轴数据(未完成会议纪要)
        List<Integer> yUnfinishAxisData = new ArrayList<>();
        // y轴数据(已完成会议纪要)
        List<Integer> ytotalAxisData = new ArrayList<>();
        // y轴数据(会议总量)
        List<Integer> yMeetingAxisData = new ArrayList<>();
        List<String> legendData = new ArrayList<>();
        // y轴数据(会议纪要完成率)
        List<Double> yRateAxisData = new ArrayList<>();

        List<JSONObject> totalMeetingList = hyglOverviewDao.queryMeetingByDept(params);
        params.put("total", "yes");
        List<JSONObject> totalList = hyglOverviewDao.queryRwfjList(params);
        params.remove("total");
        params.put("finish", "yes");
        List<JSONObject> finishList = hyglOverviewDao.queryRwfjList(params);
        params.remove("finish");
        params.put("unfinish", "yes");
        List<JSONObject> unfinishList = hyglOverviewDao.queryRwfjList(params);

        Map<String, Integer> deptNameToTotal = new HashMap<>();
        for (JSONObject oneData : totalMeetingList) {
            deptNameToTotal.put(oneData.getString("deptName"), oneData.getIntValue("countNum"));
        }
        Map<String, Integer> deptNameToFinish = new HashMap<>();
        for (JSONObject oneData : finishList) {
            deptNameToFinish.put(oneData.getString("deptName"), oneData.getIntValue("countNum"));
        }
        Map<String, Integer> deptNameToUnfinish = new HashMap<>();
        for (JSONObject oneData : unfinishList) {
            deptNameToUnfinish.put(oneData.getString("deptName"), oneData.getIntValue("countNum"));
        }
        for (JSONObject oneData : totalMeetingList) {
            int deptToTotal = 0;
            String deptName = oneData.getString("deptName");
            xAxisData.add(deptName);
            if (deptNameToFinish.containsKey(deptName)) {
                yFinishAxisData.add(deptNameToFinish.get(deptName));
                deptToTotal = deptToTotal + deptNameToFinish.get(deptName);
            } else {
                yFinishAxisData.add(0);
            }
            if (deptNameToUnfinish.containsKey(deptName)) {
                yUnfinishAxisData.add(deptNameToUnfinish.get(deptName));
                deptToTotal = deptToTotal + deptNameToUnfinish.get(deptName);
            } else {
                yUnfinishAxisData.add(0);
            }
            ytotalAxisData.add(deptToTotal);
            yMeetingAxisData.add(oneData.getIntValue("countNum"));
        }

        // 会议纪要完成率计算
        for (int i = 0; i < ytotalAxisData.size(); i++) {
            Integer num = ytotalAxisData.get(i);
            Integer finish = yFinishAxisData.get(i);
            if (num.equals(0)) {
                yRateAxisData.add(0.00);
                continue;
            }
            Double rate = (double)finish / num * 100;
            DecimalFormat df = new DecimalFormat("0.00");
            Double finishRate = 0.00;
            if (StringUtils.isNotBlank(df.format(rate))) {
                finishRate = Double.parseDouble(df.format(rate));
            }

            yRateAxisData.add(finishRate);
        }

        // 根据会议纪要完成率 排序
        // 数据汇总
        List<JSONObject> listJson = new ArrayList<>();
        for (int i = 0; i < yRateAxisData.size(); i++) {
            JSONObject one = new JSONObject();
            // x轴数据
            String x = xAxisData.get(i);
            one.put("x", x);
            // y轴数据(已完成会议纪要)
            Integer yF = yFinishAxisData.get(i);
            one.put("yF", yF);
            // y轴数据(未完成会议纪要)
            Integer yU = yUnfinishAxisData.get(i);
            one.put("yU", yU);
            // y轴数据(会议纪要总量)
            Integer yT = ytotalAxisData.get(i);
            one.put("yT", yT);
            // y轴数据(会议总量)
            Integer yM = yMeetingAxisData.get(i);
            one.put("yM", yM);
            // y轴数据(会议纪要完成率)
            Double yR = yRateAxisData.get(i);
            one.put("yR", yR);
            // 构建
            listJson.add(one);
        }
        // 排序
        // 根据完成率由多到少排序
        Collections.sort(listJson, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject o1, JSONObject o2) {
                return (int)o2.getDoubleValue("yR") * 1000 - (int)o1.getDoubleValue("yR") * 1000;
            }
        });

        // 清空会议纪要数据
        // x轴数据
        xAxisData.clear();
        // y轴数据(已完成会议纪要)
        yFinishAxisData.clear();
        // y轴数据(未完成会议纪要)
        yUnfinishAxisData.clear();
        // y轴数据(会议纪要总量)
        ytotalAxisData.clear();
        // y轴数据(会议总量)
        yMeetingAxisData.clear();
        // y轴数据(会议纪要完成率)
        yRateAxisData.clear();

        // 根据排序结果重新分配顺序
        for (JSONObject one : listJson) {
            xAxisData.add(one.getString("x"));
            yFinishAxisData.add(one.getIntValue("yF"));
            yUnfinishAxisData.add(one.getIntValue("yU"));
            ytotalAxisData.add(one.getIntValue("yT"));
            yMeetingAxisData.add(one.getIntValue("yM"));
            yRateAxisData.add(one.getDouble("yR"));
        }

        JSONObject threeSerie = new JSONObject();
        threeSerie.put("name", "会议数量");
        threeSerie.put("type", "bar");
        threeSerie.put("data", yMeetingAxisData);
        threeSerie.put("stack", "会议数量");
        threeSerie.put("barGap", "0");
        threeSerie.put("label", labelObject);
        legendData.add(threeSerie.getString("name"));
        seriesData.add(threeSerie);
        JSONObject oneSerie = new JSONObject();
        oneSerie.put("name", "会议纪要完成数量");
        oneSerie.put("type", "bar");
        oneSerie.put("data", yFinishAxisData);
        oneSerie.put("stack", "会议纪要");
        oneSerie.put("barGap", "0");
        legendData.add(oneSerie.getString("name"));
        seriesData.add(oneSerie);
        JSONObject twoSerie = new JSONObject();
        twoSerie.put("name", "会议纪要未完成数量");
        twoSerie.put("type", "bar");
        twoSerie.put("data", yUnfinishAxisData);
        twoSerie.put("stack", "会议纪要");
        twoSerie.put("barGap", "0");
        legendData.add(twoSerie.getString("name"));
        seriesData.add(twoSerie);

        JSONObject fourSerie = new JSONObject();
        fourSerie.put("name", "会议纪要完成率");
        fourSerie.put("type", "line");
        fourSerie.put("yAxisIndex", 1);
        fourSerie.put("label", new JSONObject() {
            {
                put("show", true);
                put("position", "top");
                put("color", "black");
                put("formatter", "{c}%");
            }
        });
        fourSerie.put("data", yRateAxisData);
        // 避免与之前的stack冲突导致折线图bug
        fourSerie.put("stack", "会议纪要完成率");
        legendData.add(fourSerie.getString("name"));
        seriesData.add(fourSerie);

        resultMap.put("legendData", legendData);
        resultMap.put("series", seriesData);
        resultMap.put("xAxisData", xAxisData);
        resultMap.put("sumData", ytotalAxisData);
        return JsonResultUtil.success(resultMap);
    }

    public JsonResult queryZRRwfjFinish(HttpServletRequest request) {
        String startTime = RequestUtil.getString(request, "startTime", "");
        String endTime = RequestUtil.getString(request, "endTime", "");
        Map<String, Object> resultMap = new HashMap<>();
        JSONObject labelObject = new JSONObject();
        labelObject.put("show", "true");
        labelObject.put("position", "top");
        Map<String, Object> params = new HashMap<>();
        params.put("startTime", startTime);
        params.put("endTime", endTime);
        // 图表数据
        List<JSONObject> seriesData = new ArrayList<>();
        // x轴数据
        List<String> xAxisData = new ArrayList<String>();
        // y轴数据(已完成会议纪要)
        List<Integer> yFinishAxisData = new ArrayList<>();
        // y轴数据(未完成会议纪要)
        List<Integer> yUnfinishAxisData = new ArrayList<>();
        // y轴数据(已完成会议纪要)
        List<Integer> ytotalAxisData = new ArrayList<>();
        // y轴数据(会议总量)
        List<Integer> yMeetingAxisData = new ArrayList<>();
        List<String> legendData = new ArrayList<>();
        // y轴数据(会议纪要完成率)
        List<Double> yRateAxisData = new ArrayList<>();

        // List<JSONObject> totalMeetingList = hyglOverviewDao.queryMeetingByDept(params);
        params.put("total", "yes");
        List<JSONObject> totalList = hyglOverviewDao.queryZRRwfjList(params);
        params.remove("total");
        params.put("finish", "yes");
        List<JSONObject> finishList = hyglOverviewDao.queryZRRwfjList(params);
        params.remove("finish");
        params.put("unfinish", "yes");
        List<JSONObject> unfinishList = hyglOverviewDao.queryZRRwfjList(params);

        Map<String, Integer> deptNameToTotal = new HashMap<>();
        for (JSONObject oneData : totalList) {
            deptNameToTotal.put(oneData.getString("deptName"), oneData.getIntValue("countNum"));
        }
        Map<String, Integer> deptNameToFinish = new HashMap<>();
        for (JSONObject oneData : finishList) {
            deptNameToFinish.put(oneData.getString("deptName"), oneData.getIntValue("countNum"));
        }
        Map<String, Integer> deptNameToUnfinish = new HashMap<>();
        for (JSONObject oneData : unfinishList) {
            deptNameToUnfinish.put(oneData.getString("deptName"), oneData.getIntValue("countNum"));
        }
        for (JSONObject oneData : totalList) {
            int deptToTotal = 0;
            String deptName = oneData.getString("deptName");
            xAxisData.add(deptName);
            if (deptNameToFinish.containsKey(deptName)) {
                yFinishAxisData.add(deptNameToFinish.get(deptName));
                deptToTotal = deptToTotal + deptNameToFinish.get(deptName);
            } else {
                yFinishAxisData.add(0);
            }
            if (deptNameToUnfinish.containsKey(deptName)) {
                yUnfinishAxisData.add(deptNameToUnfinish.get(deptName));
                deptToTotal = deptToTotal + deptNameToUnfinish.get(deptName);
            } else {
                yUnfinishAxisData.add(0);
            }
            ytotalAxisData.add(deptToTotal);
            yMeetingAxisData.add(oneData.getIntValue("countNum"));
        }

        // 会议纪要完成率计算
        for (int i = 0; i < ytotalAxisData.size(); i++) {
            Integer num = ytotalAxisData.get(i);
            Integer finish = yFinishAxisData.get(i);
            if (num.equals(0)) {
                yRateAxisData.add(0.00);
                continue;
            }
            Double rate = (double)finish / num * 100;
            DecimalFormat df = new DecimalFormat("0.00");
            Double finishRate = 0.00;
            if (StringUtils.isNotBlank(df.format(rate))) {
                finishRate = Double.parseDouble(df.format(rate));
            }

            yRateAxisData.add(finishRate);
        }

        // 根据会议纪要完成率 排序
        // 数据汇总
        List<JSONObject> listJson = new ArrayList<>();
        for (int i = 0; i < yRateAxisData.size(); i++) {
            JSONObject one = new JSONObject();
            // x轴数据
            String x = xAxisData.get(i);
            one.put("x", x);
            // y轴数据(已完成会议纪要)
            Integer yF = yFinishAxisData.get(i);
            one.put("yF", yF);
            // y轴数据(未完成会议纪要)
            Integer yU = yUnfinishAxisData.get(i);
            one.put("yU", yU);
            // y轴数据(会议纪要总量)
            Integer yT = ytotalAxisData.get(i);
            one.put("yT", yT);
            // y轴数据(会议总量)
            Integer yM = yMeetingAxisData.get(i);
            one.put("yM", yM);
            // y轴数据(会议纪要完成率)
            Double yR = yRateAxisData.get(i);
            one.put("yR", yR);
            // 构建
            listJson.add(one);
        }
        // 排序
        // 根据完成率由多到少排序
        Collections.sort(listJson, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject o1, JSONObject o2) {
                return (int)o2.getDoubleValue("yR") * 1000 - (int)o1.getDoubleValue("yR") * 1000;
            }
        });

        // 清空会议纪要数据
        // x轴数据
        xAxisData.clear();
        // y轴数据(已完成会议纪要)
        yFinishAxisData.clear();
        // y轴数据(未完成会议纪要)
        yUnfinishAxisData.clear();
        // y轴数据(会议纪要总量)
        ytotalAxisData.clear();
        // y轴数据(会议总量)
        yMeetingAxisData.clear();
        // y轴数据(会议纪要完成率)
        yRateAxisData.clear();

        // 根据排序结果重新分配顺序
        for (JSONObject one : listJson) {
            xAxisData.add(one.getString("x"));
            yFinishAxisData.add(one.getIntValue("yF"));
            yUnfinishAxisData.add(one.getIntValue("yU"));
            ytotalAxisData.add(one.getIntValue("yT"));
            yMeetingAxisData.add(one.getIntValue("yM"));
            yRateAxisData.add(one.getDouble("yR"));
        }

        JSONObject threeSerie = new JSONObject();
        // threeSerie.put("name", "会议数量");
        // threeSerie.put("type", "bar");
        // threeSerie.put("data", yMeetingAxisData);
        // threeSerie.put("stack", "会议数量");
        // threeSerie.put("barGap", "0");
        // threeSerie.put("label", labelObject);
        // legendData.add(threeSerie.getString("name"));
        // seriesData.add(threeSerie);
        JSONObject oneSerie = new JSONObject();
        oneSerie.put("name", "会议纪要完成数量");
        oneSerie.put("type", "bar");
        oneSerie.put("data", yFinishAxisData);
        oneSerie.put("stack", "会议纪要");
        oneSerie.put("barGap", "0");
        legendData.add(oneSerie.getString("name"));
        seriesData.add(oneSerie);
        JSONObject twoSerie = new JSONObject();
        twoSerie.put("name", "会议纪要未完成数量");
        twoSerie.put("type", "bar");
        twoSerie.put("data", yUnfinishAxisData);
        twoSerie.put("stack", "会议纪要");
        twoSerie.put("barGap", "0");
        legendData.add(twoSerie.getString("name"));
        seriesData.add(twoSerie);

        JSONObject fourSerie = new JSONObject();
        fourSerie.put("name", "会议纪要完成率");
        fourSerie.put("type", "line");
        fourSerie.put("yAxisIndex", 1);
        fourSerie.put("label", new JSONObject() {
            {
                put("show", true);
                put("position", "top");
                put("color", "black");
                put("formatter", "{c}%");
            }
        });
        fourSerie.put("data", yRateAxisData);
        // 避免与之前的stack冲突导致折线图bug
        fourSerie.put("stack", "会议纪要完成率");
        legendData.add(fourSerie.getString("name"));
        seriesData.add(fourSerie);

        resultMap.put("legendData", legendData);
        resultMap.put("series", seriesData);
        resultMap.put("xAxisData", xAxisData);
        resultMap.put("sumData", ytotalAxisData);
        return JsonResultUtil.success(resultMap);
    }

    private List<String> toGetYearList(String start, String end) {
        List<String> result = new ArrayList<>();
        try {
            while (start.compareTo(end) <= 0) {
                result.add(start.substring(0, 4));
                start =
                    DateFormatUtil.format(DateUtil.addYear(DateFormatUtil.parse(start, "yyyy-MM-dd"), 1), "yyyy-MM-dd");
            }
        } catch (Exception e) {
            logger.error("生成年份异常", e);
        }

        return result;
    }
}
