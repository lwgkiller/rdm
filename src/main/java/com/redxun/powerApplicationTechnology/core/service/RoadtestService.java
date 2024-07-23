package com.redxun.powerApplicationTechnology.core.service;

import java.io.File;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.EncryptUtil;
import com.redxun.core.util.HttpClientUtil;
import com.redxun.powerApplicationTechnology.core.dao.RoadtestDao;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;

@Service
public class RoadtestService {
    private static Logger logger = LoggerFactory.getLogger(RoadtestService.class);
    @Autowired
    private RoadtestDao roadtestDao;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;
    @Autowired
    private SysDicManager sysDicManager;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private BpmInstManager bpmInstManager;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;

    //..
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        getListParams(params, request);
        List<JSONObject> businessList = roadtestDao.dataListQuery(params);
        int countbusinessList = roadtestDao.countDataListQuery(params);
        result.setData(businessList);
        result.setTotal(countbusinessList);
        return result;
    }

    //..
    private void getListParams(Map<String, Object> params, HttpServletRequest request) {
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)) {
            params.put("sortField", sortField);
            params.put("sortOrder", sortOrder);
        }
        if (StringUtils.isEmpty(sortField)) {
            params.put("sortField", "CREATE_TIME_");
            params.put("sortOrder", "asc");
        }
        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    // 数据库中存储的时间是UTC时间，因此需要将前台传递的北京时间转化
