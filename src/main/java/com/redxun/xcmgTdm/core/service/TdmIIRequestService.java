package com.redxun.xcmgTdm.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.*;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.bpm.core.manager.BpmSolutionManager;
import com.redxun.bpm.core.manager.BpmTaskManager;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.ExceptionUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.org.api.model.IUser;
import com.redxun.org.api.service.UserService;
import com.redxun.rdmCommon.core.controller.RdmApiController;
import com.redxun.rdmCommon.core.dao.RdmDao;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.sys.org.dao.OsGroupDao;
import com.redxun.sys.org.entity.OsGroup;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.zlgjNPI.core.dao.ZlgjWTDao;
import com.redxun.zlgjNPI.core.manager.ZlgjWTService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import sun.misc.BASE64Decoder;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TdmIIRequestService {
    private static Logger logger = LoggerFactory.getLogger(TdmIIRequestService.class);
    @Autowired
    private ZlgjWTService zlgjWTService;
    @Autowired
    private RdmDao rdmDao;
    @Autowired
    private OsGroupDao osGroupDao;
    @Autowired
    private BpmSolutionManager bpmSolutionManager;
    @Autowired
    private UserService userService;
    @Autowired
    private BpmInstManager bpmInstManager;
    @Autowired
    private ZlgjWTDao zlgjWTDao;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private BpmTaskManager bpmTaskManager;

    public void createZlgjFormTdmII(JSONObject result, String postData, String whatTypeToStart) throws Exception {
        JSONObject outSysData = JSONObject.parseObject(postData);
        logger.error(outSysData.getString("outSystem") + "向RDM创建质量改进建议流程开始，Id：" +
                outSysData.getString("id"));
        System.out.println("");
        //1.外部传过来的表单正确性校验，成功则生成Rdm质量改进需要的表单数据
        JSONObject zlgjJson = this.synValidAndTransZlgjFormTdmII(result, outSysData);
        if (!result.getBoolean("success")) {
            return;
        }
        //2.查找质量改进的solutionid
        String solId = this.synGetZlgjSolId(result);
        if (!result.getBoolean("success")) {
            return;
        }
        //3.启动流程
        JsonResult processStartResult = this.synStartProcess(solId, zlgjJson, whatTypeToStart);
        if (!processStartResult.getSuccess()) {
            result.put("success", false);
            result.put("message", processStartResult.getMessage() + ":" + processStartResult.getData().toString());
            return;
        }
        String businessId = processStartResult.getData().toString();
        //4.将对应信息写入zlgj_wtsb_outsystem
        this.synWriteOutsystemTable(outSysData, businessId);
        //5.同步文件信息
        this.synFile(outSysData, businessId, zlgjJson.getString("CREATE_BY_"));
    }

    //..1.外部传过来的表单正确性校验，成功则生成Rdm质量改进需要的表单数据
    private JSONObject synValidAndTransZlgjFormTdmII(JSONObject result, JSONObject outSysData) {
        JSONObject params = new JSONObject();
        JSONObject zlgjJson = new JSONObject();
        String outSystemId = outSysData.getString("id");
        if (StringUtil.isEmpty(outSystemId)) {
            result.put("success", false);
            result.put("message", "传输失败,'业务主键'为空！");
            return null;
        }
        zlgjJson.put("outSystemId", outSystemId);
        //问题类型:"XPSZ"->wtlx
        zlgjJson.put("wtlx", "XPSZ");
        //问题响应要求:problemResponseRequirementsName->jjcd
        String jjcd = outSysData.getString("problemResponseRequirementsName");
        if (StringUtil.isEmpty(jjcd)) {
            result.put("success", false);
            result.put("message", "传输失败,'问题响应要求'为空！");
            return null;
        }
        zlgjJson.put("jjcd", jjcd);
        //机型类别:modelCategoryName->jiXing
        String jiXing = ZlgjWTService.toGetJixingCodeByName(outSysData.getString("modelCategoryName"));
        if (StringUtil.isEmpty(jiXing)) {
            result.put("success", false);
            result.put("message", "传输失败,'机型类别'为空！");
            return null;
        }
        zlgjJson.put("jiXing", jiXing);
        //设计型号:designModel->smallJiXing
        String smallJiXing = outSysData.getString("designModel");
        if (StringUtil.isEmpty(smallJiXing)) {
            result.put("success", false);
            result.put("message", "传输失败,'设计型号'为空！");
            return null;
        }
        zlgjJson.put("smallJiXing", smallJiXing);
        //异常描述:exceptionDescription->wtms
        String wtms = outSysData.getString("exceptionDescription");
        if (StringUtil.isEmpty(wtms)) {
            result.put("success", false);
            result.put("message", "传输失败，'异常描述'为空！");
            return null;
        }
        zlgjJson.put("wtms", wtms);
        //故障系统:faultySystemName->gzxt
        String gzxt = ZlgjWTService.toGetGzxtCodeByName(outSysData.getString("faultySystemName"));
        if (StringUtil.isEmpty(gzxt)) {
            result.put("success", false);
            result.put("message", "传输失败，'故障系统'为空！");
            return null;
        }
        zlgjJson.put("gzxt", gzxt);
        //故障部件:faultyComponent->gzbw
        String gzbw = outSysData.getString("faultyComponent");
        if (StringUtil.isEmpty(gzbw)) {
            result.put("success", false);
            result.put("message", "传输失败，'故障部件'为空！");
            return null;
        }
        zlgjJson.put("gzbw", gzbw);
        //故障零件:faultyPart->gzlj
        String gzlj = outSysData.getString("faultyPart");
        if (StringUtil.isEmpty(gzlj)) {
            result.put("success", false);
            result.put("message", "传输失败，'故障零件'为空！");
            return null;
        }
        zlgjJson.put("gzlj", gzlj);
        //故障整机:attachedToPins->zjbh
        String zjbh = outSysData.getString("attachedToPins");
        if (StringUtil.isEmpty(zjbh)) {
            result.put("success", false);
            result.put("message", "传输失败，'故障整机'为空！");
            return null;
        }
        zlgjJson.put("zjbh", zjbh);
        //工作小时:workingHours->gzxs
        String gzxs = outSysData.getString("workingHours");
        if (StringUtil.isEmpty(gzxs)) {
            result.put("success", false);
            result.put("message", "传输失败，'工作小时'为空！");
            return null;
        }
        zlgjJson.put("gzxs", gzxs);
        //第一责任人:repUserNo->zrcpzgId,zrcpzgName,ssbmId,ssbmName
        String zrcpzgUserNo = outSysData.getString("repUserNo");
        if (StringUtil.isEmpty(zrcpzgUserNo)) {
            result.put("success", false);
            result.put("message", "传输失败，'第一责任人'为空！");
            return null;
        }
        params.put("userNo", zrcpzgUserNo);
        List<JSONObject> zrcpzgList = rdmDao.queryInJobUserByNo(params);
        if (zrcpzgList == null || zrcpzgList.isEmpty() || zrcpzgList.size() != 1) {
            result.put("success", false);
            result.put("message", "传输失败，'第一责任人'在RDM中无对应或对应多个用户！");
            return null;
        }
        String zrcpzgId = zrcpzgList.get(0).getString("USER_ID_");
        String zrcpzgName = zrcpzgList.get(0).getString("FULLNAME_");
        //第一责任人所在部门id
        OsGroup group = osGroupDao.getMainGroupByUserId(zrcpzgId);
        String ssbmId = group.getGroupId();
        String ssbmName = group.getName();
        if (StringUtil.isEmpty(ssbmId)) {
            result.put("success", false);
            result.put("message", "传输失败，'第一责任人'在RDM中无对应部门！");
            return null;
        }
        zlgjJson.put("zrcpzgId", zrcpzgId);
        zlgjJson.put("zrcpzgName", zrcpzgName);
        zlgjJson.put("ssbmId", ssbmId);
        zlgjJson.put("ssbmName", ssbmName);
        //施工工况:workingCondition->sggk
        String sggk = outSysData.getString("workingCondition");
        if (StringUtil.isEmpty(sggk)) {
            result.put("success", false);
            result.put("message", "传输失败，'施工工况'为空！");
            return null;
        }
        zlgjJson.put("sggk", sggk);
        //零部件供应商:partSupplier->lbjgys
        String lbjgys = outSysData.getString("partSupplier");
        if (StringUtil.isEmpty(lbjgys)) {
            result.put("success", false);
            result.put("message", "传输失败，'零部件供应商'为空！");
            return null;
        }
        zlgjJson.put("lbjgys", lbjgys);
        //交机数量/零部件数量:partQuantity->jjsl
        String jjsl = outSysData.getString("partQuantity");
        if (StringUtil.isEmpty(jjsl)) {
            result.put("success", false);
            result.put("message", "传输失败，'交机数量/零部件数量'为空！");
            return null;
        }
        zlgjJson.put("jjsl", jjsl);
        //故障数量:faultyQuantity->gzsl
        String gzsl = outSysData.getString("faultyQuantity");
        if (StringUtil.isEmpty(gzsl)) {
            result.put("success", false);
            result.put("message", "传输失败，'故障数量'为空！");
            return null;
        }
        zlgjJson.put("gzsl", gzsl);
        //问题排查过程及方法:faultyTroubleshooting->wtpcjc
        String wtpcjc = outSysData.getString("faultyTroubleshooting");
        if (StringUtil.isEmpty(wtpcjc)) {
            result.put("success", false);
            result.put("message", "传输失败，'问题排查过程及方法'为空！");
            return null;
        }
        zlgjJson.put("wtpcjc", wtpcjc);
        //现场处置方法:faultyOnsiteDisposal->xcczfa
        String xcczfa = outSysData.getString("faultyOnsiteDisposal");
        if (StringUtil.isEmpty(xcczfa)) {
            result.put("success", false);
            result.put("message", "传输失败，'现场处置方法'为空！");
            return null;
        }
        zlgjJson.put("xcczfa", xcczfa);
        //改进要求:faultyImprovementRequirements->gjyq
        String gjyq = outSysData.getString("faultyImprovementRequirements");
        if (StringUtil.isEmpty(gjyq)) {
            result.put("success", false);
            result.put("message", "传输失败，'改进要求'为空！");
            return null;
        }
        zlgjJson.put("gjyq", gjyq);
        //改进方法：improvementMethodName->improvementMethod
        String improvementMethod = outSysData.getString("improvementMethodName");
        if (StringUtil.isEmpty(improvementMethod)) {
            result.put("success", false);
            result.put("message", "传输失败，'改进方法'为空！");
            return null;
        }
        zlgjJson.put("improvementMethod", improvementMethod);
        //问题严重程度:faultySeverity->yzcd
        String yzcd = outSysData.getString("faultySeverity");
        if (StringUtil.isEmpty(yzcd)) {
            result.put("success", false);
            result.put("message", "传输失败，'问题严重程度'为空！");
            return null;
        }
        zlgjJson.put("yzcd", yzcd);
        //方案验证计划完成时间:schemeValidationPlanTime->yzTime
        String yzTime = outSysData.getString("schemeValidationPlanTime");
        if (StringUtil.isNotEmpty(yzTime)) {
            zlgjJson.put("yzTime", yzTime);
        }
        //永久方案计划完成时间:permanentSchemePlanTime->yjTime
        String yjTime = outSysData.getString("permanentSchemePlanTime");
        if (StringUtil.isNotEmpty(yjTime)) {
            zlgjJson.put("yjTime", yjTime);
        }
        //排放标准:emissionStandardName--->pfbz
        String pfbz = outSysData.getString("emissionStandardName");
        if (StringUtil.isEmpty(pfbz)) {
            result.put("success", false);
            result.put("message", "传输失败，'排放标准'为空！");
            return null;
        }
        zlgjJson.put("pfbz", pfbz);
        //故障现象:faultyAppearance->gzProgram
        String gzProgram = outSysData.getString("faultyAppearance");
        if (StringUtil.isEmpty(gzProgram)) {
            result.put("success", false);
            result.put("message", "传输失败，'故障现象'为空！");
            return null;
        }
        zlgjJson.put("gzProgram", gzProgram);
        //反馈人:feedbackPersonNo->CREATE_BY_
        //2024年4月1日创建人改为固定人员(王为金)
        String createrUserNo = outSysData.getString("feedbackPersonNo");
        if (StringUtil.isEmpty(createrUserNo)) {
            result.put("success", false);
            result.put("message", "传输失败，'反馈人'为空！");
            return null;
        }
//        params.clear();
//        params.put("userNo", createrUserNo);
//        List<JSONObject> creatorList = rdmDao.queryInJobUserByNo(params);
//        if (creatorList == null || creatorList.isEmpty() || creatorList.size() != 1) {
//            result.put("success", false);
//            result.put("message", "操作失败，反馈人在RDM中无对应或对应多个用户！");
//            return null;
//        }
        Map<String, Object> roleParams = new HashMap<>();
        roleParams.put("groupName", "TDM新品试制质量改进创建人");
        roleParams.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        List<Map<String, String>> userInfos = xcmgProjectOtherDao.queryUserByGroupName(roleParams);
        if (userInfos != null && !userInfos.isEmpty()) {
            zlgjJson.put("CREATE_BY_", userInfos.get(0).get("USER_ID_").toString());
        }

        //附件:FILE_LIST->FILE_LIST
        JSONArray files = JSONArray.parseArray(outSysData.getString("FILE_LIST"));
        if (files.size() == 0) {
            result.put("success", false);
            result.put("message", "传输失败，'故障图片'为空！");
            return null;
        }
        zlgjJson.put("FILE_LIST", outSysData.getString("FILE_LIST"));
        return zlgjJson;
    }

    //..2.查找质量改进建议的solutionid
    private String synGetZlgjSolId(JSONObject result) {
        BpmSolution bpmSol = bpmSolutionManager.getByKey("ZLGJ", "1");
        String solId = "";
        if (bpmSol != null) {
            solId = bpmSol.getSolId();
        } else {
            result.put("success", false);
            result.put("message", "同步失败,质量改进的流程方案获取出现异常！！");
        }
        return solId;
    }

    //..3.启动流程
    private JsonResult synStartProcess(String solId, JSONObject zlgjJson, String whatTypeToStart) throws Exception {
        ProcessMessage handleMessage = new ProcessMessage();
        BpmInst bpmInst = null;
        JsonResult result = new JsonResult();
        IUser currentUser = ContextUtil.getCurrentUser();//暂存
        try {
            IUser user = userService.getByUserId(zlgjJson.getString("CREATE_BY_"));
            ContextUtil.setCurUser(user);
            ProcessHandleHelper.setProcessMessage(handleMessage);
            ProcessStartCmd startCmd = new ProcessStartCmd();
            startCmd.setSolId(solId);
            startCmd.setJsonData(zlgjJson.toJSONString());
            //启动流程
            if (whatTypeToStart.equalsIgnoreCase(RdmApiController.SAVE_DRAFT)) {
                bpmInstManager.doSaveDraft(startCmd);
            } else if (whatTypeToStart.equalsIgnoreCase(RdmApiController.START_PROCESS)) {
                bpmInstManager.doStartProcess(startCmd);
            } else {
                throw new Exception("没有收到具体的启动指令");
            }
            //跳过第二步
            JSONObject processJson = JSONObject.parseObject(startCmd.getJsonData());
            processJson.put("wtId",startCmd.getBusinessKey());
            List<BpmTask> bpmTaskList = bpmTaskManager.getByInstId(startCmd.getBpmInstId());
            ProcessNextCmd processNextCmd = new ProcessNextCmd();
            processNextCmd.setTaskId(bpmTaskList.get(0).getId());
            processNextCmd.setJumpType("AGREE");
            processNextCmd.setJsonData(processJson.toJSONString());
            processNextCmd.setAgentToUserId("1");
            bpmTaskManager.doNext(processNextCmd);
            result.setData(startCmd.getBusinessKey());
        } catch (Exception ex) {
            //把具体的错误放置在内部处理，以显示正确的错误信息提示，在此不作任何的错误处理
            logger.error(ExceptionUtil.getExceptionMessage(ex));
            if (handleMessage.getErrorMsges().size() == 0) {
                handleMessage.addErrorMsg(ex.getMessage());
            }
        } finally {
            //在处理过程中，是否有错误的消息抛出
            if (handleMessage.getErrorMsges().size() > 0) {
                result.setSuccess(false);
                result.setMessage("启动流程失败!");
                result.setData(handleMessage.getErrors());
            } else {
                result.setSuccess(true);
            }
            ProcessHandleHelper.clearProcessCmd();
            ProcessHandleHelper.clearProcessMessage();
            ContextUtil.setCurUser(currentUser);//恢复当前用户
        }
        return result;
    }

    //..4.将对应信息写入zlgj_wtsb_outsystem
    private void synWriteOutsystemTable(JSONObject outSysData, String businessId) {
        JSONObject param = new JSONObject();
        param.put("id", IdUtil.getId());
        param.put("busKeyId", businessId);
        param.put("outSystem", outSysData.getString("outSystem"));
        JSONObject outSystemJson = new JSONObject();
        outSystemJson.put("outSystemId", outSysData.getString("id"));
        param.put("outSystemJson", outSystemJson.toJSONString());
        param.put("CREATE_BY_", "1");
        param.put("CREATE_TIME_", new Date());
        zlgjWTDao.insertZlgjOutSystem(param);
    }

    //..5.同步文件信息,无法控制事务，放到最后
    private void synFile(JSONObject outSysData, String businessId, String creatorId) throws IOException {
        String filePathBase = WebAppUtil.getProperty("zlgjFilePathBase");
        String filePathBase_view = WebAppUtil.getProperty("zlgjFilePathBase_preview");
        JSONArray fileList = outSysData.getJSONArray("FILE_LIST");
        if (fileList != null && !fileList.isEmpty()) {
            for (int fileIndex = 0; fileIndex < fileList.size(); fileIndex++) {
                JSONObject oneFile = fileList.getJSONObject(fileIndex);
                String id = IdUtil.getId();
                // 写入数据库
                Map<String, Object> fileInfo = new HashMap<>();
                fileInfo.put("id", id);
                fileInfo.put("fileName", oneFile.getString("fileName"));
                fileInfo.put("fileSize", oneFile.getString("fileSize"));
                fileInfo.put("wtId", businessId);
                fileInfo.put("fjlx", "gztp");
                fileInfo.put("CREATE_BY_", creatorId);
                zlgjWTDao.addFileInfos(fileInfo);
                // 向下载目录中写入文件
                String filePath = filePathBase + File.separator + businessId;
                File pathFile = new File(filePath);
                if (!pathFile.exists()) {
                    pathFile.mkdirs();
                }
                String suffix = CommonFuns.toGetFileSuffix(oneFile.getString("fileName"));
                String fileFullPath = filePath + File.separator + id + "." + suffix;
                File file = new File(fileFullPath);
                // --图片格式编码--
                BASE64Decoder decoder = new BASE64Decoder();
                byte[] fileContent = decoder.decodeBuffer(oneFile.getString("fileBase64"));
                FileCopyUtils.copy(fileContent, file);
                // 向预览目录中写入文件
                String filePath_view = filePathBase_view + File.separator + businessId;
                File pathFile_view = new File(filePath_view);
                if (!pathFile_view.exists()) {
                    pathFile_view.mkdirs();
                }
                String fileFullPath_view = filePath_view + File.separator + id + "." + suffix;
                File file_view = new File(fileFullPath_view);
                FileCopyUtils.copy(fileContent, file_view);
            }
        }
    }
}
