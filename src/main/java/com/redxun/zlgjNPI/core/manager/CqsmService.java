package com.redxun.zlgjNPI.core.manager;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.query.Page;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmCommon.core.manager.LoginRecordManager;
import com.redxun.zlgjNPI.core.dao.CqsmDao;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import groovy.util.logging.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service
@Slf4j
public class CqsmService {
    private Logger logger = LogManager.getLogger(CqsmService.class);
    @Autowired
    private CqsmDao cqsmDao;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private LoginRecordManager loginRecordManager;

    public JsonPageResult<?> queryCqsm(HttpServletRequest request, boolean doPage) {
        String currentUserId = ContextUtil.getCurrentUserId();
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)) {
            params.put("sortField", sortField);
            params.put("sortOrder", sortOrder);
        }
        if (StringUtils.isEmpty(sortField)) {
            params.put("sortField", "zlgj_cqsm.CREATE_TIME_");
            params.put("sortOrder", "desc");
        }
        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    // 数据库中存储的时间是UTC时间，因此需要将前台传递的北京时间转化
                    if ("publishTimeStart".equalsIgnoreCase(name)) {
                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), -8));
                    }
                    if ("publishTimeEnd".equalsIgnoreCase(name)) {
                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), 16));
                    }
                    params.put(name, value);
                }
            }
        }
        params.put("currentUserId",ContextUtil.getCurrentUserId());
        List<JSONObject> cqsmList = cqsmDao.queryCqsm(params);
        for (JSONObject cqsm : cqsmList) {
            if (cqsm.getDate("CREATE_TIME_") != null) {
                cqsm.put("CREATE_TIME_", DateUtil.formatDate(cqsm.getDate("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        // 向业务数据中写入任务相关的信息
        rdmZhglUtil.setTaskInfo2Data(cqsmList, ContextUtil.getCurrentUserId());
        List<JSONObject> finalSubList = new ArrayList<JSONObject>();
        // 根据分页进行subList截取
        if (doPage) {
            int pageIndex = RequestUtil.getInt(request, "pageIndex", 0);
            int pageSize = RequestUtil.getInt(request, "pageSize", Page.DEFAULT_PAGE_SIZE);
            int startSubListIndex = pageIndex * pageSize;
            int endSubListIndex = startSubListIndex + pageSize;
            if (startSubListIndex < cqsmList.size()) {
                finalSubList = cqsmList.subList(startSubListIndex,
                    endSubListIndex < cqsmList.size() ? endSubListIndex : cqsmList.size());
            }
        } else {
            finalSubList = cqsmList;
        }
        result.setData(finalSubList);
        result.setTotal(cqsmList.size());
        return result;
    }



    public void createCqsm(JSONObject formData) {
        formData.put("cqsmId", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        cqsmDao.insertCqsm(formData);
    }

    public void updateCqsm(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        cqsmDao.updateCqsm(formData);
    }


    public JSONObject getCqsmDetail(String cqsmId) {
        JSONObject detailObj = cqsmDao.queryCqsmById(cqsmId);
        if (detailObj == null) {
            return new JSONObject();
        }

        return detailObj;
    }

    public JsonResult deleteCqsm(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> cqsmIds = Arrays.asList(ids);
        Map<String, Object> param = new HashMap<>();
        param.put("cqsmIds", cqsmIds);
        cqsmDao.deleteCqsm(param);
        return result;
    }

}
