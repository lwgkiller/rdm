package com.redxun.info.core.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.info.core.dao.QbsjDao;
import com.redxun.info.core.manager.QbsjService;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.controller.GenericController;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

@Controller
@RequestMapping("/Info/Qbsj/")
public class QbsjController extends GenericController {
    @Autowired
    private BpmInstManager bpmInstManager;
    @Autowired
    private CommonBpmManager commonBpmManager;
    @Autowired
    private QbsjService qbsjService;
    @Autowired
    private QbsjDao qbsjDao;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;

    @RequestMapping("qbsjListPage")
    public ModelAndView msgManagement(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "info/Qbgz/qbsjList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        boolean isQbzy = rdmZhglUtil.judgeIsPointRoleUser(ContextUtil.getCurrentUserId(), "情报专员");
        mv.addObject("isQbzy", isQbzy);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        return mv;
    }

    @RequestMapping("queryQbsj")
    @ResponseBody
    public JsonPageResult<?> queryQbsj(HttpServletRequest request, HttpServletResponse response) {
        return qbsjService.queryQbsj(request, true);
    }

    @RequestMapping("deleteQbsj")
    @ResponseBody
    public JsonResult deleteQbsj(HttpServletRequest request, HttpServletResponse response) throws Exception {
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
            return qbsjService.deleteQbsj(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteQbsj", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("editPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "info/Qbgz/qbsjEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        // 新增或者编辑页面由流程引擎跳转并替换url中的pk和nodeId_
        String qbgzId = RequestUtil.getString(request, "qbgzId");
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
        mv.addObject("qbgzId", qbgzId).addObject("action", action).addObject("status", status);
        // 取出节点变量，返回到页面
        if (StringUtils.isNotBlank(nodeId)) {
            mv.addObject("nodeId", nodeId);
            List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId, "QBSJ", null);
            if (nodeVars != null && !nodeVars.isEmpty()) {
                mv.addObject("nodeVars", JSONObject.toJSONString(nodeVars));
            }
        }
        Map<String, Object> param2 = new HashMap<>();

        boolean isQbzy = rdmZhglUtil.judgeIsPointRoleUser(ContextUtil.getCurrentUserId(), "情报专员");
        mv.addObject("isQbzy", isQbzy);
        param2.put("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        return mv;
    }

    @RequestMapping("saveQbsj")
    @ResponseBody
    public JsonResult saveQbsj(HttpServletRequest request, @RequestBody String xcmgProjectStr,
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
            if (StringUtils.isBlank(formDataJson.getString("qbgzId"))) {
                qbsjService.createQbsj(formDataJson);
            } else {
                qbsjService.updateQbsj(formDataJson);
            }
        } catch (Exception e) {
            logger.error("Exception in saveQbsj");
            result.setSuccess(false);
            result.setMessage("Exception in saveQbsj");
            return result;
        }
        return result;
    }

    @RequestMapping("getQbsjDetail")
    @ResponseBody
    public JSONObject getQbsjDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject qbgzObj = new JSONObject();
        String qbgzId = RequestUtil.getString(request, "qbgzId");
        if (StringUtils.isNotBlank(qbgzId)) {
            qbgzObj = qbsjService.getQbsjDetail(qbgzId);
        }
        return qbgzObj;
    }

    @RequestMapping("openUploadWindow")
    public ModelAndView openUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "info/Qbgz/qbgzFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("qbgzId", RequestUtil.getString(request, "qbgzId", ""));
        return mv;
    }

    @RequestMapping(value = "upload")
    @ResponseBody
    public Map<String, Object> upload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        try {
            // 先保存文件到磁盘，成功后再写入数据库，前台是一个一个文件的调用
            qbsjService.saveQbsjUploadFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            logger.error("Exception in upload", e);
            modelMap.put("success", "false");
        }
        return modelMap;
    }

    @RequestMapping("getQbsjFileList")
    @ResponseBody
    public List<JSONObject> getQbsjFileList(HttpServletRequest request, HttpServletResponse response) {
        List<String> qbgzIdList = Arrays.asList(RequestUtil.getString(request, "qbgzId", ""));
        return qbsjService.getQbsjFileList(qbgzIdList);
    }

    @RequestMapping("deleteQbgzFile")
    public void deleteQbgzFile(HttpServletRequest request, HttpServletResponse response, @RequestBody String postBody) {
        JSONObject postBodyObj = JSONObject.parseObject(postBody);
        String fileName = postBodyObj.getString("fileName");
        String fileId = postBodyObj.getString("id");
        String qbgzId = postBodyObj.getString("formId");
        qbsjService.deleteOneQbgzFile(fileId, fileName, qbgzId);
    }

    @RequestMapping("qbgzOfficePreview")
    @ResponseBody
    public void qbgzOfficePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("qbsjFilePathBase");
        rdmZhglFileManager.officeFilePreview(fileName, fileId, formId, fileBasePath, response);
    }

    @RequestMapping("qbgzPdfPreview")
    public ResponseEntity<byte[]> qbgzPdfPreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("qbsjFilePathBase");
        return rdmZhglFileManager.pdfPreviewOrDownLoad(fileName, fileId, formId, fileBasePath);
    }

    @RequestMapping("importTemplateDownload")
    public ResponseEntity<byte[]> importTemplateDownload(HttpServletRequest request, HttpServletResponse response) {
        return qbsjService.importTemplateDownload();
    }

    @RequestMapping("fileList")
    public ModelAndView fileList(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "info/Qbgz/qbgzFileList.jsp";
        String qbgzId = RequestUtil.getString(request, "qbgzId");
        ModelAndView mv = new ModelAndView(jspPath);
        boolean isQbzy = rdmZhglUtil.judgeIsPointRoleUser(ContextUtil.getCurrentUserId(), "情报专员");
        mv.addObject("isQbzy", isQbzy);
        mv.addObject("qbgzId", qbgzId);
        mv.addObject("currentUser", ContextUtil.getCurrentUser());
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        return mv;
    }

    @RequestMapping("querySmallTypeByBigType")
    @ResponseBody
    public List<JSONObject> querySmallTypeByBigType(HttpServletRequest request, HttpServletResponse response) {
        String bigTypeName = RequestUtil.getString(request, "bigTypeName");
        return qbsjService.querySmallTypeByBigType(bigTypeName);
    }
}
