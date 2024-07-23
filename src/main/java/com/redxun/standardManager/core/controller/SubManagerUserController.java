package com.redxun.standardManager.core.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.redxun.core.json.JsonPageResult;
import com.redxun.standardManager.core.manager.SubManagerUserService;

/**
 * @author zz 群组人员
 */
@Controller
@RequestMapping("/standardManager/core/subManagerUser/")
public class SubManagerUserController {
    private static final Logger logger = LoggerFactory.getLogger(SubManagerGroupController.class);
    @Resource
    private SubManagerUserService subManagerUserService;

    /**
     * 获取列表页面
     */
    @RequestMapping("groupUserListPage")
    public ModelAndView getStorageListPage(HttpServletRequest request, HttpServletResponse response) {
        String groupId = request.getParameter("groupId");
        String jspPath = "standardManager/core/subManagerUserList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("groupId", groupId);
        return mv;
    }

    @RequestMapping("groupUserList")
    @ResponseBody
    public JsonPageResult<?> groupUserList(HttpServletRequest request, HttpServletResponse response) {
        return subManagerUserService.query(request);
    }
}
