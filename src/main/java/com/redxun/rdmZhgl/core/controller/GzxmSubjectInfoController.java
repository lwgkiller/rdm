
package com.redxun.rdmZhgl.core.controller;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmZhgl.core.service.GzxmSubjectInfoService;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.org.api.model.IUser;
import com.redxun.rdmZhgl.core.service.GzxmProjectInfoService;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.standardManager.core.util.ResultUtil;

/**
 * 国重项目-课题管理
 *
 * @author zz
 */
@Controller
@RequestMapping("/rdmZhgl/core/gzxm/subject/")
public class GzxmSubjectInfoController {
    @Resource
    GzxmSubjectInfoService gzxmSubjectInfoService;
    @Resource
    CommonInfoManager commonInfoManager;

    /**
     * 列表
     */
    @RequestMapping("getListPage")
    public ModelAndView getListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/gzxmSubjectList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        IUser currentUser = ContextUtil.getCurrentUser();
        mv.addObject("currentUser", currentUser);
        JSONObject adminJson = commonInfoManager.hasPermission("GZXM-GLY");
        mv.addObject("gzxmAdmin",adminJson.getBoolean("GZXM-GLY")||"admin".equals(currentUser.getUserNo()));
        return mv;
    }

    /**
     * 获取编辑页面
     */
    @RequestMapping("getEditPage")
    public ModelAndView getEditPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/gzxmSubjectEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String mainId = request.getParameter("mainId");
        String action = request.getParameter("action");
        JSONObject applyObj = new JSONObject();
        Boolean isReporter = false;
        if (!StringUtil.isEmpty(mainId)) {
            applyObj = gzxmSubjectInfoService.getObjectById(mainId);
            if (applyObj.get("startDate") != null) {
                applyObj.put("startDate", DateUtil.formatDate((Date)applyObj.get("startDate"), "yyyy-MM-dd"));
            }
            if (applyObj.get("endDate") != null) {
                applyObj.put("endDate", DateUtil.formatDate((Date)applyObj.get("endDate"), "yyyy-MM-dd"));
            }
            String userId = ContextUtil.getCurrentUserId();
            String reportUserId = applyObj.getString("reportUserId");
            if(reportUserId.equals(userId)){
                isReporter = true;
            }
        }

        mv.addObject("action", action);
        mv.addObject("applyObj", applyObj);
        IUser currentUser = ContextUtil.getCurrentUser();
        JSONObject adminJson = commonInfoManager.hasPermission("GZXM-GLY");
        mv.addObject("gzxmAdmin",adminJson.getBoolean("GZXM-GLY")||"admin".equals(currentUser.getUserNo()));
        mv.addObject("isReporter", isReporter);
        return mv;
    }

    /**
     * 挂图督战展示列表
     */
    @RequestMapping("getViewListPage")
    public ModelAndView getViewListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/gtzzViewList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        IUser currentUser = ContextUtil.getCurrentUser();
        mv.addObject("currentUser", currentUser);
        return mv;
    }

    /**
     * getViewPage 获取编辑页面
     */
    @RequestMapping("getViewPage")
    public ModelAndView getViewPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/gzxmSubjectView.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String mainId = request.getParameter("mainId");
        JSONObject applyObj = new JSONObject();
        mv.addObject("mainId", mainId);
        mv.addObject("applyObj", applyObj);
        IUser currentUser = ContextUtil.getCurrentUser();
        mv.addObject("currentUser", currentUser);
        return mv;
    }

    @RequestMapping("remove")
    @ResponseBody
    public JSONObject remove(HttpServletRequest request, HttpServletResponse response) {
        JSONObject resultJSON = null;
        resultJSON = gzxmSubjectInfoService.remove(request);
        return resultJSON;
    }

    @RequestMapping("save")
    @ResponseBody
    public JSONObject save(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");
        JSONObject resultJSON = null;
        if (StringUtil.isEmpty(id)) {
            resultJSON = gzxmSubjectInfoService.add(request);
        } else {
            resultJSON = gzxmSubjectInfoService.update(request);
        }
        return resultJSON;
    }

    @RequestMapping(value = "dealData", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject dealData(HttpServletRequest request, @RequestBody String changeGridDataStr,
        HttpServletResponse response) {
        JSONObject result = new JSONObject();
        if (StringUtils.isBlank(changeGridDataStr)) {
            return ResultUtil.result(false,"requestBody is blank",null);
        }
        result = gzxmSubjectInfoService.saveOrUpdateItem(changeGridDataStr);
        return result;
    }

    @RequestMapping(value = "items")
    @ResponseBody
    public List<JSONObject> getItemList(HttpServletRequest request, HttpServletResponse response) {
        return gzxmSubjectInfoService.getItemList(request);
    }

    @RequestMapping(value = "list")
    @ResponseBody
    public JsonPageResult<?> getPlanList(HttpServletRequest request, HttpServletResponse response) {
        return gzxmSubjectInfoService.query(request);
    }

    @RequestMapping(value = "gzxmList")
    @ResponseBody
    public List<JSONObject> getGzxmList(HttpServletRequest request, HttpServletResponse response) {
        return gzxmSubjectInfoService.getGzxmList(request);
    }

    /**
     * 模板下载
     */
    @RequestMapping("/importTemplateDownload")
    public ResponseEntity<byte[]> importTemplateDownload(HttpServletRequest request, HttpServletResponse response) {
        return gzxmSubjectInfoService.importTemplateDownload();
    }

    /**
     * 批量导入
     */
    @RequestMapping("importExcel")
    @ResponseBody
    public JSONObject importExcel(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        gzxmSubjectInfoService.importGtzz(result, request);
        return result;
    }
    /**
     * 课题任务列表
     * */
    @RequestMapping("subjectTaskPage")
    public ModelAndView getSubjectTaskPage(HttpServletRequest request,HttpServletResponse response) throws Exception{
        String jspPath = "rdmZhgl/core/gzxmSubjectTaskList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        IUser currentUser = ContextUtil.getCurrentUser();
        String mainId = request.getParameter("mainId");
        String gzxmAdmin = request.getParameter("gzxmAdmin");
        String isReporter = request.getParameter("isReporter");
        mv.addObject("currentUser", currentUser);
        mv.addObject("mainId",mainId);
        mv.addObject("gzxmAdmin",gzxmAdmin);
        mv.addObject("isReporter",isReporter);
        return mv;
    }
    @RequestMapping("exportSubjectTask")
    public void exportSubjectTask(HttpServletRequest request, HttpServletResponse response) {
        gzxmSubjectInfoService.exportSubjectTask(request, response);
    }
}
