package com.redxun.rdmZhgl.core.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.materielextend.core.util.CommonUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmZhgl.core.dao.MonthWorkApplyDao;
import com.redxun.rdmZhgl.core.dao.MonthWorkDao;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.standardManager.core.util.ResultUtil;
import com.redxun.sys.org.dao.OsUserDao;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import com.redxun.xcmgProjectManager.report.dao.XcmgProjectReportDao;
import com.redxun.xcmgProjectManager.report.manager.XcmgProjectReportManager;

/**
 * @author zhangzhen
 */
@Service
public class MonthWorkService {
    private static final Logger logger = LoggerFactory.getLogger(MonthWorkService.class);
    @Resource
    MonthWorkDao monthWorkDao;
    @Autowired
    OsUserDao osUserDao;
    @Autowired
    RdmZhglUtil rdmZhglUtil;

    @Resource
    CommonInfoManager commonInfoManager;
    @Resource
    MonthWorkApplyDao monthWorkApplyDao;
    @Resource
    XcmgProjectReportManager xcmgProjectReportManager;
    @Resource
    CommonInfoDao commonInfoDao;
    @Resource
    private XcmgProjectReportDao xcmgProjectReportDao;

    public static void convertDate(List<Map<String, Object>> list) {
        if (list != null && !list.isEmpty()) {
            for (Map<String, Object> oneApply : list) {
                for (String key : oneApply.keySet()) {
                    if (key.endsWith("_TIME_") || key.endsWith("_time") || key.endsWith("_date")) {
                        if (oneApply.get(key) != null) {
                            oneApply.put(key, DateUtil.formatDate((Date)oneApply.get(key), "yyyy-MM-dd  HH:mm:ss"));
                        }
                    }
                    if ("startDate".equals(key)) {
                        if (oneApply.get(key) != null) {
                            oneApply.put(key, DateUtil.formatDate((Date)oneApply.get(key), "yyyy-MM-dd"));
                        }
                    }
                    if ("endDate".equals(key)) {
                        if (oneApply.get(key) != null) {
                            oneApply.put(key, DateUtil.formatDate((Date)oneApply.get(key), "yyyy-MM-dd"));
                        }
                    }
                }
            }
        }
    }

    public List<Map<String, Object>> getPersonProjectList(JSONObject jsonObject) throws ParseException {
        // list已包含计划结束时间
        List<Map<String, Object>> list = monthWorkDao.getPersonProjectList(jsonObject);
        rdmZhglUtil.setTaskCreateTimeInfo(list,true);
        for (Map<String, Object> map : list) {
            xcmgProjectReportManager.queryProjectProgressNum(map, "");
            xcmgProjectReportManager.queryProjectRisk(map, "");
        }
        List<String> timeNames = new ArrayList<>();
        timeNames.add("planStartTime");
        timeNames.add("planEndTime");
        CommonFuns.convertMapListDate(list, timeNames, "yyyy-MM-dd");
        return list;
    }

    public JSONArray getStageList(JSONObject jsonObject) {
        return monthWorkDao.getStageList(jsonObject);
    }

