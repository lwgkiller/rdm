package com.redxun.serviceEngineering.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.excel.ExcelReaderUtil;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.ExcelUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.materielextend.core.service.MaterielService;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.serviceEngineering.core.dao.MaintenanceManualDao;
import com.redxun.serviceEngineering.core.dao.PartsAtlasDao;
import com.redxun.serviceEngineering.core.dao.SparepartsVerificationDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SeGeneralKanBanService {
    private static Logger logger = LoggerFactory.getLogger(SeGeneralKanBanService.class);
    @Autowired
    private PartsAtlasService partsAtlasService;
    @Autowired
    private MaintenanceManualService maintenanceManualService;
    @Autowired
    private MaintenanceManualDao maintenanceManualDao;
    @Autowired
    private StandardvalueService standardvalueService;
    @Autowired
    private SparepartsVerificationService sparepartsVerificationService;
    @Autowired
    private SparepartsVerificationDao sparepartsVerificationDao;

    //..111
    //..零件图册分期计数器
    private void PartsAtlasKanbanCalculate(HashMap<String, Integer> totalMap,
                                           HashMap<String, Integer> alreadyMap,
                                           String timeTag, JSONObject businessJsonObject) {
//        System.out.println(businessJsonObject.getString("id"));
        //已经有某期的就继续计数
        if (totalMap.containsKey(businessJsonObject.getString(timeTag).substring(0, 7))) {
            Integer total = totalMap.get(businessJsonObject.getString(timeTag).substring(0, 7));
            total++;
            totalMap.put(businessJsonObject.getString(timeTag).substring(0, 7), total);
        } else {//没有计1
            totalMap.put(businessJsonObject.getString(timeTag).substring(0, 7), 1);
        }
        //已经有某期的且状态为已发布就继续计数
        if (businessJsonObject.getString("instanceStatus") != null) {
            if (alreadyMap.containsKey(businessJsonObject.getString(timeTag).substring(0, 7))
                    && businessJsonObject.getString("instanceStatus").equalsIgnoreCase("已发布")) {
                Integer already = alreadyMap.get(businessJsonObject.getString(timeTag).substring(0, 7));
                already++;
                alreadyMap.put(businessJsonObject.getString(timeTag).substring(0, 7), already);
            } else if (businessJsonObject.getString("instanceStatus").equalsIgnoreCase("已发布")) {//没有计1
                alreadyMap.put(businessJsonObject.getString(timeTag).substring(0, 7), 1);
            }
        }
    }

    //..获取零件图册看板数据
    public List<JSONObject> getPartsAtlasKanbanData(HttpServletRequest request, HttpServletResponse response, String postDataStr) {
        JSONObject postDataJson = JSONObject.parseObject(postDataStr);
        Map<String, Object> params = new HashMap<>();
        params.put("yearMonthBegin", postDataJson.getString("yearMonthBegin") + "-00");
        params.put("yearMonthEnd", postDataJson.getString("yearMonthEnd") + "-31");
        params.put("action", postDataJson.getString("action"));
        List<JSONObject> partsAtlasStorageDatas = partsAtlasService.getListByActionAndDaterange(params);
        LinkedHashMap<String, Integer> totalMap = new LinkedHashMap<>();
        LinkedHashMap<String, Integer> alreadyMap = new LinkedHashMap<>();
        for (JSONObject jsonObject : partsAtlasStorageDatas) {
            //入库
            if (params.get("action").equals("storage")) {
                PartsAtlasKanbanCalculate(totalMap, alreadyMap, "storageTime", jsonObject);
            }//发运
            else if (params.get("action").equals("shipment")) {
                PartsAtlasKanbanCalculate(totalMap, alreadyMap, "shipmentTime", jsonObject);
            }
        }
        //下面进行图表json的拼装
        List<JSONObject> resultDataList = new ArrayList<>();
        Iterator<String> iterator = totalMap.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("日期", key);
            jsonObject.put("月度总数", totalMap.get(key));
            jsonObject.put("已发布数", alreadyMap.containsKey(key) ? alreadyMap.get(key) : 0);
            resultDataList.add(jsonObject);
        }
        return resultDataList;
    }

    //..获取机型零件图册制作总数，此处重复定义业务service同名方法，解耦
    public Integer getPartsAtlasModelTotal() {
        return partsAtlasService.getPartsAtlasModelTotal();
    }

    //..实例零件图册制作总数，此处重复定义业务service同名方法，解耦
    public Integer getPartsAtlasInstanceTotal() {
        return partsAtlasService.getPartsAtlasInstanceTotal();
    }

    //..333
    //..操保手册分期计数器
    private void maintenanceManualKanbanCalculate(HashMap<String, Integer> totalMap,
                                                  HashMap<String, Integer> alreadyMap,
                                                  JSONObject businessJsonObject,
                                                  HashMap<String, String> businessMap) {
        //已经有某期的就继续计数
        if (totalMap.containsKey(businessJsonObject.getString("shipmentTime").substring(0, 7))) {
            Integer total = totalMap.get(businessJsonObject.getString("shipmentTime").substring(0, 7));
            total++;
            totalMap.put(businessJsonObject.getString("shipmentTime").substring(0, 7), total);
        } else {//没有计1
            totalMap.put(businessJsonObject.getString("shipmentTime").substring(0, 7), 1);
        }
        //已经有某期的且操保手册map里面有物料key的就继续计数
        if (alreadyMap.containsKey(businessJsonObject.getString("shipmentTime").substring(0, 7))
                && businessMap.containsKey(businessJsonObject.getString("materialCode"))) {
            Integer already = alreadyMap.get(businessJsonObject.getString("shipmentTime").substring(0, 7));
            already++;
            alreadyMap.put(businessJsonObject.getString("shipmentTime").substring(0, 7), already);
        } else if (businessMap.containsKey(businessJsonObject.getString("materialCode"))) {//没有计1
            alreadyMap.put(businessJsonObject.getString("shipmentTime").substring(0, 7), 1);
        }
    }

    //..操保手册看板数据
    public List<JSONObject> getMaintenanceManualKanbanData(HttpServletRequest request, HttpServletResponse response, String postDataStr) {
        JSONObject postDataJson = JSONObject.parseObject(postDataStr);
        Map<String, Object> params = new HashMap<>();
        params.put("yearMonthBegin", postDataJson.getString("signYearMaintenanceManual") + "-01-00");
        params.put("yearMonthEnd", postDataJson.getString("signYearMaintenanceManual") + "-12-31");
        params.put("action", "shipment");
        List<JSONObject> partsAtlasDatas = partsAtlasService.getListByActionAndDaterange(params);
        params.clear();
        List<JSONObject> maintenanceManualDatas = maintenanceManualDao.dataListQuery(params);
        HashMap<String, String> maintenanceManualMap = new HashMap<>();
        //只要是否切换打印为是就代表做手册了
        for (JSONObject jsonObject : maintenanceManualDatas) {
            if (jsonObject.getString("isPrint").equalsIgnoreCase("是")) {
                maintenanceManualMap.put(jsonObject.getString("materialCode"),
                        jsonObject.getString("manualCode"));
            }
        }
        LinkedHashMap<String, Integer> totalMap = new LinkedHashMap<>();
        LinkedHashMap<String, Integer> alreadyMap = new LinkedHashMap<>();
        for (JSONObject jsonObject : partsAtlasDatas) {
            maintenanceManualKanbanCalculate(totalMap, alreadyMap, jsonObject, maintenanceManualMap);
        }
        //下面进行图表json的拼装
        List<JSONObject> resultDataList = new ArrayList<>();
        Iterator<String> iterator = totalMap.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("日期", key);
            jsonObject.put("发运产品数量", totalMap.get(key));
            jsonObject.put("结构化操保手册数量", alreadyMap.containsKey(key) ? alreadyMap.get(key) : 0);
            resultDataList.add(jsonObject);
        }
        return resultDataList;
    }

    //..操保手册制作总数，此处重复定义业务service同名方法，解耦
    public Integer getMaintenanceManualTotal() {
        return maintenanceManualService.getMaintenanceManualTotal();
    }

    //..666
    //..测试版制作总数，此处重复定义业务service同名方法，解耦
    public Integer getStandardvalueBetaTotal(String postDataStr) {
        return standardvalueService.getStandardvalueBetaTotal(postDataStr);
    }

    //..常规版制作总数，此处重复定义业务service同名方法，解耦
    public Integer getStandardvalueRoutineTotal(String postDataStr) {
        return standardvalueService.getStandardvalueRoutineTotal(postDataStr);
    }

    //..完整版制作总数，此处重复定义业务service同名方法，解耦
    public Integer getStandardvalueCompleteTotal(String postDataStr) {
        return standardvalueService.getStandardvalueCompleteTotal(postDataStr);
    }

    //..检修标准值看板数据
    public JSONObject getStandardvalueKanbanData(HttpServletRequest request, HttpServletResponse response,
                                                 String postDataStr) {
        JSONObject resultJson = new JSONObject();
        resultJson.put("qa", standardvalueService.getStandardvalueQa(postDataStr));
        resultJson.put("test", standardvalueService.getStandardvalueTest(postDataStr));
        resultJson.put("qaActual", standardvalueService.getStandardvalueQaActual(postDataStr));
        resultJson.put("testActual", standardvalueService.getStandardvalueTestActual(postDataStr));
        return resultJson;
    }

    //..555
    //..某年备件核查总数，此处重复定义业务service同名方法，解耦
    public Integer getSparepartsVerificationTotal(String postDataStr) {
        return sparepartsVerificationService.getSparepartsVerificationTotal(postDataStr);
    }

    //..备件核查看板数据
    public JSONObject getSparepartsVerificationKanbanData(HttpServletRequest request, HttpServletResponse response,
                                                          String postDataStr) {
        JSONObject postDataJson = JSONObject.parseObject(postDataStr);
        postDataJson.put("sortField", "signYear asc,signMonth asc,signWeek");
        postDataJson.put("sortOrder", "asc");
        List<JSONObject> businessList = sparepartsVerificationDao.dataListQuery(postDataJson);
        //业务列表按周存储，取某年的业务列表，将月份去重排列形成月份列表，简单映射lambda实现，尝试一下
        List<String> monthList = businessList.stream()
                .map(jsonObject -> jsonObject.getString("signMonth"))
                .distinct()
                .collect(Collectors.toList());
        //业务列表按周存储，取某年的业务列表，将每周的值按照月份汇总形成数量列表。
        //todo:@lwgkiller:复杂映射尝试用lambda实现一下，如下：
        //按月分组，保留原有顺序
        LinkedHashMap<String, List<JSONObject>> map = businessList.stream()
                .collect(Collectors.groupingBy(jsonObject -> jsonObject.getString("signMonth"),
                        LinkedHashMap::new, Collectors.toList()));
        List<String> countListMock = new ArrayList<>();
        //遍历分组，进行统计汇总
        map.forEach((key, value) -> {
            IntSummaryStatistics stats = value.stream()
                    .mapToInt((jsonObject) -> jsonObject.getInteger("verificationAmount")).summaryStatistics();
            countListMock.add(String.valueOf(stats.getSum()));
        });
        //todo:@lwgkiller:如上已经实现了。优点：代码简洁，模板代码少，字面意义更能表达业务意图。缺点：晦涩，团队约定不统一的情况下会给同伴带来困扰，性能较低。
        ////以下常规实现，有点繁琐。
        Map<String, Integer> countMap = new LinkedHashMap<>();
        for (JSONObject jsonObject : businessList) {
            if (countMap.get(jsonObject.getString("signMonth")) != null) {
                countMap.put(jsonObject.getString("signMonth"),
                        countMap.get(jsonObject.getString("signMonth")) +
                                jsonObject.getInteger("verificationAmount"));
            } else {
                countMap.put(jsonObject.getString("signMonth"),
                        jsonObject.getInteger("verificationAmount"));
            }
        }
        List<String> countList = new ArrayList<>();
        Iterator iterator = countMap.keySet().iterator();
        while (iterator.hasNext()) {
            Integer integer = countMap.get(iterator.next());
            countList.add(integer.toString());
        }
        ////
        JSONObject resultJson = new JSONObject();
        resultJson.put("month", monthList);
        resultJson.put("count", countList);
        return resultJson;
    }
}
