package com.redxun.rdmZhgl.core.service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.entity.AbstractExecutionCmd;
import com.redxun.core.script.GroovyScript;
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
import com.redxun.rdmZhgl.core.dao.ZljsjdsDao;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

// 项目流程节点的触发事件
@Service
public class JsjdsScript implements GroovyScript {
    private Logger logger = LoggerFactory.getLogger(JsjdsScript.class);
    @Autowired
    private ZljsjdsDao zljsjdsDao;
    @Resource
    private SendDDNoticeManager sendDDNoticeManager;

    public void updateBmscwcScript(Map<String, Object> vars) {
        // 更新审批完成时间
        logger.info("技术交底书部门审查完成");
        AbstractExecutionCmd cmd = (AbstractExecutionCmd)vars.get("cmd");
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        if (formDataJson.isEmpty()) {
            logger.warn("formData is blank");
            return;
        }
        Map<String, Object> param = new HashMap<>();
        param.put("jsjdsId", formDataJson.getString("jsjdsId"));
        zljsjdsDao.updateBmscwcTime(param);

        // 发送通知
        String CREATE_BY_ = formDataJson.getString("CREATE_BY_");
        JSONObject noticeObj = new JSONObject();
        StringBuilder stringBuilder = new StringBuilder("【专利申请交底书】");
        stringBuilder.append("，您的专利技术交底书：").append(formDataJson.getString("zlName")).append("已审核通过");
        stringBuilder.append("，请上传至徐工知识产权管理系统http://10.1.1.57/WJJ/Login.aspx");
        noticeObj.put("content", stringBuilder.toString());
        sendDDNoticeManager.sendNoticeForCommon(CREATE_BY_, noticeObj);
    }

    public void resetBmscwcScript(Map<String, Object> vars) {
        // 更新审批完成时间
        logger.info("技术交底书部门驳回");
        AbstractExecutionCmd cmd = (AbstractExecutionCmd)vars.get("cmd");
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        if (formDataJson.isEmpty()) {
            logger.warn("formData is blank");
            return;
        }
        Map<String, Object> param = new HashMap<>();
        param.put("jsjdsId", formDataJson.getString("jsjdsId"));
        zljsjdsDao.resetBmscwcTime(param);
    }

    public boolean yesLx(AbstractExecutionCmd cmd, Map<String, Object> vars) {
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
        if("是".equals(formDataJson.getString("sflx"))){
            return true;
        }
        return false;
    }
    public boolean noLx(AbstractExecutionCmd cmd, Map<String, Object> vars) {
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
        if("否".equals(formDataJson.getString("sflx"))){
            return true;
        }
        return false;
    }
}
