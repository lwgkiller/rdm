package com.redxun.materielextend.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.query.Page;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.materielextend.core.dao.MaterielApplyDao;
import com.redxun.materielextend.core.dao.MaterielDao;
import com.redxun.materielextend.core.dao.MaterielTypeFDao;
import com.redxun.materielextend.core.service.wsdl.IN_DATA;
import com.redxun.materielextend.core.service.wsdl.XgwjjSapZWJXERDM_MODIFY_MARALocator;
import com.redxun.materielextend.core.service.wsdl.XgwjjSapZWJXERDM_MODIFY_MARAPortType;
import com.redxun.materielextend.core.util.CommonUtil;
import com.redxun.materielextend.core.util.MaterielConstant;
import com.redxun.materielextend.core.util.ResultData;
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.rmi.RemoteException;
import java.text.DecimalFormat;
import java.util.*;

/**
 * 应用模块名称
 * <p>
 * 代码描述
 * <p>
 * Copyright: Copyright (C) 2020 XXX, Inc. All rights reserved.
 * <p>
 * Company: 徐工挖掘机械有限公司
 * <p>
 *
 * @author liangchuanjiang
 * @since 2020/6/6 15:07
 */
@Service
public class MaterielApplyService {
    private static final Logger logger = LoggerFactory.getLogger(MaterielApplyService.class);
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private MaterielApplyDao materielApplyDao;
    @Autowired
    private CommonService commonService;
    @Autowired
    private MaterielApplyService materielApplyService;
    @Autowired
    private MaterielDao materielDao;
    @Autowired
    private MaterielService materielService;
    @Autowired
    private SendDDNoticeManager sendDDNoticeManager;
    @Autowired
    private MaterielTypeFDao materielTypeFDao;

    /**
     * 新增、更新或者提交申请
     *
     * @param postBody
     * @return
     */
    public ResultData applySaveOrCommit(String postBody) {
        ResultData resultData = new ResultData();
        resultData.setMessage("操作成功！");
        JSONObject postBodyObj = JSONObject.parseObject(postBody);
        if (postBodyObj == null || postBodyObj.isEmpty()) {
            resultData.setSuccess(false);
            resultData.setMessage("操作失败，发送内容为空！");
            return resultData;
        }
        String applyNo = postBodyObj.getString("applyNo");
        logger.info("applyNo“" + applyNo + "”进入applySaveOrCommit");
        // 操作者角色
        String opRoleName = postBodyObj.getString("opRoleName");
        if (StringUtils.isBlank(opRoleName)) {
            resultData.setSuccess(false);
            resultData.setMessage("操作失败，处理人角色为空！");
            return resultData;
        }
        try {
            // 对于申请人操作的场景，需要更新联系电话
            if (MaterielConstant.APPLY_OP_SQRKC.equalsIgnoreCase(opRoleName)) {
                String mobile = postBodyObj.getString("applyUserMobile");
                if (StringUtils.isBlank(mobile)) {
                    resultData.setSuccess(false);
                    resultData.setMessage("操作失败，申请人联系电话为空！");
                    return resultData;
                }
                // 更新申请人的联系电话
                Map<String, Object> params = new HashMap<>();
                params.put("userId", ContextUtil.getCurrentUserId());
                params.put("mobile", mobile);
                materielApplyDao.updateApplyerMobile(params);
            }
            // 暂存或者提交申请单
            String scene = postBodyObj.getString("scene");
            if ("save".equalsIgnoreCase(scene)) {
                saveApply(postBodyObj, resultData);
            } else if ("commit".equalsIgnoreCase(scene)) {
                commitApply(postBodyObj, resultData);
            }
            if (resultData.isSuccess()) {
                resultData.setMessage("操作成功!");
            }
        } catch (Exception e) {
            logger.error("Exception in applySaveOrCommit", e);
            resultData.setSuccess(false);
            resultData.setMessage("操作失败，系统异常!");
        }
        return resultData;
    }

    // 新增或者更新申请单（暂存）
    public void saveApply(JSONObject postBodyObj, ResultData resultData) {
        String applyNo = postBodyObj.getString("applyNo");
        // 新增申请单
        if (StringUtils.isBlank(applyNo)) {
            Map<String, String> param = new HashMap<>();
            List<JSONObject> roles = materielApplyDao.queryMaterielOpRoles(param);
            Map<String, String> roleKey2UserId = new HashMap<>();
            for (JSONObject oneRole : roles) {
                roleKey2UserId.put(oneRole.getString("KEY_"), oneRole.getString("userId"));
            }
            applyNo = CommonUtil.toGetApplyNo();
            Map<String, Object> params = new HashMap<>();
            params.put("applyNo", applyNo);
            params.put("sqUserDepId", ContextUtil.getCurrentUser().getMainGroupId());
            params.put("sqUserId", ContextUtil.getCurrentUserId());
            params.put("sqCommitTime", "");
            params.put("gyStatus", MaterielConstant.KCSTATUS_NO);
            params.put("gyCommitUserId", roleKey2UserId.get(MaterielConstant.APPLY_OP_GYKC));
            params.put("cgStatus", MaterielConstant.KCSTATUS_NO);
            params.put("cgCommitUserId", roleKey2UserId.get(MaterielConstant.APPLY_OP_CGKC));
            params.put("gfStatus", MaterielConstant.KCSTATUS_NO);
            params.put("gfCommitUserId", roleKey2UserId.get(MaterielConstant.APPLY_OP_GFKC));
            params.put("cwStatus", MaterielConstant.KCSTATUS_NO);
            params.put("cwCommitUserId", roleKey2UserId.get(MaterielConstant.APPLY_OP_CWKC));
            params.put("wlStatus", MaterielConstant.KCSTATUS_NO);
            params.put("wlCommitUserId", roleKey2UserId.get(MaterielConstant.APPLY_OP_WLKC));
            params.put("zzStatus", MaterielConstant.KCSTATUS_NO);
            params.put("zzCommitUserId", roleKey2UserId.get(MaterielConstant.APPLY_OP_ZZKC));
            params.put("applyStatus", MaterielConstant.APPLYSTATUS_DRAFT);
            params.put("urgent", postBodyObj.getString("urgent"));
            materielApplyDao.addMaterielApply(params);
        } else {
            // 更新申请单
            Map<String, Object> params = new HashMap<>();
            params.put("applyNoVal", applyNo);
            params.put("urgent", postBodyObj.getString("urgent"));
            materielApplyDao.updateMaterielApply(params);
        }
        resultData.setData(applyNo);
    }

