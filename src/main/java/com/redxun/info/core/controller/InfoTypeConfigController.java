package com.redxun.info.core.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.info.core.manager.InfoTypeConfigManager;
import com.redxun.info.core.manager.SpiderManager;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONArray;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;

/**
 * @author zz
 * 信息分类
 * */
@Controller
@RequestMapping("/info/type")
public class InfoTypeConfigController {
    @Resource
    InfoTypeConfigManager infoTypeConfigManager;
    @RequestMapping("infoTypeListPage")
    public ModelAndView getInfoTypeListPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "info/config/infoTypeConfigList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        return mv;
    }
    /**
     * 获取申请列表
     * */
    @RequestMapping("getDicInfoType")
    @ResponseBody
    public JSONArray getDicInfoType(HttpServletRequest request, HttpServletResponse response) {
        return infoTypeConfigManager.getDicInfoType();
    }
    @RequestMapping(value = "dealData", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult dealData(HttpServletRequest request, @RequestBody String changeGridDataStr,
                               HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "");
        if (StringUtils.isBlank(changeGridDataStr)) {
            result.setSuccess(false);
            result.setMessage("requestBody is blank");
            return result;
        }
        infoTypeConfigManager.saveOrUpdate(result, changeGridDataStr);
        return result;
    }
    @RequestMapping("infoTypeList")
    @ResponseBody
    public JsonPageResult<?> getInfoTypeList(HttpServletRequest request, HttpServletResponse response) {
        return infoTypeConfigManager.query(request);
    }
}
