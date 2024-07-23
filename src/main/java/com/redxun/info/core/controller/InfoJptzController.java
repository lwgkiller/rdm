package com.redxun.info.core.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.OfficeDocPreview;
import com.redxun.info.core.dao.QbgzDao;
import com.redxun.info.core.manager.InfoJptzService;
import com.redxun.info.core.model.InfoJptz;
import com.redxun.info.core.model.InfoJptzFiles;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.strategicPlanning.core.domain.vo.ParamsVo;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 竞品图纸的控制层
 * 
 * @author douhongli
 * @date 2021年5月31日09:58:18
 */
@RestController
@RequestMapping("/info/core/jptz/")
public class InfoJptzController {
    private static final Logger logger = LoggerFactory.getLogger(InfoJptzController.class);

    @Resource
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Resource
    private RdmZhglUtil rdmZhglUtil;
    @Resource
    private InfoJptzService infoJptzService;
    @Resource
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private QbgzDao qbgzDao;

    /**
     * 竞品图纸页面
     * 
     * @param request
     *            请求
     * @param response
     *            相应
     * @return ModelAndView
     */
    @RequestMapping("page")
    public ModelAndView gnPage(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = getModelAndView("/info/jptz/infoJptz.jsp");
        Map<String, Object> param2 = new HashMap<>();
        param2.put("currentUserId", ContextUtil.getCurrentUserId());
        boolean isLDR = false;
        if (ContextUtil.getCurrentUserId().equals("1")) {
            mv.addObject("isLDR", isLDR);
        } else {
            List<JSONObject> isLDCList = qbgzDao.isLD(param2);
            for(JSONObject isLDC:isLDCList) {
                if (isLDC != null && !"1".equals(isLDC.getString("SN_"))) {
                    isLDR = true;
                }
            }
            mv.addObject("isLDR", isLDR);
        }
        return mv;
    }

    /**
     * 竞品图纸样本附件操作页面
     * 
     * @param request
     *            请求
     * @param response
     *            相应
     * @return ModelAndView
     */
    @RequestMapping("files/page")
    public ModelAndView filesPage(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = getModelAndView("info/jptz/infoJptzFiles.jsp");
        modelAndView.addObject("belongId", request.getParameter("id"));
        return modelAndView;
    }

    /**
     * 竞品图纸样本附件上传操作页面
     * 
     * @param request
     *            请求
     * @param response
     *            相应
     * @return ModelAndView
     */
    @RequestMapping("filesUpload/page")
    public ModelAndView filesUploadPage(HttpServletRequest request, HttpServletResponse response) {
        return getModelAndView("info/jptz/infoJptzFilesUpload.jsp");
    }

