package com.redxun.rdmZhgl.core.controller;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpmclient.util.HttpUtil;
import com.redxun.core.json.JsonResult;
import com.redxun.rdmZhgl.core.service.SgykService;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.controller.GenericController;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.sys.core.entity.SysDic;
import com.redxun.sys.core.manager.SysDicManager;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;
import java.util.List;

@Controller
@RequestMapping("/zhgl/core/sgyk/")
public class SgykController extends GenericController {
    @Autowired
    private SgykService sgykService;

    //..
    @RequestMapping("templatePage")
    public ModelAndView templatePage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "rdmZhgl/core/sgykTemplate.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        return mv;
    }

    //..
    @RequestMapping("sgyklistPage")
    public ModelAndView sgyklistPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "rdmZhgl/core/sgykList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        return mv;
    }

    //..
    @RequestMapping("sgykOneYearListPage")
    public ModelAndView sgykOneYearListPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "rdmZhgl/core/sgykOneYearList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        return mv;
    }

    //..
    @RequestMapping("sgykCycleReportPage")
    public ModelAndView sgykCycleReportPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/sgykCycleReport.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        return mv;
    }

    //..
    @RequestMapping("editPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "rdmZhgl/core/sgykEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String sgykId = RequestUtil.getString(request, "sgykId");
        String signYear = RequestUtil.getString(request, "signYear");
        String signMonth = RequestUtil.getString(request, "signMonth");
        mv.addObject("sgykId", sgykId).addObject("signYear", signYear).addObject("signMonth", signMonth);
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        return mv;
    }

    //..
    @RequestMapping("templateListQuery")
    @ResponseBody
    public List<JSONObject> templateListQuery(HttpServletRequest request, HttpServletResponse response) {
        return sgykService.templateList();
    }

    //..
    @RequestMapping("saveTemplate")
    @ResponseBody
    public JSONObject saveTemplate(HttpServletRequest request, @RequestBody String changeGridDataStr,
                                   HttpServletResponse response) {
        JSONObject result = new JSONObject();
        if (StringUtils.isBlank(changeGridDataStr)) {
            logger.warn("requestBody is blank");
            result.put("message", "保存失败，数据为空！");
            return result;
        }
        sgykService.saveTemplate(result, changeGridDataStr);
        if (result.get("message") == null) {
            result.put("message", "保存成功！");
        }
        return result;
    }

    //..
    @RequestMapping(value = "saveSgykDetail", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult saveSgykDetail(HttpServletRequest request, @RequestBody String DataStr,
                                     HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "保存成功");
        sgykService.saveSgykDetail(result, DataStr);
        return result;
    }

    //..
    @RequestMapping("sgykListQuery")
    @ResponseBody
    public List<JSONObject> sgykListQuery(HttpServletRequest request, HttpServletResponse response) {
        return sgykService.sgykList(request, response);
    }

    //..
    @RequestMapping("sgykOneYearListQuery")
    @ResponseBody
    public List<JSONObject> sgykOneYearListQuery(HttpServletRequest request, HttpServletResponse response) {
        return sgykService.sgykOneYearList(request, response);
    }

    //..
    @RequestMapping("sgykCycleReportQuery")
    @ResponseBody
    public LinkedHashMap<String, LinkedHashMap> sgykCycleReportQuery(HttpServletRequest request, HttpServletResponse response,
                                                                     @RequestBody String postDataStr) {
        return sgykService.sgykCycleReportList(request, response, postDataStr);
    }

    //..
    @RequestMapping("sgykDetailListQuery")
    @ResponseBody
    public List<JSONObject> sgykDetailListQuery(HttpServletRequest request, HttpServletResponse response) {
        String sgykId = RequestUtil.getString(request, "sgykId");
        return sgykService.sgykDetailListByMainId(sgykId);
    }

    //..
    @RequestMapping("createSgyk")
    @ResponseBody
    public JsonResult createSgyk(HttpServletRequest request, HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "生成成功");
        String signYear = RequestUtil.getString(request, "signYear");
        String signMonth = RequestUtil.getString(request, "signMonth");
        sgykService.createSgyk(result, signYear, signMonth);
        return result;
    }

    //..
    @RequestMapping("calculateSgyk")
    @ResponseBody
    public JsonResult calculateSgyk(HttpServletRequest request, HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "计算成功");
        String signYear = RequestUtil.getString(request, "signYear");
        String signMonth = RequestUtil.getString(request, "signMonth");
        if (signYear.equalsIgnoreCase("") || signMonth.equalsIgnoreCase("")) {
            result.setSuccess(false);
            result.setMessage("没有传入正确的年月，计算失败！");
            return result;
        }
        sgykService.calculateSgyk(result, signYear, signMonth);
        return result;
    }

    //..
    @RequestMapping("exportOneYearList")
    public void exportOneYearList(HttpServletRequest request, HttpServletResponse response) {
        sgykService.exportOneYearList(request, response);
    }
}
