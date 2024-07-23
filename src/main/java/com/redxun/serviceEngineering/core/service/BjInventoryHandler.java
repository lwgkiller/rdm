package com.redxun.serviceEngineering.core.service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.handler.ProcessEndHandler;
import com.redxun.bpm.activiti.handler.ProcessStartAfterHandler;
import com.redxun.bpm.activiti.handler.ProcessStartPreHandler;
import com.redxun.bpm.activiti.handler.TaskPreHandler;
import com.redxun.bpm.core.entity.*;
import com.redxun.bpm.core.entity.config.ProcessConfig;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.serviceEngineering.core.dao.BjInventoryDao;
import com.redxun.sys.core.manager.SysDicManager;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class BjInventoryHandler
    implements ProcessStartPreHandler, ProcessStartAfterHandler, TaskPreHandler, ProcessEndHandler {
    private Logger logger = LoggerFactory.getLogger(BjInventoryHandler.class);
    @Autowired
    private BjInventoryService bjInventoryService;
    @Autowired
    private BjInventoryDao bjInventoryDao;
    @Autowired
    private SysDicManager sysDicManager;

    // 整个流程启动之前的处理(进行表单的创建或更新，草稿和提交都会调用)
    @Override
    public void processStartPreHandle(ProcessStartCmd processStartCmd) {
        String id = createOrUpdateBjInventoryByFormData(processStartCmd);
        if (StringUtils.isNotBlank(id)) {
            processStartCmd.setBusinessKey(id);
        }
    }

    // 第一个任务创建之后流程的后置处理器
    @Override
    public String processStartAfterHandle(ProcessConfig processConfig, ProcessStartCmd processStartCmd,
        BpmInst bpmInst) {
        String id = processStartCmd.getBusinessKey();
        JSONObject param = new JSONObject();
        param.put("id", id);
        String processNum = toGetBjInventoryNumber();
        param.put("processNum", processNum);
        bjInventoryDao.updateBjInventoryNumber(param);
        return id;
    }

    // 任务审批之后的前置处理器
    @Override
    public void taskPreHandle(IExecutionCmd iExecutionCmd, Task task, String busKey) {
        ProcessNextCmd processNextCmd = (ProcessNextCmd)iExecutionCmd;
        createOrUpdateBjInventoryByFormData(processNextCmd);
    }

    // 流程成功结束之后的处理
    @Override
    public void endHandle(BpmInst bpmInst) {
        if ("SUCCESS_END".equalsIgnoreCase(bpmInst.getStatus())) {
            String id = bpmInst.getBusKey();
            //暂时保留自动生成excel
            /*JSONObject oneForm = bjInventoryService.getBjInventoryDetail(id);
            List<JSONObject> detailList = bjInventoryService.queryDetail(id);
            int index = 1;
            for (JSONObject oneDetail : detailList) {
                oneDetail.put("index", index);
                index++;
            }

            String title = "保养件清单明细";
            String excelName = title;
            String[] firstFieldNames = {"编号", "需要检查的项目", "需要检查的项目", "需要检查的项目", "对应保养操作的TOPIC号", "维护物料编码", "数量/容量",
                    "服务周期", "服务周期", "服务周期", "服务周期", "服务周期", "服务周期", "服务周期", "服务周期", "服务周期", "服务周期", "备注"};
            String[] secondFieldNames = {"编号", "部件名称", "关联物料编码", "详细项目", "对应保养操作的TOPIC号", "维护物料编码", "数量/容量",
                    "8/每日", "50", "100", "250", "500", "1000", "2000", "4000", "4500", "5000", "备注"};
            String[] fieldCodes = {"index", "partName", "linkMaterialCode", "detailContent", "topicNum", "whMaterialCode", "capacity",
                    "cycle8", "cycle50", "cycle100", "cycle250", "cycle500", "cycle1000", "cycle2000", "cycle4000", "cycle4500", "cycle5000",
                    "note"};
            XSSFWorkbook wbObj = ExcelUtils.exportExcelBjInventory(detailList, firstFieldNames, secondFieldNames, fieldCodes);
            String filePathBase =
                    sysDicManager.getBySysTreeKeyAndDicKey("serviceEngineeringUploadPosition", "partsAtlasArchive").getValue();

                FileOutputStream file = new FileOutputStream(filePathBase+ File.separator+oneForm.getString("materielCode")+"_"+oneForm.getString("designModelName")+".xlsx");
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }*/
//            Map<String, Object> roleParams = new HashMap<>();
//            roleParams.put("groupName", "原理图需求申请专员");
//            roleParams.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
//            List<Map<String, String>> userInfos = xcmgProjectOtherDao.queryUserByGroupName(roleParams);
//            List<Map<String, String>> depRespMans = rdmZhglUtil.queryFwgcs();
//            if (depRespMans != null && !depRespMans.isEmpty()) {
//                for (Map<String, String> depRespMan : depRespMans) {
//                    JSONObject noticeObj = new JSONObject();
//                    StringBuilder stringBuilder = new StringBuilder("【原理图需求申请通知】：");
//                    stringBuilder.append("单据编号：").append(noticeNo).append("，设计型号：").append(modelName).append("，整机编号：")
//                            .append(machineNum).append("，原理图需求申请流程已通过");
//                    noticeObj.put("content", stringBuilder.toString());
//                    sendDDNoticeManager.sendNoticeForCommon(depRespMan.get("PARTY2_"), noticeObj);
//                }
//            }
//            if (userInfos.size() > 0) {
//                for (Map<String, String> userMap : userInfos) {
//                    JSONObject noticeObj = new JSONObject();
//                    StringBuilder stringBuilder = new StringBuilder("【原理图需求申请通知】：");
//                    stringBuilder.append("单据编号：").append(noticeNo).append("，设计型号：").append(modelName).append("，整机编号：")
//                            .append(machineNum).append("，原理图需求申请流程已通过");
//                    noticeObj.put("content", stringBuilder.toString());
//                    sendDDNoticeManager.sendNoticeForCommon(userMap.get("USER_ID_"), noticeObj);
//                } ;
//            }
        }
    }

    // 驳回场景cmd中没有表单数据
    private String createOrUpdateBjInventoryByFormData(AbstractExecutionCmd cmd) {
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
        if (StringUtils.isBlank(formDataJson.getString("id"))) {
            bjInventoryService.createBjInventory(formDataJson);
        } else {
            bjInventoryService.updateBjInventory(formDataJson);
        }
        return formDataJson.getString("id");
    }

    public String toGetBjInventoryNumber() {
        String num = null;
        String nowYearStart = DateFormatUtil.getNowByString("yyyyMMdd");
        Map<String, Object> param = new HashMap<>();
        param.put("applyTimeStart", nowYearStart);
        JSONObject maxClhb = bjInventoryDao.queryMaxBjInventoryNum(param);
        int existNumber = 0;
        if (maxClhb != null) {
            existNumber = Integer.parseInt(maxClhb.getString("processNum").substring(15));
        }
        int thisNumber = existNumber + 1;
        String newDate = DateFormatUtil.getNowByString("yyyyMMdd");
        if (thisNumber < 10) {
            num = "BJTJQD" + newDate + "-00" + thisNumber;
        } else if (thisNumber < 100 && thisNumber >= 10) {
            num = "BJTJQD" + newDate  +"-0"+ thisNumber;
        }else if (thisNumber < 1000 && thisNumber >= 100) {
            num = "BJTJQD" + newDate  +"-"+ thisNumber;
        }
        return num;
    }

}
