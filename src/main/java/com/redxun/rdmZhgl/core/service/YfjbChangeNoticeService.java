package com.redxun.rdmZhgl.core.service;

import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.core.util.DateFormatUtil;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmZhgl.core.dao.YfjbChangeNoticeDao;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.standardManager.core.util.ResultUtil;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

/**
 * @author zhangzhen
 */
@Service
public class YfjbChangeNoticeService {
    private static final Logger logger = LoggerFactory.getLogger(YfjbChangeNoticeService.class);
    @Resource
    YfjbChangeNoticeDao yfjbChangeNoticeDao;
    @Resource
    CommonInfoManager commonInfoManager;
    @Resource
    CommonInfoDao commonInfoDao;

    public JsonPageResult<?> query(HttpServletRequest request) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            Map<String, Object> params = new HashMap<>(16);
            List<Map<String, Object>> list = new ArrayList<>();
            List<Map<String, Object>> totalList = new ArrayList<>();
            params = CommonFuns.getSearchParam(params, request, true);
            String deptId = CommonFuns.nullToString(params.get("deptId"));
            if(StringUtil.isEmpty(deptId)){
                JSONObject resultJson = commonInfoManager.hasPermission("YFJB-GLY");
                if(resultJson.getBoolean("YFJB-GLY")||resultJson.getBoolean(RdmConst.ALLDATA_QUERY_KEY)||"admin".equals(ContextUtil.getCurrentUser().getUserNo())){
                }else{
                    String currentDeptId = ContextUtil.getCurrentUser().getMainGroupId();
                    /**
                     * 如果是大挖所，则可以看到智控所所有项目
                     * */
                    if("161416982793249239".equals(currentDeptId)){
                        params.put("deptId", currentDeptId);
                        params.put("isDw", true);
                        params.put("zkDeptId", "87212403321741356");
                    }else{
                        params.put("deptId", currentDeptId);
                    }
                }
            }
            list = yfjbChangeNoticeDao.query(params);
            params = new HashMap<>(16);
            params = CommonFuns.getSearchParam(params, request, false);
            if(StringUtil.isEmpty(deptId)){
                JSONObject resultJson = commonInfoManager.hasPermission("YFJB-GLY");
                if(resultJson.getBoolean("YFJB-GLY")||resultJson.getBoolean(RdmConst.ALLDATA_QUERY_KEY)||"admin".equals(ContextUtil.getCurrentUser().getUserNo())){
                }else{
                    String currentDeptId = ContextUtil.getCurrentUser().getMainGroupId();
                    /**
                     * 如果是大挖所，则可以看到智控所所有项目
                     * */
                    if("161416982793249239".equals(currentDeptId)){
                        params.put("deptId", currentDeptId);
                        params.put("isDw", true);
                        params.put("zkDeptId", "87212403321741356");
                    }else{
                        params.put("deptId", currentDeptId);
                    }
                }
            }
            totalList = yfjbChangeNoticeDao.query(params);
            convertDate(list);
            // 返回结果
            result.setData(list);
            result.setTotal(totalList.size());
        } catch (Exception e) {
            logger.error("Exception in 查询异常", e);
        }
        return result;
    }
    public JSONObject add(HttpServletRequest request) {
        JSONObject resultJson = new JSONObject();
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
                if ("noticeDate".equals(mapKey)) {
                    if (mapValue != null && !"".equals(mapValue)) {
                        mapValue = CommonFuns.convertDateToStr(new Date(mapValue), "yyyy-MM-dd");
                    }
                }
                objBody.put(mapKey, mapValue);
            }
            String id = IdUtil.getId();
            objBody.put("id", id);
            objBody.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
