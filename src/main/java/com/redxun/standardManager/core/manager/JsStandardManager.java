package com.redxun.standardManager.core.manager;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.json.JsonResultUtil;
import com.redxun.core.query.Page;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.standardManager.core.dao.StandardManagementDao;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import groovy.util.logging.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.*;

@Service
@Slf4j
public class JsStandardManager {
    private static Logger logger = LoggerFactory.getLogger(JsStandardManager.class);
    @Autowired
    private StandardManagementDao standardManagementDao;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Resource
    private BpmInstManager bpmInstManager;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Resource
    private CommonInfoDao commonInfoDao;

    public JsonPageResult<?> standardApplyList(HttpServletRequest request, HttpServletResponse response) {
        String currentUserId = ContextUtil.getCurrentUserId();
        JsonPageResult result = new JsonPageResult(true);
        try {
            Map<String, Object> params = new HashMap<>();
            // 传入条件(不包括分页)
            getApplyListParams(params, request);

            String sortField = request.getParameter("sortField");
            String sortOrder = request.getParameter("sortOrder");
            if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)) {
                params.put("sortField", sortField);
                params.put("sortOrder", sortOrder);
            }
            if (StringUtils.isEmpty(sortField)) {
                params.put("sortField", "CREATE_TIME_");
                params.put("sortOrder", "desc");
            }
            String filterParams = request.getParameter("filter");
            if (StringUtils.isNotBlank(filterParams)) {
                JSONArray jsonArray = JSONArray.parseArray(filterParams);
                for (int i = 0; i < jsonArray.size(); i++) {
                    String name = jsonArray.getJSONObject(i).getString("name");
                    String value = jsonArray.getJSONObject(i).getString("value");
                    if (StringUtils.isNotBlank(value)) {
                        // 数据库中存储的时间是UTC时间，因此需要将前台传递的北京时间转化
                        if ("applyTimeStart".equalsIgnoreCase(name)) {
                            value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), -8));
                        }
                        if ("applyTimeEnd".equalsIgnoreCase(name)) {
                            value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), 16));
                        }
                        params.put(name, value);
                    }
                }
            }

            List<JSONObject> standardList = standardManagementDao.queryStandardList(params);

            Iterator<JSONObject> iterator = standardList.iterator();
            while (iterator.hasNext()) {
                JSONObject next = iterator.next();
                if ("DRAFTED".equals(next.get("instStatus")) && !currentUserId.equals(next.get("CREATE_BY_"))) {
                    iterator.remove();
                }
                if (StringUtils.isBlank(next.getString("standardName"))) {
                    next.put("standardName", next.getString("oldStandardName"));
                }
            }
            fenxianZDadd(standardList);
            List<String> actInstIds = new ArrayList<>();
            Map<String, JSONObject> instId2Obj = new HashMap<>();
            for (JSONObject standard : standardList) {
                if (standard.getDate("CREATE_TIME_") != null) {
                    standard.put("CREATE_TIME_", DateUtil.formatDate(standard.getDate("CREATE_TIME_"), "yyyy-MM-dd"));
                }
                if (standard.getDate("draftTime") != null) {
                    standard.put("draftTime", DateUtil.formatDate(standard.getDate("draftTime"), "yyyy-MM-dd"));
                }
                if (standard.getDate("solicitopinionsTime") != null) {
                    standard.put("solicitopinionsTime",
                        DateUtil.formatDate(standard.getDate("solicitopinionsTime"), "yyyy-MM-dd"));
                }
                if (standard.getDate("reviewTime") != null) {
                    standard.put("reviewTime", DateUtil.formatDate(standard.getDate("reviewTime"), "yyyy-MM-dd"));
                }
                if (standard.getDate("reportforapprovalTime") != null) {
                    standard.put("reportforapprovalTime",
                        DateUtil.formatDate(standard.getDate("reportforapprovalTime"), "yyyy-MM-dd"));
                }
                if (standard.getDate("applicationTime") != null) {
                    standard.put("applicationTime",
                        DateUtil.formatDate(standard.getDate("applicationTime"), "yyyy-MM-dd"));
                }
                if (StringUtils.isNotBlank(standard.getString("ACT_INST_ID_"))) {
                    actInstIds.add(standard.getString("ACT_INST_ID_"));
                    instId2Obj.put(standard.getString("ACT_INST_ID_"), standard);
                }

            }

            // 1、通过actInstId去act_ru_task中查询taskId,taskName,proc_inst_ID，返回List<Map<String, Object>>
            Map<String, Object> param = new HashMap<>();
            if (!actInstIds.isEmpty()) {
                param.put("actInstIds", actInstIds);
                List<Map<String, Object>> taskAll = standardManagementDao.queryTaskAll(param);

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
                    String taskName = oneInstIdTaskList.get(0).get("NAME_").toString();
                    String allTaskUserNames = "";
                    StringBuilder stringBuilder = new StringBuilder();
                    String myTaskId = "";
                    for (Map<String, Object> oneTask : oneInstIdTaskList) {
                        stringBuilder.append(oneTask.get("currentProcessUser").toString()).append(",");
                        if (StringUtils.isBlank(myTaskId) && oneTask.get("currentProcessUserId") != null) {
                            List<String> userIds =
                                Arrays.asList(oneTask.get("currentProcessUserId").toString().split(",", -1));
                            if (userIds.contains(currentUserId)) {
                                myTaskId = oneTask.get("taskId").toString();
                            }
                        }
                    }
                    allTaskUserNames = stringBuilder.substring(0, stringBuilder.length() - 1);

                    // 赋值到业务数据中
                    JSONObject jstbObj = instId2Obj.get(proc_inst_id_);
                    jstbObj.put("taskName", taskName);
                    jstbObj.put("myTaskId", myTaskId);
                    jstbObj.put("allTaskUserNames", allTaskUserNames);
                }
            }
            if (params.containsKey("taskName")) {
                standardList = filterListByTaskName(standardList, params.get("taskName").toString());
            }
            List<JSONObject> finalSubList = new ArrayList<JSONObject>();
            // 根据分页进行subList截取
            int pageIndex = RequestUtil.getInt(request, "pageIndex", 0);
            int pageSize = RequestUtil.getInt(request, "pageSize", Page.DEFAULT_PAGE_SIZE);
            int startSubListIndex = pageIndex * pageSize;
            int endSubListIndex = startSubListIndex + pageSize;
            if (startSubListIndex < standardList.size()) {
                finalSubList = standardList.subList(startSubListIndex,
                    endSubListIndex < standardList.size() ? endSubListIndex : standardList.size());
            }

            /* if (finalSubList != null && !finalSubList.isEmpty()) {
                Set<String> doDeptIdSet = new HashSet<>();
                for (Map<String, Object> oneApply : finalSubList) {
            
                    if (oneApply.get("UPDATE_TIME_") != null) {
                        oneApply.put("UPDATE_TIME_",
                            DateUtil.formatDate((Date)oneApply.get("UPDATE_TIME_"), "yyyy-MM-dd HH:mm:ss"));
                    }
                    if (oneApply.get("doDeptIds") != null) {
                        String[] doDeptIdArr = oneApply.get("doDeptIds").toString().split(",", -1);
                        if (doDeptIdArr != null && doDeptIdArr.length > 0) {
                            doDeptIdSet.addAll(Arrays.asList(doDeptIdArr));
                        }
                    }
                }
                assignDoDeptNames(finalSubList, doDeptIdSet);
            }*/
            // 返回结果
            result.setData(finalSubList);
            result.setTotal(standardList.size());
        } catch (Exception e) {
            logger.error("Exception in standardApplyList", e);
            result.setSuccess(false);
            result.setMessage("系统异常");
        }
        return result;
    }

    private void assignDoDeptNames(List<Map<String, Object>> finalSubApplyList, Set<String> deptIds) {
        if (deptIds == null || deptIds.isEmpty()) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("deptIds", deptIds);
        List<JSONObject> deptInfos = commonInfoDao.queryDeptByIds(params);
        if (deptInfos == null || deptInfos.isEmpty()) {
            return;
        }
        Map<String, String> deptId2Name = new HashMap<>();
        for (JSONObject oneDept : deptInfos) {
            deptId2Name.put(oneDept.getString("GROUP_ID_"), oneDept.getString("NAME_"));
        }
        for (Map<String, Object> oneApply : finalSubApplyList) {
            if (oneApply.get("doDeptIds") != null && StringUtils.isNotBlank(oneApply.get("doDeptIds").toString())) {
                String[] doDeptIdArr = oneApply.get("doDeptIds").toString().split(",", -1);
                if (doDeptIdArr != null && doDeptIdArr.length > 0) {
                    StringBuilder sb = new StringBuilder();
                    for (String oneDeptId : doDeptIdArr) {
                        sb.append(deptId2Name.get(oneDeptId)).append(",");
                    }
                    oneApply.put("doDeptNames", sb.substring(0, sb.length() - 1));
                }
            }
        }
    }

    // 将过滤条件、排序等信息传入，分页不在此处进行
    // 根据登录人部门、角色对列表进行过滤

    private void getApplyListParams(Map<String, Object> params, HttpServletRequest request) {
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");

        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)) {
            params.put("sortField", sortField);
            params.put("sortOrder", sortOrder);
        }
        if (StringUtils.isEmpty(sortField)) {
            params.put("sortField", "applyId");
            params.put("sortOrder", "DESC");
        }
        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    // 数据库中存储的时间是UTC时间，因此需要将前台传递的北京时间转化
                    if ("applyTimeStart".equalsIgnoreCase(name)) {
                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), -8));
                    }
                    if ("applyTimeEnd".equalsIgnoreCase(name)) {
                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), 16));
                    }
                    params.put(name, value);
                }
            }
        }
    }

    private List<Map<String, Object>> filterApplyListByDepRole(List<Map<String, Object>> applyList) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        if (applyList == null || applyList.isEmpty()) {
            return result;
        }
        // 刷新任务的当前执行人
        xcmgProjectManager.setTaskCurrentUser(applyList);
        // 管理员查看所有的包括草稿数据
        if ("admin".equals(ContextUtil.getCurrentUser().getUserNo())) {
            return applyList;
        }
        // 标准管理领导的查看权限等同于标准管理人员
        boolean showAll = false;
        Map<String, Object> params = new HashMap<>();
        params.put("userId", ContextUtil.getCurrentUser().getUserId());
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params);
        for (Map<String, Object> oneRole : currentUserRoles) {
            if (oneRole.get("REL_TYPE_KEY_").toString().equalsIgnoreCase("GROUP-USER-LEADER")
                || oneRole.get("REL_TYPE_KEY_").toString().equalsIgnoreCase("GROUP-USER-BELONG")) {
                if ("标准管理领导".equalsIgnoreCase(oneRole.get("NAME_").toString())
                    || oneRole.get("NAME_").toString().indexOf("标准管理人员") != -1) {
                    showAll = true;
                    break;
                }
            }
        }
        // 确定当前登录人是否是部门负责人
        JSONObject currentUserDepInfo = xcmgProjectManager.isCurrentUserDepRespman();
        if (!"success".equals(currentUserDepInfo.getString("result"))) {
            return result;
        }
        boolean isDepRespMan = currentUserDepInfo.getBoolean("isDepRespMan");
        String currentUserMainDepId = currentUserDepInfo.getString("currentUserMainDepId");
        String currentUserId = ContextUtil.getCurrentUserId();

        // 过滤
        for (Map<String, Object> oneApply : applyList) {
            // 自己是当前流程处理人
            if (oneApply.get("currentProcessUserId") != null
                && oneApply.get("currentProcessUserId").toString().contains(currentUserId)) {
                oneApply.put("processTask", true);
                result.add(oneApply);
            } else if (showAll) {
                // 标准管理领导和标准管理人员查看所有非草稿的数据或者草稿但是创建人CREATE_BY_是自己的
                if (oneApply.get("instStatus") != null && !"DRAFTED".equals(oneApply.get("instStatus").toString())) {
                    result.add(oneApply);
                } else {
                    if (oneApply.get("CREATE_BY_").toString().equals(currentUserId)) {
                        result.add(oneApply);
                    }
                }
            } else if (isDepRespMan) {
                // 部门负责人对于非草稿的且申请人部门是当前部门，或者草稿但是创建人CREATE_BY_是自己的
                if (oneApply.get("instStatus") != null && !"DRAFTED".equals(oneApply.get("instStatus").toString())) {
                    if (oneApply.get("applyUserDepId").toString().equals(currentUserMainDepId)) {
                        result.add(oneApply);
                    }
                } else {
                    if (oneApply.get("CREATE_BY_").toString().equals(currentUserId)) {
                        result.add(oneApply);
                    }
                }
            } else {
                // 其他人对于创建人CREATE_BY_是自己的
                if (oneApply.get("CREATE_BY_").toString().equals(currentUserId)) {
                    result.add(oneApply);
                }
            }
        }

        return result;
    }

    // 删除表单及关联的所有信息
    public JsonResult deleteApply(String[] applyIdArr, String[] instIdArr) {
        if (applyIdArr.length != instIdArr.length) {
            return new JsonResult(false, "表单和流程个数不相同！");
        }
        for (int i = 0; i < applyIdArr.length; i++) {
            String applyId = applyIdArr[i];
            standardManagementDao.deleteStandardById(applyId);
            // 删除实例
            bpmInstManager.deleteCascade(instIdArr[i], "");
        }
        return new JsonResult(true, "成功删除!");
    }

    // 新增申请单
    public void createStandardApply(JSONObject params) {
        String prefix = "SC-";
        String applyId = prefix + XcmgProjectUtil.getNowLocalDateStr("yyyyMMddHHmmssSSS") + (int)(Math.random() * 100);
        params.put("applyId", applyId);
        params.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        params.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        standardManagementDao.addStandardDemand(params);

    }

    private void demandProcess(String applyId, JSONArray demandArr) {
        if (demandArr == null || demandArr.isEmpty()) {
            return;
        }
        JSONObject param = new JSONObject();
        param.put("ids", new JSONArray());
        for (int index = 0; index < demandArr.size(); index++) {
            JSONObject oneObject = demandArr.getJSONObject(index);
            String id = oneObject.getString("id");
            String state = oneObject.getString("_state");
            if ("added".equals(state) || StringUtils.isBlank(id)) {
                // 新增
                oneObject.put("id", IdUtil.getId());
                oneObject.put("applyId", applyId);
                oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                oneObject.put("CREATE_TIME_", new Date());
                standardManagementDao.insertTeamDraft(oneObject);
            }else if ("modified".equals(state)) {
                oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                oneObject.put("UPDATE_TIME_", new Date());
                standardManagementDao.updateTeamDraft(oneObject);
            } else if ("removed".equals(state)) {
                // 删除
                param.getJSONArray("ids").add(oneObject.getString("id"));
            }
        }
        if (!param.getJSONArray("ids").isEmpty()) {
            standardManagementDao.deleteTeamDraft(param);
        }
    }


    public void updateStandardApply(JSONObject params) {
        params.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        params.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        if (StringUtils.isBlank(params.getString("sjcaTime"))) {
            params.put("sjcaTime", null);
        }
        if (StringUtils.isBlank(params.getString("sjzqTime"))) {
            params.put("sjzqTime", null);
        }
        if (StringUtils.isBlank(params.getString("sjpsTime"))) {
            params.put("sjpsTime", null);
        }
        if (StringUtils.isBlank(params.getString("sjbpTime"))) {
            params.put("sjbpTime", null);
        }
        if (StringUtils.isBlank(params.getString("sjyyTime"))) {
            params.put("sjyyTime", null);
        }
        standardManagementDao.updateStandardDemand(params);
        if (StringUtils.isNotBlank(params.getString("changeData"))) {
            JSONArray tdmcDataJson = JSONObject.parseArray(params.getString("changeData"));
            for (int i = 0; i < tdmcDataJson.size(); i++) {
                JSONObject oneObject = tdmcDataJson.getJSONObject(i);
                String state = oneObject.getString("_state");
                String tdmcId = oneObject.getString("id");
                if ("added".equals(state) || StringUtils.isBlank(tdmcId)) {
                    oneObject.put("id", IdUtil.getId());
                    oneObject.put("reviseInfoId", params.getString("id"));
                    oneObject.put("opinion", oneObject.getString("opinion"));
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", new Date());
                    standardManagementDao.insertBzxdssyj(oneObject);
                } else if ("modified".equals(state)) {
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", new Date());
                    standardManagementDao.updateBzxdssyj(oneObject);
                } else if ("removed".equals(state)) {
                    Map<String, Object> param = new HashMap<>();
                    param.put("id", oneObject.getString("id"));
                    standardManagementDao.deleteBzxdssyj(oneObject);
                }
            }
        }
        demandProcess(params.getString("id"), params.getJSONArray("changeDemandGrid"));


    }

    public JSONObject queryDemandDetail(String formId) {
        JSONObject result = standardManagementDao.queryDemandDetailById(formId);
        return result;
    }

    // 查询附件类型
    public List<JSONObject> queryStandardFileTypes(String stageKey) {
        return standardManagementDao.queryStandardFileTypes(stageKey);
    }

    public List<JSONObject> getStandardFileList(List<String> standardIdList, String stageKey) {
        List<JSONObject> njjdFileList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("standardIds", standardIdList);
        param.put("stageKey", stageKey);
        List<JSONObject> jsonObjects = standardManagementDao.queryStandardFileList(param);
        for (JSONObject json : jsonObjects) {
            if (json.get("CREATE_TIME_") != null) {
                json.put("CREATE_TIME_", DateUtil.formatDate((Date)json.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        return jsonObjects;
    }

    public void saveUploadFiles(HttpServletRequest request) {
        Map<String, String[]> parameters = request.getParameterMap();
        if (parameters == null || parameters.isEmpty()) {
            logger.warn("没有找到上传的参数");
            return;
        }
        // 多附件上传需要用到的MultipartHttpServletRequest
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        if (fileMap == null || fileMap.isEmpty()) {
            logger.warn("没有找到上传的文件");
            return;
        }
        String filePathBase = WebAppUtil.getProperty("standardMegFilePathBase");
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find standardMegFilePathBase");
            return;
        }
        try {
            String applyId = toGetParamVal(parameters.get("applyId"));
            String stageKey = toGetParamVal(parameters.get("typeId"));
            String id = IdUtil.getId();
            String fileName = toGetParamVal(parameters.get("fileName"));
            String fileSize = toGetParamVal(parameters.get("fileSize"));
            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();

            // 向下载目录中写入文件
            String filePath = filePathBase + File.separator + applyId;
            File pathFile = new File(filePath);
            if (!pathFile.exists()) {
                pathFile.mkdirs();
            }
            String suffix = CommonFuns.toGetFileSuffix(fileName);
            String fileFullPath = filePath + File.separator + id + "." + suffix;
            File file = new File(fileFullPath);
            FileCopyUtils.copy(mf.getBytes(), file);

            // 写入数据库
            JSONObject fileInfo = new JSONObject();
            fileInfo.put("id", id);
            fileInfo.put("fileName", fileName);
            fileInfo.put("fileSize", fileSize);
            fileInfo.put("reviseInfoId", applyId);
            fileInfo.put("fileTypeId", stageKey);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            standardManagementDao.addStandardFileInfos(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
    }

    /**
     * 查询征求意见信息
     *
     * @param applyId
     * @return
     */
    public List<JSONObject> getStandardList(String applyId, String isZqyj) {

        List<JSONObject> userList = new ArrayList<>();
        if (applyId != null && !"".equals(applyId)) {
            userList = standardManagementDao.getStandardList(applyId, ContextUtil.getCurrentUserId(), isZqyj);
        }
        return userList;
    }

    /**
     * 查询团队成员意见信息
     *
     * @param applyId
     * @return
     */
    public List<JSONObject> getTeamDraftList(String applyId, String isMkfzr) {

        List<JSONObject> userList = new ArrayList<>();
        if (applyId != null && !"".equals(applyId)) {
            userList = standardManagementDao.getTeamDraftList(applyId, ContextUtil.getCurrentUserId(), isMkfzr);
        }
        return userList;
    }

    // 保存（包括新增保存、编辑保存）
    public void savePublicStandard(JSONObject result, HttpServletRequest request) {
        try {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
            Map<String, String[]> parameters = request.getParameterMap();
            /*     MultipartFile fileObj = multipartRequest.getFile("standardFile");*/
            String applyId = RequestUtil.getString(request, "applyId");
            if (parameters == null || parameters.isEmpty()) {
                logger.error("表单内容为空！");
                result.put("message", "操作失败，表单内容为空！");
                result.put("success", false);
                return;
            }
            String id = parameters.get("id")[0];

            if ("".equals(id)) {

                // 用户信息添加
                JSONObject userInfo = new JSONObject();
                userInfo.put("id", IdUtil.getId());
                userInfo.put("reviseInfoId", applyId);
                userInfo.put("chapter", parameters.get("chapter")[0]);
                userInfo.put("opinion", parameters.get("opinion")[0]);
                userInfo.put("feedback", parameters.get("feedback")[0]);
                userInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                userInfo.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                userInfo.put("ifyj", parameters.get("ifyj")[0]);
                standardManagementDao.saveUserList(userInfo);
            } else {
                JSONObject userInfo = new JSONObject();
                userInfo.put("id", id);
                userInfo.put("chapter", parameters.get("chapter")[0]);
                userInfo.put("opinion", parameters.get("opinion")[0]);
                userInfo.put("feedback", parameters.get("feedback")[0]);
                userInfo.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                userInfo.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                userInfo.put("ifyj", parameters.get("ifyj")[0]);
                standardManagementDao.updateUserList(userInfo);
            }

        } catch (Exception e) {
            logger.error("Exception in savePublicStandard", e);
            result.put("message", "系统异常！");
            result.put("success", false);
        }
    }

    private String toGetParamVal(String[] paramValArr) {
        if (paramValArr == null || paramValArr.length == 0) {
            return null;
        }
        return paramValArr[0];
    }

    /**
     * 过滤当前任务筛选
     * 
     * @param standardList
     * @return
     */
    private List<JSONObject> filterListByTaskName(List<JSONObject> standardList, String filterTaskName) {
        List<JSONObject> result = new ArrayList<JSONObject>();
        if (standardList == null || standardList.isEmpty() || StringUtils.isBlank(filterTaskName)) {
            return result;
        }
        // 过滤
        for (JSONObject oneData : standardList) {
            String taskName = oneData.getString("taskName");
            if (StringUtils.isNotBlank(taskName) && taskName.contains(filterTaskName)) {
                result.add(oneData);
            }
        }
        return result;
    }

    public List<JSONObject> getSsyjList(String applyId) {
        JSONObject paramJson = new JSONObject();
        paramJson.put("applyId", applyId);
        List<JSONObject> reasonList = standardManagementDao.getSsyjList(paramJson);
        return reasonList;
    }

    // 计算项目风险,没有风险是0，延迟是1，不延迟是2，不涉及是3
    public void fenxianZDadd(List<JSONObject> standardList) throws ParseException {
        for (JSONObject standard : standardList) {
            if ("DRAFTED".equalsIgnoreCase(standard.getString("instStatus"))) {
                standard.put("hasRisk", 3);
            } else if ("SUCCESS_END".equalsIgnoreCase(standard.getString("instStatus"))) {
                standard.put("hasRisk", 0);
            } else if ("RUNNING".equalsIgnoreCase(standard.getString("instStatus"))) {
                if (StringUtils.isNotBlank(standard.getString("dqzt"))) {
                    if ("ca".equalsIgnoreCase(standard.getString("dqzt"))) {
                        String s1 = DateUtil.formatDate(standard.getDate("draftTime"), "yyyy-MM-dd");
                        String s2 = DateUtil.formatDate(new Date(), "yyyy-MM-dd");
                        int l1 = s1.compareTo(s2);
                        if (l1 >= 0) {
                            standard.put("hasRisk", 2);
                        } else {
                            standard.put("hasRisk", 1);
                        }
                    } else if ("zq".equalsIgnoreCase(standard.getString("dqzt"))) {
                        String s1 = DateUtil.formatDate(standard.getDate("solicitopinionsTime"), "yyyy-MM-dd");
                        String s2 = DateUtil.formatDate(new Date(), "yyyy-MM-dd");
                        int l1 = s1.compareTo(s2);
                        if (l1 >= 0) {
                            standard.put("hasRisk", 2);
                        } else {
                            standard.put("hasRisk", 1);
                        }
                    } else if ("ps".equalsIgnoreCase(standard.getString("dqzt"))) {
                        String s1 = DateUtil.formatDate(standard.getDate("reviewTime"), "yyyy-MM-dd");
                        String s2 = DateUtil.formatDate(new Date(), "yyyy-MM-dd");
                        int l1 = s1.compareTo(s2);
                        if (l1 >= 0) {
                            standard.put("hasRisk", 2);
                        } else {
                            standard.put("hasRisk", 1);
                        }
                    } else if ("bp".equalsIgnoreCase(standard.getString("dqzt"))) {
                        String s1 = DateUtil.formatDate(standard.getDate("reportforapprovalTime"), "yyyy-MM-dd");
                        String s2 = DateUtil.formatDate(new Date(), "yyyy-MM-dd");
                        int l1 = s1.compareTo(s2);
                        if (l1 >= 0) {
                            standard.put("hasRisk", 2);
                        } else {
                            standard.put("hasRisk", 1);
                        }
                    } else if ("yy".equalsIgnoreCase(standard.getString("dqzt"))) {
                        String s1 = DateUtil.formatDate(standard.getDate("applicationTime"), "yyyy-MM-dd");
                        String s2 = DateUtil.formatDate(new Date(), "yyyy-MM-dd");
                        int l1 = s1.compareTo(s2);
                        if (l1 >= 0) {
                            standard.put("hasRisk", 2);
                        } else {
                            standard.put("hasRisk", 1);
                        }
                    }
                } else {
                    standard.put("hasRisk", 0);
                }
            } else {
                standard.put("hasRisk", 3);
            }
        }
    }

    public List<JSONObject> revisePlanList(HttpServletRequest request) {
        Map<String, Object> params = new HashMap<>();
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
        List<JSONObject> plans = standardManagementDao.queryRevisePlan(params);
        for (JSONObject jsonObject : plans) {
            if (StringUtils.isNotBlank(jsonObject.getString("CREATE_TIME_"))) {
                jsonObject.put("CREATE_TIME_", DateFormatUtil.format(jsonObject.getDate("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        return plans;
    }

    public JsonResult revisePlanSave(String requestBody) {
        JsonResult result = new JsonResult(true, "");
        JSONArray changedDataArray = JSONObject.parseArray(requestBody);
        if (changedDataArray == null || changedDataArray.isEmpty()) {
            logger.warn("数据为空");
            result.setMessage("数据为空！");
            return result;
        }
        try {
            for (int i = 0; i < changedDataArray.size(); i++) {
                JSONObject oneObject = changedDataArray.getJSONObject(i);
                String state = oneObject.getString("_state");
                if ("added".equals(state)) {
                    // 新增
                    oneObject.put("id", IdUtil.getId());
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", new Date());
                    standardManagementDao.insertRevisePlan(oneObject);
                } else if ("modified".equals(state)) {
                    // 修改
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", new Date());
                    standardManagementDao.updateRevisePlan(oneObject);
                }
            }
            result.setMessage("数据保存成功！");
        } catch (Exception e) {
            logger.error("Exception in revisePlanSave", e);
            result.setMessage("系统异常，保存失败！");
        }
        return result;
    }

    /**
     * 各部门标准制修订完成情况报表
     * 
     * @param request
     * @return
     */
    public JsonResult queryReviseOverviewByDept(HttpServletRequest request) {
        DecimalFormat df = new DecimalFormat("0.00");
        String startTime = RequestUtil.getString(request, "startTime", "");
        String endTime = RequestUtil.getString(request, "endTime", "");
        Map<String, Object> resultMap = new HashMap<>();
        JSONObject labelObject = new JSONObject();
        labelObject.put("show", "true");
        labelObject.put("position", "top");
        // 时间段内各部门应完成的总数
        Map<String, Object> params = new HashMap<>();
        params.put("startYearMonth", startTime.substring(0, 7));
        params.put("endYearMonth", endTime.substring(0, 7));
        List<JSONObject> revisePlanDeptList = standardManagementDao.queryRevisePlanTotal(params);
        if (revisePlanDeptList == null || revisePlanDeptList.isEmpty()) {
            List<JSONObject> list = commonInfoDao.getDeptInfoByDeptName(RdmConst.JSZX_NAME);
            String jszxId = list.get(0).getString("GROUP_ID_");
            revisePlanDeptList = commonInfoDao.queryDeptUnderJSZX(jszxId);
            for (JSONObject jsonObject : revisePlanDeptList) {
                jsonObject.put("totalNum", 0);
                jsonObject.put("deptId", jsonObject.getString("GROUP_ID_"));
                jsonObject.put("deptName", jsonObject.getString("NAME_"));
            }
        }
        Map<String, JSONObject> deptId2Obj = new LinkedHashMap<>();
        for (JSONObject jsonObject : revisePlanDeptList) {
            jsonObject.put("finishNum", 0);
            jsonObject.put("finishRate", 0);
            deptId2Obj.put(jsonObject.getString("deptId"), jsonObject);
        }

        // 时间段内各部门完成的数量
        params.clear();
        params.put("startTime", startTime + " 00:00:00");
        params.put("endTime", endTime + " 23:59:59");
        params.put("deptIds", deptId2Obj.keySet());
        List<JSONObject> finishList = standardManagementDao.queryReviseDeptFinish(params);
        if (finishList != null && !finishList.isEmpty()) {
            for (JSONObject oneData : finishList) {
                JSONObject totalObj = deptId2Obj.get(oneData.getString("deptId"));
                totalObj.put("finishNum", oneData.getIntValue("finishNum"));
                totalObj.put("finishRate", Double.parseDouble(
                    df.format(totalObj.getIntValue("finishNum") * 100.0 / totalObj.getIntValue("totalNum"))));
            }
        }
        List<JSONObject> deptObject = new ArrayList<>(deptId2Obj.values());
        // 完成率从小到大排序

        // 图表数据
        List<JSONObject> seriesData = new ArrayList<>();
        // x轴数据
        List<String> xAxisData = new ArrayList<>();
        // y轴数据(完成数量)
        List<Integer> finishYAxisData = new ArrayList<>();
        // y轴数据(总数)
        List<Integer> totalYAxisData = new ArrayList<>();
        // y轴数据（完成率）
        List<Double> rateYAxisData = new ArrayList<>();
        for (JSONObject oneData : deptObject) {
            xAxisData.add(oneData.getString("deptName"));
            finishYAxisData.add(oneData.getIntValue("finishNum"));
            rateYAxisData.add(oneData.getDoubleValue("finishRate"));
            totalYAxisData.add(oneData.getIntValue("totalNum"));
        }
        // legend
        List<String> legendData = new ArrayList<>();
        JSONObject threeSerie = new JSONObject();
        threeSerie.put("name", "计划完成数量");
        threeSerie.put("type", "bar");
        threeSerie.put("label", labelObject);
        threeSerie.put("data", totalYAxisData);
        legendData.add(threeSerie.getString("name"));
        seriesData.add(threeSerie);
        JSONObject oneSerie = new JSONObject();
        oneSerie.put("name", "实际完成数量");
        oneSerie.put("type", "bar");
        oneSerie.put("barGap", "0");
        oneSerie.put("label", labelObject);
        oneSerie.put("data", finishYAxisData);
        legendData.add(oneSerie.getString("name"));
        seriesData.add(oneSerie);
        JSONObject twoSerie = new JSONObject();
        twoSerie.put("name", "总量完成率");
        twoSerie.put("type", "line");
        twoSerie.put("yAxisIndex", 1);
        twoSerie.put("label", new JSONObject() {
            {
                put("show", true);
                put("position", "top");
                put("color", "black");
                put("formatter", "{c}%");
            }
        });
        twoSerie.put("data", rateYAxisData);
        legendData.add(twoSerie.getString("name"));
        seriesData.add(twoSerie);
        resultMap.put("legendData", legendData);
        resultMap.put("xAxisData", xAxisData);
        resultMap.put("series", seriesData);
        return JsonResultUtil.success(resultMap);
    }

    public JsonResult queryYearOverviewByDept(HttpServletRequest request) {
        DecimalFormat df = new DecimalFormat("0.00");
        String endTime = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String nowYearStart = DateFormatUtil.getNowByString("yyyy") + "-01-01";
        String nowYearEnd = DateFormatUtil.getNowByString("yyyy") + "-12-31";
        Map<String, Object> resultMap = new HashMap<>();
        JSONObject labelObject = new JSONObject();
        labelObject.put("show", "true");
        labelObject.put("position", "top");
        // 时间段内各部门应完成的总数
        Map<String, Object> params = new HashMap<>();
        params.put("startYearMonth", nowYearStart);
        params.put("endYearMonth", nowYearEnd);
        List<JSONObject> revisePlanDeptList = standardManagementDao.queryYearTotal(params);
        if (revisePlanDeptList == null || revisePlanDeptList.isEmpty()) {
            return new JsonResult();
        }
        Map<String, JSONObject> deptId2Obj = new LinkedHashMap<>();
        for (JSONObject jsonObject : revisePlanDeptList) {
            jsonObject.put("finishNum", 0);
            jsonObject.put("finishRate", 0);
            deptId2Obj.put(jsonObject.getString("deptId"), jsonObject);
        }

        // 时间段内各部门完成的数量
        params.clear();
        params.put("startTime", nowYearStart + " 00:00:00");
        params.put("endTime", endTime + " 23:59:59");
        params.put("deptIds", deptId2Obj.keySet());
        List<JSONObject> finishList = standardManagementDao.queryReviseDeptFinish(params);
        if (finishList != null && !finishList.isEmpty()) {
            for (JSONObject oneData : finishList) {
                JSONObject totalObj = deptId2Obj.get(oneData.getString("deptId"));
                totalObj.put("finishNum", oneData.getIntValue("finishNum"));
                totalObj.put("finishRate", Double.parseDouble(
                    df.format(totalObj.getIntValue("finishNum") * 100.0 / totalObj.getIntValue("totalNum"))));
            }
        }
        List<JSONObject> deptObject = new ArrayList<>(deptId2Obj.values());

        // 图表数据
        List<JSONObject> seriesData = new ArrayList<>();
        // x轴数据
        List<String> xAxisData = new ArrayList<>();
        // y轴数据(完成数量)
        List<Integer> finishYAxisData = new ArrayList<>();
        // y轴数据(总数)
        List<Integer> totalYAxisData = new ArrayList<>();
        // y轴数据（完成率）
        List<Double> rateYAxisData = new ArrayList<>();
        for (JSONObject oneData : deptObject) {
            xAxisData.add(oneData.getString("deptName"));
            finishYAxisData.add(oneData.getIntValue("finishNum"));
            rateYAxisData.add(oneData.getDoubleValue("finishRate"));
            totalYAxisData.add(oneData.getIntValue("totalNum"));
        }
        // legend
        List<String> legendData = new ArrayList<>();
        JSONObject threeSerie = new JSONObject();
        threeSerie.put("name", "计划完成数量");
        threeSerie.put("type", "bar");
        threeSerie.put("label", labelObject);
        threeSerie.put("data", totalYAxisData);
        legendData.add(threeSerie.getString("name"));
        seriesData.add(threeSerie);
        JSONObject oneSerie = new JSONObject();
        oneSerie.put("name", "实际完成数量");
        oneSerie.put("type", "bar");
        oneSerie.put("barGap", "0");
        oneSerie.put("label", labelObject);
        oneSerie.put("data", finishYAxisData);
        legendData.add(oneSerie.getString("name"));
        seriesData.add(oneSerie);
        JSONObject twoSerie = new JSONObject();
        twoSerie.put("name", "总量完成率");
        twoSerie.put("type", "line");
        twoSerie.put("yAxisIndex", 1);
        twoSerie.put("label", new JSONObject() {
            {
                put("show", true);
                put("position", "top");
                put("color", "black");
                put("formatter", "{c}%");
            }
        });
        twoSerie.put("data", rateYAxisData);
        legendData.add(twoSerie.getString("name"));
        seriesData.add(twoSerie);
        resultMap.put("legendData", legendData);
        resultMap.put("xAxisData", xAxisData);
        resultMap.put("series", seriesData);
        return JsonResultUtil.success(resultMap);
    }

    public JsonResult queryJdOverviewByDept(HttpServletRequest request) {
        Map<String, Object> params = new HashMap<>();
        DecimalFormat df = new DecimalFormat("0.00");
        String uIdStr = RequestUtil.getString(request, "deptId");
        if (StringUtils.isNotBlank(uIdStr)) {
            String[] deptIds = uIdStr.split(",");
            List<String> deptId = Arrays.asList(deptIds);
            params.put("deptIds", deptId);
        }
        String nowYearStart = DateFormatUtil.getNowByString("yyyy");
        params.put("nowYearStart", nowYearStart);
        List<JSONObject> revisePlanDeptList = standardManagementDao.queryJdTotal(params);
        if (revisePlanDeptList == null || revisePlanDeptList.isEmpty()) {
            return new JsonResult();
        }
        Map<String, JSONObject> deptId2Obj = new LinkedHashMap<>();
        int n = 0;
        for (JSONObject jsonObject : revisePlanDeptList) {
            n = n + jsonObject.getIntValue("totalNum");
            jsonObject.put("finishNum", 0);
            jsonObject.put("finishRate", 0);
            deptId2Obj.put(jsonObject.getString("belongJd"), jsonObject);
        }
        JSONObject year = new JSONObject();
        year.put("finishNum", 0);
        year.put("finishRate", 0);
        year.put("belongJd", "年度累计");
        year.put("totalNum", String.valueOf(n));
        deptId2Obj.put("年度累计", year);
        // 各季度完成的数量
        String nowYear = DateFormatUtil.getNowByString("yyyy");
        params.put("nowyear", nowYear);
        List<JSONObject> finishList = standardManagementDao.queryJdFinish(params);
        if (finishList != null && !finishList.isEmpty()) {
            for (JSONObject oneData : finishList) {
                if (StringUtils.isNotBlank(oneData.getString("jd")) && "1".equals(oneData.getString("jd"))) {
                    oneData.put("jd", "1季度");
                } else if (StringUtils.isNotBlank(oneData.getString("jd")) && "2".equals(oneData.getString("jd"))) {
                    oneData.put("jd", "2季度");
                } else if (StringUtils.isNotBlank(oneData.getString("jd")) && "3".equals(oneData.getString("jd"))) {
                    oneData.put("jd", "3季度");
                } else if (StringUtils.isNotBlank(oneData.getString("jd")) && "4".equals(oneData.getString("jd"))) {
                    oneData.put("jd", "4季度");
                } else if (StringUtils.isNotBlank(oneData.getString("jd")) && nowYear.equals(oneData.getString("jd"))) {
                    oneData.put("jd", "年度累计");
                }
                JSONObject totalObj = deptId2Obj.get(oneData.getString("jd"));
                totalObj.put("finishNum", oneData.getIntValue("finishNum"));
                totalObj.put("finishRate", Double.parseDouble(
                    df.format(totalObj.getIntValue("finishNum") * 100.0 / totalObj.getIntValue("totalNum"))));
            }
        }
        List<JSONObject> deptObject = new ArrayList<>(deptId2Obj.values());

        // 图表数据
        List<JSONObject> seriesData = new ArrayList<>();
        // x轴数据
        List<String> xAxisData = new ArrayList<>();
        // y轴数据(完成数量)
        List<Integer> finishYAxisData = new ArrayList<>();
        // y轴数据(总数)
        List<Integer> totalYAxisData = new ArrayList<>();
        // y轴数据（完成率）
        List<Double> rateYAxisData = new ArrayList<>();
        for (JSONObject oneData : deptObject) {
            xAxisData.add(oneData.getString("belongJd"));
            finishYAxisData.add(oneData.getIntValue("finishNum"));
            rateYAxisData.add(oneData.getDoubleValue("finishRate"));
            totalYAxisData.add(oneData.getIntValue("totalNum"));
        }
        // legend
        List<String> legendData = new ArrayList<>();
        JSONObject labelObject = new JSONObject();
        labelObject.put("show", "true");
        labelObject.put("position", "top");
        JSONObject oneSerie = new JSONObject();
        JSONObject threeSerie = new JSONObject();
        threeSerie.put("name", "计划完成数量");
        threeSerie.put("type", "bar");
        threeSerie.put("label", labelObject);
        threeSerie.put("data", totalYAxisData);
        legendData.add(threeSerie.getString("name"));
        seriesData.add(threeSerie);
        oneSerie.put("name", "实际完成数量");
        oneSerie.put("type", "bar");
        oneSerie.put("barGap", "0");
        oneSerie.put("label", labelObject);
        oneSerie.put("data", finishYAxisData);
        legendData.add(oneSerie.getString("name"));
        seriesData.add(oneSerie);
        JSONObject twoSerie = new JSONObject();
        twoSerie.put("name", "总量完成率");
        twoSerie.put("type", "line");
        twoSerie.put("yAxisIndex", 1);
        twoSerie.put("label", new JSONObject() {
            {
                put("show", true);
                put("position", "top");
                put("color", "black");
                put("formatter", "{c}%");
            }
        });
        twoSerie.put("data", rateYAxisData);
        legendData.add(twoSerie.getString("name"));
        seriesData.add(twoSerie);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("legendData", legendData);
        resultMap.put("xAxisData", xAxisData);
        resultMap.put("series", seriesData);
        return JsonResultUtil.success(resultMap);
    }

    public JsonPageResult<?> queryDept(HttpServletRequest request) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        String nowYear = DateFormatUtil.getNowByString("yyyy");
        params.put("nowyear", nowYear);
        List<JSONObject> typeList = standardManagementDao.queryDept(params);
        result.setData(typeList);
        return result;
    }
}
