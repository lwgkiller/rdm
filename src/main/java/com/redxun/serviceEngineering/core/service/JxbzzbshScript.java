package com.redxun.serviceEngineering.core.service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.AbstractExecutionCmd;
import com.redxun.bpm.core.entity.IExecutionCmd;
import com.redxun.bpm.core.entity.TaskExecutor;
import com.redxun.core.script.GroovyScript;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.serviceEngineering.core.dao.JxbzzbshDao;
import com.redxun.serviceEngineering.core.dao.JxbzzbxfsqDao;
import com.redxun.serviceEngineering.core.dao.StandardvalueShipmentnotmadeDao;
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
public class JxbzzbshScript implements GroovyScript {

    private Logger logger = LoggerFactory.getLogger(JxbzzbshScript.class);
    @Resource
    CommonInfoDao commonInfoDao;
    @Autowired
    private JxbzzbshDao jxbzzbshDao;

    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;

    @Autowired
    private JxbzzbxfsqDao jxbzzbxfsqDao;

    @Autowired
    private StandardvalueShipmentnotmadeDao standardDao;

    @Autowired
    private OsUserDao osUserDao;

    // 是否测试版(是)
    public boolean isCsb(AbstractExecutionCmd cmd, Map<String, Object> vars) {
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
        String versionType = formDataJson.getString("versionType");
        if (versionType.equals("csb")) {
            return true;
        }
        return false;
    }

    // 是否测试版(否)
    public boolean isNotCsb(AbstractExecutionCmd cmd, Map<String, Object> vars) {
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
        String versionType = formDataJson.getString("versionType");
        if (!versionType.equals("csb")) {
            return true;
        }
        return false;
    }

    // 是否自动生成(是)
    public boolean isAutoProcess(AbstractExecutionCmd cmd, Map<String, Object> vars) {
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
        String autoProcess = formDataJson.getString("autoProcess");
        if (StringUtils.isNotBlank(autoProcess)&&"yes".equalsIgnoreCase(autoProcess)) {
            return true;
        }
        return false;
    }

    // 是否自动生成(否)
    public boolean isNotAutoProcess(AbstractExecutionCmd cmd, Map<String, Object> vars) {
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
        String autoProcess = formDataJson.getString("autoProcess");
        if (StringUtils.isNotBlank(autoProcess)&&"yes".equalsIgnoreCase(autoProcess)) {
            return false;
        }
        return true;
    }

    // 是否变更(是)
    public boolean isChange(AbstractExecutionCmd cmd, Map<String, Object> vars) {
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
        String oldId = formDataJson.getString("oldId");
        if (StringUtils.isNotBlank(oldId)) {
            return true;
        }
        return false;
    }

    // 是否变更(否)
    public boolean isNotChange(AbstractExecutionCmd cmd, Map<String, Object> vars) {
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
        String oldId = formDataJson.getString("oldId");
        if (StringUtils.isBlank(oldId)) {
            return true;
        }
        return false;
    }

    // 标准值校对人员
    public Collection<TaskExecutor> proofreadUserInfos() {
        List<TaskExecutor> users = new ArrayList<>();
        Map<String, Object> roleParams = new HashMap<>();
        roleParams.put("groupName", "检修标准值表审核技术文档专员");
        roleParams.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        List<Map<String, String>> userInfos = xcmgProjectOtherDao.queryUserByGroupName(roleParams);
        if (userInfos != null && !userInfos.isEmpty()) {
            for (Map<String, String> oneLeader : userInfos) {
                users.add(new TaskExecutor(oneLeader.get("USER_ID_"), oneLeader.get("FULLNAME_")));
            }
        }
        Map<String, Object> leaderParams = new HashMap<>();
        leaderParams.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        leaderParams.put("groupName", "服务工程技术研究所");
        List<Map<String, String>> depRespMans = xcmgProjectOtherDao.getDepRespManById(leaderParams);
        if (depRespMans != null && !depRespMans.isEmpty()) {
            for (Map<String, String> depRespMan : depRespMans) {
                users.add(new TaskExecutor(depRespMan.get("PARTY2_"), depRespMan.get("FULLNAME_")));
            }
        }
        return users;
    }

    // 标准值表审核人员
    public Collection<TaskExecutor> auditUserInfos() {
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        List<TaskExecutor> users = new ArrayList<>();
        Map<String, Object> roleParams = new HashMap<>();
        roleParams.put("groupName", "质保部测试数据回传批准人员");
        roleParams.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        List<Map<String, String>> userInfos = xcmgProjectOtherDao.queryUserByGroupName(roleParams);
        if (userInfos != null && !userInfos.isEmpty()) {
            for (Map<String, String> oneLeader : userInfos) {
                users.add(new TaskExecutor(oneLeader.get("USER_ID_"), oneLeader.get("FULLNAME_")));
            }
        }
        String productDepartment = formDataJson.getString("productDepartment");
        String productType = formDataJson.getString("productType");
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        params.put("groupName", productDepartment);
        List<Map<String, String>> ccpsRespMans = xcmgProjectOtherDao.getDepRespManById(params);
        if (ccpsRespMans != null && !ccpsRespMans.isEmpty()) {
            Map<String, String> ccpsRespMan = new HashMap<>();
            if(RdmConst.XWYJS_NAME.equalsIgnoreCase(productDepartment)){
                if("lunwa".equalsIgnoreCase(productType)){
                    ccpsRespMan = ccpsRespMans.get(0);
                }else  if("lvwa".equalsIgnoreCase(productType)){
                    ccpsRespMan = ccpsRespMans.get(1);
                }
            }else {
                ccpsRespMan = ccpsRespMans.get(0);
            }
            users.add(new TaskExecutor(ccpsRespMan.get("PARTY2_"), ccpsRespMan.get("FULLNAME_")));
        }
        Map<String, Object> leaderParams = new HashMap<>();
        leaderParams.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        leaderParams.put("groupName", "服务工程技术研究所");
        List<Map<String, String>> fwsRespMans = xcmgProjectOtherDao.getDepRespManById(leaderParams);
        if (fwsRespMans != null && !fwsRespMans.isEmpty()) {
            Map<String, String> fwsRespMan = fwsRespMans.get(0);
            users.add(new TaskExecutor(fwsRespMan.get("PARTY2_"), fwsRespMan.get("FULLNAME_")));
        }
        return users;
    }

