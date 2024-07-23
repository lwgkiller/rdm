package com.redxun.matPriceReview.core.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.matPriceReview.core.dao.MatPriceReviewDao;
import com.redxun.matPriceReview.core.service.oaWsdl.AttachmentForm;
import com.redxun.matPriceReview.core.service.oaWsdl.IKmReviewWebserviceService;
import com.redxun.matPriceReview.core.service.oaWsdl.IKmReviewWebserviceServiceServiceLocator;
import com.redxun.matPriceReview.core.service.oaWsdl.KmReviewParamterForm;
import com.redxun.materielextend.core.util.ResultData;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;

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
public class MatPriceCallOAService {
    private static final Logger logger = LoggerFactory.getLogger(MatPriceCallOAService.class);

    @Autowired
    private MatPriceReviewDao matPriceReviewDao;

    /**
     * 调用OA接口创建流程，
     * 
     * @param formDataJson
     * @return
     */
    public String callOaAddReview(JSONObject formDataJson, ResultData result) {
        try {
            IKmReviewWebserviceServiceServiceLocator locator = new IKmReviewWebserviceServiceServiceLocator();
            IKmReviewWebserviceService service = locator.getIKmReviewWebserviceServicePort();
            // 构建参数
            KmReviewParamterForm webForm = createForm(formDataJson);
            // 调用接口
            String oaKeyId = service.addReview(webForm);
            logger.info("调用OA成功，返回主键:" + oaKeyId);
            result.setSuccess(true);
            return oaKeyId;
        } catch (Exception e) {
            logger.error("Exception in callOaAddReview", e);
            result.setSuccess(false);
            result.setMessage("系统异常！具体信息：" + e.getMessage());
            return "";
        }
    }

    /**
     * 创建表单数据
     */
    private KmReviewParamterForm createForm(JSONObject formDataJson) throws Exception {
        KmReviewParamterForm form = new KmReviewParamterForm();
        String reviewCategory = formDataJson.getString("reviewCategory");
        String title = "采购物料价格审批表";
        String templateId = "17b7c20c82a6306cf270a3b4a31afac3";
        if ("disposable".equalsIgnoreCase(reviewCategory)) {
            title = "一次性采购物料价格审批表";
            templateId = "17b772750a688da5d6f9a174246ac2fd";
        } else if ("emergency".equalsIgnoreCase(reviewCategory)) {
            title = "应急采购物料价格审批表";
            templateId = "17b4e4c86b3d87aec6aca45421984af8";
        }
        // 文档标题
        form.setDocSubject(title);
        // 文档模板id
        form.setFdTemplateId(templateId);
        // 文档的状态
        form.setDocStatus("10");
        // 流程发起人
        JSONObject creatorObj = new JSONObject();
        creatorObj.put("LoginName", formDataJson.getString("applyUserNo"));
        form.setDocCreator(creatorObj.toJSONString());

        // 流程表单内容
        String formValues = createFormValueStr(reviewCategory, formDataJson);
        form.setFormValues(formValues);
        // 附件列表
        String applyId = formDataJson.getString("id");
        if (StringUtils.isNotBlank(applyId)) {
            AttachmentForm[] attForms = createAllAtts(reviewCategory, applyId);
            form.setAttachmentForms(attForms);
        }

        return form;
    }

    /**
     * 创建表单内容
     * 
     * @return
     */
    private String createFormValueStr(String reviewCategory, JSONObject formDataJson) {
        JSONObject formObj = new JSONObject();
        if ("common".equalsIgnoreCase(reviewCategory)) {
            createBaseInfoCommon(formObj, formDataJson);
            createMatDetailsCommon(formObj, formDataJson.getString("id"));
            createJcRecords(formObj, formDataJson.getString("id"));
            createFjcRecords(formObj, formDataJson.getString("id"));
        } else if ("disposable".equalsIgnoreCase(reviewCategory)) {
            createBaseInfoDisposable(formObj, formDataJson);
            createMatDetailsDisposable(formObj, formDataJson.getString("id"));
            createJcRecords(formObj, formDataJson.getString("id"));
            createFjcRecords(formObj, formDataJson.getString("id"));
        } else if ("emergency".equalsIgnoreCase(reviewCategory)) {
            createBaseInfoEmergency(formObj, formDataJson);
            createMatDetailsEmergency(formObj, formDataJson.getString("id"));
            createJcRecords(formObj, formDataJson.getString("id"));
            createFjcRecords(formObj, formDataJson.getString("id"));
        }

        return formObj.toJSONString();
    }

