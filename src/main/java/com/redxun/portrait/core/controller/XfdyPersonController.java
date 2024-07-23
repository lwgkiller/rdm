package com.redxun.portrait.core.controller;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.portrait.core.dao.XfdyPersonDao;
import com.redxun.portrait.core.manager.XfdyPersonService;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 *
 *
 * @author zhangzhen
 */
@Controller
@RequestMapping("/person/core/")
public class XfdyPersonController {
    private Logger logger = LogManager.getLogger(XfdyPersonController.class);
    @Resource
    private XfdyPersonService xfdyPersonService;
    @Resource
    private XfdyPersonDao xfdyPersonDao;
    @Resource
    private RdmZhglUtil rdmZhglUtil;

    @RequestMapping("personPage")
    public ModelAndView xfdyPersonPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "portrait/userInfo/xfdyPersonList.jsp";
        boolean isXfdy = rdmZhglUtil.judgeIsPointRoleUser(ContextUtil.getCurrentUserId(), "先锋党员专员");
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("isXfdy", isXfdy);
        return mv;
    }

    @RequestMapping("edit")
    public ModelAndView editGjll(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "portrait/userInfo/xfdyPersonEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String action = RequestUtil.getString(request, "action");
        String xfdyId = RequestUtil.getString(request, "xfdyId");
        Map<String, Object> params = new HashMap<>();
        mv.addObject("action", action);
        mv.addObject("xfdyId", xfdyId);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUser", ContextUtil.getCurrentUser());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        return mv;
    }

    @RequestMapping("saveBusiness")
    @ResponseBody
    public JSONObject saveBusiness(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        xfdyPersonService.saveBusiness(result, request);
        return result;
    }

    @RequestMapping("deleteXfdyPerson")
    @ResponseBody
    public JsonResult deleteXfdyPerson(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return xfdyPersonService.deleteXfdyPerson(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteSecret", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    @ResponseBody
    @RequestMapping("getPerosn")
    public JsonPageResult<?> queryJstb(HttpServletRequest request, HttpServletResponse response) {
        return xfdyPersonService.queryXfdyPerson(request, false);
    }

    @RequestMapping("getXfdyDetail")
    @ResponseBody
    public JSONObject getGjllDetail(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        JSONObject gjllobj = new JSONObject();
        String xfdyId = RequestUtil.getString(request, "xfdyId");
        if (StringUtils.isNotBlank(xfdyId)) {
            gjllobj = xfdyPersonService.getXfdyPersonDetail(xfdyId);
        }
        return gjllobj;
    }


    
    @RequestMapping("downloadShowFile")
    public void downloadShowFile(HttpServletRequest request, HttpServletResponse response){
        String prePath = "E:/devManagement/document/xfdy/";
        String xfdyId = RequestUtil.getString(request, "xfdyId");
        JSONObject imgJosn = xfdyPersonDao.queryImgById(xfdyId);
        String fileName = imgJosn.getString("fileName");
        String suffix = CommonFuns.toGetFileSuffix(fileName);
        FileInputStream fileInputStream = null;
        OutputStream outputStream =null;
        try {
            //拼接路径   根据自己实际情况拼接
            fileInputStream= new FileInputStream(prePath+xfdyId+"."+suffix);
            outputStream = response.getOutputStream();
            FileCopyUtils.copy(fileInputStream,outputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
