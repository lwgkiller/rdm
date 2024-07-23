package com.redxun.rdmZhgl.core.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.rdmZhgl.core.dao.MaterialCostCenterDao;
import com.redxun.rdmZhgl.core.wsdl.WsdlUtils;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;

import groovy.util.logging.Slf4j;

/**
 * @author zz
 */
@Service
@Slf4j
public class MaterialCostCenterService {
    private static Logger logger = LoggerFactory.getLogger(MaterialCostCenterService.class);
    @Resource
    private MaterialCostCenterDao materialCostCenterDao;

    public JsonPageResult<?> getCostCenterList(HttpServletRequest request, HttpServletResponse response, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            Map<String, Object> params = new HashMap<>(16);
            List<Map<String, Object>> list = new ArrayList<>();
            List<Map<String, Object>> totalList = new ArrayList<>();
            if(doPage){
                params = CommonFuns.getSearchParam(params, request, doPage);
                list = materialCostCenterDao.getCostCenterList(params);
                params = new HashMap<>(16);
                params = CommonFuns.getSearchParam(params, request, false);
                totalList = materialCostCenterDao.getCostCenterList(params);
            }else{
                params = CommonFuns.getSearchParam(params, request, doPage);
                totalList = materialCostCenterDao.getCostCenterList(params);
                list = totalList;
            }
            CommonFuns.convertDate(list);
            result.setData(list);
            result.setTotal(totalList.size());
        } catch (Exception e) {
            logger.error("Exception in getCostCenterList", e);
            result.setSuccess(false);
            result.setMessage("系统异常");
        }
        return result;
    }
    public JsonResult asyncCostCenter(HttpServletRequest request, HttpServletResponse response){
        JSONArray resultArray = new JSONArray();
        try{
            resultArray = WsdlUtils.getCostCenter();
            if(resultArray!=null&&resultArray.size()>0){
                //先删除历史数据
                materialCostCenterDao.deleteAll();
                // 分批写入数据库(20条一次)
                JSONArray tempInsert = new JSONArray();
                for (int i = 0; i < resultArray.size(); i++) {
                    tempInsert.add(resultArray.getJSONObject(i));
                    if (tempInsert.size() % 20 == 0) {
                        materialCostCenterDao.batchInsert(tempInsert);
                        tempInsert.clear();
                    }
                }
                if (!tempInsert.isEmpty()) {
                    materialCostCenterDao.batchInsert(tempInsert);
                    tempInsert.clear();
                }

            }
        }catch (Exception e){
            logger.error("Exception in asyncCostCenter", e);
            return new JsonResult(false, "系统异常asyncCostCenter");
        }
        return new JsonResult(true, "同步成功");
    }
    public JSONArray getCostCenterDic(HttpServletRequest request, HttpServletResponse response){
        return materialCostCenterDao.getCostCenterDic();
    }
}
