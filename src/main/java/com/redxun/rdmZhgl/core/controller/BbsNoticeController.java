
package com.redxun.rdmZhgl.core.controller;



import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.rdmZhgl.core.dao.BbsNoticeDao;
import com.redxun.rdmZhgl.core.service.BbsNoticeService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.redxun.core.json.JsonPageResult;

/**
 * 论坛
 * @author zz
 */
@Controller
@RequestMapping("/rdmZhgl/core/bbs/notice/")
public class BbsNoticeController {
    @Resource
    BbsNoticeService bbsNoticeService;

    @RequestMapping(value = "list")
    @ResponseBody
    public JsonPageResult<?> getPlanList(HttpServletRequest request, HttpServletResponse response)  {
        return bbsNoticeService.query(request);
    }
}
