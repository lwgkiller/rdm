package com.redxun.rdmZhgl.core.controller;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.SaleFileOMAXiazaiApplyService;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.serviceEngineering.core.controller.DecorationDownloadApplyController;
import com.redxun.serviceEngineering.core.dao.DecorationDownloadApplyDao;
import com.redxun.serviceEngineering.core.dao.ManualFileDownloadApplyDao;
import com.redxun.serviceEngineering.core.service.DecorationDownloadApplyService;
import com.redxun.serviceEngineering.core.service.ManualFileDownloadApplyManager;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.sys.core.dao.SysDicDao;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.sys.core.util.SysPropertiesUtil;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/rdmZhgl/core/SaleFileOMAXiazaiApply/")

public class SaleFileOMAXiazaiApplyController {
    private static final Logger logger = LoggerFactory.getLogger(SaleFileOMAXiazaiApplyController.class);
    @Autowired
    private CommonBpmManager commonBpmManager;
    @Autowired
    private SaleFileOMAXiazaiApplyService saleFileOMAXiazaiApplyService;
    @Autowired
    BpmInstManager bpmInstManager;

    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    //..
    @RequestMapping("applyListPage")
    public ModelAndView getListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String url = "rdmZhgl/core/SaleFileXiazaiApplyList.jsp";
        ModelAndView mv = new ModelAndView(url);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        return mv;
    }
    @RequestMapping("applyList")
    @ResponseBody
    public JsonPageResult<?> queryApplyList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return saleFileOMAXiazaiApplyService.queryApplyList(request, true);
    }
    @RequestMapping("applyEditPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/SaleFileXiazaiApplyEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        // 新增或者编辑页面由流程引擎跳转并替换url中的pk和nodeId_
        String applyId = RequestUtil.getString(request, "applyId", "");
        String nodeId = RequestUtil.getString(request, "nodeId", "");
        String action = RequestUtil.getString(request, "action", "");
        String status = RequestUtil.getString(request, "status", "");
        if (StringUtils.isBlank(action)) {
            // 新增或编辑的时候没有nodeId
            if (StringUtils.isBlank(nodeId) || nodeId.contains("PROCESS")) {
                action = "edit";
            } else {
                // 处理任务的时候有nodeId
                action = "task";
            }
        }
        // 取出节点变量，返回到页面
        if (StringUtils.isNotBlank(nodeId)) {
            List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId, "SaleFileXiazaiApply", null);
            if (nodeVars != null && !nodeVars.isEmpty()) {
                mv.addObject("nodeVars", JSONObject.toJSONString(nodeVars));
            }
        }
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        mv.addObject("applyId", applyId);
        mv.addObject("action", action);
        mv.addObject("status", status);
        return mv;
    }



@RequestMapping("saleFileOMAList")
@ResponseBody
public List<JSONObject> saleFileOMAList(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String applyId = RequestUtil.getString(request, "applyId", "");
    if (StringUtils.isBlank(applyId)) {
        logger.error("applyId is blank");
        return null;
    }
    JSONObject params = new JSONObject();
    params.put("applyId", applyId);
    List<JSONObject> demandList = saleFileOMAXiazaiApplyService.queryDemandList(params);
    return demandList;
}
    @RequestMapping("getJson")
    @ResponseBody
    public JSONObject getJson(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = RequestUtil.getString(request, "id", "");
        if (StringUtils.isBlank(id)) {
            JSONObject result = new JSONObject();
            result.put("creatorName", ContextUtil.getCurrentUser().getFullname());
            result.put("creatorDeptName", ContextUtil.getCurrentUser().getMainGroupName());
            return result;
        }
        return saleFileOMAXiazaiApplyService.queryApplyDetail(id);
    }


    /**
     * 获取list
     */
    @RequestMapping("querySaleFileOMAList")
    @ResponseBody
    public JsonPageResult<?> querySaleFileOMAList(HttpServletRequest request, HttpServletResponse response) {
        return saleFileOMAXiazaiApplyService.querySaleFileOMAList(request, response);
    }

    @RequestMapping("preview")
    public ResponseEntity<byte[]> preview(HttpServletRequest request, HttpServletResponse response) {
        ResponseEntity<byte[]> result = null;
//        String fileType = RequestUtil.getString(request, "fileType");
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String salefileId = RequestUtil.getString(request, "id");
        String filePathBase = SysPropertiesUtil.getGlobalProperty("saleFileUrl");
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find saleFileUrl");
            return result;
        }
        String fileType = CommonFuns.toGetFileSuffix(fileName);
        switch (fileType) {
            case "pdf":
                result = rdmZhglFileManager.pdfPreviewOrDownLoad(fileName, fileId, salefileId, filePathBase);
            case "pic":
                rdmZhglFileManager.imageFilePreview(fileName, fileId, salefileId, filePathBase, response);
                break;
            default:
                rdmZhglFileManager.officeFilePreview(fileName, fileId, salefileId, filePathBase, response);
                break;
        }
        return result;
    }



    @RequestMapping("saveInProcess")
    @ResponseBody
    public JsonResult saveInProcess(HttpServletRequest request, @RequestBody String data, HttpServletResponse response)
            throws Exception {
        JsonResult result = new JsonResult(true, "保存成功");
        if (StringUtils.isBlank(data)) {
            logger.warn("requestBody is blank");
            result.setSuccess(false);
            result.setMessage("表单为空，保存失败！");
            return result;
        }
        saleFileOMAXiazaiApplyService.saveInProcess(result, data);
        return result;
    }

    @RequestMapping("download")
    public ResponseEntity<byte[]> download(HttpServletRequest request, HttpServletResponse response) {
        String id = RequestUtil.getString(request, "id");
        String description = RequestUtil.getString(request, "description");
        String fileId =RequestUtil.getString(request, "fileId");
        return saleFileOMAXiazaiApplyService.Download(request, id, description,fileId);
    }

    @RequestMapping("deleteApply")
    @ResponseBody
    public JsonResult deleteApply(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String instIdStr = RequestUtil.getString(request, "instIds");
            if (StringUtils.isNotBlank(instIdStr)) {
                String[] instIds = instIdStr.split(",");
                for (int index = 0; index < instIds.length; index++) {
                    bpmInstManager.deleteCascade(instIds[index], "");
                }
            }
            String[] ids = uIdStr.split(",");
            return saleFileOMAXiazaiApplyService.delApplys(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteApply", e);
            return new JsonResult(false, e.getMessage());
        }
    }
}
