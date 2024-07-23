package com.redxun.rdmZhgl.core.service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.util.StringUtil;
import com.redxun.rdmZhgl.core.dao.CxyProjectDao;
import com.redxun.rdmZhgl.core.dao.HyglGeneralKanBanDao;
import com.redxun.rdmZhgl.core.dao.HyglInternalDao;
import com.redxun.sys.core.entity.SysDic;
import com.redxun.sys.core.entity.SysTree;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.sys.core.manager.SysTreeManager;
import com.redxun.xcmgJsjl.core.dao.XcmgJsjlDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Service
public class CxyProjectGeneralKanBanService {
    private static Logger logger = LoggerFactory.getLogger(CxyProjectGeneralKanBanService.class);
    @Autowired
    private CxyProjectDao cxyProjectDao;
    @Autowired
    private SysTreeManager sysTreeManager;
    @Autowired
    private SysDicManager sysDicManager;

    //..
    public List<JSONObject> getKanbanData(HttpServletRequest request, HttpServletResponse response, String postDataStr) {
        JSONObject postDataJson = JSONObject.parseObject(postDataStr);
        JSONObject params = new JSONObject();
        params.put("beginTime", postDataJson.getString("yearMonthBegin"));
        params.put("endTime", postDataJson.getString("yearMonthEnd"));
        List<JSONObject> list = cxyProjectDao.dataListQuery(params);
        LinkedHashMap<String, Integer> resultMap = new LinkedHashMap<>();
        //下面根据不同的action统计不同的维度
        if (postDataJson.getString("action").equals("byDepartment")) {
            calculateKanbanData(list, resultMap, "responsibleDepName");
        } else if (postDataJson.getString("action").equals("byImplementation")) {
            calculateKanbanData(list, resultMap, "implementation");
        } else if (postDataJson.getString("action").equals("byProjectProperties")) {
            SysTree sysTree = sysTreeManager.getByKey("cxyProjectProperties");
            List<SysDic> sysDics = new ArrayList<SysDic>();
            sysDics = sysDicManager.getByTreeId(sysTree.getTreeId());
            for (SysDic sysDic : sysDics) {
                resultMap.put(sysDic.getValue(), 0);
            }
            calculateKanbanData(list, resultMap, "projectProperties");
        }

        //下面进行图表json的拼装
        List<JSONObject> resultDataList = new ArrayList<>();
        Iterator<String> iterator = resultMap.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            JSONObject jsonObject = new JSONObject();
            if (postDataJson.getString("action").equals("byDepartment")) {
                jsonObject.put("部门", key);
            } else if (postDataJson.getString("action").equals("byImplementation")) {
                jsonObject.put("完成情况", key);
            } else if (postDataJson.getString("action").equals("byProjectProperties")) {
                jsonObject.put("项目性质", key);
            }
            jsonObject.put("数量", resultMap.get(key));
            resultDataList.add(jsonObject);
        }
        return resultDataList;
    }

    //..
    private void calculateKanbanData(List<JSONObject> list, HashMap<String, Integer> map, String whatToBy) {
        for (JSONObject jsonObject : list) {
            List<String> listwhatToBy = StringUtil.toList(jsonObject.getString(whatToBy), ",");
            for (String toByString : listwhatToBy) {
                //已有就继续计数
                if (map.containsKey(toByString)) {
                    Integer total = map.get(toByString);
                    total++;
                    map.put(toByString, total);
                } else {//没有计1
                    map.put(toByString, 1);
                }
            }
        }
    }

    //..获取总数
    public Integer getTotal(HttpServletRequest request, HttpServletResponse response, String postDataStr) {
        JSONObject postDataJson = JSONObject.parseObject(postDataStr);
        JSONObject params = new JSONObject();
        params.put("beginTime", postDataJson.getString("yearMonthBegin"));
        params.put("endTime", postDataJson.getString("yearMonthEnd"));
        return cxyProjectDao.getTotal(params);
    }
}
