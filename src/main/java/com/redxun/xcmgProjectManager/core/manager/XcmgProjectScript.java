package com.redxun.xcmgProjectManager.core.manager;

import java.util.*;

import javax.annotation.Resource;

import com.redxun.sys.core.entity.SysDic;
import com.redxun.sys.core.manager.SysDicManager;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.AbstractExecutionCmd;
import com.redxun.bpm.core.entity.IExecutionCmd;
import com.redxun.bpm.core.entity.ProcessNextCmd;
import com.redxun.bpm.core.entity.TaskExecutor;
import com.redxun.core.script.GroovyScript;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmCommon.core.manager.LoginRecordManager;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectDao;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.util.ConstantUtil;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

// 项目流程节点的触发事件
@Service
public class XcmgProjectScript implements GroovyScript {
    private Logger logger = LoggerFactory.getLogger(XcmgProjectHandler.class);
    @Autowired
    private CommonBpmManager commonBpmManager;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Resource
    private XcmgProjectFileUploadManager xcmgProjectFileUploadManager;
    @Resource
    private XcmgProjectManager xcmgProjectManager;
    @Autowired
    private XcmgProjectDao xcmgProjectDao;
    @Autowired
    private LoginRecordManager loginRecordManager;
    @Autowired
    private CommonInfoDao commonInfoDao;
    @Resource
    private CommonInfoManager commonInfoManager;
    @Autowired
    private SysDicManager sysDicManager;

    // 任务创建事件（在此处需要进行节点变量stageNo获取用来刷新当前阶段，同时需要刷新项目计划中的原阶段的实际结束时间和现阶段的实际开始时间）
    public void taskCreateScript(Map<String, Object> vars) {
        logger.info("taskCreateScript");
        String busKey = vars.get("busKey").toString();
        AbstractExecutionCmd cmd = (AbstractExecutionCmd)vars.get("cmd");
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        if (StringUtils.isNotBlank(formDataJson.getString("projectId"))) {
            busKey = formDataJson.getString("projectId");
        }
        String oldStageNo = formDataJson.getString("currentStageNo");
        String oldStageId = formDataJson.getString("currentStageId");
        String categoryId = formDataJson.getString("categoryId");
        // 查找该节点上的所有变量
        String solId = vars.get("solId").toString();
        String nodeId = vars.get("activityId").toString();
        List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId, null, solId);
        if (nodeVars == null || nodeVars.isEmpty()) {
            logger.warn("no vars in node {}", nodeId);
            return;
        }
        // 查找变量stageNo
        String newStageNo = "";
        for (Map<String, String> oneVar : nodeVars) {
            if ("stageNo".equals(oneVar.get("KEY_"))) {
                newStageNo = oneVar.get("DEF_VAL_");
                break;
            }
        }
        if (StringUtils.isBlank(newStageNo)) {
            logger.info("stageNo in node {} is blank", nodeId);
            return;
        }
        // 根据stageNo是否变化进行业务表数据的刷新
        if (newStageNo.equals(oldStageNo)) {
            return;
        }
        // 根据类别Id和stageNo查询新的stageId
        Map<String, Object> params = new HashMap<>();
        params.put("categoryId", categoryId);
        params.put("stageNo", newStageNo);
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        Map<String, String> newStageInfo = xcmgProjectOtherDao.queryStageByCategoryAndNo(params);
        if (newStageInfo == null || newStageInfo.isEmpty()) {
            logger.error("can't find stageInfo by stageNo {} and categoryId {}", newStageNo, categoryId);
            return;
        }
        String newStageId = newStageInfo.get("stageId");
        // 刷新projectBase表
        params.clear();
        params.put("currentStageId", newStageId);
        params.put("currentStageNo", newStageNo);
        params.put("projectId", busKey);
        xcmgProjectOtherDao.updateProjectStageInfo(params);

        String currentLocalTime = XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd");
        String currentUtcTime = DateUtil.formatDate(
            DateUtil.addHour(DateUtil.parseDate(currentLocalTime, "yyyy-MM-dd"), -8), "yyyy-MM-dd HH:mm:ss");
        // 刷新projectPlan表中新阶段的实际开始时间
        params.clear();
        params.put("actualStartTime", currentUtcTime);
        params.put("stageId", newStageId);
        params.put("projectId", busKey);
        xcmgProjectOtherDao.updateProjectPlanTime(params);

        // 如果是从后面阶段驳回到前面阶段的场景，则进行积分和实际开始结束时间相应的清理动作。2020-04-01
        if (oldStageNo.compareTo(newStageNo) > 0) {
            processAfterReject(busKey, newStageNo);
            return;
        }

        // 刷新projectPlan表中老阶段的实际结束时间
        if (StringUtils.isNotBlank(oldStageId)) {
            params.clear();
            params.put("actualEndTime", currentUtcTime);
            params.put("stageId", oldStageId);
            params.put("projectId", busKey);
            xcmgProjectOtherDao.updateProjectPlanTime(params);
        }

        // 创建新阶段的文件夹（如果不存在）
        Map<String, String> fileInfo = new HashMap<>();
        fileInfo.put("fileName", newStageInfo.get("stageName"));
        fileInfo.put("id", newStageInfo.get("stageId"));
        fileInfo.put("pid", busKey);
        fileInfo.put("isFolder", "1");
        fileInfo.put("projectId", busKey);
        fileInfo.put("relativeFilePath", "/" + busKey);
        fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        fileInfo.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        fileInfo.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        xcmgProjectFileUploadManager.createFolder(fileInfo, false);

