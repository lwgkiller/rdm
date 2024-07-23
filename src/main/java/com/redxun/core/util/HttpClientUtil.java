package com.redxun.core.util;

import java.io.*;
import java.net.URI;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.*;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.io.ChunkedInputStream;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redxun.core.entity.CookieModel;
import com.redxun.core.entity.KeyValEnt;
import com.redxun.sys.core.util.SysPropertiesUtil;

/**
 * Http客户端工具类
 * 
 * @author mansan
 * @Email chshxuan@163.com
 * @Copyright (c) 2014-2016 广州红迅软件有限公司（http://www.redxun.cn） 本源代码受软件著作法保护，请在授权允许范围内使用。
 */
public class HttpClientUtil {
    private static final Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);

    private static String defaultCharset = "utf-8";

    private static SSLContext _sslContext;

    private static SSLContext createIgnoreVerifySSL() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sc = SSLContext.getInstance("SSLv3");
        // 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法
        X509TrustManager trustManager = new X509TrustManager() {
            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(X509Certificate[] arg0, String arg1)
                throws java.security.cert.CertificateException {}

            @Override
            public void checkServerTrusted(X509Certificate[] arg0, String arg1)
                throws java.security.cert.CertificateException {}
        };

        sc.init(null, new TrustManager[] {trustManager}, null);
        return sc;
    }

    private static CloseableHttpClient createSSLClientDefault() {
        try {
            if (_sslContext == null) {
                _sslContext = createIgnoreVerifySSL();
            }
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(_sslContext);

            return HttpClients.custom().setSSLSocketFactory(sslsf).build();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return HttpClients.createDefault();
    }

    /**
     * GET请求。
     * 
     * @param url
     *            url地址
     * @param params
     *            参数结构为map。
     * @return
     * @throws Exception
     */
    public static String getFromUrl(String url, Map<String, String> params) throws Exception {
        String rtn = getFromUrl(url, params, defaultCharset);
        return rtn;
    }

    private static String getFromUrl(String url, Map<String, String> params, String charset) throws Exception {
        if (StringUtil.isEmpty(charset)) {
            charset = defaultCharset;
        }
        CloseableHttpClient httpclient = createSSLClientDefault();
        URIBuilder uriBuilder = new URIBuilder(url);
        uriBuilder.setCharset(Charset.forName(charset));
        if (params != null) {
            Iterator<String> keyIt = params.keySet().iterator();
            while (keyIt.hasNext()) {
                String key = keyIt.next();
                String val = params.get(key);
                uriBuilder.setParameter(key, val);
            }
        }
        URI uri = uriBuilder.build();

        HttpGet httpget = new HttpGet(uri);

        // 2022-04-19增加超时15秒和中断开关
        RequestConfig config = RequestConfig.custom().setConnectTimeout(15000).build();
        httpget.setConfig(config);
        String requestApiSwitch = SysPropertiesUtil.getGlobalProperty("requestApiSwitch");
        if ("off".equalsIgnoreCase(requestApiSwitch)) {
            throw new Exception("对外请求强制中断，请联系管理员");
        }

        InputStream instream = null;
        CloseableHttpResponse response = null;
        try {
            response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                instream = entity.getContent();
                StringWriter writer = new StringWriter();
                IOUtils.copy(instream, writer, charset);
                return writer.toString();
            }
        } finally {
            if (instream != null) {
                instream.close();
            }
            if (response != null) {
                response.close();
            }
        }
        return null;
    }

    /**
     * Post参数至指定URL，并且返回响应值
     * 
     * @param url
     *            url
     * @param params
     *            map参数
     * @return
     * @throws Exception
     */
    public static String postFromUrl(String url, Map<String, String> params) throws Exception {
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        Iterator<String> keyIt = params.keySet().iterator();
        while (keyIt.hasNext()) {
            String key = keyIt.next();
            String val = params.get(key);
            nvps.add(new BasicNameValuePair(key, val));
        }
        UrlEncodedFormEntity nv = new UrlEncodedFormEntity(nvps);
        return post(url, nv);
    }

    /**
     * 上传文件。
     * 
     * @param url
     *            接收文件的URL
     * @param filepath
     *            需要上传文件的目录
     * @param fileMap
     *            文件标识和文件名Map对象
     * @return 返回上传结果。
     * @throws IOException
     */
    public static String uploadFile(String url, String filepath, Map<String, String> fileMap) throws Exception {
        MultipartEntityBuilder builer = MultipartEntityBuilder.create();
        for (Map.Entry<String, String> entry : fileMap.entrySet()) {
            FileBody bin = new FileBody(new File(filepath + File.separator + entry.getValue()));
            builer.addPart(entry.getKey(), bin);
        }
        return post(url, builer.build());
    }

    /**
     * 上传文件。
     * 
     * @param url
     * @param fileMap
     * @return
     * @throws IOException
     */
    public static String uploadFile(String url, Map<String, String> fileMap) throws Exception {
        Map<String, String> txtMap = new HashMap<String, String>();
        String json = uploadFile(url, txtMap, fileMap);
        return json;
    }

    /**
     * 上传数据。
     * 
     * @param url
     *            接收的URL
     * @param txtMap
     *            文本参数
     * @param fileMap
     *            上传文件集合
     * @return
     * @throws IOException
     */
    public static String uploadFile(String url, Map<String, String> txtMap, Map<String, String> fileMap)
        throws Exception {
        MultipartEntityBuilder builer = MultipartEntityBuilder.create();
        if (BeanUtil.isNotEmpty(fileMap)) {
            for (Map.Entry<String, String> entry : fileMap.entrySet()) {
                FileBody bin = new FileBody(new File(entry.getValue()));
                builer.addPart(entry.getKey(), bin);
            }
        }

        if (BeanUtil.isNotEmpty(txtMap)) {
            for (Map.Entry<String, String> entry : txtMap.entrySet()) {
                builer.addTextBody(entry.getKey(), entry.getValue());
            }
        }

        HttpEntity reqEntity = builer.build();

        return post(url, reqEntity);
    }

    /**
     * post 数据。
     * 
     * @param url
     * @param reqEntity
     * @return
     * @throws IOException
     */
    private static String post(String url, HttpEntity reqEntity) throws Exception {
        return post(url, reqEntity, null);
    }

    /**
     * 发送请求。
     * 
     * @param url
     *            url
     * @param reqEntity
     *            请求数据
     * @param headerMap
     *            请求HEADER MAP.
     * @return
     * @throws IOException
     */
    private static String post(String url, HttpEntity reqEntity, Map<String, String> headerMap) throws Exception {

        CloseableHttpClient httpclient = createSSLClientDefault();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(reqEntity);
        if (BeanUtil.isNotEmpty(headerMap)) {
            for (Map.Entry<String, String> ent : headerMap.entrySet()) {
                httpPost.addHeader(ent.getKey(), ent.getValue());
            }
        }
        // 2022-04-19增加超时15秒和中断开关
        RequestConfig config = RequestConfig.custom().setConnectTimeout(15000).build();
        httpPost.setConfig(config);
        String requestApiSwitch = SysPropertiesUtil.getGlobalProperty("requestApiSwitch");
        if ("off".equalsIgnoreCase(requestApiSwitch)) {
            throw new Exception("对外请求强制中断，请联系管理员");
        }

        CloseableHttpResponse response = httpclient.execute(httpPost);
        try {
            HttpEntity entity = response.getEntity();
            StringBuilder sb = new StringBuilder();
            ChunkedInputStream in;
            BufferedInputStream is = new BufferedInputStream(new DataInputStream(entity.getContent()));
            byte[] byteArray = new byte[1024];
            int tmp = 0;
            while ((tmp = is.read(byteArray)) != -1) {
                sb.append(new String(byteArray, 0, tmp));
            }

            return sb.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Exception in post ", ex);
        } finally {
            response.close();
        }
        return null;
    }

    /**
     * 向服务端发送请求并接收返回的数据。
     * 
     * @param url
     * @param reqHeaders
     * @param params
     * @return
     * @throws Exception
     */
    public static HttpRtnModel postFromUrl(String url, Map<String, String> reqHeaders, Map<String, String> params)
        throws Exception {
        HttpRtnModel rtn = postFromUrl(url, reqHeaders, params, defaultCharset);
        return rtn;
    }

    private static HttpRtnModel postFromUrl(String url, Map<String, String> reqHeaders, Map<String, String> params,
        String charset) throws Exception {
        HttpPost httpPost = new HttpPost(url);
        if (BeanUtil.isNotEmpty(params)) {
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            Iterator<String> keyIt = params.keySet().iterator();
            while (keyIt.hasNext()) {
                String key = keyIt.next();
                String val = params.get(key);
                nvps.add(new BasicNameValuePair(key, val));
            }
            UrlEncodedFormEntity urlBodyEnt = new UrlEncodedFormEntity(nvps, charset);
            httpPost.setEntity(urlBodyEnt);
        }
        HttpRtnModel rtnModel = sendRequest(httpPost, reqHeaders, charset);

        return rtnModel;
    }

    /**
     * 发送put 请求。
     * 
     * @param url
     *            url
     * @param reqHeaders
     *            请求头
     * @param params
     *            请求参数
     * @return
     * @throws Exception
     */
    public static HttpRtnModel putFromUrl(String url, Map<String, String> reqHeaders, Map<String, String> params)
        throws Exception {
        HttpRtnModel rtnModel = postFromUrl(url, reqHeaders, params, defaultCharset);
        return rtnModel;
    }

    public static HttpRtnModel putFromUrl(String url, Map<String, String> reqHeaders, Map<String, String> params,
        String charset) throws Exception {
        HttpPut httpPut = new HttpPut(url);
        if (BeanUtil.isNotEmpty(params)) {
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            Iterator<String> keyIt = params.keySet().iterator();
            while (keyIt.hasNext()) {
                String key = keyIt.next();
                String val = params.get(key);
                nvps.add(new BasicNameValuePair(key, val));
            }
            UrlEncodedFormEntity ent = new UrlEncodedFormEntity(nvps, charset);
            httpPut.setEntity(ent);
        }
        HttpRtnModel rtnModel = sendRequest(httpPut, reqHeaders, charset);

        return rtnModel;
    }

    /**
     * 发送put请求。
     * 
     * @param url
     * @param reqHeaders
     * @param content
     * @return
     * @throws Exception
     */
    public static HttpRtnModel putFromUrl(String url, Map<String, String> reqHeaders, String content) throws Exception {

        HttpRtnModel rtnModel = putFromUrl(url, reqHeaders, content, defaultCharset);

        return rtnModel;
    }

    public static HttpRtnModel putFromUrl(String url, Map<String, String> reqHeaders, String content, String charset)
        throws Exception {
        HttpPut httpPut = new HttpPut(url);
        httpPut.setEntity(new StringEntity(content, charset));
        HttpRtnModel rtnModel = sendRequest(httpPut, reqHeaders, charset);

        return rtnModel;
    }

    /**
     * 发送请求内容为字符串。
     * 
     * @param url
     * @param reqHeaders
     * @param content
     * @return
     * @throws Exception
     */
    public static HttpRtnModel postFromUrl(String url, Map<String, String> reqHeaders, String content)
        throws Exception {
        HttpRtnModel rtnModel = postFromUrl(url, reqHeaders, content, defaultCharset);
        return rtnModel;
    }

    private static HttpRtnModel postFromUrl(String url, Map<String, String> reqHeaders, String content, String charset)
        throws Exception {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(new StringEntity(content, charset));

        HttpRtnModel rtnModel = sendRequest(httpPost, reqHeaders, charset);
        return rtnModel;
    }

    /**
     * 执行http请求。
     * 
     * @param httpReq
     * @return
     * @throws Exception
     */
    private static HttpRtnModel sendRequest(HttpRequestBase httpReq, Map<String, String> reqHeaders, String charset)
        throws Exception {
        if (BeanUtil.isNotEmpty(reqHeaders)) {
            for (Map.Entry<String, String> ent : reqHeaders.entrySet()) {
                httpReq.addHeader(ent.getKey(), ent.getValue());
            }
        }
        HttpRtnModel rtnModel = new HttpClientUtil().new HttpRtnModel();
        CloseableHttpClient httpclient = createSSLClientDefault();
        CloseableHttpResponse response = httpclient.execute(httpReq);
        InputStream instream = null;
        try {
            rtnModel.setStatusCode(response.getStatusLine().getStatusCode());
            HttpEntity entity = response.getEntity();
            Header[] headers = response.getAllHeaders();
            for (Header header : headers) {
                rtnModel.addHeader(header.getName(), header.getValue());
            }
            if (entity != null) {
                instream = entity.getContent();
                StringWriter writer = new StringWriter();
                IOUtils.copy(instream, writer, charset);
                rtnModel.setContent(writer.toString());
            }
        } finally {
            if (instream != null) {
                instream.close();
            }
            response.close();
        }
        return rtnModel;
    }

    /**
     * 发送删除请求。
     * 
     * @param url
     * @param reqHeaders
     * @return
     * @throws Exception
     */
    public static HttpRtnModel delFromUrl(String url, Map<String, String> reqHeaders) throws Exception {
        HttpRtnModel rtnModel = delFromUrl(url, reqHeaders, defaultCharset);
        return rtnModel;
    }

    public static HttpRtnModel delFromUrl(String url, Map<String, String> reqHeaders, String charset) throws Exception {
        HttpDelete httpDel = new HttpDelete(url);
        HttpRtnModel rtnModel = sendRequest(httpDel, reqHeaders, charset);
        return rtnModel;
    }

    /**
     * 提交Json参数
     * 
     * @param url
     * @param jsonParams
     * @return
     * @throws Exception
     */
    public static String postJson(String url, String jsonParams) throws Exception {
        String rtn = postJson(url, jsonParams, defaultCharset, null);
        return rtn;
    }

    public static String postJson(String url, String jsonParams, Map<String, String> reqHeaders) throws Exception {
        String rtn = postJson(url, jsonParams, defaultCharset, reqHeaders);
        return rtn;
    }

    public static String postJson(String url, String jsonParams, String charset, Map<String, String> reqHeaders)
        throws Exception {
        StringEntity entity = new StringEntity(jsonParams, charset);// 解决中文乱码问题
        return post(url, entity, reqHeaders);
    }

    /**
     * http 返回对象。
     * 
     * @author ray
     *
     */
    public class HttpRtnModel {

        private List<KeyValEnt> ents = new ArrayList<KeyValEnt>();
        private String content = "";
        private int statusCode = 200;

        /**
         * 添加头
         * 
         * @param key
         * @param val
         */
        public void addHeader(String key, String val) {
            KeyValEnt ent = new KeyValEnt(key, val);
            ents.add(ent);
        }

        public List<KeyValEnt> getHeader(String key) {
            List<KeyValEnt> rtnList = new ArrayList<KeyValEnt>();
            for (KeyValEnt ent : ents) {
                if (ent.getKey().equals(key)) {
                    rtnList.add(ent);
                }
            }
            return rtnList;
        }

        public List<KeyValEnt> getEnts() {
            return ents;
        }

        public void setEnts(List<KeyValEnt> ents) {
            this.ents = ents;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public int getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(int statusCode) {
            this.statusCode = statusCode;
        }

        public List<CookieModel> getCookies() {
            List<CookieModel> models = new ArrayList<CookieModel>();
            List<KeyValEnt> ents = getHeader("Set-Cookie");

            for (KeyValEnt ent : ents) {
                String val = ent.getVal().toString();
                CookieModel model = getCookie(val);
                models.add(model);
            }

            return models;
        }

        private CookieModel getCookie(String str) {
            String[] aryTmp = str.split(";");
            String tmp = aryTmp[0];
            String[] aryCookie = tmp.split("=");
            CookieModel model = new CookieModel();
            model.setName(aryCookie[0]);
            if (aryCookie.length == 2) {
                model.setValue(aryCookie[1]);
            }

            return model;
        }

        public String getSessionId() {
            List<CookieModel> list = getCookies();

            for (CookieModel model : list) {
                if ("JSESSIONID".equals(model.getName())) {
                    return model.getValue();
                }
            }
            return "";
        }

    }

    public static HttpRtnModel getFromUrlHreader(String url, Map<String, String> reqHeaders) throws Exception {
        HttpRtnModel rtnModel = getFromUrlHreader(url, reqHeaders, defaultCharset);
        return rtnModel;
    }

    private static HttpRtnModel getFromUrlHreader(String url, Map<String, String> reqHeaders, String charset)
        throws Exception {
        HttpGet httpget = new HttpGet(url);
        HttpRtnModel rtnModel = sendRequest(httpget, reqHeaders, charset);
        return rtnModel;
    }
}
