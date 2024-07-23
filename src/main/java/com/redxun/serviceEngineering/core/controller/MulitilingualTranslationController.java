package com.redxun.serviceEngineering.core.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.ImmutableMap;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.serviceEngineering.core.dao.MulitilingualTranslationDao;
import com.redxun.serviceEngineering.core.service.MulitilingualTranslationService;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 应用模块名称
 * <p>
 * 代码描述
 * <p>
 * Copyright: Copyright (C) 2021 XXX, Inc. All rights reserved.
 * <p>
 * Company: 徐工挖掘机械有限公司
 * <p>
 *
 * @author QY
 * @since 2021/05/07 10:43
 */
@RestController
@RequestMapping("/yfgj/core/mulitilingualTranslation/")
public class MulitilingualTranslationController {
    private Logger logger = LogManager.getLogger(MulitilingualTranslationController.class);
    @Resource
    private MulitilingualTranslationService mulitilingualTranslationService;

    @Autowired
    private MulitilingualTranslationDao mulitilingualTranslationDao;

    @Autowired
    private RdmZhglUtil rdmZhglUtil;

    @RequestMapping("ljtcListPage")
    public ModelAndView ljtcListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/ljtcTranslationList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        Map<String, Object> params = new HashMap<>();
        params.put("userId", ContextUtil.getCurrentUserId());
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        // 返回当前登录人角色信息
        /*List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params);
        JSONArray userRolesJsonArray = XcmgProjectUtil.convertListMap2JsonArrObject(currentUserRoles);
        mv.addObject("currentUserRoles", userRolesJsonArray);*/

        //是否是后市场多语言翻译人员
        boolean isDYYFY = rdmZhglUtil.judgeIsPointRoleUser(ContextUtil.getCurrentUserId(), "后市场多语言翻译人员");
        mv.addObject("isDYYFY", isDYYFY);