    /**
     * 创建附件列表
     */
    private AttachmentForm[] createAllAtts(String reviewCategory, String applyId) throws Exception {
        List<AttachmentForm> attForms = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();
        params.put("reviewId", applyId);
        List<JSONObject> fileInfos = matPriceReviewDao.getFileList(params);
        if (fileInfos != null && !fileInfos.isEmpty()) {
            String filePathBase =
                WebAppUtil.getProperty("jgspFilePathBase") + File.separator + applyId + File.separator;
            // TODO 不同表单的附件id
            String fdKey = "";
            if ("common".equalsIgnoreCase(reviewCategory)) {
                fdKey = "";
            } else if ("disposable".equalsIgnoreCase(reviewCategory)) {
                fdKey = "";
            } else if ("emergency".equalsIgnoreCase(reviewCategory)) {
                fdKey = "";
            }

            for (JSONObject oneFile : fileInfos) {
                String fileName = oneFile.getString("fileName");
                String suffix = CommonFuns.toGetFileSuffix(fileName);
                String id = oneFile.getString("id");
                String fullFilePath = filePathBase + id + "." + suffix;
                AttachmentForm attForm = createAtt(fileName, fullFilePath, fdKey);
                attForms.add(attForm);
            }
        }
        return attForms.toArray(new AttachmentForm[0]);
    }

    /**
     * 创建附件对象
     */
    private AttachmentForm createAtt(String fileName, String fileFullPath, String fdKey) throws Exception {
        AttachmentForm attForm = new AttachmentForm();
        attForm.setFdFileName(fileName);
        attForm.setFdKey(fdKey);
        byte[] data = file2bytes(fileFullPath);
        attForm.setFdAttachment(data);
        return attForm;
    }

    // 基本信息——常规
    private void createBaseInfoCommon(JSONObject result, JSONObject formDataJson) {
        result.put("", formDataJson.getString("applyDeptName"));
        result.put("", formDataJson.getString("applyUserName"));
        result.put("", formDataJson.getString("applyUserMobile"));
        result.put("", formDataJson.getString("applyCategory"));
        result.put("", formDataJson.getString("matCategory"));
        result.put("", formDataJson.getString("applierCode"));
        result.put("", formDataJson.getString("applierName"));
        result.put("", formDataJson.getString("ptProduct"));
        result.put("", formDataJson.getString("cgUser"));
        result.put("", formDataJson.getString("zxRate"));
        result.put("", formDataJson.getString("jgExcuteStart"));
        result.put("", formDataJson.getString("jgExcuteEnd"));
        result.put("", formDataJson.getString("priceDesc"));
    }

    // 基本信息——一次性
    private void createBaseInfoDisposable(JSONObject result, JSONObject formDataJson) {
        result.put("", formDataJson.getString("applyDeptName"));
        result.put("", formDataJson.getString("applyUserName"));
        result.put("", formDataJson.getString("applyUserMobile"));
        result.put("", formDataJson.getString("applyCategory"));
        result.put("", formDataJson.getString("applierCode"));
        result.put("", formDataJson.getString("applierName"));
        result.put("", formDataJson.getString("address"));
        result.put("", formDataJson.getString("ptProduct"));
        result.put("", formDataJson.getString("zlFeature"));
        result.put("", formDataJson.getString("matCategory"));
        result.put("", formDataJson.getString("moneyCategory"));
        result.put("", formDataJson.getString("zxRate"));
        result.put("", formDataJson.getString("sfft"));
        result.put("", formDataJson.getString("jsxyNumber"));
        result.put("", formDataJson.getString("cgApplyNo"));
        result.put("", formDataJson.getString("cgOrderNo"));
        result.put("", formDataJson.getString("requireArrivalTime"));
        result.put("", formDataJson.getString("cgUser"));
        result.put("", formDataJson.getString("priceDesc"));
    }

