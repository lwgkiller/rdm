package com.redxun.wwrz.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.sys.core.entity.SysDic;
import com.redxun.sys.core.entity.SysTree;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.sys.core.manager.SysTreeManager;
import com.redxun.wwrz.core.dao.CeinfoDao;
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
public class CeinfoService {
    private Logger logger = LogManager.getLogger(CeinfoService.class);
    @Autowired
    private CeinfoDao ceinfoDao;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private SysTreeManager sysTreeManager;
    @Autowired
    private SysDicManager sysDicManager;
    public JsonPageResult<?> queryCeinfo(HttpServletRequest request, HttpServletResponse response, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)) {
            params.put("sortField", sortField);
            params.put("sortOrder", sortOrder);
        }
        if (StringUtils.isEmpty(sortField)) {
            params.put("sortField", "CREATE_TIME_");
            params.put("sortOrder", "desc");
        }
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
        if (doPage) {
            rdmZhglUtil.addPage(request, params);
        }
        // 增加分页条件
        List<JSONObject> gjllList = ceinfoDao.queryCeinfo(params);
        for (JSONObject gjll : gjllList) {
            if (gjll.getDate("CREATE_TIME_") != null) {
                gjll.put("CREATE_TIME_", DateUtil.formatDate(gjll.getDate("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        result.setData(gjllList);
        int countQbgzDataList = ceinfoDao.countCeinfoList(params);
        result.setTotal(countQbgzDataList);
        return result;
    }
    
    public void insertCeinfo(JSONObject formData) {
        formData.put("ceinfoId", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        String addupdate = formData.getString("addupdate");
        if("新增".equals(addupdate)){
            JSONObject maxNumJson = ceinfoDao.queryMaxNum(formData);
            int existNumber = 0;
            String onlyNum = null;
            if (maxNumJson != null) {
                String maxNum =maxNumJson.getString("maxNum");
                existNumber = Integer.parseInt(maxNum);
            }
            int thisNumber = existNumber + 1;
            if (thisNumber < 10) {
                onlyNum = formData.getString("saleModel") + "0" +thisNumber;
            } else if (thisNumber < 100 && thisNumber >= 10) {
                onlyNum = formData.getString("saleModel") + thisNumber;
            }
            formData.put("onlyNum", onlyNum);
        }
        if("更新".equals(addupdate)){
            ceinfoDao.updateNoteStatus(formData);
        }
        formData.put("noteStatus", "0");
        ceinfoDao.createCeinfo(formData);
    }

    public void updateCeinfo(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        ceinfoDao.updateCeinfo(formData);
    }

    public JSONObject getCeinfoDetail(String standardId) {
        JSONObject detailObj = ceinfoDao.queryCeinfoById(standardId);
        if (detailObj == null) {
            return new JSONObject();
        }
        if (detailObj.getDate("CREATE_TIME_") != null) {
            detailObj.put("CREATE_TIME_", DateUtil.formatDate(detailObj.getDate("CREATE_TIME_"), "yyyy-MM-dd"));
        }
        return detailObj;
    }

    public JsonResult deleteCeinfo(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> ceinfoIds = Arrays.asList(ids);
        Map<String, Object> param = new HashMap<>();
        param.put("ceinfoIds", ceinfoIds);
        ceinfoDao.deleteCeinfo(param);
        return result;
    }

    //ce自声明查询信息
    public JSONObject queryCeinfoByOnlyNum(JSONObject result,String onlyNum) {
        try {
            List<JSONObject> detailList = ceinfoDao.queryCeinfoByOnlyNum(onlyNum);
            if (detailList.size()!=1) {
                result.put("success", false);
                result.put("message", "查询失败！未查询到相关数据");
                result.put("code", "0");
            }else {
                SysTree sysTree=sysTreeManager.getByKey("certName");
                if(sysTree==null) {
                    result.put("success", false);
                    result.put("message", "查询失败！未查询到认证机构数据");
                    result.put("code", "0");
                }
                JSONObject detail = detailList.get(0);
                String certNameId = detail.getString("notified");
                String certName = "";
                List<SysDic> sysDics=sysDicManager.getByTreeId(sysTree.getTreeId());
                for (SysDic sysDic : sysDics) {
                    if(sysDic.getKey().equalsIgnoreCase(certNameId)){
                        certName = sysDic.getValue();
                    }
                }
                detail.put("notified",certName);
                result.put("result",detail);
            }
            return result;
        } catch (Exception e) {
            logger.error("查询失败" + e.getMessage(), e);
            throw e;
        }
    }


}
