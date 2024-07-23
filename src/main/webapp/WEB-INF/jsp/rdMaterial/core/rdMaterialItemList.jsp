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
    <div style="height: 50px;line-height:50px">
        <span id="titleSpan" class="text"
              style="font-size:30px;font: bold">研发库退库物料:&nbsp;${materialCode}&nbsp;物料描述:&nbsp;${materialDescription}</span>
    </div>
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right:15px">
                    <span class="text" style="width:auto">责任人：</span>
                    <input class="mini-textbox" id="responsibleUser" name="responsibleUser" style="width:100%;"/>
                </li>
                <li style="margin-right:15px">
                    <span class="text" style="width:auto">责任部门：</span>
                    <input class="mini-textbox" id="responsibleDep" name="responsibleDep" style="width:100%;"/>
                </li>
                <li style="margin-right:15px">
                    <span class="text" style="width:auto">入库原因：</span>
                    <input class="mini-textbox" id="reasonForStorage" name="reasonForStorage" style="width:100%;"/>
                </li>
                <li style="margin-right:15px">
                    <span class="text" style="width:auto">入库日期：</span>
                    <input class="mini-textbox" id="inDateBegin" name="inDateBegin" style="width:100%;"/>
                </li>
                <li style="margin-right:15px">
                    <span class="text" style="width:auto">至：</span>
                    <input class="mini-textbox" id="inDateEnd" name="inDateEnd" style="width:100%;"/>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="exportBusiness()">导出</a>
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
         sizeList="[50,100]" pageSize="50" pagerButtons="#pagerButtons" rowStyler="rowStyler"
         url="${ctxPath}/rdMaterial/core/summary/itemListQuery.do?materialCode=${materialCode}
&untreatedTimespanBegin=${untreatedTimespanBegin}&untreatedTimespanEnd=${untreatedTimespanEnd}
&theYear=${theYear}&untreatedQuantityNotEqual=${untreatedQuantityNotEqual}&responsibleDep=${responsibleDep}">
        <div property="columns">
            <div type="checkcolumn" width="40" trueValue="1" falseValue="0"></div>
            <div type="indexcolumn" width="45" headerAlign="center" align="center" allowSort="true">序号</div>
            <div name="action" cellCls="actionIcons" width="85" headerAlign="center" align="center" renderer="onActionRenderer"
                 cellStyle="padding:0;">操作
            </div>
            <div field="materialCode" width="130" headerAlign="center" align="center">物料号</div>
            <div field="materialDescription" width="200" headerAlign="center" align="center">物料描述</div>
            <div field="businessNo" width="130" headerAlign="center" align="center">入库单据号</div>
            <div field="reasonForStorage" width="130" headerAlign="center" align="center">入库原因</div>
            <div field="responsibleDep" width="130" headerAlign="center" align="center">责任部门</div>
            <div field="responsibleUser" width="130" headerAlign="center" align="center">责任人</div>
            <div field="inDate" width="100" headerAlign="center" align="center" dateFormat="yyyy-MM-dd">入库日期</div>
            <div field="inQuantity" width="80" headerAlign="center" align="center">入库数量</div>
            <div field="untreatedQuantity" width="80" headerAlign="center" align="center">未处置数量</div>
            <div field="untreatedTimespan" width="80" headerAlign="center" align="center" renderer="theRenderer">未处置搁置时长(天)</div>
        </div>
    </div>
</div>
<!--导出Excel相关HTML-->
<form id="excelForm" action="${ctxPath}/rdMaterial/core/summary/exportItemList.do?materialCode=${materialCode}
&untreatedTimespanBegin=${untreatedTimespanBegin}&untreatedTimespanEnd=${untreatedTimespanEnd}
&theYear=${theYear}&untreatedQuantityNotEqual=${untreatedQuantityNotEqual}&responsibleDep=${responsibleDep}"
      method="post" target="excelIFrame">
    <input type="hidden" name="filter" id="filter"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>
<%--JS--%>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var currentUserName = "${currentUserName}";
    var materialCode = "${materialCode}";
    var materialDescription = "${materialDescription}";
    var untreatedTimespanBegin = "${untreatedTimespanBegin}";
    var untreatedTimespanEnd = "${untreatedTimespanBegin}";
    var theYear = "${theYear}";
    var untreatedQuantityNotEqual = "${untreatedQuantityNotEqual}";
    var responsibleDep = "${responsibleDep}"
    var businessListGrid = mini.get("businessListGrid");
    //..
    $(function () {
        if (materialCode == "" && materialDescription == "") {
            document.getElementById("titleSpan").style.display = "none";
        }
    });
    //..
    function showBusiness(record) {
        var url = jsUseCtxPath + "/rdMaterial/core/inStorage/editPage.do?businessId=" + record.mainId + "&action=detail";
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
        var s = '<span  title="查看入库凭证" onclick="showBusiness(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">查看入库凭证</span>';
        return s;
    }
    //..常规渲染
    function render(e) {
        if (e.value != null && e.value != "") {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            return html;
        }
    }
    //..
    function theRenderer(e) {
        if (e.row.untreatedQuantity != 0) {
            if (e.row.untreatedTimespan >= 60 && e.row.untreatedTimespan < 150) {
                e.rowStyle = 'background-color:#D3D445';
            } else if (e.row.untreatedTimespan >= 150) {
                e.rowStyle = 'background-color:#FFC3C3';
            }
        }
        return e.value;
    }
    //..
    function exportBusiness() {
        var parent = $(".search-form");
        var inputAry = $("input", parent);
        var params = [];
        inputAry.each(function (i) {
            var el = $(this);
            var obj = {};
            obj.name = el.attr("name");
            if (!obj.name) return true;
            obj.value = el.val();
            params.push(obj);
        });
        $("#filter").val(mini.encode(params));
        var excelForm = $("#excelForm");
        excelForm.submit();
    }
</script>
<redxun:gridScript gridId="businessListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>