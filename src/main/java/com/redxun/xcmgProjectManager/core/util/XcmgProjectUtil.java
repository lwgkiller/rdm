package com.redxun.xcmgProjectManager.core.util;

import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.saweb.context.ContextUtil;

public class XcmgProjectUtil {
    private static final Logger logger = LoggerFactory.getLogger(XcmgProjectUtil.class);

    // 获取UTC当前时间字符串
    public static String getNowUTCDateStr(String formatStr) {
        String todayStart = DateFormatUtil.getNowByString(formatStr);
        String result = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(todayStart, formatStr), -8), formatStr);
        return result;
    }

    // 获取本地当前时间字符串
    public static String getNowLocalDateStr(String formatStr) {
        String todayStart = DateFormatUtil.getNowByString(formatStr);
        return todayStart;
    }

    // 通过本月的yyyy-MM获得指定月
    public static String getPointMonthDateStr(String currentMonthDateStr, int num) {
        if (StringUtils.isBlank(currentMonthDateStr)) {
            return "";
        }
        try {
            SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM");
            Date currentDate = sd.parse(currentMonthDateStr);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currentDate);
            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + num);
            return sd.format(calendar.getTime());
        } catch (Exception e) {
            logger.error("Exception in getPointMonthDateStr", e);
            return "";
        }
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

    public static JSONArray convertListMap2JsonArrString(List<Map<String, String>> infos) {
        JSONArray result = new JSONArray();
        if (infos == null || infos.isEmpty()) {
            return result;
        }
        for (Map<String, String> info : infos) {
            JSONObject jsonObject = new JSONObject();
            for (Map.Entry<String, String> entry : info.entrySet()) {
                jsonObject.put(entry.getKey(), entry.getValue());
            }
            result.add(jsonObject);
        }
        return result;
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

    public static JSONObject convertMap2JsonString(Map<String, String> info) {
        JSONObject result = new JSONObject();
        if (info == null || info.isEmpty()) {
            return result;
        }
        for (Map.Entry<String, String> entry : info.entrySet()) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    // 判断是否是部门级别的项目管理人员
    public static boolean judgeIsDepProjectManager(String currentUserMainDepName,
        List<Map<String, Object>> currentUserRoles) {
        for (Map<String, Object> oneRole : currentUserRoles) {
            if (oneRole.get("REL_TYPE_KEY_").toString().equalsIgnoreCase("GROUP-USER-LEADER")
                || oneRole.get("REL_TYPE_KEY_").toString().equalsIgnoreCase("GROUP-USER-BELONG")) {
                String roleName = oneRole.get("NAME_").toString();
                if (!RdmConst.JSZXXMGLRY.equalsIgnoreCase(roleName) && roleName.endsWith("项目管理人员")
                    && roleName.startsWith(currentUserMainDepName)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 将null转为字符串 防止null报错的问题
     */
    public static String nullToString(Object o) {
        return o == null ? "" : o.toString();
    }

    /**
     * 过滤代办信息
     */
    public static Boolean filterTask(String key, String[] taskKeys) {
        try {
            List<String> taskKeyList = Arrays.asList(taskKeys);
            if (taskKeyList.contains(key)) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    /**
     * 获取查询参数公共方法
     */
    public static Map<String, Object> getSearchParam(Map<String, Object> params, HttpServletRequest request) {
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
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
                    // 数据库中存储的时间是UTC时间，因此需要将前台传递的北京时间转化
                    if (name.endsWith("_startTime")) {
                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), -8));
                    }
                    if (name.endsWith("_endTime")) {
                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), 16));
                    }
                    params.put(name, value);
                }
            }
        }
        return params;
    }

    public static String convertProjectStatusCode2Name(Object statusCode) {
        if (statusCode == null) {
            return "";
        }
        String statusCodeStr = statusCode.toString();
        if (StringUtils.isBlank(statusCodeStr)) {
            return "";
        }
        switch (statusCodeStr) {
            case "DRAFTED":
                return "草稿";
            case "RUNNING":
                return "运行中";
            case "SUCCESS_END":
                return "成功结束";
            case "DISCARD_END":
                return "作废";
            case "ABORT_END":
                return "异常中止结束";
            case "PENDING":
                return "挂起";
        }
        return "";
    }
}
