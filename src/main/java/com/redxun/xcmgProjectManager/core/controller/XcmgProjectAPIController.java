
package com.redxun.xcmgProjectManager.core.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectAPIManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.nio.charset.Charset;

/**
 * 项目管理API接口
 */
@Controller
@RequestMapping("/xcmgProjectManager/core/api/")
public class XcmgProjectAPIController {
    private Logger logger = LogManager.getLogger(XcmgProjectAPIController.class);

    @Resource
    private XcmgProjectAPIManager xcmgProjectAPIManager;

    /**
     * 员工职业发展系统获取项目经验
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "xcmghr/getProjects", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject xcmghrGetProjects(HttpServletRequest request, @RequestBody String postData,
        HttpServletResponse response) {
        postData = new String(postData.getBytes(), Charset.forName("utf-8"));
        JSONObject result = new JSONObject();
        if (StringUtils.isBlank(postData)) {
            logger.error("requestBody is blank");
            result.put("result", false);
            result.put("message","请求信息为空！");
            return result;
        }
        xcmgProjectAPIManager.xcmghrGetProjects(result, postData);
        return result;
    }
}
