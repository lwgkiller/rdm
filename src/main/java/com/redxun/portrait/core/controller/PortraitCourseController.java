package com.redxun.portrait.core.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.core.util.DateUtil;
import com.redxun.portrait.core.manager.PortraitCourseManager;
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
@RequestMapping("/portrait/course/")
public class PortraitCourseController {
    @Resource
    PortraitCourseManager portraitCourseManager;
    @Resource
    CommonInfoManager commonInfoManager;
    /**
     * 获取编辑页面
     * */
    @RequestMapping("courseEditPage")
    public ModelAndView getCourseEditPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "portrait/course/portraitCourseEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String action = request.getParameter("action");
        String id = request.getParameter("id");
        JSONObject applyObj = new JSONObject();
        //1.查看、或者修改 根据id查询
        if(ConstantUtil.FORM_EDIT.equals(action)||ConstantUtil.FORM_VIEW.equals(action)){
            applyObj = portraitCourseManager.getObjectById(id);
            if (applyObj.get("courseDate") != null) {
                applyObj.put("courseDate",
                        DateUtil.formatDate((Date)applyObj.get("courseDate"), "yyyy-MM-dd"));
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
    @RequestMapping("courseListPage")
    public ModelAndView getCourseListPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "portrait/course/portraitCourseList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        JSONObject resultJson = commonInfoManager.hasPermission("courseAdmin");
        mv.addObject("permission",resultJson.getBoolean("HX-GLY")||resultJson.getBoolean("courseAdmin")||"admin".equals(ContextUtil.getCurrentUser().getUserNo()));
        return mv;
    }
    /**
     * 获取列表数据
     * */
    @RequestMapping("courseList")
    @ResponseBody
    public JsonPageResult<?> courseList(HttpServletRequest request, HttpServletResponse response) {
        return portraitCourseManager.query(request);
    }
    @RequestMapping("save")
    @ResponseBody
    public JSONObject save(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");
        JSONObject resultJSON = null;
        if(StringUtil.isEmpty(id)){
            resultJSON = portraitCourseManager.add(request);
        }else{
            resultJSON = portraitCourseManager.update(request);
        }
        return resultJSON;
    }
    @RequestMapping("remove")
    @ResponseBody
    public JSONObject remove(HttpServletRequest request, HttpServletResponse response) {
        JSONObject resultJSON = null;
        resultJSON = portraitCourseManager.remove(request);
        return resultJSON;
    }
    /**
     * 获取列表数据
     * */
    @RequestMapping("coursePersonList")
    @ResponseBody
    public List<Map<String,Object>> getCoursePersonList(HttpServletRequest request, HttpServletResponse response) {
        String userId = request.getParameter("userId");
        String reportYear = request.getParameter("reportYear");
        JSONObject paramJson = new JSONObject();
        paramJson.put("userId",userId);
        paramJson.put("reportYear",reportYear);
        return portraitCourseManager.getPersonCourseList(paramJson);
    }
    @RequestMapping("asyncCourse")
    @ResponseBody
    public JSONObject asyncCourse(HttpServletRequest request, HttpServletResponse response) {
        JSONObject resultJSON = null;
        resultJSON = portraitCourseManager.asyncCourse();
        return resultJSON;
    }
}