    // 服务工程部门领导
    public Collection<TaskExecutor> fwLeaderInfos() {
        List<TaskExecutor> users = new ArrayList<>();
        Map<String, Object> leaderParams = new HashMap<>();
        leaderParams.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        leaderParams.put("groupName", "服务工程技术研究所");
        List<Map<String, String>> depRespMans = xcmgProjectOtherDao.getDepRespManById(leaderParams);
        if (depRespMans != null && !depRespMans.isEmpty()) {
            for (Map<String, String> depRespMan : depRespMans) {
                users.add(new TaskExecutor(depRespMan.get("PARTY2_"), depRespMan.get("FULLNAME_")));
            }
        }
        return users;
    }

    // 分管主任
    public Collection<TaskExecutor> getDeptFgzr() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String deptId = formDataJson.getString("productDepartmentId");
        Map<String, Object> params = new HashMap<>();
        params.put("deptId", deptId);
        params.put("REL_TYPE_KEY_", "GROUP-USER-DIRECTOR");
        List<Map<String, String>> userInfos = commonInfoDao.queryUserByGroupId(params);
        if (userInfos != null && !userInfos.isEmpty()) {
            for (Map<String, String> oneLeader : userInfos) {
                users.add(new TaskExecutor(oneLeader.get("USER_ID_"), oneLeader.get("FULLNAME_")));
            }
        }
        return users;
    }

    // 发放部门部门领导
    public Collection<TaskExecutor> distributionLeaderInfos() {
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        List<TaskExecutor> users = new ArrayList<>();
        JSONObject leaderParams = new JSONObject();
        String[] distributionDepartments = formDataJson.getString("distributionDepartment").split(",");
        leaderParams.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        leaderParams.put("groupNames", distributionDepartments);
        List<JSONObject> depRespMans = jxbzzbxfsqDao.getDepRespMan(leaderParams);
        if (depRespMans != null && !depRespMans.isEmpty()) {
            for (JSONObject depRespMan : depRespMans) {
                users.add(new TaskExecutor(depRespMan.getString("PARTY2_"), depRespMan.getString("FULLNAME_")));
            }
        }
        return users;
    }

    // 产品所产品主管
    public Collection<TaskExecutor> cpsLeaderInfos() {
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        List<TaskExecutor> users = new ArrayList<>();
        String shipmentnotmadeId = formDataJson.getString("shipmentnotmadeId");
        JSONObject standard = standardDao.queryById(shipmentnotmadeId);
        OsUser osUser = osUserDao.get(standard.getString("principalId"));
        if (osUser != null) {
            users.add(new TaskExecutor(osUser.getUserId(), osUser.getFullname()));
        }
        return users;
    }

    /**
     * 归档通过时触发 归档同意后改变制作状态，改为制作完成
     */
    public void updateCompletionZzwc(Map<String, Object> vars) {
        AbstractExecutionCmd cmd = (AbstractExecutionCmd)vars.get("cmd");
        String jsonData = cmd.getJsonData();
        JSONObject formData = JSONObject.parseObject(jsonData);
        String shipmentnotmadeId = formData.getString("shipmentnotmadeId");
        JSONObject standard = standardDao.queryById(shipmentnotmadeId);
        standard.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        standard.put("UPDATE_TIME_", new Date());
        standard.put("betaCompletion", "zzwc");
        standardDao.updateData(standard);
    }

    /**
     * 标准值表审核任务创建前调用
     */
    public void beforeApprove(Map<String, Object> vars) {
        AbstractExecutionCmd cmd = (AbstractExecutionCmd)vars.get("cmd");
        String jsonData = cmd.getJsonData();
        JSONObject formData = JSONObject.parseObject(jsonData);
        String shipmentnotmadeId = formData.getString("shipmentnotmadeId");
        JSONObject standard = standardDao.queryById(shipmentnotmadeId);
        Long createTime = standard.getDate("CREATE_TIME_").getTime();
        Date date = new Date();
        long day = (date.getTime() - createTime) / 24 / 60 / 60 / 1000;
        if (day <= 7) {
            standard.put("responseStatus", "正常");
        } else if (7 < day && day <= 14) {
            standard.put("responseStatus", "一级");
        } else if (14 < day && day <= 17) {
            standard.put("responseStatus", "二级");
        } else {
            standard.put("responseStatus", "三级");
        }
        standard.put("responseTime", date);
        standard.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        standard.put("UPDATE_TIME_", new Date());
        standardDao.updateData(standard);
    }

}
