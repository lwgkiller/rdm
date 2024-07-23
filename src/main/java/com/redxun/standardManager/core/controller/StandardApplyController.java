package com.redxun.standardManager.core.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
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
import com.redxun.org.api.model.IUser;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.controller.GenericController;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.standardManager.core.dao.StandardApplyDao;
import com.redxun.standardManager.core.manager.StandardApplyManager;
import com.redxun.standardManager.core.manager.StandardManager;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

@Controller
@RequestMapping("/standardManager/core/standardApply/")
public class StandardApplyController extends GenericController {
    @Autowired
    private StandardApplyManager standardApplyManager;
    @Autowired
    private StandardApplyDao applyDao;
    @Resource
    private CommonBpmManager commonBpmManager;
    @Autowired
    private StandardManager standardManager;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;

    @RequestMapping("list")
    public ModelAndView listPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "standardManager/core/standardApplyList.jsp";
        String applyCategoryId = RequestUtil.getString(request, "applyCategoryId");
        ModelAndView mv = new ModelAndView(jspPath);
        IUser currentUser = ContextUtil.getCurrentUser();
        mv.addObject("currentUser", currentUser);
        mv.addObject("applyCategoryId", applyCategoryId);
        return mv;
    }

    @RequestMapping("applyQuery")
    @ResponseBody
    public JsonPageResult<?> applyQuery(HttpServletRequest request, HttpServletResponse response) {
        return standardApplyManager.standardApplyList(request, response);
    }

    @RequestMapping("applyDel")
    @ResponseBody
    public JsonResult applyDel(HttpServletRequest request, HttpServletResponse response) {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String instIdStr = RequestUtil.getString(request, "instIds");
            if (StringUtils.isNotEmpty(uIdStr) && StringUtils.isNotEmpty(instIdStr)) {
                String[] ids = uIdStr.split(",");
                String[] instIds = instIdStr.split(",");
                return standardApplyManager.deleteApply(ids, instIds);
            }
        } catch (Exception e) {
            return new JsonResult(false, e.getMessage());
        }
        return new JsonResult(true, "成功删除!");
    }

    @RequestMapping("edit")
    public ModelAndView applyEdit(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = getPathView(request);
        // 新增或者编辑页面由流程引擎跳转并替换url中的pk和nodeId_
        String applyId = RequestUtil.getString(request, "applyId");
        String nodeId = RequestUtil.getString(request, "nodeId");
        // 对于直接从标准界面跳转到新增申请的场景，需要带过来选择的标准信息
        String standardId = RequestUtil.getString(request, "standardId");
        String action = "";
        String standardApplyCategoryId = RequestUtil.getString(request, "standardApplyCategoryId");
        String applyCategoryName = "preview".equalsIgnoreCase(standardApplyCategoryId) ? "预览" : "下载";
        // 新增或编辑的时候没有nodeId
        if (StringUtils.isBlank(nodeId) || nodeId.contains("PROCESS")) {
            action = "edit";
        } else {
            // 处理任务的时候有nodeId
            action = "task";
        }

        // 取出节点变量，返回到页面
        if (StringUtils.isNotBlank(nodeId)) {
            mv.addObject("nodeId", nodeId);
            List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId, "BZGLSQ", null);
            if (nodeVars != null && !nodeVars.isEmpty()) {
                mv.addObject("nodeVars", JSONObject.toJSONString(nodeVars));

            }
        }

        mv.addObject("action", action);
        Map<String, Object> standardApplyObj = null;
        if (StringUtils.isNotBlank(applyId)) {
            Map<String, Object> params = new HashMap<>();
            params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            params.put("applyId", applyId);
            standardApplyObj = applyDao.queryStandardApplyById(params);
            standardId = standardApplyObj.get("standardId").toString();
        }
        JSONObject standardApplyJsonObj = XcmgProjectUtil.convertMap2JsonObject(standardApplyObj);
        standardApplyJsonObj.put("applyCategoryId", standardApplyCategoryId);
        standardApplyJsonObj.put("applyCategoryName", applyCategoryName);
        if (StringUtils.isNotBlank(standardId)) {
            // 查询标准的信息
            Map<String, Object> standardInfo = standardManager.queryStandardById(standardId);
            if (standardInfo != null) {
                standardApplyJsonObj.put("standardId", standardId);
                standardApplyJsonObj.put("standardName", standardInfo.get("standardName").toString());
                standardApplyJsonObj.put("standardNumber", standardInfo.get("standardNumber").toString());
                standardApplyJsonObj.put("categoryName", standardInfo.get("categoryName").toString());
                standardApplyJsonObj.put("systemName", standardInfo.get("systemName").toString());
                standardApplyJsonObj.put("systemCategoryId", standardInfo.get("systemCategoryId").toString());
            }
        }
        mv.addObject("standardApplyJsonObj", standardApplyJsonObj);

        return mv;
    }

    // 判断预览或者下载是否需要申请(1---已有审批完成的未使用的申请，2---已有草稿或运行中的申请，3---需要增加新申请)
    @RequestMapping("checkOperateApply")
    @ResponseBody
    public Map<String, String> checkOperateApply(HttpServletRequest request, @RequestBody String requestBody,
        HttpServletResponse response) throws Exception {
        Map<String, String> result = new HashMap<>();
        if (StringUtils.isBlank(requestBody)) {
            logger.error("请求体为空！");
            result.put("result", "3");
            return result;
        }
        standardApplyManager.checkOperateApply(requestBody, result);
        return result;
    }

    // 更改申请单的使用状态
    @RequestMapping("changeUseStatus")
    @ResponseBody
    public Map<String, Object> changeUseStatus(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        Map<String, Object> result = new HashMap<>();
        String useStatus = RequestUtil.getString(request, "useStatus");
        String applyId = RequestUtil.getString(request, "applyId");
        if (StringUtils.isBlank(useStatus) || StringUtils.isBlank(applyId)) {
            logger.error("useStatus or applyId is blank");
            result.put("result", false);
            return result;
        }
        standardApplyManager.changeUseStatus(useStatus, applyId, result);
        return result;
    }

    /**
     * 判断对接人是否是技术标准管理人员
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("getIsJSZXGLRY")
    @ResponseBody
    public Map<String, Object> getIsJSZXGLRY(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        Map<String, Object> result = new HashMap<>();
        String standardPeopleId = RequestUtil.getString(request, "standardPeopleId");
        if (StringUtils.isBlank(standardPeopleId)) {
            logger.error(" standardPeopleId is blank");
            result.put("result", "false");
            return result;
        }
        boolean isJSZXGLRY = rdmZhglUtil.judgeIsPointRoleUser(standardPeopleId, "技术标准管理人员");
        if (isJSZXGLRY) {
            result.put("result", "true");
        } else {
            result.put("result", "false");
        }

        return result;
    }

    /**
     * 判断草案阶段是否有标准草案附件
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("getIsBzcn")
    @ResponseBody
    public Map<String, Object> getIsBzcn(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> result = new HashMap<>();
        String applyId = RequestUtil.getString(request, "applyId");
        String stageKey = RequestUtil.getString(request, "stageKey");
        if (StringUtils.isBlank(applyId)) {
            logger.error(" applyId is blank");
            result.put("result", "false");
            return result;
        }
        /*        if(stageKey!= null && "darft".equals(stageKey)){
            //草案阶段校验引用标准
            String referStandardIds = applyDao.selectReferStandardIds(applyId);
            if (referStandardIds == null || "".equals(referStandardIds)){
                result.put("result", "false");
                result.put("message", "请上传引用标准");
                return result;
            }
        }*/
        // String isBzcn = applyDao.selectIsBzcn(applyId);
        // 查询已填跟必填项数量
        List<String> currentList = applyDao.selectCurrentNum(applyId, stageKey);
        List<String> countList = applyDao.selectCountNum(stageKey);

        countList.removeAll(currentList);

        if (countList.size() == 0) {
            result.put("result", "true");
        } else {
            result.put("result", "false");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < countList.size(); i++) {
                if (i == countList.size() - 1) {
                    sb.append(countList.get(i));
                } else {
                    sb.append(countList.get(i)).append(",");
                }
            }
            result.put("message", "请上传必填项【" + sb.toString() + "】");
        }

        return result;
    }
}
