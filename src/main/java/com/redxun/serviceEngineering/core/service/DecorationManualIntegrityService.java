package com.redxun.serviceEngineering.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.excel.ExcelReaderUtil;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.ExcelUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.serviceEngineering.core.dao.DecorationManualIntegrityDao;
import com.redxun.serviceEngineering.core.dao.DecorationManualIntegrityWarehouseDao;
import com.redxun.serviceEngineering.core.dao.WgjzlsjDao;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.sys.core.manager.SysSeqIdManager;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import javafx.beans.binding.ObjectExpression;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.*;
import java.text.DecimalFormat;

@Service
public class DecorationManualIntegrityService {
    private static Logger logger = LoggerFactory.getLogger(DecorationManualIntegrityService.class);
    @Autowired
    private DecorationManualIntegrityDao decorationManualIntegrityDao;
    @Autowired
    private DecorationManualIntegrityWarehouseDao decorationManualIntegrityWarehouseDao;
    @Autowired
    private WgjzlsjDao wgjzlsjDao;

    @Autowired
    private XcmgProjectManager xcmgProjectManager;

    @Autowired
    private SysSeqIdManager sysSeqIdManager;

    //..
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        CommonFuns.getSearchParam(params, request, true);

        List<Map<String, Object>> businessList = decorationManualIntegrityDao.dataListQuery(params);
        for (Map<String, Object> business : businessList) {
            if (business.get("CREATE_TIME_") != null) {
                business.put("CREATE_TIME_", DateUtil.formatDate((Date) business.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
            if (business.get("UPDATE_TIME_") != null) {
                business.put("UPDATE_TIME_", DateUtil.formatDate((Date) business.get("UPDATE_TIME_"), "yyyy-MM-dd"));
            }
            if(business.get("totalNum") !=null && business.get("useNum") !=null){
                int useNum= (Integer) business.get("useNum");
                int totalNum =(Integer) business.get("totalNum");
                DecimalFormat df = new DecimalFormat("#.##%");
                String collectorUse="";
                String formatted="";
                if(totalNum > 0){
                    formatted = df.format((double)useNum/totalNum);
                    collectorUse = useNum+"/"+totalNum+ "(" + formatted + ")";
                }else{
                    collectorUse = useNum+"/"+totalNum;
                }

                business.put("collectorUse",collectorUse);
            }
            if(business.get("totalNum") !=null && business.get("repairNum") !=null){
                int repairNum= (Integer) business.get("repairNum");
                int totalNum =(Integer) business.get("totalNum");
                DecimalFormat df = new DecimalFormat("#.##%");
                String formatted="";
                String collectorRepair="";
                if(totalNum >0){
                    formatted = df.format((double)repairNum/totalNum);
                    collectorRepair = repairNum+"/"+totalNum+ "(" + formatted + ")";
                }else{
                    collectorRepair = repairNum+"/"+totalNum;
                }

                business.put("collectorRepair",collectorRepair);
            }
        }
        // 查询当前处理人
        xcmgProjectManager.setTaskCurrentUser(businessList);
        int businessListCount = decorationManualIntegrityDao.countDataListQuery(params);
        result.setData(businessList);
        result.setTotal(businessListCount);
        return result;
    }

    public JsonPageResult<?> warehouseDataListQuery(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        CommonFuns.getSearchParam(params, request, true);

        List<Map<String, Object>> businessList = decorationManualIntegrityWarehouseDao.warehouseDataListQuery(params);
        for (Map<String, Object> business : businessList) {
            if (business.get("CREATE_TIME_") != null) {
                business.put("CREATE_TIME_", DateUtil.formatDate((Date) business.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
            if (business.get("UPDATE_TIME_") != null) {
                business.put("UPDATE_TIME_", DateUtil.formatDate((Date) business.get("UPDATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        // 查询当前处理人
        xcmgProjectManager.setTaskCurrentUser(businessList);
        int businessListCount = decorationManualIntegrityWarehouseDao.countWarehouseDataListQuery(params);
        result.setData(businessList);
        result.setTotal(businessListCount);
        return result;
    }


    //..
    public JsonResult deleteBusiness(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> businessIds = Arrays.asList(ids);

        Map<String, Object> param = new HashMap<>();
        param.put("businessIds", businessIds);
        decorationManualIntegrityDao.deleteBusiness(param);
        param.clear();
        JSONObject params = new JSONObject();
        params.put("mainIds",businessIds);
        decorationManualIntegrityDao.deleteItems(params);
        return result;
    }



    //..
    public JSONObject getDetail(String businessId) {
        JSONObject jsonObject = decorationManualIntegrityDao.queryDetailById(businessId);
        if (jsonObject == null) {
            jsonObject = new JSONObject();
            jsonObject.put("applyUserId", ContextUtil.getCurrentUser().getUserId());
            jsonObject.put("applyUser", ContextUtil.getCurrentUser().getFullname());
            jsonObject.put("applyDepId", ContextUtil.getCurrentUser().getMainGroupId());
            jsonObject.put("applyDep", ContextUtil.getCurrentUser().getMainGroupName());
        }
        if (jsonObject.get("CREATE_TIME_") != null) {
            jsonObject.put("CREATE_TIME_", DateUtil.formatDate((Date)jsonObject.get("CREATE_TIME_"), "yyyy-MM-dd"));
        }
        return jsonObject;
    }


    //..
    public List<JSONObject> getItemList(JSONObject params) throws Exception {
        return decorationManualIntegrityDao.getItemList(params);
    }

    public JsonResult saveBusiness(JSONObject formData) throws Exception {
        JsonResult result = new JsonResult(true, "操作成功！");
        if (StringUtil.isEmpty(formData.getString("id"))) {
            formData.put("id", IdUtil.getId());
            formData.put("applyUserId", ContextUtil.getCurrentUserId());
            formData.put("applyUser", ContextUtil.getCurrentUser().getFullname());
            formData.put("applyDepId", ContextUtil.getCurrentUser().getMainGroupId());
            formData.put("applyDep", ContextUtil.getCurrentUser().getMainGroupName());
            if(StringUtil.isEmpty(formData.getString("totalNum"))){
                formData.put("totalNum", 0);
                formData.put("useNum", 0);
                formData.put("repairNum", 0);
            }

            //@zwt:自动生成编号
            if (StringUtil.isEmpty(formData.getString("businessNo"))) {
                formData.put("businessNo", sysSeqIdManager.genSequenceNo("decorationManualIntegrity", ContextUtil.getCurrentTenantId()));
            }
            formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            formData.put("CREATE_TIME_", new Date());
            this.processItems(formData);
            decorationManualIntegrityDao.insertBusiness(formData);
        } else {
            formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            formData.put("UPDATE_TIME_", new Date());
            this.processItems(formData);
            decorationManualIntegrityDao.updateBusiness(formData);
        }
        result.setData(formData.getString("id"));
        return result;
    }
    //..
    private void processItems(JSONObject jsonObject) throws Exception {
        if (jsonObject.containsKey("items")
                && StringUtil.isNotEmpty(jsonObject.getString("items"))) {
            JSONArray jsonArrayItemData = jsonObject.getJSONArray("items");
            List<String> removeItemIds = new ArrayList<>();

            int totalNum = 0;
            int useNum = 0;
            int repairNum = 0;
            if(StringUtils.isNotBlank(jsonObject.getString("totalNum")) ){
                totalNum =jsonObject.getInteger("totalNum");
            }else{
                jsonObject.put("totalNum",0);
            }
            if(StringUtils.isNotBlank(jsonObject.getString("useNum") )){
                useNum =jsonObject.getInteger("useNum");
            }else{
                jsonObject.put("useNum",0);
            }
            if(StringUtils.isNotBlank(jsonObject.getString("repairNum") )){
                useNum =jsonObject.getInteger("repairNum");
            }else{
                jsonObject.put("repairNum",0);
            }

            for (Object item : jsonArrayItemData) {
                JSONObject itemJson = (JSONObject) item;
                if (itemJson.containsKey("_state")) {//处理新增和修改
                    if (itemJson.getString("_state").equalsIgnoreCase("added")) {
                        itemJson.put("id", IdUtil.getId());
                        itemJson.put("mainId", jsonObject.getString("id"));
                        itemJson.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                        itemJson.put("CREATE_TIME_", new Date());
                        if(StringUtils.isNotBlank(itemJson.getString("materialCode"))){
                            String materialCode =itemJson.getString("materialCode");
                            Map<String, Object> params = new HashMap<>();
                            params.put("materialCode",materialCode);
                            params.put("dataType","维修手册类");
                            String repairBpmStatus= queryBpmStatus(params);
                            itemJson.put("repairBpmStatus",repairBpmStatus);
                            params.put("dataType","使用说明");
                            String useBpmStatus = queryBpmStatus(params);
                            itemJson.put("useBpmStatus",useBpmStatus);

                        }
                        //计算使用说明书数
                        if(StringUtils.isNotBlank(itemJson.getString("useTopicCode"))){
                            useNum =useNum +1;
                        }
                        //计算维修要领书数
                        if(StringUtils.isNotBlank(itemJson.getString("repairTopicCode"))){
                            repairNum =repairNum +1;
                        }
                        totalNum = totalNum +1;
                        jsonObject.put("useNum",useNum);
                        jsonObject.put("repairNum",repairNum);
                        jsonObject.put("totalNum",totalNum);
                        decorationManualIntegrityDao.insertItem(itemJson);
                        processStoreItem(itemJson);
                        continue;
                    } else if (itemJson.getString("_state").equalsIgnoreCase("modified")) {
                        itemJson.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                        itemJson.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                        if(StringUtils.isNotBlank(itemJson.getString("materialCode"))){
                            String materialCode =itemJson.getString("materialCode");
                            Map<String, Object> params = new HashMap<>();
                            params.put("materialCode",materialCode);
                            params.put("dataType","维修手册类");
                            String repairBpmStatus= queryBpmStatus(params);
                            itemJson.put("repairBpmStatus",repairBpmStatus);
                            params.put("dataType","使用说明");
                            String useBpmStatus = queryBpmStatus(params);
                            itemJson.put("useBpmStatus",useBpmStatus);
                            //更新usenum数量
                            JSONObject storeItem = decorationManualIntegrityWarehouseDao.queryByMaterialCode(materialCode);
                            if(storeItem !=null &&StringUtils.isBlank(storeItem.getString("useTopicCode")) &&StringUtils.isNotBlank(itemJson.getString("useTopicCode"))){
                                if(jsonObject.getInteger("useNum") !=null)
                                {
                                    jsonObject.put("useNum",jsonObject.getInteger("useNum")+1);
                                }else {
                                    jsonObject.put("useNum",1);
                                }
                            }
                            if(storeItem !=null &&StringUtils.isNotBlank(storeItem.getString("useTopicCode")) &&StringUtils.isBlank(itemJson.getString("useTopicCode"))) {
                                if(jsonObject.getInteger("useNum") !=null)
                                {
                                    jsonObject.put("useNum",jsonObject.getInteger("useNum")-1);
                                }else {
                                    jsonObject.put("useNum",0);
                                }
                            }
                            if(storeItem !=null&&StringUtils.isBlank(storeItem.getString("repairTopicCode")) &&StringUtils.isNotBlank(itemJson.getString("repairTopicCode"))){
                                if(jsonObject.getInteger("repairNum") !=null)
                                {
                                    jsonObject.put("repairNum",jsonObject.getInteger("repairNum")+1);
                                }else {
                                    jsonObject.put("repairNum",1);
                                }

                            }
                            if(storeItem !=null&&StringUtils.isNotBlank(storeItem.getString("repairTopicCode")) &&StringUtils.isBlank(itemJson.getString("repairTopicCode"))){
                                if(jsonObject.getInteger("repairNum") !=null)
                                {
                                    jsonObject.put("repairNum",jsonObject.getInteger("repairNum")-1);
                                }else {
                                    jsonObject.put("repairNum",0);
                                }
                            }

                        }
                        decorationManualIntegrityDao.updateItem(itemJson);
                        processStoreItem(itemJson);
                        continue;
                    } else if (itemJson.getString("_state").equalsIgnoreCase("removed")) {
                        //计算使用说明书数
                        if(StringUtils.isNotBlank(itemJson.getString("useTopicCode"))){
                            useNum =useNum -1;
                        }
                        //计算维修要领书数
                        if(StringUtils.isNotBlank(itemJson.getString("repairTopicCode"))){
                            repairNum =repairNum -1;
                        }
                        totalNum = totalNum - 1;
                        jsonObject.put("useNum",useNum);
                        jsonObject.put("repairNum",repairNum);
                        jsonObject.put("totalNum",totalNum);
                        removeItemIds.add(itemJson.getString("id"));
                    }
                }
            }
            JSONObject params = new JSONObject();
            params.put("ids", removeItemIds);
            decorationManualIntegrityDao.deleteItems(params);//统一处理删除
            removeItemIds.clear();
        }
    }

    //添加或更新库
    private void processStoreItem(JSONObject jsonObject) throws Exception{
        if(StringUtils.isNotBlank(jsonObject.getString("materialCode"))){
            JSONObject itemJson = decorationManualIntegrityWarehouseDao.queryByMaterialCode(jsonObject.getString("materialCode"));
            if(itemJson == null){
                decorationManualIntegrityWarehouseDao.insertWarehouse(jsonObject);
            }else
            {
                jsonObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                jsonObject.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                decorationManualIntegrityWarehouseDao.updateWarehouse(jsonObject);
            }

        }
    }

    public String queryBpmStatus(Map<String, Object> params) {
        List<Map<String, Object>> wgjzlsjList = wgjzlsjDao.dataListQuery(params);
        String BpmStatus ="";
        if (!wgjzlsjList.isEmpty()) {
            String taskStatus = wgjzlsjList.get(0).get("taskStatus").toString();
            if (taskStatus.equalsIgnoreCase("DRAFTED")) {
                taskStatus = "草稿;\n";
            } else if (taskStatus.equalsIgnoreCase("RUNNING")) {
                taskStatus = "审批中;\n";
            } else if (taskStatus.equalsIgnoreCase("SUCCESS_END")) {
                taskStatus = "批准;\n";
            } else if (taskStatus.equalsIgnoreCase("DISCARD_END")) {
                taskStatus = "作废;\n";
            }
            String businessNo="";
            if(wgjzlsjList != null &&wgjzlsjList.get(0).get("businessNo")!=null){
                businessNo = wgjzlsjList.get(0).get("businessNo").toString();
            }

            String fwgcPrincipal = "";
            if(wgjzlsjList != null &&wgjzlsjList.get(0).get("fwgcPrincipal")!=null){
                fwgcPrincipal = wgjzlsjList.get(0).get("fwgcPrincipal").toString();
            }

            BpmStatus = taskStatus + fwgcPrincipal+ ";\n" +  businessNo;

        }else {
            BpmStatus ="需要提交申请流程\n";
        }
        return BpmStatus;
    }








    public JSONObject getMaterialCodeDetail(String materialCode) {
        JSONObject result = new JSONObject();
        if (StringUtils.isBlank(materialCode)) {
            return result;
        }
        JSONObject storeItem = decorationManualIntegrityWarehouseDao.queryByMaterialCode(materialCode);
        if(storeItem !=null){
            result =storeItem;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("materialCode",materialCode);
        params.put("dataType","维修手册类");
        List<Map<String, Object>> wgjzlsjList = wgjzlsjDao.dataListQuery(params);
        String BpmStatus ="";
        if (!wgjzlsjList.isEmpty()) {
            String taskStatus = wgjzlsjList.get(0).get("taskStatus").toString();
            if (taskStatus.equalsIgnoreCase("DRAFTED")) {
                taskStatus = "草稿;\n";
            } else if (taskStatus.equalsIgnoreCase("RUNNING")) {
                taskStatus = "审批中;\n";
            } else if (taskStatus.equalsIgnoreCase("SUCCESS_END")) {
                taskStatus = "批准;\n";
            } else if (taskStatus.equalsIgnoreCase("DISCARD_END")) {
                taskStatus = "作废;\n";
            }
            String businessNo = wgjzlsjList.get(0).get("businessNo").toString();
            String fwgcPrincipal = wgjzlsjList.get(0).get("fwgcPrincipal").toString();
            BpmStatus = taskStatus + fwgcPrincipal + ";\n" +  businessNo;
            String materialName = wgjzlsjList.get(0).get("materialName").toString();
            String materialDescription = wgjzlsjList.get(0).get("materialDescription").toString();
            result.put("materialName",materialName);
            result.put("materialDesc",materialDescription);

        }else {
            BpmStatus ="需要提交申请流程\n";
        }
        result.put("repairBpmStatus",BpmStatus);
        params.put("dataType","使用说明");
        List<Map<String, Object>> wgjzlsjList2 = wgjzlsjDao.dataListQuery(params);
        String useBpmStatus="";
        if (!wgjzlsjList2.isEmpty()) {
            String taskStatus = wgjzlsjList2.get(0).get("taskStatus").toString();
            if (taskStatus.equalsIgnoreCase("DRAFTED")) {
                taskStatus = "草稿;\n";
            } else if (taskStatus.equalsIgnoreCase("RUNNING")) {
                taskStatus = "审批中;\n";
            } else if (taskStatus.equalsIgnoreCase("SUCCESS_END")) {
                taskStatus = "批准;\n";
            } else if (taskStatus.equalsIgnoreCase("DISCARD_END")) {
                taskStatus = "作废;\n";
            }
            String businessNo = wgjzlsjList2.get(0).get("businessNo").toString();
            String fwgcPrincipal = wgjzlsjList2.get(0).get("fwgcPrincipal").toString();
            useBpmStatus = taskStatus + businessNo + ";" + fwgcPrincipal;
            String materialName = wgjzlsjList2.get(0).get("materialName").toString();
            String materialDescription = wgjzlsjList2.get(0).get("materialDescription").toString();
            result.put("materialName",materialName);
            result.put("materialDesc",materialDescription);

        }else {
            useBpmStatus ="需要提交申请流程\n";
        }
        result.put("useBpmStatus",useBpmStatus);
        return result;
    }


    //..
    public JsonResult storeDataListFresh() {
        JsonResult result = new JsonResult(true, "刷新成功！");
        Map<String, Object> params = new HashMap<>();
        List<Map<String, Object>> businessList = decorationManualIntegrityWarehouseDao.warehouseDataListQuery(params);
        for (Map<String, Object> business : businessList){
            String materialCode =business.get("materialCode").toString();
            params.clear();
            params.put("materialCode",materialCode);
            params.put("dataType","维修手册类");
            List<Map<String, Object>> wgjzlsjList = wgjzlsjDao.dataListQuery(params);
            String BpmStatus ="";
            if (!wgjzlsjList.isEmpty()) {
                String taskStatus = wgjzlsjList.get(0).get("taskStatus").toString();
                if (taskStatus.equalsIgnoreCase("DRAFTED")) {
                    taskStatus = "草稿;\n";
                } else if (taskStatus.equalsIgnoreCase("RUNNING")) {
                    taskStatus = "审批中;\n";
                } else if (taskStatus.equalsIgnoreCase("SUCCESS_END")) {
                    taskStatus = "批准;\n";
                } else if (taskStatus.equalsIgnoreCase("DISCARD_END")) {
                    taskStatus = "作废;\n";
                }
                String businessNo = wgjzlsjList.get(0).get("businessNo").toString();
                String fwgcPrincipal = wgjzlsjList.get(0).get("fwgcPrincipal").toString();
                BpmStatus = taskStatus + fwgcPrincipal + ";\n" +  businessNo;

            }else {
                BpmStatus ="需要提交申请流程\n";
            }
            business.put("repairBpmStatus",BpmStatus);

            params.put("dataType","使用说明");
            List<Map<String, Object>> wgjzlsjList2 = wgjzlsjDao.dataListQuery(params);
            String useBpmStatus="";
            if (!wgjzlsjList2.isEmpty()) {
                String taskStatus = wgjzlsjList2.get(0).get("taskStatus").toString();
                if (taskStatus.equalsIgnoreCase("DRAFTED")) {
                    taskStatus = "草稿;\n";
                } else if (taskStatus.equalsIgnoreCase("RUNNING")) {
                    taskStatus = "审批中;\n";
                } else if (taskStatus.equalsIgnoreCase("SUCCESS_END")) {
                    taskStatus = "批准;\n";
                } else if (taskStatus.equalsIgnoreCase("DISCARD_END")) {
                    taskStatus = "作废;\n";
                }
                String businessNo = wgjzlsjList2.get(0).get("businessNo").toString();
                String fwgcPrincipal = wgjzlsjList2.get(0).get("fwgcPrincipal").toString();
                useBpmStatus = taskStatus +  fwgcPrincipal+ ";\n" + businessNo;

            }else {
                useBpmStatus ="需要提交申请流程\n";
            }
            business.put("repairBpmStatus",useBpmStatus);
            decorationManualIntegrityWarehouseDao.updateWareItem(business);
        }
        return result;
    }


    //..
    public void exportStoreExcel(HttpServletRequest request, HttpServletResponse response) {

        JSONObject params = new JSONObject();
        List<Map<String, Object>> businessList = decorationManualIntegrityWarehouseDao.warehouseDataListQuery(params);
        for (Map<String, Object> business : businessList) {
            if (business.get("CREATE_TIME_") != null) {
                business.put("CREATE_TIME_", DateUtil.formatDate((Date) business.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
            if (business.get("UPDATE_TIME_") != null) {
                business.put("UPDATE_TIME_", DateUtil.formatDate((Date) business.get("UPDATE_TIME_"), "yyyy-MM-dd"));
            }
        }

        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title =  "装修手册完整性登记库表";
        String excelName = nowDate + title;
        String[] fieldNames = {"物料编码", "物料名称", "物料描述", "使用说明书", "Topic编码",
                "RDM外购件资料收集流程状态", "维修要领书", "Topic编码", "RDM外购件资料收集流程状态"};
        String[] fieldCodes = { "materialCode", "materialName", "materialDesc", "useDescBook", "useTopicCode",
                "useBpmStatus", "repairBook", "repairTopicCode", "repairBpmStatus"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(businessList, fieldNames, fieldCodes, title);
        //输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);

    }

    //..
    public JsonResult deleteStoreItem(String[] ids) throws Exception {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> idList = Arrays.asList(ids);
        JSONObject params = new JSONObject();
        params.put("businessIds", idList);
        decorationManualIntegrityWarehouseDao.deleteStoreItem(params);//删仓库中条目
        params.clear();
        return result;
    }
}
