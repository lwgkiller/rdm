package com.redxun.xcmgTdm.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.service.ActInstService;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.*;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.bpm.core.manager.BpmSolutionManager;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.ExceptionUtil;
import com.redxun.core.util.HttpClientUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.org.api.model.IUser;
import com.redxun.org.api.service.UserService;
import com.redxun.rdmCommon.core.dao.PdmApiDao;
import com.redxun.rdmCommon.core.dao.RdmDao;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmCommon.core.manager.PdmApiManager;
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
import com.redxun.rdmCommon.core.util.StringProcessUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.serviceEngineering.core.dao.MaintainabilityDisassemblyProposalDao;
import com.redxun.serviceEngineering.core.service.MaintainabilityDisassemblyProposalService;
import com.redxun.sys.core.entity.SysDic;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.sys.core.util.SysPropertiesUtil;
import com.redxun.sys.org.dao.OsGroupDao;
import com.redxun.sys.org.entity.OsGroup;
import com.redxun.sys.webreq.manager.SysWebReqDefManager;
import com.redxun.zlgjNPI.core.dao.ZlgjWTDao;
import com.redxun.zlgjNPI.core.manager.ZlgjWTService;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import sun.misc.BASE64Decoder;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;

@Service
public class TdmRequestService {
    private static Logger logger = LoggerFactory.getLogger(TdmRequestService.class);
    @Resource
    private CommonInfoManager commonInfoManager;
    @Autowired
    private SysWebReqDefManager sysWebReqDefManager;
    @Autowired
    SysDicManager sysDicManager;
    @Autowired
    MaintainabilityDisassemblyProposalService maintainabilityDisassemblyProposalService;
    @Autowired
    MaintainabilityDisassemblyProposalDao maintainabilityDisassemblyProposalDao;
    @Autowired
    ZlgjWTDao zlgjWTDao;
    @Autowired
    UserService userService;
    @Autowired
    BpmInstManager bpmInstManager;
    @Autowired
    ActInstService actInstService;
    @Autowired
    private RdmDao rdmDao;
    @Autowired
    private OsGroupDao groupDao;
    @Resource
    private BpmSolutionManager bpmSolutionManager;
    @Autowired
    private SendDDNoticeManager sendDDNoticeManager;
    @Autowired
    private PdmApiDao pdmApiDao;
    @Autowired
    private PdmApiManager pdmApiManager;

    // TDM待办信息加工
    public List<BpmTask> getTasksTodoByUserId(String userNo, String ip, String descValue) throws Exception {
        List<BpmTask> needTodoTasks = new ArrayList<>();
        try {
            String[] ips = ip.split("\\.");
            String url = "";
            String userid = ContextUtil.getCurrentUser().getUserNo();
            List<SysDic> sysDicList = sysDicManager.getByTreeKey("TDMIpClass");
            if (ips.length > 3) {// ipv4
                if (Integer.parseInt(ips[2]) > 3 && Integer.parseInt(ips[2]) < 8) {
                    url = sysDicList.get(0).getValue() + ":8080/pvdm/server/DataReceiver/taskList";
                } else if (ip.equalsIgnoreCase("127.0.0.1")) {
                    url = sysDicList.get(1).getValue() + ":8080/pvdm/server/DataReceiver/taskList";
                } else {
                    url = sysDicList.get(1).getValue() + ":8080/pvdm/server/DataReceiver/taskList";
                }
                url = url + "?userid=" + userid;
            } else {// ipv6
                url = sysDicList.get(1).getValue() + ":8080/pvdm/server/DataReceiver/taskList";
                url = url + "?userid=" + userid;
            }
            String resultString = HttpClientUtil.getFromUrl(url, null);
            if (StringUtil.isNotEmpty(resultString)) {
                JSONObject resultJson = JSONObject.parseObject(resultString);
                JSONArray resultJsonArray = resultJson.getJSONArray("Data");
                for (Object object : resultJsonArray) {
                    JSONObject jsonObject = (JSONObject) object;
                    String desc = jsonObject.getString("desc");
                    String instanceCaption = jsonObject.getString("instanceCaption");
                    String description = "【TDM】:" + instanceCaption + "-" + desc;
                    if (!description.contains(descValue)) {
                        continue;
                    }
                    String name = jsonObject.getString("name");
                    String tdmBusinessId = jsonObject.getString("taskId");
                    BpmTask task = new BpmTask();
                    task.setDescription(description);
                    task.setName(name);
                    task.setSolKey("TdmTodo");
                    task.setId(tdmBusinessId);
                    needTodoTasks.add(task);
                }
            } else {
                logger.error("TDM拉取待办失败");
            }
        } catch (Exception e) {
            logger.error("TDM拉取待办失败", e);
        }
        return needTodoTasks;
    }

