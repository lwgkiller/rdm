package com.redxun.xcmgTdm.core.controller;

import java.io.*;
import java.net.URLEncoder;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.standardManager.core.manager.StandardMessageManager;
import com.redxun.sys.core.util.SysPropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.service.ActInstService;
import com.redxun.bpm.core.entity.BpmInst;
import com.redxun.bpm.core.entity.BpmTask;
import com.redxun.bpm.core.entity.TaskExecutor;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.bpm.core.manager.BpmTaskManager;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.CookieUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.sys.core.entity.SysDic;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.xcmgTdm.core.service.TdmRequestService;

@RestController
@RequestMapping("/xcmgTdm/core/requestapi/")
public class TdmRequestController {
    private static final Logger logger = LoggerFactory.getLogger(TdmRequestController.class);
    @Autowired
    ActInstService actInstService;
    @Autowired
    BpmInstManager bpmInstManager;
    @Autowired
    BpmTaskManager bpmTaskManager;
    @Autowired
    SysDicManager sysDicManager;
    @Autowired
    TdmRequestService tdmRequestService;
    @Autowired
    StandardMessageManager standardMessageManager;

    // 测一下普通request，放着别理o(￣ヘ￣o＃)
    @RequestMapping("test")
    public void test(HttpServletRequest request, HttpServletResponse response) throws Exception {
        System.out.println("");
        //standardMessageManager.whatTheHellItIs();
        /*        ResponseCookie cookies = ResponseCookie.from(name, value) // key & value
                .httpOnly(true) // 禁止js读取
                .secure(true) // 在http下也传输
                .path("/") // path
                .maxAge(time(selectExpires)) // 1个小时候过期
                .sameSite("None") // 大多数情况也是不发送第三方 Cookie，但是导航到目标网址的 Get 请求除外
                .build();
        response.setHeader(HttpHeaders.SET_COOKIE, cookies.toString());*/
//        String testV = CookieUtil.getValueByName("SameSite", request);
//        CookieUtil.addCookie("jumpLenovo", "rdm", true, request, response);
//        response.sendRedirect("http://10.15.10.121/");

        // String ip = request.getHeader("X-Forwarded-For");
        // if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
        // if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
        // ip = request.getHeader("Proxy-Client-IP");
        // }
        // if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
        // ip = request.getHeader("WL-Proxy-Client-IP");
        // }
        // if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
        // ip = request.getHeader("HTTP_CLIENT_IP");
        // }
        // if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
        // ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        // }
        // if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
        // ip = request.getRemoteAddr();
        // }
        // } else if (ip.length() > 15) {
        // String[] ips = ip.split(",");
        // for (int index = 0; index < ips.length; index++) {
        // String strIp = (String) ips[index];
        // if (!("unknown".equalsIgnoreCase(strIp))) {
        // ip = strIp;
        // break;
        // }
        // }
        // }
        // 一下测试单点
        // String userid = ContextUtil.getCurrentUser().getUserNo();
        // String urlcut = TDMUrlStatus.DEFAULT;
        // String url = "http://192.168.1.116:4200/#/sso/" + urlcut + "?userid=" + userid;
        // response.sendRedirect(url);
        // System.out.println("");
    }

    /**
     * TDM调用RDM传递质量问题并发起改进流程
     *
     * @param jsonObject
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "zlgjCreate", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject zlgjCreate(@RequestBody JSONObject jsonObject) throws Exception {
        JSONObject resultJson = new JSONObject();
        resultJson.put("result", "success");
        resultJson.put("message", "操作成功！");
        try {
            tdmRequestService.doZlgj(jsonObject, resultJson);
            return resultJson;
        } catch (Exception e) {
            resultJson.put("result", "fail");
            resultJson.put("message", "操作失败，系统异常！错误信息：" + e.getMessage());
            return resultJson;
        }
    }

    /**
     * @param contents:文件流
     * @param filePath:文件名称全路径
     */
    public static void byteToFile(byte[] contents, String filePath) {
        BufferedInputStream bufferedInputStream = null;
        FileOutputStream fileOutputStream = null;
        BufferedOutputStream bufferedOutputStream = null;
        try {
            ByteArrayInputStream byteInputStream = new ByteArrayInputStream(contents);
            bufferedInputStream = new BufferedInputStream(byteInputStream);
            File file = new File(filePath);// 获取文件的父路径字符串
            File path = file.getParentFile();
            if (!path.exists()) {
                logger.info("文件夹不存在，创建。path={}", path);
                boolean isCreated = path.mkdirs();
                if (!isCreated) {
                    logger.error("创建文件夹失败，path={}", path);
                }
            }
            fileOutputStream = new FileOutputStream(file);// 实例化OutputString 对象
            bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
            byte[] buffer = new byte[1024];
            int length = bufferedInputStream.read(buffer);
            while (length != -1) {
                bufferedOutputStream.write(buffer, 0, length);
                length = bufferedInputStream.read(buffer);
            }
            bufferedOutputStream.flush();
        } catch (Exception e) {
            logger.error("输出文件流时抛异常，filePath={}", filePath, e);
        } finally {
            try {
                bufferedInputStream.close();
                fileOutputStream.close();
                bufferedOutputStream.close();
            } catch (IOException e0) {
                logger.error("文件处理失败，filePath={}", filePath, e0);
            }
        }
    }

