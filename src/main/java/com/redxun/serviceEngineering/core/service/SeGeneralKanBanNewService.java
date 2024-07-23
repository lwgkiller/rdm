package com.redxun.serviceEngineering.core.service;

import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
import com.redxun.saweb.util.IdUtil;
import com.redxun.serviceEngineering.core.dao.*;
import com.redxun.serviceEngineering.core.webservice.IXcmgPeieWebserviceService;
import com.redxun.serviceEngineering.core.webservice.XcmgPeieWebserviceServiceService;
import com.redxun.sys.core.entity.SysDic;
import com.redxun.sys.core.entity.SysTree;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.sys.core.manager.SysTreeManager;
import com.redxun.sys.core.util.SysPropertiesUtil;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;

@Service
public class SeGeneralKanBanNewService {
    private static Logger logger = LoggerFactory.getLogger(SeGeneralKanBanNewService.class);
    @Autowired
    private SeGeneralKanBanNewDao seGeneralKanBanNewDao;
    @Autowired
    private SysTreeManager sysTreeManager;
    @Autowired
    private SysDicManager sysDicManager;
    @Autowired
    private DecorationManualDemandDao decorationManualDemandDao;
    @Autowired
    private DecorationManualFileDao decorationManualFileDao;
    @Autowired
    private MaintenanceManualfileDao maintenanceManualfileDao;
    @Autowired
    private MaintenanceManualDemandDao maintenanceManualDemandDao;
    @Autowired
    private ExportPartsAtlasDao exportPartsAtlasDao;

    // ..刷新缓存-总体情况
    public JsonResult refreshCacheGenSitu() {
        JsonResult result = new JsonResult(true, "刷新缓存成功！");
        try {
            // 机型图册总数量
            JsonResult<JSONObject> resultJson = getAccumulatedFromGss();
            if (resultJson.getSuccess()) {
                CacheUtil.addCache("accumulatedModelFromGss", resultJson.getData().getInteger("structModelAtlas"),
                    Integer.MAX_VALUE);
                CacheUtil.addCache("accumulatedInstanceFromGss", resultJson.getData().getInteger("structCarAtlas"),
                    Integer.MAX_VALUE);
            } else {
                logger.error("getAccumulatedFromGss调用失败:" + resultJson.getMessage());
                result.setSuccess(false);
                result.setMessage(resultJson.getMessage());
                return result;
            }
            return result;
        } catch (Exception e) {
            logger.error("SeGeneralKanBanNewService的refreshCacheGenSitu出现运行时错误:" + e.getMessage());
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            return result;
        }
    }

    // ..刷新缓存-发运产品随机文件完备性
    public JsonResult refreshCacheAtDocAcpRate(String signYear) {
        JsonResult result = new JsonResult(true, "刷新缓存成功！");
        String beginDate = signYear + "-01-01";
        String endDate = signYear + "-12-31";
        try {
            // 起止日期内按月汇总的发运总数，发布数量，发布率
            JsonResult<JSONObject> resultJson = getShipmentGroupByMonthFromGss(beginDate, endDate);
            if (resultJson.getSuccess()) {
                JSONObject cacheJson = (JSONObject)CacheUtil.getCache("shipmentGroupByMonthFromGss");
                if (cacheJson == null) {
                    cacheJson = new JSONObject();
                }
                for (Map.Entry<String, Object> entry : resultJson.getData().entrySet()) {
                    cacheJson.put(entry.getKey(), entry.getValue());
                }
                CacheUtil.addCache("shipmentGroupByMonthFromGss", cacheJson, Integer.MAX_VALUE);
            } else {
                logger.error("getShipmentGroupByMonthFromGss调用失败:" + resultJson.getMessage());
                result.setSuccess(false);
                result.setMessage(resultJson.getMessage());
                return result;
            }
            return result;
        } catch (Exception e) {
            logger.error("SeGeneralKanBanNewService的refreshCacheAtDocAcpRate出现运行时错误:" + e.getMessage());
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            return result;
        }
    }

    // ..刷新缓存-补发信息
    public JsonResult refreshCacheAtDocReDisRate() {
        JsonResult result = new JsonResult(true, "刷新缓存成功！");
        try {
            // 机型图册总数量
            JsonResult<JSONObject> resultJson = getReDispatchInfoFromOa();
            // @lwgkiller:常规实现
            // List<Object> objectList = new ArrayList<>();
            // JSONArray items = resultJson.getData().getJSONArray("items");
            // for (Object itemObj : items) {
            // if(((JSONObject)itemObj).containsKey("items")){
            // objectList.add(itemObj);
            // }
            // }
            // resultJson.getData().put("items",objectList);
            // @lwgkiller:lambda流式实现，对比常规实现，省去了中间变量objectList，items以及对items进行遍历的模板代码
            // 更直接表达业务意图本身，代码清爽有力，确实优雅
            resultJson.getData().put("items", resultJson.getData().getJSONArray("items").stream()
                .filter(object -> ((JSONObject)object).containsKey("pin")).collect(Collectors.toList()));
            //
            if (resultJson.getSuccess()) {
                CacheUtil.addCache("reDispatchInfo", resultJson.getData(), Integer.MAX_VALUE);
            } else {
                logger.error("getReDispatchInfoFromOa调用失败:" + resultJson.getMessage());
            }
            return result;
        } catch (Exception e) {
            logger.error("SeGeneralKanBanNewService的refreshCacheAtDocReDisRate出现运行时错误:" + e.getMessage());
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            return result;
        }
    }

    // ..调用TIS获取:机型图册总数量,覆盖的实例图册总数量 todo:TIS 正式环境ok!!!
    private JsonResult<JSONObject> getAccumulatedFromGss() {
        JsonResult<JSONObject> result = new JsonResult(true);
        String url = sysDicManager.getBySysTreeKeyAndDicKey("serviceEngineeringCallbackUrls", "getAccumulatedFromTis")
            .getValue();
        Map<String, String> reqHeaders = new HashMap<>();
        reqHeaders.put("Content-Type", "application/json;charset=utf-8");
        try {
            HttpClientUtil.HttpRtnModel httpRtnModel = HttpClientUtil.getFromUrlHreader(url, reqHeaders);
            JSONObject dataFromTis = JSONObject.parseObject(httpRtnModel.getContent()).getJSONObject("data");
            result.setData(dataFromTis);
            return result;
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            return result;
        }
    }

