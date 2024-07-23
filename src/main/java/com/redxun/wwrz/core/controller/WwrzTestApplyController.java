
package com.redxun.wwrz.core.controller;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.manager.MybatisBaseManager;
import com.redxun.core.util.DateUtil;
import com.redxun.org.api.model.IUser;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.controller.MybatisListController;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.sys.core.util.SysPropertiesUtil;
import com.redxun.wwrz.core.dao.WwrzDocListDao;
import com.redxun.wwrz.core.dao.WwrzTestApplyDao;
import com.redxun.wwrz.core.service.WwrzTestApplyService;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *委外认证 测试流程
 * @author zz
 */
@Controller
@RequestMapping("/wwrz/core/test/")
public class WwrzTestApplyController extends MybatisListController {
    @Resource
    private CommonBpmManager commonBpmManager;
    @Resource
    private WwrzTestApplyService wwrzTestApplyService;
    @Resource
    private WwrzTestApplyDao wwrzTestApplyDao;
    @Resource
    private WwrzDocListDao wwrzDocListDao;
    @Resource
    private CommonInfoManager commonInfoManager;
    @SuppressWarnings("rawtypes")
    @Override
    public MybatisBaseManager getBaseManager() {
        return null;
    }
    /**
     * 审批列表页面
     * */
    @RequestMapping("listPage")
    public ModelAndView getListPage(HttpServletRequest request,HttpServletResponse response) throws Exception{
        String jspPath = "wwrz/core/wwrzTestApplyList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        IUser currentUser = ContextUtil.getCurrentUser();
        mv.addObject("currentUser", currentUser);
//        String currentDept = ContextUtil.getCurrentUser().getMainGroupName();
//        Boolean permission = true;
//        if(currentDept.equals(RdmConst.BZJSS_NAME)){
//            permission = true;
//        }
        JSONObject adminJson = commonInfoManager.hasPermission("WWRZ-GLY");
        mv.addObject("permission",adminJson.getBoolean("WWRZ-GLY"));
        return mv;
    }

