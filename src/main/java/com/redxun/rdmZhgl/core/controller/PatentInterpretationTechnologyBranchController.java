package com.redxun.rdmZhgl.core.controller;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmZhgl.core.service.PatentInterpretationTechnologyBranchService;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/zhgl/core/patentInterpretation/technologyBranch")
public class PatentInterpretationTechnologyBranchController {
    private static final Logger logger = LoggerFactory.getLogger(PatentInterpretationTechnologyBranchController.class);
    @Autowired
    private PatentInterpretationTechnologyBranchService patentInterpretationTechnologyBranchService;

    //..
    @RequestMapping("dataListPage")
    public ModelAndView dataListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/patentInterpretationTechnologyBranchList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        return mv;
    }
    @RequestMapping("jszhTree")
    public ModelAndView drbfmAllSingleTree(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "rdmZhgl/core/jszhTree.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        return mv;
    }
    //..
    @RequestMapping("dataListQuery")
    @ResponseBody
    public List<JSONObject> dataListQuery(HttpServletRequest request, HttpServletResponse response) {
        return patentInterpretationTechnologyBranchService.dataListQuery(request, response);
    }

    //..
    @RequestMapping("deleteData")
    @ResponseBody
    public JsonResult deleteData(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String id = RequestUtil.getString(request, "id");
            return patentInterpretationTechnologyBranchService.deleteData(id);
        } catch (Exception e) {
            logger.error("Exception in deleteData", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    //..
    @RequestMapping("editPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/patentInterpretationTechnologyBranchEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String parentId = RequestUtil.getString(request, "parentId");
        String businessId = RequestUtil.getString(request, "businessId");
        String action = RequestUtil.getString(request, "action");
        mv.addObject("businessId", businessId).addObject("action", action).addObject("parentId", parentId)
                .addObject("currentUserId", ContextUtil.getCurrentUserId())
                .addObject("currentUserName", ContextUtil.getCurrentUser().getFullname())
                .addObject("currentTime", DateUtil.formatDate(new Date(), DateUtil.DATE_FORMAT_FULL));
        return mv;
    }

    //..
    @RequestMapping(value = "saveData", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult saveData(HttpServletRequest request, @RequestBody String DataStr,
                               HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "保存成功");
        if (StringUtils.isBlank(DataStr)) {
            logger.warn("requestBody is blank");
            result.setSuccess(false);
            result.setMessage("表单为空，保存失败！");
            return result;
        }
        patentInterpretationTechnologyBranchService.saveData(result, DataStr);
        return result;
    }

    //..
    @RequestMapping("queryDataById")
    @ResponseBody
    public JSONObject queryDataById(HttpServletRequest request, HttpServletResponse response) {
        String businessId = RequestUtil.getString(request, "businessId");
        if (StringUtils.isBlank(businessId)) {
            logger.error("Id is blank");
            return null;
        }
        return patentInterpretationTechnologyBranchService.queryDataById(businessId);
    }
}
