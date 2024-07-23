package com.redxun.rdmZhgl.core.service;

import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmZhgl.core.dao.ProductConfigDao;
import com.redxun.rdmZhgl.core.dao.ProductDao;
import com.redxun.rdmZhgl.core.dao.XpszChangeDao;
import com.redxun.saweb.util.IdUtil;
import com.redxun.standardManager.core.util.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.query.Page;
import com.redxun.core.util.DateUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import com.redxun.xcmgProjectManager.core.util.ConstantUtil;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

import groovy.util.logging.Slf4j;

/**
 * @author zz
 */
@Service
@Slf4j
public class XpszChangeService {
    private static Logger logger = LoggerFactory.getLogger(XpszChangeService.class);
    @Resource
    private BpmInstManager bpmInstManager;
    @Resource
    private XpszChangeDao xpszChangeDao;
    @Resource
    private XcmgProjectManager xcmgProjectManager;
    @Resource
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Resource
    private ProductDao productDao;
    @Resource
    private ProductConfigDao productConfigDao;
    @Resource
    private CommonInfoManager commonInfoManager;

    public void add(Map<String, Object> params) {
        String preFix = "SP-";
        String id = preFix + XcmgProjectUtil.getNowLocalDateStr("yyyyMMddHHmmssSSS") + (int)(Math.random() * 100);
        params.put("id", id);
        params.put("deptId", ContextUtil.getCurrentUser().getMainGroupId());
        params.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        params.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        params.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        params.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        xpszChangeDao.add(params);

    }

    public void update(Map<String, Object> params) {
        params.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        params.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        xpszChangeDao.update(params);
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
            xpszChangeDao.delete(applyId);
            // 删除实例
            bpmInstManager.deleteCascade(instIdArr[i], "");
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
            params = XcmgProjectUtil.getSearchParam(params, request);
            List<Map<String, Object>> applyList = xpszChangeDao.queryList(params);
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
        if (ConstantUtil.ADMIN.equals(ContextUtil.getCurrentUser().getUserNo())) {
            return applyList;
        }
        JSONObject roleObj = commonInfoManager.hasPermission("XPSZ-JHGGY");
        if(roleObj.getBoolean("XPSZ-JHGGY")){
            return applyList;
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
                if (RdmConst.AllDATA_QUERY_NAME.equalsIgnoreCase(oneRole.get("NAME_").toString())) {
                    showAll = true;
                    break;
                }
            }
        }
        // 确定当前登录人是否是部门负责人
        JSONObject currentUserDepInfo = xcmgProjectManager.isCurrentUserDepRespman();
        if (!ConstantUtil.SUCCESS.equals(currentUserDepInfo.getString("result"))) {
            return result;
        }
        boolean isDepRespMan = currentUserDepInfo.getBoolean("isDepRespMan");
        String currentUserMainDepId = currentUserDepInfo.getString("currentUserMainDepId");
        String currentUserId = ContextUtil.getCurrentUserId();
        String currentUserMainDepName = currentUserDepInfo.getString("currentUserMainDepName");
        boolean isDepProjectManager =
            XcmgProjectUtil.judgeIsDepProjectManager(currentUserMainDepName, currentUserRoles);
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
            } else if (isDepRespMan || isDepProjectManager) {
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

    public JSONObject getBpmSolution(Map<String, Object> param) {
        param.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        List<Map<String, Object>> list = xpszChangeDao.getBmpSolution(param);
        JSONObject resultJson = new JSONObject();
        if (list != null && list.size() > 0) {
            resultJson = XcmgProjectUtil.convertMap2JsonObject(list.get(0));
        }
        return resultJson;
    }

    public JSONObject changePlan(JSONObject postDataJson) {
        JSONObject resultJson = new JSONObject();
        try {
            String id = postDataJson.getString("id");
            JSONObject changeObj = xpszChangeDao.getJsonObject(id);
            String isChange = changeObj.getString("isChange");
            if("1".equals(isChange)){
                return ResultUtil.result(false, "已经变更！", "");
            }
            String mainIds = changeObj.getString("mainIds");
            String startItemId = changeObj.getString("startItemId");
            String endItemId  = changeObj.getString("endItemId");
            int delayDays = changeObj.getInteger("delayDays");
            //先将最新的数据复制一份 作为 历史数据，然后再修改数据
            JSONObject paramJson = new JSONObject();
            paramJson.put("mainId",mainIds);
            paramJson.put("itemType","3");
            Map<String, Object> resultMap = productDao.getItemObject(paramJson);
            resultMap.put("id", IdUtil.getId());
            resultMap.put("itemType","2");
            productDao.addItem(resultMap);
            //更新最新记录
            JSONObject startObj = productConfigDao.getItemObjById(startItemId);
            int startSort = startObj.getInteger("sort");
            JSONObject endObj = productConfigDao.getItemObjById(endItemId);
            int endSort = endObj.getInteger("sort");
            paramJson = new JSONObject();
            paramJson.put("startSort",startSort);
            paramJson.put("endSort",endSort);
            List<JSONObject> list = productConfigDao.getItemList(paramJson);
            JSONObject param = new JSONObject();
            for(JSONObject temp : list){
                param.put(temp.getString("itemCode"),delayDays);
            }
            param.put("mainId", mainIds);
            param.put("itemType","3");
            productDao.updatePlanDate(param);
            //更新主表
            StringBuffer changeStr = new StringBuffer();
            changeStr.append("从").append(startObj.getString("itemName")).append("节点到").append(endObj.getString("itemName"));
            changeStr.append("节点延期").append(delayDays).append("天");
            Map<String,Object> map = new HashMap<>(16);
            map.put("id",mainIds);
            map.put("planChange",changeStr.toString());
            productDao.updateObject(map);
            //变更表 变更状态修改
            map = new HashMap<>(16);
            map.put("id",id);
            map.put("isChange","1");
            map.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            map.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            xpszChangeDao.update(map);
            //添加变更记录表
            map = new HashMap<>(16);
            map.put("id",IdUtil.getId());
            map.put("mainId",mainIds);
            map.put("startItem",startObj.getString("itemName"));
            map.put("endItem",endObj.getString("itemName"));
            map.put("delayDays",delayDays);
            map.put("reason",changeObj.getString("reason"));
            map.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            map.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            map.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            map.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            xpszChangeDao.addChangeRecord(map);

        } catch (Exception e) {
            logger.error("Exception in changePlan ", e);
            return ResultUtil.result(false, "更改计划异常！", "");
        }
        return ResultUtil.result(true, "更改日期成功！", resultJson);
    }

}