    // 提交申请单
    private void commitApply(JSONObject postBodyObj, ResultData resultData) {
        String applyNo = postBodyObj.getString("applyNo");
        if (StringUtils.isBlank(applyNo)) {
            logger.error("commitApply failed, applyNo is blank");
            resultData.setSuccess(false);
            resultData.setMessage("提交失败，申请单不存在！");
            return;
        }
        resultData.setData(applyNo);
        // 根据操作人角色设置各节点的状态，更新申请表中不同的字段
        String opRoleName = postBodyObj.getString("opRoleName");
        Map<String, Object> updateParams = new HashMap<>();
        updateParams.put("applyNoVal", applyNo);
        switch (opRoleName) {
            case MaterielConstant.APPLY_OP_SQRKC:
                updateParams.put("sqCommitTime", DateFormatUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                updateParams.put("gyStatus", MaterielConstant.KCSTATUS_DOING);
                updateParams.put("applyStatus", MaterielConstant.APPLYSTATUS_RUNNING);
                updateParams.put("urgent", postBodyObj.getString("urgent"));
                break;
            case MaterielConstant.APPLY_OP_GYKC:
                updateParams.put("gyCommitTime", DateFormatUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                updateParams.put("gyCommitUserId", ContextUtil.getCurrentUserId());
                updateParams.put("gyStatus", MaterielConstant.KCSTATUS_DONE);
                updateParams.put("cgStatus", MaterielConstant.KCSTATUS_DOING);
                updateParams.put("gfStatus", MaterielConstant.KCSTATUS_DOING);
                updateParams.put("cwStatus", MaterielConstant.KCSTATUS_DOING);
                updateParams.put("wlStatus", MaterielConstant.KCSTATUS_DOING);
                updateParams.put("zzStatus", MaterielConstant.KCSTATUS_DOING);
                break;
            case MaterielConstant.APPLY_OP_CGKC:
                updateParams.put("cgCommitTime", DateFormatUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                updateParams.put("cgCommitUserId", ContextUtil.getCurrentUserId());
                updateParams.put("cgStatus", MaterielConstant.KCSTATUS_DONE);
                break;
            case MaterielConstant.APPLY_OP_GFKC:
                updateParams.put("gfCommitTime", DateFormatUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                updateParams.put("gfCommitUserId", ContextUtil.getCurrentUserId());
                updateParams.put("gfStatus", MaterielConstant.KCSTATUS_DONE);
                break;
            case MaterielConstant.APPLY_OP_CWKC:
                updateParams.put("cwCommitTime", DateFormatUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                updateParams.put("cwCommitUserId", ContextUtil.getCurrentUserId());
                updateParams.put("cwStatus", MaterielConstant.KCSTATUS_DONE);
                break;
            case MaterielConstant.APPLY_OP_WLKC:
                updateParams.put("wlCommitTime", DateFormatUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                updateParams.put("wlCommitUserId", ContextUtil.getCurrentUserId());
                updateParams.put("wlStatus", MaterielConstant.KCSTATUS_DONE);
                break;
            case MaterielConstant.APPLY_OP_ZZKC:
                updateParams.put("zzCommitTime", DateFormatUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                updateParams.put("zzCommitUserId", ContextUtil.getCurrentUserId());
                updateParams.put("zzStatus", MaterielConstant.KCSTATUS_DONE);
                break;
        }
        materielApplyDao.updateMaterielApply(updateParams);
        // 判断是否需要触发同步SAP（即所有的环节都提交完成），如果是则更新申请单的状态为needSync2Sap
        boolean needSyncSap = judgeAllProcessCommit(applyNo);
        if (needSyncSap) {
            long flag = System.currentTimeMillis();
            logger.info("applyNo“" + applyNo + "”开始调用物料扩充needSyncSap，" + flag);
            updateParams.clear();
            updateParams.put("applyNoVal", applyNo);
            updateParams.put("applyStatus", MaterielConstant.APPLYSTATUS_NEEDSYNC2SAP);
            materielApplyDao.updateMaterielApply(updateParams);
            // 调用SAP接口进行同步，同时更新物料和申请单的状态
            sync2SAP(resultData, applyNo);
            logger.info("applyNo“" + applyNo + "”结束调用物料扩充needSyncSap，" + flag);
        }
    }

    /**
     * 同步某个申请单中的扩充好的物料信息到SAP中,SAP能调通返回true,否则返回false。
     *
     * @param resultData
     * @param applyNo
     */
    public void sync2SAP(ResultData resultData, String applyNo) {
        long flag = System.currentTimeMillis();
        logger.info("applyNo“" + applyNo + "”开始调用物料扩充sync2SAP，" + flag);
        // 查询申请单下的物料
        Map<String, Object> param = new HashMap<>();
        List<String> applyNos = new ArrayList<>();
        applyNos.add(applyNo);
        param.put("applyNos", applyNos);
        List<JSONObject> materielList = materielService.queryMaterielsByApplyNo(param);
        List<JSONObject> noErrorMateriels = new ArrayList<>();
        for (int index = 0; index < materielList.size(); index++) {
            JSONObject oneMateriel = materielList.get(index);
            if (!MaterielConstant.MARK_ERROR_YES.equalsIgnoreCase(oneMateriel.getString("markError"))) {
                noErrorMateriels.add(oneMateriel);
            }
        }
        // 对于非问题物料进行进一步的检查
        List<JSONObject> materielSync2SAP = new ArrayList<>();
        List<JSONObject> update2ErrorMateriels = new ArrayList<>();
        if (!noErrorMateriels.isEmpty()) {
            // 先进行各个扩充节点重叠字段的必填性检查（如价格是采购类型为F且特殊采购类不为30时必填），存在不合格的则标注为问题物料，不再同步
            Map<String, Object> params = new HashMap<>();
            params.put("respRoleKey", MaterielConstant.APPLY_OP_COMMON);
            List<JSONObject> respProperties = materielService.queryMaterielProperties(params);
            if (respProperties != null && !respProperties.isEmpty()) {
                checkMaterielsBeforeSync2SAP(noErrorMateriels, respProperties, materielSync2SAP, update2ErrorMateriels);
            }
        }
        // 更新到表中问题物料
        if (!update2ErrorMateriels.isEmpty()) {
            for (JSONObject oneMateriel : update2ErrorMateriels) {
                materielDao.updateMateriel2Error(oneMateriel);
            }
        }
        // 对于非问题物料，调用SAP的接口。SAP调用成功则对所有物料和申请单进行状态更新,调用失败则返回（申请单状态为等待同步SAP）
        callSAP(materielSync2SAP, resultData, flag);
        logger.info("applyNo“" + applyNo + "”结束调用物料扩充sync2SAP，" + flag);
        if (!resultData.isSuccess()) {
            return;
        }
        // 对于所有的物料更新extendResult,问题物料为fail，成功的物料为success
        String applyFinalStatus = MaterielConstant.APPLYSTATUS_SUCCESSEND;
        for (int index = 0; index < materielList.size(); index++) {
            JSONObject oneMateriel = materielList.get(index);
            if (MaterielConstant.MARK_ERROR_YES.equalsIgnoreCase(oneMateriel.getString("markError"))) {
                applyFinalStatus = MaterielConstant.APPLYSTATUS_FAILEND;
                oneMateriel.put("extendResult", MaterielConstant.EXTENDRESULT_FAIL);
            } else {
                oneMateriel.put("extendResult", MaterielConstant.EXTENDRESULT_SUCCESS);
            }
            materielDao.updateMaterielExtendResult(oneMateriel);
        }
        // 更新申请单的状态
        Map<String, Object> updateParams = new HashMap<>();
        updateParams.put("applyNoVal", applyNo);
        updateParams.put("applyStatus", applyFinalStatus);
        materielApplyDao.updateMaterielApply(updateParams);
//        logger.info("applyNo“" + applyNo + "”开始创建信息记录台账，" + flag);
//        typeFinfo(applyNo, materielList);
//        logger.info("applyNo“" + applyNo + "”结束创建信息记录台账，" + flag);
    }

    public void typeFinfo(String applyNo, List<JSONObject> materielList) {
        // 查询该申请单号已添加的信息记录并生成物料号码set
        Set materCode = new HashSet();
        List<JSONObject> idJson = materielTypeFDao.queryMaterielTypeFByApplyNo(applyNo);
        if (idJson != null && !idJson.isEmpty()) {
            Map<String, Object> idParam = new HashMap<>();
            idParam.put("belongId", idJson.get(0).getString("id"));
            List<JSONObject> materielTypeFDetailList = materielTypeFDao.queryMaterielTypeFDetail(idParam);
            for (JSONObject oneTypeFDetail : materielTypeFDetailList) {
                materCode.add(oneTypeFDetail.getString("wlhm"));
            }
        }
        List<JSONObject> typeFList = new ArrayList<>();
        for (int index = 0; index < materielList.size(); index++) {
            JSONObject oneMateriel = materielList.get(index);
            if (MaterielConstant.EXTENDRESULT_SUCCESS.equalsIgnoreCase(oneMateriel.getString("extendResult"))
                && "是".equalsIgnoreCase(oneMateriel.getString("sfwg"))
                && !materCode.contains(oneMateriel.getString("wlhm"))) {
                typeFList.add(oneMateriel);
            }
        }
        if (typeFList != null && !typeFList.isEmpty()) {
            JSONObject materielApplyObj = materielApplyDao.queryMaterielApplyById(applyNo);
            JSONObject formDataJson = new JSONObject();
            long flag = System.currentTimeMillis();
            if (idJson == null || idJson.isEmpty()) {
                String id = IdUtil.getId();
                logger.info("applyNo“" + applyNo + "”正在创建信息记录台账，" + flag);
                formDataJson.put("applyNo", applyNo);
                formDataJson.put("id", id);
                formDataJson.put("CREATE_BY_", materielApplyObj.getString("sqUserId"));
                formDataJson.put("CREATE_TIME_", new Date());
                materielTypeFDao.createMaterielTypeF(formDataJson);
                for (JSONObject oneTypeF : typeFList) {
                    oneTypeF.put("belongId", id);
                    oneTypeF.put("materType", "0");
                    oneTypeF.put("currency", "CNY");
                    oneTypeF.put("taxCode", "J5");
                    oneTypeF.put("estimatePrice", "X");
                    oneTypeF.put("wlhm", oneTypeF.getString("wlhm"));
                    oneTypeF.put("wlms", oneTypeF.getString("wlms"));
                    oneTypeF.put("gc", oneTypeF.getString("gc"));
                    oneTypeF.put("xszz", oneTypeF.getString("xszz"));
                    oneTypeF.put("jhjhsj", oneTypeF.getString("jhjhsj"));
                    oneTypeF.put("cgz", oneTypeF.getString("cgz"));
                    oneTypeF.put("jg", oneTypeF.getString("jg"));
                    oneTypeF.put("jgdw", oneTypeF.getString("jgdw"));
                    oneTypeF.put("dw", oneTypeF.getString("dw"));
                    oneTypeF.put("detailId", IdUtil.getId());
                    oneTypeF.put("CREATE_BY_", materielApplyObj.getString("sqUserId"));
                    oneTypeF.put("CREATE_TIME_", new Date());
                    materielTypeFDao.createMaterielTypeFDetail(oneTypeF);
                }
                // 发送钉钉消息
                JSONObject noticeObj = new JSONObject();
                noticeObj.put("content",
                    "物料扩充申请单号:" + materielApplyObj.getString("applyNo") + "已自动生成物料信息添加记录,请完善信息后提交。");
                sendDDNoticeManager.sendNoticeForCommon(materielApplyObj.getString("sqUserId"), noticeObj);
            } else {
                formDataJson.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                formDataJson.put("UPDATE_TIME_", new Date());
                materielTypeFDao.updateMaterielTypeFDetail(formDataJson);
                for (JSONObject oneTypeF : typeFList) {
                    oneTypeF.put("belongId", idJson.get(0).getString("id"));
                    oneTypeF.put("materType", "0");
                    oneTypeF.put("currency", "CNY");
                    oneTypeF.put("taxCode", "J5");
                    oneTypeF.put("estimatePrice", "X");
                    oneTypeF.put("wlhm", oneTypeF.getString("wlhm"));
                    oneTypeF.put("wlms", oneTypeF.getString("wlms"));
                    oneTypeF.put("gc", oneTypeF.getString("gc"));
                    oneTypeF.put("xszz", oneTypeF.getString("xszz"));
                    oneTypeF.put("jhjhsj", oneTypeF.getString("jhjhsj"));
                    oneTypeF.put("cgz", oneTypeF.getString("cgz"));
                    oneTypeF.put("jg", oneTypeF.getString("jg"));
                    oneTypeF.put("jgdw", oneTypeF.getString("jgdw"));
                    oneTypeF.put("dw", oneTypeF.getString("dw"));
                    oneTypeF.put("detailId", IdUtil.getId());
                    oneTypeF.put("CREATE_BY_", materielApplyObj.getString("sqUserId"));
                    oneTypeF.put("CREATE_TIME_", new Date());
                    materielTypeFDao.createMaterielTypeFDetail(oneTypeF);
                }
            }
        }
    }

    // 对于非问题物料，调用SAP的接口。如果调用失败则返回失败，调用成功则更新sap返回的失败的物料为问题物料
    private void callSAP(List<JSONObject> materielSync2SAP, ResultData resultData, long flag) {
        if (materielSync2SAP == null || materielSync2SAP.isEmpty()) {
            return;
        }
        // 调用SAP
        try {
            List<JSONObject> updateErrorMateriels = new ArrayList<>();
            XgwjjSapZWJXERDM_MODIFY_MARALocator locator = new XgwjjSapZWJXERDM_MODIFY_MARALocator();
            XgwjjSapZWJXERDM_MODIFY_MARAPortType service = locator.getXgwjjSapZWJXERDM_MODIFY_MARAHttpPort();
            List<IN_DATA> tempDATA = new ArrayList<>();
            Map<String, JSONObject> tempObj = new HashMap<>();
            for (JSONObject oneMat : materielSync2SAP) {
                IN_DATA inData = convertJSON2Data(oneMat);
                tempDATA.add(inData);
                tempObj.put(oneMat.getString("id"), oneMat);
                if (tempDATA.size() == 20) {
                    IN_DATA[] tempArr = new IN_DATA[tempDATA.size()];
                    callSAPReal(tempDATA.toArray(tempArr), tempObj, service, resultData, updateErrorMateriels);
                    tempDATA.clear();
                    tempObj.clear();
                    if (!resultData.isSuccess()) {
                        return;
                    }
                }
            }
            if (!tempDATA.isEmpty()) {
                IN_DATA[] tempArr = new IN_DATA[tempDATA.size()];
                callSAPReal(tempDATA.toArray(tempArr), tempObj, service, resultData, updateErrorMateriels);
                tempDATA.clear();
                tempObj.clear();
                if (!resultData.isSuccess()) {
                    return;
                }
            }

            // 对于更新失败的，更新JSONObject中的字段并更新到数据库中
            if (!updateErrorMateriels.isEmpty()) {
                for (JSONObject oneMateriel : updateErrorMateriels) {
                    materielDao.updateMateriel2Error(oneMateriel);
                }
            }
        } catch (Exception e) {
            logger.error("物料扩充调用SAP失败，" + flag, e);
            resultData.setSuccess(false);
            resultData.setMessage("调用SAP失败，请通过“查看”进入表单点击“更新到SAP”重试，或联系管理员处理！");
        }
    }

    // 将物料转为SAP数据
    private IN_DATA convertJSON2Data(JSONObject oneMat) {
        IN_DATA inData = new IN_DATA();
        inData.setMATID(oneMat.getString("id"));
        inData.setMATNR(oneMat.getString("wlhm"));
        inData.setMTART(oneMat.getString("wllx"));
        inData.setWERKS(oneMat.getString("gc"));
        inData.setVKORG(oneMat.getString("xszz"));
        inData.setVTWEG(oneMat.getString("fxqd"));
        inData.setMAKTX(oneMat.getString("wlms"));
        inData.setMEINS(oneMat.getString("dw"));
        inData.setMATKL(oneMat.getString("wlz"));
        inData.setSPART(oneMat.getString("cpz"));
        inData.setDWERK(oneMat.getString("jhgc"));
        inData.setKTGRM(oneMat.getString("kmszz"));
        inData.setSTPRS(oneMat.getString("jg"));
        inData.setMAGRV(oneMat.getString("jkgc"));
        inData.setDISPO(oneMat.getString("mrpkzz"));
        inData.setBESKZ(oneMat.getString("cglx"));
        inData.setSOBSL(oneMat.getString("tscgl"));
        inData.setEKGRP(oneMat.getString("cgz"));
        inData.setZPSLX(oneMat.getString("pslx"));
        inData.setPLIFZ(oneMat.getString("jhjhsj"));
        inData.setDISLS(oneMat.getString("pldx"));
        inData.setSBDKZ(oneMat.getString("dljz"));
        inData.setPRCTR(oneMat.getString("lrzx"));
        inData.setBKLAS(oneMat.getString("pgl"));
        inData.setPEINH(oneMat.getString("jgdw"));
        inData.setZCK1(oneMat.getString("kcdd1"));
        inData.setZCK2(oneMat.getString("kcdd2"));
        inData.setZCK3(oneMat.getString("kcdd3"));
        inData.setDISMM(oneMat.getString("mrplx"));
        inData.setMTVFP(oneMat.getString("kyxjc"));
        inData.setLGPRO(oneMat.getString("scccdd"));
        inData.setSERNP(oneMat.getString("xlhcswj"));
        inData.setRGEKZ(oneMat.getString("fc"));
        inData.setDZEIT(oneMat.getString("zzscsj"));
        inData.setMMSTA(oneMat.getString("wlzt"));
        inData.setLGFSB(oneMat.getString("cgccdd"));
        inData.setRETURNCODE("");
        inData.setRETURNMSG("");
        return inData;
    }

    // 批量调用SAP一次
    private void callSAPReal(IN_DATA[] inData, Map<String, JSONObject> tempObj,
        XgwjjSapZWJXERDM_MODIFY_MARAPortType service, ResultData resultData, List<JSONObject> updateErrorMateriels)
        throws RemoteException {
        logger.error("************调用SAP进行物料扩充的物料条数为：" + inData.length);
        IN_DATA[] outData = service.ZWJXERDM_MODIFY_MARA(inData);
        if (outData == null || outData.length == 0) {
            logger.error("return message from SAP is blank");
            resultData.setSuccess(false);
            resultData.setMessage("SAP返回数据为空！请通过“查看”进入表单点击“更新到SAP”重试，或联系管理员处理！");
            return;
        }
        for (IN_DATA temp : outData) {
            String returnCode = temp.getRETURNCODE();
            String returnMessage = temp.getRETURNMSG();
            String id = temp.getMATID();
            if (!"OK".equalsIgnoreCase(returnCode)) {
                if (StringUtils.isBlank(returnMessage)) {
                    logger.error("returnMessage from SAP is blank");
                    resultData.setSuccess(false);
                    resultData.setMessage("SAP调用失败！请通过“查看”进入表单点击“更新到SAP”重试，或联系管理员处理！");
                    return;
                }
                JSONObject oneMateriel = tempObj.get(id);
                oneMateriel.put("markError", MaterielConstant.MARK_ERROR_YES);
                oneMateriel.put("markErrorReason", "SAP校验失败：" + returnMessage);
                oneMateriel.put("markErrorUserId", "1");
                oneMateriel.put("markErrorTime", DateFormatUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                updateErrorMateriels.add(oneMateriel);
            }
        }
    }

    // 对物料进行必填性检查，如果有问题则标注为问题物料
    private void checkMaterielsBeforeSync2SAP(List<JSONObject> materiels, List<JSONObject> respProperties,
        List<JSONObject> materielSync2SAP, List<JSONObject> update2ErrorMateriels) {
        for (JSONObject oneMateriel : materiels) {
            boolean checkResult = true;
            for (int index = 0; index < respProperties.size(); index++) {
                JSONObject oneProperty = respProperties.get(index);
                // 非必填的跳过
                if (!MaterielConstant.REQUIRED_YES.equalsIgnoreCase(oneProperty.getString("required"))) {
                    continue;
                }
                String propertyKey = oneProperty.getString("propertyKey");
                String propertyName = oneProperty.getString("propertyName");
                String propertyValue = oneMateriel.getString(propertyKey);
                String requiredPrePropertyKey = oneProperty.getString("requiredPrePropertyKey");
                // 无前置条件的必填
                if (StringUtils.isBlank(requiredPrePropertyKey)) {
                    if (StringUtils.isBlank(propertyValue)) {
                        oneMateriel.put("markError", MaterielConstant.MARK_ERROR_YES);
                        oneMateriel.put("markErrorReason", propertyName + "为必填项");
                        oneMateriel.put("markErrorUserId", "1");
                        oneMateriel.put("markErrorTime", DateFormatUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                        checkResult = false;
                        break;
                    }
                } else {
                    // 有前置条件的必填
                    String requiredPrePropertyValue = oneMateriel.getString(requiredPrePropertyKey);
                    if (!"jg".equalsIgnoreCase(propertyKey)) {
                        if (StringUtils.isBlank(propertyValue)
                            && requiredPrePropertyValue.equals(oneProperty.getString("requiredPrePropertyValue"))) {
                            String requiredPrePropertyName = oneProperty.getString("requiredPrePropertyName");
                            oneMateriel.put("markError", MaterielConstant.MARK_ERROR_YES);
                            oneMateriel.put("markErrorReason", propertyName + "在“" + requiredPrePropertyName + "”等于“"
                                + requiredPrePropertyValue + "”时为必填项！");
                            oneMateriel.put("markErrorUserId", "1");
                            oneMateriel.put("markErrorTime", DateFormatUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                            checkResult = false;
                            break;
                        }
                    } else {
                        String cglxValue = oneMateriel.getString("cglx");
                        String tscglValue = oneMateriel.getString("tscgl");
                        if ("F".equalsIgnoreCase(cglxValue) && !"30".equalsIgnoreCase(tscglValue)
                            && StringUtils.isBlank(propertyValue)) {
                            oneMateriel.put("markError", MaterielConstant.MARK_ERROR_YES);
                            oneMateriel.put("markErrorReason", "价格在“采购类型”等于“F”，且“特殊采购类”不等于“30”时为必填项！");
                            oneMateriel.put("markErrorUserId", "1");
                            oneMateriel.put("markErrorTime", DateFormatUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                            checkResult = false;
                            break;
                        }
                    }
                }
            }
            // 校验失败则更新到表，校验成功则更新到SAP
            if (checkResult) {
                materielSync2SAP.add(oneMateriel);
            } else {
                update2ErrorMateriels.add(oneMateriel);
            }
        }
    }

    /**
     * 判断该申请单是否所有的环节都提交完成
     *
     * @param applyNo
     * @return
     */
    private boolean judgeAllProcessCommit(String applyNo) {
        if (StringUtils.isBlank(applyNo)) {
            return false;
        }
        JSONObject materielApplyObj = materielApplyDao.queryMaterielApplyById(applyNo);
        if (materielApplyObj == null) {
            return false;
        }
        String gyStatus = materielApplyObj.getString("gyStatus");
        String cgStatus = materielApplyObj.getString("cgStatus");
        String gfStatus = materielApplyObj.getString("gfStatus");
        String cwStatus = materielApplyObj.getString("cwStatus");
        String wlStatus = materielApplyObj.getString("wlStatus");
        String zzStatus = materielApplyObj.getString("zzStatus");
        return MaterielConstant.KCSTATUS_DONE.equals(gyStatus) && MaterielConstant.KCSTATUS_DONE.equals(cgStatus)
            && MaterielConstant.KCSTATUS_DONE.equals(gfStatus) && MaterielConstant.KCSTATUS_DONE.equals(cwStatus)
            && MaterielConstant.KCSTATUS_DONE.equals(wlStatus) && MaterielConstant.KCSTATUS_DONE.equals(zzStatus);
    }

    /**
     * 批量删除扩充申请单和关联的物料
     *
     * @param applyNos
     * @return
     */
    public ResultData applyDel(List<String> applyNos) {
        ResultData resultData = new ResultData();
        if (applyNos == null || applyNos.isEmpty()) {
            return resultData;
        }
        try {
            materielDao.batchDeleteMaterielsByApplyNos(applyNos);
            materielApplyDao.batchDeleteApplyByIds(applyNos);
        } catch (Exception e) {
            logger.error("Exception in applyDel", e);
            resultData.setSuccess(false);
            resultData.setMessage("系统异常");
        }
        return resultData;
    }

    /**
     * 查询用户的物料扩充申请操作角色
     *
     * @param userId
     * @return
     */
    public String queryMaterielOpRoleByUserId(String userId) {
        Map<String, String> param = new HashMap<>();
        param.put("userId", userId);
        List<JSONObject> roles = materielApplyDao.queryMaterielOpRoles(param);
        if (roles == null || roles.isEmpty()) {
            return MaterielConstant.APPLY_OP_SQRKC;
        }
        return roles.get(0).getString("KEY_");
    }

    /**
     * 通过申请单ID查询申请单详情
     *
     * @param applyNo
     * @return
     */
    public JSONObject queryMaterielApplyById(String applyNo) {
        JSONObject applyInfo = materielApplyDao.queryMaterielApplyById(applyNo);
        if (applyInfo == null) {
            return new JSONObject();
        }
        // 查询各环节处理人的姓名
        Set<String> userIds = new HashSet<>();
        if (StringUtils.isNotBlank(applyInfo.getString("gyCommitUserId"))) {
            userIds.add(applyInfo.getString("gyCommitUserId"));
        }
        if (StringUtils.isNotBlank(applyInfo.getString("cgCommitUserId"))) {
            userIds.add(applyInfo.getString("cgCommitUserId"));
        }
        if (StringUtils.isNotBlank(applyInfo.getString("cwCommitUserId"))) {
            userIds.add(applyInfo.getString("cwCommitUserId"));
        }
        if (StringUtils.isNotBlank(applyInfo.getString("gfCommitUserId"))) {
            userIds.add(applyInfo.getString("gfCommitUserId"));
        }
        if (StringUtils.isNotBlank(applyInfo.getString("wlCommitUserId"))) {
            userIds.add(applyInfo.getString("wlCommitUserId"));
        }
        if (StringUtils.isNotBlank(applyInfo.getString("zzCommitUserId"))) {
            userIds.add(applyInfo.getString("zzCommitUserId"));
        }
        Map<String, String> userId2Name = commonService.queryUserNameByIds(userIds);
        if (userId2Name.containsKey(applyInfo.getString("gyCommitUserId"))) {
            applyInfo.put("gyCommitUserName", userId2Name.get(applyInfo.getString("gyCommitUserId")));
        }
        if (userId2Name.containsKey(applyInfo.getString("cgCommitUserId"))) {
            applyInfo.put("cgCommitUserName", userId2Name.get(applyInfo.getString("cgCommitUserId")));
        }
        if (userId2Name.containsKey(applyInfo.getString("cwCommitUserId"))) {
            applyInfo.put("cwCommitUserName", userId2Name.get(applyInfo.getString("cwCommitUserId")));
        }
        if (userId2Name.containsKey(applyInfo.getString("gfCommitUserId"))) {
            applyInfo.put("gfCommitUserName", userId2Name.get(applyInfo.getString("gfCommitUserId")));
        }
        if (userId2Name.containsKey(applyInfo.getString("wlCommitUserId"))) {
            applyInfo.put("wlCommitUserName", userId2Name.get(applyInfo.getString("wlCommitUserId")));
        }
        if (userId2Name.containsKey(applyInfo.getString("zzCommitUserId"))) {
            applyInfo.put("zzCommitUserName", userId2Name.get(applyInfo.getString("zzCommitUserId")));
        }

        // 处理时间格式
        if (StringUtils.isNotBlank(applyInfo.getString("sqCommitTime"))) {
            applyInfo.put("sqCommitTime", DateFormatUtil.formatDateTime(applyInfo.getDate("sqCommitTime")));
        }
        if (StringUtils.isNotBlank(applyInfo.getString("gyCommitTime"))) {
            applyInfo.put("gyCommitTime", DateFormatUtil.formatDateTime(applyInfo.getDate("gyCommitTime")));
        }
        if (StringUtils.isNotBlank(applyInfo.getString("cgCommitTime"))) {
            applyInfo.put("cgCommitTime", DateFormatUtil.formatDateTime(applyInfo.getDate("cgCommitTime")));
        }
        if (StringUtils.isNotBlank(applyInfo.getString("gfCommitTime"))) {
            applyInfo.put("gfCommitTime", DateFormatUtil.formatDateTime(applyInfo.getDate("gfCommitTime")));
        }
        if (StringUtils.isNotBlank(applyInfo.getString("cwCommitTime"))) {
            applyInfo.put("cwCommitTime", DateFormatUtil.formatDateTime(applyInfo.getDate("cwCommitTime")));
        }
        if (StringUtils.isNotBlank(applyInfo.getString("wlCommitTime"))) {
            applyInfo.put("wlCommitTime", DateFormatUtil.formatDateTime(applyInfo.getDate("wlCommitTime")));
        }
        if (StringUtils.isNotBlank(applyInfo.getString("zzCommitTime"))) {
            applyInfo.put("zzCommitTime", DateFormatUtil.formatDateTime(applyInfo.getDate("zzCommitTime")));
        }

        return applyInfo;
    }

    /**
     * 查询申请单列表
     *
     * @param request
     * @param response
     * @return
     */
    public JsonPageResult<?> getApplyList(HttpServletRequest request, HttpServletResponse response, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            Map<String, Object> params = new HashMap<>();
            // 传入条件的构建
            toGetApplyListParams(params, request, doPage);
            String scene = RequestUtil.getString(request, "scene", "");
            if (!"admin".equalsIgnoreCase(ContextUtil.getCurrentUser().getUserNo())
                && !"queryPage".equalsIgnoreCase(scene)) {
                // 传入当前人角色
                String opRoleName = materielApplyService.queryMaterielOpRoleByUserId(ContextUtil.getCurrentUserId());
                params.put("opRoleName", opRoleName);
                params.put("currentUserId", ContextUtil.getCurrentUserId());
            }
            // 查询申请单明细
            List<JSONObject> applyList = materielApplyDao.queryMaterielApplyList(params);
            for (JSONObject oneApply : applyList) {
                String sqCommitTime = oneApply.getString("sqCommitTime");
                if (StringUtils.isNotBlank(sqCommitTime)) {
                    oneApply.put("sqCommitTime", DateFormatUtil.formatDateTime(oneApply.getDate("sqCommitTime")));
                }
            }
            // 查询申请单总数
            int applyListTotal = materielApplyDao.queryMaterielApplyListTotal(params);
            result.setData(applyList);
            result.setTotal(applyListTotal);
        } catch (Exception e) {
            logger.error("Exception in getApplyList", e);
            result.setSuccess(false);
            result.setMessage("系统异常");
        }
        return result;
    }

    /**
     * 提取查询的参数
     *
     * @param params
     * @param request
     */
    private void toGetApplyListParams(Map<String, Object> params, HttpServletRequest request, boolean doPage) {
        // 排序
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)) {
            params.put("sortField", sortField);
            params.put("sortOrder", sortOrder);
        }
        if (StringUtils.isEmpty(sortField)) {
            params.put("sortField", "applyStatus asc,sqCommitTime desc");
        }
        // 分页
        if (doPage) {
            String pageIndex = RequestUtil.getString(request, "pageIndex", "0");
            String pageSize = RequestUtil.getString(request, "pageSize", String.valueOf(Page.DEFAULT_PAGE_SIZE));
            String startIndex = String.valueOf(Integer.parseInt(pageIndex) * Integer.parseInt(pageSize));
            params.put("startIndex", startIndex);
            params.put("pageSize", pageSize);
        }

        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    if ("startTime".equalsIgnoreCase(name)) {
                        value += " 00:00:00";
                    }
                    if ("endTime".equalsIgnoreCase(name)) {
                        value += " 23:59:59";
                    }
                    params.put(name, value);
                }
            }
        }
    }

    public JsonPageResult<?> queryCodeList(HttpServletRequest request, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)) {
            params.put("sortField", sortField);
            params.put("sortOrder", sortOrder);
        }
        if (StringUtils.isEmpty(sortField)) {
            params.put("sortField", "num");
            params.put("sortOrder", "asc");
        }
        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    if ("codeNames".equalsIgnoreCase(name)) {
                        JSONArray systemIdArr = JSONArray.parseArray(value);
                        if (systemIdArr != null && !systemIdArr.isEmpty()) {
                            params.put(name, systemIdArr.toJavaList(String.class));
                        }
                    } else {
                        params.put(name, value);
                    }
                }
            }
        }
        if (doPage) {
            rdmZhglUtil.addPage(request, params);
        }
        List<JSONObject> jscsList = materielApplyDao.queryCodeList(params);
        result.setData(jscsList);
        int countJscsDataList = materielApplyDao.countCodeList(params);
        result.setTotal(countJscsDataList);
        return result;
    }

