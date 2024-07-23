package com.redxun.rdmZhgl.core.service;

import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.query.Page;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmZhgl.core.dao.DelayApplyDao;
import com.redxun.rdmZhgl.core.dao.DelayApplyDetailDao;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.sys.org.manager.OsGroupManager;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;

import groovy.util.logging.Slf4j;

/**
 * @author zz
 */
@Service
@Slf4j
public class DelayApplyService {
    private static Logger logger = LoggerFactory.getLogger(DelayApplyService.class);
    @Resource
    private BpmInstManager bpmInstManager;
    @Resource
    private DelayApplyDao delayApplyDao;
    @Resource
    private DelayApplyDetailDao delayApplyDetailDao;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private DelayApplyDetailService delayApplyDetailService;
    @Autowired
    private OsGroupManager osGroupManager;
    @Autowired
    private CommonInfoManager commonInfoManager;

    public void add(JSONObject params) {
        String preFix = "SP-";
        String id = preFix + CommonFuns.getNowLocalDateStr("yyyyMMddHHmmssSSS") + (int)(Math.random() * 100);
        params.put("id", id);
        params.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        params.put("CREATE_TIME_", new Date());
        params.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        params.put("UPDATE_TIME_", new Date());
        delayApplyDao.add(params);
        // 如果有明细的变化则处理
        if (params.getJSONArray("itemChangeData") != null && !params.getJSONArray("itemChangeData").isEmpty()) {
            JsonResult result = new JsonResult();
            delayApplyDetailService.saveOrUpdate(result, params.getJSONArray("itemChangeData"), id);
        }
    }

    public void update(JSONObject params) {
        String id = params.getString("id");
        params.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        params.put("UPDATE_TIME_", new Date());
        delayApplyDao.update(params);
        // 如果有明细的变化则处理
        if (params.getJSONArray("itemChangeData") != null && !params.getJSONArray("itemChangeData").isEmpty()) {
            JsonResult result = new JsonResult();
            delayApplyDetailService.saveOrUpdate(result, params.getJSONArray("itemChangeData"), id);
        }
    }

    /**
     * 删除表单及关联的所有信息
     */
    public JsonResult delete(String[] applyIdArr, String[] instIdArr) {
        if (applyIdArr.length != instIdArr.length) {
            return new JsonResult(false, "表单和流程个数不相同！");
        }
        for (int i = 0; i < applyIdArr.length; i++) {
            String applyId = applyIdArr[i];
            delayApplyDao.delete(applyId);
            // 删除实例
            bpmInstManager.deleteCascade(instIdArr[i], "");
            // 删除明细
            delayApplyDetailDao.delDetail(applyId);
        }
        return new JsonResult(true, "成功删除!");
    }

