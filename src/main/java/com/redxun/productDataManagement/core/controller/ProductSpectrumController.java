package com.redxun.productDataManagement.core.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.productDataManagement.core.dao.ProductSpectrumDao;
import com.redxun.productDataManagement.core.manager.ProductSpectrumService;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

/**
 * 产品型谱
 *
 * @author mh
 * @date 2022年10月13日10:02:28
 */

@Controller
@RequestMapping("/world/core/productSpectrum")
public class ProductSpectrumController {
    private static final Logger logger = LoggerFactory.getLogger(ProductSpectrumController.class);
    @Autowired
    ProductSpectrumService productSpectrumService;
    @Autowired
    RdmZhglUtil rdmZhglUtil;
    @Resource
    private BpmInstManager bpmInstManager;
    @Autowired
    private CommonBpmManager commonBpmManager;
    @Autowired
    private ProductSpectrumDao productSpectrumDao;

    // ..
    @RequestMapping("dataListPage")
    public ModelAndView dataListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "productDataManagement/core/productSpectrumList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String menuType = request.getParameter("menuType");
        boolean isCpxp = rdmZhglUtil.judgeIsPointRoleUser(ContextUtil.getCurrentUserId(), "产品型谱新增权限");
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        mv.addObject("menuType", menuType);
        mv.addObject("isCpxp", isCpxp);
        return mv;
    }

    // ..
    @RequestMapping("dataListQuery")
    @ResponseBody
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) {
        return productSpectrumService.dataListQuery(request, response);
    }

    @RequestMapping("applyList")
    @ResponseBody
    public JsonPageResult<?> queryApplyList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return productSpectrumService.queryApplyList(request, true);
    }

    // ..
    @RequestMapping("EditPage")
    public ModelAndView EditPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "productDataManagement/core/productSpectrumEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String id = RequestUtil.getString(request, "id");
        String menuType = RequestUtil.getString(request, "menuType");
        String applyId = RequestUtil.getString(request, "applyId", "");
        String nodeId = RequestUtil.getString(request, "nodeId", "");
        String action = RequestUtil.getString(request, "action", "");
        String status = RequestUtil.getString(request, "status", "");
        String changeType = RequestUtil.getString(request, "changeType", "");
        if (StringUtils.isBlank(action)) {
            // 新增或编辑的时候没有nodeId
            if (StringUtils.isBlank(nodeId) || nodeId.contains("PROCESS")) {
                action = "edit";
            } else {
                // 处理任务的时候有nodeId
                action = "task";
            }
        }
        // 取出节点变量，返回到页面
        if (StringUtils.isNotBlank(nodeId)) {
            List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId, "CPXPFB", null);
            if (nodeVars != null && !nodeVars.isEmpty()) {
                mv.addObject("nodeVars", JSONObject.toJSONString(nodeVars));
            }
        }
        JSONObject obj = new JSONObject();
        if (StringUtils.isNotBlank(id)) {
            obj = productSpectrumService.queryDataById(id);
        }
        obj.put("CREATE_BY_", ContextUtil.getCurrentUser().getUserId());
        obj.put("creatorName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("obj", obj).addObject("businessId", obj.getString("id"));
        mv.addObject("action", action);
        mv.addObject("menuType", menuType);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("productModel", obj.getString("designModel"));
        mv.addObject("changeType", changeType);

        return mv;
    }

    // ..
    @RequestMapping("deleteBusiness")
    @ResponseBody
    public JSONObject deleteBusiness(HttpServletRequest request, @RequestBody String requestBody,
        HttpServletResponse response) {
        JSONObject result = new JSONObject();
        JSONObject requestBodyObj = JSONObject.parseObject(requestBody);
        String id = requestBodyObj.getString("id");
        productSpectrumService.deleteBusiness(result, id);
        return result;
    }

    @RequestMapping("deleteApply")
    @ResponseBody
    public JsonResult deleteApply(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String instIdStr = RequestUtil.getString(request, "instIds");
            if (StringUtils.isNotBlank(instIdStr)) {
                String[] instIds = instIdStr.split(",");
                for (int index = 0; index < instIds.length; index++) {
                    bpmInstManager.deleteCascade(instIds[index], "");
                }
            }
            String[] ids = uIdStr.split(",");
            return productSpectrumService.delApplys(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteApply", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("saveBusiness")
    @ResponseBody
    public JsonResult saveBusiness(HttpServletRequest request, @RequestBody String formDataStr,
        HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "");
        if (StringUtils.isBlank(formDataStr)) {
            logger.warn("requestBody is blank");
            result.setSuccess(false);
            result.setMessage("requestBody is blank");
            return result;
        }
        try {
            JSONObject formDataJson = JSONObject.parseObject(formDataStr);
            if (formDataJson == null || formDataJson.isEmpty()) {
                logger.warn("formData is blank");
                result.setSuccess(false);
                result.setMessage("requestBody is blank");
                return result;
            }
            if (StringUtils.isBlank(formDataJson.getString("id"))) {
                productSpectrumService.createSpectrum(formDataJson);
            } else {
                productSpectrumService.updateSpectrum(formDataJson);
            }
        } catch (Exception e) {
            logger.error("Exception in save productSpectrum", e);
            result.setSuccess(false);
            result.setMessage("Exception in save productSpectrum");
            return result;
        }
        return result;
    }

    @RequestMapping("exportData")
    public void exportData(HttpServletRequest request, HttpServletResponse response) {
        productSpectrumService.exportData(request, response);
    }

    @RequestMapping("exportSetting")
    public void exportSetting(HttpServletRequest request, HttpServletResponse response) {
        productSpectrumService.exportMainSetting(request, response);
    }

    @RequestMapping("exportParam")
    public void exportParam(HttpServletRequest request, HttpServletResponse response) {
        productSpectrumService.exportMainParam(request, response);
    }

    @RequestMapping("monthList")
    @ResponseBody
    public List<JSONObject> monthList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String applyId = RequestUtil.getString(request, "applyId", "");
        if (StringUtils.isBlank(applyId)) {
            logger.error("applyId is blank");
            return null;
        }
        JSONObject params = new JSONObject();
        params.put("applyId", applyId);
        List<JSONObject> monthList = productSpectrumService.queryMonthStatusList(params);
        return monthList;
    }

    @RequestMapping("mainSettingList")
    @ResponseBody
    public List<JSONObject> mainSettingList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String applyId = RequestUtil.getString(request, "applyId", "");
        if (StringUtils.isBlank(applyId)) {
            logger.error("applyId is blank");
            return null;
        }
        JSONObject params = new JSONObject();
        params.put("applyId", applyId);
        List<JSONObject> mainSettingList = productSpectrumService.queryMainSettingList(params);
        return mainSettingList;
    }

    @RequestMapping("mainParamList")
    @ResponseBody
    public List<JSONObject> mainParamList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String applyId = RequestUtil.getString(request, "applyId", "");
        if (StringUtils.isBlank(applyId)) {
            logger.error("applyId is blank");
            return null;
        }
        JSONObject params = new JSONObject();
        params.put("applyId", applyId);
        List<JSONObject> mainParamList = productSpectrumService.queryMainParamList(params);
        return mainParamList;
    }

    @RequestMapping("manualChangeList")
    @ResponseBody
    public List<JSONObject> manualChangeList(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        String applyId = RequestUtil.getString(request, "applyId", "");
        if (StringUtils.isBlank(applyId)) {
            logger.error("applyId is blank");
            return null;
        }
        JSONObject params = new JSONObject();
        params.put("applyId", applyId);
        List<JSONObject> manualChangeList = productSpectrumService.queryManualChangeList(params);
        return manualChangeList;
    }

    @RequestMapping("workDeviceList")
    @ResponseBody
    public List<JSONObject> workDeviceList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String applyId = RequestUtil.getString(request, "applyId", "");
        if (StringUtils.isBlank(applyId)) {
            logger.error("applyId is blank");
            return null;
        }
        JSONObject params = new JSONObject();
        params.put("applyId", applyId);
        List<JSONObject> manualChangeList = productSpectrumService.queryWorkDeviceList(params);
        return manualChangeList;
    }

    @RequestMapping("tagListGrid")
    @ResponseBody
    public List<JSONObject> tagListGrid(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String applyId = RequestUtil.getString(request, "applyId", "");
        String tagIdsStr = RequestUtil.getString(request, "tagIds", "");
        if (StringUtils.isBlank(applyId)) {
            logger.error("applyId is blank");
            return null;
        }

        if (StringUtils.isNotEmpty(tagIdsStr) && StringUtils.isNotEmpty(tagIdsStr)) {
            String[] tagIds = tagIdsStr.split(",");
            JSONObject params = new JSONObject();
            params.put("id", applyId);
            params.put("tagIds", tagIds);
            params.put("type", "CPXPBQ");
            List<JSONObject> tagListGrid = productSpectrumService.tagListGrid(params);
            return tagListGrid;
        }
        return null;
    }

    @RequestMapping("tagListQuery")
    @ResponseBody
    public JsonPageResult<?> tagListQuery(HttpServletRequest request, HttpServletResponse response) {
        return productSpectrumService.tagListQuery(request, response);
    }

    @RequestMapping("tagType")
    @ResponseBody
    public List<Map<String, Object>> queryTagType(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("key", "CPXPBQ");
        return productSpectrumDao.queryTagType(params);
    }

    @RequestMapping("tagName")
    @ResponseBody
    public List<Map<String, Object>> queryTagName(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        String tagType = RequestUtil.getString(request, "tagType", "");
        Map<String, String> params = new HashMap<>();
        params.put("key", "CPXPBQ");
        if (StringUtils.isNotEmpty(tagType)) {
            params.put("tagType", tagType);
        } else {
            params.put("tagType", "工况");
        }
        return productSpectrumDao.queryTagName(params);
    }

    @RequestMapping("distinctType")
    @ResponseBody
    public List<Map<String, Object>> queryDistinctType(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        // 给个默认值 中大挖
        String key = RequestUtil.getString(request, "key", "mainParamZDW");
        Map<String, String> params = new HashMap<>();
        params.put("key", key);
        return productSpectrumDao.queryTagType(params);
    }

    @RequestMapping("getSubClass")
    @ResponseBody
    public List<Map<String, Object>> querysubClassName(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        String className = RequestUtil.getString(request, "className", "");
        String key = RequestUtil.getString(request, "key", "");
        Map<String, String> params = new HashMap<>();

        params.put("key", key);
        if (StringUtils.isNotEmpty(className)) {
            params.put("tagType", className);
        } else {
            params.put("tagType", "工况");
        }
        return productSpectrumDao.queryTagName(params);
    }

    // 获取PDM更改通知单和技术通知单
    @RequestMapping("getPdmInfo")
    @ResponseBody
    public List<JSONObject> getPdmInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String productModel = RequestUtil.getString(request, "productModel", "");
        if (StringUtils.isBlank(productModel)) {
            logger.info("productModel is blank");
            return null;
        }
        JSONObject params = new JSONObject();
        params.put("productModel", productModel);
        List<JSONObject> pdmInfoList = productSpectrumService.getPdmInfoList(params);
        return pdmInfoList;
    }

    @RequestMapping("getZgProcessFromCrm")
    @ResponseBody
    public JsonResult getZgProcessFromCrm(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JsonResult result = new JsonResult(true, "查询成功");
        String jstzNo = RequestUtil.getString(request, "jstzNo", "");
        if (StringUtils.isBlank(jstzNo)) {
            logger.info("jstzNo is blank");
            return null;
        }
        JSONObject params = new JSONObject();
        params.put("jstzNo", jstzNo);
        productSpectrumService.getZgProcessFromCrm(result, params);
        return result;
    }

    @RequestMapping("startDraft")
    @ResponseBody
    public void createProcessDraft(HttpServletRequest request, HttpServletResponse response) throws Exception {
        productSpectrumService.createProcessDraft(request, response);
        return;
    }

    @RequestMapping("getDefaultSettingList")
    @ResponseBody
    public List<JSONObject> getDefaultSettingList(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        JSONObject settingsParams = new JSONObject();
        settingsParams.put("type", "mainSettingInit");
        List<JSONObject> settingsList = productSpectrumDao.settingListQuery(settingsParams);
        return settingsList;
    }

    @RequestMapping("getDefaultParamList")
    @ResponseBody
    public List<JSONObject> getDefaultParamList(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        JSONObject params = new JSONObject();
        params.put("type", "mainParamZDW");
        // 这里要用传过来的类型
        String type = RequestUtil.getString(request, "type", "");
        if (StringUtils.isNotEmpty(type)) {
            params.put("type", type);
        }

        List<JSONObject> paramList = productSpectrumDao.paramListQuery(params);
        return paramList;
    }

    @RequestMapping("checkEditPermition")
    @ResponseBody
    public JsonResult checkEditPermition(HttpServletRequest request, HttpServletResponse response) {
        JsonResult result = new JsonResult(false, "当前用户无此型号型谱编辑权限，请进行申请");
        String modifyId = RequestUtil.getString(request, "modifyId", "");
        String designModel = RequestUtil.getString(request, "designModel", "");
        String aimType = RequestUtil.getString(request, "aimType", "");
        JSONObject params = new JSONObject();
        params.put("modifyId", modifyId);
        params.put("designModel", designModel);
        params.put("aimType", aimType);
        // 写个查询，直接到dao
        List<JSONObject> paramList = productSpectrumDao.checkEditPermition(params);
        if (paramList.size() > 0) {
            result.setSuccess(true);
            result.setMessage("");
        }
        return result;
    }

    /**
     * 根据产品型号和物料号查询是否型谱中已有
     * 
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("checkExsit")
    @ResponseBody
    public JsonResult checkExsit(HttpServletRequest request, HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "该设计型号或物料号在型谱中已存在！");
        String designModel = RequestUtil.getString(request, "designModel", "");
        String materialCode = RequestUtil.getString(request, "materialCode", "");
        String id = RequestUtil.getString(request, "id", "");
        if (StringUtils.isBlank(designModel) && StringUtils.isBlank(materialCode)) {
            result.setSuccess(true);
            result.setMessage("设计型号和物料号条件不能均为空！");
            return result;
        }
        // 查找materialCode相同或者designModel相同的，并且id不等于参数id的，如果有则报错
        JSONObject params = new JSONObject();
        if (StringUtils.isNotBlank(materialCode)) {
            params.put("materialCode", materialCode);
        }
        if (StringUtils.isNotBlank(designModel)) {
            params.put("designModel", designModel);
        }
        if (StringUtils.isNotBlank(id)) {
            params.put("id", id);
        }
        boolean exsit = productSpectrumService.checkDesignModelExist(params);
        if (!exsit) {
            result.setSuccess(false);
            result.setMessage("");
        }
        return result;
    }

    @RequestMapping("importMainParam")
    @ResponseBody
    public JSONObject importMainParam(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        result.put("success", true);
        productSpectrumService.batchImportMainParam(result, request);
        return result;
    }

    @RequestMapping("importMainSettings")
    @ResponseBody
    public JSONObject importMainSettings(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        result.put("success", true);
        productSpectrumService.batchImportMainSetting(result, request);
        return result;
    }

}
