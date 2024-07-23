
package com.redxun.rdmZhgl.core.controller;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.OfficeDocPreview;
import com.redxun.core.util.StringUtil;
import com.redxun.org.api.model.IUser;
import com.redxun.rdmCommon.core.manager.CommonBpmManager;
import com.redxun.rdmZhgl.core.dao.BbsBaseInfoDao;
import com.redxun.rdmZhgl.core.dao.BbsNoticeDao;
import com.redxun.rdmZhgl.core.service.BbsBaseInfoService;
import com.redxun.rdmZhgl.core.service.BbsDiscussService;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.standardManager.core.dao.StandardDao;
import com.redxun.sys.core.dao.SubsystemDao;
import com.redxun.sys.core.entity.Subsystem;
import com.redxun.sys.core.util.SysPropertiesUtil;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

/**
 * 论坛
 *
 * @author zz
 */
@Controller
@RequestMapping("/rdmZhgl/core/bbs/")
public class BbsBaseInfoController {
    @Resource
    BbsBaseInfoService bbsBaseInfoService;
    @Resource
    BbsBaseInfoDao bbsBaseInfoDao;
    @Resource
    BbsDiscussService bbsDiscussService;
    @Resource
    private StandardDao standardDao;
    @Resource
    private BbsNoticeDao bbsNoticeDao;
    @Resource
    private SubsystemDao subsystemDao;
    @Resource
    private CommonBpmManager commonBpmManager;

