package com.redxun.standardManager.core.util;

import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import com.google.protobuf.StringValue;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.controller.GenericController;

/**
 * 公共方法
 *
 * @author zz
 */
public class CommonFuns {
    protected Logger logger = LogManager.getLogger(GenericController.class);

    public static ModelAndView getPathView(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath();
        requestURI = requestURI.replace(".do", "");
        int cxtIndex = requestURI.indexOf(contextPath);
        if (cxtIndex != -1) {
            requestURI = requestURI.substring(cxtIndex + contextPath.length());
        }
        String[] paths = requestURI.split("[/]");
        String jspPath = null;
        if (paths != null && paths.length == 6) {
            jspPath = paths[1] + "/" + paths[2] + "/" + paths[3] + "/" + paths[4]
                + StringUtil.makeFirstLetterUpperCase(paths[5]) + ".jsp";
        } else if (paths != null && paths.length == 5) {
            jspPath =
                paths[1] + "/" + paths[2] + "/" + paths[3] + StringUtil.makeFirstLetterUpperCase(paths[4]) + ".jsp";
        } else if (paths != null && paths.length == 4) {
            jspPath = paths[1] + "/" + paths[2] + StringUtil.makeFirstLetterUpperCase(paths[3]) + ".jsp";
        } else {
            jspPath = requestURI + ".jsp";
        }
        return new ModelAndView(jspPath);
    }

