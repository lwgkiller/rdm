package com.redxun.serviceEngineering.core.controller;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonResult;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.serviceEngineering.core.service.SeGeneralKanBan2023Service;
import com.redxun.sys.core.manager.SysDicManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/serviceEngineering/core/seGeneralKanban2023/")
public class SeGeneralKanBan2023Controller {
    private static final Logger logger = LoggerFactory.getLogger(SeGeneralKanBan2023Controller.class);
    @Autowired
    private SysDicManager sysDicManager;
    @Autowired
    private SeGeneralKanBan2023Service seGeneralKanBan2023Service;

    //..
    @RequestMapping("seGeneralKanban2023Page")
    public ModelAndView seGeneralKanban2023Page(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "serviceEngineering/core/seGeneralKanban2023.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        String seKanbanAdmin = sysDicManager.getBySysTreeKeyAndDicKey("serviceEngineeringGroups", "seKanbanAdmin").getValue();
        mv.addObject("seKanbanAdmin", seKanbanAdmin);
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        return mv;
    }

    //..装修手册个人指标
    @RequestMapping("zhuangXiuShouCeGeRenZhiBiaoIni")
    @ResponseBody
    public JsonResult zhuangXiuShouCeGeRenZhiBiaoIni(HttpServletRequest request, HttpServletResponse response) {
        try {
            return seGeneralKanBan2023Service.zhuangXiuShouCeGeRenZhiBiaoIni();
        } catch (Exception e) {
            logger.error("Exception in zhuangXiuShouCeGeRenZhiBiaoIni", e);
            return new JsonResult(false, e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
        }
    }

    //..资料收集超期
    @RequestMapping("ziLiaoShouJiChaoQiIni")
    @ResponseBody
    public JsonResult ziLiaoShouJiChaoQiIni(HttpServletRequest request, HttpServletResponse response) {
        try {
            return seGeneralKanBan2023Service.ziLiaoShouJiChaoQiIni();
        } catch (Exception e) {
            logger.error("Exception in ziLiaoShouJiChaoQiIni", e);
            return new JsonResult(false, e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
        }
    }

}


