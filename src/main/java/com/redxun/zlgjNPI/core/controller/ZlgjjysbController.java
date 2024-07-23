package com.redxun.zlgjNPI.core.controller;

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

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.entity.BpmInst;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.StringUtil;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import com.redxun.zlgjNPI.core.dao.ZlgjjysbDao;
import com.redxun.zlgjNPI.core.manager.ZlgjjysbService;

@Controller
@RequestMapping("/zlgjNPI/core/zlgjjysb/")
public class ZlgjjysbController {
    private static final Logger logger = LoggerFactory.getLogger(ZlgjjysbController.class);

    @Autowired
    private ZlgjjysbService zlgjjysbService;
    @Autowired
    private CommonBpmManager commonBpmManager;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private BpmInstManager bpmInstManager;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private ZlgjjysbDao zlgjjysbDao;
    @Autowired
    private SysDicManager sysDicManager;

    @RequestMapping("zlgjjysbListPage")
    public ModelAndView zlgjjysbListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "zlgjNPI/zlgjjysbList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        boolean isGJJYZY = rdmZhglUtil.judgeIsPointRoleUser(ContextUtil.getCurrentUserId(), "质量改进建议专员");
        mv.addObject("isGJJYZY", isGJJYZY);
        String crmToZlgjjyAgent = sysDicManager.getBySysTreeKeyAndDicKey("zlgjGroups", "crmToZlgjjyAgent").getValue();
        mv.addObject("crmToZlgjjyAgent", crmToZlgjjyAgent);
        return mv;
    }

    // ..
    @RequestMapping("zlgjjysbListQuery")
    @ResponseBody
    public JsonPageResult<?> zlgjjysbListQuery(HttpServletRequest request, HttpServletResponse response) {
        return zlgjjysbService.zlgjjysbListQuery(request, response, true);
    }

    @RequestMapping("zlgjjysbEditPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "zlgjNPI/zlgjjysbEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        // 新增或者编辑页面由流程引擎跳转并替换url中的pk和nodeId_
        String zlgjjysbId = RequestUtil.getString(request, "zlgjjysbId");
        String nodeId = RequestUtil.getString(request, "nodeId");
        String action = RequestUtil.getString(request, "action");
        if (StringUtils.isBlank(action)) {
            // 新增或编辑的时候没有nodeId
            if (StringUtils.isBlank(nodeId) || nodeId.contains("PROCESS")) {
                action = "edit";
            } else {
                // 处理任务的时候有nodeId
                action = "task";
            }
        }
        mv.addObject("zlgjjysbId", zlgjjysbId).addObject("action", action);
        // 取出节点变量，返回到页面
        if (StringUtils.isNotBlank(nodeId)) {
            mv.addObject("nodeId", nodeId);
            List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId, "ZLGJJYSB", null);
            if (nodeVars != null && !nodeVars.isEmpty()) {
                mv.addObject("nodeVars", JSONObject.toJSONString(nodeVars));
            }
        }
        Map<String, Object> params = new HashMap<>();
        params.put("userId", ContextUtil.getCurrentUserId());
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        String wdzy = "false";
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        mv.addObject("mainGroupId", ContextUtil.getCurrentUser().getMainGroupId());
        mv.addObject("mainGroupName", ContextUtil.getCurrentUser().getMainGroupName());
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("mobile", ContextUtil.getCurrentUser().getMobile());
        return mv;
    }

    private void toGetSelectArray(List<JSONObject> result, String name, String value, Set<String> canSelectSet) {
        JSONObject bpjy = new JSONObject();
        bpjy.put("key", name);
        bpjy.put("value", value);
        if (!canSelectSet.contains(name)) {
            bpjy.put("enabled", false);
        }
        result.add(bpjy);
    }

    @RequestMapping("getZlgjjysbDetail")
    @ResponseBody
    public JSONObject getZlgjjysbDetail(HttpServletRequest request, HttpServletResponse response) {
        String zlgjjysbId = RequestUtil.getString(request, "zlgjjysbId");
        return zlgjjysbService.getZlgjjysbDetail(zlgjjysbId);
    }

    @RequestMapping("openZlgjjysbUploadWindow")
    public ModelAndView openZlgjjysbUploadWindow(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        String jspPath = "zlgjNPI/ZlgjjysbFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("zlgjjysbId", RequestUtil.getString(request, "zlgjjysbId"));
        return mv;
    }

    @RequestMapping(value = "zlgjjysbUpload")
    @ResponseBody
    public Map<String, Object> zlgjjysbUpload(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        try {
            // 先保存文件到磁盘，成功后再写入数据库，前台是一个一个文件的调用
            zlgjjysbService.zlgjjysbUpload(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            logger.error("Exception in upload", e);
            modelMap.put("success", "false");
        }
        return modelMap;
    }

    @RequestMapping("queryZlgjjysbFileList")
    @ResponseBody
    public List<JSONObject> queryZlgjjysbFileList(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        String zlgjjysbId = RequestUtil.getString(request, "zlgjjysbId");
        if (StringUtils.isBlank(zlgjjysbId)) {
            logger.error("zlgjjysbId is blank");
            return null;
        }
        JSONObject params = new JSONObject();
        List<String> zlgjjysbIds = new ArrayList<>();
        zlgjjysbIds.add(zlgjjysbId);
        params.put("zlgjjysbIds", zlgjjysbIds);
        List<JSONObject> fileInfos = zlgjjysbService.queryZlgjjysbFileList(params);
        return fileInfos;
    }

    // 删除某个文件（从文件表和磁盘上都删除）
    @RequestMapping("delZlgjjysbFileById")
    public void delZlgjjysbFileById(HttpServletRequest request, HttpServletResponse response,
        @RequestBody String postBody) {
        JSONObject postBodyObj = JSONObject.parseObject(postBody);
        String fileName = postBodyObj.getString("fileName");
        String id = postBodyObj.getString("id");
        String zlgjjysbId = postBodyObj.getString("formId");
        zlgjjysbService.delZlgjjysbFileById(id, fileName, zlgjjysbId);
    }

    // 文档的下载（预览pdf也调用这里）
    @RequestMapping("zlgjjysbDownload")
    public ResponseEntity<byte[]> zlgjjysbDownload(HttpServletRequest request, HttpServletResponse response) {
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
            String zlgjjysbId = RequestUtil.getString(request, "formId");
            if (StringUtils.isBlank(zlgjjysbId)) {
                logger.error("操作失败，主表Id为空！");
                return null;
            }
            String fileBasePath = WebAppUtil.getProperty("zlgjjysbPathBase");
            if (StringUtils.isBlank(fileBasePath)) {
                logger.error("操作失败，找不到存储路径");
                return null;
            }
            String suffix = CommonFuns.toGetFileSuffix(fileName);
            String fullFilePath = fileBasePath + File.separator + zlgjjysbId + File.separator + fileId + "." + suffix;
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

    @RequestMapping("zlgjjysbOfficePreview")
    @ResponseBody
    public void zlgjjysbOfficePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("zlgjjysbPathBase");
        rdmZhglFileManager.officeFilePreview(fileName, fileId, formId, fileBasePath, response);
    }

    @RequestMapping("zlgjjysbImagePreview")
    @ResponseBody
    public void zlgjjysbImagePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("zlgjjysbPathBase");
        rdmZhglFileManager.imageFilePreview(fileName, fileId, formId, fileBasePath, response);
    }

    @RequestMapping("deleteZlgjjysb")
    @ResponseBody
    public JsonResult deleteZlgjjysb(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String instIdStr = RequestUtil.getString(request, "instIds");
            // //todo:质量改进建议回调外部系统，并更新回调状态，暂时取消!!!
            // //为什么放在这里？下面这个坑爹的service连调放在了controller层，各自事务隔离，不同步，
            // //我不能改动这么大，只有在前面先来一下，报错后面都不执行,也只能这样了。当然
            // JsonResult jsonResult = new JsonResult();
            // List<String> businessIds = Arrays.asList(uIdStr);
            // for (String businessId : businessIds) {
            // jsonResult = zlgjjysbService.doCallBackOutSystemFromzlgjjysb(businessId, false);
            // if (!jsonResult.getSuccess()) {
            // throw new Exception(jsonResult.getMessage());
            // }
            // }
            // //..
            if (StringUtils.isNotBlank(instIdStr)) {
                String[] instIds = instIdStr.split(",");
                for (int index = 0; index < instIds.length; index++) {
                    bpmInstManager.deleteCascade(instIds[index], "");
                }
            }
            String[] ids = uIdStr.split(",");
            return zlgjjysbService.deleteZlgjjysb(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteData", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("saveZlgjjysb")
    @ResponseBody
    public JsonResult saveZlgjjysb(HttpServletRequest request, @RequestBody String formData,
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
            if (StringUtils.isBlank(formDataJson.getString("id"))) {
                zlgjjysbService.createZlgjjysb(formDataJson);
            } else {
                zlgjjysbService.updateZlgjjysb(formDataJson);
            }
        } catch (Exception e) {
            logger.error("Exception in save jxbzzbsh");
            result.setSuccess(false);
            result.setMessage("Exception in save jxbzzbsh");
            return result;
        }
        return result;
    }

    @RequestMapping("queryDcry")
    @ResponseBody
    public List<JSONObject> queryDcry(HttpServletRequest request) {
        String zlgjjysbId = RequestUtil.getString(request, "zlgjjysbId");
        return zlgjjysbService.queryDcry(zlgjjysbId);
    }

    @RequestMapping("zlgjjysbAppeal")
    @ResponseBody
    public JsonResult zlgjjysbAppeal(HttpServletRequest request, HttpServletResponse response) {
        String projectName = RequestUtil.getString(request, "projectName");
        String declareTime = RequestUtil.getString(request, "declareTime");
        String creator = RequestUtil.getString(request, "creator");
        return zlgjjysbService.zlgjjysbAppeal(projectName, declareTime, creator);
    }

    @RequestMapping("deleteDcryById")
    @ResponseBody
    public JsonResult deleteDcryById(HttpServletRequest request) {
        JsonResult result = new JsonResult(true, "");
        String id = RequestUtil.getString(request, "id");
        zlgjjysbService.deleteDcryById(id);
        return result;
    }

    // 导出
    @RequestMapping("exportZlgjjysb")
    public void exportZlgjjysb(HttpServletRequest request, HttpServletResponse response) {
        zlgjjysbService.exportZlgjjysb(request, response);
    }

    // ..质量改进问题回调外部系统-作废场景，instId必传
    @RequestMapping("callBackOutSystem")
    @ResponseBody
    public JsonResult callBackOutSystem(HttpServletRequest request, HttpServletResponse response) {
        JsonResult jsonResult = new JsonResult();
        String instId = RequestUtil.getString(request, "instId");
        int okOrNot = RequestUtil.getInt(request, "okOrNot");
        String zlshshyjMock = RequestUtil.getString(request, "zlshshyjMock");
        BpmInst bpmInst = bpmInstManager.get(instId);
        String businessId = bpmInst.getBusKey();
        try {
            jsonResult = zlgjjysbService.doCallBackOutSystemFromzlgjjysb(businessId, okOrNot, zlshshyjMock, "", "");
            return jsonResult;
        } catch (Exception e) {
            jsonResult.setSuccess(false);
            jsonResult.setMessage("回调失败，系统异常！错误信息：" + e.getMessage());
            return jsonResult;
        }
    }

    // ..质量改进问题回调外部系统-手工场景
    @RequestMapping("callBackOutSystemTest")
    @ResponseBody
    public JsonResult callBackOutSystemTest(HttpServletRequest request, HttpServletResponse response) {
        String businessId = RequestUtil.getString(request, "businessId");
        int okOrNot = RequestUtil.getInt(request, "okOrNot");
        String zlshshyjMock = RequestUtil.getString(request, "zlshshyjMock");
        JsonResult jsonResult = new JsonResult();
        try {
            jsonResult = zlgjjysbService.doCallBackOutSystemFromzlgjjysb(businessId, okOrNot, zlshshyjMock, "", "");
            return jsonResult;
        } catch (Exception e) {
            jsonResult.setSuccess(false);
            jsonResult.setMessage("回调失败，系统异常！错误信息：" + e.getMessage());
            return jsonResult;
        }
    }

    @RequestMapping("judgeZlbzb")
    @ResponseBody
    public boolean judgeZlbzb(HttpServletRequest request, HttpServletResponse response) {
        String userId = RequestUtil.getString(request, "userId");
        // String msg = "调用成功";
        if (StringUtil.isEmpty(userId)) {
            // msg = "用户Id不能为空！";
            // return ResultUtil.result(org.apache.http.HttpStatus.SC_BAD_REQUEST, msg,"");
            return false;
        }
        boolean flag = zlgjjysbService.judgeZlbzb(userId);
        // return ResultUtil.result(org.apache.http.HttpStatus.SC_OK, msg, flag);
        return flag;
    }

    // 输出修改部门名称的sql
    @RequestMapping("test")
    @ResponseBody
    public void test(HttpServletRequest request, HttpServletResponse response) {
        List<JSONObject> result = zlgjjysbDao.test();
        StringBuilder stringBuilder = new StringBuilder();
        for (JSONObject oneData : result) {
            stringBuilder.append("update " + oneData.getString("tableName") + " set " + oneData.getString("tableColumn")
                + "= \"高端属具产品研究\"").append(" where " + oneData.getString("tableColumn") + "= \"零部件产品研究所\";\n");
        }
        System.out.println(stringBuilder.toString());
    }
}
