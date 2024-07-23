package com.redxun.xcmgProjectManager.core.util;

import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class ExcelUtils {
    private static Logger logger = LoggerFactory.getLogger(ExcelUtils.class);




    /**
     * 导出excel
     *
     * @param dataList
     *            数据列表
     * @param fieldCodes
     *            列对应表头的code，注意：顺序必须和表头名相同，其值为dataList中List<T>类型T的属性名
     */
    public static <T> XSSFWorkbook exportExcelByInventory(List<T> dataList, String[] firstFieldNames,String[] secondFieldNames, String[] fieldCodes) {
        XSSFWorkbook wbObj = new XSSFWorkbook();

        XSSFSheet sheet = wbObj.createSheet();

        XSSFCell cell = null;
        // 表头1
        XSSFCellStyle firstRowStyle = createFirstRowStyleXlsx(wbObj);
        XSSFRow row = sheet.createRow(0);
        // 表头赋值
        for (int i = 0; i < firstFieldNames.length; i++) {
            cell = row.createCell(i);
            cell.setCellValue(firstFieldNames[i]);
            cell.setCellStyle(firstRowStyle);
        }
        // 表头2
        XSSFCellStyle secondRowStyle = createFirstRowStyleXlsx(wbObj);
        row = sheet.createRow(1);
        // 表头赋值
        for (int i = 0; i < secondFieldNames.length; i++) {
            cell = row.createCell(i);
            cell.setCellValue(secondFieldNames[i]);
            cell.setCellStyle(secondRowStyle);
        }
        //序号
        CellRangeAddress region3 = new CellRangeAddress(0, 1, 0, 0);
        sheet.addMergedRegion(region3);
        //需要检查的项目
        CellRangeAddress region4 = new CellRangeAddress(0, 0, 1, 3);
        sheet.addMergedRegion(region4);
        //服务周期
        CellRangeAddress region5 = new CellRangeAddress(0, 0, 7, 16);
        sheet.addMergedRegion(region5);
        //对应保养操作的TOPIC号
        CellRangeAddress region6 = new CellRangeAddress(0, 1, 4, 4);
        sheet.addMergedRegion(region6);
        //维护物料编码
        CellRangeAddress region7 = new CellRangeAddress(0, 1, 5, 5);
        sheet.addMergedRegion(region7);
        //数量/容量
        CellRangeAddress region8= new CellRangeAddress(0, 1, 6, 6);
        sheet.addMergedRegion(region8);
        //备注
        CellRangeAddress region9= new CellRangeAddress(0, 1, 17, 17);
        sheet.addMergedRegion(region9);
        XSSFCellStyle cellStyle = wbObj.createCellStyle();
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        // 内容赋值
        if (dataList != null) {
            for (int i = 0; i < dataList.size(); i++) {
                String json = JSON.toJSONString(dataList.get(i));
                JSONObject temp = JSON.parseObject(json);
                XSSFRow rows = sheet.createRow(i + 2);
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

        // 设置每一列的自适应宽度
        for (int i = 0; i < fieldCodes.length; i++) {
            sheet.setColumnWidth(i, firstFieldNames[i].getBytes().length * 300);
        }
        sheet.createFreezePane(0, 2);
        return wbObj;
    }

    public static <T> HSSFWorkbook exportExcelWB(List<T> dataList, String[] fieldNames, String[] fieldCodes,
        String title) {
        HSSFWorkbook wbObj = new HSSFWorkbook();

        HSSFSheet sheet = wbObj.createSheet();
        // 标题设置样式
        HSSFCellStyle titleStyle = createTitleStyle(wbObj);
        HSSFRow row = sheet.createRow(0);
        HSSFCell cell = null;
        cell = row.createCell(0);
        cell.setCellValue(title);
        cell.setCellStyle(titleStyle);

        // 表头
        HSSFCellStyle firstRowStyle = createFirstRowStyle(wbObj);
        row = sheet.createRow(1);
        // 表头赋值
        for (int i = 0; i < fieldNames.length; i++) {
            cell = row.createCell(i);
            cell.setCellValue(fieldNames[i]);
            cell.setCellStyle(firstRowStyle);
        }

        HSSFCellStyle cellStyle = wbObj.createCellStyle();
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
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
        sheet.createFreezePane(0, 2);
        return wbObj;
    }

    public static <T> HSSFWorkbook exportMonthWorkExcel(List<T> dataList, String[] fieldNames, String[] fieldCodes,
        String title) {
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

        // 表头
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
                if (i != 0) {
                    lastRow = sheet.getRow(i + 1);
                }
                for (int j = 0; j < fieldCodes.length; j++) {
                    cell = rows.createCell(j);
                    String value = "";
                    if (StringUtils.isNotBlank(temp.getString(fieldCodes[j]))) {
                        value = temp.getString(fieldCodes[j]);
                    }
                    cell.setCellValue(value);
                    cell.setCellStyle(cellStyle);
                    if (i != 0) {
                        // 判断和上一行数据是否一样
                        String lastCellValue = lastRow.getCell(j).getStringCellValue();
                        if (lastCellValue.equals(value)) {
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
        sheet.createFreezePane(0, 2);
        return wbObj;
    }

    public static <T> HSSFWorkbook exportExcelGy(List<T> dataList, String[] fieldNames, String[] fieldCodes) {
        HSSFWorkbook wbObj = new HSSFWorkbook();

        HSSFSheet sheet = wbObj.createSheet();
        // 标题设置样式
        HSSFRow row = sheet.createRow(0);
        HSSFCell cell = null;
        cell = row.createCell(0);

        // 表头
        HSSFCellStyle firstRowStyle = createFirstRowStyle(wbObj);
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
                HSSFRow rows = sheet.createRow(i + 1);
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

        // 设置每一列的自适应宽度
        for (int i = 0; i < fieldNames.length; i++) {
            sheet.setColumnWidth(i, fieldNames[i].getBytes().length * 3 * 256);
        }
        sheet.createFreezePane(0, 2);
        return wbObj;
    }

    public static void writeWorkBook2Stream(String exportFileName, Workbook wbObj, HttpServletResponse response) {
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
    public static void writeXlsxWorkBook2Stream(String exportFileName, Workbook wbObj, HttpServletResponse response) {
        try {
            String excelName = exportFileName + ".xlsx";
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + java.net.URLEncoder.encode(excelName, "UTF-8"));
            response.setStatus(HttpServletResponse.SC_OK);
            OutputStream out = response.getOutputStream();
            wbObj.write(out);
            out.close();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            logger.error("Exception in writeXlsxWorkBook2Stream", e);
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

    private static HSSFCellStyle createFirstRowStyle(HSSFWorkbook wbObj) {
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

    /**
     * 导出待分割线的excel
     */
    public static <T> HSSFWorkbook exportStyleExcel(List<T> dataList, String[] fieldNames, String[] fieldCodes,
        String title) {
        HSSFWorkbook wbObj = new HSSFWorkbook();

        HSSFSheet sheet = wbObj.createSheet();
        // 标题设置样式
        HSSFCellStyle titleStyle = createTitleStyle(wbObj);
        HSSFCellStyle cellStyle = createCellStyle(wbObj);
        HSSFRow row = sheet.createRow(0);
        HSSFCell cell = null;
        cell = row.createCell(0);
        cell.setCellValue(title);
        cell.setCellStyle(titleStyle);

        // 表头
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
                    String field = fieldCodes[j];
                    cell = rows.createCell(j);
                    String value = "";
                    if (field.endsWith("_float")) {
                        field = field.replaceAll("_float", "");
                        cell.setCellValue(temp.getDoubleValue(field));
                        cell.setCellStyle(cellStyle);
                    } else {
                        value = temp.getString(field);
                        cell.setCellValue(value);
                        cell.setCellStyle(cellStyle);
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
        sheet.createFreezePane(0, 2);
        return wbObj;
    }

    private static HSSFCellStyle createCellStyle(HSSFWorkbook wbObj) {
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

    /**
     * 导出excel,不带表头title
     * 
     * @param dataList
     * @param fieldNames
     * @param fieldCodes
     */
    public static <T> HSSFWorkbook exportExcelWBWithoutTitle(List<T> dataList, String[] fieldNames,
        String[] fieldCodes) {
        HSSFWorkbook wbObj = new HSSFWorkbook();
        HSSFSheet sheet = wbObj.createSheet();
        HSSFCell cell = null;
        // 表头
        HSSFCellStyle firstRowStyle = createFirstRowStyle(wbObj);
        HSSFRow row = sheet.createRow(0);
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
                HSSFRow rows = sheet.createRow(i + 1);
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
        // 设置每一列的自适应宽度
        for (int i = 0; i < fieldNames.length; i++) {
            sheet.setColumnWidth(i, fieldNames[i].getBytes().length * 3 * 256);
        }
        return wbObj;
    }

    private static XSSFCellStyle createTitleStyle(XSSFWorkbook wbObj) {
        // 设置字体
        XSSFFont font = wbObj.createFont();
        // 设置字体大小
        font.setFontHeightInPoints((short)16);
        // 字体加粗
        font.setBold(true);
        // 设置字体名字
        font.setFontName("Courier New");
        // 设置样式
        XSSFCellStyle topstyle = wbObj.createCellStyle();
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


    private static XSSFCellStyle createFirstRowStyleXlsx(XSSFWorkbook wbObj) {
        // 设置字体
        XSSFFont font = wbObj.createFont();
        // 设置字体大小
        font.setFontHeightInPoints((short)11);
        // 字体加粗
        font.setBold(true);
        // 设置字体名字
        font.setFontName("Courier New");
        // 设置样式
        XSSFCellStyle topstyle = wbObj.createCellStyle();
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

    /**
     * 导出后缀xlsx的excel,不带表头title
     *
     * @param dataList
     * @param fieldNames
     * @param fieldCodes
     */
    public static <T> XSSFWorkbook exportXlsxExcelWBWithoutTitle(List<T> dataList, String[] fieldNames,
                                                             String[] fieldCodes) {
        XSSFWorkbook wbObj = new XSSFWorkbook();
        XSSFSheet sheet = wbObj.createSheet();
        XSSFCell cell = null;
        // 表头
        XSSFCellStyle firstRowStyle = createFirstRowStyleXlsx(wbObj);
        XSSFRow row = sheet.createRow(0);
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
                XSSFRow rows = sheet.createRow(i + 1);
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
        // 设置每一列的自适应宽度
        for (int i = 0; i < fieldCodes.length; i++) {
            sheet.setColumnWidth(i, fieldCodes[i].getBytes().length * 3 * 256);
        }
        return wbObj;
    }

}
