package com.redxun.info.core.controller;

import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.info.core.manager.SpiderManager;
import com.redxun.saweb.util.RequestUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.util.DateUtil;
import com.redxun.info.core.manager.InfoStandardManager;

/**
 * @author zhangzhen
 */
@RestController
@RequestMapping("/info/spider/")
public class SpiderController {
    @Resource
    private SpiderManager spiderManager;

    @RequestMapping("doSpider")
    @ResponseBody
    public void doSpider(HttpServletRequest request, HttpServletResponse response) {
        String infoTypeId = RequestUtil.getString(request, "infoTypeId", "");
        String busTypeId = RequestUtil.getString(request, "busTypeId", "");
        spiderManager.doSpider(infoTypeId, busTypeId);
    }
}