    // 基本信息——应急
    private void createBaseInfoEmergency(JSONObject result, JSONObject formDataJson) {
        result.put("", formDataJson.getString("applyDeptName"));
        result.put("", formDataJson.getString("applyUserName"));
        result.put("", formDataJson.getString("applyUserMobile"));
        result.put("", formDataJson.getString("applyCategory"));
        result.put("", formDataJson.getString("applierCode"));
        result.put("", formDataJson.getString("applierName"));
        result.put("", formDataJson.getString("address"));
        result.put("", formDataJson.getString("ptProduct"));
        result.put("", formDataJson.getString("zlFeature"));
        result.put("", formDataJson.getString("matCategory"));
        result.put("", formDataJson.getString("moneyCategory"));
        result.put("", formDataJson.getString("zxRate"));
        result.put("", formDataJson.getString("sfft"));
        result.put("", formDataJson.getString("jsxyNumber"));
        result.put("", formDataJson.getString("cgApplyNo"));
        result.put("", formDataJson.getString("cgOrderNo"));
        result.put("", formDataJson.getString("requireArrivalTime"));
        result.put("", formDataJson.getString("cgUser"));
        result.put("", formDataJson.getString("priceDesc"));
    }

    // 物料信息——常规
    private void createMatDetailsCommon(JSONObject result, String reviewId) {
        result.put("", new JSONObject());
        // 取物料列表
        JSONObject param = new JSONObject();
        param.put("reviewId", reviewId);
        List<JSONObject> matDetailList = matPriceReviewDao.queryMatDetailList(param);
        if (matDetailList == null || matDetailList.isEmpty()) {
            return;
        }
        Map<String, List<String>> column2Values = new HashMap<>();
        column2Values.put("matCode", new ArrayList<>());
        column2Values.put("matName", new ArrayList<>());
        column2Values.put("tuhao", new ArrayList<>());
        column2Values.put("zjxh", new ArrayList<>());
        column2Values.put("jldw", new ArrayList<>());
        column2Values.put("cgsl", new ArrayList<>());
        column2Values.put("sbjg", new ArrayList<>());
        column2Values.put("pzjghs", new ArrayList<>());
        column2Values.put("pzjgbhs", new ArrayList<>());
        column2Values.put("yfcdf", new ArrayList<>());
        column2Values.put("jzdsjgbhs", new ArrayList<>());
        column2Values.put("zjf", new ArrayList<>());
        column2Values.put("remark", new ArrayList<>());
        for (JSONObject oneData : matDetailList) {
            column2Values.get("matCode").add(oneData.getString("matCode"));
            column2Values.get("matName").add(oneData.getString("matName"));
            column2Values.get("tuhao").add(oneData.getString("tuhao"));
            column2Values.get("zjxh").add(oneData.getString("zjxh"));
            column2Values.get("jldw").add(oneData.getString("jldw"));
            column2Values.get("cgsl").add(oneData.getString("cgsl"));
            column2Values.get("sbjg").add(oneData.getString("sbjg"));
            column2Values.get("pzjghs").add(oneData.getString("pzjghs"));
            column2Values.get("pzjgbhs").add(oneData.getString("pzjgbhs"));
            column2Values.get("yfcdf").add(oneData.getString("yfcdf"));
            column2Values.get("jzdsjgbhs").add(oneData.getString("jzdsjgbhs"));
            column2Values.get("zjf").add(oneData.getString("zjf"));
            column2Values.get("remark").add(oneData.getString("remark"));
        }

        JSONObject subTable = result.getJSONObject("");
        subTable.put(".", column2Values.get("matCode"));
        subTable.put(".", column2Values.get("matName"));
        subTable.put(".", column2Values.get("tuhao"));
        subTable.put(".", column2Values.get("zjxh"));
        subTable.put(".", column2Values.get("jldw"));
        subTable.put(".", column2Values.get("cgsl"));
        subTable.put(".", column2Values.get("sbjg"));
        subTable.put(".", column2Values.get("pzjghs"));
        subTable.put(".", column2Values.get("pzjgbhs"));
        subTable.put(".", column2Values.get("yfcdf"));
        subTable.put(".", column2Values.get("jzdsjgbhs"));
        subTable.put(".", column2Values.get("zjf"));
        subTable.put(".", column2Values.get("remark"));
    }

