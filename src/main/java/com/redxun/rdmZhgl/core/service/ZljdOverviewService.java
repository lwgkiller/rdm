package com.redxun.rdmZhgl.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.json.JsonResultUtil;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmZhgl.core.dao.ZljdOverviewDao;
import com.redxun.saweb.util.RequestUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
public class ZljdOverviewService {
    private static Logger logger = LoggerFactory.getLogger(ZljdOverviewService.class);
    @Autowired
    private ZljdOverviewDao zljdOverviewDao;

    public JsonResult queryValue(HttpServletRequest request) {
        String startTime = RequestUtil.getString(request, "startTime", "");
        String endTime = RequestUtil.getString(request, "endTime", "");
        Map<String, Object> resultMap = new HashMap<>();
        JSONObject labelObject = new JSONObject();
        labelObject.put("show", "true");
        labelObject.put("position", "top");
        // 时间段内各部门应完成的总数
        Map<String, Object> params = new HashMap<>();
        params.put("startTime", startTime.substring(0, 7));
        params.put("endTime", endTime.substring(0, 7));
        List<JSONObject> revisePlanDeptList = zljdOverviewDao.queryValue(params);

        // 图表数据
        List<JSONObject> seriesData = new ArrayList<>();
        // x轴数据
        List<String> xAxisData = new ArrayList<String>();
        // y轴数据(无价值)
        List<Integer> nullYAxisData = new ArrayList<>();
        // y轴数据(部分价值)
        List<Integer> halfYAxisData = new ArrayList<>();
        // y轴数据(有价值)
        List<Integer> fullYAxisData = new ArrayList<>();
        xAxisData.add("三一");
        xAxisData.add("卡特");
        for (JSONObject oneData : revisePlanDeptList) {
            String applicationValue = oneData.getString("applicationValue");
            int count = oneData.getIntValue("count(*)");

            if ("无".equals(applicationValue)) {
                nullYAxisData.add(count);
            } else if ("部分".equals(applicationValue)) {
                halfYAxisData.add(count);
            } else if ("有".equals(applicationValue)) {
                fullYAxisData.add(count);
            }
        }
        // legend
        List<String> legendData = new ArrayList<>();
        JSONObject threeSerie = new JSONObject();
        threeSerie.put("name", "有应用价值");
        threeSerie.put("type", "bar");
        threeSerie.put("label", labelObject);
        threeSerie.put("data", fullYAxisData);
        legendData.add(threeSerie.getString("name"));
        seriesData.add(threeSerie);
        JSONObject oneSerie = new JSONObject();
        oneSerie.put("name", "部分应用价值");
        oneSerie.put("type", "bar");
        oneSerie.put("barGap", "0");
        oneSerie.put("label", labelObject);
        oneSerie.put("data", halfYAxisData);
        legendData.add(oneSerie.getString("name"));
        seriesData.add(oneSerie);
        JSONObject twoSerie = new JSONObject();
        twoSerie.put("name", "无应用价值");
        twoSerie.put("type", "bar");
        twoSerie.put("barGap", "0");
        twoSerie.put("label", labelObject);
        twoSerie.put("data", nullYAxisData);
        legendData.add(twoSerie.getString("name"));
        seriesData.add(twoSerie);
        resultMap.put("legendData", legendData);
        resultMap.put("xAxisData", xAxisData);
        resultMap.put("series", seriesData);
        return JsonResultUtil.success(resultMap);
    }


