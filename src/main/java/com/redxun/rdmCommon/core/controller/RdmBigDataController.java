package com.redxun.rdmCommon.core.controller;

import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 应用模块名称
 * <p>
 * 代码描述
 * <p>
 * Copyright: Copyright (C) 2020 XXX, Inc. All rights reserved.
 * <p>
 * Company: 徐工挖掘机械有限公司
 * <p>
 *
 * @author liangchuanjiang
 * @since 2020/6/2 17:40
 */
@Controller
@RequestMapping("/rdmCommon/bigData/")
public class RdmBigDataController {
    @Resource
    private CommonInfoManager commonInfoManager;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;
    // 大数据界面
    @RequestMapping("wwBigDataPage")
    public void wwBigDataPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String url = "http://10.15.10.104:8082/report/r_ada15-9p8vgqts-mrvo4k/";
        String shareID = "4ba8d066127a078717a27300eb97a892";
        String token = "0nDyRob9bXBk7T8EpTJt4beERbaWJ3EV";
        String jspPath = getSignedUrlWithoutParameter(url,shareID,token,"");
        response.sendRedirect(jspPath);
    }
    @RequestMapping("xwBigDataPage")
    public void xwBigDataPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String url = "http://10.15.10.104:8082/report/r_ada15-81hes6yo-kqe7yk/";
        String shareID = "cb3e20fc25d514e760ede2d79a46dadc";
        String token = "0nDyRob9bXBk7T8EpTJt4beERbaWJ3EV";
        String jspPath = getSignedUrlWithoutParameter(url,shareID,token,"");
        response.sendRedirect(jspPath);
    }
    @RequestMapping("dwBigDataPage")
    public void dwBigDataPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String url = "http://10.15.10.104:8082/report/r_ada15-pqyuntv-ke4ojm/";
        String shareID = "3d5a4d2a78e6b9e6be5ca4df9fc5e94a";
        String token = "0nDyRob9bXBk7T8EpTJt4beERbaWJ3EV";
        String jspPath = getSignedUrlWithoutParameter(url,shareID,token,"");
        response.sendRedirect(jspPath);
    }
    @RequestMapping("zwBigDataPage")
    public void zwBigDataPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String url = "http://10.15.10.104:8082/report/r_ada15-cga9ghz1-klxv9k/";
        String shareID = "4862518dd19651a4f6ce02c44bbbba27";
        String token = "0nDyRob9bXBk7T8EpTJt4beERbaWJ3EV";
        String jspPath = getSignedUrlWithoutParameter(url,shareID,token,"");
        response.sendRedirect(jspPath);
    }
    @RequestMapping("lwBigDataPage")
    public void lwBigDataPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String url = "http://10.15.10.104:8082/report/r_ada15-1c301od-kpexe2/";
        String shareID = "cc3de085c5ee0ed0f13bdbaa2662c408";
        String token = "0nDyRob9bXBk7T8EpTJt4beERbaWJ3EV";
        String jspPath = getSignedUrlWithoutParameter(url,shareID,token,"");
        response.sendRedirect(jspPath);
    }

    @RequestMapping("kjBigDataPage")
    public void kjBigDataPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String url = "http://10.15.10.104:8082/report/r_ada15-3gegdhmt-m1bxjk/";
        String shareID = "7c607c56bb049de83d09c117d7c7d188";
        String token = "0nDyRob9bXBk7T8EpTJt4beERbaWJ3EV";
        String jspPath = getSignedUrlWithoutParameter(url,shareID,token,"");
        response.sendRedirect(jspPath);
    }

    @RequestMapping("phmAllDataPage")
    public void phmAllDataPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String url = "http://10.15.10.104:8082/dashboard/";
        String shareID = "4640411495f2a9a375723bb332e943da";
        String token = "MUOJWwzsleGa5fdHuci9RvoABlBP2ggI";
        String jspPath = getSignedUrlWithoutParameter(url,shareID,token,"");
        response.sendRedirect(jspPath);
    }

    @RequestMapping("phmBigDataPage")
    public void phmBigDataPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String url = "http://10.15.10.104:8082/dashboard/";
        String shareID = "1f1824af99e7ee8592edd92e5a2435cb";
        String token = "MUOJWwzsleGa5fdHuci9RvoABlBP2ggI";

        String signedQueryParamReg = "^sugar_sign_.*";
        Map<String, String> customParams = new HashMap<>();
//        JSONObject currentUserDepInfo = xcmgProjectManager.isCurrentUserDepRespman();
//        Map<String, Object> params = new HashMap<>();
//        params.put("currentUserMainDepId", currentUserDepInfo.getString("currentUserMainDepId"));
//        JSONObject dept = commonInfoManager.queryDeptNameById(currentUserDepInfo.getString("currentUserMainDepId"));
//        String deptName = dept.getString("deptname");
        String deptName = RequestUtil.getString(request, "deptName");
        customParams.put("sugar_sign_name", deptName);
        String signParamsStr =  customParams.entrySet().stream().filter(entry -> Pattern.matches(signedQueryParamReg, entry.getKey()))
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .sorted()
                .collect(Collectors.joining("&"));
        String jspPath = getSignedUrlWithParameter(url,shareID,token,signParamsStr)+"&sugar_sign_name="+URLEncoder.encode(deptName,"UTF-8");

        response.sendRedirect(jspPath);
    }

    /**
     * 动力应用技术模块,公开加密,iframe绑定前缀
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping("PowerSystemDigitalPlatformBI")
    public void PowerSystemDigitalPlatformBI(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String url = "http://10.15.10.104:8082/dashboard/";
        String shareID = "38b45426ef961df8b3eff37525ebca31";
        String jspPath = url+shareID;
        response.sendRedirect(jspPath);
    }

    public static String getSignedUrlWithoutParameter(String url,String shareID, String token, String signParamsStr) {
            Date date = new Date();
            Long time = date.getTime();
            String stringToSign = shareID + "|" + time;
            if (!StringUtils.isEmpty(signParamsStr)) {
                stringToSign = stringToSign + "|" + signParamsStr;
            }
            String signature = HMACSHA256(stringToSign.getBytes(), token.getBytes());
            String lastUrl = url + shareID + "?_sugar_time=" + time + "&_sugar_signature=" + signature + "&" + signParamsStr;
            return lastUrl;
    }

    public static String getSignedUrlWithParameter(String url,String shareID, String token, String signParamsStr) {
        Date date = new Date();
        Long time = date.getTime();
        String stringToSign = shareID + "|" + time;
        if (!StringUtils.isEmpty(signParamsStr)) {
            stringToSign = stringToSign + "|" + signParamsStr;
        }
        String signature = HMACSHA256(stringToSign.getBytes(), token.getBytes());
        String lastUrl = url + shareID + "?_sugar_time=" + time + "&_sugar_signature=" + signature;
        return lastUrl;
    }
    /**
     *  使用java原生的摘要实现SHA256加密。
     * @return
     */
    public static String HMACSHA256(byte[] data, byte[] key) {
        try  {
            SecretKeySpec signingKey = new SecretKeySpec(key, "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(signingKey);
            return URLEncoder.encode(byte2Base64(mac.doFinal(data)),"UTF-8");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
        return null;
    }
    private static String byte2Base64(byte[] bytes){
        return Base64.encodeBase64String(bytes);
    }
}
