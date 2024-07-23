package com.redxun.componentTest.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.componentTest.core.dao.ComponentTestCapabilityDao;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.saweb.util.RequestUtil;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.MalformedParameterizedTypeException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ComponentTestCapabilityService {
    private static Logger logger = LoggerFactory.getLogger(ComponentTestCapabilityService.class);
    @Autowired
    private ComponentTestCapabilityDao componentTestCapabilityDao;

    //..
    public JsonPageResult<?> componentTestCapabilityItemListQuery(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        JSONObject params = new JSONObject();
        CommonFuns.getSearchParam(params, request, true);
        List<JSONObject> componentTestCapabilityItemList = componentTestCapabilityDao.componentTestCapabilityItemListQuery(params);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        List<JSONObject> componentTestCapabilityList = componentTestCapabilityDao.componentTestCapabilityListQuery(new JSONObject());
        Map<String, JSONObject> componentTestCapabilityMap = new HashedMap();
        for (JSONObject componentTestCapability : componentTestCapabilityList) {
            componentTestCapabilityMap.put(componentTestCapability.getString("id"), componentTestCapability);
        }
        for (JSONObject componentTestCapabilityItem : componentTestCapabilityItemList) {
            if (componentTestCapabilityItem.get("CREATE_TIME_") != null) {
                componentTestCapabilityItem.put("CREATE_TIME_", sdf.format(componentTestCapabilityItem.get("CREATE_TIME_")));
            }
            String key = componentTestCapabilityItem.getString("capabilityId");
            if (componentTestCapabilityMap.containsKey(key)) {
                String[] ids = componentTestCapabilityMap.get(key).getString("path").split("\\.");
                if (ids.length > 3) {
                    componentTestCapabilityItem.put("capability2", componentTestCapabilityMap.get(ids[2]).getString("capability"));
                } else {
                    componentTestCapabilityItem.put("capability2", componentTestCapabilityItem.getString("capability"));
                }
            }
        }
        int count = componentTestCapabilityDao.countComponentTestCapabilityItemListQuery(params);
        result.setData(componentTestCapabilityItemList);
        result.setTotal(count);
        return result;
    }

    //..新方法,服务于树选择小窗口的回调
    public List<JSONObject> componentTestCapabilityItemListQuery2(String capabilityId) {
        JSONObject params = new JSONObject();
        params.put("capabilityId", capabilityId);
        List<JSONObject> componentTestCapabilityItemList = componentTestCapabilityDao.componentTestCapabilityItemListQuery(params);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        List<JSONObject> componentTestCapabilityList = componentTestCapabilityDao.componentTestCapabilityListQuery(new JSONObject());
        Map<String, JSONObject> componentTestCapabilityMap = new HashedMap();
        for (JSONObject componentTestCapability : componentTestCapabilityList) {
            componentTestCapabilityMap.put(componentTestCapability.getString("id"), componentTestCapability);
        }
        for (JSONObject componentTestCapabilityItem : componentTestCapabilityItemList) {
            if (componentTestCapabilityItem.get("CREATE_TIME_") != null) {
                componentTestCapabilityItem.put("CREATE_TIME_", sdf.format(componentTestCapabilityItem.get("CREATE_TIME_")));
            }
            String key = componentTestCapabilityItem.getString("capabilityId");
            if (componentTestCapabilityMap.containsKey(key)) {
                String[] ids = componentTestCapabilityMap.get(key).getString("path").split("\\.");
                if (ids.length > 3) {
                    componentTestCapabilityItem.put("capability2", componentTestCapabilityMap.get(ids[2]).getString("capability"));
                } else {
                    componentTestCapabilityItem.put("capability2", componentTestCapabilityItem.getString("capability"));
                }
            }
        }
        return componentTestCapabilityItemList;
    }

    //..
    public List<JSONObject> getComponentTestCapabilityTree() {
//        List<JSONObject> list = componentTestCapabilityDao.componentTestCapabilityListQuery(new JSONObject());
//        for (JSONObject jsonObject : list) {
//            jsonObject.put("text", jsonObject.getString("capability"));
//        }
//        return list;
        //@lwgkiller:看似和常规实现代码差不多量，但是lambda实现一气呵成，没有中间变量，省去了for循环的模板代码，直指业务本身，还是
        //很有魅力的，不用则以，用起来还是很不错的
        return componentTestCapabilityDao.componentTestCapabilityListQuery(new JSONObject())
                .stream()
                .map(jsonObject -> {
                    jsonObject.put("text", jsonObject.getString("capability"));
                    return jsonObject;
                }).collect(Collectors.toList());
    }

    //..新方法
    public List<JSONObject> getComponentTestCapabilityTreeByPath(String path) {
        return componentTestCapabilityDao.selectComponentTestCapabilityByPath(path)
                .stream()
                .map(jsonObject -> {
                    jsonObject.put("text", jsonObject.getString("capability"));
                    return jsonObject;
                }).collect(Collectors.toList());
    }

    //..
    public List<JSONObject> getComponentTestCapabilityChartData() {
        JSONObject param = new JSONObject();
        List<JSONObject> echartsData = new ArrayList<>();
        param.put("pid", "0");
        List<JSONObject> componentTestCapabilityList = componentTestCapabilityDao.componentTestCapabilityListQuery(param);
        if (componentTestCapabilityList.size() > 0) {
            echartsData = this.buildComponentTestCapabilityChartData(componentTestCapabilityList);
        }
        return echartsData;
    }

    //..
    private List<JSONObject> buildComponentTestCapabilityChartData(List<JSONObject> list) {
        List<JSONObject> finalData = new ArrayList<>();
        for (JSONObject componentTestCapability : list) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", componentTestCapability.getString("capability"));
            jsonObject.put("value", componentTestCapability.getString("id"));
            String id = componentTestCapability.getString("id");
            JSONObject param = new JSONObject();
            param.put("pid", id);
            List<JSONObject> children = componentTestCapabilityDao.componentTestCapabilityListQuery(param);
            if (children.size() > 0) {
                jsonObject.put("children", buildComponentTestCapabilityChartData(children));
            }
            finalData.add(jsonObject);
        }
        return finalData;
    }

    //..
    public void createComponentTestCapability(JSONObject formDataJson) {
        String pid = formDataJson.getString("pid");
        String id = IdUtil.getId();
        if (StringUtils.isBlank(pid)) {
            formDataJson.put("pid", "0");
            formDataJson.put("path", "0." + id + ".");
        } else {
            formDataJson.put("path", formDataJson.getString("path") + id + ".");
        }
        formDataJson.put("id", id);
        formDataJson.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formDataJson.put("CREATE_TIME_", new Date());
        componentTestCapabilityDao.createComponentTestCapability(formDataJson);
    }

    //..
    public void updateComponentTestCapability(JSONObject formDataJson) {
        formDataJson.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formDataJson.put("UPDATE_TIME_", new Date());
        componentTestCapabilityDao.updateComponentTestCapability(formDataJson);
    }

    //..
    public JSONObject getComponentTestCapabilityById(String componentTestCapabilityId) {
        return componentTestCapabilityDao.getComponentTestCapabilityById(componentTestCapabilityId);
    }

    //..
    public JsonResult deleteComponentTestCapability(String path) {
        JsonResult jsonResult = new JsonResult(true);
        List<JSONObject> list = componentTestCapabilityDao.selectComponentTestCapabilityByPath(path);
        List<String> componentTestCapabilityIds =
                list.stream().map(jsonObject -> jsonObject.getString("id")).collect(Collectors.toList());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("componentTestCapabilityIds", componentTestCapabilityIds);
        componentTestCapabilityDao.deleteComponentTestCapabilityItem(jsonObject);
        componentTestCapabilityDao.deleteComponentTestCapability(path);
        return jsonResult;
    }

    //..
    public void createComponentTestCapabilityItem(JSONObject jsonObject) {
        jsonObject.put("id", IdUtil.getId());
        jsonObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        jsonObject.put("creator", ContextUtil.getCurrentUser().getFullname());
        jsonObject.put("CREATE_TIME_", new Date());
        componentTestCapabilityDao.createComponentTestCapabilityItem(jsonObject);
    }

    //..
    public void updateComponentTestCapabilityItem(JSONObject jsonObject) {
        jsonObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        jsonObject.put("UPDATE_TIME_", new Date());
        componentTestCapabilityDao.updateComponentTestCapabilityItem(jsonObject);
    }

    //..
    public JsonResult deleteComponentTestCapabilityItem(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ids", ids);
        componentTestCapabilityDao.deleteComponentTestCapabilityItem(jsonObject);
        return result;
    }
}
