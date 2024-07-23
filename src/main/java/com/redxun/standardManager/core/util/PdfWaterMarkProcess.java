package com.redxun.standardManager.core.util;

import com.lowagie.text.Element;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.*;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;

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
 * @since 2020/3/6 8:36
 */
public class PdfWaterMarkProcess {
    private static final Logger logger = LoggerFactory.getLogger(PdfWaterMarkProcess.class);
    private static int topMargin = 250;
    private static int rowNum = 3;
    private static String waterMarkName = "徐工挖机 - ";
    // 处理水印的临时文件夹，下载后将文件删除

    public static void waterMarkPdfGenerate(String inputFilePath, String outputFilePath) {
        try {
            logger.info("start waterMark");
            PdfReader reader = new PdfReader(inputFilePath);
            Field f = PdfReader.class.getDeclaredField("ownerPasswordUsed");
            f.setAccessible(true);
            f.set(reader, Boolean.TRUE);
            PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(outputFilePath));
            BaseFont base = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.EMBEDDED);
            Rectangle pageRect = null;
            PdfGState gs = new PdfGState();
            gs.setFillOpacity(0.2f);
            gs.setStrokeOpacity(0.2f);
            int total = reader.getNumberOfPages() + 1;

            PdfContentByte under;
            for (int i = 1; i < total; i++) {
                pageRect = reader.getPageSizeWithRotation(i);
                under = stamper.getOverContent(i);
                under.saveState();
                under.setGState(gs);
                under.beginText();
                under.setFontAndSize(base, 35);
                under.setColorFill(Color.RED);
                for (int row = 0; row < rowNum; row++) {
                    float y = topMargin + (pageRect.getHeight() - topMargin) * row / rowNum;
                    float x = 80;
                    String finalWaterMark = waterMarkName + ContextUtil.getCurrentUser().getFullname() + " - "
                        + XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd");
                    under.showTextAligned(Element.ALIGN_LEFT, finalWaterMark, x, y, 340);
                }
                // 添加水印文字
                under.endText();
            }
            stamper.close();
            reader.close();
        } catch (Exception e) {
            logger.error("Exception in waterMarkPdfGenerate. file is " + inputFilePath, e);
        }
        logger.info("end waterMark");
    }

    /**
     * 删除文件
     * 
     * @param filePath
     */
    public static void waterMarkPdfDelete(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            logger.error("Exception in waterMarkPdfDelete", e);
        }
    }
}
