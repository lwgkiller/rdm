package com.redxun.productDataManagement.core.manager;

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
import com.redxun.core.util.DateUtil;
import com.redxun.productDataManagement.core.dao.CpxhsqDao;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectMessageDao;

@Service
public class CpxhsqManager {
    private static final Logger logger = LoggerFactory.getLogger(CpxhsqManager.class);
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private CpxhsqDao cpxhsqDao;
    @Autowired
    private ProductSpectrumService productSpectrumService;

    public JsonPageResult<?> queryApplyList(HttpServletRequest request, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        rdmZhglUtil.addOrder(request, params, "product_design_model_apply_base.CREATE_TIME_", "desc");
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

        List<JSONObject> applyList = cpxhsqDao.queryApplyList(params);
        for (JSONObject oneApply : applyList) {
            if (oneApply.get("CREATE_TIME_") != null) {
                oneApply.put("CREATE_TIME_", DateUtil.formatDate((Date)oneApply.get("CREATE_TIME_"), "yyyy-MM-dd"));
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
        JSONObject obj = cpxhsqDao.queryApplyDetail(params);
        if (obj == null) {
            return result;
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

    public void createCpxhsq(JSONObject formData) {
        formData.put("id", IdUtil.getId());
        formData.put("departId", ContextUtil.getCurrentUser().getMainGroupId());
        formData.put("creatorName", ContextUtil.getCurrentUser().getFullname());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        // 基本信息
        cpxhsqDao.insertCpxhsq(formData);

    }

    public void updateCpxhsq(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        // 基本信息
        cpxhsqDao.updateCpxhsq(formData);
    }

    public JsonResult delApplys(String[] applyIdArr) {
        List<String> applyIdList = Arrays.asList(applyIdArr);
        JsonResult result = new JsonResult(true, "操作成功！");
        if (applyIdArr.length == 0) {
            return result;
        }
        JSONObject param = new JSONObject();
        param.put("applyIds", applyIdList);

        // 删除主表
        param.put("ids", applyIdList);
        cpxhsqDao.deleteCpxhsq(param);
        return result;
    }

    public void checkDesignModelValid(JSONObject param, JsonResult result) {
        // 先检查设计型号申请表中有无重复数据
        List<JSONObject> temp = cpxhsqDao.queryDesignModelValid(param);
        if (temp != null && !temp.isEmpty()) {
            result.setSuccess(false);
            result.setMessage("当前已存在相同设计型号的申请，不允许重复申请！");
            return;
        }
        // 再检查型谱中有没有相同设计型号的数据
        param.remove("id");
        boolean productSpectrumExist = productSpectrumService.checkDesignModelExist(param);
        if (productSpectrumExist) {
            result.setSuccess(false);
            result.setMessage("产品型谱中已经存在该设计型号，不允许重复申请！");
            return;
        }
    }

}
