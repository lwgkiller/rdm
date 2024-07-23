package com.redxun.zlgjNPI.core.manager;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.AbstractExecutionCmd;
import com.redxun.bpm.core.entity.IExecutionCmd;
import com.redxun.bpm.core.entity.TaskExecutor;
import com.redxun.core.script.GroovyScript;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

// 项目流程节点的触发事件
@Service
public class ProductRequireScript implements GroovyScript {
    private Logger logger = LoggerFactory.getLogger(ProductRequireScript.class);

    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;


    public Collection<TaskExecutor> getYyjsRespUser() {
        List<TaskExecutor> users = new ArrayList<>();
        List<Map<String, String>> depRespMans = queryYyjsRespUser();
        if (depRespMans != null && !depRespMans.isEmpty()) {
            for (Map<String, String> depRespMan : depRespMans) {
                users.add(new TaskExecutor(depRespMan.get("PARTY2_"), depRespMan.get("FULLNAME_")));
            }
        }
        return users;
    }

    public List<Map<String, String>> queryYyjsRespUser() {
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        params.put("groupName", RdmConst.YYJSB_NAME);
        List<Map<String, String>> depRespMans = xcmgProjectOtherDao.getDepRespManById(params);
        return depRespMans;
    }

    public List<Map<String, String>> queryCpsRespUser() {
        Map<String, Object> params = new HashMap<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        params.put("groupName", formDataJson.getString("modelDeptName"));
        List<Map<String, String>> depRespMans = xcmgProjectOtherDao.getDepRespManById(params);
        return depRespMans;
    }

    public Collection<TaskExecutor> getCpsRespUser() {
        List<TaskExecutor> users = new ArrayList<>();
        List<Map<String, String>> depRespMans = queryCpsRespUser();
        if (depRespMans != null && !depRespMans.isEmpty()) {
            for (Map<String, String> depRespMan : depRespMans) {
                users.add(new TaskExecutor(depRespMan.get("PARTY2_"), depRespMan.get("FULLNAME_")));
            }
        }
        return users;
    }

    public Collection<TaskExecutor> getCpzg() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String SzrId = formDataJson.getString("cpzgId");
        if (StringUtils.isNotBlank(SzrId)) {
            String SzrName = formDataJson.getString("cpzgName");
            users.add(new TaskExecutor(SzrId, SzrName));
        }
        return users;
    }

    public boolean cpfxApprove(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String fileType = formDataJson.getString("fileType");
        if ("产品竞争力分析报告".equals(fileType)) {
            return true;
        }
        return false;
    }

    public boolean kfjyApprove(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String fileType = formDataJson.getString("fileType");
        if ("产品开发建议书".equals(fileType)) {
            return true;
        }
        return false;
    }

}
