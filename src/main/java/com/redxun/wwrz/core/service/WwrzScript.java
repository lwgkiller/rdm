package com.redxun.wwrz.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.AbstractExecutionCmd;
import com.redxun.bpm.core.entity.IExecutionCmd;
import com.redxun.bpm.core.entity.TaskExecutor;
import com.redxun.core.script.GroovyScript;
import com.redxun.core.util.StringUtil;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.wwrz.core.dao.WwrzTestApplyDao;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class WwrzScript implements GroovyScript {
    private Logger logger = LoggerFactory.getLogger(WwrzScript.class);

    @Resource
    private WwrzTestApplyDao wwrzTestApplyDao;
    @Autowired
    private CommonInfoDao commonInfoDao;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    // 网关判断(判断是否通过认证测试) 认证通过
    public boolean testApprove(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String pass = formDataJson.getString("pass");
        if ("tg".equals(pass)) {
            return true;
        }
        return false;
    }

    // 网关判断(判断是否通过认证测试) 认证未通过
    public boolean testNotApprove(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String pass = formDataJson.getString("pass");
        if ("btg".equals(pass)) {
            return true;
        }
        return false;
    }

    /**
     * @tty 获取产品主管
     */
    public Collection<TaskExecutor> getProjectRespUser() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String productLeader = formDataJson.getString("productLeader");
        if (StringUtil.isEmpty(productLeader)) {
            return users;
        }
        String productLeaderName = formDataJson.getString("productLeaderName");
        users.add(new TaskExecutor(productLeader, productLeaderName));
        return users;
    }

    /**
     * @tty 获取整改方案各部分负责人
     */
    public Collection<TaskExecutor> getPlanCharger() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        JSONArray problemList = formDataJson.getJSONArray("SUB_problem");
        for (int i = 0; i < problemList.size(); i++) {
            JSONObject obj = problemList.getJSONObject(i);
            if (obj.getString("passed")==null||"否".equals(obj.getString("passed"))) {
                users.add(new TaskExecutor(problemList.getJSONObject(i).getString("charger"),
                    problemList.getJSONObject(i).getString("chargerName")));
            }
        }
        return users;
    }

    /**
     * 查询产品主管的部门负责人
     *
     * @return
     */
    public Collection<TaskExecutor> getCpzgDeptResper() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String userId = formDataJson.getString("productLeader");
        List<JSONObject> deptResps = commonInfoDao.queryDeptRespByUserId(userId);
        if (deptResps != null && !deptResps.isEmpty()) {
            for (JSONObject depRespMan : deptResps) {
                users.add(new TaskExecutor(depRespMan.getString("USER_ID_"), depRespMan.getString("FULLNAME_")));
            }
        }
        return users;
    }

    /**
     * @tty 获取资料编制各部分负责人
     */
    public Collection<TaskExecutor> getDocumentCharger() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        JSONArray documentList = formDataJson.getJSONArray("SUB_document");
        for (int i = 0; i < documentList.size(); i++) {
            JSONObject obj = documentList.getJSONObject(i);
            if ("是".equals(obj.getString("used"))||StringUtils.isBlank(obj.getString("used")) && (StringUtils.isBlank(obj.getString("passed"))||"是".equals(obj.getString("passed")))) {
                users.add(new TaskExecutor(documentList.getJSONObject(i).getString("charger"),
                    documentList.getJSONObject(i).getString("chargerName")));
            }
        }
        return users;
    }

    /**
     * 挖掘机械研究院主任审批
     */
    public Collection<TaskExecutor> getTechManager() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String techManagerIds = formDataJson.getString("techManagerIds");
        String techManagerNames = formDataJson.getString("techManagerNames");
        if (StringUtils.isNotBlank(techManagerIds) && StringUtils.isNotBlank(techManagerNames)) {
            List<String> ssqrIdsList = Arrays.asList(techManagerIds.split(",", -1));
            List<String> ssqrNmaesList = Arrays.asList(techManagerNames.split(",", -1));
            for (int index = 0; index < ssqrIdsList.size(); index++) {
                users.add(new TaskExecutor(ssqrIdsList.get(index), ssqrNmaesList.get(index)));
            }
        }
        return users;
    }

    // 网关判断资料是否完整
    public boolean testDocComplete(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String docComplete = formDataJson.getString("docComplete");
        if ("1".equals(docComplete)) {
            return true;
        }
        return false;
    }

    // 网关判断(网关判断资料是否完整) 认证未通过
    public boolean testDocNotComplete(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String docComplete = formDataJson.getString("docComplete");
        if ("0".equals(docComplete)) {
            return true;
        }
        return false;
    }

    // 网关判断资料是否OK
    public boolean testDocOk(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String docOk = formDataJson.getString("docOk");
        if ("0".equals(docOk)) {
            return true;
        }
        return false;
    }

    // 网关判断资料是否OK
    public boolean testDocNotOk(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String docOk = formDataJson.getString("docOk");
        if ("1".equals(docOk)) {
            return true;
        }
        return false;
    }

    /**
     * @tty 获取 项目废止流程，原来流程创建人
     */
    public Collection<TaskExecutor> getFlowCreator() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String mainId = formDataJson.getString("mainId");
        JSONObject applyObj = wwrzTestApplyDao.getJsonObject(mainId);
        users.add(new TaskExecutor(applyObj.getString("CREATE_BY_"), applyObj.getString("creator")));
        return users;
    }

    /**
     * @tty 获取 委外认证报告预览申请 ，技术部门领导
     */
    public Collection<TaskExecutor> getTechRespUser() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        users.add(
            new TaskExecutor(formDataJson.getString("techLeaderUserId"), formDataJson.getString("techLeaderName")));
        return users;
    }
    /**
     * @tty 获取委外认证计划审批 所长
     */
    public Collection<TaskExecutor> getWwrzPlanRespUser() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String deptId = formDataJson.getString("deptId");
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        params.put("GROUP_ID_", deptId);
        List<Map<String, String>> depRespMans = xcmgProjectOtherDao.getDepRespManById(params);
        if (depRespMans != null && !depRespMans.isEmpty()) {
            for (Map<String, String> depRespMan : depRespMans) {
                users.add(new TaskExecutor(depRespMan.get("PARTY2_"), depRespMan.get("FULLNAME_")));
            }
        }
        return users;
    }

    // 网关判断(网关判断是否生成台账) 生成
    public boolean needNote(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String items = formDataJson.getString("items");
        if (items.contains("UKCAzs1")||items.contains("CEzs1")) {
            return true;
        }
        return false;
    }
    // 网关判断(网关判断是否生成台账) 不生成
    public boolean notNeedNote(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String items = formDataJson.getString("items");
        if (!items.contains("UKCAzs1")&&!items.contains("CEzs1")) {
            return true;
        }
        return false;
    }
}
