package com.redxun.rdmCommon.core.manager;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.BpmSolution;
import com.redxun.bpm.core.entity.IExecutionCmd;
import com.redxun.bpm.core.entity.ProcessStartCmd;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.bpm.core.manager.BpmSolutionManager;
import com.redxun.bpm.core.manager.BpmTaskManager;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.ExceptionUtil;
import com.redxun.core.util.FileUtil;
import com.redxun.org.api.model.IUser;
import com.redxun.presaleDocuments.core.service.PresaleDocumentsApplyService;
import com.redxun.presaleDocuments.core.service.PresaleDocumentsFileService;
import com.redxun.presaleDocuments.core.service.PresaleDocumentsService;
import com.redxun.rdmCommon.core.controller.RdmApiController;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.serviceEngineering.core.dao.AttachedDocTranslateDao;
import com.redxun.serviceEngineering.core.service.AttachedDocTranslateManager;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.sys.org.entity.OsGroup;
import com.redxun.sys.org.entity.OsUser;
import com.redxun.sys.org.manager.OsGroupManager;
import com.redxun.sys.org.manager.OsUserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * RDM流程相关的中介者模式，充当跨模块之间的service相互创建流程的中介者
 */
@Service
public class CommonBpmMediator {
    private static Logger logger = LoggerFactory.getLogger(AttachedDocTranslateManager.class);
    //公用部分
    @Autowired
    private BpmSolutionManager bpmSolutionManager;
    @Autowired
    private BpmInstManager bpmInstManager;
    @Autowired
    private BpmTaskManager bpmTaskManager;
    @Autowired
    private OsGroupManager osGroupManager;
    @Autowired
    private OsUserManager osUserManager;
    @Autowired
    private SysDicManager sysDicManager;
    //业务增量
    @Autowired
    private PresaleDocumentsService presaleDocumentsService;
    @Autowired
    private PresaleDocumentsApplyService presaleDocumentsApplyService;
    @Autowired
    private PresaleDocumentsFileService presaleDocumentsFileService;
    @Autowired
    private AttachedDocTranslateDao attachedDocTranslateDao;

    //..入口方法
    public void doAutoStartWorkFlow(JSONObject sourceData,
                                    String sourceSolKey,
                                    String targetSolKey,
                                    String whatTypeToStart,
                                    IUser TargetProcessCreator) throws Exception {
        //1.外部传过来的表单正确性校验，成功则生成目标表单数据，利用反射工厂化
        JSONObject targetData = this.doValidAndTransToTargetData(sourceData, sourceSolKey, targetSolKey);
        //2.查找目标表单的BpmSolution
        BpmSolution bpmSolution = this.doGetTargetBpmSolution(targetSolKey);
        //3.启动流程
        String targetBusinessId = this.doStartTargetProcess(bpmSolution, targetData, whatTypeToStart, TargetProcessCreator);
        //4.同步文件信息至目标业务，利用反射工厂化
        this.doSynFileToTarget(targetData.getString("sourceFileMainId"), targetBusinessId, sourceSolKey, targetSolKey);
    }

    //1.外部传过来的表单正确性校验，成功则生成目标表单数据，利用反射工厂化
    private JSONObject doValidAndTransToTargetData(JSONObject sourceData,
                                                   String sourceSolKey,
                                                   String targetSolKey) throws Exception {
        Method method = this.getClass().getDeclaredMethod(sourceSolKey + "To" + targetSolKey,
                JSONObject.class, String.class);
        Object targetDataObj = method.invoke(this, sourceData, sourceSolKey);
        return (JSONObject) targetDataObj;
    }

