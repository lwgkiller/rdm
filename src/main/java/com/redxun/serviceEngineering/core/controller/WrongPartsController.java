package com.redxun.serviceEngineering.core.controller;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.OfficeDocPreview;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.serviceEngineering.core.service.WrongPartsService;
import com.redxun.sys.org.entity.OsGroup;
import com.redxun.sys.org.manager.OsGroupManager;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
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
import org.springframework.web.bind.annotation.PostMapping;
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
@RequestMapping("/serviceEngineering/core/wrongParts")
public class WrongPartsController {
    private static final Logger logger = LoggerFactory.getLogger(WrongPartsController.class);
    @Autowired
    private WrongPartsService wrongPartsService;
    @Autowired
    private CommonBpmManager commonBpmManager;
    @Resource
    private BpmInstManager bpmInstManager;
    @Autowired
    private OsGroupManager osGroupManager;

    //..
    @RequestMapping("dataListPage")
    public ModelAndView dataListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/wrongPartsList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        List<JSONObject> nodeSetListWithName =
                commonBpmManager.getNodeSetListWithName("CJZG", "userTask", "endEvent");
        mv.addObject("nodeSetListWithName", nodeSetListWithName);
        return mv;
    }

    //..
    @RequestMapping("dataListQuery")
    @ResponseBody
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) {
        return wrongPartsService.dataListQuery(request, response, true);
    }

    //..
    @RequestMapping("deleteData")
    @ResponseBody
    public JsonResult deleteData(HttpServletRequest request, HttpServletResponse response) throws Exception {
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
            return wrongPartsService.deleteData(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteData", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    //..作废
    @RequestMapping("importTemplateDownload")
    public ResponseEntity<byte[]> importTemplateDownload(HttpServletRequest request, HttpServletResponse response) {
        return wrongPartsService.importTemplateDownload();
    }

    //..作废
    @RequestMapping("importExcel")
    @ResponseBody
    public JSONObject importExcel(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        wrongPartsService.importExcel(result, request);
        return result;
    }

    //..
    @RequestMapping("editPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/wrongPartsEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        // 新增或者编辑页面由流程引擎跳转并替换url中的pk和nodeId_
        String cjzgId = RequestUtil.getString(request, "cjzgId");
        String nodeId = RequestUtil.getString(request, "nodeId");
        String action = RequestUtil.getString(request, "action");
        String taskStatus = RequestUtil.getString(request, "taskStatus");
        if (StringUtils.isBlank(action)) {
            // 新增或编辑的时候没有nodeId
            if (StringUtils.isBlank(nodeId) || nodeId.contains("PROCESS")) {
                action = "edit";
            } else {
                // 处理任务的时候有nodeId
                action = "task";
            }
        }
        mv.addObject("cjzgId", cjzgId).addObject("action", action).addObject("taskStatus", taskStatus);
        // 取出节点变量，返回到页面
        if (StringUtils.isNotBlank(nodeId)) {
            mv.addObject("nodeId", nodeId);
            List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId, "CJZG", null);
            if (nodeVars != null && !nodeVars.isEmpty()) {
                mv.addObject("nodeVars", JSONObject.toJSONString(nodeVars));
            }
        }
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        boolean isFwgcjswdzy = wrongPartsService.isFwgcjswdzy();//当前用户是否是服务工程技术文档专员
        mv.addObject("isFwgcjswdzy", isFwgcjswdzy);
        List<OsGroup> groups = osGroupManager.getByDepName(RdmConst.FWGCS_NAME);
        mv.addObject("fwgcsId", groups.get(0).getGroupId());//服务工程技术研究所的id
        return mv;
    }

    //..
    @RequestMapping("getCjzgDetail")
    @ResponseBody
    public JSONObject getCjzgDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject obj = new JSONObject();
        String cjzgId = RequestUtil.getString(request, "cjzgId");
        if (StringUtils.isNotBlank(cjzgId)) {
            obj = wrongPartsService.getCjzgDetail(cjzgId);
        }
        return obj;
    }

    //..
    @RequestMapping("saveCjzg")
    @ResponseBody
    public JsonResult saveCjzg(HttpServletRequest request, @RequestBody String formData, HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "");
        if (StringUtils.isBlank(formData)) {
            logger.warn("requestBody is blank");
            result.setSuccess(false);
            result.setMessage("requestBody is blank");
            return result;
        }
        try {
            JSONObject formDataJson = JSONObject.parseObject(formData);
            if (formDataJson == null || formDataJson.isEmpty()) {
                logger.warn("formData is blank");
                result.setSuccess(false);
                result.setMessage("requestBody is blank");
                return result;
            }
            if (StringUtils.isBlank(formDataJson.getString("id"))) {
                wrongPartsService.createCjzg(formDataJson);
            } else {
                wrongPartsService.updateCjzg(formDataJson);
            }
        } catch (Exception e) {
            logger.error("Exception in save cjzg");
            result.setSuccess(false);
            result.setMessage("Exception in save cjzg");
            return result;
        }
        return result;
    }

    //..
    @RequestMapping("openCjzgUploadWindow")
    public ModelAndView fileUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/wrongPartsFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("cjzgId", RequestUtil.getString(request, "cjzgId"));
        // 查询附件类型
        return mv;
    }

    //..
    @RequestMapping(value = "cjzgUpload")
    @ResponseBody
    public Map<String, Object> upload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        try {
            // 先保存文件到磁盘，成功后再写入数据库，前台是一个一个文件的调用
            wrongPartsService.saveUploadFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            logger.error("Exception in upload", e);
            modelMap.put("success", "false");
        }
        return modelMap;
    }

    //..
    @RequestMapping("cjzgFileList")
    @ResponseBody
    public List<JSONObject> dataList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String cjzgId = RequestUtil.getString(request, "cjzgId");
        if (StringUtils.isBlank(cjzgId)) {
            logger.error("cjzgId is blank");
            return null;
        }
        Map<String, Object> params = new HashMap<>();
        List<String> cjzgIds = new ArrayList<>();
        cjzgIds.add(cjzgId);
        params.put("wrongPartsIds", cjzgIds);
        List<JSONObject> fileInfos = wrongPartsService.queryCjzgFileList(params);
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

    //..
    @RequestMapping("delCjzgUploadFile")
    public void delete(HttpServletRequest request, HttpServletResponse response, @RequestBody String postBody) {
        JSONObject postBodyObj = JSONObject.parseObject(postBody);
        String fileName = postBodyObj.getString("fileName");
        String id = postBodyObj.getString("id");
        String cjzgId = postBodyObj.getString("formId");
        wrongPartsService.delCjzgUploadFile(id, fileName, cjzgId);
    }

    //..
    @RequestMapping("cjzgFileDownload")
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
            String cjzgId = RequestUtil.getString(request, "formId");
            if (StringUtils.isBlank(cjzgId)) {
                logger.error("操作失败，错件整改Id为空！");
                return null;
            }
            // 预览还是下载
            // String action = RequestUtil.getString(request, "action");
            // if (StringUtils.isBlank(action)) {
            // logger.error("操作类型为空");
            // return null;
            // }
            String fileBasePath = WebAppUtil.getProperty("cjzgFilePathBase");
            if (StringUtils.isBlank(fileBasePath)) {
                logger.error("操作失败，找不到存储路径");
                return null;
            }
            String suffix = CommonFuns.toGetFileSuffix(fileName);
            String fullFilePath = fileBasePath + File.separator + cjzgId + File.separator + fileId + "." + suffix;
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

    //..
    @RequestMapping("cjzgOfficePreview")
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
        String cjzgId = RequestUtil.getString(request, "formId");
        if (StringUtils.isBlank(cjzgId)) {
            logger.error("操作失败，错件整改Id为空！");
            return;
        }

        String fileBasePath = WebAppUtil.getProperty("cjzgFilePathBase");
        if (StringUtils.isBlank(fileBasePath)) {
            logger.error("操作失败，找不到存储路径");
            return;
        }
        String suffix = CommonFuns.toGetFileSuffix(fileName);
        String originalFilePath = fileBasePath + File.separator + cjzgId + File.separator + fileId + "." + suffix;
        String convertPdfDir = WebAppUtil.getProperty("convertPdfDir");
        String convertPdfPath =
                fileBasePath + File.separator + cjzgId + File.separator + convertPdfDir + File.separator + fileId + ".pdf";
        ;
        OfficeDocPreview.previewOfficeDoc(originalFilePath, convertPdfPath, response);
    }

    //..
    @RequestMapping("cjzgImagePreview")
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
        String cjzgId = RequestUtil.getString(request, "formId");
        if (StringUtils.isBlank(cjzgId)) {
            logger.error("操作失败，组件Id为空！");
            return;
        }

        String fileBasePath = WebAppUtil.getProperty("cjzgFilePathBase");
        if (StringUtils.isBlank(fileBasePath)) {
            logger.error("操作失败，找不到存储路径");
            return;
        }
        String suffix = CommonFuns.toGetFileSuffix(fileName);
        String originalFilePath = fileBasePath + File.separator + cjzgId + File.separator + fileId + "." + suffix;
        OfficeDocPreview.imagePreview(originalFilePath, response);
    }

    //..
    @PostMapping("exportWrongParts")
    public void exportWrongParts(HttpServletResponse response, HttpServletRequest request) {
        wrongPartsService.exportWrongParts(request, response);
    }
}
