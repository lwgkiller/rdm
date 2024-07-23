
package com.redxun.rdmZhgl.core.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmZhgl.core.service.YfjbMonthProductionsService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.util.StringUtil;
import com.redxun.org.api.model.IUser;
import com.redxun.saweb.context.ContextUtil;

/**
 * 月度产量维护
 * @author zz
 */
@Controller
@RequestMapping("/rdmZhgl/core/yfjb/monthProducts/")
public class YfjbMonthProductionsController {
    @Resource
    YfjbMonthProductionsService yfjbMonthProductionsService;
    @Resource
    CommonInfoManager commonInfoManager;
    /**
     * 列表
     * */
    @RequestMapping("getListPage")
    public ModelAndView getListPage(HttpServletRequest request,HttpServletResponse response) throws Exception{
        String jspPath = "rdmZhgl/core/yfjbMonthProductionsList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        IUser currentUser = ContextUtil.getCurrentUser();
        mv.addObject("currentUser", currentUser);
        JSONObject yfjbAdmin = commonInfoManager.hasPermission("YFJB-GLY");
        JSONObject JBZY = commonInfoManager.hasPermission("JBZY");
        mv.addObject("permission",yfjbAdmin.getBoolean("YFJB-GLY")||JBZY.getBoolean("JBZY")||"admin".equals(ContextUtil.getCurrentUser().getUserNo()));
        return mv;
    }

   /**
    * 获取编辑页面
    * */
    @RequestMapping("getEditPage")
    public ModelAndView getEditPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/yfjbMonthProductionsEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String id = request.getParameter("id");
        String action = request.getParameter("action");
        JSONObject applyObj = new JSONObject();
        if(!StringUtil.isEmpty(id)){
            applyObj = yfjbMonthProductionsService.getObjectById(id);
        }
        mv.addObject("action",action);
        mv.addObject("applyObj", applyObj);
        return mv;
    }
    @RequestMapping("remove")
    @ResponseBody
    public JSONObject remove(HttpServletRequest request, HttpServletResponse response) {
        JSONObject resultJSON = null;
        resultJSON = yfjbMonthProductionsService.remove(request);
        return resultJSON;
    }
    @RequestMapping("save")
    @ResponseBody
    public JSONObject save(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");
        JSONObject resultJSON = null;
        if(StringUtil.isEmpty(id)){
            resultJSON = yfjbMonthProductionsService.add(request);
        }else{
            resultJSON = yfjbMonthProductionsService.update(request);
        }
        return resultJSON;
    }
    @RequestMapping(value = "list")
    @ResponseBody
    public JsonPageResult<?> getPlanList(HttpServletRequest request, HttpServletResponse response)  {
        return yfjbMonthProductionsService.query(request);
    }
    /**
     * 模板下载
     * */
    @RequestMapping("/importTemplateDownload")
    public ResponseEntity<byte[]> importTemplateDownload(HttpServletRequest request, HttpServletResponse response) {
        return yfjbMonthProductionsService.importTemplateDownload();
    }

    /**
     * 批量导入
     * */
    @RequestMapping("importExcel")
    @ResponseBody
    public JSONObject importExcel(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        yfjbMonthProductionsService.importProduct(result, request);
        return result;
    }
}
