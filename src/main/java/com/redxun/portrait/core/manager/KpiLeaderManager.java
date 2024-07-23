package com.redxun.portrait.core.manager;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.BpmInst;
import com.redxun.bpm.core.entity.ProcessMessage;
import com.redxun.bpm.core.entity.ProcessStartCmd;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.query.Page;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.ExceptionUtil;
import com.redxun.org.api.model.IUser;
import com.redxun.org.api.service.UserService;
import com.redxun.portrait.core.dao.KpiLeaderDao;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
import com.redxun.xcmgbudget.core.dao.BudgetMonthUserDao;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DecimalFormat;
import java.util.*;

@Service
public class KpiLeaderManager {
    private static final Logger logger = LoggerFactory.getLogger(KpiLeaderManager.class);

    @Autowired
    private KpiLeaderDao kpiLeaderDao;
    @Autowired
    private UserService userService;
    @Autowired
    BpmInstManager bpmInstManager;
    @Autowired
    private CommonInfoDao commonInfoDao;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private BudgetMonthUserDao budgetMonthUserDao;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;

    /**
     * 新增或者更新KPI
     *
     * @param formDataObj
     * @param result
     */
    public void addOrUpdateKpiYear(JSONObject formDataObj, JsonResult result) {
        String kpiId = formDataObj.getString("id");
        if (StringUtils.isBlank(kpiId)) {
            addKpiYear(formDataObj, result);
        } else {
            updateKpiYear(formDataObj, result);
        }
    }

    // 新增KPI，需要判断如果被考核部门是“所有部门”，则需要将KPI自动复制到除了“徐工挖机”和“所有部门”之外的部门中
    private void addKpiYear(JSONObject formDataObj, JsonResult result) {
        // 基本信息表
        String kpiId = IdUtil.getId();
        formDataObj.put("id", kpiId);
        formDataObj.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formDataObj.put("CREATE_TIME_", new Date());
        kpiLeaderDao.addKpiBaseInfo(formDataObj);
    }

    // 更新某个KPI的信息
    private void updateKpiYear(JSONObject formDataObj, JsonResult result) {
        // 更新kpi基本信息表
        formDataObj.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formDataObj.put("UPDATE_TIME_", new Date());
        kpiLeaderDao.updateKpiBaseInfo(formDataObj);
    }

