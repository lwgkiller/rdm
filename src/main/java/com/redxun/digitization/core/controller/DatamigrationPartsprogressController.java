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
import com.redxun.digitization.core.service.DatamigrationPartsprogressService;
import com.redxun.saweb.util.RequestUtil;

@Controller
@RequestMapping("/digitization/core/datamigration/partsprogress")
public class DatamigrationPartsprogressController {
    private static final Logger logger = LoggerFactory.getLogger(DatamigrationPartsprogressController.class);
    @Autowired
    DatamigrationPartsprogressService datamigrationPartsprogressService;

    @RequestMapping("dataListPage")
    public ModelAndView dataListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath = "digitization/core/datamigrationPartsprogressList.jsp";
        ModelAndView mv = new ModelAndView(jspPath);
        return mv;
    }

    @RequestMapping("dataListQuery")
    @ResponseBody
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) {
        return datamigrationPartsprogressService.dataListQuery(request, response);
    }

    @RequestMapping("deleteData")
    @ResponseBody
    public JsonResult deleteData(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String uIdStr = RequestUtil.getString(request, "ids");
            String[] ids = uIdStr.split(",");
            return datamigrationPartsprogressService.deleteData(ids);
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
        datamigrationPartsprogressService.saveBusiness(result, businessDataStr);
        return result;
    }

    @RequestMapping(value = "calculateBusiness", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult calculateBusiness(HttpServletRequest request, @RequestBody String businessDataStr,
        HttpServletResponse response) {
        JsonResult result = new JsonResult(true, "计算成功");
        datamigrationPartsprogressService.calculateBusiness(result, businessDataStr);
        return result;
    }
}