    // 物料信息——一次性
    private void createMatDetailsDisposable(JSONObject result, String reviewId) {
        result.put("", new JSONObject());
        // 取物料列表
        JSONObject param = new JSONObject();
        param.put("reviewId", reviewId);
        List<JSONObject> matDetailList = matPriceReviewDao.queryMatDetailList(param);
        if (matDetailList == null || matDetailList.isEmpty()) {
            return;
        }
        Map<String, List<String>> column2Values = new HashMap<>();
        column2Values.put("matCode", new ArrayList<>());
        column2Values.put("matName", new ArrayList<>());
        column2Values.put("tuhao", new ArrayList<>());
        column2Values.put("zjxh", new ArrayList<>());
        column2Values.put("jldw", new ArrayList<>());
        column2Values.put("cgsl", new ArrayList<>());
        column2Values.put("jghs", new ArrayList<>());
        column2Values.put("jgbhs", new ArrayList<>());
        column2Values.put("yfcdf", new ArrayList<>());
        column2Values.put("remark", new ArrayList<>());
        for (JSONObject oneData : matDetailList) {
            column2Values.get("matCode").add(oneData.getString("matCode"));
            column2Values.get("matName").add(oneData.getString("matName"));
            column2Values.get("tuhao").add(oneData.getString("tuhao"));
            column2Values.get("zjxh").add(oneData.getString("zjxh"));
            column2Values.get("jldw").add(oneData.getString("jldw"));
            column2Values.get("cgsl").add(oneData.getString("cgsl"));
            column2Values.get("jghs").add(oneData.getString("jghs"));
            column2Values.get("jgbhs").add(oneData.getString("jgbhs"));
            column2Values.get("yfcdf").add(oneData.getString("yfcdf"));
            column2Values.get("remark").add(oneData.getString("remark"));
        }

        JSONObject subTable = result.getJSONObject("");
        subTable.put(".", column2Values.get("matCode"));
        subTable.put(".", column2Values.get("matName"));
        subTable.put(".", column2Values.get("tuhao"));
        subTable.put(".", column2Values.get("zjxh"));
        subTable.put(".", column2Values.get("jldw"));
        subTable.put(".", column2Values.get("cgsl"));
        subTable.put(".", column2Values.get("jghs"));
        subTable.put(".", column2Values.get("jgbhs"));
        subTable.put(".", column2Values.get("yfcdf"));
        subTable.put(".", column2Values.get("remark"));
    }

    // 物料信息——应急
    private void createMatDetailsEmergency(JSONObject result, String reviewId) {
        result.put("", new JSONObject());
        // 取物料列表
        JSONObject param = new JSONObject();
        param.put("reviewId", reviewId);
        List<JSONObject> matDetailList = matPriceReviewDao.queryMatDetailList(param);
        if (matDetailList == null || matDetailList.isEmpty()) {
            return;
        }
        Map<String, List<String>> column2Values = new HashMap<>();
        column2Values.put("matCode", new ArrayList<>());
        column2Values.put("matName", new ArrayList<>());
        column2Values.put("tuhao", new ArrayList<>());
        column2Values.put("zjxh", new ArrayList<>());
        column2Values.put("jldw", new ArrayList<>());
        column2Values.put("cgsl", new ArrayList<>());
        column2Values.put("jghs", new ArrayList<>());
        column2Values.put("jgbhs", new ArrayList<>());
        column2Values.put("yfcdf", new ArrayList<>());
        column2Values.put("remark", new ArrayList<>());
        for (JSONObject oneData : matDetailList) {
            column2Values.get("matCode").add(oneData.getString("matCode"));
            column2Values.get("matName").add(oneData.getString("matName"));
            column2Values.get("tuhao").add(oneData.getString("tuhao"));
            column2Values.get("zjxh").add(oneData.getString("zjxh"));
            column2Values.get("jldw").add(oneData.getString("jldw"));
            column2Values.get("cgsl").add(oneData.getString("cgsl"));
            column2Values.get("jghs").add(oneData.getString("jghs"));
            column2Values.get("jgbhs").add(oneData.getString("jgbhs"));
            column2Values.get("yfcdf").add(oneData.getString("yfcdf"));
            column2Values.get("remark").add(oneData.getString("remark"));
        }

        JSONObject subTable = result.getJSONObject("");
        subTable.put(".", column2Values.get("matCode"));
        subTable.put(".", column2Values.get("matName"));
        subTable.put(".", column2Values.get("tuhao"));
        subTable.put(".", column2Values.get("zjxh"));
        subTable.put(".", column2Values.get("jldw"));
        subTable.put(".", column2Values.get("cgsl"));
        subTable.put(".", column2Values.get("jghs"));
        subTable.put(".", column2Values.get("jgbhs"));
        subTable.put(".", column2Values.get("yfcdf"));
        subTable.put(".", column2Values.get("remark"));
    }

