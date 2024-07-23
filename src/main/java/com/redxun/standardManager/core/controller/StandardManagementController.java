package com.redxun.standardManager.core.controller;

import java.io.File;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.entity.TaskExecutor;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.org.api.model.IUser;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.rdmCommon.core.manager.LoginRecordManager;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.serviceEngineering.core.service.MaintainabilityEvaluationFormScript;
import com.redxun.standardManager.core.dao.StandardManagementDao;
import com.redxun.standardManager.core.manager.JsStandardManager;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.standardManager.core.util.StandardConstant;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

@Controller
@RequestMapping("/standardManager/core/standardManagement/")
public class StandardManagementController {
    private Logger logger = LoggerFactory.getLogger(StandardManagementController.class);
    @Autowired
    private JsStandardManager jsStandardManager;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;

    @Autowired
    private StandardManagementDao standardManagementDao;

    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;

    @Autowired
    private LoginRecordManager loginRecordManager;

    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private MaintainabilityEvaluationFormScript maintainabilityEvaluationFormScript;
    @Autowired
    private CommonBpmManager commonBpmManager;

    @RequestMapping("jsStandardListPage")
    public ModelAndView listPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "standardManager/core/jsStandardManagementList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        IUser currentUser = ContextUtil.getCurrentUser();
        mv.addObject("currentUser", currentUser);
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        // 返回当前登录人的权限（角色） 是否是标准认证研究所
        boolean isBzrzs = false;
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        params.put("groupId", ContextUtil.getCurrentUser().getMainGroupId());
        List<Map<String, String>> depParentInfos = xcmgProjectOtherDao.queryDepParentById(params);
        if (depParentInfos.size() > 0) {
            if (RdmConst.BZJSS_NAME.equals(depParentInfos.get(0).get("NAME_"))) {
                isBzrzs = true;
            }
        }
        mv.addObject("isBzrzs", isBzrzs);
        // 是否是分管领导
        boolean isFGLD = rdmZhglUtil.judgeUserIsFgld(ContextUtil.getCurrentUserId());
        mv.addObject("isFGLD", isFGLD);
        // 是否是挖掘机械研究院的所长
        boolean isJszxsz = false;
        List<TaskExecutor> users = (List<TaskExecutor>)maintainabilityEvaluationFormScript.getReviewingUsers();
        for (TaskExecutor user : users) {
            if (ContextUtil.getCurrentUserId().equals(user.getId())) {
                isJszxsz = true;
                break;
            }
        }
        mv.addObject("isJszxsz", isJszxsz);

