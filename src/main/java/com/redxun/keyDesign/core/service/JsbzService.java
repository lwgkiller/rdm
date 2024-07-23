package com.redxun.keyDesign.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateUtil;
import com.redxun.keyDesign.core.dao.JsbzDao;
import com.redxun.rdmCommon.core.manager.LoginRecordManager;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class JsbzService {
    private Logger logger = LogManager.getLogger(JsbzService.class);
    @Autowired
    private JsbzDao jsbzDao;
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

    public JsonPageResult<?> queryJsbz(HttpServletRequest request, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        String belongbj = RequestUtil.getString(request, "belongbj");
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
                    if ("codeNames".equalsIgnoreCase(name)) {
                        JSONArray systemIdArr = JSONArray.parseArray(value);
                        if (systemIdArr != null && !systemIdArr.isEmpty()) {
                            params.put(name, systemIdArr.toJavaList(String.class));
                        }
                    } else {
                        params.put(name, value);
                    }
                }
            }
        }
        if (doPage) {
            rdmZhglUtil.addPage(request, params);
        }
        params.put("currentUserId", ContextUtil.getCurrentUserId());
        params.put("belongbj", belongbj);
        // addJsbzRoleParam(params,ContextUtil.getCurrentUserId(), ContextUtil.getCurrentUser().getUserNo());
        List<JSONObject> jsbzList = jsbzDao.queryJsbz(params);
        for (JSONObject jsbz : jsbzList) {
            if (jsbz.get("CREATE_TIME_") != null) {
                jsbz.put("CREATE_TIME_", DateUtil.formatDate((Date)jsbz.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        result.setData(jsbzList);
        int countJsbzDataList = jsbzDao.countJsbzList(params);
        result.setTotal(countJsbzDataList);
        return result;
    }

    public void createJsbz(JSONObject formData) {
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        jsbzDao.createJsbz(formData);
    }

    public JsonResult deleteJsbz(String[] jsIdsArr, String[] belongbjArr) {
        JsonResult result = new JsonResult(true, "操作成功");
        Map<String, Object> param = new HashMap<>();
        String belongbj = belongbjArr[0];
        for (String jsId : jsIdsArr) {
            // 删除基本信息
            param.clear();
            param.put("standardId", jsId);
            param.put("belongbj", belongbj);
            jsbzDao.deleteJsbz(param);
        }
        return result;
    }

    public JsonPageResult<?> queryMsg(HttpServletRequest request, boolean doPage) {
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
        params.put("belongbj", RequestUtil.getString(request, "belongbj", ""));
        // addMsgRoleParam(params,ContextUtil.getCurrentUserId(), ContextUtil.getCurrentUser().getUserNo());
        List<JSONObject> msgList = jsbzDao.queryMsg(params);
        for (JSONObject msg : msgList) {
            if (msg.getDate("CREATE_TIME_") != null) {
                msg.put("CREATE_TIME_", DateUtil.formatDate(msg.getDate("CREATE_TIME_"), "yyyy-MM-dd"));
            }

        }
        result.setData(msgList);
        int countMsgDataList = jsbzDao.countMsgList(params);
        result.setTotal(countMsgDataList);
        return result;
    }

    public JSONObject getMsgById(String msgId) {
        JSONObject detailObj = jsbzDao.queryMsgById(msgId);
        if (detailObj == null) {
            return new JSONObject();
        }
        return detailObj;
    }

    public List<JSONObject> getMsgDetailList(HttpServletRequest request) {
        String belongId = RequestUtil.getString(request, "belongId", "");
        Map<String, Object> param = new HashMap<>();
        param.put("belongId", belongId);
        List<JSONObject> msgDetailList = jsbzDao.queryMsgDetail(param);
        for (Map<String, Object> qbgz : msgDetailList) {
            if (qbgz.get("CREATE_TIME_") != null) {
                qbgz.put("CREATE_TIME_", DateUtil.formatDate((Date)qbgz.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        return msgDetailList;
    }

    public void createMsg(JSONObject formData) {
        formData.put("msgId", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        if (StringUtils.isNotBlank(formData.getString("ryzj"))) {
            JSONArray reasonDataJson = JSONObject.parseArray(formData.getString("ryzj"));
            for (int i = 0; i < reasonDataJson.size(); i++) {
                JSONObject oneObject = reasonDataJson.getJSONObject(i);
                String state = oneObject.getString("_state");
                String linkId = oneObject.getString("linkId");
                if ("added".equals(state) || StringUtils.isBlank(linkId)) {
                    // 新增
                    oneObject.put("linkId", IdUtil.getId());
                    oneObject.put("belongId", formData.getString("msgId"));
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", new Date());
                    jsbzDao.createMsgDetail(oneObject);
                } else if ("modified".equals(state)) {
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", new Date());
                    jsbzDao.updateMsgDetail(oneObject);
                } else if ("removed".equals(state)) {
                    Map<String, Object> param = new HashMap<>();
                    param.put("linkId", oneObject.getString("linkId"));
                    jsbzDao.deleteMsgDetail(param);
                }
            }
        }
        jsbzDao.createMsg(formData);
    }

    public void updateMsg(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        formData.put("linkaction", "已关联");
        if (StringUtils.isNotBlank(formData.getString("ryzj"))) {
            JSONArray reasonDataJson = JSONObject.parseArray(formData.getString("ryzj"));
            for (int i = 0; i < reasonDataJson.size(); i++) {
                JSONObject oneObject = reasonDataJson.getJSONObject(i);
                String state = oneObject.getString("_state");
                String linkId = oneObject.getString("linkId");
                if ("added".equals(state) || StringUtils.isBlank(linkId)) {
                    // 新增
                    oneObject.put("linkId", IdUtil.getId());
                    oneObject.put("belongId", formData.getString("msgId"));
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", new Date());
                    jsbzDao.createMsgDetail(oneObject);
                } else if ("modified".equals(state)) {
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", new Date());
                    jsbzDao.updateMsgDetail(oneObject);
                } else if ("removed".equals(state)) {
                    Map<String, Object> param = new HashMap<>();
                    param.put("linkId", oneObject.getString("linkId"));
                    jsbzDao.deleteMsgDetail(param);
                }
            }
        }
        jsbzDao.updateMsg(formData);
    }

    public JsonResult deleteMsgDetail(String[] MsgIdsArr) {
        JsonResult result = new JsonResult(true, "操作成功");
        Map<String, Object> param = new HashMap<>();
        for (String linkId : MsgIdsArr) {
            // 删除基本信息
            param.clear();
            param.put("linkId", linkId);
            jsbzDao.deleteMsgDetail(param);
        }
        return result;
    }

    public JsonResult deleteMsg(String msgId) {
        JsonResult result = new JsonResult(true, "操作成功");
        Map<String, Object> param = new HashMap<>();
        param.put("belongId", msgId);
        jsbzDao.deleteMsgDetail(param);
        param.clear();
        param.put("msgId", msgId);
        jsbzDao.deleteMsg(param);
        return result;
    }

}
