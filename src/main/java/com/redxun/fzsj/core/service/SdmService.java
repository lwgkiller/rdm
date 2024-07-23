package com.redxun.fzsj.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.util.DateUtil;
import com.redxun.fzsj.core.dao.FzdxDao;
import com.redxun.fzsj.core.dao.SdmDao;
import com.redxun.info.core.util.HttpRequestUtil;
import com.redxun.rdmCommon.core.util.RdmFTPUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.standardManager.core.util.CommonFuns;
import com.redxun.sys.core.manager.SysSeqIdManager;
import com.redxun.sys.core.util.SysPropertiesUtil;
import com.redxun.sys.org.dao.OsUserDao;
import com.redxun.sys.org.entity.OsUser;
import com.redxun.sys.org.manager.OsUserManager;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.*;

@Service
public class SdmService {
    private static Logger logger = LoggerFactory.getLogger(SdmService.class);
    @Autowired
    SysSeqIdManager sysSeqIdManager;
    @Resource
    SdmDao sdmDao;
    @Resource
    OsUserDao osUserDao;
    @Resource
    OsUserManager osUserManager;
    @Resource
    FzdxDao fzdxDao;
    @Resource
    FzsjService fzsjService;
    public List<JSONObject> getSdmProductList() {
        JSONObject paramJson = new JSONObject();
        List<JSONObject> treeList = new ArrayList<>();
        try {
            String postIP = SysPropertiesUtil.getGlobalProperty("SDMUrl");
            String portUrl = postIP+"/hwe/services/rdm/getProductRelation";
            JSONObject resultJson = HttpRequestUtil.doPost(portUrl, paramJson);
            if (resultJson.getIntValue("code") == 200) {
                JSONObject result = resultJson.getJSONObject("result");
                JSONArray companyList = result.getJSONArray("child");
                for (int i = 0; i < companyList.size(); i++) {
                    JSONObject companyObj = companyList.getJSONObject(i);
                    if ("挖机".equals(companyObj.getString("name"))) {
                        companyObj.put("parentId", "");
                        String companyId = companyObj.getString("id");
                        treeList.add(companyObj);
                        JSONArray list = companyObj.getJSONArray("child");
                        for (int j = 0; j < list.size(); j++) {
                            JSONObject modelObj = list.getJSONObject(j);
                            modelObj.put("parentId", companyId);
                            treeList.add(modelObj);
                            String modelId = modelObj.getString("id");
                            JSONArray productList = modelObj.getJSONArray("child");
                            for (int k = 0; k < productList.size(); k++) {
                                JSONObject productObj = productList.getJSONObject(k);
                                productObj.put("parentId", modelId);
                                treeList.add(productObj);
                            }
                        }
                        break;
                    }
                }
            }
        } catch (Exception e) {
            logger.error("同步SDM产品族谱异常", e);
        }
        return treeList;
    }

