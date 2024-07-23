package com.redxun.rdmZhgl.core.service;

import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmZhgl.core.dao.MaterialApplyDao;
import com.redxun.rdmZhgl.core.util.RdmZhglConst;
import com.redxun.rdmZhgl.core.wsdl.WsdlUtils;
import com.redxun.standardManager.core.util.ResultUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.query.Page;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
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
public class MaterialApplyService {
    private static Logger logger = LoggerFactory.getLogger(MaterialApplyService.class);
    @Resource
    private BpmInstManager bpmInstManager;
    @Resource
    private MaterialApplyDao materialApplyDao;
    @Resource
    private XcmgProjectManager xcmgProjectManager;
    @Resource
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Resource
    private CommonInfoManager commonInfoManager;

    public JsonPageResult<?> getDetailReport(HttpServletRequest request) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            Map<String, Object> params = new HashMap<>(16);
            List<Map<String, Object>> list = new ArrayList<>();
            List<Map<String, Object>> totalList = new ArrayList<>();
            params = CommonFuns.getSearchParam(params, request, true);
            JSONObject resultJson = commonInfoManager.hasPermission("WLYLH-GLY");
            if (resultJson.getBoolean("WLYLH-GLY") || resultJson.getBoolean(RdmConst.ALLDATA_QUERY_KEY)
                || "admin".equals(ContextUtil.getCurrentUser().getUserNo())) {
            } else {
                params.put("userId", ContextUtil.getCurrentUserId());
            }
            list = materialApplyDao.getDetailReport(params);
            params = new HashMap<>(16);
            params = CommonFuns.getSearchParam(params, request, false);
            if (resultJson.getBoolean("WLYLH-GLY") || resultJson.getBoolean(RdmConst.ALLDATA_QUERY_KEY)
                || "admin".equals(ContextUtil.getCurrentUser().getUserNo())) {
            } else {
                params.put("userId", ContextUtil.getCurrentUserId());
            }
            totalList = materialApplyDao.getDetailReport(params);
            convertDate(list);
            // 返回结果
            result.setData(list);
            result.setTotal(totalList.size());
        } catch (Exception e) {
            logger.error("Exception in 查询异常", e);
        }
        return result;
    }

    public void add(Map<String, Object> params) {
        String preFix = "SP-";
        String id = preFix + XcmgProjectUtil.getNowLocalDateStr("yyyyMMddHHmmssSSS") + (int)(Math.random() * 100);
        params.put("id", id);
        params.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        params.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        params.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        params.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        materialApplyDao.add(params);
        if (StringUtils.isNotBlank(CommonFuns.nullToString(params.get("SUB_detail")))) {
            JSONArray detailDataJson = JSONObject.parseArray(params.get("SUB_detail").toString());
            for (int i = 0; i < detailDataJson.size(); i++) {
                JSONObject oneObject = detailDataJson.getJSONObject(i);
                oneObject.put("id", IdUtil.getId());
                oneObject.put("applyId", id);
                oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                oneObject.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                oneObject.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                materialApplyDao.addDetail(oneObject);
            }
        }
    }

    public void update(Map<String, Object> params) {
        params.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        params.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        materialApplyDao.update(params);
        if (StringUtils.isNotBlank(CommonFuns.nullToString(params.get("detailData")))) {
            JSONArray detailDataJson = JSONObject.parseArray(params.get("detailData").toString());
            String applyId = CommonFuns.nullToString(params.get("id"));
            for (int i = 0; i < detailDataJson.size(); i++) {
                JSONObject oneObject = detailDataJson.getJSONObject(i);
                String state = oneObject.getString("_state");
                if ("added".equals(state) || StringUtils.isBlank(applyId)) {
                    // 新增
                    oneObject.put("id", IdUtil.getId());
                    oneObject.put("applyId", applyId);
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                    materialApplyDao.addDetail(oneObject);
                } else if ("modified".equals(state)) {
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                    materialApplyDao.updateDetail(oneObject);
                } else if ("removed".equals(state)) {
                    // 删除
                    materialApplyDao.delDetailById(oneObject.getString("id"));
                }
            }
        }
    }

    public void saveOrUpdateDetail(String changeGridDataStr) {
        JSONObject resultJson = new JSONObject();
        try {
            JSONArray changeGridDataJson = JSONObject.parseArray(changeGridDataStr);
            for (int i = 0; i < changeGridDataJson.size(); i++) {
                JSONObject oneObject = changeGridDataJson.getJSONObject(i);
                String state = oneObject.getString("_state");
                String detailId = oneObject.getString("id");
                if ("added".equals(state) || StringUtils.isBlank(detailId)) {
                    // 新增
                    oneObject.put("id", IdUtil.getId());
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                    materialApplyDao.addDetail(oneObject);
                } else if ("modified".equals(state)) {
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                    materialApplyDao.updateDetail(oneObject);
                } else if ("removed".equals(state)) {
                    // 删除
                    materialApplyDao.delDetailById(oneObject.getString("id"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("保存信息异常", e);
            return;
        }
    }

    /**
     * 删除表单及关联的所有信息
     */
    public JsonResult delete(String[] applyIdArr, String[] instIdArr) {
        if (applyIdArr.length != instIdArr.length) {
            return new JsonResult(false, "表单和流程个数不相同！");
        }
        Map<String, Object> param = new HashMap<>();
        for (int i = 0; i < applyIdArr.length; i++) {
            String applyId = applyIdArr[i];
            param.put("applyId", applyId);
            materialApplyDao.delete(applyId);
            // 删除实例
            bpmInstManager.deleteCascade(instIdArr[i], "");
            materialApplyDao.delDetailByApplyId(applyId);
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
            List<Map<String, Object>> applyList = materialApplyDao.queryList(params);
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
        boolean showAll = false;
        // 管理员查看所有的包括草稿数据
        JSONObject resultJson = commonInfoManager.hasPermission("WLYLH-GLY");
        if (ConstantUtil.ADMIN.equals(ContextUtil.getCurrentUser().getUserNo()) || resultJson.getBoolean("WLYLH-GLY")) {
            showAll = true;
        }
        // 分管领导的查看权限等同于项目管理人员
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

    public JSONObject sendToSap(String applyId) {
        JSONObject resultJson = new JSONObject();
        try {
            JSONObject sendObj = new JSONObject();
            // 1.先拼接主表单信息
            JSONObject applyObj = materialApplyDao.getJsonObject(applyId);
            // 收货库存地点
            sendObj.put("UMLGO", applyObj.getString("storageLocation"));
            // 收货方
            sendObj.put("WEMPF", "");
            // 需求日期
            sendObj.put("BDTER", DateUtil.formatDate(applyObj.getDate("finalDate"), "YYYYMMdd"));
            // 成本中心
            sendObj.put("KOSTL", CommonFuns.nullToString(applyObj.getString("costCenter")));
            // 订单编号
            sendObj.put("AUFNR", CommonFuns.nullToString(applyObj.getString("orderCode")));
            // 总账科目编号
            sendObj.put("SAKNR", CommonFuns.nullToString(applyObj.getString("ledgerAccount")));
            // 移动类型
            String applyType = applyObj.getString("applyType");
            if (applyType.startsWith("Z55") || applyType.startsWith("Z53")) {
                applyType = applyType.substring(0, 3);
            }
            sendObj.put("MOVE_TYPE", applyType);
            // 2.拼接物料详情信息
            List<JSONObject> itemList = materialApplyDao.getDetailList(applyId);
            JSONArray itemArray = new JSONArray();
            JSONObject itemJson;
            for (JSONObject itemObj : itemList) {
                itemJson = new JSONObject();
                // 物料编号
                itemJson.put("MATNR", CommonFuns.nullToString(itemObj.getString("itemCode")));
                // 物料描述
                itemJson.put("MAKTX", "");
                // 需求量
                itemJson.put("BDMNG", itemObj.getString("totalNum"));
                // 存储地点
                if ("311".equals(applyType)) {
                    itemJson.put("LGORT", CommonFuns.nullToString(itemObj.getString("storage")));
                } else {
                    itemJson.put("LGORT", "");
                }
                // 基本计量单位
                itemJson.put("MEINS", CommonFuns.nullToString(itemObj.getString("unit")));
                itemArray.add(itemJson);
            }
            sendObj.put("itemArray", itemArray);
            JSONObject sendResultJson = WsdlUtils.createMaterial(sendObj);
            // 如果成功的话，回写预留单号,SAP行号,推送成功标志
            if (RdmZhglConst.SAP_OK.equals(sendResultJson.getString("errorCode"))) {
                String materialCode = sendResultJson.getString("materialCode");
                String succeed = "1";
                JSONObject applyMainObj = new JSONObject();
                applyMainObj.put("id", applyId);
                applyMainObj.put("materialCode", materialCode);
                applyMainObj.put("succeed", succeed);
                materialApplyDao.updateSendStatus(applyMainObj);
                // 回写SAP行号
                JSONArray itemResultList = sendResultJson.getJSONArray("itemList");
                JSONObject itemObj;
                JSONObject lineNoObj;
                for (int i = 0; i < itemResultList.size(); i++) {
                    itemObj = itemResultList.getJSONObject(i);
                    lineNoObj = new JSONObject();
                    lineNoObj.put("applyId", applyId);
                    lineNoObj.put("itemCode", itemObj.getString("MATNR"));
                    lineNoObj.put("lineNo", itemObj.getString("RSPOS"));
                    lineNoObj.put("materialCode", itemObj.getString("RSNUM"));
                    materialApplyDao.updateDetailLineNo(lineNoObj);
                }
                resultJson = ResultUtil.result(true, "推送成功！", "");
            } else {
                String succeed = "0";
                JSONObject applyMainObj = new JSONObject();
                applyMainObj.put("id", applyId);
                applyMainObj.put("succeed", succeed);
                applyMainObj.put("errorMsg", sendResultJson.getString("errorMsg"));
                materialApplyDao.updateSendStatus(applyMainObj);
                resultJson = ResultUtil.result(false, sendResultJson.getString("errorMsg"), null);
            }
        } catch (Exception e) {
            resultJson = ResultUtil.result(false, "推送异常！", null);
            logger.error("推送sap异常", e);
        }
        return resultJson;
    }

    public JSONObject asyncItemStatus(List<String> materialCodeList, List<String> lineNoList) {
        JSONObject resultJson = new JSONObject();
        try {
            JSONArray itemArray = new JSONArray();
            JSONObject itemJson;
            for (int i = 0; i < materialCodeList.size(); i++) {
                itemJson = new JSONObject();
                itemJson.put("RSNUM", materialCodeList.get(i));
                itemJson.put("RSPOS", lineNoList.get(i));
                itemArray.add(itemJson);
            }
            JSONObject getResultJson = WsdlUtils.getMaterialItemStatus(itemArray);
            if (RdmZhglConst.SAP_OK.equals(getResultJson.getString("errorCode"))) {
                JSONArray resultList = getResultJson.getJSONArray("resultList");
                JSONObject itemObj;
                JSONObject paramJson;
                for (int i = 0; i < resultList.size(); i++) {
                    itemObj = resultList.getJSONObject(i);
                    paramJson = new JSONObject();
                    String RSNUM = itemObj.getString("RSNUM");
                    String RSPOS = itemObj.getString("RSPOS");
                    String XLOEK = itemObj.getString("XLOEK");
                    String WQSL = itemObj.getString("WQSL");
                    paramJson.put("materialCode", RSNUM);
                    paramJson.put("lineNo", RSPOS);
                    if ("X".equals(XLOEK)) {
                        paramJson.put("delFlag", "1");
                    }
                    if (WQSL != null) {
                        WQSL = WQSL.trim();
                    }
                    paramJson.put("delaNum", WQSL);
                    // 如果未清数量为0 则完成
                    if (StringUtils.isNotBlank(WQSL) && Float.parseFloat(WQSL) == 0.0) {
                        paramJson.put("finishFlag", "1");
                    }
                    paramJson.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    paramJson.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                    materialApplyDao.updateDetailStatus(paramJson);
                }
                resultJson = ResultUtil.result(true, "同步成功！", "");
            } else {
                resultJson = ResultUtil.result(false, getResultJson.getString("errorMsg"), null);
            }
        } catch (Exception e) {
            logger.error("同步物料状态详情", e);
            resultJson = ResultUtil.result(false, "同步异常！", null);
        }
        return resultJson;
    }

    public JSONObject invalidItems(List<String> materialCodeList, List<String> lineNoList) {
        JSONObject resultJson = new JSONObject();
        try {
            JSONArray itemArray = new JSONArray();
            JSONObject itemJson;
            for (int i = 0; i < materialCodeList.size(); i++) {
                itemJson = new JSONObject();
                itemJson.put("RSNUM", materialCodeList.get(i));
                itemJson.put("RSPOS", lineNoList.get(i));
                itemArray.add(itemJson);
            }
            JSONObject getResultJson = WsdlUtils.delMaterialItems(itemArray);
            if (!"NG".equals(getResultJson.getString("errorCode"))) {
                asyncItemStatus(materialCodeList, lineNoList);
                resultJson = ResultUtil.result(true, "作废成功！", "");
            } else {
                resultJson = ResultUtil.result(false, getResultJson.getString("errorMsg"), null);
            }
        } catch (Exception e) {
            logger.error("invalidItems", e);
            resultJson = ResultUtil.result(false, "作废异常！", null);
        }
        return resultJson;
    }

    public static void convertDate(List<Map<String, Object>> list) {
        if (list != null && !list.isEmpty()) {
            for (Map<String, Object> oneApply : list) {
                for (String key : oneApply.keySet()) {
                    if (key.endsWith("_TIME_") || key.endsWith("_time") || key.endsWith("_date")) {
                        if (oneApply.get(key) != null) {
                            oneApply.put(key, DateUtil.formatDate((Date)oneApply.get(key), "yyyy-MM-dd  HH:mm:ss"));
                        }
                    }
                    if ("startDate".equals(key)) {
                        if (oneApply.get(key) != null) {
                            oneApply.put(key, DateUtil.formatDate((Date)oneApply.get(key), "yyyy-MM-dd"));
                        }
                    }
                    if ("finalDate".equals(key)) {
                        if (oneApply.get(key) != null) {
                            oneApply.put(key, DateUtil.formatDate((Date)oneApply.get(key), "yyyy-MM-dd"));
                        }
                    }
                }
            }
        }
    }

    // TDMII获取RDM预留申请列表
    public void getMaterialApplyListWithItems(JSONObject result, String postData) {
        JSONObject params = JSONObject.parseObject(postData);
        if (params.getString("userNo").equalsIgnoreCase("admin")) {
            params.remove("userNo");
        }
        params.put("instStatus", "SUCCESS_END");
        params.put("succeed", "1");
        List<JSONObject> materialApplyList = materialApplyDao.queryListForTdmII(params);
        if (!materialApplyList.isEmpty()) {
            for (JSONObject materialApply : materialApplyList) {
                List<JSONObject> detailList = materialApplyDao.getDetailList(materialApply.getString("id"));
                materialApply.put("items", JSONObject.toJSONString(detailList));
            }
        }
        Integer materialApplyListCount = materialApplyDao.queryListCountForTdmII(params);
        result.put("data", materialApplyList);
        result.put("dataCount", materialApplyListCount);
    }

    public JSONObject checkOrderCode(String orderCode, JSONObject approveValid) {
        List<JSONObject> projectList = materialApplyDao.getProjectByOrderCode(orderCode);
        if (projectList.size() == 0) {
            approveValid.put("success", false);
            approveValid.put("message", "该财务订单号未关联项目!");
        } else {
            JSONObject projectInfo = projectList.get(0);
            if ("DISCARD_END".equalsIgnoreCase(projectInfo.getString("status"))
                || "SUCCESS_END".equalsIgnoreCase(projectInfo.getString("status"))) {
                approveValid.put("success", false);
                approveValid.put("message", "该财务订单号关联项目已结项!");
            }
        }
        return approveValid;
    }
}
