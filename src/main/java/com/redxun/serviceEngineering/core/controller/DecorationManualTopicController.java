package com.redxun.serviceEngineering.core.controller;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmZhgl.core.service.MonthWorkService;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.serviceEngineering.core.service.DecorationManualTopicService;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;
import java.util.zip.ZipOutputStream;

@Controller
@RequestMapping("/serviceEngineering/core/decorationManualTopic")
public class DecorationManualTopicController {
    private static final Logger logger = LoggerFactory.getLogger(DecorationManualTopicController.class);
    @Autowired
    DecorationManualTopicService decorationManualTopicService;
    @Autowired
    SysDicManager sysDicManager;
    @Autowired
    RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    MonthWorkService monthWorkService;

    //..
    @RequestMapping("dataListPage")
    public ModelAndView dataListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/decorationManualTopicList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        String decorationManualTopicAdmin = sysDicManager.getBySysTreeKeyAndDicKey(
                "serviceEngineeringGroups", "decorationManualTopicAdmin").getValue();
        mv.addObject("decorationManualTopicAdmin", decorationManualTopicAdmin);
        return mv;
    }

    //..
    @RequestMapping("dataListQuery")
    @ResponseBody
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) {
        return decorationManualTopicService.dataListQuery(request, response);
    }

    //..
    @RequestMapping("zipFileDownload")
    public void zipFileDownload(HttpServletRequest request, HttpServletResponse response) {
        try {
            // 多文件打包下载成zip 以Form表单提交，各字段间以逗号分隔
            String formIdStr = RequestUtil.getString(request, "formId");
            if (StringUtils.isEmpty(formIdStr)) {
                return;
            }
            //这里根据topic的id找到各topic的所有文件 fileId fileName
            String[] ids = formIdStr.split(",");
            List<JSONObject> filePathList = new ArrayList<>();
            JSONObject pathObj = new JSONObject();
            String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                    "serviceEngineeringUploadPosition", "decorationManualTopic").getValue();
            List<String> businessIdList = Arrays.asList(ids);
            List<JSONObject> fileList = decorationManualTopicService.getFileList(businessIdList);
            for (JSONObject oneFile : fileList) {
                String fileId = oneFile.getString("id");
                String fileName = oneFile.getString("fileName");
                String id = oneFile.getString("mainId");
                String suffix = CommonFuns.toGetFileSuffix(fileName);
                String relativeFilePath = "";
                if (StringUtils.isNotBlank(id)) {
                    relativeFilePath = File.separator + id;
                }
                String realFileName = fileId + "." + suffix;
                String fullFilePath = filePathBase + relativeFilePath + File.separator + realFileName;
                pathObj = new JSONObject();
                pathObj.put("path", fullFilePath);
                pathObj.put("fileName", fileName);
                filePathList.add(pathObj);
            }
            //这里要进行路径去重，文件名一样的话就会报错
            HashSet set = new LinkedHashSet(filePathList);
            filePathList.clear();
            filePathList.addAll(set);
            if (filePathList.size() != 0) {
                //创建临时路径，存放压缩文件
                String downFile = XcmgProjectUtil.getNowUTCDateStr("yyyyMMddHHmmss") + "-Topic.zip";
                String zipFilePath = filePathBase + File.separator + downFile;
                ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFilePath));
                for (JSONObject path : filePathList) {
                    monthWorkService.fileToZip(path.getString("path"), zipOutputStream, path.getString("fileName"));
                }
                //压缩完成后，关闭压缩流
                zipOutputStream.close();
                String fileName = new String(downFile.getBytes("UTF-8"), "ISO8859-1");
                response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
                ServletOutputStream outputStream = response.getOutputStream();
                FileInputStream inputStream = new FileInputStream(zipFilePath);
                IOUtils.copy(inputStream, outputStream);
                inputStream.close();
                outputStream.close();//关闭流
                File fileTempZip = new File(zipFilePath);
                fileTempZip.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //..
    @RequestMapping("editPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "serviceEngineering/core/decorationManualTopicEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String id = RequestUtil.getString(request, "id");
        String action = RequestUtil.getString(request, "action");
        JSONObject obj = new JSONObject();
        if (StringUtils.isNotBlank(id)) {
            obj = decorationManualTopicService.queryDataById(id);
        }
        mv.addObject("obj", obj).addObject("businessId", obj.getString("id"));
        mv.addObject("action", action);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        if (action.equalsIgnoreCase("copy")) {
            mv.addObject("businessId", "");
        }
        return mv;
    }

    //..
    @RequestMapping("deleteBusiness")
    @ResponseBody
    public JsonResult deleteBusiness(HttpServletRequest request, @RequestBody String requestBody,
                                     HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "删除成功");
        JSONObject requestBodyObj = JSONObject.parseObject(requestBody);
        String id = requestBodyObj.getString("id");
        try {
            decorationManualTopicService.deleteBusiness(id);
        } catch (Exception e) {
            logger.error("Exception in delete");
            result.setSuccess(false);
            result.setMessage("Exception in delete");
        }
        return result;
    }

    //..
    @RequestMapping("releaseBusiness")
    @ResponseBody
    public JsonResult releaseBusiness(HttpServletRequest request, @RequestBody String requestBody,
                                      HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "发布成功");
        JSONObject requestBodyObj = JSONObject.parseObject(requestBody);
        String id = requestBodyObj.getString("id");
        try {
            decorationManualTopicService.doReleaseBusiness(id);
        } catch (Exception e) {
            logger.error("Exception in release");
            result.setSuccess(false);
            result.setMessage("Exception in release");
        }
        return result;
    }

    //..
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
                String id = decorationManualTopicService.addBusiness(formDataJson);
                result.setData(id);
                result.setMessage("添加成功");
            } else {
                decorationManualTopicService.saveBusiness(formDataJson);
                result.setData(formDataJson.getString("id"));
                result.setMessage("保存成功");
            }
        } catch (Exception e) {
            logger.error("Exception in save");
            result.setSuccess(false);
            result.setMessage("Exception in save");
            return result;
        }
        return result;
    }

    //..
    @RequestMapping("exportList")
    public void exportExcel(HttpServletRequest request, HttpServletResponse response) {
        decorationManualTopicService.exportList(request, response);
    }

    //..
    @RequestMapping("fileUploadWindow")
    public ModelAndView fileUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/decorationManualTopicFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("businessId", RequestUtil.getString(request, "businessId", ""));
        return mv;
    }

    //..
    @RequestMapping(value = "fileUpload")
    @ResponseBody
    public Map<String, Object> fileUpload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        try {
            // 先保存文件，成功后再写入数据库，前台是一个一个文件的调用
            decorationManualTopicService.saveUploadFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            logger.error("Exception in upload", e);
            modelMap.put("success", "false");
        }
        return modelMap;
    }

    //..
    @RequestMapping("getFileList")
    @ResponseBody
    public List<JSONObject> getFileList(HttpServletRequest request, HttpServletResponse response) {
        List<String> businessIdList = Arrays.asList(RequestUtil.getString(request, "businessId", ""));
        return decorationManualTopicService.getFileList(businessIdList);
    }

    //..
    @RequestMapping("delFile")
    public void delFile(HttpServletRequest request, HttpServletResponse response, @RequestBody String postBody) {
        try {
            JSONObject postBodyObj = JSONObject.parseObject(postBody);
            String fileName = postBodyObj.getString("fileName");
            String fileId = postBodyObj.getString("id");
            String businessId = postBodyObj.getString("formId");
            decorationManualTopicService.deleteOneBusinessFile(fileId, fileName, businessId);
        } catch (Exception e) {
            logger.error("Exception in delFile");
        }
    }

    //..
    @RequestMapping("PdfPreviewAndAllDownload")
    public ResponseEntity<byte[]> PdfPreviewAndAllDownload(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "serviceEngineeringUploadPosition", "decorationManualTopic").getValue();
        return rdmZhglFileManager.pdfPreviewOrDownLoad(fileName, fileId, formId, filePathBase);
    }

    //..
    @RequestMapping("PdfPreview")
    public ResponseEntity<byte[]> PdfPreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "serviceEngineeringUploadPosition", "decorationManualTopic").getValue();
        return rdmZhglFileManager.pdfPreviewOrDownLoad(fileName, fileId, formId, filePathBase);
    }

    //..
    @RequestMapping("OfficePreview")
    @ResponseBody
    public void OfficePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "serviceEngineeringUploadPosition", "decorationManualTopic").getValue();
        rdmZhglFileManager.officeFilePreview(fileName, fileId, formId, filePathBase, response);
    }

    //..
    @RequestMapping("ImagePreview")
    @ResponseBody
    public void ImagePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "serviceEngineeringUploadPosition", "decorationManualTopic").getValue();
        rdmZhglFileManager.imageFilePreview(fileName, fileId, formId, filePathBase, response);
    }
}
