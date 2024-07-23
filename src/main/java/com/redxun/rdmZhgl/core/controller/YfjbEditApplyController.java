
package com.redxun.rdmZhgl.core.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.manager.MybatisBaseManager;
import com.redxun.org.api.model.IUser;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmZhgl.core.dao.YfjbBaseInfoDao;
import com.redxun.rdmZhgl.core.dao.YfjbEditApplyDao;
import com.redxun.rdmZhgl.core.service.YfjbEditApplyService;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.controller.MybatisListController;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

/**
 * 研发降本修改信息审批流程
 * 
 * @author zz
 */
@Controller
@RequestMapping("/rdmZhgl/core/yfjb/editApply/")
public class YfjbEditApplyController extends MybatisListController {
    @Resource
    private CommonBpmManager commonBpmManager;
    @Resource
    private YfjbEditApplyService yfjbEditApplyService;
    @Resource
    private YfjbEditApplyDao yfjbEditApplyDao;
    @Resource
    private YfjbBaseInfoDao yfjbBaseInfoDao;
    @Resource
    CommonInfoManager commonInfoManager;

    @SuppressWarnings("rawtypes")
    @Override
    public MybatisBaseManager getBaseManager() {
        return null;
    }

    /**
     * 审批列表页面
     */
    @RequestMapping("getListPage")
    public ModelAndView getListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/yfjbEditApplyList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        IUser currentUser = ContextUtil.getCurrentUser();
        mv.addObject("currentUser", currentUser);
        JSONObject resultJson = commonInfoManager.hasPermission("YFJB-GLY");
        mv.addObject("YFJBAdmin", resultJson.getBoolean("YFJB-GLY"));
        return mv;
    }

    /**
     * 获取审批页面
     */
    @RequestMapping("getEditPage")
    public ModelAndView getEditPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/yfjbEditApplyEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String taskId_ = RequestUtil.getString(request, "taskId_");
        String nodeId = RequestUtil.getString(request, "nodeId");
        String id = RequestUtil.getString(request, "id");
        String mainId = RequestUtil.getString(request, "mainId");
        mv.addObject("taskId_", taskId_);
        // 新增或编辑的时候没有nodeId
        String action = "";
        if (StringUtils.isBlank(nodeId) || nodeId.contains("PROCESS")) {
            action = "edit";
        } else {
            action = "task";
        }
        // 取出节点变量，返回到页面
        if (StringUtils.isNotBlank(nodeId)) {
            mv.addObject("nodeId", nodeId);
            List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId, "YFJB-XGXXSP", null);
            if (nodeVars != null && !nodeVars.isEmpty()) {
                mv.addObject("nodeVars", JSONObject.toJSONString(nodeVars));
            }
        }
        mv.addObject("action", action);
        Map<String, Object> planApplyObj = null;
        JSONObject applyObj = new JSONObject();
        if (StringUtils.isNotBlank(id)) {
            planApplyObj = yfjbEditApplyDao.getObjectById(id);
            applyObj = XcmgProjectUtil.convertMap2JsonObject(planApplyObj);
        } else if (StringUtils.isNotBlank(mainId)) {
            applyObj.put("mainId", mainId);
            JSONObject jsonObject = yfjbBaseInfoDao.getObjectById(mainId);
            applyObj.put("saleModel", jsonObject.getString("saleModel"));
            applyObj.put("designModel", jsonObject.getString("designModel"));
            applyObj.put("deptId", ContextUtil.getCurrentUser().getMainGroupId());
            applyObj.put("taskId_", taskId_);
        }
        mv.addObject("applyObj", applyObj);
        return mv;
    }

    /**
     * 获取list
     */
    @RequestMapping("queryList")
    @ResponseBody
    public JsonPageResult<?> queryList(HttpServletRequest request, HttpServletResponse response) {
        return yfjbEditApplyService.queryList(request, response);
    }

    /**
     * 项目作废表单和流程删除
     */
    @RequestMapping("delete")
    @ResponseBody
    public JsonResult delete(HttpServletRequest request, HttpServletResponse response) {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String instIdStr = RequestUtil.getString(request, "instIds");
            if (StringUtils.isNotEmpty(uIdStr) && StringUtils.isNotEmpty(instIdStr)) {
                String[] ids = uIdStr.split(",");
                String[] instIds = instIdStr.split(",");
                return yfjbEditApplyService.delete(ids, instIds);
            }
        } catch (Exception e) {
            return new JsonResult(false, e.getMessage());
        }
        return new JsonResult(true, "成功删除!");
    }

    /**
     * 明细：查看审批信息
     */
    @RequestMapping("edit")
    public ModelAndView edit(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = RequestUtil.getString(request, "id");
        String action = RequestUtil.getString(request, "action");
        String status = RequestUtil.getString(request, "status");
        String jspPath = "rdmZhgl/core/yfjbEditApplyEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("id", id).addObject("action", action).addObject("status", status);
        Map<String, Object> abolishApplyObj = null;
        JSONObject applyObj = new JSONObject();
        if (StringUtils.isNotBlank(id)) {
            abolishApplyObj = yfjbEditApplyDao.getObjectById(id);
            applyObj = XcmgProjectUtil.convertMap2JsonObject(abolishApplyObj);
        }
        mv.addObject("applyObj", applyObj);
        return mv;
    }

    @RequestMapping("exportBaseInfoExcel")
    public void exportBaseInfoExcel(HttpServletRequest request, HttpServletResponse response) {
        yfjbEditApplyService.exportBaseInfoExcel(request, response);
    }
}
