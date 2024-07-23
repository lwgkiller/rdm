
package com.redxun.drbfm.core.service;

import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.core.util.DateFormatUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.drbfm.core.dao.DrbfmReportDao;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.standardManager.core.dao.StandardManagementDao;

@Service
public class DrbfmReportService {
    private Logger logger = LogManager.getLogger(DrbfmReportService.class);
    @Resource
    private DrbfmReportDao drbfmReportDao;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private StandardManagementDao standardManagementDao;

    /*// 导出项目进度excel
    public void exportQuotaReport(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = quotaReportList(request, response, false);
        List<Map<String, Object>> finalSubProjectList = result.getData();
        // 按照部门统计项目的个数
        List<Integer> mainDepIdCount = new ArrayList<>();
        // 统计连续相同的部门ID的个数
        String currentMainDepId = "";
        int currentMainDepIdCount = 0;
        if (!finalSubProjectList.isEmpty()) {
            currentMainDepId = finalSubProjectList.get(0).get("mainDepId").toString();
            currentMainDepIdCount = 1;
        }
        for (int i = 0; i < finalSubProjectList.size(); i++) {
            Map<String, Object> oneMap = finalSubProjectList.get(i);
            if (oneMap.get("pointStandardScore") != null
                && StringUtils.isNotBlank(oneMap.get("pointStandardScore").toString())) {
                oneMap.put("standardScore", Integer.parseInt(oneMap.get("pointStandardScore").toString()));
            }
            oneMap.put("statusName", XcmgProjectUtil.convertProjectStatusCode2Name(oneMap.get("status")));
            String riskStr = oneMap.get("hasRisk").toString();
            String riskName = "";
            if ("0".equals(riskStr)) {
                riskName = "正常";
            } else if ("1".equals(riskStr)) {
                riskName = "轻微延误";
            } else if ("2".equals(riskStr)) {
                riskName = "严重延误";
            }
            oneMap.put("riskName", riskName);
            String progressNumStr = oneMap.get("progressNum").toString() + "%";
            oneMap.put("progressNumStr", progressNumStr);
            // 跳过第一个
            if (i != 0) {
                String thisMainDepId = oneMap.get("mainDepId").toString();
                if (thisMainDepId.equals(currentMainDepId)) {
                    currentMainDepIdCount++;
                } else {
                    mainDepIdCount.add(currentMainDepIdCount);
                    currentMainDepId = thisMainDepId;
                    currentMainDepIdCount = 1;
                }
            }
        }
        mainDepIdCount.add(currentMainDepIdCount);
    
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "科技项目进度统计表";
        String excelName = nowDate + title;
        String[] fieldNames = {"牵头部门", "项目类别", "项目级别", "标准分", "项目名称", "项目编号", "项目来源", "项目负责人", "项目状态", "当前阶段", "当前处理人",
            "进度风险", "项目进度", "项目进度总积分"};
        String[] fieldCodes = {"mainDepName", "categoryName", "levelName", "standardScore", "projectName", "number",
            "sourceName", "respMan", "statusName", "currentStageName", "allTaskUserNames", "riskName", "progressNumStr",
            "stageScore"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(finalSubProjectList, fieldNames, fieldCodes, title);
        if (!mainDepIdCount.isEmpty()) {
            int sumRow = 2;
            // 合并发布年份列
            HSSFSheet sheet = wbObj.getSheetAt(0);
            for (int i = 0; i < mainDepIdCount.size(); i++) {
                int number = mainDepIdCount.get(i);
                int startRow = sumRow;
                int endRow = startRow + number - 1;
                // 行号、列号从0开始，开始和结束都包含
                sheet.addMergedRegionUnsafe(new CellRangeAddress(startRow, endRow, 0, 0));
                sumRow = endRow + 1;
            }
        }
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }*/

    public JsonPageResult<?> quotaReportList(HttpServletRequest request, HttpServletResponse response, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            Map<String, Object> params = new HashMap<>();
            rdmZhglUtil.addOrder(request, params, "jixing,drbfm_total_struct.structNumber", null);
            if (doPage) {
                rdmZhglUtil.addPage(request, params);
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
            List<JSONObject> quotaList = drbfmReportDao.queryQuotaReportList(params);
            Map<String, String> standardId2Name = new HashMap<>();
            toGetStandardId2Names(quotaList, standardId2Name);
            toAddStandardNames(quotaList, standardId2Name);
            for (JSONObject oneData : quotaList) {
                if(oneData.get("CREATE_TIME_")!=null) {
                    oneData.put("CREATE_TIME_", DateFormatUtil.format(oneData.getDate("CREATE_TIME_"),"yyyy-MM-dd"));
                }
            }
            result.setData(quotaList);
            if (doPage) {
                int total = drbfmReportDao.countQuotaReport(params);
                result.setTotal(total);
            }
            return result;
        } catch (Exception e) {
            logger.error("Exception in quotaReportList", e);
            result.setSuccess(false);
            result.setMessage("系统异常！");
        }
        return result;
    }

