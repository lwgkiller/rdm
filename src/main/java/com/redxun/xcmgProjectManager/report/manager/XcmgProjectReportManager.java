
package com.redxun.xcmgProjectManager.report.manager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.rdmCommon.core.util.RdmCommonUtil;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.query.Page;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmCommon.core.manager.LoginRecordManager;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectScoreDao;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import com.redxun.xcmgProjectManager.core.util.ConstantUtil;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import com.redxun.xcmgProjectManager.report.dao.XcmgProjectReportDao;
import com.redxun.xcmgProjectManager.report.entity.XcmgProjectReportYwph;
import com.redxun.xcmgProjectManager.report.entity.YdjfDepsumscore;

/**
 * @author lcj
 *
 *         <pre>
 * 描述：project_baseInfo 处理接口
 * 作者:x
 * 日期:2019-08-22 08:59:49
 * 版权：xx
 *         </pre>
 */
@Service
public class XcmgProjectReportManager {
    private Logger logger = LoggerFactory.getLogger(XcmgProjectReportManager.class);
    @Resource
    private XcmgProjectReportDao xcmgProjectReportDao;
    @Resource
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Resource
    private XcmgProjectManager xcmgProjectManager;
    @Resource
    private XcmgProjectScoreDao xcmgProjectScoreDao;
    @Resource
    private LoginRecordManager loginRecordManager;
    @Autowired
    private CommonInfoManager commonInfoManager;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;

