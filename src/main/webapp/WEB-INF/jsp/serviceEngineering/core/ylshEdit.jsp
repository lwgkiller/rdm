<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html >
<html>
<head>
    <title>翻译语料库审批单</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
    <link href="${ctxPath}/styles/css/multiupload.css" rel="stylesheet" type="text/css"/>
</head>
<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="processInfo" class="mini-button" style="display: none" onclick="processInfo()">流程信息</a>
        <a id="closeWindow1" class="mini-button" onclick="CloseWindow()">关闭</a>
    </div>
</div>

<div class="mini-fit">
    <div class="form-container" style="margin:0 auto; width: 100%;height:98%;padding: 0">
        <form id="ylshForm" method="post" style="height: 95%;width: 98%">
            <input class="mini-hidden" id="id" name="id"/>
            <input id="instId" name="instId" class="mini-hidden"/>
            <input id="CREATE_BY_" name="CREATE_BY_" class="mini-hidden"/>
            <input id="checkName" name="checkName" class="mini-hidden"/>
            <p style="text-align: center;font-size: 20px;font-weight: bold;margin-top: 0px;margin-bottom: 10px">翻译语料库审批单</p>
            <table class="table-detail grey" cellspacing="1" cellpadding="0">
                <tr>
                    <td style="text-align: center;width: 15%">翻译申请单号：</td>
                    <td style="min-width:170px">
                        <input id="transApplyId" name="transApplyId" textname="transApplyId"
                               style="width:98%;height:34px;" value="临时"
                               class="mini-buttonedit" showClose="true" allowInput="true"
                               oncloseclick="inputClearClick()" onbuttonclick="inputWindowClick()"/>
                    </td>
                    <td style="text-align: center;width: 15%">手册类型:<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="manualType" name="manualType" class="mini-combobox" style="width:98%;" enabled="true"
                               textField="value" valueField="key" emptyText="请选择..."
                               allowInput="false" showNullItem="true" nullItemText="请选择..."
                               data="[{'key' : '操保手册','value' : '操保手册'},
							   {'key' : '装修手册','value' : '装修手册'},
							   {'key' : '装箱单','value' : '装箱单'},
							   {'key' : '样本','value' : '样本'},
							   {'key' : '其他','value' : '其他'}]"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">文件名：</td>
                    <td style="min-width:170px">
                        <input id="aimFileName" name="aimFileName" class="mini-textbox" style="width:98%;"
                               enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 20%">文件类型：</td>
                    <td style="min-width:170px">
                        <input id="aimFileType" name="aimFileType" class="mini-combobox" style="width:98%;"
                               enabled="true"
                               textField="value" valueField="key" emptyText="请选择..."
                               allowInput="false" showNullItem="true" nullItemText="请选择..."
                               data="[{'key' : '语料库','value' : '语料库'},
							   {'key' : '翻译对照表','value' : '翻译对照表'},
							   {'key' : '其他','value' : '其他'}]"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">销售型号：<span style="color:red">*</span></td>
                    <td>
                        <input id="saleType" name="saleType" class="mini-textbox" style="width:98%;"
                               enabled="true"/>
                    </td>
                    <td style="text-align: center;width: 20%">手册编码：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="manualCode" name="manualCode" class="mini-textbox" style="width:98%;"
                               enabled="true"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">物料编码：<span style="color:red">*</span></td>
                    <td>
                        <input id="materialCode" name="materialCode" class="mini-textbox" style="width:98%;"
                               enabled="true"/>
                    </td>
                    <td style="text-align: center;width: 20%">词条数：</td>
                    <td style="min-width:170px">
                        <input id="wordsNum" name="wordsNum" class="mini-textbox" style="width:98%;"
                               enabled="true"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">源语言：<span style="color:red">*</span></td>
                    <td>
                        <input id="sourceManualLan" name="sourceManualLan" class="mini-textbox" style="width:98%;"
                               enabled="true"/>
                    </td>
                    <td style="text-align: center;width: 20%">翻译语言：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="targetManualLan" name="targetManualLan" class="mini-textbox" style="width:98%;"
                               enabled="true"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center">完成时间：<span style="color:red">*</span></td>
                    <td>
                        <input id="endTime" name="endTime" class="mini-datepicker" format="yyyy-MM-dd"
                               allowInput="false"
                               showTime="false" showOkButton="false" showClearButton="true" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 20%">提交人：</td>
                    <td style="min-width:170px">
                        <input id="creatorName" name="creatorName" class="mini-textbox" style="width:98%;"
                               enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">版本：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="version" name="version" class="mini-textbox" style="width:98%;"
                               enabled="true"/>
                    </td>
                    <td style="text-align: center;width: 20%">校对人：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="checkId" name="checkId" class="mini-combobox" style="width:98%;"
                               valueField="key" textField="value" onvaluechanged="checkIdChange()"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center">备注：</td>
                    <td colspan="3">
						<textarea id="remark" name="remark" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:98%;height:125px;line-height:25px;"
                                  label="备注" datatype="varchar" length="2000" vtype="length:2000" minlen="0"
                                  allowinput="true"
                                  emptytext="请输入备注..." mwidth="80" wunit="%" mheight="125" hunit="px"></textarea>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 14%;height:10px">附件列表：</td>
                    <td colspan="3" height="60px">
                        <div style="margin-top: 10px;margin-bottom: 2px">
                            <a id="addYlshFile" name="addYlshFile" class="mini-button"
                               onclick="addYlshFile('${applyId}')">待审核文件上传</a>
                            <a class="mini-button btn-red" id="delDemandRow" name="delDemandRow" plain="true"
                               onclick="delDataRow">移除</a>
                            <a class="mini-button" id="addYlshRetFile" name="addYlshRetFile" plain="true"
                               onclick="addYlshRetFile">已审核文件上传</a>
                            <span style="color: red">注：添加附件前，请先进行草稿的保存</span>
                        </div>
                        <div id="fileListGrid" class="mini-datagrid"
                             allowResize="false"
                             url="${ctxPath}/serviceEngineering/core/ylsh/demandList.do?applyId=${applyId}"
                             autoload="true"
                             multiSelect="true" showPager="false" showColumnsMenu="false"
                             allowAlternating="true"
                             style="height:150px;">
                            <div property="columns">
                                <div type="checkcolumn" width="20"></div>
                                <div field="oriFileId" width="80" headerAlign="center" align="center">关联文件</div>
                                <div field="aimFileName" width="140" headerAlign="center" align="center">待审文件名称</div>
                                <div field="aimDesc" width="80" headerAlign="center" align="center">待审描述</div>
                                <div field="action" width="100" headerAlign='center' align="center" renderer="aimFileRenderer">待审核文件操作</div>
                                <div field="resFileName" name="resFileName" width="120" headerAlign="center" align="center" allowSort="true">已审文件名称
                                    <input property="editor" class="mini-textbox"/>
                                </div>
                                <div field="resDesc" name="resDesc" width="250" headerAlign="center" align="center" allowSort="true">已审说明
                                    <input property="editor" class="mini-textbox">
                                </div>
                                <div id="reop" name="reop" cellCls="actionIcons" width="110" headerAlign="center" align="center"
                                     renderer="retFileRenderer" cellStyle="padding:0;">已审文件操作
                                </div>
                            </div>
                        </div>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>

