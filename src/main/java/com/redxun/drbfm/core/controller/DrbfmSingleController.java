package com.redxun.drbfm.core.controller;

import java.io.File;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import com.redxun.drbfm.core.service.DrbfmSingleService;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.sys.core.util.SysPropertiesUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.drbfm.core.dao.DrbfmSingleDao;
import com.redxun.drbfm.core.service.DrbfmTestTaskService;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.controller.GenericController;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

@Controller
@RequestMapping("/drbfm/single/")
public class DrbfmSingleController {
    protected Logger logger = LogManager.getLogger(GenericController.class);
    @Autowired
    private DrbfmSingleService drbfmSingleService;
    @Autowired
    private CommonBpmManager commonBpmManager;
    @Autowired
    private DrbfmSingleDao drbfmSingleDao;
    @Autowired
    private DrbfmTestTaskService drbfmTestTaskService;
    @Resource
    private BpmInstManager bpmInstManager;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;

    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;

    @RequestMapping("openDemandWindow")
    public ModelAndView openDemandWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "drbfm/demandFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String relDemandId = RequestUtil.getString(request, "relDemandId", "");
        String belongSingleId = RequestUtil.getString(request, "belongSingleId", "");
        mv.addObject("currentUser", ContextUtil.getCurrentUser());
        mv.addObject("relDemandId", relDemandId);
        mv.addObject("belongSingleId", belongSingleId);
        return mv;
    }

    @RequestMapping("uploadDemand")
    @ResponseBody
    public Map<String, Object> uploadDemand(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        try {
            // 先保存文件到磁盘，成功后再写入数据库，前台是一个一个文件的调用
            drbfmSingleService.saveDemandFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            modelMap.put("success", "false");
        }
        return modelMap;
    }

    @RequestMapping("demandFileList")
    @ResponseBody
    public List<JSONObject> demandFileList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String relDemandId = RequestUtil.getString(request, "relDemandId", "");
        if (StringUtils.isBlank(relDemandId)) {
            return null;
        }
        JSONObject params = new JSONObject();
        params.put("relDemandId", relDemandId);
        List<JSONObject> fileList = drbfmTestTaskService.queryDemandList(params);
        return fileList;
    }

    @RequestMapping("singleListPage")
    public ModelAndView totalListPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "drbfm/drbfmSingleList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());

        return mv;
    }

    @RequestMapping("getSingleList")
    @ResponseBody
    public JsonPageResult<?> getSingleList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return drbfmSingleService.getSingleList(request, response, true);
    }

    @RequestMapping("getSelectSingleList")
    @ResponseBody
    public JsonPageResult<?> getSelectSingleList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return drbfmSingleService.getSelectSingleList(request, response, true);
    }

    // 单一项目的主体框架页面
    @RequestMapping("singleFramePage")
    public ModelAndView singleFramePage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "drbfm/singleFramePage.jsp";
        // 新增或者编辑页面由流程引擎跳转并替换url中的pk和nodeId_
        String singleId = RequestUtil.getString(request, "singleId");
        String nodeId = RequestUtil.getString(request, "nodeId");
        String action = RequestUtil.getString(request, "action");
        String status = RequestUtil.getString(request, "status");
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
        // 取出节点变量，返回到页面
        List<Map<String, String>> nodeVars = new ArrayList<>();
        if (StringUtils.isNotBlank(nodeId)) {
            nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId, "drbfmSingle", null);
        }
        String stageName = "";
        String statusName = "";
        if (nodeVars != null && !nodeVars.isEmpty()) {
            for (int i = 0; i < nodeVars.size(); i++) {
                if (nodeVars.get(i).get("KEY_").equals("stageName")) {
                    stageName = nodeVars.get(i).get("DEF_VAL_");
                }
                if (nodeVars.get(i).get("KEY_").equals("statusName")) {
                    statusName = nodeVars.get(i).get("DEF_VAL_");
                }
            }
        }
        mv.addObject("stageName", stageName);
        mv.addObject("statusName", statusName);
        mv.addObject("singleId", singleId);
        mv.addObject("status", status);
        mv.addObject("action", action);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        //mh这里要查一下是不是顶层件或底层件
        String parentId = drbfmSingleDao.queryParentIdBySingleId(singleId);
        if ("0".equalsIgnoreCase(parentId)) {
            mv.addObject("startOrEnd", "start");
            return mv;
        }
        int cnt = drbfmSingleDao.countXjbjBySingleId(singleId);
        if (cnt == 1) {
            mv.addObject("startOrEnd", "end");
        }


        return mv;
    }

    @RequestMapping("singleBaseInfoPage")
    public ModelAndView singleBaseInfoPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "drbfm/singleBaseInfoPage.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String singleId = RequestUtil.getString(request, "singleId");
        String action = RequestUtil.getString(request, "action");
        String stageName = RequestUtil.getString(request, "stageName");
        mv.addObject("stageName", stageName);
        mv.addObject("singleId", singleId);
        mv.addObject("action", action);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        return mv;
    }

    @RequestMapping("getSingleBaseDetail")
    @ResponseBody
    public JSONObject getSingleBaseDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject ceinfoobj = new JSONObject();
        String id = RequestUtil.getString(request, "id");
        if (StringUtils.isNotBlank(id)) {
            ceinfoobj = drbfmSingleService.getSingleDetail(id);
        }
        return ceinfoobj;
    }



    @RequestMapping("querySingleBaseList")
    @ResponseBody
    public JsonPageResult<?> querySingleBaseList(HttpServletRequest request, HttpServletResponse response) {
        return drbfmSingleService.getSingleList(request, response, false);
    }

    @RequestMapping("queryStatusBySingleId")
    @ResponseBody
    public String queryStatusBySingleId(HttpServletRequest request, HttpServletResponse response) {
        return drbfmSingleService.queryStatusBySingleId(request, response, false);
    }

    @RequestMapping("saveBaseInfo")
    @ResponseBody
    public JsonResult saveBaseInfo(HttpServletRequest request, @RequestBody JSONObject formDataJson,
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
                drbfmSingleService.insertSingleBase(formDataJson);
            } else {
                drbfmSingleService.updateSingleBase(formDataJson);
            }
            //这里根据风险分析类型判断是否要将后续的所有要求中，风险分析置为是或否
            String riskAnalysisType = formDataJson.getString("riskAnalysisType");
            if (StringUtils.isBlank(formDataJson.getString("id"))) {
                return result;
            }
            JSONObject params = new JSONObject();
            params.put("partId", formDataJson.getString("id"));
            if ("fxfxj".equalsIgnoreCase(riskAnalysisType)) {
                //风险分析件，全置位是
                params.put("needFxfx","是");
                drbfmSingleDao.batchUpdateSxmsFxfx(params);
            } else if ("wghxzj".equalsIgnoreCase(riskAnalysisType)) {
                //外购黑匣子件/底层自制件
                params.put("needFxfx","否");
                drbfmSingleDao.batchUpdateSxmsFxfx(params);
            }

        } catch (Exception e) {
            logger.error("Exception in saveBaseInfo");
            result.setSuccess(false);
            result.setMessage("Exception in saveBaseInfo");
            return result;
        }
        result.setData(formDataJson.getString("id"));
        return result;
    }

    @RequestMapping("singleDeptDemandPage")
    public ModelAndView singleDeptDemandPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "drbfm/singleDeptDemandPage.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String singleId = RequestUtil.getString(request, "singleId");
        String action = RequestUtil.getString(request, "action");
        String stageName = RequestUtil.getString(request, "stageName");
        mv.addObject("stageName", stageName);
        mv.addObject("singleId", singleId);
        mv.addObject("action", action);
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        return mv;
    }

    @RequestMapping("getSingleDeptDemandList")
    @ResponseBody
    public List<JSONObject> getSingleDeptDemandList(HttpServletRequest request, HttpServletResponse response) {
        String belongSingleId = RequestUtil.getString(request, "belongSingleId");
        String filterParams = request.getParameter("filter");
        return drbfmSingleService.getSingleDeptDemandList(belongSingleId, filterParams);
    }


    @RequestMapping("saveDeptDemand")
    @ResponseBody
    public JsonResult saveDemand(HttpServletRequest request, @RequestBody JSONObject formDataJson,
        HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "保存成功");
        try {
            if (formDataJson == null || formDataJson.isEmpty()) {
                logger.warn("formData is blank");
                result.setSuccess(false);
                result.setMessage("requestBody is blank");
                return result;
            }
            if (StringUtils.isBlank(formDataJson.getString("id"))) {
                drbfmSingleService.insertDeptDemand(formDataJson);
            } else {
                drbfmSingleService.updateDeptDemand(formDataJson);
            }
        } catch (Exception e) {
            logger.error("Exception in saveDemand");
            result.setSuccess(false);
            result.setMessage("Exception in saveDemand");
            return result;
        }
        result.setData(formDataJson.getString("id"));
        return result;
    }

    @RequestMapping("getSingleDeptDemandDetail")
    @ResponseBody
    public JSONObject getSingleDeptDemandDetail(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        JSONObject ceinfoobj = new JSONObject();
        String id = RequestUtil.getString(request, "id");
        if (StringUtils.isNotBlank(id)) {
            ceinfoobj = drbfmSingleService.getSingleDeptDemandDetail(id);
        }
        return ceinfoobj;
    }

    @RequestMapping("deleteDeptDemand")
    @ResponseBody
    public JsonResult deleteDeptDemand(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return drbfmSingleService.deleteDeptDemand(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteDeptDemand", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("singleFunctionRequestPage")
    public ModelAndView singleFunctionRequestPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "drbfm/singleFunctionRequestPage.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String singleId = RequestUtil.getString(request, "singleId");
        String action = RequestUtil.getString(request, "action");
        String stageName = RequestUtil.getString(request, "stageName");
        String collectType = RequestUtil.getString(request, "collectType");
        String belongCollectFlowId = RequestUtil.getString(request, "belongCollectFlowId");
        mv.addObject("stageName", stageName);
        mv.addObject("singleId", singleId);
        mv.addObject("action", action);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        mv.addObject("collectType", collectType);
        mv.addObject("belongCollectFlowId", belongCollectFlowId);
        return mv;
    }

    @RequestMapping("singleFunctionNetPage")
    public ModelAndView singleFunctionNetPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "drbfm/singleFunctionNetPage.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String singleId = RequestUtil.getString(request, "singleId");
        String action = RequestUtil.getString(request, "action");
        String stageName = RequestUtil.getString(request, "stageName");
        String collectType = RequestUtil.getString(request, "collectType");
        String belongCollectFlowId = RequestUtil.getString(request, "belongCollectFlowId");
        String startOrEnd = RequestUtil.getString(request, "startOrEnd");
        mv.addObject("stageName", stageName);
        mv.addObject("singleId", singleId);
        mv.addObject("action", action);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("collectType", collectType);
        mv.addObject("belongCollectFlowId", belongCollectFlowId);
        mv.addObject("startOrEnd", startOrEnd);


        return mv;
    }

    @RequestMapping("singleFunctionRiskAnalysisPage")
    public ModelAndView singleFunctionRiskAnalysisPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "drbfm/singleRiskAnalysisPage.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String singleId = RequestUtil.getString(request, "singleId");
        String action = RequestUtil.getString(request, "action");
        String stageName = RequestUtil.getString(request, "stageName");
        String collectType = RequestUtil.getString(request, "collectType");
        String belongCollectFlowId = RequestUtil.getString(request, "belongCollectFlowId");
        mv.addObject("stageName", stageName);
        mv.addObject("singleId", singleId);
        mv.addObject("action", action);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("collectType", collectType);
        mv.addObject("belongCollectFlowId", belongCollectFlowId);
        return mv;
    }

    @RequestMapping("singleFunctionRequestJSPage")
    public ModelAndView singleFunctionRequestJSPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "drbfm/singleFunctionRequestJSPage.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String jsId = RequestUtil.getString(request, "jsId");
        String action = RequestUtil.getString(request, "action");
        mv.addObject("jsId", jsId);
        mv.addObject("action", action);
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        return mv;
    }

    @RequestMapping("singleFunctionRequestCollectPage")
    public ModelAndView singleFunctionRequestCollectPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "drbfm/singleFunctionRequestCollectPage.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String singleId = RequestUtil.getString(request, "singleId");
        String action = RequestUtil.getString(request, "action");
        String stageName = RequestUtil.getString(request, "stageName");
        String collectType = RequestUtil.getString(request, "collectType");
        String belongCollectFlowId = RequestUtil.getString(request, "belongCollectFlowId");
        mv.addObject("stageName", stageName);
        mv.addObject("singleId", singleId);
        mv.addObject("action", action);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("collectType", collectType);
        mv.addObject("belongCollectFlowId", belongCollectFlowId);
        return mv;
    }

    @RequestMapping("getFunctionList")
    @ResponseBody
    public List<JSONObject> getFunctionList(HttpServletRequest request, HttpServletResponse response) {
        String belongId = RequestUtil.getString(request, "belongSingleId");
        String filterParams = request.getParameter("filter");
        return drbfmSingleService.getFunctionList(belongId, filterParams);
    }

    @RequestMapping("getFunctionListByCollectId")
    @ResponseBody
    public List<JSONObject> getFunctionListByCollectId(HttpServletRequest request, HttpServletResponse response) {
        String belongCollectFlowId = RequestUtil.getString(request, "belongCollectFlowId");
        String filterParams = request.getParameter("filter");
        return drbfmSingleService.getFunctionListByCollectId(belongCollectFlowId, filterParams);
    }

    @RequestMapping("getFunctionListByJSId")
    @ResponseBody
    public List<JSONObject> getFunctionListByJSId(HttpServletRequest request, HttpServletResponse response) {
        String jsId = RequestUtil.getString(request, "jsId");
        String filterParams = request.getParameter("filter");
        return drbfmSingleService.getFunctionListByJSId(jsId, filterParams);
    }

    @RequestMapping("saveFunction")
    @ResponseBody
    public JsonResult saveFunction(HttpServletRequest request, @RequestBody JSONObject formDataJson,
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
                drbfmSingleService.insertFunction(formDataJson);
            } else {
                drbfmSingleService.updateFunction(formDataJson);
            }
        } catch (Exception e) {
            logger.error("Exception in saveFunction");
            result.setSuccess(false);
            result.setMessage("Exception in saveFunction");
            return result;
        }
        result.setData(formDataJson.getString("id"));
        return result;
    }

    @RequestMapping("getFunctionDetail")
    @ResponseBody
    public JSONObject getFunctionDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject ceinfoobj = new JSONObject();
        String id = RequestUtil.getString(request, "id");
        if (StringUtils.isNotBlank(id)) {
            ceinfoobj = drbfmSingleService.getFunctionDetail(id);
        }
        return ceinfoobj;
    }

    @RequestMapping("deleteFunction")
    @ResponseBody
    public JsonResult deleteFunction(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return drbfmSingleService.deleteFunction(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteCeinfo", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    // 要求分解
    @RequestMapping("getRequestList")
    @ResponseBody
    public List<JSONObject> getRequestList(HttpServletRequest request, HttpServletResponse response) {
        String belongId = RequestUtil.getString(request, "belongSingleId");
        String filterParams = request.getParameter("filter");
        List<JSONObject> txRequestList = drbfmSingleService.getRequestList(belongId, filterParams);
        List<JSONObject> res = drbfmSingleService.addIsRelationChangePointList(belongId,txRequestList);
        return res;
    }

    // 要求分解
    @RequestMapping("getFinalProcessList")
    @ResponseBody
    public List<JSONObject> getFinalProcessList(HttpServletRequest request, HttpServletResponse response) {
        String belongId = RequestUtil.getString(request, "belongSingleId");
        String filterParams = request.getParameter("filter");
        return drbfmSingleService.getFinalProcessList(belongId, filterParams);
    }
    @RequestMapping("getRequestListByCollectId")
    @ResponseBody
    public List<JSONObject> getRequestListByCollectId(HttpServletRequest request, HttpServletResponse response) {
        String belongCollectFlowId = RequestUtil.getString(request, "belongCollectFlowId");
        String filterParams = request.getParameter("filter");
        return drbfmSingleService.getRequestListByCollectId(belongCollectFlowId, filterParams);
    }

    @RequestMapping("getRequestListByJSId")
    @ResponseBody
    public List<JSONObject> getRequestListByJSId(HttpServletRequest request, HttpServletResponse response) {
        String jsId = RequestUtil.getString(request, "jsId");
        String filterParams = request.getParameter("filter");
        return drbfmSingleService.getRequestListByJSId(jsId, filterParams);
    }

    @RequestMapping("saveRequest")
    @ResponseBody
    public JsonResult saveRequest(HttpServletRequest request, @RequestBody JSONObject formDataJson,
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
                drbfmSingleService.insertRequest(formDataJson);
            } else {
                drbfmSingleService.updateRequest(formDataJson);
            }
        } catch (Exception e) {
            logger.error("Exception in saveRequest");
            result.setSuccess(false);
            result.setMessage("Exception in saveRequest");
            return result;
        }
        result.setData(formDataJson.getString("id"));
        return result;
    }

    @RequestMapping("getRequestDetail")
    @ResponseBody
    public JSONObject getRequestDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject ceinfoobj = new JSONObject();
        String id = RequestUtil.getString(request, "id");
        if (StringUtils.isNotBlank(id)) {
            ceinfoobj = drbfmSingleService.getRequestDetail(id);
        }
        return ceinfoobj;
    }

    @RequestMapping("deleteRequest")
    @ResponseBody
    public JsonResult deleteRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return drbfmSingleService.deleteRequest(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteCeinfo", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("singleQuotaDecomposePage")
    public ModelAndView singleQuotaDecomposePage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "drbfm/singleQuotaDecomposePage.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String singleId = RequestUtil.getString(request, "singleId");
        String action = RequestUtil.getString(request, "action");
        String stageName = RequestUtil.getString(request, "stageName");
        String collectType = RequestUtil.getString(request, "collectType");
        String belongCollectFlowId = RequestUtil.getString(request, "belongCollectFlowId");
        mv.addObject("stageName", stageName);
        mv.addObject("singleId", singleId);
        mv.addObject("action", action);
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        mv.addObject("collectType", collectType);
        mv.addObject("belongCollectFlowId", belongCollectFlowId);
        return mv;
    }

    @RequestMapping("singleQuotaDecomposeJSPage")
    public ModelAndView singleQuotaDecomposeJSPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "drbfm/singleQuotaDecomposeJSPage.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String jsId = RequestUtil.getString(request, "jsId");
        String action = RequestUtil.getString(request, "action");
        mv.addObject("jsId", jsId);
        mv.addObject("action", action);
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        return mv;
    }

    @RequestMapping("singleQuotaDecomposeCollectPage")
    public ModelAndView singleQuotaDecomposeCollectPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "drbfm/singleQuotaDecomposeCollectPage.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String singleId = RequestUtil.getString(request, "singleId");
        String action = RequestUtil.getString(request, "action");
        String stageName = RequestUtil.getString(request, "stageName");
        String collectType = RequestUtil.getString(request, "collectType");
        String belongCollectFlowId = RequestUtil.getString(request, "belongCollectFlowId");
        mv.addObject("stageName", stageName);
        mv.addObject("singleId", singleId);
        mv.addObject("action", action);
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        mv.addObject("collectType", collectType);
        mv.addObject("belongCollectFlowId", belongCollectFlowId);
        return mv;
    }

    // 指标分解
    @RequestMapping("getQuotaList")
    @ResponseBody
    public List<JSONObject> getQuotaList(HttpServletRequest request, HttpServletResponse response) {
        String belongId = RequestUtil.getString(request, "belongSingleId");
        String filterParams = request.getParameter("filter");
        return drbfmSingleService.getQuotaList(belongId, filterParams);
    }

    // 指标分解
    @RequestMapping("getQuotaListByJSList")
    @ResponseBody
    public List<JSONObject> getQuotaListByJSList(HttpServletRequest request, HttpServletResponse response) {
        String jsId = RequestUtil.getString(request, "jsId");
        String filterParams = request.getParameter("filter");
        return drbfmSingleService.getQuotaListByJSList(jsId, filterParams);
    }

    @RequestMapping("saveQuota")
    @ResponseBody
    public JsonResult saveQuota(HttpServletRequest request, @RequestBody JSONObject formDataJson,
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
                if (StringUtils.isBlank(formDataJson.getString("replaceQuotaId"))) {
                    drbfmSingleService.insertQuota(formDataJson);
                } else {
                    drbfmSingleService.stopOldCreateNewQuota(formDataJson, result);
                }
            } else {
                drbfmSingleService.updateQuota(formDataJson);
            }
        } catch (Exception e) {
            logger.error("Exception in saveQuota");
            result.setSuccess(false);
            result.setMessage("Exception in saveQuota");
            return result;
        }
        result.setData(formDataJson.getString("id"));
        return result;
    }

    @RequestMapping("getQuotaDetail")
    @ResponseBody
    public JSONObject getQuotaDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject ceinfoobj = new JSONObject();
        String id = RequestUtil.getString(request, "id");
        if (StringUtils.isNotBlank(id)) {
            ceinfoobj = drbfmSingleService.getQuotaDetail(id);
        }
        return ceinfoobj;
    }

    @RequestMapping("deleteQuota")
    @ResponseBody
    public JsonResult deleteQuota(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return drbfmSingleService.deleteQuota(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteQuota", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("singleTestTaskPage")
    public ModelAndView singleTestTaskPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "drbfm/singleTestTaskPage.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String singleId = RequestUtil.getString(request, "singleId");
        String action = RequestUtil.getString(request, "action");
        String stageName = RequestUtil.getString(request, "stageName");
        mv.addObject("stageName", stageName);
        mv.addObject("singleId", singleId);
        mv.addObject("action", action);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        return mv;
    }

    @RequestMapping("singleQuotaEvaluatePage")
    public ModelAndView singleQuotaEvaluatePage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "drbfm/singleQuotaEvaluatePage.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String singleId = RequestUtil.getString(request, "singleId");
        String action = RequestUtil.getString(request, "action");
        String stageName = RequestUtil.getString(request, "stageName");
        mv.addObject("stageName", stageName);
        mv.addObject("singleId", singleId);
        mv.addObject("action", action);
        return mv;
    }

    /**
     * 查询某个单一项目中，带评价结果的指标列表
     * 
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("getQuotaWithEvaluateList")
    @ResponseBody
    public List<JSONObject> getQuotaWithEvaluateList(HttpServletRequest request, HttpServletResponse response) {
        String belongId = RequestUtil.getString(request, "belongSingleId");
        String filterParams = request.getParameter("filter");
        return drbfmSingleService.getQuotaWithEvaluateList(belongId, filterParams);
    }

    // 查询某个指标的综合评价数据
    @RequestMapping("getOneQuotaEvaluateList")
    @ResponseBody
    public List<JSONObject> getOneQuotaEvaluateList(HttpServletRequest request, HttpServletResponse response) {
        String belongId = RequestUtil.getString(request, "belongSingleId");
        String belongQuotaId = RequestUtil.getString(request, "belongQuotaId");
        String filterParams = request.getParameter("filter");
        return drbfmSingleService.getOneQuotaEvaluateList(belongId, belongQuotaId, filterParams);
    }

    @RequestMapping("saveOneQuotaEvaluate")
    @ResponseBody
    public JsonResult saveOneQuotaEvaluate(HttpServletRequest request, @RequestBody String postStr,
        HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "");
        if (StringUtils.isBlank(postStr)) {
            logger.warn("requestBody is blank");
            result.setSuccess(false);
            result.setMessage("传递参数为空");
            return result;
        }
        try {
            JSONObject formDataJson = JSONObject.parseObject(postStr);
            if (formDataJson == null || formDataJson.isEmpty()) {
                logger.warn("formData is blank");
                result.setSuccess(false);
                result.setMessage("传递参数为空");
                return result;
            }
            drbfmSingleService.updateQuotaEvaluate(formDataJson);
        } catch (Exception e) {
            logger.error("Exception in save saveOneQuotaEvaluate");
            result.setSuccess(false);
            result.setMessage("系统异常");
            return result;
        }
        return result;
    }

    @RequestMapping("openUploadWindow")
    public ModelAndView openUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "/drbfm/quotaFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String relQuotaId = RequestUtil.getString(request, "relQuotaId", "");
        String belongSingleId = RequestUtil.getString(request, "belongSingleId", "");
        mv.addObject("currentUser", ContextUtil.getCurrentUser());
        mv.addObject("relQuotaId", relQuotaId);
        mv.addObject("belongSingleId", belongSingleId);
        return mv;
    }
    @RequestMapping("requestUploadWindow")
    public ModelAndView requestUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "/drbfm/requestFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String requestId = RequestUtil.getString(request, "requestId", "");
        String belongSingleId = RequestUtil.getString(request, "belongSingleId", "");
        mv.addObject("currentUser", ContextUtil.getCurrentUser());
        mv.addObject("requestId", requestId);
        mv.addObject("belongSingleId", belongSingleId);
        return mv;
    }

    // 文件相关

    @RequestMapping("upload")
    @ResponseBody
    public Map<String, Object> upload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        try {
            // 先保存文件到磁盘，成功后再写入数据库，前台是一个一个文件的调用
            drbfmSingleService.saveUploadFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            modelMap.put("success", "false");
        }
        return modelMap;
    }

    @RequestMapping("quotaFileList")
    @ResponseBody
    public List<JSONObject> quotaFileList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String relQuotaId = RequestUtil.getString(request, "relQuotaId", "");
        if (StringUtils.isBlank(relQuotaId)) {
            return null;
        }
        JSONObject params = new JSONObject();
        params.put("relQuotaId", relQuotaId);
        List<JSONObject> fileList = drbfmTestTaskService.queryDemandList(params);
        return fileList;
    }
    @RequestMapping("requestFileList")
    @ResponseBody
    public List<JSONObject> requestFileList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String requestId = RequestUtil.getString(request, "requestId", "");
        if (StringUtils.isBlank(requestId)) {
            return null;
        }
        JSONObject params = new JSONObject();
        params.put("requestId", requestId);
        List<JSONObject> fileList = drbfmTestTaskService.queryDemandList(params);
        return fileList;
    }

    @RequestMapping("stopOldQuota")
    @ResponseBody
    public JsonResult stopOldQuota(HttpServletRequest request, HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "操作成功");

        try {
            drbfmSingleService.stopOldQuota(RequestUtil.getString(request, "quotaId", ""), result);
        } catch (Exception e) {
            logger.error("Exception in stopOldQuota");
            result.setSuccess(false);
            result.setMessage("系统异常");
            return result;
        }
        return result;
    }

    @RequestMapping("singleFinalProcessPage")
    public ModelAndView singleFinalProcessPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "drbfm/singleFinalProcessPage.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String singleId = RequestUtil.getString(request, "singleId");
        String action = RequestUtil.getString(request, "action");
        String stageName = RequestUtil.getString(request, "stageName");
        mv.addObject("stageName", stageName);
        mv.addObject("singleId", singleId);
        mv.addObject("action", action);
        return mv;
    }

    // 检查风险分析分解内容是否填写完成
    @RequestMapping("checkRiskDecompose")
    @ResponseBody
    public JsonResult checkFile(HttpServletRequest request, HttpServletResponse response) {
        String singleId = RequestUtil.getString(request, "singleId");
        return drbfmSingleService.checkRiskDecompose(singleId);
    }

    // 检查风险验证及综合评价内容是否完成
    @RequestMapping("checkTestTaskAndResult")
    @ResponseBody
    public JSONObject checkTestTaskAndResult(HttpServletRequest request, HttpServletResponse response) {
        String singleId = RequestUtil.getString(request, "singleId");
        return drbfmSingleService.checkTestTaskAndResult(singleId);
    }

    // 检查改进后处理是否完成
    @RequestMapping("checkFinalProcess")
    @ResponseBody
    public JsonResult checkFinalProcess(HttpServletRequest request, HttpServletResponse response) {
        String singleId = RequestUtil.getString(request, "singleId");
        return drbfmSingleService.checkFinalProcess(singleId);
    }

    @RequestMapping("updateRequestRiskLevelFinal")
    @ResponseBody
    public JsonResult updateRequestRiskLevelFinal(HttpServletRequest request, @RequestBody String formDataStr,
        HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "");
        if (StringUtils.isBlank(formDataStr)) {
            logger.warn("requestBody is blank");
            result.setSuccess(false);
            result.setMessage("传递参数为空");
            return result;
        }
        try {
            JSONObject formDataJson = JSONObject.parseObject(formDataStr);
            drbfmSingleDao.updateRequestRiskLevelFinal(formDataJson);
        } catch (Exception e) {
            logger.error("Exception in save updateRequestRiskLevelFinal");
            result.setSuccess(false);
            result.setMessage("系统异常");
            return result;
        }
        return result;
    }

    @RequestMapping("deleteNextWork")
    @ResponseBody
    public JsonResult deleteNextWork(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            JsonResult result = new JsonResult(true, "操作成功！");
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            List<String> idList = Arrays.asList(ids);
            JSONObject param = new JSONObject();
            param.put("ids", idList);
            drbfmSingleDao.deleteNextWork(param);
            return result;
        } catch (Exception e) {
            logger.error("Exception in deleteNextWork", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("queryNextWork")
    @ResponseBody
    public List<JSONObject> queryNextWork(HttpServletRequest request, HttpServletResponse response) {
        String belongSingleId = RequestUtil.getString(request, "belongSingleId");
        String testTaskId = RequestUtil.getString(request, "testTaskId");
        if (StringUtils.isBlank(belongSingleId) && StringUtils.isBlank(testTaskId)) {
            return Collections.emptyList();
        }
        Map<String, Object> param = new HashMap<>();
        if (StringUtils.isNotBlank(belongSingleId)) {
            param.put("belongSingleId", belongSingleId);
        }
        if (StringUtils.isNotBlank(testTaskId)) {
            param.put("relTestTaskId", testTaskId);
        }
        List<JSONObject> dataList = drbfmSingleDao.queryNextWork(param);
        for (JSONObject oneData : dataList) {
            if (oneData.getDate("finishTime") != null) {
                oneData.put("finishTime", DateFormatUtil.format(oneData.getDate("finishTime"), "yyyy-MM-dd"));
            }
            if (oneData.getDate("CREATE_TIME_") != null) {
                oneData.put("CREATE_TIME_", DateFormatUtil.format(oneData.getDate("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        return dataList;
    }

    @RequestMapping("saveNextWork")
    @ResponseBody
    public JsonResult saveNextWork(HttpServletRequest request, @RequestBody String postStr,
        HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "");
        if (StringUtils.isBlank(postStr)) {
            logger.warn("requestBody is blank");
            result.setSuccess(false);
            result.setMessage("传递参数为空");
            return result;
        }
        try {
            JSONObject formDataJson = JSONObject.parseObject(postStr);
            drbfmSingleService.saveNextWork(formDataJson);
        } catch (Exception e) {
            logger.error("Exception in save saveNextWork");
            result.setSuccess(false);
            result.setMessage("系统异常");
            return result;
        }
        return result;
    }

    @RequestMapping("deleteSingles")
    @ResponseBody
    public JsonResult deleteSingles(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String instIdStr = RequestUtil.getString(request, "instIds");
            if (StringUtils.isBlank(uIdStr) || StringUtils.isBlank(instIdStr)) {
                return new JsonResult(false, "未找到要操作的数据");
            }
            String[] ids = uIdStr.split(",");
            String[] instIds = instIdStr.split(",");
            return drbfmSingleService.deleteSingles(ids, instIds);
        } catch (Exception e) {
            logger.error("Exception in deleteSingles", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("demandCollectStart")
    @ResponseBody
    public JsonResult demandCollectStart(HttpServletRequest request, HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "流程创建成功");
        drbfmSingleService.createAndStartDemandCollect(request, result);
        return result;
    }

    // 部件项目需求收集编辑页面
    @RequestMapping("demandCollectPage")
    public ModelAndView demandCollectPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "drbfm/drbfmDemandCollectEdit.jsp";
        // 新增或者编辑页面由流程引擎跳转并替换url中的pk和nodeId_
        String applyId = RequestUtil.getString(request, "applyId");
        String nodeId = RequestUtil.getString(request, "nodeId");
        String action = RequestUtil.getString(request, "action");
        String status = RequestUtil.getString(request, "status");
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
        // 取出节点变量，返回到页面
        List<Map<String, String>> nodeVars = new ArrayList<>();
        if (StringUtils.isNotBlank(nodeId)) {
            nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId, "drbfmSingleDemandCollect", null);
        }
        String stageName = "";
        if (nodeVars != null && !nodeVars.isEmpty()) {
            for (int i = 0; i < nodeVars.size(); i++) {
                if (nodeVars.get(i).get("KEY_").equals("stageName")) {
                    stageName = nodeVars.get(i).get("DEF_VAL_");
                }
            }
        }
        mv.addObject("stageName", stageName);
        mv.addObject("applyId", applyId);
        mv.addObject("status", status);
        mv.addObject("action", action);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        return mv;
    }

    @RequestMapping("getDeptDemandCollectJson")
    @ResponseBody
    public JSONObject getDeptDemandCollectJson(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        String id = RequestUtil.getString(request, "id", "");
        if (StringUtils.isBlank(id)) {
            JSONObject result = new JSONObject();
            return result;
        }
        return drbfmSingleService.getDeptDemandCollectJson(id);
    }

    @RequestMapping("deptDemandCollectListPage")
    public ModelAndView deptDemandListPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "drbfm/drbfmDemandCollectList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("belongSingleId", RequestUtil.getString(request, "belongSingleId", ""));
        return mv;
    }

    @RequestMapping("getDeptDemandCollectList")
    @ResponseBody
    public JsonPageResult<?> getDeptDemandCollectList(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        return drbfmSingleService.getDeptDemandCollectList(request, response, true);
    }

    @RequestMapping("deleteDemandCollect")
    @ResponseBody
    public JsonResult deleteDemandCollect(HttpServletRequest request, HttpServletResponse response) throws Exception {
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
            return drbfmSingleService.deleteDemandCollect(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteDemandCollect", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("getOneCollectDemandList")
    @ResponseBody
    public List<JSONObject> getOneCollectDemandList(HttpServletRequest request, HttpServletResponse response) {
        String belongCollectFlowId = RequestUtil.getString(request, "belongCollectFlowId");
        String filterParams = request.getParameter("filter");
        return drbfmSingleService.getOneCollectDemandList(belongCollectFlowId, filterParams);
    }

    @RequestMapping("exportFunction")
    @ResponseBody
    public void exportFunction(HttpServletRequest request, HttpServletResponse response) {
        String singleId = RequestUtil.getString(request, "singleId");
        drbfmSingleService.exportFunction(singleId, request, response);
    }

    @RequestMapping("exportRequest")
    @ResponseBody
    public void exportRequest(HttpServletRequest request, HttpServletResponse response) {
        String singleId = RequestUtil.getString(request, "singleId");
        drbfmSingleService.exportRequest(singleId, request, response);
    }

    @RequestMapping("exportQuota")
    @ResponseBody
    public void exportQuota(HttpServletRequest request, HttpServletResponse response) {
        String singleId = RequestUtil.getString(request, "singleId");
        drbfmSingleService.exportQuota(singleId, request, response);
    }

    @RequestMapping("exportAll")
    @ResponseBody
    public void exportAll(HttpServletRequest request, HttpServletResponse response) {
        String singleId = RequestUtil.getString(request, "singleId");
        drbfmSingleService.exportAll(singleId, request, response);
    }

    @RequestMapping("drbfmAllSingleTree")
    public ModelAndView drbfmAllSingleTree(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "drbfm/drbfmAllSingleTree.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String singleId = RequestUtil.getString(request, "singleId");
        JSONObject singleObj = drbfmSingleDao.querySingleBaseById(singleId);
        mv.addObject("id", singleObj.getString("belongTotalId"));
        return mv;
    }

    @RequestMapping("interfaceCollectListPage")
    public ModelAndView interfaceCollectListPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "drbfm/drbfmSingleInterfaceCollectList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("belongSingleId", RequestUtil.getString(request, "belongSingleId", ""));
        return mv;
    }

    // APQP部件验证接口需求收集
    @RequestMapping("interfaceCollectPage")
    public ModelAndView singlePage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "drbfm/singleInterfaceCollectFramePage.jsp";
        // 新增或者编辑页面由流程引擎跳转并替换url中的pk和nodeId_
        String id = RequestUtil.getString(request, "applyId");
        String structId = RequestUtil.getString(request, "id");
        String nodeId = RequestUtil.getString(request, "nodeId");
        String action = RequestUtil.getString(request, "action");
        String status = RequestUtil.getString(request, "status");
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
        // 取出节点变量，返回到页面
        List<Map<String, String>> nodeVars = new ArrayList<>();
        if (StringUtils.isNotBlank(nodeId)) {
            nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId, "drbfmSingleInterfaceCollect", null);
        }
        String stageName = "";
        if (nodeVars != null && !nodeVars.isEmpty()) {
            for (int i = 0; i < nodeVars.size(); i++) {
                if (nodeVars.get(i).get("KEY_").equals("stageName")) {
                    stageName = nodeVars.get(i).get("DEF_VAL_");
                }
            }
        }
        mv.addObject("id", id);
        mv.addObject("collectType", "collectType");
        mv.addObject("stageName", stageName);
        mv.addObject("structId", structId);
        mv.addObject("status", status);
        mv.addObject("action", action);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        return mv;
    }

    @RequestMapping("getTotalStructDetail")
    @ResponseBody
    public JSONObject getTotalStructDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject obj = new JSONObject();
        String id = RequestUtil.getString(request, "id");
        if (StringUtils.isNotBlank(id)) {
            obj = drbfmSingleService.getTotalStructDetail(id);
        }
        return obj;
    }

    @RequestMapping("getInterfaceCollectList")
    @ResponseBody
    public JsonPageResult<?> getInterfaceCollectList(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        return drbfmSingleService.getInterfaceCollectList(request, response, true);
    }

    @RequestMapping("getFunctionInterfaceList")
    @ResponseBody
    public List<JSONObject> getFunctionInterfaceList(HttpServletRequest request, HttpServletResponse response) {
        String belongCollectFlowId = RequestUtil.getString(request, "belongCollectFlowId");
        String filterParams = request.getParameter("filter");
        return drbfmSingleService.getFunctionInterfaceList(belongCollectFlowId, filterParams);
    }

    // 要求分解
    @RequestMapping("getRequestInterfaceList")
    @ResponseBody
    public List<JSONObject> getRequestInterfaceList(HttpServletRequest request, HttpServletResponse response) {
        String belongCollectFlowId = RequestUtil.getString(request, "belongCollectFlowId");
        String filterParams = request.getParameter("filter");
        return drbfmSingleService.getRequestInterfaceList(belongCollectFlowId, filterParams);
    }

    // 指标分解
    @RequestMapping("getQuotaInterfaceList")
    @ResponseBody
    public List<JSONObject> getQuotaInterfaceList(HttpServletRequest request, HttpServletResponse response) {
        String belongCollectFlowId = RequestUtil.getString(request, "belongCollectFlowId");
        String filterParams = request.getParameter("filter");
        return drbfmSingleService.getQuotaInterfaceList(belongCollectFlowId, filterParams);
    }

    @RequestMapping("querySingleIdInterface")
    @ResponseBody
    public String querySingleIdInterface(HttpServletRequest request, HttpServletResponse response) {
        return drbfmSingleService.querySingleIdInterface(request, response, false);
    }

    @RequestMapping("queryInstIdIdInterface")
    @ResponseBody
    public String queryInstIdIdInterface(HttpServletRequest request, HttpServletResponse response) {
        return drbfmSingleService.queryInstIdIdInterface(request, response, false);
    }

    @RequestMapping("deleteInterfaceCollect")
    @ResponseBody
    public JsonResult deleteInterfaceCollect(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
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
            return drbfmSingleService.deleteInterfaceCollect(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteInterfaceCollect", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    // 检查流程对应的接口收集需求项目是否全部完成
    @RequestMapping("checkSingleInterface")
    @ResponseBody
    public JsonResult checkSingleInterface(HttpServletRequest request, HttpServletResponse response) {
        String singleId = RequestUtil.getString(request, "singleId");
        return drbfmSingleService.checkSingleInterface(singleId);
    }

    @RequestMapping("selectRiskLevel")
    public ModelAndView selectRiskLevel(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "drbfm/selectRiskLevel.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String requestId = RequestUtil.getString(request, "requestId");
//        JSONObject singleObj = drbfmSingleDao.querySingleBaseById(singleId);
        mv.addObject("id", requestId);
        return mv;
    }
    @RequestMapping("selectRiskLevelFinal")
    public ModelAndView selectRiskLevelFinal(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "drbfm/selectRiskLevelFinal.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String requestId = RequestUtil.getString(request, "requestId");
//        JSONObject singleObj = drbfmSingleDao.querySingleBaseById(singleId);
        mv.addObject("id", requestId);
        return mv;
    }
    // SOD
    @RequestMapping("fashengdu")
    public ModelAndView fashengdu(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "drbfm/SOD/fashengdu.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        return mv;
    }
    @RequestMapping("showFashengdu")
    @ResponseBody
    public List<JSONObject> showFashengdu(HttpServletRequest request, HttpServletResponse response) {
        return drbfmSingleService.fashengduPage(request);
    }
    @RequestMapping("yanzhongdu")
    public ModelAndView yanzhongdu(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "drbfm/SOD/yanzhongdu.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        return mv;
    }

    @RequestMapping("showYanzhongdu")
    @ResponseBody
    public List<JSONObject> showYanzhongdu(HttpServletRequest request, HttpServletResponse response) {
        return drbfmSingleService.yanzhongduPage(request);
    }

    @RequestMapping("tancedu")
    public ModelAndView tancedu(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "drbfm/SOD/tancedu.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        return mv;
    }

    @RequestMapping("showTancedu")
    @ResponseBody
    public List<JSONObject> showTancedu(HttpServletRequest request, HttpServletResponse response) {
        return drbfmSingleService.tanceduPage(request);
    }

    // SOD
    @RequestMapping("getRiskLevelSOD")
    @ResponseBody
    public JSONObject getRiskLevelSOD(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject riskSODObj = new JSONObject();
        String requestId = RequestUtil.getString(request, "requestId");
        if (StringUtils.isNotBlank(requestId)) {
            riskSODObj = drbfmSingleService.getRiskLevelSOD(requestId);
        }
        return riskSODObj;
    }
    @RequestMapping("getRiskLevelFinalSOD")
    @ResponseBody
    public JSONObject getRiskLevelFinalSOD(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject riskSODObj = new JSONObject();
        String requestId = RequestUtil.getString(request, "requestId");
        if (StringUtils.isNotBlank(requestId)) {
            riskSODObj = drbfmSingleService.getRiskLevelFinalSOD(requestId);
        }
        return riskSODObj;
    }
    @RequestMapping("saveSOD")
    @ResponseBody
    public JsonResult saveSOD(HttpServletRequest request, @RequestBody JSONObject formDataJson,
                              HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "成功");
        try {
            String id = RequestUtil.getString(request, "id");
            if (formDataJson == null || formDataJson.isEmpty()) {
                logger.warn("formData is blank");
                result.setSuccess(false);
                result.setMessage("requestBody is blank");
                return result;
            }
            if (StringUtils.isBlank(id)) {
                result.setSuccess(false);
                result.setMessage("请先点击保存一下要求编辑框再打分");
                return result;
            } else {
                formDataJson.put("id",id);
                drbfmSingleDao.insertRiskLevelSOD(formDataJson);
            }
        } catch (Exception e) {
            logger.error("Exception in save SOD");
            result.setSuccess(false);
            result.setMessage("Exception in save SOD");
            return result;
        }
        return result;
    }
    @RequestMapping("saveSODFinal")
    @ResponseBody
    public JsonResult saveSODFinal(HttpServletRequest request, @RequestBody JSONObject formDataJson,
                              HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "成功");
        try {
            String id = RequestUtil.getString(request, "id");
            if (formDataJson == null || formDataJson.isEmpty()) {
                logger.warn("formData is blank");
                result.setSuccess(false);
                result.setMessage("requestBody is blank");
                return result;
            }
            if (StringUtils.isBlank(id)) {
                result.setSuccess(false);
                result.setMessage("请先点击保存一下要求编辑框再打分");
                return result;
            } else {
                formDataJson.put("id",id);
                drbfmSingleDao.insertRiskLevelSODFinal(formDataJson);
            }
        } catch (Exception e) {
            logger.error("Exception in save SODFinal");
            result.setSuccess(false);
            result.setMessage("Exception in save SODFinal");
            return result;
        }
        return result;
    }
    //复制引入其他单一流程中的 功能，要求和分解内容
    @RequestMapping("copySingleProcess")
    @ResponseBody
    public JsonResult copySingleProcess(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String singleId = RequestUtil.getString(request, "singleId");
            String[] ids = uIdStr.split(",");
            return drbfmSingleService.copySingleProcess(ids, singleId);
        } catch (Exception e) {
            logger.error("Exception in copySingleProcess", e);
            return new JsonResult(false, "服务器错误，请联系系统管理员");
        }
    }

    // 导出
    @PostMapping("exportSingleExcel")
    public void exportSingleExcel(HttpServletResponse response, HttpServletRequest request) {
        drbfmSingleService.exportSingle(response, request);
    }

    // 获取失效模式列表 -在功能需求描述的页签-要求中使用
    @RequestMapping("getSxmsList")
    @ResponseBody
    public List<JSONObject> getSxmsList(HttpServletRequest request, HttpServletResponse response) {
        String yqId = RequestUtil.getString(request, "yqId");
        JSONObject params = new JSONObject();
        params.put("yqId", yqId);
        return drbfmSingleService.querySxmsList(params);
    }

    // 获取部件功能描述列表 -- 在特性要求筛选使用
    @RequestMapping("getFunctionListByPartId")
    @ResponseBody
    public List<JSONObject> getFunctionListByPartId(HttpServletRequest request, HttpServletResponse response) {
        String partId = RequestUtil.getString(request, "partId");
        JSONObject params = new JSONObject();
        params.put("belongSingleId", partId);
        List<JSONObject> sxmsList = drbfmSingleDao.queryFunctionListByPartId(params);
        return sxmsList;
    }



    // 获取部件失效模式列表 -- 在功能失效网页签使用
    @RequestMapping("getSingleSxmsListById")
    @ResponseBody
    public List<JSONObject> getSingleSxmsListById(HttpServletRequest request, HttpServletResponse response) {
        String partId = RequestUtil.getString(request, "partId");
        JSONObject params = new JSONObject();
        params.put("belongSingleId", partId);
        List<JSONObject> sxmsList = drbfmSingleDao.querySxmsListBySingleId(params);
        return sxmsList;
    }

    // 分页显示当前部件的可选失效模式列表
    @RequestMapping("getSelectSxmsList")
    @ResponseBody
    public JsonPageResult<?> getSelectSxmsList(HttpServletRequest request, HttpServletResponse response) {
        String partId = RequestUtil.getString(request, "partId");
        JSONObject params = new JSONObject();
        params.put("belongSingleId", partId);
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
        rdmZhglUtil.addPage(request, params);
        return drbfmSingleService.getSelectSxmsList(params);
    }


    // 分页显示当前部件的可选失效模式及对应的特性要求列表
    @RequestMapping("getRequestAndSxmsList")
    @ResponseBody
    public JsonPageResult<?> getRequestAndSxmsList(HttpServletRequest request, HttpServletResponse response) {
        String partId = RequestUtil.getString(request, "partId");
        JSONObject params = new JSONObject();
        params.put("belongSingleId", partId);
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
        rdmZhglUtil.addPage(request, params);
        return drbfmSingleService.getRequestAndSxmsList(params);
    }

    // 获取部件失效模式列表 -- 在功能失效网页签使用
    @RequestMapping("getSingleSxmsList")
    @ResponseBody
    public JSONObject getSingleSxmsList(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        result.put("success", true);
        String partId = RequestUtil.getString(request, "partId");
        String sxms = RequestUtil.getString(request, "sxms");
        String functionDesc = RequestUtil.getString(request, "functionDesc");
        String requestDesc = RequestUtil.getString(request, "requestDesc");
        String gnfxxj = RequestUtil.getString(request, "gnfxxj");
        String jgfxxj = RequestUtil.getString(request, "jgfxxj");
        String upSearch = RequestUtil.getString(request, "upSearch");
        String downSearch = RequestUtil.getString(request, "downSearch");
        JSONObject params = new JSONObject();
        params.put("partId", partId);
        params.put("sxms", sxms);
        params.put("functionDesc", functionDesc);
        params.put("requestDesc", requestDesc);
        params.put("upSearch", upSearch);
        params.put("downSearch", downSearch);
        params.put("gnfxxj", gnfxxj);
        params.put("jgfxxj", jgfxxj);
        drbfmSingleService.querySingleSxmsList(params,result);
        return result;
    }


    // 获取部件风险失效分析 -- 在风险失效分析页签使用
    @RequestMapping("getRiskAnalysisList")
    @ResponseBody
    public JSONObject getRiskAnalysisList(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        String partId = RequestUtil.getString(request, "partId");
        String functionDesc = RequestUtil.getString(request, "functionDesc");
        String requestDesc = RequestUtil.getString(request, "requestDesc");
        String csyxj = RequestUtil.getString(request, "csyxj");
        String sxms = RequestUtil.getString(request, "sxms");
        String sxqy = RequestUtil.getString(request, "sxqy");
        JSONObject params = new JSONObject();
        params.put("partId", partId);
        params.put("functionDesc", functionDesc);
        params.put("requestDesc", requestDesc);
        params.put("csyxj", csyxj);
        params.put("sxms", sxms);
        params.put("sxqy", sxqy);
        result.put("success", true);
        drbfmSingleService.queryRiskAnalysisList(params,result);
        return result;
//        return drbfmSingleService.queryRiskAnalysisList(params);
    }





    // 获取当前失效模式的关系网，在查看并关联后弹窗使用
    @RequestMapping("getSxmsNetList")
    @ResponseBody
    public List<JSONObject> getSxmsNetList(HttpServletRequest request, HttpServletResponse response) {
        String sxmsId = RequestUtil.getString(request, "sxmsId");
        JSONObject params = new JSONObject();
        params.put("sxmsId", sxmsId);
        return drbfmSingleService.getSxmsNetList(params);
    }

    @RequestMapping("getFxpgList")
    @ResponseBody
    public List<JSONObject> getFxpgList(HttpServletRequest request, HttpServletResponse response) {
        String partId = RequestUtil.getString(request, "partId");
        JSONObject params = new JSONObject();
        params.put("partId", partId);
        return drbfmSingleService.getSxmsNetList(params);
    }




    // 获取整机失效模式列表 -- 在筛选界面使用
    @RequestMapping("getModelSxmsList")
    @ResponseBody
    public JsonPageResult<?> getModelSxmsList(HttpServletRequest request, HttpServletResponse response) {
        String partId = RequestUtil.getString(request, "partId");
        String baseId = RequestUtil.getString(request, "baseId");
        String relType = RequestUtil.getString(request, "relType");
        JSONObject params = new JSONObject();
        //baseId为空,是在风险时效分析中选择关联，此时只需要筛选当前部件即可
        if (StringUtils.isBlank(baseId)) {
            params.put("partId", partId);
            return drbfmSingleService.getModelSxmsList(params);
        }


        JSONObject detailObj = drbfmSingleDao.querySingleBaseById(partId);
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
        String jixing = detailObj.getString("jixing");
        String structId = detailObj.getString("structId");
        String belongTotalId = detailObj.getString("belongTotalId");
        params.put("jixing", jixing);
        params.put("baseId", baseId);
        params.put("belongTotalId", belongTotalId);
        String pathIdsStr = detailObj.getString("pathIds");
        String[] pathIds = pathIdsStr.split("\\.");
        //加个分页 太卡了
        rdmZhglUtil.addPage(request, params);


        //todo 限制条件太多没法走流程，当前版本先不放开 2023年8月2日10:57:39
//        if ("up".equalsIgnoreCase(relType)) {
//            // 向上关联只显示上级件
//            params.put("pathIds", pathIds);
//            params.put("structId", structId);
//        }
//
//        if ("down".equalsIgnoreCase(relType)) {
//            // 向下关联只显示下级件，自己【同级】的有接口名称的要求，和【上级】上一级有接口名称的要求
//            params.put("xjbj", "yes");
//            //如果有同级件
//            String parentId = "";
//            if (pathIds.length > 2) {
//                //大于二说明有父节点有同级件
//                parentId = pathIds[pathIds.length-2];
//                params.put("sjtjbj", "yes");
//            }
//            //没有父节点用不到这个参数
//            params.put("parentId", parentId);
//        }


        return drbfmSingleService.getModelSxmsList(params);
    }



    @RequestMapping("createSxmsRel")
    @ResponseBody
    public JsonResult createSxmsRel(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String baseId = RequestUtil.getString(request, "baseId");
            String relId = RequestUtil.getString(request, "relIds");
            String partId = RequestUtil.getString(request, "partId");
            String relType = RequestUtil.getString(request, "relType");
            JSONObject params = new JSONObject();
            if ("up".equalsIgnoreCase(relType)) {
                String temp = baseId;
                baseId = relId;
                relId = temp;
                String[] baseIds = baseId.split(",");
                params.put("baseIds", baseIds);
                params.put("relId", relId);
            } else {
                params.put("baseId", baseId);
                String[] relIds = relId.split(",");
                params.put("relIds", relIds);
            }
            params.put("partId", partId);

            return drbfmSingleService.createSxmsRel(params,relType);
        } catch (Exception e) {
            logger.error("Exception in createSxmsRel", e);
            return new JsonResult(false, e.getMessage());
        }
    }


    @RequestMapping("deleteSxmsRel")
    @ResponseBody
    public JsonResult deleteSxmsRel(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String id = RequestUtil.getString(request, "id");
            JSONObject params = new JSONObject();
            params.put("id", id);
            return drbfmSingleService.deleteSxmsRel(params);
        } catch (Exception e) {
            logger.error("Exception in deleteApply", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("saveRiskAnalysis")
    @ResponseBody
    public JsonResult saveRiskAnalysis(HttpServletRequest request, @RequestBody JSONObject formDataJson,
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
                drbfmSingleService.insertRiskAnalysis(formDataJson);
            } else {
                drbfmSingleService.updateRiskAnalysis(formDataJson);
            }
            if (StringUtils.isBlank(formDataJson.getString("sxmsfxId"))) {
                drbfmSingleService.insertRiskAnalysisSxms(formDataJson);
            } else {
                drbfmSingleService.updateRiskAnalysisSxms(formDataJson);
            }
        } catch (Exception e) {
            logger.error("Exception in saveFxsxfx");
            result.setSuccess(false);
            result.setMessage("Exception in saveFxsxfx");
            return result;
        }
        result.setData(formDataJson.getString("id"));
        return result;
    }


    @RequestMapping("getRiskAnalysisDetail")
    @ResponseBody
    public JSONObject getRiskAnalysisDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject ceinfoobj = new JSONObject();
        String sxmsId = RequestUtil.getString(request, "sxmsId");
        String sxyyId = RequestUtil.getString(request, "sxyyId");
        String partId = RequestUtil.getString(request, "partId");
         if (StringUtils.isNotBlank(sxmsId)&&StringUtils.isNotBlank(sxyyId)){
            ceinfoobj = drbfmSingleService.getRiskAnalysisDetail(sxmsId,sxyyId,partId);
        }
        return ceinfoobj;
    }


    @RequestMapping("saveNewSxms")
    @ResponseBody
    public JsonResult saveNewSxms(HttpServletRequest request, @RequestBody JSONObject formDataJson,
                                       HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "成功");
        try {
            if (formDataJson == null || formDataJson.isEmpty()) {
                logger.warn("formData is blank");
                result.setSuccess(false);
                result.setMessage("requestBody is blank");
                return result;
            }
            //保存新的失效模式
//            drbfmSingleService.insertRiskAnalysis(formDataJson);

//            JSONObject insertObj = new JSONObject();
            String id = IdUtil.getId();

            formDataJson.put("id", id);
            formDataJson.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            formDataJson.put("CREATE_TIME_", new Date());
            drbfmSingleDao.insertSxms(formDataJson);
                //建立关系
            JSONObject params = new JSONObject();
            String baseId = formDataJson.getString("chooseSxmsId");
            String partId = formDataJson.getString("partId");
            params.put("id", IdUtil.getId());
            params.put("baseId", baseId);
            params.put("relId", id);
            params.put("partId", partId);
            params.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            params.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            drbfmSingleDao.insertSxmsRel(params);


        } catch (Exception e) {
            logger.error("Exception in saveNewSxms");
            result.setSuccess(false);
            result.setMessage("Exception in saveNewSxms");
            return result;
        }
        result.setData(formDataJson.getString("id"));
        return result;
    }

    @RequestMapping("deleteSxyy")
    @ResponseBody
    public JsonResult deleteSxyy(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String id = RequestUtil.getString(request, "id");
            JSONObject params = new JSONObject();
            params.put("id", id);
            return drbfmSingleService.deleteSxyy(params);
        } catch (Exception e) {
            logger.error("Exception in deleteApply", e);
            return new JsonResult(false, e.getMessage());
        }
    }


    // 检查风险验证及综合评价内容是否完成
    @RequestMapping("checkFunctionNetStatus")
    @ResponseBody
    public JsonResult checkFunctionNetStatus(HttpServletRequest request, HttpServletResponse response) {
        String partId = RequestUtil.getString(request, "partId");
        String startOrEnd = RequestUtil.getString(request, "startOrEnd");
        JSONObject params = new JSONObject();
        params.put("partId", partId);
//        JSONObject res = drbfmSingleService.checkFunctionNetStatus(params,startOrEnd);
//        return res;
        return drbfmSingleService.checkFunctionNetStatus(params,startOrEnd);
    }


    // 获取变化维度列表
    @RequestMapping("getChangeDimensionList")
    @ResponseBody
    public List<JSONObject> getChangeDimensionList(HttpServletRequest request, HttpServletResponse response) {
        JSONObject params = new JSONObject();
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
        return drbfmSingleDao.getChangeDimensionList(params);
    }

    // 获取失效模式列表 -在功能需求描述的页签-要求中使用
    @RequestMapping("getRiskList")
    @ResponseBody
    public List<JSONObject> getRiskList(HttpServletRequest request, HttpServletResponse response) {
        String yqId = RequestUtil.getString(request, "yqId");
        JSONObject params = new JSONObject();
        params.put("yqId", yqId);
        return drbfmSingleService.queryRiskList(params);
    }


    @RequestMapping("singleChangeInfoPage")
    public ModelAndView singleChangeInfoPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "drbfm/singleChangeInfoPage.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String singleId = RequestUtil.getString(request, "singleId");
        String action = RequestUtil.getString(request, "action");
        String stageName = RequestUtil.getString(request, "stageName");
        mv.addObject("stageName", stageName);
        mv.addObject("singleId", singleId);
        mv.addObject("action", action);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        return mv;
    }

    // 获取部件失效模式列表 -- 在功能失效网页签使用
    @RequestMapping("getSingleChangeList")
    @ResponseBody
    public List<JSONObject> getSingleChangeList(HttpServletRequest request, HttpServletResponse response) {
        String partId = RequestUtil.getString(request, "partId");
        JSONObject params = new JSONObject();
        params.put("partId", partId);
        return drbfmSingleService.querySingleChangeList(params);
    }

    //保存新建的变化点
    @RequestMapping("saveChange")
    @ResponseBody
    public JsonResult saveChange(HttpServletRequest request, @RequestBody JSONObject formDataJson,
                                   HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "成功");
        try {
            if (formDataJson == null || formDataJson.isEmpty()) {
                logger.warn("formData is blank");
                result.setSuccess(false);
                result.setMessage("requestBody is blank");
                return result;
            }
            String uIdStr = formDataJson.getString("relTxId");
            String[] ids = uIdStr.split(",");
            String sxStr = formDataJson.getString("relSxId");
            String[] sxIds = sxStr.split(",");
            for (int i = 0; i < ids.length; i++) {
                formDataJson.put("yqId", ids[i]);
                formDataJson.put("relSxmsId", sxIds[i]);
                formDataJson.put("id", IdUtil.getId());
                drbfmSingleDao.insertDimension(formDataJson);
            }
        } catch (Exception e) {
            logger.error("Exception in saveChange");
            result.setSuccess(false);
            result.setMessage("Exception in saveChange");
            return result;
        }
        result.setData(formDataJson.getString("id"));
        return result;
    }

    //2024年3月7日08:37:27 新增经验教训模块
    @RequestMapping("singleExpInfoPage")
    public ModelAndView singleExpInfoPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "drbfm/singleExpInfoPage.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String singleId = RequestUtil.getString(request, "singleId");
        String action = RequestUtil.getString(request, "action");
        String stageName = RequestUtil.getString(request, "stageName");
        mv.addObject("stageName", stageName);
        mv.addObject("singleId", singleId);
        mv.addObject("action", action);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        return mv;
    }

    // 获取部件失效模式列表 -- 在功能失效网页签使用
    @RequestMapping("getSingleExpList")
    @ResponseBody
    public List<JSONObject> getSingleExpList(HttpServletRequest request, HttpServletResponse response) {
        String partId = RequestUtil.getString(request, "partId");
        JSONObject params = new JSONObject();
        params.put("partId", partId);
        return drbfmSingleService.querySingleExpList(params);
    }

    //保存关联的经验教训
    @RequestMapping("saveExpInfo")
    @ResponseBody
    public JsonResult saveExpInfo(HttpServletRequest request, @RequestBody JSONObject formDataJson,
                                 HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "成功");
        try {
            if (formDataJson == null || formDataJson.isEmpty()) {
                logger.warn("formData is blank");
                result.setSuccess(false);
                result.setMessage("requestBody is blank");
                return result;
            }
            String uIdStr = formDataJson.getString("relTxId");
            String[] ids = uIdStr.split(",");
            for (int i = 0; i < ids.length; i++) {
                formDataJson.put("yqId", ids[i]);
                formDataJson.put("id", IdUtil.getId());
                drbfmSingleDao.insertDimension(formDataJson);
            }
        } catch (Exception e) {
            logger.error("Exception in saveExpInfo");
            result.setSuccess(false);
            result.setMessage("Exception in saveExpInfo");
            return result;
        }
        result.setData(formDataJson.getString("id"));
        return result;
    }


    @RequestMapping("createExpRel")
    @ResponseBody
    public JsonResult createExpRel(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String partId = RequestUtil.getString(request, "partId");
            String relExpIds = RequestUtil.getString(request, "relExpIds");
            JSONObject params = new JSONObject();
            params.put("partId", partId);
            String[] relExpIdsList = relExpIds.split(",");
            params.put("relExpIds", relExpIdsList);

            return drbfmSingleService.createExpRel(params);
        } catch (Exception e) {
            logger.error("Exception in createExpRel", e);
            return new JsonResult(false, e.getMessage());
        }
    }


    @RequestMapping("deleteExpRel")
    @ResponseBody
    public JsonResult deleteExpRel(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String id = RequestUtil.getString(request, "id");
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            JSONObject params = new JSONObject();
            List<String> idsList = Arrays.asList(ids);
            params.put("id", id);
            params.put("ids", idsList);
            return drbfmSingleService.deleteExpRel(params);
        } catch (Exception e) {
            logger.error("Exception in deleteExpRel", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    /**
     * 经验教训模块：对于无法关联的历史文件，提供附件上传功能
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("openExpUploadWindow")
    public ModelAndView openExpUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "drbfm/expFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String belongSingleId = RequestUtil.getString(request, "belongSingleId", "");
        mv.addObject("currentUser", ContextUtil.getCurrentUser());
        mv.addObject("belongSingleId", belongSingleId);
        return mv;
    }
    @RequestMapping("saveExpFiles")
    public void saveExpFiles(HttpServletRequest request, HttpServletResponse response) throws Exception {
        drbfmSingleService.saveExpFiles(request);
    }

    @RequestMapping("queryExpFileList")
    @ResponseBody
    public List<JSONObject> queryExpFileList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String belongSingleId = RequestUtil.getString(request, "belongSingleId", "");
        if (StringUtils.isBlank(belongSingleId)) {
            return null;
        }
        JSONObject params = new JSONObject();
        params.put("belongSingleId", belongSingleId);
        List<JSONObject> fileList = drbfmSingleService.queryExpFileList(params);
        return fileList;
    }

    @RequestMapping("deleteFiles")
    public void deleteFiles(HttpServletRequest request, HttpServletResponse response, @RequestBody String postBody) {
        drbfmSingleService.deleteFile(postBody);

    }

    @RequestMapping("/preview")
    public ResponseEntity<byte[]> filePreview(HttpServletRequest request, HttpServletResponse response) {
        ResponseEntity<byte[]> result = null;
        String fileType = RequestUtil.getString(request, "fileType");
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = SysPropertiesUtil.getGlobalProperty("drbfm");
        switch (fileType) {
            case "pdf":
                result = rdmZhglFileManager.pdfPreviewOrDownLoad(fileName, fileId, formId, fileBasePath);
            case "office":
                rdmZhglFileManager.officeFilePreview(fileName, fileId, formId, fileBasePath, response);
                break;
            case "pic":
                rdmZhglFileManager.imageFilePreview(fileName, fileId, formId, fileBasePath, response);
                break;
        }
        return result;
    }

    @RequestMapping("/fileDownload")
    public ResponseEntity<byte[]> fileDownload(HttpServletRequest request, HttpServletResponse response) {
        try {
            String fileName = RequestUtil.getString(request, "fileName");
            if (StringUtils.isBlank(fileName)) {
                logger.error("操作失败，文件名为空！");
                return null;
            }
            String fileId = RequestUtil.getString(request, "fileId");
            if (StringUtils.isBlank(fileId)) {
                logger.error("操作失败，文件主键为空！");
                return null;
            }
            String formId = RequestUtil.getString(request, "formId");
            String fileBasePath = SysPropertiesUtil.getGlobalProperty("drbfm");
            if (StringUtils.isBlank(fileBasePath)) {
                logger.error("操作失败，找不到存储根路径");
                return null;
            }
            String suffix = CommonFuns.toGetFileSuffix(fileName);
            String relativeFilePath = "";
            if (StringUtils.isNotBlank(formId)) {
                relativeFilePath = File.separator + formId;
            }
            String realFileName = fileId + "." + suffix;
            String fullFilePath = fileBasePath + relativeFilePath + File.separator + realFileName;
            // 创建文件实例
            File file = new File(fullFilePath);
            // 修改文件名的编码格式
            String downloadFileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");

            // 设置httpHeaders,使浏览器响应下载
            HttpHeaders headers = new HttpHeaders();
            // 告诉浏览器执行下载的操作，“attachment”告诉了浏览器进行下载,下载的文件 文件名为 downloadFileName
            headers.setContentDispositionFormData("attachment", downloadFileName);
            // 设置响应方式为二进制，以二进制流传输
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file), headers, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Exception in DownloadExpFile", e);
            return null;
        }
    }
}
