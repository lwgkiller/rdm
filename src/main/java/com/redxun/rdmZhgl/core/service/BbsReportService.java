package com.redxun.rdmZhgl.core.service;

import java.io.File;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.rdmZhgl.core.dao.BbsReportDao;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
import com.redxun.rdmZhgl.core.dao.BbsBaseInfoDao;
import com.redxun.rdmZhgl.core.dao.BbsDiscussDao;
import com.redxun.rdmZhgl.core.dao.BbsNoticeDao;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.standardManager.core.dao.StandardDao;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.standardManager.core.util.ResultUtil;
import com.redxun.sys.core.dao.SubsystemDao;
import com.redxun.sys.core.entity.Subsystem;
import com.redxun.sys.core.util.SysPropertiesUtil;
import com.redxun.sys.org.dao.OsUserDao;
import com.redxun.sys.org.entity.OsUser;
import com.redxun.util.CommonExcelUtils;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

/**
 * @author zhangzhen
 */
@Service
public class BbsReportService {
    private static final Logger logger = LoggerFactory.getLogger(BbsReportService.class);

    @Resource
    BbsReportDao bbsReportDao;
    @Resource
    BbsBaseInfoDao bbsBaseInfoDao;

    public JSONObject getBbsTypeReport(HttpServletRequest request){
        JSONObject resultJson = new JSONObject();
        JSONObject paramJson = new JSONObject();
        try {
            resultJson.put("gjta",0);
            resultJson.put("gktl",0);
            resultJson.put("zsfx",0);
            String bbs_startTime = request.getParameter("bbs_startTime");
            String bbs_endTime = request.getParameter("bbs_endTime");
            if(StringUtil.isNotEmpty(bbs_startTime)){
                bbs_startTime += " 00:00:00";
            }
            if(StringUtil.isNotEmpty(bbs_endTime)){
                bbs_endTime += " 23:59:59";
            }
            paramJson.put("bbs_startTime",bbs_startTime);
            paramJson.put("bbs_endTime",bbs_endTime);
            List<JSONObject> bbsTypeObj = bbsReportDao.getBbsType(paramJson);
            for(JSONObject temp:bbsTypeObj){
                if("gjta".equals(temp.getString("bbsType"))){
                    resultJson.put("gjta",temp.getIntValue("bbsNum"));
                }
                if("gktl".equals(temp.getString("bbsType"))){
                    resultJson.put("gktl",temp.getIntValue("bbsNum"));
                }
                if("zsfx".equals(temp.getString("bbsType"))){
                    resultJson.put("zsfx",temp.getIntValue("bbsNum"));
                }
            }
        }catch (Exception e){
            logger.error("Exception in getBbsTypeReport", e);
            return ResultUtil.result(false,"获取帖子分类统计异常",resultJson);
        }
        return ResultUtil.result(true,"",resultJson);
    }
    public JSONObject getBbsDataReport(HttpServletRequest request){
        JSONObject resultJson = new JSONObject();
        JSONObject paramJson = new JSONObject();
        try {
            resultJson.put("totalNum",0);
            resultJson.put("adopt",0);
            resultJson.put("unAdopt",0);
            resultJson.put("finished",0);
            resultJson.put("unFinished",0);
            String bbs_startTime = request.getParameter("bbs_startTime");
            String bbs_endTime = request.getParameter("bbs_endTime");
            if(StringUtil.isNotEmpty(bbs_startTime)){
                bbs_startTime += " 00:00:00";
            }
            if(StringUtil.isNotEmpty(bbs_endTime)){
                bbs_endTime += " 23:59:59";
            }
            paramJson.put("bbs_startTime",bbs_startTime);
            paramJson.put("bbs_endTime",bbs_endTime);
            //获取总数
            JSONObject totalJson = bbsReportDao.getBbsData(paramJson);
            if(totalJson != null){
                resultJson.put("totalNum",totalJson.getIntValue("totalNum"));
            }
            //获取已采纳数
            paramJson.put("isAdopt","1");
            JSONObject adoptJson = bbsReportDao.getBbsData(paramJson);
            if(adoptJson != null){
                resultJson.put("adopt",adoptJson.getIntValue("totalNum"));
            }
            //获取未采纳数
            paramJson.put("isAdopt","0");
            JSONObject unAdoptJson = bbsReportDao.getBbsData(paramJson);
            if(unAdoptJson != null){
                resultJson.put("unAdopt",unAdoptJson.getIntValue("totalNum"));
            }
            //已完成数
            paramJson.put("isAdopt",null);
            paramJson.put("finishFlag",true);
            JSONObject finishJson = bbsReportDao.getBbsData(paramJson);
            if(finishJson != null){
                resultJson.put("finished",finishJson.getIntValue("totalNum"));
            }
            //未完成数
            paramJson.put("isAdopt",null);
            paramJson.put("finishFlag",null);
            paramJson.put("unFinishFlag",true);
            JSONObject unFinishJson = bbsReportDao.getBbsData(paramJson);
            if(unFinishJson != null){
                resultJson.put("unFinished",unFinishJson.getIntValue("totalNum"));
            }
        }catch (Exception e){
            logger.error("Exception in getBbsDataReport", e);
            return ResultUtil.result(false,"获取改进提案关键节点数据",resultJson);
        }
        return ResultUtil.result(true,"",resultJson);
    }
    public JSONObject getBbsGjtaRankReport(HttpServletRequest request){
        List<JSONObject> resultJson = new ArrayList<>();
        JSONObject paramJson = new JSONObject();
        try {
            String bbs_startTime = request.getParameter("bbs_startTime");
            String bbs_endTime = request.getParameter("bbs_endTime");
            if(StringUtil.isNotEmpty(bbs_startTime)){
                bbs_startTime += " 00:00:00";
            }
            if(StringUtil.isNotEmpty(bbs_endTime)){
                bbs_endTime += " 23:59:59";
            }
            paramJson.put("bbs_startTime",bbs_startTime);
            paramJson.put("bbs_endTime",bbs_endTime);
            resultJson = bbsReportDao.getBbsGjtaData(paramJson);
        }catch (Exception e){
            logger.error("Exception in getBbsGjtaRankReport", e);
            return ResultUtil.result(false,"改进提案类发帖数/人（TOP10）",resultJson);
        }
        return ResultUtil.result(true,"",resultJson);
    }
    public JSONObject getBbsPostRankReport(HttpServletRequest request){
        List<JSONObject> resultJson = new ArrayList<>();
        JSONObject paramJson = new JSONObject();
        try {
            String bbs_startTime = request.getParameter("bbs_startTime");
            String bbs_endTime = request.getParameter("bbs_endTime");
            if(StringUtil.isNotEmpty(bbs_startTime)){
                bbs_startTime += " 00:00:00";
            }
            if(StringUtil.isNotEmpty(bbs_endTime)){
                bbs_endTime += " 23:59:59";
            }
            paramJson.put("bbs_startTime",bbs_startTime);
            paramJson.put("bbs_endTime",bbs_endTime);
            resultJson = bbsReportDao.getBbsPostData(paramJson);
        }catch (Exception e){
            logger.error("Exception in getBbsPostRankReport", e);
            return ResultUtil.result(false,"发帖数/人（TOP10）",resultJson);
        }
        return ResultUtil.result(true,"",resultJson);
    }
    public JsonPageResult<?> getBbsList(HttpServletRequest request) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            Map<String, Object> params = new HashMap<>(16);
            List<Map<String, Object>> list = new ArrayList<>();
            params = CommonFuns.getSearchParam(params, request, false);
            String reportType = CommonFuns.nullToString(params.get("reportType"));
            if("bbsData".equals(reportType)){
                params.put("bbsType","gjta");
                String barName = CommonFuns.nullToString(params.get("barName"));
                switch (barName){
                    case "已采纳":
                        params.put("isAdopt","1");
                        break;
                    case "未采纳":
                        params.put("isAdopt","0");
                        break;
                    case "已完成数":
                        params.put("finishFlag",true);
                        break;
                    case "未完成数":
                        params.put("unFinishFlag",true);
                        break;
                }
            }
            if("gjtaPostRank".equals(reportType)){
                params.put("bbsType","gjta");
                String userName = CommonFuns.nullToString(params.get("barName"));
                params.put("userName",userName);
            }
            if("postRank".equals(reportType)){
                String userName = CommonFuns.nullToString(params.get("barName"));
                params.put("userName",userName);
                String seriesName = CommonFuns.nullToString(params.get("seriesName"));
                switch (seriesName){
                    case "改进提案":
                        params.put("bbsType","gjta");
                        break;
                    case "知识分享":
                        params.put("bbsType","zsfx");
                        break;
                    case "公开讨论":
                        params.put("bbsType","gktl");
                        break;
                }
            }
            if("bbsTypeReport".equals(reportType)){
                String barName = CommonFuns.nullToString(params.get("barName"));
                switch (barName){
                    case "改进提案":
                        params.put("bbsType","gjta");
                        break;
                    case "知识分享":
                        params.put("bbsType","zsfx");
                        break;
                    case "公开讨论":
                        params.put("bbsType","gktl");
                        break;
                }
            }
            list = bbsBaseInfoDao.getBbsList(params);
            convertDate(list);
            // 返回结果
            result.setData(list);
            result.setTotal(list.size());
        } catch (Exception e) {
            logger.error("Exception in 查询异常", e);
        }
        return result;
    }




    public static void convertDate(List<Map<String, Object>> list) {
        if (list != null && !list.isEmpty()) {
            for (Map<String, Object> oneApply : list) {
                for (String key : oneApply.keySet()) {
                    if (key.endsWith("_TIME_") || key.endsWith("_time") || key.endsWith("_date")) {
                        if (oneApply.get(key) != null) {
                            oneApply.put(key, DateUtil.formatDate((Date)oneApply.get(key), "yyyy-MM-dd  HH:mm:ss"));
                        }
                    }
                    if ("closeDate".equals(key)) {
                        if (oneApply.get(key) != null) {
                            oneApply.put(key, DateUtil.formatDate((Date)oneApply.get(key), "yyyy-MM-dd HH:mm:ss"));
                        }
                    }
                    if ("planFinishDate".equals(key)) {
                        if (oneApply.get(key) != null) {
                            oneApply.put(key, DateUtil.formatDate((Date)oneApply.get(key), "yyyy-MM-dd"));
                        }
                    }
                    if ("conformDate".equals(key)) {
                        if (oneApply.get(key) != null) {
                            oneApply.put(key, DateUtil.formatDate((Date)oneApply.get(key), "yyyy-MM-dd"));
                        }
                    }
                }
            }
        }
    }
}
