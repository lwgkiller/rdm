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
                    <span class="text" style="width:auto">物料号：</span>
                    <input class="mini-textbox" id="materialCode" name="materialCode" style="width:100%;"/>
                </li>
                <li style="margin-right:15px">
                    <span class="text" style="width:auto">物料类型：</span>
                    <input id="materialType" name="materialType"
                           class="mini-combobox" style="width:98%"
                           data="[{'key':'液压类','value':'液压类'},{'key':'电气类','value':'电气类'},
                           {'key':'动力类','value':'动力类'},{'key':'结构件类','value':'结构件类'}]"
                           valueField="key" textField="value"/>
                </li>
                <li style="margin-right:15px">
                    <span class="text" style="width:auto">物料描述：</span>
                    <input class="mini-textbox" id="materialDescription" name="materialDescription" style="width:100%;"/>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
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
         sizeList="[50,100]" pageSize="50" pagerButtons="#pagerButtons"
         url="${ctxPath}/rdMaterial/core/summary/summaryListQuery.do">
        <div property="columns">
            <div type="checkcolumn" width="40" trueValue="1" falseValue="0"></div>
            <div type="indexcolumn" width="45" headerAlign="center" align="center" allowSort="true">序号</div>
            <div name="action" cellCls="actionIcons" width="85" headerAlign="center" align="center" renderer="onActionRenderer"
                 cellStyle="padding:0;">操作
            </div>
            <div field="materialCode" width="120" headerAlign="center" align="center" allowSort="true">物料号</div>
            <div field="materialType" width="130" headerAlign="center" align="center" allowSort="true">物料类型</div>
            <div field="materialDescription" width="120" headerAlign="center" align="center" allowSort="true">物料描述</div>
            <div field="inQuantitySummary" width="80" headerAlign="center" align="center">入库数量(汇总)</div>
            <div field="untreatedQuantitySummary" width="80" headerAlign="center" align="center">未处理数量(汇总)</div>
        </div>
    </div>
</div>
<%--JS--%>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var currentUserName = "${currentUserName}";
    var businessListGrid = mini.get("businessListGrid");
    //..
    $(function () {
    });
    //..
    function showBusiness(record) {
        var url = jsUseCtxPath + "/rdMaterial/core/summary/itemListPage.do?" +
            "materialCode=" + record.materialCode + "&materialDescription=" + record.materialDescription;
        var winObj = window.open(url, '');
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (businessListGrid) {
                    businessListGrid.reload();
                }
            }
        }, 1000);
    }
    //..操作渲染
    function onActionRenderer(e) {
        var record = e.record;
        var s = '<span  title="查看明细" onclick="showBusiness(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">查看明细</span>';
        return s;
    }
    //..常规渲染
    function render(e) {
        if (e.value != null && e.value != "") {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            return html;
        }
    }
</script>
<redxun:gridScript gridId="businessListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>