    /**
     * 列表
     */
    @RequestMapping("getListPage")
    public ModelAndView getListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/bbsList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String plate = request.getParameter("plate");
        String model = request.getParameter("model");
        mv.addObject("plate", plate);
        mv.addObject("model", model);
        IUser currentUser = ContextUtil.getCurrentUser();
        mv.addObject("currentUser", currentUser);
        return mv;
    }

    /**
     * 获取编辑页面
     */
    @RequestMapping("getEditPage")
    public ModelAndView getEditPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/bbsEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String id = request.getParameter("id");
        String action = request.getParameter("action");
        String plate = request.getParameter("plate");
        String model = request.getParameter("model");
        String recordId = request.getParameter("recordId");
        String flag = request.getParameter("flag");
        JSONObject applyObj = new JSONObject();
        if (!StringUtil.isEmpty(id)) {
            applyObj = bbsBaseInfoDao.getObject(id);
        } else {
            applyObj.put("plate", plate);
            applyObj.put("model", model);
            if (StringUtil.isNotEmpty(plate)) {
                Subsystem subsystem = subsystemDao.getByKey(plate);
                if (subsystem != null) {
                    applyObj.put("direction", subsystem.getName());
                }
                if (StringUtil.isNotEmpty(recordId)) {
                    if ("standardManager".equals(plate)) {
                        JSONObject standardObj = standardDao.queryStandardById(recordId);
                        if (standardObj != null) {
                            applyObj.put("title", standardObj.getString("standardName"));
                        }
                    }
                }
            }
            applyObj.put("recordId", recordId);
        }
        mv.addObject("action", action);
        mv.addObject("flag", flag);
        mv.addObject("applyObj", applyObj);
        return mv;
    }

    /**
     * 获取编辑页面
     */
    @RequestMapping("applyEditPage")
    public ModelAndView getApplyEditPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/bbsApplyEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String id = request.getParameter("id");
        String bbsId = request.getParameter("bbsId");
        if (StringUtil.isEmpty(id) && StringUtil.isNotEmpty(bbsId)) {
            id = bbsId;
        }
        String plate = request.getParameter("plate");
        String model = request.getParameter("model");
        String bbsType = request.getParameter("bbsType");
        String recordId = request.getParameter("recordId");
        String flag = request.getParameter("flag");
        String taskId_ = RequestUtil.getString(request, "taskId_");
        String nodeId = RequestUtil.getString(request, "nodeId");
        String action;
        JSONObject applyObj = new JSONObject();
        // 新增或编辑的时候没有nodeId
        if (StringUtils.isBlank(nodeId) || nodeId.contains("PROCESS")) {
            action = "edit";
        } else {
            action = "task";
        }
        // 取出节点变量，返回到页面
        if (StringUtils.isNotBlank(nodeId)) {
            mv.addObject("nodeId", nodeId);
            List<Map<String, String>> nodeVars = commonBpmManager.queryNodeVarsByParam(nodeId, "bbsFlow", null);
            if (nodeVars != null && !nodeVars.isEmpty()) {
                mv.addObject("nodeVars", JSONObject.toJSONString(nodeVars));
            }
        }
        mv.addObject("action", action);
        if (!StringUtil.isEmpty(id)) {
            applyObj = bbsBaseInfoDao.getObject(id);
            if (applyObj != null) {
                if (applyObj.get("planFinishDate") != null) {
                    applyObj.put("planFinishDate",
                        DateUtil.formatDate((Date)applyObj.get("planFinishDate"), "yyyy-MM-dd"));
                }
            }
            JSONObject paramJson = new JSONObject();
            paramJson.put("mainId", id);
            List<JSONObject> list = bbsDiscussService.getDiscuss(paramJson);
            applyObj.put("discussNum", list.size());
            applyObj.put("createTime", DateUtil.formatDate(applyObj.getDate("CREATE_TIME_"), "yyyy-MM-dd HH:mm:ss"));
        } else {
            applyObj.put("plate", plate);
            applyObj.put("model", model);
            applyObj.put("bbsType", bbsType);
            if (StringUtil.isNotEmpty(plate)) {
                Subsystem subsystem = subsystemDao.getByKey(plate);
                if (subsystem != null) {
                    applyObj.put("direction", subsystem.getName());
                }
                if (StringUtil.isNotEmpty(recordId)) {
                    if ("standardManager".equals(plate)) {
                        JSONObject standardObj = standardDao.queryStandardById(recordId);
                        if (standardObj != null) {
                            applyObj.put("title", standardObj.getString("standardName"));
                        }
                    }
                }
            }
            applyObj.put("recordId", recordId);
            applyObj.put("discussNum", 0);
            applyObj.put("publisher", ContextUtil.getCurrentUser().getFullname());
            applyObj.put("createTime", DateUtil.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
        }
        mv.addObject("taskId_", taskId_);
        mv.addObject("flag", flag);
        mv.addObject("applyObj", applyObj);
        return mv;
    }

    /**
     * 明细：查看审批信息
     */
    @RequestMapping("applyDetail")
    public ModelAndView applyDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/bbsApplyEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String id = request.getParameter("id");

        JSONObject applyObj = bbsBaseInfoDao.getObject(id);
        if (applyObj != null) {
            if (applyObj.get("planFinishDate") != null) {
                applyObj.put("planFinishDate", DateUtil.formatDate((Date)applyObj.get("planFinishDate"), "yyyy-MM-dd"));
            }
        }
        JSONObject paramJson = new JSONObject();
        paramJson.put("mainId", id);
        List<JSONObject> list = bbsDiscussService.getDiscuss(paramJson);
        applyObj.put("discussNum", list.size());
        applyObj.put("createTime", DateUtil.formatDate(applyObj.getDate("CREATE_TIME_"), "yyyy-MM-dd HH:mm:ss"));
        mv.addObject("action", "detail");
        mv.addObject("applyObj", applyObj);
        mv.addObject("flag", "");
        mv.addObject("bbsType", applyObj.getString("bbsType"));
        mv.addObject("nodeVars", "");
        return mv;
    }

    @RequestMapping("viewPage")
    public ModelAndView getViewPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "rdmZhgl/core/bbsView.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String id = request.getParameter("id");
        JSONObject applyObj = new JSONObject();
        applyObj = bbsBaseInfoDao.getObject(id);
        applyObj.put("CREATE_TIME_", DateUtil.formatDate((Date)applyObj.get("CREATE_TIME_"), "yyyy-MM-dd HH:mm:ss"));
        JSONObject paramJson = new JSONObject();
        paramJson.put("mainId", id);
        List<JSONObject> list = bbsDiscussService.getDiscuss(paramJson);
        applyObj.put("discussNum", list.size());
        mv.addObject("applyObj", applyObj);
        String readStatus = request.getParameter("readStatus");
        if (StringUtil.isNotEmpty(readStatus)) {
            Map<String, Object> paramMap = new HashMap<>(16);
            paramMap.put("userId", ContextUtil.getCurrentUserId());
            paramMap.put("mainId", id);
            paramMap.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            paramMap.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            bbsNoticeDao.updateReadStatus(paramMap);
        }
        return mv;
    }

    @RequestMapping("remove")
    @ResponseBody
    public JSONObject remove(HttpServletRequest request, HttpServletResponse response) {
        JSONObject resultJSON = null;
        resultJSON = bbsBaseInfoService.remove(request);
        return resultJSON;
    }

    @RequestMapping("save")
    @ResponseBody
    public JSONObject save(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");
        JSONObject resultJSON = null;
        if (StringUtil.isEmpty(id)) {
            resultJSON = bbsBaseInfoService.add(request);
        } else {
            resultJSON = bbsBaseInfoService.update(request);
        }
        return resultJSON;
    }

    @RequestMapping(value = "list")
    @ResponseBody
    public JsonPageResult<?> getPlanList(HttpServletRequest request, HttpServletResponse response) {
        return bbsBaseInfoService.query(request);
    }

    @RequestMapping("imagePreview")
    @ResponseBody
    public void imagePreview(HttpServletRequest request, HttpServletResponse response) {
        String fileName = RequestUtil.getString(request, "newFileName");
        String picUrl = SysPropertiesUtil.getGlobalProperty("BBS_PIC_URL");
        String originalFilePath = picUrl + File.separator + "image" + File.separator + fileName;
        OfficeDocPreview.imagePreview(originalFilePath, response);
    }

    /**
     * 列表
     */
    @RequestMapping("listAllPage")
    public ModelAndView getListAllPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/bbsAllList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        IUser currentUser = ContextUtil.getCurrentUser();
        mv.addObject("currentUser", currentUser);
        return mv;
    }

    @RequestMapping(value = "bbsList")
    @ResponseBody
    public JsonPageResult<?> getBBsList(HttpServletRequest request, HttpServletResponse response) {
        return bbsBaseInfoService.getBbsList(request);
    }

    @RequestMapping("exportExcel")
    public void exportExcel(HttpServletRequest request, HttpServletResponse response) {
        bbsBaseInfoService.exportBaseInfoExcel(request, response);
    }

    @RequestMapping("open")
    @ResponseBody
    public JSONObject openPost(HttpServletRequest request, HttpServletResponse response) {
        JSONObject resultJSON = null;
        resultJSON = bbsBaseInfoService.openPost(request);
        return resultJSON;
    }

    @RequestMapping("close")
    @ResponseBody
    public JSONObject closePost(HttpServletRequest request, HttpServletResponse response) {
        JSONObject resultJSON = null;
        resultJSON = bbsBaseInfoService.closePost(request);
        return resultJSON;
    }

    @RequestMapping("removePost")
    @ResponseBody
    public JSONObject removePost(HttpServletRequest request, HttpServletResponse response) {
        JSONObject resultJSON = null;
        resultJSON = bbsBaseInfoService.removePost(request);
        return resultJSON;
    }

    /**
     * 获取总览报表
     */
    @RequestMapping("overSeePage")
    public ModelAndView getOverSeePage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "rdmZhgl/report/bbsOverSee.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        return mv;
    }

    /**
     * 获取编辑页面
     */
    @RequestMapping("reApplyPage")
    public ModelAndView getReApplyPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/bbsApplyEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String id = request.getParameter("id");
        String action = "edit";;
        JSONObject applyObj = new JSONObject();
        mv.addObject("action", action);
        if (!StringUtil.isEmpty(id)) {
            applyObj = bbsBaseInfoDao.getObject(id);
            if (applyObj != null) {
                if (applyObj.get("planFinishDate") != null) {
                    applyObj.put("planFinishDate",
                        DateUtil.formatDate((Date)applyObj.get("planFinishDate"), "yyyy-MM-dd"));
                }
            }
            JSONObject paramJson = new JSONObject();
            paramJson.put("mainId", id);
            List<JSONObject> list = bbsDiscussService.getDiscuss(paramJson);
            applyObj.put("discussNum", list.size());
        }
        mv.addObject("taskId_", "");
        mv.addObject("flag", false);
        mv.addObject("applyObj", applyObj);
        return mv;
    }

}
