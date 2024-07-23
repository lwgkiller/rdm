package com.redxun.fzsj.core.controller;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.fzsj.core.service.SdmService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/fzsj/core/sdm/")
public class SdmController {
    private static final Logger logger = LoggerFactory.getLogger(SdmController.class);

    @Resource
    private SdmService sdmService;
    /**
     * 查找SDM系统产品族谱
     *
     */
    @RequestMapping("listSdmProduct")
    @ResponseBody
    public  List<JSONObject>  listSdmProduct(HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<JSONObject>  treeList = sdmService.getSdmProductList();
        return treeList;
    }
    /**
     * 查找SDM系统仿真能力清单
     *
     */
    @RequestMapping("listSdmAnalysis")
    @ResponseBody
    public  List<JSONObject>  listSdmAnalysis(HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<JSONObject>  treeList = sdmService.getSdmAnalysisList();
        return treeList;
    }
    @RequestMapping("sdmProjectPage")
    public ModelAndView getSdmProjectPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "fzsj/core/sdmProjectList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        return mv;
    }
    @RequestMapping("sdmProjectList")
    @ResponseBody
    public JsonPageResult<?> getSdmProjectList(HttpServletRequest request, HttpServletResponse response) {
        return sdmService.getSdmProjectList(request);
    }
    @RequestMapping("sdmAssignmentPage")
    public ModelAndView getSdmAssignmentPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "fzsj/core/sdmAssignmentList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        return mv;
    }
    @RequestMapping("sdmAssignmentList")
    @ResponseBody
    public JsonPageResult<?> getSdmAssignmentList(HttpServletRequest request, HttpServletResponse response) {
        return sdmService.getSdmAssignmentList(request);
    }
    @RequestMapping("sdmTaskPage")
    public ModelAndView getSdmTaskPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "fzsj/core/sdmTaskList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        return mv;
    }
    @RequestMapping("sdmTaskList")
    @ResponseBody
    public JsonPageResult<?> getSdmTaskList(HttpServletRequest request, HttpServletResponse response) {
        return sdmService.getSdmTaskList(request);
    }
    /**
     *仿真任务进行打分
     * */
    @RequestMapping("sdmScorePage")
    public ModelAndView getSdmScorePage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "fzsj/core/sdmScoreList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        return mv;
    }
    @RequestMapping("sdmScoreList")
    @ResponseBody
    public JsonPageResult<?> getSdmScoreList(HttpServletRequest request, HttpServletResponse response) {
        return sdmService.getSdmScoreList(request);
    }
    /**
     *采纳信息报表
     * */
    @RequestMapping("sdmAdoptPage")
    public ModelAndView getSdmAdoptPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "fzsj/core/sdmAdoptList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        return mv;
    }
    @RequestMapping("sdmAdoptList")
    @ResponseBody
    public JsonPageResult<?> getSdmAdoptList(HttpServletRequest request, HttpServletResponse response) {
        return sdmService.getSdmAdoptList(request);
    }
    /**
     *实施效果
     * */
    @RequestMapping("sdmImplementPage")
    public ModelAndView getSdmImplementPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "fzsj/core/sdmImplementList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        return mv;
    }
    @RequestMapping("sdmImplementList")
    @ResponseBody
    public JsonPageResult<?> getSdmImplementList(HttpServletRequest request, HttpServletResponse response) {
        return sdmService.getSdmImplementList(request);
    }
}
