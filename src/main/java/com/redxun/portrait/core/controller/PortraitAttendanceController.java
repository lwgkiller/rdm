package com.redxun.portrait.core.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.portrait.core.manager.PortraitAttendanceManager;
import com.redxun.rdmCommon.core.manager.CommonFuns;
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

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author zhangzhen
 */
@RestController
@RequestMapping("/portrait/attendance/")
public class PortraitAttendanceController {
    @Resource
    PortraitAttendanceManager portraitAttendanceManager;
    @Resource
    CommonInfoManager commonInfoManager;
    /**
     * 获取编辑页面
     * */
    @RequestMapping("attendanceEditPage")
    public ModelAndView getAttendanceEditPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "portrait/attendance/portraitAttendanceEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String action = request.getParameter("action");
        String id = request.getParameter("id");
        JSONObject applyObj = new JSONObject();
        //1.查看、或者修改 根据id查询
        if(ConstantUtil.FORM_EDIT.equals(action)||ConstantUtil.FORM_VIEW.equals(action)){
            applyObj = portraitAttendanceManager.getObjectById(id);
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
    @RequestMapping("attendanceListPage")
    public ModelAndView getAttendanceListPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "portrait/attendance/portraitAttendanceList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        JSONObject resultJson = commonInfoManager.hasPermission("cultureAdmin");
        mv.addObject("permission",resultJson.getBoolean("HX-GLY")||"admin".equals(ContextUtil.getCurrentUser().getUserNo()));
        return mv;
    }
    /**
     * 获取列表数据
     * */
    @RequestMapping("attendanceList")
    @ResponseBody
    public JsonPageResult<?> attendanceList(HttpServletRequest request, HttpServletResponse response) {
        return portraitAttendanceManager.query(request);
    }
    /**
     * 获取列表数据
     * */
    @RequestMapping("attendancePersonList")
    @ResponseBody
    public List<Map<String,Object>> getAttendancePersonList(HttpServletRequest request, HttpServletResponse response) {
        String userId = request.getParameter("userId");
        String reportYear = request.getParameter("reportYear");
        JSONObject paramJson = new JSONObject();
        paramJson.put("userId",userId);
        paramJson.put("reportYear",reportYear);
        return portraitAttendanceManager.getPersonAttendanceList(paramJson);
    }
    @RequestMapping("save")
    @ResponseBody
    public JSONObject save(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");
        JSONObject resultJSON = null;
        if(StringUtil.isEmpty(id)){
            resultJSON = portraitAttendanceManager.add(request);
        }else{
            resultJSON = portraitAttendanceManager.update(request);
        }
        return resultJSON;
    }
    @RequestMapping("remove")
    @ResponseBody
    public JSONObject remove(HttpServletRequest request, HttpServletResponse response) {
        JSONObject resultJSON = null;
        resultJSON = portraitAttendanceManager.remove(request);
        return resultJSON;
    }
    @RequestMapping("asyncAttendance")
    @ResponseBody
    public JSONObject asyncAttendance(HttpServletRequest request, HttpServletResponse response) {
        JSONObject resultJSON = null;
        String yearMonthParam = request.getParameter("yearMonth");
        String yearMonth = CommonFuns.convertDateToStr(new Date(yearMonthParam), "yyyy-MM");
        resultJSON = portraitAttendanceManager.asyncAttendance(yearMonth);
        return resultJSON;
    }
}