    public void exportMaterielsByApply(HttpServletResponse response, HttpServletRequest request) {
        try {
            // 先查询得到申请单的列表
            List<JSONObject> applyList = new ArrayList<>();
            JsonPageResult result = materielApplyService.getApplyList(request, response, false);
            if (request != null && result.getData() != null && !result.getData().isEmpty()) {
                applyList = result.getData();
            }
            // 再根据申请单查询物料列表
            List<String> applyNos = new ArrayList<>();
            if (applyList != null && !applyList.isEmpty()) {
                for (JSONObject oneApply : applyList) {
                    applyNos.add(oneApply.getString("applyNo"));
                }
            }
            // 通过模板导出物料
            String templateFileName = "审批单物料扩充导出模板.xlsx";
            File file = new File(MaterielApplyService.class.getClassLoader()
                .getResource("templates/materielExtend/" + templateFileName).toURI());
            Workbook wb = com.redxun.sys.util.ExcelUtil.getWorkBook(templateFileName, new FileInputStream(file));
            if (wb == null) {
                return;
            }
            materielService.generateWbByApplyNo(applyNos, "all", wb, false, false);
            String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
            String excelName = nowDate + "导出扩充物料明细表";
            // 输出
            ExcelUtils.writeXlsxWorkBook2Stream(excelName, wb, response);
        } catch (Exception e) {
            logger.error("Exception in exportMaterielsByApply", e);
        }
    }

