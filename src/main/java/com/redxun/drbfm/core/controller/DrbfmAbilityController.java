package com.redxun.drbfm.core.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.drbfm.core.service.DrbfmAbilityService;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;

@Controller
@RequestMapping("/drbfm/ability/")
public class DrbfmAbilityController {
    private Logger logger = LogManager.getLogger(DrbfmAbilityController.class);
    @Autowired
    private DrbfmAbilityService drbfmAbilityService;

    @RequestMapping("abilityListPage")
    public ModelAndView totalListPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "drbfm/verifyAbilityList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserDeptId", ContextUtil.getCurrentUser().getMainGroupId());
        mv.addObject("currentUserDeptName", ContextUtil.getCurrentUser().getMainGroupName());
        return mv;
    }

    @RequestMapping("getAbilityList")
    @ResponseBody
    public JsonPageResult<?> getAbilityList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return drbfmAbilityService.getAbilityList(request, response);
    }

    @RequestMapping("deleteAbility")
    @ResponseBody
    public JsonResult deleteAbility(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            JsonResult result = new JsonResult(true, "操作成功！");
            String uIdStr = RequestUtil.getString(request, "ids");
            if (StringUtils.isBlank(uIdStr)) {
                return result;
            }
            String[] totalIds = uIdStr.split(",");
            return drbfmAbilityService.deleteAbility(totalIds);
        } catch (Exception e) {
            logger.error("Exception in deleteAbility", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("saveAbility")
    @ResponseBody
    public JsonResult saveAbility(HttpServletRequest request, HttpServletResponse response,
        @RequestBody String postBody) {
        JsonResult result = new JsonResult(true, "保存成功");
        if (StringUtils.isBlank(postBody)) {
            logger.warn("postBody is blank");
            result.setSuccess(false);
            result.setMessage("表单为空，保存失败！");
            return result;
        }
        JSONObject formJSON = JSONObject.parseObject(postBody);
        drbfmAbilityService.saveAbility(result, formJSON);
        return result;
    }

}
