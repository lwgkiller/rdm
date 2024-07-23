
package com.redxun.productDataManagement.core.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.FileUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.productDataManagement.core.manager.AttachedtoolsSpectrumService;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.sys.core.manager.SysDicManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/productDataManagement/core/attachedtoolsSpectrum/")
public class AttachedtoolsSpectrumController {
    private static final Logger logger = LoggerFactory.getLogger(AttachedtoolsSpectrumController.class);
    @Autowired
    private SysDicManager sysDicManager;
    @Autowired
    private CommonInfoManager commonInfoManager;
    @Autowired
    private AttachedtoolsSpectrumService attachedtoolsSpectrumService;

    //..
    @RequestMapping("imageView")
    public void imageView(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String fileId = request.getParameter("fileId");
        String fileName = request.getParameter("fileName");
        String suffix = "";
        if (StringUtil.isNotEmpty(fileName)) {
            suffix = fileName.split("\\.")[1];
        }
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey("pdmUploadPosition",
                "attachedtoolsSpectrumProductPic").getValue();
        String filePath = filePathBase + File.separator + fileId + "." + suffix;
        //设置response的编码方式
        response.setContentType("image/" + suffix);
        // 创建file对象
        File file = new File(filePath);
        if (file.exists()) {
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            FileUtil.downLoad(file, response);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    //..
    @RequestMapping("modelsPage")
    public ModelAndView modelsPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "productDataManagement/core/attachedtoolsSpectrumList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        boolean isAttachedtoolsSpectrumAdmin =
                commonInfoManager.judgeUserIsPointRole("属具型谱管理员", ContextUtil.getCurrentUserId());
        mv.addObject("isAttachedtoolsSpectrumAdmin", isAttachedtoolsSpectrumAdmin);
        return mv;
    }

    //..
    @RequestMapping("modelEditPage")
    public ModelAndView modelEditPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "productDataManagement/core/attachedtoolsSpectrumEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String businessId = RequestUtil.getString(request, "businessId");
        String action = RequestUtil.getString(request, "action");
        mv.addObject("businessId", businessId).addObject("action", action);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        boolean isAttachedtoolsSpectrumAdmin =
                commonInfoManager.judgeUserIsPointRole("属具型谱管理员", ContextUtil.getCurrentUserId());
        mv.addObject("isAttachedtoolsSpectrumAdmin", isAttachedtoolsSpectrumAdmin);
        return mv;
    }

