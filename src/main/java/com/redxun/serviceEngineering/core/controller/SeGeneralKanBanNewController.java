package com.redxun.serviceEngineering.core.controller;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.serviceEngineering.core.service.SeGeneralKanBanNewService;
import com.redxun.sys.core.manager.SysDicManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/serviceEngineering/core/seGeneralKanbanNew")
public class SeGeneralKanBanNewController {
    private static final Logger logger = LoggerFactory.getLogger(SeGeneralKanBanNewController.class);
    @Autowired
    private SeGeneralKanBanNewService seGeneralKanBanNewService;
    @Autowired
    private SysDicManager sysDicManager;

    @RequestMapping("refreshCacheGenSit")
    @ResponseBody
    public JsonResult refreshCacheGenSit(HttpServletRequest request, HttpServletResponse response) {
        try {
            return seGeneralKanBanNewService.refreshCacheGenSitu();
        } catch (Exception e) {
            logger.error("SeGeneralKanBanNewController的refreshCacheGenSit出现运行时错误:" + e.getMessage());
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("refreshCacheAtDocAcpRate")
    @ResponseBody
    public JsonResult refreshCacheAtDocAcpRate(HttpServletRequest request, HttpServletResponse response) {
        try {
            String signYear = RequestUtil.getString(request, "signYear");
            return seGeneralKanBanNewService.refreshCacheAtDocAcpRate(signYear);
        } catch (Exception e) {
            logger.error("SeGeneralKanBanNewController的refreshCacheAtDocAcpRate出现运行时错误:" + e.getMessage());
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("refreshCacheAtDocReDisRate")
    @ResponseBody
    public JsonResult refreshCacheAtDocReDisRate(HttpServletRequest request, HttpServletResponse response) {
        try {
            return seGeneralKanBanNewService.refreshCacheAtDocReDisRate();
        } catch (Exception e) {
            logger.error("SeGeneralKanBanNewController的refreshCacheAtDocReDisRate出现运行时错误:" + e.getMessage());
            return new JsonResult(false, e.getMessage());
        }
    }

    //..
    @RequestMapping("seGeneralKanbanNewPage")
    public ModelAndView seGeneralKanbanPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/seGeneralKanbanNew.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String seKanbanAdmin =
                sysDicManager.getBySysTreeKeyAndDicKey("serviceEngineeringGroups", "seKanbanAdmin").getValue();
        mv.addObject("seKanbanAdmin", seKanbanAdmin);
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        return mv;
    }

    //..数量类统计-通用
    @RequestMapping("getAmount")
    @ResponseBody
    public JSONObject getAmount(HttpServletRequest request, HttpServletResponse response,
                                @RequestBody String postDataStr) {
        return seGeneralKanBanNewService.getAmount(request, response, postDataStr);
    }

    //..decoCompletion:装修手册需求完成情况
    @RequestMapping("getDecoCompletionData")
    @ResponseBody
    public JSONObject getDecoCompletionData(HttpServletRequest request, HttpServletResponse response,
                                            @RequestBody String postDataStr) {
        return seGeneralKanBanNewService.getDecoCompletionData(request, response, postDataStr);
    }

    //..maintTransRate:中文操保手册英文译本翻译率
    @RequestMapping("getMaintTransRateData")
    @ResponseBody
    public List<JSONObject> getMaintTransRateData(HttpServletRequest request, HttpServletResponse response,
                                                  @RequestBody String postDataStr) {
        return seGeneralKanBanNewService.getMaintTransRateData(request, response, postDataStr);
    }

    //..manualMinorLanguage:小语种手册数量
    @RequestMapping("getMaintManualMinorLangData")//操保
    @ResponseBody
    public List<JSONObject> getMaintManualMinorLangData(HttpServletRequest request, HttpServletResponse response,
                                                        @RequestBody String postDataStr) {
        return seGeneralKanBanNewService.getMaintManualMinorLangData(request, response, postDataStr);
    }

    @RequestMapping("getDecoManualMinorLangData")//装修
    @ResponseBody
    public List<JSONObject> getDecoManualMinorLangData(HttpServletRequest request, HttpServletResponse response,
                                                       @RequestBody String postDataStr) {
        return seGeneralKanBanNewService.getDecoManualMinorLangData(request, response, postDataStr);
    }

    //..generalSituation:总体情况
    @RequestMapping("getGeneralSituationData")
    @ResponseBody
    public JSONObject getGeneralSituationData(HttpServletRequest request, HttpServletResponse response,
                                              @RequestBody String postDataStr) {
        return seGeneralKanBanNewService.getGeneralSituationData(request, response, postDataStr);
    }

    //..exportAtDocAcpRate:出口产品随机文件完成率
    @RequestMapping("getExportAtDocAcpRateData")
    @ResponseBody
    public JSONObject getExportAtDocAcpRateData(HttpServletRequest request, HttpServletResponse response,
                                                @RequestBody String postDataStr) {
        JSONObject postDataJson = JSONObject.parseObject(postDataStr);
        String signYear = postDataJson.getString("signYear");
        if (Integer.parseInt(signYear) >= 2023) {//2023年以后用新算法
            return seGeneralKanBanNewService.getExportAtDocAcpRateData(request, response, postDataStr);
        } else {
            return seGeneralKanBanNewService.getExportAtDocAcpRateData_obsolete(request, response, postDataStr);
        }
    }

    //..exportAtDocReDisRate:出口产品随机文件补发完成率
    @RequestMapping("getPartsAtlasReDisRateData")//零件
    @ResponseBody
    public List<JSONObject> getPartsAtlasReDisRateData(HttpServletRequest request, HttpServletResponse response,
                                                       @RequestBody String postDataStr) {
        return seGeneralKanBanNewService.getPartsAtlasReDisRateData(request, response, postDataStr);
    }

    @RequestMapping("getMaintManualReDisRateData")//操保
    @ResponseBody
    public List<JSONObject> getMaintManualReDisRateData(HttpServletRequest request, HttpServletResponse response,
                                                        @RequestBody String postDataStr) {
        return seGeneralKanBanNewService.getMaintManualReDisRateData(request, response, postDataStr);
    }

    //..atDocAcpRate:随机文件完成率
    @RequestMapping("getAtDocAcpRateData")
    @ResponseBody
    public JSONObject getAtDocAcpRateData(HttpServletRequest request, HttpServletResponse response,
                                          @RequestBody String postDataStr) {
        return seGeneralKanBanNewService.getAtDocAcpRateData(request, response, postDataStr);
    }


    //..给gss缓存加一个查询页面
    @RequestMapping("gssShipmentCacheListPage")
    public ModelAndView gssShipmentCacheListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/gssShipmentCacheList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        return mv;
    }

    @RequestMapping("gssShipmentCacheListQuery")
    @ResponseBody
    public JsonPageResult<?> gssShipmentCacheListQuery(HttpServletRequest request, HttpServletResponse response) {
        return seGeneralKanBanNewService.gssShipmentCacheListQuery(request, response);
    }

    @RequestMapping("gssShipmentCacheExport")
    public void gssShipmentCacheExport(HttpServletRequest request, HttpServletResponse response) {
        seGeneralKanBanNewService.gssShipmentCacheExport(request, response);
    }
}


