<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>会议管理</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
</head>

<body>
<div id="loading" class="loading" style="display:none;text-align:center;"><img
        src="${ctxPath}/styles/images/loading.gif"></div>
<div id="toolBar" class="topToolBar">
    <div>
        <a id="saveMeetingDraft" class="mini-button" style="display: none" onclick="saveMeetingDraft()">保存草稿</a>
        <a id="commitMeeting" class="mini-button" style="display: none" onclick="commitMeeting()">提交</a>
        <a id="feedbackMeeting" class="mini-button" style="display: none" onclick="feedbackMeeting()">保存</a>
        <a id="closeWindow" class="mini-button btn-red" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit" id="content">
    <div class="form-container" style="margin:0 auto; width: 100%;padding: 0">
        <form id="meetingForm" method="post">
            <input class="mini-hidden" id="id" name="id"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0" style="height: 100%;width: 100%">
                <caption style="font-weight: bold;font-size: 20px !important;">会议管理</caption>
                <tr>
                    <td style="text-align: center;width: 15%">组织部门<span style="color:red">*</span>：</td>
                    <td style="min-width:170px">
                        <input id="meetingOrgDepId" name="meetingOrgDepId" class="mini-dep rxc" plugins="mini-dep"
                               data-options="{'single':'true','config':{'type':'','grouplevel':'','groupid':'','groupidText':''}}"
                               style="width:98%;height:34px" allowinput="false" label="会议组织部门"
                               textname="meetingOrgDepName" length="500"
                               maxlength="500" minlen="0" single="true" required="false" initlogindep="false"
                               showclose="false"
                               mwidth="160" wunit="px" mheight="34" hunit="px"/>
                    </td>
                    <td style="text-align: center;width: 15%">会议负责人<span style="color:red">*</span>：</td>
                    <td style="min-width:170px">
                        <input id="meetingOrgUserId" name="meetingOrgUserId" textname="meetingOrgUserName"
                               class="mini-user rxc"
                               plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="会议组织者"
                               length="50" showclose="false"
                               mainfield="no" single="true"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">参会人员<span style="color:red">*</span>：</td>
                    <td style="min-width:170px">
                        <input id="meetingUserIds" name="meetingUserIds" textname="meetingUserNames"
                               class="mini-user rxc"
                               plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="参会人员"
                               length="1000" maxlength="1000"
                               mainfield="no" single="false"/>
                    </td>
                    <td style="text-align: center;width: 15%">会议时间<span style="color:red">*</span>：</td>
                    <td style="min-width:170px">
                        <input id="meetingTime" name="meetingTime" class="mini-datepicker" format="yyyy-MM-dd HH:mm:ss"
                               timeFormat="HH:mm:ss" showTime="true" showOkButton="true" showClearButton="false"
                               style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">会议地点<span style="color:red">*</span>：</td>
                    <td style="min-width:170px">
                        <input id="meetingPlace" name="meetingPlace" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%">会议类型<span style="color:red">*</span>：</td>
                    <td style="min-width:170px">
                        <input id="meetingModel" name="meetingModelId"
                               class="mini-combobox" style="width:98%" emptyText="请选择..."
                               url="${ctxPath}/zhgl/core/hygl/getHyglType.do?"
                               valueField="id" textField="value"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">会议主题<span style="color:red">*</span>：</td>
                    <td style="min-width:170px">
                        <input id="meetingTheme" name="meetingTheme" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%">会议编号(保存后自动生成)：</td>
                    <td style="min-width:170px">
                        <input id="meetingNo" name="meetingNo" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">外部交流单位(非必填)：</td>
                    <td style="min-width:170px">
                        <input id="company" name="company" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%">会议记录人<span style="color:red">*</span>：</td>
                    <td style="min-width:170px">
                        <input id="apply" name="applyId" textname="applyName" class="mini-user rxc"
                               plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="会议组织者"
                               length="50" showclose="false"
                               mainfield="no" single="true"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">会议概要（1000字以内）<span style="color:red">*</span>：</td>
                    <td colspan="3">
						<textarea id="contentAndConclusion" name="contentAndConclusion" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:99.1%;height:150px;line-height:25px;"
                                  label="交流相关业务内容，达成的共识以及形成的结论" datatype="varchar" allowinput="true"
                                  emptytext="请输入交流相关业务内容，达成的共识以及形成的结论..." mwidth="80" wunit="%" mheight="200"
                                  hunit="px"></textarea>
                    </td>
                </tr>
                <tr style="display: none">
                    <td style="text-align: center;width: 20%;">采取的计划、执行结果（1000字以内）<span style="color:red">*</span>：</td>
                    <td colspan="3">
						<textarea id="planAndResult" name="planAndResult" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:99.1%;height:150px;line-height:25px;"
                                  label="采取的行动计划及执行结果" datatype="varchar" allowinput="true"
                                  emptytext="请输入采取的行动计划及执行结果..." mwidth="80" wunit="%" mheight="200"
                                  hunit="px"></textarea>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;height: 500px">会议纪要分解及执行情况反馈<span style="color:red">*</span>：</td>
                    <td colspan="3">
                        <div class="mini-toolbar" id="planButtons" style="display: none">
                            <a id="addPlan" class="mini-button" style="margin-bottom: 5px;margin-top: 5px"
                               onclick="addMeetingPlan">添加会议纪要</a>
                            <a id="moveUp" class="mini-button" value="上移" onclick="moveUp()">上移</a>
                            <a id="moveDown" class="mini-button" value="下移" onclick="moveDown()">下移</a>
                            <a id="copyOtherResp" class="mini-button" value="下移" onclick="copyOtherResp()">复制参会人员至其他责任人</a>
                            <a id="deletePlan" class="mini-button btn-red" style="margin-bottom: 5px;margin-top: 5px"
                               onclick="deleteMeetingPlan">删除会议纪要</a>
                            <a class="mini-button" style="margin-right: 5px" plain="true"
                               onclick="exportPlanBusiness()">导出</a>
                            <span style="color: red">注：添加会议纪要前,请先点击‘保存草稿’;移动会议纪要后请保存以生效！</span>
                        </div>
                        <div id="planListGrid" class="mini-datagrid" style="width: 100%; height: 85%"
                             allowResize="false" allowCellWrap="true" allowHeaderWrap="true"
                             idField="id" url="${ctxPath}/zhgl/core/hygl/getMeetingPlanList.do?meetingId=${meetingId}"
                             multiSelect="false" showPager="false" showColumnsMenu="false" allowcelledit="true"
                             allowcellselect="true"
                             allowAlternating="true" oncellvalidation="onCellValidation">
                            <div property="columns">
                                <div type="checkcolumn" width="20"></div>
                                <div type="indexcolumn" headerAlign="center" align="center" width="30">序号</div>
                                <div field="meetingContent" align="left" headerAlign="center" width="300"
                                     renderer="render">会议纪要描述
                                    <input property="editor" class="mini-textarea"/>
                                </div>
                                <%--lwgkiller：此处在行内用mini-user组件，textname无效，默认会以name+"_name"作为textname--%>
                                <div field="meetingPlanRespUserIds" displayField="meetingPlanRespUserIds_name"
                                     align="center" headerAlign="center" width="160">第一责任人<br>（具体执行人员）
                                    <input property="editor" class="mini-user rxc" plugins="mini-user"
                                           allowinput="false" single="true"
                                           mainfield="no" name="meetingPlanRespUserIds"/>
                                </div>
                                <div field="otherPlanRespUserIds" displayField="otherPlanRespUserIds_name"
                                     align="center" headerAlign="center" width="160">其他责任人<br>（相关责任人或领导）
                                    <input property="editor" class="mini-user rxc" plugins="mini-user"
                                           allowinput="false" single="false"
                                           mainfield="no" name="otherPlanRespUserIds"/>
                                </div>
                                <div field="meetingPlanEndTime" align="center" headerAlign="center" width="100"
                                     dateFormat="yyyy-MM-dd">预计完成时间
                                    <input property="editor" class="mini-datepicker" format="yyyy-MM-dd"
                                           showTime="false" showOkButton="true" showClearButton="false"/>
                                </div>
                                <div field="isComplete" align="center" headerAlign="center" width="60"
                                     renderer="isCompleteRenderer">完成自评
                                    <input property="editor" class="mini-combobox" align="center" textField="key"
                                           valueField="value"
                                           data="[ {'key' : '是','value' : 'true'},{'key' : '否','value' : 'false'}]"
                                           readonly/>
                                </div>
                                <div field="meetingPlanCompletion" align="left" headerAlign="center" width="300"
                                     renderer="render">情况描述
                                    <input property="editor" class="mini-textarea" readonly/>
                                </div>
                                <div field="finishTime" align="center" headerAlign="center" width="100"
                                     dateFormat="yyyy-MM-dd">实际完成时间
                                </div>
                                <div field="status" width="70" headerAlign="center" align="center" allowSort="true"
                                     renderer="onStatusRenderer">纪要状态
                                </div>
                            </div>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;height: 180px">签字版会议纪要附件列表<span style="color:red">*</span>：</td>
                    <td colspan="3">
                        <div class="mini-toolbar" id="fileButtons" style="display: none">
                            <a id="uploadFile" class="mini-button" style="margin-bottom: 5px;margin-top: 5px"
                               onclick="uploadMeetingFile">添加附件</a>
                            <span style="color: red">注：添加附件前，请先点击“保存草稿!”</span>
                        </div>
                        <div id="fileListGrid" class="mini-datagrid" style="width: 100%; height: 75%"
                             allowResize="false"
                             idField="id" url="${ctxPath}/zhgl/core/hygl/getMeetingFileList.do?meetingId=${meetingId}"
                             multiSelect="false" showPager="false" showColumnsMenu="false"
                             allowAlternating="true">
                            <div property="columns">
                                <div type="indexcolumn" headerAlign="center" align="center" width="20">序号</div>
                                <div field="fileName" align="center" headerAlign="center" width="150">文件名</div>
                                <div field="fileSize" align="center" headerAlign="center" width="60">文件大小</div>
                                <div field="fileDesc" align="center" headerAlign="center" width="100">备注说明</div>
                                <div field="action" width="80" headerAlign='center' align="center"
                                     renderer="operationRenderer">操作
                                </div>
                            </div>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">会议是否完成：</td>
                    <td>
                        <input id="isfinish" name="isfinish" class="mini-combobox" style="width:98%;"
                               textField="key" valueField="value" emptyText="请选择..."
                               required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                               data="[ {'key' : '是','value' : '是'},{'key' : '否','value' : '否'}]"
                        />
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">会议总结：</td>
                    <td colspan="3">
						<textarea id="meetingSum" name="meetingSum" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:99.1%;height:150px;line-height:25px;"
                                  label="会议总结" datatype="varchar" allowinput="true"
                                  emptytext="请输入会议总结..." mwidth="80" wunit="%" mheight="200" hunit="px"></textarea>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>

