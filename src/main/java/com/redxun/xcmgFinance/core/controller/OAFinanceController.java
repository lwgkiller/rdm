package com.redxun.xcmgFinance.core.controller;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.OfficeDocPreview;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.serviceEngineering.core.controller.WrongPartsController;
import com.redxun.xcmgFinance.core.dao.OAFinanceDao;
import com.redxun.xcmgFinance.core.manager.OAFinanceManager;
import com.redxun.xcmgFinance.core.wsdl.AttachmentForm;
import com.redxun.xcmgFinance.core.wsdl.IXcmgReviewWebserviceService;
import com.redxun.xcmgFinance.core.wsdl.IXcmgReviewWebserviceServiceServiceLocator;
import com.redxun.xcmgFinance.core.wsdl.XcmgReviewUpdateParamterForm;
import com.redxun.xcmgbudget.core.manager.BudgetMonthFlowManager;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.rpc.ServiceException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/oa/oaFinance")
public class OAFinanceController {
    private static final Logger logger = LoggerFactory.getLogger(OAFinanceController.class);

    @Autowired
    private BudgetMonthFlowManager budgetMonthFlowManager;
    @Autowired
    private OAFinanceManager oaFinanceManager;

    @RequestMapping("/listPage")
    public ModelAndView listPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "xcmgFinance/core/oaFinanceList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        JSONObject userRole = budgetMonthFlowManager.queryUserRoles(ContextUtil.getCurrentUserId());
        mv.addObject("currentUserRoles", userRole);
        return mv;
    }

    @RequestMapping("/editPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "xcmgFinance/core/oaFinanceEdit.jsp";
        String id = RequestUtil.getString(request, "id", "");
        String oaFlowId = RequestUtil.getString(request, "oaFlowId", "");
        String action = RequestUtil.getString(request, "action", "");
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        JSONObject userRole = budgetMonthFlowManager.queryUserRoles(ContextUtil.getCurrentUserId());
        mv.addObject("currentUserRoles", userRole);
        mv.addObject("id", id);
        mv.addObject("oaFlowId", oaFlowId);
        mv.addObject("action", action);
        return mv;
    }

    @RequestMapping("/queryList")
    @ResponseBody
    public JsonPageResult<?> queryList(HttpServletRequest request, HttpServletResponse response) {
        return oaFinanceManager.queryOAFinanceFlowList(request, response);
    }

    @RequestMapping("/getJson")
    @ResponseBody
    public JSONObject getFinanceJson(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject OAFinanceList = new JSONObject();
        String oaFlowId = RequestUtil.getString(request, "oaFlowId");
        if (StringUtils.isNotBlank(oaFlowId)) {
            OAFinanceList = oaFinanceManager.getDetailJson(oaFlowId);
        }
        return OAFinanceList;
    }

    @RequestMapping(value = "saveForm", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult saveForm(HttpServletRequest request, @RequestBody String businessDataStr,
        HttpServletResponse response) throws IOException, ServiceException {
        JsonResult result = new JsonResult(true, "保存成功");
        String oaFlowId = RequestUtil.getString(request, "oaFlowId");
        String filePath = RequestUtil.getString(request, "filePath");
        oaFinanceManager.saveForm(result, businessDataStr, oaFlowId, filePath);
        return result;
    }

    @RequestMapping(value = "saveFormToOA", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult saveFormToOA(HttpServletRequest request, @RequestBody String businessDataStr,
        HttpServletResponse response) throws IOException, ServiceException {
        JsonResult result = new JsonResult(true, "保存成功");
        String oaFlowId = RequestUtil.getString(request, "oaFlowId");
        String filePath = RequestUtil.getString(request, "filePath");
        oaFinanceManager.saveFormToOA(result, businessDataStr, oaFlowId, filePath);
        return result;
    }

    @RequestMapping(value = "nextPoint", method = RequestMethod.GET)
    @ResponseBody
    public JsonResult nextPoint(HttpServletRequest request, HttpServletResponse response)
        throws ServiceException, RemoteException {
        JsonResult result = new JsonResult(true, "执行成功");
        String oaFlowId = RequestUtil.getString(request, "oaFlowId");
        oaFinanceManager.nextPoint(result, oaFlowId);
        return result;
    }

    @RequestMapping("/oaFileList")
    @ResponseBody
    public List<JSONObject> oaFileList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String oaFlowId = RequestUtil.getString(request, "oaFlowId");
        if (StringUtils.isBlank(oaFlowId)) {
            logger.error("oaFlowId is blank");
            return null;
        }
        List<JSONObject> fileInfos = oaFinanceManager.queryOAFileList(oaFlowId);
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

    @RequestMapping("/openOAUploadWindow")
    public ModelAndView fileUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "xcmgFinance/core/oaFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("oaFlowId", RequestUtil.getString(request, "oaFlowId"));
        // 查询附件类型
        return mv;
    }

    @RequestMapping(value = "oaUpload")
    @ResponseBody
    public Map<String, Object> upload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        try {
            // 先保存文件到磁盘，成功后再写入数据库，前台是一个一个文件的调用
            JSONObject param = oaFinanceManager.saveUploadFiles(request);
            // oaFinanceManager.sendFile2OA(param);
            modelMap.put("success", "true");
        } catch (Exception e) {
            logger.error("Exception in upload", e);
            modelMap.put("success", "false");
        }
        return modelMap;
    }

    @RequestMapping(value = "addToOA")
    @ResponseBody
    public Map<String, Object> addToOA(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        try {
            // 先保存文件到磁盘，成功后再写入数据库，前台是一个一个文件的调用
            oaFinanceManager.sendFile2OA(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            logger.error("Exception in upload", e);
            modelMap.put("success", "false");
        }
        return modelMap;
    }

    // 删除某个文件（从文件表和磁盘上都删除）
    @RequestMapping("/delOAUploadFile")
    public void delete(HttpServletRequest request, HttpServletResponse response, @RequestBody String postBody) {
        JSONObject postBodyObj = JSONObject.parseObject(postBody);
        String fileName = postBodyObj.getString("fileName");
        String id = postBodyObj.getString("id");
        String oaFlowId = postBodyObj.getString("formId");
        oaFinanceManager.delOAUploadFile(id, fileName, oaFlowId);
    }

    // 文档的下载（预览pdf也调用这里）
    @RequestMapping("/oaFileDownload")
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
            String oaFlowId = RequestUtil.getString(request, "formId");
            if (StringUtils.isBlank(oaFlowId)) {
                logger.error("操作失败，OA流程ID为空！");
                return null;
            }
            String fileBasePath = WebAppUtil.getProperty("oaFinanceFilePathBase");
            if (StringUtils.isBlank(fileBasePath)) {
                logger.error("操作失败，找不到存储路径");
                return null;
            }
            String suffix = CommonFuns.toGetFileSuffix(fileName);
            String fullFilePath = fileBasePath + File.separator + oaFlowId + File.separator + fileId + "." + suffix;
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

    @RequestMapping("/oaOfficePreview")
    @ResponseBody
    public void officePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        if (StringUtils.isBlank(fileName)) {
            logger.error("操作失败，文件名为空！");
            return;
        }
        String fileId = RequestUtil.getString(request, "fileId");
        if (StringUtils.isBlank(fileId)) {
            logger.error("操作失败，文件Id为空！");
            return;
        }
        String oaFlowId = RequestUtil.getString(request, "formId");
        if (StringUtils.isBlank(oaFlowId)) {
            logger.error("操作失败，OA流程ID为空！");
            return;
        }

        String fileBasePath = WebAppUtil.getProperty("oaFinanceFilePathBase");
        if (StringUtils.isBlank(fileBasePath)) {
            logger.error("操作失败，找不到存储路径");
            return;
        }
        String suffix = CommonFuns.toGetFileSuffix(fileName);
        String originalFilePath = fileBasePath + File.separator + oaFlowId + File.separator + fileId + "." + suffix;
        String convertPdfDir = WebAppUtil.getProperty("convertPdfDir");
        String convertPdfPath = fileBasePath + File.separator + oaFlowId + File.separator + convertPdfDir
            + File.separator + fileId + ".pdf";;
        OfficeDocPreview.previewOfficeDoc(originalFilePath, convertPdfPath, response);
    }

    @RequestMapping("/oaImagePreview")
    @ResponseBody
    public void imagePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        if (StringUtils.isBlank(fileName)) {
            logger.error("操作失败，文件名为空！");
            return;
        }
        String fileId = RequestUtil.getString(request, "fileId");
        if (StringUtils.isBlank(fileId)) {
            logger.error("操作失败，文件Id为空！");
            return;
        }
        String oaFlowId = RequestUtil.getString(request, "formId");
        if (StringUtils.isBlank(oaFlowId)) {
            logger.error("操作失败，OA流程ID为空！");
            return;
        }

        String fileBasePath = WebAppUtil.getProperty("oaFinanceFilePathBase");
        if (StringUtils.isBlank(fileBasePath)) {
            logger.error("操作失败，找不到存储路径");
            return;
        }
        String suffix = CommonFuns.toGetFileSuffix(fileName);
        String originalFilePath = fileBasePath + File.separator + oaFlowId + File.separator + fileId + "." + suffix;
        OfficeDocPreview.imagePreview(originalFilePath, response);
    }

    @RequestMapping("/add1")
    @ResponseBody
    public String indexAdd1() throws ServiceException, IOException {
        IXcmgReviewWebserviceServiceServiceLocator locator = new IXcmgReviewWebserviceServiceServiceLocator();
        IXcmgReviewWebserviceService ss = locator.getIXcmgReviewWebserviceServicePort();
        // 构建参数
        XcmgReviewUpdateParamterForm form = new XcmgReviewUpdateParamterForm();
        form.setFdId("185a9d0166a439cc3e2fcfb4ec89ac8a");
        String paramJson = "{\n" + "            \t\"fd_3b63f7c71810f0.fd_3b6d3205b2dc20\":[\"ASDF\",\"JKL\"],\n"
            + "            \t\"fd_3b63f7c71810f0.fd_3b6d32073f8386\":[\"50\",\"210\"]}";
        form.setFormValues(paramJson);

        AttachmentForm attachmentForm = new AttachmentForm();
        attachmentForm.setFdKey("fd_3b63f932d3a132");
        attachmentForm.setFdFileName("et.png");
        File file = new File("F:\\a\\te.png");
        DataSource dataSource = new FileDataSource(file);
        DataHandler dataHandler = new DataHandler(dataSource);
        byte[] b = new byte[dataHandler.getInputStream().available()];
        attachmentForm.setFdAttachment(b);

        form.setAttachmentForms(new AttachmentForm[] {attachmentForm});
        ss.updateFormData(form);

        System.out.println("success");
        return "success";
    }

}
