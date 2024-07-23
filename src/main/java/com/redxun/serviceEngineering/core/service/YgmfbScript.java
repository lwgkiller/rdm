package com.redxun.serviceEngineering.core.service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.AbstractExecutionCmd;
import com.redxun.bpm.core.entity.IExecutionCmd;
import com.redxun.bpm.core.entity.TaskExecutor;
import com.redxun.core.script.GroovyScript;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.serviceEngineering.core.dao.MaintenanceManualMctDao;
import com.redxun.serviceEngineering.core.dao.MaintenanceManualfileDao;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class YgmfbScript implements GroovyScript {
    private Logger logger = LoggerFactory.getLogger(YgmfbScript.class);
    @Autowired
    private YgmfbManager ygmfbManager;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;

    @Autowired
    private SendDDNoticeManager sendDDNoticeManager;

    //..获取备件公司领导信息
    public Collection<TaskExecutor> getBjgsRepUser() {
        List<TaskExecutor> users = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        params.put("groupName", "备件公司");
        List<Map<String, String>> depRespMans = xcmgProjectOtherDao.getDepRespManById(params);
        if (depRespMans != null && !depRespMans.isEmpty()) {
            for (Map<String, String> depRespMan : depRespMans) {
                users.add(new TaskExecutor(depRespMan.get("PARTY2_"), depRespMan.get("FULLNAME_")));
            }
        }
        return users;
    }

    //获取服务工程部部门领导信息
    public Collection<TaskExecutor> getFwgcRepUser() {
        List<TaskExecutor> users = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        params.put("groupName", RdmConst.FWGCS_NAME);
        List<Map<String, String>> depRespMans = xcmgProjectOtherDao.getDepRespManById(params);
        if (depRespMans != null && !depRespMans.isEmpty()) {
            for (Map<String, String> depRespMan : depRespMans) {
                users.add(new TaskExecutor(depRespMan.get("PARTY2_"), depRespMan.get("FULLNAME_")));
            }
        }
        return users;
    }

    //..
    public void taskEndScript(Map<String, Object> vars) {
        //@lwgkiller:阿诺多罗 下面这个cmd可以取代getDetail，而且传来的是表单的最新数据
        JSONObject jsonObject = ProcessHandleHelper.getProcessCmd().getJsonDataObject();
        String activitiId = vars.get("activityId").toString();
        if (activitiId.equalsIgnoreCase("n1")) {
            jsonObject.put("applyUserId", ContextUtil.getCurrentUserId());
            jsonObject.put("applyUser", ContextUtil.getCurrentUser().getFullname());
            jsonObject.put("applyDepId", ContextUtil.getCurrentUser().getMainGroupId());
            jsonObject.put("applyDep", ContextUtil.getCurrentUser().getMainGroupName());
        } else if (activitiId.equalsIgnoreCase("n2")) {
            jsonObject.put("bjgsfzrId", ContextUtil.getCurrentUserId());
            jsonObject.put("bjgsfzr", ContextUtil.getCurrentUser().getFullname());
        } else if (activitiId.equalsIgnoreCase("n3")) {
            jsonObject.put("fwgcywyId", ContextUtil.getCurrentUserId());
            jsonObject.put("fwgcywy", ContextUtil.getCurrentUser().getFullname());
        } else if (activitiId.equalsIgnoreCase("n4")) {
            jsonObject.put("fwgcfzrId", ContextUtil.getCurrentUserId());
            jsonObject.put("fwgcfzr", ContextUtil.getCurrentUser().getFullname());
        } else if (activitiId.equalsIgnoreCase("n5")) {
            jsonObject.put("fgldId", ContextUtil.getCurrentUserId());
            jsonObject.put("fgld", ContextUtil.getCurrentUser().getFullname());
            sendDingDing(jsonObject);
        }
        ygmfbManager.updateBusiness(jsonObject);
    }

    //..是服务工程所提交的
    public boolean isCoordinate(AbstractExecutionCmd cmd, Map<String, Object> vars) {
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
        if (formDataJson.getString("applyDep").equalsIgnoreCase(RdmConst.FWGCS_NAME)) {
            return true;
        } else {
            return false;
        }
    }

    //..不是服务工程所提交的
    public boolean isNoCoordinate(AbstractExecutionCmd cmd, Map<String, Object> vars) {
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
        if (formDataJson.getString("applyDep").equalsIgnoreCase(RdmConst.FWGCS_NAME)) {
            return false;
        } else {
            return true;
        }
    }

    //..
    public void sendDingDing(JSONObject jsonObject) {
        JSONObject noticeObj = new JSONObject();
        StringBuilder stringBuilder = new StringBuilder("油缸密封包传递通知:厂家[");
        stringBuilder.append(jsonObject.getString("changjia"))
                .append("],油缸类型[").append(jsonObject.getString("ygType"))
                .append("],油缸物料号[").append(jsonObject.getString("ygMatNo"))
                .append("],油缸型号[").append(jsonObject.getString("ygXinghao"))
                .append("],油缸物料描述[").append(jsonObject.getString("ygMatDesc"))
                .append("],密封包物料号[").append(jsonObject.getString("mfbMatNo"))
                .append("],密封包物料描述[").append(jsonObject.getString("mfbMatDesc"))
                .append("]");
        stringBuilder.append(XcmgProjectUtil.getNowLocalDateStr(DateUtil.DATE_FORMAT_FULL));
        noticeObj.put("content", stringBuilder.toString());
        sendDDNoticeManager.sendNoticeForCommon(jsonObject.getString("receiverIds"), noticeObj);
    }
}

