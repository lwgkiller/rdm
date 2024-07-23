package com.redxun.rdmCommon.core.manager;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.redxun.core.util.StringUtil;
import com.redxun.rdmZhgl.core.dao.JstbDao;
import com.redxun.rdmZhgl.core.dao.ProductDao;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.serviceEngineering.core.dao.MaintenanceManualfileDao;
import com.redxun.standardManager.core.dao.StandardFileInfosDao;
import com.redxun.standardManager.core.manager.StandardFileInfosManager;
import com.redxun.sys.core.entity.SysDic;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectDao;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.entity.BpmTask;
import com.redxun.bpm.core.manager.BpmTaskManager;
import com.redxun.componentTest.core.dao.ComponentTestKanbanDao;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.query.IWhereClause;
import com.redxun.core.query.Page;
import com.redxun.core.query.QueryFilter;
import com.redxun.core.query.QueryParam;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.HttpClientUtil;
import com.redxun.embedsoft.core.dao.*;
import com.redxun.keyDesign.core.dao.JsbzDao;
import com.redxun.materielextend.core.dao.MaterielTypeFDao;
import com.redxun.meeting.core.dao.HyglDao;
import com.redxun.rdmCommon.core.dao.RdmHomeDao;
import com.redxun.rdmCommon.core.util.RdmCommonUtil;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmZhgl.core.dao.BbsNoticeDao;
import com.redxun.rdmZhgl.core.service.JstbService;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.QueryFilterBuilder;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.serviceEngineering.core.service.StandardvalueShipmentnotmadeService;
import com.redxun.standardManager.core.manager.StandardMessageManager;
import com.redxun.standardManager.core.util.StandardConstant;
import com.redxun.standardManager.core.util.StandardManagerUtil;
import com.redxun.sys.core.util.SysPropertiesUtil;
import com.redxun.sys.org.entity.OsUser;
import com.redxun.sys.org.manager.OsUserManager;
import com.redxun.xcmgFinance.core.manager.OAFinanceManager;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectMsgManager;
import com.redxun.xcmgTdm.core.service.TdmRequestService;

import groovy.util.logging.Slf4j;

/**
 * @author zhangzhen
 */
