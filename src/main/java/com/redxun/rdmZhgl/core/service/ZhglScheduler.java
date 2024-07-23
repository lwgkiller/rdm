package com.redxun.rdmZhgl.core.service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmZhgl.core.dao.*;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.standardManager.core.util.ResultUtil;
import com.redxun.wwrz.core.dao.CeinfoDao;
import com.redxun.wwrz.core.dao.WwrzFilesDao;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectMessageDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import com.redxun.xcmgProjectManager.report.manager.XcmgProjectReportManager;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author zhangzhen
 */
@Service
@EnableScheduling
public class ZhglScheduler {
    private static Logger logger = LoggerFactory.getLogger(ZhglScheduler.class);
    @Resource
    ProductDao productDao;
    @Resource
    ProductConfigDao productConfigDao;
    @Resource
    MonthWorkDao monthWorkDao;
    @Resource
    XcmgProjectReportManager xcmgProjectReportManager;
    @Resource
    CommonInfoDao commonInfoDao;
    @Resource
    MonthUnProjectPlanDao monthUnProjectPlanDao;
    @Resource
    MonthUnProjectPlanService monthUnProjectPlanService;
    @Resource
    YfjbBaseInfoDao yfjbBaseInfoDao;
    @Resource
    private SendDDNoticeManager sendDDNoticeManager;
    @Resource
    private NdkfjhPlanDetailDao ndkfjhPlanDetailDao;
    @Resource
    private NdkfjhPlanDetailService ndkfjhPlanDetailService;
    @Autowired
    private CommonInfoManager commonInfoManager;
    @Resource
    private MaterialApplyDao materialApplyDao;
    @Resource
    private MaterialApplyService materialApplyService;
    @Resource
    private WwrzFilesDao wwrzFilesDao;
    @Resource
    private CeinfoDao ceinfoDao;
    @Resource
    private ZljsjdsDao zljsjdsDao;
    @Resource
    private XcmgProjectMessageDao xcmgProjectMessageDao;
    @Autowired
    RdmZhglUtil rdmZhglUtil;

