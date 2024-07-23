package com.redxun.xcmgbudget.core.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonResult;
import com.redxun.xcmgbudget.core.dao.BudgetMonthDao;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/xcmgBudget/core/budgetMonthEPM/")
public class BudgetMonthEPMController {
    private static final Logger logger = LoggerFactory.getLogger(BudgetMonthEPMController.class);

    @Autowired
    private BudgetMonthDao budgetMonthDao;

    // 过程费用保存
    @RequestMapping("syncMonthProcess")
    @ResponseBody
    public JsonResult syncMonthProcess(HttpServletRequest request, HttpServletResponse response,
                                       @RequestBody String requestBody) {
        JsonResult result = new JsonResult(true);
        try {
            if (StringUtils.isBlank(requestBody)) {
                result.setSuccess(false);
                result.setMessage("同步失败，数据为空！");
                return result;
            }
            JSONObject requestObj = JSONObject.parseObject(requestBody);
            JSONArray dataArr = requestObj.getJSONArray("data");
            if (dataArr == null || dataArr.isEmpty()) {
                result.setSuccess(false);
                result.setMessage("同步失败，数据为空！");
                return result;
            }
            for (int index = 0; index < dataArr.size(); index++) {
                JSONObject oneData = dataArr.getJSONObject(index);
                if (oneData.getString("bxId").isEmpty()||oneData.getString("bxId")==null){
                    result.setSuccess(false);
                    result.setMessage("同步失败，勾选数据含非RDM系统导入数据，请重新选择！");
                    return result;
                }
            }
            for (int index = 0; index < dataArr.size(); index++) {
                JSONObject oneData = dataArr.getJSONObject(index);
                budgetMonthDao.syncBudgetMonthProcess(oneData);
            }
            result.setSuccess(true);
            result.setMessage("同步成功！");
            return result;
        } catch (Exception e) {
            logger.error("Exception in syncMonthProcess", e);
            return new JsonResult(false, "系统异常！");
        }
    }
}
