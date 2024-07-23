package com.redxun.core.util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

public class HttpPostFormData {
    private static final Logger logger = LoggerFactory.getLogger(HttpPostFormData.class);
    private static final String BOUNDARY = "-------48"; // muitipart协议边界值
    private static final String FILE_ENCTYPE = "multipart/form-data";
    // 存放文本类数据
    Map<String, String> textParams = new HashMap<String, String>();
    // 存放多个File文件,JSONObject中有“fileName”和“file”两个key，分别代表文件的真实名称和文件体
    Map<String, JSONObject> fileparams = new HashMap<String, JSONObject>();

    String url;

    public HttpPostFormData(String url) throws Exception {
        this.url = url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void addTextParams(String key, String value) {
        this.textParams.put(key, value);
    }

    public void addFileparams(String key, JSONObject file) {
        this.fileparams.put(key, file);
    }

    public String send(Map<String, String> reqHeaders) {
        String response = null;
        try {
            InputStream returnStream = post(this.url, this.textParams, this.fileparams, reqHeaders);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            if (returnStream != null) {
                int b;
                while ((b = returnStream.read()) != -1) {
                    out.write(b);
                }
                response = new String(out.toByteArray());
            }
        } catch (Exception e) {
            logger.error("系统异常", e);
        }
        return response;
    }

    /**
     *
     * @param urlStr
     *            http请求路径
     * @param textMap
     *            请求参数
     * @param filesMap
     *            上传文件 add by yys
     */
    public static InputStream post(String urlStr, Map<String, String> textMap, Map<String, JSONObject> filesMap,
        Map<String, String> reqHeaders) throws Exception {
        InputStream is = null;

        URL url = new URL(urlStr);
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        if (BeanUtil.isNotEmpty(reqHeaders)) {
            for (Map.Entry<String, String> ent : reqHeaders.entrySet()) {
                con.setRequestProperty(ent.getKey(), ent.getValue());
            }
        }
        con.setConnectTimeout(15000);
        con.setDoInput(true);
        con.setDoOutput(true);
        con.setUseCaches(false);
        con.setRequestMethod("POST");
        con.setRequestProperty("Connection", "Keep-Alive");
        con.setRequestProperty("Charset", "UTF-8");
        con.setRequestProperty("Content-Type", FILE_ENCTYPE + "; boundary=" + BOUNDARY);

        StringBuilder sb = null;
        DataOutputStream dos = new DataOutputStream(con.getOutputStream());
        if (textMap != null) {
            sb = new StringBuilder();
            for (String s : textMap.keySet()) {
                sb.append("--");
                sb.append(BOUNDARY);
                sb.append("\r\n");
                sb.append("Content-Disposition: form-data; name=\"");
                sb.append(s);
                sb.append("\"\r\n\r\n");
                sb.append(textMap.get(s));
                sb.append("\r\n");
            }

            dos.write(sb.toString().getBytes());
        }
        if (filesMap != null) {
            for (String s : filesMap.keySet()) {
                JSONObject fileObj = filesMap.get(s);
                String fileName = fileObj.getString("fileName");
                File file = (File)fileObj.get("file");
                sb = new StringBuilder();
                sb.append("--");
                sb.append(BOUNDARY);
                sb.append("\r\n");
                sb.append("Content-Disposition: form-data; name=\"");
                sb.append(s);
                sb.append("\"; filename=\"");
                sb.append(fileName);
                sb.append("\"\r\n");
                sb.append("Content-Type: multipart/form-data");
                sb.append("\r\n\r\n");
                dos.write(sb.toString().getBytes());

                FileInputStream fis = new FileInputStream(file);
                byte[] buffer = new byte[1024];
                int len;
                while ((len = fis.read(buffer)) != -1) {
                    dos.write(buffer, 0, len);
                }
                dos.write("\r\n".getBytes());
                fis.close();
            }

            sb = new StringBuilder();
            sb.append("--");
            sb.append(BOUNDARY);
            sb.append("--\r\n");
            dos.write(sb.toString().getBytes());
        }
        dos.flush();

        if (con.getResponseCode() == 200) {
            is = con.getInputStream();
        }

        dos.close();
        return is;
    }
}