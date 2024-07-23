<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title></title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<div class="mini-toolbar" >
    <ul class="toolBtnBox">
        <li style="float: left;margin-left: 0">
            <a class="mini-button" iconCls="icon-reload" onclick="refreshData()" plain="true">刷新</a>
        </li>
    </ul>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="workStatusGrid" class="mini-datagrid" style="width: 100%; height: 100%;"
         allowResize="false" url="${ctxPath}/rdmHome/core/rdmHomeTabContent.do?name=workStatus" autoLoad="true" onload="dataGridLoad"
         idField="userId" showPager="false" multiSelect="false" showColumnsMenu="true" allowAlternating="true" allowCellWrap="true"
    >
        <div property="columns">
            <div name="action" cellCls="actionIcons" headerAlign="center" align="center" width="85" renderer="onActionRenderer" cellStyle="padding:0;">操作</div>
            <div field="userName" headerAlign="center" align="center" width="85" >姓名</div>
            <div field="statusDesc"  width="140" headerAlign="center" align="left" allowSort="false">当前日程
            </div>
        </div>
    </div>
</div>


<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var grid=mini.get("workStatusGrid");

    function dataGridLoad(e) {
        window.parent.queryTabNums();
    }

    function refreshData() {
        grid.load();
    }

    function onActionRenderer(e) {
        var record = e.record;
        return '<span style="color: #409EFF;cursor: pointer " title="查看&编辑" onclick="processWorkStatus(\'' + record.userId +'\',\''+record.userName+'\')">查看&编辑</span>';
    }

    function processWorkStatus(leaderUserId,leaderUserName) {
        mini.open({
            url : "${ctxPath}/rdmHome/core/workStatusDetailPage.do?leaderUserName="+leaderUserName+"&leaderUserId="+leaderUserId,
            title : "工作状态",
            width : 1010,
            height : 500,
            ondestroy : function(action) {
                refreshData();
            }
        });
    }


</script>
</body>
</html>
