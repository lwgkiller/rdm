package com.redxun.strategicPlanning.core.controller;

import java.io.File;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.strategicPlanning.core.service.CpxpghService;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

@Controller
@RequestMapping("/strategicplanning/core/cpxpgh")
public class CpxpghController {
    private static final Logger logger = LoggerFactory.getLogger(CpxpghController.class);
    @Autowired
    private CpxpghService cpxpghService;
    @Autowired
    private CommonBpmManager commonBpmManager;
    @Autowired
    private BpmInstManager bpmInstManager;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;

    // ..
    @RequestMapping("cpxpghListPage")
    public ModelAndView cpxpghListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "zlghCpxpgh/core/cpxpghList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String mainGroupName = ContextUtil.getCurrentUser().getMainGroupName();
        boolean isFgld = rdmZhglUtil.judgeUserIsFgld(ContextUtil.getCurrentUserId());
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("isFgld", isFgld);
        mv.addObject("mainGroupName", mainGroupName);
        return mv;
    }

    // ..
    @RequestMapping("cpxpghListQuery")
    @ResponseBody
    public JsonPageResult<?> cpxpghListQuery(HttpServletRequest request, HttpServletResponse response) {
        return cpxpghService.cpxpghListQuery(request, response, true);
    }

    @RequestMapping("cpxpghEditPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "zlghCpxpgh/core/cpxpghEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        // 新增或者编辑页面由流程引擎跳转并替换url中的pk和nodeId_
        String cpxpghId = RequestUtil.getString(request, "cpxpghId");
        String nodeId = RequestUtil.getString(request, "nodeId");
        String action = RequestUtil.getString(request, "action");
        String taskStatus = RequestUtil.getString(request, "taskStatus");
        String changeId = RequestUtil.getString(request, "changeId");
        if (StringUtils.isBlank(action)) {
            // 新增或编辑的时候没有nodeId
            if (StringUtils.isBlank(nodeId) || nodeId.contains("PROCESS")) {
                action = "edit";
            } else {
                // 处理任务的时候有nodeId
                action = "task";
            }
        }
        mv.addObject("cpxpghId", cpxpghId).addObject("action", action).addObject("taskStatus", taskStatus);
        // 取出节点变量，返回到页面
        if (StringUtils.isNotBlank(nodeId)) {
            mv.addObject("nodeId", nodeId);
            List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId, "CPXPGH", null);
            if (nodeVars != null && !nodeVars.isEmpty()) {
                mv.addObject("nodeVars", JSONObject.toJSONString(nodeVars));
            }
        }
        String jsztqrUser = cpxpghService.isJsztqrUsers();
        String mainGroupId = ContextUtil.getCurrentUser().getMainGroupId();
        String mainGroupName = ContextUtil.getCurrentUser().getMainGroupName();
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        mv.addObject("mainGroupId", mainGroupId);
        mv.addObject("mainGroupName", mainGroupName);
        mv.addObject("jsztqrUser", jsztqrUser);
        mv.addObject("changeId", changeId);
        return mv;
    }

    @RequestMapping("getCpxpghDetail")
    @ResponseBody
    public JSONObject getCpxpghDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject obj = new JSONObject();
        String cpxpghId = RequestUtil.getString(request, "cpxpghId");
        if (StringUtils.isNotBlank(cpxpghId)) {
            obj = cpxpghService.getCpxpghDetail(cpxpghId);
        }
        return obj;
    }

    @RequestMapping("queryChilds")
    @ResponseBody
    public List<JSONObject> querySalesArea(HttpServletRequest request, HttpServletResponse response) {
        String cpxpghId = RequestUtil.getString(request, "cpxpghId");
        String childType = RequestUtil.getString(request, "childType");
        JSONObject obj = new JSONObject();
        obj.put("cpxpghId", cpxpghId);
        obj.put("childType", childType);
        return cpxpghService.queryChilds(obj);
    }

    @RequestMapping("delChildsById")
    @ResponseBody
    public JsonResult delChildsById(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String id = RequestUtil.getString(request, "id");
            return cpxpghService.delChildsById(id);
        } catch (Exception e) {
            logger.error("Exception in delSalesArea", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("openCpxpghUploadWindow")
    public ModelAndView openCpxpghUploadWindow(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        String jspPath = "zlghCpxpgh/core/cpxpghFileUpload.jsp";
        String cpxpghId = RequestUtil.getString(request, "cpxpghId");
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("cpxpghId", cpxpghId);
        return mv;
    }

    @RequestMapping(value = "cpxpghUpload")
    @ResponseBody
    public Map<String, Object> cpxpghUpload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        try {
            // 先保存文件到磁盘，成功后再写入数据库，前台是一个一个文件的调用
            cpxpghService.saveUploadFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            logger.error("Exception in upload", e);
            modelMap.put("success", "false");
        }
        return modelMap;
    }

    @RequestMapping("queryCpxpghFileList")
    @ResponseBody
    public List<JSONObject> queryCpxpghFileList(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        String cpxpghId = RequestUtil.getString(request, "cpxpghId");
        if (StringUtils.isBlank(cpxpghId)) {
            logger.error("cpxpghId is blank");
            return null;
        }
        Map<String, Object> params = new HashMap<>();
        List<String> cpxpghIds = new ArrayList<>();
        cpxpghIds.add(cpxpghId);
        params.put("cpxpghIds", cpxpghIds);
        List<JSONObject> fileInfos = cpxpghService.queryCpxpghFileList(params);
        // 格式化时间
        if (fileInfos != null && !fileInfos.isEmpty()) {
            for (JSONObject oneFile : fileInfos) {
                if (StringUtils.isNotBlank(oneFile.getString("CREATE_TIME_"))) {
                    oneFile.put("CREATE_TIME_",
                        DateUtil.formatDate(oneFile.getDate("CREATE_TIME_"), "yyyy-MM-dd HH:mm:ss"));
                }
            }
        }
        return fileInfos;
    }

    // 删除某个文件（从文件表和磁盘上都删除）
    @RequestMapping("delCpxpghFiles")
    public void delCpxpghFiles(HttpServletRequest request, HttpServletResponse response, @RequestBody String postBody) {
        JSONObject postBodyObj = JSONObject.parseObject(postBody);
        String fileName = postBodyObj.getString("fileName");
        String id = postBodyObj.getString("id");
        String cpxpghId = postBodyObj.getString("formId");
        cpxpghService.delCpxpghFiles(id, fileName, cpxpghId);
    }

    // 文档的下载（预览pdf也调用这里）
    @RequestMapping("cpxpghDownload")
    public ResponseEntity<byte[]> fileDownload(HttpServletRequest request, HttpServletResponse response) {
        try {
            String fileName = RequestUtil.getString(request, "fileName");
            if (StringUtils.isBlank(fileName)) {
                logger.error("操作失败，文件名为空！");
                return null;
            }
            String fileId = RequestUtil.getString(request, "fileId");
            if (StringUtils.isBlank(fileId)) {
                logger.error("操作失败，文件Id为空！");
                return null;
            }
            String formId = RequestUtil.getString(request, "formId");
            if (StringUtils.isBlank(formId)) {
                logger.error("操作失败，主表Id为空！");
                return null;
            }
            String fileBasePath = WebAppUtil.getProperty("cpxpghFilePathBase");
            if (StringUtils.isBlank(fileBasePath)) {
                logger.error("操作失败，找不到存储路径");
                return null;
            }
            String suffix = CommonFuns.toGetFileSuffix(fileName);
            String fullFilePath = fileBasePath + File.separator + formId + File.separator + fileId + "." + suffix;
            // 创建文件实例
            File file = new File(fullFilePath);
            // 修改文件名的编码格式
            String downloadFileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");

            // 设置httpHeaders,使浏览器响应下载
            HttpHeaders headers = new HttpHeaders();
            // 告诉浏览器执行下载的操作，“attachment”告诉了浏览器进行下载,下载的文件 文件名为 downloadFileName
            headers.setContentDispositionFormData("attachment", downloadFileName);
            // 设置响应方式为二进制，以二进制流传输
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file), headers, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Exception in fileDownload", e);
            return null;
        }
    }

    @RequestMapping("cpxpghOfficePreview")
    @ResponseBody
    public void officePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("cpxpghFilePathBase");
        rdmZhglFileManager.officeFilePreview(fileName, fileId, formId, fileBasePath, response);
    }

    @RequestMapping("cpxpghImagePreview")
    @ResponseBody
    public void imagePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("cpxpghFilePathBase");
        rdmZhglFileManager.imageFilePreview(fileName, fileId, formId, fileBasePath, response);
    }

    @RequestMapping("deleteCpxpgh")
    @ResponseBody
    public JsonResult deleteCpxpgh(HttpServletRequest request, HttpServletResponse response) throws Exception {
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
            return cpxpghService.deleteCpxpgh(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteData", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("saveCpxpgh")
    @ResponseBody
    public JsonResult saveCpxpgh(HttpServletRequest request, @RequestBody String formData,
        HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "");
        if (StringUtils.isBlank(formData)) {
            logger.warn("requestBody is blank");
            result.setSuccess(false);
            result.setMessage("requestBody is blank");
            return result;
        }
        try {
            JSONObject formDataJson = JSONObject.parseObject(formData);
            if (formDataJson == null || formDataJson.isEmpty()) {
                logger.warn("formData is blank");
                result.setSuccess(false);
                result.setMessage("requestBody is blank");
                return result;
            }
            String id = formDataJson.getString("id");
            if (StringUtils.isBlank(id)) {
                cpxpghService.insertCpxpgh(formDataJson);
            } else {
                cpxpghService.updateCpxpgh(formDataJson);
                Map<String, Object> param = new HashMap<>();
                List<String> ids = new ArrayList<>();
                ids.add(id);
                param.put("cpxpghIds", ids);
                cpxpghService.delChildsByCpxpghId(param);
                JSONArray cpxsztGrid = formDataJson.getJSONArray("SUB_cpxsztGrid");
                JSONArray cppzGrid = formDataJson.getJSONArray("SUB_cppzGrid");
                List<JSONObject> list = new ArrayList<>();
                if (cpxsztGrid != null && !cpxsztGrid.isEmpty()) {
                    for (int i = 0; i < cpxsztGrid.size(); i++) {
                        JSONObject jsonObject = cpxsztGrid.getJSONObject(i);
                        jsonObject.put("id", IdUtil.getId());
                        jsonObject.put("CREATE_TIME_", new Date());
                        jsonObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                        jsonObject.put("cpxpghId", id);
                        list.add(jsonObject);
                    }
                }
                if (cppzGrid != null && !cppzGrid.isEmpty()) {
                    for (int i = 0; i < cppzGrid.size(); i++) {
                        JSONObject jsonObject = cppzGrid.getJSONObject(i);
                        jsonObject.put("id", IdUtil.getId());
                        jsonObject.put("CREATE_TIME_", new Date());
                        jsonObject.put("cpxpghId", id);
                        jsonObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                        list.add(jsonObject);
                    }
                }
                if (list.size() > 0) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("list", list);
                    cpxpghService.insertChilds(map);
                }
            }
        } catch (Exception e) {
            logger.error("Exception in save cpxpgh");
            result.setSuccess(false);
            result.setMessage("Exception in save cpxpgh");
            return result;
        }
        return result;
    }

    // ..
    @RequestMapping("cpxpghFinalListPage")
    public ModelAndView cpxpghListFinalPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "zlghCpxpgh/core/cpxpghFinalList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String mainGroupName = ContextUtil.getCurrentUser().getMainGroupName();
        boolean isFgld = rdmZhglUtil.judgeUserIsFgld(ContextUtil.getCurrentUserId());
        boolean isZlghzy = cpxpghService.isZlghzy();
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("isFgld", isFgld);
        mv.addObject("isZlghzy", isZlghzy);
        mv.addObject("mainGroupName", mainGroupName);
        return mv;
    }

    // ..
    @RequestMapping("cpxpghFinalListQuery")
    @ResponseBody
    public JsonPageResult<?> cpxpghFinalListQuery(HttpServletRequest request, HttpServletResponse response) {
        return cpxpghService.cpxpghFinalListQuery(request, response, true);
    }

    @RequestMapping("cpxpghFinalEditPage")
    public ModelAndView cpxpghFinalEditPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "zlghCpxpgh/core/cpxpghFinalEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String mainGroupId = ContextUtil.getCurrentUser().getMainGroupId();
        String mainGroupName = ContextUtil.getCurrentUser().getMainGroupName();
        String cpxpghId = RequestUtil.getString(request, "cpxpghId");
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        mv.addObject("mainGroupId", mainGroupId);
        mv.addObject("mainGroupName", mainGroupName);
        mv.addObject("cpxpghId", cpxpghId);
        return mv;
    }

    // 判断是否有在运行中的数据
    @RequestMapping("wheterRunning")
    @ResponseBody
    public Integer wheterRunning(HttpServletRequest request, HttpServletResponse response) {
        String finalId = RequestUtil.getString(request, "finalId");
        return cpxpghService.wheterRunning(finalId);
    }

    // 查询历史版本
    @RequestMapping("queryHistoryData")
    @ResponseBody
    public List<JSONObject> queryHistoryData(HttpServletRequest request, HttpServletResponse response) {
        String finalId = RequestUtil.getString(request, "finalId");
        return cpxpghService.queryHistoryData(finalId);
    }

    // 作废
    @RequestMapping("discardCpxpghInst")
    @ResponseBody
    public JsonResult discardCpxpghInst(HttpServletRequest request, @RequestBody String formData,
        HttpServletResponse response) {
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String finalId = formDataJson.getString("finalId");
        return cpxpghService.discardCpxpghInst(finalId);
    }

}