    /**
     * 维护一个表单转换方法的签名系列，一定要以 “源流程方案key”+To+“目标流程方案key”命名。
     * 按照策略模式，应该将其分解为独立的策略类，此处简化处理，避免过度设计
     *
     * @param sourceData   要转换的原始表单数据
     * @param sourceSolKey 要转换的原始表单的流程方案的key
     * @return 转换后的目标表单数据
     * @throws Exception
     */
    //售前文件申请单据转换为翻译申请的单据
    private JSONObject PresaleDocumentApplyToattachedDocTranslate(JSONObject sourceData, String sourceSolKey) throws Exception {
        JSONObject targetData = new JSONObject();
        JSONObject presaleDocument = presaleDocumentsService.getDataByApplyId(sourceData.getString("id"));
        targetData.put("sourceBusinessId", sourceData.getString("id"));
        targetData.put("sourceInstId", sourceData.getString("INST_ID_"));
        targetData.put("sourceBpmSolKey", sourceSolKey);
        targetData.put("sourceRefId", presaleDocument.getString("REF_ID_"));
        targetData.put("manualType", sourceData.getString("businessType"));
        JSONArray productSpectrum = sourceData.getJSONArray("productSpectrum");
        StringBuilder stringBuilder1 = new StringBuilder();
        StringBuilder stringBuilder2 = new StringBuilder();
        StringBuilder stringBuilder3 = new StringBuilder();
        for (Object object : productSpectrum) {
            JSONObject product = (JSONObject) object;
            stringBuilder1.append(product.getString("saleModel_item")).append(",");
            stringBuilder2.append(product.getString("designModel_item")).append(",");
            stringBuilder3.append(product.getString("materialCode_item")).append(",");
        }
        targetData.put("saleType", stringBuilder1.length() > 0 ?
                stringBuilder1.substring(0, stringBuilder1.length() - 1) : stringBuilder1.toString());
        targetData.put("designType", stringBuilder2.length() > 0 ?
                stringBuilder2.substring(0, stringBuilder2.length() - 1) : stringBuilder2.toString());
        targetData.put("materialCode", stringBuilder3.length() > 0 ?
                stringBuilder3.substring(0, stringBuilder3.length() - 1) : stringBuilder3.toString());
        targetData.put("manualCode", "无");
        targetData.put("mannulCE", "");
        targetData.put("manualVersion", presaleDocument.getString("docVersion"));
        Date currentDate = new Date();
        targetData.put("applyTime", DateUtil.formatDate(currentDate, DateUtil.DATE_FORMAT_YMD));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DAY_OF_MONTH, 15);
        targetData.put("needTime", DateUtil.formatDate(calendar.getTime(), DateUtil.DATE_FORMAT_YMD));
        targetData.put("sourceManualLan", sourceData.getString("docLanguage"));
        targetData.put("targetManualLan", "英语");
        OsGroup presaleDocumentsTranslator = osGroupManager.getByKeyTenantId("PresaleDocumentsTranslator", "1");
        if (presaleDocumentsTranslator == null) {
            throw new RuntimeException("没有售前文件翻译人员这个角色！");
        }
        List<OsUser> translators = osUserManager.getByGroupId(presaleDocumentsTranslator.getGroupId());
        if (translators.isEmpty()) {
            throw new RuntimeException("售前文件翻译人员这个角色没有相应人员！");
        }
        stringBuilder1.setLength(0);
        stringBuilder2.setLength(0);
        for (OsUser osUser : translators) {
            stringBuilder1.append(osUser.getUserId()).append(",");
            stringBuilder2.append(osUser.getFullname()).append(",");
        }
        targetData.put("translatorId", stringBuilder1.length() > 0 ?
                stringBuilder1.substring(0, stringBuilder1.length() - 1) : stringBuilder1.toString());
        targetData.put("translator", stringBuilder2.length() > 0 ?
                stringBuilder2.substring(0, stringBuilder2.length() - 1) : stringBuilder2.toString());
        targetData.put("remark", sourceData.getString("applicabilityStatement") + ";" + sourceData.getString("remarks"));
        targetData.put("sourceFileMainId", presaleDocument.getString("id"));//辅助用，非业务实体本身数据
        return targetData;
    }

    //2.查找目标表单的BpmSolution
    private BpmSolution doGetTargetBpmSolution(String targetSolKey) throws Exception {
        BpmSolution bpmSolution = bpmSolutionManager.getByKey(targetSolKey, "1");
        if (bpmSolution == null) {
            throw new RuntimeException("没有找到目标表单的流程方案！");
        }
        return bpmSolution;
    }

    //3.启动流程：此处为了实现中介者模式的流程嵌套做了一些调整
    private String doStartTargetProcess(BpmSolution targetSolution, JSONObject targetData,
                                        String whatTypeToStart, IUser TargetProcessCreator) throws Exception {
        IExecutionCmd oriCmd = ProcessHandleHelper.getProcessCmd();//由于是中介者，需要先保留原先驱动者的Cmd快照
        IUser currentUser = ContextUtil.getCurrentUser();//由于是中介者，需要先保留原先驱动者的当前用户快照
        ProcessStartCmd startCmd = new ProcessStartCmd();//new一个新的Cmd，这个则为中介者启动其他流程的Cmd快照
        try {
            if (TargetProcessCreator != null) {//如果指定了目标发起人，则将当前用户先设置为目标发起人
                TargetProcessCreator.setTenant(ContextUtil.getTenant());
                ContextUtil.setCurUser(TargetProcessCreator);
            }
            startCmd.setSolId(targetSolution.getSolId());
            startCmd.setJsonData(targetData.toJSONString());
            //启动流程
            if (whatTypeToStart.equalsIgnoreCase(RdmApiController.SAVE_DRAFT)) {
                bpmInstManager.doSaveDraft(startCmd);
            } else if (whatTypeToStart.equalsIgnoreCase(RdmApiController.START_PROCESS)) {
                bpmInstManager.doStartProcess(startCmd);
            } else {
                throw new RuntimeException("没有收到具体的启动指令");
            }
            return startCmd.getBusinessKey();
        } catch (Exception ex) {
            //把具体的错误放置在内部处理，以显示正确的错误信息提示，在此不作任何的错误处理
            logger.error(ExceptionUtil.getExceptionMessage(ex));
            if (ProcessHandleHelper.getProcessMessage().getErrorMsges().size() == 0) {
                //如果原先的流程没有错误，就将启动新流程的错误放入流程的Err快照
                ProcessHandleHelper.getProcessMessage().addErrorMsg(ex.getMessage());
            }
            return "";
        } finally {
            ProcessHandleHelper.setProcessCmd(oriCmd);//最终要还原驱动者的Cmd快照
            ContextUtil.setCurUser(currentUser);//最终要还原驱动者的当前用户
            System.out.println("");
        }
    }

    //4.同步文件信息至目标业务，利用反射工厂化
    private void doSynFileToTarget(String sourceFileMainId,
                                   String targetFileMainId,
                                   String sourceSolKey,
                                   String targetSolKey) throws Exception {
        Method method = this.getClass().getDeclaredMethod(sourceSolKey + "FileTo" + targetSolKey + "File",
                String.class, String.class);
        method.invoke(this, sourceFileMainId, targetFileMainId);
    }

    /**
     * 维护一个表单转换方法的签名系列，一定要以 “源流程方案key”+FileTo+“目标流程方案key”+“File”命名。
     * 按照策略模式，应该将其分解为独立的策略类，此处简化处理，避免过度设计
     *
     * @param sourceFileMainId 原始文件的mainId
     * @param targetFileMainId 目标文件的mainId
     * @throws Exception
     */
    //售前文件申请单附件传递到翻译申请的附件
    private void PresaleDocumentApplyFileToattachedDocTranslateFile(String sourceFileMainId, String targetFileMainId) throws Exception {
        //源文件信息
        List<JSONObject> sourceFileInfos = new ArrayList<>();
        List<String> sourceBusinessIds = new ArrayList<>();
        sourceBusinessIds.add(sourceFileMainId);
        JSONObject params = new JSONObject();
        params.put("businessIds", sourceBusinessIds);
        sourceFileInfos = presaleDocumentsFileService.getFileListInfos(params);
        //源文件基础目录
        String filePathBaseSource = sysDicManager.getBySysTreeKeyAndDicKey(
                "PresaleDocumentUploadPosition", "主数据文件").getValue();
        //目标文件基础目录
        String filePathBaseTarget = sysDicManager.getBySysTreeKeyAndDicKey(
                "serviceEngineeringUploadPosition", "attachedDocTranslate").getValue();
        for (JSONObject sourceFileInfo : sourceFileInfos) {
            //写入目标文件数据库
            JSONObject targetFileInfo = new JSONObject();
            targetFileInfo.put("id", IdUtil.getId());
            targetFileInfo.put("fileName", sourceFileInfo.getString("fileName"));
            targetFileInfo.put("fileSize", sourceFileInfo.getString("fileSize"));
            targetFileInfo.put("mainId", targetFileMainId);
            targetFileInfo.put("CREATE_BY_", sourceFileInfo.getString("CREATE_BY_"));
            targetFileInfo.put("CREATE_TIME_", new Date());
            attachedDocTranslateDao.addFileInfos(targetFileInfo);
            //扩展名，共用
            String suffix = CommonFuns.toGetFileSuffix(sourceFileInfo.getString("fileName"));
            //源文件全路径
            String fileFullPathSource = filePathBaseSource + File.separator + sourceFileMainId
                    + File.separator + sourceFileInfo.getString("id") + "." + suffix;
            //目标文件二级目录
            String filePathBaseTarget2 = filePathBaseTarget + File.separator + targetFileMainId;
            File filePathBaseTarget2Dir = new File(filePathBaseTarget2);
            if (!filePathBaseTarget2Dir.exists()) {
                filePathBaseTarget2Dir.mkdirs();
            }
            String fileFullPathTarget = filePathBaseTarget2 + File.separator + targetFileInfo.getString("id") + "." + suffix;
            FileUtil.copyFile(fileFullPathSource, fileFullPathTarget);
        }
    }

    //翻译申请的附件传递到售前文件全系的附件 attachedDocTranslate PresaleDocument
    private void attachedDocTranslateFileToPresaleDocumentFile(String sourceFileMainId, String targetFileMainId) throws Exception {
        //源文件信息
        List<JSONObject> sourceFileInfos = new ArrayList<>();
        List<String> sourceBusinessIds = new ArrayList<>();
        sourceBusinessIds.add(sourceFileMainId);
        JSONObject params = new JSONObject();
        params.put("businessIds", sourceBusinessIds);
        sourceFileInfos = attachedDocTranslateDao.queryFileList(params);
        String businessType = "";
        if (!sourceFileInfos.isEmpty()) {
            String mainId = sourceFileInfos.get(0).getString("mainId");
            JSONObject attachedDocTranslate = attachedDocTranslateDao.queryDetailById(mainId);
            if (attachedDocTranslate == null) {
                throw new RuntimeException("在传输文件过程中未获取到正确的文件类型！");
            } else {
                businessType = attachedDocTranslate.getString("manualType");
            }
        }
        //源文件基础目录
        String filePathBaseSource = sysDicManager.getBySysTreeKeyAndDicKey(
                "serviceEngineeringUploadPosition", "attachedDocTranslate").getValue();
        //目标文件基础目录
        String filePathBaseTarget = sysDicManager.getBySysTreeKeyAndDicKey(
                "PresaleDocumentUploadPosition", "主数据文件").getValue();
        for (JSONObject sourceFileInfo : sourceFileInfos) {
            //写入目标文件数据库
            JSONObject targetFileInfo = new JSONObject();
            targetFileInfo.put("id", IdUtil.getId());
            targetFileInfo.put("businessId", targetFileMainId);
            targetFileInfo.put("businessType", businessType);
            targetFileInfo.put("fileName", sourceFileInfo.getString("fileName"));
            targetFileInfo.put("fileSize", sourceFileInfo.getString("fileSize"));
            targetFileInfo.put("fileDesc", sourceFileInfo.getString("fileDesc"));
            targetFileInfo.put("CREATE_BY_", sourceFileInfo.getString("CREATE_BY_"));
            targetFileInfo.put("CREATE_TIME_", new Date());
            presaleDocumentsFileService.insertFileInfos(targetFileInfo);
            //扩展名，共用
            String suffix = CommonFuns.toGetFileSuffix(sourceFileInfo.getString("fileName"));
            //源文件全路径
            String fileFullPathSource = filePathBaseSource + File.separator + sourceFileMainId
                    + File.separator + sourceFileInfo.getString("id") + "." + suffix;
            //目标文件二级目录
            String filePathBaseTarget2 = filePathBaseTarget + File.separator + targetFileMainId;
            File filePathBaseTarget2Dir = new File(filePathBaseTarget2);
            if (!filePathBaseTarget2Dir.exists()) {
                filePathBaseTarget2Dir.mkdirs();
            }
            String fileFullPathTarget = filePathBaseTarget2 + File.separator + targetFileInfo.getString("id") + "." + suffix;
            FileUtil.copyFile(fileFullPathSource, fileFullPathTarget);
        }
    }

    //5.最终的后处理,利用反射工厂化
    public void doTheFinalCallBack(JSONObject sourceData,
                                   String sourceSolKey,
                                   String targetSolKey) throws Exception {
        Method method = this.getClass().getDeclaredMethod(sourceSolKey + "To" + targetSolKey + "FinalCallBack",
                JSONObject.class);
        Object targetDataObj = method.invoke(this, sourceData);
    }

    /**
     * 维护一个最终的后处理方法的签名系列，一定要以 “源流程方案key”+To+“目标流程方案key”+“FinalCallBack”命名。
     * 按照策略模式，应该将其分解为独立的策略类，此处简化处理，避免过度设计
     *
     * @param sourceData 要进行后处理回调的流程form的数据
     * @throws Exception
     */
    private void attachedDocTranslateToPresaleDocumentApplyFinalCallBack(JSONObject sourceData) throws Exception {
        /**
         * 此处引用售前文件本身的发布功能，因此需要MOCK一个PresaleDocumentApply。此处的sourceData为attachedDocTranslate，
         * 里面有原始驱动者PresaleDocumentApply的id，只要用这个id将原始PresaleDocumentApply找到，替换部分翻译驱动的特征信息即可。
         */
        String presaleDocumentApplyId = sourceData.getString("sourceBusinessId");
        JSONObject presaleDocumentsApplyMock = presaleDocumentsApplyService.getDataById(presaleDocumentApplyId);
        presaleDocumentsApplyMock.put("docLanguage", sourceData.getString("targetManualLan"));//语言传递
        presaleDocumentsApplyMock.put("docVersion", sourceData.getString("manualVersion"));//版本传递
        presaleDocumentsApplyMock.put("REF_ID_", sourceData.getString("sourceRefId"));//族码传递
        JSONObject presaleDocument = presaleDocumentsService.doTransAndReleaseBusinessWithOutSynFile(presaleDocumentsApplyMock);
        this.doSynFileToTarget(sourceData.getString("id"), presaleDocument.getString("id"),
                "attachedDocTranslate", "PresaleDocument");
    }
}
