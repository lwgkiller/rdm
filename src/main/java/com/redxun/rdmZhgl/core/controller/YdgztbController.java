package com.redxun.rdmZhgl.core.controller;

import java.io.File;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmZhgl.core.dao.YdgztbDao;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.rdmZhgl.core.service.YdgztbManager;
import com.redxun.rdmZhgl.core.service.YdgztbScheduler;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

/**
 * 月度工作提报
 *
 * @mh 2023年5月4日17:22:02
 */
@Controller
@RequestMapping("/zhgl/core/ydgztb/")
public class YdgztbController {
    private static final Logger logger = LoggerFactory.getLogger(YdgztbController.class);

    @Autowired
    private YdgztbManager ydgztbManager;
    @Resource
    private BpmInstManager bpmInstManager;
    @Autowired
    private CommonBpmManager commonBpmManager;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private YdgztbDao ydgztbDao;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private SysDicManager sysDicManager;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;
    @Autowired
    private YdgztbScheduler ydgztbScheduler;

    @RequestMapping("applyListPage")
    public ModelAndView getListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String applyList = "rdmZhgl/core/ydgztbList.jsp";
        ModelAndView mv = new ModelAndView(applyList);
        String yearMonth = DateFormatUtil.format(new Date(), "yyyy-MM");
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("deptName", ContextUtil.getCurrentUser().getMainGroupName());
        mv.addObject("yearMonth", yearMonth);
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DATE);
        mv.addObject("currentDay", day);
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        // 这里要加所长角色，只有所长才可以选优秀案例

        // 确定当前登录人是否是部门负责人
        JSONObject currentUserDepInfo = xcmgProjectManager.isCurrentUserDepRespman();

        boolean isSuozhang = currentUserDepInfo.getBoolean("isDepRespMan");

        mv.addObject("isSuozhang", isSuozhang);

        // 还有一个admin权限或技术管理部权限 可以看未提报
        boolean isTbAdmin = rdmZhglUtil.judgeIsPointRoleUser(ContextUtil.getCurrentUserId(), "月度工作提报-管理员");
        mv.addObject("isTbAdmin", isTbAdmin);
        String scene = RequestUtil.getString(request, "scene");
        mv.addObject("scene", scene);

        return mv;
    }

    @RequestMapping("applyList")
    @ResponseBody
    public JsonPageResult<?> queryApplyList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return ydgztbManager.queryApplyList(request, true);
    }

    /**
     * 新增月度工作提报知识库
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("getTBLibrary")
    @ResponseBody
    public ModelAndView getTBLibrary(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String libraryList = "rdmZhgl/core/ydgztbLibrary.jsp";
        ModelAndView mv = new ModelAndView(libraryList);
        return mv;
    }

    @RequestMapping("getTBLibraryList")
    @ResponseBody
    public JsonPageResult<?> getTBLibraryList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return ydgztbManager.getTBLibraryList(request, true);
    }

    /**
     * 新增月度工作提报总览
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("applyAnalysis")
    @ResponseBody
    public ModelAndView applyAnalysis(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "/rdmZhgl/core/ydgztbAnalysis.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        return mv;
    }

    @RequestMapping(value = "queryTBSituation", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject queryTBSituation(HttpServletRequest request, @RequestBody String postData, HttpServletResponse response)
            throws Exception {
        JSONObject postDataJson = new JSONObject();
        if (StringUtils.isNotBlank(postData)) {
            postDataJson = JSONObject.parseObject(postData);
        }
        return ydgztbManager.queryTBSituation(postDataJson);
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
            return ydgztbManager.delApplys(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteApply", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("applyEditPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/ydgztbEdit.jsp";
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
            List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId, "YDGZTB", null);
            if (nodeVars != null && !nodeVars.isEmpty()) {
                mv.addObject("nodeVars", JSONObject.toJSONString(nodeVars));
            }
        }
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DATE);
        mv.addObject("currentDay", day);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserDeptName", ContextUtil.getCurrentUser().getMainGroupName());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        mv.addObject("applyId", applyId);
        mv.addObject("action", action);
        mv.addObject("status", status);

        return mv;
    }

    @RequestMapping("getJson")
    @ResponseBody
    public JSONObject queryApplyDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = RequestUtil.getString(request, "id", "");
        if (StringUtils.isBlank(id)) {
            JSONObject result = new JSONObject();
            result.put("CREATE_BY_", ContextUtil.getCurrentUser().getUserId());
            result.put("creatorName", ContextUtil.getCurrentUser().getFullname());
            result.put("departName", ContextUtil.getCurrentUser().getMainGroupName());
            result.put("applyTime", DateFormatUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
            result.put("yearMonth", DateFormatUtil.format(new Date(), "yyyy-MM"));
            return result;
        }
        return ydgztbManager.queryApplyDetail(id);
    }

    // 文件相关

    @RequestMapping("upload")
    @ResponseBody
    public Map<String, Object> upload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        try {
            // 先保存文件到磁盘，成功后再写入数据库，前台是一个一个文件的调用

            ydgztbManager.saveUploadFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            logger.error("Exception in upload", e);
            modelMap.put("success", "false");
        }
        return modelMap;
    }

    @RequestMapping("openUploadWindow")
    public ModelAndView openUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/ydgztbFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String applyId = RequestUtil.getString(request, "applyId", "");
        String fileType = RequestUtil.getString(request, "fileType", "");
        mv.addObject("currentUser", ContextUtil.getCurrentUser());
        mv.addObject("applyId", applyId);
        mv.addObject("fileType", fileType);
        return mv;
    }

    @RequestMapping("/fileDownload")
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
            String fileBasePath = sysDicManager.getBySysTreeKeyAndDicKey("zhglUploadPosition", "ydgztb").getValue();
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

    // 清除文件
    @RequestMapping("deleteFiles")
    public void deleteFiles(HttpServletRequest request, HttpServletResponse response, @RequestBody String postBody) {
        JSONObject postBodyObj = JSONObject.parseObject(postBody);
        String fileId = postBodyObj.getString("id");
        if (StringUtils.isBlank(fileId)) {
            return;
        }
        String fileName = postBodyObj.getString("fileName");
        String formId = postBodyObj.getString("formId");
        String fileBasePath = sysDicManager.getBySysTreeKeyAndDicKey("zhglUploadPosition", "ydgztb").getValue();
        rdmZhglFileManager.deleteOneFileFromDisk(fileId, fileName, formId, fileBasePath);
        // 清除文件后,将表单中的文件信息置空
        JSONObject param = new JSONObject();
        param.put("id", fileId);
        ydgztbDao.deleteFile(param);

    }

    @RequestMapping("/preview")
    public ResponseEntity<byte[]> filePreview(HttpServletRequest request, HttpServletResponse response) {
        ResponseEntity<byte[]> result = null;
        String fileType = RequestUtil.getString(request, "fileType");
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = sysDicManager.getBySysTreeKeyAndDicKey("zhglUploadPosition", "ydgztb").getValue();
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

    // 文件列表
    @RequestMapping("fileList")
    @ResponseBody
    public List<JSONObject> fileList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String applyId = RequestUtil.getString(request, "applyId", "");
        if (StringUtils.isBlank(applyId)) {
            logger.error("applyId is blank");
            return null;
        }
        JSONObject params = new JSONObject();
        params.put("applyId", applyId);
        List<JSONObject> fileList = ydgztbManager.queryFileList(params);

        return fileList;
    }

    @RequestMapping("chooseYxal")
    @ResponseBody
    public JsonResult chooseYxal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String yearMonth = RequestUtil.getString(request, "yearMonth");
            String[] ids = uIdStr.split(",");
            return ydgztbManager.chooseYxal(ids,yearMonth);
        } catch (Exception e) {
            logger.error("Exception in chooseYxal", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("cancelYxal")
    @ResponseBody
    public JsonResult cancelYxal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return ydgztbManager.cancelYxal(ids);
        } catch (Exception e) {
            logger.error("Exception in confirmApply", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("exportExecl")
    public void exportExcel(HttpServletRequest request, HttpServletResponse response) {
        ydgztbManager.exportExecl(request, response);
    }

    @RequestMapping("isExistYdgz")
    @ResponseBody
    public JSONObject isExistMonthFlow(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        result.put("success", false);
        String id = RequestUtil.getString(request, "id", "");
        String yearMonth = RequestUtil.getString(request, "yearMonth", "");
        String creatorId = RequestUtil.getString(request, "creatorId", "");
        Map<String, Object> param = new HashMap<>();
        param.put("yearMonth", yearMonth);
        param.put("creatorId", creatorId);
        if (StringUtils.isNotBlank(id)) {
            param.put("notEqualId", id);
        }
        List<JSONObject> monthFlowList = ydgztbDao.queryExist(param);
        if (monthFlowList != null && !monthFlowList.isEmpty()) {
            result.put("success", true);
            return result;
        }
        return result;
    }

    @RequestMapping("saveBusiness")
    @ResponseBody
    public JsonResult saveBusiness(HttpServletRequest request, @RequestBody String formDataStr,
        HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "");
        if (StringUtils.isBlank(formDataStr)) {
            logger.warn("requestBody is blank");
            result.setSuccess(false);
            result.setMessage("requestBody is blank");
            return result;
        }
        try {
            JSONObject formDataJson = JSONObject.parseObject(formDataStr);
            if (formDataJson == null || formDataJson.isEmpty()) {
                logger.warn("formData is blank");
                result.setSuccess(false);
                result.setMessage("requestBody is blank");
                return result;
            }
            if (StringUtils.isBlank(formDataJson.getString("id"))) {
                ydgztbManager.createYdgztb(formDataJson);
            } else {
                ydgztbManager.updateYdgztb(formDataJson);
            }
        } catch (Exception e) {
            logger.error("Exception in save ydgztb");
            result.setSuccess(false);
            result.setMessage("Exception in save ydgztb");
            return result;
        }
        return result;
    }

    @RequestMapping("cancelRemind")
    @ResponseBody
    public JsonResult cancelRemind(HttpServletRequest request, HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "");
        String yearMonth = DateFormatUtil.format(new Date(), "yyyy-MM");
        String userId = ContextUtil.getCurrentUserId();
        JSONObject param = new JSONObject();
        param.put("id", IdUtil.getId());
        param.put("yearMonth", yearMonth);
        param.put("deptRespUserId", userId);
        param.put("CREATE_BY_", userId);
        param.put("CREATE_TIME_", new Date());
        ydgztbDao.insertRemindCancel(param);
        return result;
    }

    @RequestMapping("batchCreate")
    @ResponseBody
    public JsonResult batchCreate(HttpServletRequest request, HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "");
        ydgztbScheduler.ydgztbCreateFlow();
        result.setMessage("批量创建完成");
        return result;
    }

    @RequestMapping("ydgztbTemplateDownload")
    public ResponseEntity<byte[]> importTemplateDownload(HttpServletRequest request, HttpServletResponse response) {
        return ydgztbManager.importTemplateDownload();
    }
}