    /**
     * 根据jsp路径返回ModelAndView
     * 
     * @param jspPath
     *            jsp相对路径
     * @return ModelAndView
     */
    private ModelAndView getModelAndView(String jspPath) {
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        Map<String, Object> params = new HashMap<>();
        params.put("userId", ContextUtil.getCurrentUserId());
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        // 返回当前登录人角色信息
        List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params);
        // List<JSONObject> kpiObject= zlghKPIDao.queryKPIList(params);
        JSONArray userRolesJsonArray = XcmgProjectUtil.convertListMap2JsonArrObject(currentUserRoles);
        // mv.addObject("kpiObject",kpiObject);
        mv.addObject("currentUserRoles", userRolesJsonArray);
        // 返回当前用户是否是技术管理部负责人
        boolean isJsglbRespUser = rdmZhglUtil.judgeUserIsJsglbRespUser(ContextUtil.getCurrentUserId());
        mv.addObject("isJsglbRespUser", isJsglbRespUser);
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        // 是否是战略规划专员
        boolean isZLGHZY = rdmZhglUtil.judgeIsPointRoleUser(ContextUtil.getCurrentUserId(), "情报专员");
        mv.addObject("isZLGHZY", isZLGHZY);
        return mv;
    }

    /**
     * 竞品图纸分页查询
     * 
     * @param paramsVo
     *            参数对象
     * @param request
     *            请求
     * @return JsonPageResult
     * @author douhongli
     * @date 2021年5月27日15:14:55
     */
    @RequestMapping("list")
    @ResponseBody
    public JsonPageResult<T> jptzList(ParamsVo paramsVo, HttpServletRequest request) {
        return infoJptzService.selecJptzList(paramsVo, request, true);
    }

    /**
     * 新增/修改/删除竞品图纸
     * 
     * @param infoJptzList
     *            参数列表
     * @return JsonResult
     * @author douhongli
     * @date 2021年5月27日15:15:09
     */
    @RequestMapping(value = "batchOptions")
    @ResponseBody
    public JsonResult jptzBatchOptions(@RequestBody List<InfoJptz> infoJptzList) {
        return infoJptzService.jptzBatchOptions(infoJptzList);
    }

    /**
     * 竞品图纸文件列表
     *
     * @param paramsVo
     *            参数对象
     * @param request
     *            请求
     * @return JsonPageResult
     * @author douhongli
     * @date 2021年6月1日09:55:01
     */
    @RequestMapping("jptzFile/list")
    @ResponseBody
    public JsonPageResult<T> jptzFileList(ParamsVo paramsVo, HttpServletRequest request) {
        return infoJptzService.selectJptzFileList(paramsVo, request, true);
    }

    /**
     * 新增/修改/删除竞品图纸文件
     * 
     * @param infoJptzFilesList
     *            竞品图纸文件参数列表
     * @return JsonResult
     * @author douhongli
     * @date 2021年6月1日09:56:05
     */
    @RequestMapping(value = "jptzFile/batchOptions")
    @ResponseBody
    public JsonResult jptzFileOptions(@RequestBody List<InfoJptzFiles> infoJptzFilesList) {
        return infoJptzService.jptzFileBatchOptions(infoJptzFilesList);
    }

    /**
     * 文件上传
     * 
     * @param request
     *            请求参数
     * @param response
     *            相应参数
     * @return Map<String, Object>
     * @throws Exception
     *             文件上传异常
     * @author douhongli
     * @since 2021年6月1日16:55:32
     */
    @RequestMapping("file/upload")
    @ResponseBody
    public Map<String, Object> upload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        try {
            // 先保存文件，成功后再写入数据库，前台是一个一个文件的调用
            infoJptzService.saveCommonUploadFiles(request);
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
     * @param request
     *            请求
     * @param response
     *            响应
     * @author douhongli
     * @since 2021年6月1日19:58:44
     */
    @RequestMapping("file/delete")
    public void delete(HttpServletRequest request, HttpServletResponse response) {
        infoJptzService.deleteOneFile(request);
    }

    /**
     * 下载文件
     * 
     * @param request
     *            请求
     * @param response
     *            响应
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
            String fileBasePath = WebAppUtil.getProperty("jptzFileDir") + File.separator + belongId;
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
     * 
     * @param request
     *            请求
     * @param response
     *            响应
     * @return ResponseEntity
     * @author douhongli
     * @since 2021年6月2日09:29:28
     */
    @RequestMapping("downOrPdfPreview")
    public ResponseEntity<byte[]> downOrPdfPreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String belongId = RequestUtil.getString(request, "belongId");
        // String relativeFilePath = RequestUtil.getString(request, "relativeFilePath");
        // String fileBasePath = SysPropertiesUtil.getGlobalProperty("worldResearch");
        String fileBasePath = WebAppUtil.getProperty("jptzFileDir");
        return rdmZhglFileManager.pdfPreviewOrDownLoad(fileName, fileId, belongId, fileBasePath);
    }

    /**
     * 预览office文件
     * 
     * @param request
     *            请求
     * @param response
     *            响应
     * @author douhongli
     * @since 2021年6月2日09:59:56
     */
    @RequestMapping("officePreview")
    public void officePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String suffix = CommonFuns.toGetFileSuffix(fileName);
        // String fileBasePath = SysPropertiesUtil.getGlobalProperty("worldResearch");
        // String relativeFilePath = RequestUtil.getString(request, "relativeFilePath");
        String belongId = RequestUtil.getString(request, "belongId");
        String fileBasePath = WebAppUtil.getProperty("jptzFileDir");
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
        String originalFilePath = fileBasePath + File.separator + belongId + File.separator + fileId + "." + suffix;
        String convertPdfDir = WebAppUtil.getProperty("convertPdfDir");
        String convertPdfPath = fileBasePath + File.separator + belongId + File.separator + convertPdfDir
            + File.separator + fileId + ".pdf";
        OfficeDocPreview.previewOfficeDoc(originalFilePath, convertPdfPath, response);
    }

    /**
     * 图片预览
     * 
     * @param request
     *            请求
     * @param response
     *            响应
     * @author douhongli
     * @since 2021年6月2日10:02:48
     */
    @RequestMapping("imagePreview")
    @ResponseBody
    public void imagePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String suffix = CommonFuns.toGetFileSuffix(fileName);
        // String fileBasePath = SysPropertiesUtil.getGlobalProperty("worldResearch");
        // String relativeFilePath = RequestUtil.getString(request, "relativeFilePath");
        String belongId = RequestUtil.getString(request, "belongId");
        String fileBasePath = WebAppUtil.getProperty("jptzFileDir");
        if (StringUtils.isBlank(fileName)) {
            logger.error("图片预览失败，文件名为空！");
            return;
        }
        if (StringUtils.isBlank(fileBasePath)) {
            logger.error("图片预览失败，找不到存储路径");
            return;
        }
        String originalFilePath = fileBasePath + File.separator + belongId + File.separator + fileId + "." + suffix;
        OfficeDocPreview.imagePreview(originalFilePath, response);
    }
}
