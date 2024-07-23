package com.redxun.environment.core.service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.handler.ProcessEndHandler;
import com.redxun.bpm.activiti.handler.ProcessStartAfterHandler;
import com.redxun.bpm.activiti.handler.ProcessStartPreHandler;
import com.redxun.bpm.activiti.handler.TaskPreHandler;
import com.redxun.bpm.core.entity.*;
import com.redxun.bpm.core.entity.config.ProcessConfig;
import com.redxun.environment.core.dao.ClhbDao;
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import org.activiti.engine.task.Task;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ClhbHandler
    implements ProcessStartPreHandler, ProcessStartAfterHandler, TaskPreHandler, ProcessEndHandler {
    private Logger logger = LoggerFactory.getLogger(ClhbHandler.class);
    @Autowired
    private ClhbService clhbService;
    @Autowired
    private ClhbDao clhbDao;
    @Autowired
    private SendDDNoticeManager sendDDNoticeManager;

    // 整个流程启动之前的处理(进行表单的创建或更新，草稿和提交都会调用)
    @Override
    public void processStartPreHandle(ProcessStartCmd processStartCmd) {
        String wjId = createOrUpdateRjbgByFormData(processStartCmd);
        if (StringUtils.isNotBlank(wjId)) {
            processStartCmd.setBusinessKey(wjId);
        }
    }

    // 第一个任务创建之后流程的后置处理器
    @Override
    public String processStartAfterHandle(ProcessConfig processConfig, ProcessStartCmd processStartCmd,
        BpmInst bpmInst) {
        String wjId = processStartCmd.getBusinessKey();
        JSONObject param = new JSONObject();
        param.put("wjId", wjId);
        clhbDao.updateWj(param);
        return wjId;
    }

    // 任务审批之后的前置处理器
    @Override
    public void taskPreHandle(IExecutionCmd iExecutionCmd, Task task, String busKey) {
        ProcessNextCmd processNextCmd = (ProcessNextCmd)iExecutionCmd;
        createOrUpdateRjbgByFormData(processNextCmd);
    }

    // 流程成功结束之后的处理
    @Override
    public void endHandle(BpmInst bpmInst) {
        if ("SUCCESS_END".equalsIgnoreCase(bpmInst.getStatus())) {
            String wjId = bpmInst.getBusKey();
            JSONObject detail = clhbService.getWjDetail(wjId);
            StringBuilder userIdBuilder = new StringBuilder();
            JSONObject noticeObj = new JSONObject();
            List<JSONObject> userIdJsonList = clhbDao.queryMsgInfo();
            for(JSONObject userIdJson:userIdJsonList){
                userIdBuilder.append(userIdJson.getString("userId")).append(",");
            }
            String userIds=userIdBuilder.deleteCharAt(userIdBuilder.length()-1).toString();
            noticeObj.put("content",detail.getString("wjModel")+"整机环保信息已公开，请查看。");
            sendDDNoticeManager.sendNoticeForCommon(userIds, noticeObj);
            String num = clhbService.toGetClhbNumber();
            JSONObject param = new JSONObject();
            param.put("num", num);
            param.put("wjId", wjId);
            clhbDao.updateNumber(param);

            JSONObject idJson = new JSONObject();
            idJson.put("wjId", wjId);
            idJson.put("noteStatus", "有效");
            clhbDao.statusChange(idJson);
        }
    }

    // 驳回场景cmd中没有表单数据
    private String createOrUpdateRjbgByFormData(AbstractExecutionCmd cmd) {
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
        if (StringUtils.isBlank(formDataJson.getString("wjId"))) {
            clhbService.insertWj(formDataJson);
            if("old".equals(formDataJson.getString("type"))){
                String oldWjId = formDataJson.getString("oldWjId");
                Map<String, Object> param = new HashMap<>();
                param.put("wjId", oldWjId);
                List<JSONObject> cxFileList = clhbDao.queryWjFileList(param);
                for(JSONObject saleFile:cxFileList){
                    copySaveFile(saleFile,formDataJson.getString("wjId"));
                }
            }
        } else {
            clhbService.updateWj(formDataJson);
        }
        return formDataJson.getString("wjId");
    }

    private void copySaveFile(JSONObject saleFile, String newWjId) {
        String bfileName = saleFile.getString("fileName");
        String bfileId = saleFile.getString("fileId");
        String belongId = saleFile.getString("wjId");
        String fileType = saleFile.getString("fileType");
        String bfileBasePath = WebAppUtil.getProperty("wjFilePathBase");
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
        String filePath = bfileBasePath + File.separator + newWjId;
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
            fileInfo.put("fileId", id);
            fileInfo.put("fileName", bfileName);
            fileInfo.put("fileSize", fileSize);
            fileInfo.put("belongId", newWjId);
            fileInfo.put("fileType", fileType);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            clhbDao.addFileInfos(fileInfo);
        } catch (IOException e) {
            logger.error("文件加载失败", e.getMessage());
        }
    }
}