    // ..调用GSS获取:起止日期内按月汇总的发运总数，发布数量，发布率 todo:GSS 正式环境ok!!!
    private JsonResult<JSONObject> getShipmentGroupByMonthFromGss(String beginDate, String endDate) {
        JsonResult<JSONObject> result = new JsonResult(true);
        String url = SysPropertiesUtil.getGlobalProperty("getShipmentGroupByMonthFromGss");
        url += "?beginDate=" + beginDate + "&endDate=" + endDate;
        Map<String, String> params = new HashMap<>();
        Map<String, String> reqHeaders = new HashMap<>();
        reqHeaders.put("Content-Type", "application/json;charset=utf-8");
        if (url.contains("xgssuat")) {
            reqHeaders.put("Authorization",
                sysDicManager.getBySysTreeKeyAndDicKey("serviceEngineeringCallbackUrls", "xgssuat").getValue());
        } else {
            reqHeaders.put("Authorization",
                sysDicManager.getBySysTreeKeyAndDicKey("serviceEngineeringCallbackUrls", "xgssapp").getValue());
        }
        try {
            HttpClientUtil.HttpRtnModel httpRtnModel = HttpClientUtil.postFromUrl(url, reqHeaders, params);
            JSONObject dataFromGss = JSONObject.parseObject(httpRtnModel.getContent()).getJSONObject("data");
            result.setData(dataFromGss);
            return result;
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            return result;
        }
    }

    // ..调用OA获取:补发信息 todo:OA 正式环境ok!!!
    private JsonResult<JSONObject> getReDispatchInfoFromOa() {
        JsonResult<JSONObject> result = new JsonResult(true);
        try {
            XcmgPeieWebserviceServiceService xcmgPeieWebserviceServiceService = new XcmgPeieWebserviceServiceService();
            IXcmgPeieWebserviceService xcmgPeieWebserviceServicePort =
                xcmgPeieWebserviceServiceService.getXcmgPeieWebserviceServicePort();
            JSONObject oaReturnDate = (JSONObject)JSONObject.parse(xcmgPeieWebserviceServicePort.data());
            JSONObject oaBusinessData = oaReturnDate.getJSONObject("data");
            result.setData(oaBusinessData);
            return result;
        } catch (Exception e) {
            logger.error("系统异常：", e);
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            return result;
        }
    }

    // ..被GSS调用接收:发运的PIN码增量 todo:GSS 正式环境ok!!!
    public void doInsertGssShipmentCache(JSONObject result, String postData, String whatTypeToStart) {
        try {
            List<JSONObject> shipmentCacheList = seGeneralKanBanNewDao.gssShipmentCacheListQuery(null);
            Set<String> shipmentCachePinSet = new HashSet<>();
            for (JSONObject shipmentCache : shipmentCacheList) {
                shipmentCachePinSet.add(shipmentCache.getString("pin"));
            }
            //
            JSONArray pins = JSONObject.parseObject(postData).getJSONArray("pins");
            for (Object pin : pins) {
                JSONObject pinJson = new JSONObject();
                pinJson.put("id", IdUtil.getId());
                pinJson.put("pin", ((JSONObject)pin).getString("pin"));
                pinJson.put("shipmentTime", ((JSONObject)pin).getString("date"));
                pinJson.put("material", ((JSONObject)pin).getString("material"));
                pinJson.put("isOversea", ((JSONObject)pin).getString("isOversea"));
                pinJson.put("salesModels", ((JSONObject)pin).getString("salesModels"));
                pinJson.put("designModel", ((JSONObject)pin).getString("designModel"));
                pinJson.put("CREATE_BY_", "1");
                pinJson.put("CREATE_TIME_", new Date());
                if (shipmentCachePinSet.contains(pinJson.getString("pin"))) {
                    pinJson.put("UPDATE_BY_", "1");
                    pinJson.put("UPDATE_TIME_", new Date());
                    seGeneralKanBanNewDao.updateGssShipmentCache(pinJson);// 目前只更新salesModels
                } else {
                    seGeneralKanBanNewDao.insertGssShipmentCache(pinJson);
                }
            }
        } catch (Exception e) {
            logger.error("GSS发运的PIN码增量传输失败" + e.getMessage(), e);
            throw e;
        }
    }

    // ..数量类统计-通用/////////////////////////////////////////////////////////////////
    public JSONObject getAmount(HttpServletRequest request, HttpServletResponse response, String postDataStr) {
        HashSet<String> maintSet = new HashSet<>();
        HashSet<String> maintSet2 = new HashSet<>();
        JSONObject resultJson = new JSONObject();
        JSONObject param = new JSONObject();
        param.put("manualStatus", "可转出");
        List<JSONObject> decoList = decorationManualFileDao.dataListQuery(param);
        for (JSONObject deco : decoList) {
            if (deco.getString("manualType").equalsIgnoreCase("装修手册")) {
                maintSet.add(deco.getString("designModel"));
            } else {
                maintSet2.add(deco.getString("designModel"));
            }
        }
        resultJson.put("decoAmount", maintSet.size());
        resultJson.put("decoSubAmount", maintSet2.size());
        return resultJson;
    }

