package com.redxun.core.util;

import java.io.File;
import java.io.OutputStream;

import org.docx4j.Docx4J;
import org.docx4j.convert.out.FOSettings;
import org.docx4j.fonts.IdentityPlusMapper;
import org.docx4j.fonts.Mapper;
import org.docx4j.fonts.PhysicalFonts;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.org.apache.poi.util.IOUtils;

/**
 * 将pdf转成 word文档。
 * @author ray
 *
 */
public class DocumentUitl {
	
	static Mapper fontMapper = new IdentityPlusMapper();
	
	static {
		fontMapper.put("隶书", PhysicalFonts.get("LiSu"));
        fontMapper.put("宋体",PhysicalFonts.get("SimSun"));
        fontMapper.put("微软雅黑",PhysicalFonts.get("Microsoft Yahei"));
        fontMapper.put("黑体",PhysicalFonts.get("SimHei"));
        fontMapper.put("楷体",PhysicalFonts.get("KaiTi"));
        fontMapper.put("新宋体",PhysicalFonts.get("NSimSun"));
        fontMapper.put("华文行楷", PhysicalFonts.get("STXingkai"));
        fontMapper.put("华文仿宋", PhysicalFonts.get("STFangsong"));
        fontMapper.put("华文楷体", PhysicalFonts.get("STKaiti"));
        
        fontMapper.put("宋体扩展",PhysicalFonts.get("simsun-extB"));
        fontMapper.put("仿宋",PhysicalFonts.get("FangSong"));
        fontMapper.put("仿宋_GB2312",PhysicalFonts.get("FangSong_GB2312"));
        fontMapper.put("幼圆",PhysicalFonts.get("YouYuan"));
        fontMapper.put("华文宋体",PhysicalFonts.get("STSong"));
        fontMapper.put("华文中宋",PhysicalFonts.get("STZhongsong"));
        fontMapper.put("方正舒体",PhysicalFonts.get("FZShuTi"));
        fontMapper.put("华文隶书",PhysicalFonts.get("STLiti"));
        fontMapper.put("华文细黑",PhysicalFonts.get("STXihei"));
        fontMapper.put("华文新魏",PhysicalFonts.get("STXinwei"));
        fontMapper.put("华文彩云",PhysicalFonts.get("STCaiyun"));
	}

	/**
	 * 将docx文件转成pdf文件。
	 * @param docxPath
	 * @param os
	 * @throws Exception
	 */
	public static void convertDocxToPDF(String docxPath, OutputStream os) throws Exception {
		try {
			WordprocessingMLPackage mlPackage = WordprocessingMLPackage.load(new File(docxPath));
			
			mlPackage.setFontMapper(fontMapper);

			FOSettings foSettings = Docx4J.createFOSettings();
			foSettings.setWmlPackage(mlPackage);
			Docx4J.toFO(foSettings, os, Docx4J.FLAG_EXPORT_PREFER_XSL);

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			IOUtils.closeQuietly(os);
		}
	}
	
	/**
	 * 将docx转成pdf文件。
	 * @param docxPath
	 * @param pdfPath
	 * @throws Exception
	 */
	public static void convertDocxToPDF(String docxPath, String pdfPath) throws Exception {
		OutputStream os =  new java.io.FileOutputStream(pdfPath);  
		convertDocxToPDF( docxPath,  os);
	}

}
