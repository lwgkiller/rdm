package com.redxun.meeting.core.service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import com.redxun.meeting.core.dao.HyglDao;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;

@Service
public class HyglService {
    private static Logger logger = LoggerFactory.getLogger(HyglService.class);
    @Autowired
    private HyglDao hyglDao;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private MaterielApplyDao materielApplyDao;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;

    // ..
    public JsonPageResult<?> getHyglList(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        // *分页处理
        getListParams(params, request);
        // *获取当前用户ID
        String currentUserId = ContextUtil.getCurrentUserId();
        params.put("currentUserId", currentUserId);
        Map<String, Object> queryUserParam = new HashMap<>();
        queryUserParam.put("userId", currentUserId);
        queryUserParam.put("groupName", "分管领导");
        //*根据用户ID、组查询数据库
        List<Map<String, Object>> queryRoleResult = xcmgProjectOtherDao.queryUserRoles(queryUserParam);
        boolean isZjl = rdmZhglUtil.judgeIsPointRoleUser(ContextUtil.getCurrentUserId(), RdmConst.JSZX_ZR);
        boolean hyglzy = rdmZhglUtil.judgeIsPointRoleUser(ContextUtil.getCurrentUserId(), RdmConst.HYGLZY);
        boolean allDataQuery =
            rdmZhglUtil.judgeIsPointRoleUser(ContextUtil.getCurrentUserId(), RdmConst.AllDATA_QUERY_NAME);
        // 增加角色过滤的条件（暂不考虑草稿的概念）
        String currentUserNo = ContextUtil.getCurrentUser().getUserNo();
        // 1、admin查看所有，因此不附加条件
        if (!currentUserNo.equalsIgnoreCase("admin") && !isZjl && !hyglzy && !allDataQuery
            && !(queryRoleResult != null && !queryRoleResult.isEmpty())) {
            params.put("currentUserId", currentUserId);
            queryUserParam.clear();
            queryUserParam.put("userId", currentUserId);
            List<String> typeKeyList = new ArrayList<>();
            typeKeyList.add("GROUP-USER-LEADER");
            queryUserParam.put("typeKeyList", typeKeyList);
            List<Map<String, Object>> queryDeptResult = xcmgProjectOtherDao.queryUserDeps(queryUserParam);
            if (queryDeptResult != null && !queryDeptResult.isEmpty()) {
                // 3、部门负责人，查看自己负责部门的、自己创建的、自己参会的，使用“fgzr”参数代表（不考虑草稿）
                params.put("roleName", "fgzr");
                judgeUserDeptRole(queryDeptResult, params);
            } else {
                // 判断是否为部门会议管理专员，并返回所在部门，使用“fgzr”参数代表
                boolean bmhyglzy = rdmZhglUtil.judgeIsPointRoleUser(ContextUtil.getCurrentUserId(), RdmConst.BMHYGLZY);
                if (bmhyglzy) {
                    Set<String> deptIdList = new HashSet<>();
                    deptIdList.add(ContextUtil.getCurrentUser().getMainGroupId());
                    params.put("deptIds", deptIdList);
                    params.put("roleName", "fgzr");
                } else {
                    // 4、其他人员，查看自己创建的、自己参会的，使用"ptyg"参数代表（不考虑草稿）
                    params.put("roleName", "ptyg");
                }
            }

        }

        // 数据的查询和后处理
        List<JSONObject> meetingDataList = hyglDao.getHyglList(params);
        int countMeetingDataList = hyglDao.countHyglList(params);
        List<String> meetingIds = new ArrayList<>();
        if (meetingDataList != null && !meetingDataList.isEmpty()) {
            Set<String> meetingUserIdsSet = new HashSet<>();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (JSONObject oneObj : meetingDataList) {
                meetingIds.add(oneObj.getString("id"));
                if (oneObj.getDate("CREATE_TIME_") != null) {
                    oneObj.put("CREATE_TIME_", DateUtil.formatDate((Date)oneObj.get("CREATE_TIME_"), "yyyy-MM-dd"));
                }
                if (StringUtils.isNotBlank(oneObj.getString("meetingUserIds"))) {
                    meetingUserIdsSet.addAll(Arrays.asList(oneObj.getString("meetingUserIds").split(",", -1)));
                }
                /*// 获取附件名称列表
                List<String> meetingFileNamesMapList =
                        hyglDao.getFileNamesListByMeetingId(oneObj.getString("id"));
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
            Map<String, Object> param = new HashMap<>();
            param.put("meetingIds", meetingIds);
            List<JSONObject> meetingPlanDataList = hyglDao.queryAllMeetingPlanList(param);
            Stream<JSONObject> stream = meetingPlanDataList.stream();
            Map<String, List<JSONObject>> idToPlan =
                stream.collect(Collectors.groupingBy(j -> j.getString("meetingId")));
            for (JSONObject oneObj : meetingDataList) {
                String meetingId = oneObj.getString("id");
                List<JSONObject> planList = idToPlan.get(meetingId);
                boolean finish = true;
                if (planList != null && !planList.isEmpty()) {
                    for (JSONObject onePlan : planList) {
                        if ("RUNNING".equalsIgnoreCase(onePlan.getString("status"))
                            || StringUtils.isBlank(onePlan.getString("status"))) {
                            finish = false;
                            break;
                        }
                    }
                    if ("已提交".equalsIgnoreCase(oneObj.getString("recordStatus")) && finish) {
                        oneObj.put("recordStatus", "纪要审批完成");
                    }
                }
            }
        }

        result.setData(meetingDataList);
        result.setTotal(countMeetingDataList);
        return result;
    }

    // ..
    public String toGetUserNamesByIds(Map<String, String> userId2Name, List<String> userIdList) {
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
    public Map<String, String> queryUserNameByIds(Set<String> userIds) {
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
            params.put("sortField", "CREATE_TIME_");
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
    }

    // ..
    public void saveOrCommitMeetingData(JsonResult result, String meetingDataStr) {
        JSONObject meetingObj = JSONObject.parseObject(meetingDataStr);
        if (meetingObj == null || meetingObj.isEmpty()) {
            result.setSuccess(false);
            result.setMessage("表单内容为空，操作失败！");
            return;
        }
        if (meetingObj.getString("meetingNo").equalsIgnoreCase("")) {
            meetingObj.put("meetingNo", this.genSequenceNoInternal(meetingObj, "MinutesInternal"));
        } else if (!getDateString(meetingObj.getString("meetingTime").substring(0, 4)).equalsIgnoreCase(
            meetingObj.getString("meetingNo").substring(meetingObj.getString("meetingNo").length() - 8,
                meetingObj.getString("meetingNo").length() - 4))) {
            meetingObj.put("meetingNo", this.genSequenceNoInternal(meetingObj, "MinutesInternal"));
        }
        if (StringUtils.isBlank(meetingObj.getString("id"))) {
            meetingObj.put("id", IdUtil.getId());
            meetingObj.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            meetingObj.put("CREATE_TIME_", new Date());
            hyglDao.insertMeetingData(meetingObj);
            result.setData(meetingObj.getString("id"));
        } else {
            meetingObj.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            meetingObj.put("UPDATE_TIME_", new Date());
            hyglDao.updateMeetingData(meetingObj);
            result.setData(meetingObj.getString("id"));
        }
    }

    // ..生成会内部议编号
    private synchronized String genSequenceNoInternal(JSONObject meetingObj, String key) {
        String dateStr = getDateString(meetingObj.getString("meetingTime").substring(0, 4));
        String deptCode = "";
        if (StringUtils.isBlank(dateStr)) {
            return "";
        }
        JSONObject params = new JSONObject();
        String deptId = meetingObj.getString("meetingOrgDepId");
        params.put("deptId", deptId);
        JSONObject deptCodeJson = hyglDao.getDeptCode(params);
        if (deptCodeJson != null && !deptCodeJson.isEmpty()) {
            deptCode = deptCodeJson.getString("deptCode");
        }
        String tempStr = deptCode + dateStr;
        params.put("tempStr", tempStr);
        JSONObject seq = hyglDao.getSeqInternal(params);
        if (seq == null) {
            return tempStr + "0001";
        } else {
            int thisNumber = Integer.parseInt(seq.getString("num")) + 1;
            if (thisNumber < 10) {
                return tempStr + "000" + thisNumber;
            } else if (thisNumber < 100 && thisNumber >= 10) {
                return tempStr + "00" + thisNumber;
            } else if (thisNumber < 1000 && thisNumber >= 100) {
                return tempStr + "0" + thisNumber;
            }
            return tempStr + thisNumber;
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
        JSONObject meetingObj = hyglDao.queryMeetingById(meetingId);
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
            hyglDao.deleteMeetingPlanByMeetingId(oneMeetingId);
        }
        Map<String, Object> param = new HashMap<>();
        param.put("meetingIds", meetingIds);
        hyglDao.deleteMeetingFile(param);
        hyglDao.deleteMeeting(param);
        return result;
    }

    // ..
    public List<JSONObject> getMeetingFileList(List<String> meetingIdList) {
        List<JSONObject> meetingFileList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("meetingIds", meetingIdList);
        meetingFileList = hyglDao.queryMeetingFileList(param);
        return meetingFileList;
    }

    // ..
    public void deleteOneMeetingFile(String fileId, String fileName, String meetingId) {
        rdmZhglFileManager.deleteOneFileFromDisk(fileId, fileName, meetingId,
            WebAppUtil.getProperty("meetingFilePathBase"));
        Map<String, Object> param = new HashMap<>();
        param.put("id", fileId);
        hyglDao.deleteMeetingFile(param);
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
            hyglDao.addMeetingFileInfos(fileInfo);
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
        List<JSONObject> meetingPlanList = hyglDao.queryMeetingPlanList(meetingId);
        Set<String> userIdSet = new HashSet<>();
        Set<String> otherIdSet = new HashSet<>();
        for (JSONObject jsonObject : meetingPlanList) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            if (jsonObject.getDate("meetingPlanEndTime") != null) {
                jsonObject.put("meetingPlanEndTime", simpleDateFormat.format(jsonObject.getDate("meetingPlanEndTime")));
            }
            if (StringUtils.isNotBlank(jsonObject.getString("meetingPlanRespUserIds"))) {
                userIdSet.addAll(Arrays.asList(jsonObject.getString("meetingPlanRespUserIds").split(",", -1)));
            }
            if (StringUtils.isNotBlank(jsonObject.getString("otherPlanRespUserIds"))) {
                otherIdSet.addAll(Arrays.asList(jsonObject.getString("otherPlanRespUserIds").split(",", -1)));
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
        if (!otherIdSet.isEmpty()) {
            Map<String, String> otherId2Name = queryUserNameByIds(otherIdSet);
            for (JSONObject jsonObject : meetingPlanList) {
                if (StringUtils.isNotBlank(jsonObject.getString("otherPlanRespUserIds"))) {
                    List<String> userIds = Arrays.asList(jsonObject.getString("otherPlanRespUserIds").split(",", -1));
                    String otherUserNames = toGetUserNamesByIds(otherId2Name, userIds);
                    jsonObject.put("otherPlanRespUserIds_name", otherUserNames);
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
        int index = 1;
        for (Object object : meetingPlanObjs) {
            JSONObject meetingPlanObj = (JSONObject)object;
            if (StringUtils.isBlank(meetingPlanObj.getString("id"))) {
                meetingPlanObj.put("id", IdUtil.getId());
                meetingPlanObj.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                meetingPlanObj.put("CREATE_TIME_", new Date());
                meetingPlanObj.put("meetingPlanEndTime", meetingPlanObj.getString("meetingPlanEndTime"));
                meetingPlanObj.put("sn", index);
                hyglDao.insertMeetingPlanData(meetingPlanObj);
            } else {
                meetingPlanObj.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                meetingPlanObj.put("UPDATE_TIME_", new Date());
                meetingPlanObj.put("meetingPlanEndTime", meetingPlanObj.getString("meetingPlanEndTime"));
                meetingPlanObj.put("sn", index);
                hyglDao.updateMeetingPlanData(meetingPlanObj);

            }
            index++;
        }
        // 查询会议最新的任务中的主责人，分隔去重后，更新回主表中
        updateOneMeetingPlanRespUserIds(meetingId);
    }

    // ..
    public JsonResult deleteOneMeetingPlan(String id, String meetingId) {
        JsonResult result = new JsonResult(true, "删除成功！");
        hyglDao.deleteOneMeetingPlan(id);
        // 查询会议最新的任务中的主责人，分隔去重后，更新回主表中
        updateOneMeetingPlanRespUserIds(meetingId);
        return result;
    }

    // 会议任务变化后，查询会议最新的任务中的主责人，分隔去重后，更新回主表中
    public void updateOneMeetingPlanRespUserIds(String meetingId) {
        if (StringUtils.isBlank(meetingId)) {
            return;
        }
        Set<String> userIdSet = new HashSet<>();
        List<JSONObject> meetingPlanList = hyglDao.queryMeetingPlanList(meetingId);
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
            hyglDao.updateMeetingPlanRespUserIds(param);
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
        for (JSONObject msg : listDataJson) {
            if (msg.getDate("meetingPlanEndTime") != null) {
                msg.put("meetingPlanEndTime", DateUtil.formatDate(msg.getDate("meetingPlanEndTime"), "yyyy-MM-dd"));
            }
        }
        List<Map<String, Object>> listData = new ArrayList<>();
        for (JSONObject jsonObject : listDataJson) {
            listData.add(jsonObject.getInnerMap());
        }

        for (Map<String, Object> oneMap : listData) {
            oneMap.put("isComplete", oneMap.get("isComplete").toString().equalsIgnoreCase("true") ? "是" : "否");
        }
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        JSONObject jsonObject = queryMeetingById(meetingId);
        String title = "会议纪要执行列表，会议主题：" + jsonObject.getString("meetingTheme");
        String excelName = nowDate + title;
        String[] fieldNames = {"会议纪要描述", "第一责任人", "其他责任人", "预计完成时间", "情况描述", "完成自评"};
        String[] fieldCodes = {"meetingContent", "meetingPlanRespUserIds_name", "otherPlanRespUserIds_name",
            "meetingPlanEndTime", "meetingPlanCompletion", "isComplete"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(listData, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }

}
