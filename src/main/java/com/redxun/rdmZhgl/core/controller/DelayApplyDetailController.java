package com.redxun.rdmZhgl.core.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.rdmZhgl.core.service.DelayApplyDetailService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;

/**
 * @author zz

 * */
@Controller
@RequestMapping("/rdmZhgl/core/applyDetail/")
public class DelayApplyDetailController {
    @Resource
    DelayApplyDetailService delayApplyDetailService;

    @RequestMapping("itemList")
    @ResponseBody
    public JsonPageResult<?> getItemList(HttpServletRequest request, HttpServletResponse response) {
        return delayApplyDetailService.query(request,false);
    }

    @RequestMapping("exportExcel")
    public void exportExcel(HttpServletRequest request, HttpServletResponse response) {
        delayApplyDetailService.exportExcel(request, response);
    }

    @RequestMapping("exportDelayApply")
    public void exportDelayApply(HttpServletRequest request, HttpServletResponse response) {
        delayApplyDetailService.exportDelayApply(request, response);
    }
}
