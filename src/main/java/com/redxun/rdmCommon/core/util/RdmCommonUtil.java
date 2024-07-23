package com.redxun.rdmCommon.core.util;

import java.util.*;

import javax.servlet.http.HttpServletRequest;

import com.redxun.core.util.StringUtil;
import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.query.Page;
import com.redxun.core.util.DateUtil;
import com.redxun.saweb.util.RequestUtil;

public class RdmCommonUtil {
    public static List doPageByRequest(List allDataList, HttpServletRequest request) {
        if (allDataList == null || allDataList.isEmpty()) {
            return new ArrayList();
        }
        int pageIndex = RequestUtil.getInt(request, "pageIndex", 0);
        int pageSize = RequestUtil.getInt(request, "pageSize", Page.DEFAULT_PAGE_SIZE);
        int startSubListIndex = pageIndex * pageSize;
        int endSubListIndex = startSubListIndex + pageSize;
        if (startSubListIndex < allDataList.size()) {
            return allDataList.subList(startSubListIndex,
                endSubListIndex < allDataList.size() ? endSubListIndex : allDataList.size());
        } else {
            return new ArrayList<>();
        }
    }

    public static String toGetParamVal(String[] paramValArr) {
        if (paramValArr == null || paramValArr.length == 0) {
            return null;
        }
        return paramValArr[0];
    }

    public static void formatCreateTime(List<JSONObject> dataList, String formatStr) {
        if (dataList != null && !dataList.isEmpty()) {
            if (StringUtils.isBlank(formatStr)) {
                formatStr = "yyyy-MM-dd HH:mm:ss";
            }
            for (JSONObject oneData : dataList) {
                if (oneData.get("CREATE_TIME_") != null) {
                    oneData.put("CREATE_TIME_", DateUtil.formatDate((Date)oneData.get("CREATE_TIME_"), formatStr));
                }
                if (oneData.get("planStartTime") != null
                    && StringUtils.isNotBlank(oneData.getString("planStartTime"))) {
                    oneData.put("planStartTime", DateUtil.formatDate(oneData.getDate("planStartTime"), formatStr));
                }
            }
        }
    }

    // admin查所有，包括草稿；其他人员看非草稿的或者自己的
    public static void addListAllQueryRoleExceptDraft(Map<String, Object> params, String userId, String userNo) {
        if (userNo.equalsIgnoreCase("admin") && !params.containsKey("report")) {
            return;
        }
        params.put("currentUserId", userId);
        params.put("roleName", "other");
    }

    //..根据args里的属性名称，将json对象里对应名称的字符串日期属性格式化为yyyy-mm-dd格式的字符串
    public static void formatJsonObjectStringDateField(String[] args, JSONObject jsonObject) {
        for (String arg : args) {
            if (jsonObject.containsKey(arg) && StringUtil.isNotEmpty(jsonObject.getString(arg))) {
                String dateString = jsonObject.getString(arg);
                Date date = DateUtil.parseDate(dateString);
                String dateStringFormat = DateUtil.formatDate(date, DateUtil.DATE_FORMAT_YMD);
                jsonObject.put(arg, dateStringFormat);
            }
        }
    }

    public static Map<String, String> parseUrlParams(String url) {
        Map<String, String> params = new HashMap<>();
        String[] urlParts = url.split("\\?");
        if (urlParts.length > 1) {
            String queryString = urlParts[1];
            String[] pairs = queryString.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                if (keyValue.length > 1) {
                    params.put(keyValue[0], keyValue[1]);
                } else {
                    params.put(keyValue[0], "");
                }
            }
        }
        return params;
    }
}