        // 计算老阶段项目的进度延误扣分和每个人的得分
        if (StringUtils.isNotBlank(oldStageNo)) {
            xcmgProjectManager.processProjectScore(formDataJson, false);
        }
    }

    /**
     * 将驳回到节点所处阶段和之后的阶段的扣分情况、积分情况和实际结束时间都清除，将之后阶段的实际开始时间清除
     *
     * @param projectId
     * @param stageNo
     */
    private void processAfterReject(String projectId, String stageNo) {
        Map<String, Object> param = new HashMap<>();
        param.put("projectId", projectId);
        param.put("stageNo", stageNo);

        // 清理本阶段和之后阶段的扣分、实际结束时间
        xcmgProjectOtherDao.updatePlanDeductScoreAndEndTime(param);
        // 清理之后阶段的实际开始时间
        xcmgProjectOtherDao.deleteUserScore(param);
        // 清理本阶段和之后阶段的个人积分
        xcmgProjectOtherDao.updatePlanStartTime(param);
    }

    // 任务完成事件
    public void taskCompleteScript(Map<String, Object> vars) {
        logger.info("taskCompleteScript");
    }

    // 项目立项结束之后的动作（生成项目编号、刷新基本信息中的立项完成时间）,配置在流程中的立项（策划）阶段的归档节点中
    public void projectBuildCompleteScript(Map<String, Object> vars) {
        ProcessNextCmd cmd = (ProcessNextCmd)vars.get("cmd");
        if (cmd == null || StringUtils.isBlank(cmd.getJumpType()) || !"AGREE".equalsIgnoreCase(cmd.getJumpType())) {
            logger.error("cmd is null or jumpType is blank or not agree");
            return;
        }

        String projectId = vars.get("busKey").toString();
        Map<String, Object> params = new HashMap<>();

        // 生成项目编号，如果页面填写则不再生成2020-1-12 by liangchuanjiang
        StringBuilder projectNo = new StringBuilder();
        JSONObject formData = cmd.getJsonDataObject();
        if (formData != null && StringUtils.isNotBlank(formData.getString("number"))) {
            projectNo.append(formData.getString("number"));
        } else {
            params.put("projectId", projectId);
            params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            // 生成项目编号
            List<Map<String, String>> projectInfo = xcmgProjectOtherDao.queryForProjectNo(params);
            if (projectInfo == null || projectInfo.isEmpty()) {
                logger.error("Can't find projectInfos for no, projectId is {}", projectId);
                return;
            }
            String sourceCode = projectInfo.get(0).get("sourceCode");
            String levelPName = projectInfo.get(0).get("levelPName");
            String categoryCode = projectInfo.get(0).get("categoryCode");
            // 取本地时间
            String buildYear = XcmgProjectUtil.getNowLocalDateStr("yyyy");
            params.clear();
            // 查询一定条件下当前已有的项目个数（排除自己）
            params.put("sourceCode", sourceCode);
            params.put("levelPName", levelPName);
            params.put("buildYear", buildYear);
            params.put("categoryCode", categoryCode);
            params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            params.put("projectId", projectId);
            int hasNoProjectCount = xcmgProjectOtherDao.countForProjectNo(params);
            projectNo.append(sourceCode).append("-").append(levelPName).append("-").append(buildYear).append("-")
                .append(categoryCode).append("-").append(hasNoProjectCount + 1);
        }

        // 更新基本信息表(编号和立项时间)
        String currentUtcTime = XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss");
        params.clear();
        params.put("buildTime", currentUtcTime);
        params.put("projectId", projectId);
        params.put("projectNo", projectNo.toString());
        xcmgProjectOtherDao.updateProjectBuild(params);
    }

    // 项目来源网关判断(非自主申报类)
    public boolean notSelfApplySource(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String sourceId = formDataJson.getString("sourceId");
        Map<String, Object> params = new HashMap<>();
        params.put("sourceId", sourceId);
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        List<Map<String, String>> sourceInfos = xcmgProjectOtherDao.queryProjectSources(params);
        String sourceName = sourceInfos.get(0).get("sourceName");
        if ("自主申报类".equals(sourceName)) {
            return false;
        }
        return true;
    }

    // 项目来源网关判断(自主申报类)
    public boolean selfApplySource(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String sourceId = formDataJson.getString("sourceId");
        Map<String, Object> params = new HashMap<>();
        params.put("sourceId", sourceId);
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        List<Map<String, String>> sourceInfos = xcmgProjectOtherDao.queryProjectSources(params);
        String sourceName = sourceInfos.get(0).get("sourceName");
        if ("自主申报类".equals(sourceName)) {
            return true;
        }
        return false;
    }

    // 项目类别网关判断(产品研发设计类)
    public boolean cpyfSJ(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String categoryName = getCategoryNameById(cmd, vars);
        if ("产品研发（整机）类".equals(categoryName)) {
            return true;
        }
        return false;
    }

    // 项目类别网关判断(产品研发工艺类)
    public boolean cpyfGY(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String categoryName = getCategoryNameById(cmd, vars);
        if ("产品研发（工艺）类".equals(categoryName)) {
            return true;
        }
        return false;
    }

    // 项目类别网关判断(技术研发（产品技术）类)
    public boolean jsyfCPJS(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String categoryName = getCategoryNameById(cmd, vars);
        if ("技术研发（产品技术）类".equals(categoryName)) {
            return true;
        }
        return false;
    }

    // 项目类别网关判断(技术研发（工艺技术）类)
    public boolean jsyfGYJS(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String categoryName = getCategoryNameById(cmd, vars);
        if ("技术研发（工艺技术）类".equals(categoryName)) {
            return true;
        }
        return false;
    }

    // 项目类别网关判断(技术研发（测试技术）类)
    public boolean jsyfCSJS(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String categoryName = getCategoryNameById(cmd, vars);
        if ("技术研发（测试技术）类".equals(categoryName)) {
            return true;
        }
        return false;
    }

    // 项目类别网关判断(技术研发（仿真技术）类)
    public boolean jsyfFZJS(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String categoryName = getCategoryNameById(cmd, vars);
        if ("技术研发（仿真技术）类".equals(categoryName)) {
            return true;
        }
        return false;
    }

    // 项目类别网关判断(工艺规划类)
    public boolean gygh(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String categoryName = getCategoryNameById(cmd, vars);
        if ("工艺规划类".equals(categoryName)) {
            return true;
        }
        return false;
    }

    // 项目类别网关判断(技术改进类)
    public boolean jsgj(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String categoryName = getCategoryNameById(cmd, vars);
        if ("技术改进类".equals(categoryName)) {
            return true;
        }
        return false;
    }

    // 项目类别网关判断(标准技术类)
    public boolean bzjs(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String categoryName = getCategoryNameById(cmd, vars);
        if ("标准技术类".equals(categoryName)) {
            return true;
        }
        return false;
    }

    // 项目类别网关判断(信息技术（实施开发）类)
    public boolean xxjsSSKF(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String categoryName = getCategoryNameById(cmd, vars);
        if ("信息技术（实施开发）类".equals(categoryName)) {
            return true;
        }
        return false;
    }

    // 项目类别网关判断(信息技术（系统实施）类)
    public boolean xxjsXTSS(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String categoryName = getCategoryNameById(cmd, vars);
        if ("信息技术（系统实施）类".equals(categoryName)) {
            return true;
        }
        return false;
    }

    // 项目类别网关判断(信息技术（网络硬件实施）类)
    public boolean xxjsWLYJSS(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String categoryName = getCategoryNameById(cmd, vars);
        if ("信息技术（网络硬件实施）类".equals(categoryName)) {
            return true;
        }
        return false;
    }

    // 项目类别网关判断(信息技术（体系建设及咨询）类)
    public boolean xxjsTXJSJZX(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String categoryName = getCategoryNameById(cmd, vars);
        if ("信息技术（体系建设及咨询）类".equals(categoryName)) {
            return true;
        }
        return false;
    }

    // 项目类别网关判断(其他专项类)
    public boolean qtzx(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String categoryName = getCategoryNameById(cmd, vars);
        if ("其他专项类".equals(categoryName)) {
            return true;
        }
        return false;
    }

    // 项目类别网关判断(其他专项类新版)
    public boolean nqtzx(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String categoryName = getCategoryNameById(cmd, vars);
        if ("其他专项类(新版)".equals(categoryName)) {
            return true;
        }
        return false;
    }

    // 项目类别网关判断(特殊项目)
    public boolean special(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String categoryName = getCategoryNameById(cmd, vars);
        if ("特殊项目类".equals(categoryName)) {
            return true;
        }
        return false;
    }

    // 项目类别网关判断(产学研项目)
    public boolean cxy(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String categoryName = getCategoryNameById(cmd, vars);
        if (categoryName.contains(RdmConst.CXY)) {
            return true;
        }
        return false;
    }

    public boolean newqtzx(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String categoryName = getCategoryNameById(cmd, vars);
        if ("其他专项类(新版)".equals(categoryName)) {
            return true;
        }
        return false;
    }

    // 项目类别网关判断(特殊项目)
    public boolean oldqtzx(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String categoryName = getCategoryNameById(cmd, vars);
        if ("其他专项类".equals(categoryName)) {
            return true;
        }
        return false;
    }

    // 项目初审网关判断(需要初审)
    public boolean needChuShen(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String levelId = formDataJson.getString("levelId");
        Map<String, Object> params = new HashMap<>();
        params.put("levelId", levelId);
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        List<Map<String, String>> levelInfos = xcmgProjectOtherDao.queryProjectLevel(params);
        String levelName = levelInfos.get(0).get("levelName");
        if (levelName.contains("A") || levelName.contains("B")) {
            return true;
        }
        return false;
    }

    // 项目初审网关判断(不需要初审)
    public boolean notNeedChuShen(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String levelId = formDataJson.getString("levelId");
        Map<String, Object> params = new HashMap<>();
        params.put("levelId", levelId);
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        List<Map<String, String>> levelInfos = xcmgProjectOtherDao.queryProjectLevel(params);
        String levelName = levelInfos.get(0).get("levelName");
        if (levelName.contains("C")) {
            return true;
        }
        return false;
    }

    // 项目审核网关判断(A/B级项目)
    public boolean needShenHe(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String levelId = formDataJson.getString("levelId");
        Map<String, Object> params = new HashMap<>();
        params.put("levelId", levelId);
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        List<Map<String, String>> levelInfos = xcmgProjectOtherDao.queryProjectLevel(params);
        String levelName = levelInfos.get(0).get("levelName");
        if (levelName.contains("A") || levelName.contains("B")) {
            return true;
        }
        return false;
    }

    // 项目审核网关判断(C级项目)
    public boolean notNeedShenHe(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String levelId = formDataJson.getString("levelId");
        Map<String, Object> params = new HashMap<>();
        params.put("levelId", levelId);
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        List<Map<String, String>> levelInfos = xcmgProjectOtherDao.queryProjectLevel(params);
        String levelName = levelInfos.get(0).get("levelName");
        if (levelName.contains("C")) {
            return true;
        }
        return false;
    }

    /**
     * 变更流程,是否需要分管领导审批
     */
    public boolean tecManageChangeApprove(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String isLeader = formDataJson.getString("isLeader");
        if ("否".equalsIgnoreCase(isLeader)) {
            return true;
        }
        return false;
    }

    /**
     * 变更,是否需要分管领导审批
     */
    public boolean leaderChangeApprove(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String isLeader = formDataJson.getString("isLeader");
        if ("是".equalsIgnoreCase(isLeader)) {
            return true;
        }
        return false;
    }


    /**
     * 作废流程，不是产学研项目或者C类的技术管理部批准
     */
    public boolean tecManageApprove(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String projectType = formDataJson.getString("projectType");
        if (StringUtils.isNotBlank(projectType) && projectType.contains(RdmConst.CXY)) {
            return false;
        }
        String levelName = formDataJson.getString("projectLevel");
        if (levelName.contains(ConstantUtil.LEVEL_C)) {
            return true;
        }
        return false;
    }

    /**
     * 作废流程，产学研项目或者A/B类的分管领导批准
     */
    public boolean leaderApprove(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String projectType = formDataJson.getString("projectType");
        if (StringUtils.isNotBlank(projectType) && projectType.contains(RdmConst.CXY)) {
            return true;
        }
        String levelName = formDataJson.getString("projectLevel");
        if (levelName.contains(ConstantUtil.LEVEL_A) || levelName.contains(ConstantUtil.LEVEL_B)) {
            return true;
        }
        return false;
    }


    // 科技项目申请流程中，项目负责人的获取
    public Collection<TaskExecutor> getProRespUser() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String respManId = formDataJson.getString("respman");
        if (StringUtils.isNotBlank(respManId)) {
            String respManName = formDataJson.getString("respmanName");
            users.add(new TaskExecutor(respManId, respManName));
        }
        return users;
    }

    // 科技项目申请流程中，项目牵头部门负责人的获取
    public Collection<TaskExecutor> getMainDepRespUser() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        Map<String, Object> queryDepRespMan = new HashMap<>();
        queryDepRespMan.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        queryDepRespMan.put("GROUP_ID_", formDataJson.getString("mainDepId"));
        List<Map<String, String>> depRespMans = xcmgProjectOtherDao.getDepRespManById(queryDepRespMan);
        if (depRespMans == null || depRespMans.isEmpty()) {
            logger.error("Can't find depRespMan, depId is {}", formDataJson.getString("mainDepId"));
            return users;
        }
        for (Map<String, String> depRespMan : depRespMans) {
            users.add(new TaskExecutor(depRespMan.get("PARTY2_"), depRespMan.get("FULLNAME_")));
        }
        return users;
    }

    public Collection<TaskExecutor> getMemberDepRespUser() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        JSONArray memberArr = formDataJson.getJSONArray("SUB_project_memberInfo");
        if (memberArr == null || memberArr.isEmpty()) {
            return users;
        }
        Set<String> memberDeptIds = new HashSet<>();
        // 如果是产品研发类&不是牵头部门&不是技术中心下的部门，则不需要部门负责人进行成员的选择
        String categoryId = formDataJson.getString("categoryId");
        String mainDepId = formDataJson.getString("mainDepId");
        for (int index = 0; index < memberArr.size(); index++) {
            String deptId = memberArr.getJSONObject(index).getString("memberDeptId");
            Boolean isTech = commonInfoManager.judgeIsTechDept(deptId);
            if (ConstantUtil.PRODUCT_CATEGORY.equals(categoryId)) {
                if (deptId.equals(mainDepId)) {
                    memberDeptIds.add(deptId);
                } else if (isTech) {
                    memberDeptIds.add(deptId);
                }
            } else {
                memberDeptIds.add(deptId);
            }
        }
        Map<String, Object> queryDepRespMan = new HashMap<>();
        queryDepRespMan.put("groupIds", memberDeptIds);
        List<Map<String, String>> depRespMans = xcmgProjectOtherDao.getDepRespManById(queryDepRespMan);
        if (depRespMans == null || depRespMans.isEmpty()) {
            logger.error("Can't find depRespMan");
            return users;
        }
        for (Map<String, String> depRespMan : depRespMans) {
            users.add(new TaskExecutor(depRespMan.get("PARTY2_"), depRespMan.get("FULLNAME_")));
        }
        return users;
    }

    // 查找需要项目打分确认的成员（去掉项目负责人和项目指导人）
    public Collection<TaskExecutor> getProValidMemberUser() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        JSONArray memberArr = formDataJson.getJSONArray("SUB_project_memberInfo");
        if (memberArr == null || memberArr.isEmpty()) {
            return users;
        }
        Set<String> userIds = new HashSet<>();
        JSONObject respUser = null;
        String categoryId = formDataJson.getString("categoryId");
        String mainDepId = formDataJson.getString("mainDepId");
        for (int index = 0; index < memberArr.size(); index++) {
            JSONObject oneMember = memberArr.getJSONObject(index);
            String userValid = oneMember.getString("userValid");
            if (StringUtils.isNotBlank(userValid) && "02".equalsIgnoreCase(userValid)) {
                continue;
            }
            String roleName = oneMember.getString("roleName");
            if (StringUtils.isNotBlank(roleName) && "项目负责人".equalsIgnoreCase(roleName)) {
                respUser = oneMember;
            }
            if (StringUtils.isNotBlank(roleName)
                && ("项目负责人".equalsIgnoreCase(roleName) || "项目指导人".equalsIgnoreCase(roleName))) {
                continue;
            }
            // 非挖掘机械研究院的，并且主部门不是当前部门的不需要参与打分
            String deptId = oneMember.getString("memberDeptId");
            Boolean isTech = commonInfoManager.judgeIsTechDept(deptId);
            if (ConstantUtil.PRODUCT_CATEGORY.equals(categoryId)) {
                if (deptId.equals(mainDepId)) {
                    users.add(new TaskExecutor(oneMember.getString("userId"), oneMember.getString("userName")));
                    userIds.add(oneMember.getString("userId"));
                } else if (isTech) {
                    users.add(new TaskExecutor(oneMember.getString("userId"), oneMember.getString("userName")));
                    userIds.add(oneMember.getString("userId"));
                }
            } else {
                users.add(new TaskExecutor(oneMember.getString("userId"), oneMember.getString("userName")));
                userIds.add(oneMember.getString("userId"));
            }
        }
        // 处理没有项目成员的场景
        if (users.isEmpty()) {
            users.add(new TaskExecutor(respUser.getString("userId"), respUser.getString("userName")));
            return users;
        }
        // 对于离职的人，过滤掉
        JSONObject param = new JSONObject();
        param.put("userIds", new ArrayList<>(userIds));
        List<JSONObject> userList = xcmgProjectOtherDao.queryUserByIds(param);
        Map<String, JSONObject> userId2Obj = new HashMap<>();
        for (JSONObject oneUser : userList) {
            userId2Obj.put(oneUser.getString("USER_ID_"), oneUser);
        }
        Iterator iterator = users.iterator();
        while (iterator.hasNext()) {
            TaskExecutor oneUser = (TaskExecutor)iterator.next();
            JSONObject userObj = userId2Obj.get(oneUser.getId());
            if (!"IN_JOB".equalsIgnoreCase(userObj.getString("STATUS_"))) {
                iterator.remove();
            }
        }
        if (users.isEmpty()) {
            users.add(new TaskExecutor(respUser.getString("userId"), respUser.getString("userName")));
            return users;
        }
        return users;
    }

    // 获取流程中使用的处理人为部门负责人的信息(需要修正)
    public Collection<TaskExecutor> getDepRespUser() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String projectId = formDataJson.getString("projectId");
        JSONObject xcmgProject = xcmgProjectDao.queryProjectById(projectId);
        Map<String, Object> queryDepRespMan = new HashMap<>(16);
        queryDepRespMan.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        queryDepRespMan.put("GROUP_ID_", xcmgProject.getString("mainDepId"));
        List<Map<String, String>> depRespMans = xcmgProjectOtherDao.getDepRespManById(queryDepRespMan);
        if (depRespMans == null || depRespMans.isEmpty()) {
            logger.error("Can't find depRespMan, depId is {}");
            return users;
        }
        for (Map<String, String> depRespMan : depRespMans) {
            users.add(new TaskExecutor(depRespMan.get("PARTY2_"), depRespMan.get("FULLNAME_")));
        }
        return users;
    }

    private String getCategoryNameById(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String categoryId = formDataJson.getString("categoryId");
        Map<String, Object> params = new HashMap<>();
        params.put("categoryId", categoryId);
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        List<Map<String, String>> categoryInfos = xcmgProjectOtherDao.queryProjectCategory(params);
        String categoryName = categoryInfos.get(0).get("categoryName");
        // 增加项目负责人流程变量的赋值
        // cmd.getVars().put("proRespUserId", formDataJson.getString("respman"));
        // vars.put("proRespUserId", formDataJson.getString("respman"));
        return categoryName;
    }

    // 科技项目申请流程中，立项批准人员(工艺技术部的为“工艺分管领导”)
    public Collection<TaskExecutor> getProjectPiZhunUser() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        // 查询级别
        String levelId = formDataJson.getString("levelId");
        Map<String, Object> params = new HashMap<>();
        params.put("levelId", levelId);
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        List<Map<String, String>> levelInfos = xcmgProjectOtherDao.queryProjectLevel(params);
        String levelName = levelInfos.get(0).get("levelName");
        if (levelName.contains("A")) {
            String groupName = "分管领导";
            String mainDepId = formDataJson.getString("mainDepId");
            List<String> deptIds = new ArrayList<String>();
            deptIds.add(mainDepId);
            Map<String, Object> deptParams = new HashMap<>();
            deptParams.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            deptParams.put("depIds", deptIds);
            List<Map<String, Object>> depIdNames = xcmgProjectOtherDao.queryDepNameByIds(deptParams);
            if (depIdNames != null && !depIdNames.isEmpty()) {
                String deptName = depIdNames.get(0).get("NAME_").toString();
                if (deptName.contains("工艺")) {
                    groupName = RdmConst.GY_FGLD;
                }
            }
            // 查询分管领导
            params.put("groupName", groupName);
            params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            List<Map<String, String>> leaderInfos = xcmgProjectOtherDao.queryUserByGroupName(params);
            if (leaderInfos == null || leaderInfos.isEmpty()) {
                logger.error("Can't find leaderInfos");
                return users;
            }
            users.add(new TaskExecutor(leaderInfos.get(0).get("USER_ID_"), leaderInfos.get(0).get("FULLNAME_")));
            return users;
        }
        // B、C类由部门负责人批准
        Map<String, Object> queryDepRespMan = new HashMap<>();
        queryDepRespMan.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        queryDepRespMan.put("GROUP_ID_", formDataJson.getString("mainDepId"));
        List<Map<String, String>> depRespMan = xcmgProjectOtherDao.getDepRespManById(queryDepRespMan);
        if (depRespMan == null || depRespMan.isEmpty()) {
            logger.error("Can't find depRespMan, depId is {}", formDataJson.getString("mainDepId"));
            return users;
        }
        users.add(new TaskExecutor(depRespMan.get(0).get("PARTY2_"), depRespMan.get(0).get("FULLNAME_")));
        return users;
    }

    // 科技项目申请流程中，确定项目管理人员（挖掘机械研究院的和非挖掘机械研究院的），挖掘机械研究院有可能有更细的划分
    public Collection<TaskExecutor> projectManagers() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        JSONObject judgeResult =
            loginRecordManager.judgeIsJSZX(formDataJson.getString("mainDepId"), formDataJson.getString("mainDepName"));
        Map<String, Object> params = new HashMap<>();
        List<Map<String, String>> userInfos;
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        // 查询项目类别
        String categoryName = "";
        if (StringUtils.isNotBlank(formDataJson.getString("projectId"))) {
            JSONObject project = xcmgProjectDao.queryProjectById(formDataJson.getString("projectId"));
            if (project != null) {
                categoryName = project.getString("categoryName");
            }
        }
        if (StringUtils.isBlank(categoryName)) {
            categoryName = formDataJson.getString("categoryName");
        }
        // 挖掘机械研究院的项目
        if (judgeResult.getBoolean("isJSZX")) {
            if (categoryName.contains(RdmConst.CXY)) {
                // 如果是产学研类的项目，则使用“产学研项目管理人员”角色中的人
                params.put("groupName", "产学研项目管理人员");
                userInfos = xcmgProjectOtherDao.queryUserByGroupName(params);
            } else {
                params.put("deptId", formDataJson.getString("mainDepId"));
                params.put("REL_TYPE_KEY_", "GROUP-USER-TEC");
                // 先查询该部门有没有专属的项目管理人员，如果没有则使用挖掘机械研究院所有的项目管理人员
                userInfos = commonInfoDao.queryUserByGroupId(params);
                if (userInfos == null || userInfos.size() == 0) {
                    params.put("groupName", RdmConst.JSZXXMGLRY);
                    userInfos = xcmgProjectOtherDao.queryUserByGroupName(params);
                }
            }

        } else {
            String depName = judgeResult.getString("depName");
            params.put("groupName", depName + "项目管理人员");
            userInfos = xcmgProjectOtherDao.queryUserByGroupName(params);
        }
        if (userInfos != null && !userInfos.isEmpty()) {
            for (Map<String, String> oneUser : userInfos) {
                users.add(new TaskExecutor(oneUser.get("USER_ID_"), oneUser.get("FULLNAME_")));
            }
        }
        return users;
    }

    // 项目申请、变更和废止流程中，项目审核人员（挖掘机械研究院的和非挖掘机械研究院的），挖掘机械研究院的为技术管理部负责人，否则为其他部门负责人
    public Collection<TaskExecutor> projectShenHeUsers() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        JSONObject judgeResult =
            loginRecordManager.judgeIsJSZX(formDataJson.getString("mainDepId"), formDataJson.getString("mainDepName"));
        String depName = judgeResult.getString("depName");
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        if (judgeResult.getBoolean("isJSZX")) {
            params.put("groupName", RdmConst.JSGLB_NAME);
        } else {
            String depId = judgeResult.getString("depId");
            params.put("GROUP_ID_", depId);
        }
        List<Map<String, String>> depRespMans = xcmgProjectOtherDao.getDepRespManById(params);
        if (depRespMans != null && !depRespMans.isEmpty()) {
            for (Map<String, String> depRespMan : depRespMans) {
                users.add(new TaskExecutor(depRespMan.get("PARTY2_"), depRespMan.get("FULLNAME_")));
            }
        }
        return users;
    }

    /**
     * 项目变更和废止流程中，技术管理部负责人
     */
    public Collection<TaskExecutor> telManageLeader() {
        List<TaskExecutor> users = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        params.put("groupName", RdmConst.JSGLB_NAME);
        List<Map<String, String>> depRespMans = xcmgProjectOtherDao.getDepRespManById(params);
        if (depRespMans != null && !depRespMans.isEmpty()) {
            for (Map<String, String> depRespMan : depRespMans) {
                users.add(new TaskExecutor(depRespMan.get("PARTY2_"), depRespMan.get("FULLNAME_")));
            }
        }
        return users;
    }

    /**
     * 项目交付物审批流程中，校对环节查询本部门的室主任和项目负责人
     */
    public Collection<TaskExecutor> getOfficeManager() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formJson = JSONObject.parseObject(formData);
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        params.put("groupName", formJson.getString("mainDeptName"));
        List<Map<String, String>> depRespMans = xcmgProjectOtherDao.getOfficeManager(params);
        if (depRespMans != null && !depRespMans.isEmpty()) {
            for (Map<String, String> depRespMan : depRespMans) {
                users.add(new TaskExecutor(depRespMan.get("PARTY2_"), depRespMan.get("FULLNAME_")));
            }
        }
        // 查询项目负责人
        String projectId = formJson.getString("projectId");
        if (StringUtils.isNotBlank(projectId)) {
            params.clear();
            params.put("projectId", projectId);
            List<JSONObject> members = xcmgProjectOtherDao.queryMemberInfosByProjectId(params);
            if (members != null && !members.isEmpty()) {
                for (JSONObject oneUser : members) {
                    if ("项目负责人".equalsIgnoreCase(oneUser.getString("roleName"))) {
                        users.add(new TaskExecutor(oneUser.getString("userId"), oneUser.getString("FULLNAME_")));
                    }
                }
            }
        }
        return users;
    }

    /**
     * 项目交付物审批流程中，审核环节，查询本部门的部长、所长、副所长、副部长或者助理
     */
    public Collection<TaskExecutor> getDepartmentManager() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formJson = JSONObject.parseObject(formData);
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        params.put("groupName", formJson.getString("mainDeptName"));
        List<Map<String, String>> depRespMans = xcmgProjectOtherDao.getDepartmentManager(params);
        if (depRespMans != null && !depRespMans.isEmpty()) {
            for (Map<String, String> depRespMan : depRespMans) {
                users.add(new TaskExecutor(depRespMan.get("PARTY2_"), depRespMan.get("FULLNAME_")));
            }
        }
        return users;
    }

    /**
     * 项目交付物评审文件审批流程中，批准环节人员（部门负责人+挖掘机械研究院主任+分管领导）
     *
     * @return
     */
    public Collection<TaskExecutor> projectFilePZ_PS() {
        List<TaskExecutor> users = new ArrayList<>();
        Collection<TaskExecutor> departManagers = getDepartmentManager();
        users.addAll(departManagers);
        Map<String, Object> params = new HashMap<>();
        params.put("groupName", RdmConst.FGLD);
        List<Map<String, String>> leaderInfos = xcmgProjectOtherDao.queryUserByGroupName(params);
        if (leaderInfos != null && !leaderInfos.isEmpty()) {
            for (Map<String, String> oneLeader : leaderInfos) {
                users.add(new TaskExecutor(oneLeader.get("USER_ID_"), oneLeader.get("FULLNAME_")));
            }
        }
        params.put("groupName", RdmConst.JSZX_ZR);
        List<Map<String, String>> jszxZR = xcmgProjectOtherDao.queryUserByGroupName(params);
        if (jszxZR != null && !jszxZR.isEmpty()) {
            for (Map<String, String> oneZR : jszxZR) {
                users.add(new TaskExecutor(oneZR.get("USER_ID_"), oneZR.get("FULLNAME_")));
            }
        }
        return users;
    }

    /**
     * 项目交付物审批流程中，批准人员的确定（区分挖掘机械研究院和非挖掘机械研究院）
     *
     * @return
     */
    public Collection<TaskExecutor> toGetProjectFileApprovalPZ() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        JSONObject judgeResult =
            loginRecordManager.judgeIsJSZX(formDataJson.getString("mainDepId"), formDataJson.getString("mainDepName"));
        Map<String, Object> params = new HashMap<>();
        List<Map<String, String>> userInfos;
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        if (judgeResult.getBoolean("isJSZX")) {
            params.put("groupName", "技术中心交付物批准人员");
            userInfos = xcmgProjectOtherDao.queryUserByGroupName(params);
        } else {
            String depName = judgeResult.getString("depName");
            params.put("groupName", depName + "交付物批准人员");
            userInfos = xcmgProjectOtherDao.queryUserByGroupName(params);
        }
        if (userInfos != null && !userInfos.isEmpty()) {
            for (Map<String, String> oneUser : userInfos) {
                users.add(new TaskExecutor(oneUser.get("USER_ID_"), oneUser.get("FULLNAME_")));
            }
        }
        return users;
    }

    /**
     * 项目交付物审批流程中，标准化人员的确定
     *
     * @return
     */
    public Collection<TaskExecutor> projectGetStandarder() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String mainDepName = formDataJson.getString("mainDeptName");
        List<SysDic> sysDics = sysDicManager.getByTreeKey("projectStandarder");
        if (sysDics != null && !sysDics.isEmpty()) {
            for (SysDic oneUser : sysDics) {
                if (oneUser.getKey().equalsIgnoreCase(mainDepName)) {
                    users.add(new TaskExecutor(oneUser.getValue(), oneUser.getDescp()));
                }
            }
        }
        if(users == null ||users.isEmpty()){
            Map<String, Object> groupParams = new HashMap<>();
            groupParams.put("groupName", "项目管理交付物审批标准化通用专员");
            List<Map<String, String>> userInfos = xcmgProjectOtherDao.queryUserByGroupName(groupParams);
            for (Map<String, String> userInfo : userInfos) {
                    users.add(new TaskExecutor(userInfo.get("USER_ID_").toString(), userInfo.get("FULLNAME_").toString()));
            }
        }
        return users;
    }

    /**
     * 获取项目交付物审批流程中的项目负责人
     *
     * @return
     */
    public Collection<TaskExecutor> projectFileApprovalProjectResp() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String projectId = formDataJson.getString("projectId");
        Map<String, Object> params = new HashMap<>();
        params.put("projectId", projectId);
        List<JSONObject> members = xcmgProjectOtherDao.queryMemberInfosByProjectId(params);
        if (members != null && !members.isEmpty()) {
            for (JSONObject oneUser : members) {
                if ("项目负责人".equalsIgnoreCase(oneUser.getString("roleName"))) {
                    users.add(new TaskExecutor(oneUser.getString("userId"), oneUser.getString("FULLNAME_")));
                }
            }
        }
        return users;
    }

    /**
     * 获取项目交付物审批流程中的项目指导人
     *
     * @return
     */
    public Collection<TaskExecutor> projectFileApprovalProjectZD() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String projectId = formDataJson.getString("projectId");
        Map<String, Object> params = new HashMap<>();
        params.put("projectId", projectId);
        List<JSONObject> members = xcmgProjectOtherDao.queryMemberInfosByProjectId(params);
        if (members != null && !members.isEmpty()) {
            for (JSONObject oneUser : members) {
                if ("项目指导人".equalsIgnoreCase(oneUser.getString("roleName"))) {
                    users.add(new TaskExecutor(oneUser.getString("userId"), oneUser.getString("FULLNAME_")));
                }
            }
        }
        return users;
    }

    // 区分工艺和其他部门的分管领导
    public Collection<TaskExecutor> projectFgld() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String groupName = "分管领导";
        String mainDepId = formDataJson.getString("mainDepId");
        List<String> deptIds = new ArrayList<String>();
        deptIds.add(mainDepId);
        Map<String, Object> deptParams = new HashMap<>();
        deptParams.put("depIds", deptIds);
        List<Map<String, Object>> depIdNames = xcmgProjectOtherDao.queryDepNameByIds(deptParams);
        if (depIdNames != null && !depIdNames.isEmpty()) {
            String deptName = depIdNames.get(0).get("NAME_").toString();
            if (deptName.contains("工艺")) {
                groupName = RdmConst.GY_FGLD;
            }
        }
        // 查询分管领导
        deptParams.clear();
        deptParams.put("groupName", groupName);
        List<Map<String, String>> leaderInfos = xcmgProjectOtherDao.queryUserByGroupName(deptParams);
        if (leaderInfos == null || leaderInfos.isEmpty()) {
            logger.error("Can't find leaderInfos");
            return users;
        }
        users.add(new TaskExecutor(leaderInfos.get(0).get("USER_ID_"), leaderInfos.get(0).get("FULLNAME_")));
        return users;
    }

    /**
     * 获取项目负责人
     *
     * @return
     */
    public Collection<TaskExecutor> getProjectManager() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String projectId = formDataJson.getString("projectId");
        Map<String, Object> params = new HashMap<>();
        params.put("projectId", projectId);
        List<JSONObject> members = xcmgProjectOtherDao.queryMemberInfosByProjectId(params);
        if (members != null && !members.isEmpty()) {
            for (JSONObject oneUser : members) {
                if ("项目负责人".equalsIgnoreCase(oneUser.getString("roleName"))) {
                    users.add(new TaskExecutor(oneUser.getString("userId"), oneUser.getString("FULLNAME_")));
                }
            }
        }
        return users;
    }

    // 牵头部门是零部件所
    public boolean lbjDeptProjectYes(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String mainDepName = formDataJson.getString("mainDepName");
        if (RdmConst.LBJYJS_NAME.equalsIgnoreCase(mainDepName)) {
            return true;
        }
        return false;
    }

    // 牵头部门不是零部件所
    public boolean lbjDeptProjectNo(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        return !lbjDeptProjectYes(cmd, vars);
    }

    // 零部件所部门负责人
    public Collection<TaskExecutor> lbjsDeptResp() {
        List<TaskExecutor> users = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();
        params.put("groupName", RdmConst.LBJYJS_NAME);
        List<Map<String, String>> depRespMans = xcmgProjectOtherDao.getDepRespManById(params);
        if (depRespMans != null && !depRespMans.isEmpty()) {
            for (Map<String, String> depRespMan : depRespMans) {
                users.add(new TaskExecutor(depRespMan.get("PARTY2_"), depRespMan.get("FULLNAME_")));
            }
        }
        return users;
    }
}
