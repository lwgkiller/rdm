package com.redxun.serviceEngineering.core.service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.IExecutionCmd;
import com.redxun.bpm.core.entity.TaskExecutor;
import com.redxun.core.script.GroovyScript;
import com.redxun.core.util.DateUtil;
import com.redxun.org.api.model.IUser;
import com.redxun.org.api.service.UserService;
import com.redxun.rdmZhgl.core.service.PatentInterpretationScript;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.serviceEngineering.core.dao.MultilingualGlossaryDao;
import com.redxun.sys.core.manager.SysDicManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MultilingualGlossaryScript implements GroovyScript {
    private Logger logger = LoggerFactory.getLogger(PatentInterpretationScript.class);
    @Autowired
    MultilingualGlossaryService multilingualGlossaryService;
    @Autowired
    MultilingualGlossaryDao multilingualGlossaryDao;
    @Autowired
    SysDicManager sysDicManager;
    @Autowired
    UserService userService;

    //..
    public void taskCreateScript(Map<String, Object> vars) {
        try {
            //@lwgkiller:阿诺多罗 下面这个cmd可以取代getDetail，而且传来的是表单的最新数据
            JSONObject jsonObject = ProcessHandleHelper.getProcessCmd().getJsonDataObject();
            String activitiId = vars.get("activityId").toString();
            jsonObject.put("businessStatus", activitiId);
            multilingualGlossaryService.updateBusiness(jsonObject);
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
            List<JSONObject> itemList = multilingualGlossaryService.getItemList(jsonObject.getString("id"));
            if (activitiId.equalsIgnoreCase("A-editing")) {//冗余存储一下人员
                jsonObject.put("applyUserId", ContextUtil.getCurrentUserId());
                jsonObject.put("applyUser", ContextUtil.getCurrentUser().getFullname());
                jsonObject.put("applyDate", DateUtil.formatDate(new Date(), DateUtil.DATE_FORMAT_YMD));
            } else if(activitiId.equalsIgnoreCase("AchineseReviewing")) {//冗余存储一下人员
                jsonObject.put("chReviewerId", ContextUtil.getCurrentUserId());
                jsonObject.put("chReviewer", ContextUtil.getCurrentUser().getFullname());
                for (JSONObject item : itemList) {
                    item.put("chineseUpdateBy", ContextUtil.getCurrentUser().getUserNo());
                    multilingualGlossaryDao.updateItem(item);
                }
            } else if (activitiId.equalsIgnoreCase("B-chineseReviewing")) {//冗余存储一下人员
                jsonObject.put("chReviewerId", ContextUtil.getCurrentUserId());
                jsonObject.put("chReviewer", ContextUtil.getCurrentUser().getFullname());
                for (JSONObject item : itemList) {
                    item.put("chineseUpdateBy", ContextUtil.getCurrentUser().getUserNo());
                    multilingualGlossaryDao.updateItem(item);
                }
            } else if (activitiId.equalsIgnoreCase("C-englishTranslation")) {//冗余存储一下人员
                jsonObject.put("enReviewerId", ContextUtil.getCurrentUserId());
                jsonObject.put("enReviewer", ContextUtil.getCurrentUser().getFullname());
                for (JSONObject item : itemList) {
                    item.put("enUpdateBy", ContextUtil.getCurrentUser().getUserNo());
                    multilingualGlossaryDao.updateItem(item);
                }
            } else if (activitiId.equalsIgnoreCase("D-multilingualTranslation")) {//冗余存储一下人员
                jsonObject.put("multilingualReviewerId", ContextUtil.getCurrentUserId());
                jsonObject.put("multilingualReviewer", ContextUtil.getCurrentUser().getFullname());
                jsonObject.put("businessStatus", "E-close");
                for (JSONObject item : itemList) {
                    item.put("multilingualUpdateBy", ContextUtil.getCurrentUser().getUserNo());
                    multilingualGlossaryDao.updateItem(item);
                }
                //处理语言库更新
                multilingualGlossaryService.glossaryUpdateProcessing(jsonObject);
            }
            multilingualGlossaryService.updateBusiness(jsonObject);
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
        List<JSONObject> itemList = multilingualGlossaryService.getItemList(formDataJson.getString("id"));
        if (itemList.size() > 0) {
            String multilingualKey = itemList.get(0).getString("multilingualKey");
            String[] multilingualReviewerUserNos = sysDicManager.getBySysTreeKeyAndDicKey(
                    "serviceEngineeringLanguageKey", multilingualKey).getValue().split(",");
            for (String userNo : multilingualReviewerUserNos) {
                IUser user = userService.getByUsername(userNo);
                users.add(new TaskExecutor(user.getUserId(), user.getFullname()));
            }
        }
        return users;
    }
}
