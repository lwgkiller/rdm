package com.redxun.portrait.core.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.portrait.core.manager.PortraitTeamWorkManager;
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
@RequestMapping("/portrait/teamwork/")
public class PortraitTeamWorkController {
    @Resource
    PortraitTeamWorkManager portraitTeamWorkManager;
    @Resource
    CommonInfoManager commonInfoManager;
    /**
     * 获取编辑页面
     * */
    @RequestMapping("teamWorkEditPage")
    public ModelAndView getTeamworkEditPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "portrait/teamWork/portraitTeamWorkEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String action = request.getParameter("action");
        String id = request.getParameter("id");
        JSONObject applyObj = new JSONObject();
        //1.查看、或者修改 根据id查询
        if(ConstantUtil.FORM_EDIT.equals(action)||ConstantUtil.FORM_VIEW.equals(action)){
            applyObj = portraitTeamWorkManager.getObjectById(id);
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
    @RequestMapping("teamWorkListPage")
    public ModelAndView getTeamWorkListPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "portrait/teamWork/portraitTeamWorkList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        JSONObject resultJson = commonInfoManager.hasPermission("YGHXZGLY");
        mv.addObject("permission",resultJson.getBoolean("HX-GLY")||resultJson.getBoolean("YGHXZGLY")||"admin".equals(ContextUtil.getCurrentUser().getUserNo()));
        return mv;
    }
    /**
     * 获取列表数据
     * */
    @RequestMapping("teamWorkList")
    @ResponseBody
    public JsonPageResult<?> teamWorkList(HttpServletRequest request, HttpServletResponse response) {
        return portraitTeamWorkManager.query(request);
    }
    /**
     * 获取列表数据
     * */
    @RequestMapping("teamPersonList")
    @ResponseBody
    public List<Map<String,Object>> getTeamPersonList(HttpServletRequest request, HttpServletResponse response) {
        String userId = request.getParameter("userId");
        String reportYear = request.getParameter("reportYear");
        JSONObject paramJson = new JSONObject();
        paramJson.put("userId",userId);
        paramJson.put("reportYear",reportYear);
        return portraitTeamWorkManager.getPersonTeamList(paramJson);
    }
    @RequestMapping("save")
    @ResponseBody
    public JSONObject save(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");
        JSONObject resultJSON = null;
        if(StringUtil.isEmpty(id)){
            resultJSON = portraitTeamWorkManager.add(request);
        }else{
            resultJSON = portraitTeamWorkManager.update(request);
        }
        return resultJSON;
    }
    @RequestMapping("remove")
    @ResponseBody
    public JSONObject remove(HttpServletRequest request, HttpServletResponse response) {
        JSONObject resultJSON = null;
        resultJSON = portraitTeamWorkManager.remove(request);
        return resultJSON;
    }

    /**
     * 模板下载
     * */
    @RequestMapping("/importTemplateDownload")
    public ResponseEntity<byte[]> importTemplateDownload(HttpServletRequest request, HttpServletResponse response) {
        return portraitTeamWorkManager.importTemplateDownload();
    }
    /**
     * 批量导入
     * */
    @RequestMapping("importExcel")
    @ResponseBody
    public JSONObject importExcel(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        portraitTeamWorkManager.importTeamWork(result, request);
        return result;
    }
}
