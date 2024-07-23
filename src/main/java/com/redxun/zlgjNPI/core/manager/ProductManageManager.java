package com.redxun.zlgjNPI.core.manager;

import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.rdmCommon.core.util.RdmConst;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateUtil;
import com.redxun.materielextend.core.dao.MaterielApplyDao;
import com.redxun.rdmCommon.core.manager.LoginRecordManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import com.redxun.zlgjNPI.core.dao.ProductManageDao;

import groovy.util.logging.Slf4j;

@Service
@Slf4j
public class ProductManageManager {
    private static Logger logger = LoggerFactory.getLogger(ProductManageManager.class);
    @Resource
    private ProductManageDao productManageDao;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Resource
    private BpmInstManager bpmInstManager;
    @Autowired
    private MaterielApplyDao materielApplyDao;

    @Autowired
    private RdmZhglUtil rdmZhglUtil;

    @Autowired
    private LoginRecordManager loginRecordManager;

    public JsonPageResult<?> getProductManageList(HttpServletRequest request, HttpServletResponse response,
        boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        rdmZhglUtil.addOrder(request, params, "id", "desc");
        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    // 数据库中存储的时间是UTC时间，因此需要将前台传递的北京时间转化
                    if ("rdTimeStart".equalsIgnoreCase(name)) {
                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), -8));
                    }
                    if ("rdTimeEnd".equalsIgnoreCase(name)) {
                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), 16));
                    }
                    params.put(name, value);
                }
            }
        }

        // 增加分页条件
        if (doPage) {
            rdmZhglUtil.addPage(request, params);
        }

        List<Map<String, Object>> productManageList = productManageDao.queryProductManageList(params);

        for (Map<String, Object> productManage : productManageList) {
            if (productManage.get("jlTime") != null) {
                productManage.put("jlTime", DateUtil.formatDate((Date)productManage.get("jlTime"), "yyyy-MM-dd"));
            }

            if (productManage.get("CREATE_TIME_") != null) {
                productManage.put("CREATE_TIME_",
                    DateUtil.formatDate((Date)productManage.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }

        Iterator<Map<String, Object>> iterator = productManageList.iterator();
        while (iterator.hasNext()) {
            Map<String, Object> next = iterator.next();
            if ("DRAFTED".equals(next.get("instStatus")) && !productManageList.equals(next.get("CREATE_BY_"))) {
                iterator.remove();
            }
        }

        // 查询当前处理人
        xcmgProjectManager.setTaskCurrentUser(productManageList);

        productManageList = filterListByRole(productManageList);
        result.setData(productManageList);

        int countProductManageList = productManageDao.countProductManageLists(params);
        result.setTotal(countProductManageList);
        return result;
    }

    public JsonResult deleteProductManage(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> productManageIds = Arrays.asList(ids);

        Map<String, Object> param = new HashMap<>();
        param.put("productManageIds", productManageIds);
        productManageDao.deleteProductManage(param);
        return result;
    }

    private List<Map<String, Object>> filterListByRole(List<Map<String, Object>> JstbList) {

        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        if (JstbList == null || JstbList.isEmpty()) {
            return result;
        }
        Map<String, Object> params = new HashMap<>();
        boolean isJSGLBUser = false;
        params.put("currentUserId", ContextUtil.getCurrentUserId());

        // 管理员查看所有的包括草稿数据
        if ("admin".equals(ContextUtil.getCurrentUser().getUserNo())) {
            return JstbList;
        }

        String currentUserId = ContextUtil.getCurrentUserId();

        // 分管领导的查看权限
        boolean showAll = false;
        if (rdmZhglUtil.judgeUserIsFgld(ContextUtil.getCurrentUserId()) || isJSGLBUser) {
            showAll = true;
        }

        // 过滤
        for (Map<String, Object> oneProject : JstbList) {
            // 自己是当前流程处理人
            Object taskId = oneProject.get("taskId");
            if (taskId != null && oneProject.get("currentProcessUserId").toString().contains(currentUserId)) {
                result.add(oneProject);

            } else if (showAll) {
                // 对于非草稿的数据或者草稿但是创建人CREATE_BY_是自己的
                if (oneProject.get("STATUS") != null && !"DRAFTED".equals(oneProject.get("STATUS").toString())) {
                    result.add(oneProject);
                } else {
                    if (oneProject.get("CREATE_BY_").toString().equals(currentUserId)) {
                        result.add(oneProject);
                    }
                }

            } else {
                if (oneProject.get("CREATE_BY_").toString().equals(currentUserId)) {
                    result.add(oneProject);
                }

            }
        }
        return result;
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
            productManageDao.deleteStandardById(applyId);
            // 删除实例
            bpmInstManager.deleteCascade(instIdArr[i], "");
        }
        return new JsonResult(true, "成功删除!");
    }

    // 新增申请单
    public void createProductManage(JSONObject params) {
        String manageId = IdUtil.getId();
        params.put("manageId", manageId);

        // 产品开发管控信息添加
        Object performznceGrid = params.get("SUB_performznceGrid");
        List<JSONObject> performznceGridList = castList(performznceGrid, JSONObject.class);
        if (!performznceGridList.isEmpty()) {
            for (JSONObject performznce : performznceGridList) {
                performznce.put("id", IdUtil.getId());
                performznce.put("manageId", manageId);
                performznce.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                performznce.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                productManageDao.addProduct(performznce);
            }

        }

        // 产品(整机)设计添加
        Object productGrid = params.get("SUB_productGrid");
        List<JSONObject> productGridList = castList(productGrid, JSONObject.class);
        if (!productGridList.isEmpty()) {
            for (JSONObject product : productGridList) {
                product.put("id", IdUtil.getId());
                product.put("manageId", manageId);
                product.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                product.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                productManageDao.addProduct(product);
            }

        }

        // 部品设计添加
        Object assemblyGrid = params.get("SUB_assemblyGrid");
        List<JSONObject> assemblyGridList = castList(assemblyGrid, JSONObject.class);
        if (!assemblyGridList.isEmpty()) {
            for (JSONObject assembly : assemblyGridList) {
                assembly.put("id", IdUtil.getId());
                assembly.put("manageId", manageId);
                assembly.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                assembly.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                productManageDao.addProduct(assembly);
            }

        }

        // 验证/首台添加
        Object verificationGrid = params.get("SUB_verificationGrid");
        List<JSONObject> verificationGridList = castList(verificationGrid, JSONObject.class);
        if (!verificationGridList.isEmpty()) {
            for (JSONObject verification : verificationGridList) {
                verification.put("id", IdUtil.getId());
                verification.put("manageId", manageId);
                verification.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                verification.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                productManageDao.addProduct(verification);
            }

        }
        // 小批/量产添加
        Object batchGrid = params.get("SUB_batchGrid");
        List<JSONObject> batchGridList = castList(batchGrid, JSONObject.class);
        if (!batchGridList.isEmpty()) {
            for (JSONObject batch : batchGridList) {
                batch.put("id", IdUtil.getId());
                batch.put("manageId", manageId);
                batch.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                batch.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                productManageDao.addProduct(batch);
            }
        }

        params.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        params.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        productManageDao.addProductManage(params);

    }

    public static <T> List<T> castList(Object obj, Class<T> clazz) {
        List<T> result = new ArrayList<T>();
        if (obj instanceof List<?>) {
            for (Object o : (List<?>)obj) {
                result.add(clazz.cast(o));
            }
            return result;
        }
        return null;
    }

    public void updateStandardApply(JSONObject params) {
        String manageId = params.getString("id");
        // 删除
        productManageDao.delProductManageById(manageId);

        // 产品开发管控信息添加
        Object performznceGrid = params.get("SUB_performznceGrid");
        List<JSONObject> performznceGridList = castList(performznceGrid, JSONObject.class);
        if (!performznceGridList.isEmpty()) {
            for (JSONObject performznce : performznceGridList) {
                performznce.put("id", IdUtil.getId());
                performznce.put("manageId", manageId);
                performznce.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                performznce.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                productManageDao.addProduct(performznce);
            }

        }

        // 产品(整机)设计添加
        Object productGrid = params.get("SUB_productGrid");
        List<JSONObject> productGridList = castList(productGrid, JSONObject.class);
        if (!productGridList.isEmpty()) {
            for (JSONObject product : productGridList) {
                product.put("id", IdUtil.getId());
                product.put("manageId", manageId);
                product.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                product.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                productManageDao.addProduct(product);
            }

        }

        // 部品设计添加
        Object assemblyGrid = params.get("SUB_assemblyGrid");
        List<JSONObject> assemblyGridList = castList(assemblyGrid, JSONObject.class);
        if (!assemblyGridList.isEmpty()) {
            for (JSONObject assembly : assemblyGridList) {
                assembly.put("id", IdUtil.getId());
                assembly.put("manageId", manageId);
                assembly.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                assembly.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                productManageDao.addProduct(assembly);
            }

        }

        // 验证/首台添加
        Object verificationGrid = params.get("SUB_verificationGrid");
        List<JSONObject> verificationGridList = castList(verificationGrid, JSONObject.class);
        if (!verificationGridList.isEmpty()) {
            for (JSONObject verification : verificationGridList) {
                verification.put("id", IdUtil.getId());
                verification.put("manageId", manageId);
                verification.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                verification.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                productManageDao.addProduct(verification);
            }

        }
        // 小批/量产添加
        Object batchGrid = params.get("SUB_batchGrid");
        List<JSONObject> batchGridList = castList(batchGrid, JSONObject.class);
        if (!batchGridList.isEmpty()) {
            for (JSONObject batch : batchGridList) {
                batch.put("id", IdUtil.getId());
                batch.put("manageId", manageId);
                batch.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                batch.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                productManageDao.addProduct(batch);
            }
        }

        params.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        params.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        productManageDao.updateProductManage(params);

    }

    public JSONObject queryProductManage(String formId) {
        JSONObject result = productManageDao.queryProductManageById(formId);
        return result;
    }

    private List<JSONObject> filterListByDepRole(List<JSONObject> JstbList) {
        List<JSONObject> result = new ArrayList<JSONObject>();
        if (JstbList == null || JstbList.isEmpty()) {
            return result;
        }
        Map<String, Object> params = new HashMap<>();
        boolean isJSGLBUser = false;
        params.put("currentUserId", ContextUtil.getCurrentUserId());

        List<JSONObject> isJSGLB = productManageDao.isJSGLB(params);

        // List<JSONObject> isJSGLB = null;
        for (JSONObject jstb : isJSGLB) {
            if (jstb.getString("deptname").equals(RdmConst.JSGLB_NAME)) {
                isJSGLBUser = true;
                break;
            }
        }
        // 管理员查看所有的包括草稿数据
        if ("admin".equals(ContextUtil.getCurrentUser().getUserNo())) {
            return JstbList;
        }
        // 分管领导的查看权限
        boolean showAll = false;
        if (rdmZhglUtil.judgeUserIsFgld(ContextUtil.getCurrentUserId()) || isJSGLBUser) {
            showAll = true;
        }

        // 确定当前登录人是否是部门负责人
        JSONObject currentUserDepInfo = xcmgProjectManager.isCurrentUserDepRespman();
        if (!"success".equals(currentUserDepInfo.getString("result"))) {
            return result;
        }
        boolean isDepRespMan = currentUserDepInfo.getBoolean("isDepRespMan");
        String currentUserMainDepId = currentUserDepInfo.getString("currentUserMainDepId");
        String currentUserId = ContextUtil.getCurrentUserId();
        String currentUserMainDepName = currentUserDepInfo.getString("currentUserMainDepName");
        // 是否挖掘机械研究院下的部门
        JSONObject isJSZX = loginRecordManager.judgeIsJSZX(currentUserMainDepId, currentUserMainDepName);
        boolean isJSZXUser = false;
        if (isJSZX.getString("isJSZX").equals("true")) {
            isJSZXUser = true;
        }
        // 如果是，则查询出挖掘机械研究院这个部门的id
        // 过滤
        for (JSONObject oneProject : JstbList) {
            // 自己是当前流程处理人
            if (StringUtils.isNotBlank(oneProject.getString("myTaskId"))) {
                result.add(oneProject);
            } else if (showAll) {
                // 对于非草稿的数据或者草稿但是创建人CREATE_BY_是自己的
                if (oneProject.get("status") != null && !"草稿".equals(oneProject.get("status").toString())) {
                    result.add(oneProject);
                } else {
                    if (oneProject.get("CREATE_BY_").toString().equals(currentUserId)) {
                        result.add(oneProject);
                    }
                }
            } else if (isDepRespMan) {
                // 审批中：自己部门员工或者自己提交的
                if (oneProject.get("status") != null && "审批中".equals(oneProject.get("status").toString())) {
                    if (oneProject.get("creatorDepId").toString().equals(currentUserMainDepId)
                        || oneProject.get("CREATE_BY_").toString().equals(currentUserId)) {
                        result.add(oneProject);
                    }
                    // 发布：自己部门员工或者自己提交或者涉及部门有自己部门
                } else if (oneProject.get("status") != null && "发布".equals(oneProject.get("status").toString())) {
                    if ((oneProject.get("relatedDeptIds") != null
                        && oneProject.get("relatedDeptIds").toString().indexOf(currentUserMainDepId) != -1)
                        || (isJSZXUser && (oneProject.get("relatedDeptIds") != null
                            && oneProject.get("relatedDeptIds").toString().indexOf("87212403321741318") != -1))
                        || oneProject.get("creatorDepId").toString().equals(currentUserMainDepId)
                        || oneProject.get("CREATE_BY_").toString().equals(currentUserId)) {
                        result.add(oneProject);
                    }
                    // 草稿：自己的
                } else if (oneProject.get("status") != null && "草稿".equals(oneProject.get("status").toString())) {
                    if (oneProject.get("CREATE_BY_").toString().equals(currentUserId)) {
                        result.add(oneProject);
                    }
                } else if (oneProject.get("status") != null && "作废".equals(oneProject.get("status").toString())) {
                    if ((oneProject.get("relatedDeptIds") != null
                        && oneProject.get("relatedDeptIds").toString().indexOf(currentUserMainDepId) != -1)
                        || (isJSZXUser && (oneProject.get("relatedDeptIds") != null
                            && oneProject.get("relatedDeptIds").toString().indexOf("87212403321741318") != -1))
                        || oneProject.get("creatorDepId").toString().equals(currentUserMainDepId)
                        || oneProject.get("CREATE_BY_").toString().equals(currentUserId)) {
                        result.add(oneProject);
                    }
                }
            } else {
                // 其他人对于非草稿的且项目成员中包含自己的，或者草稿但是创建人CREATE_BY_是自己的
                if ((oneProject.get("status") != null && "发布".equals(oneProject.get("status").toString()))
                    && oneProject.get("relatedDeptIds") != null
                    && (oneProject.get("relatedDeptIds").toString().indexOf(currentUserMainDepId) != -1 || (isJSZXUser
                        && oneProject.get("relatedDeptIds").toString().indexOf("87212403321741318") != -1))) {
                    result.add(oneProject);
                } else if ((oneProject.get("status") != null && "作废".equals(oneProject.get("status").toString()))
                    && ((oneProject.get("relatedDeptIds") != null
                        && oneProject.get("relatedDeptIds").toString().indexOf(currentUserMainDepId) != -1)
                        || (isJSZXUser
                            && oneProject.get("relatedDeptIds").toString().indexOf("87212403321741318") != -1))) {
                    result.add(oneProject);
                } else {
                    if (oneProject.get("CREATE_BY_").toString().equals(currentUserId)) {
                        result.add(oneProject);
                    }
                }
            }
        }
        return result;
    }

    /**
     * 产品技术管控需求下拉框
     *
     * @param dicType
     * @return
     */
    public List<Map<String, Object>> getDicValues(String dicType) {
        Map<String, Object> params = new HashMap<>(16);
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            params.put("key", dicType);
            list = productManageDao.getProductExploitRelation(params);
        } catch (Exception e) {
            logger.error("Exception in getDicValues", e);
        }
        return list;
    }

    public List<JSONObject> getItemList(HttpServletRequest request) {

        String manageId = request.getParameter("manageId");
        if ("".equals(manageId)) {
            return new ArrayList<>();
        }
        String dicType = request.getParameter("dicType");
        JSONObject paramJson = new JSONObject();
        paramJson.put("mainId", manageId);
        paramJson.put("dicType", dicType);
        List<JSONObject> resultArray = productManageDao.getItemList(paramJson);

        return resultArray;

    }
}
