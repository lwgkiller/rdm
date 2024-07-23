package com.redxun.strategicPlanning.core.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.strategicPlanning.core.service.MonthlyService;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 研发月刊
 */
@RestController
@RequestMapping("/strategicplanning/core/monthly/")
public class MonthlyController {
    @Autowired
    private MonthlyService monthlyService;

    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;


    @RequestMapping("getMonthlyListPage")
    public ModelAndView getMonthlyList(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "zlghMonthly/core/zlghMonthly.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        Map<String, Object> params = new HashMap<>();
        params.put("userId", ContextUtil.getCurrentUserId());
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        // 返回当前登录人角色信息
        List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params);
        JSONArray userRolesJsonArray = XcmgProjectUtil.convertListMap2JsonArrObject(currentUserRoles);
        mv.addObject("currentUserRoles", userRolesJsonArray);
        // 返回当前用户是否是技术管理部负责人
        boolean isJsglbRespUser = rdmZhglUtil.judgeUserIsJsglbRespUser(ContextUtil.getCurrentUserId());
        mv.addObject("isJsglbRespUser", isJsglbRespUser);
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("type",RequestUtil.getString(request,"type",""));
        return mv;
    }

    @RequestMapping("getMonthlyList")
    @ResponseBody
    public JsonPageResult<?> getJsmmList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return monthlyService.getMonthlyList(request, response, true);
    }

    @RequestMapping("templateUploadWindow")
    public ModelAndView msgManagement(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "zlghMonthly/core/zlghMonthlyWindow.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("type",RequestUtil.getString(request,"type",""));
        return mv;
    }



    @RequestMapping("monthlyDownload")
    public ResponseEntity<byte[]> pdfPreviewOrDownload(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String fileBasePath = WebAppUtil.getProperty("zlghMonthlyFile");
        return rdmZhglFileManager.pdfPreviewOrDownLoad(fileName, fileId, "", fileBasePath);
    }

    //删除文件
    @RequestMapping("delMonthlyFile")
    public void delMonthlyFile(HttpServletRequest request, HttpServletResponse response, @RequestBody String postBody) {
        JSONObject postBodyObj = JSONObject.parseObject(postBody);
        String fileName = postBodyObj.getString("fileName");
        String fileId = postBodyObj.getString("id");
        monthlyService.deleteOneMonthlyFile(fileId, fileName);
    }

    @RequestMapping(value = "monthlyUpload")
    @ResponseBody
    public Map<String, Object> monthlyUpload(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        try {
            // 先保存文件，成功后再写入数据库，前台是一个一个文件的调用
            monthlyService.saveUploadFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            /*logger.error("Exception in upload", e);*/
            modelMap.put("success", "false");
        }
        return modelMap;
    }

    @RequestMapping("monthlyPdfPreview")
    @ResponseBody
    public ResponseEntity<byte[]> monthlyPreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("zlghMonthlyFile");
        return monthlyService.pdfFilePreview(fileName, fileId, formId, fileBasePath);
    }

}