    public List<Map<String, Object>> getPlanList(HttpServletRequest request) {
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            Map<String, Object> params = new HashMap<>(16);
            // filter、翻页、租户字段TENANT_ID_添加
            params = CommonFuns.getSearchParam(params, request, false);
            String deptId = CommonFuns.nullToString(params.get("deptId"));
            String[] searchdeptId=deptId.split(",");
            List<String> searchdeptIds = Arrays.asList(searchdeptId);
            JSONObject resultJson = commonInfoManager.hasPermission("YDGZ-GLY");
            if (resultJson.getBoolean("YDGZ-GLY") || resultJson.getBoolean(RdmConst.ALLDATA_QUERY_KEY)
                    || "admin".equals(ContextUtil.getCurrentUser().getUserNo())) {
                if (StringUtil.isNotEmpty(deptId)) {
                    params.put("deptId", searchdeptIds);
                }
            } else if (resultJson.getBoolean("FGZR")) {
                // 分管主任看分管所的数据，和自己参与的项目
                JSONObject paramJson = new JSONObject();
                paramJson.put("userId", ContextUtil.getCurrentUserId());
                paramJson.put("typeKey", "GROUP-USER-DIRECTOR");
                List<String> deptIds = commonInfoManager.getGroupIds(paramJson);
                if (StringUtil.isNotEmpty(deptId)) {
                    deptIds = new ArrayList<>();
                    deptIds.addAll(searchdeptIds);
                    params.put("deptId", deptIds);
                } else {
                    params.put("deptId", deptIds);
                }
                params.put("userId", ContextUtil.getCurrentUserId());
            } else if (resultJson.getBoolean("isLeader")) {
                // 组负责人 看本部门所有人参与的项目
                List<String> deptIds = new ArrayList<>();
                deptIds.add(ContextUtil.getCurrentUser().getMainGroupId());
                params.put("deptId", deptIds);
                params.put("userId", ContextUtil.getCurrentUserId());
                List<String> userIds = commonInfoDao.getUsersByDeptId(ContextUtil.getCurrentUser().getMainGroupId());
                params.put("userIds", userIds);
            } else {
                // 组负责人和普通人看本部门所有数据，以及别的部门有自己参与的项目
                List<String> deptIds = new ArrayList<>();
                deptIds.add(ContextUtil.getCurrentUser().getMainGroupId());
                params.put("deptId", deptIds);
                params.put("userId", ContextUtil.getCurrentUserId());
            }
            String ids = CommonFuns.nullToString(params.get("planIds"));
            if (StringUtil.isNotEmpty(ids)) {
                String[] idArr = ids.split(",", -1);
                List<String> idList = Arrays.asList(idArr);
                params.put("ids", idList);
            }
            result = monthWorkDao.getPlanLst(params);
            // 如果是审批的话，先将归口部门不为空，且不为自己的过滤掉
            if (params.get("apply") != null) {
                String FGLD = CommonFuns.nullToString(params.get("FGLD"));
                Boolean isFGLD = "true".equals(FGLD) ? true : false;
                Iterator<Map<String, Object>> iterator = result.iterator();
                while (iterator.hasNext()) {
                    String mainDeptId = ContextUtil.getCurrentUser().getMainGroupId();
                    Map<String, Object> map = iterator.next();
                    // 如果审批流程的话，到分管领导处，将内控级的去掉
                    if (isFGLD && "3".equals(CommonFuns.nullToString(map.get("isCompanyLevel")))) {
                        iterator.remove();
                    }
                    if (map.get("reportDeptId") == null || "".equals(map.get("reportDeptId"))) {
                        continue;
                    }
                    if (!mainDeptId.equals(map.get("reportDeptId").toString())) {
                        iterator.remove();
                    }
                }
            }
            int flag = 1;
            for (int i = 0; i < result.size(); i++) {
                Map<String, Object> map = result.get(i);
                if (i == 0) {
                    map.put("rowNum", flag);
                    flag++;
                } else {
                    String mainId = CommonFuns.nullToString(map.get("mainId"));
                    String preMainId = CommonFuns.nullToString(result.get(i - 1).get("mainId"));
                    if (mainId.equals(preMainId)) {
                        map.put("rowNum", result.get(i - 1).get("rowNum"));
                    } else {
                        map.put("rowNum", flag);
                        flag++;
                    }
                }
                // 实时查看项目进度状态
                String projectId = CommonFuns.nullToString(map.get("projectId"));
                String processStatus = queryProjectRisk(projectId, "");
                map.put("processStatus", processStatus);
            }
            convertDate(result);
        } catch (Exception e) {
            logger.error("Exception in 查询异常", e);
        }
        return result;
    }

    public JSONObject add(HttpServletRequest request) {
        JSONObject resultJson = new JSONObject();
        try {
            Map<String, String[]> parameters = request.getParameterMap();
            if (parameters == null || parameters.isEmpty()) {
                logger.error("表单内容为空！");
                return ResultUtil.result(false, "操作失败，表单内容为空！", "");
            }
            Map<String, Object> objBody = new HashMap<>(16);
            for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
                String mapKey = entry.getKey();
                String mapValue = entry.getValue()[0];
                if (mapKey.equals("yearMonth")) {
                    if (mapValue != null && !"".equals(mapValue)) {
                        mapValue = CommonFuns.convertDateToStr(new Date(mapValue), "yyyy-MM");
                    }
                }
                objBody.put(mapKey, mapValue);
            }
            String id = IdUtil.getId();
            objBody.put("id", id);
            objBody.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            objBody.put("asyncStatus", "0");
            resultJson.put("id", id);
            Boolean flag = getActTaskById(objBody);
            if (flag) {
                monthWorkDao.addObject(objBody);
            } else {
                return ResultUtil.result(false, "计划已审批，不允许新增计划！", "");
            }
        } catch (Exception e) {
            logger.error("Exception in add 添加异常！", e);
            return ResultUtil.result(false, "添加异常！", "");
        }
        return ResultUtil.result(true, "新增成功", resultJson);
    }

    public JSONObject update(HttpServletRequest request) {
        JSONObject resultJson = new JSONObject();
        try {
            Map<String, String[]> parameters = request.getParameterMap();
            Map<String, Object> objBody = new HashMap<>(16);
            for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
                String mapKey = entry.getKey();
                String mapValue = entry.getValue()[0];
                if (mapKey.equals("yearMonth")) {
                    if (mapValue != null && !"".equals(mapValue)) {
                        mapValue = CommonFuns.convertDateToStr(new Date(mapValue), "yyyy-MM");
                    }
                }
                objBody.put(mapKey, mapValue);
            }
            objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            monthWorkDao.updateObject(objBody);
            resultJson.put("id", objBody.get("id"));
        } catch (Exception e) {
            logger.error("Exception in update 更新异常", e);
            return ResultUtil.result(false, "更新异常！", "");
        }
        return ResultUtil.result(true, "更新成功", resultJson);
    }

    public void saveOrUpdateItem(String changeGridDataStr) {
        JSONObject resultJson = new JSONObject();
        try {
            JSONArray changeGridDataJson = JSONObject.parseArray(changeGridDataStr);
            for (int i = 0; i < changeGridDataJson.size(); i++) {
                JSONObject oneObject = changeGridDataJson.getJSONObject(i);
                String state = oneObject.getString("_state");
                String id = oneObject.getString("id");
                if ("added".equals(state) || StringUtils.isBlank(id)) {
                    // 新增
                    oneObject.put("id", IdUtil.getId());
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", new Date());
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", new Date());
                    monthWorkDao.addItem(oneObject);
                } else if ("modified".equals(state)) {
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", new Date());
                    monthWorkDao.updateItem(oneObject);
                } else if ("removed".equals(state)) {
                    // 删除
                    monthWorkDao.delItem(oneObject.getString("id"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("保存信息异常", e);
            return;
        }
    }

    public JSONObject remove(HttpServletRequest request) {
        JSONObject resultJson = new JSONObject();
        try {
            Map<String, Object> params = new HashMap<>(16);
            String ids = request.getParameter("ids");
            String[] idArr = ids.split(",", -1);
            List<String> idList = Arrays.asList(idArr);
            params.put("ids", idList);
            monthWorkDao.batchDelete(params);
            monthWorkDao.batchDeleteItems(params);
        } catch (Exception e) {
            logger.error("Exception in update 删除科技项目", e);
            return ResultUtil.result(false, "删除科技项目异常！", "");
        }
        return ResultUtil.result(true, "删除成功", resultJson);
    }

    public JSONObject removeItem(HttpServletRequest request) {
        JSONObject resultJson = new JSONObject();
        try {
            Map<String, Object> params = new HashMap<>(16);
            String ids = request.getParameter("ids");
            String[] idArr = ids.split(",", -1);
            List<String> idList = Arrays.asList(idArr);
            params.put("ids", idList);
            monthWorkDao.batchDeleteItemsById(params);
        } catch (Exception e) {
            logger.error("Exception in update 删除信息异常", e);
            return ResultUtil.result(false, "删除信息异常！", "");
        }
        return ResultUtil.result(true, "删除成功", resultJson);
    }

    public JSONObject getObjectById(String id) {
        JSONObject jsonObject = monthWorkDao.getObjectById(id);
        return jsonObject;
    }

    public List<JSONObject> getItemList(HttpServletRequest request) {
        List<JSONObject> resultArray = new ArrayList<>();
        try {
            String mainId = request.getParameter("mainId");
            JSONObject paramJson = new JSONObject();
            paramJson.put("mainId", mainId);
            resultArray = monthWorkDao.getItemList(paramJson);
        } catch (Exception e) {
            logger.error("Exception in getItemList", e);
        }
        return resultArray;
    }

    public void exportProjectPlanExcel(HttpServletRequest request, HttpServletResponse response) {
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String excelName = nowDate + "项目计划";
        Map<String, Object> params = new HashMap<>(16);
        params = CommonFuns.getSearchParam(params, request, false);
        String deptId = CommonFuns.nullToString(params.get("deptId"));
        String[] searchdeptId=deptId.split(",");
        List<String> searchdeptIds = Arrays.asList(searchdeptId);
        JSONObject resultJson = commonInfoManager.hasPermission("YDGZ-GLY");
        if (resultJson.getBoolean("YDGZ-GLY") || resultJson.getBoolean(RdmConst.ALLDATA_QUERY_KEY)
                || "admin".equals(ContextUtil.getCurrentUser().getUserNo())) {
            if (StringUtil.isNotEmpty(deptId)) {
                params.put("deptId", searchdeptIds);
            }
        } else if (resultJson.getBoolean("FGZR")) {
            // 分管主任看分管所的数据，和自己参与的项目
            JSONObject paramJson = new JSONObject();
            paramJson.put("userId", ContextUtil.getCurrentUserId());
            paramJson.put("typeKey", "GROUP-USER-DIRECTOR");
            List<String> deptIds = commonInfoManager.getGroupIds(paramJson);
            if (StringUtil.isNotEmpty(deptId)) {
                deptIds = new ArrayList<>();
                deptIds.addAll(searchdeptIds);
                params.put("deptId", deptIds);
            } else {
                params.put("deptId", deptIds);
            }
            params.put("userId", ContextUtil.getCurrentUserId());
        } else if (resultJson.getBoolean("isLeader")) {
            // 组负责人 看本部门所有人参与的项目
            List<String> deptIds = new ArrayList<>();
            deptIds.add(ContextUtil.getCurrentUser().getMainGroupId());
            params.put("deptId", deptIds);
            params.put("userId", ContextUtil.getCurrentUserId());
            List<String> userIds = commonInfoDao.getUsersByDeptId(ContextUtil.getCurrentUser().getMainGroupId());
            params.put("userIds", userIds);
        } else {
            // 组负责人和普通人看本部门所有数据，以及别的部门有自己参与的项目
            List<String> deptIds = new ArrayList<>();
            deptIds.add(ContextUtil.getCurrentUser().getMainGroupId());
            params.put("deptId", deptIds);
            params.put("userId", ContextUtil.getCurrentUserId());
        }
        List<Map<String, Object>> list = monthWorkDao.getPlanLst(params);
        convertDate(list);
        Map<String, Object> sfMap = commonInfoManager.genMap("YESORNO");
        Map<String, Object> levelMap = commonInfoManager.genMap("YDJH-JHJB");
        for (Map<String, Object> map : list) {
            if (map.get("isDelayApply") != null) {
                map.put("delayApplyText", sfMap.get(map.get("isDelayApply")));
            }
            if (map.get("isCompanyLevel") != null) {
                map.put("levelText", levelMap.get(map.get("isCompanyLevel")));
            }
        }
        String title = "项目计划";
        String[] fieldNames = {"项目名称", "项目编号", "年月", "项目起止日期", "项目阶段", "项目负责人","负责部门", "本月工作内容", "本月工作完成标志", "责任人", "配合部门",
            "计划级别", "完成情况", "备注", "是否提交延期申请"};
        String[] fieldCodes = {"projectName", "number", "yearMonth", "startEndDate", "stageName",
            "responseMan", "deptName","workContent", "finishFlag", "planResponseMan", "responseDeptId", "levelText",
            "finishStatusText", "remark", "delayApplyText"};
        HSSFWorkbook wbObj = ExcelUtils.exportMonthWorkExcel(list, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }

    public List<Map<String, Object>> reportCompanyPlan(HttpServletRequest request) {
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            Map<String, Object> params = new HashMap<>(16);
            params = CommonFuns.getSearchParam(params, request, false);
            String yearMonthStart = CommonFuns.nullToString(params.get("yearMonthStart"));
            String yearMonthEnd = CommonFuns.nullToString(params.get("yearMonthEnd"));
            JSONObject paramJson = new JSONObject();
            paramJson.put("yearMonthStart", yearMonthStart);
            paramJson.put("yearMonthEnd", yearMonthEnd);
            result = monthWorkDao.reportCompanyPlan(paramJson);
            int flag = 1;
            for (int i = 0; i < result.size(); i++) {
                Map<String, Object> map = result.get(i);
                if (i == 0) {
                    map.put("rowNum", flag);
                    flag++;
                } else {
                    String mainId = CommonFuns.nullToString(map.get("mainId"));
                    String preMainId = CommonFuns.nullToString(result.get(i - 1).get("mainId"));
                    if (mainId.equals(preMainId)) {
                        map.put("rowNum", result.get(i - 1).get("rowNum"));
                    } else {
                        map.put("rowNum", flag);
                        flag++;
                    }
                }
            }
            convertDate(result);
        } catch (Exception e) {
            logger.error("Exception in 查询异常", e);
        }
        return result;
    }

    public JSONObject reportCompanyPlanNum(HttpServletRequest request) {
        JSONObject resultJson = new JSONObject();
        try {
            String yearMonthStart = request.getParameter("yearMonthStart");
            String yearMonthEnd = request.getParameter("yearMonthEnd");
            String finishStatus = request.getParameter("finishStatus");
            String isCompanyLevel = request.getParameter("isCompanyLevel");
            String isShow = request.getParameter("isShow");
            JSONObject paramJson = new JSONObject();
            paramJson.put("yearMonthStart", yearMonthStart);
            paramJson.put("yearMonthEnd", yearMonthEnd);
            paramJson.put("finishStatus", finishStatus);
            paramJson.put("isCompanyLevel", isCompanyLevel);
            paramJson.put("isShow", isShow);
            List<JSONObject> planList = monthWorkDao.reportCompanyPlanNum(paramJson);
            int dw = 0, zw = 0, xw = 0, xml = 0, gjh = 0, zk = 0, bzh = 0, fz = 0, cs = 0, fwgc = 0, gczx = 0,
                lbjchp = 0, jsglb = 0, lsjs = 0;
            for (JSONObject temp : planList) {
                if (temp.getString("deptId") != null) {
                    switch (temp.getString("deptId")) {
                        case "161416982793249239":
                            dw = temp.getInteger("totalNum");
                            break;
                        case "161416982793248778":
                            zw = temp.getInteger("totalNum");
                            break;
                        case "161416982793248776":
                            xw = temp.getInteger("totalNum");
                            break;
                        case "618713695671535233":
                            xml = temp.getInteger("totalNum");
                            break;
                        case "161416982793249236":
                            gjh = temp.getInteger("totalNum");
                            break;
                        case "87212403321741356":
                            zk = temp.getInteger("totalNum");
                            break;
                        case "618713695671535250":
                            bzh = temp.getInteger("totalNum");
                            break;
                        case "618713695671535248":
                            fz = temp.getInteger("totalNum");
                            break;
                        case "161416982793249241":
                            cs = temp.getInteger("totalNum");
                            break;
                        case "161416982793249243":
                            gczx = temp.getInteger("totalNum");
                            break;
                        case "618713695671535246":
                            fwgc = temp.getInteger("totalNum");
                            break;
                        case "90063265173946371":
                            jsglb = temp.getInteger("totalNum");
                            break;
                        case "1397235874523888454":
                            lsjs = temp.getInteger("totalNum");
                            break;
                        case "1671900218515762300":
                            lbjchp = temp.getInteger("totalNum");
                            break;
                    }
                }
            }
            resultJson.put("dw", dw);
            resultJson.put("zw", zw);
            resultJson.put("xw", xw);
            resultJson.put("xml", xml);
            resultJson.put("gjh", gjh);
            resultJson.put("zk", zk);
            resultJson.put("bzh", bzh);
            resultJson.put("fz", fz);
            resultJson.put("cs", cs);
            resultJson.put("gczx", gczx);
            resultJson.put("fwgc", fwgc);
            resultJson.put("fwgc", fwgc);
            resultJson.put("jsglb", jsglb);
            resultJson.put("lsjs", lsjs);
            resultJson.put("lbjchp", lsjs);

        } catch (Exception e) {
            logger.error("Exception in reportCompanyPlanNum", e);
            return ResultUtil.result(false, "获取完成情况统计表异常！", resultJson);
        }
        return ResultUtil.result(true, "", resultJson);
    }

    public JSONObject reportPlanNum(HttpServletRequest request) {
        JSONObject resultJson = new JSONObject();
        try {
            String yearMonthStart = request.getParameter("yearMonthStart");
            String yearMonthEnd = request.getParameter("yearMonthEnd");
            JSONObject paramJson = new JSONObject();
            paramJson.put("yearMonthStart", yearMonthStart);
            paramJson.put("yearMonthEnd", yearMonthEnd);
            paramJson.put("isShow", "isShow");
            List<JSONObject> planList = monthWorkDao.reportPlanNum(paramJson);
            dealPlanData(planList, resultJson, "plan");
            List<JSONObject> unPlanList = monthWorkDao.reportUnPlanNum(paramJson);
            dealPlanData(unPlanList, resultJson, "unPlan");
            List<JSONObject> taskList = monthWorkDao.reportTaskNum(paramJson);
            dealPlanData(taskList, resultJson, "task");
            List<JSONObject> totalList = monthWorkDao.reportCompanyPlanNum(paramJson);
            int allPlan = 0;
            for (JSONObject temp : totalList) {
                allPlan += temp.getIntValue("totalNum");
            }
            dealPlanData(totalList, resultJson, "total");
            paramJson.put("finishStatus", "1");
            planList = monthWorkDao.reportPlanNum(paramJson);
            dealPlanData(planList, resultJson, "planFinish");
            unPlanList = monthWorkDao.reportUnPlanNum(paramJson);
            dealPlanData(unPlanList, resultJson, "unPlanFinish");
            taskList = monthWorkDao.reportTaskNum(paramJson);
            dealPlanData(taskList, resultJson, "taskFinish");
            totalList = monthWorkDao.reportCompanyPlanNum(paramJson);
            int allUnFinishPlan = 0;
            for (JSONObject temp : totalList) {
                allUnFinishPlan += temp.getIntValue("totalNum");
            }
            double ratio = 100;
            if (allPlan != 0) {
                ratio = (double)(allPlan - allUnFinishPlan) / allPlan * 100;
            }
            resultJson.put("ratio", ratio);
            dealPlanData(totalList, resultJson, "totalFinish");
        } catch (Exception e) {
            logger.error("Exception in reportCompanyPlanNum", e);
            return ResultUtil.result(false, "获取完成情况统计表异常！", resultJson);
        }
        return ResultUtil.result(true, "", resultJson);
    }

    public void dealPlanData(List<JSONObject> list, JSONObject resultJson, String planType) {
        try {
            int dw = 0, zw = 0, xw = 0, xml = 0, gjh = 0, zk = 0, bzh = 0, fz = 0, cs = 0, fwgc = 0, gczx = 0,
                jsglb = 0, lsjs = 0, lbjcp = 0;
            for (JSONObject temp : list) {
                if (temp.getString("deptId") != null) {
                    switch (temp.getString("deptId")) {
                        case "161416982793249239":
                            dw = temp.getInteger("totalNum");
                            break;
                        case "161416982793248778":
                            zw = temp.getInteger("totalNum");
                            break;
                        case "161416982793248776":
                            xw = temp.getInteger("totalNum");
                            break;
                        case "618713695671535233":
                            xml = temp.getInteger("totalNum");
                            break;
                        case "161416982793249236":
                            gjh = temp.getInteger("totalNum");
                            break;
                        case "87212403321741356":
                            zk = temp.getInteger("totalNum");
                            break;
                        case "618713695671535250":
                            bzh = temp.getInteger("totalNum");
                            break;
                        case "618713695671535248":
                            fz = temp.getInteger("totalNum");
                            break;
                        case "161416982793249241":
                            cs = temp.getInteger("totalNum");
                            break;
                        case "161416982793249243":
                            gczx = temp.getInteger("totalNum");
                            break;
                        case "618713695671535246":
                            fwgc = temp.getInteger("totalNum");
                            break;
                        case "90063265173946371":
                            jsglb = temp.getInteger("totalNum");
                            break;
                        case "1397235874523888454":
                            lsjs = temp.getInteger("totalNum");
                            break;
                        case "1671900218515762300":
                            lbjcp = temp.getInteger("totalNum");
                            break;
                    }
                }
            }
            resultJson.put("dw" + planType, dw);
            resultJson.put("zw" + planType, zw);
            resultJson.put("xw" + planType, xw);
            resultJson.put("xml" + planType, xml);
            resultJson.put("gjh" + planType, gjh);
            resultJson.put("zk" + planType, zk);
            resultJson.put("bzh" + planType, bzh);
            resultJson.put("fz" + planType, fz);
            resultJson.put("cs" + planType, cs);
            resultJson.put("gczx" + planType, gczx);
            resultJson.put("fwgc" + planType, fwgc);
            resultJson.put("jsglb" + planType, jsglb);
            resultJson.put("lsjs" + planType, lsjs);
            resultJson.put("lbjcp" + planType, lbjcp);
        } catch (Exception e) {
            logger.error("Exception in dealPlanData", e);
        }
    }

    public JSONObject copyPlan(HttpServletRequest request) {
        JSONObject resultJson = new JSONObject();
        try {
            Map<String, Object> params = new HashMap<>(16);
            String ids = request.getParameter("ids");
            String yearMonth = request.getParameter("yearMonth");
            String[] idArr = ids.split(",", -1);
            JSONObject map;
            String newId = "";
            for (String id : idArr) {
                // 1.先复制主表信息
                map = monthWorkDao.getObjectById(id);
                String projectCode = map.getString("projectCode");
                if (StringUtil.isEmpty(projectCode)) {
                    return ResultUtil.result(false, "项目编号不存在，不允许复制！", resultJson);
                }

                JSONObject paramJson = new JSONObject();
                paramJson.put("projectCode", projectCode);
                paramJson.put("yearMonth", yearMonth);
                JSONObject planJson = monthWorkDao.getPlanByCodeAndMonth(paramJson);
                if (planJson == null) {
                    return ResultUtil.result(false, "选中的月份计划尚未生成，不允许复制！", resultJson);
                }
                // 2.复制子表信息
                List<Map<String, Object>> list = monthWorkDao.getItemMapList(id);
                for (Map<String, Object> tempMap : list) {
                    tempMap.put("id", IdUtil.getId());
                    tempMap.put("mainId", planJson.getString("id"));
                    tempMap.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    tempMap.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                    tempMap.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    tempMap.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                    monthWorkDao.addItem(tempMap);
                }
            }
        } catch (Exception e) {
            logger.error("Exception in update 复制计划异常", e);
            return ResultUtil.result(false, "复制计划异常！", "");
        }
        return ResultUtil.result(true, "复制成功", resultJson);
    }

    public Boolean getActTaskById(Map<String, Object> objBody) {
        // 如果是月度工作管理员，则直接 返回true
        JSONObject adminJson = commonInfoManager.hasPermission("YDGZ-GLY");
        if (adminJson.getBoolean("YDGZ-GLY")) {
            return true;
        }
        List<JSONObject> list = new ArrayList<>();
        list = monthWorkApplyDao.getPlanDeptMonthApply(objBody);
        boolean flag = true;
        if (list != null && list.size() == 0) {
            flag = true;
        } else {
            for (JSONObject temp : list) {
                String applyId = temp.getString("id");
                JSONObject jsonObject = monthWorkApplyDao.getActTaskById(applyId);
                if (!"调度员发起审批".equals(jsonObject.getString("actName"))
                    && !"SUCCESS_END".equals(jsonObject.getString("processStatus"))) {
                    flag = false;
                    break;
                }
            }
        }
        return flag;
    }

    public List<Map<String, Object>> getPersonPlanList(HttpServletRequest request) {
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            Map<String, Object> params = new HashMap<>(16);
            params = CommonFuns.getSearchParam(params, request, false);
            params.put("userId", ContextUtil.getCurrentUserId());
            result = monthWorkDao.getPersonPlanList(params);
            // 如果是审批的话，先将归口部门不为空，且不为自己的过滤掉
            int flag = 1;
            for (int i = 0; i < result.size(); i++) {
                Map<String, Object> map = result.get(i);
                if (i == 0) {
                    map.put("rowNum", flag);
                    flag++;
                } else {
                    String mainId = CommonFuns.nullToString(map.get("mainId"));
                    String preMainId = CommonFuns.nullToString(result.get(i - 1).get("mainId"));
                    if (mainId.equals(preMainId)) {
                        map.put("rowNum", result.get(i - 1).get("rowNum"));
                    } else {
                        map.put("rowNum", flag);
                        flag++;
                    }
                }
                map.put("processStatus", map.get("processStatus"));
            }
            convertDate(result);
        } catch (Exception e) {
            logger.error("Exception in 查询异常", e);
        }
        return result;
    }

    // 计算项目风险,没有风险是0，延迟5天之内是1，延迟大于5天是2，不涉及是3,没有计划时间且延误超过30天是4
    public String queryProjectRisk(String projectId, String endTime) throws ParseException {
        int hasRisk = 0;
        String processStatus = "项目未延误";
        JSONObject oneProject = monthWorkDao.getProjectById(projectId);

        if (oneProject == null) {
            return processStatus;
        }
        // 当前的风险
        if (StringUtils.isBlank(endTime)) {
            if ("DRAFTED".equalsIgnoreCase(CommonFuns.nullToString(oneProject.get("status")))) {
                hasRisk = 3;
            } else if ("SUCCESS_END".equalsIgnoreCase(CommonFuns.nullToString(oneProject.get("status")))) {
                hasRisk = 0;
            } else {
                hasRisk = 0;
                Map<String, Object> params = new HashMap<>(16);
                params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
                params.put("projectId", CommonFuns.nullToString(oneProject.get("projectId")));
                params.put("currentStageNo", CommonFuns.nullToString(oneProject.get("currentStageNo")));
                List<Map<String, Object>> progressInfos = xcmgProjectReportDao.projectProgressReport(params);
                if (progressInfos != null && !progressInfos.isEmpty()) {
                    // 只有正常运行的，才需要判断延误风险
                    if ("RUNNING".equalsIgnoreCase(CommonFuns.nullToString(oneProject.get("status")))) {
                        Map<String, Object> current = progressInfos.get(progressInfos.size() - 1);
                        Date planEndTime = (Date)current.get("planEndTime");
                        long currentTime =
                            DateFormatUtil.parseDate(XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd")).getTime();
                        if (planEndTime != null) {
                            String projectRiskPardonMillSecStr = WebAppUtil.getProperty("projectRiskPardonMillSec");
                            if (StringUtils.isBlank(projectRiskPardonMillSecStr)) {
                                projectRiskPardonMillSecStr = "432000000";
                            }
                            long delayPardonTime = Long.parseLong(projectRiskPardonMillSecStr);
                            if (currentTime - planEndTime.getTime() > delayPardonTime) {
                                hasRisk = 2;
                            } else if (currentTime - planEndTime.getTime() <= delayPardonTime
                                && currentTime - planEndTime.getTime() > 0) {
                                hasRisk = 1;
                            }
                        } else {
                            // 计划结束时间为空且延误超过30天设置为有风险
                            List<Map<String, Object>> tempProject = CommonUtil.convertJSONObject2MapObj(oneProject);
                            rdmZhglUtil.setTaskCreateTimeInfo(tempProject,true);
                            long createTime =
                                DateFormatUtil.parseDate(tempProject.get(0).get("taskCreateTime").toString()).getTime();
                            if (currentTime - createTime > 2592000000L) {
                                hasRisk = 4;
                            }
                        }
                    } else {
                        hasRisk = 3;
                    }
                }
                oneProject.put("hasRisk", hasRisk);
            }
        } else {
            hasRisk = 0;
        }

        if (hasRisk == 1) {
            processStatus = "项目延误时间未超过5天";
        } else if (hasRisk == 2) {
            processStatus = "项目延误时间超过5天";
        } else if (hasRisk == 3) {
            processStatus = "项目未启动或已停止";
        } else if (hasRisk == 4) {
            processStatus = "未填写计划时间且延误超过30天";
        }
        return processStatus;
    }

    public List<Map<String, Object>> getDeptUnFinishList(HttpServletRequest request) {
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            Map<String, Object> params = new HashMap<>(16);
            params = CommonFuns.getSearchParam(params, request, false);
            result = monthWorkDao.getDeptUnFinishList(params);
            // 如果是审批的话，先将归口部门不为空，且不为自己的过滤掉
            int flag = 1;
            for (int i = 0; i < result.size(); i++) {
                Map<String, Object> map = result.get(i);
                if (i == 0) {
                    map.put("rowNum", flag);
                    flag++;
                } else {
                    String mainId = CommonFuns.nullToString(map.get("mainId"));
                    String preMainId = CommonFuns.nullToString(result.get(i - 1).get("mainId"));
                    if (mainId.equals(preMainId)) {
                        map.put("rowNum", result.get(i - 1).get("rowNum"));
                    } else {
                        map.put("rowNum", flag);
                        flag++;
                    }
                }
            }
            convertDate(result);
        } catch (Exception e) {
            logger.error("Exception in 查询异常", e);
        }
        return result;
    }

    public void fileToZip(String filePath, ZipOutputStream zipOutputStream, String fileName) throws IOException {
        // 需要压缩的文件
        File file = new File(filePath);
        FileInputStream fileInputStream = new FileInputStream(filePath);
        // 缓冲
        byte[] bufferArea = new byte[1024 * 10];
        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream, 1024 * 10);
        // 将当前文件作为一个zip实体写入压缩流，fileName代表压缩文件中的文件名称
        zipOutputStream.putNextEntry(new ZipEntry(fileName));
        int length = 0;
        while ((length = bufferedInputStream.read(bufferArea, 0, 1024 * 10)) != -1) {
            zipOutputStream.write(bufferArea, 0, length);
        }
        // 关闭流
        fileInputStream.close();
        bufferedInputStream.close();
    }

    public String getDeptId(String deptName) {
        String deptId = "";
        switch (deptName) {
            case "大挖":
                deptId = "161416982793249239";
                break;
            case "中挖":
                deptId = "161416982793248778";
                break;
            case "小挖":
                deptId = "161416982793248776";
                break;
            case "特挖产品研究所":
                deptId = "618713695671535233";
                break;
            case "国际":
                deptId = "161416982793249236";
                break;
            case "智控":
                deptId = "87212403321741356";
                break;
            case "标准所":
                deptId = "618713695671535250";
                break;
            case "仿真":
                deptId = "618713695671535248";
                break;
            case "测试":
                deptId = "161416982793249241";
                break;
            case "工程中心":
                deptId = "161416982793249243";
                break;
            case "服务工程":
                deptId = "618713695671535246";
                break;
            case RdmConst.JSGLB_NAME:
                deptId = "90063265173946371";
                break;
            case "微挖":
                deptId = "995326785014989631";
                break;
            case "绿色技术":
                deptId = "1397235874523888454";
                break;
            case "零部件产品":
                deptId = "1671900218515762300";
                break;
        }
        return deptId;
    }

}
