package com.redxun.serviceEngineering.core.controller;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.serviceEngineering.core.service.DecorationManualFileService;
import com.redxun.serviceEngineering.core.service.MaintenanceManualfileService;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/serviceEngineering/core/decorationManualFile")
public class DecorationManualFileController {
    private static final Logger logger = LoggerFactory.getLogger(DecorationManualFileController.class);
    @Autowired
    DecorationManualFileService decorationManualFileService;
    @Autowired
    SysDicManager sysDicManager;

    //..
    @RequestMapping("dataListPage")
    public ModelAndView dataListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/decorationManualFileList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        //第三方页面打开时传入便捷查询参数
        String salesModel = RequestUtil.getString(request, "salesModel");
        String designModel = RequestUtil.getString(request, "designModel");
        String materialCode = RequestUtil.getString(request, "materialCode");
        String manualLanguage = RequestUtil.getString(request, "manualLanguage");
        String manualType = RequestUtil.getString(request, "manualType");
        String decorationManualAdmin = sysDicManager.getBySysTreeKeyAndDicKey(
                "serviceEngineeringGroups", "decorationManualAdmin").getValue();
        mv.addObject("salesModel", salesModel)
                .addObject("designModel", designModel)
                .addObject("materialCode", materialCode)
                .addObject("manualLanguage", manualLanguage)
                .addObject("manualType", manualType)
                .addObject("decorationManualAdmin", decorationManualAdmin);
        return mv;
    }

    //..
    @RequestMapping("dataListQuery")
    @ResponseBody
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) {
        return decorationManualFileService.dataListQuery(request, response);
    }

    //..
    @RequestMapping("preview")
    public void preview(HttpServletRequest request, HttpServletResponse response) {
        decorationManualFileService.preview(request, response);
    }

    //..
    @RequestMapping("download")
    public ResponseEntity<byte[]> download(HttpServletRequest request, HttpServletResponse response) {
        String id = RequestUtil.getString(request, "id");
        String description = RequestUtil.getString(request, "description");
        return decorationManualFileService.Download(request, id, description);
    }

    //..
    @RequestMapping("editPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "serviceEngineering/core/decorationManualFileEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String id = RequestUtil.getString(request, "id");
        String action = RequestUtil.getString(request, "action");
        JSONObject obj = new JSONObject();
        if (StringUtils.isNotBlank(id)) {
            obj = decorationManualFileService.queryDataById(id);
        }
        mv.addObject("obj", obj).addObject("businessId", obj.getString("id"));
        mv.addObject("action", action);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        return mv;
    }

    //..
    @RequestMapping("deleteBusiness")
    @ResponseBody
    public JSONObject deleteBusiness(HttpServletRequest request, @RequestBody String requestBody,
                                     HttpServletResponse response) {
        JSONObject result = new JSONObject();
        JSONObject requestBodyObj = JSONObject.parseObject(requestBody);
        String id = requestBodyObj.getString("id");
        decorationManualFileService.deleteBusiness(result, id);
        return result;
    }

    //..
    @RequestMapping("releaseBusiness")
    @ResponseBody
    public JSONObject releaseBusiness(HttpServletRequest request, @RequestBody String requestBody,
                                      HttpServletResponse response) {
        JSONObject result = new JSONObject();
        JSONObject requestBodyObj = JSONObject.parseObject(requestBody);
        String id = requestBodyObj.getString("id");
        decorationManualFileService.releaseBusiness(result, id);
        return result;
    }

    //..
    @RequestMapping("saveBusiness")
    @ResponseBody
    public JSONObject saveBusiness(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        decorationManualFileService.saveBusiness(result, request);
        return result;
    }

    //..
    @RequestMapping("exportList")
    public void exportExcel(HttpServletRequest request, HttpServletResponse response) {
        decorationManualFileService.exportList(request, response);
    }
}
