package com.redxun.xcmgTdm.core.controller;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.service.ActInstService;
import com.redxun.bpm.core.entity.BpmInst;
import com.redxun.bpm.core.entity.BpmTask;
import com.redxun.bpm.core.entity.TaskExecutor;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.bpm.core.manager.BpmTaskManager;
import com.redxun.core.json.JsonResult;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.standardManager.core.manager.StandardMessageManager;
import com.redxun.sys.core.entity.SysDic;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.sys.core.util.SysPropertiesUtil;
import com.redxun.xcmgTdm.core.service.TdmRequestService;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/xcmgTdmii/core/requestapi/")
public class TdmIIRequestController {
    private static final Logger logger = LoggerFactory.getLogger(TdmIIRequestController.class);
    @Autowired
    private SysDicManager sysDicManager;

    @RequestMapping("loginToTdmii")
    @ResponseBody
    public void loginToTdmii(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String ip = request.getRemoteAddr();
        String[] ips = ip.split("\\.");
        String url = "";
        List<SysDic> sysDicList = sysDicManager.getByTreeKey("TDMiiIpClass");
        if (ips.length > 3) {// ipv4
            if (Integer.parseInt(ips[2]) > 3 && Integer.parseInt(ips[2]) < 8) {
                url = String.format("%s/tdmII/rdmLoginSSO.do?acc=" + ContextUtil.getCurrentUser().getUserNo()
                        + "&url=%s/tdmII/index.do", sysDicList.get(0).getValue(), sysDicList.get(0).getValue());
            } else if (ip.equalsIgnoreCase("127.0.0.1")) {
                url = String.format("%s/tdmII/rdmLoginSSO.do?acc=" + ContextUtil.getCurrentUser().getUserNo()
                        + "&url=%s/tdmII/index.do", sysDicList.get(1).getValue(), sysDicList.get(1).getValue());
            } else {
                url = String.format("%s/tdmII/rdmLoginSSO.do?acc=" + ContextUtil.getCurrentUser().getUserNo()
                        + "&url=%s/tdmII/index.do", sysDicList.get(1).getValue(), sysDicList.get(1).getValue());
            }
        } else {//ipv6
            url = String.format("%s/tdmII/rdmLoginSSO.do?acc=" + ContextUtil.getCurrentUser().getUserNo()
                    + "&url=%s/tdmII/index.do", sysDicList.get(1).getValue(), sysDicList.get(1).getValue());
        }

        String url2 = "http://10.15.10.151:8080/tdmII/rdmLoginSSO.do?acc=" + ContextUtil.getCurrentUser().getUserNo()
                + "&url=http://10.15.10.151:8080/tdmII/index.do";
        response.sendRedirect(url);
    }
}
