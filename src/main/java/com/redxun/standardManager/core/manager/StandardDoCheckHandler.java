package com.redxun.standardManager.core.manager;

import java.util.*;

import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.handler.ProcessEndHandler;
import com.redxun.bpm.activiti.handler.ProcessStartPreHandler;
import com.redxun.bpm.activiti.handler.TaskPreHandler;
import com.redxun.bpm.core.entity.*;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.saweb.util.IdUtil;
import com.redxun.standardManager.core.dao.StandardDoCheckDao;

@Service
public class StandardDoCheckHandler implements ProcessStartPreHandler, TaskPreHandler, ProcessEndHandler {
    private Logger logger = LoggerFactory.getLogger(StandardDoCheckHandler.class);
    @Autowired
    private StandardDoCheckManager standardDoCheckManager;
    @Autowired
    private StandardDoCheckDao standardDoCheckDao;
    @Autowired
    private SendDDNoticeManager sendDDNoticeManager;
    @Autowired
    private CommonInfoManager commonInfoManager;
    @Autowired
    private CommonInfoDao commonInfoDao;

    // 整个流程启动之前的处理，草稿也会调用这里
    @Override
    public void processStartPreHandle(ProcessStartCmd processStartCmd) {
        logger.info("StandardDoCheckHandler processStartPreHandle");
        String id = addOrUpdateStandardDoCheck(processStartCmd);
        if (StringUtils.isNotBlank(id)) {
            processStartCmd.setBusinessKey(id);
        }
    }

    @Override
    public void taskPreHandle(IExecutionCmd cmd, Task task, String busKey) {
        ProcessNextCmd processNextCmd = (ProcessNextCmd)cmd;
        addOrUpdateStandardDoCheck(processNextCmd);
    }

    // 驳回场景cmd中没有表单数据
    private String addOrUpdateStandardDoCheck(AbstractExecutionCmd cmd) {
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
        JSONObject result = new JSONObject();
        if (StringUtils.isBlank(formDataJson.getString("id"))) {
            standardDoCheckManager.createStandardDoCheck(formDataJson);
        } else {
            standardDoCheckManager.updateStandardDoCheck(formDataJson);
        }
        return formDataJson.getString("id");
    }

    /**
     * 流程结束认为自查完成
     * 
     * @param bpmInst
     */
    @Override
    public void endHandle(BpmInst bpmInst) {
        if ("SUCCESS_END".equalsIgnoreCase(bpmInst.getStatus())) {
            String id = bpmInst.getBusKey();
            Map<String, Object> param = new HashMap<>();
            // 查找这个流程中，自查是否存在不符合项，写入基本表，同时将不符合项写入考核表，并发钉钉通知
            param.put("baseInfoId", id);
            List<JSONObject> resultNotOkList = standardDoCheckDao.queryCheckDetailResultNotOkList(param);
            if (resultNotOkList == null || resultNotOkList.isEmpty()) {
                param.clear();
                param.put("id", id);
                param.put("zcStatus", RdmConst.STANDARD_DOCHECK_WC);
                param.put("zcResult", "符合");
                standardDoCheckDao.updateDoCheckStatus(param);
                return;
            }
            // 更新基本表状态
            param.clear();
            param.put("id", id);
            param.put("zcStatus", RdmConst.STANDARD_DOCHECK_WC);
            param.put("zcResult", "不符合");
            standardDoCheckDao.updateDoCheckStatus(param);

            // 整理不合格项考核数据
            Set<String> closeRespUserNameSetJszx = new HashSet<>();
            StringBuilder closeRespUserNameStrJszx = new StringBuilder();
            Set<String> closeRespUserNameSetOther = new HashSet<>();
            StringBuilder closeRespUserNameStrOther = new StringBuilder();
            List<JSONObject> punishList = new ArrayList<>();
            Map<String, String> deptId2Name = commonInfoManager.queryDeptUnderJSZX();
            for (JSONObject oneData : resultNotOkList) {
                JSONObject onePunish = new JSONObject();
                onePunish.put("id", IdUtil.getId());
                onePunish.put("baseInfoId", oneData.getString("baseInfoId"));
                onePunish.put("punishType", "结果不合格");
                onePunish.put("notOkDetailId", oneData.getString("id"));
                onePunish.put("userId", oneData.getString("closeRespUserId"));
                onePunish.put("userName", oneData.getString("closeRespUserName"));
                onePunish.put("CREATE_BY_", "1");
                onePunish.put("CREATE_TIME_", new Date());
                punishList.add(onePunish);

                String closeDeptId = oneData.getString("closeDeptId");
                String closeDeptName = oneData.getString("closeDeptName");
                if (closeDeptName.contains("挖掘机械研究院") || deptId2Name.containsKey(closeDeptId)) {
                    if (!closeRespUserNameSetJszx.contains(onePunish.getString("userName"))) {
                        closeRespUserNameSetJszx.add(onePunish.getString("userName"));
                        closeRespUserNameStrJszx.append(onePunish.getString("userName") + "，");
                    }
                } else {
                    if (!closeRespUserNameSetOther.contains(onePunish.getString("userName"))) {
                        closeRespUserNameSetOther.add(onePunish.getString("userName"));
                        closeRespUserNameStrOther.append(onePunish.getString("userName") + "，");
                    }
                }
            }
            // 写入考核表
            standardDoCheckDao.batchInsertResultNotOkPunish(punishList);

            // 发钉钉通知
            if (!closeRespUserNameSetJszx.isEmpty()) {
                String notice = "标准编号：" + resultNotOkList.get(0).getString("standardNumber") + "，标准名称："
                    + resultNotOkList.get(0).getString("standardName") + "，自查年份："
                    + resultNotOkList.get(0).getString("doCheckYear") + "，责任人："
                    + closeRespUserNameStrJszx.substring(0, closeRespUserNameStrJszx.length() - 1);
                sendRemindNotice(RdmConst.STANDARD_DOCHECK_NOTICE_JSZX, notice);
            }
            if (!closeRespUserNameSetOther.isEmpty()) {
                String notice = "标准编号：" + resultNotOkList.get(0).getString("standardNumber") + "，标准名称："
                    + resultNotOkList.get(0).getString("standardName") + "，自查年份："
                    + resultNotOkList.get(0).getString("doCheckYear") + "，责任人："
                    + closeRespUserNameStrOther.substring(0, closeRespUserNameStrOther.length() - 1);
                sendRemindNotice(RdmConst.STANDARD_DOCHECK_NOTICE_OTHER, notice);
            }
        }
    }

    private void sendRemindNotice(String noticeGroupName, String noticeContent) {
        Map<String, Object> params = new HashMap<>();
        params.put("groupName", noticeGroupName);
        params.put("REL_TYPE_KEY_", "GROUP-USER-BELONG");
        List<Map<String, String>> users = commonInfoDao.queryUserByGroupName(params);
        String userIds = "";
        if (users != null && !users.isEmpty()) {
            for (Map<String, String> oneUser : users) {
                userIds += oneUser.get("USER_ID_") + ",";
            }
            userIds = userIds.substring(0, userIds.length() - 1);
            JSONObject noticeObj = new JSONObject();
            noticeObj.put("content", "【标准执行自查关闭不合格提醒】\n" + noticeContent);
            sendDDNoticeManager.sendNoticeForCommon(userIds, noticeObj);
        }
    }
}
