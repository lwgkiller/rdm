package com.redxun.rdmZhgl.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.HttpClientUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmZhgl.core.dao.MaterialProcureME51NDao;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import groovy.util.logging.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service
public class MaterialProcureME51NService {
    private static Logger logger = LoggerFactory.getLogger(MaterialProcureME51NService.class);
    /**
     * 状态："新增待创建"，明细指令库表无值
     * 1.删除：直接删库
     * 2.修改：直接修改
     * 3.创建SAP：指令赋add，明细指令赋add，调用SAP
     * --3.1.成功：状态变为"新增成功"，回写 applyNo|result|message，明细回写itemNo
     * --3.2.失败：状态不变，回写 result|message
     * 4.修改SAP：neg
     */
    public static final String BUSINESS_STATUS_XINZENGDAITUISONG = "新增待创建";
    /**
     * 状态："新增成功"，明细指令库表无值
     * 1.删除：指令赋delete，明细指令赋delete，调用SAP
     * --1.1.成功：状态变为"删除成功"，回写 result|message，明细库表指令清空
     * --1.2.失败：状态不变，回写 result|message
     * 2.修改：状态变为"修改待推送"，明细指令根据实际修改情况写库表
     * 3.创建SAP：neg
     * 4.修改SAP：neg
     */
    public static final String BUSINESS_STATUS_XINZENGCHENGGONG = "新增成功";
    /**
     * 状态："修改待推送"，明细指令库表有实际修改情况值
     * 1.删除：指令赋delete，明细指令赋delete，调用SAP
     * --1.1.成功：状态变为"删除成功"，回写 result|message，明细库表指令清空
     * --1.2.失败：状态不变，回写 result|message
     * 2.修改：状态不变，明细指令根据实际修改情况写库表
     * 3.创建SAP：neg
     * 4.修改SAP：指令赋alter，明细指令读库表，调用SAP
     * --4.1.成功：状态变为"修改成功"，回写 result|message，明细库表指令清空，新增的明细回写itemNo
     * --4.2.失败：状态不变，回写 result|message
     */
    public static final String BUSINESS_STATUS_XIUGAIDAITUISONG = "修改待推送";
    /**
     * 状态："修改成功"，明细指令库表无值
     * 1.删除：指令赋delete，明细指令赋delete，调用SAP
     * --1.1.成功：状态变为"删除成功"，回写 result|message，明细库表指令清空
     * --1.2.失败：状态不变，回写 result|message
     * 2.修改：状态变为"修改待推送"，明细指令根据实际修改情况写库表
     * 3.创建SAP：neg
     * 4.修改SAP：neg
     */
    public static final String BUSINESS_STATUS_XIUGAICHEGNGONG = "修改成功";
    /**
     * 状态："删除成功"
     * 1.删除：neg
     * 2.修改：neg
     * 3.创建SAP：neg
     * 4.修改SAP：neg
     */
    public static final String BUSINESS_STATUS_SHANCHUCHENGGONG = "删除成功";

    public static final String OPERATION_ADD = "ADD";//新增指令
    public static final String OPERATION_ALTER = "ALTER";//修改指令
    public static final String OPERATION_DELETE = "DELETE";//删除指令

    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private MaterialProcureME51NDao materialProcureME51NDao;
    @Autowired
    private SysDicManager sysDicManager;

