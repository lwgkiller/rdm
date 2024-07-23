package com.redxun.standardManager.core.controller;

import java.io.File;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.rdmCommon.core.dao.CommonInfoDao;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.rdmCommon.core.manager.LoginRecordManager;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.standardManager.core.dao.StandardConfigDao;
import com.redxun.standardManager.core.dao.StandardDoCheckDao;
import com.redxun.standardManager.core.manager.StandardDoCheckManager;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

/**
 * 组件管理
 */
@Controller
@RequestMapping("/standard/core/doCheck/")
public class StandardDoCheckController {
    private static final Logger logger = LoggerFactory.getLogger(StandardDoCheckController.class);
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private StandardDoCheckManager standardDoCheckManager;
    @Resource
    private BpmInstManager bpmInstManager;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private StandardDoCheckDao standardDoCheckDao;
    @Autowired
    private StandardConfigDao standardConfigDao;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private LoginRecordManager loginRecordManager;
    @Autowired
    private CommonBpmManager commonBpmManager;
    @Autowired
    private CommonInfoDao commonInfoDao;

    @RequestMapping("applyListPage")
    public ModelAndView getListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String zjManList = "standardManager/core/standardDoCheckApplyList.jsp";
        ModelAndView mv = new ModelAndView(zjManList);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        return mv;
    }

    @RequestMapping("applyList")
    @ResponseBody
    public JsonPageResult<?> queryApplyList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return standardDoCheckManager.queryDoCheckList(request, true);
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
            return standardDoCheckManager.delApplys(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteApply", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("applyEditPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "standardManager/core/standardDoCheckApplyEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        // 新增或者编辑页面由流程引擎跳转并替换url中的pk和nodeId_
        String applyId = RequestUtil.getString(request, "applyId", "");
        String nodeId = RequestUtil.getString(request, "nodeId", "");
        String action = RequestUtil.getString(request, "action", "");
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
            List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId, "BZZXXZC", null);
            if (nodeVars != null && !nodeVars.isEmpty()) {
                mv.addObject("nodeVars", JSONObject.toJSONString(nodeVars));
            }
        }
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        mv.addObject("applyId", applyId);
        mv.addObject("action", action);
        mv.addObject("status", status);

        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        // 类别
        List<JSONObject> categoryList = new ArrayList<>();
        List<Map<String, Object>> categoryMapResults = standardConfigDao.queryStandardCategory(params);
        if (categoryMapResults == null || categoryMapResults.isEmpty()) {
            categoryMapResults = new ArrayList<>();
        } else {
            for (Map<String, Object> oneMap : categoryMapResults) {
                JSONObject oneJson = new JSONObject();
                oneJson.put("id", oneMap.get("id").toString());
                oneJson.put("categoryName", oneMap.get("categoryName").toString());
                categoryList.add(oneJson);
            }
        }
        mv.addObject("categoryList", categoryList);
        return mv;
    }

    @RequestMapping("getJson")
    @ResponseBody
    public JSONObject queryApplyJson(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = RequestUtil.getString(request, "id", "");
        if (StringUtils.isBlank(id)) {
            JSONObject result = new JSONObject();
            return result;
        }
        return standardDoCheckManager.queryApplyJson(id);
    }

    @RequestMapping("checkDetailList")
    @ResponseBody
    public List<JSONObject> checkDetailList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String applyId = RequestUtil.getString(request, "applyId", "");
        if (StringUtils.isBlank(applyId)) {
            logger.error("applyId is blank");
            return null;
        }
        JSONObject params = new JSONObject();
        params.put("baseInfoId", applyId);
        List<JSONObject> result = standardDoCheckManager.checkDetailList(params);

        // 根据当前用户角色进行过滤
        if ("admin".equalsIgnoreCase(ContextUtil.getCurrentUser().getUserNo())) {
            return result;
        }

        String currentUserId = ContextUtil.getCurrentUserId();
        // 分管领导所有
        boolean isFgld = rdmZhglUtil.judgeUserIsFgld(currentUserId);
        if (isFgld) {
            return result;
        }
        // 考核人员（如技术管理部、质量保证部相关人员）看所有
        params.clear();
        params.put("groupName", RdmConst.STANDARD_DOCHECK_NOTICE_JSZX);
        params.put("REL_TYPE_KEY_", "GROUP-USER-BELONG");
        List<Map<String, String>> usersJSZX = commonInfoDao.queryUserByGroupName(params);
        for (Map<String, String> oneUser : usersJSZX) {
            if(currentUserId.equalsIgnoreCase(oneUser.get("USER_ID_"))) {
                return result;
            }
        }
        params.put("groupName", RdmConst.STANDARD_DOCHECK_NOTICE_OTHER);
        List<Map<String, String>> usersOther = commonInfoDao.queryUserByGroupName(params);
        for (Map<String, String> oneUser : usersOther) {
            if(currentUserId.equalsIgnoreCase(oneUser.get("USER_ID_"))) {
                return result;
            }
        }

        // 过滤
        List<JSONObject> finalResult = new ArrayList<>();
        String currentUserDeptId = ContextUtil.getCurrentUser().getMainGroupId();
        // 判断是否是几个特殊节点的任务处理场景（只需要看到自己或者本部门不符合的）
        String stageName = RequestUtil.getString(request, "stageName", "");
        if (StringUtils.isNotBlank(stageName) && RdmConst.STANDARD_DOCHECK_STAGE_FILTER_ARR.contains(stageName)) {
            for (JSONObject oneDetail : result) {
                String respUserId = oneDetail.getString("respUserId");
                String closeRespUserId = oneDetail.getString("closeRespUserId");
                if (stageName.equalsIgnoreCase(RdmConst.STANDARD_DOCHECK_STAGE_ZGPLANWRITE)) {
                    // 不符合项责任人是自己，符合性判定为否
                    if (currentUserId.equalsIgnoreCase(respUserId)
                        && "否".equalsIgnoreCase(oneDetail.getString("judge"))) {
                        finalResult.add(oneDetail);
                    }
                } else if (stageName.equalsIgnoreCase(RdmConst.STANDARD_DOCHECK_STAGE_ZGPLANDEPTCHECK)) {
                    // 不符合项责任部门是本部门，符合性判定为否
                    if (currentUserDeptId.equalsIgnoreCase(oneDetail.getString("deptId"))
                        && "否".equalsIgnoreCase(oneDetail.getString("judge"))) {
                        finalResult.add(oneDetail);
                    }
                } else if (stageName.equalsIgnoreCase(RdmConst.STANDARD_DOCHECK_STAGE_ZGPLANWRITEAGAIN)) {
                    // 不符合项责任人是自己，计划确认为“不符合”
                    if (currentUserId.equalsIgnoreCase(respUserId)
                        && "不符合".equalsIgnoreCase(oneDetail.getString("confirmPlan"))) {
                        finalResult.add(oneDetail);
                    }
                } else if (stageName.equalsIgnoreCase(RdmConst.STANDARD_DOCHECK_STAGE_ZGPLANDEPTCHECKAGAIN)) {
                    // 不符合项责任部门是本部门，计划确认为“不符合”
                    if (currentUserDeptId.equalsIgnoreCase(oneDetail.getString("deptId"))
                        && "不符合".equalsIgnoreCase(oneDetail.getString("confirmPlan"))) {
                        finalResult.add(oneDetail);
                    }
                } else if (stageName.equalsIgnoreCase(RdmConst.STANDARD_DOCHECK_STAGE_ZGRESULTWRITE)) {
                    // 问题关闭责任人是自己，符合性判定为否
                    if (currentUserId.equalsIgnoreCase(closeRespUserId)
                        && "否".equalsIgnoreCase(oneDetail.getString("judge"))) {
                        finalResult.add(oneDetail);
                    }
                } else if (stageName.equalsIgnoreCase(RdmConst.STANDARD_DOCHECK_STAGE_ZGRESULTDEPTCHECK)) {
                    // 问题关闭责任部门是本部门，符合性判定为否
                    if (currentUserDeptId.equalsIgnoreCase(oneDetail.getString("closeDeptId"))
                        && "否".equalsIgnoreCase(oneDetail.getString("judge"))) {
                        finalResult.add(oneDetail);
                    }
                } else if (stageName.equalsIgnoreCase(RdmConst.STANDARD_DOCHECK_STAGE_ZGRESULTWRITEAGAIN)) {
                    // 问题关闭责任人是自己，结果确认为“待整改”
                    if (currentUserId.equalsIgnoreCase(closeRespUserId)
                        && "待整改".equalsIgnoreCase(oneDetail.getString("confirmResult"))) {
                        finalResult.add(oneDetail);
                    }
                } else if (stageName.equalsIgnoreCase(RdmConst.STANDARD_DOCHECK_STAGE_ZGRESULTDEPTCHECKAGAIN)) {
                    // 问题关闭责任部门是本部门，结果确认为“待整改”
                    if (currentUserDeptId.equalsIgnoreCase(oneDetail.getString("closeDeptId"))
                        && "待整改".equalsIgnoreCase(oneDetail.getString("confirmResult"))) {
                        finalResult.add(oneDetail);
                    }
                }
            }
            return finalResult;
        }

        // 标准技术所所有
        JSONObject isBzrzsObj = loginRecordManager.isBzrzs(ContextUtil.getCurrentUser().getMainGroupId());
        boolean isBzrzs = isBzrzsObj.getBooleanValue("isBzrzs");
        if (isBzrzs) {
            return result;
        }

        // 创建人、自查人、自查人室主任、标准对接人所有
        JSONObject applyObj = standardDoCheckManager.queryApplyJson(applyId);
        if (currentUserId.equalsIgnoreCase(applyObj.getString("CREATE_BY_"))
            || currentUserId.equalsIgnoreCase(applyObj.getString("firstWriterId"))
            || currentUserId.equalsIgnoreCase(applyObj.getString("szrUserId"))
            || currentUserId.equalsIgnoreCase(applyObj.getString("djrUserId"))) {
            return result;
        }

        // 所有的责任部门和问题关闭部门
        Set<String> respOrCloseDeptIds = new HashSet<>();
        for (JSONObject oneData : result) {
            if (StringUtils.isNotBlank(oneData.getString("deptId"))) {
                respOrCloseDeptIds.add(oneData.getString("deptId"));
            }
            if (StringUtils.isNotBlank(oneData.getString("closeDeptId"))) {
                respOrCloseDeptIds.add(oneData.getString("closeDeptId"));
            }
        }
        // 是否是责任或问题部门负责人
        boolean isDeptRespUser = false;
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("groupIds", respOrCloseDeptIds);
        List<Map<String, String>> depRespMans = xcmgProjectOtherDao.getDepRespManById(params);
        for (Map<String, String> oneData : depRespMans) {
            if (currentUserId.equalsIgnoreCase(oneData.get("PARTY2_"))) {
                isDeptRespUser = true;
                break;
            }
        }

        for (JSONObject oneData : result) {
            // 部门负责人看本部门的
            if (isDeptRespUser) {
                if (currentUserDeptId.equalsIgnoreCase(oneData.getString("deptId"))
                    || currentUserDeptId.equalsIgnoreCase(oneData.getString("closeDeptId"))) {
                    finalResult.add(oneData);
                }
            } else {
                if (currentUserId.equalsIgnoreCase(oneData.getString("respUserId"))
                    || currentUserId.equalsIgnoreCase(oneData.getString("closeRespUserId"))) {
                    finalResult.add(oneData);
                }
            }
        }

        return finalResult;
    }

    @RequestMapping("checkDetailFileList")
    @ResponseBody
    public List<JSONObject> checkDetailFileList(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        String detailId = RequestUtil.getString(request, "detailId", "");
        if (StringUtils.isBlank(detailId)) {
            logger.error("detailId is blank");
            return null;
        }
        JSONObject params = new JSONObject();
        params.put("detailIds", Arrays.asList(detailId));
        List<JSONObject> fileList = standardDoCheckManager.checkDetailFileList(params);
        return fileList;
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
        standardDoCheckManager.saveInProcess(result, data);
        return result;
    }

    @RequestMapping("preview")
    public ResponseEntity<byte[]> txxyBorrowPreview(HttpServletRequest request, HttpServletResponse response) {
        ResponseEntity<byte[]> result = null;
        String fileType = RequestUtil.getString(request, "fileType");
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("standardDoCheckFilePathBase");
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
            String formId = RequestUtil.getString(request, "formId");
            String fileBasePath = WebAppUtil.getProperty("standardDoCheckFilePathBase");
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

    @RequestMapping("deleteFiles")
    public void deleteFiles(HttpServletRequest request, HttpServletResponse response, @RequestBody String postBody) {
        JSONObject postBodyObj = JSONObject.parseObject(postBody);
        String fileId = postBodyObj.getString("id");
        if (StringUtils.isBlank(fileId)) {
            return;
        }
        String fileName = postBodyObj.getString("fileName");
        String formId = postBodyObj.getString("formId");
        JSONObject param = new JSONObject();
        param.put("ids", Arrays.asList(fileId));
        standardDoCheckDao.delDoCheckFile(param);
        String fileBasePath = WebAppUtil.getProperty("standardDoCheckFilePathBase");
        rdmZhglFileManager.deleteOneFileFromDisk(fileId, fileName, formId, fileBasePath);
    }

    @RequestMapping("openUploadWindow")
    public ModelAndView openUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "standardManager/core/standardDoCheckApplyUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        return mv;
    }

    @RequestMapping("upload")
    @ResponseBody
    public Map<String, Object> upload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        try {
            // 先保存文件到磁盘，成功后再写入数据库，前台是一个一个文件的调用
            standardDoCheckManager.saveUploadFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            logger.error("Exception in upload", e);
            modelMap.put("success", "false");
        }
        return modelMap;
    }

    // 判断是否能够查看表单
    @RequestMapping("judgeCanSee")
    @ResponseBody
    public JsonResult judgeCanSee(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JsonResult result = new JsonResult(true, "");
        String id = RequestUtil.getString(request, "id", "");
        if (StringUtils.isBlank(id)) {
            result.setSuccess(false);
            return result;
        }
        if ("admin".equalsIgnoreCase(ContextUtil.getCurrentUser().getUserNo())) {
            return result;
        }

        String currentUserId = ContextUtil.getCurrentUserId();
        // 分管领导允许
        boolean isFgld = rdmZhglUtil.judgeUserIsFgld(currentUserId);
        if (isFgld) {
            return result;
        }
        // 标准技术所允许
        JSONObject isBzrzsObj = loginRecordManager.isBzrzs(ContextUtil.getCurrentUser().getMainGroupId());
        boolean isBzrzs = isBzrzsObj.getBooleanValue("isBzrzs");
        if (isBzrzs) {
            return result;
        }

        // 创建人、起草人、起草人室主任、标准对接人允许
        JSONObject applyObj = standardDoCheckManager.queryApplyJson(id);
        if (currentUserId.equalsIgnoreCase(applyObj.getString("CREATE_BY_"))
            || currentUserId.equalsIgnoreCase(applyObj.getString("firstWriterId"))
            || currentUserId.equalsIgnoreCase(applyObj.getString("szrUserId"))
            || currentUserId.equalsIgnoreCase(applyObj.getString("djrUserId"))) {
            return result;
        }
        // 责任人或者问题关闭人允许
        JSONObject params = new JSONObject();
        params.put("baseInfoId", id);
        List<JSONObject> checkDetailList = standardDoCheckManager.checkDetailList(params);
        if (checkDetailList == null || checkDetailList.isEmpty()) {
            result.setSuccess(false);
            return result;
        }
        Set<String> respDeptIds = new HashSet<>();
        Set<String> closeDeptIds = new HashSet<>();
        for (JSONObject oneData : checkDetailList) {
            respDeptIds.add(oneData.getString("deptId"));
            closeDeptIds.add(oneData.getString("closeDeptId"));
            if (currentUserId.equalsIgnoreCase(oneData.getString("respUserId"))
                || currentUserId.equalsIgnoreCase(oneData.getString("closeRespUserId"))) {
                return result;
            }
        }
        // 责任部门负责人或问题关闭部门负责人允许
        respDeptIds.addAll(closeDeptIds);
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("groupIds", respDeptIds);
        List<Map<String, String>> depRespMans = xcmgProjectOtherDao.getDepRespManById(params);
        if (depRespMans == null || depRespMans.isEmpty()) {
            result.setSuccess(false);
            return result;
        }
        for (Map<String, String> oneData : depRespMans) {
            if (currentUserId.equalsIgnoreCase(oneData.get("PARTY2_"))) {
                return result;
            }
        }

        result.setSuccess(false);
        return result;
    }

    @RequestMapping("startDoCheckFlow")
    @ResponseBody
    public JsonResult startDoCheckFlow(HttpServletRequest request, HttpServletResponse response) {
        return standardDoCheckManager.startDoCheckFlow(request, response);
    }

    // 导出
    @RequestMapping("exportStandardDoCheckList")
    @ResponseBody
    public void exportStandardDoCheckList(HttpServletRequest request, HttpServletResponse response) {
         standardDoCheckManager.exportStandardDoCheckList(request, response);
    }
}
