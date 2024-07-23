package com.redxun.digitization.core.controller;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.digitization.core.service.DatamigrationTotalprogressService;
import com.redxun.saweb.util.RequestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/digitization/core/datamigration/totalprogress")
public class DatamigrationTotalprogressController {
    private static final Logger logger = LoggerFactory.getLogger(DatamigrationTotalprogressController.class);
    @Autowired
    DatamigrationTotalprogressService datamigrationTotalprogressService;

    @RequestMapping("dataListPage")
    public ModelAndView dataListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "digitization/core/datamigrationTotalprogressList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        return mv;
    }

    @RequestMapping("dataListQuery")
    @ResponseBody
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) {
        return datamigrationTotalprogressService.dataListQuery(request, response);
    }

    @RequestMapping("deleteData")
    @ResponseBody
    public JsonResult deleteData(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return datamigrationTotalprogressService.deleteData(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteData", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    @RequestMapping(value = "saveBusiness", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult saveBusiness(HttpServletRequest request, @RequestBody String businessDataStr,
                                   HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "保存成功");
        datamigrationTotalprogressService.saveBusiness(result, businessDataStr);
        return result;
    }

    @RequestMapping("kanbanPage")
    public ModelAndView kanbanPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "digitization/core/datamigrationTotalprogressKanban.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        return mv;
    }

    @RequestMapping("getTotalprogressKanbanData")
    @ResponseBody
    public List<JSONObject> getTotalprogressKanbanData(HttpServletRequest request, HttpServletResponse response,
                                                       @RequestBody String postDataStr) {
        return datamigrationTotalprogressService.getTotalprogressKanbanData(request, response, postDataStr);
    }
}
