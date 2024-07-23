package com.redxun.serviceEngineering.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.query.Page;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.serviceEngineering.core.dao.JpzlDao;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import groovy.util.logging.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Service
@Slf4j
public class JpzlService {
    private Logger logger = LogManager.getLogger(JpzlService.class);
    @Autowired
    private JpzlDao jpzlDao;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;

    public JsonPageResult<?> queryJpzl(HttpServletRequest request, HttpServletResponse response, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    params.put(name, value);
                }
            }
        }
        // 增加分页条件
        List<Map<String, Object>> jpzlList = jpzlDao.queryJpzl(params);
        List<Map<String, Object>> finalSubList = new ArrayList<Map<String, Object>>();
        if (doPage) {
            // 根据分页进行subList截取
            int pageIndex = RequestUtil.getInt(request, "pageIndex", 0);
            int pageSize = RequestUtil.getInt(request, "pageSize", Page.DEFAULT_PAGE_SIZE);
            int startSubListIndex = pageIndex * pageSize;
            int endSubListIndex = startSubListIndex + pageSize;
            if (startSubListIndex < jpzlList.size()) {
                finalSubList = jpzlList.subList(startSubListIndex,
                        endSubListIndex < jpzlList.size() ? endSubListIndex : jpzlList.size());
            }
        } else {
            finalSubList = jpzlList;
        }
        result.setData(finalSubList);
        result.setTotal(jpzlList.size());
        return result;
    }

    public void insertJpzl(JSONObject formData) {
        formData.put("id", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        jpzlDao.insertJpzl(formData);
    }

    public void updateJpzl(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        jpzlDao.updateJpzl(formData);
    }


    public JSONObject jpzlDetail(String jpzlid) {
        JSONObject detailObj = jpzlDao.queryJpzlById(jpzlid);
        if (detailObj == null) {
            return new JSONObject();
        }

        return detailObj;
    }

    public JsonResult deleteJpzl(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> jpzlids = Arrays.asList(ids);
        Map<String, Object> param = new HashMap<>();
        param.put("ids", jpzlids);
        jpzlDao.deleteJpzl(param);
        return result;
    }

}