    /**
     * @param jsonObject
     * @return
     */
    @RequestMapping(value = "sendDingDing", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject sendDingDing(@RequestBody JSONObject jsonObject) {
        JSONObject resultJson = new JSONObject();
        resultJson.put("result", "success");
        resultJson.put("message", "操作成功！");
        String tdmSendDingDing = SysPropertiesUtil.getGlobalProperty("tdmSendDingDing");
        if ("on".equalsIgnoreCase(tdmSendDingDing)) {
            try {
                tdmRequestService.sendDingDing(jsonObject, resultJson);
                return resultJson;
            } catch (Exception e) {
                resultJson.put("result", "fail");
                resultJson.put("message", "操作失败，系统异常！错误信息：" + e.getMessage());
                return resultJson;
            }
        } else {
            resultJson.put("result", "fail");
            resultJson.put("message", "发送开关关闭！");
            return resultJson;
        }
    }

    // 测一下普通request，激活等待任务
    @RequestMapping("testorderTdmAct")
    public JsonResult testorderTdmApproval(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            // 流程实例id
            String bpminstId = RequestUtil.getString(request, "bpminstId");
            BpmInst bpmInst = bpmInstManager.get(bpminstId);
            String executionId = bpmInst.getActInstId();
            // "AGREE","REFUSE"
            String jumpType = RequestUtil.getString(request, "jumpType");
            // 备注信息
            String opinion = RequestUtil.getString(request, "opinion");
            actInstService.signal(executionId, jumpType, opinion);
            return new JsonResult(true, "成功！");
        } catch (Exception e) {
            return new JsonResult(false, "失败！");
        }
    }

    // 获取某流程实例id对应的活动任务id
    @RequestMapping("getTaskId")
    @ResponseBody
    public String getTaskId(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String instid = request.getParameter("id");
        BpmInst bpmInst = bpmInstManager.get(instid);
        List<BpmTask> bpmTaskList = bpmTaskManager.getByInstId(bpmInst.getInstId());
        for (BpmTask bpmTask : bpmTaskList) {
            if (bpmTask.getLocked() == 0) {
                return bpmTask.getId();
            }
        }
        return "";
    }

    // 获取某业务id对应的流程实例id
    @RequestMapping("getInstId")
    @ResponseBody
    public String getInstId(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String pkId = request.getParameter("id");
        BpmInst bpmInst = bpmInstManager.getByBusKey(pkId);
        return bpmInst.getInstId();
    }

    // 判断当前账户和输入账户是否一致
    @RequestMapping("currentUserCheck")
    @ResponseBody
    public Boolean currentUserCheck(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String userid = request.getParameter("id");
        if (userid.equals(ContextUtil.getCurrentUser().getUserId())) {
            return true;
        } else {
            return false;
        }
    }

