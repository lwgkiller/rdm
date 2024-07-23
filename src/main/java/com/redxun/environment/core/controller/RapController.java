package com.redxun.environment.core.controller;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.environment.core.service.RapService;
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
@RequestMapping("/environment/core/Rap/")
public class RapController extends GenericController {
    @Autowired
    private RapService rapService;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;

    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;

    @RequestMapping("openUploadWindow")
    public ModelAndView openUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rap/core/rapFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("rapId", RequestUtil.getString(request, "rapId", ""));
        return mv;
    }

    @RequestMapping(value = "upload")
    @ResponseBody
    public Map<String, Object> upload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        try {
            // 先保存文件到磁盘，成功后再写入数据库，前台是一个一个文件的调用
            rapService.saveRapUploadFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            logger.error("Exception in upload", e);
            modelMap.put("success", "false");
        }
        return modelMap;
    }
    @RequestMapping("fileList")
    public ModelAndView fileList(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "rap/core/rapFileList.jsp";
        String rapId = RequestUtil.getString(request, "rapId");
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("rapId", rapId);
        mv.addObject("currentUser", ContextUtil.getCurrentUser());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        return mv;
    }

    @RequestMapping("list")
    public ModelAndView management(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "rap/core/rapList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        Map<String, Object> params = new HashMap<>();
        params.put("userId", ContextUtil.getCurrentUserId());
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        // 角色信息
        String userId=ContextUtil.getCurrentUserId();
        boolean isFGLR = rdmZhglUtil.judgeIsPointRoleUser(userId, "法规录入专员");
        boolean isFGSH = rdmZhglUtil.judgeIsPointRoleUser(userId, "法规审核专员");
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserId", userId);
        mv.addObject("isFGLR", isFGLR);
        mv.addObject("isFGSH", isFGSH);
        return mv;
    }

    // 标准新增和编辑页面
    @RequestMapping("edit")
    public ModelAndView editRap(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "rap/core/rapEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String action = RequestUtil.getString(request, "action");
        String rapId = RequestUtil.getString(request, "rapId");
        if (StringUtils.isBlank(action)) {
            action = "edit";
        }
        mv.addObject("action", action);
        mv.addObject("rapId", rapId);
        mv.addObject("currentUser", ContextUtil.getCurrentUser());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        return mv;
    }

    @RequestMapping("getRapDetail")
    @ResponseBody
    public JSONObject getRapDetail(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        JSONObject rapobj = new JSONObject();
        String rapId = RequestUtil.getString(request, "rapId");
        if (StringUtils.isNotBlank(rapId)) {
            rapobj = rapService.getRapDetail(rapId);
        }
        return rapobj;
    }

    // 标准列表查询
    @RequestMapping("queryList")
    @ResponseBody
    public JsonPageResult<?> queryRapList(HttpServletRequest request, HttpServletResponse response) {
        return rapService.queryRap(request, response, true);
    }

    @RequestMapping("saveRap")
    @ResponseBody
    public JsonResult saveRap(HttpServletRequest request, @RequestBody JSONObject formDataJson,
        HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "成功");
        try {
            if (formDataJson == null || formDataJson.isEmpty()) {
                logger.warn("formData is blank");
                result.setSuccess(false);
                result.setMessage("requestBody is blank");
                return result;
            }
            if (StringUtils.isBlank(formDataJson.getString("rapId"))) {
                rapService.insertRap(formDataJson);
            } else {
                if(StringUtils.isNotBlank(formDataJson.getString("action"))&&formDataJson.getString("action").equals("submit")){
                    formDataJson.put("auditStatus","正在审核");
                }
                if(StringUtils.isNotBlank(formDataJson.getString("action"))&&formDataJson.getString("action").equals("reject")){
                    formDataJson.put("auditStatus","驳回待提交");
                }
                if(StringUtils.isNotBlank(formDataJson.getString("action"))&&formDataJson.getString("action").equals("approve")){
                    formDataJson.put("auditStatus","审核通过");
                }
                rapService.updateRap(formDataJson);
            }
        } catch (Exception e) {
            logger.error("Exception in save rap");
            result.setSuccess(false);
            result.setMessage("Exception in save rap");
            return result;
        }
        return result;
    }

    @RequestMapping("getFileList")
    @ResponseBody
    public List<JSONObject> getrapFileList(HttpServletRequest request, HttpServletResponse response) {
        List<String> rapIdList = Arrays.asList(RequestUtil.getString(request, "rapId", ""));
        return rapService.getRapFileList(rapIdList);
    }

    @RequestMapping("rapPdfPreview")
    public ResponseEntity<byte[]> rapPdfPreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("rapFilePathBase");
        return rdmZhglFileManager.pdfPreviewOrDownLoad(fileName, fileId, formId, fileBasePath);
    }

    @RequestMapping("rapOfficePreview")
    @ResponseBody
    public void rapOfficePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("rapFilePathBase");
        rdmZhglFileManager.officeFilePreview(fileName, fileId, formId, fileBasePath, response);
    }

    @RequestMapping("rapImagePreview")
    @ResponseBody
    public void rapImagePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("rapFilePathBase");
        rdmZhglFileManager.imageFilePreview(fileName, fileId, formId, fileBasePath, response);
    }

    @RequestMapping("deleterapFile")
    public void deleteRapFile(HttpServletRequest request, HttpServletResponse response, @RequestBody String postBody) {
        JSONObject postBodyObj = JSONObject.parseObject(postBody);
        String fileName = postBodyObj.getString("fileName");
        String fileId = postBodyObj.getString("id");
        String rapId = postBodyObj.getString("formId");
        rapService.deleteOneRapFile(fileId, fileName, rapId);
    }

    @RequestMapping("deleterap")
    @ResponseBody
    public JsonResult deleteRap(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return rapService.deleteRap(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteRap", e);
            return new JsonResult(false, e.getMessage());
        }
    }
    @RequestMapping("importTemplateDownload")
    public ResponseEntity<byte[]> importTemplateDownload(HttpServletRequest request, HttpServletResponse response) {
        return rapService.importTemplateDownload();
    }

    /**
     * 批量导入
     * */
    @RequestMapping("importExcel")
    @ResponseBody
    public JSONObject importExcel(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        rapService.importRap(result, request);
        return result;
    }
}
