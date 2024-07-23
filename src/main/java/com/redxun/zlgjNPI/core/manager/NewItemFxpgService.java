package com.redxun.zlgjNPI.core.manager;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.rdmCommon.core.util.RdmCommonUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import com.redxun.zlgjNPI.core.dao.NewItemFxpgDao;

@Service
public class NewItemFxpgService {
    private Logger logger = LoggerFactory.getLogger(NewItemFxpgService.class);
    @Autowired
    private NewItemFxpgDao newItemFxpgDao;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;

    public JsonPageResult<?> getXpsxList(HttpServletRequest request, HttpServletResponse response, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        rdmZhglUtil.addOrder(request, params, "CREATE_TIME_", "desc");
        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    // 数据库中存储的时间是UTC时间，因此需要将前台传递的北京时间转化
                    if ("rdTimeStart".equalsIgnoreCase(name)) {
                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), -8));
                    }
                    if ("rdTimeEnd".equalsIgnoreCase(name)) {
                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), 16));
                    }
                    params.put(name, value);
                }
            }
        }
        // 增加分页条件
        if (doPage) {
            rdmZhglUtil.addPage(request, params);
        }
        // 增加角色过滤的条件
        RdmCommonUtil.addListAllQueryRoleExceptDraft(params, ContextUtil.getCurrentUserId(), ContextUtil.getCurrentUser().getUserNo());
        List<Map<String, Object>> xpsxList = newItemFxpgDao.queryXpsxList(params);
        for (Map<String, Object> xpsx : xpsxList) {
            if (xpsx.get("CREATE_TIME_") != null) {
                xpsx.put("CREATE_TIME_", DateUtil.formatDate((Date)xpsx.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }

        }
        // 查询当前处理人
        xcmgProjectManager.setTaskCurrentUser(xpsxList);
        result.setData(xpsxList);
        int countXpsxDataList = newItemFxpgDao.countXpsxList(params);
        result.setTotal(countXpsxDataList);
        return result;
    }


    public void createXpsx(JSONObject formData) {
        formData.put("id", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        newItemFxpgDao.insertXpsx(formData);
    }

    public void updateXpsx(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        // 风险评估
        if (StringUtils.isNotBlank(formData.getString("changeFxpgData"))) {
            JSONArray linshiCSDataJson = JSONObject.parseArray(formData.getString("changeFxpgData"));
            for (int i = 0; i < linshiCSDataJson.size(); i++) {
                JSONObject oneObject = linshiCSDataJson.getJSONObject(i);
                String state = oneObject.getString("_state");
                String csId = oneObject.getString("csId");
                if ("added".equals(state) || StringUtils.isBlank(csId)) {
                    // 新增
                    oneObject.put("id", IdUtil.getId());
                    oneObject.put("xpfxId", formData.getString("id"));
                    oneObject.put("fxd", oneObject.getString("fxd"));
                    oneObject.put("gkcs", oneObject.getString("gkcs"));
                    oneObject.put("yzfa", oneObject.getString("yzfa"));
                    if (StringUtils.isBlank(oneObject.getString("wcTime"))) {
                        oneObject.put("wcTime", null);
                    }
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    newItemFxpgDao.addFxpg(oneObject);
                } else if ("modified".equals(state)) {
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    newItemFxpgDao.updatFxpg(oneObject);
                } else if ("removed".equals(state)) {
                    // 删除
                    newItemFxpgDao.delFxpg(oneObject.getString("id"));
                }
            }
        }
        newItemFxpgDao.updateXpsx(formData);
    }

    public JSONObject getXpsxDetail(String xpsxId) {
        JSONObject detailObj = newItemFxpgDao.queryXpsxById(xpsxId);
        return detailObj;
    }

    public List<JSONObject> getFxpgList(String xpsxId) {
        JSONObject paramJson = new JSONObject();
        paramJson.put("xpsxId", xpsxId);
        List<JSONObject> fxpgList = newItemFxpgDao.getFxpgList(paramJson);
        return fxpgList;
    }

    public JsonResult deleteXpsx(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> wtIds = Arrays.asList(ids);
        Map<String, Object> param = new HashMap<>();
        param.put("wtIds", wtIds);
        newItemFxpgDao.deleteXpsx(param);
        newItemFxpgDao.deleteFxpg(param);
        return result;
    }
}