        // 返回当前登录人角色信息
        params.put("userId", ContextUtil.getCurrentUserId());
        List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params);

        // 判断职级别，师三级及以上职级才可以新增
        boolean isZJConform = false;
        List<Map<String, Object>> currentUserZJ = xcmgProjectOtherDao.queryUserZJ(params);
        if (currentUserZJ != null && !currentUserZJ.isEmpty()) {
            if ((int)currentUserZJ.get(0).get("rankNum") > 3) {
                isZJConform = true;
            }
        }
        mv.addObject("isZJConform", isZJConform);

        // 当前用户是否可以发起任务
        boolean canAddApply = false;
        for (int i = 0; i < currentUserRoles.size(); i++) {
            Map<String, Object> map = currentUserRoles.get(i);
            if (RdmConst.NOT_BZS_ADD_APPLY.equals(map.get("NAME_"))) {
                canAddApply = true;
                break;
            }
        }

        //动态标准作废
        boolean isZfAdmin = rdmZhglUtil.judgeIsPointRoleUser(ContextUtil.getCurrentUserId(), "标准动态作废管理员");
        mv.addObject("isZfAdmin", isZfAdmin);

        mv.addObject("canAddApply", canAddApply);
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        return mv;
    }

    @RequestMapping("jsStandardList")
    @ResponseBody
    public JsonPageResult<?> demandList(HttpServletRequest request, HttpServletResponse response) {
        return jsStandardManager.standardApplyList(request, response);
    }

    @RequestMapping("jsStandardDelete")
    @ResponseBody
    public JsonResult demandDelete(HttpServletRequest request, HttpServletResponse response) {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String instIdStr = RequestUtil.getString(request, "instIds");
            if (StringUtils.isNotEmpty(uIdStr) && StringUtils.isNotEmpty(instIdStr)) {
                String[] ids = uIdStr.split(",");
                String[] instIds = instIdStr.split(",");
                return jsStandardManager.deleteApply(ids, instIds);
            }
        } catch (Exception e) {
            logger.error("Exception in demandDelete", e);
            return new JsonResult(false, e.getMessage());
        }
        return new JsonResult(true, "成功删除!");
    }

    @RequestMapping("jsStandardManagementEdit")
    public ModelAndView jsDemandEdit(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "standardManager/core/jsStandardManagementEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        // 新增或者编辑页面由流程引擎跳转并替换url中的pk和nodeId_
        // 如果传过来standardid
        String formId = RequestUtil.getString(request, "formId");

        String standardId = RequestUtil.getString(request, "standardId");
        if (standardId != null && StringUtils.isBlank(formId)) {

            formId = xcmgProjectOtherDao.queryFormIdbyStandard(standardId);

        }

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
        // 取出节点变量，返回到页面
        if (StringUtils.isNotBlank(nodeId)) {
            mv.addObject("nodeId", nodeId);
            List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId, "JSBZDTGL", null);
            if (nodeVars != null && !nodeVars.isEmpty()) {
                mv.addObject("nodeVars", JSONObject.toJSONString(nodeVars));
                for (Map<String, String> nodeVar : nodeVars) {
                    if ("isZqyj".equals(nodeVar.get("KEY_"))) {
                        mv.addObject("isZqyj", nodeVar.get("DEF_VAL_"));
                    }
                }
            }
        }

        mv.addObject("action", action);
        JSONObject formObj = new JSONObject();
        if (StringUtils.isNotBlank(formId)) {
            formObj = jsStandardManager.queryDemandDetail(formId);
        } else {
            IUser user = ContextUtil.getCurrentUser();
            formObj.put("applyUserId", user.getUserId());
            formObj.put("applyUserName", user.getFullname());
            formObj.put("applyUserPhone", user.getMobile());
            formObj.put("applyUserDeptName", user.getMainGroupName());
            formObj.put("feedbackType", "need");
        }
        mv.addObject("standardDemandObj", formObj);
        mv.addObject("applyId", formId);

        // 返回当前登录人的权限（角色） 是否是标准技术所
        JSONObject isBzrzsObj = loginRecordManager.isBzrzs(ContextUtil.getCurrentUser().getMainGroupId());
        boolean isBzrzs = isBzrzsObj.getBooleanValue("isBzrzs");
        mv.addObject("isBzrzs", isBzrzs);
        // 查询登陆人是否是申请人、标准牵头人、主要起草人、标准所对接人、征求意见人和标准所人员
        List<String> userList = new ArrayList<>();
        JSONObject jsonObject = standardManagementDao.selectRight(ContextUtil.getCurrentUserId(), formId);
        if (jsonObject != null) {
            if (StringUtils.isNotBlank(jsonObject.getString("standardLeaderId"))) {
                userList.add(jsonObject.getString("standardLeaderId"));
            }
            if (StringUtils.isNotBlank(jsonObject.getString("CREATE_BY_"))) {
                userList.add(jsonObject.getString("CREATE_BY_"));
            }
            if (StringUtils.isNotBlank(jsonObject.getString("mainDraftIds"))) {
                String[] mainDraftIds = jsonObject.getString("mainDraftIds").split(",");
                for (String name : Arrays.asList(mainDraftIds)) {
                    userList.add(name);
                }
            }
            if (StringUtils.isNotBlank(jsonObject.getString("standardPeopleId"))) {
                String[] standardPeopleId = jsonObject.getString("standardPeopleId").split(",");
                for (String name : Arrays.asList(standardPeopleId)) {
                    userList.add(name);
                }
            }
            if (StringUtils.isNotBlank(jsonObject.getString("yjUserIds"))) {
                String[] yjUserIds = jsonObject.getString("yjUserIds").split(",");
                for (String name : Arrays.asList(yjUserIds)) {
                    userList.add(name);
                }
            }
        }
        if (userList.contains(ContextUtil.getCurrentUserId()) || isBzrzs) {
            mv.addObject("isOperation", true);
        } else {
            mv.addObject("isOperation", false);
        }
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        IUser currentUser = ContextUtil.getCurrentUser();
        mv.addObject("currentUserName", currentUser.getFullname());
        mv.addObject("status", status);
        return mv;
    }

    /**
     * 草案附件上传
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("fileUploadWindow")
    public ModelAndView fileUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "standardManager/core/standardMegUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("applyId", RequestUtil.getString(request, "applyId", ""));
        String stageKey = RequestUtil.getString(request, "stageKey", "");
        String applyId = RequestUtil.getString(request, "applyId", "");
        List<JSONObject> standardFileTypes = jsStandardManager.queryStandardFileTypes(stageKey);
        mv.addObject("typeInfos", standardFileTypes);
        mv.addObject("stageKey", stageKey);
        return mv;
    }

    /**
     * 查询附件列表
     * 
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("getStandardFileList")
    @ResponseBody
    public List<JSONObject> getStandardFileList(HttpServletRequest request, HttpServletResponse response) {
        List<String> standardIdList = Arrays.asList(RequestUtil.getString(request, "applyId", ""));
        String stageKey = RequestUtil.getString(request, "stageKey", "");
        return jsStandardManager.getStandardFileList(standardIdList, stageKey);
    }

    @RequestMapping(value = "fileUpload")
    @ResponseBody
    public Map<String, Object> fileUpload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        try {
            // 先保存文件，成功后再写入数据库，前台是一个一个文件的调用
            jsStandardManager.saveUploadFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            logger.error("Exception in upload", e);
            modelMap.put("success", "false");
        }
        return modelMap;
    }

    /**
     * 征求意见查询
     * 
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("getStandardList")
    @ResponseBody
    public List<JSONObject> getStandardList(HttpServletRequest request, HttpServletResponse response) {
        String applyId = RequestUtil.getString(request, "applyId");
        String isZqyj = RequestUtil.getString(request, "isZqyj");
        return jsStandardManager.getStandardList(applyId, isZqyj);
    }

    /**
     * 团队成员意见查询
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("getTeamDraftList")
    @ResponseBody
    public List<JSONObject> getTeamDraftList(HttpServletRequest request, HttpServletResponse response) {
        String applyId = RequestUtil.getString(request, "applyId");
        String isMkfzr = RequestUtil.getString(request, "isMkfzr");
        return jsStandardManager.getTeamDraftList(applyId, isMkfzr);
    }

    // 用户新增页面 标准新增和编辑页面
    @GetMapping("editUser")
    public ModelAndView editUser(HttpServletRequest request, HttpServletResponse response) {
        // ModelAndView mv = getPathView(request);
        String jspPath = "standardManager/core/jsStandardPublicEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String applyId = RequestUtil.getString(request, "applyId");
        String standardId = RequestUtil.getString(request, "standardId");
        String action = RequestUtil.getString(request, "action");
        String isZqyj = RequestUtil.getString(request, "isZqyj");
        String isPsg = RequestUtil.getString(request, "isPsg");

        // 根据Id查询用户信息
        JSONObject standardObj = new JSONObject();
        if (StringUtils.isNotBlank(standardId)) {
            Map<String, Object> standardInfo = standardManagementDao.getStandard(standardId);
            standardObj = XcmgProjectUtil.convertMap2JsonObject(standardInfo);
        }
        mv.addObject("standardObj", standardObj);
        mv.addObject("action", action);
        mv.addObject("isPsg", isPsg);
        mv.addObject("isZqyj", isZqyj);
        mv.addObject("applyId", RequestUtil.getString(request, "applyId"));

        mv.addObject("currentUser", ContextUtil.getCurrentUser());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        mv.addObject("systemCategoryId",
            RequestUtil.getString(request, "systemCategoryId", StandardConstant.SYSTEMCATEGORY_JS));

        return mv;
    }

    // 标准保存（包括新增、编辑）
    @RequestMapping("savePublic")
    @ResponseBody
    public JsonResult savePublic(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        jsStandardManager.savePublicStandard(result, request);
        return new JsonResult(true, "操作成功");
    }

    @RequestMapping("deletePublic")
    @ResponseBody
    public JsonResult deletePublic(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {

            String id = RequestUtil.getString(request, "id");

            Map<String, Object> param = new HashMap<>();
            param.put("id", id);

            standardManagementDao.delUserListById(param);

        } catch (Exception e) {
            logger.error("Exception in deleteJsmm", e);
            return new JsonResult(false, e.getMessage());
        }

        return new JsonResult(true, "操作成功");
    }

    @RequestMapping("standardPdfPreview")
    public ResponseEntity<byte[]> standardPdfPreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        // String fileId = njjdDao.selectFjIdByNjBaseId(formId);
        String fileBasePath = WebAppUtil.getProperty("standardMegFilePathBase");
        return rdmZhglFileManager.pdfPreviewOrDownLoad(fileName, fileId, formId, fileBasePath);
    }

    @RequestMapping("standardOfficePreview")
    @ResponseBody
    public void standardOfficePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String formId = RequestUtil.getString(request, "formId");
        String id = RequestUtil.getString(request, "id");
        // String fileId = njjdDao.selectFjIdByNjBaseId(id);
        String fileId = RequestUtil.getString(request, "fileId");
        String fileBasePath = WebAppUtil.getProperty("standardMegFilePathBase");
        rdmZhglFileManager.officeFilePreview(fileName, fileId, formId, fileBasePath, response);
    }

    @RequestMapping("standardImagePreview")
    @ResponseBody
    public void standardImagePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        // String fileId = njjdDao.selectFjIdByNjBaseId(formId);
        String fileBasePath = WebAppUtil.getProperty("standardMegFilePathBase");
        rdmZhglFileManager.imageFilePreview(fileName, fileId, formId, fileBasePath, response);
    }

    // 文档的下载（预览pdf也调用这里）
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
                logger.error("操作失败，文件Id为空！");
                return null;
            }
            String jsjlId = RequestUtil.getString(request, "applyId");

            if (jsjlId == null || "".equals(jsjlId)) {
                jsjlId = standardManagementDao.selectStandardByFjId(fileId);
            }
            if (StringUtils.isBlank(jsjlId)) {
                logger.error("操作失败，技术交流Id为空！");
                return null;
            }
            // 预览还是下载
            String action = RequestUtil.getString(request, "action");
            if (StringUtils.isBlank(action)) {
                logger.error("操作类型为空");
                return null;
            }
            String fileBasePath = WebAppUtil.getProperty("standardMegFilePathBase");
            if (StringUtils.isBlank(fileBasePath)) {
                logger.error("操作失败，找不到存储路径");
                return null;
            }
            String suffix = CommonFuns.toGetFileSuffix(fileName);
            String fullFilePath = fileBasePath + File.separator + jsjlId + File.separator + fileId + "." + suffix;
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

    /**
     * 附件删除
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("deleteBookMeg")
    @ResponseBody
    public JsonResult deleteBookMeg(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {

            String applyId = RequestUtil.getString(request, "applyId");

            String id = RequestUtil.getString(request, "id");

            String fileName = RequestUtil.getString(request, "fileName");

            // 删除附件
            Map<String, Object> param = new HashMap<>();
            param.put("id", id);
            String standardFilePathBase = WebAppUtil.getProperty("standardMegFilePathBase");

            // 查询附件id
            // String fjId = njjdDao.selectFjId(id);
            // 处理下载目录的删除
            String suffix = CommonFuns.toGetFileSuffix(fileName);
            String fileFullPath = standardFilePathBase + File.separator + applyId + File.separator + id + '.' + suffix;
            File file = new File(fileFullPath);
            file.delete();

            // 预览删除
            String convertPdfDir = WebAppUtil.getProperty("convertPdfDir");
            String convertPdfPath = standardFilePathBase + File.separator + applyId + File.separator + File.separator
                + convertPdfDir + File.separator + id + ".pdf";
            File pdffile = new File(convertPdfPath);
            pdffile.delete();

            standardManagementDao.delStandardById(param);

        } catch (Exception e) {
            logger.error("Exception in deleteJsmm", e);
            return new JsonResult(false, e.getMessage());
        }

        return new JsonResult(true, "操作成功");
    }

    @RequestMapping("getSsyjList")
    @ResponseBody
    public List<JSONObject> getSsyjList(HttpServletRequest request, HttpServletResponse response) {
        String applyId = RequestUtil.getString(request, "applyId");
        return jsStandardManager.getSsyjList(applyId);
    }

    @RequestMapping("revisePlanPage")
    public ModelAndView revisePlanPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "standardManager/core/standardRevisePlan.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        // 是否是技术标准管理人员
        boolean isOperator = rdmZhglUtil.judgeIsPointRoleUser(ContextUtil.getCurrentUserId(), "技术标准管理人员");
        // 超级管理员
        boolean isAdmin = Objects.equals(ContextUtil.getCurrentUser().getUserNo(), "admin");
        mv.addObject("isOperator", isOperator || isAdmin);
        return mv;
    }

    @RequestMapping("revisePlanList")
    @ResponseBody
    public List<JSONObject> revisePlanList(HttpServletRequest request) {
        return jsStandardManager.revisePlanList(request);
    }

    @RequestMapping(value = "revisePlanSave")
    @ResponseBody
    public JsonResult revisePlanSave(HttpServletRequest request, @RequestBody String requestBody) {
        return jsStandardManager.revisePlanSave(requestBody);
    }

    @RequestMapping("reviseOverviewPage")
    public ModelAndView reviseOverviewPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "standardManager/report/standardReviseOverview.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        return mv;
    }

    @RequestMapping("queryReviseOverviewByDept")
    @ResponseBody
    public JsonResult queryReviseOverviewByDept(HttpServletRequest request) {
        return jsStandardManager.queryReviseOverviewByDept(request);
    }

    @RequestMapping("queryYearOverviewByDept")
    @ResponseBody
    public JsonResult queryYearOverviewByDept(HttpServletRequest request) {
        return jsStandardManager.queryYearOverviewByDept(request);
    }

    @RequestMapping("queryJdOverviewByDept")
    @ResponseBody
    public JsonResult queryJdOverviewByDept(HttpServletRequest request) {
        return jsStandardManager.queryJdOverviewByDept(request);
    }

    @RequestMapping("queryDept")
    @ResponseBody
    public JsonPageResult<?> queryDept(HttpServletRequest request) {
        return jsStandardManager.queryDept(request);
    }

    @RequestMapping("saveStandard")
    @ResponseBody
    public JsonResult saveJsmm(HttpServletRequest request, @RequestBody String xcmgProjectStr,
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
            if (StringUtils.isBlank(formDataJson.getString("id"))) {
                jsStandardManager.createStandardApply(formDataJson);
            } else {
                jsStandardManager.updateStandardApply(formDataJson);
            }
        } catch (Exception e) {
            logger.error("Exception in save Standard");
            result.setSuccess(false);
            result.setMessage("Exception in save Standard");
            return result;
        }
        return result;
    }
}
