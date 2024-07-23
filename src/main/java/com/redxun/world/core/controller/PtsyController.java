package com.redxun.world.core.controller;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.controller.GenericController;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.world.core.service.PtsyService;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/rdm/core/ptsy/")
public class PtsyController extends GenericController {
    @Autowired
    private CommonBpmManager commonBpmManager;
    @Autowired
    private PtsyService ptsyService;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;
    @Autowired
    private CommonInfoDao commonInfoDao;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @RequestMapping("openUploadWindow")
    public ModelAndView openUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "world/core/ptsyFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("ptsyId", RequestUtil.getString(request, "ptsyId", ""));
        mv.addObject("fileType", RequestUtil.getString(request, "fileType", ""));
        return mv;
    }
    @RequestMapping(value = "upload")
    @ResponseBody
    public Map<String, Object> upload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        try {
            // 先保存文件到磁盘，成功后再写入数据库，前台是一个一个文件的调用
            ptsyService.savePtsyUploadFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            logger.error("Exception in upload", e);
            modelMap.put("success", "false");
        }
        return modelMap;
    }

    @RequestMapping("ptsyListPage")
    public ModelAndView msgManagement(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "world/core/ptsyList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        return mv;
    }

    @RequestMapping("editPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "world/core/ptsyEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        // 新增或者编辑页面由流程引擎跳转并替换url中的pk和nodeId_
        String ptsyId = RequestUtil.getString(request, "ptsyId");
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
        mv.addObject("ptsyId", ptsyId).addObject("action", action).addObject("status", status);
        // 取出节点变量，返回到页面
        if (StringUtils.isNotBlank(nodeId)) {
            mv.addObject("nodeId", nodeId);
            List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId, "PTSY", null);
            if (nodeVars != null && !nodeVars.isEmpty()) {
                mv.addObject("nodeVars", JSONObject.toJSONString(nodeVars));
            }
        }
        JSONObject currentUserDepInfo = xcmgProjectManager.isCurrentUserDepRespman();
        Map<String, Object> params = new HashMap<>();
        params.put("currentUserMainDepId", currentUserDepInfo.getString("currentUserMainDepId"));
        params.put("deptId", currentUserDepInfo.getString("currentUserMainDepId"));
        JSONObject dept = commonInfoDao.queryDept(params);
        String deptName = dept.getString("deptname");
        mv.addObject("deptName", deptName);
        mv.addObject("deptId", currentUserDepInfo.getString("currentUserMainDepId"));
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        return mv;
    }

    @RequestMapping("queryPtsy")
    @ResponseBody
    public JsonPageResult<?> queryJstb(HttpServletRequest request, HttpServletResponse response) {
        return ptsyService.queryPtsy(request, true);
    }

    @RequestMapping("deletePtsy")
    @ResponseBody
    public JsonResult deletePtsy(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String instIdStr = RequestUtil.getString(request, "instIds");
            String[] instIds = instIdStr.split(",");
            String[] ids = uIdStr.split(",");
            return ptsyService.deletePtsy(ids, instIds);
        } catch (Exception e) {
            logger.error("Exception in deletePtsy", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("getPtsy")
    @ResponseBody
    public JSONObject getPtsy(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject ptsyObj = new JSONObject();
        String ptsyId = RequestUtil.getString(request, "ptsyId");
        if (StringUtils.isNotBlank(ptsyId)) {
            ptsyObj = ptsyService.getPtsyById(ptsyId);
        }
        return ptsyObj;
    }

    @RequestMapping("getPtsyFileList")
    @ResponseBody
    public List<JSONObject> getPtsyFileList(HttpServletRequest request, HttpServletResponse response) {
        String belongId = RequestUtil.getString(request, "belongId");
        String fileType = RequestUtil.getString(request, "fileType");
        return ptsyService.getPtsyFileList(belongId,fileType);
    }

    @RequestMapping("deletePtsyFile")
    public void deletePtsyFile(HttpServletRequest request, HttpServletResponse response, @RequestBody String postBody) {
        JSONObject postBodyObj = JSONObject.parseObject(postBody);
        String fileName = postBodyObj.getString("fileName");
        String fileId = postBodyObj.getString("id");
        String ptsyId = postBodyObj.getString("formId");
        ptsyService.deleteOnePtsyFile(fileId, fileName, ptsyId);
    }
    @RequestMapping("ptsyPdfPreview")
    public ResponseEntity<byte[]> ptsyPdfPreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("ptsyFilePathBase");
        return rdmZhglFileManager.pdfPreviewOrDownLoad(fileName, fileId, formId, fileBasePath);
    }

    @RequestMapping("ptsyOfficePreview")
    @ResponseBody
    public void ptsyOfficePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("ptsyFilePathBase");
        rdmZhglFileManager.officeFilePreview(fileName, fileId, formId, fileBasePath, response);
    }

    @RequestMapping("ptsyImagePreview")
    @ResponseBody
    public void ptsyImagePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("ptsyFilePathBase");
        rdmZhglFileManager.imageFilePreview(fileName, fileId, formId, fileBasePath, response);
    }

}
