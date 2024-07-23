package com.redxun.matPriceReview.core.controller;

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
import com.redxun.core.json.JsonPageResult;
import com.redxun.matPriceReview.core.dao.MatPriceReviewDao;
import com.redxun.matPriceReview.core.service.MatPriceReviewService;
import com.redxun.materielextend.core.util.ResultData;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.sys.org.entity.OsUser;
import com.redxun.sys.org.manager.OsUserManager;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

/**
 * 应用模块名称
 * <p>
 * 代码描述
 * <p>
 * Copyright: Copyright (C) 2020 XXX, Inc. All rights reserved.
 * <p>
 * Company: 徐工挖掘机械有限公司
 * <p>
 *
 * @author liangchuanjiang
 * @since 2020/6/2 17:40
 */
@Controller
@RequestMapping("/matPriceReview/core")
public class MatPriceReviewController {
    private static final Logger logger = LoggerFactory.getLogger(MatPriceReviewController.class);
    @Autowired
    private MatPriceReviewService matPriceReviewService;
    @Autowired
    private OsUserManager osUserManager;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private MatPriceReviewDao matPriceReviewDao;

    @RequestMapping("/listPage")
    public ModelAndView applyListPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "matPriceReview/matPriceReviewList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("reviewCategory", RequestUtil.getString(request, "reviewCategory", "common"));
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());

        return mv;
    }

    @RequestMapping("/getReviewList")
    @ResponseBody
    public JsonPageResult<?> getReviewList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return matPriceReviewService.getReviewList(request, response, true);
    }

    @RequestMapping("/applyDelete")
    @ResponseBody
    public ResultData applyDelete(HttpServletRequest request, HttpServletResponse response) {
        String ids = RequestUtil.getString(request, "ids");
        List<String> applyIds = new ArrayList<>();
        if (StringUtils.isNotBlank(ids)) {
            applyIds.addAll(Arrays.asList(ids.split(",", -1)));
        }
        return matPriceReviewService.applyDelete(applyIds);
    }

    @RequestMapping("/editPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "matPriceReview/matPriceReviewEditCommon.jsp";
        String reviewCategory = RequestUtil.getString(request, "reviewCategory", "common");
        String action = RequestUtil.getString(request, "action", "");
        String id = RequestUtil.getString(request, "id", "");
        if (reviewCategory.equalsIgnoreCase("disposable")) {
            jspPath = "matPriceReview/matPriceReviewEditDisposable.jsp";
        } else if (reviewCategory.equalsIgnoreCase("emergency")) {
            jspPath = "matPriceReview/matPriceReviewEditEmergency.jsp";
        }
        JSONObject matPriceReviewObj = new JSONObject();
        if (StringUtils.isNotBlank(id)) {
            matPriceReviewObj = matPriceReviewService.getObjDetailById(id);
        } else {
            OsUser currentUser = osUserManager.get(ContextUtil.getCurrentUserId());
            matPriceReviewObj.put("applyDeptId", ContextUtil.getCurrentUser().getMainGroupId());
            matPriceReviewObj.put("applyDeptName", ContextUtil.getCurrentUser().getMainGroupName());
            matPriceReviewObj.put("applyUserId", ContextUtil.getCurrentUserId());
            matPriceReviewObj.put("applyUserName", ContextUtil.getCurrentUser().getFullname());
            matPriceReviewObj.put("reviewCategory", reviewCategory);
            matPriceReviewObj.put("applyUserMobile", currentUser.getMobile());
            matPriceReviewObj.put("applyUserNo",ContextUtil.getCurrentUser().getUserNo());
        }

        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("action", action);
        mv.addObject("matPriceReviewObj", matPriceReviewObj);
        mv.addObject("matPriceReviewObjId", id);
        return mv;
    }

    @RequestMapping("/applySaveOrCommit")
    @ResponseBody
    public ResultData applySaveOrCommit(HttpServletRequest request, @RequestBody String requestBody,
        HttpServletResponse response) {
        if (StringUtils.isBlank(requestBody)) {
            logger.error("applySaveOrCommit failed, postBody is blank!");
            ResultData resultData = new ResultData();
            resultData.setSuccess(false);
            resultData.setMessage("操作失败，发送内容为空！");
            return resultData;
        }
        String scene = RequestUtil.getString(request, "scene", "save");
        return matPriceReviewService.applySaveOrCommit(requestBody, scene);
    }

    @RequestMapping("/fileListWindow")
    public ModelAndView zlgjFileWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "matPriceReview/matPriceReviewFileList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String formId = RequestUtil.getString(request, "formId");
        String canOperateFile = RequestUtil.getString(request, "canOperateFile");
        mv.addObject("formId", formId);
        mv.addObject("canOperateFile", canOperateFile);
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        return mv;
    }

    @RequestMapping("/openUploadWindow")
    public ModelAndView openUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "matPriceReview/matPriceReviewUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        return mv;
    }

    @RequestMapping(value = "/upload")
    @ResponseBody
    public Map<String, Object> upload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>(16);
        try {
            // 先保存文件到磁盘，成功后再写入数据库，前台是一个一个文件的调用
            matPriceReviewService.saveUploadFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return modelMap;
    }

    @RequestMapping("/files")
    @ResponseBody
    public List<JSONObject> getFiles(HttpServletRequest request, HttpServletResponse response) {
        List<JSONObject> fileInfos = matPriceReviewService.getFileListByFormId(request);
        return fileInfos;
    }

    @RequestMapping("/preview")
    public ResponseEntity<byte[]> zlgjPdfPreview(HttpServletRequest request, HttpServletResponse response) {
        ResponseEntity<byte[]> result = null;
        String fileType = RequestUtil.getString(request, "fileType");
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String fileBasePath = WebAppUtil.getProperty("jgspFilePathBase");
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
            String fileBasePath = WebAppUtil.getProperty("jgspFilePathBase");
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

    @RequestMapping("/deleteFiles")
    public void delJsjdsFile(HttpServletRequest request, HttpServletResponse response, @RequestBody String postBody) {
        JSONObject postBodyObj = JSONObject.parseObject(postBody);
        String fileName = postBodyObj.getString("fileName");
        String fileId = postBodyObj.getString("id");
        String formId = postBodyObj.getString("formId");
        Map<String, Object> param = new HashMap<>();
        param.put("id", fileId);
        matPriceReviewDao.deleteFiles(param);
        rdmZhglFileManager.deleteOneFileFromDisk(fileId, fileName, formId, WebAppUtil.getProperty("jgspFilePathBase"));
    }

    @RequestMapping("/getMatDetailList")
    @ResponseBody
    public List<JSONObject> getMatDetailList(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        return matPriceReviewService.getMatDetailList(request, response);
    }

    @RequestMapping("/matDetailSave")
    @ResponseBody
    public ResultData matDetailSave(HttpServletRequest request, @RequestBody String requestBody,
        HttpServletResponse response) {
        if (StringUtils.isBlank(requestBody)) {
            logger.error("matDetailSave failed, postBody is blank!");
            ResultData resultData = new ResultData();
            resultData.setSuccess(false);
            resultData.setMessage("操作失败，发送内容为空！");
            return resultData;
        }
        return matPriceReviewService.matDetailSave(requestBody);
    }

    @RequestMapping("/syncMatInfo")
    @ResponseBody
    public ResultData syncMatInfo(HttpServletRequest request, HttpServletResponse response) {
        String matCode = RequestUtil.getString(request, "matCode", "");
        if (StringUtils.isBlank(matCode)) {
            logger.error("syncMatInfo failed, matCode is blank!");
            ResultData resultData = new ResultData();
            resultData.setSuccess(false);
            resultData.setMessage("同步失败，物料号为空！");
            return resultData;
        }
        return matPriceReviewService.syncMatInfo(matCode);
    }

    @RequestMapping("/matDetailDelete")
    @ResponseBody
    public ResultData matDetailDelete(HttpServletRequest request, HttpServletResponse response) {
        String ids = RequestUtil.getString(request, "ids");
        List<String> idList = new ArrayList<>();
        if (StringUtils.isNotBlank(ids)) {
            idList.addAll(Arrays.asList(ids.split(",", -1)));
        }
        return matPriceReviewService.deleteMatDetailByIds(idList);
    }

    @RequestMapping("/getRecordList")
    @ResponseBody
    public List<JSONObject> getRecordList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return matPriceReviewService.getRecordList(request, response);
    }

    @RequestMapping("/recordEditPage")
    public ModelAndView recordEditPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "matPriceReview/matPriceReviewFjcRecordEdit.jsp";
        String jclx = RequestUtil.getString(request, "jclx", "fjc");
        String action = RequestUtil.getString(request, "action", "");
        String id = RequestUtil.getString(request, "id", "");
        String reviewId = RequestUtil.getString(request, "reviewId", "");
        if (!jclx.equalsIgnoreCase("fjc")) {
            jspPath = "matPriceReview/matPriceReviewJcRecordEdit.jsp";
        }
        JSONObject recordObj = new JSONObject();
        if (StringUtils.isNotBlank(id)) {
            recordObj = matPriceReviewService.getRecordDetailById(id);
        } else {
            recordObj.put("jclx", jclx);
            recordObj.put("reviewId", reviewId);
            // 查询申请单基本信息
            JSONObject matPriceReviewObj = matPriceReviewService.getObjDetailById(reviewId);
            // 按照集采和非集采，区分进行初始值的查询
            if (!jclx.equalsIgnoreCase("fjc")) {
                // 集采
                recordObj.put("cgzz", "2080");
                recordObj.put("cggs", "2080");
                recordObj.put("gc", "2080");
                recordObj.put("applierCode", matPriceReviewObj.getString("applierCode"));
                recordObj.put("applierName", matPriceReviewObj.getString("applierName"));
                recordObj.put("jldw", "EA");
                recordObj.put("jgdw", "1");
                recordObj.put("jgNumber", "1");
                recordObj.put("jbdw", "EA");
                recordObj.put("bizhong", StringUtils.isNotBlank(matPriceReviewObj.getString("moneyCategory"))
                    ? matPriceReviewObj.getString("moneyCategory") : "人民币");
                recordObj.put("shuima", "J5");
                recordObj.put("shuilv", "13");
                recordObj.put("cpf", "4040");
            } else {
                // 非集采
                recordObj.put("applierCode", matPriceReviewObj.getString("applierCode"));
                recordObj.put("applierName", matPriceReviewObj.getString("applierName"));
                recordObj.put("cgzz", "2080");
                recordObj.put("gc", "2080");
                recordObj.put("bizhong", StringUtils.isNotBlank(matPriceReviewObj.getString("moneyCategory"))
                    ? matPriceReviewObj.getString("moneyCategory") : "CNY");
                recordObj.put("jgdw", "1");
                recordObj.put("jldw", "EA");
                recordObj.put("shuima", "J5");
            }
        }

        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("action", action);
        mv.addObject("recordObj", recordObj);
        return mv;
    }

    @RequestMapping("/recordSave")
    @ResponseBody
    public ResultData recordSave(HttpServletRequest request, @RequestBody String requestBody,
        HttpServletResponse response) {
        if (StringUtils.isBlank(requestBody)) {
            logger.error("recordSave failed, postBody is blank!");
            ResultData resultData = new ResultData();
            resultData.setSuccess(false);
            resultData.setMessage("操作失败，发送内容为空！");
            return resultData;
        }
        return matPriceReviewService.recordSave(requestBody);
    }

    @RequestMapping("/recordDelete")
    @ResponseBody
    public ResultData recordDelete(HttpServletRequest request, HttpServletResponse response) {
        String ids = RequestUtil.getString(request, "ids");
        List<String> idList = new ArrayList<>();
        if (StringUtils.isNotBlank(ids)) {
            idList.addAll(Arrays.asList(ids.split(",", -1)));
        }
        return matPriceReviewService.deleteRecordByIds(idList);
    }

    /**
     * 根据申请单号、物料号和集采类型，依次从申请单、物料信息、扩充信息中获取需要的信息
     * 
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/syncRecordInfo")
    @ResponseBody
    public ResultData syncRecordInfo(HttpServletRequest request, HttpServletResponse response) {
        String matCode = RequestUtil.getString(request, "matCode", "");
        String reviewId = RequestUtil.getString(request, "reviewId", "");
        String jclx = RequestUtil.getString(request, "jclx", "");

        return matPriceReviewService.syncRecordInfo(matCode, reviewId, jclx);
    }

    @RequestMapping("/generateRecord")
    @ResponseBody
    public ResultData generateRecord(HttpServletRequest request, HttpServletResponse response) {
        String reviewId = RequestUtil.getString(request, "reviewId", "");
        String jclx = RequestUtil.getString(request, "jclx", "");

        return matPriceReviewService.generateRecord(reviewId, jclx);
    }

}
