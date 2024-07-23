package com.redxun.portrait.core.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.portrait.core.dao.PortraitDocumentDao;
import com.redxun.portrait.core.manager.PortraitDocumentManager;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.sys.org.dao.OsUserDao;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;



/**
 * @author zhangzhen
 */
@RestController
@RequestMapping("/portrait/document/")
public class PortraitDocumentController {
    @Resource
    PortraitDocumentManager portraitDocumentManager;
    @Resource
    CommonInfoManager commonInfoManager;
    @Resource
    PortraitDocumentDao portraitDocumentDao;
    /**
     * 获取列表页面
     * */
    @RequestMapping("documentListPage")
    public ModelAndView getDocumentListPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "portrait/document/portraitDocumentList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("permission","admin".equals(ContextUtil.getCurrentUser().getUserNo()));
        return mv;
    }
    /**
     * 获取列表数据
     * */
    @RequestMapping("documentList")
    @ResponseBody
    public JSONArray documentList(HttpServletRequest request, HttpServletResponse response) {
        return portraitDocumentManager.getUserList(request);

    }
    /**
     * 获取档案详情页
     * */
    @RequestMapping("documentDetail")
    @ResponseBody
    public ModelAndView getDetailPage(HttpServletRequest request, HttpServletResponse response){
        String jspPath = "portrait/document/portraitDocDetail.jsp";
        String userId = request.getParameter("userId");
        String reportYear = request.getParameter("reportYear");
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("userId",userId);
        mv.addObject("reportYear", reportYear);
        return mv;
    }
    /**
     * 获取用户详情页面
     * */
    @RequestMapping("userInfoPage")
    public ModelAndView getUserInfoPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "portrait/document/portraitUserInfo.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String userId = request.getParameter("userId");
        JSONObject applyObj = portraitDocumentDao.getUserInfoById(userId);
        mv.addObject("applyObj", applyObj);
        return mv;
    }
    /**
     * 获取三高一可列表页面
     * */
    @RequestMapping("sgykPersonListPage")
    public ModelAndView getSGYKPersonListPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "portrait/document/portraitSGYKPersonList.jsp";
        String userId = request.getParameter("userId");
        String reportYear = request.getParameter("reportYear");
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("userId",userId);
        mv.addObject("reportYear", reportYear);
        return mv;
    }
    /**
     * 获取技术创新列表页面
     * */
    @RequestMapping("innovatePersonListPage")
    public ModelAndView getInnovatePersonListPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "portrait/document/portraitInnovateList.jsp";
        String userId = request.getParameter("userId");
        String reportYear = request.getParameter("reportYear");
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("userId",userId);
        mv.addObject("reportYear", reportYear);
        return mv;
    }
    /**
     * 获取技术协同页面
     * */
    @RequestMapping("teamWorkPersonListPage")
    public ModelAndView getTeamWorkListPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "portrait/document/portraitTeamPersonList.jsp";
        String userId = request.getParameter("userId");
        String reportYear = request.getParameter("reportYear");
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("userId",userId);
        mv.addObject("reportYear", reportYear);
        return mv;
    }
    /**
     * 获取敬业表现页面
     * */
    @RequestMapping("employListPage")
    public ModelAndView getEmployListPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "portrait/document/portraitEmployList.jsp";
        String userId = request.getParameter("userId");
        String reportYear = request.getParameter("reportYear");
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("userId",userId);
        mv.addObject("reportYear", reportYear);
        return mv;
    }
    /**
     * 获取月度绩效页面
     * */
    @RequestMapping("performanceListPage")
    public ModelAndView getPerformanceListPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "portrait/document/portraitPerformancePersonList.jsp";
        String userId = request.getParameter("userId");
        String reportYear = request.getParameter("reportYear");
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("userId",userId);
        mv.addObject("reportYear", reportYear);
        return mv;
    }
    /**
     * 获取其他类页面
     * */
    @RequestMapping("otherListPage")
    public ModelAndView getOtherListPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "portrait/document/portraitOtherList.jsp";
        String userId = request.getParameter("userId");
        String reportYear = request.getParameter("reportYear");
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("userId",userId);
        mv.addObject("reportYear", reportYear);
        return mv;
    }
    /**
     * 获取个人画像页面
     * */
    @RequestMapping("personShowPage")
    public ModelAndView getPersonShowPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "portrait/document/portraitPersonShow.jsp";
        String userId = request.getParameter("userId");
        String reportYear = request.getParameter("reportYear");
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("userId",userId);
        mv.addObject("reportYear", reportYear);
        return mv;
    }
    /**
     * 获取对照图页面
     * */
    @RequestMapping("compareShowPage")
    public ModelAndView getCompareShowPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "portrait/document/portraitCompareShow.jsp";
        String userId1 = request.getParameter("userId1");
        String userId2 = request.getParameter("userId2");
        String reportYear = request.getParameter("reportYear");
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("userId1",userId1);
        mv.addObject("userId2",userId2);
        mv.addObject("reportYear", reportYear);
        return mv;
    }
    /**
     * 获取列表数据
     * */
    @RequestMapping("personScore")
    @ResponseBody
    public JSONObject getPersonScore(HttpServletRequest request, HttpServletResponse response) {
        return portraitDocumentManager.getPersonScore(request);
    }
    /**
     * 获取列表数据
     * */
    @RequestMapping("personYearScore")
    @ResponseBody
    public JSONObject getPersonYearScore(HttpServletRequest request, HttpServletResponse response) {
        return portraitDocumentManager.getPersonScore(request);
    }
    @RequestMapping("compareScore")
    @ResponseBody
    public JSONArray getCompareScore(HttpServletRequest request, HttpServletResponse response) {
        return portraitDocumentManager.getCompareScore(request);
    }
}