//            objBody.put("deptId", ContextUtil.getCurrentUser().getMainGroupId());
            resultJson.put("id",id);
            yfjbChangeNoticeDao.addObject(objBody);
        } catch (Exception e) {
            logger.error("Exception in add 添加异常！", e);
            return ResultUtil.result(false, "添加异常！", "");
        }
        return ResultUtil.result(true, "新增成功", resultJson);
    }
    public JSONObject update(HttpServletRequest request) {
        JSONObject resultJson = new JSONObject();
        try {
            Map<String, String[]> parameters = request.getParameterMap();
            Map<String, Object> objBody = new HashMap<>(16);
            for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
                String mapKey = entry.getKey();
                String mapValue = entry.getValue()[0];
                if ("noticeDate".equals(mapKey)) {
                    if (mapValue != null && !"".equals(mapValue)) {
                        mapValue = CommonFuns.convertDateToStr(new Date(mapValue), "yyyy-MM-dd");
                    }
                }
                objBody.put(mapKey, mapValue);
            }
            objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            yfjbChangeNoticeDao.updateObject(objBody);
            resultJson.put("id",objBody.get("id"));
        } catch (Exception e) {
            logger.error("Exception in update 更新异常", e);
            return ResultUtil.result(false, "更新异常！", "");
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
            yfjbChangeNoticeDao.batchDelete(params);
        } catch (Exception e) {
            logger.error("Exception in update 删除切换通知单", e);
            return ResultUtil.result(false, "删除切换通知单异常！", "");
        }
        return ResultUtil.result(true, "删除成功", resultJson);
    }
    public JSONObject getObjectById(String id){
        JSONObject jsonObject =  yfjbChangeNoticeDao.getObjectById(id);
        return jsonObject;
    }
    public void exportExcel(HttpServletRequest request, HttpServletResponse response) {
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");

        String excelName = nowDate + "切换通知单";
        Map<String, Object> params = new HashMap<>(16);
        params = CommonFuns.getSearchParam(params, request, false);
        String deptId = CommonFuns.nullToString(params.get("deptId"));
        if(StringUtil.isEmpty(deptId)){
            JSONObject resultJson = commonInfoManager.hasPermission("YFJB-GLY");
            if(resultJson.getBoolean("YFJB-GLY")||resultJson.getBoolean(RdmConst.ALLDATA_QUERY_KEY)||"admin".equals(ContextUtil.getCurrentUser().getUserNo())){
            }else{
                params.put("deptId", ContextUtil.getCurrentUser().getMainGroupId());
            }
        }
        List<Map<String, Object>> list = yfjbChangeNoticeDao.query(params);
        convertDate(list);
        //转换字典项
        Map<String, Object> jbfsMap = new HashMap<>(16);
        Map<String, Object> sfMap = new HashMap<>(16);
        Map<String, Object> majorMap = new HashMap<>(16);
        Map categoryMap = new HashMap<>(16);
        categoryMap.put("key", "YFJB-JBFS");
        CommonFuns.getCategoryKey2Text(jbfsMap, commonInfoDao.getDicValues(categoryMap));
        categoryMap.put("key", "YESORNO");
        CommonFuns.getCategoryKey2Text(sfMap, commonInfoDao.getDicValues(categoryMap));
        categoryMap.put("key", "YFJB-SSZY");
        CommonFuns.getCategoryKey2Text(majorMap, commonInfoDao.getDicValues(categoryMap));
        for(Map<String,Object> map : list){
            if(map.get("costType")!=null){
                map.put("costTypeText",jbfsMap.get(map.get("costType")));
            }
            if(map.get("isReplace")!=null){
                map.put("isReplaceText",sfMap.get(map.get("isReplace")));
            }
            if(map.get("major")!=null){
                map.put("majorText",majorMap.get(map.get("major")));
            }
        }
        //处理标题年份
        String reportYear = CommonFuns.nullToString(params.get("reportYear"));
        if(StringUtil.isNotEmpty(reportYear)){
            reportYear = reportYear+"年";
        }
        String title = reportYear+"下发切换通知单列表";
        String[] fieldNames = {"原物料编码", "原物料名称", "原供应商", "原物料价格", "降本方式", "降本措施", "替代物料编码", "替代物料名称", "替代供应商",
                "替代物料价格", "差额", "单台用量", "代替比例(%)","单台降本","互换性","主要差异性、试制要求、竞品使用情况","已实现单台降本","风险评估","生产是否切换",
                "实际切换时间","所属专业","责任人","涉及机型","切换通知号","切换通知单下发时间","通知单中库存及切换车号","备注"};
        String[] fieldCodes = {"orgItemCode", "orgItemName", "orgSupplier","orgItemPrice", "costTypeText", "costMeasure", "newItemCode", "newItemName", "newSupplier",
                "newItemPrice",  "differentPrice", "perSum", "replaceRate", "perCost","changeable","assessment","achieveCost","risk","isReplaceText",
                "sjqh_date","majorText","responseMan","saleModel","noticeNo","noticeDate","storageAndCar","remark"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(list, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }
    public static void convertDate(List<Map<String, Object>> list) {
        if (list != null && !list.isEmpty()) {
            for (Map<String, Object> oneApply : list) {
                for (String key : oneApply.keySet()) {
                    if (key.endsWith("_TIME_") || key.endsWith("_time")) {
                        if (oneApply.get(key) != null) {
                            oneApply.put(key, DateUtil.formatDate((Date)oneApply.get(key), "yyyy-MM-dd  HH:mm:ss"));
                        }
                    }
                    if ("noticeDate".equals(key)) {
                        if (oneApply.get(key) != null) {
                            oneApply.put(key, DateUtil.formatDate((Date)oneApply.get(key), "yyyy-MM-dd"));
                        }
                    }
                }
            }
        }
    }

}
