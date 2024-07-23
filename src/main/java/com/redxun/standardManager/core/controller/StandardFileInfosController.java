
package com.redxun.standardManager.core.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.manager.MybatisBaseManager;
import com.redxun.core.util.OfficeDocPreview;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.controller.MybatisListController;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.standardManager.core.dao.StandardFileInfosDao;
import com.redxun.standardManager.core.manager.StandardFileInfosManager;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.standardManager.core.util.StandardConstant;
import com.redxun.standardManager.core.util.StandardManagerUtil;
import com.redxun.sys.core.util.OpenOfficeUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.util.ConstantUtil;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 附件相关操作
 *
 * @author zz
 */
@Controller
@RequestMapping("/standardManager/core/standardFileInfos/")
public class StandardFileInfosController extends MybatisListController {
    @Autowired
    private StandardFileInfosManager standardFileInfosManager;
    @Autowired
    private StandardFileInfosDao standardFileInfosDao;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;

    @SuppressWarnings("rawtypes")
    @Override
    public MybatisBaseManager getBaseManager() {
        return null;
    }

    @RequestMapping("openUploadWindow")
    public ModelAndView openUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "standardManager/core/standardFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String standardType = RequestUtil.getString(request, "standardType");
        mv.addObject("currentUser", ContextUtil.getCurrentUser());
        mv.addObject("standardType", standardType);
        return mv;
    }

    @RequestMapping(value = "upload")
    @ResponseBody
    public Map<String, Object> upload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        try {
            // 先保存文件到磁盘，成功后再写入数据库，前台是一个一个文件的调用
            standardFileInfosManager.saveUploadFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            logger.error("Exception in upload", e);
            modelMap.put("success", "false");
        }
        return modelMap;
    }

    /**
     * 查询文件
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("getFileList")
    @ResponseBody
    public JSONArray getFileList(HttpServletRequest request, HttpServletResponse response) {
        JSONArray fileInfoArray = standardFileInfosManager.getFiles(request);
        return fileInfoArray;
    }

    // 项目管理文档的下载（预览pdf格式的文件也调用这里）
    @RequestMapping("fileDownload")
    public ResponseEntity<byte[]> fileDownload(HttpServletRequest request, HttpServletResponse response) {
        try {
            String fileName = RequestUtil.getString(request, "fileName");
            if (StringUtils.isBlank(fileName)) {
                logger.error("操作失败，文件名为空！");
                return null;
            }
            // 预览还是下载，取的根路径不一样
            String action = RequestUtil.getString(request, "action");
            if (StringUtils.isBlank(action)) {
                logger.error("操作类型为空");
                return null;
            }
            String fileId = RequestUtil.getString(request, "fileId");
            if (StringUtils.isBlank(fileId)) {
                logger.error("操作失败，文件主键为空！");
                return null;
            }
            String standardId = RequestUtil.getString(request, "standardId");
            String fileBasePath = "";
            fileBasePath =
                ConstantUtil.PREVIEW.equalsIgnoreCase(action) ? WebAppUtil.getProperty("standardAttachFilePath_preview")
                    : WebAppUtil.getProperty("standardAttachFilePath");
            if (StringUtils.isBlank(fileBasePath)) {
                logger.error("操作失败，找不到存储根路径");
                return null;
            }
            String suffix = CommonFuns.toGetFileSuffix(fileName);
            String relativeFilePath = "";
            if (StringUtils.isNotBlank(standardId)) {
                relativeFilePath = File.separator + standardId;
            }
            String realFileName = fileId + "." + suffix;
            String fullFilePath = fileBasePath + relativeFilePath + File.separator + realFileName;
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
        String fileId = RequestUtil.getString(request, "fileId");
        if (StringUtils.isBlank(fileId)) {
            logger.error("操作失败，文件主键为空！");
            return;
        }
        String standardId = RequestUtil.getString(request, "standardId");
        String fileBasePath = "";
        fileBasePath = WebAppUtil.getProperty("standardAttachFilePath_preview");
        if (StringUtils.isBlank(fileBasePath)) {
            logger.error("文档预览失败，找不到存储路径");
            return;
        }
        String suffix = CommonFuns.toGetFileSuffix(fileName);
        String relativeFilePath = "";
        if (StringUtils.isNotBlank(standardId)) {
            relativeFilePath = File.separator + standardId;
        }
        String realFileName = fileId + "." + suffix;
        String originalFilePath = fileBasePath + relativeFilePath + File.separator + realFileName;
        String convertPdfDir = WebAppUtil.getProperty("convertPdfDir");
        String convertPdfPath = fileBasePath + relativeFilePath + File.separator + convertPdfDir + File.separator
            + OpenOfficeUtil.generateConvertPdfFileName(realFileName);
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
        String fileId = RequestUtil.getString(request, "fileId");
        if (StringUtils.isBlank(fileId)) {
            logger.error("操作失败，文件主键为空！");
            return;
        }
        String standardId = RequestUtil.getString(request, "standardId");
        String fileBasePath = "";
        fileBasePath = WebAppUtil.getProperty("standardAttachFilePath_preview");
        if (StringUtils.isBlank(fileBasePath)) {
            logger.error("图片预览失败，找不到存储路径");
            return;
        }
        String suffix = CommonFuns.toGetFileSuffix(fileName);
        String relativeFilePath = "";
        if (StringUtils.isNotBlank(standardId)) {
            relativeFilePath = File.separator + standardId;
        }
        String realFileName = fileId + "." + suffix;
        String originalFilePath = fileBasePath + relativeFilePath + File.separator + realFileName;
        OfficeDocPreview.imagePreview(originalFilePath, response);
    }

    // 删除某个文件（从文件表和磁盘上都删除）
    @RequestMapping("deleteStandardFiles")
    public void deleteStandardFiles(HttpServletRequest request, HttpServletResponse response) {
        String standardId = RequestUtil.getString(request, "standardId");
        String id = RequestUtil.getString(request, "id");
        String fileName = RequestUtil.getString(request, "fileName");
        String suffix = CommonFuns.toGetFileSuffix(fileName);
        standardFileInfosManager.deleteOneStandardFileOnDisk(standardId, id, suffix);
        Map<String, Object> fileParams = new HashMap<>(16);
        List<String> fileIds = new ArrayList<>();
        fileIds.add(id);
        fileParams.put("fileIds", fileIds);
        standardFileInfosDao.deleteFileByIds(fileParams);
    }

    @RequestMapping("workPlanFileWindow")
    public ModelAndView workPlanFileWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "xcmgProjectManager/core/projectWorkPlanFileList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String projectId = RequestUtil.getString(request, "projectId");
        String workPlanId = RequestUtil.getString(request, "workPlanId");
        String coverContent = RequestUtil.getString(request, "coverContent");
        String projectAction = RequestUtil.getString(request, "action");
        String canOperateFile = RequestUtil.getString(request, "canOperateFile");
        mv.addObject("coverContent", coverContent);
        mv.addObject("projectAction", projectAction);
        mv.addObject("projectId", projectId);
        mv.addObject("workPlanId", workPlanId);
        mv.addObject("canOperateFile", canOperateFile);
        return mv;
    }

    @RequestMapping("attachFileListWindow")
    public ModelAndView attachFileListWindow(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        String jspPath = "standardManager/core/standardAttachFileList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String standardId = RequestUtil.getString(request, "standardId");
        String coverContent = RequestUtil.getString(request, "coverContent");
        String canOperateFile = RequestUtil.getString(request, "canOperateFile");
        String standardType = RequestUtil.getString(request, "standardType");
        String action = RequestUtil.getString(request, "action");
        String processId = RequestUtil.getString(request, "processId");
        // 角色信息
        Map<String, Object> params = new HashMap<>();
        params.put("userId",  ContextUtil.getCurrentUserId());
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params);
        JSONArray userRolesJsonArray = XcmgProjectUtil.convertListMap2JsonArrObject(currentUserRoles);
        mv.addObject("currentUserRoles", userRolesJsonArray);
        mv.addObject("coverContent", coverContent);
        mv.addObject("standardType", standardType);
        mv.addObject("standardId", standardId);
        mv.addObject("canOperateFile", canOperateFile);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("action", action);
        mv.addObject("processId", processId);
        return mv;
    }

    @RequestMapping("attachFilePage")
    public ModelAndView attachFilePage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "standardManager/core/standardAttachFilePage.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        String webappName = WebAppUtil.getProperty("webappName", "rdm");
        String systemCategoryValue = StandardConstant.SYSTEMCATEGORY_GL;
        if ("rdm".equalsIgnoreCase(webappName) && !StandardManagerUtil.judgeGLNetwork(request)) {
            systemCategoryValue = StandardConstant.SYSTEMCATEGORY_JS;
        }
        mv.addObject("systemCategoryValue", systemCategoryValue);
        return mv;
    }

    @RequestMapping("getAttachFileGridList")
    @ResponseBody
    public JsonPageResult<?> getAttachFileGridList(HttpServletRequest request, HttpServletResponse response) {
        return standardFileInfosManager.getAttachFileGridList(request, true);
    }

    @RequestMapping("exportAttachFileGrid")
    public void exportAttachFileGrid(HttpServletRequest request, HttpServletResponse response) {
        standardFileInfosManager.exportAttachFileGrid(request, response);
    }

    @RequestMapping("updateAttachFileStatus")
    public void updateAttachFileStatus(HttpServletRequest request, @RequestBody JSONObject formData, HttpServletResponse response) {
        if (formData != null || !formData.isEmpty()) {
            standardFileInfosManager.updateAttachFileStatus(formData);
        }
    }

}
