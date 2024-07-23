
package com.redxun.rdmZhgl.core.controller;

import java.io.File;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.manager.MybatisBaseManager;
import com.redxun.core.util.OfficeDocPreview;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmZhgl.core.dao.ProductDao;
import com.redxun.rdmZhgl.core.dao.XpszFinishApplyDao;
import com.redxun.rdmZhgl.core.service.ProductService;
import com.redxun.saweb.controller.MybatisListController;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.standardManager.core.util.ResultUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.StringUtil;
import com.redxun.org.api.model.IUser;
import com.redxun.saweb.context.ContextUtil;

/**
 * 新品试制配置
 * @author zz
 */
@Controller
@RequestMapping("/rdmZhgl/core/product/")
public class ProductController extends MybatisListController {
    @Resource
    ProductService productService;
    @Resource
    ProductDao productDao;
    @Resource
    CommonInfoManager commonInfoManager;
    @Resource
    XpszFinishApplyDao xpszFinishApplyDao;
    @Override
    public MybatisBaseManager getBaseManager() {
        return null;
    }
    /**
     * 项目作废申请流程列表
     * */
    @RequestMapping("getListPage")
    public ModelAndView getListPage(HttpServletRequest request,HttpServletResponse response) throws Exception{
        String jspPath = "rdmZhgl/core/productList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        IUser currentUser = ContextUtil.getCurrentUser();
        mv.addObject("currentUser", currentUser);
        JSONObject resultJson = commonInfoManager.hasPermission("XPSZ-JHGGY");
        mv.addObject("permission",resultJson.getBoolean("XPSZ-JHGGY")||"admin".equals(ContextUtil.getCurrentUser().getUserNo()));
        JSONObject commmonPri = commonInfoManager.hasPermission("GBMXMGGZHJS");
        mv.addObject("common",commmonPri.getBoolean("GBMXMGGZHJS"));
        String processStatus = request.getParameter("processStatus");
        String important = request.getParameter("important");
        String belongYear = request.getParameter("belongYear");
        String reportType = request.getParameter("reportType");
        String dateStart = request.getParameter("dateStart");
        String dateEnd = request.getParameter("dateEnd");
        mv.addObject("processStatus", processStatus);
        mv.addObject("important", important);
        mv.addObject("belongYear", belongYear);
        mv.addObject("reportType", reportType);
        mv.addObject("dateStart", dateStart);
        mv.addObject("dateEnd", dateEnd);
        return mv;
    }

