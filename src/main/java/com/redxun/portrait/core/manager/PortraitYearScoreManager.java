package com.redxun.portrait.core.manager;

import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.portrait.core.dao.PortraitYearScoreDao;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.portrait.core.dao.PortraitDocumentDao;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.sys.org.dao.OsUserDao;

/**
 * @author zhangzhen
 */
@Service
public class PortraitYearScoreManager {
    private static final Logger logger = LoggerFactory.getLogger(PortraitYearScoreManager.class);
    @Resource
    PortraitDocumentDao portraitDocumentDao;
    @Autowired
    OsUserDao osUserDao;
    @Resource
    PortraitYearScoreDao portraitYearScoreDao;
    @Resource
    CommonInfoManager commonInfoManager;
    public List<JSONObject> getYearScoreList(HttpServletRequest request,boolean flag){
        List<JSONObject> resultArray = new ArrayList<>();
        try {
            Map<String, Object> params = new HashMap<>(16);
            params = CommonFuns.getSearchParam(params, request, flag);
            List<String> idList = new ArrayList<>();
            //用户权限
            if("admin".equals(ContextUtil.getCurrentUser().getUserNo())){
                Map<String,String> deptMap=commonInfoManager.queryDeptUnderJSZX();
                idList.addAll(deptMap.keySet());
            }else{
                JSONObject resultJson = commonInfoManager.hasPermission("YGHXZGLY");
                if(resultJson.getBoolean(RdmConst.ALLDATA_QUERY_KEY)||resultJson.getBoolean("HX-GLY")){
                    //分管领导看所有数据
                    Map<String,String> deptMap=commonInfoManager.queryDeptUnderJSZX();
                    idList.addAll(deptMap.keySet());
                }else if(resultJson.getBoolean("isLeader")){
                    //部门领导看自己部门的
                    idList.add(ContextUtil.getCurrentUser().getMainGroupId());
                }else{
                    //普通员工看自己的
                    params.put("userId",ContextUtil.getCurrentUserId());
                    idList.add(ContextUtil.getCurrentUser().getMainGroupId());
                }
            }
            params.put("ids", idList);
            resultArray = portraitYearScoreDao.query(params);
        } catch (Exception e) {
            logger.error("Exception in getYearScoreList ", e);
        }
        return resultArray;
    }


    public JSONArray genYearScore(HttpServletRequest request){
        JSONArray resultArray = new JSONArray();
        try {
            Map<String, Object> params = new HashMap<>(16);
            String reportYear = request.getParameter("reportYear");
            params.put("reportYear",reportYear);
            Map<String,String> deptMap=commonInfoManager.queryDeptUnderJSZX();
            List<String> idList = new ArrayList<>(deptMap.keySet());
            params.put("ids", idList);
            resultArray = portraitDocumentDao.getUserList(params);
            Map<String,Object> paramMap = new HashMap<>(16);
            paramMap.put("list",resultArray);
            paramMap.put("reportYear",reportYear);
            portraitYearScoreDao.delByYear(reportYear);
            portraitYearScoreDao.batchInsertOrgScore(paramMap);
            //1.计算科技项目得分
            calculateYearScore(reportYear,"techScore",50,"yearTechScore");
            //2.技术协同
            calculateYearScore(reportYear,"teamWorkScore",15,"yearTeamWorkScore");
            //3.敬业表现
            calculateYearScore(reportYear,"workScore",30,"yearWorkScore");
            //4.职业发展
            calculateYearScore(reportYear,"employeeScore",5,"yearEmployeeScore");
            //5.计算总分
            calculateYearTotal(reportYear);
        } catch (Exception e) {
            logger.error("Exception in genYearScore ", e);
            return null;
         }
        return resultArray;
    }
   /**
    * 公共计算得分
    * */
    public void calculateYearScore(String reportYear,String scoreType,float totalScore,String yearScoreType){
        JSONObject paramJson = new JSONObject();
        paramJson.put("scoreType",scoreType);
        paramJson.put("reportYear",reportYear);
        List<JSONObject> resultList = portraitYearScoreDao.getScoreRank(paramJson);
        if(resultList!=null && !resultList.isEmpty()){
            float firstScore = resultList.get(0).getFloatValue(scoreType);
            float lastScore = resultList.get(resultList.size()-1).getFloatValue(scoreType);
            float  unitScore = (firstScore-lastScore)/5;
            for(JSONObject temp:resultList){
                float currentScore = temp.getFloatValue(scoreType);
                float finalScore = formateScore(currentScore,unitScore,firstScore,totalScore);
                temp.put(yearScoreType,finalScore);
                temp.put("reportYear",reportYear);
                portraitYearScoreDao.updateYearScore(temp);
            }
        }
    }
    /**
     * 计算总得分
     * */
    public void calculateYearTotal(String reportYear){
        portraitYearScoreDao.calculateTotalScore(reportYear);
    }
    public float formateScore(float score,float unitScore,float firstScore,float totalSore){
        float finalScore = 0;
         float standardScore = totalSore/5;
        if(score>=firstScore-unitScore){
            finalScore = totalSore;
        }else if(score>=firstScore-unitScore*2){
            finalScore = totalSore-standardScore;
        }else if(score>=firstScore-unitScore*3){
            finalScore = totalSore-standardScore*2;
        }else if(score>=firstScore-unitScore*4){
            finalScore = totalSore-standardScore*3;
        }else if(score>=firstScore-unitScore*5){
            finalScore = totalSore-standardScore*4;
        }
        return finalScore;
    }
    public void exportExcel(HttpServletRequest request, HttpServletResponse response) {
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String excelName = nowDate + "年度绩效";
        List<JSONObject> list = getYearScoreList(request,false);
        String title = "年度绩效";
        String[] fieldNames = {"所属部门", "姓名", "岗位", "职级",  "技术创新", "技术协同", "敬业表现", "职业发展", "总分"};
        String[] fieldCodes = {"deptName", "userName", "post", "duty",  "yearTechScore", "yearTeamWorkScore", "yearWorkScore", "yearEmployeeScore", "yearTotalScore"};
        HSSFWorkbook wbObj = ExcelUtils.exportStyleExcel(list, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }
}
