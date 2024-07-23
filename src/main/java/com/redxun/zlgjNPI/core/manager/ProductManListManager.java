package com.redxun.zlgjNPI.core.manager;

import com.alibaba.fastjson.JSONArray;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import com.redxun.zlgjNPI.core.dao.ProductManageDao;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Service
public class ProductManListManager {
    private static Logger logger = LoggerFactory.getLogger(ProductManListManager.class);
    @Resource
    private ProductManageDao productManageDao;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;

    public JsonPageResult<?> getProductManDataList(HttpServletRequest request, HttpServletResponse response, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        rdmZhglUtil.addOrder(request, params, "designType,parentName", "desc");
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

        List<Map<String, Object>> productManageList = productManageDao.queryProductManDataList(params);

        for (Map<String, Object> productManage : productManageList) {
            if (productManage.get("jlTime") != null) {
                productManage.put("jlTime", DateUtil.formatDate((Date) productManage.get("jlTime"), "yyyy-MM-dd"));
            }

            if (productManage.get("CREATE_TIME_") != null) {
                productManage.put("CREATE_TIME_", DateUtil.formatDate((Date) productManage.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }

        Iterator<Map<String, Object>> iterator = productManageList.iterator();
        while (iterator.hasNext()) {
            Map<String, Object> next = iterator.next();
            if ("DRAFTED".equals(next.get("instStatus")) && !productManageList.equals(next.get("CREATE_BY_"))) {
                iterator.remove();
            }
        }
        // 查询当前处理人
        xcmgProjectManager.setTaskCurrentUser(productManageList);
        productManageList = filterListByRole(productManageList);
        result.setData(productManageList);

//        int countProductManageList = productManageDao.countProductManageLists(params);
        int countProductManageList=productManageList.size();
        result.setTotal(countProductManageList);
        return result;
    }

    private List<Map<String, Object>> filterListByRole(List<Map<String, Object>> JstbList) {

        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        if (JstbList == null || JstbList.isEmpty()) {
            return result;
        }
        Map<String, Object> params = new HashMap<>();
        boolean isJSGLBUser = false;
        params.put("currentUserId", ContextUtil.getCurrentUserId());

        // 管理员查看所有的包括草稿数据
        if ("admin".equals(ContextUtil.getCurrentUser().getUserNo())
        ) {
            return JstbList;
        }

        String currentUserId = ContextUtil.getCurrentUserId();

        // 分管领导的查看权限
        boolean showAll = false;
        if (rdmZhglUtil.judgeUserIsFgld(ContextUtil.getCurrentUserId()) || isJSGLBUser) {
            showAll = true;
        }


        // 过滤
        for (Map<String, Object> oneProject : JstbList) {
            // 自己是当前流程处理人
            Object taskId = oneProject.get("taskId");
            if (taskId != null && oneProject.get("currentProcessUserId").toString().contains(currentUserId)) {
                result.add(oneProject);

            } else if (showAll) {
                // 对于非草稿的数据或者草稿但是创建人CREATE_BY_是自己的
                if (oneProject.get("STATUS") != null && !"DRAFTED".equals(oneProject.get("STATUS").toString())) {
                    result.add(oneProject);
                } else {
                    if (oneProject.get("CREATE_BY_").toString().equals(currentUserId)) {
                        result.add(oneProject);
                    }
                }

            } else {
                if (oneProject.get("CREATE_BY_").toString().equals(currentUserId)) {
                    result.add(oneProject);
                }

            }
        }
        return result;
    }
}
