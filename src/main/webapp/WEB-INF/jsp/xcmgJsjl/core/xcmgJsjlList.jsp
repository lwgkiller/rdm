<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>外部会议列表管理</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/xcmgJsjl/xcmgJsjlList.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <input class="mini-hidden" id="deptName" name="deptName"/>
            <input class="mini-hidden" id="recordStatus" name="recordStatus"/>
            <input class="mini-hidden" id="dimensionName" name="dimensionName"/>
            <ul>
                <li style="margin-right: 15px"><span class="text" style="width:auto">组织部门:</span>
                    <input id="deptId" name="deptId" class="mini-dep rxc" plugins="mini-dep"
                           data-options="{'single':'true','config':{'type':'','grouplevel':'','groupid':'','groupidText':''}}"
                           style="width:160px;height:34px" allowinput="false" label="部门" textname="deptName" length="500"
                           maxlength="500" minlen="0" single="true" required="false" initlogindep="false" showclose="false"
                           mwidth="160" wunit="px" mheight="34" hunit="px"/>
                </li>

                <li style="margin-right: 15px"><span class="text" style="width:auto">会议类型: </span>
                    <input id="dimension" name="dimensionId" class="mini-combobox" style="width:150px;"
                           textField="dimensionName" valueField="id" emptyText="请选择..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."/>
                </li>

                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">交流单位: </span>
                    <input class="mini-textbox" id="communicateCompany" name="communicateCompany"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">交流时间</span>:<input id="communicateStartTime" name="communicateStartTime"
                                                                             class="mini-datepicker" format="yyyy-MM-dd" style="width:120px"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text-to" style="width:auto">至: </span><input id="communicateEndTime" name="communicateEndTime"
                                                                              class="mini-datepicker" format="yyyy-MM-dd" style="width:120px"/>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="addJsjl()">新增</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="removeJsjlRow()">删除</a>
                </li>
            </ul>
        </form>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="jsjlListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" allowCellWrap="true" showCellTip="true"
         url="${ctxPath}/jsjl/core/dataListQuery.do?scene=${scene}" idField="id" allowCellEdit="true" allowCellSelect="true"
         multiSelect="true" showColumnsMenu="false" sizeList="[5,10,30,50,100]" pageSize="10" allowAlternating="true"
         pagerButtons="#pagerButtons" autoLoad="false">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div type="indexcolumn" width="40" headerAlign="center" align="center">序号</div>
            <div name="action" cellCls="actionIcons" width="80" headerAlign="center" align="left" renderer="onActionRenderer" cellStyle="padding:0;">
                操作
            </div>
            <div field="dimensionName" width="100" headerAlign="center" align="center" renderer="render">会议类型</div>
            <div field="meetingNo" width="130" headerAlign="center" align="center" renderer="render">纪要编号</div>
            <div field="deptName" width="80" headerAlign="center" align="center" renderer="render">组织部门</div>
            <div width="80" field="receiveUserName" headerAlign="center" align="center" renderer="render">业务接待人</div>
            <div field="meetingUserNames" width="110" headerAlign="center" align="center" renderer="renderMeetingUserNames">参会人员</div>
            <div field="communicateTime" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" width="95" headerAlign="center">交流时间</div>
            <div field="communicateRoom" headerAlign="center" align="center" width="90" renderer="render">地点</div>
            <div field="communicateCompany" width="90" headerAlign="center" align="center" renderer="render">交流单位</div>
            <div field="contentAndConclusion" width="350" headerAlign="center" align="left" renderer="renderContent">交流内容、结论</div>
            <div field="planAndResult" width="350" headerAlign="center" align="left" renderer="renderPlan">采取的计划、执行结果</div>
            <div field="recordStatus" width="60" headerAlign="center" align="center" renderer="render">状态</div>
            <div field="creator" width="60" headerAlign="center" align="center" renderer="render">创建人</div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var jsjlListGrid = mini.get("jsjlListGrid");
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var communicateStartTime = "${communicateStartTime}";
    var communicateEndTime = "${communicateEndTime}";
    var deptName = "${deptName}";
    var dimensionName = "${dimensionName}";
    var recordStatus = "${recordStatus}";
    var scene = "${scene}";

    $(function () {
        mini.get("communicateStartTime").setValue(communicateStartTime);
        mini.get("communicateEndTime").setValue(communicateEndTime);
        mini.get("deptName").setValue(deptName);
        mini.get("dimensionName").setValue(dimensionName);
        mini.get("recordStatus").setValue(recordStatus);
        searchFrm();
    });

    function onActionRenderer(e) {
        var record = e.record;
        var jsjlId = record.id;
        var recordStatus = record.recordStatus;
        var s = '<span  title="查看" onclick="detailJsjlRow(\'' + jsjlId + '\')">查看</span>';
        if (recordStatus == '草稿' && (currentUserId == record.CREATE_BY_ || currentUserNo == 'admin' || currentUserNo == 'zhujia')) {
            s += '<span  title="编辑" onclick="editJsjlRow(\'' + jsjlId + '\')">编辑</span>';
        } else if (recordStatus != '草稿' && (currentUserId == record.CREATE_BY_ || currentUserNo == 'admin' || currentUserNo == 'zhujia')) {
            s += '<span  title="会议反馈" onclick="feedBackJsjlRow(\'' + jsjlId + '\')">会议反馈</span>';
        }
        //无删除权限的按钮变灰色
        if (currentUserNo == 'admin' || currentUserNo == 'zhujia' || (recordStatus == '草稿' && currentUserId == record.CREATE_BY_)) {
            s += '<span  title="删除" onclick="removeJsjlRow(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
        }

        return s;
    }

    jsjlListGrid.on("rowdblclick", function (e) {
        var record = e.record;
        var jsjlId = record.id;
        var recordStatus = record.recordStatus;
        if (recordStatus == '草稿' && (currentUserId == record.CREATE_BY_ || currentUserNo == 'admin' || currentUserNo == 'zhujia')) {
            editJsjlRow(jsjlId);
        } else if (recordStatus != '草稿' && (currentUserId == record.CREATE_BY_ || currentUserNo == 'admin' || currentUserNo == 'zhujia')) {
            feedBackJsjlRow(jsjlId);
        } else {
            detailJsjlRow(jsjlId);
        }
    });

    //所有非特殊文本都变成这个格式
    function render(e) {
        if (e.value != null && e.value != "") {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            return html;
        }
    }
</script>
<redxun:gridScript gridId="jsjlListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>