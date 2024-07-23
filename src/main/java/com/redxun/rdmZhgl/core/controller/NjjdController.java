package com.redxun.rdmZhgl.core.controller;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.rdmCommon.core.manager.LoginRecordManager;
import com.redxun.rdmZhgl.core.dao.NjjdDao;
import com.redxun.rdmZhgl.core.service.JsmmService;
import com.redxun.rdmZhgl.core.service.NjjdService;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.standardManager.core.util.StandardConstant;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

/**
 * 应用模块名称
 * <p>
 * 代码描述
 * <p>
 * Copyright: Copyright (C) 2021 XXX, Inc. All rights reserved.
 * <p>
 * Company: 徐工挖掘机械有限公司
 * <p>
 *
 * @author QY
 * @since 2021/3/25 10:43
 */
@RestController
@RequestMapping("/zhgl/core/njjd/")
public class NjjdController {
    private Logger logger = LogManager.getLogger(NjjdController.class);
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Resource
    private BpmInstManager bpmInstManager;
    @Resource
    private NjjdService njjdService;
    @Resource
    private NjjdDao njjdDao;
    @Autowired
    private LoginRecordManager loginRecordManager;
    @Autowired
    private CommonBpmManager commonBpmManager;

    @RequestMapping("listPage")
    public ModelAndView listPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/njjdList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        Map<String, Object> params = new HashMap<>();
        params.put("userId", ContextUtil.getCurrentUserId());
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        // 返回当前登录人角色信息
        List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params);
        JSONArray userRolesJsonArray = XcmgProjectUtil.convertListMap2JsonArrObject(currentUserRoles);
        mv.addObject("currentUserRoles", userRolesJsonArray);
        // 返回当前用户是否是技术管理部负责人
        boolean isJsglbRespUser = rdmZhglUtil.judgeUserIsJsglbRespUser(ContextUtil.getCurrentUserId());
        mv.addObject("isJsglbRespUser", isJsglbRespUser);
        boolean isNjzy = rdmZhglUtil.judgeIsPointRoleUser(ContextUtil.getCurrentUserId(), "农机鉴定专员");
        mv.addObject("isNjzy", isNjzy);
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        return mv;
    }

    @RequestMapping("getNjjdList")
    @ResponseBody
    public JsonPageResult<?> getNjjdList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return njjdService.getNjjdList(request, response, true);
    }

    @RequestMapping("exportNjjdList")
    public void exportExcel(HttpServletRequest request, HttpServletResponse response) {
        njjdService.exportNjjdList(request, response);
    }

    @RequestMapping("deleteNjjd")
    @ResponseBody
    public JsonResult deleteNjjd(HttpServletRequest request, HttpServletResponse response) throws Exception {
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
            return njjdService.deleteNjjd(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteJsmm", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("editPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/njjdEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        // 新增或者编辑页面由流程引擎跳转并替换url中的pk和nodeId_
        String jsmmId = RequestUtil.getString(request, "njjdId");
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
        mv.addObject("njjdId", jsmmId).addObject("action", action).addObject("status", status);
        // 取出节点变量，返回到页面
        if (StringUtils.isNotBlank(nodeId)) {
            mv.addObject("nodeId", nodeId);
            List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId, "NJJD", null);
            if (nodeVars != null && !nodeVars.isEmpty()) {
                mv.addObject("nodeVars", JSONObject.toJSONString(nodeVars));
            }

        }
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));

        // 返回当前登录人的权限（角色）
        JSONObject isJSZXObj = loginRecordManager.judgeIsJSZX(ContextUtil.getCurrentUser().getMainGroupId(),
            ContextUtil.getCurrentUser().getMainGroupName());
        boolean isJSZX = isJSZXObj.getBooleanValue("isJSZX");
        boolean isFgld = rdmZhglUtil.judgeUserIsFgld(ContextUtil.getCurrentUserId());
        mv.addObject("seeJsfj", isJSZX || isFgld);
        mv.addObject("isJSZX", isJSZX);

        // 是否是营销公司市场部和农机鉴定专员
        JSONObject judgeIsYXSCB = loginRecordManager.judgeIsYXSCB(ContextUtil.getCurrentUser().getMainGroupId());
        boolean isNJJDZY = rdmZhglUtil.judgeIsPointRoleUser(ContextUtil.getCurrentUserId(), "农机鉴定专员");
        boolean isYXSCB = judgeIsYXSCB.getBooleanValue("isYXSCB");
        boolean depName = judgeIsYXSCB.getBooleanValue("depName");
        mv.addObject("isUser", isYXSCB || depName || isNJJDZY);

        mv.addObject("isNjzy", isNJJDZY);
        // 是否是分管领导
        boolean isFGLD = rdmZhglUtil.judgeUserIsFgld(ContextUtil.getCurrentUserId());
        mv.addObject("isFGLD", isFGLD);
        return mv;
    }

    /**
     * 查询基本信息列表
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("getNjjdDetail")
    @ResponseBody
    public JSONObject getNjjdDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject njjdObj = new JSONObject();
        String njjdId = RequestUtil.getString(request, "njjdId");
        if (StringUtils.isNotBlank(njjdId)) {
            njjdObj = njjdService.getNjjdDetail(njjdId);
        }
        return njjdObj;
    }

    @RequestMapping("saveSubsidy")
    @ResponseBody
    public JsonResult saveSubsidy(HttpServletRequest request, @RequestBody String postStr,
        HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "");
        if (StringUtils.isBlank(postStr)) {
            logger.warn("requestBody is blank");
            result.setSuccess(false);
            result.setMessage("请求参数为空");
            return result;
        }
        try {
            JSONObject formDataJson = JSONObject.parseObject(postStr);
            njjdDao.updateNjSubsidy(formDataJson);
        } catch (Exception e) {
            logger.error("Exception in saveSubsidy");
            result.setSuccess(false);
            result.setMessage("系统异常");
            return result;
        }
        return result;
    }

    @RequestMapping("getNjjdFileList")
    @ResponseBody
    public List<JSONObject> getNjjdFileList(HttpServletRequest request, HttpServletResponse response) {
        List<String> njjdIdList = Arrays.asList(RequestUtil.getString(request, "njjdId", ""));
        String njfjDl = RequestUtil.getString(request, "njfjDl", "");
        return njjdService.getNjjdFileList(njjdIdList, njfjDl);
    }

    @RequestMapping("fileUploadWindow")
    public ModelAndView fileUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/njjdFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("njjdId", RequestUtil.getString(request, "njjdId", ""));
        // 查询附件类型
        String njfjDl = RequestUtil.getString(request, "njfjDl", "");
        List<JSONObject> njjdFileTypes = njjdService.queryNjjdFileTypes(njfjDl);
        mv.addObject("typeInfos", njjdFileTypes);
        mv.addObject("njfjDl", njfjDl);
        return mv;
    }

    @RequestMapping(value = "fileUpload")
    @ResponseBody
    public Map<String, Object> fileUpload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        try {
            // 先保存文件，成功后再写入数据库，前台是一个一个文件的调用
            njjdService.saveUploadFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            logger.error("Exception in upload", e);
            modelMap.put("success", "false");
        }
        return modelMap;
    }

    @RequestMapping("njjdPdfPreview")
    public ResponseEntity<byte[]> jsmmPdfPreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        // String fileId = njjdDao.selectFjIdByNjBaseId(formId);
        String fileBasePath = WebAppUtil.getProperty("njjdFilePathBase_preview");
        return rdmZhglFileManager.pdfPreviewOrDownLoad(fileName, fileId, formId, fileBasePath);
    }

    @RequestMapping("njjdOfficePreview")
    @ResponseBody
    public void jsmmOfficePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String formId = RequestUtil.getString(request, "formId");
        String id = RequestUtil.getString(request, "id");
        // String fileId = njjdDao.selectFjIdByNjBaseId(id);
        String fileId = RequestUtil.getString(request, "fileId");
        String fileBasePath = WebAppUtil.getProperty("njjdFilePathBase_preview");
        rdmZhglFileManager.officeFilePreview(fileName, fileId, formId, fileBasePath, response);
    }

    @RequestMapping("njjdImagePreview")
    @ResponseBody
    public void jsmmImagePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        // String fileId = njjdDao.selectFjIdByNjBaseId(formId);
        String fileBasePath = WebAppUtil.getProperty("njjdFilePathBase_preview");
        rdmZhglFileManager.imageFilePreview(fileName, fileId, formId, fileBasePath, response);
    }

    /**
     * 用户信息查询
     * 
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("getUserList")
    @ResponseBody
    public List<JSONObject> getUserList(HttpServletRequest request, HttpServletResponse response) {
        String njjdId = RequestUtil.getString(request, "njjdId");
        return njjdService.getUserList(njjdId);
    }

    /**
     * 用户信息保存
     * 
     * @param request
     * @param response
     * @return
     */
    @PostMapping("saveUserList")
    @ResponseBody
    public JSONObject saveUserList(HttpServletRequest request, @RequestBody String changeGridDataStr,
        HttpServletResponse response) {
        JSONObject result = new JSONObject();
        String njBaseId = RequestUtil.getString(request, "njjdId");
        if (StringUtils.isBlank(changeGridDataStr)) {
            logger.warn("requestBody is blank");
            result.put("message", "保存失败，数据为空！");
            return result;
        }
        njjdService.saveUserList(result, changeGridDataStr, njBaseId);
        if (result.get("message") == null) {
            result.put("message", "保存成功！");
        }
        return result;
    }

    // 用户新增页面 标准新增和编辑页面
    @GetMapping("editUser")
    public ModelAndView editUser(HttpServletRequest request, HttpServletResponse response) {
        // ModelAndView mv = getPathView(request);
        String jspPath = "rdmZhgl/core/userPublicEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String njjdId = RequestUtil.getString(request, "njjdId");
        String standardId = RequestUtil.getString(request, "standardId");
        String action = RequestUtil.getString(request, "action");
        // List<JSONObject> standardObj = new ArrayList<>();
        // if (StringUtils.isNotBlank(njjdId) ) {
        // standardObj = njjdService.getUserList(njjdId);
        // }
        // 根据Id查询用户信息
        JSONObject standardObj = new JSONObject();
        if (StringUtils.isNotBlank(standardId)) {
            Map<String, Object> standardInfo = njjdDao.getUser(standardId);
            standardObj = XcmgProjectUtil.convertMap2JsonObject(standardInfo);
        }
        mv.addObject("standardObj", standardObj);
        mv.addObject("action", action);
        mv.addObject("njjdId", RequestUtil.getString(request, "njjdId"));

        mv.addObject("currentUser", ContextUtil.getCurrentUser());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        mv.addObject("systemCategoryId",
            RequestUtil.getString(request, "systemCategoryId", StandardConstant.SYSTEMCATEGORY_JS));

        return mv;
    }

    // 标准保存（包括新增、编辑）
    @RequestMapping("savePublic")
    @ResponseBody
    public JSONObject savePublic(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        njjdService.savePublicStandard(result, request);
        return result;
    }

    @RequestMapping("deletePublic")
    @ResponseBody
    public JsonResult deletePublic(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            // String uIdStr = RequestUtil.getString(request, "ids");
            // String instIdStr = RequestUtil.getString(request, "instIds");

            String njjdId = RequestUtil.getString(request, "njjdId");

            String id = RequestUtil.getString(request, "id");
            String fileId = RequestUtil.getString(request, "fileId");

            String fileName = RequestUtil.getString(request, "fileName");

            // 删除附件
            Map<String, Object> param = new HashMap<>();
            param.put("id", id);
            param.put("fileId", fileId);
            String standardFilePathBase = WebAppUtil.getProperty("njjdFilePathBase");
            String njjdFilePathBase_preview = WebAppUtil.getProperty("njjdFilePathBase_preview");
            // 查询附件id
            // String fjId = njjdDao.selectFjId(id);

            // 处理下载目录的删除
            String suffix = CommonFuns.toGetFileSuffix(fileName);
            String fileFullPath =
                standardFilePathBase + File.separator + njjdId + File.separator + fileId + '.' + suffix;
            File file = new File(fileFullPath);
            file.delete();
            // 预览目录
            String fileFullPath_re =
                njjdFilePathBase_preview + File.separator + njjdId + File.separator + fileId + '.' + suffix;
            File file_re = new File(fileFullPath_re);
            file_re.delete();
            // 预览删除
            String convertPdfDir = WebAppUtil.getProperty("convertPdfDir");
            String convertPdfPath = njjdFilePathBase_preview + File.separator + njjdId + File.separator + File.separator
                + convertPdfDir + File.separator + fileId + ".pdf";
            File pdffile = new File(convertPdfPath);
            pdffile.delete();

            njjdDao.delFjById(param);
            njjdDao.delUserListById(param);

            /*            if (StringUtils.isNotBlank(instIdStr)) {
                String[] instIds = instIdStr.split(",");
                for (int index = 0; index < instIds.length; index++) {
                    bpmInstManager.deleteCascade(instIds[index], "");
                }
            }*/

            // String[] ids = uIdStr.split(",");
            // return jsmmService.deleteJsmm(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteJsmm", e);
            return new JsonResult(false, e.getMessage());
        }

        return new JsonResult(true, "操作成功");
    }

    // 文档的下载（预览pdf也调用这里）
    @RequestMapping("fileDownload")
    public ResponseEntity<byte[]> fileDownload(HttpServletRequest request, HttpServletResponse response) {
        try {
            String fileName = RequestUtil.getString(request, "fileName");
            if (StringUtils.isBlank(fileName)) {
                logger.error("操作失败，文件名为空！");
                return null;
            }
            String fileId = RequestUtil.getString(request, "fileId");
            if (StringUtils.isBlank(fileId)) {
                logger.error("操作失败，文件Id为空！");
                return null;
            }
            String jsjlId = RequestUtil.getString(request, "njjdId");

            if (jsjlId == null || "".equals(jsjlId)) {
                jsjlId = njjdDao.selectNjjdByFjId(fileId);
            }
            if (StringUtils.isBlank(jsjlId)) {
                logger.error("操作失败，技术交流Id为空！");
                return null;
            }
            // 预览还是下载
            String action = RequestUtil.getString(request, "action");
            if (StringUtils.isBlank(action)) {
                logger.error("操作类型为空");
                return null;
            }
            String fileBasePath = WebAppUtil.getProperty("njjdFilePathBase");
            if (StringUtils.isBlank(fileBasePath)) {
                logger.error("操作失败，找不到存储路径");
                return null;
            }
            String suffix = CommonFuns.toGetFileSuffix(fileName);
            String fullFilePath = fileBasePath + File.separator + jsjlId + File.separator + fileId + "." + suffix;
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

    /**
     * 证书/材料附件删除
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("deleteBookMeg")
    @ResponseBody
    public JsonResult deleteBookMeg(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            // String uIdStr = RequestUtil.getString(request, "ids");
            // String instIdStr = RequestUtil.getString(request, "instIds");

            String njjdId = RequestUtil.getString(request, "njjdId");

            String id = RequestUtil.getString(request, "id");

            String fileName = RequestUtil.getString(request, "fileName");

            // 删除附件
            Map<String, Object> param = new HashMap<>();
            param.put("id", id);
            String standardFilePathBase = WebAppUtil.getProperty("njjdFilePathBase");
            String ylPathBase = WebAppUtil.getProperty("njjdFilePathBase_preview");

            // 查询附件id
            // String fjId = njjdDao.selectFjId(id);
            // 处理下载目录的删除
            String suffix = CommonFuns.toGetFileSuffix(fileName);
            String fileFullPath = standardFilePathBase + File.separator + njjdId + File.separator + id + '.' + suffix;
            File file = new File(fileFullPath);
            file.delete();
            // 目录删除（新加的）
            String ylfilePath = ylPathBase + File.separator + njjdId + File.separator + id + '.' + suffix;
            File ylfile = new File(ylfilePath);
            ylfile.delete();
            // 预览删除
            String convertPdfDir = WebAppUtil.getProperty("convertPdfDir");
            String convertPdfPath = standardFilePathBase + File.separator + njjdId + File.separator + File.separator
                + convertPdfDir + File.separator + id + ".pdf";
            File pdffile = new File(convertPdfPath);
            pdffile.delete();
            // 预览目录新加的
            String ylconvertPdfPath = ylPathBase + File.separator + njjdId + File.separator + File.separator
                + convertPdfDir + File.separator + id + ".pdf";
            File ylpdffile = new File(ylconvertPdfPath);
            ylpdffile.delete();
            njjdDao.delZsById(param);
            // njjdDao.delUserListById(param);

        } catch (Exception e) {
            logger.error("Exception in deleteJsmm", e);
            return new JsonResult(false, e.getMessage());
        }

        return new JsonResult(true, "操作成功");
    }

    /**
     * 农机鉴定模板下载
     */
    @RequestMapping("/importTemplateDownload")
    public ResponseEntity<byte[]> importTemplateDownload(HttpServletRequest request, HttpServletResponse response) {
        return njjdService.importTemplateDownload();
    }

    /**
     * 修改农机鉴定销售状态按钮
     * 
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping("updateSaleButton")
    @ResponseBody
    public JSONObject updateSaleButton(HttpServletRequest request, HttpServletResponse responses,
        @RequestBody String postData) throws Exception {
        JSONObject postDataObj = JSONObject.parseObject(postData);
        String saleStatus = postDataObj.getString("saleStatus");
        if ("0".equals(saleStatus)) {
            saleStatus = "1";
        } else {
            saleStatus = "0";
        }
        String id = postDataObj.getString("id");
        Map<String, Object> param = new HashMap<>();
        param.put("saleStatus", saleStatus);
        param.put("id", id);
        njjdDao.updateSaleButton(param);
        return new JSONObject();
    }

    /**
     * 修改农机鉴定下载状态按钮
     * 
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping("updateSfxzButton")
    @ResponseBody
    public JSONObject updateSfxzButton(HttpServletRequest request, HttpServletResponse responses,
        @RequestBody String postData) throws Exception {
        JSONObject postDataObj = JSONObject.parseObject(postData);
        String saleStatus = postDataObj.getString("saleStatus");
        String applyId = postDataObj.getString("applyId");
        Map<String, Object> param = new HashMap<>();
        param.put("saleStatus", saleStatus);
        param.put("applyId", applyId);
        njjdDao.updateSfxzButton(param);
        return new JSONObject();
    }

    /**
     * 查询该型号是否有在售类型
     * 
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping("selectproductTypeNum")
    @ResponseBody
    public JSONObject selectproductTypeNum(HttpServletRequest request, HttpServletResponse responses,
        @RequestBody String postData) throws Exception {
        JSONObject postDataObj = JSONObject.parseObject(postData);
        String productType = postDataObj.getString("productType");
        String njjdId = postDataObj.getString("njjdId");
        String num = "";
        if ("".equals(njjdId)) {
            num = njjdDao.selectproductTypeNum(productType, njjdId);
        } else {
            num = njjdDao.selectproductTypeNumById(productType, njjdId);
        }
        JSONObject jsonObject = new JSONObject();
        // if (Integer.valueOf(num)>0){
        // //查询用户条数
        // String count = njjdDao.selectUserNum(njjdId);
        // jsonObject.put("count",count);
        // }else {
        // return null;
        // }

        // 查询用户条数
        String count = njjdDao.selectUserNum(njjdId);
        jsonObject.put("num", num);
        jsonObject.put("count", count);

        return jsonObject;
    }

    /**
     * 查询农机产品附件类型
     * 
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping("selectPicNum")
    @ResponseBody
    public JSONObject selectPicNum(HttpServletRequest request, HttpServletResponse responses,
        @RequestBody String postData) throws Exception {
        JSONObject postDataObj = JSONObject.parseObject(postData);
        String njjdId = postDataObj.getString("njjdId");
        String count = njjdDao.selectPicNum(njjdId);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("count", count);

        return jsonObject;
    }

    /**
     * 查询证书类型
     * 
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping("selectZsNum")
    @ResponseBody
    public JSONObject selectZsNum(HttpServletRequest request, HttpServletResponse responses,
        @RequestBody String postData) throws Exception {
        JSONObject postDataObj = JSONObject.parseObject(postData);
        String njjdId = postDataObj.getString("njjdId");
        String count = njjdDao.selectZsNum(njjdId);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("count", count);

        return jsonObject;
    }

    // 判断预览或者下载是否需要申请(1---已有审批完成的未使用的申请，2---已有草稿或运行中的申请，3---需要增加新申请)
    @RequestMapping("checkOperateApply")
    @ResponseBody
    public Map<String, String> checkOperateApply(HttpServletRequest request, @RequestBody String requestBody,
        HttpServletResponse response) throws Exception {
        Map<String, String> result = new HashMap<>();
        if (StringUtils.isBlank(requestBody)) {
            logger.error("请求体为空！");
            result.put("result", "3");
            return result;
        }
        njjdService.checkOperateApply(requestBody, result);
        return result;
    }

    @RequestMapping("getXzpzList")
    @ResponseBody
    public List<JSONObject> getXzpzList(HttpServletRequest request, HttpServletResponse response) {
        String njjdId = RequestUtil.getString(request, "njjdId");
        return njjdService.getXzpzList(njjdId);
    }

    @RequestMapping("exportNjjdcpgg")
    public void exportCpggExcel(HttpServletRequest request, HttpServletResponse response) {
        String njjdId = RequestUtil.getString(request, "njjdId");
        njjdService.exportNjjdcpgg(request, response, njjdId);
    }
}
