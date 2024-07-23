package com.redxun.environment.core.controller;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.OfficeDocPreview;
import com.redxun.environment.core.dao.RjbgDao;
import com.redxun.environment.core.service.ZjhmService;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/environment/core/Zjhm/")
public class ZjhmController {
    private Logger logger = LogManager.getLogger(ZjhmController.class);
    @Autowired
    private CommonBpmManager commonBpmManager;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;
    @Resource
    private ZjhmService zjhmService;

    //列表页面跳转
    @RequestMapping("zjhmList")
    public ModelAndView msgManagement(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "rap/core/zjhmList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        return mv;
    }

    //编辑页面跳转
    @RequestMapping("editPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rap/core/zjhmEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        // 新增或者编辑页面由流程引擎跳转并替换url中的pk和nodeId_
        String projectId = RequestUtil.getString(request, "projectId");
        String nodeId = RequestUtil.getString(request, "nodeId");
        String action = RequestUtil.getString(request, "action");
        String status = RequestUtil.getString(request, "status");
        if (StringUtils.isBlank(action)) {
            // 新增或编辑的时候没有nodeId
            if (StringUtils.isBlank(nodeId) || nodeId.contains("PROCESS")) {
                action = "edit";
            } else {
                // 处理任务的时候有nodeId
                action = "task";
            }
        }
        mv.addObject("projectId", projectId).addObject("action", action).addObject("status", status);
        // 取出节点变量，返回到页面
        if (StringUtils.isNotBlank(nodeId)) {
            mv.addObject("nodeId", nodeId);
            List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId,"ZJHM",null);
            if (nodeVars != null && !nodeVars.isEmpty()) {
                mv.addObject("nodeVars", JSONObject.toJSONString(nodeVars));
            }
        }
        JSONObject currentUserDepInfo = xcmgProjectManager.isCurrentUserDepRespman();
        Map<String, Object> params = new HashMap<>();
        params.put("currentUserMainDepId", currentUserDepInfo.getString("currentUserMainDepId"));
//        JSONObject dept = commonInfoManager.queryDeptNameById(currentUserDepInfo.getString("currentUserMainDepId"));
//        String deptName = dept.getString("deptname");
//        mv.addObject("deptName", deptName);
//        mv.addObject("currentUserMainDepId", currentUserDepInfo.getString("currentUserMainDepId"));
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        return mv;
    }

    //编辑页面基础数据获取
    @RequestMapping("getZjhmDetail")
    @ResponseBody
    public JSONObject getZjhmDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject zjhmObj = new JSONObject();
        String projectId = RequestUtil.getString(request, "projectId");
        if (StringUtils.isNotBlank(projectId)) {
            zjhmObj = zjhmService.getZjhmDetail(projectId);
        }
        return zjhmObj;
    }

    //列表页面列表内容获取
    @RequestMapping("queryZjhm")
    @ResponseBody
    public JsonPageResult<?> queryZjhm(HttpServletRequest request, HttpServletResponse response) {
        return zjhmService.queryZjhm(request, true);
    }

    //流程删除
    @RequestMapping("deleteZjhm")
    @ResponseBody
    public JsonResult deleteZjhm(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            return zjhmService.deleteZjhm(request);
        } catch (Exception e) {
            logger.error("Exception in deleteZjhm", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    //文件列表获取
    @RequestMapping("/getZjhmFileList")
    @ResponseBody
    public List<JSONObject> getZjhmFileList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String projectId = RequestUtil.getString(request, "projectId");
        if (StringUtils.isBlank(projectId)) {
            logger.error("projectId is blank");
            return null;
        }
        Map<String, Object> params = new HashMap<>();
        List<String> projectIds = new ArrayList<>();
        projectIds.add(projectId);
        params.put("projectIds", projectIds);
        List<JSONObject> fileInfos = zjhmService.getZjhmFileList(params);
        // 格式化时间
        if (fileInfos != null && !fileInfos.isEmpty()) {
            for (JSONObject oneFile : fileInfos) {
                if (StringUtils.isNotBlank(oneFile.getString("CREATE_TIME_"))) {
                    oneFile.put("CREATE_TIME_",
                            DateUtil.formatDate(oneFile.getDate("CREATE_TIME_"), "yyyy-MM-dd HH:mm:ss"));
                }
            }
        }
        return fileInfos;
    }

    //文件上传页面跳转
    @RequestMapping("/openUploadWindow")
    public ModelAndView fileUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rap/core/zjhmFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("projectId", RequestUtil.getString(request, "projectId"));
        // 查询附件类型
        return mv;
    }

    //文件上传
    @RequestMapping(value = "oaUpload")
    @ResponseBody
    public Map<String, Object> upload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        try {
            // 先保存文件到磁盘，成功后再写入数据库，前台是一个一个文件的调用
            zjhmService.saveUploadFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            logger.error("Exception in upload", e);
            modelMap.put("success", "false");
        }
        return modelMap;
    }

    // 删除某个文件（从文件表和磁盘上都删除）
    @RequestMapping("/delUploadFile")
    public void delete(HttpServletRequest request, HttpServletResponse response, @RequestBody String postBody) {
        JSONObject postBodyObj = JSONObject.parseObject(postBody);
        String fileName = postBodyObj.getString("fileName");
        String id = postBodyObj.getString("id");
        String projectId = postBodyObj.getString("formId");
        zjhmService.delOAUploadFile(id, fileName, projectId);
    }

    // 文档的下载（预览pdf也调用这里）
    @RequestMapping("/fileDownload")
    public ResponseEntity<byte[]> fileDownload(HttpServletRequest request, HttpServletResponse response) {
        try {
            String fileName = RequestUtil.getString(request, "fileName");
            if (StringUtils.isBlank(fileName)) {
                logger.error("操作失败，文件名为空！");
                return null;
            }
            String fileId = RequestUtil.getString(request, "fileId");
            if (StringUtils.isBlank(fileId)) {
                logger.error("操作失败，文件Id为空！");
                return null;
            }
            String projectId = RequestUtil.getString(request, "formId");
            if (StringUtils.isBlank(projectId)) {
                logger.error("操作失败，流程主键为空！");
                return null;
            }
            String fileBasePath = WebAppUtil.getProperty("zjhmFilePathBase");
            if (StringUtils.isBlank(fileBasePath)) {
                logger.error("操作失败，找不到存储路径");
                return null;
            }
            String suffix = CommonFuns.toGetFileSuffix(fileName);
            String fullFilePath = fileBasePath + File.separator + projectId + File.separator + fileId + "." + suffix;
            // 创建文件实例
            File file = new File(fullFilePath);
            // 修改文件名的编码格式
            String downloadFileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");

            // 设置httpHeaders,使浏览器响应下载
            HttpHeaders headers = new HttpHeaders();
            // 告诉浏览器执行下载的操作，“attachment”告诉了浏览器进行下载,下载的文件 文件名为 downloadFileName
            headers.setContentDispositionFormData("attachment", downloadFileName);
            // 设置响应方式为二进制，以二进制流传输
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file), headers, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Exception in fileDownload", e);
            return null;
        }
    }


    @RequestMapping("/officePreview")
    @ResponseBody
    public void officePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        if (StringUtils.isBlank(fileName)) {
            logger.error("操作失败，文件名为空！");
            return;
        }
        String fileId = RequestUtil.getString(request, "fileId");
        if (StringUtils.isBlank(fileId)) {
            logger.error("操作失败，文件Id为空！");
            return;
        }
        String projectId = RequestUtil.getString(request, "formId");
        if (StringUtils.isBlank(projectId)) {
            logger.error("操作失败，流程主键为空！");
            return;
        }
        String fileBasePath = WebAppUtil.getProperty("zjhmFilePathBase");
        if (StringUtils.isBlank(fileBasePath)) {
            logger.error("操作失败，找不到存储路径");
            return;
        }
        String suffix = CommonFuns.toGetFileSuffix(fileName);
        String originalFilePath = fileBasePath + File.separator + projectId + File.separator + fileId + "." + suffix;
        String convertPdfDir = WebAppUtil.getProperty("convertPdfDir");
        String convertPdfPath =
                fileBasePath + File.separator + projectId + File.separator + convertPdfDir + File.separator + fileId + ".pdf";;
        OfficeDocPreview.previewOfficeDoc(originalFilePath, convertPdfPath, response);
    }

    @RequestMapping("/imagePreview")
    @ResponseBody
    public void imagePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        if (StringUtils.isBlank(fileName)) {
            logger.error("操作失败，文件名为空！");
            return;
        }
        String fileId = RequestUtil.getString(request, "fileId");
        if (StringUtils.isBlank(fileId)) {
            logger.error("操作失败，文件Id为空！");
            return;
        }
        String projectId = RequestUtil.getString(request, "formId");
        if (StringUtils.isBlank(projectId)) {
            logger.error("操作失败，流程主键为空！");
            return;
        }
        String fileBasePath = WebAppUtil.getProperty("zjhmFilePathBase");
        if (StringUtils.isBlank(fileBasePath)) {
            logger.error("操作失败，找不到存储路径");
            return;
        }
        String suffix = CommonFuns.toGetFileSuffix(fileName);
        String originalFilePath = fileBasePath + File.separator + projectId + File.separator + fileId + "." + suffix;
        OfficeDocPreview.imagePreview(originalFilePath, response);
    }
}
