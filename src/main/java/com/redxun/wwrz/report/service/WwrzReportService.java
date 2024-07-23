package com.redxun.wwrz.report.service;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.query.Page;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.standardManager.core.util.ResultUtil;
import com.redxun.wwrz.core.dao.WwrzTestApplyDao;
import com.redxun.wwrz.report.dao.WwrzReportDao;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import com.redxun.xcmgProjectManager.core.util.ConstantUtil;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.wwrz.core.dao.WwrzMoneyDao;

import java.text.DecimalFormat;
import java.util.*;

/**
 * @author zhangzhen
 */
@Service
public class WwrzReportService {
    private static final Logger logger = LoggerFactory.getLogger(WwrzReportService.class);
    @Resource
    WwrzReportDao wwrzReportDao;
    @Resource
    CommonInfoManager commonInfoManager;
    @Resource
    private XcmgProjectManager xcmgProjectManager;
    @Resource
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Resource
    private WwrzTestApplyDao wwrzTestApplyDao;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    public JSONObject getReportData(HttpServletRequest request) {
        List<JSONObject> totalList = new ArrayList<>();
        try {
            String reportYear = request.getParameter("reportYear");
            int reportYearInt = Integer.parseInt(reportYear);
            JSONObject dataObj;
            int paramYear;
            for(int i=9;i>=0;i--){
                dataObj = new JSONObject();
                paramYear = reportYearInt-i;
                dataObj.put("reportYear",paramYear);
                //获取数据
                dataObj.put("dataValue",wwrzReportDao.getReportData(paramYear));
                totalList.add(dataObj);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.result(false, "获取数据失败", totalList);
        }
        return ResultUtil.result(true, "获取数据成功", totalList);
    }

    public JSONObject getReportTypeData(HttpServletRequest request) {
        List<JSONObject> totalList = new ArrayList<>();
        try {
            String reportYearStart = request.getParameter("reportYearStart");
            String reportYearEnd = request.getParameter("reportYearEnd");
            JSONObject paramJson = new JSONObject();
            paramJson.put("reportYearStart",reportYearStart);
            paramJson.put("reportYearEnd",reportYearEnd);
            totalList = wwrzReportDao.getReportTypeData(paramJson);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.result(false, "获取数据失败", totalList);
        }
        return ResultUtil.result(true, "获取数据成功", totalList);
    }

    public JSONObject getDeptProjectData(HttpServletRequest request) {
        List<JSONObject> totalList = new ArrayList<>();
        try {
            String reportYearStart = request.getParameter("reportYearStart");
            String reportYearEnd = request.getParameter("reportYearEnd");
            JSONObject paramJson = new JSONObject();
            paramJson.put("reportYearStart",reportYearStart);
            paramJson.put("reportYearEnd",reportYearEnd);
            List<JSONObject> deptIdList = wwrzReportDao.getProjectDepts(paramJson);
            JSONObject deptDataObj;
            for(JSONObject deptObj:deptIdList){
                deptDataObj = new JSONObject();
                deptDataObj.put("deptName",deptObj.getString("deptName"));
                String deptId = deptObj.getString("deptId");
                paramJson.put("STATUS_","RUNNING");
                paramJson.put("deptId",deptId);
                int runningNum = wwrzReportDao.getDeptProjectData(paramJson);
                deptDataObj.put("runningNum",runningNum);
                paramJson.put("STATUS_","SUCCESS_END");
                int endNum = wwrzReportDao.getDeptProjectData(paramJson);
                deptDataObj.put("endNum",endNum);
                totalList.add(deptDataObj);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.result(false, "获取数据失败", totalList);
        }
        return ResultUtil.result(true, "获取数据成功", totalList);
    }

    public JSONObject getWwrzProjectData(HttpServletRequest request) {
        List<JSONObject> totalList = new ArrayList<>();
        try {
            List<Map<String,Object>> dicList = commonInfoManager.getDicValues("RZXM");
            String reportYear = request.getParameter("reportYear");
            JSONObject paramJson = new JSONObject();
            paramJson.put("reportYear",reportYear);
            int totalNum = wwrzReportDao.getWwrzProjectData(paramJson);
            JSONObject projectObj;
            for(Map<String,Object> map:dicList){
                projectObj = new JSONObject();
                paramJson = new JSONObject();
                paramJson.put("reportYear",reportYear);
                String key_ = CommonFuns.nullToString(map.get("key_"));
                paramJson.put("itemKey",key_);
                projectObj.put("itemName",map.get("text"));
                int projectNum = wwrzReportDao.getWwrzProjectData(paramJson);
                projectObj.put("projectNum",projectNum);
                int signNum = wwrzReportDao.getSignContractProjectData(paramJson);
                projectObj.put("signNum",signNum);
                double rate = 0;
                if(projectNum!=0){
                    rate = (double)projectNum/totalNum*100;
                }
                projectObj.put("rate",new DecimalFormat("#.##").format(rate));
                totalList.add(projectObj);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.result(false, "获取数据失败", totalList);
        }
        return ResultUtil.result(true, "获取数据成功", totalList);
    }
    public JSONObject getWwrzMoneyData(HttpServletRequest request) {
        List<JSONObject> totalList = new ArrayList<>();
        try {
            List<Map<String,Object>> dicList = commonInfoManager.getDicValues("RZXM");
            String reportYear = request.getParameter("reportYear");
            JSONObject paramJson = new JSONObject();
            JSONObject projectObj;
            //获取合同总金额
            paramJson.put("reportYear",reportYear);
            double totalMoney = wwrzReportDao.getMoneyData(paramJson);

            for(Map<String,Object> map:dicList){
                paramJson = new JSONObject();
                paramJson.put("reportYear",reportYear);
                projectObj = new JSONObject();
                String key_ = CommonFuns.nullToString(map.get("key_"));
                paramJson.put("itemKey",key_);
                projectObj.put("itemName",map.get("text"));
                double projectMoney = wwrzReportDao.getMoneyData(paramJson);
                projectObj.put("totalMoney",new DecimalFormat("#.##").format(projectMoney));
                paramJson.put("isPayMoney","isPayMoney");
                double payMoney = wwrzReportDao.getMoneyData(paramJson);
                projectObj.put("payMoney",new DecimalFormat("#.##").format(payMoney));
                double rate = 0;
                if(totalMoney!=0){
                    rate = (double)projectMoney/totalMoney*100;
                }
                projectObj.put("rate",new DecimalFormat("#.##").format(rate));
                totalList.add(projectObj);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.result(false, "获取数据失败", totalList);
        }
        return ResultUtil.result(true, "获取数据成功", totalList);
    }

    public JSONObject getWwrzMoneyViewData(HttpServletRequest request) {
        JSONObject moneyObj = new JSONObject();
        try {
            List<Map<String,Object>> dicList = commonInfoManager.getDicValues("RZXM");
            String reportYear = request.getParameter("reportYear");
            JSONObject paramJson = new JSONObject();
            JSONObject projectObj;
            int totalNum = 0;
            for(Map<String,Object> map:dicList){
                paramJson = new JSONObject();
                paramJson.put("reportYear",reportYear);
                String key_ = CommonFuns.nullToString(map.get("key_"));
                paramJson.put("itemKey",key_);
                int projectNum = wwrzReportDao.getWwrzProjectData(paramJson);
                totalNum += projectNum;
            }
            paramJson = new JSONObject();
            paramJson.put("reportYear",reportYear);
            double totalMoney = wwrzReportDao.getMoneyData(paramJson);
            paramJson.put("isPayMoney","isPayMoney");
            double payMoney = wwrzReportDao.getMoneyData(paramJson);
            moneyObj.put("totalNum",totalNum);
            moneyObj.put("totalMoney",totalMoney);
            moneyObj.put("payMoney",payMoney);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.result(false, "获取数据失败", moneyObj);
        }
        return ResultUtil.result(true, "获取数据成功", moneyObj);
    }

    public JSONObject getPlanData(HttpServletRequest request) {
        List<JSONObject> totalList = new ArrayList<>();
        try {
            String reportYear = request.getParameter("reportYear");
            String deptId = request.getParameter("deptId");
            String month;
            String yearMonth;
            JSONObject paramJson;
            int firstQuarterPlan = 0,firstQuarterPlanExe = 0,firstQuarterUnPlanExe = 0;
            int secondQuarterPlan = 0,secondQuarterPlanExe = 0,secondQuarterUnPlanExe = 0;
            int thirdQuarterPlan = 0,thirdQuarterPlanExe = 0,thirdQuarterUnPlanExe = 0;
            int fourthQuarterPlan = 0,fourthQuarterPlanExe = 0,fourthQuarterUnPlanExe = 0;
            float firstQuartRate = 0,secondQuartRate = 0,thirdQuartRate = 0,fourthQuartRate = 0;
            for(int i=1;i<=12;i++){
                month = CommonFuns.genDigitalCode(i,2);
                yearMonth = reportYear+"-"+month;
                paramJson = new JSONObject();
                paramJson.put("deptId",deptId);
                paramJson.put("yearMonth",yearMonth);
                int resultPlanNum = wwrzReportDao.getPlanData(paramJson);
                if(i<=3){
                    firstQuarterPlan += resultPlanNum;
                }else if(i>3&&i<=6){
                    secondQuarterPlan += resultPlanNum;
                }else if(i>6&&i<=9){
                    thirdQuarterPlan += resultPlanNum;
                }else if(i>9&&i<=12){
                    fourthQuarterPlan += resultPlanNum;
                }
                paramJson.put("isApply","isApply");
                int resultPlanExe = wwrzReportDao.getPlanData(paramJson);
                int resultUnPlanExe = wwrzReportDao.getUnPlanData(paramJson);
                if(i<=3){
                    firstQuarterPlanExe += resultPlanExe;
                    firstQuarterUnPlanExe += resultUnPlanExe;
                }else if(i>3&&i<=6){
                    secondQuarterPlanExe += resultPlanExe;
                    secondQuarterUnPlanExe += resultUnPlanExe;
                }else if(i>6&&i<=9){
                    thirdQuarterPlanExe += resultPlanExe;
                    thirdQuarterUnPlanExe += resultUnPlanExe;
                }else if(i>9&&i<=12){
                    fourthQuarterPlanExe += resultPlanExe;
                    fourthQuarterUnPlanExe += resultUnPlanExe;
                }
            }
            JSONObject firstObj = new JSONObject();
            firstObj.put("quarterName","一季度");
            firstObj.put("planNum",firstQuarterPlan);
            firstObj.put("planExeNum",firstQuarterPlanExe);
            firstObj.put("unPlanExeNum",firstQuarterUnPlanExe);
            if(firstQuarterPlan!=0||firstQuarterUnPlanExe!=0){
                firstQuartRate = (float) firstQuarterPlanExe/(firstQuarterPlan+firstQuarterUnPlanExe)*100;
            }
            firstObj.put("quartRate",new DecimalFormat("#.##").format(firstQuartRate));
            totalList.add(firstObj);
            JSONObject secondObj = new JSONObject();
            secondObj.put("quarterName","二季度");
            secondObj.put("planNum",secondQuarterPlan);
            secondObj.put("planExeNum",secondQuarterPlanExe);
            secondObj.put("unPlanExeNum",secondQuarterUnPlanExe);
            if(secondQuarterPlan!=0||secondQuarterUnPlanExe!=0){
                secondQuartRate = (float)secondQuarterPlanExe/(secondQuarterPlan+secondQuarterUnPlanExe)*100;
            }
            secondObj.put("quartRate",new DecimalFormat("#.##").format(secondQuartRate));
            totalList.add(secondObj);
            JSONObject thirdObj = new JSONObject();
            thirdObj.put("quarterName","三季度");
            thirdObj.put("planNum",thirdQuarterPlan);
            thirdObj.put("planExeNum",thirdQuarterPlanExe);
            thirdObj.put("unPlanExeNum",thirdQuarterUnPlanExe);
            if(thirdQuarterPlan!=0||thirdQuarterUnPlanExe!=0){
                thirdQuartRate = (float)thirdQuarterPlanExe/(thirdQuarterPlan+thirdQuarterUnPlanExe)*100;
            }
            thirdObj.put("quartRate",new DecimalFormat("#.##").format(thirdQuartRate));
            totalList.add(thirdObj);
            JSONObject fourthObj = new JSONObject();
            fourthObj.put("quarterName","四季度");
            fourthObj.put("planNum",fourthQuarterPlan);
            fourthObj.put("planExeNum",fourthQuarterPlanExe);
            fourthObj.put("unPlanExeNum",fourthQuarterUnPlanExe);
            if(fourthQuarterPlan!=0||fourthQuarterUnPlanExe!=0){
                fourthQuartRate = (float)fourthQuarterPlanExe/(fourthQuarterPlan+fourthQuarterUnPlanExe)*100;
            }
            fourthObj.put("quartRate",new DecimalFormat("#.##").format(fourthQuartRate));
            totalList.add(fourthObj);

        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.result(false, "获取数据失败", totalList);
        }
        return ResultUtil.result(true, "获取数据成功", totalList);
    }

    public JSONObject getDeptPlanData(HttpServletRequest request) {
        List<JSONObject> totalList = new ArrayList<>();
        try {
            String reportYearMonthStart = request.getParameter("reportYearMonthStart");
            String reportYearMonthEnd = request.getParameter("reportYearMonthEnd");
            JSONObject paramJson = new JSONObject();
            paramJson.put("reportYearMonthStart",reportYearMonthStart);
            paramJson.put("reportYearMonthEnd",reportYearMonthEnd);
            List<JSONObject> deptList = wwrzReportDao.getPlanDeptList(paramJson);
            String deptId;
            for(JSONObject deptObj:deptList){
                paramJson = new JSONObject();
                paramJson.put("reportYearMonthStart",reportYearMonthStart);
                paramJson.put("reportYearMonthEnd",reportYearMonthEnd);
                deptId = deptObj.getString("deptId");
                paramJson.put("deptId",deptId);
                int planNum = wwrzReportDao.getDeptPlanData(paramJson);
                deptObj.put("planNum",planNum);
                paramJson.put("isApply","isApply");
                int planExeNum = wwrzReportDao.getDeptPlanData(paramJson);
                deptObj.put("planExeNum",planExeNum);
                int unPlanExeNum = wwrzReportDao.getUnDeptPlanData(paramJson);
                deptObj.put("unPlanExeNum",unPlanExeNum);
                float deptRate = 0;
                if(planNum!=0||unPlanExeNum!=0){
                    deptRate = (float) planExeNum/(planNum+unPlanExeNum)*100;
                }
                deptObj.put("deptRate",new DecimalFormat("#.##").format(deptRate));
                totalList.add(deptObj);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.result(false, "获取数据失败", totalList);
        }
        return ResultUtil.result(true, "获取数据成功", totalList);
    }


    /**
     * 查询变更列表
     */
    public JsonPageResult<?> queryList(HttpServletRequest request, HttpServletResponse response, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            Map<String, Object> params = new HashMap<>();
            // 传入条件(不包括分页)
            params = XcmgProjectUtil.getSearchParam(params, request);
            params.put("reportStatus","reportStatus");
            List<JSONObject> applyList = wwrzTestApplyDao.dataListQuery(params);
            // 增加不同角色和部门的人看到的数据不一样的过滤
            rdmZhglUtil.setTaskInfo2Data(applyList, ContextUtil.getCurrentUserId());
            applyList = filterApplyListByDepRole(applyList);
            // 根据分页进行subList截取
            List<JSONObject> finalSubList = new ArrayList<JSONObject>(16);
            // 根据分页进行subList截取
            if (doPage) {
                int pageIndex = RequestUtil.getInt(request, "pageIndex", 0);
                int pageSize = RequestUtil.getInt(request, "pageSize", Page.DEFAULT_PAGE_SIZE);
                int startSubListIndex = pageIndex * pageSize;
                int endSubListIndex = startSubListIndex + pageSize;
                if (startSubListIndex < applyList.size()) {
                    finalSubList = applyList.subList(startSubListIndex,
                            endSubListIndex < applyList.size() ? endSubListIndex : applyList.size());
                }
            } else {
                finalSubList = applyList;
            }
            result.setData(finalSubList);
            result.setTotal(applyList.size());
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
    public List<JSONObject> filterApplyListByDepRole(List<JSONObject>  applyList) {
        List<JSONObject> result = new ArrayList<>();
        if (applyList == null || applyList.isEmpty()) {
            return result;
        }
        // 刷新任务的当前执行人
        boolean showAll = false;
        // 管理员查看所有的包括草稿数据
        if (ConstantUtil.ADMIN.equals(ContextUtil.getCurrentUser().getUserNo())) {
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
                if (RdmConst.JSZX_ZR.equalsIgnoreCase(oneRole.get("NAME_").toString())) {
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
        //标准技术所 查看所有数据
        if (RdmConst.BZJSS_NAME.equalsIgnoreCase(currentUserMainDepName)) {
            showAll = true;
        }
        // 过滤
        for (JSONObject oneApply : applyList) {
            // 自己是当前流程处理人
            if (StringUtils.isNotBlank(oneApply.getString("myTaskId"))) {
                oneApply.put("processTask", true);
                result.add(oneApply);
            } else if (showAll) {
                // 分管领导和项目管理人员查看所有非草稿的数据或者草稿但是创建人CREATE_BY_是自己的
                if (oneApply.get("status") != null && !"DRAFTED".equals(oneApply.get("status").toString())) {
                    result.add(oneApply);
                } else {
                    if (oneApply.get("CREATE_BY_").toString().equals(currentUserId)) {
                        result.add(oneApply);
                    }
                }
            } else if (isDepRespMan ) {
                // 部门负责人对于非草稿的且申请人部门是当前部门，或者草稿但是创建人CREATE_BY_是自己的
                if (oneApply.get("status") != null && !"DRAFTED".equals(oneApply.get("status").toString())) {
                    if (oneApply.get("deptId").toString().equals(currentUserMainDepId)) {
                        result.add(oneApply);
                    }
                } else {
                    if (oneApply.get("CREATE_BY_").toString().equals(currentUserId)) {
                        result.add(oneApply);
                    }
                }

            } else {
                // 其他人对于创建人CREATE_BY_是自己的
                if (oneApply.get("CREATE_BY_").toString().equals(currentUserId)||oneApply.get("productLeader").toString().equals(currentUserId)) {
                    result.add(oneApply);
                }
            }
        }
        return result;
    }


}