   /**
    * 获取审批页面
    * */
    @RequestMapping("editPage")
    public ModelAndView getEditPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "wwrz/core/wwrzTestApplyEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String taskId_ = RequestUtil.getString(request, "taskId_");
        String nodeId = RequestUtil.getString(request, "nodeId");
        String id = RequestUtil.getString(request, "id");
        mv.addObject("taskId_", taskId_);
        // 新增或编辑的时候没有nodeId
        String action = "";
        if (StringUtils.isBlank(nodeId) || nodeId.contains("PROCESS")) {
            action = "edit";
        } else {
            action = "task";
        }
        //取出节点变量，返回到页面
        if (StringUtils.isNotBlank(nodeId)) {
            mv.addObject("nodeId", nodeId);
            List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId,"WwrzTest",null);
            if (nodeVars != null && !nodeVars.isEmpty()) {
                mv.addObject("nodeVars", JSONObject.toJSONString(nodeVars));
            }
        }
        mv.addObject("action", action);
        Map<String, Object> planApplyObj = null;
        JSONObject applyObj = new JSONObject();
        List<JSONObject> detailList = new ArrayList<>();
        List<JSONObject> docList = new ArrayList<>();
        if (StringUtils.isNotBlank(id)) {
            planApplyObj = wwrzTestApplyDao.getObjectById(id);
            applyObj = XcmgProjectUtil.convertMap2JsonObject(planApplyObj);
            if (applyObj.get("startDate") != null) {
                applyObj.put("startDate",
                        DateUtil.formatDate((Date)applyObj.get("startDate"), "yyyy-MM-dd"));
            }
            if (applyObj.get("endDate") != null) {
                applyObj.put("endDate",
                        DateUtil.formatDate((Date)applyObj.get("endDate"), "yyyy-MM-dd"));
            }
            if (applyObj.get("reStartDate") != null) {
                applyObj.put("reStartDate",
                        DateUtil.formatDate((Date)applyObj.get("reStartDate"), "yyyy-MM-dd"));
            }
            if (applyObj.get("reEndDate") != null) {
                applyObj.put("reEndDate",
                        DateUtil.formatDate((Date)applyObj.get("reEndDate"), "yyyy-MM-dd"));
            }
            if (applyObj.get("sendDate") != null) {
                applyObj.put("sendDate",
                        DateUtil.formatDate((Date)applyObj.get("sendDate"), "yyyy-MM-dd"));
            }
            detailList = wwrzTestApplyDao.getProblemList(id);
            docList = wwrzDocListDao.getDocumentList(id);
        }else{
            applyObj.put("editorUserId",ContextUtil.getCurrentUserId());
            applyObj.put("editorUserName",ContextUtil.getCurrentUser().getFullname());
            applyObj.put("editorUserDeptId",ContextUtil.getCurrentUser().getMainGroupId());
            applyObj.put("editorUserDeptName",ContextUtil.getCurrentUser().getMainGroupName());
        }
        applyObj.put("detailList",detailList);
        applyObj.put("docList",docList);
        mv.addObject("applyId", id);
        mv.addObject("applyObj", applyObj);
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        return mv;
    }
    /**
     * 获取list
     * */
    @RequestMapping("queryList")
    @ResponseBody
    public JsonPageResult<?> queryList(HttpServletRequest request, HttpServletResponse response) {
        return wwrzTestApplyService.queryList(request, response,true);
    }
    /**
     * 项目作废表单和流程删除
     * */
    @RequestMapping("delete")
    @ResponseBody
    public JsonResult delete(HttpServletRequest request, HttpServletResponse response) {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String instIdStr = RequestUtil.getString(request, "instIds");
            if (StringUtils.isNotEmpty(uIdStr) && StringUtils.isNotEmpty(instIdStr)) {
                String[] ids = uIdStr.split(",");
                String[] instIds = instIdStr.split(",");
                return wwrzTestApplyService.delete(ids, instIds);
            }
        } catch (Exception e) {
            return new JsonResult(false, e.getMessage());
        }
        return new JsonResult(true, "成功删除!");
    }
    /**
     * 明细：查看审批信息
     * */
    @RequestMapping("edit")
    public ModelAndView edit(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = RequestUtil.getString(request, "id");
        String action = RequestUtil.getString(request, "action");
        String status = RequestUtil.getString(request, "status");
        String jspPath = "wwrz/core/wwrzTestApplyEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("id", id).addObject("action", action).addObject("status", status);
        Map<String, Object> planApplyObj = null;
        JSONObject applyObj = new JSONObject();
        List<JSONObject> detailList = new ArrayList<>();
        List<JSONObject> docList = new ArrayList<>();
        if (StringUtils.isNotBlank(id)) {
            planApplyObj = wwrzTestApplyDao.getObjectById(id);
            applyObj = XcmgProjectUtil.convertMap2JsonObject(planApplyObj);
            if (applyObj.get("startDate") != null) {
                applyObj.put("startDate",
                        DateUtil.formatDate((Date)applyObj.get("startDate"), "yyyy-MM-dd"));
            }
            if (applyObj.get("endDate") != null) {
                applyObj.put("endDate",
                        DateUtil.formatDate((Date)applyObj.get("endDate"), "yyyy-MM-dd"));
            }
            if (applyObj.get("reStartDate") != null) {
                applyObj.put("reStartDate",
                        DateUtil.formatDate((Date)applyObj.get("reStartDate"), "yyyy-MM-dd"));
            }
            if (applyObj.get("reEndDate") != null) {
                applyObj.put("reEndDate",
                        DateUtil.formatDate((Date)applyObj.get("reEndDate"), "yyyy-MM-dd"));
            }
            if (applyObj.get("sendDate") != null) {
                applyObj.put("sendDate",
                        DateUtil.formatDate((Date)applyObj.get("sendDate"), "yyyy-MM-dd"));
            }
            detailList = wwrzTestApplyDao.getProblemList(id);
            docList = wwrzDocListDao.getDocumentList(id);
        }
        mv.addObject("applyObj", applyObj);
        mv.addObject("applyId", id);
        applyObj.put("detailList",detailList);
        applyObj.put("docList",docList);
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        return mv;
    }
    @RequestMapping("fileUploadWindow")
    public ModelAndView fileUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/saleFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("applyId", RequestUtil.getString(request, "applyId", ""));
        mv.addObject("fileModel", RequestUtil.getString(request, "fileModel", ""));
        return mv;
    }
    @RequestMapping(value = "dealProblemData", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult dealProblemData(HttpServletRequest request, @RequestBody String changeGridDataStr,
                               HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "");
        if (StringUtils.isBlank(changeGridDataStr)) {
            result.setSuccess(false);
            result.setMessage("requestBody is blank");
            return result;
        }
        wwrzTestApplyService.saveOrUpdateProblem(changeGridDataStr);
        return result;
    }
    @RequestMapping(value = "dealDocumentData", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult dealDocumentData(HttpServletRequest request, @RequestBody String changeGridDataStr,
                                      HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "");
        if (StringUtils.isBlank(changeGridDataStr)) {
            result.setSuccess(false);
            result.setMessage("requestBody is blank");
            return result;
        }
        wwrzTestApplyService.saveOrUpdateDocument(changeGridDataStr);
        return result;
    }
    @RequestMapping("fileDownload")
    public ResponseEntity<byte[]> fileDownload(HttpServletRequest request, HttpServletResponse response) {
        try {
            String fileName = RequestUtil.getString(request, "fileName");
            if (StringUtils.isBlank(fileName)) {
                logger.error("操作失败，文件名为空！");
                return null;
            }
            // 预览还是下载，取的根路径不一样
            String action = RequestUtil.getString(request, "action");
            if (StringUtils.isBlank(action)) {
                logger.error("操作类型为空");
                return null;
            }
            String fileId = RequestUtil.getString(request, "fileId");
            if (StringUtils.isBlank(fileId)) {
                logger.error("操作失败，文件主键为空！");
                return null;
            }
            String applyId = RequestUtil.getString(request, "applyId");
            String fileBasePath = SysPropertiesUtil.getGlobalProperty("saleFileUrl");
            if (StringUtils.isBlank(fileBasePath)) {
                logger.error("操作失败，找不到存储根路径");
                return null;
            }
            String suffix = CommonFuns.toGetFileSuffix(fileName);
            String relativeFilePath = "";
            if (StringUtils.isNotBlank(applyId)) {
                relativeFilePath = File.separator + applyId;
            }
            String realFileName = fileId + "." + suffix;
            String fullFilePath = fileBasePath + relativeFilePath + File.separator + realFileName;
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
    @RequestMapping("listApplyData")
    @ResponseBody
    public JsonPageResult<?> getListApplyData(HttpServletRequest request, HttpServletResponse response) {
        return wwrzTestApplyService.getApplyList(request);
    }

    /**
     * 认证问题列表页面
     * */
    @RequestMapping("listProblemPage")
    public ModelAndView getListProblemPage(HttpServletRequest request,HttpServletResponse response) throws Exception{
        String jspPath = "wwrz/core/wwrzProblemList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        IUser currentUser = ContextUtil.getCurrentUser();
        mv.addObject("currentUser", currentUser);
        return mv;
    }
    @RequestMapping("listProblemData")
    @ResponseBody
    public JsonPageResult<?> getListProblemData(HttpServletRequest request, HttpServletResponse response) {
        return wwrzTestApplyService.getAllProblemList(request);
    }
    @RequestMapping("exportExcel")
    public void exportExcel(HttpServletRequest request, HttpServletResponse response) {
        wwrzTestApplyService.exportProblemListExcel(request, response);
    }

    @RequestMapping("exportApplyExcel")
    public void exportApplyExcel(HttpServletRequest request, HttpServletResponse response) {
        wwrzTestApplyService.exportBaseInfoExcel(request, response);
    }

    @RequestMapping("checkNote")
    @ResponseBody
    public JsonResult checkNote(HttpServletRequest request, HttpServletResponse response) {
        String id = RequestUtil.getString(request, "id");
        return wwrzTestApplyService.checkNote(id);
    }
}
