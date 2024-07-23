
package com.redxun.drbfm.core.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.redxun.core.json.JsonPageResult;
import com.redxun.drbfm.core.service.DrbfmReportService;

/**
 * project_baseInfo控制器
 *
 * @author x
 */
@Controller
@RequestMapping("/drbfm/report/")
public class DrbfmReportController {
    @Resource
    private DrbfmReportService drbfmReportService;

    @RequestMapping("drbfmQuotaReport")
    public ModelAndView overview(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "drbfm/report/drbfmQuotaReport.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        return mv;
    }

    @RequestMapping("quotaReportList")
    @ResponseBody
    public JsonPageResult<?> quotaReportList(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        return drbfmReportService.quotaReportList(request, response, true);
    }

    @RequestMapping("exportQuotaReport")
    public void exportQuotaReport(HttpServletRequest request, HttpServletResponse response) {
//        drbfmReportService.exportQuotaReport(request, response);
    }
}
