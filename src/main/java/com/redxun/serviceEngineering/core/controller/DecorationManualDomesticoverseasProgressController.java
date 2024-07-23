package com.redxun.serviceEngineering.core.controller;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.serviceEngineering.core.service.DecorationManualDomesticoverseasProgressService;
import com.redxun.serviceEngineering.core.service.MaintainabilityDisassemblyPlanService;
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
import java.util.List;

@Controller
@RequestMapping("/serviceEngineering/core/decorationManual/domesticOverseasProgress")
public class DecorationManualDomesticoverseasProgressController {
    private static final Logger logger = LoggerFactory.getLogger(DecorationManualDomesticoverseasProgressController.class);
    @Autowired
    DecorationManualDomesticoverseasProgressService decorationManualDomesticoverseasProgressService;

    //..
    @RequestMapping("dataListPage")
    public ModelAndView dataListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/decorationManualDomesticoverseasProgressList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        return mv;
    }

    //..
    @RequestMapping("kanbanPage")
    public ModelAndView kanbanPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/decorationManualDomesticoverseasProgressKanban.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        return mv;
    }

    //..
    @RequestMapping("dataListQuery")
    @ResponseBody
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) {
        return decorationManualDomesticoverseasProgressService.dataListQuery(request, response);
    }

    //..
    @RequestMapping("deleteData")
    @ResponseBody
    public JsonResult deleteData(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return decorationManualDomesticoverseasProgressService.deleteData(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteData", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    //..
    @RequestMapping(value = "saveBusiness", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult saveBusiness(HttpServletRequest request, @RequestBody String businessDataStr,
                                   HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "保存成功");
        decorationManualDomesticoverseasProgressService.saveBusiness(result, businessDataStr);
        return result;
    }

    //..计划进度柱状图专用
    @RequestMapping("kanbanData")
    @ResponseBody
    public List<JSONObject> kanbanData(HttpServletRequest request, HttpServletResponse response,
                                               @RequestBody String postDataStr) {
        return decorationManualDomesticoverseasProgressService.kanbanData(request, response, postDataStr);
    }

    //..机型表专用
    @RequestMapping("modelListQuery")
    @ResponseBody
    public JsonPageResult<?> modelListQuery(HttpServletRequest request, HttpServletResponse response) {
        return decorationManualDomesticoverseasProgressService.modelListQuery(request, response);
    }

    //..机型进度柱状图专用
    @RequestMapping("kanbanData2")
    @ResponseBody
    public List<JSONObject> kanbanData2(HttpServletRequest request, HttpServletResponse response,
                                       @RequestBody String postDataStr) {
        return decorationManualDomesticoverseasProgressService.kanbanData2(request, response, postDataStr);
    }
}
