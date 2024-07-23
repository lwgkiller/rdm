package com.redxun.xcmgjssjk.core.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.BpmSolution;
import com.redxun.bpm.core.entity.ProcessMessage;
import com.redxun.bpm.core.entity.ProcessStartCmd;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.bpm.core.manager.BpmSolutionManager;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.ExceptionUtil;
import com.redxun.org.api.model.IUser;
import com.redxun.org.api.service.UserService;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmZhgl.core.controller.HyglInternalController;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.controller.GenericController;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.util.ConstantUtil;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import com.redxun.xcmgjssjk.core.dao.XcmgjssjkDao;
import com.redxun.xcmgjssjk.core.service.JssjkHandler;
import com.redxun.xcmgjssjk.core.service.XcmgjssjkService;
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
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * 技术数据库
 */
@Controller
@RequestMapping("/jssj/core/config/")
public class XcmgjssjkController extends GenericController {
    private static final Logger logger = LoggerFactory.getLogger(HyglInternalController.class);
    @Resource
    private XcmgjssjkService xcmgjssjkService;
    @Autowired
    private XcmgjssjkDao xcmgjssjkDao;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Resource
    private BpmInstManager bpmInstManager;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private CommonBpmManager commonBpmManager;
    @Resource
    private BpmSolutionManager bpmSolutionManager;
    @Resource
    private UserService userService;
    @Resource
    private JssjkHandler jssjkHandler;

