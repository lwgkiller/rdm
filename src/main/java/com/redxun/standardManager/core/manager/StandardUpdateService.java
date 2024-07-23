package com.redxun.standardManager.core.manager;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmCommon.core.manager.LoginRecordManager;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.standardManager.core.dao.StandardUpdateDao;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import groovy.util.logging.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class StandardUpdateService {
    private Logger logger = LogManager.getLogger(StandardUpdateService.class);
    @Autowired
    private StandardUpdateDao standardUpdateDao;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private LoginRecordManager loginRecordManager;

    public JsonPageResult<?> queryGzjl(HttpServletRequest request, boolean doPage) {
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
        params.put("currentUserId",ContextUtil.getCurrentUserId());
        params.put("belongbz",RequestUtil.getString(request,"belongbz",""));
        //addGzjlRoleParam(params,ContextUtil.getCurrentUserId(), ContextUtil.getCurrentUser().getUserNo());
        List<JSONObject> msgList = standardUpdateDao.queryGzjl(params);
        for (JSONObject msg : msgList) {
            if (msg.getDate("CREATE_TIME_") != null) {
                msg.put("CREATE_TIME_", DateUtil.formatDate(msg.getDate("CREATE_TIME_"), "yyyy-MM-dd"));
            }

        }
        result.setData(msgList);
        int countGzjlDataList = standardUpdateDao.countGzjlList(params);
        result.setTotal(countGzjlDataList);
        return result;
    }

    public JSONObject getGzjlById(String updateId) {
        JSONObject detailObj = standardUpdateDao.queryGzjlById(updateId);
        if (detailObj == null) {
            return new JSONObject();
        }
        return detailObj;
    }
    public List<JSONObject> getGzjlDetailList(HttpServletRequest request) {
        String belongId = RequestUtil.getString(request, "belongId", "");
        Map<String, Object> param = new HashMap<>();
        param.put("belongId", belongId);
        List<JSONObject> msgDetailList = standardUpdateDao.queryGzjlDetail(param);
        for (JSONObject qbgz : msgDetailList) {
            if (StringUtils.isNotBlank(qbgz.getString("CREATE_TIME_"))) {
                qbgz.put("CREATE_TIME_", DateUtil.formatDate((Date)qbgz.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
//            if(StringUtils.isNotBlank(qbgz.getString("updateNote"))&&qbgz.getString("updateNote").equals("发布时间")){
//                if(StringUtils.isNotBlank(qbgz.getString("oldNote"))){
//                    qbgz.put("oldNote", DateUtil.formatDate(DateUtil.parseDate(qbgz.getString("oldNote")), "yyyy-MM-dd HH:mm:ss"));
//                }
//                if(StringUtils.isNotBlank(qbgz.getString("newNote"))){
//                    qbgz.put("newNote", DateUtil.formatDate(DateUtil.parseDate(qbgz.getString("newNote")), "yyyy-MM-dd HH:mm:ss"));
//                }
//            }
        }
        return msgDetailList;
    }

    public void createGzjl(JSONObject formData) {
        formData.put("updateId", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        if (StringUtils.isNotBlank(formData.getString("ryzj"))) {
            JSONArray reasonDataJson = JSONObject.parseArray(formData.getString("ryzj"));
            for (int i = 0; i < reasonDataJson.size(); i++) {
                JSONObject oneObject = reasonDataJson.getJSONObject(i);
                String state = oneObject.getString("_state");
                String detailId = oneObject.getString("detailId");
                if ("added".equals(state) || StringUtils.isBlank(detailId)) {
                    //新增
                    oneObject.put("detailId", IdUtil.getId());
                    oneObject.put("belongId", formData.getString("updateId"));
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", new Date());
                    standardUpdateDao.createGzjlDetail(oneObject);
                } else if ("modified".equals(state)) {
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", new Date());
                    standardUpdateDao.updateGzjlDetail(oneObject);
                }else if ("removed".equals(state)) {
                    Map<String, Object> param = new HashMap<>();
                    param.put("detailId", oneObject.getString("detailId"));
                    standardUpdateDao.deleteGzjlDetail(param);
                }
            }
        }
        standardUpdateDao.createGzjl(formData);
    }
    public void updateGzjl(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        if (StringUtils.isNotBlank(formData.getString("ryzj"))) {
            JSONArray reasonDataJson = JSONObject.parseArray(formData.getString("ryzj"));
            for (int i = 0; i < reasonDataJson.size(); i++) {
                JSONObject oneObject = reasonDataJson.getJSONObject(i);
                String state = oneObject.getString("_state");
                String detailId = oneObject.getString("detailId");
                if ("added".equals(state) || StringUtils.isBlank(detailId)) {
                    //新增
                    oneObject.put("detailId", IdUtil.getId());
                    oneObject.put("belongId", formData.getString("updateId"));
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", new Date());
                    standardUpdateDao.createGzjlDetail(oneObject);
                } else if ("modified".equals(state)) {
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", new Date());
                    standardUpdateDao.updateGzjlDetail(oneObject);
                }else if ("removed".equals(state)) {
                    Map<String, Object> param = new HashMap<>();
                    param.put("detailId", oneObject.getString("detailId"));
                    standardUpdateDao.deleteGzjlDetail(param);
                }
            }
        }
        standardUpdateDao.updateGzjl(formData);
    }

    public JsonResult deleteGzjlDetail(String[] GzjlIdsArr) {
        JsonResult result = new JsonResult(true, "操作成功");
        Map<String, Object> param = new HashMap<>();
        for (String detailId : GzjlIdsArr) {
            // 删除基本信息
            param.clear();
            param.put("detailId", detailId);
            standardUpdateDao.deleteGzjlDetail(param);
        }
        return result;
    }

    public JsonResult deleteGzjl(String updateId) {
        JsonResult result = new JsonResult(true, "操作成功");
        Map<String, Object> param = new HashMap<>();
        param.put("belongId",updateId);
        standardUpdateDao.deleteGzjlDetail(param);
        param.clear();
        param.put("updateId",updateId);
        standardUpdateDao.deleteGzjl(param);
        return result;
    }


}
