<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title></title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">整机编号：</span>
                    <input class="mini-textbox" id="pin" name="pin"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">物料号：</span>
                    <input class="mini-textbox" id="material" name="material"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">发运时间：</span>
                    <input class="mini-textbox" id="shipmentTime" name="shipmentTime"/>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="exportList()">导出</a>

                </li>
            </ul>
        </form>
        <span class="searchSelectionBtn" onclick="no_search(this ,'searchForm')">
				<i class="icon-sc-lower"></i>
        </span>
    </div>
</div>
<%-------------------------------%>
<div class="mini-fit" style="height: 100%;">
    <div id="businessListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" allowCellWrap="true"
         showCellTip="true" idField="id" allowCellEdit="true" allowCellSelect="true" allowHeaderWrap="true"
         multiSelect="true" showColumnsMenu="false" sizeList="[5000,10000]" pageSize="5000" allowAlternating="true"
         pagerButtons="#pagerButtons"
         sortField="shipmentTime" sortOrder="desc"
         url="${ctxPath}/serviceEngineering/core/seGeneralKanbanNew/gssShipmentCacheListQuery.do" virtualScroll="true">
        <div property="columns">
            <div type="checkcolumn" width="40"></div>
            <div field="isOk" width="80" headerAlign="center" align="center" renderer="render">是否有操保手册</div>
            <div field="pin" width="100" headerAlign="center" align="center" renderer="render">整机编号</div>
            <div field="shipmentTime" width="80" headerAlign="center" align="center" renderer="render">发运时间</div>
            <div field="material" width="80" headerAlign="center" align="center" allowSort="true" renderer="render">物料号</div>
            <div field="salesModels" width="80" headerAlign="center" align="center" allowSort="true" renderer="render">销售型号</div>
            <div field="designModel" width="200" headerAlign="center" align="center" allowSort="true" renderer="render">设计型号</div>
        </div>
    </div>
</div>
</div>
<%--导出--%>
<form id="excelForm" action="${ctxPath}/serviceEngineering/core/seGeneralKanbanNew/gssShipmentCacheExport.do" method="post"
      target="excelIFrame">
    <input type="hidden" name="filter" id="filter"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>

<%-------------------------------%>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var businessListGrid = mini.get("businessListGrid");
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var currentUserName = "${currentUserName}";
    //..
    $(function () {
    });
    //..
    function render(e) {
        var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
        return html;
    }
    //..
    function getSelectFile() {
        var fileList = $("#inputFile")[0].files;
        if (fileList && fileList.length > 0) {
            var fileNameSuffix = fileList[0].name.split('.').pop();
            if (fileNameSuffix == 'xls' || fileNameSuffix == 'xlsx') {
                mini.get("fileName").setValue(fileList[0].name);
            }
            else {
                clearUploadFile();
                mini.alert('请上传excel文件！');
            }
        }
    }
    //..
    function exportList() {
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