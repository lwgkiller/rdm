package com.redxun.serviceEngineering.core.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;
import java.util.zip.ZipOutputStream;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.rdmZhgl.core.service.MonthWorkService;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.serviceEngineering.core.dao.ManualTopicDao;
import com.redxun.serviceEngineering.core.service.ManualTopicManager;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.serviceEngineering.core.dao.ZxdpsDao;
import com.redxun.serviceEngineering.core.service.ZxdpsManager;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

/**
 * 操保手册Toipc
 * 
 * @mh 2022年8月8日08:52:13
 */
@Controller
@RequestMapping("/serviceEngineering/core/topic/")
public class ManualTopicController {
    private static final Logger logger = LoggerFactory.getLogger(ManualTopicController.class);

    @Autowired
    private ManualTopicManager manualTopicManager;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private ManualTopicDao manualTopicDao;
    @Resource
    MonthWorkService monthWorkService;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;


    @RequestMapping("applyListPage")
    public ModelAndView getListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String manualTopicList = "serviceEngineering/core/manualTopicList.jsp";
        ModelAndView mv = new ModelAndView(manualTopicList);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        // @mh  2022年8月19日14:34:39 增加角色权限，只有该角色可以在后续编辑
        boolean isAdmin = rdmZhglUtil.judgeIsPointRoleUser(ContextUtil.getCurrentUserId(), "操保手册Topic管理员");
        mv.addObject("isAdmin", isAdmin);
        return mv;
    }

    @RequestMapping("applyList")
    @ResponseBody
    public JsonPageResult<?> queryApplyList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return manualTopicManager.queryApplyList(request, true);
    }

    @RequestMapping("deleteApply")
    @ResponseBody
    public JsonResult deleteApply(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return manualTopicManager.delApplys(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteApply", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("confirmApply")
    @ResponseBody
    public JsonResult confirmApply(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return manualTopicManager.confirmApplys(ids);
        } catch (Exception e) {
            logger.error("Exception in confirmApply", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("confirmApplyCancel")
    @ResponseBody
    public JsonResult confirmApplyCancel(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return manualTopicManager.confirmApplysCancel(ids);
        } catch (Exception e) {
            logger.error("Exception in confirmApplyCancel", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("updateVersion")
    @ResponseBody
    public JsonResult updateVersion(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String id = RequestUtil.getString(request, "id");
            return manualTopicManager.updateVersion(id);
        } catch (Exception e) {
            logger.error("Exception in updateVersion", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("applyEditPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/manualTopicEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String applyId = RequestUtil.getString(request, "applyId");
        String action = RequestUtil.getString(request, "action");
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentUserDeptName", ContextUtil.getCurrentUser().getMainGroupName());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        mv.addObject("applyId", applyId);
        mv.addObject("action", action);



        return mv;
    }

    @RequestMapping("getJson")
    @ResponseBody
    public JSONObject queryApplyDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = RequestUtil.getString(request, "id", "");
        if (StringUtils.isBlank(id)) {
            JSONObject result = new JSONObject();
            result.put("CREATE_BY_", ContextUtil.getCurrentUser().getUserId());
            result.put("creatorName", ContextUtil.getCurrentUser().getFullname());
            result.put("departName", ContextUtil.getCurrentUser().getMainGroupName());
            result.put("applyTime", DateFormatUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
            return result;
        }
        return manualTopicManager.queryApplyDetail(id);
    }

    // 文件相关

    @RequestMapping("upload")
    @ResponseBody
    public Map<String, Object> upload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        try {
            // 先保存文件到磁盘，成功后再写入数据库，前台是一个一个文件的调用
            manualTopicManager.saveUploadFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            logger.error("Exception in upload", e);
            modelMap.put("success", "false");
        }
        return modelMap;
    }

    // @RequestMapping("openFileWindow")
    // @ResponseBody
    // public ModelAndView openFileWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
    //// String jspPath = "serviceEngineering/core/zxdpsFileUpload.jsp";;
    // String jspPath = "world/core/worldFitnessFileUpload.jsp";
    // ModelAndView mv = new ModelAndView(jspPath);
    // String applyId = RequestUtil.getString(request, "applyId");
    // String fileType = RequestUtil.getString(request, "fileType");
    // String canEdit = RequestUtil.getString(request, "canEdit");
    // String action = RequestUtil.getString(request, "action");
    //
    // mv.addObject("action", action);
    // mv.addObject("applyId", applyId);
    // mv.addObject("fileType", fileType);
    // mv.addObject("canEdit", canEdit);
    // mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
    // mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
    //
    // return mv;
    // }

    @RequestMapping("openUploadWindow")
    public ModelAndView openUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/manualTopicFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUser", ContextUtil.getCurrentUser());
        return mv;
    }

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
                logger.error("操作失败，文件主键为空！");
                return null;
            }
            String formId = RequestUtil.getString(request, "formId");
            String fileBasePath = WebAppUtil.getProperty("manualTopicFilePathBase");
            if (StringUtils.isBlank(fileBasePath)) {
                logger.error("操作失败，找不到存储根路径");
                return null;
            }
            String suffix = CommonFuns.toGetFileSuffix(fileName);
            String relativeFilePath = "";
            if (StringUtils.isNotBlank(formId)) {
                relativeFilePath = File.separator + formId;
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

    // 清除文件
    @RequestMapping("deleteFiles")
    public void deleteFiles(HttpServletRequest request, HttpServletResponse response, @RequestBody String postBody) {
        JSONObject postBodyObj = JSONObject.parseObject(postBody);
        String fileId = postBodyObj.getString("id");
        if (StringUtils.isBlank(fileId)) {
            return;
        }
        String fileName = postBodyObj.getString("fileName");
        String formId = postBodyObj.getString("formId");

        String fileBasePath = WebAppUtil.getProperty("manualTopicFilePathBase");
        rdmZhglFileManager.deleteOneFileFromDisk(fileId, fileName, formId, fileBasePath);
        // 清除文件后,将表单中的文件信息置空
        JSONObject param = new JSONObject();
        param.put("id", fileId);
        manualTopicDao.deleteFile(param);

    }

    @RequestMapping("/preview")
    public ResponseEntity<byte[]> filePreview(HttpServletRequest request, HttpServletResponse response) {
        ResponseEntity<byte[]> result = null;
        String fileType = RequestUtil.getString(request, "fileType");
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("manualTopicFilePathBase");
        switch (fileType) {
            case "pdf":
                result = rdmZhglFileManager.pdfPreviewOrDownLoad(fileName, fileId, formId, fileBasePath);
            case "office":
                rdmZhglFileManager.officeFilePreview(fileName, fileId, formId, fileBasePath, response);
                break;
            case "pic":
                rdmZhglFileManager.imageFilePreview(fileName, fileId, formId, fileBasePath, response);
                break;
        }
        return result;
    }

    @RequestMapping("demandList")
    @ResponseBody
    public List<JSONObject> demandList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String applyId = RequestUtil.getString(request, "applyId", "");
        String detailId = RequestUtil.getString(request, "detailId", "");
        if (StringUtils.isBlank(applyId)) {
            logger.error("applyId is blank");
            return null;
        }
        JSONObject params = new JSONObject();
        params.put("applyId", applyId);
        params.put("detailId", detailId);
        List<JSONObject> demandList = manualTopicManager.queryDemandList(params);

        return demandList;
    }

    @RequestMapping("standardList")
    @ResponseBody
    public List<JSONObject> standardList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String applyId = RequestUtil.getString(request, "applyId", "");
        if (StringUtils.isBlank(applyId)) {
            logger.error("applyId is blank");
            return null;
        }
        JSONObject params = new JSONObject();
        params.put("applyId", applyId);
        List<JSONObject> standardList = manualTopicManager.queryStandardList(params);

        return standardList;
    }

    @RequestMapping("benchmarkingList")
    @ResponseBody
    public List<JSONObject> benchmarkingList(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        String applyId = RequestUtil.getString(request, "applyId", "");
        if (StringUtils.isBlank(applyId)) {
            logger.error("applyId is blank");
            return null;
        }
        JSONObject params = new JSONObject();
        params.put("applyId", applyId);
        List<JSONObject> benchmarkingList = manualTopicManager.queryBenchmarkingList(params);

        return benchmarkingList;
    }

    @RequestMapping("/checkFileList")
    @ResponseBody
    public List<JSONObject> checkFileList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String detailId = RequestUtil.getString(request, "detailId", "");
        if (StringUtils.isBlank(detailId)) {
            logger.error("detailId is blank");
            return null;
        }
        JSONObject params = new JSONObject();
        params.put("detailId", detailId);
        List<JSONObject> fileList = manualTopicManager.checkFileList(params);
        return fileList;
    }

    @RequestMapping("saveBusiness")
    @ResponseBody
    public JsonResult saveBusiness(HttpServletRequest request, @RequestBody String formDataStr,
        HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "");
        if (StringUtils.isBlank(formDataStr)) {
            logger.warn("requestBody is blank");
            result.setSuccess(false);
            result.setMessage("requestBody is blank");
            return result;
        }
        try {
            JSONObject formDataJson = JSONObject.parseObject(formDataStr);
            if (formDataJson == null || formDataJson.isEmpty()) {
                logger.warn("formData is blank");
                result.setSuccess(false);
                result.setMessage("requestBody is blank");
                return result;
            }
            if (StringUtils.isBlank(formDataJson.getString("id"))) {
                String id = manualTopicManager.createTopic(formDataJson);
                result.setData(id);
            } else {
                manualTopicManager.updateTopic(formDataJson);
                result.setData(formDataJson.getString("id"));
            }
        } catch (Exception e) {
            logger.error("Exception in save");
            result.setSuccess(false);
            result.setMessage("Exception in save");
            return result;
        }
        return result;
    }

    @RequestMapping("exportData")
    public void exportData(HttpServletRequest request, HttpServletResponse response) {
        manualTopicManager.exportData(request, response);
    }

    @RequestMapping("zipFileDownload")

    public void zipFileDownload(HttpServletRequest request, HttpServletResponse response) {
        try {
            // 多文件打包下载成zip 以Form表单提交，各字段间以逗号分隔
            // String fileNameStr = RequestUtil.getString(request, "fileName");
            // String fileIdStr = RequestUtil.getString(request, "fileId");
            String formIdStr = RequestUtil.getString(request, "formId");
            if (StringUtils.isEmpty(formIdStr)) {
                return;
            }
            // 这里根据topic的id 找到各topic的所有文件 fileId fileName

            String[] ids = formIdStr.split(",");
            // String[] fileIds = fileIdStr.split(",");
            // String[] fileNames = fileNameStr.split(",");
            List<JSONObject> filePathList = new ArrayList<>();
            JSONObject pathObj = new JSONObject();
            String fileBasePath = WebAppUtil.getProperty("manualTopicFilePathBase");

            for (int i = 0; i < ids.length; i++) {
                // 拼接路径
                JSONObject params = new JSONObject();
                params.put("applyId", ids[i]);
                List<JSONObject> fileList = manualTopicManager.checkFileList(params);
                for (JSONObject oneFile : fileList) {

                    String fileId = oneFile.getString("id");
                    String fileName = oneFile.getString("fileName");
                    String id = oneFile.getString("applyId");
                    String suffix = CommonFuns.toGetFileSuffix(fileName);
                    String relativeFilePath = "";
                    if (StringUtils.isNotBlank(id)) {
                        relativeFilePath = File.separator + id;
                    }
                    String realFileName = fileId + "." + suffix;
                    String fullFilePath = fileBasePath + relativeFilePath + File.separator + realFileName;
                    pathObj = new JSONObject();
                    pathObj.put("path", fullFilePath);
                    pathObj.put("fileName", fileName);
                    filePathList.add(pathObj);
                }

            }
//            //这里要进行路径去重，文件名一样的话就会报错
//            HashSet set = new LinkedHashSet(filePathList);
//            filePathList.addAll(set);

            if (filePathList.size() != 0) {
                // 创建临时路径，存放压缩文件
                String downFile = XcmgProjectUtil.getNowUTCDateStr("yyyyMMddHHmmss") + "-Topic.zip";
                String zipFilePath = fileBasePath + File.separator + downFile;
                ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFilePath));
                for (JSONObject path : filePathList) {
                    monthWorkService.fileToZip(path.getString("path"), zipOutputStream, path.getString("fileName"));
                }
                // 压缩完成后，关闭压缩流
                zipOutputStream.close();

                String fileName = new String(downFile.getBytes("UTF-8"), "ISO8859-1");
                response.setHeader("Content-Disposition", "attachment;filename=" + fileName);

                ServletOutputStream outputStream = response.getOutputStream();
                FileInputStream inputStream = new FileInputStream(zipFilePath);

                IOUtils.copy(inputStream, outputStream);
                inputStream.close();
                outputStream.close(); // 关闭流

                File fileTempZip = new File(zipFilePath);
                fileTempZip.delete();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
