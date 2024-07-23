package com.redxun.zlgjNPI.core.controller;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.zlgjNPI.core.manager.GjllService;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.controller.GenericController;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
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
@RequestMapping("/zlgjNPI/core/Gjll/")
public class GjllController extends GenericController {
    @Autowired
    private GjllService gjllService;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;

    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;

    @RequestMapping("openUploadWindow")
    public ModelAndView openUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "zlgjNPI/gjllFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("gjId", RequestUtil.getString(request, "gjId", ""));
        return mv;
    }

    @RequestMapping(value = "upload")
    @ResponseBody
    public Map<String, Object> upload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        try {
            // 先保存文件到磁盘，成功后再写入数据库，前台是一个一个文件的调用
            gjllService.saveGjllUploadFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            logger.error("Exception in upload", e);
            modelMap.put("success", "false");
        }
        return modelMap;
    }
    @RequestMapping("fileList")
    public ModelAndView fileList(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "zlgjNPI/gjllFileList.jsp";
        String gjId = RequestUtil.getString(request, "gjId");
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("gjId", gjId);
        mv.addObject("currentUser", ContextUtil.getCurrentUser());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        return mv;
    }

    @RequestMapping("list")
    public ModelAndView management(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "zlgjNPI/gjllList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        Map<String, Object> params = new HashMap<>();
        params.put("userId", ContextUtil.getCurrentUserId());
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        // 角色信息
        String userId=ContextUtil.getCurrentUserId();
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserId", userId);
        String belongbj = RequestUtil.getString(request, "belongbj", "");
        String type = RequestUtil.getString(request, "type", "");
        mv.addObject("belongbj", belongbj).addObject("type", type);
        return mv;
    }

    // 标准新增和编辑页面
    @RequestMapping("edit")
    public ModelAndView editGjll(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "zlgjNPI/gjllEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String action = RequestUtil.getString(request, "action");
        String gjId = RequestUtil.getString(request, "gjId");
        if (StringUtils.isBlank(action)) {
            action = "edit";
        }
        mv.addObject("action", action);
        mv.addObject("gjId", gjId);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUser", ContextUtil.getCurrentUser());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        return mv;
    }

    @RequestMapping("getGjllDetail")
    @ResponseBody
    public JSONObject getGjllDetail(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        JSONObject gjllobj = new JSONObject();
        String gjId = RequestUtil.getString(request, "gjId");
        if (StringUtils.isNotBlank(gjId)) {
            gjllobj = gjllService.getGjllDetail(gjId);
        }
        return gjllobj;
    }

    // 标准列表查询
    @RequestMapping("queryList")
    @ResponseBody
    public JsonPageResult<?> queryGjllList(HttpServletRequest request, HttpServletResponse response) {
        return gjllService.queryGjll(request, response, true);
    }

    @RequestMapping("saveGjll")
    @ResponseBody
    public JsonResult saveGjll(HttpServletRequest request, @RequestBody JSONObject formDataJson,
        HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "成功");
        try {
            if (formDataJson == null || formDataJson.isEmpty()) {
                logger.warn("formData is blank");
                result.setSuccess(false);
                result.setMessage("requestBody is blank");
                return result;
            }
            if (StringUtils.isBlank(formDataJson.getString("gjId"))) {
                gjllService.insertGjll(formDataJson);
            } else {
                gjllService.updateGjll(formDataJson);
            }
        } catch (Exception e) {
            logger.error("Exception in save gjll");
            result.setSuccess(false);
            result.setMessage("Exception in save gjll");
            return result;
        }
        result.setData(formDataJson.getString("gjId"));
        return result;
    }

    @RequestMapping("getFileList")
    @ResponseBody
    public List<JSONObject> getgjllFileList(HttpServletRequest request, HttpServletResponse response) {
        List<String> gjIdList = Arrays.asList(RequestUtil.getString(request, "gjId", ""));
        return gjllService.getGjllFileList(gjIdList);
    }

    @RequestMapping("gjllPdfPreview")
    public ResponseEntity<byte[]> gjllPdfPreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("gjllFilePathBase");
        return rdmZhglFileManager.pdfPreviewOrDownLoad(fileName, fileId, formId, fileBasePath);
    }

    @RequestMapping("gjllOfficePreview")
    @ResponseBody
    public void gjllOfficePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("gjllFilePathBase");
        rdmZhglFileManager.officeFilePreview(fileName, fileId, formId, fileBasePath, response);
    }

    @RequestMapping("gjllImagePreview")
    @ResponseBody
    public void gjllImagePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("gjllFilePathBase");
        rdmZhglFileManager.imageFilePreview(fileName, fileId, formId, fileBasePath, response);
    }

    @RequestMapping("deleteGjllFile")
    public void deleteGjllFile(HttpServletRequest request, HttpServletResponse response, @RequestBody String postBody) {
        JSONObject postBodyObj = JSONObject.parseObject(postBody);
        String fileName = postBodyObj.getString("fileName");
        String fileId = postBodyObj.getString("id");
        String gjId = postBodyObj.getString("formId");
        gjllService.deleteOneGjllFile(fileId, fileName, gjId);
    }

    @RequestMapping("deleteGjll")
    @ResponseBody
    public JsonResult deleteGjll(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return gjllService.deleteGjll(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteGjll", e);
            return new JsonResult(false, e.getMessage());
        }
    }
    @RequestMapping("exportGjllList")
    public void exportExcel(HttpServletRequest request, HttpServletResponse response) {
        gjllService.exportGjllList(request, response);
    }
}