    // TDM告警信息
    public List<BpmTask> getMsgTasksTodoByUserId(String userNo, String ip, String descValue) throws Exception {
        List<BpmTask> needTodoTasks = new ArrayList<>();
        try {
            String[] ips = ip.split("\\.");
            String url = "";
            String userid = ContextUtil.getCurrentUser().getUserNo();
            List<SysDic> sysDicList = sysDicManager.getByTreeKey("TDMIpClass");
            if (ips.length > 3) {// ipv4
                if (Integer.parseInt(ips[2]) > 3 && Integer.parseInt(ips[2]) < 8) {
                    url = sysDicList.get(0).getValue() + ":8080/pvdm/server/DataReceiver/earlyWarningList";
                } else if (ip.equalsIgnoreCase("127.0.0.1")) {
                    url = sysDicList.get(1).getValue() + ":8080/pvdm/server/DataReceiver/earlyWarningList";
                } else {
                    url = sysDicList.get(1).getValue() + ":8080/pvdm/server/DataReceiver/earlyWarningList";
                }
                url = url + "?userid=" + userid;
            } else {// ipv6
                url = sysDicList.get(1).getValue() + ":8080/pvdm/server/DataReceiver/earlyWarningList";
                url = url + "?userid=" + userid;
            }
            String resultString = HttpClientUtil.getFromUrl(url, null);
            if (StringUtil.isNotEmpty(resultString)) {
                JSONObject resultJson = JSONObject.parseObject(resultString);
                JSONArray resultJsonArray = resultJson.getJSONArray("Data");
                for (Object object : resultJsonArray) {
                    JSONObject jsonObject = (JSONObject) object;
                    String desc = jsonObject.getString("desc");
                    String name = jsonObject.getString("name");
                    String tdmBusinessId = jsonObject.getString("taskId");
                    String description = "【TDM】:" + desc;
                    if (!description.contains(descValue)) {
                        continue;
                    }
                    BpmTask task = new BpmTask();
                    task.setDescription(description);
                    task.setName(name);
                    task.setSolKey("TdmMsgTodo");
                    task.setId(tdmBusinessId);
                    needTodoTasks.add(task);
                }
            }
        } catch (Exception e) {
            logger.error("TDM拉取告警失败", e);
        }
        return needTodoTasks;
    }

    // 创建业务，启流程草稿测试
    public void doFuck(JSONObject jsonObject) throws Exception {
        try {
            List<LinkedHashMap> linkedHashMapList = (ArrayList) jsonObject.get("data");
            for (LinkedHashMap linkedHashMap : linkedHashMapList) {
                // 拼凑表单数据
                JSONObject businessTest = new JSONObject();
                businessTest.put("businessStatus", "A-editing");
                businessTest.put("editorUserId", '1');
                businessTest.put("remark", linkedHashMap.get("T_FAULTID").toString());
                // 启动草稿
                IUser user = userService.getByUsername("admin");
                ContextUtil.setCurUser(user);

                ProcessStartCmd startCmd = new ProcessStartCmd();
                startCmd.setSolId("1084163905747828758");
                startCmd.setJsonData(businessTest.toJSONString());
                // BpmInst bpmInst = bpmInstManager.doSaveDraft(startCmd);
                BpmInst bpmInst = bpmInstManager.doStartProcess(startCmd);
                // 生成文件
                String filePathBase = sysDicManager
                        .getBySysTreeKeyAndDicKey("serviceEngineeringUploadPosition", "disassemblyProposal").getValue();
                String businessId = startCmd.getBusinessKey();
                String id = IdUtil.getId();
                String fileName = linkedHashMap.get("T_FILENAME").toString();
                // 向下载目录中写入文件
                String filePath = filePathBase + File.separator + businessId;
                File pathFile = new File(filePath);
                if (!pathFile.exists()) {
                    pathFile.mkdirs();
                }
                String suffix = CommonFuns.toGetFileSuffix(fileName);
                String fileFullPath = filePath + File.separator + id + "." + suffix;
                File file = new File(fileFullPath);
                // --图片格式编码--
                BASE64Decoder decoder = new BASE64Decoder();
                byte[] bytes;
                bytes = decoder.decodeBuffer(linkedHashMap.get("T_FILE").toString());
                // --------------
                FileCopyUtils.copy(bytes, file);
                // 写入数据库
                JSONObject fileInfo = new JSONObject();
                fileInfo.put("id", id);
                fileInfo.put("fileName", fileName);
                fileInfo.put("fileSize", file.length());
                fileInfo.put("businessId", businessId);
                fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                fileInfo.put("CREATE_TIME_", new Date());
                maintainabilityDisassemblyProposalDao.addFileInfos(fileInfo);
                // HttpClientUtil.postJson("", businessTest.toJSONString());
                // int i = 1 / 0;
            }
        } catch (Exception E) {
            logger.error("创建失败：" + E.getMessage());
            throw E;
        }
    }

