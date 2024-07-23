package com.redxun.rdmZhgl.core.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.rdmZhgl.core.dao.CqbgDao;
import com.redxun.rdmZhgl.core.dao.ZlglmkDao;
import com.redxun.rdmZhgl.core.dao.ZljsjdsDao;
import com.redxun.rdmZhgl.core.service.CqbgService;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.controller.GenericController;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/rdmZhgl/Cqbg/")
public class CqbgController extends GenericController {
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private CqbgService cqbgService;
    @Autowired
    private CqbgDao cqbgDao;
    @Autowired
    private ZlglmkDao zlglmkDao;
    @Autowired
    private ZljsjdsDao zljsjdsDao;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private CommonBpmManager commonBpmManager;

    @RequestMapping("CqbgListPage")
    public ModelAndView msgManagement(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "rdmZhgl/core/cqbgList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        Map<String, Object> params2 = new HashMap<>();
        params2.put("userId", ContextUtil.getCurrentUserId());
        params2.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params2);
        JSONArray userRolesJsonArray = XcmgProjectUtil.convertListMap2JsonArrObject(currentUserRoles);
        mv.addObject("currentUserRoles", userRolesJsonArray);
        // 返回当前用户是否是技术管理部负责人
        boolean isJsglbRespUser = rdmZhglUtil.judgeUserIsJsglbRespUser(ContextUtil.getCurrentUserId());
        mv.addObject("isJsglbRespUser", isJsglbRespUser);
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        return mv;
    }

    @RequestMapping("editPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "";
        // 新增或者编辑页面由流程引擎跳转并替换url中的pk和nodeId_
        String flowType = RequestUtil.getString(request, "flowType", "");
        String cqbgId = RequestUtil.getString(request, "cqbgId");
        String nodeId = RequestUtil.getString(request, "nodeId");
        String action = RequestUtil.getString(request, "action");
        String status = RequestUtil.getString(request, "status");
        if (StringUtils.isBlank(flowType)) {
            if (StringUtils.isNotBlank(cqbgId)) {
                JSONObject objJson = cqbgService.getCqbgDetail(cqbgId);
                if (objJson != null) {
                    flowType = objJson.getString("flowType");
                }
            } else {
                flowType = "project";
            }
        }
        switch (flowType) {
            case "person":
                jspPath = "rdmZhgl/core/cqbgEdit.jsp";
                break;
            case "project":
                jspPath = "rdmZhgl/core/cqxmbgEdit.jsp";
                break;
            default:
                break;
        }
        ModelAndView mv = new ModelAndView(jspPath);
        if (StringUtils.isBlank(action)) {
            // 新增或编辑的时候没有nodeId
            if (StringUtils.isBlank(nodeId) || nodeId.contains("PROCESS")) {
                action = "edit";
            } else {
                // 处理任务的时候有nodeId
                action = "task";
            }
        }
        mv.addObject("cqbgId", cqbgId).addObject("action", action).addObject("status", status);
        // 取出节点变量，返回到页面
        if (StringUtils.isNotBlank(nodeId)) {
            mv.addObject("nodeId", nodeId);
            List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId, "JSJDSBGSQ", null);
            if (nodeVars != null && !nodeVars.isEmpty()) {
                mv.addObject("nodeVars", JSONObject.toJSONString(nodeVars));
            }
        }
        mv.addObject("flowType", flowType);
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        return mv;
    }

    @RequestMapping("queryCqbg")
    @ResponseBody
    public JsonPageResult<?> queryCqbg(HttpServletRequest request, HttpServletResponse response) {
        return cqbgService.queryCqbg(request, true);
    }

    @RequestMapping("deleteCqbg")
    @ResponseBody
    public JsonResult deleteCqbg(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return cqbgService.deleteCqbg(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteCqbg", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("saveCqbg")
    @ResponseBody
    public JsonResult saveCqbg(HttpServletRequest request, @RequestBody String xcmgProjectStr,
        HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "");
        if (StringUtils.isBlank(xcmgProjectStr)) {
            logger.warn("requestBody is blank");
            result.setSuccess(false);
            result.setMessage("requestBody is blank");
            return result;
        }
        try {
            JSONObject formDataJson = JSONObject.parseObject(xcmgProjectStr);
            if (formDataJson == null || formDataJson.isEmpty()) {
                logger.warn("formData is blank");
                result.setSuccess(false);
                result.setMessage("requestBody is blank");
                return result;
            }
            if (StringUtils.isBlank(formDataJson.getString("cqbgId"))) {
                cqbgService.createCqbg(formDataJson);
            } else {
                cqbgService.updateCqbg(formDataJson);
            }
        } catch (Exception e) {
            logger.error("Exception in save cqbg");
            result.setSuccess(false);
            result.setMessage("Exception in save cqbg");
            return result;
        }
        return result;
    }

    @RequestMapping("getCqbgDetail")
    @ResponseBody
    public JSONObject getCqbgDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject cqbgObj = new JSONObject();
        String cqbgId = RequestUtil.getString(request, "cqbgId");
        if (StringUtils.isNotBlank(cqbgId)) {
            cqbgObj = cqbgService.getCqbgDetail(cqbgId);
        }
        return cqbgObj;
    }

    @RequestMapping("checkZgzl")
    @ResponseBody
    public JSONObject checkZgzl(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject approveValid = new JSONObject();
        approveValid.put("success", true);
        approveValid.put("message", "");
        String jsjdsId = RequestUtil.getString(request, "jsjdsId", "");
        if (StringUtils.isBlank(jsjdsId)) {
            approveValid.put("success", false);
            approveValid.put("message", "交底书Id不能为空！");
            return approveValid;
        }
        JSONObject jdsObj = zljsjdsDao.queryJsjdsById(jsjdsId);
        if(StringUtils.isBlank(jdsObj.getString("instStatus"))||
                "SUCCESS_END".equalsIgnoreCase(jdsObj.getString("instStatus"))){
            List<JSONObject> checJdskList = new ArrayList<>();
            checJdskList = cqbgDao.queryCqbgByJds(jsjdsId);
            if (checJdskList.size() == 0) {
                approveValid.put("success", false);
                approveValid.put("message", "未查询到该交底书关联的中国专利台账,请联系专利工程师进行关联！");
            }else {
                String zgzlId = checJdskList.get(0).getString("zgzlId");
                JSONObject params = new JSONObject();
                List<JSONObject> checkProjectList = new ArrayList<>();
                params.put("zgzlId", zgzlId);
                checkProjectList = zlglmkDao.checkProject(params);
                if (checkProjectList.size() != 0) {
                    approveValid.put("success", false);
                    approveValid.put("message", "该技术交底书产生的专利已关联项目-"+checkProjectList.get(0).getString("projectName")+"！");
                }
            }
        }
        return approveValid;
    }
}