    public JsonPageResult<?> getKpiYearList(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)) {
            params.put("sortField", sortField);
            params.put("sortOrder", sortOrder);
        } else {
            params.put("sortField", "year");
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
        boolean doPage = false;
        if (StringUtils.isNotBlank(request.getParameter("pageIndex"))
                && StringUtils.isNotBlank(request.getParameter("pageSize"))) {
            int pageIndex = RequestUtil.getInt(request, "pageIndex", 0);
            int pageSize = RequestUtil.getInt(request, "pageSize", Page.DEFAULT_PAGE_SIZE);
            params.put("startIndex", "0" + pageIndex * pageSize);
            params.put("pageSize", "0" + pageSize);
            doPage = true;
        }
        List<JSONObject> kpiYearList = kpiLeaderDao.queryKpiYearList(params);
        result.setData(kpiYearList);
        // 查询总数
        if (doPage) {
            int countKpiYearList = kpiLeaderDao.countKpiYearList(params);
            result.setTotal(countKpiYearList);
        }
        return result;
    }

    // 查询kpiYear的基本信息
    public JSONObject getKpiYearJson(String kpiId) {
        JSONObject kpiYear = new JSONObject();
        // 查询基本信息
        Map<String, Object> params = new HashMap<>();
        params.put("id", kpiId);
        List<JSONObject> kpiYearList = kpiLeaderDao.queryKpiYearList(params);
        if (kpiYearList != null && !kpiYearList.isEmpty()) {
            kpiYear = kpiYearList.get(0);
        }
        return kpiYear;
    }

    // 删除kpi及相关的所有数据
    public JsonResult deleteKpiYearList(List<String> ids) {
        JsonResult result = new JsonResult(true, "成功删除!");
        // 判断待删除的KPi是否已经分解，如果是则不允许删除
        List<String> realDelIds = new ArrayList<>();
        for (String id : ids) {
            realDelIds.add(id);
        }
        if (!realDelIds.isEmpty()) {
            // 删除kpi_baseInfo
            kpiLeaderDao.batchDeleteKpiBaseInfo(realDelIds);
        }
        return result;
    }

    //人员列表维护
    public void saveUsersList(JsonResult result, String changeGridDataStr) {
        try {
            JSONArray changeGridDataJson = JSONObject.parseArray(changeGridDataStr);
            if (changeGridDataJson == null || changeGridDataJson.isEmpty()) {
                logger.warn("gridData is blank");
                result.setSuccess(false);
                result.setMessage("请求体为空");
                return;
            }
            for (int i = 0; i < changeGridDataJson.size(); i++) {
                JSONObject oneObject = changeGridDataJson.getJSONObject(i);
                String state = oneObject.getString("_state");
                String id = oneObject.getString("id");
                if ("added".equals(state) || StringUtils.isBlank(id)) {
                    // 新增
                    oneObject.put("id", IdUtil.getId());
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", new Date());
                    kpiLeaderDao.saveUsersList(oneObject);
                } else if ("modified".equals(state)) {
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", new Date());
                    kpiLeaderDao.updateUsersList(oneObject);
                } else if ("removed".equals(state)) {
                    // 删除
                    kpiLeaderDao.deleteUsersList(oneObject);
                }
            }

        } catch (Exception e) {
            logger.error("Exception in saveUsersList");
            result.setSuccess(false);
            result.setMessage("保存数据异常");
            return;
        }
    }

    public JsonPageResult<?> getUsersList(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)) {
            params.put("sortField", sortField);
            params.put("sortOrder", sortOrder);
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
        boolean doPage = false;
        if (StringUtils.isNotBlank(request.getParameter("pageIndex"))
                && StringUtils.isNotBlank(request.getParameter("pageSize"))) {
            int pageIndex = RequestUtil.getInt(request, "pageIndex", 0);
            int pageSize = RequestUtil.getInt(request, "pageSize", Page.DEFAULT_PAGE_SIZE);
            params.put("startIndex", "0" + pageIndex * pageSize);
            params.put("pageSize", "0" + pageSize);
            doPage = true;
        }
        List<JSONObject> usersList = kpiLeaderDao.queryUsersList(params);
        result.setData(usersList);
        // 查询总数
        if (doPage) {
            int countUsersList = kpiLeaderDao.countUsersList(params);
            result.setTotal(countUsersList);
        }
        return result;
    }

    // 新增或者更新月度绩效表单
    public JsonResult createAllProcess(HttpServletRequest request) throws Exception {
        //定义返回类型
        JsonResult result = new JsonResult(true);
        //获取前台传输数据
        String yearMonth = RequestUtil.getString(request, "yearMonth", "");
        Map<String, Object> param = new HashMap<>();
        param.put("yearMonth",yearMonth);
        Integer num = kpiLeaderDao.checkKpiYearMonth(param);
        if (num > 0){
            result.setSuccess(true);
            result.setMessage(yearMonth+"挖机研究院所长月度绩效已创建，无法重复提交");
            return result;
        }
        //定义流程处理
        ProcessMessage handleMessage = new ProcessMessage();
        try {
            //将管理员账号设置为当前用户
            IUser user = userService.getByUsername("admin");
            ContextUtil.setCurUser(user);
            //获取流程人员信息
            Map<String, Object> params = new HashMap<>();
            List<JSONObject> userList = kpiLeaderDao.queryUsersList(params);
            //封装业务表数据
            for (JSONObject userContent : userList){
                //定义业务主表单数据
                ProcessHandleHelper.setProcessMessage(handleMessage);
                ProcessStartCmd startCmd = new ProcessStartCmd();
                userContent.put("id","");
                userContent.put("yearMonth",yearMonth);
                userContent.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                userContent.put("CREATE_TIME_", new Date());
                startCmd.setSolId(kpiLeaderDao.querySolIdByKey());
                startCmd.setJsonData(userContent.toJSONString());
                startCmd.setJumpType("SKIP");
                bpmInstManager.doStartProcess(startCmd);
            }
        }catch (Exception ex) {
            logger.error(ExceptionUtil.getExceptionMessage(ex));
            if (handleMessage.getErrorMsges().size() == 0) {
                handleMessage.addErrorMsg(ex.getMessage());
            }
        }finally {
            // 在处理过程中，是否有错误的消息抛出
            if (handleMessage.getErrorMsges().size() > 0) {
                result.setSuccess(false);
                result.setMessage("数据提交失败!");
                result.setData(handleMessage.getErrors());
            } else {
                result.setSuccess(true);
                result.setMessage("数据提交成功，请在相应页面进行确认！");
            }
            ProcessHandleHelper.clearProcessCmd();
            ProcessHandleHelper.clearProcessMessage();
            return result;
        }
    }

    // 新增或者更新月度预算表单
    public JsonResult saveFjScore(HttpServletRequest request) throws Exception {
        JsonResult result = null;
        try {
            //定义返回类型
            result = new JsonResult(true);
            //获取前台传输数据
            String id = RequestUtil.getString(request, "id", "");
            String fjScore = RequestUtil.getString(request, "fjScore", "");
            //保存
            Map<String, Object> params = new HashMap<>();
            params.put("id",id);
            params.put("fjScore",fjScore);
            kpiLeaderDao.updateFjScore(params);
            result.setSuccess(true);
            result.setMessage("保存成功！");
        } catch (Exception e) {
//            e.printStackTrace();
            logger.error("保存失败！",e);
            result.setSuccess(false);
            result.setMessage("保存失败!");
        }
        return result;
    }

    public void createKpiLeaderFlow(JSONObject formData) {
        //0.保存流程基础表信息
        formData.put("id",IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        kpiLeaderDao.insertKpiMonthBaseInfo(formData);
        //1.获取被考核人信息
        String bkhUserId = formData.getString("bkhUserId");
        String yearMonth = formData.getString("yearMonth");
        //2.根据被考核人信息获取kpi条目
        Map<String, Object> params = new HashMap<>();
        params.put("bkhUserId",bkhUserId);
        params.put("year",yearMonth.substring(0,4));
        List<JSONObject> kpiList = kpiLeaderDao.findKpiListByBkhId(params);
        //3.创建条目
        for (JSONObject kpiLine : kpiList){
            //3.1建立创建基本信息
            kpiLine.put("relKpiId",kpiLine.getString("id"));
            kpiLine.put("id",IdUtil.getId());
            kpiLine.put("kpiId",formData.getString("id"));
            kpiLine.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            kpiLine.put("CREATE_TIME_", new Date());
            //3.2根据年月信息引用季度
            if ("季度".equalsIgnoreCase(kpiLine.getString("period"))){
                //获取当前月份
                String month = formData.getString("yearMonth").substring(5);
                Map<String, Object> paramMonth = new HashMap<>();
                paramMonth.put("month",month);
                List<JSONObject> monthLine = kpiLeaderDao.getKplLeaderMonth(paramMonth);
                //如果是复制月份，则查询上月的信息
                if (monthLine.size() == 1){
                    String toMonth = monthLine.get(0).getString("toMonth");
                    String toYear = kpiLine.getString("year");
                    if (month.equalsIgnoreCase("01")){
                        Integer y2 = Integer.parseInt(toYear) - 1;
                        toYear = y2.toString();
                    }
                    String toYearMonth = toYear +"-"+toMonth;
                    //根据年月，被考核人员信息，查询要继承的历史数据
                    Map<String, Object> paramCopy = new HashMap<>();
                    paramCopy.put("yearMonth",toYearMonth);
                    paramCopy.put("bkhUserId",bkhUserId);
                    paramCopy.put("relKpiId",kpiLine.getString("relKpiId"));
                    List<JSONObject> mess = kpiLeaderDao.getKplLeaderCopyDetail(paramCopy);
                    if (mess.size() == 1){
                        //查询历史指标分数
                        String score = mess.get(0).getString("score");
                        //将分数写入新月份，打上执考标志位
                        kpiLine.put("score", score);
                        kpiLine.put("copyType", "1");
                    }
                }
            }

            //新建与复制
            //1.查询当前需要创建流程的年月和考核人，属于属不属于复制月份，属于则构建复制信息（打上执考标志位），不属于则构建新建信息
            //复制时无历史记录月份，新建处理
            //2.属于复制月份，但之前无考核信息，则作为新建处理，不打执考标志位

            kpiLeaderDao.insertKpiMonthDetail(kpiLine);
        }
    }

    public void updateKpiLeaderFlow(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
    }

    public JsonPageResult<?> kpiLeaderFlowList(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
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
        List<JSONObject> list = kpiLeaderDao.queryKpiLeaderFlowList(params);
        // 查询当前处理人
        rdmZhglUtil.setTaskInfo2Data(list, ContextUtil.getCurrentUserId());
//        setTaskCurrentUser(list);
        // 过滤
        List<JSONObject> dataList = filterListByDepRole(list);
        //总分汇总
        setTotalScore(dataList);
        //封装
        List<JSONObject> finalSubProjectList = new ArrayList<>();
        // 根据分页进行subList截取
        int pageIndex = RequestUtil.getInt(request, "pageIndex", 0);
        int pageSize = RequestUtil.getInt(request, "pageSize", Page.DEFAULT_PAGE_SIZE);
        int startSubListIndex = pageIndex * pageSize;
        int endSubListIndex = startSubListIndex + pageSize;
        if (startSubListIndex < dataList.size()) {
            finalSubProjectList = dataList.subList(startSubListIndex,
                    endSubListIndex < dataList.size() ? endSubListIndex : dataList.size());
        }
        if (finalSubProjectList != null && !finalSubProjectList.isEmpty()) {
            for (Map<String, Object> oneProject : finalSubProjectList) {
                if (oneProject.get("CREATE_TIME_") != null) {
                    oneProject.put("CREATE_TIME_",
                            DateUtil.formatDate((Date)oneProject.get("CREATE_TIME_"), "yyyy-MM-dd"));
                }
            }
        }
        result.setData(finalSubProjectList);
        result.setTotal(dataList.size());
        return result;
    }

    public JsonPageResult<?> kpiLeaderFlowReportList(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
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
        List<JSONObject> dataList = kpiLeaderDao.queryKpiLeaderFlowList(params);
        rdmZhglUtil.setTaskInfo2Data(dataList, ContextUtil.getCurrentUserId());
        // 查询当前处理人
//        setTaskCurrentUser(dataList);
        // 过滤
//        List<JSONObject> dataList = filterListByDepRole(list);
        //总分汇总
        setTotalScore(dataList);
        //封装
        List<JSONObject> finalSubProjectList = new ArrayList<>();
        // 根据分页进行subList截取
        int pageIndex = RequestUtil.getInt(request, "pageIndex", 0);
        int pageSize = RequestUtil.getInt(request, "pageSize", Page.DEFAULT_PAGE_SIZE);
        int startSubListIndex = pageIndex * pageSize;
        int endSubListIndex = startSubListIndex + pageSize;
        if (startSubListIndex < dataList.size()) {
            finalSubProjectList = dataList.subList(startSubListIndex,
                    endSubListIndex < dataList.size() ? endSubListIndex : dataList.size());
        }
        if (finalSubProjectList != null && !finalSubProjectList.isEmpty()) {
            for (Map<String, Object> oneProject : finalSubProjectList) {
                if (oneProject.get("CREATE_TIME_") != null) {
                    oneProject.put("CREATE_TIME_",
                            DateUtil.formatDate((Date)oneProject.get("CREATE_TIME_"), "yyyy-MM-dd"));
                }
            }
        }
        result.setData(finalSubProjectList);
        result.setTotal(dataList.size());
        return result;
    }

    private List<JSONObject> setTotalScore(List<JSONObject> kpiList) {
        List<JSONObject> result = new ArrayList<JSONObject>();
        if (kpiList == null || kpiList.isEmpty()) {
            return result;
        }
        for (JSONObject oneProject : kpiList) {
            String id = oneProject.getString("id");
            JSONObject list = getDetailJson(id);
            oneProject.put("scoreTotal",list.getString("score"));
        }
        return result;
    }

    private List<JSONObject> filterListByDepRole(List<JSONObject> kpiList) {
        List<JSONObject> result = new ArrayList<JSONObject>();
        if (kpiList == null || kpiList.isEmpty()) {
            return result;
        }
        // 管理员查看所有的包括草稿数据
        if ("admin".equals(ContextUtil.getCurrentUser().getUserNo())) {
            return kpiList;
        }

        String userId = ContextUtil.getCurrentUserId();

        // 过滤
        for (JSONObject oneProject : kpiList) {
            // 被考核人员查看
            if (oneProject.getString("bkhUserId").equalsIgnoreCase(userId)) {
                result.add(oneProject);
                continue;
            }
            //流程处理人查看
            if (StringUtils.isNotBlank(oneProject.getString("myTaskId"))) {
                result.add(oneProject);
                continue;
            }
            //执考人员查看
        }
        return result;
    }


    public void setTaskCurrentUser(List<JSONObject> objList) {
        Map<String, Map<String, Object>> taskId2Pro = new HashMap<>();
        for (JSONObject onePro : objList) {
            if (onePro.get("taskId") != null && StringUtils.isNotBlank(onePro.getString("taskId"))) {
                taskId2Pro.put(onePro.getString("taskId"), onePro);
            }
        }
        if (taskId2Pro.isEmpty()) {
            return;
        }
        Map<String, Object> queryTaskExecutors = new HashMap<>();
        queryTaskExecutors.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        queryTaskExecutors.put("taskIds", new ArrayList<String>(taskId2Pro.keySet()));
        List<Map<String, String>> task2Executors = xcmgProjectOtherDao.queryTaskExecutorsByTaskIds(queryTaskExecutors);
        if (task2Executors == null || task2Executors.isEmpty()) {
            logger.warn("can't find task executors");
            return;
        }
        for (Map<String, String> oneTaskExecutor : task2Executors) {
            String taskId = oneTaskExecutor.get("taskId");
            String executorName = oneTaskExecutor.get("currentProcessUser");
            String executorId = oneTaskExecutor.get("currentProcessUserId");
            Map<String, Object> needPutPro = taskId2Pro.get(taskId);
            if (needPutPro.get("currentProcessUser") != null
                    && StringUtils.isNotBlank(needPutPro.get("currentProcessUser").toString())) {
                executorName += "," + needPutPro.get("currentProcessUser").toString();
                executorId += "," + needPutPro.get("currentProcessUserId").toString();
            }
            needPutPro.put("currentProcessUser", executorName);
            needPutPro.put("currentProcessUserId", executorId);
        }
    }

    public JsonResult deleteKpiLeaderMonthFlow(String[] idArr, String[] instIdArr) {
        if (idArr.length != instIdArr.length) {
            return new JsonResult(false, "操作失败，表单和流程实例数据不一致！");
        }
        for (int i = 0; i < idArr.length; i++) {
            String id = idArr[i];
            // 删除流程主表数据
            kpiLeaderDao.delKpiLeaderBaseFlowById(id);
            // 删除流程预算数据
            kpiLeaderDao.delKpiLeaderDetailFlowById(id);
            // 删除实例
            bpmInstManager.deleteCascade(instIdArr[i], "");
            // 删除任务表
            kpiLeaderDao.delTaskData(instIdArr[i]);
        }
        return new JsonResult(true, "成功删除!");
    }

    public JSONObject getDetailJson(String id) {
        JSONObject list = new JSONObject();
        Map<String, Object> params = new HashMap<>();
        params.put("id",id);
        //1.基础信息
        List<JSONObject> listBaseInfo = kpiLeaderDao.getKpiLeaderBaseById(params);
        if (listBaseInfo.size() > 0){
            list.put("baseInfo",listBaseInfo.get(0));
        }
        //2.列表信息
        List<JSONObject> listDetail = kpiLeaderDao.getKpiLeaderDetailById(params);
        List<JSONObject> detailList = detailListByDepRole(listDetail);
        if (detailList.size() > 0){
            list.put("detail",detailList);
        }
        //3.绩效总分
        double totalScore = 0.0;
        for (JSONObject scoreLine : listDetail){
            if (scoreLine.getString("score") == null || scoreLine.getString("score").isEmpty()) {
                list.put("score","");
                return list;
            }
            String score = scoreLine.getString("score");
            String weight = scoreLine.getString("weight");
//            String targetUpperValue = scoreLine.getString("targetUpperValue");
//            String targetLowerValue = scoreLine.getString("targetLowerValue");
            double metricScore = Double.parseDouble(score);
            double weightScore = Double.parseDouble(weight);
            //目标上下限值不参与考评计算，总分修改为加权得分与附加分总和
//            double targetUpper = Double.parseDouble(targetUpperValue);
//            double targetLower = Double.parseDouble(targetLowerValue);
//            if (metricScore > targetUpper){
//                metricScore = targetUpper;
//            }
//            if (metricScore < targetLower){
//                metricScore = 0;
//            }
            totalScore += metricScore * weightScore /100;
        }
        //4.附加分
        double fjScore = 0.0;
        String fjScoreString = listBaseInfo.get(0).getString("fjScore");
        if (fjScoreString != null && !fjScoreString.isEmpty()) {
            fjScore = Double.parseDouble(fjScoreString);
        }
        //总分
        totalScore = totalScore+fjScore;

        DecimalFormat df = new DecimalFormat("0.00");
        list.put("score", df.format(totalScore));
        return list;
    }

    public JSONObject getDetailJsonReport(String id) {
        JSONObject list = new JSONObject();
        Map<String, Object> params = new HashMap<>();
        params.put("id",id);
        //1.基础信息
        List<JSONObject> listBaseInfo = kpiLeaderDao.getKpiLeaderBaseById(params);
        if (listBaseInfo.size() > 0){
            list.put("baseInfo",listBaseInfo.get(0));
        }
        //2.列表信息
        List<JSONObject> listDetail = kpiLeaderDao.getKpiLeaderDetailById(params);
        //不做数据权限过滤处理，展示全部流程数据
//        List<JSONObject> detailList = detailListByDepRole(listDetail);
        if (listDetail.size() > 0){
            list.put("detail",listDetail);
        }
        //3.绩效总分
        double totalScore = 0.0;
        for (JSONObject scoreLine : listDetail){
            if (scoreLine.getString("score") == null || scoreLine.getString("score").isEmpty()) {
                list.put("score","");
                return list;
            }
            String score = scoreLine.getString("score");
            String weight = scoreLine.getString("weight");
            double metricScore = Double.parseDouble(score);
            double weightScore = Double.parseDouble(weight);
            totalScore += metricScore * weightScore /100;
        }
        //4.附加分
        double fjScore = 0.0;
        String fjScoreString = listBaseInfo.get(0).getString("fjScore");
        if (fjScoreString != null && !fjScoreString.isEmpty()) {
            fjScore = Double.parseDouble(fjScoreString);
        }
        //总分
        totalScore = totalScore+fjScore;

        DecimalFormat df = new DecimalFormat("0.00");
        list.put("score", df.format(totalScore));
        return list;
    }

    private List<JSONObject> detailListByDepRole(List<JSONObject> kpiList) {
        List<JSONObject> result = new ArrayList<JSONObject>();
        if (kpiList == null || kpiList.isEmpty()) {
            return result;
        }
        // 管理员查看所有的包括草稿数据
        if ("admin".equals(ContextUtil.getCurrentUser().getUserNo())) {
            return kpiList;
        }
        //当前用户id
        String userId = ContextUtil.getCurrentUserId();

        //kpi管理人员查看全部数据（kpi管理维护人，技术管理部部长，分管领导）
        boolean isSZYDJX = false;
        isSZYDJX = judgeIsSZYDJX(userId);
        if (isSZYDJX){
            return kpiList;
        }
        // 过滤
        for (JSONObject oneProject : kpiList) {
            // 被考核人员查看
            if (oneProject.getString("bkhUserIds").contains(userId)) {
                result.add(oneProject);
                continue;
            }
            //执考人查看
            if (oneProject.getString("zkUserId").equalsIgnoreCase(userId)) {
                result.add(oneProject);
                continue;
            }
            //执考人领导查看
            List<JSONObject> leader = commonInfoDao.queryDeptRespByUserId(oneProject.getString("zkUserId"));
            String leaderUserId = leader.get(0).get("USER_ID_").toString();
            if (leaderUserId.equalsIgnoreCase(userId)){
                result.add(oneProject);
                continue;
            }


        }
        return result;
    }

    public boolean judgeIsSZYDJX(String userId) {
        List<JSONObject> roleKeys = queryAndReturnUserRoles(userId);
        boolean isSZYDJX = false;
        for (JSONObject oneRole : roleKeys) {
            if (oneRole.getString("roleKey").equalsIgnoreCase("SZYDJX")) {
                isSZYDJX = true;
            }
        }
        return isSZYDJX;
    }

    /**
     * 查询用户的角色
     *
     * @param userId
     * @return
     */
    public List<JSONObject> queryAndReturnUserRoles(String userId) {
        List<JSONObject> roleKeys = budgetMonthUserDao.queryRelInstRoles(userId);
        if (roleKeys == null) {
            roleKeys = new ArrayList<>();
        }
        return roleKeys;
    }

    public void saveDetailList(JsonResult result, String changeGridDataStr) {
        try {
            JSONArray changeGridDataJson = JSONObject.parseArray(changeGridDataStr);
            if (changeGridDataJson == null || changeGridDataJson.isEmpty()) {
                logger.warn("gridData is blank");
                result.setSuccess(false);
                result.setMessage("请求体为空");
                return;
            }
            for (int i = 0; i < changeGridDataJson.size(); i++) {
                JSONObject oneObject = changeGridDataJson.getJSONObject(i);
                oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                oneObject.put("UPDATE_TIME_", new Date());
                kpiLeaderDao.updateKpiMonthDetailList(oneObject);
            }

        } catch (Exception e) {
            logger.error("Exception in saveMaterialList");
            result.setSuccess(false);
            result.setMessage("保存数据异常");
            return;
        }
    }

    public void exportKpiLeaderReport(HttpServletRequest request, HttpServletResponse response) {
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
//        params.put("yearMonth",params.get("month").toString());
        List<JSONObject> dataList = kpiLeaderDao.queryKpiLeaderFlowList(params);
        // 查询当前处理人
        setTaskCurrentUser(dataList);
        //总分汇总
        setTotalScore(dataList);
        //封装
        List<JSONObject> finalSubProjectList = new ArrayList<>();
        // 根据分页进行subList截取
//        int pageIndex = RequestUtil.getInt(request, "pageIndex", 0);
//        int pageSize = RequestUtil.getInt(request, "pageSize", Page.DEFAULT_PAGE_SIZE);
//        int startSubListIndex = pageIndex * pageSize;
//        int endSubListIndex = startSubListIndex + pageSize;
//        if (startSubListIndex < dataList.size()) {
//            finalSubProjectList = dataList.subList(startSubListIndex,
//                    endSubListIndex < dataList.size() ? endSubListIndex : dataList.size());
//        }
        if (dataList != null && !dataList.isEmpty()) {
            for (Map<String, Object> oneProject : dataList) {
                if (oneProject.get("CREATE_TIME_") != null) {
                    oneProject.put("CREATE_TIME_",
                            DateUtil.formatDate((Date)oneProject.get("CREATE_TIME_"), "yyyy-MM-dd"));
                }
                if (oneProject.get("instStatus") != null && BpmInst.STATUS_RUNNING.equalsIgnoreCase(oneProject.get("instStatus").toString())) {
                    oneProject.put("instStatus","运行中");
                }else if (oneProject.get("instStatus") != null && BpmInst.STATUS_SUCCESS_END.equalsIgnoreCase(oneProject.get("instStatus").toString())){
                    oneProject.put("instStatus","成功结束");
                }
            }
        }
        String[] fieldNames = {"考核期间","考核类型","岗位","被考核人","考核总分","创建时间","当前任务","当前处理人","流程状态"};
        String[] fieldCodes = {"yearMonth","type","post","bkhUserName","scoreTotal","CREATE_TIME_","currentProcessTask","currentProcessUser","instStatus"};
        String month = params.get("yearMonth").toString();
        String title = "挖掘机械研究院所长月度绩效";
        String excelName = month + title;
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(dataList, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }
}
