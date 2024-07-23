package com.redxun.presaleDocuments.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.service.ActInstService;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.AbstractExecutionCmd;
import com.redxun.bpm.core.entity.TaskExecutor;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.bpm.core.manager.BpmTaskManager;
import com.redxun.bpm.enums.TaskOptionType;
import com.redxun.core.script.GroovyScript;
import com.redxun.core.util.DateUtil;
import com.redxun.presaleDocuments.core.util.PresaleDocumentsConst;
import com.redxun.rdmCommon.core.controller.RdmApiController;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.rdmCommon.core.manager.CommonBpmMediator;
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.sys.core.entity.SysDic;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.sys.org.entity.OsGroup;
import com.redxun.sys.org.entity.OsUser;
import com.redxun.sys.org.manager.OsGroupManager;
import com.redxun.sys.org.manager.OsUserManager;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PresaleDocumentsApplyScript implements GroovyScript {
    private Logger logger = LoggerFactory.getLogger(PresaleDocumentsApplyScript.class);
    @Autowired
    private PresaleDocumentsApplyService presaleDocumentsApplyService;
    @Autowired
    private PresaleDocumentsService presaleDocumentsService;
    @Autowired
    private BpmInstManager bpmInstManager;
    @Autowired
    private BpmTaskManager bpmTaskManager;
    @Autowired
    private ActInstService actInstService;
    @Autowired
    private SendDDNoticeManager sendDDNoticeManager;
    @Autowired
    private SysDicManager sysDicManager;
    @Autowired
    private OsUserManager osUserManager;
    @Autowired
    private OsGroupManager osGroupManager;
    @Autowired
    private CommonInfoDao commonInfoDao;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private CommonBpmMediator commonBpmMediator;

    //..获取人员辅助
    private List<TaskExecutor> getTargetUsers(String dicKeyTop1, String dicKeyTop2) {
        List<TaskExecutor> users = new ArrayList<>();
        List<String> userNos = new ArrayList<>();
        SysDic sysDic = sysDicManager.getBySysTreeKeyAndDicKey("PresaleDocumentApPerson", dicKeyTop1);
        List<SysDic> sysDic2 = sysDicManager.getByParentId(sysDic.getDicId());
        for (SysDic dic : sysDic2) {
            if (dic.getKey().equalsIgnoreCase(dicKeyTop2)) {
                userNos = Arrays.asList(dic.getValue().split(","));
            }
        }
        for (String userNo : userNos) {
            OsUser osUser = osUserManager.getByUserName(userNo);
            users.add(new TaskExecutor(osUser.getUserId(), osUser.getFullname()));
        }
        return users;
    }

    //..获取产品导购手册校对人
    public Collection<TaskExecutor> getB1() {
        String salesArea = JSONObject.parseObject(ProcessHandleHelper.getProcessCmd()
                .getJsonData()).getString("salesArea");
        if (salesArea.equalsIgnoreCase(PresaleDocumentsConst
                .serviceEngineeringDecorationManualSalesArea.BUSINESS_SALESAREA_NEIXIAO)) {
            return this.getTargetUsers(PresaleDocumentsConst.PresaleDocumentType.BUSINESS_TYPE_CHANPINDAOGOUSHOUCE, "B1-NX");
        } else {
            return this.getTargetUsers(PresaleDocumentsConst.PresaleDocumentType.BUSINESS_TYPE_CHANPINDAOGOUSHOUCE, "B1");
        }
    }

    //..获取导购手册产品主管会签人
    public Collection<TaskExecutor> getB2() {
        List<TaskExecutor> users = new ArrayList<>();
        JSONObject formDataJson = JSONObject.parseObject(ProcessHandleHelper.getProcessCmd().getJsonData());
        JSONArray productSpectrumItems = formDataJson.getJSONArray("productSpectrum");
        Map<String, String> userId2UserName = new HashedMap();
        for (Object productSpectrum : productSpectrumItems) {
            //去重
            JSONObject productSpectrumJson = (JSONObject) productSpectrum;
            userId2UserName.put(productSpectrumJson.getString("productManagerId_item"),
                    productSpectrumJson.getString("productManagerName_item"));
        }
        userId2UserName.forEach((key, value) -> {
            users.add(new TaskExecutor(key, value));
        });
        return users;
    }

    //..获取再次编制人
    public Collection<TaskExecutor> getC1() {
        List<TaskExecutor> users = new ArrayList<>();
        JSONObject formDataJson = JSONObject.parseObject(ProcessHandleHelper.getProcessCmd().getJsonData());
        users.add(new TaskExecutor(formDataJson.getString("repUserId"),
                osUserManager.get(formDataJson.getString("repUserId")).getFullname()));
        return users;
    }

    //..获取服务工程校对人
    public Collection<TaskExecutor> getC2() {
        JSONObject formDataJson = JSONObject.parseObject(ProcessHandleHelper.getProcessCmd().getJsonData());
        String businessType = formDataJson.getString("businessType");
        return this.getTargetUsers(businessType, "C2");
    }

    //..获取业务部门校对人
    public Collection<TaskExecutor> getC3() {
        JSONObject formDataJson = JSONObject.parseObject(ProcessHandleHelper.getProcessCmd().getJsonData());
        String businessType = formDataJson.getString("businessType");
        String salesArea = formDataJson.getString("salesArea");
        if (salesArea.equalsIgnoreCase(PresaleDocumentsConst
                .serviceEngineeringDecorationManualSalesArea.BUSINESS_SALESAREA_NEIXIAO)) {
            return this.getTargetUsers(businessType, "C3-NX");
        } else {
            return this.getTargetUsers(businessType, "C3");
        }
    }

    //..获取成本清单再次编制人
    public Collection<TaskExecutor> getD1() {
        List<TaskExecutor> users = new ArrayList<>();
        JSONObject formDataJson = JSONObject.parseObject(ProcessHandleHelper.getProcessCmd().getJsonData());
        users.add(new TaskExecutor(formDataJson.getString("repUserId"),
                osUserManager.get(formDataJson.getString("repUserId")).getFullname()));
        return users;
    }

    //..获取成本清单部件负责人
    public Collection<TaskExecutor> getD2() {
        List<TaskExecutor> users = new ArrayList<>();
        JSONObject formDataJson = JSONObject.parseObject(ProcessHandleHelper.getProcessCmd().getJsonData());
        JSONArray buJianFuZeRenItems = formDataJson.getJSONArray("buJianFuZeRen");
        Map<String, String> userId2UserName = new HashedMap();
        for (Object buJianFuZeRen : buJianFuZeRenItems) {
            //去重
            JSONObject buJianFuZeRenItemsJson = (JSONObject) buJianFuZeRen;
            userId2UserName.put(buJianFuZeRenItemsJson.getString("fuZeRenId_item"),
                    buJianFuZeRenItemsJson.getString("fuZeRenName_item"));
        }
        userId2UserName.forEach((key, value) -> {
            users.add(new TaskExecutor(key, value));
        });
        return users;
    }

    //..获取成本清单相关部门上传文件人
    public Collection<TaskExecutor> getD4() {
        JSONObject formDataJson = JSONObject.parseObject(ProcessHandleHelper.getProcessCmd().getJsonData());
        String businessType = formDataJson.getString("businessType");
        return this.getTargetUsers(businessType, "D4");
    }

    //..获取校对会签人
    public Collection<TaskExecutor> getD8() {
        JSONObject formDataJson = JSONObject.parseObject(ProcessHandleHelper.getProcessCmd().getJsonData());
        String businessType = formDataJson.getString("businessType");
        return this.getTargetUsers(businessType, "D8");
    }

    //..获取审核会签人
    public Collection<TaskExecutor> getD9() {
        List<TaskExecutor> users = new ArrayList<>();
        JSONObject formDataJson = JSONObject.parseObject(ProcessHandleHelper.getProcessCmd().getJsonData());
        //找服务所的负责人
        List<Map<String, String>> depRespMans = rdmZhglUtil.queryFwgcs();
        if (depRespMans != null && !depRespMans.isEmpty()) {
            for (Map<String, String> depRespMan : depRespMans) {
                users.add(new TaskExecutor(depRespMan.get("PARTY2_"), depRespMan.get("FULLNAME_")));
            }
        }
        //查询repUserId的所长
        List<JSONObject> fenGuanSuoZhangs = commonInfoDao.queryDeptRespByUserId(formDataJson.getString("repUserId"));
        for (JSONObject fenguansuozhang : fenGuanSuoZhangs) {
            users.add(new TaskExecutor(fenguansuozhang.getString("USER_ID_"), fenguansuozhang.getString("FULLNAME_")));
        }
        //查询repUserId的主任
        OsGroup mainDep = osGroupManager.getMainDeps(formDataJson.getString("repUserId"), "1");
        JSONObject params = new JSONObject();
        params.put("REL_TYPE_KEY_", "GROUP-USER-DIRECTOR");
        params.put("deptId", mainDep.getGroupId());
        List<Map<String, String>> fenGuanzhuRens = commonInfoDao.queryUserByGroupId(params);
        for (Map<String, String> fenGuanzhuRen : fenGuanzhuRens) {
            users.add(new TaskExecutor(fenGuanzhuRen.get("USER_ID_").toString(), fenGuanzhuRen.get("FULLNAME_").toString()));
        }
        return users;
    }

    //..获取技术规格书结构化文档上传人员
    public Collection<TaskExecutor> getU() {
        return this.getTargetUsers(PresaleDocumentsConst.PresaleDocumentType.BUSINESS_TYPE_JISHUGUIGESHU, "U");
    }

    //..按照业务类型获取审核2人员
    public Collection<TaskExecutor> getW() {
        List<TaskExecutor> users = new ArrayList<>();
        JSONObject formDataJson = JSONObject.parseObject(ProcessHandleHelper.getProcessCmd().getJsonData());
        String businessType = formDataJson.getString("businessType");
        Map<String, String> userId2UserName = new HashedMap();
        if (businessType.equalsIgnoreCase(PresaleDocumentsConst.PresaleDocumentType.BUSINESS_TYPE_CHANPINDAOGOUSHOUCE)) {
            //产品导购手册类型的需要根据B2会签人员获取他们的所长
            Collection<TaskExecutor> executors = this.getB2();
            for (TaskExecutor executor : executors) {
                List<JSONObject> jsonObjects = commonInfoDao.queryDeptRespByUserId(executor.getId());
                for (JSONObject jsonObject : jsonObjects) {
                    //去重
                    userId2UserName.put(jsonObject.getString("USER_ID_"), jsonObject.getString("FULLNAME_"));
                }
            }
            userId2UserName.forEach((key, value) -> {
                users.add(new TaskExecutor(key, value));
            });
        } else {
            //其余类型的根据责任人获取责任人的所长
            String repUserId = formDataJson.getString("repUserId");
            List<JSONObject> jsonObjects = commonInfoDao.queryDeptRespByUserId(repUserId);
            for (JSONObject jsonObject : jsonObjects) {
                users.add(new TaskExecutor(jsonObject.getString("USER_ID_"), jsonObject.getString("FULLNAME_")));
            }
        }
        return users;
    }

    //..获取审核3人员
    public Collection<TaskExecutor> getX() {
        JSONObject formDataJson = JSONObject.parseObject(ProcessHandleHelper.getProcessCmd().getJsonData());
        String businessType = formDataJson.getString("businessType");
        return this.getTargetUsers(businessType, "X");
    }

    //..是产品导购手册
    public boolean isDaoGouShouCe(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        if (StringUtils.isBlank(formData)) {
            logger.warn("formData is blank");
            return false;
        }
        JSONObject formDataJson = JSONObject.parseObject(formData);
        if (formDataJson.isEmpty()) {
            logger.warn("formData is blank");
            return false;
        }
        if (formDataJson.getString("businessType").
                equalsIgnoreCase(PresaleDocumentsConst.PresaleDocumentType.BUSINESS_TYPE_CHANPINDAOGOUSHOUCE)) {
            return true;
        } else {
            return false;
        }
    }

    //..是其余手册
    public boolean isQiYuShouCe(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        if (StringUtils.isBlank(formData)) {
            logger.warn("formData is blank");
            return false;
        }
        JSONObject formDataJson = JSONObject.parseObject(formData);
        if (formDataJson.isEmpty()) {
            logger.warn("formData is blank");
            return false;
        }
        if (!formDataJson.getString("businessType").
                equalsIgnoreCase(PresaleDocumentsConst.PresaleDocumentType.BUSINESS_TYPE_CHANPINDAOGOUSHOUCE)
                &&
                !formDataJson.getString("businessType").
                        equalsIgnoreCase(PresaleDocumentsConst.PresaleDocumentType.BUSINESS_TYPE_CHANPINQUANSHENGMINGZHOUQICHENGBENQINGDAN)
                &&
                !formDataJson.getString("businessType").
                        equalsIgnoreCase(PresaleDocumentsConst.PresaleDocumentType.BUSINESS_TYPE_JISHUWENJIANFUJIAN)) {
            return true;
        } else {
            return false;
        }
    }

    //..是产品全生命周期成本清单
    public boolean isChengBenQingDan(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        if (StringUtils.isBlank(formData)) {
            logger.warn("formData is blank");
            return false;
        }
        JSONObject formDataJson = JSONObject.parseObject(formData);
        if (formDataJson.isEmpty()) {
            logger.warn("formData is blank");
            return false;
        }
        if (formDataJson.getString("businessType").
                equalsIgnoreCase(PresaleDocumentsConst.PresaleDocumentType.BUSINESS_TYPE_CHANPINQUANSHENGMINGZHOUQICHENGBENQINGDAN)) {
            return true;
        } else {
            return false;
        }
    }

    //..是技术资料附件
    public boolean isJiShuZiLiaoFuJian(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        if (StringUtils.isBlank(formData)) {
            logger.warn("formData is blank");
            return false;
        }
        JSONObject formDataJson = JSONObject.parseObject(formData);
        if (formDataJson.isEmpty()) {
            logger.warn("formData is blank");
            return false;
        }
        if (formDataJson.getString("businessType").
                equalsIgnoreCase(PresaleDocumentsConst.PresaleDocumentType.BUSINESS_TYPE_JISHUWENJIANFUJIAN)) {
            return true;
        } else {
            return false;
        }
    }

    //..是技术规格书
    public boolean isJiShuGuiGeShu(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        if (StringUtils.isBlank(formData)) {
            logger.warn("formData is blank");
            return false;
        }
        JSONObject formDataJson = JSONObject.parseObject(formData);
        if (formDataJson.isEmpty()) {
            logger.warn("formData is blank");
            return false;
        }
        if (formDataJson.getString("businessType").
                equalsIgnoreCase(PresaleDocumentsConst.PresaleDocumentType.BUSINESS_TYPE_JISHUGUIGESHU)) {
            return true;
        } else {
            return false;
        }
    }

    //..不是技术规格书
    public boolean isNotJiShuGuiGeShu(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        if (StringUtils.isBlank(formData)) {
            logger.warn("formData is blank");
            return false;
        }
        JSONObject formDataJson = JSONObject.parseObject(formData);
        if (formDataJson.isEmpty()) {
            logger.warn("formData is blank");
            return false;
        }
        if (formDataJson.getString("businessType").
                equalsIgnoreCase(PresaleDocumentsConst.PresaleDocumentType.BUSINESS_TYPE_JISHUGUIGESHU)) {
            return false;
        } else {
            return true;
        }
    }

    //..申请人是服务所的
    public boolean isService(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        if (StringUtils.isBlank(formData)) {
            logger.warn("formData is blank");
            return false;
        }
        JSONObject formDataJson = JSONObject.parseObject(formData);
        if (formDataJson.isEmpty()) {
            logger.warn("formData is blank");
            return false;
        }
        OsUser applyUser = osUserManager.get(formDataJson.getString("applyUserId"));
        if (applyUser != null) {
            OsGroup mainDep = osGroupManager.getMainDeps(applyUser.getUserId(), "1");
            if (mainDep.getName().equalsIgnoreCase(RdmConst.FWGCS_NAME)
                    || ContextUtil.getCurrentUser().getUserNo().equalsIgnoreCase("admin")) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    //..申请人不是服务所的
    public boolean isNotService(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        if (StringUtils.isBlank(formData)) {
            logger.warn("formData is blank");
            return false;
        }
        JSONObject formDataJson = JSONObject.parseObject(formData);
        if (formDataJson.isEmpty()) {
            logger.warn("formData is blank");
            return false;
        }
        OsUser applyUser = osUserManager.get(formDataJson.getString("applyUserId"));
        if (applyUser != null) {
            OsGroup mainDep = osGroupManager.getMainDeps(applyUser.getUserId(), "1");
            if (mainDep.getName().equalsIgnoreCase(RdmConst.FWGCS_NAME)
                    || ContextUtil.getCurrentUser().getUserNo().equalsIgnoreCase("admin")) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    //..
    public void taskCreateScript(Map<String, Object> vars) throws Exception {
        try {
            //@lwgkiller:阿诺多罗 下面这个cmd可以取代getDetail，而且传来的是表单的最新数据
            JSONObject jsonObject = ProcessHandleHelper.getProcessCmd().getJsonDataObject();
            String activitiId = vars.get("activityId").toString();
            jsonObject.put("businessStatus", activitiId);
            presaleDocumentsApplyService.updateBusiness(jsonObject);
        } catch (Exception e) {
            ProcessHandleHelper.addErrorMsg(e.getMessage());
            throw e;
        }
    }

    //..
    public void taskEndScript(Map<String, Object> vars) throws Exception {
        try {
            //@lwgkiller:阿诺多罗 下面这个cmd可以取代getDetail，而且传来的是表单的最新数据
            JSONObject jsonObject = ProcessHandleHelper.getProcessCmd().getJsonDataObject();
            String activitiId = vars.get("activityId").toString();
            if (activitiId.equalsIgnoreCase("A")) {//第一个编辑节点通过后
                OsUser applyUser = osUserManager.get(jsonObject.getString("applyUserId"));
                OsGroup mainDep = osGroupManager.getMainDeps(applyUser.getUserId(), "1");
                if (!jsonObject.getString("businessType")
                        .equalsIgnoreCase(PresaleDocumentsConst.PresaleDocumentType.BUSINESS_TYPE_CHANPINDAOGOUSHOUCE) &&
                        (mainDep.getName().equalsIgnoreCase(RdmConst.FWGCS_NAME)
                                || ContextUtil.getCurrentUser().getUserNo().equalsIgnoreCase("admin"))) {
                    //如果 图册类型不是"产品导购手册" 且 是服务所发起的，则将编辑者改变为负责人
                    jsonObject.put("applyUserId", jsonObject.getString("repUserId"));
                } else if (!jsonObject.getString("businessType")
                        .equalsIgnoreCase(PresaleDocumentsConst.PresaleDocumentType.BUSINESS_TYPE_CHANPINDAOGOUSHOUCE) &&
                        !mainDep.getName().equalsIgnoreCase(RdmConst.FWGCS_NAME) &&
                        !ContextUtil.getCurrentUser().getUserNo().equalsIgnoreCase("admin")) {
                    //如果 图册类型不是"产品导购手册" 且 不是服务所发起的，则将负责人改变为编辑者，更新提交时间
                    jsonObject.put("repUserId", jsonObject.getString("applyUserId"));
                    jsonObject.put("applyTime", DateUtil.formatDate(new Date(), DateUtil.DATE_FORMAT_YMD));
                } else {
                    //"产品导购手册" 更新提交时间
                    jsonObject.put("applyTime", DateUtil.formatDate(new Date(), DateUtil.DATE_FORMAT_YMD));
                }
            } else if (activitiId.equalsIgnoreCase("C1") ||
                    activitiId.equalsIgnoreCase("D1")) {//第二个编辑节点通过后
                if (ProcessHandleHelper.getProcessCmd().getJumpType().equalsIgnoreCase(TaskOptionType.AGREE.name())) {
                    //更新提交时间
                    jsonObject.put("applyTime", DateUtil.formatDate(new Date(), DateUtil.DATE_FORMAT_YMD));
                    //由于有转办，因此还要更新实际办理者为申请人和责任人
                    jsonObject.put("applyUserId", ContextUtil.getCurrentUserId());
                    jsonObject.put("repUserId", ContextUtil.getCurrentUserId());
                } else if (ProcessHandleHelper.getProcessCmd().getJumpType().equalsIgnoreCase(TaskOptionType.BACK_TO_STARTOR.name())) {
                    //驳回情况下清空提交时间和申请人
                    jsonObject.put("applyTime", "");
                    jsonObject.put("applyUserId", jsonObject.getString("CREATE_BY_"));
                }
            } else if (activitiId.equalsIgnoreCase("C2") || activitiId.equalsIgnoreCase("C3") ||
                    activitiId.equalsIgnoreCase("V") || activitiId.equalsIgnoreCase("W")) {//剩下的只要是有驳回发起人的场景
                if (ProcessHandleHelper.getProcessCmd().getJumpType().equalsIgnoreCase(TaskOptionType.BACK_TO_STARTOR.name())) {
                    //驳回情况下清空提交时间和申请人
                    jsonObject.put("applyTime", "");
                    jsonObject.put("applyUserId", jsonObject.getString("CREATE_BY_"));
                }

            } else if (activitiId.equalsIgnoreCase("Y") || activitiId.equalsIgnoreCase("E1")) {
                //更新最终状态
                jsonObject.put("businessStatus", "Z");
                jsonObject.put("endTime", DateUtil.formatDate(new Date(), DateUtil.DATE_FORMAT_YMD));
                //创建对应的主数据并发布
                presaleDocumentsService.doTransAndReleaseBusiness(jsonObject);
                //如果不是下列四类手册，则启动生成翻译流程
                String salesArea = jsonObject.getString("salesArea");
                String businessType = jsonObject.getString("businessType");
                if (!salesArea.equalsIgnoreCase(PresaleDocumentsConst.serviceEngineeringDecorationManualSalesArea.BUSINESS_SALESAREA_NEIXIAO)
                        && !businessType.equalsIgnoreCase(PresaleDocumentsConst.PresaleDocumentType.BUSINESS_TYPE_CHANPINBIAOXUANPEIBIAO)
                        && !businessType.equalsIgnoreCase(PresaleDocumentsConst.PresaleDocumentType.BUSINESS_TYPE_DUOGONGNENGJIJUXITONGYALILIULIANGFANWEIBIAOZHUNZHIBIAO)
                        && !businessType.equalsIgnoreCase(PresaleDocumentsConst.PresaleDocumentType.BUSINESS_TYPE_CHANPINQUANSHENGMINGZHOUQICHENGBENQINGDAN)
                        && !businessType.equalsIgnoreCase(PresaleDocumentsConst.PresaleDocumentType.BUSINESS_TYPE_JISHUWENJIANFUJIAN)) {
                    OsUser osUser = osUserManager.get(jsonObject.getString("repUserId"));//取责任人
                    //todo:在这里通过中介者创建还有首节点跳过不了的问题
                    commonBpmMediator.doAutoStartWorkFlow(jsonObject, "PresaleDocumentApply",
                            "attachedDocTranslate", RdmApiController.START_PROCESS, osUser);//由责任人为发起人启动翻译流程
                }
            }
            presaleDocumentsApplyService.updateBusiness(jsonObject);
        } catch (Exception e) {
            ProcessHandleHelper.addErrorMsg(e.getMessage());
            throw e;
        }
    }
}
