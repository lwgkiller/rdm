
package com.redxun.rdmZhgl.core.controller;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.manager.MybatisBaseManager;
import com.redxun.core.util.StringUtil;
import com.redxun.org.api.model.IUser;
import com.redxun.org.api.service.GroupService;
import com.redxun.rdmZhgl.core.service.MaterialProcureME51NService;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.controller.MybatisListController;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.sys.org.entity.OsGroup;
import com.redxun.sys.org.manager.OsGroupManager;
import com.redxun.sys.org.manager.OsUserManager;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Calendar;
import java.util.List;

@Controller
@RequestMapping("/rdmZhgl/core/materialProcureME51N/")
public class MaterialProcureME51NController extends MybatisListController {
    @Autowired
    private MaterialProcureME51NService materialProcureME51NService;
    @Autowired
    private OsGroupManager osGroupManager;


    @SuppressWarnings("rawtypes")
    @Override
    public MybatisBaseManager getBaseManager() {
        return null;
    }

    //..
    @RequestMapping("listPage")
    public ModelAndView getListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/materialProcureME51NList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        IUser currentUser = ContextUtil.getCurrentUser();
        mv.addObject("currentUser", currentUser);
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DATE);
        mv.addObject("currentDay", day);
        return mv;
    }

    //..
    @RequestMapping("editPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/materialProcureME51NEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String id = RequestUtil.getString(request, "id");
        String action = RequestUtil.getString(request, "action");
        mv.addObject("id", id).addObject("action", action);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        return mv;
    }

    //..
    @RequestMapping("queryList")
    @ResponseBody
    public JsonPageResult<?> queryList(HttpServletRequest request, HttpServletResponse response) {
        return materialProcureME51NService.queryList(request, true);
    }

    //..
    @RequestMapping("getItemList")
    @ResponseBody
    public List<JSONObject> getItemList(HttpServletRequest request, HttpServletResponse response) {
        return materialProcureME51NService.getItemList(request);
    }

    //..
    @RequestMapping("getDetail")
    @ResponseBody
    public JSONObject getDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject jsonObject = new JSONObject();
        String id = RequestUtil.getString(request, "id");
        if (StringUtils.isNotBlank(id)) {
            jsonObject = materialProcureME51NService.getDetail(id);
        } else {
            jsonObject.put("applyUserName", ContextUtil.getCurrentUser().getFullname());
            OsGroup belongDep = osGroupManager.getBelongDep(ContextUtil.getCurrentUserId());
            jsonObject.put("applyDeptName", belongDep.getName());
        }
        return jsonObject;
    }

    //..
    @RequestMapping("saveApply")
    @ResponseBody
    public JsonResult saveApply(HttpServletRequest request, HttpServletResponse response,
                                @RequestBody JSONObject formDataJson) {
        JsonResult result = new JsonResult(true, "成功");
        try {
            materialProcureME51NService.createOrUpdateApply(formDataJson);
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage("Exception in save saveApply" + e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
            return result;
        }
        result.setData(formDataJson.getString("id"));
        return result;
    }

    //..createToSap
    @RequestMapping(value = "createToSap")
    @ResponseBody
    public JsonResult createToSap(HttpServletRequest request, HttpServletResponse response,
                                  @RequestBody String formDataJson) {
        JsonResult result = new JsonResult();
        try {
            result = materialProcureME51NService.doCreateToSap(JSONObject.parseObject(formDataJson));
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage("Exception in save createToSap" + e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
            return result;
        }
        return result;
    }

    //..
    @RequestMapping(value = "sendToSap")
    @ResponseBody
    public JsonResult sendToSap(HttpServletRequest request, HttpServletResponse response,
                                @RequestBody String formDataJson) {
        JsonResult result = new JsonResult();
        try {
            result = materialProcureME51NService.doSendToSap(JSONObject.parseObject(formDataJson));
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage("Exception in save sendToSap" + e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
            return result;
        }
        return result;
    }

    //..
    @RequestMapping(value = "removeFromSap")
    @ResponseBody
    public JsonResult removeFromSap(HttpServletRequest request, HttpServletResponse response,
                                    @RequestBody String formDataJson) {
        JsonResult result = new JsonResult();
        try {
            result = materialProcureME51NService.doRemoveFromSap(JSONObject.parseObject(formDataJson));
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage("Exception in save removeFromSap" + e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
            return result;
        }
        return result;
    }
}
