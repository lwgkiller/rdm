package com.redxun.rdmZhgl.core.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmZhgl.core.dao.RjzzDao;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RjzzService;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.util.ConstantUtil;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/zhgl/core/rjzz/")
public class RjzzController {
    private Logger logger = LogManager.getLogger(RjzzController.class);
    @Autowired
    private RjzzDao rjzzDao;
    @Resource
    private RjzzService rjzzService;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Resource
    private BpmInstManager bpmInstManager;
    @Autowired
    private CommonBpmManager commonBpmManager;

    @RequestMapping("listPage")
    public ModelAndView listPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/rjzzList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        Map<String, Object> params = new HashMap<>();
        params.put("userId", ContextUtil.getCurrentUserId());
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        // 返回当前登录人角色信息
        List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params);
        JSONArray userRolesJsonArray = XcmgProjectUtil.convertListMap2JsonArrObject(currentUserRoles);
        mv.addObject("currentUserRoles", userRolesJsonArray);
        // 当前用户是否是专利工程师
        boolean isZlgcsUser = false;
        for (int i = 0; i < currentUserRoles.size(); i++) {
            Map<String, Object> map = currentUserRoles.get(i);
            if (RdmConst.ZLGCS.equals(map.get("NAME_"))) {
                isZlgcsUser = true;
                break;
            }
        }
        mv.addObject("isZlgcsUser", isZlgcsUser);
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        return mv;
    }

    @RequestMapping("getRjzzList")
    @ResponseBody
    public JsonPageResult<?> getRjzzList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return rjzzService.getRjzzList(request, response, true);
    }

    @RequestMapping("exportRjzzList")
    public void exportExcel(HttpServletRequest request, HttpServletResponse response) {
        rjzzService.exportRjzzList(request, response);
    }

    @RequestMapping("exportRjzzDetail")
    public void exportRjzzetail(HttpServletRequest request, HttpServletResponse response) {
        rjzzService.exportRjzzDetail(request, response);
    }

    @RequestMapping("deleteRjzz")
    @ResponseBody
    public JsonResult deleteRjzz(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String instIdStr = RequestUtil.getString(request, "instIds");
            if (StringUtils.isNotBlank(instIdStr)) {
                String[] instIds = instIdStr.split(",");
                for (int index = 0; index < instIds.length; index++) {
                    bpmInstManager.deleteCascade(instIds[index], "");
                }
            }
            String[] ids = uIdStr.split(",");
            return rjzzService.deleteRjzz(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteRjzz", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("editPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/rjzzEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String rjzzId = RequestUtil.getString(request, "rjzzId");
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
        mv.addObject("rjzzId", rjzzId).addObject("action", action).addObject("status", status);
        // 取出节点变量，返回到页面
        if (StringUtils.isNotBlank(nodeId)) {
            mv.addObject("nodeId", nodeId);
            List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId,"RJZZ",null);
            if (nodeVars != null && !nodeVars.isEmpty()) {
                mv.addObject("nodeVars", JSONObject.toJSONString(nodeVars));
            }
        }
        // 返回当前登录人角色信息
        Map<String, Object> params = new HashMap<>();
        params.put("userId", ContextUtil.getCurrentUserId());
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params);
        JSONArray userRolesJsonArray = XcmgProjectUtil.convertListMap2JsonArrObject(currentUserRoles);
        mv.addObject("currentUserRoles", userRolesJsonArray);
        // 当前用户是否是专利工程师
        boolean isZlgcsUser = false;
        for (int i = 0; i < currentUserRoles.size(); i++) {
            Map<String, Object> map = currentUserRoles.get(i);
            if (RdmConst.ZLGCS.equals(map.get("NAME_"))) {
                isZlgcsUser = true;
                break;
            }
        }
        mv.addObject("isZlgcsUser", isZlgcsUser);
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));

        return mv;
    }

    @RequestMapping("getRjzzDetail")
    @ResponseBody
    public JSONObject getRjzzDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject rjzzObj = new JSONObject();
        String rjzzId = RequestUtil.getString(request, "rjzzId");
        if (StringUtils.isNotBlank(rjzzId)) {
            rjzzObj = rjzzService.getRjzzDetail(rjzzId);
        }
        return rjzzObj;
    }

    @RequestMapping("saveRjzz")
    @ResponseBody
    public JsonResult saveRjzz(HttpServletRequest request, @RequestBody String xcmgProjectStr,
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
            if (StringUtils.isBlank(formDataJson.getString("rjzzId"))) {
                rjzzService.createRjzz(formDataJson);
            } else {
                rjzzService.updateRjzz(formDataJson);
            }
        } catch (Exception e) {
            logger.error("Exception in save rjzz");
            result.setSuccess(false);
            result.setMessage("Exception in save rjzz");
            return result;
        }
        return result;
    }

    @RequestMapping("getRjzzFileList")
    @ResponseBody
    public List<JSONObject> getRjzzFileList(HttpServletRequest request, HttpServletResponse response) {
        List<String> rjzzIdList = Arrays.asList(RequestUtil.getString(request, "rjzzId", ""));
        return rjzzService.getRjzzFileList(rjzzIdList);
    }

    @RequestMapping("fileUploadWindow")
    public ModelAndView fileUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/rjzzFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("rjzzId", RequestUtil.getString(request, "rjzzId", ""));
        return mv;
    }

    @RequestMapping(value = "fileUpload")
    @ResponseBody
    public Map<String, Object> fileUpload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        try {
            // 先保存文件，成功后再写入数据库，前台是一个一个文件的调用
            rjzzService.saveUploadFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            logger.error("Exception in upload", e);
            modelMap.put("success", "false");
        }
        return modelMap;
    }

    @RequestMapping("delRjzzFile")
    public void delRjzzFile(HttpServletRequest request, HttpServletResponse response, @RequestBody String postBody) {
        JSONObject postBodyObj = JSONObject.parseObject(postBody);
        String fileName = postBodyObj.getString("fileName");
        String fileId = postBodyObj.getString("id");
        String rjzzId = postBodyObj.getString("formId");
        rjzzService.deleteOneRjzzFile(fileId, fileName, rjzzId);
    }

    @RequestMapping("rjzzPdfPreview")
    public ResponseEntity<byte[]> rjzzPdfPreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("rjzzFilePathBase");
        return rdmZhglFileManager.pdfPreviewOrDownLoad(fileName, fileId, formId, fileBasePath);
    }

    @RequestMapping("rjzzOfficePreview")
    @ResponseBody
    public void rjzzOfficePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("rjzzFilePathBase");
        rdmZhglFileManager.officeFilePreview(fileName, fileId, formId, fileBasePath, response);
    }

    @RequestMapping("rjzzImagePreview")
    @ResponseBody
    public void rjzzImagePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("rjzzFilePathBase");
        rdmZhglFileManager.imageFilePreview(fileName, fileId, formId, fileBasePath, response);
    }

    // 文档的下载
    @RequestMapping("fileDownload")
    public ResponseEntity<byte[]> fileDownload(HttpServletRequest request, HttpServletResponse response) {
        try {
            String fileName = RequestUtil.getString(request, "fileName");
            if (StringUtils.isBlank(fileName)) {
                logger.error("操作失败，文件名为空！");
                return null;
            }
            // 预览还是下载，取的根路径不一样
            String action = RequestUtil.getString(request, "action");
            if (StringUtils.isBlank(action)) {
                logger.error("操作类型为空");
                return null;
            }
            String fileId = RequestUtil.getString(request, "fileId");
            if (StringUtils.isBlank(fileId)) {
                logger.error("操作失败，文件主键为空！");
                return null;
            }
            String standardId = RequestUtil.getString(request, "standardId");
            if (StringUtils.isBlank(standardId)) {
                logger.error("操作失败，主表id为空！");
                return null;
            }
            String fileBasePath = "";
            fileBasePath = ConstantUtil.PREVIEW.equalsIgnoreCase(action) ? WebAppUtil.getProperty("rjzzFilePathBase")
                : WebAppUtil.getProperty("rjzzFilePathBase");
            if (StringUtils.isBlank(fileBasePath)) {
                logger.error("操作失败，找不到存储根路径");
                return null;
            }
            String suffix = CommonFuns.toGetFileSuffix(fileName);
            String relativeFilePath = "";
            if (StringUtils.isNotBlank(standardId)) {
                relativeFilePath = File.separator + standardId;
            }
            String realFileName = fileId + "." + suffix;
            String fullFilePath = fileBasePath + relativeFilePath + File.separator + realFileName;
            // 创建文件实例
            File file = new File(fullFilePath);
            // 修改文件名的编码格式
            String downloadFileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");

            // 设置httpHeaders,使浏览器响应下载
            HttpHeaders headers = new HttpHeaders();
            // 告诉浏览器执行下载的操作，“attachment”告诉了浏览器进行下载,下载的文件 文件名为 downloadFileName
            headers.setContentDispositionFormData("attachment", downloadFileName);
            // 设置响应方式为二进制，以二进制流传输
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file), headers, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Exception in fileDownload", e);
            return null;
        }
    }

    @RequestMapping("testupdate")
    @ResponseBody
    public void test(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> param = new HashMap<>();

        List<Map<String, Object>> result = rjzzDao.queryRjzzList(param);
        List<JSONObject> osUserInfos=rjzzDao.queryUserInfos();
        Map<String,String> userName2Id=new HashMap<>();
        for(JSONObject oneUser:osUserInfos) {
            userName2Id.put(oneUser.getString("FULLNAME_"),oneUser.getString("USER_ID_"));
        }
        for(Map<String, Object> oneMap:result) {
            String names=oneMap.get("lxfs").toString();
            oneMap.put("CREATE_BY_",userName2Id.get(names));
            rjzzDao.updaterjkfId(oneMap);
        }
    }
}