    // 集采信息记录
    private void createJcRecords(JSONObject result, String reviewId) {
        result.put("", new JSONObject());
        // 取集采信息记录列表
        JSONObject params = new JSONObject();
        // 传入条件的构建
        params.put("reviewId", reviewId);
        params.put("jclx", "jc");
        List<JSONObject> recordList = matPriceReviewDao.queryRecordList(params);
        if (recordList == null || recordList.isEmpty()) {
            return;
        }
        Map<String, List<String>> column2Values = new HashMap<>();
        column2Values.put("cgzz", new ArrayList<>());
        column2Values.put("cggs", new ArrayList<>());
        column2Values.put("gc", new ArrayList<>());
        column2Values.put("applierName", new ArrayList<>());
        column2Values.put("applierCode", new ArrayList<>());
        column2Values.put("matName", new ArrayList<>());
        column2Values.put("matCode", new ArrayList<>());
        column2Values.put("jldw", new ArrayList<>());
        column2Values.put("jgdw", new ArrayList<>());
        column2Values.put("jgNumber", new ArrayList<>());
        column2Values.put("jbdw", new ArrayList<>());
        column2Values.put("bizhong", new ArrayList<>());
        column2Values.put("wsdj", new ArrayList<>());
        column2Values.put("shuima", new ArrayList<>());
        column2Values.put("shuilv", new ArrayList<>());
        column2Values.put("priceValidStart", new ArrayList<>());
        column2Values.put("priceValidEnd", new ArrayList<>());
        column2Values.put("fktj", new ArrayList<>());
        column2Values.put("jsfs", new ArrayList<>());
        column2Values.put("zgPrice", new ArrayList<>());
        column2Values.put("jgld", new ArrayList<>());
        column2Values.put("jgsy", new ArrayList<>());
        column2Values.put("cpf", new ArrayList<>());
        column2Values.put("remark", new ArrayList<>());
        for (JSONObject oneData : recordList) {
            column2Values.get("cgzz").add(oneData.getString("cgzz"));
            column2Values.get("cggs").add(oneData.getString("cggs"));
            column2Values.get("gc").add(oneData.getString("gc"));
            column2Values.get("applierName").add(oneData.getString("applierName"));
            column2Values.get("applierCode").add(oneData.getString("applierCode"));
            column2Values.get("matName").add(oneData.getString("matName"));
            column2Values.get("matCode").add(oneData.getString("matCode"));
            column2Values.get("jldw").add(oneData.getString("jldw"));
            column2Values.get("jgdw").add(oneData.getString("jgdw"));
            column2Values.get("jgNumber").add(oneData.getString("jgNumber"));
            column2Values.get("jbdw").add(oneData.getString("jbdw"));
            column2Values.get("bizhong").add(oneData.getString("bizhong"));
            column2Values.get("wsdj").add(oneData.getString("wsdj"));
            column2Values.get("shuima").add(oneData.getString("shuima"));
            column2Values.get("shuilv").add(oneData.getString("shuilv"));
            column2Values.get("priceValidStart").add(oneData.getString("priceValidStart"));
            column2Values.get("priceValidEnd").add(oneData.getString("priceValidEnd"));
            column2Values.get("fktj").add(oneData.getString("fktj"));
            column2Values.get("jsfs").add(oneData.getString("jsfs"));
            column2Values.get("zgPrice").add(oneData.getString("zgPrice"));
            column2Values.get("jgld").add(oneData.getString("jgld"));
            column2Values.get("jgsy").add(oneData.getString("jgsy"));
            column2Values.get("cpf").add(oneData.getString("cpf"));
            column2Values.get("remark").add(oneData.getString("remark"));
        }

        JSONObject subTable = result.getJSONObject("");
        subTable.put(".", column2Values.get("cgzz"));
        subTable.put(".", column2Values.get("cggs"));
        subTable.put(".", column2Values.get("gc"));
        subTable.put(".", column2Values.get("applierName"));
        subTable.put(".", column2Values.get("applierCode"));
        subTable.put(".", column2Values.get("matName"));
        subTable.put(".", column2Values.get("matCode"));
        subTable.put(".", column2Values.get("jldw"));
        subTable.put(".", column2Values.get("jgdw"));
        subTable.put(".", column2Values.get("jgNumber"));
        subTable.put(".", column2Values.get("jbdw"));
        subTable.put(".", column2Values.get("bizhong"));
        subTable.put(".", column2Values.get("wsdj"));
        subTable.put(".", column2Values.get("shuima"));
        subTable.put(".", column2Values.get("shuilv"));
        subTable.put(".", column2Values.get("priceValidStart"));
        subTable.put(".", column2Values.get("priceValidEnd"));
        subTable.put(".", column2Values.get("fktj"));
        subTable.put(".", column2Values.get("jsfs"));
        subTable.put(".", column2Values.get("zgPrice"));
        subTable.put(".", column2Values.get("jgld"));
        subTable.put(".", column2Values.get("jgsy"));
        subTable.put(".", column2Values.get("cpf"));
        subTable.put(".", column2Values.get("remark"));
    }

