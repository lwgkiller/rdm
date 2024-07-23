package com.redxun.productDataManagement.report.manager;

import java.net.URLEncoder;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

import com.redxun.core.util.HttpClientUtil;
import com.redxun.serviceEngineering.core.dao.*;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.sys.core.manager.SysTreeManager;

@Service
public class PdmReportManager {
    private static Logger logger = LoggerFactory.getLogger(PdmReportManager.class);
    @Autowired
    private SeGeneralKanBanNewDao seGeneralKanBanNewDao;
    @Autowired
    private SysTreeManager sysTreeManager;
    @Autowired
    private SysDicManager sysDicManager;
    @Autowired
    private DecorationManualDemandDao decorationManualDemandDao;
    @Autowired
    private DecorationManualFileDao decorationManualFileDao;
    @Autowired
    private MaintenanceManualfileDao maintenanceManualfileDao;
    @Autowired
    private MaintenanceManualDemandDao maintenanceManualDemandDao;
    @Autowired
    private ExportPartsAtlasDao exportPartsAtlasDao;



//    // getPDMReport1
//    public JsonResult getPdmReport1(JSONObject params) {
//        JsonResult result = new JsonResult(true, "刷新缓存成功！");
//
//        String productModel = params.getString("productModel");
//        try {
//
//            //构建请求体
//            String reportUrlBase = sysDicManager
//                    .getBySysTreeKeyAndDicKey("pdmReportApiUrls", "report1").getValue();
//            if (StringUtils.isBlank(reportUrlBase)) {
//                result.setSuccess(false);
//                result.setMessage("pdm接口调用失败，接口url为空，请联系管理员！");
//                return result;
//            }
//            // 这里要构造请求url拼接参数
//            String url = reportUrlBase+"{"+"}";
//
//            //@mh 2023年1月27日 增加身份验证
//            String user = "mesadmin";
//            String pwd = "mesadmin";
//            Map<String, String> reqHeaders = new HashMap<>();
//            reqHeaders.put("Authorization", "Basic " + Base64.getUrlEncoder().encodeToString((user + ":" + pwd).getBytes()));
//            HttpClientUtil.HttpRtnModel httpRtnModel = HttpClientUtil.getFromUrlHreader(url, reqHeaders);
//
//            if (httpRtnModel.getStatusCode()==200) {
//                JSONObject dataFromTis = JSONObject.parseObject(httpRtnModel.getContent()).getJSONObject("msg");
//                List<JSONObject> resList = JSONObject.parseArray(dataFromTis.getString("resultData"), JSONObject.class);
//                for (JSONObject res : resList) {
//                    if (res.get("docCreateTime") != null) {
//                        res.put("docCreateTime", DateUtil.formatDate(res.getDate("docCreateTime"), "yyyy-MM-dd"));
//                    }
//                }
//                return result;
//            }else {
//                logger.info(productModel+"pdm报表接口返回值不为200");
//                return null;
//            }
//        } catch (Exception E) {
//            logger.error("pdm报表接口调用错误");
//            return null;
//        }
//    }


