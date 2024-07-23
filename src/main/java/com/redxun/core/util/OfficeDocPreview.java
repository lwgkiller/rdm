package com.redxun.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 应用模块名称
 * <p>
 * 代码描述
 * <p>
 * 将指定路径的原office文件转为pdf流 Copyright: Copyright (C) 2019 XXX, Inc. All rights reserved.
 * <p>
 * Company: 徐工挖掘机械有限公司
 * <p>
 *
 * @author liangchuanjiang
 * @since 2019/12/21 15:15
 */
public class OfficeDocPreview {
    private static final Logger logger = LoggerFactory.getLogger(OfficeDocPreview.class);

    public static void previewOfficeDoc(String originalDocPath, String convertPdfPath, HttpServletResponse response) {
        if (StringUtils.isBlank(originalDocPath)) {
            logger.error("源文件路径为空！");
            return;
        }
        /*if (StringUtils.isBlank(convertPdfPath)) {
            logger.error("目标文件路径为空！");
            return;
        }*/

        File originalFile = new File(originalDocPath);
        if (!originalFile.exists()) {
            logger.error("源文件不存在！");
            return;
        }
        String fileExt = findFileExt(originalDocPath);
        if (StringUtils.isBlank(fileExt) || !isOfficeDoc(fileExt)) {
            logger.error("源文件不是可预览的office文件！");
            return;
        }
        if (originalFile.length() / 1024 / 1024 > 50) {
            logger.error("源文件超过50MB，不允许预览！");
            return;
        }
        AsposeUtil.convertOffice2Pdf(originalDocPath, convertPdfPath, response);
        /*boolean enableOpenOffice = OpenOfficeUtil.isOpenOfficeEnabled();
        if (!enableOpenOffice) {
            logger.error("openOffice转换功能已关闭！");
            return;
        }
        File convertPdfFile = new File(convertPdfPath);
        if (convertPdfFile == null || !convertPdfFile.exists()) {
            JSONObject convertResult = OpenOfficeUtil.coverFromOffice2Pdf(originalDocPath, convertPdfPath);
            if (convertResult == null || !convertResult.getBoolean("success")) {
                logger.error("文件" + originalDocPath + "转换失败：" + convertResult.getString("reason"));
                return;
            }
        }
        if (convertPdfFile != null && convertPdfFile.exists()) {
            byte[] data = null;
            try {
                FileInputStream input = new FileInputStream(convertPdfFile);
                data = new byte[input.available()];
                input.read(data);
                response.getOutputStream().write(data);
                input.close();
            } catch (Exception e) {
                logger.error("文件" + originalDocPath + "预览处理异常：" + e.getMessage());
            }
        }*/
    }

    public static void imagePreview(String originalDocPath, HttpServletResponse response) {
        if (StringUtils.isBlank(originalDocPath)) {
            logger.error("源文件路径为空！");
            return;
        }

        File originalFile = new File(originalDocPath);
        if (!originalFile.exists()) {
            logger.error("源文件不存在！");
            return;
        }
        String fileExt = findFileExt(originalDocPath);
        if (StringUtils.isBlank(fileExt) || !isImage(fileExt)) {
            logger.error("源文件不是可预览的图片文件！");
            return;
        }
        byte[] data = null;
        try {
            FileInputStream input = new FileInputStream(originalFile);
            data = new byte[input.available()];
            input.read(data);
            response.getOutputStream().write(data);
            input.close();
        } catch (Exception e) {
            logger.error("文件" + originalDocPath + "预览处理异常：" + e.getMessage());
        }
    }

    // 查找文件的扩展名
    public static String findFileExt(String filePath) {
        if (StringUtils.isBlank(filePath)) {
            return "";
        }
        int lastDotIndex = filePath.lastIndexOf(".");
        if (lastDotIndex == -1) {
            return "";
        }
        return filePath.substring(lastDotIndex + 1).toLowerCase();
    }

    public static boolean isOfficeDoc(String ext) {
        List<String> officeSuffix = Arrays.asList("doc", "docx", "ppt", "txt", "xlsx", "xls", "pptx");
        if (officeSuffix.contains(ext)) {
            return true;
        }
        return false;
    }

    public static boolean isImage(String ext) {
        List<String> imageSuffix = Arrays.asList("jpg", "jpeg", "jif", "bmp", "png", "tif", "gif", "svg");
        if (imageSuffix.contains(ext)) {
            return true;
        }
        return false;
    }

    public static String findFileNameWithoutExt(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return "";
        }
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex == -1) {
            return fileName;
        }
        return fileName.substring(0, lastDotIndex);
    }

    // 得到预览时临时生成的pdf文件的名字(如my.doc--->my_doc.pdf)
    public static String generateConvertPdfFileName(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            logger.error("fileName is blank");
            return "tmp.pdf";
        }
        String fileNameWithOutExt = findFileNameWithoutExt(fileName);
        String fileExt = findFileExt(fileName);
        return fileNameWithOutExt + "_" + fileExt + ".pdf";
    }
}
