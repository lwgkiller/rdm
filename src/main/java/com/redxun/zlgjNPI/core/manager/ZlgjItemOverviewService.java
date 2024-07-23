package com.redxun.zlgjNPI.core.manager;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.entity.BpmInst;
import com.redxun.core.json.JsonResult;
import com.redxun.core.json.JsonResultUtil;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.zlgjNPI.core.dao.ZlgjItemOverviewDao;

/**
 * 质量改进项目总览-报表服务层
 *
 * @author douhongli
 * @date 2021年6月5日14:17:41
 */
@Service
public class ZlgjItemOverviewService {
    private static final Logger logger = LoggerFactory.getLogger(ZlgjItemOverviewService.class);

    @Resource
    private ZlgjItemOverviewDao zlgjItemOverviewDao;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;
    @Autowired
    private CommonInfoDao commonInfoDao;
    /**
     * 厂内改进图表
     *
     * @param queryChartDataVO
     *            参数
     * @return JsonResult
     * @author douhongli
     * @since 2021年6月7日09:03:59
     */
    public JsonResult queryChartData(String queryChartDataVO) {
        JSONObject paramObj = JSONObject.parseObject(queryChartDataVO);
        Map<String, Object> resultMap = new HashMap<>();
        // 将传入参数转化为开始月份和结束月份
        List<String> xAxisData = toGetYearMonthList(paramObj.getString("startTime"), paramObj.getString("endTime"));
        resultMap.put("xAxisData", xAxisData);
        // y轴数据
        List<Integer> yAxisDataEnd = new ArrayList<>();
        List<Integer> yAxisDataRunning = new ArrayList<>();
        // 总数
        List<Integer> sumData = new ArrayList<>();
        // 获取当前时间段内所有的数据
        Map<String, JSONObject> month2Data = new HashMap<>();
        List<JSONObject> queryDataList = zlgjItemOverviewDao.selectListByMonth(paramObj);
        if (queryDataList != null && !queryDataList.isEmpty()) {
            for (JSONObject oneData : queryDataList) {
                String createMonth = oneData.getString("createMonth");
                String zrbmshTime = oneData.getString("zrbmshTime");
                if (!month2Data.containsKey(createMonth)) {
                    month2Data.put(createMonth, new JSONObject());
                }
                int sumNum = month2Data.get(createMonth).getIntValue("sum");
                month2Data.get(createMonth).put("sum", sumNum + 1);
                if (StringUtils.isNotBlank(zrbmshTime)
                    || BpmInst.STATUS_DISCARD.equalsIgnoreCase(oneData.getString("STATUS_"))) {
                    int endNum = month2Data.get(createMonth).getIntValue("end");
                    month2Data.get(createMonth).put("end", endNum + 1);
                } else {
                    int runningNum = month2Data.get(createMonth).getIntValue("run");
                    month2Data.get(createMonth).put("run", runningNum + 1);
                }
            }
        }
        for (String yearMonth : xAxisData) {
            if (!month2Data.containsKey(yearMonth) || !month2Data.get(yearMonth).containsKey("end")) {
                yAxisDataEnd.add(0);
            } else {
                yAxisDataEnd.add(month2Data.get(yearMonth).getIntValue("end"));
            }
            if (!month2Data.containsKey(yearMonth) || !month2Data.get(yearMonth).containsKey("sum")) {
                sumData.add(0);
            } else {
                sumData.add(month2Data.get(yearMonth).getIntValue("sum"));
            }
            if (!month2Data.containsKey(yearMonth) || !month2Data.get(yearMonth).containsKey("run")) {
                yAxisDataRunning.add(0);
            } else {
                yAxisDataRunning.add(month2Data.get(yearMonth).getIntValue("run"));
            }
        }
        // 组合数据
        List<JSONObject> seriesData = new ArrayList<>();
        JSONObject oneSerie = new JSONObject();
        oneSerie.put("name", "已完成");
        oneSerie.put("type", "bar");
        oneSerie.put("stack", "数量");
        oneSerie.put("data", yAxisDataEnd);
        seriesData.add(oneSerie);
        JSONObject twoSerie = new JSONObject();
        twoSerie.put("name", "进行中");
        twoSerie.put("type", "bar");
        twoSerie.put("stack", "数量");
        twoSerie.put("data", yAxisDataRunning);
        seriesData.add(twoSerie);
        resultMap.put("series", seriesData);
        resultMap.put("sumData", sumData);
        return JsonResultUtil.success(resultMap);
    }

