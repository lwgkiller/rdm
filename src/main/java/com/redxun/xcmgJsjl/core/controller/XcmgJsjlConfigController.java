package com.redxun.xcmgJsjl.core.controller;

import com.alibaba.fastjson.JSONObject;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.controller.GenericController;
import com.redxun.xcmgJsjl.core.manager.XcmgJsjlConfigManager;
import org.apache.commons.lang.StringUtils;
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
@RequestMapping("/jsjl/core/config/")
public class XcmgJsjlConfigController extends GenericController {
    @Autowired
    private XcmgJsjlConfigManager xcmgJsjlConfigManager;

    @RequestMapping("dimensionConfigPage")
    public ModelAndView dimensionConfigPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "xcmgJsjl/core/xcmgJsjlDimensionConfig.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        return mv;
    }

    @RequestMapping("dimensionConfigSave")
    @ResponseBody
    public JSONObject dimensionConfigSave(HttpServletRequest request, @RequestBody String changeGridDataStr,
        HttpServletResponse response) {
        JSONObject result = new JSONObject();
        if (StringUtils.isBlank(changeGridDataStr)) {
            logger.warn("requestBody is blank");
            result.put("message", "保存失败，数据为空！");
            return result;
        }
        xcmgJsjlConfigManager.saveDimension(result, changeGridDataStr);
        if (result.get("message") == null) {
            result.put("message", "保存成功！");
        }
        return result;
    }

    @RequestMapping("dimensionListQuery")
    @ResponseBody
    public List<JSONObject> dimensionListQuery(HttpServletRequest request, HttpServletResponse response) {
        return xcmgJsjlConfigManager.dimensionList(request);
    }
}
