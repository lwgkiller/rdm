package com.redxun.portrait.core.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import com.redxun.portrait.core.manager.PortraitIndexManager;
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

/**
 * @author zhangzhen
 */
@RestController
@RequestMapping("/portrait/index/")
public class PortraitIndexController {
    @Resource
    PortraitIndexManager portraitIndexManager;
    @Resource
    CommonInfoManager commonInfoManager;
    /**
     * 获取编辑页面
     * */
    @RequestMapping("indexEditPage")
    public ModelAndView getIndexEditPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "portrait/index/portraitIndexEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String action = request.getParameter("action");
        String id = request.getParameter("id");
        JSONObject applyObj = new JSONObject();
        //1.查看、或者修改 根据id查询
        if(ConstantUtil.FORM_EDIT.equals(action)||ConstantUtil.FORM_VIEW.equals(action)){
            applyObj = portraitIndexManager.getObjectById(id);
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
    @RequestMapping("indexListPage")
    public ModelAndView getIndexListPage(HttpServletRequest request, HttpServletResponse response) {
        String jspPath = "portrait/index/portraitIndexList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        JSONObject resultJson = commonInfoManager.hasPermission("YGHXZGLY");
        mv.addObject("permission",resultJson.getBoolean("HX-GLY")||resultJson.getBoolean("YGHXZGLY")||"admin".equals(ContextUtil.getCurrentUser().getUserNo()));
        return mv;
    }
    /**
     * 获取列表数据
     * */
    @RequestMapping("indexList")
    @ResponseBody
    public JsonPageResult<?> indexList(HttpServletRequest request, HttpServletResponse response) {
        return portraitIndexManager.query(request);
    }
    @RequestMapping("save")
    @ResponseBody
    public JSONObject save(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");
        JSONObject resultJSON = null;
        if(StringUtil.isEmpty(id)){
            resultJSON = portraitIndexManager.add(request);
        }else{
            resultJSON = portraitIndexManager.update(request);
        }
        return resultJSON;
    }
    @RequestMapping("remove")
    @ResponseBody
    public JSONObject remove(HttpServletRequest request, HttpServletResponse response) {
        JSONObject resultJSON = null;
        resultJSON = portraitIndexManager.remove(request);
        return resultJSON;
    }
    /**
     * 获取指标字典
     **/
    @RequestMapping("indexDic")
    @ResponseBody
    public JSONArray getIndexDic(HttpServletRequest request, HttpServletResponse response){
        return portraitIndexManager.getIndexDic(request, response);
    }


    /**
     * 模板下载
     * */
    @RequestMapping("/importTemplateDownload")
    public ResponseEntity<byte[]> importTemplateDownload(HttpServletRequest request, HttpServletResponse response) {
        return portraitIndexManager.importTemplateDownload();
    }
    /**
     * 批量导入
     * */
    @RequestMapping("importExcel")
    @ResponseBody
    public JSONObject importExcel(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        portraitIndexManager.importIndex(result, request);
        return result;
    }
}
