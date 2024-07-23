package com.redxun.componentTest.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.componentTest.core.dao.ComponentTestApplyDao;
import com.redxun.componentTest.core.dao.ComponentTestKanbanDao;
import com.redxun.core.excel.ExcelReaderUtil;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.ExcelUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.materielextend.core.service.MaterielService;
import com.redxun.org.api.model.IUser;
import com.redxun.org.api.service.UserService;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmCommon.core.util.RdmCommonUtil;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.sys.core.entity.SysDic;
import com.redxun.sys.core.entity.SysTree;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.sys.core.manager.SysSeqIdManager;
import com.redxun.sys.core.manager.SysTreeManager;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

@Service
public class ComponentTestApplyService {
    private static Logger logger = LoggerFactory.getLogger(ComponentTestApplyService.class);
    @Autowired
    private ComponentTestKanbanDao componentTestKanbanDao;
    @Autowired
    private ComponentTestApplyDao componentTestApplyDao;
    @Autowired
    private SysDicManager sysDicManager;
    @Autowired
    private SysSeqIdManager sysSeqIdManager;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private SysTreeManager sysTreeManager;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;
    @Autowired
    private BpmInstManager bpmInstManager;
    @Autowired
    private UserService userService;

    //..
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        getListParams(params, request);
        //--以下数据权限处理
        //admin/分管领导/零部件试验特权人员(qinqiang,xiaoyunbo)看所有/todo 所长看本部门的
        //普通人看自己相关的
        String currentUserId = ContextUtil.getCurrentUserId();
        String currentUserNo = ContextUtil.getCurrentUser().getUserNo();
        boolean isComponentTestResultAll = false;
        String componentTestResultAll = sysDicManager.getBySysTreeKeyAndDicKey(
                "componentTestManGroups", "ComponentTestResultAll").getValue();
        List<String> componentTestResultAllList = Arrays.asList(componentTestResultAll.split(","));
        for (String userNo : componentTestResultAllList) {
            if (currentUserNo.equalsIgnoreCase(userNo)) {
                isComponentTestResultAll = true;
                break;
            }
        }
        if (!currentUserNo.equalsIgnoreCase("admin") && !isComponentTestResultAll) {
            params.put("currentUserId", currentUserId);
            // 查询角色是否为分管领导
            Map<String, Object> queryUserParam = new HashMap<>();
            queryUserParam.put("userId", currentUserId);
            queryUserParam.put("groupName", RdmConst.FGLD);
            List<Map<String, Object>> queryRoleResult = xcmgProjectOtherDao.queryUserRoles(queryUserParam);
            if (queryRoleResult != null && !queryRoleResult.isEmpty()) {//分管领导
                params.put("roleName", "fgld");
            } else if (rdmZhglUtil.judgeIsPointRoleUser(currentUserId, RdmConst.AllDATA_QUERY_NAME)) {
                params.put("roleName", "fgld");
            } else if (ContextUtil.getCurrentUser().getMainGroupName().equalsIgnoreCase(RdmConst.GCZX_NAME)) {
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
        //--以上数据权限处理
        List<JSONObject> businessList = componentTestApplyDao.dataListQuery(params);
        int businessListCount = componentTestApplyDao.countDataListQuery(params);
        // 查询当前处理人
        xcmgProjectManager.setTaskCurrentUserJSON(businessList);
        //处理多型字段
        Set<String> testItemsSet = new LinkedHashSet<>();//测试项目
        Set<String> itemsOfTestItemSet = new LinkedHashSet<>();//测试项次
        for (JSONObject oneObj : businessList) {
            //获取合同名称列表
            List<String> testContractNameList = componentTestKanbanDao.getTestContractNameListByBusinessId(oneObj.getString("id"));
            //如果有合同名称
            if (testContractNameList.size() > 0) {
                StringBuilder stringBuilder = new StringBuilder();
                for (String fileName : testContractNameList) {
                    stringBuilder.append("《").append(fileName).append("》");
                }
                oneObj.put("testContract", stringBuilder.toString());
            }
            //获取检测报告名称列表
            List<String> testReportNameList = componentTestKanbanDao.getTestReportNameListByBusinessId(oneObj.getString("id"));
            //如果有检测报告名称
            if (testReportNameList.size() > 0) {
                StringBuilder stringBuilder = new StringBuilder();
                for (String fileName : testReportNameList) {
                    stringBuilder.append("《").append(fileName).append("》");
                }
                oneObj.put("testReport", stringBuilder.toString());
            }
            //领料人关联显示
            if (StringUtil.isNotEmpty(oneObj.getString("sampleSource")) &&
                    oneObj.getString("sampleSource").equalsIgnoreCase("厂内领料")) {
                oneObj.put("sampleSource", oneObj.getString("sampleSource") + "(" + oneObj.getString("receiver") + ")");
            }
            //以上领料人关联显示
            //以下处理testItems itemsOfTestItem
            JSONArray jsonArray = oneObj.getJSONArray("testItemsJson");
            if (jsonArray != null && !jsonArray.isEmpty()) {
                for (Object object : jsonArray) {
                    JSONObject jsonObject = (JSONObject) object;
                    testItemsSet.add(jsonObject.getString("testItem_item"));
                    itemsOfTestItemSet.add(jsonObject.getString("itemOfTestItem_item"));
                }
            }
            JSONArray jsonArrayNonStandard = oneObj.getJSONArray("testItemsNonStandardJson");
            if (jsonArrayNonStandard != null && !jsonArrayNonStandard.isEmpty()) {
                for (Object object : jsonArrayNonStandard) {
                    JSONObject jsonObject = (JSONObject) object;
                    testItemsSet.add(jsonObject.getString("testItem_item"));
                    itemsOfTestItemSet.add(jsonObject.getString("itemOfTestItem_item"));
                }
            }
            StringBuilder stringBuilder = new StringBuilder();
            for (String s : testItemsSet) {
                stringBuilder.append(s).append(",");
            }
            if (stringBuilder.length() != 0) {
                stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());
                oneObj.put("testItems", stringBuilder.toString());
            }
            stringBuilder = new StringBuilder();
            for (String s : itemsOfTestItemSet) {
                stringBuilder.append(s).append(",");
            }
            if (stringBuilder.length() != 0) {
                stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());
                oneObj.put("itemsOfTestItem", stringBuilder.toString());
            }
            testItemsSet.clear();//清空测试项目
            itemsOfTestItemSet.clear();//清空测试项次
        }
        result.setData(businessList);
        result.setTotal(businessListCount);
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
    private void getListParams(Map<String, Object> params, HttpServletRequest request) {
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)) {
            params.put("sortField", sortField);
            params.put("sortOrder", sortOrder);
        }
        if (StringUtils.isEmpty(sortField)) {
            //params.put("sortField", "applyStatus asc,sqCommitTime desc");
            params.put("sortField", "applyTime");
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

    //..
    public JSONObject queryDataById(String businessId) {
        JSONObject result = new JSONObject();
        if (StringUtil.isEmpty(businessId)) {
            result.put("applyUserId", ContextUtil.getCurrentUserId());
            result.put("applyUser", ContextUtil.getCurrentUser().getFullname());
            result.put("applyDepId", ContextUtil.getCurrentUser().getMainGroupId());
            result.put("applyDep", ContextUtil.getCurrentUser().getMainGroupName());
            result.put("applyTime", DateUtil.formatDate(new Date(), DateUtil.DATE_FORMAT_YMD));
            result.put("testStatus", "未开展");
            result.put("testProgress", "0");
            result.put("testItemsJson", "[]");
            result.put("testItemsNonStandardJson", "[]");
            return result;
        }
        JSONObject jsonObject = componentTestApplyDao.queryDataById(businessId);
        if (jsonObject == null) {
            return result;
        }
        return jsonObject;
    }

    //..
    public JsonResult saveBusiness(JSONObject jsonObject) throws Exception {
        JsonResult result = new JsonResult(true, "操作成功！");
        String[] strings = new String[]{"applyTime", "hopeTestMonth",
                "samplePlanTime", "actualTestMonth", "completeTestMonth"};
        RdmCommonUtil.formatJsonObjectStringDateField(strings, jsonObject);
        String key = jsonObject.getString("componentCategory");
        if (StringUtil.isNotEmpty(key)) {
            String testMajorLeaderNo = sysDicManager.getBySysTreeKeyAndDicKey("componentCategory-testMajorLeader", key).getValue();
            IUser user = userService.getByUsername(testMajorLeaderNo);
            if (user != null) {
                jsonObject.put("testMajorLeaderId", user.getUserId());
                jsonObject.put("testMajorLeader", user.getFullname());
            } else {
                jsonObject.put("testMajorLeaderId", "");
                jsonObject.put("testMajorLeader", "");
            }
        }
        if (StringUtil.isEmpty(jsonObject.getString("testNo")) &&
                StringUtil.isNotEmpty(jsonObject.getString("actualTestMonth"))) {
            //todo:改为流程节点A4后驱动
            //jsonObject.put("testNo", this.getSysSeq(jsonObject));
        }
        if (StringUtil.isEmpty(jsonObject.getString("id"))) {
            jsonObject.put("id", IdUtil.getId());
            jsonObject.put("componentTestAbnormalTag", "true");
            jsonObject.put("businessStatus", "A1");//给个初始化状态
            jsonObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            jsonObject.put("CREATE_TIME_", new Date());
            this.processTestItemsJson(jsonObject);
            this.processTestItemsNonStandardJson(jsonObject);
            componentTestApplyDao.insertBusiness(jsonObject);
        } else {
            jsonObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            jsonObject.put("UPDATE_TIME_", new Date());
            this.processTestItemsJson(jsonObject);
            this.processTestItemsNonStandardJson(jsonObject);
            this.processTestProgressByTestItemsJson(jsonObject);
            componentTestApplyDao.updateBusiness(jsonObject);
        }
        result.setData(jsonObject.getString("id"));
        return result;
    }

    //..testItems->testItemsJson
    private void processTestItemsJson(JSONObject jsonObject) throws Exception {
        if (jsonObject.containsKey("testItems")
                && StringUtil.isNotEmpty(jsonObject.getString("testItems"))) {
            JSONArray jsonArray = new JSONArray();
            JSONArray jsonArrayItemData = jsonObject.getJSONArray("testItems");
            for (Object itemDataObject : jsonArrayItemData) {
                JSONObject itemDataJson = (JSONObject) itemDataObject;
                if (itemDataJson.containsKey("_state")) {//处理新增,修改
                    if (itemDataJson.getString("_state").equalsIgnoreCase("added")) {
                        itemDataJson.remove("_state");
                        itemDataJson.remove("_id");
                        itemDataJson.remove("_uid");
                        itemDataJson.put("id", IdUtil.getId());
                        jsonArray.add(itemDataJson);
                    } else if (itemDataJson.getString("_state").equalsIgnoreCase("modified")) {
                        itemDataJson.remove("_state");
                        itemDataJson.remove("_id");
                        itemDataJson.remove("_uid");
                        jsonArray.add(itemDataJson);
                    }
                } else {//处理没变化的
                    itemDataJson.remove("_state");
                    itemDataJson.remove("_id");
                    itemDataJson.remove("_uid");
                    jsonArray.add(itemDataJson);
                }
            }
            jsonObject.put("testItemsJson", jsonArray.toString());
        } else if (!jsonObject.containsKey("testItemsJson")) {
            jsonObject.put("testItemsJson", "[]");
        }
    }

    //..testItemsNonStandard->testItemsNonStandardJson
    private void processTestItemsNonStandardJson(JSONObject jsonObject) throws Exception {
        if (jsonObject.containsKey("testItemsNonStandard")
                && StringUtil.isNotEmpty(jsonObject.getString("testItemsNonStandard"))) {
            JSONArray jsonArray = new JSONArray();
            JSONArray jsonArrayItemData = jsonObject.getJSONArray("testItemsNonStandard");
            for (Object itemDataObject : jsonArrayItemData) {
                JSONObject itemDataJson = (JSONObject) itemDataObject;
                if (itemDataJson.containsKey("_state")) {//处理新增,修改
                    if (itemDataJson.getString("_state").equalsIgnoreCase("added")) {
                        itemDataJson.remove("_state");
                        itemDataJson.remove("_id");
                        itemDataJson.remove("_uid");
                        itemDataJson.put("id", IdUtil.getId());
                        jsonArray.add(itemDataJson);
                    } else if (itemDataJson.getString("_state").equalsIgnoreCase("modified")) {
                        itemDataJson.remove("_state");
                        itemDataJson.remove("_id");
                        itemDataJson.remove("_uid");
                        jsonArray.add(itemDataJson);
                    }
                } else {//处理没变化的
                    itemDataJson.remove("_state");
                    itemDataJson.remove("_id");
                    itemDataJson.remove("_uid");
                    jsonArray.add(itemDataJson);
                }
            }
            jsonObject.put("testItemsNonStandardJson", jsonArray.toString());
        } else if (!jsonObject.containsKey("testItemsNonStandardJson")) {
            jsonObject.put("testItemsNonStandardJson", "[]");
        }
    }

    //..在businessStatus为E的情况下，通过testItems的完成率计算总体完成率
    private void processTestProgressByTestItemsJson(JSONObject jsonObject) throws Exception {
        if (jsonObject.getString("businessStatus").equalsIgnoreCase("E")) {
            double isOk = 0;
            JSONArray jsonArrayItemData = new JSONArray();
            int itemDataSize = 0;
            JSONArray jsonArrayItemDataNonStandard = new JSONArray();
            int itemDataNonStandardSize = 0;
            if (jsonObject.containsKey("testItems")
                    && StringUtil.isNotEmpty(jsonObject.getString("testItems"))) {
                jsonArrayItemData = jsonObject.getJSONArray("testItems");
                itemDataSize = jsonArrayItemData.size();
                for (Object itemDataObject : jsonArrayItemData) {
                    JSONObject itemDataJsonObject = (JSONObject) itemDataObject;
                    if (itemDataJsonObject.getString("status_item").equalsIgnoreCase("已完成")) {
                        isOk++;
                    }
                }
            }
            if (jsonObject.containsKey("testItemsNonStandard")
                    && StringUtil.isNotEmpty(jsonObject.getString("testItemsNonStandard"))) {
                jsonArrayItemDataNonStandard = jsonObject.getJSONArray("testItemsNonStandard");
                itemDataNonStandardSize = jsonArrayItemDataNonStandard.size();
                for (Object itemDataNonStandardObject : jsonArrayItemDataNonStandard) {
                    JSONObject itemDataNonStandardJsonObject = (JSONObject) itemDataNonStandardObject;
                    if (itemDataNonStandardJsonObject.getString("status_item").equalsIgnoreCase("已完成")) {
                        isOk++;
                    }
                }
            }
            double testProgress = 0;
            if (itemDataSize == 0 && itemDataNonStandardSize == 0) {
                //一个测试项目都没有
                testProgress = 0.25 + 0.5;
            } else {
                testProgress = 0.25 + (isOk / (itemDataSize + itemDataNonStandardSize)) * 0.5;
            }
            DecimalFormat format = new DecimalFormat("0.00");
            jsonObject.put("testProgress", format.format(testProgress));
        }
    }

    //..todo:应该用不到了，暂留
    private void iniStatusAndProgress(JSONObject dataObj) {
        if (StringUtils.isBlank(dataObj.getString("id")) &&
                StringUtils.isBlank(dataObj.getString("testStatus"))) {
            dataObj.put("testStatus", "未开展");
            dataObj.put("testProgress", "0");
        }
        if (StringUtils.isNotEmpty(dataObj.getString("id"))) {
            List<String> mainKanbanIdList = new ArrayList<>();
            mainKanbanIdList.add(dataObj.getString("id"));
            List<JSONObject> contractFileList = this.getTestContractFileList(mainKanbanIdList);
            List<JSONObject> reportFileList = this.getTestReportFileList(mainKanbanIdList);
            if (contractFileList.size() > 0) {
                //有合同
                if (reportFileList.size() > 0) {
                    //有报告
                    dataObj.put("testStatus", "已完成");
                    dataObj.put("testProgress", "1");
                } else {
                    //无报告
                    if (!dataObj.getString("testProgress").equals("0.5") && !dataObj.getString("testProgress").equals("0.75")) {
                        //屏蔽掉手动更新的两个特殊值
                        dataObj.put("testStatus", "进行中");
                        dataObj.put("testProgress", "0.25");
                    }
                }
            } else {
                //无合同
                dataObj.put("testStatus", "未开展");
                dataObj.put("testProgress", "0");
            }
        }
    }

    //..todo:应该用不到了，暂留
    private void iniSameTest(JSONObject dataObj) {
        if (dataObj.getString("testResult").equals("合格")) {
            JSONObject params = new JSONObject();
            params.put("materialCode", dataObj.getString("materialCode"));
            params.put("componentModel", dataObj.getString("componentModel"));
            List<JSONObject> businessList = componentTestKanbanDao.dataListQuery(params);
            for (JSONObject business : businessList) {
                business.put("componentTestAbnormalTag", "false");
                componentTestKanbanDao.updateBusiness(business);
            }
            dataObj.put("componentTestAbnormalTag", "false");
        }
    }

    //..定位流水号-1 todo:有变化，确认
    public String getSysSeq(Map<String, Object> componentObj) {
        switch (componentObj.get("componentCategory").toString()) {
            case "电气":
                return this.getSysSeq2("dq", componentObj);
            case "液压":
                return this.getSysSeq2("yy", componentObj);
            case "动力":
                return this.getSysSeq2("dl", componentObj);
            case "结构":
                return this.getSysSeq2("jg", componentObj);
            case "控制":
                return this.getSysSeq2("kz", componentObj);
            case "多学科":
                return this.getSysSeq2("dxk", componentObj);
            default:
                return "";
        }
    }

    //..定位流水号-2 todo:有变化，确认
    private String getSysSeq2(String seqKey, Map<String, Object> componentObj) {
        if (componentObj.get("testCategory").toString().equalsIgnoreCase("年度计划试验")) {
            return this.getSysSeq3(seqKey + "-1", componentObj);
        } else if (componentObj.get("testCategory").toString().equalsIgnoreCase("临时新增试验")) {
            return this.getSysSeq3(seqKey + "-2", componentObj);
        } else {
            return "";
        }
    }

    //..生成编号:实验对象，编号key（如yy-1，yy-1，yy-1，dl-1，dl-2，dl-3等）
    private synchronized String getSysSeq3(String seqKey, Map<String, Object> componentObj) {
        JSONObject params = new JSONObject();
        params.put("key", seqKey);
        //params.put("date", getDateString(componentObj.get("actualTestMonth").toString()));todo:有变化，确认
        params.put("date", getDateString(DateUtil.formatDate(new Date(), DateUtil.DATE_FORMAT_YMD)));
        JSONObject seq = componentTestKanbanDao.getSeq(params);
        if (seq == null) {
            seq = new JSONObject();
            seq.put("id", IdUtil.getId());
            seq.put("KEY_", seqKey);
            //seq.put("DATE_", getDateString(componentObj.get("actualTestMonth").toString()));
            seq.put("DATE_", getDateString(DateUtil.formatDate(new Date(), DateUtil.DATE_FORMAT_YMD)));
            seq.put("INIT_VAL_", "1");
            componentTestKanbanDao.insertSeq(seq);
        } else {
            int i = seq.getIntValue("INIT_VAL_");
            i++;
            seq.put("INIT_VAL_", i);
            componentTestKanbanDao.updateSeq(seq);
        }
        String[] keys = seqKey.split("-");
        String seqvalue = seq.getString("INIT_VAL_").length() < 2 ? "00" + seq.getString("INIT_VAL_") :
                seq.getString("INIT_VAL_").length() < 3 ? "0" + seq.getString("INIT_VAL_") :
                        seq.getString("INIT_VAL_");
        return keys[0] + "-" + seq.getString("DATE_") + seqvalue + "-" + keys[1];
    }

    //..获取时间字符yyyyMM
    private String getDateString(String dateString) {
        String[] arrNow;
        if (dateString == null) {
            arrNow = DateUtil.formatDate(new Date(), DateUtil.DATE_FORMAT_MON).split("-");
        } else {
            arrNow = dateString.split("-");
        }

        StringBuffer sb = new StringBuffer();
//        for (String a : arrNow) {
//            sb.append(a);
//        }
        for (int i = 0; i < arrNow.length - 1; i++) {
            sb.append(arrNow[i]);
        }
        return sb.toString();
    }

    //..
    public JsonResult deleteBusiness(String[] ids, String[] instIds) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> businessIds = Arrays.asList(ids);
        List<JSONObject> filesContract = this.getTestContractFileList(businessIds);
        List<JSONObject> filesReport = this.getTestReportFileList(businessIds);
        List<JSONObject> filesMPT = this.getTestMPTFileList(businessIds);
        String filePathBaseContract = sysDicManager.getBySysTreeKeyAndDicKey(
                "componentTestUploadPosition", "testContract").getValue();
        String filePathBaseReport = sysDicManager.getBySysTreeKeyAndDicKey(
                "componentTestUploadPosition", "testReport").getValue();
        String filePathBaseMPT = sysDicManager.getBySysTreeKeyAndDicKey(
                "componentTestUploadPosition", "MPT").getValue();
        for (JSONObject oneFile : filesContract) {
            rdmZhglFileManager.deleteOneFileFromDisk(oneFile.getString("id"),
                    oneFile.getString("fileName"), oneFile.getString("mainKanbanId"), filePathBaseContract);
        }
        for (JSONObject oneFile : filesReport) {
            rdmZhglFileManager.deleteOneFileFromDisk(oneFile.getString("id"),
                    oneFile.getString("fileName"), oneFile.getString("mainKanbanId"), filePathBaseReport);
        }
        for (JSONObject oneFile : filesMPT) {
            rdmZhglFileManager.deleteOneFileFromDisk(oneFile.getString("id"),
                    oneFile.getString("fileName"), oneFile.getString("mainKanbanId"), filePathBaseMPT);
        }
        for (String oneBusinessId : businessIds) {
            rdmZhglFileManager.deleteDirFromDisk(oneBusinessId, filePathBaseContract);
            rdmZhglFileManager.deleteDirFromDisk(oneBusinessId, filePathBaseReport);
            rdmZhglFileManager.deleteDirFromDisk(oneBusinessId, filePathBaseMPT);
        }
        Map<String, Object> param = new HashMap<>();
        param.put("businessIds", businessIds);
        componentTestKanbanDao.deleteContractFile(param);
        componentTestKanbanDao.deleteReportFile(param);
        componentTestKanbanDao.deleteMPTFile(param);
        componentTestKanbanDao.deleteStandard(param);
        List<JSONObject> msgReadyToDeleteList = componentTestKanbanDao.queryBindingStandardMsgList(param);
        List<String> msgReadyToDeleteIdList = new ArrayList<>();
        for (JSONObject msgReadyToDelete : msgReadyToDeleteList) {
            msgReadyToDeleteIdList.add(msgReadyToDelete.getString("id"));
        }
        param.put("msgIds", msgReadyToDeleteIdList.size() > 0 ? msgReadyToDeleteIdList : "");
        componentTestKanbanDao.deleteBindingStandardMsgItem(param);
        componentTestKanbanDao.deleteBindingStandardMsg(param);
        componentTestKanbanDao.deleteBusiness(param);
        //删除实例,不是同步删除，但是总量是能一对一的
        for (String oneInstId : instIds) {
            bpmInstManager.deleteCascade(oneInstId, "");
        }
        return result;
    }

    //..
    public List<JSONObject> getTestContractFileList(List<String> mainKanbanIdList) {
        List<JSONObject> businessFileList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("mainKanbanIds", mainKanbanIdList);
        businessFileList = componentTestKanbanDao.queryTestContractFileList(param);
        return businessFileList;
    }

    //..
    public List<JSONObject> getTestReportFileList(List<String> mainKanbanIdList) {
        List<JSONObject> businessFileList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("mainKanbanIds", mainKanbanIdList);
        businessFileList = componentTestKanbanDao.queryTestReportFileList(param);
        return businessFileList;
    }

    //..
    public List<JSONObject> getTestMPTFileList(List<String> mainKanbanIdList) {
        List<JSONObject> businessFileList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("mainKanbanIds", mainKanbanIdList);
        businessFileList = componentTestKanbanDao.queryTestMPTFileList(param);
        return businessFileList;
    }

    //..
    public void exportExcel(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String filterParams = request.getParameter("filter");
        JSONArray jsonArray = JSONArray.parseArray(filterParams);
        List<String> ids = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            ids.add(jsonArray.getJSONObject(i).getString("value"));
        }
        JSONObject param = new JSONObject();
        param.put("ids", ids);
        List<JSONObject> businessList = componentTestKanbanDao.dataListQuery(param);

        for (JSONObject oneObj : businessList) {
            //获取合同名称列表
            List<String> testContractNameList = componentTestKanbanDao.getTestContractNameListByBusinessId(oneObj.getString("id"));
            //如果有合同名称
            if (testContractNameList.size() > 0) {
                StringBuilder stringBuilder = new StringBuilder();
                for (String fileName : testContractNameList) {
                    stringBuilder.append("《").append(fileName).append("》");
                }
                oneObj.put("testContract", stringBuilder.toString());
            }
            //获取检测报告名称列表
            List<String> testReportNameList = componentTestKanbanDao.getTestReportNameListByBusinessId(oneObj.getString("id"));
            //如果有检测报告名称
            if (testReportNameList.size() > 0) {
                StringBuilder stringBuilder = new StringBuilder();
                for (String fileName : testReportNameList) {
                    stringBuilder.append("《").append(fileName).append("》");
                }
                oneObj.put("testReport", stringBuilder.toString());
            }
        }

        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "零部件试验";
        String excelName = nowDate + title;
        String[] fieldNames = {"试验编号", "专业类别", "样品名称", "型号规格", "物料编码", "样品类型", "配套主机型号", "配套主机名称",
                "供应商名称", "承担单位", "试验类型", "期望试验开始时间", "申请人", "申请部门", "试验计划类型",
                "试验负责人", "测试运行状态", "测试开始时间", "测试进度", "测试完成时间", "测试结果", "不合格项说明",
                "检测报告", "检测合同", "测试方案状态", "测试费用", "测试次数", "样品来源", "样品处理方式", "备注"};
        String[] fieldCodes = {"testNo", "componentCategory", "componentName", "componentModel", "materialCode", "sampleType", "machineModel", "machineName",
                "supplierName", "laboratory", "testType", "hopeTestMonth", "applyUser", "applyDep", "testCategory",
                "testLeader", "testStatus", "actualTestMonth", "testProgress", "completeTestMonth", "testResult", "nonconformingDescription",
                "testReport", "testContract", "testContractStatus", "testCost", "testRounds", "sampleSource", "sampleProcessingMethod", "remark"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(businessList, fieldNames, fieldCodes, title);
        //输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }
}