    // 创建质量问题，启动流程测试
    public void doZlgj(JSONObject jsonObject, JSONObject resultJson) throws Exception {
        try {
            // 1、必填项正确性校验
            JSONArray zlgjWtArr = jsonObject.getJSONArray("data");
            if (zlgjWtArr == null || zlgjWtArr.isEmpty()) {
                resultJson.put("result", "fail");
                resultJson.put("message", "操作失败，请求信息为空！");
                return;
            }
            for (int index = 0; index < zlgjWtArr.size(); index++) {
                JSONObject oneWtObj = zlgjWtArr.getJSONObject(index);
                logger.error("TDM向RDM创建流程开始，T_FAULTID：" + oneWtObj.getString("T_FAULTID"));
                String jjcd = oneWtObj.getString("T_FEEDBACK");
                if (StringUtils.isBlank(jjcd)) {
                    jjcd = "一般";
                }
                //..2023-10-31做一下映射
                String jiXing = this.toGetJiXing(oneWtObj.getString("T_PRODUCTTYPE"));
                if (StringUtils.isBlank(jiXing)) {
                    resultJson.put("result", "fail");
                    resultJson.put("message", "操作失败，T_PRODUCTTYPE(产品类型)为空或RDM中无对应！");
                    return;
                }
                String smallJiXing = oneWtObj.getString("T_DESIGNTYPE");
                if (StringUtils.isBlank(smallJiXing)) {
                    resultJson.put("result", "fail");
                    resultJson.put("message", "操作失败，T_DESIGNTYPE(设计型号)为空！");
                    return;
                }
                String wtms = oneWtObj.getString("T_DESCRIBE");
                if (StringUtils.isBlank(wtms)) {
                    resultJson.put("result", "fail");
                    resultJson.put("message", "操作失败，T_DESCRIBE(故障详细说明)为空！");
                    return;
                }
                String zjbh = oneWtObj.getString("T_MACHINENUMBER");
                if (StringUtils.isBlank(zjbh)) {
                    resultJson.put("result", "fail");
                    resultJson.put("message", "操作失败，T_MACHINENUMBER(整机编号)为空！");
                    return;
                }
                String gzxs = oneWtObj.getString("T_METERTIME");
                if (StringUtils.isBlank(gzxs)) {
                    resultJson.put("result", "fail");
                    resultJson.put("message", "操作失败，T_METERTIME(仪表时间)为空！");
                    return;
                }
                // 验证发起人
                String createrUserNo = oneWtObj.getString("T_REQUESTPERSON");
                if (StringUtils.isBlank(createrUserNo)) {
                    resultJson.put("result", "fail");
                    resultJson.put("message", "操作失败，T_REQUESTPERSON(测试工程师)为空！");
                    return;
                }
                JSONObject param = new JSONObject();
                param.put("userNo", createrUserNo);
                List<JSONObject> creatorList = rdmDao.queryInJobUserByNo(param);
                if (creatorList == null || creatorList.isEmpty() || creatorList.size() != 1) {
                    resultJson.put("result", "fail");
                    resultJson.put("message", "操作失败，T_REQUESTPERSON(测试工程师)在RDM中无对应或对应多个用户！");
                    return;
                }
                IUser user = userService.getByUsername(createrUserNo);
                String creatorId = creatorList.get(0).getString("USER_ID_");
                // 验证产品主管
                String zrcpzgUserNo = oneWtObj.getString("T_PRODUCTSUPERVISOR");
                if (StringUtils.isBlank(zrcpzgUserNo)) {
                    resultJson.put("result", "fail");
                    resultJson.put("message", "操作失败，T_PRODUCTSUPERVISOR(产品主管)为空！");
                    return;
                }
                param.put("userNo", zrcpzgUserNo);
                List<JSONObject> zrcpzgList = rdmDao.queryInJobUserByNo(param);
                if (zrcpzgList == null || zrcpzgList.isEmpty() || zrcpzgList.size() != 1) {
                    resultJson.put("result", "fail");
                    resultJson.put("message", "操作失败，T_PRODUCTSUPERVISOR(产品主管)在RDM中无对应或对应多个用户！");
                    return;
                }
                String zrcpzgId = zrcpzgList.get(0).getString("USER_ID_");
                String zrcpzgName = zrcpzgList.get(0).getString("FULLNAME_");
                // 产品主管所在部门id
                OsGroup group = groupDao.getMainGroupByUserId(zrcpzgId);
                String ssbmId = group.getGroupId();
                String ssbmName = group.getName();
                if (StringUtils.isBlank(ssbmId)) {
                    resultJson.put("result", "fail");
                    resultJson.put("message", "操作失败，T_PRODUCTSUPERVISOR(产品主管)在RDM中无对应部门！");
                    return;
                }
                String sggk = oneWtObj.getString("T_WORKINGCONDITION");
                if (StringUtils.isBlank(sggk)) {
                    resultJson.put("result", "fail");
                    resultJson.put("message", "操作失败，T_WORKINGCONDITION(施工工况)为空！");
                    return;
                }
                String lbjgys = oneWtObj.getString("T_SUPPLIER");
                if (StringUtils.isBlank(lbjgys)) {
                    resultJson.put("result", "fail");
                    resultJson.put("message", "操作失败，T_SUPPLIER(故障零部件供应商)为空！");
                    return;
                }
                String gzxt = this.toGetGzxtCodeByName(oneWtObj.getString("T_FAULTSYSTEM"));
                if (StringUtils.isBlank(gzxt)) {
                    resultJson.put("result", "fail");
                    resultJson.put("message", "操作失败，T_FAULTSYSTEM(faultSystem)为空或RDM中无对应！");
                    return;
                }
                String gzbw = oneWtObj.getString("T_FAULTPARTS");
                if (StringUtils.isBlank(gzbw)) {
                    resultJson.put("result", "fail");
                    resultJson.put("message", "操作失败，T_FAULTPARTS(故障部位)为空！");
                    return;
                }
                String gzlj = oneWtObj.getString("T_DEFECTIVEPARTS");
                if (StringUtils.isBlank(gzlj)) {
                    resultJson.put("result", "fail");
                    resultJson.put("message", "操作失败，T_DEFECTIVEPARTS(故障零件)为空！");
                    return;
                }
                String wtpcjc = oneWtObj.getString("T_CHECKMETHOD");
                if (StringUtils.isBlank(wtpcjc)) {
                    resultJson.put("result", "fail");
                    resultJson.put("message", "操作失败，T_CHECKMETHOD(问题排查过程及检测方法)为空！");
                    return;
                }
                String xcczfa = oneWtObj.getString("T_HANDLEMETHOD");
                if (StringUtils.isBlank(xcczfa)) {
                    resultJson.put("result", "fail");
                    resultJson.put("message", "操作失败，T_HANDLEMETHOD(现场处置方式)为空！");
                    return;
                }
                String gjyq = oneWtObj.getString("T_HANDLEDEMAND");
                if (StringUtils.isBlank(gjyq)) {
                    resultJson.put("result", "fail");
                    resultJson.put("message", "操作失败，T_HANDLEDEMAND(问题处理要求)为空！");
                    return;
                }
                String yzcd = oneWtObj.getString("T_FAULTLEVEL");
                if (!Arrays.asList("A", "B", "C", "D", "W").contains(yzcd)) {
                    resultJson.put("result", "fail");
                    resultJson.put("message", "操作失败，T_FAULTLEVEL(故障等级)为空或RDM中无对应！");
                    return;
                }
                // 故障现象:T_FAULTSYMPTOMS->gzProgram todo:追加！！！
                String gzProgram = oneWtObj.getString("T_FAULTSYMPTOMS");
                if (StringUtils.isBlank(gzProgram)) {
                    resultJson.put("result", "fail");
                    resultJson.put("message", "操作失败，T_FAULTSYMPTOMS(故障现象)为空！");
                    return;
                }
                String tdmSylx = oneWtObj.getString("T_TESTTYPE");
                if (StringUtils.isBlank(tdmSylx)) {
                    resultJson.put("result", "fail");
                    resultJson.put("message", "操作失败，T_TESTTYPE(试验类型)为空！");
                    return;
                }
                // 排放标准:T_EMISSIONSTANDARD->pfbz todo:追加！！！
                String pfbz = this.toGetPfbz(oneWtObj.getString("T_EMISSIONSTANDARD"));
                if (StringUtils.isBlank(pfbz)) {
                    resultJson.put("result", "fail");
                    resultJson.put("message", "操作失败，T_EMISSIONSTANDARD(排放标准)为空！");
                    return;
                }
                // 改进方法:T_IMPROVEMENTMETHOD->improvementMethod todo:追加！！！
                String improvementMethod = oneWtObj.getString("T_IMPROVEMENTMETHOD");
                if (StringUtils.isBlank(improvementMethod)) {
                    resultJson.put("result", "fail");
                    resultJson.put("message", "操作失败，T_IMPROVEMENTMETHOD(改进方法)为空！");
                    return;
                }
                // 查找质量改进的solution
                BpmSolution bpmSol = bpmSolutionManager.getByKey("ZLGJ", "1");
                String solId = "";
                if (bpmSol != null) {
                    solId = bpmSol.getSolId();
                }

                // 2、拼接表单数据
                JSONObject businessTest = new JSONObject();
                businessTest.put("wtlx", "XPZDSY");// 问题类型:"XPZDSY"->wtlx todo:默认"XPZDSY"
                businessTest.put("jjcd", jjcd);// 问题响应要求:T_FEEDBACK->jjcd todo:如果为空默认"一般"
                businessTest.put("jiXing", jiXing);// 产品类别:T_PRODUCTTYPE->jiXing
                businessTest.put("smallJiXing", smallJiXing);// 机型/图号:T_DESIGNTYPE->smallJiXing
                businessTest.put("wtms", wtms);// 问题详细描述:T_DESCRIBE->wtms
                businessTest.put("zjbh", zjbh);// 整机编号:T_MACHINENUMBER->zjbh
                businessTest.put("gzxs", gzxs);// 工作小时:T_METERTIME->gzxs
                businessTest.put("CREATE_BY_", creatorId);// 发起人:T_REQUESTPERSON->CREATE_BY_
                businessTest.put("zrcpzgId", zrcpzgId);// 第一责任人:T_PRODUCTSUPERVISOR->zrcpzgId,zrcpzgName
                businessTest.put("zrcpzgName", zrcpzgName);// 第一责任人:T_PRODUCTSUPERVISOR->zrcpzgId,zrcpzgName
                businessTest.put("ssbmId", ssbmId);// 责任部门:T_PRODUCTSUPERVISOR->ssbmId,ssbmName
                businessTest.put("ssbmName", ssbmName);// 责任部门:T_PRODUCTSUPERVISOR->ssbmId,ssbmName
                // businessTest.put("zrrId", "");rdm自动生成
                // businessTest.put("zrrName", "");rdm自动生成
                businessTest.put("sggk", sggk);// 施工工况:T_WORKINGCONDITION->sggk
                businessTest.put("lbjgys", lbjgys);// 零部件供应商:T_SUPPLIER->lbjgys
                businessTest.put("jjsl", "1");// 交机数量/零部件数量:"1"->jjsl todo:默认值"1"
                businessTest.put("gzsl", "1");// 故障数量:"1"->gzsl todo:默认值"1"
                businessTest.put("gzxt", gzxt);// 故障系统:T_FAULTSYSTEM->gzxt
                businessTest.put("gzbw", gzbw);// 故障部件:T_FAULTPARTS->gzbw
                businessTest.put("gzlj", gzlj);// 故障零件:T_DEFECTIVEPARTS->gzlj
                businessTest.put("wtpcjc", wtpcjc);// 问题排查过程及检测方法:T_CHECKMETHOD->wtpcjc
                businessTest.put("xcczfa", xcczfa);// 现场处置方法:T_HANDLEMETHOD->xcczfa
                businessTest.put("gjyq", gjyq);// 改进要求:T_HANDLEDEMAND->gjyq
                businessTest.put("yzcd", yzcd.equalsIgnoreCase("D") ? "W" : yzcd);// 问题严重程度:T_FAULTLEVEL->yzcd
                businessTest.put("gzProgram", gzProgram);// 故障现象:T_FAULTSYMPTOMS->gzProgram todo:追加！！！
                businessTest.put("tdmSylx", tdmSylx);// 试验类型:T_TESTTYPE->tdmSylx
                businessTest.put("pfbz", pfbz);// 排放标准:T_EMISSIONSTANDARD->pfbz todo:追加！！！
                businessTest.put("gzl", "100%");// 故障率:"100%"->gzl todo:默认值"100%"
                businessTest.put("improvementMethod", improvementMethod);// 改进方法:T_IMPROVEMENTMETHOD->improvementMethod
                // todo:追加！！！

                // 3、启动流程
                JsonResult processStartResult = startProcess(solId, businessTest, user);
                if (!processStartResult.getSuccess()) {
                    resultJson.put("result", "fail");
                    resultJson.put("message", "操作失败，流程启动异常！");
                    return;
                }
                String wtId = processStartResult.getData().toString();

                // 4、如果存在附件，则写入数据库、磁盘
                String filePathBase = WebAppUtil.getProperty("zlgjFilePathBase");
                String filePathBase_view = WebAppUtil.getProperty("zlgjFilePathBase_preview");
                JSONArray fileList = oneWtObj.getJSONArray("T_FILElIST");
                if (fileList != null && !fileList.isEmpty()) {
                    for (int fileIndex = 0; fileIndex < fileList.size(); fileIndex++) {
                        JSONObject oneFile = fileList.getJSONObject(fileIndex);
                        String id = IdUtil.getId();
                        // 写入数据库
                        Map<String, Object> fileInfo = new HashMap<>();
                        fileInfo.put("id", id);
                        fileInfo.put("fileName", oneFile.getString("fileName"));
                        fileInfo.put("fileSize", oneFile.getString("fileSize"));
                        fileInfo.put("wtId", wtId);
                        fileInfo.put("fjlx", "gztp");
                        fileInfo.put("CREATE_BY_", creatorId);
                        zlgjWTDao.addFileInfos(fileInfo);

                        // 向下载目录中写入文件
                        String filePath = filePathBase + File.separator + wtId;
                        File pathFile = new File(filePath);
                        if (!pathFile.exists()) {
                            pathFile.mkdirs();
                        }
                        String suffix = CommonFuns.toGetFileSuffix(oneFile.getString("fileName"));
                        String fileFullPath = filePath + File.separator + id + "." + suffix;
                        File file = new File(fileFullPath);
                        // --图片格式编码--
                        BASE64Decoder decoder = new BASE64Decoder();
                        byte[] fileContent = decoder.decodeBuffer(oneFile.getString("fileContent"));
                        FileCopyUtils.copy(fileContent, file);
                        // 向预览目录中写入文件
                        String filePath_view = filePathBase_view + File.separator + wtId;
                        File pathFile_view = new File(filePath_view);
                        if (!pathFile_view.exists()) {
                            pathFile_view.mkdirs();
                        }
                        String fileFullPath_view = filePath_view + File.separator + id + "." + suffix;
                        File file_view = new File(fileFullPath_view);
                        FileCopyUtils.copy(fileContent, file_view);
                    }
                }

                // 5、将对应信息写入zlgj_wtsb_outsystem
                param.clear();
                param.put("id", IdUtil.getId());
                param.put("busKeyId", wtId);
                param.put("outSystem", "tdm");
                JSONObject outSystemJson = new JSONObject();
                outSystemJson.put("T_CLASSNAME", oneWtObj.getString("T_CLASSNAME"));
                outSystemJson.put("T_FAULTID", oneWtObj.getString("T_FAULTID"));
                param.put("outSystemJson", outSystemJson.toJSONString());
                param.put("CREATE_BY_", "1");
                param.put("CREATE_TIME_", new Date());
                zlgjWTDao.insertZlgjOutSystem(param);

                logger.error("TDM向RDM创建流程成功，T_FAULTID：" + oneWtObj.getString("T_FAULTID"));
            }
        } catch (Exception e) {
            logger.error("创建失败：" + e.getMessage(), e);
            throw e;
        }
    }