    /**
     * 获取SDM仿真能力清单
     */
    public List<JSONObject> getSdmAnalysisList() {
        JSONObject paramJson = new JSONObject();
        List<JSONObject> treeList = new ArrayList<>();
        try {
            String postIP = SysPropertiesUtil.getGlobalProperty("SDMUrl");
            String portUrl = postIP+"/hwe/services/rdm/getAnalysisByCom?comName=挖机";
            JSONObject resultJson = HttpRequestUtil.doPost(portUrl, paramJson);
            if (resultJson.getIntValue("code") == 200) {
                JSONArray companyList = resultJson.getJSONArray("result");
                JSONObject companyObj = companyList.getJSONObject(0);
                String companyId = companyObj.getString("id");
                companyObj.put("parentId", "");
                treeList.add(companyObj);
                JSONArray productList = companyObj.getJSONArray("child");
                for (int i = 0; i < productList.size(); i++) {
                    JSONObject productObj = productList.getJSONObject(i);
                    productObj.put("parentId", companyId);
                    treeList.add(productObj);
                    String productId = productObj.getString("id");
                    JSONArray majorList = productObj.getJSONArray("child");
                    for (int k = 0; k < majorList.size(); k++) {
                        JSONObject majorObj = majorList.getJSONObject(k);
                        majorObj.put("parentId", productId);
                        treeList.add(majorObj);
                        String majorId = majorObj.getString("id");
                        JSONArray analysisList = majorObj.getJSONArray("child");
                        if(analysisList!=null){
                            for (int t = 0; t < analysisList.size(); t++) {
                                JSONObject analysisObj = analysisList.getJSONObject(t);
                                analysisObj.put("parentId", majorId);
                                treeList.add(analysisObj);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("同步SDM仿真分析项异常", e);
        }
        return treeList;
    }
    /**
     * 获取SDM平台的数据准备单
     */
    public JSONObject getSdmAnalysisExcelList(String analysisId) {
        JSONObject resultJson = new JSONObject();
        JSONObject paramJson = new JSONObject();
        try {
            String postIP = SysPropertiesUtil.getGlobalProperty("SDMUrl");
            String portUrl = postIP+"/hwe/services/rdm/getAnalysisExcel?id="+analysisId;
            JSONObject resultObj = HttpRequestUtil.doPost(portUrl, paramJson);
            if (resultObj.getIntValue("code") == 200) {
                resultJson = resultObj.getJSONObject("result");
            }
        } catch (Exception e) {
            logger.error("获取SDM平台的数据准备单异常", e);
        }
        return resultJson;
    }
    /**
     * （4）推送项目信息和分析项列表
     */
    public void sendProjectInfo(JSONObject paramObj){
        try {
            JSONObject projectInfo = new JSONObject();
            projectInfo.put("projectCode",paramObj.getString("fzNumber"));
            projectInfo.put("projectName",paramObj.getString("questName"));
            List<JSONObject> stageList = new ArrayList<>();
            JSONObject stageObj = new JSONObject();
            stageObj.put("projectStageDate", DateUtil.formatDate(new Date(),"yyyy-MM-dd"));
            stageObj.put("projectStage", "仿真任务");
            stageList.add(stageObj);
            projectInfo.put("projectStageInfo",stageList);
            projectInfo.put("projectType",convertProjectType(paramObj.getString("taskResource")));
            projectInfo.put("projectProductRelation",paramObj.getString("applicationType"));
            //获取仿真室主任 PDM账号
            String fzszrId = paramObj.getString("fzszrId");
            OsUser osUser=osUserManager.get(fzszrId);
            projectInfo.put("projectCreator",osUser.getPdmUserNo());
            projectInfo.put("projectCreatorName",osUser.getFullname());
            projectInfo.put("projectDesc","无");
            //拼接分析项数据
            List<JSONObject> analysisList = new ArrayList<>();
            JSONObject analysisObj = new JSONObject();
            JSONObject fzfxxObj = fzdxDao.getAnalysisObj(paramObj.getString("fzlbId"));
            analysisObj.put("id", fzfxxObj.getString("sdmAnalysisId"));
            analysisObj.put("startDate",DateUtil.formatDate(new Date(),"yyyy-MM-dd"));
            //获取任务节点数据
            JSONArray taskList = paramObj.getJSONArray("SUB_fzzxGrid");
            JSONObject taskObj = taskList.getJSONObject(taskList.size()-1);
            analysisObj.put("endDate",taskObj.getString("timeNode"));
            analysisObj.put("type", fzfxxObj.getString("sdmAnalysisType"));
            analysisObj.put("client", "挖机");
            analysisList.add(analysisObj);
            projectInfo.put("analysisList",analysisList);
            String postIP = SysPropertiesUtil.getGlobalProperty("SDMUrl");
            String portUrl = postIP+"/hwe/services/rdm/createProject";
            JSONObject resultJson = HttpRequestUtil.doPost(portUrl, projectInfo);
            //保存接口返回信息
            projectInfo.put("resultCode",resultJson.getIntValue("code"));
            projectInfo.put("resultMessage",resultJson.getString("message"));
            projectInfo.put("applyId",paramObj.getString("id"));
            projectInfo.put("id", IdUtil.getId());
            projectInfo.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            projectInfo.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            projectInfo.put("projectStageInfo",JSONObject.toJSONString(projectInfo.get("projectStageInfo")));
            projectInfo.put("analysisList",JSONObject.toJSONString(projectInfo.get("analysisList")));
            //先判断是否已经推送过，推送过则更新
            JSONObject sendObj = sdmDao.getSdmProjectObj(paramObj.getString("id"));
            if(sendObj==null){
                sdmDao.addSdmProject(projectInfo);
            }else{
                projectInfo.put("id",sendObj.getString("id"));
                sdmDao.updateSdmProject(projectInfo);
            }
        }catch (Exception e){
            logger.error("推送项目信息异常",e);
        }
    }
    /**
     * （7）创建委托书
     */
    public JSONObject sendAssignmentInfo(JSONObject paramObj){
        JSONObject returnJson = new JSONObject();
        try {
            JSONObject sendObj = sdmDao.getSdmAssignmentObj(paramObj.getString("id"));

            String assignmentCode;
            if(sendObj!=null){
                assignmentCode = sendObj.getString("assignmentCode");
            }else{
                String seqNo = sysSeqIdManager.genSequenceNo("assignmentCode", "1");
                assignmentCode = "RDM_FZWT"+seqNo+"_A";
            }
            JSONObject assignMentInfo = new JSONObject();
            assignMentInfo.put("assignmentCode",assignmentCode);
            assignMentInfo.put("assignmentName",paramObj.getString("questName"));

            assignMentInfo.put("assignmentVersion",1);
            assignMentInfo.put("simulationCompany","挖机");
            assignMentInfo.put("from","RDM");
            assignMentInfo.put("projectCode",paramObj.getString("fzNumber"));
            assignMentInfo.put("projectName",paramObj.getString("questName"));
            assignMentInfo.put("simulationType",paramObj.getString("taskType"));
            assignMentInfo.put("simulationCategory",convertProjectType(paramObj.getString("taskResource")));
            assignMentInfo.put("assignmentNumber",1);
            assignMentInfo.put("assignmentObject",paramObj.getString("fzdx"));
            JSONArray taskList = paramObj.getJSONArray("SUB_fzzxGrid");
            JSONObject taskObj = taskList.getJSONObject(taskList.size()-1);
            assignMentInfo.put("completionDate",taskObj.getString("timeNode"));

            assignMentInfo.put("ratedHours","");
            assignMentInfo.put("planHours","");
            assignMentInfo.put("assignmentCost","");
            assignMentInfo.put("expensesCost","");

            assignMentInfo.put("productStage",paramObj.getString("productStage"));
            assignMentInfo.put("projectProductRelation",paramObj.getString("applicationType"));
            assignMentInfo.put("productDevelopmentType",convertProjectType(paramObj.getString("taskResource")));
            assignMentInfo.put("productDevelopmentStage",paramObj.getString("productStage"));
            //拼接委托人
            List<String> clientList = new ArrayList<>();
            String clientString = paramObj.getString("fzszr")+" "+ DateUtil.formatDate(new Date(),"yyyy-MM-dd");
            clientList.add(clientString);
            assignMentInfo.put("client",clientList);

            assignMentInfo.put("centerPerson",new ArrayList<>());
            assignMentInfo.put("simulationPerson",new ArrayList<>());
            assignMentInfo.put("technologyPresident",new ArrayList<>());
            assignMentInfo.put("simulationCenter",new ArrayList<>());
            assignMentInfo.put("simulationDate", DateUtil.formatDate(new Date(),"yyyy-MM-dd"));


            //拼接分析项数据
            List<JSONObject> analysisList = new ArrayList<>();
            JSONObject analysisObj = new JSONObject();
            JSONObject fzfxxObj = fzdxDao.getAnalysisObj(paramObj.getString("fzlbId"));
            analysisObj.put("simuTaskId", fzfxxObj.getString("sdmAnalysisId"));
            analysisObj.put("simuTaskName", fzfxxObj.getString("sdmAnalysisName"));
            analysisObj.put("modelCode","");
            analysisObj.put("planStartDate", DateUtil.formatDate(new Date(),"yyyy-MM-dd"));
            analysisObj.put("planEndDate",taskObj.getString("timeNode"));
            analysisObj.put("simulationWorkhour","");
            analysisObj.put("calculateWorkhour","");
            analysisObj.put("planAdjustmentTimes",1);
            analysisObj.put("desc","无");
            analysisObj.put("abilityMaster","");
            analysisObj.put("abilityMasterPhone","");
            analysisList.add(analysisObj);
            assignMentInfo.put("analysisList",analysisList);


            String postIP = SysPropertiesUtil.getGlobalProperty("SDMUrl");
            String portUrl = postIP+"/hwe/services/rdm/createAssignmentForSDM";
            JSONObject resultJson = HttpRequestUtil.doPost(portUrl, assignMentInfo);
            //保存接口返回信息
            assignMentInfo.put("resultCode",resultJson.getIntValue("code"));
            assignMentInfo.put("resultMessage",resultJson.getString("message"));
            assignMentInfo.put("applyId",paramObj.getString("id"));
            assignMentInfo.put("id", IdUtil.getId());
            assignMentInfo.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            assignMentInfo.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            assignMentInfo.put("client",JSONObject.toJSONString(assignMentInfo.get("client")));
            assignMentInfo.put("centerPerson",JSONObject.toJSONString(assignMentInfo.get("centerPerson")));
            assignMentInfo.put("simulationPerson",JSONObject.toJSONString(assignMentInfo.get("simulationPerson")));
            assignMentInfo.put("technologyPresident",JSONObject.toJSONString(assignMentInfo.get("technologyPresident")));
            assignMentInfo.put("simulationCenter",JSONObject.toJSONString(assignMentInfo.get("simulationCenter")));
            assignMentInfo.put("analysisList",JSONObject.toJSONString(assignMentInfo.get("analysisList")));
            //先判断是否已经推送过，推送过则更新
            if(sendObj==null){
                sdmDao.addSdmAssignment(assignMentInfo);
            }else{
                assignMentInfo.put("id",sendObj.getString("id"));
                sdmDao.updateSdmAssignment(assignMentInfo);
            }
            returnJson.put("assignmentId",assignmentCode);
            returnJson.put("assignmentName",paramObj.getString("questName"));
            returnJson.put("analysisId",fzfxxObj.getString("sdmAnalysisId"));
            returnJson.put("simuTaskName",fzfxxObj.getString("sdmAnalysisName"));

        }catch (Exception e){
            logger.error("推送委托书信息异常",e);
        }
        return returnJson;
    }
    /**
     * （5）创建SDM仿真任务
     * */
    public void sendTaskInfo(JSONObject paramObj,JSONObject assignmentObj){
        JSONObject retrunJson = new JSONObject();
        try {
            JSONObject taskInfoObj = new JSONObject();
            JSONObject sendObj = sdmDao.getSdmTaskObj(paramObj.getString("id"));
            String simuTaskId;
            if(sendObj!=null){
                simuTaskId = sendObj.getString("simuTaskId");
            }else{
                String seqNo = sysSeqIdManager.genSequenceNo("simuTaskID", "1");
                simuTaskId = "RDM_FZWT"+seqNo+"_A";
            }
            taskInfoObj.put("simuTaskId",simuTaskId);
            List<String> simuTaskIdRelativeList = new ArrayList<>();
            simuTaskIdRelativeList.add(simuTaskId);
            taskInfoObj.put("simuTaskIdRelative",simuTaskIdRelativeList);
            taskInfoObj.put("analysisId",assignmentObj.getString("analysisId"));
            taskInfoObj.put("assignmentId",assignmentObj.getString("assignmentId")+".1");
            taskInfoObj.put("assignmentName",assignmentObj.getString("assignmentName"));
            taskInfoObj.put("simuTaskStatus","CREATE");
            taskInfoObj.put("projectProductRelation",paramObj.getString("applicationType"));
            taskInfoObj.put("projectCode",paramObj.getString("fzNumber"));
            taskInfoObj.put("contract","");
            taskInfoObj.put("contractName","");
            taskInfoObj.put("from","RDM");
            taskInfoObj.put("testInfoList",new ArrayList<>());
            //查找数据准备单
            String analysisId = assignmentObj.getString("analysisId");
            JSONObject fileObj = getSdmAnalysisExcelList(analysisId);
            taskInfoObj.put("excelFile",fileObj.getString("filePath"));
            taskInfoObj.put("modelFile",fileObj.getString("filePath"));

            //拼接提报人信息
            String submitor = paramObj.getString("fzszr");
            String fzszrId = paramObj.getString("fzszrId");
            OsUser osUser=osUserManager.get(fzszrId);
            taskInfoObj.put("submitor",submitor);
            taskInfoObj.put("submitorDept",paramObj.getString("department"));
            taskInfoObj.put("email",osUser.getEmail());
            taskInfoObj.put("submitorInfo",osUser.getMobile());
            taskInfoObj.put("company","XCMG_WJ");

            //任务信息
            taskInfoObj.put("taskType",paramObj.getString("taskType"));
            taskInfoObj.put("taskCategory",convertProjectType(paramObj.getString("taskResource")));
            JSONArray taskList = paramObj.getJSONArray("SUB_fzzxGrid");
            JSONObject taskObj = taskList.getJSONObject(taskList.size()-1);
            taskInfoObj.put("taskStartDate",DateUtil.formatDate(new Date(),"yyyy-MM-dd"));
            taskInfoObj.put("taskEndDate",taskObj.getString("timeNode"));
            taskInfoObj.put("taskBackgroud",paramObj.getString("fzmd"));
            taskInfoObj.put("taskTarget",paramObj.getString("fzmd"));
            taskInfoObj.put("taskDetail",assignmentObj.getString("assignmentName"));
            String zxry = taskObj.getString("zxry");
            String zxryArry[] = zxry.split(",");
            String zxryId = taskObj.getString("zxryId");
            OsUser zxryUser = osUserManager.get(zxryId);
            String pdmAccount = zxryUser.getPdmUserNo();
            taskInfoObj.put("receiveUserName",pdmAccount);
            taskInfoObj.put("desc","");
            taskInfoObj.put("versionNumber",1);
            taskInfoObj.put("planAdjustmentTimes",1);
            taskInfoObj.put("simulationWorker",pdmAccount);
            String postIP = SysPropertiesUtil.getGlobalProperty("SDMUrl");
            String portUrl = postIP+"/hwe/services/rdm/createSimulationTaskForRDM";
            JSONObject resultJson = HttpRequestUtil.doPost(portUrl, taskInfoObj);
            //保存接口返回信息
            taskInfoObj.put("resultCode",resultJson.getIntValue("code"));
            taskInfoObj.put("resultMessage",resultJson.getString("message"));
            taskInfoObj.put("applyId",paramObj.getString("id"));
            taskInfoObj.put("id", IdUtil.getId());
            taskInfoObj.put("taskId",taskObj.getString("id"));
            taskInfoObj.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            taskInfoObj.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            taskInfoObj.put("simuTaskIdRelative",JSONObject.toJSONString(taskInfoObj.get("simuTaskIdRelative")));
            taskInfoObj.put("testInfoList",JSONObject.toJSONString(taskInfoObj.get("testInfoList")));
            //先判断是否已经推送过，推送过则更新
            if(sendObj==null){
                sdmDao.addSdmTask(taskInfoObj);
            }else{
                taskInfoObj.put("id",sendObj.getString("id"));
                sdmDao.updateSdmTask(taskInfoObj);
            }
        }catch (Exception e){
            logger.error("创建SDM仿真任务异常",e);
        }
    }
    public JsonPageResult<?> getSdmProjectList(HttpServletRequest request) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            Map<String, Object> params = new HashMap<>(16);
            List<Map<String, Object>> list = new ArrayList<>();
            List<Map<String, Object>> totalList = new ArrayList<>();
            params = CommonFuns.getSearchParam(params, request, true);
            list = sdmDao.getSdmProject(params);
            params = new HashMap<>(16);
            params = CommonFuns.getSearchParam(params, request, false);
            totalList = sdmDao.getSdmProject(params);
            CommonFuns.convertDate(list);
            // 返回结果
            result.setData(list);
            result.setTotal(totalList.size());
        } catch (Exception e) {
            logger.error("Exception in 查询异常", e);
            result.setSuccess(false);
            result.setMessage("系统异常");
        }
        return result;
    }
    public JsonPageResult<?> getSdmAssignmentList(HttpServletRequest request) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            Map<String, Object> params = new HashMap<>(16);
            List<Map<String, Object>> list = new ArrayList<>();
            List<Map<String, Object>> totalList = new ArrayList<>();
            params = CommonFuns.getSearchParam(params, request, true);
            list = sdmDao.getSdmAssignment(params);
            params = new HashMap<>(16);
            params = CommonFuns.getSearchParam(params, request, false);
            totalList = sdmDao.getSdmAssignment(params);
            CommonFuns.convertDate(list);
            // 返回结果
            result.setData(list);
            result.setTotal(totalList.size());
        } catch (Exception e) {
            logger.error("Exception in 查询异常", e);
            result.setSuccess(false);
            result.setMessage("系统异常");
        }
        return result;
    }
    public JsonPageResult<?> getSdmTaskList(HttpServletRequest request) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            Map<String, Object> params = new HashMap<>(16);
            List<Map<String, Object>> list = new ArrayList<>();
            List<Map<String, Object>> totalList = new ArrayList<>();
            params = CommonFuns.getSearchParam(params, request, true);
            list = sdmDao.getSdmTaskList(params);
            params = new HashMap<>(16);
            params = CommonFuns.getSearchParam(params, request, false);
            totalList = sdmDao.getSdmTaskList(params);
            CommonFuns.convertDate(list);
            // 返回结果
            result.setData(list);
            result.setTotal(totalList.size());
        } catch (Exception e) {
            logger.error("Exception in 查询异常", e);
            result.setSuccess(false);
            result.setMessage("系统异常");
        }
        return result;
    }
    /**
     * （8）对仿真任务进行打分
     */
    public JSONObject sendScoreInfo(JSONObject paramObj){
        JSONObject returnJson = new JSONObject();
        try {
            JSONObject sendObj = sdmDao.getSdmScoreObj(paramObj.getString("applyId"));

            String postIP = SysPropertiesUtil.getGlobalProperty("SDMUrl");
            String portUrl = postIP+"/hwe/services/rdm/scoringSimulationTaskForRDM";
            JSONObject postJson = new JSONObject();
            postJson.put("simuTaskId",paramObj.getString("simuTaskId"));
            postJson.put("creator",paramObj.getString("creator"));
            postJson.put("star",paramObj.getString("star"));
            postJson.put("comment",paramObj.getString("comment"));
            JSONObject resultJson = HttpRequestUtil.doPost(portUrl, postJson);
            //保存接口返回信息
            paramObj.put("resultCode",resultJson.getIntValue("code"));
            paramObj.put("resultMessage",resultJson.getString("message"));
            paramObj.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            paramObj.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            //先判断是否已经推送过，推送过则更新
            if(sendObj==null){
                paramObj.put("id", IdUtil.getId());
                sdmDao.addSdmScore(paramObj);
            }else{
                paramObj.put("id",sendObj.getString("id"));
                sdmDao.updateSdmScore(paramObj);
            }
        }catch (Exception e){
            logger.error("推送打分信息异常",e);
        }
        return returnJson;
    }

    /**
     * （9）对仿真任务进行采纳信息推送
     */
    public JSONObject sendAdoptInfo(JSONObject paramObj){
        JSONObject returnJson = new JSONObject();
        try {
            JSONObject sendObj = sdmDao.getSdmAdoptObj(paramObj.getString("applyId"));

            String postIP = SysPropertiesUtil.getGlobalProperty("SDMUrl");
            String portUrl = postIP+"/hwe/services/rdm/sendAdoptInfo";
            JSONObject postJson = new JSONObject();
            postJson.put("simuTaskId",paramObj.getString("simuTaskId"));
            postJson.put("adoptResult",paramObj.getString("adoptResult"));
            postJson.put("adoptDescription",paramObj.getString("adoptDescription"));
            JSONObject resultJson = HttpRequestUtil.doPost(portUrl, postJson);
            //保存接口返回信息
            paramObj.put("resultCode",resultJson.getIntValue("code"));
            paramObj.put("resultMessage",resultJson.getString("message"));
            paramObj.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            paramObj.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            //先判断是否已经推送过，推送过则更新
            if(sendObj==null){
                paramObj.put("id", IdUtil.getId());
                sdmDao.addSdmAdopt(paramObj);
            }else{
                paramObj.put("id",sendObj.getString("id"));
                sdmDao.updateSdmAdopt(paramObj);
            }
        }catch (Exception e){
            logger.error("推送采纳信息异常",e);
        }
        return returnJson;
    }

    /**
     * （10）对仿真任务进行实施效果信息推送
     */
    public JSONObject sendImplementInfo(JSONObject paramObj){
        JSONObject returnJson = new JSONObject();
        try {
            String applyId = paramObj.getString("applyId");
            JSONObject sendObj = sdmDao.getSdmImplementObj(applyId);

            String postIP = SysPropertiesUtil.getGlobalProperty("SDMUrl");
            String portUrl = postIP+"/hwe/services/rdm/sendImplementationEffectInfo";
            JSONObject postJson = new JSONObject();
            postJson.put("simuTaskId",paramObj.getString("simuTaskId"));
            postJson.put("implementation",paramObj.getString("implementation"));
            String ftpUrl = sendFileByFtp(applyId);
            postJson.put("url",ftpUrl);
            JSONObject resultJson = HttpRequestUtil.doPost(portUrl, postJson);
            //保存接口返回信息
            paramObj.put("resultCode",resultJson.getIntValue("code"));
            paramObj.put("resultMessage",resultJson.getString("message"));
            paramObj.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            paramObj.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            //先判断是否已经推送过，推送过则更新
            if(sendObj==null){
                paramObj.put("id", IdUtil.getId());
                sdmDao.addSdmImplement(paramObj);
            }else{
                paramObj.put("id",sendObj.getString("id"));
                sdmDao.updateSdmImplement(paramObj);
            }
        }catch (Exception e){
            logger.error("推送实施效果信息异常",e);
        }
        return returnJson;
    }
    public String sendFileByFtp(String applyId){
            StringBuffer stringBuffer = new StringBuffer();
        try {
            List<String> fzsjIds = new ArrayList<>();
            fzsjIds.add(applyId);
            JSONObject params = new JSONObject();
            params.put("belongDetailIds", fzsjIds);
            List<JSONObject> fileInfos = fzsjService.queryFzsjFileList(params);
            if(fileInfos.size()>0){
                String filePath = WebAppUtil.getProperty("fzsjFilePathBase");
                String fileName;
                String suffix;
                String fullFilePath;
                String fileId;
                String ftpInfo = SysPropertiesUtil.getGlobalProperty("SDM_FTP_INFO");
                String ftpInfoArray[] = ftpInfo.split(",");
                JSONObject paramJson = new JSONObject();
                paramJson.put("ftpIp",ftpInfoArray[0]);
                paramJson.put("ftpPort",ftpInfoArray[1]);
                paramJson.put("ftpUserName",ftpInfoArray[2]);
                paramJson.put("ftpUserPwd",ftpInfoArray[3]);
                paramJson.put("ftpFilePath",ftpInfoArray[4]);
                stringBuffer.append(ftpInfoArray[4]).append("/");
                JSONObject fileObj = fileInfos.get(0);
                fileName = fileObj.getString("fileName");
                fileId = fileObj.getString("id");
                suffix = com.redxun.rdmCommon.core.manager.CommonFuns.toGetFileSuffix(fileName);
                fullFilePath = filePath + File.separator + applyId + File.separator + fileId + "." + suffix;
                stringBuffer.append(fileName);
                paramJson.put("fullFilePath",fullFilePath);
                paramJson.put("fileName",fileName);
                RdmFTPUtil.ftpTool(paramJson);
            }
        }catch (Exception e){
            logger.error("发送文件失败！",e.getMessage());
        }
        return stringBuffer.toString();
    }






    public JsonPageResult<?> getSdmScoreList(HttpServletRequest request) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            Map<String, Object> params = new HashMap<>(16);
            List<Map<String, Object>> list = new ArrayList<>();
            List<Map<String, Object>> totalList = new ArrayList<>();
            params = CommonFuns.getSearchParam(params, request, true);
            list = sdmDao.getSdmScore(params);
            params = new HashMap<>(16);
            params = CommonFuns.getSearchParam(params, request, false);
            totalList = sdmDao.getSdmScore(params);
            CommonFuns.convertDate(list);
            // 返回结果
            result.setData(list);
            result.setTotal(totalList.size());
        } catch (Exception e) {
            logger.error("Exception in 查询异常", e);
            result.setSuccess(false);
            result.setMessage("系统异常");
        }
        return result;
    }
    public JsonPageResult<?> getSdmAdoptList(HttpServletRequest request) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            Map<String, Object> params = new HashMap<>(16);
            List<Map<String, Object>> list = new ArrayList<>();
            List<Map<String, Object>> totalList = new ArrayList<>();
            params = CommonFuns.getSearchParam(params, request, true);
            list = sdmDao.getSdmAdopt(params);
            params = new HashMap<>(16);
            params = CommonFuns.getSearchParam(params, request, false);
            totalList = sdmDao.getSdmAdopt(params);
            CommonFuns.convertDate(list);
            // 返回结果
            result.setData(list);
            result.setTotal(totalList.size());
        } catch (Exception e) {
            logger.error("Exception in 查询异常", e);
            result.setSuccess(false);
            result.setMessage("系统异常");
        }
        return result;
    }
    public JsonPageResult<?> getSdmImplementList(HttpServletRequest request) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            Map<String, Object> params = new HashMap<>(16);
            List<Map<String, Object>> list = new ArrayList<>();
            List<Map<String, Object>> totalList = new ArrayList<>();
            params = CommonFuns.getSearchParam(params, request, true);
            list = sdmDao.getSdmImplement(params);
            params = new HashMap<>(16);
            params = CommonFuns.getSearchParam(params, request, false);
            totalList = sdmDao.getSdmImplement(params);
            CommonFuns.convertDate(list);
            // 返回结果
            result.setData(list);
            result.setTotal(totalList.size());
        } catch (Exception e) {
            logger.error("Exception in 查询异常", e);
            result.setSuccess(false);
            result.setMessage("系统异常");
        }
        return result;
    }

    /**
     * （11）RDM 推送仿真报告
     */
    public JSONObject sendReportInfo(JSONObject paramObj){
        JSONObject returnJson = new JSONObject();
        try {
            //查看是否允许推送仿真报告
            String isAllowSendReport = SysPropertiesUtil.getGlobalProperty("isAllowSendReport");
            if("NO".equals(isAllowSendReport)){
                return returnJson;
            }
            String taskId = paramObj.getString("taskId");
            String applyId = paramObj.getString("applyId");
            JSONObject params = new JSONObject();
            List<String> fzsjIds = new ArrayList<>();
            fzsjIds.add(taskId);
            params.put("belongDetailIds", fzsjIds);
            List<JSONObject> fileInfos = fzsjService.queryFzsjFileList(params);
            if(fileInfos.size()>0){
                StringBuffer stringBuffer = new StringBuffer();
                String filePath = WebAppUtil.getProperty("fzsjFilePathBase");
                String fileName;
                String suffix;
                String fullFilePath;
                String fileId;
                String ftpInfo = SysPropertiesUtil.getGlobalProperty("SDM_FTP_INFO");
                String ftpInfoArray[] = ftpInfo.split(",");
                JSONObject paramJson = new JSONObject();
                paramJson.put("ftpIp",ftpInfoArray[0]);
                paramJson.put("ftpPort",ftpInfoArray[1]);
                paramJson.put("ftpUserName",ftpInfoArray[2]);
                paramJson.put("ftpUserPwd",ftpInfoArray[3]);
                paramJson.put("ftpFilePath",ftpInfoArray[4]);
                stringBuffer.append(ftpInfoArray[4]).append("/");
                JSONObject fileObj = fileInfos.get(0);
                fileName = fileObj.getString("fileName");
                fileId = fileObj.getString("id");
                suffix = com.redxun.rdmCommon.core.manager.CommonFuns.toGetFileSuffix(fileName);
                fullFilePath = filePath + File.separator + taskId + File.separator + fileId + "." + suffix;
                stringBuffer.append(fileName);
                paramJson.put("fullFilePath",fullFilePath);
                paramJson.put("fileName",fileName);
                RdmFTPUtil.ftpTool(paramJson);

                JSONObject sendObj = sdmDao.getSdmReportObj(applyId);
                String postIP = SysPropertiesUtil.getGlobalProperty("SDMUrl");
                String portUrl = postIP+"/hwe/services/rdm/sendSimulationReport";
                JSONObject postJson = new JSONObject();
                postJson.put("simuTaskId",paramObj.getString("simuTaskId"));
                postJson.put("creator",paramObj.getString("creator"));
                postJson.put("filename",fileName);
                postJson.put("download_url",stringBuffer.toString());
                JSONObject resultJson = HttpRequestUtil.doPost(portUrl, postJson);
                //保存接口返回信息
                paramObj.put("resultCode",resultJson.getIntValue("code"));
                paramObj.put("resultMessage",resultJson.getString("message"));
                paramObj.put("filename",fileName);
                paramObj.put("download_url",stringBuffer.toString());
                paramObj.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                paramObj.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                //先判断是否已经推送过，推送过则更新
                if(sendObj==null){
                    paramObj.put("id", IdUtil.getId());
                    sdmDao.addSdmReport(paramObj);
                }else{
                    paramObj.put("id",sendObj.getString("id"));
                    sdmDao.updateSdmReport(paramObj);
                }
            }
        }catch (Exception e){
            logger.error("推送仿真报告信息异常",e);
        }
        return returnJson;
    }

    public JSONObject receiveSdmReport(String postData) {
        JSONObject result = new JSONObject();
        try {
            JSONObject postDataJson = JSONObject.parseObject(postData);
            if(StringUtils.isEmpty(postDataJson.getString("simuTaskId"))){
                result.put("code", 199);
                result.put("messages", "仿真任务Id不能为空！");
                return result;
            }
            if(StringUtils.isEmpty(postDataJson.getString("fileFtpUrl"))){
                result.put("code", 199);
                result.put("messages", "报告文件地址不能为空！");
                return result;
            }
            if(StringUtils.isEmpty(postDataJson.getString("fileName"))){
                result.put("code", 199);
                result.put("messages", "仿真报告名称不能为空！");
                return result;
            }
            JSONObject fileObj = sdmDao.getSdmReceiveReportObj(postDataJson);
            if(fileObj!=null){
                result.put("code", 199);
                result.put("messages", "仿真报告已存在！");
                return result;
            }
            postDataJson.put("id",IdUtil.getId());
            postDataJson.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            postDataJson.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            sdmDao.addSdmReceiveReport(postDataJson);
            result.put("code", 200);
            result.put("messages", "发送成功！");
        } catch (Exception e) {
            logger.error("仿真报告接收失败" + e.getMessage(), e);
            result.put("code", 199);
            result.put("messages", "发送失败！");
            throw e;
        }
        return result;
    }

    /**
     * 根据任务来源转换为SDM系统中项目类型
     * 1.SDM 产品研发类：新品研发、在研/预研产品、市场改进
     * 2.SDM 技术研究类：核心技术研究
     * */
    public String convertProjectType(String taskResource){
        String projectType = "";
        if(taskResource.equals("xpyf")||taskResource.equals("zyOrYycp")||taskResource.equals("scgj")){
            projectType = "产品研发类";
        }else if(taskResource.equals("hxjsyj")){
            projectType = "技术研究类";
        }
        return projectType;
    }
    /**
     * 1.RDM ：同意改进、部分改进、不同意改进
     * 2.SDM ：采纳/不采纳/部分采纳
     * */
    public String convertAdoptType(String adoptType){
        String projectType = "";
        if(adoptType.equals("tygj")){
            projectType = "采纳";
        }else if(adoptType.equals("btygj")){
            projectType = "不采纳";
        }else if(adoptType.equals("bfgj")){
            projectType = "部分采纳";
        }
        return projectType;
    }
}
