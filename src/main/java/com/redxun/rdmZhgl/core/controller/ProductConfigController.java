
package com.redxun.rdmZhgl.core.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.rdmZhgl.core.dao.ProductConfigDao;
import com.redxun.rdmZhgl.core.service.ProductConfigService;
import com.redxun.standardManager.core.util.ResultUtil;
import org.apache.commons.lang.StringUtils;
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
import com.redxun.saweb.context.ContextUtil;

/**
 * 新品试制配置
 * @author zz
 */
@Controller
@RequestMapping("/rdmZhgl/core/productConfig/")
public class ProductConfigController {
    @Resource
    ProductConfigService productConfigService;
    @Resource
    ProductConfigDao productConfigDao;

    /**
     * 项目作废申请流程列表
     * */
    @RequestMapping("getListPage")
    public ModelAndView getListPage(HttpServletRequest request,HttpServletResponse response) throws Exception{
        String jspPath = "rdmZhgl/core/productConfigList.jsp";
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
        String jspPath = "rdmZhgl/core/productConfigEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String mainId = request.getParameter("mainId");
        String action = request.getParameter("action");
        JSONObject applyObj = new JSONObject();
        if(!StringUtil.isEmpty(mainId)){
            applyObj = productConfigService.getObjectById(mainId);
        }
        mv.addObject("action",action);
        mv.addObject("applyObj", applyObj);
        return mv;
    }
    @RequestMapping("remove")
    @ResponseBody
    public JSONObject remove(HttpServletRequest request, HttpServletResponse response) {
        JSONObject resultJSON = null;
        resultJSON = productConfigService.remove(request);
        return resultJSON;
    }
    @RequestMapping("save")
    @ResponseBody
    public JSONObject save(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");
        JSONObject resultJSON = null;
        if(StringUtil.isEmpty(id)){
            resultJSON = productConfigService.add(request);
        }else{
            resultJSON = productConfigService.update(request);
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
        productConfigService.saveOrUpdateItem(changeGridDataStr);
        return result;
    }
    @RequestMapping(value = "items")
    @ResponseBody
    public List<JSONObject> getItemList(HttpServletRequest request, HttpServletResponse response)  {
        return productConfigService.getItemList(request);
    }
    @RequestMapping(value = "list")
    @ResponseBody
    public List<Map<String, Object>> getPlanList(HttpServletRequest request, HttpServletResponse response)  {
        return productConfigService.query(request);
    }
    @RequestMapping(value = "itemList", method = RequestMethod.POST)
    @ResponseBody
    public List<Map<String, Object>> itemList(HttpServletRequest request, @RequestBody String postData,
                                       HttpServletResponse response) throws Exception {
        JSONObject postDataJson = new JSONObject();
        if (StringUtils.isNotBlank(postData)) {
            postDataJson = JSONObject.parseObject(postData);
        }
        return productConfigService.query(request);
    }
    @RequestMapping(value = "verifyItem", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject verifyItem(HttpServletRequest request, @RequestBody String postData,
                                HttpServletResponse response) throws Exception {
        JSONObject postDataJson = new JSONObject();
        JSONObject resultJson = new JSONObject();
        if (StringUtils.isNotBlank(postData)) {
            postDataJson = JSONObject.parseObject(postData);
        }else{
            resultJson = ResultUtil.result(false,"请求参数不能为空",null);
        }
        String startId = postDataJson.getString("startId");
        if(StringUtil.isEmpty(startId)){
            resultJson =  ResultUtil.result(false,"开始节点不能为空",null);
        }
        JSONObject jsonObject = productConfigDao.getItemObjById(startId);
        int startSort = jsonObject.getInteger("sort");
        int endSort = postDataJson.getInteger("sort");
        if(endSort<startSort){
            resultJson = ResultUtil.result(false,"结束节点不能早于开始节点！",null);
        }else{
            resultJson = ResultUtil.result(true,"",null);
        }
        return resultJson;
    }

    @RequestMapping(value = "deliverList")
    @ResponseBody
    public List<JSONObject> getDeliverList(HttpServletRequest request, HttpServletResponse response)  {
        return productConfigService.getDeliverList(request);
    }
}
