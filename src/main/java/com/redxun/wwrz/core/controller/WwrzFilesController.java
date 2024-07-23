package com.redxun.wwrz.core.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;
import java.util.zip.ZipOutputStream;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.org.api.model.IUser;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.standardManager.core.util.ResultUtil;
import com.redxun.sys.core.util.SysPropertiesUtil;
import com.redxun.wwrz.core.dao.WwrzDocListDao;
import com.redxun.wwrz.core.dao.WwrzFilesDao;
import com.redxun.wwrz.core.dao.WwrzTestApplyDao;
import com.redxun.wwrz.core.service.WwrzFilesService;
import com.redxun.xcmgProjectManager.core.util.ConstantUtil;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.redxun.core.json.JsonPageResult;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;

/**
 * @author zhangzhen
 */
@RestController
@RequestMapping("/wwrz/core/file/")
public class WwrzFilesController {
    @Resource
    WwrzFilesService wwrzFilesService;
    @Resource
    WwrzFilesDao wwrzFilesDao;
    @Resource
    WwrzDocListDao wwrzDocListDao;
    @Resource
    WwrzTestApplyDao wwrzTestApplyDao;
    @Resource
    CommonInfoManager commonInfoManager;

    @RequestMapping("fileWindow")
    public ModelAndView getFileWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "wwrz/core/wwrzFileList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String detailId = RequestUtil.getString(request, "detailId");
        String fileType = RequestUtil.getString(request, "fileType");
        Boolean editable = RequestUtil.getBoolean(request, "editable");
        mv.addObject("detailId", detailId);
        mv.addObject("fileType", fileType);
        mv.addObject("editable", editable);
        mv.addObject("currentUser", ContextUtil.getCurrentUser());
        return mv;
    }
    /**
     * 获取列表数据
     * */
    @RequestMapping("files")
    @ResponseBody
    public JsonPageResult<?> getFiles(HttpServletRequest request, HttpServletResponse response) {
        return wwrzFilesService.query(request);
    }
    @RequestMapping("fileUploadWindow")
    public ModelAndView fileUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "wwrz/core/wwrzFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("detailId", RequestUtil.getString(request, "detailId", ""));
        mv.addObject("fileType", RequestUtil.getString(request, "fileType", ""));
        return mv;
    }
    @RequestMapping("reportUploadWindow")
    public ModelAndView reportUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "wwrz/core/wwrzReportUpload.jsp";
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
            wwrzFilesService.saveUploadFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            modelMap.put("success", "false");
        }
        return modelMap;
    }
    @RequestMapping("delFile")
    public void delFile(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "id");
        String detailId = RequestUtil.getString(request, "mainId");
        wwrzFilesService.deleteOneFile(fileId, fileName, detailId);
    }
    @RequestMapping(value = "fileList", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject getFileList(HttpServletRequest request, @RequestBody String postData,
                                 HttpServletResponse response) throws Exception {
        JSONObject postDataJson = new JSONObject();
        JSONObject resultJson = new JSONObject();
        if (StringUtils.isNotBlank(postData)) {
            postDataJson = JSONObject.parseObject(postData);
        }else{
            return ResultUtil.result(false,"请求参数不能为空",null);

        }
        resultJson = wwrzFilesService.getFileList(postDataJson);
        return resultJson;
    }
    @RequestMapping(value = "fileListByParam", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject getFileListByParam(HttpServletRequest request, @RequestBody String postData,
                                  HttpServletResponse response) throws Exception {
        JSONObject postDataJson = new JSONObject();
        JSONObject resultJson = new JSONObject();
        if (StringUtils.isNotBlank(postData)) {
            postDataJson = JSONObject.parseObject(postData);
        }else{
            return ResultUtil.result(false,"请求参数不能为空",null);

        }
        resultJson = wwrzFilesService.getFileListByParam(postDataJson);
        return resultJson;
    }
    /**
     * 获取列表页面
     * */
    @RequestMapping("reportFilePage")
    public ModelAndView getReportFilePage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "wwrz/core/wwrzReportFileList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        JSONObject adminJson = commonInfoManager.hasPermission("WWRZ-GLY");
        mv.addObject("permission",adminJson.getBoolean("WWRZ-GLY")||"admin".equals(ContextUtil.getCurrentUser().getUserNo()));
        Boolean exportPermission = false;
        if(RdmConst.BZJSS_NAME.equals(ContextUtil.getCurrentUser().getMainGroupName())||"admin".equals(ContextUtil.getCurrentUser().getUserNo())){
            exportPermission = true;
        }
        mv.addObject("exportPermission", exportPermission);
        IUser currentUser = ContextUtil.getCurrentUser();
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        return mv;
    }
    /**
     * 获取列表数据
     * */
    @RequestMapping("reportFiles")
    @ResponseBody
    public JsonPageResult<?> getReportFiles(HttpServletRequest request, HttpServletResponse response) {
        return wwrzFilesService.getReportFileList(request);
    }
    @RequestMapping("removeReportFile")
    @ResponseBody
    public JSONObject removeReportFile(HttpServletRequest request, HttpServletResponse response) {
        JSONObject resultJSON = null;
        resultJSON = wwrzFilesService.removeReportFile(request);
        return resultJSON;
    }
    @RequestMapping("batchInvalid")
    @ResponseBody
    public JSONObject batchInvalid(HttpServletRequest request, HttpServletResponse response) {
        JSONObject resultJSON = null;
        resultJSON = wwrzFilesService.batchInvalid(request);
        return resultJSON;
    }


    @RequestMapping("exportReportExcel")
    public void exportReportExcel(HttpServletRequest request, HttpServletResponse response) {
        wwrzFilesService.exportReportListExcel(request, response);
    }
    @RequestMapping("fileDownload")
    public void fileDownload(HttpServletRequest request, HttpServletResponse response) {
        try {
            String applyId = RequestUtil.getString(request, "applyId");
            JSONObject applyObj = wwrzTestApplyDao.getJsonObject(applyId);
            String fileUrl = RequestUtil.getString(request, "fileUrl");
            String fileBasePath = SysPropertiesUtil.getGlobalProperty(fileUrl);
            List<JSONObject> filePathList = new ArrayList<>();
            List<JSONObject> docList = wwrzDocListDao.getDocumentList(applyId);
            JSONObject pathObj = new JSONObject();
            for(int i=0;i<docList.size();i++){
                JSONObject docObj = docList.get(i);
                String mainId = docObj.getString("id");
                String fileInfo = CommonFuns.genProjectCode(String.valueOf(i),2)+"-"+applyObj.getString("productModel")+"-"+docObj.getString("docName")+"-";
                List<JSONObject> fileList = wwrzFilesDao.getFileListByMainId(mainId);
                for(JSONObject fileObj:fileList){
                    String fileId = fileObj.getString("id");
                    String fileName = fileObj.getString("fileName");
                    String suffix = CommonFuns.toGetFileSuffix(fileName);
                    String relativeFilePath = "";
                    if (StringUtils.isNotBlank(mainId)) {
                        relativeFilePath = File.separator + mainId;
                    }
                    String realFileName = fileId + "." + suffix;
                    String fullFilePath = fileBasePath + relativeFilePath + File.separator + realFileName;
                    pathObj = new JSONObject();
                    pathObj.put("path",fullFilePath);
                    pathObj.put("fileName",fileInfo+fileName);
                    filePathList.add(pathObj);
                }
            }
            if(filePathList.size()!=0){
                //创建临时路径，存放压缩文件
                String downFile = applyObj.getString("productModel")+"-认证资料明细.zip";
                String zipFilePath = fileBasePath+File.separator+downFile;
                ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFilePath));
                for(JSONObject path:filePathList){
                    wwrzFilesService.fileToZip(path.getString("path"),zipOutputStream,path.getString("fileName"));
                }
                //压缩完成后，关闭压缩流
                zipOutputStream.close();
                String fileName = new String(downFile.getBytes(),"ISO8859-1");
                response.setHeader("Content-Disposition","attachment;filename="+fileName);

                ServletOutputStream outputStream = response.getOutputStream();
                FileInputStream inputStream = new FileInputStream(zipFilePath);

                IOUtils.copy(inputStream,outputStream);
                inputStream.close();

                File fileTempZip = new File(zipFilePath);
                fileTempZip.delete();
            }
        } catch (Exception e) {
           e.printStackTrace();
        }
    }
    /**
     * 获取编辑页面
     * */
    @RequestMapping("getEditPage")
    public ModelAndView getEditPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "wwrz/core/wwrzReportFileEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String id = request.getParameter("id");
        JSONObject applyObj = new JSONObject();
        if(!StringUtil.isEmpty(id)){
            applyObj = wwrzFilesDao.getFileObj(id);
            if (applyObj.get("reportDate") != null) {
                applyObj.put("reportDate", DateUtil.formatDate((Date)applyObj.get("reportDate"), "yyyy-MM-dd"));
            }
            if (applyObj.get("reportValidity") != null) {
                applyObj.put("reportValidity", DateUtil.formatDate((Date)applyObj.get("reportValidity"), "yyyy-MM-dd"));
            }
        }
        mv.addObject("applyObj", applyObj);
        return mv;
    }
    @RequestMapping("save")
    @ResponseBody
    public JSONObject save(HttpServletRequest request, HttpServletResponse response) {
        JSONObject resultJSON = null;
        String id = request.getParameter("id");
        if(StringUtil.isEmpty(id)){
            resultJSON = wwrzFilesService.addReportFile(request);
        }else{
            resultJSON = wwrzFilesService.updateReportFile(request);
        }

        return resultJSON;
    }
    /**
     * 模板下载
     * */
    @RequestMapping("/importTemplateDownload")
    public ResponseEntity<byte[]> importTemplateDownload(HttpServletRequest request, HttpServletResponse response) {
        return wwrzFilesService.importTemplateDownload();
    }
    /**
     * 批量导入
     * */
    @RequestMapping("importExcel")
    @ResponseBody
    public JSONObject importExcel(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        wwrzFilesService.importReportCert(result, request);
        return result;
    }
    /**
     * 新增、更新报告证书文件
     * */
    @RequestMapping("dealReportFile")
    @ResponseBody
    public JSONObject dealReportFile(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        wwrzFilesService.dealReportFile(result, request);
        return result;
    }
    /**
     * 获取公共文档列表页面
     * */
    @RequestMapping("commonFilePage")
    public ModelAndView getCommonFilePage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "wwrz/core/wwrzCommonFileList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        JSONObject adminJson = commonInfoManager.hasPermission("WWRZ-GLY");
        mv.addObject("permission",adminJson.getBoolean("WWRZ-GLY")||"admin".equals(ContextUtil.getCurrentUser().getUserNo()));
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        return mv;
    }
    /**
     * 获取列表数据
     * */
    @RequestMapping("commonFiles")
    @ResponseBody
    public JsonPageResult<?> getCommonFiles(HttpServletRequest request, HttpServletResponse response) {
        return wwrzFilesService.getCommonFileList(request);
    }

    @RequestMapping(value = "updateIndexSort", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject updateIndexSort(HttpServletRequest request, @RequestBody String postData,
                                   HttpServletResponse response) throws Exception {
        JSONObject postDataJson = new JSONObject();
        if (StringUtils.isNotBlank(postData)) {
            postDataJson = JSONObject.parseObject(postData);
        }
        JSONObject resultJSON = null;
        resultJSON = wwrzFilesService.updateIndexSort(postDataJson);
        return resultJSON;
    }
}
