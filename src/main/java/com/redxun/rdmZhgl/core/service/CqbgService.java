package com.redxun.rdmZhgl.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.query.Page;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmCommon.core.manager.LoginRecordManager;
import com.redxun.rdmZhgl.core.dao.CqbgDao;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class CqbgService {
    private Logger logger = LogManager.getLogger(CqbgService.class);
    @Autowired
    private CqbgDao cqbgDao;
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

    public JsonPageResult<?> queryCqbg(HttpServletRequest request, boolean doPage) {
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
            params.put("sortField", "zltz_change.CREATE_TIME_");
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
        params.put("currentUserId", ContextUtil.getCurrentUserId());
        List<JSONObject> cqbgList = cqbgDao.queryCqbg(params);
        for (JSONObject cqbg : cqbgList) {
            if (cqbg.getDate("CREATE_TIME_") != null) {
                cqbg.put("CREATE_TIME_", DateUtil.formatDate(cqbg.getDate("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        // 向业务数据中写入任务相关的信息
        rdmZhglUtil.setTaskInfo2Data(cqbgList, ContextUtil.getCurrentUserId());
        List<JSONObject> finalSubList = new ArrayList<JSONObject>();
        // 根据分页进行subList截取
        if (doPage) {
            int pageIndex = RequestUtil.getInt(request, "pageIndex", 0);
            int pageSize = RequestUtil.getInt(request, "pageSize", Page.DEFAULT_PAGE_SIZE);
            int startSubListIndex = pageIndex * pageSize;
            int endSubListIndex = startSubListIndex + pageSize;
            if (startSubListIndex < cqbgList.size()) {
                finalSubList = cqbgList.subList(startSubListIndex,
                    endSubListIndex < cqbgList.size() ? endSubListIndex : cqbgList.size());
            }
        } else {
            finalSubList = cqbgList;
        }
        result.setData(finalSubList);
        result.setTotal(cqbgList.size());
        return result;
    }

    public void createCqbg(JSONObject formData) {
        formData.put("cqbgId", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        cqbgDao.insertCqbg(formData);
    }

    public void updateCqbg(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        cqbgDao.updateCqbg(formData);
    }

    public JSONObject getCqbgDetail(String cqbgId) {
        JSONObject detailObj = cqbgDao.queryCqbgById(cqbgId);
        String projectId = detailObj.getString("projectId");
        if (detailObj == null) {
            return new JSONObject();
        }
        if (detailObj.getDate("bmscwcTime") != null) {
            detailObj.put("bmscwcTime", DateUtil.formatDate(detailObj.getDate("bmscwcTime"), "yyyy-MM-dd"));
        }
        if (StringUtils.isNotBlank(projectId)) {
            // 添加项目计划开始和结束日期
            List<JSONObject> timeJsonList = xcmgProjectOtherDao.queryTimeByProjectId();
            Stream<JSONObject> stream = timeJsonList.stream();
            Map<String, List<JSONObject>> idToTimeList =
                stream.collect(Collectors.groupingBy(j -> j.getString("projectId")));
            List<JSONObject> oneTimeList = idToTimeList.get(projectId);
            if (oneTimeList != null && !oneTimeList.isEmpty()) {
                String firstPlanStartTime = oneTimeList.get(0).getString("planStartTime");
                String lastPlanEndTime = oneTimeList.get(oneTimeList.size() - 1).getString("planEndTime");
                detailObj.put("firstPlanStartTime", firstPlanStartTime);
                detailObj.put("lastPlanEndTime", lastPlanEndTime);
            }
            //添加项目进度
            JSONObject stageJson = cqbgDao.queryCqbgProjectStage(projectId);
            detailObj.put("currentStageName", stageJson.getString("currentStageName"));
        }
        return detailObj;
    }

    public JsonResult deleteCqbg(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> cqbgIds = Arrays.asList(ids);
        Map<String, Object> param = new HashMap<>();
        param.put("cqbgIds", cqbgIds);
        cqbgDao.deleteCqbg(param);
        return result;
    }

}
