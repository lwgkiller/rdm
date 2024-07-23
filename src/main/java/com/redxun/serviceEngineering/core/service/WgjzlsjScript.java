package com.redxun.serviceEngineering.core.service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.AbstractExecutionCmd;
import com.redxun.bpm.core.entity.IExecutionCmd;
import com.redxun.bpm.core.entity.TaskExecutor;
import com.redxun.core.script.GroovyScript;
import com.redxun.core.util.StringUtil;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.serviceEngineering.core.dao.WgjzlsjDao;
import com.redxun.serviceEngineering.core.dao.WrongPartsDao;
import com.redxun.sys.org.dao.OsUserDao;
import com.redxun.sys.org.entity.OsGroup;
import com.redxun.sys.org.entity.OsUser;
import com.redxun.sys.org.manager.OsGroupManager;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import org.apache.commons.lang.StringUtils;
import org.apache.http.annotation.Obsolete;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class WgjzlsjScript implements GroovyScript {
    private Logger logger = LoggerFactory.getLogger(WgjzlsjScript.class);
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private WrongPartsDao wrongPartsDao;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private OsUserDao osUserDao;
    @Autowired
    private OsGroupManager osGroupManager;
    @Autowired
    private WgjzlsjService wgjzlsjService;

    //..获取服务工程技术文档专员
    public Collection<TaskExecutor> getFwgcjswdzy() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        JSONObject formDataJson = cmd.getJsonDataObject();
        String fwgcPrincipalId = formDataJson.getString("fwgcPrincipalId");
        OsUser osUser = osUserDao.get(fwgcPrincipalId);
        users.add(new TaskExecutor(osUser.getUserId(), osUser.getFullname()));
        return users;
    }

    //..获取产品所责任人
    public Collection<TaskExecutor> getCpsPrincipal() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        JSONObject formDataJson = cmd.getJsonDataObject();
        String cpsPrincipalId = formDataJson.getString("cpsPrincipalId");
        OsUser osUser = osUserDao.get(cpsPrincipalId);
        users.add(new TaskExecutor(osUser.getUserId(), osUser.getFullname()));
        return users;
    }

    //..是否归档(是)
    public boolean filing(AbstractExecutionCmd cmd, Map<String, Object> vars) {
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
        String filing = formDataJson.getString("filing");
        if (filing.equals("yes")) {
            return true;
        }
        return false;
    }

    //..是否归档(否)
    public boolean notFiling(AbstractExecutionCmd cmd, Map<String, Object> vars) {
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
        String filing = formDataJson.getString("filing");
        if (filing.equals("no")) {
            return true;
        }
        return false;
    }

    //..计划制作完成时间>预计制作完成时间
    public boolean isMakeTimePlanOver(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        JSONObject formDataJson = cmd.getJsonDataObject();
        Date makeTimePlan = formDataJson.getDate("makeTimePlan");
        Date yjwcsj = formDataJson.getDate("yjwcsj");
        if (makeTimePlan.after(yjwcsj)) {
            return true;
        }
        return false;
    }

    //..计划制作完成时间<=预计制作完成时间
    public boolean isNotMakeTimePlanOver(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        JSONObject formDataJson = cmd.getJsonDataObject();
        Date makeTimePlan = formDataJson.getDate("makeTimePlan");
        Date yjwcsj = formDataJson.getDate("yjwcsj");
        if (!makeTimePlan.after(yjwcsj)) {
            return true;
        }
        return false;
    }

    //..是零件图册
    public boolean isDataTypeL(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        JSONObject formDataJson = cmd.getJsonDataObject();
        String dataType = formDataJson.getString("dataType");
        if (dataType.equalsIgnoreCase("零件图册类")) {
            return true;
        }
        return false;
    }

    //..不是零件图册
    public boolean isNotDataTypeL(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        JSONObject formDataJson = cmd.getJsonDataObject();
        String dataType = formDataJson.getString("dataType");
        if (!dataType.equalsIgnoreCase("零件图册类")) {
            return true;
        }
        return false;
    }

    /**
     * 以下部分是淘汰功能，仅供参考
     */

    // 创建人部门是服务支持所并且已归档
    @Obsolete
    public boolean isFwgcAndYgd(AbstractExecutionCmd cmd, Map<String, Object> vars) {
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
        String createBy = formDataJson.getString("CREATE_BY_");
        String filing = formDataJson.getString("filing");
        OsGroup mainDeps = osGroupManager.getMainDeps(createBy, ContextUtil.getCurrentTenantId());
        String mainGroupName = mainDeps.getName();
        if (mainGroupName.equals("服务工程技术研究所") && filing.equals("yes")) {
            return true;
        }
        return false;
    }

    // 创建人部门是服务支持所,不同意提供
    @Obsolete
    public boolean isFwgc(AbstractExecutionCmd cmd, Map<String, Object> vars) {
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
        String createBy = formDataJson.getString("CREATE_BY_");
        String filing = formDataJson.getString("filing");
        String firstProvide = formDataJson.getString("firstProvide");
        //获得主部门
        OsGroup mainDeps = osGroupManager.getMainDeps(createBy, ContextUtil.getCurrentTenantId());
        String mainGroupName = mainDeps.getName();
        if (mainGroupName.equals("服务工程技术研究所") && filing.equals("no") && firstProvide.equals("no")) {
            return true;
        }
        return false;
    }

    // 产品所创建或者 (服务工程创建，没归档，同意提供)
    @Obsolete
    public boolean isNotFwgc(AbstractExecutionCmd cmd, Map<String, Object> vars) {
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
        String createBy = formDataJson.getString("CREATE_BY_");
        OsGroup mainDeps = osGroupManager.getMainDeps(createBy, ContextUtil.getCurrentTenantId());
        String mainGroupName = mainDeps.getName();
        String filing = formDataJson.getString("filing");
        String firstProvide = formDataJson.getString("firstProvide");
        if (!mainGroupName.equals("服务工程技术研究所")) {
            return true;
        }
        if (filing.equals("no") && firstProvide.equals("yes")) {
            return true;
        }
        return false;
    }

    // 一级响应是否提供(是)
    @Obsolete
    public boolean firstProvide(AbstractExecutionCmd cmd, Map<String, Object> vars) {
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
        String firstProvide = formDataJson.getString("firstProvide");
        if (firstProvide.equals("yes")) {
            return true;
        }
        return false;
    }

    // 一级响应是否提供(否)
    @Obsolete
    public boolean notFirstProvide(AbstractExecutionCmd cmd, Map<String, Object> vars) {
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
        String firstProvide = formDataJson.getString("firstProvide");
        if (firstProvide.equals("no")) {
            return true;
        }
        return false;
    }

    // 获取流程中产品所负责人所在部门的部门负责人
    @Obsolete
    public Collection<TaskExecutor> getDepRespUser() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String cpsPrincipalId = formDataJson.getString("cpsPrincipalId");
        OsGroup mainDeps = osGroupManager.getMainDeps(cpsPrincipalId, ContextUtil.getCurrentTenantId());
        Map<String, Object> params = new HashMap<>();
        params.put("deptId", mainDeps.getGroupId());
        params.put("REL_TYPE_KEY_", "GROUP-USER-LEADER");
        List<JSONObject> userInfos = wrongPartsDao.geRespUserByDeptId(params);
        if (userInfos != null && !userInfos.isEmpty()) {
            for (JSONObject depRespMan : userInfos) {
                users.add(new TaskExecutor(depRespMan.getString("USER_ID_"), depRespMan.getString("FULLNAME_")));
            }
        }
        return users;
    }

    // 二级响应是否提供(是)
    @Obsolete
    public boolean secondProvide(AbstractExecutionCmd cmd, Map<String, Object> vars) {
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
        String secondProvide = formDataJson.getString("secondProvide");
        if (secondProvide.equals("yes")) {
            return true;
        }
        return false;
    }

    // 二级响应是否提供(否)
    @Obsolete
    public boolean notSecondProvide(AbstractExecutionCmd cmd, Map<String, Object> vars) {
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
        String secondProvide = formDataJson.getString("secondProvide");
        if (secondProvide.equals("no")) {
            return true;
        }
        return false;
    }
}
