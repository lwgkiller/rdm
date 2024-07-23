package com.redxun.standardManager.report.manager;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.query.Page;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.standardManager.core.manager.StandardConfigManager;
import com.redxun.standardManager.core.manager.StandardManager;
import com.redxun.standardManager.core.util.StandardConstant;
import com.redxun.standardManager.report.dao.StandardReportDao;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 应用模块名称
 * <p>
 * 代码描述
 * <p>
 * Copyright: Copyright (C) 2019 XXX, Inc. All rights reserved.
 * <p>
 * Company: 徐工挖掘机械有限公司
 * <p>
 *
 * @author liangchuanjiang
 * @since 2019/12/17 15:20
 */
@Service
public class StandardReportManager {
    private static Logger logger = LoggerFactory.getLogger(StandardManager.class);
    @Autowired
    private StandardReportDao standardReportDao;
    @Autowired
    private StandardManager standardManager;
    @Autowired
    private StandardConfigManager standardConfigManager;

    @Autowired
    private CommonInfoManager commonInfoManager;
    @Resource
    private CommonInfoDao commonInfoDao;

    public JSONObject queryPublishChart(String publishTimeFrom, String publishTimeTo, String systemCategoryId) {
        int startY = Integer.parseInt(publishTimeFrom.substring(0, 4));
        int endY = Integer.parseInt(publishTimeTo.substring(0, 4));
        List<String> years = new ArrayList<>();
        for (int i = startY; i <= endY; i++) {
            years.add(String.valueOf(i));
        }
        String publishTimeFromLocal = StandardManager.parseTime2Utc(publishTimeFrom);
        String publishTimeToLocal = StandardManager.parseTime2Utc(publishTimeTo);
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        params.put("publishTimeFrom", publishTimeFromLocal);
        params.put("publishTimeTo", publishTimeToLocal);
        params.put("systemCategoryId", systemCategoryId);

        List<Map<String, Object>> resultMap = standardReportDao.queryPublishChart(params);
        Map<String, Long> year2Num = new HashMap<>();
        if (resultMap != null && !resultMap.isEmpty()) {
            for (Map<String, Object> oneMap : resultMap) {
                String year = oneMap.get("publishY").toString();
                Long num = (Long)oneMap.get("countNum");
                year2Num.put(year, num);
            }
        }
        List<Long> resultData = new ArrayList<>();
        for (int i = 0; i < years.size(); i++) {
            if (year2Num.containsKey(years.get(i))) {
                resultData.add(year2Num.get(years.get(i)));
            } else {
                resultData.add(0L);
            }
        }
        JSONObject result = new JSONObject();
        result.put("data", resultData);
        result.put("years", years);
        return result;
    }

    public JSONObject queryCategoryChart(String systemCategoryId) {
        JSONObject result = new JSONObject();
        List<String> categoryNames = new ArrayList<>();
        List<String> categoryIds = new ArrayList<>();
        List<Map<String, Object>> categoryInfos = standardConfigManager.categoryQuery();
        if (categoryInfos != null && !categoryInfos.isEmpty()) {
            for (Map<String, Object> oneMap : categoryInfos) {
                categoryNames.add(oneMap.get("categoryName").toString());
                categoryIds.add(oneMap.get("id").toString());
            }
        }
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        params.put("systemCategoryId", systemCategoryId);
        List<Map<String, Object>> resultMap = standardReportDao.queryCategoryChart(params);
        Map<String, Long> categoryId2Num = new HashMap<>();
        if (resultMap != null && !resultMap.isEmpty()) {
            for (Map<String, Object> oneMap : resultMap) {
                String categoryId = oneMap.get("standardCategoryId").toString();
                Long num = (Long)oneMap.get("countNum");
                categoryId2Num.put(categoryId, num);
            }
        }
        List<Long> resultData = new ArrayList<>();
        for (int i = 0; i < categoryIds.size(); i++) {
            if (categoryId2Num.containsKey(categoryIds.get(i))) {
                resultData.add(categoryId2Num.get(categoryIds.get(i)));
            } else {
                resultData.add(0L);
            }
        }
        result.put("data", resultData);
        result.put("categoryNames", categoryNames);
        return result;
    }

