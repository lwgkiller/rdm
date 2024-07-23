package com.redxun.rdmZhgl.core.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.StringValue;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

public class YfjbExcelUtils {

    /**
     * 导出excel
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
    public static <T> HSSFWorkbook exportExcelWB(List<T> dataList,
                                                 String[] fieldNames, String[] fieldCodes, String title) {
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
                    String field = fieldCodes[j];
                    cell = rows.createCell(j);
                    String value = "";
                    if(field.endsWith("_float")){
                        field = field.replaceAll("_float","");
                        cell.setCellValue(temp.getDoubleValue(field));
                        cell.setCellStyle(cellStyle);
                    }else {
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
        sheet.createFreezePane(0,2);
        return wbObj;
    }

    public static <T> HSSFWorkbook exportModelExcel(List<T> dataList, String[] fieldNames, String[] fieldCodes, String title,JSONObject yearReportJson,JSONObject monthReport) {
        HSSFWorkbook wbObj = new HSSFWorkbook();

        HSSFSheet sheet = wbObj.createSheet();
        // 标题设置样式
        HSSFCellStyle titleStyle = createModelTitleStyle(wbObj);
        HSSFCellStyle cellStyle = createCellStyle(wbObj);
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
                //设置降本项目进度跟踪
                JSONArray processList = temp.getJSONArray("processList");
                for(int k=0;k<processList.size();k++){
                    cell = rows.createCell(fieldCodes.length+k);
                    String yearMonth = processList.getJSONObject(processList.size()-k-1).getString("yearMonth").substring(0,7);
                    String typeName = processList.getJSONObject(processList.size()-k-1).getString("typeName");
                    String content =  processList.getJSONObject(processList.size()-k-1).getString("content");
                    String value = yearMonth+typeName+"："+content;
                    cell.setCellValue(value);
                    cell.setCellStyle(cellStyle);
                }
            }
            HSSFCellStyle speStyle = createSpeStyle(wbObj);
            HSSFRow rows = sheet.createRow(dataList.size() + 3);
            cell = rows.createCell(1);
            cell.setCellValue("总计");
            cell.setCellStyle(speStyle);
            cell = rows.createCell(2);
            cell.setCellValue(yearReportJson.getString("lastYear"));
            cell.setCellStyle(speStyle);
            cell = rows.createCell(3);
            cell.setCellValue(yearReportJson.getString("currentYear"));
            cell.setCellStyle(speStyle);
            rows = sheet.createRow(dataList.size() + 4);
            cell = rows.createCell(0);
            cell.setCellValue("策划成本");
            cell.setCellStyle(speStyle);
            cell = rows.createCell(1);
            cell.setCellValue(yearReportJson.getString("totalPerCost"));
            cell.setCellStyle(speStyle);
            cell = rows.createCell(2);
            cell.setCellValue(yearReportJson.getString("lastYearPerCost"));
            cell.setCellStyle(speStyle);
            cell = rows.createCell(3);
            cell.setCellValue(yearReportJson.getString("currentPerCost"));
            cell.setCellStyle(speStyle);
            rows = sheet.createRow(dataList.size() + 5);
            cell = rows.createCell(0);
            cell.setCellValue("实现降本");
            cell.setCellStyle(speStyle);
            cell = rows.createCell(1);
            cell.setCellValue(yearReportJson.getString("totalAchieveCost"));
            cell.setCellStyle(speStyle);
            cell = rows.createCell(2);
            cell.setCellValue(yearReportJson.getString("lastAchieveCost"));
            cell.setCellStyle(speStyle);
            cell = rows.createCell(3);
            cell.setCellValue(yearReportJson.getString("currentAchieveCost"));
            cell.setCellStyle(speStyle);
            rows = sheet.createRow(dataList.size() + 6);
            cell = rows.createCell(0);
            cell.setCellValue("整机成本");
            cell.setCellStyle(speStyle);
            cell = rows.createCell(2);
            cell.setCellValue(yearReportJson.getString("lastProductCost"));
            cell.setCellStyle(speStyle);
            cell = rows.createCell(3);
            cell.setCellValue(yearReportJson.getString("currentProductCost"));
            cell.setCellStyle(speStyle);
        }
        //设置月度单台降本统计

        HSSFRow monthReportRow = sheet.createRow(dataList.size() + 8);
        cell = monthReportRow.createCell(1);
        cell.setCellValue(monthReport.getString("lastTitle"));
        cell.setCellStyle(cellStyle);
        monthReportRow = sheet.createRow(dataList.size() + 11);
        cell = monthReportRow.createCell(1);
        cell.setCellValue(monthReport.getString("currentTitle"));
        cell.setCellStyle(cellStyle);
        JSONArray monthReportArray = monthReport.getJSONArray("list");
        for(int i=0;i<monthReportArray.size();i++){
            JSONObject temp = monthReportArray.getJSONObject(i);
            if(i==0){
                monthReportRow = sheet.createRow(dataList.size() + 9);
                for(int j=1;j<=13;j++){
                    cell = monthReportRow.createCell(j);
                    cell.setCellValue(temp.getString(String.valueOf(j)));
                    cell.setCellStyle(cellStyle);
                }
                monthReportRow = sheet.createRow(dataList.size() + 12);
                for(int j=1;j<=13;j++){
                    cell = monthReportRow.createCell(j);
                    cell.setCellValue(temp.getString(String.valueOf(j)));
                    cell.setCellStyle(cellStyle);
                }
            }else if(i==1){
                monthReportRow = sheet.createRow(dataList.size() + 10);
                for(int j=1;j<=13;j++){
                    cell = monthReportRow.createCell(j);
                    cell.setCellValue(temp.getString(String.valueOf(j)));
                    cell.setCellStyle(cellStyle);
                }
            }else if(i==2){
                monthReportRow = sheet.createRow(dataList.size() + 13);
                for(int j=1;j<=13;j++){
                    cell = monthReportRow.createCell(j);
                    cell.setCellValue(temp.getString(String.valueOf(j)));
                    cell.setCellStyle(cellStyle);
                }
                monthReportRow = sheet.createRow(dataList.size() + 14);
                cell = monthReportRow.createCell(12);
                cell.setCellValue("总计");
                cell.setCellStyle(cellStyle);
                cell = monthReportRow.createCell(13);
                cell.setCellValue(monthReport.getString("total"));
                cell.setCellStyle(cellStyle);
            }
        }
        // 合并第一行表头 单元格
        CellRangeAddress region = new CellRangeAddress(0, 0, 0, fieldNames.length - 1);
        sheet.addMergedRegionUnsafe(region);
        region = new CellRangeAddress(dataList.size() + 8, dataList.size() + 8, 1, 13);
        sheet.addMergedRegionUnsafe(region);
        region = new CellRangeAddress(dataList.size() + 11, dataList.size() + 11, 1, 13);
        sheet.addMergedRegionUnsafe(region);
        // 设置每一列的自适应宽度
        for (int i = 0; i < fieldNames.length; i++) {
            sheet.setColumnWidth(i, fieldNames[i].getBytes().length * 200*2);
        }
        sheet.createFreezePane(0,2);
        return wbObj;
    }
    /**
     * 导出型号汇总表
     * */
    public static <T> HSSFWorkbook exportTotalModel(List<T> currentDataList,List<T> lastDataList,
                                                 String[] fieldNames, String[] fieldCodes, String title1,String title2) {
        HSSFWorkbook wbObj = new HSSFWorkbook();

        HSSFSheet sheet = wbObj.createSheet();
        // 标题设置样式
        HSSFCellStyle titleStyle = createTitleStyle(wbObj);
        HSSFCellStyle cellStyle = createCellStyle(wbObj);
        HSSFRow row = sheet.createRow(0);
        HSSFCell cell = null;
        cell = row.createCell(0);
        cell.setCellValue(title1);
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
        if (currentDataList != null) {
            for (int i = 0; i < currentDataList.size(); i++) {
                String json = JSON.toJSONString(currentDataList.get(i));
                JSONObject temp = JSON.parseObject(json);
                HSSFRow rows = sheet.createRow(i + 2);
                for (int j = 0; j < fieldCodes.length; j++) {
                    String field = fieldCodes[j];
                    cell = rows.createCell(j);
                    String value = "";
                    if(field.endsWith("_float")){
                        field = field.replaceAll("_float","");
                        cell.setCellValue(new DecimalFormat("#.##").format(temp.getDoubleValue(field)));
                        cell.setCellStyle(cellStyle);
                    }else {
                        value = temp.getString(field);
                        cell.setCellValue(value);
                        cell.setCellStyle(cellStyle);
                    }

                }
            }
        }
        row = sheet.createRow(currentDataList.size()+2);
        cell = row.createCell(0);
        cell.setCellValue(title2);
        cell.setCellStyle(titleStyle);
        //表头
        row = sheet.createRow(currentDataList.size()+3);
        // 表头赋值
        for (int i = 0; i < fieldNames.length; i++) {
            cell = row.createCell(i);
            cell.setCellValue(fieldNames[i]);
            cell.setCellStyle(firstRowStyle);
        }
        if (lastDataList != null) {
            for (int i = 0; i < lastDataList.size(); i++) {
                String json = JSON.toJSONString(lastDataList.get(i));
                JSONObject temp = JSON.parseObject(json);
                HSSFRow rows = sheet.createRow(currentDataList.size() +4+i);
                for (int j = 0; j < fieldCodes.length; j++) {
                    String field = fieldCodes[j];
                    cell = rows.createCell(j);
                    String value = "";
                    if(field.endsWith("_float")){
                        field = field.replaceAll("_float","");
                        cell.setCellValue(temp.getFloatValue(field));
                        cell.setCellStyle(cellStyle);
                    }else {
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
        region = new CellRangeAddress(currentDataList.size()+2, currentDataList.size()+2, 0, fieldNames.length - 1);
        sheet.addMergedRegionUnsafe(region);
        // 设置每一列的自适应宽度
        for (int i = 0; i < fieldNames.length; i++) {
            sheet.setColumnWidth(i, fieldNames[i].getBytes().length * 2 * 256);
        }
        sheet.createFreezePane(0,2);
        return wbObj;
    }


    /**
     * 降本增效零部件变更汇总表
     * */
    public static <T> HSSFWorkbook exportJbzxExcel(List<T> dataList, String[] fieldNames1,String[] fieldNames2, String[] fieldCodes, String title) {
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

        //表头
        HSSFCellStyle firstRowStyle = createFirstRowStyle(wbObj);
        row = sheet.createRow(1);
        // 表头赋值
        for (int i = 0; i < fieldNames1.length; i++) {
            cell = row.createCell(i);
            cell.setCellValue(fieldNames1[i]);
            cell.setCellStyle(firstRowStyle);
        }
        row = sheet.createRow(2);
        // 表头赋值
        for (int i = 0; i < fieldNames2.length; i++) {
            cell = row.createCell(i);
            cell.setCellValue(fieldNames2[i]);
            cell.setCellStyle(firstRowStyle);
        }
        // 内容赋值
        if (dataList != null) {
            for (int i = 0; i < dataList.size(); i++) {
                String json = JSON.toJSONString(dataList.get(i));
                JSONObject temp = JSON.parseObject(json);
                HSSFRow rows = sheet.createRow(i + 3);
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
        CellRangeAddress region = new CellRangeAddress(0, 0, 0, fieldNames1.length - 1);
        sheet.addMergedRegionUnsafe(region);
        region = new CellRangeAddress(1, 2, 0, 0);
        sheet.addMergedRegionUnsafe(region);
        region = new CellRangeAddress(1, 2, 1, 1);
        sheet.addMergedRegionUnsafe(region);
        region = new CellRangeAddress(1, 2, 2, 2);
        sheet.addMergedRegionUnsafe(region);
        region = new CellRangeAddress(1, 1, 3, 5);
        sheet.addMergedRegionUnsafe(region);
        region = new CellRangeAddress(1, 1, 6, 8);
        sheet.addMergedRegionUnsafe(region);
        region = new CellRangeAddress(1, 2, 9, 9);
        sheet.addMergedRegionUnsafe(region);
        region = new CellRangeAddress(1, 2, 10, 10);
        sheet.addMergedRegionUnsafe(region);
        region = new CellRangeAddress(1, 2, 11, 11);
        sheet.addMergedRegionUnsafe(region);
        region = new CellRangeAddress(1, 2, 12, 12);
        sheet.addMergedRegionUnsafe(region);
        region = new CellRangeAddress(1, 2, 13, 13);
        sheet.addMergedRegionUnsafe(region);
        region = new CellRangeAddress(1, 1, 14, 16);
        sheet.addMergedRegionUnsafe(region);
        // 设置每一列的自适应宽度
        for (int i = 0; i < fieldCodes.length; i++) {
            sheet.setColumnWidth(i, fieldCodes[i].getBytes().length * 200*2);
        }
        return wbObj;
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
    private static HSSFCellStyle createModelTitleStyle(HSSFWorkbook wbObj) {
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
        topstyle.setAlignment(HorizontalAlignment.LEFT);
        topstyle.setVerticalAlignment(VerticalAlignment.CENTER);
        return topstyle;
    }
    private static HSSFCellStyle createSpeStyle(HSSFWorkbook wbObj) {
        // 设置字体
        HSSFFont font = wbObj.createFont();
        // 设置字体大小
        font.setFontHeightInPoints((short)11);
        // 字体加粗
        font.setBold(true);
        font.setColor(HSSFColor.HSSFColorPredefined.RED.getIndex());
        // 设置字体名字
        font.setFontName("Courier New");
        // 设置样式
        HSSFCellStyle topstyle = wbObj.createCellStyle();
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
        // 设置水平对齐的样式为居中对齐；
        topstyle.setAlignment(HorizontalAlignment.CENTER);
        topstyle.setVerticalAlignment(VerticalAlignment.CENTER);
        return topstyle;
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
     * 新品试制导出
     * */
    public static <T> HSSFWorkbook exportXpszExcel(List<T> dataList, String[] fieldNames1,String[] fieldNames2, String[] fieldCodes) {
        HSSFWorkbook wbObj = new HSSFWorkbook();

        HSSFSheet sheet = wbObj.createSheet();
        // 标题设置样式
        HSSFCellStyle cellStyle = createCellStyle(wbObj);
        HSSFRow row = null;
        HSSFCell cell = null;

        //表头
        HSSFCellStyle firstRowStyle = createFirstRowStyle(wbObj);
        row = sheet.createRow(0);
        // 表头赋值
        for (int i = 0; i < fieldNames1.length; i++) {
            cell = row.createCell(i);
            cell.setCellValue(fieldNames1[i]);
            cell.setCellStyle(firstRowStyle);
        }
        row = sheet.createRow(1);
        // 表头赋值
        for (int i = 0; i < fieldNames2.length; i++) {
            cell = row.createCell(i);
            cell.setCellValue(fieldNames2[i]);
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
        CellRangeAddress region = null;
        region = new CellRangeAddress(0, 1, 0, 0);
        sheet.addMergedRegionUnsafe(region);
        region = new CellRangeAddress(0, 1, 1, 1);
        sheet.addMergedRegionUnsafe(region);
        region = new CellRangeAddress(0, 1, 2, 2);
        sheet.addMergedRegionUnsafe(region);
        region = new CellRangeAddress(0, 1, 3, 3);
        sheet.addMergedRegionUnsafe(region);
        region = new CellRangeAddress(0, 1, 4, 4);
        sheet.addMergedRegionUnsafe(region);
        region = new CellRangeAddress(0, 1, 5, 5);
        sheet.addMergedRegionUnsafe(region);
        region = new CellRangeAddress(0, 1, 6, 6);
        sheet.addMergedRegionUnsafe(region);
        region = new CellRangeAddress(0, 0, 7, 12);
        sheet.addMergedRegionUnsafe(region);
        region = new CellRangeAddress(0, 0, 13, 14);
        sheet.addMergedRegionUnsafe(region);
        region = new CellRangeAddress(0, 0, 20, 21);
        sheet.addMergedRegionUnsafe(region);
        region = new CellRangeAddress(0, 0, 23, 24);
        sheet.addMergedRegionUnsafe(region);
        region = new CellRangeAddress(0, 0, 25, 28);
        sheet.addMergedRegionUnsafe(region);
        region = new CellRangeAddress(0, 0, 30, 34);
        sheet.addMergedRegionUnsafe(region);
        region = new CellRangeAddress(0, 1, 35, 35);
        sheet.addMergedRegionUnsafe(region);
        region = new CellRangeAddress(0, 1, 36, 36);
        sheet.addMergedRegionUnsafe(region);
        region = new CellRangeAddress(0, 1, 37, 37);
        sheet.addMergedRegionUnsafe(region);
        region = new CellRangeAddress(0, 1, 38, 38);
        sheet.addMergedRegionUnsafe(region);
        region = new CellRangeAddress(0, 1, 39, 39);
        sheet.addMergedRegionUnsafe(region);
        region = new CellRangeAddress(0, 1, 40, 40);
        sheet.addMergedRegionUnsafe(region);
        region = new CellRangeAddress(0, 1, 41, 41);
        sheet.addMergedRegionUnsafe(region);
        region = new CellRangeAddress(0, 1, 42, 42);
        sheet.addMergedRegionUnsafe(region);
        region = new CellRangeAddress(0, 1, 43, 43);
        sheet.addMergedRegionUnsafe(region);
        region = new CellRangeAddress(0, 1, 44, 44);
        sheet.addMergedRegionUnsafe(region);
        region = new CellRangeAddress(0, 1, 45, 45);
        sheet.addMergedRegionUnsafe(region);
        // 设置每一列的自适应宽度
        for (int i = 0; i < fieldCodes.length; i++) {
            sheet.setColumnWidth(i, fieldCodes[i].getBytes().length * 200*2);
        }
        return wbObj;
    }
}