    public JsonResult queryNum(HttpServletRequest request) {
        DecimalFormat df = new DecimalFormat("0.00");
        String startTime = RequestUtil.getString(request, "startTime", "");
        String endTime = RequestUtil.getString(request, "endTime", "");
        Map<String, Object> resultMap = new HashMap<>();
        JSONObject labelObject = new JSONObject();
        labelObject.put("show", "true");
        labelObject.put("position", "top");
        // 时间段内各部门应完成的总数
        Map<String, Object> params = new HashMap<>();
        params.put("startTime", startTime.substring(0, 7));
        params.put("endTime", endTime.substring(0, 7));
        JSONObject unfinishList = zljdOverviewDao.queryZljdUnfinishNum(params);
        JSONObject finishList = zljdOverviewDao.queryZljdFinished(params);
        JSONObject delayList = zljdOverviewDao.queryZljdDelay(params);
        // 图表数据
        List<JSONObject> seriesData = new ArrayList<>();
        // legend
        List<String> legendData = new ArrayList<>();
        JSONObject threeSerie = new JSONObject();
        threeSerie.put("name", "待完成");
        threeSerie.put("value", unfinishList.getInteger("unFinishNum"));
        legendData.add(threeSerie.getString("name"));
        seriesData.add(threeSerie);
        JSONObject oneSerie = new JSONObject();
        oneSerie.put("name", "已完成");
        oneSerie.put("value", finishList.getInteger("finishNum"));
        legendData.add(oneSerie.getString("name"));
        seriesData.add(oneSerie);
        JSONObject twoSerie = new JSONObject();
        twoSerie.put("name", "延期未完成");
        twoSerie.put("value", delayList.getInteger("delayNum"));
        legendData.add(twoSerie.getString("name"));
        seriesData.add(twoSerie);
        resultMap.put("legendData", legendData);
        resultMap.put("series", seriesData);
        return JsonResultUtil.success(resultMap);
    }

    public JsonResult queryReTotalNum(HttpServletRequest request) {
        DecimalFormat df = new DecimalFormat("0.00");
        String startTime = RequestUtil.getString(request, "startTime", "");
        String endTime = RequestUtil.getString(request, "endTime", "");
        Map<String, Object> resultMap = new HashMap<>();
        JSONObject labelObject = new JSONObject();
        labelObject.put("show", "true");
        labelObject.put("position", "top");
        // 时间段内各部门应完成的总数
        Map<String, Object> params = new HashMap<>();
        params.put("startTime", startTime.substring(0, 7));
        params.put("endTime", endTime.substring(0, 7));
        List<JSONObject> reTotalNum = zljdOverviewDao.queryReTotalNum(params);
        int reFinfish = 0;
        int reUnFinfish = 0;
        for (JSONObject oneData : reTotalNum) {
            if (StringUtils.isNotBlank(oneData.getString("chuangXinJiaoDiShuNo"))) {
                reFinfish++;
            } else {
                reUnFinfish++;
            }
        }
        // 图表数据
        List<JSONObject> seriesData = new ArrayList<>();
        // legend
        List<String> legendData = new ArrayList<>();
        JSONObject threeSerie = new JSONObject();
        threeSerie.put("name", "待提交申请");
        threeSerie.put("value", reUnFinfish);
        legendData.add(threeSerie.getString("name"));
        seriesData.add(threeSerie);
        JSONObject oneSerie = new JSONObject();
        oneSerie.put("name", "已提交申请");
        oneSerie.put("value", reFinfish);
        legendData.add(oneSerie.getString("name"));
        seriesData.add(oneSerie);
        resultMap.put("legendData", legendData);
        resultMap.put("series", seriesData);
        return JsonResultUtil.success(resultMap);
    }

