package com.redxun.info.core.controller;


import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.info.core.dao.QbgzDao;
import com.redxun.info.core.manager.QbgzService;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.rdmCommon.core.manager.LoginRecordManager;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.controller.GenericController;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.sys.org.manager.OsGroupManager;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/Info/Qbgz/")
public class QbgzController extends GenericController{
    @Autowired
    private CommonBpmManager commonBpmManager;
    @Autowired
    private QbgzService qbgzService;
    @Autowired
    private QbgzDao qbgzDao;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;

    @RequestMapping("openUploadWindow")
    public ModelAndView openUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "info/Qbgz/qbgzFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("qbgzId", RequestUtil.getString(request, "qbgzId", ""));
        return mv;
    }
    @RequestMapping("qbgzOverview")
    public ModelAndView qbgzOverview(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "info/Qbgz/qbgzOverview.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        return mv;
    }
    @RequestMapping("QbgzListPage")
    public ModelAndView msgManagement(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "info/Qbgz/qbgzList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        boolean isQbzy = rdmZhglUtil.judgeIsPointRoleUser(ContextUtil.getCurrentUserId(), "情报专员");
        mv.addObject("isQbzy", isQbzy);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        return mv;
    }
    @RequestMapping("editPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "info/Qbgz/qbgzEdit.jsp";
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
            List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId,"QBBG",null);
            if (nodeVars != null && !nodeVars.isEmpty()) {
                mv.addObject("nodeVars", JSONObject.toJSONString(nodeVars));
            }
        }
        Map<String, Object> param2 = new HashMap<>();
        param2.put("currentUserId", ContextUtil.getCurrentUserId());
        boolean isLDR = false;
        if(ContextUtil.getCurrentUserId().equals("1")){
            mv.addObject("isLDR", isLDR);
        }else {
            List<JSONObject> isLDCList = qbgzDao.isLD(param2);
            for(JSONObject isLDC:isLDCList){
                if (isLDC != null && !"1".equals(isLDC.getString("SN_"))) {
                    isLDR = true;
                }
            }
            mv.addObject("isLDR", isLDR);
        }
        boolean isQbzy = rdmZhglUtil.judgeIsPointRoleUser(ContextUtil.getCurrentUserId(), "情报专员");
        mv.addObject("isQbzy", isQbzy);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        return mv;
    }

    @RequestMapping("queryQbgz")
    @ResponseBody
    public JsonPageResult<?> queryJstb(HttpServletRequest request, HttpServletResponse response) {
        return qbgzService.queryQbgz(request, true);
    }
    @RequestMapping("deleteQbgz")
    @ResponseBody
    public JsonResult deleteJstb(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return qbgzService.deleteQbgz(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteQbgz", e);
            return new JsonResult(false, e.getMessage());
        }
    }



    @RequestMapping("saveQbgz")
    @ResponseBody
    public JsonResult saveJstb(HttpServletRequest request, @RequestBody String xcmgProjectStr, HttpServletResponse response) {
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
                qbgzService.createQbgz(formDataJson);
            } else {
                qbgzService.updateQbgz(formDataJson);
            }
        } catch (Exception e) {
            logger.error("Exception in save qbgz");
            result.setSuccess(false);
            result.setMessage("Exception in save qbgz");
            return result;
        }
        return result;
    }
    @RequestMapping("getQbgzDetail")
    @ResponseBody
    public JSONObject getQbgzDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject qbgzObj = new JSONObject();
        String qbgzId = RequestUtil.getString(request, "qbgzId");
        if (StringUtils.isNotBlank(qbgzId)) {
            qbgzObj = qbgzService.getQbgzDetail(qbgzId);
        }
        return qbgzObj;
    }
    @RequestMapping(value = "upload")
    @ResponseBody
    public Map<String, Object> upload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        try {
            // 先保存文件到磁盘，成功后再写入数据库，前台是一个一个文件的调用
            qbgzService.saveQbgzUploadFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            logger.error("Exception in upload", e);
            modelMap.put("success", "false");
        }
        return modelMap;
    }
    @RequestMapping("getQbgzFileList")
    @ResponseBody
    public List<JSONObject> getQbgzFileList(HttpServletRequest request, HttpServletResponse response) {
        List<String> qbgzIdList = Arrays.asList(RequestUtil.getString(request, "qbgzId", ""));
        return qbgzService.getQbgzFileList(qbgzIdList);
    }
    @RequestMapping("deleteQbgzFile")
    public void deleteQbgzFile(HttpServletRequest request, HttpServletResponse response, @RequestBody String postBody) {
        JSONObject postBodyObj = JSONObject.parseObject(postBody);
        String fileName = postBodyObj.getString("fileName");
        String fileId = postBodyObj.getString("id");
        String qbgzId = postBodyObj.getString("formId");
        qbgzService.deleteOneQbgzFile(fileId, fileName, qbgzId);
    }
    @RequestMapping("qbgzPdfPreview")
    public ResponseEntity<byte[]> qbgzPdfPreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("qbgzFilePathBase");
        return rdmZhglFileManager.pdfPreviewOrDownLoad(fileName, fileId, formId, fileBasePath);
    }

    @RequestMapping("qbgzOfficePreview")
    @ResponseBody
    public void qbgzOfficePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("qbgzFilePathBase");
        rdmZhglFileManager.officeFilePreview(fileName, fileId, formId, fileBasePath, response);
    }

    @RequestMapping("qbgzImagePreview")
    @ResponseBody
    public void qbgzImagePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("qbgzFilePathBase");
        rdmZhglFileManager.imageFilePreview(fileName, fileId, formId, fileBasePath, response);
    }
    @RequestMapping("importTemplateDownload")
    public ResponseEntity<byte[]> importTemplateDownload(HttpServletRequest request, HttpServletResponse response) {
        return qbgzService.importTemplateDownload();
    }

    @RequestMapping(value = "queryProvideChart", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject queryPublishChart(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String timeFrom = RequestUtil.getString(request, "timeFrom");
        String timeTo = RequestUtil.getString(request, "timeTo");
        return qbgzService.queryProvideChart(timeFrom, timeTo);

    }
    @RequestMapping(value = "queryType", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject queryType(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String timeFrom = RequestUtil.getString(request, "timeFrom");
        String timeTo = RequestUtil.getString(request, "timeTo");
        return qbgzService.queryType(timeFrom, timeTo);
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
}

