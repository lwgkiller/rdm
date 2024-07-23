package com.redxun.rdmCommon.core.controller;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.environment.core.dao.GsClhbDao;
import com.redxun.environment.core.service.ClhbService;
import com.redxun.environment.core.service.GsClhbService;
import com.redxun.materielextend.core.service.MaterielApiService;
import com.redxun.productDataManagement.core.manager.ProductSpectrumService;
import com.redxun.rdmCommon.core.manager.PdmApiManager;
import com.redxun.rdmZhgl.core.service.MaterialApplyService;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.serviceEngineering.core.service.ExportPartsAtlasService;
import com.redxun.serviceEngineering.core.service.SeGeneralKanBanNewService;
import com.redxun.serviceEngineering.core.service.WgjzlsjService;
import com.redxun.standardManager.core.util.ResultUtil;
import com.redxun.world.core.service.CkddService;
import com.redxun.wwrz.core.service.CeinfoService;
import com.redxun.xcmgFinance.core.manager.OAFinanceManager;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectScoreDao;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectAPIManager;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import com.redxun.xcmgTdm.core.service.TdmIIRequestService;
import com.redxun.zlgjNPI.core.manager.ZlgjWTService;
import com.redxun.zlgjNPI.core.manager.ZlgjjysbService;

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
 * @since 2020/6/2 17:40
 */
@Controller
@RequestMapping("/rdmCommon/api/")
public class RdmApiController {
    private static final Logger logger = LoggerFactory.getLogger(RdmApiController.class);
    public static final String SAVE_DRAFT = "doSaveDraft";// 保存草稿
    public static final String START_PROCESS = "doStartProcess";// 启动流程
    @Autowired
    private MaterielApiService materielApiService;
    @Autowired
    private PdmApiManager pdmApiManager;
    @Autowired
    private ExportPartsAtlasService exportPartsAtlasService;
    @Autowired
    private ZlgjWTService zlgjWTService;
    @Autowired
    private ZlgjjysbService zlgjjysbService;
    @Autowired
    private WgjzlsjService wgjzlsjService;
    @Autowired
    private GsClhbService gsClhbService;
    @Autowired
    private ClhbService clhbService;
    @Autowired
    private XcmgProjectAPIManager xcmgProjectAPIManager;
    @Autowired
    private CkddService ckddService;
    @Autowired
    private SeGeneralKanBanNewService seGeneralKanBanNewService;
    @Autowired
    private CeinfoService ceinfoService;
    @Resource
    private GsClhbService getGsClhbService;
    @Resource
    private OAFinanceManager oaFinanceManager;
    @Autowired
    private TdmIIRequestService tdmIIRequestService;
    @Autowired
    private MaterialApplyService materialApplyService;
    @Autowired
    private GsClhbDao gsClhbDao;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private XcmgProjectScoreDao xcmgProjectScoreDao;
    @Autowired
    private ProductSpectrumService productSpectrumService;

    /*@RequestMapping(value = "reComputeEndStageProjectScore", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject test(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        String projectId = RequestUtil.getString(request, "projectId", "");
        if (StringUtils.isBlank(projectId)) {
            result.put("code", HttpStatus.SC_BAD_REQUEST);
            result.put("message", "projectId为空");
            return result;
        }
    
        JSONObject formDataJson = xcmgProjectManager.getXcmgProject(projectId);
        formDataJson.put("SUB_project_memberInfo",formDataJson.getObject("xcmgProjectMembers",List.class));
        formDataJson.put("SUB_project_plan",formDataJson.getObject("xcmgProjectPlans",List.class));
        // 刷新项目计划表
        String lastStageId = formDataJson.getString("currentStageId");
        String currentLocalTime = XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd");
        String currentUtcTime = XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss");
        currentUtcTime = DateUtil.formatDate(
                DateUtil.addHour(DateUtil.parseDate(currentLocalTime, "yyyy-MM-dd"), -8), "yyyy-MM-dd HH:mm:ss");
        Map<String, Object> params = new HashMap<>();
        if (org.apache.commons.lang.StringUtils.isNotBlank(lastStageId)) {
            params.clear();
            params.put("actualEndTime", currentUtcTime);
            params.put("stageId", lastStageId);
            params.put("projectId", projectId);
            xcmgProjectOtherDao.updateProjectPlanTime(params);
    
            // 计算各个阶段的得分
            xcmgProjectManager.processProjectScore(formDataJson, true);
            // 计算每个成员最终的角色系数并写入成员表
            Map<String, Object> paramUpdateRatio = new HashMap<>();
            paramUpdateRatio.put("projectId", projectId);
            List<JSONObject> ratioData = xcmgProjectScoreDao.queryMemFinalRatio(paramUpdateRatio);
            if (ratioData != null && !ratioData.isEmpty()) {
                paramUpdateRatio.clear();
                paramUpdateRatio.put("dataList", ratioData);
                xcmgProjectScoreDao.updateMemFinalRatio(paramUpdateRatio);
            }
        } else {
            logger.error("项目最后阶段ID为空，projectId =" + projectId);
        }
        return result;
    }*/

