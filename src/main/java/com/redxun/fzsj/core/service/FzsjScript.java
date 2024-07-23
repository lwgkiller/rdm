package com.redxun.fzsj.core.service;

import java.util.*;

import com.redxun.fzsj.core.dao.SdmDao;
import com.redxun.sys.core.util.SysPropertiesUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.AbstractExecutionCmd;
import com.redxun.bpm.core.entity.IExecutionCmd;
import com.redxun.bpm.core.entity.TaskExecutor;
import com.redxun.core.script.GroovyScript;
import com.redxun.fzsj.core.dao.FzsjDao;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.sys.org.dao.OsUserDao;
import com.redxun.sys.org.entity.OsUser;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class FzsjScript implements GroovyScript {

    private Logger logger = LoggerFactory.getLogger(FzsjScript.class);

    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;

    @Autowired
    private OsUserDao osUserDao;

    @Autowired
    private FzsjDao fzsjDao;
    @Autowired
    private CommonInfoDao commonInfoDao;
    @Resource
    private SdmService sdmService;

    @Autowired
    private RdmZhglUtil rdmZhglUtil;

    @Resource
    private SdmDao sdmDao;

    // 校对人员
    public Collection<TaskExecutor> jdUserInfos() {
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        List<TaskExecutor> users = new ArrayList<>();
        String[] cpzgOrSzrIds = formDataJson.getString("cpzgOrSzrId").split(",");
        for (String cpzgOrSzrId : cpzgOrSzrIds) {
            OsUser osUser = osUserDao.get(cpzgOrSzrId);
            users.add(new TaskExecutor(osUser.getUserId(), osUser.getFullname()));
        }
        return users;
    }

    // 仿真室主任
    public Collection<TaskExecutor> fzszrUserInfos() {
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        List<TaskExecutor> users = new ArrayList<>();
        String fzszrId = formDataJson.getString("fzszrId");
        OsUser osUser = osUserDao.get(fzszrId);
        users.add(new TaskExecutor(osUser.getUserId(), osUser.getFullname()));
        return users;
    }

    // 部门负责人
    public Collection<TaskExecutor> getDepRespUser() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String applyUserId = formDataJson.getString("CREATE_BY_");
        if (StringUtils.isBlank(applyUserId)) {
            applyUserId = ContextUtil.getCurrentUserId();
        }
        List<JSONObject> deptResps = commonInfoDao.queryDeptRespByUserId(applyUserId);
        if (deptResps != null && !deptResps.isEmpty()) {
            for (JSONObject depRespMan : deptResps) {
                users.add(new TaskExecutor(depRespMan.getString("USER_ID_"), depRespMan.getString("FULLNAME_")));
            }
        }
        return users;
    }

    // 标准预览下载部门负责人(技术党支部数据的，由技术管理部负责人审批)_作废但保留，以备有恢复原有逻辑的需求
    public Collection<TaskExecutor> standardPreviewDownloadDepRespUser_obsolete() {
        List<TaskExecutor> users = (List<TaskExecutor>) getDepRespUser();
        if (users.isEmpty()) {
            boolean isJSZXSJ = rdmZhglUtil.judgeIsPointRoleUser(ContextUtil.getCurrentUserId(), RdmConst.JSZX_DZBSJ);
            if (isJSZXSJ) {
                Map<String, Object> params = new HashMap<>();
                params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
                params.put("groupName", RdmConst.JSGLB_NAME);
                List<Map<String, String>> depRespMans = xcmgProjectOtherDao.getDepRespManById(params);
                if (depRespMans != null && !depRespMans.isEmpty()) {
                    for (Map<String, String> depRespMan : depRespMans) {
                        users.add(new TaskExecutor(depRespMan.get("PARTY2_"), depRespMan.get("FULLNAME_")));
                    }
                }
            }
        }
        return users;
    }

    //..标准预览下载部门负责人(技术党支部书记的，由本人审批)
    public Collection<TaskExecutor> standardPreviewDownloadDepRespUser() {
        List<TaskExecutor> users = (List<TaskExecutor>) getDepRespUser();
        if (users.isEmpty()) {
            boolean isJSZXSJ = rdmZhglUtil.judgeIsPointRoleUser(ContextUtil.getCurrentUserId(), RdmConst.JSZX_DZBSJ);
            if (isJSZXSJ) {
                users.add(new TaskExecutor(ContextUtil.getCurrentUserId(), ContextUtil.getCurrentUser().getFullname()));
            }
        }
        return users;
    }

    // 仿真所长
    public Collection<TaskExecutor> fzLeaderInfos() {
        List<TaskExecutor> users = new ArrayList<>();
        Map<String, Object> leaderParams = new HashMap<>();
        leaderParams.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        leaderParams.put("groupName", "仿真技术研究所");
        List<Map<String, String>> depRespMans = xcmgProjectOtherDao.getDepRespManById(leaderParams);
        if (depRespMans != null && !depRespMans.isEmpty()) {
            for (Map<String, String> depRespMan : depRespMans) {
                users.add(new TaskExecutor(depRespMan.get("PARTY2_"), depRespMan.get("FULLNAME_")));
            }
        }
        return users;
    }

    // 仿真任务执行人员
    public Collection<TaskExecutor> fzrwzxry() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String fzsjId = formDataJson.getString("id");
        JSONObject lastFzzx = fzsjDao.queryLastFzzx(fzsjId);
        String[] zxryIds = lastFzzx.getString("zxryId").split(",");
        for (String zxryId : zxryIds) {
            OsUser osUser = osUserDao.get(zxryId);
            users.add(new TaskExecutor(osUser.getUserId(), osUser.getFullname()));
        }
        return users;
    }

    // 是否同意(是)
    public boolean agree(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        if (StringUtils.isBlank(formData)) {
            logger.warn("formData is blank");
            return false;
        }
        JSONObject formDataJson = JSONObject.parseObject(formData);
        if (formDataJson.isEmpty()) {
            logger.warn("formData is blank");
            return false;
        }
        String id = formDataJson.getString("id");
        JSONObject jsonObject = fzsjDao.queryLastFzzx(id);
        String confirmResult = jsonObject.getString("confirmResult");
        if (confirmResult.equals("ty")) {
            return true;
        }
        return false;
    }

    // 是否同意(否)
    public boolean notAgree(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        if (StringUtils.isBlank(formData)) {
            logger.warn("formData is blank");
            return false;
        }
        JSONObject formDataJson = JSONObject.parseObject(formData);
        if (formDataJson.isEmpty()) {
            logger.warn("formData is blank");
            return false;
        }
        String id = formDataJson.getString("id");
        JSONObject jsonObject = fzsjDao.queryLastFzzx(id);
        String confirmResult = jsonObject.getString("confirmResult");
        if (confirmResult.equals("bty")) {
            return true;
        }
        return false;
    }

    //..是否紧急(是)
    public boolean idUrgent(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        if (StringUtils.isBlank(formData)) {
            logger.warn("formData is blank");
            return false;
        }
        JSONObject formDataJson = JSONObject.parseObject(formData);
        if (formDataJson.isEmpty()) {
            logger.warn("formData is blank");
            return false;
        }
        if (formDataJson.containsKey("idUrgent") &&
                formDataJson.getString("idUrgent").equalsIgnoreCase("是")) {
            return true;
        }
        return false;
    }

    //..是否紧急(否)
    public boolean idNotUrgent(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        if (StringUtils.isBlank(formData)) {
            logger.warn("formData is blank");
            return false;
        }
        JSONObject formDataJson = JSONObject.parseObject(formData);
        if (formDataJson.isEmpty()) {
            logger.warn("formData is blank");
            return false;
        }
        if (!formDataJson.containsKey("idUrgent") ||
                formDataJson.getString("idUrgent").equalsIgnoreCase("否")) {
            return true;
        }
        return false;
    }

    /**
     * 分管领导审批通过时触发，更新领导结束审批时间
     */
    public void updateLdjsspsj(Map<String, Object> vars) {
        AbstractExecutionCmd cmd = (AbstractExecutionCmd) vars.get("cmd");
        String jsonData = cmd.getJsonData();
        JSONObject formData = JSONObject.parseObject(jsonData);
        String id = formData.getString("id");
        JSONObject fzsj = fzsjDao.queryFzsjById(id);
        fzsj.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        fzsj.put("UPDATE_TIME_", new Date());
        fzsj.put("ldjsspsj", new Date());
        fzsjDao.updateFzsj(fzsj);
    }

    /**
     * 仿真所长审批通过时触发
     */
    public void updateSqlcsfjs(Map<String, Object> vars) {
        AbstractExecutionCmd cmd = (AbstractExecutionCmd) vars.get("cmd");
        String jsonData = cmd.getJsonData();
        JSONObject formData = JSONObject.parseObject(jsonData);
        String id = formData.getString("id");
        JSONObject fzsj = fzsjDao.queryFzsjById(id);
        fzsj.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        fzsj.put("UPDATE_TIME_", new Date());
        fzsj.put("sqlcsfjs", "yes");
        fzsjDao.updateFzsj(fzsj);
    }

    /**
     * 所长批准后，推送到SDM系统中
     */
    public void sendTaskToSdm(Map<String, Object> vars) {
        String isSdmUsed = SysPropertiesUtil.getGlobalProperty("isSdmUsed");
        if (!"YES".equals(isSdmUsed)) {
            return;
        }
        AbstractExecutionCmd cmd = (AbstractExecutionCmd) vars.get("cmd");
        String jsonData = cmd.getJsonData();
        JSONObject formData = JSONObject.parseObject(jsonData);
        if ("YES".equals(formData.getString("isSendToSDM"))) {
            //1.创建委托书信息
            JSONObject retrunJson = sdmService.sendAssignmentInfo(formData);
            //2.创建项目信息
            sdmService.sendProjectInfo(formData);
            //3.创建仿真任务
            sdmService.sendTaskInfo(formData, retrunJson);
        }
    }

    /**
     * 申请人结果审核 将打分信息推送到SDM
     */
    public void sendScoreToSdm(Map<String, Object> vars) {
        String isSdmUsed = SysPropertiesUtil.getGlobalProperty("isSdmUsed");
        if (!"YES".equals(isSdmUsed)) {
            return;
        }
        AbstractExecutionCmd cmd = (AbstractExecutionCmd) vars.get("cmd");
        String jsonData = cmd.getJsonData();
        JSONObject formData = JSONObject.parseObject(jsonData);
        if ("YES".equals(formData.getString("isSendToSDM"))) {
            String applyId = formData.getString("id");
            JSONObject taskObj = sdmDao.getSdmTaskObj(applyId);
            JSONObject fzzxObj = fzsjDao.getFzzxDetail(taskObj.getString("taskId"));
            if (taskObj != null && fzzxObj != null) {
                String simuTaskId = taskObj.getString("simuTaskId");
                String creator = taskObj.getString("submitor");
                String star = fzzxObj.getString("star");
                String comment = fzzxObj.getString("comment");
                JSONObject paramJson = new JSONObject();
                paramJson.put("simuTaskId", simuTaskId);
                paramJson.put("applyId", applyId);
                paramJson.put("taskId", taskObj.getString("taskId"));
                paramJson.put("creator", creator);
                paramJson.put("star", star);
                paramJson.put("comment", comment);
                sdmService.sendScoreInfo(paramJson);
                sdmService.sendReportInfo(paramJson);
            }

        }
    }

    /**
     * 申请人结果审核 将采纳信息推送到SDM
     */
    public void sendAdoptToSdm(Map<String, Object> vars) {
        String isSdmUsed = SysPropertiesUtil.getGlobalProperty("isSdmUsed");
        if (!"YES".equals(isSdmUsed)) {
            return;
        }
        AbstractExecutionCmd cmd = (AbstractExecutionCmd) vars.get("cmd");
        String jsonData = cmd.getJsonData();
        JSONObject formData = JSONObject.parseObject(jsonData);
        if ("YES".equals(formData.getString("isSendToSDM"))) {
            String applyId = formData.getString("id");
            JSONObject taskObj = sdmDao.getSdmTaskObj(applyId);
            if (taskObj != null) {
                String simuTaskId = taskObj.getString("simuTaskId");
                JSONObject paramJson = new JSONObject();
                paramJson.put("simuTaskId", simuTaskId);
                paramJson.put("applyId", applyId);
                paramJson.put("adoptResult", sdmService.convertAdoptType(formData.getString("gjyj")));
                paramJson.put("adoptDescription", formData.getString("gjyy"));
                sdmService.sendAdoptInfo(paramJson);
            }

        }
    }

    /**
     * 申请人反馈改进效果 将实施效果信息推送到SDM
     */
    public void sendImplementToSdm(Map<String, Object> vars) {
        String isSdmUsed = SysPropertiesUtil.getGlobalProperty("isSdmUsed");
        if (!"YES".equals(isSdmUsed)) {
            return;
        }
        AbstractExecutionCmd cmd = (AbstractExecutionCmd) vars.get("cmd");
        String jsonData = cmd.getJsonData();
        JSONObject formData = JSONObject.parseObject(jsonData);
        if ("YES".equals(formData.getString("isSendToSDM"))) {
            String applyId = formData.getString("id");
            JSONObject taskObj = sdmDao.getSdmTaskObj(applyId);
            if (taskObj != null) {
                String simuTaskId = taskObj.getString("simuTaskId");
                JSONObject paramJson = new JSONObject();
                paramJson.put("simuTaskId", simuTaskId);
                paramJson.put("applyId", applyId);
                paramJson.put("implementation", formData.getString("gjhxntsfk"));
                sdmService.sendImplementInfo(paramJson);
            }

        }
    }
}
