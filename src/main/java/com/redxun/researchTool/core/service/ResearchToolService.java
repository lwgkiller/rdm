package com.redxun.researchTool.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.query.Page;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.researchTool.core.dao.ResearchToolDao;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import groovy.util.logging.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Service
@Slf4j
public class ResearchToolService {
    private Logger logger = LogManager.getLogger(ResearchToolService.class);
    @Autowired
    private ResearchToolDao researchToolDao;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;

    public JsonPageResult<?> queryTool(HttpServletRequest request, HttpServletResponse response, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
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
        // 增加分页条件
        List<Map<String, Object>> toolList = researchToolDao.queryTool(params);
        List<Map<String, Object>> finalSubList = new ArrayList<Map<String, Object>>();
        if (doPage) {
            // 根据分页进行subList截取
            int pageIndex = RequestUtil.getInt(request, "pageIndex", 0);
            int pageSize = RequestUtil.getInt(request, "pageSize", Page.DEFAULT_PAGE_SIZE);
            int startSubListIndex = pageIndex * pageSize;
            int endSubListIndex = startSubListIndex + pageSize;
            if (startSubListIndex < toolList.size()) {
                finalSubList = toolList.subList(startSubListIndex,
                        endSubListIndex < toolList.size() ? endSubListIndex : toolList.size());
            }
        } else {
            finalSubList = toolList;
        }
        result.setData(finalSubList);
        result.setTotal(toolList.size());
        return result;
    }

    public void insertTool(JSONObject formData) {
        formData.put("toolid", IdUtil.getId());
        formData.put("type", "fztool");
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        researchToolDao.insertTool(formData);
    }

    public void updateTool(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        researchToolDao.updateTool(formData);
    }


    public JSONObject toolDetail(String toolid) {
        JSONObject detailObj = researchToolDao.queryToolById(toolid);
        if (detailObj == null) {
            return new JSONObject();
        }

        return detailObj;
    }

    public JsonResult deletetool(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> toolids = Arrays.asList(ids);
        Map<String, Object> param = new HashMap<>();
        param.put("toolids", toolids);
        researchToolDao.deletetool(param);
        return result;
    }
    public JsonPageResult<?> queryTrain(HttpServletRequest request, HttpServletResponse response, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
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
        // 增加分页条件
        List<Map<String, Object>> trainList = researchToolDao.queryTrain(params);
        List<Map<String, Object>> finalSubList = new ArrayList<Map<String, Object>>();
        if (doPage) {
            // 根据分页进行subList截取
            int pageIndex = RequestUtil.getInt(request, "pageIndex", 0);
            int pageSize = RequestUtil.getInt(request, "pageSize", Page.DEFAULT_PAGE_SIZE);
            int startSubListIndex = pageIndex * pageSize;
            int endSubListIndex = startSubListIndex + pageSize;
            if (startSubListIndex < trainList.size()) {
                finalSubList = trainList.subList(startSubListIndex,
                        endSubListIndex < trainList.size() ? endSubListIndex : trainList.size());
            }
        } else {
            finalSubList = trainList;
        }
        result.setData(finalSubList);
        result.setTotal(trainList.size());
        return result;
    }
}
