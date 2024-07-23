package com.redxun.environment.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateUtil;
import com.redxun.environment.core.dao.RjbgglDao;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import groovy.util.logging.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service
@Slf4j
public class RjbgglService {
    private Logger logger = LogManager.getLogger(RjbgglService.class);
    @Autowired
    private RjbgglDao rjbgglDao;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;


    public JsonPageResult<?> queryRjbggl(HttpServletRequest request, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)) {
            params.put("sortField", sortField);
            params.put("sortOrder", sortOrder);
        }
        if (StringUtils.isEmpty(sortField)) {
            params.put("sortField", "CREATE_TIME_");
            params.put("sortOrder", "desc");
        }
        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    params.put(name, value);
                }
            }
        }
        if (doPage) {
            rdmZhglUtil.addPage(request, params);
        }
        params.put("currentUserId", ContextUtil.getCurrentUserId());
        //addRjbgglRoleParam(params,ContextUtil.getCurrentUserId(), ContextUtil.getCurrentUser().getUserNo());
        List<JSONObject> rjbgglList = rjbgglDao.queryRjbggl(params);
        for (JSONObject rjbggl : rjbgglList) {
            if (rjbggl.get("CREATE_TIME_") != null) {
                rjbggl.put("CREATE_TIME_", DateUtil.formatDate((Date) rjbggl.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        rdmZhglUtil.setTaskInfo2Data(rjbgglList, ContextUtil.getCurrentUserId());
        result.setData(rjbgglList);
        result.setTotal(rjbgglList.size());
        return result;
    }

    public void createRjbggl(JSONObject formData) {
        formData.put("id", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        rjbgglDao.createRjbggl(formData);
    }

    public void updateRjbggl(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        rjbgglDao.updateRjbggl(formData);
        if (StringUtils.isNotBlank(formData.getString("szr"))) {
            JSONArray tdmcDataJson = JSONObject.parseArray(formData.getString("szr"));
            for (int i = 0; i < tdmcDataJson.size(); i++) {
                JSONObject oneObject = tdmcDataJson.getJSONObject(i);
                String state = oneObject.getString("_state");
                String id = oneObject.getString("id");
                if ("added".equals(state) || StringUtils.isBlank(id)) {
                    oneObject.put("id", IdUtil.getId());
                    oneObject.put("belongId", formData.getString("id"));
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", new Date());
                    rjbgglDao.createSzr(oneObject);
                } else if ("modified".equals(state)) {
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", new Date());
                    rjbgglDao.updateSzr(oneObject);
                } else if ("removed".equals(state)) {
                    Map<String, Object> param = new HashMap<>();
                    param.put("id", oneObject.getString("id"));
                    rjbgglDao.deleteSzr(param);
                }
            }
        }
        if (StringUtils.isNotBlank(formData.getString("jsy"))) {
            JSONArray tdmcDataJson = JSONObject.parseArray(formData.getString("jsy"));
            for (int i = 0; i < tdmcDataJson.size(); i++) {
                JSONObject oneObject = tdmcDataJson.getJSONObject(i);
                String state = oneObject.getString("_state");
                String id = oneObject.getString("id");
                if ("added".equals(state) || StringUtils.isBlank(id)) {
                    oneObject.put("id", IdUtil.getId());
                    oneObject.put("belongId", formData.getString("id"));
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", new Date());
                    rjbgglDao.createJsy(oneObject);
                } else if ("modified".equals(state)) {
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", new Date());
                    rjbgglDao.updateJsy(oneObject);
                } else if ("removed".equals(state)) {
                    Map<String, Object> param = new HashMap<>();
                    param.put("id", oneObject.getString("id"));
                    rjbgglDao.deleteJsy(param);
                }
            }
        }
        if (StringUtils.isNotBlank(formData.getString("model"))) {
            JSONArray tdmcDataJson = JSONObject.parseArray(formData.getString("model"));
            for (int i = 0; i < tdmcDataJson.size(); i++) {
                JSONObject oneObject = tdmcDataJson.getJSONObject(i);
                String state = oneObject.getString("_state");
                String id = oneObject.getString("id");
                if ("added".equals(state) || StringUtils.isBlank(id)) {
                    oneObject.put("id", IdUtil.getId());
                    oneObject.put("belongId", formData.getString("id"));
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", new Date());
                    rjbgglDao.createModel(oneObject);
                } else if ("modified".equals(state)) {
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", new Date());
                    rjbgglDao.updateModel(oneObject);
                } else if ("removed".equals(state)) {
                    Map<String, Object> param = new HashMap<>();
                    param.put("id", oneObject.getString("id"));
                    rjbgglDao.deleteModel(param);
                }
            }
        }
    }

    public List<JSONObject> querySzr(String belongId) {
        Map<String, Object> param = new HashMap<>();
        param.put("belongId", belongId);
        List<JSONObject> szrList = rjbgglDao.querySzr(param);
        return szrList;
    }

    public List<JSONObject> queryJsy(String belongId) {
        Map<String, Object> param = new HashMap<>();
        param.put("belongId", belongId);
        List<JSONObject> szrList = rjbgglDao.queryJsy(param);
        return szrList;
    }

    public List<JSONObject> queryModel(String belongId) {
        Map<String, Object> param = new HashMap<>();
        param.put("belongId", belongId);
        List<JSONObject> modelList = rjbgglDao.queryModel(param);
        return modelList;
    }
    
    public JSONObject getRjbggl(String id) {
        JSONObject detailObj = rjbgglDao.queryRjbgglById(id);
        if (detailObj == null) {
            return new JSONObject();
        }
        if (detailObj.getDate("CREATE_TIME_") != null) {
            detailObj.put("CREATE_TIME_", DateUtil.formatDate(detailObj.getDate("CREATE_TIME_"), "yyyy-MM-dd"));
        }
        return detailObj;
    }

    private String toGetParamVal(String[] paramValArr) {
        if (paramValArr == null || paramValArr.length == 0) {
            return null;
        }
        return paramValArr[0];
    }
    
    
    public JsonResult deleteRjbggl(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        Map<String, Object> param = new HashMap<>();
        param.put("ids", Arrays.asList(ids));
        rjbgglDao.deleteRjbggl(param);
        rjbgglDao.deleteSzr(param);
        rjbgglDao.deleteJsy(param);
        rjbgglDao.deleteModel(param);
        return result;
    }


}