    // ..decoCompletion:装修手册需求完成情况
    public JSONObject getDecoCompletionData(HttpServletRequest request, HttpServletResponse response,
        String postDataStr) {
        // 找到所有的装修手册需求申请
        List<Map<String, Object>> decoDemandList = decorationManualDemandDao.dataListQuery(null);
        JSONObject param = new JSONObject();
        param.put("manualType", "装修手册");
        param.put("manualStatus", "可转出");
        // 找到可转出状态的装修手册归档文件
        List<JSONObject> decoManualList = decorationManualFileDao.dataListQuery(param);
        // 保留找到的归档文件物料号的唯一记录集
        HashSet<String> decoManualSet = new HashSet<>();
        for (JSONObject decoManual : decoManualList) {
            decoManualSet.add(decoManual.getString("materialCode"));
        }
        // dataset.dimensions
        List<String> dimensions = new ArrayList<>();
        dimensions.add("销售区域");
        dimensions.add("需求制作");
        dimensions.add("已完成");
        // dataset.source
        List<JSONObject> sources = new ArrayList<>();
        JSONObject Statistics = new JSONObject(new LinkedHashMap());
        // 根据字典配置的销售区域进行各柱名称和数据初始化
        SysTree sysTree = sysTreeManager.getByKey("serviceEngineeringDecorationManualSalesArea");
        List<SysDic> sysDics = new ArrayList<SysDic>();
        sysDics = sysDicManager.getByTreeId(sysTree.getTreeId());
        JSONObject source;
        for (SysDic sysDic : sysDics) {
            source = new JSONObject();
            source.put("销售区域", sysDic.getValue());
            source.put("需求制作", 0);
            source.put("已完成", 0);
            Statistics.put(sysDic.getValue(), source);
        }
        // 初始化合计柱子
        source = new JSONObject();
        source.put("销售区域", "合计");
        source.put("需求制作", 0);
        source.put("已完成", 0);
        Statistics.put("合计", source);
        // 处理每个需求单的实际业务统计
        for (Map<String, Object> business : decoDemandList) {
            JSONObject decoDemand = new JSONObject(business);
            String salesArea = decoDemand.getString("salesArea");
            // 处理各销售区域数据
            source = Statistics.getJSONObject(salesArea);
            if (source != null) {
                source.put("需求制作", source.getIntValue("需求制作") + 1);
                if (decoManualSet.contains(decoDemand.getString("materialCode"))) {
                    source.put("已完成", source.getIntValue("已完成") + 1);
                }
                Statistics.put(salesArea, source);
            }
            // 处理合计
            source = Statistics.getJSONObject("合计");
            if (source != null) {
                source.put("需求制作", source.getIntValue("需求制作") + 1);
                if (decoManualSet.contains(decoDemand.getString("materialCode"))) {
                    source.put("已完成", source.getIntValue("已完成") + 1);
                }
                Statistics.put("合计", source);
            }
            //// ----------------------
        }
        for (Map.Entry entry : Statistics.entrySet()) {
            sources.add((JSONObject)entry.getValue());
        }
        JSONObject resultJson = new JSONObject();
        resultJson.put("dimensions", dimensions);
        resultJson.put("source", sources);
        return resultJson;
    }

    // ..maintTransRate:中文操保手册英文译本翻译率
    public List<JSONObject> getMaintTransRateData(HttpServletRequest request, HttpServletResponse response,
        String postDataStr) {
        // 找到所有的操保手册
        JSONObject param = new JSONObject();
        List<JSONObject> maintList = maintenanceManualfileDao.dataListQuery(null);
        // 初始化两个set，一个存储中文的设计型号，一个存储英文的设计型号，各只保留一个
        HashSet<String> maintZhSet = new HashSet<>();
        HashSet<String> maintEnSet = new HashSet<>();
        for (JSONObject maint : maintList) {
            if (maint.getString("manualLanguage").equalsIgnoreCase("中文")) {
                maintZhSet.add(maint.getString("designModel"));
            } else if (maint.getString("manualLanguage").equalsIgnoreCase("英语")) {
                maintEnSet.add(maint.getString("designModel"));
            }
        }
        Integer completed = 0, incompleted = 0;
        // 每个中文设计型号，有对应的英文设计型号，算已完成。所有中文设计型号数量-已完成的数量=未完成
        for (String designModelZh : maintZhSet) {
            if (maintEnSet.contains(designModelZh)) {
                completed++;
            }
        }
        incompleted = maintZhSet.size() - completed;
        // ..
        JSONObject jsonCompleted = new JSONObject();
        jsonCompleted.put("value", completed);
        jsonCompleted.put("name", "已完成");
        jsonCompleted.put("total", maintZhSet.size());
        JSONObject jsonIncompleted = new JSONObject();
        jsonIncompleted.put("value", incompleted);
        jsonIncompleted.put("name", "待完成");
        jsonIncompleted.put("total", maintZhSet.size());
        List<JSONObject> resultList = new ArrayList<>();
        resultList.add(jsonCompleted);
        resultList.add(jsonIncompleted);
        return resultList;
    }