    @RequestMapping("spListPage")
    public ModelAndView listPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "xcmgjssjk/core/jssjkspList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        Map<String, Object> params = new HashMap<>();
        params.put("userId", ContextUtil.getCurrentUserId());
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        // 返回当前登录人角色信息
        List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params);
        JSONArray userRolesJsonArray = XcmgProjectUtil.convertListMap2JsonArrObject(currentUserRoles);
        mv.addObject("currentUserRoles", userRolesJsonArray);
        // 当前用户是否是专利工程师
        boolean isZlgcsUser = false;
        for (int i = 0; i < currentUserRoles.size(); i++) {
            Map<String, Object> map = currentUserRoles.get(i);
            if (RdmConst.ZLGCS.equals(map.get("NAME_"))) {
                isZlgcsUser = true;
                break;
            }
        }
        mv.addObject("isZlgcsUser", isZlgcsUser);
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        return mv;
    }

    @RequestMapping("wdjskListPage")
    public ModelAndView wdjskListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "xcmgjssjk/core/xcmgjssjkwdList.jsp";
        String type = RequestUtil.getString(request, "type", "");
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("type", type);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());

        return mv;
    }

    @RequestMapping("getJssjkList")
    @ResponseBody
    public JsonPageResult<?> getJssjkList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return xcmgjssjkService.getJssjkList(request, response, true);
    }

    @RequestMapping("getSpList")
    @ResponseBody
    public JsonPageResult<?> getSpList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return xcmgjssjkService.getSpList(request, response, true);
    }

    @RequestMapping("getJssjkDetail")
    @ResponseBody
    public JSONObject getJssjkDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject jssjkObj = new JSONObject();
        String jssjkId = RequestUtil.getString(request, "jssjkId");
        if (StringUtils.isNotBlank(jssjkId)) {
            jssjkObj = xcmgjssjkService.getJssjkDetail(jssjkId);
        }
        return jssjkObj;
    }

    @RequestMapping("saveJssjk")
    @ResponseBody
    public JsonResult saveJssjk(HttpServletRequest request, @RequestBody String xcmgProjectStr,
        HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "");
        if (StringUtils.isBlank(xcmgProjectStr)) {
            logger.warn("requestBody is blank");
            result.setSuccess(false);
            result.setMessage("requestBody is blank");
            return result;
        }
        try {
            JSONObject formDataJson = JSONObject.parseObject(xcmgProjectStr);
            if (formDataJson == null || formDataJson.isEmpty()) {
                logger.warn("formData is blank");
                result.setSuccess(false);
                result.setMessage("requestBody is blank");
                return result;
            }
            if (StringUtils.isBlank(formDataJson.getString("jssjkId"))) {
                result=  xcmgjssjkService.createJssjk(formDataJson);
                if(result.isSuccess()){
                    autoCreateFlow(formDataJson);
                }
            } else {
                result=  xcmgjssjkService.updateJssjk(formDataJson);
            }
        } catch (Exception e) {
            logger.error("Exception in save jssjk");
            result.setSuccess(false);
            result.setMessage("Exception in save jssjk");
            return result;
        }
        return result;
    }

    @RequestMapping("editPage")
    public ModelAndView jssjkPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "xcmgjssjk/core/xcmgjssjkEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String jssjkId = RequestUtil.getString(request, "jssjkId");
        String nodeId = RequestUtil.getString(request, "nodeId");
        String action = RequestUtil.getString(request, "action");
        String status = RequestUtil.getString(request, "status");
        String sptype = RequestUtil.getString(request, "sptype");
        String selectId = RequestUtil.getString(request, "selectId");
        if (StringUtils.isBlank(action)) {
            // 新增或编辑的时候没有nodeId
            if (StringUtils.isBlank(nodeId) || nodeId.contains("PROCESS")) {
                action = "edit";
            } else {
                // 处理任务的时候有nodeId
                action = "task";
            }
        }
        JSONObject checkResult = xcmgjssjkService.getJssjkCp(selectId);
        if (StringUtils.isNotBlank(selectId) && "update".equals(sptype)) {
            if (checkResult == null) {
                checkResult = new JSONObject();
                List<JSONObject> jssjkFileObj = new ArrayList<JSONObject>();
                List<JSONObject> jssjktdmcList = new ArrayList<JSONObject>();
                List<JSONObject> jssjktdmcwbList = new ArrayList<JSONObject>();
                checkResult = xcmgjssjkService.getJssjkDetail(selectId);
                jssjkFileObj = xcmgjssjkService.getJssjkCpFile(selectId);
                jssjktdmcList = xcmgjssjkService.getTdmcList(selectId);
                jssjktdmcwbList = xcmgjssjkService.getTdmcwbList(selectId);
                String newJssjkId = IdUtil.getId();
                checkResult.put("jssjkId", newJssjkId);
                checkResult.put("CREATE_BY_", null);
                checkResult.put("CREATE_TIME_", null);
                checkResult.put("jsfzrId", ContextUtil.getCurrentUserId());
                checkResult.put("jsfzrName", ContextUtil.getCurrentUser().getFullname());
                checkResult.put("splx", "update");
                checkResult.put("sbbId", selectId);
                checkResult.put("ifzb", "no");
                if (StringUtils.isBlank(checkResult.getString("path"))) {
                    checkResult.put("path", selectId);
                } else {
                    StringBuilder str = new StringBuilder();
                    str.append(checkResult.getString("path")).append(",").append(selectId);
                    checkResult.put("path", str.toString());
                }
                checkResult.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                checkResult.put("CREATE_TIME_", new Date());
                // 去掉编号
                checkResult.remove("jsNum");
                xcmgjssjkDao.insertJssjk(checkResult);
                for(JSONObject onedata:jssjkFileObj){
                    copySaveFile(onedata, selectId, newJssjkId);
                }

                if (jssjktdmcList.size() > 0) {
                    for (JSONObject jssjktdmc : jssjktdmcList) {
                        jssjktdmc.put("id", IdUtil.getId());
                        jssjktdmc.put("jssjkId", newJssjkId);
                        jssjktdmc.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                        jssjktdmc.put("CREATE_TIME_", new Date());
                        xcmgjssjkDao.insertJssjktdmc(jssjktdmc);
                    }
                }
                if (jssjktdmcwbList.size() > 0) {
                    for (JSONObject jssjktdmc : jssjktdmcwbList) {
                        jssjktdmc.put("id", IdUtil.getId());
                        jssjktdmc.put("jssjkId", newJssjkId);
                        jssjktdmc.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                        jssjktdmc.put("CREATE_TIME_", new Date());
                        xcmgjssjkDao.insertJssjktdmcwb(jssjktdmc);
                    }
                }
                mv.addObject("jssjkId", newJssjkId);

                autoCreateFlow(checkResult);

            }
        }
        if (StringUtils.isNotBlank(selectId) && "delete".equals(sptype)) {
            if (checkResult == null) {
                checkResult = new JSONObject();
                List<JSONObject> jssjkFileObj = new ArrayList<JSONObject>();
                List<JSONObject> jssjktdmcList = new ArrayList<JSONObject>();
                List<JSONObject> jssjktdmcwbList = new ArrayList<JSONObject>();
                checkResult = xcmgjssjkService.getJssjkDetail(selectId);
                jssjkFileObj = xcmgjssjkService.getJssjkCpFile(selectId);
                jssjktdmcList = xcmgjssjkService.getTdmcList(selectId);
                jssjktdmcwbList = xcmgjssjkService.getTdmcwbList(selectId);
                String newJssjkId = IdUtil.getId();
                checkResult.put("jssjkId", newJssjkId);
                checkResult.put("CREATE_BY_", null);
                checkResult.put("CREATE_TIME_", null);
                checkResult.put("splx", "delete");
                checkResult.put("sbbId",selectId);
                checkResult.put("ifzb", "no");
                checkResult.put("ifdel", "yes");
                if (StringUtils.isBlank(checkResult.getString("path"))) {
                    checkResult.put("path", selectId);
                } else {
                    StringBuilder str = new StringBuilder();
                    str.append(checkResult.getString("path")).append(",").append(selectId);
                    checkResult.put("path", str.toString());
                }
                checkResult.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                checkResult.put("CREATE_TIME_", new Date());
                xcmgjssjkDao.insertJssjk(checkResult);
                for(JSONObject onedata:jssjkFileObj){
                    copySaveFile(onedata, selectId, newJssjkId);
                }
                if (jssjktdmcList.size() > 0) {
                    for (JSONObject jssjktdmc : jssjktdmcList) {
                        jssjktdmc.put("id", IdUtil.getId());
                        jssjktdmc.put("jssjkId", newJssjkId);
                        jssjktdmc.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                        jssjktdmc.put("CREATE_TIME_", new Date());
                        xcmgjssjkDao.insertJssjktdmc(jssjktdmc);
                    }
                }
                if (jssjktdmcwbList.size() > 0) {
                    for (JSONObject jssjktdmc : jssjktdmcwbList) {
                        jssjktdmc.put("id", IdUtil.getId());
                        jssjktdmc.put("jssjkId", newJssjkId);
                        jssjktdmc.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                        jssjktdmc.put("CREATE_TIME_", new Date());
                        xcmgjssjkDao.insertJssjktdmcwb(jssjktdmc);
                    }
                }
                mv.addObject("jssjkId", newJssjkId);
                autoCreateFlow(checkResult);
            }
        }
        if (StringUtils.isBlank(selectId)) {
            mv.addObject("jssjkId", jssjkId);
        }
        if (("update".equals(sptype) && StringUtils.isNotBlank(selectId))
            || ("delete".equals(sptype) && StringUtils.isNotBlank(selectId))) {
            mv.addObject("jssjkId", checkResult.getString("jssjkId"));
        }
        mv.addObject("action", action).addObject("status", status).addObject("sptype", sptype);
        // 取出节点变量，返回到页面
        if (StringUtils.isNotBlank(nodeId)) {
            mv.addObject("nodeId", nodeId);
            List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId, "JSSJK", null);
            if (nodeVars != null && !nodeVars.isEmpty()) {
                mv.addObject("nodeVars", JSONObject.toJSONString(nodeVars));
            }
        }
        // 返回当前登录人角色信息
        Map<String, Object> params = new HashMap<>();
        params.put("userId", ContextUtil.getCurrentUserId());
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params);
        JSONArray userRolesJsonArray = XcmgProjectUtil.convertListMap2JsonArrObject(currentUserRoles);
        mv.addObject("currentUserRoles", userRolesJsonArray);
        // 当前用户是否是专利工程师
        boolean isZlgcsUser = false;
        for (int i = 0; i < currentUserRoles.size(); i++) {
            Map<String, Object> map = currentUserRoles.get(i);
            if (RdmConst.ZLGCS.equals(map.get("NAME_"))) {
                isZlgcsUser = true;
                break;
            }
        }
        mv.addObject("isZlgcsUser", isZlgcsUser);
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));


        return mv;
    }

    @RequestMapping("deleteJssjk")
    @ResponseBody
    public JsonResult deleteJssjk(HttpServletRequest request, HttpServletResponse response) throws Exception {
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
            return xcmgjssjkService.deleteJssjk(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteJssjk", e);
            return new JsonResult(false, e.getMessage());
        }
    }
    @RequestMapping("startJssjk")
    @ResponseBody
    public JsonResult startJssjk(HttpServletRequest request,@RequestBody String xcmgProjectStr, HttpServletResponse response)  {

        JsonResult result = new JsonResult(true, "");
        if (StringUtils.isBlank(xcmgProjectStr)) {
            logger.warn("requestBody is blank");
            result.setSuccess(false);
            result.setMessage("requestBody is blank");
            return result;
        }
        try {
            JSONObject formDataJson = JSONObject.parseObject(xcmgProjectStr);
            if (formDataJson == null || formDataJson.isEmpty()) {
                logger.warn("formData is blank");
                result.setSuccess(false);
                result.setMessage("requestBody is blank");
                return result;
            }

            if (StringUtils.isBlank(formDataJson.getString("jssjkId"))) {
                result=  xcmgjssjkService.createJssjk(formDataJson);
                if(result.isSuccess()){
                    autoStartFlow(formDataJson);
                }
                }else{
                autoStartFlow(formDataJson);
            }

        } catch (Exception e) {
            logger.error("Exception in start jssjk");
            result.setSuccess(false);
            result.setMessage("Exception in start jssjk");
            return result;
        }
        return result;
    }
    @RequestMapping("fileUploadWindow")
    public ModelAndView openUpaaloadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "xcmgjssjk/core/xcmgjssjkFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("jssjkId", RequestUtil.getString(request, "jssjkId", ""));
        return mv;
    }

    @RequestMapping(value = "fileUpload")
    @ResponseBody
    public Map<String, Object> fileUpload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        try {
            // 先保存文件到磁盘，成功后再写入数据库，前台是一个一个文件的调用
            xcmgjssjkService.saveUploadFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            logger.error("Exception in upload", e);
            modelMap.put("success", "false");
        }
        return modelMap;
    }

    @RequestMapping("getJssjkFileList")
    @ResponseBody
    public List<JSONObject> getJssjkFileList(HttpServletRequest request, HttpServletResponse response) {
        List<String> jssjkIdList = Arrays.asList(RequestUtil.getString(request, "jssjkId", ""));
        return xcmgjssjkService.getJssjkFileList(jssjkIdList);
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
            fileBasePath = ConstantUtil.PREVIEW.equalsIgnoreCase(action) ? WebAppUtil.getProperty("jssjkFilePathBase")
                : WebAppUtil.getProperty("jssjkFilePathBase");
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
    @RequestMapping("delJssjkFile")
    public void delJssjkFile(HttpServletRequest request, HttpServletResponse response, @RequestBody String postBody) {
        JSONObject postBodyObj = JSONObject.parseObject(postBody);
        String fileName = postBodyObj.getString("fileName");
        String fileId = postBodyObj.getString("id");
        String jssjkId = postBodyObj.getString("formId");
        xcmgjssjkService.deleteOneJssjkFile(fileId, fileName, jssjkId);
    }

    @RequestMapping("jssjkPdfPreview")
    public ResponseEntity<byte[]> jssjkPdfPreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("jssjkFilePathBase");
        return rdmZhglFileManager.pdfPreviewOrDownLoad(fileName, fileId, formId, fileBasePath);
    }

    @RequestMapping("jssjkOfficePreview")
    @ResponseBody
    public void jssjkOfficePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("jssjkFilePathBase");
        rdmZhglFileManager.officeFilePreview(fileName, fileId, formId, fileBasePath, response);
    }

    @RequestMapping("jssjkImagePreview")
    @ResponseBody
    public void jssjkImagePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("jssjkFilePathBase");
        rdmZhglFileManager.imageFilePreview(fileName, fileId, formId, fileBasePath, response);
    }

    public void copySaveFile(JSONObject jssjkFileObj, String selectId, String newJssjkId) {
        String bfileName = jssjkFileObj.getString("fileName");
        String bfileId = jssjkFileObj.getString("id");
        String bstandardId = jssjkFileObj.getString("jssjkId");
        String bfileBasePath = WebAppUtil.getProperty("jssjkFilePathBase");
        String bsuffix = CommonFuns.toGetFileSuffix(bfileName);
        String relativeFilePath = "";
        if (StringUtils.isNotBlank(bstandardId)) {
            relativeFilePath = File.separator + bstandardId;
        }
        String brealFileName = bfileId + "." + bsuffix;
        String bfullFilePath = bfileBasePath + relativeFilePath + File.separator + brealFileName;
        File bfile = new File(bfullFilePath);
        String id = IdUtil.getId();
        String fileSize = jssjkFileObj.getString("fileSize");
        String fjlx = jssjkFileObj.getString("fjlx");
        // 向下载目录中写入文件
        String filePath = bfileBasePath + File.separator + newJssjkId;
        File pathFile = new File(filePath);
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }
        String suffix = CommonFuns.toGetFileSuffix(bfileName);
        String fileFullPath = filePath + File.separator + id + "." + suffix;
        File file = new File(fileFullPath);
        try {
            FileCopyUtils.copy(FileUtils.readFileToByteArray(bfile), file);
            JSONObject fileInfo = new JSONObject();
            fileInfo.put("id", id);
            fileInfo.put("fileName", bfileName);
            fileInfo.put("fileSize", fileSize);
            fileInfo.put("jssjkId", newJssjkId);
            fileInfo.put("fjlx", fjlx);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            xcmgjssjkDao.addFileInfos(fileInfo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("zxDataQuery")
    @ResponseBody
    public JSONObject zxDataQuery(HttpServletRequest request, HttpServletResponse response, String selectId) {
        return xcmgjssjkService.getJssjkCp(selectId);
    }

    @RequestMapping("getTdmcList")
    @ResponseBody
    public List<JSONObject> getTdmcList(HttpServletRequest request, HttpServletResponse response) {
        String jssjkId = RequestUtil.getString(request, "jssjkId");
        return xcmgjssjkService.getTdmcList(jssjkId);
    }

    @RequestMapping("getTdmcwbList")
    @ResponseBody
    public List<JSONObject> getTdmcwbList(HttpServletRequest request, HttpServletResponse response) {
        String jssjkId = RequestUtil.getString(request, "jssjkId");
        return xcmgjssjkService.getTdmcwbList(jssjkId);
    }

    /**
     * 自动创建审批流程
     * */
    public void autoCreateFlow(JSONObject postJSON){
        try {
            String flowType = "JSSJK";
            BpmSolution bpmSolution = bpmSolutionManager.getByKey(flowType,"1");
            IUser user = userService.getByUserId(ContextUtil.getCurrentUserId());
            JsonResult result = doSaveDraft(bpmSolution.getSolId(),postJSON,user);
        }catch (Exception e){
            logger.error("Exception in autoCreateFlow", e);
        }
    }
    /**
     * 自动启动审批流程
     * */
    public void autoStartFlow(JSONObject postJSON){
        try {
            String flowType = "JSSJK";
            BpmSolution bpmSolution = bpmSolutionManager.getByKey(flowType,"1");
            IUser user = userService.getByUserId(ContextUtil.getCurrentUserId());
            JsonResult result = startProcess(bpmSolution.getSolId(),postJSON,user);
        }catch (Exception e){
            logger.error("Exception in autoStartFlow", e);
        }
    }
    private JsonResult doSaveDraft(String solId, JSONObject contentJson, IUser user) throws Exception {
        // 加上处理的消息提示
        ProcessMessage handleMessage = new ProcessMessage();
        JsonResult result = new JsonResult();
        try {
            ContextUtil.setCurUser(user);
            ProcessHandleHelper.setProcessMessage(handleMessage);
            ProcessStartCmd startCmd = new ProcessStartCmd();
            startCmd.setSolId(solId);
            startCmd.setJsonData(contentJson.toJSONString());
            // 启动流程
            bpmInstManager.doSaveDraft(startCmd);
            result.setData(startCmd.getBusinessKey());
//            jssjkHandler.processStartPreHandle(startCmd);

        } catch (Exception ex) {
            // 把具体的错误放置在内部处理，以显示正确的错误信息提示，在此不作任何的错误处理
            logger.error(ExceptionUtil.getExceptionMessage(ex));
            if (handleMessage.getErrorMsges().size() == 0) {
                handleMessage.addErrorMsg(ex.getMessage());
            }
        } finally {
            // 在处理过程中，是否有错误的消息抛出
            if (handleMessage.getErrorMsges().size() > 0) {
                result.setSuccess(false);
                result.setMessage("保存草稿失败!");
                result.setData(handleMessage.getErrors());
            } else {
                result.setSuccess(true);
                result.setMessage("保存草稿成功！");
            }
            ProcessHandleHelper.clearProcessCmd();
            ProcessHandleHelper.clearProcessMessage();
        }
        return result;
    }
    private JsonResult startProcess(String solId, JSONObject contentJson, IUser user) throws Exception {
        // 加上处理的消息提示
        ProcessMessage handleMessage = new ProcessMessage();
        JsonResult result = new JsonResult();
        try {
            ContextUtil.setCurUser(user);
            ProcessHandleHelper.setProcessMessage(handleMessage);
            ProcessStartCmd startCmd = new ProcessStartCmd();
            startCmd.setSolId(solId);
            startCmd.setBpmInstId(contentJson.getString("instId"));
            startCmd.setJsonData(contentJson.toJSONString());
            // 启动流程
            bpmInstManager.doStartProcess(startCmd);
            result.setData(startCmd.getBusinessKey());

        } catch (Exception ex) {
            // 把具体的错误放置在内部处理，以显示正确的错误信息提示，在此不作任何的错误处理
            logger.error(ExceptionUtil.getExceptionMessage(ex));
            if (handleMessage.getErrorMsges().size() == 0) {
                handleMessage.addErrorMsg(ex.getMessage());
            }
        } finally {
            // 在处理过程中，是否有错误的消息抛出
            if (handleMessage.getErrorMsges().size() > 0) {
                result.setSuccess(false);
                result.setMessage("保存草稿失败!");
                result.setData(handleMessage.getErrors());
            } else {
                result.setSuccess(true);
                result.setMessage("保存草稿成功！");
            }
            ProcessHandleHelper.clearProcessCmd();
            ProcessHandleHelper.clearProcessMessage();
        }
        return result;
    }

}
