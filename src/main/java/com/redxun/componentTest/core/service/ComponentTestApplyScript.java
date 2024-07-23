package com.redxun.componentTest.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.IExecutionCmd;
import com.redxun.bpm.core.entity.ProcessNextCmd;
import com.redxun.bpm.core.entity.ProcessStartCmd;
import com.redxun.bpm.core.entity.TaskExecutor;
import com.redxun.core.script.GroovyScript;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.org.api.model.IUser;
import com.redxun.org.api.service.UserService;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.saweb.util.IdUtil;
import com.redxun.serviceEngineering.core.service.MixedinputService;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.sys.org.entity.OsGroup;
import com.redxun.sys.org.entity.OsRelType;
import com.redxun.sys.org.entity.OsUser;
import com.redxun.sys.org.manager.OsGroupManager;
import com.redxun.sys.org.manager.OsUserManager;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ComponentTestApplyScript implements GroovyScript {
    private Logger logger = LoggerFactory.getLogger(ComponentTestApplyScript.class);
    @Autowired
    private ComponentTestApplyService componentTestApplyService;
    @Autowired
    private SysDicManager sysDicManager;
    @Autowired
    private UserService userService;
    @Autowired
    private OsUserManager osUserManager;

    //..
    public void taskCreateScript(Map<String, Object> vars) throws Exception {
        try {
            JSONObject jsonDataObject = ProcessHandleHelper.getProcessCmd().getJsonDataObject();
            String activitiId = vars.get("activityId").toString();
            jsonDataObject.put("businessStatus", activitiId);
            IExecutionCmd processCmd = ProcessHandleHelper.getProcessCmd();
            if (processCmd instanceof ProcessStartCmd) {
                ProcessStartCmd processStartCmd = (ProcessStartCmd) ProcessHandleHelper.getProcessCmd();
                processStartCmd.setJsonData(jsonDataObject.toJSONString());
            } else if (processCmd instanceof ProcessNextCmd) {
                ProcessNextCmd processNextCmd = (ProcessNextCmd) ProcessHandleHelper.getProcessCmd();
                processNextCmd.setJsonData(jsonDataObject.toJSONString());
            }
            componentTestApplyService.saveBusiness(jsonDataObject);
        } catch (Exception e) {
            ProcessHandleHelper.addErrorMsg(e.getMessage());
            throw e;
        }
    }

    //A4通过后，写testProgress为0.01,生成编号,写testStatus为进行中，写testItemsJson.status_item为进行中
    //D1通过后，写actualTestMonth
    //D5通过后，写testProgress为0.25，
    //E通过后，判断是不是testItems的status_item都为已完成，不是则抛出运行时错误
    //F5通过后，写testProgress为1，写testStatus为已完成，写completeTestMonth，
    public void taskEndScript(Map<String, Object> vars) throws Exception {
        try {
            JSONObject jsonDataObject = ProcessHandleHelper.getProcessCmd().getJsonDataObject();
            String activitiId = vars.get("activityId").toString();
            if (activitiId.equalsIgnoreCase("A4")) {
                jsonDataObject.put("testProgress", "0.01");
                if (!jsonDataObject.containsKey("testNo") || StringUtil.isEmpty(jsonDataObject.getString("testNo"))) {
                    jsonDataObject.put("testNo", componentTestApplyService.getSysSeq(jsonDataObject));
                }
                jsonDataObject.put("testStatus", "进行中");
                if (jsonDataObject.containsKey("testItems")
                        && StringUtil.isNotEmpty(jsonDataObject.getString("testItems"))) {
                    JSONArray jsonArrayItemData = jsonDataObject.getJSONArray("testItems");
                    for (Object itemDataObject : jsonArrayItemData) {
                        JSONObject itemDataJson = (JSONObject) itemDataObject;
                        itemDataJson.put("status_item", "进行中");
                    }
                    jsonDataObject.put("testItemsJson", jsonArrayItemData.toString());
                }
                if (jsonDataObject.containsKey("testItemsNonStandard")
                        && StringUtil.isNotEmpty(jsonDataObject.getString("testItemsNonStandard"))) {
                    JSONArray jsonArrayItemData = jsonDataObject.getJSONArray("testItemsNonStandard");
                    for (Object itemDataObject : jsonArrayItemData) {
                        JSONObject itemDataJson = (JSONObject) itemDataObject;
                        itemDataJson.put("status_item", "进行中");
                    }
                    jsonDataObject.put("testItemsNonStandardJson", jsonArrayItemData.toString());
                }
            } else if (activitiId.equalsIgnoreCase("D1")) {
                jsonDataObject.put("actualTestMonth", DateUtil.formatDate(new Date(), DateUtil.DATE_FORMAT_YMD));
            } else if (activitiId.equalsIgnoreCase("D5")) {
                jsonDataObject.put("testProgress", "0.25");
//                jsonDataObject.put("testStatus", "进行中");
//                if (jsonDataObject.containsKey("testItems")
//                        && StringUtil.isNotEmpty(jsonDataObject.getString("testItems"))) {
//                    JSONArray jsonArrayItemData = jsonDataObject.getJSONArray("testItems");
//                    for (Object itemDataObject : jsonArrayItemData) {
//                        JSONObject itemDataJson = (JSONObject) itemDataObject;
//                        itemDataJson.put("status_item", "进行中");
//                    }
//                    jsonDataObject.put("testItemsJson", jsonArrayItemData.toString());
//                }
//                if (jsonDataObject.containsKey("testItemsNonStandard")
//                        && StringUtil.isNotEmpty(jsonDataObject.getString("testItemsNonStandard"))) {
//                    JSONArray jsonArrayItemData = jsonDataObject.getJSONArray("testItemsNonStandard");
//                    for (Object itemDataObject : jsonArrayItemData) {
//                        JSONObject itemDataJson = (JSONObject) itemDataObject;
//                        itemDataJson.put("status_item", "进行中");
//                    }
//                    jsonDataObject.put("testItemsNonStandardJson", jsonArrayItemData.toString());
//                }
            } else if (activitiId.equalsIgnoreCase("E")) {
                if (jsonDataObject.containsKey("testItems")
                        && StringUtil.isNotEmpty(jsonDataObject.getString("testItems"))) {
                    JSONArray jsonArrayItemData = jsonDataObject.getJSONArray("testItems");
                    for (Object itemDataObject : jsonArrayItemData) {
                        JSONObject itemDataJson = (JSONObject) itemDataObject;
                        if (!itemDataJson.getString("status_item").equalsIgnoreCase("已完成")) {
                            throw new RuntimeException("测试项目必须都为‘已完成’状态才能进行下一步");
                        }
                    }
                }
                if (jsonDataObject.containsKey("testItemsNonStandard")
                        && StringUtil.isNotEmpty(jsonDataObject.getString("testItemsNonStandard"))) {
                    JSONArray jsonArrayItemData = jsonDataObject.getJSONArray("testItemsNonStandard");
                    for (Object itemDataObject : jsonArrayItemData) {
                        JSONObject itemDataJson = (JSONObject) itemDataObject;
                        if (!itemDataJson.getString("status_item").equalsIgnoreCase("已完成")) {
                            throw new RuntimeException("测试项目必须都为‘已完成’状态才能进行下一步");
                        }
                    }
                }
            } else if (activitiId.equalsIgnoreCase("F5")) {
                jsonDataObject.put("testProgress", "1");
                jsonDataObject.put("testStatus", "已完成");
                jsonDataObject.put("completeTestMonth", DateUtil.formatDate(new Date(), DateUtil.DATE_FORMAT_YMD));
                jsonDataObject.put("businessStatus", "Z");
            }
            componentTestApplyService.saveBusiness(jsonDataObject);
        } catch (Exception e) {
            ProcessHandleHelper.addErrorMsg(e.getMessage());
            throw e;
        }
    }

    //D2-D5驳回后，通过componentTestStageStartNode找D对应的首节点，如果目标节点不匹配则抛出运行时错误
    //F2-F5驳回后，通过componentTestStageStartNode找F对应的首节点，如果目标节点不匹配则抛出运行时错误
    public void taskEndScript2(Map<String, Object> vars) throws Exception {
        try {
            ProcessNextCmd processNextCmd = (ProcessNextCmd) ProcessHandleHelper.getProcessCmd();
            String destNodeId = processNextCmd.getDestNodeId();//取此次驳回的指定节点
            String nodeSetJsonString =
                    sysDicManager.getBySysTreeKeyAndDicKey("LCJDFZ", "componentTestStageStartNode").getValue();
            JSONObject nodeSetJson = JSONObject.parseObject(nodeSetJsonString);//取各阶段的目标节点限制
            String activitiId = vars.get("activityId").toString();
            if (activitiId.equalsIgnoreCase("D2") ||
                    activitiId.equalsIgnoreCase("D3") ||
                    activitiId.equalsIgnoreCase("D4") ||
                    activitiId.equalsIgnoreCase("D5")) {
                if (!destNodeId.equalsIgnoreCase(nodeSetJson.getString("D"))) {//指定节点与目标节点不一致
                    throw new RuntimeException("此阶段只能驳回至‘合同方案上传’节点");
                }
            } else if (activitiId.equalsIgnoreCase("F2") ||
                    activitiId.equalsIgnoreCase("F3") ||
                    activitiId.equalsIgnoreCase("F4") ||
                    activitiId.equalsIgnoreCase("F5")) {
                if (!destNodeId.equalsIgnoreCase(nodeSetJson.getString("F"))) {//指定节点与目标节点不一致
                    throw new RuntimeException("此阶段只能驳回至‘报告上传’节点");
                }
            }
        } catch (Exception e) {
            ProcessHandleHelper.addErrorMsg(e.getMessage());
            throw e;
        }
    }

    //..
    public Collection<TaskExecutor> getTestMajorLeader() {
        return Stream.of(new TaskExecutor(ProcessHandleHelper.getProcessCmd().getJsonDataObject().getString("testMajorLeaderId"),
                ProcessHandleHelper.getProcessCmd().getJsonDataObject().getString("testMajorLeader")))
                .collect(Collectors.toList());
    }

    //..
    public Collection<TaskExecutor> getTestLeader() {
        return Stream.of(new TaskExecutor(ProcessHandleHelper.getProcessCmd().getJsonDataObject().getString("testLeaderId"),
                ProcessHandleHelper.getProcessCmd().getJsonDataObject().getString("testLeader")))
                .collect(Collectors.toList());
    }

    //..
    public Collection<TaskExecutor> getRelDepLeader() {
        return osUserManager.getByGroupIdRelTypeId(ProcessHandleHelper.getProcessCmd().getJsonDataObject().getString("relDepId"),
                OsRelType.REL_CAT_GROUP_USER_LEADER_ID).stream()
                .map(osUser -> {
                    TaskExecutor taskExecutor = new TaskExecutor(osUser.getUserId(), osUser.getFullname());
                    return taskExecutor;
                }).collect(Collectors.toList());
    }
}
