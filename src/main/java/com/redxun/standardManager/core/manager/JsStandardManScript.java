package com.redxun.standardManager.core.manager;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.entity.AbstractExecutionCmd;
import com.redxun.core.script.GroovyScript;
import com.redxun.standardManager.core.dao.StandardManagementDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class JsStandardManScript implements GroovyScript {
    private Logger logger = LoggerFactory.getLogger(JsStandardManScript.class);
    @Autowired
    private StandardManagementDao standardManagementDao;
    //实际草案完成时间
    public void updateSjcawcsjScript(Map<String, Object> vars) {
        logger.info("标准化对接人审批完成");
        AbstractExecutionCmd cmd = (AbstractExecutionCmd)vars.get("cmd");
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        if (formDataJson.isEmpty()) {
            logger.warn("formData is blank");
            return;
        }
        formDataJson.put("sjcaTime", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        if (StringUtils.isBlank(formDataJson.getString("sjzqTime"))) {
            formDataJson.put("sjzqTime", null);
        }
        if (StringUtils.isBlank(formDataJson.getString("sjpsTime"))) {
            formDataJson.put("sjpsTime", null);
        }
        if (StringUtils.isBlank(formDataJson.getString("sjbpTime"))) {
            formDataJson.put("sjbpTime", null);
        }
        if (StringUtils.isBlank(formDataJson.getString("sjyyTime"))) {
            formDataJson.put("sjyyTime", null);
        }
        standardManagementDao.updateStandardDemand(formDataJson);
    }
    //实际征求意见完成日期
    public void updateSjzqyjwcrqScript(Map<String, Object> vars) {
        logger.info("标准评审审批完成");
        AbstractExecutionCmd cmd = (AbstractExecutionCmd)vars.get("cmd");
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        if (formDataJson.isEmpty()) {
            logger.warn("formData is blank");
            return;
        }
        formDataJson.put("sjzqTime", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        if (StringUtils.isBlank(formDataJson.getString("sjcaTime"))) {
            formDataJson.put("sjcaTime", null);
        }
        if (StringUtils.isBlank(formDataJson.getString("sjpsTime"))) {
            formDataJson.put("sjpsTime", null);
        }
        if (StringUtils.isBlank(formDataJson.getString("sjbpTime"))) {
            formDataJson.put("sjbpTime", null);
        }
        if (StringUtils.isBlank(formDataJson.getString("sjyyTime"))) {
            formDataJson.put("sjyyTime", null);
        }
        standardManagementDao.updateStandardDemand(formDataJson);
    }
    //实际评审稿完成日期
    public void updateSjpsgwcrqScript(Map<String, Object> vars) {
        logger.info("标准报批审批完成");
        AbstractExecutionCmd cmd = (AbstractExecutionCmd)vars.get("cmd");
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        if (formDataJson.isEmpty()) {
            logger.warn("formData is blank");
            return;
        }
        formDataJson.put("sjpsTime", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        if (StringUtils.isBlank(formDataJson.getString("sjcaTime"))) {
            formDataJson.put("sjcaTime", null);
        }
        if (StringUtils.isBlank(formDataJson.getString("sjzqTime"))) {
            formDataJson.put("sjzqTime", null);
        }
        if (StringUtils.isBlank(formDataJson.getString("sjbpTime"))) {
            formDataJson.put("sjbpTime", null);
        }
        if (StringUtils.isBlank(formDataJson.getString("sjyyTime"))) {
            formDataJson.put("sjyyTime", null);
        }
        standardManagementDao.updateStandardDemand(formDataJson);
    }
    //实际报批完成日期
    public void updateSjbpwcrqScript(Map<String, Object> vars) {
        logger.info("标准发布审批完成");
        AbstractExecutionCmd cmd = (AbstractExecutionCmd)vars.get("cmd");
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        if (formDataJson.isEmpty()) {
            logger.warn("formData is blank");
            return;
        }
        formDataJson.put("sjbpTime", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        if (StringUtils.isBlank(formDataJson.getString("sjcaTime"))) {
            formDataJson.put("sjcaTime", null);
        }
        if (StringUtils.isBlank(formDataJson.getString("sjzqTime"))) {
            formDataJson.put("sjzqTime", null);
        }
        if (StringUtils.isBlank(formDataJson.getString("sjpsTime"))) {
            formDataJson.put("sjpsTime", null);
        }
        if (StringUtils.isBlank(formDataJson.getString("sjyyTime"))) {
            formDataJson.put("sjyyTime", null);
        }
        standardManagementDao.updateStandardDemand(formDataJson);
    }
    //实际应用完成时间
    public void updateSjyywcsjScript(Map<String, Object> vars) {
        logger.info("标准对接人最后审批完成");
        AbstractExecutionCmd cmd = (AbstractExecutionCmd)vars.get("cmd");
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        if (formDataJson.isEmpty()) {
            logger.warn("formData is blank");
            return;
        }
        formDataJson.put("sjyyTime", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        if (StringUtils.isBlank(formDataJson.getString("sjcaTime"))) {
            formDataJson.put("sjcaTime", null);
        }
        if (StringUtils.isBlank(formDataJson.getString("sjzqTime"))) {
            formDataJson.put("sjzqTime", null);
        }
        if (StringUtils.isBlank(formDataJson.getString("sjpsTime"))) {
            formDataJson.put("sjpsTime", null);
        }
        if (StringUtils.isBlank(formDataJson.getString("sjbpTime"))) {
            formDataJson.put("sjbpTime", null);
        }
        standardManagementDao.updateStandardDemand(formDataJson);
    }
    //草案时间的状态
    public void updateZtca(Map<String, Object> vars) {
        AbstractExecutionCmd cmd = (AbstractExecutionCmd)vars.get("cmd");
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        if (formDataJson.isEmpty()) {
            logger.warn("formData is blank");
            return;
        }
        Map<String, Object> param =new HashMap<>();
        param.put("id",formDataJson.getString("id"));
        param.put("dqzt","ca");
        standardManagementDao.updateDqzt(param);
    }
    //征求意见时间的状态
    public void updateZtzqyj(Map<String, Object> vars) {
        AbstractExecutionCmd cmd = (AbstractExecutionCmd)vars.get("cmd");
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        if (formDataJson.isEmpty()) {
            logger.warn("formData is blank");
            return;
        }
        Map<String, Object> param =new HashMap<>();
        param.put("id",formDataJson.getString("id"));
        param.put("dqzt","zq");
        standardManagementDao.updateDqzt(param);
    }
    //评审稿时间的状态
    public void updateZtpsg(Map<String, Object> vars) {
        AbstractExecutionCmd cmd = (AbstractExecutionCmd)vars.get("cmd");
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        if (formDataJson.isEmpty()) {
            logger.warn("formData is blank");
            return;
        }
        Map<String, Object> param =new HashMap<>();
        param.put("id",formDataJson.getString("id"));
        param.put("dqzt","ps");
        standardManagementDao.updateDqzt(param);
    }
    //报批时间的状态
    public void updateZtbp(Map<String, Object> vars) {
        AbstractExecutionCmd cmd = (AbstractExecutionCmd)vars.get("cmd");
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        if (formDataJson.isEmpty()) {
            logger.warn("formData is blank");
            return;
        }
        Map<String, Object> param =new HashMap<>();
        param.put("id",formDataJson.getString("id"));
        param.put("dqzt","bp");
        standardManagementDao.updateDqzt(param);
    }
    //应用时间的状态
    public void updateZtyy(Map<String, Object> vars) {
        AbstractExecutionCmd cmd = (AbstractExecutionCmd)vars.get("cmd");
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        if (formDataJson.isEmpty()) {
            logger.warn("formData is blank");
            return;
        }
        Map<String, Object> param =new HashMap<>();
        param.put("id",formDataJson.getString("id"));
        param.put("dqzt","yy");
        standardManagementDao.updateDqzt(param);
    }
    //为空的状态
    public void updateZtNull(Map<String, Object> vars) {
        AbstractExecutionCmd cmd = (AbstractExecutionCmd)vars.get("cmd");
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        if (formDataJson.isEmpty()) {
            logger.warn("formData is blank");
            return;
        }
        Map<String, Object> param =new HashMap<>();
        param.put("id",formDataJson.getString("id"));
        param.put("dqzt","");
        standardManagementDao.updateDqzt(param);
    }
}
