package com.redxun.environment.core.controller;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.environment.core.service.RjscService;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.controller.GenericController;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/environment/core/Rjsc/")
public class RjscController extends GenericController {
    @Autowired
    private RjscService rjscService;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;

    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;

    @RequestMapping("openUploadWindow")
    public ModelAndView openUploadWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rap/core/rjscFileUpload.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("rjId", RequestUtil.getString(request, "rjId", ""));
        return mv;
    }

    @RequestMapping(value = "upload")
    @ResponseBody
    public Map<String, Object> upload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        try {
            // 先保存文件到磁盘，成功后再写入数据库，前台是一个一个文件的调用
            rjscService.saveRjscUploadFiles(request);
            modelMap.put("success", "true");
        } catch (Exception e) {
            logger.error("Exception in upload", e);
            modelMap.put("success", "false");
        }
        return modelMap;
    }

    @RequestMapping("list")
    public ModelAndView management(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "rap/core/rjscList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        Map<String, Object> params = new HashMap<>();
        params.put("userId", ContextUtil.getCurrentUserId());
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        // 角色信息
        String userId=ContextUtil.getCurrentUserId();
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("currentUserId", userId);
        return mv;
    }

    // 标准新增和编辑页面
    @RequestMapping("edit")
    public ModelAndView editRjsc(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "rap/core/rjscEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String action = RequestUtil.getString(request, "action");
        String rjId = RequestUtil.getString(request, "rjId");
        if (StringUtils.isBlank(action)) {
            action = "edit";
        }
        mv.addObject("action", action);
        mv.addObject("rjId", rjId);
        mv.addObject("currentUser", ContextUtil.getCurrentUser());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        return mv;
    }

    @RequestMapping("getRjscDetail")
    @ResponseBody
    public JSONObject getRjscDetail(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        JSONObject rjscobj = new JSONObject();
        String rjId = RequestUtil.getString(request, "rjId");
        if (StringUtils.isNotBlank(rjId)) {
            rjscobj = rjscService.getRjscDetail(rjId);
        }
        return rjscobj;
    }

    // 标准列表查询
    @RequestMapping("queryList")
    @ResponseBody
    public JsonPageResult<?> queryRjscList(HttpServletRequest request, HttpServletResponse response) {
        return rjscService.queryRjsc(request, response, true);
    }

    @RequestMapping("saveRjsc")
    @ResponseBody
    public JsonResult saveRjsc(HttpServletRequest request, @RequestBody JSONObject formDataJson,
        HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "成功");
        try {
            if (formDataJson == null || formDataJson.isEmpty()) {
                logger.warn("formData is blank");
                result.setSuccess(false);
                result.setMessage("requestBody is blank");
                return result;
            }
            if (StringUtils.isBlank(formDataJson.getString("rjId"))) {
                rjscService.insertRjsc(formDataJson);
            } else {
                rjscService.updateRjsc(formDataJson);
            }
        } catch (Exception e) {
            logger.error("Exception in save rjsc");
            result.setSuccess(false);
            result.setMessage("Exception in save rjsc");
            return result;
        }
        return result;
    }

    @RequestMapping("getFileList")
    @ResponseBody
    public List<JSONObject> getrjscFileList(HttpServletRequest request, HttpServletResponse response) {
        List<String> rjIdList = Arrays.asList(RequestUtil.getString(request, "rjId", ""));
        return rjscService.getRjscFileList(rjIdList);
    }

    @RequestMapping("deleterjscFile")
    public void deleteRjscFile(HttpServletRequest request, HttpServletResponse response, @RequestBody String postBody) {
        JSONObject postBodyObj = JSONObject.parseObject(postBody);
        String fileName = postBodyObj.getString("fileName");
        String fileId = postBodyObj.getString("id");
        String rjId = postBodyObj.getString("formId");
        rjscService.deleteOneRjscFile(fileId, fileName, rjId);
    }

    @RequestMapping("deleterjsc")
    @ResponseBody
    public JsonResult deleteRjsc(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return rjscService.deleteRjsc(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteRjsc", e);
            return new JsonResult(false, e.getMessage());
        }
    }

}
