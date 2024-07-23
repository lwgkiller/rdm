package com.redxun.researchTool.core.controller;


import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.researchTool.core.service.ResearchToolService;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.controller.GenericController;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/researchTool/train/")
public class TrainController extends GenericController {
    @Autowired
    private ResearchToolService researchToolService;
    @RequestMapping("TrainListPage")
    public ModelAndView trainList(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "researchTool/core/trainList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        return mv;
    }

    @RequestMapping("queryTrain")
    @ResponseBody
    public JsonPageResult<?> queryTrain(HttpServletRequest request, HttpServletResponse response) {
        return researchToolService.queryTrain(request, response, true);
    }

}