    /**
     * 从request中构造参数
     *
     * @param params
     * @param request
     * @param doPage
     *            是否进行分页
     * @return
     */
    public static Map<String, Object> getSearchParam(Map<String, Object> params, HttpServletRequest request,
        boolean doPage) {
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        String pageIndex = request.getParameter("pageIndex");
        String pageSize = request.getParameter("pageSize");
        if (doPage) {
            params.put("currentIndex", Integer.parseInt(pageIndex) * Integer.parseInt(pageSize));
            params.put("pageSize", pageSize);
        }
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)) {
            params.put("sortField", sortField);
            params.put("sortOrder", sortOrder);
        }

        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    if (name.endsWith("_startTime")) {
                        value = DateUtil.formatDate(DateUtil.parseDate(value));
                    }
                    if (name.endsWith("_endTime")) {
                        value = DateUtil.formatDate(DateUtil.parseDate(value));
                    }
                    params.put(name, value);
                }
            }
        }
        return params;
    }

    /**
     * 时间转字符串
     */
    public static String convertDateToStr(Date date, String formatStr) {
        SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
        return sdf.format(date);
    }

    /**
     * 时间格式转换
     */
    public static void convertDate(List<Map<String, Object>> list) {
        if (list != null && !list.isEmpty()) {
            for (Map<String, Object> oneApply : list) {
                for (String key : oneApply.keySet()) {
                    if (key.endsWith("_TIME_") || key.endsWith("_time") || key.endsWith("_date")) {
                        if (oneApply.get(key) != null) {
                            oneApply.put(key, DateUtil.formatDate((Date)oneApply.get(key), "yyyy-MM-dd  HH:mm:ss"));
                        }
                    }
                    if ("conformDate".equals(key)) {
                        if (oneApply.get(key) != null) {
                            oneApply.put(key, DateUtil.formatDate((Date)oneApply.get(key), "yyyy-MM-dd HH:mm:ss"));
                        }
                    }
                    if ("closeDate".equals(key)) {
                        if (oneApply.get(key) != null) {
                            oneApply.put(key, DateUtil.formatDate((Date)oneApply.get(key), "yyyy-MM-dd HH:mm:ss"));
                        }
                    }
                }
            }
        }
    }
    /**
     * 时间格式转换
     */
    public static void convertMapDate(List<Map<String, Object>> list,String dataFormat) {
        if (list != null && !list.isEmpty()) {
            for (Map<String, Object> oneApply : list) {
                for (String key : oneApply.keySet()) {
                    if (key.endsWith("_TIME_") || key.endsWith("_time") || key.endsWith("_date")) {
                        if (oneApply.get(key) != null) {
                            oneApply.put(key, DateUtil.formatDate((Date)oneApply.get(key), dataFormat));
                        }
                    }
                }
            }
        }
    }
    public static void convertDateJSON(List<JSONObject> list) {
        if (list != null && !list.isEmpty()) {
            for (JSONObject object : list) {
                for (String key : object.keySet()) {
                    if (key.endsWith("_TIME_") || key.endsWith("_time") || key.endsWith("_date")) {
                        if (StringUtils.isNotBlank(object.getString(key))) {
                            object.put(key, DateUtil.formatDate(object.getDate(key), "yyyy-MM-dd  HH:mm:ss"));
                        }
                    }
                }
            }
        }
    }
    public static void convertDateJSON(List<JSONObject> list,String dataFormat) {
        if (list != null && !list.isEmpty()) {
            for (JSONObject object : list) {
                for (String key : object.keySet()) {
                    if (key.endsWith("_TIME_") || key.endsWith("_time") || key.endsWith("_date")) {
                        if (StringUtils.isNotBlank(object.getString(key))) {
                            object.put(key, DateUtil.formatDate(object.getDate(key), dataFormat));
                        }
                    }
                }
            }
        }
    }

    public static void convertJsonListDate(List<JSONObject> list,List<String> timeNames,String dateFormat) {
        if (list != null && !list.isEmpty()) {
            for (JSONObject object : list) {
                for (String key : object.keySet()) {
                    for(String timeName:timeNames){
                        if(key.equals(timeName)){
                            if (StringUtils.isNotBlank(object.getString(key))) {
                                object.put(key, DateUtil.formatDate(object.getDate(key), dateFormat));
                            }
                        }
                    }
                }
            }
        }
    }
    public static void convertMapListDate(List<Map<String,Object>> list,List<String> timeNames,String dateFormat) {
        if (list != null && !list.isEmpty()) {
            for (Map<String,Object> object : list) {
                for (String key : object.keySet()) {
                    for(String timeName:timeNames){
                        if(key.equals(timeName)){
                            if (StringUtils.isNotBlank(CommonFuns.nullToString(object.get(key)))) {
                                object.put(key, DateUtil.formatDate((Date)object.get(key), dateFormat));
                            }
                        }
                    }
                }
            }
        }
    }

    public static JSONObject convertMap2JsonObject(Map<String, Object> info) {
        JSONObject result = new JSONObject();
        if (info == null || info.isEmpty()) {
            return result;
        }
        for (Map.Entry<String, Object> entry : info.entrySet()) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    /**
     * 将null转为字符串 防止null报错的问题
     */
    public static String nullToString(Object o) {
        return o == null ? "" : o.toString();
    }
    public static String nullToZeroString(Object o) {
        return o == null ? "0" : o.toString();
    }


    public static Float nullToZero(Object o) {
        return o == null ? 0f : (Float)o;
    }

    public static Float nullToStringToZero(Object o) {
        try{
            String value = nullToString(o);
            if(StringUtil.isEmpty(value)){
                return 0f;
            }else{
                return Float.parseFloat(value);
            }
        }catch (Exception e){
            return 0f;
        }
    }
    public static Double nullToDoubleZero(Object o) {
        return o == null ? 0d : (Double)o;
    }

    public static Double nullToStringToDoubleZero(Object o) {
        try{
            String value = nullToString(o);
            if(StringUtil.isEmpty(value)){
                return 0d;
            }else{
                return Double.parseDouble(value);
            }
        }catch (Exception e){
            return 0d;
        }
    }

    public static String parseTime2Local(String timeStr, String formatStr) {
        if (StringUtils.isBlank(timeStr)) {
            return null;
        }
        if (StringUtils.isBlank(formatStr)) {
            formatStr = DateUtil.DATE_FORMAT_FULL;
        }
        Date date = DateUtil.parseDate(timeStr, formatStr);
        if (date == null) {
            return null;
        }
        return DateUtil.formatDate(date);
    }
    /**
     * 处理历史数据 过滤部门
     * 如果部门fromDeptName存在，则取，不然取fromDeptId
     * */
    public static List<Map<String, Object>> filterDept(List<Map<String, Object>> list){
        for(Map map:list){
            if(map.get("fromDeptId")!=null&&map.get("fromDeptName")!=null){
                map.put("fromDeptId",map.get("fromDeptName"));
            }
        }
        return list;
    }
    public static JSONArray convertListMap2JsonArrObject(List<Map<String, Object>> infos) {
        JSONArray result = new JSONArray();
        if (infos == null || infos.isEmpty()) {
            return result;
        }
        for (Map<String, Object> info : infos) {
            JSONObject jsonObject = new JSONObject();
            for (Map.Entry<String, Object> entry : info.entrySet()) {
                jsonObject.put(entry.getKey(), entry.getValue());
            }
            result.add(jsonObject);
        }
        return result;
    }
    public static JSONObject covertNullToStr(JSONObject jsonObject){
        for(String str:jsonObject.keySet()){
            if(jsonObject.get(str)==null){
                jsonObject.put(str,"");
            }
        }
        return jsonObject;
    }
    /**
     * 获取文件后缀
     *
     * @param fileName
     * @return
     */
    public static String toGetFileSuffix(String fileName) {
        String result = "";
        if (StringUtils.isNotBlank(fileName)) {
            String[] arr = fileName.split("\\.", -1);
            if (arr.length > 1) {
                result = arr[arr.length - 1];
            }
        }
        return result;
    }
    /**
     * 构造字典项
     */
    public  static void getCategoryName2Id(Map<String, Object> categoryName2Id, List<Map<String, Object>> categoryInfos) {
        if (categoryInfos != null && !categoryInfos.isEmpty()) {
            for (Map<String, Object> oneMap : categoryInfos) {
                categoryName2Id.put(oneMap.get("text").toString(), oneMap.get("key_").toString());
                categoryName2Id.put(oneMap.get("key_").toString(), oneMap.get("value").toString());
            }
        }
    }
    /**
     * 构造字典项
     */
    public  static void getCategoryKey2Text(Map<String, Object> categoryName2Id, List<Map<String, Object>> categoryInfos) {
        if (categoryInfos != null && !categoryInfos.isEmpty()) {
            for (Map<String, Object> oneMap : categoryInfos) {
                categoryName2Id.put(oneMap.get("key_").toString(), oneMap.get("text").toString());
            }
        }
    }
    /**
     * 随机生成四位码
     * */
    public static String genCode(int size){
        String codeStorage = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String code = "";
        Random random = new Random();
        for(int i=0;i<size;i++){
            int num = random.nextInt(codeStorage.length());
            char c = codeStorage.charAt(num);
            code += c;
        }
        return code;
    }

    public static String genYearMonth(String type){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        Calendar cal = Calendar.getInstance();
        Date date = new Date();
        cal.setTime(date);
        if("pre".equals(type)){
            cal.add(Calendar.MONTH, -1);
        }else if("cur".equals(type)){
            cal.add(Calendar.MONTH, 0);
        }else if("next".equals(type)){
            cal.add(Calendar.MONTH, +1);
        }

        String yearMonth = sdf.format(cal.getTime());
        return yearMonth;
    }
    public static String genProjectCode(String code,int size){
        int codeNum = Integer.parseInt(code);
        codeNum = codeNum+1;
        StringBuffer projectCode = new StringBuffer();
        projectCode.append(codeNum);
        if(code.length()<size){
            for(int i=0;i<size-code.length();i++){
                projectCode.insert(0,"0");
            }
        }
        return projectCode.toString();
    }
    /**
     * 生成普通流水号
     * */
    public static String genGeneralCode(String code,int size){
        int codeNum = Integer.parseInt(code);
        codeNum = codeNum+1;
        StringBuffer projectCode = new StringBuffer();
        projectCode.append(codeNum);
        if(String.valueOf(codeNum).length()<size){
            for(int i=0;i<size-String.valueOf(codeNum).length();i++){
                projectCode.insert(0,"0");
            }
        }
        return projectCode.toString();
    }
    public static String toGetParamVal(String[] paramValArr) {
        if (paramValArr == null || paramValArr.length == 0) {
            return null;
        }
        return paramValArr[0];
    }
    /**
     * 获取年月 ****-** 2021-06
     *
     */
    public static String getYearMonth(){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        String yearMonth = "";
        if(month<9){
            yearMonth = year+"-0"+month;
        }else{
            yearMonth = year+"-"+month;
        }
        return yearMonth;
    }
    /**
     * 生成数字自动补0的方法
     * */
    /**
     * 生成普通流水号
     * */
    public static String genDigitalCode(int code,int size){
        StringBuffer projectCode = new StringBuffer();
        projectCode.append(code);
        if(String.valueOf(code).length()<size){
            for(int i=0;i<size-String.valueOf(code).length();i++){
                projectCode.insert(0,"0");
            }
        }
        return projectCode.toString();
    }
    /**
     * 月份转换成季度
     * */
    public static String monthToQuarter(int month){
        String quarter = "";
        if(month<=3){
            quarter = "一";
        }else if(month>3 && month<=6){
            quarter = "二";
        }else if(month>6 && month<=9){
            quarter = "三";
        }else if(month>9 && month<=12){
            quarter = "四";
        }
        return quarter;
    }
}
