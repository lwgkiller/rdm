package com.redxun.serviceEngineering.core.controller;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmZhgl.core.service.MonthWorkService;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.serviceEngineering.core.dao.ExternalTranslationDao;
import com.redxun.serviceEngineering.core.service.ExternalTranslationManager;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;
import java.util.zip.ZipOutputStream;

/**
 * 外发翻译
 *
 * @mh 2022年4月25日17:42:22
 */
@Controller
@RequestMapping("/serviceEngineering/core/externalTranslation/")
public class ExternalTranslationController {
    private static final Logger logger = LoggerFactory.getLogger(ExternalTranslationController.class);
    @Autowired
    private CommonBpmManager commonBpmManager;
    @Autowired
    private ExternalTranslationManager externalTranslationManager;
    @Resource
    private BpmInstManager bpmInstManager;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private ExternalTranslationDao externalTranslationDao;
    @Autowired
    private MonthWorkService monthWorkService;

    @RequestMapping("applyListPage")
    public ModelAndView getListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String applyList = "serviceEngineering/core/externalTranslationList.jsp";
        ModelAndView mv = new ModelAndView(applyList);
        Map<String, Object> params = new HashMap<>();
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        params.put("userId", ContextUtil.getCurrentUserId());
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        return mv;
    }

    @RequestMapping("applyList")
    @ResponseBody
    public JsonPageResult<?> queryApplyList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return externalTranslationManager.queryApplyList(request, true);
    }

    // 目前仅允许删除单条请求
    @RequestMapping("deleteApply")
    @ResponseBody
    public JsonResult deleteApply(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String instIdStr = RequestUtil.getString(request, "instIds");
            if (StringUtils.isNotBlank(instIdStr)) {
                String[] instIds = instIdStr.split(",");
                for (int index = 0; index < instIds.length; index++) {
                    bpmInstManager.deleteCascade(instIds[index], "");
                }
            }
            String[] ids = uIdStr.split(",");
            return externalTranslationManager.delApplys(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteApply", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("applyEditPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/externalTranslationEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        // 新增或者编辑页面由流程引擎跳转并替换url中的pk和nodeId_
        String applyId = RequestUtil.getString(request, "applyId", "");
        String nodeId = RequestUtil.getString(request, "nodeId", "");
        String baseInfoId = RequestUtil.getString(request, "baseInfoId", "");
        String action = RequestUtil.getString(request, "action", "");
        String status = RequestUtil.getString(request, "status", "");
        String instId = RequestUtil.getString(request, "instId", "");

        if (StringUtils.isBlank(action)) {
            // 新增或编辑的时候没有nodeId
            if (StringUtils.isBlank(nodeId) || nodeId.contains("PROCESS")) {
                action = "edit";
            } else {
                // 处理任务的时候有nodeId
                action = "task";
            }
        }
        // 取出节点变量，返回到页面
        if (StringUtils.isNotBlank(nodeId)) {
            List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId, "WFFY", null);
            if (nodeVars != null && !nodeVars.isEmpty()) {
                mv.addObject("nodeVars", JSONObject.toJSONString(nodeVars));
            }
        }
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        mv.addObject("applyId", applyId);
        mv.addObject("baseInfoId", baseInfoId);
        mv.addObject("action", action);
        mv.addObject("status", status);
        mv.addObject("instId", instId);
        return mv;
    }

    // 获取父表【翻译申请】信息
    @RequestMapping("getJson")
    @ResponseBody
    public JSONObject queryApplyDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = RequestUtil.getString(request, "id", "");
        String baseInfoId = RequestUtil.getString(request, "baseInfoId", "");
        // 如果初次创建，从父表直接查，如果id已存在，通过baseInfoId查父表
        if (StringUtils.isBlank(id)) {
            JSONObject result = new JSONObject();
            if (StringUtils.isNotBlank(baseInfoId)) {
                JSONObject param = new JSONObject();
                param.put("baseInfoId", baseInfoId);
                result = externalTranslationDao.queryBaseInfo(param);
                result.put("baseInfoId", result.getString("id"));
                result.put("id", "");
                result.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                result.put("creatorName", ContextUtil.getCurrentUser().getFullname());
                if (result == null) {
                    result = new JSONObject();
                    result.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    result.put("creatorName", ContextUtil.getCurrentUser().getFullname());
                }
            }

            return result;
        }
        return externalTranslationManager.queryApplyDetail(id);
    }

    @RequestMapping("demandList")
    @ResponseBody
    public List<JSONObject> demandList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String applyId = RequestUtil.getString(request, "applyId", "");
        if (StringUtils.isBlank(applyId)) {
            logger.error("applyId is blank");
            return null;
        }
        JSONObject params = new JSONObject();
        params.put("applyId", applyId);
        List<JSONObject> demandList = externalTranslationManager.queryDemandList(params);
        for (JSONObject oneApply : demandList) {
            if (oneApply.get("returnFileTime") != null) {
                oneApply.put("returnFileTime",
                        DateUtil.formatDate((Date) oneApply.get("returnFileTime"), "yyyy-MM-dd HH:mm"));
            }
        }
        return demandList;
    }

    @RequestMapping("upload")
    @ResponseBody
    public Map<String, Object> upload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        try {
            // 先保存文件到磁盘，成功后再写入数据库，前台是一个一个文件的调用
            String fType = RequestUtil.getString(request, "fType", "");
            if (StringUtils.isBlank(fType)) {
                externalTranslationManager.saveUploadFiles(request);
            } else if ("ret".equalsIgnoreCase(fType)) {
                // 回传文件上传时是更新明细表
                externalTranslationManager.saveReturnFiles(request);
            }
            modelMap.put("success", "true");
        } catch (Exception e) {
            logger.error("Exception in upload", e);
            modelMap.put("success", "false");
        }
        return modelMap;
    }

    @RequestMapping("openUploadWindow")
    public ModelAndView openUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/externalTranslationFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String applyId = RequestUtil.getString(request, "applyId", "");
        String detailId = RequestUtil.getString(request, "detailId", "");
        // 区分外翻及回传 回传fType=ret
        String fType = RequestUtil.getString(request, "fType", "");
        mv.addObject("currentUser", ContextUtil.getCurrentUser());
        mv.addObject("applyId", applyId);
        mv.addObject("detailId", detailId);
        mv.addObject("fType", fType);
        return mv;
    }

    @RequestMapping("buchongUploadWindow")
    public ModelAndView buchongUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/externalTranslationFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String applyId = RequestUtil.getString(request, "applyId", "");
        String detailId = RequestUtil.getString(request, "detailId", "");
        // 区分外翻及回传 回传fType=ret
        String fType = RequestUtil.getString(request, "fType", "");
        mv.addObject("currentUser", ContextUtil.getCurrentUser());
        mv.addObject("applyId", applyId);
        mv.addObject("detailId", detailId);
        mv.addObject("fType", fType);
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
            String fileBasePath = WebAppUtil.getProperty("externalTranslationFilePathBase");
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

    //清除回传文件
    @RequestMapping("deleteFiles")
    public void deleteFiles(HttpServletRequest request, HttpServletResponse response, @RequestBody String postBody) {
        JSONObject postBodyObj = JSONObject.parseObject(postBody);
        String fileId = postBodyObj.getString("id");
        if (StringUtils.isBlank(fileId)) {
            return;
        }
        String fileName = postBodyObj.getString("fileName");
        String formId = postBodyObj.getString("formId");

        String fileBasePath = WebAppUtil.getProperty("externalTranslationFilePathBase");
        rdmZhglFileManager.deleteOneFileFromDisk(fileId, fileName, formId, fileBasePath);
        // 清除文件后,将表单中的文件信息置空
        JSONObject param = new JSONObject();
        param.put("updateFileId", fileId);
        param.put("returnFileName", "");
        param.put("returnFileId", "");
        param.put("returnFileTime", null);
        param.put("returnDesc", "");
        //externalTranslationDao.updateDemand(param);
        //todo:用updateDemandReturnFile替换
        externalTranslationDao.updateReturnFile(param);
    }

    //删除某个需求记录和全部文件文件
    @RequestMapping("deleteOriFiles")
    @ResponseBody
    public JsonResult deleteOriFiles(HttpServletRequest request, HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "操作成功！");
        try {
            String demandId = RequestUtil.getString(request, "demandId");
            JSONObject params = new JSONObject();
            params.put("demandId", demandId);
            JSONObject demand = externalTranslationDao.queryDemand(params);
            if (demand != null) {
                String fileBasePath = WebAppUtil.getProperty("externalTranslationFilePathBase");
                String formId = demand.getString("applyId");
                //原始
                String fileId = demand.getString("outFileId");
                String fileName = demand.getString("outFileName");
                rdmZhglFileManager.deleteOneFileFromDisk(fileId, fileName, formId, fileBasePath);
                //外发
                fileId = demand.getString("returnFileId");
                fileName = demand.getString("returnFileName");
                rdmZhglFileManager.deleteOneFileFromDisk(fileId, fileName, formId, fileBasePath);
            }
            params.clear();
            List<String> ids = new ArrayList<>();
            ids.add(demandId);
            params.put("ids", ids);
            externalTranslationDao.deleteDemand(params);
            return result;
        } catch (Exception e) {
            logger.error("Exception in delete", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("/preview")
    public ResponseEntity<byte[]> filePreview(HttpServletRequest request, HttpServletResponse response) {
        ResponseEntity<byte[]> result = null;
        String fileType = RequestUtil.getString(request, "fileType");
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("externalTranslationFilePathBase");
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

    //..新页面，只显示外发翻译文件
    @RequestMapping("externalTranslationFileListPage")
    public ModelAndView externalTranslationFileListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/externalTranslationFileList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        return mv;
    }

    //..新页面，只显示外发翻译文件
    @RequestMapping("externalTranslationFileListQuery")
    @ResponseBody
    public JsonPageResult<?> externalTranslationFileListQuery(HttpServletRequest request, HttpServletResponse response) {
        return externalTranslationManager.externalTranslationFileListQuery(request, true);
    }

    //..新功能，保存需求信息
    @RequestMapping(value = "saveDemand", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult saveDemand(HttpServletRequest request, @RequestBody String businessDataStr,
                                 HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "保存成功");
        externalTranslationManager.saveDemand(result, businessDataStr);
        return result;
    }

    //..新功能，导出外发翻译文件属性信息，关联翻译主流程编号
    @RequestMapping("exportFileListInfo")
    public void exportFileListInfo(HttpServletRequest request, HttpServletResponse response) {
        externalTranslationManager.exportFileListInfo(request, response);
    }

    //..新功能，打包导出外发文件
    @RequestMapping("zipFileDownload")
    public void zipFileDownload(HttpServletRequest request, HttpServletResponse response) {
        try {
            //多文件打包下载成zip 以Form表单提交，各字段间以逗号分隔
            String fileNameStr = RequestUtil.getString(request, "fileName");
            String fileIdStr = RequestUtil.getString(request, "fileId");
            String formIdStr = RequestUtil.getString(request, "formId");
            if (StringUtils.isEmpty(formIdStr) || StringUtils.isEmpty(fileIdStr) || StringUtils.isEmpty(fileNameStr)) {
                return;
            }
            String[] ids = formIdStr.split(",");
            String[] fileIds = fileIdStr.split(",");
            String[] fileNames = fileNameStr.split(",");
            List<JSONObject> filePathList = new ArrayList<>();
            JSONObject pathObj = new JSONObject();
            String fileBasePath = WebAppUtil.getProperty("externalTranslationFilePathBase");
            for (int i = 0; i < ids.length; i++) {
                // 拼接路径
                String fileId = fileIds[i];
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
                String downFile = XcmgProjectUtil.getNowUTCDateStr("yyyyMMddHHmmss") + ".zip";
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

    //..新功能，保存相关翻译流程的信息
    @RequestMapping(value = "update4Attribute", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult update4Attribute(HttpServletRequest request, @RequestBody String DataStr,
                                     HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "保存成功");
        externalTranslationManager.update4Attribute(result, DataStr);
        return result;
    }
}
