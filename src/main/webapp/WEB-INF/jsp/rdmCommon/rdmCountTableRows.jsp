<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title></title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<%--工具栏--%>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right:15px">
                    <span class="text" style="width:auto">表名：</span>
                    <input class="mini-textbox" id="tableName" name="tableName" style="width:100%;"/>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <a class="mini-button btn-yellow" style="margin-right: 5px" plain="true" onclick="refreshBusiness()">刷新</a>
                </li>
            </ul>
        </form>
    </div>
</div>
<%--列表视图--%>
<div class="mini-fit" style="height: 100%;">
    <div id="businessListGrid" idField="id" class="mini-datagrid" style="width: 100%; height: 100%;"
         allowCellWrap="true" showCellTip="true" allowCellEdit="true" allowCellSelect="true"
         allowResize="true" allowAlternating="true" showColumnsMenu="false" multiSelect="true"
         showPager="false" sortField="tableRowCount" sortOrder="desc"
         url="${ctxPath}/rdm/core/getCountTableRows.do">
        <div property="columns">
            <div type="checkcolumn" width="40" trueValue="1" falseValue="0"></div>
            <div type="indexcolumn" width="45" headerAlign="center" align="center">序号</div>
            <div field="tableName" width="100" headerAlign="center" align="center" allowSort="true">表名</div>
            <div field="tableRowCount" width="100" headerAlign="center" align="center" allowSort="true" numberFormat="#,#">行数</div>
        </div>
    </div>
</div>
<%--JS--%>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var businessListGrid = mini.get("businessListGrid");

    //..
    function refreshBusiness(record) {
        _SubmitJson({
            url: jsUseCtxPath + "/rdm/core/refreshCountTableRows.do",
            method: 'POST',
            postJson: false,
            showMsg: false,
            success: function (returnData) {
                if (returnData.success) {
                    mini.alert(returnData.message);
                    searchFrm();
                } else {
                    mini.alert("刷新失败:" + returnData.message);
                }
            },
            fail: function (returnData) {
                mini.alert("刷新失败:" + returnData.message);
            }
        });
    }
</script>
<redxun:gridScript gridId="businessListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>