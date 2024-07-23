
package com.redxun.rdmZhgl.core.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.core.util.DateUtil;
import com.redxun.rdmZhgl.core.dao.NdkfjhBudgetDao;
import com.redxun.rdmZhgl.core.service.NdkfjhBudgetService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.util.StringUtil;
import com.redxun.org.api.model.IUser;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.saweb.context.ContextUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 年度开发预算
 * @author zz
 */
@Controller
@RequestMapping("/rdmZhgl/core/ndkfjh/budget/")
public class NdkfjhBudgetController {
    @Resource
    NdkfjhBudgetService ndkfjhBudgetService;
    @Resource
    CommonInfoManager commonInfoManager;
    @Resource
    NdkfjhBudgetDao ndkfjhBudgetDao;
    /**
     * 列表
     * */
    @RequestMapping("listPage")
    public ModelAndView getListPage(HttpServletRequest request,HttpServletResponse response) throws Exception{
        String jspPath = "rdmZhgl/core/ndkfjhBudgetList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        IUser currentUser = ContextUtil.getCurrentUser();
        mv.addObject("currentUser", currentUser);
        JSONObject budgetAdmin = commonInfoManager.hasPermission("NDKFJH-YSGLY");
        mv.addObject("permission",budgetAdmin.getBoolean("NDKFJH-YSGLY")||"admin".equals(ContextUtil.getCurrentUser().getUserNo()));
        return mv;
    }

   /**
    * 获取编辑页面
    * */
    @RequestMapping("getEditPage")
    public ModelAndView getEditPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/ndkfjhBudgetEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String id = request.getParameter("id");
        String action = request.getParameter("action");
        JSONObject applyObj = new JSONObject();
        List<JSONObject> detailList = new ArrayList<>();
        if(!StringUtil.isEmpty(id)){
            applyObj = ndkfjhBudgetService.getObjectById(id);
            if (applyObj != null) {
                if (applyObj.get("startDate") != null) {
                    applyObj.put("startDate", DateUtil.formatDate((Date)applyObj.get("startDate"), "yyyy-MM-dd"));
                }
                if (applyObj.get("endDate") != null) {
                    applyObj.put("endDate", DateUtil.formatDate((Date)applyObj.get("endDate"), "yyyy-MM-dd"));
                }
            }
            detailList = ndkfjhBudgetDao.getBudgetDetailList(id);
        }
        applyObj.put("detailList",detailList);
        mv.addObject("action",action);
        mv.addObject("applyObj", applyObj);
        return mv;
    }
    @RequestMapping("remove")
    @ResponseBody
    public JSONObject remove(HttpServletRequest request, HttpServletResponse response) {
        JSONObject resultJSON = null;
        resultJSON = ndkfjhBudgetService.remove(request);
        return resultJSON;
    }
    @RequestMapping("save")
    @ResponseBody
    public JSONObject save(HttpServletRequest request, HttpServletResponse response,@RequestBody String budgetStr) {
        JSONObject formDataJson = JSONObject.parseObject(budgetStr);
        String id = formDataJson.getString("id");
        JSONObject resultJSON = null;
        if(StringUtil.isEmpty(id)){
            resultJSON = ndkfjhBudgetService.add(formDataJson);
        }else{
            resultJSON = ndkfjhBudgetService.update(formDataJson);
        }
        return resultJSON;
    }
    @RequestMapping(value = "list")
    @ResponseBody
    public JsonPageResult<?> getDataList(HttpServletRequest request, HttpServletResponse response)  {
        return ndkfjhBudgetService.query(request);
    }
    /**
     * 模板下载
     * */
    @RequestMapping("/importTemplateDownload")
    public ResponseEntity<byte[]> importTemplateDownload(HttpServletRequest request, HttpServletResponse response) {
        return ndkfjhBudgetService.importTemplateDownload();
    }

    /**
     * 批量导入
     * */
    @RequestMapping("importExcel")
    @ResponseBody
    public JSONObject importExcel(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        ndkfjhBudgetService.importBudget(result, request);
        return result;
    }
}
