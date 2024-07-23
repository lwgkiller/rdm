package com.redxun.rdmZhgl.core.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.rdmZhgl.core.dao.GjjsDao;
import com.redxun.rdmZhgl.core.service.GjjsService;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.controller.GenericController;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
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
 * 经鉴定的关键技术
 * */
@Controller
@RequestMapping("/zhgl/core/gjjs/")
public class GjjsController extends GenericController {
    private static final Logger logger = LoggerFactory.getLogger(HyglInternalController.class);
    @Autowired
    private GjjsService gjjsService;
    @Autowired
    private GjjsDao gjjsDao;
    @Autowired
    private JssjkFileManager jssjkFileManager;

    @RequestMapping("gjjsListPage")
    public ModelAndView zgzlListPage(HttpServletRequest request, HttpServletResponse response)throws Exception{
        String jspPath = "rdmZhgl/core/gjjsList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        return mv;
    }
    @RequestMapping("queryListData")
    @ResponseBody
    public JsonPageResult<?> queryListData(HttpServletRequest request, HttpServletResponse response) throws Exception{
        return gjjsService.queryListData(request, response,true);
    }
    /*
     *进去关键技术编辑页面
     */
    @RequestMapping("gjjsPage")
    public ModelAndView gjjsPage(HttpServletRequest request, HttpServletResponse response) throws Exception{
        String jspPath = "rdmZhgl/core/gjjsEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String pId = RequestUtil.getString(request, "pId");
        String action = RequestUtil.getString(request, "action");
        mv.addObject("action",action);
        Map<String, Object> gjjsObj=new HashMap<>();
        if (StringUtils.isNotEmpty(pId)){
            mv.addObject(pId,pId);
            Map<String, Object> params = new HashMap<>();
            params.put("pId", pId);
            List<Map<String, Object>> gjjsValue = gjjsDao.queryDataId(params);
            if (gjjsValue != null && !gjjsValue.isEmpty()) {
                gjjsObj=gjjsValue.get(0);
            }
        }
        JSONObject obj=XcmgProjectUtil.convertMap2JsonObject(gjjsObj);
        mv.addObject("gjjsObj", obj);
        mv.addObject("pId", pId);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserName",ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        return mv;
    }
    /**
     * 新建保存关键技术
     */
    @RequestMapping(value = "saveNewGjjsData",method = RequestMethod.POST)
    @ResponseBody
    public JsonResult saveNewGjjsData(HttpServletRequest request, @RequestBody String newGjjsDataStr, HttpServletResponse response){
        JsonResult result = new JsonResult(true, "保存成功");
        if (StringUtils.isBlank(newGjjsDataStr)) {
            logger.warn("requestBody is blank");
            result.setSuccess(false);
            result.setMessage("表单为空，保存失败！");
            return result;
        }
        gjjsService.saveOrCommitGjjsData(result,newGjjsDataStr);
        return result;
    }
    @RequestMapping(value = "saveJishu", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult saveJishu(HttpServletRequest request, @RequestBody String jiShuDataStr,
                                  HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "保存成功");
        String meetingId = RequestUtil.getString(request, "meetingId");
        if (StringUtils.isBlank(jiShuDataStr)) {
            logger.warn("requestBody is blank");
            result.setSuccess(false);
            result.setMessage("数据为空，保存失败！");
            return result;
        }
        gjjsService.saveJiShu(result, jiShuDataStr,meetingId);
        return result;
    }
    @RequestMapping("getJiShuList")
    @ResponseBody
    public List<JSONObject> getJiShuList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String meetingId = RequestUtil.getString(request, "pId");
        //如果项目id为空就不展示技术部分
        if(StringUtils.isBlank(meetingId)){
            return null;
        }
        return gjjsService.getJiShuList(meetingId);
    }
    @RequestMapping("deleteJiShuData")
    @ResponseBody
    public JsonResult deleteJiShuData(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return gjjsService.deleteJiShu(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteJsmm", e);
            return new JsonResult(false, e.getMessage());
        }
    }
    @RequestMapping("gjjsUploadWindow")
    public ModelAndView fileUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/gjjsFileUploadList.jsp";
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
        String jspPath = "rdmZhgl/core/gjjsFileUpload.jsp";
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
            gjjsService.saveUploadFiles(request);
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
        JSONArray fileInfoArray = gjjsService.getFiles(request);
        return fileInfoArray;
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
            String standardId = RequestUtil.getString(request, "standardId");
            String fileBasePath = "";
            fileBasePath =
                    ConstantUtil.PREVIEW.equalsIgnoreCase(action) ? WebAppUtil.getProperty("standardAttachFilePath_preview")
                            : WebAppUtil.getProperty("gjjsmkFilePathBase");
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
    @RequestMapping("deleteGjjsFiles")
    public void deleteGjjsFiles(HttpServletRequest request, HttpServletResponse response) {
        String standardId = RequestUtil.getString(request, "standardId");
        String id = RequestUtil.getString(request, "id");
        String fileName = RequestUtil.getString(request, "fileName");
        String suffix = CommonFuns.toGetFileSuffix(fileName);
        deleteOneJssjkFileOnDisk(standardId, id, suffix);
        Map<String, Object> fileParams = new HashMap<>(16);
        List<String> fileIds = new ArrayList<>();
        fileIds.add(id);
        fileParams.put("fileIds", fileIds);
        gjjsDao.deleteFileByIds(fileParams);
    }
    //删除某一个附件文件
    public void deleteOneJssjkFileOnDisk(String standardId, String fileId, String suffix) {
        String fullFileName = fileId + "." + suffix;
        StringBuilder fileBasePath = new StringBuilder(WebAppUtil.getProperty("gjjsmkFilePathBase"));
        String fullPath = fileBasePath.append(File.separator).append(standardId).append(File.separator)
                .append(fullFileName).toString();
        File file = new File(fullPath);
        if (file.exists()) {
            file.delete();
        }
    }
    @RequestMapping("gjjsPdfPreview")
    public ResponseEntity<byte[]> gjjsPdfPreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("gjjsmkFilePathBase");
        return jssjkFileManager.pdfPreviewOrDownLoad(fileName, fileId, formId, fileBasePath);
    }
    @RequestMapping("gjjsOfficePreview")
    @ResponseBody
    public void gjjsOfficePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("gjjsmkFilePathBase");
        jssjkFileManager.officeFilePreview(fileName, fileId, formId, fileBasePath, response);
    }
    @RequestMapping("gjjsImagePreview")
    @ResponseBody
    public void gjjsImagePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("gjjsmkFilePathBase");
        jssjkFileManager.imageFilePreview(fileName, fileId, formId, fileBasePath, response);
    }
    @RequestMapping("deleteGjjs")
    @ResponseBody
    public JsonResult deleteGjjs(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return gjjsService.deleteGjjs(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteJsmm", e);
            return new JsonResult(false, e.getMessage());
        }
    }
}