    public JsonResult queryZljdNumByuser(HttpServletRequest request) {
        String startTime = RequestUtil.getString(request, "startTime", "");
        String endTime = RequestUtil.getString(request, "endTime", "");
        Map<String, Object> resultMap = new HashMap<>();
        JSONObject labelObject = new JSONObject();
        List<JSONObject> userToResult = new ArrayList<>();
        labelObject.put("show", "true");
        labelObject.put("position", "top");
        // 图表数据
        List<JSONObject> seriesData = new ArrayList<>();
        // x轴数据
        List<String> xAxisData = new ArrayList<String>();
        // y轴数据(无价值)
        List<Integer> yAxisData = new ArrayList<>();
        // 时间段内各部门应完成的总数
        Map<String, Object> params = new HashMap<>();
        params.put("startTime", startTime.substring(0, 7));
        params.put("endTime", endTime.substring(0, 7));
        List<JSONObject> revisePlanDeptList = zljdOverviewDao.queryZljdNumByuser(params);
        Stream<JSONObject> stream = revisePlanDeptList.stream();
        Map<String, List<JSONObject>> userIdToZljd =
                stream.collect(Collectors.groupingBy(j -> j.getString("interpreterUserId")));
        if (userIdToZljd != null && !userIdToZljd.isEmpty()) {
            Set keys = userIdToZljd.keySet();
            Iterator it = keys.iterator();
            while (it.hasNext()) {
                JSONObject result = new JSONObject();
                String userId = (String) it.next();
                List<JSONObject> oneuser = userIdToZljd.get(userId);
                double num = 0;
                String userName = null;
                String userDept = null;
                for (JSONObject onedata : oneuser) {
                    userName = onedata.getString("userName");
                    userDept = onedata.getString("userDept");
                    if (!"覆盖件外观专利".equals(onedata.getString("description"))) {
                        if ("优秀".equals(onedata.getString("interpretationEvaluation"))) {
                            num += 3;
                        } else if ("良好".equals(onedata.getString("interpretationEvaluation"))) {
                            num += 2;
                        } else if ("合格".equals(onedata.getString("interpretationEvaluation"))) {
                            num += 1;
                        }
                    } else {
                        if ("优秀".equals(onedata.getString("interpretationEvaluation"))) {
                            num += 1.5;
                        } else if ("良好".equals(onedata.getString("interpretationEvaluation"))) {
                            num += 1;
                        } else if ("合格".equals(onedata.getString("interpretationEvaluation"))) {
                            num += 0.5;
                        }
                    }
                }
                result.put("userId", userId);
                result.put("userName", userName);
                result.put("userDept", userDept);
                result.put("num", num);
                userToResult.add(result);
            }
            List<JSONObject> userToResultList = userToResult.stream().sorted((o1, o2) -> {
                if (o1.getDouble("num").compareTo(o2.getDouble("num")) >= 0) {
                    return -1;
                } else return 1;
            }).collect(Collectors.toList());
            int index = 0;
            int lastNum = 0;
            for (int i = 0; i < userToResultList.size(); i++) {
                JSONObject oneResult = userToResultList.get(i);
                int num = oneResult.getInteger("num");
                String userName = oneResult.getString("userName");
                String userDept = oneResult.getString("userDept");
                yAxisData.add(num);
                xAxisData.add(userName);
                lastNum = num;
                if (i == 9) {
                    break;
                }
            }
            for (int j = 10; j < userToResultList.size(); j++) {
                JSONObject nextResult = userToResultList.get(j);
                int nextnum = nextResult.getInteger("num");
                String nextuserName = nextResult.getString("userName");
                String nextuserDept = nextResult.getString("userDept");
                if (lastNum == nextnum) {
                    yAxisData.add(nextnum);
                    xAxisData.add(nextuserName);
                } else {
                    break;
                }
            }
        }

        // legend
        List<String> legendData = new ArrayList<>();
        JSONObject oneSerie = new JSONObject();
        oneSerie.put("name", "总分");
        oneSerie.put("type", "bar");
        oneSerie.put("barGap", "0");
        oneSerie.put("label", labelObject);
        oneSerie.put("data", yAxisData);
        legendData.add(oneSerie.getString("name"));
        seriesData.add(oneSerie);
        resultMap.put("legendData", legendData);
        resultMap.put("xAxisData", xAxisData);
        resultMap.put("series", seriesData);
        return JsonResultUtil.success(resultMap);
    }