    //..
    public JsonPageResult<?> queryList(HttpServletRequest request, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
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
                    params.put(name, value);
                }
            }
        }
        if (doPage) {
            rdmZhglUtil.addPage(request, params);
        }
        String currentUserId = ContextUtil.getCurrentUserId();
        params.put("currentUserId", currentUserId);
        String currentUserNo = ContextUtil.getCurrentUser().getUserNo();
        // 1、admin和朱佳查看所有，因此不附加条件
        String cxyProjectLookUserNo = WebAppUtil.getProperty("cxyProjectLookUserNo", "");
        boolean wlsqzy = rdmZhglUtil.judgeIsPointRoleUser(ContextUtil.getCurrentUserId(), RdmConst.WLSQZY);
        if (!currentUserNo.equalsIgnoreCase("admin") && !currentUserNo.equalsIgnoreCase(cxyProjectLookUserNo)&&!wlsqzy) {
            params.put("currentUserId", currentUserId);
            // 2、分管领导、管理专员、数据特权人员，也能看所有数据（不考虑草稿）。使用“fgld”参数代表
            Map<String, Object> queryUserParam = new HashMap<>();
            queryUserParam.put("userId", currentUserId);
            queryUserParam.put("groupName", "分管领导");
            List<Map<String, Object>> queryRoleResult = xcmgProjectOtherDao.queryUserRoles(queryUserParam);
            if ((queryRoleResult != null && !queryRoleResult.isEmpty())) {
                params.put("roleName", "fgld");
            } else {
                queryUserParam.clear();
                queryUserParam.put("userId", currentUserId);
                List<String> typeKeyList = new ArrayList<>();
                typeKeyList.add("GROUP-USER-LEADER");
                queryUserParam.put("typeKeyList", typeKeyList);
                List<Map<String, Object>> queryDeptResult = xcmgProjectOtherDao.queryUserDeps(queryUserParam);
                if (queryDeptResult != null && !queryDeptResult.isEmpty()) {
                    // 3、分管主任或部门负责人，查看自己负责部门的、自己创建的，使用“fgzr”参数代表（不考虑草稿）
                    params.put("roleName", "fgzr");
                    judgeUserDeptRole(queryDeptResult, params);
                } else {
                    // 4、其他人员，查看自己创建的，使用"ptyg"参数代表（不考虑草稿）
                    params.put("roleName", "ptyg");
                }
            }
        }
        List<JSONObject> businessList = materialProcureME51NDao.queryList(params);
        List<String> idList = new ArrayList<>();
        for (JSONObject business : businessList) {
            if (business.get("CREATE_TIME_") != null) {
                business.put("CREATE_TIME_",
                        DateUtil.formatDate((Date) business.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
            idList.add(business.getString("id"));
        }
        int countBusinessList = materialProcureME51NDao.countQueryList(params);
        result.setTotal(countBusinessList);
        result.setData(businessList);
        return result;
    }

    //..
    private void judgeUserDeptRole(List<Map<String, Object>> queryDeptResult, Map<String, Object> params) {
        Set<String> deptIdList = new HashSet<>();
        for (Map<String, Object> oneDept : queryDeptResult) {
            String groupId = oneDept.get("PARTY1_").toString();
            deptIdList.add(groupId);
        }
        params.put("deptIds", deptIdList);
    }

    //..
    public List<JSONObject> getItemList(HttpServletRequest request) {
        String mainId = RequestUtil.getString(request, "mainId", "");
        Map<String, Object> params = new HashMap<>();
        params.put("mainId", mainId);
        List<JSONObject> itemList = materialProcureME51NDao.getItemList(params);
        for (JSONObject item : itemList) {
            if (item.get("CREATE_TIME_") != null) {
                item.put("CREATE_TIME_",
                        DateUtil.formatDate((Date) item.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        return itemList;
    }

    //..
    public JSONObject getDetail(String id) {
        JSONObject detailObj = materialProcureME51NDao.getDetail(id);
        if (detailObj == null) {
            return new JSONObject();
        } else {

        }
        return detailObj;
    }

    //..
    public void createOrUpdateApply(JSONObject formDataJson) throws Exception {
        if (StringUtil.isEmpty(formDataJson.getString("id"))) {
            formDataJson.put("id", IdUtil.getId());
            formDataJson.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            formDataJson.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            formDataJson.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            formDataJson.put("businessStatus", this.BUSINESS_STATUS_XINZENGDAITUISONG);//初始状态
            materialProcureME51NDao.insertApply(formDataJson);
            this.createOrUpdateItems(formDataJson);//formDataJson的当前主状态为初始状态，因此放在后面就行
        } else {
            this.createOrUpdateItems(formDataJson);//需要用到formDataJson的当前主状态，因此需要放在前面
            formDataJson.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            formDataJson.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            if (formDataJson.getString("businessStatus").equalsIgnoreCase(this.BUSINESS_STATUS_XINZENGCHENGGONG)
                    && !formDataJson.getJSONArray("itemsChangeData").isEmpty()) {
                //"新增成功"状态下状态变为"修改待推送"
                formDataJson.put("businessStatus", this.BUSINESS_STATUS_XIUGAIDAITUISONG);
            } else if (formDataJson.getString("businessStatus").equalsIgnoreCase(this.BUSINESS_STATUS_XIUGAICHEGNGONG)
                    && !formDataJson.getJSONArray("itemsChangeData").isEmpty()) {
                //"修改成功"状态下状态变为"修改待推送"
                formDataJson.put("businessStatus", this.BUSINESS_STATUS_XIUGAIDAITUISONG);
            }
            materialProcureME51NDao.updateApply(formDataJson);
        }
    }

    //..
    public void createOrUpdateItems(JSONObject formDataJson) throws Exception {
        JSONArray itemsChangeData = formDataJson.getJSONArray("itemsChangeData");
        for (int i = 0; i < itemsChangeData.size(); i++) {
            JSONObject itemJson = itemsChangeData.getJSONObject(i);
            String state = itemJson.getString("_state");
            String itemId = itemJson.getString("id");
            if ("added".equals(state)) {
                if (formDataJson.getString("businessStatus").equalsIgnoreCase(this.BUSINESS_STATUS_XINZENGDAITUISONG)) {
                    //"新增待创建"状态下直接新增,在获取调SAP数据时统一给OPERATION_ADD状态
                } else {
                    //其余状态下明细指令写库表OPERATION_ADD
                    itemJson.put("operation", this.OPERATION_ADD);
                }
                itemJson.put("id", IdUtil.getId());
                itemJson.put("mainId", formDataJson.getString("id"));
                itemJson.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                itemJson.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                materialProcureME51NDao.insertItem(itemJson);
            } else if ("modified".equals(state)) {
                if (formDataJson.getString("businessStatus").equalsIgnoreCase(this.BUSINESS_STATUS_XINZENGDAITUISONG)) {
                    //"新增待创建"状态下直接修改,在获取调SAP数据时统一给OPERATION_ADD状态
                } else {
                    //其余状态下明细指令根据实际修改情况写库表
                    if (StringUtil.isNotEmpty(itemJson.getString("itemNo"))) {
                        //此条明细曾经传过SAP
                        if (itemJson.getBoolean("isDelete")) {
                            //打删除标记的明细指令写库表OPERATION_DELETE
                            itemJson.put("operation", this.OPERATION_DELETE);
                        } else {
                            //没打删除标记的明细指令写库表OPERATION_ALTER
                            itemJson.put("operation", this.OPERATION_ALTER);
                        }
                    } else {
                        //此条明细还从来没有传过SAP，什么都不做，因为它肯定有状态OPERATION_ADD是在第一次新增时赋予的
                    }
                }
                itemJson.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                itemJson.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                materialProcureME51NDao.updateItem(itemJson);
            } else if ("removed".equals(state)) {//"新增待创建"状态下仅有，其他在前端不会作为remove处理
                materialProcureME51NDao.deleteItem(itemId);
            }
        }
    }

    //..直接删除
    public void removeApply(JSONObject formDataJson) throws Exception {
        JSONObject params = new JSONObject();
        params.put("mainId", formDataJson.getString("id"));
        List<JSONObject> items = materialProcureME51NDao.queryListItems(params);
        for (JSONObject item : items) {
            materialProcureME51NDao.deleteItem(item.getString("id"));
        }
        materialProcureME51NDao.deleteApply(formDataJson.getString("id"));
    }

    /**
     * 3.创建SAP：指令赋add，明细指令赋add，调用SAP
     * --3.1.成功：状态变为"新增成功"，回写 applyNo|result|message，明细回写itemNo
     * --3.2.失败：状态不变，回写 result|message
     */
    public JsonResult doCreateToSap(JSONObject formDataJson) throws Exception {
        String createToSapParams = getCreateToSapParams(formDataJson);
        System.out.println(createToSapParams);
        String SAPLock = sysDicManager.getBySysTreeKeyAndDicKey("zhglCallbackUrls", "SAPLock").getValue();
        //JSONObject createToSapResult = createToSapMock(createToSapParams, new Boolean(SAPLock));
        JSONObject createToSapResult = callSap(createToSapParams, new Boolean(SAPLock));
        if (createToSapResult.getBoolean("success")) {
            formDataJson.put("businessStatus", this.BUSINESS_STATUS_XINZENGCHENGGONG);
            formDataJson.put("applyNo", createToSapResult.getString("applyno"));
            formDataJson.put("result", createToSapResult.getString("success"));
            formDataJson.put("message", createToSapResult.getString("message"));
            formDataJson.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            formDataJson.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            materialProcureME51NDao.updateApply(formDataJson);
            JSONObject params = new JSONObject();
            params.put("mainId", formDataJson.getString("id"));
            List<JSONObject> items = materialProcureME51NDao.getItemList(params);
            JSONArray createToSapResultItems = createToSapResult.getJSONArray("data");
            for (JSONObject item : items) {
                for (int j = 0; j < createToSapResultItems.size(); j++) {
                    if (item.getString("materialCode").
                            equalsIgnoreCase(createToSapResultItems.getJSONObject(j).getString("matnr"))) {
                        item.put("itemNo", createToSapResultItems.getJSONObject(j).getString("itemno"));
                        break;
                    }
                }
                item.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                item.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                materialProcureME51NDao.updateItem(item);
            }
            return new JsonResult(true, "创建至SAP成功！");
        } else {
            formDataJson.put("result", createToSapResult.getString("success"));
            formDataJson.put("message", createToSapResult.getString("message"));
            formDataJson.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            formDataJson.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            materialProcureME51NDao.updateApply(formDataJson);
            return new JsonResult(true, "创建至SAP失败！");
        }
    }

    //..指令赋add，明细指令赋add
    private String getCreateToSapParams(JSONObject formDataJson) {
        JSONObject requestParams = new JSONObject();
        requestParams.put("BANFN", formDataJson.getString("applyNo"));
        requestParams.put("OPERATION", this.OPERATION_ADD);
        JSONObject params = new JSONObject();
        params.put("mainId", formDataJson.getString("id"));
        List<JSONObject> itemList = materialProcureME51NDao.getItemList(params);
        JSONArray itemArray = new JSONArray();
        for (JSONObject item : itemList) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("MATNR", item.getString("materialCode"));
            jsonObject.put("MENGE", item.getString("purcureCount"));
            jsonObject.put("WERKS", item.getString("factoryNumber"));
            jsonObject.put("EKORG", item.getString("purcureOrg"));
            jsonObject.put("OPERATION", this.OPERATION_ADD);
            jsonObject.put("ITEMNO", item.getString("itemNo"));
            itemArray.add(jsonObject);
        }
        requestParams.put("ITEM", itemArray);
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(requestParams);
        return jsonArray.toJSONString();
    }

    /**
     * 4.修改SAP：指令赋alter，明细指令读库表，调用SAP
     * --4.1.成功：状态变为"修改成功"，回写 result|message，明细库表指令清空，新增的明细回写itemNo
     * --4.2.失败：状态不变，回写 result|message
     */
    public JsonResult doSendToSap(JSONObject formDataJson) throws Exception {
        String sendToSapParams = getSendToSapParams(formDataJson);
        System.out.println(sendToSapParams);
        String SAPLock = sysDicManager.getBySysTreeKeyAndDicKey("zhglCallbackUrls", "SAPLock").getValue();
        //JSONObject sendToSapResult = sendToSapMock(sendToSapParams, true);
        JSONObject sendToSapResult = callSap(sendToSapParams, new Boolean(SAPLock));
        if (sendToSapResult.getBoolean("success")) {
            formDataJson.put("businessStatus", this.BUSINESS_STATUS_XIUGAICHEGNGONG);
            formDataJson.put("result", sendToSapResult.getString("success"));
            formDataJson.put("message", sendToSapResult.getString("message"));
            formDataJson.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            formDataJson.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            materialProcureME51NDao.updateApply(formDataJson);
            JSONObject params = new JSONObject();
            params.put("mainId", formDataJson.getString("id"));
            List<JSONObject> items = materialProcureME51NDao.getItemList(params);
            JSONArray sendToSapResultItems = sendToSapResult.getJSONArray("data");
            for (JSONObject item : items) {
                for (int j = 0; j < sendToSapResultItems.size(); j++) {
                    if (item.getString("materialCode").
                            equalsIgnoreCase(sendToSapResultItems.getJSONObject(j).getString("matnr")) &&
                            StringUtil.isEmpty(item.getString("itemNo"))) {
                        item.put("itemNo", sendToSapResultItems.getJSONObject(j).getString("itemno"));
                        break;
                    }
                }
                item.put("operation", "");
                item.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                item.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                materialProcureME51NDao.updateItem(item);
            }
            return new JsonResult(true, "更新至SAP成功！");
        } else {
            formDataJson.put("result", sendToSapResult.getString("success"));
            formDataJson.put("message", sendToSapResult.getString("message"));
            formDataJson.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            formDataJson.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            materialProcureME51NDao.updateApply(formDataJson);
            return new JsonResult(true, "更新至SAP失败！");
        }
    }

    //..指令赋alter，明细指令读库表
    private String getSendToSapParams(JSONObject formDataJson) {
        JSONObject requestParams = new JSONObject();
        requestParams.put("BANFN", formDataJson.getString("applyNo"));
        requestParams.put("OPERATION", this.OPERATION_ALTER);
        JSONObject params = new JSONObject();
        params.put("mainId", formDataJson.getString("id"));
        List<JSONObject> itemList = materialProcureME51NDao.getItemList(params);
        JSONArray itemArray = new JSONArray();
        for (JSONObject item : itemList) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("MATNR", item.getString("materialCode"));
            jsonObject.put("MENGE", item.getString("purcureCount"));
            jsonObject.put("WERKS", item.getString("factoryNumber"));
            jsonObject.put("EKORG", item.getString("purcureOrg"));
            jsonObject.put("OPERATION", item.getString("operation"));
            jsonObject.put("ITEMNO", item.getString("itemNo"));
            itemArray.add(jsonObject);
        }
        requestParams.put("ITEM", itemArray);
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(requestParams);
        return jsonArray.toJSONString();
    }

    /**
     * 新增待创建：直接删除
     * 新增成功||修改成功||修改待推送：
     * 1.删除：指令赋delete，明细指令赋delete，调用SAP
     * --1.1.成功：状态变为"删除成功"，回写 result|message，明细库表指令清空
     * --1.2.失败：状态不变，回写 result|message
     */
    public JsonResult doRemoveFromSap(JSONObject formDataJson) throws Exception {
        if (formDataJson.getString("businessStatus").equalsIgnoreCase(this.BUSINESS_STATUS_XINZENGDAITUISONG)) {
            //直接删除
            this.removeApply(formDataJson);
            return new JsonResult(true, "删除成功！");
        } else {
            String removeFromSapParams = getRemoveFromSapParams(formDataJson);
            System.out.println(removeFromSapParams);
            String SAPLock = sysDicManager.getBySysTreeKeyAndDicKey("zhglCallbackUrls", "SAPLock").getValue();
            //JSONObject removeFromSapResult = removeFromSapMock(removeFromSapParams, true);
            JSONObject removeFromSapResult = callSap(removeFromSapParams, new Boolean(SAPLock));
            if (removeFromSapResult.getBoolean("success")) {
                formDataJson.put("businessStatus", this.BUSINESS_STATUS_SHANCHUCHENGGONG);
                formDataJson.put("result", removeFromSapResult.getString("success"));
                formDataJson.put("message", removeFromSapResult.getString("message"));
                formDataJson.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                formDataJson.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                materialProcureME51NDao.updateApply(formDataJson);
                JSONObject params = new JSONObject();
                params.put("mainId", formDataJson.getString("id"));
                List<JSONObject> items = materialProcureME51NDao.getItemList(params);
                for (JSONObject item : items) {
                    item.put("operation", "");
                    item.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    item.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                    materialProcureME51NDao.updateItem(item);
                }
                return new JsonResult(true, "删除SAP成功！");
            } else {
                formDataJson.put("result", removeFromSapResult.getString("success"));
                formDataJson.put("message", removeFromSapResult.getString("message"));
                formDataJson.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                formDataJson.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                materialProcureME51NDao.updateApply(formDataJson);
                return new JsonResult(true, "删除SAP失败！");
            }
        }
    }

    //..指令赋delete，明细指令赋delete
    private String getRemoveFromSapParams(JSONObject formDataJson) {
        JSONObject requestParams = new JSONObject();
        requestParams.put("BANFN", formDataJson.getString("applyNo"));
        requestParams.put("OPERATION", this.OPERATION_DELETE);
        JSONObject params = new JSONObject();
        params.put("mainId", formDataJson.getString("id"));
        List<JSONObject> itemList = materialProcureME51NDao.getItemList(params);
        JSONArray itemArray = new JSONArray();
        for (JSONObject item : itemList) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("MATNR", item.getString("materialCode"));
            jsonObject.put("MENGE", item.getString("purcureCount"));
            jsonObject.put("WERKS", item.getString("factoryNumber"));
            jsonObject.put("EKORG", item.getString("purcureOrg"));
            jsonObject.put("OPERATION", this.OPERATION_DELETE);
            jsonObject.put("ITEMNO", item.getString("itemNo"));
            itemArray.add(jsonObject);
        }
        requestParams.put("ITEM", itemArray);
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(requestParams);
        return jsonArray.toJSONString();
    }

    //..真实发送SAP方法,创建修改删除三合一
    private JSONObject callSap(String createToSapParams, boolean successMock) throws Exception {
        JSONObject result = new JSONObject();
        if (successMock) {
            String ME51NToSapUrl = sysDicManager.getBySysTreeKeyAndDicKey("zhglCallbackUrls", "ME51NToSap").getValue();
            if (StringUtil.isEmpty(ME51NToSapUrl)) {
                result.put("success", false);
                result.put("message", "SAP调用失败，调用地址为空，请联系管理员！");
                return result;
            }
            Map<String, String> reqHeaders = new HashMap<>();
            String rtnContent = HttpClientUtil.postJson(ME51NToSapUrl, createToSapParams, reqHeaders);
            logger.info("response from SAP-ME51N,return message:" + rtnContent);
            if (StringUtil.isEmpty(rtnContent)) {
                result.put("success", false);
                result.put("message", "SAP调用失败，返回值为空，请联系管理员！");
                return result;
            }
            JSONArray returnArray = JSONObject.parseArray(rtnContent);
            if (returnArray.size() == 0) {
                result.put("success", false);
                result.put("message", "SAP调用失败，返回值为空，请联系管理员！");
                return result;
            }
            //一旦有错误，返回值有个特点，每行的错误信息都会被叠加到返回数组的最后一个元素里，所以取最后一个元素是信息最全的
            JSONObject returnJson = returnArray.getJSONObject(returnArray.size() - 1);
            if ("1".equalsIgnoreCase(returnJson.getString("success"))) {
                result.put("success", false);
                result.put("message", "SAP调用失败，原因：" + returnJson.getString("message"));
                return result;
            } else if ("0".equalsIgnoreCase(returnJson.getString("success"))) {
                result.put("success", true);
                result.put("message", returnJson.getString("message"));
                result.put("applyno", returnJson.getString("applyno"));
                result.put("data", returnJson.containsKey("data") ? returnJson.getJSONArray("data") : "[]");
            }
        } else {
            result.put("success", false);
            result.put("message", "SAP调用失败，调用通道关闭，请联系管理员！");
        }
        return result;
    }

    //..SAP创建MOCK方法
    private JSONObject createToSapMock(String createToSapParams, boolean successMock) {
        JSONObject result = new JSONObject();
        JSONArray data = new JSONArray();
        if (successMock) {
            result.put("success", true);
            result.put("message", "SAP端创建成功");
            result.put("applyno", "虚拟采购订单号");
            JSONArray items = ((JSONObject) JSONArray.parseArray(createToSapParams).get(0)).getJSONArray("ITEM");
            for (int i = 0; i < items.size(); i++) {
                JSONObject item = items.getJSONObject(i);
                JSONObject onedata = new JSONObject();
                onedata.put("matnr", item.getString("MATNR"));
                onedata.put("itemno", i);
                data.add(onedata);
            }
        } else {
            result.put("success", false);
            result.put("message", "SAP端创建失败");
        }
        result.put("data", data);
        return result;
    }

    //..SAP修改MOCK方法
    private JSONObject sendToSapMock(String sendToSapParams, boolean successMock) {
        JSONObject result = new JSONObject();
        JSONArray data = new JSONArray();
        if (successMock) {
            result.put("success", true);
            result.put("message", "SAP端更新成功");
            result.put("applyno", ((JSONObject) JSONArray.parseArray(sendToSapParams).get(0)).getString("BANFN"));
            JSONArray items = ((JSONObject) JSONArray.parseArray(sendToSapParams).get(0)).getJSONArray("ITEM");
            int itemNoMax = 0;
            for (int i = 0; i < items.size(); i++) {
                JSONObject item = items.getJSONObject(i);
                int now = StringUtil.isEmpty(item.getString("ITEMNO")) ? 0 : item.getInteger("ITEMNO");
                if (itemNoMax < now) {
                    itemNoMax = now;
                }
            }
            for (int i = 0; i < items.size(); i++) {
                JSONObject item = items.getJSONObject(i);
                if (StringUtil.isEmpty(item.getString("ITEMNO"))) {
                    JSONObject onedata = new JSONObject();
                    onedata.put("matnr", item.getString("MATNR"));
                    onedata.put("itemno", ++itemNoMax);
                    data.add(onedata);
                }
            }
        } else {
            result.put("success", false);
            result.put("message", "SAP端更新失败");
            result.put("applyNo", ((JSONObject) JSONArray.parseArray(sendToSapParams).get(0)).getString("BANFN"));
        }
        result.put("data", data);
        return result;
    }

    //..SAP删除MOCK方法
    private JSONObject removeFromSapMock(String removeFromSapParams, boolean successMock) {
        JSONObject result = new JSONObject();
        JSONArray data = new JSONArray();
        if (successMock) {
            result.put("success", true);
            result.put("message", "SAP端删除成功");
            result.put("applyno", ((JSONObject) JSONArray.parseArray(removeFromSapParams).get(0)).getString("BANFN"));
        } else {
            result.put("success", false);
            result.put("message", "SAP端删除失败");
            result.put("applyno", ((JSONObject) JSONArray.parseArray(removeFromSapParams).get(0)).getString("BANFN"));
        }
        result.put("data", data);
        return result;
    }
}
