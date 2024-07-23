
package com.redxun.rdmZhgl.core.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.rdmCommon.core.manager.CommonFuns;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.org.api.model.IUser;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmZhgl.core.service.NdkfjhPlanDetailService;
import com.redxun.saweb.context.ContextUtil;

/**
 * 年度开发计划
 * @author zz
 */
@Controller
@RequestMapping("/rdmZhgl/core/ndkfjh/planDetail/")
public class NdkfjhPlanDetailController {
    @Resource
    NdkfjhPlanDetailService ndkfjhPlanDetailService;
    @Resource
    CommonInfoManager commonInfoManager;
    /**
     * 列表
     * */
    @RequestMapping("listPage")
    public ModelAndView getListPage(HttpServletRequest request,HttpServletResponse response) throws Exception{
        String jspPath = "rdmZhgl/core/ndkfjhPlanList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        IUser currentUser = ContextUtil.getCurrentUser();
        mv.addObject("currentUser", currentUser);
        JSONObject budgetAdmin = commonInfoManager.hasPermission("NDKFJH-JHGLY");
        mv.addObject("permission",budgetAdmin.getBoolean("NDKFJH-JHGLY")||"admin".equals(ContextUtil.getCurrentUser().getUserNo()));
        return mv;
    }

   /**
    * 获取编辑页面
    * */
    @RequestMapping("editPage")
    public ModelAndView getEditPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/ndkfjhPlanDetailEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String id = request.getParameter("id");
        String action = request.getParameter("action");
        String permission = request.getParameter("permission");
        JSONObject applyObj = new JSONObject();
        List<JSONObject> detailList = new ArrayList<>();
        if(!StringUtil.isEmpty(id)){
            applyObj = ndkfjhPlanDetailService.getObjectById(id);
            if (applyObj != null) {
                if (applyObj.get("startDate") != null) {
                    applyObj.put("startDate", DateUtil.formatDate((Date)applyObj.get("startDate"), "yyyy-MM-dd"));
                }
                if (applyObj.get("endDate") != null) {
                    applyObj.put("endDate", DateUtil.formatDate((Date)applyObj.get("endDate"), "yyyy-MM-dd"));
                }
                if (applyObj.get("stageFinishDate") != null) {
                    applyObj.put("stageFinishDate", DateUtil.formatDate((Date)applyObj.get("stageFinishDate"), "yyyy-MM-dd"));
                }
            }
        }
        String yearMonth = CommonFuns.genYearMonth("cur");
        if(applyObj.get("yearMonth")==null){
            applyObj.put("yearMonth",yearMonth);
        }
        applyObj.put("detailList",detailList);
        mv.addObject("action",action);
        mv.addObject("permission",permission);
        mv.addObject("applyObj", applyObj);
        return mv;
    }
    /**
     * 获取编辑页面
     * */
    @RequestMapping("planViewPage")
    public ModelAndView getPlanViewPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/ndkfjhPlanView.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String detailId = request.getParameter("detailId");
        mv.addObject("detailId", detailId);
        return mv;
    }
    @RequestMapping("remove")
    @ResponseBody
    public JSONObject remove(HttpServletRequest request, HttpServletResponse response) {
        JSONObject resultJSON = null;
        resultJSON = ndkfjhPlanDetailService.remove(request);
        return resultJSON;
    }
    @RequestMapping("save")
    @ResponseBody
    public JSONObject save(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");
        JSONObject resultJSON = null;
        if(StringUtil.isEmpty(id)){
//            resultJSON = ndkfjhPlanDetailService.add(request);
        }else{
            resultJSON = ndkfjhPlanDetailService.update(request);
        }
        return resultJSON;
    }
    @RequestMapping(value = "list")
    @ResponseBody
    public JsonPageResult<?> getDataList(HttpServletRequest request, HttpServletResponse response)  {
        return ndkfjhPlanDetailService.query(request);
    }
    @RequestMapping("asyncBudgetPlan")
    @ResponseBody
    public JSONObject asyncBudgetPlan(HttpServletRequest request, HttpServletResponse response) {
        return ndkfjhPlanDetailService.asyncBudgetPlan(request);
    }
    @RequestMapping(value = "listSpecialOrder")
    @ResponseBody
    public JsonPageResult<?> getSpecialOrderList(HttpServletRequest request, HttpServletResponse response)  {
        return ndkfjhPlanDetailService.getSpecialOrderList(request);
    }
    @RequestMapping(value = "listNewProduct")
    @ResponseBody
    public JsonPageResult<?> getNewProductList(HttpServletRequest request, HttpServletResponse response)  {
        return ndkfjhPlanDetailService.getNewProductList(request);
    }
    @RequestMapping(value = "listProject")
    @ResponseBody
    public JsonPageResult<?> getProjectList(HttpServletRequest request, HttpServletResponse response)  {
        return ndkfjhPlanDetailService.getProjectList(request);
    }
    @RequestMapping(value = "listProcess")
    @ResponseBody
    public List<JSONObject> getProcessList(HttpServletRequest request, HttpServletResponse response)  {
        return ndkfjhPlanDetailService.getProcessList(request);
    }
}
