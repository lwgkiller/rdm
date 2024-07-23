package com.redxun.world.core.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.controller.GenericController;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.world.core.service.CkddService;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

@Controller
@RequestMapping("/Ckdd/")
public class CkddController extends GenericController {
    @Autowired
    private CommonBpmManager commonBpmManager;
    @Autowired
    private CkddService ckddService;
    @Autowired
    private CommonInfoManager commonInfoManager;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;

    @RequestMapping("openUploadWindow")
    public ModelAndView openUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "world/core/ckddFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("ckddId", RequestUtil.getString(request, "ckddId", ""));
        return mv;
    }

    @RequestMapping(value = "upload")
    @ResponseBody
    public Map<String, Object> upload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        try {
            // 先保存文件到磁盘，成功后再写入数据库，前台是一个一个文件的调用
            ckddService.saveCkddUploadFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            logger.error("Exception in upload", e);
            modelMap.put("success", "false");
        }
        return modelMap;
    }

    @RequestMapping("ckddListPage")
    public ModelAndView msgManagement(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "world/core/ckddList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        return mv;
    }

    @RequestMapping("editPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "world/core/ckddEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        // 新增或者编辑页面由流程引擎跳转并替换url中的pk和nodeId_
        String ckddId = RequestUtil.getString(request, "ckddId");
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
        mv.addObject("ckddId", ckddId).addObject("action", action).addObject("status", status);
        // 取出节点变量，返回到页面

        if (StringUtils.isNotBlank(nodeId)) {
            mv.addObject("nodeId", nodeId);
            List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId,"CKDD",null);
            if (nodeVars != null && !nodeVars.isEmpty()) {
                mv.addObject("nodeVars", JSONObject.toJSONString(nodeVars));
            }
        }
        JSONObject currentUserDepInfo = xcmgProjectManager.isCurrentUserDepRespman();
        JSONObject dept = commonInfoManager.queryDeptNameById(currentUserDepInfo.getString("currentUserMainDepId"));
        String deptName = dept.getString("deptname");
        mv.addObject("deptName", deptName);
        mv.addObject("currentUserMainDepId", currentUserDepInfo.getString("currentUserMainDepId"));
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        return mv;
    }

    @RequestMapping("queryCkdd")
    @ResponseBody
    public JsonPageResult<?> queryJstb(HttpServletRequest request, HttpServletResponse response) {
        return ckddService.queryCkdd(request, true);
    }

    @RequestMapping("deleteCkdd")
    @ResponseBody
    public JsonResult deleteJstb(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String ckddId = RequestUtil.getString(request, "id");
            return ckddService.deleteCkdd(ckddId);
        } catch (Exception e) {
            logger.error("Exception in deleteWj", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("saveCkdd")
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
            if (StringUtils.isBlank(formDataJson.getString("ckddId"))) {
                ckddService.createCkdd(formDataJson);
            } else {
                ckddService.updateCkdd(formDataJson);
            }
        } catch (Exception e) {
            logger.error("Exception in save ckdd");
            result.setSuccess(false);
            result.setMessage("Exception in save ckdd");
            return result;
        }
        return result;
    }

    @RequestMapping("getCkddDetail")
    @ResponseBody
    public JSONObject getCkddDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject ckddObj = new JSONObject();
        String ckddId = RequestUtil.getString(request, "ckddId");
        if (StringUtils.isNotBlank(ckddId)) {
            ckddObj = ckddService.getCkddDetail(ckddId);
        }
        return ckddObj;
    }

    @RequestMapping("getTimeList")
    @ResponseBody
    public List<JSONObject> getTimeList(HttpServletRequest request, HttpServletResponse response) {
        String belongId = RequestUtil.getString(request, "belongId");
        return ckddService.getTimeList(belongId);
    }

    @RequestMapping("getDetailList")
    @ResponseBody
    public List<JSONObject> getCnList(HttpServletRequest request, HttpServletResponse response) {
        String belongId = RequestUtil.getString(request, "belongId");
        return ckddService.getDetailList(belongId);
    }

    @RequestMapping("getCkddFileList")
    @ResponseBody
    public List<JSONObject> getCkddFileList(HttpServletRequest request, HttpServletResponse response) {
        String belongId = RequestUtil.getString(request, "belongId");
        return ckddService.getCkddFileList(belongId);
    }

    @RequestMapping("deleteCkddFile")
    public void deleteCkddFile(HttpServletRequest request, HttpServletResponse response, @RequestBody String postBody) {
        JSONObject postBodyObj = JSONObject.parseObject(postBody);
        String fileName = postBodyObj.getString("fileName");
        String fileId = postBodyObj.getString("id");
        String ckddId = postBodyObj.getString("formId");
        ckddService.deleteOneCkddFile(fileId, fileName, ckddId);
    }

    @RequestMapping("ckddPdfPreview")
    public ResponseEntity<byte[]> ckddPdfPreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("ckddFilePathBase");
        return rdmZhglFileManager.pdfPreviewOrDownLoad(fileName, fileId, formId, fileBasePath);
    }

    @RequestMapping("ckddOfficePreview")
    @ResponseBody
    public void ckddOfficePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("ckddFilePathBase");
        rdmZhglFileManager.officeFilePreview(fileName, fileId, formId, fileBasePath, response);
    }

    @RequestMapping("ckddImagePreview")
    @ResponseBody
    public void ckddImagePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("ckddFilePathBase");
        rdmZhglFileManager.imageFilePreview(fileName, fileId, formId, fileBasePath, response);
    }

    @RequestMapping("saveTime")
    @ResponseBody
    public JsonResult saveTime(HttpServletRequest request, @RequestBody JSONObject formDataJson,
        HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "成功");
        try {
            String belongId = RequestUtil.getString(request, "belongId");
            if (formDataJson == null || formDataJson.isEmpty()) {
                logger.warn("formData is blank");
                result.setSuccess(false);
                result.setMessage("requestBody is blank");
                return result;
            }
            if (StringUtils.isBlank(formDataJson.getString("cnsxId"))) {
                formDataJson.put("belongId", belongId);
                ckddService.insertTime(formDataJson);
            } else {
                ckddService.updateTime(formDataJson);
            }
        } catch (Exception e) {
            logger.error("Exception in save Time");
            result.setSuccess(false);
            result.setMessage("Exception in save Time");
            return result;
        }
        return result;
    }

    @RequestMapping("deleteTime")
    @ResponseBody
    public JsonResult deleteTime(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String ydjxIds = RequestUtil.getString(request, "ids");
            String[] ydjxIdsArr = ydjxIds.split(",", -1);
            return ckddService.deleteTime(ydjxIdsArr);
        } catch (Exception e) {
            logger.error("Exception in deleteYdjx", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("deleteDetail")
    @ResponseBody
    public JsonResult deleteDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String ydjxIds = RequestUtil.getString(request, "ids");
            String[] ydjxIdsArr = ydjxIds.split(",", -1);
            return ckddService.deleteDetail(ydjxIdsArr);
        } catch (Exception e) {
            logger.error("Exception in deleteYdjx", e);
            return new JsonResult(false, e.getMessage());
        }
    }
}
