
package com.redxun.rdmZhgl.core.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmZhgl.core.service.NdkfjhPlanDetailService;
import com.redxun.saweb.context.ContextUtil;

/**
 * 年度开发计划
 * 
 * @author zz
 */
@Controller
@RequestMapping("/rdmZhgl/core/ndkfjh/plan/")
public class NdkfjhPlanController {
    @Resource
    CommonInfoManager commonInfoManager;
    @Resource
    NdkfjhPlanDetailService ndkfjhPlanDetailService;

    /**
     * 列表
     */
    @RequestMapping("overViewPage")
    public ModelAndView getOverViewPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/report/ndkfjhOverview.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String userId = ContextUtil.getCurrentUserId();
        JSONObject adminJson = commonInfoManager.hasPermission("NDKFJH-JHGLY");
        JSONObject fGzrJson = commonInfoManager.hasPermission("JSZXZR");
        Boolean techLeader = commonInfoManager.judgeUserIsDeptRespor(userId, RdmConst.JSGLB_NAME);
        Boolean permission = adminJson.getBoolean("NDKFJH-JHGLY") || adminJson.getBoolean(RdmConst.ALLDATA_QUERY_KEY)
            || fGzrJson.getBoolean("JSZXZR") || techLeader || "admin".equals(ContextUtil.getCurrentUser().getUserNo());
        mv.addObject("permission", permission);
        mv.addObject("userName", ContextUtil.getCurrentUser().getFullname());
        return mv;
    }

    @RequestMapping("reportZrszData")
    @ResponseBody
    public JSONObject reportZrszData(HttpServletRequest request, HttpServletResponse response) {
        return ndkfjhPlanDetailService.reportResponseFinishRate(request);
    }

    @RequestMapping("reportGsjhData")
    @ResponseBody
    public JSONObject reportGsjhData(HttpServletRequest request, HttpServletResponse response) {
        return ndkfjhPlanDetailService.reportDeptFinishRate(request);
    }

    @RequestMapping("reportViewPage")
    public ModelAndView getReportViewPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/report/ndkfjhReportView.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String barType = request.getParameter("barType");
        String userName = request.getParameter("userName");
        String deptName = request.getParameter("deptName");
        String yearMonth = request.getParameter("yearMonth");
        mv.addObject("barType", barType);
        mv.addObject("userName", userName);
        mv.addObject("deptName", deptName);
        mv.addObject("yearMonth", yearMonth);
        return mv;
    }

    @RequestMapping("listReportData")
    @ResponseBody
    public List<Map<String, Object>> getReportDataList(HttpServletRequest request, HttpServletResponse response) {
        return ndkfjhPlanDetailService.getReportDetailList(request);
    }

}
