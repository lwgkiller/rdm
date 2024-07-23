x
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>内部会议管理</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmZhgl/hyglInternalEdit.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>

<body>
<div id="toolBar" class="topToolBar">
    <div>
        <a id="saveMeetingDraft" class="mini-button" style="display: none" onclick="saveMeetingDraft()">保存草稿</a>
        <a id="commitMeeting" class="mini-button" style="display: none" onclick="commitMeeting()">提交</a>
        <a id="feedbackMeeting" class="mini-button" style="display: none" onclick="feedbackMeeting()">保存</a>
        <a id="closeWindow" class="mini-button btn-red" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin:0 auto; width: 100%;padding: 0">
        <form id="meetingForm" method="post">
            <input class="mini-hidden" id="id" name="id"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0" style="height: 100%;width: 100%">
                <caption style="font-weight: bold;font-size: 20px !important;">内部会议管理</caption>
                <tr>
                    <td style="text-align: center;width: 15%">组织部门：</td>
                    <td style="min-width:170px">
                        <input id="meetingOrgDepId" name="meetingOrgDepId" class="mini-dep rxc" plugins="mini-dep"
                               data-options="{'single':'true','config':{'type':'','grouplevel':'','groupid':'','groupidText':''}}"
                               style="width:98%;height:34px" allowinput="false" label="会议组织部门" textname="meetingOrgDepName" length="500"
                               maxlength="500" minlen="0" single="true" required="false" initlogindep="false" showclose="false"
                               mwidth="160" wunit="px" mheight="34" hunit="px"/>
                    </td>
                    <td style="text-align: center;width: 15%">组织者：</td>
                    <td style="min-width:170px">
                        <input id="meetingOrgUserId" name="meetingOrgUserId" textname="meetingOrgUserName" class="mini-user rxc"
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
                               timeFormat="HH:mm:ss" showTime="true" showOkButton="true" showClearButton="false" style="width:98%;"/>
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
                               url="${ctxPath}/zhgl/core/hyglInternal/getDicByParam.do?dicKey=meetingManageInternal&descp=${scene}"
                               valueField="dicId" textField="key"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">会议主题：</td>
                    <td style="min-width:170px">
                        <input id="meetingTheme" name="meetingTheme" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%">是否钉钉发送参会通知：</td>
                    <td style="min-width:170px">
                        <input id="sendNotice" class="mini-checkbox" style="width:98%;"/>
                        <span style="color: #ef1b01">此选项仅在“提交”过程触发，“保存草稿”和“保存”过程无效</span>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">是否生成会议纪要编号：</td>
                    <td style="min-width:170px">
                        <input id="generationNo" name="generationNo" class="mini-checkbox" style="width:98%;"/>
                        <%--todo:改了--%>
                        <span style="color: #ef1b01">此选项会在“提交”和“保存”过程触发，“保存草稿”过程无效</span>
                    </td>
                    <td style="text-align: center;width: 15%">会议纪要编号：</td>
                    <td style="min-width:170px">
                        <input id="meetingNo" name="meetingNo" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">会议内容、结论（1500字以内）：</td>
                    <td colspan="3">
						<textarea id="contentAndConclusion" name="contentAndConclusion" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:99.1%;height:150px;line-height:25px;"
                                  label="交流相关业务内容，达成的共识以及形成的结论" datatype="varchar" allowinput="true"
                                  emptytext="请输入交流相关业务内容，达成的共识以及形成的结论..." mwidth="80" wunit="%" mheight="200" hunit="px"></textarea>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">采取的计划、执行结果（1500字以内）：</td>
                    <td colspan="3">
						<textarea id="planAndResult" name="planAndResult" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:99.1%;height:150px;line-height:25px;"
                                  label="采取的行动计划及执行结果" datatype="varchar" allowinput="true"
                                  emptytext="请输入采取的行动计划及执行结果..." mwidth="80" wunit="%" mheight="200" hunit="px"></textarea>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;height: 180px">附件列表：</td>
                    <td colspan="3">
                        <div class="mini-toolbar" id="fileButtons" style="display: none">
                            <a id="uploadFile" class="mini-button" style="margin-bottom: 5px;margin-top: 5px" onclick="uploadMeetingFile">添加附件</a>
                        </div>
                        <div id="fileListGrid" class="mini-datagrid" style="width: 100%; height: 75%" allowResize="false"
                             idField="id" url="${ctxPath}/zhgl/core/hyglInternal/getMeetingFileList.do?meetingId=${meetingId}"
                             multiSelect="false" showPager="false" showColumnsMenu="false"
                             allowAlternating="true">
                            <div property="columns">
                                <div type="indexcolumn" headerAlign="center" align="center" width="20">序号</div>
                                <div field="fileName" align="center" headerAlign="center" width="150">文件名</div>
                                <div field="fileSize" align="center" headerAlign="center" width="60">文件大小</div>
                                <div field="fileDesc" align="center" headerAlign="center" width="100">备注说明</div>
                                <div field="action" width="80" headerAlign='center' align="center" renderer="operationRenderer">操作</div>
                            </div>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;height: 500px">任务分解及执行情况反馈：</td>
                    <td colspan="3">
                        <div class="mini-toolbar" id="planButtons" style="display: none">
                            <a id="addPlan" class="mini-button" style="margin-bottom: 5px;margin-top: 5px" onclick="addMeetingPlan">添加任务</a>
                            <a id="deletePlan" class="mini-button btn-red" style="margin-bottom: 5px;margin-top: 5px"
                               onclick="deleteMeetingPlan">删除任务</a>
                            <a class="mini-button" style="margin-right: 5px" plain="true" onclick="exportPlanBusiness()">导出</a>
                            <div style="display: inline-block" class="separator"></div>
                            <a id="savePlan" class="mini-button btn-yellow" style="margin-bottom: 5px;margin-top: 5px"
                               onclick="saveMeetingPlan">保存任务</a>
                            <span style="color: #ef1b01">(注意，添加任务和编辑任务后，请点击“保存任务”按钮！)</span>
                        </div>
                        <div id="planListGrid" class="mini-datagrid" style="width: 100%; height: 85%" allowResize="false" allowCellWrap="true"
                             idField="id" url="${ctxPath}/zhgl/core/hyglInternal/getMeetingPlanList.do?meetingId=${meetingId}"
                             multiSelect="false" showPager="false" showColumnsMenu="false" allowcelledit="true" allowcellselect="true"
                             allowAlternating="true" oncellvalidation="onCellValidation">
                            <div property="columns">
                                <div type="checkcolumn" width="20"></div>
                                <div type="indexcolumn" headerAlign="center" align="center" width="30">序号</div>
                                <div field="meetingContent" align="left" headerAlign="center" width="300" renderer="render">任务描述
                                    <input property="editor" class="mini-textarea"/>
                                </div>
                                <%--lwgkiller：此处在行内用mini-user组件，textname无效，默认会以name+"_name"作为textname--%>
                                <div field="meetingPlanRespUserIds" displayField="meetingPlanRespUserIds_name"
                                     align="center" headerAlign="center" width="160">主责人
                                    <input property="editor" class="mini-user rxc" plugins="mini-user" allowinput="false" single="false"
                                           mainfield="no" name="meetingPlanRespUserIds"/>
                                </div>
                                <div field="meetingPlanEndTime" align="center" headerAlign="center" width="100"
                                     dateFormat="yyyy-MM-dd">预计完成时间
                                    <input property="editor" class="mini-datepicker" format="yyyy-MM-dd"
                                           showTime="false" showOkButton="true" showClearButton="false"/>
                                </div>
                                <div field="meetingPlanCompletion" align="left" headerAlign="center" width="300" renderer="render">完成情况
                                    <input property="editor" class="mini-textarea"/>
                                </div>
                                <div field="isComplete" align="center" headerAlign="center" width="60" renderer="isCompleteRenderer">是否完成
                                    <input property="editor" class="mini-combobox" align="center" textField="key" valueField="value"
                                           data="[ {'key' : '是','value' : 'true'},{'key' : '否','value' : 'false'}]"/>
                                </div>
                            </div>
                        </div>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>

<!--导出Excel相关HTML-->
<form id="excelForm" action="${ctxPath}/zhgl/core/hyglInternal/exportPlanList.do" method="post" target="excelIFrame">
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
    var scene = "${scene}";

    function operationRenderer(e) {
        var record = e.record;
        var cellHtml = '';
        cellHtml = returnMeetingPreviewSpan(record.fileName, record.id, record.meetingId, coverContent);
        //和pdf预览用的同一个url
        var downLoadUrl = '/zhgl/core/hyglInternal/meetingPdfPreviewAndAllDownload.do';
        cellHtml += '&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downLoadFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.meetingId + '\',\'' + downLoadUrl + '\')">下载</span>';
        //增加删除按钮
        if (record.CREATE_BY_ == currentUserId && action != 'detail') {
            var deleteUrl = "/zhgl/core/hyglInternal/delMeetingFile.do"
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.meetingId + '\',\'' + deleteUrl + '\')">删除</span>';
        }
        return cellHtml;
    }

    //..
    function exportPlanBusiness() {
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

</script>
</body>
</html>