        return mv;
    }

    // 标准新增和编辑页面
    @RequestMapping("editLjtc")
    public ModelAndView editLjtc(HttpServletRequest request, HttpServletResponse response) {

        String jspPath = "serviceEngineering/core/ljtcTranslationEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String chineseId = RequestUtil.getString(request, "chineseId");
        mv.addObject("chineseId", chineseId);
        JSONObject ljtcObj = new JSONObject();
        //查询信息
        if (StringUtils.isNotBlank(chineseId)) {
            Map<String, Object> ljtcInfo = mulitilingualTranslationService.queryLjtcById(chineseId);
            ljtcObj = XcmgProjectUtil.convertMap2JsonObject(ljtcInfo);
        }

        mv.addObject("ljtcObj", ljtcObj);
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());

        return mv;
    }

    @RequestMapping("getLjtcList")
    @ResponseBody
    public JsonPageResult<?> getLjtcList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mulitilingualTranslationService.getLjtcList(request, response, true);
    }


    @RequestMapping("saveLjtc")
    @ResponseBody
    public JsonResult saveLjtc(HttpServletRequest request, @RequestBody JSONObject formDataJson,
                                 HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "保存成功");
        try {
            if (formDataJson == null || formDataJson.isEmpty()) {
                logger.warn("formData is blank");
                result.setSuccess(false);
                result.setMessage("requestBody is blank");
                return result;
            }
            if (StringUtils.isBlank(formDataJson.getString("chineseId"))) {
                mulitilingualTranslationService.addLjtc(formDataJson);
            } else {
                mulitilingualTranslationService.updateLjtc(formDataJson);
            }
        } catch (Exception e) {
            logger.error("Exception in saveDemand");
            result.setSuccess(false);
            result.setMessage("Exception in saveDemand");
            return result;
        }
        result.setData(formDataJson.getString("id"));
        return result;
    }

    @RequestMapping("deleteLjtc")
    @ResponseBody
    public JsonResult removeLjtc(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            List<String> ids = Arrays.asList(uIdStr.split(","));
            Map<String, Object> param = new HashMap<>();
            param.put("ids", ids);
            mulitilingualTranslationDao.deleteLjtc(param);
        } catch (Exception e) {
            logger.error("Exception in removeLjtc", e);
            return new JsonResult(false, e.getMessage());
        }

        return new JsonResult(true, "操作成功");
    }

    @RequestMapping("exportLjtcList")
    public void exportLjtcList(HttpServletRequest request, HttpServletResponse response) {
        mulitilingualTranslationService.exportLjtcList(request, response);
    }

    @RequestMapping("exportLjtcListAll")
    public void exportLjtcListAll(HttpServletRequest request, HttpServletResponse response) {
        mulitilingualTranslationService.exportLjtcListAll(request, response);
    }
    @RequestMapping("getLjtcExist")
    @ResponseBody
    public Map<String, Object> getLjtcExist(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        Map<String, Object> result = new HashMap<>();
        String materialCode = RequestUtil.getString(request, "materialCode");
        String chineseId = RequestUtil.getString(request, "chineseId");
        if (StringUtils.isBlank(materialCode)) {
            logger.error(" originChinese is blank");
            result.put("result", "false");
            return result;
        }
        boolean isYbExist = mulitilingualTranslationService.getLjtcExist(materialCode, chineseId);
        if (isYbExist) {
            result.put("result", "true");
        } else {
            result.put("result", "false");
        }

        return result;
    }

    //仪表

    @RequestMapping("ybListPage")
    public ModelAndView ybListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/ybTranslationList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        Map<String, Object> params = new HashMap<>();
        params.put("userId", ContextUtil.getCurrentUserId());
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        // 返回当前登录人角色信息
        /*List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params);
        JSONArray userRolesJsonArray = XcmgProjectUtil.convertListMap2JsonArrObject(currentUserRoles);
        mv.addObject("currentUserRoles", userRolesJsonArray);*/

        //是否是后市场多语言翻译人员
        boolean isDYYFY = rdmZhglUtil.judgeIsPointRoleUser(ContextUtil.getCurrentUserId(), "后市场多语言翻译人员");
        mv.addObject("isDYYFY", isDYYFY);

        return mv;
    }

    // 标准新增和编辑页面
    @RequestMapping("editYb")
    public ModelAndView editYb(HttpServletRequest request, HttpServletResponse response) {

        String jspPath = "serviceEngineering/core/ybTranslationEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String chineseId = RequestUtil.getString(request, "chineseId");
        mv.addObject("chineseId", chineseId);
        JSONObject ybObj = new JSONObject();
        //查询信息
        if (StringUtils.isNotBlank(chineseId)) {
            Map<String, Object> ybInfo = mulitilingualTranslationService.queryYbById(chineseId);
            ybObj = XcmgProjectUtil.convertMap2JsonObject(ybInfo);
        }

        mv.addObject("ybObj", ybObj);
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());

        return mv;
    }

    @RequestMapping("getYbList")
    @ResponseBody
    public JsonPageResult<?> getYbList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mulitilingualTranslationService.getYbList(request, response, true);
    }

    @RequestMapping("saveYb")
    @ResponseBody
    public JsonResult saveYb(HttpServletRequest request, @RequestBody JSONObject formDataJson,
                               HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "保存成功");
        try {
            if (formDataJson == null || formDataJson.isEmpty()) {
                logger.warn("formData is blank");
                result.setSuccess(false);
                result.setMessage("requestBody is blank");
                return result;
            }
            if (StringUtils.isBlank(formDataJson.getString("chineseId"))) {
                mulitilingualTranslationService.addYb(formDataJson);
            } else {
                mulitilingualTranslationService.updateYb(formDataJson);
            }
        } catch (Exception e) {
            logger.error("Exception in saveDemand");
            result.setSuccess(false);
            result.setMessage("Exception in saveDemand");
            return result;
        }
        result.setData(formDataJson.getString("id"));
        return result;
    }

    @RequestMapping("deleteYb")
    @ResponseBody
    public JsonResult removeYb(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            List<String> ids = Arrays.asList(uIdStr.split(","));
            Map<String, Object> param = new HashMap<>();
            param.put("ids", ids);
            mulitilingualTranslationDao.deleteYb(param);
        } catch (Exception e) {
            logger.error("Exception in removeYb", e);
            return new JsonResult(false, e.getMessage());
        }

        return new JsonResult(true, "操作成功");
    }

    @RequestMapping("exportYbList")
    public void exportYbList(HttpServletRequest request, HttpServletResponse response) {
        mulitilingualTranslationService.exportYbList(request, response);
    }
    @RequestMapping("exportYbAllList")
    public void exportYbAllList(HttpServletRequest request, HttpServletResponse response) {
        mulitilingualTranslationService.exportYbAllList(request, response);
    }

    @RequestMapping("importExcel")
    @ResponseBody
    public JSONObject importExcel(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        mulitilingualTranslationService.importMaterialYb(result, request);
        return result;
    }
    @RequestMapping("importExcelljtc")
    @ResponseBody
    public JSONObject importExcelljtc(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        mulitilingualTranslationService.importMaterialljtc(result, request);
        return result;
    }

    /**
     * 仪表模板下载
     */
    @RequestMapping("/importTemplateDownload")
    public ResponseEntity<byte[]> importTemplateDownload(HttpServletRequest request, HttpServletResponse response) {
        return mulitilingualTranslationService.importTemplateDownload();
    }
    /**
     * 零件图册模板下载
     */
    @RequestMapping("/importTemplateDownloadLjtc")
    public ResponseEntity<byte[]> importTemplateDownloadLjtc(HttpServletRequest request, HttpServletResponse response) {
        return mulitilingualTranslationService.importTemplateDownloadLjtc();
    }

    @RequestMapping("getYbExist")
    @ResponseBody
    public Map<String, Object> getYbExist(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        Map<String, Object> result = new HashMap<>();
        String originChinese = RequestUtil.getString(request, "originChinese");
        String chineseId = RequestUtil.getString(request, "chineseId");
        if (StringUtils.isBlank(originChinese)) {
            logger.error(" originChinese is blank");
            result.put("result", "false");
            return result;
        }
        boolean isYbExist = mulitilingualTranslationService.getYbExist(originChinese, chineseId);
        if (isYbExist) {
            result.put("result", "true");
        } else {
            result.put("result", "false");
        }

        return result;
    }

    //查询推荐中文的英文和小语种翻译
    @RequestMapping("getRecommend")
    @ResponseBody
    public JSONObject getRecommend(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String chineseName = RequestUtil.getString(request, "chineseName", "");
        String multilingualSign = RequestUtil.getString(request, "multilingualSign", "");

        return mulitilingualTranslationService.getRecommend(chineseName,multilingualSign);
    }

}
