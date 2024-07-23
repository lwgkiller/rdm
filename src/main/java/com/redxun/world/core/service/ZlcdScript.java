package com.redxun.world.core.service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.entity.AbstractExecutionCmd;
import com.redxun.bpm.core.entity.TaskExecutor;
import com.redxun.core.script.GroovyScript;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.world.core.dao.ZlcdDao;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

// 项目流程节点的触发事件
@Service
public class ZlcdScript implements GroovyScript {
    private Logger logger = LoggerFactory.getLogger(ZlcdScript.class);

    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private ZlcdDao zlcdDao;


    public boolean threeDOrtowD(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        Set<String> ident = new HashSet();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String zlcdId = formDataJson.getString("zlcdId");
        List<JSONObject> zlcdCnList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("zlcdId", zlcdId);
        zlcdCnList = zlcdDao.queryZlcdDetail(param);
        for (JSONObject zlcd : zlcdCnList) {
            ident.add(zlcd.getString("fileType"));
        }
        if (ident.contains("3D") || ident.contains("2D")) {
            return true;
        }
        return false;
    }

    public boolean pdf(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        Set<String> ident = new HashSet();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String zlcdId = formDataJson.getString("zlcdId");
        List<JSONObject> zlcdCnList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("zlcdId", zlcdId);
        zlcdCnList = zlcdDao.queryZlcdDetail(param);
        for (JSONObject zlcd : zlcdCnList) {
            ident.add(zlcd.getString("fileType"));
        }
        if (ident.contains("Pdf") && !(ident.contains("3D") || ident.contains("2D"))) {
            return true;
        }
        return false;
    }

    public boolean excel(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        Set<String> ident = new HashSet();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String zlcdId = formDataJson.getString("zlcdId");
        List<JSONObject> zlcdCnList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("zlcdId", zlcdId);
        zlcdCnList = zlcdDao.queryZlcdDetail(param);
        for (JSONObject zlcd : zlcdCnList) {
            ident.add(zlcd.getString("fileType"));
        }
        if (!ident.contains("Pdf") && !ident.contains("3D") && !ident.contains("2D")) {
            return true;
        }
        return false;
    }

    public boolean towD(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        Set<String> ident = new HashSet();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String zlcdId = formDataJson.getString("zlcdId");
        List<JSONObject> zlcdCnList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("zlcdId", zlcdId);
        zlcdCnList = zlcdDao.queryZlcdDetail(param);
        for (JSONObject zlcd : zlcdCnList) {
            ident.add(zlcd.getString("fileType"));
        }
        if (!ident.contains("3D")) {
            return true;
        }

        return false;
    }

    public boolean threeD(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        Set<String> ident = new HashSet();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String zlcdId = formDataJson.getString("zlcdId");
        List<JSONObject> zlcdCnList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("zlcdId", zlcdId);
        zlcdCnList = zlcdDao.queryZlcdDetail(param);
        for (JSONObject zlcd : zlcdCnList) {
            ident.add(zlcd.getString("fileType"));
        }
        if (ident.contains("3D")) {
            return true;
        }
        return false;
    }
}
