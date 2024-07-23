<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>内部会议列表管理</title>
    <%@include file="/commons/list.jsp" %>
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
                           url="${ctxPath}/zhgl/core/hygl/getHyglType.do?"
                           valueField="id" textField="value"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">会议编号: </span>
                    <input class="mini-textbox" id="meetingNo" name="meetingNo"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">会议主题: </span>
                    <input class="mini-textbox" id="meetingTheme" name="meetingTheme"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">会议负责人: </span>
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
                    <input id="meetingTimeBegin" name="meetingTimeBegin" class="mini-datepicker" format="yyyy-MM-dd" style="width:120px"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text-to" style="width:auto">至</span>
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
                    <a class="mini-button" style="margin-right: 5px;" plain="true" onclick="downloadFile()">下载</a>
                </li>
            </ul>
        </form>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="meetingListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" allowCellWrap="true" showCellTip="true"
         url="${ctxPath}/zhgl/core/hygl/getHyglList.do?" idField="id" allowCellEdit="true" allowCellSelect="true"
         multiSelect="true" showColumnsMenu="false" sizeList="[5,10,30,50,100]" pageSize="10" allowAlternating="true"
         pagerButtons="#pagerButtons" autoLoad="false">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div type="indexcolumn" width="40" headerAlign="center" align="center">序号</div>
            <div name="action" cellCls="actionIcons" width="80" headerAlign="center" align="left" renderer="onActionRenderer" cellStyle="padding:0;">
                操作
            </div>
            <div field="meetingModel" width="80" headerAlign="center" align="center" renderer="render">会议类型</div>
            <div field="meetingNo" width="80" headerAlign="center" align="center" renderer="render">会议编号</div>
            <div field="meetingOrgDepName" width="80" headerAlign="center" align="center" renderer="render">组织部门</div>
            <div field="meetingTime" width="85" headerAlign="center" align="center" dateFormat="yyyy-MM-dd HH:mm:ss">会议时间</div>
            <div field="meetingOrgUserName" width="50" headerAlign="center" align="center" renderer="render">会议负责人</div>
            <div field="meetingUserNames" width="120" headerAlign="center" align="left" renderer="renderMeetingUserNames">参会人员</div>
            <div field="meetingPlace" width="50" headerAlign="center" align="center" renderer="render">地点</div>
            <div field="meetingTheme" width="100" headerAlign="center" align="center" renderer="render">主题</div>
            <div field="contentAndConclusion" width="200" headerAlign="center" align="left" renderer="renderContent">会议概要</div>
            <div field="recordStatus" width="70" headerAlign="center" align="center" renderer="onStatusRenderer">状态</div>
            <div field="applyName" width="60" headerAlign="center" align="center" renderer="render">会议记录人</div>
            <div field="CREATE_TIME_" width="70" headerAlign="center" align="center" renderer="render">创建时间</div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var meetingListGrid = mini.get("meetingListGrid");
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";

    $(function () {
        searchFrm();
    });

    function onActionRenderer(e) {
        var record = e.record;
        var meetingId = record.id;
        var recordStatus = record.recordStatus;
        var s = '<span  title="查看" onclick="detailMeeting(\'' + meetingId + '\')">查看</span>';
        if (recordStatus == '草稿' && (currentUserId == record.CREATE_BY_ || currentUserNo == 'admin' || currentUserNo == 'zhujia')) {
            s += '<span  title="编辑" onclick="editMeeting(\'' + meetingId + '\')">编辑</span>';
        }
        if (recordStatus=='纪要审批完成'&&currentUserId == record.meetingOrgUserId) {
            s += '<span  title="会议总结" onclick="feedBackMeeting(\'' + meetingId + '\')">会议总结</span>';
        }
        //无删除权限的按钮变灰色
        if (currentUserNo == 'admin'  || (recordStatus == '草稿' && currentUserId == record.CREATE_BY_)) {
            s += '<span  title="删除" onclick="removeMeeting(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
        }
        return s;
    }


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
    //..
    function renderMeetingUserNames(e) {
        var record = e.record;
        var html = "<div style='display:table-cell;vertical-align:middle;line-height: 20px;height:150px;overflow: auto;text-align: center' >";
        var meetingUserNames = record.meetingUserNames;
        if (meetingUserNames == null) {
            meetingUserNames = "";
        }
        html += '<span style="white-space:pre-wrap;" >' + meetingUserNames + '</span>';
        html += '</div>'
        return html;
    }
    //..
    function addMeeting() {
        var url = jsUseCtxPath + "/zhgl/core/hygl/editPage.do?meetingId=&action=add";
        var winObj = window.open(url, '');
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (meetingListGrid) {
                    meetingListGrid.reload();
                }
            }
        }, 1000);
    }
    //..
    function detailMeeting(meetingId) {
        var url = jsUseCtxPath + "/zhgl/core/hygl/editPage.do?meetingId=" + meetingId + "&action=detail";
        var winObj = window.open(url, '');
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (meetingListGrid) {
                    meetingListGrid.reload();
                }
            }
        }, 1000);
    }
    //..
    function editMeeting(meetingId) {
        var url = jsUseCtxPath + "/zhgl/core/hygl/editPage.do?meetingId=" + meetingId + "&action=edit";
        var winObj = window.open(url, '');
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (meetingListGrid) {
                    meetingListGrid.reload();
                }
            }
        }, 1000);
    }
    //..
    function feedBackMeeting(meetingId) {
        var url = jsUseCtxPath + "/zhgl/core/hygl/editPage.do?meetingId=" + meetingId + "&action=feedback";
        var winObj = window.open(url, '');
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (meetingListGrid) {
                    meetingListGrid.reload();
                }
            }
        }, 1000);
    }


    //..
    function removeMeeting(record) {
        var rows = [];
        if (record) {
            rows.push(record);
        } else {
            rows = meetingListGrid.getSelecteds();
        }
        if (rows.length <= 0) {
            mini.alert("请至少选中一条记录");
            return;
        }
        mini.confirm("确定删除选中记录？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                var rowIds = [];
                for (var i = 0, l = rows.length; i < l; i++) {
                    var r = rows[i];
                    if (currentUserNo == 'admin' || currentUserNo == 'zhujia' || (r.recordStatus == '草稿' && currentUserId == r.CREATE_BY_)) {
                        rowIds.push(r.id);
                    }
                }
                if (rowIds.length <= 0) {
                    mini.alert("仅可删除本人创建的尚未提交的数据！");
                    return;
                }
                _SubmitJson({
                    url: jsUseCtxPath + "/zhgl/core/hygl/deleteMeeting.do",
                    method: 'POST',
                    data: {ids: rowIds.join(',')},
                    success: function (data) {
                        if (data) {
                            mini.alert(data.message);
                            searchFrm();
                        }
                    }
                });
            }
        });
    }

    function downloadFile() {
        var rows = meetingListGrid.getSelecteds();
        if (rows.length <= 0|| rows.length>1) {
            mini.alert("请选中一条记录");
            return;
        }
        var rowIds = [];
        for (var i = 0, l = rows.length; i < l; i++) {
            var r = rows[i];
            rowIds.push(r.id);
        }
        var url = "/zhgl/core/hygl/downloadHyglWord.do";
        downLoad(rowIds, url);
    }
    function downLoad(formId, urlValue) {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + urlValue);
        var inputFormId = $("<input>");
        inputFormId.attr("type", "hidden");
        inputFormId.attr("name", "formId");
        inputFormId.attr("value", formId);
        $("body").append(form);
        form.append(inputFormId);
        form.submit();
        form.remove();
    }

    function onStatusRenderer(e) {
        var record = e.record;
        var status = record.recordStatus;
        var arr = [{'key': '草稿', 'value': '草稿', 'css': 'orange'},
            {'key': '已提交', 'value': '纪要审批中', 'css': 'green'},
            {'key': '纪要审批完成', 'value': '纪要审批完成', 'css': 'green'},
            {'key': '会议已总结', 'value': '会议已总结', 'css': 'blue'}
        ];

        return $.formatItemValue(arr, status);
    }
</script>
<redxun:gridScript gridId="meetingListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>