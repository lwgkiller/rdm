
package com.redxun.xcmgProjectManager.core.controller;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONArray;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.OfficeDocPreview;
import com.redxun.org.api.model.IUser;
import com.redxun.rdmCommon.core.manager.PdmApiManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectDeliveryApprovalDao;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectFileUploadManager;
import com.redxun.xcmgProjectManager.core.util.ConstantUtil;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

/**
 * project_baseInfo控制器
 *
 * @author x
 */
@Controller
@RequestMapping("/xcmgProjectManager/core/fileUpload/")
public class XcmgProjectFileUploadController {
    private Logger logger = LoggerFactory.getLogger(XcmgProjectFileUploadController.class);
    @Resource
    private XcmgProjectFileUploadManager fileUploadManager;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private XcmgProjectDeliveryApprovalDao xcmgProjectDeliveryApprovalDao;
    @Autowired
    private PdmApiManager pdmApiManager;

    @RequestMapping("openUploadWindow")
    public ModelAndView openUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "xcmgProjectManager/core/multiFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String projectId = request.getParameter("projectId");
        mv.addObject("currentUser", ContextUtil.getCurrentUser());
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("projectId", projectId);
        return mv;
    }

    @RequestMapping("openProductUploadWindow")
    public ModelAndView openProductUploadWindow(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String jspPath = "xcmgProjectManager/core/multiProductFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String projectId = request.getParameter("projectId");
        String productIds = request.getParameter("productIds");
        mv.addObject("currentUser", ContextUtil.getCurrentUser());
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("projectId", projectId);
        mv.addObject("includeProductIds", productIds);
        return mv;
    }

    @RequestMapping(value = "upload")
    @ResponseBody
    public Map<String, Object> upload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        try {
            // 先保存文件，成功后再写入数据库，前台是一个一个文件的调用
            fileUploadManager.saveUploadFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            logger.error("Exception in upload", e);
            modelMap.put("success", "false");
        }
        return modelMap;
    }

    /**
     * 项目详情页面显示交付物文档的树形
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("getProjectFiles")
    @ResponseBody
    public List<Map<String, Object>> getFiles(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String projectId = RequestUtil.getString(request, "projectId");
        if (StringUtils.isBlank(projectId)) {
            return null;
        }
        String tenantId = ContextUtil.getCurrentTenantId();
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", tenantId);
        params.put("projectId", projectId);
        List<Map<String, Object>> fileInfos = xcmgProjectDeliveryApprovalDao.queryDeliveryApproval(params);
        if (fileInfos != null && !fileInfos.isEmpty()) {
            for (Map<String, Object> oneFile : fileInfos) {
                // 格式化时间
                if (oneFile.get("CREATE_TIME_") != null) {
                    oneFile.put("CREATE_TIME_",
                            DateUtil.formatDate((Date) oneFile.get("CREATE_TIME_"), "yyyy-MM-dd HH:mm:ss"));
                }

                // 文件审核状态
                String approvalStatus = toGetApprovalStatus(oneFile);
                oneFile.put("approvalStatus", approvalStatus);
            }
        }
        // 查询SDM(仿真设计)中与科技项目关联的仿真申请
        List<Map<String, Object>> fzsjInfos = fileUploadManager.queryFzsjByProjectId(projectId, null);
        if (fzsjInfos != null) {
            fileInfos.addAll(fzsjInfos);
        }

        // 通过项目Id查询PDM系统中项目的交付物
        List<Map<String, Object>> pdmFiles = pdmApiManager.getPdmProjectFiles(params);
        if (pdmFiles != null&& !pdmFiles.isEmpty()) {
            List<Map<String, Object>> pdmFilesInRdm = xcmgProjectOtherDao.queryPdmProjectFileInfos(params);
            Set<String> fileNumberInRdm = new HashSet<>();
            for (Map<String, Object> stringObjectMap : pdmFilesInRdm) {
                fileNumberInRdm.add(stringObjectMap.get("id").toString());
            }
            List<Map<String, Object>> unexistPdmFiles = pdmFiles.stream().filter(m -> !fileNumberInRdm.contains(m.get("id").toString())).collect(Collectors.toList());
            //pdm交付物信息往rdm交付物列表也存一份
            if (unexistPdmFiles != null&& !unexistPdmFiles.isEmpty()) {
                unexistPdmFiles.forEach(m->{
                    m.put("productIds", "");
                    m.put("productNames", "");
                });
                xcmgProjectOtherDao.batchInsertPdmfile(unexistPdmFiles);
                pdmFilesInRdm.addAll(unexistPdmFiles);
            }
            for (Map<String, Object> pdmFile : pdmFiles) {
                String fileNumber = pdmFile.get("id").toString();
                for (Map<String, Object> stringObjectMap : pdmFilesInRdm) {
                    if (fileNumber.equalsIgnoreCase(stringObjectMap.get("id").toString())) {
                        pdmFile.put("productIds", stringObjectMap.get("productIds").toString());
                        pdmFile.put("productNames", stringObjectMap.get("productNames").toString());
                    }
                }
            }
            fileInfos.addAll(pdmFiles);
        }


        // 通过项目Id查询产品开发需求交付物
        //产品建议
        List<Map<String, Object>> cpjyFiles = fileUploadManager.queryCpjyByProjectId(projectId, null);
        if (cpjyFiles != null) {
            fileInfos.addAll(cpjyFiles);
        }
        //市场分析
        List<Map<String, Object>> scfxFiles = fileUploadManager.queryScfxByProjectId(projectId, null);
        if (scfxFiles != null) {
            fileInfos.addAll(scfxFiles);
        }
        return fileInfos;
    }

    // 返回显示用的文件审批状态
    private String toGetApprovalStatus(Map<String, Object> oneFile) {
        String approvalStatus = "";
        String isFolder = "0";
        if (oneFile.get("isFolder") != null) {
            isFolder = oneFile.get("isFolder").toString();
        }
        if ("1".equalsIgnoreCase(isFolder)) {
            return approvalStatus;
        }

        // 仅文件才有审批状态
        String isPDMFile = "";
        if (oneFile.get("isPDMFile") != null) {
            isPDMFile = oneFile.get("isPDMFile").toString();
        }
        if ("1".equalsIgnoreCase(isPDMFile)) {
            if (oneFile.get("approvalStatus") != null) {
                approvalStatus = "PDM(" + oneFile.get("approvalStatus").toString() + ")";
            } else {
                approvalStatus = "PDM(未知状态)";
            }
        } else {
            String approvalSolutionId = null;
            if (oneFile.get("approvalSolutionId") != null) {
                approvalSolutionId = oneFile.get("approvalSolutionId").toString();
            }
            // RDM中的文件且不需要审批
            if (StringUtils.isBlank(approvalSolutionId) || "pdmApprove".equalsIgnoreCase(approvalSolutionId)) {
                approvalStatus = "无需审批";
            } else {
                // RDM中的文件且需要审批
                if (oneFile.get("instStatus") == null) {
                    approvalStatus = "未提交";
                }
                if (oneFile.get("instStatus") != null
                        && "SUCCESS_END".equalsIgnoreCase(oneFile.get("instStatus").toString())) {
                    approvalStatus = "审批完成";
                }
                if (oneFile.get("instStatus") != null
                        && "RUNNING".equalsIgnoreCase(oneFile.get("instStatus").toString())) {
                    approvalStatus = "审批中(" + oneFile.get("currentProcessTask").toString() + ")";
                }
                if (oneFile.get("instStatus") != null
                        && "DISCARD_END".equalsIgnoreCase(oneFile.get("instStatus").toString())) {
                    approvalStatus = "审批作废";
                }
                if (oneFile.get("instStatus") != null
                        && !"SUCCESS_END".equalsIgnoreCase(oneFile.get("instStatus").toString())
                        && !"DISCARD_END".equalsIgnoreCase(oneFile.get("instStatus").toString())
                        && !"RUNNING".equalsIgnoreCase(oneFile.get("instStatus").toString())) {
                    approvalStatus = "未知状态";
                }

            }
        }
        return approvalStatus;
    }

    // 删除某个文件（从文件表和磁盘上都删除）
    @RequestMapping("deleteProjectFiles")
    public void deleteProjectFiles(HttpServletRequest request, HttpServletResponse response) {
        String projectId = RequestUtil.getString(request, "projectId");
        String id = RequestUtil.getString(request, "id");
        String relativeFilePath = RequestUtil.getString(request, "relativeFilePath");
        String fileName = RequestUtil.getString(request, "fileName");
        fileUploadManager.deleteProjectOneFile(projectId, id, relativeFilePath, fileName, false, ConstantUtil.DOWNLOAD);
        fileUploadManager.deleteProjectOneFile(projectId, id, relativeFilePath, fileName, false, ConstantUtil.PREVIEW);
    }

    @RequestMapping("xcmgDocMgr")
    public ModelAndView xcmgDocMgrPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "xcmgProjectManager/core/xcmgDocMgrList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUser", ContextUtil.getCurrentUser());
        IUser currentUser = ContextUtil.getCurrentUser();
        Map<String, Object> params = new HashMap<>();
        params.put("userId", currentUser.getUserId());
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        // 返回当前登录人角色信息
        List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params);
        JSONArray userRolesJsonArray = XcmgProjectUtil.convertListMap2JsonArrObject(currentUserRoles);
        mv.addObject("currentUserRoles", userRolesJsonArray);
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        return mv;
    }

    @RequestMapping("xcmgDocMgrList")
    @ResponseBody
    public List<Map<String, Object>> xcmgDocMgrList(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String tenantId = ContextUtil.getCurrentTenantId();
        String docName = RequestUtil.getString(request, "docName");
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", tenantId);
        if (StringUtils.isNotBlank(docName)) {
            params.put("fileName", docName);
        }
        List<Map<String, Object>> fileInfos = fileUploadManager.queryXcmgDocMgrList(params);
        // 格式化时间
        if (fileInfos != null && !fileInfos.isEmpty()) {
            for (Map<String, Object> oneFile : fileInfos) {
                if (oneFile.get("CREATE_TIME_") != null) {
                    oneFile.put("CREATE_TIME_",
                            DateUtil.formatDate((Date) oneFile.get("CREATE_TIME_"), "yyyy-MM-dd HH:mm:ss"));
                }
            }
        }
        return fileInfos;
    }

    @RequestMapping("xcmgDocMgrUploadWindow")
    public ModelAndView xcmgDocMgrUploadWindow(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String type = RequestUtil.getString(request, "type");
        String jspPath = "xcmgProjectManager/core/xcmgDocMgrUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUser", ContextUtil.getCurrentUser());
        return mv;
    }

    @RequestMapping(value = "xcmgDocMgrUpload")
    @ResponseBody
    public Map<String, Object> xcmgDocMgrUpload(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        try {
            // 先保存文件，成功后再写入数据库，前台是一个一个文件的调用
            fileUploadManager.saveCommonUploadFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            logger.error("Exception in upload", e);
            modelMap.put("success", "false");
        }
        return modelMap;
    }

    // 删除某个文件（从文件表和磁盘上都删除）
    @RequestMapping("xcmgDocMgrDelete")
    public void xcmgDocMgrDelete(HttpServletRequest request, HttpServletResponse response) {
        String id = RequestUtil.getString(request, "id");
        String relativeFilePath = RequestUtil.getString(request, "relativeFilePath");
        String fileName = RequestUtil.getString(request, "fileName");
        fileUploadManager.deleteProjectOneFile("", id, relativeFilePath, fileName, true, ConstantUtil.DOWNLOAD);
        fileUploadManager.deleteProjectOneFile("", id, relativeFilePath, fileName, true, ConstantUtil.PREVIEW);
    }

    // 项目管理文档的下载（预览pdf也调用这里）
    @RequestMapping("fileDownload")
    public ResponseEntity<byte[]> fileDownload(HttpServletRequest request, HttpServletResponse response) {
        try {
            String fileName = RequestUtil.getString(request, "fileName");
            if (StringUtils.isBlank(fileName)) {
                logger.error("操作失败，文件名为空！");
                return null;
            }
            // 项目还是公共
            String actionType = RequestUtil.getString(request, "actionType");
            // 预览还是下载
            String action = RequestUtil.getString(request, "action");
            if (StringUtils.isBlank(action)) {
                logger.error("操作类型为空");
                return null;
            }
            String fileBasePath = "";
            if ("project".equalsIgnoreCase(actionType)) {
                fileBasePath = ConstantUtil.PREVIEW.equalsIgnoreCase(action)
                        ? WebAppUtil.getProperty("projectFilePathBase_preview")
                        : WebAppUtil.getProperty("projectFilePathBase");
            } else if ("common".equalsIgnoreCase(actionType)) {
                fileBasePath =
                        ConstantUtil.PREVIEW.equalsIgnoreCase(action) ? WebAppUtil.getProperty("commonFilePathBase_preview")
                                : WebAppUtil.getProperty("commonFilePathBase");
            }
            if (StringUtils.isBlank(fileBasePath)) {
                logger.error("操作失败，找不到存储路径");
                return null;
            }

            String relativeFilePath = RequestUtil.getString(request, "relativeFilePath");
            if (StringUtils.isBlank(relativeFilePath)) {
                relativeFilePath = "";
            }
            String fullFilePath = fileBasePath + relativeFilePath + File.separator + fileName;
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

    @RequestMapping("officePreview")
    public void officePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        if (StringUtils.isBlank(fileName)) {
            logger.error("文档预览失败，文件名为空！");
            return;
        }
        String actionType = RequestUtil.getString(request, "actionType");
        String fileBasePath = "";
        if ("project".equalsIgnoreCase(actionType)) {
            fileBasePath = WebAppUtil.getProperty("projectFilePathBase_preview");
        } else if ("common".equalsIgnoreCase(actionType)) {
            fileBasePath = WebAppUtil.getProperty("commonFilePathBase_preview");
        }
        if (StringUtils.isBlank(fileBasePath)) {
            logger.error("文档预览失败，找不到存储路径");
            return;
        }
        String relativeFilePath = RequestUtil.getString(request, "relativeFilePath");
        if (StringUtils.isBlank(relativeFilePath)) {
            relativeFilePath = "";
        }
        String originalFilePath = fileBasePath + relativeFilePath + File.separator + fileName;
        String convertPdfDir = WebAppUtil.getProperty("convertPdfDir");
        String convertPdfPath = fileBasePath + relativeFilePath + File.separator + convertPdfDir + File.separator
                + OfficeDocPreview.generateConvertPdfFileName(fileName);
        OfficeDocPreview.previewOfficeDoc(originalFilePath, convertPdfPath, response);
    }

    @RequestMapping("imagePreview")
    @ResponseBody
    public void imagePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        if (StringUtils.isBlank(fileName)) {
            logger.error("图片预览失败，文件名为空！");
            return;
        }
        String actionType = RequestUtil.getString(request, "actionType");
        String fileBasePath = "";
        if ("project".equalsIgnoreCase(actionType)) {
            fileBasePath = WebAppUtil.getProperty("projectFilePathBase_preview");
        } else if ("common".equalsIgnoreCase(actionType)) {
            fileBasePath = WebAppUtil.getProperty("commonFilePathBase_preview");
        }
        if (StringUtils.isBlank(fileBasePath)) {
            logger.error("图片预览失败，找不到存储路径");
            return;
        }
        String relativeFilePath = RequestUtil.getString(request, "relativeFilePath");
        if (StringUtils.isBlank(relativeFilePath)) {
            relativeFilePath = "";
        }
        String originalFilePath = fileBasePath + relativeFilePath + File.separator + fileName;
        OfficeDocPreview.imagePreview(originalFilePath, response);
    }
}
