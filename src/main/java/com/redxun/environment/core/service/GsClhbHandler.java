package com.redxun.environment.core.service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.handler.*;
import com.redxun.bpm.core.entity.*;
import com.redxun.bpm.core.entity.config.ProcessConfig;
import com.redxun.environment.core.dao.GsClhbDao;
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
public class GsClhbHandler
    implements ProcessStartPreHandler, ProcessStartAfterHandler, TaskPreHandler, ProcessEndHandler, TaskAfterHandler {
    private Logger logger = LoggerFactory.getLogger(GsClhbHandler.class);
    @Autowired
    private GsClhbService gsClhbService;
    @Autowired
    private GsClhbDao gsClhbDao;
    @Autowired
    private SendDDNoticeManager sendDDNoticeManager;

    // 整个流程启动之前的处理(进行表单的创建或更新，草稿和提交都会调用)
    @Override
    public void processStartPreHandle(ProcessStartCmd processStartCmd) {
        String cxId = createOrUpdateRjbgByFormData(processStartCmd);
        if (StringUtils.isNotBlank(cxId)) {
            processStartCmd.setBusinessKey(cxId);
        }
    }

    // 第一个任务创建之后流程的后置处理器
    @Override
    public String processStartAfterHandle(ProcessConfig processConfig, ProcessStartCmd processStartCmd,
        BpmInst bpmInst) {
        String cxId = processStartCmd.getBusinessKey();
//        String formData = processStartCmd.getJsonData();
//        if (StringUtils.isBlank(formData)) {
//            logger.warn("formData is blank");
//        }
//        JSONObject formDataJson = JSONObject.parseObject(formData);
//        if (formDataJson.isEmpty()) {
//            logger.warn("formData is blank");
//        }
//        String jxxzmc = "";
//        Map<String, Object> param = new HashMap<>();
//        String cyjxs = formDataJson.getString("cyjxs");
//        String fldm = formDataJson.getString("fldm");
//        param.put("cyjxs", cyjxs);
//        param.put("fldm", fldm);
//        param.put("countnumInfo", "yes");
//        List<JSONObject> fdjList = gsClhbDao.countFdjNumber(param);
//        //存在分类代码相同且发动机相同
//        if(fdjList!=null&&!fdjList.isEmpty()){
//            jxxzmc = fdjList.get(0).getString("jxxzmc");
//        }else {
//            //不存在相同则查询此分类代码下的最大值
//            List<JSONObject> maxNumberList = gsClhbDao.queryMaxFdjNumber(param);
//            if (maxNumberList == null || maxNumberList.isEmpty()) {
//                jxxzmc = "XG4" + fldm + "0001";
//            }else {
//                int existNumber = Integer.parseInt(maxNumberList.get(0).getString("jxxzmc").substring(6));
//                int thisNumber = existNumber + 1;
//                if (thisNumber < 10) {
//                    jxxzmc= "XG4" + fldm + "000" + thisNumber;
//                }else if(thisNumber < 100&&thisNumber > 10) {
//                    jxxzmc= "XG4" + fldm + "00" +thisNumber;
//                }else if(thisNumber < 1000&&thisNumber > 100) {
//                    jxxzmc= "XG4" + fldm + "0" +thisNumber;
//                }
//            }
//        }
//        param.clear();
//        param.put("cxId",cxId);
//        param.put("jxxzmc",jxxzmc);
//        gsClhbDao.updateFdjNum(param);
        return cxId;
    }

    // 任务审批之后的前置处理器
    @Override
    public void taskPreHandle(IExecutionCmd iExecutionCmd, Task task, String busKey) {
        ProcessNextCmd processNextCmd = (ProcessNextCmd)iExecutionCmd;
        createOrUpdateRjbgByFormData(processNextCmd);
    }
    // 任务处理之后的后置处理器
    @Override
    public void taskAfterHandle(IExecutionCmd iExecutionCmd, String nodeId, String busKey) {
        ProcessNextCmd processNextCmd = (ProcessNextCmd)iExecutionCmd;
        String formData = processNextCmd.getJsonData();
        if (StringUtils.isBlank(formData)) {
            logger.warn("formData is blank");
        }
        JSONObject formDataJson = JSONObject.parseObject(formData);
        if (formDataJson.isEmpty()) {
            logger.warn("formData is blank");
        }
        String cxId = formDataJson.getString("cxId");
        String jxxzmc = "";
        Map<String, Object> param = new HashMap<>();
        String cyjxs = formDataJson.getString("cyjxs");
        String fldm = formDataJson.getString("fldm");
        param.put("cxId", cxId);
        param.put("cyjxs", cyjxs);
        param.put("fldm", fldm);
        param.put("countnumInfo", "yes");
        List<JSONObject> fdjList = gsClhbDao.countFdjNumber(param);
        if(fdjList!=null&&!fdjList.isEmpty()){
            jxxzmc = fdjList.get(0).getString("jxxzmc");
        }else {
            List<JSONObject> maxNumberList = gsClhbDao.queryMaxFdjNumber(param);
            if (maxNumberList == null || maxNumberList.isEmpty()) {
                jxxzmc = "XG4" + fldm + "0001";
            }else {
                int existNumber = Integer.parseInt(maxNumberList.get(0).getString("jxxzmc").substring(6));
                int thisNumber = existNumber + 1;
                if (thisNumber < 10) {
                    jxxzmc= "XG4" + fldm + "000" + thisNumber;
                }else if(thisNumber < 100&&thisNumber >= 10) {
                    jxxzmc= "XG4" + fldm + "00" +thisNumber;
                }else if(thisNumber < 1000&&thisNumber >= 100) {
                    jxxzmc= "XG4" + fldm + "0" +thisNumber;
                }
            }
        }
        param.clear();
        param.put("cxId",cxId);
        param.put("jxxzmc",jxxzmc);
        gsClhbDao.updateFdjNum(param);
    }


    // 流程成功结束之后的处理
    @Override
    public void endHandle(BpmInst bpmInst) {
        if ("SUCCESS_END".equalsIgnoreCase(bpmInst.getStatus())) {
            String cxId = bpmInst.getBusKey();
            JSONObject detail = gsClhbService.getCxDetail(cxId);
            StringBuilder userIdBuilder = new StringBuilder();
            JSONObject noticeObj = new JSONObject();
            List<JSONObject> userIdJsonList = gsClhbDao.queryMsgInfo();
            for(JSONObject userIdJson:userIdJsonList){
                userIdBuilder.append(userIdJson.getString("userId")).append(",");
            }
            String userIds=userIdBuilder.deleteCharAt(userIdBuilder.length()-1).toString();
            noticeObj.put("content",detail.getString("cpjxxh")+"整机环保信息已公开，请查看。");
            sendDDNoticeManager.sendNoticeForCommon(userIds, noticeObj);
            String num = gsClhbService.toGetClhbNumber();
            JSONObject param = new JSONObject();
            param.put("num", num);
            param.put("cxId", cxId);
            gsClhbDao.updateNumber(param);

            JSONObject idJson = new JSONObject();
            idJson.put("cxId", cxId);
            idJson.put("noteStatus", "有效");
            gsClhbDao.statusChange(idJson);
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
        if (StringUtils.isBlank(formDataJson.getString("cxId"))) {
            gsClhbService.insertCx(formDataJson);
            if("old".equals(formDataJson.getString("type"))){
                String oldCxId = formDataJson.getString("oldCxId");
                Map<String, Object> param = new HashMap<>();
                param.put("cxId", oldCxId);
                List<JSONObject> cxFileList = gsClhbDao.queryCxFileList(param);
                for(JSONObject saleFile:cxFileList){
                    copySaveFile(saleFile,formDataJson.getString("cxId"));
                }
            }
        } else {
            gsClhbService.updateCx(formDataJson);
        }
        return formDataJson.getString("cxId");
    }

    private void copySaveFile(JSONObject saleFile, String newCxId) {
        String bfileName = saleFile.getString("fileName");
        String bfileId = saleFile.getString("fileId");
        String belongId = saleFile.getString("cxId");
        String upNode = saleFile.getString("upNode");
        String fileType = saleFile.getString("fileType");
        String bfileBasePath = WebAppUtil.getProperty("gsclhbFilePathBase");
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
        String filePath = bfileBasePath + File.separator + newCxId;
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
            fileInfo.put("belongId", newCxId);
            fileInfo.put("fileType", fileType);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            fileInfo.put("upNode", upNode);
            gsClhbDao.addFileInfos(fileInfo);
        } catch (IOException e) {
            logger.error("文件加载失败", e.getMessage());
        }
    }
}
