package com.redxun.xcmgProjectManager.core.manager;

import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.query.Page;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectChangeDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import com.redxun.xcmgProjectManager.report.dao.XcmgProjectReportDao;

import groovy.util.logging.Slf4j;

/**
 * @author zz
 */
@Service
@Slf4j
public class XcmgProjectChangeManager {
    private static Logger logger = LoggerFactory.getLogger(XcmgProjectChangeManager.class);
    @Resource
    private BpmInstManager bpmInstManager;
    @Autowired
    private XcmgProjectChangeDao xcmgProjectChangeDao;
    @Autowired
    private XcmgProjectReportDao xcmgProjectReportDao;
    @Autowired
    private CommonInfoManager commonInfoManager;
    @Autowired
    private XcmgProjectAbolishManager xcmgProjectAbolishManager;

    public void createChangeApply(Map<String, Object> params) {
        String preFix = "SP-";
        String id = preFix + XcmgProjectUtil.getNowLocalDateStr("yyyyMMddHHmmssSSS") + (int)(Math.random() * 100);
        params.put("id", id);
        params.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        params.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        xcmgProjectChangeDao.addChangeApply(params);
    }

    public void updateChangeApply(Map<String, Object> params) {
        params.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        params.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        xcmgProjectChangeDao.updateChangeApply(params);
    }

    /**
     * 删除表单及关联的所有信息
     */
    public JsonResult deleteChangeApplyById(String[] applyIdArr, String[] instIdArr) {
        if (applyIdArr.length != instIdArr.length) {
            return new JsonResult(false, "表单和流程个数不相同！");
        }
        for (int i = 0; i < applyIdArr.length; i++) {
            String applyId = applyIdArr[i];
            xcmgProjectChangeDao.deleteChangeApplyById(applyId);
            // 删除实例
            bpmInstManager.deleteCascade(instIdArr[i], "");
        }
        return new JsonResult(true, "成功删除!");
    }

