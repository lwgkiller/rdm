
package com.redxun.rdmZhgl.core.controller;

import java.util.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.core.util.DateUtil;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmZhgl.core.dao.GzxmProjectInfoDao;
import com.redxun.rdmZhgl.core.service.GzxmProjectInfoService;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.standardManager.core.util.ResultUtil;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
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
import com.redxun.core.util.StringUtil;
import com.redxun.org.api.model.IUser;
import com.redxun.saweb.context.ContextUtil;

/**
 * 国重项目
 *
 * @author zz
 */
@Controller
@RequestMapping("/rdmZhgl/core/gzxm/project/")
public class GzxmProjectInfoController {
    @Resource
    GzxmProjectInfoService gzxmProjectInfoService;
    @Resource
    GzxmProjectInfoDao gzxmProjectInfoDao;
    @Resource
    CommonInfoManager commonInfoManager;
    /**
     * 列表
     */
    @RequestMapping("getListPage")
    public ModelAndView getListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/gzxmProjectList.jsp";
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
        String jspPath = "rdmZhgl/core/gzxmProjectEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String mainId = request.getParameter("mainId");
        String action = request.getParameter("action");
        JSONObject applyObj = new JSONObject();
        Boolean isReporter = false;
        if (!StringUtil.isEmpty(mainId)) {
            applyObj = gzxmProjectInfoService.getObjectById(mainId);
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
        String jspPath = "rdmZhgl/core/gzxmProjectView.jsp";
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
        resultJSON = gzxmProjectInfoService.remove(request);
        return resultJSON;
    }

    @RequestMapping("save")
    @ResponseBody
    public JSONObject save(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");
        JSONObject resultJSON = null;
        if (StringUtil.isEmpty(id)) {
            resultJSON = gzxmProjectInfoService.add(request);
        } else {
            resultJSON = gzxmProjectInfoService.update(request);
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
        result = gzxmProjectInfoService.saveOrUpdateItem(changeGridDataStr);
        return result;
    }

    @RequestMapping(value = "items")
    @ResponseBody
    public List<JSONObject> getItemList(HttpServletRequest request, HttpServletResponse response) {
        return gzxmProjectInfoService.getItemList(request);
    }

    @RequestMapping(value = "list")
    @ResponseBody
    public JsonPageResult<?> getPlanList(HttpServletRequest request, HttpServletResponse response) {
        return gzxmProjectInfoService.query(request);
    }

    @RequestMapping(value = "gzxmList")
    @ResponseBody
    public List<JSONObject> getGzxmList(HttpServletRequest request, HttpServletResponse response) {
        return gzxmProjectInfoService.getGzxmList(request);
    }

    /**
     * 模板下载
     */
    @RequestMapping("/importTemplateDownload")
    public ResponseEntity<byte[]> importTemplateDownload(HttpServletRequest request, HttpServletResponse response) {
        return gzxmProjectInfoService.importTemplateDownload();
    }

    /**
     * 批量导入
     */
    @RequestMapping("importExcel")
    @ResponseBody
    public JSONObject importExcel(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        gzxmProjectInfoService.importGtzz(result, request);
        return result;
    }
    /**
     * 项目任务列表
     * */
    @RequestMapping("projectTaskPage")
    public ModelAndView getProjectTaskPage(HttpServletRequest request,HttpServletResponse response) throws Exception{
        String jspPath = "rdmZhgl/core/gzxmProjectTaskList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        IUser currentUser = ContextUtil.getCurrentUser();
        String mainId = request.getParameter("mainId");
        String gzxmAdmin = request.getParameter("gzxmAdmin");
        String isReporter = request.getParameter("isReporter");
        mv.addObject("gzxmAdmin",gzxmAdmin);
        mv.addObject("isReporter",isReporter);
        mv.addObject("currentUser", currentUser);
        mv.addObject("mainId",mainId);
        return mv;
    }
    @RequestMapping("fileWindow")
    public ModelAndView workPlanFileWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/gzxmFileList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String taskId = RequestUtil.getString(request, "taskId");
        mv.addObject("taskId", taskId);
        String editable = RequestUtil.getString(request, "editable");
        String gzxmAdmin = RequestUtil.getString(request, "gzxmAdmin");
        String isReporter = RequestUtil.getString(request, "isReporter");
        String fileType = RequestUtil.getString(request, "fileType");
        mv.addObject("fileType", fileType);
        mv.addObject("editable", editable);
        mv.addObject("gzxmAdmin", gzxmAdmin);
        mv.addObject("isReporter", isReporter);
        mv.addObject("currentUser", ContextUtil.getCurrentUser());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        return mv;
    }
    /**
     * 查询交付物信息
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("files")
    @ResponseBody
    public List<JSONObject> getFiles(HttpServletRequest request, HttpServletResponse response) {
        List<JSONObject> fileInfos = gzxmProjectInfoService.getFileListByMainId(request);
        return fileInfos;
    }
    @RequestMapping("openUploadWindow")
    public ModelAndView openUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/gzxmFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUser", ContextUtil.getCurrentUser());
        return mv;
    }

    @RequestMapping(value = "upload")
    @ResponseBody
    public Map<String, Object> upload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>(16);
        try {
            // 先保存文件到磁盘，成功后再写入数据库，前台是一个一个文件的调用
            gzxmProjectInfoService.saveUploadFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return modelMap;
    }
    @RequestMapping("deleteFiles")
    public void deleteFiles(HttpServletRequest request, HttpServletResponse response) {
        String taskId = RequestUtil.getString(request, "taskId");
        String id = RequestUtil.getString(request, "id");
        String fileName = RequestUtil.getString(request, "fileName");
        String suffix = CommonFuns.toGetFileSuffix(fileName);
        gzxmProjectInfoService.deleteFileOnDisk(taskId, id, suffix,"gzxmFileUrl");
        Map<String, Object> fileParams = new HashMap<>(16);
        List<String> fileIds = new ArrayList<>();
        fileIds.add(id);
        fileParams.put("fileIds", fileIds);
        gzxmProjectInfoDao.deleteFileByIds(fileParams);
    }

    @RequestMapping("exportProjectTask")
    public void exportProjectTask(HttpServletRequest request, HttpServletResponse response) {
        gzxmProjectInfoService.exportProjectTask(request, response);
    }
}
