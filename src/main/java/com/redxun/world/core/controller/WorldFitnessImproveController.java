package com.redxun.world.core.controller;

/**
 * 适应性改进
 * 
 * @author mh
 * @date 2022/4/19 10:40
 */

import java.io.File;
import java.util.*;

import javax.annotation.Resource;
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
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.world.core.dao.WorldFitnessImproveDao;
import com.redxun.world.core.service.WorldFitnessImproveManager;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

@Controller
@RequestMapping("/world/core/fitnessImprove/")
public class WorldFitnessImproveController {
    private static final Logger logger =
        LoggerFactory.getLogger(com.redxun.world.core.controller.WorldFitnessImproveController.class);
    @Autowired
    private CommonBpmManager commonBpmManager;
    @Autowired
    private WorldFitnessImproveManager worldFitnessImproveManager;
    @Resource
    private BpmInstManager bpmInstManager;

    @Autowired
    private WorldFitnessImproveDao worldFitnessImproveDao;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;

    @RequestMapping("applyListPage")
    public ModelAndView getListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String worldFitnessImproveList = "world/core/worldFitnessImproveList.jsp";
        ModelAndView mv = new ModelAndView(worldFitnessImproveList);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        return mv;
    }

    @RequestMapping("applyList")
    @ResponseBody
    public JsonPageResult<?> queryApplyList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return worldFitnessImproveManager.queryApplyList(request, true);
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
            return worldFitnessImproveManager.delApplys(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteApply", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping("applyEditPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "world/core/worldFitnessImproveEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        // 新增或者编辑页面由流程引擎跳转并替换url中的pk和nodeId_
        String applyId = RequestUtil.getString(request, "applyId", "");
        String nodeId = RequestUtil.getString(request, "nodeId", "");
        String action = RequestUtil.getString(request, "action", "");
        String instId = RequestUtil.getString(request, "instId", "");
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
            List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId, "HWJDSYXFK", null);
            if (nodeVars != null && !nodeVars.isEmpty()) {
                mv.addObject("nodeVars", JSONObject.toJSONString(nodeVars));
            }
        }
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        mv.addObject("applyId", applyId);
        mv.addObject("instId", instId);
        mv.addObject("action", action);
        mv.addObject("status", status);
        return mv;
    }

    @RequestMapping("getJson")
    @ResponseBody
    public JSONObject queryApplyDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = RequestUtil.getString(request, "id", "");
        if (StringUtils.isBlank(id)) {
            logger.error("id is blank");
            JSONObject result = new JSONObject();
            result.put("creatorName", ContextUtil.getCurrentUser().getFullname());
            result.put("CREATE_TIME_", DateUtil.formatDate(new Date(), DateUtil.DATE_FORMAT_FULL));
            return result;
        }
        return worldFitnessImproveManager.queryApplyDetail(id);
    }

    @RequestMapping("openFileWindow")
    @ResponseBody
    public ModelAndView openFileWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "world/core/worldFitnessFileList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String applyId = RequestUtil.getString(request, "applyId");
        String fileType = RequestUtil.getString(request, "fileType");
        String canEdit = RequestUtil.getString(request, "canEdit");
        String action = RequestUtil.getString(request, "action");

        mv.addObject("action", action);
        mv.addObject("applyId", applyId);
        mv.addObject("fileType", fileType);
        mv.addObject("canEdit", canEdit);
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));

        return mv;
    }

    @RequestMapping("openUploadWindow")
    public ModelAndView openUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "world/core/worldFitnessFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUser", ContextUtil.getCurrentUser());

        return mv;
    }

    @RequestMapping("upload")
    @ResponseBody
    public Map<String, Object> upload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        try {
            // 先保存文件到磁盘，成功后再写入数据库，前台是一个一个文件的调用
            worldFitnessImproveManager.saveUploadFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            logger.error("Exception in upload", e);
            modelMap.put("success", "false");
        }
        return modelMap;
    }

    @RequestMapping("getFileList")
    @ResponseBody
    public List<JSONObject> getFileList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String baseInfoId = RequestUtil.getString(request, "baseInfoId", "");
        String fileType = RequestUtil.getString(request, "fileType", "");
        if (StringUtils.isBlank(baseInfoId)) {
            logger.error("baseInfoId is blank");
            return null;
        }
        JSONObject params = new JSONObject();
        params.put("baseInfoId", baseInfoId);
        params.put("fileType", fileType);
        List<JSONObject> fileList = worldFitnessImproveManager.queryFileList(params);
        return fileList;
    }

    @RequestMapping("/preview")
    public ResponseEntity<byte[]> filePreview(HttpServletRequest request, HttpServletResponse response) {
        ResponseEntity<byte[]> result = null;
        String fileType = RequestUtil.getString(request, "fileType");
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("worldFitnessImproveFilePathBase");
        switch (fileType) {
            case "pdf":
                result = rdmZhglFileManager.pdfPreviewOrDownLoad(fileName, fileId, formId, fileBasePath);
            case "office":
                rdmZhglFileManager.officeFilePreview(fileName, fileId, formId, fileBasePath, response);
                break;
            case "pic":
                rdmZhglFileManager.imageFilePreview(fileName, fileId, formId, fileBasePath, response);
                break;
        }
        return result;
    }

    @RequestMapping("/fileDownload")
    public ResponseEntity<byte[]> fileDownload(HttpServletRequest request, HttpServletResponse response) {
        try {
            String fileName = RequestUtil.getString(request, "fileName");
            if (StringUtils.isBlank(fileName)) {
                logger.error("操作失败，文件名为空！");
                return null;
            }
            String fileId = RequestUtil.getString(request, "fileId");
            if (StringUtils.isBlank(fileId)) {
                logger.error("操作失败，文件主键为空！");
                return null;
            }
            String formId = RequestUtil.getString(request, "formId");
            String fileBasePath = WebAppUtil.getProperty("worldFitnessImproveFilePathBase");
            if (StringUtils.isBlank(fileBasePath)) {
                logger.error("操作失败，找不到存储根路径");
                return null;
            }
            String suffix = CommonFuns.toGetFileSuffix(fileName);
            String relativeFilePath = "";
            if (StringUtils.isNotBlank(formId)) {
                relativeFilePath = File.separator + formId;
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

    @RequestMapping("deleteFiles")
    public void deleteFiles(HttpServletRequest request, HttpServletResponse response, @RequestBody String postBody) {
        JSONObject postBodyObj = JSONObject.parseObject(postBody);
        // 删的时候根据 baseInfoId 和 fileType
        String fileId = postBodyObj.getString("id");
        if (StringUtils.isBlank(fileId)) {
            return;
        }
        String fileName = postBodyObj.getString("fileName");
        String formId = postBodyObj.getString("formId");
        JSONObject param = new JSONObject();
        param.put("ids", Arrays.asList(fileId));
        // todo 这个没删掉
        worldFitnessImproveDao.deleteApplyFile(param);
        String fileBasePath = WebAppUtil.getProperty("worldFitnessImproveFilePathBase");
        rdmZhglFileManager.deleteOneFileFromDisk(fileId, fileName, formId, fileBasePath);
    }

    @RequestMapping("checkFileList")
    @ResponseBody
    public boolean checkFileList(HttpServletRequest request, HttpServletResponse response) {
        String baseInfoId = RequestUtil.getString(request, "baseInfoId", "");
        String fileType = RequestUtil.getString(request, "fileType", "");
        if (StringUtils.isBlank(baseInfoId)) {
            logger.error("baseInfoId is blank");
            return false;
        }
        JSONObject params = new JSONObject();
        params.put("baseInfoId", baseInfoId);
        params.put("fileType", fileType);
        List<JSONObject> fileList = worldFitnessImproveManager.queryFileList(params);
        if (fileList.size() > 0) {
            return true;
        }
        return false;
    }

    @RequestMapping("statusChange")
    @ResponseBody
    public JsonResult statusChange(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String applyId = RequestUtil.getString(request, "applyId");
            String technical = RequestUtil.getString(request, "technical");
            return worldFitnessImproveManager.statusChange(applyId,technical);
        } catch (Exception e) {
            logger.error("Exception in statusChange", e);
            return new JsonResult(false, e.getMessage());
        }
    }
}
