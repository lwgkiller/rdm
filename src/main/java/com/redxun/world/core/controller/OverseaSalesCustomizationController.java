
package com.redxun.world.core.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.manager.MybatisBaseManager;
import com.redxun.core.util.ExceptionUtil;
import com.redxun.core.util.OfficeDocPreview;
import com.redxun.core.util.StringUtil;
import com.redxun.org.api.model.IUser;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.controller.MybatisListController;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.serviceEngineering.core.controller.DecorationManualTopicController;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.sys.core.util.SysPropertiesUtil;
import com.redxun.sys.org.entity.OsDimension;
import com.redxun.world.core.dao.WorldResearchDao;
import com.redxun.world.core.service.OverseaSalesCustomizationService;
import com.redxun.world.core.service.WorldResearchService;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.commons.lang.StringUtils;
import org.owasp.validator.html.util.ErrorMessageUtil;
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

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.*;

@Controller
@RequestMapping("/world/core/overseaSalesCustomization/")
public class OverseaSalesCustomizationController {
    private static final Logger logger = LoggerFactory.getLogger(OverseaSalesCustomizationController.class);
    @Autowired
    private SysDicManager sysDicManager;
    @Autowired
    private CommonInfoManager commonInfoManager;
    @Autowired
    private OverseaSalesCustomizationService overseaSalesCustomizationService;

