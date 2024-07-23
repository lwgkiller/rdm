package com.redxun.zlgjNPI.core.controller;

import com.redxun.core.json.JsonPageResult;
import com.redxun.org.api.model.IUser;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.zlgjNPI.core.manager.ProductManListManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/zhgl/core/kfgksj/")
public class ProductManListController {
    private Logger logger = LoggerFactory.getLogger(ProductManListController.class);
    @Autowired
    private ProductManListManager productManListManager;

    @RequestMapping("listPage")
    public ModelAndView listPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "zlgjNPI/productManDataList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        IUser currentUser = ContextUtil.getCurrentUser();
        mv.addObject("currentUser", currentUser);
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        return mv;
    }
    @RequestMapping("getProductManDataList")
    @ResponseBody
    public JsonPageResult<?> getProductManageList(HttpServletRequest request, HttpServletResponse response) {
        return productManListManager.getProductManDataList(request, response, true);
    }
}
