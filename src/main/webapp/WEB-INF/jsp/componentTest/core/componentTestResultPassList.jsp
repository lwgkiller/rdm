<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title></title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right:15px">
                    <span class="text" style="width:auto">零部件名称：</span>
                    <input class="mini-textbox" id="componentName" name="componentName" style="width:100%;"/>
                </li>
                <li style="margin-right:15px">
                    <span class="text" style="width:auto">零部件型号：</span>
                    <input class="mini-textbox" id="componentModel" name="componentModel" style="width:100%;"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">物料编码：</span>
                    <input class="mini-textbox" id="materialCode" name="materialCode"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">配套主机型号：</span>
                    <input class="mini-textbox" id="machineModel" name="machineModel"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">供应商名称：</span>
                    <input class="mini-textbox" id="supplierName" name="supplierName"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">试验类型：</span>
                    <input id="testType" name="testType"
                           class="mini-combobox" style="width:98%"
                           url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=testType"
                           valueField="key" textField="value"/>
                </li>
                <li style="margin-right:15px">
                    <span class="text" style="width:auto">零部件类别：</span>
                    <input id="componentCategory" name="componentCategory"
                           class="mini-combobox" style="width:98%"
                           url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=componentCategory"
                           valueField="key" textField="value"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">试验完成时间：</span>
                    <input id="completeTestMonth" name="completeTestMonth" class="mini-monthpicker" allowinput="false"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">承担单位：</span>
                    <input id="laboratory" name="laboratory"
                           class="mini-combobox" style="width:98%"
                           url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=undertakeLaboratory"
                           valueField="key" textField="value"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">申请人：</span>
                    <input class="mini-textbox" id="applyUser" name="applyUser"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">申请部门：</span>
                    <input class="mini-textbox" id="applyDep" name="applyDep"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">试验负责人：</span>
                    <input class="mini-textbox" id="testLeader" name="testLeader"/>
                </li>
                <li style="margin-left: 10px">
                    <f:a alias="componentTestPassList-searchFrm" onclick="searchFrm()" showNoRight="false" style="margin-right: 5px">查询</f:a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <f:a alias="componentTestPassList-exportItem" onclick="exportItem()" showNoRight="false" style="margin-right: 5px">导出</f:a>
                </li>
            </ul>
        </form>
        <span class="searchSelectionBtn" onclick="no_search(this ,'searchForm')">
				<i class="icon-sc-lower"></i>
        </span>
    </div>
</div>

<div class="mini-fit" style="height: 100%;">
    <div id="businessListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         allowCellWrap="true"
         showCellTip="true" idField="id" allowCellEdit="true" allowCellSelect="true" oncellvalidation="onCellValidation"
         multiSelect="true" showColumnsMenu="false" sizeList="[50,100]" pageSize="50" allowAlternating="true"
         pagerButtons="#pagerButtons"
         url="${ctxPath}/componentTest/core/result/dataPassListQuery.do">
        <div property="columns">
            <div type="checkcolumn" width="40" trueValue="1" falseValue="0"></div>
            <div type="indexcolumn" width="45" headerAlign="center" align="center" allowSort="true">序号</div>
            <div field="componentName" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">零部件名称</div>
            <div field="componentModel" width="130" headerAlign="center" align="center" allowSort="true" renderer="render">零部件型号</div>
            <div field="materialCode" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">物料编码</div>
            <div field="machineModel" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">配套主机型号</div>
            <div field="testStandard" width="130" headerAlign="center" align="center" allowSort="true" renderer="render">相关试验标准</div>
            <div field="supplierName" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">供应商名称</div>
            <div field="testReportNo" width="130" headerAlign="center" align="center" allowSort="true" renderer="render">试验报告编号</div>
        </div>
    </div>
</div>
<!--导出Excel相关HTML-->
<form id="excelForm" action="${ctxPath}/componentTest/core/result/exportPass.do" method="post" target="excelIFrame">
    <input type="hidden" name="filter" id="filter"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var businessListGrid = mini.get("businessListGrid");
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var currentUserName = "${currentUserName}";
    //..p
    function render(e) {
        if (e.value != null && e.value != "") {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            return html;
        }
    }
    //..导出
    function exportItem() {
        var rows = businessListGrid.getSelecteds();
        if (rows.length <= 0) {
            mini.alert("请至少选中一条记录");
            return;
        }
        var params = [];
        for (var i = 0; i < rows.length; i++) {
            var obj = {};
            obj.name = "id";
            obj.value = rows[i].id
            params.push(obj);
        }
        $("#filter").val(mini.encode(params));
        var excelForm = $("#excelForm");
        excelForm.submit();
    }
</script>
<redxun:gridScript gridId="businessListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>