package com.redxun.xcmgJsjl.core.manager;

import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.materielextend.core.dao.MaterielApplyDao;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.xcmgJsjl.core.dao.XcmgJsjlDao;
import com.redxun.xcmgJsjl.core.dao.XcmgJsjlFileDao;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;

@Service
public class XcmgJsjlManager {
    private static Logger logger = LoggerFactory.getLogger(XcmgJsjlManager.class);
    @Autowired
    private XcmgJsjlDao xcmgJsjlDao;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private MaterielApplyDao materielApplyDao;
    @Autowired
    private XcmgJsjlFileDao xcmgJsjlFileDao;
    @Autowired
    private XcmgJsjlFileManager xcmgJsjlFileManager;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;

    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        getListParams(params, request);
        // 增加角色过滤的条件
        String currentUserId = ContextUtil.getCurrentUserId();
        String currentUserNo = ContextUtil.getCurrentUser().getUserNo();
        // 管理专员
        boolean hyglzy = rdmZhglUtil.judgeIsPointRoleUser(ContextUtil.getCurrentUserId(), RdmConst.HYGLZY);
        // 1、admin查看所有，因此不附加条件
        if (!currentUserNo.equalsIgnoreCase("admin")) {
            params.put("currentUserId", currentUserId);
            // 2、分管领导、管理专员、数据特权人员，也能看所有数据（不考虑草稿）。使用“fgld”参数代表
            Map<String, Object> queryUserParam = new HashMap<>();
            queryUserParam.put("userId", currentUserId);
            queryUserParam.put("groupName", "分管领导");
            List<Map<String, Object>> queryRoleResult = xcmgProjectOtherDao.queryUserRoles(queryUserParam);
            boolean isZjl =
                rdmZhglUtil.judgeIsPointRoleUser(ContextUtil.getCurrentUserId(), RdmConst.AllDATA_QUERY_NAME);
            if ((queryRoleResult != null && !queryRoleResult.isEmpty()) || isZjl
                || hyglzy) {
                params.put("roleName", "fgld");
            } else {
                queryUserParam.clear();
                queryUserParam.put("userId", currentUserId);
                List<String> typeKeyList = new ArrayList<>();
                typeKeyList.add("GROUP-USER-LEADER");
                typeKeyList.add("GROUP-USER-DIRECTOR");
                queryUserParam.put("typeKeyList", typeKeyList);
                List<Map<String, Object>> queryDeptResult = xcmgProjectOtherDao.queryUserDeps(queryUserParam);
                if (queryDeptResult == null || queryDeptResult.isEmpty()) {
                    params.put("roleName", "ptyg");
                } else {
                    // 3、分管主任或部门负责人，查看自己负责部门的、自己创建的、自己参会的，使用“fgzr”参数代表（不考虑草稿）
                    params.put("roleName", "fgzr");
                    judgeUserDeptRole(queryDeptResult, params);
                }
            }
        }

        List<JSONObject> jsjlDataList = xcmgJsjlDao.dataListQuery(params);
        int countJsjlDataList = xcmgJsjlDao.countDataListQuery(params);
        if (jsjlDataList != null && !jsjlDataList.isEmpty()) {
            Set<String> userIdSet = new HashSet<>();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (JSONObject oneObj : jsjlDataList) {
                if (oneObj.getDate("CREATE_TIME_") != null) {
                    oneObj.put("CREATE_TIME_", simpleDateFormat.format(oneObj.getDate("CREATE_TIME_")));
                }
                if (StringUtils.isNotBlank(oneObj.getString("meetingUserIds"))) {
                    userIdSet.addAll(Arrays.asList(oneObj.getString("meetingUserIds").split(",", -1)));
                }
                /*// 获取附件名称列表
                List<String> FileNamesList = xcmgJsjlDao.getFileNamesListByMainId(oneObj.getString("id"));
                // 如果有附件名称
                if (FileNamesList.size() > 0) {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (String fileName : FileNamesList) {
                        stringBuilder.append("《").append(fileName).append("》");
                    }
                    oneObj.put("FileNames", stringBuilder.toString());
                }*/
            }
            if (!userIdSet.isEmpty()) {
                Map<String, String> userId2Name = queryUserNameByIds(userIdSet);
                for (JSONObject oneObj : jsjlDataList) {
                    if (StringUtils.isNotBlank(oneObj.getString("meetingUserIds"))) {
                        List<String> userIds = Arrays.asList(oneObj.getString("meetingUserIds").split(",", -1));
                        String meetingUserNames = toGetUserNamesByIds(userId2Name, userIds);
                        oneObj.put("meetingUserNames", meetingUserNames);
                    }
                }

            }
        }

