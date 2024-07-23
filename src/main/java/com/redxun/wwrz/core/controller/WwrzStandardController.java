
package com.redxun.wwrz.core.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.standardManager.core.util.ResultUtil;
import com.redxun.wwrz.core.dao.WwrzFilesDao;
import com.redxun.wwrz.core.dao.WwrzStandardDao;
import com.redxun.wwrz.core.service.WwrzStandardService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.util.StringUtil;
import com.redxun.org.api.model.IUser;
import com.redxun.saweb.context.ContextUtil;

/**
 * 标准结构化
 * @author zz
 */
@Controller
@RequestMapping("/wwrz/core/standard/")
public class WwrzStandardController {
    @Resource
    WwrzStandardService wwrzStandardService;
    @Resource
    WwrzStandardDao wwrzStandardDao;
    @Resource
    WwrzFilesDao wwrzFilesDao;
    /**
     * 列表
     * */
    @RequestMapping("listPage")
    public ModelAndView getListPage(HttpServletRequest request,HttpServletResponse response) throws Exception{
        String jspPath = "wwrz/core/wwrzStandardList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        IUser currentUser = ContextUtil.getCurrentUser();
        mv.addObject("currentUser", currentUser);
        return mv;
    }

   /**
    * 获取编辑页面
    * */
    @RequestMapping("getEditPage")
    public ModelAndView getEditPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "wwrz/core/wwrzStandardEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String id = request.getParameter("id");
        String action = request.getParameter("action");
        JSONObject applyObj = new JSONObject();
        List<JSONObject> detailList = new ArrayList<>();
        if(!StringUtil.isEmpty(id)){
            applyObj = wwrzStandardService.getObjectById(id);
            detailList = wwrzStandardDao.getStandardDetailList(id);
        }
        applyObj.put("detailList",detailList);
        mv.addObject("action",action);
        mv.addObject("applyObj", applyObj);
        return mv;
    }
    @RequestMapping("remove")
    @ResponseBody
    public JSONObject remove(HttpServletRequest request, HttpServletResponse response) {
        JSONObject resultJSON = null;
        resultJSON = wwrzStandardService.remove(request);
        return resultJSON;
    }
    @RequestMapping("save")
    @ResponseBody
    public JSONObject save(HttpServletRequest request, HttpServletResponse response,@RequestBody String budgetStr) {
        JSONObject formDataJson = JSONObject.parseObject(budgetStr);
        String id = formDataJson.getString("id");
        JSONObject resultJSON = null;
        if(StringUtil.isEmpty(id)){
            resultJSON = wwrzStandardService.add(formDataJson);
        }else{
            resultJSON = wwrzStandardService.update(formDataJson);
        }
        return resultJSON;
    }
    @RequestMapping(value = "list")
    @ResponseBody
    public JsonPageResult<?> getDataList(HttpServletRequest request, HttpServletResponse response)  {
        return wwrzStandardService.query(request);
    }

    @RequestMapping(value = "listStandard")
    @ResponseBody
    public List<JSONObject> getListStandard(HttpServletRequest request, HttpServletResponse response)  {
        return wwrzStandardService.getStandardList();
    }
    @RequestMapping(value = "listSector")
    @ResponseBody
    public List<JSONObject> getListSector(HttpServletRequest request, HttpServletResponse response)  {
        String standardId = request.getParameter("standardId");
        return wwrzStandardDao.getStandardDetailList(standardId);
    }
    /**
     * 获取标准查看页面
     * */
    @RequestMapping("viewStandardPage")
    public ModelAndView getViewStandardPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "wwrz/core/wwrzStandardEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String sectorId = request.getParameter("sectorId");
        JSONObject applyObj = new JSONObject();
        List<JSONObject> detailList = new ArrayList<>();
        mv.addObject("applyObj", applyObj);
        return mv;
    }
    @RequestMapping(value = "standardFile", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject getStandardFile(HttpServletRequest request, @RequestBody String changeGridDataStr,
                                      HttpServletResponse response) {
        JSONObject postDataJson = new JSONObject();
        if (StringUtils.isNotBlank(changeGridDataStr)) {
            postDataJson = JSONObject.parseObject(changeGridDataStr);
        }else{
            return ResultUtil.result(false,"请求参数不能为空",null);

        }
        String sectorId = postDataJson.getString("sectorId");
        List<JSONObject> list = wwrzFilesDao.getFileListByMainId(sectorId);
        return ResultUtil.result(true,"成功",list);
    }
}
