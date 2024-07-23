package com.redxun.materielextend.core.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.materielextend.core.dao.MaterielTypeFDao;
import com.redxun.materielextend.core.service.MaterielTypeFService;
import com.redxun.materielextend.core.util.ResultData;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.controller.GenericController;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

/**
 * 信息记录
 */
@Controller
@RequestMapping("/materielTypeF/core/")
public class MaterielTypeFController extends GenericController {
    @Autowired
    private CommonInfoManager commonInfoManager;
    @Autowired
    private MaterielTypeFService materielTypeFService;
    @Autowired
    private MaterielTypeFDao materielTypeFDao;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;

    @RequestMapping("openUploadWindow")
    public ModelAndView openUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "materielExtend/materielTypeFFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("detailId", RequestUtil.getString(request, "detailId", ""));
        return mv;
    }

    @RequestMapping("materielTypeFListPage")
    public ModelAndView msgManagement(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "materielExtend/materielTypeFList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        return mv;
    }

    @RequestMapping("editPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "materielExtend/materielTypeFEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        // 新增或者编辑页面由流程引擎跳转并替换url中的pk和nodeId_
        String id = RequestUtil.getString(request, "id");
        String action = RequestUtil.getString(request, "action");
        mv.addObject("id", id).addObject("action", action);
        JSONObject currentUserDepInfo = xcmgProjectManager.isCurrentUserDepRespman();
        JSONObject dept = commonInfoManager.queryDeptNameById(currentUserDepInfo.getString("currentUserMainDepId"));
        String deptName = dept.getString("deptname");
        mv.addObject("deptName", deptName);
        mv.addObject("currentUserMainDepId", currentUserDepInfo.getString("currentUserMainDepId"));
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        return mv;
    }

    @RequestMapping("saveMaterielTypeF")
    @ResponseBody
    public JsonResult saveMaterielTypeF(HttpServletRequest request, @RequestBody JSONObject formDataJson,
        HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "成功");
        try {
            if (formDataJson == null || formDataJson.isEmpty()) {
                logger.warn("formData is blank");
                result.setSuccess(false);
                result.setMessage("requestBody is blank");
                return result;
            }
            if (StringUtils.isBlank(formDataJson.getString("id"))) {
                materielTypeFService.createMaterielTypeF(formDataJson);
            } else {
                materielTypeFService.updateMaterielTypeF(formDataJson);
            }
        } catch (Exception e) {
            logger.error("Exception in saveMaterielTypeF", e);
            result.setSuccess(false);
            result.setMessage("Exception in save Cnsx");
            return result;
        }
        result.setData(formDataJson.getString("id"));
        return result;
    }

    @RequestMapping("queryMaterielTypeF")
    @ResponseBody
    public JsonPageResult<?> queryJstb(HttpServletRequest request, HttpServletResponse response) {
        return materielTypeFService.queryMaterielTypeF(request, true);
    }

    @RequestMapping("deleteMaterielTypeF")
    @ResponseBody
    public JsonResult deleteMaterielTypeF(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return materielTypeFService.deleteMaterielTypeF(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteMaterielTypeF", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("getMaterielTypeF")
    @ResponseBody
    public JSONObject getMaterielTypeF(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject materielTypeFObj = new JSONObject();
        String id = RequestUtil.getString(request, "id");
        if (StringUtils.isNotBlank(id)) {
            materielTypeFObj = materielTypeFService.getMaterielTypeFById(id);
        }
        return materielTypeFObj;
    }

    @RequestMapping("getMaterielTypeFDetailList")
    @ResponseBody
    public List<JSONObject> getMaterielTypeFDetailList(HttpServletRequest request, HttpServletResponse response) {
        return materielTypeFService.getMaterielTypeFDetailList(request);
    }

    @RequestMapping("editMaterielTypeFDetail")
    public ModelAndView editCn(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "materielExtend/materielTypeFDetailEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String action = RequestUtil.getString(request, "action");
        String belongId = RequestUtil.getString(request, "id");
        String detailId = RequestUtil.getString(request, "detailId");
        if (StringUtils.isBlank(action)) {
            action = "edit";
        }
        mv.addObject("action", action);
        mv.addObject("belongId", belongId);
        mv.addObject("detailId", detailId);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUser", ContextUtil.getCurrentUser());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        return mv;
    }

    @RequestMapping("saveMaterielTypeFDetail")
    @ResponseBody
    public JsonResult saveMaterielTypeFDetail(HttpServletRequest request, @RequestBody JSONObject formDataJson,
        HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "成功");
        try {
            String belongId = RequestUtil.getString(request, "belongId");
            if (formDataJson == null || formDataJson.isEmpty()) {
                logger.warn("formData is blank");
                result.setSuccess(false);
                result.setMessage("requestBody is blank");
                return result;
            }
            if (StringUtils.isBlank(formDataJson.getString("detailId"))) {
                formDataJson.put("belongId", belongId);
                materielTypeFService.createMaterielTypeFDetail(formDataJson);
            } else {
                materielTypeFService.updateMaterielTypeFDetail(formDataJson);
            }

        } catch (Exception e) {
            logger.error("Exception in saveMaterielTypeFDetail", e);
            result.setSuccess(false);
            result.setMessage("Exception in saveMaterielTypeFDetail");
            return result;
        }
        result.setData(formDataJson.getString("detailId"));
        return result;
    }

    @RequestMapping("getMaterielTypeFDetail")
    @ResponseBody
    public JSONObject getMaterielTypeFDetail(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        JSONObject materielTypeFObj = new JSONObject();
        String detailId = RequestUtil.getString(request, "detailId");
        if (StringUtils.isNotBlank(detailId)) {
            materielTypeFObj = materielTypeFService.getMaterielTypeFDetail(detailId);
        }
        return materielTypeFObj;
    }

    @RequestMapping("deleteMaterielTypeFDetail")
    @ResponseBody
    public JsonResult deleteMaterielTypeFDetail(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        try {
            String detailId = RequestUtil.getString(request, "detailId");
            return materielTypeFService.deleteOneMaterielTypeFDetail(detailId);
        } catch (Exception e) {
            logger.error("Exception in deleteMaterielTypeFDetail", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("setWlinfo")
    @ResponseBody
    public JSONObject setWlinfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject materielTypeFObj = new JSONObject();
        String wlhm = RequestUtil.getString(request, "wlhm", "");
        if (StringUtils.isNotBlank(wlhm)) {
            materielTypeFObj = materielTypeFService.setWlinfo(wlhm);
        }
        return materielTypeFObj;
    }

    @RequestMapping("sync2SAP")
    @ResponseBody
    public ResultData sync2SAP(HttpServletRequest request, HttpServletResponse response) {
        String id = RequestUtil.getString(request, "id", "");
        Map<String, Object> param = new HashMap<>();
        param.put("belongId", id);
        param.put("callsap", "yes");
        List<JSONObject> materielTypeFDetailList = materielTypeFDao.queryMaterielTypeFDetail(param);
        ResultData resultData = new ResultData();
        resultData.setMessage("操作成功!");
        Map<String, JSONObject> tempObj = new HashMap<>();
        for (JSONObject oneMat : materielTypeFDetailList) {
            tempObj.put(oneMat.getString("wlhm"), oneMat);
        }
        List<JSONObject> newmaterielTypeFDetailList = new ArrayList<>();
        tempObj.forEach((codeString, codeJson) -> {
            newmaterielTypeFDetailList.add(codeJson);
        });
        materielTypeFService.callSAP(newmaterielTypeFDetailList, resultData);
        materielTypeFDao.updateTypeFNotice(param);
        return resultData;
    }
}