        result.setData(jsjlDataList);
        result.setTotal(countJsjlDataList);
        return result;
    }

    private String toGetUserNamesByIds(Map<String, String> userId2Name, List<String> userIdList) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String userId : userIdList) {
            if (userId2Name.containsKey(userId)) {
                stringBuilder.append(userId2Name.get(userId)).append("，");
            }
        }
        if (stringBuilder.length() > 0) {
            return stringBuilder.substring(0, stringBuilder.length() - 1);
        }
        return "";
    }

    private Map<String, String> queryUserNameByIds(Set<String> userIds) {
        Map<String, String> userId2Name = new HashMap<>();
        if (userIds == null || userIds.isEmpty()) {
            return userId2Name;
        }
        List<JSONObject> userInfos = materielApplyDao.queryUserInfosByIds(new ArrayList<>(userIds));
        if (userInfos == null || userInfos.isEmpty()) {
            return userId2Name;
        }
        for (JSONObject oneUserInfo : userInfos) {
            userId2Name.put(oneUserInfo.getString("userId"), oneUserInfo.getString("userName"));
        }
        return userId2Name;
    }

    // 判断用户是否是分管主任或者部门负责人，并抽取对应的部门
    private void judgeUserDeptRole(List<Map<String, Object>> queryDeptResult, Map<String, Object> params) {
        Set<String> deptIdList = new HashSet<>();
        for (Map<String, Object> oneDept : queryDeptResult) {
            String groupId = oneDept.get("PARTY1_").toString();
            deptIdList.add(groupId);
        }
        params.put("deptIds", deptIdList);
    }

    private void getListParams(Map<String, Object> params, HttpServletRequest request) {
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)) {
            params.put("sortField", sortField);
            params.put("sortOrder", sortOrder);
        }
        if (StringUtils.isEmpty(sortField)) {
            params.put("sortField", "communicateTime");
            params.put("sortOrder", "desc");
        }
        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    // 数据库中存储的时间是UTC时间，因此需要将前台传递的北京时间转化
                    // if ("communicateStartTime".equalsIgnoreCase(name)) {
                    // value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), -8));
                    // }
                    // if ("communicateEndTime".equalsIgnoreCase(name)) {
                    // value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), 16));
                    // }
                    params.put(name, value);
                }
            }
        }

        // 增加分页条件
        params.put("startIndex", 0);
        params.put("pageSize", 20);
        String pageIndex = request.getParameter("pageIndex");
        String pageSize = request.getParameter("pageSize");
        if (StringUtils.isNotBlank(pageIndex) && StringUtils.isNotBlank(pageSize)) {
            params.put("startIndex", Integer.parseInt(pageSize) * Integer.parseInt(pageIndex));
            params.put("pageSize", Integer.parseInt(pageSize));
        }
        String scene = request.getParameter("scene");
        if (StringUtils.isNotBlank(scene)) {
            params.put("scene", scene);
        }
    }

    /**
     * 保存草稿或者提交技术交流的数据
     *
     * @param result
     * @param jsjlDataStr
     */
    public void saveOrCommitJsjlData(JsonResult result, String jsjlDataStr) {
        JSONObject jsjlObj = JSONObject.parseObject(jsjlDataStr);
        if (jsjlObj == null || jsjlObj.isEmpty()) {
            result.setSuccess(false);
            result.setMessage("表单内容为空，操作失败！");
            return;
        }
        // 草稿recordStatus不是已提交，不执行取号。
        if (jsjlObj.getString("recordStatus").equalsIgnoreCase("已提交") && jsjlObj.getBooleanValue("generationNo")) {
            // todo:改了
            if (jsjlObj.getString("meetingNo").equalsIgnoreCase("")) {
                jsjlObj.put("meetingNo", this.genSequenceNoJsjl(jsjlObj, "MinutesExternal"));
            } else if (!getDateString(jsjlObj.getString("communicateTime").substring(0, 10))
                .equalsIgnoreCase(jsjlObj.getString("meetingNo").substring(4, 12))) {
                jsjlObj.put("meetingNo", this.genSequenceNoJsjl(jsjlObj, "MinutesExternal"));
            }
        }
        if (StringUtils.isBlank(jsjlObj.getString("id"))) {
            jsjlObj.put("id", IdUtil.getId());
            jsjlObj.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            jsjlObj.put("CREATE_TIME_", new Date());
            xcmgJsjlDao.insertJsjlData(jsjlObj);
            result.setData(jsjlObj.getString("id"));
        } else {
            jsjlObj.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            jsjlObj.put("UPDATE_TIME_", new Date());
            xcmgJsjlDao.updateJsjlData(jsjlObj);
            result.setData(jsjlObj.getString("id"));
        }
    }

    // ..生成会议编号
    private synchronized String genSequenceNoJsjl(JSONObject meetingObj, String key) {
        String dateStr = getDateString(meetingObj.getString("communicateTime").substring(0, 10));
        if (StringUtils.isBlank(dateStr)) {
            return "";
        }
        String tempStr = meetingObj.getString("meetingModelDescp") + dateStr + "-";
        JSONObject params = new JSONObject();
        params.put("tempStr", tempStr);
        JSONObject seq = xcmgJsjlDao.getSeqJsjl(params);
        if (seq == null) {
            return tempStr + "1";
        } else {
            return tempStr + (Integer.parseInt(seq.getString("num")) + 1);
        }
    }

    // ..获取时间字符yyyyMMdd
    private String getDateString(String dateString) {
        String[] arrNow;
        if (dateString == null) {
            arrNow = DateUtil.formatDate(new Date(), DateUtil.DATE_FORMAT_YMD).split("-");
        } else {
            arrNow = dateString.split("-");
        }

        StringBuffer sb = new StringBuffer();
        for (String a : arrNow) {
            sb.append(a);
        }
        return sb.toString();
    }

    /**
     * 根据id查询表单详情
     *
     * @param jsjlId
     * @return
     */
    public JSONObject queryJsjlById(String jsjlId) {
        JSONObject result = new JSONObject();
        if (StringUtils.isBlank(jsjlId)) {
            return result;
        }
        JSONObject jsjlObj = xcmgJsjlDao.queryJsjlById(jsjlId);
        if (jsjlObj == null) {
            return result;
        }
        Set<String> userIdSet = new HashSet<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (jsjlObj.getDate("CREATE_TIME_") != null) {
            jsjlObj.put("CREATE_TIME_", simpleDateFormat.format(jsjlObj.getDate("CREATE_TIME_")));
        }
        if (StringUtils.isNotBlank(jsjlObj.getString("meetingUserIds"))) {
            userIdSet.addAll(Arrays.asList(jsjlObj.getString("meetingUserIds").split(",", -1)));
        }
        if (!userIdSet.isEmpty()) {
            Map<String, String> userId2Name = queryUserNameByIds(userIdSet);
            if (StringUtils.isNotBlank(jsjlObj.getString("meetingUserIds"))) {
                List<String> userIds = Arrays.asList(jsjlObj.getString("meetingUserIds").split(",", -1));
                String meetingUserNames = toGetUserNamesByIds(userId2Name, userIds);
                jsjlObj.put("meetingUserNames", meetingUserNames);
            }
        }
        return jsjlObj;
    }

    /**
     * 删除技术交流数据，同时删除对应的数据库和磁盘中的文件
     *
     * @param ids
     * @return
     */
    public JsonResult deleteJsjl(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        Map<String, Object> param = new HashMap<>();
        param.put("jsjlIds", Arrays.asList(ids));
        List<JSONObject> files = xcmgJsjlFileDao.queryFilesByJsjlIds(param);
        for (JSONObject oneFile : files) {
            xcmgJsjlFileManager.deleteOneFileFromDisk(oneFile.getString("id"), oneFile.getString("fileName"),
                oneFile.getString("jsjlId"));
        }
        for (String oneJsjlId : ids) {
            xcmgJsjlFileManager.deleteJsjlDirFromDisk(oneJsjlId);
            xcmgJsjlDao.deleteMeetingPlanByMeetingId(oneJsjlId);
        }

        xcmgJsjlFileDao.deleteFileByJsjlIds(param);
        xcmgJsjlDao.deleteJsjl(param);
        return result;
    }

    // ..
    public List<JSONObject> getMeetingPlanList(String meetingId) {
        List<JSONObject> meetingPlanList = xcmgJsjlDao.queryMeetingPlanList(meetingId);
        Set<String> userIdSet = new HashSet<>();
        for (JSONObject jsonObject : meetingPlanList) {
            if (StringUtils.isNotBlank(jsonObject.getString("meetingPlanRespUserIds"))) {
                userIdSet.addAll(Arrays.asList(jsonObject.getString("meetingPlanRespUserIds").split(",", -1)));
            }
        }
        if (!userIdSet.isEmpty()) {
            Map<String, String> userId2Name = queryUserNameByIds(userIdSet);
            for (JSONObject jsonObject : meetingPlanList) {
                if (StringUtils.isNotBlank(jsonObject.getString("meetingPlanRespUserIds"))) {
                    List<String> userIds = Arrays.asList(jsonObject.getString("meetingPlanRespUserIds").split(",", -1));
                    String meetingUserNames = toGetUserNamesByIds(userId2Name, userIds);
                    jsonObject.put("meetingPlanRespUserIds_name", meetingUserNames);
                }
            }
        }
        return meetingPlanList;
    }

    // ..
    public void saveMeetingPlan(JsonResult result, String meetingPlanDataStr, String meetingId) {
        JSONArray meetingPlanObjs = JSONObject.parseArray(meetingPlanDataStr);
        if (meetingPlanObjs == null || meetingPlanObjs.isEmpty()) {
            result.setSuccess(false);
            result.setMessage("表单内容为空，操作失败！");
            return;
        }
        for (Object object : meetingPlanObjs) {
            JSONObject meetingPlanObj = (JSONObject)object;
            if (StringUtils.isBlank(meetingPlanObj.getString("id"))) {
                meetingPlanObj.put("id", IdUtil.getId());
                meetingPlanObj.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                meetingPlanObj.put("CREATE_TIME_", new Date());
                meetingPlanObj.put("meetingPlanEndTime",
                    meetingPlanObj.getString("meetingPlanEndTime").substring(0, 10));
                xcmgJsjlDao.insertMeetingPlanData(meetingPlanObj);
            } else {
                meetingPlanObj.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                meetingPlanObj.put("UPDATE_TIME_", new Date());
                meetingPlanObj.put("meetingPlanEndTime",
                    meetingPlanObj.getString("meetingPlanEndTime").substring(0, 10));
                xcmgJsjlDao.updateMeetingPlanData(meetingPlanObj);
            }
        }
        // 查询会议最新的任务中的主责人，分隔去重后，更新回主表中
        updateOneMeetingPlanRespUserIds(meetingId);
    }

    // ..
    public JsonResult deleteOneMeetingPlan(String id, String meetingId) {
        JsonResult result = new JsonResult(true, "删除成功！");
        xcmgJsjlDao.deleteOneMeetingPlan(id);
        // 查询会议最新的任务中的主责人，分隔去重后，更新回主表中
        updateOneMeetingPlanRespUserIds(meetingId);
        return result;
    }

    // 会议任务变化后，查询会议最新的任务中的主责人，分隔去重后，更新回主表中
    public void updateOneMeetingPlanRespUserIds(String meetingId) {
        if(StringUtils.isBlank(meetingId)) {
            return;
        }
        Set<String> userIdSet = new HashSet<>();
        List<JSONObject> meetingPlanList = xcmgJsjlDao.queryMeetingPlanList(meetingId);
        for (JSONObject jsonObject : meetingPlanList) {
            if (StringUtils.isNotBlank(jsonObject.getString("meetingPlanRespUserIds"))) {
                userIdSet.addAll(Arrays.asList(jsonObject.getString("meetingPlanRespUserIds").split(",", -1)));
            }
        }
        if (!userIdSet.isEmpty()) {
            JSONObject param = new JSONObject();
            param.put("planRespUserIds", String.join(",", userIdSet));
            param.put("id", meetingId);
            xcmgJsjlDao.updateMeetingPlanRespUserIds(param);
        }
    }

    // ..
    public void exportPlanExcel(HttpServletRequest request, HttpServletResponse response) {
        String filterParams = request.getParameter("filter");
        String jsjlId = "";
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            jsjlId = jsonArray.getJSONObject(0).getString("value");
        }
        List<JSONObject> listDataJson = getMeetingPlanList(jsjlId);
        List<Map<String, Object>> listData = new ArrayList<>();
        for (JSONObject jsonObject : listDataJson) {
            listData.add(jsonObject.getInnerMap());
        }

        for (Map<String, Object> oneMap : listData) {
            oneMap.put("isComplete", oneMap.get("isComplete").toString().equalsIgnoreCase("true") ? "是" : "否");
        }
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        JSONObject jsonObject = queryJsjlById(jsjlId);
        String title = "外部会议纪要执行列表，会议主题：" + jsonObject.getString("meetingTheme");
        String excelName = nowDate + title;
        String[] fieldNames = {"任务描述", "主责人", "预计完成时间", "完成情况", "是否完成"};
        String[] fieldCodes = {"meetingContent", "meetingPlanRespUserIds_name", "meetingPlanEndTime",
            "meetingPlanCompletion", "isComplete"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(listData, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }
}
