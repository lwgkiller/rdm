package com.redxun.digitization.core.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.digitization.core.service.DatamigrationModelprogressService;
import com.redxun.saweb.util.RequestUtil;

@Controller
@RequestMapping("/digitization/core/datamigration/modelprogress")
public class DatamigrationModelprogressController {
    private static final Logger logger = LoggerFactory.getLogger(DatamigrationModelprogressController.class);
    @Autowired
    DatamigrationModelprogressService datamigrationModelprogressService;

    @RequestMapping("dataListPage")
    public ModelAndView dataListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "digitization/core/datamigrationModelprogressList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        return mv;
    }

    @RequestMapping("dataListQuery")
    @ResponseBody
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) {
        return datamigrationModelprogressService.dataListQuery(request, response);
    }

    @RequestMapping("deleteData")
    @ResponseBody
    public JsonResult deleteData(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return datamigrationModelprogressService.deleteData(ids);
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
        datamigrationModelprogressService.saveBusiness(result, businessDataStr);
        return result;
    }

}
