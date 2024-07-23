package com.redxun.componentTest.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.componentTest.core.dao.ComponentTestKanbanDao;
import com.redxun.componentTest.core.dao.ComponentTestResultDao;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.FileUtil;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.standardManager.core.manager.StandardManager;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.sys.db.manager.SysSqlCustomQueryUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Service
public class ComponentTestResultService {
    private static Logger logger = LoggerFactory.getLogger(ComponentTestResultService.class);
    @Autowired
    private ComponentTestResultDao componentTestResultDao;
    @Autowired
    private ComponentTestKanbanDao componentTestKanbanDao;
    @Autowired
    private SysDicManager sysDicManager;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;

    //..
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        getListParams(params, request);
        //--以下数据权限处理
        //admin/分管领导/零部件试验特权人员(qinqiang,xiaoyunbo)/挖掘机械研究院主任看所有
        //部门负责人看本部门
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
            } else {
                queryUserParam.remove("groupName");
                List<String> typeKeyList = new ArrayList<>();
                typeKeyList.add("GROUP-USER-DIRECTOR");
                queryUserParam.put("typeKeyList", typeKeyList);
                List<Map<String, Object>> queryDeptResult = xcmgProjectOtherDao.queryUserDeps(queryUserParam);
                if (queryDeptResult != null && !queryDeptResult.isEmpty()) {//分管主任
                    params.put("roleName", "fgzr");
                } else {
                    typeKeyList.clear();
                    typeKeyList.add("GROUP-USER-LEADER");
                    queryUserParam.put("typeKeyList", typeKeyList);
                    queryDeptResult = xcmgProjectOtherDao.queryUserDeps(queryUserParam);
                    if (queryDeptResult != null && !queryDeptResult.isEmpty()) {//部门负责人
                        List<String> myDeptIdList = new ArrayList<>();
                        for (Map<String, Object> oneDept : queryDeptResult) {
                            String groupId = oneDept.get("PARTY1_").toString();
                            myDeptIdList.add(groupId);
                            params.put("roleName", "bmfzr");
                            params.put("deptIds", myDeptIdList);
                        }
                    } else {//普通员工
                        params.put("roleName", "ptyg");
                    }
                }
            }
        }
        //--以上数据权限处理
        List<JSONObject> businessList = componentTestResultDao.dataListQuery(params);
        int businessListCount = componentTestResultDao.countDataListQuery(params);
        result.setData(businessList);
        result.setTotal(businessListCount);
        return result;
    }

    //..
    public JsonPageResult<?> dataPassListQuery(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        getListParams(params, request);
        List<JSONObject> businessList = componentTestResultDao.dataPassListQuery(params);
        int businessListCount = componentTestResultDao.countDataPassListQuery(params);
        for (JSONObject oneObj : businessList) {
            //获取关联标准名称列表
            List<String> testStandardIdList = componentTestKanbanDao.getTestStandardIdListByBusinessId(oneObj.getString("id"));
            //如果有关联标准
            if (testStandardIdList.size() > 0) {
                StringBuilder stringBuilder = new StringBuilder();
                for (String standardId : testStandardIdList) {
                    //@lwgkiller:莫名其妙
                    //Map<String, Object> standard = standardManager.queryStandardById(standardId);
                    //stringBuilder.append("《").append(standard.get("standardNumber").toString()).append("》");
                    JSONObject param = new JSONObject();
                    param.put("id", standardId);
                    JsonResult jsonResult = SysSqlCustomQueryUtil.queryForJson("根据id查询标准编号", param.toJSONString());
                    ArrayList<HashMap> arrayList = (ArrayList<HashMap>) jsonResult.getData();
                    if (!arrayList.isEmpty()) {
                        stringBuilder.append("《").append(arrayList.get(0).get("standardNumber").toString()).append("》");
                    }
                }
                oneObj.put("testStandard", stringBuilder.toString());
            }
        }
        result.setData(businessList);
        result.setTotal(businessListCount);
        return result;
    }

    //..
    public void exportPass(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String filterParams = request.getParameter("filter");
        JSONArray jsonArray = JSONArray.parseArray(filterParams);
        List<String> listids = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            listids.add(jsonArray.getJSONObject(i).getString("value"));
        }
        JSONObject param = new JSONObject();
        param.put("ids", listids);
        List<JSONObject> listData = componentTestResultDao.dataPassListQuery(param);
        for (JSONObject oneObj : listData) {
            //获取关联标准名称列表
            List<String> testStandardIdList = componentTestKanbanDao.getTestStandardIdListByBusinessId(oneObj.getString("id"));
            //如果有关联标准
            if (testStandardIdList.size() > 0) {
                StringBuilder stringBuilder = new StringBuilder();
                for (String standardId : testStandardIdList) {
                    param.clear();
                    param.put("id", standardId);
                    JsonResult jsonResult = SysSqlCustomQueryUtil.queryForJson("根据id查询标准编号", param.toJSONString());
                    ArrayList<HashMap> arrayList = (ArrayList<HashMap>) jsonResult.getData();
                    if (!arrayList.isEmpty()) {
                        stringBuilder.append("《").append(arrayList.get(0).get("standardNumber").toString()).append("》");
                    }
                }
                oneObj.put("testStandard", stringBuilder.toString());
            }
        }
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "通过试验验证零部件目录";
        String excelName = nowDate + title;
        String[] fieldNames = {"零部件名称", "零部件型号", "物料编码", "配套主机型号", "相关试验标准", "供应商名称", "试验报告编号"};
        String[] fieldCodes = {"componentName", "componentModel", "materialCode", "machineModel", "testStandard", "supplierName", "testReportNo"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(listData, fieldNames, fieldCodes, title);
        //输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);

    }

    //..
    public JsonPageResult<?> dataNotPassListQuery(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        getListParams(params, request);
        List<JSONObject> businessList = componentTestResultDao.dataNotPassListQuery(params);
        int businessListCount = componentTestResultDao.countDataNotPassListQuery(params);
        for (JSONObject oneObj : businessList) {
            //获取关联标准名称列表
            List<String> testStandardIdList = componentTestKanbanDao.getTestStandardIdListByBusinessId(oneObj.getString("id"));
            //如果有关联标准
            if (testStandardIdList.size() > 0) {
                StringBuilder stringBuilder = new StringBuilder();
                for (String standardId : testStandardIdList) {
                    //@lwgkiller:莫名其妙
                    //Map<String, Object> standard = standardManager.queryStandardById(standardId);
                    JSONObject param = new JSONObject();
                    param.put("id", standardId);
                    JsonResult jsonResult = SysSqlCustomQueryUtil.queryForJson("根据id查询标准编号", param.toJSONString());
                    ArrayList<HashMap> arrayList = (ArrayList<HashMap>) jsonResult.getData();
                    stringBuilder.append("《").append(arrayList.get(0).get("standardNumber").toString()).append("》");
                    System.out.println("");

                }
                oneObj.put("testStandard", stringBuilder.toString());
            }
        }
        result.setData(businessList);
        result.setTotal(businessListCount);
        return result;
    }

    //..
    public void exportNotPass(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String filterParams = request.getParameter("filter");
        JSONArray jsonArray = JSONArray.parseArray(filterParams);
        List<String> listids = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            listids.add(jsonArray.getJSONObject(i).getString("value"));
        }
        JSONObject param = new JSONObject();
        param.put("ids", listids);
        List<JSONObject> listData = componentTestResultDao.dataNotPassListQuery(param);
        for (JSONObject oneObj : listData) {
            //获取关联标准名称列表
            List<String> testStandardIdList = componentTestKanbanDao.getTestStandardIdListByBusinessId(oneObj.getString("id"));
            //如果有关联标准
            if (testStandardIdList.size() > 0) {
                StringBuilder stringBuilder = new StringBuilder();
                for (String standardId : testStandardIdList) {
                    param.clear();
                    param.put("id", standardId);
                    JsonResult jsonResult = SysSqlCustomQueryUtil.queryForJson("根据id查询标准编号", param.toJSONString());
                    ArrayList<HashMap> arrayList = (ArrayList<HashMap>) jsonResult.getData();
                    if (!arrayList.isEmpty()) {
                        stringBuilder.append("《").append(arrayList.get(0).get("standardNumber").toString()).append("》");
                    }
                }
                oneObj.put("testStandard", stringBuilder.toString());
            }
        }
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "未通过试验验证零部件目录";
        String excelName = nowDate + title;
        String[] fieldNames = {"零部件名称", "零部件型号", "物料编码", "配套主机型号", "相关试验标准", "供应商名称", "试验报告编号", "不合格项说明"};
        String[] fieldCodes = {"componentName", "componentModel", "materialCode", "machineModel", "testStandard", "supplierName",
                "testReportNo", "nonconformingDescription"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(listData, fieldNames, fieldCodes, title);
        //输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);

    }

    //..p
    private void getListParams(Map<String, Object> params, HttpServletRequest request) {
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)) {
            params.put("sortField", sortField);
            params.put("sortOrder", sortOrder);
        }
        if (StringUtils.isEmpty(sortField)) {
            params.put("sortField", "testNo");
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
}