    public JsonResult queryReNumByuser(HttpServletRequest request) {
        String startTime = RequestUtil.getString(request, "startTime", "");
        String endTime = RequestUtil.getString(request, "endTime", "");
        Map<String, Object> resultMap = new HashMap<>();
        JSONObject labelObject = new JSONObject();
        List<JSONObject> userToResult = new ArrayList<>();
        labelObject.put("show", "true");
        labelObject.put("position", "top");
        // 图表数据
        List<JSONObject> seriesData = new ArrayList<>();
        // x轴数据
        List<String> xAxisData = new ArrayList<String>();
        // y轴数据(无价值)
        List<Integer> yAxisData = new ArrayList<>();
        // 时间段内各部门应完成的总数
        Map<String, Object> params = new HashMap<>();
        params.put("startTime", startTime.substring(0, 7));
        params.put("endTime", endTime.substring(0, 7));
        List<JSONObject> revisePlanDeptList = zljdOverviewDao.queryReNumByuser(params);
        Stream<JSONObject> stream = revisePlanDeptList.stream();
        Map<String, List<JSONObject>> userIdToZljd =
                stream.collect(Collectors.groupingBy(j -> j.getString("chuangXinRenId")));
        if (userIdToZljd != null && !userIdToZljd.isEmpty()) {
            Set keys = userIdToZljd.keySet();
            Iterator it = keys.iterator();
            while (it.hasNext()) {
                JSONObject result = new JSONObject();
                String userId = (String) it.next();
                List<JSONObject> oneuser = userIdToZljd.get(userId);
                double num = 0;
                String userName = null;
                String userDept = null;
                for (JSONObject onedata : oneuser) {
                    userName = onedata.getString("userName");
                    userDept = onedata.getString("userDept");
                    if ("优秀".equals(onedata.getString("chuangXinPingFen"))) {
                        num += 3;
                    } else if ("良好".equals(onedata.getString("chuangXinPingFen"))) {
                        num += 2;
                    } else if ("合格".equals(onedata.getString("chuangXinPingFen"))) {
                        num += 1;
                    }
                }
                result.put("userId", userId);
                result.put("userName", userName);
                result.put("userDept", userDept);
                result.put("num", num);
                userToResult.add(result);
            }
            List<JSONObject> userToResultList = userToResult.stream().sorted((o1, o2) -> {
                if (o1.getInteger("num").compareTo(o2.getInteger("num")) >= 0) {
                    return -1;
                } else return 1;
            }).collect(Collectors.toList());
            int index = 0;
            int lastNum = 0;
            for (int i = 0; i < userToResultList.size(); i++) {
                JSONObject oneResult = userToResultList.get(i);
                int num = oneResult.getInteger("num");
                String userName = oneResult.getString("userName");
                String userDept = oneResult.getString("userDept");
                yAxisData.add(num);
                xAxisData.add(userName);
                lastNum = num;
                if (i == 9) {
                    break;
                }
            }
            for (int j = 10; j < userToResultList.size(); j++) {
                JSONObject nextResult = userToResultList.get(j);
                int nextnum = nextResult.getInteger("num");
                String nextuserName = nextResult.getString("userName");
                String nextuserDept = nextResult.getString("userDept");
                if (lastNum == nextnum) {
                    yAxisData.add(nextnum);
                    xAxisData.add(nextuserName);
                } else {
                    break;
                }
            }
        }

        // legend
        List<String> legendData = new ArrayList<>();
        JSONObject oneSerie = new JSONObject();
        oneSerie.put("name", "总分");
        oneSerie.put("type", "bar");
        oneSerie.put("barGap", "0");
        oneSerie.put("label", labelObject);
        oneSerie.put("data", yAxisData);
        legendData.add(oneSerie.getString("name"));
        seriesData.add(oneSerie);
        resultMap.put("legendData", legendData);
        resultMap.put("xAxisData", xAxisData);
        resultMap.put("series", seriesData);
        return JsonResultUtil.success(resultMap);
    }

