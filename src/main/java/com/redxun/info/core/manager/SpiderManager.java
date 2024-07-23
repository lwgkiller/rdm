package com.redxun.info.core.manager;

import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.redxun.info.core.dao.InfoAuthConfigDao;
import com.redxun.info.core.dao.InfoUrlConfigDao;
import com.redxun.info.core.util.BeanReflectUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.standardManager.core.util.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.info.core.dao.InfoNewsDao;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.sys.org.dao.OsUserDao;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * @author zhangzhen
 */
@Service
public class SpiderManager {
    private static final Logger logger = LoggerFactory.getLogger(SpiderManager.class);
    @Resource
    InfoAuthConfigDao infoAuthConfigDao;
    @Resource
    InfoUrlConfigDao infoUrlConfigDao;
    public void doSpiderTest(String infoTypeId, String busTypeId) {
        // url列表进行遍历
        String url = "https://www.cehome.com/news/xinpin/1";
        String processor = "com.redxun.info.core.util.spiderprocess.Spider1";
        JSONObject urlConfig = new JSONObject();
        urlConfig.put("id", IdUtil.getId());
        urlConfig.put("url", url);
        urlConfig.put("infoTypeId", "001");
        urlConfig.put("busTypeId", "001");
        urlConfig.put("UPDATE_TIME_", new Date());
        PageProcessor pageProcessor = (PageProcessor)BeanReflectUtil.newObj(processor, urlConfig);
        Spider.create(pageProcessor).addUrl(url).thread(1).runAsync();
    }
    /**
     * 所有的爬虫模块统一都走此方法
     * */
    public JSONObject doSpider(String infoTypeName, String deptId) {
        //根据情报类型 和部门id获取 同步网站的配置
        JSONObject paramJson = new JSONObject();
        paramJson.put("infoTypeName",infoTypeName);
        paramJson.put("deptId",deptId);
        try {
            List<JSONObject> list = infoAuthConfigDao.getAuthUrls(paramJson);
            Map<String,Object> paramMap;
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
                    //更新此次同步时间
                    paramMap = new HashMap<>(16);
                    paramMap.put("id",temp.getString("id"));
                    paramMap.put("asyncDate",new Date());
                    infoUrlConfigDao.updateObject(paramMap);
                }
            }else {
                logger.info("尚未找到配置数据！");
            }
        }catch (Exception e){
            logger.error("Exception in 同步产品数据异常", e);
        }
        return ResultUtil.result(true,"",null);
    }
}
