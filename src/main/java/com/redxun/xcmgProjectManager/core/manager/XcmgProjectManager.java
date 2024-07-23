
package com.redxun.xcmgProjectManager.core.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.entity.BpmNodeJump;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.bpm.core.manager.BpmNodeJumpManager;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.query.Page;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.drbfm.core.dao.DrbfmTotalDao;
import com.redxun.fzsj.core.dao.FzsjDao;
import com.redxun.org.api.model.IUser;
import com.redxun.org.api.service.UserService;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmCommon.core.manager.PdmApiManager;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmCommon.core.util.StringProcessUtil;
import com.redxun.rdmZhgl.core.dao.ProductConfigDao;
import com.redxun.rdmZhgl.core.dao.ProductDao;
import com.redxun.rdmZhgl.core.dao.RjzzDao;
import com.redxun.rdmZhgl.core.dao.ZlglmkDao;
import com.redxun.rdmZhgl.core.service.ProductService;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.standardManager.core.dao.StandardDao;
import com.redxun.standardManager.core.util.CommonFuns;
import com.redxun.standardManager.core.util.ResultUtil;
import com.redxun.sys.org.entity.OsUser;
import com.redxun.sys.org.manager.OsUserManager;
import com.redxun.xcmgProjectManager.core.controller.XcmgProjectController;
import com.redxun.xcmgProjectManager.core.dao.*;
import com.redxun.xcmgProjectManager.core.util.ConstantUtil;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import com.redxun.xcmgProjectManager.report.dao.XcmgProjectReportDao;
import com.redxun.xcmgProjectManager.report.manager.XcmgProjectReportManager;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <pre>
 * 描述：project_baseInfo 处理接口
 * 作者:x
 * 日期:2019-08-22 08:59:49
 * 版权：xx
 * </pre>
 */
@Service
public class XcmgProjectManager {
    private Logger logger = LogManager.getLogger(XcmgProjectController.class);
    @Resource
    private XcmgProjectDao xcmgProjectDao;
    @Resource
    private XcmgProjectExtensionDao xcmgProjectExtensionDao;
    @Resource
    private XcmgProjectPlanDao xcmgProjectPlanDao;
    @Resource
    private XcmgProjectMemberDao xcmgProjectMemberDao;
    @Resource
    private XcmgProjectMemOutDao xcmgProjectMemOutDao;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Resource
    private XcmgProjectFileUploadManager xcmgProjectFileUploadManager;
    @Resource
    private OsUserManager osUserManager;
    @Resource
    private XcmgProjectScoreDao xcmgProjectScoreDao;
    @Resource
    private BpmInstManager bpmInstManager;
    @Resource
    private XcmgProjectReportDao xcmgProjectReportDao;
    @Autowired
    private BpmNodeJumpManager bpmNodeJumpManager;
    @Autowired
    private UserService userService;
    @Resource
    private XcmgProjectAchievementDao xcmgProjectAchievementDao;
    @Resource
    private XcmgProjectReportManager xcmgProjectReportManager;
    @Resource
    private ProductService productService;
    @Autowired
    private XcmgProjectDeliveryApprovalDao xcmgProjectDeliveryApprovalDao;
    @Resource
    private XcmgProjectFileDao xcmgProjectFileDao;
    @Autowired
    private PdmApiManager pdmApiManager;
    @Resource
    private XcmgProjectChangeDao xcmgProjectChangeDao;
    @Autowired
    private CommonInfoManager commonInfoManager;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private StandardDao standardDao;
    @Autowired
    private FzsjDao fzsjDao;
    @Autowired
    private XcmgProjectConfigDao xcmgProjectConfigDao;
    @Resource
    private XcmgProjectFileUploadManager fileUploadManager;
    @Resource
    private XcmgProjectDeliveryProductDao xcmgProjectDeliveryProductDao;
    @Resource
    private RjzzDao rjzzDao;
    @Resource
    private ZlglmkDao zlglmkDao;
    @Autowired
    private CommonInfoDao commonInfoDao;
    @Resource
    private ProductConfigDao productConfigDao;
    @Resource
    private ProductDao productDao;

    @Resource
    private DrbfmTotalDao drbfmTotalDao;
    // ..@lwgkiller:获取所有的项目基本信息
    public JsonPageResult<?> allProjectListQuery(HttpServletRequest request, HttpServletResponse response,
                                                 boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        CommonFuns.getSearchParam(params, request, doPage);
        // --以上数据权限处理
        List<JSONObject> businessList = xcmgProjectDao.allProjectListQuery(params);
        int businessListCount = xcmgProjectDao.countAllProjectListQuery(params);
        result.setData(businessList);
        result.setTotal(businessListCount);
        return result;
    }

    // 查询项目编辑用到的一些下拉和对应规则信息
    public Map<String, Object> getProjectEditRule() {
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> params = new HashMap<>();
        // 来源
        List<Map<String, String>> mapResults = xcmgProjectOtherDao.queryProjectSources(params);
        if (mapResults != null && !mapResults.isEmpty()) {
            result.put("source", mapResults);
        } else {
            result.put("source", Collections.emptyList());
        }

        // 类别
        List<Map<String, String>> mapResults2 = xcmgProjectOtherDao.queryProjectCategory(params);
        if (mapResults2 != null && !mapResults2.isEmpty()) {
            // 去掉大类别
            Iterator<Map<String, String>> iterator = mapResults2.iterator();
            while (iterator.hasNext()) {
                Map<String, String> oneMap = iterator.next();
                if ("0".equals(oneMap.get("parentId"))) {
                    iterator.remove();
                }
            }
            result.put("category", mapResults2);
        } else {
            result.put("category", Collections.emptyList());
        }
        // 级别
        List<Map<String, String>> mapResults3 = xcmgProjectOtherDao.queryProjectLevel(params);
        if (mapResults3 != null && !mapResults3.isEmpty()) {
            result.put("level", mapResults3);
        } else {
            result.put("level", Collections.emptyList());
        }
        // 成员角色
        List<Map<String, String>> mapResultsRole = xcmgProjectOtherDao.queryProjectMemRole(params);
        if (mapResultsRole != null && !mapResultsRole.isEmpty()) {
            result.put("memberRole", mapResultsRole);
        } else {
            result.put("memberRole", Collections.emptyList());
        }
        // 项目成果分类
        List<Map<String, String>> mapResultsAchieveType = xcmgProjectOtherDao.queryProjectAchieveType(params);
        if (mapResultsAchieveType != null && !mapResultsAchieveType.isEmpty()) {
            result.put("achieveType", mapResultsAchieveType);
        } else {
            result.put("achieveType", Collections.emptyList());
        }
        // 结项分数与评价等级的对应关系
        List<Map<String, Object>> queryProjectKnot2Rating = xcmgProjectOtherDao.queryProjectKnot2Rating(params);
        if (queryProjectKnot2Rating != null && !queryProjectKnot2Rating.isEmpty()) {
            result.put("knot2Rating", queryProjectKnot2Rating);
        } else {
            result.put("knot2Rating", Collections.emptyList());
        }

        // 项目角色、级别与职级的对应关系
        List<Map<String, String>> projectRoleRankRequire = xcmgProjectOtherDao.queryProjectRoleRankRequire(params);
        if (projectRoleRankRequire != null && !projectRoleRankRequire.isEmpty()) {
            result.put("roleRankRequire", projectRoleRankRequire);
        } else {
            result.put("roleRankRequire", Collections.emptyList());
        }

        // 项目成员角色与角色系数的对应关系
        List<Map<String, Object>> roleRatioList = xcmgProjectOtherDao.getRoleRatioList(params);
        if (roleRatioList != null && !roleRatioList.isEmpty()) {
            result.put("roleRatioList", roleRatioList);
        } else {
            result.put("roleRatioList", Collections.emptyList());
        }
        result.put("currentUserId", ContextUtil.getCurrentUserId());
        return result;
    }

    /**
     * 查询特定来源、类别、级别下的交付物类型
     *
     * @param request
     * @return
     */
    public List<JSONObject> queryProjectDeliverys(HttpServletRequest request) {
        String projectSourceId = RequestUtil.getString(request, "projectSourceId", "");
        String projectLevelId = RequestUtil.getString(request, "projectLevelId", "");
        String projectCategoryId = RequestUtil.getString(request, "projectCategoryId", "");
        Map<String, Object> params = new HashMap<>();
        params.put("projectSourceId", projectSourceId);
        params.put("projectLevelId", projectLevelId);
        params.put("projectCategoryId", projectCategoryId);
        return xcmgProjectOtherDao.queryProjectDeliverys(params);
    }

    // 检查必传交付物是否分配完成
    public JsonResult checkDeliveryAssign(String requestBody) {
        JsonResult result = new JsonResult(true);
        JSONObject requestObj = JSONObject.parseObject(requestBody);
        Map<String, Object> params = new HashMap<>();
        params.put("projectSourceId", requestObj.getString("projectSourceId"));
        params.put("projectLevelId", requestObj.getString("projectLevelId"));
        params.put("projectCategoryId", requestObj.getString("projectCategoryId"));
        String projectId = requestObj.getString("projectId");
        List<JSONObject> deliverys = xcmgProjectOtherDao.queryProjectDeliverys(params);
        if (deliverys == null || deliverys.isEmpty()) {
            return result;
        }
        Map<String, String> deliveryId2Name = new HashMap<>();
        for (JSONObject oneData : deliverys) {
            deliveryId2Name.put(oneData.getString("deliveryId"), oneData.getString("deliveryName"));
        }
        Set<String> missKeys = new HashSet<>(deliveryId2Name.keySet());
        // 如果是产品研发（整机）类，需要从后台取出交付物，其他从页面传递
        JSONObject xcmgProject = getXcmgProject(projectId);
        List<String> assignDeliveryIdArr = new ArrayList<>();
        if (ConstantUtil.PRODUCT_CATEGORY.equals(xcmgProject.getString("categoryId"))) {
            // 查询已经分配的交付物
            String deliverIds = xcmgProjectDeliveryProductDao.getRespDeliveryIds(projectId);
            if (StringUtil.isNotEmpty(deliverIds)) {
                String[] deliveryArray = deliverIds.split(",");
                assignDeliveryIdArr = Arrays.asList(deliveryArray);
            }
        } else {
            assignDeliveryIdArr = requestObj.getObject("assignDeliveryIds", List.class);
        }
        if (assignDeliveryIdArr != null && !assignDeliveryIdArr.isEmpty()) {
            Set<String> assignKeys = new HashSet<>(assignDeliveryIdArr);
            missKeys.removeAll(assignKeys);
        }
        if (!missKeys.isEmpty()) {
            result.setSuccess(false);
            StringBuilder stringBuilder = new StringBuilder();
            for (String key : missKeys) {
                stringBuilder.append(deliveryId2Name.get(key)).append("，");
            }
            result.setMessage(
                    "下列必传交付物尚未分配到成员，请点击“负责交付物”分配：<br/>" + stringBuilder.substring(0, stringBuilder.length() - 1));
        }
        return result;
    }

    // 产品研发整机类的项目，需要在立项阶段就选择好管理部门的人
    public JsonResult checkProductGlUserAssign(String requestBody) {
        JsonResult result = new JsonResult(true);
        JSONObject requestObj = JSONObject.parseObject(requestBody);
        String categoryId = requestObj.getString("categoryId");
        JSONArray memberArr = requestObj.getJSONArray("membersData");
        if (memberArr == null || memberArr.isEmpty()) {
            return result;
        }
        if (!ConstantUtil.PRODUCT_CATEGORY.equals(categoryId)) {
            return result;
        }
        StringBuilder memberDeptNames = new StringBuilder();
        for (int index = 0; index < memberArr.size(); index++) {
            String deptId = memberArr.getJSONObject(index).getString("memberDeptId");
            Boolean isTech = commonInfoManager.judgeIsTechDept(deptId);
            if (!isTech && StringUtils.isBlank(memberArr.getJSONObject(index).getString("userId"))) {
                memberDeptNames.append(memberArr.getJSONObject(index).getString("memberDeptName") + "，");
            }
        }
        if (memberDeptNames.length() > 0) {
            result.setSuccess(false);
            result.setMessage(
                    "不属于挖掘机械研究院下的部门（" + memberDeptNames.substring(0, memberDeptNames.length() - 1) + "），需要选定“成员姓名”！");
        }

        return result;
    }

    // 查询列表
    public JsonPageResult<?> getProjectList(HttpServletRequest request, HttpServletResponse response,
                                            Map<String, Object> params) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            // 传入条件(不包括分页)
            getProjectListParams(params, request);
            List<Map<String, Object>> projectList = new ArrayList<>();
            // 增加不同角色和部门的人看到的数据不一样的过滤
            List<Map<String, Object>> finalAllProjectList = null;
            if (ConstantUtil.PERSON_PROJECT_LIST.equals(request.getAttribute(ConstantUtil.DESK_HOME))) {
                // 如果是办公桌面的个人项目进展情况，则只需查询个人参与的项目列表
                Map<String, Object> param = new HashMap<>(16);
                param.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
                param.put("userId", ContextUtil.getCurrentUserId());
                projectList = xcmgProjectReportDao.queryPersonProjectList(param);
                finalAllProjectList = filterProjectListByPerson(projectList);
            } else {
                // 如果是挖掘机械研究院项目管理人员，则只查询挖掘机械研究院下的部门
                boolean isJSZXProjManagUsers =
                        commonInfoManager.judgeUserIsPointRole(RdmConst.JSZXXMGLRY, ContextUtil.getCurrentUserId());
                boolean isJSZXProjectAllQuery = false;
                if (!isJSZXProjManagUsers) {
                    isJSZXProjectAllQuery = commonInfoManager.judgeUserIsPointRole(RdmConst.JSZX_ALL_PROJECT_QUERY,
                            ContextUtil.getCurrentUserId());
                }
                if (isJSZXProjManagUsers || isJSZXProjectAllQuery) {
                    Map<String, String> deptId2Name = commonInfoManager.queryDeptUnderJSZX();
                    // 拼接挖掘机械研究院本身
                    List<JSONObject> list = commonInfoDao.getDeptInfoByDeptName(RdmConst.JSZX_NAME);
                    if (list != null && !list.isEmpty()) {
                        deptId2Name.put(list.get(0).getString("GROUP_ID_"), list.get(0).getString("NAME_"));
                    }
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
                } else {
                    boolean isGYFGLD =
                            commonInfoManager.judgeUserIsPointRole(RdmConst.GY_FGLD, ContextUtil.getCurrentUserId());
                    if (isGYFGLD) {
                        Map<String, Object> param = new HashMap<>();
                        param.put("deptName", RdmConst.GYJSB_NAME);
                        List<JSONObject> deptList = xcmgProjectReportDao.queryDeptByName(param);
                        if (deptList != null && !deptList.isEmpty()) {
                            params.put("deptIds", Arrays.asList(deptList.get(0).getString("GROUP_ID_")));
                        }
                    }
                }

                projectList = xcmgProjectOtherDao.queryProjectList(params);
                finalAllProjectList = filterProjectListByDepRole(projectList);
            }
            List<String> projectIds = new ArrayList<>();
            if (finalAllProjectList != null && !finalAllProjectList.isEmpty()) {
                for (Map<String, Object> oneProject : finalAllProjectList) {
                    projectIds.add(oneProject.get("projectId").toString());
                }
            }
            Set<String> depIds = new HashSet<>();
            Set<String> findInstIdByBusKeys = new HashSet<>();
            // 根据projectId查找负责人,有过滤条件的话会过滤
            if (!projectIds.isEmpty()) {
                Map<String, Object> queryRespManParams = new HashMap<>();
                queryRespManParams.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
                queryRespManParams.put("projectIds", projectIds);
                List<Map<String, Object>> respManInfo =
                        xcmgProjectOtherDao.queryRespManByProjectIds(queryRespManParams);
                Map<String, JSONObject> projectId2RespMan = new HashMap<>();
                // 负责人、负责人ID
                for (Map<String, Object> oneMap : respManInfo) {
                    JSONObject userObj = new JSONObject();
                    userObj.put("userId", oneMap.get("USER_ID_").toString());
                    userObj.put("userName", oneMap.get("FULLNAME_").toString());
                    projectId2RespMan.put(oneMap.get("projectId").toString(), userObj);
                }
                // 修改列表使用迭代器
                Iterator iterator = finalAllProjectList.iterator();
                while (iterator.hasNext()) {
                    Map<String, Object> oneProject = (Map<String, Object>) iterator.next();
                    String respManName = "";
                    String respManId = "";
                    if (projectId2RespMan.containsKey(oneProject.get("projectId").toString())) {
                        respManName =
                                projectId2RespMan.get(oneProject.get("projectId").toString()).getString("userName");
                        respManId = projectId2RespMan.get(oneProject.get("projectId").toString()).getString("userId");
                    }
                    // 进行项目负责人过滤
                    if (params.containsKey("respMan")) {
                        if (!respManName.contains(params.get("respMan").toString())) {
                            iterator.remove();
                            continue;
                        }
                    }
                    // 进行项目当前审批任务过滤
                    if (params.get("taskName") != null && StringUtils.isNotBlank(params.get("taskName").toString())) {
                        String projectTaskName =
                                oneProject.get("taskName") == null ? "" : oneProject.get("taskName").toString();
                        if (!projectTaskName.contains(params.get("taskName").toString())) {
                            iterator.remove();
                            continue;
                        }
                    }
                    oneProject.put("respMan", respManName);
                    oneProject.put("respManId", respManId);
                    try {
                        if (oneProject.get("pointStandardScore") != null
                                && StringUtils.isNotBlank(oneProject.get("pointStandardScore").toString())) {
                            oneProject.put("standardScore",
                                    Integer.parseInt(oneProject.get("pointStandardScore").toString()));
                        }
                        // instId为空的数据
                        if (oneProject.get("INST_ID_") == null
                                || StringUtils.isBlank(oneProject.get("INST_ID_").toString())) {
                            findInstIdByBusKeys.add(oneProject.get("projectId").toString());
                        }

                        if (StringUtils.isNotBlank((String) oneProject.get("mainDepId"))) {
                            depIds.add((String) oneProject.get("mainDepId"));
                        }
                        if (StringUtils.isNotBlank((String) oneProject.get("otherDepId"))) {
                            String[] otherDepIdArr = ((String) oneProject.get("otherDepId")).split(",", -1);
                            oneProject.put("otherDepIdArr", otherDepIdArr);
                            depIds.addAll(Arrays.asList(otherDepIdArr));
                        }
                        if (oneProject.get("buildTime") != null) {
                            oneProject.put("buildTime", DateFormatUtil
                                    .format(DateUtil.parseDate(oneProject.get("buildTime").toString()), "yyyy-MM-dd"));
                        }
                        if (oneProject.get("knotTime") != null && oneProject.get("knotTime") instanceof Date) {
                            oneProject.put("knotTime",
                                    DateUtil.formatDate((Date) oneProject.get("knotTime"), "yyyy-MM-dd"));
                        }
                        if (oneProject.get("CREATE_TIME_") != null) {
                            Object createTimeObj = oneProject.get("CREATE_TIME_");
                            if (createTimeObj instanceof Date) {
                                oneProject.put("CREATE_TIME_", DateUtil.formatDate((Date) createTimeObj, "yyyy-MM-dd"));
                            } else {
                                oneProject.put("CREATE_TIME_", createTimeObj.toString());
                            }
                        }
                        if (oneProject.get("UPDATE_TIME_") != null) {
                            Object updateTimeObj = oneProject.get("UPDATE_TIME_");
                            if (updateTimeObj instanceof Date) {
                                oneProject.put("UPDATE_TIME_", DateUtil.formatDate((Date) updateTimeObj, "yyyy-MM-dd"));
                            } else {
                                oneProject.put("UPDATE_TIME_", updateTimeObj.toString());
                            }
                        }
                    } catch (Exception e) {
                        logger.error(oneProject.get("projectName"), e);
                    }
                }
            }
            // 赋值部门的名称
            if (!depIds.isEmpty()) {
                Map<String, String> depId2Names = getDepNamesByIds(depIds);
                for (Map<String, Object> oneProject : finalAllProjectList) {
                    if (StringUtils.isNotBlank((String) oneProject.get("mainDepId"))) {
                        oneProject.put("mainDepName",
                                depId2Names.getOrDefault((String) oneProject.get("mainDepId"), ""));
                    }
                    if (oneProject.get("otherDepIdArr") != null) {
                        String[] otherDepIdArr = (String[]) oneProject.get("otherDepIdArr");
                        String otherDepNames = "";
                        for (String otherDepId : otherDepIdArr) {
                            otherDepNames += depId2Names.get(otherDepId) + ",";
                        }
                        oneProject.put("otherDepName", otherDepNames.substring(0, otherDepNames.length() - 1));
                    }
                }
            }

