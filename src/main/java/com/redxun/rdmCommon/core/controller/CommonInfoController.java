package com.redxun.rdmCommon.core.controller;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.standardManager.core.util.PdfWaterMarkProcess;
import com.redxun.standardManager.core.util.ResultUtil;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.standardManager.core.util.StandardConstant;
import com.redxun.sys.core.util.SysPropertiesUtil;
import org.apache.axis.transport.jms.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.util.StringUtil;
import com.redxun.org.api.service.UserService;

/**
 *
 *
 * @author zhangzhen
 */
@RestController
@RequestMapping("/sys/core/commonInfo/")
public class CommonInfoController {

    protected Logger logger = LogManager.getLogger(CommonInfoController.class);

    @Resource
    UserService userService;
    @Resource
    private CommonInfoManager commonInfoManager;


    @GetMapping("/getDicItem")
    public JSONObject getDicItem(@RequestParam(name = "dicType") String dicType) {
        String msg = "调用成功";
        if (StringUtil.isEmpty(dicType)) {
            msg = "字典类型不能为空！";
            return ResultUtil.result(HttpStatus.SC_BAD_REQUEST, msg, "list");
        }
        List list = commonInfoManager.getDicValues(dicType);

        return ResultUtil.result(HttpStatus.SC_OK, msg, list);
    }
    @GetMapping("/getYearList")
    public JSONObject getYearList(){
        Calendar cal = Calendar.getInstance();
        int currentYear = cal.get(Calendar.YEAR);
        JSONObject jsonObject;
        JSONArray jsonArray = new JSONArray();
        for(int i=0;i<5;i++){
            jsonObject = new JSONObject();
            jsonObject.put("value",currentYear-i);
            jsonObject.put("text",currentYear-i);
            jsonArray.add(jsonObject);
        }
        return ResultUtil.result(HttpStatus.SC_OK,"查询成功",jsonArray);
    }
    /**
     * 获取前5年后1年的年份列表
     * */
    @GetMapping("/getNearYearList")
    public JSONObject getNearYearList(){
        Calendar cal = Calendar.getInstance();
        int currentYear = cal.get(Calendar.YEAR);
        JSONObject jsonObject;
        JSONArray jsonArray = new JSONArray();
        for(int i=-1;i<5;i++){
            jsonObject = new JSONObject();
            jsonObject.put("value",currentYear-i);
            jsonObject.put("text",currentYear-i);
            jsonArray.add(jsonObject);
        }
        return ResultUtil.result(HttpStatus.SC_OK,"查询成功",jsonArray);
    }

    @GetMapping("getMessage")
    public JSONObject getMessage(@RequestParam(name = "dicType") String dicType,@RequestParam(name = "nodeName") String nodeName) {
        String msg = "调用成功";
        if (StringUtil.isEmpty(dicType)) {
            msg = "字典类型不能为空！";
            return ResultUtil.result(org.apache.http.HttpStatus.SC_BAD_REQUEST, msg, "list");
        }
        List list = commonInfoManager.getMessage(dicType,nodeName);
        JSONObject result = ResultUtil.result(HttpStatus.SC_OK, msg, list);
        return result;
    }
    @RequestMapping(value = "rootPath", method = RequestMethod.POST)
    @ResponseBody
    public String getRootPath(HttpServletRequest request, @RequestBody String postData,
                                   HttpServletResponse response) throws Exception {

        return request.getContextPath();
    }
    @RequestMapping("pdfPreview")
    public ResponseEntity<byte[]> pdfPreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String applyId = RequestUtil.getString(request, "formId");
        String fileUrl = RequestUtil.getString(request, "fileUrl");
        String tableName = RequestUtil.getString(request, "tableName");
        String fileBasePath = SysPropertiesUtil.getGlobalProperty(fileUrl);
        return commonInfoManager.pdfPreviewOrDownLoad(fileName, fileId, applyId, fileBasePath,tableName);
    }
    @RequestMapping("officePreview")
    @ResponseBody
    public void officePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String applyId = RequestUtil.getString(request, "formId");
        String tableName = RequestUtil.getString(request, "tableName");
        String fileUrl = RequestUtil.getString(request, "fileUrl");
        String fileBasePath = SysPropertiesUtil.getGlobalProperty(fileUrl);
        commonInfoManager.officeFilePreview(fileName, fileId, applyId, fileBasePath, response,tableName);
    }
    @RequestMapping("imagePreview")
    @ResponseBody
    public void imagePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String applyId = RequestUtil.getString(request, "formId");
        String tableName = RequestUtil.getString(request, "tableName");
        String fileUrl = RequestUtil.getString(request, "fileUrl");
        String fileBasePath = SysPropertiesUtil.getGlobalProperty(fileUrl);
        commonInfoManager.imageFilePreview(fileName, fileId, applyId, fileBasePath, response,tableName);
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
            String formId = RequestUtil.getString(request, "formId");
            String fileUrl = RequestUtil.getString(request, "fileUrl");
            String fileBasePath = SysPropertiesUtil.getGlobalProperty(fileUrl);
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
            File file;
            //如果是PDF文件，下载添加水印
            if("PDF".equals(suffix.toUpperCase())){
                String waterMarkPdfFullFilePath =
                        fileBasePath + relativeFilePath + File.separator + StandardConstant.PROCESSTMPDIR + File.separator + fileName;
                File tmpDir = new File(fileBasePath + relativeFilePath + File.separator + StandardConstant.PROCESSTMPDIR);
                if (!tmpDir.exists()) {
                    tmpDir.mkdirs();
                }
                // 删除临时目录中水印文件
                PdfWaterMarkProcess.waterMarkPdfDelete(waterMarkPdfFullFilePath);
                // 生成新的带水印文件
                PdfWaterMarkProcess.waterMarkPdfGenerate(fullFilePath, waterMarkPdfFullFilePath);
                // 创建文件实例
                file = new File(waterMarkPdfFullFilePath);
            }else{
                file = new File(fullFilePath);
            }
            // 修改文件名的编码格式
            String downloadFileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");

            // 设置httpHeaders,使浏览器响应下载
            HttpHeaders headers = new HttpHeaders();
            // 告诉浏览器执行下载的操作，“attachment”告诉了浏览器进行下载,下载的文件 文件名为 downloadFileName
            headers.setContentDispositionFormData("attachment", downloadFileName);
            // 设置响应方式为二进制，以二进制流传输
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file), headers, org.springframework.http.HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Exception in fileDownload", e);
            return null;
        }
    }


}