    public String toGetGzxtCodeByName(String gzxtName) {
        if (org.apache.commons.lang.StringUtils.isBlank(gzxtName)) {
            return "";
        }
        String gzxtCode = "";
        List<JSONObject> list = zlgjWTDao.queryGzxtCodeByName(gzxtName);
        if (list.size() == 1) {
            gzxtCode = list.get(0).getString("name");
        }
        return gzxtCode;
    }

    private String toGetPfbz(String tdmKey) {
        if (StringUtil.isEmpty(tdmKey)) {
            return "";
        }
        String pfbz = "";
        List<SysDic> emissionStandardToPFBZ = sysDicManager.getByTreeKey("emissionStandardToPFBZ");
        for (SysDic sysDic : emissionStandardToPFBZ) {
            if (sysDic.getKey().equalsIgnoreCase(tdmKey)) {
                pfbz = sysDic.getValue();
            }
        }
        return pfbz;
    }

    private String toGetJiXing(String tdmKey) {
        if (StringUtil.isEmpty(tdmKey)) {
            return "";
        }
        String jixing = "";
        List<SysDic> productypeToJiXing = sysDicManager.getByTreeKey("productypeToJiXing");
        for (SysDic sysDic : productypeToJiXing) {
            if (sysDic.getKey().equalsIgnoreCase(tdmKey)) {
                jixing = sysDic.getValue();
            }
        }
        return jixing;
    }

