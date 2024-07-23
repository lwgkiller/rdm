package com.redxun.fzsj.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.query.Page;
import com.redxun.fzsj.core.dao.FzdxDao;
import com.redxun.fzsj.core.dao.FzsjDao;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.serviceEngineering.core.dao.StandardvalueShipmentnotmadeDao;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FzdxService {
    private static Logger logger = LoggerFactory.getLogger(FzdxService.class);

    @Autowired
    private FzdxDao fzdxDao;

    public JsonPageResult <?> fzdxListQuery(HttpServletRequest request, HttpServletResponse response) {
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
        params.put("startIndex", 0);
        params.put("pageSize", 20);
        String pageIndex = request.getParameter("pageIndex");
        String pageSize = request.getParameter("pageSize");
        if (StringUtils.isNotBlank(pageIndex) && StringUtils.isNotBlank(pageSize)) {
            params.put("startIndex", Integer.parseInt(pageSize) * Integer.parseInt(pageIndex));
            params.put("pageSize", Integer.parseInt(pageSize));
        }
        List <JSONObject> fzdxList = fzdxDao.fzfxxListQuery(params);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        for (JSONObject fzsj : fzdxList) {
            if (fzsj.get("completionTime") != null) {
                fzsj.put("completionTime", format.format(fzsj.get("completionTime")));
            }
            if (fzsj.get("CREATE_TIME_") != null) {
                fzsj.put("CREATE_TIME_", sdf.format(fzsj.get("CREATE_TIME_")));
            }
        }
        int count = fzdxDao.countFzfxxQuery(params);
        result.setData(fzdxList);
        result.setTotal(count);
        return result;
    }

    public List <JSONObject> getFzdxTree() {
        return fzdxDao.fzdxListQuery(new JSONObject());
    }

    public List <JSONObject> getFzdxEchartsData() {
        JSONObject param = new JSONObject();
        List <JSONObject> echartsData = new ArrayList <>();
        param.put("pid", "0");
        List <JSONObject> fzdxList = fzdxDao.fzdxListQuery(param);
        if (fzdxList.size() > 0) {
            echartsData = buildFzdxEchartsData(fzdxList);
        }
        return echartsData;
    }

    public List <JSONObject> buildFzdxEchartsData(List <JSONObject> list) {
        List <JSONObject> finalData = new ArrayList <>();
        for (JSONObject fzdx : list) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", fzdx.getString("fzdx"));
            jsonObject.put("value", fzdx.getString("id"));
            String id = fzdx.getString("id");
            JSONObject param = new JSONObject();
            param.put("pid", id);
            List <JSONObject> children = fzdxDao.fzdxListQuery(param);
            if (children.size() > 0) {
                jsonObject.put("children", buildFzdxEchartsData(children));
            }
            finalData.add(jsonObject);
        }
        return finalData;
    }

    public void createFzdx(JSONObject formDataJson) {
        String pid = formDataJson.getString("pid");
        String id = IdUtil.getId();
        if (StringUtils.isBlank(pid)) {
            formDataJson.put("pid", "0");
            formDataJson.put("path", "0." + id + ".");
        } else {
            formDataJson.put("path",  formDataJson.getString("path") + id +".");
        }
        formDataJson.put("id", id);
        formDataJson.put("CREATE_BY_",ContextUtil.getCurrentUserId());
        formDataJson.put("CREATE_TIME_",new Date());
        fzdxDao.createFzdx(formDataJson);
    }

    public void updateFzzx(JSONObject formDataJson) {
        formDataJson.put("UPDATE_BY_",ContextUtil.getCurrentUserId());
        formDataJson.put("UPDATE_TIME_",new Date());
        fzdxDao.updateFzzx(formDataJson);
    }

    public JSONObject getFzdxById(String fzdxId) {
        return fzdxDao.getFzdxById(fzdxId);
    }

    public JsonResult deleteFzdx(String path) {
        JsonResult jsonResult = new JsonResult(true);
        List <JSONObject> list = fzdxDao.selectFzdxByPath(path);
        List <String> fzdxIds = list.stream().map(jsonObject -> jsonObject.getString("id")).collect(Collectors.toList());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("fzdxIds", fzdxIds);
        fzdxDao.deleteFzfxxByFzdxId(jsonObject);
        fzdxDao.deleteFzdx(path);
        return jsonResult;
    }

    public void createFzfxx(JSONObject jsonObject) {
        jsonObject.put("id", IdUtil.getId());
        jsonObject.put("CREATE_BY_",ContextUtil.getCurrentUserId());
        jsonObject.put("creator",ContextUtil.getCurrentUser().getFullname());
        jsonObject.put("CREATE_TIME_",new Date());
        fzdxDao.createFzfxx(jsonObject);
    }

    public void updateFzfxx(JSONObject jsonObject) {
        jsonObject.put("UPDATE_BY_",ContextUtil.getCurrentUserId());
        jsonObject.put("UPDATE_TIME_",new Date());
        fzdxDao.updateFzfxx(jsonObject);
    }

    public JsonResult deleteFzfxx(String[] ids) {
        JsonResult result = new JsonResult(true,"操作成功！");
        JSONObject jsonObject= new JSONObject();
        jsonObject.put("ids", ids);
        fzdxDao.deleteFzfxxById(jsonObject);
        return result;
    }


}
