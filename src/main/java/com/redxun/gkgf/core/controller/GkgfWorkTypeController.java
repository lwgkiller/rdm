package com.redxun.gkgf.core.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.redxun.gkgf.core.manager.GkgfWorkTypeManager;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.saweb.context.ContextUtil;
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

import java.util.List;

/**
 * @author zz
 * 工况工法 作业分类
 * */
@Controller
@RequestMapping("/gkgf/core/workType/")
public class GkgfWorkTypeController {
    @Resource
    GkgfWorkTypeManager gkgfWorkTypeManager;
    @Resource
    CommonInfoManager commonInfoManager;

    @RequestMapping("listPage")
    public ModelAndView getListPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "gkgf/core/gkgfWorkTypeList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        JSONObject adminJson = commonInfoManager.hasPermission("GKGF-GLY");
        mv.addObject("isAdmin",adminJson.getBoolean("GKGF-GLY")||"admin".equals(ContextUtil.getCurrentUser().getUserNo()));
        return mv;
    }
    /**
     * 获取字典列表
     * */
    @RequestMapping("dictWorkType")
    @ResponseBody
    public List<JSONObject> getDictWorkType(HttpServletRequest request, HttpServletResponse response) {
        return gkgfWorkTypeManager.getDictWorkType();
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
        gkgfWorkTypeManager.saveOrUpdate(result, changeGridDataStr);
        return result;
    }
    @RequestMapping("dataList")
    @ResponseBody
    public JsonPageResult<?> getDataList(HttpServletRequest request, HttpServletResponse response) {
        return gkgfWorkTypeManager.query(request);
    }
}
