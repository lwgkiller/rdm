package com.redxun.portrait.core.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.portrait.core.manager.PortraitNoticeManager;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.saweb.context.ContextUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.util.StringUtil;
import com.redxun.util.ConstantUtil;

import java.util.List;
import java.util.Map;

/**
 * @author zhangzhen
 */
@RestController
@RequestMapping("/portrait/notice/")
public class PortraitNoticeController {
    @Resource
    PortraitNoticeManager portraitNoticeManager;
    @Resource
    CommonInfoManager commonInfoManager;
    /**
     * 获取编辑页面
     * */
    @RequestMapping("noticeEditPage")
    public ModelAndView getBidPlanEditPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "portrait/notice/portraitNoticeEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String action = request.getParameter("action");
        String id = request.getParameter("id");
        JSONObject applyObj = new JSONObject();
        //1.查看、或者修改 根据id查询
        if(ConstantUtil.FORM_EDIT.equals(action)||ConstantUtil.FORM_VIEW.equals(action)){
            applyObj = portraitNoticeManager.getObjectById(id);
        }
        if(ConstantUtil.FORM_ADD.equals(action)){
        }
        mv.addObject("action",action);
        mv.addObject("applyObj", applyObj);
        return mv;
    }
    /**
     * 获取列表页面
     * */
    @RequestMapping("noticeListPage")
    public ModelAndView getBidPlanListPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "portrait/notice/portraitNoticeList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        JSONObject resultJson = commonInfoManager.hasPermission("noticeAdmin");
        mv.addObject("permission",resultJson.getBoolean("HX-GLY")||resultJson.getBoolean("noticeAdmin")||"admin".equals(ContextUtil.getCurrentUser().getUserNo()));
        return mv;
    }
    /**
     * 获取列表数据
     * */
    @RequestMapping("noticeList")
    @ResponseBody
    public JsonPageResult<?> bidPlanList(HttpServletRequest request, HttpServletResponse response) {
        return portraitNoticeManager.query(request);
    }
    @RequestMapping("save")
    @ResponseBody
    public JSONObject save(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");
        JSONObject resultJSON = null;
        if(StringUtil.isEmpty(id)){
            resultJSON = portraitNoticeManager.add(request);
        }else{
            resultJSON = portraitNoticeManager.update(request);
        }
        return resultJSON;
    }
    @RequestMapping("remove")
    @ResponseBody
    public JSONObject remove(HttpServletRequest request, HttpServletResponse response) {
        JSONObject resultJSON = null;
        resultJSON = portraitNoticeManager.remove(request);
        return resultJSON;
    }
    /**
     * 获取列表数据
     * */
    @RequestMapping("notificationPersonList")
    @ResponseBody
    public List<Map<String,Object>> getNotificationPersonList(HttpServletRequest request, HttpServletResponse response) {
        String userId = request.getParameter("userId");
        String reportYear = request.getParameter("reportYear");
        JSONObject paramJson = new JSONObject();
        paramJson.put("userId",userId);
        paramJson.put("reportYear",reportYear);
        return portraitNoticeManager.getPersonNotificationList(paramJson);
    }

    /**
     * 模板下载
     * */
    @RequestMapping("/importTemplateDownload")
    public ResponseEntity<byte[]> importTemplateDownload(HttpServletRequest request, HttpServletResponse response) {
        return portraitNoticeManager.importTemplateDownload();
    }
    /**
     * 批量导入
     * */
    @RequestMapping("importExcel")
    @ResponseBody
    public JSONObject importExcel(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        portraitNoticeManager.importNotice(result, request);
        return result;
    }
}
