package com.redxun.core.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.lang.StringUtils;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xhtmlrenderer.pdf.PDFEncryption;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.BaseFont;


/**
 * html转PDF工具。
 * @author yongguo
 */
public class PdfUtil {

	/**
	 * 将html生成文件到流。
	 * @param content		html代码
	 * @param output		输出流
	 */
	public static  void convertHtmlToPdf(String content, OutputStream output)  {
		try {
			ITextRenderer renderer = new ITextRenderer();
			// 解决中文支持问题
			ITextFontResolver resolver = renderer.getFontResolver();
			String path=FileUtil.getWebRootPath() + "/font/";
			resolver.addFont(path +"msyh.ttf", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
			renderer.setDocumentFromString(content);
			// 解决图片的相对路径问题,图片路径必须以file开头
			renderer.getSharedContext().setBaseURL("file:/");
			renderer.layout();
			renderer.createPDF(output); 
			
		}
		catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	/**
	 * 将html转换后到文件。
	 * @param content		html内容
	 * @param storagePath	文件输出路径
	 */
	public static  void convertHtmlToPdf(String content, String storagePath)  {
		FileOutputStream os = null;
		try {
			File file = new File(storagePath);
			if (!file.exists()) {
				file.createNewFile();
			}
			os = new FileOutputStream(file);
			convertHtmlToPdf( content, os);
		}  catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != os){ 
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * HTML代码转PDF文档整体html
	 * 
	 * @param content
	 *            待转换的HTML代码
	 * @param storagePath
	 *            保存为PDF文件的路径
	 * @throws IOException
	 */
	public static void parsePdf(String content, String storagePath, String password) {
		FileOutputStream os = null;
		try {
			File file = new File(storagePath);
			if (!file.exists()) {
				file.createNewFile();
			}
			os = new FileOutputStream(file);

			ITextRenderer renderer = new ITextRenderer();
			// 解决中文支持问题
			ITextFontResolver resolver = renderer.getFontResolver();
			String path=FileUtil.getWebRootPath() + "/font/";
			resolver.addFont(path +"msyh.ttf", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
			renderer.setDocumentFromString(content);
			// 解决图片的相对路径问题,图片路径必须以file开头
			renderer.getSharedContext().setBaseURL("file:/");
			renderer.layout();

			if (StringUtils.isNotBlank(password)) {
				PDFEncryption pdfEncryption = new PDFEncryption(password.getBytes(), (password + "_REDXUN_PDF_DECODE").getBytes());
				renderer.setPDFEncryption(pdfEncryption);
			}
			renderer.createPDF(os);
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != os) {
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