    private JsonResult startProcess(String solId, JSONObject contentJson, IUser user) throws Exception {
        // 加上处理的消息提示
        ProcessMessage handleMessage = new ProcessMessage();
        JsonResult result = new JsonResult();
        try {
            ContextUtil.setCurUser(user);
            ProcessHandleHelper.setProcessMessage(handleMessage);
            ProcessStartCmd startCmd = new ProcessStartCmd();
            startCmd.setSolId(solId);
            startCmd.setJsonData(contentJson.toJSONString());
            // 启动流程
            bpmInstManager.doStartProcess(startCmd);
            result.setData(startCmd.getBusinessKey());
        } catch (Exception ex) {
            // 把具体的错误放置在内部处理，以显示正确的错误信息提示，在此不作任何的错误处理
            logger.error(ExceptionUtil.getExceptionMessage(ex));
            if (handleMessage.getErrorMsges().size() == 0) {
                handleMessage.addErrorMsg(ex.getMessage());
            }
        } finally {
            // 在处理过程中，是否有错误的消息抛出
            if (handleMessage.getErrorMsges().size() > 0) {
                result.setSuccess(false);
                result.setMessage("启动流程失败!");
                result.setData(handleMessage.getErrors());
            } else {
                result.setSuccess(true);
                result.setMessage("成功启动流程！");
            }
            ProcessHandleHelper.clearProcessCmd();
            ProcessHandleHelper.clearProcessMessage();
        }
        return result;
    }

