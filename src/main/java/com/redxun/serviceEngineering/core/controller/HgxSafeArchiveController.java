package com.redxun.serviceEngineering.core.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipOutputStream;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.core.json.JsonResult;
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
import com.redxun.core.json.JsonPageResult;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmZhgl.core.service.MonthWorkService;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.serviceEngineering.core.service.HgxSafeArchiveService;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

@Controller
@RequestMapping("/serviceEngineering/core/safeArchive")
public class HgxSafeArchiveController {
    private static final Logger logger = LoggerFactory.getLogger(HgxSafeArchiveController.class);
    @Autowired
    HgxSafeArchiveService hgxSafeArchiveService;

    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;

    @Autowired
    private SysDicManager sysDicManager;

    @Resource
    MonthWorkService monthWorkService;

    // ..
    @RequestMapping("dataListPage")
    public ModelAndView dataListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/hgxSafeArchiveList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        return mv;
    }

    // ..
    @RequestMapping("dataListQuery")
    @ResponseBody
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) {
        return hgxSafeArchiveService.dataListQuery(request, response);
    }

    // ..
    @RequestMapping("Preview")
    public void Preview(HttpServletRequest request, HttpServletResponse response) {
        hgxSafeArchiveService.Preview(request, response);
    }

    @RequestMapping("/preview")
    public ResponseEntity<byte[]> filePreview(HttpServletRequest request, HttpServletResponse response) {
        ResponseEntity<byte[]> result = null;
        String fileType = RequestUtil.getString(request, "fileType");
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath =
                sysDicManager.getBySysTreeKeyAndDicKey("serviceEngineeringUploadPosition", "hgxSafeArchive").getValue();
//        String fileBasePath = WebAppUtil.getProperty("worldFitnessImproveFilePathBase");
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
//            String fileBasePath = WebAppUtil.getProperty("worldFitnessImproveFilePathBase");
            String fileBasePath =
                    sysDicManager.getBySysTreeKeyAndDicKey("serviceEngineeringUploadPosition", "hgxSafeArchive").getValue();

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

    // ..
    @RequestMapping("Download")
    public ResponseEntity<byte[]> Download(HttpServletRequest request, HttpServletResponse response) {
        String id = RequestUtil.getString(request, "id");
        String description = RequestUtil.getString(request, "description");
        return hgxSafeArchiveService.Download(request, id, description);
    }

    // ..
    @RequestMapping("EditPage")
    public ModelAndView EditPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "serviceEngineering/core/hgxSafeArchiveEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String id = RequestUtil.getString(request, "id");
        String action = RequestUtil.getString(request, "action");
        JSONObject obj = new JSONObject();
        if (StringUtils.isNotBlank(id)) {
            obj = hgxSafeArchiveService.queryDataById(id);
        }
        obj.put("creatorName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("obj", obj).addObject("businessId", obj.getString("id"));
        mv.addObject("action", action);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        return mv;
    }

    // ..
    @RequestMapping("deleteBusiness")
    @ResponseBody
    public JSONObject deleteBusiness(HttpServletRequest request, @RequestBody String requestBody,
        HttpServletResponse response) {
        JSONObject result = new JSONObject();
        JSONObject requestBodyObj = JSONObject.parseObject(requestBody);
        String id = requestBodyObj.getString("id");
        String fileName = requestBodyObj.getString("fileName");
        hgxSafeArchiveService.deleteBusiness(result, id, fileName);
        return result;
    }

    // ..
    @RequestMapping("saveBusiness")
    @ResponseBody
    public JSONObject saveBusiness(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        hgxSafeArchiveService.saveBusiness(result, request);
        return result;
    }

    @RequestMapping("exportData")
    public void exportData(HttpServletRequest request, HttpServletResponse response) {
        hgxSafeArchiveService.exportData(request, response);
    }

    @RequestMapping("zipFileDownload")

    public void zipFileDownload(HttpServletRequest request, HttpServletResponse response) {
        try {
            // 多文件打包下载成zip 以Form表单提交，各字段间以逗号分隔
             String fileNameStr = RequestUtil.getString(request, "fileName");
            String formIdStr = RequestUtil.getString(request, "formId");
            if (StringUtils.isEmpty(formIdStr)) {
                return;
            }
            // 这里根据topic的id 找到各topic的所有文件 fileId fileName

            String[] ids = formIdStr.split(",");
             String[] fileNames = fileNameStr.split(",");
            List<JSONObject> filePathList = new ArrayList<>();
            JSONObject pathObj = new JSONObject();
//            String fileBasePath = WebAppUtil.getProperty("manualTopicFilePathBase");
            String fileBasePath =
                    sysDicManager.getBySysTreeKeyAndDicKey("serviceEngineeringUploadPosition", "hgxSafeArchive").getValue();

            for (int i = 0; i < ids.length; i++) {
                // 拼接路径

                String fileId = ids[i];
                String fileName = fileNames[i];
                String id = ids[i];
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

            if (filePathList.size() != 0) {
                // 创建临时路径，存放压缩文件
                String downFile = XcmgProjectUtil.getNowUTCDateStr("yyyyMMddHHmmss") + "-SafeInfo.zip";
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

    @RequestMapping("topicList")
    @ResponseBody
    public List<JSONObject> topicList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String applyId = RequestUtil.getString(request, "applyId", "");
        if (StringUtils.isBlank(applyId)) {
            logger.error("applyId is blank");
            return null;
        }
        JSONObject params = new JSONObject();
        params.put("applyId", applyId);
        List<JSONObject> topicList = hgxSafeArchiveService.queryTopicList(params);
        return topicList;
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
        List<JSONObject> standardList = hgxSafeArchiveService.queryStandardList(params);
        return standardList;
    }

    @RequestMapping("updateVersion")
    @ResponseBody
    public JsonResult updateVersion(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String id = RequestUtil.getString(request, "id");
            return hgxSafeArchiveService.updateVersion(id);
        } catch (Exception e) {
            logger.error("Exception in updateVersion", e);
            return new JsonResult(false, e.getMessage());
        }
    }

}
