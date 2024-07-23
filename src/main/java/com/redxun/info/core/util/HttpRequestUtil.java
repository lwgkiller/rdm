package com.redxun.info.core.util;

import java.nio.charset.Charset;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONObject;

/**
 * @author zhangzhen
 */
public class HttpRequestUtil {
    public static JSONObject doPost(String url, JSONObject date) {
        HttpClient client = HttpClients.createDefault();
        // 要调用的接口方法
        HttpPost post = new HttpPost(url);
        JSONObject jsonObject = null;
        try {
            StringEntity s = new StringEntity(date.toString(), Charset.forName("UTF-8"));
            s.setContentEncoding("UTF-8");
            s.setContentType("application/json");
            post.setEntity(s);
            post.addHeader("content-type", "application/json;charset=utf-8");
            HttpResponse res = client.execute(post);
            String response1 = EntityUtils.toString(res.getEntity());
            jsonObject = JSONObject.parseObject(response1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return jsonObject;
    }
}
