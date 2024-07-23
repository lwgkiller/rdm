package com.redxun.rdmCommon.core.manager;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.redxun.saweb.util.WebAppUtil;

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
 * @since 2020/7/10 16:43
 */
@Service
public class DownLoadManager {
    private Logger logger = LoggerFactory.getLogger(DownLoadManager.class);

    public ResponseEntity<byte[]> downloadExplorer() {
        try {
            String fileName = "ChromeSetup_64.exe";
            // 创建文件实例
            String rdmDownloadDir = WebAppUtil.getProperty("rdmDownloadDir");
            File dirFile = new File(rdmDownloadDir);
            // 目录不存在则创建
            if (!dirFile.exists()) {
                dirFile.mkdirs();
            }
            String fileFullPath = rdmDownloadDir + File.separator + fileName;
            File file = new File(fileFullPath);
            if (!file.exists()) {
                logger.warn("File " + fileFullPath + " not exist!");
                return null;
            }
            String finalDownloadFileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");

            // 设置httpHeaders,使浏览器响应下载
            HttpHeaders headers = new HttpHeaders();
            // 告诉浏览器执行下载的操作，“attachment”告诉了浏览器进行下载,下载的文件 文件名为 finalDownloadFileName
            headers.setContentDispositionFormData("attachment", finalDownloadFileName);
            // 设置响应方式为二进制，以二进制流传输
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file), headers, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Exception in downloadExplorer", e);
            return null;
        }
    }
}
