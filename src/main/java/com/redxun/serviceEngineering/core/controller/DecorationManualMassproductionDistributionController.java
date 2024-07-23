package com.redxun.serviceEngineering.core.controller;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.serviceEngineering.core.service.DecorationManualDomesticoverseasProgressService;
import com.redxun.serviceEngineering.core.service.DecorationManualMassproductionDistributionService;
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
@RequestMapping("/serviceEngineering/core/decorationManual/massproductionDistribution")
public class DecorationManualMassproductionDistributionController {
    private static final Logger logger = LoggerFactory.getLogger(DecorationManualMassproductionDistributionController.class);
    @Autowired
    DecorationManualMassproductionDistributionService decorationManualMassproductionDistributionService;

    //..
    @RequestMapping("dataListPage")
    public ModelAndView dataListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/decorationmanualMassproductionDistributionList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        return mv;
    }

    //..
    @RequestMapping("kanbanPage")
    public ModelAndView kanbanPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/decorationmanualMassproductionDistributionKanban.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        return mv;
    }

    //..
    @RequestMapping("dataListQuery")
    @ResponseBody
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) {
        return decorationManualMassproductionDistributionService.dataListQuery(request, response);
    }

    //..
    @RequestMapping("deleteData")
    @ResponseBody
    public JsonResult deleteData(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return decorationManualMassproductionDistributionService.deleteData(ids);
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
        decorationManualMassproductionDistributionService.saveBusiness(result, businessDataStr);
        return result;
    }

    //..
    @RequestMapping(value = "calculateBusiness", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult calculateBusiness(HttpServletRequest request, @RequestBody String businessDataStr,
                                        HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "计算成功");
        decorationManualMassproductionDistributionService.calculateBusiness(result, businessDataStr);
        return result;
    }

    //..量产机型手册制作计划及进度柱状图专用
    @RequestMapping("kanbanData")
    @ResponseBody
    public List<JSONObject> kanbanData(HttpServletRequest request, HttpServletResponse response,
                                       @RequestBody String postDataStr) {
        return decorationManualMassproductionDistributionService.kanbanData(request, response, postDataStr);
    }

    //..量产机型数量饼状图专用
    @RequestMapping("kanbanData2")
    @ResponseBody
    public List<JSONObject> kanbanData2(HttpServletRequest request, HttpServletResponse response,
                                       @RequestBody String postDataStr) {
        return decorationManualMassproductionDistributionService.kanbanData2(request, response, postDataStr);
    }
}
