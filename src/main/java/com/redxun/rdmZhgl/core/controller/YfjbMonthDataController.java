
package com.redxun.rdmZhgl.core.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.rdmZhgl.core.service.YfjbMonthDataService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.org.api.model.IUser;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.saweb.context.ContextUtil;

/**
 * 整机成本
 * @author zz
 */
@Controller
@RequestMapping("/rdmZhgl/core/yfjb/monthData/")
public class YfjbMonthDataController {
    @Resource
    YfjbMonthDataService yfjbMonthDataService;
    @Resource
    CommonInfoManager commonInfoManager;
    /**
     * 整机成本
     * */
    @RequestMapping("listPage")
    public ModelAndView getProcessPage(HttpServletRequest request,HttpServletResponse response) throws Exception{
        String jspPath = "rdmZhgl/core/yfjbMonthDataList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        IUser currentUser = ContextUtil.getCurrentUser();
        mv.addObject("currentUser", currentUser);
        JSONObject resultJson = commonInfoManager.hasPermission("YFJB-GLY");
        JSONObject jbzyJson = commonInfoManager.hasPermission("JBZY");
        mv.addObject("permission",resultJson.getBoolean("YFJB-GLY")||jbzyJson.getBoolean("JBZY")||"admin".equals(ContextUtil.getCurrentUser().getUserNo()));
        return mv;
    }
    @RequestMapping(value = "dealData", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject dealData(HttpServletRequest request, @RequestBody String changeGridDataStr,
                               HttpServletResponse response) {
        return yfjbMonthDataService.saveOrUpdateItem(changeGridDataStr);
    }
    @RequestMapping("listData")
    @ResponseBody
    public JsonPageResult<?> listData(HttpServletRequest request, HttpServletResponse response) {
        return yfjbMonthDataService.query(request);
    }

    //降本率数据维护

    @RequestMapping("quarterListPage")
    public ModelAndView quarterListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdmZhgl/core/yfjbQuarterDataList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        IUser currentUser = ContextUtil.getCurrentUser();
        mv.addObject("currentUser", currentUser);
        return mv;
    }

    @RequestMapping("quarterListData")
    @ResponseBody
    public JsonPageResult<?> quarterListData(HttpServletRequest request, HttpServletResponse response) {
        return yfjbMonthDataService.queryQuarter(request);
    }

    @RequestMapping(value = "dealQuarterData", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject dealQuarterData(HttpServletRequest request, @RequestBody String changeGridDataStr,
                               HttpServletResponse response) {
        return yfjbMonthDataService.saveOrUpdateQuarter(changeGridDataStr);
    }
}
