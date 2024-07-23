package com.redxun.rdmZhgl.core.controller;

import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.rdmCommon.core.manager.LoginRecordManager;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmZhgl.core.dao.JstbDao;
import com.redxun.rdmZhgl.core.service.JstbService;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.controller.GenericController;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.sys.org.entity.OsGroup;
import com.redxun.sys.org.manager.OsGroupManager;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

@Controller
@RequestMapping("/rdmZhgl/Jstb/")
public class JstbController extends GenericController {
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private JstbService jstbService;
    @Autowired
    private JstbDao jstbDao;
    @Resource
    private OsGroupManager osGroupManager;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private LoginRecordManager loginRecordManager;
    @Autowired
    private CommonBpmManager commonBpmManager;

    @RequestMapping("openUploadWindow")
    public ModelAndView openUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/JstbFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("jstbId", RequestUtil.getString(request, "jstbId", ""));
        return mv;
    }

    @RequestMapping("fileList")
    public ModelAndView fileList(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "rdmZhgl/core/jstbFileList.jsp";
        String jstbId = RequestUtil.getString(request, "jstbId");
        ModelAndView mv = new ModelAndView(jspPath);
        Map<String, Object> params = new HashMap<>();
        boolean isJSGLBUser = false;
        params.put("currentUserId", ContextUtil.getCurrentUserId());
        List<JSONObject> isJSGLB = jstbDao.isJSGLB(params);
        for (JSONObject jstb : isJSGLB) {
            if (jstb.getString("deptname").equals(RdmConst.JSGLB_NAME)) {
                isJSGLBUser = true;
                break;
            }
        }
        mv.addObject("jstbId", jstbId);
        mv.addObject("isJSGLBUser", isJSGLBUser);
        mv.addObject("currentUser", ContextUtil.getCurrentUser());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        return mv;
    }

    @RequestMapping("readStatusPage")
    public ModelAndView readStatusPage(HttpServletRequest request, HttpServletResponse response) {
        String jstbId = RequestUtil.getString(request, "jstbId");
        String jspPath = "rdmZhgl/core/jstbReadStatusPage.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        List<Map<String, Object>> tempData = null;
        Map<String, Object> params = new HashMap<>(16);
        params.put("jstbIds", Arrays.asList(jstbId));
        tempData = jstbDao.queryReaders(params);
        if (tempData != null && !tempData.isEmpty()) {
            for (Map<String, Object> oneMsg : tempData) {
                if (oneMsg.get("firstReadTime") != null) {
                    oneMsg.put("firstReadTime", DateUtil.formatDate((Date)oneMsg.get("firstReadTime"), "yyyy-MM-dd"));
                }
            }
        }
        JSONArray jsonArray = XcmgProjectUtil.convertListMap2JsonArrObject(tempData);
        mv.addObject("gridData", jsonArray);
        return mv;
    }

    @RequestMapping("JstbListPage")
    public ModelAndView msgManagement(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "rdmZhgl/core/jstbList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        JSONObject isJSZX = loginRecordManager.judgeIsJSZX(ContextUtil.getCurrentUser().getMainGroupId(),
            ContextUtil.getCurrentUser().getMainGroupName());
        boolean isJSZXUser = false;
        if (isJSZX.getString("isJSZX").equals("true")) {
            isJSZXUser = true;
        }
        Map<String, Object> params = new HashMap<>();
        boolean isJSGLBUser = false;
        params.put("currentUserId", ContextUtil.getCurrentUserId());
        List<JSONObject> isJSGLB = jstbDao.isJSGLB(params);
        for (JSONObject jstb : isJSGLB) {
            if (jstb.getString("deptname").equals(RdmConst.JSGLB_NAME)) {
                isJSGLBUser = true;
                break;
            }
        }
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        Map<String, Object> params2 = new HashMap<>();
        params2.put("userId", ContextUtil.getCurrentUserId());
        params2.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params2);
        JSONArray userRolesJsonArray = XcmgProjectUtil.convertListMap2JsonArrObject(currentUserRoles);
        mv.addObject("currentUserRoles", userRolesJsonArray);
        // 返回当前用户是否是技术管理部负责人
        boolean isJsglbRespUser = rdmZhglUtil.judgeUserIsJsglbRespUser(ContextUtil.getCurrentUserId());
        mv.addObject("isJsglbRespUser", isJsglbRespUser);
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("isJSZXUser", isJSZXUser);
        mv.addObject("isJSGLBUser", isJSGLBUser);
        return mv;
    }

    @RequestMapping("editPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/jstbEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        // 新增或者编辑页面由流程引擎跳转并替换url中的pk和nodeId_
        String jstbId = RequestUtil.getString(request, "jstbId");
        String nodeId = RequestUtil.getString(request, "nodeId");
        String action = RequestUtil.getString(request, "action");
        String status = RequestUtil.getString(request, "status");
        if (StringUtils.isBlank(action)) {
            // 新增或编辑的时候没有nodeId
            if (StringUtils.isBlank(nodeId) || nodeId.contains("PROCESS")) {
                action = "edit";
            } else {
                // 处理任务的时候有nodeId
                action = "task";
            }
        }
        mv.addObject("jstbId", jstbId).addObject("action", action).addObject("status", status);
        // 取出节点变量，返回到页面
        if (StringUtils.isNotBlank(nodeId)) {
            mv.addObject("nodeId", nodeId);
            List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId, "JSGLTB", null);
            if (nodeVars != null && !nodeVars.isEmpty()) {
                mv.addObject("nodeVars", JSONObject.toJSONString(nodeVars));
            }
        }
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        return mv;
    }

    @RequestMapping("queryJstb")
    @ResponseBody
    public JsonPageResult<?> queryJstb(HttpServletRequest request, HttpServletResponse response) {
        return jstbService.queryJstb(request, true);
    }

    @RequestMapping("deleteJstb")
    @ResponseBody
    public JsonResult deleteJstb(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return jstbService.deleteJstb(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteJstb", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("cancelJstb")
    @ResponseBody
    public JsonResult cancelJstb(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return jstbService.cancelJstb(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteJstb", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("see")
    public ModelAndView msgSee(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "rdmZhgl/core/Jstbsee.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String jstbId = RequestUtil.getString(request, "jstbId");
        Map<String, Object> oneMessage = null;
        if (StringUtils.isNotBlank(jstbId)) {
            Map<String, Object> params = new HashMap<>();
            params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            params.put("jstbId", jstbId);
            params.put("userId", ContextUtil.getCurrentUserId());

            List<JSONObject> messageInfos = jstbDao.queryJstb(params);
            if (messageInfos != null && !messageInfos.isEmpty()) {
                for (Map<String, Object> oneMsg : messageInfos) {
                    if (oneMsg.get("CREATE_TIME_") != null) {
                        oneMsg.put("CREATE_TIME_", DateUtil.formatDate((Date)oneMsg.get("CREATE_TIME_"), "yyyy-MM-dd"));
                    }
                }
                oneMessage = messageInfos.get(0);
                String userDepFullName = "";
                // 查找创建人的部门
                OsGroup mainDep = osGroupManager.getMainDeps(oneMessage.get("CREATE_BY_").toString(),
                    ContextUtil.getCurrentTenantId());
                if (mainDep != null) {
                    // 获取部门的全路径
                    String fullName = osGroupManager.getGroupFullPathNames(mainDep.getGroupId());
                    if (fullName.contains("-")) {
                        String[] fullNames = fullName.split("[-]");
                        for (int i = 1; i < fullNames.length; i++) {
                            if (StringUtil.isEmpty(userDepFullName)) {
                                userDepFullName = fullNames[i];
                            } else {
                                userDepFullName += "-" + fullNames[i];
                            }
                        }
                    } else {
                        userDepFullName = fullName;
                    }
                }
                oneMessage.put("creatorDepFullName", userDepFullName);
            }
        }
        JSONObject messageJOSNObject = XcmgProjectUtil.convertMap2JsonObject(oneMessage);
        mv.addObject("messageObj", messageJOSNObject);
        mv.addObject("jstbId", jstbId);
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        Map<String, Object> params = new HashMap<>();
        params.put("jstbId", jstbId);
        JSONObject jstbStatus = jstbDao.queryJstbStatus(params);
        if (jstbStatus.getString("status").equals("发布")) {
            JSONObject readers = new JSONObject();
            String currentUserId = ContextUtil.getCurrentUserId();
            readers.put("id", IdUtil.getId());
            readers.put("jstbIds", Arrays.asList(jstbId));
            readers.put("readUserId", currentUserId);
            readers.put("firstReadTime", new Date());
            List<Map<String, Object>> readersId = jstbDao.queryReaders(readers);
            if (readersId.isEmpty()) {
                readers.put("jstbId", jstbId);
                jstbDao.addFileReaders(readers);
            }
        }
        return mv;
    }

    @RequestMapping("saveJstb")
    @ResponseBody
    public JsonResult saveJstb(HttpServletRequest request, @RequestBody String xcmgProjectStr,
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
            if (StringUtils.isBlank(formDataJson.getString("jstbId"))) {
                jstbService.createJstb(formDataJson);
            } else {
                jstbService.updateJstb(formDataJson);
            }
        } catch (Exception e) {
            logger.error("Exception in save jstb");
            result.setSuccess(false);
            result.setMessage("Exception in save jstb");
            return result;
        }
        return result;
    }

    @RequestMapping("getJstbDetail")
    @ResponseBody
    public JSONObject getJstbDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject jstbObj = new JSONObject();
        String jstbId = RequestUtil.getString(request, "jstbId");
        if (StringUtils.isNotBlank(jstbId)) {
            jstbObj = jstbService.getJstbDetail(jstbId);
        }
        return jstbObj;
    }

    @RequestMapping(value = "upload")
    @ResponseBody
    public Map<String, Object> upload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        try {
            // 先保存文件到磁盘，成功后再写入数据库，前台是一个一个文件的调用
            jstbService.saveJstbUploadFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            logger.error("Exception in upload", e);
            modelMap.put("success", "false");
        }
        return modelMap;
    }

    @RequestMapping("getJstbFileList")
    @ResponseBody
    public List<JSONObject> getJstbFileList(HttpServletRequest request, HttpServletResponse response) {
        List<String> jstbIdList = Arrays.asList(RequestUtil.getString(request, "jstbId", ""));
        return jstbService.getJstbFileList(jstbIdList);
    }

    @RequestMapping("delJstbFile")
    public void delJstbFile(HttpServletRequest request, HttpServletResponse response, @RequestBody String postBody) {
        JSONObject postBodyObj = JSONObject.parseObject(postBody);
        String fileName = postBodyObj.getString("fileName");
        String fileId = postBodyObj.getString("id");
        String jstbId = postBodyObj.getString("formId");
        jstbService.deleteOneJstbFile(fileId, fileName, jstbId);
    }

    @RequestMapping("jstbPdfPreview")
    public ResponseEntity<byte[]> jstbPdfPreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("jstbFilePathBase");
        return rdmZhglFileManager.pdfPreviewOrDownLoad(fileName, fileId, formId, fileBasePath);
    }

    @RequestMapping("jstbOfficePreview")
    @ResponseBody
    public void jstbOfficePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("jstbFilePathBase");
        rdmZhglFileManager.officeFilePreview(fileName, fileId, formId, fileBasePath, response);
    }

    @RequestMapping("jstbImagePreview")
    @ResponseBody
    public void jstbImagePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("jstbFilePathBase");
        rdmZhglFileManager.imageFilePreview(fileName, fileId, formId, fileBasePath, response);
    }
}
