package com.redxun.keyDesign.core.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.keyDesign.core.dao.JsbzDao;
import com.redxun.keyDesign.core.service.JsbzService;
import com.redxun.org.api.model.IUser;
import com.redxun.rdmCommon.core.manager.LoginRecordManager;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.controller.GenericController;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.standardManager.core.util.StandardManagerUtil;
import com.redxun.sys.org.manager.OsGroupManager;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/jsbz/")
public class JsbzController extends GenericController{
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private JsbzService jsbzService;
    @Autowired
    private JsbzDao jsbzDao;
    @Resource
    private OsGroupManager osGroupManager;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private LoginRecordManager loginRecordManager;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;


    @RequestMapping("jsbzListPage")
    public ModelAndView msgManagement(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "keyDesign/core/jsbzList.jsp";
        String type =RequestUtil.getString(request,"type","");
        ModelAndView mv = new ModelAndView(jspPath);
        IUser currentUser = ContextUtil.getCurrentUser();
        Map<String, Object> params = new HashMap<>();
        params.put("userId", currentUser.getUserId());
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        // 角色信息
        List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params);
        JSONArray userRolesJsonArray = XcmgProjectUtil.convertListMap2JsonArrObject(currentUserRoles);
        mv.addObject("currentUserRoles", userRolesJsonArray);
        // 职级信息
        List<Map<String, Object>> currentUserZJ = xcmgProjectOtherDao.queryUserZJ(params);
        JSONArray userZJJsonArray = XcmgProjectUtil.convertListMap2JsonArrObject(currentUserZJ);
        boolean isGlNetwork= StandardManagerUtil.judgeGLNetwork(request);
        mv.addObject("isGlNetwork", isGlNetwork);
        mv.addObject("currentUserZJ", userZJJsonArray);
        mv.addObject("type", type);
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        boolean isGlr = rdmZhglUtil.judgeIsPointRoleUser(ContextUtil.getCurrentUserId(), "关键零部件管理人");
        mv.addObject("isGlr", isGlr);
        return mv;
    }


    @RequestMapping("saveJsbz")
    @ResponseBody
    public JsonResult saveJsbz(HttpServletRequest request, HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "关联成功");
        try {
            JSONObject formDataJson = new JSONObject();
            String type = RequestUtil.getString(request, "type");
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            formDataJson.put("belongbj",type);
            List<JSONObject> jsbzLists = jsbzDao.queryJsbz(formDataJson);
            for (String standardId : ids) {
                boolean link = true;
                for(JSONObject jsbzList:jsbzLists){
                    if(jsbzList.getString("standardId").equals(standardId)){
                        link = false;
                        break;
                    }
                }
                if(link){
                    formDataJson.put("standardId",standardId);
                    jsbzService.createJsbz(formDataJson);
                }
            }

        } catch (Exception e) {
            logger.error("Exception in save Cnsx");
            result.setSuccess(false);
            result.setMessage("Exception in save Cnsx");
            return result;
        }
        return result;
    }

    @RequestMapping("queryJsbz")
    @ResponseBody
    public JsonPageResult<?> queryJstb(HttpServletRequest request, HttpServletResponse response) {
        return jsbzService.queryJsbz(request, true);
    }



    @RequestMapping("deleteJsbz")
    @ResponseBody
    public JsonResult deleteJsbz(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            String belongStr = RequestUtil.getString(request, "belongbjs");
            String[] belongbjs = belongStr.split(",");
            return jsbzService.deleteJsbz(ids,belongbjs);
        } catch (Exception e) {
            logger.error("Exception in deleteJsbz", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("sendMsgPage")
    public ModelAndView sendMsgPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "keyDesign/core/msgList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String action = RequestUtil.getString(request, "action");
        String type = RequestUtil.getString(request, "type");
        mv.addObject("action", action).addObject("type", type);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        return mv;
    }

    @RequestMapping("editMsgPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "keyDesign/core/msgEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String action = RequestUtil.getString(request, "action");
        String type = RequestUtil.getString(request, "type");
        String msgId = RequestUtil.getString(request, "msgId");
        mv.addObject("action", action).addObject("type", type);
        mv.addObject("msgId", msgId);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        return mv;
    }


    @RequestMapping("queryMsg")
    @ResponseBody
    public JsonPageResult<?> queryMsg(HttpServletRequest request, HttpServletResponse response) {
        return jsbzService.queryMsg(request, true);
    }

    @RequestMapping("getMsgBaseInfo")
    @ResponseBody
    public JSONObject getSzhBaseInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject ydjxObj = new JSONObject();
        String msgId = RequestUtil.getString(request, "msgId");
        if (StringUtils.isNotBlank(msgId)) {
            ydjxObj = jsbzService.getMsgById(msgId);
        }
        return ydjxObj;
    }
    
    @RequestMapping("getMsgDetailList")
    @ResponseBody
    public List<JSONObject> getMsgDetailList(HttpServletRequest request, HttpServletResponse response) {
        return jsbzService.getMsgDetailList(request);
    }

    @RequestMapping("saveMsgDetail")
    @ResponseBody
    public JsonResult saveZlgj(HttpServletRequest request, @RequestBody String zlgjStr,
                               HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "");
        if (StringUtils.isBlank(zlgjStr)) {
            logger.warn("requestBody is blank");
            result.setSuccess(false);
            result.setMessage("requestBody is blank");
            return result;
        }

        try {
            JSONObject formDataJson = JSONObject.parseObject(zlgjStr);
            if (formDataJson == null || formDataJson.isEmpty()) {
                logger.warn("formData is blank");
                result.setSuccess(false);
                result.setMessage("requestBody is blank");
                return result;
            }
            if (StringUtils.isBlank(formDataJson.getString("msgId"))) {
                formDataJson.put("linkaction","未关联");
                jsbzService.createMsg(formDataJson);
            } else {
                jsbzService.updateMsg(formDataJson);
            }
            result.setData(formDataJson.getString("msgId"));
        } catch (Exception e) {
            logger.error("Exception in save zlgj");
            result.setSuccess(false);
            result.setMessage("Exception in save zlgj");
            return result;
        }
        return result;
    }

    @RequestMapping("deleteMsg")
    @ResponseBody
    public JsonResult deleteMsg(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String msgId = RequestUtil.getString(request, "id");
            return jsbzService.deleteMsg(msgId);
        } catch (Exception e) {
            logger.error("Exception in deleteMsg", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("deleteMsgDetail")
    @ResponseBody
    public JsonResult deleteMsgDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String ydjxIds = RequestUtil.getString(request, "ids");
            String[] ydjxIdsArr = ydjxIds.split(",",-1);
            return jsbzService.deleteMsgDetail(ydjxIdsArr);
        } catch (Exception e) {
            logger.error("Exception in deleteMsg", e);
            return new JsonResult(false, e.getMessage());
        }
    }
}

