package com.redxun.serviceEngineering.core.service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.*;
import com.redxun.core.script.GroovyScript;
import com.redxun.core.util.DateUtil;
import com.redxun.org.api.model.IGroup;
import com.redxun.org.api.service.GroupService;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.restApi.orguser.entity.Group;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.serviceEngineering.core.dao.PartsAtlasFileDownloadApplyDao;
import com.redxun.serviceEngineering.core.dao.WrongPartsDao;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.sys.org.entity.OsGroup;
import com.redxun.sys.org.manager.OsGroupManager;
import com.redxun.sys.org.manager.OsUserManager;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CjzgScript implements GroovyScript {

    private Logger logger = LoggerFactory.getLogger(CjzgScript.class);
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private WrongPartsService wrongPartsService;
    @Autowired
    private WrongPartsDao wrongPartsDao;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private SendDDNoticeManager sendDDNoticeManager;
    @Autowired
    private SysDicManager sysDicManager;
    @Autowired
    private OsUserManager osUserManager;
    @Autowired
    private OsGroupManager osGroupManager;
    @Autowired
    private CommonInfoDao commonInfoDao;
    @Autowired
    private PartsAtlasFileDownloadApplyDao partsAtlasFileDownloadApplyDao;

    //..通过表单的责任部门id来获取部门负责人
    public Collection<TaskExecutor> getWaiBuMenFuZeRenFromForm() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        JSONObject formDataJson = cmd.getJsonDataObject();
        String responsibleDepartmentId = formDataJson.getString("responsibleDepartmentId");
        JSONObject params = new JSONObject();
        params.put("deptId", responsibleDepartmentId);
        params.put("REL_TYPE_KEY_", "GROUP-USER-LEADER");
        List<Map<String, String>> userInfos = commonInfoDao.queryUserByGroupId(params);
        if (userInfos != null && !userInfos.isEmpty()) {
            for (Map<String, String> oneLeader : userInfos) {
                users.add(new TaskExecutor(oneLeader.get("USER_ID_"), oneLeader.get("FULLNAME_")));
            }
        }
        return users;
    }

    //..通过表单获取责任人
    public Collection<TaskExecutor> getZeRenRenFromForm() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        JSONObject formDataJson = cmd.getJsonDataObject();
        String XGSSRespUserId = formDataJson.getString("principalUserId");
        String XGSSRespUserName = formDataJson.getString("principalUserName");
        users.add(new TaskExecutor(XGSSRespUserId, XGSSRespUserName));
        return users;
    }

    //..通过表单获取gss责任人
    public Collection<TaskExecutor> getGssZeRenRenFromForm() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        JSONObject formDataJson = cmd.getJsonDataObject();
        String XGSSRespUserId = formDataJson.getString("XGSSRespUserId");
        String XGSSRespUserName = formDataJson.getString("XGSSRespUserName");
        users.add(new TaskExecutor(XGSSRespUserId, XGSSRespUserName));
        return users;
    }

    //..服务所负责
    public boolean isFuWuSuoFuZe(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        JSONObject formDataJson = cmd.getJsonDataObject();
        OsGroup osGroup = osGroupManager.get(formDataJson.getString("responsibleDepartmentId"));
        if (RdmConst.FWGCS_NAME.equals(osGroup.getName())) {
            return true;
        }
        return false;
    }

    //..非服务所负责
    public boolean isNotFuWuSuoFuZe(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        JSONObject formDataJson = cmd.getJsonDataObject();
        String respDepName = formDataJson.getString("respDepName");
        if (!RdmConst.FWGCS_NAME.equals(respDepName)) {
            return true;
        }
        return false;
    }

    //..
    public void taskCreateScript(Map<String, Object> vars) {
        try {
            JSONObject jsonObject = ProcessHandleHelper.getProcessCmd().getJsonDataObject();
            String activitiId = vars.get("activityId").toString();
            jsonObject.put("businessStatus", activitiId);
            jsonObject.put("currentNodeBeginTime", DateUtil.formatDate(new Date(), DateUtil.DATE_FORMAT_FULL));//当前节点开始时间
            wrongPartsService.updateCjzg(jsonObject);
        } catch (Exception e) {
            logger.error("Exception in taskCreateScript", e);
        }
    }

    //..
    public void taskEndScript(Map<String, Object> vars) {
        try {
            JSONObject jsonObject = ProcessHandleHelper.getProcessCmd().getJsonDataObject();
            String activitiId = vars.get("activityId").toString();
            if (activitiId.equalsIgnoreCase("G")) {//服务所责任人处理节点结束后，记录actualTime实际完成时间
                jsonObject.put("actualTime", DateUtil.formatDate(new Date(), DateUtil.DATE_FORMAT_FULL));
                wrongPartsService.updateCjzg(jsonObject);
            } else if (activitiId.equalsIgnoreCase("L")) {
                jsonObject.put("businessStatus", "Z");
                wrongPartsService.updateCjzg(jsonObject);
                //..发钉钉
                this.sendDingDing(jsonObject);
            }
        } catch (Exception e) {
            logger.error("Exception in taskEndScript", e);
        }
    }

    //..
    private void sendDingDing(JSONObject jsonObject) {
        String receiverNoString = sysDicManager.getBySysTreeKeyAndDicKey(
                "serviceEngineeringGroups", "wrongParts").getValue();
        String[] receiverNos = receiverNoString.split(",");
        StringBuilder stringBuilder = new StringBuilder();
        if (receiverNos.length > 0) {
            for (String userNo : receiverNos) {
                stringBuilder.append(osUserManager.getByUserName(userNo).getUserId());
                stringBuilder.append(",");
            }
        }
        stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());
        JSONObject noticeObj = new JSONObject();
        noticeObj.put("content", "错件名称为: " + jsonObject.getString("wrongPartName") + " 的错件整改已完成！");
        sendDDNoticeManager.sendNoticeForCommon(stringBuilder.toString(), noticeObj);
    }

    /**
     * 以下全部作废
     */
    //获取流程中责任部门的部门领导信息-作废
    public Collection<TaskExecutor> getDepRespUser() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String departmentId = formDataJson.getString("responsibleDepartmentId");
        Map<String, Object> params = new HashMap<>();
        params.put("deptId", departmentId);
        params.put("REL_TYPE_KEY_", "GROUP-USER-LEADER");
        List<JSONObject> userInfos = wrongPartsDao.geRespUserByDeptId(params);
        if (userInfos != null && !userInfos.isEmpty()) {
            for (JSONObject depRespMan : userInfos) {
                users.add(new TaskExecutor(depRespMan.getString("USER_ID_"), depRespMan.getString("FULLNAME_")));
            }
        }
        return users;
    }

    //获取流程中责任部门的人员信息-作废
    public Collection<TaskExecutor> getUserById() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String departmentId = formDataJson.getString("responsibleDepartmentId");
        Map<String, Object> params = new HashMap<>();
        params.put("deptId", departmentId);
        params.put("REL_TYPE_KEY_", "GROUP-USER-BELONG");
        List<JSONObject> userInfos = wrongPartsDao.geRespUserByDeptId(params);
        if (userInfos != null && !userInfos.isEmpty()) {
            for (JSONObject depRespMan : userInfos) {
                users.add(new TaskExecutor(depRespMan.getString("USER_ID_"), depRespMan.getString("FULLNAME_")));
            }
        }
        return users;
    }

    //获取服务工程部部门领导信息-作废
    public Collection<TaskExecutor> getFwgcRepUser() {
        List<TaskExecutor> users = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        params.put("groupName", "服务工程技术研究所");
        List<Map<String, String>> depRespMans = xcmgProjectOtherDao.getDepRespManById(params);
        if (depRespMans != null && !depRespMans.isEmpty()) {
            for (Map<String, String> depRespMan : depRespMans) {
                users.add(new TaskExecutor(depRespMan.get("PARTY2_"), depRespMan.get("FULLNAME_")));
            }
        }
        return users;
    }

    //根据条件获取零件图册上传审核人员
    public Collection<TaskExecutor> getPartsAtlasUploadUser() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String id = formDataJson.getString("id");
        JSONObject param = new JSONObject();
        param.put("id", id);
        List<String> fileTypeList = partsAtlasFileDownloadApplyDao.queryApplyFileTypeById(param);
        if (fileTypeList != null && !fileTypeList.isEmpty()) {
            Map<String, Object> params = new HashMap<>();
            if ("零件图册".equals(fileTypeList.get(0))) {
                params.put("REL_TYPE_KEY_", "GROUP-USER-BELONG");
                params.put("groupName", "零件图册归档人");
            } else {
                params.put("REL_TYPE_KEY_", "GROUP-USER-BELONG");
                params.put("groupName", "外购件流程负责人");
            }
            List<Map<String, String>> depRespMans = commonInfoDao.queryUserByGroupName(params);
            if (depRespMans != null && !depRespMans.isEmpty()) {
                for (Map<String, String> depRespMan : depRespMans) {
                    users.add(new TaskExecutor(depRespMan.get("USER_ID_"), depRespMan.get("FULLNAME_")));
                }
            }
        }
        return users;
    }

    //获取选择的数字化文件工程师-作废
    public Collection<TaskExecutor> getSzhgcsUser() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String szhgcsGDId = formDataJson.getString("szhgcsGDId");
        String szhgcsGDName = formDataJson.getString("szhgcsGDName");
        users.add(new TaskExecutor(szhgcsGDId, szhgcsGDName));
        return users;
    }

    //责任部门领导审批任务创建时触发-作废
    public void zrbmldspCreate(Map<String, Object> vars) {
        AbstractExecutionCmd cmd = (AbstractExecutionCmd) vars.get("cmd");
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        formDataJson.put("zrbmReceiptTime", new Date());
        formDataJson.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formDataJson.put("UPDATE_TIME_", new Date());
        if (StringUtils.isNotBlank(formDataJson.getString("gdReceiptTime"))) {
            formDataJson.put("gdReceiptTime", formDataJson.getDate("gdReceiptTime"));
        }
        wrongPartsDao.updateData(formDataJson);
    }

    //归档任务创建时触发-作废
    public void gdCreate(Map<String, Object> vars) {
        AbstractExecutionCmd cmd = (AbstractExecutionCmd) vars.get("cmd");
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String zrbmReceiptTime = formDataJson.getString("zrbmReceiptTime");
        if (StringUtils.isNotBlank(zrbmReceiptTime)) {
            Date date = formDataJson.getDate("zrbmReceiptTime");
            formDataJson.put("zrbmReceiptTime", date);
        }
        formDataJson.put("gdReceiptTime", new Date());
        formDataJson.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formDataJson.put("UPDATE_TIME_", new Date());
        wrongPartsDao.updateData(formDataJson);
    }

    //网关判断(不需要分管领导批准)-作废
    public boolean noNeedLdPZ(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String respDepName = formDataJson.getString("respDepName");
        if (RdmConst.FWGCS_NAME.equals(respDepName)) {
            return true;
        }
        return false;
    }

    //项网关判断(需要分管领导批准)-作废
    public boolean needLdPZ(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String respDepName = formDataJson.getString("respDepName");
        if (!RdmConst.FWGCS_NAME.equals(respDepName)) {
            return true;
        }
        return false;
    }
}
