<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>专利解读信息</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>

<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="processInfo" class="mini-button" style="display: none" onclick="processInfo()">流程信息</a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin: 0 auto; width: 100%;">
        <form id="formBusiness" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <input id="businessStatus" name="businessStatus" class="mini-hidden"/>
            <input id="applyUserId" name="applyUserId" class="mini-hidden"/>
            <input id="applyUser" name="applyUser" class="mini-hidden"/>
            <input id="interpreterUserAssId" name="interpreterUserAssId" class="mini-hidden"/>
            <input id="leaderUserId" name="leaderUserId" class="mini-hidden"/>
            <input id="INST_ID_" name="INST_ID_" class="mini-hidden"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0">
                <caption style="font-weight: bold">
                    专利解读信息
                </caption>
                <tr>
                    <td style="text-align: center;width: 20%">专业类别：</td>
                    <td style="min-width:170px">
                        <input id="professionalCategory" name="professionalCategory" class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=professionalCategory"
                               valueField="key" textField="value"/>
                    </td>
                    <td style="text-align: center;width: 20%">专利公开（公告）号：</td>
                    <td style="min-width:170px">
                        <input id="patentPublicationNo" name="patentPublicationNo" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">专利名称：</td>
                    <td style="min-width:170px">
                        <input id="patentName" name="patentName" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%">申请日：</td>
                    <td style="min-width:170px">
                        <input id="applicationDate" name="applicationDate" class="mini-datepicker" format="yyyy-MM-dd" allowInput="false"
                               valueType="string" showTime="false" showOkButton="false" showClearButton="false" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">公开（公告）日：</td>
                    <td style="min-width:170px">
                        <input id="openDate" name="openDate" class="mini-datepicker" format="yyyy-MM-dd" allowInput="false"
                               valueType="string" showTime="false" showOkButton="false" showClearButton="false" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%">专利申请人：</td>
                    <td style="min-width:170px">
                        <input id="patentApplicant" name="patentApplicant" class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=patentApplicant"
                               valueField="key" textField="value"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">IPC分类号：</td>
                    <td style="min-width:170px">
                        <input id="IPCNo" name="IPCNo" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%">IPC主分类号：</td>
                    <td style="min-width:170px">
                        <input id="IPCMainNo" name="IPCMainNo" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">解读完成日期：</td>
                    <td style="min-width:170px">
                        <input id="interpretationCompletionDate" name="interpretationCompletionDate" class="mini-datepicker" format="yyyy-MM-dd"
                               allowInput="false" ondrawdate="onDrawDate"
                               valueType="string" showTime="false" showOkButton="false" showClearButton="false" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%">解读助手(自动生成)：</td>
                    <td style="min-width:170px">
                        <input id="interpreterUserAss" name="interpreterUserAss" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">解读人：</td>
                    <td style="min-width:170px">
                        <input id="interpreterUserId" name="interpreterUserId" textname="interpreterUser" style="width:98%;height:34px;"
                               class="mini-buttonedit" allowInput="false" showClose="true" onbuttonclick="SelectInterpreterUser"
                               oncloseclick="ColseInterpreterUser"/>
                    </td>
                    <td style="text-align: center;width: 15%">牵头人(自动生成)：</td>
                    <td style="min-width:170px">
                        <input id="leaderUser" name="leaderUser" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">方案简述（800字以内）：</td>
                    <td colspan="3">
						<textarea id="schemeDescription" name="schemeDescription" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:99.1%;height:150px;line-height:25px;" datatype="varchar" allowinput="true"
                                  emptytext="方案简述：简述专利所要解决的技术问题，解决该问题的技术方案的要点以及产生的技术效果或主要用途。"
                                  emptytext="简述主要创新点..." mwidth="80" wunit="%" mheight="200" hunit="px"></textarea>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">技术关键词（200字以内）：</td>
                    <td colspan="3">
						<textarea id="keyWords" name="keyWords" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:99.1%;height:100px;line-height:25px;" datatype="varchar" allowinput="true"
                                  emptytext="关键词从专利文献中摘取，可以为多个，格式为中文关键词（英文关键词）..."
                                  mwidth="80" wunit="%" mheight="200" hunit="px"></textarea>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">侵权风险：</td>
                    <td style="min-width:170px">
                        <input id="tortRisk" name="tortRisk" class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=tortRisk"
                               valueField="key" textField="value"/>
                    </td>
                    <td style="text-align: center;width: 15%">技术分支：</td>
                    <td style="min-width:170px">
                        <input id="technologyBranchId" name="technologyBranchId" textname="technologyBranch" style="width:98%;height:34px;"
                               class="mini-buttonedit" allowInput="false" showClose="true" onbuttonclick="SelectTechnologyBranch"
                               oncloseclick="ColseTechnologyBranch"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">风险分析对应产品范围：</td>
                    <td style="min-width:170px">
                        <input id="riskToPro" name="riskToPro" class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=riskToPro"
                               valueField="key" textField="value"/>
                    </td>
                    <td style="text-align: center;width: 15%">我司在该技术领域是否已有专利保护：</td>
                    <td style="min-width:170px">
                        <input id="riskToProAlready" name="riskToProAlready" class="mini-combobox" style="width:98%"
                               valueField="key" textField="value" data="[{key:'是',value:'是'},{key:'否',value:'否'}]"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">侵权风险分析：</td>
                    <td style="min-width:170px">
                        <textarea id="tortRiskAnalysis" name="tortRiskAnalysis" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:99.1%;height:150px;line-height:25px;" datatype="varchar" allowinput="true"
                                  emptytext="侵权风险分析..." mwidth="80" wunit="%" mheight="200" hunit="px"></textarea>
                    </td>
                    <td style="text-align: center;width: 15%">技术效果：</td>
                    <td style="min-width:170px">
                        <textarea id="technicalEffect" name="technicalEffect" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:99.1%;height:150px;line-height:25px;" datatype="varchar" allowinput="true"
                                  emptytext="技术效果..." mwidth="80" wunit="%" mheight="200" hunit="px"></textarea>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">我司专利名称及申请号<br>（可选多个）：</td>
                    <td style="min-width:170px">
                        <textarea id="patentNameMine" name="patentNameMine" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:99.1%;height:150px;line-height:25px;" datatype="varchar" allowinput="true"
                                  emptytext="名称-专利编号" mwidth="80" wunit="%" mheight="200" hunit="px" enabled="false"></textarea>
                        <a id="SelectPatentNameMine" class="mini-button" onclick="SelectPatentNameMine" enabled="false">选择专利</a>
                        <a id="ColsePatentNameMine" class="mini-button" onclick="ColsePatentNameMine" enabled="false">清空选择</a>
                    </td>
                    <td style="text-align: center;width: 15%">复杂程度：</td>
                    <td style="min-width:170px">
                        <input id="complexity" name="complexity" class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=complexity"
                               valueField="key" textField="value"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">解读评价：</td>
                    <td style="min-width:170px">
                        <input id="interpretationEvaluation" name="interpretationEvaluation" class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=interpretationEvaluate"
                               valueField="key" textField="value"/>
                    </td>
                    <td style="text-align: center;width: 15%">应用价值：</td>
                    <td style="min-width:170px">
                        <input id="applicationValue" name="applicationValue" class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=applicationValue"
                               valueField="key" textField="value"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">应用价值分析：</td>
                    <td colspan="3">
                        <textarea id="applicationValueAnalysis" name="applicationValueAnalysis" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:99.1%;height:150px;line-height:25px;" datatype="varchar" allowinput="true"
                                  emptytext="1、有应用价值的，要写明预计应用于我公司哪些产品或技术研究项目工作;
