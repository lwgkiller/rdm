
package com.redxun.rdmZhgl.core.controller;

import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmZhgl.core.dao.YfjbBaseInfoDao;
import com.redxun.rdmZhgl.core.dao.YfjbProductionNoticeDao;
import com.redxun.rdmZhgl.core.service.YfjbProductionNoticeService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.org.api.model.IUser;
import com.redxun.saweb.context.ContextUtil;

/**
 * 新品试制配置
 * @author zz
 */
@Controller
@RequestMapping("/rdmZhgl/core/yfjb/productionNotice/")
public class YfjbProductionNoticeController {
    @Resource
    YfjbProductionNoticeService yfjbProductionNoticeService;
    @Resource
    YfjbProductionNoticeDao yfjbProductionNoticeDao;
    @Resource
    YfjbBaseInfoDao yfjbBaseInfoDao;
    @Resource
    CommonInfoManager commonInfoManager;
    /**
     * 列表
     * */
    @RequestMapping("getListPage")
    public ModelAndView getListPage(HttpServletRequest request,HttpServletResponse response) throws Exception{
        String jspPath = "rdmZhgl/core/yfjbProductionNoticeList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        IUser currentUser = ContextUtil.getCurrentUser();
        mv.addObject("currentUser", currentUser);
        JSONObject resultJson = commonInfoManager.hasPermission("YFJB-GLY");
        mv.addObject("permission",resultJson.getBoolean("YFJB-GLY") ||"admin".equals(ContextUtil.getCurrentUser().getUserNo()));
        return mv;
    }

   /**
    * 获取编辑页面
    * */
    @RequestMapping("getEditPage")
    public ModelAndView getEditPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/yfjbProductionNoticeEdit.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String mainId = request.getParameter("mainId");
        String id = request.getParameter("id");
        String action = request.getParameter("action");
        JSONObject applyObj = new JSONObject();
        if(!StringUtil.isEmpty(id)){
            applyObj = yfjbProductionNoticeService.getObjectById(id);
        }
        if(!StringUtil.isEmpty(mainId)){
            applyObj = yfjbProductionNoticeDao.getObjectByMainId(mainId);
            if(applyObj==null){
                JSONObject baseInfo = yfjbBaseInfoDao.getObjectById(mainId);
                applyObj = new JSONObject();
                applyObj.put("mainId",mainId);
                applyObj.put("orgSupplier",baseInfo.getString("orgSupplier"));
                applyObj.put("newSupplier",baseInfo.getString("newSupplier"));
            }
        }
        if (applyObj.get("noticeDate") != null) {
            applyObj.put("noticeDate",
                    DateUtil.formatDate((Date)applyObj.get("noticeDate"), "yyyy-MM-dd"));
        }
        mv.addObject("action",action);
        mv.addObject("applyObj", applyObj);
        return mv;
    }
    @RequestMapping(value = "list")
    @ResponseBody
    public JsonPageResult<?> getPlanList(HttpServletRequest request, HttpServletResponse response)  {
        return yfjbProductionNoticeService.query(request);
    }
    @RequestMapping("remove")
    @ResponseBody
    public JSONObject remove(HttpServletRequest request, HttpServletResponse response) {
        JSONObject resultJSON = null;
        resultJSON = yfjbProductionNoticeService.remove(request);
        return resultJSON;
    }
    @RequestMapping("save")
    @ResponseBody
    public JSONObject save(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");
        JSONObject resultJSON = null;
        if(StringUtil.isEmpty(id)){
            resultJSON = yfjbProductionNoticeService.add(request);
        }else{
            resultJSON = yfjbProductionNoticeService.update(request);
        }
        return resultJSON;
    }

    @RequestMapping("exportExcel")
    public void exportExcel(HttpServletRequest request, HttpServletResponse response) {
        yfjbProductionNoticeService.exportExcel(request, response);
    }
}
