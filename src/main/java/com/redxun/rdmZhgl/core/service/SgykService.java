package com.redxun.rdmZhgl.core.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.rdmZhgl.core.dao.SgykDao;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class SgykService {
    private static Logger logger = LoggerFactory.getLogger(SgykService.class);
    @Autowired
    private SgykDao sgykDao;

    //..
    public List<JSONObject> templateList() {
        List<JSONObject> result = sgykDao.templateList();
        for (JSONObject oneObj : result) {
            if (StringUtils.isNotBlank(oneObj.getString("CREATE_TIME_"))) {
                oneObj.put("CREATE_TIME_",
                        DateFormatUtil.format(oneObj.getDate("CREATE_TIME_"), "yyyy-MM-dd HH:mm:ss"));
            }
            if (StringUtils.isNotBlank(oneObj.getString("UPDATE_TIME_"))) {
                oneObj.put("UPDATE_TIME_",
                        DateFormatUtil.format(oneObj.getDate("UPDATE_TIME_"), "yyyy-MM-dd HH:mm:ss"));
            }
        }
        return result;
    }

    //..
    public void saveTemplate(JSONObject result, String changeGridDataStr) {
        try {
            JSONArray changeGridDataJson = JSONObject.parseArray(changeGridDataStr);
            if (changeGridDataJson == null || changeGridDataJson.isEmpty()) {
                logger.warn("gridDataArray is blank");
                result.put("message", "保存失败，数据为空！");
                return;
            }

            Set<String> needDelId = new HashSet<>();
            for (int i = 0; i < changeGridDataJson.size(); i++) {
                JSONObject oneObject = changeGridDataJson.getJSONObject(i);
                String state = oneObject.getString("_state");
                String id = oneObject.getString("id");
                if ("added".equals(state) || StringUtils.isBlank(id)) {
                    // 新增
                    oneObject.put("id", IdUtil.getId());
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                    sgykDao.saveTemplate(oneObject);
                } else if ("modified".equals(state)) {
                    // 修改
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                    sgykDao.updateTemplate(oneObject);
                } else if ("removed".equals(state)) {
                    needDelId.add(id);
                }
            }
            if (!needDelId.isEmpty()) {
                Map<String, Object> params = new HashMap<>();
                params.put("ids", needDelId);
                sgykDao.delTemplateByIds(params);

            }

        } catch (Exception e) {
            logger.error("Exception in saveDimension");
            result.put("message", "保存失败，系统异常！");
            return;
        }
    }

    //..
    public List<JSONObject> sgykList(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> params = new HashMap<>();
        //初始化查询参数
        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    // 数据库中存储的时间是UTC时间，因此需要将前台传递的北京时间转化
                }
                params.put(name, value);
            }
        }
        List<JSONObject> sgykDataList = sgykDao.sgykList(params);
        if (sgykDataList != null && !sgykDataList.isEmpty()) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (JSONObject oneObj : sgykDataList) {
                if (oneObj.getDate("CREATE_TIME_") != null) {
                    oneObj.put("CREATE_TIME_", simpleDateFormat.format(oneObj.getDate("CREATE_TIME_")));
                }
            }
        }
        return sgykDataList;
    }

    //..
    public List<JSONObject> sgykOneYearList(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> params = new HashMap<>();
        //初始化查询参数
        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    // 数据库中存储的时间是UTC时间，因此需要将前台传递的北京时间转化
                }
                params.put(name, value);
            }
        }
        //下面是行数据转置为列数据的过程，规定每次都是一整年的数据，缺的月份的指标值补全为空字符串
        List<JSONObject> sgykOneYearDataList = sgykDao.sgykOneYearList(params);
        LinkedHashMap<String, JSONObject> sgykOneYearDataMap = new LinkedHashMap<>();
        for (JSONObject sgykYearMonthAccount : sgykOneYearDataList) {
            //没有当月的某指标的数据
            if (sgykOneYearDataMap.get(sgykYearMonthAccount.getString("normKey")) == null) {
                JSONObject sgykYearAccount = new JSONObject();
                iniYearData(sgykYearAccount, sgykYearMonthAccount);
                transposeYearData(sgykYearMonthAccount.getString("signMonth"), sgykYearAccount,
                        sgykYearMonthAccount.getString("normValue"));
                sgykOneYearDataMap.put(sgykYearMonthAccount.getString("normKey"), sgykYearAccount);
            } //有当月的某指标的数据
            else {
                JSONObject sgykYearAccount = sgykOneYearDataMap.get(sgykYearMonthAccount.getString("normKey"));
                transposeYearData(sgykYearMonthAccount.getString("signMonth"), sgykYearAccount,
                        sgykYearMonthAccount.getString("normValue"));
                sgykOneYearDataMap.put(sgykYearMonthAccount.getString("normKey"), sgykYearAccount);
            }
        }
        List<JSONObject> sgykOneYearDataListTranspose = new ArrayList<>();
        Iterator<String> iterator = sgykOneYearDataMap.keySet().iterator();
        while (iterator.hasNext()) {
            String account = iterator.next();
            sgykOneYearDataListTranspose.add(sgykOneYearDataMap.get(account));
        }
        return sgykOneYearDataListTranspose;
    }

    //..
    public LinkedHashMap<String, LinkedHashMap> sgykCycleReportList(HttpServletRequest request, HttpServletResponse response,
                                                                    String postDataStr) {
        JSONObject postDataJson = JSONObject.parseObject(postDataStr);
        Map<String, Object> params = new HashMap<>();
        params.put("yearBegin", postDataJson.getString("yearMonthBegin").split("-")[0]);
        params.put("monthBegin", postDataJson.getString("yearMonthBegin").split("-")[1]);
        params.put("yearEnd", postDataJson.getString("yearMonthEnd").split("-")[0]);
        params.put("monthEnd", postDataJson.getString("yearMonthEnd").split("-")[1]);
        params.put("account", postDataJson.getString("account").equalsIgnoreCase("") ? "" :
                postDataJson.getString("account").split(","));
        //下面是行数据转置为列数据的过程，缺的月份的指标值补全为0
        List<JSONObject> sgykCycleReportDataList = sgykDao.sgykCycleReportList(params);
        LinkedHashMap<String, LinkedHashMap> sgykCycleReportDataMap = new LinkedHashMap<>();
        for (JSONObject sgykCycleReportData : sgykCycleReportDataList) {
            String key = sgykCycleReportData.getString("normKey") + ":" + sgykCycleReportData.getString("normDesp");
            String yearMonthKey = sgykCycleReportData.getString("signYear") + "-" + sgykCycleReportData.getString("signMonth");
            String value = sgykCycleReportData.getString("normValue");
            if (sgykCycleReportDataMap.containsKey(key)) {
                sgykCycleReportDataMap.get(key).put(yearMonthKey, value);
            } else {
                LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<>();
                linkedHashMap.put(yearMonthKey, value);
                sgykCycleReportDataMap.put(key, linkedHashMap);
            }
        }
        return sgykCycleReportDataMap;
    }

    private void iniYearData(JSONObject sgykYearAccount, JSONObject sgykYearMonthAccount) {
        sgykYearAccount.put("signYear", sgykYearMonthAccount.getString("signYear"));
        sgykYearAccount.put("normClass", sgykYearMonthAccount.getString("normClass"));
        sgykYearAccount.put("normDesp", sgykYearMonthAccount.getString("normDesp"));
        sgykYearAccount.put("normKey", sgykYearMonthAccount.getString("normKey"));
        sgykYearAccount.put("January", "");
        sgykYearAccount.put("February", "");
        sgykYearAccount.put("March", "");
        sgykYearAccount.put("April", "");
        sgykYearAccount.put("May", "");
        sgykYearAccount.put("June", "");
        sgykYearAccount.put("July", "");
        sgykYearAccount.put("August", "");
        sgykYearAccount.put("September", "");
        sgykYearAccount.put("October", "");
        sgykYearAccount.put("November", "");
        sgykYearAccount.put("December", "");
    }

    private void transposeYearData(String normMonth, JSONObject sgykYearAccount, String normValue) {
        switch (normMonth) {
            case "01":
                sgykYearAccount.put("January", normValue);
                break;
            case "02":
                sgykYearAccount.put("February", normValue);
                break;
            case "03":
                sgykYearAccount.put("March", normValue);
                break;
            case "04":
                sgykYearAccount.put("April", normValue);
                break;
            case "05":
                sgykYearAccount.put("May", normValue);
                break;
            case "06":
                sgykYearAccount.put("June", normValue);
                break;
            case "07":
                sgykYearAccount.put("July", normValue);
                break;
            case "08":
                sgykYearAccount.put("August", normValue);
                break;
            case "09":
                sgykYearAccount.put("September", normValue);
                break;
            case "10":
                sgykYearAccount.put("October", normValue);
                break;
            case "11":
                sgykYearAccount.put("November", normValue);
                break;
            case "12":
                sgykYearAccount.put("December", normValue);
                break;
        }
    }

    //..
    public List<JSONObject> sgykDetailListByMainId(String sgykId) {
        List<JSONObject> sgykDetailDataList = sgykDao.sgykDetailListByMainId(sgykId);
        if (sgykDetailDataList != null && !sgykDetailDataList.isEmpty()) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (JSONObject oneObj : sgykDetailDataList) {
                if (oneObj.getDate("CREATE_TIME_") != null) {
                    oneObj.put("CREATE_TIME_", simpleDateFormat.format(oneObj.getDate("CREATE_TIME_")));
                }
            }
        }
        return sgykDetailDataList;
    }

    //..
    public void createSgyk(JsonResult result, String signYear, String signMonth) {
        List<JSONObject> sgykTemplateList = null;
        Map<String, Object> params = new HashMap<>();
        params.put("signYear", signYear);
        params.put("signMonth", signMonth);
        List<JSONObject> sgykList = sgykDao.sgykList(params);
        //如果找到了当前时间的记录，则不生成
        if (sgykList.size() > 0) {
            result.setSuccess(false);
            result.setMessage("已经存在当年当月的记录，不能重复创建！");
            return;
        } else {
            sgykTemplateList = this.templateList();
            JSONObject sgykBase = new JSONObject();
            sgykBase.put("id", IdUtil.getId());
            sgykBase.put("signYear", signYear);
            sgykBase.put("signMonth", signMonth);
            sgykBase.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            sgykBase.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            sgykDao.saveSgykBase(sgykBase);
            for (JSONObject sgykTemplateItem : sgykTemplateList) {
                JSONObject sgykDetail = new JSONObject();
                sgykDetail.put("id", IdUtil.getId());
                sgykDetail.put("sgykId", sgykBase.getString("id"));
                sgykDetail.put("normClass", sgykTemplateItem.getString("normClass"));
                sgykDetail.put("normKey", sgykTemplateItem.getString("normKey"));
                sgykDetail.put("normDesp", sgykTemplateItem.getString("normDesp"));
                sgykDetail.put("normValue", "0");
                sgykDetail.put("orderNum", sgykTemplateItem.getString("orderNum"));
                sgykDetail.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                sgykDetail.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                sgykDao.saveSgykDetail(sgykDetail);
            }
        }
    }

    //..
    public void calculateSgyk(JsonResult result, String signYear, String signMonth) {
        List<JSONObject> sgykTemplateList = null;
        Map<String, JSONObject> sgykTemplateMap = new HashMap<>();
        Map<String, JSONObject> sgykDetailMap = new HashMap<>();
        Map<String, Object> params = new HashMap<>();
        params.put("signYear", signYear);
        params.put("signMonth", signMonth);
        List<JSONObject> sgykList = sgykDao.sgykList(params);
        //如果没找到当前时间的记录，则不计算
        if (sgykList.size() == 0) {
            result.setSuccess(false);
            result.setMessage("不存在当年当月的记录，不能计算！");
            return;
        } else {
            sgykTemplateList = this.templateList();
            for (JSONObject sgykTemplateItem : sgykTemplateList) {
                sgykTemplateMap.put(sgykTemplateItem.getString("normKey"), sgykTemplateItem);
            }
            List<JSONObject> sgykDetailList =
                    this.sgykDetailListByMainId(sgykList.get(0).getString("id"));
            for (JSONObject sgykDetail : sgykDetailList) {
                sgykDetailMap.put(sgykDetail.getString("normKey"), sgykDetail);
            }
        }
        this.calculateSgykValue(result, sgykTemplateMap, sgykDetailMap);
    }

    //..
    private void calculateSgykValue(JsonResult result,
                                    Map<String, JSONObject> sgykTemplateMap,
                                    Map<String, JSONObject> sgykDetailMap) {
        Iterator<String> iterator = sgykDetailMap.keySet().iterator();
        while (iterator.hasNext()) {
            String account = iterator.next();
            JSONObject TemplateItem = sgykTemplateMap.get(account);
            String[] parms = null;
            //此处简化为硬编码参数个数，因为情况有限，最多两个参数参与计算，按理说这个应该放到模板的配置里，通过一个容器进行传参
            BigDecimal value1 = BigDecimal.ZERO;
            BigDecimal value2 = BigDecimal.ZERO;
            BigDecimal normValue = BigDecimal.ZERO;
            //此处简化为case，按理说应该通过一个容器参数，根据配置内容调用不同的策略模式，实现解耦，一切从简了
            if (TemplateItem != null) {
                switch (TemplateItem.getString("calculateClass")) {
                    case "neg":
                        break;
                    case "a/b*100":
                        parms = TemplateItem.getString("calculateParms").split(",");
                        value1 = new BigDecimal(sgykDetailMap.get(parms[0]).getString("normValue"));
                        value2 = new BigDecimal(sgykDetailMap.get(parms[1]).getString("normValue"));
                        if (value2.compareTo(BigDecimal.ZERO) != 0) {
                            //注意，这里写死了，只有一个指标key是保留6位的情况，而且指标key不会改变，按理说需要将保留位数作为配置项放到模板里面
                            if (account.equalsIgnoreCase("account34")) {
                                normValue = value1.divide(value2, 8, BigDecimal.ROUND_HALF_UP).
                                        multiply(new BigDecimal("100")).setScale(6, BigDecimal.ROUND_HALF_UP);
                                sgykDetailMap.get(account).put("normValue", normValue.toString());
                            } else {
                                normValue = value1.divide(value2, 4, BigDecimal.ROUND_HALF_UP).
                                        multiply(new BigDecimal("100")).setScale(2, BigDecimal.ROUND_HALF_UP);
                                sgykDetailMap.get(account).put("normValue", normValue.toString());
                            }
                            sgykDao.updateSgykDetail(sgykDetailMap.get(account));
                        } else {
                            result.setSuccess(false);
                            result.setMessage(sgykTemplateMap.get(parms[1]).getString("normDesp") +
                                    "的值为0且为分母，不能计算！");
                            return;
                        }
                        break;
                    case "a/b":
                        parms = TemplateItem.getString("calculateParms").split(",");
                        value1 = new BigDecimal(sgykDetailMap.get(parms[0]).getString("normValue"));
                        value2 = new BigDecimal(sgykDetailMap.get(parms[1]).getString("normValue"));
                        if (value2.compareTo(BigDecimal.ZERO) != 0) {
                            //注意，这里写死了，只有一个指标key是保留6位的情况，而且指标key不会改变，按理说需要将保留位数作为配置项放到模板里面
                            if (account.equalsIgnoreCase("account34")) {
                                normValue = value1.divide(value2, 6, BigDecimal.ROUND_HALF_UP);
                                sgykDetailMap.get(account).put("normValue", normValue.toString());
                            } else {
                                normValue = value1.divide(value2, 2, BigDecimal.ROUND_HALF_UP);
                                sgykDetailMap.get(account).put("normValue", normValue.toString());
                            }
                            sgykDao.updateSgykDetail(sgykDetailMap.get(account));
                        } else {
                            result.setSuccess(false);
                            result.setMessage(sgykTemplateMap.get(parms[1]).getString("normDesp") +
                                    "的值为0且为分母，不能计算！");
                            return;
                        }
                        break;
                    case "a+b":
                        parms = TemplateItem.getString("calculateParms").split(",");
                        value1 = new BigDecimal(sgykDetailMap.get(parms[0]).getString("normValue"));
                        value2 = new BigDecimal(sgykDetailMap.get(parms[1]).getString("normValue"));
                        normValue = value1.add(value2);
                        sgykDetailMap.get(account).put("normValue", normValue.toString());
                        sgykDao.updateSgykDetail(sgykDetailMap.get(account));
                        break;
                    case "a-b":
                        parms = TemplateItem.getString("calculateParms").split(",");
                        value1 = new BigDecimal(sgykDetailMap.get(parms[0]).getString("normValue"));
                        value2 = new BigDecimal(sgykDetailMap.get(parms[1]).getString("normValue"));
                        normValue = value1.subtract(value2);
                        sgykDetailMap.get(account).put("normValue", normValue.toString());
                        sgykDao.updateSgykDetail(sgykDetailMap.get(account));
                        break;
                    case "a":
                        parms = TemplateItem.getString("calculateParms").split(",");
                        value1 = new BigDecimal(sgykDetailMap.get(parms[0]).getString("normValue"));
                        normValue = value1;
                        sgykDetailMap.get(account).put("normValue", normValue.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                        sgykDao.updateSgykDetail(sgykDetailMap.get(account));
                        break;
                }
            }
        }
    }

    //..
    public void saveSgykDetail(JsonResult result, String dataStr) {
        JSONArray jsonArray = JSONObject.parseArray(dataStr);
        for (Object object : jsonArray) {
            JSONObject sgykDetailObj = (JSONObject) object;
            sgykDetailObj.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            sgykDetailObj.put("UPDATE_TIME_", new Date());
            sgykDao.updateSgykDetail(sgykDetailObj);
        }
    }

    //..
    public void exportOneYearList(HttpServletRequest request, HttpServletResponse response) {
        List<Map<String, Object>> listDataMap = new ArrayList<>();
        List<JSONObject> listData = sgykOneYearList(request, response);
        for (JSONObject jsonObject : listData) {
            listDataMap.add(jsonObject.getInnerMap());
        }

        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "三高一可指标列表";
        String excelName = nowDate + title;
        String[] fieldNames = {"年份", "指标类别", "指标描述", "1月", "2月", "3月", "4月", "5月", "6月", "7月",
                "8月", "9月", "10月", "11月", "12月"};
        String[] fieldCodes = {"signYear", "normClass", "normDesp", "January", "February", "March",
                "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(listDataMap, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }
}
