package com.redxun.rdmZhgl.core.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmZhgl.core.dao.ZlglmkDao;
import com.redxun.rdmZhgl.core.service.ZlglmkService;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.controller.GenericController;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.sys.core.util.SysPropertiesUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.util.ConstantUtil;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import com.redxun.xcmgjssjk.core.service.JssjkFileManager;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
 * 专利管理模块
 */
@Controller
@RequestMapping("/zhgl/core/zlgl/")
public class ZlglmkController extends GenericController {
    private static final Logger logger = LoggerFactory.getLogger(HyglInternalController.class);
    @Autowired
    private ZlglmkService zlglmkService;
    @Autowired
    private ZlglmkDao zlglmkDao;
    @Autowired
    private JssjkFileManager jssjkFileManager;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;

    @RequestMapping("zgzlListPage")
    public ModelAndView zgzlListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/zlglZgzlList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        Map<String, Object> params = new HashMap<>();
        params.put("userId", ContextUtil.getCurrentUserId());
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        // 返回当前登录人角色信息
        List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params);
        JSONArray userRolesJsonArray = XcmgProjectUtil.convertListMap2JsonArrObject(currentUserRoles);
        mv.addObject("currentUserRoles", userRolesJsonArray);
        String lastUpdateTime = SysPropertiesUtil.getGlobalProperty("ZLTZ_LAST_TIME");
        mv.addObject("lastUpdateTime", lastUpdateTime);
        return mv;
    }

    @RequestMapping("queryListData")
    @ResponseBody
    public JsonPageResult<?> queryListData(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return zlglmkService.queryListData(request, response, true);
    }

    /*
     *进去中国专利编辑页面
      */
    @RequestMapping("zgzlPage")
    public ModelAndView zgzlPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/zlglZgzlEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String zgzlId = RequestUtil.getString(request, "zgzlId");
        String action = RequestUtil.getString(request, "action");
        mv.addObject("action", action);
        Map<String, Object> zgzlObj = new HashMap<>();
        if (StringUtils.isNotEmpty(zgzlId)) {
            mv.addObject(zgzlId, zgzlId);
            Map<String, Object> params = new HashMap<>();
            params.put("zgzlId", zgzlId);
            List<Map<String, Object>> zgzlValue = zlglmkDao.queryZgzlId(params);
            if (zgzlValue != null && !zgzlValue.isEmpty()) {
                zgzlObj = zgzlValue.get(0);
            }
        }
        JSONObject obj = XcmgProjectUtil.convertMap2JsonObject(zgzlObj);
        mv.addObject("zgzlObj", obj);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        mv.addObject("currentUserMainGroupId", ContextUtil.getCurrentUser().getMainGroupId());
        mv.addObject("currentUserMainGroupName", ContextUtil.getCurrentUser().getMainGroupName());

        Map<String, Object> params = new HashMap<>();
        params.put("userId", ContextUtil.getCurrentUserId());
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        // 返回当前登录人角色信息
        List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params);
        // 如果是专利管理员或者分管领导
        boolean showJiaofei = false;
        for (int i = 0; i < currentUserRoles.size(); i++) {
            Map<String, Object> map = currentUserRoles.get(i);
            if (RdmConst.ZLGCS.equals(map.get("NAME_")) || RdmConst.FGLD.equals(map.get("NAME_"))) {
                showJiaofei = true;
                break;
            }
        }
        mv.addObject("showJiaofei", showJiaofei);
        return mv;
    }

    /**
     * 新建保存中国专利
     */
    @RequestMapping(value = "saveNewZgzlData", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult saveNewZgzlData(HttpServletRequest request, @RequestBody String newZgzlDataStr,
        HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "保存成功");
        if (StringUtils.isBlank(newZgzlDataStr)) {
            logger.warn("requestBody is blank");
            result.setSuccess(false);
            result.setMessage("表单为空，保存失败！");
            return result;
        }
        zlglmkService.saveOrCommitZgzlData(result, newZgzlDataStr);
        return result;
    }

    /**
     * 新建保存国际专利
     */
    @RequestMapping(value = "saveNewGjzlData", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult saveNewGjzlData(HttpServletRequest request, @RequestBody String newGjzlDataStr,
        HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "保存成功");
        if (StringUtils.isBlank(newGjzlDataStr)) {
            logger.warn("requestBody is blank");
            result.setSuccess(false);
            result.setMessage("表单为空，保存失败！");
            return result;
        }
        zlglmkService.saveOrCommitGjzlData(result, newGjzlDataStr);
        return result;
    }

    /*
    * 查询下拉框数据
    * */
    @RequestMapping("enumListQuery")
    @ResponseBody
    public List<JSONObject> enumListQuery(HttpServletRequest request, HttpServletResponse response, String type) {
        return zlglmkService.enumList(type);
    }

    @RequestMapping("zgzlUploadWindow")
    public ModelAndView fileUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/zgzlFileUploadList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String meetingId = RequestUtil.getString(request, "meetingId");
        String coverContent = RequestUtil.getString(request, "coverContent");
        String projectAction = RequestUtil.getString(request, "action");
        String canOperateFile = RequestUtil.getString(request, "canOperateFile");
        String fjlx = RequestUtil.getString(request, "fjlx");
        mv.addObject("coverContent", coverContent);
        mv.addObject("projectAction", projectAction);
        mv.addObject("meetingId", meetingId);
        mv.addObject("canOperateFile", canOperateFile);
        mv.addObject("fjlx", fjlx);
        return mv;
    }

    @RequestMapping("openUploadWindow")
    public ModelAndView openUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/zgzlFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUser", ContextUtil.getCurrentUser());
        return mv;
    }

    @RequestMapping(value = "upload")
    @ResponseBody
    public Map<String, Object> upload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        try {
            // 先保存文件到磁盘，成功后再写入数据库，前台是一个一个文件的调用
            zlglmkService.saveUploadFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            logger.error("Exception in upload", e);
            modelMap.put("success", "false");
        }
        return modelMap;
    }

    @RequestMapping("getFileList")
    @ResponseBody
    public JSONArray getFileList(HttpServletRequest request, HttpServletResponse response) {
        JSONArray fileInfoArray = zlglmkService.getFiles(request);
        return fileInfoArray;
    }

    @RequestMapping("getJiaoFeiList")
    @ResponseBody
    public List<JSONObject> getJiaoFeiList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // String meetingId = RequestUtil.getString(request, "meetingId", "");
        String meetingId = RequestUtil.getString(request, "ids");
        return zlglmkService.getJiaoFeiList(meetingId);
    }

    @RequestMapping(value = "saveJiaoFei", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult saveJiaoFei(HttpServletRequest request, @RequestBody String jiaoFeiDataStr,
        HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "保存成功");
        String meetingId = RequestUtil.getString(request, "meetingId");
        if (StringUtils.isBlank(jiaoFeiDataStr)) {
            logger.warn("requestBody is blank");
            result.setSuccess(false);
            result.setMessage("数据为空，保存失败！");
            return result;
        }
        zlglmkService.saveJiaoFei(result, jiaoFeiDataStr, meetingId);
        return result;
    }

    @RequestMapping("gjzlListPage")
    public ModelAndView gjzlListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/zlglGjzlList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        Map<String, Object> params = new HashMap<>();
        params.put("userId", ContextUtil.getCurrentUserId());
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        // 返回当前登录人角色信息
        List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params);
        JSONArray userRolesJsonArray = XcmgProjectUtil.convertListMap2JsonArrObject(currentUserRoles);
        mv.addObject("currentUserRoles", userRolesJsonArray);
        return mv;
    }

    @RequestMapping("queryGjListData")
    @ResponseBody
    public JsonPageResult<?> queryGjListData(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        return zlglmkService.queryGjListData(request, response, true);
    }

    /*
     *进去国际专利编辑页面
     */
    @RequestMapping("gjzlPage")
    public ModelAndView gjzlPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/zlglGjzlEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String gjzlId = RequestUtil.getString(request, "gjzlId");
        String action = RequestUtil.getString(request, "action");
        mv.addObject("action", action);
        Map<String, Object> gjzlObj = new HashMap<>();
        if (StringUtils.isNotEmpty(gjzlId)) {
            mv.addObject(gjzlId, gjzlId);
            Map<String, Object> params = new HashMap<>();
            params.put("gjzlId", gjzlId);
            List<Map<String, Object>> gjzlValue = zlglmkDao.queryGjzlId(params);
            if (gjzlValue != null && !gjzlValue.isEmpty()) {
                gjzlObj = gjzlValue.get(0);
            }
        }
        JSONObject obj = XcmgProjectUtil.convertMap2JsonObject(gjzlObj);
        mv.addObject("gjzlObj", obj);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        mv.addObject("currentUserMainGroupId", ContextUtil.getCurrentUser().getMainGroupId());
        mv.addObject("currentUserMainGroupName", ContextUtil.getCurrentUser().getMainGroupName());
        Map<String, Object> params = new HashMap<>();
        params.put("userId", ContextUtil.getCurrentUserId());
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        // 返回当前登录人角色信息
        List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params);
        // 如果是专利管理员或者分管领导
        boolean showJiaofei = false;
        for (int i = 0; i < currentUserRoles.size(); i++) {
            Map<String, Object> map = currentUserRoles.get(i);
            if (RdmConst.ZLGLY.equals(map.get("NAME_")) || RdmConst.FGLD.equals(map.get("NAME_"))) {
                showJiaofei = true;
                break;
            }
        }
        mv.addObject("showJiaofei", showJiaofei);
        return mv;
    }

    @RequestMapping("zlglPdfPreview")
    public ResponseEntity<byte[]> zlglPdfPreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("zlglmkFilePathBase");
        return jssjkFileManager.pdfPreviewOrDownLoad(fileName, fileId, formId, fileBasePath);
    }

    @RequestMapping("zlglOfficePreview")
    @ResponseBody
    public void zlglOfficePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("zlglmkFilePathBase");
        jssjkFileManager.officeFilePreview(fileName, fileId, formId, fileBasePath, response);
    }

    @RequestMapping("zlglImagePreview")
    @ResponseBody
    public void zlglImagePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("zlglmkFilePathBase");
        jssjkFileManager.imageFilePreview(fileName, fileId, formId, fileBasePath, response);
    }

    // 文档的下载
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
            String standardId = "";
            if ("1".equals(RequestUtil.getString(request, "bjType"))) {
                String id = RequestUtil.getString(request, "standardId");
                Map<String, Object> params = new HashMap<>();
                params.put("fyId", id);
                List<JSONObject> fyzlId = zlglmkDao.queryFYId(params);
                JSONObject obj = fyzlId.get(0);
                standardId = obj.getString("zlId");
            } else {
                standardId = RequestUtil.getString(request, "standardId");
            }
            String fileBasePath = "";
            fileBasePath =
                ConstantUtil.PREVIEW.equalsIgnoreCase(action) ? WebAppUtil.getProperty("standardAttachFilePath_preview")
                    : WebAppUtil.getProperty("zlglmkFilePathBase");
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

    // 删除某个文件（从文件表和磁盘上都删除）
    @RequestMapping("deleteZlglFiles")
    public void deleteZlglFiles(HttpServletRequest request, HttpServletResponse response) {
        String standardId = RequestUtil.getString(request, "standardId");
        String id = RequestUtil.getString(request, "id");
        String fileName = RequestUtil.getString(request, "fileName");
        String suffix = CommonFuns.toGetFileSuffix(fileName);
        deleteOneJssjkFileOnDisk(standardId, id, suffix);
        Map<String, Object> fileParams = new HashMap<>(16);
        List<String> fileIds = new ArrayList<>();
        fileIds.add(id);
        fileParams.put("fileIds", fileIds);
        zlglmkDao.deleteFileByIds(fileParams);
    }

    @RequestMapping("deleteJfpjFiles")
    public void deleteJfpjFiles(HttpServletRequest request, HttpServletResponse response) {
        String standardId = RequestUtil.getString(request, "standardId");
        String id = RequestUtil.getString(request, "id");
        String fileName = RequestUtil.getString(request, "fileName");
        String suffix = CommonFuns.toGetFileSuffix(fileName);
        deleteOneJssjkFileOnDisk(standardId, id, suffix);
        Map<String, Object> fileParams = new HashMap<>(16);
        List<String> fileIds = new ArrayList<>();
        fileIds.add(id);
        fileParams.put("fileIds", fileIds);
        zlglmkDao.deleteJfpjFileByIds(fileParams);
    }

    // 删除某一个附件文件
    public void deleteOneJssjkFileOnDisk(String standardId, String fileId, String suffix) {
        String fullFileName = fileId + "." + suffix;
        StringBuilder fileBasePath = new StringBuilder(WebAppUtil.getProperty("zlglmkFilePathBase"));
        String fullPath = fileBasePath.append(File.separator).append(standardId).append(File.separator)
            .append(fullFileName).toString();
        File file = new File(fullPath);
        if (file.exists()) {
            file.delete();
        }
    }

    @RequestMapping("deleteZgzl")
    @ResponseBody
    public JsonResult deleteZgzl(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return zlglmkService.deleteZgzl(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteJsmm", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("deleteOneJiaofeiData")
    @ResponseBody
    public JsonResult deleteOneJiaofeiData(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String id = RequestUtil.getString(request, "id");
            return zlglmkService.deleteJiaoFei(id);
        } catch (Exception e) {
            logger.error("Exception in deleteJsmm", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("deleteGjzl")
    @ResponseBody
    public JsonResult deleteGjzl(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return zlglmkService.deleteGjzl(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteJsmm", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("exportZgzlList")
    public void exportExcel(HttpServletRequest request, HttpServletResponse response) {
        zlglmkService.exportZgzlList(request, response);
    }

    @RequestMapping("exportGjzlList")
    public void exportExcelg(HttpServletRequest request, HttpServletResponse response) {
        zlglmkService.exportGjzlList(request, response);
    }

    /**
     * 模板下载
     */
    @RequestMapping("/importTemplateDownload")
    public ResponseEntity<byte[]> importTemplateDownload(HttpServletRequest request, HttpServletResponse response) {
        return zlglmkService.importTemplateDownload();
    }

    /**
     * 批量导入
     */
    @RequestMapping("importExcel")
    @ResponseBody
    public JSONObject importExcel(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        zlglmkService.importProduct(result, request);
        return result;
    }

    @RequestMapping("/saveTime")
    public void saveTime(HttpServletRequest request, @RequestBody String time, HttpServletResponse response) {
        JSONObject timejson = JSONObject.parseObject(time);
        String lastUpdateTime = timejson.getString("lastSaveTime");
        zlglmkService.saveTime(lastUpdateTime);
    }

    // 需求条目附件列表
    @RequestMapping("checkProject")
    @ResponseBody
    public JSONObject approveValid(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject approveValid = new JSONObject();
        approveValid.put("success", true);
        approveValid.put("message", "");
        String zgzlId = RequestUtil.getString(request, "zgzlId", "");
        JSONObject params = new JSONObject();
        List<JSONObject> checkList = new ArrayList<>();
        params.put("zgzlId", zgzlId);
        checkList = zlglmkDao.checkProject(params);
        if (checkList.size() != 0) {
            approveValid.put("success", false);
            approveValid.put("message", "该专利已关联项目-"+checkList.get(0).getString("projectName")+"！");
        }
        return approveValid;
    }
}
