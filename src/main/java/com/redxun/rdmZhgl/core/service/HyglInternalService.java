package com.redxun.rdmZhgl.core.service;

import java.io.File;
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
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.materielextend.core.dao.MaterielApplyDao;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmZhgl.core.dao.HyglInternalDao;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;

@Service
public class HyglInternalService {
    private static Logger logger = LoggerFactory.getLogger(HyglInternalService.class);
    @Autowired
    private HyglInternalDao hyglInternalDao;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private MaterielApplyDao materielApplyDao;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;

    // ..
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        getListParams(params, request);

        // 增加角色过滤的条件（暂不考虑草稿的概念）
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
                if (queryDeptResult != null && !queryDeptResult.isEmpty()) {
                    // 3、分管主任或部门负责人，查看自己负责部门的、自己创建的、自己参会的，使用“fgzr”参数代表（不考虑草稿）
                    params.put("roleName", "fgzr");
                    judgeUserDeptRole(queryDeptResult, params);
                } else {
                    // 4、其他人员，查看自己创建的、自己参会的，使用"ptyg"参数代表（不考虑草稿）
                    params.put("roleName", "ptyg");
                }
            }
        }

        // 数据的查询和后处理
        List<JSONObject> meetingDataList = hyglInternalDao.dataListQuery(params);
        int countMeetingDataList = hyglInternalDao.countDataListQuery(params);

        if (meetingDataList != null && !meetingDataList.isEmpty()) {
            Set<String> meetingUserIdsSet = new HashSet<>();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (JSONObject oneObj : meetingDataList) {
                if (oneObj.getDate("CREATE_TIME_") != null) {
                    oneObj.put("CREATE_TIME_", simpleDateFormat.format(oneObj.getDate("CREATE_TIME_")));
                }
                if (StringUtils.isNotBlank(oneObj.getString("meetingUserIds"))) {
                    meetingUserIdsSet.addAll(Arrays.asList(oneObj.getString("meetingUserIds").split(",", -1)));
                }
                /*// 获取附件名称列表
                List<String> meetingFileNamesMapList =
                        hyglInternalDao.getFileNamesListByMeetingId(oneObj.getString("id"));
                // 如果有附件名称
                if (meetingFileNamesMapList.size() > 0) {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (String fileName : meetingFileNamesMapList) {
                        stringBuilder.append("《").append(fileName).append("》");
                    }
                    oneObj.put("meetingFileNames", stringBuilder.toString());
                }*/
            }
            if (!meetingUserIdsSet.isEmpty()) {
                Map<String, String> userId2Name = queryUserNameByIds(meetingUserIdsSet);
                for (JSONObject oneObj : meetingDataList) {
                    if (StringUtils.isNotBlank(oneObj.getString("meetingUserIds"))) {
                        List<String> userIds = Arrays.asList(oneObj.getString("meetingUserIds").split(",", -1));
                        String meetingUserNames = toGetUserNamesByIds(userId2Name, userIds);
                        oneObj.put("meetingUserNames", meetingUserNames);
                    }
                }

            }
        }

        result.setData(meetingDataList);
        result.setTotal(countMeetingDataList);
        return result;
    }

    private void ptygZhuJiaISurrenderOk(Map<String, Object> params) {
        String roleName = params.get("roleName").toString();
        params.remove("roleName");
        params.remove("pageSize");
        List<JSONObject> list = hyglInternalDao.dataListQuery(params);
        List<String> meetingIds = new ArrayList<>();
        for (JSONObject jsonObject : list) {
            String[] meetingUserIds = jsonObject.getString("meetingUserIds").split(",");
            for (String string : meetingUserIds) {
                if (string.equalsIgnoreCase(params.get("currentUserId").toString())) {
                    meetingIds.add(jsonObject.getString("id"));
                    break;
                }
            }
        }
        params.put("meetingIds", meetingIds);
        params.put("roleName", roleName);
        params.put("pageSize", 10);
    }

    // ..
    private String toGetUserNamesByIds(Map<String, String> userId2Name, List<String> userIdList) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String userId : userIdList) {
            if (userId2Name.containsKey(userId)) {
                stringBuilder.append(userId2Name.get(userId)).append(",");
            }
        }
        if (stringBuilder.length() > 0) {
            return stringBuilder.substring(0, stringBuilder.length() - 1);
        }
        return "";
    }

    // ..
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

    private void judgeUserDeptRole(List<Map<String, Object>> queryDeptResult, Map<String, Object> params) {
        Set<String> deptIdList = new HashSet<>();
        for (Map<String, Object> oneDept : queryDeptResult) {
            String groupId = oneDept.get("PARTY1_").toString();
            deptIdList.add(groupId);
        }
        params.put("deptIds", deptIdList);
    }

    // ..
    private void getListParams(Map<String, Object> params, HttpServletRequest request) {
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)) {
            params.put("sortField", sortField);
            params.put("sortOrder", sortOrder);
        }
        if (StringUtils.isEmpty(sortField)) {
            params.put("sortField", "meetingTime");
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

    // ..
    public void saveOrCommitMeetingData(JsonResult result, String meetingDataStr) {
        JSONObject meetingObj = JSONObject.parseObject(meetingDataStr);
        if (meetingObj == null || meetingObj.isEmpty()) {
            result.setSuccess(false);
            result.setMessage("表单内容为空，操作失败！");
            return;
        }
        // 草稿recordStatus不是已提交，不执行取号。
        if (meetingObj.getString("recordStatus").equalsIgnoreCase("已提交")
            && meetingObj.getBooleanValue("generationNo")) {
            // todo:改了
            if (meetingObj.getString("meetingNo").equalsIgnoreCase("")) {
                meetingObj.put("meetingNo", this.genSequenceNoInternal(meetingObj, "MinutesInternal"));
            } else if (!getDateString(meetingObj.getString("meetingTime").substring(0, 10))
                .equalsIgnoreCase(meetingObj.getString("meetingNo").substring(4, 12))) {
                meetingObj.put("meetingNo", this.genSequenceNoInternal(meetingObj, "MinutesInternal"));
            }
        }
        if (StringUtils.isBlank(meetingObj.getString("id"))) {
            meetingObj.put("id", IdUtil.getId());
            meetingObj.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            meetingObj.put("CREATE_TIME_", new Date());
            hyglInternalDao.insertMeetingData(meetingObj);
            result.setData(meetingObj.getString("id"));
        } else {
            meetingObj.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            meetingObj.put("UPDATE_TIME_", new Date());
            hyglInternalDao.updateMeetingData(meetingObj);
            result.setData(meetingObj.getString("id"));
        }
    }

    // ..生成会内部议编号
    private synchronized String genSequenceNoInternal(JSONObject meetingObj, String key) {
        String dateStr = getDateString(meetingObj.getString("meetingTime").substring(0, 10));
        if (StringUtils.isBlank(dateStr)) {
            return "";
        }
        String tempStr = meetingObj.getString("meetingModelDescp") + dateStr + "-";
        JSONObject params = new JSONObject();
        params.put("tempStr", tempStr);
        JSONObject seq = hyglInternalDao.getSeqInternal(params);
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

    // ..
    public JSONObject queryMeetingById(String meetingId) {
        JSONObject result = new JSONObject();
        if (StringUtils.isBlank(meetingId)) {
            return result;
        }
        JSONObject meetingObj = hyglInternalDao.queryMeetingById(meetingId);
        if (meetingObj == null) {
            return result;
        }
        Set<String> userIdSet = new HashSet<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (meetingObj.getDate("CREATE_TIME_") != null) {
            meetingObj.put("CREATE_TIME_", simpleDateFormat.format(meetingObj.getDate("CREATE_TIME_")));
        }
        if (StringUtils.isNotBlank(meetingObj.getString("meetingUserIds"))) {
            userIdSet.addAll(Arrays.asList(meetingObj.getString("meetingUserIds").split(",", -1)));
        }
        if (!userIdSet.isEmpty()) {
            Map<String, String> userId2Name = queryUserNameByIds(userIdSet);
            if (StringUtils.isNotBlank(meetingObj.getString("meetingUserIds"))) {
                List<String> userIds = Arrays.asList(meetingObj.getString("meetingUserIds").split(",", -1));
                String meetingUserNames = toGetUserNamesByIds(userId2Name, userIds);
                meetingObj.put("meetingUserNames", meetingUserNames);
            }
        }
        return meetingObj;
    }

    // ..
    public JsonResult deleteMeeting(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> meetingIds = Arrays.asList(ids);
        List<JSONObject> files = this.getMeetingFileList(meetingIds);
        String meetingFilePathBase = WebAppUtil.getProperty("meetingFilePathBase");
        for (JSONObject oneFile : files) {
            rdmZhglFileManager.deleteOneFileFromDisk(oneFile.getString("id"), oneFile.getString("fileName"),
                oneFile.getString("meetingId"), meetingFilePathBase);
        }
        for (String oneMeetingId : ids) {
            rdmZhglFileManager.deleteDirFromDisk(oneMeetingId, meetingFilePathBase);
            hyglInternalDao.deleteMeetingPlanByMeetingId(oneMeetingId);
        }
        Map<String, Object> param = new HashMap<>();
        param.put("meetingIds", meetingIds);
        hyglInternalDao.deleteMeetingFile(param);
        hyglInternalDao.deleteMeeting(param);
        return result;
    }

    // ..
    public List<JSONObject> getMeetingFileList(List<String> meetingIdList) {
        List<JSONObject> meetingFileList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("meetingIds", meetingIdList);
        meetingFileList = hyglInternalDao.queryMeetingFileList(param);
        return meetingFileList;
    }

    // ..
    public void deleteOneMeetingFile(String fileId, String fileName, String meetingId) {
        rdmZhglFileManager.deleteOneFileFromDisk(fileId, fileName, meetingId,
            WebAppUtil.getProperty("meetingFilePathBase"));
        Map<String, Object> param = new HashMap<>();
        param.put("id", fileId);
        hyglInternalDao.deleteMeetingFile(param);
    }

    // ..
    public void saveUploadFiles(HttpServletRequest request) {
        Map<String, String[]> parameters = request.getParameterMap();
        if (parameters == null || parameters.isEmpty()) {
            logger.warn("没有找到上传的参数");
            return;
        }
        // 多附件上传需要用到的MultipartHttpServletRequest
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        if (fileMap == null || fileMap.isEmpty()) {
            logger.warn("没有找到上传的文件");
            return;
        }
        String filePathBase = WebAppUtil.getProperty("meetingFilePathBase");
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find meetingFilePathBase");
            return;
        }
        try {
            String meetingId = toGetParamVal(parameters.get("meetingId"));
            String id = IdUtil.getId();
            String fileName = toGetParamVal(parameters.get("fileName"));
            String fileSize = toGetParamVal(parameters.get("fileSize"));
            String fileDesc = toGetParamVal(parameters.get("fileDesc"));

            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();

            // 向下载目录中写入文件
            String filePath = filePathBase + File.separator + meetingId;
            File pathFile = new File(filePath);
            if (!pathFile.exists()) {
                pathFile.mkdirs();
            }
            String suffix = CommonFuns.toGetFileSuffix(fileName);
            String fileFullPath = filePath + File.separator + id + "." + suffix;
            File file = new File(fileFullPath);
            FileCopyUtils.copy(mf.getBytes(), file);

            // 写入数据库
            JSONObject fileInfo = new JSONObject();
            fileInfo.put("id", id);
            fileInfo.put("fileName", fileName);
            fileInfo.put("fileSize", fileSize);
            fileInfo.put("meetingId", meetingId);
            fileInfo.put("fileDesc", fileDesc);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            hyglInternalDao.addMeetingFileInfos(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
    }

    // ..
    private String toGetParamVal(String[] paramValArr) {
        if (paramValArr == null || paramValArr.length == 0) {
            return null;
        }
        return paramValArr[0];
    }

    // ..
    public List<JSONObject> getMeetingPlanList(String meetingId) {
        List<JSONObject> meetingPlanList = hyglInternalDao.queryMeetingPlanList(meetingId);
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
                hyglInternalDao.insertMeetingPlanData(meetingPlanObj);
            } else {
                meetingPlanObj.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                meetingPlanObj.put("UPDATE_TIME_", new Date());
                meetingPlanObj.put("meetingPlanEndTime",
                    meetingPlanObj.getString("meetingPlanEndTime").substring(0, 10));
                hyglInternalDao.updateMeetingPlanData(meetingPlanObj);
            }
        }
        // 查询会议最新的任务中的主责人，分隔去重后，更新回主表中
        updateOneMeetingPlanRespUserIds(meetingId);
    }

    // ..
    public JsonResult deleteOneMeetingPlan(String id, String meetingId) {
        JsonResult result = new JsonResult(true, "删除成功！");
        hyglInternalDao.deleteOneMeetingPlan(id);
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
        List<JSONObject> meetingPlanList = hyglInternalDao.queryMeetingPlanList(meetingId);
        if (meetingPlanList != null && !meetingPlanList.isEmpty()) {
            for (JSONObject jsonObject : meetingPlanList) {
                if (StringUtils.isNotBlank(jsonObject.getString("meetingPlanRespUserIds"))) {
                    userIdSet.addAll(Arrays.asList(jsonObject.getString("meetingPlanRespUserIds").split(",", -1)));
                }
            }
        }
        if (!userIdSet.isEmpty()) {
            JSONObject param = new JSONObject();
            param.put("planRespUserIds", String.join(",", userIdSet));
            param.put("id", meetingId);
            hyglInternalDao.updateMeetingPlanRespUserIds(param);
        }
    }

    // ..
    public void exportPlanExcel(HttpServletRequest request, HttpServletResponse response) {
        String filterParams = request.getParameter("filter");
        String meetingId = "";
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            meetingId = jsonArray.getJSONObject(0).getString("value");
        }
        List<JSONObject> listDataJson = getMeetingPlanList(meetingId);
        List<Map<String, Object>> listData = new ArrayList<>();
        for (JSONObject jsonObject : listDataJson) {
            listData.add(jsonObject.getInnerMap());
        }

        for (Map<String, Object> oneMap : listData) {
            oneMap.put("isComplete", oneMap.get("isComplete").toString().equalsIgnoreCase("true") ? "是" : "否");
        }
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        JSONObject jsonObject = queryMeetingById(meetingId);
        String title = "内部会议纪要执行列表，会议主题：" + jsonObject.getString("meetingTheme");
        String excelName = nowDate + title;
        String[] fieldNames = {"任务描述", "主责人", "预计完成时间", "完成情况", "是否完成"};
        String[] fieldCodes = {"meetingContent", "meetingPlanRespUserIds_name", "meetingPlanEndTime",
            "meetingPlanCompletion", "isComplete"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(listData, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }

    public List<JSONObject> getDicByParam(JSONObject param) {
        return hyglInternalDao.getDicByParam(param);
    }
}