    // ..manualMinorLanguage:小语种手册数量
    // 操保
    public List<JSONObject> getMaintManualMinorLangData(HttpServletRequest request, HttpServletResponse response,
        String postDataStr) {
        // 找到所有的操保手册
        JSONObject param = new JSONObject();
        List<JSONObject> maintList = maintenanceManualfileDao.dataListQuery(null);
        // 初始化一个map，存储<语言,数量>
        HashMap<String, Integer> maintManualMinorLang = new HashMap<>();
        for (JSONObject maint : maintList) {
            if (!maint.getString("manualLanguage").equalsIgnoreCase("中文")) {
                if (maintManualMinorLang.containsKey(maint.getString("manualLanguage"))) {
                    Integer count = maintManualMinorLang.get(maint.getString("manualLanguage"));
                    count++;
                    maintManualMinorLang.put(maint.getString("manualLanguage"), count);
                } else {
                    maintManualMinorLang.put(maint.getString("manualLanguage"), 1);
                }
            }
        }
        // ..
        List<JSONObject> resultList = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : maintManualMinorLang.entrySet()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("value", entry.getValue());
            jsonObject.put("name", entry.getKey());
            resultList.add(jsonObject);
        }
        return resultList;
    }

    // 装修
    public List<JSONObject> getDecoManualMinorLangData(HttpServletRequest request, HttpServletResponse response,
        String postDataStr) {
        // 找到所有的装修手册
        JSONObject param = new JSONObject();
        List<JSONObject> decoList = decorationManualFileDao.dataListQuery(null);
        // 初始化一个map，存储<语言,数量>
        HashMap<String, Integer> decoManualMinorLang = new HashMap<>();
        for (JSONObject deco : decoList) {
            if (!deco.getString("manualLanguage").equalsIgnoreCase("中文")) {
                if (decoManualMinorLang.containsKey(deco.getString("manualLanguage"))) {
                    Integer count = decoManualMinorLang.get(deco.getString("manualLanguage"));
                    count++;
                    decoManualMinorLang.put(deco.getString("manualLanguage"), count);
                } else {
                    decoManualMinorLang.put(deco.getString("manualLanguage"), 1);
                }
            }
        }
        // ..
        List<JSONObject> resultList = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : decoManualMinorLang.entrySet()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("value", entry.getValue());
            jsonObject.put("name", entry.getKey());
            resultList.add(jsonObject);
        }
        return resultList;
    }

    // ..generalSituation:总体情况
    public JSONObject getGeneralSituationData(HttpServletRequest request, HttpServletResponse response,
        String postDataStr) {
        // dataset.dimensions
        List<String> dimensions = new ArrayList<>();
        dimensions.add("图册类型");
        dimensions.add("数量");
        // dataset.source
        List<JSONObject> sources = new ArrayList<>();
        JSONObject Statistics = new JSONObject(new LinkedHashMap());
        // 零件图册数量来自缓存
        JSONObject source = new JSONObject();
        source.put("图册类型", "零件图册");
        source.put("数量",
            CacheUtil.containKey("accumulatedModelFromGss") ? CacheUtil.getCache("accumulatedModelFromGss") : 0);
        sources.add(source);
        // 初始化一个set存储设计型号，只保留一个
        HashSet<String> maintSet = new HashSet<>();
        // 操保手册数量
        JSONObject param = new JSONObject();
        param.put("manualStatus", "可打印");
        List<JSONObject> maintList = maintenanceManualfileDao.dataListQuery(param);
        for (JSONObject maint : maintList) {
            maintSet.add(maint.getString("designModel"));
        }
        source = new JSONObject();
        source.put("图册类型", "操保手册");
        source.put("数量", maintSet.size());
        sources.add(source);
        // 装修手册和分子手册数量
        maintSet.clear();
        // HashSet<String> maintSet2 = new HashSet<>();
        Integer otherCount = 0;
        param.clear();
        param.put("manualStatus", "可转出");
        List<JSONObject> decoList = decorationManualFileDao.dataListQuery(param);
        for (JSONObject deco : decoList) {
            if (deco.getString("manualType").equalsIgnoreCase("装修手册")) {
                maintSet.add(deco.getString("designModel"));
            } else {
                // maintSet2.add(deco.getString("designModel"));
                otherCount++;
            }
        }
        source = new JSONObject();
        source.put("图册类型", "装修手册");
        source.put("数量", maintSet.size());
        sources.add(source);
        source = new JSONObject();
        source.put("图册类型", "分子手册");
        // source.put("数量", maintSet2.size());
        source.put("数量", otherCount);
        sources.add(source);
        JSONObject resultJson = new JSONObject();
        resultJson.put("dimensions", dimensions);
        resultJson.put("source", sources);
        // 在返回结构中再加上覆盖实例数量，单独显示，来自缓存
        resultJson.put("accumulatedInstance",
            CacheUtil.containKey("accumulatedInstanceFromGss") ? CacheUtil.getCache("accumulatedInstanceFromGss") : 0);
        return resultJson;
    }

    // ..exportAtDocAcpRate:出口产品随机文件完成率(淘汰的算法)
    public JSONObject getExportAtDocAcpRateData_obsolete(HttpServletRequest request, HttpServletResponse response,
        String postDataStr) {
        // 找到所有的操保手册需求申请，同时形成<"需求单号-销售型号-物料编码-语言",需求申请>map,以备最后进行筛选
        List<String> notStatus = new ArrayList<>();
        notStatus.add("DISCARD_END");
        JSONObject param = new JSONObject();
        param.put("notStatus", notStatus);
        List<Map<String, Object>> maintDemandList = maintenanceManualDemandDao.dataListQuery(param);
        HashMap<String, JSONObject> maintDemandMap = new HashMap<>();
        for (Map<String, Object> maintDemand : maintDemandList) {
            String[] manualLanguages = maintDemand.get("manualLanguage").toString().split(",");
            for (String manualLanguage : manualLanguages) {
                if (StringUtil.isNotEmpty(maintDemand.get("demandListNo").toString())
                    && StringUtil.isNotEmpty(maintDemand.get("salesModel").toString())
                    && StringUtil.isNotEmpty(maintDemand.get("materialCode").toString())) {
                    maintDemandMap.put(
                        maintDemand.get("demandListNo").toString() + "-" + maintDemand.get("salesModel").toString()
                            + "-" + maintDemand.get("materialCode").toString() + "-" + manualLanguage,
                        new JSONObject(maintDemand));
                }
            }
        }
        // dataset.dimensions
        List<String> dimensions = new ArrayList<>();
        dimensions.add("月份");
        dimensions.add("月度发运产品总数");
        dimensions.add("实例图册发布数量");
        dimensions.add("操保手册发布数量");
        // dataset.source
        List<JSONObject> sources = new ArrayList<>();
        JSONObject Statistics = new JSONObject(new LinkedHashMap());
        JSONObject source;
        for (int i = 1; i <= 12; i++) {
            source = new JSONObject();
            source.put("月份", i + "月");
            source.put("月度发运产品总数", 0);
            source.put("实例图册发布数量", 0);
            source.put("操保手册发布数量", 0);
            if (i > 9) {
                Statistics.put("" + i, source);
            } else {
                Statistics.put("0" + i, source);
            }
        }
        // 找到符合条件零件图册出口制作任务
        JSONObject postDataJson = JSONObject.parseObject(postDataStr);
        notStatus = new ArrayList<>();
        notStatus.add("08ZF");// 作废
        param = new JSONObject();
        param.put("notStatus", notStatus);
        param.put("signYear", postDataJson.getString("signYear"));
        List<JSONObject> exportList = exportPartsAtlasDao.queryAtlasTaskList(param);
        // 处理每个出口制作任务的实际业务统计
        for (JSONObject export : exportList) {
            if (StringUtil.isNotEmpty(export.getString("dispatchTime"))
                && StringUtil.isNotEmpty(export.getString("taskStatus"))
                && StringUtil.isNotEmpty(export.getString("languages"))
                && StringUtil.isNotEmpty(export.getString("demandNo"))
                && StringUtil.isNotEmpty(export.getString("saleType"))
                && StringUtil.isNotEmpty(export.getString("matCode"))) {
                String dispatchMonth = export.getString("dispatchTime").substring(5, 7);
                // 处理各月份的数据
                source = Statistics.getJSONObject(dispatchMonth);
                if (source != null) {
                    // 有一个任务则某月的发运数+1
                    source.put("月度发运产品总数", source.getIntValue("月度发运产品总数") + 1);
                    // 如果任务状态满足，某月的实例发布数+1
                    if (export.getString("taskStatus").equalsIgnoreCase("06ZZWCYZC")
                        || export.getString("taskStatus").equalsIgnoreCase("07YJS")) {// 制作完成已转出||档案室已接收
                        source.put("实例图册发布数量", source.getIntValue("实例图册发布数量") + 1);
                    }
                    // 通过其"需求单号-销售型号-物料编码-语言"去操保手册里面去匹配
                    // 没有的算完成，有的业务状态为"打印"或"结束",流程状态"作废"算完成
                    String[] exportLanguages = export.getString("languages").split(",");
                    for (String exportLanguage : exportLanguages) {
                        String toBeFindKey = export.getString("demandNo") + "-" + export.getString("saleType") + "-"
                            + export.getString("matCode") + "-" + exportLanguage;
                        if (maintDemandMap.containsKey(toBeFindKey)) {// 有的业务状态为"打印"或"结束"
                            JSONObject toBeJudgeDemand = maintDemandMap.get(toBeFindKey);
                            if (toBeJudgeDemand.getString("businessStatus").equalsIgnoreCase("E-printing")
                                || toBeJudgeDemand.getString("businessStatus").equalsIgnoreCase("F-close")) {
                                source.put("操保手册发布数量", source.getIntValue("操保手册发布数量") + 1);
                                break;
                            }
                        } else {// 没有的算完成,流程状态"作废"的最初就被过滤掉了,如果没有相同的不作废的记录，自然就算作没有了，擦
                            source.put("操保手册发布数量", source.getIntValue("操保手册发布数量") + 1);
                            break;
                        }
                    }
                }
                Statistics.put(dispatchMonth, source);
            }
        }
        // 拼凑返回结果
        for (Map.Entry entry : Statistics.entrySet()) {
            sources.add((JSONObject)entry.getValue());
        }
        JSONObject resultJson = new JSONObject();
        resultJson.put("dimensions", dimensions);
        resultJson.put("source", sources);
        return resultJson;
    }

    // ..exportAtDocAcpRate:出口产品随机文件完成率(新算法)
    public JSONObject getExportAtDocAcpRateData(HttpServletRequest request, HttpServletResponse response,
        String postDataStr) {
        // dataset.dimensions
        List<String> dimensions = new ArrayList<>();
        dimensions.add("月份");
        dimensions.add("月度发运产品总数");
        dimensions.add("实例图册发布数量");
        dimensions.add("操保手册发布数量");
        // dataset.source
        List<JSONObject> sources = new ArrayList<>();
        JSONObject Statistics = new JSONObject(new LinkedHashMap());
        JSONObject source;
        for (int i = 1; i <= 12; i++) {
            source = new JSONObject();
            source.put("月份", i + "月");
            source.put("月度发运产品总数", 0);
            source.put("实例图册发布数量", 0);
            source.put("操保手册发布数量", 0);
            if (i > 9) {
                Statistics.put("" + i, source);
            } else {
                Statistics.put("0" + i, source);
            }
        }
        // 找到符合条件的成品库发运数据--目前已经是过滤到作废状态的
        JSONObject postDataJson = JSONObject.parseObject(postDataStr);
        JSONObject params = new JSONObject();
        params.put("signYear", postDataJson.getString("signYear"));
        List<JSONObject> exportList = exportPartsAtlasDao.queryDispatchFPWList(params);
        Set<String> oaItemsSet2 = new HashSet<>();// 声明一个set，将OA明细里"零件图册"类型的信息放入oaItemsSet<pin>
        Set<String> oaItemsSet1 = new HashSet<>();// 声明一个set，将OA明细里"操保手册"类型的信息放入oaItemsSet<pin>
        if (CacheUtil.containKey("reDispatchInfo") && CacheUtil.getCache("reDispatchInfo") != null) {
            JSONObject reDispatchInfo = (JSONObject)CacheUtil.getCache("reDispatchInfo");
            JSONArray reDispatchInfoItemArr = reDispatchInfo.getJSONArray("items");
            for (Object reDispatchInfoItem : reDispatchInfoItemArr) {
                if (((JSONObject)reDispatchInfoItem).getString("fileType").equalsIgnoreCase("2")) {
                    oaItemsSet2.add(((JSONObject)reDispatchInfoItem).getString("pin"));
                } else if (((JSONObject)reDispatchInfoItem).getString("fileType").equalsIgnoreCase("1")) {
                    oaItemsSet1.add(((JSONObject)reDispatchInfoItem).getString("pin"));
                }
            }
        }
        // 处理每个成品库发运数据
        for (JSONObject export : exportList) {
            String dispatchMonth = export.getString("dispatchTimeFPW").substring(5, 7);
            source = Statistics.getJSONObject(dispatchMonth);// 处理各月份的数据
            if (source != null) {
                // 有一个任务则某月的发运数+1
                source.put("月度发运产品总数", source.getIntValue("月度发运产品总数") + 1);
                if (!oaItemsSet2.contains(export.getString("pin"))) {// 如果OA2型单据里没有对应的pin码，就算实例图册完成
                    source.put("实例图册发布数量", source.getIntValue("实例图册发布数量") + 1);
                }
                if (!oaItemsSet1.contains(export.getString("pin"))) {// 如果OA1型单据里没有对应的pin码，就算操保手册完成
                    source.put("操保手册发布数量", source.getIntValue("操保手册发布数量") + 1);
                }
            }
            Statistics.put(dispatchMonth, source);
        }
        // ..2023年临时数据的补充处理
        if (postDataJson.getString("signYear").equalsIgnoreCase("2023")) {
            this.tempProcess2023(Statistics);
        }
        // 拼凑返回结果
        for (Map.Entry entry : Statistics.entrySet()) {
            sources.add((JSONObject)entry.getValue());
        }
        JSONObject resultJson = new JSONObject();
        resultJson.put("dimensions", dimensions);
        resultJson.put("source", sources);
        return resultJson;
    }

    // ..2023年临时数据的补充处理，以后就去掉了
    private void tempProcess2023(JSONObject Statistics) {
        System.out.println(Statistics);
        Statistics.getJSONObject("01").put("月度发运产品总数", 1288);
        Statistics.getJSONObject("01").put("实例图册发布数量", 1288);
        Statistics.getJSONObject("01").put("操保手册发布数量", 1288);
        Statistics.getJSONObject("02").put("月度发运产品总数", 1745);
        Statistics.getJSONObject("02").put("实例图册发布数量", 1745);
        Statistics.getJSONObject("02").put("操保手册发布数量", 1745);
        Statistics.getJSONObject("03").put("月度发运产品总数", 2005);
        Statistics.getJSONObject("03").put("实例图册发布数量", 2005);
        Statistics.getJSONObject("03").put("操保手册发布数量", 2005);
    }

    // ..exportAtDocReDisRate:出口产品随机文件补发完成率
    // 零件
    public List<JSONObject> getPartsAtlasReDisRateData(HttpServletRequest request, HttpServletResponse response,
        String postDataStr) {
        // 找到所有零件图册出口制作任务
        List<String> notStatus = new ArrayList<>();
        notStatus.add("08ZF");// 作废
        JSONObject param = new JSONObject();
        param.put("notStatus", notStatus);
        List<JSONObject> exportList = exportPartsAtlasDao.queryAtlasTaskList(param);
        // 将有补发时间的零件图册出口任务的车号放入exportSet改为->
        // 将状态为'06ZZWCYZC'和'07YJS'的零件图册出口任务的车号放入exportSet
        Set<String> exportSet = new HashSet<>();
        for (JSONObject export : exportList) {
            // if (StringUtil.isNotEmpty(export.getString("dispatchTime"))) {
            // exportSet.add(export.getString("machineCode"));
            // }
            if (export.getString("taskStatus").equalsIgnoreCase("06ZZWCYZC")
                || export.getString("taskStatus").equalsIgnoreCase("07YJS")) {
                exportSet.add(export.getString("machineCode"));
            }
        }
        // 声明一个map，将OA明细里"零件图册"类型的信息去重，放入oaItemsMap<pin,item>，其实没必要用map，但是为了和操保算法统一，就用了
        HashMap<String, JSONObject> oaItemsMap = new HashMap<>();
        if (CacheUtil.containKey("reDispatchInfo") && CacheUtil.getCache("reDispatchInfo") != null) {
            JSONObject reDispatchInfo = (JSONObject)CacheUtil.getCache("reDispatchInfo");
            JSONArray reDispatchInfoItemArr = reDispatchInfo.getJSONArray("items");
            for (Object reDispatchInfoItem : reDispatchInfoItemArr) {
                if (((JSONObject)reDispatchInfoItem).getString("fileType").equalsIgnoreCase("2")) {
                    oaItemsMap.put(((JSONObject)reDispatchInfoItem).getString("pin"), (JSONObject)reDispatchInfoItem);
                }
            }
        }
        // 统计已补发数量
        Integer reDisOk = 0;
        for (Map.Entry entry : oaItemsMap.entrySet()) {
            if (exportSet.contains(entry.getKey())) {
                reDisOk++;
            }
        }
        // 拼凑返回结果
        Integer total = 0;
        if (CacheUtil.containKey("reDispatchInfo") && CacheUtil.getCache("reDispatchInfo") != null) {
            total = ((JSONObject)CacheUtil.getCache("reDispatchInfo")).getInteger("partsAtlasCount");
        }
        List<JSONObject> resultList = new ArrayList<>();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("value", reDisOk);
        jsonObject.put("name", "已补发");
        jsonObject.put("total", total);
        resultList.add(jsonObject);
        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put("value", total - reDisOk);
        jsonObject2.put("name", "需补发");
        jsonObject2.put("total", total);
        resultList.add(jsonObject2);
        return resultList;
    }

    // 操保
    public List<JSONObject> getMaintManualReDisRateData(HttpServletRequest request, HttpServletResponse response,
        String postDataStr) {
        // 找到可打印的操保手册
        JSONObject param = new JSONObject();
        param.put("manualStatus", "可打印");
        List<JSONObject> maintList = maintenanceManualfileDao.dataListQuery(param);
        // 将找到的操保手册,存唯一<设计型号-销售型号-物料编码>
        Set<String> maintSet = new HashSet<>();
        for (JSONObject maint : maintList) {
            maintSet.add(maint.getString("designModel") + "-" + maint.getString("salesModel") + "-"
                + maint.getString("materialCode"));
        }
        // 声明一个map，将OA明细里"操保手册"类型的信息去重，放入oaItemsMap<pin,item>
        HashMap<String, JSONObject> oaItemsMap = new HashMap<>();
        if (CacheUtil.containKey("reDispatchInfo") && CacheUtil.getCache("reDispatchInfo") != null) {
            JSONObject reDispatchInfo = (JSONObject)CacheUtil.getCache("reDispatchInfo");
            JSONArray reDispatchInfoItemArr = reDispatchInfo.getJSONArray("items");
            for (Object reDispatchInfoItem : reDispatchInfoItemArr) {
                if (((JSONObject)reDispatchInfoItem).getString("fileType").equalsIgnoreCase("1")) {
                    oaItemsMap.put(((JSONObject)reDispatchInfoItem).getString("pin"), (JSONObject)reDispatchInfoItem);
                }
            }
        }
        // 统计已补发数量
        Integer reDisOk = 0;
        for (Map.Entry entry : oaItemsMap.entrySet()) {
            JSONObject item = (JSONObject)entry.getValue();
            if (maintSet.contains(item.getString("designModel") + "-" + item.getString("salesModel") + "-"
                + item.getString("materialCode"))) {
                reDisOk++;
            }
        }
        // 拼凑返回结果
        Integer total = 0;
        if (CacheUtil.containKey("reDispatchInfo") && CacheUtil.getCache("reDispatchInfo") != null) {
            total = ((JSONObject)CacheUtil.getCache("reDispatchInfo")).getInteger("maintManualCount");
        }
        List<JSONObject> resultList = new ArrayList<>();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("value", reDisOk);
        jsonObject.put("name", "已补发");
        jsonObject.put("total", total);
        resultList.add(jsonObject);
        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put("value", total - reDisOk);
        jsonObject2.put("name", "需补发");
        jsonObject2.put("total", total);
        resultList.add(jsonObject2);
        return resultList;
    }

    // ..atDocAcpRate:随机文件完成率
    public JSONObject getAtDocAcpRateData_Obsolete(HttpServletRequest request, HttpServletResponse response,
        String postDataStr) {
        JSONObject postDataJson = JSONObject.parseObject(postDataStr);
        String signYear = postDataJson.getString("signYear");
        // 取缓存里的shipmentGroupByMonthFromGss，里面是接口获取的每月汇总的发运、实例的数量
        JSONObject shipmentGroupByMonthFromGss = (JSONObject)CacheUtil.getCache("shipmentGroupByMonthFromGss");
        // 找到所有的操保手册需求申请，同时形成<"需求单号-销售型号-物料编码-语言",需求申请>map,以备最后进行筛选
        List<String> notStatus = new ArrayList<>();
        notStatus.add("DISCARD_END");
        JSONObject param = new JSONObject();
        param.put("notStatus", notStatus);
        List<Map<String, Object>> maintDemandList = maintenanceManualDemandDao.dataListQuery(param);
        HashMap<String, JSONObject> maintDemandMap = new HashMap<>();
        for (Map<String, Object> maintDemand : maintDemandList) {
            String[] manualLanguages = maintDemand.get("manualLanguage").toString().split(",");
            for (String manualLanguage : manualLanguages) {
                if (StringUtil.isNotEmpty(maintDemand.get("demandListNo").toString())
                    && StringUtil.isNotEmpty(maintDemand.get("salesModel").toString())
                    && StringUtil.isNotEmpty(maintDemand.get("materialCode").toString())) {
                    maintDemandMap.put(
                        maintDemand.get("demandListNo").toString() + "-" + maintDemand.get("salesModel").toString()
                            + "-" + maintDemand.get("materialCode").toString() + "-" + manualLanguage,
                        new JSONObject(maintDemand));
                }
            }
        }
        // 找到所有gss回传的发运时间为signYear的记录。
        param.clear();
        param.put("shipmentTime", signYear);
        List<JSONObject> gssShipCaListQueryList = seGeneralKanBanNewDao.gssShipmentCacheListQuery(param);
        // ..
        // dataset.dimensions
        List<String> dimensions = new ArrayList<>();
        dimensions.add("月份");
        dimensions.add("月度发运产品总数");
        dimensions.add("实例图册发布数量");
        dimensions.add("操保手册发布数量");
        // dataset.source
        List<JSONObject> sources = new ArrayList<>();
        JSONObject Statistics = new JSONObject(new LinkedHashMap());
        JSONObject source;
        for (int i = 1; i <= 12; i++) {
            source = new JSONObject();
            source.put("月份", i + "月");
            String yyyyMM = i > 9 ? signYear + "-" + i : signYear + "-0" + i;
            String mm = i > 9 ? "" + i : "0" + i;
            if (shipmentGroupByMonthFromGss != null && shipmentGroupByMonthFromGss.containsKey(yyyyMM)) {
                source.put("月度发运产品总数", shipmentGroupByMonthFromGss.getJSONObject(yyyyMM).getIntValue("shipmentTotal"));
                source.put("实例图册发布数量", shipmentGroupByMonthFromGss.getJSONObject(yyyyMM).getIntValue("instanceTotal"));
            } else {
                source.put("月度发运产品总数", 0);
                source.put("实例图册发布数量", 0);
            }
            source.put("操保手册发布数量", 0);
            Statistics.put(mm, source);
        }
        // todo 处理出口数据
        // 找到符合条件零件图册出口制作任务。同时初始化一个出口<"PIN码">set,在后面遍历出口数据时进行初始化
        HashSet<String> exportPinSet = new HashSet<>();
        notStatus = new ArrayList<>();
        notStatus.add("08ZF");// 作废
        param.clear();
        param.put("notStatus", notStatus);
        param.put("signYear", signYear);
        List<JSONObject> exportList = exportPartsAtlasDao.queryAtlasTaskList(param);
        // 处理每个出口制作任务的实际业务统计
        for (JSONObject export : exportList) {
            if (StringUtil.isNotEmpty(export.getString("dispatchTime"))
                && StringUtil.isNotEmpty(export.getString("taskStatus"))
                && StringUtil.isNotEmpty(export.getString("languages"))
                && StringUtil.isNotEmpty(export.getString("demandNo"))
                && StringUtil.isNotEmpty(export.getString("saleType"))
                && StringUtil.isNotEmpty(export.getString("matCode"))) {
                // ..
                exportPinSet.add(export.getString("machineCode"));// 初始化一下前面的exportPinSet
                // ..
                String dispatchMonth = export.getString("dispatchTime").substring(5, 7);
                // 处理各月份的数据
                source = Statistics.getJSONObject(dispatchMonth);
                if (source != null) {
                    // 通过其"需求单号-销售型号-物料编码-语言"去操保手册里面去匹配
                    // 没有的算完成，有的业务状态为"打印"或"结束",流程状态"作废"算完成
                    String[] exportLanguages = export.getString("languages").split(",");
                    for (String exportLanguage : exportLanguages) {
                        String toBeFindKey = export.getString("demandNo") + "-" + export.getString("saleType") + "-"
                            + export.getString("matCode") + "-" + exportLanguage;
                        if (maintDemandMap.containsKey(toBeFindKey)) {// 有的业务状态为"打印"或"结束"
                            JSONObject toBeJudgeDemand = maintDemandMap.get(toBeFindKey);
                            if (toBeJudgeDemand.getString("businessStatus").equalsIgnoreCase("E-printing")
                                || toBeJudgeDemand.getString("businessStatus").equalsIgnoreCase("F-close")) {
                                source.put("操保手册发布数量", source.getIntValue("操保手册发布数量") + 1);
                                break;
                            }
                        } else {// 没有的算完成,流程状态"作废"的最初就被过滤掉了,如果没有相同的不作废的记录，自然就算作没有了，擦
                            source.put("操保手册发布数量", source.getIntValue("操保手册发布数量") + 1);
                            break;
                        }
                    }
                }
                Statistics.put(dispatchMonth, source);
            }
        }
        // todo 处理内销数据
        // 找到所有的操保手册，同时初始化一个<物料-销售型号>的set
        List<JSONObject> maintenanceManualfileList = maintenanceManualfileDao.dataListQuery(null);
        Set<String> maintenanceManualfileSet = new HashSet<>();
        for (JSONObject maintenanceManualfile : maintenanceManualfileList) {
            maintenanceManualfileSet.add(
                maintenanceManualfile.getString("materialCode") + "-" + maintenanceManualfile.getString("salesModel"));
        }
        for (JSONObject gssShipCa : gssShipCaListQueryList) {
            if (!exportPinSet.contains(gssShipCa.getString("pin"))) {// 不是出口的
                String shipmentMonth = gssShipCa.getString("shipmentTime").substring(5, 7);
                // 处理各月份的数据
                source = Statistics.getJSONObject(shipmentMonth);
                if (source != null) {
                    if (maintenanceManualfileSet
                        .contains(gssShipCa.getString("material") + "-" + gssShipCa.getString("salesModels"))) {
                        source.put("操保手册发布数量", source.getIntValue("操保手册发布数量") + 1);
                    }
                }
                Statistics.put(shipmentMonth, source);
            }
        }
        // 拼凑返回结果
        for (Map.Entry entry : Statistics.entrySet()) {
            sources.add((JSONObject)entry.getValue());
        }
        JSONObject resultJson = new JSONObject();
        resultJson.put("dimensions", dimensions);
        resultJson.put("source", sources);
        return resultJson;
    }

    public JSONObject getAtDocAcpRateData(HttpServletRequest request, HttpServletResponse response,
        String postDataStr) {
        JSONObject postDataJson = JSONObject.parseObject(postDataStr);
        String signYear = postDataJson.getString("signYear");
        // 取缓存里的shipmentGroupByMonthFromGss，里面是接口获取的每月汇总的发运、实例的数量
        JSONObject shipmentGroupByMonthFromGss = (JSONObject)CacheUtil.getCache("shipmentGroupByMonthFromGss");
        JSONObject param = new JSONObject();
        // 找到所有gss回传的发运时间为signYear的记录。
        param.clear();
        param.put("shipmentTime", signYear);
        List<JSONObject> gssShipCaListQueryList = seGeneralKanBanNewDao.gssShipmentCacheListQuery(param);
        // ..
        // dataset.dimensions
        List<String> dimensions = new ArrayList<>();
        dimensions.add("月份");
        dimensions.add("月度发运产品总数");
        dimensions.add("实例图册发布数量");
        dimensions.add("操保手册发布数量");
        // dataset.source
        List<JSONObject> sources = new ArrayList<>();
        JSONObject Statistics = new JSONObject(new LinkedHashMap());
        JSONObject source;
        for (int i = 1; i <= 12; i++) {
            source = new JSONObject();
            source.put("月份", i + "月");
            String yyyyMM = i > 9 ? signYear + "-" + i : signYear + "-0" + i;
            String mm = i > 9 ? "" + i : "0" + i;
            if (shipmentGroupByMonthFromGss != null && shipmentGroupByMonthFromGss.containsKey(yyyyMM)) {
                source.put("月度发运产品总数", shipmentGroupByMonthFromGss.getJSONObject(yyyyMM).getIntValue("shipmentTotal"));
                source.put("实例图册发布数量", shipmentGroupByMonthFromGss.getJSONObject(yyyyMM).getIntValue("instanceTotal"));
            } else {
                source.put("月度发运产品总数", 0);
                source.put("实例图册发布数量", 0);
            }
            source.put("操保手册发布数量", 0);
            source.put("月度发运产品总数明细数量", 0);
            Statistics.put(mm, source);
        }
        // 找到所有的操保手册，同时初始化一个<物料>的set
        List<JSONObject> maintenanceManualfileList = maintenanceManualfileDao.dataListQuery(null);
        Set<String> maintenanceManualfileSet = new HashSet<>();
        for (JSONObject maintenanceManualfile : maintenanceManualfileList) {
            maintenanceManualfileSet.add(maintenanceManualfile.getString("materialCode"));
        }
        for (JSONObject gssShipCa : gssShipCaListQueryList) {
            String shipmentMonth = gssShipCa.getString("shipmentTime").substring(5, 7);
            // 处理各月份的数据
            source = Statistics.getJSONObject(shipmentMonth);
            if (source != null) {
                if (maintenanceManualfileSet.contains(gssShipCa.getString("material"))) {
                    source.put("操保手册发布数量", source.getIntValue("操保手册发布数量") + 1);
                }
                // ..张婉莹补差用
                source.put("月度发运产品总数明细数量", source.getIntValue("月度发运产品总数明细数量") + 1);
            }
            Statistics.put(shipmentMonth, source);
        }
        // 拼凑返回结果
        for (Map.Entry entry : Statistics.entrySet()) {
            JSONObject jsonObject = (JSONObject)entry.getValue();
            // ..张婉莹补差:将 发运总数 和 明细总数 之间的差异体现到 操保手册发布数量
            int offset = jsonObject.getIntValue("月度发运产品总数") - jsonObject.getIntValue("月度发运产品总数明细数量");
            int maintNum = jsonObject.getIntValue("操保手册发布数量");
            maintNum += offset;
            jsonObject.put("操保手册发布数量", maintNum > 0 ? maintNum : 0);
            sources.add(jsonObject);
        }
        JSONObject resultJson = new JSONObject();
        resultJson.put("dimensions", dimensions);
        resultJson.put("source", sources);
        return resultJson;
    }

    // ..给gss缓存加一个查询页面
    public JsonPageResult<?> gssShipmentCacheListQuery(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        JSONObject params = new JSONObject();
        CommonFuns.getSearchParam(params, request, true);
        List<JSONObject> gssShipCaListQueryList = seGeneralKanBanNewDao.gssShipmentCacheListQuery(params);
        int businessListTotal = seGeneralKanBanNewDao.countGssShipmentCacheListQuery(params);
        List<JSONObject> maintenanceManualfileList = maintenanceManualfileDao.dataListQuery(null);
        Set<String> maintenanceManualfileSet = new HashSet<>();
        for (JSONObject maintenanceManualfile : maintenanceManualfileList) {
            maintenanceManualfileSet.add(maintenanceManualfile.getString("materialCode").trim());
        }
        for (JSONObject oneDispatch : gssShipCaListQueryList) {
            if (oneDispatch.get("CREATE_TIME_") != null) {
                oneDispatch.put("CREATE_TIME_", DateUtil.formatDate(oneDispatch.getDate("CREATE_TIME_"), "yyyy-MM-dd"));
            }
            if (maintenanceManualfileSet.contains(oneDispatch.getString("material").trim())) {
                oneDispatch.put("isOk", "是");
            } else {
                oneDispatch.put("isOk", "否");
            }

        }
        result.setData(gssShipCaListQueryList);
        result.setTotal(businessListTotal);
        return result;
    }

    public void gssShipmentCacheExport(HttpServletRequest request, HttpServletResponse response) {
        // 这个导出不分页
        JSONObject params = new JSONObject();
        CommonFuns.getSearchParam(params, request, false);
        List<JSONObject> gssShipCaListQueryList = seGeneralKanBanNewDao.gssShipmentCacheListQuery(params);
        List<JSONObject> maintenanceManualfileList = maintenanceManualfileDao.dataListQuery(null);
        Set<String> maintenanceManualfileSet = new HashSet<>();
        for (JSONObject maintenanceManualfile : maintenanceManualfileList) {
            maintenanceManualfileSet.add(maintenanceManualfile.getString("materialCode"));
        }
        for (JSONObject oneDispatch : gssShipCaListQueryList) {
            if (maintenanceManualfileSet.contains(oneDispatch.getString("material"))) {
                oneDispatch.put("isOk", "是");
            } else {
                oneDispatch.put("isOk", "否");
            }

        }
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "gss发运数据缓存导出";
        String excelName = nowDate + title;
        String[] fieldNames = {"是否有操保手册", "整机编号", "发运时间", "物料号", "销售型号", "设计型号"};
        String[] fieldCodes = {"isOk", "pin", "shipmentTime", "material", "salesModels", "designModel"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(gssShipCaListQueryList, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }
}
