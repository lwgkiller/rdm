package com.redxun.rdmZhgl.core.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmZhgl.core.dao.ZljsjdsDao;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.ZljsjdsService;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.sys.core.entity.SysDic;
import com.redxun.sys.core.manager.SysDicManager;
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
@RequestMapping("/zhgl/core/jsjds/")
public class ZljsjdsController {
    private Logger logger = LogManager.getLogger(ZljsjdsController.class);
    @Resource
    private ZljsjdsService zljsjdsService;
    @Resource
    private ZljsjdsDao zljsjdsDao;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Resource
    private BpmInstManager bpmInstManager;
    @Autowired
    private CommonBpmManager commonBpmManager;
    @Autowired
    private SysDicManager sysDicManager;

    @RequestMapping("listPage")
    public ModelAndView listPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/zljsjdsList.jsp";
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

    @RequestMapping("getJsjdsList")
    @ResponseBody
    public JsonPageResult<?> getJsjdsList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return zljsjdsService.getJsjdsList(request);
    }

    @RequestMapping("deleteJsjds")
    @ResponseBody
    public JsonResult deleteJsjds(HttpServletRequest request, HttpServletResponse response) throws Exception {
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
            return zljsjdsService.deleteJsjds(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteRjzz", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("editPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/zljsjdsEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String jsjdsId = RequestUtil.getString(request, "jsjdsId");
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
        mv.addObject("jsjdsId", jsjdsId).addObject("action", action).addObject("status", status);
        // 取出节点变量，返回到页面
        if (StringUtils.isNotBlank(nodeId)) {
            mv.addObject("nodeId", nodeId);
            List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId, "ZLSQ-JSJDS", null);
            if (nodeVars != null && !nodeVars.isEmpty()) {
                mv.addObject("nodeVars", JSONObject.toJSONString(nodeVars));
            }
        }
        boolean needReport = false;
        List<SysDic> sysDics = sysDicManager.getByTreeKey("JSJDSJSBGSCBM");
        needReport = sysDics.stream().anyMatch(s->s.getKey().equalsIgnoreCase(ContextUtil.getCurrentUser().getMainGroupName()));
        mv.addObject("needReport", needReport);
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));

        return mv;
    }

    @RequestMapping("getJsjdsDetail")
    @ResponseBody
    public JSONObject getJsjdsDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject jsjdsObj = new JSONObject();
        String jsjdsId = RequestUtil.getString(request, "jsjdsId");
        if (StringUtils.isNotBlank(jsjdsId)) {
            jsjdsObj = zljsjdsService.getJsjdsDetail(jsjdsId);
        }
        return jsjdsObj;
    }

    @RequestMapping("saveJsjds")
    @ResponseBody
    public JsonResult saveJsjds(HttpServletRequest request, @RequestBody String jsjdsStr,
        HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "");
        if (StringUtils.isBlank(jsjdsStr)) {
            logger.warn("requestBody is blank");
            result.setSuccess(false);
            result.setMessage("requestBody is blank");
            return result;
        }
        try {
            JSONObject formDataJson = JSONObject.parseObject(jsjdsStr);
            if (formDataJson == null || formDataJson.isEmpty()) {
                logger.warn("formData is blank");
                result.setSuccess(false);
                result.setMessage("requestBody is blank");
                return result;
            }
            if (StringUtils.isBlank(formDataJson.getString("jsjdsId"))) {
                zljsjdsService.createJsjds(formDataJson);
            } else {
                zljsjdsService.updateJsjds(formDataJson);
            }
            JSONObject zgzlJson = zljsjdsDao.selectZgzlByJdsId(formDataJson.getString("jsjdsId"));
            // 项目id
            String projectId = formDataJson.getString("projectId");
            // 项目名称
            String projectName = formDataJson.getString("projectName");
            // 计划id
            String outPlanId = formDataJson.getString("planId");
            // 计划名称
            String planName = formDataJson.getString("planName");

            if(zgzlJson!=null&&!zgzlJson.isEmpty()&&StringUtils.isNotBlank(projectId)&&StringUtils.isNotBlank(outPlanId)){
                zgzlJson.put("projectId",projectId);
                zgzlJson.put("projectName",projectName);
                zgzlJson.put("planId",outPlanId);
                zgzlJson.put("planName",planName);
                zljsjdsService.saveOutList(zgzlJson);
                zljsjdsDao.updateZgzlProject(zgzlJson);
            }
        } catch (Exception e) {
            logger.error("Exception in save rjzz");
            result.setSuccess(false);
            result.setMessage("Exception in save rjzz");
            return result;
        }
        return result;
    }

    @RequestMapping("selectProjectPlan")
    @ResponseBody
    public List<JSONObject> selectProjectPlan(HttpServletRequest request, HttpServletResponse response) {
        return zljsjdsService.selectProjectPlan(request);
    }

    @RequestMapping("saveOutList")
    @ResponseBody
    public JsonResult saveOutList(HttpServletRequest request, @RequestBody String jsjdsStr,
                                HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "");
        if (StringUtils.isBlank(jsjdsStr)) {
            logger.warn("requestBody is blank");
            result.setSuccess(false);
            result.setMessage("requestBody is blank");
            return result;
        }
        try {
            JSONObject formDataJson = JSONObject.parseObject(jsjdsStr);
            if (formDataJson == null || formDataJson.isEmpty()) {
                logger.warn("formData is blank");
                result.setSuccess(false);
                result.setMessage("requestBody is blank");
                return result;
            }
            zljsjdsService.saveOutList(formDataJson);
        } catch (Exception e) {
            logger.error("Exception in saveOutList");
            result.setSuccess(false);
            result.setMessage("Exception saveOutList");
            return result;
        }
        return result;
    }

    @RequestMapping("getJsjdsFileList")
    @ResponseBody
    public List<JSONObject> getJsjdsFileList(HttpServletRequest request, HttpServletResponse response) {
        List<String> jsjdsIdList = Arrays.asList(RequestUtil.getString(request, "jsjdsId", ""));
        return zljsjdsService.getJsjdsFileList(jsjdsIdList);
    }


    @RequestMapping("fileUploadWindow")
    public ModelAndView fileUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/zljsjdsFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("jsjdsId", RequestUtil.getString(request, "jsjdsId", ""));
        return mv;
    }

    @RequestMapping(value = "fileUpload")
    @ResponseBody
    public Map<String, Object> fileUpload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        try {
            // 先保存文件，成功后再写入数据库，前台是一个一个文件的调用
            zljsjdsService.saveUploadFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            logger.error("Exception in upload", e);
            modelMap.put("success", "false");
        }
        return modelMap;
    }

    @RequestMapping("delJsjdsFile")
    public void delJsjdsFile(HttpServletRequest request, HttpServletResponse response, @RequestBody String postBody) {
        JSONObject postBodyObj = JSONObject.parseObject(postBody);
        String fileName = postBodyObj.getString("fileName");
        String fileId = postBodyObj.getString("id");
        String jsjdsId = postBodyObj.getString("formId");
        zljsjdsService.deleteOneJsjdsFile(fileId, fileName, jsjdsId);
    }

    @RequestMapping("jsjdsPdfPreview")
    public ResponseEntity<byte[]> jsjdsPdfPreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("zljsjdsFilePathBase");
        return rdmZhglFileManager.pdfPreviewOrDownLoad(fileName, fileId, formId, fileBasePath);
    }

    @RequestMapping("jsjdsOfficePreview")
    @ResponseBody
    public void jsjdsOfficePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("zljsjdsFilePathBase");
        rdmZhglFileManager.officeFilePreview(fileName, fileId, formId, fileBasePath, response);
    }

    @RequestMapping("jsjdsImagePreview")
    @ResponseBody
    public void jsjdsImagePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("zljsjdsFilePathBase");
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
            fileBasePath = ConstantUtil.PREVIEW.equalsIgnoreCase(action) ? WebAppUtil.getProperty("zljsjdsFilePathBase")
                : WebAppUtil.getProperty("zljsjdsFilePathBase");
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

    @RequestMapping("fileList")
    public ModelAndView fileList(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "rdmZhgl/core/zljsjdsFileList.jsp";
        String jsjdsId = RequestUtil.getString(request, "jsjdsId");
        ModelAndView mv = new ModelAndView(jspPath);
        Map<String, Object> params = new HashMap<>();
        params.put("userId", ContextUtil.getCurrentUserId());
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        // 返回当前登录人角色信息
        List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params);
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
        mv.addObject("jsjdsId", jsjdsId);
        mv.addObject("currentUser", ContextUtil.getCurrentUser());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        return mv;
    }

}
