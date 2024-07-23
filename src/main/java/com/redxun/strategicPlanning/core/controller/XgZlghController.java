package com.redxun.strategicPlanning.core.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.strategicPlanning.core.dao.XgZlghDao;
import com.redxun.strategicPlanning.core.service.XgZlghService;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.util.ConstantUtil;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.*;

@RestController
@RequestMapping("/strategicPlanning/core/xgzlgh/")
public class XgZlghController {
    private static final Logger logger = LoggerFactory.getLogger(XgZlghController.class);
    @Resource
    private XgZlghService xgZlghService;
    @Autowired
    private XgZlghDao xgZlghDao;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private CommonInfoManager commonInfoManager;
    @Resource
    SendDDNoticeManager sendDDNoticeManager;

    @RequestMapping("listPage")
    public ModelAndView listPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "strategicPlanning/core/xgzlghList.jsp";
        String type = RequestUtil.getString(request, "type", "");
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("type", type);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        Map<String, Object> params = new HashMap<>();
        params.put("userId", ContextUtil.getCurrentUserId());
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        // 返回当前登录人角色信息
        List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params);
        JSONArray userRolesJsonArray = XcmgProjectUtil.convertListMap2JsonArrObject(currentUserRoles);
        mv.addObject("currentUserRoles", userRolesJsonArray);
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        return mv;
    }
    @RequestMapping("getZlghList")
    @ResponseBody
    public JsonPageResult<?> getZlghList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return xgZlghService.getZlghList(request, response, true);
    }
    @RequestMapping("edit")
    public ModelAndView edit(HttpServletRequest request, HttpServletResponse response) {

        String jspPath = "strategicPlanning/core/xgzlghEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String zlghId = RequestUtil.getString(request, "zlghId");
        mv.addObject("zlghId", zlghId);
        String action = RequestUtil.getString(request, "action");
        String type = RequestUtil.getString(request, "type");
        JSONObject zlghObj = new JSONObject();
        //查询信息
        if (StringUtils.isNotBlank(zlghId)) {
            Map<String, Object> anpInfo = xgZlghService.queryZlghById(zlghId);
            zlghObj = XcmgProjectUtil.convertMap2JsonObject(anpInfo);
        }
        mv.addObject("zlghObj", zlghObj);
        mv.addObject("action", action);
        mv.addObject("type", type);
        mv.addObject("currentUser", ContextUtil.getCurrentUser());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        return mv;
    }
    @RequestMapping("saveZlgh")
    @ResponseBody
    public JSONObject saveZlgh(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        xgZlghService.saveZlgh(result, request);
        return result;
    }
    @RequestMapping("zlghUploadWindow")
    public ModelAndView fileUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "strategicPlanning/core/zlghFileList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String meetingId = RequestUtil.getString(request, "zlghId");
        String coverContent = RequestUtil.getString(request, "coverContent");
        String projectAction = RequestUtil.getString(request, "action");
        String canOperateFile = RequestUtil.getString(request, "canOperateFile");
        boolean isFzr=false;
        if (StringUtils.isNotBlank(meetingId)){
            String fzrId = xgZlghDao.selectFzrId(meetingId);
            if (StringUtils.isNotBlank(fzrId)){
                List<String> fzrIdList =Arrays.asList(fzrId.split(",",-1));
                for (int i =0;i<fzrIdList.size();i++){
                    if (fzrIdList.get(i).equalsIgnoreCase(ContextUtil.getCurrentUserId())){
                        isFzr=true;
                        break;
                    }
                }
            }
        }
        mv.addObject("isFzr",isFzr);
        //挖掘机械研究院所有的部门
        boolean isZgsz=false;
        Map<String, Object> params = new HashMap<>();
        Map<String, String> deptId2Name = commonInfoManager.queryDeptUnderJSZX();
        params.put("deptIds", deptId2Name.keySet());
        List<Map<String, String>> depRespMans = xgZlghDao.getDepManById(params);
        for (Map<String, String> depRespMan :depRespMans){
            if (StringUtils.isNotBlank(depRespMan.get("USER_ID_"))){
                if (depRespMan.get("USER_ID_").equalsIgnoreCase(ContextUtil.getCurrentUserId())){
                    isZgsz=true;
                    break;
                }
            }
        }
        mv.addObject("isZgsz",isZgsz);
        //是否是战略规划专员
        boolean isZLGHZY = rdmZhglUtil.judgeIsPointRoleUser(ContextUtil.getCurrentUserId(), "战略规划专员");
        mv.addObject("isZLGHZY",isZLGHZY);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("coverContent", coverContent);
        mv.addObject("projectAction", projectAction);
        mv.addObject("meetingId", meetingId);
        mv.addObject("canOperateFile", canOperateFile);
        return mv;
    }
    @RequestMapping("getFileList")
    @ResponseBody
    public JSONArray getFileList(HttpServletRequest request, HttpServletResponse response) {
        JSONArray fileInfoArray = xgZlghService.getFiles(request);
        return fileInfoArray;
    }
    @RequestMapping("openUploadWindow")
    public ModelAndView openUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "strategicPlanning/core/zlghFileUpload.jsp";
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
            xgZlghService.saveUploadFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            logger.error("Exception in upload", e);
            modelMap.put("success", "false");
        }
        return modelMap;
    }
    @RequestMapping("zlghPdfPreview")
    public ResponseEntity<byte[]> zlghPdfPreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("zlghFilePathBase");
        return rdmZhglFileManager.pdfPreviewOrDownLoad(fileName, fileId, formId, fileBasePath);
    }
    @RequestMapping("zlghOfficePreview")
    @ResponseBody
    public void zlghOfficePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("zlghFilePathBase");
        rdmZhglFileManager.officeFilePreview(fileName, fileId, formId, fileBasePath, response);
    }
    @RequestMapping("zlghImagePreview")
    @ResponseBody
    public void zlghImagePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("zlghFilePathBase");
        rdmZhglFileManager.imageFilePreview(fileName, fileId, formId, fileBasePath, response);
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
                    ConstantUtil.PREVIEW.equalsIgnoreCase(action) ? WebAppUtil.getProperty("zlghFilePathBase")
                            : WebAppUtil.getProperty("zlghFilePathBase");
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
    @RequestMapping("deleteZlghFiles")
    public void deleteZlghFiles(HttpServletRequest request, HttpServletResponse response) {
        String standardId = RequestUtil.getString(request, "standardId");
        String id = RequestUtil.getString(request, "id");
        String fileName = RequestUtil.getString(request, "fileName");
        String suffix = CommonFuns.toGetFileSuffix(fileName);
        deleteOneZlghFileOnDisk(standardId, id, suffix);
        Map<String, Object> fileParams = new HashMap<>(16);
        List<String> fileIds = new ArrayList<>();
        fileIds.add(id);
        fileParams.put("fileIds", fileIds);
        xgZlghDao.deleteFileByIds(fileParams);
    }
    //删除某一个附件文件
    public void deleteOneZlghFileOnDisk(String standardId, String fileId, String suffix) {
        String fullFileName = fileId + "." + suffix;
        StringBuilder fileBasePath = new StringBuilder(WebAppUtil.getProperty("zlghFilePathBase"));
        String fullPath = fileBasePath.append(File.separator).append(standardId).append(File.separator)
                .append(fullFileName).toString();
        File file = new File(fullPath);
        if (file.exists()) {
            file.delete();
        }
    }
    @RequestMapping("deleteZlgh")
    @ResponseBody
    public JsonResult deleteZlgh(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return xgZlghService.deleteZlgh(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteZlgh", e);
            return new JsonResult(false, e.getMessage());
        }
    }
    /**
     * 发送项目通知
     * */
    @RequestMapping("notice")
    @ResponseBody
    public JsonResult notice(HttpServletRequest request, HttpServletResponse response) {
        try {
            String strh="";
            String ghfzrId = RequestUtil.getString(request, "ghfzrId");
            String xbbmfzrId = RequestUtil.getString(request, "xbbmfzrId");
            if (StringUtils.isNotBlank(xbbmfzrId)){
                strh=ghfzrId+","+xbbmfzrId;
            }else {
                strh=ghfzrId;
            }
            List<String> userList =Arrays.asList(strh.split(",",-1));
            StringBuilder userStr = new StringBuilder();
            Set<String> setList = new HashSet<>();
            for (int i=0;i<userList.size();i++){
                setList.add(userList.get(i));
            }
            for (String oneSet :setList){
                userStr.append(oneSet).append(",");
            }
            JSONObject noticeObj = new JSONObject();
            StringBuilder stringBuilder = new StringBuilder("【战略规划】");
            stringBuilder.append("您是规划负责人");
            stringBuilder.append("，请及时查看处理");
            stringBuilder.append("，通知时间：").append(XcmgProjectUtil.getNowLocalDateStr(DateUtil.DATE_FORMAT_FULL));
            noticeObj.put("content", stringBuilder.toString());
            sendDDNoticeManager.sendNoticeForCommon(userStr.substring(0,userStr.length()-1), noticeObj);
        } catch (Exception e) {
            return new JsonResult(false, e.getMessage());
        }
        return new JsonResult(true, "发送成功!");
    }
}
