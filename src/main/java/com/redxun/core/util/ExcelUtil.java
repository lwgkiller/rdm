package com.redxun.core.util;

import java.io.*;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.sys.bo.entity.SysBoAttr;
import com.redxun.ui.view.model.ExportFieldColumn;

public class ExcelUtil {
    public static String NO_DEFINE = "no_define";// 未定义的字段
    public static String DEFAULT_DATE_PATTERN = "yyyy年MM月dd日";// 默认日期格式
    public static int DEFAULT_COLOUMN_WIDTH = 17;

    /**
     * 导出Excel 97(.xls)格式 ，少量数据
     * 
     * @param title
     *            标题行
     * @param headMap
     *            属性-列名
     * @param jsonArray
     *            数据集
     * @param datePattern
     *            日期格式，null则用默认日期格式
     * @param colWidth
     *            列宽 默认 至少17个字节
     * @param out
     *            输出流
     */
    public static void exportExcel(String title, Map<String, String> headMap, JSONArray jsonArray, String datePattern,
        int colWidth, OutputStream out) {
        if (datePattern == null)
            datePattern = DEFAULT_DATE_PATTERN;
        // 声明一个工作薄
        HSSFWorkbook workbook = new HSSFWorkbook();
        workbook.createInformationProperties();
        workbook.getDocumentSummaryInformation().setCompany("*****公司");
        SummaryInformation si = workbook.getSummaryInformation();
        si.setAuthor("JACK"); // 填加xls文件作者信息
        si.setApplicationName("导出程序"); // 填加xls文件创建程序信息
        si.setLastAuthor("最后保存者信息"); // 填加xls文件最后保存者信息
        si.setComments("JACK is a programmer!"); // 填加xls文件作者信息
        si.setTitle("POI导出Excel"); // 填加xls文件标题信息
        si.setSubject("POI导出Excel");// 填加文件主题信息
        si.setCreateDateTime(new Date());
        // 表头样式
        HSSFCellStyle titleStyle = workbook.createCellStyle();
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        HSSFFont titleFont = workbook.createFont();
        titleFont.setFontHeightInPoints((short)20);
        titleFont.setBold(true);
        titleStyle.setFont(titleFont);
        // 列头样式
        HSSFCellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        HSSFFont headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short)12);
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        // 单元格样式
        HSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        HSSFFont cellFont = workbook.createFont();
        cellFont.setBold(false);
        cellStyle.setFont(cellFont);
        // 生成一个(带标题)表格
        HSSFSheet sheet = workbook.createSheet();
        // 声明一个画图的顶级管理器
        /*        HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
        // 定义注释的大小和位置,详见文档
        HSSFComment comment = patriarch.createComment(new HSSFClientAnchor(0,
                0, 0, 0, (short) 4, 2, (short) 6, 5));
        // 设置注释内容
        comment.setString(new HSSFRichTextString("可以在POI中添加注释！"));
        // 设置注释作者，当鼠标移动到单元格上是可以在状态栏中看到该内容.
        comment.setAuthor("JACK");*/
        // 设置列宽
        int minBytes = colWidth < DEFAULT_COLOUMN_WIDTH ? DEFAULT_COLOUMN_WIDTH : colWidth;// 至少字节数
        int[] arrColWidth = new int[headMap.size()];
        // 产生表格标题行,以及设置列宽
        String[] properties = new String[headMap.size()];
        String[] headers = new String[headMap.size()];
        int ii = 0;
        for (Iterator<String> iter = headMap.keySet().iterator(); iter.hasNext();) {
            String fieldName = iter.next();

            properties[ii] = fieldName;
            headers[ii] = fieldName;

            int bytes = fieldName.getBytes().length;
            arrColWidth[ii] = bytes < minBytes ? minBytes : bytes;
            sheet.setColumnWidth(ii, arrColWidth[ii] * 256);
            ii++;
        }
        // 遍历集合数据，产生数据行
        int rowIndex = 0;
        for (Object obj : jsonArray) {
            if (rowIndex == 65535 || rowIndex == 0) {
                if (rowIndex != 0)
                    sheet = workbook.createSheet();// 如果数据超过了，则在第二页显示

                HSSFRow titleRow = sheet.createRow(0);// 表头 rowIndex=0
                titleRow.createCell(0).setCellValue(title);
                titleRow.getCell(0).setCellStyle(titleStyle);
                sheet.addMergedRegionUnsafe(new CellRangeAddress(0, 0, 0, headMap.size() - 1));

                HSSFRow headerRow = sheet.createRow(1); // 列头 rowIndex =1
                for (int i = 0; i < headers.length; i++) {
                    headerRow.createCell(i).setCellValue(headers[i]);
                    headerRow.getCell(i).setCellStyle(headerStyle);

                }
                rowIndex = 2;// 数据内容从 rowIndex=2开始
            }
            JSONObject jo = (JSONObject)JSON.toJSON(obj);
            HSSFRow dataRow = sheet.createRow(rowIndex);
            for (int i = 0; i < properties.length; i++) {
                HSSFCell newCell = dataRow.createCell(i);

                Object o = jo.get(properties[i]);
                String cellValue = "";
                if (o == null)
                    cellValue = "";
                else if (o instanceof Date)
                    cellValue = new SimpleDateFormat(datePattern).format(o);
                else
                    cellValue = o.toString();

                newCell.setCellValue(cellValue);
                newCell.setCellStyle(cellStyle);
            }
            rowIndex++;
        }
        // 自动调整宽度
        /*for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }*/
        try {
            workbook.write(out);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 导出Excel 2007 OOXML (.xlsx)格式
     * 
     * @param title
     *            标题行
     * @param headMap
     *            属性-列头
     * @param jsonArray
     *            数据集
     * @param datePattern
     *            日期格式，传null值则默认 年月日
     * @param colWidth
     *            列宽 默认 至少17个字节
     * @param out
     *            输出流
     */
    public static void exportExcelX(String title, List<ExportFieldColumn> headMap, JSONArray jsonArray,
        String datePattern, int colWidth, OutputStream out) {
        if (datePattern == null)
            datePattern = DEFAULT_DATE_PATTERN;
        // 声明一个工作薄
        SXSSFWorkbook workbook = new SXSSFWorkbook(1000);// 缓存
        workbook.setCompressTempFiles(true);
        // 表头样式
        CellStyle titleStyle = workbook.createCellStyle();
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        Font titleFont = workbook.createFont();
        titleFont.setFontHeightInPoints((short)20);
        titleFont.setBold(true);
        titleStyle.setFont(titleFont);
        // 列头样式
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        Font headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short)12);
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        // 单元格样式
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        Font cellFont = workbook.createFont();
        cellFont.setBold(false);
        cellStyle.setFont(cellFont);
        // 生成一个(带标题)表格
        SXSSFSheet sheet = (SXSSFSheet)workbook.createSheet();
        // 设置列宽
        int minBytes = colWidth < DEFAULT_COLOUMN_WIDTH ? DEFAULT_COLOUMN_WIDTH : colWidth;// 至少字节数
        List<ExportFieldColumn> allHeadMap = new ArrayList<ExportFieldColumn>();
        getAllHeadMap(headMap, allHeadMap);
        int[] arrColWidth = new int[allHeadMap.size()];
        // 产生表格标题行,以及设置列宽
        String[] properties = new String[allHeadMap.size()];
        int ii = 0;
        int rowspan = getColumnLength(headMap);
        for (ExportFieldColumn field : allHeadMap) {
            String fieldName = field.getField();

            properties[ii] = fieldName;

            int bytes = fieldName.getBytes().length;
            arrColWidth[ii] = bytes < minBytes ? minBytes : bytes;
            sheet.setColumnWidth(ii, arrColWidth[ii] * 256);
            ii++;
        }
        // 遍历集合数据，产生数据行
        int rowIndex = 0;
        if (rowIndex == 65535 || rowIndex == 0) {
            if (rowIndex != 0)
                sheet = (SXSSFSheet)workbook.createSheet();// 如果数据超过了，则在第二页显示

            SXSSFRow titleRow = (SXSSFRow)sheet.createRow(0);// 表头 rowIndex=0
            titleRow.createCell(0).setCellValue(title);
            titleRow.getCell(0).setCellStyle(titleStyle);
            sheet.addMergedRegionUnsafe(new CellRangeAddress(0, 0, 0, allHeadMap.size() - 1));

            // 先创建表头行，才能进行表格合并
            rowIndex = 1;
            for (int l = 0; l < rowspan; l++) {
                sheet.createRow(l + 1);
                rowIndex += 1;
            }
            // 绘制表头
            drawTitleColumn(sheet, headerStyle, 1, 0, rowspan, headMap);

        }
        for (Object obj : jsonArray) {

            // 绘制数据
            JSONObject jo = (JSONObject)JSON.toJSON(obj);
            SXSSFRow dataRow = (SXSSFRow)sheet.createRow(rowIndex);
            for (int i = 0; i < properties.length; i++) {
                SXSSFCell newCell = (SXSSFCell)dataRow.createCell(i);

                Object o = jo.get(properties[i]);
                String cellValue = "";
                if (o == null)
                    cellValue = "";
                else if (o instanceof Date)
                    cellValue = new SimpleDateFormat(datePattern).format(o);
                else if (o instanceof Float || o instanceof Double)
                    cellValue = new BigDecimal(o.toString()).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
                else
                    cellValue = o.toString();

                newCell.setCellValue(cellValue);
                newCell.setCellStyle(cellStyle);
            }
            rowIndex++;
        }
        // 自动调整宽度
        /*for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }*/
        try {
            workbook.write(out);
            workbook.dispose();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * WEB导出Excel表
     * 
     * @param title
     *            标题
     * @param headMap
     *            导出表的表头
     * @param ja
     *            导出表数据
     * @param response
     */
    public static void downloadExcelFile(String title, List<ExportFieldColumn> headMap, JSONArray ja,
        HttpServletResponse response) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ExcelUtil.exportExcelX(title, headMap, ja, null, 0, os);
            byte[] content = os.toByteArray();
            InputStream is = new ByteArrayInputStream(content);
            // 设置response参数，可以打开下载页面
            response.reset();
            String fileName = title + ".xlsx";
            fileName = URLEncoder.encode(fileName, "UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            response.setContentType("application/vnd.ms-excel");
            response.setContentLength(content.length);

            FileUtil.downLoad(is, response);
        } catch (Exception e) {
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

    public static void downloadImportTemp(String title, List<ExportFieldColumn> headMap, HttpServletResponse response) {
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            JSONArray ja = new JSONArray();
            ExcelUtil.exportExcelX(title, headMap, ja, null, 0, os);
            byte[] content = os.toByteArray();
            InputStream is = new ByteArrayInputStream(content);
            // 设置response参数，可以打开下载页面
            response.reset();

            // response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
            response.setHeader("Content-Disposition",
                "attachment;filename=" + new String((title + "导入模板.xlsx").getBytes(), "iso-8859-1"));
            response.setContentType("application/vnd.ms-excel");
            // response.setHeader("Content-disposition", "attachment;filename=student.xls");
            response.setContentLength(content.length);
            ServletOutputStream outputStream = response.getOutputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            BufferedOutputStream bos = new BufferedOutputStream(outputStream);
            byte[] buff = new byte[8192];
            int bytesRead;
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);

            }
            bis.close();
            bos.close();
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获得该列表头的级数
     * 
     * @param headColumn
     * @return
     */
    private static int getColumnLength2(ExportFieldColumn headColumn) {
        int newl = 1;
        if (headColumn.getChildColumn().size() > 0) {
            List<ExportFieldColumn> headColumns = headColumn.getChildColumn();
            int l1 = getColumnLength(headColumns);
            newl = newl + l1;
        }
        return newl;
    }

    private static int getColumnLength(List<ExportFieldColumn> headColumns) {
        int l = 0;
        for (ExportFieldColumn headColumn : headColumns) {
            int newl = getColumnLength2(headColumn);
            if (newl > l) {
                l = newl;
            }
        }
        return l;
    }

    /**
     * 绘制每个单元格的列表头
     * 
     * @param sheet
     * @param headerStyle
     * @param row
     *            行
     * @param column
     *            列
     * @param rowspan
     *            该列表头的级数
     * @param fieldColumn
     *            该单元格的表头信息
     */
    private static void drawTitleColumn2(SXSSFSheet sheet, CellStyle headerStyle, int row, int column, int rowspan,
        ExportFieldColumn fieldColumn) {
        SXSSFRow titleRow = (SXSSFRow)sheet.getRow(row);
        if (fieldColumn.getColspan() > 1) {
            CellRangeAddress region = new CellRangeAddress(row, row, column, column + fieldColumn.getColspan() - 1);
            setRegionStyle(sheet, region, headerStyle);
            sheet.addMergedRegionUnsafe(region);// 合并列
        }
        if (fieldColumn.getChildColumn().size() > 0) {
            List<ExportFieldColumn> subFieldColumn = fieldColumn.getChildColumn();
            drawTitleColumn(sheet, headerStyle, row + 1, column, rowspan, subFieldColumn);
        } else if (rowspan > row) {
            CellRangeAddress region = new CellRangeAddress(row, rowspan, column, column);
            setRegionStyle(sheet, region, headerStyle);
            sheet.addMergedRegionUnsafe(region);// 合并行
        }
        titleRow.createCell(column).setCellValue(fieldColumn.getHeader());
        titleRow.getCell(column).setCellStyle(headerStyle);
    }

    /**
     * 
     * @param sheet
     * @param headerStyle
     * @param startRow
     *            起始行
     * @param startColumn
     *            起始列
     * @param rowspan
     *            该列表头的级数
     * @param fieldColumns
     *            该行所有单元格的表头信息
     */
    private static void drawTitleColumn(SXSSFSheet sheet, CellStyle headerStyle, int startRow, int startColumn,
        int rowspan, List<ExportFieldColumn> fieldColumns) {
        int sn = 0;
        for (ExportFieldColumn fieldColumn : fieldColumns) {
            drawTitleColumn2(sheet, headerStyle, startRow, startColumn + sn, rowspan, fieldColumn);
            sn += fieldColumn.getColspan();
        }
    }

    /**
     * 获得需要显示的数据表头
     * 
     * @param headMaps
     *            第一级的表头
     * @param allHeadMap
     *            最底层要需要显示数据的表头
     */

    private static void getAllHeadMap(List<ExportFieldColumn> headMaps, List<ExportFieldColumn> allHeadMap) {
        for (ExportFieldColumn headMap : headMaps) {
            if (headMap.getChildColumn().size() > 0) {
                getAllHeadMap(headMap.getChildColumn(), allHeadMap);
            } else {
                allHeadMap.add(headMap);
            }
        }
    }

    /**
     * 设置xlsx的合并单元格样式
     * 
     * @param sheet
     * @param region
     * @param headerStyle
     */
    public static void setRegionStyle(SXSSFSheet sheet, CellRangeAddress region, CellStyle headerStyle) {

        for (int i = region.getFirstRow(); i <= region.getLastRow(); i++) {

            SXSSFRow row = (SXSSFRow)sheet.getRow(i);
            if (row == null)
                row = (SXSSFRow)sheet.createRow(i);
            for (int j = region.getFirstColumn(); j <= region.getLastColumn(); j++) {
                SXSSFCell cell = (SXSSFCell)row.getCell(j);
                if (cell == null) {
                    cell = (SXSSFCell)row.createCell(j);
                    cell.setCellValue("");
                }
                cell.setCellStyle(headerStyle);
            }
        }
    }

    public static List<Map<String, Object>> importExcel(List<SysBoAttr> headers, MultipartFile mFile) {
        String fileName = mFile.getOriginalFilename();// 获取文件名
        if (!validateExcel(fileName)) {// 验证文件名是否合格
            return null;
        }
        boolean isXls = getExcelInfo(fileName);
        try {
            List<Map<String, Object>> dataList = createExcel(headers, mFile.getInputStream(), isXls);
            return dataList;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static List<Map<String, Object>> createExcel(List<SysBoAttr> headers, InputStream is, boolean isXls) {
        List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
        try {
            Workbook wb = null;
            if (isXls) {// 当excel是2003时,创建excel2003
                wb = new HSSFWorkbook(is);
            } else {// 当excel是2007时,创建excel2007
                wb = new XSSFWorkbook(is);
            }
            dataList = readExcelValue(wb, headers, is, isXls);// 读取Excel里面客户的信息
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataList;
    }

    public static List<Map<String, Object>> readExcelValue(Workbook wb, List<SysBoAttr> headers, InputStream is,
        boolean isXls) {
        List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
        Map<Integer, SysBoAttr> titleMap = new HashMap<Integer, SysBoAttr>();
        // 得到第一个shell
        Sheet sheet = wb.getSheetAt(0);
        // 得到Excel的行数
        int totalRows = sheet.getPhysicalNumberOfRows();
        // 得到Excel的列数(前提是有行数)
        int totalCells = 0;
        if (totalRows > 1 && sheet.getRow(0) != null) {
            totalCells = sheet.getRow(1).getPhysicalNumberOfCells();
        }
        for (int r = 1; r < totalRows; r++) {
            Row row = sheet.getRow(r);
            if (row == null) {
                continue;
            }
            Map<String, Object> map = new HashMap<String, Object>();
            // 循环Excel的列
            for (int c = 0; c < totalCells; c++) {
                Cell cell = row.getCell(c);
                if (null != cell) {
                    if (r == 1) {
                        String name = cell.getStringCellValue();
                        for (SysBoAttr header : headers) {
                            if (header.getComment().equals(name)) {
                                titleMap.put(c, header);
                            }
                        }
                    } else {
                        // 如果是纯数字,比如你写的是25,cell.getNumericCellValue()获得是25.0,通过截取字符串去掉.0获得25
                        SysBoAttr title = titleMap.get(c);
                        if (title != null) {
/*                            if ("string".equals(title.getDataType())) {
                                cell.setCellType(CellType.STRING);
                            }*/
                            String value = getValueByCellType(cell);
                            String titleName = title.getFieldName();
                            if ("date".equals(title.getDataType())) {
                                String formStr = getCnDateStr(value);
                                Date d = DateUtil.parseDate(value, formStr);
                                map.put(titleName, d);
                            } else {
                                map.put(titleName, value);
                            }
                        }
                    }
                }
            }
            // 添加到list
            if (r != 1) {
                dataList.add(map);
            }
        }
        return dataList;
    }

    private static String getValueByCellType(Cell cell) {
        CellType cellType = cell.getCellType();
        String val = "";
        switch (cellType) {
            case STRING:
                val = cell.getStringCellValue();
                break;
            case FORMULA:
                try {
                    val = cell.getStringCellValue();
                } catch (IllegalStateException e) {
                    val = String.valueOf(cell.getNumericCellValue());
                }
                break;
            case BOOLEAN:
                Boolean val1 = cell.getBooleanCellValue();
                val = val1.toString();
                break;
            case NUMERIC:
                if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
                    Date theDate = cell.getDateCellValue();
                    SimpleDateFormat dff = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    val = dff.format(theDate);
                } else {
                    DecimalFormat df = new DecimalFormat("0");
                    val = df.format(cell.getNumericCellValue());
                }
                break;
            case BLANK:
                break;
            default:
        }

        return val;
    }

    public static boolean getExcelInfo(String fileName) {
        boolean isXls = true;// 根据文件名判断文件是2003版本还是2007版本
        if (isExcel2007(fileName)) {
            isXls = false;
        }
        return isXls;
    }

    /**
     * 验证EXCEL文件
     * 
     * @param filePath
     * @return
     */
    public static boolean validateExcel(String filePath) {
        if (filePath == null || !(isExcel2003(filePath) || isExcel2007(filePath))) {
            return false;
        }
        return true;
    }

    // @描述：是否是2003的excel，返回true是2003
    public static boolean isExcel2003(String filePath) {
        return filePath.matches("^.+\\.(?i)(xls)$");
    }

    // @描述：是否是2007的excel，返回true是2007
    public static boolean isExcel2007(String filePath) {
        return filePath.matches("^.+\\.(?i)(xlsx)$");
    }

    public static String getCnDateStr(String value) {
        if (value.matches(
            "^((([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29))\\s+([0-1]?[0-9]|2[0-3])-([0-5][0-9])-([0-5][0-9])$")) {
            return "yyyy-MM-dd HH-mm-ss";
        } else if (value.matches(
            "^((([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29))\\s+([0-1]?[0-9]|2[0-3])-([0-5][0-9])-([0-5][0-9])$")) {
            return "yyyy年MM月dd日 HH时mm分ss秒";
        } else if (value.matches("^\\d{4}(\\-)\\d{1,2}(\\-)\\d{1,2}$")) {
            return "yyyy-MM-dd";
        } else if (value.matches("^\\d{4}(年)\\d{1,2}(月)\\d{1,2}日$")) {
            return "yyyy年MM月dd日";
        }
        return "";
    }

    public static String getCellFormatValue(Cell cell) {
        if (cell == null)
            return "";
        String cellvalue = "";
        switch (cell.getCellType()) {
            case STRING:
                cellvalue = cell.getRichStringCellValue().getString();
                break;
            case NUMERIC:
            case FORMULA:
                if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
                    Date date = cell.getDateCellValue();
                    cellvalue = DateUtil.formatDate(date);
                } else {
                    cellvalue = String.valueOf(cell.getNumericCellValue());
                }
                break;
            case BOOLEAN:
                cellvalue = String.valueOf(cell.getBooleanCellValue());
                break;
            case BLANK:
            default:
                cellvalue = "";
        }
        return cellvalue;
    }

    /**
     * 下载EXCEL模板
     * 
     * @param workBook
     * @param fileName
     * @param response
     * @throws IOException
     */
    public static void downloadExcel(HSSFWorkbook workBook, String fileName, HttpServletResponse response)
        throws IOException {
        response.setContentType("application/x-download");
        if (System.getProperty("file.encoding").equals("GBK")) {
            response.setHeader("Content-Disposition",
                "attachment;filename=\"" + new String(fileName.getBytes(), "ISO-8859-1") + ".xls" + "\"");
        } else {
            response.setHeader("Content-Disposition",
                "attachment;filename=\"" + URLEncoder.encode(fileName, "utf-8") + ".xls" + "\"");
        }
        OutputStream os = null;
        try {
            os = response.getOutputStream();
            workBook.write(os);
            os.flush();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                os.close();
            }
        }
    }

    /**
     * 设置列默认样式
     * 
     * @param wb
     * @param sheet
     * @param column
     * @param width
     * @param format
     * @return
     */
    public static HSSFSheet setDefaultColumnStyle(HSSFWorkbook wb, HSSFSheet sheet, int column, int width,
        String format) {
        CellStyle cellStyle = wb.createCellStyle();
        DataFormat dataFormat = wb.createDataFormat();
        cellStyle.setDataFormat(dataFormat.getFormat(format));
        sheet.setDefaultColumnStyle(column, cellStyle);
        sheet.setColumnWidth(column, width);
        return sheet;
    }

    /**
     * 设置表格样式 不带下拉框
     * 
     * @param wb
     * @param sheet
     * @param rowNum
     * @param bold
     * @param color
     * @param list
     * @return
     */
    public static HSSFRow setValueStyle(HSSFWorkbook wb, HSSFSheet sheet, int rowNum, boolean bold, int color,
        Object[] list) {
        HSSFRow row = sheet.createRow(rowNum);
        for (int i = 0; i < list.length; i++) {
            HSSFCell cell = row.createCell(i);
            cell.setCellValue((String)list[i]);// 设置值
            HSSFCellStyle style = cell.getCellStyle();
            style.setFillBackgroundColor((short)color);
            style.getFont(wb).setBold(bold);// 设置字体(粗体700/普通400)
        }
        return row;
    }

    /**
     * 设置表格样式 带下拉框
     * 
     * @param sheet
     * @param col
     * @param rowNum
     * @param width
     * @param values
     */
    public static void values(HSSFSheet sheet, int col, int rowNum, int width, Object[] values) {
        setSelectValue(sheet, values, rowNum, col);
        // value(sheet, col, rowNum, width, values);
    }

    /**
     * 设置值
     * 
     * @param sheet
     * @param col
     * @param row
     * @param width
     * @param values
     */
    public static void value(HSSFSheet sheet, int col, int row, int width, Object[] values) {
        HSSFRow rowH = sheet.getRow(row);
        if (rowH == null) {
            rowH = sheet.createRow(row);
        }
        HSSFCell cell = rowH.createCell(col);
        setCellValueArray(cell, values);
        sheet.setColumnWidth(col, width);
    }

    /**
     * 批量设置值
     * 
     * @param sheet
     * @param row
     * @param values
     */
    public static void value(HSSFSheet sheet, int row, Object[] values) {
        for (int i = 0; i < values.length; i++) {
            HSSFCell cell = sheet.createRow(row).createCell(i);
            Object value = values[i];
            setCellValue(cell, value);
        }
    }

    /**
     * 设置下拉框
     * 
     * @param sheet
     * @param values
     * @param row
     * @param col
     */
    private static void setSelectValue(HSSFSheet sheet, Object[] values, int row, int col) {
        DataValidationHelper helper = sheet.getDataValidationHelper();

        // CellRangeAddressList(firstRow, lastRow, firstCol, lastCol)设置行列范围
        CellRangeAddressList addressList = new CellRangeAddressList(row, row, col, col);

        // 设置下拉框数据
        String[] pos = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            pos[i] = (String)values[i];
        }
        DataValidationConstraint constraint = helper.createExplicitListConstraint(pos);
        DataValidation dataValidation = helper.createValidation(constraint, addressList);
        // 处理Excel兼容性问题
        if (dataValidation instanceof XSSFDataValidation) {
            dataValidation.setSuppressDropDownArrow(true);
            dataValidation.setShowErrorBox(true);
        } else {
            dataValidation.setSuppressDropDownArrow(false);
        }

        sheet.addValidationData(dataValidation);
    }

    private static void setCellValueArray(HSSFCell cell, Object[] values) {
        if (values != null) {
            for (int i = 0; i < values.length; i++) {
                setCellValue(cell, values[i]);
            }
        }
    }

    private static void setCellValue(HSSFCell cell, Object value) {
        if (value == null) {
            return;
        }
        if (((value instanceof Double)) || ((value instanceof Float)) || ((value instanceof Long))
            || ((value instanceof Integer)) || ((value instanceof Short)) || ((value instanceof BigDecimal))
            || ((value instanceof Byte))) {
            cell.setCellValue(Double.valueOf(value.toString()).doubleValue());
            cell.setCellType(CellType.NUMERIC);
        } else if ((value instanceof Boolean)) {
            cell.setCellValue(((Boolean)value).booleanValue());
            cell.setCellType(CellType.BOOLEAN);
        } else if ((value != null) && (value.toString().startsWith("="))) {
            cell.setCellFormula(value.toString().substring(1));
            cell.setCellType(CellType.FORMULA);
        } else if ((value instanceof Date)) {
            cell.setCellValue((Date)value);
        } else {
            cell.setCellValue(new HSSFRichTextString(value == null ? "" : value.toString()));

            // cell.setCellType(1);
        }
    }
}