    // 查询标准的预览或者下载次数排行
    public JSONObject queryStandardCheckChart(String systemCategoryId, String timeFrom, String timeTo,
        String checkCategoryId) {
        JSONObject result = new JSONObject();
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        params.put("systemCategoryId", systemCategoryId);
        if (StringUtils.isNotBlank(timeTo)) {
            params.put("timeTo", DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(timeTo), 16)));
        }
        if (StringUtils.isNotBlank(timeFrom)) {
            params.put("timeFrom", DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(timeFrom), -8)));
        }
        params.put("checkCategoryId", checkCategoryId);
        List<Map<String, Object>> resultMap = standardReportDao.queryStandardCheckChart(params);
        if (resultMap == null) {
            resultMap = Collections.emptyList();
        }
        result.put("data", resultMap);
        return result;
    }

    // 查询标准的预览或者下载次数排行
    public JSONObject queryDepCheckStandardChart(String systemCategoryId, String timeFrom, String timeTo,
        String checkCategoryId) {
        JSONObject result = new JSONObject();
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        params.put("systemCategoryId", systemCategoryId);
        if (StringUtils.isNotBlank(timeTo)) {
            params.put("timeTo", DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(timeTo), 16)));
        }
        if (StringUtils.isNotBlank(timeFrom)) {
            params.put("timeFrom", DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(timeFrom), -8)));
        }
        params.put("checkCategoryId", checkCategoryId);
        List<Map<String, Object>> resultMap = standardReportDao.queryDepCheckStandardChart(params);
        if (resultMap == null) {
            resultMap = Collections.emptyList();
        }
        result.put("data", resultMap);
        return result;
    }

    // 标准按照发布日期统计
    public JsonPageResult<?> groupByPublish(HttpServletRequest request) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            Map<String, Object> queryParam = new HashMap<String, Object>();
            String currentUserId = ContextUtil.getCurrentUserId();
            getGroupByPublishParams(request, queryParam);
            List<Map<String, Object>> queryPageResult = standardReportDao.groupByPublish(queryParam);
            // 查询总数(按年分类)
            List<Map<String, Object>> countByYear = standardReportDao.groupByPublishCount(queryParam);
            Long sumNumber = 0L;
            Map<String, String> year2Num = new HashMap<>();
            if (countByYear != null && !countByYear.isEmpty()) {
                for (Map<String, Object> oneMap : countByYear) {
                    Object number = oneMap.get("countNum");
                    year2Num.put(oneMap.get("publishY").toString(), number.toString());
                    sumNumber += (Long)number;
                }
            }
            // 发布年份拼接每年的个数
            if (queryPageResult != null && !queryPageResult.isEmpty()) {
                for (Map<String, Object> oneMap : queryPageResult) {
                    String year = oneMap.get("publishY").toString();
                    year += "年(" + year2Num.get(year) + "个)";
                    oneMap.put("publishY", year);
                }
            }
            result.setData(queryPageResult);
            result.setTotal(sumNumber.intValue());
        } catch (Exception e) {
            logger.error("Exception in groupByPublish", e);
            result.setSuccess(false);
            result.setMessage("系统异常！");
        }
        return result;
    }

    // 拼接请求参数和分页排序参数
    private void getGroupByPublishParams(HttpServletRequest request, Map<String, Object> queryParam) {
        queryParam.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        // 分页
        int pageIndex = RequestUtil.getInt(request, "pageIndex", 0);
        int pageSize = RequestUtil.getInt(request, "pageSize", Page.DEFAULT_PAGE_SIZE);
        queryParam.put("startIndex", pageIndex * pageSize);
        queryParam.put("pageSize", pageSize);
        // 排序
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)) {
            queryParam.put("sortField", sortField);
            queryParam.put("sortOrder", sortOrder);
        }
        // 过滤条件
        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    // 数据库中存储的时间是UTC时间，因此需要将前台传递的北京时间转化
                    if ("publishTimeFrom".equalsIgnoreCase(name)) {
                        value = StandardManager.parseTime2Utc(value);
                    }
                    if ("publishTimeTo".equalsIgnoreCase(name)) {
                        value = StandardManager.parseTime2Utc(value);
                    }
                    if ("selectedSystemIds".equalsIgnoreCase(name)) {
                        String[] systemIdArr = value.split(",", -1);
                        if (systemIdArr != null && systemIdArr.length != 0) {
                            queryParam.put(name, Arrays.asList(systemIdArr));
                        }
                    } else {
                        queryParam.put(name, value);
                    }
                }
            }
        }
    }

    // 导出标准excel
    public void exportGroupByPublish(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> queryParam = new HashMap<String, Object>();
        getGroupByPublishParams(request, queryParam);
        queryParam.remove("startIndex");
        queryParam.remove("pageSize");
        queryParam.remove("sortField");
        queryParam.remove("sortOrder");
        List<Map<String, Object>> queryPageResult = standardReportDao.groupByPublish(queryParam);
        // 查询总数(按年分类)
        List<Map<String, Object>> countByYear = standardReportDao.groupByPublishCount(queryParam);
        Map<String, String> year2Num = new HashMap<>();
        if (countByYear != null && !countByYear.isEmpty()) {
            for (Map<String, Object> oneMap : countByYear) {
                Object number = oneMap.get("countNum");
                year2Num.put(oneMap.get("publishY").toString(), number.toString());
            }
        }
        // 发布年份拼接每年的个数
        if (queryPageResult != null && !queryPageResult.isEmpty()) {
            for (int i = 0; i < queryPageResult.size(); i++) {
                Map<String, Object> oneMap = queryPageResult.get(i);
                String year = oneMap.get("publishY").toString();
                year += "年(" + year2Num.get(year) + "个)";
                oneMap.put("publishY", year);
                if (oneMap.get("standardStatus") != null) {
                    oneMap.put("standardStatusName",
                        StandardManager.getStatusName(oneMap.get("standardStatus").toString()));
                }
                oneMap.put("index", i + 1);
            }
        }
        String systemCategoryName = "技术标准";
        if (queryParam.get("systemCategoryId") != null) {
            if (queryParam.get("systemCategoryId").equals(StandardConstant.SYSTEMCATEGORY_GL)) {
                systemCategoryName = "管理标准";
            }
        }
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "徐工挖机" + systemCategoryName + "发布日期统计表";
        String excelName = nowDate + title;
        String[] fieldNames = {"序号", "发布年份", "标准体系", "体系编号", "标准编号", "标准类别", "标准名称", "标准状态", "归口部门"};
        String[] fieldCodes = {"index", "publishY", "systemName", "systemNumber", "standardNumber", "categoryName",
            "standardName", "standardStatusName", "belongDepName"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(queryPageResult, fieldNames, fieldCodes, title);
        if (countByYear != null && !countByYear.isEmpty()) {
            int sumRow = 2;
            // 合并发布年份列
            HSSFSheet sheet = wbObj.getSheetAt(0);
            for (Map<String, Object> oneMap : countByYear) {
                Long number = (Long)oneMap.get("countNum");
                int startRow = sumRow;
                int endRow = startRow + number.intValue() - 1;
                // 行号、列号从0开始，开始和结束都包含
                sheet.addMergedRegionUnsafe(new CellRangeAddress(startRow, endRow, 1, 1));
                sumRow = endRow + 1;
            }
        }
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }

    // 标准按照体系统计
    public JsonPageResult<?> groupBySystem(HttpServletRequest request) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            Map<String, Object> queryParam = new HashMap<String, Object>();
            getGroupBySystemParams(request, queryParam);
            List<Map<String, Object>> queryPageResult = standardReportDao.groupBySystem(queryParam);
            // 查询总数(按体系分类)
            List<Map<String, Object>> countBySystem = standardReportDao.groupBySystemCount(queryParam);
            Long sumNumber = 0L;
            Map<String, String> systemNumber2Num = new HashMap<>();
            if (countBySystem != null && !countBySystem.isEmpty()) {
                for (Map<String, Object> oneMap : countBySystem) {
                    Object number = oneMap.get("countNum");
                    systemNumber2Num.put(oneMap.get("systemNumber").toString(), number.toString());
                    sumNumber += (Long)number;
                }
            }
            // 标准体系拼接个数
            if (queryPageResult != null && !queryPageResult.isEmpty()) {
                for (Map<String, Object> oneMap : queryPageResult) {
                    String systemName = oneMap.get("systemName").toString();
                    String systemNumber = oneMap.get("systemNumber").toString();
                    systemName += "(" + systemNumber2Num.get(systemNumber) + "个)";
                    oneMap.put("systemName", systemName);
                    if (oneMap.get("publishTime") != null) {
                        oneMap.put("publishTime",
                            DateUtil.formatDate((Date)oneMap.get("publishTime"), "yyyy-MM-dd HH:mm:ss"));
                    }
                }
            }
            result.setData(queryPageResult);
            result.setTotal(sumNumber.intValue());
        } catch (Exception e) {
            logger.error("Exception in groupBySystem", e);
            result.setSuccess(false);
            result.setMessage("系统异常！");
        }
        return result;
    }

    // 拼接请求参数和分页排序参数
    private void getGroupBySystemParams(HttpServletRequest request, Map<String, Object> queryParam) {
        queryParam.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        // 分页
        int pageIndex = RequestUtil.getInt(request, "pageIndex", 0);
        int pageSize = RequestUtil.getInt(request, "pageSize", Page.DEFAULT_PAGE_SIZE);
        queryParam.put("startIndex", pageIndex * pageSize);
        queryParam.put("pageSize", pageSize);
        // 排序
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)) {
            queryParam.put("sortField", sortField);
            queryParam.put("sortOrder", sortOrder);
        }
        // 过滤条件
        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    // 数据库中存储的时间是UTC时间，因此需要将前台传递的北京时间转化
                    if ("publishTimeFrom".equalsIgnoreCase(name)) {
                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), -8));
                    }
                    if ("publishTimeTo".equalsIgnoreCase(name)) {
                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), 16));
                    }

                    if ("selectedSystemIds".equalsIgnoreCase(name)) {
                        String[] systemIdArr = value.split(",", -1);
                        if (systemIdArr != null && systemIdArr.length != 0) {
                            queryParam.put(name, Arrays.asList(systemIdArr));
                        }
                    } else {
                        queryParam.put(name, value);
                    }
                }
            }
        }
    }

    // 导出标准excel
    public void exportGroupBySystem(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> queryParam = new HashMap<String, Object>();
        getGroupBySystemParams(request, queryParam);
        queryParam.remove("startIndex");
        queryParam.remove("pageSize");
        queryParam.remove("sortField");
        queryParam.remove("sortOrder");
        List<Map<String, Object>> queryPageResult = standardReportDao.groupBySystem(queryParam);
        // 查询总数(按体系分类)
        List<Map<String, Object>> countBySystem = standardReportDao.groupBySystemCount(queryParam);

        Map<String, String> systemNumber2Num = new HashMap<>();
        if (countBySystem != null && !countBySystem.isEmpty()) {
            for (Map<String, Object> oneMap : countBySystem) {
                Object number = oneMap.get("countNum");
                systemNumber2Num.put(oneMap.get("systemNumber").toString(), number.toString());
            }
        }
        // 标准体系拼接个数
        if (queryPageResult != null && !queryPageResult.isEmpty()) {
            for (int i = 0; i < queryPageResult.size(); i++) {
                Map<String, Object> oneMap = queryPageResult.get(i);
                String systemName = oneMap.get("systemName").toString();
                String systemNumber = oneMap.get("systemNumber").toString();
                systemName += "(" + systemNumber2Num.get(systemNumber) + "个)";
                oneMap.put("systemName", systemName);
                if (oneMap.get("standardStatus") != null) {
                    oneMap.put("standardStatusName",
                        StandardManager.getStatusName(oneMap.get("standardStatus").toString()));
                }
                oneMap.put("index", i + 1);
                if (oneMap.get("publishTime") != null) {
                    oneMap.put("publishTime",
                        DateUtil.formatDate((Date)oneMap.get("publishTime"), "yyyy-MM-dd HH:mm:ss"));
                }
            }
        }

        String systemCategoryName = "技术标准";
        if (queryParam.get("systemCategoryId") != null) {
            if (queryParam.get("systemCategoryId").equals(StandardConstant.SYSTEMCATEGORY_GL)) {
                systemCategoryName = "管理标准";
            }
        }
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "徐工挖机" + systemCategoryName + "体系统计表";
        String excelName = nowDate + title;
        String[] fieldNames = {"序号", "标准体系", "体系编号", "标准编号", "标准类别", "标准名称", "标准状态", "归口部门", "发布时间"};
        String[] fieldCodes = {"index", "systemName", "systemNumber", "standardNumber", "categoryName", "standardName",
            "standardStatusName", "belongDepName", "publishTime"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(queryPageResult, fieldNames, fieldCodes, title);
        if (countBySystem != null && !countBySystem.isEmpty()) {
            int sumRow = 2;
            // 合并发布年份列
            HSSFSheet sheet = wbObj.getSheetAt(0);
            for (Map<String, Object> oneMap : countBySystem) {
                Long number = (Long)oneMap.get("countNum");
                int startRow = sumRow;
                int endRow = startRow + number.intValue() - 1;
                // 行号、列号从0开始，开始和结束都包含
                sheet.addMergedRegionUnsafe(new CellRangeAddress(startRow, endRow, 1, 1));
                sumRow = endRow + 1;
            }
        }
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }

    // 标准按照项目统计
    public JsonPageResult<?> groupByProject(HttpServletRequest request) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            Map<String, Object> queryParam = new HashMap<String, Object>();
            getGroupByProjectParams(request, queryParam);
            List<Map<String, Object>> queryPageResult = standardReportDao.groupByProject(queryParam);
            if (queryPageResult != null && !queryPageResult.isEmpty()) {
                for (int i = 0; i < queryPageResult.size(); i++) {
                    Map<String, Object> oneMap = queryPageResult.get(i);
                    JSONObject dept = commonInfoManager.queryDeptNameById(oneMap.get("mainDepId").toString());
                    String deptName = dept.getString("deptname");
                    oneMap.put("deptname",deptName);
                }
            }
            int ListCount = standardReportDao.groupByProjectCount(queryParam);
            result.setData(queryPageResult);
            result.setTotal(ListCount);
        } catch (Exception e) {
            logger.error("Exception in groupByProject", e);
            result.setSuccess(false);
            result.setMessage("系统异常！");
        }
        return result;
    }
    // 拼接请求参数和分页参数
    private void getGroupByProjectParams(HttpServletRequest request, Map<String, Object> queryParam) {

        // 分页
        int pageIndex = RequestUtil.getInt(request, "pageIndex", 0);
        int pageSize = RequestUtil.getInt(request, "pageSize", Page.DEFAULT_PAGE_SIZE);
        queryParam.put("startIndex", pageIndex * pageSize);
        queryParam.put("pageSize", pageSize);

        // 过滤条件
        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");

                if (StringUtils.isNotBlank(value)) {
                    if ("depName".equalsIgnoreCase(name)) {
                        List<JSONObject> list = commonInfoDao.getDeptInfoByDeptName(value);
                        if (list == null || list.isEmpty()) {
                            continue;
                        }else {
                            String mainDepId = list.get(0).getString("GROUP_ID_");
                            queryParam.put("mainDepId", mainDepId);
                        }

                    }
                    queryParam.put(name, value);
                }else
                {
                    queryParam.put(name, value);
                }
            }
        }
    }

    // 导出标准按项目统计excel
    public void exportGroupByProject(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> queryParam = new HashMap<String, Object>();
        getGroupByProjectParams(request, queryParam);
        queryParam.remove("startIndex");
        queryParam.remove("pageSize");

        List<Map<String, Object>> queryPageResult = standardReportDao.groupByProject(queryParam);

        // 标准体系拼接个数
        if (queryPageResult != null && !queryPageResult.isEmpty()) {
            for (int i = 0; i < queryPageResult.size(); i++) {
                Map<String, Object> oneMap = queryPageResult.get(i);
                oneMap.put("index",i+1);
                JSONObject dept = commonInfoManager.queryDeptNameById(oneMap.get("mainDepId").toString());
                String deptName = dept.getString("deptname");
                oneMap.put("deptname",deptName);
            }
        }

        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "徐工挖机标准按科技项目生成统计表";
        String excelName = nowDate + title;
        String[] fieldNames = {"序号", "标准编号", "标准名称", "标准类别","项目名称", "项目编号","牵头部门","项目负责人"};
        String[] fieldCodes = {"index", "outNumber", "outName", "typeName", "projectName", "number","deptname","respMan"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(queryPageResult, fieldNames, fieldCodes, title);

        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }
}
