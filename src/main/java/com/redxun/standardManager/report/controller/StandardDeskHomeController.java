package com.redxun.standardManager.report.controller;

import java.util.ArrayList;
import java.util.HashMap;
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
import com.redxun.bpm.core.entity.BpmTask;
import com.redxun.bpm.core.manager.BpmTaskManager;
import com.redxun.core.query.Page;
import com.redxun.core.query.QueryFilter;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.controller.GenericController;
import com.redxun.saweb.util.QueryFilterBuilder;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.standardManager.core.util.StandardConstant;
import com.redxun.standardManager.core.util.StandardManagerUtil;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectAbolishManager;
import com.redxun.xcmgProjectManager.core.util.ConstantUtil;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

@Controller
@RequestMapping("/standardManager/report/standard/")
public class StandardDeskHomeController extends GenericController {
    @Resource
    BpmTaskManager bpmTaskManager;
    @Resource
    XcmgProjectAbolishManager xcmgProjectAbolishManager;

    @RequestMapping("deskHome")
    public ModelAndView overview(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = getPathView(request);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        String webappName = WebAppUtil.getProperty("webappName", "rdm");
        String systemCategoryValue = StandardConstant.SYSTEMCATEGORY_GL;
        if ("rdm".equalsIgnoreCase(webappName) && !StandardManagerUtil.judgeGLNetwork(request)) {
            systemCategoryValue = StandardConstant.SYSTEMCATEGORY_JS;
        }
        mv.addObject("systemCategoryValue", systemCategoryValue);
        return mv;
    }

    // 查询待办个数
    @RequestMapping("queryTaskTODONum")
    @ResponseBody
    public JSONObject queryTaskTODONum(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject jsonObject = new JSONObject();
        QueryFilter filter = QueryFilterBuilder.createQueryFilter(request);
        Page pageParam = (Page)filter.getPage();
        pageParam.setPageSize(2000);
        pageParam.setPageIndex(0);
        // 待办
        List<BpmTask> bpmTasks = bpmTaskManager.getByUserId(filter);
        String prjectTaskKeys = WebAppUtil.getProperty("standardTaskKey");
        String[] taskKeys = prjectTaskKeys.split(",");
        List<BpmTask> newTasks = new ArrayList<>();
        String solKey;
        JSONObject solJSON = new JSONObject();
        for (BpmTask bpmTask : bpmTasks) {
            solKey = bpmTask.getSolKey();
            if (solKey == null || ConstantUtil.NULL.equals(solKey) || "".equals(solKey)) {
                Map<String, Object> param = new HashMap<>();
                param.put("solId", bpmTask.getSolId());
                solJSON = xcmgProjectAbolishManager.getBpmSolution(param);
                solKey = solJSON.getString("KEY_");
            }
            if (XcmgProjectUtil.filterTask(solKey, taskKeys)) {
                newTasks.add(bpmTask);
            }
        }

        jsonObject.put("task", newTasks.size());
        return jsonObject;
    }
}
