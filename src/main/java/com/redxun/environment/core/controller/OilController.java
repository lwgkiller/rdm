package com.redxun.environment.core.controller;

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
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.environment.core.dao.OilDao;
import com.redxun.environment.core.service.OilService;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.controller.GenericController;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

/**
 * 国家标准制修订管理
 */
@Controller
@RequestMapping("/environment/core/Oil/")
public class OilController extends GenericController {
    @Autowired
    private OilService oilService;
    @Autowired
    private CommonInfoManager commonInfoManager;
    @Autowired
    private OilDao oilDao;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;

    @RequestMapping("openUploadWindow")
    public ModelAndView openUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rap/core/oilFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("oilId", RequestUtil.getString(request, "oilId", ""));
        return mv;
    }

    @RequestMapping(value = "upload")
    @ResponseBody
    public Map<String, Object> upload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        try {
            // 先保存文件到磁盘，成功后再写入数据库，前台是一个一个文件的调用
            oilService.saveOilUploadFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            logger.error("Exception in upload", e);
            modelMap.put("success", "false");
        }
        return modelMap;
    }

    @RequestMapping("fileList")
    public ModelAndView fileList(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "rap/core/oilFileList.jsp";
        String oilId = RequestUtil.getString(request, "oilId");
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("oilId", oilId);
        mv.addObject("currentUser", ContextUtil.getCurrentUser());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        return mv;
    }

    @RequestMapping("list")
    public ModelAndView management(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "rap/core/oilList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        Map<String, Object> params = new HashMap<>();
        params.put("userId", ContextUtil.getCurrentUserId());
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        // 角色信息
        String userId = ContextUtil.getCurrentUserId();
        boolean isYELR = rdmZhglUtil.judgeIsPointRoleUser(userId, "油液录入专员");
        JSONObject currentUserDepInfo = xcmgProjectManager.isCurrentUserDepRespman();
        JSONObject dept = commonInfoManager.queryDeptNameById(currentUserDepInfo.getString("currentUserMainDepId"));
        String deptname = dept.get("deptname").toString();
        mv.addObject("deptname", deptname);
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserId", userId);
        mv.addObject("isYELR", isYELR);
        return mv;
    }

    // 标准新增和编辑页面
    @RequestMapping("edit")
    public ModelAndView editOil(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "rap/core/oilEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String action = RequestUtil.getString(request, "action");
        String oilId = RequestUtil.getString(request, "oilId");
        if (StringUtils.isBlank(action)) {
            action = "edit";
        }
        JSONObject currentUserDepInfo = xcmgProjectManager.isCurrentUserDepRespman();
        JSONObject dept = commonInfoManager.queryDeptNameById(currentUserDepInfo.getString("currentUserMainDepId"));
        String deptname = dept.get("deptname").toString();
        mv.addObject("deptname", deptname);
        mv.addObject("action", action);
        mv.addObject("oilId", oilId);
        mv.addObject("currentUser", ContextUtil.getCurrentUser());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        return mv;
    }

    @RequestMapping("getOilDetail")
    @ResponseBody
    public JSONObject getOilDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject oilobj = new JSONObject();
        String oilId = RequestUtil.getString(request, "oilId");
        if (StringUtils.isNotBlank(oilId)) {
            oilobj = oilService.getOilDetail(oilId);
        }
        return oilobj;
    }

    // 标准列表查询
    @RequestMapping("queryList")
    @ResponseBody
    public JsonPageResult<?> queryOilList(HttpServletRequest request, HttpServletResponse response) {
        return oilService.queryOil(request, response, true);
    }

    @RequestMapping("saveOil")
    @ResponseBody
    public JsonResult saveOil(HttpServletRequest request, @RequestBody JSONObject formDataJson,
        HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "成功");
        try {
            if (formDataJson == null || formDataJson.isEmpty()) {
                logger.warn("formData is blank");
                result.setSuccess(false);
                result.setMessage("requestBody is blank");
                return result;
            }
            if (StringUtils.isBlank(formDataJson.getString("oilId"))) {
                oilService.insertOil(formDataJson);
            } else {
                if (StringUtils.isNotBlank(formDataJson.getString("action"))
                    && formDataJson.getString("action").equals("submit")) {
                    formDataJson.put("auditStatus", "正在审核");
                }
                if (StringUtils.isNotBlank(formDataJson.getString("action"))
                    && formDataJson.getString("action").equals("reject")) {
                    formDataJson.put("auditStatus", "驳回待提交");
                }
                if (StringUtils.isNotBlank(formDataJson.getString("action"))
                    && formDataJson.getString("action").equals("approve")) {
                    formDataJson.put("auditStatus", "审核通过");
                }
                oilService.updateOil(formDataJson);
            }
        } catch (Exception e) {
            logger.error("Exception in save oil");
            result.setSuccess(false);
            result.setMessage("Exception in save oil");
            return result;
        }
        return result;
    }

    @RequestMapping("getFileList")
    @ResponseBody
    public List<JSONObject> getoilFileList(HttpServletRequest request, HttpServletResponse response) {
        List<String> oilIdList = Arrays.asList(RequestUtil.getString(request, "oilId", ""));
        return oilService.getOilFileList(oilIdList);
    }

    @RequestMapping("oilPdfPreview")
    public ResponseEntity<byte[]> oilPdfPreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("oilFilePathBase");
        return rdmZhglFileManager.pdfPreviewOrDownLoad(fileName, fileId, formId, fileBasePath);
    }

    @RequestMapping("oilOfficePreview")
    @ResponseBody
    public void oilOfficePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("oilFilePathBase");
        rdmZhglFileManager.officeFilePreview(fileName, fileId, formId, fileBasePath, response);
    }

    @RequestMapping("oilImagePreview")
    @ResponseBody
    public void oilImagePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("oilFilePathBase");
        rdmZhglFileManager.imageFilePreview(fileName, fileId, formId, fileBasePath, response);
    }

    @RequestMapping("deleteoilFile")
    public void deleteOilFile(HttpServletRequest request, HttpServletResponse response, @RequestBody String postBody) {
        JSONObject postBodyObj = JSONObject.parseObject(postBody);
        String fileName = postBodyObj.getString("fileName");
        String fileId = postBodyObj.getString("id");
        String oilId = postBodyObj.getString("formId");
        oilService.deleteOneOilFile(fileId, fileName, oilId);
    }

    @RequestMapping("deleteoil")
    @ResponseBody
    public JsonResult deleteOil(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return oilService.deleteOil(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteOil", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("importTemplateDownload")
    public ResponseEntity<byte[]> importTemplateDownload(HttpServletRequest request, HttpServletResponse response) {
        return oilService.importTemplateDownload();
    }

    /**
     * 批量导入
     */
    @RequestMapping("importExcel")
    @ResponseBody
    public JSONObject importExcel(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        oilService.importOil(result, request);
        return result;
    }
}