    /**
     * 新品试制更新产品状态：每天早上5点半
     */
    @Scheduled(cron = "0 30 05 * * *")
    public void productStatus() {
        try {
            logger.info("start productStatus");
            String webappName = WebAppUtil.getProperty("webappName", "rdm");
            if (!"rdm".equalsIgnoreCase(webappName)) {
                return;
            }
            JSONObject paramJson = new JSONObject();
            paramJson.put("finished", "finished");
            List<JSONObject> list = productDao.getProducts(paramJson);
            List<JSONObject> itemList = productConfigDao.getItemList(new JSONObject());
            for (JSONObject temp : list) {
                JSONObject planJson = productDao.getProductObjByMainId(temp.getString("id"));
                String field = "";
                String stage = "";
                int sort = 0;
                for (String key : temp.keySet()) {
                    if (key.endsWith("_date")) {
                        Object planDate = temp.get(key);
                        if (planDate != null) {
                            int sortIndex = getSort(key);
                            if (sortIndex > sort) {
                                sort = sortIndex;
                                field = key;
                            }
                        }
                    }
                }
                boolean isExe = true;
                if (StringUtil.isNotEmpty(field)) {
                    for (int i = 0; i < itemList.size(); i++) {
                        JSONObject itemJson = itemList.get(i);
                        int index = itemJson.getInteger("sort");
                        if (index == itemList.size()) {
                            stage = itemJson.getString("itemCode");
                            isExe = false;
                            break;
                        }
                        if (index > sort) {
                            String currentField = itemJson.getString("itemCode");
                            if (planJson.get(currentField) != null) {
                                stage = currentField;
                                break;
                            }
                        }
                    }

                } else {
                    // 返回最新计划第一个不等于null的
                    String mainId = temp.getString("mainId");
                    JSONObject planObj = productDao.getProductObjByMainId(mainId);
                    String stageCode = "";
                    int planSort = 26;
                    for (String key : planObj.keySet()) {
                        if (key.endsWith("_date")) {
                            Object planDate = planObj.get(key);
                            if (planDate != null) {
                                int sortIndex = getSort(key);
                                if (sortIndex < planSort) {
                                    planSort = sortIndex;
                                    stageCode = key;
                                }
                            }
                        }
                    }
                    if (StringUtil.isNotEmpty(stageCode)) {
                        stage = stageCode;
                    } else {
                        stage = itemList.get(0).getString("itemCode");
                    }
                }
                if (isExe) {
                    // 获取最新计划时间，然后和当前时间比较
                    Date planDate = planJson.getDate(stage);
                    if (planDate != null) {
                        long diff = System.currentTimeMillis() - planDate.getTime();
                        long days = diff / (1000 * 60 * 60 * 24);
                        String processStatus = "1";
                        if (days >= 4 && days <= 8) {
                            processStatus = "2";
                        } else if (days > 8) {
                            processStatus = "3";
                        }
                        Map<String, Object> objBody = new HashMap<>(16);
                        objBody.put("id", temp.get("id"));
                        objBody.put("processStatus", processStatus);
                        objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                        objBody.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                        productDao.updateObject(objBody);
                    }
                }
                if ("ssrq_date".equals(stage) && !isExe) {
                    // 获取最新计划时间，然后和当前时间比较
                    JSONObject json = new JSONObject();
                    json.put("mainId", temp.get("id"));
                    json.put("itemType", "4");
                    List<JSONObject> resultArray = productDao.getItemList(json);
                    if (resultArray != null && resultArray.size() > 0) {
                        JSONObject finishJson = resultArray.get(0);
                        Date finishDate = finishJson.getDate(stage);
                        if (finishDate != null) {
                            String processStatus = "1";
                            Map<String, Object> objBody = new HashMap<>(16);
                            objBody.put("id", temp.get("id"));
                            objBody.put("processStatus", processStatus);
                            objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                            objBody.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                            productDao.updateObject(objBody);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("productStatus 任务执行失败", e);
        }
    }

    /**
     * 新品试制计划提醒：每天早上9:55点半提醒 提醒对应项目的交付物负责上传人
     *
     */
    @Scheduled(cron = "0 55 09 * * *")
    public void xpszNotice() {
        try {
            logger.info("Start xpszNotice");
            String webappName = WebAppUtil.getProperty("webappName", "rdm");
            if (!"rdm".equalsIgnoreCase(webappName)) {
                return;
            }
            List<JSONObject> baseList = productDao.getBaseInfoList();
            for (JSONObject baseObj : baseList) {
                String mainId = baseObj.getString("id");
                JSONObject json = new JSONObject();
                json.put("mainId", mainId);
                json.put("type", "type");
                List<JSONObject> resultArray = productDao.getItemList(json);
                long remainDays = 0L;
                boolean noticeFlag = false;
                String itemCode = null;
                if (resultArray != null && resultArray.size() == 2) {
                    JSONObject planJson = resultArray.get(0);
                    JSONObject actJson = resultArray.get(1);
                    for (String key : planJson.keySet()) {
                        if (key.endsWith("_date")) {
                            Date planDate = planJson.getDate(key);
                            Date actDate = actJson.getDate(key);
                            if (planDate == null || actDate != null) {
                                continue;
                            } else {
                                long diff = planDate.getTime() - System.currentTimeMillis();
                                long days = diff / (1000 * 60 * 60 * 24);
                                if (days == 5) {
                                    remainDays = days;
                                    noticeFlag = true;
                                    itemCode = key;
                                    break;
                                }
                            }
                        }
                    }
                }
                if (noticeFlag) {
                    JSONObject itemObj = productConfigDao.getItemObjByCode(itemCode);
                    String itemName = "";
                    if (itemObj != null) {
                        itemName = itemObj.getString("itemName");
                    }
                    JSONObject deliveryObj = getDeliveryResponse(baseObj.getString("productId"), itemCode);
                    if (deliveryObj.getBoolean("success")) {
                        JSONObject reponseObj = deliveryObj.getJSONObject("data");
                        String responseId = reponseObj.getString("userId");
                        JSONObject noticeObj = new JSONObject();
                        StringBuilder stringBuilder = new StringBuilder("【新品试制】");
                        stringBuilder.append("产品设计型号").append(baseObj.getString("productModel"));
                        stringBuilder.append("，有节点").append(itemName).append("还有").append(remainDays).append("天延期");
                        stringBuilder.append("，请及时到科技项目:" + reponseObj.getString("projectName") + "。上传交付物");
                        stringBuilder.append("，通知时间：")
                            .append(XcmgProjectUtil.getNowLocalDateStr(DateUtil.DATE_FORMAT_FULL));
                        noticeObj.put("content", stringBuilder.toString());
                        sendDDNoticeManager.sendNoticeForCommon(responseId, noticeObj);
                        JSONObject paramJson = new JSONObject();
                        paramJson.put("id", IdUtil.getId());
                        paramJson.put("msgType", "新品试制：提醒项目交付物负责人上传");
                        paramJson.put("recUserIds", responseId);
                        paramJson.put("content", stringBuilder.toString());
                        paramJson.put("CREATE_TIME_", XcmgProjectUtil.getNowLocalDateStr(DateUtil.DATE_FORMAT_FULL));
                        commonInfoDao.insertDingMessage(paramJson);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("xpszNotice任务执行失败", e);
        }
    }

    private JSONObject getDeliveryResponse(String productId, String itemCode) {
        JSONObject resultJson = new JSONObject();
        try {
            // 先根据阶段获取对应的交付物
            JSONObject stageObj = productConfigDao.getItemObjByCode(itemCode);
            if (stageObj == null || StringUtil.isEmpty(stageObj.getString("deliveryId"))) {
                return ResultUtil.result(false, "阶段尚未配置交付物", null);
            }
            String deliveryId = CommonFuns.nullToString(stageObj.getString("deliveryId"));
            // 根据产品id获取项目信息
            JSONObject projectObj = productDao.getProjectByProductId(productId);
            if (projectObj == null) {
                return ResultUtil.result(false, "未找到科技项目", null);
            }
            String projectId = projectObj.getString("projectId");
            JSONObject paraJson = new JSONObject();
            paraJson.put("projectId", projectId);
            paraJson.put("deliveryId", deliveryId);
            paraJson.put("productId", productId);
            JSONObject responseObj = productDao.getDeliveryResponse(paraJson);
            if (responseObj == null) {
                return ResultUtil.result(false, "未找到交付物负责人", null);
            }
            responseObj.put("projectName", projectObj.getString("projectName"));
            resultJson = ResultUtil.result(true, "", responseObj);

        } catch (Exception e) {
            logger.error("getDeliveryResponse", e);
        }
        return resultJson;
    }

    /**
     * 每月17号自动生成挖掘机械研究院月度计划
     */
    @Scheduled(cron = "0 30 22 17 * *")
    public void genMonthPlan() throws Exception {
        try {
            logger.info("start genMonthPlan");
            String webappName = WebAppUtil.getProperty("webappName", "rdm");
            if (!"rdm".equalsIgnoreCase(webappName)) {
                return;
            }

            // 取挖掘机械研究院下所有的部门id
            Map<String, String> deptId2Name = commonInfoManager.queryDeptUnderJSZX();
            List<String> techDeptIds = new ArrayList<>(deptId2Name.keySet());
            JSONObject paramJson = new JSONObject();
            paramJson.put("techDeptIds", techDeptIds);
            List<Map<String, Object>> list = monthWorkDao.getPersonProjectList(paramJson);
            // list已包含计划结束和计划开始时间
            rdmZhglUtil.setTaskCreateTimeInfo(list, true);
            List<String> timeNames = new ArrayList<>();
            timeNames.add("planStartTime");
            timeNames.add("planEndTime");
            CommonFuns.convertMapListDate(list, timeNames, "yyyy-MM-dd");
            for (Map<String, Object> map : list) {
                String stageName = CommonFuns.nullToString(map.get("stageName"));
                if ("市场指标".equals(stageName)) {
                    continue;
                }
                xcmgProjectReportManager.queryProjectProgressNum(map, "");
                xcmgProjectReportManager.queryProjectRisk(map, "");
                addMonthPlan(map);
            }
        } catch (ParseException e) {
            logger.error("genMonthPlan任务执行失败", e);
        }
    }

    private void addMonthPlan(Map<String, Object> map) {
        Map<String, Object> objBody = new HashMap<>(16);
        String id = IdUtil.getId();
        String userId = CommonFuns.nullToString(map.get("userId"));
        objBody.put("id", id);
        objBody.put("CREATE_BY_", userId);
        objBody.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        objBody.put("UPDATE_BY_", userId);
        objBody.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        objBody.put("projectId", map.get("projectId"));
        objBody.put("stageId", map.get("currentStageId"));
        objBody.put("responseMan", map.get("responseMan"));
        objBody.put("isCompanyLevel", "2");
        objBody.put("deptId", map.get("mainDepId"));
        objBody.put("responseUserId", map.get("userId"));
        objBody.put("yearMonth", CommonFuns.genYearMonth("next"));
        String planStartTime = CommonFuns.nullToString(map.get("planStartTime"));
        String planEndTime = CommonFuns.nullToString(map.get("planEndTime"));
        objBody.put("startEndDate", planStartTime + "至" + planEndTime);
        objBody.put("processRate", map.get("progressNum") + "%");
        String hasRisk = CommonFuns.nullToString(map.get("hasRisk"));
        String processStatus = "项目未延误";
        if ("1".equals(hasRisk)) {
            processStatus = "项目延误时间未超过5天";
        } else if ("2".equals(hasRisk)) {
            processStatus = "项目延误时间超过5天";
        } else if ("3".equals(hasRisk)) {
            processStatus = "项目未启动或已停止";
        } else if ("4".equals(hasRisk)) {
            processStatus = "未填写计划时间且延误超30天";
        }
        objBody.put("processStatus", processStatus);
        objBody.put("asyncStatus", "1");
        monthWorkDao.addObject(objBody);
        addMonthPlanDetail(id, userId);
    }

    private void addMonthPlanDetail(String mainId, String userId) {
        JSONObject oneObject = new JSONObject();
        oneObject.put("id", IdUtil.getId());
        oneObject.put("CREATE_BY_", userId);
        oneObject.put("CREATE_TIME_", new Date());
        oneObject.put("UPDATE_BY_", userId);
        oneObject.put("UPDATE_TIME_", new Date());
        oneObject.put("mainId", mainId);
        oneObject.put("responseUserId", userId);
        oneObject.put("workContent", "未填写");
        oneObject.put("finishFlag", "未填写");
        oneObject.put("sortIndex", "1");
        monthWorkDao.addItem(oneObject);
    }

    private int getSort(String field) {
        List<JSONObject> itemList = productConfigDao.getItemList(new JSONObject());
        int sort = 0;
        for (int i = 0; i < itemList.size(); i++) {
            JSONObject jsonObject = itemList.get(i);
            if (field.equals(jsonObject.getString("itemCode"))) {
                sort = jsonObject.getInteger("sort");
                break;
            }
        }
        return sort;
    }

    /**
     * 每月20号 根据研发将本，自动生成非项目类计划 20号之前，判断是否填写本月月初计划， 20号之后，判断是否填写本月月底计划
     */
    @Scheduled(cron = "0 00 23 * * *")
    public void yfjbLastProcess() throws Exception {
        try {
            logger.info("start yfjbLastProcess");
            String webappName = WebAppUtil.getProperty("webappName", "rdm");
            if (!"rdm".equalsIgnoreCase(webappName)) {
                return;
            }
            List<JSONObject> baseList = yfjbBaseInfoDao.getInfoListByReplace();
            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DATE);
            String yearMonth = CommonFuns.genYearMonth("cur");
            String type = "";
            if (day < 20) {
                type = "1";
            } else {
                type = "2";
            }
            String tempYearMonth;
            String tempType;
            JSONObject paramJson = new JSONObject();
            for (JSONObject obj : baseList) {
                paramJson.put("id", obj.getString("id"));
                tempYearMonth = obj.getString("yearMonth");
                tempType = obj.getString("type");
                if (yearMonth.equals(tempYearMonth) && type.equals(tempType)) {
                    // 更新状态
                    paramJson.put("isNewProcess", "1");
                } else {
                    paramJson.put("isNewProcess", "0");
                }
                yfjbBaseInfoDao.updateIsNewProcess(paramJson);
            }
        } catch (Exception e) {
            logger.error("yfjbLastProcess任务执行失败", e);
        }
    }

    /**
     * 研发降本生成各类延期报表数据：每天晚上11点10分执行
     */
    @Scheduled(cron = "0 24 15 * * *")
    public void yfjbDelayReport() {
        try {
            logger.info("start yfjbDelayReport");
            String webappName = WebAppUtil.getProperty("webappName", "rdm");
            if (!"rdm".equalsIgnoreCase(webappName)) {
                return;
            }
            Calendar calendar = Calendar.getInstance();
            int currentYear = calendar.get(Calendar.YEAR);
            int currentMonth = calendar.get(Calendar.MONTH) + 1;
            List<JSONObject> deptNumList = yfjbBaseInfoDao.getDeptProjectNum();
            List<JSONObject> detailList = new ArrayList<>();
            JSONObject detailObj;
            for (JSONObject temp : deptNumList) {
                int productDelayNum = 0;
                int changeNoticeDelayNum = 0;
                int changeDelayNum = 0;
                List<JSONObject> projectList = yfjbBaseInfoDao.getProjectListByDeptId(temp.getString("deptId"));
                for (JSONObject yfjbObj : projectList) {
                    String jhsz_date = StringUtils.trim(yfjbObj.getString("jhsz_date"));
                    String sjsz_date = StringUtils.trim(yfjbObj.getString("sjsz_date"));
                    if (StringUtils.isNotBlank(jhsz_date) && !"/".equals(jhsz_date) && !"无".equals(jhsz_date)) {
                        boolean productDelayFlag = compareDate(jhsz_date, sjsz_date, currentYear, currentMonth);
                        if (productDelayFlag) {
                            productDelayNum++;
                            detailObj = new JSONObject();
                            detailObj.put("type", "productDelay");
                            detailObj.put("deptId", temp.getString("deptId"));
                            detailObj.put("productId", yfjbObj.getString("id"));
                            detailList.add(detailObj);
                        }
                    }
                    String jhxfqh_date = StringUtils.trim(yfjbObj.getString("jhxfqh_date"));
                    String sjxfqh_date = StringUtils.trim(yfjbObj.getString("sjxfqh_date"));
                    if (StringUtils.isNotBlank(jhxfqh_date) && !"/".equals(jhxfqh_date) && !"无".equals(jhxfqh_date)) {
                        boolean changeNoticeDelayFlag =
                            compareDate(jhxfqh_date, sjxfqh_date, currentYear, currentMonth);
                        if (changeNoticeDelayFlag) {
                            changeNoticeDelayNum++;
                            detailObj = new JSONObject();
                            detailObj.put("type", "changeNoticeDelay");
                            detailObj.put("deptId", temp.getString("deptId"));
                            detailObj.put("productId", yfjbObj.getString("id"));
                            detailList.add(detailObj);
                        }
                    }
                    String jhqh_date = StringUtils.trim(yfjbObj.getString("jhqh_date"));
                    String sjqh_date = StringUtils.trim(yfjbObj.getString("sjqh_date"));
                    if (StringUtils.isNotBlank(jhqh_date) && !"/".equals(jhqh_date) && !"无".equals(jhqh_date)) {
                        boolean changeDelayFlag = compareDate(jhqh_date, sjqh_date, currentYear, currentMonth);
                        if (changeDelayFlag) {
                            changeDelayNum++;
                            detailObj = new JSONObject();
                            detailObj.put("type", "changeDelay");
                            detailObj.put("deptId", temp.getString("deptId"));
                            detailObj.put("productId", yfjbObj.getString("id"));
                            detailList.add(detailObj);
                        }
                    }
                }
                temp.put("productDelayNum", productDelayNum);
                temp.put("changeNoticeDelayNum", changeNoticeDelayNum);
                temp.put("changeDelayNum", changeDelayNum);
                temp.put("asyncDate", new Date());
                JSONObject dataObj = yfjbBaseInfoDao.getDelayData(temp.getString("deptId"));
                if (dataObj != null) {
                    yfjbBaseInfoDao.updateDelayData(temp);
                } else {
                    temp.put("id", IdUtil.getId());
                    yfjbBaseInfoDao.addDelayData(temp);
                }
            }
            yfjbBaseInfoDao.delDelayInfo();
            List<JSONObject> tempAddList = new ArrayList<>();
            for (int i = 0; i < detailList.size(); i++) {
                JSONObject oneObj = detailList.get(i);
                oneObj.put("id", IdUtil.getId());
                tempAddList.add(oneObj);
                if (tempAddList.size() % 20 == 0) {
                    yfjbBaseInfoDao.batchAddDelay(tempAddList);
                    tempAddList.clear();
                }
            }
            if (!tempAddList.isEmpty()) {
                yfjbBaseInfoDao.batchAddDelay(tempAddList);
                tempAddList.clear();
            }
        } catch (Exception e) {
            logger.error("yfjbDelayReport任务执行失败", e);
        }
    }

    /**
     * 计划时间和实际实际比较 如果实际时间为空，则和当前时间比较 注：时间格式比较乱需要特殊处理 返回值：true 延期，false未延期
     */
    private boolean compareDate(String planTime, String actTime, int currentYear, int currentMonth) {
        try {
            int planYear = 0;
            int planMonth = 0;
            int actYear = currentYear;
            int actMonth = currentMonth;
            if (planTime.indexOf(".") > -1) {
                planYear = Integer.parseInt(planTime.split("\\.")[0]);
                planMonth = Integer.parseInt(planTime.split("\\.")[1]);
            } else if (planTime.indexOf("-") > -1) {
                planYear = Integer.parseInt(planTime.split("-")[0]);
                planMonth = Integer.parseInt(planTime.split("-")[1]);
            }
            if (StringUtils.isNotBlank(actTime)) {
                if (actTime.indexOf(".") > -1) {
                    actYear = Integer.parseInt(actTime.split("\\.")[0]);
                    actMonth = Integer.parseInt(actTime.split("\\.")[1]);
                } else if (actTime.indexOf("-") > -1) {
                    actYear = Integer.parseInt(actTime.split("-")[0]);
                    actMonth = Integer.parseInt(actTime.split("-")[1]);
                }
            }
            if (planYear < actYear) {
                return true;
            }
            if (planYear == actYear && planMonth < actMonth) {
                return true;
            }
        } catch (Exception e) {
            logger.error("compareDate", e);
        }
        return false;
    }

    /**
     * 年度开发计划：每个月4号晚上定期同步计划进度情况 只同步本年度 计划
     */
    @Scheduled(cron = "0 48 23 04 * *")
    public void asyncYearDevelopPlan() throws Exception {
        try {
            logger.info("start asyncYearDevelopPlan");
            String webappName = WebAppUtil.getProperty("webappName", "rdm");
            if (!"rdm".equalsIgnoreCase(webappName)) {
                return;
            }
            Calendar calendar = Calendar.getInstance();
            String yearMonth = CommonFuns.genYearMonth("cur");
            int year = calendar.get(Calendar.YEAR);
            JSONObject paramJson = new JSONObject();
            paramJson.put("planYear", year);
            List<Map<String, Object>> list = ndkfjhPlanDetailDao.getPlanDetailList(paramJson);
            for (Map<String, Object> obj : list) {
                String planSource = CommonFuns.nullToString(obj.get("planSource"));
                if ("lxxm".equals(planSource)) {
                    dealProjectInfo(obj, yearMonth);
                } else if ("xpsz".equals(planSource)) {
                    dealNewProductInfo(obj, yearMonth);
                } else if ("tsdd".equals(planSource)) {
                    dealSpecialOrderInfo(obj, yearMonth);
                } else if ("zxxz".equals(planSource)) {
                    dealAddSelfInfo(obj, yearMonth);
                }
            }
        } catch (Exception e) {
            logger.error("asyncYearDevelopPlan任务执行失败", e);
        }
    }

    private void dealProjectInfo(Map<String, Object> obj, String yearMonth) throws ParseException {
        String projectId = CommonFuns.nullToString(obj.get("sourceId"));
        String detailId = CommonFuns.nullToString(obj.get("id"));
        JSONObject projectObj = ndkfjhPlanDetailDao.getProjectObj(projectId);
        Date planEndTime = projectObj.getDate("stageFinishDate");
        long currentTime = DateFormatUtil.parseDate(XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd")).getTime();
        if (planEndTime != null) {
            if (currentTime > planEndTime.getTime()) {
                obj.put("isDelay", "1");
                obj.put("delayDays", (currentTime - planEndTime.getTime()) / (3600 * 1000 * 24));
            } else {
                obj.put("isDelay", "1");
                obj.put("delayDays", null);
            }
        }
        obj.put("currentStage", projectObj.getString("currentStage"));
        obj.put("stageFinishDate", projectObj.get("stageFinishDate"));
        obj.put("finishRate", null);
        obj.put("yearMonth", yearMonth);
        obj.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        ndkfjhPlanDetailDao.updateObject(obj);
        JSONObject paramJson = new JSONObject();
        paramJson.put("yearMonth", yearMonth);
        paramJson.put("detailId", detailId);
        JSONObject processHistoryObj = ndkfjhPlanDetailDao.getProcessHistory(paramJson);
        if (processHistoryObj == null) {
            obj.put("id", IdUtil.getId());
            obj.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            obj.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            obj.put("detailId", detailId);
            obj.put("processDate", yearMonth);
            ndkfjhPlanDetailDao.addProcessHistory(obj);
        }
    }

    /**
     * 处理新品试制信息
     */
    private void dealNewProductInfo(Map<String, Object> obj, String yearMonth) {
        String id = CommonFuns.nullToString(obj.get("sourceId"));
        String detailId = obj.get("id").toString();
        Map<String, Object> newProductObj = ndkfjhPlanDetailDao.getNewProductObj(id);
        ndkfjhPlanDetailService.getNewProductStageInfo(newProductObj);
        obj.put("currentStage", newProductObj.get("currentStage"));
        obj.put("stageFinishDate", newProductObj.get("stageFinishDate"));
        obj.put("finishRate", null);
        obj.put("yearMonth", yearMonth);
        obj.put("isDelay", newProductObj.get("isDelay"));
        obj.put("delayDays", newProductObj.get("delayDays"));
        obj.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        ndkfjhPlanDetailDao.updateObject(obj);
        JSONObject paramJson = new JSONObject();
        paramJson.put("yearMonth", yearMonth);
        paramJson.put("detailId", detailId);
        JSONObject processHistoryObj = ndkfjhPlanDetailDao.getProcessHistory(paramJson);
        if (processHistoryObj == null) {
            obj.put("id", IdUtil.getId());
            obj.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            obj.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            obj.put("detailId", detailId);
            obj.put("processDate", yearMonth);
            ndkfjhPlanDetailDao.addProcessHistory(obj);
        }
    }

    /**
     * 处理自增的项目，先查询是否进度为当前月份，如果是则不更新，如果不是，则将进度信息质空，替换为当前月份
     */
    private void dealAddSelfInfo(Map<String, Object> obj, String yearMonth) {
        JSONObject paramJson = new JSONObject();
        paramJson.put("yearMonth", yearMonth);
        paramJson.put("detailId", obj.get("id"));
        JSONObject detailObj = ndkfjhPlanDetailDao.getObjectByParam(paramJson);
        if (detailObj == null) {
            obj.put("currentStage", null);
            obj.put("stageFinishDate", null);
            obj.put("stageFinishDate", null);
            obj.put("finishRate", null);
            obj.put("isDelay", null);
            obj.put("delayDays", null);
            obj.put("remark", null);
            obj.put("yearMonth", yearMonth);
            ndkfjhPlanDetailDao.updateObject(obj);
        }
    }

    /**
     * 处理特殊订单项目，先查询是否进度为当前月份，如果是则不更新，如果不是，则将进度信息质空，替换为当前月份
     */
    private void dealSpecialOrderInfo(Map<String, Object> obj, String yearMonth) {
        JSONObject paramJson = new JSONObject();
        paramJson.put("yearMonth", yearMonth);
        paramJson.put("detailId", obj.get("id"));
        JSONObject detailObj = ndkfjhPlanDetailDao.getObjectByParam(paramJson);
        if (detailObj == null) {
            obj.put("currentStage", null);
            obj.put("stageFinishDate", null);
            obj.put("stageFinishDate", null);
            obj.put("finishRate", null);
            obj.put("isDelay", null);
            obj.put("delayDays", null);
            obj.put("remark", null);
            obj.put("yearMonth", yearMonth);
            ndkfjhPlanDetailDao.updateObject(obj);
        }
    }

    /**
     * 物料预留号 每天同步预留单状态，删除到期物料申请 晚上22:50
     */
    @Scheduled(cron = "0 50 22 * * *")
    public void dealMaterialInfo() {
        try {
            logger.info("start dealMaterialInfo");
            String webappName = WebAppUtil.getProperty("webappName", "rdm");
            if (!"rdm".equalsIgnoreCase(webappName)) {
                return;
            }
            // 1.先删除到期的物料
            JSONObject paramJson = new JSONObject();
            paramJson.put("finalDate", DateUtil.formatDate(new Date(), "yyyy-MM-dd"));
            List<JSONObject> limitTimeItemList = materialApplyDao.getMaterialItemList(paramJson);
            List<String> materialCodeList = new ArrayList<>();
            List<String> lineNoList = new ArrayList<>();
            if (limitTimeItemList != null && !limitTimeItemList.isEmpty()) {
                for (JSONObject itemObj : limitTimeItemList) {
                    materialCodeList.add(itemObj.getString("materialCode"));
                    lineNoList.add(itemObj.getString("lineNo"));
                }
                materialApplyService.invalidItems(materialCodeList, lineNoList);
            }
            // 2.同步所有状态
            paramJson.put("finalDate", "");
            List<JSONObject> itemList = materialApplyDao.getMaterialItemList(paramJson);
            materialCodeList = new ArrayList<>();
            lineNoList = new ArrayList<>();
            if (itemList != null && !itemList.isEmpty()) {
                for (JSONObject itemObj : itemList) {
                    materialCodeList.add(itemObj.getString("materialCode"));
                    lineNoList.add(itemObj.getString("lineNo"));
                }
                materialApplyService.asyncItemStatus(materialCodeList, lineNoList);
            }
        } catch (Exception e) {
            logger.error("dealMaterialInfo任务执行失败", e);
        }
    }

    /**
     * 委外认证 报告证书，超过有效期后，自动作废 晚上22:54
     */
    @Scheduled(cron = "0 54 22 * * *")
    public void dealWwrzReportStatus() {
        try {
            logger.info("start dealWwrzReportStatus");
            String webappName = WebAppUtil.getProperty("webappName", "rdm");
            if (!"rdm".equalsIgnoreCase(webappName)) {
                return;
            }
            List<JSONObject> reportList = wwrzFilesDao.getOutDateReportList();
            for (JSONObject report : reportList) {
                String id = report.getString("id");
                wwrzFilesDao.updateReportStatus(id);
            }
        } catch (Exception e) {
            logger.error("dealWwrzReportStatus任务执行失败", e);
        }
    }

    /**
     * EC自声明自动作废，明天10点执行一次
     */
    @Scheduled(cron = "0 0 10 * * *")
    public void ecAutoDiscard() {
        try {
            logger.info("Start ecAutoDiscard");
            String dayStr = DateFormatUtil.getNowByString("yyyy-MM-dd");
            Map<String, Object> params = new HashMap<>();
            List<JSONObject> gjllList = ceinfoDao.queryCeinfo(params);
            for (JSONObject oneData : gjllList) {
                JSONObject param = new JSONObject();
                String zsEndDate = oneData.getString("zsEndDate");
                String replaceNo = oneData.getString("ceinfoId");
                param.put("replaceNo", replaceNo);
                if (StringUtils.isNotBlank(zsEndDate)) {
                    int dateFlag = zsEndDate.compareTo(dayStr);
                    if (dateFlag < 0) {
                        ceinfoDao.updateNoteStatus(param);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("ecAutoDiscard任务执行失败", e);
        }
    }

    /**
     * EC证书到期提醒，明天9:30执行一次
     */
    @Scheduled(cron = "0 30 9 * * *")
//    @Scheduled(fixedRate = 60000)
    public void ecExpireRemind() {
        try {
            logger.info("Start ecExpireRemind");
            String webappName = WebAppUtil.getProperty("webappName", "rdm");
            if (!"rdm".equalsIgnoreCase(webappName)) {
                return;
            }
            Date dNow = new Date(); // 当前时间
            Calendar calendar = Calendar.getInstance(); // 得到日历
            calendar.setTime(dNow);// 把当前时间赋给日历
            calendar.add(Calendar.MONTH, +3); // 设置为后3月
            Date d3Before = calendar.getTime(); // 得到后3月的时间
            SimpleDateFormat a3m = new SimpleDateFormat("yyyy-MM-dd"); // 设置时间格式
            String after3Month = a3m.format(d3Before); // 格式化后3月的时间

            calendar.clear();
            calendar.setTime(dNow);
            calendar.add(Calendar.MONTH, +1); // 设置为后1月
            Date d1Before = calendar.getTime(); // 得到后1月的时间
            SimpleDateFormat a1m = new SimpleDateFormat("yyyy-MM-dd"); // 设置时间格式
            String after1Month = a1m.format(d1Before); // 格式化后1月的时间

            Map<String, Object> params = new HashMap<>();
            params.put("reportValidity", after3Month);
            params.put("fileType", "report");
            params.put("send3Msg", "yes");
            List<Map<String, Object>> after3MonthList = wwrzFilesDao.getReport(params);
           Stream<Map<String, Object>> stream3Month = after3MonthList.stream();
            Map<String, List<Map<String, Object>>> userIdToReport3Month =
                    stream3Month.collect(Collectors.groupingBy(j -> String.valueOf(j.get("productManagerId"))));
            if (userIdToReport3Month != null && !userIdToReport3Month.isEmpty()) {
                for (String userId : userIdToReport3Month.keySet()) {
                    if(!"null".equalsIgnoreCase(userId)){
                        JSONObject postDataObj = new JSONObject();
                        StringBuilder numBuilder = new StringBuilder();
                        postDataObj.put("recUserIds", userId);
                        List<Map<String, Object>> oneuser = userIdToReport3Month.get(userId);
                        for (Map<String, Object> oneData : oneuser) {
                            numBuilder.append(oneData.get("reportCode").toString()).append("，");
                        }
                        String num = numBuilder.deleteCharAt(numBuilder.length() - 1).toString();
                        String content = "证书编号:“" + num + "”距离到期还有三个月,请及时发起新的委外认证流程";
                        postDataObj.put("content", content);
                        sendMsg(postDataObj);
                        for (Map<String, Object> oneData : oneuser) {
                            wwrzFilesDao.updateReportSend3Msg(oneData.get("id").toString());
                        }
                    }
                }
            }
            //一个月到期提醒
            params.clear();
            params.put("reportValidity", after1Month);
            params.put("fileType", "report");
            params.put("send1Msg", "yes");
            List<Map<String, Object>> after1MonthList = wwrzFilesDao.getReport(params);
            Stream<Map<String, Object>> stream1Month = after1MonthList.stream();
            Map<String, List<Map<String, Object>>> userIdToReport1Month =
                    stream1Month.collect(Collectors.groupingBy(j -> String.valueOf(j.get("productManagerId"))));
            if (userIdToReport1Month != null && !userIdToReport1Month.isEmpty()) {
                for (String userId : userIdToReport1Month.keySet()) {
                    if(!"null".equalsIgnoreCase(userId)){
                        JSONObject postDataObj = new JSONObject();
                        StringBuilder numBuilder = new StringBuilder();
                        postDataObj.put("recUserIds", userId);
                        List<Map<String, Object>> oneuser = userIdToReport1Month.get(userId);
                        for (Map<String, Object> oneData : oneuser) {
                            numBuilder.append(oneData.get("reportCode").toString()).append("，");
                        }
                        String num = numBuilder.deleteCharAt(numBuilder.length() - 1).toString();
                        String content = "证书编号:“" + num + "”距离到期还有一个月,请及时发起新的委外认证流程";
                        postDataObj.put("content", content);
                        sendMsg(postDataObj);
                        for (Map<String, Object> oneData : oneuser) {
                            wwrzFilesDao.updateReportSend1Msg(oneData.get("id").toString());
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("EC证书到期提醒执行失败", e);
        }
    }

    // 发送消息，用于页面主动发送的消息
    private void sendMsg(JSONObject postDataObj) {
        logger.info("Start sendMsg");
        String title = "委外认证到期提醒";
        String finalUserIdStr = postDataObj.getString("recUserIds");
        String plainTxt = postDataObj.getString("content");
        // 保存到消息表
//        String currentUserId = ContextUtil.getCurrentUserId();
//        postDataObj.put("id", IdUtil.getId());
//        postDataObj.put("CREATE_BY_", StringUtils.isBlank(currentUserId) ? "1" : currentUserId);
//        postDataObj.put("CREATE_TIME_", new Date());
//        postDataObj.put("expireTime", null);
//        postDataObj.put("title", title);
//        postDataObj.put("messageType", "group");
//        postDataObj.put("canPopup", "yes");
//        postDataObj.put("recType", "computer,dingding");
//        postDataObj.put("appName", "rdm");
//        xcmgProjectMessageDao.insertToProjectMessage(postDataObj);
        // 发送消息到钉钉

        String sendMsg = "【RDM平台消息】标题：" + title + ",内容：" + plainTxt + "。";
        JSONObject taskJson = new JSONObject();
        taskJson.put("content", sendMsg);
        sendDDNoticeManager.sendNoticeForCommon(finalUserIdStr, taskJson);
    }


    /**
     * 每周一9:30，给技术中心所有所长发送技术交底书的完成情况
     */
    @Scheduled(cron = "0 30 9 * * 2")
    public void jsjdsCompleteStatus() {
        try {
            logger.info("Start jsjdsCompleteStatus");
            String webappName = WebAppUtil.getProperty("webappName", "rdm");
            if (!"rdm".equalsIgnoreCase(webappName)) {
                return;
            }
            JSONObject noticeObj = new JSONObject();
            DecimalFormat sumf = new DecimalFormat("0.00");
            DecimalFormat inventf = new DecimalFormat("0.00");
            String nowTime = DateFormatUtil.getNowByString("yyyy-MM-dd");
            String nowYear = DateFormatUtil.getNowByString("yyyy");
            String startTime = nowYear + "-01-01" + " 00:00:00";
            String endTime = nowTime + " 23:59:59";
            String zllx = "FM";
            Calendar cal = Calendar.getInstance();
            String year = Integer.toString(cal.get(Calendar.YEAR));
            String month = Integer.toString(cal.get(Calendar.MONTH) + 1);
            JSONObject param = new JSONObject();
            param.put("year", year);
            param.put("month", month);
            List<JSONObject> jdsPlans = zljsjdsDao.selectJsjdsPlan(param);
            param.clear();
            param.put("startTime", startTime);
            param.put("endTime", endTime);
            List<JSONObject> jdsFinishs = zljsjdsDao.selectJsjdsFinish(param);
            param.put("zllx", zllx);
            List<JSONObject> jdsFmFinishs = zljsjdsDao.selectJsjdsFinish(param);
            Map<String, List<JSONObject>> deptIdsToSum =
                jdsPlans.stream().collect(Collectors.groupingBy(j -> j.getString("deptId")));
            Map<String, List<JSONObject>> deptIdsToFinish =
                jdsFinishs.stream().collect(Collectors.groupingBy(j -> j.getString("deptId")));
            Map<String, List<JSONObject>> deptIdsToFmFinish =
                jdsFmFinishs.stream().collect(Collectors.groupingBy(j -> j.getString("deptId")));

            JSONObject deptInfo = new JSONObject();
            List<Map<String, String>> groupUsers =
                commonInfoManager.queryUserByGroupNameAndRelType(RdmConst.JSZX_SZ, "GROUP-USER-BELONG");
            for (Map<String, String> groupUser : groupUsers) {
                Double totalFinishRate = 0.00;
                Double inventFinishRate = 0.00;
                int totalSum = 0;
                int inventSum = 0;
                int totalFinish = 0;
                int inventFinish = 0;
                int totalunFinish = 0;
                int inventunFinish = 0;
                String totalContent = "";
                String inventContent = "";
                String userId = groupUser.get("USER_ID_");
                deptInfo = commonInfoManager.queryDeptByUserId(userId);
                String deptId = deptInfo.getString("deptId");
                String deptName = deptInfo.getString("deptName");
                List<JSONObject> deptIdToSum = deptIdsToSum.get(deptId);
                List<JSONObject> deptIdToFinish = deptIdsToFinish.get(deptId);
                List<JSONObject> deptIdToFmFinish = deptIdsToFmFinish.get(deptId);
                if (deptIdToSum != null && !deptIdToSum.isEmpty()) {
                    totalSum = deptIdToSum.get(0).getInteger("totalSum");
                    inventSum = deptIdToSum.get(0).getInteger("inventSum");
                }
                if (deptIdToFinish != null && !deptIdToFinish.isEmpty()) {
                    totalFinish = deptIdToFinish.get(0).getInteger("count");
                }
                if (deptIdToFmFinish != null && !deptIdToFmFinish.isEmpty()) {
                    inventFinish = deptIdToFmFinish.get(0).getInteger("count");
                }
                if (totalSum > 0) {
                    totalunFinish = totalSum - totalFinish;
                    if (totalunFinish < 0) {
                        totalunFinish = 0;
                    }
                    totalFinishRate = Double.parseDouble(sumf.format(totalFinish * 100.0 / totalSum));
                    totalContent = deptName + "-截至当前技术交底书完成量为" + totalFinish + "件,未完成" + totalunFinish + "件,完成率为"
                        + totalFinishRate + "%。";
                }
                if (inventSum > 0) {
                    inventunFinish = inventSum - inventFinish;
                    if (inventunFinish < 0) {
                        inventunFinish = 0;
                    }
                    inventFinishRate = Double.parseDouble(inventf.format(inventFinish * 100.0 / inventSum));
                    inventContent =
                        "其中发明类技术交底书完成量为" + inventFinish + "件,未完成" + inventunFinish + "件,完成率为" + inventFinishRate + "%。";
                }
                if (StringUtils.isNotBlank(totalContent)) {
                    noticeObj.put("content", totalContent + inventContent);
                    sendDDNoticeManager.sendNoticeForCommon(userId, noticeObj);
                    noticeObj.clear();
                }
            }
        } catch (Exception e) {
            logger.error("技术交底书完成情况发送失败", e);
        }
    }

}
