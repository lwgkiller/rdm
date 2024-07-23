package com.redxun.serviceEngineering.core.service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.AbstractExecutionCmd;
import com.redxun.bpm.core.entity.IExecutionCmd;
import com.redxun.bpm.core.entity.TaskExecutor;
import com.redxun.core.script.GroovyScript;
import com.redxun.org.api.model.IUser;
import com.redxun.org.api.service.UserService;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.serviceEngineering.core.dao.MultiLanguageBuildDao;
import com.redxun.sys.core.entity.SysDic;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.util.ConstantUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

// 项目流程节点的触发事件
@Service
public class BjInventoryScript implements GroovyScript {
    private Logger logger = LoggerFactory.getLogger(BjInventoryScript.class);
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    MultiLanguageBuildService multiLanguageBuildService;
    @Autowired
    MultiLanguageBuildDao multiLanguageBuildDao;
    @Autowired
    SysDicManager sysDicManager;
    @Autowired
    UserService userService;

    public List<Map<String, String>> queryFwsRespUser() {
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        params.put("groupName", RdmConst.FWGCS_NAME);
        List<Map<String, String>> depRespMans = xcmgProjectOtherDao.getDepRespManById(params);
        return depRespMans;
    }

    public Collection<TaskExecutor> getFwsRespUser() {
        List<TaskExecutor> users = new ArrayList<>();
        List<Map<String, String>> depRespMans = queryFwsRespUser();
        if (depRespMans != null && !depRespMans.isEmpty()) {
            for (Map<String, String> depRespMan : depRespMans) {
                users.add(new TaskExecutor(depRespMan.get("PARTY2_"), depRespMan.get("FULLNAME_")));
            }
        }
        return users;
    }

    public List<Map<String, String>> queryBJRespUser() {
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        params.put("groupName", RdmConst.BJGS_NAME);
        List<Map<String, String>> depRespMans = xcmgProjectOtherDao.getDepRespManById(params);
        return depRespMans;
    }


    public Collection<TaskExecutor> queryBjTranslation() {
        List<TaskExecutor> users = new ArrayList<>();
        List<SysDic> sysDicList = sysDicManager.getByTreeKey("bjWeightToTranslation");
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String designModel = formDataJson.getString("designModelName");
        StringBuilder weightBuilder = new StringBuilder();
        for(char c :designModel.toCharArray()){
            if(Character.isDigit(c)){
                weightBuilder.append(c);
            }else  if(weightBuilder.length()>0&&!Character.isDigit(c)){
                break;
            }
        }
        int weight =Integer.parseInt(weightBuilder.toString());
        for (SysDic sysDic : sysDicList) {
            int dicWeight = Integer.parseInt(sysDic.getKey());
            if(weight<=dicWeight){
                IUser user = userService.getByUsername(sysDic.getValue());
                users.add(new TaskExecutor(user.getUserId(), user.getFullname()));
                break;
            }
        }
        return users;
    }

    public Collection<TaskExecutor> getBjRespUser() {
        List<TaskExecutor> users = new ArrayList<>();
        List<Map<String, String>> depRespMans = queryBJRespUser();
        if (depRespMans != null && !depRespMans.isEmpty()) {
            for (Map<String, String> depRespMan : depRespMans) {
                users.add(new TaskExecutor(depRespMan.get("PARTY2_"), depRespMan.get("FULLNAME_")));
            }
        }
        return users;
    }

    public Collection<TaskExecutor> getMultilingualReviewerUser() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        String multilingualKey = "";
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String needLanguage = formDataJson.getString("needLanguage");
        switch (needLanguage) {
            case "英文":
                multilingualKey = "en";
                break;
            case "俄文":
                multilingualKey = "ru";
                break;
            case "葡文":
                multilingualKey = "pt";
                break;
            case "德文":
                multilingualKey = "de";
                break;
            case "西文":
                multilingualKey = "es";
                break;
            case "法文":
                multilingualKey = "fr";
                break;
            case "意文":
                multilingualKey = "it";
                break;
            case "波兰语":
                multilingualKey = "pl";
                break;
            case "土耳其语":
                multilingualKey = "tr";
                break;
            case "瑞典语":
                multilingualKey = "sv";
                break;
            case "丹麦文":
                multilingualKey = "da";
                break;
            case "荷兰语":
                multilingualKey = "nl";
                break;
            case "斯洛文尼亚语":
                multilingualKey = "sl";
                break;
            case "罗马尼亚语":
                multilingualKey = "sl";
                break;
            case "繁体字":
                multilingualKey = "zh-TW";
                break;
            case "泰语":
                multilingualKey = "th";
                break;
            case "匈牙利语":
                multilingualKey = "hu";
                break;
            case "挪威语":
                multilingualKey = "no";
                break;
            case "韩语":
                multilingualKey = "ko";
                break;
            case "印尼语":
                multilingualKey = "id";
                break;
            case "阿拉伯语":
                multilingualKey = "ar";
                break;
            case "日语":
                multilingualKey = "ja";
                break;
            default:
                break;
        }
        String[] multilingualReviewerUserNos = sysDicManager.getBySysTreeKeyAndDicKey(
                "serviceEngineeringLanguageKey", multilingualKey).getValue().split(",");
        for (String userNo : multilingualReviewerUserNos) {
            IUser user = userService.getByUsername(userNo);
            users.add(new TaskExecutor(user.getUserId(), user.getFullname()));
        }
        return users;
    }

    public boolean needTranslation(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String needLanguage = formDataJson.getString("needLanguage");
        if (StringUtils.isNotBlank(needLanguage) && "无需翻译".equalsIgnoreCase(needLanguage)) {
            return false;
        }
        return true;
    }


    public boolean notNeedTranslation(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String needLanguage = formDataJson.getString("needLanguage");
        if (StringUtils.isNotBlank(needLanguage) && "无需翻译".equalsIgnoreCase(needLanguage)) {
            return true;
        }
        return false;
    }
}
