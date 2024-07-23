package com.redxun.serviceEngineering.core.controller;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.serviceEngineering.core.service.MaintenanceManualService;
import com.redxun.serviceEngineering.core.service.SeGeneralKanBanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/serviceEngineering/core/seGeneralKanban")
public class SeGeneralKanBanController {
    private static final Logger logger = LoggerFactory.getLogger(SeGeneralKanBanController.class);
    @Autowired
    private SeGeneralKanBanService seGeneralKanBanService;

    //..
    @RequestMapping("seGeneralKanbanPage")
    public ModelAndView seGeneralKanbanPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/seGeneralKanban.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        return mv;
    }

    //..111
    //..获取零件图册看板数据
    @RequestMapping("getPartsAtlasKanbanData")
    @ResponseBody
    public List<JSONObject> getPartsAtlasKanbanData(HttpServletRequest request, HttpServletResponse response,
                                                    @RequestBody String postDataStr) {
        return seGeneralKanBanService.getPartsAtlasKanbanData(request, response, postDataStr);
    }

    //..获取机型零件图册制作总数
    @RequestMapping("getPartsAtlasModelTotalData")
    @ResponseBody
    public Integer getPartsAtlasModelTotalData(HttpServletRequest request, HttpServletResponse response) {
        return seGeneralKanBanService.getPartsAtlasModelTotal();
    }

    //..实例零件图册制作总数
    @RequestMapping("getPartsAtlasInstanceTotalData")
    @ResponseBody
    public Integer getPartsAtlasInstanceTotalData(HttpServletRequest request, HttpServletResponse response) {
        return seGeneralKanBanService.getPartsAtlasInstanceTotal();
    }

    //..333
    //..操保手册看板数据
    @RequestMapping("getMaintenanceManualKanbanData")
    @ResponseBody
    public List<JSONObject> getMaintenanceManualKanbanData(HttpServletRequest request, HttpServletResponse response,
                                                           @RequestBody String postDataStr) {
        return seGeneralKanBanService.getMaintenanceManualKanbanData(request, response, postDataStr);
    }

    //..操保手册制作总数
    @RequestMapping("getMaintenanceManualTotalData")
    @ResponseBody
    public Integer getMaintenanceManualTotalData(HttpServletRequest request, HttpServletResponse response) {
        return seGeneralKanBanService.getMaintenanceManualTotal();
    }

    //..666
    //..测试版制作总数
    @RequestMapping("getStandardvalueBetaTotalData")
    @ResponseBody
    public Integer getStandardvalueBetaTotalData(HttpServletRequest request, HttpServletResponse response,
                                                 @RequestBody String postDataStr) {
        return seGeneralKanBanService.getStandardvalueBetaTotal(postDataStr);
    }

    //..常规版制作总数
    @RequestMapping("getStandardvalueRoutineTotalData")
    @ResponseBody
    public Integer getStandardvalueRoutineTotalData(HttpServletRequest request, HttpServletResponse response,
                                                    @RequestBody String postDataStr) {
        return seGeneralKanBanService.getStandardvalueRoutineTotal(postDataStr);
    }

    //..完整版制作总数
    @RequestMapping("getStandardvalueCompleteTotalData")
    @ResponseBody
    public Integer getStandardvalueCompleteTotalData(HttpServletRequest request, HttpServletResponse response,
                                                     @RequestBody String postDataStr) {
        return seGeneralKanBanService.getStandardvalueCompleteTotal(postDataStr);
    }

    //..检修标准值看板数据
    @RequestMapping("getStandardvalueKanbanData")
    @ResponseBody
    public JSONObject getStandardvalueKanbanData(HttpServletRequest request, HttpServletResponse response,
                                                 @RequestBody String postDataStr) {
        return seGeneralKanBanService.getStandardvalueKanbanData(request, response, postDataStr);
    }

    //..555
    //..某年备件核查总数
    @RequestMapping("getSparepartsVerificationTotalData")
    @ResponseBody
    public Integer getSparepartsVerificationTotalData(HttpServletRequest request, HttpServletResponse response,
                                                      @RequestBody String postDataStr) {
        return seGeneralKanBanService.getSparepartsVerificationTotal(postDataStr);
    }

    //..备件核查看板数据
    @RequestMapping("getSparepartsVerificationKanbanData")
    @ResponseBody
    public JSONObject getSparepartsVerificationKanbanData(HttpServletRequest request, HttpServletResponse response,
                                                          @RequestBody String postDataStr) {
        return seGeneralKanBanService.getSparepartsVerificationKanbanData(request, response, postDataStr);
    }

}
