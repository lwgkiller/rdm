package com.redxun.rdmZhgl.core.job;

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

public class NjjdExcelUtils {
    private static Logger logger = LoggerFactory.getLogger(NjjdExcelUtils.class);

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
    public static <T> HSSFWorkbook exportExcelWB(List<T> dataList,List<T> data1List,
        String[] fieldNames, String[] fieldCodes, String title) {
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
        if (dataList.size() >0){
            String json =JSON.toJSONString(dataList.get(0));
            JSONObject temp = JSON.parseObject(json);
            for (int i =0; i < temp.size();i++){
                HSSFRow rows = sheet.createRow(i + 2);
                if(rows.getRowNum()==2){
                    for (int j = 0; j < fieldNames.length; j++){
                        cell = rows.createCell(0);
                        cell.setCellValue("整机参数");
                        cell = rows.createCell(1);
                        cell.setCellValue("型号");
                        cell = rows.createCell(2);
                        cell.setCellValue("型号");
                        cell = rows.createCell(3);
                        cell.setCellValue("/");
                        cell = rows.createCell(4);
                        cell.setCellValue(temp.getString("ztXh"));

                    }
                }else if(rows.getRowNum()==3){
                    for (int j = 0; j < fieldNames.length; j++){
                        cell = rows.createCell(0);
                        cell.setCellValue("");
                        cell = rows.createCell(1);
                        cell.setCellValue("结构型式");
                        cell = rows.createCell(2);
                        cell.setCellValue("");
                        cell = rows.createCell(3);
                        cell.setCellValue("/");
                        cell = rows.createCell(4);
                        cell.setCellValue(temp.getString("jgxsName"));
                    }
                }else if (rows.getRowNum()==4){
                    for (int j = 0; j < fieldNames.length; j++){
                        cell = rows.createCell(0);
                        cell.setCellValue("");
                        cell = rows.createCell(1);
                        cell.setCellValue("工程质量");
                        cell = rows.createCell(2);
                        cell.setCellValue("");
                        cell = rows.createCell(3);
                        cell.setCellValue("kg");
                        cell = rows.createCell(4);
                        cell.setCellValue(temp.getString("ztGzzl"));
                    }
                }else if (rows.getRowNum()==5){
                    for (int j = 0; j < fieldNames.length; j++){
                        cell = rows.createCell(0);
                        cell.setCellValue("");
                        cell = rows.createCell(1);
                        cell.setCellValue("铲斗容量");
                        cell = rows.createCell(2);
                        cell.setCellValue("");
                        cell = rows.createCell(3);
                        cell.setCellValue("m3");
                        cell = rows.createCell(4);
                        cell.setCellValue(temp.getString("ztCdrl"));
                    }
                }else if (rows.getRowNum()==6){
                    for (int j = 0; j < fieldNames.length; j++){
                        cell = rows.createCell(0);
                        cell.setCellValue("");
                        cell = rows.createCell(1);
                        cell.setCellValue("外形尺寸(长x宽x高)");
                        cell = rows.createCell(2);
                        cell.setCellValue("运输状态");
                        cell = rows.createCell(3);
                        cell.setCellValue("mm");
                        cell = rows.createCell(4);
                        cell.setCellValue(temp.getString("ztWxcc"));
                    }
                }else if (rows.getRowNum()==7){
                    for (int j = 0; j < fieldNames.length; j++){
                        cell = rows.createCell(0);
                        cell.setCellValue("");
                        cell = rows.createCell(1);
                        cell.setCellValue("轴距");
                        cell = rows.createCell(2);
                        cell.setCellValue("");
                        cell = rows.createCell(3);
                        cell.setCellValue("mm");
                        cell = rows.createCell(4);
                        cell.setCellValue(temp.getString("ztZj"));
                    }
                }else if (rows.getRowNum()==8){
                    for (int j = 0; j < fieldNames.length; j++){
                        cell = rows.createCell(0);
                        cell.setCellValue("");
                        cell = rows.createCell(1);
                        cell.setCellValue("轮式");
                        cell = rows.createCell(2);
                        cell.setCellValue("轮距(前/后)");
                        cell = rows.createCell(3);
                        cell.setCellValue("mm");
                        cell = rows.createCell(4);
                        cell.setCellValue(temp.getString("ztLsLj"));
                    }
                }else if (rows.getRowNum()==9){
                    for (int j = 0; j < fieldNames.length; j++){
                        cell = rows.createCell(0);
                        cell.setCellValue("");
                        cell = rows.createCell(1);
                        cell.setCellValue("");
                        cell = rows.createCell(2);
                        cell.setCellValue("轮胎规格");
                        cell = rows.createCell(3);
                        cell.setCellValue("mm");
                        cell = rows.createCell(4);
                        cell.setCellValue(temp.getString("ztLsLtgg"));
                    }
                }else if (rows.getRowNum()==10){
                    for (int j = 0; j < fieldNames.length; j++){
                        cell = rows.createCell(0);
                        cell.setCellValue("");
                        cell = rows.createCell(1);
                        cell.setCellValue("");
                        cell = rows.createCell(2);
                        cell.setCellValue("轮胎气压");
                        cell = rows.createCell(3);
                        cell.setCellValue("mm");
                        cell = rows.createCell(4);
                        cell.setCellValue(temp.getString("ztLsLtQy"));
                    }
                }else if (rows.getRowNum()==11){
                    for (int j = 0; j < fieldNames.length; j++){
                        cell = rows.createCell(0);
                        cell.setCellValue("");
                        cell = rows.createCell(1);
                        cell.setCellValue("履带式");
                        cell = rows.createCell(2);
                        cell.setCellValue("履带轴距");
                        cell = rows.createCell(3);
                        cell.setCellValue("mm");
                        cell = rows.createCell(4);
                        cell.setCellValue(temp.getString("ztLdGj"));
                    }
                } else if (rows.getRowNum()==12){
                    for (int j = 0; j < fieldNames.length; j++){
                        cell = rows.createCell(0);
                        cell.setCellValue("");
                        cell = rows.createCell(1);
                        cell.setCellValue("");
                        cell = rows.createCell(2);
                        cell.setCellValue("履带接地长度");
                        cell = rows.createCell(3);
                        cell.setCellValue("mm");
                        cell = rows.createCell(4);
                        cell.setCellValue(temp.getString("ztLdCd"));
                    }
                }else if (rows.getRowNum()==13){
                    for (int j = 0; j < fieldNames.length; j++){
                        cell = rows.createCell(0);
                        cell.setCellValue("");
                        cell = rows.createCell(1);
                        cell.setCellValue("");
                        cell = rows.createCell(2);
                        cell.setCellValue("履带板宽度");
                        cell = rows.createCell(3);
                        cell.setCellValue("mm");
                        cell = rows.createCell(4);
                        cell.setCellValue(temp.getString("ztLdKd"));
                    }
                }else if (rows.getRowNum()==14){
                    for (int j = 0; j < fieldNames.length; j++){
                        cell = rows.createCell(0);
                        cell.setCellValue("");
                        cell = rows.createCell(1);
                        cell.setCellValue("");
                        cell = rows.createCell(2);
                        cell.setCellValue("履带材质");
                        cell = rows.createCell(3);
                        cell.setCellValue("mm");
                        cell = rows.createCell(4);
                        cell.setCellValue(temp.getString("ldczName"));
                    }
                }else if (rows.getRowNum()==15){
                    for (int j = 0; j < fieldNames.length; j++){
                        cell = rows.createCell(0);
                        cell.setCellValue("");
                        cell = rows.createCell(1);
                        cell.setCellValue("后端回转半径");
                        cell = rows.createCell(2);
                        cell.setCellValue("");
                        cell = rows.createCell(3);
                        cell.setCellValue("mm");
                        cell = rows.createCell(4);
                        cell.setCellValue(temp.getString("ztHdhzBj"));
                    }
                }else if (rows.getRowNum()==16){
                    for (int j = 0; j < fieldNames.length; j++){
                        cell = rows.createCell(0);
                        cell.setCellValue("");
                        cell = rows.createCell(1);
                        cell.setCellValue("离地间隙");
                        cell = rows.createCell(2);
                        cell.setCellValue("");
                        cell = rows.createCell(3);
                        cell.setCellValue("mm");
                        cell = rows.createCell(4);
                        cell.setCellValue(temp.getString("ztLdJx"));
                    }
                }else if (rows.getRowNum()==17){
                    for (int j = 0; j < fieldNames.length; j++){
                        cell = rows.createCell(0);
                        cell.setCellValue("");
                        cell = rows.createCell(1);
                        cell.setCellValue("转台离地高度");
                        cell = rows.createCell(2);
                        cell.setCellValue("");
                        cell = rows.createCell(3);
                        cell.setCellValue("mm");
                        cell = rows.createCell(4);
                        cell.setCellValue(temp.getString("ztZtldGd"));
                    }
                }else if (rows.getRowNum()==18){
                    for (int j = 0; j < fieldNames.length; j++){
                        cell = rows.createCell(0);
                        cell.setCellValue("");
                        cell = rows.createCell(1);
                        cell.setCellValue("转台总宽度");
                        cell = rows.createCell(2);
                        cell.setCellValue("");
                        cell = rows.createCell(3);
                        cell.setCellValue("mm");
                        cell = rows.createCell(4);
                        cell.setCellValue(temp.getString("ztZtKd"));
                    }
                }else if (rows.getRowNum()==19){
                    for (int j = 0; j < fieldNames.length; j++){
                        cell = rows.createCell(0);
                        cell.setCellValue("");
                        cell = rows.createCell(1);
                        cell.setCellValue("转台尾端长度");
                        cell = rows.createCell(2);
                        cell.setCellValue("");
                        cell = rows.createCell(3);
                        cell.setCellValue("mm");
                        cell = rows.createCell(4);
                        cell.setCellValue(temp.getString("ztZtwdCd"));
                    }
                }else if (rows.getRowNum()==20){
                    for (int j = 0; j < fieldNames.length; j++){
                        cell = rows.createCell(0);
                        cell.setCellValue("");
                        cell = rows.createCell(1);
                        cell.setCellValue("回转中心至驱动轮中心距离");
                        cell = rows.createCell(2);
                        cell.setCellValue("");
                        cell = rows.createCell(3);
                        cell.setCellValue("mm");
                        cell = rows.createCell(4);
                        cell.setCellValue(temp.getString("ztZxj"));
                    }
                }else if (rows.getRowNum()==21){
                    for (int j = 0; j < fieldNames.length; j++){
                        cell = rows.createCell(0);
                        cell.setCellValue("");
                        cell = rows.createCell(1);
                        cell.setCellValue("平均接地比压");
                        cell = rows.createCell(2);
                        cell.setCellValue("");
                        cell = rows.createCell(3);
                        cell.setCellValue("kPa");
                        cell = rows.createCell(4);
                        cell.setCellValue(temp.getString("ztJdby"));
                    }
                }else if (rows.getRowNum()==22){
                    for (int j = 0; j < fieldNames.length; j++){
                        cell = rows.createCell(0);
                        cell.setCellValue("");
                        cell = rows.createCell(1);
                        cell.setCellValue("档位数（前进/倒退）");
                        cell = rows.createCell(2);
                        cell.setCellValue("");
                        cell = rows.createCell(3);
                        cell.setCellValue("/");
                        cell = rows.createCell(4);
                        cell.setCellValue(temp.getString("ztDws"));
                    }
                }else if (rows.getRowNum()==23){
                    for (int j = 0; j < fieldNames.length; j++){
                        cell = rows.createCell(0);
                        cell.setCellValue("");
                        cell = rows.createCell(1);
                        cell.setCellValue("理论速度");
                        cell = rows.createCell(2);
                        cell.setCellValue("前进");
                        cell = rows.createCell(3);
                        cell.setCellValue("km/h");
                        cell = rows.createCell(4);
                        cell.setCellValue(temp.getString("ztSdQj"));
                    }
                }else if (rows.getRowNum()==24){
                    for (int j = 0; j < fieldNames.length; j++){
                        cell = rows.createCell(0);
                        cell.setCellValue("");
                        cell = rows.createCell(1);
                        cell.setCellValue("");
                        cell = rows.createCell(2);
                        cell.setCellValue("倒退");
                        cell = rows.createCell(3);
                        cell.setCellValue("km/h");
                        cell = rows.createCell(4);
                        cell.setCellValue(temp.getString("ztSdDt"));
                    }
                }else if (rows.getRowNum()==25){
                    for (int j = 0; j < fieldNames.length; j++){
                        cell = rows.createCell(0);
                        cell.setCellValue("");
                        cell = rows.createCell(1);
                        cell.setCellValue("爬坡能力");
                        cell = rows.createCell(2);
                        cell.setCellValue("");
                        cell = rows.createCell(3);
                        cell.setCellValue("/");
                        cell = rows.createCell(4);
                        cell.setCellValue(temp.getString("ztPp"));
                    }
                }else if (rows.getRowNum()==26){
                    for (int j = 0; j < fieldNames.length; j++){
                        cell = rows.createCell(0);
                        cell.setCellValue("发动机");
                        cell = rows.createCell(1);
                        cell.setCellValue("型号");
                        cell = rows.createCell(2);
                        cell.setCellValue("");
                        cell = rows.createCell(3);
                        cell.setCellValue("/");
                        cell = rows.createCell(4);
                        cell.setCellValue(temp.getString("fdjXh"));
                    }
                }else if (rows.getRowNum()==27){
                    for (int j = 0; j < fieldNames.length; j++){
                        cell = rows.createCell(0);
                        cell.setCellValue("");
                        cell = rows.createCell(1);
                        cell.setCellValue("型式");
                        cell = rows.createCell(2);
                        cell.setCellValue("");
                        cell = rows.createCell(3);
                        cell.setCellValue("/");
                        cell = rows.createCell(4);
                        cell.setCellValue(temp.getString("fdjXs"));
                    }
                }else if (rows.getRowNum()==28){
                    for (int j = 0; j < fieldNames.length; j++){
                        cell = rows.createCell(0);
                        cell.setCellValue("");
                        cell = rows.createCell(1);
                        cell.setCellValue("生产厂");
                        cell = rows.createCell(2);
                        cell.setCellValue("");
                        cell = rows.createCell(3);
                        cell.setCellValue("/");
                        cell = rows.createCell(4);
                        cell.setCellValue(temp.getString("fdjFactory"));
                    }
                }else if (rows.getRowNum()==29){
                    for (int j = 0; j < fieldNames.length; j++){
                        cell = rows.createCell(0);
                        cell.setCellValue("");
                        cell = rows.createCell(1);
                        cell.setCellValue("标定功率");
                        cell = rows.createCell(2);
                        cell.setCellValue("");
                        cell = rows.createCell(3);
                        cell.setCellValue("kW");
                        cell = rows.createCell(4);
                        cell.setCellValue(temp.getString("fdjBdGl"));
                    }
                }else if (rows.getRowNum()==30){
                    for (int j = 0; j < fieldNames.length; j++){
                        cell = rows.createCell(0);
                        cell.setCellValue("");
                        cell = rows.createCell(1);
                        cell.setCellValue("额定净功率");
                        cell = rows.createCell(2);
                        cell.setCellValue("");
                        cell = rows.createCell(3);
                        cell.setCellValue("kW");
                        cell = rows.createCell(4);
                        cell.setCellValue(temp.getString("fdjEdGl"));
                    }
                }else if (rows.getRowNum()==31){
                    for (int j = 0; j < fieldNames.length; j++){
                        cell = rows.createCell(0);
                        cell.setCellValue("");
                        cell = rows.createCell(1);
                        cell.setCellValue("标定转速");
                        cell = rows.createCell(2);
                        cell.setCellValue("");
                        cell = rows.createCell(3);
                        cell.setCellValue("r/min");
                        cell = rows.createCell(4);
                        cell.setCellValue(temp.getString("fdjBdZs"));
                    }
                }else if (rows.getRowNum()==32){
                    for (int j = 0; j < fieldNames.length; j++){
                        cell = rows.createCell(0);
                        cell.setCellValue("驾驶室");
                        cell = rows.createCell(1);
                        cell.setCellValue("型号");
                        cell = rows.createCell(2);
                        cell.setCellValue("");
                        cell = rows.createCell(3);
                        cell.setCellValue("/");
                        cell = rows.createCell(4);
                        cell.setCellValue(temp.getString("jssXh"));
                    }
                }else if (rows.getRowNum()==33){
                    for (int j = 0; j < fieldNames.length; j++){
                        cell = rows.createCell(0);
                        cell.setCellValue("");
                        cell = rows.createCell(1);
                        cell.setCellValue("型式");
                        cell = rows.createCell(2);
                        cell.setCellValue("");
                        cell = rows.createCell(3);
                        cell.setCellValue("/");
                        cell = rows.createCell(4);
                        cell.setCellValue(temp.getString("jsxsName"));
                    }
                }else if (rows.getRowNum()==34){
                    for (int j = 0; j < fieldNames.length; j++){
                        cell = rows.createCell(0);
                        cell.setCellValue("");
                        cell = rows.createCell(1);
                        cell.setCellValue("生产厂");
                        cell = rows.createCell(2);
                        cell.setCellValue("");
                        cell = rows.createCell(3);
                        cell.setCellValue("/");
                        cell = rows.createCell(4);
                        cell.setCellValue(temp.getString("jssFactory"));
                    }
                }else if (rows.getRowNum()==35){
                    for (int j = 0; j < fieldNames.length; j++){
                        cell = rows.createCell(0);
                        cell.setCellValue("作业参数");
                        cell = rows.createCell(1);
                        cell.setCellValue("最大挖掘力");
                        cell = rows.createCell(2);
                        cell.setCellValue("铲斗");
                        cell = rows.createCell(3);
                        cell.setCellValue("kN");
                        cell = rows.createCell(4);
                        cell.setCellValue(temp.getString("zyccZdWjlCd"));
                    }
                }else if (rows.getRowNum()==36){
                    for (int j = 0; j < fieldNames.length; j++){
                        cell = rows.createCell(0);
                        cell.setCellValue("");
                        cell = rows.createCell(1);
                        cell.setCellValue("");
                        cell = rows.createCell(2);
                        cell.setCellValue("斗杆");
                        cell = rows.createCell(3);
                        cell.setCellValue("kN");
                        cell = rows.createCell(4);
                        cell.setCellValue(temp.getString("zycsZdwjlDg"));
                    }
                }else if (rows.getRowNum()==37){
                    for (int j = 0; j < fieldNames.length; j++){
                        cell = rows.createCell(0);
                        cell.setCellValue("");
                        cell = rows.createCell(1);
                        cell.setCellValue("最大挖掘半径");
                        cell = rows.createCell(2);
                        cell.setCellValue("");
                        cell = rows.createCell(3);
                        cell.setCellValue("mm");
                        cell = rows.createCell(4);
                        cell.setCellValue(temp.getString("zycsZdwjBj"));
                    }
                }else if (rows.getRowNum()==38){
                    for (int j = 0; j < fieldNames.length; j++){
                        cell = rows.createCell(0);
                        cell.setCellValue("");
                        cell = rows.createCell(1);
                        cell.setCellValue("最大挖掘深度");
                        cell = rows.createCell(2);
                        cell.setCellValue("");
                        cell = rows.createCell(3);
                        cell.setCellValue("mm");
                        cell = rows.createCell(4);
                        cell.setCellValue(temp.getString("zycsZdwjSd"));
                    }
                }else if (rows.getRowNum()==39){
                    for (int j = 0; j < fieldNames.length; j++){
                        cell = rows.createCell(0);
                        cell.setCellValue("");
                        cell = rows.createCell(1);
                        cell.setCellValue("最大垂直挖掘深度");
                        cell = rows.createCell(2);
                        cell.setCellValue("");
                        cell = rows.createCell(3);
                        cell.setCellValue("mm");
                        cell = rows.createCell(4);
                        cell.setCellValue(temp.getString("zycsZdCzwjSd"));
                    }
                }else if (rows.getRowNum()==40){
                    for (int j = 0; j < fieldNames.length; j++){
                        cell = rows.createCell(0);
                        cell.setCellValue("");
                        cell = rows.createCell(1);
                        cell.setCellValue("最大挖掘高度");
                        cell = rows.createCell(2);
                        cell.setCellValue("");
                        cell = rows.createCell(3);
                        cell.setCellValue("mm");
                        cell = rows.createCell(4);
                        cell.setCellValue(temp.getString("zycsZdwjGd"));
                    }
                }else if (rows.getRowNum()==41){
                    for (int j = 0; j < fieldNames.length; j++){
                        cell = rows.createCell(0);
                        cell.setCellValue("");
                        cell = rows.createCell(1);
                        cell.setCellValue("最大卸载高度");
                        cell = rows.createCell(2);
                        cell.setCellValue("");
                        cell = rows.createCell(3);
                        cell.setCellValue("mm");
                        cell = rows.createCell(4);
                        cell.setCellValue(temp.getString("zycsZdxzGd"));
                    }
                }else if (rows.getRowNum()==42){
                    for (int j = 0; j < fieldNames.length; j++){
                        cell = rows.createCell(0);
                        cell.setCellValue("工作装置");
                        cell = rows.createCell(1);
                        cell.setCellValue("动臂长度");
                        cell = rows.createCell(2);
                        cell.setCellValue("");
                        cell = rows.createCell(3);
                        cell.setCellValue("mm");
                        cell = rows.createCell(4);
                        cell.setCellValue(temp.getString("gzzzDbCd"));
                    }
                }else if (rows.getRowNum()==43){
                    for (int j = 0; j < fieldNames.length; j++){
                        cell = rows.createCell(0);
                        cell.setCellValue("");
                        cell = rows.createCell(1);
                        cell.setCellValue("斗杆长度");
                        cell = rows.createCell(2);
                        cell.setCellValue("");
                        cell = rows.createCell(3);
                        cell.setCellValue("mm");
                        cell = rows.createCell(4);
                        cell.setCellValue(temp.getString("gzzzDgCd"));
                    }
                }else if (rows.getRowNum()==44){
                    for (int j = 0; j < fieldNames.length; j++){
                        cell = rows.createCell(0);
                        cell.setCellValue("液压系统");
                        cell = rows.createCell(1);
                        cell.setCellValue("主液压泵型号");
                        cell = rows.createCell(2);
                        cell.setCellValue("");
                        cell = rows.createCell(3);
                        cell.setCellValue("/");
                        cell = rows.createCell(4);
                        cell.setCellValue(temp.getString("yybXh"));
                    }
                }else if (rows.getRowNum()==45){
                    for (int j = 0; j < fieldNames.length; j++){
                        cell = rows.createCell(0);
                        cell.setCellValue("");
                        cell = rows.createCell(1);
                        cell.setCellValue("主液压泵流量");
                        cell = rows.createCell(2);
                        cell.setCellValue("");
                        cell = rows.createCell(3);
                        cell.setCellValue("L/min");
                        cell = rows.createCell(4);
                        cell.setCellValue(temp.getString("yybLl"));
                    }
                }else if (rows.getRowNum()==46){
                    for (int j = 0; j < fieldNames.length; j++){
                        cell = rows.createCell(0);
                        cell.setCellValue("");
                        cell = rows.createCell(1);
                        cell.setCellValue("设定工作压力");
                        cell = rows.createCell(2);
                        cell.setCellValue("");
                        cell = rows.createCell(3);
                        cell.setCellValue("MPa");
                        cell = rows.createCell(4);
                        cell.setCellValue(temp.getString("gzyl"));
                    }
                }
            }
        }
        if (data1List.size() >0){
            for (int i=0; i<data1List.size(); i++){
                String json =JSON.toJSONString(data1List.get(i));
                JSONObject temp = JSON.parseObject(json);
                HSSFRow rows = sheet.createRow(i + 47);
                if(rows.getRowNum()>=47){
                    for (int j = 0; j < fieldNames.length; j++){
                        cell = rows.createCell(0);
                        cell.setCellValue("选装配置");
                        cell = rows.createCell(1);
                        cell.setCellValue(temp.getString("pzmc"));
                        cell = rows.createCell(2);
                        cell.setCellValue("");
                        cell = rows.createCell(3);
                        cell.setCellValue(temp.getString("pzdw"));
                        cell = rows.createCell(4);
                        cell.setCellValue(temp.getString("pzsjz"));
                    }
                }

            }
        }


        // 合并第一行表头 单元格
        CellRangeAddress region = new CellRangeAddress(0, 0, 0, fieldNames.length - 1);
        CellRangeAddress region1 = new CellRangeAddress(1, 1, 0, 2);
        CellRangeAddress region2 = new CellRangeAddress(2, 2, 1, 2);
        CellRangeAddress region3 = new CellRangeAddress(3, 3, 1, 2);
        CellRangeAddress region4 = new CellRangeAddress(4, 4, 1, 2);
        CellRangeAddress region5 = new CellRangeAddress(5, 5, 1, 2);
        CellRangeAddress region6 = new CellRangeAddress(7, 7, 1, 2);
        CellRangeAddress region7 = new CellRangeAddress(15, 15, 1, 2);
        CellRangeAddress region8 = new CellRangeAddress(16, 16, 1, 2);
        CellRangeAddress region9 = new CellRangeAddress(17, 17, 1, 2);
        CellRangeAddress region10 = new CellRangeAddress(18, 18, 1, 2);
        CellRangeAddress region11 = new CellRangeAddress(19, 19, 1, 2);
        CellRangeAddress region12 = new CellRangeAddress(20, 20, 1, 2);
        CellRangeAddress region13 = new CellRangeAddress(21, 21, 1, 2);
        CellRangeAddress region14 = new CellRangeAddress(22, 22, 1, 2);
        CellRangeAddress region15 = new CellRangeAddress(25, 25, 1, 2);
        CellRangeAddress region16 = new CellRangeAddress(26, 26, 1, 2);
        CellRangeAddress region17 = new CellRangeAddress(27, 27, 1, 2);
        CellRangeAddress region18 = new CellRangeAddress(28, 28, 1, 2);
        CellRangeAddress region19 = new CellRangeAddress(29, 29, 1, 2);
        CellRangeAddress region20 = new CellRangeAddress(30, 30, 1, 2);
        CellRangeAddress region21 = new CellRangeAddress(31, 31, 1, 2);
        CellRangeAddress region22 = new CellRangeAddress(32, 32, 1, 2);
        CellRangeAddress region23 = new CellRangeAddress(33, 33, 1, 2);
        CellRangeAddress region24 = new CellRangeAddress(34, 34, 1, 2);
        CellRangeAddress region25 = new CellRangeAddress(37, 37, 1, 2);
        CellRangeAddress region26 = new CellRangeAddress(38, 38, 1, 2);
        CellRangeAddress region27 = new CellRangeAddress(39, 39, 1, 2);
        CellRangeAddress region28 = new CellRangeAddress(40, 40, 1, 2);
        CellRangeAddress region29 = new CellRangeAddress(41, 41, 1, 2);
        CellRangeAddress region30 = new CellRangeAddress(42, 42, 1, 2);
        CellRangeAddress region31 = new CellRangeAddress(43, 43, 1, 2);
        CellRangeAddress region32 = new CellRangeAddress(44, 44, 1, 2);
        CellRangeAddress region33 = new CellRangeAddress(45, 45, 1, 2);
        CellRangeAddress region34 = new CellRangeAddress(46, 46, 1, 2);
        CellRangeAddress region35 = new CellRangeAddress(2, 25, 0, 0);
        CellRangeAddress region36 = new CellRangeAddress(26, 31, 0, 0);
        CellRangeAddress region37 = new CellRangeAddress(32, 34, 0, 0);
        CellRangeAddress region38 = new CellRangeAddress(35, 41, 0, 0);
        CellRangeAddress region39 = new CellRangeAddress(42, 43, 0, 0);
        CellRangeAddress region40 = new CellRangeAddress(44, 46, 0, 0);
        CellRangeAddress region41 = new CellRangeAddress(8, 10, 1, 1);
        CellRangeAddress region42 = new CellRangeAddress(11, 14, 1, 1);
        CellRangeAddress region43 = new CellRangeAddress(23, 24, 1, 1);
        CellRangeAddress region44 = new CellRangeAddress(35, 36, 1, 1);

        sheet.addMergedRegionUnsafe(region);sheet.addMergedRegionUnsafe(region1);sheet.addMergedRegionUnsafe(region2);sheet.addMergedRegionUnsafe(region3);
        sheet.addMergedRegionUnsafe(region4);sheet.addMergedRegionUnsafe(region5);sheet.addMergedRegionUnsafe(region6);sheet.addMergedRegionUnsafe(region7);
        sheet.addMergedRegionUnsafe(region8);sheet.addMergedRegionUnsafe(region9);sheet.addMergedRegionUnsafe(region10);sheet.addMergedRegionUnsafe(region11);
        sheet.addMergedRegionUnsafe(region12);sheet.addMergedRegionUnsafe(region13);sheet.addMergedRegionUnsafe(region14);sheet.addMergedRegionUnsafe(region15);
        sheet.addMergedRegionUnsafe(region16);sheet.addMergedRegionUnsafe(region17);sheet.addMergedRegionUnsafe(region18);sheet.addMergedRegionUnsafe(region19);
        sheet.addMergedRegionUnsafe(region20);sheet.addMergedRegionUnsafe(region21);sheet.addMergedRegionUnsafe(region22);sheet.addMergedRegionUnsafe(region23);
        sheet.addMergedRegionUnsafe(region24);sheet.addMergedRegionUnsafe(region25);sheet.addMergedRegionUnsafe(region26);sheet.addMergedRegionUnsafe(region27);
        sheet.addMergedRegionUnsafe(region28);sheet.addMergedRegionUnsafe(region29);sheet.addMergedRegionUnsafe(region30);sheet.addMergedRegionUnsafe(region31);
        sheet.addMergedRegionUnsafe(region32);sheet.addMergedRegionUnsafe(region33);sheet.addMergedRegionUnsafe(region34);sheet.addMergedRegionUnsafe(region35);
        sheet.addMergedRegionUnsafe(region36);sheet.addMergedRegionUnsafe(region37);sheet.addMergedRegionUnsafe(region38);sheet.addMergedRegionUnsafe(region39);
        sheet.addMergedRegionUnsafe(region40);sheet.addMergedRegionUnsafe(region41);sheet.addMergedRegionUnsafe(region42);sheet.addMergedRegionUnsafe(region43);
        sheet.addMergedRegionUnsafe(region44);
        if(data1List.size()>0){
            CellRangeAddress region45 = new CellRangeAddress(47, 47+data1List.size()-1, 0, 0);
            sheet.addMergedRegionUnsafe(region45);
        }
        // 设置每一列的自适应宽度
        for (int i = 0; i < fieldNames.length; i++) {
            sheet.setColumnWidth(i, fieldNames[i].getBytes().length * 2 * 256);
        }
        sheet.createFreezePane(0,2);
        return wbObj;
    }

    public static <T> HSSFWorkbook exportMonthWorkExcel(List<T> dataList,
                                                 String[] fieldNames, String[] fieldCodes, String title) {
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

}
