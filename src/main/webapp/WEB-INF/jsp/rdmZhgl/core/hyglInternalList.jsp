<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>内部会议列表管理</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/rdmZhgl/hyglInternalList.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <input class="mini-hidden" id="meetingOrgDepName" name="meetingOrgDepName"/>
            <input class="mini-hidden" id="recordStatus" name="recordStatus"/>
            <ul>
                <li style="margin-right: 15px"><span class="text" style="width:auto">会议类型: </span>
                    <input id="meetingModel" name="meetingModel"
                           class="mini-combobox" style="width:98%"
                           url="${ctxPath}/zhgl/core/hyglInternal/getDicByParam.do?dicKey=meetingManageInternal&descp=${scene}"
                           valueField="dicId" textField="value"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">会议主题: </span>
                    <input class="mini-textbox" id="meetingTheme" name="meetingTheme"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">组织者: </span>
                    <input class="mini-textbox" id="meetingOrgUserName" name="meetingOrgUserName"/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">组织部门:</span>
                    <input id="meetingOrgDepId" name="meetingOrgDepId" class="mini-dep rxc" plugins="mini-dep"
                           data-options="{'single':'true','config':{'type':'','grouplevel':'','groupid':'','groupidText':''}}"
                           style="width:160px;height:34px" allowinput="false" label="部门" textname="meetingOrgDepName" length="500"
                           maxlength="500" minlen="0" single="true" required="false" initlogindep="false" showclose="false"
                           mwidth="160" wunit="px" mheight="34" hunit="px"/>
                </li>

                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">会议时间</span>
                    <%--todo:带时间无法通过yyyy-MM-dd字符串setValue--%>
                    <%--<input id="meetingTimeBegin" name="meetingTimeBegin" class="mini-datepicker"--%>
                    <%--format="yyyy-MM-dd HH:mm:ss" timeFormat="HH:mm:ss" showTime="true"/>--%>
                    <input id="meetingTimeBegin" name="meetingTimeBegin" class="mini-datepicker" format="yyyy-MM-dd" style="width:120px"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text-to" style="width:auto">至</span>
                    <%--<input id="meetingTimeEnd" name="meetingTimeEnd" class="mini-datepicker"--%>
                    <%--format="yyyy-MM-dd HH:mm:ss" timeFormat="HH:mm:ss" showTime="true"/>--%>
                    <input id="meetingTimeEnd" name="meetingTimeEnd" class="mini-datepicker" format="yyyy-MM-dd" style="width:120px"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">地点: </span>
                    <input class="mini-textbox" id="meetingPlace" name="meetingPlace"/>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="addMeeting()">新增</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="removeMeeting()">删除</a>
                </li>
            </ul>
        </form>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="meetingListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" allowCellWrap="true" showCellTip="true"
         url="${ctxPath}/zhgl/core/hyglInternal/dataListQuery.do?scene=${scene}" idField="id" allowCellEdit="true" allowCellSelect="true"
         multiSelect="true" showColumnsMenu="false" sizeList="[5,10,30,50,100]" pageSize="10" allowAlternating="true"
         pagerButtons="#pagerButtons" autoLoad="false">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div type="indexcolumn" width="40" headerAlign="center" align="center">序号</div>
            <div name="action" cellCls="actionIcons" width="80" headerAlign="center" align="left" renderer="onActionRenderer" cellStyle="padding:0;">
                操作
            </div>
            <div field="meetingModel" width="80" headerAlign="center" align="center" renderer="render">会议类型</div>
            <div field="meetingNo" width="122" headerAlign="center" align="center" renderer="render">纪要编号</div>
            <div field="meetingOrgDepName" width="95" headerAlign="center" align="center" renderer="render">组织部门</div>
            <div field="meetingTime" width="92" headerAlign="center" align="center" dateFormat="yyyy-MM-dd HH:mm:ss">会议时间</div>
            <div field="meetingOrgUserName" width="60" headerAlign="center" align="center" renderer="render">组织者</div>
            <div field="meetingUserNames" width="180" headerAlign="center" align="left" renderer="renderMeetingUserNames">参会人员</div>
            <div field="meetingPlace" width="60" headerAlign="center" align="center" renderer="render">地点</div>
            <div field="meetingTheme" width="80" headerAlign="center" align="center" renderer="render">主题</div>
            <div field="contentAndConclusion" width="350" headerAlign="center" align="left" renderer="renderContent">内容、结论</div>
            <div field="planAndResult" width="350" headerAlign="center" align="left" renderer="renderPlan">采取的计划、执行结果</div>
