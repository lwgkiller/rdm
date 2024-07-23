package com.redxun.presaleDocuments.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.AbstractExecutionCmd;
import com.redxun.bpm.core.entity.TaskExecutor;
import com.redxun.core.script.GroovyScript;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.sys.core.entity.SysDic;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.sys.org.manager.OsGroupManager;
import com.redxun.sys.org.manager.OsUserManager;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PresaleDocumentsDownloadApplyScript implements GroovyScript {
    private Logger logger = LoggerFactory.getLogger(PresaleDocumentsDownloadApplyScript.class);
    @Autowired
    private PresaleDocumentsDownloadApplyService presaleDocumentsDownloadApplyService;
    @Autowired
    private SysDicManager sysDicManager;
    @Autowired
    private CommonInfoDao commonInfoDao;
    @Autowired
    private OsUserManager osUserManager;
    @Autowired
    private OsGroupManager osGroupManager;

    //..当前文件类型和当前用户是否符合绿色通道标准
    private boolean currentUserAndBusinessTypeIsGreen(List<String> businessTypes) {
        Map<String, Boolean> businessTypesToIsGreen = new HashedMap();
        for (String businessType : businessTypes) {
            businessTypesToIsGreen.put(businessType, false);
            SysDic sysDic = sysDicManager.getBySysTreeKeyAndDicKey("PresaleDocumentDownloadGreenPerson", businessType);
            String[] greenUserNos = sysDic.getValue().split(",");
            for (String greenUserNo : greenUserNos) {
                if (greenUserNo.equalsIgnoreCase(ContextUtil.getCurrentUser().getUserNo())) {
                    businessTypesToIsGreen.put(businessType, true);
                    break;
                }
            }
        }
        boolean result = true;
        for (Boolean value : businessTypesToIsGreen.values()) {
            result &= value;
        }
        return result;
    }

    //..不是绿色通道情景
    public boolean isNotGreen(AbstractExecutionCmd cmd, Map<String, Object> vars) {
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
        List<String> businessTypes = new ArrayList<>();
        JSONArray jsonArrayItemData = formDataJson.getJSONArray("documentListGrid");
        for (Object object : jsonArrayItemData) {
            JSONObject jsonObject = (JSONObject) object;
            businessTypes.add(jsonObject.getString("businessType"));
        }
        if (businessTypes.isEmpty()) {
            return false;
        } else {
            return !this.currentUserAndBusinessTypeIsGreen(businessTypes);
        }
    }

    //..是绿色通道情景
    public boolean isGreen(AbstractExecutionCmd cmd, Map<String, Object> vars) {
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
        List<String> businessTypes = new ArrayList<>();
        JSONArray jsonArrayItemData = formDataJson.getJSONArray("documentListGrid");
        for (Object object : jsonArrayItemData) {
            JSONObject jsonObject = (JSONObject) object;
            businessTypes.add(jsonObject.getString("businessType"));
        }
        if (businessTypes.isEmpty()) {
            return false;
        } else {
            return this.currentUserAndBusinessTypeIsGreen(businessTypes);
        }
    }

    //..按照业务类型获取审核2人员
    public Collection<TaskExecutor> getB() {
        List<TaskExecutor> users = new ArrayList<>();
        JSONObject formDataJson = JSONObject.parseObject(ProcessHandleHelper.getProcessCmd().getJsonData());
        JSONArray jsonArrayItemData = formDataJson.getJSONArray("documentListGrid");
        for (Object document : jsonArrayItemData) {
            JSONObject documentJson = (JSONObject) document;
            String repUserId = documentJson.getString("repUserId");
            List<JSONObject> repUsers = commonInfoDao.queryDeptRespByUserId(repUserId);
            for (JSONObject repUser : repUsers) {
                users.add(new TaskExecutor(repUser.getString("USER_ID_"), repUser.getString("FULLNAME_")));
            }
        }
        return users;
    }

    //..
    public void taskCreateScript(Map<String, Object> vars) throws Exception {
        try {
            //@lwgkiller:阿诺多罗 下面这个cmd可以取代getDetail，而且传来的是表单的最新数据
            JSONObject jsonObject = ProcessHandleHelper.getProcessCmd().getJsonDataObject();
            String activitiId = vars.get("activityId").toString();
            jsonObject.put("businessStatus", activitiId);
            presaleDocumentsDownloadApplyService.updateBusiness(jsonObject);
        } catch (Exception e) {
            ProcessHandleHelper.addErrorMsg(e.getMessage());
            throw e;
        }
    }

    //..
    public void taskEndScript(Map<String, Object> vars) throws Exception {
        try {
            //@lwgkiller:阿诺多罗 下面这个cmd可以取代getDetail，而且传来的是表单的最新数据
            JSONObject jsonObject = ProcessHandleHelper.getProcessCmd().getJsonDataObject();
            String activitiId = vars.get("activityId").toString();
            if (activitiId.equalsIgnoreCase("A")) {//第一个编辑节点通过后
                jsonObject.put("applyUserId", ContextUtil.getCurrentUserId());
                jsonObject.put("applyTime", DateUtil.formatDate(new Date(), DateUtil.DATE_FORMAT_YMD));
                List<String> businessTypes = new ArrayList<>();
                JSONArray jsonArrayItemData = jsonObject.getJSONArray("documentListGrid");
                for (Object object : jsonArrayItemData) {
                    businessTypes.add(((JSONObject) object).getString("businessType"));
                }
                if (this.currentUserAndBusinessTypeIsGreen(businessTypes)) {//当前是绿色通道情景
                    //更新最终状态
                    jsonObject.put("businessStatus", "Z");
                    jsonObject.put("endTime", DateUtil.formatDate(new Date(), DateUtil.DATE_FORMAT_YMD));
                }

            } else if (activitiId.equalsIgnoreCase("C")) {
                //更新最终状态
                jsonObject.put("businessStatus", "Z");
                jsonObject.put("endTime", DateUtil.formatDate(new Date(), DateUtil.DATE_FORMAT_YMD));
            }
            presaleDocumentsDownloadApplyService.updateBusiness(jsonObject);
        } catch (Exception e) {
            ProcessHandleHelper.addErrorMsg(e.getMessage());
            throw e;
        }
    }
}
