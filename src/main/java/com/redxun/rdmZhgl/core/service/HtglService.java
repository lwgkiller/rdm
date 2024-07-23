package com.redxun.rdmZhgl.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmZhgl.core.dao.HtglDao;
import com.redxun.rdmZhgl.core.dao.HtglFileDao;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.sys.core.manager.SysSeqIdManager;
import com.redxun.xcmgJsjl.core.manager.XcmgJsjlManager;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;

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
public class HtglService {
    private static Logger logger = LoggerFactory.getLogger(HtglService.class);
    @Autowired
    private HtglDao htglDao;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private HtglFileService htglFileService;
    @Autowired
    private HtglFileDao htglFileDao;
    @Autowired
    private SysSeqIdManager sysSeqIdManager;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private SendDDNoticeManager sendDDNoticeManager;

    //..
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        //初始化查询参数
        getListParams(params, request);

        //增加角色过滤的条件（admin/分管领导看所有、分管主任看管理的多个部门、部门负责人看本部门；创建人看自己的所有的）
        String currentUserId = ContextUtil.getCurrentUserId();
        String currentUserNo = ContextUtil.getCurrentUser().getUserNo();
        String LookUserNo = WebAppUtil.getProperty("jsjlLookUserNo", "");//老朱
        boolean htglzy = rdmZhglUtil.judgeIsPointRoleUser(ContextUtil.getCurrentUserId(), RdmConst.HTGLZY);
        if (!currentUserNo.equalsIgnoreCase("admin") && !currentUserNo.equalsIgnoreCase(LookUserNo) && !htglzy) {
            params.put("currentUserId", currentUserId);
            // 查询角色是否为分管领导
            Map<String, Object> queryUserParam = new HashMap<>();
            queryUserParam.put("userId", currentUserId);
            queryUserParam.put("groupName", "分管领导");
            List<Map<String, Object>> queryRoleResult = xcmgProjectOtherDao.queryUserRoles(queryUserParam);
            if (queryRoleResult != null && !queryRoleResult.isEmpty()) {
                params.put("roleName", "fgld");
            } else if (rdmZhglUtil.judgeIsPointRoleUser(currentUserId, RdmConst.AllDATA_QUERY_NAME)) {
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
                } else {
                    judgeUserDeptRole(queryDeptResult, params);
                }
            }
        }


        List<JSONObject> contractDataList = htglDao.dataListQuery(params);
        if (contractDataList != null && !contractDataList.isEmpty()) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (JSONObject oneObj : contractDataList) {
                if (oneObj.getDate("CREATE_TIME_") != null) {
                    oneObj.put("CREATE_TIME_", simpleDateFormat.format(oneObj.getDate("CREATE_TIME_")));
                }
                //获取附件名称列表
                List<String> FileNamesList = htglDao.getFileNamesListByMainId(oneObj.getString("id"));
                //如果有附件名称
                if (FileNamesList.size() > 0) {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (String fileName : FileNamesList) {
                        stringBuilder.append("《").append(fileName).append("》");
                    }
                    oneObj.put("FileNames", stringBuilder.toString());
                }
            }
        }
        int countContractDataList = htglDao.countDataListQuery(params);
        result.setData(contractDataList);
        result.setTotal(countContractDataList);
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
            params.put("sortField", "contractNo");
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
                }
                params.put(name, value);
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

    //..
    public void saveContractData(JsonResult result, String contractDataStr) {
        JSONObject contractObj = JSONObject.parseObject(contractDataStr);
        if (contractObj == null || contractObj.isEmpty()) {
            result.setSuccess(false);
            result.setMessage("表单内容为空，操作失败！");
            return;
        }
        if (StringUtils.isBlank(contractObj.getString("id"))) {
            String tenantId = ContextUtil.getCurrentTenantId();
            contractObj.put("id", IdUtil.getId());
            contractObj.put("contractNo", sysSeqIdManager.genSequenceNo("contractNo", tenantId));
            contractObj.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            contractObj.put("CREATE_TIME_", new Date());
            htglDao.insertContractData(contractObj);
            result.setData(contractObj.getString("id"));
        } else {
            contractObj.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            contractObj.put("UPDATE_TIME_", new Date());
            htglDao.updateContractData(contractObj);
            result.setData(contractObj.getString("id"));
        }
    }

    //..
    public JSONObject queryContractById(String contractId) {
        JSONObject result = new JSONObject();
        if (StringUtils.isBlank(contractId)) {
            return result;
        }
        JSONObject contractObj = htglDao.queryContractById(contractId);
        if (contractObj == null) {
            return result;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (contractObj.getDate("CREATE_TIME_") != null) {
            contractObj.put("CREATE_TIME_", simpleDateFormat.format(contractObj.getDate("CREATE_TIME_")));
        }
        return contractObj;
    }

    //..
    public JsonResult deleteContract(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        Map<String, Object> param = new HashMap<>();
        param.put("contractIds", Arrays.asList(ids));
        List<JSONObject> files = htglFileDao.queryFilesByContractIds(param);
        for (JSONObject oneFile : files) {
            htglFileService.deleteOneFileFromDisk(oneFile.getString("id"), oneFile.getString("fileName"),
                    oneFile.getString("contractId"));
        }
        for (String contractId : ids) {
            htglFileService.deleteContractDirFromDisk(contractId);
        }
        htglFileDao.deleteFileByContractIds(param);
        htglDao.deleteContract(param);
        return result;
    }

    //..
    public JsonResult discardContract(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        Map<String, Object> param = new HashMap<>();
        param.put("contractIds", Arrays.asList(ids));
        htglDao.discardContract(param);
        return result;
    }

    //..
    public void exportList(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = dataListQuery(request, response);
        List<Map<String, Object>> listData = result.getData();
        for (Map<String, Object> oneMap : listData) {
            oneMap.put("isSingHonest", oneMap.get("isSingHonest").toString().equalsIgnoreCase("1") ? "是" : "否");
            oneMap.put("isRecord", oneMap.get("isRecord").toString().equalsIgnoreCase("1") ? "是" : "否");
            oneMap.put("isFile", oneMap.get("isFile").toString().equalsIgnoreCase("1") ? "是" : "否");
        }
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "合同列表";
        String excelName = nowDate + title;
        String[] fieldNames = {"合同编号", "合同名称", "签订人", "签订部门", "签订日期",
                "变更/解除情况", "年份", "月份", "甲方", "乙方", "丙方", "丁方",
                "是否签订廉洁承诺", "是否备案", "是否归档"};
        String[] fieldCodes = {"contractNo", "contractDesc", "signerUserName", "signerUserDepName", "signDate",
                "CAndTStatus", "signYear", "signMonth", "partA", "partB", "partC", "partD",
                "isSingHonest", "isRecord", "isFile"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(listData, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }
}
