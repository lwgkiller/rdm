package com.redxun.portrait.core.controller;

import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.portrait.core.dao.PortraitManageDao;
import com.redxun.portrait.core.manager.PortraitManageManager;
import com.redxun.saweb.util.RequestUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.portrait.core.manager.PortraitHonorManager;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.util.ConstantUtil;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

/**
 * @author zhangzhen
 */
@RestController
@RequestMapping("/portrait/manage/")
public class PortraitManageController {
    @Resource
    PortraitManageManager portraitManageManager;
    @Resource
    CommonInfoManager commonInfoManager;
    @Resource
    PortraitManageDao portraitManageDao;
    /**
     * 获取编辑页面
     * */
    @RequestMapping("editPage")
    public ModelAndView getEditPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "portrait/manage/portraitManageEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String action = request.getParameter("action");
        String id = request.getParameter("id");
        JSONObject applyObj = new JSONObject();
        //1.查看、或者修改 根据id查询
        if(ConstantUtil.FORM_EDIT.equals(action)||ConstantUtil.FORM_VIEW.equals(action)){
            applyObj = portraitManageManager.getObjectById(id);
            if (applyObj.get("rewardDate") != null) {
                applyObj.put("rewardDate",
                        DateUtil.formatDate((Date)applyObj.get("rewardDate"), "yyyy-MM-dd"));
            }
        }
        if(ConstantUtil.FORM_ADD.equals(action)){
        }
        mv.addObject("action",action);
        mv.addObject("applyObj", applyObj);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        mv.addObject("editable", true);
        return mv;
    }
    /**
     * 获取列表页面
     * */
    @RequestMapping("listPage")
    public ModelAndView getDataListPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "portrait/manage/portraitManageList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        JSONObject resultJson = commonInfoManager.hasPermission("HX-GLY");
        mv.addObject("permission",resultJson.getBoolean("HX-GLY")||"admin".equals(ContextUtil.getCurrentUser().getUserNo()));
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        return mv;
    }
    /**
     * 获取列表数据
     * */
    @RequestMapping("dataList")
    @ResponseBody
    public JsonPageResult<?> getDataList(HttpServletRequest request, HttpServletResponse response) {
        return portraitManageManager.query(request);
    }
    @RequestMapping("save")
    @ResponseBody
    public JSONObject save(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");
        JSONObject resultJSON = null;
        if(StringUtil.isEmpty(id)){
            resultJSON = portraitManageManager.add(request);
        }else{
            resultJSON = portraitManageManager.update(request);
        }
        return resultJSON;
    }
    @RequestMapping("remove")
    @ResponseBody
    public JSONObject remove(HttpServletRequest request, HttpServletResponse response) {
        JSONObject resultJSON = null;
        resultJSON = portraitManageManager.remove(request);
        return resultJSON;
    }
    @RequestMapping("fileWindow")
    public ModelAndView getFileWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "portrait/manage/portraitManageFileList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String mainId = RequestUtil.getString(request, "mainId");
        String fileType = RequestUtil.getString(request, "fileType");
        Boolean editable = RequestUtil.getBoolean(request, "editable");
        mv.addObject("mainId", mainId);
        mv.addObject("fileType", fileType);
        mv.addObject("editable", editable);
        mv.addObject("currentUser", ContextUtil.getCurrentUser());
        JSONObject manageObj = portraitManageDao.getObjectById(mainId);
        String viewUserIds = manageObj.getString("viewUserIds");
        String downUserIds = manageObj.getString("downUserIds");
        String currentUserId = ContextUtil.getCurrentUserId();
        Boolean viewFlag = false;
        Boolean downFlag = false;
        if(StringUtil.isEmpty(viewUserIds)){
            viewFlag = true;
        }else{
            if(viewUserIds.indexOf(currentUserId)>-1||currentUserId.equals(manageObj.getString("CREATE_BY_"))){
                viewFlag = true;
            }
        }
        if(StringUtil.isEmpty(downUserIds)){
            downFlag = true;
        }else{
            if(downUserIds.indexOf(currentUserId)>-1||currentUserId.equals(manageObj.getString("CREATE_BY_"))){
                downFlag = true;
            }
        }
        mv.addObject("viewFlag", viewFlag);
        mv.addObject("downFlag", downFlag);
        return mv;
    }
}