    // 遍历结果中的standardId并分批查询对应的name，最终组织为map
    private void toGetStandardId2Names(List<JSONObject> quotaList, Map<String, String> standardId2Name) {
        if (quotaList == null || quotaList.isEmpty()) {
            return;
        }
        Set<String> standardIds = new HashSet<>();
        for (JSONObject oneData : quotaList) {
            if (StringUtils.isNotBlank(oneData.getString("sjStandardIds"))) {
                standardIds.addAll(Arrays.asList(oneData.getString("sjStandardIds").split(",", -1)));
            }
            if (StringUtils.isNotBlank(oneData.getString("testStandardIds"))) {
                standardIds.addAll(Arrays.asList(oneData.getString("testStandardIds").split(",", -1)));
            }
            if (StringUtils.isNotBlank(oneData.getString("evaluateStandardIds"))) {
                standardIds.addAll(Arrays.asList(oneData.getString("evaluateStandardIds").split(",", -1)));
            }
        }
        // 分批查询
        if (standardIds.isEmpty()) {
            return;
        }
        Map<String, Object> param = new HashMap<>();
        List<String> tempIds = new ArrayList<>();
        for (String id : standardIds) {
            tempIds.add(id);
            if (tempIds.size() % 20 == 0) {
                param.put("ids", tempIds);
                List<JSONObject> queryData = standardManagementDao.queryStandardByIds(param);
                for (JSONObject oneData : queryData) {
                    standardId2Name.put(oneData.getString("id"), oneData.getString("standardName"));
                }
                param.clear();
                tempIds.clear();
            }
        }
        if (!tempIds.isEmpty()) {
            param.put("ids", tempIds);
            List<JSONObject> queryData = standardManagementDao.queryStandardByIds(param);
            for (JSONObject oneData : queryData) {
                standardId2Name.put(oneData.getString("id"), oneData.getString("standardName"));
            }
            param.clear();
            tempIds.clear();
        }

    }

    // 逐条增加标准对应的名称
    private void toAddStandardNames(List<JSONObject> quotaList, Map<String, String> standardId2Name) {
        for (JSONObject oneData : quotaList) {
            if (StringUtils.isNotBlank(oneData.getString("sjStandardIds"))) {
                List<String> sjIds=Arrays.asList(oneData.getString("sjStandardIds").split(",", -1));
                StringBuilder sjSb=new StringBuilder();
                for (String id:sjIds) {
                    if(standardId2Name.containsKey(id)) {
                        sjSb.append(standardId2Name.get(id)).append("，");
                    }
                }
                if (sjSb.length()>0) {
                    oneData.put("sjStandardNames",sjSb.substring(0,sjSb.length()-1));
                }
            }
            if (StringUtils.isNotBlank(oneData.getString("testStandardIds"))) {
                List<String> testIds=Arrays.asList(oneData.getString("testStandardIds").split(",", -1));
                StringBuilder testSb=new StringBuilder();
                for (String id:testIds) {
                    if(standardId2Name.containsKey(id)) {
                        testSb.append(standardId2Name.get(id)).append("，");
                    }
                }
                if (testSb.length()>0) {
                    oneData.put("testStandardNames",testSb.substring(0,testSb.length()-1));
                }
            }
            if (StringUtils.isNotBlank(oneData.getString("evaluateStandardIds"))) {
                List<String> evaluateIds=Arrays.asList(oneData.getString("evaluateStandardIds").split(",", -1));
                StringBuilder evaluateSb=new StringBuilder();
                for (String id:evaluateIds) {
                    if(standardId2Name.containsKey(id)) {
                        evaluateSb.append(standardId2Name.get(id)).append("，");
                    }
                }
                if (evaluateSb.length()>0) {
                    oneData.put("evaluateStandardNames",evaluateSb.substring(0,evaluateSb.length()-1));
                }
            }
        }
    }
}
