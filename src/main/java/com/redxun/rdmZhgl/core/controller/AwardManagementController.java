package com.redxun.rdmZhgl.core.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.portrait.core.dao.PortraitAsyncRewardDao;
import com.redxun.rdmZhgl.core.dao.AwardManagementDao;
import com.redxun.rdmZhgl.core.service.AwardManagementService;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
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

import static com.redxun.rdmZhgl.core.service.CityProgressAwardService.delFiles;

@RestController
@RequestMapping("/zhgl/core/awardManagement/")
public class AwardManagementController {
    private Logger logger = LogManager.getLogger(AwardManagementController.class);
    @Resource
    private AwardManagementService awardManagementService;
    @Autowired
    private AwardManagementDao awardManagementDao;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private PortraitAsyncRewardDao portraitAsyncRewardDao;
    /*
     * 跳转管理奖列表页面
     * */
    @RequestMapping("listPage")
    public ModelAndView listPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/awardManagement.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        Map<String, Object> params = new HashMap<>();
        params.put("userId", ContextUtil.getCurrentUserId());
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        // 返回当前登录人角色信息
        List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params);
        JSONArray userRolesJsonArray = XcmgProjectUtil.convertListMap2JsonArrObject(currentUserRoles);
        mv.addObject("currentUserRoles", userRolesJsonArray);
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        return mv;
    }
    @RequestMapping("getAmgList")
    @ResponseBody
    public JsonPageResult<?> getAmgList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return awardManagementService.getAmgList(request, response, true);
    }
    @RequestMapping("edit")
    public ModelAndView edit(HttpServletRequest request, HttpServletResponse response) {

        String jspPath = "rdmZhgl/core/awardMgeEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String awardId = RequestUtil.getString(request, "awardId");
        mv.addObject("awardId", awardId);
        String action = RequestUtil.getString(request, "action");
        JSONObject amgObj = new JSONObject();
        //查询信息
        if (StringUtils.isNotBlank(awardId)) {
            Map<String, Object> amgInfo = awardManagementService.queryAmgById(awardId);
            amgObj = XcmgProjectUtil.convertMap2JsonObject(amgInfo);
        }

        mv.addObject("amgObj", amgObj);
        mv.addObject("action", action);
        mv.addObject("currentUser", ContextUtil.getCurrentUser());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));

        return mv;
    }
    @RequestMapping("saveAmg")
    @ResponseBody
    public JSONObject saveAmg(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        awardManagementService.saveAmg(result, request);
        return result;
    }
    @RequestMapping("deleteAmg")
    @ResponseBody
    public JsonResult removeAmg(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {

            String uIdStr = RequestUtil.getString(request, "ids");
            String fileIdStr = RequestUtil.getString(request, "fileIds");
            List<String> ids = Arrays.asList(uIdStr.split(","));
            List<String> fileIds = Arrays.asList(fileIdStr.split(","));

            Map<String, Object> param = new HashMap<>();
            param.put("ids",ids);
            param.put("fileIds",fileIds);

            if (!ids.isEmpty()){
                awardManagementDao.deleteAmg(param);
                portraitAsyncRewardDao.delRewardInfoByFromId(param);
            }
            if(!fileIds.isEmpty()){
                awardManagementDao.deleteAmgFileIds(param);
            }


            //文件删除
            String standardFilePathBase = WebAppUtil.getProperty("amgFilePathBase");
            if (StringUtils.isBlank(standardFilePathBase)) {
                logger.error("can't find amgFilePathBase");
            }
            if(!ids.isEmpty()){
                for (String id : ids) {
                    String filePath = standardFilePathBase + File.separator + id;
                    File pathFile = new File(filePath);
                    delFiles(pathFile);
                }
            }


        } catch (Exception e) {
            logger.error("Exception in removeLbj", e);
            return new JsonResult(false, e.getMessage());
        }

        return new JsonResult(true,"操作成功");
    }
    /**
     * 批量导入
     * */
    @RequestMapping("importExcel")
    @ResponseBody
    public JSONObject importExcel(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        awardManagementService.importMaterial(result, request);
        return result;
    }
    /**
     * 模板下载
     * */
    @RequestMapping("/importTemplateDownload")
    public ResponseEntity<byte[]> importTemplateDownload(HttpServletRequest request, HttpServletResponse response) {
        return awardManagementService.importTemplateDownload();
    }
    @RequestMapping("amgPdfPreview")
    public ResponseEntity<byte[]> amgPdfPreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("amgFilePathBase");
        return rdmZhglFileManager.pdfPreviewOrDownLoad(fileName, fileId, formId, fileBasePath);
    }
    @RequestMapping("amgOfficePreview")
    @ResponseBody
    public void amgOfficePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String formId = RequestUtil.getString(request, "formId");
        String id = RequestUtil.getString(request, "id");
        String fileId = RequestUtil.getString(request, "fileId");
        String fileBasePath = WebAppUtil.getProperty("amgFilePathBase");
        rdmZhglFileManager.officeFilePreview(fileName, fileId, formId, fileBasePath, response);
    }
    @RequestMapping("amgImagePreview")
    @ResponseBody
    public void amgImagePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("amgFilePathBase");
        rdmZhglFileManager.imageFilePreview(fileName, fileId, formId, fileBasePath, response);
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
            String jsjlId = RequestUtil.getString(request, "id");

            if (jsjlId == null || "".equals(jsjlId)){
                jsjlId = awardManagementDao.selectAnpByFjId(fileId);
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
            String fileBasePath = WebAppUtil.getProperty("amgFilePathBase");
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
    @RequestMapping("exportAmgList")
    public void exportExcelAmg(HttpServletRequest request, HttpServletResponse response) {
        awardManagementService.exportAmgList(request, response);
    }
}
