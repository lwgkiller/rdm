package com.redxun.rdmZhgl.core.controller;

import com.alibaba.fastjson.JSONObject;
import com.redxun.rdmZhgl.core.service.HyglGeneralKanBanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/zhgl/core/hyglGeneralKanban")
public class HyglGeneralKanBanController {
    private static final Logger logger = LoggerFactory.getLogger(HyglGeneralKanBanController.class);
    @Autowired
    private HyglGeneralKanBanService hyglGeneralKanBanService;

    //..
    @RequestMapping("hyglGeneralKanbanPage")
    public ModelAndView hyglGeneralKanbanPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/report/hyglGeneralKanban.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        return mv;
    }

    //..获取零件图册看板数据
    @RequestMapping("getKanbanData")
    @ResponseBody
    public List<JSONObject> getKanbanData(HttpServletRequest request, HttpServletResponse response,
                                          @RequestBody String postDataStr) {
        return hyglGeneralKanBanService.getKanbanData(request, response, postDataStr);
    }

    //..111
    //..获取内部会议总数
    @RequestMapping("getInternalTotal")
    @ResponseBody
    public Integer getInternalTotal(HttpServletRequest request, HttpServletResponse response,
                                    @RequestBody String postDataStr) {
        return hyglGeneralKanBanService.getInternalTotal(request, response, postDataStr);
    }

    //..获取外部会议总数
    @RequestMapping("getExternalTotal")
    @ResponseBody
    public Integer getExternalTotal(HttpServletRequest request, HttpServletResponse response,
                                    @RequestBody String postDataStr) {
        return hyglGeneralKanBanService.getExternalTotal(request, response, postDataStr);
    }
}
