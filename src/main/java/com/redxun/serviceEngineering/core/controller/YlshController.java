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

import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmZhgl.core.service.MonthWorkService;
import com.redxun.serviceEngineering.core.dao.YlshDao;
import com.redxun.serviceEngineering.core.service.YlshManager;
import com.redxun.sys.org.manager.OsUserManager;
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
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

/**
 * 语料库审核
 *
 * @mh 2022年6月011日08:49:56
 */

@Controller
@RequestMapping("/serviceEngineering/core/ylsh/")
public class YlshController {
    private static final Logger logger = LoggerFactory.getLogger(YlshController.class);

    @Autowired
    private YlshManager ylshManager;
    @Resource
    private BpmInstManager bpmInstManager;
    @Autowired
    private CommonBpmManager commonBpmManager;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private YlshDao ylshDao;
    @Resource
    private MonthWorkService monthWorkService;
    @Autowired
    private CommonInfoManager commonInfoManager;

    @RequestMapping("applyListPage")
    public ModelAndView getListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String ylshList = "serviceEngineering/core/ylshList.jsp";
        ModelAndView mv = new ModelAndView(ylshList);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        return mv;
    }

    @RequestMapping("applyList")
    @ResponseBody
    public JsonPageResult<?> queryApplyList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return ylshManager.queryApplyList(request, true);
    }

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
            return ylshManager.delApplys(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteApply", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("applyEditPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/ylshEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        // 新增或者编辑页面由流程引擎跳转并替换url中的pk和nodeId_
        String applyId = RequestUtil.getString(request, "applyId", "");
        String nodeId = RequestUtil.getString(request, "nodeId", "");
        String action = RequestUtil.getString(request, "action", "");
        String instId = RequestUtil.getString(request, "instId", "");
        String status = RequestUtil.getString(request, "status", "");
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
            List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId, "FYYLKSXGD", null);
            if (nodeVars != null && !nodeVars.isEmpty()) {
                mv.addObject("nodeVars", JSONObject.toJSONString(nodeVars));
            }
        }
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        mv.addObject("applyId", applyId);
        mv.addObject("instId", instId);
        mv.addObject("action", action);
        mv.addObject("status", status);
        //取后市场多语言翻译人员
        List<Map<String, String>> groupUsers =
                commonInfoManager.queryUserByGroupNameAndRelType("后市场多语言翻译人员", "GROUP-USER-BELONG");
        List<JSONObject> checkUsers = new ArrayList<>();
        for (Map<String, String> groupUser : groupUsers) {
            JSONObject checkUser = new JSONObject();
            checkUser.put("key",groupUser.get("USER_ID_"));
            checkUser.put("value",groupUser.get("FULLNAME_"));
            checkUsers.add(checkUser);
        }
        mv.addObject("checkUsers", checkUsers);
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
            result.put("transApplyId", "临时");
            return result;
        }
        return ylshManager.queryApplyDetail(id);
    }

    @RequestMapping("upload")
    @ResponseBody
    public Map<String, Object> upload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        try {
            // 先保存文件到磁盘，成功后再写入数据库，前台是一个一个文件的调用

            String fType = RequestUtil.getString(request, "fType", "");
            if (StringUtils.isBlank(fType)) {
                ylshManager.saveUploadFiles(request);
            } else if ("ret".equalsIgnoreCase(fType)) {
                // 回传文件上传时是更新明细表
                ylshManager.saveReturnFiles(request);
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
        String jspPath = "serviceEngineering/core/ylshFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String applyId = RequestUtil.getString(request, "applyId", "");
        String fType = RequestUtil.getString(request, "fType", "");
        mv.addObject("currentUser", ContextUtil.getCurrentUser());
        mv.addObject("applyId", applyId);
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
            String fileBasePath = WebAppUtil.getProperty("ylshFilePathBase");
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

    @RequestMapping("zipFileDownload")
    public void zipFileDownload(HttpServletRequest request, HttpServletResponse response) {
        try {
            // 多文件打包下载成zip 以Form表单提交，各字段间以逗号分隔
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
            String fileBasePath = WebAppUtil.getProperty("ylshFilePathBase");
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
                String downFile = XcmgProjectUtil.getNowUTCDateStr("yyyyMMddHHmmss") + "-ylsh.zip";
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

        String fileBasePath = WebAppUtil.getProperty("ylshFilePathBase");
        rdmZhglFileManager.deleteOneFileFromDisk(fileId, fileName, formId, fileBasePath);
        // 清除文件后,将表单中的文件信息置空
        JSONObject param = new JSONObject();
        param.put("id", formId);
        param.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        param.put("UPDATE_TIME_", new Date());
        ylshDao.deleteDemand(param);

    }

    @RequestMapping("/preview")
    public ResponseEntity<byte[]> filePreview(HttpServletRequest request, HttpServletResponse response) {
        ResponseEntity<byte[]> result = null;
        String fileType = RequestUtil.getString(request, "fileType");
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("ylshFilePathBase");
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
        if (StringUtils.isBlank(applyId)) {
            logger.error("applyId is blank");
            return null;
        }
        JSONObject params = new JSONObject();
        params.put("id", applyId);
        List<JSONObject> demandList = ylshManager.queryDemandList(params);

        return demandList;
    }

    @RequestMapping("saveInProcess")
    @ResponseBody
    public JsonResult saveInProcess(HttpServletRequest request, @RequestBody String data, HttpServletResponse response)
            throws Exception {
        JsonResult result = new JsonResult(true, "保存成功");
        if (StringUtils.isBlank(data)) {
            logger.warn("requestBody is blank");
            result.setSuccess(false);
            result.setMessage("表单为空，保存失败！");
            return result;
        }
        ylshManager.saveInProcess(result, data);
        return result;
    }

    @RequestMapping("exportData")
    public void exportData(HttpServletRequest request, HttpServletResponse response) {
        ylshManager.exportData(request, response);
    }

    //..处理sdltm文件拷贝
    @RequestMapping("getSdltmFromTranslationToYlsh")
    @ResponseBody
    public JsonResult getSdltmFromTranslationToYlsh(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JsonResult result = new JsonResult(true, "复制成功");
        String translationId = RequestUtil.getString(request, "translationId");
        String applyId = RequestUtil.getString(request, "applyId");
        String fileId = RequestUtil.getString(request, "fileId");
        ylshManager.getSdltmFromTranslationToYlsh(result, translationId, applyId, fileId);
        return result;
    }

    //..
    @RequestMapping("copyBusiness")
    @ResponseBody
    public JsonResult copyBusiness(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String businessId = RequestUtil.getString(request, "businessId");
            return ylshManager.copyBusiness(businessId);
        } catch (Exception e) {
            logger.error("Exception in copyBusiness", e);
            return new JsonResult(false, e.getMessage());
        }
    }
}
