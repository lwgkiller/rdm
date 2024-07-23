package com.redxun.util;

import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class CommonExcelUtils {
    private static Logger logger = LoggerFactory.getLogger(CommonExcelUtils.class);

    /**
     * 导出excel （普通样式的excel 导出）
     *
     * @param dataList
     *            数据列表
     * @param fieldNames
     *            表头名
     * @param fieldCodes
     *            列对应表头的code，注意：顺序必须和表头名相同，其值为dataList中List<T>类型T的属性名
     * @param title
     *            表头居中名称
     */
    public static <T> HSSFWorkbook exportExcelWB(List<T> dataList, String[] fieldNames, String[] fieldCodes, String title) {
        HSSFWorkbook wbObj = new HSSFWorkbook();

        HSSFSheet sheet = wbObj.createSheet();
        // 标题设置样式
        HSSFCellStyle titleStyle = createTitleStyle(wbObj);
        HSSFRow row = sheet.createRow(0);
        HSSFCell cell = null;
        cell = row.createCell(0);
        cell.setCellValue(title);
        cell.setCellStyle(titleStyle);

        //表头
        HSSFCellStyle firstRowStyle = createFirstRowStyle(wbObj);
        row = sheet.createRow(1);
        // 表头赋值
        for (int i = 0; i < fieldNames.length; i++) {
            cell = row.createCell(i);
            cell.setCellValue(fieldNames[i]);
            cell.setCellStyle(firstRowStyle);
        }

        // 内容赋值
        if (dataList != null) {
            for (int i = 0; i < dataList.size(); i++) {
                String json = JSON.toJSONString(dataList.get(i));
                JSONObject temp = JSON.parseObject(json);
                HSSFRow rows = sheet.createRow(i + 2);
                for (int j = 0; j < fieldCodes.length; j++) {
                    cell = rows.createCell(j);
                    String value = "";
                    if (StringUtils.isNotBlank(temp.getString(fieldCodes[j]))) {
                        value = temp.getString(fieldCodes[j]);
                    }
                    cell.setCellValue(value);
                }
            }
        }

        // 合并第一行表头 单元格
        CellRangeAddress region = new CellRangeAddress(0, 0, 0, fieldNames.length - 1);
        sheet.addMergedRegionUnsafe(region);
        // 设置每一列的自适应宽度
        for (int i = 0; i < fieldNames.length; i++) {
            sheet.setColumnWidth(i, fieldNames[i].getBytes().length * 3 * 256);
        }
        sheet.createFreezePane(0,2);
        return wbObj;
    }

    /**
     * 能够合并单元格的excel导出
     * */
    public static <T> HSSFWorkbook exportComboExcel(List<T> dataList, String[] fieldNames, String[] fieldCodes, String title) {
        HSSFWorkbook wbObj = new HSSFWorkbook();

        HSSFSheet sheet = wbObj.createSheet();
        // 标题设置样式
        HSSFCellStyle titleStyle = createTitleStyle(wbObj);
        HSSFCellStyle cellStyle = createComCellStyle(wbObj);
        HSSFRow row = sheet.createRow(0);
        HSSFCell cell = null;
        cell = row.createCell(0);
        cell.setCellValue(title);
        cell.setCellStyle(titleStyle);

        //表头
        HSSFCellStyle firstRowStyle = createFirstRowStyle(wbObj);
        row = sheet.createRow(1);
        // 表头赋值
        for (int i = 0; i < fieldNames.length; i++) {
            cell = row.createCell(i);
            cell.setCellValue(fieldNames[i]);
            cell.setCellStyle(firstRowStyle);
        }

        // 内容赋值
        if (dataList != null) {
            for (int i = 0; i < dataList.size(); i++) {
                String json = JSON.toJSONString(dataList.get(i));
                JSONObject temp = JSON.parseObject(json);
                HSSFRow rows = sheet.createRow(i + 2);
                HSSFRow lastRow = null;
                if(i!=0){
                    lastRow = sheet.getRow(i+1);
                }
                for (int j = 0; j < fieldCodes.length; j++) {
                    cell = rows.createCell(j);
                    String value = "";
                    if (StringUtils.isNotBlank(temp.getString(fieldCodes[j]))) {
                        value = temp.getString(fieldCodes[j]);
                    }
                    cell.setCellValue(value);
                    cell.setCellStyle(cellStyle);
                    if(i!=0){
                        //判断和上一行数据是否一样
                        String lastCellValue = lastRow.getCell(j).getStringCellValue();
                        if(lastCellValue.equals(value)){
                            CellRangeAddress region = new CellRangeAddress(i + 1, i + 2, j, j);
                            sheet.addMergedRegionUnsafe(region);
                        }
                    }
                }
            }
        }

        // 合并第一行表头 单元格
        CellRangeAddress region = new CellRangeAddress(0, 0, 0, fieldNames.length - 1);
        sheet.addMergedRegionUnsafe(region);
        // 设置每一列的自适应宽度
        for (int i = 0; i < fieldNames.length; i++) {
            sheet.setColumnWidth(i, fieldNames[i].getBytes().length * 2 * 256);
        }
        sheet.createFreezePane(0,2);
        return wbObj;
    }

    /**
     * 不合并单元格的excel导出，字体居中，加粗
     * */
    public static <T> HSSFWorkbook exportStyleExcel(List<T> dataList, String[] fieldNames, String[] fieldCodes, String title) {
        HSSFWorkbook wbObj = new HSSFWorkbook();

        HSSFSheet sheet = wbObj.createSheet();
        // 标题设置样式
        HSSFCellStyle titleStyle = createTitleStyle(wbObj);
        HSSFCellStyle cellStyle = createComCellStyle(wbObj);
        HSSFRow row = sheet.createRow(0);
        HSSFCell cell = null;
        cell = row.createCell(0);
        cell.setCellValue(title);
        cell.setCellStyle(titleStyle);

        //表头
        HSSFCellStyle firstRowStyle = createFirstRowStyle(wbObj);
        row = sheet.createRow(1);
        // 表头赋值
        for (int i = 0; i < fieldNames.length; i++) {
            cell = row.createCell(i);
            cell.setCellValue(fieldNames[i]);
            cell.setCellStyle(firstRowStyle);
        }

        // 内容赋值
        if (dataList != null) {
            for (int i = 0; i < dataList.size(); i++) {
                String json = JSON.toJSONString(dataList.get(i));
                JSONObject temp = JSON.parseObject(json);
                HSSFRow rows = sheet.createRow(i + 2);
                for (int j = 0; j < fieldCodes.length; j++) {
                    cell = rows.createCell(j);
                    String value = "";
                    if (StringUtils.isNotBlank(temp.getString(fieldCodes[j]))) {
                        value = temp.getString(fieldCodes[j]);
                    }
                    cell.setCellValue(value);
                    cell.setCellStyle(cellStyle);
                }
            }
        }

        // 合并第一行表头 单元格
        CellRangeAddress region = new CellRangeAddress(0, 0, 0, fieldNames.length - 1);
        sheet.addMergedRegionUnsafe(region);
        // 设置每一列的自适应宽度
        for (int i = 0; i < fieldNames.length; i++) {
            sheet.setColumnWidth(i, fieldNames[i].getBytes().length * 2 * 256);
        }
        sheet.createFreezePane(0,2);
        return wbObj;
    }

    public static void writeWorkBook2Stream(String exportFileName, HSSFWorkbook wbObj, HttpServletResponse response) {
        try {
            String excelName = exportFileName + ".xls";
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition",
                "attachment;filename=" + java.net.URLEncoder.encode(excelName, "UTF-8"));
            OutputStream out = response.getOutputStream();
            wbObj.write(out);
            out.close();
        } catch (Exception e) {
            logger.error("Exception in writeWorkBook2Stream", e);
        }
    }

    private static HSSFCellStyle createTitleStyle(HSSFWorkbook wbObj) {
        // 设置字体
        HSSFFont font = wbObj.createFont();
        // 设置字体大小
        font.setFontHeightInPoints((short)16);
        // 字体加粗
        font.setBold(true);
        // 设置字体名字
        font.setFontName("Courier New");
        // 设置样式
        HSSFCellStyle topstyle = wbObj.createCellStyle();
        // 设置下边框
        topstyle.setBorderTop(BorderStyle.THIN);
        // 设置右边框
        topstyle.setBorderLeft(BorderStyle.THIN);
        // 设置下边框
        topstyle.setBorderBottom(BorderStyle.THIN);
        // 设置右边框
        topstyle.setBorderRight(BorderStyle.THIN);
        topstyle.setBottomBorderColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        topstyle.setTopBorderColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        topstyle.setLeftBorderColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        topstyle.setRightBorderColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        // 在样式中应用设置的字体
        topstyle.setFont(font);
        // 设置自动换行
        topstyle.setWrapText(false);
        // 设置水平对齐的样式为居中对齐；
        topstyle.setAlignment(HorizontalAlignment.CENTER);
        topstyle.setVerticalAlignment(VerticalAlignment.CENTER);
        return topstyle;
    }

    public static HSSFCellStyle createFirstRowStyle(HSSFWorkbook wbObj) {
        // 设置字体
        HSSFFont font = wbObj.createFont();
        // 设置字体大小
        font.setFontHeightInPoints((short)12);
        // 字体加粗
        font.setBold(true);
        // 设置字体名字
        font.setFontName("Courier New");
        // 设置样式
        HSSFCellStyle topstyle = wbObj.createCellStyle();
        // 设置下边框
        topstyle.setBorderTop(BorderStyle.THIN);
        // 设置右边框
        topstyle.setBorderLeft(BorderStyle.THIN);
        // 设置下边框
        topstyle.setBorderBottom(BorderStyle.THIN);
        // 设置右边框
        topstyle.setBorderRight(BorderStyle.THIN);
        topstyle.setBottomBorderColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        topstyle.setTopBorderColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        topstyle.setLeftBorderColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        topstyle.setRightBorderColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        // 在样式中应用设置的字体
        topstyle.setFont(font);
        // 设置自动换行
        topstyle.setWrapText(false);
        // 设置水平对齐的样式为居中对齐；
        topstyle.setAlignment(HorizontalAlignment.CENTER);
        topstyle.setVerticalAlignment(VerticalAlignment.CENTER);
        return topstyle;
    }

    private static HSSFCellStyle createComCellStyle(HSSFWorkbook wbObj) {
        // 设置字体
        HSSFFont font = wbObj.createFont();
        // 设置字体大小
        font.setFontHeightInPoints((short)11);
        // 字体加粗
        font.setBold(true);
        // 设置字体名字
        font.setFontName("Courier New");
        // 设置样式
        HSSFCellStyle topstyle = wbObj.createCellStyle();
        // 设置下边框
        topstyle.setBorderTop(BorderStyle.THIN);
        // 设置右边框
        topstyle.setBorderLeft(BorderStyle.THIN);
        // 设置下边框
        topstyle.setBorderBottom(BorderStyle.THIN);
        // 设置右边框
        topstyle.setBorderRight(BorderStyle.THIN);
        topstyle.setBottomBorderColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        topstyle.setTopBorderColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        topstyle.setLeftBorderColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        topstyle.setRightBorderColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        // 在样式中应用设置的字体
        topstyle.setFont(font);
        // 设置自动换行
        topstyle.setWrapText(true);
        // 设置水平对齐的样式为居中对齐；
        topstyle.setAlignment(HorizontalAlignment.CENTER);
        topstyle.setVerticalAlignment(VerticalAlignment.CENTER);
        return topstyle;
    }

}
