<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html >
<html>
<head>
    <title>会议纪要管理表单</title>
    <%@include file="/commons/edit.jsp"%>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css" />
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="processInfo" class="mini-button" style="display: none" onclick="processInfo()">流程信息</a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
    </div>
</div>

<div class="mini-fit">
    <div class="form-container" style="margin:0 auto; width: 70%;height:98%;padding: 0">
        <form id="rwfjForm" method="post" style="height: 95%;width: 100%">
            <input id="instId" name="instId" class="mini-hidden"/>
            <input id="id" name="id" class="mini-hidden"/>
            <table class="table-detail grey"  cellspacing="1" cellpadding="0">
                <caption style="font-weight: bold;font-size: 20px !important;">会议纪要管理</caption>
                <tr>
                    <td style="text-align: center;width: 15%">组织部门：</td>
                    <td style="min-width:170px">
                        <input id="meetingOrgDepId" name="meetingOrgDepId" class="mini-dep rxc" plugins="mini-dep"
                               data-options="{'single':'true','config':{'type':'','grouplevel':'','groupid':'','groupidText':''}}"
                               style="width:98%;height:34px" allowinput="false" label="会议组织部门" textname="meetingOrgDepName" length="500"
                               maxlength="500" minlen="0" single="true" required="false" initlogindep="false" showclose="false"
                               mwidth="160" wunit="px" mheight="34" hunit="px"/>
                    </td>
                    <td style="text-align: center;width: 15%">会议负责人：</td>
                    <td style="min-width:170px">
                        <input id="meetingOrgUserId" name="meetingOrgUserId" textname="meetingOrgName" class="mini-user rxc"
                               plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="会议组织者" length="50" showclose="false"
                               mainfield="no" single="true"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">参会人员：</td>
                    <td style="min-width:170px">
                        <input id="meetingUserIds" name="meetingUserIds" textname="meetingUserNames" class="mini-user rxc"
                               plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="参会人员" length="1000" maxlength="1000"
                               mainfield="no" single="false"/>
                    </td>
                    <td style="text-align: center;width: 15%">会议时间：</td>
                    <td style="min-width:170px">
                        <input id="meetingTime" name="meetingTime" class="mini-datepicker" format="yyyy-MM-dd HH:mm:ss"
                                showTime="true" showOkButton="true" showClearButton="false" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">会议地点：</td>
                    <td style="min-width:170px">
                        <input id="meetingPlace" name="meetingPlace" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%">会议类型：</td>
                    <td style="min-width:170px">
                        <input id="meetingModel" name="meetingModelId"
                               class="mini-combobox" style="width:98%" emptyText="请选择..."
                               url="${ctxPath}/zhgl/core/hygl/getHyglType.do?"
                               valueField="id" textField="value"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">会议主题：</td>
                    <td style="min-width:170px">
                        <input id="meetingTheme" name="meetingTheme" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%">会议纪要编号：</td>
                    <td style="min-width:170px">
                        <input id="meetingNo" name="meetingNo" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">会议纪要描述：</td>
                    <td colspan="3" style="min-width:170px">
                        <input id="meetingContent" name="meetingContent" class="mini-textarea" style="width:99%;height:150px"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">第一责任人（具体执行人员）：</td>
                    <td style="min-width:170px">
                        <input id="meetingPlanRespUserIds" name="meetingPlanRespUserIds" textname="meetingPlanRespName" class="mini-user rxc"
                               plugins="mini-user" style="width:98%;height:34px;" allowinput="false"  length="1000" maxlength="1000"
                               mainfield="no" single="false"/>
                    </td>
                    <td style="text-align: center;width: 15%">其他责任人（相关责任人或领导）：</td>
                    <td style="min-width:170px">
                        <input id="otherPlanRespUserIds" name="otherPlanRespUserIds" textname="otherPlanRespUserName" class="mini-user rxc"
                               plugins="mini-user" style="width:98%;height:34px;" allowinput="false"  length="1000" maxlength="1000"
                               mainfield="no" single="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">预计完成时间：</td>
                    <td style="min-width:170px">
                        <input id="meetingPlanEndTime" name="meetingPlanEndTime" class="mini-datepicker" format="yyyy-MM-dd"
                               showTime="false" showOkButton="true" showClearButton="false" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%">是否完成：</td>
                    <td colspan="3" style="min-width:170px">
                        <input id="isComplete" name="isComplete" class="mini-combobox" style="width:98%;"
                               textField="key" valueField="value" emptyText="请选择..." onvaluechanged="displayShLeader()"
                               required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                               data="[ {'key' : '是','value' : 'true'},{'key' : '否','value' : 'false'}]"
                        />
                    </td>
                </tr>
                <tr id="shLeaderTr" style="display: none">
                    <td style="text-align: center;width: 15%">未完成会议纪要审核人：</td>
                    <td style="min-width:170px">
                        <input id="shLeader" name="shLeaderId" textname="shLeaderName"
                               class="mini-user rxc"
                               plugins="mini-user" style="width:98%;height:34px;" allowinput="false" length="1000"
                               maxlength="1000"
                               mainfield="no" single="true"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">情况描述：</td>
                    <td colspan="3" style="min-width:170px">
                        <input id="meetingPlanCompletion" name="meetingPlanCompletion" class="mini-textarea" style="width:99%;height:150px"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 15%;height: 200px;text-align: center ">附件列表：</td>
                    <td colspan="3">
                        <div style="margin-top: 2px;margin-bottom: 2px">
                            <a id="addFile" class="mini-button" onclick="fjupload()">添加附件</a>
                        </div>
                        <div id="fileListGrid" class="mini-datagrid" style="width: 100%; height: 85%"
                             allowResize="false"
                             idField="id" autoload="true"
                             url="${ctxPath}/zhgl/core/hygl/getMeetingFileList.do?meetingId=${id}"
                             multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
                            <div property="columns">
                                <div type="indexcolumn" align="center" width="15">序号</div>
                                <div field="fileName" width="140" headerAlign="center" align="center">附件名称</div>
                                <div field="fileSize" width="80" headerAlign="center" align="center">附件大小</div>
                                <div field="action" width="100" headerAlign='center' align="center"
                                     renderer="operationRenderer">操作
                                </div>
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
    var nodeVarsStr = '${nodeVars}';
    var jsUseCtxPath="${ctxPath}";
    var rwfjForm = new mini.Form("#rwfjForm");
    var id="${id}";
    var action="${action}";
    var status="${status}";
    var currentUserId="${currentUserId}";
    var currentUserName="${currentUserName}";
    var currentTime="${currentTime}";
    var fileListGrid=mini.get("fileListGrid");
    var coverContent=currentUserName+"<br/>"+currentTime+"<br/>徐州徐工挖掘机械有限公司";

    var stageName="";
    $(function () {
        if(id) {
            $.ajaxSettings.async = false;
            var url = jsUseCtxPath + "/rdm/core/rwfj/getRwfjDetail.do";
            $.post(
                url,
                {id: id},
                function (json) {
                    rwfjForm.setData(json);
                });
            $.ajaxSettings.async = true;
        }
        //变更入口
        if(action=='task') {
            taskActionProcess();
        }else if(action == 'detail'){
            rwfjForm.setEnabled(false);
            mini.get("addFile").setEnabled(false);
            $("#detailToolBar").show();
            if (status != 'DRAFTED') {
                $("#processInfo").show();
            }
        }
        displayShLeader();
        var meetingModelId=$.trim(mini.get("meetingModel").getValue());
        var isComplete=$.trim(mini.get("isComplete").getValue());
        if (meetingModelId=='0004'&&isComplete=='false'){
            mini.get("shLeader").setEnabled(false);
            mini.get("shLeader").setValue('679607106339236274');
            mini.get("shLeader").setText('徐玉兵');
        }
    });


    function displayShLeader() {
        var meetingModelId=$.trim(mini.get("meetingModel").getValue());
        var isComplete=$.trim(mini.get("isComplete").getValue());
        if(isComplete=='false'){
            $("#shLeaderTr").show();
        }else {
            $("#shLeaderTr").hide();
        }
    }

    function operationRenderer(e) {
        var record = e.record;
        var cellHtml = '';
        cellHtml = returnMeetingPreviewSpan(record.fileName, record.id, record.meetingId, coverContent);
        //和pdf预览用的同一个url
        var downLoadUrl = '/zhgl/core/hygl/meetingPdfPreviewAndAllDownload.do';
        cellHtml += '&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downLoadFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.meetingId + '\',\'' + downLoadUrl + '\')">下载</span>';
        //增加删除按钮
        if (record.CREATE_BY_ == currentUserId && stageName == 'second') {
            var deleteUrl = "/zhgl/core/hygl/delMeetingFile.do"
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.meetingId + '\',\'' + deleteUrl + '\')">删除</span>';
        }
        return cellHtml;
    }


    function returnMeetingPreviewSpan(fileName, fileId, formId, coverContent) {
        var fileType = getFileType(fileName);
        var s = '';
        if (fileType == 'other') {
            s = '<span  title="预览" style="color: silver" >预览</span>';
        } else if (fileType == 'pdf') {
            var url = '/zhgl/core/hygl/meetingPdfPreviewAndAllDownload.do';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        } else if (fileType == 'office') {
            var url = '/zhgl/core/hygl/meetingOfficePreview.do';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        } else if (fileType == 'pic') {
            var url = '/zhgl/core/hygl/meetingImagePreview.do';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        }
        return s;
    }

    function returnRwfjPreviewSpan(fileName, fileId, formId, coverContent) {
        var fileType = getFileType(fileName);
        var s = '';
        if (fileType == 'other') {
            s = '<span  title="预览" style="color: silver" >预览</span>';
        } else if (fileType == 'pdf') {
            var url = '/rdm/core/rwfj/rwfjPdfPreview';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        } else if (fileType == 'office') {
            var url = '/rdm/core/rwfj/rwfjOfficePreview';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        } else if (fileType == 'pic') {
            var url = '/rdm/core/rwfj/rwfjImagePreview';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        }
        return s;
    }

    function downLoadRwfjFile(fileName, fileId, formId) {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + '/rdm/core/rwfj/rwfjPdfPreview.do?action=download');
        var inputFileName = $("<input>");
        inputFileName.attr("type", "hidden");
        inputFileName.attr("name", "fileName");
        inputFileName.attr("value", fileName);
        var inputstandardId = $("<input>");
        inputstandardId.attr("type", "hidden");
        inputstandardId.attr("name", "formId");
        inputstandardId.attr("value", formId);
        var inputFileId = $("<input>");
        inputFileId.attr("type", "hidden");
        inputFileId.attr("name", "fileId");
        inputFileId.attr("value", fileId);
        $("body").append(form);
        form.append(inputFileName);
        form.append(inputstandardId);
        form.append(inputFileId);
        form.submit();
        form.remove();
    }


    function deleteRwfjFile(fileName,fileId,formId,urlValue) {
        mini.confirm("确定删除？", "确定？",
            function (action) {
                if (action == "ok") {
                    var url = jsUseCtxPath + urlValue;
                    var data = {
                        fileName: fileName,
                        id: fileId,
                        formId: formId
                    };
                    $.ajax({
                        url:url,
                        method:'post',
                        contentType: 'application/json',
                        data:mini.encode(data),
                        success:function (json) {
                            if(fileListGrid) {
                                fileListGrid.load();
                            }
                        }
                    });
                }
            }
        );
    }

    function getData() {
        var formData = _GetFormJsonMini("rwfjForm");
        //此处用于向后台产生流程实例时替换标题中的参数时使用
        // formData.bos=[];
        // formData.vars=[{key:'meetingTheme',val:formData.meetingTheme}];
        return formData;
    }
    function validFirst() {
        var meetingContent=$.trim(mini.get("meetingContent").getValue());
        if(!meetingContent) {
            return {"result": false, "message": "请填写会议纪要描述"};
        }
        var meetingPlanRespUserIds=$.trim(mini.get("meetingPlanRespUserIds").getValue())
        if(!meetingPlanRespUserIds) {
            return {"result": false, "message": "请填写第一责任人"};
        }
        var meetingPlanEndTime = $.trim(mini.get("meetingPlanEndTime").getValue());
        if (!meetingPlanEndTime) {
            return {"result": false, "message": "请选择预计完成时间"};
        }
        return {"result": true};
    }

    function validSecond() {
        var isComplete = $.trim(mini.get("isComplete").getValue());
        if (!isComplete) {
            return {"result": false, "message": "请选择是否完成"};
        }
        var meetingPlanCompletion=$.trim(mini.get("meetingPlanCompletion").getValue())
        if(!meetingPlanCompletion) {
            return {"result": false, "message": "请填写情况描述"};
        }
        var fileListGrid = $.trim(mini.get("fileListGrid").getData());
        if (!fileListGrid) {
            return {"result": false, "message": "请上传会议纪要管理附件"};
        }
        return {"result": true};
    }

    function validThird() {
        var isComplete = $.trim(mini.get("isComplete").getValue());
        if(isComplete=='false'){
            var shLeader = $.trim(mini.get("shLeader").getValue());
            if (!shLeader) {
                return {"result": false, "message": "请选择未完成会议纪要审核人"};
            }
        }
        return {"result": true};
    }


    function saveInfo() {
        var formData = _GetFormJsonMini("rwfjForm");
        if (formData.SUB_fileListGrid) {
            delete formData.SUB_fileListGrid;
        }
        $.ajax({
            url: jsUseCtxPath + '/rdm/core/rwfj/saveRwfj.do',
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

    function saveRwfj(e) {
        // var formValid = validFirst();
        // if (!formValid.result) {
        //     mini.alert(formValid.message);
        //     return;
        // }
        window.parent.saveDraft(e);
    }

    function startRwfjProcess(e) {
        var formValid = validFirst();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        window.parent.startProcess(e);
    }
    function rwfjApprove() {
        //编制阶段的下一步需要校验表单必填字段
        if (stageName == 'first') {
            var formValid = validFirst();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }
        if (stageName == 'second') {
            var formValid = validSecond();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
            var isComplete = $.trim(mini.get("isComplete").getValue());
            if(isComplete=='false'){
                alert('当前“是否完成”选择为“否”，会议负责人需在下一步选择领导审核未完成情况，请知悉');
            }
        }
        if (stageName == 'third') {
            var formValid = validThird();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }
        window.parent.approve();
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
        rwfjForm.setEnabled(false);
        mini.get("addFile").setEnabled(false);
        if(stageName == 'first') {
            mini.get("meetingContent").setEnabled(true);
            mini.get("meetingPlanEndTime").setEnabled(true);
        }
        if(stageName == 'second') {
            mini.get("addFile").setEnabled(true);
            mini.get("meetingPlanCompletion").setEnabled(true);
            mini.get("isComplete").setEnabled(true);
        }
        if(stageName == 'third') {
            mini.get("shLeader").setEnabled(true);
        }
    }

    function fjupload() {
        var id = mini.get("id").getValue();
        if (!id) {
            mini.alert('请先点击‘保存草稿’进行表单的保存！');
            return;
        }
        mini.open({
            title: "添加附件",
            url: jsUseCtxPath + "/rdm/core/rwfj/openUploadWindow.do?id=" + id,
            width: 850,
            height: 550,
            showModal: true,
            allowResize: true,
            ondestroy: function () {
                if (fileListGrid) {
                    fileListGrid.load();
                }
            }
        });
    }

    //所有非特殊文本都变成这个格式
    function render(e) {
        if (e.value != null && e.value != "") {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            return html;
        }
    }
</script>
</body>
</html>
