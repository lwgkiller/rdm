package com.redxun.info.core.controller;

import com.alibaba.fastjson.JSONArray;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.OfficeDocPreview;
import com.redxun.info.core.manager.InfoEnterpriseService;
import com.redxun.info.core.model.InfoCpybFiles;
import com.redxun.info.core.model.InfoGngys;
import com.redxun.info.core.model.InfoGwgys;
import com.redxun.info.core.model.InfoGyslxr;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.strategicPlanning.core.domain.dto.ZlghDto;
import com.redxun.strategicPlanning.core.domain.vo.ParamsVo;
import com.redxun.strategicPlanning.core.service.ZlghPlanningService;
import com.redxun.sys.core.util.SysPropertiesUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 企业供应商的控制层
 * @author douhongli
 * @date 2021年5月31日09:58:18
 */
@RestController
@RequestMapping("/info/core/enterprise/")
public class InfoEnterPriseController {
    private static final Logger logger = LoggerFactory.getLogger(InfoEnterPriseController.class);

    @Resource
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Resource
    private RdmZhglUtil rdmZhglUtil;
    @Resource
    private InfoEnterpriseService infoEnterpriseService;
    @Resource
    private RdmZhglFileManager rdmZhglFileManager;

    /**
     * 国内企业供应商页面
     * @param request 请求
     * @param response 相应
     * @return ModelAndView
     */
    @RequestMapping("gn/page")
    public ModelAndView gnPage(HttpServletRequest request, HttpServletResponse response){
        return getModelAndView("info/enterprise/infoGngys.jsp");
    }

    /**
     * 国外企业供应商页面
     * @param request 请求
     * @param response 相应
     * @return ModelAndView
     */
    @RequestMapping("gw/page")
    public ModelAndView gwPage(HttpServletRequest request, HttpServletResponse response){
        return getModelAndView("info/enterprise/infoGwgys.jsp");
    }

    /**
     * 企业供应商联系人操作页面
     * @param request 请求
     * @param response 相应
     * @return ModelAndView
     */
    @RequestMapping("contacts/page")
    public ModelAndView contactsPage(HttpServletRequest request, HttpServletResponse response){
        return getModelAndView("info/enterprise/infoEnterpriseContacts.jsp");
    }

    /**
     * 企业供应商样本附件操作页面
     * @param request 请求
     * @param response 相应
     * @return ModelAndView
     */
    @RequestMapping("files/page")
    public ModelAndView filesPage(HttpServletRequest request, HttpServletResponse response){
        ModelAndView modelAndView = getModelAndView("info/enterprise/infoEnterpriseFiles.jsp");
        modelAndView.addObject("belongId", request.getParameter("id"));
        return modelAndView;
    }

    /**
     * 企业供应商样本附件上传操作页面
     * @param request 请求
     * @param response 相应
     * @return ModelAndView
     */
    @RequestMapping("filesUpload/page")
    public ModelAndView filesUploadPage(HttpServletRequest request, HttpServletResponse response){
        return getModelAndView("info/enterprise/infoEnterpriseFileUpload.jsp");
    }