    // 批量导入物料信息
    public void importMateriel(JSONObject result, HttpServletRequest request) {
        try {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
            MultipartFile fileObj = multipartRequest.getFile("materielImportFile");
            if (fileObj == null) {
                logger.error("文件为空！");
                result.put("result", false);
                result.getJSONArray("message").add("数据导入失败，内容为空！");
                return;
            }
            Workbook wb = null;
            String fileName = fileObj.getOriginalFilename();
            if (fileName.endsWith(".xls")) {
                wb = new HSSFWorkbook(fileObj.getInputStream());
            } else {
                wb = new XSSFWorkbook(fileObj.getInputStream());
            }
            Sheet sheet = wb.getSheet("物料数据");
            if (sheet == null) {
                sheet = wb.getSheetAt(0);
                if (sheet == null) {
                    logger.error("找不到物料数据导入页");
                    result.put("result", false);
                    result.getJSONArray("message").add("数据导入失败，找不到物料数据导入页！");
                    return;
                }
            }
            int rowNum = sheet.getPhysicalNumberOfRows();
            if (rowNum < 1) {
                logger.error("找不到标题行");
                result.put("result", false);
                result.getJSONArray("message").add("数据导入失败，找不到标题行！");
                return;
            }

            // 解析标题部分
            Row titleRow = sheet.getRow(0);
            if (titleRow == null) {
                logger.error("找不到标题行");
                result.put("result", false);
                result.getJSONArray("message").add("数据导入失败，找不到标题行！");
                return;
            }
            List<String> titleList = new ArrayList<>();
            for (int i = 0; i < titleRow.getLastCellNum(); i++) {
                String titleVal = StringUtils.trim(titleRow.getCell(i).getStringCellValue());
                if ("备注(材料）".equalsIgnoreCase(titleVal)) {
                    titleVal = "备注（材质）";
                }
                titleList.add(titleVal);
            }

            if (rowNum < 2) {
                logger.info("数据行为空");
                result.put("result", false);
                result.getJSONArray("message").add("数据导入失败，有效数据为空!");
                return;
            }

            // 解析验证数据部分,根据角色确定要处理哪些属性，以及默认值的赋值和必填的校验
            List<JSONObject> dataUpdate = new ArrayList<>();
            // 存储角色对应的字段Name和key
            Map<String, Map<String, String>> role2Name2Key = new HashMap<String, Map<String, String>>();
            JSONObject role2Key2Obj = new JSONObject();
            toGetMaterielProperty(role2Name2Key, role2Key2Obj);
            String opRoleName = materielApplyService.queryMaterielOpRoleByUserId(ContextUtil.getCurrentUserId());
            Map<String, String> name2Key = role2Name2Key.get(opRoleName);
            if (name2Key == null || name2Key.isEmpty()) {
                result.put("result", false);
                result.getJSONArray("message").add("数据导入失败，本角色没有可处理的属性！");
                return;
            }
            Map<String, Map<String, JSONObject>> materielDic = materielService.toGetMaterielDic();
            for (int i = 1; i < rowNum; i++) {
                Row row = sheet.getRow(i);
                // 判断是否是个空行（所有cell的值都为空），不处理
                boolean isBlankRow = CommonUtil.judgeIsBlankRow(row);
                if (!isBlankRow) {
                    parseAndCheckDataFromRow(i, row, dataUpdate, titleList, result, materielDic, role2Name2Key,
                        role2Key2Obj, opRoleName);
                }
            }
            // 如果数据有错误，则不进行新增或者更新
            if (!result.getBooleanValue("result")) {
                logger.error("数据中存在错误，本次操作不执行！");
                return;
            }

            // 批量更新
            List<Map<String, String>> updateList = CommonUtil.convertJSONObject2Map(dataUpdate);
            List<Map<String, String>> tempUpdateList = new ArrayList<>();
            for (int i = 0; i < updateList.size(); i++) {
                Map<String, String> oneUpdateMaterielObj = updateList.get(i);
                materielService.convert2UpperMap(oneUpdateMaterielObj);
                tempUpdateList.add(oneUpdateMaterielObj);
                logger.info("申请界面excel更新操作(update)"+"物料主键："+oneUpdateMaterielObj.get("id")+"更新人员:"+ContextUtil.getCurrentUserId()+"采购类型："+oneUpdateMaterielObj.get("cglx") +"采购存储地点"+oneUpdateMaterielObj.get("cgccdd"));
                if (tempUpdateList.size() % 20 == 0) {
                    materielDao.updateMaterielBatch(tempUpdateList);
                    tempUpdateList.clear();
                }
            }
            if (!tempUpdateList.isEmpty()) {
                materielDao.updateMaterielBatch(tempUpdateList);
                tempUpdateList.clear();
            }
            result.put("result", true);
            if (result.getJSONArray("message").isEmpty()) {
                result.getJSONArray("message").add("数据导入完成！");
            }
        } catch (Exception e) {
            logger.error("Exception in importMateriel", e);
            result.put("result", false);
            result.getJSONArray("message").add("数据导入失败，系统异常！");
        }
    }

