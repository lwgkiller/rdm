package com.redxun.rdMaterial.core.service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.rdMaterial.core.dao.RdMaterialSummaryDao;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.standardManager.core.util.CommonFuns;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RdMaterialSummaryService {
    private static Logger logger = LoggerFactory.getLogger(RdMaterialSummaryService.class);
    @Autowired
    private RdMaterialSummaryDao rdMaterialSummaryDao;

    //..
    public JsonPageResult<?> summaryListQuery(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        JSONObject params = new JSONObject();
        CommonFuns.getSearchParam(params, request, true);
        List<JSONObject> businessList = rdMaterialSummaryDao.summaryListQuery(params);
        int businessListCount = rdMaterialSummaryDao.countSummaryListQuery(params);
        result.setData(businessList);
        result.setTotal(businessListCount);
        return result;
    }

    //..
    public JsonPageResult<?> itemListQuery(HttpServletRequest request, HttpServletResponse response) throws ParseException {
        String materialCode = RequestUtil.getString(request, "materialCode", "");
        String untreatedTimespanBegin = RequestUtil.getString(request, "untreatedTimespanBegin", "");
        String untreatedTimespanEnd = RequestUtil.getString(request, "untreatedTimespanEnd", "");
        String theYear = RequestUtil.getString(request, "theYear", "");
        String untreatedQuantityNotEqual = RequestUtil.getString(request, "untreatedQuantityNotEqual", "");
        String responsibleDep = RequestUtil.getString(request, "responsibleDep", "");
        JsonPageResult result = new JsonPageResult(true);
        JSONObject params = new JSONObject();
        CommonFuns.getSearchParam(params, request, true);
        if (StringUtil.isNotEmpty(materialCode)) {
            params.put("materialCode", materialCode);
        }
        if (StringUtil.isNotEmpty(untreatedTimespanBegin)) {
            params.put("untreatedTimespanBegin", untreatedTimespanBegin);
        }
        if (StringUtil.isNotEmpty(untreatedTimespanEnd)) {
            params.put("untreatedTimespanEnd", untreatedTimespanEnd);
        }
        if (StringUtil.isNotEmpty(theYear)) {
            params.put("theYear", theYear);
        }
        if (StringUtil.isNotEmpty(untreatedQuantityNotEqual)) {
            params.put("untreatedQuantityNotEqual", untreatedQuantityNotEqual);
        }
        if (StringUtil.isNotEmpty(responsibleDep)) {
            params.put("responsibleDep", responsibleDep);
        }
        List<JSONObject> businessList = rdMaterialSummaryDao.itemListQuery(params);
        int businessListCount = rdMaterialSummaryDao.countItemListQuery(params);
        for (JSONObject business : businessList) {
            Date inDate = business.getDate("inDate");
            Integer untreatedTimespan = DateUtil.daysBetween(inDate, new Date());
            business.put("untreatedTimespan", untreatedTimespan);
        }
        result.setData(businessList);
        result.setTotal(businessListCount);
        return result;
    }

    //..
    public void exportItemList(HttpServletRequest request, HttpServletResponse response) throws ParseException {
        String materialCode = RequestUtil.getString(request, "materialCode", "");
        String untreatedTimespanBegin = RequestUtil.getString(request, "untreatedTimespanBegin", "");
        String untreatedTimespanEnd = RequestUtil.getString(request, "untreatedTimespanEnd", "");
        String theYear = RequestUtil.getString(request, "theYear", "");
        String untreatedQuantityNotEqual = RequestUtil.getString(request, "untreatedQuantityNotEqual", "");
        String responsibleDep = RequestUtil.getString(request, "responsibleDep", "");
        JsonPageResult result = new JsonPageResult(true);
        JSONObject params = new JSONObject();
        CommonFuns.getSearchParam(params, request, false);
        if (StringUtil.isNotEmpty(materialCode)) {
            params.put("materialCode", materialCode);
        }
        if (StringUtil.isNotEmpty(untreatedTimespanBegin)) {
            params.put("untreatedTimespanBegin", untreatedTimespanBegin);
        }
        if (StringUtil.isNotEmpty(untreatedTimespanEnd)) {
            params.put("untreatedTimespanEnd", untreatedTimespanEnd);
        }
        if (StringUtil.isNotEmpty(theYear)) {
            params.put("theYear", theYear);
        }
        if (StringUtil.isNotEmpty(untreatedQuantityNotEqual)) {
            params.put("untreatedQuantityNotEqual", untreatedQuantityNotEqual);
        }
        if (StringUtil.isNotEmpty(responsibleDep)) {
            params.put("responsibleDep", responsibleDep);
        }
        List<JSONObject> businessList = rdMaterialSummaryDao.itemListQuery(params);
        for (JSONObject business : businessList) {
            Date inDate = business.getDate("inDate");
            Integer untreatedTimespan = DateUtil.daysBetween(inDate, new Date());
            business.put("untreatedTimespan", untreatedTimespan);
        }
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "研发物料明细导出";
        String excelName = nowDate + title;
        String[] fieldNames = {"物料号", "物料描述", "入库单据号", "入库原因", "责任部门",
                "责任人", "入库日期", "入库数量", "未处置数量", "未处置搁置时长(天)"};
        String[] fieldCodes = {"materialCode", "materialDescription", "businessNo", "reasonForStorage", "responsibleDep",
                "responsibleUser", "inDate", "inQuantity", "untreatedQuantity", "untreatedTimespan"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(businessList, fieldNames, fieldCodes, title);
        //输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }
}
