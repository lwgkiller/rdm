<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>考勤列表</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/portrait/attendance/portraitAttendanceList.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px"><span class="text" style="width:auto">所属部门: </span><input class="mini-textbox" style="width: 150px" name="deptName"/></li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">姓名: </span><input class="mini-textbox" style="width: 150px" name="userName"/></li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">年月: </span><input id="yearMonthPick" class="mini-monthpicker" style="width: 150px" name="yearMonth"/></li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                </li>
            </ul>
        </form>
        <ul class="toolBtnBox">
            <div style="display: inline-block" class="separator"></div>
<%--            <a id="addButton" class="mini-button" plain="true" img="${ctxPath}/scripts/mini/miniui/res/images/add.png" onclick="addRow()">新增</a>--%>
<%--            <a id="delButton" class="mini-button" style="margin-left: 10px"  img="${ctxPath}/scripts/mini/miniui/res/images/cancel.png" onclick="removeRow()">删除</a>--%>
            <a id="asyncButton" class="mini-button" style="margin-left: 10px" img="${ctxPath}/scripts/mini/miniui/res/images/xhtml.png"
               onclick="openEditWindow()">数据同步</a>
            <div style="display: inline-block" class="separator"></div>

        </ul>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="attendanceListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         url="${ctxPath}/portrait/attendance/attendanceList.do" idField="id" sortField="score" sortOrder="desc"
         multiSelect="true" showColumnsMenu="false" sizeList="[10,20,50,100,200]" pageSize="15" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div type="indexcolumn" align="center" headerAlign="center" width="30">序号</div>
            <div name="action" cellCls="actionIcons" width="60" headerAlign="center" align="center"
                 renderer="onActionRenderer" cellStyle="padding:0;">操作
            </div>
            <div field="deptName" width="80" headerAlign="center" align="center" allowSort="true">所属部门</div>
            <div field="userName" width="80" headerAlign="center" align="center" allowSort="true">姓名</div>
            <div field="yearMonth" width="120" headerAlign="center" align="center" allowSort="true">年月</div>
            <div field="attendanceRank" width="80" headerAlign="center" align="center" allowSort="true"  renderer="onWSwitchRank">排名占比</div>
            <div field="score" width="80" headerAlign="center" align="center" allowSort="true">得分</div>
            <div field="CREATE_TIME_" width="80" headerAlign="center" align="center" allowSort="false">同步日期</div>
        </div>
    </div>
</div>
<div id="monthSelectedWindow" title="选择需要同步的出勤月份" class="mini-window" style="width:500px;height:200px;"
     showModal="true" showFooter="false" allowResize="true">
    <div class="topToolBar" style="text-align:right;">
        <a  class="mini-button"  onclick="asyncData()">同步</a>
    </div>
    <div class="mini-fit" style="font-size: 14px">
        <form id="monthForm" method="post">
            <table cellspacing="1" cellpadding="0" class="table-detail column-six grey">
                <tr>
                    <td align="center" style="width: 10%">考勤月份<span style="color: red">*</span>：</td>
                    <td align="center" style="width: 20%">
                        <input class="mini-monthpicker" required style="width: 150px" name="yearMonth"/>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var attendanceListGrid = mini.get("attendanceListGrid");
    var rankList = getDics("attendanceRank");
    var monthSelectedWindow=mini.get("monthSelectedWindow");
    var monthForm = new mini.Form("#monthForm");
    var permission = ${permission};
    if(!permission){
        mini.get("addButton").setEnabled(false);
        mini.get("delButton").setEnabled(false);
        mini.get("asyncButton").setEnabled(false);
    }
    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var id = record.id;
        var s = '<span  title="查看" onclick="viewForm(\'' + id + '\',\'view\')">查看</span>';
        if(permission){
            s += '<span  title="编辑" onclick="viewForm(\'' + id + '\',\'edit\')">编辑</span>';
        }
        return s;
    }
    function onWSwitchRank(e) {
        var record = e.record;
        var rank = record.attendanceRank;
        var rankText = '';
        for(var i=0;i<rankList.length;i++){
            if(rankList[i].key_==rank){
                rankText = rankList[i].text;
                break
            }
        }
        return rankText;
    }
    function getDics(dicKey) {
        let resultDate = [];
        $.ajax({
            async:false,
            url: __rootPath + '/sys/core/commonInfo/getDicItem.do?dicType='+dicKey,
            type: 'GET',
            contentType: 'application/json',
            success: function (data) {
                if (data.code==200) {
                    resultDate = data.data;
                }
            }
        });
        return resultDate;
    }
</script>
<redxun:gridScript gridId="attendanceListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
