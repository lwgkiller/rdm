
package com.redxun.xcmgJsjl.core.controller;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.OfficeDocPreview;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.xcmgJsjl.core.manager.XcmgJsjlFileManager;
import com.redxun.xcmgProjectManager.core.util.ConstantUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * project_baseInfo控制器
 *
 * @author x
 */
@Controller
@RequestMapping("/jsjl/core/file/")
public class XcmgJsjlFileController {
    private Logger logger = LoggerFactory.getLogger(XcmgJsjlFileController.class);

    @Resource
    private XcmgJsjlFileManager xcmgJsjlFileManager;

    @RequestMapping("openUploadWindow")
    public ModelAndView openUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "xcmgJsjl/core/xcmgJsjlFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUser", ContextUtil.getCurrentUser());
        return mv;
    }

    @RequestMapping(value = "upload")
    @ResponseBody
    public Map<String, Object> upload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        try {
            // 先保存文件到磁盘，成功后再写入数据库，前台是一个一个文件的调用
            xcmgJsjlFileManager.saveJsjlUploadFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            logger.error("Exception in upload", e);
            modelMap.put("success", "false");
        }
        return modelMap;
    }

    @RequestMapping("dataList")
    @ResponseBody
    public List<JSONObject> dataList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jsjlId = RequestUtil.getString(request, "jsjlId");
        if (StringUtils.isBlank(jsjlId)) {
            logger.error("jsjlId is blank");
            return null;
        }
        Map<String, Object> params = new HashMap<>();
        List<String> jsjlIds = new ArrayList<>();
        jsjlIds.add(jsjlId);
        params.put("jsjlIds", jsjlIds);
        List<JSONObject> fileInfos = xcmgJsjlFileManager.queryJsjlFileList(params);
        // 格式化时间
        if (fileInfos != null && !fileInfos.isEmpty()) {
            for (JSONObject oneFile : fileInfos) {
                if (StringUtils.isNotBlank(oneFile.getString("CREATE_TIME_"))) {
                    oneFile.put("CREATE_TIME_",
                        DateUtil.formatDate(oneFile.getDate("CREATE_TIME_"), "yyyy-MM-dd HH:mm:ss"));
                }
            }
        }
        return fileInfos;
    }

    // 删除某个文件（从文件表和磁盘上都删除）
    @RequestMapping("delete")
    public void delete(HttpServletRequest request, HttpServletResponse response, @RequestBody String postBody) {
        JSONObject postBodyObj = JSONObject.parseObject(postBody);
        String fileName = postBodyObj.getString("fileName");
        String id = postBodyObj.getString("id");
        String jsjlId = postBodyObj.getString("jsjlId");
        xcmgJsjlFileManager.deleteOneFile(id, fileName, jsjlId);
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
            String jsjlId = RequestUtil.getString(request, "jsjlId");
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
            String fileBasePath = ConstantUtil.PREVIEW.equalsIgnoreCase(action)
                ? WebAppUtil.getProperty("jsjlFilePathBase_preview") : WebAppUtil.getProperty("jsjlFilePathBase");
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

    @RequestMapping("officePreview")
    @ResponseBody
    public void officePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        if (StringUtils.isBlank(fileName)) {
            logger.error("操作失败，文件名为空！");
            return;
        }
        String fileId = RequestUtil.getString(request, "fileId");
        if (StringUtils.isBlank(fileId)) {
            logger.error("操作失败，文件Id为空！");
            return;
        }
        String jsjlId = RequestUtil.getString(request, "jsjlId");
        if (StringUtils.isBlank(jsjlId)) {
            logger.error("操作失败，技术交流Id为空！");
            return;
        }

        String fileBasePath = WebAppUtil.getProperty("jsjlFilePathBase_preview");
        if (StringUtils.isBlank(fileBasePath)) {
            logger.error("操作失败，找不到存储路径");
            return;
        }
        String suffix = CommonFuns.toGetFileSuffix(fileName);
        String originalFilePath = fileBasePath + File.separator + jsjlId + File.separator + fileId + "." + suffix;
        String convertPdfDir = WebAppUtil.getProperty("convertPdfDir");
        String convertPdfPath = fileBasePath + File.separator + jsjlId + File.separator + convertPdfDir + File.separator+ fileId + ".pdf";;
        OfficeDocPreview.previewOfficeDoc(originalFilePath, convertPdfPath, response);
    }

    @RequestMapping("imagePreview")
    @ResponseBody
    public void imagePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        if (StringUtils.isBlank(fileName)) {
            logger.error("操作失败，文件名为空！");
            return;
        }
        String fileId = RequestUtil.getString(request, "fileId");
        if (StringUtils.isBlank(fileId)) {
            logger.error("操作失败，文件Id为空！");
            return;
        }
        String jsjlId = RequestUtil.getString(request, "jsjlId");
        if (StringUtils.isBlank(jsjlId)) {
            logger.error("操作失败，技术交流Id为空！");
            return;
        }

        String fileBasePath = WebAppUtil.getProperty("jsjlFilePathBase_preview");
        if (StringUtils.isBlank(fileBasePath)) {
            logger.error("操作失败，找不到存储路径");
            return;
        }
        String suffix = CommonFuns.toGetFileSuffix(fileName);
        String originalFilePath = fileBasePath + File.separator + jsjlId + File.separator + fileId + "." + suffix;
        OfficeDocPreview.imagePreview(originalFilePath, response);
    }
}