    //..获取总览三类通知单总下发量
    public List<JSONObject> getPdmReportChartOne(HttpServletRequest request, HttpServletResponse response, String postDataStr) {

        JSONObject postDataJson = JSONObject.parseObject(postDataStr);
        JSONObject params = new JSONObject();
        params.put("startDate", postDataJson.getString("yearMonthBegin"));
        params.put("endDate", postDataJson.getString("yearMonthEnd"));
        params.put("department", postDataJson.getString("departName"));
        //下面进行图表json的拼装
        String urlParamStr = params.toJSONString();
        List<JSONObject> resultDataList = new ArrayList<>();
        try {
            //构建请求体
            String reportUrlBase = sysDicManager
                    .getBySysTreeKeyAndDicKey("pdmReportApiUrls", "report1").getValue();
            if (StringUtils.isBlank(reportUrlBase)) {
                logger.info("pdm接口调用失败，接口url为空，请联系管理员！");
            }

            //连不上接口，测试用数据
            JSONObject obj1 = new JSONObject();
            obj1.put("yearMonth", "2022-02");
            obj1.put("tzdCnt1", 2);
            obj1.put("tzdCnt2", 4);
            obj1.put("tzdCnt3", 5);

            JSONObject obj2 = new JSONObject();
            obj2.put("yearMonth", "2022-03");
            obj2.put("tzdCnt1", 3);
            obj2.put("tzdCnt2", 2);
            obj2.put("tzdCnt3", 3);
            resultDataList.add(obj1);
            resultDataList.add(obj2);
//            return resultDataList;

            // 这里要构造请求url拼接参数
            String url = reportUrlBase+ URLEncoder.encode(urlParamStr,"UTF-8");

            //@mh 2023年1月27日 增加身份验证
            String user = "mesadmin";
            String pwd = "mesadmin";
            Map<String, String> reqHeaders = new HashMap<>();

            reqHeaders.put("Authorization", "Basic " + Base64.getUrlEncoder().encodeToString((user + ":" + pwd).getBytes()));
            HttpClientUtil.HttpRtnModel httpRtnModel = HttpClientUtil.getFromUrlHreader(url, reqHeaders);

            if (httpRtnModel.getStatusCode()==200) {
                JSONObject dataFromTis = JSONObject.parseObject(httpRtnModel.getContent()).getJSONObject("msg");
                List<JSONObject> resList = JSONObject.parseArray(dataFromTis.getString("source"), JSONObject.class);
                return resList;
            }else {
                logger.info("pdm报表接口返回值不为200");
                return null;
            }
        } catch (Exception E) {
            logger.error("pdm报表接口调用错误");
            //todo 测试时专用 上线要删掉
            return resultDataList;
//            return null;
        }


    }



    public List<JSONObject> getPdmReportChartTwo(HttpServletRequest request, HttpServletResponse response, String postDataStr) {

        JSONObject postDataJson = JSONObject.parseObject(postDataStr);
        JSONObject params = new JSONObject();
        params.put("startDate", postDataJson.getString("yearMonthBegin"));
        params.put("endDate", postDataJson.getString("yearMonthEnd"));
        params.put("department", postDataJson.getString("departName"));
        //下面进行图表json的拼装
        String urlParamStr = params.toJSONString();
        List<JSONObject> resultDataList = new ArrayList<>();
        try {
            //构建请求体
            String reportUrlBase = sysDicManager
                    .getBySysTreeKeyAndDicKey("pdmReportApiUrls", "report1").getValue();
            if (StringUtils.isBlank(reportUrlBase)) {
                logger.info("pdm接口调用失败，接口url为空，请联系管理员！");
            }
            // 这里要构造请求url拼接参数
            String url = reportUrlBase+urlParamStr;

            //@mh 2023年1月27日 增加身份验证
            String user = "mesadmin";
            String pwd = "mesadmin";
            Map<String, String> reqHeaders = new HashMap<>();
            JSONObject obj1 = new JSONObject();
            obj1.put("time", "2022-02");
            obj1.put("cnt1", 2);
            obj1.put("cnt2", 4);
            obj1.put("cnt3", 5);

            JSONObject obj2 = new JSONObject();
            obj2.put("time", "2022-03");
            obj2.put("cnt1", 3);
            obj2.put("cnt2", 2);
            obj2.put("cnt3", 3);
            resultDataList.add(obj1);
            resultDataList.add(obj2);
            return resultDataList;




//            reqHeaders.put("Authorization", "Basic " + Base64.getUrlEncoder().encodeToString((user + ":" + pwd).getBytes()));
//            HttpClientUtil.HttpRtnModel httpRtnModel = HttpClientUtil.getFromUrlHreader(url, reqHeaders);
//
//            if (httpRtnModel.getStatusCode()==200) {
//                JSONObject dataFromTis = JSONObject.parseObject(httpRtnModel.getContent()).getJSONObject("msg");
//                List<JSONObject> resList = JSONObject.parseArray(dataFromTis.getString("resultData"), JSONObject.class);
////                for (JSONObject res : resList) {
////                    if (res.get("docCreateTime") != null) {
////                        res.put("docCreateTime", DateUtil.formatDate(res.getDate("docCreateTime"), "yyyy-MM-dd"));
////                    }
////                }
//                return resList;
//            }else {
//                logger.info("pdm报表接口返回值不为200");
//                return null;
//            }
        } catch (Exception E) {
            logger.error("pdm报表接口调用错误");
            return null;
        }


    }

}
