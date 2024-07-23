
package com.redxun.xcmgProjectManager.core.manager;

import java.util.*;

import javax.annotation.Resource;
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
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectScoreDao;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import com.redxun.xcmgProjectManager.report.dao.XcmgProjectReportDao;

/**
 * <pre>
 * 描述：project_baseInfo 处理接口
 * 作者:x
 * 日期:2019-08-22 08:59:49
 * 版权：xx
 * </pre>
 */
@Service
public class XcmgProjectScoreManager {
    private Logger logger = LoggerFactory.getLogger(XcmgProjectScoreManager.class);
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private XcmgProjectScoreDao xcmgProjectScoreDao;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;
    @Autowired
    private CommonInfoManager commonInfoManager;
    @Resource
    private XcmgProjectReportDao xcmgProjectReportDao;

    // 查询列表
    public JsonPageResult<?> getScoreList(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            Map<String, Object> queryParams = new HashMap<>();
            getScoreListParams(queryParams, request);
            /*增加权限判断过滤
            // 1、对于admin、分管领导、积分查看人员，直接查询。
            // 2、对于部门负责人，如果条件中没有部门条件，则增加部门条件后查询；如果有部门条件，部门不为本部门则返回空，等于本部门则查询。
            // 3、对于其他人，增加userId的过滤条件后查询。
            */

            // admin直接查询
            if ("admin".equals(ContextUtil.getCurrentUser().getUserNo())) {
                List<Map<String, Object>> pageDataList = xcmgProjectScoreDao.queryUserProjectScore(queryParams);
                result.setData(pageDataList);
                queryParams.remove("startIndex");
                queryParams.remove("pageSize");
                List<Map<String, Object>> totalDataList = xcmgProjectScoreDao.queryUserProjectScore(queryParams);
                result.setTotal(totalDataList.size());
                return result;
            }

            // 查询角色，确定是否是特权账号
            Map<String, Object> roleFilterParams = new HashMap<>();
            roleFilterParams.put("userId", ContextUtil.getCurrentUser().getUserId());
            roleFilterParams.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(roleFilterParams);
            boolean showAll = false;
            for (Map<String, Object> oneRole : currentUserRoles) {
                if (oneRole.get("REL_TYPE_KEY_").toString().equalsIgnoreCase("GROUP-USER-LEADER")
                    || oneRole.get("REL_TYPE_KEY_").toString().equalsIgnoreCase("GROUP-USER-BELONG")) {
                    if (RdmConst.AllDATA_QUERY_NAME.equalsIgnoreCase(oneRole.get("NAME_").toString())
                        || RdmConst.JSZXXMGLRY.equalsIgnoreCase(oneRole.get("NAME_").toString())
                        || RdmConst.GY_FGLD.equalsIgnoreCase(oneRole.get("NAME_").toString())) {
                        showAll = true;
                        break;
                    }
                }
            }
            if (showAll) {
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
                    queryParams.put("deptIds", deptId2Name.keySet());
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
                    queryParams.put("deptIds", deptId2Name.keySet());
                } else {
                    boolean isGYFGLD =
                        commonInfoManager.judgeUserIsPointRole(RdmConst.GY_FGLD, ContextUtil.getCurrentUserId());
                    if (isGYFGLD) {
                        Map<String, Object> param = new HashMap<>();
                        param.put("deptName", RdmConst.GYJSB_NAME);
                        List<JSONObject> deptList = xcmgProjectReportDao.queryDeptByName(param);
                        if (deptList != null && !deptList.isEmpty()) {
                            queryParams.put("deptIds", Arrays.asList(deptList.get(0).getString("GROUP_ID_")));
                        }
                    }
                }
                List<Map<String, Object>> pageDataList = xcmgProjectScoreDao.queryUserProjectScore(queryParams);
                result.setData(pageDataList);
                queryParams.remove("startIndex");
                queryParams.remove("pageSize");
                List<Map<String, Object>> totalDataList = xcmgProjectScoreDao.queryUserProjectScore(queryParams);
                result.setTotal(totalDataList.size());
                return result;
            }

