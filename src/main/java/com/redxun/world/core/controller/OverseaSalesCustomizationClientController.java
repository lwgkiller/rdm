
package com.redxun.world.core.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.FileUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.sys.core.entity.SysFile;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.world.core.dao.OverseaSalesCustomizationDao;
import com.redxun.world.core.service.OverseaSalesCustomizationClientService;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import static com.redxun.rdmCommon.core.util.RdmCommonUtil.toGetParamVal;

@Controller
@RequestMapping("/world/core/overseaSalesCustomizationClient/")
public class OverseaSalesCustomizationClientController {
    private static final Logger logger = LoggerFactory.getLogger(OverseaSalesCustomizationClientController.class);
    @Autowired
    private SysDicManager sysDicManager;
    @Autowired
    private CommonInfoManager commonInfoManager;
    @Autowired
    private OverseaSalesCustomizationClientService overseaSalesCustomizationClientService;
    @Autowired
    private OverseaSalesCustomizationDao overseaSalesCustomizationDao;

    //..
    @RequestMapping("imageView")
    public void imageView(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String fileId = request.getParameter("fileId");
        String fileName = request.getParameter("fileName");
        String suffix = "";
        if (StringUtil.isNotEmpty(fileName)) {
            suffix = fileName.split("\\.")[1];
        }
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey("worldUploadPosition",
                "overseaSalesCustomizationProductPic").getValue();
        String filePath = filePathBase + File.separator + fileId + "." + suffix;
        //设置response的编码方式
        response.setContentType("image/" + suffix);
        // 创建file对象
        File file = new File(filePath);
        if (file.exists()) {
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);

            FileUtil.downLoad(file, response);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    //..
    @RequestMapping("clientPage")
    public ModelAndView dataListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "world/core/overseasalesCustomizationClient.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String action = RequestUtil.getString(request, "action");
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("clientGroupId", ContextUtil.getCurrentUser().getMainGroupId());
        boolean isOverseaSalesCustomizationAdmins =
                commonInfoManager.judgeUserIsPointRole("海外销售标选配管理员", ContextUtil.getCurrentUserId());
        mv.addObject("isOverseaSalesCustomizationAdmins", isOverseaSalesCustomizationAdmins);
        mv.addObject("action", action);
        return mv;
    }

    //..
    @RequestMapping("clientDetailPage")
    public ModelAndView clientDetailPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "world/core/overseasalesCustomizationClientDetail.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String businessId = RequestUtil.getString(request, "businessId");
        String action = RequestUtil.getString(request, "action");
        mv.addObject("businessId", businessId).addObject("action", action);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentUserGroupId", ContextUtil.getCurrentUser().getMainGroupId());
        mv.addObject("nodeTypeList", sysDicManager.getSysDicKeyValueListByTreeKey("overseaSalesCustomizationNodeType"));
        mv.addObject("model", overseaSalesCustomizationDao.getModelDataById(businessId));
        mv.addObject("action", action);
        return mv;
    }

    //..
    @RequestMapping("productGroupQuery")
    @ResponseBody
    public JSONArray productGroupQuery(HttpServletRequest request, HttpServletResponse response) {
        return overseaSalesCustomizationClientService.productGroupQuery(request, response);
    }

    //..
    @RequestMapping("modelQueryByGroup")
    @ResponseBody
    public JSONArray modelQueryByGroup(HttpServletRequest request, HttpServletResponse response) {
        return overseaSalesCustomizationClientService.modelQueryByGroup(request, response);
    }

    //..
    @RequestMapping("clientTreeQuery")
    @ResponseBody
    public List<JSONObject> clientTreeQuery(HttpServletRequest request, HttpServletResponse response) {
        return overseaSalesCustomizationClientService.clientTreeQuery(request, response);
    }

    //..
    @RequestMapping("checkAndIniInst")
    @ResponseBody
    public JsonResult checkAndIniInst(HttpServletRequest request, HttpServletResponse response) {
        try {
            return overseaSalesCustomizationClientService.checkAndIniInst(request);
        } catch (Exception e) {
            logger.error("Exception in checkAndIniInst", e);
            return new JsonResult(false, e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
        }
    }

    //..
    @RequestMapping(value = "saveClientInst", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult saveClientInst(HttpServletRequest request, HttpServletResponse response,
                                     @RequestBody String DataStr) {
        JSONObject jsonObject = JSONObject.parseObject(DataStr);
        if (jsonObject == null || jsonObject.isEmpty()) {
            return new JsonResult(false, "没有待更新的内容！");
        }
        try {
            return overseaSalesCustomizationClientService.saveClientInst(jsonObject);
        } catch (Exception e) {
            logger.error("Exception in saveClientInst", e);
            return new JsonResult(false, e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
        }
    }

    //..
    @RequestMapping(value = "doCreateReport", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult doCreateReport(HttpServletRequest request, HttpServletResponse response,
                                     @RequestBody String DataStr) {
        DataStr = new String(DataStr.getBytes(), Charset.forName("utf-8"));
        JSONObject jsonObject = JSONObject.parseObject(DataStr);
        if (jsonObject == null || jsonObject.isEmpty()) {
            return new JsonResult(false, "内容为空，操作失败！");
        }
        try {
            return overseaSalesCustomizationClientService.doCreateReport(jsonObject);
        } catch (Exception e) {
            logger.error("Exception in doCreateReport", e);
            return new JsonResult(false, "创建失败！错误信息：" + e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
        }
    }
}
