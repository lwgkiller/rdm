package com.redxun.digitization.core.service;

import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.digitization.core.dao.SzhDao;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;

import groovy.util.logging.Slf4j;

@Service
@Slf4j
public class SzhService {
    private Logger logger = LogManager.getLogger(SzhService.class);
    @Autowired
    private SzhDao szhDao;

    public List<JSONObject> getMenus(Map<String, Object> param) {
        return szhDao.getMenus(param);
    }

    public List<JSONObject> getSubsysByParam(Map<String, Object> param) {
        return szhDao.getSubsysByParam(param);
    }

    public void clickCount(JSONObject formDataJson) {
        formDataJson.put("deptId", ContextUtil.getCurrentUser().getMainGroupId());
        formDataJson.put("dept", ContextUtil.getCurrentUser().getMainGroupName());
        szhDao.insertPageClick(formDataJson);

        // 某个菜单、部门、年月的点击量计数
        String yearMonth = DateUtil.formatDate(new Date(), "yyyy-MM");
        formDataJson.put("yearMonth", yearMonth);
        szhDao.insertPageClickYearMonth(formDataJson);
    }

    public JSONObject queryMenuClickData(HttpServletRequest request) {
        String menuId = RequestUtil.getString(request, "menuId");
        String type = RequestUtil.getString(request, "type");
        String startYearMonth = DateFormatUtil.format(DateUtil.addYear(new Date(), -1), "yyyy-MM");
        String endYearMonth = DateFormatUtil.format(new Date(), "yyyy-MM");

        Map<String, Object> params = new HashMap<>();
        params.put("menuId", menuId);
        params.put("startYearMonth", startYearMonth);
        params.put("endYearMonth", endYearMonth);
        if ("groupByDept".equalsIgnoreCase(type)) {
            return queryDeptMenuClick(params);
        } else {
            return queryMonthMenuClick(params);
        }
    }

    private JSONObject queryDeptMenuClick(Map<String, Object> params) {
        JSONObject result = new JSONObject();
        List<String> deptNames = new ArrayList<>();
        List<Integer> clickNum = new ArrayList<>();
        List<JSONObject> queryData = szhDao.pageClickYearMonthQueryGroupDept(params);
        if (queryData != null && !queryData.isEmpty()) {
            for (JSONObject oneData : queryData) {
                deptNames.add(oneData.getString("deptName"));
                clickNum.add(oneData.getInteger("sumCount"));
            }
        }
        result.put("data", clickNum);
        result.put("deptNames", deptNames);
        return result;
    }

    private JSONObject queryMonthMenuClick(Map<String, Object> params) {
        JSONObject result = new JSONObject();
        List<String> yearMonths = new ArrayList<>();
        Date start = DateUtil.addYear(new Date(), -1);
        for (int index = 1; index <= 12; index++) {
            yearMonths.add(DateFormatUtil.format(DateUtil.add(start, Calendar.MONTH, index), "yyyy-MM"));
        }
        List<Integer> clickNum = new ArrayList<>();
        List<JSONObject> queryData = szhDao.pageClickYearMonthQueryGroupMonth(params);
        Map<String, Integer> month2Num = new HashMap<>();
        if (queryData != null && !queryData.isEmpty()) {
            for (JSONObject oneData : queryData) {
                month2Num.put(oneData.getString("yearMonth"), oneData.getInteger("sumCount"));
            }
        }
        for (String oneMonth : yearMonths) {
            clickNum.add(month2Num.containsKey(oneMonth) ? month2Num.get(oneMonth) : 0);
        }
        result.put("data", clickNum);
        result.put("yearMonths", yearMonths);
        return result;
    }
}
