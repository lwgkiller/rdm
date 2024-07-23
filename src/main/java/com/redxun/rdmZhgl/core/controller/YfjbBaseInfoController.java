
package com.redxun.rdmZhgl.core.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmZhgl.core.dao.YfjbChangeNoticeDao;
import com.redxun.rdmZhgl.core.dao.YfjbProductionNoticeDao;
import com.redxun.rdmZhgl.core.service.YfjbBaseInfoService;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.standardManager.core.util.ResultUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.StringUtil;
import com.redxun.org.api.model.IUser;
import com.redxun.saweb.context.ContextUtil;

import java.util.*;

/**
 * 新品试制配置
 *
 * @author zz
 */
@Controller
@RequestMapping("/rdmZhgl/core/yfjb/")
public class YfjbBaseInfoController {
    @Resource
    YfjbBaseInfoService yfjbBaseInfoService;
    @Resource
    CommonInfoManager commonInfoManager;
    @Resource
    YfjbChangeNoticeDao yfjbChangeNoticeDao;
    @Resource
    YfjbProductionNoticeDao yfjbProductionNoticeDao;
    @Resource
    CommonInfoDao commonInfoDao;

    /**
     * 列表
     */
    @RequestMapping("getListPage")
    public ModelAndView getListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/yfjbList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        IUser currentUser = ContextUtil.getCurrentUser();
        mv.addObject("currentUser", currentUser);
        JSONObject resultJson1 = commonInfoManager.hasPermission("YFJB-FGZR");
        JSONObject resultJson2 = commonInfoManager.hasPermission("JBZY");
        JSONObject resultJson3 = commonInfoManager.hasPermission("YFJB-GLY");
        mv.addObject("permission",
            resultJson1.getBoolean("YFJB-FGZR") || resultJson2.getBoolean("JBZY") || resultJson3.getBoolean("YFJB-GLY")
                || resultJson3.getBoolean(RdmConst.ALLDATA_QUERY_KEY) || "admin".equals(ContextUtil.getCurrentUser().getUserNo()));
        mv.addObject("YFJBAdmin",
            resultJson3.getBoolean("YFJB-GLY") || "admin".equals(ContextUtil.getCurrentUser().getUserNo()));
        mv.addObject("JbzyAdmin", resultJson2.getBoolean("JBZY"));
        mv.addObject("deptId", ContextUtil.getCurrentUser().getMainGroupId());
        // 图标跳转处理
        JSONObject paramJson = new JSONObject();
        String majorName = request.getParameter("majorName");
        paramJson.put("majorName", majorName);
        JSONObject searchValue = new JSONObject();
        searchValue.put("name", majorName);
        searchValue.put("type", "YFJB-SSZY");
        JSONObject majorDicObj = commonInfoDao.getDicKeyByValue(searchValue);
        if (majorDicObj != null) {
            paramJson.put("major", majorDicObj.getString("key_"));
        }
        String deptName = request.getParameter("deptName");
        paramJson.put("deptName", deptName);
        String reportType = request.getParameter("reportType");
        paramJson.put("reportType", reportType);
        mv.addObject("paramJson", paramJson);
        return mv;
    }

    /**
     * 获取编辑页面
     */
    @RequestMapping("getEditPage")
    public ModelAndView getEditPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/yfjbEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String id = request.getParameter("id");
        String action = request.getParameter("action");
        String type = request.getParameter("type");
        JSONObject applyObj = new JSONObject();
        if (!StringUtil.isEmpty(id)) {
            applyObj = yfjbBaseInfoService.getObjectById(id);
        }
        if (StringUtil.isEmpty(type)) {
            type = "";
        }
        mv.addObject("action", action);
        mv.addObject("type", type);
        mv.addObject("applyObj", applyObj);
        return mv;
    }

    /**
     * 降本项目进度列表
     */
    @RequestMapping("getProcessPage")
    public ModelAndView getProcessPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/yfjbProcessList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        IUser currentUser = ContextUtil.getCurrentUser();
        String mainId = request.getParameter("mainId");
        String responser = request.getParameter("response");
        Boolean permission = false;
        if (responser.equals(currentUser.getUserId())) {
            permission = true;
        }
        mv.addObject("permission", permission);
        mv.addObject("currentUser", currentUser);
        mv.addObject("mainId", mainId);
        return mv;
    }

    @RequestMapping("processList")
    @ResponseBody
    public JsonPageResult<?> processList(HttpServletRequest request, HttpServletResponse response) {
        return yfjbBaseInfoService.processList(request);
    }

    @RequestMapping("exportBaseInfoExcel")
    public void exportBaseInfoExcel(HttpServletRequest request, HttpServletResponse response) {
        yfjbBaseInfoService.exportBaseInfoExcel(request, response);
    }
    @RequestMapping("exportDelayExcel")
    public void exportDelayExcel(HttpServletRequest request, HttpServletResponse response) {
        yfjbBaseInfoService.exportDelayListExcel(request, response);
    }

    /**
     * 降本项目进度列表
     */
    @RequestMapping("memberPage")
    public ModelAndView getMemberPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/yfjbMemberList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        IUser currentUser = ContextUtil.getCurrentUser();
        String mainId = request.getParameter("mainId");
        String responser = request.getParameter("response");
        Boolean permission = false;
        if (responser.equals(currentUser.getUserId())) {
            permission = true;
        }
        mv.addObject("permission", permission);
        mv.addObject("currentUser", currentUser);
        mv.addObject("mainId", mainId);
        return mv;
    }

    @RequestMapping("memberList")
    @ResponseBody
    public JsonPageResult<?> getMemberList(HttpServletRequest request, HttpServletResponse response) {
        return yfjbBaseInfoService.getMemberList(request);
    }

    @RequestMapping("remove")
    @ResponseBody
    public JSONObject remove(HttpServletRequest request, HttpServletResponse response) {
        JSONObject resultJSON = null;
        resultJSON = yfjbBaseInfoService.remove(request);
        return resultJSON;
    }

    @RequestMapping("copy")
    @ResponseBody
    public JSONObject copy(HttpServletRequest request, HttpServletResponse response) {
        JSONObject resultJSON = null;
        resultJSON = yfjbBaseInfoService.copy(request);
        return resultJSON;
    }

    @RequestMapping("save")
    @ResponseBody
    public JSONObject save(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");
        JSONObject resultJSON = null;
        if (StringUtil.isEmpty(id)) {
            resultJSON = yfjbBaseInfoService.add(request);
        } else {
            resultJSON = yfjbBaseInfoService.update(request);
        }
        return resultJSON;
    }

    @RequestMapping(value = "dealData", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult dealData(HttpServletRequest request, @RequestBody String changeGridDataStr,
        HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "");
        if (StringUtils.isBlank(changeGridDataStr)) {
            result.setSuccess(false);
            result.setMessage("requestBody is blank");
            return result;
        }
        String mainId = request.getParameter("mainId");
        yfjbBaseInfoService.saveOrUpdateItem(changeGridDataStr, mainId);
        return result;
    }

    @RequestMapping(value = "dealMemberData", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult dealMemberData(HttpServletRequest request, @RequestBody String changeGridDataStr,
        HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "");
        if (StringUtils.isBlank(changeGridDataStr)) {
            result.setSuccess(false);
            result.setMessage("requestBody is blank");
            return result;
        }
        String mainId = request.getParameter("mainId");
        yfjbBaseInfoService.saveOrUpdateMember(changeGridDataStr, mainId);
        return result;
    }

    @RequestMapping(value = "list")
    @ResponseBody
    public JsonPageResult<?> getPlanList(HttpServletRequest request, HttpServletResponse response) {
        return yfjbBaseInfoService.query(request);
    }

    @RequestMapping(value = "listInfo")
    @ResponseBody
    public List<Map<String, Object>> getListInfo(HttpServletRequest request, HttpServletResponse response) {
        return yfjbBaseInfoService.getInfoListByMainId(request);
    }

    @RequestMapping("submit")
    @ResponseBody
    public JSONObject submit(HttpServletRequest request, HttpServletResponse response) {
        JSONObject resultJSON = null;
        resultJSON = yfjbBaseInfoService.submit(request);
        return resultJSON;
    }

    @PostMapping("/exportExcel")
    public void exportExcel(HttpServletResponse response, HttpServletRequest request) {
        yfjbBaseInfoService.exportExcel(response, request);
    }

    /**
     * 模板下载
     */
    @RequestMapping("/importTemplateDownload")
    public ResponseEntity<byte[]> importTemplateDownload(HttpServletRequest request, HttpServletResponse response) {
        return yfjbBaseInfoService.importTemplateDownload();
    }

    /**
     * 批量导入
     */
    @RequestMapping("importExcel")
    @ResponseBody
    public JSONObject importExcel(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        yfjbBaseInfoService.importProduct(result, request);
        return result;
    }

    @RequestMapping(value = "conformChangeForm", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject conformChangeForm(HttpServletRequest request, @RequestBody String postData,
        HttpServletResponse response) throws Exception {
        JSONObject postDataJson = new JSONObject();
        if (StringUtils.isNotBlank(postData)) {
            postDataJson = JSONObject.parseObject(postData);
        }
        String mainId = postDataJson.getString("id");
        JSONObject jsonObject = yfjbChangeNoticeDao.getObjectByMainId(mainId);
        if (jsonObject != null) {
            return ResultUtil.result(true, "", null);
        } else {
            return ResultUtil.result(false, "请先添加切换通知单", null);
        }
    }

    @RequestMapping(value = "conformProduceForm", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject conformProduceForm(HttpServletRequest request, @RequestBody String postData,
        HttpServletResponse response) throws Exception {
        JSONObject postDataJson = new JSONObject();
        if (StringUtils.isNotBlank(postData)) {
            postDataJson = JSONObject.parseObject(postData);
        }
        String mainId = postDataJson.getString("id");
        JSONObject jsonObject = yfjbProductionNoticeDao.getObjectByMainId(mainId);
        if (jsonObject != null) {
            return ResultUtil.result(true, "", null);
        } else {
            return ResultUtil.result(false, "请先添加试制通知单", null);
        }
    }

    /**
     * 获取总览页面
     */
    @RequestMapping("overview")
    public ModelAndView overview(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/yfjbOverview.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        return mv;
    }

    /**
     * 获取图形总览页面
     */
    @RequestMapping("figureOverview")
    public ModelAndView figureOverview(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/yfjbFigureOverview.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        return mv;
    }

    /**
     * 获取报表
     */
    @RequestMapping(value = "reportAllYear")
    @ResponseBody
    public List<JSONObject> reportAllYear(HttpServletRequest request, HttpServletResponse response) {
        List<JSONObject> list = new ArrayList<>();
        Map<String, Object> params = new HashMap<>(16);
        params = CommonFuns.getSearchParam(params, request, true);
        String reportYear = CommonFuns.nullToString(params.get("reportYear"));
        String reportRule = CommonFuns.nullToString(params.get("reportRule"));
        yfjbBaseInfoService.genAllYearData(list, reportRule, reportYear);
        return list;
    }

    @RequestMapping("reportFigureData")
    @ResponseBody
    public JSONObject reportFigureData(HttpServletRequest request, HttpServletResponse response) {
        List<JSONObject> list = new ArrayList<>();
        String reportYear = request.getParameter("reportYear");
        String reportRule = request.getParameter("reportRule");
        yfjbBaseInfoService.genAllYearData(list, reportRule, reportYear);
        return ResultUtil.result(true, "获取成功", list);
    }

    @RequestMapping("reportDeptProjectData")
    @ResponseBody
    public JSONObject reportDeptProjectData(HttpServletRequest request, HttpServletResponse response) {
        return yfjbBaseInfoService.getReportDeptProjectNum();
    }

    @RequestMapping("reportMajorData")
    @ResponseBody
    public JSONObject reportMajorData(HttpServletRequest request, HttpServletResponse response) {
        return yfjbBaseInfoService.getMajorData();
    }

    @RequestMapping("reportDelayData")
    @ResponseBody
    public JSONObject reportDelayData(HttpServletRequest request, HttpServletResponse response) {
        return yfjbBaseInfoService.getDelayDataList();
    }

    @RequestMapping("getQuarterData")
    @ResponseBody
    public JSONObject quarterData(HttpServletRequest request, HttpServletResponse response) {
        return yfjbBaseInfoService.getQuarterData(request);
    }

    @RequestMapping("getQuaData")
    @ResponseBody
    public JsonResult quaData(HttpServletRequest request, HttpServletResponse response) {
        return yfjbBaseInfoService.getQuaData(request);
    }

    @RequestMapping("delayListPage")
    public ModelAndView getDelayListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/yfjbDelayList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        IUser currentUser = ContextUtil.getCurrentUser();
        mv.addObject("currentUser", currentUser);
        // 图标跳转处理
        String deptName = request.getParameter("deptName");
        String delayName = request.getParameter("delayName");
        String deptId = "";
        String delayType = "";
        List<JSONObject> deptList = commonInfoDao.getDeptInfoByDeptName(deptName);
        if (deptList != null && deptList.size() > 0) {
            deptId = deptList.get(0).getString("GROUP_ID_");
        }
        switch (delayName) {
            case "试制延期":
                delayType = "productDelay";
                break;
            case "下发切换延期":
                delayType = "changeNoticeDelay";
                break;
            case "切换延期":
                delayType = "changeDelay";
                break;
        }
        mv.addObject("deptId", deptId);
        mv.addObject("delayType", delayType);
        return mv;
    }

    @RequestMapping(value = "delayList")
    @ResponseBody
    public List<JSONObject> getDelayList(HttpServletRequest request, HttpServletResponse response) {
        return yfjbBaseInfoService.getDelayProductList(request);
    }

}
