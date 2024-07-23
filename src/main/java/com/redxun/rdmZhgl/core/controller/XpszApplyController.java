
package com.redxun.rdmZhgl.core.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.manager.MybatisBaseManager;
import com.redxun.org.api.model.IUser;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.rdmZhgl.core.dao.XpszApplyDao;
import com.redxun.rdmZhgl.core.service.ProductService;
import com.redxun.rdmZhgl.core.service.XpszApplyService;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.controller.MybatisListController;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

/**
 * 新品试制月度审批流程
 * 
 * @author zz
 */
@Controller
@RequestMapping("/rdmZhgl/core/xpszApply/")
public class XpszApplyController extends MybatisListController {
    @Resource
    ProductService productService;

    @SuppressWarnings("rawtypes")
    @Override
    public MybatisBaseManager getBaseManager() {
        return null;
    }




    /**
     * 明细：查看审批信息
     */
    @RequestMapping("detail")
    public ModelAndView detail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = RequestUtil.getString(request, "id");
        String jspPath = "rdmZhgl/core/xpszDetail.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("id", id);
        JSONObject applyObj = new JSONObject();
        applyObj.put("mainIds", id);
        mv.addObject("applyObj", applyObj);
        JSONArray list = productService.getJsonArray(request);
        mv.addObject("listData", list.toJSONString());
        return mv;
    }
}
