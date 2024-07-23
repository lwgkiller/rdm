package com.redxun.serviceEngineering.core.service;

import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.query.Page;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.serviceEngineering.core.dao.ZxdpsDao;

/**
 * 操保手册制修订评审申请
 *
 * @mh 2022年7月14日15:41:58
 */

@Service
public class ZxdpsManager {
    private static final Logger logger = LoggerFactory.getLogger(ZxdpsManager.class);
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private ZxdpsDao zxdpsDao;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;

    public JsonPageResult<?> queryApplyList(HttpServletRequest request, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        rdmZhglUtil.addOrder(request, params, "service_engineering_maintenance_manualfile_review_module.CREATE_TIME_",
            "desc");
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

        // 增加角色过滤的条件
        addRoleParam(params, ContextUtil.getCurrentUserId(), ContextUtil.getCurrentUser().getUserNo());

        List<JSONObject> applyList = zxdpsDao.queryApplyList(params);
        for (JSONObject oneApply : applyList) {
            if (oneApply.get("CREATE_TIME_") != null) {
                oneApply.put("CREATE_TIME_", DateUtil.formatDate((Date)oneApply.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
            if (oneApply.get("applyTime") != null) {
                oneApply.put("applyTime", DateUtil.formatDate((Date)oneApply.get("applyTime"), "yyyy-MM-dd HH:mm:ss"));
            }
        }
        rdmZhglUtil.setTaskInfo2Data(applyList, ContextUtil.getCurrentUserId());

        // 根据分页进行subList截取
        List<JSONObject> finalSubList = new ArrayList<JSONObject>();
        if (doPage) {
            int pageIndex = RequestUtil.getInt(request, "pageIndex", 0);
            int pageSize = RequestUtil.getInt(request, "pageSize", Page.DEFAULT_PAGE_SIZE);
            int startSubListIndex = pageIndex * pageSize;
            int endSubListIndex = startSubListIndex + pageSize;
            if (startSubListIndex < applyList.size()) {
                finalSubList = applyList.subList(startSubListIndex,
                    endSubListIndex < applyList.size() ? endSubListIndex : applyList.size());
            }
        } else {
            finalSubList = applyList;
        }
        result.setData(finalSubList);
        result.setTotal(applyList.size());
        return result;
    }

    public JSONObject queryApplyDetail(String id) {
        JSONObject result = new JSONObject();
        if (StringUtils.isBlank(id)) {
            return result;
        }
        JSONObject params = new JSONObject();
        params.put("id", id);
        JSONObject obj = zxdpsDao.queryApplyDetail(params);
        if (obj == null) {
            return result;
        }
        if (obj.get("applyTime") != null) {
            obj.put("applyTime", DateUtil.formatDate((Date)obj.get("applyTime"), "yyyy-MM-dd HH:mm:ss"));
        }
        return obj;
    }

    private void addRoleParam(Map<String, Object> params, String userId, String userNo) {
        if (userNo.equalsIgnoreCase("admin")) {
            return;
        }
        params.put("currentUserId", userId);
        params.put("roleName", "other");
    }

    public void createZxdps(JSONObject formData) {
        formData.put("id", IdUtil.getId());
        formData.put("creatorDeptId", ContextUtil.getCurrentUser().getMainGroupId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("creatorName", ContextUtil.getCurrentUser().getFullname());

        formData.put("CREATE_TIME_", new Date());
        // 基本信息
        zxdpsDao.insertZxdps(formData);

        // topic信息
        topicProcess(formData.getString("id"), formData.getJSONArray("changeTopicGrid"));

        // 审核意见
        demandProcess(formData.getString("id"), formData.getJSONArray("changeOpinionGrid"));

    }

    public void updateZxdps(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        // 基本信息
        zxdpsDao.updateZxdps(formData);

        // topic信息
        topicProcess(formData.getString("id"), formData.getJSONArray("changeTopicGrid"));

        // 审核意见
        demandProcess(formData.getString("id"), formData.getJSONArray("changeOpinionGrid"));

    }

    public JsonResult delApplys(String[] applyIdArr) {
        List<String> applyIdList = Arrays.asList(applyIdArr);
        JsonResult result = new JsonResult(true, "操作成功！");
        if (applyIdArr.length == 0) {
            return result;
        }
        JSONObject param = new JSONObject();
        param.put("applyIds", applyIdList);

        // 删除子表

        zxdpsDao.deleteOpinion(param);

        // 删除主表
        param.put("ids", applyIdList);
        zxdpsDao.deleteZxdps(param);

        return result;
    }

    public List<JSONObject> queryDemandList(JSONObject params) {
        List<JSONObject> demandList = zxdpsDao.queryFileList(params);
        return demandList;
    }

    public List<JSONObject> queryOpinionList(JSONObject params) {
        List<JSONObject> opinionList = zxdpsDao.queryOpinionList(params);
        for (JSONObject oneObj : opinionList) {
            if (oneObj.get("reviewTime") != null) {
                oneObj.put("reviewTime", DateUtil.formatDate((Date)oneObj.get("reviewTime"), "yyyy-MM-dd HH:mm:ss"));
            }
        }

        return opinionList;
    }

    private void demandProcess(String applyId, JSONArray demandArr) {
        // gridType: 表格类型 共三种名称，对应三个子表

        if (demandArr == null || demandArr.isEmpty()) {
            return;
        }
        JSONObject param = new JSONObject();
        param.put("ids", new JSONArray());
        for (int index = 0; index < demandArr.size(); index++) {
            JSONObject oneObject = demandArr.getJSONObject(index);
            String id = oneObject.getString("id");
            String state = oneObject.getString("_state");

            if ("added".equals(state)) {
                oneObject.put("id", IdUtil.getId());
                oneObject.put("applyId", applyId);
                oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                oneObject.put("CREATE_TIME_", new Date());
                zxdpsDao.insertOpinion(oneObject);

            } else if ("removed".equals(state)) {
                // 删除
                param.getJSONArray("ids").add(oneObject.getString("id"));
            } else if ("modified".equals(state)) {
                // 更新
                oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                oneObject.put("UPDATE_TIME_", DateFormatUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                zxdpsDao.updateOpinion(oneObject);
            }
        }
        if (!param.getJSONArray("ids").isEmpty()) {
            zxdpsDao.deleteOpinion(param);

        }
    }

    private void topicProcess(String applyId, JSONArray demandArr) {

        if (demandArr == null || demandArr.isEmpty()) {
            return;
        }

        JSONObject param = new JSONObject();
        param.put("ids", new JSONArray());
        for (int index = 0; index < demandArr.size(); index++) {
            JSONObject oneObject = demandArr.getJSONObject(index);
            String id = oneObject.getString("id");
            String state = oneObject.getString("_state");

            if ("added".equals(state)) {
                oneObject.put("id", IdUtil.getId());
                oneObject.put("applyId", applyId);
                oneObject.put("topicId", id);
                oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                oneObject.put("CREATE_TIME_", new Date());
                zxdpsDao.insertTopic(oneObject);
            } else if ("removed".equals(state)) {
                // 删除
                param.getJSONArray("ids").add(oneObject.getString("id"));
            }
        }
        if (!param.getJSONArray("ids").isEmpty()) {
            zxdpsDao.deleteTopic(param);
        }
    }

    public List<JSONObject> queryTopicList(JSONObject params) {
        List<JSONObject> demandList = zxdpsDao.queryTopicList(params);
        return demandList;
    }

}
