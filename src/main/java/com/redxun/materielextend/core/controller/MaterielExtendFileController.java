package com.redxun.materielextend.core.controller;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.saweb.util.WebAppUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.redxun.materielextend.core.service.MaterielExtendFileService;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;

/**
 * 应用模块名称
 * <p>
 * 代码描述
 * <p>
 * Copyright: Copyright (C) 2020 XXX, Inc. All rights reserved.
 * <p>
 * Company: 徐工挖掘机械有限公司
 * <p>
 *
 * @author liangchuanjiang
 * @since 2020/6/2 17:40
 */
@Controller
@RequestMapping("/materielExtend/file")
public class MaterielExtendFileController {
    private static final Logger logger = LoggerFactory.getLogger(MaterielExtendFileController.class);
    @Autowired
    private MaterielExtendFileService materielExtendFileService;

    @RequestMapping("/listPage")
    public ModelAndView listPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "materielExtend/materielExtendFileList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        return mv;
    }

    @RequestMapping("/listData")
    @ResponseBody
    public List<JSONObject> listData(HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<JSONObject> fileInfos = materielExtendFileService.listData();
        return fileInfos;
    }

    @RequestMapping("/uploadPage")
    public ModelAndView uploadPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "materielExtend/materielExtendFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        return mv;
    }

    @RequestMapping("/upload")
    @ResponseBody
    public Map<String, Object> upload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        try {
            // 先保存文件，成功后再写入数据库，前台是一个一个文件的调用
            materielExtendFileService.saveCommonUploadFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            logger.error("Exception in upload", e);
            modelMap.put("success", "false");
        }
        return modelMap;
    }

    // 删除某个文件（从文件表和磁盘上都删除）
    @RequestMapping("/delete")
    public void delete(HttpServletRequest request, HttpServletResponse response) {
        String id = RequestUtil.getString(request, "id");
        String fileName = RequestUtil.getString(request, "fileName");
        materielExtendFileService.deleteOneFile(id, fileName);
    }

    // 文档的下载
    @RequestMapping("/download")
    public ResponseEntity<byte[]> fileDownload(HttpServletRequest request, HttpServletResponse response) {
        try {
            String fileId = RequestUtil.getString(request, "id");
            String fileName = RequestUtil.getString(request, "fileName");
            String suffix = "";
            String[] arr = fileName.split("\\.", -1);
            if (arr.length > 1) {
                suffix = arr[arr.length - 1];
            }
            String fileBasePath = WebAppUtil.getProperty("materielExtendFileDir");
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
}
