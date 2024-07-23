package com.redxun.presaleDocuments.core.controller;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.StringUtil;
import com.redxun.presaleDocuments.core.service.PresaleDocumentsService;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmZhgl.core.service.MonthWorkService;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.sys.core.util.SysPropertiesUtil;
import com.redxun.sys.org.entity.OsDimension;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipOutputStream;

@Controller
@RequestMapping("/presaleDocuments/core/masterData/")
public class PresaleDocumentsController {
    private static final Logger logger = LoggerFactory.getLogger(PresaleDocumentsController.class);
    @Autowired
    private PresaleDocumentsService presaleDocumentsService;
    @Autowired
    private CommonInfoManager commonInfoManager;
    @Autowired
    private MonthWorkService monthWorkService;
    @Autowired
    private SysDicManager sysDicManager;

    //..
    @RequestMapping("dataListPage")
    public ModelAndView dataListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "presaleDocuments/core/presaleDocumentsList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        boolean isPresaleDocumentsAdmin = commonInfoManager.judgeUserIsPointGroup("PresaleDocumentsAdmin", OsDimension.DIM_ROLE_ID,
                "0", ContextUtil.getCurrentUserId(), "1");
        mv.addObject("isPresaleDocumentsAdmin", isPresaleDocumentsAdmin);
        return mv;
    }

    //..
    @RequestMapping("editPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "presaleDocuments/core/presaleDocumentsEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String businessId = RequestUtil.getString(request, "id");
        String action = RequestUtil.getString(request, "action");
        mv.addObject("businessId", businessId).addObject("action", action);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        boolean isPresaleDocumentsAdmin = commonInfoManager.judgeUserIsPointGroup("PresaleDocumentsAdmin", OsDimension.DIM_ROLE_ID,
                "0", ContextUtil.getCurrentUserId(), "1");
        mv.addObject("isPresaleDocumentsAdmin", isPresaleDocumentsAdmin);
        return mv;
    }

    //..
    @RequestMapping("dataListQuery")
    @ResponseBody
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) {
        return presaleDocumentsService.dataListQuery(request, response);
    }

    //..
    @RequestMapping("getDataById")
    @ResponseBody
    public JSONObject getDataById(HttpServletRequest request, HttpServletResponse response) {
        String id = RequestUtil.getString(request, "businessId");
        return presaleDocumentsService.getDataById(id);
    }

    //..
    @RequestMapping(value = "saveBusiness", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult saveBusiness(HttpServletRequest request, HttpServletResponse response,
                                   @RequestBody String DataStr) {
        JSONObject jsonObject = JSONObject.parseObject(DataStr);
        if (jsonObject == null || jsonObject.isEmpty()) {
            return new JsonResult(false, "表单内容为空，操作失败！");
        }
        try {
            return presaleDocumentsService.saveBusiness(jsonObject);
        } catch (Exception e) {
            logger.error("Exception in saveBusiness", e);
            return new JsonResult(false, e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
        }
    }

    //..
    @RequestMapping("deleteBusiness")
    @ResponseBody
    public JsonResult deleteBusiness(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return presaleDocumentsService.deleteBusiness(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteBusiness", e);
            return new JsonResult(false, e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
        }
    }

    //..
    @RequestMapping("releaseBusiness")
    @ResponseBody
    public JsonResult releaseBusiness(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return presaleDocumentsService.doReleaseBusiness(ids, null);
        } catch (Exception e) {
            logger.error("Exception in releaseBusiness", e);
            return new JsonResult(false, e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
        }
    }

    //..
    @RequestMapping("exportBusiness")
    public void exportBusiness(HttpServletRequest request, HttpServletResponse response) {
        presaleDocumentsService.exportBusiness(request, response);
    }

    //..
    @RequestMapping("zipFileDownload")
    public void zipFileDownload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            //多文件打包下载成zip 以Form表单提交，各字段间以逗号分隔
            String fileNameStr = RequestUtil.getString(request, "fileName");
            String fileIdStr = RequestUtil.getString(request, "fileId");
            String formIdStr = RequestUtil.getString(request, "formId");
            if (StringUtils.isEmpty(formIdStr) || StringUtils.isEmpty(fileIdStr) || StringUtils.isEmpty(fileNameStr)) {
                return;
            }
            String[] ids = formIdStr.split(",");
            String[] fileIds = fileIdStr.split(",");
            String[] fileNames = fileNameStr.split(",");
            Set<String> fileNamesSet = Arrays.stream(fileNames).collect(Collectors.toSet());
            if (fileNamesSet.size() != fileNames.length) {
                throw new RuntimeException("无法打包下载，下载的文件存在重复的文件名!");
            }
            List<JSONObject> filePathList = new ArrayList<>();
            JSONObject pathObj = new JSONObject();
            String fileBasePath = sysDicManager.getBySysTreeKeyAndDicKey("PresaleDocumentUploadPosition", "主数据文件").getValue();
            if (StringUtils.isBlank(fileBasePath)) {
                logger.error("操作失败，找不到存储根路径");
                return;
            }
            for (int i = 0; i < ids.length; i++) {
                // 拼接路径
                String fileId = fileIds[i];
                String fileName = fileNames[i];
                String id = ids[i];
                String suffix = CommonFuns.toGetFileSuffix(fileName);
                String relativeFilePath = "";

                if (StringUtils.isNotBlank(id)) {
                    relativeFilePath = File.separator + id;
                }
                String realFileName = fileId + "." + suffix;
                String fullFilePath = fileBasePath + relativeFilePath + File.separator + realFileName;
                pathObj = new JSONObject();
                pathObj.put("path", fullFilePath);
                pathObj.put("fileName", fileName);
                filePathList.add(pathObj);
            }
            if (filePathList.size() != 0) {
                // 创建临时路径，存放压缩文件
                String downFile = XcmgProjectUtil.getNowUTCDateStr("yyyyMMddHHmmss") + ".zip";
                String zipFilePath = fileBasePath + File.separator + downFile;
                ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFilePath));
                for (JSONObject path : filePathList) {
                    monthWorkService.fileToZip(path.getString("path"), zipOutputStream, path.getString("fileName"));
                }
                // 压缩完成后，关闭压缩流
                zipOutputStream.close();

                String fileName = new String(downFile.getBytes("UTF-8"), "ISO8859-1");
                response.setHeader("Content-Disposition", "attachment;filename=" + fileName);

                ServletOutputStream outputStream = response.getOutputStream();
                FileInputStream inputStream = new FileInputStream(zipFilePath);

                IOUtils.copy(inputStream, outputStream);
                inputStream.close();
                outputStream.close(); // 关闭流

                File fileTempZip = new File(zipFilePath);
                fileTempZip.delete();
            }
        } catch (Exception e) {
            throw e;
        }
    }
}
