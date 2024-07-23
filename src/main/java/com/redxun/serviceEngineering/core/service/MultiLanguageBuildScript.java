package com.redxun.serviceEngineering.core.service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.AbstractExecutionCmd;
import com.redxun.bpm.core.entity.IExecutionCmd;
import com.redxun.bpm.core.entity.TaskExecutor;
import com.redxun.core.script.GroovyScript;
import com.redxun.core.util.DateUtil;
import com.redxun.org.api.model.IUser;
import com.redxun.org.api.service.UserService;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmZhgl.core.service.PatentInterpretationScript;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.serviceEngineering.core.dao.MultiLanguageBuildDao;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MultiLanguageBuildScript implements GroovyScript {
    private Logger logger = LoggerFactory.getLogger(PatentInterpretationScript.class);
    @Autowired
    MultiLanguageBuildService multiLanguageBuildService;
    @Autowired
    MultiLanguageBuildDao multiLanguageBuildDao;
    @Autowired
    SysDicManager sysDicManager;
    @Autowired
    UserService userService;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;

    //..
    public void taskCreateScript(Map<String, Object> vars) {
        try {
            //@lwgkiller:阿诺多罗 下面这个cmd可以取代getDetail，而且传来的是表单的最新数据
            JSONObject jsonObject = ProcessHandleHelper.getProcessCmd().getJsonDataObject();
            String activitiId = vars.get("activityId").toString();
            jsonObject.put("businessStatus", activitiId);
            multiLanguageBuildService.updateBusiness(jsonObject);
        } catch (Exception e) {
            logger.error("Exception in taskCreateScript", e);
        }
    }

    //..
    public void taskEndScript(Map<String, Object> vars) {
        try {
            //@lwgkiller:阿诺多罗 下面这个cmd可以取代getDetail，而且传来的是表单的最新数据
            JSONObject jsonObject = ProcessHandleHelper.getProcessCmd().getJsonDataObject();
            String activitiId = vars.get("activityId").toString();
            List<JSONObject> itemList = multiLanguageBuildService.getItemList(jsonObject.getString("id"));
            if (activitiId.equalsIgnoreCase("A-editing")) {//冗余存储一下人员
                jsonObject.put("applyUserId", ContextUtil.getCurrentUserId());
                jsonObject.put("applyUser", ContextUtil.getCurrentUser().getFullname());
                jsonObject.put("applyDate", DateUtil.formatDate(new Date(), DateUtil.DATE_FORMAT_YMD));
                //申请人提交的时候进行比对词库表
                multiLanguageBuildService.updateBusiness(jsonObject);
            } else if (activitiId.equalsIgnoreCase("B-chineseReviewing")) {//冗余存储一下人员
                jsonObject.put("chReviewerId", ContextUtil.getCurrentUserId());
                jsonObject.put("chReviewer", ContextUtil.getCurrentUser().getFullname());
                for (JSONObject item : itemList) {
                    item.put("chineseUpdateBy", ContextUtil.getCurrentUser().getUserNo());
                    multiLanguageBuildDao.updateItem(item);
                }
                multiLanguageBuildService.updateBusiness(jsonObject);
            } else if (activitiId.equalsIgnoreCase("C-englishTranslation")) {//冗余存储一下人员
                jsonObject.put("enReviewerId", ContextUtil.getCurrentUserId());
                jsonObject.put("enReviewer", ContextUtil.getCurrentUser().getFullname());

                for (JSONObject item : itemList) {
                    item.put("enUpdateBy", ContextUtil.getCurrentUser().getUserNo());
                    multiLanguageBuildDao.updateItem(item);
                }
                if(jsonObject.getString("multilingualSign").equalsIgnoreCase("0"))
                {
                    jsonObject.put("businessStatus", "E-close");
                    //处理语言库更新
//                    multiLanguageBuildService.glossaryUpdateProcessing(jsonObject);
                }
                multiLanguageBuildService.updateBusiness(jsonObject);
            } else if (activitiId.equalsIgnoreCase("D-multilingualTranslation")) {//冗余存储一下人员
                jsonObject.put("multilingualReviewerId", ContextUtil.getCurrentUserId());
                jsonObject.put("multilingualReviewer", ContextUtil.getCurrentUser().getFullname());
                jsonObject.put("businessStatus", "E-close");
                for (JSONObject item : itemList) {
                    item.put("multilingualUpdateBy", ContextUtil.getCurrentUser().getUserNo());
                    multiLanguageBuildDao.updateItem(item);
                }
                multiLanguageBuildService.updateBusiness(jsonObject);
                //处理语言库更新
//                multiLanguageBuildService.glossaryUpdateProcessing(jsonObject);
            }

        } catch (Exception e) {
            logger.error("Exception in taskEndScript", e);
        }
    }

    //..根据语种获取翻译人
    public Collection<TaskExecutor> getMultilingualReviewerUser() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        List<JSONObject> itemList = multiLanguageBuildService.getItemList(formDataJson.getString("id"));
        if (itemList.size() > 0) {
            String multilingualKey = formDataJson.getString("multilingualSign");
            String[] multilingualReviewerUserNos = sysDicManager.getBySysTreeKeyAndDicKey(
                    "serviceEngineeringLanguageKey", multilingualKey).getValue().split(",");
            for (String userNo : multilingualReviewerUserNos) {
                IUser user = userService.getByUsername(userNo);
                users.add(new TaskExecutor(user.getUserId(), user.getFullname()));
            }
        }
        return users;
    }
    // 多语言翻译
    public boolean needMutiling(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String multilingualKey = formDataJson.getString("multilingualSign");
        if (multilingualKey.equalsIgnoreCase("0")) {
            return false;
        }
            return true;

    }
       //无多语言
    public boolean noMutiling(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String multilingualKey = formDataJson.getString("multilingualSign");
        if (multilingualKey.equalsIgnoreCase("0")) {
            return true;
        }
        return false;
    }

    // 跳过英语翻译阶段
    public boolean jumpEnglish(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String jumpEnglish = formDataJson.getString("jumpEnglish");
        if ("true".equalsIgnoreCase(jumpEnglish)) {
            return true;
        }
        return false;
    }

    // 不跳过英语翻译阶段
    public boolean noJumpEnglish(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String jumpEnglish = formDataJson.getString("jumpEnglish");
        if ("false".equalsIgnoreCase(jumpEnglish)) {
            return true;
        }
        return false;
    }

    public boolean yesExternal(AbstractExecutionCmd cmd, Map<String, Object> vars){
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String externalSelect = formDataJson.getString("externalSelect");
        if (externalSelect.equalsIgnoreCase("true")) {
            return true;
        }
        return  false;
    }
    public boolean noExternal(AbstractExecutionCmd cmd, Map<String, Object> vars){
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String externalSelect = formDataJson.getString("externalSelect");
        if (externalSelect.equalsIgnoreCase("false")) {
            return true;
        }

        return false;
    }
    // 获取服务工程部部门领导信息
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
    //获取流程中使用的处理人为分管领导的信息
    public Collection<TaskExecutor> fgldUserInfos() {
        List<TaskExecutor> users = new ArrayList<>();
        List<Map<String, String>> leaderInfos = rdmZhglUtil.queryFgld();
        if (leaderInfos != null && !leaderInfos.isEmpty()) {
            for (Map<String, String> oneLeader : leaderInfos) {
                users.add(new TaskExecutor(oneLeader.get("USER_ID_"), oneLeader.get("FULLNAME_")));
            }
        }
        return users;
    }
}
