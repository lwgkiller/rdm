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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.entity.BpmTask;
import com.redxun.bpm.core.entity.ProcessNextCmd;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.bpm.core.manager.BpmTaskManager;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import com.redxun.zlgjNPI.core.dao.ZlgjWTDao;
import com.redxun.zlgjNPI.core.manager.ZlgjWTService;

/**
 * 这个控制器由六个菜单共享，流程一样,分别是： 新产品导入下的(新品试制问题,XPSZ),(新品整机试验问题,XPZDSY),(新品路试问题,XPLS)
 * 分析改进下的(厂内问题改进,CNWT),(市场问题改进,SCWT),(海外问题改进,HWWT)
 */
@RestController
@RequestMapping("/xjsdr/core/zlgj/")
public class ZlgjWTController {
    private Logger logger = LogManager.getLogger(ZlgjWTController.class);
    @Resource
    private ZlgjWTService zlgjWTService;
    @Resource
    private ZlgjWTDao zlgjWTDao;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Resource
    private BpmInstManager bpmInstManager;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private CommonBpmManager commonBpmManager;

    @Autowired
    private BpmTaskManager bpmTaskManager;

    @RequestMapping("listPage")
    public ModelAndView listPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "zlgjNPI/zlgjList.jsp";
        String wtlxtype = RequestUtil.getString(request, "wtlxtype", "");
        String create_startTime = RequestUtil.getString(request, "create_startTime", "");
        String create_endTime = RequestUtil.getString(request, "create_endTime", "");
        String deptName = RequestUtil.getString(request, "deptName", "");
        String chartType = RequestUtil.getString(request, "chartType", "");
        String czxpj = RequestUtil.getString(request, "czxpj", "");
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("wtlxtype", wtlxtype);
        mv.addObject("create_startTime", create_startTime);
        mv.addObject("create_endTime", create_endTime);
        mv.addObject("deptName", deptName);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("chartType", chartType);
        mv.addObject("czxpj", czxpj);
        Map<String, Object> params = new HashMap<>();
        params.put("userId", ContextUtil.getCurrentUserId());
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        // 返回当前登录人角色信息
        List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params);
        JSONArray userRolesJsonArray = XcmgProjectUtil.convertListMap2JsonArrObject(currentUserRoles);
        mv.addObject("currentUserRoles", userRolesJsonArray);
        // 是否是市场问题提交人
        boolean isScwtUser = false;
        // 是否是海外问题提交人
        boolean isHwwtUser = false;
        if (RdmConst.ZLBZB_NAME.equalsIgnoreCase(ContextUtil.getCurrentUser().getMainGroupName())) {
            isScwtUser = true;
            isHwwtUser = true;
        } else {
            for (int i = 0; i < currentUserRoles.size(); i++) {
                Map<String, Object> map = currentUserRoles.get(i);
                if ("市场问题提交人".equals(map.get("NAME_"))) {
                    isScwtUser = true;
                }
                if ("海外问题提交人".equals(map.get("NAME_"))) {
                    isHwwtUser = true;
                }
            }
        }

        mv.addObject("isScwtUser", true);
        mv.addObject("isHwwtUser", isHwwtUser);
        // 返回当前用户是否是技术管理部负责人
        boolean isJsglbRespUser = rdmZhglUtil.judgeUserIsJsglbRespUser(ContextUtil.getCurrentUserId());
        mv.addObject("isJsglbRespUser", isJsglbRespUser);
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        boolean isBgry = rdmZhglUtil.judgeIsPointRoleUser(ContextUtil.getCurrentUserId(), "质量改进变更人员");
        mv.addObject("isBgry", isBgry);

        String report = RequestUtil.getString(request, "report", "");
        mv.addObject("report", report);
        return mv;
    }

    @RequestMapping("getZlgjList")
    @ResponseBody
    public JsonPageResult<?> getZlgjList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> params = new HashMap<>();
        return zlgjWTService.getZlgjList(request, response, true, params, true, "zwt.CREATE_TIME_");
    }

    // 导出质量改进问题
    @PostMapping("exportWtsbExcel")
    public void exportWtsbExcel(HttpServletResponse response, HttpServletRequest request) {
        zlgjWTService.exportWtsbExcel(response, request);
    }

    @RequestMapping("editPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "zlgjNPI/zlgjEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String wtId = RequestUtil.getString(request, "wtId");
        String nodeId = RequestUtil.getString(request, "nodeId");
        String action = RequestUtil.getString(request, "action");
        String status = RequestUtil.getString(request, "status");
        String wtlxtype = RequestUtil.getString(request, "wtlxtype");
        String ifCopy = RequestUtil.getString(request, "ifCopy");
        String copyId = RequestUtil.getString(request, "copyId");

        if (StringUtils.isBlank(action)) {
            // 新增或编辑的时候没有nodeId
            if (StringUtils.isBlank(nodeId) || nodeId.contains("PROCESS")) {
                action = "edit";
            } else {
                // 处理任务的时候有nodeId
                action = "task";
            }
        }
        mv.addObject("wtId", wtId).addObject("action", action).addObject("status", status);
        // 取出节点变量，返回到页面
        if (StringUtils.isNotBlank(nodeId)) {
            mv.addObject("nodeId", nodeId);
            List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId, "ZLGJ", null);
            if (nodeVars != null && !nodeVars.isEmpty()) {
                mv.addObject("nodeVars", JSONObject.toJSONString(nodeVars));
            }
        }
        mv.addObject("wtlxtype", wtlxtype);
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        mv.addObject("ifCopy", ifCopy);
        JSONObject copyZlgjObj = new JSONObject();
        if ("yes".equalsIgnoreCase(ifCopy)) {
            // 查询出拷贝问题的信息
            JSONObject queryCopyZlgjObj = zlgjWTService.getZlgjDetail(copyId);
            if (queryCopyZlgjObj != null) {
                copyZlgjObj = queryCopyZlgjObj;
            }
        }

        mv.addObject("copyZlgjObj", copyZlgjObj);
        return mv;
    }

    @RequestMapping("getZlgjDetail")
    @ResponseBody
    public JSONObject getZlgjDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject zlgjObj = new JSONObject();
        String wtId = RequestUtil.getString(request, "wtId");
        if (StringUtils.isNotBlank(wtId)) {
            zlgjObj = zlgjWTService.getZlgjDetail(wtId);
        }
        return zlgjObj;
    }

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
                /*********** 定位问题 *********/
                if (StringUtils.isBlank(formDataJson.getString("wtlx"))) {
                    logger.error("!!!!问题类型为空");
                    result.setSuccess(false);
                    result.setMessage("问题类型为空");
                    return result;
                }
                zlgjWTService.createZlgj(formDataJson);
            } else {
                /*********** 定位问题 *********/
                if (StringUtils.isBlank(formDataJson.getString("wtlx"))) {
                    logger.error("!!!!问题类型为空");
                    result.setSuccess(false);
                    result.setMessage("问题类型为空");
                    return result;
                }
                zlgjWTService.updateZlgj(formDataJson);
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
            return zlgjWTService.deleteZlgj(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteZlgj", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("zlgjFileWindow")
    public ModelAndView zlgjFileWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "zlgjNPI/zlgjPicList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String meetingId = RequestUtil.getString(request, "meetingId");
        String action = RequestUtil.getString(request, "action");
        String canOperateFile = RequestUtil.getString(request, "canOperateFile");
        String coverContent = RequestUtil.getString(request, "coverContent");
        String fjlx = RequestUtil.getString(request, "fjlx");
        String faId = RequestUtil.getString(request, "faId");
        // String csId = RequestUtil.getString(request, "csId");
        // String yzyyId = RequestUtil.getString(request, "yzyyId");
        mv.addObject("coverContent", coverContent);
        mv.addObject("meetingId", meetingId);
        mv.addObject("action", action);
        mv.addObject("canOperateFile", canOperateFile);
        mv.addObject("fjlx", fjlx);
        mv.addObject("faId", faId);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        // mv.addObject("csId", csId);
        // mv.addObject("yzyyId", yzyyId);
        return mv;
    }

    @RequestMapping("lbjFileWindow")
    public ModelAndView lbjFileWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "zlgjNPI/zlgjlbjfile.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String businessId = RequestUtil.getString(request, "businessId");
        mv.addObject("businessId", businessId);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUser", ContextUtil.getCurrentUser());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        return mv;
    }

    @RequestMapping("openUploadWindow")
    public ModelAndView openUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "zlgjNPI/zlgjPicUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUser", ContextUtil.getCurrentUser());
        return mv;
    }

    @RequestMapping(value = "upload")
    @ResponseBody
    public Map<String, Object> upload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>(16);
        try {
            // 先保存文件到磁盘，成功后再写入数据库，前台是一个一个文件的调用
            zlgjWTService.saveUploadFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return modelMap;
    }

    @RequestMapping("files")
    @ResponseBody
    public List<JSONObject> getFiles(HttpServletRequest request, HttpServletResponse response) {
        List<JSONObject> fileInfos = zlgjWTService.getFileListByTypeId(request);
        return fileInfos;
    }

    @RequestMapping("zlgjPdfPreview")
    public ResponseEntity<byte[]> zlgjPdfPreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("zlgjFilePathBase_preview");
        return rdmZhglFileManager.pdfPreviewOrDownLoad(fileName, fileId, formId, fileBasePath);
    }

    @RequestMapping("zlgjOfficePreview")
    @ResponseBody
    public void zlgjOfficePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("zlgjFilePathBase_preview");
        rdmZhglFileManager.officeFilePreview(fileName, fileId, formId, fileBasePath, response);
    }

    @RequestMapping("zlgjImagePreview")
    @ResponseBody
    public void zlgjImagePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("zlgjFilePathBase_preview");
        rdmZhglFileManager.imageFilePreview(fileName, fileId, formId, fileBasePath, response);
    }

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
            String mainId = RequestUtil.getString(request, "mainId");
            String fileBasePath = "";
            if (StringUtils.isNotBlank(mainId)) {
                fileBasePath = "preview".equalsIgnoreCase(action) ? WebAppUtil.getProperty("zlgjFilePathBase_preview")
                    : WebAppUtil.getProperty("zlgjFilePathBase");
            } else {
                fileBasePath = "preview".equalsIgnoreCase(action) ? WebAppUtil.getProperty("zlgjFilePathBase_preview")
                    : WebAppUtil.getProperty("zlgjFilePathBase");
            }
            if (StringUtils.isBlank(fileBasePath)) {
                logger.error("操作失败，找不到存储根路径");
                return null;
            }
            String suffix = CommonFuns.toGetFileSuffix(fileName);
            String relativeFilePath = "";
            if (StringUtils.isNotBlank(mainId)) {
                relativeFilePath = File.separator + mainId;
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

    @RequestMapping("deleteFiles")
    public void deleteFiles(HttpServletRequest request, HttpServletResponse response) {
        String mainId = RequestUtil.getString(request, "mainId");
        String id = RequestUtil.getString(request, "id");
        String fileName = RequestUtil.getString(request, "fileName");
        String suffix = CommonFuns.toGetFileSuffix(fileName);
        zlgjWTService.deleteFileOnDisk(mainId, id, suffix);
        Map<String, Object> fileParams = new HashMap<>(16);
        List<String> fileIds = new ArrayList<>();
        fileIds.add(id);
        fileParams.put("fileIds", fileIds);
        zlgjWTDao.deleteFileByIds(fileParams);
    }

    @RequestMapping("getReasonList")
    @ResponseBody
    public List<JSONObject> getReasonList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String wtId = RequestUtil.getString(request, "ids");
        return zlgjWTService.getReasonList(wtId);
    }

    @RequestMapping("getLscsList")
    @ResponseBody
    public List<JSONObject> getLscsList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String wtId = RequestUtil.getString(request, "ids");
        return zlgjWTService.getLscsList(wtId, "");
    }

    @RequestMapping("getyzList")
    @ResponseBody
    public List<JSONObject> getyzList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String wtId = RequestUtil.getString(request, "ids");
        return zlgjWTService.getyzList(wtId, "");
    }

    @RequestMapping("getfayzList")
    @ResponseBody
    public List<JSONObject> getfayzList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String wtId = RequestUtil.getString(request, "ids");
        return zlgjWTService.getfayzList(wtId, "");
    }

    @RequestMapping("getfajjList")
    @ResponseBody
    public List<JSONObject> getfajjList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String wtId = RequestUtil.getString(request, "ids");
        return zlgjWTService.getfajjList(wtId, "");
    }

    @RequestMapping("getRiskUserList")
    @ResponseBody
    public List<JSONObject> getRiskUserList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String wtId = RequestUtil.getString(request, "ids");
        return zlgjWTService.getRiskUserList(wtId);
    }

    @RequestMapping("getRiskList")
    @ResponseBody
    public List<JSONObject> getRiskList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String wtId = RequestUtil.getString(request, "ids");
        return zlgjWTService.getRiskList(wtId);
    }

    // 查询下拉框数据
    @RequestMapping("reasonListQuery")
    @ResponseBody
    public List<JSONObject> reasonListQuery(HttpServletRequest request, HttpServletResponse response) {
        String uIdStr = RequestUtil.getString(request, "ids");
        String[] ids = uIdStr.split(",");
        return zlgjWTService.reasonList(ids);
    }

    @RequestMapping("callBackOut")
    @ResponseBody
    public JsonResult callBackOut(HttpServletRequest request, HttpServletResponse response) {
        String wtId = RequestUtil.getString(request, "wtId");
        return zlgjWTService.zlgjWtCallBack(wtId);
    }

    @RequestMapping("checkFile")
    @ResponseBody
    public JsonResult checkFile(HttpServletRequest request, HttpServletResponse response) {
        String wtId = RequestUtil.getString(request, "wtId");
        String checkType = RequestUtil.getString(request, "checkType");
        String rounds = RequestUtil.getString(request, "rounds");
        return zlgjWTService.checkFile(wtId, checkType, rounds);
    }

    @RequestMapping("judgeRes")
    @ResponseBody
    public JSONObject judgeRes(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        result.put("result", true);
        String resId = RequestUtil.getString(request, "resId", "");
        String leaderId = RequestUtil.getString(request, "leaderId", "");
        zlgjWTService.judgeRes(result, resId, leaderId);
        return result;
    }

    @RequestMapping("checkNoFile")
    @ResponseBody
    public JsonResult checkNoFile(HttpServletRequest request, HttpServletResponse response) {
        String wtId = RequestUtil.getString(request, "wtId");
        return zlgjWTService.checkNoFile(wtId);
    }

    @RequestMapping("listPageCq")
    public ModelAndView listPageCq(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "zlgjNPI/zlgjListCq.jsp";
        String wtlxtype = RequestUtil.getString(request, "wtlxtype", "");
        String zrType = RequestUtil.getString(request, "zrType", "");
        String create_startTime = RequestUtil.getString(request, "create_startTime", "");
        String create_endTime = RequestUtil.getString(request, "create_endTime", "");
        String deptName = RequestUtil.getString(request, "deptName", "");
        String chartType = RequestUtil.getString(request, "chartType", "");
        String czxpj = RequestUtil.getString(request, "czxpj", "");

        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("zrType", zrType);
        mv.addObject("wtlxtype", wtlxtype);
        mv.addObject("create_startTime", create_startTime);
        mv.addObject("create_endTime", create_endTime);
        mv.addObject("deptName", deptName);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        // chartType
        mv.addObject("chartType", chartType);
        mv.addObject("czxpj", czxpj);
        Map<String, Object> params = new HashMap<>();
        params.put("userId", ContextUtil.getCurrentUserId());
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        // 返回当前登录人角色信息
        List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params);
        JSONArray userRolesJsonArray = XcmgProjectUtil.convertListMap2JsonArrObject(currentUserRoles);
        mv.addObject("currentUserRoles", userRolesJsonArray);
        // 是否是市场问题提交人
        boolean isScwtUser = false;
        // 是否是海外问题提交人
        boolean isHwwtUser = false;
        if (RdmConst.ZLBZB_NAME.equalsIgnoreCase(ContextUtil.getCurrentUser().getMainGroupName())) {
            isScwtUser = true;
            isHwwtUser = true;
        } else {
            for (int i = 0; i < currentUserRoles.size(); i++) {
                Map<String, Object> map = currentUserRoles.get(i);
                if ("市场问题提交人".equals(map.get("NAME_"))) {
                    isScwtUser = true;
                }
                if ("海外问题提交人".equals(map.get("NAME_"))) {
                    isHwwtUser = true;
                }
            }
        }

        mv.addObject("isScwtUser", true);
        mv.addObject("isHwwtUser", isHwwtUser);
        // 返回当前用户是否是技术管理部负责人
        boolean isJsglbRespUser = rdmZhglUtil.judgeUserIsJsglbRespUser(ContextUtil.getCurrentUserId());
        mv.addObject("isJsglbRespUser", isJsglbRespUser);
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        boolean isBgry = rdmZhglUtil.judgeIsPointRoleUser(ContextUtil.getCurrentUserId(), "质量改进变更人员");
        mv.addObject("isBgry", isBgry);
        String yzcd = RequestUtil.getString(request, "yzcd", "");
        mv.addObject("yzcd", yzcd);
        return mv;
    }

    @RequestMapping("getZlgjListCq")
    @ResponseBody
    public JsonPageResult<?> getZlgjListCq(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> params = new HashMap<>();
        return zlgjWTService.getZlgjListCq(request, response, true, params, true, "zwt.CREATE_TIME_");
    }

    // 导出质量改进问题
    @PostMapping("exportWtsbCqExcel")
    public void exportWtsbCqExcel(HttpServletResponse response, HttpServletRequest request) {
        zlgjWTService.exportWtsbCqExcel(response, request);
    }

    @RequestMapping("manualSkipFirst")
    @ResponseBody
    public JsonResult manualSkipFirst(HttpServletRequest request, HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "skip first success");
        String instId = RequestUtil.getString(request, "instId");
        try {
            List<BpmTask> bpmTaskList = bpmTaskManager.getByInstId(instId);
            ProcessNextCmd processNextCmd = new ProcessNextCmd();
            processNextCmd.setTaskId(bpmTaskList.get(0).getId());
            processNextCmd.setJumpType("AGREE");
            processNextCmd.setJsonData(new JSONObject().toJSONString());
            processNextCmd.setAgentToUserId("1");
            bpmTaskManager.doNext(processNextCmd);
        } catch (Exception e) {
            logger.error("Exception in manualSkipFirst", e);
            result.setSuccess(false);
            result.setMessage("系统异常");
        }
        return result;
    }

    /**
     * 向PDM创建PR
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("createPr2Pdm")
    @ResponseBody
    public JsonResult createPr2Pdm(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JsonResult result = new JsonResult(true, "创建成功");
        String wtId = RequestUtil.getString(request, "wtId");
        if (StringUtils.isBlank(wtId)) {
            result.setSuccess(false);
            result.setMessage("问题单ID为空");
            return result;
        }
        try {
            zlgjWTService.createPr2Pdm(wtId, result);
        } catch (Exception e) {
            logger.error("Exception in createPr2Pdm", e);
            result.setSuccess(false);
            result.setMessage("操作失败，系统异常");
        }
        return result;
    }

    /**
     * 从PDM查询PR
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("queryPrFromPdm")
    @ResponseBody
    public JsonResult queryPrFromPdm(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JsonResult result = new JsonResult(true, "创建成功");
        String wtId = RequestUtil.getString(request, "wtId");
        if (StringUtils.isBlank(wtId)) {
            result.setSuccess(false);
            result.setMessage("问题单ID为空");
            return result;
        }
        try {
            zlgjWTService.queryPrFromPdm(wtId, result);
        } catch (Exception e) {
            logger.error("Exception in queryPrFromPdm", e);
            result.setSuccess(false);
            result.setMessage("系统异常");
        }
        return result;
    }
}
