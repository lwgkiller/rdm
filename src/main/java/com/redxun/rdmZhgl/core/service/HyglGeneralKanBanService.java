package com.redxun.rdmZhgl.core.service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.rdmZhgl.core.dao.HyglGeneralKanBanDao;
import com.redxun.rdmZhgl.core.dao.HyglInternalDao;
import com.redxun.xcmgJsjl.core.dao.XcmgJsjlDao;
import com.redxun.xcmgJsjl.core.manager.XcmgJsjlManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

@Service
public class HyglGeneralKanBanService {
    private static Logger logger = LoggerFactory.getLogger(HyglGeneralKanBanService.class);
    @Autowired
    private HyglInternalDao hyglInternalDao;
    @Autowired
    private XcmgJsjlDao xcmgJsjlDao;
    @Autowired
    private HyglGeneralKanBanDao hyglGeneralKanBanDao;

    //..
    public List<JSONObject> getKanbanData(HttpServletRequest request, HttpServletResponse response, String postDataStr) {
        JSONObject postDataJson = JSONObject.parseObject(postDataStr);
        JSONObject params = new JSONObject();
        params.put("meetingTimeBegin", postDataJson.getString("yearMonthBegin"));
        params.put("meetingTimeEnd", postDataJson.getString("yearMonthEnd"));
        params.put("communicateStartTime", postDataJson.getString("yearMonthBegin"));
        params.put("communicateEndTime", postDataJson.getString("yearMonthEnd"));
        params.put("recordStatus", "已提交");
        List<JSONObject> internalList = hyglInternalDao.dataListQuery(params);
        List<JSONObject> externalList = xcmgJsjlDao.dataListQuery(params);
        HashMap<String, Integer> resultMap = new HashMap<>();
        //下面根据不同的action统计不同的维度
        if (postDataJson.getString("action").equals("internalByDepartment")) {
            calculateKanbanData(internalList, resultMap, "meetingOrgDepName");
        } else if (postDataJson.getString("action").equals("externalByDepartment")) {
            calculateKanbanData(externalList, resultMap, "deptName");
        } else if (postDataJson.getString("action").equals("internalByModel")) {
            calculateKanbanData(internalList, resultMap, "meetingModel");
        } else if (postDataJson.getString("action").equals("externalByModel")) {
            calculateKanbanData(externalList, resultMap, "dimensionName");
        }
        //下面进行图表json的拼装
        List<JSONObject> resultDataList = new ArrayList<>();
        Iterator<String> iterator = resultMap.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            JSONObject jsonObject = new JSONObject();
            if (postDataJson.getString("action").equals("internalByDepartment") ||
                    postDataJson.getString("action").equals("externalByDepartment")) {
                jsonObject.put("部门", key);
            } else if (postDataJson.getString("action").equals("internalByModel") ||
                    postDataJson.getString("action").equals("externalByModel")) {
                jsonObject.put("模块", key);
            }
            jsonObject.put("数量", resultMap.get(key));
            resultDataList.add(jsonObject);
        }
        return resultDataList;
    }

    //..
    private void calculateKanbanData(List<JSONObject> list, HashMap<String, Integer> map, String whatToBy) {
        for (JSONObject jsonObject : list) {
            //已有就继续计数
            if (map.containsKey(jsonObject.getString(whatToBy))) {
                Integer total = map.get(jsonObject.getString(whatToBy));
                total++;
                map.put(jsonObject.getString(whatToBy), total);
            } else {//没有计1
                map.put(jsonObject.getString(whatToBy), 1);
            }
        }
    }

    //..111
    //..获取内部会议总数
    public Integer getInternalTotal(HttpServletRequest request, HttpServletResponse response, String postDataStr) {
        JSONObject postDataJson = JSONObject.parseObject(postDataStr);
        JSONObject params = new JSONObject();
        params.put("meetingTimeBegin", postDataJson.getString("yearMonthBegin"));
        params.put("meetingTimeEnd", postDataJson.getString("yearMonthEnd"));
        params.put("recordStatus", "已提交");
        return hyglGeneralKanBanDao.getInternalTotal(params);
    }

    //..获取外部会议总数
    public Integer getExternalTotal(HttpServletRequest request, HttpServletResponse response, String postDataStr) {
        JSONObject postDataJson = JSONObject.parseObject(postDataStr);
        JSONObject params = new JSONObject();
        params.put("communicateStartTime", postDataJson.getString("yearMonthBegin"));
        params.put("communicateEndTime", postDataJson.getString("yearMonthEnd"));
        params.put("recordStatus", "已提交");
        return hyglGeneralKanBanDao.getExternalTotal(params);
    }
}
