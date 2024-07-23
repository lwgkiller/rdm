package com.redxun.powerApplicationTechnology.core.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.EncryptUtil;
import com.redxun.core.util.HttpClientUtil;
import com.redxun.powerApplicationTechnology.core.service.RoadtestService;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;

@Controller
@RequestMapping("/powerApplicationTechnology/core/roadtest")
public class RoadtestController {
    private static final Logger logger = LoggerFactory.getLogger(RoadtestController.class);
    @Autowired
    private RoadtestService roadtestService;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private SysDicManager sysDicManager;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;

    //..
    @RequestMapping("dataListPage")
    public ModelAndView dataListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "powerApplicationTechnology/core/roadtestList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        return mv;
    }

    //..
    @RequestMapping("dataListQuery")
    @ResponseBody
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) {
        return roadtestService.dataListQuery(request, response);
    }

    //..
    @RequestMapping("editPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "powerApplicationTechnology/core/roadtestEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String businessId = RequestUtil.getString(request, "businessId");
        String action = RequestUtil.getString(request, "action");
        mv.addObject("businessId", businessId).addObject("action", action);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        return mv;
    }

    //..
    @RequestMapping("deleteBusiness")
    @ResponseBody
    public JsonResult deleteBusiness(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return roadtestService.deleteBusiness(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteBusiness", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    //..
    @RequestMapping("start")
    @ResponseBody
    public JsonResult start(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return roadtestService.startBusiness(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteBusiness", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    //..
    @RequestMapping("close")
    @ResponseBody
    public JsonResult close(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return roadtestService.closeBusiness(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteBusiness", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    //..
    @RequestMapping("getTestdataList")
    @ResponseBody
    public List<JSONObject> getTestdataList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String businessId = RequestUtil.getString(request, "businessId", "");
        return roadtestService.getTestdataList(businessId);
    }

    //..
    @RequestMapping("getDailyList")
    @ResponseBody
    public List<JSONObject> getDailyList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject params = new JSONObject();
        params.put("businessId", RequestUtil.getString(request, "businessId", ""));
        params.put("beginDate", RequestUtil.getString(request, "beginDate", ""));
        params.put("endDate", RequestUtil.getString(request, "endDate", ""));
        return roadtestService.getDailyList(params);
    }

    //..
    @RequestMapping("getDetail")
    @ResponseBody
    public JSONObject getDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject jsonObject = new JSONObject();
        String businessId = RequestUtil.getString(request, "businessId");
        if (StringUtils.isNotBlank(businessId)) {
            jsonObject = roadtestService.getDetail(businessId);
        }
        return jsonObject;
    }

    //..
    @RequestMapping("saveBusiness")
    @ResponseBody
    public JsonResult saveBusiness(HttpServletRequest request, @RequestBody String formDataStr,
                                   HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "保存成功");
        if (StringUtils.isBlank(formDataStr)) {
            logger.warn("requestBody is blank");
            result.setSuccess(false);
            result.setMessage("requestBody is blank");
            return result;
        }
        try {
            JSONObject formDataJson = JSONObject.parseObject(formDataStr);
            if (formDataJson == null || formDataJson.isEmpty()) {
                logger.warn("formData is blank");
                result.setSuccess(false);
                result.setMessage("requestBody is blank");
                return result;
            }
            if (StringUtils.isBlank(formDataJson.getString("id"))) {
                roadtestService.createBusiness(formDataJson);
            } else {
                roadtestService.updateBusiness(formDataJson);
            }
            result.setData(formDataJson.getString("id"));
        } catch (Exception e) {
            logger.error("Exception in save");
            result.setSuccess(false);
            result.setMessage("Exception in save");
            return result;
        }
        return result;
    }

    //..
    @RequestMapping(value = "saveDaily", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult saveDaily(HttpServletRequest request, @RequestBody String dataStr,
                                HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "保存成功");
        if (StringUtils.isBlank(dataStr)) {
            logger.warn("requestBody is blank");
            result.setSuccess(false);
            result.setMessage("数据为空，保存失败！");
            return result;
        }
        roadtestService.saveDaily(result, dataStr);
        return result;
    }

    //..
    @RequestMapping("testdataPage")
    public ModelAndView testdataPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "powerApplicationTechnology/core/roadtestTestdataEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String mainId = RequestUtil.getString(request, "mainId");
        String businessId = RequestUtil.getString(request, "businessId");
        String action = RequestUtil.getString(request, "action");
        mv.addObject("mainId", mainId).addObject("businessId", businessId).addObject("action", action);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        return mv;
    }

    //..
    @RequestMapping("deleteTestdata")
    @ResponseBody
    public JsonResult deleteTestdata(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String id = RequestUtil.getString(request, "id");
            return roadtestService.deleteTestdata(id);
        } catch (Exception e) {
            logger.error("Exception in deleteData", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    //..
    @RequestMapping("getTestdataFileList")
    @ResponseBody
    public List<JSONObject> getTestdataFileList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<String> mainIdList = Arrays.asList(RequestUtil.getString(request, "mainId", ""));
        return roadtestService.getTestdataFileList(mainIdList);
    }

    //..
    @RequestMapping("getTestdataDetail")
    @ResponseBody
    public JSONObject getTestdataDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject jsonObject = new JSONObject();
        String businessId = RequestUtil.getString(request, "businessId");
        if (StringUtils.isNotBlank(businessId)) {
            jsonObject = roadtestService.getTestdataDetail(businessId);
        }
        return jsonObject;
    }

    //..
    @RequestMapping(value = "saveTestdata", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult saveTestdata(HttpServletRequest request, @RequestBody String dataStr,
                                   HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "保存成功");
        if (StringUtils.isBlank(dataStr)) {
            logger.warn("requestBody is blank");
            result.setSuccess(false);
            result.setMessage("数据为空，保存失败！");
            return result;
        }
        roadtestService.saveTestdata(result, dataStr);
        return result;
    }

    //..
    @RequestMapping("openTestdataFileUploadWindow")
    public ModelAndView openTestdataFileUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "powerApplicationTechnology/core/roadtestTestdataFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("mainId", RequestUtil.getString(request, "mainId", ""));
        return mv;
    }

    //..
    @RequestMapping("pdfPreviewAndAllDownload")
    public ResponseEntity<byte[]> pdfPreviewAndAllDownload(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "powerApplicationTechnologyUploadPosition", "Roadtest").getValue();
        return rdmZhglFileManager.pdfPreviewOrDownLoad(fileName, fileId, formId, filePathBase);
    }

    //..
    @RequestMapping("delTestdataFile")
    public void delTestdataFile(HttpServletRequest request, HttpServletResponse response, @RequestBody String postBody) {
        JSONObject postBodyObj = JSONObject.parseObject(postBody);
        String fileName = postBodyObj.getString("fileName");
        String fileId = postBodyObj.getString("id");
        String mainId = postBodyObj.getString("formId");
        roadtestService.delTestdataFile(fileId, fileName, mainId);
    }

    //..
    @RequestMapping("officePreview")
    @ResponseBody
    public void officePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "powerApplicationTechnologyUploadPosition", "Roadtest").getValue();
        rdmZhglFileManager.officeFilePreview(fileName, fileId, formId, filePathBase, response);
    }

    //..
    @RequestMapping("imagePreview")
    @ResponseBody
    public void imagePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "fileName");
        String fileId = RequestUtil.getString(request, "fileId");
        String formId = RequestUtil.getString(request, "formId");
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
                "powerApplicationTechnologyUploadPosition", "Roadtest").getValue();
        rdmZhglFileManager.imageFilePreview(fileName, fileId, formId, filePathBase, response);
    }

    //..
    @RequestMapping("testdataFileUpload")
    @ResponseBody
    public Map<String, Object> testdataFileUpload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        try {
            //先保存文件，成功后再写入数据库，前台是一个一个文件的调用
            roadtestService.saveTestdataUploadFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            logger.error("Exception in upload", e);
            modelMap.put("success", "false");
        }
        return modelMap;
    }

    //..
    @RequestMapping("synchGPS")
    @ResponseBody
    public JsonResult synchGps(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String id = RequestUtil.getString(request, "id");
            return roadtestService.synchGPS(id);
//            return roadtestService.synchGPS2(id);
        } catch (Exception e) {
            logger.error("Exception in synchGps", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    //..
    @RequestMapping("calculate")
    @ResponseBody
    public JsonResult calculate(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return roadtestService.calculateBusiness(ids);
        } catch (Exception e) {
            logger.error("Exception in calculateBusiness", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    //..
    @RequestMapping("test")
    @ResponseBody
    public synchronized void test(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String IotUrl = "http://front.prod.iot.hanyunmmip.cn/business/external/WorkCondition?" +
                "vincode=%s&companyId=%s&appId=%s&signTime=%s&sign=%s";
        String vinCode = RequestUtil.getString(request, "id");
        vinCode = "XUGA550DKLKA00369";
        String companyId = "1237979465155112961";
        String appId = "wjcrm";
        String appSecret = "C67844291B7C4D4DABC6CB6866B73BD6";
        String signTime = String.valueOf(System.currentTimeMillis());
        String param = String.format("appId=%s&companyId=%s&signTime=%s&vincode=%s", appId, companyId, signTime, vinCode);
        String singA = param + "&appSecret=" + appSecret;
        String sing = EncryptUtil.encryptMd5(singA);
        String url = String.format(IotUrl, vinCode, companyId, appId, signTime, sing);
        String resp = HttpClientUtil.getFromUrl(url, null);
        JSONObject jsonObject = JSON.parseObject(resp);
        System.out.println("");

//        boolean b = StringUtil.isExist("厦门大学、徐工研究院、徐矿大学生委员会", "研究", "院");
//        System.out.println(b);

//        String s = "0.05";
//        Boolean strResult = s.matches("-?[0-9]+.?[0-9]*");
//        if (strResult == true) {
//            Double value = Double.parseDouble(s);
//            if (value.doubleValue() > 1 || value.doubleValue() < 0) {
//                System.out.println("");
//            } else {
//                System.out.println("");
//            }
//        }
    }

}
