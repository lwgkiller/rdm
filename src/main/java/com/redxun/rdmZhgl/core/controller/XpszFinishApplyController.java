
package com.redxun.rdmZhgl.core.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.rdmZhgl.core.dao.ProductConfigDao;
import com.redxun.rdmZhgl.core.dao.XpszFinishApplyDao;
import com.redxun.rdmZhgl.core.service.XpszFinishApplyService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.manager.MybatisBaseManager;
import com.redxun.core.util.DateUtil;
import com.redxun.org.api.model.IUser;
import com.redxun.rdmZhgl.core.service.ProductService;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.controller.MybatisListController;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.standardManager.core.util.ResultUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

/**
 *新品试制填写完成日期审批流程
 * @author zz
 */
@Controller
@RequestMapping("/rdmZhgl/core/xpszFinish/")
public class XpszFinishApplyController extends MybatisListController {
    @Resource
    private XpszFinishApplyDao xpszFinishApplyDao;
    @Resource
    private CommonBpmManager commonBpmManager;
    @Resource
    XpszFinishApplyService xpszFinishApplyService;
    @Resource
    ProductService productService;
    @Resource
    ProductConfigDao productConfigDao;

    @SuppressWarnings("rawtypes")
    @Override
    public MybatisBaseManager getBaseManager() {
        return null;
    }
    /**
     * 审批列表页面
     * */
    @RequestMapping("getListPage")
    public ModelAndView getListPage(HttpServletRequest request,HttpServletResponse response) throws Exception{
        String jspPath = "rdmZhgl/core/xpszFinishList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        IUser currentUser = ContextUtil.getCurrentUser();
        mv.addObject("currentUser", currentUser);
        return mv;
    }

   /**
    * 获取审批页面
    * */
    @RequestMapping("getEditPage")
    public ModelAndView getEditPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/xpszFinishEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String taskId_ = RequestUtil.getString(request, "taskId_");
        String nodeId = RequestUtil.getString(request, "nodeId");
        String id = RequestUtil.getString(request, "id");
        String mainId = RequestUtil.getString(request, "mainId");
        String item = RequestUtil.getString(request, "item");
        mv.addObject("taskId_", taskId_);
        // 新增或编辑的时候没有nodeId
        String action = "";
        if (StringUtils.isBlank(nodeId) || nodeId.contains("PROCESS")) {
            action = "edit";
        } else {
            action = "task";
        }
        //取出节点变量，返回到页面
        if (StringUtils.isNotBlank(nodeId)) {
            mv.addObject("nodeId", nodeId);
            List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId,"XPSZ-WCRQSP",null);
            if (nodeVars != null && !nodeVars.isEmpty()) {
                mv.addObject("nodeVars", JSONObject.toJSONString(nodeVars));
            }
        }
        mv.addObject("action", action);
        Map<String, Object> planApplyObj = null;
        JSONObject applyObj = new JSONObject();
        if (StringUtils.isNotBlank(id)) {
            planApplyObj = xpszFinishApplyDao.getObjectById(id);
            applyObj = XcmgProjectUtil.convertMap2JsonObject(planApplyObj);
            if (applyObj.get("finishDate") != null) {
                applyObj.put("finishDate",
                        DateUtil.formatDate((Date)applyObj.get("finishDate"), "yyyy-MM-dd"));
            }
        }else{
            applyObj.put("mainId", mainId);
            applyObj.put("item", item);
            JSONObject jsonObject = productConfigDao.getItemObjByCode(item);
            if(jsonObject!=null){
                applyObj.put("itemName", jsonObject.getString("itemName"));
            }
            applyObj.put("deptId", ContextUtil.getCurrentUser().getMainGroupId());
            applyObj.put("taskId_", taskId_);
        }
        mv.addObject("applyObj", applyObj);
        return mv;
    }
    /**
     * 获取list
     * */
    @RequestMapping("queryList")
    @ResponseBody
    public JsonPageResult<?> queryList(HttpServletRequest request, HttpServletResponse response) {
        return xpszFinishApplyService.queryList(request, response);
    }
    /**
     * 项目作废表单和流程删除
     * */
    @RequestMapping("delete")
    @ResponseBody
    public JsonResult delete(HttpServletRequest request, HttpServletResponse response) {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String instIdStr = RequestUtil.getString(request, "instIds");
            if (StringUtils.isNotEmpty(uIdStr) && StringUtils.isNotEmpty(instIdStr)) {
                String[] ids = uIdStr.split(",");
                String[] instIds = instIdStr.split(",");
                return xpszFinishApplyService.delete(ids, instIds);
            }
        } catch (Exception e) {
            return new JsonResult(false, e.getMessage());
        }
        return new JsonResult(true, "成功删除!");
    }
    /**
     * 明细：查看审批信息
     * */
    @RequestMapping("edit")
    public ModelAndView edit(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = RequestUtil.getString(request, "id");
        String action = RequestUtil.getString(request, "action");
        String status = RequestUtil.getString(request, "status");
        String jspPath = "rdmZhgl/core/xpszFinishEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("id", id).addObject("action", action).addObject("status", status);
        Map<String, Object> abolishApplyObj = null;
        JSONObject applyObj = new JSONObject();
        if (StringUtils.isNotBlank(id)) {
            abolishApplyObj = xpszFinishApplyDao.getObjectById(id);
            applyObj = XcmgProjectUtil.convertMap2JsonObject(abolishApplyObj);
            if (applyObj.get("finishDate") != null) {
                applyObj.put("finishDate",
                        DateUtil.formatDate((Date)applyObj.get("finishDate"), "yyyy-MM-dd"));
            }
        }
        JSONArray list = productService.getJsonArray(request);
        mv.addObject("listData",list.toJSONString());
        mv.addObject("applyObj", applyObj);
        return mv;
    }
    @RequestMapping(value = "changePlan", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject changePlan(HttpServletRequest request, @RequestBody String postData,
                                 HttpServletResponse response) throws Exception {
        JSONObject postDataJson = new JSONObject();
        JSONObject resultJson = new JSONObject();
        if (StringUtils.isNotBlank(postData)) {
            postDataJson = JSONObject.parseObject(postData);
        }else{
            resultJson = ResultUtil.result(false,"请求参数不能为空",null);
        }
        resultJson = xpszFinishApplyService.changePlan(postDataJson);
        return resultJson;
    }
    @RequestMapping(value = "verify", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject verify(HttpServletRequest request, @RequestBody String postData,
                                 HttpServletResponse response) throws Exception {
        JSONObject postDataJson = new JSONObject();
        JSONObject resultJson = new JSONObject();
        if (StringUtils.isNotBlank(postData)) {
            postDataJson = JSONObject.parseObject(postData);
        }else{
            resultJson = ResultUtil.result(false,"请求参数不能为空",null);
        }
        resultJson = xpszFinishApplyService.verify(postDataJson);
        return resultJson;
    }
}
