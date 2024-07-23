package com.redxun.rdmZhgl.core.service;

import java.util.*;

import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmZhgl.core.dao.SaleFileOMAXiazaiApplyDao;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.serviceEngineering.core.dao.ManualFileDownloadApplyDao;
import com.redxun.serviceEngineering.core.service.ManualFileDownloadApplyScript;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.IExecutionCmd;
import com.redxun.bpm.core.entity.ProcessStartCmd;
import com.redxun.bpm.core.entity.TaskExecutor;
import com.redxun.core.script.GroovyScript;

@Service
public class SaleFileOMAXiazaiApplyScript implements GroovyScript {
    private Logger logger = LoggerFactory.getLogger(SaleFileOMAXiazaiApplyScript.class);
    @Autowired
    private SaleFileOMAXiazaiApplyDao saleFileOMAXiazaiApplyDao;
    @Autowired
    private CommonInfoDao commonInfoDao;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    /**
     * 获取申请单中售前文件流程的创建人领导
     *
     * @return
     */
    public Collection<TaskExecutor> getOMAFileResp() {
        List<TaskExecutor> users = new ArrayList<>();
        try {

            IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
            if (cmd == null) {
                return users;
            }
            String applyId = null;
            String jsonData = cmd.getJsonData();
            if (StringUtils.isNotBlank(jsonData)) {
                JSONObject jsonObject = JSONObject.parseObject(jsonData);
                applyId = jsonObject.getString("id");
            }


            if (StringUtils.isBlank(applyId)) {
                return users;
            }
            //// 获取服务工程部部门领导信息
            Map<String, Object> param = new HashMap<>();
            param.put("groupName", RdmConst.FWGCS_NAME);
            List<Map<String, String>> depRespMans = xcmgProjectOtherDao.getDepRespManById(param);
            if (depRespMans != null && !depRespMans.isEmpty()) {
                for (Map<String, String> depRespMan : depRespMans) {
                    users.add(new TaskExecutor(depRespMan.get("PARTY2_"), depRespMan.get("FULLNAME_")));
                }
            }
            // 文件部门负责人
            JSONObject params = new JSONObject();
            params.put("applyId", applyId);
            List<JSONObject> demandList = saleFileOMAXiazaiApplyDao.queryDemandList(params);
//数据库语句文件创建人领导改为获取售前文件审批流程的创建人领导
            Map<String, String> userId2Name = new HashMap<>();
            for (JSONObject oneDemand : demandList) {
                if (StringUtils.isNotBlank(oneDemand.getString("fileCreatorId"))) {
                    List<JSONObject> deptResps = commonInfoDao.queryDeptRespByUserId(oneDemand.getString("fileCreatorId"));
                    for (JSONObject deptResp : deptResps) {
                        userId2Name.put(deptResp.getString("USER_ID_"),deptResp.getString("FULLNAME_"));
                    }
                }
            }



            if (!userId2Name.isEmpty()) {
                for (Map.Entry<String, String> oneEntry : userId2Name.entrySet()) {
                    users.add(new TaskExecutor(oneEntry.getKey(), oneEntry.getValue()));
                }
            }
        } catch (Exception e) {
            logger.error("系统异常", e);
        }
        return users;
    }
}