            // 更新instId
            if (!findInstIdByBusKeys.isEmpty()) {
                Map<String, String> buskey2InstId = getInstIdsByBusKeys(findInstIdByBusKeys);
                for (Map<String, Object> oneProject : finalAllProjectList) {
                    if (oneProject.get("INST_ID_") == null
                            || StringUtils.isBlank(oneProject.get("INST_ID_").toString())) {
                        String instId = buskey2InstId.get(oneProject.get("projectId").toString());
                        if (StringUtils.isNotBlank(instId)) {
                            oneProject.put("INST_ID_", instId);
                            Map<String, Object> updateParam = new HashMap<>();
                            updateParam.put("instId", instId);
                            updateParam.put("projectId", oneProject.get("projectId").toString());
                            xcmgProjectOtherDao.updateProjectInstId(updateParam);
                        }
                    }
                }
            }
            // 添加项目计划开始和结束日期
            List<JSONObject> timeJsonList = xcmgProjectOtherDao.queryTimeByProjectId();
            Stream<JSONObject> stream = timeJsonList.stream();
            // project 对应的不同阶段的开始结束时间
            Map<String, List<JSONObject>> idToTimeList =
                    stream.collect(Collectors.groupingBy(j -> j.getString("projectId")));
            for (Map<String, Object> oneProject : finalAllProjectList) {
                String projectId = oneProject.get("projectId").toString();
                List<JSONObject> oneTimeList = idToTimeList.get(projectId);
                if (oneTimeList != null && !oneTimeList.isEmpty()) {
                    String firstPlanStartTime = oneTimeList.get(0).getString("planStartTime");
                    String lastPlanEndTime = oneTimeList.get(oneTimeList.size() - 1).getString("planEndTime");
                    oneProject.put("firstPlanStartTime", firstPlanStartTime);
                    oneProject.put("lastPlanEndTime", lastPlanEndTime);
                }
            }
            // 将产学研国重类的单独抽取出来并排序后置顶
            List<Map<String, Object>> cxyGzProjectList = toGetCxyGz(finalAllProjectList);
            /*// 将其他类别的项目，按照部门,负责人和级别排序
            Collections.sort(finalAllProjectList, new Comparator<Map<String, Object>>() {
                @Override
                public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                    return 1;
                }
            });*/
            /*            if (!cxyGzProjectList.isEmpty()) {
                // 将产学研国重的项目，按照部门,负责人和级别排序
                Collections.sort(cxyGzProjectList, new Comparator<Map<String, Object>>() {
                    @Override
                    public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                        return 1;
                    }
                });
            }*/

