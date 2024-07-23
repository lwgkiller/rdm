
package com.redxun.gkgf.core.controller;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.gkgf.core.dao.GkgfApplyDao;
import com.redxun.gkgf.core.manager.GkgfApplyManager;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.manager.MybatisBaseManager;
import com.redxun.core.util.DateUtil;
import com.redxun.org.api.model.IUser;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmZhgl.core.dao.SaleFileApplyDao;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.SaleFileApplyService;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.controller.MybatisListController;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.sys.core.util.SysPropertiesUtil;
import com.redxun.sys.org.dao.OsGroupDao;
import com.redxun.sys.org.entity.OsGroup;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

/**
 *工况工法分析申请
 * @author zz
 */
@Controller
@RequestMapping("/gkgf/core/apply/")
public class gkgfApplyController extends MybatisListController {
    @Resource
    private CommonBpmManager commonBpmManager;
    @Resource
    private GkgfApplyManager gkgfApplyManager;
    @Resource
    private GkgfApplyDao gkgfApplyDao;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
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
        String jspPath = "gkgf/core/gkgfApplyList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        IUser currentUser = ContextUtil.getCurrentUser();
        String applyType = RequestUtil.getString(request, "applyType");
        mv.addObject("currentUser", currentUser);
        mv.addObject("applyType", applyType);
        return mv;
    }

   /**
    * 获取审批页面
    * */
    @RequestMapping("editPage")
    public ModelAndView getEditPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "gkgf/core/gkgfApplyEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String taskId_ = RequestUtil.getString(request, "taskId_");
        String nodeId = RequestUtil.getString(request, "nodeId");
        String id = RequestUtil.getString(request, "id");
        String applyType = RequestUtil.getString(request, "applyType");
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
            List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId,"GKGFFlow",null);
            if (nodeVars != null && !nodeVars.isEmpty()) {
                mv.addObject("nodeVars", JSONObject.toJSONString(nodeVars));
            }
        }
        mv.addObject("action", action);
        Map<String, Object> planApplyObj = null;
        JSONObject applyObj = new JSONObject();
        if (StringUtils.isNotBlank(id)) {
            planApplyObj = gkgfApplyDao.getObjectById(id);
            applyObj = XcmgProjectUtil.convertMap2JsonObject(planApplyObj);
        }else{
            applyObj.put("CREATE_BY_",ContextUtil.getCurrentUserId());
            applyObj.put("userName",ContextUtil.getCurrentUser().getFullname());
            applyObj.put("deptId",ContextUtil.getCurrentUser().getMainGroupId());
            applyObj.put("deptName",ContextUtil.getCurrentUser().getMainGroupName());
        }
        applyObj.put("applyType",applyType);
        mv.addObject("applyId", id);
        mv.addObject("applyObj", applyObj);
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        mv.addObject("CREATE_BY_", ContextUtil.getCurrentUserId());
        mv.addObject("userName", ContextUtil.getCurrentUser().getFullname());
        return mv;
    }
    /**
     * 获取list
     * */
    @RequestMapping("queryList")
    @ResponseBody
    public JsonPageResult<?> queryList(HttpServletRequest request, HttpServletResponse response) {
        return gkgfApplyManager.queryList(request, response);
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
                return gkgfApplyManager.delete(ids, instIds);
            }
        } catch (Exception e) {
            return new JsonResult(false, e.getMessage());
        }
        return new JsonResult(true, "成功删除!");
    }
    @RequestMapping(value = "items")
    @ResponseBody
    public List<JSONObject> getItemList(HttpServletRequest request, HttpServletResponse response)  {
        return gkgfApplyManager.getItemList(request);
    }
    /**
     * 明细：查看审批信息
     * */
    @RequestMapping("edit")
    public ModelAndView edit(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = RequestUtil.getString(request, "id");
        String action = RequestUtil.getString(request, "action");
        String status = RequestUtil.getString(request, "status");
        String applyType = RequestUtil.getString(request, "applyType");
        String jspPath = "gkgf/core/gkgfApplyEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("id", id).addObject("action", action).addObject("status", status);
        Map<String, Object> abolishApplyObj = null;
        JSONObject applyObj = new JSONObject();
        if (StringUtils.isNotBlank(id)) {
            abolishApplyObj = gkgfApplyDao.getObjectById(id);
            applyObj = XcmgProjectUtil.convertMap2JsonObject(abolishApplyObj);
        }
        mv.addObject("applyObj", applyObj);
        applyObj.put("applyType",applyType);
        mv.addObject("applyId", id);
        mv.addObject("action", action);
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        return mv;
    }
    @RequestMapping("fileWindow")
    public ModelAndView workPlanFileWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "gkgf/core/gkgfFileList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String detailId = RequestUtil.getString(request, "detailId");
        mv.addObject("detailId", detailId);
        String fileType = RequestUtil.getString(request, "fileType");
        Boolean editable = RequestUtil.getBoolean(request, "editable");
        mv.addObject("fileType", fileType);
        mv.addObject("editable", editable);
        mv.addObject("currentUser", ContextUtil.getCurrentUser());
        return mv;
    }
    @RequestMapping("fileUploadWindow")
    public ModelAndView fileUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "gkgf/core/gkgfFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("detailId", RequestUtil.getString(request, "detailId", ""));
        mv.addObject("fileType", RequestUtil.getString(request, "fileType", ""));
        return mv;
    }
    @RequestMapping(value = "fileUpload")
    @ResponseBody
    public Map<String, Object> fileUpload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        try {
            // 先保存文件，成功后再写入数据库，前台是一个一个文件的调用
            gkgfApplyManager.saveUploadFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            logger.error("Exception in upload", e);
            modelMap.put("success", "false");
        }
        return modelMap;
    }
    @RequestMapping("fileList")
    @ResponseBody
    public List<JSONObject> getFileList(HttpServletRequest request, HttpServletResponse response) {
        return gkgfApplyManager.getFileListByApplyId(request);
    }
    @RequestMapping("delFile")
    public void delFile(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "id");
        String detailId = RequestUtil.getString(request, "detailId");
        gkgfApplyManager.deleteOneSaleFile(fileId, fileName, detailId);
    }

    @RequestMapping("pdfPreview")
    public ResponseEntity<byte[]> pdfPreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String applyId = RequestUtil.getString(request, "formId");
        String fileBasePath = SysPropertiesUtil.getGlobalProperty("saleFileUrl");
        return rdmZhglFileManager.pdfPreviewOrDownLoad(fileName, fileId, applyId, fileBasePath);
    }

    //..
    @RequestMapping("officePreview")
    @ResponseBody
    public void officePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String applyId = RequestUtil.getString(request, "formId");
        String fileBasePath = SysPropertiesUtil.getGlobalProperty("saleFileUrl");
        rdmZhglFileManager.officeFilePreview(fileName, fileId, applyId, fileBasePath, response);
    }

    //..
    @RequestMapping("imagePreview")
    @ResponseBody
    public void imagePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String applyId = RequestUtil.getString(request, "formId");
        String fileBasePath = SysPropertiesUtil.getGlobalProperty("saleFileUrl");
        rdmZhglFileManager.imageFilePreview(fileName, fileId, applyId, fileBasePath, response);
    }
    /**
     * 获取文件列表页面
     * */
    @RequestMapping("detailListPage")
    public ModelAndView getDetailListPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "gkgf/core/gkgfDetailList.jsp";
        String applyType = request.getParameter("applyType");
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("applyType",applyType);
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        return mv;
    }
    @RequestMapping("listDetailData")
    @ResponseBody
    public JsonPageResult<?> getListDetailData(HttpServletRequest request, HttpServletResponse response) {
        return gkgfApplyManager.getDetailList(request);
    }
    /**
     * 获取列表数据
     * */
    @RequestMapping("files")
    @ResponseBody
    public JsonPageResult<?> getFiles(HttpServletRequest request, HttpServletResponse response) {
        return gkgfApplyManager.getFileList(request);
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

    /**
     * 预算类模板下载
     * */
    @RequestMapping("/templateDownload")
    public ResponseEntity<byte[]> templateDownload(HttpServletRequest request, HttpServletResponse response) {
        return gkgfApplyManager.templateDownload();
    }
    /**
     * 批量导入
     * */
    @RequestMapping("importExcel")
    @ResponseBody
    public JSONObject importExcel(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        gkgfApplyManager.importExcel(result, request);
        return result;
    }
}
