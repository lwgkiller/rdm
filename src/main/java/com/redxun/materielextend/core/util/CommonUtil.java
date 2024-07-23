package com.redxun.materielextend.core.util;

import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.util.DateFormatUtil;

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
 * @since 2020/6/9 16:21
 */
public class CommonUtil {
    private static final Logger logger = LoggerFactory.getLogger(CommonUtil.class);

    public static String toGetApplyNo() {
        return "sq-" + DateFormatUtil.getNowUTCDateStr("yyyyMMddHHmmssSSS") + genereate3Random();
    }

    public static String toGetMatPriceReviewApplyNo() {
        return "jgsp-" + DateFormatUtil.getNowUTCDateStr("yyyyMMddHHmmssSSS") + genereate3Random();
    }

    // 产生随机的3位数
    public static String genereate3Random() {
        Random random = new Random();
        return String.valueOf(random.nextInt(900) + 100);
    }

    public static List<Map<String, String>> convertJSONObject2Map(List<JSONObject> objs) {
        List<Map<String, String>> result = new ArrayList<>();
        if (objs == null || objs.isEmpty()) {
            return result;
        }
        for (JSONObject obj : objs) {
            Map<String, String> oneMap = new HashMap<>();
            for (String key : obj.keySet()) {
                oneMap.put(key, obj.getString(key));
            }
            result.add(oneMap);
        }
        return result;
    }

    public static List<Map<String, Object>> convertJSONObject2MapObj(JSONObject obj) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (obj == null || obj.isEmpty()) {
            return result;
        }
        Map<String, Object> oneMap = new HashMap<>();
        for (String key : obj.keySet()) {
            oneMap.put(key, obj.getString(key));
        }
        result.add(oneMap);
        return result;
    }

    public static String toGetCellStringByType(Row row, int cellIndex) {
        if (row == null || cellIndex < 0) {
            return "";
        }
        Cell cell = row.getCell(cellIndex);
        if (cell == null) {
            return "";
        }
        /*cell.setCellType(CellType.STRING);
        String cellValue = cell.getStringCellValue();*/
        DataFormatter dataFormatter = new DataFormatter();
        return StringUtils.trim(dataFormatter.formatCellValue(cell));
    }

    public static boolean judgeIsBlankRow(Row row) {
        if (row == null) {
            return true;
        }
        for (int index = 0; index < row.getLastCellNum(); index++) {
            String cellValue = toGetCellStringByType(row, index);
            if (StringUtils.isNotBlank(cellValue)) {
                return false;
            }
        }
        return true;
    }

    public static boolean judgeIsNumber(String numberStr) {
        if (StringUtils.isBlank(numberStr)) {
            return false;
        }
        try {
            Double numberVal = Double.parseDouble(numberStr);
        } catch (Exception e) {
            logger.error("Exception in judgeIsNumber, str is " + numberStr, e);
            return false;
        }
        return true;
    }
}