<%--            <div field="meetingFileNames" width="200" headerAlign="center" align="center" renderer="render">会议产物</div>--%>
            <div field="recordStatus" width="60" headerAlign="center" align="center" renderer="render">状态</div>
            <div field="creator" width="60" headerAlign="center" align="center" renderer="render">创建人</div>
            <%--<div field="CREATE_TIME_" width="65" headerAlign="center" align="center" dateFormat="yyyy-MM-dd HH:mm:ss">创建时间</div>--%>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var meetingListGrid = mini.get("meetingListGrid");
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var meetingTimeBegin = "${meetingTimeBegin}";
    var meetingTimeEnd = "${meetingTimeEnd}";
    var meetingOrgDepName = "${meetingOrgDepName}";
    var meetingModel = "${meetingModel}";
    var recordStatus = "${recordStatus}";
    var scene="${scene}";

    $(function () {
        mini.get("meetingTimeBegin").setValue(meetingTimeBegin);
        mini.get("meetingTimeEnd").setValue(meetingTimeEnd);
        mini.get("meetingOrgDepName").setValue(meetingOrgDepName);
        mini.get("meetingModel").setValue(meetingModel);
        mini.get("recordStatus").setValue(recordStatus);
        searchFrm();
    });

    function onActionRenderer(e) {
        var record = e.record;
        var meetingId = record.id;
        var recordStatus = record.recordStatus;
        var s = '<span  title="查看" onclick="detailMeeting(\'' + meetingId + '\')">查看</span>';
        if (recordStatus == '草稿' && (currentUserId == record.CREATE_BY_ || currentUserNo == 'admin' || currentUserNo == 'zhujia')) {
            s += '<span  title="编辑" onclick="editMeeting(\'' + meetingId + '\')">编辑</span>';
        } else if (recordStatus != '草稿') {
            s += '<span  title="会议反馈" onclick="feedBackMeeting(\'' + meetingId + '\')">会议反馈</span>';
        }
        //无删除权限的按钮变灰色
        if (currentUserNo == 'admin' || currentUserNo == 'zhujia' || (recordStatus == '草稿' && currentUserId == record.CREATE_BY_)) {
            s += '<span  title="删除" onclick="removeMeeting(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
        }
        return s;
    }

    meetingListGrid.on("rowdblclick", function (e) {
        var record = e.record;
        var meetingId = record.id;
        var recordStatus = record.recordStatus;
        if (recordStatus == '草稿' && (currentUserId == record.CREATE_BY_ || currentUserNo == 'admin' || currentUserNo == 'zhujia')) {
            editMeeting(meetingId);
        } else if (recordStatus != '草稿') {
            feedBackMeeting(meetingId);
        } else {
            detailMeeting(meetingId);
        }
    });

    //所有非特殊文本都变成这个格式
    function render(e) {
        if (e.value != null && e.value != "") {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            return html;
        }
    }

    function renderContent(e) {
        var record = e.record;
        var html = "<div style='line-height: 20px;height:150px;overflow: auto' >";
        var contentAndConclusion = record.contentAndConclusion;
        if (contentAndConclusion == null) {
            contentAndConclusion = "";
        }
        html += '<span style="white-space:pre-wrap" >' + contentAndConclusion + '</span>';
        html += '</div>'
        return html;
    }

    function renderPlan(e) {
        var record = e.record;
        var html = "<div style='line-height: 20px;height:150px;overflow: auto' >";
        var planAndResult = record.planAndResult;
        if (planAndResult == null) {
            planAndResult = "";
        }
        html += '<span style="white-space:pre-wrap" >' + planAndResult + '</span>';
        html += '</div>';
        return html;
    }
</script>
<redxun:gridScript gridId="meetingListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>