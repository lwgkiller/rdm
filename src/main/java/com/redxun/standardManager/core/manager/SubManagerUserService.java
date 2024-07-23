package com.redxun.standardManager.core.manager;

import java.util.*;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.docx4j.org.apache.poi.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.standardManager.core.dao.SubManagerUserDao;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.standardManager.core.util.ResultUtil;

/**
 * @author zhangzhen
 */
@Service
public class SubManagerUserService {
    private static final Logger logger = LoggerFactory.getLogger(SubManagerUserService.class);
    @Autowired
    private SubManagerUserDao subManagerUserDao;

    public JsonPageResult<?> query(HttpServletRequest request) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            Map<String, Object> params = new HashMap<>(16);
            List<Map<String, Object>> list = new ArrayList<>();
            List<Map<String, Object>> totalList = new ArrayList<>();
            params = CommonFuns.getSearchParam(params, request, true);
            list = subManagerUserDao.query(params);
            params = new HashMap<>(16);
            params = CommonFuns.getSearchParam(params, request, false);
            totalList = subManagerUserDao.query(params);
            CommonFuns.convertDate(list);
            // 返回结果
            result.setData(list);
            result.setTotal(totalList.size());
        } catch (Exception e) {
            logger.error("Exception in 群组人员查询异常", e);
            result.setSuccess(false);
            result.setMessage("群组人员查询异常");
        }
        return result;
    }

    /**
     * 查询用户所在的子管理员用户组，标准体系类别与标准体系(包括所有的子体系)的数据 { "GL":["dsssdfsfd","fdsfdsfsd"], "JS":["sfdsfsf","sdfdsfds"] }
     * 
     * @param userId
     * @return
     */
    public JSONObject querySubManagerSystemIds(String userId) {
        JSONObject result = new JSONObject();
        // 先查询出该用户所属的所有用户组对应的体系ID
        List<Map<String, String>> querySubManagerData = subManagerUserDao.querySubManagerSystemIds(userId);
        if (querySubManagerData == null || querySubManagerData.isEmpty()) {
            return result;
        }
        Map<String, Set<String>> sysCategoryId2ParentIds = new HashMap<String, Set<String>>();
        for (Map<String, String> oneInfo : querySubManagerData) {
            String systemCategoryId = oneInfo.get("systemCategoryId");
            String systemId = oneInfo.get("systemId");
            if (!sysCategoryId2ParentIds.containsKey(systemCategoryId)) {
                sysCategoryId2ParentIds.put(systemCategoryId, new HashSet<>());
            }
            sysCategoryId2ParentIds.get(systemCategoryId).add(systemId);
        }
        // 再查询出所有体系对应的子体系
        Map<String, Object> param = new HashMap<>();
        for (Map.Entry<String, Set<String>> entry : sysCategoryId2ParentIds.entrySet()) {
            String systemCategoryId = entry.getKey();
            param.put("systemCategoryId", systemCategoryId);
            Set<String> thisCategorySystemIds=new HashSet<>();
            Set<String> parentIds = entry.getValue();
            for (String oneParentId : parentIds) {
                param.put("systemId", oneParentId);
                Map<String, String> subSystemIdsResult = subManagerUserDao.querySubSystemIds(param);
                String subSystemIds = subSystemIdsResult.get("result");
                if (StringUtils.isNotBlank(subSystemIds)) {
                    String[] systemIds=subSystemIds.substring(1).split(",",-1);
                    thisCategorySystemIds.addAll(Arrays.asList(systemIds));
                }
            }
            if(!thisCategorySystemIds.isEmpty()) {
                result.put(systemCategoryId,thisCategorySystemIds);
            }
        }
        return result;
    }

}