   /**
    * 获取编辑页面
    * */
    @RequestMapping("getEditPage")
    public ModelAndView getEditPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/productEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String mainId = request.getParameter("mainId");
        String action = request.getParameter("action");
        JSONObject applyObj = new JSONObject();
        if(!StringUtil.isEmpty(mainId)){
            applyObj = productService.getObjectById(mainId);
        }
        mv.addObject("action",action);
        mv.addObject("applyObj", applyObj);
        JSONObject resultJson = commonInfoManager.hasPermission("XPSZ-JHGGY");
        mv.addObject("permission",resultJson.getBoolean("XPSZ-JHGGY")||"admin".equals(ContextUtil.getCurrentUser().getUserNo()));
        return mv;
    }

    /**
     * 获取批量修改计划时间页面
     * */
    @RequestMapping("batchEditDatePage")
    public ModelAndView getBatchEditDatePage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/xpszBatchEditDate.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String mainId = request.getParameter("mainId");
        String action = request.getParameter("action");
        JSONObject applyObj = new JSONObject();
        if(!StringUtil.isEmpty(mainId)){
            JSONObject productObj  = productService.getObjectById(mainId);
            applyObj.put("productType",productObj.getString("productType"));
            applyObj.put("productModel",productObj.getString("productModel"));
            applyObj.put("id",mainId);
        }
        mv.addObject("action",action);
        mv.addObject("applyObj", applyObj);
        return mv;
    }
    @RequestMapping("remove")
    @ResponseBody
    public JSONObject remove(HttpServletRequest request, HttpServletResponse response) {
        JSONObject resultJSON = null;
        resultJSON = productService.remove(request);
        return resultJSON;
    }
    @RequestMapping("save")
    @ResponseBody
    public JSONObject save(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");
        JSONObject resultJSON = null;
        if(StringUtil.isEmpty(id)){
            resultJSON = productService.add(request);
        }else{
            resultJSON = productService.update(request);
        }
        return resultJSON;
    }
    @RequestMapping(value = "dealData", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult dealData(HttpServletRequest request, @RequestBody String changeGridDataStr,
                               HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "");
        if (StringUtils.isBlank(changeGridDataStr)) {
            result.setSuccess(false);
            result.setMessage("requestBody is blank");
            return result;
        }
        productService.saveOrUpdateItem(changeGridDataStr);
        return result;
    }
    @RequestMapping(value = "items")
    @ResponseBody
    public List<JSONObject> getItemList(HttpServletRequest request, HttpServletResponse response)  {
        return productService.getItemList(request);
    }
    @RequestMapping(value = "list")
    @ResponseBody
    public JsonPageResult<?> getPlanList(HttpServletRequest request, HttpServletResponse response)  {
        return productService.query(request);
    }

    @RequestMapping(value = "baseList")
    @ResponseBody
    public List<Map<String, Object>> getBaseList(HttpServletRequest request, HttpServletResponse response)  {
        return productService.getBaseList(request);
    }

    @RequestMapping(value = "updateDate", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject updateDate(HttpServletRequest request, @RequestBody String postData,
                                       HttpServletResponse response) throws Exception {
        JSONObject postDataJson = new JSONObject();
        if (StringUtils.isNotBlank(postData)) {
            postDataJson = JSONObject.parseObject(postData);
        }
        productService.updateDate(postDataJson);
        return ResultUtil.result(true,"", null);
    }
    @RequestMapping(value = "verifyPic", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject verifyPic(HttpServletRequest request, @RequestBody String postData,
                           HttpServletResponse response) throws Exception {
        JSONObject postDataJson = new JSONObject();
        if (StringUtils.isNotBlank(postData)) {
            postDataJson = JSONObject.parseObject(postData);
        }
        return  productService.verifyPic(postDataJson);
    }
    @RequestMapping(value = "verifyMarket", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject verifyMarket(HttpServletRequest request, @RequestBody String postData,
                                HttpServletResponse response) throws Exception {
        JSONObject postDataJson = new JSONObject();
        if (StringUtils.isNotBlank(postData)) {
            postDataJson = JSONObject.parseObject(postData);
        }
        return  productService.verifyMarket(postDataJson);
    }
    @RequestMapping("fileWindow")
    public ModelAndView workPlanFileWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/productPicList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String id = RequestUtil.getString(request, "id");
        String mainId = RequestUtil.getString(request, "mainId");
        String editable = RequestUtil.getString(request, "editable");
        String coverContent = RequestUtil.getString(request, "coverContent");
        JSONObject obj = productDao.getObjectById(mainId);
        //是否为产品主管
        Boolean isProductLeader = false;
        if(obj.getString("productLeader").equals(ContextUtil.getCurrentUserId())){
            isProductLeader = true;
        }
        mv.addObject("coverContent", coverContent);
        mv.addObject("id", id);
        mv.addObject("mainId", mainId);
        mv.addObject("editable", editable);
        JSONObject postDataJson = new JSONObject();
        postDataJson.put("mainId",mainId);
        postDataJson.put("item","yjwczcs_date");
        JSONObject changeObj = xpszFinishApplyDao.getObjectByInfo(postDataJson);
        //样机完成转测试 是否审批过
        Boolean isApply = false;
        if(changeObj==null){
            isApply = true;
        }
        JSONObject resultJson = commonInfoManager.hasPermission("XPSZ-JHGGY");
        mv.addObject("delRight", resultJson.getBoolean("XPSZ-JHGGY")||(isProductLeader&&isApply));
        return mv;
    }
    @RequestMapping("openUploadWindow")
    public ModelAndView openUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/productPicUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUser", ContextUtil.getCurrentUser());
        return mv;
    }
    @RequestMapping(value = "upload")
    @ResponseBody
    public Map<String, Object> upload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>(16);
        try {
            // 先保存文件到磁盘，成功后再写入数据库，前台是一个一个文件的调用
            productService.saveUploadFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return modelMap;
    }
    /**
     * 查询工作计划文件
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("files")
    @ResponseBody
    public List<JSONObject> getFiles(HttpServletRequest request, HttpServletResponse response) {
        List<JSONObject>  fileInfos = productService.getFileListByMainId(request);
        return fileInfos;
    }
    @RequestMapping("changeRecords")
    @ResponseBody
    public List<JSONObject> getChangeRecords(HttpServletRequest request, HttpServletResponse response) {
        List<JSONObject>  list = productService.getChangeRecordListByMainId(request);
        return list;
    }
    @RequestMapping("deleteFiles")
    public void deleteFiles(HttpServletRequest request, HttpServletResponse response) {
        String mainId = RequestUtil.getString(request, "mainId");
        String id = RequestUtil.getString(request, "id");
        String fileName = RequestUtil.getString(request, "fileName");
        String suffix = CommonFuns.toGetFileSuffix(fileName);
        productService.deleteFileOnDisk(mainId, id, suffix);
        Map<String, Object> fileParams = new HashMap<>(16);
        List<String> fileIds = new ArrayList<>();
        fileIds.add(id);
        fileParams.put("fileIds", fileIds);
        productDao.deleteFileByIds(fileParams);
    }
    // 项目管理文档的下载（预览pdf格式的文件也调用这里）
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
            String mainId = RequestUtil.getString(request, "mainId");
            String fileBasePath = "";
            if (StringUtils.isNotBlank(mainId)) {
                fileBasePath = "preview".equalsIgnoreCase(action)
                        ? WebAppUtil.getProperty("xpszFilePathBase_preview")
                        : WebAppUtil.getProperty("xpszFilePathBase");
            } else {
                fileBasePath =
                        "preview".equalsIgnoreCase(action) ? WebAppUtil.getProperty("commonFilePathBase_preview")
                                : WebAppUtil.getProperty("commonFilePathBase");
            }
            if (StringUtils.isBlank(fileBasePath)) {
                logger.error("操作失败，找不到存储根路径");
                return null;
            }
            String suffix = CommonFuns.toGetFileSuffix(fileName);
            String relativeFilePath = "";
            if (StringUtils.isNotBlank(mainId)) {
                relativeFilePath = File.separator + mainId;
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
    @RequestMapping("imagePreview")
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
        String mainId = RequestUtil.getString(request, "mainId");
        if (StringUtils.isBlank(mainId)) {
            logger.error("操作失败，主表id为空！");
            return;
        }

        String fileBasePath = WebAppUtil.getProperty("xpszFilePathBase");
        if (StringUtils.isBlank(fileBasePath)) {
            logger.error("操作失败，找不到存储路径");
            return;
        }
        String suffix = CommonFuns.toGetFileSuffix(fileName);
        String originalFilePath = fileBasePath + File.separator + mainId + File.separator + fileId + "." + suffix;
        OfficeDocPreview.imagePreview(originalFilePath, response);
    }
    /**
     * 获取总览页面
     * */
    @RequestMapping("overview")
    public ModelAndView overview(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/xpszOverview.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        return mv;
    }
    @RequestMapping("reportJdzt")
    @ResponseBody
    public JSONObject reportJdzt(HttpServletRequest request, HttpServletResponse response) {
        return productService.getReportJdzt(request);
    }

    @RequestMapping("reportJdtj")
    @ResponseBody
    public JSONObject getReportJdtj(HttpServletRequest request, HttpServletResponse response) {
        return productService.getReportJdtj(request);
    }

    /**
     * 模板下载
     * */
    @RequestMapping("/importTemplateDownload")
    public ResponseEntity<byte[]> importTemplateDownload(HttpServletRequest request, HttpServletResponse response) {
        return productService.importTemplateDownload();
    }
    @PostMapping("/exportExcel")
    public void exportExcel(HttpServletResponse response, HttpServletRequest request) {
        productService.exportExcel(response, request);
    }

    @PostMapping("/exportStageExcel")
    public void exportStageExcel(HttpServletResponse response, HttpServletRequest request) {
        productService.exportStageExcel(response, request);
    }

    /**
     * 批量导入
     * */
    @RequestMapping("importExcel")
    @ResponseBody
    public JSONObject importExcel(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        productService.importProduct(result, request);
        return result;
    }
    @RequestMapping(value = "getObj", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject getObj(HttpServletRequest request, @RequestBody String postData,
                                   HttpServletResponse response) throws Exception {
        JSONObject postDataJson = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        if (StringUtils.isNotBlank(postData)) {
            postDataJson = JSONObject.parseObject(postData);
            String mainId = postDataJson.getString("mainId");
            jsonObject = productDao.getProductObjByMainId(mainId);
        }
        return ResultUtil.result(true,"",jsonObject);
    }

    @RequestMapping("editDate")
    @ResponseBody
    public JSONObject editDate(HttpServletRequest request, HttpServletResponse response) {
        return productService.batchChangePlanDate(request);
    }

    @RequestMapping("recordChange")
    public ModelAndView recordChange(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/productChangeRecordList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String mainId = RequestUtil.getString(request, "mainId");
        mv.addObject("mainId", mainId);
        return mv;
    }
    @RequestMapping(value = "asyncPdmDelivery", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject asyncPdmDelivery(HttpServletRequest request, @RequestBody String postData,
                             HttpServletResponse response) throws Exception {
        return productService.asyncPdmDelivery();
    }
}
