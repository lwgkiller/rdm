package com.redxun.rdMaterial.core.service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.rdMaterial.core.dao.RdMaterialSummaryDao;
import com.redxun.serviceEngineering.core.dao.DecorationManualCollectDao;
import com.redxun.serviceEngineering.core.dao.DecorationManualDemandDao;
import com.redxun.serviceEngineering.core.service.DecorationManualDemandService;
import com.redxun.sys.org.manager.OsGroupManager;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RdMaterialGeneralKanBanService {
    private static Logger logger = LoggerFactory.getLogger(RdMaterialGeneralKanBanService.class);
    @Autowired
    private RdMaterialSummaryDao rdMaterialSummaryDao;

    //..
    public JsonResult zhiLiuWuLiaoTongJi(String theYear) throws Exception {
        JsonResult result = new JsonResult(true, "加载成功！");
        //dataset.dimensions
        List<String> dimensions = new ArrayList<>();
        dimensions.add("超期时长");
        dimensions.add("数量");
        //dataset.source
        List<JSONObject> sources = new ArrayList<>();
        //..
        Map<String, Integer> timespanToCount = new LinkedHashMap<>();
        timespanToCount.put("未超期", 0);
        timespanToCount.put("2-5个月", 0);
        timespanToCount.put("超过5个月", 0);
        JSONObject params = new JSONObject();
        if (StringUtil.isNotEmpty(theYear)) {
            params.put("theYear", theYear);
        }
        List<JSONObject> itemList = rdMaterialSummaryDao.itemListQuery(params);
        for (JSONObject item : itemList) {
            Date inDate = item.getDate("inDate");
            Integer untreatedTimespan = DateUtil.daysBetween(inDate, new Date());
            if (item.containsKey("untreatedQuantity") && item.getInteger("untreatedQuantity") != 0) {
                if (untreatedTimespan >= 60 && untreatedTimespan < 150) {
                    //timespanToCount.put("2-5个月", timespanToCount.get("2-5个月") + 1);
                    timespanToCount.put("2-5个月", timespanToCount.get("2-5个月") + item.getInteger("untreatedQuantity"));
                } else if (untreatedTimespan >= 150) {
                    //timespanToCount.put("超过5个月", timespanToCount.get("超过5个月") + 1);
                    timespanToCount.put("超过5个月", timespanToCount.get("超过5个月") + item.getInteger("untreatedQuantity"));
                } else {
                    //timespanToCount.put("未超期", timespanToCount.get("未超期") + 1);
                    timespanToCount.put("未超期", timespanToCount.get("未超期") + item.getInteger("untreatedQuantity"));
                }
            }
        }
        timespanToCount.forEach((timespan, count) -> {
            JSONObject source = new JSONObject();
            source.put("超期时长", timespan);
            source.put("数量", count);
            sources.add(source);
        });
        JSONObject resultData = new JSONObject();
        resultData.put("dimensions", dimensions);
        resultData.put("source", sources);
        result.setData(resultData);
        return result;
    }

    //..
    public JsonResult zhiLiuWuLiaoTongJi2(String theYear) throws Exception {
        JsonResult result = new JsonResult(true, "加载成功！");
        //dataset.dimensions
        List<String> dimensions = new ArrayList<>();
        dimensions.add("责任部门");
        dimensions.add("2-5个月");
        dimensions.add("超过5个月");
        //dataset.source
        List<JSONObject> sources = new ArrayList<>();
        //..
        Map<String, JSONObject> responsibleDepToStatistic = new LinkedHashMap<>();
        JSONObject params = new JSONObject();
        if (StringUtil.isNotEmpty(theYear)) {
            params.put("theYear", theYear);
        }
        List<JSONObject> itemList = rdMaterialSummaryDao.itemListQuery(params);
        for (JSONObject item : itemList) {
            Date inDate = item.getDate("inDate");
            Integer untreatedTimespan = DateUtil.daysBetween(inDate, new Date());
            if (item.containsKey("untreatedQuantity") && item.getInteger("untreatedQuantity") != 0) {
                JSONObject statistic = new JSONObject();
                String responsibleDep = item.getString("responsibleDep");
                if (untreatedTimespan >= 60 && untreatedTimespan < 150) {
                    if (responsibleDepToStatistic.containsKey(responsibleDep)) {
                        statistic = responsibleDepToStatistic.get(responsibleDep);
                    } else {
                        statistic.fluentPut("2-5个月", 0).fluentPut("超过5个月", 0);
                    }
                    //statistic.put("2-5个月", statistic.getInteger("2-5个月") + 1);
                    statistic.put("2-5个月", statistic.getInteger("2-5个月") + item.getInteger("untreatedQuantity"));
                    responsibleDepToStatistic.put(responsibleDep, statistic);
                } else if (untreatedTimespan >= 150) {
                    if (responsibleDepToStatistic.containsKey(responsibleDep)) {
                        statistic = responsibleDepToStatistic.get(responsibleDep);
                    } else {
                        statistic.fluentPut("2-5个月", 0).fluentPut("超过5个月", 0);
                    }
                    //statistic.put("超过5个月", statistic.getInteger("超过5个月") + 1);
                    statistic.put("超过5个月", statistic.getInteger("超过5个月") + item.getInteger("untreatedQuantity"));
                    responsibleDepToStatistic.put(responsibleDep, statistic);
                }
            }
        }
        responsibleDepToStatistic.forEach((responsibleDep, statistic) -> {
            JSONObject source = new JSONObject();
            source.put("责任部门", responsibleDep);
            source.put("2-5个月", statistic.getInteger("2-5个月"));
            source.put("超过5个月", statistic.getInteger("超过5个月"));
            sources.add(source);
        });
        JSONObject resultData = new JSONObject();
        resultData.put("dimensions", dimensions);
        resultData.put("source", sources);
        result.setData(resultData);
        return result;
    }

    //..
    public JsonResult chuRuKuWuLiaoChuLi(String theYear, String type) throws Exception {
        JsonResult result = new JsonResult(true, "加载成功！");
        //dataset.dimensions
        List<String> dimensions = new ArrayList<>();
        if (type.equalsIgnoreCase("materialType")) {
            dimensions.add("物料类型");
        } else if (type.equalsIgnoreCase("reasonForStorage")) {
            dimensions.add("入库原因");
        }
        dimensions.add("处理数量");
        dimensions.add("未处理数量");
        //dataset.source
        List<JSONObject> sources = new ArrayList<>();
        //..
        Map<String, JSONObject> statisticKeyToStatistic = new LinkedHashMap<>();
        Integer treated = 0;
        Integer untreated = 0;
        JSONObject params = new JSONObject();
        if (StringUtil.isNotEmpty(theYear)) {
            params.put("theYear", theYear);
        }
        List<JSONObject> itemList = rdMaterialSummaryDao.itemListQuery(params);
        for (JSONObject item : itemList) {
            treated += item.getInteger("inQuantity") - item.getInteger("untreatedQuantity");
            untreated += item.getInteger("untreatedQuantity");
            JSONObject statistic = new JSONObject();
            if (type.equalsIgnoreCase("materialType")) {
                String materialType = item.getString("materialType");
                if (statisticKeyToStatistic.containsKey(materialType)) {
                    statistic = statisticKeyToStatistic.get(materialType);
                } else {
                    statistic.fluentPut("处理数量", 0).fluentPut("未处理数量", 0);
                }
                statistic.put("处理数量", statistic.getInteger("处理数量") +
                        item.getInteger("inQuantity") - item.getInteger("untreatedQuantity"));
                statistic.put("未处理数量", statistic.getInteger("未处理数量") + item.getInteger("untreatedQuantity"));
                statisticKeyToStatistic.put(materialType, statistic);
            } else if (type.equalsIgnoreCase("reasonForStorage")) {
                String reasonForStorage = item.getString("reasonForStorage");
                if (statisticKeyToStatistic.containsKey(reasonForStorage)) {
                    statistic = statisticKeyToStatistic.get(reasonForStorage);
                } else {
                    statistic.fluentPut("处理数量", 0).fluentPut("未处理数量", 0);
                }
                statistic.put("处理数量", statistic.getInteger("处理数量") +
                        item.getInteger("inQuantity") - item.getInteger("untreatedQuantity"));
                statistic.put("未处理数量", statistic.getInteger("未处理数量") + item.getInteger("untreatedQuantity"));
                statisticKeyToStatistic.put(reasonForStorage, statistic);
            }
        }
        statisticKeyToStatistic.forEach((statisticKey, statistic) -> {
            JSONObject source = new JSONObject();
            if (type.equalsIgnoreCase("materialType")) {
                source.put("物料类型", statisticKey);
            } else if (type.equalsIgnoreCase("reasonForStorage")) {
                source.put("入库原因", statisticKey);
            }
            source.put("处理数量", statistic.getInteger("处理数量"));
            source.put("未处理数量", statistic.getInteger("未处理数量"));
            sources.add(source);
        });
        JSONObject resultData = new JSONObject();
        resultData.put("dimensions", dimensions);
        resultData.put("source", sources);
        resultData.put("treated", treated);
        resultData.put("untreated", untreated);
        result.setData(resultData);
        return result;
    }

    //..
    public JsonResult chuLiWuLiaoTongJi(String theYear) throws Exception {
        JsonResult result = new JsonResult(true, "加载成功！");
        //dataset.dimensions
        List<String> dimensions = new ArrayList<>();
        dimensions.add("处置方式");
        dimensions.add("数量");
        //dataset.source
        List<JSONObject> sources = new ArrayList<>();
        //..
        Map<String, Integer> handlingMethodToCount = new LinkedHashMap<>();
        //..
        JSONObject params = new JSONObject();
        if (StringUtil.isNotEmpty(theYear)) {
            params.put("theYear", theYear);
        }
        List<JSONObject> itemList = rdMaterialSummaryDao.handingItemListQuery(params);
        for (JSONObject item : itemList) {
            String handlingMethod = item.getString("handlingMethod");
            if (handlingMethodToCount.containsKey(handlingMethod)) {
                //handlingMethodToCount.put(handlingMethod, handlingMethodToCount.get(handlingMethod) + 1);
                handlingMethodToCount.put(handlingMethod, handlingMethodToCount.get(handlingMethod) + item.getInteger("handlingQuantity"));
            } else {
                //handlingMethodToCount.put(handlingMethod, 1);
                handlingMethodToCount.put(handlingMethod, item.getInteger("handlingQuantity"));
            }
        }
        handlingMethodToCount.forEach((handlingMethod, count) -> {
            JSONObject source = new JSONObject();
            source.put("处置方式", handlingMethod);
            source.put("数量", count);
            sources.add(source);
        });
        JSONObject resultData = new JSONObject();
        resultData.put("dimensions", dimensions);
        resultData.put("source", sources);
        result.setData(resultData);
        return result;
    }
}
