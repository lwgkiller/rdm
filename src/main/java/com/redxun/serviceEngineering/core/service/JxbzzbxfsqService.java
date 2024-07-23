package com.redxun.serviceEngineering.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.query.Page;
import com.redxun.materielextend.core.service.MaterielService;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.serviceEngineering.core.dao.JxbzzbshDao;
import com.redxun.serviceEngineering.core.dao.JxbzzbxfsqDao;
import com.redxun.serviceEngineering.core.dao.JxcshcDao;
import com.redxun.serviceEngineering.core.dao.StandardvalueShipmentnotmadeDao;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class JxbzzbxfsqService {
    private static Logger logger = LoggerFactory.getLogger(JxbzzbxfsqService.class);
    @Autowired
    private JxbzzbxfsqDao jxbzzbxfsqDao;

    @Autowired
    private RdmZhglUtil rdmZhglUtil;

    @Autowired
    private JxcshcDao jxcshcDao;


    //..
    public JsonPageResult<?> jxbzzbxfsqListQuery(HttpServletRequest request, HttpServletResponse response, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        JSONObject params = new JSONObject();
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)) {
            params.put("sortField", sortField);
            params.put("sortOrder", sortOrder);
        }
        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    // 数据库中存储的时间是UTC时间，因此需要将前台传递的北京时间转化
                    params.put(name, value);
                }
            }
        }
        // 增加分页条件
        if (doPage) {
            params.put("startIndex", 0);
            params.put("pageSize", 20);
            String pageIndex = request.getParameter("pageIndex");
            String pageSize = request.getParameter("pageSize");
            if (StringUtils.isNotBlank(pageIndex) && StringUtils.isNotBlank(pageSize)) {
                params.put("startIndex", Integer.parseInt(pageSize) * Integer.parseInt(pageIndex));
                params.put("pageSize", Integer.parseInt(pageSize));
            }
        }
        // 增加角色过滤的条件（需要自己办理的目前已包含在下面的条件中）
        addJxbzzbxfsqRoleParam(params, ContextUtil.getCurrentUserId(), ContextUtil.getCurrentUser().getUserNo());
        List <JSONObject> jxbzzbxfsqList = jxbzzbxfsqDao.jxbzzbxfsqListQuery(params);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (JSONObject jxbzzbxfsq : jxbzzbxfsqList) {
            if (jxbzzbxfsq.get("CREATE_TIME_") != null) {
                jxbzzbxfsq.put( "CREATE_TIME_", sdf.format(jxbzzbxfsq.get("CREATE_TIME_")));
            }
        }
        // 向业务数据中写入任务相关的信息
        rdmZhglUtil.setTaskInfo2Data(jxbzzbxfsqList, ContextUtil.getCurrentUserId());
        result.setData(jxbzzbxfsqList);
        int count = jxbzzbxfsqDao.countJxbzzbxfsqQuery(params);
        result.setTotal(count);
        return result;
    }

    // 1、admin所有，相当于不加条件；
    // 2、分管领导、服务工程人员、其他人看所有提交的，自己的；
    private void addJxbzzbxfsqRoleParam(Map<String, Object> params, String userId, String userNo) {
        params.put("currentUserId", userId);
        if (userNo.equalsIgnoreCase("admin")) {
            return;
        }
        boolean isAllDataQuery = rdmZhglUtil.judgeIsPointRoleUser(userId, RdmConst.AllDATA_QUERY_NAME);
        if (isAllDataQuery) {
            params.put("roleName", "fgld");
            return;
        }
        params.put("roleName", "ptyg");
    }

    //..
    public JsonResult deleteJxbzzbxfsq(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> businessIds = Arrays.asList(ids);
        JSONObject param = new JSONObject();
        param.put("ids", businessIds);
        jxbzzbxfsqDao.deleteJxbzzbxfsq(param);
        return result;
    }

    public JSONObject getJxbzzbxfsqDetail(String jxbzzbxfsqId) {
        JSONObject detailObj = jxbzzbxfsqDao.getJxbzzbxfsqDetail(jxbzzbxfsqId);
        if (detailObj == null) {
            return new JSONObject();
        }
        return detailObj;
    }

    public void insertJxbzzbxfsq(JSONObject formData) {
        formData.put("id", IdUtil.getId());
        formData.put("CREATE_TIME_", new Date());
        jxbzzbxfsqDao.insertJxbzzbxfsq(formData);
    }

    public void updateJxbzzbxfsq(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        jxbzzbxfsqDao.updateJxbzzbxfsq(formData);
    }

    public List <JSONObject> queryJxbzzbxfsqFileList(String jxbzzbshId) {
        if (StringUtils.isBlank(jxbzzbshId)) {
            return null;
        }
        Map<String, Object> params = new HashMap<>();
        String[] jxbzzbshIds = jxbzzbshId.split(",");
        params.put("masterIds", jxbzzbshIds);
        List <JSONObject> list = jxcshcDao.queryFileList(params);
        if (list.size() == 0) {
            return null;
        }
        List <JSONObject> fileList = new ArrayList <>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:ss:mm");
        for (JSONObject file : list) {
            if (file.get("CREATE_TIME_") != null) {
                file.put( "CREATE_TIME_", sdf.format(file.get("CREATE_TIME_")));
            }
            if (file.getString("fileName").endsWith("pdf")) {
                fileList.add(file);
            }
        }
        if (fileList.size() == 0) {
            return null;
        }
        return fileList;
    }
}
