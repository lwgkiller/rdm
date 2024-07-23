
package com.redxun.xcmgProjectManager.report.controller;

import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.manager.MybatisBaseManager;
import com.redxun.core.query.QueryFilter;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.controller.MybatisListController;
import com.redxun.saweb.util.QueryFilterBuilder;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import com.redxun.xcmgProjectManager.report.dao.XcmgProjectReportDao;
import com.redxun.xcmgProjectManager.report.entity.YdjfDepsumscore;
import com.redxun.xcmgProjectManager.report.manager.JfzbDepsumscoreManager;
import com.redxun.xcmgProjectManager.report.manager.XcmgProjectParticipateManager;
import com.redxun.xcmgProjectManager.report.manager.XcmgProjectReportManager;
import com.redxun.xcmgProjectManager.report.manager.YdjfDepsumscoreManager;

/**
 * project_baseInfo控制器
 *
 * @author x
 */
@Controller
@RequestMapping("/xcmgProjectManager/report/xcmgProject/")
public class XcmgProjectReportController extends MybatisListController {
    @Resource
    private XcmgProjectReportManager xcmgProjectReportManager;
    @Resource
    private YdjfDepsumscoreManager ydjfDepsumscoreManager;
    @Resource
    private JfzbDepsumscoreManager jfzbDepsumscoreManager;
    @Resource
    private XcmgProjectParticipateManager xcmgProjectParticipateManager;
    @Resource
    private XcmgProjectReportDao xcmgProjectReportDao;
    @Autowired
    private CommonInfoManager commonInfoManager;

    @Override
    protected QueryFilter getQueryFilter(HttpServletRequest request) {
        QueryFilter queryFilter = QueryFilterBuilder.createQueryFilter(request);
        queryFilter.addFieldParam("TENANT_ID_", ContextUtil.getCurrentTenantId());
        return queryFilter;
    }

    @RequestMapping("overview")
    public ModelAndView overview(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mv = getPathView(request);
        mv.addObject("lastMonth",
            XcmgProjectUtil.getPointMonthDateStr(XcmgProjectUtil.getNowLocalDateStr("yyyy-MM"), -1));
        return mv;
    }

