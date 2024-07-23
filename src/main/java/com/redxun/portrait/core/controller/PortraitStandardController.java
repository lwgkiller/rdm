package com.redxun.portrait.core.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import com.redxun.core.util.DateUtil;
import com.redxun.portrait.core.manager.PortraitStandardManager;
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
@RequestMapping("/portrait/standard/")
public class PortraitStandardController {
    @Resource
    PortraitStandardManager portraitStandardManager;
    @Resource
    CommonInfoManager commonInfoManager;
    /**
     * 获取编辑页面
     * */
    @RequestMapping("standardEditPage")
    public ModelAndView getStandardEditPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "portrait/standard/portraitStandardEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String action = request.getParameter("action");
        String id = request.getParameter("id");
        JSONObject applyObj = new JSONObject();
        //1.查看、或者修改 根据id查询
        if(ConstantUtil.FORM_EDIT.equals(action)||ConstantUtil.FORM_VIEW.equals(action)){
            applyObj = portraitStandardManager.getObjectById(id);
            if (applyObj.get("publishDate") != null) {
                applyObj.put("publishDate",
                        DateUtil.formatDate((Date)applyObj.get("publishDate"), "yyyy-MM-dd"));
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
    @RequestMapping("standardListPage")
    public ModelAndView getStandardListPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "portrait/standard/portraitStandardList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        JSONObject resultJson = commonInfoManager.hasPermission("standardAdmin");
        mv.addObject("permission",resultJson.getBoolean("HX-GLY")||resultJson.getBoolean("standardAdmin")||"admin".equals(ContextUtil.getCurrentUser().getUserNo()));
        return mv;
    }
    /**
     * 获取列表数据
     * */
    @RequestMapping("standardList")
    @ResponseBody
    public JsonPageResult<?> standardList(HttpServletRequest request, HttpServletResponse response) {
        return portraitStandardManager.query(request);
    }
    /**
     * 获取列表数据
     * */
    @RequestMapping("standardPersonList")
    @ResponseBody
    public List<Map<String,Object>> getStandardPersonList(HttpServletRequest request, HttpServletResponse response) {
        String userId = request.getParameter("userId");
        String reportYear = request.getParameter("reportYear");
        JSONObject paramJson = new JSONObject();
        paramJson.put("userId",userId);
        paramJson.put("reportYear",reportYear);
        return portraitStandardManager.getPersonStandardList(paramJson);
    }
    @RequestMapping("save")
    @ResponseBody
    public JSONObject save(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");
        JSONObject resultJSON = null;
        if(StringUtil.isEmpty(id)){
            resultJSON = portraitStandardManager.add(request);
        }else{
            resultJSON = portraitStandardManager.update(request);
        }
        return resultJSON;
    }
    @RequestMapping("remove")
    @ResponseBody
    public JSONObject remove(HttpServletRequest request, HttpServletResponse response) {
        JSONObject resultJSON = null;
        resultJSON = portraitStandardManager.remove(request);
        return resultJSON;
    }
    @RequestMapping("asyncStandard")
    @ResponseBody
    public JSONObject asyncStandard(HttpServletRequest request, HttpServletResponse response) {
        JSONObject resultJSON = null;
        resultJSON = portraitStandardManager.asyncStandard();
        return resultJSON;
    }
}
