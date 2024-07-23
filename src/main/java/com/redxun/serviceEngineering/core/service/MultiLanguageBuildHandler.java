package com.redxun.serviceEngineering.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.handler.ProcessEndHandler;
import com.redxun.bpm.activiti.handler.ProcessStartPreHandler;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.AbstractExecutionCmd;
import com.redxun.bpm.core.entity.BpmInst;
import com.redxun.bpm.core.entity.IExecutionCmd;
import com.redxun.bpm.core.entity.ProcessStartCmd;
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.serviceEngineering.core.dao.MulitilingualTranslationDao;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MultiLanguageBuildHandler implements ProcessStartPreHandler,ProcessEndHandler {
    private Logger logger = LoggerFactory.getLogger(MultiLanguageBuildHandler.class);
    @Autowired
    private MultiLanguageBuildService multiLanguageBuildService;
    @Autowired
    private MulitilingualTranslationDao mulitilingualTranslationDao;
    @Autowired
    private SendDDNoticeManager sendDDNoticeManager;

    //..整个流程启动之前的处理(进行表单的创建或更新，草稿和提交都会调用)
    @Override
    public void processStartPreHandle(ProcessStartCmd processStartCmd) {
        String formData = processStartCmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        formDataJson.put("INST_ID_", processStartCmd.getBpmInstId());
        processStartCmd.setJsonData(formDataJson.toJSONString());
        String businessId = createOrUpdateBusinessByFormData(processStartCmd);
        if (StringUtils.isNotBlank(businessId)) {
            processStartCmd.setBusinessKey(businessId);
        }
    }
    // 流程成功结束之后的处理
    @Override
    public void endHandle(BpmInst bpmInst) {
        if ("SUCCESS_END".equalsIgnoreCase(bpmInst.getStatus())) {
            try {
                Map<String, Object> params = new HashMap<>();
                List<Map<String, Object>> lctcList = mulitilingualTranslationDao.queryLjtcList(params);
                Set<String> materialCodes = new HashSet<>();
                for (Map<String, Object> oneLjtc:lctcList){
                    materialCodes.add(oneLjtc.get("materialCode").toString().trim());
                }
                IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
                String formData = cmd.getJsonData();
                JSONObject formDataJson = JSONObject.parseObject(formData);
                String multilingualSign = formDataJson.getString("multilingualSign");
                JSONArray changeGridDataJson = formDataJson.getJSONArray("SUB_itemListGrid");
                for (int i = 0; i < changeGridDataJson.size(); i++) {
                    JSONObject dataJson = new JSONObject();
                    JSONObject oneObject = changeGridDataJson.getJSONObject(i);
                    String materialCode = oneObject.getString("materialCode");
                    String originChinese = oneObject.getString("originChinese");
                    String chineseName = oneObject.getString("chinese");
                    String englishName = oneObject.getString("en");
                    String multilingualText = oneObject.getString("multilingualText");
                    dataJson.put("originChinese",originChinese);
                    dataJson.put("chineseName",chineseName);
                    dataJson.put("englishName",englishName);
                    dataJson.put("materialCode",materialCode);
                    if("pt".equals(multilingualSign)){
                        dataJson.put("portugueseName",multilingualText);
                    } else if("ru".equals(multilingualSign)){
                        dataJson.put("russianName",multilingualText);
                    }else if("de".equals(multilingualSign)){
                        dataJson.put("germanyName",multilingualText);
                    }else if("es".equals(multilingualSign)){
                        dataJson.put("spanishName",multilingualText);
                    }else if("fr".equals(multilingualSign)){
                        dataJson.put("frenchName",multilingualText);
                    }else if("it".equals(multilingualSign)){
                        dataJson.put("italianName",multilingualText);
                    }else if("da".equals(multilingualSign)){
                        dataJson.put("danishName",multilingualText);
                    }else if("nl".equals(multilingualSign)){
                        dataJson.put("dutchName",multilingualText);
                    }else if("pl".equals(multilingualSign)){
                        dataJson.put("polishName",multilingualText);
                    }else if("tr".equals(multilingualSign)){
                        dataJson.put("turkishName",multilingualText);
                    }
                    if(materialCodes.contains(materialCode)){
                        dataJson.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                        dataJson.put("UPDATE_TIME_", new Date());
                        mulitilingualTranslationDao.updateLjtcByCode(dataJson);
                    }else {
                        dataJson.put("chineseId", IdUtil.getId());
                        dataJson.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                        dataJson.put("CREATE_TIME_", new Date());
                        mulitilingualTranslationDao.addLjtc(dataJson);
                    }
                }
                // 给创建人发钉钉通知，提醒查看
                JSONObject noticeObj = new JSONObject();
                noticeObj.put("content", "您申请的缺词补充流程已审批完成。");
                sendDDNoticeManager.sendNoticeForCommon(formDataJson.getString("applyUserId"), noticeObj);
            } catch (Exception ex) {
                logger.error("流程启动失败", ex.getMessage());
            } finally {
                ContextUtil.clearCurLocal();
            }
        }
    }
    //..
    private String createOrUpdateBusinessByFormData(AbstractExecutionCmd cmd) {
        String formData = cmd.getJsonData();
        if (StringUtils.isBlank(formData)) {
            logger.warn("formData is blank");
            return null;
        }
        JSONObject formDataJson = JSONObject.parseObject(formData);
        if (formDataJson.isEmpty()) {
            logger.warn("formData is blank");
            return null;
        }
        //如果是新增的情况
        if (StringUtils.isBlank(formDataJson.getString("id"))) {
            multiLanguageBuildService.createBusiness(formDataJson);
        } else {
            multiLanguageBuildService.updateBusiness(formDataJson);
        }
        return formDataJson.getString("id");
    }

}