//                    if ("communicateStartTime".equalsIgnoreCase(name)) {
//                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), -8));
//                    }
//                    if ("communicateEndTime".equalsIgnoreCase(name)) {
//                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), 16));
//                    }
                    params.put(name, value);
                }
            }
        }

        // 增加分页条件
        params.put("startIndex", 0);
        params.put("pageSize", 20);
        String pageIndex = request.getParameter("pageIndex");
        String pageSize = request.getParameter("pageSize");
        if (StringUtils.isNotBlank(pageIndex) && StringUtils.isNotBlank(pageSize)) {
            params.put("startIndex", Integer.parseInt(pageSize) * Integer.parseInt(pageIndex));
            params.put("pageSize", Integer.parseInt(pageSize));
        }
    }

    //..
    public JsonResult deleteBusiness(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        //主业务id
        List<String> businessIds = Arrays.asList(ids);
        //日工作信息
        List<String> dailyIds = new ArrayList<>();
        //初始化日工作信息id
        for (String businessId : businessIds) {
            dailyIds.addAll(roadtestDao.getDailyIdList(businessId));
        }
        //测试数据
        List<String> testdataIds = new ArrayList<>();
        //初始化测试数据id
        for (String businessId : businessIds) {
            testdataIds.addAll(roadtestDao.getTestdataIdList(businessId));
        }
        //测试数据附件
        List<JSONObject> testdataFiles = new ArrayList<>();
        //初始化测试数据附件
        if (testdataIds.size() > 0) {
            testdataFiles = getTestdataFileList(testdataIds);
        }
        //统一存放位置
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "powerApplicationTechnologyUploadPosition", "Roadtest").getValue();
        //测试数据附件物理逐删
        for (JSONObject oneFile : testdataFiles) {
            rdmZhglFileManager.deleteOneFileFromDisk(oneFile.getString("id"),
                    oneFile.getString("fileName"), oneFile.getString("mainId"), filePathBase);
        }
        //测试数据附件目录逐删
        for (String oneTestdataId : testdataIds) {
            rdmZhglFileManager.deleteDirFromDisk(oneTestdataId, filePathBase);
        }
        Map<String, Object> param = new HashMap<>();
        param.put("businessIds", businessIds);
        //删主业务信息
        roadtestDao.deleteBusiness(param);
        //删日工作信息
        roadtestDao.deleteDaily(param);
        //删测试数据
        roadtestDao.deleteTestdata(param);

        param.clear();
        if (testdataIds.size() > 0) {
            param.put("testdataIds", testdataIds);
        }
        //删测试数据附件信息
        roadtestDao.deleteTestdataFileinfo(param);
        return result;
    }

    //..
    public List<JSONObject> getTestdataFileList(List<String> testdataIdList) {
        List<JSONObject> businessFileList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("mainIds", testdataIdList);
        businessFileList = roadtestDao.getTestdataFileList(param);
        return businessFileList;
    }

    //..
    public JsonResult startBusiness(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        //主业务id
        List<String> businessIds = Arrays.asList(ids);
        Map<String, Object> param = new HashMap<>();
        param.put("businessIds", businessIds);
        //开启
        roadtestDao.startBusiness(param);
        return result;
    }

    //..
    public JsonResult closeBusiness(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        //主业务id
        List<String> businessIds = Arrays.asList(ids);
        Map<String, Object> param = new HashMap<>();
        param.put("businessIds", businessIds);
        //开启
        roadtestDao.closeBusiness(param);
        return result;
    }

    //..
    public List<JSONObject> getDailyList(JSONObject params) {
        List<JSONObject> dailyList = roadtestDao.getDailyList(params);
        return dailyList;
    }

    //..
    public List<JSONObject> getTestdataList(String businessId) {
        List<JSONObject> testDataList = roadtestDao.getTestdataList(businessId);
        return testDataList;
    }

    //..
    public JSONObject getDetail(String businessId) {
        JSONObject jsonObject = roadtestDao.getDetailById(businessId);
        if (jsonObject == null) {
            return new JSONObject();
        }
        return jsonObject;
    }

    //..
    public void createBusiness(JSONObject formData) {
        formData.put("id", IdUtil.getId());
        //不要求用户填写的自充字段要在新增时进行初始化
        formData.put("isClose", "否");
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        roadtestDao.insertBusiness(formData);
    }

    //..
    public void updateBusiness(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        roadtestDao.updateBusiness(formData);
    }

    //..
    public void saveDaily(JsonResult result, String dataStr) {
        JSONArray dataStrObjs = JSONObject.parseArray(dataStr);
        if (dataStrObjs == null || dataStrObjs.isEmpty()) {
            result.setSuccess(false);
            result.setMessage("表单内容为空，操作失败！");
            return;
        }
        for (Object object : dataStrObjs) {
            JSONObject dataObj = (JSONObject) object;
            if (StringUtils.isBlank(dataObj.getString("id"))) {
                dataObj.put("id", IdUtil.getId());
                dataObj.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                dataObj.put("CREATE_TIME_", new Date());
                roadtestDao.insertDaily(dataObj);
            } else {
                dataObj.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                dataObj.put("UPDATE_TIME_", new Date());
                roadtestDao.updateDaily(dataObj);
            }
        }
    }

    //..
    public JsonResult deleteTestdata(String id) {
        JsonResult result = new JsonResult(true, "删除成功！");
        List<String> testdataIds = new ArrayList<>();
        testdataIds.add(id);
        List<JSONObject> testdataFiles = getTestdataFileList(testdataIds);
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "powerApplicationTechnologyUploadPosition", "Roadtest").getValue();
        for (JSONObject oneFile : testdataFiles) {
            rdmZhglFileManager.deleteOneFileFromDisk(oneFile.getString("id"),
                    oneFile.getString("fileName"), oneFile.getString("mainId"), filePathBase);
        }
        for (String oneTestdataId : testdataIds) {
            rdmZhglFileManager.deleteDirFromDisk(oneTestdataId, filePathBase);
        }
        Map<String, Object> param = new HashMap<>();
        param.put("id", id);
        roadtestDao.deleteTestdata(param);

        param.clear();
        param.put("testdataIds", testdataIds);
        roadtestDao.deleteTestdataFileinfo(param);
        return result;
    }

    //..
    public JSONObject getTestdataDetail(String businessId) {
        JSONObject jsonObject = roadtestDao.getTestdataDetailById(businessId);
        if (jsonObject == null) {
            return new JSONObject();
        }
        return jsonObject;
    }

    //..
    public void saveTestdata(JsonResult result, String dataStr) {
        JSONObject dataStrObj = JSONObject.parseObject(dataStr);
        if (dataStrObj == null || dataStrObj.isEmpty()) {
            result.setSuccess(false);
            result.setMessage("表单内容为空，操作失败！");
            return;
        }
        if (StringUtils.isBlank(dataStrObj.getString("id"))) {
            dataStrObj.put("id", IdUtil.getId());
            dataStrObj.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            dataStrObj.put("CREATE_TIME_", new Date());
            roadtestDao.insertTestdata(dataStrObj);
        } else {
            dataStrObj.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            dataStrObj.put("UPDATE_TIME_", new Date());
            roadtestDao.updateTestdata(dataStrObj);
        }
        result.setData(dataStrObj.getString("id"));
    }

    //..
    public void delTestdataFile(String fileId, String fileName, String mainId) {
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "powerApplicationTechnologyUploadPosition", "Roadtest").getValue();
        rdmZhglFileManager.deleteOneFileFromDisk(fileId, fileName, mainId, filePathBase);
        Map<String, Object> param = new HashMap<>();
        param.put("id", fileId);
        roadtestDao.deleteTestdataFileinfo(param);
    }

    //..
    public void saveTestdataUploadFiles(HttpServletRequest request) {
        Map<String, String[]> parameters = request.getParameterMap();
        if (parameters == null || parameters.isEmpty()) {
            logger.warn("没有找到上传的参数");
            return;
        }
        // 多附件上传需要用到的MultipartHttpServletRequest
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        if (fileMap == null || fileMap.isEmpty()) {
            logger.warn("没有找到上传的文件");
            return;
        }
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "powerApplicationTechnologyUploadPosition", "Roadtest").getValue();
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find FilePathBase");
            return;
        }
        try {
            String mainId = toGetParamVal(parameters.get("mainId"));
            String id = IdUtil.getId();
            String fileName = toGetParamVal(parameters.get("fileName"));
            String fileSize = toGetParamVal(parameters.get("fileSize"));
            String fileDesc = toGetParamVal(parameters.get("fileDesc"));
            String fileType = toGetParamVal(parameters.get("fileType"));

            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();

            // 向下载目录中写入文件
            String filePath = filePathBase + File.separator + mainId;
            File pathFile = new File(filePath);
            if (!pathFile.exists()) {
                pathFile.mkdirs();
            }
            String suffix = CommonFuns.toGetFileSuffix(fileName);
            String fileFullPath = filePath + File.separator + id + "." + suffix;
            File file = new File(fileFullPath);
            FileCopyUtils.copy(mf.getBytes(), file);

            // 写入数据库
            JSONObject fileInfo = new JSONObject();
            fileInfo.put("id", id);
            fileInfo.put("fileName", fileName);
            fileInfo.put("fileSize", fileSize);
            fileInfo.put("mainId", mainId);
            fileInfo.put("fileDesc", fileDesc);
            fileInfo.put("fileType", fileType);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            roadtestDao.addTestdataFileInfos(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
    }

    //..
    private String toGetParamVal(String[] paramValArr) {
        if (paramValArr == null || paramValArr.length == 0) {
            return null;
        }
        return paramValArr[0];
    }

    //..同步功能
    public JsonResult synchGPS(String id) {
        JsonResult result = new JsonResult(true, "同步成功！");
        JSONObject roadtest = roadtestDao.getDetailById(id);
        String pin = roadtest.getString("pin");
        String roadtestId = roadtest.getString("id");
        try {
            JSONObject gpsMachineJson = this.getGPSMachineJsonByPin(pin);
            if (gpsMachineJson.getString("isok").equalsIgnoreCase("1")) {
                //--处理GPS缓存
                JSONObject gpsCache = this.buildGPSCacheByGPSMachineJson(gpsMachineJson, roadtestId);
                JSONObject params = new JSONObject();
                params.put("businessId", gpsCache.getString("mainId"));
                params.put("cacheDate", gpsCache.getString("cacheDate"));
                List<JSONObject> gpscacheList = roadtestDao.getGpscacheList(params);
                if (gpscacheList.isEmpty()) {
                    gpsCache.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    gpsCache.put("CREATE_TIME_", new Date());
                    roadtestDao.insertGpscache(gpsCache);
                } else {
                    gpsCache.put("id", gpscacheList.get(0).getString("id"));
                    gpsCache.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    gpsCache.put("UPDATE_TIME_", new Date());
                    roadtestDao.updateGpscache(gpsCache);
                }
                //--处理日工作信息
                JSONObject daily = this.buildDailyByGPSCache(gpsCache, roadtestId);
                params.clear();
                params.put("businessId", daily.getString("mainId"));
                params.put("theDate", daily.getString("theDate"));
                List<JSONObject> dailyList = roadtestDao.getDailyList(params);
                if (dailyList.isEmpty()) {
                    daily.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    daily.put("CREATE_TIME_", new Date());
                    roadtestDao.insertDaily(daily);
                } else {
                    daily.put("id", dailyList.get(0).getString("id"));
                    daily.put("crmStatus", dailyList.get(0).getString("crmStatus"));
                    daily.put("crmStatusNeg", dailyList.get(0).getString("crmStatusNeg"));
                    daily.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    daily.put("UPDATE_TIME_", new Date());
                    roadtestDao.updateDaily(daily);
                }
            }
            result.setData(id);
            return result;
        } catch (Exception E) {
            logger.error(pin + " 车调用GPS接口发生致命错误");
            result.setData(id);
            result.setMessage(pin + " 车调用GPS接口发生致命错误,数据将不进行任何同步");
            return result;
        }
    }

    //..todo:同步功能2，通过单日数据接口同步，直接同步daily数据，不需要gpscache辅助，只能同步前一天的数据，测试未通过
    public JsonResult synchGPS2(String id) throws Exception {
        JsonResult result = new JsonResult(true, "同步成功！");
        JSONObject roadtest = roadtestDao.getDetailById(id);
        String pin = roadtest.getString("pin");
        String roadtestId = roadtest.getString("id");
        JSONObject gpsMachineJson = this.getGPSMachineJsonByPin2(pin);
        result.setData(id);
        return result;
    }

    //..通过pin码，获取整机GPS信息
    private JSONObject getGPSMachineJsonByPin(String pin) {
        try {
            String IotUrl = "http://front.prod.iot.hanyunmmip.cn/business/external/WorkCondition?" +
                    "vincode=%s&companyId=%s&appId=%s&signTime=%s&sign=%s";
            String vinCode = pin;
            String companyId = "1237979465155112961";
            String appId = "wjcrm";
            String appSecret = "C67844291B7C4D4DABC6CB6866B73BD6";
            String signTime = String.valueOf(System.currentTimeMillis());
            String param = String.format("appId=%s&companyId=%s&signTime=%s&vincode=%s", appId, companyId, signTime, vinCode);
            String singA = param + "&appSecret=" + appSecret;
            String sing = EncryptUtil.encryptMd5(singA);
            String url = String.format(IotUrl, vinCode, companyId, appId, signTime, sing);
            String resp = HttpClientUtil.getFromUrl(url, null);
            JSONObject jsonObject = JSON.parseObject(resp);
            return jsonObject;
        } catch (Exception E) {
            logger.error(pin + " 车调用GPS接口发生致命错误");
            return null;
        }
    }

    //..todo:通过pin码，获取单日整机GPS信息，信息没有即时的全，但包含明确的单日油耗工时，且只能获取前一天的数据，测试未通过
    private JSONObject getGPSMachineJsonByPin2(String pin) throws Exception {
        pin = "XUGF035UVLKA00832";
        String IotUrl = "http://front.prod.iot.hanyunmmip.cn/business/external/GetVehicleWorkTimeAndOil?" +
                "vinCode=%s&startTime=%s&endTime=%s&appId=%s&signTime=%s&sign=%s&companyId=%s";
        String startTime = DateUtil.formatDate(DateUtil.addDay(new Date(), -2), DateUtil.DATE_FORMAT_YMD);
        String endTime = DateUtil.formatDate(DateUtil.addDay(new Date(), -1), DateUtil.DATE_FORMAT_YMD);
        String vinCode = pin;
        String companyId = "1237979465155112961";
        String appId = "wjcrm";
        String appSecret = "C67844291B7C4D4DABC6CB6866B73BD6";
        String signTime = String.valueOf(System.currentTimeMillis());
        String param = String.format("appId=%s&companyId=%s&endTime=%s&signTime=%s&startTime=%s&vinCode=%s",
                appId, companyId, endTime, signTime, startTime, vinCode);
        String singA = param + "&appSecret=" + appSecret;
        String sing = EncryptUtil.encryptMd5(singA);
        String url = String.format(IotUrl, vinCode, startTime, endTime, appId, signTime, sing, companyId);
        String resp = HttpClientUtil.getFromUrl(url, null);
        JSONObject jsonObject = JSON.parseObject(resp);
        return jsonObject;
    }

    //..通过整机GPS信息，生成GPSCache的jsonObject
    private JSONObject buildGPSCacheByGPSMachineJson(JSONObject gpsMachineJson, String businessId) {
        JSONObject gpsCache = new JSONObject();
        JSONArray gpsMachineJsonDatas = gpsMachineJson.getJSONArray("data");
        JSONObject gpsMachineJsonData = gpsMachineJsonDatas.getJSONObject(0);
        gpsCache.put("id", IdUtil.getId());
        gpsCache.put("mainId", businessId);
        gpsCache.put("vinCode", gpsMachineJsonData.getString("vinCode"));
        gpsCache.put("lng", gpsMachineJsonData.getString("lng"));
        gpsCache.put("lat", gpsMachineJsonData.getString("lat"));
        gpsCache.put("gpsTime", gpsMachineJsonData.getString("gpsTime"));
        gpsCache.put("accStatus", gpsMachineJsonData.getString("accStatus"));
        gpsCache.put("totalEngineHours", gpsMachineJsonData.getString("totalEngineHours"));
        gpsCache.put("engineWaterTemperature", gpsMachineJsonData.getString("Enginewatertemperature").split("\\(")[0]);
        gpsCache.put("fuelLevel", gpsMachineJsonData.getString("FuelLevel").split("\\(")[0]);
        gpsCache.put("hydraulicOilTemperature", gpsMachineJsonData.getString("Hydraulicoiltemperature").split("\\(")[0]);
        gpsCache.put("altitude", gpsMachineJsonData.getString("altitude").split("\\(")[0]);
        gpsCache.put("enginespeed", gpsMachineJsonData.getString("enginespeed").split("\\(")[0]);
        gpsCache.put("totalEngineFuel", gpsMachineJsonData.getString("TotalEngineFuel").split("\\(")[0]);
        gpsCache.put("currentWorkTime", gpsMachineJsonData.getString("currentWorkTime"));
        gpsCache.put("throttleGear", gpsMachineJsonData.getString("Throttlegear"));
        gpsCache.put("alarmList", gpsMachineJsonData.getString("alarmList"));
        gpsCache.put("cacheDate", DateUtil.formatDate(gpsMachineJsonData.getDate("gpsTime"), DateUtil.DATE_FORMAT_YMD));
        return gpsCache;
    }

    //..通过GPSCache和路试项目主id，生成Daily的jsonObject
    private JSONObject buildDailyByGPSCache(JSONObject gpsCache, String mainId) {
        JSONObject daily = new JSONObject();
        daily.put("id", IdUtil.getId());
        daily.put("mainId", mainId);
        daily.put("theDate", gpsCache.getString("cacheDate"));
        return daily;
    }

    //..计算功能
    public JsonResult calculateBusiness(String[] ids) throws ParseException {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> roadtestIds = Arrays.asList(ids);
        //时间复杂度O(n)
        for (String roadtestId : roadtestIds) {
            //计算当日油耗和工作时间
            calculateDailyFuelConsumptionAndWorkingHours(roadtestId);
            //计算路试数据本身的一些综合指标
            calculateRoadtest(roadtestId);
        }
        return result;
    }

    //..计算当日油耗和工作时间
    private void calculateDailyFuelConsumptionAndWorkingHours(String roadtestId) {
        JSONObject params = new JSONObject();
        params.put("businessId", roadtestId);
        List<JSONObject> dailys = roadtestDao.getDailyList(params);
        List<JSONObject> gpscaches = roadtestDao.getGpscacheList(params);
        //油耗
        calculateDailyFuelConsumptionByGPSCache(dailys, gpscaches);
        //时间
        calculateDailyWorkingHoursByGPSCache(dailys, gpscaches);
        //O(n*m)下有update操作，当心效率问题。目前n量级大概在10级，m量级大概在100级
        //会造成数量级1000左右的update操作,合并到此处
        for (int i = 0; i < dailys.size() - 1; i++) {//注意下标-1，倒序列表，最下面一个最早的数据不会被计算
            roadtestDao.updateDaily(dailys.get(i));
        }
    }

    //..递推当日油耗，如果GPS能提供，则此过程省略
    private void calculateDailyFuelConsumptionByGPSCache(List<JSONObject> dailys, List<JSONObject> gpscaches) {
        //时间复杂度O(n*m)
        for (int i = 0; i < dailys.size() - 1; i++) {
            JSONObject currentDaily = dailys.get(i);
            JSONObject currentGpscaches = gpscaches.get(i);
            JSONObject priGpscaches = gpscaches.get(i + 1);
            currentDaily.put("dailyFuelConsumption",
                    new BigDecimal(currentGpscaches.getDoubleValue("totalEngineFuel") - priGpscaches.getDoubleValue("totalEngineFuel"))
                            .setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()
            );
            //O(n*m)下有update操作，当心效率问题。目前n量级大概在10级，m量级大概在100级
            //会造成数量级1000左右的update操作,合并到上层
            //roadtestDao.updateDaily(currentDaily);
        }
    }

    //..递推当日工作时间，如果GPS能提供，则此过程省略
    private void calculateDailyWorkingHoursByGPSCache(List<JSONObject> dailys, List<JSONObject> gpscaches) {
        //时间复杂度O(n*m)
        for (int i = 0; i < dailys.size() - 1; i++) {
            JSONObject currentDaily = dailys.get(i);
            JSONObject currentGpscaches = gpscaches.get(i);
            JSONObject priGpscaches = gpscaches.get(i + 1);
            currentDaily.put("dailyWorkingHours",
                    new BigDecimal(currentGpscaches.getDoubleValue("totalEngineHours") - priGpscaches.getDoubleValue("totalEngineHours"))
                            .setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()
            );

            //O(n*m)下有update操作，当心效率问题。目前n量级大概在10级，m量级大概在100级
            //会造成数量级1000左右的update操作,合并到上层
            //roadtestDao.updateDaily(currentDaily);
        }
    }

    //..计算路试数据本身的一些综合指标
    private void calculateRoadtest(String roadtestId) throws ParseException {
        JSONObject roadtest = roadtestDao.getDetailById(roadtestId);
        //总路试计划天数
        int totalCount = DateUtil.daysBetween(roadtest.getDate("roadtestBeginDate"),
                roadtest.getDate("roadtestEndDate")) + 1;
        //还剩的的路试计划天数
//        int yetCount = DateUtil.daysBetween(new Date(), roadtest.getDate("roadtestEndDate")) + 1 < 0 ? 0 :
//                DateUtil.daysBetween(new Date(), roadtest.getDate("roadtestEndDate")) + 1;
        int yetCount = DateUtil.daysBetween(new Date(), roadtest.getDate("roadtestEndDate")) < 0 ? 0 :
                DateUtil.daysBetween(new Date(), roadtest.getDate("roadtestEndDate"));
        //已进行的路试天数
        int alreadyCount = totalCount - yetCount;
        //取路试周期内的日工作信息
        JSONObject params = new JSONObject();
        params.put("businessId", roadtestId);
        params.put("beginDate", roadtest.getString("roadtestBeginDate"));
        params.put("endDate", roadtest.getString("roadtestEndDate"));
        List<JSONObject> dailys = roadtestDao.getDailyList(params);
        //累计工作小时数
        double cumulativeWorkingHours = 0;
        //累计油耗量
        double cumulativeFuelConsumption = 0;
        for (JSONObject daily : dailys) {
            //累计工作小时数
            cumulativeWorkingHours = new BigDecimal(cumulativeWorkingHours + daily.getDoubleValue("dailyWorkingHours")).
                    setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            //累计油耗量
            cumulativeFuelConsumption = new BigDecimal(cumulativeFuelConsumption += daily.getDoubleValue("dailyFuelConsumption")).
                    setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        }
        //日均工作小时(实际)=累计工作小时数/已经进行的路试计划天数
        double actualDailyAverageWorkingHours = alreadyCount == 0 ? 0 :
                new BigDecimal(cumulativeWorkingHours / alreadyCount).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        //剩余天数预计日均工作小时(目标)=((日均小时目标值*总路试天数)-累计工作小时数)/剩余路试天数
        double targetDailyAverageWorkingHoursRemaining = yetCount == 0 ? 0 :
                new BigDecimal(((roadtest.getDouble("targetDailyAverageWorkingHours") * totalCount) - cumulativeWorkingHours) / yetCount).
                        setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        //每小时油耗(L)=累计油耗(L)/累计工作小时数
        double perhourFuelConsumption = cumulativeWorkingHours == 0 ? 0 :
                new BigDecimal(cumulativeFuelConsumption / cumulativeWorkingHours).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        roadtest.put("targetDailyAverageWorkingHoursRemaining", targetDailyAverageWorkingHoursRemaining);
        roadtest.put("actualDailyAverageWorkingHours", actualDailyAverageWorkingHours);
        roadtest.put("cumulativeWorkingHours", cumulativeWorkingHours);
        roadtest.put("cumulativeFuelConsumption", cumulativeFuelConsumption);
        roadtest.put("perhourFuelConsumption", perhourFuelConsumption);
        roadtest.put("calculateDate", DateUtil.formatDate(new Date(), DateUtil.DATE_FORMAT_YMD));
        roadtestDao.updateBusiness(roadtest);
    }
}
