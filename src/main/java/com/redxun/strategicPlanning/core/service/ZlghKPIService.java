package com.redxun.strategicPlanning.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.standardManager.core.manager.SubManagerUserService;
import com.redxun.strategicPlanning.core.dao.ZlghKPIDao;
import com.redxun.strategicPlanning.core.domain.ZlghAnnualData;
import com.redxun.strategicPlanning.core.domain.ZlghKpi;
import com.redxun.strategicPlanning.core.domain.vo.ParamsVo;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class ZlghKPIService {
    public static final String TARGET = "target";
    private static final Logger logger = LoggerFactory.getLogger(SubManagerUserService.class);
    @Resource
    private ZlghKPIDao zlghKPIDao;
    @Resource
    private RdmZhglUtil rdmZhglUtil;

    public JsonPageResult<?> getKPIList(HttpServletRequest request, ParamsVo paramsVo, boolean doPage) {
        Map<String, Object> param = new HashMap<>();
        JsonPageResult result = new JsonPageResult();
//        String KPIName = RequestUtil.getString(request, "KPI_name", "");
        if (doPage) {
            rdmZhglUtil.addPage(request, param);
        }
        // 获取查询参数
        paramsVo.getSearchConditions().forEach(searchParamsData -> {
            param.put(searchParamsData.getName(), searchParamsData.getValue());
        });
//        if (StringUtils.isNotBlank(KPIName)) {
//            param.put("KPI_name", KPIName);
//        }
        rdmZhglUtil.addOrder(request, param, "zk.`order`+0", "asc");
        List<ZlghKpi> zlghKpiList = zlghKPIDao.queryKPITest(param);
        zlghKpiList.forEach(zlghKpi -> {
            param.put("KPI_id", zlghKpi.getId());
            rdmZhglUtil.addOrder(request, param, "folk", "asc");
            List<ZlghAnnualData> zlghAnnualDataList = zlghKPIDao.selectAnnualData(param);
            zlghAnnualDataList.forEach(zlghAnnualData -> {
                // 年份
                int folk = Integer.valueOf(zlghAnnualData.getFolk());
                int curYear = DateUtil.getCurYear();
                if (folk == (curYear-1)){
                    // 前一年
                    zlghKpi.setPreFolk(zlghAnnualData.getFolk());
                    zlghKpi.setPreId(zlghAnnualData.getId());
                    zlghKpi.setPreTarget(zlghAnnualData.getTarget());
                    zlghKpi.setPreReality(zlghAnnualData.getReality());
                }
                if (folk == curYear) {
                    // 当前年
                    zlghKpi.setCurrentFolk(zlghAnnualData.getFolk());
                    zlghKpi.setCurrentId(zlghAnnualData.getId());
                    zlghKpi.setCurrentTarget(zlghAnnualData.getTarget());
                    zlghKpi.setCurrentReality(zlghAnnualData.getReality());
                }
                if (folk == (curYear+1)){
                    // 后一年
                    zlghKpi.setAfterOneFolk(zlghAnnualData.getFolk());
                    zlghKpi.setAfterOneId(zlghAnnualData.getId());
                    zlghKpi.setAfterOneTarget(zlghAnnualData.getTarget());
                    zlghKpi.setAfterOneReality(zlghAnnualData.getReality());
                }
                if (folk == (curYear+2)){
                    // 后二年
                    zlghKpi.setAfterTwoFolk(zlghAnnualData.getFolk());
                    zlghKpi.setAfterTwoId(zlghAnnualData.getId());
                    zlghKpi.setAfterTwoTarget(zlghAnnualData.getTarget());
                    zlghKpi.setAfterTwoReality(zlghAnnualData.getReality());
                }
                if (folk == (curYear+3)){
                    // 后三年
                    zlghKpi.setAfterThreeFolk(zlghAnnualData.getFolk());
                    zlghKpi.setAfterThreeId(zlghAnnualData.getId());
                    zlghKpi.setAfterThreeTarget(zlghAnnualData.getTarget());
                    zlghKpi.setAfterThreeReality(zlghAnnualData.getReality());
                }
                if (folk == (curYear+4)){
                    // 后四年
                    zlghKpi.setAfterFourFolk(zlghAnnualData.getFolk());
                    zlghKpi.setAfterFourId(zlghAnnualData.getId());
                    zlghKpi.setAfterFourTarget(zlghAnnualData.getTarget());
                    zlghKpi.setAfterFourReality(zlghAnnualData.getReality());
                }
            });

        });
        result.setData(zlghKpiList);
        result.setTotal(zlghKPIDao.countKPIList(param));
        result.setSuccess(true);
        result.setMessage("操作成功!");
        return result;
    }

    // 保存
    public void saveKpi(JsonResult result, String changeGridDataStr) {
        try {
            JSONArray arr = JSONObject.parseArray(changeGridDataStr);
            if (arr == null || arr.isEmpty()) {
                logger.warn("gridData is blank");
                result.setSuccess(false);
                result.setMessage("requestBody is blank");
                return;
            }
            List<JSONObject> jsList = new ArrayList<>();
            arr.forEach(x -> {
                JSONObject j = (JSONObject) x;
                for (Map.Entry entry : j.entrySet()) {
                    if (!entry.getKey().toString().equals("id") &&
                            !entry.getKey().toString().equals("_state")
                            && !entry.getKey().toString().equals("_id")
                            && !entry.getKey().toString().equals("_uid")
                            && !entry.getKey().toString().equals("KPI_name")
                            && entry.getKey().toString().substring(4).equals("0")) {
                        JSONObject newJS = new JSONObject();
                        newJS.put("id", j.getString("id"));
                        newJS.put("folk", entry.getKey().toString().substring(0, entry.getKey().toString().length() - 1));
                        newJS.put("target", toGetValue(j.getString(entry.getKey().toString())));
                        newJS.put("reality", toGetValue(j.getString(entry.getKey().toString().substring(0, 4) + "1")));
                        jsList.add(newJS);
                    }
                }
            });
            zlghKPIDao.updateKpi(jsList);
            List<JSONObject> list = zlghKPIDao.selectSaveData();
            //过滤不存在的数据，进行插入
            List<JSONObject> insertList = new ArrayList<>();
            for (int i = 0; i < jsList.size(); i++) {
                boolean flag = false;
                for (int y = 0; y < list.size(); y++) {
                    if (jsList.get(i).getString("id").equals(list.get(y).getString("KPI_id")) &&
                            jsList.get(i).getString("folk").equals(list.get(y).getString("folk"))
                    ) {
                        flag = true;
                    }
                }
                if (!flag) {
                    insertList.add(jsList.get(i));
                }
            }
            //将新增数据插入
            if (insertList.size() > 0) {
                zlghKPIDao.insertUpData(insertList);
            }
        } catch (Exception e) {
            logger.error("Exception in save saveAchievement", e);
            result.setSuccess(false);
            result.setMessage("Exception in save saveAchievement");
        }
    }

    private String toGetValue(String value) {
        if (StringUtils.isBlank(value)) {
            return "";
        }
        return value;
    }

    /*==============================================我是美丽的分割线==============================================*/
//关键绩效指标名称管理
    public JsonPageResult<?> getKpiNameList(HttpServletRequest request, HttpServletResponse response, boolean doPage) {
        JsonPageResult result = new JsonPageResult();
        String KPIName = RequestUtil.getString(request, "KPI_name", "");
        Map<String, Object> param = new HashMap<>();
        if (StringUtils.isNotBlank(KPIName)) {
            param.put("KPI_name", KPIName);
        }
        rdmZhglUtil.addPage(request, param);
        rdmZhglUtil.addOrder(request, param, "order", "asc");
        List<JSONObject> gjllFileList = zlghKPIDao.queryKPINameList(param);
        if (gjllFileList != null && !gjllFileList.isEmpty()) {
            for (JSONObject oneFile : gjllFileList) {
                if (oneFile.getDate("CREATE_TIME_") != null) {
                    oneFile.put("CREATE_TIME_",
                            DateUtil.formatDate(oneFile.getDate("CREATE_TIME_"), "yyyy-MM-dd HH:mm:ss"));
                }
            }
        }
        result.setData(gjllFileList);
        int count = zlghKPIDao.countKPINameList(param);
        result.setTotal(count);
        return result;
    }

    // 保存
    public void saveKpiName(JsonResult result, String changeGridDataStr) {
        try {
            JSONArray changeGridDataJson = JSONObject.parseArray(changeGridDataStr);
            if (changeGridDataJson == null || changeGridDataJson.isEmpty()) {
                logger.warn("gridData is blank");
                result.setSuccess(false);
                result.setMessage("requestBody is blank");
                return;
            }
            for (int i = 0; i < changeGridDataJson.size(); i++) {
                JSONObject oneObject = changeGridDataJson.getJSONObject(i);
                String state = oneObject.getString("_state");
                String id = oneObject.getString("id");
                if ("added".equals(state) || StringUtils.isBlank(id)) {
                    // 新增
                    String kpiId = IdUtil.getId();
                    String annualId = IdUtil.getId();
                    oneObject.put("id", kpiId);
                    oneObject.put("order", oneObject.get("order"));
                    oneObject.put("KPI_name", oneObject.get("KPI_name"));
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                    oneObject.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
                    zlghKPIDao.createKpiName(oneObject);
                    //存入绩效指标表
                    oneObject.put("id", annualId);
                    oneObject.put("KPI_id", kpiId);
                    oneObject.put("folk", oneObject.getString("folk"));
                    oneObject.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                    oneObject.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
                    zlghKPIDao.createKpiAu(oneObject);
                } else if ("modified".equals(state)) {
                    // 修改
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                    zlghKPIDao.updateKpiName(oneObject);
                } else if ("removed".equals(state)) {
                    // 删除
                    zlghKPIDao.deleteKpiName(id);
                    //删除绩效指标表中数据
                    List<ZlghAnnualData> kpiIds = zlghKPIDao.queryAnnualData(id);
                    zlghKPIDao.batchDelete(kpiIds.stream().map(ZlghAnnualData::getId).collect(Collectors.toList()));
//                    if (kpiIds.isEmpty()) {
//                        for (JSONObject jj : kpiIds) {
//                            String ids = jj.getString("id");
//                            zlghKPIDao.deleteKpi(ids);
//                        }
//                    }
                }
            }
        } catch (Exception e) {
            logger.error("Exception in save saveAchievement");
            result.setSuccess(false);
            result.setMessage("Exception in save saveAchievement");
        }
    }

    /**
     * 根据参数数据筛选出需要新增或修改的数据
     * @param zlghKpiList 需要新增或修改的原始数据
     * @return JsonResult
     */
    public JsonResult batchSaveOrUpdateKpiData(List<ZlghKpi> zlghKpiList) {
        String currentUserId = ContextUtil.getCurrentUserId();
        int curYear = DateUtil.getCurYear();
        List<ZlghAnnualData> zlghAnnualDataListParam = new ArrayList<>();
        zlghKpiList.forEach(zlghKpi -> {
            /*前一年*/
            if (StringUtil.isEmpty(zlghKpi.getPreId())) {
                if (StringUtil.isNotEmpty(zlghKpi.getPreTarget()) || StringUtil.isNotEmpty(zlghKpi.getPreReality())) {
                    zlghAnnualDataListParam.add(new ZlghAnnualData(IdUtil.getId(), zlghKpi.getId(), (curYear-1) + "",
                            zlghKpi.getPreTarget(), zlghKpi.getPreReality(), true));
                }
            } else {
                zlghAnnualDataListParam.add(new ZlghAnnualData(zlghKpi.getPreId(), zlghKpi.getId(), zlghKpi.getPreFolk(),
                        zlghKpi.getPreTarget(), zlghKpi.getPreReality(), false));
            }
            /*当前年*/
            if (StringUtil.isEmpty(zlghKpi.getCurrentId())){
                if (StringUtil.isNotEmpty(zlghKpi.getCurrentTarget()) || StringUtil.isNotEmpty(zlghKpi.getCurrentReality())) {
                    zlghAnnualDataListParam.add(new ZlghAnnualData(IdUtil.getId(), zlghKpi.getId(), curYear + "",
                            zlghKpi.getCurrentTarget(), zlghKpi.getCurrentReality(), true));
                }
            } else {
                zlghAnnualDataListParam.add(new ZlghAnnualData(zlghKpi.getCurrentId(), zlghKpi.getId(), zlghKpi.getCurrentFolk(),
                        zlghKpi.getCurrentTarget(), zlghKpi.getCurrentReality(), false));
            }
            /*后一年*/
            if (StringUtil.isEmpty(zlghKpi.getAfterOneId())){
                if (StringUtil.isNotEmpty(zlghKpi.getAfterOneTarget()) || StringUtil.isNotEmpty(zlghKpi.getAfterOneReality())) {
                    zlghAnnualDataListParam.add(new ZlghAnnualData(IdUtil.getId(), zlghKpi.getId(), (curYear+1) + "",
                            zlghKpi.getAfterOneTarget(), zlghKpi.getAfterOneReality(), true));
                }
            } else {
                zlghAnnualDataListParam.add(new ZlghAnnualData(zlghKpi.getAfterOneId(), zlghKpi.getId(), zlghKpi.getAfterOneFolk(),
                        zlghKpi.getAfterOneTarget(), zlghKpi.getAfterOneReality(), false));
            }
            /*后二年*/
            if (StringUtil.isEmpty(zlghKpi.getAfterTwoId())) {
                if (StringUtil.isNotEmpty(zlghKpi.getAfterTwoTarget()) || StringUtil.isNotEmpty(zlghKpi.getAfterTwoReality())) {

                    zlghAnnualDataListParam.add(new ZlghAnnualData(IdUtil.getId(), zlghKpi.getId(), (curYear+2) + "",
                            zlghKpi.getAfterTwoTarget(), zlghKpi.getAfterTwoReality(), true));
                }
            } else {
                zlghAnnualDataListParam.add(new ZlghAnnualData(zlghKpi.getAfterTwoId(), zlghKpi.getId(), zlghKpi.getAfterTwoFolk(),
                        zlghKpi.getAfterTwoTarget(), zlghKpi.getAfterTwoReality(), false));
            }
            /*后三年*/
            if (StringUtil.isEmpty(zlghKpi.getAfterThreeId())){
                if (StringUtil.isNotEmpty(zlghKpi.getAfterThreeTarget()) || StringUtil.isNotEmpty(zlghKpi.getAfterThreeReality())) {
                    zlghAnnualDataListParam.add(new ZlghAnnualData(IdUtil.getId(), zlghKpi.getId(), (curYear+3) + "",
                            zlghKpi.getAfterThreeTarget(), zlghKpi.getAfterThreeReality(), true));
                }
            } else {
                zlghAnnualDataListParam.add(new ZlghAnnualData(zlghKpi.getAfterThreeId(), zlghKpi.getId(), zlghKpi.getAfterThreeFolk(),
                        zlghKpi.getAfterThreeTarget(), zlghKpi.getAfterThreeReality(), false));
            }
            /*后四年*/
            if (StringUtil.isEmpty(zlghKpi.getAfterFourId())) {
                if (StringUtil.isNotEmpty(zlghKpi.getAfterFourTarget()) || StringUtil.isNotEmpty(zlghKpi.getAfterFourReality())) {
                    zlghAnnualDataListParam.add(new ZlghAnnualData(IdUtil.getId(), zlghKpi.getId(), (curYear+4) + "",
                            zlghKpi.getAfterFourTarget(), zlghKpi.getAfterFourReality(), true));
                }
            } else {
                zlghAnnualDataListParam.add(new ZlghAnnualData(zlghKpi.getAfterFourId(), zlghKpi.getId(), zlghKpi.getAfterFourFolk(),
                        zlghKpi.getAfterFourTarget(), zlghKpi.getAfterFourReality(), false));
            }
        });
        List<ZlghAnnualData> insertList = zlghAnnualDataListParam.stream().filter(ZlghAnnualData::getCanInsert).collect(Collectors.toList());
        JsonResult result = new JsonResult();
        try {
            if (insertList.size() > 0) {
                insertList.forEach(zlghAnnualData -> {zlghAnnualData.setCreateBy(currentUserId);});
                zlghKPIDao.batchInsert(insertList);
            }
        } catch (Exception e) {
            logger.error("kpi新增失败！异常信息：{}", e.getMessage());
            return new JsonResult(false, "kpi新增失败");
        }
        List<ZlghAnnualData> modifyList = zlghAnnualDataListParam.stream().filter(ZlghAnnualData -> !ZlghAnnualData.getCanInsert()).filter(zlghAnnualData -> {
            ZlghAnnualData zlghAnnualDataTemp = zlghKPIDao.selectById(zlghAnnualData.getId());
            return !zlghAnnualData.equals(zlghAnnualDataTemp);
        }).collect(Collectors.toList());
        try {
            if (modifyList.size() > 0) {
                modifyList.forEach(zlghAnnualData -> {zlghAnnualData.setUpdateBy(currentUserId);});
                zlghKPIDao.batchUpdate(modifyList);
            }
        } catch (Exception e) {
            logger.error("kpi更新失败！异常信息：{}", e.getMessage());
            return new JsonResult(false, "kpi更新失败");
        }
        return new JsonResult(true, "保存成功");
    }
}
