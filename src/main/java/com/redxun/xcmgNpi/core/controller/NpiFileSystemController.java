package com.redxun.xcmgNpi.core.controller;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.xcmgNpi.core.manager.NpiFileSystemManager;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.org.api.model.IUser;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.controller.GenericController;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.standardManager.core.manager.StandardSystemManager;
import com.redxun.standardManager.core.util.BussinessUtil;
import com.redxun.standardManager.core.util.StandardConstant;
import com.redxun.standardManager.core.util.StandardManagerUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

@Controller
@RequestMapping("/xcmgNpi/core/npiFileSystem/")
public class NpiFileSystemController {
    private static final Logger logger = LoggerFactory.getLogger(NpiFileController.class);

    @Autowired
    private NpiFileSystemManager npiFileSystemManager;

    // 体系管理框架页面
    @RequestMapping("managementPage")
    public ModelAndView managementPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "xcmgNpi/core/npiFileSystemPage.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        return mv;
    }

    // 编辑节点
    @RequestMapping("treeEdit")
    public ModelAndView treeEdit(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "xcmgNpi/core/npiFileSystemEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        return mv;
    }


    // 保存体系树
    @RequestMapping("treeSave")
    @ResponseBody
    public JSONObject treeSave(HttpServletRequest request, @RequestBody String changedDataStr,
        HttpServletResponse response) {
        JSONObject result = new JSONObject();
        if (StringUtils.isBlank(changedDataStr)) {
            logger.warn("requestBody is blank");
            result.put("message", "数据为空");
            return result;
        }
        npiFileSystemManager.treeSave(result, changedDataStr);
        return result;
    }

    // 查询体系类别下文件的个数
    @RequestMapping("queryNpiFileBySystemIds")
    @ResponseBody
    public JSONObject queryStandardBySystemIds(HttpServletRequest request, @RequestBody String systemIds,
        HttpServletResponse response) {
        JSONObject result = new JSONObject();
        if (StringUtils.isBlank(systemIds)) {
            logger.warn("requestBody is blank");
            result.put("num", 0);
            return result;
        }
        npiFileSystemManager.queryNpiFileBySystemIds(result, systemIds);
        return result;
    }
}
