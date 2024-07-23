package com.redxun.rdmCommon.core.controller;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author liwenguang
 * @since 2022/7/16
 */
@Controller
@RequestMapping("/rdmCommon/api/outSystem")
public class RdmApiOutSystemMockController {
    private static final Logger logger = LoggerFactory.getLogger(RdmApiOutSystemMockController.class);

    //质量改进问题CRM系统回调模拟
    @RequestMapping("crm/callBackCrmMock")
    @ResponseBody
    public JSONObject callBackCrmMock(HttpServletRequest request, HttpServletResponse response,
                                      @RequestBody String callData) {
        JSONObject callDataJson = JSONObject.parseObject(callData);
        String qualityId = callDataJson.getString("qualityId");
        String timeStamp = callDataJson.getString("timeStamp");
        String techResult = callDataJson.getString("techResult");
        String qualityOptions = callDataJson.getString("qualityOptions");
        String level = callDataJson.getString("level");
        String approvalStatus = callDataJson.getString("approvalStatus");
        JSONObject result = new JSONObject();
        try {
            JSONObject data = new JSONObject();
            data.put("isok",1);
            data.put("errormsg",null);
            result.put("Data", data);
            result.put("qualityOptions", qualityOptions);
            result.put("ErrorCode", 0);
            result.put("Message", null);
            //System.out.println(1 / 0);//todo:异常锁callBackCrmMock
            return result;
        } catch (Exception e) {
            JSONObject data = new JSONObject();
            data.put("isok",0);
            data.put("errormsg","CRM错误原因");
            result.put("Data", data);
            result.put("ErrorCode", 0);
            result.put("Message", null);
            return result;
        }
    }
}
