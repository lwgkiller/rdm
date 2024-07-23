package com.redxun.rdmZhgl.core.service;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.activiti.engine.task.Task;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.handler.ProcessEndHandler;
import com.redxun.bpm.activiti.handler.ProcessStartAfterHandler;
import com.redxun.bpm.activiti.handler.ProcessStartPreHandler;
import com.redxun.bpm.activiti.handler.TaskPreHandler;
import com.redxun.bpm.core.entity.*;
import com.redxun.bpm.core.entity.config.ProcessConfig;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.bpm.core.manager.BpmSolutionManager;
import com.redxun.core.util.HttpClientUtil;
import com.redxun.rdmZhgl.core.dao.SaleFileApplyDao;
import com.redxun.rdmZhgl.core.dao.SqbgDao;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.sys.core.util.SysPropertiesUtil;

@Service
public class SqbgHandler
    implements ProcessStartPreHandler, ProcessStartAfterHandler, TaskPreHandler, ProcessEndHandler {
    private Logger logger = LoggerFactory.getLogger(SqbgHandler.class);
    @Autowired
    private SqbgService sqbgService;

    @Autowired
    private SaleFileApplyDao saleFileApplyDao;


    // 整个流程启动之前的处理(进行表单的创建或更新，草稿和提交都会调用)
    @Override
    public void processStartPreHandle(ProcessStartCmd processStartCmd) {
        String sqbgId = createOrUpdateSqbgByFormData(processStartCmd);
        if (StringUtils.isNotBlank(sqbgId)) {
            processStartCmd.setBusinessKey(sqbgId);
        }
    }

    // 第一个任务创建之后流程的后置处理器
    @Override
    public String processStartAfterHandle(ProcessConfig processConfig, ProcessStartCmd processStartCmd,
        BpmInst bpmInst) {
        String sqbgId = processStartCmd.getBusinessKey();
        return sqbgId;
    }

    // 任务审批之后的前置处理器
    @Override
    public void taskPreHandle(IExecutionCmd iExecutionCmd, Task task, String busKey) {
        ProcessNextCmd processNextCmd = (ProcessNextCmd)iExecutionCmd;
        createOrUpdateSqbgByFormData(processNextCmd);
    }

    // 流程成功结束之后的处理
    @Override
    public void endHandle(BpmInst bpmInst) {
        if ("SUCCESS_END".equalsIgnoreCase(bpmInst.getStatus())) {
            try {
                String sqbgId = bpmInst.getBusKey();
                // 通过http方式调用生成流程，解决在一个事务内创建新流程失败的问题
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json;charset=utf-8");
                String sqUrl =
                    SysPropertiesUtil.getGlobalProperty("rdmAddress") + "/Sqbg/startLc.do?sqbgId=" + sqbgId;
                JSONObject requestBody = new JSONObject();
                String rtnContent = HttpClientUtil.postJson(sqUrl, requestBody.toJSONString(), headers);
                JSONObject rtnContentObj=JSONObject.parseObject(rtnContent);
            } catch (Exception ex) {
                logger.error("流程启动失败", ex.getMessage());
            } finally {
                ContextUtil.clearCurLocal();
            }
        }
    }

    // 驳回场景cmd中没有表单数据
    private String createOrUpdateSqbgByFormData(AbstractExecutionCmd cmd) {
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
        if (StringUtils.isBlank(formDataJson.getString("sqbgId"))) {
            sqbgService.createSqbg(formDataJson);
        } else {
            sqbgService.updateSqbg(formDataJson);
        }
        return formDataJson.getString("sqbgId");
    }

    private void copySaveFile(JSONObject saleFile, String newapplyId) {
        String bfileName = saleFile.getString("fileName");
        String bfileId = saleFile.getString("fileId");
        String belongId = saleFile.getString("belongId");
        String bfileBasePath = WebAppUtil.getProperty("sqbgFilePathBase");
        String bsuffix = CommonFuns.toGetFileSuffix(bfileName);
        String relativeFilePath = "";
        if (StringUtils.isNotBlank(belongId)) {
            relativeFilePath = File.separator + belongId;
        }
        String brealFileName = bfileId + "." + bsuffix;
        String bfullFilePath = bfileBasePath + relativeFilePath + File.separator + brealFileName;
        File bfile = new File(bfullFilePath);
        String id = IdUtil.getId();
        String fileSize = saleFile.getString("fileSize");
        // 向下载目录中写入文件
        String filePath = SysPropertiesUtil.getGlobalProperty("saleFileUrl") + File.separator + newapplyId;
        File pathFile = new File(filePath);
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }
        String suffix = CommonFuns.toGetFileSuffix(bfileName);
        String fileFullPath = filePath + File.separator + id + "." + suffix;
        File file = new File(fileFullPath);
        try {
            FileCopyUtils.copy(FileUtils.readFileToByteArray(bfile), file);
            JSONObject fileInfo = new JSONObject();
            fileInfo.put("id", id);
            fileInfo.put("fileName", bfileName);
            fileInfo.put("fileSize", fileSize);
            fileInfo.put("applyId", newapplyId);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            fileInfo.put("fileModel", "sq");
            saleFileApplyDao.addSaleFileInfos(fileInfo);
        } catch (IOException e) {
            logger.error("文件加载失败", e.getMessage());
        }
    }
}
