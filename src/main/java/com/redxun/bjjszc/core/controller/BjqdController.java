package com.redxun.bjjszc.core.controller;

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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bjjszc.core.dao.BjqdDao;
import com.redxun.bjjszc.core.service.BjqdService;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

@Controller
@RequestMapping("/rdm/core/Bjqd/")
public class BjqdController {
    private static final Logger logger = LoggerFactory.getLogger(BjqdController.class);

    @Resource
    private BjqdService bjqdService;
    @Resource
    private CommonBpmManager commonBpmManager;
    @Resource
    private CommonInfoManager commonInfoManager;
    @Resource
    private XcmgProjectManager xcmgProjectManager;
    @Autowired
    private BjqdDao bjqdDao;

    // 总清单列表跳转
    @RequestMapping("bjqdListPage")
    public ModelAndView bjqdListPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "bjjszc/core/bjqdList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        return mv;
    }

    // 总清单查询
    @RequestMapping("queryBjqd")
    @ResponseBody
    public JsonPageResult<?> queryBjqd(HttpServletRequest request, HttpServletResponse response) {
        return bjqdService.queryBjqdList(request, true);
    }

    // 总清单删除
    @RequestMapping("delBjqd")
    @ResponseBody
    public JsonResult delBjqd(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            if (StringUtils.isNotEmpty(uIdStr)) {
                String[] ids = uIdStr.split(",");
                return bjqdService.delBjqd(ids);
            }
        } catch (Exception e) {
            logger.error("Exception in delBjqd", e);
            return new JsonResult(false, "系统异常！");
        }
        return new JsonResult(true, "成功删除!");
    }

    // 流程总清删除(还原总清单状态)
    @RequestMapping("delBjqdFlow")
    @ResponseBody
    public JsonResult delBjqdFlow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            if (StringUtils.isNotEmpty(uIdStr)) {
                String[] ids = uIdStr.split(",");
                return bjqdService.delBjqdFlow(ids);
            }
        } catch (Exception e) {
            logger.error("Exception in delBjqdFlow", e);
            return new JsonResult(false, "系统异常！");
        }
        return new JsonResult(true, "成功删除!");
    }

    // 流程清单查询
    @RequestMapping("getBjqdDetail")
    @ResponseBody
    public JsonPageResult<?> getBjqdDetail(HttpServletRequest request, HttpServletResponse response) {
        String id = RequestUtil.getString(request, "id");
        return bjqdService.getBjqdDetail(request, id);
    }

    // 流程列表查询
    @RequestMapping("queryBjqdFlowList")
    @ResponseBody
    public JsonPageResult<?> queryBjqdFlowList(HttpServletRequest request, HttpServletResponse response) {
        return bjqdService.queryBjqdFlowList(request, true);
    }

    // 总清单导入模板下载
    @RequestMapping("importTemplateDownload")
    public ResponseEntity<byte[]> importTemplateDownload(HttpServletRequest request, HttpServletResponse response) {
        return bjqdService.importTemplateDownload();
    }

    // 总清单导入
    @RequestMapping("importExcel")
    @ResponseBody
    public JSONObject importExcel(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        bjqdService.importBjqd(result, request);
        return result;
    }

    // 流程中导入“可购买属性”
    @RequestMapping("importExcelFlow")
    @ResponseBody
    public JSONObject importExcelFlow(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        bjqdService.importBjqdFlow(result, request);
        return result;
    }

    // 流程中导入“PMS价格维护”和价格
    @RequestMapping("importExcelFlowPMS")
    @ResponseBody
    public JSONObject importExcelFlowPMS(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        bjqdService.importBjqdFlowPMS(result, request);
        return result;
    }

    // 备件清单流程列表
    @RequestMapping("bjqdFlowListPage")
    public ModelAndView bjqdFlowListPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "bjjszc/core/bjqdFlowList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        return mv;
    }

    @RequestMapping("editPage")
    public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "bjjszc/core/bjqdFlowEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        // 新增或者编辑页面由流程引擎跳转并替换url中的pk和nodeId_
        String id = RequestUtil.getString(request, "id");
        String nodeId = RequestUtil.getString(request, "nodeId");
        String action = RequestUtil.getString(request, "action");
        String status = RequestUtil.getString(request, "status");
        if (StringUtils.isBlank(action)) {
            // 新增或编辑的时候没有nodeId
            if (StringUtils.isBlank(nodeId) || nodeId.contains("PROCESS")) {
                action = "edit";
            } else {
                // 处理任务的时候有nodeId
                action = "task";
            }
        }
        mv.addObject("id", id).addObject("action", action).addObject("status", status);
        // 取出节点变量，返回到页面

        if (StringUtils.isNotBlank(nodeId)) {
            mv.addObject("nodeId", nodeId);
            List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId, "BJQD", null);
            if (nodeVars != null && !nodeVars.isEmpty()) {
                mv.addObject("nodeVars", JSONObject.toJSONString(nodeVars));
            }
        }
        JSONObject currentUserDepInfo = xcmgProjectManager.isCurrentUserDepRespman();
        JSONObject dept = commonInfoManager.queryDeptNameById(currentUserDepInfo.getString("currentUserMainDepId"));
        String deptName = dept.getString("deptname");

        String instId = "";
        if (StringUtils.isNotBlank(id)) {
            instId = bjqdDao.queryInstIdById(id);
        }
        mv.addObject("instId", instId);

        mv.addObject("deptName", deptName);
        mv.addObject("currentUserMainDepId", currentUserDepInfo.getString("currentUserMainDepId"));
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        return mv;
    }

    // 流程保存（主键）
    @RequestMapping("saveBjqd")
    @ResponseBody
    public JsonResult saveBjqd(HttpServletRequest request, @RequestBody String xcmgProjectStr,
        HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "");
        try {
            JSONObject formDataJson = JSONObject.parseObject(xcmgProjectStr);
            if (StringUtils.isBlank(formDataJson.getString("id"))) {
                bjqdService.createBjqd(formDataJson);
            }
        } catch (Exception e) {
            logger.error("Exception in saveBjqd");
            result.setSuccess(false);
            result.setMessage("Exception in saveBjqd");
            return result;
        }
        return result;
    }

    // 总清单导出
    @RequestMapping("exportBjqdList")
    public void exportBjqdList(HttpServletRequest request, HttpServletResponse response) {
        bjqdService.exportBjqdList(request, response);
    }

    // 流程清单导出
    @RequestMapping("exportBjqdFlowList")
    public void exportBjqdFlowList(HttpServletRequest request, HttpServletResponse response) {
        bjqdService.exportBjqdFlowList(request, response);
    }

    // 备件技术支持看板
    @RequestMapping("bjjszcKanban")
    public ModelAndView bjjszcKanban(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "bjjszc/core/bjjszcKanban.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        return mv;
    }

    // 备件清单图表(备件供应形式清单统计)
    @RequestMapping("getBjqdCompletionData")
    @ResponseBody
    public JSONObject getBjqdCompletionData(HttpServletRequest request, HttpServletResponse response,
        @RequestBody String postDataStr) {
        return bjqdService.getBjqdCompletionData(request, response, postDataStr);
    }

    // ..数量类统计-通用(备件供应形式清单统计)
    @RequestMapping("getAmount")
    @ResponseBody
    public JSONObject getAmount(HttpServletRequest request, HttpServletResponse response,
        @RequestBody String postDataStr) {
        return bjqdService.getAmount(request, response, postDataStr);
    }

    // 备件清单图表(可购买属性及PMS价格实时维护)
    @RequestMapping("getBjqdCompletionLineData")
    @ResponseBody
    public JSONObject getBjqdCompletionLineData(HttpServletRequest request, HttpServletResponse response,
        @RequestBody String postDataStr) {
        return bjqdService.getBjqdCompletionLineData(request, response, postDataStr);
    }

    @RequestMapping("judgeKGMDataExist")
    @ResponseBody
    public JSONObject judgeKGMDataExist(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        result.put("success", "false");
        String projectId = RequestUtil.getString(request, "projectId", "");
        if (StringUtils.isBlank(projectId)) {
            result.put("message", "表单id为空！");
            return result;
        }
        Map<String, Object> param = new HashMap<>();
        param.put("projectId", projectId);
        int totalSize = bjqdDao.countBjqdById(param);
        if (totalSize == 0) {
            result.put("message", "请导入物料可购买属性！");
            return result;
        }
        result.put("success", "true");
        return result;
    }
}
