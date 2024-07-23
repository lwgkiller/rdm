package com.redxun.portrait.core.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import com.redxun.portrait.core.manager.PortraitKnowledgeManager;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.saweb.context.ContextUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.util.ConstantUtil;

/**
 * @author zhangzhen
 */
@RestController
@RequestMapping("/portrait/knowledge/")
public class PortraitKnowledgeController {
    @Resource
    PortraitKnowledgeManager portraitKnowledgeManager;
    @Resource
    CommonInfoManager commonInfoManager;
    /**
     * 获取编辑页面
     * */
    @RequestMapping("knowledgeEditPage")
    public ModelAndView getKnowledgeEditPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "portrait/knowledge/portraitKnowledgeEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String action = request.getParameter("action");
        String id = request.getParameter("id");
        JSONObject applyObj = new JSONObject();
        //1.查看、或者修改 根据id查询
        if(ConstantUtil.FORM_EDIT.equals(action)||ConstantUtil.FORM_VIEW.equals(action)){
            applyObj = portraitKnowledgeManager.getObjectById(id);
            if (applyObj.get("applyDate") != null) {
                applyObj.put("applyDate",
                        DateUtil.formatDate((Date)applyObj.get("applyDate"), "yyyy-MM-dd"));
            }
            if (applyObj.get("authorizeDate") != null) {
                applyObj.put("authorizeDate",
                        DateUtil.formatDate((Date)applyObj.get("authorizeDate"), "yyyy-MM-dd"));
            }
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
    @RequestMapping("knowledgeListPage")
    public ModelAndView getKnowledgeListPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "portrait/knowledge/portraitKnowledgeList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        JSONObject resultJson = commonInfoManager.hasPermission("knowledgeAdmin");
        mv.addObject("permission",resultJson.getBoolean("HX-GLY")||resultJson.getBoolean("knowledgeAdmin")||"admin".equals(ContextUtil.getCurrentUser().getUserNo()));
        return mv;
    }
    /**
     * 获取列表数据
     * */
    @RequestMapping("knowledgeList")
    @ResponseBody
    public JsonPageResult<?> knowledgeList(HttpServletRequest request, HttpServletResponse response) {
        return portraitKnowledgeManager.query(request);
    }
    @RequestMapping("save")
    @ResponseBody
    public JSONObject save(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");
        JSONObject resultJSON = null;
        if(StringUtil.isEmpty(id)){
            resultJSON = portraitKnowledgeManager.add(request);
        }else{
            resultJSON = portraitKnowledgeManager.update(request);
        }
        return resultJSON;
    }
    @RequestMapping("remove")
    @ResponseBody
    public JSONObject remove(HttpServletRequest request, HttpServletResponse response) {
        JSONObject resultJSON = null;
        resultJSON = portraitKnowledgeManager.remove(request);
        return resultJSON;
    }
    /**
     * 获取列表数据
     * */
    @RequestMapping("knowledgePersonList")
    @ResponseBody
    public List<Map<String,Object>> getKnowledgePersonList(HttpServletRequest request, HttpServletResponse response) {
        String userId = request.getParameter("userId");
        String reportYear = request.getParameter("reportYear");
        JSONObject paramJson = new JSONObject();
        paramJson.put("userId",userId);
        paramJson.put("reportYear",reportYear);
        return portraitKnowledgeManager.getPersonKnowledgeList(paramJson);
    }
   /**
    * 知识产权模板下载
    * */
    @RequestMapping("/importTemplateDownload")
    public ResponseEntity<byte[]> importTemplateDownload(HttpServletRequest request, HttpServletResponse response) {
        return portraitKnowledgeManager.importTemplateDownload();
    }
    /**
     * 知识产权批量导入
     * */
    @RequestMapping("importExcel")
    @ResponseBody
    public JSONObject importExcel(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        portraitKnowledgeManager.importKnowledge(result, request);
        return result;
    }
    @RequestMapping("asyncKnowledge")
    @ResponseBody
    public JSONObject asyncKnowledge(HttpServletRequest request, HttpServletResponse response) {
        JSONObject resultJSON = null;
        resultJSON = portraitKnowledgeManager.asyncKnowledge();
        return resultJSON;
    }
}
