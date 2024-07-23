package com.redxun.rdmZhgl.core.controller;

import com.alibaba.fastjson.JSONObject;
import com.redxun.rdmZhgl.core.service.CxyProjectGeneralKanBanService;
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
@RequestMapping("/zhgl/core/cxyGeneralKanban")
public class CxyProjectGeneralKanBanController {
    private static final Logger logger = LoggerFactory.getLogger(CxyProjectGeneralKanBanController.class);
    @Autowired
    private CxyProjectGeneralKanBanService cxyProjectGeneralKanBanService;

    //..
    @RequestMapping("cxyGeneralKanbanPage")
    public ModelAndView hyglGeneralKanbanPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/report/cxyProjectGeneralKanban.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        return mv;
    }

    //..获取零件图册看板数据
    @RequestMapping("getKanbanData")
    @ResponseBody
    public List<JSONObject> getKanbanData(HttpServletRequest request, HttpServletResponse response,
                                          @RequestBody String postDataStr) {
        return cxyProjectGeneralKanBanService.getKanbanData(request, response, postDataStr);
    }

    //..获取总数
    @RequestMapping("getTotal")
    @ResponseBody
    public Integer getTotal(HttpServletRequest request, HttpServletResponse response,
                                    @RequestBody String postDataStr) {
        return cxyProjectGeneralKanBanService.getTotal(request, response, postDataStr);
    }

}
