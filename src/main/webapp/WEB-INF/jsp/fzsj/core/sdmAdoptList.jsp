<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>SDM同步仿真任务列表</title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px"><span class="text" style="width:auto">仿真编号: </span><input class="mini-textbox" style="width: 150px" name="fzNumber"/></li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                </li>
            </ul>
        </form>
        <ul class="toolBtnBox">
        </ul>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="listGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         url="${ctxPath}/fzsj/core/sdm/sdmAdoptList.do" idField="id" sortField="score" sortOrder="desc"
         multiSelect="true" showColumnsMenu="false" sizeList="[10,20,50,100,200]" pageSize="15" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div type="indexcolumn" align="center" headerAlign="center" width="30">序号</div>
            <%--<div name="action" cellCls="actionIcons" width="60" headerAlign="center" align="center"--%>
                 <%--renderer="onActionRenderer" cellStyle="padding:0;">操作--%>
            <%--</div>--%>
            <div field="resultCode" width="50" headerAlign="center" align="center" allowSort="true">返回code</div>
            <div field="resultMessage" width="120" headerAlign="center" align="center" allowSort="true">返回信息</div>
            <div field="fzNumber" width="100" headerAlign="center" align="center" allowSort="true">仿真编号</div>
            <div field="questName" width="120" headerAlign="center" align="center" allowSort="true">任务名称</div>
            <div field="simuTaskId" width="150" headerAlign="center" align="center" allowSort="true">仿真任务ID</div>
            <div field="CREATE_TIME_" width="100" headerAlign="center" align="center" allowSort="false">推送日期</div>
        </div>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var listGrid = mini.get("listGrid");
    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var id = record.id;
        var resultCode = record.resultCode;
        var s = '';
        if(resultCode!=200){
            s = '<span  title="重新推送" onclick="viewForm(\'' + id + '\',\'view\')">重新推送</span>';
        }
        return s;
    }
</script>
<redxun:gridScript gridId="listGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
