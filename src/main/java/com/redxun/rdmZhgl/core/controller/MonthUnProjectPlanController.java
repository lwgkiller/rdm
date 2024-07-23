
package com.redxun.rdmZhgl.core.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.core.util.DateUtil;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmZhgl.core.service.MonthUnProjectPlanService;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.StringUtil;
import com.redxun.org.api.model.IUser;
import com.redxun.rdmZhgl.core.service.MonthWorkService;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectChangeDao;

/**
 * 月度重点工作
 *
 * @author zz
 */
@Controller
@RequestMapping("/rdmZhgl/core/monthUnProjectPlan/")
public class MonthUnProjectPlanController {
    @Resource
    MonthUnProjectPlanService monthUnProjectPlanService;
    @Resource
    CommonInfoManager commonInfoManager;

    /**
     * 项目列表
     * */
    @RequestMapping("getListPage")
    public ModelAndView getListPage(HttpServletRequest request,HttpServletResponse response) throws Exception{
        String jspPath = "rdmZhgl/core/monthUnWorkList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        IUser currentUser = ContextUtil.getCurrentUser();
        mv.addObject("currentUser", currentUser);
        JSONObject resultJson = commonInfoManager.hasPermission("YDGZ-JHDDY");
        JSONObject adminJson = commonInfoManager.hasPermission("YDGZ-GLY");
        mv.addObject("permission",resultJson.getBoolean("YDGZ-JHDDY"));
        mv.addObject("isAdmin",adminJson.getBoolean("YDGZ-GLY"));
        return mv;
    }
    /**
     * 获取编辑页面
     */
    @RequestMapping("getEditPage")
    public ModelAndView getEditPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/monthUnProjectPlanEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String mainId = request.getParameter("mainId");
        String action = request.getParameter("action");
        JSONObject applyObj = new JSONObject();
        if (!StringUtil.isEmpty(mainId)) {
            applyObj = monthUnProjectPlanService.getObjectById(mainId);
            if (applyObj != null) {
                if (applyObj.get("startDate") != null) {
                    applyObj.put("startDate", DateUtil.formatDate((Date)applyObj.get("startDate"), "yyyy-MM-dd"));
                }
                if (applyObj.get("endDate") != null) {
                    applyObj.put("endDate", DateUtil.formatDate((Date)applyObj.get("endDate"), "yyyy-MM-dd"));
                }
            }
        } else {
            applyObj.put("responseMan", ContextUtil.getCurrentUserId());
            applyObj.put("responseManText", ContextUtil.getCurrentUser().getFullname());
            applyObj.put("deptId", ContextUtil.getCurrentUser().getMainGroupId());
            applyObj.put("deptName",ContextUtil.getCurrentUser().getMainGroupName());
        }
        JSONObject resultJson = commonInfoManager.hasPermission("YDGZ-JHDDY");
        mv.addObject("permission", resultJson.getBoolean("YDGZ-JHDDY"));
        JSONObject adminJson = commonInfoManager.hasPermission("YDGZ-GLY");
        mv.addObject("isAdmin",adminJson.getBoolean("YDGZ-GLY"));
        mv.addObject("action", action);
        mv.addObject("applyObj", applyObj);
        IUser currentUser = ContextUtil.getCurrentUser();
        mv.addObject("currentUser", currentUser);
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        return mv;
    }

    @RequestMapping("remove")
    @ResponseBody
    public JSONObject remove(HttpServletRequest request, HttpServletResponse response) {
        JSONObject resultJSON = null;
        resultJSON = monthUnProjectPlanService.remove(request);
        return resultJSON;
    }

    @RequestMapping("removeItem")
    @ResponseBody
    public JSONObject removeItem(HttpServletRequest request, HttpServletResponse response) {
        JSONObject resultJSON = null;
        resultJSON = monthUnProjectPlanService.removeItem(request);
        return resultJSON;
    }

    @RequestMapping("save")
    @ResponseBody
    public JSONObject save(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");
        JSONObject resultJSON = null;
        if (StringUtil.isEmpty(id)) {
            resultJSON = monthUnProjectPlanService.add(request);
        } else {
            resultJSON = monthUnProjectPlanService.update(request);
        }
        return resultJSON;
    }

    @RequestMapping(value = "dealData", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult dealData(HttpServletRequest request, @RequestBody String changeGridDataStr,
        HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "");
        if (StringUtils.isBlank(changeGridDataStr)) {
            result.setSuccess(false);
            result.setMessage("requestBody is blank");
            return result;
        }
        monthUnProjectPlanService.saveOrUpdateItem(changeGridDataStr);
        return result;
    }

    @RequestMapping(value = "items")
    @ResponseBody
    public List<JSONObject> getItemList(HttpServletRequest request, HttpServletResponse response) {
        return monthUnProjectPlanService.getItemList(request);
    }

    @RequestMapping(value = "plans")
    @ResponseBody
    public List<Map<String, Object>> getPlanList(HttpServletRequest request, HttpServletResponse response) {
        return monthUnProjectPlanService.getPlanList(request);
    }

    @RequestMapping("exportProjectPlanExcel")
    public void exportExcel(HttpServletRequest request, HttpServletResponse response) {
        monthUnProjectPlanService.exportUnProjectPlanExcel(request, response);
    }

    @RequestMapping("copy")
    @ResponseBody
    public JSONObject copyPlan(HttpServletRequest request, HttpServletResponse response) {
        JSONObject resultJSON = null;
        resultJSON = monthUnProjectPlanService.copyPlan(request);
        return resultJSON;
    }

    /**
     * 预算类模板下载
     * */
    @RequestMapping("/budgetTemplateDownload")
    public ResponseEntity<byte[]> budgetTemplateDownload(HttpServletRequest request, HttpServletResponse response) {
        return monthUnProjectPlanService.budgetTemplateDownload();
    }

    /**
     * 批量导入
     * */
    @RequestMapping("importBudgetExcel")
    @ResponseBody
    public JSONObject importBudgetExcel(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        monthUnProjectPlanService.importBudgetExcel(result, request);
        return result;
    }

    /**
     * 预算类模板下载
     * */
    @RequestMapping("/strategyTemplateDownload")
    public ResponseEntity<byte[]> strategyTemplateDownload(HttpServletRequest request, HttpServletResponse response) {
        return monthUnProjectPlanService.strategyTemplateDownload();
    }

    /**
     * 批量导入
     * */
    @RequestMapping("importStrategyExcel")
    @ResponseBody
    public JSONObject importStrategyExcel(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        monthUnProjectPlanService.importStrategyExcel(result, request);
        return result;
    }
    /**
     * 个人非项目类
     * */
    @RequestMapping(value = "personUnProject")
    @ResponseBody
    public List<Map<String, Object>> getPersonUnProjectPlan(HttpServletRequest request, HttpServletResponse response)  {
        return monthUnProjectPlanService.getPersonUnProjectPlan(request);
    }
    /**
     * 未完成
     * */
    @RequestMapping(value = "unFinishPlan")
    @ResponseBody
    public List<Map<String, Object>> getUnFinishPlan(HttpServletRequest request, HttpServletResponse response)  {
        return monthUnProjectPlanService.getDeptUnFinishList(request);
    }
}