    /**
     * 查询物料的属性信息，组装成各个角色负责的属性
     *
     * @param role2Name2Key
     * @param role2Key2Obj
     */
    public void toGetMaterielProperty(Map<String, Map<String, String>> role2Name2Key, JSONObject role2Key2Obj) {
        Map<String, Object> params = new HashMap<>();
        List<JSONObject> respProperties = queryMaterielProperties(params);
        if (respProperties == null || respProperties.isEmpty()) {
            return;
        }
        for (int index = 0; index < respProperties.size(); index++) {
            JSONObject propertyObj = respProperties.get(index);
            String respRoleKey = propertyObj.getString("respRoleKey");
            String propertyName = propertyObj.getString("propertyName");
            String propertyKey = propertyObj.getString("propertyKey");
            // Map
            if (!role2Name2Key.containsKey(respRoleKey)) {
                role2Name2Key.put(respRoleKey, new HashMap<>());
            }
            role2Name2Key.get(respRoleKey).put(propertyName, propertyKey);

            // JSONObject
            if (!role2Key2Obj.containsKey(respRoleKey)) {
                role2Key2Obj.put(respRoleKey, new JSONObject());
            }
            JSONObject oneRespRoleKeyObj = role2Key2Obj.getJSONObject(respRoleKey);
            JSONObject oneObj = new JSONObject();
            oneObj.put("propertyKey", propertyKey);
            oneObj.put("propertyName", propertyName);
            oneObj.put("requiredPrePropertyKey", propertyObj.getOrDefault("requiredPrePropertyKey", ""));
            oneObj.put("requiredPrePropertyName", propertyObj.getOrDefault("requiredPrePropertyName", ""));
            oneObj.put("requiredPrePropertyValue", propertyObj.getOrDefault("requiredPrePropertyValue", ""));
            oneObj.put("required", propertyObj.getString("required"));
            oneObj.put("propertyDesc", propertyObj.getOrDefault("propertyDesc", ""));
            oneRespRoleKeyObj.put(propertyKey, oneObj);
        }
    }

