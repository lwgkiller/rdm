package com.redxun.portrait.core.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.core.util.DateUtil;
import com.redxun.portrait.core.manager.PortraitCultureManager;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.saweb.context.ContextUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.util.StringUtil;
import com.redxun.util.ConstantUtil;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author zhangzhen
 */
@RestController
@RequestMapping("/portrait/culture/")
public class PortraitCultureController {
    @Resource
    PortraitCultureManager portraitCultureManager;
    @Resource
    CommonInfoManager commonInfoManager;

    /**
     * 获取编辑页面
     * */
    @RequestMapping("cultureEditPage")
    public ModelAndView getCultureEditPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "portrait/culture/portraitCultureEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String action = request.getParameter("action");
        String id = request.getParameter("id");
        JSONObject applyObj = new JSONObject();
        //1.查看、或者修改 根据id查询
        if(ConstantUtil.FORM_EDIT.equals(action)||ConstantUtil.FORM_VIEW.equals(action)){
            applyObj = portraitCultureManager.getObjectById(id);
            if (applyObj.get("conformDate") != null) {
                applyObj.put("conformDate",
                        DateUtil.formatDate((Date)applyObj.get("conformDate"), "yyyy-MM-dd"));
            }
        }
        if(ConstantUtil.FORM_ADD.equals(action)){
        }
        mv.addObject("action",action);
        mv.addObject("applyObj", applyObj);
        return mv;
    }
    /**
     * 获取列表页面
     * */
    @RequestMapping("cultureListPage")
    public ModelAndView getCultureListPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "portrait/culture/portraitCultureList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        JSONObject resultJson = commonInfoManager.hasPermission("cultureAdmin");
        mv.addObject("permission",resultJson.getBoolean("HX-GLY")||resultJson.getBoolean("cultureAdmin")||"admin".equals(ContextUtil.getCurrentUser().getUserNo()));
        return mv;
    }
    /**
     * 获取列表数据
     * */
    @RequestMapping("cultureList")
    @ResponseBody
    public JsonPageResult<?> cultureList(HttpServletRequest request, HttpServletResponse response) {
        return portraitCultureManager.query(request);
    }
    @RequestMapping("save")
    @ResponseBody
    public JSONObject save(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");
        JSONObject resultJSON = null;
        if(StringUtil.isEmpty(id)){
            resultJSON = portraitCultureManager.add(request);
        }else{
            resultJSON = portraitCultureManager.update(request);
        }
        return resultJSON;
    }
    @RequestMapping("remove")
    @ResponseBody
    public JSONObject remove(HttpServletRequest request, HttpServletResponse response) {
        JSONObject resultJSON = null;
        resultJSON = portraitCultureManager.remove(request);
        return resultJSON;
    }
    /**
     * 获取列表数据
     * */
    @RequestMapping("culturePersonList")
    @ResponseBody
    public List<Map<String,Object>> getCulturePersonList(HttpServletRequest request, HttpServletResponse response) {
        String userId = request.getParameter("userId");
        String reportYear = request.getParameter("reportYear");
        JSONObject paramJson = new JSONObject();
        paramJson.put("userId",userId);
        paramJson.put("reportYear",reportYear);
        return portraitCultureManager.getPersonCultureList(paramJson);
    }
    @RequestMapping("asyncCulture")
    @ResponseBody
    public JSONObject asyncCulture(HttpServletRequest request, HttpServletResponse response) {
        JSONObject resultJSON = null;
        resultJSON = portraitCultureManager.asyncCulture();
        return resultJSON;
    }
}
