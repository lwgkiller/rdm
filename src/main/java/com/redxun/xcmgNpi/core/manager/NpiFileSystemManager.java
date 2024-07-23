package com.redxun.xcmgNpi.core.manager;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.xcmgNpi.core.dao.NpiFileDao;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.standardManager.core.dao.StandardSystemDao;
import com.redxun.standardManager.core.util.StandardConstant;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

import groovy.util.logging.Slf4j;

@Service
@Slf4j
public class NpiFileSystemManager {
    private static Logger logger = LoggerFactory.getLogger(NpiFileSystemManager.class);
    @Autowired
    private NpiFileDao npiFileDao;

    // 保存体系
    public void treeSave(JSONObject result, String changedDataStr) {
        JSONArray changedDataArray = JSONObject.parseArray(changedDataStr);
        if (changedDataArray == null || changedDataArray.isEmpty()) {
            logger.warn("数据为空");
            result.put("message", "数据为空！");
            return;
        }
        try {
            for (int i = 0; i < changedDataArray.size(); i++) {
                JSONObject oneObject = changedDataArray.getJSONObject(i);
                String state = oneObject.getString("_state");
                if ("added".equals(state)) {
                    // 新增
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", new Date());
                    npiFileDao.saveSystemNode(oneObject);
                } else if ("modified".equals(state)) {
                    // 修改
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", new Date());
                    npiFileDao.updateSystemNode(oneObject);
                } else if ("removed".equals(state)) {
                    // 删除
                    npiFileDao.delSystemNode(oneObject.getString("id"));
                }
            }
            result.put("message", "数据保存成功！");
        } catch (Exception e) {
            logger.error("Exception in treeSave", e);
            result.put("message", "系统异常，保存失败！");
        }
    }

    // 查询这些体系是否有关联的文件，返回个数
    public void queryNpiFileBySystemIds(JSONObject result, String systemIds) {
        JSONArray systemIdArr = JSONArray.parseArray(systemIds);
        if (systemIdArr == null || systemIdArr.size() == 0) {
            logger.warn("requestBody is blank");
            result.put("num", 0);
            return;
        }
        Set<String> systemIdSet = new HashSet<>();
        for (int i = 0; i < systemIdArr.size(); i++) {
            JSONObject object = systemIdArr.getJSONObject(i);
            if (StringUtils.isNotBlank(object.getString("id"))) {
                systemIdSet.add(object.getString("id"));
            }
        }
        if (systemIdSet.isEmpty()) {
            result.put("num", 0);
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("systemIds", systemIdSet);
        int standardNum = npiFileDao.queryNpiFileBySystemIds(params);
        result.put("num", standardNum);
    }


}
