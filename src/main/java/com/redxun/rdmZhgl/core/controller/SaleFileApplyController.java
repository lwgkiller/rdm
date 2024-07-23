
package com.redxun.rdmZhgl.core.controller;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.manager.MybatisBaseManager;
import com.redxun.core.util.StringUtil;
import com.redxun.org.api.model.IUser;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmZhgl.core.dao.SaleFileApplyDao;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.SaleFileApplyService;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.controller.MybatisListController;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.sys.core.util.SysPropertiesUtil;
import com.redxun.sys.org.dao.OsGroupDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

/**
 * 售前文件
 * 
 * @author zz
 */
@Controller
@RequestMapping("/rdmZhgl/core/saleFile/")
public class SaleFileApplyController extends MybatisListController {
    @Resource
    private CommonBpmManager commonBpmManager;
    @Resource
    private SaleFileApplyService saleFileApplyService;
    @Resource
    private SaleFileApplyDao saleFileApplyDao;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Resource
    private CommonInfoManager commonInfoManager;

    @SuppressWarnings("rawtypes")
    @Override
    public MybatisBaseManager getBaseManager() {
        return null;
    }

    /**
     * 审批列表页面
     */
    @RequestMapping("getListPage")
    public ModelAndView getListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/saleFileApplyList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        IUser currentUser = ContextUtil.getCurrentUser();
        String applyType = request.getParameter("applyType");
        mv.addObject("applyType", applyType);
        mv.addObject("currentUser", currentUser);
        return mv;
    }

    /**
     * 获取审批页面
     */
    @RequestMapping("getEditPage")
    public ModelAndView getEditPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/saleFileApplyEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String taskId_ = RequestUtil.getString(request, "taskId_");
        String nodeId = RequestUtil.getString(request, "nodeId");
        String id = RequestUtil.getString(request, "id", "");
        String applyType = RequestUtil.getString(request, "applyType");
        mv.addObject("taskId_", taskId_);
        // 新增或编辑的时候没有nodeId
        String action = "";
        if (StringUtils.isBlank(nodeId) || nodeId.contains("PROCESS")) {
            action = "edit";
        } else {
            action = "task";
        }
        // 取出节点变量，返回到页面
        if (StringUtils.isNotBlank(nodeId)) {
            mv.addObject("nodeId", nodeId);
            List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId, "SQWJSP", null);
            if (nodeVars != null && !nodeVars.isEmpty()) {
                mv.addObject("nodeVars", JSONObject.toJSONString(nodeVars));
            }
        }
        mv.addObject("action", action);
        Map<String, Object> planApplyObj = null;
        JSONObject applyObj = new JSONObject();
        if (StringUtils.isNotBlank(id)) {
            planApplyObj = saleFileApplyDao.getObjectById(id);
            applyObj = XcmgProjectUtil.convertMap2JsonObject(planApplyObj);
        } else {
            applyObj.put("editorUserId", ContextUtil.getCurrentUserId());
            applyObj.put("editorUserName", ContextUtil.getCurrentUser().getFullname());
            applyObj.put("editorUserDeptId", ContextUtil.getCurrentUser().getMainGroupId());
            applyObj.put("editorUserDeptName", ContextUtil.getCurrentUser().getMainGroupName());
            applyObj.put("applyType", applyType);
        }
        mv.addObject("applyId", id);
        mv.addObject("applyObj", applyObj);
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        // 判断当前登录人的部门是挖机营销公司、挖机海外营销公司还是挖掘机械研究院下属的部门
        String userRole = "";
        String currentUserDeptId = ContextUtil.getCurrentUser().getMainGroupId();
        if (commonInfoManager.queryWhetherUnderDept(currentUserDeptId, RdmConst.GNYXGS_NAME)) {
            userRole = "gnyx";
        } else if (commonInfoManager.queryWhetherUnderDept(currentUserDeptId, RdmConst.HWYXGS_NAME)) {
            userRole = "hwyx";
        } else if (commonInfoManager.queryWhetherUnderDept(currentUserDeptId, RdmConst.JSZX_NAME)) {
            userRole = "yjy";
        }
        mv.addObject("userRole", userRole);
        return mv;
    }

    /**
     * 获取list
     */
    @RequestMapping("queryList")
    @ResponseBody
    public JsonPageResult<?> queryList(HttpServletRequest request, HttpServletResponse response) {
        return saleFileApplyService.queryList(request, response);
    }

    /**
     * 项目作废表单和流程删除
     */
    @RequestMapping("delete")
    @ResponseBody
    public JsonResult delete(HttpServletRequest request, HttpServletResponse response) {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String instIdStr = RequestUtil.getString(request, "instIds");
            if (StringUtils.isNotEmpty(uIdStr) && StringUtils.isNotEmpty(instIdStr)) {
                String[] ids = uIdStr.split(",");
                String[] instIds = instIdStr.split(",");
                return saleFileApplyService.delete(ids, instIds);
            }
        } catch (Exception e) {
            return new JsonResult(false, e.getMessage());
        }
        return new JsonResult(true, "成功删除!");
    }

    /**
     * 明细：查看审批信息
     */
    @RequestMapping("edit")
    public ModelAndView edit(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = RequestUtil.getString(request, "id");
        String action = RequestUtil.getString(request, "action");
        String status = RequestUtil.getString(request, "status");
        String applyType = RequestUtil.getString(request, "applyType");
        String jspPath = "rdmZhgl/core/saleFileApplyEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("id", id).addObject("action", action).addObject("status", status);
        JSONObject applyObj = new JSONObject();
        if (StringUtils.isNotBlank(id)) {
            Map<String, Object> abolishApplyObj = saleFileApplyDao.getObjectById(id);
            applyObj = XcmgProjectUtil.convertMap2JsonObject(abolishApplyObj);
        }
        mv.addObject("applyObj", applyObj);
        applyObj.put("applyType", applyType);
        mv.addObject("applyId", id);
        mv.addObject("action", action);
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

    @RequestMapping(value = "fileUpload")
    @ResponseBody
    public Map<String, Object> fileUpload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        try {
            // 先保存文件，成功后再写入数据库，前台是一个一个文件的调用
            saleFileApplyService.saveUploadFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            logger.error("Exception in upload", e);
            modelMap.put("success", "false");
        }
        return modelMap;
    }

    @RequestMapping("saleFileList")
    @ResponseBody
    public List<JSONObject> getSaleFileList(HttpServletRequest request, HttpServletResponse response) {
        return saleFileApplyService.getSaleFileListByApplyId(request);
    }

    @RequestMapping("delSaleFile")
    public void delSaleFile(HttpServletRequest request, HttpServletResponse response, @RequestBody String postBody) {
        JSONObject postBodyObj = JSONObject.parseObject(postBody);
        String fileName = postBodyObj.getString("fileName");
        String fileId = postBodyObj.getString("id");
        String applyId = postBodyObj.getString("applyId");
        saleFileApplyService.deleteOneSaleFile(fileId, fileName, applyId);
    }

    @RequestMapping("pdfPreview")
    public ResponseEntity<byte[]> pdfPreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String applyId = RequestUtil.getString(request, "formId");
        String fileBasePath = SysPropertiesUtil.getGlobalProperty("saleFileUrl");
        return rdmZhglFileManager.pdfPreviewOrDownLoad(fileName, fileId, applyId, fileBasePath);
    }

    // ..
    @RequestMapping("officePreview")
    @ResponseBody
    public void officePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String applyId = RequestUtil.getString(request, "formId");
        String fileBasePath = SysPropertiesUtil.getGlobalProperty("saleFileUrl");
        rdmZhglFileManager.officeFilePreview(fileName, fileId, applyId, fileBasePath, response);
    }

    // ..
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
     */
    @RequestMapping("saleFileListPage")
    public ModelAndView getSaleFileListPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "rdmZhgl/core/saleFilesList.jsp";
        String applyType = request.getParameter("applyType");

        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("applyType", applyType);
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        return mv;
    }

    @RequestMapping("filesDetail")
    public ModelAndView editCheckD(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "rdmZhgl/core/saleFileDetail.jsp";
        String designModel = request.getParameter("designModel");
        String saleModel = request.getParameter("saleModel");
        String fileType = request.getParameter("fileType");
        String language = request.getParameter("language");
        ModelAndView mv = new ModelAndView(jspPath);
        String action = RequestUtil.getString(request, "action");
        mv.addObject("action", action);
        mv.addObject("designModel", designModel);
        mv.addObject("saleModel", saleModel);
        mv.addObject("fileType", fileType);
        mv.addObject("language", language);
        mv.addObject("currentUser", ContextUtil.getCurrentUser());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        return mv;
    }

    /**
     * 获取列表数据
     */
    @RequestMapping("saleFiles")
    @ResponseBody
    public JsonPageResult<?> getSaleFiles(HttpServletRequest request, HttpServletResponse response) {
        return saleFileApplyService.getSaleFileList(request);
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

    @RequestMapping("checkApplyPermition")
    @ResponseBody
    public JsonResult checkEditPermition(HttpServletRequest request, HttpServletResponse response) {
        JsonResult result = new JsonResult(false, "该型号已存在对应文件类型语种的申请单,如需改动请提交变更申请单！");
        String saleModel = RequestUtil.getString(request, "saleModel", "");
        String designModel = RequestUtil.getString(request, "designModel", "");
        String language = RequestUtil.getString(request, "language", "");
        String fileType = RequestUtil.getString(request, "fileType", "");
        String id = RequestUtil.getString(request, "id", "");
        JSONObject params = new JSONObject();
        params.put("language", language);
        params.put("saleModel", saleModel);
        params.put("designModel", designModel);
        params.put("fileType", fileType);
        if (StringUtil.isNotEmpty(id)) {
            params.put("id", id);
        }
        // 查是否有重复的，没重复返回True【列表为空】
        List<JSONObject> paramList = saleFileApplyDao.checkApplyPermition(params);
        if (paramList.size() == 0) {
            result.setSuccess(true);
            result.setMessage("");
        }
        return result;
    }

    /**
     * @mh 2023年4月18日09:15:19 用设计型号返回销售型号字符串
     * @param: request里需要包含
     *             designModel关键字 可以是单个也可以是多个
     * @return : 返回拼接后的字符串 注: 当前未用到此接口，直接写到Service中了 可以作为通用设计型号查销售区域号接口
     */
    @RequestMapping("getRegionFromSpectrum")
    @ResponseBody
    public String getRegionFromSpectrum(HttpServletRequest request, HttpServletResponse response) {
        String designModel = RequestUtil.getString(request, "designModel", "");
        if (StringUtils.isEmpty(designModel)) {
            return "";
        }
        String res = saleFileApplyService.getRegionFromSpectrum(designModel);
        return res;
    }

}