    public JsonResult queryApplyNum(HttpServletRequest request) {
        String professionalCategory = RequestUtil.getString(request, "professionalCategory", "");
        Map<String, Object> resultMap = new HashMap<>();
        JSONObject labelObject = new JSONObject();
        labelObject.put("show", "true");
        labelObject.put("position", "top");
        // 时间段内各部门应完成的总数
        Map<String, Object> params = new HashMap<>();
        params.put("professionalCategory", professionalCategory);
        List<JSONObject> revisePlanDeptList = zljdOverviewDao.queryApplyNum(params);
        // x轴数据
        List<String> xAxisData = toGetYearList("2016-01-01", DateFormatUtil.getNowByString("yyyy-MM-dd"));
        resultMap.put("xAxisData", xAxisData);
        if (revisePlanDeptList == null || revisePlanDeptList.isEmpty()) {
            return new JsonResult();
        }
        List<JSONObject> lastData = new ArrayList<>();
        // 图表数据
        List<JSONObject> seriesData = new ArrayList<>();

        List<String> legendData = new ArrayList<>();
        for (JSONObject oneData : revisePlanDeptList) {
            JSONObject needData = new JSONObject();
            if(StringUtils.isNotBlank(oneData.getString("firstName"))){
                needData.put("applyYear",oneData.getString("applyYear"));
                needData.put("applyName",oneData.getString("secondName"));
                needData.put("applyId",oneData.getString("id"));
            }else if(StringUtils.isBlank(oneData.getString("firstName"))&&
                    StringUtils.isNotBlank(oneData.getString("secondName"))){
               needData.put("applyYear",oneData.getString("applyYear"));
               needData.put("applyName",oneData.getString("thirdName"));
               needData.put("applyId",oneData.getString("id"));
           }else if(StringUtils.isBlank(oneData.getString("secondName"))&&
                   StringUtils.isNotBlank(oneData.getString("thirdName"))){
               needData.put("applyYear",oneData.getString("applyYear"));
               needData.put("applyName",oneData.getString("forthName"));
               needData.put("applyId",oneData.getString("id"));
           }else if(StringUtils.isBlank(oneData.getString("thirdName"))&&
                   StringUtils.isNotBlank(oneData.getString("forthName"))){
               needData.put("applyYear",oneData.getString("applyYear"));
               needData.put("applyName",oneData.getString("fifthName"));
               needData.put("applyId",oneData.getString("id"));
           }
           lastData.add(needData);
        }
        Stream<JSONObject> stream = lastData.stream();
        Map<String, List<JSONObject>> nameToZljd =
                stream.collect(Collectors.groupingBy(j -> j.getString("applyName")));
        if (nameToZljd != null && !nameToZljd.isEmpty()) {
            Set keys = nameToZljd.keySet();
            Iterator it = keys.iterator();
            while (it.hasNext()) {
                JSONObject serie = new JSONObject();
                List<Integer> yAxisData = new ArrayList<>();
                String applyName = (String) it.next();
                legendData.add(applyName);
                List<JSONObject> oneDataList = nameToZljd.get(applyName);
                Stream<JSONObject> timeStream = oneDataList.stream();
                Map<String, List<JSONObject>> timeToZljd =
                        timeStream.collect(Collectors.groupingBy(j -> j.getString("applyYear")));
                for(String year:xAxisData){
                    if(timeToZljd.containsKey(year)){
                        List<JSONObject> oneTimeList = timeToZljd.get(year);
                        int count = oneTimeList.size();
                        yAxisData.add(count);
                    } else {
                        yAxisData.add(0);
                    }
                }
                serie.put("name", applyName);
                serie.put("type", "line");
                serie.put("label",new JSONObject() {
                    {
                        put("show", true);
                        put("position", "top");
                        put("color", "black");
                        put("formatter", "{c}");
                    }
                });
                serie.put("data", yAxisData);
                seriesData.add(serie);
            }
        }
        // legend
        resultMap.put("legendData", legendData);
        resultMap.put("xAxisData", xAxisData);
        resultMap.put("series", seriesData);
        return JsonResultUtil.success(resultMap);
    }