    /**
     * 查询按照项目类别和级别统计的，已立项且状态为运行中或成功结束的项目数量
     */
    public JSONObject queryLbtj(JSONObject postDataJson) throws Exception {
        JSONObject result = new JSONObject();
        try {
            Map<String, Object> params = new HashMap<>(16);
            params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            // 查询项目的小类别
            List<String> samllCategoryNames = new ArrayList<>();
            List<Map<String, String>> mapCategory = xcmgProjectOtherDao.queryProjectCategory(params);
            if (mapCategory != null && !mapCategory.isEmpty()) {
                for (Map<String, String> oneCatefory : mapCategory) {
                    // 去掉大类别
                    if (!"0".equals(oneCatefory.get("parentId"))) {
                        samllCategoryNames.add(oneCatefory.get("categoryName"));
                    }
                }
            }
            result.put("categoryList", samllCategoryNames);
            // 查询项目的级别
            List<String> bigLevelNames = new ArrayList<>();
            List<Map<String, String>> mapLevel = xcmgProjectOtherDao.queryProjectLevel(params);
            if (mapLevel != null && !mapLevel.isEmpty()) {
                for (Map<String, String> oneLevel : mapLevel) {
                    bigLevelNames.add(oneLevel.get("levelName") + "级");
                }
            }
            result.put("levelList", bigLevelNames);

            // 查询按照级别和类别汇总的项目个数
            postDataJson.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            for (String key : postDataJson.keySet()) {
                if (key.equals("buildTimeFrom") || key.equals("knotTimeFrom")) {
                    if (StringUtils.isNotBlank(postDataJson.getString(key))) {
                        postDataJson.put(key,
                            DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(postDataJson.getString(key)), -8)));
                    }
                }
                if (key.equals("buildTimeTo") || key.equals("knotTimeTo")) {
                    if (StringUtils.isNotBlank(postDataJson.getString(key))) {
                        postDataJson.put(key,
                            DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(postDataJson.getString(key)), 16)));
                    }
                }
            }
            // 如果是挖掘机械研究院项目管理人员，则只查询挖掘机械研究院下的部门
            boolean isJSZXProjManagUsers =
                commonInfoManager.judgeUserIsPointRole(RdmConst.JSZXXMGLRY, ContextUtil.getCurrentUserId());
            if (isJSZXProjManagUsers) {
                Map<String, String> deptId2Name = commonInfoManager.queryDeptUnderJSZX();
                //2024-3-29新增技术中心项目管理员也可以看到工艺的项目
                boolean isGYProjManagUsers =
                        commonInfoManager.judgeUserIsPointRole(RdmConst.GYJSBXMGLRY, ContextUtil.getCurrentUserId());
                if (isGYProjManagUsers) {
                    Map<String, Object> param = new HashMap<>();
                    param.put("deptName", RdmConst.GYJSB_NAME);
                    List<JSONObject> deptList = xcmgProjectReportDao.queryDeptByName(param);
                    if (deptList != null && !deptList.isEmpty()) {
                        deptId2Name.put(deptList.get(0).getString("GROUP_ID_"), deptList.get(0).getString("NAME_"));
                    }
                }
                postDataJson.put("deptIds", deptId2Name.keySet());
                //2024-4-29新增技术中心项目管理员也可以看到应用技术部的项目
                boolean isYYProjManagUsers =
                        commonInfoManager.judgeUserIsPointRole(RdmConst.YYJSBXMGLRY, ContextUtil.getCurrentUserId());
                if (isYYProjManagUsers) {
                    Map<String, Object> param = new HashMap<>();
                    param.put("deptName", RdmConst.YYJSB_NAME);
                    List<JSONObject> deptList = xcmgProjectReportDao.queryDeptByName(param);
                    if (deptList != null && !deptList.isEmpty()) {
                        deptId2Name.put(deptList.get(0).getString("GROUP_ID_"), deptList.get(0).getString("NAME_"));
                    }
                }
                postDataJson.put("deptIds", deptId2Name.keySet());
            }
            List<Map<String, Object>> categoryLevelProjNum =
                xcmgProjectReportDao.countProjectNumByCategoryLevel(postDataJson);
            Map<String, Map<String, Object>> level2Category2Num = new HashMap<>(16);
            for (Map<String, Object> oneMap : categoryLevelProjNum) {
                String levelName = oneMap.get("levelName").toString();
                String categoryName = oneMap.get("categoryName").toString();
                Long number = (Long)oneMap.get("number");
                if (!level2Category2Num.containsKey(levelName)) {
                    level2Category2Num.put(levelName, new HashMap<>(16));
                }
                level2Category2Num.get(levelName).put(categoryName, number);
            }
            // 组合数据
            List<JSONObject> seriesData = new ArrayList<>();
            for (int i = bigLevelNames.size() - 1; i >= 0; i--) {
                JSONObject oneSerie = new JSONObject();
                oneSerie.put("name", bigLevelNames.get(i));
                oneSerie.put("type", "bar");
                oneSerie.put("stack", "数量");
                List<Long> data = new ArrayList<>();
                for (String categoryName : samllCategoryNames) {
                    Long numInt = 0L;
                    if (level2Category2Num.containsKey(bigLevelNames.get(i))
                        && level2Category2Num.get(bigLevelNames.get(i)).containsKey(categoryName)) {
                        numInt = (Long)level2Category2Num.get(bigLevelNames.get(i)).get(categoryName);
                    }
                    data.add(numInt);
                }
                oneSerie.put("data", data);
                seriesData.add(oneSerie);
            }
            result.put("series", seriesData);

            // 计算每一个类别的总数
            List<Long> categorySum = new ArrayList<>();
            for (int i = 0; i < samllCategoryNames.size(); i++) {
                Long sum = 0L;
                for (JSONObject jsonObject : seriesData) {
                    List<Long> data = (List<Long>)jsonObject.get("data");
                    sum += data.get(i);
                }
                categorySum.add(sum);
            }
            result.put("sumData", categorySum);
        } catch (Exception e) {
            logger.error("Exception in queryLbtj", e);
        }
        return result;
    }

    /**
     * 查询项目成果计划
     *
     * @param postDataJson
     * @return
     * @throws Exception
     */
    public JSONObject queryCgjh(JSONObject postDataJson) throws Exception {
        JSONObject result = new JSONObject();
        try {
            Map<String, Map<String, Long>> dept2Type2Num = new HashMap<>();
            Set<String> deptNames = new HashSet<>();
            Set<String> typeNames = new HashSet<>();
            Map<String, Object> params = new HashMap<>(16);
            for (String key : postDataJson.keySet()) {
                if (key.equals("cgjhFrom")) {
                    if (StringUtils.isNotBlank(postDataJson.getString(key))) {
                        params.put(key,
                            DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(postDataJson.getString(key)), -8)));
                    }
                }
                if (key.equals("cgjhTo")) {
                    if (StringUtils.isNotBlank(postDataJson.getString(key))) {
                        params.put(key,
                            DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(postDataJson.getString(key)), 16)));
                    }
                }
            }
            // 如果是挖掘机械研究院项目管理人员，则只查询挖掘机械研究院下的部门
            boolean isJSZXProjManagUsers =
                commonInfoManager.judgeUserIsPointRole(RdmConst.JSZXXMGLRY, ContextUtil.getCurrentUserId());
            if (isJSZXProjManagUsers) {
                Map<String, String> deptId2Name = commonInfoManager.queryDeptUnderJSZX();
                //2024-3-29新增技术中心项目管理员也可以看到工艺的项目
                boolean isGYProjManagUsers =
                        commonInfoManager.judgeUserIsPointRole(RdmConst.GYJSBXMGLRY, ContextUtil.getCurrentUserId());
                if (isGYProjManagUsers) {
                    Map<String, Object> param = new HashMap<>();
                    param.put("deptName", RdmConst.GYJSB_NAME);
                    List<JSONObject> deptList = xcmgProjectReportDao.queryDeptByName(param);
                    if (deptList != null && !deptList.isEmpty()) {
                        deptId2Name.put(deptList.get(0).getString("GROUP_ID_"), deptList.get(0).getString("NAME_"));
                    }
                }
                params.put("deptIds", deptId2Name.keySet());
                //2024-4-29新增技术中心项目管理员也可以看到应用技术部的项目
                boolean isYYProjManagUsers =
                        commonInfoManager.judgeUserIsPointRole(RdmConst.YYJSBXMGLRY, ContextUtil.getCurrentUserId());
                if (isYYProjManagUsers) {
                    Map<String, Object> param = new HashMap<>();
                    param.put("deptName", RdmConst.YYJSB_NAME);
                    List<JSONObject> deptList = xcmgProjectReportDao.queryDeptByName(param);
                    if (deptList != null && !deptList.isEmpty()) {
                        deptId2Name.put(deptList.get(0).getString("GROUP_ID_"), deptList.get(0).getString("NAME_"));
                    }
                }
                params.put("deptIds", deptId2Name.keySet());
            }
            List<JSONObject> achievements = xcmgProjectReportDao.queryAchievements(params);
            for (JSONObject oneAchievement : achievements) {
                String deptId = oneAchievement.getString("deptId");
                String deptName = oneAchievement.getString("deptName");
                if (!deptName.equalsIgnoreCase(RdmConst.GYJSB_NAME) && !deptName.equalsIgnoreCase(RdmConst.XXHGLB_NAME)
                    && !loginRecordManager.judgeIsJSZX(deptId, deptName).getBooleanValue("isJSZX")) {
                    continue;
                }

                String type = oneAchievement.getString("typeName");
                Long num = oneAchievement.getLong("num");
                if (!dept2Type2Num.containsKey(deptName)) {
                    dept2Type2Num.put(deptName, new HashMap<>());
                }
                Map<String, Long> oneDeptType2Num = dept2Type2Num.get(deptName);
                if (!oneDeptType2Num.containsKey(type)) {
                    oneDeptType2Num.put(type, 0L);
                }
                Long finalNum = oneDeptType2Num.get(type) + num;
                oneDeptType2Num.put(type, finalNum);

                deptNames.add(deptName);
                typeNames.add(type);
            }
            // 组装数据
            List<String> typeNameList = new ArrayList<>(typeNames);
            List<String> deptNameList = new ArrayList<>(deptNames);
            result.put("deptNames", deptNameList);
            result.put("achievementTypes", typeNameList);
            List<JSONObject> seriesData = new ArrayList<>();
            for (String deptName : deptNameList) {
                JSONObject oneDeptData = new JSONObject();
                oneDeptData.put("name", deptName);
                oneDeptData.put("type", "bar");
                List<Long> data = new ArrayList<>();
                oneDeptData.put("data", data);
                for (String typeName : typeNameList) {
                    Long num = 0L;
                    if (dept2Type2Num.get(deptName).containsKey(typeName)) {
                        num = dept2Type2Num.get(deptName).get(typeName);
                    }
                    data.add(num);
                }
                seriesData.add(oneDeptData);
            }
            result.put("series", seriesData);
        } catch (Exception e) {
            logger.error("Exception in queryCgjh", e);
        }
        return result;
    }

    /**
     * 查询当前在运行的各个部门的项目中，进度正常和进度延迟的数量
     */
    public JSONObject queryYwph() throws Exception {
        JSONObject result = new JSONObject();
        try {
            Map<String, Object> params = new HashMap<>(16);
            params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            // 查询项目，并按照部门统计进度正常和延误的数量
            boolean isJSZXProjManagUsers =
                commonInfoManager.judgeUserIsPointRole(RdmConst.JSZXXMGLRY, ContextUtil.getCurrentUserId());
            if (isJSZXProjManagUsers) {
                Map<String, String> deptId2Name = commonInfoManager.queryDeptUnderJSZX();
                //2024-3-29新增技术中心项目管理员也可以看到工艺的项目
                boolean isGYProjManagUsers =
                        commonInfoManager.judgeUserIsPointRole(RdmConst.GYJSBXMGLRY, ContextUtil.getCurrentUserId());
                if (isGYProjManagUsers) {
                    Map<String, Object> param = new HashMap<>();
                    param.put("deptName", RdmConst.GYJSB_NAME);
                    List<JSONObject> deptList = xcmgProjectReportDao.queryDeptByName(param);
                    if (deptList != null && !deptList.isEmpty()) {
                        deptId2Name.put(deptList.get(0).getString("GROUP_ID_"), deptList.get(0).getString("NAME_"));
                    }
                }
                params.put("deptIds", deptId2Name.keySet());
                //2024-4-29新增技术中心项目管理员也可以看到应用技术部的项目
                boolean isYYProjManagUsers =
                        commonInfoManager.judgeUserIsPointRole(RdmConst.YYJSBXMGLRY, ContextUtil.getCurrentUserId());
                if (isYYProjManagUsers) {
                    Map<String, Object> param = new HashMap<>();
                    param.put("deptName", RdmConst.YYJSB_NAME);
                    List<JSONObject> deptList = xcmgProjectReportDao.queryDeptByName(param);
                    if (deptList != null && !deptList.isEmpty()) {
                        deptId2Name.put(deptList.get(0).getString("GROUP_ID_"), deptList.get(0).getString("NAME_"));
                    }
                }
                params.put("deptIds", deptId2Name.keySet());
            }
            // projectInfos包含planEndTime
            List<Map<String, Object>> projectInfos = xcmgProjectReportDao.countRunningProjProcess(params);
            rdmZhglUtil.setTaskCreateTimeInfo(projectInfos, true);
            Map<String, XcmgProjectReportYwph> dep2Ywph = new HashMap<>(16);
            // 查询出来的数据自动已经转为了本地时间
            String nowLocalTimeStr = XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd");
            long currentTime = DateFormatUtil.parseDate(XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd")).getTime();
            for (Map<String, Object> oneProject : projectInfos) {
                if (oneProject.get("depName") == null) {
                    continue;
                }
                String depName = oneProject.get("depName").toString();
                boolean isProcessOk = true;
                // 项目是否延误，有计划结束时间按照计划结束时间判断
                if (oneProject.get("planEndTime") != null) {
                    String planEndTime = DateUtil.formatDate((Date)oneProject.get("planEndTime"), "yyyy-MM-dd");
                    isProcessOk = nowLocalTimeStr.compareTo(planEndTime) > 0 ? false : true;
                    // 未填写计划时间按照实例创建时间进行判断，超过30天为延误
                } else if (currentTime
                    - DateFormatUtil.parseDate(oneProject.get("taskCreateTime").toString()).getTime() > 2592000000L) {
                    isProcessOk = false;
                }
                 if (oneProject.containsKey("progressRunStatus")
                 && "1".equalsIgnoreCase(oneProject.get("progressRunStatus").toString())) {
                 isProcessOk = true;
                 }
                if (!dep2Ywph.containsKey(depName)) {
                    dep2Ywph.put(depName, new XcmgProjectReportYwph(depName));
                }
                XcmgProjectReportYwph ywphObj = dep2Ywph.get(depName);
                if (isProcessOk) {
                    ywphObj.setProcessOk(ywphObj.getProcessOk() + 1);
                } else {
                    ywphObj.setProcessDelay(ywphObj.getProcessDelay() + 1);
                }
            }
            // 计算延误率
            List<XcmgProjectReportYwph> allDepYwph = new ArrayList<>();
            for (XcmgProjectReportYwph ywphObj : dep2Ywph.values()) {
                ywphObj.setDelayRate(
                    (double)ywphObj.getProcessDelay() / (ywphObj.getProcessDelay() + ywphObj.getProcessOk()));
                // 设置为负数，便于前台展示
                ywphObj.setProcessDelay(0 - ywphObj.getProcessDelay());
                allDepYwph.add(ywphObj);
            }
            int orgLength = allDepYwph.size();
            // 延误率从小到大排序
            Collections.sort(allDepYwph, new Comparator<XcmgProjectReportYwph>() {
                @Override
                public int compare(XcmgProjectReportYwph o1, XcmgProjectReportYwph o2) {
                    if (o1.getDelayRate() - o2.getDelayRate() > 0) {
                        return 1;
                    } else if (o1.getDelayRate() == o2.getDelayRate()) {
                        return Math.abs(o1.getProcessDelay()) - Math.abs(o2.getProcessDelay());
                    }
                    return -1;
                }
            });
            // if (orgLength >= ConstantUtil.SHOW_NUM) {
            // allDepYwph = allDepYwph.subList(orgLength - ConstantUtil.SHOW_NUM, orgLength);
            // }
            // 拼接最终的结果数据
            List<Integer> processOkArr = new ArrayList<>();
            List<Integer> processDelayArr = new ArrayList<>();
            List<Double> delayRate = new ArrayList<>();
            List<String> allDepSortedNames = new ArrayList<>();
            for (XcmgProjectReportYwph ywphObj : allDepYwph) {
                allDepSortedNames.add(ywphObj.getProjectDepName());
                processOkArr.add(ywphObj.getProcessOk());
                processDelayArr.add(ywphObj.getProcessDelay());
                delayRate.add(ywphObj.getDelayRate());
            }
            result.put("yAxis", allDepSortedNames);
            result.put("processOk", processOkArr);
            result.put("processDelay", processDelayArr);
            result.put("delayRate", delayRate);
        } catch (Exception e) {
            logger.error("Exception in queryYwph", e);
        }
        return result;
    }

    /**
     * 查询项目列表
     */
    public JsonPageResult<?> progressReportList(HttpServletRequest request, HttpServletResponse response,
        boolean doPage, String scene) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        try {
            if (ConstantUtil.PROJECT_PROGRESS_TYPE.equals(request.getAttribute(ConstantUtil.DESK_HOME))) {
                // 如果个人桌面的项目，查询则直接查询项目组人员包含当前人的即可
                params.put("userId", ContextUtil.getCurrentUserId());
                // 针对会签问题，个人的查询部门没有做多个Task合并
                List<Map<String, Object>> personProjectList = xcmgProjectReportDao.queryPersonProjectList(params);
                // 添加任务创建时间字段,personProjectList不包含planEndTime
                rdmZhglUtil.setTaskCreateTimeInfo(personProjectList, false);
                result.setData(personProjectList);
            } else {
                // 针对会签问题，getProjectList进行了多个Task合并
                result = xcmgProjectManager.getProjectList(request, response, params);
            }

            // 对于结果中的每个项目，进行项目进度、进度风险和积分的查询
            boolean progress = false;
            boolean risk = true;
            boolean score = false;
            String endTime = "";
            if (scene.equalsIgnoreCase("progressReportList")) {
                progress = true;
                score = true;
                endTime = params.get("queryEndTime") == null ? "" : params.get("queryEndTime").toString();
            } else if (scene.equalsIgnoreCase("deskHomeProgressReportList")) {
                progress = true;
            } else if (scene.equalsIgnoreCase("exportProjectProgressExcel")) {
                progress = true;
                score = true;
                endTime = params.get("queryEndTime") == null ? "" : params.get("queryEndTime").toString();
            }
            // hasRisk字段设置
            queryProjectProgressNumRiskAndScore(result.getData(), progress, risk, score, endTime);
            // 查询变更次数
            queryProjectChangeNumber(result.getData());
            List<Map<String, Object>> allData = result.getData();
            // 项目风险过滤
            if (params.containsKey("hasRisk")) {
                Iterator iterator = allData.iterator();
                while (iterator.hasNext()) {
                    Map<String, Object> oneProject = (Map<String, Object>)iterator.next();
                    if (oneProject.get("hasRisk") == null
                        || !oneProject.get("hasRisk").toString().equalsIgnoreCase(params.get("hasRisk").toString())) {
                        iterator.remove();
                    }
                }
            }
            // 总数设置并分页
            result.setTotal(allData.size());
            List<Map<String, Object>> finalSubProjectList = new ArrayList<Map<String, Object>>();
            if (doPage) {
                // 根据分页进行subList截取
                int pageIndex = RequestUtil.getInt(request, "pageIndex", 0);
                int pageSize = RequestUtil.getInt(request, "pageSize", Page.DEFAULT_PAGE_SIZE);
                int startSubListIndex = pageIndex * pageSize;
                int endSubListIndex = startSubListIndex + pageSize;
                if (startSubListIndex < allData.size()) {
                    finalSubProjectList = allData.subList(startSubListIndex,
                        endSubListIndex < allData.size() ? endSubListIndex : allData.size());
                }
            } else {
                finalSubProjectList = allData;
            }
            result.setData(finalSubProjectList);
        } catch (Exception e) {
            logger.error("Exception in progressReportList", e);
            result.setSuccess(false);
            result.setMessage("系统异常！");
        }
        return result;
    }

    /**
     * 查询项目的进度数据、风险和实际得分
     *
     * @param projectList
     * @param progress
     *            是否计算进度
     * @param risk
     *            是否计算风险
     * @param score
     *            是否计算得分
     * @param endTime
     *            进度和得分计算时的截止时间，如果没有则按照当前时间计算
     */
    private void queryProjectProgressNumRiskAndScore(List<Map<String, Object>> projectList, boolean progress,
        boolean risk, boolean score, String endTime) throws ParseException {
        if (progress) {
            queryManyProjectProgress(projectList, endTime);
        }
        if (risk) {
            queryManyProjectRisk(projectList, endTime);
        }
        if (score) {
            queryManyProjectScore(projectList, endTime);
        }
    }

    // 查询项目的进度数据
    private void queryManyProjectProgress(List<Map<String, Object>> projectList, String endTime) {
        // 筛选需要查询的项目
        Map<String, Map<String, Object>> projectId2Obj = new HashMap<>();
        for (Map<String, Object> oneProject : projectList) {
            // 查询到当前时间的进度
            if (StringUtils.isBlank(endTime)) {
                if (oneProject.get("status") == null
                    || "DRAFTED".equalsIgnoreCase(oneProject.get("status").toString())) {
                    oneProject.put("progressNum", 0);
                    continue;
                }
                if ("SUCCESS_END".equalsIgnoreCase(oneProject.get("status").toString())) {
                    oneProject.put("progressNum", 100);
                    continue;
                }
                oneProject.put("progressNum", 0);
                projectId2Obj.put(oneProject.get("projectId").toString(), oneProject);
            } else {
                oneProject.put("progressNum", 0);
                projectId2Obj.put(oneProject.get("projectId").toString(), oneProject);
            }
        }
        if (projectId2Obj.isEmpty()) {
            return;
        }
        // 对所有需要查询的集中查询
        Map<String, Object> params = new HashMap<>();
        params.put("projectIds", new ArrayList<>(projectId2Obj.keySet()));
        if (StringUtils.isNotBlank(endTime)) {
            params.put("queryEndTime", endTime);
        }
        List<JSONObject> progressDataList = xcmgProjectReportDao.queryProjectProgress(params);
        if (progressDataList != null && !progressDataList.isEmpty()) {
            for (JSONObject oneData : progressDataList) {
                projectId2Obj.get(oneData.getString("projectId")).put("progressNum",
                    oneData.getDoubleValue("progressNum"));
            }
        }
    }

    // 计算单个项目进度
    public void queryProjectProgressNum(Map<String, Object> oneProject, String endTime) {
        // 当前的进度
        if (StringUtils.isBlank(endTime)) {
            if (oneProject.get("status") == null) {
                oneProject.put("progressNum", 0);
                return;
            }
            if ("DRAFTED".equalsIgnoreCase(oneProject.get("status").toString())) {
                oneProject.put("progressNum", 0);
            } else if ("SUCCESS_END".equalsIgnoreCase(oneProject.get("status").toString())) {
                oneProject.put("progressNum", 100);
            } else {
                Map<String, Object> params = new HashMap<>(16);
                params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
                params.put("projectId", oneProject.get("projectId").toString());
                params.put("currentStageNo", oneProject.get("currentStageNo").toString());
                List<Map<String, Object>> progressInfos = xcmgProjectReportDao.projectProgressReport(params);
                int progressNum = 0;
                if (progressInfos != null && !progressInfos.isEmpty()) {
                    for (int i = 0; i < progressInfos.size() - 1; i++) {
                        progressNum += Integer.parseInt(progressInfos.get(i).get("stagePercent").toString());
                    }
                }
                oneProject.put("progressNum", progressNum);
            }
        } else {
            // 到某个截止时间的进度
            Map<String, Object> params = new HashMap<>(16);
            params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            params.put("projectId", oneProject.get("projectId").toString());
            params.put("queryEndTime", endTime);
            List<Map<String, Object>> progressInfos = xcmgProjectReportDao.projectProgressReport(params);
            int progressNum = 0;
            if (progressInfos != null && !progressInfos.isEmpty()) {
                for (int i = 0; i < progressInfos.size(); i++) {
                    progressNum += Integer.parseInt(progressInfos.get(i).get("stagePercent").toString());
                }
            }
            oneProject.put("progressNum", progressNum);
        }
    }

    // 查询多个项目的风险
    private void queryManyProjectRisk(List<Map<String, Object>> projectList, String endTime) {
        // 筛选需要查询的项目
        Map<String, Map<String, Object>> projectId2Obj = new HashMap<>();
        for (Map<String, Object> oneProject : projectList) {
            // 查询到当前时间的进度
            if (StringUtils.isBlank(endTime)) {
                String status = "";
                // 项目状态不为空，获取状态
                if (oneProject.get("status") != null) {
                    status = oneProject.get("status").toString();
                }
                // 项目状态为空、起草状态设置风险为3
                if (StringUtils.isBlank(status) || "DRAFTED".equalsIgnoreCase(status)) {
                    oneProject.put("hasRisk", 3);
                    continue;
                }
                // 项目状态为成功完成设置风险为0
                if ("SUCCESS_END".equalsIgnoreCase(status)) {
                    oneProject.put("hasRisk", 0);
                    continue;
                }
                // 项目状态为不正常执行设置风险为3，项目未启动或已经停止
                if (!"RUNNING".equalsIgnoreCase(status)) {
                    oneProject.put("hasRisk", 3);
                    continue;
                }
                // 正常运行中的才去查询风险
                oneProject.put("hasRisk", 0);// 赋初始值
                projectId2Obj.put(oneProject.get("projectId").toString(), oneProject);
            } else {
                // 指定时间的风险目前不再做处理，意义不大
                oneProject.put("hasRisk", 0);
                continue;
            }
        }
        if (projectId2Obj.isEmpty()) {
            return;
        }
        // 对所有需要查询的集中查询
        Map<String, Object> params = new HashMap<>();
        params.put("projectIds", new ArrayList<>(projectId2Obj.keySet()));
        // 根据项目ID查询项目计划结束时间
        List<JSONObject> currentProgressDataList = xcmgProjectReportDao.queryProjectCurrentProgress(params);
        if (currentProgressDataList != null && !currentProgressDataList.isEmpty()) {
            String projectRiskPardonMillSecStr = WebAppUtil.getProperty("projectRiskPardonMillSec");
            if (StringUtils.isBlank(projectRiskPardonMillSecStr)) {
                // 5天的毫秒单位
                projectRiskPardonMillSecStr = "432000000";
            }
            long delayPardonTime = Long.parseLong(projectRiskPardonMillSecStr);
            // 根据项目的计划结束时间判断风险
            for (JSONObject oneData : currentProgressDataList) {
                Date planEndTime = oneData.getDate("planEndTime");
                // 针对计划时间不为空的项目进行风险设置
                try {
                    long currentTime =
                        DateFormatUtil.parseDate(XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd")).getTime();
                    if (planEndTime != null) {
                        int hasRisk = 0;
                        // 超过5天
                        if (currentTime - planEndTime.getTime() > delayPardonTime) {
                            hasRisk = 2;
                            long delayDay = (currentTime - planEndTime.getTime()) / (3600 * 1000 * 24);
                            oneData.put("delayDay", delayDay);
                        } else if (currentTime - planEndTime.getTime() <= delayPardonTime
                            && currentTime - planEndTime.getTime() > 0) {
                            // 项目延误未超过5天
                            hasRisk = 1;
                        }
                        projectId2Obj.get(oneData.getString("projectId")).put("hasRisk", hasRisk);
                    } else {
                        int hasRisk = 0;
                        long createTime = DateFormatUtil
                            .parseDate(
                                projectId2Obj.get(oneData.getString("projectId")).get("taskCreateTime").toString())
                            .getTime();
                        // // 计划时间为空，且实例创建超过30天设置为有风险
                        if (currentTime - createTime > 2592000000L) {
                            hasRisk = 4;
                            long delayDay = (currentTime - createTime) / (3600 * 1000 * 24);
                            oneData.put("delayDay", delayDay);
                        }
                        projectId2Obj.get(oneData.getString("projectId")).put("hasRisk", hasRisk);
                    }
                } catch (Exception e) {
                    logger.error("Exception in queryManyProjectRisk", e);
                }
            }

        }
    }

    // 查询项目变更次数
    private void queryProjectChangeNumber(List<Map<String, Object>> projectList) {
        Map<String, Map<String, Object>> projectId2Obj = new HashMap<>();
        for (Map<String, Object> oneProject : projectList) {
            String status = "";
            if (oneProject.get("status") != null) {
                status = oneProject.get("status").toString();
            }
            // 正常运行或者结束中的才去查询变更次数
            if (StringUtils.isNotBlank(status)
                && ("RUNNING".equalsIgnoreCase(status) || "SUCCESS_END".equalsIgnoreCase(status))) {
                oneProject.put("changeNumber", 0);// 赋初始值
                projectId2Obj.put(oneProject.get("projectId").toString(), oneProject);
            } else {
                oneProject.put("changeNumber", 0);
                continue;
            }

        }
        if (projectId2Obj.isEmpty()) {
            return;
        }
        // 统一查询
        Map<String, Object> params = new HashMap<>();
        params.put("projectIds", new ArrayList<>(projectId2Obj.keySet()));
        List<JSONObject> changeNumberDataList = xcmgProjectReportDao.queryChangeNumber(params);
        if (changeNumberDataList != null && !changeNumberDataList.isEmpty()) {
            for (JSONObject oneData : changeNumberDataList) {
                Integer changeNumeber = oneData.getInteger("changeNumber");
                projectId2Obj.get(oneData.getString("projectId")).put("changeNumber", changeNumeber);
            }
        }

    }

    // 计算项目风险,没有风险是0，延迟5天之内是1，延迟大于5天是2，不涉及是3,没有填写计划时间且延期超过30天是4
    public void queryProjectRisk(Map<String, Object> oneProject, String endTime) throws ParseException {
        // 当前的风险
        String status = "";
        if (oneProject.get("status") != null) {
            status = oneProject.get("status").toString();
        }
        if (StringUtils.isBlank(endTime)) {
            if ("DRAFTED".equalsIgnoreCase(status)) {
                oneProject.put("hasRisk", 3);
            } else if ("SUCCESS_END".equalsIgnoreCase(status)) {
                oneProject.put("hasRisk", 0);
            } else {
                int hasRisk = 0;
                Map<String, Object> params = new HashMap<>(16);
                params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
                params.put("projectId", CommonFuns.nullToString(oneProject.get("projectId")));
                params.put("currentStageNo", CommonFuns.nullToString(oneProject.get("currentStageNo")));
                List<Map<String, Object>> progressInfos = xcmgProjectReportDao.projectProgressReport(params);
                if (progressInfos != null && !progressInfos.isEmpty()) {
                    // 只有正常运行的，才需要判断延误风险
                    if ("RUNNING".equalsIgnoreCase(status)) {
                        Map<String, Object> current = progressInfos.get(progressInfos.size() - 1);
                        Date planEndTime = (Date)current.get("planEndTime");
                        long currentTime =
                            DateFormatUtil.parseDate(XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd")).getTime();
                        if (planEndTime != null) {
                            String projectRiskPardonMillSecStr = WebAppUtil.getProperty("projectRiskPardonMillSec");
                            if (StringUtils.isBlank(projectRiskPardonMillSecStr)) {
                                projectRiskPardonMillSecStr = "432000000";
                            }
                            long delayPardonTime = Long.parseLong(projectRiskPardonMillSecStr);
                            // 超过5天
                            if (currentTime - planEndTime.getTime() > delayPardonTime) {
                                hasRisk = 2;
                                long delayDay = (currentTime - planEndTime.getTime()) / (3600 * 1000 * 24);
                                oneProject.put("delayDay", delayDay);
                            } else if (currentTime - planEndTime.getTime() <= delayPardonTime
                                && currentTime - planEndTime.getTime() > 0) {
                                hasRisk = 1;
                            }
                        } else {
                            // 未填写计划时间且延误超过30天设置为有风险
                            long createTime =
                                DateFormatUtil.parseDate(oneProject.get("taskCreateTime").toString()).getTime();
                            if (currentTime - createTime > 2592000000L) {
                                hasRisk = 4;
                                long delayDay = (currentTime - createTime) / (3600 * 1000 * 24);
                                oneProject.put("delayDay", delayDay);
                            }
                        }
                    } else {
                        hasRisk = 3;
                    }
                }
                oneProject.put("hasRisk", hasRisk);
            }
        } else {
            oneProject.put("hasRisk", 0);
        }
    }

    // 计算项目得分
    private void queryManyProjectScore(List<Map<String, Object>> projectList, String endTime) {
        if (StringUtils.isBlank(endTime)) {
            endTime = XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss");
        }
        // 筛选需要查询的项目
        Map<String, Map<String, Object>> projectId2Obj = new HashMap<>();
        for (Map<String, Object> oneProject : projectList) {
            oneProject.put("stageScore", 0.0);
            projectId2Obj.put(oneProject.get("projectId").toString(), oneProject);
        }
        if (projectId2Obj.isEmpty()) {
            return;
        }
        // 对所有需要查询的集中查询
        Map<String, Object> params = new HashMap<>();
        params.put("projectIds", new ArrayList<>(projectId2Obj.keySet()));
        params.put("queryEndTime", endTime);
        List<JSONObject> scoreDataList = xcmgProjectScoreDao.queryStageScoreByEndTime(params);
        if (scoreDataList != null && !scoreDataList.isEmpty()) {
            for (JSONObject oneData : scoreDataList) {
                double stageScore = 0.0;
                if (oneData.get("stageScore") != null) {
                    stageScore = (double)Math.round(oneData.getDoubleValue("stageScore") * 1000) / 1000;
                }
                projectId2Obj.get(oneData.getString("projectId")).put("stageScore", stageScore);
            }
        }
    }

    /**
     * //获取人员积分查询列表
     */
    public JsonPageResult<?> getPersonScoreList(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            Map<String, Object> queryParams = new HashMap<>(16);
            getScoreListParams(queryParams, request);
            for (String key : queryParams.keySet()) {
                if (key.equals("endTime")) {
                    if (StringUtils.isNotBlank(XcmgProjectUtil.nullToString(queryParams.get(key)))) {
                        queryParams.put(key, DateUtil.formatDate(DateUtil
                            .addHour(DateUtil.parseDate(XcmgProjectUtil.nullToString(queryParams.get(key))), 16)));
                    }
                }
                if (key.equals("startTime")) {
                    if (StringUtils.isNotBlank(XcmgProjectUtil.nullToString(queryParams.get(key)))) {
                        queryParams.put(key, DateUtil.formatDate(DateUtil
                            .addHour(DateUtil.parseDate(XcmgProjectUtil.nullToString(queryParams.get(key))), -8)));
                    }
                }
                if (key.equals("projectEndTime")) {
                    if (StringUtils.isNotBlank(XcmgProjectUtil.nullToString(queryParams.get(key)))) {
                        queryParams.put(key, DateUtil.formatDate(DateUtil
                            .addHour(DateUtil.parseDate(XcmgProjectUtil.nullToString(queryParams.get(key))), 16)));
                    }
                }
                if (key.equals("projectStartTime")) {
                    if (StringUtils.isNotBlank(XcmgProjectUtil.nullToString(queryParams.get(key)))) {
                        queryParams.put(key, DateUtil.formatDate(DateUtil
                            .addHour(DateUtil.parseDate(XcmgProjectUtil.nullToString(queryParams.get(key))), -8)));
                    }
                }
            }
            /*增加权限判断过滤
            // 1、对于admin、分管领导、积分查看人员，直接查询。
            // 2、对于部门负责人，如果条件中没有部门条件，则增加部门条件后查询；如果有部门条件，部门不为本部门则返回空，等于本部门则查询。
            // 3、对于其他人，增加userId的过滤条件后查询。
            */
            // admin直接查询
            if ("admin".equals(ContextUtil.getCurrentUser().getUserNo())) {
                List<Map<String, Object>> pageDataList = xcmgProjectReportDao.personScoreList(queryParams);
                result.setData(pageDataList);
                queryParams.remove("startIndex");
                queryParams.remove("pageSize");
                List<Map<String, Object>> totalDataList = xcmgProjectReportDao.personScoreList(queryParams);
                result.setTotal(totalDataList.size());
                return result;
            }
            // 查询角色，确定是否是特权账号
            Map<String, Object> roleFilterParams = new HashMap<>(16);
            roleFilterParams.put("userId", ContextUtil.getCurrentUser().getUserId());
            roleFilterParams.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(roleFilterParams);
            boolean showAll = false;
            for (Map<String, Object> oneRole : currentUserRoles) {
                if (oneRole.get("REL_TYPE_KEY_").toString().equalsIgnoreCase("GROUP-USER-LEADER")
                    || oneRole.get("REL_TYPE_KEY_").toString().equalsIgnoreCase("GROUP-USER-BELONG")) {
                    if (RdmConst.AllDATA_QUERY_NAME.equalsIgnoreCase(oneRole.get("NAME_").toString())
                        || RdmConst.JSZXXMGLRY.equalsIgnoreCase(oneRole.get("NAME_").toString())
                        || RdmConst.GY_FGLD.equalsIgnoreCase(oneRole.get("NAME_").toString())) {
                        showAll = true;
                        break;
                    }
                }
            }
            if (showAll) {
                // 如果是挖掘机械研究院项目管理人员，则只查询挖掘机械研究院下的部门
                boolean isJSZXProjManagUsers =
                    commonInfoManager.judgeUserIsPointRole(RdmConst.JSZXXMGLRY, ContextUtil.getCurrentUserId());
                if (isJSZXProjManagUsers) {
                    Map<String, String> deptId2Name = commonInfoManager.queryDeptUnderJSZX();
                    //2024-3-29新增技术中心项目管理员也可以看到工艺的项目
                    boolean isGYProjManagUsers =
                            commonInfoManager.judgeUserIsPointRole(RdmConst.GYJSBXMGLRY, ContextUtil.getCurrentUserId());
                    if (isGYProjManagUsers) {
                        Map<String, Object> param = new HashMap<>();
                        param.put("deptName", RdmConst.GYJSB_NAME);
                        List<JSONObject> deptList = xcmgProjectReportDao.queryDeptByName(param);
                        if (deptList != null && !deptList.isEmpty()) {
                            deptId2Name.put(deptList.get(0).getString("GROUP_ID_"), deptList.get(0).getString("NAME_"));
                        }
                    }
                    queryParams.put("deptIds", deptId2Name.keySet());
                    //2024-4-29新增技术中心项目管理员也可以看到应用技术部的项目
                    boolean isYYProjManagUsers =
                            commonInfoManager.judgeUserIsPointRole(RdmConst.YYJSBXMGLRY, ContextUtil.getCurrentUserId());
                    if (isYYProjManagUsers) {
                        Map<String, Object> param = new HashMap<>();
                        param.put("deptName", RdmConst.YYJSB_NAME);
                        List<JSONObject> deptList = xcmgProjectReportDao.queryDeptByName(param);
                        if (deptList != null && !deptList.isEmpty()) {
                            deptId2Name.put(deptList.get(0).getString("GROUP_ID_"), deptList.get(0).getString("NAME_"));
                        }
                    }
                    queryParams.put("deptIds", deptId2Name.keySet());
                } else {
                    boolean isGYFGLD =
                        commonInfoManager.judgeUserIsPointRole(RdmConst.GY_FGLD, ContextUtil.getCurrentUserId());
                    if (isGYFGLD) {
                        Map<String, Object> param = new HashMap<>();
                        param.put("deptName", RdmConst.GYJSB_NAME);
                        List<JSONObject> deptList = xcmgProjectReportDao.queryDeptByName(param);
                        if (deptList != null && !deptList.isEmpty()) {
                            queryParams.put("deptIds", Arrays.asList(deptList.get(0).getString("GROUP_ID_")));
                        }
                    }
                }
                List<Map<String, Object>> pageDataList = xcmgProjectReportDao.personScoreList(queryParams);
                result.setData(pageDataList);
                queryParams.remove("pageSize");
                queryParams.remove("startIndex");
                List<Map<String, Object>> totalDataList = xcmgProjectReportDao.personScoreList(queryParams);
                result.setTotal(totalDataList.size());
                return result;
            }

            // 部门负责人
            JSONObject currentUserDepInfo = xcmgProjectManager.isCurrentUserDepRespman();
            if (!"success".equals(currentUserDepInfo.getString("result"))) {
                result.setData(Collections.emptyList());
                result.setTotal(0);
                return result;
            }
            boolean isDepRespMan = currentUserDepInfo.getBoolean("isDepRespMan");
            String currentUserMainDepId = currentUserDepInfo.getString("currentUserMainDepId");
            String currentUserMainDepName = currentUserDepInfo.getString("currentUserMainDepName");
            if (isDepRespMan) {
                String depQueryParam =
                    queryParams.get("userDepId") == null ? "" : queryParams.get("userDepId").toString();
                if (StringUtils.isNotBlank(depQueryParam) && !depQueryParam.contains(currentUserMainDepId)) {
                    result.setData(Collections.emptyList());
                    result.setTotal(0);
                    return result;
                }
                String[] deptIds = currentUserMainDepId.split(",", -1);
                queryParams.put("userDepId", Arrays.asList(deptIds));
                List<Map<String, Object>> pageDataList = xcmgProjectReportDao.personScoreList(queryParams);
                result.setData(pageDataList);
                queryParams.remove("startIndex");
                queryParams.remove("pageSize");
                List<Map<String, Object>> totalDataList = xcmgProjectReportDao.personScoreList(queryParams);
                result.setTotal(totalDataList.size());
                return result;
            }

            // 普通员工
            queryParams.put("userId", ContextUtil.getCurrentUser().getUserId());
            List<Map<String, Object>> pageDataList = xcmgProjectReportDao.personScoreList(queryParams);
            result.setData(pageDataList);
            queryParams.remove("startIndex");
            queryParams.remove("pageSize");
            List<Map<String, Object>> totalDataList = xcmgProjectReportDao.personScoreList(queryParams);
            result.setTotal(totalDataList.size());
            return result;
        } catch (Exception e) {
            logger.error("Exception in getPersonScoreList", e);
            result.setData(Collections.emptyList());
            result.setTotal(0);
            result.setSuccess(false);
            return result;
        }
    }

    /**
     * //获取导出人员积分查询列表
     */
    public List<Map<String, Object>> getExportPersonScoreList(HttpServletRequest request,
        HttpServletResponse response) {
        try {
            Map<String, Object> queryParams = new HashMap<>(16);
            getScoreListParams(queryParams, request);
            for (String key : queryParams.keySet()) {
                if (key.equals("endTime")) {
                    if (StringUtils.isNotBlank(XcmgProjectUtil.nullToString(queryParams.get(key)))) {
                        queryParams.put(key, DateUtil.formatDate(DateUtil
                            .addHour(DateUtil.parseDate(XcmgProjectUtil.nullToString(queryParams.get(key))), 16)));
                    }
                }
                if (key.equals("startTime")) {
                    if (StringUtils.isNotBlank(XcmgProjectUtil.nullToString(queryParams.get(key)))) {
                        queryParams.put(key, DateUtil.formatDate(DateUtil
                            .addHour(DateUtil.parseDate(XcmgProjectUtil.nullToString(queryParams.get(key))), -8)));
                    }
                }
                if (key.equals("projectEndTime")) {
                    if (StringUtils.isNotBlank(XcmgProjectUtil.nullToString(queryParams.get(key)))) {
                        queryParams.put(key, DateUtil.formatDate(DateUtil
                            .addHour(DateUtil.parseDate(XcmgProjectUtil.nullToString(queryParams.get(key))), 16)));
                    }
                }
                if (key.equals("projectStartTime")) {
                    if (StringUtils.isNotBlank(XcmgProjectUtil.nullToString(queryParams.get(key)))) {
                        queryParams.put(key, DateUtil.formatDate(DateUtil
                            .addHour(DateUtil.parseDate(XcmgProjectUtil.nullToString(queryParams.get(key))), -8)));
                    }
                }
            }
            queryParams.remove("startIndex");
            queryParams.remove("pageSize");
            if ("admin".equals(ContextUtil.getCurrentUser().getUserNo())) {
                List<Map<String, Object>> totalDataList = xcmgProjectReportDao.personScoreList(queryParams);
                return totalDataList;
            }
            // 查询角色，确定是否是特权账号
            Map<String, Object> roleFilterParams = new HashMap<>(16);
            roleFilterParams.put("userId", ContextUtil.getCurrentUser().getUserId());
            roleFilterParams.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(roleFilterParams);
            boolean showAll = false;
            for (Map<String, Object> oneRole : currentUserRoles) {
                if (oneRole.get("REL_TYPE_KEY_").toString().equalsIgnoreCase("GROUP-USER-LEADER")
                    || oneRole.get("REL_TYPE_KEY_").toString().equalsIgnoreCase("GROUP-USER-BELONG")) {
                    if ("分管领导".equalsIgnoreCase(oneRole.get("NAME_").toString())
                        || RdmConst.JSZXXMGLRY.equalsIgnoreCase(oneRole.get("NAME_").toString())) {
                        showAll = true;
                        break;
                    }
                }
            }
            if (showAll) {
                List<Map<String, Object>> totalDataList = xcmgProjectReportDao.personScoreList(queryParams);
                return totalDataList;
            }

            // 部门负责人
            JSONObject currentUserDepInfo = xcmgProjectManager.isCurrentUserDepRespman();
            if (!"success".equals(currentUserDepInfo.getString("result"))) {
                return Collections.emptyList();
            }
            boolean isDepRespMan = currentUserDepInfo.getBoolean("isDepRespMan");
            String currentUserMainDepId = currentUserDepInfo.getString("currentUserMainDepId");
            String currentUserMainDepName = currentUserDepInfo.getString("currentUserMainDepName");
            if (isDepRespMan) {
                String depQueryParam =
                    queryParams.get("userDepId") == null ? "" : queryParams.get("userDepId").toString();
                if (StringUtils.isNotBlank(depQueryParam) && !depQueryParam.contains(currentUserMainDepId)) {
                    return Collections.emptyList();
                }
                String[] deptIds = currentUserMainDepId.split(",", -1);
                queryParams.put("userDepId", Arrays.asList(deptIds));
                List<Map<String, Object>> totalDataList = xcmgProjectReportDao.personScoreList(queryParams);
                return totalDataList;
            }

            // 普通员工
            queryParams.put("userId", ContextUtil.getCurrentUser().getUserId());
            List<Map<String, Object>> totalDataList = xcmgProjectReportDao.personScoreList(queryParams);
            return totalDataList;
        } catch (Exception e) {
            logger.error("Exception in getPersonScoreList", e);
            return Collections.emptyList();
        }
    }

    /**
     * //获取部门积分查询列表
     */
    public List<Map<String, Object>> getExportDeptScoreList(HttpServletRequest request, HttpServletResponse response) {
        try {
            Map<String, Object> queryParams = new HashMap<>(16);
            getScoreListParams(queryParams, request);
            for (String key : queryParams.keySet()) {
                if (key.equals("startTime")) {
                    if (StringUtils.isNotBlank(XcmgProjectUtil.nullToString(queryParams.get(key)))) {
                        queryParams.put(key, DateUtil.formatDate(DateUtil
                            .addHour(DateUtil.parseDate(XcmgProjectUtil.nullToString(queryParams.get(key))), -8)));
                    }
                }
                if (key.equals("endTime")) {
                    if (StringUtils.isNotBlank(XcmgProjectUtil.nullToString(queryParams.get(key)))) {
                        queryParams.put(key, DateUtil.formatDate(DateUtil
                            .addHour(DateUtil.parseDate(XcmgProjectUtil.nullToString(queryParams.get(key))), 16)));
                    }
                }
            }
            queryParams.remove("startIndex");
            queryParams.remove("pageSize");

            if ("admin".equals(ContextUtil.getCurrentUser().getUserNo())) {
                List<Map<String, Object>> totalDataList = xcmgProjectReportDao.deptScoreList(queryParams);
                return totalDataList;
            }
            // 查询角色，确定是否是特权账号
            Map<String, Object> roleFilterParams = new HashMap<>(16);
            roleFilterParams.put("userId", ContextUtil.getCurrentUser().getUserId());
            roleFilterParams.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(roleFilterParams);
            boolean showAll = false;
            for (Map<String, Object> oneRole : currentUserRoles) {
                if (oneRole.get("REL_TYPE_KEY_").toString().equalsIgnoreCase("GROUP-USER-LEADER")
                    || oneRole.get("REL_TYPE_KEY_").toString().equalsIgnoreCase("GROUP-USER-BELONG")) {
                    if ("分管领导".equalsIgnoreCase(oneRole.get("NAME_").toString())
                        || RdmConst.JSZXXMGLRY.equalsIgnoreCase(oneRole.get("NAME_").toString())) {
                        showAll = true;
                        break;
                    }
                }
            }
            if (showAll) {
                List<Map<String, Object>> totalDataList = xcmgProjectReportDao.deptScoreList(queryParams);
                return totalDataList;
            }

            // 部门负责人
            JSONObject currentUserDepInfo = xcmgProjectManager.isCurrentUserDepRespman();
            if (!"success".equals(currentUserDepInfo.getString("result"))) {
                return Collections.emptyList();
            }
            boolean isDepRespMan = currentUserDepInfo.getBoolean("isDepRespMan");
            String currentUserMainDepId = currentUserDepInfo.getString("currentUserMainDepId");
            String[] deptIds = currentUserMainDepId.split(",", -1);
            String currentUserMainDepName = currentUserDepInfo.getString("currentUserMainDepName");
            if (isDepRespMan) {
                String depQueryParam =
                    queryParams.get("userDepId") == null ? "" : queryParams.get("userDepId").toString();
                if (StringUtils.isNotBlank(depQueryParam) && !depQueryParam.contains(currentUserMainDepId)) {
                    return Collections.emptyList();
                }
                queryParams.put("userDepId", Arrays.asList(deptIds));
                List<Map<String, Object>> totalDataList = xcmgProjectReportDao.deptScoreList(queryParams);
                return totalDataList;
            }

            // 普通员工
            String depQueryParam = queryParams.get("userDepId") == null ? "" : queryParams.get("userDepId").toString();
            if (StringUtils.isNotBlank(depQueryParam) && !depQueryParam.contains(currentUserMainDepId)) {
                return Collections.emptyList();
            }
            queryParams.put("userDepId", Arrays.asList(deptIds));
            List<Map<String, Object>> totalDataList = xcmgProjectReportDao.deptScoreList(queryParams);
            return totalDataList;
        } catch (Exception e) {
            logger.error("Exception in getDeptScoreList", e);
            return Collections.emptyList();
        }
    }

    /**
     * //获取部门积分查询列表
     */
    public JsonPageResult<?> getDeptScoreList(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            Map<String, Object> queryParams = new HashMap<>(16);
            getScoreListParams(queryParams, request);
            for (String key : queryParams.keySet()) {
                if (key.equals("startTime")) {
                    if (StringUtils.isNotBlank(XcmgProjectUtil.nullToString(queryParams.get(key)))) {
                        queryParams.put(key, DateUtil.formatDate(DateUtil
                            .addHour(DateUtil.parseDate(XcmgProjectUtil.nullToString(queryParams.get(key))), -8)));
                    }
                }
                if (key.equals("endTime")) {
                    if (StringUtils.isNotBlank(XcmgProjectUtil.nullToString(queryParams.get(key)))) {
                        queryParams.put(key, DateUtil.formatDate(DateUtil
                            .addHour(DateUtil.parseDate(XcmgProjectUtil.nullToString(queryParams.get(key))), 16)));
                    }
                }
            }
            if ("admin".equals(ContextUtil.getCurrentUser().getUserNo())) {
                List<Map<String, Object>> pageDataList = xcmgProjectReportDao.deptScoreList(queryParams);
                result.setData(pageDataList);
                queryParams.remove("startIndex");
                queryParams.remove("pageSize");
                List<Map<String, Object>> totalDataList = xcmgProjectReportDao.deptScoreList(queryParams);
                result.setTotal(totalDataList.size());
                return result;
            }
            // 查询角色，确定是否是特权账号
            Map<String, Object> roleFilterParams = new HashMap<>(16);
            roleFilterParams.put("userId", ContextUtil.getCurrentUser().getUserId());
            roleFilterParams.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(roleFilterParams);
            boolean showAll = false;
            for (Map<String, Object> oneRole : currentUserRoles) {
                if (oneRole.get("REL_TYPE_KEY_").toString().equalsIgnoreCase("GROUP-USER-LEADER")
                    || oneRole.get("REL_TYPE_KEY_").toString().equalsIgnoreCase("GROUP-USER-BELONG")) {
                    if ("分管领导".equalsIgnoreCase(oneRole.get("NAME_").toString())
                        || RdmConst.AllDATA_QUERY_NAME.equalsIgnoreCase(oneRole.get("NAME_").toString())
                        || RdmConst.JSZXXMGLRY.equalsIgnoreCase(oneRole.get("NAME_").toString())
                        || RdmConst.GY_FGLD.equalsIgnoreCase(oneRole.get("NAME_").toString())) {
                        showAll = true;
                        break;
                    }
                }
            }
            if (showAll) {
                // 如果是挖掘机械研究院项目管理人员，则只查询挖掘机械研究院下的部门
                boolean isJSZXProjManagUsers =
                    commonInfoManager.judgeUserIsPointRole(RdmConst.JSZXXMGLRY, ContextUtil.getCurrentUserId());
                if (isJSZXProjManagUsers) {
                    Map<String, String> deptId2Name = commonInfoManager.queryDeptUnderJSZX();
                    //2024-3-29新增技术中心项目管理员也可以看到工艺的项目
                    boolean isGYProjManagUsers =
                            commonInfoManager.judgeUserIsPointRole(RdmConst.GYJSBXMGLRY, ContextUtil.getCurrentUserId());
                    if (isGYProjManagUsers) {
                        Map<String, Object> param = new HashMap<>();
                        param.put("deptName", RdmConst.GYJSB_NAME);
                        List<JSONObject> deptList = xcmgProjectReportDao.queryDeptByName(param);
                        if (deptList != null && !deptList.isEmpty()) {
                            deptId2Name.put(deptList.get(0).getString("GROUP_ID_"), deptList.get(0).getString("NAME_"));
                        }
                    }
                    queryParams.put("deptIds", deptId2Name.keySet());
                    //2024-4-29新增技术中心项目管理员也可以看到应用技术部的项目
                    boolean isYYProjManagUsers =
                            commonInfoManager.judgeUserIsPointRole(RdmConst.YYJSBXMGLRY, ContextUtil.getCurrentUserId());
                    if (isYYProjManagUsers) {
                        Map<String, Object> param = new HashMap<>();
                        param.put("deptName", RdmConst.YYJSB_NAME);
                        List<JSONObject> deptList = xcmgProjectReportDao.queryDeptByName(param);
                        if (deptList != null && !deptList.isEmpty()) {
                            deptId2Name.put(deptList.get(0).getString("GROUP_ID_"), deptList.get(0).getString("NAME_"));
                        }
                    }
                    queryParams.put("deptIds", deptId2Name.keySet());
                } else {
                    boolean isGYFGLD =
                        commonInfoManager.judgeUserIsPointRole(RdmConst.GY_FGLD, ContextUtil.getCurrentUserId());
                    if (isGYFGLD) {
                        Map<String, Object> param = new HashMap<>();
                        param.put("deptName", RdmConst.GYJSB_NAME);
                        List<JSONObject> deptList = xcmgProjectReportDao.queryDeptByName(param);
                        if (deptList != null && !deptList.isEmpty()) {
                            queryParams.put("deptIds", Arrays.asList(deptList.get(0).getString("GROUP_ID_")));
                        }
                    }
                }
                List<Map<String, Object>> pageDataList = xcmgProjectReportDao.deptScoreList(queryParams);
                result.setData(pageDataList);
                queryParams.remove("pageSize");
                queryParams.remove("startIndex");
                List<Map<String, Object>> totalDataList = xcmgProjectReportDao.deptScoreList(queryParams);
                result.setTotal(totalDataList.size());
                return result;
            }

            // 部门负责人
            JSONObject currentUserDepInfo = xcmgProjectManager.isCurrentUserDepRespman();
            if (!"success".equals(currentUserDepInfo.getString("result"))) {
                result.setData(Collections.emptyList());
                result.setTotal(0);
                return result;
            }
            boolean isDepRespMan = currentUserDepInfo.getBoolean("isDepRespMan");
            String currentUserMainDepId = currentUserDepInfo.getString("currentUserMainDepId");
            String[] deptIds = currentUserMainDepId.split(",", -1);
            String currentUserMainDepName = currentUserDepInfo.getString("currentUserMainDepName");
            if (isDepRespMan) {
                String depQueryParam =
                    queryParams.get("userDepId") == null ? "" : queryParams.get("userDepId").toString();
                if (StringUtils.isNotBlank(depQueryParam) && !depQueryParam.contains(currentUserMainDepId)) {
                    result.setData(Collections.emptyList());
                    result.setTotal(0);
                    return result;
                }
                queryParams.put("userDepId", Arrays.asList(deptIds));
                List<Map<String, Object>> pageDataList = xcmgProjectReportDao.deptScoreList(queryParams);
                result.setData(pageDataList);
                queryParams.remove("startIndex");
                queryParams.remove("pageSize");
                List<Map<String, Object>> totalDataList = xcmgProjectReportDao.deptScoreList(queryParams);
                result.setTotal(totalDataList.size());
                return result;
            }

            // 普通员工
            String depQueryParam = queryParams.get("userDepId") == null ? "" : queryParams.get("userDepId").toString();
            if (StringUtils.isNotBlank(depQueryParam) && !depQueryParam.contains(currentUserMainDepId)) {
                result.setData(Collections.emptyList());
                result.setTotal(0);
                return result;
            }
            queryParams.put("userDepId", Arrays.asList(deptIds));
            List<Map<String, Object>> pageDataList = xcmgProjectReportDao.deptScoreList(queryParams);
            result.setData(pageDataList);
            queryParams.remove("startIndex");
            queryParams.remove("pageSize");
            List<Map<String, Object>> totalDataList = xcmgProjectReportDao.deptScoreList(queryParams);
            result.setTotal(totalDataList.size());
            return result;
        } catch (Exception e) {
            logger.error("Exception in getDeptScoreList", e);
            result.setData(Collections.emptyList());
            result.setTotal(0);
            result.setSuccess(false);
            return result;
        }
    }

    public void exportPersonScoreExcel(HttpServletResponse response, HttpServletRequest request) {
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String excelName = nowDate + "个人积分列表";
        Map<String, Object> queryParams = new HashMap<>(16);
        getScoreListParams(queryParams, request);
        String startTime = XcmgProjectUtil.nullToString(queryParams.get("startTime"));
        String entTime = XcmgProjectUtil.nullToString(queryParams.get("endTime"));
        String title = "员工积分列表";
        if (!"".equals(startTime) && "".equals(entTime)) {
            title = startTime + "至今" + title;
        }
        if ("".equals(startTime) && !"".equals(entTime)) {
            title = "截止到" + entTime + title;
        }
        if (!"".equals(startTime) && !"".equals(entTime)) {
            title = startTime + "至" + entTime + title;
        }
        List list = getExportPersonScoreList(request, response);
        String[] fieldNames = {"员工姓名", "员工部门", "员工岗位", "员工职级", "获得积分", "身份证号"};
        String[] fieldCodes = {"name", "deptName", "workName", "dutyName", "stageScore", "CERT_NO_"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(list, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }

    public void exportDeptScoreExcel(HttpServletResponse response, HttpServletRequest request) {
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String excelName = nowDate + "部门积分列表";
        Map<String, Object> queryParams = new HashMap<>(16);
        getScoreListParams(queryParams, request);
        String startTime = XcmgProjectUtil.nullToString(queryParams.get("startTime"));
        String entTime = XcmgProjectUtil.nullToString(queryParams.get("endTime"));
        String title = "员工积分列表";
        if (!"".equals(startTime) && "".equals(entTime)) {
            title = startTime + "至今" + title;
        }
        if ("".equals(startTime) && !"".equals(entTime)) {
            title = "截止到" + entTime + title;
        }
        if (!"".equals(startTime) && !"".equals(entTime)) {
            title = startTime + "至" + entTime + title;
        }
        List list = getExportDeptScoreList(request, response);
        String[] fieldNames = {"部门名称", "员工人数", "总积分", "平均积分"};
        String[] fieldCodes = {"deptName", "userNum", "totalScore", "avgScore"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(list, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }

    /**
     * //将过滤条件、排序等信息传入
     */
    private void getScoreListParams(Map<String, Object> params, HttpServletRequest request) {
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        String sortOrder = request.getParameter("sortOrder");
        String sortField = request.getParameter("sortField");
        if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)) {
            params.put("sortField", sortField);
            params.put("sortOrder", sortOrder);
        }

        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String value = jsonArray.getJSONObject(i).getString("value");
                String name = jsonArray.getJSONObject(i).getString("name");
                if (StringUtils.isNotBlank(value)) {
                    if ("userDepId".equals(name)) {
                        String[] deptIds = value.split(",", -1);
                        params.put(name, Arrays.asList(deptIds));
                    } else {
                        params.put(name, value);
                    }
                }
            }
        }
        // 增加分页条件
        params.put("startIndex", 0);
        params.put("pageSize", 20);
        String pageIndex = request.getParameter("pageIndex");
        String pageSize = request.getParameter("pageSize");
        if (StringUtils.isNotBlank(pageIndex) && StringUtils.isNotBlank(pageSize)) {
            params.put("startIndex", Integer.parseInt(pageSize) * Integer.parseInt(pageIndex));
            params.put("pageSize", Integer.parseInt(pageSize));
        }

    }

    public JSONObject deskHomePersonScore(YdjfDepsumscore ydjfDepsumscoreParam) {
        // 数据库中存储的时间是UTC时间，需要将查询条件转为UTC时间
        String queryTime = ydjfDepsumscoreParam.getTime() + "-01T00:00:00";
        String queryTimeFrom = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(queryTime), -8));
        String queryTimeTo =
            DateUtil.formatDate(DateUtil.addHour(DateUtil.add(DateUtil.parseDate(queryTime), Calendar.MONTH, 1), -8));
        Map<String, Object> params = new HashMap<>(16);
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        params.put("userId", ContextUtil.getCurrentUser().getUserId());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdfMonth = new SimpleDateFormat("yyyy-MM");
        Date startTime = null;
        Date endTime = null;
        Date orgTime = null;
        List<String> monthList = new ArrayList<>();
        List<String> stageScoreList = new ArrayList<>();
        JSONObject resultJson = new JSONObject();
        try {
            startTime = sdf.parse(queryTimeFrom);
            endTime = sdf.parse(queryTimeTo);
            orgTime = sdfMonth.parse(queryTime);
            Calendar rightNow = Calendar.getInstance();
            for (int i = 0; i < 6; i++) {
                rightNow.setTime(orgTime);
                rightNow.add(Calendar.MONTH, -i);
                Date monthDate = rightNow.getTime();
                String monthDate2 = sdfMonth.format(monthDate);
                monthList.add(monthDate2);
                rightNow.setTime(startTime);
                rightNow.add(Calendar.MONTH, -i);
                Date dt1 = rightNow.getTime();
                String timeFrom = sdf.format(dt1);
                params.put("timeFrom", timeFrom);
                rightNow.setTime(endTime);
                rightNow.add(Calendar.MONTH, -i);
                Date dt2 = rightNow.getTime();
                String timeTo = sdf.format(dt2);
                params.put("timeTo", timeTo);
                List<Map<String, Object>> queryDepScores = xcmgProjectReportDao.deskHomePersonScore(params);
                if (queryDepScores != null && queryDepScores.size() > 0) {
                    // map转String
                    String json = JSON.toJSONString(queryDepScores.get(0));
                    // String转json
                    JSONObject temp = JSON.parseObject(json);
                    stageScoreList.add(temp.getString("stageScore"));
                } else {
                    stageScoreList.add("0");
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        resultJson.put("times", monthList);
        resultJson.put("scores", stageScoreList);
        return resultJson;
    }

    public JSONObject deskHomeProjectType(JSONObject postDataJson, HttpServletResponse response,
        HttpServletRequest request) throws Exception {
        JSONObject result = new JSONObject();
        try {
            Map<String, Object> params = new HashMap<>(16);
            // 查询项目的级别
            List<String> bigLevelNames = new ArrayList<>();
            List<Map<String, String>> mapLevel = xcmgProjectOtherDao.queryProjectLevel(params);
            if (mapLevel != null && !mapLevel.isEmpty()) {
                for (Map<String, String> oneLevel : mapLevel) {
                    bigLevelNames.add(oneLevel.get("levelName") + "级");
                }
            }
            result.put("levelList", bigLevelNames);
            // 查询项目的小类别
            params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            List<String> smallCategoryNames = new ArrayList<>();
            List<Map<String, String>> mapCategory = xcmgProjectOtherDao.queryProjectCategory(params);
            if (mapCategory != null && !mapCategory.isEmpty()) {
                for (Map<String, String> oneCatefory : mapCategory) {
                    // 去掉大类别
                    if (!"0".equals(oneCatefory.get("parentId"))) {
                        smallCategoryNames.add(oneCatefory.get("categoryName"));
                    }
                }
            }
            result.put("categoryList", smallCategoryNames);
            // 查询按照级别和类别汇总的项目个数
            postDataJson.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            postDataJson.put("userId", ContextUtil.getCurrentUserId());
            for (String key : postDataJson.keySet()) {
                if (key.equals("buildTimeTo") || key.equals("knotTimeTo")) {
                    if (StringUtils.isNotBlank(postDataJson.getString(key))) {
                        postDataJson.put(key,
                            DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(postDataJson.getString(key)), 16)));
                    }
                }
                if (key.equals("buildTimeFrom") || key.equals("knotTimeFrom")) {
                    if (StringUtils.isNotBlank(postDataJson.getString(key))) {
                        postDataJson.put(key,
                            DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(postDataJson.getString(key)), -8)));
                    }
                }
            }
            List<Map<String, Object>> categoryLevelProjNum =
                xcmgProjectReportDao.deskHomeProjectNumByCategoryLevel(postDataJson);
            Map<String, Map<String, Object>> level2Category2Num = new HashMap<>(16);
            for (Map<String, Object> oneMap : categoryLevelProjNum) {
                String categoryName = oneMap.get("categoryName").toString();
                String levelName = oneMap.get("levelName").toString();
                Long number = (Long)oneMap.get("number");
                if (!level2Category2Num.containsKey(levelName)) {
                    level2Category2Num.put(levelName, new HashMap<>(16));
                }
                level2Category2Num.get(levelName).put(categoryName, number);
            }
            // 组合数据
            List<JSONObject> seriesData = new ArrayList<>();
            for (int i = bigLevelNames.size() - 1; i >= 0; i--) {
                JSONObject oneSerie = new JSONObject();
                oneSerie.put("name", bigLevelNames.get(i));
                oneSerie.put("stack", "数量");
                oneSerie.put("type", "bar");
                List<Long> data = new ArrayList<>();
                Long numInt;
                for (String categoryName : smallCategoryNames) {
                    numInt = 0L;
                    if (level2Category2Num.containsKey(bigLevelNames.get(i))
                        && level2Category2Num.get(bigLevelNames.get(i)).containsKey(categoryName)) {
                        numInt = (Long)level2Category2Num.get(bigLevelNames.get(i)).get(categoryName);
                    }
                    data.add(numInt);
                }
                oneSerie.put("data", data);
                seriesData.add(oneSerie);
            }
            result.put("series", seriesData);
            // 计算每一个类别的总数
            List<Long> categorySum = new ArrayList<>();
            Long sum;
            for (int i = 0; i < smallCategoryNames.size(); i++) {
                sum = 0L;
                for (JSONObject jsonObject : seriesData) {
                    List<Long> data = (List<Long>)jsonObject.get("data");
                    sum += data.get(i);
                }
                categorySum.add(sum);
            }
            result.put("sumData", categorySum);
        } catch (Exception e) {
            logger.error("Exception in deskHomeProjectType", e);
        }
        return result;
    }

    public JSONObject deskHomeProjectProgress(HttpServletResponse response, HttpServletRequest request)
        throws Exception {
        JSONObject jsonObject = new JSONObject();
        JsonPageResult<?> result = progressReportList(request, response, false, "deskHomeProjectProgress");
        List finalSubProjectList = result.getData();
        int normal = 0;
        int delay = 0;
        int warning = 0;
        int delayInStartUp = 0;
        // 没有风险是0，延迟5天之内是1，延迟大于5天是2，不涉及是3,未填写计划时间且延误超过30天编号是4
        if (finalSubProjectList != null && finalSubProjectList.size() > 0) {
            for (int i = 0; i < finalSubProjectList.size(); i++) {
                String jsonString = JSON.toJSONString(finalSubProjectList.get(i));
                JSONObject sub = JSONObject.parseObject(jsonString);
                String risk = sub.getString("hasRisk");
                if ("0".equals(risk)) {
                    normal++;
                } else if ("1".equals(risk)) {
                    warning++;
                } else if ("2".equals(risk)) {
                    delay++;
                } else if ("4".equals(risk)) {
                    delayInStartUp++;
                }
            }
        }
        jsonObject.put("normal", normal);
        jsonObject.put("delay", delay);
        jsonObject.put("warning", warning);
        jsonObject.put("delayInStartUp", delayInStartUp);
        return jsonObject;
    }

    // 导出项目进度excel
    public void exportProjectProgressExcel(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = progressReportList(request, response, false, "exportProjectProgressExcel");
        List<Map<String, Object>> finalSubProjectList = result.getData();
        if (finalSubProjectList != null && !finalSubProjectList.isEmpty()) {
            Collections.sort(finalSubProjectList, new Comparator<Map<String, Object>>() {
                @Override
                public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                    if (o1.containsKey("mainDepId") && o2.containsKey("mainDepId")) {
                        return o1.get("mainDepId").toString().compareTo(o2.get("mainDepId").toString());
                    } else {
                        return 1;
                    }
                }
            });
        }
        // 按照部门统计项目的个数
        List<Integer> mainDepIdCount = new ArrayList<>();
        // 统计连续相同的部门ID的个数
        String currentMainDepId = "";
        int currentMainDepIdCount = 0;
        if (!finalSubProjectList.isEmpty()) {
            currentMainDepId = finalSubProjectList.get(0).get("mainDepId").toString();
            currentMainDepIdCount = 1;
        }
        for (int i = 0; i < finalSubProjectList.size(); i++) {
            Map<String, Object> oneMap = finalSubProjectList.get(i);
            if (oneMap.get("pointStandardScore") != null
                && StringUtils.isNotBlank(oneMap.get("pointStandardScore").toString())) {
                oneMap.put("standardScore", Integer.parseInt(oneMap.get("pointStandardScore").toString()));
            }
            oneMap.put("statusName", XcmgProjectUtil.convertProjectStatusCode2Name(oneMap.get("status")));
            String riskStr = oneMap.get("hasRisk").toString();
            String riskName = "";
            if ("0".equals(riskStr)) {
                riskName = "正常";
            } else if ("1".equals(riskStr)) {
                riskName = "轻微延误";
            } else if ("2".equals(riskStr)) {
                riskName = "严重延误";
            } else if ("4".equals(riskStr)) {
                riskName = "项目没有计划时间且延误超过30天";
            }
            oneMap.put("riskName", riskName);
            String progressNumStr = oneMap.get("progressNum").toString() + "%";
            oneMap.put("progressNumStr", progressNumStr);
            // 跳过第一个
            if (i != 0) {
                String thisMainDepId = oneMap.get("mainDepId").toString();
                if (thisMainDepId.equals(currentMainDepId)) {
                    currentMainDepIdCount++;
                } else {
                    mainDepIdCount.add(currentMainDepIdCount);
                    currentMainDepId = thisMainDepId;
                    currentMainDepIdCount = 1;
                }
            }
        }
        mainDepIdCount.add(currentMainDepIdCount);

        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "科技项目进度统计表";
        String excelName = nowDate + title;
        String[] fieldNames = {"牵头部门", "项目类别", "项目级别", "标准分", "项目名称", "项目编号", "项目来源", "项目负责人", "项目状态", "当前阶段", "当前处理人",
            "进度风险", "项目进度", "项目进度总积分"};
        String[] fieldCodes = {"mainDepName", "categoryName", "levelName", "standardScore", "projectName", "number",
            "sourceName", "respMan", "statusName", "currentStageName", "allTaskUserNames", "riskName", "progressNumStr",
            "stageScore"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(finalSubProjectList, fieldNames, fieldCodes, title);
        if (!mainDepIdCount.isEmpty()) {
            int sumRow = 2;
            // 合并发布年份列
            HSSFSheet sheet = wbObj.getSheetAt(0);
            for (int i = 0; i < mainDepIdCount.size(); i++) {
                int number = mainDepIdCount.get(i);
                if (number > 1) {
                    int startRow = sumRow;
                    int endRow = startRow + number - 1;
                    // 行号、列号从0开始，开始和结束都包含
                    sheet.addMergedRegionUnsafe(new CellRangeAddress(startRow, endRow, 0, 0));
                    sumRow = endRow + 1;
                } else {
                    sumRow++;
                }
            }
        }
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }

    /**
     * 此页面暂只对项目管理人员开放，因此不需要额外加条件
     *
     * @param request
     * @param response
     * @param doPage
     * @return
     */
    public JsonPageResult<?> evaluateScoreList(HttpServletRequest request, HttpServletResponse response,
        boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        // 通过条件筛选项目
        Map<String, Object> params = new HashMap<>();
        getListParams(params, request, doPage);
        // 如果是挖掘机械研究院项目管理人员，则只查询挖掘机械研究院下的部门
        boolean isJSZXProjManagUsers =
            commonInfoManager.judgeUserIsPointRole(RdmConst.JSZXXMGLRY, ContextUtil.getCurrentUserId());
        if (isJSZXProjManagUsers) {
            Map<String, String> deptId2Name = commonInfoManager.queryDeptUnderJSZX();
            //2024-3-29新增技术中心项目管理员也可以看到工艺的项目
            boolean isGYProjManagUsers =
                    commonInfoManager.judgeUserIsPointRole(RdmConst.GYJSBXMGLRY, ContextUtil.getCurrentUserId());
            if (isGYProjManagUsers) {
                Map<String, Object> param = new HashMap<>();
                param.put("deptName", RdmConst.GYJSB_NAME);
                List<JSONObject> deptList = xcmgProjectReportDao.queryDeptByName(param);
                if (deptList != null && !deptList.isEmpty()) {
                    deptId2Name.put(deptList.get(0).getString("GROUP_ID_"), deptList.get(0).getString("NAME_"));
                }
            }
            params.put("deptIds", deptId2Name.keySet());
            //2024-4-29新增技术中心项目管理员也可以看到应用技术部的项目
            boolean isYYProjManagUsers =
                    commonInfoManager.judgeUserIsPointRole(RdmConst.YYJSBXMGLRY, ContextUtil.getCurrentUserId());
            if (isYYProjManagUsers) {
                Map<String, Object> param = new HashMap<>();
                param.put("deptName", RdmConst.YYJSB_NAME);
                List<JSONObject> deptList = xcmgProjectReportDao.queryDeptByName(param);
                if (deptList != null && !deptList.isEmpty()) {
                    deptId2Name.put(deptList.get(0).getString("GROUP_ID_"), deptList.get(0).getString("NAME_"));
                }
            }
            params.put("deptIds", deptId2Name.keySet());
        }
        List<JSONObject> queryDataList = xcmgProjectReportDao.queryEvaluateScoreList(params);
        result.setData(queryDataList);
        if (doPage) {
            int countNum = xcmgProjectReportDao.countEvaluateScoreList(params);
            result.setTotal(countNum);
        }
        if (queryDataList == null || queryDataList.isEmpty()) {
            return result;
        }
        Set<String> queryStageInfoProjectIds = new HashSet<>();
        Set<String> queryRespManProjectIds = new HashSet<>();
        for (JSONObject oneProject : queryDataList) {
            if (StringUtils.isNotBlank(oneProject.getString("pointStandardScore"))) {
                oneProject.put("standardScore", Integer.parseInt(oneProject.getString("pointStandardScore")));
            }
            if (StringUtils.isNotBlank(oneProject.getString("standardScore"))) {
                queryStageInfoProjectIds.add(oneProject.getString("projectId"));
            }
            queryRespManProjectIds.add(oneProject.getString("projectId"));
        }
        // 查询负责人
        Map<String, Object> queryRespManParams = new HashMap<>();
        queryRespManParams.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        queryRespManParams.put("projectIds", queryRespManProjectIds);
        List<Map<String, Object>> respManInfo = xcmgProjectOtherDao.queryRespManByProjectIds(queryRespManParams);
        Map<String, JSONObject> projectId2RespMan = new HashMap<>();
        for (Map<String, Object> oneMap : respManInfo) {
            JSONObject userObj = new JSONObject();
            userObj.put("userId", oneMap.get("USER_ID_").toString());
            userObj.put("userName", oneMap.get("FULLNAME_").toString());
            projectId2RespMan.put(oneMap.get("projectId").toString(), userObj);
        }
        for (JSONObject oneProject : queryDataList) {
            String respManName = "";
            String respManId = "";
            if (projectId2RespMan.containsKey(oneProject.getString("projectId"))) {
                respManName = projectId2RespMan.get(oneProject.getString("projectId")).getString("userName");
                respManId = projectId2RespMan.get(oneProject.getString("projectId")).getString("userId");
            }
            oneProject.put("respMan", respManName);
            oneProject.put("respManId", respManId);
        }
        // 然后再根据积分查询时间找到各个项目计划结束时间在范围内的stage，根据标准分和stage百分比计算出预估分
        if (queryStageInfoProjectIds.isEmpty()) {
            return result;
        }
        Map<String, Object> stageQueryParams = new HashMap<>();
        stageQueryParams.put("projectIds", queryStageInfoProjectIds);
        if (params.containsKey("evaluateScoreStartTime")) {
            stageQueryParams.put("evaluateScoreStartTime", params.get("evaluateScoreStartTime"));
        }
        if (params.containsKey("evaluateScoreEndTime")) {
            stageQueryParams.put("evaluateScoreEndTime", params.get("evaluateScoreEndTime"));
        }
        List<JSONObject> stageInfos = xcmgProjectReportDao.queryStageByPlanEndTime(stageQueryParams);
        if (stageInfos == null || stageInfos.isEmpty()) {
            return result;
        }
        // 查找各个项目在筛选条件下的阶段百分比之和
        Map<String, Double> projectId2StagePercentSum = new HashMap<>();
        for (JSONObject oneStageInfo : stageInfos) {
            if (!projectId2StagePercentSum.containsKey(oneStageInfo.getString("projectId"))) {
                projectId2StagePercentSum.put(oneStageInfo.getString("projectId"),
                    oneStageInfo.getDouble("stagePercent"));
            } else {
                Double existSum = projectId2StagePercentSum.get(oneStageInfo.getString("projectId"));
                projectId2StagePercentSum.put(oneStageInfo.getString("projectId"),
                    oneStageInfo.getDouble("stagePercent") + existSum);
            }
        }
        for (JSONObject oneProject : queryDataList) {
            if (projectId2StagePercentSum.containsKey(oneProject.getString("projectId"))) {
                String evaluateScore = String.valueOf(projectId2StagePercentSum.get(oneProject.getString("projectId"))
                    * Integer.parseInt(oneProject.getString("standardScore")) / 100);
                oneProject.put("evaluateScore", evaluateScore);
            }
        }
        return result;
    }

    public JsonPageResult<?> personEvaluateScoreList(HttpServletRequest request, HttpServletResponse response,
                                               boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        List<JSONObject> personEvaluateScoreListResult = new ArrayList<>();
        // 通过条件筛选项目
        Map<String, Object> params = new HashMap<>();
        getListParams(params, request, false);
        // 如果是挖掘机械研究院项目管理人员，则只查询挖掘机械研究院下的部门
        boolean isJSZXProjManagUsers =
                commonInfoManager.judgeUserIsPointRole(RdmConst.JSZXXMGLRY, ContextUtil.getCurrentUserId());
        if (isJSZXProjManagUsers) {
            Map<String, String> deptId2Name = commonInfoManager.queryDeptUnderJSZX();
            //2024-3-29新增技术中心项目管理员也可以看到工艺的项目
            boolean isGYProjManagUsers =
                    commonInfoManager.judgeUserIsPointRole(RdmConst.GYJSBXMGLRY, ContextUtil.getCurrentUserId());
            if (isGYProjManagUsers) {
                Map<String, Object> param = new HashMap<>();
                param.put("deptName", RdmConst.GYJSB_NAME);
                List<JSONObject> deptList = xcmgProjectReportDao.queryDeptByName(param);
                if (deptList != null && !deptList.isEmpty()) {
                    deptId2Name.put(deptList.get(0).getString("GROUP_ID_"), deptList.get(0).getString("NAME_"));
                }
            }
            params.put("deptIds", deptId2Name.keySet());
            //2024-4-29新增技术中心项目管理员也可以看到应用技术部的项目
            boolean isYYProjManagUsers =
                    commonInfoManager.judgeUserIsPointRole(RdmConst.YYJSBXMGLRY, ContextUtil.getCurrentUserId());
            if (isYYProjManagUsers) {
                Map<String, Object> param = new HashMap<>();
                param.put("deptName", RdmConst.YYJSB_NAME);
                List<JSONObject> deptList = xcmgProjectReportDao.queryDeptByName(param);
                if (deptList != null && !deptList.isEmpty()) {
                    deptId2Name.put(deptList.get(0).getString("GROUP_ID_"), deptList.get(0).getString("NAME_"));
                }
            }
            params.put("deptIds", deptId2Name.keySet());
        }
        List<JSONObject> projectEvaluateScoreList = xcmgProjectReportDao.queryEvaluateScoreList(params);

        if (projectEvaluateScoreList == null || projectEvaluateScoreList.isEmpty()) {
            return result;
        }
        Set<String> queryStageInfoProjectIds = new HashSet<>();
        for (JSONObject oneProject : projectEvaluateScoreList) {
            if (StringUtils.isNotBlank(oneProject.getString("pointStandardScore"))) {
                oneProject.put("standardScore", Integer.parseInt(oneProject.getString("pointStandardScore")));
            }
            if (StringUtils.isNotBlank(oneProject.getString("standardScore"))) {
                queryStageInfoProjectIds.add(oneProject.getString("projectId"));
            }
        }
        // 然后再根据积分查询时间找到各个项目计划结束时间在范围内的stage，根据标准分和stage百分比计算出预估分
        if (queryStageInfoProjectIds.isEmpty()) {
            return result;
        }
        Map<String, Object> stageQueryParams = new HashMap<>();
        stageQueryParams.put("projectIds", queryStageInfoProjectIds);
        if (params.containsKey("personEvaluateScoreStartTime")) {
            stageQueryParams.put("personEvaluateScoreStartTime", params.get("personEvaluateScoreStartTime"));
        }
        if (params.containsKey("personEvaluateScoreEndTime")) {
            stageQueryParams.put("personEvaluateScoreEndTime", params.get("personEvaluateScoreEndTime"));
        }
        List<JSONObject> stageInfos = xcmgProjectReportDao.queryUnfinishStageByPlanEndTime(stageQueryParams);
        if (stageInfos == null || stageInfos.isEmpty()) {
            return result;
        }
        // 查找各个项目在筛选条件下的阶段百分比之和
        Map<String, Double> projectId2StagePercentSum = new HashMap<>();
        for (JSONObject oneStageInfo : stageInfos) {
            if (!projectId2StagePercentSum.containsKey(oneStageInfo.getString("projectId"))) {
                projectId2StagePercentSum.put(oneStageInfo.getString("projectId"),
                        oneStageInfo.getDouble("stagePercent"));
            } else {
                Double existSum = projectId2StagePercentSum.get(oneStageInfo.getString("projectId"));
                projectId2StagePercentSum.put(oneStageInfo.getString("projectId"),
                        oneStageInfo.getDouble("stagePercent") + existSum);
            }
        }
        for (JSONObject oneProject : projectEvaluateScoreList) {
            if (projectId2StagePercentSum.containsKey(oneProject.getString("projectId"))) {
                String evaluateScore = String.valueOf(projectId2StagePercentSum.get(oneProject.getString("projectId"))
                        * Integer.parseInt(oneProject.getString("standardScore")) / 100);
                oneProject.put("evaluateScore", evaluateScore);
            }
        }
        //预估分信息
        Map<String, List<JSONObject>> idToProjectEvaluateScoreList =
                projectEvaluateScoreList.stream().collect(Collectors.groupingBy(j -> j.getString("projectId")));
        List<String> idsList = new ArrayList<>();
        for (JSONObject jsonObject : projectEvaluateScoreList) {
            idsList.add(jsonObject.getString("projectId"));
        }
        Map<String, Object> param = new HashMap<>();
        param.put("projectIds", idsList);
        //项目成员信息
        List<JSONObject> membersList = xcmgProjectReportDao.queryProjectMemberByIds(param);
        Map<String, List<JSONObject>> idToMembersList =
                membersList.stream().collect(Collectors.groupingBy(j -> j.getString("projectId")));
        //遍历得到每一个项目中项目成员信息map

        for (Map.Entry<String, List<JSONObject>> membersListEntry : idToMembersList.entrySet()) {
            String projectId = membersListEntry.getKey();
            List<JSONObject> memberList = membersListEntry.getValue();
            if(memberList==null||memberList.isEmpty()){
                continue;
            }
            //获得这个项目的基本信息
            JSONObject oneProjectInfo = idToProjectEvaluateScoreList.get(projectId).get(0);
            //该项目的预估分
            double keyProjectEvaluateScore = oneProjectInfo.getDoubleValue("evaluateScore");
            //该项目成员系数和
            double sumRatio = 0.00;
            //该项目的成员系数表
                for (JSONObject jsonObject : memberList) {
                    sumRatio+= StringUtils.isNotBlank(jsonObject.getString("roleRatio"))?jsonObject.getDoubleValue("roleRatio"):0.00;
                }
                if(sumRatio>0){
                    for (JSONObject jsonObject : memberList) {
                        double oneMemberRatio = jsonObject.getDoubleValue("roleRatio");
                        //该成员在这个项目里的预估分
                        double oneMemberEvaluateScore = (double)Math.round(oneMemberRatio/sumRatio*keyProjectEvaluateScore* 1000)/1000;
                        JSONObject oneMemberEvaluateScoreJson = new JSONObject();
                        oneMemberEvaluateScoreJson.put("personName",jsonObject.getString("FULLNAME_"));
                        oneMemberEvaluateScoreJson.put("deptName",jsonObject.getString("memberDeptName"));
                        oneMemberEvaluateScoreJson.put("projectId",oneProjectInfo.getString("projectId"));
                        oneMemberEvaluateScoreJson.put("projectName",oneProjectInfo.getString("projectName"));
                        oneMemberEvaluateScoreJson.put("categoryName",oneProjectInfo.getString("categoryName"));
                        oneMemberEvaluateScoreJson.put("levelName",oneProjectInfo.getString("levelName"));
                        oneMemberEvaluateScoreJson.put("number",oneProjectInfo.getString("number"));
                        oneMemberEvaluateScoreJson.put("STATUS",oneProjectInfo.getString("STATUS"));
                        oneMemberEvaluateScoreJson.put("personEvaluateScore",oneMemberEvaluateScore);
                        personEvaluateScoreListResult.add(oneMemberEvaluateScoreJson);
                    }
                }else {
                    continue;
                }

        }
        if (personEvaluateScoreListResult == null || personEvaluateScoreListResult.isEmpty()) {
            return result;
        }
        if (doPage) {
            result.setTotal(personEvaluateScoreListResult.size());
            personEvaluateScoreListResult = RdmCommonUtil.doPageByRequest(personEvaluateScoreListResult, request);
        }
        result.setData(personEvaluateScoreListResult);

        return result;
    }

    private void getListParams(Map<String, Object> params, HttpServletRequest request, boolean doPage) {
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)) {
            params.put("sortField", sortField);
            params.put("sortOrder", sortOrder);
        }
        if (StringUtils.isEmpty(sortField)) {
            params.put("sortField", "mainDepId,levelId");
        }
        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    // 数据库中存储的时间是UTC时间，因此需要将前台传递的北京时间转化
                    if ("evaluateScoreStartTime".equalsIgnoreCase(name) || "projectStartTime".equalsIgnoreCase(name)) {
                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), -8));
                    }
                    if ("evaluateScoreEndTime".equalsIgnoreCase(name) || "projectEndTime".equalsIgnoreCase(name)) {
                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), 16));
                    }
                    if ("mainDepId".equalsIgnoreCase(name)) {
                        params.put(name, Arrays.asList(value.split(",", -1)));
                    } else {
                        params.put(name, value);
                    }
                }
            }
        }
        // 分页
        if (doPage) {
            String pageIndex = RequestUtil.getString(request, "pageIndex", "0");
            String pageSize = RequestUtil.getString(request, "pageSize", String.valueOf(Page.DEFAULT_PAGE_SIZE));
            String startIndex = String.valueOf(Integer.parseInt(pageIndex) * Integer.parseInt(pageSize));
            params.put("startIndex", startIndex);
            params.put("pageSize", pageSize);
        }
    }

    public void exportEvaluateScore(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = evaluateScoreList(request, response, false);
        List<Map<String, Object>> finalSubProjectList = result.getData();
        // 按照部门统计项目的个数
        List<Integer> mainDepIdCount = new ArrayList<>();
        // 统计连续相同的部门ID的个数
        String currentMainDepId = "";
        int currentMainDepIdCount = 0;
        if (!finalSubProjectList.isEmpty()) {
            currentMainDepId = finalSubProjectList.get(0).get("mainDepId").toString();
            currentMainDepIdCount = 1;
        }
        for (int i = 0; i < finalSubProjectList.size(); i++) {
            Map<String, Object> oneMap = finalSubProjectList.get(i);
            oneMap.put("statusName", XcmgProjectUtil.convertProjectStatusCode2Name(oneMap.get("STATUS")));
            // 跳过第一个
            if (i != 0) {
                String thisMainDepId = oneMap.get("mainDepId").toString();
                if (thisMainDepId.equals(currentMainDepId)) {
                    currentMainDepIdCount++;
                } else {
                    mainDepIdCount.add(currentMainDepIdCount);
                    currentMainDepId = thisMainDepId;
                    currentMainDepIdCount = 1;
                }
            }
        }
        mainDepIdCount.add(currentMainDepIdCount);

        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "科技项目预估分统计表";
        String excelName = nowDate + title;
        String[] fieldNames = {"牵头部门", "项目类别", "项目级别", "标准分总分", "项目名称", "项目编号", "项目负责人", "项目状态", "标准分预估分"};
        String[] fieldCodes = {"mainDepName", "categoryName", "levelName", "standardScore", "projectName", "number",
            "respMan", "statusName", "evaluateScore"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(finalSubProjectList, fieldNames, fieldCodes, title);
        if (!mainDepIdCount.isEmpty()) {
            int sumRow = 2;
            // 合并发布年份列
            HSSFSheet sheet = wbObj.getSheetAt(0);
            for (int i = 0; i < mainDepIdCount.size(); i++) {
                int number = mainDepIdCount.get(i);
                int startRow = sumRow;
                int endRow = startRow + number - 1;
                // 行号、列号从0开始，开始和结束都包含
                if (endRow > startRow) {
                    sheet.addMergedRegionUnsafe(new CellRangeAddress(startRow, endRow, 0, 0));
                }
                sumRow = endRow + 1;
            }
        }

        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }

    public void exportPersonEvaluateScoreList(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = personEvaluateScoreList(request, response, false);
        List<Map<String, Object>> finalSubProjectList = result.getData();
        for (int i = 0; i < finalSubProjectList.size(); i++) {
            Map<String, Object> oneMap = finalSubProjectList.get(i);
            oneMap.put("statusName", XcmgProjectUtil.convertProjectStatusCode2Name(oneMap.get("STATUS")));
        }
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "科技项目个人预估分统计表";
        String excelName = nowDate + title;
        String[] fieldNames = {"员工姓名", "员工部门","项目名称", "项目类别","项目级别", "项目编号", "项目状态", "预估分"};
        String[] fieldCodes = {"personName", "deptName", "projectName", "categoryName", "levelName", "number",
                 "statusName", "personEvaluateScore"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(finalSubProjectList, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }

    /**
     * 查询项目延期大于5天的
     */
    public JsonPageResult<?> projectDelayList(HttpServletRequest request, HttpServletResponse response, boolean doPage,
        String scene) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        try {
            String reportType = RequestUtil.getString(request, "reportType", "");
            // allData不包含planEndTime
            if("homedesk".equalsIgnoreCase(reportType)){
                params.put("reportType","homedesk");
                params.put("userId",ContextUtil.getCurrentUserId());
            }
            List<Map<String, Object>> allData = xcmgProjectOtherDao.queryRunningProjectList(params);
            rdmZhglUtil.setTaskCreateTimeInfo(allData, false);
            queryProjectRiskInfo(allData);
            // 项目风险过滤
            Iterator iterator = allData.iterator();
            while (iterator.hasNext()) {
                Map<String, Object> oneProject = (Map<String, Object>)iterator.next();
                if (oneProject.get("hasRisk") != null) {
                    if (oneProject.get("hasRisk").toString().equalsIgnoreCase("2")
                        || oneProject.get("hasRisk").toString().equalsIgnoreCase("4"))
                            {
                        continue;
                    }
                    iterator.remove();
                }
            }
            Collections.sort(allData, new Comparator<Map<String, Object>>() {
                @Override
                public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                    long day1 = Long.parseLong(o1.get("delayDay").toString());
                    long day2 = Long.parseLong(o2.get("delayDay").toString());
                    if (day2 - day1 > 0) {
                        return 1;
                    }
                    return -1;
                }
            });
            // 总数设置并分页
            result.setTotal(allData.size());
            List<Map<String, Object>> finalSubProjectList = new ArrayList<Map<String, Object>>();
            if (doPage) {
                // 根据分页进行subList截取
                int pageIndex = RequestUtil.getInt(request, "pageIndex", 0);
                int pageSize = RequestUtil.getInt(request, "pageSize", Page.DEFAULT_PAGE_SIZE);
                int startSubListIndex = pageIndex * pageSize;
                int endSubListIndex = startSubListIndex + pageSize;
                if (startSubListIndex < allData.size()) {
                    finalSubProjectList = allData.subList(startSubListIndex,
                        endSubListIndex < allData.size() ? endSubListIndex : allData.size());
                }
            } else {
                finalSubProjectList = allData;
            }
            result.setData(finalSubProjectList);
        } catch (Exception e) {
            logger.error("Exception in projectDelayList", e);
            result.setSuccess(false);
            result.setMessage("系统异常！");
        }
        return result;
    }

    public void exportDelayExcel(HttpServletResponse response, HttpServletRequest request) {
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String excelName = nowDate + "项目延期列表";
        JsonPageResult pageResult = projectDelayList(request, response, false, "");
        List list = pageResult.getData();
        String title = "项目延期列表";
        String[] fieldNames = {"项目名称", "延期天数", "牵头部门", "项目负责人", "当前阶段"};
        String[] fieldCodes = {"projectName", "delayDay", "mainDepName", "respMan", "currentStageName"};
        HSSFWorkbook wbObj = ExcelUtils.exportStyleExcel(list, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }

    private void queryProjectRiskInfo(List<Map<String, Object>> projectList) throws ParseException {
        for (Map<String, Object> oneProject : projectList) {
            queryProjectDelay(oneProject);
        }
    }

    public void queryProjectDelay(Map<String, Object> oneProject) throws ParseException {
        // 当前的风险
        String status = "";
        if (oneProject.get("status") != null) {
            status = oneProject.get("status").toString();
        }
        int hasRisk = 0;
        Map<String, Object> params = new HashMap<>(16);
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        params.put("projectId", CommonFuns.nullToString(oneProject.get("projectId")));
        params.put("currentStageNo", CommonFuns.nullToString(oneProject.get("currentStageNo")));
        List<Map<String, Object>> progressInfos = xcmgProjectReportDao.projectProgressReport(params);
        if (progressInfos != null && !progressInfos.isEmpty()) {
            // 只有正常运行的，才需要判断延误风险
            Map<String, Object> current = progressInfos.get(progressInfos.size() - 1);
            Date planEndTime = (Date)current.get("planEndTime");
            long currentTime = DateFormatUtil.parseDate(XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd")).getTime();
            if (planEndTime != null) {
                String projectRiskPardonMillSecStr = WebAppUtil.getProperty("projectRiskPardonMillSec");
                if (StringUtils.isBlank(projectRiskPardonMillSecStr)) {
                    projectRiskPardonMillSecStr = "432000000";
                }
                long delayPardonTime = Long.parseLong(projectRiskPardonMillSecStr);
                // 超过5天
                if (currentTime - planEndTime.getTime() > delayPardonTime) {
                    hasRisk = 2;
                    long delayDay = (currentTime - planEndTime.getTime()) / (3600 * 1000 * 24);
                    oneProject.put("delayDay", delayDay);
                } else if (currentTime - planEndTime.getTime() <= delayPardonTime
                    && currentTime - planEndTime.getTime() > 0) {
                    hasRisk = 1;
                }
                // 没有设置计划时间且延误超过30天设置为有风险
            } else {
                long createTime = DateFormatUtil.parseDate(oneProject.get("taskCreateTime").toString()).getTime();
                if (currentTime - createTime > 2592000000L) {
                    hasRisk = 4;
                    long delayDay = (currentTime - createTime) / (3600 * 1000 * 24);
                    oneProject.put("delayDay", delayDay);
                }
            }
        }
        oneProject.put("hasRisk", hasRisk);
    }

}
