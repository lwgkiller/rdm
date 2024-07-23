package com.redxun.rdmZhgl.core.controller;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmZhgl.core.dao.RyydDao;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.rdmZhgl.core.service.RyydService;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
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
import java.util.List;

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
 * @author liangchuanjiang
 * @since 2021/2/23 10:43
 */
@RestController
@RequestMapping("/zhgl/core/ryyd/")
public class RyydController {
    private Logger logger = LogManager.getLogger(RyydController.class);
    @Resource
    private RyydService ryydService;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private RyydDao ryydDao;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;


    @RequestMapping("ryydListPage")
    public ModelAndView ryydListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/ryydList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String currentUserId=ContextUtil.getCurrentUserId();
        JSONObject resMan = xcmgProjectManager.isCurrentUserDepRespman();
        boolean isJxzy = rdmZhglUtil.judgeIsPointRoleUser(currentUserId, RdmConst.JSZX_RZZY);
        mv.addObject("isJxzy", isJxzy);
        boolean isZbsj = rdmZhglUtil.judgeIsPointRoleUser(currentUserId, "技术支部书记");
        mv.addObject("isZbsj", isZbsj);
        boolean isFgld = rdmZhglUtil.judgeIsPointRoleUser(currentUserId, RdmConst.FGLD);
        mv.addObject("isFgld", isFgld);
        mv.addObject("currentUserId", currentUserId);
        mv.addObject("isDepRespMan", resMan.getString("isDepRespMan"));
        mv.addObject("currentUserMainDepName", resMan.getString("currentUserMainDepName"));

        return mv;
    }

    @RequestMapping("getRyydList")
    @ResponseBody
    public JsonPageResult<?> getRyydList(HttpServletRequest request, HttpServletResponse response) {
        return ryydService.queryRyyd(request, true);
    }

    @RequestMapping("deleteRyydDetail")
    @ResponseBody
    public JsonResult deleteRyydDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String ryydIds = RequestUtil.getString(request, "ids");
            String[] ryydIdsArr = ryydIds.split(",",-1);
            return ryydService.deleteRyydDetail(ryydIdsArr);
        } catch (Exception e) {
            logger.error("Exception in deleteRyyd", e);
            return new JsonResult(false, e.getMessage());
        }
    }
    @RequestMapping("ryydEditPage")
    public ModelAndView szhEditPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/ryydEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String action = RequestUtil.getString(request, "action", "");
        String zId = RequestUtil.getString(request, "zId", "");
        mv.addObject("zId", zId);
        mv.addObject("action", action);
        String currentUserId=ContextUtil.getCurrentUserId();
        JSONObject resMan = xcmgProjectManager.isCurrentUserDepRespman();
        mv.addObject("currentUserId", currentUserId);
        boolean isJxzy = rdmZhglUtil.judgeIsPointRoleUser(currentUserId, RdmConst.JSZX_RZZY);
        mv.addObject("isJxzy", isJxzy);
        boolean isZbsj = rdmZhglUtil.judgeIsPointRoleUser(currentUserId, "技术支部书记");
        mv.addObject("isZbsj", isZbsj);
        boolean isFgld = rdmZhglUtil.judgeIsPointRoleUser(currentUserId,  RdmConst.FGLD);
        mv.addObject("isFgld", isFgld);
        mv.addObject("currentUserId", currentUserId);
        mv.addObject("isDepRespMan", resMan.getString("isDepRespMan"));
        mv.addObject("currentUserMainDepName", resMan.getString("currentUserMainDepName"));
        return mv;
    }

    @RequestMapping("getRyydBaseInfo")
    @ResponseBody
    public JSONObject getSzhBaseInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject ryydObj = new JSONObject();
        String zId = RequestUtil.getString(request, "zId");
        if (StringUtils.isNotBlank(zId)) {
            ryydObj = ryydService.getRyydById(zId);
        }
        return ryydObj;
    }
    @RequestMapping("getRyydDetailList")
    @ResponseBody
    public List<JSONObject> getSzhDetailList(HttpServletRequest request, HttpServletResponse response) {
        return ryydService.getRyydDetailList(request);
    }
    @RequestMapping("saveRyydDetail")
    @ResponseBody
    public JsonResult saveZlgj(HttpServletRequest request, @RequestBody String zlgjStr,
                               HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "");
        if (StringUtils.isBlank(zlgjStr)) {
            logger.warn("requestBody is blank");
            result.setSuccess(false);
            result.setMessage("requestBody is blank");
            return result;
        }
        try {
            JSONObject formDataJson = JSONObject.parseObject(zlgjStr);
            if (formDataJson == null || formDataJson.isEmpty()) {
                logger.warn("formData is blank");
                result.setSuccess(false);
                result.setMessage("requestBody is blank");
                return result;
            }
            List<JSONObject> titleList = ryydDao.queryTitle();
            if (StringUtils.isBlank(formDataJson.getString("zId"))) {
                for (JSONObject title:titleList){
                    if(title.getString("title").substring(0,7).equals(formDataJson.getString("month").substring(0,7))){
                        logger.warn("请勿重复添加本月人员异动表");
                        result.setSuccess(false);
                        result.setMessage("请勿重复添加本月人员异动表");
                        return result;
                    }
                }
                ryydService.createRyyd(formDataJson);
            } else {
                ryydService.updateRyyd(formDataJson);
            }
            result.setData(formDataJson.getString("zId"));
        } catch (Exception e) {
            logger.error("Exception in save zlgj");
            result.setSuccess(false);
            result.setMessage("Exception in save zlgj");
            return result;
        }
        return result;
    }
    @RequestMapping("deleteRyyd")
    @ResponseBody
    public JsonResult deleteRyyd(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String zId = RequestUtil.getString(request, "id");
            return ryydService.deleteRyyd(zId);
        } catch (Exception e) {
            logger.error("Exception in deleteRyyd", e);
            return new JsonResult(false, e.getMessage());
        }
    }





    @RequestMapping("importTemplateDownload")
    public ResponseEntity<byte[]> importTemplateDownload(HttpServletRequest request, HttpServletResponse response) {
        return ryydService.importTemplateDownload();
    }

    /**
     * 批量导入
     * */
    @RequestMapping("importExcel")
    @ResponseBody
    public JSONObject importExcel(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        ryydService.importRyyd(result, request);
        return result;
    }


}
