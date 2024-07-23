package com.redxun.rdmZhgl.core.service;

import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.redxun.core.util.StringUtil;
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
import com.redxun.rdmZhgl.core.dao.BbsBaseInfoDao;
import com.redxun.rdmZhgl.core.dao.BbsDiscussDao;
import com.redxun.sys.core.entity.Subsystem;
import com.redxun.sys.org.entity.OsUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.util.DateUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.standardManager.core.util.ResultUtil;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

/**
 * @author zhangzhen
 */
@Service
public class BbsDiscussService {
    private static final Logger logger = LoggerFactory.getLogger(BbsDiscussService.class);
    @Resource
    BbsDiscussDao bbsDiscussDao;
    @Resource
    BbsBaseInfoDao baseInfoDao;
    @Resource
    private SendDDNoticeManager sendDDNoticeManager;
    public JSONObject query(JSONObject paramJson) {
        List<Map<String, Object>> list = null;
        try {
            list = bbsDiscussDao.query(paramJson);
            convertDate(list);
        } catch (Exception e) {
            logger.error("Exception in 查询异常", e);
            ResultUtil.result(false,"获取评论数据异常，请联系管理员",null);
        }
        return ResultUtil.result(true,"",list);
    }
    public JSONObject getChildDiscussList(JSONObject paramJson) {
        List<Map<String, Object>> list = null;
        try {
            list = bbsDiscussDao.getChildDiscussList(paramJson);
            convertDate(list);
        } catch (Exception e) {
            logger.error("Exception in 查询异常", e);
            ResultUtil.result(false,"获取评论数据异常，请联系管理员",null);
        }
        return ResultUtil.result(true,"",list);
    }
    public JSONObject add(JSONObject postData) {
        JSONObject resultJson = new JSONObject();
        try {
            Map<String, Object> objBody = new HashMap<>(16);
            String id = "";
            String parentId = postData.getString("parentId");
            String mainId = postData.getString("mainId");
            if(StringUtil.isEmpty(parentId)){
                id = CommonFuns.genCode(4);
            }else{
                id = parentId + "-" + CommonFuns.genCode(4);
            }
            //判断随机生成的id是否重复
            Boolean flag = true;
            while (flag){
                JSONObject jsonObject = bbsDiscussDao.getObject(id);
                if(jsonObject == null){
                    flag = false;
                }else{
                    if(StringUtil.isEmpty(parentId)){
                        id = CommonFuns.genCode(4);
                    }else{
                        id = parentId + "-" + CommonFuns.genCode(4);
                    }
                }
            }
            objBody.put("id", id);
            objBody.put("mainId",mainId);
            objBody.put("content",postData.getString("content"));
            objBody.put("parentId",parentId);
            objBody.put("deptId",ContextUtil.getCurrentUser().getMainGroupId());
            objBody.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            resultJson.put("id",id);
            bbsDiscussDao.add(objBody);
            sendDingMsg(objBody);
        } catch (Exception e) {
            logger.error("Exception in add 评论异常！", e);
            return ResultUtil.result(false, "评论异常！", "");
        }
        return ResultUtil.result(true, "评论成功", resultJson);
    }
    public void sendDingMsg(Map<String, Object> paramMap){
        try {
            JSONObject noticeObj = new JSONObject();
            String mainId = CommonFuns.nullToString(paramMap.get("mainId"));
            JSONObject bbsObj = baseInfoDao.getObject(mainId);
            String title = bbsObj.getString("title");
            String parentId = CommonFuns.nullToString(paramMap.get("parentId"));
            if(StringUtil.isEmpty(parentId)){
                StringBuilder stringBuilder = new StringBuilder("【RDM论坛】");
                stringBuilder.append("您发布的帖子：“").append(title).append("”");
                stringBuilder.append("，有最新评论，请及时查看");
                stringBuilder.append("，通知时间：").append(XcmgProjectUtil.getNowLocalDateStr(DateUtil.DATE_FORMAT_FULL));
                noticeObj.put("content", stringBuilder.toString());
                sendDDNoticeManager.sendNoticeForCommon(bbsObj.getString("CREATE_BY_"), noticeObj);
            }else{
                JSONObject discussObj = bbsDiscussDao.getObject(parentId);
                StringBuilder stringBuilder = new StringBuilder("【RDM论坛】");
                stringBuilder.append("您评论的帖子：“").append(title).append("”");
                stringBuilder.append("，有最新回复，请及时查看");
                stringBuilder.append("，通知时间：").append(XcmgProjectUtil.getNowLocalDateStr(DateUtil.DATE_FORMAT_FULL));
                noticeObj.put("content", stringBuilder.toString());
                sendDDNoticeManager.sendNoticeForCommon(discussObj.getString("CREATE_BY_"), noticeObj);
            }

        }catch (Exception e){
            logger.error("发送钉钉消息异常", e);
        }
    }
    public JSONObject remove(HttpServletRequest request) {
        JSONObject resultJson = new JSONObject();
        try {
            Map<String, Object> params = new HashMap<>(16);
            String ids = request.getParameter("ids");
            String[] idArr = ids.split(",", -1);
            List<String> idList = Arrays.asList(idArr);
            params.put("ids", idList);
            bbsDiscussDao.batchDelete(params);
        } catch (Exception e) {
            logger.error("Exception in update 删除", e);
            return ResultUtil.result(false, "删除异常！", "");
        }
        return ResultUtil.result(true, "删除成功", resultJson);
    }
    public List<JSONObject> getDiscuss(JSONObject paramJson){
        return bbsDiscussDao.getDiscuss(paramJson);
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
                }
            }
        }
    }
}