            // 部门负责人
            JSONObject currentUserDepInfo = xcmgProjectManager.isCurrentUserDepRespman();
            if (!"success".equals(currentUserDepInfo.getString("result"))) {
                result.setData(Collections.emptyList());
                result.setTotal(0);
                return result;
            }
            boolean isDepRespMan = currentUserDepInfo.getBoolean("isDepRespMan");
            String currentUserMainDepId = currentUserDepInfo.getString("currentUserMainDepId");
            String currentUserMainDepName = currentUserDepInfo.getString("currentUserMainDepName");
            if (isDepRespMan) {
                List<String> userDepIdList = queryParams.get("userDepId") == null ? Collections.EMPTY_LIST
                    : (List<String>)queryParams.get("userDepId");
                // 不为空且不包含本部门，则返回空
                if (!userDepIdList.isEmpty() && !userDepIdList.contains(currentUserMainDepId)) {
                    result.setData(Collections.emptyList());
                    result.setTotal(0);
                    return result;
                }
                // 否则查询本部门的
                List<String> selfUserDepIdList = new ArrayList<>();
                selfUserDepIdList.add(currentUserMainDepId);
                queryParams.put("userDepId", selfUserDepIdList);
                List<Map<String, Object>> pageDataList = xcmgProjectScoreDao.queryUserProjectScore(queryParams);
                result.setData(pageDataList);
                queryParams.remove("startIndex");
                queryParams.remove("pageSize");
                List<Map<String, Object>> totalDataList = xcmgProjectScoreDao.queryUserProjectScore(queryParams);
                result.setTotal(totalDataList.size());
                return result;
            }