    @RequestMapping(value = "queryLbtj", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject queryLbtj(HttpServletRequest request, @RequestBody String postData, HttpServletResponse response)
        throws Exception {
        JSONObject postDataJson = new JSONObject();
        if (StringUtils.isNotBlank(postData)) {
            postDataJson = JSONObject.parseObject(postData);
        }
        return xcmgProjectReportManager.queryLbtj(postDataJson);
    }

    @RequestMapping(value = "queryCgjh", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject queryCgjh(HttpServletRequest request, @RequestBody String postData, HttpServletResponse response)
        throws Exception {
        JSONObject postDataJson = new JSONObject();
        if (StringUtils.isNotBlank(postData)) {
            postDataJson = JSONObject.parseObject(postData);
        }

        return xcmgProjectReportManager.queryCgjh(postDataJson);
    }

    @RequestMapping("queryYwph")
    @ResponseBody
    public JSONObject queryYwph(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return xcmgProjectReportManager.queryYwph();
    }

    @ResponseBody
    @RequestMapping("/queryYdjfDepsumscore")
    public JSONObject queryYdjfDepsumscore(YdjfDepsumscore ydjfDepsumscore) {
        JSONObject json = new JSONObject();
        json.put("success", true);
        json.put("msg", "操作成功!");
        try {
            json.put("data", ydjfDepsumscoreManager.selectList(ydjfDepsumscore));
        } catch (Exception e) {
            logger.error("Exception in queryYdjfDepsumscore", e);
            json.put("msg", e.getMessage());
            json.put("success", false);
        }
        return json;
    }

    @ResponseBody
    @RequestMapping(value = "/queryJfzbDepsumscore", method = RequestMethod.POST)
    public JSONObject queryJfzbDepsumscore(HttpServletRequest request, @RequestBody String postData,
        HttpServletResponse response) {
        JSONObject json = new JSONObject();
        json.put("success", true);
        json.put("msg", "操作成功!");
        JSONObject postDataJson = new JSONObject();
        try {
            if (StringUtils.isNotBlank(postData)) {
                postDataJson = JSONObject.parseObject(postData);
            }
            json.put("data", jfzbDepsumscoreManager.selectList(postDataJson));
        } catch (Exception e) {
            logger.error("Exception in queryJfzbDepsumscore", e);
            json.put("msg", "部门累计标准分占比查询异常！");
            json.put("success", false);
        }
        return json;
    }

    @RequestMapping("progressReport")
    public ModelAndView progressReport(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mv = getPathView(request);
        String clickDeptName = RequestUtil.getString(request, "clickDeptName", "");
        String clickDeptId = "";
        if (StringUtils.isNotBlank(clickDeptName)) {
            Map<String, Object> param = new HashMap<>();
            param.put("deptName", clickDeptName);
            List<JSONObject> deptList = xcmgProjectReportDao.queryDeptByName(param);
            if (deptList != null && !deptList.isEmpty()) {
                clickDeptId = deptList.get(0).getString("GROUP_ID_");
            }
        }
        mv.addObject("clickDeptId", clickDeptId);
        mv.addObject("clickDeptName", clickDeptName);
        return mv;
    }

    @RequestMapping("progressReportList")
    @ResponseBody
    public JsonPageResult<?> progressReportList(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        return xcmgProjectReportManager.progressReportList(request, response, true, "progressReportList");
    }

    @RequestMapping("projectDelayList")
    @ResponseBody
    public JsonPageResult<?> projectDelayList(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        return xcmgProjectReportManager.projectDelayList(request, response, true, "projectDelayList");
    }

    @PostMapping("/exportDelayExcel")
    public void exportDelayExcel(HttpServletResponse response, HttpServletRequest request) {
        xcmgProjectReportManager.exportDelayExcel(response, request);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public MybatisBaseManager getBaseManager() {
        return null;
    }

    /**
     * @auhor zz 获取个人积分链接地址
     */
    @RequestMapping("personScore")
    public ModelAndView personScore(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mv = getPathView(request);
        return mv;
    }

    /**
     * @auhor zz 获取个人积分列表
     */
    @RequestMapping("getPersonScoreList")
    @ResponseBody
    public JsonPageResult<?> getPersonScoreList(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        return xcmgProjectReportManager.getPersonScoreList(request, response);
    }

    /**
     * @auhor zz 获取部门积分链接地址
     */
    @RequestMapping("deptScore")
    public ModelAndView deptScore(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mv = getPathView(request);
        String clickDeptName = RequestUtil.getString(request, "clickDeptName", "");
        String clickDeptId = "";
        if (StringUtils.isNotBlank(clickDeptName)) {
            Map<String, Object> param = new HashMap<>();
            param.put("deptName", clickDeptName);
            List<JSONObject> deptList = xcmgProjectReportDao.queryDeptByName(param);
            if (deptList != null && !deptList.isEmpty()) {
                clickDeptId = deptList.get(0).getString("GROUP_ID_");
            }
        }
        mv.addObject("clickDeptId", clickDeptId);
        mv.addObject("clickDeptName", clickDeptName);
        String ydjfMonth = RequestUtil.getString(request, "ydjfMonth", "");
        if (StringUtils.isNotBlank(ydjfMonth)) {
            String startTime = ydjfMonth + "-01";
            mv.addObject("jfStartTime", startTime);
            Date endTime =
                DateUtil.add(DateUtil.add(DateUtil.parseDate(startTime), Calendar.MONTH, 1), Calendar.DATE, -1);
            mv.addObject("jfEndTime", DateUtil.formatDate(endTime, "yyyy-MM-dd"));
        }
        return mv;
    }

    /**
     * @auhor zz 获取部门积分列表
     */
    @RequestMapping("getDeptScoreList")
    @ResponseBody
    public JsonPageResult<?> getDeptScoreList(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        return xcmgProjectReportManager.getDeptScoreList(request, response);
    }

    /**
     * @author zz 个人积分列表导出
     */
    @PostMapping("/exportPersonScoreExcel")
    public void exportPersonScoreExcel(HttpServletResponse response, HttpServletRequest request) {
        xcmgProjectReportManager.exportPersonScoreExcel(response, request);
    }

    /**
     * @author zz 部门积分列表导出
     */
    @PostMapping("/exportDeptScoreExcel")
    public void exportDeptScoreExcel(HttpServletResponse response, HttpServletRequest request) {
        xcmgProjectReportManager.exportDeptScoreExcel(response, request);
    }

    /**
     * 办公桌面
     */
    @RequestMapping("deskHome")
    public ModelAndView deskHome(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mv = getPathView(request);
        mv.addObject("lastMonth",
            XcmgProjectUtil.getPointMonthDateStr(XcmgProjectUtil.getNowLocalDateStr("yyyy-MM"), -1));
        return mv;
    }

    /**
     * 办公桌面，统计个人前六月积分情况
     */
    @ResponseBody
    @RequestMapping("/deskHomePersonScore")
    public JSONObject deskHomePersonScore(YdjfDepsumscore ydjfDepsumscore) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msg", "操作成功!");
        jsonObject.put("success", true);
        try {
            jsonObject.put("data", xcmgProjectReportManager.deskHomePersonScore(ydjfDepsumscore));
        } catch (Exception e) {
            logger.error("Exception in deskHomePersonScore", e);
            jsonObject.put("success", false);
            jsonObject.put("msg", e.getMessage());
        }
        return jsonObject;
    }

    /**
     * 个人参与项目类别统计
     */
    @RequestMapping(value = "deskHomeProjectType", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject deskHomeProjectType(HttpServletRequest request, @RequestBody String postData,
        HttpServletResponse response) throws Exception {
        JSONObject postDataJson = new JSONObject();
        if (StringUtils.isNotBlank(postData)) {
            postDataJson = JSONObject.parseObject(postData);
        }
        return xcmgProjectReportManager.deskHomeProjectType(postDataJson, response, request);
    }

    /**
     * 个人参与项目进度情况分类统计
     */
    @RequestMapping(value = "deskHomeProjectProgress", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject deskHomeProjectProgress(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        request.setAttribute("deskHome", "projectTypeCondition");
        return xcmgProjectReportManager.deskHomeProjectProgress(response, request);
    }

    /**
     * 个人参与项目进展情况列表
     */
    @RequestMapping("deskHomeProgressReportList")
    @ResponseBody
    public JsonPageResult<?> deskHomeProgressReportList(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        request.setAttribute("deskHome", "personProjectProgress");
        return xcmgProjectReportManager.progressReportList(request, response, false, "deskHomeProgressReportList");
    }

    @RequestMapping("exportProjectProgressExcel")
    public void exportProjectProgressExcel(HttpServletRequest request, HttpServletResponse response) {
        xcmgProjectReportManager.exportProjectProgressExcel(request, response);
    }

    @RequestMapping("participatePage")
    public ModelAndView participatePage(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView("/xcmgProjectManager/report/xcmgProjectParticipate.jsp");
        String userRoleStr = xcmgProjectParticipateManager.toQueryUserRoleStr();
        modelAndView.addObject("userRoleStr", userRoleStr);
        // 是否是工艺分管领导或者工艺技术部人员
        boolean isGYFGLD = commonInfoManager.judgeUserIsPointRole(RdmConst.GY_FGLD, ContextUtil.getCurrentUserId());
        String isGY = "false";
        if (isGYFGLD) {
            isGY = "true";
        } else {
            String currentUserDeptName = ContextUtil.getCurrentUser().getMainGroupName();
            if (RdmConst.GYJSB_NAME.equalsIgnoreCase(currentUserDeptName)) {
                isGY = "true";
            }
        }
        modelAndView.addObject("isGY", isGY);
        return modelAndView;
    }

    @RequestMapping("participateList")
    @ResponseBody
    public JsonPageResult<?> participateList(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        return xcmgProjectParticipateManager.participateList(request);
    }

    @RequestMapping("exportParticipateExcel")
    public void exportParticipateExcel(HttpServletRequest request, HttpServletResponse response) {
        xcmgProjectParticipateManager.exportParticipateExcel(request, response);
    }

    @RequestMapping("evaluateScorePage")
    public ModelAndView evaluateScorePage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView modelAndView = new ModelAndView("/xcmgProjectManager/report/xcmgProjectEvaluateScore.jsp");
        String pointDeptName = RequestUtil.getString(request, "pointDeptName", "");
        String clickDeptId = "";
        if (StringUtils.isNotBlank(pointDeptName)) {
            Map<String, Object> param = new HashMap<>();
            param.put("deptName", pointDeptName);
            List<JSONObject> deptList = xcmgProjectReportDao.queryDeptByName(param);
            if (deptList != null && !deptList.isEmpty()) {
                clickDeptId = deptList.get(0).getString("GROUP_ID_");
            }
        }
        modelAndView.addObject("clickDeptId", clickDeptId);
        modelAndView.addObject("clickDeptName", pointDeptName);
        modelAndView.addObject("projectStartTime", RequestUtil.getString(request, "projectStartTime", ""));
        modelAndView.addObject("projectEndTime", RequestUtil.getString(request, "projectEndTime", ""));
        return modelAndView;
    }

    @RequestMapping("evaluateScoreList")
    @ResponseBody
    public JsonPageResult<?> evaluateScoreList(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        return xcmgProjectReportManager.evaluateScoreList(request, response, true);
    }

    @RequestMapping("exportEvaluateScoreList")
    public void exportEvaluateScoreList(HttpServletRequest request, HttpServletResponse response) {
        xcmgProjectReportManager.exportEvaluateScore(request, response);
    }

    @RequestMapping("personEvaluateScorePage")
    public ModelAndView personEvaluateScorePage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView modelAndView = new ModelAndView("/xcmgProjectManager/report/xcmgPersonEvaluateScore.jsp");
        modelAndView.addObject("projectStartTime", RequestUtil.getString(request, "projectStartTime", ""));
        modelAndView.addObject("projectEndTime", RequestUtil.getString(request, "projectEndTime", ""));
        return modelAndView;
    }

    @RequestMapping("personEvaluateScoreList")
    @ResponseBody
    public JsonPageResult<?> personEvaluateScoreList(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        return xcmgProjectReportManager.personEvaluateScoreList(request, response, true);
    }

    @RequestMapping("exportPersonEvaluateScoreList")
    public void exportPersonEvaluateScoreList(HttpServletRequest request, HttpServletResponse response) {
        xcmgProjectReportManager.exportPersonEvaluateScoreList(request, response);
    }
}
