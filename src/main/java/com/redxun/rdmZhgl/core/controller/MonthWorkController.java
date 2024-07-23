
package com.redxun.rdmZhgl.core.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.StringUtil;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmZhgl.core.dao.MonthWorkFilesDao;
import com.redxun.rdmZhgl.core.service.MonthWorkService;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.sys.core.util.SysPropertiesUtil;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.redxun.org.api.model.IUser;
import com.redxun.saweb.context.ContextUtil;

/**
 * 月度重点工作
 *
 * @author zz
 */
@Controller
@RequestMapping("/rdmZhgl/core/monthWork/")
public class MonthWorkController {
    @Resource
    MonthWorkService monthWorkService;
    @Resource
    CommonInfoManager commonInfoManager;
    @Resource
    MonthWorkFilesDao monthWorkFilesDao;

    /**
     * 项目列表
     */
    @RequestMapping("getListPage")
    public ModelAndView getListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/monthWorkList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        IUser currentUser = ContextUtil.getCurrentUser();
        mv.addObject("currentUser", currentUser);
        JSONObject resultJson = commonInfoManager.hasPermission("YDGZ-JHDDY");
        JSONObject adminJson = commonInfoManager.hasPermission("YDGZ-GLY");
        mv.addObject("permission", resultJson.getBoolean("YDGZ-JHDDY"));
        mv.addObject("isAdmin", adminJson.getBoolean("YDGZ-GLY"));
        return mv;
    }

    /**
     * 获取编辑页面
     */
    @RequestMapping("getEditPage")
    public ModelAndView getEditPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/monthWorkEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String mainId = request.getParameter("mainId");
        String action = request.getParameter("action");
        JSONObject applyObj = new JSONObject();
        if (!StringUtil.isEmpty(mainId)) {
            applyObj = monthWorkService.getObjectById(mainId);
            String projectId = applyObj.getString("projectId");
            String processStatus = monthWorkService.queryProjectRisk(projectId, "");
            applyObj.put("processStatus", processStatus);
        } else {
            applyObj.put("deptId", ContextUtil.getCurrentUser().getMainGroupId());
            applyObj.put("deptName", ContextUtil.getCurrentUser().getMainGroupName());
        }

        mv.addObject("action", action);
        mv.addObject("applyObj", applyObj);
        IUser currentUser = ContextUtil.getCurrentUser();
        mv.addObject("currentUser", currentUser);
        JSONObject resultJson = commonInfoManager.hasPermission("YDGZ-JHDDY");
        mv.addObject("permission",
            resultJson.getBoolean("YDGZ-JHDDY") || resultJson.getBoolean(RdmConst.ALLDATA_QUERY_KEY));
        JSONObject adminJson = commonInfoManager.hasPermission("YDGZ-GLY");
        mv.addObject("isAdmin", adminJson.getBoolean("YDGZ-GLY"));
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        return mv;
    }

    @RequestMapping("remove")
    @ResponseBody
    public JSONObject remove(HttpServletRequest request, HttpServletResponse response) {
        JSONObject resultJSON = null;
        resultJSON = monthWorkService.remove(request);
        return resultJSON;
    }

    @RequestMapping("removeItem")
    @ResponseBody
    public JSONObject removeItem(HttpServletRequest request, HttpServletResponse response) {
        JSONObject resultJSON = null;
        resultJSON = monthWorkService.removeItem(request);
        return resultJSON;
    }

    @RequestMapping("copy")
    @ResponseBody
    public JSONObject copyPlan(HttpServletRequest request, HttpServletResponse response) {
        JSONObject resultJSON = null;
        resultJSON = monthWorkService.copyPlan(request);
        return resultJSON;
    }

    /**
     * 获取用户参与的正在运行的科技项目信息
     */
    @RequestMapping("personProjectList")
    @ResponseBody
    public List<Map<String, Object>> getProjectPersonList(HttpServletRequest request, HttpServletResponse response)
        throws ParseException {
        String userId = ContextUtil.getCurrentUserId();
        JSONObject paramJson = new JSONObject();
        JSONObject resultJson = commonInfoManager.hasPermission("YDGZ-JHDDY");
        String type = request.getParameter("type");
        if ("all".equals(type)) {
            return monthWorkService.getPersonProjectList(paramJson);
        }
        if (resultJson.getBoolean("YDGZ-JHDDY")) {
            paramJson.put("mainDepId", ContextUtil.getCurrentUser().getMainGroupId());
        } else {
            paramJson.put("userId", userId);
        }
        return monthWorkService.getPersonProjectList(paramJson);
    }

    /**
     * 获取阶段列表
     */
    @RequestMapping("stages")
    @ResponseBody
    public JSONArray getStageList(HttpServletRequest request, HttpServletResponse response) {
        String categoryId = request.getParameter("categoryId");
        JSONObject paramJson = new JSONObject();
        paramJson.put("categoryId", categoryId);
        return monthWorkService.getStageList(paramJson);
    }

    @RequestMapping("save")
    @ResponseBody
    public JSONObject save(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");
        JSONObject resultJSON = null;
        if (StringUtil.isEmpty(id)) {
            resultJSON = monthWorkService.add(request);
        } else {
            resultJSON = monthWorkService.update(request);
        }
        return resultJSON;
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
        monthWorkService.saveOrUpdateItem(changeGridDataStr);
        return result;
    }

    @RequestMapping(value = "items")
    @ResponseBody
    public List<JSONObject> getItemList(HttpServletRequest request, HttpServletResponse response) {
        return monthWorkService.getItemList(request);
    }

    @RequestMapping(value = "plans")
    @ResponseBody
    public List<Map<String, Object>> getPlanList(HttpServletRequest request, HttpServletResponse response) {
        return monthWorkService.getPlanList(request);
    }

    @RequestMapping("exportProjectPlanExcel")
    public void exportExcel(HttpServletRequest request, HttpServletResponse response) {
        monthWorkService.exportProjectPlanExcel(request, response);
    }

    /**
     * 获取总览页面
     */
    @RequestMapping("overview")
    public ModelAndView overview(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/monthWorkOverview.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        return mv;
    }

    /**
     * 获取总览页面
     */
    @RequestMapping("personView")
    public ModelAndView personView(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/monthWorkPersonView.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        return mv;
    }

    /**
     * 获取报表
     */
    @RequestMapping(value = "reportMonthPlan")
    @ResponseBody
    public List<Map<String, Object>> reportCompanyPlan(HttpServletRequest request, HttpServletResponse response) {
        return monthWorkService.reportCompanyPlan(request);
    }

    /**
     * 个人项目类
     */
    @RequestMapping(value = "personPlan")
    @ResponseBody
    public List<Map<String, Object>> getPersonPlan(HttpServletRequest request, HttpServletResponse response) {
        return monthWorkService.getPersonPlanList(request);
    }

    /**
     * 未完成
     */
    @RequestMapping(value = "unFinishPlan")
    @ResponseBody
    public List<Map<String, Object>> getUnFinishPlan(HttpServletRequest request, HttpServletResponse response) {
        return monthWorkService.getDeptUnFinishList(request);
    }

    @RequestMapping("reportPlans")
    @ResponseBody
    public JSONObject reportCompanyPlanNum(HttpServletRequest request, HttpServletResponse response) {
        return monthWorkService.reportCompanyPlanNum(request);
    }

    @RequestMapping("reportPlanData")
    @ResponseBody
    public JSONObject reportPlanData(HttpServletRequest request, HttpServletResponse response) {
        return monthWorkService.reportPlanNum(request);
    }

    /**
     * 月度工作总tab页
     */
    @RequestMapping("tabPage")
    public ModelAndView getTabPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/monthWorkTab.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        IUser currentUser = ContextUtil.getCurrentUser();
        mv.addObject("currentUser", currentUser);
        //获取人员最高权限
        JSONObject resultJson = commonInfoManager.hasPermission("YDGZ-JHDDY");
        JSONObject adminJson = commonInfoManager.hasPermission("YDGZ-GLY");
        mv.addObject("permission", resultJson.getBoolean("YDGZ-JHDDY"));
        mv.addObject("isAdmin", adminJson.getBoolean("YDGZ-GLY"));
        return mv;
    }

    /**
     * 获取未完成列表
     */
    @RequestMapping("getUnFinishPage")
    public ModelAndView getUnFinishPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/monthWorkUnFinish.jsp";
        String deptName = request.getParameter("deptName");
        String deptId = monthWorkService.getDeptId(deptName);
        String finishStatus = request.getParameter("finishStatus");
        String yearMonthStart = request.getParameter("yearMonthStart");
        String yearMonthEnd = request.getParameter("yearMonthEnd");
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("deptId", deptId);
        mv.addObject("finishStatus", finishStatus);
        mv.addObject("yearMonthStart", yearMonthStart);
        mv.addObject("yearMonthEnd", yearMonthEnd);
        return mv;
    }

    @RequestMapping("fileDownload")
    public void fileDownload(HttpServletRequest request, HttpServletResponse response) {
        try {
            String applyId = RequestUtil.getString(request, "applyId");
            String projectName = RequestUtil.getString(request, "projectName");
            String yearMonth = RequestUtil.getString(request, "yearMonth");
            String fileUrl = RequestUtil.getString(request, "fileUrl");
            String fileBasePath = SysPropertiesUtil.getGlobalProperty(fileUrl);
            List<JSONObject> filePathList = new ArrayList<>();
            List<JSONObject> fileList = monthWorkFilesDao.getFilesById(applyId);
            JSONObject pathObj = new JSONObject();
            for (JSONObject fileObj : fileList) {
                String fileId = fileObj.getString("id");
                String fileName = fileObj.getString("fileName");
                String suffix = CommonFuns.toGetFileSuffix(fileName);
                String relativeFilePath = "";
                if (StringUtils.isNotBlank(applyId)) {
                    relativeFilePath = File.separator + applyId;
                }
                String realFileName = fileId + "." + suffix;
                String fullFilePath = fileBasePath + relativeFilePath + File.separator + realFileName;
                pathObj = new JSONObject();
                pathObj.put("path", fullFilePath);
                pathObj.put("fileName", fileName);
                filePathList.add(pathObj);
            }
            if (filePathList.size() != 0) {
                // 创建临时路径，存放压缩文件
                String downFile = yearMonth+"-"+projectName+ "-证明材料.zip";
                String zipFilePath = fileBasePath + File.separator + downFile;
                ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFilePath));
                for (JSONObject path : filePathList) {
                    monthWorkService.fileToZip(path.getString("path"), zipOutputStream, path.getString("fileName"));
                }
                // 压缩完成后，关闭压缩流
                zipOutputStream.close();
                String fileName = new String(downFile.getBytes(), "ISO8859-1");
                response.setHeader("Content-Disposition", "attachment;filename=" + fileName);

                ServletOutputStream outputStream = response.getOutputStream();
                FileInputStream inputStream = new FileInputStream(zipFilePath);

                IOUtils.copy(inputStream, outputStream);
                inputStream.close();
                outputStream.close(); //关闭流
                File fileTempZip = new File(zipFilePath);
                fileTempZip.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