@Service
@Slf4j
public class RdmHomeManager {
    private static Logger logger = LoggerFactory.getLogger(RdmHomeManager.class);
    @Resource
    private RdmHomeDao rdmHomeDao;
    @Resource
    private BpmTaskManager bpmTaskManager;
    @Autowired
    private StandardMessageManager standardMessageManager;
    @Autowired
    private CommonInfoManager commonInfoManager;
    @Resource
    private XcmgProjectMsgManager xcmgProjectMsgManager;
    @Autowired
    private JstbService jstbService;
    @Autowired
    private SendDDNoticeManager sendDDNoticeManager;
    @Autowired
    private JsbzDao jsbzDao;
    @Autowired
    private ComponentTestKanbanDao componentTestKanbanDao;
    @Autowired
    private StandardvalueShipmentnotmadeService standardvalueShipmentnotmadeService;
    @Resource
    private BbsNoticeDao bbsNoticeDao;
    @Autowired
    private TdmRequestService tdmRequestService;
    @Autowired
    private CxMessageDao cxMessageDao;
    @Autowired
    private DmgzDao dmgzDao;
    @Autowired
    private TxxyAddDao txxyAddDao;
    @Autowired
    private KzxtgnkfDao kzxtgnkfDao;
    @Autowired
    private MaterielTypeFDao materielTypeFDao;
    @Resource
    private OAFinanceManager oaFinanceManager;
    @Autowired
    private StorageAreaManagementDao storageAreaManagementDao;
    @Autowired
    private OsUserManager osUserManager;
    @Autowired
    private HyglDao hyglDao;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private SysDicManager sysDicManager;
    @Autowired
    private ProductDao productDao;
    @Autowired
    private JstbDao jstbDao;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;
    @Autowired
    private XcmgProjectDao xcmgProjectDao;
    @Autowired
    private PdmApiManager pdmApiManager;
    @Autowired
    private StandardFileInfosDao standardFileInfosDao;
    @Autowired
    private MaintenanceManualfileDao maintenanceManualfileDao;
    public JsonPageResult<?> queryDelivery(HttpServletRequest request, boolean doPage)
            throws Exception {

        JsonPageResult result = new JsonPageResult(true);
        List<JSONObject> unUploadDeliveryList = new ArrayList<>();
        List<JSONObject> lastList = new ArrayList<>();
        JSONObject queryJson = new JSONObject();
        queryJson.put("userId",ContextUtil.getCurrentUserId());
        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    queryJson.put(name, value);
                }
            }
        }
        List<JSONObject> allDeliveryList = productDao.getDeliveryByUserId(queryJson);
        Map<String, List<JSONObject>> idToDeliveryList =
                allDeliveryList.stream().collect(Collectors.groupingBy(j -> j.getString("projectId")));
        for (Map.Entry<String, List<JSONObject>> projectIdEntry : idToDeliveryList.entrySet()) {
            //查询未提交的交付物
            String projectId = projectIdEntry.getKey();
            //所有的交付物
            List<JSONObject> deliveryList = projectIdEntry.getValue();
            //RDM上已上传的交付物
            List<JSONObject> uploadDeliveryList = productDao.getFinishDeliveryByProject(projectId);
            //PDM上已上传的交付物
            String tenantId = ContextUtil.getCurrentTenantId();
            Map<String, Object> params = new HashMap<>();
            params.put("TENANT_ID_", tenantId);
            params.put("projectId", projectId);
            List<Map<String, Object>> pdmFiles = pdmApiManager.getPdmProjectFiles(params);
            Set<String> uploadDeliveryId = new HashSet<>();
            uploadDeliveryList.forEach(j -> uploadDeliveryId.add(j.getString("deliveryId")));
            pdmFiles.forEach(j -> uploadDeliveryId.add(j.get("deliveryId").toString()));
            unUploadDeliveryList.addAll(
                    deliveryList.stream()
                            .filter(j -> !uploadDeliveryId.contains(j.getString("deliveryId")))
                            .collect(Collectors.toList()));
            if (unUploadDeliveryList != null && !unUploadDeliveryList.isEmpty()) {
                //查询各节点计划完成时间
                String[] productIdStrings = deliveryList.get(0).getString("productIds").split(",");
                Map<String, Object> idparam = new HashMap<>();
                idparam.put("productIds", Arrays.asList(productIdStrings));
                List<JSONObject> xpszPlanTime = xcmgProjectDao.queryXpszInfo(idparam);
                Map<String, List<JSONObject>> productIdToJson =
                        xpszPlanTime.stream().collect(Collectors.groupingBy(j -> j.getString("productId")));

                for (JSONObject object : unUploadDeliveryList) {
                    String[] productIds = object.getString("productIds").split(",");
                    String[] productNames = object.getString("productNames").split(",");
                    for (int i = 0; i < productIds.length; i++) {
                        JSONObject newJson = JSONObject.parseObject(object.toString());
                        if (productIdToJson != null && !productIdToJson.isEmpty() && productIdToJson.containsKey(productIds[i])) {
                            JSONObject planTimeJson = productIdToJson.get(productIds[i]).get(0);
                            newJson.put("planTime", planTimeJson.getString(object.getString("itemCode")));
                            newJson.put("productIds", productIds[i]);
                            newJson.put("productNames", productNames[i]);
                            lastList.add(newJson);
                        }
                    }
                }
            }
        }
        setTaskInfo2DataMap(lastList, ContextUtil.getCurrentUserId());
        result.setTotal(lastList.size());
        // 手动分页
        if (doPage) {
            lastList = RdmCommonUtil.doPageByRequest(lastList, request);
        }
        result.setData(lastList);
        return result;
    }

    public void setTaskInfo2DataMap(List<JSONObject> dataList, String currentUserId) {
        List<String> actInstIds = new ArrayList<>();
        for (JSONObject jstb : dataList) {
            if (StringUtils.isNotBlank(jstb.getString("ACT_INST_ID_"))) {
                actInstIds.add(jstb.getString("ACT_INST_ID_"));
            }
        }
        Map<String, List<JSONObject>> instId2Obj =
                dataList.stream().collect(Collectors.groupingBy(j -> j.get("ACT_INST_ID_").toString()));
        // 1、通过actInstId去act_ru_task中查询taskId,taskName,proc_inst_ID，返回List<Map<String, Object>>
        Map<String, Object> param = new HashMap<>();
        if (!actInstIds.isEmpty()) {
            param.put("actInstIds", actInstIds);
            List<Map<String, Object>> taskAll = jstbDao.queryTaskAll(param);

            // 2、调用xcmgProjectManager.setTaskCurrentUser(jsmmList);查询每一个task的处理人id和name
            if (taskAll != null && !taskAll.isEmpty()) {
                xcmgProjectManager.setTaskCurrentUser(taskAll);
            }
            // 3、通过PROC_INST_ID_关联所属的代办任务
            Map<String, List<Map<String, Object>>> instId2TaskList = new HashMap<>();
            for (Map<String, Object> oneTask : taskAll) {
                String proc_inst_id_ = oneTask.get("PROC_INST_ID_").toString();
                if (!instId2TaskList.containsKey(proc_inst_id_)) {
                    instId2TaskList.put(proc_inst_id_, new ArrayList<>());
                }
                instId2TaskList.get(proc_inst_id_).add(oneTask);
            }
            // 4、遍历每一个task列表，取出最终的taskName,处理人和myTaskId
            for (Map.Entry<String, List<Map<String, Object>>> entry : instId2TaskList.entrySet()) {
                String proc_inst_id_ = entry.getKey();
                List<Map<String, Object>> oneInstIdTaskList = entry.getValue();
                Set<String> taskNames = new HashSet<>();
                String allTaskUserNames = "";
                StringBuilder stringBuilder = new StringBuilder();
                String allTaskUserIds = "";
                StringBuilder allTaskUserIdSb = new StringBuilder();
                String myTaskId = "";
                for (Map<String, Object> oneTask : oneInstIdTaskList) {
                    String oneTaskName = oneTask.get("NAME_").toString();
                    taskNames.add(oneTaskName);
                    if (oneTask.get("currentProcessUser") != null) {
                        stringBuilder.append(oneTask.get("currentProcessUser").toString()).append(",");
                    }
                    if (oneTask.get("currentProcessUserId") != null) {
                        allTaskUserIdSb.append(oneTask.get("currentProcessUserId").toString()).append(",");
                    }
                    if (StringUtils.isBlank(myTaskId) && oneTask.get("currentProcessUserId") != null) {
                        List<String> userIds =
                                Arrays.asList(oneTask.get("currentProcessUserId").toString().split(",", -1));
                        if (userIds.contains(currentUserId)) {
                            myTaskId = oneTask.get("taskId").toString();
                        }
                    }
                }
                if (StringUtils.isNotBlank(stringBuilder.toString())) {
                    allTaskUserNames = stringBuilder.substring(0, stringBuilder.length() - 1);
                }
                if (StringUtils.isNotBlank(allTaskUserIdSb.toString())) {
                    allTaskUserIds = allTaskUserIdSb.substring(0, allTaskUserIdSb.length() - 1);
                }
                StringBuilder sbTaskNames = new StringBuilder();
                for (String oneTaskName : taskNames) {
                    sbTaskNames.append(oneTaskName).append(",");
                }
                // 赋值到业务数据中
                List<JSONObject> jstbObjList = instId2Obj.get(proc_inst_id_);
                for (Map<String, Object> stringObjectMap : jstbObjList) {
                    stringObjectMap.put("taskName", sbTaskNames.substring(0, sbTaskNames.length() - 1));
                    stringObjectMap.put("myTaskId", myTaskId);
                    stringObjectMap.put("allTaskUserNames", allTaskUserNames);
                    stringObjectMap.put("allTaskUserIds", allTaskUserIds);
                    String taskCreateTime =
                            DateFormatUtil.format((Date)oneInstIdTaskList.get(0).get("CREATE_TIME_"), "yyyy-MM-dd HH:mm:ss");
                    stringObjectMap.put("taskCreateTime", taskCreateTime);
                }
            }
        }
    }


    public JsonPageResult<?> queryAndFilterCurrentUserTodos(HttpServletRequest request, boolean doPage)
            throws Exception {
        JsonPageResult result = new JsonPageResult(true);
        // 先全部查出来
        QueryFilter filter = QueryFilterBuilder.createQueryFilter(request);
        Page pageParam = (Page) filter.getPage();
        pageParam.setPageSize(20000);
        pageParam.setPageIndex(0);
        // 待办
        List<BpmTask> bpmTasks = bpmTaskManager.getByUserId(filter);
        // 根据事项描述进行下面结果的过滤
        String descValue = "";
        if (filter.getFieldLogic().getCommands() != null) {
            List<IWhereClause> params = filter.getFieldLogic().getCommands();
            if (!params.isEmpty()) {
                for (int index = 0; index < params.size(); index++) {
                    QueryParam param = (QueryParam) params.get(index);
                    if ("description".equalsIgnoreCase((String) param.getFieldName())) {
                        descValue = (String) param.getValue();
                        break;
                    }
                }
            }
        }
        // 添加会议总结待办
        List<JSONObject> hyglTaskList = hyglDao.getHyglFinishList(ContextUtil.getCurrentUserId());
        List<BpmTask> needTodoHyglTasks = getTasksFromHyglTask(hyglTaskList, descValue);
        bpmTasks.addAll(needTodoHyglTasks);
        // 查询sap信息记录添加待办
        List<JSONObject> typeFinfoList = materielTypeFDao.queryMaterielTypeFByUserId(ContextUtil.getCurrentUserId());
        List<BpmTask> needTodoTypeFinfoTasks = getTasksFromMatereilTypeF(typeFinfoList, descValue);
        bpmTasks.addAll(needTodoTypeFinfoTasks);
        // 将检修标准值中当前用户待制作的数据查出来
        List<JSONObject> needTodoJxbzzs =
                standardvalueShipmentnotmadeService.queryNeedTodoByUserId(ContextUtil.getCurrentUserId());
        List<BpmTask> needTodoJxbzzTasks = getTasksFromJxbzzTodos(needTodoJxbzzs, descValue);
        bpmTasks.addAll(needTodoJxbzzTasks);
        // 财务OA机器人流程审批节点（非RDM流程，放入待办）
        List<JSONObject> oaFinanceList =
                oaFinanceManager.getOAFinanceDetailByCurrentUserId(ContextUtil.getCurrentUser().getUserNo());
        List<BpmTask> oaFinanceListTasks = getTasksFromOAFinanceTodos(oaFinanceList, descValue);
        bpmTasks.addAll(oaFinanceListTasks);
        // 标准管理，管理标准，管理标准附表审批;如果是“管理标准附表审批人员”（GLBZFBSPRY）则显示待办信息
        JSONObject params = new JSONObject();
        params.put("roleKey", "GLBZFBSPRY");
        JSONObject param = new JSONObject();
        param.put("status", "unReview");
        List<JSONObject> roleUsers = maintenanceManualfileDao.getUsersByRolekey(params);
        for (JSONObject object:roleUsers) {
            if (ContextUtil.getCurrentUserId().equals(object.getString("USER_ID_"))) {
                JSONArray standardFJSPList = standardFileInfosDao.getFiles(param);
                List<BpmTask> standardFJSPListTasks = getTasksFromStandardReviewTodos(standardFJSPList, descValue);
                bpmTasks.addAll(standardFJSPListTasks);
            }
        }
        /************** TDM待办，需关注是否超时，增加熔断开关 **************/
        String tdmTodoRequest = SysPropertiesUtil.getGlobalProperty("tdmTodoRequest");
        if ("on".equalsIgnoreCase(tdmTodoRequest)) {
            // TDM待办信息
            List<BpmTask> needTodoTdmTasks = tdmRequestService
                    .getTasksTodoByUserId(ContextUtil.getCurrentUser().getUserNo(), request.getRemoteAddr(), descValue);
            // TDM待办信息追加
            bpmTasks.addAll(needTodoTdmTasks);
        }
        String tdmMessageRequest = SysPropertiesUtil.getGlobalProperty("tdmMessageRequest");
        if ("on".equalsIgnoreCase(tdmMessageRequest)) {
            // TDM告警信息
            List<BpmTask> needTodoTdmMsgTasks = tdmRequestService
                    .getMsgTasksTodoByUserId(ContextUtil.getCurrentUser().getUserNo(), request.getRemoteAddr(), descValue);
            // TDM告警信息追加
            bpmTasks.addAll(needTodoTdmMsgTasks);
        }
        /******************* QMS待办，用开关控制 ***************/
        String qmsTodoSwitch = SysPropertiesUtil.getGlobalProperty("qmsTodoSwitch");
        if ("yes".equalsIgnoreCase(qmsTodoSwitch)) {
            // QMS待办信息
            List<BpmTask> needTodoQmsTasks = getTasksFromQms(descValue);
            // QMS待办信息追加
            bpmTasks.addAll(needTodoQmsTasks);
        }

        result.setTotal(bpmTasks.size());
        // 手动分页
        if (doPage) {
            bpmTasks = RdmCommonUtil.doPageByRequest(bpmTasks, request);
        }
        result.setData(bpmTasks);
        return result;
    }

    private List<BpmTask> getTasksFromQms(String descValue) {
        List<BpmTask> needTodoTasks = new ArrayList<>();
        String qmsTodoUrl = SysPropertiesUtil.getGlobalProperty("qmsTodoUrl");
        if (StringUtils.isBlank(qmsTodoUrl)) {
            return needTodoTasks;
        }
        // 查询当前用户对应的qms登录名
        OsUser osUser = osUserManager.get(ContextUtil.getCurrentUserId());
        String qmsUserNo = osUser.getQmsUserNo();
        if (StringUtils.isBlank(qmsUserNo)) {
            return needTodoTasks;
        }
        String url = qmsTodoUrl + qmsUserNo;
        String resultString = "";
        try {
            resultString = HttpClientUtil.getFromUrl(url, null);
        } catch (Exception e) {
            logger.error("获取QMS待办数据异常", e);
        }
        if (StringUtils.isBlank(resultString)) {
            return needTodoTasks;
        }
        JSONObject resultObj = JSONObject.parseObject(resultString);
        JSONArray todoArr = resultObj.getJSONArray("data");
        if (todoArr == null || todoArr.isEmpty()) {
            return needTodoTasks;
        }
        for (int index = 0; index < todoArr.size(); index++) {
            JSONObject oneTodo = todoArr.getJSONObject(index);
            String description = oneTodo.getString("title");
            if (!description.contains(descValue)) {
                continue;
            }
            BpmTask task = new BpmTask();
            task.setDescription(description);
            task.setSolKey("QMSTODO");
            task.setId(IdUtil.getId());
            task.setExecutionId("");
            task.setName(oneTodo.getString("task"));
            task.setLink(oneTodo.getString("link"));
            needTodoTasks.add(task);
        }
        return needTodoTasks;
    }

    private List<BpmTask> getTasksFromHyglTask(List<JSONObject> needTodoTypeF, String descValue) {
        List<BpmTask> needTodoTasks = new ArrayList<>();
        for (JSONObject oneTodo : needTodoTypeF) {
            String description = "【会议总结】会议编号：" + oneTodo.getString("meetingNo");
            if (!description.contains(descValue)) {
                continue;
            }
            BpmTask task = new BpmTask();
            task.setDescription(description);
            task.setSolKey("HYGL");
            task.setId(oneTodo.getString("id"));
            task.setExecutionId("");
            task.setName("会议总结");
            needTodoTasks.add(task);
        }
        return needTodoTasks;
    }

    private List<BpmTask> getTasksFromMatereilTypeF(List<JSONObject> needTodoTypeF, String descValue) {
        List<BpmTask> needTodoTasks = new ArrayList<>();
        for (JSONObject oneTodo : needTodoTypeF) {
            String description = "【物料信息记录添加】有物料的信息记录需要添加";
            if (!description.contains(descValue)) {
                continue;
            }
            BpmTask task = new BpmTask();
            task.setDescription(description);
            task.setSolKey("TYPEFINFO");
            task.setId(oneTodo.getString("id"));
            task.setExecutionId("");
            task.setName("编制");
            needTodoTasks.add(task);
        }
        return needTodoTasks;
    }

    private List<BpmTask> getTasksFromJxbzzTodos(List<JSONObject> needTodoJxbzzs, String descValue) {
        List<BpmTask> needTodoTasks = new ArrayList<>();
        for (JSONObject oneTodo : needTodoJxbzzs) {
            String versionType = oneTodo.getString("versionType");
            if (versionType.equalsIgnoreCase("csb")) {
                versionType = "测试版";
            } else if (versionType.equalsIgnoreCase("cgb")) {
                versionType = "常规版";
            } else if (versionType.equalsIgnoreCase("wzb")) {
                versionType = "完整版";
            }
            String description = "【检修标准值表待制作】物料编码：" + oneTodo.getString("materialCode") + "，版本类型:" + versionType;
            if (!description.contains(descValue)) {
                continue;
            }

            BpmTask task = new BpmTask();
            task.setDescription(description);
            task.setSolKey("JXBZZB");
            task.setId(oneTodo.getString("id"));
            String instId = oneTodo.getString("instId");
            if (StringUtils.isBlank(instId)) {
                instId = "";
            }
            task.setExecutionId(instId);
            task.setName("编制");
            needTodoTasks.add(task);
        }
        return needTodoTasks;
    }

    private List<BpmTask> getTasksFromStandardReviewTodos(JSONArray standardFJSPList, String descValue) {
        List<BpmTask> needTodoTasks = new ArrayList<>();
        if (standardFJSPList != null) {
            for (int index = 0; index < standardFJSPList.size(); index++) {
                JSONObject oneTodo = standardFJSPList.getJSONObject(index);
                String description =
                        "【管理标准上传附件审批】申请流程审批，申请人：" + oneTodo.getString("creator") + "，提交时间" + oneTodo.getString("CREATE_TIME_");
                if (!description.contains(descValue)) {
                    continue;
                }
                BpmTask task = new BpmTask();
                task.setDescription(description);
                task.setSolKey("GLBZFJSP");
                task.setId(oneTodo.getString("id"));
                // 封装原因，利用instId写入oaFlowId使用
    //            task.setInstId(oneTodo.getString("oaFlowId"));
                task.setName("管理标准附件审批");
                needTodoTasks.add(task);
            }
        }
        return needTodoTasks;
    }
    private List<BpmTask> getTasksFromOAFinanceTodos(List<JSONObject> OAFinanceTodos, String descValue) {
        List<BpmTask> needTodoTasks = new ArrayList<>();
        for (JSONObject oneTodo : OAFinanceTodos) {
            String description =
                    "【OA财务成本审批】申请流程审批，申请人：" + oneTodo.getString("applyName") + "，提交时间" + oneTodo.getString("CREATE_TIME_");
            if (!description.contains(descValue)) {
                continue;
            }
            BpmTask task = new BpmTask();
            task.setDescription(description);
            task.setSolKey("OACWCB");
            task.setId(oneTodo.getString("id"));
            // 封装原因，利用instId写入oaFlowId使用
            task.setInstId(oneTodo.getString("oaFlowId"));
            task.setName("物料信息补充");
            needTodoTasks.add(task);
        }
        return needTodoTasks;
    }

    /**
     * 查询当前用户能看到的消息，按照时间降序排列
     *
     * @return
     */
    public JsonPageResult<?> queryUserMessage(HttpServletRequest request, String isRead, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        // 查询标准宣贯消息
        List<JSONObject> standardMsgList = queryStandardMessage(request, isRead);
        // 查询平台发送的消息
        List<JSONObject> systemMsgList = querySystemMessage(request, isRead);
        // 马天宇,关键零部件设计补充标准
        List<JSONObject> linkMsgList = queryStandardLink(request);
        // 控制程序发布单
        List<JSONObject> cxMsgList = queryCxMessage(request, isRead);
        // 故障代码新增通知
        List<JSONObject> gzdmMsgList = queryGzdm(request, isRead);
        // 马辉，通信协议新增通知
        List<JSONObject> txxyMsgList = queryTxxy(request, isRead);
        // 控制系统功能开发通知
        List<JSONObject> kzxtMsgList = queryKzxt(request, isRead);
        // 控制器存储区管理
        List<JSONObject> kzccMsgList = queryStorageArea(request, isRead);

        // @lwgkiller：带关联标准的链接消息，和上面合并
        // List<JSONObject> linkMsgList2 = queryComponentTestStandardLink(request, isRead);
        // linkMsgList.addAll(linkMsgList2);
        List<JSONObject> dataList = new ArrayList<>();

        //todo@lwgkiller：增加嵌入式系统屏蔽账户过滤
        boolean QRSXTXXPBYH_allMessage_hide = false;
        SysDic sysDic = sysDicManager.getBySysTreeKeyAndDicKey("QRSXTXXPBYH", "allMessage");
        if (sysDic != null) {
            String users = sysDic.getValue();
            if (StringUtil.isNotEmpty(users)) {
                List<String> userNoList = Arrays.asList(users.split(","));
                for (String userNo : userNoList) {
                    if (ContextUtil.getCurrentUser().getUserNo().equalsIgnoreCase(userNo)) {
                        QRSXTXXPBYH_allMessage_hide = true;
                        break;
                    }
                }
            }
        }
        if (!QRSXTXXPBYH_allMessage_hide) {
            dataList.addAll(standardMsgList);
            dataList.addAll(systemMsgList);
            dataList.addAll(linkMsgList);
            dataList.addAll(cxMsgList);
            dataList.addAll(gzdmMsgList);
            dataList.addAll(txxyMsgList);
            dataList.addAll(kzxtMsgList);
            dataList.addAll(kzccMsgList);
        }

        // 未读的在最前面，然后时间倒序
        Collections.sort(dataList, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject o1, JSONObject o2) {
                if (!o1.getString("isRead").equalsIgnoreCase(o2.getString("isRead"))) {
                    return o1.getString("isRead").compareTo(o2.getString("isRead"));
                } else {
                    return o2.getString("CREATE_TIME_").compareTo(o1.getString("CREATE_TIME_"));
                }
            }
        });
        result.setTotal(dataList.size());
        // 手动分页
        if (doPage) {
            dataList = RdmCommonUtil.doPageByRequest(dataList, request);
        }
        result.setData(dataList);
        return result;
    }

    public JsonPageResult queryBbsNotice(HttpServletRequest request, String isRead, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        String userId = ContextUtil.getCurrentUserId();
        Map<String, Object> paramMap = new HashMap<>(16);
        paramMap.put("userId", userId);
        paramMap.put("isRead", isRead);
        List<Map<String, Object>> list = bbsNoticeDao.query(paramMap);
        result.setTotal(list.size());
        if (doPage) {
            list = RdmCommonUtil.doPageByRequest(list, request);
        }
        CommonFuns.convertMapDate(list, "yyyy-MM-dd");
        result.setData(list);
        return result;
    }

    private List<JSONObject> querySystemMessage(HttpServletRequest request, String isRead) {
        String webappName = WebAppUtil.getProperty("webappName", "rdm");
        List<JSONObject> result = new ArrayList<>();
        List<Map<String, Object>> msgList =
                xcmgProjectMsgManager.queryRecMsg(ContextUtil.getCurrentUserId(), "computer", webappName);
        if (msgList != null && !msgList.isEmpty()) {
            for (Map<String, Object> oneMsg : msgList) {
                String status = "未读";
                if (oneMsg.get("status") != null) {
                    status = oneMsg.get("status").toString();
                }
                String msgIsRead = status.equals("未读") ? "0" : "1";
                if (StringUtils.isNotBlank(isRead) && "0".equalsIgnoreCase(isRead)
                        && !"0".equalsIgnoreCase(msgIsRead)) {
                    continue;
                }
                JSONObject oneObj = new JSONObject();
                oneObj.put("title", oneMsg.get("title"));
                oneObj.put("creator", oneMsg.get("sendUserName"));
                oneObj.put("CREATE_TIME_", oneMsg.get("CREATE_TIME_"));
                oneObj.put("typeName", "消息");
                oneObj.put("isRead", msgIsRead);
                oneObj.put("url",
                        "/xcmgProjectManager/core/message/see.do?id=" + oneMsg.get("id") + "&status=" + msgIsRead);
                result.add(oneObj);
            }
        }
        return result;
    }

    private List<JSONObject> queryStandardMessage(HttpServletRequest request, String isRead) {
        List<JSONObject> result = new ArrayList<>();
        boolean isGl = StandardManagerUtil.judgeGLNetwork(request);
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        params.put("userId", ContextUtil.getCurrentUserId());
        params.put("systemCategoryId", isGl ? StandardConstant.SYSTEMCATEGORY_GL : "");
        JsonPageResult queryResult = standardMessageManager.queryStandardMsgList(params, false);
        List<Map<String, Object>> msgList = queryResult.getData();
        if (msgList != null && !msgList.isEmpty()) {
            List<Map<String, Object>> standardMsgList =
                    standardMessageManager.getStandardMsgByUserId(ContextUtil.getCurrentUserId());
            Set<String> unReadMessageIds = new HashSet<String>();
            if (standardMsgList != null && !standardMsgList.isEmpty()) {
                for (Map<String, Object> oneMsg : standardMsgList) {
                    unReadMessageIds.add(oneMsg.get("messageId").toString());
                }
            }
            for (Map<String, Object> oneMsg : msgList) {
                String msgIsRead = null;
                if (oneMsg.get("isRead") == null) {
                    msgIsRead = "0";
                } else if (StringUtils.isBlank(oneMsg.get("isRead").toString())) {
                    msgIsRead = unReadMessageIds.contains(oneMsg.get("id").toString()) ? "0" : "1";
                } else {
                    msgIsRead = oneMsg.get("isRead").toString();
                }
                // 仅查询未读的
                if (StringUtils.isNotBlank(isRead) && "0".equalsIgnoreCase(isRead)
                        && !"0".equalsIgnoreCase(msgIsRead)) {
                    continue;
                }
                JSONObject oneObj = new JSONObject();
                oneObj.put("isRead", msgIsRead);
                oneObj.put("title", oneMsg.get("standardMsgTitle"));
                oneObj.put("creator", oneMsg.get("creator"));
                oneObj.put("CREATE_TIME_", oneMsg.get("CREATE_TIME_"));
                oneObj.put("typeName", "标准宣贯");
                oneObj.put("url", "/standardManager/core/standardMessage/see.do?id=" + oneMsg.get("id") + "&standardId="
                        + oneMsg.get("relatedStandardId"));
                result.add(oneObj);
            }
        }
        return result;
    }

    // 马天宇,关键零部件设计补充标准
    private List<JSONObject> queryStandardLink(HttpServletRequest request) {
        List<JSONObject> result = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        params.put("recId", ContextUtil.getCurrentUserId());
        // 关键零部件设计的相关消息
        List<JSONObject> msgList = jsbzDao.queryMsg(params);
        for (JSONObject msg : msgList) {
            if (msg.getDate("CREATE_TIME_") != null) {
                msg.put("CREATE_TIME_", DateUtil.formatDate(msg.getDate("CREATE_TIME_"), "yyyy-MM-dd"));
            }

        }
        if (msgList != null && !msgList.isEmpty()) {
            for (Map<String, Object> oneMsg : msgList) {
                String status = null;
                if (oneMsg.get("linkaction") != null) {
                    status = oneMsg.get("linkaction").toString();
                }
                String msgIsRead = status.equals("未关联") ? "0" : "1";
                if (!"0".equalsIgnoreCase(msgIsRead)) {
                    continue;
                }
                JSONObject oneObj = new JSONObject();
                oneObj.put("title", "请补充标准资源管理缺失标准");
                oneObj.put("creator", oneMsg.get("userName"));
                oneObj.put("CREATE_TIME_", oneMsg.get("CREATE_TIME_"));
                oneObj.put("typeName", "标准关联");
                oneObj.put("isRead", msgIsRead);
                oneObj.put("url", "/jsbz/editMsgPage.do?action=edit&type=" + oneMsg.get("belongbj") + "&msgId="
                        + oneMsg.get("msgId") + "&status=" + status);
                result.add(oneObj);
            }
        }
        return result;
    }

    // 程序发布单
    private List<JSONObject> queryCxMessage(HttpServletRequest request, String isRead) {
        List<JSONObject> result = new ArrayList<>();
        JSONObject params = new JSONObject();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        params.put("userId", ContextUtil.getCurrentUserId());
        JsonPageResult queryResult = queryCxMsgList(request, false);
        List<JSONObject> msgList = queryResult.getData();
        if (msgList != null && !msgList.isEmpty()) {
            List<JSONObject> cxMsgList = cxMessageDao.getCxMsgByUserId(ContextUtil.getCurrentUserId());
            Set<String> unReadMessageIds = new HashSet<String>();
            if (cxMsgList != null && !cxMsgList.isEmpty()) {
                for (JSONObject oneMsg : cxMsgList) {
                    unReadMessageIds.add(oneMsg.getString("msgId"));
                }
            }
            for (JSONObject oneMsg : msgList) {
                String msgIsRead = null;
                if (oneMsg.get("isRead") == null) {
                    msgIsRead = "0";
                } else if (StringUtils.isBlank(oneMsg.getString("isRead"))) {
                    msgIsRead = unReadMessageIds.contains(oneMsg.getString("msgId")) ? "0" : "1";
                } else {
                    msgIsRead = oneMsg.getString("isRead");
                }
                // 仅查询未读的
                if (StringUtils.isNotBlank(isRead) && "0".equalsIgnoreCase(isRead)
                        && !"0".equalsIgnoreCase(msgIsRead)) {
                    continue;
                }
                JSONObject oneObj = new JSONObject();
                oneObj.put("isRead", msgIsRead);
                oneObj.put("title", oneMsg.get("content"));
                oneObj.put("creator", oneMsg.get("userName"));
                oneObj.put("CREATE_TIME_", oneMsg.get("CREATE_TIME_"));
                oneObj.put("typeName", "程序发布通知单");
                oneObj.put("targetSubSysKey", RdmConst.SUBSYS_SOFT_KEY);
                oneObj.put("url", "/embedsoft/core/cxRes/editPage.do?msgId=" + oneMsg.get("msgId") + "&cxResId="
                        + oneMsg.get("cxResId") + "&action=detail&read=yes");
                result.add(oneObj);
            }
        }
        return result;
    }

    // 故障代码新增通知
    private List<JSONObject> queryGzdm(HttpServletRequest request, String isRead) {
        List<JSONObject> result = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        params.put("recId", ContextUtil.getCurrentUserId());
        // 故障代码的相关消息
        List<JSONObject> msgList = dmgzDao.queryGzdmMsg(params);
        for (JSONObject msg : msgList) {
            if (msg.getDate("CREATE_TIME_") != null) {
                msg.put("CREATE_TIME_", DateUtil.formatDate(msg.getDate("CREATE_TIME_"), "yyyy-MM-dd"));
            }

        }
        if (msgList != null && !msgList.isEmpty()) {
            for (JSONObject oneMsg : msgList) {
                String msgIsRead = null;
                if (oneMsg.get("isRead") == null) {
                    msgIsRead = "0";
                } else {
                    msgIsRead = oneMsg.getString("isRead");
                }
                // 仅查询未读的
                if (StringUtils.isNotBlank(isRead) && "0".equalsIgnoreCase(isRead)
                        && !"0".equalsIgnoreCase(msgIsRead)) {
                    continue;
                }
                JSONObject oneObj = new JSONObject();
                oneObj.put("title", "故障代码新增通知");
                oneObj.put("creator", oneMsg.get("userName"));
                oneObj.put("CREATE_TIME_", oneMsg.get("CREATE_TIME_"));
                oneObj.put("typeName", "故障代码新增通");
                oneObj.put("targetSubSysKey", RdmConst.SUBSYS_SOFT_KEY);
                oneObj.put("isRead", msgIsRead);
                oneObj.put("url", "/rdm/core/Dmgz/editPage.do?&msgId=" + oneMsg.get("msgId") + "&dmgzId="
                        + oneMsg.get("dmgzId") + "&action=detail&read=yes");
                result.add(oneObj);
            }
        }
        return result;
    }

    // 通信协议新增通知
    private List<JSONObject> queryTxxy(HttpServletRequest request, String isRead) {
        List<JSONObject> result = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        params.put("recId", ContextUtil.getCurrentUserId());
        // 故障代码的相关消息
        List<JSONObject> msgList = txxyAddDao.queryTxxyMsg(params);
        for (JSONObject msg : msgList) {
            if (msg.getDate("CREATE_TIME_") != null) {
                msg.put("CREATE_TIME_", DateUtil.formatDate(msg.getDate("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        if (msgList != null && !msgList.isEmpty()) {
            for (JSONObject oneMsg : msgList) {
                String msgIsRead = null;
                if (oneMsg.get("isRead") == null) {
                    msgIsRead = "0";
                } else {
                    msgIsRead = oneMsg.getString("isRead");
                }
                // 仅查询未读的
                if (StringUtils.isNotBlank(isRead) && "0".equalsIgnoreCase(isRead)
                        && !"0".equalsIgnoreCase(msgIsRead)) {
                    continue;
                }
                JSONObject oneObj = new JSONObject();
                oneObj.put("title", "通信协议新增通知");
                oneObj.put("creator", "管理员");
                oneObj.put("CREATE_TIME_", oneMsg.get("CREATE_TIME_"));
                oneObj.put("typeName", "通信协议新增通知");
                oneObj.put("targetSubSysKey", RdmConst.SUBSYS_SOFT_KEY);
                oneObj.put("isRead", msgIsRead);
                oneObj.put("url", "/embedsoft/core/txxyAdd/applyEditPage.do?&msgId=" + oneMsg.get("msgId") + "&applyId="
                        + oneMsg.get("txxyId") + "&action=detail&read=yes");
                result.add(oneObj);
            }
        }
        return result;
    }

    // 控制器存储区管理通知
    private List<JSONObject> queryStorageArea(HttpServletRequest request, String isRead) {
        List<JSONObject> result = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        params.put("recId", ContextUtil.getCurrentUserId());
        // 故障代码的相关消息
        List<JSONObject> msgList = storageAreaManagementDao.queryStorageAreaMsg(params);
        for (JSONObject msg : msgList) {
            if (msg.getDate("CREATE_TIME_") != null) {
                msg.put("CREATE_TIME_", DateUtil.formatDate(msg.getDate("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        if (msgList != null && !msgList.isEmpty()) {
            for (JSONObject oneMsg : msgList) {
                String msgIsRead = null;
                if (oneMsg.get("isRead") == null) {
                    msgIsRead = "0";
                } else {
                    msgIsRead = oneMsg.getString("isRead");
                }
                // 仅查询未读的
                if (StringUtils.isNotBlank(isRead) && "0".equalsIgnoreCase(isRead)
                        && !"0".equalsIgnoreCase(msgIsRead)) {
                    continue;
                }
                JSONObject oneObj = new JSONObject();
                oneObj.put("title", "控制器存储区管理通知");
                oneObj.put("creator", "管理员");
                oneObj.put("CREATE_TIME_", oneMsg.get("CREATE_TIME_"));
                oneObj.put("typeName", "控制器存储区管理通知");
                oneObj.put("targetSubSysKey", RdmConst.SUBSYS_SOFT_KEY);
                oneObj.put("isRead", msgIsRead);
                oneObj.put("url", "/embedsoft/core/storageArea/applyEditPage.do?&msgId=" + oneMsg.get("msgId")
                        + "&applyId=" + oneMsg.get("storageAreaId") + "&action=detail&read=yes");
                result.add(oneObj);
            }
        }
        return result;
    }

    // 控制系统功能开发通知
    private List<JSONObject> queryKzxt(HttpServletRequest request, String isRead) {
        List<JSONObject> result = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        params.put("recId", ContextUtil.getCurrentUserId());
        // 故障代码的相关消息
        List<JSONObject> msgList = kzxtgnkfDao.queryKzxtMsg(params);
        for (JSONObject msg : msgList) {
            if (msg.getDate("CREATE_TIME_") != null) {
                msg.put("CREATE_TIME_", DateUtil.formatDate(msg.getDate("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        if (msgList != null && !msgList.isEmpty()) {
            for (JSONObject oneMsg : msgList) {
                String msgIsRead = null;
                if (oneMsg.get("isRead") == null) {
                    msgIsRead = "0";
                } else {
                    msgIsRead = oneMsg.getString("isRead");
                }
                // 仅查询未读的
                if (StringUtils.isNotBlank(isRead) && "0".equalsIgnoreCase(isRead)
                        && !"0".equalsIgnoreCase(msgIsRead)) {
                    continue;
                }
                JSONObject oneObj = new JSONObject();
                oneObj.put("title", "控制系统功能开发通知");
                oneObj.put("creator", "管理员");
                oneObj.put("CREATE_TIME_", oneMsg.get("CREATE_TIME_"));
                oneObj.put("typeName", "控制系统功能开发通知");
                oneObj.put("targetSubSysKey", RdmConst.SUBSYS_SOFT_KEY);
                oneObj.put("isRead", msgIsRead);
                oneObj.put("url", "/embedsoft/core/kzxtgnkf/applyEditPage.do?&msgId=" + oneMsg.get("msgId")
                        + "&applyId=" + oneMsg.get("kzxtId") + "&action=detail&read=yes");
                result.add(oneObj);
            }
        }
        return result;
    }

    // @lwgkiller:零部件试验相关，由于马天宇的方法过程侵入了有特殊意义的字段linkaction，而且状态值有可能有差异
    // 因此没法合并，除非同一约定状态字段的表达方式
    private List<JSONObject> queryComponentTestStandardLink(HttpServletRequest request, String isRead) {
        List<JSONObject> result = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        params.put("recId", ContextUtil.getCurrentUserId());
        // 零部件试验的相关消息
        List<JSONObject> componentTestStandardMsgList = componentTestKanbanDao.queryBindingStandardMsgList(params);
        for (JSONObject msg : componentTestStandardMsgList) {
            if (msg.getDate("CREATE_TIME_") != null) {
                msg.put("CREATE_TIME_", DateUtil.formatDate(msg.getDate("CREATE_TIME_"), "yyyy-MM-dd"));
            }

        }
        if (componentTestStandardMsgList != null && !componentTestStandardMsgList.isEmpty()) {
            for (Map<String, Object> oneMsg : componentTestStandardMsgList) {
                String status = "未关联";
                if (oneMsg.get("status") != null) {
                    status = oneMsg.get("status").toString();
                }
                String msgIsRead = (status.equals("未关联") || status.equals("部分关联")) ? "0" : "1";
                if (StringUtils.isNotBlank(isRead) && "0".equalsIgnoreCase(isRead)
                        && !"0".equalsIgnoreCase(msgIsRead)) {
                    continue;
                }
                JSONObject oneObj = new JSONObject();
                oneObj.put("title", "请补充标准资源管理缺失标准");
                oneObj.put("creator", oneMsg.get("userName"));
                oneObj.put("CREATE_TIME_", oneMsg.get("CREATE_TIME_"));
                oneObj.put("typeName", "标准关联");
                oneObj.put("isRead", msgIsRead);
                oneObj.put("url",
                        "/componentTest/core/kanban/editBindingStandardMsgPage.do?action=edit&id=" + oneMsg.get("id"));
                result.add(oneObj);
            }
        }
        return result;
    }

    /**
     * 查询当前用户能看到的通知公告，按照时间降序排列
     *
     * @return
     */
    public JsonPageResult queryUserNotice(HttpServletRequest request, String isRead, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        //todo@lwgkiller：增加嵌入式系统屏蔽账户过滤
        boolean QRSXTXXPBYH_allMessage_hide = false;
        SysDic sysDic = sysDicManager.getBySysTreeKeyAndDicKey("QRSXTXXPBYH", "allMessage");
        if (sysDic != null) {
            String users = sysDic.getValue();
            if (StringUtil.isNotEmpty(users)) {
                List<String> userNoList = Arrays.asList(users.split(","));
                for (String userNo : userNoList) {
                    if (ContextUtil.getCurrentUser().getUserNo().equalsIgnoreCase(userNo)) {
                        QRSXTXXPBYH_allMessage_hide = true;
                        break;
                    }
                }
            }
        }
        if (!QRSXTXXPBYH_allMessage_hide) {
            List<JSONObject> data = jstbService.queryNoticeForRdmHome(isRead);
            result.setTotal(data.size());
            if (doPage) {
                data = RdmCommonUtil.doPageByRequest(data, request);
            }
            result.setData(data);
        }
        return result;
    }

    /**
     * 查询所有人当前的动态
     *
     * @param request
     * @return
     */
    public JsonPageResult queryWorkStatusNow(HttpServletRequest request) {
        JsonPageResult result = new JsonPageResult(true);
        // 人员id
        List<JSONObject> userInfos = rdmHomeDao.queryWorkStatusUserEnum();
        if (userInfos == null || userInfos.isEmpty()) {
            return null;
        }
        Set<String> userIds = new HashSet<>();
        for (JSONObject oneUser : userInfos) {
            userIds.add(oneUser.getString("userId"));
        }
        Map<String, Object> params = new HashMap<>();
        params.put("userIds", new ArrayList<>(userIds));
        params.put("dayStr", DateFormatUtil.getNowByString("yyyy-MM-dd"));
        String nowHour = DateFormatUtil.getNowByString("HH:mm");
        params.put("currentTime", nowHour);
        List<JSONObject> data = rdmHomeDao.queryWorkStatus(params);
        if (data != null) {
            Map<String, List<JSONObject>> userId2StatusList = new HashMap<>();
            for (JSONObject oneData : data) {
                if (!userId2StatusList.containsKey(oneData.getString("leaderUserId"))) {
                    userId2StatusList.put(oneData.getString("leaderUserId"), new ArrayList<>());
                }
                userId2StatusList.get(oneData.getString("leaderUserId")).add(oneData);
            }
            for (JSONObject oneUser : userInfos) {
                String leaderUserId = oneUser.getString("userId");
                if (userId2StatusList.containsKey(leaderUserId)) {
                    List<JSONObject> oneUserList = userId2StatusList.get(leaderUserId);
                    StringBuilder oneLeaderNowStatus = new StringBuilder();
                    for (JSONObject oneSchedule : oneUserList) {
                        oneLeaderNowStatus.append(oneSchedule.getString("startTime") + "~")
                                .append(oneSchedule.getString("endTime") + "，");
                        oneLeaderNowStatus.append(oneSchedule.getString("place") + "；");
                    }
                    oneUser.put("statusDesc", oneLeaderNowStatus.substring(0, oneLeaderNowStatus.length() - 1));
                }
            }
        }
        result.setData(userInfos);
        result.setTotal(userInfos.size());
        return result;
    }

    /**
     * 查询某个人某天的动态
     *
     * @param userId
     * @return
     */
    public List<JSONObject> queryWorkStatusOneUser(String userId, String dayStr) {
        Map<String, Object> params = new HashMap<>();
        params.put("userIds", Arrays.asList(userId));
        params.put("dayStr", dayStr);
        List<JSONObject> data = rdmHomeDao.queryWorkStatus(params);
        if (data != null) {
            for (JSONObject oneData : data) {
                if (oneData.getDate("CREATE_TIME_") != null) {
                    oneData.put("CREATE_TIME_",
                            DateFormatUtil.format(oneData.getDate("CREATE_TIME_"), "yyyy-MM-dd HH:mm"));
                }
            }
        }
        return data;
    }

    /**
     * 判断是否在白名单，如果不在则判断是否允许加密软件，运行才允许登录
     *
     * @param request
     * @return
     */
    private JsonResult judgeJMLogin(HttpServletRequest request) {
        JsonResult result = new JsonResult(true, "");
        String currentIp = StandardManagerUtil.getIpAddr(request);
        if (StringUtils.isNotBlank(currentIp)) {
            List<Map<String, Object>> ips = commonInfoManager.getDicValues("JMIPWHITE");
            for (Map<String, Object> oneIp : ips) {
                String ipValue = oneIp.get("value") == null ? "" : oneIp.get("value").toString();
                if (ipValue.contains(currentIp)) {
                    return result;
                }
            }
        }
        boolean runJM = judgeRunJM();
        if (runJM) {
            return result;
        }
        result.setSuccess(false);
        result.setMessage("请先登录亿赛通加密软件！");
        return result;
    }

    /**
     * 判断是否在运行加密软件程序
     *
     * @return
     */
    private boolean judgeRunJM() {
        try {
            ProcessBuilder pb = new ProcessBuilder("tasklist");
            Process p = pb.start();
            BufferedReader out = new BufferedReader(
                    new InputStreamReader(new BufferedInputStream(p.getInputStream()), Charset.forName("GB2312")));
            String ostr;
            while ((ostr = out.readLine()) != null) {
                if (ostr.toLowerCase().contains("cdgregedit")) {
                    return true;
                }
            }
        } catch (Exception e) {
            logger.error("Exception in judgeRunJM", e);
            return false;
        }
        return false;
    }

    public void saveOrUpdateWorkStatus(JsonResult result, String changeGridDataStr) {
        try {
            JSONArray changeGridDataJson = JSONObject.parseArray(changeGridDataStr);
            if (changeGridDataJson == null || changeGridDataJson.isEmpty()) {
                logger.warn("gridData is blank");
                result.setSuccess(true);
                result.setMessage("保存成功");
                return;
            }
            for (int i = 0; i < changeGridDataJson.size(); i++) {
                JSONObject oneObject = changeGridDataJson.getJSONObject(i);
                String state = oneObject.getString("_state");
                String id = oneObject.getString("id");
                if ("added".equals(state) || StringUtils.isBlank(id)) {
                    // 新增
                    oneObject.put("id", IdUtil.getId());
                    String startTime = oneObject.getString("startTime");
                    if (startTime.contains("T")) {
                        startTime = startTime.substring(11, 16);
                        oneObject.put("startTime", startTime);
                    }
                    String endTime = oneObject.getString("endTime");
                    if (endTime.contains("T")) {
                        endTime = endTime.substring(11, 16);
                        oneObject.put("endTime", endTime);
                    }
                    oneObject.put("CREATE_TIME_", new Date());
                    rdmHomeDao.insertWorkStatus(oneObject);
                } else if ("modified".equals(state)) {
                    String startTime = oneObject.getString("startTime");
                    if (startTime.contains("T")) {
                        startTime = startTime.substring(11, 16);
                        oneObject.put("startTime", startTime);
                    }
                    String endTime = oneObject.getString("endTime");
                    if (endTime.contains("T")) {
                        endTime = endTime.substring(11, 16);
                        oneObject.put("endTime", endTime);
                    }
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", new Date());
                    rdmHomeDao.updateWorkStatus(oneObject);
                } else if ("removed".equals(state)) {
                    rdmHomeDao.deleteWorkStatus(oneObject);
                }
            }
        } catch (Exception e) {
            logger.error("Exception in saveOrUpdateWorkStatus ");
            result.setSuccess(false);
            result.setMessage("系统异常");
            return;
        }
    }

    // 发送某一个领导的日程钉钉提醒，scheduleList是按照startTime升序排列的
    public void sendOneLeaderScheduleNotice(String userId, List<JSONObject> scheduleList, JsonResult result) {
        if (scheduleList == null || scheduleList.isEmpty()) {
            return;
        }
        JSONObject noticeObj = new JSONObject();
        StringBuilder stringBuilder = new StringBuilder();
        for (int index = 0; index < scheduleList.size(); index++) {
            JSONObject oneSchedule = scheduleList.get(index);
            stringBuilder.append("\n" + (index + 1) + "、开始时间：" + oneSchedule.getString("startTime"))
                    .append("，结束时间：" + oneSchedule.getString("endTime"));
            stringBuilder.append("，地点：" + oneSchedule.getString("place"))
                    .append("，事项：" + oneSchedule.getString("scheduleDesc"));
            stringBuilder.append("，创建人：" + oneSchedule.getString("creatorName"));
        }
        noticeObj.put("content", "【" + scheduleList.get(0).getString("dayStr") + "日程提醒】" + stringBuilder.toString());
        sendDDNoticeManager.sendNoticeForCommon(userId, noticeObj);
    }

    // 查询标准管理的消息列表
    public JsonPageResult<?> queryCxMsgList(HttpServletRequest request, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)) {
            params.put("sortField", sortField);
            params.put("sortOrder", sortOrder);
        }
        if (StringUtils.isEmpty(sortField)) {
            params.put("sortField", "soft_message.CREATE_TIME_");
            params.put("sortOrder", "desc");
        }
        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    params.put(name, value);
                }
            }
        }
        if (doPage) {
            rdmZhglUtil.addPage(request, params);
        }
        params.put("userId", ContextUtil.getCurrentUserId());
        // addFsjRoleParam(params,ContextUtil.getCurrentUserId(), ContextUtil.getCurrentUser().getUserNo());
        List<JSONObject> lccxResList = cxMessageDao.queryCxMsgList(params);
        for (JSONObject fsj : lccxResList) {
            if (fsj.get("CREATE_TIME_") != null) {
                fsj.put("CREATE_TIME_", DateUtil.formatDate((Date) fsj.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        rdmZhglUtil.setTaskInfo2Data(lccxResList, ContextUtil.getCurrentUserId());
        result.setData(lccxResList);
        int countCxRes = cxMessageDao.countCxMsgList(params);
        result.setTotal(countCxRes);
        return result;
    }
}
