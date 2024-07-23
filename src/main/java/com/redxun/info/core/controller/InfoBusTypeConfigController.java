package com.redxun.info.core.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.info.core.manager.InfoBusTypeConfigManager;
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
@RequestMapping("/info/busTypeConfig")
public class InfoBusTypeConfigController {
    @Resource
    InfoBusTypeConfigManager infoBusTypeConfigManager;

    @RequestMapping("infoBusTypeListPage")
    public ModelAndView getBusTypeInfoTypeListPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "info/config/infoBusTypeConfigList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        return mv;
    }
    /**
     * 获取申请列表
     * */
    @RequestMapping("getDicInfoType")
    @ResponseBody
    public JSONArray getDicInfoType(HttpServletRequest request, HttpServletResponse response) {
        return infoBusTypeConfigManager.getDicInfoType();
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
        infoBusTypeConfigManager.saveOrUpdate(result, changeGridDataStr);
        return result;
    }
    @RequestMapping("busInfoTypeList")
    @ResponseBody
    public JsonPageResult<?> getBusInfoTypeList(HttpServletRequest request, HttpServletResponse response) {
        return infoBusTypeConfigManager.query(request);
    }
}
