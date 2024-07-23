package com.redxun.keyDesign.core.controller;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.keyDesign.core.dao.KeyGjllDao;
import com.redxun.keyDesign.core.service.KeyGjllService;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.controller.GenericController;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import com.redxun.zlgjNPI.core.manager.GjllService;
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
@RequestMapping("/Gjll/")
public class KeyGjllController extends GenericController {
    @Autowired
    private KeyGjllService keyGjllService;
    @Autowired
    private KeyGjllDao keyGjllDao;
    @Autowired
    private GjllService gjllService;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;

    @RequestMapping("openUploadWindow")
    public ModelAndView openUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "keyDesign/core/keyGjllFileUpload.jsp";
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
            keyGjllService.saveGjllUploadFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            logger.error("Exception in upload", e);
            modelMap.put("success", "false");
        }
        return modelMap;
    }


    @RequestMapping("keyGjllListPage")
    public ModelAndView management(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "keyDesign/core/keyGjllList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        Map<String, Object> params = new HashMap<>();
        String type =RequestUtil.getString(request,"type","");
        boolean isGlr = rdmZhglUtil.judgeIsPointRoleUser(ContextUtil.getCurrentUserId(), "关键零部件管理人");
        mv.addObject("isGlr", isGlr);
        mv.addObject("type", type);
        params.put("userId", ContextUtil.getCurrentUserId());
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        // 角色信息
        String userId=ContextUtil.getCurrentUserId();
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserId", userId);
        return mv;
    }

    // 标准新增和编辑页面
    @RequestMapping("edit")
    public ModelAndView editGjll(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "keyDesign/core/keyGjllEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String action = RequestUtil.getString(request, "action");
        String type =RequestUtil.getString(request,"type","");
        String gjId = RequestUtil.getString(request, "gjId");
        Map<String, Object> params = new HashMap<>();
        params.put("id", type);
        JSONObject dept = keyGjllDao.queryBj(params);
        String codeName = dept.getString("codeName");
        mv.addObject("codeName", codeName);
        mv.addObject("action", action);
        mv.addObject("gjId", gjId).addObject("type", type);
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
            gjllobj = keyGjllService.getGjllDetail(gjId);
        }
        return gjllobj;
    }

    // 标准列表查询
    @RequestMapping("queryList")
    @ResponseBody
    public JsonPageResult<?> queryGjllList(HttpServletRequest request, HttpServletResponse response) {
        return keyGjllService.queryGjll(request, response, true);
    }

    @RequestMapping("saveGjll")
    @ResponseBody
    public JsonResult saveGjll(HttpServletRequest request, @RequestBody JSONObject formDataJson,
        HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "成功");
        String type = RequestUtil.getString(request, "type");
        try {
            if (formDataJson == null || formDataJson.isEmpty()) {
                logger.warn("formData is blank");
                result.setSuccess(false);
                result.setMessage("requestBody is blank");
                return result;
            }
            if (StringUtils.isBlank(formDataJson.getString("gjId"))) {
                formDataJson.put("belongbj",type);
                keyGjllService.insertGjll(formDataJson);
            } else {
                keyGjllService.updateGjll(formDataJson);
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

    @RequestMapping("linkGjll")
    @ResponseBody
    public JsonResult linkGjll(HttpServletRequest request, HttpServletResponse response)  {
        JsonResult result = new JsonResult(true, "关联成功");
        try {
            JSONObject gjllobj = new JSONObject();
            JSONObject formDataJson = new JSONObject();
            String belongbj = RequestUtil.getString(request, "belongbj");
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            for (String gjId : ids) {
                gjllobj = gjllService.getGjllDetail(gjId);
                formDataJson.put("codeName",gjllobj.getString("gzlj"));
                formDataJson.put("supplier",gjllobj.getString("lbjgys"));
                formDataJson.put("problem",gjllobj.getString("wtms"));
                formDataJson.put("reason",gjllobj.getString("reason"));
                formDataJson.put("solve",gjllobj.getString("cqcs"));
                formDataJson.put("changeTime",gjllobj.getString("qhTime"));
                formDataJson.put("situation",gjllobj.getString("yjqhch"));
                formDataJson.put("changeFile",gjllobj.getString("tzdh"));
                formDataJson.put("belongbj",belongbj);
                keyGjllService.linkGjll(formDataJson,gjId);
            }

        } catch (Exception e) {
            logger.error("Exception in save Cnsx");
            result.setSuccess(false);
            result.setMessage("Exception in save Cnsx");
            return result;
        }
        return result;
    }

    @RequestMapping("getFileList")
    @ResponseBody
    public List<JSONObject> getgjllFileList(HttpServletRequest request, HttpServletResponse response) {
        List<String> gjIdList = Arrays.asList(RequestUtil.getString(request, "gjId", ""));
        return keyGjllService.getGjllFileList(gjIdList);
    }

    @RequestMapping("gjllPdfPreview")
    public ResponseEntity<byte[]> gjllPdfPreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("keyGjllFilePathBase");
        return rdmZhglFileManager.pdfPreviewOrDownLoad(fileName, fileId, formId, fileBasePath);
    }

    @RequestMapping("gjllOfficePreview")
    @ResponseBody
    public void gjllOfficePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("keyGjllFilePathBase");
        rdmZhglFileManager.officeFilePreview(fileName, fileId, formId, fileBasePath, response);
    }

    @RequestMapping("gjllImagePreview")
    @ResponseBody
    public void gjllImagePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("keyGjllFilePathBase");
        rdmZhglFileManager.imageFilePreview(fileName, fileId, formId, fileBasePath, response);
    }

    @RequestMapping("deleteGjllFile")
    public void deleteGjllFile(HttpServletRequest request, HttpServletResponse response, @RequestBody String postBody) {
        JSONObject postBodyObj = JSONObject.parseObject(postBody);
        String fileName = postBodyObj.getString("fileName");
        String fileId = postBodyObj.getString("id");
        String gjId = postBodyObj.getString("formId");
        keyGjllService.deleteOneGjllFile(fileId, fileName, gjId);
    }

    @RequestMapping("deleteGjll")
    @ResponseBody
    public JsonResult deleteGjll(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return keyGjllService.deleteGjll(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteGjll", e);
            return new JsonResult(false, e.getMessage());
        }
    }

}