            // 结果合并
            cxyGzProjectList.addAll(finalAllProjectList);
            result.setData(cxyGzProjectList);
        } catch (Exception e) {
            logger.error("Exception in getProjectList", e);
            result.setSuccess(false);
            result.setMessage("Internal error");
        }
        return result;
    }

    // 将产学研国重项目置顶
    private List<Map<String, Object>> putCxyGz2Top(List<Map<String, Object>> projectList) {
        if (projectList == null || projectList.isEmpty()) {
            return projectList;
        }
        List<Map<String, Object>> result = new ArrayList<>();
        Iterator<Map<String, Object>> iterable = projectList.iterator();
        while (iterable.hasNext()) {
            Map<String, Object> oneData = iterable.next();
            if (oneData.get("categoryName") != null
                    && oneData.get("categoryName").toString().contains(RdmConst.CXY_GZ)) {
                result.add(oneData);
                iterable.remove();
            }
        }
        result.addAll(projectList);
        return result;
    }

    // 将产学研国重项目置顶
    private List<Map<String, Object>> toGetCxyGz(List<Map<String, Object>> projectList) {
        if (projectList == null || projectList.isEmpty()) {
            return projectList;
        }
        List<Map<String, Object>> result = new ArrayList<>();
        Iterator<Map<String, Object>> iterable = projectList.iterator();
        while (iterable.hasNext()) {
            Map<String, Object> oneData = iterable.next();
            if (oneData.get("categoryName") != null
                    && oneData.get("categoryName").toString().contains(RdmConst.CXY_GZ)) {
                result.add(oneData);
                iterable.remove();
            }
        }
        return result;
    }

    // 根据登录人部门、角色对列表进行过滤
    private List<Map<String, Object>> filterProjectListByDepRole(List<Map<String, Object>> projectList) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        if (projectList == null || projectList.isEmpty()) {
            return result;
        }
        // 向业务数据中写入任务相关的信息
        rdmZhglUtil.setTaskInfo2DataMap(projectList, ContextUtil.getCurrentUserId());
        // 管理员查看所有的包括草稿数据
        if ("admin".equals(ContextUtil.getCurrentUser().getUserNo())) {
            return projectList;
        }
        // 分管领导的查看权限等同于项目管理人员
        boolean showAll = false;
        Map<String, Object> params = new HashMap<>();
        params.put("userId", ContextUtil.getCurrentUser().getUserId());
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params);
        for (Map<String, Object> oneRole : currentUserRoles) {
            if (oneRole.get("REL_TYPE_KEY_").toString().equalsIgnoreCase("GROUP-USER-LEADER")
                    || oneRole.get("REL_TYPE_KEY_").toString().equalsIgnoreCase("GROUP-USER-BELONG")) {
                if (RdmConst.AllDATA_QUERY_NAME.equalsIgnoreCase(oneRole.get("NAME_").toString())
                        || RdmConst.JSZXXMGLRY.equalsIgnoreCase(oneRole.get("NAME_").toString())
                        || RdmConst.JSZX_ALL_PROJECT_QUERY.equalsIgnoreCase(oneRole.get("NAME_").toString())
                        || RdmConst.GY_FGLD.equalsIgnoreCase(oneRole.get("NAME_").toString())
                        || RdmConst.ZLGCS.equalsIgnoreCase(oneRole.get("NAME_").toString())) {
                    showAll = true;
                    break;
                }
            }
        }

        // 确定当前登录人是否是部门负责人
        JSONObject currentUserDepInfo = isCurrentUserDepRespman();
        if (!"success".equals(currentUserDepInfo.getString("result"))) {
            return result;
        }
        boolean isDepRespMan = currentUserDepInfo.getBoolean("isDepRespMan");
        String currentUserMainDepId = currentUserDepInfo.getString("currentUserMainDepId");
        String currentUserId = ContextUtil.getCurrentUserId();
        String currentUserMainDepName = currentUserDepInfo.getString("currentUserMainDepName");
        boolean isDepProjectManager =
                XcmgProjectUtil.judgeIsDepProjectManager(currentUserMainDepName, currentUserRoles);
        // 过滤
        Set<String> needQueryMemProIds = new HashSet<>();
        Map<String, Map<String, Object>> projectId2Detail = new HashMap<>();
        for (Map<String, Object> oneProject : projectList) {
            projectId2Detail.put(oneProject.get("projectId").toString(), oneProject);
            // 自己是当前流程处理人
            if (oneProject.get("myTaskId") != null && StringUtils.isNotBlank(oneProject.get("myTaskId").toString())) {
                oneProject.put("processTask", true);
                result.add(oneProject);
            } else if (showAll) {
                // 对于非草稿的数据或者草稿但是创建人CREATE_BY_是自己的
                if (oneProject.get("status") != null && !"DRAFTED".equals(oneProject.get("status").toString())) {
                    result.add(oneProject);
                } else {
                    if (oneProject.get("CREATE_BY_").toString().equals(currentUserId)) {
                        result.add(oneProject);
                    }
                }
            } else if (isDepRespMan || isDepProjectManager) {
                // 部门负责人对于非草稿的且项目主部门是当前部门，或者非草稿的且项目成员中包含自己的，或者草稿但是创建人CREATE_BY_是自己的
                if (oneProject.get("status") != null && !"DRAFTED".equals(oneProject.get("status").toString())) {
                    if (oneProject.get("mainDepId").toString().equals(currentUserMainDepId)) {
                        result.add(oneProject);
                    } else {
                        needQueryMemProIds.add(oneProject.get("projectId").toString());
                    }
                } else {
                    if (oneProject.get("CREATE_BY_").toString().equals(currentUserId)) {
                        result.add(oneProject);
                    }
                }

            } else {
                // 其他人对于非草稿的且项目成员中包含自己的，或者草稿但是创建人CREATE_BY_是自己的
                if (oneProject.get("status") != null && !"DRAFTED".equals(oneProject.get("status").toString())) {
                    needQueryMemProIds.add(oneProject.get("projectId").toString());
                } else {
                    if (oneProject.get("CREATE_BY_").toString().equals(currentUserId)) {
                        result.add(oneProject);
                    }
                }
            }
        }
        if (!needQueryMemProIds.isEmpty()) {
            // 分批查询，防止数据量过大
            List<String> subProjectIds = new ArrayList<>();
            for (String projectId : needQueryMemProIds) {
                subProjectIds.add(projectId);
                if (subProjectIds.size() >= 50) {
                    Map<String, Object> queryUserIdOfProject = new HashMap<>();
                    queryUserIdOfProject.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
                    queryUserIdOfProject.put("USER_ID_", currentUserId);
                    queryUserIdOfProject.put("PROJECT_ID_", new ArrayList<String>(subProjectIds));
                    List<Map<String, Object>> userProjectIdList =
                            xcmgProjectOtherDao.getUserIdOfProject(queryUserIdOfProject);
                    if (userProjectIdList != null && !userProjectIdList.isEmpty()) {
                        for (Map<String, Object> oneMap_user : userProjectIdList) {
                            result.add(projectId2Detail.get(oneMap_user.get("projectId")));
                        }
                    }
                    subProjectIds.clear();
                }
            }
            if (!subProjectIds.isEmpty()) {
                Map<String, Object> queryUserIdOfProject = new HashMap<>();
                queryUserIdOfProject.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
                queryUserIdOfProject.put("USER_ID_", currentUserId);
                queryUserIdOfProject.put("PROJECT_ID_", new ArrayList<String>(subProjectIds));
                List<Map<String, Object>> userProjectIdList =
                        xcmgProjectOtherDao.getUserIdOfProject(queryUserIdOfProject);
                if (userProjectIdList != null && !userProjectIdList.isEmpty()) {
                    for (Map<String, Object> oneMap_user : userProjectIdList) {
                        result.add(projectId2Detail.get(oneMap_user.get("projectId")));
                    }
                }
                subProjectIds.clear();
            }
        }

        return result;
    }

    private List<Map<String, Object>> filterProjectListByPerson(List<Map<String, Object>> projectList) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        if (projectList == null || projectList.isEmpty()) {
            return result;
        }
        // 向业务数据中写入任务相关的信息
        rdmZhglUtil.setTaskInfo2DataMap(projectList, ContextUtil.getCurrentUserId());
        Map<String, Object> params = new HashMap<>(16);
        params.put("userId", ContextUtil.getCurrentUser().getUserId());
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());

        String currentUserId = ContextUtil.getCurrentUserId();
        // 过滤
        Set<String> needQueryMemProIds = new HashSet<>();
        Map<String, Map<String, Object>> projectId2Detail = new HashMap<>(16);
        for (Map<String, Object> oneProject : projectList) {
            projectId2Detail.put(oneProject.get("projectId").toString(), oneProject);
            // 自己是当前流程处理人
            if (oneProject.get("myTaskId") != null && StringUtils.isNotBlank(oneProject.get("myTaskId").toString())) {
                oneProject.put("processTask", true);
                result.add(oneProject);
            } else {
                // 其他人对于非草稿的且项目成员中包含自己的，或者草稿但是创建人CREATE_BY_是自己的
                if (oneProject.get("status") != null && !"DRAFTED".equals(oneProject.get("status").toString())) {
                    needQueryMemProIds.add(oneProject.get("projectId").toString());
                } else {
                    if (oneProject.get("CREATE_BY_").toString().equals(currentUserId)) {
                        result.add(oneProject);
                    }
                }
            }
        }
        if (!needQueryMemProIds.isEmpty()) {
            // 分批查询，防止数据量过大
            List<String> subProjectIds = new ArrayList<>();
            for (String projectId : needQueryMemProIds) {
                subProjectIds.add(projectId);
                if (subProjectIds.size() >= 50) {
                    Map<String, Object> queryUserIdOfProject = new HashMap<>();
                    queryUserIdOfProject.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
                    queryUserIdOfProject.put("PROJECT_ID_", new ArrayList<String>(subProjectIds));
                    queryUserIdOfProject.put("USER_ID_", currentUserId);
                    List<Map<String, Object>> userProjectIdList =
                            xcmgProjectOtherDao.getUserIdOfProject(queryUserIdOfProject);
                    if (userProjectIdList != null && !userProjectIdList.isEmpty()) {
                        for (Map<String, Object> oneMap_user : userProjectIdList) {
                            result.add(projectId2Detail.get(oneMap_user.get("projectId")));
                        }
                    }
                    subProjectIds.clear();
                }
            }
            if (!subProjectIds.isEmpty()) {
                Map<String, Object> queryUserIdOfProject = new HashMap<>();
                queryUserIdOfProject.put("PROJECT_ID_", new ArrayList<String>(subProjectIds));
                queryUserIdOfProject.put("USER_ID_", currentUserId);
                queryUserIdOfProject.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
                List<Map<String, Object>> userProjectIdList =
                        xcmgProjectOtherDao.getUserIdOfProject(queryUserIdOfProject);
                if (userProjectIdList != null && !userProjectIdList.isEmpty()) {
                    for (Map<String, Object> oneMap_user : userProjectIdList) {
                        result.add(projectId2Detail.get(oneMap_user.get("projectId")));
                    }
                }
                subProjectIds.clear();
            }
        }

        return result;
    }

    // 判断当前登录人是否是部门负责人，并返回所在部门
    public JSONObject isCurrentUserDepRespman() {
        JSONObject result = new JSONObject();
        // 查询当前登录人的主部门id和名称
        String currentUserId = ContextUtil.getCurrentUserId();
        Map<String, Object> queryUserParams = new HashMap<>();
        queryUserParams.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        queryUserParams.put("USER_ID_", currentUserId);
        List<Map<String, Object>> userInfos = xcmgProjectOtherDao.getUserInfoById(queryUserParams);
        if (userInfos == null || userInfos.isEmpty()) {
            logger.error("Can't find userInfo by id {}", currentUserId);
            result.put("result", "failed");
            result.put("message", "Can't find userInfo by id " + currentUserId);
            return result;
        }
        String currentUserMainDepId = "";
        String currentUserMainDepName = "";
        for (Map<String, Object> oneMap : userInfos) {
            if (StringUtils.isNotBlank(oneMap.get("dimKey").toString()) && oneMap.get("dimKey").equals("_ADMIN")) {
                currentUserMainDepId = oneMap.get("groupId").toString();
                currentUserMainDepName = oneMap.get("groupName").toString();
                break;
            }
        }
        if (StringUtils.isBlank(currentUserMainDepId)) {
            logger.error("Can't find userMainDep by id {}", currentUserId);
            result.put("result", "failed");
            result.put("message", "Can't find userMainDep by id " + currentUserId);
            return result;
        }
        // 根据userId、currentUserMainDepId确定是否用户是主部门的负责人
        boolean isDepRespMan = false;
        Map<String, Object> queryDepRespMan = new HashMap<>();
        queryDepRespMan.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        queryDepRespMan.put("USER_ID_", currentUserId);
        queryDepRespMan.put("GROUP_ID_", currentUserMainDepId);
        List<Map<String, String>> depRespMan = xcmgProjectOtherDao.getDepRespManById(queryDepRespMan);
        if (depRespMan != null && !depRespMan.isEmpty()) {
            isDepRespMan = true;
        }
        result.put("result", "success");
        result.put("isDepRespMan", isDepRespMan);
        result.put("currentUserMainDepId", currentUserMainDepId);
        result.put("currentUserMainDepName", currentUserMainDepName);
        return result;
    }

    // 对于一个任务有多个候选执行人的场景，act_ru_task中没有执行人，需要从act_ru_identitylink查询。因此统一在此处查询
    public void setTaskCurrentUser(List<Map<String, Object>> objList) {
        Map<String, Map<String, Object>> taskId2Pro = new HashMap<>();
        for (Map<String, Object> onePro : objList) {
            if (onePro.get("taskId") != null && StringUtils.isNotBlank(onePro.get("taskId").toString())) {
                taskId2Pro.put(onePro.get("taskId").toString(), onePro);
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

    // 对于一个任务有多个候选执行人的场景，act_ru_task中没有执行人，需要从act_ru_identitylink查询。因此统一在此处查询
    public void setTaskCurrentUserJSON(List<JSONObject> objList) {
        Map<String, JSONObject> taskId2Pro = new HashMap<>();
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
            JSONObject needPutPro = taskId2Pro.get(taskId);
            if (needPutPro.get("currentProcessUser") != null
                    && StringUtils.isNotBlank(needPutPro.getString("currentProcessUser"))) {
                executorName += "," + needPutPro.getString("currentProcessUser");
                executorId += "," + needPutPro.getString("currentProcessUserId");
            }
            needPutPro.put("currentProcessUser", executorName);
            needPutPro.put("currentProcessUserId", executorId);
        }
    }

    // 将过滤条件、排序等信息传入，分页不在此处进行
    private void getProjectListParams(Map<String, Object> params, HttpServletRequest request) {
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)) {
            params.put("sortField", sortField);
            params.put("sortOrder", sortOrder);
        }
        if (StringUtils.isEmpty(sortField)) {
            params.put("sortField", "project_baseinfo.CREATE_TIME_");
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
                    if ("buildTimeStart".equalsIgnoreCase(name) || "knotTimeStart".equalsIgnoreCase(name)
                            || "projectStartTime".equalsIgnoreCase(name) || "planEndTimeStart".equalsIgnoreCase(name)) {
                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), -8));
                    }
                    if ("buildTimeEnd".equalsIgnoreCase(name) || "knotTimeEnd".equalsIgnoreCase(name)
                            || "projectEndTime".equalsIgnoreCase(name) || "queryEndTime".equalsIgnoreCase(name)
                            || "planEndTimeEnd".equalsIgnoreCase(name)) {
                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), 16));
                    }

                    if ("mainDepId".equalsIgnoreCase(name) || "status".equalsIgnoreCase(name)) {
                        String[] mainDepIdArr = value.split(",", -1);
                        params.put(name, Arrays.asList(mainDepIdArr));
                    } else {
                        params.put(name, value);
                    }
                }
            }
        }
    }

    // 通过部门id查询部门名称，最后返回id-name
    private Map<String, String> getDepNamesByIds(Set<String> depIds) {
        Map<String, String> result = new HashMap<>();
        if (depIds != null && !depIds.isEmpty()) {
            Map<String, Object> params = new HashMap<>();
            params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            params.put("depIds", new ArrayList<String>(depIds));
            List<Map<String, Object>> depIdNames = xcmgProjectOtherDao.queryDepNameByIds(params);
            if (depIdNames != null && !depIdNames.isEmpty()) {
                for (Map<String, Object> oneMap : depIdNames) {
                    result.put(oneMap.get("GROUP_ID_").toString(), oneMap.get("NAME_").toString());
                }
            }
        }
        return result;
    }

    // 通过busKeys查询instIds
    private Map<String, String> getInstIdsByBusKeys(Set<String> busKeys) {
        Map<String, String> result = new HashMap<>();
        if (busKeys != null && !busKeys.isEmpty()) {
            Map<String, Object> params = new HashMap<>();
            params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            params.put("BUS_KEY_", new ArrayList<String>(busKeys));
            List<Map<String, Object>> instInfos = xcmgProjectOtherDao.queryInstInfosByBusKeys(params);
            if (instInfos != null && !instInfos.isEmpty()) {
                for (Map<String, Object> oneMap : instInfos) {
                    result.put(oneMap.get("BUS_KEY_").toString(), oneMap.get("INST_ID_").toString());
                }
            }
        }
        return result;
    }

    // 根据projectId查询project信息及关联表
    public JSONObject getXcmgProject(String uId) {
        JSONObject xcmgProject = xcmgProjectDao.queryProjectById(uId);

        if (xcmgProject != null) {
            try {
                xcmgProject.put("instId", xcmgProject.getString("INST_ID_"));
                xcmgProject.put("buildTime",
                        DateFormatUtil.format(xcmgProject.getDate("buildTime"), "yyyy-MM-dd HH:mm:ss"));
                xcmgProject.put("knotTime",
                        DateFormatUtil.format(xcmgProject.getDate("knotTime"), "yyyy-MM-dd HH:mm:ss"));
                Set<String> depIds = new HashSet<>();
                if (StringUtils.isNotBlank(xcmgProject.getString("mainDepId"))) {
                    depIds.add(xcmgProject.getString("mainDepId"));
                }
                if (StringUtils.isNotBlank(xcmgProject.getString("otherDepId"))) {
                    String[] arr = xcmgProject.getString("otherDepId").split(",", -1);
                    depIds.addAll(Arrays.asList(arr));
                }
                Map<String, String> depId2Names = getDepNamesByIds(depIds);
                if (StringUtils.isNotBlank(xcmgProject.getString("mainDepId"))) {
                    xcmgProject.put("mainDepName", depId2Names.get(xcmgProject.getString("mainDepId")));
                }
                if (StringUtils.isNotBlank(xcmgProject.getString("otherDepId"))) {
                    String[] arr = xcmgProject.getString("otherDepId").split(",", -1);
                    String otherDepNames = "";
                    for (String otherDepId : arr) {
                        otherDepNames += depId2Names.get(otherDepId) + ",";
                    }
                    xcmgProject.put("otherDepName", otherDepNames.substring(0, otherDepNames.length() - 1));
                }
                // 目标
                List<JSONObject> xcmgProjectExtensions = xcmgProjectExtensionDao.getExtensionByProjectId(uId);
                xcmgProject.put("xcmgProjectExtensions", xcmgProjectExtensions);

                // 查询该项目各个成员在之前阶段已经获得的分数总和
                Map<String, Object> params1 = new HashMap<>();
                params1.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
                params1.put("projectId", uId);
                List<Map<String, Object>> memberHasGetScores = xcmgProjectOtherDao.queryMemberScoreByPid(params1);
                Map<String, Double> userId2Score = new HashMap<>();
                for (Map<String, Object> oneMap : memberHasGetScores) {
                    userId2Score.put(oneMap.get("userId").toString(), (Double) oneMap.get("score"));
                }

                // 成员
                List<JSONObject> xcmgProjectMembers = xcmgProjectMemberDao.getMemberByProjectId(uId);
                for (JSONObject xcmgProjectMember : xcmgProjectMembers) {
                    Map<String, Object> result = new HashMap<>();
                    getUserInfoById(xcmgProjectMember.getString("userId"), result);
                    xcmgProjectMember.put("userJob", result.get("GW").toString());
                    xcmgProjectMember.put("gwno", result.get("gwno").toString());
                    if (userId2Score.containsKey(xcmgProjectMember.getString("userId"))) {
                        xcmgProjectMember.put("score", userId2Score.get(xcmgProjectMember.getString("userId")));
                    }
                }
                xcmgProject.put("xcmgProjectMembers", xcmgProjectMembers);

                // 产学研外部成员
                List<JSONObject> xcmgProjectMemOuts = xcmgProjectMemOutDao.getMemberOutByProjectId(uId);
                xcmgProject.put("xcmgProjectMemOut", xcmgProjectMemOuts);

                // 项目成果
                List<JSONObject> xcmgProjectAchievements = xcmgProjectAchievementDao.getAchievementByProjectId(uId);
                if (xcmgProjectAchievements != null && !xcmgProjectAchievements.isEmpty()) {
                    for (JSONObject oneAchievement : xcmgProjectAchievements) {
                        oneAchievement.put("output_time",
                                DateFormatUtil.format(oneAchievement.getDate("output_time"), "yyyy-MM-dd"));
                    }
                }
                xcmgProject.put("xcmgProjectAchievement", xcmgProjectAchievements);

                // 计划
                List<JSONObject> xcmgProjectPlans = xcmgProjectPlanDao.getPlanByProjectId(uId);
                if (xcmgProjectPlans != null && !xcmgProjectPlans.isEmpty()) {
                    for (JSONObject onePlan : xcmgProjectPlans) {
                        onePlan.put("planStartTime",
                                DateFormatUtil.format(onePlan.getDate("planStartTime"), "yyyy-MM-dd"));
                        onePlan.put("planEndTime", DateFormatUtil.format(onePlan.getDate("planEndTime"), "yyyy-MM-dd"));
                        onePlan.put("actualStartTime",
                                DateFormatUtil.format(onePlan.getDate("actualStartTime"), "yyyy-MM-dd"));
                        onePlan.put("actualEndTime",
                                DateFormatUtil.format(onePlan.getDate("actualEndTime"), "yyyy-MM-dd"));
                    }

                    // 根据计划中的阶段Id、项目大级别Id、项目来源Id查询交付物名称
                    if (StringUtils.isNotBlank(xcmgProject.getString("levelId"))
                            && StringUtils.isNotBlank(xcmgProject.getString("sourceId"))) {
                        Map<String, Object> params = new HashMap<>();
                        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
                        params.put("levelId", xcmgProject.getString("levelId"));
                        params.put("sourceId", xcmgProject.getString("sourceId"));
                        List<String> stageIds = new ArrayList<>();
                        for (JSONObject onePlan : xcmgProjectPlans) {
                            stageIds.add(onePlan.getString("stageId"));
                        }
                        params.put("stageId", stageIds);
                        List<Map<String, Object>> deliveryInfos = xcmgProjectOtherDao.queryDelivery(params);
                        // getOtherDelivery(deliveryInfos, xcmgProject, "");
                        // 得到每个stageId对应的交付物名称
                        Map<String, String> stageId2DeliveryNames = getStageId2DeliveryNames(deliveryInfos);
                        if (!stageId2DeliveryNames.isEmpty()) {
                            for (JSONObject onePlan : xcmgProjectPlans) {
                                onePlan.put("deliveryName", stageId2DeliveryNames.get(onePlan.getString("stageId")));
                            }
                        }
                    }
                }
                xcmgProject.put("xcmgProjectPlans", xcmgProjectPlans);

                // 查询项目进度的数据
                Map<String, Object> params = new HashMap<>();
                params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
                params.put("categoryId", xcmgProject.getString("categoryId"));
                List<Map<String, String>> stageInfos = xcmgProjectOtherDao.queryStage(params);
                xcmgProject.put("projectProcess", stageInfos);
                // 创建人
                OsUser creator = osUserManager.get(xcmgProject.getString("CREATE_BY_"));
                xcmgProject.put("creator", creator.getFullname());
                // 当前阶段是否延误（只对运行中的项目查询）
                // 没有风险是0，延迟5天之内是1，延迟大于5天是2，不涉及是3
                params.put("busKey", uId);
                List<Map<String, Object>> statusInfo = xcmgProjectOtherDao.queryProjectStatus(params);
                String status = "";
                if (statusInfo != null && statusInfo.size() == 1) {
                    status = statusInfo.get(0).get("STATUS_").toString();
                }
                int whetherDelay = 0;
                if ("RUNNING".equalsIgnoreCase(status)) {
                    Date currentStagePlanEndTime = null;
                    for (JSONObject plan : xcmgProjectPlans) {
                        if (plan.getString("stageId").equals(xcmgProject.getString("currentStageId"))) {
                            currentStagePlanEndTime = plan.getDate("planEndTime");
                            break;
                        }
                    }
                    if (currentStagePlanEndTime != null) {
                        long delayPardonTime = Long.parseLong(WebAppUtil.getProperty("projectRiskPardonMillSec"));
                        long currentTime =
                                DateFormatUtil.parseDate(XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd")).getTime();
                        if (currentTime - currentStagePlanEndTime.getTime() > delayPardonTime) {
                            whetherDelay = 2;
                        } else if (currentTime - currentStagePlanEndTime.getTime() <= delayPardonTime
                                && currentTime - currentStagePlanEndTime.getTime() > 0) {
                            whetherDelay = 1;
                        }
                    }
                } else if (!"SUCCESS_END".equalsIgnoreCase(status)) {
                    whetherDelay = 3;
                }
                xcmgProject.put("whetherDelay", whetherDelay);

            } catch (Exception e) {
                logger.error("Exception in getXcmgProject", e);
            }

        }
        return xcmgProject;
    }

    public List<Map<String, Object>> getOtherDelivery(List<Map<String, Object>> deliveryInfos, JSONObject xcmgProject,
                                                      String stageId) {
        try {
            if (ConstantUtil.PRODUCT_CATEGORY.equals(xcmgProject.getString("categoryId"))) {
                if (StringUtil.isNotEmpty(xcmgProject.getString("productIds"))) {
                    // 获取非必填交付物并且已经分配到项目成员的交付物
                    Map<String, Object> paramMap = new HashMap<>(16);
                    paramMap.put("projectId", xcmgProject.getString("projectId"));
                    paramMap.put("deliveryIds", deliveryInfos);
                    paramMap.put("stageId", stageId);
                    List<Map<String, Object>> list = xcmgProjectOtherDao.getOtherNeedDelivery(paramMap);
                    deliveryInfos.addAll(list);
                }
            }
        } catch (Exception e) {
            logger.error("Exception in getOtherDelivery", e);
        }

        return deliveryInfos;
    }

    // 得到stageId对应的交付物名称（逗号分隔）
    private Map<String, String> getStageId2DeliveryNames(List<Map<String, Object>> deliveryInfos) {
        Map<String, String> result = new HashMap<>();
        if (deliveryInfos == null || deliveryInfos.isEmpty()) {
            return result;
        }
        for (Map<String, Object> deliveryInfo : deliveryInfos) {
            if (deliveryInfo.get("stageId") == null || deliveryInfo.get("deliveryName") == null) {
                continue;
            }
            String stageId = deliveryInfo.get("stageId").toString();
            String deliveryName = deliveryInfo.get("deliveryName").toString();
            if (!result.containsKey(stageId)) {
                result.put(stageId, deliveryName);
            } else {
                String deliveryNames = result.get(stageId) + "，" + deliveryName;
                result.put(stageId, deliveryNames);
            }
        }
        return result;
    }

    // 创建新的project
    public void create(JSONObject entity) {
        entity.put("projectId", IdUtil.getId());
        entity.put("buildTime", null);
        entity.put("createTime", new Date());
        entity.put("createBy", ContextUtil.getCurrentUserId());
        xcmgProjectDao.createBaseInfo(entity);

        // 扩展信息
        JSONObject xcmgProjectExtension = new JSONObject();
        xcmgProjectExtension.put("id", IdUtil.getId());
        xcmgProjectExtension.put("projectId", entity.getString("projectId"));
        xcmgProjectExtension.put("createTime", new Date());
        xcmgProjectExtension.put("createBy", ContextUtil.getCurrentUserId());
        xcmgProjectExtension.put("projectBuildReason", entity.getString("projectBuildReason"));
        xcmgProjectExtension.put("describeTarget", entity.getString("describeTarget"));
        xcmgProjectExtension.put("mainTask", entity.getString("mainTask"));
        xcmgProjectExtension.put("applyPlan", entity.getString("applyPlan"));
        JSONArray measureTargetArr = entity.getJSONArray("SUB_project_measureTarget");
        if (measureTargetArr == null) {
            measureTargetArr = new JSONArray();
        }
        xcmgProjectExtension.put("measureTarget", measureTargetArr.toJSONString());
        xcmgProjectExtensionDao.createProjectExtension(xcmgProjectExtension);

        // 项目计划
        JSONArray planArr = entity.getJSONArray("SUB_project_plan");
        if (planArr != null && !planArr.isEmpty()) {
            for (int i = 0; i < planArr.size(); i++) {
                JSONObject planJsonInfo = planArr.getJSONObject(i);
                planJsonInfo.put("id", IdUtil.getId());
                planJsonInfo.put("projectId", entity.getString("projectId"));
                if (StringUtils.isBlank(planJsonInfo.getString("actualEndTime"))) {
                    planJsonInfo.put("actualEndTime", null);
                }
                if (StringUtils.isBlank(planJsonInfo.getString("actualStartTime"))) {
                    planJsonInfo.put("actualStartTime", null);
                }
                if (StringUtils.isBlank(planJsonInfo.getString("planEndTime"))) {
                    planJsonInfo.put("planEndTime", null);
                }
                if (StringUtils.isBlank(planJsonInfo.getString("planStartTime"))) {
                    planJsonInfo.put("planStartTime", null);
                }
                if (StringUtils.isBlank(planJsonInfo.getString("money"))) {
                    planJsonInfo.put("money", null);
                }
                if (StringUtils.isBlank(planJsonInfo.getString("deductScore"))) {
                    planJsonInfo.put("deductScore", null);
                }
                planJsonInfo.put("createTime", new Date());
                planJsonInfo.put("createBy", ContextUtil.getCurrentUserId());
                xcmgProjectPlanDao.createProjectPlan(planJsonInfo);
            }
        }

        // 成员信息
        JSONArray memArr = entity.getJSONArray("SUB_project_memberInfo");
        if (memArr != null && !memArr.isEmpty()) {
            for (int i = 0; i < memArr.size(); i++) {
                JSONObject memJsonInfo = memArr.getJSONObject(i);
                memJsonInfo.put("id", IdUtil.getId());
                memJsonInfo.put("projectId", entity.getString("projectId"));
                memJsonInfo.put("createTime", new Date());
                memJsonInfo.put("createBy", ContextUtil.getCurrentUserId());
                if (StringUtils.isBlank(memJsonInfo.getString("roleRatio"))) {
                    memJsonInfo.put("roleRatio", null);
                }
                xcmgProjectMemberDao.createProjectMember(memJsonInfo);
            }
        }

        // 产学研成员信息
        JSONArray memOutArr = entity.getJSONArray("SUB_cxy_memberInfo");
        if (memOutArr != null && !memOutArr.isEmpty()) {
            for (int i = 0; i < memOutArr.size(); i++) {
                JSONObject memOutJsonInfo = memOutArr.getJSONObject(i);
                memOutJsonInfo.put("id", IdUtil.getId());
                memOutJsonInfo.put("projectId", entity.getString("projectId"));
                memOutJsonInfo.put("createTime", new Date());
                memOutJsonInfo.put("createBy", ContextUtil.getCurrentUserId());
                xcmgProjectMemOutDao.createProjectMemberOut(memOutJsonInfo);
            }
        }

        // 成果计划信息
        JSONArray achievementArr = entity.getJSONArray("SUB_project_achievement");
        if (achievementArr != null && !achievementArr.isEmpty()) {
            for (int i = 0; i < achievementArr.size(); i++) {
                JSONObject achievement = achievementArr.getJSONObject(i);
                achievement.put("id", IdUtil.getId());
                achievement.put("projectId", entity.getString("projectId"));
                achievement.put("createTime", new Date());
                achievement.put("createBy", ContextUtil.getCurrentUserId());
                if (StringUtils.isBlank(achievement.getString("num"))) {
                    achievement.put("num", null);
                }
                if (StringUtils.isBlank(achievement.getString("output_time"))) {
                    achievement.put("output_time", null);
                }
                xcmgProjectAchievementDao.createProjectAchievement(achievement);
            }
        }

        // 创建本项目所有的文件夹
        JSONObject param = new JSONObject();
        param.put("projectId", entity.getString("projectId"));
        param.put("projectName", entity.getString("projectName"));
        param.put("categoryId", entity.getString("categoryId"));
        fileUploadManager.createProjectAllFolders(param);
    }

    public void update(JSONObject entity) {
        if (StringUtils.isBlank(entity.getString("buildTime"))) {
            entity.put("buildTime", null);
        }
        if (StringUtils.isBlank(entity.getString("knotTime"))) {
            entity.put("knotTime", null);
        }
        if (StringUtils.isBlank(entity.getString("knotScore"))) {
            entity.put("knotScore", null);
        }
        if (StringUtils.isBlank(entity.getString("knotRatio"))) {
            entity.put("knotRatio", null);
        }
        entity.put("updateBy", ContextUtil.getCurrentUserId());
        entity.put("updateTime", new Date());
        xcmgProjectDao.updateProjectBaseInfo(entity);
        // 更新项目文件最外层目录的名称
        JSONObject params = new JSONObject();
        params.put("projectId", entity.getString("projectId"));
        params.put("projectName", entity.getString("projectName"));
        xcmgProjectFileDao.updateProjectFileFoldName(params);

        // 扩展信息，先删除后新增
        xcmgProjectExtensionDao.delExtensionByProjectId(entity.getString("projectId"));
        JSONObject xcmgProjectExtension = new JSONObject();
        xcmgProjectExtension.put("id", IdUtil.getId());
        xcmgProjectExtension.put("projectId", entity.getString("projectId"));
        xcmgProjectExtension.put("createTime", new Date());
        xcmgProjectExtension.put("createBy", ContextUtil.getCurrentUserId());
        xcmgProjectExtension.put("projectBuildReason", entity.getString("projectBuildReason"));
        xcmgProjectExtension.put("describeTarget", entity.getString("describeTarget"));
        xcmgProjectExtension.put("mainTask", entity.getString("mainTask"));
        xcmgProjectExtension.put("applyPlan", entity.getString("applyPlan"));
        JSONArray measureTargetArr = entity.getJSONArray("SUB_project_measureTarget");
        if (measureTargetArr == null) {
            measureTargetArr = new JSONArray();
        }
        xcmgProjectExtension.put("measureTarget", measureTargetArr.toJSONString());
        xcmgProjectExtensionDao.createProjectExtension(xcmgProjectExtension);

        // 项目计划，先删除后新增
        xcmgProjectPlanDao.delPlanByProjectId(entity.getString("projectId"));
        JSONArray planArr = entity.getJSONArray("SUB_project_plan");
        if (planArr != null && !planArr.isEmpty()) {
            for (int i = 0; i < planArr.size(); i++) {
                JSONObject planJsonInfo = planArr.getJSONObject(i);
                planJsonInfo.put("id", IdUtil.getId());
                planJsonInfo.put("projectId", entity.getString("projectId"));
                if (StringUtils.isBlank(planJsonInfo.getString("actualEndTime"))) {
                    planJsonInfo.put("actualEndTime", null);
                }
                if (StringUtils.isBlank(planJsonInfo.getString("actualStartTime"))) {
                    planJsonInfo.put("actualStartTime", null);
                }
                if (StringUtils.isBlank(planJsonInfo.getString("planEndTime"))) {
                    planJsonInfo.put("planEndTime", null);
                }
                if (StringUtils.isBlank(planJsonInfo.getString("planStartTime"))) {
                    planJsonInfo.put("planStartTime", null);
                }
                if (StringUtils.isBlank(planJsonInfo.getString("money"))) {
                    planJsonInfo.put("money", null);
                }
                if (StringUtils.isBlank(planJsonInfo.getString("deductScore"))) {
                    planJsonInfo.put("deductScore", null);
                }
                planJsonInfo.put("createTime", new Date());
                planJsonInfo.put("createBy", ContextUtil.getCurrentUserId());
                xcmgProjectPlanDao.createProjectPlan(planJsonInfo);
            }
        }

        // 项目成员的更新处理
        JSONArray changeMemberArr = entity.getJSONArray("changeMemberArr");
        if (changeMemberArr != null && !changeMemberArr.isEmpty()) {
            JSONArray nowMemberArr = entity.getJSONArray("SUB_project_memberInfo");
            projectMemberProcess(changeMemberArr, nowMemberArr, entity.getString("projectId"),
                    entity.getString("categoryId"));
        }

        // 外部成员（部分流程有），先删除后新增
        xcmgProjectMemOutDao.delMemberOutByProjectId(entity.getString("projectId"));
        JSONArray memOutArr = entity.getJSONArray("SUB_cxy_memberInfo");
        if (memOutArr != null && !memOutArr.isEmpty()) {
            for (int i = 0; i < memOutArr.size(); i++) {
                JSONObject memOutJsonInfo = memOutArr.getJSONObject(i);
                memOutJsonInfo.put("id", IdUtil.getId());
                memOutJsonInfo.put("projectId", entity.getString("projectId"));
                memOutJsonInfo.put("createTime", new Date());
                memOutJsonInfo.put("createBy", ContextUtil.getCurrentUserId());
                xcmgProjectMemOutDao.createProjectMemberOut(memOutJsonInfo);
            }
        }

        // 项目成果计划存在着输出链接，因此不能直接删掉换新id
        JSONArray changeAchievementArr = entity.getJSONArray("changeAchievementArr");
        if (changeAchievementArr != null && !changeAchievementArr.isEmpty()) {
            saveAchievementList(changeAchievementArr, entity.getString("projectId"));
        }
    }

    // 项目成员的更新处理
    private void projectMemberProcess(JSONArray changeMemberArr, JSONArray nowMemberArr, String projectId,
                                      String categoryId) {
        try {
            // 判断是否为产品研发（整机）类
            boolean productFlag = false;
            if (ConstantUtil.PRODUCT_CATEGORY.equals(categoryId)) {
                productFlag = true;
            }
            // 需要删除的memberId
            Set<String> delMemberIds = new HashSet<>();
            // 需要删除的userId
            Set<String> delUserIds = new HashSet<>();
            // 变化的人员信息，包含在nowMemberArr中
            for (int i = 0; i < changeMemberArr.size(); i++) {
                JSONObject oneObject = changeMemberArr.getJSONObject(i);
                String state = oneObject.getString("_state");
                String id = oneObject.getString("id");
                if ("added".equals(state) || StringUtils.isBlank(id)) {
                    // 新增
                    oneObject.put("id", IdUtil.getId());
                    oneObject.put("projectId", projectId);
                    oneObject.put("createBy", ContextUtil.getCurrentUserId());
                    oneObject.put("createTime", new Date());
                    if (StringUtils.isBlank(oneObject.getString("roleRatio"))) {
                        oneObject.put("roleRatio", null);
                    }
                    // 对于产品研发（整机）类的，单独从交付物表中查询后补充
                    addRespDeliveryInfo(productFlag, oneObject);
                    xcmgProjectMemberDao.createProjectMember(oneObject);
                } else if ("modified".equals(state) && StringUtils.isNotBlank(id)) {
                    // 修改
                    oneObject.put("updateBy", ContextUtil.getCurrentUserId());
                    oneObject.put("updateTime", new Date());
                    if (StringUtils.isBlank(oneObject.getString("roleRatio"))) {
                        oneObject.put("roleRatio", null);
                    }
                    if (StringUtils.isBlank(oneObject.getString("finalRoleRatio"))) {
                        oneObject.put("finalRoleRatio", null);
                    }
                    // 对于产品研发（整机）类的，单独从交付物表中查询后补充
                    addRespDeliveryInfo(productFlag, oneObject);
                    xcmgProjectMemberDao.updateMember(oneObject);
                } else if ("removed".equals(state)) {
                    delMemberIds.add(oneObject.getString("id"));
                    delUserIds.add(oneObject.getString("userId"));
                }
            }
            // 根据Id删除项目成员
            if (!delMemberIds.isEmpty()) {
                JSONObject param = new JSONObject();
                param.put("ids", delMemberIds);
                xcmgProjectMemberDao.removeMemberByIds(param);
            }
            // 根据userId删除用户其他信息
            if (!delUserIds.isEmpty()) {
                // 积分
                Map<String, Object> deleteParams = new HashMap<>();
                deleteParams.put("projectId", projectId);
                deleteParams.put("userIds", new ArrayList<>(delUserIds));
                xcmgProjectScoreDao.deleteScoreByPidUserIds(deleteParams);
                // 评价
                xcmgProjectScoreDao.delSomeUserStageEvaluate(deleteParams);
                // 负责的交付物
                xcmgProjectDeliveryProductDao.delDeliveryProductByParam(deleteParams);
            }

        } catch (Exception e) {
            logger.error("Exception in projectMemberProcess", e);
        }
    }

    // 补充成员的交付物信息
    private void addRespDeliveryInfo(boolean productFlag, JSONObject memJsonInfo) {
        if (productFlag && StringUtil.isEmpty(memJsonInfo.getString("respDeliveryIds"))
                && StringUtils.isNotBlank(memJsonInfo.getString("userId"))) {
            JSONObject jsonObject = xcmgProjectOtherDao.getExistProjectDelivery(memJsonInfo);
            if (jsonObject != null) {
                memJsonInfo.put("respDeliveryIds", jsonObject.getString("deliveryIds"));
                memJsonInfo.put("respDeliveryNames", jsonObject.getString("deliveryNames"));
            }
        }
    }

    public void saveAchievementList(JSONArray changeAchievementArr, String projectId) {
        try {
            if (changeAchievementArr == null || changeAchievementArr.isEmpty()) {
                logger.warn("gridData is blank");
                return;
            }

            for (int i = 0; i < changeAchievementArr.size(); i++) {
                JSONObject oneObject = changeAchievementArr.getJSONObject(i);
                String state = oneObject.getString("_state");
                String id = oneObject.getString("id");
                if ("added".equals(state) || StringUtils.isBlank(id)) {
                    // 新增
                    oneObject.put("id", IdUtil.getId());
                    oneObject.put("projectId", projectId);
                    oneObject.put("createBy", ContextUtil.getCurrentUserId());
                    oneObject.put("createTime", new Date());
                    if (StringUtils.isBlank(oneObject.getString("num"))) {
                        oneObject.put("num", null);
                    }
                    if (StringUtils.isBlank(oneObject.getString("output_time"))) {
                        oneObject.put("output_time", null);
                    }
                    xcmgProjectOtherDao.insertAchievement(oneObject);
                } else if ("modified".equals(state)) {
                    // 修改
                    oneObject.put("updateBy", ContextUtil.getCurrentUserId());
                    oneObject.put("updateTime", new Date());
                    if (StringUtils.isBlank(oneObject.getString("num"))) {
                        oneObject.put("num", null);
                    }
                    if (StringUtils.isBlank(oneObject.getString("output_time"))) {
                        oneObject.put("output_time", null);
                    }
                    xcmgProjectOtherDao.updateAchievement(oneObject);
                } else if ("removed".equals(state)) {
                    // 删除
                    xcmgProjectAchievementDao.removeAchievementById(oneObject.getString("id"));
                    // 删除成果输出链接
                    JSONObject param = new JSONObject();
                    List<String> outPlanIds = new ArrayList<>();
                    outPlanIds.add(id);
                    param.put("outPlanIds", outPlanIds);
                    xcmgProjectOtherDao.deleteOut(param);
                }
            }
        } catch (Exception e) {
            logger.error("Exception in saveAchievementList", e);
        }
    }

    public void getUserInfoById(String userId, Map<String, Object> result) {
        result.put("mainDepName", "");
        result.put("mainDepId", "");
        result.put("GW", "");
        result.put("gwno", "");
        result.put("ZJ", "");
        result.put("ZJNo", "");
        if (StringUtils.isBlank(userId)) {
            return;
        }
        result.put("userId", userId);
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        params.put("USER_ID_", userId);
        List<Map<String, Object>> userMapInfo = xcmgProjectOtherDao.getUserInfoById(params);
        if (userMapInfo != null && !userMapInfo.isEmpty()) {
            for (Map<String, Object> oneMap : userMapInfo) {
                if (oneMap.get("dimKey").equals("_ADMIN")) {
                    result.put("mainDepName", oneMap.get("groupName").toString());
                    result.put("mainDepId", oneMap.get("groupId").toString());
                }
                if (oneMap.get("dimKey").equals("GW")) {
                    result.put("GW", oneMap.get("groupName").toString());
                    result.put("gwno", oneMap.get("SN_") == null ? "1" : oneMap.get("SN_").toString());
                }
                if (oneMap.get("dimKey").equals("ZJ")) {
                    result.put("ZJ", oneMap.get("groupName").toString());
                    result.put("ZJNo", oneMap.get("SN_") == null ? "1" : oneMap.get("SN_").toString());
                }
                result.put("userName", oneMap.get("userName").toString());
                result.put("userNo", oneMap.get("userNo").toString());
            }
        }
    }

    // 获取新的项目计划，有交付物则填充
    public JSONObject getNewPlan(String categoryId, String levelId, String sourceId) {
        JSONObject result = new JSONObject();
        List<JSONObject> planResult = new ArrayList<>();
        // 查询项目阶段
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        params.put("categoryId", categoryId);
        List<Map<String, String>> stageInfos = xcmgProjectOtherDao.queryStage(params);
        if (stageInfos == null || stageInfos.isEmpty()) {
            logger.error("Can't find stageInfos by categoryId {}", categoryId);
            return result;
        }
        List<String> stageIds = new ArrayList<>();
        for (Map<String, String> oneStageInfo : stageInfos) {
            JSONObject xcmgProjectPlan = new JSONObject();
            xcmgProjectPlan.put("stageId", oneStageInfo.get("stageId"));
            xcmgProjectPlan.put("stageNo", oneStageInfo.get("stageNo"));
            xcmgProjectPlan.put("stageName", oneStageInfo.get("stageName"));
            xcmgProjectPlan.put("stagePercent", oneStageInfo.get("stagePercent"));
            planResult.add(xcmgProjectPlan);
            stageIds.add(xcmgProjectPlan.getString("stageId"));
        }
        // 查询项目交付物
        if (StringUtils.isNotBlank(levelId) && StringUtils.isNotBlank(sourceId)) {
            params.put("levelId", levelId);
            params.put("sourceId", sourceId);
            params.put("stageId", stageIds);
            List<Map<String, Object>> deliveryInfos = xcmgProjectOtherDao.queryDelivery(params);
            // 得到每个stageId对应的交付物名称
            Map<String, String> stageId2DeliveryNames = getStageId2DeliveryNames(deliveryInfos);
            if (!stageId2DeliveryNames.isEmpty()) {
                for (JSONObject onePlan : planResult) {
                    onePlan.put("deliveryName", stageId2DeliveryNames.get(onePlan.getString("stageId")));
                }
            }
        }
        result.put("newPlan", planResult);
        result.put("stage", stageInfos);
        return result;
    }

    // 删除项目及关联的所有信息
    public JsonResult deleteProject(String[] projectIdArr, String[] instIdArr) {
        if (projectIdArr.length != instIdArr.length) {
            return new JsonResult(false, "项目和流程数据不相同！");
        }
        for (int i = 0; i < projectIdArr.length; i++) {
            String projectId = projectIdArr[i];
            // 删除base表
            xcmgProjectDao.removeById(projectId);
            xcmgProjectExtensionDao.delExtensionByProjectId(projectId);
            xcmgProjectPlanDao.delPlanByProjectId(projectId);
            xcmgProjectMemberDao.delMemberByProjectId(projectId);
            // 删除本项目归档的文件夹和文件
            xcmgProjectFileUploadManager.deleteProject(projectId);
            // 删除积分表
            xcmgProjectScoreDao.deleteScoreByPid(projectId);
            // 删除进度追赶设置表
            xcmgProjectOtherDao.deleteProgressRunSetByProjectId(projectId);
            // 删除成果表
            xcmgProjectAchievementDao.delAchievementByProjectId(projectId);
            // 删除成果链接表
            JSONObject param = new JSONObject();
            param.put("projectId", projectId);
            xcmgProjectOtherDao.deleteOut(param);
            // 删除项目输入表
            xcmgProjectOtherDao.deleteInput(param);
            // 删除实例
            bpmInstManager.deleteCascade(instIdArr[i], "");
        }
        return new JsonResult(true, "成功删除!");
    }

    // 查询特定阶段的交付物类型，并查询交付物指定的提交人
    public List<Map<String, Object>> getDeliveryId2Name(String stageId, String levelPid, String sourceId,
                                                        String queryPublic, String projectId) {
        List<String> stageIds = new ArrayList<>();
        stageIds.add(stageId);
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        params.put("levelId", levelPid);
        params.put("sourceId", sourceId);
        params.put("stageId", stageIds);
        params.put("queryPublic", queryPublic);
        List<Map<String, Object>> deliveryInfos = xcmgProjectOtherDao.queryDelivery(params);
        // 查找交付物分配的指定提交人
        if (deliveryInfos != null && !deliveryInfos.isEmpty() && StringUtils.isNotBlank(projectId)) {
            queryDeliveryPointMember(deliveryInfos, projectId);
        }
        return deliveryInfos;
    }

    // 查找交付物分配的指定提交人
    private void queryDeliveryPointMember(List<Map<String, Object>> deliveryInfos, String projectId) {
        Map<String, Map<String, Object>> deliveryId2Obj = new HashMap<>();
        for (Map<String, Object> oneDelivery : deliveryInfos) {
            oneDelivery.put("pointUserIds", new HashSet<>());
            deliveryId2Obj.put(oneDelivery.get("deliveryId").toString(), oneDelivery);
        }
        Set<String> deliveryIds = deliveryId2Obj.keySet();
        Map<String, Object> params = new HashMap<>();
        params.put("projectId", projectId);
        List<JSONObject> memberInfos = xcmgProjectOtherDao.queryValidMembersByDeliverys(params);
        if (memberInfos == null || memberInfos.isEmpty()) {
            return;
        }
        for (JSONObject oneMember : memberInfos) {
            String respDeliveryIds = oneMember.getString("respDeliveryIds");
            String userId = oneMember.getString("userId");
            if (StringUtils.isBlank(respDeliveryIds) || StringUtils.isBlank(userId)) {
                continue;
            }
            Set<String> respDeliveryIdSet = new HashSet<>(Arrays.asList(respDeliveryIds.split(",", -1)));
            respDeliveryIdSet.retainAll(deliveryIds);
            if (!respDeliveryIdSet.isEmpty()) {

                for (String deliveryId : respDeliveryIdSet) {
                    Set<String> tmp = (HashSet) deliveryId2Obj.get(deliveryId).get("pointUserIds");
                    tmp.add(userId);
                }
            }
        }
    }

    // 检查当前是否有缺失的必选交付物或者指定交付物类型的交付物
    public JSONObject checkStageDelivery(String projectId, String stageId, String levelId, String sourceId,
                                         String pointDeliveryName, String categoryName) {
        if (StringUtils.isNotBlank(pointDeliveryName)) {
            JSONObject result = checkPointDeliveryName(projectId, stageId, pointDeliveryName);
            return result;
        }
        JSONObject result = new JSONObject();
        result.put("result", "true");
        if (StringUtils.isBlank(projectId) || StringUtils.isBlank(stageId) || StringUtils.isBlank(levelId)
                || StringUtils.isBlank(sourceId)) {
            logger.error("参数缺失");
            result.put("result", "false");
            result.put("content", "参数缺失，检查失败！");
            return result;
        }
        // 查询应该上传的交付物
        List<Map<String, Object>> shouldUploadDeliverys = getDeliveryId2Name(stageId, levelId, sourceId, null, null);
        if ((shouldUploadDeliverys == null || shouldUploadDeliverys.isEmpty())
                && !categoryName.contains(RdmConst.CXY)) {
            return result;
        }
        // 查询RDM中该项目当前阶段已经上传的交付物
        Set<String> uploadFileDeliveryIds = new HashSet<>();
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        params.put("projectId", projectId);
        params.put("pid", stageId);
        params.put("stageId", stageId);
        params.put("isFolder", "0");
        List<Map<String, Object>> uploadFiles = xcmgProjectFileUploadManager.queryProjectFileInfos(params);
        if (uploadFiles != null && !uploadFiles.isEmpty()) {
            for (Map<String, Object> oneFile : uploadFiles) {
                if (oneFile.get("deliveryId") != null) {
                    uploadFileDeliveryIds.add(oneFile.get("deliveryId").toString());
                }
            }
        }
        // 请求PDM中关联的这个项目这个阶段的交付物
        List<Map<String, Object>> pdmFiles = pdmApiManager.getPdmProjectFiles(params);
        if (pdmFiles != null && !pdmFiles.isEmpty()) {
            for (Map<String, Object> onePdm : pdmFiles) {
                String deliveryId = StringProcessUtil.toGetStringNotNull(onePdm.get("deliveryId"));
                if (StringUtils.isNotBlank(deliveryId)) {
                    uploadFileDeliveryIds.add(deliveryId);
                }
            }
        }

        // 查询SDM中（当前在RDM的仿真设计模块）关联的这个项目的流程有没有
        // 查询“仿真申请书”的交付物类型信息
        Map<String, Object> param = new HashMap<>();
        param.put("projectId", projectId);
        param.put("deliveryName", RdmConst.PROJECT_DELIVERY_FZSQS);
        List<JSONObject> deliveryInfos = xcmgProjectConfigDao.queryDeliveryByNameAndProjectId(param);
        if (deliveryInfos != null && !deliveryInfos.isEmpty()) {
            String deliveryId = deliveryInfos.get(0).getString("deliveryId");
            List<JSONObject> fzsjList = fzsjDao.queryFzsjByProject(params);
            if (fzsjList != null && !fzsjList.isEmpty()) {
                uploadFileDeliveryIds.add(deliveryId);
            }
        }

        if (categoryName.contains(RdmConst.CXY) && (shouldUploadDeliverys == null || shouldUploadDeliverys.isEmpty())
                && uploadFileDeliveryIds.isEmpty()) {
            result.put("result", "false");
            result.put("content", "产学研项目请至少上传一个交付物！");
            return result;
        }

        // 遍历找到缺失的
        StringBuilder missDeliveryNames = new StringBuilder();
        if (shouldUploadDeliverys != null && !shouldUploadDeliverys.isEmpty()) {
            for (Map<String, Object> oneDelivery : shouldUploadDeliverys) {
                if (!uploadFileDeliveryIds.contains(oneDelivery.get("deliveryId").toString())) {
                    missDeliveryNames.append(oneDelivery.get("deliveryName").toString()).append("，");
                }
            }
        }
        if (missDeliveryNames.length() > 0) {
            result.put("result", "false");
            result.put("content", "下列必传交付物缺失，请在页面下方‘文件归档’处上传或在对应系统（PDM/TDM/SDM）及相关流程中创建并审批完成！<br>缺失交付物："
                    + missDeliveryNames.substring(0, missDeliveryNames.length() - 1));
            return result;
        }

        return result;
    }

    // 查询指定交付物类型是否已经上传(对于“变更评审材料”需要当天上传的)
    public JSONObject checkPointDeliveryName(String projectId, String stageId, String pointDeliveryName) {
        JSONObject result = new JSONObject();
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        params.put("projectId", projectId);
        params.put("pid", stageId);
        params.put("isFolder", "0");
        params.put("deliveryName", pointDeliveryName);
        List<Map<String, Object>> uploadFiles = xcmgProjectFileUploadManager.queryProjectFileInfos(params);
        if (!"变更评审材料".equals(pointDeliveryName)) {
            if (uploadFiles != null && !uploadFiles.isEmpty()) {
                result.put("result", "true");
                return result;
            }
            result.put("result", "false");
            result.put("content", "请于页面下方的“文件归档”处，上传审批通过的“" + pointDeliveryName + "”文件或者扫描件");
            return result;
        }
        if (uploadFiles != null && !uploadFiles.isEmpty()) {
            String prefix = XcmgProjectUtil.getNowLocalDateStr("yyyyMMdd");
            prefix = "(" + prefix;
            for (Map<String, Object> file : uploadFiles) {
                if (file.get("fileName") != null) {
                    String fileName = file.get("fileName").toString();
                    if (fileName.startsWith(prefix)) {
                        result.put("result", "true");
                        return result;
                    }
                }
            }
        }
        result.put("result", "false");
        result.put("content", "请于页面下方的“文件归档”处，上传当日审批通过的“变更评审材料”文件或者扫描件");
        return result;
    }

    // 处理积分相关的业务（阶段的延误扣分、每个人的阶段得分）
    public void processProjectScore(JSONObject projectJson, boolean isEndStage) {
        try {
            // 查询该项目的标准分
            String projectId = projectJson.getString("projectId");
            if (StringUtils.isBlank(projectId)) {
                return;
            }
            Map<String, Object> params = new HashMap<>();
            params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            params.put("projectId", projectId);
            List<Map<String, Object>> standardInfos = xcmgProjectOtherDao.queryStandardStoreByPid(params);
            if (standardInfos == null || standardInfos.isEmpty()) {
                logger.error("Can't find standard score by projectId {}", projectId);
                return;
            }
            Map<String, Object> standardScoreInfo = standardInfos.get(0);
            if (standardScoreInfo.get("pointStandardScore") != null
                    && StringUtils.isNotBlank(standardScoreInfo.get("pointStandardScore").toString())) {
                standardScoreInfo.put("score",
                        Integer.parseInt(standardScoreInfo.get("pointStandardScore").toString()));
            }
            int standardScore = (int) standardScoreInfo.get("score");

            // 查询起止阶段的周期天数、当前阶段的详细信息、已完成阶段当前延误扣分总和
            JSONArray planArr = projectJson.getJSONArray("SUB_project_plan");
            List<JSONObject> planList = planArr.toJavaList(JSONObject.class);
            Collections.sort(planList, new Comparator<JSONObject>() {
                @Override
                public int compare(JSONObject o1, JSONObject o2) {
                    return o1.getString("stageNo").compareTo(o2.getString("stageNo"));
                }
            });
            String currentStageNo = projectJson.getString("currentStageNo");
            // 项目起始阶段
            JSONObject startStage = planList.get(0);
            // 项目结束阶段
            JSONObject endStage = planList.get(planList.size() - 1);
            JSONObject currentStage = null;
            boolean currentIsFirst = false;
            for (int i = 0; i < planArr.size(); i++) {
                JSONObject obj = planArr.getJSONObject(i);
                String objStageNo = obj.getString("stageNo");
                if (objStageNo.equals(currentStageNo)) {
                    currentStage = obj;
                    if (i == 0) {
                        currentIsFirst = true;
                    }
                    break;
                }
            }

            JSONArray memberInfos = projectJson.getJSONArray("SUB_project_memberInfo");
            // 计算所有有效成员的角色系数之和,用于计算当前阶段的分数
            double roleRatioSum = 0.0;
            for (int i = 0; i < memberInfos.size(); i++) {
                JSONObject oneMember = memberInfos.getJSONObject(i);
                // 冻结人员、信息不完整的人员系数均不参与计算
                if (!"02".equalsIgnoreCase(oneMember.getString("userValid"))
                        && StringUtils.isNotBlank(oneMember.getString("userId"))
                        && StringUtils.isNotBlank(oneMember.getString("memberDeptId"))) {
                    roleRatioSum += oneMember.getDouble("roleRatio") == null ? 0.0
                            : memberInfos.getJSONObject(i).getDouble("roleRatio");
                }
            }

            // 计算阶段的延误扣分(超过宽限天数的才扣分)
            long delayPardonTime = Long.parseLong(WebAppUtil.getProperty("projectRiskPardonMillSec"));
            long currentTime = DateFormatUtil.parseDate(XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd")).getTime();
            long planEndTime = DateFormatUtil.parseDate(currentStage.getString("planEndTime")).getTime();
            // 计算延误率
            double delayRate = 0.0;
            // 超过宽限时间
            if (currentTime - planEndTime > delayPardonTime) {
                long projectStartTime = DateFormatUtil.parseDate(startStage.getString("planStartTime")).getTime();
                long projectEndTime = DateFormatUtil.parseDate(endStage.getString("planEndTime")).getTime();
                // 项目总天数
                double dayPeriod = (projectEndTime - projectStartTime) * 1.0 / 1000 / 60 / 60 / 24;
                dayPeriod += 1;
                // 第一阶段延误率=（实际结束日期-计划结束日期）/总天数
                if (currentIsFirst) {
                    delayRate = (currentTime - planEndTime) * 1.0 / 1000 / 60 / 60 / 24 / dayPeriod;
                } else {
                    // 其他阶段延误率=[（实际结束日期-实际开始日期）-（计划结束日期-计划开始日期）]/总天数
                    long actualStartTime =
                            DateFormatUtil.parseDate(currentStage.getString("actualStartTime")).getTime();
                    long planStartTime = DateFormatUtil.parseDate(currentStage.getString("planStartTime")).getTime();
                    delayRate = ((currentTime - actualStartTime) - (planEndTime - planStartTime)) * 1.0 / 1000 / 60 / 60
                            / 24 / dayPeriod;
                }
            }
            if (delayRate < 0.0) {
                delayRate = 0.0;
            }
            // 本阶段扣掉的分数
            Double deductScore = 0.0;
            // 保留3位小数
            deductScore = 0.3 * standardScore * delayRate;
            deductScore = (double) Math.round(deductScore * 1000) / 1000;
            // 如果当前项目为老项目赶进度的状态，则不扣延误分，2020-1-10 by liangchuanjiang
            String progressRunStatus = projectJson.getString("progressRunStatus");
            String scoreGetTime = projectJson.getString("scoreGetTime");
            if ("1".equals(progressRunStatus)) {
                deductScore = 0.0;
            }

            // 更新当前阶段的延误扣分
            params.put("stageId", currentStage.getString("stageId"));
            params.put("deductScore", deductScore);
            xcmgProjectOtherDao.updateProjectPlanDeductScore(params);
            List<JSONObject> memberInfoList = memberInfos.toJavaList(JSONObject.class);
            if (isEndStage) {
                // 最终阶段的得分计算（先算最后阶段应得分，然后将因系数变化、打分变化产生的差值分在各个阶段进行计算到每个人身上）
                endStageMemberScore(standardScore, deductScore, currentStage,
                        Double.parseDouble(projectJson.getString("knotRatio")), memberInfoList, roleRatioSum, scoreGetTime,
                        projectJson.getString("categoryId"));
            } else {
                // 非最终阶段的得分计算
                notEndStageMemberScore(standardScore, deductScore, currentStage, memberInfoList, roleRatioSum,
                        scoreGetTime);
            }
        } catch (Exception e) {
            logger.error("Exception in processProjectScore", e);
        }

    }

    // 非最终阶段的成员得分计算
    private void notEndStageMemberScore(int standardScore, double deductScore, JSONObject currentStage,
                                        List<JSONObject> memberInfos, double roleRatioSum, String scoreGetTime) {
        double stagePercent = Double.parseDouble(currentStage.getString("stagePercent")) / 100;
        // 该阶段所有人应得总分（可能是负数）
        double totalActualScore = standardScore * stagePercent - deductScore;
        List<Map<String, Object>> memberScores = new ArrayList<>();
        for (int i = 0; i < memberInfos.size(); i++) {
            JSONObject memberInfo = memberInfos.get(i);
            Double memRoleRatio = memberInfo.getDouble("roleRatio");
            // 没有角色系数的不给分
            if (memRoleRatio == null) {
                continue;
            }
            // 成员已经失效、成员关键信息缺失的没有分
            String userValid = memberInfo.getString("userValid");
            if ("02".equalsIgnoreCase(userValid) || StringUtils.isBlank(memberInfo.getString("userId"))
                    || StringUtils.isBlank(memberInfo.getString("memberDeptId"))) {
                continue;
            }
            double memberStageScore = totalActualScore * memRoleRatio / roleRatioSum;
            memberStageScore = (double) Math.round(memberStageScore * 1000) / 1000;
            Map<String, Object> oneMemberScore = new HashMap<>();
            oneMemberScore.put("id", IdUtil.getId());
            oneMemberScore.put("projectId", currentStage.getString("projectId"));
            oneMemberScore.put("stageId", currentStage.getString("stageId"));
            oneMemberScore.put("userId", memberInfo.getString("userId"));
            oneMemberScore.put("stageRoleRatio", memberInfo.getDoubleValue("roleRatio"));
            oneMemberScore.put("stageScore", memberStageScore);
            oneMemberScore.put("roleId", memberInfo.getString("roleId"));
            oneMemberScore.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            String createTime = generateScoreGetTime(scoreGetTime, currentStage);
            oneMemberScore.put("CREATE_TIME_", createTime);
            memberScores.add(oneMemberScore);
        }

        // 先删除本阶段所有的成员的积分（如果存在，防止有之前的垃圾数据，如驳回场景）
        Map<String, Object> params = new HashMap<>();
        params.put("projectId", currentStage.getString("projectId"));
        params.put("stageId", currentStage.getString("stageId"));
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        xcmgProjectOtherDao.deleteMemberStageScore(params);

        // 再插入本次的积分数据
        if (!memberScores.isEmpty()) {
            for (Map<String, Object> memberScore : memberScores) {
                xcmgProjectOtherDao.insertMemberStageScore(memberScore);
            }
        }
    }

    // 最终阶段的成员得分计算
    public void endStageMemberScore(int standardScore, double endStageDeductScore, JSONObject endStage,
                                    double knotRatio, List<JSONObject> memberInfos, double endStageRoleRatioSum, String scoreGetTime,
                                    String categoryId) {
        String projectId = endStage.getString("projectId");
        // 用于存储本函数中最后计算的所有人的分数，存入数据库
        Map<String, Map<String, Object>> userId2EndStageScores = new HashMap<>();
        // *******************1、首先计算出最后一个阶段，所有成员的阶段得分（与之前阶段的计算一致）****************************
        double endStagePercent = Double.parseDouble(endStage.getString("stagePercent")) / 100;
        // 最后阶段所有人应得总分（可能是负数）
        double endStageTotalActualScore = standardScore * endStagePercent - endStageDeductScore;
        Map<String, JSONObject> userId2MemberInfo = new HashMap<>();
        for (int i = 0; i < memberInfos.size(); i++) {
            JSONObject memberInfo = memberInfos.get(i);
            Double memRoleRatio = memberInfo.getDouble("roleRatio");
            // 没有角色系数的不给分
            if (memRoleRatio == null) {
                continue;
            }
            // 成员已经失效、成员关键信息缺失的没有分
            String userValid = memberInfo.getString("userValid");
            if ("02".equalsIgnoreCase(userValid) || StringUtils.isBlank(memberInfo.getString("userId"))
                    || StringUtils.isBlank(memberInfo.getString("memberDeptId"))) {
                continue;
            }
            userId2MemberInfo.put(memberInfo.getString("userId"), memberInfo);
            double memberStageScore = endStageTotalActualScore * memRoleRatio / endStageRoleRatioSum;
            memberStageScore = (double) Math.round(memberStageScore * 1000) / 1000;
            Map<String, Object> oneMemberScore = new HashMap<>();
            oneMemberScore.put("projectId", projectId);
            oneMemberScore.put("stageId", endStage.getString("stageId"));
            oneMemberScore.put("userId", memberInfo.getString("userId"));
            oneMemberScore.put("stageRoleRatio", memberInfo.getDoubleValue("roleRatio"));
            oneMemberScore.put("stageScore", memberStageScore);
            oneMemberScore.put("roleId", memberInfo.getString("roleId"));
            userId2EndStageScores.put(memberInfo.getString("userId"), oneMemberScore);
        }
        // *******************2、因为项目结项系数变化和最后打分评价的变化，需要在所有阶段上（包括最后阶段）对每个人进行差值得分的计算
        Map<String, Double> userId2DiffScoreSum = new HashMap<>();
        computeDiffScore(userId2DiffScoreSum, standardScore, knotRatio, projectId, userId2EndStageScores,
                userId2MemberInfo, categoryId);
        // **3、所有阶段的差值得分之和，最终加到endStage上面存储到数据库(如果endStage中有的成员没有，需要新建，因为这些成员在前面阶段存在差值)
        if (!userId2DiffScoreSum.isEmpty()) {
            for (Map.Entry<String, Double> entry : userId2DiffScoreSum.entrySet()) {
                if (userId2EndStageScores.containsKey(entry.getKey())) {
                    // 最后阶段有这个成员的得分，则差值分加进去
                    Map<String, Object> endStageScore = userId2EndStageScores.get(entry.getKey());
                    double endScore = (double) endStageScore.get("stageScore");
                    double finalScore = (double) Math.round((endScore + entry.getValue()) * 1000) / 1000;
                    endStageScore.put("stageScore", finalScore);
                } else {
                    // 最后阶段没有这个成员的得分，则新增差值分对象
                    Map<String, Object> oneMemberScore = new HashMap<>();
                    oneMemberScore.put("projectId", projectId);
                    oneMemberScore.put("stageId", endStage.getString("stageId"));
                    oneMemberScore.put("userId", entry.getKey());
                    double stageRoleRatio = 0.0;
                    if (userId2MemberInfo.get(entry.getKey()) != null) {
                        stageRoleRatio = userId2MemberInfo.get(entry.getKey()).getDouble("roleRatio");
                    }
                    oneMemberScore.put("stageRoleRatio", stageRoleRatio);
                    double diffScore = entry.getValue();
                    oneMemberScore.put("stageScore", (double) Math.round(diffScore * 1000) / 1000);
                    userId2EndStageScores.put(entry.getKey(), oneMemberScore);
                }
            }
        }
        // 4、数据存入数据库
        if (!userId2EndStageScores.isEmpty()) {
            for (Map.Entry<String, Map<String, Object>> entry : userId2EndStageScores.entrySet()) {
                Map<String, Object> oneMemberEndStageScore = entry.getValue();
                oneMemberEndStageScore.put("id", IdUtil.getId());
                oneMemberEndStageScore.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                String createTime = generateScoreGetTime(scoreGetTime, endStage);
                oneMemberEndStageScore.put("CREATE_TIME_", createTime);
                xcmgProjectOtherDao.insertMemberStageScore(oneMemberEndStageScore);
            }
        }
    }

    // 1、结项系数导致的差值在各个阶段各个人的计算；2、打分评价导致的差值在各个阶段各个人的计算。(仅限于之前那个阶段有分的成员)
    private void computeDiffScore(Map<String, Double> userId2DiffScoreSum, int standardScore, double knotRatio,
                                  String projectId, Map<String, Map<String, Object>> userId2EndStageScores,
                                  Map<String, JSONObject> userId2MemberInfo, String categoryId) {
        // 各个阶段的扣减分(没用)
        Map<String, Object> param = new HashMap<>();
        param.put("projectId", projectId);
        List<JSONObject> plans = xcmgProjectOtherDao.queryPlanByProjectId(param);
        Map<String, Double> stageId2DeductScore = new HashMap<>();
        for (JSONObject onePlan : plans) {
            stageId2DeductScore.put(onePlan.getString("stageId"),
                    onePlan.getDouble("deductScore") == null ? 0.0 : onePlan.getDouble("deductScore"));
        }
        //以上没用
        // 该类型项目的全部阶段
        param.clear();
        param.put("categoryId", categoryId);
        List<Map<String, String>> stageInfos = xcmgProjectOtherDao.queryStage(param);
        // 结项系数差值总分
        double totalKnotDiffScore = (knotRatio - 1.0) * standardScore;

        // 所有成员在所有阶段获得的分数和系数，按照阶段进行划分
        param.clear();
        param.put("projectId", projectId);
        //项目结算计算差值分时,扣分算上离职的,加分不算离职的
        if (totalKnotDiffScore >= 0) {
            param.put("excludeOut", "yes");
        }
        List<Map<String, Object>> allStageMemberScores = xcmgProjectOtherDao.queryMemberScores(param);
        allStageMemberScores.addAll(userId2EndStageScores.values());
        Map<String, List<Map<String, Object>>> stageId2MemScoreList = new HashMap<>();
        for (Map<String, Object> oneData : allStageMemberScores) {
            String stageId = oneData.get("stageId").toString();
            if (!stageId2MemScoreList.containsKey(stageId)) {
                stageId2MemScoreList.put(stageId, new ArrayList<>());
            }
            stageId2MemScoreList.get(stageId).add(oneData);
        }
        // 每个阶段每个人依次处理
        for (Map<String, String> oneStage : stageInfos) {
            double stagePercent = Double.parseDouble(oneStage.get("stagePercent")) / 100;
            double stageKnotDiffScore = stagePercent * totalKnotDiffScore;
            String stageId = oneStage.get("stageId");

            List<Map<String, Object>> oneStageMemScoreList = stageId2MemScoreList.get(stageId);
            if (oneStageMemScoreList == null || oneStageMemScoreList.isEmpty()) {
                continue;
            }
            // 当前阶段所有人的系数之和
            double oneStageRoleRatioMultiScoreTotal = 0.0;
            for (Map<String, Object> oneMemScore : oneStageMemScoreList) {
                String userId = oneMemScore.get("userId").toString();
                double stageRoleRatio = (double) oneMemScore.get("stageRoleRatio");
                oneStageRoleRatioMultiScoreTotal += stageRoleRatio;
            }
            // 遍历阶段中的所有人，得到每个人的差值分，并存入每个人差值和中
            for (Map<String, Object> oneMemScore : oneStageMemScoreList) {
                String userId = oneMemScore.get("userId").toString();
                double diffScore1 =
                        stageKnotDiffScore * (double) oneMemScore.get("stageRoleRatio") / oneStageRoleRatioMultiScoreTotal;
                if (!userId2DiffScoreSum.containsKey(userId)) {
                    userId2DiffScoreSum.put(userId, 0.0);
                }
                userId2DiffScoreSum.put(userId, userId2DiffScoreSum.get(userId) + diffScore1);
            }
        }

    }

    // 返回积分的发放时间。1、为空则返回当前。2、当前实际结束时间。3、计划结束时间。4、自定义时间。
    private String generateScoreGetTime(String scoreGetTime, JSONObject currentStage) {
        if (StringUtils.isBlank(scoreGetTime)) {
            return XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss");
        }
        if ("actualEndTime".equalsIgnoreCase(scoreGetTime)) {
            return XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss");
        }
        if ("planEndTime".equalsIgnoreCase(scoreGetTime)) {
            return DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(currentStage.getString("planEndTime")), -8));
        }
        if ("actualEndTimeAgain".equalsIgnoreCase(scoreGetTime)) {
            return DateUtil
                    .formatDate(DateUtil.addHour(DateUtil.parseDate(currentStage.getString("actualEndTime")), -8));
        }
        // 存入数据库的是UTC，从前台返回的已经就是UTC了不需要再处理
        return scoreGetTime;
    }

    public JSONArray getChangeInfo(JSONObject postDataJson, HttpServletResponse response, HttpServletRequest request)
            throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("taskId_", postDataJson.getString("taskId_"));
        List<Map<String, String>> list = xcmgProjectOtherDao.queryChangeInfo(params);
        JSONArray resultJsonArr = new JSONArray();
        if (list != null && list.size() > 0) {
            for (Map<String, String> oneMap : list) {
                JSONObject resultJson = XcmgProjectUtil.convertMap2JsonString(oneMap);
                resultJsonArr.add(resultJson);
            }
        }
        return resultJsonArr;
    }

    public Map<String, String> getPreHandleResult(String actInstId) {
        Map<String, String> result = new HashMap<>();
        // 设置分页信息
        Page page = new Page();
        int pageIndex = 0;
        int pageSize = 1;
        page.setPageSize(pageSize);
        page.setPageIndex(pageIndex);
        List<BpmNodeJump> list = bpmNodeJumpManager.getByActInstId(actInstId, page);
        if (list == null || list.isEmpty()) {
            return result;
        }
        BpmNodeJump preNode = list.get(0);
        result.put("result", preNode.getCheckStatus());
        result.put("resultText", preNode.getCheckStatusText());
        result.put("remark", preNode.getRemark());
        result.put("nodeName", preNode.getNodeName());
        String handlerId = preNode.getHandlerId();
        if (StringUtil.isNotEmpty(handlerId)) {
            IUser user = userService.getByUserId(handlerId);
            result.put("handler", user.getFullname());
        }
        String ownerId = preNode.getOwnerId();
        if (StringUtils.isNotBlank(ownerId) && !ownerId.equalsIgnoreCase(handlerId)) {
            IUser user = userService.getByUserId(ownerId);
            result.put("owner", user.getFullname());
        }
        return result;
    }

    // 导出项目excel
    public void exportProjectExcel(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result =
                xcmgProjectReportManager.progressReportList(request, response, false, "exportProjectExcel");
        List<Map<String, Object>> finalSubProjectList = result.getData();
        Map<String, Object> queryPlanParam = new HashMap<>();
        for (Map<String, Object> oneMap : finalSubProjectList) {
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
            // 进度追赶状态
            if (oneMap.containsKey("progressRunStatus")) {
                String progressRunStatus = oneMap.get("progressRunStatus").toString();
                if ("1".equals(progressRunStatus)) {
                    oneMap.put("progressRunStatus", "是");
                } else {
                    oneMap.put("progressRunStatus", "否");
                }
            } else {
                oneMap.put("progressRunStatus", "否");
            }
            // 增加项目的计划开始时间和计划结束时间
            queryPlanParam.clear();
            queryPlanParam.put("projectId", oneMap.get("projectId").toString());
            List<JSONObject> plans = xcmgProjectOtherDao.queryPlanByProjectId(queryPlanParam);
            if (plans != null && !plans.isEmpty()) {
                if (plans.get(0).getDate("planStartTime") != null) {
                    oneMap.put("planStartTime",
                            DateFormatUtil.format(plans.get(0).getDate("planStartTime"), "yyyy-MM-dd"));
                }
                if (plans.size() > 1 && plans.get(plans.size() - 1).getDate("planEndTime") != null) {
                    oneMap.put("planEndTime",
                            DateFormatUtil.format(plans.get(plans.size() - 1).getDate("planEndTime"), "yyyy-MM-dd"));
                }
            }
        }

        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "科技项目列表";
        String excelName = nowDate + title;
        String[] fieldNames = {"项目名称", "项目编号", "牵头部门", "项目来源", "项目类别", "立项级别", "结项级别", "项目负责人", "项目状态", "当前阶段", "当前处理人",
                "当前审批任务", "当前审批任务创建时间", "进度风险", "立项完成时间", "结项完成时间", "计划开始时间", "计划结束时间", "自筹财务订单号", "进度追赶状态"};
        String[] fieldCodes = {"projectName", "number", "mainDepName", "sourceName", "categoryName", "initLevelName",
                "levelName", "respMan", "statusName", "currentStageName", "allTaskUserNames", "taskName", "taskCreateTime",
                "riskName", "buildTime", "knotTime", "planStartTime", "planEndTime", "cwddh", "progressRunStatus"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(finalSubProjectList, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }

    /**
     * 设置或者取消项目的进度追赶
     *
     * @param requestObject
     * @return
     */
    public String changeProgressRunStatus(JSONObject requestObject) {
        String projectIdStr = requestObject.getString("projectIdStr");
        if (StringUtils.isBlank(projectIdStr)) {
            logger.error("projectIdStr is blank");
            return "操作失败！";
        }
        String action = requestObject.getString("action");
        String scoreGetTime = "";
        if ("1".equals(action)) {
            scoreGetTime = requestObject.getString("scoreGetTime");
            if (StringUtils.isBlank(scoreGetTime)) {
                logger.error("scoreGetTime is blank");
                return "操作失败！";
            }
            if (!"actualEndTime".equalsIgnoreCase(scoreGetTime) && !"planEndTime".equalsIgnoreCase(scoreGetTime)) {
                scoreGetTime = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(scoreGetTime), -8));
            }
        } else {
            scoreGetTime = null;
        }
        String[] projectIdArr = projectIdStr.split(",", -1);
        for (int i = 0; i < projectIdArr.length; i++) {
            Map<String, Object> params = new HashMap<>();
            params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            // 先改变project_base中的数据
            params.put("projectId", projectIdArr[i]);
            params.put("progressRunStatus", "1".equals(action) ? "1" : null);
            params.put("scoreGetTime", scoreGetTime);
            xcmgProjectOtherDao.updateProgressRunStatus(params);
            // 向记录表中记录日志
            params.put("id", IdUtil.getId());
            params.put("action", action);
            params.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            params.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            xcmgProjectOtherDao.insertProgressRunSetRecord(params);
        }
        return "操作成功！";
    }

    public JSONObject checkDeliveryProduct(String projectId) {
        JSONObject result = new JSONObject();
        result.put("result", "true");
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        params.put("projectId", projectId);
        params.put("checkDelivery", "yes");
        List<Map<String, Object>> queryResult = xcmgProjectOtherDao.queryPdmProjectFileInfos(params);
        if (queryResult == null || queryResult.isEmpty()) {
            return result;
        }else {
            result.put("result", "false");
            result.put("content",
                    "请点击产品补录填写PDM交付物适用产品");
            return result;
        }
    }

    /**
     * 检查当前阶段已上传的所有交付物，需要审批的是否已经审批完成
     *
     * @param projectId
     * @param stageId
     * @return
     */
    public JSONObject checkDeliveryApproval(String projectId, String stageId) {
        JSONObject result = new JSONObject();
        result.put("result", "true");
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        params.put("projectId", projectId);
        params.put("stageId", stageId);
        params.put("isFolder", "0");
        List<Map<String, Object>> queryResult = xcmgProjectDeliveryApprovalDao.queryDeliveryApproval(params);
        // 查询PDM中的文件
        List<Map<String, Object>> pdmFiles = pdmApiManager.getPdmProjectFiles(params);
        // 查询SDM中的文件(仿真设计版块与科技项目关联的仿真申请)
        List<Map<String, Object>> fzsjInfos = fileUploadManager.queryFzsjByProjectId(projectId, stageId);
        if ((queryResult == null || queryResult.isEmpty()) && (pdmFiles == null || pdmFiles.isEmpty())
                && (fzsjInfos == null || fzsjInfos.isEmpty())) {
            return result;
        }
        if (pdmFiles != null) {
            queryResult.addAll(pdmFiles);
        }
        if (fzsjInfos != null) {
            queryResult.addAll(fzsjInfos);
        }

        for (Map<String, Object> oneResult : queryResult) {
            boolean isPDMFile = false;
            if (oneResult.get("fileSource") != null && "pdm".equalsIgnoreCase(oneResult.get("fileSource").toString())) {
                isPDMFile = true;
            }
            boolean isSDMFile = false;
            if (oneResult.get("fileSource") != null && "sdm".equalsIgnoreCase(oneResult.get("fileSource").toString())) {
                isSDMFile = true;
            }
            // 来自pdm的文件，要求状态是“已发布”
            if (isPDMFile) {
                String pdmFileStatus = "";
                if (oneResult.get("approvalStatus") != null) {
                    pdmFileStatus = oneResult.get("approvalStatus").toString();
                }
                if (!pdmFileStatus.contains("发布") && !pdmFileStatus.contains("已发")) {
                    result.put("result", "false");
                    result.put("content",
                            "Windchill PDM文件“" + oneResult.get("fileName").toString() + "”，状态不是“已发布”，请在PDM审批完成后重试！");
                    return result;
                }
            } else if (isSDMFile) {
                String sdmFileStatus = null;
                if (oneResult.get("approvalStatus") != null) {
                    sdmFileStatus = oneResult.get("approvalStatus").toString();
                }
                if (!ConstantUtil.SDM_APPROVAL_FINAL.equalsIgnoreCase(sdmFileStatus)) {
                    result.put("result", "false");
                    result.put("content", "仿真申请书“" + oneResult.get("fileName").toString() + "”，状态不是“审批完成”，请审批完成后重试！");
                    return result;
                }
            } else {
                String approvalSolutionId = null;
                if (oneResult.get("approvalSolutionId") != null) {
                    approvalSolutionId = oneResult.get("approvalSolutionId").toString();
                }
                // RDM中的文件且需要审批
                if (StringUtils.isNotBlank(approvalSolutionId) && !"pdmApprove".equalsIgnoreCase(approvalSolutionId)) {
                    if (oneResult.get("instStatus") == null) {
                        result.put("result", "false");
                        result.put("content",
                                "文件‘" + oneResult.get("fileName").toString() + "’没有提交审批，请在‘文件归档’处点击‘创建交付物审批单’");
                        return result;
                    }
                    if (oneResult.get("instStatus") != null
                            && !"SUCCESS_END".equalsIgnoreCase(oneResult.get("instStatus").toString())) {
                        result.put("result", "false");
                        result.put("content", "文件‘" + oneResult.get("fileName").toString() + "’尚未审批完成（或需作废后删除），单号‘"
                                + oneResult.get("fileapplyId").toString() + "’");
                        return result;
                    }
                }
            }
        }
        return result;
    }

    /**
     * 后台启动交付物审批流程
     *
     * @param request
     * @param response
     * @return
     */
    public JSONObject startDeliveryApproval(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        String projectId = RequestUtil.getString(request, "projectId");
        String stageId = RequestUtil.getString(request, "stageId");
        String mainDepId = RequestUtil.getString(request, "mainDepId");
        String mainDeptName = RequestUtil.getString(request, "mainDeptName");
        String currentUserProjectRoleName = RequestUtil.getString(request, "currentUserProjectRoleName", "");

        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        params.put("projectId", projectId);
        params.put("stageId", stageId);
        params.put("isFolder", "0");
        if (!"项目负责人".equalsIgnoreCase(currentUserProjectRoleName)) {
            params.put("creatorId", ContextUtil.getCurrentUserId());
        }
        List<Map<String, Object>> queryResult = xcmgProjectDeliveryApprovalDao.queryDeliveryApproval(params);
        if (queryResult == null || queryResult.isEmpty()) {
            result.put("message", "当前阶段没有交付物或无权限操作他人上传的交付物！");
            return result;
        }
        // 对于需要在RDM中审批的文件，按照流程方案进行归类
        Map<String, Set<String>> solId2FileIds = new HashMap<>();
        for (Map<String, Object> oneResult : queryResult) {
            boolean isPDMFile = false;
            if (oneResult.get("isPDMFile") != null && "1".equalsIgnoreCase(oneResult.get("isPDMFile").toString())) {
                isPDMFile = true;
            }
            if (!isPDMFile) {
                String approvalSolutionId = null;
                if (oneResult.get("approvalSolutionId") != null) {
                    approvalSolutionId = oneResult.get("approvalSolutionId").toString();
                }
                if (StringUtils.isNotBlank(approvalSolutionId) && !"pdmApprove".equalsIgnoreCase(approvalSolutionId)) {
                    // RDM中的文件、需要审批、当前没有提交
                    if (oneResult.get("fileapplyId") == null) {
                        if (!solId2FileIds.containsKey(approvalSolutionId)) {
                            solId2FileIds.put(approvalSolutionId, new HashSet<>());
                        }
                        solId2FileIds.get(approvalSolutionId).add(oneResult.get("id").toString());
                    }
                }
            }
        }
        if (solId2FileIds.isEmpty()) {
            result.put("message", "当前阶段没有需要在RDM中审批的交付物");
            return result;
        }
        // 组装流程实例启动参数
        for (String solId : solId2FileIds.keySet()) {
            Set<String> fileIds = solId2FileIds.get(solId);
            List<String> fileIdList = new ArrayList<>(fileIds);
            String fileIdStr = StringUtils.join(fileIds.toArray(), ",");
            JSONObject postJSON = new JSONObject();
            postJSON.put("projectId", projectId);
            postJSON.put("stageId", stageId);
            postJSON.put("fileIds", fileIdStr);
            postJSON.put("mainDepId", mainDepId);
            postJSON.put("mainDeptName", mainDeptName);

            request.setAttribute("solId", solId);
            request.setAttribute("jsonData", postJSON.toJSONString());
            try {
                JsonResult jsonResult = bpmInstManager.startProcessForRdm(request, response);
                if (!jsonResult.getSuccess()) {
                    // 根据某个交付物id查询所在的申请单号（对于审批节点为空的情况，流程启动有问题）
                    String fileApplyId = "";
                    params.put("fileId", fileIdList.get(0));
                    List<Map<String, Object>> queryOneFileResult =
                            xcmgProjectDeliveryApprovalDao.queryDeliveryApproval(params);
                    if (queryOneFileResult != null && !queryOneFileResult.isEmpty()
                            && queryOneFileResult.get(0).get("fileapplyId") != null) {
                        fileApplyId = queryOneFileResult.get(0).get("fileapplyId").toString();
                    }
                    if (StringUtils.isNotBlank(fileApplyId)) {
                        fileApplyId = "<br/>流程单号:" + fileApplyId;
                    }
                    result.put("message", jsonResult.getMessage() + jsonResult.getData() + fileApplyId);
                    return result;
                }
            } catch (Exception e) {
                logger.error("Exception in startDeliveryApproval,solId is " + solId, e);
                result.put("message", "流程启动失败，请联系管理员处理");
                return result;
            }
        }
        result.put("message", "流程启动成功，请在‘申请流程管理’--‘项目交付物审批流程’页面跟进审批进度");
        return result;
    }

    // 获取已经走过的阶段
    public List<Map<String, String>> getPassStage(String categoryId, String stageId) {
        JSONObject result = new JSONObject();
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        params.put("categoryId", categoryId);
        if (StringUtils.isNotBlank(stageId)) {
            params.put("stageId", stageId);
        }
        List<Map<String, String>> stageInfos = xcmgProjectOtherDao.queryStage(params);
        return stageInfos;
    }

    // 更换项目负责人的待办任务
    public void changeRespTask(String projectId, String originalRespManId, String currentRespManId) {
        Map<String, Object> param = new HashMap<>();
        param.put("projectId", projectId);
        param.put("originalRespManId", originalRespManId);
        param.put("currentRespManId", currentRespManId);
        xcmgProjectOtherDao.updateActTaskRespman(param);
        xcmgProjectOtherDao.updateActIdentityLinkRespman(param);
    }

    // 判断用户是否是项目成员，返回角色名称
    public JSONObject judgeUserIsMem(String userId, String projectId) {
        JSONObject result = new JSONObject();
        result.put("result", false);
        Map<String, Object> queryUserIdOfProject = new HashMap<>();
        queryUserIdOfProject.put("USER_ID_", userId);
        List<String> projectIds = new ArrayList<>();
        projectIds.add(projectId);
        queryUserIdOfProject.put("PROJECT_ID_", projectIds);
        List<Map<String, Object>> userProjectIdList = xcmgProjectOtherDao.getUserIdOfProject(queryUserIdOfProject);
        if (userProjectIdList != null && !userProjectIdList.isEmpty()) {
            result.put("result", true);
            result.put("roleName", userProjectIdList.get(0).get("roleName").toString());
        }
        return result;
    }

    // 替换成员与项目的关系，替换项目中的积分
    public JSONObject replaceMember(String projectId, String originalUserId, String currentUserId, String memberId) {
        JSONObject result = new JSONObject();
        result.put("result", true);
        if (StringUtils.isBlank(projectId) || StringUtils.isBlank(originalUserId) || StringUtils.isBlank(currentUserId)
                || StringUtils.isBlank(memberId)) {
            result.put("result", false);
            result.put("message", "参数为空");
            return result;
        }
        // 替换成员与项目的关系，替换项目中的积分
        Map<String, Object> params = new HashMap<>();
        params.put("currentUserId", currentUserId);
        params.put("memberId", memberId);
        xcmgProjectChangeDao.updateProjectMember(params);
        params.put("projectId", projectId);
        params.put("originalUserId", originalUserId);
        xcmgProjectScoreDao.updateScoreUserId(params);
        return result;
    }

    public List<JSONObject> queryMembersByPid(String projectId) {
        // 成员
        List<JSONObject> xcmgProjectMembers = xcmgProjectMemberDao.getMemberByProjectId(projectId);
        if (xcmgProjectMembers == null || xcmgProjectMembers.isEmpty()) {
            return Collections.emptyList();
        }
        // 查询该项目各个成员在之前阶段已经获得的分数总和
        Map<String, Object> params1 = new HashMap<>();
        params1.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        params1.put("projectId", projectId);
        List<Map<String, Object>> memberHasGetScores = xcmgProjectOtherDao.queryMemberScoreByPid(params1);
        Map<String, Double> userId2Score = new HashMap<>();
        for (Map<String, Object> oneMap : memberHasGetScores) {
            userId2Score.put(oneMap.get("userId").toString(), (Double) oneMap.get("score"));
        }
        for (JSONObject xcmgProjectMember : xcmgProjectMembers) {
            Map<String, Object> result = new HashMap<>();
            getUserInfoById(xcmgProjectMember.getString("userId"), result);
            xcmgProjectMember.put("userJob", result.get("GW").toString());
            xcmgProjectMember.put("gwno", result.get("gwno").toString());
            if (userId2Score.containsKey(xcmgProjectMember.getString("userId"))) {
                xcmgProjectMember.put("score", userId2Score.get(xcmgProjectMember.getString("userId")));
            }
        }
        return xcmgProjectMembers;
    }

    // 查询用户负责的项目延期超过5天的数量
    public int respProjectDelay5(String userId) {
        Map<String, Object> param = new HashMap<>(16);
        param.put("userId", userId);
        param.put("roleName", "项目负责人");
        // 负责的运行中项目、projectList查询不包含planEndTime
        List<Map<String, Object>> projectList = xcmgProjectReportDao.queryPersonProjectList(param);
        rdmZhglUtil.setTaskCreateTimeInfo(projectList, false);
        if (projectList == null || projectList.isEmpty()) {
            return 0;
        }
        int delayMore5Number = 0;
        for (Map<String, Object> oneProject : projectList) {
            try {
                xcmgProjectReportManager.queryProjectRisk(oneProject, "");
                // 统计延误超过5天的项目数
                if (oneProject.containsKey("hasRisk")) {
                    if ((int) oneProject.get("hasRisk") == 2 || (int) oneProject.get("hasRisk") == 4) {
                        delayMore5Number++;
                    }
                }
            } catch (Exception e) {
                logger.error("Exception in respProjectDelay5", e);
            }
        }

        return delayMore5Number;
    }

    // 查询项目输入
    public List<JSONObject> queryInputList(String projectId) {
        if (StringUtils.isBlank(projectId)) {
            return Collections.emptyList();
        }

        return xcmgProjectOtherDao.queryInputList(projectId);
    }

    public JsonResult saveInputEdit(String requestBody) {
        JsonResult result = new JsonResult(true);
        if (StringUtils.isBlank(requestBody)) {
            return result;
        }
        try {
            JSONObject requestObj = JSONObject.parseObject(requestBody);
            String id = requestObj.getString("id");
            if (StringUtils.isBlank(id)) {
                requestObj.put("id", IdUtil.getId());
                requestObj.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                requestObj.put("CREATE_TIME_", new Date());
                // 根据类型，塞入不同的跳转url
                String inputUrl =
                        toGetInputUrlByType(requestObj.getString("inputType"), requestObj.getString("referId"));
                requestObj.put("inputUrl", inputUrl);
                xcmgProjectOtherDao.insertInput(requestObj);
            } else {
                requestObj.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                requestObj.put("UPDATE_TIME_", new Date());
                // 根据类型，塞入不同的跳转url
                String inputUrl =
                        toGetInputUrlByType(requestObj.getString("inputType"), requestObj.getString("referId"));
                requestObj.put("inputUrl", inputUrl);
                xcmgProjectOtherDao.updateInput(requestObj);
            }
        } catch (Exception e) {
            logger.error("Exception in saveInputEdit", e);
            result.setSuccess(false);
            result.setMessage("保存失败！");
        }
        return result;
    }

    private String toGetInputUrlByType(String inputType, String referId) {
        if (StringUtils.isBlank(referId)) {
            return "";
        }
        if ("标准".equalsIgnoreCase(inputType)) {
            // 查询是管理标准还是技术标准
            Map<String, Object> param = new HashMap<>();
            param.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            param.put("id", referId);
            Map<String, Object> oneInfo = standardDao.queryStandard(param);
            if (oneInfo == null) {
                return "";
            }
            return "/standardManager/core/standard/edit.do?standardId=" + referId + "&action=detail&systemCategoryId="
                    + oneInfo.get("systemCategoryId");
        }
        if ("专利".equalsIgnoreCase(inputType)) {
            return "/zhgl/core/zlgl/zgzlPage.do?zgzlId=" + referId + "&action=detail";
        }
        if ("情报报告".equalsIgnoreCase(inputType)) {
            return "/Info/Qbgz/editPage.do?action=detail&qbgzId=" + referId;
        }
        return "";
    }

    public JsonResult deleteInput(String[] idArr) {
        JSONObject param = new JSONObject();
        param.put("ids", Arrays.asList(idArr));
        xcmgProjectOtherDao.deleteInput(param);
        return new JsonResult(true, "成功删除!");
    }

    // ..todo:增加成果查询的类型参数(老李mark暂留)
    public List<JSONObject> queryOutList(String outPlanId, String typeName) {
        if (StringUtils.isBlank(outPlanId)) {
            return Collections.emptyList();
        }
        Map<String, Object> param = new HashMap<>();
        param.put("outPlanId", outPlanId);
        // ..
        List<JSONObject> resultList = xcmgProjectOtherDao.queryOutList(param);// 成果原始记录
        Map<String, JSONObject> outReferMap = new HashedMap();// 原始记录对应的实际业务记录map
        if (!resultList.isEmpty()) {
            param.clear();
            param.put("outList", resultList);
            if (typeName.contains("软件著作权")) {
                List<Map<String, Object>> outReferList = rjzzDao.queryRjzzList(param);// 原始记录对应的实际业务记录
                for (Map<String, Object> map : outReferList) {// 填充原始记录对应的实际业务记录map
                    outReferMap.put(map.get("rjzzId").toString(), new JSONObject(map));
                }
                for (JSONObject result : resultList) {// 原始记录对应的实际业务记录map增加成果原始记录里的特殊字段
                    JSONObject temp = outReferMap.get(result.getString("outReferId"));
                    if (temp != null) {
                        result.put("zpsm", temp.getString("zpsm"));
                        result.put("fbzt", temp.getString("fbzt"));
                    }
                }
            } else if (typeName.contains("标准")) {
                List<Map<String, Object>> outReferList = standardDao.queryStandardList(param);// 原始记录对应的实际业务记录
                for (Map<String, Object> map : outReferList) {// 填充原始记录对应的实际业务记录map
                    outReferMap.put(map.get("id").toString(), new JSONObject(map));
                }
                for (JSONObject result : resultList) {// 原始记录对应的实际业务记录map增加成果原始记录里的特殊字段
                    JSONObject temp = outReferMap.get(result.getString("outReferId"));
                    if (temp != null) {
                        result.put("categoryName", temp.getString("categoryName"));
                        result.put("standardStatus", temp.getString("standardStatus"));
                    }
                }
            } else if (typeName.contains("国内发明专利") || typeName.contains("国内实用新型专利") || typeName.contains("国内外观专利")) {
                List<JSONObject> outReferList = zlglmkDao.queryZgzlList(param);// 原始记录对应的实际业务记录
                for (JSONObject json : outReferList) {// 填充原始记录对应的实际业务记录map
                    outReferMap.put(json.getString("zgzlId"), json);
                }
                for (JSONObject result : resultList) {// 原始记录对应的实际业务记录map增加成果原始记录里的特殊字段
                    JSONObject temp = outReferMap.get(result.getString("outReferId"));
                    if (temp != null) {
                        result.put("zllxName", temp.getString("zllxName"));
                        result.put("gnztName", temp.getString("gnztName"));
                    }
                }
            } else if (typeName.contains("FMEA")) {
                List<String> referIdList = new ArrayList<>();
                for (int i = 0; i < resultList.size(); i++) {
                    referIdList.add(resultList.get(i).getString("outReferId"));
                }
                HashMap<String, Object> params = new HashMap<>();
                params.put("referIdList", referIdList);
                List<Map<String, Object>> outReferList = drbfmTotalDao.queryTotalList(params);
                for (Map<String, Object> map : outReferList) {
                    outReferMap.put(map.get("id").toString(), new JSONObject(map));
                }
                for (JSONObject result : resultList) {
                    JSONObject temp = outReferMap.get(result.getString("outReferId"));
                    if (temp != null) {
                        result.put("femaType", temp.getString("femaType"));
                    }
                }
            }

        }
        return resultList;
    }

    public void saveOutList(JsonResult result, String changeGridDataStr) {
        try {
            JSONObject changeJsonObj = JSONObject.parseObject(changeGridDataStr);
            JSONArray changeGridDataJson = changeJsonObj.getJSONArray("changeDataArr");
            String typeName = changeJsonObj.getString("typeName");
            if (changeGridDataJson == null || changeGridDataJson.isEmpty()) {
                logger.warn("gridData is blank");
                result.setSuccess(false);
                result.setMessage("请求参数为空");
                return;
            }
            for (int i = 0; i < changeGridDataJson.size(); i++) {
                JSONObject oneObject = changeGridDataJson.getJSONObject(i);
                String state = oneObject.getString("_state");
                String id = oneObject.getString("id");
                if ("added".equals(state) || StringUtils.isBlank(id)) {
                    // 对于专利和论文，判断数据库中是否已存在成果的关联，并且不是自己
                    boolean judgeResult = findExistOutLink(typeName, oneObject, result);
                    if (!judgeResult) {
                        // 新增
                        oneObject.put("id", IdUtil.getId());
                        oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                        oneObject.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                        // 根据类型，塞入不同的跳转url
                        String outUrl = toGetOutUrlByType(typeName, oneObject.getString("outReferId"));
                        oneObject.put("outUrl", outUrl);
                        xcmgProjectOtherDao.insertOut(oneObject);
                    }
                } else if ("modified".equals(state)) {
                    // 对于专利和论文，判断数据库中是否已存在成果的关联，并且不是自己
                    boolean judgeResult = findExistOutLink(typeName, oneObject, result);
                    if (!judgeResult) {
                        // 修改
                        oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                        oneObject.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                        // 根据类型，塞入不同的跳转url
                        String outUrl = toGetOutUrlByType(typeName, oneObject.getString("outReferId"));
                        oneObject.put("outUrl", outUrl);
                        xcmgProjectOtherDao.updateOut(oneObject);
                    }
                } else if ("removed".equals(state)) {
                    // 删除
                    JSONObject param = new JSONObject();
                    List<String> ids = new ArrayList<>();
                    ids.add(oneObject.getString("id"));
                    param.put("ids", ids);
                    xcmgProjectOtherDao.deleteOut(param);
                }
            }
        } catch (Exception e) {
            logger.error("Exception in saveOutList", e);
            result.setSuccess(false);
            result.setMessage("系统异常！");
        }
    }

    // 对于专利和论文，判断数据库中是否已存在成果的关联，并且不是自己；找到则在result的message中写入提示信息,返回true
    // 再增标准类型
    private boolean findExistOutLink(String typeName, JSONObject oneObject, JsonResult result) {
        if (StringUtils.isBlank(typeName) || (!typeName.contains("专利") && !typeName.contains("技术封装")
                && !typeName.contains("论文") && !typeName.contains("期刊") && !typeName.contains("标准"))) {
            return false;
        }
        List<JSONObject> existObjs = xcmgProjectOtherDao.queryExistOutByReferId(oneObject);
        if (existObjs != null && !existObjs.isEmpty()) {
            result.setSuccess(false);
            String message = result.getMessage();
            if (StringUtils.isNotBlank(message)) {
                message += "；";
            }
            message += "成果链接“" + existObjs.get(0).getString("outName") + "”在项目“"
                    + existObjs.get(0).getString("projectName") + "”中已存在！";
            result.setMessage(message);
            return true;
        }
        return false;
    }

    private String toGetOutUrlByType(String outTypeName, String outReferId) {
        if (StringUtils.isBlank(outReferId) || StringUtils.isBlank(outTypeName)) {
            return "";
        }
        if (outTypeName.contains("国内发明专利") || outTypeName.contains("国内实用新型专利") || outTypeName.contains("国内外观专利")) {
            return "/zhgl/core/zlgl/zgzlPage.do?zgzlId=" + outReferId + "&action=detail";
        } else if (outTypeName.contains("专利")) {
            return "/zhgl/core/zlgl/gjzlPage.do?gjzlId=" + outReferId + "&action=detail";
        }
        if (outTypeName.contains("标准")) {
            // 查询是管理标准还是技术标准
            Map<String, Object> param = new HashMap<>();
            param.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            param.put("id", outReferId);
            Map<String, Object> oneInfo = standardDao.queryStandard(param);
            if (oneInfo == null) {
                return "";
            }
            return "/standardManager/core/standard/edit.do?standardId=" + outReferId
                    + "&action=detail&systemCategoryId=" + oneInfo.get("systemCategoryId");
        }
        if (outTypeName.contains("软件著作权")) {
            return "/zhgl/core/rjzz/editPage.do?rjzzId=" + outReferId + "&action=detail";
        }
        if (outTypeName.contains("论文") || outTypeName.contains("期刊")) {
            return "/zhgl/core/kjlw/editPage.do?kjlwId=" + outReferId + "&action=detail";
        }
        if (outTypeName.contains("技术封装")) {
            return "/jssj/core/config/editPage.do?jssjkId=" + outReferId + "&action=detail";
        }
        if (outTypeName.contains("FMEA")) {
            return "/rdm/core/noFlowFormIframe?url=%2Frdm%2Fdrbfm%2Ftotal%2FtotalDecomposePage.do%3Faction%3Ddetail%26id%3D"+outReferId+"%26flag%3Dundefined";
        }
        return "";
    }

    public List<JSONObject> getProductList(HttpServletRequest request) {
        List<JSONObject> list = new ArrayList<>();
        try {
            JSONObject paramJson = new JSONObject();
            list = xcmgProjectOtherDao.getProductList(paramJson);
            for (JSONObject oneProduct : list) {
                if ("3".equalsIgnoreCase(oneProduct.getString("important"))
                        && StringUtils.isNotBlank(oneProduct.getString("taskFrom"))
                        && !"/".equalsIgnoreCase(oneProduct.getString("taskFrom"))
                        && !"自主申报".equalsIgnoreCase(oneProduct.getString("taskFrom"))
                ) {
                    oneProduct.put("productName", oneProduct.getString("productName") + "(重点产品)");
                }
            }
        } catch (Exception e) {
            logger.error("获取产品信息", e);
        }
        return list;
    }

    public List<JSONObject> getDeliveryData(HttpServletRequest request) {
        List<JSONObject> list = new ArrayList<>();
        List<JSONObject> lastList = new ArrayList<>();
        try {
            String projectId = RequestUtil.getString(request, "projectId", "");
            String userId = RequestUtil.getString(request, "userId", "");
            JSONObject projectObj = xcmgProjectDao.queryProjectById(projectId);
            Map<String, Object> params = new HashMap<>();
            params.put("projectId", projectId);
            params.put("userId", userId);
            params.put("projectSourceId", projectObj.getString("sourceId"));
            params.put("projectLevelId", projectObj.getString("levelId"));
            params.put("projectCategoryId", projectObj.getString("categoryId"));
            list = xcmgProjectOtherDao.getProjectDelivery(params);
            // 当前用户是否已保存负责的交付物
            JSONObject param = new JSONObject();
            param.put("projectId", projectId);
            param.put("userId", userId);
            JSONObject jsonObject = xcmgProjectOtherDao.getExistProjectDelivery(param);
            boolean hasSave = false;
            if (jsonObject != null && StringUtils.isNotBlank(jsonObject.getString("deliveryIds"))) {
                hasSave = true;
            }
            Map<String, Object> genMap = new HashMap<>();
            if (!hasSave) {
                // 新品试制中，所在部门应该负责的交付物
                String deptId = commonInfoDao.getDeptIdByUserId(userId);
                List<JSONObject> resList = xcmgProjectOtherDao.getDeptDeliveryIds(deptId);
                genMap = genMap(resList);
            }
            boolean nodelivery = true;
            for (JSONObject temp : list) {
                if (StringUtil.isEmpty(temp.getString("userId"))) {
                    temp.put("productIds", projectObj.getString("productIds"));
                    temp.put("productNames", projectObj.getString("productNames"));
                    if (genMap.get(temp.get("deliveryId")) != null) {
                        temp.put("selected", true);
                    } else {
                        temp.put("selected", false);
                    }
                } else {
                    // if(StringUtil.isEmpty(temp.getString("productIds"))){
                    // temp.put("productIds", projectObj.getProductIds());
                    // temp.put("productNames", projectObj.getProductNames());
                    // }
                    temp.put("selected", true);
                }
            }
            String[] productIdStrings = projectObj.getString("productIds").split(",");
            Map<String, Object> idparam = new HashMap<>();
            idparam.put("productIds", Arrays.asList(productIdStrings));
            List<JSONObject> xpszPlanTime = xcmgProjectDao.queryXpszInfo(idparam);
                Map<String, List<JSONObject>> productIdToJson =
                        xpszPlanTime.stream().collect(Collectors.groupingBy(j -> j.getString("productId")));
            for (JSONObject object : list) {
                String[] productIds = object.getString("productIds").split(",");
                String[] productNames = object.getString("productNames").split(",");
                for(int i=0;i<productIds.length;i++){
                    JSONObject newJson = JSONObject.parseObject(object.toString());
                    if(productIdToJson!=null&&!productIdToJson.isEmpty()&&productIdToJson.containsKey(productIds[i])){
                        JSONObject planTimeJson = productIdToJson.get(productIds[i]).get(0);
                        newJson.put("planTime",planTimeJson.getString(object.getString("itemCode")));
                    }
                    newJson.put("productIds",productIds[i]);
                    newJson.put("productNames",productNames[i]);
                    lastList.add(newJson);
                }
            }
        } catch (Exception e) {
            logger.error("getDeliveryData", e);
        }

        return lastList;
    }

    public Map<String, Object> genMap(List<JSONObject> deliveryList) {
        Map<String, Object> resultMap = new HashMap<>(16);
        for (JSONObject json : deliveryList) {
            resultMap.put(json.getString("deliveryId"), json.get("deliveryName"));
        }
        return resultMap;
    }

    public List<JSONObject> getProjectProductList(HttpServletRequest request) {
        List<JSONObject> list = new ArrayList<>();
        try {
            String projectId = RequestUtil.getString(request, "projectId", "");
            if (StringUtils.isBlank(projectId)) {
                return list;
            }
            JSONObject projectObj = xcmgProjectDao.queryProjectById(projectId);
            String productIds = projectObj.getString("productIds");
            String productNames = projectObj.getString("productNames");
            if (StringUtils.isNotBlank(productIds) && StringUtils.isNotBlank(productNames)) {
                String productIdArry[] = productIds.split(",");
                String productNameArry[] = productNames.split(",");
                JSONObject tempJson;
                for (int i = 0; i < productIdArry.length; i++) {
                    tempJson = new JSONObject();
                    tempJson.put("productId", productIdArry[i]);
                    tempJson.put("productName", productNameArry[i]);
                    list.add(tempJson);
                }
            }

        } catch (Exception e) {
            logger.error("获取项目产品信息", e);
        }
        return list;
    }

    public JSONObject saveProjectDeliveryProduct(String changeGridDataStr, HttpServletRequest request) {
        try {
            JSONArray changeGridDataJson = JSONObject.parseArray(changeGridDataStr);
            List<JSONObject> changeGridDataJsonObject = JSONArray.parseArray(changeGridDataStr,JSONObject.class);
            Map<String, List<JSONObject>> deliveryIdToList =
                    changeGridDataJsonObject.stream().collect(Collectors.groupingBy(j -> j.getString("deliveryId")));
            String projectId = request.getParameter("projectId");
            String userId = request.getParameter("userId");
            // 先删除保存的关系
            JSONObject paramJson = new JSONObject();
            paramJson.put("projectId", projectId);
            paramJson.put("userId", userId);
            xcmgProjectDeliveryProductDao.delDeliveryProduct(paramJson);

            String deliveryIds = "";
            String deliveryNames = "";
            for (Map.Entry<String, List<JSONObject>> entry : deliveryIdToList.entrySet()) {
                Set<String> productIdSet = new HashSet<>();
                Set<String> productNameSet = new HashSet<>();
                String deliveryId = entry.getKey();
                List<JSONObject> productList = entry.getValue();
                for (JSONObject oneProduct : productList) {
                    productIdSet.add(oneProduct.getString("productIds"));
                    productNameSet.add(oneProduct.getString("productNames"));
                }
                String deliveryName =productList.get(0).getString("deliveryName");
                JSONObject oneObject = new JSONObject();
                oneObject.put("id", IdUtil.getId());
                oneObject.put("projectId", projectId);
                oneObject.put("userId", userId);
                oneObject.put("deliveryId", deliveryId);
                oneObject.put("productIds", StringUtils.join(productIdSet.toArray(),","));
                oneObject.put("productNames", StringUtils.join(productNameSet.toArray(),","));
                xcmgProjectDeliveryProductDao.addDeliveryProduct(oneObject);
                deliveryIds += deliveryId + ",";
                deliveryNames += deliveryName + ",";
            }


//            for (int i = 0; i < changeGridDataJson.size(); i++) {
//                JSONObject oneObject = changeGridDataJson.getJSONObject(i);
//                oneObject.put("id", IdUtil.getId());
//                oneObject.put("projectId", projectId);
//                oneObject.put("userId", userId);
//                deliveryIds += oneObject.getString("deliveryId") + ",";
//                deliveryNames += oneObject.getString("deliveryName") + ",";
//                xcmgProjectDeliveryProductDao.addDeliveryProduct(oneObject);
//            }
            // 项目成员表
            if (deliveryIds.length() > 0) {
                deliveryIds = deliveryIds.substring(0, deliveryIds.length() - 1);
                deliveryNames = deliveryNames.substring(0, deliveryNames.length() - 1);
                paramJson.put("respDeliveryIds", deliveryIds);
                paramJson.put("respDeliveryNames", deliveryNames);
                xcmgProjectDeliveryProductDao.updateDeliveryInfo(paramJson);
            } else {
                paramJson.put("respDeliveryIds", deliveryIds);
                paramJson.put("respDeliveryNames", deliveryNames);
                xcmgProjectDeliveryProductDao.updateDeliveryInfo(paramJson);
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("保存信息异常", e);
            return ResultUtil.result(false, "保存信息异常！", null);
        }
        return ResultUtil.result(true, "保存成功！", null);
    }

    // 检查(产品研发类)当前是否有缺失的必选交付物或者指定交付物类型的交付物
    public JSONObject checkProductStageDelivery(String projectId, String stageId, String levelId, String sourceId,
                                                String pointDeliveryName, String categoryName) {
        JSONObject result = new JSONObject();
        result.put("result", "true");
        if (StringUtils.isBlank(projectId) || StringUtils.isBlank(stageId) || StringUtils.isBlank(levelId)
                || StringUtils.isBlank(sourceId)) {
            logger.error("参数缺失");
            result.put("result", "false");
            result.put("content", "参数缺失，检查失败！");
            return result;
        }
        JSONObject xcmgProject = getXcmgProject(projectId);
        String productIds = xcmgProject.getString("productIds");
        String[] productIdArray = productIds.split(",");
        String productNames = xcmgProject.getString("productNames");
        String[] productNameArray = productNames.split(",");
        List<Map<String, Object>> shouldUploadDeliverys = new ArrayList<>();
        Set<String> uploadFileDeliveryIds = new HashSet<>();
        Map<String, Object> params = new HashMap<>();
        StringBuilder missDeliveryNames = new StringBuilder();
        for (int i = 0; i < productIdArray.length; i++) {
            String productId = productIdArray[i];
            String productName = productNameArray[i];
            // 查询应该上传的交付物
            shouldUploadDeliverys = getDeliveryId2Name(stageId, levelId, sourceId, null, null);
            getOtherDelivery(shouldUploadDeliverys, xcmgProject, stageId);
            if (shouldUploadDeliverys == null || shouldUploadDeliverys.isEmpty()) {
                return result;
            }
            // 查询RDM中该项目当前阶段已经上传的交付物
            uploadFileDeliveryIds = new HashSet<>();
            params = new HashMap<>();
            params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            params.put("projectId", projectId);
            params.put("pid", stageId);
            params.put("stageId", stageId);
            params.put("isFolder", "0");
            params.put("productId", productId);
            List<Map<String, Object>> uploadFiles = xcmgProjectFileUploadManager.queryProjectFileInfos(params);
            if (uploadFiles != null && !uploadFiles.isEmpty()) {
                for (Map<String, Object> oneFile : uploadFiles) {
                    if (oneFile.get("deliveryId") != null) {
                        uploadFileDeliveryIds.add(oneFile.get("deliveryId").toString());
                    }
                }
            }
            // 请求PDM中关联的这个项目这个阶段的交付物
            List<Map<String, Object>> pdmFiles = pdmApiManager.getPdmProjectFiles(params);
            if (pdmFiles != null && !pdmFiles.isEmpty()) {
                for (Map<String, Object> onePdm : pdmFiles) {
                    String deliveryId = StringProcessUtil.toGetStringNotNull(onePdm.get("deliveryId"));
                    String pdmProductName =
                            productService.getProductName(StringProcessUtil.toGetStringNotNull(onePdm.get("filePath")));
                    if (!StringUtil.isEmpty(productName)) {
                        if (productName.indexOf(pdmProductName) > -1) {
                            if (StringUtils.isNotBlank(deliveryId)) {
                                uploadFileDeliveryIds.add(deliveryId);
                            }
                        }
                    }
                }
            }
            // 查询SDM中（当前在RDM的仿真设计模块）关联的这个项目的流程有没有
            // 查询“仿真申请书”的交付物类型信息
            Map<String, Object> param = new HashMap<>();
            param.put("projectId", projectId);
            param.put("deliveryName", RdmConst.PROJECT_DELIVERY_FZSQS);
            List<JSONObject> deliveryInfos = xcmgProjectConfigDao.queryDeliveryByNameAndProjectId(param);
            if (deliveryInfos != null && !deliveryInfos.isEmpty()) {
                String deliveryId = deliveryInfos.get(0).getString("deliveryId");
                List<JSONObject> fzsjList = fzsjDao.queryFzsjByProject(params);
                if (fzsjList != null && !fzsjList.isEmpty()) {
                    uploadFileDeliveryIds.add(deliveryId);
                }
            }
            // 遍历找到缺失的
            if (shouldUploadDeliverys != null && !shouldUploadDeliverys.isEmpty()) {
                for (Map<String, Object> oneDelivery : shouldUploadDeliverys) {
                    if (!uploadFileDeliveryIds.contains(oneDelivery.get("deliveryId").toString())) {
                        missDeliveryNames
                                .append("产品：" + productName + "——对应交付物：" + oneDelivery.get("deliveryName").toString())
                                .append("，</br>");
                    }
                }
            }
        }
        if (missDeliveryNames.length() > 0) {
            result.put("result", "false");
            result.put("content", "下列必传交付物缺失，请在页面下方‘文件归档’处上传或在对应系统（PDM/TDM/SDM）及相关流程中创建并审批完成！<br>缺失交付物："
                    + missDeliveryNames.substring(0, missDeliveryNames.length() - 1));
            return result;
        }

        return result;
    }

    public JSONObject getProjectUserDelivery(JSONObject postData) {
        String userName = "";
        try {
            String projectId = postData.getString("projectId");
            userName = xcmgProjectOtherDao.getUnFinishDelivery(projectId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("获取信息异常", e);
            return ResultUtil.result(false, "getProjectUserDelivery！", null);
        }
        return ResultUtil.result(true, "获取情况！", userName);
    }

    public JSONObject genProductUserDelivery(JSONObject param) {
        try {
            JSONObject paramJson = new JSONObject();
            paramJson.put("categoryId", ConstantUtil.PRODUCT_CATEGORY);
            paramJson.put("STATUS_", "RUNNING");
            paramJson.put("year", param.getString("year"));
            List<JSONObject> list = xcmgProjectOtherDao.getProductProjectDelivery(paramJson);
            String projectId, productIds, productNames, userId, respDeliveryIds;
            for (JSONObject temp : list) {
                projectId = temp.getString("projectId");
                productIds = temp.getString("productIds");
                productNames = temp.getString("productNames");
                userId = temp.getString("userId");
                respDeliveryIds = temp.getString("respDeliveryIds");
                // 拆分交付物
                String[] deliveryIdArray = respDeliveryIds.split(",");
                for (String deliveryId : deliveryIdArray) {
                    JSONObject oneObject = new JSONObject();
                    oneObject.put("id", IdUtil.getId());
                    oneObject.put("projectId", projectId);
                    oneObject.put("userId", userId);
                    oneObject.put("deliveryId", deliveryId);
                    oneObject.put("productIds", productIds);
                    oneObject.put("productNames", productNames);
                    xcmgProjectDeliveryProductDao.addDeliveryProduct(oneObject);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.result(false, "拆分失败！", null);
        }
        return ResultUtil.result(true, "拆分成功！", null);
    }

    public JSONObject editProduct(HttpServletRequest request) {
        JSONObject paramJson = new JSONObject();
        try {
            String fileId = request.getParameter("fileId");
            String approvalStatus = request.getParameter("approvalStatus");
            String productIds = request.getParameter("productIds");
            String productNames = request.getParameter("productNames");
            String finishDate = request.getParameter("finishDate");
            paramJson.put("fileId", fileId);
            paramJson.put("productIds", productIds);
            paramJson.put("productNames", productNames);
            xcmgProjectFileDao.updateFileProduct(paramJson);
            // 如果是 审批完成、或者无需审批的，则推送到新品试制模块
            if (("审批完成".equals(approvalStatus) || "无需审批".equals(approvalStatus)||approvalStatus.contains("发布") ||approvalStatus.contains("已发")) && StringUtil.isNotEmpty(productIds)) {
                JSONObject fileObj = xcmgProjectFileDao.getFileObj(fileId);
                if (fileObj != null) {
                    String deliveryId = fileObj.getString("deliveryId");
                    String[] productIdArry = productIds.split(",");
                    // 根据交付物id获取绑定的新品试制节点
                    List<JSONObject> stageList = productConfigDao.getStageList(deliveryId);
                    if (!stageList.isEmpty()) {
                        for (String productId : productIdArry) {
                            JSONObject newProductObj = productDao.getNewProductInfo(productId);
                            if (newProductObj != null) {
                                String mainId = newProductObj.getString("id");
                                paramJson = new JSONObject();
                                paramJson.put("mainId", mainId);
                                paramJson.put("itemType", "4");
                                int count = 0;
                                if (StringUtil.isEmpty(finishDate)) {
                                    finishDate = XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss");
                                }
                                for (JSONObject temp : stageList) {
                                    // 先判断阶段时间是否已经填写，已填写则不更新
                                    paramJson.put("stageName", temp.getString("itemCode"));
                                    String isFinishDate = productDao.getStageFinishDate(paramJson);
                                    if (isFinishDate == null) {
                                        paramJson.put(temp.getString("itemCode"), finishDate);
                                        count++;
                                    }
                                }
                                if (count > 0) {
                                    productDao.updateOrgDate(paramJson);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("补录产品信息异常", e);
            return ResultUtil.result(false, "editProduct！", null);
        }
        return ResultUtil.result(true, "补录成功！", null);
    }

    public List<JSONObject> queryStageEvaluateList(HttpServletRequest request) {
        String projectId = RequestUtil.getString(request, "projectId", "");
        String userId = RequestUtil.getString(request, "userId", "");
        if (StringUtils.isBlank(projectId) || StringUtils.isBlank(userId)) {
            return null;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("projectId", projectId);
        params.put("userId", userId);
        List<JSONObject> data = xcmgProjectOtherDao.queryStageEvaluateList(params);
        if (data != null && !data.isEmpty()) {
            for (JSONObject oneData : data) {
                oneData.put("CREATE_TIME_", DateUtil.formatDate((Date) oneData.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        return data;
    }

    public void saveStageEvaluate(JsonResult result, String postDataStr) {
        try {
            if (StringUtils.isBlank(postDataStr)) {
                logger.warn("postDataStr is blank");
                return;
            }
            JSONObject postDataObj = JSONObject.parseObject(postDataStr);
            JSONArray changeDataArr = postDataObj.getJSONArray("gridChange");
            String projectId = postDataObj.getString("projectIdVal");
            String userId = postDataObj.getString("userId");
            for (int i = 0; i < changeDataArr.size(); i++) {
                JSONObject oneObject = changeDataArr.getJSONObject(i);
                String state = oneObject.getString("_state");
                String id = oneObject.getString("id");
                if ("added".equals(state) || StringUtils.isBlank(id)) {
                    // 新增
                    oneObject.put("id", IdUtil.getId());
                    oneObject.put("projectId", projectId);
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                    oneObject.put("userId", userId);
                    xcmgProjectScoreDao.insertUserStageEvaluate(oneObject);
                } else if ("modified".equals(state)) {
                    // 修改
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                    xcmgProjectScoreDao.updateUserStageEvaluate(oneObject);
                } else if ("removed".equals(state)) {
                    // 删除
                    xcmgProjectScoreDao.delUserStageEvaluate(oneObject);
                }
            }
        } catch (Exception e) {
            logger.error("Exception in saveStageEvaluate", e);
        }
    }

    public void checkEvaluate(JsonResult result, String postDataStr) {
        try {
            if (StringUtils.isBlank(postDataStr)) {
                logger.warn("postDataStr is blank");
                result.setSuccess(false);
                result.setMessage("参数为空");
                return;
            }
            JSONObject postDataObj = JSONObject.parseObject(postDataStr);
            JSONArray userIds = postDataObj.getJSONArray("userIds");
            String projectId = postDataObj.getString("projectId");
            String stageId = postDataObj.getString("stageId");
            if (StringUtils.isBlank(projectId) || StringUtils.isBlank(stageId)) {
                result.setSuccess(false);
                result.setMessage("项目或阶段参数为空");
                return;
            }
            Map<String, Object> params = new HashMap<>();
            params.put("projectId", projectId);
            params.put("stageId", stageId);
            params.put("userIds", userIds);
            Set<String> existUserIds = new HashSet<>();
            List<JSONObject> data = xcmgProjectOtherDao.queryStageEvaluateList(params);
            if (data != null && !data.isEmpty()) {
                for (JSONObject oneData : data) {
                    existUserIds.add(oneData.getString("userId"));
                }
            }
            userIds.removeAll(existUserIds);
            if (!userIds.isEmpty()) {
                JSONObject param = new JSONObject();
                param.put("userIds", new ArrayList<>(userIds));
                List<JSONObject> userList = xcmgProjectOtherDao.queryUserByIds(param);
                StringBuilder stringBuilder = new StringBuilder();
                for (JSONObject oneUser : userList) {
                    stringBuilder.append(oneUser.getString("FULLNAME_") + "，");
                }
                result.setSuccess(false);
                result.setMessage("以下成员未进行阶段评价，请点击“项目成员信息”中的“阶段评价”列：" + stringBuilder);
            }
        } catch (Exception e) {
            logger.error("Exception in checkEvaluate", e);
            result.setSuccess(false);
            result.setMessage("系统异常");
        }
    }

    public List<JSONObject> queryMemberDeliveryAssign(HttpServletRequest request) {
        String projectId = RequestUtil.getString(request, "projectId", "");
        if (StringUtils.isBlank(projectId)) {
            return null;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("projectId", projectId);
        List<JSONObject> data = xcmgProjectOtherDao.queryValidMembersByDeliverys(params);
        return data;
    }
}
