
package com.redxun.rdmZhgl.core.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.core.json.JsonPageResult;
import com.redxun.rdmZhgl.core.service.GtzzService;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.StringUtil;
import com.redxun.org.api.model.IUser;
import com.redxun.rdmZhgl.core.service.ProductConfigService;
import com.redxun.saweb.context.ContextUtil;

/**
 * 新品试制配置
 * @author zz
 */
@Controller
@RequestMapping("/rdmZhgl/core/gtzz/")
public class GtzzController {
    @Resource
    GtzzService gtzzService;

    /**
     * 列表
     * */
    @RequestMapping("getListPage")
    public ModelAndView getListPage(HttpServletRequest request,HttpServletResponse response) throws Exception{
        String jspPath = "rdmZhgl/core/gtzzList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        IUser currentUser = ContextUtil.getCurrentUser();
        mv.addObject("currentUser", currentUser);
        return mv;
    }

   /**
    * 获取编辑页面
    * */
    @RequestMapping("getEditPage")
    public ModelAndView getEditPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/gtzzEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String mainId = request.getParameter("mainId");
        String action = request.getParameter("action");
        JSONObject applyObj = new JSONObject();
        if(!StringUtil.isEmpty(mainId)){
            applyObj = gtzzService.getObjectById(mainId);
        }
        mv.addObject("action",action);
        mv.addObject("applyObj", applyObj);
        return mv;
    }
    /**
     * 挂图督战展示列表
     * */
    @RequestMapping("getViewListPage")
    public ModelAndView getViewListPage(HttpServletRequest request,HttpServletResponse response) throws Exception{
        String jspPath = "rdmZhgl/core/gtzzViewList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        IUser currentUser = ContextUtil.getCurrentUser();
        mv.addObject("currentUser", currentUser);
        return mv;
    }

    /**getViewPage
     * 获取编辑页面
     * */
    @RequestMapping("getViewPage")
    public ModelAndView getViewPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/gtzzView.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String mainId = request.getParameter("mainId");
        JSONObject applyObj = new JSONObject();
        mv.addObject("mainId",mainId);
        mv.addObject("applyObj", applyObj);
        return mv;
    }
    @RequestMapping("remove")
    @ResponseBody
    public JSONObject remove(HttpServletRequest request, HttpServletResponse response) {
        JSONObject resultJSON = null;
        resultJSON = gtzzService.remove(request);
        return resultJSON;
    }
    @RequestMapping("save")
    @ResponseBody
    public JSONObject save(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");
        JSONObject resultJSON = null;
        if(StringUtil.isEmpty(id)){
            resultJSON = gtzzService.add(request);
        }else{
            resultJSON = gtzzService.update(request);
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
        gtzzService.saveOrUpdateItem(changeGridDataStr);
        return result;
    }
    @RequestMapping(value = "items")
    @ResponseBody
    public List<JSONObject> getItemList(HttpServletRequest request, HttpServletResponse response)  {
        return gtzzService.getItemList(request);
    }
    @RequestMapping(value = "list")
    @ResponseBody
    public JsonPageResult<?> getPlanList(HttpServletRequest request, HttpServletResponse response)  {
        return gtzzService.query(request);
    }

    @RequestMapping(value = "gtzzList")
    @ResponseBody
    public List<JSONObject> getGtzzList(HttpServletRequest request, HttpServletResponse response)  {
        return gtzzService.getGtzzList(request);
    }


    /**
     * 模板下载
     * */
    @RequestMapping("/importTemplateDownload")
    public ResponseEntity<byte[]> importTemplateDownload(HttpServletRequest request, HttpServletResponse response) {
        return gtzzService.importTemplateDownload();
    }

    /**
     * 批量导入
     * */
    @RequestMapping("importExcel")
    @ResponseBody
    public JSONObject importExcel(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        gtzzService.importGtzz(result, request);
        return result;
    }
}
