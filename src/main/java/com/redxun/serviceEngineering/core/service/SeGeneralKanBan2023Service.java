package com.redxun.serviceEngineering.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.cache.CacheUtil;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.HttpClientUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.serviceEngineering.core.dao.*;
import com.redxun.serviceEngineering.core.webservice.IXcmgPeieWebserviceService;
import com.redxun.serviceEngineering.core.webservice.XcmgPeieWebserviceServiceService;
import com.redxun.sys.core.entity.SysDic;
import com.redxun.sys.core.entity.SysTree;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.sys.core.manager.SysTreeManager;
import com.redxun.sys.core.util.SysPropertiesUtil;
import com.redxun.sys.org.manager.OsGroupManager;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import com.redxun.xcmgProjectManager.report.entity.XcmgProjectReportYwph;
import org.apache.commons.collections.map.HashedMap;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SeGeneralKanBan2023Service {
    private static Logger logger = LoggerFactory.getLogger(SeGeneralKanBan2023Service.class);
    @Autowired
    DecorationManualDemandDao decorationManualDemandDao;
    @Autowired
    DecorationManualCollectDao decorationManualCollectDao;
    @Autowired
    OsGroupManager osGroupManager;

    public JsonResult zhuangXiuShouCeGeRenZhiBiaoIni() throws Exception {
        JsonResult result = new JsonResult(true, "加载成功！");
        JSONObject params = new JSONObject();
        params.put("all", "all");
        List<JSONObject> itemList = decorationManualDemandDao.queryItemList(params);//拿到所有的申请明细
        //统计总量，延误量
        Map<String, JSONObject> useridToStatistics = new HashedMap();
        for (JSONObject item : itemList) {
            if (item.containsKey("repUserId") && StringUtil.isNotEmpty(item.getString("repUserId")) &&
                    item.containsKey("accomplishTime") && StringUtil.isNotEmpty(item.getString("accomplishTime"))) {
                if (useridToStatistics.containsKey(item.getString("repUserId"))) {
                    JSONObject statistics = useridToStatistics.get(item.getString("repUserId"));
                    boolean isDelay = DateUtil.judgeEndBigThanStart(item.getDate("accomplishTime"), new Date());
                    if (isDelay && !item.getString("businessStatus").equalsIgnoreCase(DecorationManualDemandService.ITEM_STATUS_CLEAR)) {
                        statistics.put("delay", statistics.getIntValue("delay") + 1);
                    } else {
                        statistics.put("ok", statistics.getIntValue("ok") + 1);
                    }
                } else {
                    JSONObject statistics = new JSONObject();
                    statistics.put("repUser", item.getString("repUser"));
                    statistics.put("ok", 0);
                    statistics.put("delay", 0);
                    boolean isDelay = DateUtil.judgeEndBigThanStart(item.getDate("accomplishTime"), new Date());
                    if (isDelay && !item.getString("businessStatus").equalsIgnoreCase(DecorationManualDemandService.ITEM_STATUS_CLEAR)) {
                        statistics.put("delay", 1);
                    } else {
                        statistics.put("ok", 1);
                    }
                    useridToStatistics.put(item.getString("repUserId"), statistics);
                }
            }
        }
        //计算延误率
        useridToStatistics.forEach((userid, statistics) -> {
            double delayRate = statistics.getDoubleValue("delay") /
                    (statistics.getDoubleValue("delay") + statistics.getDoubleValue("ok"));
            statistics.put("delayRate", delayRate);
            statistics.put("delay", 0 - statistics.getIntValue("delay"));//设置为负数，便于前台展示
        });
        //使用Lambda表达式按照"delayRate"字段降序排序
        Map<String, JSONObject> useridToStatisticsSorted = useridToStatistics.entrySet().stream()
                .sorted(Map.Entry.<String, JSONObject>comparingByValue((v1, v2) ->
                        Double.compare(v2.getDouble("delayRate"), v1.getDouble("delayRate"))))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v1, LinkedHashMap::new));
        //拼接最终的结果数据
        List<String> repUserIds = new ArrayList<>();
        List<String> repUsers = new ArrayList<>();
        List<Integer> processOk = new ArrayList<>();
        List<Integer> processDelay = new ArrayList<>();
        List<Double> delayRate = new ArrayList<>();
        useridToStatisticsSorted.forEach((userid, statistics) -> {
            repUserIds.add(userid);
            repUsers.add(statistics.getString("repUser"));
            processOk.add(statistics.getIntValue("ok"));
            processDelay.add(statistics.getIntValue("delay"));
            delayRate.add(statistics.getDoubleValue("delayRate"));
        });
        JSONObject resultData = new JSONObject();
        resultData.put("repUserIds", repUserIds);
        resultData.put("yAxis", repUsers);
        resultData.put("processOk", processOk);
        resultData.put("processDelay", processDelay);
        resultData.put("delayRate", delayRate);
        result.setData(resultData);
        return result;
    }

    public JsonResult ziLiaoShouJiChaoQiIni() throws Exception {
        JsonResult result = new JsonResult(true, "加载成功！");
        //dataset.dimensions
        List<String> dimensions = new ArrayList<>();
        dimensions.add("部门");
        dimensions.add("超期数量");
        //dataset.source
        List<JSONObject> sources = new ArrayList<>();
        //..
        Map<String, Integer> depNameToExpCount = new LinkedHashMap<>();
        Map<String, String> depNameToExpId = new LinkedHashMap<>();
        JSONObject params = new JSONObject();
        params.put("businessStatus", "C");
        List<Map<String, Object>> collectListC = decorationManualCollectDao.dataListQuery(params);
        params.put("businessStatus", "E");
        List<Map<String, Object>> collectListE = decorationManualCollectDao.dataListQuery(params);
        for (Map<String, Object> collectC : collectListC) {
            JSONObject collectCJson = new JSONObject(collectC);
            if (collectCJson.containsKey("collectorId") && StringUtil.isNotEmpty(collectCJson.getString("collectorId")) &&
                    collectCJson.containsKey("publishTime") && StringUtil.isNotEmpty(collectCJson.getString("publishTime"))) {
                String depName = osGroupManager.getMainDeps(collectCJson.getString("collectorId"), "1").getName();
                if (DateUtil.judgeEndBigThanStart(collectCJson.getDate("publishTime"), new Date())) {
                    if (depNameToExpCount.containsKey(depName)) {
                        depNameToExpCount.put(depName, depNameToExpCount.get(depName) + 1);
                        String id = depNameToExpId.get(depName);
                        id = id + "," + collectC.get("id").toString();
                        depNameToExpId.put(depName, id);
                    } else {
                        depNameToExpCount.put(depName, 1);
                        depNameToExpId.put(depName, collectC.get("id").toString());
                    }
                }
            }
        }
        for (Map<String, Object> collectE : collectListE) {
            JSONObject collectEJson = new JSONObject(collectE);
            if (collectEJson.containsKey("collectorId2") && StringUtil.isNotEmpty(collectEJson.getString("collectorId2")) &&
                    collectEJson.containsKey("publishTime") && StringUtil.isNotEmpty(collectEJson.getString("publishTime"))) {
                String depName = osGroupManager.getMainDeps(collectEJson.getString("collectorId2"), "1").getName();
                if (DateUtil.judgeEndBigThanStart(collectEJson.getDate("publishTime"), new Date())) {
                    if (depNameToExpCount.containsKey(depName)) {
                        depNameToExpCount.put(depName, depNameToExpCount.get(depName) + 1);
                        String id = depNameToExpId.get(depName);
                        id = id + "," + collectE.get("id").toString();
                        depNameToExpId.put(depName, id);
                    } else {
                        depNameToExpCount.put(depName, 1);
                        depNameToExpId.put(depName, collectE.get("id").toString());
                    }
                }
            }
        }
        depNameToExpCount.forEach((depName, expCount) -> {
            JSONObject source = new JSONObject();
            source.put("部门", depName);
            source.put("超期数量", expCount);
            sources.add(source);
        });
        JSONObject resultData = new JSONObject();
        resultData.put("dimensions", dimensions);
        resultData.put("source", sources);
        resultData.put("depNameToExpId", depNameToExpId);
        result.setData(resultData);
        return result;
    }
}
