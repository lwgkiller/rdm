package com.redxun.serviceEngineering.core.service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.IExecutionCmd;
import com.redxun.bpm.core.entity.TaskExecutor;
import com.redxun.core.script.GroovyScript;
import com.redxun.org.api.service.UserService;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
import com.redxun.rdmZhgl.core.dao.CcbgDao;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.serviceEngineering.core.dao.MaintainabilityRectificationItemsDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MaintainabilityRectificationItemsScript implements GroovyScript {
    private Logger logger = LoggerFactory.getLogger(MaintainabilityRectificationItemsScript.class);
    @Autowired
    private CommonInfoDao commonInfoDao;
    @Autowired
    private MaintainabilityRectificationItemsService maintainabilityRectificationItemsService;
    @Autowired
    private MaintainabilityRectificationItemsDao maintainabilityRectificationItemsDao;
    @Autowired
    private CommonInfoManager commonInfoManager;

    //获取校对人的信息
    public Collection<TaskExecutor> getProofreadingUsers() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String editUserId = formDataJson.getString("editorUserId");
        if (editUserId.equalsIgnoreCase("1")) {
            users.add(new TaskExecutor(editUserId, "管理员"));
        } else {
            List<JSONObject> deptResps = commonInfoDao.queryDeptRespByUserId(editUserId);
            if (deptResps != null && !deptResps.isEmpty()) {
                for (JSONObject depRespMan : deptResps) {
                    users.add(new TaskExecutor(depRespMan.getString("USER_ID_"), depRespMan.getString("FULLNAME_")));
                }
            }
        }
        return users;
    }

    //获取审核人的信息
    public Collection<TaskExecutor> getReviewingUsers() {
        List<TaskExecutor> users = new ArrayList<>();
        List<Map<String, String>> deptResps = commonInfoManager.queryJszxAllSuoZhang();
        if (deptResps != null && !deptResps.isEmpty()) {
            for (Map<String, String> depRespMan : deptResps) {
                users.add(new TaskExecutor(depRespMan.get("USER_ID_"), depRespMan.get("FULLNAME_")));
            }
        }
        return users;
    }

    //1.以驳回形式进入编制状态需要
    //2.以任何形式进入校对状态需要
    //3.以任何形式进入审核状态需要
    //4.以任何形式进入批准状态需要
    //其实没必要这么执着，直接利用act的status信息就行，节点编号和命名规范就可以了
    //但是我非要把他同步到表单里
    public void taskCreateScript(Map<String, Object> vars) {
        Map<String, Object> _vars = vars;
        JSONObject jsonObject = maintainabilityRectificationItemsService.getDetail(vars.get("busKey").toString());
        String activitiId = vars.get("activityId").toString();
        if (activitiId.equalsIgnoreCase("editing")) {
            jsonObject.put("businessStatus", "A-" + activitiId);
        } else if (activitiId.equalsIgnoreCase("proofreading")) {
            jsonObject.put("businessStatus", "B-" + activitiId);
        } else if (activitiId.equalsIgnoreCase("reviewing")) {
            jsonObject.put("businessStatus", "C-" + activitiId);
        } else if (activitiId.equalsIgnoreCase("approving")) {
            jsonObject.put("businessStatus", "D-" + activitiId);
        }
        //如果进入的是编制状态，要将后面三个业务处理人清空
        if (activitiId.equals("editing")) {
            jsonObject.put("proofreaderUserId", "");
            jsonObject.put("reviewerUserId", "");
            jsonObject.put("approverUserId", "");
        }
        maintainabilityRectificationItemsService.updateBusiness(jsonObject);
    }

    //1.以通过形式结束校对状态需要
    //2.以通过形式结束审核状态需要
    //3.以通过形式结束批准状态需要
    public void taskEndScript(Map<String, Object> vars) {
        JSONObject jsonObject = maintainabilityRectificationItemsService.getDetail(vars.get("busKey").toString());
        String activitiId = vars.get("activityId").toString();
        //如果结束的是校对状态，记录校对人
        if (activitiId.equals("proofreading")) {
            jsonObject.put("proofreaderUserId", ContextUtil.getCurrentUserId());
        }//如果结束的是审核状态，记录审核人和最终状态
        else if (activitiId.equals("reviewing")) {
            jsonObject.put("reviewerUserId", ContextUtil.getCurrentUserId());
        }//如果结束的是批准状态，记录批准人和最终状态
        else if (activitiId.equals("approving")) {
            jsonObject.put("approverUserId", ContextUtil.getCurrentUserId());
            jsonObject.put("businessStatus", "E-close");
        }
        maintainabilityRectificationItemsService.updateBusiness(jsonObject);
    }
}