    /**
     * 查询物料字段属性说明
     *
     * @param params
     * @return
     */
    public List<JSONObject> queryMaterielProperties(Map<String, Object> params) {
        List<JSONObject> materielProperties = materielDao.queryMaterielProperties(params);
        if (materielProperties == null) {
            materielProperties = new ArrayList<>();
        }
        return materielProperties;
    }

    /**
     * 解析并验证每一行数据
     *
     * @param rowIndex
     * @param row
     * @param dataUpdate
     * @param titleList
     * @param result
     * @param materielDic
     * @param role2Name2Key
     * @param role2Key2Obj
     */
    private void parseAndCheckDataFromRow(int rowIndex, Row row, List<JSONObject> dataUpdate, List<String> titleList,
        JSONObject result, Map<String, Map<String, JSONObject>> materielDic,
        Map<String, Map<String, String>> role2Name2Key, JSONObject role2Key2Obj, String opRoleName) {
        noSqrkcImportMateriels(rowIndex, row, dataUpdate, titleList, result, materielDic, role2Name2Key, role2Key2Obj,
            opRoleName);
    }

    // 非申请人扩充物料的导入
    private void noSqrkcImportMateriels(int rowIndex, Row row, List<JSONObject> dataUpdate, List<String> titleList,
        JSONObject result, Map<String, Map<String, JSONObject>> materielDic,
        Map<String, Map<String, String>> role2Name2Key, JSONObject role2Key2Obj, String opRoleName) {
        int materielIdIndex = titleList.indexOf("物料主键");
        String materielId = CommonUtil.toGetCellStringByType(row, materielIdIndex);
        if (StringUtils.isBlank(materielId)) {
            result.put("result", false);
            result.getJSONArray("message").add("第" + (rowIndex + 1) + "行数据导入失败，“物料主键”为空！");
            return;
        }
        JSONObject materielObj = new JSONObject();
        materielObj.put("id", materielId);

        // 集采类型转换
        int jclxIndex = titleList.indexOf("集采类型");
        String jclx = CommonUtil.toGetCellStringByType(row, jclxIndex);
        // 当且仅当采购扩充时集采类型为必填项
        if (StringUtils.isBlank(jclx) && opRoleName.equalsIgnoreCase("CGKC")) {
            result.put("result", false);
            result.getJSONArray("message").add("第" + (rowIndex + 1) + "行数据导入失败，“集采类型”为空！");
            return;
        } else {
            if (jclx.equalsIgnoreCase("非集采")) {
                materielObj.put("jclx", "fjc");
            } else if (jclx.equalsIgnoreCase("保税集采")) {
                materielObj.put("jclx", "bsjc");
            } else if (jclx.equalsIgnoreCase("供应集采")) {
                materielObj.put("jclx", "gyjc");
            } else {
                materielObj.put("jclx", "");
            }
        }

        // 问题物料信息处理
        int markErrorIndex = titleList.indexOf("是否为问题物料");
        String markErrorValue = CommonUtil.toGetCellStringByType(row, markErrorIndex);
        boolean judgeErrorResult =
            materielService.judgeErrorMateriel(markErrorValue, result, titleList, rowIndex, row, materielObj);
        if (!judgeErrorResult) {
            return;
        }
        // 构造物料信息体，选择性的读取字段，同时赋值本角色属性影响的属性
        Map<String, String> name2Key = role2Name2Key.get(opRoleName);
        for (Map.Entry<String, String> entry : name2Key.entrySet()) {
            int cellIndex = titleList.indexOf(entry.getKey());
            String cellVal = CommonUtil.toGetCellStringByType(row, cellIndex);
            if ("采购类型".equalsIgnoreCase(entry.getKey())) {
                cellVal = cellVal.toUpperCase();
            }
            // 本属性影响的属性赋值
            if (materielDic.containsKey("prePropertyEffect")
                && materielDic.get("prePropertyEffect").containsKey(entry.getValue())) {
                JSONObject prePropertyEffect = materielDic.get("prePropertyEffect").get(entry.getValue());
                materielService.toAssignEffectProperty(prePropertyEffect, cellVal, materielObj);
            }
            // 属性为default不处理
            if (materielDic.containsKey("default") && materielDic.get("default").containsKey(entry.getValue())) {
                continue;
            }
            // 属性为preDefault不处理(前置条件时候已经处理完)
            if (materielDic.containsKey("preDefault") && materielDic.get("preDefault").containsKey(entry.getValue())) {
                continue;
            }
            // 如果是价格字段，值为空则丢弃
            if ("价格".equals(entry.getKey())) {
                if (StringUtils.isBlank(cellVal)) {
                    continue;
                } else {
                    if (!CommonUtil.judgeIsNumber(cellVal)) {
                        result.put("result", false);
                        result.getJSONArray("message").add("第" + (rowIndex + 1) + "行数据导入失败，价格不是数字！");
                        return;
                    }
                    DecimalFormat df = new DecimalFormat("0.00");
                    cellVal = df.format(Double.parseDouble(cellVal));
                }
            }
            //采购存储地点转换大写
            if ("采购存储地点".equals(entry.getKey()) && !cellVal.isEmpty()) {
                cellVal = cellVal.toUpperCase();
            }
            materielObj.put(entry.getValue(), cellVal);
        }
        // 财务角色额外读取重量，采购角色额外读取供应商（如果不为空）
        if (opRoleName.equals(MaterielConstant.APPLY_OP_CWKC)) {
            int cellIndex = titleList.indexOf("重量");
            String cellVal = CommonUtil.toGetCellStringByType(row, cellIndex);
            if (StringUtils.isNotBlank(cellVal)) {
                materielObj.put("zl", cellVal);
            }
        }
        if (opRoleName.equals(MaterielConstant.APPLY_OP_CGKC)) {
            int cellIndex = titleList.indexOf("供应商");
            String cellVal = CommonUtil.toGetCellStringByType(row, cellIndex);
            if (StringUtils.isNotBlank(cellVal)) {
                materielObj.put("gys", cellVal);
            }
        }

        // 对select进行检查(如果是问题物料，只检查不为空的select)
        if (materielDic.containsKey("select")) {
            Map<String, JSONObject> selectObjMap = materielDic.get("select");
            Collection<String> opRoleKeys = name2Key.values();
            for (Map.Entry<String, JSONObject> selectObjEntry : selectObjMap.entrySet()) {
                if (opRoleKeys.contains(selectObjEntry.getKey())) {
                    JSONArray values = selectObjEntry.getValue().getJSONArray("value");
                    String propertyName = selectObjEntry.getValue().getString("propertyName");
                    String cellValue = materielObj.getString(selectObjEntry.getKey());

                    if (!"是".equals(markErrorValue) || StringUtils.isNotBlank(cellValue)) {
                        if (!values.contains(cellValue)) {
                            result.put("result", false);
                            result.getJSONArray("message")
                                .add("第" + (rowIndex + 1) + "行数据导入失败，" + propertyName + "值非法！");
                            return;
                        }
                    }
                }
            }
        }

        // 对于非工艺角色，不允许将问题物料状态由“是”改为“否”
        if (!MaterielConstant.APPLY_OP_GYKC.equals(opRoleName) && !"是".equals(markErrorValue)) {
            JSONObject materielDBObj = materielDao.queryMaterielById(materielId);
            if (MaterielConstant.MARK_ERROR_YES.equals(materielDBObj.getString("markError"))) {
                result.put("result", false);
                result.getJSONArray("message").add("第" + (rowIndex + 1) + "行数据导入失败，本角色所在环节不允许将问题物料状态由“是”改为“否”！");
                return;
            }
        }
        // 对属性进行必填性检查（非问题物料）
        if (!"是".equals(markErrorValue)) {
            JSONObject oneRoleProperty = role2Key2Obj.getJSONObject(opRoleName);
            boolean judgeResult = materielService.judgeRequiredByOwnProperty(rowIndex, row, oneRoleProperty,
                materielObj, result, titleList);
            if (!judgeResult) {
                return;
            }
        }

        dataUpdate.add(materielObj);
    }

