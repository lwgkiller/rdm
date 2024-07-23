package com.redxun.rdMaterial.core.controller;

import com.redxun.core.json.JsonResult;
import com.redxun.rdMaterial.core.service.RdMaterialGeneralKanBanService;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
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
@RequestMapping("/rdMaterial/core/generalKanban/")
public class RdMaterialGeneralKanBanController {
    private static final Logger logger = LoggerFactory.getLogger(RdMaterialGeneralKanBanController.class);
    @Autowired
    private SysDicManager sysDicManager;
    @Autowired
    private RdMaterialGeneralKanBanService rdMaterialGeneralKanBanService;

    //..
    @RequestMapping("kanbanPage")
    public ModelAndView kanbanPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "rdMaterial/core/rdMaterialGeneralKanban.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        mv.addObject("currentUserNo", ContextUtil.getCurrentUser().getUserNo());
        return mv;
    }

    //..滞留物料统计1
    @RequestMapping("zhiLiuWuLiaoTongJi")
    @ResponseBody
    public JsonResult zhiLiuWuLiaoTongJi(HttpServletRequest request, HttpServletResponse response) {
        try {
            String theYear = RequestUtil.getString(request, "theYear", "");
            return rdMaterialGeneralKanBanService.zhiLiuWuLiaoTongJi(theYear);
        } catch (Exception e) {
            logger.error("Exception in zhiLiuWuLiaoTongJi", e);
            return new JsonResult(false, e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
        }
    }

    //..滞留物料统计2
    @RequestMapping("zhiLiuWuLiaoTongJi2")
    @ResponseBody
    public JsonResult zhiLiuWuLiaoTongJi2(HttpServletRequest request, HttpServletResponse response) {
        try {
            String theYear = RequestUtil.getString(request, "theYear", "");
            return rdMaterialGeneralKanBanService.zhiLiuWuLiaoTongJi2(theYear);
        } catch (Exception e) {
            logger.error("Exception in zhiLiuWuLiaoTongJi2", e);
            return new JsonResult(false, e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
        }
    }

    //..出入库物料处理情况1
    @RequestMapping("chuRuKuWuLiaoChuLi")
    @ResponseBody
    public JsonResult chuRuKuWuLiaoChuLi(HttpServletRequest request, HttpServletResponse response) {
        try {
            String theYear = RequestUtil.getString(request, "theYear", "");
            String type = RequestUtil.getString(request, "type", "");
            return rdMaterialGeneralKanBanService.chuRuKuWuLiaoChuLi(theYear, type);
        } catch (Exception e) {
            logger.error("Exception in chuRuKuWuLiaoChuLi", e);
            return new JsonResult(false, e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
        }
    }

    //..处理物料统计
    @RequestMapping("chuLiWuLiaoTongJi")
    @ResponseBody
    public JsonResult chuLiWuLiaoTongJi(HttpServletRequest request, HttpServletResponse response) {
        try {
            String theYear = RequestUtil.getString(request, "theYear", "");
            return rdMaterialGeneralKanBanService.chuLiWuLiaoTongJi(theYear);
        } catch (Exception e) {
            logger.error("Exception in chuLiWuLiaoTongJi", e);
            return new JsonResult(false, e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
        }
    }
}


