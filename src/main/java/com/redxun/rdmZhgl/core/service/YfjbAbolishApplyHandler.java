package com.redxun.rdmZhgl.core.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.redxun.core.util.DateUtil;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
import com.redxun.rdmZhgl.core.dao.YfjbAbolishApplyDao;
import com.redxun.rdmZhgl.core.dao.YfjbBaseInfoDao;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.handler.ProcessEndHandler;
import com.redxun.bpm.activiti.handler.ProcessStartAfterHandler;
import com.redxun.bpm.activiti.handler.ProcessStartPreHandler;
import com.redxun.bpm.activiti.handler.TaskPreHandler;
import com.redxun.bpm.core.entity.*;
import com.redxun.bpm.core.entity.config.ProcessConfig;

/**
 * @author zz
 * */
@Service
public class YfjbAbolishApplyHandler implements ProcessStartPreHandler, TaskPreHandler, ProcessStartAfterHandler, ProcessEndHandler {
    private Logger logger = LoggerFactory.getLogger(YfjbAbolishApplyHandler.class);
    @Resource
    private YfjbAbolishApplyService yfjbAbolishApplyService;
    @Resource
    private YfjbAbolishApplyDao yfjbAbolishApplyDao;
    @Resource
    private YfjbBaseInfoDao yfjbBaseInfoDao;
    @Resource
    private SendDDNoticeManager sendDDNoticeManager;
    @Resource
    private CommonInfoDao commonInfoDao;

    /**
     * 整个流程启动之前的处理，草稿也会调用这里
     * */
    @Override
    public void processStartPreHandle(ProcessStartCmd processStartCmd) {
        logger.info("ProcessApplyHandler processStartPreHandle");
        Map<String, Object> changeApply = addOrUpdateApplyInfo(processStartCmd);
        if (changeApply != null) {
            processStartCmd.setBusinessKey(changeApply.get("id").toString());
        }
    }

    /**
     * 任务审批之后的前置处理器id
     * */
    @Override
    public void taskPreHandle(IExecutionCmd iExecutionCmd, Task task, String busKey) {
        logger.info("taskPreHandle");
        ProcessNextCmd processNextCmd = (ProcessNextCmd)iExecutionCmd;
        addOrUpdateApplyInfo(processNextCmd);
    }

    /**
     * 驳回场景cmd中没有表单数据
     * */
    private Map<String, Object> addOrUpdateApplyInfo(AbstractExecutionCmd cmd) {
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
        Map<String, Object> applyObj = JSONObject.parseObject(formDataJson.toJSONString(), Map.class);
        if (applyObj.get("id") == null || StringUtils.isBlank(applyObj.get("id").toString())) {
            yfjbAbolishApplyService.add(applyObj);
        } else {
            yfjbAbolishApplyService.update(applyObj);
        }
        return applyObj;
    }
    // 流程成功结束之后的处理
    @Override
    public void endHandle(BpmInst bpmInst) {
        if ("SUCCESS_END".equalsIgnoreCase(bpmInst.getStatus())) {
            String id = bpmInst.getBusKey();
            Map<String,Object> changeObj = yfjbAbolishApplyDao.getObjectById(id);
            String mainId = CommonFuns.nullToString(changeObj.get("mainId"));
            Map<String,Object> map = new HashMap<>(16);
            map.put("id",mainId);
            map.put("infoStatus","3");
            yfjbBaseInfoDao.updateObject(map);
            //发送钉钉通知
            //获取创建人所在部门id
            //获取本部门降本专员信息
            JSONObject yfjbObj = yfjbBaseInfoDao.getObjectById(mainId);
            JSONObject paramJson = new JSONObject();
            paramJson.put("deptId",yfjbObj.getString("deptId"));
            paramJson.put("roleKey","JBZY");
            List<JSONObject> userList = commonInfoDao.getDeptRoleUsers(paramJson);
            StringBuffer userIds = new StringBuffer();
            for(JSONObject temp:userList){
                userIds.append(temp.getString("PARTY2_")).append(",");
            }
            paramJson = new JSONObject();
            paramJson.put("dim","2");
            paramJson.put("key","YFJB-GLY");
            List<JSONObject> groupUserList = commonInfoDao.getGroupUsers(paramJson);
            for(JSONObject temp:groupUserList){
                userIds.append(temp.getString("PARTY2_")).append(",");
            }
            JSONObject noticeObj = new JSONObject();
            StringBuilder stringBuilder = new StringBuilder("【研发降本】");
            stringBuilder.append("，销售型号：").append(yfjbObj.getString("saleModel")).append("的降本项目已作废，请知晓");
            stringBuilder.append("，通知时间：").append(XcmgProjectUtil.getNowLocalDateStr(DateUtil.DATE_FORMAT_FULL));
            noticeObj.put("content", stringBuilder.toString());
            sendDDNoticeManager.sendNoticeForCommon(userIds.toString(), noticeObj);
        }
    }

    @Override
    public String processStartAfterHandle(ProcessConfig processConfig, ProcessStartCmd cmd, BpmInst bpmInst) {
        if("RUNNING".equals(bpmInst.getStatus())){
            String id = bpmInst.getBusKey();
            Map<String,Object> changeObj = yfjbAbolishApplyDao.getObjectById(id);
            String mainId = CommonFuns.nullToString(changeObj.get("mainId"));
            Map<String,Object> map = new HashMap<>(16);
            map.put("id",mainId);
            map.put("infoStatus","4");
            yfjbBaseInfoDao.updateInfoStatus(map);
        }
        return null;
    }
}