    /**
     * 查询变更列表
     */
    public JsonPageResult<?> queryChangeApplyList(HttpServletRequest request, HttpServletResponse response,boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            String action = RequestUtil.getString(request, "action", "");
            String projectId = RequestUtil.getString(request, "projectId", "");
            if ("queryOneProject".equalsIgnoreCase(action)) {
                if (StringUtils.isBlank(projectId)) {
                    return result;
                }
            }
            Map<String, Object> params = new HashMap<>();
            // 传入条件(不包括分页)
            params = XcmgProjectUtil.getSearchParam(params, request);
            if (params.get("sortField") == null) {
                params.put("sortField", "project_changeapply.CREATE_TIME_");
                params.put("sortOrder", "asc");
            }

            if (StringUtils.isNotBlank(projectId)) {
                params.put("projectId", projectId);
            }
            // 如果是挖掘机械研究院项目管理人员，则只查询挖掘机械研究院下的部门
            boolean isJSZXProjManagUsers =
                commonInfoManager.judgeUserIsPointRole(RdmConst.JSZXXMGLRY, ContextUtil.getCurrentUserId());
            if (isJSZXProjManagUsers) {
                Map<String, String> deptId2Name = commonInfoManager.queryDeptUnderJSZX();
                //2024-3-29新增技术中心项目管理员也可以看到工艺的项目
                boolean isGYProjManagUsers =
                        commonInfoManager.judgeUserIsPointRole(RdmConst.GYJSBXMGLRY, ContextUtil.getCurrentUserId());
                if (isGYProjManagUsers) {
                    Map<String, Object> param = new HashMap<>();
                    param.put("deptName", RdmConst.GYJSB_NAME);
                    List<JSONObject> deptList = xcmgProjectReportDao.queryDeptByName(param);
                    if (deptList != null && !deptList.isEmpty()) {
                        deptId2Name.put(deptList.get(0).getString("GROUP_ID_"), deptList.get(0).getString("NAME_"));
                    }
                }
                params.put("deptIds", deptId2Name.keySet());
                //2024-4-29新增技术中心项目管理员也可以看到应用技术部的项目
                boolean isYYProjManagUsers =
                        commonInfoManager.judgeUserIsPointRole(RdmConst.YYJSBXMGLRY, ContextUtil.getCurrentUserId());
                if (isYYProjManagUsers) {
                    Map<String, Object> param = new HashMap<>();
                    param.put("deptName", RdmConst.YYJSB_NAME);
                    List<JSONObject> deptList = xcmgProjectReportDao.queryDeptByName(param);
                    if (deptList != null && !deptList.isEmpty()) {
                        deptId2Name.put(deptList.get(0).getString("GROUP_ID_"), deptList.get(0).getString("NAME_"));
                    }
                }
                params.put("deptIds", deptId2Name.keySet());
            } else {
                boolean isGYFGLD =
                    commonInfoManager.judgeUserIsPointRole(RdmConst.GY_FGLD, ContextUtil.getCurrentUserId());
                if (isGYFGLD) {
                    Map<String, Object> param = new HashMap<>();
                    param.put("deptName", RdmConst.GYJSB_NAME);
                    List<JSONObject> deptList = xcmgProjectReportDao.queryDeptByName(param);
                    if (deptList != null && !deptList.isEmpty()) {
                        params.put("deptIds", Arrays.asList(deptList.get(0).getString("GROUP_ID_")));
                    }
                }
            }
            List<Map<String, Object>> applyList = xcmgProjectChangeDao.queryChangeApplyList(params);
            // 增加不同角色和部门的人看到的数据不一样的过滤
            List<Map<String, Object>> finalAllApplyList = null;
            finalAllApplyList = xcmgProjectAbolishManager.filterApplyListByDepRole(applyList);
            if (finalAllApplyList != null && !finalAllApplyList.isEmpty()) {
                for (Map<String, Object> oneApply : finalAllApplyList) {
                    if (oneApply.get("CREATE_TIME_") != null) {
                        oneApply.put("CREATE_TIME_",
                                DateUtil.formatDate((Date)oneApply.get("CREATE_TIME_"), "yyyy-MM-dd HH:mm:ss"));
                    }
                    if (oneApply.get("applyTime") != null) {
                        oneApply.put("applyTime",
                                DateUtil.formatDate((Date)oneApply.get("applyTime"), "yyyy-MM-dd HH:mm:ss"));
                    }
                    if (oneApply.get("UPDATE_TIME_") != null) {
                        oneApply.put("UPDATE_TIME_",
                                DateUtil.formatDate((Date)oneApply.get("UPDATE_TIME_"), "yyyy-MM-dd HH:mm:ss"));
                    }
                }
            }
            List<Map<String, Object>> finalSubApplyList = new ArrayList<Map<String, Object>>();
            if(doPage) {
                // 根据分页进行subList截取
                int pageIndex = RequestUtil.getInt(request, "pageIndex", 0);
                int pageSize = RequestUtil.getInt(request, "pageSize", Page.DEFAULT_PAGE_SIZE);
                int startSubListIndex = pageIndex * pageSize;
                int endSubListIndex = startSubListIndex + pageSize;

                if (startSubListIndex < finalAllApplyList.size()) {
                    finalSubApplyList = finalAllApplyList.subList(startSubListIndex,
                            endSubListIndex < finalAllApplyList.size() ? endSubListIndex : finalAllApplyList.size());
                }
            }else {
                finalSubApplyList = finalAllApplyList;
            }
            // 返回结果
            result.setData(finalSubApplyList);
            result.setTotal(finalAllApplyList.size());
        } catch (Exception e) {
            logger.error("Exception in queryChangeApplyList", e);
            result.setSuccess(false);
            result.setMessage("系统异常");
        }
        return result;
    }


    public void exportProjectChange(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = queryChangeApplyList(request, response,false);
        List<Map<String, Object>> listData = result.getData();
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "项目变更列表";
        String excelName = nowDate + title;
        String[] fieldNames = {"申请单号", "申请人", "项目名称","牵头部门", "申请理由", "项目阶段", "是否需要分管领导审批", "是否重大变更","申请时间"};
        String[] fieldCodes = {"id", "applyUserName", "projectName","mainDeptName",
                "reason", "currentStage", "isLeader","isBigChange","applyTime"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(listData, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }
}
