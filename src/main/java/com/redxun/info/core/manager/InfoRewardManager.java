package com.redxun.info.core.manager;

import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.redxun.info.core.dao.InfoAuthConfigDao;
import com.redxun.info.core.dao.InfoRewardDao;
import com.redxun.info.core.util.BeanReflectUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.standardManager.core.util.ResultUtil;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.info.core.dao.InfoPaperDao;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.sys.org.dao.OsUserDao;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * @author zhangzhen
 */
@Service
public class InfoRewardManager {
    private static final Logger logger = LoggerFactory.getLogger(InfoRewardManager.class);
    @Resource
    InfoAuthConfigDao infoAuthConfigDao;
    @Resource
    InfoRewardDao infoRewardDao;
    public JsonPageResult<?> query(HttpServletRequest request) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            Map<String, Object> params = new HashMap<>(16);
            List<Map<String, Object>> list = new ArrayList<>();
            List<Map<String, Object>> totalList = new ArrayList<>();
            params = CommonFuns.getSearchParam(params, request, true);
            list = infoRewardDao.query(params);
            params = new HashMap<>(16);
            params = CommonFuns.getSearchParam(params, request, false);
            totalList = infoRewardDao.query(params);
            CommonFuns.convertDate(list);
            // 返回结果
            result.setData(list);
            result.setTotal(totalList.size());
        } catch (Exception e) {
            logger.error("Exception in 查询异常", e);
            result.setSuccess(false);
            result.setMessage("系统异常");
        }
        return result;
    }
    public JSONObject getObjectById(String id){
        return infoRewardDao.getObjectById(id);
    }
    public JSONObject doSpider(String infoTypeName, String deptId) {
        //根据情报类型 和部门id获取 同步网站的配置
        JSONObject paramJson = new JSONObject();
        paramJson.put("infoTypeName",infoTypeName);
        paramJson.put("deptId",deptId);
        try {
            List<JSONObject> list = infoAuthConfigDao.getAuthUrls(paramJson);
            if(list!=null&&!list.isEmpty()){
                for(JSONObject temp : list){
                    String url = temp.getString("url");
                    String processor = temp.getString("processClass");
                    JSONObject urlConfig = new JSONObject();
                    urlConfig.put("urlConfigId", temp.getString("id"));
                    urlConfig.put("url", url);
                    urlConfig.put("infoTypeId", temp.getString("infoTypeId"));
                    urlConfig.put("busTypeId", temp.getString("busTypeId"));
                    urlConfig.put("UPDATE_TIME_", new Date());
                    urlConfig.put("asyncDate", temp.getDate("asyncDate"));
                    PageProcessor pageProcessor = (PageProcessor) BeanReflectUtil.newObj(processor, urlConfig);
                    Spider.create(pageProcessor).addUrl(url).thread(1).runAsync();
                    //更新此次
                }
            }else {
                logger.info("尚未找到配置数据！");
            }
        }catch (Exception e){
            logger.error("Exception in 同步产品数据异常", e);
        }
        return ResultUtil.result(true,"",null);
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
                if (mapKey.equals("publishDate")) {
                    if (mapValue != null && !"".equals(mapValue)) {
                        mapValue = CommonFuns.convertDateToStr(new Date(mapValue), "yyyy-MM-dd");
                    }
                }
                objBody.put(mapKey, mapValue);
            }
            objBody.put("id",IdUtil.getId());
            objBody.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            infoRewardDao.addObject(objBody);
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
                if (mapKey.equals("publishDate")) {
                    if (mapValue != null && !"".equals(mapValue)) {
                        mapValue = CommonFuns.convertDateToStr(new Date(mapValue), "yyyy-MM-dd");
                    }
                }
                objBody.put(mapKey, mapValue);
            }
            objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("UPDATE_TIME_", new Date());
            infoRewardDao.updateReward(objBody);
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
            infoRewardDao.batchDelete(params);
        } catch (Exception e) {
            logger.error("Exception in update 删除", e);
            return ResultUtil.result(false, "删除异常！", "");
        }
        return ResultUtil.result(true, "删除成功", resultJson);
    }
}
