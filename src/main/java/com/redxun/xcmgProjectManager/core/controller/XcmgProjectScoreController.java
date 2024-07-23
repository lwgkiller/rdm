
package com.redxun.xcmgProjectManager.core.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONArray;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectScoreDao;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectScoreManager;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import com.redxun.xcmgProjectManager.report.dao.XcmgProjectReportDao;

@Controller
@RequestMapping("/xcmgProjectManager/core/xcmgScore/")
public class XcmgProjectScoreController {
    private Logger logger = LoggerFactory.getLogger(XcmgProjectScoreController.class);
    @Resource
    private XcmgProjectScoreManager xcmgProjectScoreManager;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private CommonInfoManager commonInfoManager;
    @Autowired
    private XcmgProjectScoreDao xcmgProjectScoreDao;
    @Autowired
    private XcmgProjectReportDao xcmgProjectReportDao;

    // 新增或编辑、明细、处理任务
    @RequestMapping("list")
    public ModelAndView edit(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "xcmgProjectManager/core/xcmgScoreList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        // 级别
        List<Map<String, String>> projectLevelList = xcmgProjectOtherDao.queryProjectLevel(params);
        JSONArray projectLevelArr = XcmgProjectUtil.convertListMap2JsonArrString(projectLevelList);
        mv.addObject("projectLevel", projectLevelArr);
        // 成员角色
        List<Map<String, String>> memberRoleList = xcmgProjectOtherDao.queryProjectMemRole(params);
        JSONArray memberRoleArr = XcmgProjectUtil.convertListMap2JsonArrString(memberRoleList);
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
        mv.addObject("isGY", isGY);
        return mv.addObject("memberRole", memberRoleArr);
    }

    @RequestMapping("getScoreList")
    @ResponseBody
    public JsonPageResult<?> getProjectList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return xcmgProjectScoreManager.getScoreList(request, response);
    }

    /**
     * @author zz 积分列表导出
     */
    @PostMapping("/exportProjectScoreExcel")
    public void exportProjectScoreExcel(HttpServletResponse response, HttpServletRequest request) {
        xcmgProjectScoreManager.exportProjectScoreExcel(response, request);
    }

    @RequestMapping("/projectStageScoreShow")
    public ModelAndView projectStageScoreShow(HttpServletRequest request, HttpServletResponse response) {
        String projectId = RequestUtil.getString(request, "projectId");
        String userId = RequestUtil.getString(request, "userId");
        String startTime = RequestUtil.getString(request, "startTime", "");
        String endTime = RequestUtil.getString(request, "endTime", "");
        String jspPath = "xcmgProjectManager/core/xcmgScoreProjectStageScoreShow.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        if (StringUtils.isBlank(projectId) || StringUtils.isBlank(userId)) {
            logger.warn("projectId or userId is blank");
            mv.addObject("gridData", new JSONArray());
            return mv;
        }
        List<Map<String, Object>> tempData = null;
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        params.put("projectId", projectId);
        params.put("userId", userId);
        if (StringUtils.isNotBlank(startTime)) {
            params.put("startTime", DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(startTime), -8)));
        }
        if (StringUtils.isNotBlank(endTime)) {
            params.put("endTime", DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(endTime), 16)));
        }
        tempData = xcmgProjectScoreManager.queryUserProjectStageScore(params);
        JSONArray jsonArray = XcmgProjectUtil.convertListMap2JsonArrObject(tempData);
        mv.addObject("gridData", jsonArray);
        return mv;
    }

    @RequestMapping("/projectScoreShow")
    public ModelAndView projectScoreShow(HttpServletRequest request, HttpServletResponse response) {
        String userId = RequestUtil.getString(request, "userId");
        String startTime = RequestUtil.getString(request, "startTime");
        String endTime = RequestUtil.getString(request, "endTime");
        String projectStartTime = RequestUtil.getString(request, "projectStartTime");
        String projectEndTime = RequestUtil.getString(request, "projectEndTime");
        String categoryId = RequestUtil.getString(request, "categoryId");
        String jspPath = "xcmgProjectManager/core/xcmgScoreProjectScoreShow.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        if (StringUtils.isBlank(userId)) {
            logger.warn("userId is blank");
            mv.addObject("gridData", new JSONArray());
            return mv;
        }
        List<Map<String, Object>> tempData = null;
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        params.put("userId", userId);
        if (StringUtils.isNotBlank(startTime)) {
            params.put("startTime", DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(startTime), -8)));
        }
        if (StringUtils.isNotBlank(endTime)) {
            params.put("endTime", DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(endTime), 16)));
        }
        if (StringUtils.isNotBlank(projectStartTime)) {
            params.put("projectStartTime",
                DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(projectStartTime), -8)));
        }
        if (StringUtils.isNotBlank(projectEndTime)) {
            params.put("projectEndTime", DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(projectEndTime), 16)));
        }
        if (StringUtils.isNotBlank(categoryId)) {
            params.put("categoryId", categoryId);
        }
        params.put("sortField", "userProjectScore");
        params.put("sortOrder", "desc");
        tempData = xcmgProjectScoreDao.queryUserProjectScore(params);
        JSONArray jsonArray = XcmgProjectUtil.convertListMap2JsonArrObject(tempData);
        mv.addObject("gridData", jsonArray);
        return mv;
    }

    @RequestMapping("/userScoreShow")
    public ModelAndView userScoreShow(HttpServletRequest request, HttpServletResponse response) {
        String depId = RequestUtil.getString(request, "depId");
        String startTime = RequestUtil.getString(request, "startTime");
        String endTime = RequestUtil.getString(request, "endTime");
        String categoryId = RequestUtil.getString(request, "categoryId");
        String jspPath = "xcmgProjectManager/core/xcmgScoreUserScoreShow.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        if (StringUtils.isBlank(depId)) {
            logger.warn("depId is blank");
            mv.addObject("gridData", new JSONArray());
            return mv;
        }
        List<Map<String, Object>> tempData = null;
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        params.put("userDepId", Arrays.asList(depId));
        if (StringUtils.isNotBlank(startTime)) {
            params.put("startTime", DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(startTime), -8)));
        }
        if (StringUtils.isNotBlank(endTime)) {
            params.put("endTime", DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(endTime), 16)));
        }
        if (StringUtils.isNotBlank(categoryId)) {
            params.put("categoryId", categoryId);
        }
        params.put("sortField", "stageScore");
        params.put("sortOrder", "desc");
        tempData = xcmgProjectReportDao.personScoreList(params);
        JSONArray jsonArray = XcmgProjectUtil.convertListMap2JsonArrObject(tempData);
        mv.addObject("gridData", jsonArray);
        return mv;
    }
}
