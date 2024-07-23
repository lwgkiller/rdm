package com.redxun.wwrz.core.controller;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.StringUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.controller.GenericController;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.wwrz.core.dao.CeinfoDao;
import com.redxun.wwrz.core.service.CeinfoService;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 国家标准制修订管理
 */
@Controller
@RequestMapping("/wwrz/core/CE/")
public class CeinfoController extends GenericController {
    @Autowired
    private CeinfoService ceinfoService;
    @Autowired
    private CeinfoDao ceinfoDao;

    @RequestMapping("ceinfoListPage")
    public ModelAndView management(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "wwrz/core/ceinfoList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        Map<String, Object> params = new HashMap<>();
        params.put("userId", ContextUtil.getCurrentUserId());
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        // 角色信息
        String userId = ContextUtil.getCurrentUserId();
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserId", userId);
        return mv;
    }

    // 标准新增和编辑页面
    @RequestMapping("edit")
    public ModelAndView editCeinfo(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "wwrz/core/ceinfoEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        JSONObject autoData = new JSONObject();
        String action = RequestUtil.getString(request, "action");
        String ceinfoId = RequestUtil.getString(request, "ceinfoId");
        if ("auto".equals(action)) {
            String wwrzId = RequestUtil.getString(request, "wwrzId");
            List<JSONObject> ceList = ceinfoDao.queryAuto(wwrzId);
            if (ceList != null && !ceList.isEmpty()) {
                autoData.put("saleModel", ceList.get(0).getString("saleModel"));
                for (JSONObject oneCe : ceList) {
                    if ("CE噪声指令证书".equals(oneCe.getString("reportName"))
                            || "UKCA噪声指令证书".equals(oneCe.getString("reportName"))) {
                        autoData.put("zsNum", oneCe.getString("reportCode"));
                        autoData.put("zsId", oneCe.getString("id"));
                        autoData.put("zsStartDate", oneCe.getString("reportDate"));
                        autoData.put("zsEndDate", oneCe.getString("reportValidity"));
                    } else if ("CE机械指令证书".equals(oneCe.getString("reportName"))
                            || "UKCA机械指令证书".equals(oneCe.getString("reportName"))) {
                        autoData.put("jxNum", oneCe.getString("reportCode"));
                        autoData.put("jxId", oneCe.getString("id"));
                        autoData.put("jxStartDate", oneCe.getString("reportDate"));
                        autoData.put("jxEndDate", oneCe.getString("reportValidity"));
                    } else if ("CE电磁兼容指令证书".equals(oneCe.getString("reportName"))
                            || "UKCA电磁兼容指令证书".equals(oneCe.getString("reportName"))) {
                        autoData.put("dcNum", oneCe.getString("reportCode"));
                        autoData.put("dcId", oneCe.getString("id"));
                        autoData.put("dcStartDate", oneCe.getString("reportDate"));
                        autoData.put("dcEndDate", oneCe.getString("reportValidity"));
                    }
                }
            }
            autoData.put("linkWwrz", wwrzId);
        }
        //@lwgkiller:增加按照onlyNum进行查找的条件，ceinfoId为空且onlyNum不为空时，尝试使用onlyNum进行ceinfoId的查找
        String onlyNum = RequestUtil.getString(request, "onlyNum");
        if (StringUtil.isEmpty(ceinfoId) && StringUtil.isNotEmpty(onlyNum)) {
            List<JSONObject> jsonObjectList = ceinfoDao.queryCeinfoByOnlyNum(onlyNum);
            if (!jsonObjectList.isEmpty()) {
                ceinfoId = jsonObjectList.get(0).getString("ceinfoId");
            }
        }
        mv.addObject("action", action);
        mv.addObject("autoData", autoData);
        mv.addObject("ceinfoId", ceinfoId);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUser", ContextUtil.getCurrentUser());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        return mv;
    }

    @RequestMapping("getCeinfoDetail")
    @ResponseBody
    public JSONObject getCeinfoDetail(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        JSONObject ceinfoobj = new JSONObject();
        String ceinfoId = RequestUtil.getString(request, "ceinfoId");
        if (StringUtils.isNotBlank(ceinfoId)) {
            ceinfoobj = ceinfoService.getCeinfoDetail(ceinfoId);
        }
        return ceinfoobj;
    }

    @RequestMapping("queryInfoList")
    @ResponseBody
    public JsonPageResult<?> queryCeinfoList(HttpServletRequest request, HttpServletResponse response) {
        return ceinfoService.queryCeinfo(request, response, true);
    }

    @RequestMapping("saveCeinfo")
    @ResponseBody
    public JsonResult saveCeinfo(HttpServletRequest request, @RequestBody JSONObject formDataJson,
                                 HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "成功");
        try {
            if (formDataJson == null || formDataJson.isEmpty()) {
                logger.warn("formData is blank");
                result.setSuccess(false);
                result.setMessage("requestBody is blank");
                return result;
            }
            if (StringUtils.isBlank(formDataJson.getString("ceinfoId"))) {
                ceinfoService.insertCeinfo(formDataJson);
            } else {
                ceinfoService.updateCeinfo(formDataJson);
            }
        } catch (Exception e) {
            logger.error("Exception in save ceinfo");
            result.setSuccess(false);
            result.setMessage("Exception in save ceinfo");
            return result;
        }
        result.setData(formDataJson.getString("ceinfoId"));
        return result;
    }


    @RequestMapping("deleteCeinfo")
    @ResponseBody
    public JsonResult deleteCeinfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return ceinfoService.deleteCeinfo(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteCeinfo", e);
            return new JsonResult(false, e.getMessage());
        }
    }

}