    //..
    @RequestMapping("configPage")
    public ModelAndView configPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "productDataManagement/core/attachedtoolsSpectrumItemList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String mainId = RequestUtil.getString(request, "mainId");
        String action = RequestUtil.getString(request, "action");
        mv.addObject("mainId", mainId).addObject("action", action);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        boolean isAttachedtoolsSpectrumAdmin =
                commonInfoManager.judgeUserIsPointRole("属具型谱管理员", ContextUtil.getCurrentUserId());
        mv.addObject("isAttachedtoolsSpectrumAdmin", isAttachedtoolsSpectrumAdmin);
        return mv;
    }

    //..
    @RequestMapping("browsePage")
    public ModelAndView browsePage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "productDataManagement/core/attachedtoolsSpectrumBrowse.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        return mv;
    }

    //..
    @RequestMapping("modelListQuery")
    @ResponseBody
    public JsonPageResult<?> modelListQuery(HttpServletRequest request, HttpServletResponse response) {
        return attachedtoolsSpectrumService.modelListQuery(request, response);
    }

    //..
    @RequestMapping("modelGroupQuery")
    @ResponseBody
    public JSONArray modelGroupQuery(HttpServletRequest request, HttpServletResponse response) {
        return attachedtoolsSpectrumService.modelGroupQuery(request, response);
    }

    //..
    @RequestMapping("modelQueryByGroup")
    @ResponseBody
    public JSONArray modelQueryByGroup(HttpServletRequest request, HttpServletResponse response) {
        return attachedtoolsSpectrumService.modelQueryByGroup(request, response);
    }

    //..
    @RequestMapping("getModelDataById")
    @ResponseBody
    public JSONObject getModelDataById(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = RequestUtil.getString(request, "businessId");
        return attachedtoolsSpectrumService.getModelDataById(id);
    }

    //..
    @RequestMapping(value = "saveModel", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult saveModel(HttpServletRequest request, HttpServletResponse response) {
        try {
            return attachedtoolsSpectrumService.saveModel(request);
        } catch (Exception e) {
            logger.error("Exception in saveModel", e);
            return new JsonResult(false, e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
        }
    }

    //
    @RequestMapping("deleteModel")
    @ResponseBody
    public JsonResult deleteModel(HttpServletRequest request, HttpServletResponse response) {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return attachedtoolsSpectrumService.deleteModel(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteModel", e);
            return new JsonResult(false, e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
        }
    }

    //..
    @RequestMapping(value = "getParameterCfg", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult getParameterCfg(HttpServletRequest request, HttpServletResponse response,
                                      @RequestBody String DataStr) {
        try {
            String mainId = RequestUtil.getString(request, "mainId");
            return attachedtoolsSpectrumService.getParameterCfg(mainId);
        } catch (Exception e) {
            logger.error("Exception in getParameterCfg", e);
            return new JsonResult(false, e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
        }
    }

    //..
    @RequestMapping("itemListQuery")
    @ResponseBody
    public JsonPageResult<?> itemListQuery(HttpServletRequest request, HttpServletResponse response) {
        return attachedtoolsSpectrumService.itemListQuery(request, response);
    }

    //..
    @RequestMapping("itemListQueryWithOutParamProcess")
    @ResponseBody
    public JsonPageResult<?> itemListQueryWithOutParamProcess(HttpServletRequest request, HttpServletResponse response) {
        return attachedtoolsSpectrumService.itemListQueryWithOutParamProcess(request, response);
    }

    //..
    @RequestMapping(value = "saveItems", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult saveItems(HttpServletRequest request, @RequestBody String DataStr,
                                HttpServletResponse response) {
        JSONArray jsonArray = JSONObject.parseArray(DataStr);
        if (jsonArray == null || jsonArray.isEmpty()) {
            return new JsonResult(false, "表单内容为空，操作失败！");
        }
        try {
            String mainId = RequestUtil.getString(request, "mainId");
            return attachedtoolsSpectrumService.saveItems(jsonArray, mainId);
        } catch (Exception e) {
            logger.error("Exception in saveItems", e);
            return new JsonResult(false, e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
        }
    }

    //..
    @RequestMapping(value = "submitItem", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult submitItem(HttpServletRequest request, @RequestBody String DataStr,
                                 HttpServletResponse response) {
        JSONObject jsonObject = JSONObject.parseObject(DataStr);
        if (jsonObject == null) {
            return new JsonResult(false, "表单内容为空，操作失败！");
        }
        try {
            return attachedtoolsSpectrumService.doSubmitItem(jsonObject);
        } catch (Exception e) {
            logger.error("Exception in submitItem", e);
            return new JsonResult(false, e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
        }
    }

    //..
    @RequestMapping(value = "reviewItem", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult reviewItem(HttpServletRequest request, @RequestBody String DataStr,
                                 HttpServletResponse response) {
        JSONObject jsonObject = JSONObject.parseObject(DataStr);
        if (jsonObject == null) {
            return new JsonResult(false, "表单内容为空，操作失败！");
        }
        try {
            return attachedtoolsSpectrumService.doReviewItem(jsonObject);
        } catch (Exception e) {
            logger.error("Exception in reviewItem", e);
            return new JsonResult(false, e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
        }
    }

    //..
    @RequestMapping(value = "backItem", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult backItem(HttpServletRequest request, @RequestBody String DataStr,
                               HttpServletResponse response) {
        JSONObject jsonObject = JSONObject.parseObject(DataStr);
        if (jsonObject == null) {
            return new JsonResult(false, "表单内容为空，操作失败！");
        }
        try {
            return attachedtoolsSpectrumService.doBackItem(jsonObject);
        } catch (Exception e) {
            logger.error("Exception in backItem", e);
            return new JsonResult(false, e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
        }
    }

    //..
    @RequestMapping("deleteItems")
    @ResponseBody
    public JsonResult deleteItems(HttpServletRequest request, HttpServletResponse response) {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return attachedtoolsSpectrumService.deleteItems(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteItems", e);
            return new JsonResult(false, e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
        }
    }

    //..
    @RequestMapping("exportItems")
    public void exportItems(HttpServletRequest request, HttpServletResponse response) throws Exception {
        attachedtoolsSpectrumService.exportItems(request, response);
    }

    //..打开文件列表窗口
    @RequestMapping("openFileWindow")
    public ModelAndView openFileWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "productDataManagement/core/attachedtoolsSpectrumFileWindow.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String businessId = RequestUtil.getString(request, "businessId");
        String businessType = RequestUtil.getString(request, "businessType");
        String action = RequestUtil.getString(request, "action");
        String coverContent = RequestUtil.getString(request, "coverContent");
        mv.addObject("businessId", businessId).addObject("businessType", businessType).
                addObject("action", action).addObject("coverContent", coverContent);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        return mv;
    }

    //..打开新品试制模块文件上传窗口
    @RequestMapping("openFileUploadWindow")
    public ModelAndView openFileUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "productDataManagement/core/attachedtoolsSpectrumFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("businessId", RequestUtil.getString(request, "businessId", ""));
        mv.addObject("businessType", RequestUtil.getString(request, "businessType", ""));
        mv.addObject("urlCut", RequestUtil.getString(request, "urlCut", ""));
        mv.addObject("fileType", RequestUtil.getString(request, "fileType", ""));
        return mv;
    }

    //..获取文件信息
    @RequestMapping("getFileListInfos")
    @ResponseBody
    public List<JSONObject> getFileListInfos(HttpServletRequest request, HttpServletResponse response) {
        List<String> businessIdList = Arrays.asList(RequestUtil.getString(request, "businessId", ""));
        List<String> businessTypeList = new ArrayList<>();//有时只给第一个参数，第二个就要屏蔽掉，否则会有干扰
        if (StringUtil.isNotEmpty(RequestUtil.getString(request, "businessType", ""))) {
            businessTypeList = Arrays.asList(RequestUtil.getString(request, "businessType", ""));
        }
        JSONObject params = new JSONObject();
        params.put("businessIds", businessIdList);
        params.put("businessTypes", businessTypeList);
        return attachedtoolsSpectrumService.getFileListInfos(params);
    }

    //..文件上传命令
    @RequestMapping("fileUpload")
    @ResponseBody
    public JsonResult fileUpload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            return attachedtoolsSpectrumService.saveFiles(request);
        } catch (Exception e) {
            logger.error("Exception in fileUpload", e);
            return new JsonResult(false, e.getCause().toString());
        }
    }

    //..
    @RequestMapping("pdfPreview")
    public ResponseEntity<byte[]> pdfPreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String businessId = RequestUtil.getString(request, "businessId");
        String businessType = RequestUtil.getString(request, "businessType");
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey("pdmUploadPosition", businessType).getValue();
        return attachedtoolsSpectrumService.pdfPreviewOrDownLoad(fileName, fileId, businessId, filePathBase);
    }

    //..
    @RequestMapping("officePreview")
    @ResponseBody
    public void officePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String businessId = RequestUtil.getString(request, "businessId");
        String businessType = RequestUtil.getString(request, "businessType");
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey("pdmUploadPosition", businessType).getValue();
        attachedtoolsSpectrumService.officeFilePreview(fileName, fileId, businessId, filePathBase, response);
    }

    //..
    @RequestMapping("imagePreview")
    @ResponseBody
    public void imagePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String businessId = RequestUtil.getString(request, "businessId");
        String businessType = RequestUtil.getString(request, "businessType");
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey("pdmUploadPosition", businessType).getValue();
        attachedtoolsSpectrumService.imageFilePreview(fileName, fileId, businessId, filePathBase, response);
    }

    //..新品试制文件删除命令
    @RequestMapping("deleteFile")
    public void deleteFile(HttpServletRequest request, HttpServletResponse response, @RequestBody String postBody) {
        JSONObject postBodyObj = JSONObject.parseObject(postBody);
        String fileName = postBodyObj.getString("fileName");
        String fileId = postBodyObj.getString("id");
        String businessId = postBodyObj.getString("businessId");
        String businessType = postBodyObj.getString("businessType");
        try {
            attachedtoolsSpectrumService.deleteFile(fileId, fileName, businessId, businessType);
        } catch (Exception e) {
            logger.error("Exception in deleteFile", e);
        }
    }
}
