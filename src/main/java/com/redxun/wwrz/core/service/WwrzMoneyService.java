package com.redxun.wwrz.core.service;

import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.core.util.DateUtil;
import com.redxun.wwrz.core.dao.WwrzMoneyDao;
import org.apache.http.HttpStatus;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.standardManager.core.util.ResultUtil;
import com.redxun.util.CommonExcelUtils;
import com.redxun.wwrz.core.dao.WwrzTestPlanDao;

/**
 * @author zhangzhen
 */
@Service
public class WwrzMoneyService {
    private static final Logger logger = LoggerFactory.getLogger(WwrzMoneyService.class);
    @Autowired
    WwrzMoneyDao wwrzMoneyDao;
    @Resource
    CommonInfoManager commonInfoManager;
    public JsonPageResult<?> query(HttpServletRequest request) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            Map<String, Object> params = new HashMap<>(16);
            List<Map<String, Object>> list = new ArrayList<>();
            List<Map<String, Object>> totalList = new ArrayList<>();
            params = CommonFuns.getSearchParam(params, request, true);
            params.put("userId",ContextUtil.getCurrentUserId());
            list = wwrzMoneyDao.query(params);
            params = new HashMap<>(16);
            params = CommonFuns.getSearchParam(params, request, false);
            params.put("userId",ContextUtil.getCurrentUserId());
            totalList = wwrzMoneyDao.query(params);
            convertDate(list);
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
                if (mapKey.equals("paymentDate")) {
                    if (mapValue != null && !"".equals(mapValue)) {
                        mapValue = CommonFuns.convertDateToStr(new Date(mapValue), "yyyy-MM-dd");
                    }
                }
                objBody.put(mapKey, mapValue);
            }
            objBody.put("id", id);
            objBody.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("CREATE_TIME_", new Date());
            objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("UPDATE_TIME_", new Date());
            wwrzMoneyDao.addObject(objBody);
        } catch (Exception e) {
            logger.error("Exception in add 保存认证费用", e);
            return ResultUtil.result(false, "保存认证费用异常！", "");
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
                if (mapKey.equals("paymentDate")) {
                    if (mapValue != null && !"".equals(mapValue)) {
                        mapValue = CommonFuns.convertDateToStr(new Date(mapValue), "yyyy-MM-dd");
                    }
                }
                objBody.put(mapKey, mapValue);
            }
            objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("UPDATE_TIME_", new Date());
            wwrzMoneyDao.updateObject(objBody);
        } catch (Exception e) {
            logger.error("Exception in update 更新认证费用", e);
            return ResultUtil.result(false, "更新认证费用异常！", "");
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
            wwrzMoneyDao.batchDelete(params);
        } catch (Exception e) {
            logger.error("Exception in update 删除认证费用", e);
            return ResultUtil.result(false, "删除认证费用！", "");
        }
        return ResultUtil.result(true, "删除成功", resultJson);
    }
    public JSONObject finish(HttpServletRequest request) {
        JSONObject resultJson = new JSONObject();
        try {
            Map<String, Object> params = new HashMap<>(16);
            String id = request.getParameter("id");
            wwrzMoneyDao.finish(id);
        } catch (Exception e) {
            logger.error("Exception in finish 标记完成", e);
            return ResultUtil.result(false, "标记完成！", "");
        }
        return ResultUtil.result(true, "标记完成", resultJson);
    }
    public void exportBaseInfoExcel(HttpServletRequest request, HttpServletResponse response) {
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String excelName = nowDate + "委外认证费用";
        Map<String, Object> params = new HashMap<>(16);
        params =CommonFuns.getSearchParam(params, request, false);
        params.put("userId",ContextUtil.getCurrentUserId());
        List<Map<String,Object>> list = wwrzMoneyDao.query(params);
        Map<String, Object> productTypeMap = commonInfoManager.genMap("CPLX");
        Map<String, Object> cabFormMap = commonInfoManager.genMap("SJSXS");
        for(Map<String,Object> map:list){
            if(map.get("productType")!=null){
                map.put("productTypeText",productTypeMap.get(map.get("productType")));
            }
            if(map.get("cabForm")!=null){
                map.put("cabFormText",cabFormMap.get(map.get("cabForm")));
            }
        }
        convertDate(list);
        String title = "委外认证费用";
        String[] fieldNames = {"销售型号", "产品类型", "司机室形式","产品主管","认证项目","合同编号","费用金额","发票编号","付款日期","报告","证书","归档单号","认证公司代号","创建人","创建日期"};
        String[] fieldCodes = {"productModel", "productTypeText", "cabFormText", "chargerName","itemNames","contractCode","money","invoiceCode","paymentDate","reportCode","certCode","documentCode","companyCode","creator","CREATE_TIME_"};
        HSSFWorkbook wbObj = CommonExcelUtils.exportStyleExcel(list, fieldNames, fieldCodes, title);
        // 输出
        CommonExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }
    public JSONObject getItemObject(HttpServletRequest request) {
        String id = request.getParameter("id");
        if (StringUtil.isEmpty(id)) {
            return ResultUtil.result(HttpStatus.SC_BAD_REQUEST, "id不能为空", "");
        }
        JSONObject resultJSON = wwrzMoneyDao.getObjectById(id);
        return ResultUtil.result(HttpStatus.SC_OK, "调用成功", resultJSON);
    }
    public JSONObject isEnd(JSONObject paramJson) {
        JSONObject resultJson = new JSONObject();
        try {
          String applyId = paramJson.getString("applyId");
          resultJson = wwrzMoneyDao.getFlowStatus(applyId);
        } catch (Exception e) {
            logger.error("Exception in isEnd ", e);
            return ResultUtil.result(false, "获取失败！", "");
        }
        return ResultUtil.result(true, "获取成功", resultJson);
    }
    /**
     * 时间格式转换
     */
    public static void convertDate(List<Map<String, Object>> list) {
        if (list != null && !list.isEmpty()) {
            for (Map<String, Object> oneApply : list) {
                for (String key : oneApply.keySet()) {
                    if (key.endsWith("_TIME_") || key.endsWith("_time") || key.endsWith("_date")) {
                        if (oneApply.get(key) != null) {
                            oneApply.put(key, DateUtil.formatDate((Date)oneApply.get(key), "yyyy-MM-dd"));
                        }
                    }
                    if ("paymentDate".equals(key)) {
                        if (oneApply.get(key) != null) {
                            oneApply.put(key, DateUtil.formatDate((Date)oneApply.get(key), "yyyy-MM-dd"));
                        }
                    }
                }
            }
        }
    }
}