<div id="translationWindow" title="选择随机文件翻译" class="mini-window" style="width:1300px;height:550px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
    <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px"
         borderStyle="border-left:0;border-top:0;border-right:0;">
        <span style="font-size: 14px;color: #777">单据编号: </span>
        <input class="mini-textbox" width="130" id="filterBusunessNo" style="margin-right: 15px"/>
        <span style="font-size: 14px;color: #777">销售型号: </span>
        <input class="mini-textbox" width="130" id="filterSalesModel" style="margin-right: 15px"/>
        <span style="font-size: 14px;color: #777">设计型号: </span>
        <input class="mini-textbox" width="130" id="filterDesignModel" style="margin-right: 15px"/>
        <a class="mini-button" plain="true" onclick="searchInputList()">查询</a>
        <a class="mini-button btn-red" plain="true" onclick="clearInputList()">清空查询</a></td>
    </div>
    <div class="mini-fit">
        <div id="translationListGrid" class="mini-datagrid" style="width: 100%; height: 100%;"
             allowResize="false" onrowdblclick="onRowDblClick"
             idField="id" showColumnsMenu="false" sizeList="[20,50,100,200]" pageSize="20"
             allowAlternating="true" pagerButtons="#pagerButtons"
             url="${ctxPath}/serviceEngineering/core/attachedDocTranslate/dataListQuerySdltm.do"
             autoload="true">
            <div property="columns">
                <div type="checkcolumn" width="30"></div>
                <div field="fileName" width="150" headerAlign="center" align="center" allowSort="true"
                     renderer="render">Sdltm文件
                </div>
                <div field="fileId" width="150" headerAlign="center" align="center" allowSort="true" visible="false">
                </div>
                <div field="transApplyId" width="150" headerAlign="center" align="center" allowSort="true"
                     renderer="render">翻译申请单号
                </div>
                <div field="manualType" width="130" headerAlign="center" align="center" allowSort="true"
                     renderer="render">手册类型
                </div>
                <div field="saleType" width="130" headerAlign="center" align="center" allowSort="true"
                     renderer="render">销售型号
                </div>
                <div field="designType" width="130" headerAlign="center" align="center" allowSort="true"
                     renderer="render">设计型号
                </div>
                <div field="materialCode" width="130" headerAlign="center" align="center" allowSort="true"
                     renderer="render">物料编码
                </div>
                <div field="manualCode" width="100" headerAlign="center" align="center" allowSort="true"
                     renderer="render">手册编码
                </div>
                <div field="manualVersion" width="100" headerAlign="center" align="center" allowSort="true"
                     renderer="render">手册版本
                </div>
                <div field="sourceManualLan" width="100" headerAlign="center" align="center" allowSort="true"
                     renderer="render">源手册语言
                </div>
                <div field="targetManualLan" width="100" headerAlign="center" align="center" allowSort="true"
                     renderer="render">翻译语言
                </div>
                <div field="translator" width="100" headerAlign="center" align="center" allowSort="true"
                     renderer="render">翻译人
                </div>
                <div field="applyTime" width="100" headerAlign="center" align="center" allowSort="true"
                     renderer="render" dateFormat="yyyy-MM-dd">申请时间
                </div>
                <div field="needTime" width="100" headerAlign="center" align="center" allowSort="true" renderer="render"
                     dateFormat="yyyy-MM-dd">需求时间
                </div>
                <div field="creatorName" width="100" headerAlign="center" align="center" allowSort="true"
                     renderer="render">申请人
                </div>
                <div field="applyDep" width="100" headerAlign="center" align="center" allowSort="true"
                     renderer="render">申请部门
                </div>
            </div>
        </div>
    </div>
    <div property="footer" style="padding:5px;height: 35px">
        <table style="width:100%;height: 100%">
            <tr>
                <td style="width:120px;text-align:center;">
                    <input type="button" style="height: 25px;width: 70px" value="确定" onclick="selectInputOK()"/>
                    <input type="button" style="height: 25px;width: 70px" value="取消" onclick="selectInputHide()"/>
                </td>
            </tr>
        </table>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var nodeVarsStr = '${nodeVars}';
    var ylshForm = new mini.Form("#ylshForm");
    var fileListGrid = mini.get("fileListGrid");
    var translationWindow = mini.get("translationWindow");
    var translationListGrid = mini.get("translationListGrid");
    var action = "${action}";
    var status = "${status}";
    var applyId = "${applyId}";
    var instId = "${instId}";
    var currentUserId = "${currentUserId}";
    var currentUserName = "${currentUserName}";
    var currentTime = "${currentTime}";
    var checkUsers = '${checkUsers}';
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";
    function render(e) {
        if (e.value != null && e.value != "") {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            return html;
        }
    }
    var stageName = "";

    $(function () {
        var url = jsUseCtxPath + "/serviceEngineering/core/ylsh/getJson.do";
        $.post(
            url,
            {id: applyId},
            function (json) {
                ylshForm.setData(json);
                mini.get("checkId").setData(mini.decode(checkUsers));
            });
        if (action == 'detail') {
            ylshForm.setEnabled(false);
            mini.get("addYlshFile").setEnabled(false);
            mini.get("checkId").setEnabled(false);
            $("#detailToolBar").show();
            if (status != 'DRAFTED') {
                $("#processInfo").show();
            }
        } else if (action == 'task') {
            taskActionProcess();
        } else if (action == 'edit') {
            mini.get("addYlshRetFile").setEnabled(false);
            fileListGrid.hideColumn("resFileName");
            fileListGrid.hideColumn("resDesc");
            fileListGrid.hideColumn("reop");
        }
    });

    function getData() {
        var formData = _GetFormJsonMini("ylshForm");
        if (formData.SUB_fileListGrid) {
            delete formData.SUB_fileListGrid;
        }
        if (formData.SUB_demandGrid) {
            delete formData.SUB_demandGrid;
        }

        if (fileListGrid.getChanges().length > 0) {
            formData.changeDemandGrid = fileListGrid.getChanges();
        }
        return formData;
    }

    //保存草稿
    function saveDraft(e) {
        var endTime = $.trim(mini.get("endTime").getValue());
        if (!endTime) {
            mini.alert("请选择完成时间");
            return;
        }
        var checkId = $.trim(mini.get("checkId").getValue());
        if (!checkId) {
            mini.alert("请选择校对人");
            return;
        }
        var version = $.trim(mini.get("version").getValue());
        if (!version) {
            mini.alert("请输入版本");
            return;
        }
        window.parent.saveDraft(e);
    }

    //..暂存信息
    function saveInProcess() {
        var formData = getData();
        var json = mini.encode(formData);
        $.ajax({
            url: jsUseCtxPath + '/serviceEngineering/core/ylsh/saveInProcess.do',
            type: 'post',
            async: false,
            data: json,
            contentType: 'application/json',
            success: function (data) {
                if (data) {
                    var message = "";
                    if (data.success) {
                        window.location.reload();
                    } else {
                        mini.alert("数据保存失败" + data.message);
                    }
                }
            }
        });
    }

    //发起流程
    function startProcess(e) {
        var formValid = validYlsh();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        window.parent.startProcess(e);
    }

    //下一步审批
    function projectApprove() {
        //编制阶段的下一步需要校验表单必填字段
        if (stageName == 'start') {
            var formValid = validYlsh();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }
        if (stageName == 'check') {

            var demandGridData = fileListGrid.getData()[0];
            if (demandGridData.resFileId == undefined || demandGridData.resFileId == "") {
                mini.alert("请上传已审核文件！");
                return;
            }
        }

        //检查通过
        window.parent.approve();
    }

    function validYlsh() {
//        var transApplyId = $.trim(mini.get("transApplyId").getValue());
//        if (!transApplyId) {
//            return {"result": false, "message": "请填写翻译申请单号"};
//        }

        var saleType = $.trim(mini.get("saleType").getValue());
        if (!saleType) {
            return {"result": false, "message": "请填写销售型号"};
        }

        var materialCode = $.trim(mini.get("materialCode").getValue());
        if (!materialCode) {
            return {"result": false, "message": "请填写物料号"};
        }

        var manualCode = $.trim(mini.get("manualCode").getValue());
        if (!manualCode) {
            return {"result": false, "message": "请填写手册编码"};
        }

        var sourceManualLan = $.trim(mini.get("sourceManualLan").getValue());
        if (!sourceManualLan) {
            return {"result": false, "message": "请填写源语言"};
        }

        var targetManualLan = $.trim(mini.get("targetManualLan").getValue());
        if (!targetManualLan) {
            return {"result": false, "message": "请填写翻译语言"};
        }

        var manualType = $.trim(mini.get("manualType").getValue());
        if (!manualType) {
            return {"result": false, "message": "请选择手册类型"};
        }

        var endTime = $.trim(mini.get("endTime").getValue());
        if (!endTime) {
            return {"result": false, "message": "请选择完成时间"};
        }

        if (fileListGrid.getData().length == 0) {
            return {"result": false, "message": "请上传待审核文件"};
        }

        return {"result": true};
    }

    function processInfo() {
        var instId = $("#instId").val();
        var url = jsUseCtxPath + "/bpm/core/bpmInst/info.do?instId=" + instId;
        _OpenWindow({
            url: url,
            max: true,
            title: "流程图实例",
            width: 800,
            height: 600
        });
    }

    function taskActionProcess() {
        //获取上一环节的结果和处理人
        bpmPreTaskTipInForm();
        //获取环境变量
        if (!nodeVarsStr) {
            nodeVarsStr = "[]";
        }
        var nodeVars = $.parseJSON(nodeVarsStr);
        for (var i = 0; i < nodeVars.length; i++) {
            if (nodeVars[i].KEY_ == 'stageName') {
                stageName = nodeVars[i].DEF_VAL_;
            }
        }
        if (stageName != 'start') {
            ylshForm.setEnabled(false);
            mini.get("addYlshFile").setEnabled(false);
            mini.get("delDemandRow").setEnabled(false);
            mini.get("checkId").setEnabled(false);
        }
        if (stageName != "check") {
            mini.get("addYlshRetFile").setEnabled(false);
        }
        if (stageName == 'check') {

            mini.get("checkId").setValue(currentUserId);
            mini.get("checkName").setValue(currentUserName);
        }
        if (stageName == 'start') {
            fileListGrid.hideColumn("resFileName");
            fileListGrid.hideColumn("resDesc");
            fileListGrid.hideColumn("reop");
        }


    }

    function addYlshFile(applyId) {
        var fileLen = fileListGrid.getData().length;
        var stageKey = "";
        if (!applyId) {
            mini.alert('请先点击‘保存草稿’进行表单的保存！');
            return;
        }
        if (fileLen != 0) {
            mini.alert("已存在待审核文件，请移除后再添加！");
            return;

        }


        mini.open({
            title: "文件上传",
            url: jsUseCtxPath + "/serviceEngineering/core/ylsh/openUploadWindow.do?applyId=" + applyId,
            width: 750,
            height: 450,
            showModal: false,
            allowResize: true,
            onload: function () {
                var iframe = this.getIFrameEl();
                var projectParams = {};
                projectParams.applyId = applyId;
                var data = {projectParams: projectParams};  //传递上传参数
                iframe.contentWindow.SetData(data);
            },
            ondestroy: function (action) {

                window.location.reload();
            }
        });
    }

    function addYlshRetFile(e) {
        var selecteds = fileListGrid.getSelecteds();
        if (selecteds.length > 1 || selecteds.length == 0) {
            mini.alert("请选择一条数据");
            return;
        }
        if (selecteds[0].resFileId && selecteds[0].resFileId != "") {
            mini.alert("回传文件已存在，请删除后上传");
            return;
        }
        mini.open({
            title: "附件上传",
            url: jsUseCtxPath + "/serviceEngineering/core/ylsh/openUploadWindow.do?applyId=" + applyId + "&fType=" + "ret",
            width: 650,
            height: 350,
            showModal: false,
            allowResize: true,
            onload: function () {
                var iframe = this.getIFrameEl();
                var projectParams = {};
                var data = {projectParams: projectParams};  //传递上传参数
                iframe.contentWindow.SetData(data);
            },
            ondestroy: function (action) {
                fileListGrid.load();
            }
        });
    }

    // function operationRenderer(e) {
    //     var record = e.record;
    //     //预览、下载和删除
    //     if (!record.id) {
    //         return "";
    //     }
    //     var cellHtml = returnPreviewSpan(record.fileName, record.id, record.applyId, coverContent);
    //     var downloadUrl = '/zhgl/core/ylsh/fileDownload.do';
    //     cellHtml += '&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
    //         'onclick="downLoadFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.applyId + '\',\'' + downloadUrl + '\')">下载</span>';
    //
    //     if (record && (action == "edit" || stageName == "start" || stageName == "summary")) {
    //         var deleteUrl = "/zhgl/core/ylsh/deleteFiles.do";
    //         cellHtml += '&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
    //             'onclick="deleteFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.applyId + '\',\'' + deleteUrl + '\')">删除</span>';
    //     }
    //     return cellHtml;
    // }

    function returnPreviewSpan(fileName, fileId, formId, coverContent) {
        var s = '';
        if (fileName == "") {
            return s;
        }
        var fileType = getFileType(fileName);
        if (fileType == 'other') {
            s = '<span  title="预览" style="color: silver" >预览</span>';
        } else {
            var url = '/serviceEngineering/core/ylsh/preview.do?fileType=' + fileType;
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf6(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\',\'' + fileType + '\')">预览</span>';
        }
        return s;
    }

    function inputWindowClick() {
        debugger;
        var data = fileListGrid.getData()[0];
        if (data && data.oriFileId) {
            mini.alert("已经绑定翻译申请单，请先删除文件解绑，再进行选择");
            return;
        }
        translationWindow.show();
    }

    function clearInputList() {
        mini.get("filterBusunessNo").setValue("");
        mini.get("filterSalesModel").setValue("");
        mini.get("filterDesignModel").setValue("");
        searchInputList();
    }

    function searchInputList() {
        var queryParam = [];

        var transApplyId = $.trim(mini.get("filterBusunessNo").getValue());
        if (transApplyId) {
            queryParam.push({name: "transApplyId", value: transApplyId});
        }
        var saleType = $.trim(mini.get("filterSalesModel").getValue());
        if (saleType) {
            queryParam.push({name: "saleType", value: saleType});
        }
        var designType = $.trim(mini.get("filterDesignModel").getValue());
        if (designType) {
            queryParam.push({name: "designType", value: designType});
        }
        var inputList = translationListGrid;
        var data = {};
        data.filter = mini.encode(queryParam);
        data.pageIndex = inputList.getPageIndex();
        data.pageSize = inputList.getPageSize();
        data.sortField = inputList.getSortField();
        data.sortOrder = inputList.getSortOrder();
        //查询
        inputList.load(data);
    }

    function selectInputOK() {
        if (!applyId) {
            mini.alert('请先点击‘保存草稿’进行表单的保存！');
            return;
        }
        var inputList = translationListGrid;
        var selectRow = inputList.getSelected();
        if (selectRow) {
            //处理sdltm文件拷贝
            $.ajax({
                url: jsUseCtxPath + "/serviceEngineering/core/ylsh/getSdltmFromTranslationToYlsh.do?" +
                "translationId=" + selectRow.id + "&applyId=" + applyId + "&fileId=" + selectRow.fileId,
                type: 'GET',
                contentType: 'application/json',
                success: function () {
                    var saleType = '';
                    var materialCode = '';
                    var manualType = '';
                    var manualCode = '';
                    var sourceManualLan = '';
                    var targetManualLan = '';
                    var transApplyId = '';
                    transApplyId = selectRow.transApplyId;
                    saleType = selectRow.saleType;
                    materialCode = selectRow.materialCode;
                    manualType = selectRow.manualType;
                    manualCode = selectRow.manualCode;
                    sourceManualLan = selectRow.sourceManualLan;
                    targetManualLan = selectRow.targetManualLan;
                    mini.get("transApplyId").setValue(transApplyId);
                    mini.get("transApplyId").setText(transApplyId);
                    mini.get("saleType").setValue(saleType);
                    mini.get("materialCode").setValue(materialCode);
                    mini.get("manualType").setValue(manualType);
                    mini.get("manualCode").setValue(manualCode);
                    mini.get("sourceManualLan").setValue(sourceManualLan);
                    mini.get("targetManualLan").setValue(targetManualLan);
                    fileListGrid.load();
                }
            });
        } else {
            mini.alert("请选择一条数据！");
            return;
        }
        selectInputHide();
    }

    function selectInputHide() {

        translationWindow.hide();

    }

    function onRowDblClick() {
        selectInputOK();
    }

    function aimFileRenderer(e) {
        var record = e.record;
        //预览、下载和删除
        var cellHtml = returnPreviewSpan(record.aimFileName, record.aimFileId, applyId, coverContent);
        var downloadUrl = '/serviceEngineering/core/ylsh/fileDownload.do';
        cellHtml += '&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downLoadFile(\'' + record.aimFileName + '\',\'' + record.aimFileId + '\',\'' + applyId + '\',\'' + downloadUrl + '\')">下载</span>';
        return cellHtml;
    }

    function retFileRenderer(e) {
        var record = e.record;
        //预览、下载和删除
        if (!record.resFileId) {
            return "";
        }
        var cellHtml = returnPreviewSpan(record.resFileName, record.resFileId, applyId, coverContent);
        var downloadUrl = '/serviceEngineering/core/ylsh/fileDownload.do';
        cellHtml += '&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downLoadFile(\'' + record.resFileName + '\',\'' + record.resFileId + '\',\'' + applyId + '\',\'' + downloadUrl + '\')">下载</span>';
        if (record && action == "task" && stageName == "check") {
            var deleteUrl = "/serviceEngineering/core/ylsh/deleteFiles.do";
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(\'' + record.resFileName + '\',\'' + record.resFileId + '\',\'' + applyId + '\',\'' + deleteUrl + '\')">删除</span>';
        }
        return cellHtml;
    }

    function delDataRow(e) {
        var selecteds = fileListGrid.getSelecteds();
        if (selecteds.length == 0) {
            mini.alert("请选择一行后移除")
            return;
        }
        fileListGrid.removeRows(selecteds);
        saveInProcess();
    }

    function checkIdChange() {
        mini.get("checkName").setValue(mini.get("checkId").getText());
    }
</script>
</body>
</html>
