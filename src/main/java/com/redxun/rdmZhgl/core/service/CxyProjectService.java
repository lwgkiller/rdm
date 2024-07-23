package com.redxun.rdmZhgl.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmZhgl.core.dao.CxyProjectDao;
import com.redxun.rdmZhgl.core.dao.CxyProjectFileDao;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.xcmgJsjl.core.manager.XcmgJsjlManager;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;

import com.redxun.materielextend.core.dao.MaterielApplyDao;

/**
 * 产学研<p>
 * 产学研模块<p>
 * Copyright: Copyright (C) 2021 XXX, Inc. All rights reserved. <p>
 * Company: 徐工挖掘机械有限公司<p>
 *
 * @author liwenguang
 * @since 2021/2/25
 */
@Service
public class CxyProjectService {
    private static Logger logger = LoggerFactory.getLogger(XcmgJsjlManager.class);
    @Autowired
    private CxyProjectDao cxyProjectDao;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private CxyProjectFileService cxyProjectFileService;
    @Autowired
    private CxyProjectFileDao cxyProjectFileDao;
    @Autowired
    private MaterielApplyDao materielApplyDao;
    @Autowired
    private SysDicManager sysDicManager;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;


    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        //初始化查询参数
        getListParams(params, request);
        //增加角色过滤的条件（admin/分管领导看所有、分管主任看管理的多个部门、部门负责人看本部门，已提交的；创建人看自己的所有的）
        String currentUserId = ContextUtil.getCurrentUserId();
        String currentUserNo = ContextUtil.getCurrentUser().getUserNo();
        String cxyProjectLookUserNo = sysDicManager.getBySysTreeKeyAndDicKey(
                "cxyProjectLookUserNo", "查看全部人员").getValue();
        String[] cxyProjectLookUserNos = cxyProjectLookUserNo.split(",");
        boolean isAdmin = false;
        for (String userno : cxyProjectLookUserNos) {
            if (currentUserNo.equalsIgnoreCase(userno)) {
                isAdmin = true;
            }
        }
        //String cxyProjectLookUserNo = WebAppUtil.getProperty("cxyProjectLookUserNo", "");
//        if (!currentUserNo.equalsIgnoreCase("admin") && !currentUserNo.equalsIgnoreCase(cxyProjectLookUserNo)) {
        if (!currentUserNo.equalsIgnoreCase("admin") && !isAdmin) {
            params.put("currentUserId", currentUserId);
            //查询角色是否为分管领导
            Map<String, Object> queryUserParam = new HashMap<>();
            queryUserParam.put("userId", currentUserId);
            queryUserParam.put("groupName", "分管领导");
            List<Map<String, Object>> queryRoleResult = xcmgProjectOtherDao.queryUserRoles(queryUserParam);
            //查询角色是否为支部书记
            queryUserParam.clear();
            queryUserParam.put("userId", currentUserId);
            queryUserParam.put("groupName", "技术支部书记");
            List<Map<String, Object>> queryRoleResult2 = xcmgProjectOtherDao.queryUserRoles(queryUserParam);
            if ((queryRoleResult != null && !queryRoleResult.isEmpty()) ||
                    (queryRoleResult2 != null && !queryRoleResult2.isEmpty()) ||
                    rdmZhglUtil.judgeIsPointRoleUser(currentUserId, RdmConst.AllDATA_QUERY_NAME)) {
                params.put("roleName", "fgld");
            } else {
                // 查询是否为分管主任或部门负责人
                queryUserParam.put("userId", currentUserId);
                queryUserParam.remove("groupName");
                List<String> typeKeyList = new ArrayList<>();
                typeKeyList.add("GROUP-USER-LEADER");
                typeKeyList.add("GROUP-USER-DIRECTOR");
                queryUserParam.put("typeKeyList", typeKeyList);
                List<Map<String, Object>> queryDeptResult = xcmgProjectOtherDao.queryUserDeps(queryUserParam);
                if (queryDeptResult == null || queryDeptResult.isEmpty()) {
                    params.put("roleName", "ptyg");
                    //吐槽!!!!!!!!!!!!
                    ptygZhuJiaISurrenderOk(params);
                } else {
                    judgeUserDeptRole(queryDeptResult, params);
                    //吐槽!!!!!!!!!!!!
                    ptygZhuJiaISurrenderOk2(params);
                }
            }
        }
        List<JSONObject> cxyProjectDataList = cxyProjectDao.dataListQuery(params);
        if (cxyProjectDataList != null && !cxyProjectDataList.isEmpty()) {
            Set<String> responsibleUserIdSet = new HashSet<>();
            Set<String> responsibleUserDepIdSet = new HashSet<>();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (JSONObject oneObj : cxyProjectDataList) {
                if (oneObj.getDate("CREATE_TIME_") != null) {
                    oneObj.put("CREATE_TIME_", simpleDateFormat.format(oneObj.getDate("CREATE_TIME_")));
                }
                if (StringUtils.isNotBlank(oneObj.getString("responsibleUserId"))) {
                    responsibleUserIdSet.addAll(Arrays.asList(oneObj.getString("responsibleUserId").split(",", -1)));
                }
                if (StringUtils.isNotBlank(oneObj.getString("responsibleUserDepId"))) {
                    responsibleUserDepIdSet.addAll(Arrays.asList(oneObj.getString("responsibleUserDepId").split(",", -1)));
                }
                //获取附件名称列表
                List<String> FileNamesList = cxyProjectDao.getFileNamesListByMainId(oneObj.getString("id"));
                //如果有附件名称
                if (FileNamesList.size() > 0) {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (String fileName : FileNamesList) {
                        stringBuilder.append("《").append(fileName).append("》");
                    }
                    oneObj.put("FileNames", stringBuilder.toString());
                }
            }
            if (!responsibleUserIdSet.isEmpty()) {
                Map<String, String> userId2Name = queryUserNameByIds(responsibleUserIdSet);
                for (JSONObject oneObj : cxyProjectDataList) {
                    if (StringUtils.isNotBlank(oneObj.getString("responsibleUserId"))) {
                        List<String> userIds = Arrays.asList(oneObj.getString("responsibleUserId").split(",", -1));
                        String responsibleName = toGetUserNamesByIds(userId2Name, userIds);
                        oneObj.put("responsibleName", responsibleName);
                    }
                }
            }
            if (!responsibleUserDepIdSet.isEmpty()) {
                Map<String, String> depId2Name = queryDepNameByIds(responsibleUserDepIdSet);
                for (JSONObject oneObj : cxyProjectDataList) {
                    if (StringUtils.isNotBlank(oneObj.getString("responsibleUserDepId"))) {
                        List<String> depIds = Arrays.asList(oneObj.getString("responsibleUserDepId").split(",", -1));
                        String responsibleDepName = toGetDepNamesByIds(depId2Name, depIds);
                        oneObj.put("responsibleDepName", responsibleDepName);
                    }
                }
            }
        }
        int countCxyProjectDataList = cxyProjectDao.countDataListQuery(params);
        result.setData(cxyProjectDataList);
        result.setTotal(countCxyProjectDataList);
        return result;
    }

    //初始化查询参数
    private void getListParams(Map<String, Object> params, HttpServletRequest request) {
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)) {
            params.put("sortField", sortField);
            params.put("sortOrder", sortOrder);
        }
        if (StringUtils.isEmpty(sortField)) {
            params.put("sortField", "beginTime");
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
                    if ("beginTime".equalsIgnoreCase(name)) {
                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), -8));
                    }
                    if ("endTime".equalsIgnoreCase(name)) {
                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), 16));
                    }
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

    //判断用户是否是分管主任或者部门负责人，并抽取对应的部门
    private void judgeUserDeptRole(List<Map<String, Object>> queryDeptResult, Map<String, Object> params) {
        List<String> fzrDeptIdList = new ArrayList<>();
        List<String> fgzrDeptIdList = new ArrayList<>();

        boolean fgzrFlag = false;
        boolean fzrFlag = false;
        for (Map<String, Object> oneDept : queryDeptResult) {
            String relTypeKey = oneDept.get("REL_TYPE_KEY_").toString();
            String groupId = oneDept.get("PARTY1_").toString();
            if ("GROUP-USER-LEADER".equalsIgnoreCase(relTypeKey)) {
                fzrFlag = true;
                fzrDeptIdList.add(groupId);
            }
            if ("GROUP-USER-DIRECTOR".equalsIgnoreCase(relTypeKey)) {
                fgzrFlag = true;
                fgzrDeptIdList.add(groupId);
            }
        }
        if (fgzrFlag) {
            params.put("roleName", "fgzr");
            params.put("deptIds", fgzrDeptIdList);
        } else if (fzrFlag) {
            params.put("roleName", "fzr");
            params.put("deptIds", fzrDeptIdList);
        }
    }

    //保存草稿或者提交产学研项目的数据
    public void saveOrCommitCxyProjectData(JsonResult result, String cxyProjectDataStr) {
        JSONObject cxyProjectObj = JSONObject.parseObject(cxyProjectDataStr);
        if (cxyProjectObj == null || cxyProjectObj.isEmpty()) {
            result.setSuccess(false);
            result.setMessage("表单内容为空，操作失败！");
            return;
        }
        //下面的保存过程有个问题：参与者和参与部门的文本并不能即时从前端传过来，此处只能保证ids的正确，
        //一旦ids正确，下次查询到页面的数据再提交就能把参与者和参与部门的文本更新了,没有致命影响
        Map<String, Object> queryUserParam = new HashMap<>();
        StringBuilder stringBuilder = new StringBuilder();
        for (String userid : cxyProjectObj.getString("responsibleUserId").split(",")) {
            queryUserParam.put("userId", userid);
            List<String> typeKeyList = new ArrayList<>();
            typeKeyList.add("GROUP-USER-BELONG");
            queryUserParam.put("typeKeyList", typeKeyList);
            List<Map<String, Object>> queryDeptResult = xcmgProjectOtherDao.queryUserDeps(queryUserParam);
            stringBuilder.append(queryDeptResult.get(0).get("PARTY1_").toString()).append(",");
            System.out.println("");
        }
        if (stringBuilder.length() > 0) {
            cxyProjectObj.put("responsibleUserDepId", stringBuilder.substring(0, stringBuilder.length() - 1));
        }

        if (StringUtils.isBlank(cxyProjectObj.getString("id"))) {
            cxyProjectObj.put("id", IdUtil.getId());
            cxyProjectObj.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            cxyProjectObj.put("CREATE_TIME_", new Date());
            cxyProjectDao.insertCxyProjectData(cxyProjectObj);
            result.setData(cxyProjectObj.getString("id"));
        } else {
            cxyProjectObj.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            cxyProjectObj.put("UPDATE_TIME_", new Date());
            cxyProjectDao.updateCxyProjectData(cxyProjectObj);
            result.setData(cxyProjectObj.getString("id"));
        }
    }

    //根据id查询表单详情
    public JSONObject queryCxyProjectById(String cxyProjectId) {
        JSONObject result = new JSONObject();
        Set<String> responsibleUserIdSet = new HashSet<>();
        Set<String> responsibleUserDepIdSet = new HashSet<>();
        if (StringUtils.isBlank(cxyProjectId)) {
            return result;
        }
        JSONObject cxyProjectObj = cxyProjectDao.queryCxyProjectById(cxyProjectId);
        if (cxyProjectObj == null) {
            return result;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (cxyProjectObj.getDate("CREATE_TIME_") != null) {
            cxyProjectObj.put("CREATE_TIME_", simpleDateFormat.format(cxyProjectObj.getDate("CREATE_TIME_")));
        }
        if (StringUtils.isNotBlank(cxyProjectObj.getString("responsibleUserId"))) {
            responsibleUserIdSet.addAll(Arrays.asList(cxyProjectObj.getString("responsibleUserId").split(",", -1)));
        }
        if (StringUtils.isNotBlank(cxyProjectObj.getString("responsibleUserDepId"))) {
            responsibleUserDepIdSet.addAll(Arrays.asList(cxyProjectObj.getString("responsibleUserDepId").split(",", -1)));
        }
        if (!responsibleUserIdSet.isEmpty()) {
            Map<String, String> userId2Name = queryUserNameByIds(responsibleUserIdSet);
            if (StringUtils.isNotBlank(cxyProjectObj.getString("responsibleUserId"))) {
                List<String> userIds = Arrays.asList(cxyProjectObj.getString("responsibleUserId").split(",", -1));
                String responsibleName = toGetUserNamesByIds(userId2Name, userIds);
                cxyProjectObj.put("responsibleName", responsibleName);
            }

        }
        if (!responsibleUserDepIdSet.isEmpty()) {
            Map<String, String> depId2Name = queryDepNameByIds(responsibleUserDepIdSet);
            if (StringUtils.isNotBlank(cxyProjectObj.getString("responsibleUserDepId"))) {
                List<String> depIds = Arrays.asList(cxyProjectObj.getString("responsibleUserDepId").split(",", -1));
                String responsibleDepName = toGetDepNamesByIds(depId2Name, depIds);
                cxyProjectObj.put("responsibleDepName", responsibleDepName);
            }

        }
        return cxyProjectObj;
    }

    //删除产学研项目数据，同时删除对应的数据库和磁盘中的文件
    public JsonResult deleteCxyProject(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        Map<String, Object> param = new HashMap<>();
        param.put("cxyProjectIds", Arrays.asList(ids));
        List<JSONObject> files = cxyProjectFileDao.queryFilesByCxyProjectIds(param);
        for (JSONObject oneFile : files) {
            cxyProjectFileService.deleteOneFileFromDisk(oneFile.getString("id"), oneFile.getString("fileName"),
                    oneFile.getString("cxyProjectId"));
        }
        for (String oneCxyProjectId : ids) {
            cxyProjectFileService.deleteCxyProjectDirFromDisk(oneCxyProjectId);
        }
        cxyProjectFileDao.deleteFileByCxyProjectIds(param);
        cxyProjectDao.deleteCxyProject(param);
        return result;
    }

    //..
    public void exportList(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = dataListQuery(request, response);
        List<Map<String, Object>> listData = result.getData();
        for (Map<String, Object> oneMap : listData) {
            oneMap.put("isDelay", oneMap.get("isDelay").toString().equalsIgnoreCase("1") ? "是" : "否");
        }
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "合同列表";
        String excelName = nowDate + title;
        String[] fieldNames = {"项目名称", "承担单位", "合作单位", "项目负责人", "项目负责人部门", "项目开始时间", "项目结束时间",
                "合同金额(万元)", "已支付合同金额(万元)", "项目简介", "项目所属类型", "技术研究方向", "项目性质", "合同上要求指标", "目前已完成指标",
                "项目完成率(%)", "是否延期", "延期原因"};
        String[] fieldCodes = {"projectDesc", "undertaker", "collaborator", "responsibleName", "responsibleUserDepName",
                "beginTime", "endTime", "contractAmount", "paidContractAmount", "remark", "projectType", "researchDirection", "projectProperties",
                "contractIndicators", "completedIndicators", "completionRate", "isDelay", "delayReason"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(listData, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }

    //..
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

    //..
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

    //..
    private Map<String, String> queryDepNameByIds(Set<String> depIds) {
        Map<String, String> depId2Name = new HashMap<>();
        if (depIds == null || depIds.isEmpty()) {
            return depId2Name;
        }
        List<JSONObject> depInfos = cxyProjectDao.queryDepInfosByIds(new ArrayList<>(depIds));

        if (depInfos == null || depInfos.isEmpty()) {
            return depId2Name;
        }
        for (JSONObject oneDepInfo : depInfos) {
            depId2Name.put(oneDepInfo.getString("depId"), oneDepInfo.getString("depName"));
        }
        return depId2Name;
    }

    //..
    private String toGetDepNamesByIds(Map<String, String> depId2Name, List<String> depIdList) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String depId : depIdList) {
            if (depId2Name.containsKey(depId)) {
                stringBuilder.append(depId2Name.get(depId)).append(",");
            }
        }
        if (stringBuilder.length() > 0) {
            return stringBuilder.substring(0, stringBuilder.length() - 1);
        }
        return "";
    }

    //..
    private void ptygZhuJiaISurrenderOk(Map<String, Object> params) {
        String roleName = params.get("roleName").toString();
        params.remove("roleName");
        List<JSONObject> list = cxyProjectDao.dataListQuery(params);
        List<String> ids = new ArrayList<>();
        for (JSONObject jsonObject : list) {
            String[] responsibleUserIds = jsonObject.getString("responsibleUserId").split(",");
            for (String string : responsibleUserIds) {
                if (string.equalsIgnoreCase(params.get("currentUserId").toString())) {
                    ids.add(jsonObject.getString("id"));
                    break;
                }
            }
        }
        params.put("ids", ids);
        params.put("roleName", roleName);
    }

    //..
    private void ptygZhuJiaISurrenderOk2(Map<String, Object> params) {
        String roleName = params.get("roleName").toString();
        params.remove("roleName");
        List<JSONObject> list = cxyProjectDao.dataListQuery(params);
//        List<String> ids = new ArrayList<>();
        Map<String, String> idsmap = new HashMap<>();
        List<String> deptIds = (List<String>) params.get("deptIds");
        for (JSONObject jsonObject : list) {
            String[] responsibleUserIds = jsonObject.getString("responsibleUserId").split(",");
            for (String string : responsibleUserIds) {
                if (string.equalsIgnoreCase(params.get("currentUserId").toString())) {
                    idsmap.put(jsonObject.getString("id"), jsonObject.getString("id"));
                    break;
                }
            }
            String[] responsibleUserDepIds = jsonObject.getString("responsibleUserDepId").split(",");
            f1:
            for (String string : responsibleUserDepIds) {
                f2:
                for (String string2 : deptIds) {
                    if (string.equalsIgnoreCase(string2)) {
                        idsmap.put(jsonObject.getString("id"), jsonObject.getString("id"));
                        break f1;
                    }
                }
            }
        }
//        Iterator iterator = idsmap.keySet().iterator();
//        while (iterator.hasNext()) {
//            ids.add(idsmap.get(iterator.next()));
//        }
        params.put("ids", idsmap);
        params.put("roleName", roleName);
    }

}