<!--导出Excel相关HTML-->
<form id="excelForm" action="${ctxPath}/zhgl/core/hygl/exportPlanList.do" method="post" target="excelIFrame">
    <input type="hidden" name="filter" id="filter"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var meetingForm = new mini.Form("#meetingForm");
    var fileListGrid = mini.get("fileListGrid");
    var planListGrid = mini.get("planListGrid");
    var meetingId = "${meetingId}";
    var action = "${action}";
    var currentUserId = "${currentUserId}";
    var currentUserName = "${currentUserName}";
    var currentUserMainGroupId = "${currentUserMainGroupId}";
    var currentUserMainGroupName = "${currentUserMainGroupName}";
    var currentTime = "${currentTime}";
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";


    function moveUp() {
        var row = planListGrid.getSelected();
        if (row) {
            var index = planListGrid.indexOf(row);
            planListGrid.moveRow(row, index - 1);
        }
    }

    function moveDown() {
        var row = planListGrid.getSelected();
        if (row) {
            var index = planListGrid.indexOf(row);
            planListGrid.moveRow(row, index + 2);
        }
    }

    function onStatusRenderer(e) {
        var record = e.record;
        var status = record.status;

        var arr = [{'key': 'DRAFTED', 'value': '草稿', 'css': 'orange'},
            {'key': 'RUNNING', 'value': '审批中', 'css': 'green'},
            {'key': 'SUCCESS_END', 'value': '审批完成', 'css': 'blue'},
            {'key': 'DISCARD_END', 'value': '作废', 'css': 'red'}
        ];

        return $.formatItemValue(arr, status);
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
        if (record.CREATE_BY_ == currentUserId && action != 'detail') {
            var deleteUrl = "/zhgl/core/hygl/delMeetingFile.do"
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.meetingId + '\',\'' + deleteUrl + '\')">删除</span>';
        }
        return cellHtml;
    }

    //..
    function exportPlanBusiness() {
        if (!meetingId) {
            mini.alert("请先保存草稿");
            return;
        }
        var params = [];
        var obj = {};
        obj.name = "meetingId";
        obj.value = meetingId;
        params.push(obj);
        $("#filter").val(mini.encode(params));
        var excelForm = $("#excelForm");
        excelForm.submit();
    }


    //所有非特殊文本都变成这个格式
    function render(e) {
        if (e.value != null && e.value != "") {
            var html = "<div style='line-height: 20px;height:90px;overflow: auto' >";
            var content = e.value;
            if (content == null) {
                content = "";
            }
            html += '<span style="white-space:pre-wrap" >' + content + '</span>';
            html += '</div>'
            return html;
        }
    }

    //..
    $(function () {
        if (meetingId) {
            var url = jsUseCtxPath + "/zhgl/core/hygl/queryMeetingById.do?meetingId=" + meetingId;
            $.ajax({
                url: url,
                method: 'get',
                success: function (json) {
                    meetingForm.setData(json);
                }
            });
            fileListGrid.load();
            planListGrid.load();
        } else {
            mini.get("meetingOrgDepId").setValue(currentUserMainGroupId);
            mini.get("meetingOrgDepId").setText(currentUserMainGroupName);
            mini.get("apply").setValue(currentUserId);
            mini.get("apply").setText(currentUserName);
        }
        mini.get("apply").setEnabled(false);
        mini.get("meetingOrgDepId").setEnabled(false);
        //不同场景的处理
        if (action == 'detail') {
            meetingForm.setEnabled(false);
            planListGrid.setAllowCellEdit(false);
        } else if (action == 'edit' || action == 'add') {
            mini.get("saveMeetingDraft").show();
            mini.get("commitMeeting").show();
            mini.get("fileButtons").show();
            mini.get("planButtons").show();
            mini.get("isfinish").setEnabled(false);
            mini.get("meetingSum").setEnabled(false);
        } else if (action == 'feedback') {
            mini.get("feedbackMeeting").show();
            meetingForm.setEnabled(false);
            planListGrid.setAllowCellEdit(false);
            mini.get("isfinish").setEnabled(true);
            mini.get("meetingSum").setEnabled(true);
        }
    });

    //..
    function saveMeetingDraft() {
        var meetingModelId = mini.get("meetingModel").getValue();
        if (!meetingModelId) {
            mini.alert("请选择“会议类型”");
            return;
        }
        var meetingTime = mini.get("meetingTime").getValue();
        if (!meetingTime) {
            mini.alert("请选择“会议时间”");
            return;
        }
        var planListGridData = planListGrid.data;
        var planListGridDataJson = mini.encode(planListGridData);
        $.ajax({
            url: jsUseCtxPath + "/zhgl/core/hygl/saveMeetingPlan.do?meetingId=" + meetingId,
            type: 'POST',
            contentType: 'application/json',
            data: planListGridDataJson,
            success: function (returnData) {
                if (returnData && returnData.message) {
                    if (returnData.success) {
                        planListGrid.reload();
                    }
                }
            }
        });
        var postData = {};
        postData.id = $("#id").val();
        postData.meetingNo = mini.get("meetingNo").getValue();
        postData.company = mini.get("company").getValue();
        postData.applyId = mini.get("apply").getValue();
        postData.applyName = mini.get("apply").getText();
        postData.meetingOrgDepId = mini.get("meetingOrgDepId").getValue();
        postData.meetingOrgUserId = mini.get("meetingOrgUserId").getValue();
        postData.meetingUserIds = mini.get("meetingUserIds").getValue();
        postData.meetingTime = mini.get("meetingTime").getText();
        postData.meetingPlace = mini.get("meetingPlace").getValue();
        postData.meetingModelId = mini.get("meetingModel").getValue();
        postData.meetingTheme = mini.get("meetingTheme").getValue();
        postData.contentAndConclusion = $.trim(mini.get("contentAndConclusion").getValue());
        postData.planAndResult = $.trim(mini.get("planAndResult").getValue());
        postData.recordStatus = '草稿';
        var meetingModelData = mini.get("meetingModel").getSelected();
        if (meetingModelData) {
            postData.meetingModelDescp = meetingModelData.key;
        }
        $.ajax({
            url: jsUseCtxPath + "/zhgl/core/hygl/saveMeetingData.do",
            type: 'POST',
            contentType: 'application/json',
            data: mini.encode(postData),
            success: function (returnData) {
                if (returnData && returnData.message) {
                    mini.alert(returnData.message, '提示', function () {
                        if (returnData.success) {
                            var url = jsUseCtxPath + "/zhgl/core/hygl/editPage.do?meetingId=" + returnData.data + "&action=edit";
                            window.location.href = url;
                        }
                    });
                }
            }
        });
    }

    //..
    function commitMeeting() {
        var postData = {};
        postData.id = $("#id").val();
        postData.meetingNo = mini.get("meetingNo").getValue();
        postData.applyId = mini.get("apply").getValue();
        postData.applyName = mini.get("apply").getText();
        postData.company = mini.get("company").getValue();
        postData.meetingOrgDepId = mini.get("meetingOrgDepId").getValue();
        postData.meetingOrgUserId = mini.get("meetingOrgUserId").getValue();
        postData.meetingUserIds = mini.get("meetingUserIds").getValue();
        postData.meetingTime = mini.get("meetingTime").getText();
        postData.meetingPlace = mini.get("meetingPlace").getValue();
        postData.meetingModelId = mini.get("meetingModel").getValue();
        var meetingModelData = mini.get("meetingModel").getSelected();
        if (meetingModelData) {
            postData.meetingModelDescp = meetingModelData.key;
        }
        postData.meetingTheme = mini.get("meetingTheme").getValue();
        postData.contentAndConclusion = $.trim(mini.get("contentAndConclusion").getValue());
        postData.planAndResult = $.trim(mini.get("planAndResult").getValue());
        postData.recordStatus = '已提交';
        //检查必填项
        var checkResult = commitValidCheck(postData);
        if (!checkResult.success) {
            mini.alert(checkResult.reason);
            return;
        }
        var planListGridData = planListGrid.getData();
        if (planListGridData.length == 0) {
            mini.alert("请添加会议纪要分解信息");
            return;
        }
        if (planListGridData.length > 0) {
            for (var i = 0; i < planListGridData.length; i++) {
                if (planListGridData[i].meetingContent == undefined || planListGridData[i].meetingContent == "") {
                    mini.alert("请添加会议纪要描述");
                    return;
                } else if (planListGridData[i].meetingPlanRespUserIds == undefined || planListGridData[i].meetingPlanRespUserIds == "") {
                    mini.alert("请添加第一责任人");
                    return;
                } else if (planListGridData[i].meetingPlanEndTime == undefined || planListGridData[i].meetingPlanEndTime == "") {
                    mini.alert("请添加预计完成时间");
                    return;
                }
            }
        }
        planListGrid.validate();
        if (!planListGrid.isValid()) {
            var error = planListGrid.getCellErrors()[0];
            planListGrid.beginEditCell(error.record, error.column);
            mini.alert(error.column.header + error.errorText);
            return;
        }
        var planListGridData = planListGrid.data;
        var planListGridDataJson = mini.encode(planListGridData);
        $.ajaxSettings.async = false;
        $.ajax({
            url: jsUseCtxPath + "/zhgl/core/hygl/saveMeetingPlan.do?meetingId=" + meetingId,
            type: 'POST',
            contentType: 'application/json',
            data: planListGridDataJson,
            success: function (returnData) {
                if (returnData && returnData.message) {
                    if (returnData.success) {
                        planListGrid.reload();
                    }
                }
            }
        });
        mini.confirm("提交后不可修改,请确保会议纪要已分解完成,系统将根据会议纪要自动创建跟踪流程", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                showLoading();
                $.ajax({
                    url: jsUseCtxPath + "/zhgl/core/hygl/commitMeetingData.do",
                    type: 'POST',
                    contentType: 'application/json',
                    data: mini.encode(postData),
                    success: function (returnData) {
                        if (returnData && returnData.message) {
                            mini.alert(returnData.message, '提示', function () {
                                if (returnData.success) {
                                    window.close();
                                }
                            });
                        }
                    },
                    complete: function () {
                        hideLoading();
                    }
                });
            }
        });
        $.ajaxSettings.async = true;
    }

    //..
    function feedbackMeeting() {
        var postData = {};
        postData.id = $("#id").val();
        postData.meetingNo = mini.get("meetingNo").getValue();
        postData.company = mini.get("company").getValue();
        postData.applyId = mini.get("apply").getValue();
        postData.applyName = mini.get("apply").getText();
        postData.meetingOrgDepId = mini.get("meetingOrgDepId").getValue();
        postData.meetingOrgUserId = mini.get("meetingOrgUserId").getValue();
        postData.meetingUserIds = mini.get("meetingUserIds").getValue();
        postData.meetingTime = mini.get("meetingTime").getText();
        postData.meetingPlace = mini.get("meetingPlace").getValue();
        postData.meetingModelId = mini.get("meetingModel").getValue();
        postData.meetingTheme = mini.get("meetingTheme").getValue();
        postData.contentAndConclusion = $.trim(mini.get("contentAndConclusion").getValue());
        postData.planAndResult = $.trim(mini.get("planAndResult").getValue());
        postData.recordStatus = '会议已总结';
        postData.meetingSum = mini.get("meetingSum").getValue();
        postData.isfinish = mini.get("isfinish").getValue();
        //检查必填项
        var checkResult = feedbackValidCheck(postData);
        if (!checkResult.success) {
            mini.alert(checkResult.reason);
            return;
        }
        mini.confirm("保存后无法修改,确认提交？", "提示", function (action) {
                if (action != 'ok') {
                    return;
                } else {
                    $.ajax({
                        url: jsUseCtxPath + "/zhgl/core/hygl/feedbackMeetingData.do",
                        type: 'POST',
                        contentType: 'application/json',
                        data: mini.encode(postData),
                        success: function (returnData) {
                            if (returnData && returnData.message) {
                                mini.alert(returnData.message, '提示', function () {
                                    if (returnData.success) {
                                        window.close();
                                    }
                                });
                            }
                        }
                    });
                }
            }
        );
    }

    //..
    function feedbackValidCheck(postData) {
        var checkResult = {};
        if (!postData.isfinish) {
            checkResult.success = false;
            checkResult.reason = '请选择会议是否完成！';
            return checkResult;
        }
        if (!postData.meetingSum) {
            checkResult.success = false;
            checkResult.reason = '请填写会议总结！';
            return checkResult;
        }
        checkResult.success = true;
        return checkResult;
    }

    //..
    function commitValidCheck(postData) {
        var checkResult = {};
        if (!postData.meetingOrgDepId) {
            checkResult.success = false;
            checkResult.reason = '请选择部门！';
            return checkResult;
        }
        if (!postData.meetingOrgUserId) {
            checkResult.success = false;
            checkResult.reason = '请选择组织者！';
            return checkResult;
        }
        if (!postData.meetingUserIds) {
            checkResult.success = false;
            checkResult.reason = '请选择参会人员！';
            return checkResult;
        }
        if (!postData.meetingTime) {
            checkResult.success = false;
            checkResult.reason = '请选择会议时间！';
            return checkResult;
        }
        if (!postData.meetingPlace) {
            checkResult.success = false;
            checkResult.reason = '请填写会议地点！';
            return checkResult;
        }
        if (!postData.meetingModelId) {
            checkResult.success = false;
            checkResult.reason = '请选择会议类型！';
            return checkResult;
        }
        if (!postData.meetingTheme) {
            checkResult.success = false;
            checkResult.reason = '请填写会议主题！';
            return checkResult;
        }
        if (!postData.contentAndConclusion) {
            checkResult.success = false;
            checkResult.reason = '请填写会议概要！';
            return checkResult;
        }
        // if (!postData.planAndResult) {
        //     checkResult.success = false;
        //     checkResult.reason = '请填写采取的计划、执行结果！';
        //     return checkResult;
        // }
        var fileListGrid = $.trim(mini.get("fileListGrid").getData());
        if (!fileListGrid) {
            checkResult.success = false;
            checkResult.reason = '请上传签字版会议纪要！';
            return checkResult;
        }
        checkResult.success = true;
        return checkResult;
    }

    //..
    function uploadMeetingFile() {
        var meetingId = mini.get("id").getValue();
        if (!meetingId) {
            mini.alert("请先点击‘保存草稿’进行表单创建！")
            return;
        }
        mini.open({
            title: "文件上传",
            url: jsUseCtxPath + "/zhgl/core/hygl/openUploadWindow.do?meetingId=" + meetingId,
            width: 800,
            height: 350,
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

    //..
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


    function copyOtherResp() {
        var otherRespId = mini.get("meetingUserIds").getValue();
        var otherRespName = mini.get("meetingUserIds").getText();
        if (otherRespId) {
            var rows = planListGrid.getData();
            for (var i = 0, l = rows.length; i < l; i++) {
                var r = rows[i];
                planListGrid.updateRow(r,{
                    otherPlanRespUserIds:otherRespId,
                    otherPlanRespUserIds_name:otherRespName});
            }
        }
    }

    //..
    function addMeetingPlan() {
        var meetingId = mini.get("id").getValue();
        if (!meetingId) {
            mini.alert("请先点击‘保存草稿’进行计划创建！");
            return;
        }
        var newRow = {}
        newRow.id = "";
        newRow.meetingId = mini.get("id").getValue();
        newRow.meetingContent = "";
        newRow.meetingPlanRespUserIds = "";
        newRow.otherPlanRespUserIds = "";
        newRow.meetingPlanEndTime = "";
        newRow.meetingPlanCompletion = "";
        newRow.isComplete = "false";
        addRowGrid("planListGrid", newRow);
    }

    //..
    function deleteMeetingPlan() {
        var row = planListGrid.getSelected();
        if (!row) {
            mini.alert("请选中一条记录");
            return;
        }
        if (row.id == "") {
            delRowGrid("planListGrid");
            return;
        }
        mini.confirm("确定删除选中记录？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                var id = row.id;
                _SubmitJson({
                    url: jsUseCtxPath + "/zhgl/core/hygl/deleteOneMeetingPlan.do",
                    method: 'POST',
                    data: {id: id, meetingId: meetingId},
                    success: function (returnData) {
                        if (returnData) {
                            if (returnData && returnData.message) {
                                mini.alert(returnData.message, '提示', function () {
                                    if (returnData.success) {
                                        planListGrid.reload();
                                    }
                                });
                            }
                        }
                    }
                });
            }
        });
    }

    //..
    function saveMeetingPlan() {
        var meetingId = mini.get("id").getValue();
        if (!meetingId) {
            mini.alert("请先点击‘保存草稿’进行计划创建！");
            return;
        }
        planListGrid.validate();
        if (!planListGrid.isValid()) {
            var error = planListGrid.getCellErrors()[0];
            planListGrid.beginEditCell(error.record, error.column);
            mini.alert(error.column.header + error.errorText);
            return;
        }

        var postData = planListGrid.data;
        var postDataJson = mini.encode(postData);
        $.ajax({
            url: jsUseCtxPath + "/zhgl/core/hygl/saveMeetingPlan.do?meetingId=" + meetingId,
            type: 'POST',
            contentType: 'application/json',
            data: postDataJson,
            success: function (returnData) {
                if (returnData && returnData.message) {
                    mini.alert(returnData.message, '提示', function () {
                        if (returnData.success) {
                            planListGrid.reload();
                        }
                    });
                }
            }
        });
    }

    //..
    function isCompleteRenderer(e) {
        var record = e.record;
        var isComplete = record.isComplete;
        var arr = [{'key': 'true', 'value': '是', 'css': 'green'},
            {'key': 'false', 'value': '否', 'css': 'red'}];
        return $.formatItemValue(arr, isComplete);
    }

    //..
    function onCellValidation(e) {
        if (e.field == 'meetingContent' && (!e.value || e.value == '')) {
            e.isValid = false;
            e.errorText = '不能为空！';
        }
        if (e.field == 'meetingPlanRespUserIds' && (!e.value || e.value == '')) {
            e.isValid = false;
            e.errorText = '不能为空！';
        }
        if (e.field == 'meetingPlanEndTime' && (!e.value || e.value == '')) {
            e.isValid = false;
            e.errorText = '不能为空！';
        }
    }

    function showLoading() {
        $("#loading").css('display', '');
        $("#content").css('display', 'none');
        $("#toolBar").css('display', 'none');
    }

    function hideLoading() {
        $("#loading").css('display', 'none');
        $("#content").css('display', '');
        $("#toolBar").css('display', '');
    }
</script>
</body>
</html>