    /**
     * 按照责任部门查询图表数据
     */
    public JsonResult queryChartDataByDept(String zlgjStr) {
        JSONObject paramObj = JSONObject.parseObject(zlgjStr);
        if (paramObj.containsKey("startTime") && StringUtils.isNotBlank(paramObj.getString("startTime"))) {
            paramObj.put("startTime",
                DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(paramObj.getString("startTime")), -8)));
        }
        if (paramObj.containsKey("endTime") && StringUtils.isNotBlank(paramObj.getString("endTime"))) {
            paramObj.put("endTime",
                DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(paramObj.getString("endTime")), 16)));
        }
        String uIdStr = paramObj.getString("wtlx");
        if (StringUtils.isNotBlank(uIdStr)) {
            String[] wtlxs = uIdStr.split(",");
            List<String> wtlx = Arrays.asList(wtlxs);
            paramObj.put("wtlxs", wtlx);
        }
        String zrType = paramObj.getString("zrType");
        Map<String, Object> resultMap = new HashMap<>();
        // x数据
        List<String> xAxisData = new ArrayList<>();
        // 已完成
        List<Integer> yAxisDataEnd = new ArrayList<>();
        // 已完成不改进
        List<Integer> yAxisDataNogj = new ArrayList<>();
        // 已完成改进
        List<Integer> yAxisDataYesgj = new ArrayList<>();
        // 进行中
        List<Integer> yAxisDataRunning = new ArrayList<>();
        // 总数
        List<Integer> sumData = new ArrayList<>();

        // 获取当前时间内的所有的数据
        Map<String, JSONObject> deptName2Data = new LinkedHashMap<>();
        List<JSONObject> queryDataList = zlgjItemOverviewDao.selectByDate(paramObj);
        xcmgProjectManager.setTaskCurrentUserJSON(queryDataList);
        if (queryDataList != null && !queryDataList.isEmpty()) {
            // 2022-10-26：加入"总计"虚拟部门
            deptName2Data.put("总计", new JSONObject());
            // end

            List<String> zrrIds = new ArrayList<>();//责任人Id
            List<String> currentProcessUserIds = new ArrayList<>();//流程处理人Id

            for (JSONObject oneData : queryDataList) {
                String zrrId = oneData.getString("zrrId");
                if (StringUtils.isNotBlank(zrrId)) {
                    zrrIds.add(zrrId);
                }
                String processUserId = oneData.getString("currentProcessUserId");
                if (StringUtils.isNotBlank(processUserId)) {
                    String [] processUserIdStr = processUserId.split(",");
                    for (String oneId : processUserIdStr) {
                        currentProcessUserIds.add(oneId);
                    }
                }
            }
            Map<String, String> zrrId2DeptName = new HashMap<>();
            Map<String, String> processUserId2DeptName = new HashMap<>();
            if(!zrrIds.isEmpty()&&zrrIds!=null) {
                //所有的责任人部门信息
                Map<String, Object> zrrIdparams = new HashMap<>();
                zrrIdparams.put("userIds", zrrIds);
                List<JSONObject> zrrDeptInfos = commonInfoDao.getDeptInfoByUserIds(zrrIdparams);
                for (JSONObject zrrDeptInfo : zrrDeptInfos) {
                    zrrId2DeptName.put(zrrDeptInfo.getString("userId"), zrrDeptInfo.getString("deptName"));
                }
            }
            if(!currentProcessUserIds.isEmpty()&&currentProcessUserIds!=null) {
                //所有的流程处理人部门信息
                Map<String, Object> processUserIdparams = new HashMap<>();
                processUserIdparams.put("userIds",currentProcessUserIds);
                List<JSONObject> processUserDeptInfos = commonInfoDao.getDeptInfoByUserIds(processUserIdparams);
                for (JSONObject processUserDeptInfo : processUserDeptInfos) {
                    processUserId2DeptName.put(processUserDeptInfo.getString("userId"),processUserDeptInfo.getString("deptName"));
                }
            }

            for (JSONObject oneData : queryDataList) {
                //责任人部门
                oneData.put("zrrDeptName", zrrId2DeptName.get(oneData.getString("zrrId")));
                //流程处理人部门
                StringBuilder stringBuilder = new StringBuilder();
                String processUserId = oneData.getString("currentProcessUserId");
                if (StringUtils.isNotBlank(processUserId)) {
                    String [] processUserIdStr = processUserId.split(",");
                    for (String oneId : processUserIdStr) {
                        String currentProcessUserDeptName = processUserId2DeptName.get(oneId);
                        if(stringBuilder.indexOf(currentProcessUserDeptName)==-1){
                            stringBuilder.append(currentProcessUserDeptName).append(",");
                        }
                        if (stringBuilder.length() > 0) {
                            oneData.put("currentProcessUserDeptName", stringBuilder.substring(0, stringBuilder.length() - 1));
                        }
                    }
                }

                String ssbmName = null;
                if("第一责任人".equalsIgnoreCase(zrType)){
                    ssbmName = oneData.getString("ssbmName");
                } else if ("问题处理人".equalsIgnoreCase(zrType)&&StringUtils.isNotBlank(oneData.getString("zrrDeptName"))) {
                    ssbmName = oneData.getString("zrrDeptName");
                } else if ("流程处理人".equalsIgnoreCase(zrType)&&StringUtils.isNotBlank(oneData.getString("currentProcessUserDeptName"))) {
                    ssbmName = oneData.getString("currentProcessUserDeptName");
                }else {
                    continue;
                }
                String zrbmshTime = oneData.getString("zrbmshTime");
                String ifgj = oneData.getString("ifgj");
                if (!deptName2Data.containsKey(ssbmName)) {
                    deptName2Data.put(ssbmName, new JSONObject());
                }
                int sumNum = deptName2Data.get(ssbmName).getIntValue("sum");
                deptName2Data.get(ssbmName).put("sum", sumNum + 1);
                // 2022-10-26："总计"虚拟部门计数
                deptName2Data.get("总计").put("sum", deptName2Data.get("总计").getIntValue("sum") + 1);
                // end
                if (StringUtils.isNotBlank(zrbmshTime)
                    || BpmInst.STATUS_DISCARD.equalsIgnoreCase(oneData.getString("STATUS_"))) {
                    int endNum = deptName2Data.get(ssbmName).getIntValue("end");
                    deptName2Data.get(ssbmName).put("end", endNum + 1);
                    // 2022-10-26："总计"虚拟部门计数
                    deptName2Data.get("总计").put("end", deptName2Data.get("总计").getIntValue("end") + 1);
                    // end
                    if ("no".equals(ifgj)) {
                        int nogjNum = deptName2Data.get(ssbmName).getIntValue("nogj");
                        deptName2Data.get(ssbmName).put("nogj", nogjNum + 1);
                        // 2022-10-26："总计"虚拟部门计数
                        deptName2Data.get("总计").put("nogj", deptName2Data.get("总计").getIntValue("nogj") + 1);
                        // end
                    }
                    if ("yes".equals(ifgj)) {
                        int yesgjNum = deptName2Data.get(ssbmName).getIntValue("yesgj");
                        deptName2Data.get(ssbmName).put("yesgj", yesgjNum + 1);
                        // 2022-10-26："总计"虚拟部门计数
                        deptName2Data.get("总计").put("yesgj", deptName2Data.get("总计").getIntValue("yesgj") + 1);
                        // end
                    }
                } else {
                    int runningNum = deptName2Data.get(ssbmName).getIntValue("run");
                    deptName2Data.get(ssbmName).put("run", runningNum + 1);
                    // 2022-10-26："总计"虚拟部门计数
                    deptName2Data.get("总计").put("run", deptName2Data.get("总计").getIntValue("run") + 1);
                    // end
                }
            }
        }
        for (Map.Entry<String, JSONObject> oneEntry : deptName2Data.entrySet()) {
            xAxisData.add(oneEntry.getKey());
            JSONObject oneDeptNum = oneEntry.getValue();
            if (!oneDeptNum.containsKey("end")) {
                yAxisDataEnd.add(0);
            } else {
                yAxisDataEnd.add(oneDeptNum.getIntValue("end"));
            }
            if (!oneDeptNum.containsKey("sum")) {
                sumData.add(0);
            } else {
                sumData.add(oneDeptNum.getIntValue("sum"));
            }
            if (!oneDeptNum.containsKey("run")) {
                yAxisDataRunning.add(0);
            } else {
                yAxisDataRunning.add(oneDeptNum.getIntValue("run"));
            }
            if (!oneDeptNum.containsKey("nogj")) {
                yAxisDataNogj.add(0);
            } else {
                yAxisDataNogj.add(oneDeptNum.getIntValue("nogj"));
            }
            if (!oneDeptNum.containsKey("yesgj")) {
                yAxisDataYesgj.add(0);
            } else {
                yAxisDataYesgj.add(oneDeptNum.getIntValue("yesgj"));
            }
        }

        // 组合数据
        List<JSONObject> seriesData = new ArrayList<>();
        JSONObject twoSerie = new JSONObject();
        twoSerie.put("name", "已完成(无需改进)");
        twoSerie.put("type", "bar");
        twoSerie.put("stack", "数量");
        twoSerie.put("data", yAxisDataNogj);
        seriesData.add(twoSerie);
        JSONObject threeSerie = new JSONObject();
        threeSerie.put("name", "已完成(改进)");
        threeSerie.put("type", "bar");
        threeSerie.put("stack", "数量");
        threeSerie.put("data", yAxisDataYesgj);
        seriesData.add(threeSerie);
        JSONObject oneSerie = new JSONObject();
        oneSerie.put("name", "进行中");
        oneSerie.put("type", "bar");
        oneSerie.put("stack", "数量");
        oneSerie.put("data", yAxisDataRunning);
        seriesData.add(oneSerie);
        // 返回装填
        resultMap.put("xAxisData", xAxisData);
        resultMap.put("series", seriesData);
        resultMap.put("sumData", sumData);
        return JsonResultUtil.success(resultMap);
    }

    /**
     * 查询完成率通过问题类别
     *
     * @param zlgjStr
     *            参数
     * @return JsonResult
     * @author douhongli
     * @since 2021年6月8日15:33:20
     */
    public JsonResult queryGaugePercentage(String zlgjStr) {
        JSONObject paramObj = JSONObject.parseObject(zlgjStr);
        if (paramObj.containsKey("startTime") && StringUtils.isNotBlank(paramObj.getString("startTime"))) {
            paramObj.put("startTime",
                DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(paramObj.getString("startTime")), -8)));
        }
        if (paramObj.containsKey("endTime") && StringUtils.isNotBlank(paramObj.getString("endTime"))) {
            paramObj.put("endTime",
                DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(paramObj.getString("endTime")), 16)));
        }
        List<JSONObject> zlgjItemOverviewList = zlgjItemOverviewDao.selectByWtlxs(paramObj);
        long endCount = zlgjItemOverviewList.stream()
            .filter(zlgjItemOverviewDTO -> (StringUtils.isNotBlank(zlgjItemOverviewDTO.getString("zrbmshTime"))
                || BpmInst.STATUS_DISCARD.equalsIgnoreCase(zlgjItemOverviewDTO.getString("STATUS_"))))
            .count();
        if (zlgjItemOverviewList.size() > 0) {
            BigDecimal total = new BigDecimal(zlgjItemOverviewList.size());
            BigDecimal endCountDecimal = new BigDecimal(endCount);
            BigDecimal result = endCountDecimal.divide(total, 4, BigDecimal.ROUND_HALF_UP);
            return JsonResultUtil.success(result.multiply(new BigDecimal(100)));
        } else {
            return JsonResultUtil.success(0);
        }
    }

    public JsonResult queryZrrTime(HttpServletRequest request) {
        DecimalFormat df = new DecimalFormat("0.00");
        String nowTime = DateFormatUtil.getNowByString("yyyy-MM-dd HH:mm:ss");
        String startTime = RequestUtil.getString(request, "startTime", "");
        String czxpj = RequestUtil.getString(request, "czxpj", "");
        String wtlxCq = RequestUtil.getString(request, "wtlxCq", "");
        Map<String, Object> resultMap = new HashMap<>();
        Map<String, Object> params = new HashMap<>();
        params.put("startYearMonth", startTime);
        params.put("czxpj", czxpj);
        if (StringUtils.isNotBlank(wtlxCq)) {
            String[] wtlxs = wtlxCq.split(",");
            List<String> wtlx = Arrays.asList(wtlxs);
            params.put("wtlxs", wtlx);
        }
        List<JSONObject> revisePlanDeptList = zlgjItemOverviewDao.queryZrrTime(params);
        // 声明一个列表用来存储超期的问题
        List<JSONObject> cqdata = new ArrayList<>();
        if (revisePlanDeptList == null || revisePlanDeptList.isEmpty()) {
            return new JsonResult();
        }
        // Map<String, JSONObject> deptId2Obj = new LinkedHashMap<>();
        // 对所有问题是否超期进行加工，同时初始化超期问题列表
        for (JSONObject jsonObject : revisePlanDeptList) {
            long days = ((DateUtil.parseDate(nowTime).getTime()
                - DateUtil.parseDate(jsonObject.getString("zrrStart")).getTime())) / (1000 * 3600 * 24);
            jsonObject.put("cost", String.valueOf(days));
            if (("一般".equals(jsonObject.getString("jjcd")) && jsonObject.getInteger("cost") > 10)
                || ("紧急".equals(jsonObject.getString("jjcd")) && jsonObject.getInteger("cost") > 5)
                || ("特急".equals(jsonObject.getString("jjcd")) && jsonObject.getInteger("cost") > 3)) {
                jsonObject.put("cq", "yes");
                cqdata.add(jsonObject);
            } else {
                jsonObject.put("cq", "no");
            }
        }
        // 图表数据
        List<JSONObject> seriesData = new ArrayList<>();
        // x轴数据
        List<String> xAxisData = new ArrayList<>();

        // A类问题
        List<Integer> finishAData = new ArrayList<>();
        // B类问题
        List<Integer> finishBData = new ArrayList<>();
        // C类问题
        List<Integer> finishCData = new ArrayList<>();
        // W类问题
        List<Integer> finishWData = new ArrayList<>();
        // 总数
        List<Integer> finishData = new ArrayList<>();

        // 2022-10-26：加入"总计"虚拟部门，并分别记初始值
        xAxisData.add("总计");
        finishAData.add(0);
        finishBData.add(0);
        finishCData.add(0);
        finishWData.add(0);
        finishData.add(0);
        // end

        Stream<JSONObject> stream = cqdata.stream();
        Map<String, List<JSONObject>> collect = stream.collect(Collectors.groupingBy(j -> j.getString("ssbmName")));
        collect.forEach((s, data) -> {
            xAxisData.add(s);
            long countA = data.stream().filter(jsonObject -> jsonObject.getString("yzcd").equals("A")).count();
            finishAData.add(new Long(countA).intValue());
            int i = finishAData.get(0);// 总计的值A
            i += new Long(countA).intValue();// 总计的值A
            finishAData.remove(0);// 总计的值A
            finishAData.add(0, i);// 总计的值A
            long countB = data.stream().filter(jsonObject -> jsonObject.getString("yzcd").equals("B")).count();
            finishBData.add(new Long(countB).intValue());
            i = finishBData.get(0);// 总计的值B
            i += new Long(countB).intValue();// 总计的值B
            finishBData.remove(0);// 总计的值B
            finishBData.add(0, i);// 总计的值B
            long countC = data.stream().filter(jsonObject -> jsonObject.getString("yzcd").equals("C")).count();
            finishCData.add(new Long(countC).intValue());
            i = finishCData.get(0);// 总计的值C
            i += new Long(countC).intValue();// 总计的值C
            finishCData.remove(0);// 总计的值C
            finishCData.add(0, i);// 总计的值C
            long countW = data.stream().filter(jsonObject -> jsonObject.getString("yzcd").equals("W")).count();
            finishWData.add(new Long(countW).intValue());
            i = finishWData.get(0);// 总计的值W
            i += new Long(countW).intValue();// 总计的值W
            finishWData.remove(0);// 总计的值W
            finishWData.add(0, i);// 总计的值W
            long count = data.stream().filter(jsonObject -> jsonObject.getString("cq").equals("yes")).count();
            finishData.add(new Long(count).intValue());
            i = finishData.get(0);// 总计的值
            i += new Long(count).intValue();// 总计的值
            finishData.remove(0);// 总计的值
            finishData.add(0, i);// 总计的值
        });
        // legend
        List<String> legendData = new ArrayList<>();
        JSONObject fourSerie = new JSONObject();
        fourSerie.put("name", "W类问题超期数量");
        fourSerie.put("type", "bar");
        fourSerie.put("stack", "数量");
        fourSerie.put("data", finishWData);
        seriesData.add(fourSerie);
        JSONObject threeSerie = new JSONObject();
        threeSerie.put("name", "C类问题超期数量");
        threeSerie.put("type", "bar");
        threeSerie.put("stack", "数量");
        threeSerie.put("data", finishCData);
        seriesData.add(threeSerie);
        JSONObject twoSerie = new JSONObject();
        twoSerie.put("name", "B类问题超期数量");
        twoSerie.put("type", "bar");
        twoSerie.put("stack", "数量");
        twoSerie.put("data", finishBData);
        seriesData.add(twoSerie);
        JSONObject oneSerie = new JSONObject();
        oneSerie.put("name", "A类问题超期数量");
        oneSerie.put("type", "bar");
        oneSerie.put("stack", "数量");
        oneSerie.put("data", finishAData);
        legendData.add(oneSerie.getString("name"));
        legendData.add(twoSerie.getString("name"));
        legendData.add(threeSerie.getString("name"));
        legendData.add(fourSerie.getString("name"));
        seriesData.add(oneSerie);
        resultMap.put("legendData", legendData);
        resultMap.put("xAxisData", xAxisData);
        resultMap.put("series", seriesData);
        resultMap.put("finishData", finishData);
        return JsonResultUtil.success(resultMap);
    }

    // 根据开始、结束年月生成列表
    private List<String> toGetYearMonthList(String start, String end) {
        List<String> result = new ArrayList<>();
        try {
            while (start.compareTo(end) <= 0) {
                result.add(start);
                start = DateFormatUtil.format(DateUtil.addMonth(DateFormatUtil.parse(start, "yyyy-MM"), 1), "yyyy-MM");
            }
        } catch (Exception e) {
            logger.error("生成月份异常", e);
        }

        return result;
    }

    /**
     * 按照责任部门查询ABC类数据
     */
    public JsonResult queryTypeDataByDept(String zlgjStr) {
        JSONObject paramObj = JSONObject.parseObject(zlgjStr);
        if (paramObj.containsKey("startTime") && StringUtils.isNotBlank(paramObj.getString("startTime"))) {
            paramObj.put("startTime",
                DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(paramObj.getString("startTime")), -8)));
        }
        if (paramObj.containsKey("endTime") && StringUtils.isNotBlank(paramObj.getString("endTime"))) {
            paramObj.put("endTime",
                DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(paramObj.getString("endTime")), 16)));
        }
        String yzcdString = paramObj.getString("yzcd");
        if (StringUtils.isNotBlank(yzcdString)) {
            String[] yzcds = yzcdString.split(",");
            List<String> yzcdList = Arrays.asList(yzcds);
            paramObj.put("yzcds", yzcdList);
        } else {
            yzcdString = "A,B,C,W";
        }
        String wtlxString = paramObj.getString("wtlx");
        if (StringUtils.isNotBlank(wtlxString)) {
            paramObj.put("wtlxs", Arrays.asList(wtlxString.split(",",-1)));
        }

        Map<String, Object> resultMap = new HashMap<>();
        // x数据
        List<String> xAxisData = new ArrayList<>();
        // 总数
        List<Integer> sumData = new ArrayList<>();
        // A完成
        List<Integer> yAxisDataAend = new ArrayList<>();
        // B完成
        List<Integer> yAxisDataBend = new ArrayList<>();
        // C完成
        List<Integer> yAxisDataCend = new ArrayList<>();
        // A进行
        List<Integer> yAxisDataArun = new ArrayList<>();
        // B进行
        List<Integer> yAxisDataBrun = new ArrayList<>();
        // C进行
        List<Integer> yAxisDataCrun = new ArrayList<>();
        // A总数
        List<Integer> yAxisDataAsum = new ArrayList<>();
        // B总数
        List<Integer> yAxisDataBsum = new ArrayList<>();
        // C总数
        List<Integer> yAxisDataCsum = new ArrayList<>();

        // 获取当前时间内的所有的数据
        Map<String, JSONObject> deptName2Data = new HashMap<>();
        List<JSONObject> queryDataList = zlgjItemOverviewDao.selectByDateType(paramObj);
        if (queryDataList != null && !queryDataList.isEmpty()) {
            for (JSONObject oneData : queryDataList) {
                String ssbmName = oneData.getString("ssbmName");
                String zrbmshTime = oneData.getString("zrbmshTime");
                String yzcd = oneData.getString("yzcd");
                if (!deptName2Data.containsKey(ssbmName)) {
                    deptName2Data.put(ssbmName, new JSONObject());
                }
                int sumNum = deptName2Data.get(ssbmName).getIntValue("sum");
                deptName2Data.get(ssbmName).put("sum", sumNum + 1);
                if ("A".equals(yzcd)) {
                    int sumNumA = deptName2Data.get(ssbmName).getIntValue("sumA");
                    deptName2Data.get(ssbmName).put("sumA", sumNumA + 1);
                    if (StringUtils.isNotBlank(zrbmshTime)
                        || BpmInst.STATUS_DISCARD.equalsIgnoreCase(oneData.getString("STATUS_"))) {
                        int endNumA = deptName2Data.get(ssbmName).getIntValue("endA");
                        deptName2Data.get(ssbmName).put("endA", endNumA + 1);
                    } else {
                        int runningNumA = deptName2Data.get(ssbmName).getIntValue("runA");
                        deptName2Data.get(ssbmName).put("runA", runningNumA + 1);
                    }
                } else if ("B".equals(yzcd)) {
                    int sumNumB = deptName2Data.get(ssbmName).getIntValue("sumB");
                    deptName2Data.get(ssbmName).put("sumB", sumNumB + 1);
                    if (StringUtils.isNotBlank(zrbmshTime)
                        || BpmInst.STATUS_DISCARD.equalsIgnoreCase(oneData.getString("STATUS_"))) {
                        int endNumB = deptName2Data.get(ssbmName).getIntValue("endB");
                        deptName2Data.get(ssbmName).put("endB", endNumB + 1);
                    } else {
                        int runningNumB = deptName2Data.get(ssbmName).getIntValue("runB");
                        deptName2Data.get(ssbmName).put("runB", runningNumB + 1);
                    }
                } else if ("C".equals(yzcd)) {
                    int sumNumC = deptName2Data.get(ssbmName).getIntValue("sumC");
                    deptName2Data.get(ssbmName).put("sumC", sumNumC + 1);
                    if (StringUtils.isNotBlank(zrbmshTime)
                        || BpmInst.STATUS_DISCARD.equalsIgnoreCase(oneData.getString("STATUS_"))) {
                        int endNumC = deptName2Data.get(ssbmName).getIntValue("endC");
                        deptName2Data.get(ssbmName).put("endC", endNumC + 1);
                    } else {
                        int runningNumC = deptName2Data.get(ssbmName).getIntValue("runC");
                        deptName2Data.get(ssbmName).put("runC", runningNumC + 1);
                    }
                }
            }
        }
        for (Map.Entry<String, JSONObject> oneEntry : deptName2Data.entrySet()) {
            xAxisData.add(oneEntry.getKey());
            JSONObject oneDeptNum = oneEntry.getValue();
            if (!oneDeptNum.containsKey("sum")) {
                sumData.add(0);
            } else {
                sumData.add(oneDeptNum.getIntValue("sum"));
            }
            // A
            if (!oneDeptNum.containsKey("endA")) {
                yAxisDataAend.add(0);
            } else {
                yAxisDataAend.add(oneDeptNum.getIntValue("endA"));
            }
            if (!oneDeptNum.containsKey("sumA")) {
                yAxisDataAsum.add(0);
            } else {
                yAxisDataAsum.add(oneDeptNum.getIntValue("sumA"));
            }
            if (!oneDeptNum.containsKey("runA")) {
                yAxisDataArun.add(0);
            } else {
                yAxisDataArun.add(oneDeptNum.getIntValue("runA"));
            }
            // B
            if (!oneDeptNum.containsKey("endB")) {
                yAxisDataBend.add(0);
            } else {
                yAxisDataBend.add(oneDeptNum.getIntValue("endB"));
            }
            if (!oneDeptNum.containsKey("sumB")) {
                yAxisDataBsum.add(0);
            } else {
                yAxisDataBsum.add(oneDeptNum.getIntValue("sumB"));
            }
            if (!oneDeptNum.containsKey("runB")) {
                yAxisDataBrun.add(0);
            } else {
                yAxisDataBrun.add(oneDeptNum.getIntValue("runB"));
            }
            // C
            if (!oneDeptNum.containsKey("endC")) {
                yAxisDataCend.add(0);
            } else {
                yAxisDataCend.add(oneDeptNum.getIntValue("endC"));
            }
            if (!oneDeptNum.containsKey("sumC")) {
                yAxisDataCsum.add(0);
            } else {
                yAxisDataCsum.add(oneDeptNum.getIntValue("sumC"));
            }
            if (!oneDeptNum.containsKey("runC")) {
                yAxisDataCrun.add(0);
            } else {
                yAxisDataCrun.add(oneDeptNum.getIntValue("runC"));
            }
        }

        // 组合数据
        List<JSONObject> seriesData = new ArrayList<>();
        // 鼠标悬停显示标签内容
        List<String> legendData = new ArrayList<>();
        // 柱形图显示顶部数据
        JSONObject labelObjectsum = new JSONObject();
        labelObjectsum.put("show", "true");
        labelObjectsum.put("position", "top");
        // A
        if (yzcdString.contains("A")) {
            JSONObject endASerie = new JSONObject();
            endASerie.put("name", "已完成(A类)");
            endASerie.put("type", "bar");
            endASerie.put("stack", "A数量");
            endASerie.put("data", yAxisDataAend);
            seriesData.add(endASerie);
            JSONObject runASerie = new JSONObject();
            runASerie.put("name", "进行中(A类)");
            runASerie.put("type", "bar");
            runASerie.put("barGap", "0");
            runASerie.put("stack", "A数量");
            runASerie.put("data", yAxisDataArun);
            seriesData.add(runASerie);
            legendData.add(endASerie.getString("name"));
            legendData.add(runASerie.getString("name"));
            resultMap.put("sumDataA", yAxisDataAsum);
        }

        // B
        if (yzcdString.contains("B")) {
            JSONObject endBSerie = new JSONObject();
            endBSerie.put("name", "已完成(B类)");
            endBSerie.put("type", "bar");
            endBSerie.put("stack", "B数量");
            endBSerie.put("data", yAxisDataBend);
            seriesData.add(endBSerie);
            JSONObject runBSerie = new JSONObject();
            runBSerie.put("name", "进行中(B类)");
            runBSerie.put("type", "bar");
            runBSerie.put("barGap", "0");
            runBSerie.put("stack", "B数量");
            runBSerie.put("data", yAxisDataBrun);
            seriesData.add(runBSerie);
            legendData.add(endBSerie.getString("name"));
            legendData.add(runBSerie.getString("name"));
            resultMap.put("sumDataB", yAxisDataBsum);
        }

        // C
        if (yzcdString.contains("C")) {
            JSONObject endCSerie = new JSONObject();
            endCSerie.put("name", "已完成(C类)");
            endCSerie.put("type", "bar");
            endCSerie.put("stack", "C数量");
            endCSerie.put("data", yAxisDataCend);
            seriesData.add(endCSerie);
            JSONObject runCSerie = new JSONObject();
            runCSerie.put("name", "进行中(C类)");
            runCSerie.put("type", "bar");
            runCSerie.put("barGap", "0");
            runCSerie.put("stack", "C数量");
            runCSerie.put("data", yAxisDataCrun);
            seriesData.add(runCSerie);
            legendData.add(endCSerie.getString("name"));
            legendData.add(runCSerie.getString("name"));
            resultMap.put("sumDataC", yAxisDataCsum);
        }
        // 返回装填
        resultMap.put("xAxisData", xAxisData);
        resultMap.put("series", seriesData);
        resultMap.put("legendData", legendData);
        return JsonResultUtil.success(resultMap);
    }

    // 各严重程度问题各月完成情况
    public JsonResult queryDataByMonth(String queryChartDataVO) {
        JSONObject paramObj = JSONObject.parseObject(queryChartDataVO);
        Map<String, Object> resultMap = new HashMap<>();
        // 将传入参数转化为开始月份和结束月份
        List<String> xAxisData = toGetYearMonthList(paramObj.getString("startTime"), paramObj.getString("endTime"));
        resultMap.put("xAxisData", xAxisData);

        // y轴数据
        // A完成
        List<Integer> yAxisDataAend = new ArrayList<>();
        // B完成
        List<Integer> yAxisDataBend = new ArrayList<>();
        // C完成
        List<Integer> yAxisDataCend = new ArrayList<>();
        // A进行
        List<Integer> yAxisDataArun = new ArrayList<>();
        // B进行
        List<Integer> yAxisDataBrun = new ArrayList<>();
        // C进行
        List<Integer> yAxisDataCrun = new ArrayList<>();
        // A总数
        List<Integer> yAxisDataAsum = new ArrayList<>();
        // B总数
        List<Integer> yAxisDataBsum = new ArrayList<>();
        // C总数
        List<Integer> yAxisDataCsum = new ArrayList<>();
        // 获取当前时间段内所有的数据
        Map<String, JSONObject> month2Data = new HashMap<>();
        String yzcdString = paramObj.getString("yzcd");
        if (StringUtils.isNotBlank(yzcdString)) {
            String[] yzcds = yzcdString.split(",");
            List<String> yzcdList = Arrays.asList(yzcds);
            paramObj.put("yzcds", yzcdList);
        } else {
            yzcdString = "A,B,C,W";
        }
        String wtlxString = paramObj.getString("wtlx");
        if (StringUtils.isNotBlank(wtlxString)) {
            paramObj.put("wtlxs", Arrays.asList(wtlxString.split(",",-1)));
        }
        List<JSONObject> queryDataList = zlgjItemOverviewDao.selectDataByMonth(paramObj);

        for (JSONObject oneData : queryDataList) {
            String createMonth = oneData.getString("createMonth");
            String zrbmshTime = oneData.getString("zrbmshTime");
            String yzcd = oneData.getString("yzcd");
            if (!month2Data.containsKey(createMonth)) {
                month2Data.put(createMonth, new JSONObject());
            }
            int sumNum = month2Data.get(createMonth).getIntValue("sum");
            month2Data.get(createMonth).put("sum", sumNum + 1);
            if ("A".equals(yzcd)) {
                int sumNumA = month2Data.get(createMonth).getIntValue("sumA");
                month2Data.get(createMonth).put("sumA", sumNumA + 1);
                if (StringUtils.isNotBlank(zrbmshTime)
                    || BpmInst.STATUS_DISCARD.equalsIgnoreCase(oneData.getString("STATUS_"))) {
                    int endNumA = month2Data.get(createMonth).getIntValue("endA");
                    month2Data.get(createMonth).put("endA", endNumA + 1);
                } else {
                    int runningNumA = month2Data.get(createMonth).getIntValue("runA");
                    month2Data.get(createMonth).put("runA", runningNumA + 1);
                }
            } else if ("B".equals(yzcd)) {
                int sumNumB = month2Data.get(createMonth).getIntValue("sumB");
                month2Data.get(createMonth).put("sumB", sumNumB + 1);
                if (StringUtils.isNotBlank(zrbmshTime)
                    || BpmInst.STATUS_DISCARD.equalsIgnoreCase(oneData.getString("STATUS_"))) {
                    int endNumB = month2Data.get(createMonth).getIntValue("endB");
                    month2Data.get(createMonth).put("endB", endNumB + 1);
                } else {
                    int runningNumB = month2Data.get(createMonth).getIntValue("runB");
                    month2Data.get(createMonth).put("runB", runningNumB + 1);
                }
            } else if ("C".equals(yzcd)) {
                int sumNumC = month2Data.get(createMonth).getIntValue("sumC");
                month2Data.get(createMonth).put("sumC", sumNumC + 1);
                if (StringUtils.isNotBlank(zrbmshTime)
                    || BpmInst.STATUS_DISCARD.equalsIgnoreCase(oneData.getString("STATUS_"))) {
                    int endNumC = month2Data.get(createMonth).getIntValue("endC");
                    month2Data.get(createMonth).put("endC", endNumC + 1);
                } else {
                    int runningNumC = month2Data.get(createMonth).getIntValue("runC");
                    month2Data.get(createMonth).put("runC", runningNumC + 1);
                }
            }
        }
        for (String yearMonth : xAxisData) {
            // A
            if (!month2Data.containsKey(yearMonth) || !month2Data.get(yearMonth).containsKey("endA")) {
                yAxisDataAend.add(0);
            } else {
                yAxisDataAend.add(month2Data.get(yearMonth).getIntValue("endA"));
            }
            if (!month2Data.containsKey(yearMonth) || !month2Data.get(yearMonth).containsKey("sumA")) {
                yAxisDataAsum.add(0);
            } else {
                yAxisDataAsum.add(month2Data.get(yearMonth).getIntValue("sumA"));
            }
            if (!month2Data.containsKey(yearMonth) || !month2Data.get(yearMonth).containsKey("runA")) {
                yAxisDataArun.add(0);
            } else {
                yAxisDataArun.add(month2Data.get(yearMonth).getIntValue("runA"));
            }
            // B
            if (!month2Data.containsKey(yearMonth) || !month2Data.get(yearMonth).containsKey("endB")) {
                yAxisDataBend.add(0);
            } else {
                yAxisDataBend.add(month2Data.get(yearMonth).getIntValue("endB"));
            }
            if (!month2Data.containsKey(yearMonth) || !month2Data.get(yearMonth).containsKey("sumB")) {
                yAxisDataBsum.add(0);
            } else {
                yAxisDataBsum.add(month2Data.get(yearMonth).getIntValue("sumB"));
            }
            if (!month2Data.containsKey(yearMonth) || !month2Data.get(yearMonth).containsKey("runB")) {
                yAxisDataBrun.add(0);
            } else {
                yAxisDataBrun.add(month2Data.get(yearMonth).getIntValue("runB"));
            }
            // C
            if (!month2Data.containsKey(yearMonth) || !month2Data.get(yearMonth).containsKey("endC")) {
                yAxisDataCend.add(0);
            } else {
                yAxisDataCend.add(month2Data.get(yearMonth).getIntValue("endC"));
            }
            if (!month2Data.containsKey(yearMonth) || !month2Data.get(yearMonth).containsKey("sumC")) {
                yAxisDataCsum.add(0);
            } else {
                yAxisDataCsum.add(month2Data.get(yearMonth).getIntValue("sumC"));
            }
            if (!month2Data.containsKey(yearMonth) || !month2Data.get(yearMonth).containsKey("runC")) {
                yAxisDataCrun.add(0);
            } else {
                yAxisDataCrun.add(month2Data.get(yearMonth).getIntValue("runC"));
            }
        }
        // 组合数据
        List<JSONObject> seriesData = new ArrayList<>();
        // 鼠标悬停显示标签内容
        List<String> legendData = new ArrayList<>();
        // 柱形图显示顶部数据
        JSONObject labelObjectsum = new JSONObject();
        labelObjectsum.put("show", "true");
        labelObjectsum.put("position", "top");
        // A
        if (yzcdString.contains("A")) {
            JSONObject endASerie = new JSONObject();
            endASerie.put("name", "已完成(A类)");
            endASerie.put("type", "bar");
            endASerie.put("stack", "A数量");
            endASerie.put("data", yAxisDataAend);
            seriesData.add(endASerie);
            JSONObject runASerie = new JSONObject();
            runASerie.put("name", "进行中(A类)");
            runASerie.put("type", "bar");
            runASerie.put("barGap", "0");
            runASerie.put("stack", "A数量");
            runASerie.put("data", yAxisDataArun);
            seriesData.add(runASerie);
            legendData.add(endASerie.getString("name"));
            legendData.add(runASerie.getString("name"));
            resultMap.put("sumDataA", yAxisDataAsum);
        }

        // B
        if (yzcdString.contains("B")) {
            JSONObject endBSerie = new JSONObject();
            endBSerie.put("name", "已完成(B类)");
            endBSerie.put("type", "bar");
            endBSerie.put("stack", "B数量");
            endBSerie.put("data", yAxisDataBend);
            seriesData.add(endBSerie);
            JSONObject runBSerie = new JSONObject();
            runBSerie.put("name", "进行中(B类)");
            runBSerie.put("type", "bar");
            runBSerie.put("barGap", "0");
            runBSerie.put("stack", "B数量");
            runBSerie.put("data", yAxisDataBrun);
            seriesData.add(runBSerie);
            legendData.add(endBSerie.getString("name"));
            legendData.add(runBSerie.getString("name"));
            resultMap.put("sumDataB", yAxisDataBsum);
        }

        // C
        if (yzcdString.contains("C")) {
            JSONObject endCSerie = new JSONObject();
            endCSerie.put("name", "已完成(C类)");
            endCSerie.put("type", "bar");
            endCSerie.put("stack", "C数量");
            endCSerie.put("data", yAxisDataCend);
            seriesData.add(endCSerie);
            JSONObject runCSerie = new JSONObject();
            runCSerie.put("name", "进行中(C类)");
            runCSerie.put("type", "bar");
            runCSerie.put("barGap", "0");
            runCSerie.put("stack", "C数量");
            runCSerie.put("data", yAxisDataCrun);
            seriesData.add(runCSerie);
            legendData.add(endCSerie.getString("name"));
            legendData.add(runCSerie.getString("name"));
            resultMap.put("sumDataC", yAxisDataCsum);
        }
        resultMap.put("series", seriesData);
        resultMap.put("legendData", legendData);
        return JsonResultUtil.success(resultMap);
    }

    // 2022-10-27新增
    public JsonResult queryPerDayChart(String queryChartDataVO) {
        JSONObject paramObj = JSONObject.parseObject(queryChartDataVO);
        Map<String, Object> resultMap = new HashMap<>();
        // 将传入参数转化为开始月份和结束月份
        List<String> xAxisData = toGetYearMonthDayList(paramObj.getString("startTime"), paramObj.getString("endTime"));
        resultMap.put("xAxisData", xAxisData);
        // y轴数据
        List<Integer> yAxisDataEnd = new ArrayList<>();
        List<Double> yAxisDataEndPercent = new ArrayList<>();
        List<Integer> yAxisDataRun = new ArrayList<>();
        // 总数
        List<Integer> sumDataList = new ArrayList<>();
        // 获取当前时间段内所有的数据
        Map<String, JSONObject> day2Data = new HashMap<>();
        List<JSONObject> queryDataList = zlgjItemOverviewDao.selectListByDay(paramObj);
        if (queryDataList != null && !queryDataList.isEmpty()) {
            for (JSONObject oneData : queryDataList) {
                String CREATE_TIME_ = oneData.getString("CREATE_TIME_").substring(0, 10);
                String zrbmshTime = oneData.getString("zrbmshTime");
                if (!day2Data.containsKey(CREATE_TIME_)) {
                    day2Data.put(CREATE_TIME_, new JSONObject());
                }
                int sumNum = day2Data.get(CREATE_TIME_).getIntValue("sum");
                day2Data.get(CREATE_TIME_).put("sum", sumNum + 1);
                if (StringUtils.isNotBlank(zrbmshTime)
                    || BpmInst.STATUS_DISCARD.equalsIgnoreCase(oneData.getString("STATUS_"))) {
                    int endNum = day2Data.get(CREATE_TIME_).getIntValue("end");
                    day2Data.get(CREATE_TIME_).put("end", endNum + 1);
                }
            }
        }
        for (String yearMonthDay : xAxisData) {
            int endData = 0;
            if (!day2Data.containsKey(yearMonthDay) || !day2Data.get(yearMonthDay).containsKey("end")) {
                endData = 0;
            } else {
                endData = day2Data.get(yearMonthDay).getIntValue("end");
            }
            yAxisDataEnd.add(endData);
            int sumData = 0;
            if (!day2Data.containsKey(yearMonthDay) || !day2Data.get(yearMonthDay).containsKey("sum")) {
                sumData = 0;
            } else {
                sumData = day2Data.get(yearMonthDay).getIntValue("sum");
            }
            sumDataList.add(sumData);
            // 计算进行中
            int runData = sumData - endData < 0 ? 0 : sumData - endData;
            yAxisDataRun.add(runData);
            // 计算已完成百分数
            if (!day2Data.containsKey(yearMonthDay) || !day2Data.get(yearMonthDay).containsKey("sum")
                || day2Data.get(yearMonthDay).getIntValue("sum") == 0) {
                yAxisDataEndPercent.add(100d);
            } else {
                double temp = day2Data.get(yearMonthDay).getDoubleValue("end")
                    / day2Data.get(yearMonthDay).getDoubleValue("sum") * 100;
                BigDecimal percentB = new BigDecimal(temp);
                double percent = percentB.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                yAxisDataEndPercent.add(percent);
            }
        }
        // 组合数据
        resultMap.put("endData", yAxisDataEnd);
        resultMap.put("runData", yAxisDataRun);
        resultMap.put("percentData", yAxisDataEndPercent);
        resultMap.put("sumData", sumDataList);
        return JsonResultUtil.success(resultMap);
    }

    // 根据开始、结束年月生成单日列表
    private List<String> toGetYearMonthDayList(String start, String end) {
        List<String> result = new ArrayList<>();
        try {
            while (start.compareTo(end) <= 0) {
                result.add(start);
                start =
                    DateFormatUtil.format(DateUtil.addDay(DateFormatUtil.parse(start, "yyyy-MM-dd"), 1), "yyyy-MM-dd");
            }
        } catch (Exception e) {
            logger.error("生成日期异常", e);
        }
        return result;
    }
}
