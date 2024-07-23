<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>合同签订</title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px"><span class="text" style="width:auto">所属部门: </span><input class="mini-textbox" style="width: 150px" name="deptName"/></li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">姓名: </span><input class="mini-textbox" style="width: 150px" name="userName"/></li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                </li>
            </ul>
        </form>
        <ul class="toolBtnBox">
            <a id="asyncButton" class="mini-button" style="margin-left: 10px" img="${ctxPath}/scripts/mini/miniui/res/images/xhtml.png"
               onclick="asyncData()">数据同步</a>
            <span style="color: red">注：每晚数据自动同步</span>
        </ul>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="listGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         url="${ctxPath}/portrait/contract/listData.do" idField="id" sortOrder="desc" sortField="signYear"
         multiSelect="true" showColumnsMenu="false" sizeList="[10,20,50,100,200]" pageSize="15" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div type="indexcolumn" align="center" headerAlign="center" width="30">序号</div>
            <div field="contractNo" width="100" headerAlign="center" align="center" >合同编号</div>
            <div field="contractDesc" width="250" headerAlign="center" align="left" >合同名称</div>
            <div field="score" width="40" headerAlign="center" align="center" allowSort="true">得分</div>
            <div field="signYear" width="60" headerAlign="center" align="center" allowSort="true">签订年份</div>
            <div field="userName" width="80" headerAlign="center" align="center" allowSort="true">姓名</div>
            <div field="deptName" width="80" headerAlign="center" align="center" allowSort="true">所属部门</div>
            <div field="CREATE_TIME_" width="80" headerAlign="center" align="center" allowSort="true">添加日期</div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var listGrid = mini.get("listGrid");
    var permission = ${permission};
    if(!permission){
        mini.get("asyncButton").setEnabled(false);
    }
    //数据同步
    function asyncData() {
        mini.confirm("确定同步数据？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                _SubmitJson({
                    url: jsUseCtxPath + "/portrait/contract/asyncContract.do",
                    method: 'POST',
                    success: function (data) {
                        searchFrm();
                    }
                });
            }
        });
    }
</script>
<redxun:gridScript gridId="listGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