    // 判断当前账户是否是某流程实例id对应的任务id对应的执行人
    @RequestMapping("currentInstUserCheck")
    @ResponseBody
    public Boolean getTaskUserid(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String instid = request.getParameter("id");
        BpmInst bpmInst = bpmInstManager.get(instid);
        List<BpmTask> bpmTaskList = bpmTaskManager.getByInstId(bpmInst.getInstId());
        for (BpmTask bpmTask : bpmTaskList) {
            if (bpmTask.getLocked() == 0) {
                Set<TaskExecutor> taskExecutors = bpmTaskManager.getTaskHandlerUsersIds(bpmTask.getId());
                for (TaskExecutor taskExecutor : taskExecutors) {
                    if (taskExecutor.getId().equals(ContextUtil.getCurrentUser().getUserId())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // TDM单点登录
    @RequestMapping("tdmSso")
    @ResponseBody
    public void tdmSso(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String userid = ContextUtil.getCurrentUser().getUserNo();
        String urlcut = RequestUtil.getString(request, "urlcut");
        String ip = request.getRemoteAddr();
        String[] ips = ip.split("\\.");
        String url = "";
        List<SysDic> sysDicList = sysDicManager.getByTreeKey("TDMIpClass");
        if (ips.length > 3) {// ipv4
            if (Integer.parseInt(ips[2]) > 3 && Integer.parseInt(ips[2]) < 8) {
                url = sysDicList.get(0).getValue() + "/#/app/custom/sso/";
            } else if (ip.equalsIgnoreCase("127.0.0.1")) {
                url = sysDicList.get(1).getValue() + "/#/app/custom/sso/";
            } else {
                url = sysDicList.get(1).getValue() + "/#/app/custom/sso/";
            }
            String urls = URLEncoder.encode(urlcut, "UTF-8");
            url = url + urls + "?userid=" + userid;
            response.sendRedirect(url);
        } else {// ipv6
            url = sysDicList.get(1).getValue() + "/#/app/custom/sso/";
            String urls = URLEncoder.encode(urlcut, "UTF-8");
            url = url + urls + "?userid=" + userid;
            response.sendRedirect(url);
        }
    }

    // TDM代办链接获取
    @RequestMapping("getTdmTodoUrl")
    @ResponseBody
    public JsonResult getTdmTodoUrl(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JsonResult result = new JsonResult(true, "操作成功！");
        try {
            String tdmBusinessId = RequestUtil.getString(request, "tdmBusinessId");
            String userid = ContextUtil.getCurrentUser().getUserNo();
            String ip = request.getRemoteAddr();
            String[] ips = ip.split("\\.");
            String url = "";
            List<SysDic> sysDicList = sysDicManager.getByTreeKey("TDMIpClass");
            if (ips.length > 3) {// ipv4
                if (Integer.parseInt(ips[2]) > 3 && Integer.parseInt(ips[2]) < 8) {
                    url = sysDicList.get(0).getValue() + "/#/custom/task-detail/";
                } else if (ip.equalsIgnoreCase("127.0.0.1")) {
                    url = sysDicList.get(1).getValue() + "/#/custom/task-detail/";
                } else {
                    url = sysDicList.get(1).getValue() + "/#/custom/task-detail/";
                }
                url = url + userid + "/" + tdmBusinessId;
            } else {// ipv6
                url = sysDicList.get(1).getValue() + "/#/custom/task-detail/";
                url = url + userid + "/" + tdmBusinessId;
            }
            result.setData(url);
            return result;
        } catch (Exception e) {
            logger.error("Exception in getTdmTodoUrl", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    // TDM消息代办链接获取
    @RequestMapping("getTdmMsgTodoUrl")
    @ResponseBody
    public JsonResult getTdmMsgTodoUrl(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JsonResult result = new JsonResult(true, "操作成功！");
        try {
            String tdmBusinessId = RequestUtil.getString(request, "tdmBusinessId");
            String userid = ContextUtil.getCurrentUser().getUserNo();
            String ip = request.getRemoteAddr();
            String[] ips = ip.split("\\.");
            String url = "";
            List<SysDic> sysDicList = sysDicManager.getByTreeKey("TDMIpClass");
            if (ips.length > 3) {// ipv4
                if (Integer.parseInt(ips[2]) > 3 && Integer.parseInt(ips[2]) < 8) {
                    url = sysDicList.get(0).getValue() + "/#/custom/task-detail/";
                } else if (ip.equalsIgnoreCase("127.0.0.1")) {
                    url = sysDicList.get(1).getValue() + "/#/custom/task-detail/";
                } else {
                    url = sysDicList.get(1).getValue() + "/#/custom/task-detail/";
                }
                url = url + userid + "/" + tdmBusinessId;
            } else {// ipv6
                url = sysDicList.get(1).getValue() + "/#/custom/task-detail/";
                url = url + userid + "/" + tdmBusinessId;
            }
            result.setData(url);
            return result;
        } catch (Exception e) {
            logger.error("Exception in getTdmMsgTodoUrl", e);
            return new JsonResult(false, e.getMessage());
        }
    }

    //TDM获取负责或者参与的运行中的科技项目列表
    @RequestMapping(value = "getProjectList")
    @ResponseBody
    public JSONObject getProjectList(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        String userNo = RequestUtil.getString(request, "userNo", "");
        if (StringUtils.isBlank(userNo)) {
            result.put("code", HttpStatus.SC_BAD_REQUEST);
            result.put("message", "TDM账户名为空");
            return result;
        }
        tdmRequestService.getProjectList(userNo, result);
        return result;
    }
}
