package com.redxun.core.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.aspose.cells.PdfSaveOptions;
import com.aspose.cells.Workbook;
import com.aspose.slides.Presentation;
import com.aspose.words.Document;
import com.aspose.words.SaveFormat;

public class AsposeUtil {
    private static final Logger logger = LogManager.getLogger(AsposeUtil.class);

    public static void convertOffice2Pdf(String originalDocPath, String convertPdfPath, HttpServletResponse response) {
        OutputStream out = null;
        BufferedOutputStream bos = null;
        try {
            String fileExt = OfficeDocPreview.findFileExt(originalDocPath);
            File originalFile = new File(originalDocPath);
            if (!originalFile.exists()) {
                return;
            }

            /*        response.setContentType("application/octet-stream");
            response.setHeader("Content-disposition","attachment;filename=" + URLEncoder.encode(pdfPath, "UTF-8"));
            response.setCharacterEncoding("UTF-8");*/
            out = response.getOutputStream();
            bos = new BufferedOutputStream(out);
            switch (fileExt) {
                case "doc":
                case "docx":
                case "txt":
                    Document document = new Document(new FileInputStream(originalFile));
                    document.save(bos, SaveFormat.PDF);
                    break;
                case "xls":
                case "xlsx":
                    Workbook doc = new Workbook(new FileInputStream(originalFile));
                    PdfSaveOptions opts = new PdfSaveOptions();
                    opts.setOnePagePerSheet(true);
                    doc.save(bos, opts);
                    break;
                case "ppt":
                case "pptx":
                    Presentation ppt = new Presentation(new FileInputStream(originalFile));
                    ppt.save(bos, com.aspose.slides.SaveFormat.Pdf);
                    break;
                default:
            }

            bos.flush();
            out.flush();
        } catch (Exception e) {
            logger.error(e);
        } finally {
            IOUtils.closeQuietly(bos);
            IOUtils.closeQuietly(out);
        }
    }
}