    /**
     * MPM平台创建物料扩充申请单
     *
     * @param request
     * @param postData
     * @param response
     * @return
     */
    @RequestMapping(value = "mpm/matExtendApplyCreate", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject mpmMatExtendApplyCreate(HttpServletRequest request, @RequestBody String postData,
                                              HttpServletResponse response) {
        postData = new String(postData.getBytes(), Charset.forName("utf-8"));
        JSONObject result = new JSONObject();
        result.put("success", true);
        result.put("messages", new JSONArray());
        if (StringUtils.isBlank(postData)) {
            logger.error("requestBody is blank");
            result.put("success", false);
            result.getJSONArray("messages").add("传输的消息内容为空！");
            return result;
        }
        materielApiService.mpmCreateApply(result, postData);
        return result;
    }

    /**
     * PDM获取负责或者参与的运行中的科技项目列表
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "pdm/getProjectList", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject pdmGetProjectList(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        String pdmUserNo = RequestUtil.getString(request, "userNo", "");
        if (StringUtils.isBlank(pdmUserNo)) {
            result.put("code", HttpStatus.SC_BAD_REQUEST);
            result.put("message", "PDM账户名为空");
            return result;
        }

        pdmApiManager.pdmGetProjectList(pdmUserNo, result);
        return result;
    }

    /**
     * 从CRM纷享销客接收出口产品零件图册需求通知单及需求明细
     *
     * @param request
     * @param postData
     * @param response
     * @return
     */
    @RequestMapping(value = "crm/partsAtlasDemand", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject partsAtlasDemand(HttpServletRequest request, @RequestBody String postData,
                                       HttpServletResponse response) throws UnsupportedEncodingException {
        postData = new String(postData.getBytes("utf-8"), Charset.forName("utf-8"));
        JSONObject result = new JSONObject();
        result.put("success", true);
        if (StringUtils.isBlank(postData)) {
            logger.error("requestBody is blank");
            result.put("success", false);
            result.put("message", "传输的消息内容为空！");
            return result;
        }
        try {
            exportPartsAtlasService.createPartsAtlasDemandApply(result, postData, false);
            return result;
        } catch (Exception e) {// 捕获操保手册需求流程的启动会以运行时错误体现出来
            logger.error("Exception in partsAtlasDemand", e);
            result.put("success", false);
            result.put("message", "系统异常！");
            return result;
        }
    }

    /**
     * 从CRM+接收出口产品零件图册需求通知单及需求明细
     *
     * @param request
     * @param postData
     * @param response
     * @return
     */
    @RequestMapping(value = "crm/partsAtlasDemandCRMPlus", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject partsAtlasDemandCRMPlus(HttpServletRequest request, @RequestBody String postData,
                                              HttpServletResponse response) throws UnsupportedEncodingException {
        postData = new String(postData.getBytes("utf-8"), Charset.forName("utf-8"));
        JSONObject result = new JSONObject();
        result.put("success", true);
        if (StringUtils.isBlank(postData)) {
            logger.error("requestBody is blank");
            result.put("success", false);
            result.put("message", "传输的消息内容为空！");
            return result;
        }
        try {
            exportPartsAtlasService.createPartsAtlasDemandApply(result, postData, true);
            return result;
        } catch (Exception e) {// 捕获操保手册需求流程的启动会以运行时错误体现出来
            logger.error("Exception in partsAtlasDemand", e);
            result.put("success", false);
            result.put("message", "系统异常！");
            return result;
        }
    }

    /**
     * 从MES接收出口产品零件图册整机分配信息（需求明细）
     *
     * @param request
     * @param postData
     * @param response
     * @return
     */
    @RequestMapping(value = "mes/partsAtlasDemandMachine", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject partsAtlasDemandMachine(HttpServletRequest request, @RequestBody String postData,
                                              HttpServletResponse response) throws UnsupportedEncodingException {
        postData = new String(postData.getBytes("utf-8"), Charset.forName("utf-8"));
        JSONObject result = new JSONObject();
        result.put("success", true);
        result.put("message", "");
        if (StringUtils.isBlank(postData)) {
            logger.error("requestBody is blank");
            result.put("success", false);
            result.put("messages", "传输的消息内容为空！");
            return result;
        }
        exportPartsAtlasService.createPartsAtlasDemandMachine(result, postData);
        return result;
    }

    /**
     * 从OA接收出口发运补发时间
     *
     * @param request
     * @param postData
     * @param response
     * @return
     */
    @RequestMapping("oa/manualFileReDispatch")
    @ResponseBody
    public JSONObject manualFileReDispatch(HttpServletRequest request, @RequestBody String postData,
                                           HttpServletResponse response) {
        postData = new String(postData.getBytes(), Charset.forName("utf-8"));
        JSONObject result = new JSONObject();
        // mh 接口需要返回 Data:{isok errormsg} 和 Message 字段`
        int isok = 1;
        String errorMsg = null;
        String message = null;
        JSONObject data = new JSONObject();

        if (StringUtils.isBlank(postData)) {
            logger.error("requestBody is blank");
            isok = 0;
            errorMsg = "传输的消息内容为空";
            message = "调用失败！";
            data.put("isok", isok);
            data.put("errorMsg", errorMsg);
            result.put("Data", data);
            result.put("Message", message);

            return result;
        }
        exportPartsAtlasService.reDispatchApply(result, postData);
        logger.error("消息体：" + postData);
        return result;
    }

    /**
     * 从CRM纷享销客接收出口产品零件图册发运通知单及发运明细
     *
     * @param request
     * @param postData
     * @param response
     * @return
     */
    @RequestMapping(value = "crm/partsAtlasDispatch", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject partsAtlasDispatch(HttpServletRequest request, @RequestBody String postData,
                                         HttpServletResponse response) {
        postData = new String(postData.getBytes(), Charset.forName("utf-8"));
        JSONObject result = new JSONObject();
        result.put("success", true);
        if (StringUtils.isBlank(postData)) {
            logger.error("requestBody is blank");
            result.put("success", false);
            result.put("message", "传输的消息内容为空！");
            return result;
        }
        exportPartsAtlasService.createPartsAtlasDispatchApply(result, postData);
        return result;
    }

    /**
     * 从MES接收出口产品零件图册整机分配信息（发运明细）
     *
     * @param request
     * @param postData
     * @param response
     * @return
     */
    @RequestMapping(value = "mes/partsAtlasDispatchMachine", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject partsAtlasDispatchMachine(HttpServletRequest request, @RequestBody String postData,
                                                HttpServletResponse response) {
        postData = new String(postData.getBytes(), Charset.forName("utf-8"));
        JSONObject result = new JSONObject();
        result.put("success", true);
        result.put("message", "");
        if (StringUtils.isBlank(postData)) {
            logger.error("requestBody is blank");
            result.put("success", false);
            result.put("messages", "传输的消息内容为空！");
            return result;
        }
        exportPartsAtlasService.createPartsAtlasDispatchMachine(result, postData);
        return result;
    }

    /**
     * 从TMS接收出口产品零件图册整机发运信息（发运明细）
     *
     * @param request
     * @param postData
     * @param response
     * @return
     */
    @RequestMapping(value = "tms/partsAtlasDispatchTime", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject partsAtlasDispatchTime(HttpServletRequest request, @RequestBody String postData,
                                             HttpServletResponse response) {
        postData = new String(postData.getBytes(), Charset.forName("utf-8"));
        JSONObject result = new JSONObject();
        result.put("success", true);
        result.put("message", "");
        if (StringUtils.isBlank(postData)) {
            logger.error("requestBody is blank");
            result.put("success", false);
            result.put("messages", "传输的消息内容为空！");
            return result;
        }
        exportPartsAtlasService.batchUpdatePartsAtlasDispatchTime(result, postData);
        return result;
    }

    /**
     * TDMII创建新品试制质量改进
     *
     * @param request
     * @param postData
     * @param response
     * @return
     */
    @RequestMapping(value = "tdmII/createZlgj", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject tdmIICreateZlgj(HttpServletRequest request, @RequestBody String postData,
                                      HttpServletResponse response) {
        postData = new String(postData.getBytes(), Charset.forName("utf-8"));
        JSONObject result = new JSONObject();
        result.put("success", true);
        result.put("message", "创建质量改进建议申报流程成功！");
        if (StringUtils.isBlank(postData)) {
            logger.error("requestBody is blank");
            result.put("success", false);
            result.put("message", "传输的消息内容为空！");
            return result;
        }
        try {
            tdmIIRequestService.createZlgjFormTdmII(result, postData, START_PROCESS);
            return result;
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "操作失败，系统异常！错误信息：" + e.getMessage());
            return result;
        }
    }

    /**
     * TDMII获取RDM测试预留申请列表
     *
     * @param request
     * @param postData
     * @param response
     * @return
     */
    @RequestMapping(value = "tdmII/getMaterialApplyListWithItems", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject getMaterialApplyListWithItems(HttpServletRequest request, @RequestBody String postData,
                                                    HttpServletResponse response) {
        postData = new String(postData.getBytes(), Charset.forName("utf-8"));
        JSONObject result = new JSONObject();
        result.put("success", true);
        result.put("message", "获取RDM测试预留申请列表成功！");
        if (StringUtils.isBlank(postData)) {
            logger.error("requestBody is blank");
            result.put("success", false);
            result.put("message", "传输的消息内容为空！");
            return result;
        }
        try {
            materialApplyService.getMaterialApplyListWithItems(result, postData);
            return result;
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "操作失败，系统异常！错误信息：" + e.getMessage());
            return result;
        }
    }

    /**
     * 新品试制平台获取产品型谱信息 todo
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "tdmII/getProductTypeSpectrum", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject getProductTypeSpectrum(HttpServletRequest request, @RequestBody String postData,
                                             HttpServletResponse response) throws Exception {
        postData = new String(postData.getBytes(), Charset.forName("utf-8"));
        JSONObject result = new JSONObject();
        try {
            JSONObject paramJson = JSONObject.parseObject(postData);
            return xcmgProjectAPIManager.getProductModelList(paramJson);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "操作失败，系统异常！错误信息：" + e.getMessage());
            return result;
        }
    }

    /**
     * 从徐工e修APP接收质量改进建议的申报
     *
     * @param request
     * @param postData
     * @param response
     * @return
     */
    @RequestMapping(value = "crm/zlgjjy", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject zlgjjy(HttpServletRequest request, @RequestBody String postData, HttpServletResponse response)
            throws Exception {
        postData = new String(postData.getBytes(), Charset.forName("utf-8"));
        JSONObject result = new JSONObject();
        result.put("success", true);
        result.put("message", "创建质量改进建议申报流程成功！");
        if (StringUtils.isBlank(postData)) {
            logger.error("requestBody is blank");
            result.put("success", false);
            result.put("message", "传输的消息内容为空！");
            return result;
        }
        try {
            zlgjjysbService.createZlgjjyFormOutSystem(result, postData, SAVE_DRAFT);
            return result;
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "操作失败，系统异常！错误信息：" + e.getMessage());
            return result;
        }
    }

    /**
     * 从GSS接收发运的PIN码增量
     *
     * @param request
     * @param postData
     * @param response
     * @return
     */
    @RequestMapping(value = "gss/shipmentPinsIncrement", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject shipmentPinsIncrement(HttpServletRequest request, @RequestBody String postData,
                                            HttpServletResponse response) throws Exception {
        postData = new String(postData.getBytes("utf-8"), Charset.forName("utf-8"));
        JSONObject result = new JSONObject();
        result.put("success", true);
        result.put("message", "接收发运的PIN码增量成功！");
        if (StringUtils.isBlank(postData)) {
            logger.error("requestBody is blank");
            result.put("success", false);
            result.put("message", "传输的消息内容为空！");
            return result;
        }
        try {
            seGeneralKanBanNewService.doInsertGssShipmentCache(result, postData, null);
            return result;
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "GSS发运的PIN码增量传输失败：" + e.getMessage());
            return result;
        }
    }

    /**
     * 结构化文档，返回质量问题改进的单号【更改来源】
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "ggly", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject ggly(HttpServletRequest request, HttpServletResponse response) {
        String searchValue = RequestUtil.getString(request, "searchValue", "");
        JSONObject result = new JSONObject();
        result.put("success", true);
        result.put("message", "");
        if (StringUtils.isBlank(searchValue)) {
            logger.error("requestBody is blank");
            result.put("success", false);
            result.put("messages", "传输的消息内容为空！");
            return result;
        }
        zlgjWTService.getApiList(result, searchValue);
        return result;
    }

    /**
     * 结构化文档，返回质产品型号
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "cpxh", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject cpxh(HttpServletRequest request, HttpServletResponse response) {
        String searchValue = RequestUtil.getString(request, "searchValue", "");
        JSONObject result = new JSONObject();
        result.put("success", true);
        result.put("message", "");
        if (StringUtils.isBlank(searchValue)) {
            logger.error("requestBody is blank");
            result.put("success", false);
            result.put("messages", "传输的消息内容为空！");
            return result;
        }
        xcmgProjectAPIManager.getCpxhApiList(result, searchValue);
        return result;
    }

    /**
     * MES获取产品型谱信息
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "mes/product", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject getProductInfo(HttpServletRequest request, @RequestBody String postData,
                                     HttpServletResponse response) throws Exception {
        postData = new String(postData.getBytes(), Charset.forName("utf-8"));
        List<JSONObject> list = new ArrayList<>();
        JSONObject resultJson = new JSONObject();
        try {
            JSONObject paramJson = JSONObject.parseObject(postData);
            resultJson = xcmgProjectAPIManager.getProductModelList(paramJson);
        } catch (Exception e) {
            return ResultUtil.result(false, "获取识别", e);
        }
        return resultJson;
    }

    /**
     * 结构化文档，返回环保信息单号
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "hbxx", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject hbxx(HttpServletRequest request, HttpServletResponse response) {
        String searchValue = RequestUtil.getString(request, "searchValue", "");
        JSONObject result = new JSONObject();
        result.put("success", true);
        result.put("message", "");
        if (StringUtils.isBlank(searchValue)) {
            logger.error("requestBody is blank");
            result.put("success", false);
            result.put("messages", "传输的消息内容为空！");
            return result;
        }
        clhbService.getApiList(result, searchValue);
        Boolean res1 = result.getBoolean("success");
        // 目前把国三国四的拼在一起了，字段不相同
        gsClhbService.getApiList(result, searchValue);
        Boolean res2 = result.getBoolean("success");
        if (res1 || res2) {
            result.put("success", true);
            result.put("message", "");
        }
        return result;
    }

    /**
     * 结构化文档，返回外购件资料收集中的单据编号
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "wgjzlsj", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject wgjzlsj(HttpServletRequest request, HttpServletResponse response) {
        String searchValue = RequestUtil.getString(request, "searchValue", "");
        JSONObject result = new JSONObject();
        result.put("success", true);
        result.put("message", "");
        if (StringUtils.isBlank(searchValue)) {
            logger.error("requestBody is blank");
            result.put("success", false);
            result.put("messages", "传输的消息内容为空！");
            return result;
        }
        wgjzlsjService.getApiList(result, searchValue);
        return result;
    }

    /**
     * 结构化文档，返回特殊订单需求的rdm特殊订单申请号
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "rdmSpecialddNumber", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject rdmSpecialDDNumber(HttpServletRequest request, HttpServletResponse response) {
        String searchValue = RequestUtil.getString(request, "searchValue", "");
        JSONObject result = new JSONObject();
        result.put("success", true);
        result.put("message", "");
        if (StringUtils.isBlank(searchValue)) {
            logger.error("requestBody is blank");
            result.put("success", false);
            result.put("messages", "传输的消息内容为空！");
            return result;
        }
        ckddService.getApiList(result, searchValue);
        return result;
    }

    /**
     * 结构化文档，返回科技项目中的财务订单号
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "cwddh", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject cwddh(HttpServletRequest request, HttpServletResponse response) {
        String searchValue = RequestUtil.getString(request, "searchValue", "");
        JSONObject result = new JSONObject();
        result.put("success", true);
        result.put("message", "");
        if (StringUtils.isBlank(searchValue)) {
            logger.error("searchValue is blank");
            result.put("success", false);
            result.put("messages", "传输的搜索条件为空！");
            return result;
        }
        xcmgProjectAPIManager.getApiList(result, searchValue);
        return result;
    }

    /**
     * EC自声明
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "queryCeinfoByOnlyNum", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject queryCeinfoByOnlyNum(HttpServletRequest request, HttpServletResponse response) {
        String searchValue = RequestUtil.getString(request, "onlyNum", "");
        JSONObject result = new JSONObject();
        result.put("success", true);
        result.put("message", "操作成功");
        result.put("code", "1");
        if (StringUtils.isBlank(searchValue)) {
            logger.error("requestBody is blank");
            result.put("success", false);
            result.put("message", "传输的消息内容为空！");
            result.put("code", "0");
            return result;
        }
        ceinfoService.queryCeinfoByOnlyNum(result, searchValue);
        return result;
    }

    /**
     * mes获取环保信息公开信息
     *
     * @param request
     * @param postData
     * @param response
     * @return
     */
    @RequestMapping(value = "mes/environment", method = RequestMethod.POST)
    @ResponseBody
    public List<JSONObject> getEnvironmentInfo(HttpServletRequest request, @RequestBody String postData,
                                               HttpServletResponse response) throws Exception {
        postData = new String(postData.getBytes(), Charset.forName("utf-8"));
        JSONObject result = new JSONObject();
        List<JSONObject> list = new ArrayList<>();
        try {
            if (StringUtils.isBlank(postData)) {
                logger.error("requestBody is blank");
                result.put("code", 500);
                result.put("messages", "请求内容不能为空！");
                return list;
            }
            list = getGsClhbService.getEnvironmentByMaterial(postData);
            return list;
        } catch (Exception e) {
            result.put("code", 500);
            result.put("messages", "请求异常！");
            return list;
        }
    }

    /**
     * 从OA接收机器人节点传入财务成本资料申请流程审批节点数据
     *
     * @param request
     * @param postData
     * @param response
     * @return
     */
    @RequestMapping(value = "oa/oaFinance", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject oaFinance(HttpServletRequest request, @RequestBody String postData,
                                HttpServletResponse response) {
        postData = new String(postData.getBytes(), Charset.forName("utf-8"));
        JSONObject result = new JSONObject();
        result.put("success", true);
        result.put("message", "");
        if (StringUtils.isBlank(postData)) {
            result.put("success", false);
            result.put("messages", "传输的消息内容为空！");
            return result;
        }
        // 新增或根据oa流程id号更新基础表单信息，oa机器人节点触发接口
        oaFinanceManager.createOAFinanceBasic(result, postData);
        return result;
    }

    /**
     * RDM心跳测试
     *
     * @param request
     * @param response return
     */
    @RequestMapping(value = "heartTest", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject heartTest(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        result.put("success", true);
        result.put("message", "rdm normal");
        result.put("code", "1");
        return result;
    }

    /**
     * 查询国四环保公开中产品相关的数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "gsEnvProductInfo", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject gsEnvProductInfo(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        result.put("success", true);
        result.put("message", "查询成功");
        JSONObject param = new JSONObject();
        param.put("status", "SUCCESS_END");
        List<JSONObject> dataList = gsClhbDao.gsEnvProductInfo(param);
        result.put("data", dataList);
        return result;
    }

    /**
     * 用于外部查询产品型谱的API，支持传递筛选参数、排序、分页。 请求体的格式
     * { "sortField":"", "sortOrder":"asc", "pageIndex":"0", "pageSize":"20","doPage":true,
     * "filter":[
     * { "name": "materialCode", "value": "" },
     * { "name": "designModel", "value": "" },
     * { "name":"departName", "value": "" }
     * ]
     * }
     *
     * @param request
     * @param postData
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "queryDesignSpectrum", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject queryDesignSpectrum(HttpServletRequest request, @RequestBody String postData,
                                          HttpServletResponse response) throws Exception {
        JSONObject resultJson = new JSONObject();
        resultJson.put("success", true);
        resultJson.put("message", "产品型谱数据查询成功");
        // 结果返回列表数据
        resultJson.put("data", new ArrayList<>());
        resultJson.put("total", 0);
        try {
            // 解析postData
            String postStr = new String(postData.getBytes(), Charset.forName("utf-8"));
            JSONObject paramJson = JSONObject.parseObject(postStr);
            productSpectrumService.queryDesignSpectrumForApi(paramJson, resultJson);
        } catch (Exception e) {
            logger.error("系统异常", e);
            resultJson.put("success", false);
            resultJson.put("message", "系统异常");
        }
        return resultJson;
    }
}