    // 非集采信息记录
    private void createFjcRecords(JSONObject result, String reviewId) {
        result.put("", new JSONObject());
        // 取非集采信息记录列表
        JSONObject params = new JSONObject();
        // 传入条件的构建
        params.put("reviewId", reviewId);
        params.put("jclx", "fjc");
        List<JSONObject> recordList = matPriceReviewDao.queryRecordList(params);
        if (recordList == null || recordList.isEmpty()) {
            return;
        }
        Map<String, List<String>> column2Values = new HashMap<>();
        column2Values.put("applierCode", new ArrayList<>());
        column2Values.put("applierName", new ArrayList<>());
        column2Values.put("matCode", new ArrayList<>());
        column2Values.put("matName", new ArrayList<>());
        column2Values.put("mrpkzz", new ArrayList<>());
        column2Values.put("cgzz", new ArrayList<>());
        column2Values.put("gc", new ArrayList<>());
        column2Values.put("recordType", new ArrayList<>());
        column2Values.put("planDeliveryTime", new ArrayList<>());
        column2Values.put("cgz", new ArrayList<>());
        column2Values.put("jingjia", new ArrayList<>());
        column2Values.put("bizhong", new ArrayList<>());
        column2Values.put("jgdw", new ArrayList<>());
        column2Values.put("jldw", new ArrayList<>());
        column2Values.put("shuima", new ArrayList<>());
        column2Values.put("sfGjPrice", new ArrayList<>());
        for (JSONObject oneData : recordList) {
            column2Values.get("applierCode").add(oneData.getString("applierCode"));
            column2Values.get("applierName").add(oneData.getString("applierName"));
            column2Values.get("matCode").add(oneData.getString("matCode"));
            column2Values.get("matName").add(oneData.getString("matName"));
            column2Values.get("mrpkzz").add(oneData.getString("mrpkzz"));
            column2Values.get("cgzz").add(oneData.getString("cgzz"));
            column2Values.get("gc").add(oneData.getString("gc"));
            column2Values.get("recordType").add(oneData.getString("recordType"));
            column2Values.get("planDeliveryTime").add(oneData.getString("planDeliveryTime"));
            column2Values.get("cgz").add(oneData.getString("cgz"));
            column2Values.get("jingjia").add(oneData.getString("jingjia"));
            column2Values.get("bizhong").add(oneData.getString("bizhong"));
            column2Values.get("jgdw").add(oneData.getString("jgdw"));
            column2Values.get("jldw").add(oneData.getString("jldw"));
            column2Values.get("shuima").add(oneData.getString("shuima"));
            column2Values.get("sfGjPrice").add(oneData.getString("sfGjPrice"));
        }

        JSONObject subTable = result.getJSONObject("");
        subTable.put(".", column2Values.get("applierCode"));
        subTable.put(".", column2Values.get("applierName"));
        subTable.put(".", column2Values.get("matCode"));
        subTable.put(".", column2Values.get("matName"));
        subTable.put(".", column2Values.get("mrpkzz"));
        subTable.put(".", column2Values.get("cgzz"));
        subTable.put(".", column2Values.get("gc"));
        subTable.put(".", column2Values.get("recordType"));
        subTable.put(".", column2Values.get("planDeliveryTime"));
        subTable.put(".", column2Values.get("cgz"));
        subTable.put(".", column2Values.get("jingjia"));
        subTable.put(".", column2Values.get("bizhong"));
        subTable.put(".", column2Values.get("jgdw"));
        subTable.put(".", column2Values.get("jldw"));
        subTable.put(".", column2Values.get("shuima"));
        subTable.put(".", column2Values.get("sfGjPrice"));
    }

    /**
     * 将文件转换为字节编码
     */
    private byte[] file2bytes(String fileFullPath) throws Exception {
        InputStream in = new FileInputStream(fileFullPath);
        byte[] data = new byte[in.available()];
        try {
            in.read(data);
        } finally {
            try {
                in.close();
            } catch (Exception ex) {
            }
        }
        return data;
    }
}