    public void exportApplyMaterials(HttpServletRequest request, HttpServletResponse response) {
        String applyNos = RequestUtil.getString(request, "applyNos", "");
        try {
            // 通过模板导出物料
            String templateFileName = "审批单物料扩充导出模板.xlsx";
            File file = new File(MaterielService.class.getClassLoader()
                .getResource("templates/materielExtend/" + templateFileName).toURI());
            Workbook wb = com.redxun.sys.util.ExcelUtil.getWorkBook(templateFileName, new FileInputStream(file));
            if (wb == null) {
                return;
            }
            generateWbByApplyNos(applyNos, wb);
            String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
            String downLoadFileName = nowDate + "导出申请单物料信息";
            ExcelUtils.writeXlsxWorkBook2Stream(downLoadFileName, wb, response);
        } catch (Exception e) {
            logger.error("Exception in exportApplyMaterials", e);
            return;
        }
    }

    private void generateWbByApplyNos(String applyNos, Workbook wb) {
        Map<String, Object> param = new HashMap<>(16);
        String[] applyArray = applyNos.split(",");
        List<String> applyNoList = Arrays.asList(applyArray);
        param.put("applyNoList", applyNoList);
        List<JSONObject> materielList = materielApplyDao.queryMaterielsByApplyNos(param);
        if (materielList == null || materielList.isEmpty()) {
            return;
        }
        Sheet sheet = wb.getSheet("物料数据");
        // 解析汉字与key的对应关系
        Map<String, Object> params = new HashMap<>(16);
        List<JSONObject> properties = queryMaterielProperties(params);
        if (properties == null || properties.isEmpty()) {
            return;
        }
        Map<String, String> name2Key = new HashMap<>();
        for (int index = 0; index < properties.size(); index++) {
            JSONObject oneProp = properties.get(index);
            name2Key.put(oneProp.getString("propertyName"), oneProp.getString("propertyKey"));
        }
        name2Key.put("审批单号", "applyNo");
        name2Key.put("物料主键", "id");
        name2Key.put("是否为问题物料", "markError");
        name2Key.put("问题原因", "markErrorReason");
        name2Key.put("物料状态", "wlzt");
        name2Key.put("申请人", "sqUserName");
        name2Key.put("申请部门", "sqDeptName");
        // 取标题行数据
        Row titleRow = sheet.getRow(0);
        if (titleRow == null) {
            logger.error("找不到标题行");
            return;
        }
        List<String> titleList = new ArrayList<>();
        for (int i = 0; i < titleRow.getLastCellNum(); i++) {
            titleList.add(StringUtils.trim(titleRow.getCell(i).getStringCellValue()));
        }
        // 写入数据
        for (int rowIndex = 0; rowIndex < materielList.size(); rowIndex++) {
            JSONObject oneMateriel = materielList.get(rowIndex);
            // 集采类型文字替换
            if (oneMateriel.containsKey("jclx")) {
                if (oneMateriel.getString("jclx").equalsIgnoreCase("fjc")) {
                    oneMateriel.put("jclx", "非集采");
                } else if (oneMateriel.getString("jclx").equalsIgnoreCase("bsjc")) {
                    oneMateriel.put("jclx", "保税集采");
                } else if (oneMateriel.getString("jclx").equalsIgnoreCase("gyjc")) {
                    oneMateriel.put("jclx", "供应集采");
                }
            }
            Row row = sheet.createRow(rowIndex + 1);
            for (int colIndex = 0; colIndex < titleList.size(); colIndex++) {
                String oneTitleName = titleList.get(colIndex);
                String oneTitleKey = name2Key.get(oneTitleName);
                String value = "";
                if (StringUtils.isNotBlank(oneTitleKey)) {
                    value = oneMateriel.getString(oneTitleKey);
                }
                Cell cell = row.createCell(colIndex);
                if ("是否为问题物料".equalsIgnoreCase(oneTitleName)) {
                    if (MaterielConstant.MARK_ERROR_YES.equalsIgnoreCase(value)) {
                        value = "是";
                    } else if (MaterielConstant.MARK_ERROR_NO.equalsIgnoreCase(value)) {
                        value = "否";
                    }
                }
                cell.setCellValue(value);
            }
        }
    }

    /**
     * 更改申请单的状态到错误已处理
     * 
     */
    public void applyFailConfirm(JSONObject result, HttpServletRequest request) {
        String applyNo = RequestUtil.getString(request, "applyNo", "");
        if (StringUtils.isBlank(applyNo)) {
            result.put("message", "操作失败！");
            return;
        }
        materielApplyDao.updateApplyFailConfirm(applyNo);
        result.put("message", "操作成功！");
    }

    public void checkSuccessConfirm(JSONObject result, HttpServletRequest request) {
        String currentUserId = ContextUtil.getCurrentUserId();
        List<JSONObject> failEndApplys = materielApplyDao.queryFailEndApplyBySQR(currentUserId);
        if (failEndApplys != null && !failEndApplys.isEmpty()) {
            result.put("result", false);
        } else {
            result.put("result", true);
        }
    }
}