    /**
     * 根据jsp路径返回ModelAndView
     * @param jspPath jsp相对路径
     * @return ModelAndView
     */
    private ModelAndView getModelAndView(String jspPath) {
        ModelAndView mv = new ModelAndView(jspPath);
        String currentUserId = ContextUtil.getCurrentUserId();
        mv.addObject("currentUserId", currentUserId);
        Map<String, Object> params = new HashMap<>();
        params.put("userId", currentUserId);
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        // 返回当前登录人角色信息
        List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params);
//        List<JSONObject> kpiObject= zlghKPIDao.queryKPIList(params);
        JSONArray userRolesJsonArray = XcmgProjectUtil.convertListMap2JsonArrObject(currentUserRoles);
//        mv.addObject("kpiObject",kpiObject);
        mv.addObject("currentUserRoles", userRolesJsonArray);
        // 返回当前用户是否是技术管理部负责人
        boolean isJsglbRespUser = rdmZhglUtil.judgeUserIsJsglbRespUser(currentUserId);
        mv.addObject("isJsglbRespUser", isJsglbRespUser);
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        boolean isZLGHZY = rdmZhglUtil.judgeIsPointRoleUser(currentUserId, "情报专员");
        // 超级管理员
        boolean isAdmin = Objects.equals(currentUserId, "1");
        mv.addObject("isZLGHZY",isZLGHZY || isAdmin);
        return mv;
    }


    /**
     * 国内企业供应商分页查询
     * @param paramsVo 参数对象
     * @param request 请求
     * @return JsonPageResult
     * @author douhongli
     * @date 2021年5月27日15:14:55
     */
    @RequestMapping("gn/list")
    @ResponseBody
    public JsonPageResult<T> gnList(ParamsVo paramsVo, HttpServletRequest request){
        return infoEnterpriseService.selecGnList(paramsVo, request, true);
    }

    /**
     * 新增/修改/删除国内企业供应商
     * @param infoGngysList 参数列表
     * @return JsonResult
     * @author douhongli
     * @date 2021年5月27日15:15:09
     */
    @RequestMapping(value = "gn/batchOptions")
    @ResponseBody
    public JsonResult gnBatchOptions(@RequestBody List<InfoGngys> infoGngysList){
            return infoEnterpriseService.gnBatchOptions(infoGngysList);
    }

    /**
     * 国外企业供应商分页查询
     * @param paramsVo 参数对象
     * @param request 请求
     * @return JsonPageResult
     * @author douhongli
     * @date 2021年5月27日15:14:55
     */
    @RequestMapping("gw/list")
    @ResponseBody
    public JsonPageResult<T> gwList(ParamsVo paramsVo, HttpServletRequest request){
        return infoEnterpriseService.selecGwList(paramsVo, request, true);
    }

    /**
     * 新增/修改/删除国外企业供应商
     * @param infoGwgysList 参数列表
     * @return JsonResult
     * @author douhongli
     * @date 2021年5月27日15:15:09
     */
    @RequestMapping(value = "gw/batchOptions")
    @ResponseBody
    public JsonResult gwBatchOptions(@RequestBody List<InfoGwgys> infoGwgysList){
        return infoEnterpriseService.gwBatchOptions(infoGwgysList);
    }

    /**
     * 企业供应商联系人列表
     *
     * @param paramsVo 参数对象
     * @param request 请求
     * @return JsonPageResult
     * @author douhongli
     * @date 2021年5月27日15:14:55
     */
    @RequestMapping("enterpriseContacts/list")
    @ResponseBody
    public JsonPageResult<T> enterpriseContactsList(ParamsVo paramsVo, HttpServletRequest request){
        return infoEnterpriseService.selectEnterpriseContactsList(paramsVo, request, true);
    }

    /**
     * 新增/修改/删除企业供应商联系人
     * @param infoGyslxrList 企业供应商联系人参数列表
     * @return JsonResult
     * @author douhongli
     * @date 2021年6月1日09:53:40
     */
    @RequestMapping(value = "enterpriseContacts/batchOptions")
    @ResponseBody
    public JsonResult enterpriseContactsOptions(@RequestBody List<InfoGyslxr> infoGyslxrList){
        return infoEnterpriseService.enterpriseContactsBatchOptions(infoGyslxrList);
    }

    /**
     * 企业供应商样本文件列表
     *
     * @param paramsVo 参数对象
     * @param request 请求
     * @return JsonPageResult
     * @author douhongli
     * @date 2021年6月1日09:55:01
     */
    @RequestMapping("enterpriseFile/list")
    @ResponseBody
    public JsonPageResult<T> enterpriseFileList(ParamsVo paramsVo, HttpServletRequest request){
        return infoEnterpriseService.selectEnterpriseFileList(paramsVo, request, true);
    }

    /**
     * 新增/修改/删除企业供应商样本文件
     * @param infoCpybFilesList 企业供应商样本文件参数列表
     * @return JsonResult
     * @author douhongli
     * @date 2021年6月1日09:56:05
     */
    @RequestMapping(value = "enterpriseFile/batchOptions")
    @ResponseBody
    public JsonResult enterpriseFileOptions(@RequestBody List<InfoCpybFiles> infoCpybFilesList){
        return infoEnterpriseService.enterpriseFileBatchOptions(infoCpybFilesList);
    }

    /**
     * 文件上传
     * @param request 请求参数
     * @param response 相应参数
     * @return Map<String, Object>
     * @throws Exception 文件上传异常
     * @author douhongli
     * @since 2021年6月1日16:55:32
     */
    @RequestMapping("file/upload")
    @ResponseBody
    public Map<String, Object> upload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        try {
            // 先保存文件，成功后再写入数据库，前台是一个一个文件的调用
            infoEnterpriseService.saveCommonUploadFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            logger.error("Exception in upload", e);
            modelMap.put("success", "false");
        }
        return modelMap;
    }

    /**
     * 删除某个文件（从文件表和磁盘上都删除）
     *
     * @param request 请求
     * @param response 响应
     * @author douhongli
     * @since 2021年6月1日19:58:44
     */
    @RequestMapping("file/delete")
    public void delete(HttpServletRequest request, HttpServletResponse response) {
        infoEnterpriseService.deleteOneFile(request);
    }

    /**
     * 下载文件
     * @param request 请求
     * @param response 响应
     * @return ResponseEntity
     * @author douhongli
     * @since 2021年6月1日19:57:54
     */
    @RequestMapping("file/download")
    public ResponseEntity<byte[]> fileDownload(HttpServletRequest request, HttpServletResponse response) {
        try {
            String fileId = RequestUtil.getString(request, "id");
            String belongId = RequestUtil.getString(request, "belongId");
            String fileName = RequestUtil.getString(request, "fileName");
            String suffix = "";
            String[] arr = fileName.split("\\.", -1);
            if (arr.length > 1) {
                suffix = arr[arr.length - 1];
            }
            String fileBasePath = WebAppUtil.getProperty("enterpriseFileDir")+ File.separator + belongId;
            String fullFilePath = fileBasePath + File.separator + fileId + "." + suffix;
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
     * pdf 预览
     * @param request 请求
     * @param response 响应
     * @return ResponseEntity
     * @author douhongli
     * @since 2021年6月2日09:29:28
     */
    @RequestMapping("downOrPdfPreview")
    public ResponseEntity<byte[]> downOrPdfPreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String belongId = RequestUtil.getString(request, "belongId");
//        String relativeFilePath = RequestUtil.getString(request, "relativeFilePath");
//        String fileBasePath = SysPropertiesUtil.getGlobalProperty("worldResearch");
        String fileBasePath = WebAppUtil.getProperty("enterpriseFileDir");
        return rdmZhglFileManager.pdfPreviewOrDownLoad(fileName, fileId, belongId, fileBasePath);
    }

    /**
     * 预览office文件
     * @param request 请求
     * @param response 响应
     * @author douhongli
     * @since 2021年6月2日09:59:56
     */
    @RequestMapping("officePreview")
    public void officePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String suffix = CommonFuns.toGetFileSuffix(fileName);
//        String fileBasePath = SysPropertiesUtil.getGlobalProperty("worldResearch");
//        String relativeFilePath = RequestUtil.getString(request, "relativeFilePath");
        String belongId = RequestUtil.getString(request, "belongId");
        String fileBasePath = WebAppUtil.getProperty("enterpriseFileDir");
        if (StringUtils.isBlank(fileName)) {
            logger.error("文档预览失败，文件名为空！");
            return;
        }
        if (StringUtils.isBlank(fileBasePath)) {
            logger.error("文档预览失败，找不到存储路径");
            return;
        }
        if (StringUtils.isBlank(belongId)) {
            belongId = "";
        }
        String originalFilePath = fileBasePath + File.separator  + belongId + File.separator + fileId + "." + suffix;
        String convertPdfDir = WebAppUtil.getProperty("convertPdfDir");
        String convertPdfPath = fileBasePath + File.separator + belongId + File.separator + convertPdfDir + File.separator+ fileId + ".pdf";
        OfficeDocPreview.previewOfficeDoc(originalFilePath, convertPdfPath, response);
    }

    /**
     * 图片预览
     * @param request 请求
     * @param response 响应
     * @author douhongli
     * @since 2021年6月2日10:02:48
     */
    @RequestMapping("imagePreview")
    @ResponseBody
    public void imagePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String suffix = CommonFuns.toGetFileSuffix(fileName);
//        String fileBasePath = SysPropertiesUtil.getGlobalProperty("worldResearch");
//        String relativeFilePath = RequestUtil.getString(request, "relativeFilePath");
        String belongId = RequestUtil.getString(request, "belongId");
        String fileBasePath = WebAppUtil.getProperty("enterpriseFileDir");
        if (StringUtils.isBlank(fileName)) {
            logger.error("图片预览失败，文件名为空！");
            return;
        }
        if (StringUtils.isBlank(fileBasePath)) {
            logger.error("图片预览失败，找不到存储路径");
            return;
        }
        String originalFilePath = fileBasePath + File.separator  + belongId + File.separator + fileId + "." + suffix;
        OfficeDocPreview.imagePreview(originalFilePath, response);
    }
}