    // 发送钉钉通知
    public void sendDingDing(JSONObject jsonObject, JSONObject resultJson) throws Exception {
        try {
            String messageText = jsonObject.getString("messageText");
            if (StringUtils.isBlank(messageText)) {
                resultJson.put("result", "fail");
                resultJson.put("message", "操作失败，messageText为空！");
                return;
            }
            String[] userNos = jsonObject.getString("userNos").split(",");
            if (userNos.length < 1) {
                resultJson.put("result", "fail");
                resultJson.put("message", "操作失败，userNos为空！");
                return;
            }
            JSONObject param = new JSONObject();
            for (String userNo : userNos) {
                param.put("userNo", userNo);
                List<JSONObject> userList = rdmDao.queryInJobUserByNo(param);
                if (userList == null || userList.isEmpty() || userList.size() != 1) {
                    resultJson.put("result", "fail");
                    resultJson.put("message", "操作失败，userNos在RDM中无对应或对应多个用户！具体账户：" + userNo);
                    return;
                }
            }
            StringBuilder stringBuilder = new StringBuilder("");
            for (String userNo : userNos) {
                IUser user = userService.getByUsername(userNo);
                stringBuilder.append(user.getUserId());
                stringBuilder.append(",");
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            JSONObject noticeObj = new JSONObject();
            noticeObj.put("content", messageText);
            sendDDNoticeManager.sendNoticeForCommon(stringBuilder.toString(), noticeObj);
        } catch (Exception e) {
            logger.error("发送失败：" + e.getMessage(), e);
            throw e;
        }
    }

    //..
    private void sendNoticeForCommon(String userIds, JSONObject taskObj) {
        String developOrProduce = SysPropertiesUtil.getGlobalProperty("developOrProduce");
        if ("develop".equals(developOrProduce)) {
            return;
        }
        String url = WebAppUtil.getProperty("dd_url");
        if (org.apache.commons.lang.StringUtils.isBlank(userIds) || org.apache.commons.lang.StringUtils.isBlank(url)) {
            return;
        }
        JSONObject oneDDMessage = new JSONObject();
        oneDDMessage.put("message", taskObj.getString("content"));
        String userNos = this.getUserCertNoById(userIds);
        if (org.apache.commons.lang.StringUtils.isBlank(userNos)) {
            return;
        }
        oneDDMessage.put("userNos", userNos);

        JSONObject ddObj = new JSONObject();
        ddObj.put("agentId", WebAppUtil.getProperty("dd_agentId"));
        ddObj.put("appKey", WebAppUtil.getProperty("dd_appKey"));
        ddObj.put("appSecret", WebAppUtil.getProperty("dd_appSecret"));
        JSONArray array = new JSONArray();
        ddObj.put("content", array);
        array.add(oneDDMessage);
        //logger.info("待办钉钉发送至：" + oneDDMessage.getString("userNos") + "，内容：" + oneDDMessage.getString("message"));
        this.httpSendNotices(ddObj, url);
    }

    //..
    private String getUserCertNoById(String userIds) {
        String userCertNos = "";
        try {
            String[] userIdArray = userIds.split(",");
            Map paramJson = new HashMap(16);
            paramJson.put("ids", Arrays.asList(userIdArray));
            List<Map> resultArray = rdmDao.queryUserInfoByIds(paramJson);
            Set<String> certNoSet = new HashSet<>();
            for (Map oneUser : resultArray) {
                certNoSet.add(CommonFuns.nullToString(oneUser.get("certNo")));
            }

            StringBuilder sb = new StringBuilder();
            for (String certNo : certNoSet) {
                if (org.apache.commons.lang.StringUtils.isNotBlank(certNo)) {
                    sb.append(certNo).append(",");
                }
            }
            if (sb.length() == 0) {
                return "";
            }
            userCertNos = sb.substring(0, sb.length() - 1);
        } catch (Exception e) {
            //logger.error("Exception in getUserCertNoById", e);
        }
        return userCertNos;
    }

    //..
    private void httpSendNotices(JSONObject sendObj, String url) {
        try {
            Map<String, String> reqHeaders = new HashMap<>();
            reqHeaders.put("Content-Type", "application/json;charset=utf-8");
            String rtnCode = HttpClientUtil.postJson(url, sendObj.toJSONString(), reqHeaders);
        } catch (Exception e) {
            //logger.error("Exception in httpSendNotices", e);
        }
    }

    /**
     * 根据TDM用户账号查询该用户负责的或参与的，当前在运行中的科技项目、阶段、交付物
     *
     * @param userNo TDM账号
     * @param result 返回结果
     */
    public void getProjectList(String userNo, JSONObject result) {
        // 账号统一，直接使用
        IUser user = userService.getByUsername(userNo);
        JSONObject params = new JSONObject();
        try {
            String rdmUserId = user.getUserId();
            params.put("userId", rdmUserId);
            params.put("status", "RUNNING");
            List<JSONObject> projectList = pdmApiDao.pdmQueryProjectList(params);
            if (projectList != null && !projectList.isEmpty()) {
                Map<String, List<JSONObject>> categoryId2StageList =
                        pdmApiManager.queryProjectStageDeliverys(projectList);
                for (JSONObject oneProject : projectList) {
                    String categoryId = oneProject.getString("categoryId");
                    List<JSONObject> stages = categoryId2StageList.get(categoryId);
                    if (stages == null) {
                        stages = new ArrayList<>();
                    }
                    oneProject.put("stage", stages);
                    oneProject.remove("categoryId");
                }
            }
            result.put("data", projectList);
            result.put("code", HttpStatus.SC_OK);
        } catch (Exception e) {
            logger.error("Exception in TDM getProjectList", e);
            result.put("code", HttpStatus.SC_INTERNAL_SERVER_ERROR);
            result.put("message", "XE-RDM系统异常，请联系RDM管理员");
        }
    }

    /**
     * 调用TDM接口获取交付物信息developOrProduce
     *
     * @param params
     * @return
     */
    public List<Map<String, Object>> getTDMProjectFiles(Map<String, Object> params) {
        String developOrProduce = SysPropertiesUtil.getGlobalProperty("developOrProduce");
        if ("develop".equals(developOrProduce)) {
            return Collections.emptyList();
        }
        String ipClass = sysDicManager.getBySysTreeKeyAndDicKey("TDMIpClass", "tecIp").getValue();
        String TDMUrl = sysDicManager.getBySysTreeKeyAndDicKey("TDMUrl", "projectDeliverables").getValue();
        String tdmProjectFileUrl = ipClass + TDMUrl;
        if (org.apache.commons.lang.StringUtils.isBlank(tdmProjectFileUrl)) {
            return Collections.emptyList();
        }
        if (params == null || params.get("projectId") == null
                || org.apache.commons.lang.StringUtils.isBlank(params.get("projectId").toString())) {
            return Collections.emptyList();
        }
        // todo
        try {
            List<Map<String, Object>> result = new ArrayList<>();
            Map<String, String> reqHeaders = new HashMap<>();
            reqHeaders.put("Content-Type", "application/json;charset=utf-8");
            JSONObject requestBody = new JSONObject();
            requestBody.put("projectId", params.get("projectId").toString());
            requestBody.put("stageId", params.get("stageId") == null ? "" : params.get("stageId").toString());
            requestBody.put("deliveryIds", Arrays.asList());
            requestBody.put("approvalStatus", "");
            String rtnContent = HttpClientUtil.postJson(tdmProjectFileUrl, requestBody.toJSONString(), reqHeaders);
            logger.info("response from pdm,return message:" + rtnContent);
            if (org.apache.commons.lang.StringUtils.isNotBlank(rtnContent)) {
                JSONObject returnObj = JSONObject.parseObject(rtnContent);
                if (returnObj.getIntValue("code") >= 400) {
                    return Collections.emptyList();
                }
                String projectFilesStr = returnObj.getString("projectFiles");
                if (org.apache.commons.lang.StringUtils.isNotBlank(projectFilesStr)) {
                    JSONArray projectFilesArr = JSONArray.parseArray(projectFilesStr);
                    if (projectFilesArr != null && !projectFilesArr.isEmpty()) {
                        Set<String> deliveryIds = new HashSet<>();
                        for (int index = 0; index < projectFilesArr.size(); index++) {
                            Map<String, Object> onePdmFile = new HashMap<>();
                            JSONObject oneFileObj = projectFilesArr.getJSONObject(index);
                            onePdmFile.put("fileSource", "pdm");
                            onePdmFile.put("creator",
                                    StringProcessUtil.toGetStringNotNull(oneFileObj.getString("createBy")));
                            String createTime = "";
                            if (org.apache.commons.lang.StringUtils.isNotBlank(oneFileObj.getString("createTime"))) {
                                createTime = oneFileObj.getString("createTime").split(" ", -1)[0];
                            }
                            onePdmFile.put("CREATE_TIME_", createTime);
                            onePdmFile.put("fileName",
                                    StringProcessUtil.toGetStringNotNull(oneFileObj.getString("fileName")));
                            onePdmFile.put("fileSize",
                                    StringProcessUtil.toGetStringNotNull(oneFileObj.getString("fileSize") + " KB"));
                            onePdmFile.put("deliveryId",
                                    StringProcessUtil.toGetStringNotNull(oneFileObj.getString("deliveryId")));
                            if (org.apache.commons.lang.StringUtils.isNotBlank(oneFileObj.getString("deliveryId"))) {
                                deliveryIds.add(oneFileObj.getString("deliveryId"));
                            }
                            onePdmFile.put("approvalStatus",
                                    StringProcessUtil.toGetStringNotNull(oneFileObj.getString("approvalStatus")));
                            onePdmFile.put("filePath",
                                    StringProcessUtil.toGetStringNotNull(oneFileObj.getString("filePath")));
                            onePdmFile.put("id", IdUtil.getId());
                            onePdmFile.put("isFolder", "0");
                            onePdmFile.put("projectId", params.get("projectId").toString());
                            onePdmFile.put("pid",
                                    StringProcessUtil.toGetStringNotNull(oneFileObj.getString("stageId")));
                            result.add(onePdmFile);
                        }
                        if (!deliveryIds.isEmpty()) {
                            Map<String, String> deliveryId2Name =
                                    pdmApiManager.queryDeliveryId2Name(new ArrayList<>(deliveryIds));
                            for (Map<String, Object> oneData : result) {
                                String deliveryId = oneData.get("deliveryId").toString();
                                String deliveryName = "";
                                if (deliveryId2Name.containsKey(deliveryId)) {
                                    deliveryName = deliveryId2Name.get(deliveryId);
                                }
                                oneData.put("deliveryName", deliveryName);
                            }
                        }
                    }
                }
            }
            return result;
        } catch (Exception e) {
            logger.error("Exception in httpSendNotices", e);
            return Collections.emptyList();
        }
    }
}
