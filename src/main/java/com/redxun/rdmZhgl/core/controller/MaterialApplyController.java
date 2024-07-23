
package com.redxun.rdmZhgl.core.controller;

import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.core.util.DateFormatUtil;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmZhgl.core.dao.MaterialApplyDao;
import com.redxun.rdmZhgl.core.service.MaterialApplyService;
import com.redxun.standardManager.core.util.ResultUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.manager.MybatisBaseManager;
import com.redxun.core.util.DateUtil;
import com.redxun.org.api.model.IUser;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.controller.MybatisListController;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

/**
 *物料预留号申请功能模块
 * @author zz
 */
@Controller
@RequestMapping("/rdmZhgl/core/material/")
public class MaterialApplyController extends MybatisListController {
    @Resource
    private CommonBpmManager commonBpmManager;
    @Resource
    private MaterialApplyService materialApplyService;
    @Resource
    private MaterialApplyDao materialApplyDao;
    @Resource
    private CommonInfoManager commonInfoManager;
    @SuppressWarnings("rawtypes")
    @Override
    public MybatisBaseManager getBaseManager() {
        return null;
    }
    /**
     * 审批列表页面
     * */
    @RequestMapping("listPage")
    public ModelAndView getListPage(HttpServletRequest request,HttpServletResponse response) throws Exception{
        String jspPath = "rdmZhgl/core/materialApplyList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        IUser currentUser = ContextUtil.getCurrentUser();
        mv.addObject("currentUser", currentUser);
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DATE);
        mv.addObject("currentDay", day);
        return mv;
    }

   /**
    * 获取审批页面
    * */
    @RequestMapping("editPage")
    public ModelAndView getEditPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/materialApplyEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String taskId_ = RequestUtil.getString(request, "taskId_");
        String nodeId = RequestUtil.getString(request, "nodeId");
        String id = RequestUtil.getString(request, "id");
        mv.addObject("taskId_", taskId_);
        // 新增或编辑的时候没有nodeId
        String action = "";
        if (StringUtils.isBlank(nodeId) || nodeId.contains("PROCESS")) {
            action = "edit";
        } else {
            action = "task";
        }
        //取出节点变量，返回到页面
        if (StringUtils.isNotBlank(nodeId)) {
            mv.addObject("nodeId", nodeId);
            List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId,"materialApply",null);
            if (nodeVars != null && !nodeVars.isEmpty()) {
                mv.addObject("nodeVars", JSONObject.toJSONString(nodeVars));
            }
        }
        mv.addObject("action", action);
        Map<String, Object> planApplyObj = null;
        JSONObject applyObj = new JSONObject();
        List<JSONObject> detailList = new ArrayList<>();
        List<JSONObject> docList = new ArrayList<>();
        if (StringUtils.isNotBlank(id)) {
            planApplyObj = materialApplyDao.getObjectById(id);
            applyObj = XcmgProjectUtil.convertMap2JsonObject(planApplyObj);
            if (applyObj.get("finalDate") != null) {
                applyObj.put("finalDate",
                        DateUtil.formatDate((Date)applyObj.get("finalDate"), "yyyy-MM-dd"));
            }
            detailList = materialApplyDao.getDetailList(id);
        }else{
            applyObj.put("CREATE_BY_",ContextUtil.getCurrentUserId());
            applyObj.put("userName",ContextUtil.getCurrentUser().getFullname());
            applyObj.put("deptId",ContextUtil.getCurrentUser().getMainGroupId());
            applyObj.put("deptName",ContextUtil.getCurrentUser().getMainGroupName());
            String finalDate =  DateFormatUtil.format(DateUtil.addMonth(new Date(), 1), "yyyy-MM-dd");
            applyObj.put("finalDate",finalDate);
        }
        applyObj.put("detailList",detailList);
        mv.addObject("applyId", id);
        mv.addObject("applyObj", applyObj);
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DATE);
        mv.addObject("currentDay", day);
        return mv;
    }
    /**
     * 获取list
     * */
    @RequestMapping("queryList")
    @ResponseBody
    public JsonPageResult<?> queryList(HttpServletRequest request, HttpServletResponse response) {
        return materialApplyService.queryList(request, response);
    }
    /**
     * 项目作废表单和流程删除
     * */
    @RequestMapping("delete")
    @ResponseBody
    public JsonResult delete(HttpServletRequest request, HttpServletResponse response) {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String instIdStr = RequestUtil.getString(request, "instIds");
            if (StringUtils.isNotEmpty(uIdStr) && StringUtils.isNotEmpty(instIdStr)) {
                String[] ids = uIdStr.split(",");
                String[] instIds = instIdStr.split(",");
                return materialApplyService.delete(ids, instIds);
            }
        } catch (Exception e) {
            return new JsonResult(false, e.getMessage());
        }
        return new JsonResult(true, "成功删除!");
    }
    /**
     * 明细：查看审批信息
     * */
    @RequestMapping("edit")
    public ModelAndView edit(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = RequestUtil.getString(request, "id");
        String action = RequestUtil.getString(request, "action");
        String status = RequestUtil.getString(request, "status");
        String jspPath = "rdmZhgl/core/materialApplyEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("id", id).addObject("action", action).addObject("status", status);
        Map<String, Object> planApplyObj = null;
        JSONObject applyObj = new JSONObject();
        List<JSONObject> detailList = new ArrayList<>();
        List<JSONObject> docList = new ArrayList<>();
        if (StringUtils.isNotBlank(id)) {
            planApplyObj = materialApplyDao.getObjectById(id);
            applyObj = XcmgProjectUtil.convertMap2JsonObject(planApplyObj);
            if (applyObj.get("finalDate") != null) {
                applyObj.put("finalDate",
                        DateUtil.formatDate((Date)applyObj.get("finalDate"), "yyyy-MM-dd"));
            }
            detailList = materialApplyDao.getDetailList(id);
        }
        mv.addObject("applyObj", applyObj);
        mv.addObject("applyId", id);
        applyObj.put("detailList",detailList);
        applyObj.put("docList",docList);
        mv.addObject("currentTime", XcmgProjectUtil.getNowLocalDateStr("yyyy-MM-dd HH:mm:ss"));
        mv.addObject("currentUserId", ContextUtil.getCurrentUserId());
        mv.addObject("currentUserName", ContextUtil.getCurrentUser().getFullname());
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DATE);
        return mv;
    }
    @RequestMapping(value = "dealDetailData", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult dealDetailData(HttpServletRequest request, @RequestBody String changeGridDataStr,
                               HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "");
        if (StringUtils.isBlank(changeGridDataStr)) {
            result.setSuccess(false);
            result.setMessage("requestBody is blank");
            return result;
        }
        materialApplyService.saveOrUpdateDetail(changeGridDataStr);
        return result;
    }
    /**
     * 物料列表
     * */
    @RequestMapping("listDetailPage")
    public ModelAndView getListDetailPage(HttpServletRequest request,HttpServletResponse response) throws Exception{
        String jspPath = "rdmZhgl/core/materialDetailList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        IUser currentUser = ContextUtil.getCurrentUser();
        mv.addObject("currentUser", currentUser);
        JSONObject admin = commonInfoManager.hasPermission("WLYLH-GLY");
        mv.addObject("permission",admin.getBoolean("WLYLH-GLY")||"admin".equals(ContextUtil.getCurrentUser().getUserNo()));
        return mv;
    }
    @RequestMapping(value = "listDetail")
    @ResponseBody
    public JsonPageResult<?> getDetailReport(HttpServletRequest request, HttpServletResponse response)  {
        return materialApplyService.getDetailReport(request);
    }

    @RequestMapping(value = "sendToSap", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject sendToSap(HttpServletRequest request, @RequestBody String postData, HttpServletResponse response)
            throws Exception {
        JSONObject postDataJson = new JSONObject();
        if (StringUtils.isNotBlank(postData)) {
            postDataJson = JSONObject.parseObject(postData);
        }
        return materialApplyService.sendToSap(postDataJson.getString("applyId"));
    }
    /**
     * 同步状态
     * */
    @RequestMapping("asyncStatus")
    @ResponseBody
    public JSONObject asyncStatus(HttpServletRequest request, HttpServletResponse response) {
        JSONObject resultJson = new JSONObject();
        try {
            String materialCodes = RequestUtil.getString(request, "materialCodes");
            String lineNos = RequestUtil.getString(request, "lineNos");
            String materialCodesArray[] = materialCodes.split(",");
            List<String> materialCodeList = Arrays.asList(materialCodesArray);
            String lineNosArray[] = lineNos.split(",");
            List<String> lineNoList = Arrays.asList(lineNosArray);
            resultJson = materialApplyService.asyncItemStatus(materialCodeList,lineNoList);
        } catch (Exception e) {
            return ResultUtil.result(false,"同步异常！",null);
        }
        return resultJson;
    }
    /**
     * 物料作废
     * */
    @RequestMapping("invalidItems")
    @ResponseBody
    public JSONObject invalidItems(HttpServletRequest request, HttpServletResponse response) {
        JSONObject resultJson = new JSONObject();
        try {
            String materialCodes = RequestUtil.getString(request, "materialCodes");
            String lineNos = RequestUtil.getString(request, "lineNos");
            String materialCodesArray[] = materialCodes.split(",");
            List<String> materialCodeList = Arrays.asList(materialCodesArray);
            String lineNosArray[] = lineNos.split(",");
            List<String> lineNoList = Arrays.asList(lineNosArray);
            resultJson = materialApplyService.invalidItems(materialCodeList,lineNoList);
        } catch (Exception e) {
            return ResultUtil.result(false,"同步异常！",null);
        }
        return resultJson;
    }

    @RequestMapping("checkOrderCode")
    @ResponseBody
    public JSONObject checkOrderCode(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject approveValid = new JSONObject();
        approveValid.put("success", true);
        approveValid.put("message", "");
        String orderCode = RequestUtil.getString(request, "orderCode", "");
        if (StringUtils.isBlank(orderCode)) {
            approveValid.put("success", false);
            approveValid.put("message", "财务订单号不能为空！");
            return approveValid;
        }
        approveValid = materialApplyService.checkOrderCode(orderCode,approveValid);
        return approveValid;
    }
}
