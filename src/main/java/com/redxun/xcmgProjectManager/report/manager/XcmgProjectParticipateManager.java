
package com.redxun.xcmgProjectManager.report.manager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.redxun.saweb.util.RequestUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import com.redxun.xcmgProjectManager.report.dao.XcmgProjectParticipateDao;
import com.redxun.xcmgProjectManager.report.dao.XcmgProjectReportDao;

/**
 * @author lcj
 * 
 *         <pre>
 * 描述：project_baseInfo 处理接口
 * 作者:x
 * 日期:2019-08-22 08:59:49
 * 版权：xx
 *         </pre>
 */
@Service
public class XcmgProjectParticipateManager {
    private Logger logger = LoggerFactory.getLogger(XcmgProjectReportManager.class);
    @Resource
    private XcmgProjectParticipateDao xcmgProjectParticipateDao;
    @Resource
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Resource
    private XcmgProjectManager xcmgProjectManager;
    @Autowired
    private CommonInfoManager commonInfoManager;
    @Autowired
    private XcmgProjectReportDao xcmgProjectReportDao;

    /**
     * 获取人员项目参与情况查询列表
     */
    public JsonPageResult<?> participateList(HttpServletRequest request) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        getParticipateListParams(params, request, true);
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
            boolean isGYFGLD = commonInfoManager.judgeUserIsPointRole(RdmConst.GY_FGLD, ContextUtil.getCurrentUserId());
            if (isGYFGLD) {
                Map<String, Object> param = new HashMap<>();
                param.put("deptName", RdmConst.GYJSB_NAME);
                List<JSONObject> deptList = xcmgProjectReportDao.queryDeptByName(param);
                if (deptList != null && !deptList.isEmpty()) {
                    params.put("deptIds", Arrays.asList(deptList.get(0).getString("GROUP_ID_")));
                }
            }
        }
        List<JSONObject> data = xcmgProjectParticipateDao.queryParticipateList(params);
        for (JSONObject oneData : data) {
            if (StringUtils.isNotBlank(oneData.getString("createTime"))) {
                oneData.put("createTime", DateUtil.formatDate(oneData.getDate("createTime"), "yyyy-MM-dd"));
            }
            if (StringUtils.isNotBlank(oneData.getString("knotTime"))) {
                oneData.put("knotTime", DateUtil.formatDate(oneData.getDate("knotTime"), "yyyy-MM-dd"));
            }
        }
        int count = xcmgProjectParticipateDao.countParticipateList(params);
        result.setData(data);
        result.setTotal(count);
        return result;
    }

    // 将过滤条件、排序等信息传入，分页不在此处进行
    private void getParticipateListParams(Map<String, Object> params, HttpServletRequest request, boolean doPage) {
        String userRoleStr = RequestUtil.getString(request, "userRoleStr", "self");
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)) {
            params.put("sortField", sortField);
            params.put("sortOrder", sortOrder);
        }
        if (StringUtils.isEmpty(sortField)) {
            params.put("sortField", "deptId,userId,levelName");
            params.put("sortOrder", "asc");
        }
        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    // 数据库中存储的时间是UTC时间，因此需要将前台传递的北京时间转化
                    if ("startTime".equalsIgnoreCase(name)) {
                        params.put("startTimeOriginal", value);
                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), -8));
                    }
                    if ("endTime".equalsIgnoreCase(name)) {
                        params.put("endTimeOriginal", value);
                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), 16));
                    }
                    params.put(name, value);
                }
            }
        }
        // 分页
        if (doPage) {
            params.put("startIndex", 0);
            params.put("pageSize", 20);
            String pageIndex = request.getParameter("pageIndex");
            String pageSize = request.getParameter("pageSize");
            if (StringUtils.isNotBlank(pageIndex) && StringUtils.isNotBlank(pageSize)) {
                params.put("startIndex", Integer.parseInt(pageSize) * Integer.parseInt(pageIndex));
                params.put("pageSize", Integer.parseInt(pageSize));
            }
        }
        // 增加隐性条件
        params.put("userRoleStr", userRoleStr);
        params.put("currentDepId", ContextUtil.getCurrentUser().getMainGroupId());
        params.put("currentUserId", ContextUtil.getCurrentUserId());
    }

    // 返回当前登录人的数据权限（self/department/all），分别代表自己、部门和所有
    public String toQueryUserRoleStr() {
        if ("admin".equals(ContextUtil.getCurrentUser().getUserNo())) {
            return "all";
        }
        Map<String, Object> params = new HashMap<>();
        params.put("userId", ContextUtil.getCurrentUser().getUserId());
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params);
        for (Map<String, Object> oneRole : currentUserRoles) {
            if (oneRole.get("REL_TYPE_KEY_").toString().equalsIgnoreCase("GROUP-USER-LEADER")
                || oneRole.get("REL_TYPE_KEY_").toString().equalsIgnoreCase("GROUP-USER-BELONG")) {
                if (RdmConst.AllDATA_QUERY_NAME.equalsIgnoreCase(oneRole.get("NAME_").toString())
                    || RdmConst.JSZXXMGLRY.equalsIgnoreCase(oneRole.get("NAME_").toString())
                    || RdmConst.GY_FGLD.equalsIgnoreCase(oneRole.get("NAME_").toString())) {
                    return "all";
                }
            }
        }

        // 确定当前登录人是否是部门负责人
        JSONObject currentUserDepInfo = xcmgProjectManager.isCurrentUserDepRespman();
        if (!"success".equals(currentUserDepInfo.getString("result"))) {
            return "self";
        }
        boolean isDepRespMan = currentUserDepInfo.getBoolean("isDepRespMan");
        if (isDepRespMan) {
            return "department";
        }
        String currentUserMainDepName = currentUserDepInfo.getString("currentUserMainDepName");
        boolean isDepProjectManager =
            XcmgProjectUtil.judgeIsDepProjectManager(currentUserMainDepName, currentUserRoles);
        if (isDepProjectManager) {
            return "department";
        }

        return "self";
    }

    // 导出项目参与情况excel
    public void exportParticipateExcel(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> params = new HashMap<>();
        getParticipateListParams(params, request, false);
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
            boolean isGYFGLD = commonInfoManager.judgeUserIsPointRole(RdmConst.GY_FGLD, ContextUtil.getCurrentUserId());
            if (isGYFGLD) {
                Map<String, Object> param = new HashMap<>();
                param.put("deptName", RdmConst.GYJSB_NAME);
                List<JSONObject> deptList = xcmgProjectReportDao.queryDeptByName(param);
                if (deptList != null && !deptList.isEmpty()) {
                    params.put("deptIds", Arrays.asList(deptList.get(0).getString("GROUP_ID_")));
                }
            }
        }
        List<JSONObject> data = xcmgProjectParticipateDao.queryParticipateList(params);
        for (JSONObject oneData : data) {
            if (StringUtils.isNotBlank(oneData.getString("createTime"))) {
                oneData.put("createTime", DateUtil.formatDate(oneData.getDate("createTime"), "yyyy-MM-dd"));
            }
            if (StringUtils.isNotBlank(oneData.getString("knotTime"))) {
                oneData.put("knotTime", DateUtil.formatDate(oneData.getDate("knotTime"), "yyyy-MM-dd"));
            }
            if (StringUtils.isNotBlank(oneData.getString("projectStatus"))) {
                oneData.put("projectStatus",
                    XcmgProjectUtil.convertProjectStatusCode2Name(oneData.getString("projectStatus")));
            }
        }
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "科技项目参与情况统计表";
        String excelName = nowDate + title;
        String[] fieldNames = {"员工姓名", "员工部门", "项目名称", "项目类别", "项目级别", "项目牵头部门", "项目状态", "项目角色", "创建时间", "结项时间"};
        String[] fieldCodes = {"userName", "deptName", "projectName", "categoryName", "levelName", "mainDepName",
            "projectStatus", "roleName", "createTime", "knotTime"};
        if (params.get("startTimeOriginal") != null || params.get("endTimeOriginal") != null) {
            String startTime = "";
            String endTime = "";
            if (params.get("startTimeOriginal") != null) {
                startTime = params.get("startTimeOriginal").toString();
            }
            if (params.get("endTimeOriginal") != null) {
                endTime = params.get("endTimeOriginal").toString();
            }
            title += "（" + startTime + "~" + endTime + "）";
        }
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(data, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }
}