    //..
    @RequestMapping("modelsPage")
    public ModelAndView dataListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "world/core/overseasalesCustomizationModels.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("productGroupList", sysDicManager.getSysDicKeyValueListByTreeKey("overseaSalesCustomizationProductGroup"));
        boolean isOverseaSalesCustomizationAdmins =
                commonInfoManager.judgeUserIsPointRole("海外销售标选配管理员",ContextUtil.getCurrentUserId());
        mv.addObject("isOverseaSalesCustomizationAdmins", isOverseaSalesCustomizationAdmins);
        boolean isOverseaSalesCustomizationModelAdmin =
                commonInfoManager.judgeUserIsPointRole("海外标选配机型授权人",ContextUtil.getCurrentUserId());
        mv.addObject("isOverseaSalesCustomizationModelAdmin", isOverseaSalesCustomizationModelAdmin);
        return mv;
    }

    //..
    @RequestMapping("modelEditPage")
    public ModelAndView modelEditPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "world/core/overseasalesCustomizationModelEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String businessId = RequestUtil.getString(request, "businessId");
        String action = RequestUtil.getString(request, "action");
        mv.addObject("businessId", businessId).addObject("action", action);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        boolean isOverseaSalesCustomizationAdmins =
                commonInfoManager.judgeUserIsPointRole("海外销售标选配管理员",ContextUtil.getCurrentUserId());
        mv.addObject("isOverseaSalesCustomizationAdmins", isOverseaSalesCustomizationAdmins);
        boolean isOverseaSalesCustomizationModelAdmin =
                commonInfoManager.judgeUserIsPointRole("海外标选配机型授权人",ContextUtil.getCurrentUserId());
        mv.addObject("isOverseaSalesCustomizationModelAdmin", isOverseaSalesCustomizationModelAdmin);
        return mv;
    }

    //..
    @RequestMapping("modelListQuery")
    @ResponseBody
    public JsonPageResult<?> modelListQuery(HttpServletRequest request, HttpServletResponse response) {
        return overseaSalesCustomizationService.modelListQuery(request, response);
    }

    //..
    @RequestMapping("getModelDataById")
    @ResponseBody
    public JSONObject getModelDataById(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = RequestUtil.getString(request, "businessId");
        if (StringUtil.isEmpty(id)) {
            return new JSONObject().fluentPut("responsibleUserId", ContextUtil.getCurrentUserId())
                    .fluentPut("responsibleUser", ContextUtil.getCurrentUser().getFullname());
        } else {
            return overseaSalesCustomizationService.getModelDataById(id);
        }
    }

    //..
    @RequestMapping(value = "saveModel", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult saveModel(HttpServletRequest request, HttpServletResponse response) {
        try {
            return overseaSalesCustomizationService.saveModel(request);
        } catch (Exception e) {
            logger.error("Exception in saveModel", e);
            return new JsonResult(false, e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
        }
    }

    //..
    @RequestMapping("copyModel")
    @ResponseBody
    public JsonResult copyModel(HttpServletRequest request, HttpServletResponse response) {
        try {
            return overseaSalesCustomizationService.copyModel(request);
        } catch (Exception e) {
            logger.error("Exception in copyModel", e);
            return new JsonResult(false, ExceptionUtil.getExceptionMessage(e));
        }
    }

    //..
    @RequestMapping("deleteModel")
    @ResponseBody
    public JsonResult deleteModel(HttpServletRequest request, HttpServletResponse response) {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return overseaSalesCustomizationService.deleteModel(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteModel", e);
            return new JsonResult(false, e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
        }
    }

    //..
    @RequestMapping("configPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "world/core/overseasalesCustomizationConfig.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String businessId = RequestUtil.getString(request, "businessId");
        String action = RequestUtil.getString(request, "action");
        mv.addObject("businessId", businessId).addObject("action", action);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("nodeTypeList", sysDicManager.getSysDicKeyValueListByTreeKey("overseaSalesCustomizationNodeType"));
        return mv;
    }

    //..
    @RequestMapping("configTreeQuery")
    @ResponseBody
    public List<JSONObject> configTreeQuery(HttpServletRequest request, HttpServletResponse response) {
        return overseaSalesCustomizationService.configTreeQuery(request, response);
    }

    //..
    @RequestMapping(value = "saveConfigTree", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult saveConfigTree(HttpServletRequest request, HttpServletResponse response,
                                     @RequestBody String DataStr) {
        JSONArray jsonArray = JSONObject.parseArray(DataStr);
        if (jsonArray == null || jsonArray.isEmpty()) {
            return new JsonResult(false, "没有待更新的内容！");
        }
        try {
            return overseaSalesCustomizationService.saveConfigTree(jsonArray);
        } catch (Exception e) {
            logger.error("Exception in saveConfigTree", e);
            return new JsonResult(false, e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
        }
    }

    //..
    @RequestMapping("openFileWindow")
    public ModelAndView openFileWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "world/core/overseaSalesCustomizationFileWindow.jsp";
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

    //..
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
        return overseaSalesCustomizationService.getFileListInfos(params);
    }

    //..
    @RequestMapping("fileUpload")
    @ResponseBody
    public JsonResult fileUpload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            return overseaSalesCustomizationService.saveFiles(request);
        } catch (Exception e) {
            logger.error("Exception in fileUpload", e);
            return new JsonResult(false, e.getCause().toString());
        }
    }

    //..
    @RequestMapping("openFileUploadWindow")
    public ModelAndView openFileUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "world/core/overseaSalesCustomizationFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("businessId", RequestUtil.getString(request, "businessId", ""));
        mv.addObject("businessType", RequestUtil.getString(request, "businessType", ""));
        mv.addObject("urlCut", RequestUtil.getString(request, "urlCut", ""));
        mv.addObject("fileType", RequestUtil.getString(request, "fileType", ""));
        return mv;
    }

    //..
    @RequestMapping("pdfPreview")
    public ResponseEntity<byte[]> pdfPreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String businessId = RequestUtil.getString(request, "businessId");
        String businessType = RequestUtil.getString(request, "businessType");
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey("worldUploadPosition", businessType).getValue();
        return overseaSalesCustomizationService.pdfPreviewOrDownLoad(fileName, fileId, businessId, filePathBase);
    }

    //..
    @RequestMapping("officePreview")
    @ResponseBody
    public void officePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String businessId = RequestUtil.getString(request, "businessId");
        String businessType = RequestUtil.getString(request, "businessType");
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey("worldUploadPosition", businessType).getValue();
        overseaSalesCustomizationService.officeFilePreview(fileName, fileId, businessId, filePathBase, response);
    }

    //..
    @RequestMapping("imagePreview")
    @ResponseBody
    public void imagePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String businessId = RequestUtil.getString(request, "businessId");
        String businessType = RequestUtil.getString(request, "businessType");
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey("worldUploadPosition", businessType).getValue();
        overseaSalesCustomizationService.imageFilePreview(fileName, fileId, businessId, filePathBase, response);
    }

    //..
    @RequestMapping("deleteFile")
    public void deleteFile(HttpServletRequest request, HttpServletResponse response, @RequestBody String postBody) {
        JSONObject postBodyObj = JSONObject.parseObject(postBody);
        String fileName = postBodyObj.getString("fileName");
        String fileId = postBodyObj.getString("id");
        String businessId = postBodyObj.getString("businessId");
        String businessType = postBodyObj.getString("businessType");
        try {
            overseaSalesCustomizationService.deleteFile(fileId, fileName, businessId, businessType);
        } catch (Exception e) {
            logger.error("Exception in deleteFile", e);
        }
    }

    //..获取产品型谱
    @RequestMapping("productTypeSpectrumListQuery")
    @ResponseBody
    public List<JSONObject> productTypeSpectrumListQuery(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            return overseaSalesCustomizationService.productTypeSpectrumListQuery(request, response);
        } catch (Exception e) {
            List<JSONObject> list = new ArrayList<>();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("designModel", e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
            jsonObject.put("saleModel", e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
            jsonObject.put("materialCode", e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
            jsonObject.put("productManagerName", e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
            list.add(jsonObject);
            return list;
        }
    }
}
