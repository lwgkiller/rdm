package com.redxun.zlgjNPI.core.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.StringUtil;
import com.redxun.org.api.model.IUser;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.standardManager.core.util.ResultUtil;
import com.redxun.zlgjNPI.core.dao.ProductManageDao;
import com.redxun.zlgjNPI.core.manager.ProductManageManager;

@Controller
@RequestMapping("/xcpdr/core/cpkfgk/")
public class ProductManageController {
    private Logger logger = LoggerFactory.getLogger(ProductManageController.class);
    @Autowired
    private ProductManageManager productManageManager;
    @Autowired
    private CommonBpmManager commonBpmManager;

    @Resource
    private BpmInstManager bpmInstManager;

    @Resource
    private ProductManageDao productManageDao;

    @RequestMapping("productManageListPage")
    public ModelAndView listPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "zlgjNPI/productManageList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        IUser currentUser = ContextUtil.getCurrentUser();
        mv.addObject("currentUser", currentUser);
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        return mv;
    }

    @RequestMapping("getProductManageList")
    @ResponseBody
    public JsonPageResult<?> getProductManageList(HttpServletRequest request, HttpServletResponse response) {
        return productManageManager.getProductManageList(request, response, true);
    }

    @RequestMapping("deleteProductManage")
    @ResponseBody
    public JsonResult deleteProductManage(HttpServletRequest request, HttpServletResponse response) {
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
            return productManageManager.deleteProductManage(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteJsmm", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("editPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) {

        String jspPath = "zlgjNPI/productManageEdit.jsp";

        ModelAndView mv = new ModelAndView(jspPath);
        // 新增或者编辑页面由流程引擎跳转并替换url中的pk和nodeId_
        String formId = RequestUtil.getString(request, "manageId");
        String nodeId = RequestUtil.getString(request, "nodeId");
        String action = RequestUtil.getString(request, "action");
        String status = RequestUtil.getString(request, "status");
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
            mv.addObject("nodeId", nodeId);
            List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId, "CPKFGK", null);
            if (nodeVars != null && !nodeVars.isEmpty()) {
                mv.addObject("nodeVars", JSONObject.toJSONString(nodeVars));
                mv.addObject("nodeName", nodeVars.get(0).get("KEY_"));
            }

        }

        mv.addObject("action", action);

        JSONObject formObj = new JSONObject();
        if (StringUtils.isNotBlank(formId)) {
            formObj = productManageManager.queryProductManage(formId);

        } else {
            IUser user = ContextUtil.getCurrentUser();
            formObj.put("applyUserId", user.getUserId());
            formObj.put("applyUserName", user.getFullname());
            formObj.put("applyUserPhone", user.getMobile());
            formObj.put("applyUserDeptName", user.getMainGroupName());
            formObj.put("feedbackType", "need");
        }
        mv.addObject("productManageObj", formObj);
        mv.addObject("manageId", formId);
        mv.addObject("status", status);

        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());

        return mv;
    }

    @GetMapping("getMessage")
    public JSONObject getDicItem(@RequestParam(name = "dicType") String dicType) {
        String msg = "调用成功";
        if (StringUtil.isEmpty(dicType)) {
            msg = "字典类型不能为空！";
            return ResultUtil.result(org.apache.http.HttpStatus.SC_BAD_REQUEST, msg, "list");
        }

        List list = productManageManager.getDicValues(dicType);
        JSONObject result = ResultUtil.result(HttpStatus.SC_OK, msg, list);
        return result;
    }

    @RequestMapping("items")
    @ResponseBody
    public List<JSONObject> getItemList(HttpServletRequest request, HttpServletResponse response) {
        return productManageManager.getItemList(request);
    }

    /**
     * 查询该型号是否存在
     *
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping("selectproductTypeNum")
    @ResponseBody
    public JSONObject selectproductTypeNum(HttpServletRequest request, HttpServletResponse responses,
        @RequestBody String postData) throws Exception {
        JSONObject postDataObj = JSONObject.parseObject(postData);
        String designType = postDataObj.getString("designType");
        String manageId = postDataObj.getString("manageId");

        String num = productManageDao.selectproductTypeNum(designType, manageId);

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("num", num);
        return jsonObject;
    }
}
