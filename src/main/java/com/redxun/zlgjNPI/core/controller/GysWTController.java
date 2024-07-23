package com.redxun.zlgjNPI.core.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import com.redxun.zlgjNPI.core.dao.GysWTDao;
import com.redxun.zlgjNPI.core.manager.GysWTService;

@RestController
@RequestMapping("/zlgjNPI/core/gyswt/")
public class GysWTController {
    private Logger logger = LogManager.getLogger(GysWTController.class);
    @Resource
    private GysWTService gysWTService;
    @Resource
    private GysWTDao gysWTDao;
    @Autowired
    private CommonBpmManager commonBpmManager;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Resource
    private BpmInstManager bpmInstManager;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;

    @RequestMapping("listPage")
    public ModelAndView listPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "zlgjNPI/gysWtList.jsp";
        String type = RequestUtil.getString(request, "type", "");
        String bigTypeName = type.equalsIgnoreCase("WX") ? "外协件" : "外购件";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("type", bigTypeName);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        // 查询物料大类对应的物料小类
        List<JSONObject> smallTypeList = gysWTService.querySmallTypeByBig(bigTypeName);
        mv.addObject("smallTypeList", smallTypeList);
        return mv;
    }

    @RequestMapping("getZlgjList")
    @ResponseBody
    public JsonPageResult<?> getZlgjList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> params = new HashMap<>();
        return gysWTService.getZlgjList(request, response, true, params, true);
    }

    @RequestMapping("editPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "zlgjNPI/gysWtEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String wtId = RequestUtil.getString(request, "wtId");
        String nodeId = RequestUtil.getString(request, "nodeId");
        String action = RequestUtil.getString(request, "action");
        String status = RequestUtil.getString(request, "status");
        String type = RequestUtil.getString(request, "type");
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
            List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId, "GYSWTGJ", null);
            if (nodeVars != null && !nodeVars.isEmpty()) {
                mv.addObject("nodeVars", JSONObject.toJSONString(nodeVars));
            }
        }
        mv.addObject("wtId", wtId).addObject("action", action).addObject("status", status);
        mv.addObject("type", type);
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        // 查询物料大类对应的物料小类
        List<JSONObject> smallTypeList = gysWTService.querySmallTypeByBig(type);
        mv.addObject("smallTypeList", smallTypeList);
        return mv;
    }

    // 通过id获取详情
    @RequestMapping("getZlgjDetail")
    @ResponseBody
    public JSONObject getZlgjDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject zlgjObj = null;
        String wtId = RequestUtil.getString(request, "wtId");
        if (StringUtils.isNotBlank(wtId)) {
            zlgjObj = gysWTService.getZlgjDetail(wtId);
        }
        if (zlgjObj == null) {
            zlgjObj = new JSONObject();
            String type = RequestUtil.getString(request, "type");
            zlgjObj.put("bigTypeName", type);
        }
        return zlgjObj;
    }

    // 暂存信息
    @RequestMapping("saveZlgj")
    @ResponseBody
    public JsonResult saveZlgj(HttpServletRequest request, @RequestBody String zlgjStr, HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "");
        if (StringUtils.isBlank(zlgjStr)) {
            logger.warn("requestBody is blank");
            result.setSuccess(false);
            result.setMessage("requestBody is blank");
            return result;
        }
        try {
            JSONObject formDataJson = JSONObject.parseObject(zlgjStr);
            if (formDataJson == null || formDataJson.isEmpty()) {
                logger.warn("formData is blank");
                result.setSuccess(false);
                result.setMessage("requestBody is blank");
                return result;
            }
            if (StringUtils.isBlank(formDataJson.getString("wtId"))) {
                gysWTService.createZlgj(formDataJson);
            } else {
                gysWTService.updateZlgj(formDataJson);
            }
            result.setData(formDataJson.getString("wtId"));
        } catch (Exception e) {
            logger.error("Exception in save zlgj");
            result.setSuccess(false);
            result.setMessage("Exception in save zlgj");
            return result;
        }
        return result;
    }

    @RequestMapping("zlgjFileWindow")
    public ModelAndView zlgjFileWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "zlgjNPI/gysWtFileList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String wtId = RequestUtil.getString(request, "wtId");
        String canOperateFile = RequestUtil.getString(request, "canOperateFile");
        String coverContent = RequestUtil.getString(request, "coverContent");
        String faId = RequestUtil.getString(request, "faId");
        mv.addObject("coverContent", coverContent);
        mv.addObject("wtId", wtId);
        mv.addObject("canOperateFile", canOperateFile);
        mv.addObject("faId", faId);
        return mv;
    }

    @RequestMapping("files")
    @ResponseBody
    public List<JSONObject> getFiles(HttpServletRequest request, HttpServletResponse response) {
        List<JSONObject> fileInfos = gysWTService.getFileListByTypeId(request);
        return fileInfos;
    }

    @RequestMapping("openUploadWindow")
    public ModelAndView openUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "zlgjNPI/gysWtUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        return mv;
    }

    @RequestMapping(value = "upload")
    @ResponseBody
    public Map<String, Object> upload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>(16);
        try {
            // 先保存文件到磁盘，成功后再写入数据库，前台是一个一个文件的调用
            gysWTService.saveUploadFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            logger.error("Exception in upload gysWt", e);
        }
        return modelMap;
    }

    @RequestMapping("preview")
    public ResponseEntity<byte[]> zlgjPdfPreview(HttpServletRequest request, HttpServletResponse response) {
        ResponseEntity<byte[]> result = null;
        String fileType = RequestUtil.getString(request, "fileType");
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("zlgjFilePathBase");
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

    @RequestMapping("fileDownload")
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
            String mainId = RequestUtil.getString(request, "mainId");
            String fileBasePath = WebAppUtil.getProperty("zlgjFilePathBase");
            if (StringUtils.isBlank(fileBasePath)) {
                logger.error("操作失败，找不到存储根路径");
                return null;
            }
            String suffix = CommonFuns.toGetFileSuffix(fileName);
            String realFileName = fileId + "." + suffix;
            String fullFilePath = fileBasePath + File.separator + mainId + File.separator + realFileName;
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

    @RequestMapping("deleteFiles")
    public void deleteFiles(HttpServletRequest request, HttpServletResponse response) {
        String mainId = RequestUtil.getString(request, "wtId");
        String id = RequestUtil.getString(request, "id");
        String fileName = RequestUtil.getString(request, "fileName");
        String suffix = CommonFuns.toGetFileSuffix(fileName);
        gysWTService.deleteFileOnDisk(mainId, id, suffix);
        Map<String, Object> fileParams = new HashMap<>(16);
        List<String> fileIds = new ArrayList<>();
        fileIds.add(id);
        fileParams.put("fileIds", fileIds);
        gysWTDao.deleteFileByIds(fileParams);
    }

    @RequestMapping("deleteZlgj")
    @ResponseBody
    public JsonResult deleteZlgj(HttpServletRequest request, HttpServletResponse response) throws Exception {
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
            return gysWTService.deleteZlgj(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteZlgj", e);
            return new JsonResult(false, e.getMessage());
        }
    }

}