2、部分应用价值，写明哪部分技术方案可用于公司哪些产品研发或技术研究项目中，无应用价值部分要解释原因;
3、若无应用价值仍需分析;
4、若为委外或合作，可填写推荐单位"
                                  mwidth="80" wunit="%" mheight="200" hunit="px"></textarea>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;height: 300px">附件列表：</td>
                    <td colspan="3">
                        <div style="margin-top: 10px;margin-bottom: 2px">
                            <a id="addFile" class="mini-button" onclick="addBusinessFile()">添加文件</a>
                            <span style="color: red">注：添加文件前，请先进行草稿的保存</span>
                        </div>
                        <div id="fileListGrid" class="mini-datagrid" style="width: 100%; height: 85%" allowResize="false"
                             idField="id"
                             url="${ctxPath}/zhgl/core/patentInterpretation/getFileList.do?businessId=${businessId}"
                             autoload="true"
                             multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
                            <div property="columns">
                                <div type="indexcolumn" align="center" width="20">序号</div>
                                <div field="fileName" align="center" headerAlign="center" width="150">文件名</div>
                                <div field="fileSize" align="center" headerAlign="center" width="60">文件大小</div>
                                <div field="fileDesc" align="center" headerAlign="center" width="100">备注说明</div>
                                <div field="action" width="100" headerAlign='center' align="center" renderer="operationRenderer">操作</div>
                            </div>
                        </div>
                    </td>
                </tr>
                <%--///////////////////////////////////////////////////--%>
                <tr>
                    <td style="text-align: center;height: 500px">应用创新分配表：</td>
                    <td colspan="3">
                        <div class="mini-toolbar" id="itemButtons">
                            <a id="addItem" class="mini-button" style="margin-bottom: 5px;margin-top: 5px" onclick="addItem" enabled="false">添加任务</a>
                            <a id="editItem" class="mini-button" style="margin-bottom: 5px;margin-top: 5px" onclick="editItem"
                               enabled="false">编辑任务</a>
                            <a id="detailItem" class="mini-button" style="margin-bottom: 5px;margin-top: 5px" onclick="detailItem"
                               enabled="true">浏览任务</a>
                            <a id="deleteItem" class="mini-button btn-red" style="margin-bottom: 5px;margin-top: 5px" onclick="deleteItem"
                               enabled="false">删除任务</a>
                        </div>
                        <div id="itemListGrid" class="mini-datagrid" style="width: 100%; height: 85%" allowResize="true" allowCellWrap="true"
                             idField="id" url="${ctxPath}/zhgl/core/patentInterpretation/getItemList.do?businessId=${businessId}"
                             multiSelect="false" showPager="false" showColumnsMenu="false" allowcelledit="true" allowcellselect="true"
                             allowAlternating="true">
                            <div property="columns">
                                <div type="indexcolumn" headerAlign="center" align="center" width="40">序号</div>
                                <div field="suoZhang" align="center" headerAlign="center" width="60">主管所长</div>
                                <div field="chuangXinRen" align="center" headerAlign="center" width="60">创新人</div>
                                <div field="isNothing" align="center" headerAlign="center" width="80">与本所无关</div>
                                <div field="chuangXinFangAn" align="center" headerAlign="center" width="100" renderer="render">创新方案情况</div>
                                <div field="chuangXinQingKuang" align="center" headerAlign="center" width="200" renderer="render">创新情况说明</div>
                                <div field="chuangXinJiaoDiDate" align="center" headerAlign="center" width="130">创新技术交底书时间</div>
                                <div field="chuangXinWanChengDate" align="center" headerAlign="center" width="120">创新分析完成日期</div>
                                <div field="chuangXinPingFen" align="center" headerAlign="center" width="80">创新评价</div>
                                <div field="chuangXinJiaoDiShuNo" align="center" headerAlign="center" width="110" renderer="render">技术交底书编号</div>
                                <div field="chuangXinJiaoDiShuIPCMainNo" align="center" headerAlign="center" width="160" renderer="render">
                                    交底书预计IPC主分类号
                                </div>
                                <div field="remark" align="center" headerAlign="center" width="150" renderer="render">驳回意见</div>
                            </div>
                        </div>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var action = "${action}";
    var status = "${status}";
    var jsUseCtxPath = "${ctxPath}";
    var fileListGrid = mini.get("fileListGrid");
    var itemListGrid = mini.get("itemListGrid");
    var formBusiness = new mini.Form("#formBusiness");
    var businessId = "${businessId}";
    var nodeVarsStr = '${nodeVars}';
    var currentTime = "${currentTime}";
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var currentUserName = "${currentUserName}";
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";
    var isAediting = "";
    var isBassigning = "";
    var isCinterpreting = "";
    var isDevaluating = "";
    var isEassignChuangXinRen = "";
    var isFchuangXin = "";
    var isGchuangXinEvaluating = "";
    var isHreviewing = "";
    var isHreviewing2 = "";
    var isIPR = "${isIPR}";
    //..
    $(function () {
        var url = jsUseCtxPath + "/zhgl/core/patentInterpretation/getDetail.do";
        $.post(
            url,
            {businessId: businessId},
            function (json) {
                formBusiness.setData(json);
                itemListGrid.load();
                if (action == 'detail') {
                    formBusiness.setEnabled(false);
                    mini.get("addFile").setEnabled(false);
                    $("#detailToolBar").show();
                    //非草稿放开流程信息查看按钮
                    if (status != 'DRAFTED') {
                        $("#processInfo").show();
                    }
                    if (isIPR == "true") {
                        mini.get("editItem").setEnabled(true);
                    }
                } else if (action == 'task') {
                    taskActionProcess();
                }
            });
    });

    //..流程引擎调用此方法进行表单数据的获取
    function getData() {
        var formData = _GetFormJsonMini("formBusiness");
        if (formData.SUB_fileListGrid) {
            delete formData.SUB_fileListGrid;
        }
        formData.bos = [];
//        var data = itemGrid.getChanges();
//        if (data.length > 0) {
//            formData.itemChangeData=data;
//        }
        return formData;
    }

    //..保存草稿
    function saveBusiness(e) {
        //查重2022-06-29
        _SubmitJson({
            url: jsUseCtxPath + "/zhgl/core/patentInterpretation/isAlreadyHave.do",
            method: 'POST',
            data: {
                patentPublicationNo: mini.get("patentPublicationNo").getValue(),
                id: mini.get("id").getValue()
            },
            success: function (returnData) {
                if (returnData.success) {
                    window.parent.saveDraft(e);
                } else {
                    mini.alert(returnData.message);
                }
            }
        });
        //以上查重
//        window.parent.saveDraft(e);
    }

    //..启动流程
    function startBusinessProcess(e) {
        var formValid = validBusiness();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        //查重2022-06-29
        _SubmitJson({
            url: jsUseCtxPath + "/zhgl/core/patentInterpretation/isAlreadyHave.do",
            method: 'POST',
            data: {
                patentPublicationNo: mini.get("patentPublicationNo").getValue(),
                id: mini.get("id").getValue()
            },
            success: function (returnData) {
                if (returnData.success) {
                    window.parent.startProcess(e);
                } else {
                    mini.alert(returnData.message);
                }
            }
        });
        //以上查重
//        window.parent.startProcess(e);
    }

    //..流程中暂存信息（如编制阶段）
    function saveBusinessInProcess() {
        var formValid = validBusiness();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        var formData = _GetFormJsonMini("formBusiness");
        if (formData.SUB_fileListGrid) {
            delete formData.SUB_fileListGrid;
        }
        $.ajax({
            url: jsUseCtxPath + '/zhgl/core/patentInterpretation/saveBusiness.do',
            type: 'post',
            async: false,
            data: mini.encode(formData),
            contentType: 'application/json',
            success: function (data) {
                if (data) {
                    var message = "";
                    if (data.success) {
                        message = "数据保存成功";
                    } else {
                        message = "数据保存失败" + data.message;
                    }

                    mini.alert(message, "提示信息", function () {
                        window.location.reload();
                    });
                }
            }
        });
    }

    //..流程中的审批或者下一步
    function businessApprove(e) {
        //编制阶段的下一步需要校验表单必填字段
        if (isAediting == 'yes') {
            var formValid = validBusiness();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }
        //解读助手的下一步需要校验表单必填字段
        if (isBassigning == 'yes') {
            var formValid = validBusinessBassigning();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }
        //解读人的下一步需要校验表单必填字段
        if (isCinterpreting == 'yes') {
            var formValid = validBusinessCinterpreting();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }
        //牵头人的下一步需要校验表单必填字段
        if (isDevaluating == 'yes') {
            var formValid = validBusinessDevaluating();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }
        //所长分配责任人的下一步需要校验表单必填字段
        if (isEassignChuangXinRen == 'yes') {
            var formValid = validBusinessEassignChuangXinRen();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }
        //再创新的下一步需要校验表单必填字段
        if (isFchuangXin == 'yes') {
            var formValid = validBusinessFchuangXin();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }
        //所长评价的下一步需要校验表单必填字段
        if (isGchuangXinEvaluating == 'yes') {
            var formValid = validBusinessGchuangXinEvaluating();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }
        window.parent.approve();
    }

    //..添加文件
    function addBusinessFile() {
        var businessId = mini.get("id").getValue();
        if (!businessId) {
            mini.alert('请先点击‘保存草稿’进行表单的保存！');
            return;
        }
        mini.open({
            title: "文件上传",
            url: jsUseCtxPath + "/zhgl/core/patentInterpretation/fileUploadWindow.do?businessId=" + businessId,
            width: 750,
            height: 450,
            showModal: false,
            allowResize: true,
            onload: function () {
            },
            ondestroy: function (action) {
                if (fileListGrid) {
                    fileListGrid.load();
                }
            }
        });
    }

    //..流程信息浏览
    function processInfo() {
        var instId = $("#INST_ID_").val();
        var url = jsUseCtxPath + "/bpm/core/bpmInst/info.do?instId=" + instId;
        _OpenWindow({
            url: url,
            max: true,
            title: "流程图实例",
            width: 800,
            height: 600
        });
    }

    //..获取任务相关的环境变量，处理表单可见性
    function taskActionProcess() {
        //获取上一环节的结果和处理人
        bpmPreTaskTipInForm();
        //获取环境变量
        if (!nodeVarsStr) {
            nodeVarsStr = "[]";
        }
        var nodeVars = $.parseJSON(nodeVarsStr);
        for (var i = 0; i < nodeVars.length; i++) {
            if (nodeVars[i].KEY_ == 'isAediting') {
                isAediting = nodeVars[i].DEF_VAL_;
            }
            if (nodeVars[i].KEY_ == 'isBassigning') {
                isBassigning = nodeVars[i].DEF_VAL_;
            }
            if (nodeVars[i].KEY_ == 'isCinterpreting') {
                isCinterpreting = nodeVars[i].DEF_VAL_;
            }
            if (nodeVars[i].KEY_ == 'isDevaluating') {
                isDevaluating = nodeVars[i].DEF_VAL_;
            }
            if (nodeVars[i].KEY_ == 'isEassignChuangXinRen') {
                isEassignChuangXinRen = nodeVars[i].DEF_VAL_;
            }
            if (nodeVars[i].KEY_ == 'isFchuangXin') {
                isFchuangXin = nodeVars[i].DEF_VAL_;
            }
            if (nodeVars[i].KEY_ == 'isGchuangXinEvaluating') {
                isGchuangXinEvaluating = nodeVars[i].DEF_VAL_;
            }
            if (nodeVars[i].KEY_ == 'isHreviewing') {
                isHreviewing = nodeVars[i].DEF_VAL_;
            }
            if (nodeVars[i].KEY_ == 'isHreviewing2') {
                isHreviewing2 = nodeVars[i].DEF_VAL_;
            }
        }
        if (isAediting != 'yes') {
            formBusiness.setEnabled(false);
            mini.get("addFile").setEnabled(false);
            if (isBassigning == 'yes') {
                mini.get("interpreterUserId").setEnabled(true);
                mini.get("interpretationCompletionDate").setEnabled(true);
                mini.get("schemeDescription").setEnabled(true);
                mini.get("tortRisk").setEnabled(true);
                mini.get("tortRiskAnalysis").setEnabled(true);
                mini.get("technologyBranchId").setEnabled(true);
                mini.get("technicalEffect").setEnabled(true);
                mini.get("riskToPro").setEnabled(true);
                mini.get("riskToProAlready").setEnabled(true);
                mini.get("SelectPatentNameMine").setEnabled(true);
                mini.get("ColsePatentNameMine").setEnabled(true);
            } else if (isCinterpreting == 'yes') {
                mini.get("schemeDescription").setEnabled(true);
                mini.get("keyWords").setEnabled(true);
                mini.get("tortRisk").setEnabled(true);
                mini.get("tortRiskAnalysis").setEnabled(true);
                mini.get("technologyBranchId").setEnabled(true);
                mini.get("technicalEffect").setEnabled(true);
                mini.get("riskToPro").setEnabled(true);
                mini.get("riskToProAlready").setEnabled(true);
                mini.get("SelectPatentNameMine").setEnabled(true);
                mini.get("ColsePatentNameMine").setEnabled(true);
            } else if (isDevaluating == 'yes') {
                mini.get("interpretationEvaluation").setEnabled(true);
                mini.get("complexity").setEnabled(true);
                mini.get("applicationValue").setEnabled(true);
                mini.get("applicationValueAnalysis").setEnabled(true);
                mini.get("addItem").setEnabled(true);
                mini.get("editItem").setEnabled(true);
                mini.get("deleteItem").setEnabled(true);
            } else if (isEassignChuangXinRen == 'yes') {
                mini.get("addItem").setEnabled(true);
                mini.get("editItem").setEnabled(true);
                mini.get("deleteItem").setEnabled(true);
            } else if (isFchuangXin == 'yes') {
                mini.get("editItem").setEnabled(true);
            } else if (isGchuangXinEvaluating == 'yes') {
                mini.get("editItem").setEnabled(true);
            }
        }
    }

    //..检验表单是否必填
    function validBusiness() {
        var professionalCategory = $.trim(mini.get("professionalCategory").getValue());
        if (!professionalCategory) {
            return {"result": false, "message": "请填写专业类别"};
        }
        var patentPublicationNo = $.trim(mini.get("patentPublicationNo").getValue());
        if (!patentPublicationNo) {
            return {"result": false, "message": "请填写专利公开号"};
        }
        var patentName = $.trim(mini.get("patentName").getValue());
        if (!patentName) {
            return {"result": false, "message": "请填写专利名称"};
        }
        var applicationDate = $.trim(mini.get("applicationDate").getValue());
        if (!applicationDate) {
            return {"result": false, "message": "请选择申请日"};
        }
        var openDate = $.trim(mini.get("openDate").getValue());
        if (!openDate) {
            return {"result": false, "message": "请选择公开日"};
        }
        var patentApplicant = $.trim(mini.get("patentApplicant").getValue());
        if (!patentApplicant) {
            return {"result": false, "message": "请填写专利申请人"};
        }
        var IPCNo = $.trim(mini.get("IPCNo").getValue());
        if (!IPCNo) {
            return {"result": false, "message": "请填写IPC分类号"};
        }
        var IPCMainNo = $.trim(mini.get("IPCMainNo").getValue());
        if (!IPCMainNo) {
            return {"result": false, "message": "请填写IPC主分类号"};
        }
        if (fileListGrid.totalCount == 0) {
            return {"result": false, "message": '请上传附件'};
        }
        return {"result": true};
    }

    function validBusinessBassigning() {
        var interpretationCompletionDate = $.trim(mini.get("interpretationCompletionDate").getValue());
        if (!interpretationCompletionDate) {
            return {"result": false, "message": "请选择解读完成日期"};
        }
        var interpreterUserId = $.trim(mini.get("interpreterUserId").getValue());
        if (!interpreterUserId) {
            return {"result": false, "message": "请选择解读人"};
        }
        return {"result": true};
    }

    function validBusinessCinterpreting() {
        var schemeDescription = $.trim(mini.get("schemeDescription").getValue());
        if (!schemeDescription) {
            return {"result": false, "message": "请填写方案简述"};
        }
        var tortRisk = $.trim(mini.get("tortRisk").getValue());
        if (!tortRisk) {
            return {"result": false, "message": "请选择侵权风险"};
        }
        var tortRiskAnalysis = $.trim(mini.get("tortRiskAnalysis").getValue());
        if (!tortRiskAnalysis) {
            return {"result": false, "message": "请填写侵权风险分析"};
        }
        var technologyBranchId = $.trim(mini.get("technologyBranchId").getValue());
        if (!technologyBranchId) {
            return {"result": false, "message": "请选择技术分支"};
        }
        var technicalEffect = $.trim(mini.get("technicalEffect").getValue());
        if (!technicalEffect) {
            return {"result": false, "message": "请填写技术效果"};
        }
        var riskToPro = $.trim(mini.get("riskToPro").getValue());
        if (!riskToPro) {
            return {"result": false, "message": "请填写风险分析对应产品范围"};
        }
        var riskToProAlready = $.trim(mini.get("riskToProAlready").getValue());
        if (!riskToProAlready) {
            return {"result": false, "message": "请选择该技术领域是否有专利保护"};
        }
        var isBranchOk = false;
        $.ajax({
            url: jsUseCtxPath + "/zhgl/core/patentInterpretation/isBranchOk.do?id=" + businessId +
            "&technologyBranchId=" + technologyBranchId,
            async: false,
            success: function (data) {
                if (data.success) {
                    isBranchOk = true;
                }
            }
        });
        if (!isBranchOk) {
            return {"result": false, "message": "技术分支和专业类别不匹配，请选择和专业类别同类型下的技术分支"};
        }
        return {"result": true};
    }

    function validBusinessDevaluating() {
        var interpretationEvaluation = $.trim(mini.get("interpretationEvaluation").getValue());
        if (!interpretationEvaluation) {
            return {"result": false, "message": "请进行解读评价"};
        }
        var complexity = $.trim(mini.get("complexity").getValue());
        if (!complexity) {
            return {"result": false, "message": "请选择复杂度"};
        }
        var applicationValue = $.trim(mini.get("applicationValue").getValue());
        if (!applicationValue) {
            return {"result": false, "message": "请填写应用价值"};
        }
        var applicationValueAnalysis = $.trim(mini.get("applicationValueAnalysis").getValue());
        if (!applicationValueAnalysis) {
            return {"result": false, "message": "请进行应用价值分析"};
        }
        if (itemListGrid.totalCount == 0 && mini.get("applicationValue").getValue() != '无') {
            return {"result": false, "message": '请进行应用创新任务分配'};
        }
        var rows = itemListGrid.getData();
        for (i = 0; i < rows.length; i++) {
            if (rows[i].suoZhangId == "" || rows[i].suoZhangId == null) {
                return {"result": false, "message": "所有明细必须都指定主管所长"};
            }
        }
        return {"result": true};
    }

    function validBusinessEassignChuangXinRen() {
        var rows = itemListGrid.getData();
        var chuangXinRenIds = [];
        for (i = 0; i < rows.length; i++) {
            if (rows[i].suoZhangId == currentUserId &&
                (rows[i].isNothing == "否" || rows[i].isNothing == null) &&
                (rows[i].chuangXinRenId == null || rows[i].chuangXinRenId == "" ||
                rows[i].chuangXinWanChengDate == null || rows[i].chuangXinWanChengDate == "")) {
                return {"result": false, "message": "本人负责的记录必须指定创新责任人,设置完成时间"};
            }
            if (rows[i].chuangXinRenId != null && rows[i].chuangXinRenId != "" && rows[i].isNothing == "否") {
                chuangXinRenIds.push(rows[i].chuangXinRenId);
            }
        }
        if (chuangXinRenIds.length == 0) {
            return {
                "result": false, "message": "当前还没有任何所长选择创新人!如果您选择'与本所无关',可暂时等待," +
                "一旦其他所长有一个选择了创新人,您就可以继续提交了"
            };
        }
        if (isRepeat(chuangXinRenIds)) {
            return {"result": false, "message": "不能有重复的创新责任人"};
        }
        return {"result": true};
    }

    function isRepeat(arr) {
        var hash = {};
        for (i = 0; i < arr.length; i++) {
            if (hash[arr[i]]) {
                return true;
            }
            hash[arr[i]] = true;
        }
        return false;
    }

    function validBusinessFchuangXin() {
        var rows = itemListGrid.getData();
        for (i = 0; i < rows.length; i++) {
            if (rows[i].chuangXinRenId == currentUserId &&
                (rows[i].chuangXinFangAn == null || rows[i].chuangXinFangAn == "")) {
                return {"result": false, "message": "本人负责的记录必须填写创新方案情况"};
            }
            if ((rows[i].chuangXinFangAn == "有改进方案，可以申请专利" ||
                rows[i].chuangXinFangAn == "有规避方案，可以申请专利") &&
                (rows[i].chuangXinJiaoDiDate == null ||
                rows[i].chuangXinJiaoDiDate == "")) {
                return {
                    "result": false,
                    "message": "创新方案情况为'有改进方案，可以申请专利'或者'有规避方案，可以申请专利'时，必须填写技术交底书预计提交时间"
                };
            }
            if ((rows[i].chuangXinJiaoDiDate != null &&
                rows[i].chuangXinJiaoDiDate != "") &&
                ((rows[i].chuangXinJiaoDiShuNo == null ||
                rows[i].chuangXinJiaoDiShuNo == ""))) {
                return {
                    "result": false,
                    "message": "有'技术交底书预计提交时间'时，必须填写'技术交底书编号'"
                };
            }
        }
        return {"result": true};
    }

    function validBusinessGchuangXinEvaluating() {
        var rows = itemListGrid.getData();
        for (i = 0; i < rows.length; i++) {
            if (rows[i].suoZhangId == currentUserId &&
                (rows[i].chuangXinPingFen == null || rows[i].chuangXinPingFen == "")) {
                return {"result": false, "message": "本人负责的记录必须进行创新评价"};
            }
        }
        return {"result": true};
    }


    //..文件列表操作渲染
    function operationRenderer(e) {
        var record = e.record;
        var cellHtml = '';
        cellHtml = returnPreviewSpan(record.fileName, record.id, record.mainId, coverContent);
        //和pdf预览用的同一个url
        var downLoadUrl = '/zhgl/core/patentInterpretation/pdfPreviewAndAllDownload.do';
        cellHtml += '&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downLoadFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.mainId + '\',\'' + downLoadUrl + '\')">下载</span>';
        //增加删除按钮
        if (action == 'edit' || (action == 'task' && isAediting == 'yes')) {
            var deleteUrl = "/zhgl/core/patentInterpretation/delFile.do"
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.mainId + '\',\'' + deleteUrl + '\')">删除</span>';
        }
        return cellHtml;
    }

    //..文件列表预览渲染
    function returnPreviewSpan(fileName, fileId, formId, coverContent) {
        var fileType = getFileType(fileName);
        var s = '';
        if (fileType == 'other') {
            s = '<span  title="预览" style="color: silver" >预览</span>';
        } else if (fileType == 'pdf') {
            var url = '/zhgl/core/patentInterpretation/PdfPreview.do';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        } else if (fileType == 'office') {
            var url = '/zhgl/core/patentInterpretation/OfficePreview.do';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        } else if (fileType == 'pic') {
            var url = '/zhgl/core/patentInterpretation/ImagePreview.do';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        }
        return s;
    }

    //..添加任务明细
    function addItem() {
        var mainId = mini.get("id").getValue();
        if (!mainId) {
            mini.alert('请先点击‘保存草稿’进行表单的保存！');
            return;
        }
        var url = jsUseCtxPath + "/zhgl/core/patentInterpretation/itemPage.do?mainId=" + mainId + "&action=add";
        var winObj = window.open(url, '');
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (itemListGrid) {
                    itemListGrid.reload();
                }
            }
        }, 1000);
    }

    //..编辑任务明细
    function editItem() {
        var row = itemListGrid.getSelected();
        if (!row) {
            mini.alert("请选中一条记录");
            return;
        }
        var businessId = row.id;
        var suoZhangId = row.suoZhangId;
        var chuangXinRenId = row.chuangXinRenId
        if (isAediting != 'yes') {
            if (isEassignChuangXinRen == 'yes') {
                if (suoZhangId != currentUserId) {
                    mini.alert("请编辑本人负责的记录");
                    return;
                }
            } else if (isFchuangXin == 'yes') {
                if (chuangXinRenId != currentUserId) {
                    mini.alert("请编辑本人负责的记录");
                    return;
                }
            } else if (isGchuangXinEvaluating == 'yes') {
                if (suoZhangId != currentUserId) {
                    mini.alert("请编辑本人负责的记录");
                    return;
                }
            }
        }
        var url = jsUseCtxPath + "/zhgl/core/patentInterpretation/itemPage.do?businessId=" + businessId + "&action=edit";
        var winObj = window.open(url, '');
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (itemListGrid) {
                    itemListGrid.reload();
                }
            }
        }, 1000);
    }

    //..浏览任务
    function detailItem() {
        var row = itemListGrid.getSelected();
        if (!row) {
            mini.alert("请选中一条记录");
            return;
        }
        var businessId = row.id;
        var url = jsUseCtxPath + "/zhgl/core/patentInterpretation/itemPage.do?businessId=" + businessId + "&action=detail";
        var winObj = window.open(url, '');
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (itemListGrid) {
                    itemListGrid.reload();
                }
            }
        }, 1000);
    }

    //..删除任务
    function deleteItem() {
        var row = itemListGrid.getSelected();
        if (!row) {
            mini.alert("请选中一条记录");
            return;
        }
        if (row.id == "") {
            delRowGrid("itemListGrid");
            return;
        }
        var CREATE_BY_ = row.CREATE_BY_;
        if (isAediting != 'yes') {
            if (isEassignChuangXinRen == 'yes') {
                if (CREATE_BY_ != currentUserId) {
                    mini.alert("只能删除本人添加的记录！");
                    return;
                }
            }
        }
        mini.confirm("确定删除选中记录？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                var id = row.id;
                _SubmitJson({
                    url: jsUseCtxPath + "/zhgl/core/patentInterpretation/deleteItem.do",
                    method: 'POST',
                    data: {id: id},
                    success: function (returnData) {
                        if (returnData) {
                            if (returnData && returnData.message) {
                                mini.alert(returnData.message, '提示', function () {
                                    if (returnData.success) {
                                        itemListGrid.reload();
                                    }
                                });
                            }
                        }
                    }
                });
            }
        });
    }

    //..选择解读人，热配自定义对话框，只有挖掘机械研究院下助理级和师级的人员
    function SelectInterpreterUser(e) {
        e.sender.dialogname = "选择解读人员--只包含助理级和师级人员";
        e.sender.dialogalias = "技术中心助理级和师级人员选择";
        e.sender.valueField = "USER_ID_";
        e.sender.textField = "FULLNAME_";
        e.sender.height = 600;
        e.sender.width = 1200;
        _OnMiniDialogShow(e)
    }

    function ColseInterpreterUser() {
        mini.get("interpreterUserId").setValue("");
        mini.get("interpreterUserId").setText("");
    }

    //..选择技术分支，热配自定义树形对话框
    function SelectTechnologyBranch(e) {
        e.sender.dialogname = "选择技术分支--只能选择最下层子节点";
        e.sender.dialogalias = "专利解读技术分支选择";
        e.sender.valueField = "id";
        e.sender.textField = "description";
        e.sender.height = 600;
        e.sender.width = 1200;
        _OnMiniDialogShow(e)
    }

    function ColseTechnologyBranch() {
        mini.get("technologyBranchId").setValue("");
        mini.get("technologyBranchId").setText("");
    }

    //..我司专利名称及申请号，需要定制因此不能直接用_OnMiniDialogShow
    function SelectPatentNameMine() {
        _CommonDialog({
            title: "选择我司对应专利",
            height: 600,
            width: 1200,
            dialogKey: "我司专利名称-申请号-提案号-IPC主分类号",
            ondestroy: function (action) {
                var valueString = "";
                var win = this.getIFrameEl().contentWindow;
                var data = win.getData();
                var rows = data.rows;
                if (rows != null && rows.length > 0) {
                    for (i = 0; i < rows.length; i++) {
                        valueString += rows[i].patentName + "-" + rows[i].applicationNumber + ";"
                    }
                    valueString = valueString.substring(0, valueString.length - 1);
                    mini.get("patentNameMine").setValue(valueString);
                }
            }
        });
    }

    function ColsePatentNameMine() {
        mini.get("patentNameMine").setValue("");
    }

    //..所有非特殊文本都变成这个格式
    function render(e) {
        if (e.value != null && e.value != "") {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            return html;
        }
    }

    //..设置日期选择限制
    function onDrawDate(e) {
        var date = e.date;
        var dateNow = new Date();
        var dateNowForword = new Date().add(15, 'day');
        if (date.getTime() < dateNow.getTime() || date.getTime() > dateNowForword.getTime()) {
            e.allowSelect = false;
        }
    }
</script>
</body>
</html>