    /**
     * 查询变更列表
     */
    public JsonPageResult<?> queryList(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            Map<String, Object> params = new HashMap<>();
            // 传入条件(不包括分页)
            params = CommonFuns.getSearchParam(params, request, false);
            List<Map<String, Object>> applyList = delayApplyDao.queryList(params);
            // 增加不同角色和部门的人看到的数据不一样的过滤
            List<Map<String, Object>> finalAllApplyList = null;
            finalAllApplyList = filterApplyListByDepRole(applyList);
            // 根据分页进行subList截取
            int pageIndex = RequestUtil.getInt(request, "pageIndex", 0);
            int pageSize = RequestUtil.getInt(request, "pageSize", Page.DEFAULT_PAGE_SIZE);
            int startSubListIndex = pageIndex * pageSize;
            int endSubListIndex = startSubListIndex + pageSize;
            List<Map<String, Object>> finalSubApplyList = new ArrayList<Map<String, Object>>();
            if (startSubListIndex < finalAllApplyList.size()) {
                finalSubApplyList = finalAllApplyList.subList(startSubListIndex,
                    endSubListIndex < finalAllApplyList.size() ? endSubListIndex : finalAllApplyList.size());
            }

            if (finalSubApplyList != null && !finalSubApplyList.isEmpty()) {
                for (Map<String, Object> oneApply : finalSubApplyList) {
                    if (oneApply.get("CREATE_TIME_") != null) {
                        oneApply.put("CREATE_TIME_",
                            DateUtil.formatDate((Date)oneApply.get("CREATE_TIME_"), "yyyy-MM-dd HH:mm:ss"));
                    }
                    if (oneApply.get("applyTime") != null) {
                        oneApply.put("applyTime",
                            DateUtil.formatDate((Date)oneApply.get("applyTime"), "yyyy-MM-dd HH:mm:ss"));
                    }
                    if (oneApply.get("UPDATE_TIME_") != null) {
                        oneApply.put("UPDATE_TIME_",
                            DateUtil.formatDate((Date)oneApply.get("UPDATE_TIME_"), "yyyy-MM-dd HH:mm:ss"));
                    }
                }
            }
            // 返回结果
            result.setData(finalSubApplyList);
            result.setTotal(finalAllApplyList.size());
        } catch (Exception e) {
            logger.error("Exception in queryList", e);
            result.setSuccess(false);
            result.setMessage("系统异常");
        }
        return result;
    }

    /**
     * 根据登录人部门、角色对列表进行过滤
     */
    public List<Map<String, Object>> filterApplyListByDepRole(List<Map<String, Object>> applyList) {
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
        // 分管领导的查看权限等同于项目管理人员
        boolean showAll = false;
        // 是否挖掘机械研究院人资专员
        boolean isJszxrzzy = false;
        Map<String, Object> params = new HashMap<>();
        params.put("userId", ContextUtil.getCurrentUser().getUserId());
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params);
        List<String> groupNames = new ArrayList<>();
        for (Map<String, Object> oneRole : currentUserRoles) {
            if (oneRole.get("REL_TYPE_KEY_").toString().equalsIgnoreCase("GROUP-USER-LEADER")
                || oneRole.get("REL_TYPE_KEY_").toString().equalsIgnoreCase("GROUP-USER-BELONG")) {
                if (RdmConst.AllDATA_QUERY_NAME.equalsIgnoreCase(oneRole.get("NAME_").toString())) {
                    showAll = true;
                    break;
                }
                if ("技术支部书记".equalsIgnoreCase(oneRole.get("NAME_").toString())) {
                    showAll = true;
                    break;
                }
                if (RdmConst.JSZX_RZZY.equalsIgnoreCase(oneRole.get("NAME_").toString())) {
                    isJszxrzzy = true;
                    Map<String, String> deptMap = commonInfoManager.queryDeptUnderJSZX();
                    if (deptMap != null) {
                        groupNames.addAll(deptMap.values());
                    }
                    break;
                }
            }
        }
        // 确定当前登录人是否是部门负责人
        JSONObject currentUserDepInfo = xcmgProjectManager.isCurrentUserDepRespman();
        if (!"success".equals(currentUserDepInfo.getString("result"))) {
            return result;
        }
        // boolean isDepRespMan = currentUserDepInfo.getBoolean("isDepRespMan");
        boolean isDepRespMan = true;
        String currentUserMainDepId = currentUserDepInfo.getString("currentUserMainDepId");
        String currentUserId = ContextUtil.getCurrentUserId();
        String currentUserMainDepName = currentUserDepInfo.getString("currentUserMainDepName");
        // 过滤
        for (Map<String, Object> oneApply : applyList) {
            // 自己是当前流程处理人
            if (oneApply.get("currentProcessUserId") != null
                && oneApply.get("currentProcessUserId").toString().contains(currentUserId)) {
                oneApply.put("processTask", true);
                result.add(oneApply);
            } else if (showAll) {
                // 分管领导和项目管理人员查看所有非草稿的数据或者草稿但是创建人CREATE_BY_是自己的
                if (oneApply.get("instStatus") != null && !"DRAFTED".equals(oneApply.get("instStatus").toString())) {
                    result.add(oneApply);
                } else {
                    if (oneApply.get("CREATE_BY_").toString().equals(currentUserId)) {
                        result.add(oneApply);
                    }
                }
            } else if (isJszxrzzy) {
                if (groupNames.contains(oneApply.get("deptName").toString())) {
                    result.add(oneApply);
                }
            } else if (isDepRespMan) {
                // 部门负责人对于非草稿的且申请人部门是当前部门，或者草稿但是创建人CREATE_BY_是自己的
                if (oneApply.get("instStatus") != null) {
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

}