            // 普通员工
            queryParams.put("userId", ContextUtil.getCurrentUser().getUserId());
            List<Map<String, Object>> pageDataList = xcmgProjectScoreDao.queryUserProjectScore(queryParams);
            result.setData(pageDataList);
            queryParams.remove("startIndex");
            queryParams.remove("pageSize");
            List<Map<String, Object>> totalDataList = xcmgProjectScoreDao.queryUserProjectScore(queryParams);
            result.setTotal(totalDataList.size());
            return result;
        } catch (Exception e) {
            logger.error("Exception in getScoreList", e);
            result.setData(Collections.emptyList());
            result.setTotal(0);
            result.setSuccess(false);
            return result;
        }
    }

    // 将过滤条件、排序等信息传入，分页不在此处进行
    private void getScoreListParams(Map<String, Object> params, HttpServletRequest request) {
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());

        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)) {
            params.put("sortField", sortField);
            params.put("sortOrder", sortOrder);
        } else {
            params.put("sortField", "depId,userId,projectId");
        }

        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    if ("userDepId".equalsIgnoreCase(name)) {
                        String[] userDepIdArr = value.split(",", -1);
                        params.put(name, Arrays.asList(userDepIdArr));
                    } else if ("startTime".equalsIgnoreCase(name)) {
                        params.put(name, DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), -8)));
                    } else if ("endTime".equalsIgnoreCase(name)) {
                        params.put(name, DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), 16)));
                    } else {
                        params.put(name, value);
                    }
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

    public void exportProjectScoreExcel(HttpServletResponse response, HttpServletRequest request) {
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String excelName = nowDate + "员工项目积分列表";
        String title = "员工项目积分列表";
        List<Map<String, Object>> list = getExportScoreList(request, response);
        if (list != null && list.size() > 0) {
            JSONObject jsonObject;
            String statusName = "";
            for (int i = 0; i < list.size(); i++) {
                jsonObject = XcmgProjectUtil.convertMap2JsonObject(list.get(i));
                switch (jsonObject.getString("projectStatus")) {
                    case "DRAFTED":
                        statusName = "草稿";
                        break;
                    case "RUNNING":
                        statusName = "运行中";
                        break;
                    case "SUCCESS_END":
                        statusName = "成功结束";
                        break;
                    case "DISCARD_END":
                        statusName = "作废";
                        break;
                    case "ABORT_END":
                        statusName = "异常中止结束";
                        break;
                    case "PENDING":
                        statusName = "挂起";
                        break;
                }
                list.get(i).put("projectStatus", statusName);
            }
        }
        String[] fieldNames = {"员工部门", "员工姓名", "项目编号", "项目名称", "项目类别", "项目级别", "项目牵头部门", "项目状态", "承担角色", "获得积分","自筹财务订单号"};
        String[] fieldCodes = {"depName", "userName", "number", "projectName", "categoryName", "levelName",
            "projectDepName", "projectStatus", "roleName", "userProjectScore","cwddh"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(list, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }

    /**
     * 导出列表查询
     */
    public List<Map<String, Object>> getExportScoreList(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            Map<String, Object> queryParams = new HashMap<>();
            getScoreListParams(queryParams, request);
            queryParams.remove("startIndex");
            queryParams.remove("pageSize");
            // admin直接查询
            if ("admin".equals(ContextUtil.getCurrentUser().getUserNo())) {
                List<Map<String, Object>> totalDataList = xcmgProjectScoreDao.queryUserProjectScore(queryParams);
                return totalDataList;
            }
            // 查询角色，确定是否是特权账号
            Map<String, Object> roleFilterParams = new HashMap<>();
            roleFilterParams.put("userId", ContextUtil.getCurrentUser().getUserId());
            roleFilterParams.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(roleFilterParams);
            boolean showAll = false;
            for (Map<String, Object> oneRole : currentUserRoles) {
                if (oneRole.get("REL_TYPE_KEY_").toString().equalsIgnoreCase("GROUP-USER-LEADER")
                    || oneRole.get("REL_TYPE_KEY_").toString().equalsIgnoreCase("GROUP-USER-BELONG")) {
                    if ("分管领导".equalsIgnoreCase(oneRole.get("NAME_").toString())
                        || RdmConst.JSZXXMGLRY.equalsIgnoreCase(oneRole.get("NAME_").toString())) {
                        showAll = true;
                        break;
                    }
                }
            }
            if (showAll) {
                List<Map<String, Object>> totalDataList = xcmgProjectScoreDao.queryUserProjectScore(queryParams);
                return totalDataList;
            }
            // 部门负责人
            JSONObject currentUserDepInfo = xcmgProjectManager.isCurrentUserDepRespman();
            if (!"success".equals(currentUserDepInfo.getString("result"))) {
                return Collections.emptyList();
            }
            boolean isDepRespMan = currentUserDepInfo.getBoolean("isDepRespMan");
            String currentUserMainDepId = currentUserDepInfo.getString("currentUserMainDepId");
            String currentUserMainDepName = currentUserDepInfo.getString("currentUserMainDepName");
            if (isDepRespMan) {
                String depQueryParam =
                    queryParams.get("userDepId") == null ? "" : queryParams.get("userDepId").toString();
                if (StringUtils.isNotBlank(depQueryParam) && !depQueryParam.equals(currentUserMainDepId)) {
                    return Collections.emptyList();
                }
                queryParams.put("userDepId", Arrays.asList(currentUserMainDepId));
                List<Map<String, Object>> totalDataList = xcmgProjectScoreDao.queryUserProjectScore(queryParams);
                return totalDataList;
            }
            // 普通员工
            queryParams.put("userId", ContextUtil.getCurrentUser().getUserId());
            List<Map<String, Object>> totalDataList = xcmgProjectScoreDao.queryUserProjectScore(queryParams);
            return totalDataList;
        } catch (Exception e) {
            logger.error("Exception in getExportScoreList", e);
            return Collections.emptyList();
        }
    }

    // 通过projectId和userId查询该用户在本项目所有阶段获得积分情况
    public List<Map<String, Object>> queryUserProjectStageScore(Map<String, Object> params) {
        List<Map<String, Object>> queryResult = xcmgProjectScoreDao.queryUserProjectStageScore(params);
        for (Map<String, Object> oneMap : queryResult) {
            if (oneMap.get("scoreGetTime") != null) {
                oneMap.put("scoreGetTime", DateUtil.formatDate((Date)oneMap.get("scoreGetTime"), "yyyy-MM-dd"));
            }
        }
        return queryResult;
    }
}
