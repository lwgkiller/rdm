package com.redxun.fzsj.core.service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonResult;
import com.redxun.core.json.JsonResultUtil;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.saweb.util.RequestUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 仿真工具相关，包括调用RDT的数据库
 */
@Service
public class FzsjToolService {
    private static Logger logger = LoggerFactory.getLogger(FzsjToolService.class);
    @Autowired
    private CommonInfoManager commonInfoManager;

    public JsonResult jdbcQueryMonthInfo(HttpServletRequest request) {
        List<JSONObject> monthData = new ArrayList<>();
        Map<String, Object> resultMap = new HashMap<>();
        try {
            // 1、注册驱动
            Class.forName("com.mysql.jdbc.Driver");
            // 2、获取数据库连接对象
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://10.15.10.199:3306/xugong?characterEncoding=utf8&useSSL=false&allowMultiQueries=true&serverTimezone=UTC",
                    "root", "123456");
            // 3、定义sql
            String startTime = RequestUtil.getString(request, "ydsyFrom", "");
            String endTime = RequestUtil.getString(request, "ydsyTo", "");
            JSONObject param = new JSONObject();
            if (StringUtils.isNotBlank(startTime)) {
                param.put("startTime", startTime);
            }
            if (StringUtils.isNotBlank(endTime)) {
                param.put("endTime", endTime);
            }
            String sql = toQueryMonthStatement(param);
            // 4、获取执行sql的对象
            Statement stmt = conn.createStatement();
            // 5、执行sql
            ResultSet rs = stmt.executeQuery(sql);
            // 6、处理结果
            while (rs.next()) {
                JSONObject oneData = new JSONObject();
                // 获取数据 两种方式 根据列的索引获取 根据列名获取
                String useCount = rs.getString("useCount");
                if (StringUtils.isNotBlank(useCount)) {
                    oneData.put("useCount", useCount);
                }
                String useTime = rs.getString("useTime");
                if (StringUtils.isNotBlank(useTime)) {
                    oneData.put("useTime", useTime);
                }
                monthData.add(oneData);
            }
            // 7、关闭资源
            rs.close();
            stmt.close();
            conn.close();

            Map<String, Integer> month2Data = new HashMap<>();
            // 将传入参数转化为开始月份和结束月份
            List<String> xAxisData = toGetYearMonthList(startTime, endTime);
            resultMap.put("xAxisData", xAxisData);
            // y轴数据
            List<Integer> yAxisData = new ArrayList<>();

            if (monthData != null && !monthData.isEmpty()) {
                for (JSONObject oneData : monthData) {
                    String useTime = oneData.getString("useTime");
                    int useCount = oneData.getInteger("useCount");
                    month2Data.put(useTime, useCount);
                }
            }
            for (String yearMonth : xAxisData) {
                if (!month2Data.containsKey(yearMonth)) {
                    yAxisData.add(0);
                } else {
                    yAxisData.add(month2Data.get(yearMonth));
                }
            }
            // 组合数据
            List<JSONObject> seriesData = new ArrayList<>();
            JSONObject oneSerie = new JSONObject();
            oneSerie.put("name", "使用次数");
            oneSerie.put("type", "bar");
            oneSerie.put("data", yAxisData);
            seriesData.add(oneSerie);
            resultMap.put("series", seriesData);
        } catch (Exception e) {
            logger.error("Exception in jdbcQueryRdtUseInfo", e);
        }
        return JsonResultUtil.success(resultMap);
    }

    private String toQueryMonthStatement(JSONObject param) {
        String queryStatement = "SELECT count( * ) as useCount,DATE_FORMAT( sim_oplog.created_at, '%Y-%m' ) AS useTime "
                + "FROM sim_oplog LEFT JOIN sim_plugin " + "ON sim_oplog.plugin_id = sim_plugin.id LEFT JOIN sim_user "
                + "ON sim_oplog.user_id = sim_user.id";
        if (!param.isEmpty()) {
            queryStatement += " WHERE ";
            if (param.containsKey("startTime") && StringUtils.isNotBlank(param.getString("startTime"))) {
                queryStatement += "left(sim_oplog.created_at,7) >= '" + param.getString("startTime") + "' ";
            }
            if (param.size() > 1) {
                queryStatement += " AND ";
            }
            if (param.containsKey("endTime") && StringUtils.isNotBlank(param.getString("endTime"))) {
                queryStatement += " left(sim_oplog.created_at,7) <= '" + param.getString("endTime") + "' ";
            }
        }
        return queryStatement + " GROUP BY useTime";
    }

    public JsonResult jdbcQueryTypeInfo(HttpServletRequest request) {
        List<JSONObject> typeData = new ArrayList<>();
        Map<String, Object> resultMap = new HashMap<>();
        try {
            // 1、注册驱动
            Class.forName("com.mysql.jdbc.Driver");
            // 2、获取数据库连接对象
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://10.15.10.199:3306/xugong?characterEncoding=utf8&useSSL=false&allowMultiQueries=true&serverTimezone=UTC",
                    "root", "123456");
            // 3、定义sql
            String startTime = RequestUtil.getString(request, "zlsyFrom", "");
            String endTime = RequestUtil.getString(request, "zlsyTo", "");
            JSONObject param = new JSONObject();
            if (StringUtils.isNotBlank(startTime)) {
                param.put("startTime", startTime);
            }
            if (StringUtils.isNotBlank(endTime)) {
                param.put("endTime", endTime);
            }
            String sql = toQueryTypeStatement(param);
            // 4、获取执行sql的对象
            Statement stmt = conn.createStatement();
            // 5、执行sql
            ResultSet rs = stmt.executeQuery(sql);
            // 6、处理结果
            while (rs.next()) {
                JSONObject oneData = new JSONObject();
                // 获取数据 两种方式 根据列的索引获取 根据列名获取
                String useCount = rs.getString("useCount");
                if (StringUtils.isNotBlank(useCount)) {
                    oneData.put("useCount", useCount);
                }
                String useName = rs.getString("useName");
                if (StringUtils.isNotBlank(useName)) {
                    oneData.put("useName", useName);
                }
                typeData.add(oneData);
            }
            // 7、关闭资源
            rs.close();
            stmt.close();
            conn.close();
            List<String> xAxisData = new ArrayList<>();
            // y轴数据
            List<Integer> yAxisData = new ArrayList<>();

            if (typeData != null && !typeData.isEmpty()) {
                for (JSONObject oneData : typeData) {
                    xAxisData.add(oneData.getString("useName"));
                    yAxisData.add(oneData.getInteger("useCount"));
                }
            }
            resultMap.put("xAxisData", xAxisData);
            // 组合数据
            List<JSONObject> seriesData = new ArrayList<>();
            JSONObject oneSerie = new JSONObject();
            oneSerie.put("name", "使用次数");
            oneSerie.put("type", "bar");
            oneSerie.put("data", yAxisData);
            seriesData.add(oneSerie);
            resultMap.put("series", seriesData);
        } catch (Exception e) {
            logger.error("Exception in jdbcQueryRdtUseInfo", e);
        }
        return JsonResultUtil.success(resultMap);
    }

    private String toQueryTypeStatement(JSONObject param) {
        String queryStatement =
                "SELECT count( * ) as useCount,sim_plugin.title AS useName " + "FROM sim_oplog LEFT JOIN sim_plugin "
                        + "ON sim_oplog.plugin_id = sim_plugin.id LEFT JOIN sim_user " + "ON sim_oplog.user_id = sim_user.id";
        if (!param.isEmpty()) {
            queryStatement += " WHERE ";
            if (param.containsKey("startTime") && StringUtils.isNotBlank(param.getString("startTime"))) {
                queryStatement += "left(sim_oplog.created_at,7) >= '" + param.getString("startTime") + "' ";
            }
            if (param.size() > 1) {
                queryStatement += " AND ";
            }
            if (param.containsKey("endTime") && StringUtils.isNotBlank(param.getString("endTime"))) {
                queryStatement += " left(sim_oplog.created_at,7) <= '" + param.getString("endTime") + "' ";
            }
        }
        return queryStatement + " GROUP BY useName";
    }

    public JsonResult jdbcQueryDeptInfo(HttpServletRequest request) {
        List<JSONObject> deptData = new ArrayList<>();
        Map<String, Object> resultMap = new HashMap<>();
        try {
            // 1、注册驱动
            Class.forName("com.mysql.jdbc.Driver");
            // 2、获取数据库连接对象
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://10.15.10.199:3306/xugong?characterEncoding=utf8&useSSL=false&allowMultiQueries=true&serverTimezone=UTC",
                    "root", "123456");
            // 3、定义sql
            String startTime = RequestUtil.getString(request, "bmsyFrom", "");
            String endTime = RequestUtil.getString(request, "bmsyTo", "");
            JSONObject param = new JSONObject();
            if (StringUtils.isNotBlank(startTime)) {
                param.put("startTime", startTime);
            }
            if (StringUtils.isNotBlank(endTime)) {
                param.put("endTime", endTime);
            }
            String sql = toQueryDeptStatement(param);
            // 4、获取执行sql的对象
            Statement stmt = conn.createStatement();
            // 5、执行sql
            ResultSet rs = stmt.executeQuery(sql);
            // 6、处理结果
            while (rs.next()) {
                JSONObject oneData = new JSONObject();
                // 获取数据 两种方式 根据列的索引获取 根据列名获取
                String useCount = rs.getString("useCount");
                if (StringUtils.isNotBlank(useCount)) {
                    oneData.put("useCount", useCount);
                }
                String userId = rs.getString("userId");
                if (StringUtils.isNotBlank(userId)) {
                    oneData.put("userId", userId);
                }
                String userName = rs.getString("userName");
                if (StringUtils.isNotBlank(userName)) {
                    oneData.put("userName", userName);
                }
                deptData.add(oneData);
            }
            // 7、关闭资源
            rs.close();
            stmt.close();
            conn.close();

            List<JSONObject> deptInfo = new ArrayList<>();
            List<String> xAxisData = new ArrayList<>();
            // y轴数据
            List<Integer> yAxisData = new ArrayList<>();
            if (deptData != null && !deptData.isEmpty()) {
                Set<String> userIds = new HashSet<>();
                for (JSONObject onedata : deptData) {
                    userIds.add(onedata.getString("userId"));
                }
                deptInfo = commonInfoManager.batchDeptByUserId(userIds);
                Iterator<JSONObject> iterator = deptData.iterator();
                while (iterator.hasNext()) {
                    Boolean exist = false;
                    JSONObject onedata = iterator.next();
                    String userId = onedata.getString("userId");
                    for (JSONObject oneInfo : deptInfo) {
                        if (userId.equals(oneInfo.getString("userId"))) {
                            onedata.put("deptId", oneInfo.getString("deptId"));
                            onedata.put("deptName", oneInfo.getString("deptName"));
                            exist = true;
                            continue;
                        }
                    }
                    if (!exist) {
                        iterator.remove();
                    }
                }
            }
            Stream<JSONObject> stream = deptData.stream();
            Map<String, List<JSONObject>> deptIdToUser =
                    stream.collect(Collectors.groupingBy(j -> j.getString("deptName")));
            if (deptIdToUser != null && !deptIdToUser.isEmpty()) {
                Set keys = deptIdToUser.keySet();
                Iterator it = keys.iterator();
                while (it.hasNext()) {
                    int count = 0;
                    String deptName = (String) it.next();
                    List<JSONObject> userToCount = deptIdToUser.get(deptName);
                    for (JSONObject oneUser : userToCount) {
                        count += oneUser.getInteger("useCount");
                    }
                    yAxisData.add(count);
                    xAxisData.add(deptName);
                }
            }
            resultMap.put("xAxisData", xAxisData);
            // 组合数据
            List<JSONObject> seriesData = new ArrayList<>();
            JSONObject oneSerie = new JSONObject();
            oneSerie.put("name", "使用次数");
            oneSerie.put("type", "bar");
            oneSerie.put("data", yAxisData);
            seriesData.add(oneSerie);
            resultMap.put("series", seriesData);
        } catch (

                Exception e) {
            logger.error("Exception in jdbcQueryRdtUseInfo", e);
        }
        return JsonResultUtil.success(resultMap);
    }

    private String toQueryDeptStatement(JSONObject param) {
        String queryStatement = "SELECT count( * ) as useCount,sim_user.uid AS userId,sim_user.nickname AS userName "
                + "FROM sim_oplog LEFT JOIN sim_plugin " + "ON sim_oplog.plugin_id = sim_plugin.id LEFT JOIN sim_user "
                + "ON sim_oplog.user_id = sim_user.id";
        if (!param.isEmpty()) {
            queryStatement += " WHERE ";
            if (param.containsKey("startTime") && StringUtils.isNotBlank(param.getString("startTime"))) {
                queryStatement += "left(sim_oplog.created_at,7) >= '" + param.getString("startTime") + "' ";
            }
            if (param.size() > 1) {
                queryStatement += " AND ";
            }
            if (param.containsKey("endTime") && StringUtils.isNotBlank(param.getString("endTime"))) {
                queryStatement += " left(sim_oplog.created_at,7) <= '" + param.getString("endTime") + "' ";
            }
        }
        return queryStatement + " GROUP BY userName";
    }

    private List<String> toGetYearMonthList(String start, String end) {
        List<String> result = new ArrayList<>();
        try {
            while (start.compareTo(end) <= 0) {
                result.add(start);
                start = DateFormatUtil.format(DateUtil.addMonth(DateFormatUtil.parse(start, "yyyy-MM"), 1), "yyyy-MM");
            }
        } catch (Exception e) {
            logger.error("生成月份异常", e);
        }

        return result;
    }
}
