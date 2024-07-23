
package com.redxun.rdmZhgl.core.controller;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.rdmZhgl.core.service.BbsDiscussService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;

/**
 * 论坛评论
 * @author zz
 */
@Controller
@RequestMapping("/rdmZhgl/core/bbs/comment/")
public class BbsDiscussController {
    @Resource
    BbsDiscussService bbsDiscussService;
    @RequestMapping("remove")
    @ResponseBody
    public JSONObject remove(HttpServletRequest request, HttpServletResponse response) {
        JSONObject resultJSON = null;
        resultJSON = bbsDiscussService.remove(request);
        return resultJSON;
    }
    @RequestMapping(value = "list",method = RequestMethod.POST)
    @ResponseBody
    public JSONObject getPlanList(HttpServletRequest request, @RequestBody String postData,
                                  HttpServletResponse response)  {
        JSONObject postDataJson = new JSONObject();
        if (StringUtils.isNotBlank(postData)) {
            postDataJson = JSONObject.parseObject(postData);
        }
        return bbsDiscussService.query(postDataJson);
    }
    @RequestMapping(value = "save", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject saveComment(HttpServletRequest request, @RequestBody String postData,
                                       HttpServletResponse response) throws Exception {
        JSONObject postDataJson = new JSONObject();
        if (StringUtils.isNotBlank(postData)) {
            postDataJson = JSONObject.parseObject(postData);
        }
        return bbsDiscussService.add(postDataJson);
    }
    @RequestMapping(value = "childComments",method = RequestMethod.POST)
    @ResponseBody
    public JSONObject getChildComments(HttpServletRequest request, @RequestBody String postData,
                                  HttpServletResponse response)  {
        JSONObject postDataJson = new JSONObject();
        if (StringUtils.isNotBlank(postData)) {
            postDataJson = JSONObject.parseObject(postData);
        }
        return bbsDiscussService.getChildDiscussList(postDataJson);
    }
}
