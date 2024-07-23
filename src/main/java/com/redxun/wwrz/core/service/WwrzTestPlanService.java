package com.redxun.wwrz.core.service;

import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.util.CommonExcelUtils;
import com.redxun.wwrz.core.dao.WwrzTestPlanDao;
import org.apache.http.HttpStatus;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.util.StringUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.standardManager.core.util.ResultUtil;

/**
 * @author zhangzhen
 */
@Service
public class WwrzTestPlanService {
    private static final Logger logger = LoggerFactory.getLogger(WwrzTestPlanService.class);
    @Autowired
    WwrzTestPlanDao wwrzTestPlanDao;
    @Resource
    CommonInfoManager commonInfoManager;
    public JsonPageResult<?> query(HttpServletRequest request) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            Map<String, Object> params = new HashMap<>(16);
            List<Map<String, Object>> list = new ArrayList<>();
            List<Map<String, Object>> totalList = new ArrayList<>();
            params = CommonFuns.getSearchParam(params, request, true);
            JSONObject adminJson = commonInfoManager.hasPermission("WWRZ-GLY");
            JSONObject dataJson = commonInfoManager.hasPermission("WWRZ-DATA");
            if (adminJson.getBoolean(RdmConst.ALLDATA_QUERY_KEY) || adminJson.getBoolean("WWRZ-GLY")|| dataJson.getBoolean("WWRZ-DATA")||"admin".equals(ContextUtil.getCurrentUser().getUserNo())) {
                // 分管领导看所有数据
            } else if (adminJson.getBoolean("isLeader")) {
                // 部门领导看自己部门的
                params.put("deptId",ContextUtil.getCurrentUser().getMainGroupId());
            } else {
                // 普通员工看自己的
                params.put("userId", ContextUtil.getCurrentUserId());
            }
            list = wwrzTestPlanDao.query(params);
            params = new HashMap<>(16);
            params = CommonFuns.getSearchParam(params, request, false);
            if (adminJson.getBoolean(RdmConst.ALLDATA_QUERY_KEY) || adminJson.getBoolean("WWRZ-GLY")|| dataJson.getBoolean("WWRZ-DATA")||"admin".equals(ContextUtil.getCurrentUser().getUserNo())) {
                // 分管领导看所有数据
            } else if (adminJson.getBoolean("isLeader")) {
                // 部门领导看自己部门的
                params.put("deptId",ContextUtil.getCurrentUser().getMainGroupId());
            } else {
                // 普通员工看自己的
                params.put("userId", ContextUtil.getCurrentUserId());
            }
            totalList = wwrzTestPlanDao.query(params);
            CommonFuns.convertDate(list);
            // 返回结果
            result.setData(list);
            result.setTotal(totalList.size());
        } catch (Exception e) {
            logger.error("Exception in 查询异常", e);
            result.setSuccess(false);
            result.setMessage("查询异常");
        }
        return result;
    }
    public List<JSONObject> getPlanList(HttpServletRequest request){
        List<JSONObject> list = new ArrayList<>();
        try {
            JSONObject paramJson = new JSONObject();
            Map<String, Object> params = new HashMap<>(16);
            params = CommonFuns.getSearchParam(params, request, false);
            String ids = CommonFuns.nullToString(params.get("planIds"));
            if (StringUtil.isNotEmpty(ids)) {
                String[] idArr = ids.split(",", -1);
                List<String> idList = Arrays.asList(idArr);
                paramJson.put("ids", idList);
            }
            list = wwrzTestPlanDao.getPlanListByIds(paramJson);
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }
    public JSONObject add(HttpServletRequest request) {
        JSONObject resultJson = new JSONObject();
        String id = IdUtil.getId();
        try {
            Map<String, String[]> parameters = request.getParameterMap();
            if (parameters == null || parameters.isEmpty()) {
                logger.error("表单内容为空！");
                return ResultUtil.result(false, "操作失败，表单内容为空！", "");
            }
            Map<String, Object> objBody = new HashMap<>(16);
            for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
                String mapKey = entry.getKey();
                String mapValue = entry.getValue()[0];
                if (mapKey.equals("yearMonth")) {
                    if (mapValue != null && !"".equals(mapValue)) {
                        mapValue = CommonFuns.convertDateToStr(new Date(mapValue), "yyyy-MM");
                    }
                }
                objBody.put(mapKey, mapValue);
            }
            String planCodeDateStr = "RZJH"+ DateUtil.formatDate(new Date(),"YYYYMMdd");
            //查询当天的号码
            JSONObject planCodeObj = wwrzTestPlanDao.getPlanCode(planCodeDateStr);
            String planCodeStr = "";
            if(planCodeObj==null){
                planCodeStr = "01";
            }else{
                planCodeStr = CommonFuns.genGeneralCode(planCodeObj.getString("planCodeStr"), 2);
            }
            String planCode = planCodeDateStr+planCodeStr;
            objBody.put("id", id);
            objBody.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("CREATE_TIME_", new Date());
            objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("UPDATE_TIME_", new Date());
            objBody.put("planCode", planCode);
            objBody.put("planCodeStr", planCodeStr);
            objBody.put("planCodeDateStr", planCodeDateStr);
            wwrzTestPlanDao.add(objBody);
        } catch (Exception e) {
            logger.error("Exception in add 保存测试计划", e);
            return ResultUtil.result(false, "保存测试计划异常！", "");
        }
        resultJson.put("id", id);
        return ResultUtil.result(true, "保存成功", resultJson);
    }
    public JSONObject update(HttpServletRequest request) {
        JSONObject resultJson = new JSONObject();
        try {
            Map<String, String[]> parameters = request.getParameterMap();
            Map<String, Object> objBody = new HashMap<>(16);
            for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
                String mapKey = entry.getKey();
                String mapValue = entry.getValue()[0];
                if (mapKey.equals("yearMonth")) {
                    if (mapValue != null && !"".equals(mapValue)) {
                        mapValue = CommonFuns.convertDateToStr(new Date(mapValue), "yyyy-MM");
                    }
                }
                objBody.put(mapKey, mapValue);
            }
            objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("UPDATE_TIME_", new Date());
            wwrzTestPlanDao.update(objBody);
        } catch (Exception e) {
            logger.error("Exception in update 更新测试计划", e);
            return ResultUtil.result(false, "更新测试计划异常！", "");
        }
        return ResultUtil.result(true, "更新成功", resultJson);
    }
    public JSONObject remove(HttpServletRequest request) {
        JSONObject resultJson = new JSONObject();
        try {
            Map<String, Object> params = new HashMap<>(16);
            String ids = request.getParameter("ids");
            String[] idArr = ids.split(",", -1);
            List<String> idList = Arrays.asList(idArr);
            params.put("ids", idList);
            wwrzTestPlanDao.batchDelete(params);
        } catch (Exception e) {
            logger.error("Exception in update 删除测试计划", e);
            return ResultUtil.result(false, "删除测试计划！", "");
        }
        return ResultUtil.result(true, "删除成功", resultJson);
    }
    public void exportBaseInfoExcel(HttpServletRequest request, HttpServletResponse response) {
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String excelName = nowDate + "委外整机认证测试试验计划";
        Map<String, Object> params = new HashMap<>(16);
        params =CommonFuns.getSearchParam(params, request, false);
        JSONObject adminJson = commonInfoManager.hasPermission("WWRZ-GLY");
        if(!adminJson.getBoolean("WWRZ-GLY")&&!"admin".equals(ContextUtil.getCurrentUser().getUserNo())){
            params.put("userId",ContextUtil.getCurrentUserId());
        }
        List<Map<String,Object>> list = wwrzTestPlanDao.query(params);
        Map<String, Object> certTypeMap = commonInfoManager.genMap("RZLB");
        Map<String, Object> testTypeMap = commonInfoManager.genMap("CSLB");
        for(Map<String,Object> map:list){
            if(map.get("certType")!=null){
                map.put("certTypeText",certTypeMap.get(map.get("certType")));
            }
            if(map.get("testType")!=null){
                map.put("testTypeText",testTypeMap.get(map.get("testType")));
            }
        }
        String title = "委外整机认证测试试验计划";
        String[] fieldNames = {"计划编号","部门", "型号", "产品主管","认证类别","全新/补测","测试月份","备注"};
        String[] fieldCodes = {"planCode","deptName", "productModel", "chargerName", "certTypeText","testTypeText","yearMonth","remark"};
        HSSFWorkbook wbObj = CommonExcelUtils.exportStyleExcel(list, fieldNames, fieldCodes, title);
        // 输出
        CommonExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }
    public JSONObject getItemObject(HttpServletRequest request) {
        String id = request.getParameter("id");
        if (StringUtil.isEmpty(id)) {
            return ResultUtil.result(HttpStatus.SC_BAD_REQUEST, "id不能为空", "");
        }
        JSONObject resultJSON = wwrzTestPlanDao.getObjectById(id);
        return ResultUtil.result(HttpStatus.SC_OK, "调用成功", resultJSON);
    }

}
