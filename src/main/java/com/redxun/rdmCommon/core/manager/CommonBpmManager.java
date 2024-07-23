package com.redxun.rdmCommon.core.manager;

import java.util.*;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.entity.ActivityNode;
import com.redxun.bpm.activiti.service.ActRepService;
import com.redxun.bpm.core.manager.BpmDefManager;
import com.redxun.rdmCommon.core.dao.CommonBpmDao;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;

import groovy.util.logging.Slf4j;

/**
 * @author zhangzhen
 */
@Service
@Slf4j
public class CommonBpmManager {
    private static Logger logger = LoggerFactory.getLogger(CommonBpmManager.class);
    @Resource
    private CommonBpmDao commonBpmDao;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private BpmDefManager bpmDefManager;
    @Autowired
    private ActRepService actRepService;

    public List<Map<String, String>> queryNodeVarsByParam(String nodeId, String solKey, String solId) {
        List<Map<String, String>> result = new ArrayList<>();
        if (StringUtils.isBlank(nodeId)) {
            return result;
        }
        if (StringUtils.isBlank(solId) && StringUtils.isBlank(solKey)) {
            return result;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("SCOPE_", nodeId);
        if (StringUtils.isNotBlank(solKey)) {
            params.put("SOL_KEY_", solKey);
        }
        if (StringUtils.isNotBlank(solId)) {
            params.put("SOL_ID_", solId);
        }
        List<Map<String, String>> nodeVars = commonBpmDao.queryNodeVarsByParam(params);
        return nodeVars;
    }

    /**
     * 根据流程实例查找任务（可能多个，只返回当前任务名称），并返回所有的任务处理人
     *
     * @param instId
     * @param taskUsers
     * @return
     */
    public JSONObject queryTaskAndUsersByInstId(String instId, List<JSONObject> taskUsers) {
        if (StringUtils.isBlank(instId)) {
            return null;
        }
        Map<String, Object> param = new HashMap<>();
        param.put("INST_ID_", instId);
        List<JSONObject> taskList = commonBpmDao.queryTaskListByInstId(param);
        if (taskList == null || taskList.isEmpty()) {
            return null;
        }

        Set<String> taskIds = new HashSet<>();
        for (JSONObject oneTask : taskList) {
            taskIds.add(oneTask.getString("ID_"));
        }
        // 通过任务id找处理人
        Map<String, Object> queryTaskExecutors = new HashMap<>();
        queryTaskExecutors.put("taskIds", new ArrayList<>(taskIds));
        List<Map<String, String>> task2Executors = xcmgProjectOtherDao.queryTaskExecutorsByTaskIds(queryTaskExecutors);
        if (task2Executors == null || task2Executors.isEmpty()) {
            return null;
        }
        Set<String> userIds = new HashSet<>();
        for (Map<String, String> oneUser : task2Executors) {
            if (!userIds.contains(oneUser.get("currentProcessUserId"))) {
                userIds.add(oneUser.get("currentProcessUserId"));

                JSONObject oneUserObj = new JSONObject();
                oneUserObj.put("userName", oneUser.get("currentProcessUser"));
                oneUserObj.put("userCertNo", oneUser.get("userCertNo"));
                oneUserObj.put("userId", oneUser.get("currentProcessUserId"));
                taskUsers.add(oneUserObj);
            }
        }

        JSONObject taskObj = new JSONObject();
        taskObj.put("taskName", taskList.get(0).getString("NAME_"));
        taskObj.put("taskDesc", taskList.get(0).getString("DESCRIPTION_"));
        return taskObj;
    }

    /**
     * 获取某个流程方案指定类型的节点列表
     *
     * @param solKey
     *            流程方案key
     * @param whatTypeToGet
     *            获取的节点类型
     * @return
     */
    public List<JSONObject> getNodeSetListWithName(String solKey, String... whatTypeToGet) {
        List<JSONObject> nodeSetListWithName = new ArrayList<>();
        Collection<ActivityNode> actNodes = actRepService
            .getActNodes(bpmDefManager.getLatestBpmByKey(solKey, ContextUtil.getTenant().getTenantId()).getActDefId());
        for (ActivityNode actNode : actNodes) {
            for (String type : whatTypeToGet) {
                if (actNode.getType().equalsIgnoreCase(type)) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("key", actNode.getActivityId());
                    jsonObject.put("value", actNode.getName());
                    nodeSetListWithName.add(jsonObject);
                }
            }
        }
        return nodeSetListWithName;
    }

    public JSONObject querySolutionByBusKey(String buskey) {
        JSONObject param = new JSONObject();
        param.put("BUS_KEY_", buskey);
        List<JSONObject> solList = commonBpmDao.querySolByBusKey(param);
        if (solList != null && !solList.isEmpty()) {
            return solList.get(0);
        }
        return null;
    }
}