    public JsonPageResult<?> queryIPCNum(HttpServletRequest request, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    params.put(name, value);
                }
            }
        }
        List<JSONObject> dataList = zljdOverviewDao.queryIPCNum(params);
        List<JSONObject> typeList = zljdOverviewDao.queryIPCType(params);
        for (JSONObject type:typeList){
            int sanyNum=0;
            int catNum=0;
            int xcmgNum=0;
            String description = type.getString("description");
            String IPCMainNo = type.getString("IPCMainNo");
            for (JSONObject data:dataList){
                String datadescription = data.getString("description");
                String dataIPCMainNo = data.getString("IPCMainNo");
                String patentApplicant = data.getString("patentApplicant");
                int countNum = data.getInteger("countNum");
                if(datadescription.equals(description)&&dataIPCMainNo.equals(IPCMainNo)){
                    if("三一".equals(patentApplicant)){
                        sanyNum+=countNum;
                    }else if ("卡特".equals(patentApplicant)){
                        catNum+=countNum;;
                    }else if ("徐工".equals(patentApplicant)){
                        xcmgNum+=countNum;;
                    }
                }
            }
            type.put("sanyNum",sanyNum);
            type.put("catNum",catNum);
            type.put("xcmgNum",xcmgNum);
        }
        result.setData(typeList);
        int countDataList = zljdOverviewDao.countIPCNum(params);
        result.setTotal(countDataList);
        return result;
    }

    public JsonPageResult<?> queryNewApplyNum(HttpServletRequest request) {
        JsonPageResult result = new JsonPageResult(true);
        List<JSONObject> lastData = new ArrayList<>();

        Map<String, Object> params = new HashMap<>();
        List<String> yearData = toGetYearList("2016-01-01", DateFormatUtil.getNowByString("yyyy-MM-dd"));
        String professionalCategory = RequestUtil.getString(request,"professionalCategory","");
        params.put("professionalCategory", professionalCategory);
        List<JSONObject> dataList = zljdOverviewDao.queryNewApplyNum(params);
        Stream<JSONObject> streamName = dataList.stream();
        Stream<JSONObject> streamYear = dataList.stream();
        Map<String, List<JSONObject>> nameToData =
                streamName.collect(Collectors.groupingBy(j -> j.getString("technialName")));
        Map<String, List<JSONObject>> yearToData =
                streamYear.collect(Collectors.groupingBy(j -> j.getString("applyYear")));
        if (nameToData != null && !nameToData.isEmpty()) {
            Set keys = nameToData.keySet();
            Iterator it = keys.iterator();
            while (it.hasNext()) {
                int num =0;
                int sum = 0;
                JSONObject technialJson = new JSONObject();
                String technialName = (String) it.next();
                technialJson.put("technialName",technialName);
                List<JSONObject> nameToCount = nameToData.get(technialName);
                for(String year:yearData){
                    boolean isexist = false;
                    for (JSONObject oneData:nameToCount){
                        num = oneData.getInteger("num");
                        if(year.equals(oneData.getString("applyYear"))){
                            isexist = true;
                            break;
                        }
                    }
                    if(isexist){
                        technialJson.put(year,num);
                        sum =sum+num;
                    }else {
                        technialJson.put(year,0);
                    }
                }
                technialJson.put("sum",sum);
                lastData.add(technialJson);
            }
        }
        List<JSONObject> lastNumData = lastData.stream().sorted((o1, o2) -> {
            if (o1.getInteger("sum").compareTo(o2.getInteger("sum")) >= 0) {
                return -1;
            } else return 1;
        }).collect(Collectors.toList());
        result.setData(lastNumData);
        return result;
    }

    private List<String> toGetYearList(String start, String end) {
        List<String> result = new ArrayList<>();
        try {
            while (start.compareTo(end) <= 0) {
                result.add(start.substring(0, 4));
                start = DateFormatUtil.format(DateUtil.addYear(DateFormatUtil.parse(start, "yyyy-MM-dd"), 1), "yyyy-MM-dd");
            }
        } catch (Exception e) {
            logger.error("生成年份异常", e);
        }

        return result;
    }
}
