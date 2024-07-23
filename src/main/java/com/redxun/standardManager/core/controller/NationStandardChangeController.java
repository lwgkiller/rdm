package com.redxun.standardManager.core.controller;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.controller.GenericController;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.standardManager.core.manager.StandardChangeService;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 国家标准制修订管理
 */
@Controller
@RequestMapping("/standardManager/core/NationStandardChangeController/")
public class NationStandardChangeController extends GenericController {
    @Autowired
    private StandardChangeService standardChangeService;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;

    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;

    @RequestMapping("openUploadWindow")
    public ModelAndView openUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "standardManager/core/standardChangeFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("standardId", RequestUtil.getString(request, "standardId", ""));
        return mv;
    }

    @RequestMapping(value = "upload")
    @ResponseBody
    public Map<String, Object> upload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        try {
            // 先保存文件到磁盘，成功后再写入数据库，前台是一个一个文件的调用
            standardChangeService.savestandardChangeUploadFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            logger.error("Exception in upload", e);
            modelMap.put("success", "false");
        }
        return modelMap;
    }

    @RequestMapping("list")
    public ModelAndView management(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "standardManager/core/nationStandardChange.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        Map<String, Object> params = new HashMap<>();
        params.put("userId", ContextUtil.getCurrentUserId());
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        // 角色信息
        List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params);
        boolean isJSBZGL = false;
        for (Map<String, Object> roleList : currentUserRoles) {
            if (roleList.get("NAME_").equals("技术标准管理人员")) {
                isJSBZGL = true;
                break;
            }
        }
        mv.addObject("isJSBZGL", isJSBZGL);
        return mv;
    }

    // 标准新增和编辑页面
    @RequestMapping("edit")
    public ModelAndView editStandard(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "standardManager/core/nationStandardEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String action = RequestUtil.getString(request, "action");
        String standardId = RequestUtil.getString(request, "standardId");
        if (StringUtils.isBlank(action)) {
            action = "edit";
        }
        mv.addObject("action", action);
        mv.addObject("standardId", standardId);
        mv.addObject("currentUser", ContextUtil.getCurrentUser());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        return mv;
    }

    @RequestMapping("getstandardChangeDetail")
    @ResponseBody
    public JSONObject getstandardChangeDetail(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        JSONObject standardobj = new JSONObject();
        String standardId = RequestUtil.getString(request, "standardId");
        if (StringUtils.isNotBlank(standardId)) {
            standardobj = standardChangeService.getstandardChangeDetail(standardId);
        }
        if (standardobj.get("releaseTime") != null) {
            standardobj.put("releaseTime", DateUtil.formatDate(standardobj.getDate("releaseTime"), "yyyy-MM"));
        }
        return standardobj;
    }

    // 标准列表查询
    @RequestMapping("queryList")
    @ResponseBody
    public JsonPageResult<?> queryStandardList(HttpServletRequest request, HttpServletResponse response) {
        return standardChangeService.queryStandardChange(request, response, true);
    }

    @RequestMapping("saveStandardChange")
    @ResponseBody
    public JsonResult saveStandardChange(HttpServletRequest request, @RequestBody JSONObject formDataJson,
        HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "保存成功");
        try {
            if (formDataJson == null || formDataJson.isEmpty()) {
                logger.warn("formData is blank");
                result.setSuccess(false);
                result.setMessage("requestBody is blank");
                return result;
            }
            if (StringUtils.isBlank(formDataJson.getString("standardId"))) {
                standardChangeService.insertstandardChange(formDataJson);
            } else {
                standardChangeService.updatestandardChange(formDataJson);
            }
        } catch (Exception e) {
            logger.error("Exception in save standardChange");
            result.setSuccess(false);
            result.setMessage("Exception in save standardChange");
            return result;
        }
        return result;
    }

    @RequestMapping("getFileList")
    @ResponseBody
    public List<JSONObject> getJstbFileList(HttpServletRequest request, HttpServletResponse response) {
        List<String> standardIdList = Arrays.asList(RequestUtil.getString(request, "standardId", ""));
        return standardChangeService.getstandardChangeFileList(standardIdList);
    }

    @RequestMapping("standardChangePdfPreview")
    public ResponseEntity<byte[]> jstbPdfPreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("standardChangeFilePathBase");
        return rdmZhglFileManager.pdfPreviewOrDownLoad(fileName, fileId, formId, fileBasePath);
    }

    @RequestMapping("standardChangeOfficePreview")
    @ResponseBody
    public void jstbOfficePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("standardChangeFilePathBase");
        rdmZhglFileManager.officeFilePreview(fileName, fileId, formId, fileBasePath, response);
    }

    @RequestMapping("standardChangeImagePreview")
    @ResponseBody
    public void jstbImagePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("standardChangeFilePathBase");
        rdmZhglFileManager.imageFilePreview(fileName, fileId, formId, fileBasePath, response);
    }

    @RequestMapping("delstandardChangeFile")
    public void delJstbFile(HttpServletRequest request, HttpServletResponse response, @RequestBody String postBody) {
        JSONObject postBodyObj = JSONObject.parseObject(postBody);
        String fileName = postBodyObj.getString("fileName");
        String fileId = postBodyObj.getString("id");
        String standardId = postBodyObj.getString("formId");
        standardChangeService.deleteOnestandardChangeFile(fileId, fileName, standardId);
    }

    @RequestMapping("deletestandardChange")
    @ResponseBody
    public JsonResult deleteJstb(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return standardChangeService.deletestandardChange(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteJstb", e);
            return new JsonResult(false, e.getMessage());
        }
    }
}
