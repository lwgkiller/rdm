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
                    <span class="text" style="width:auto">年：</span>
                    <input class="mini-textbox" id="signYear" name="signYear"/>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="addBusiness()">新增</a>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="saveBusiness()">保存</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="removeBusiness()">删除</a>
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
         showCellTip="true" idField="id" allowCellEdit="true" allowCellSelect="true"
         multiSelect="true" showColumnsMenu="false" sizeList="[100,500]" pageSize="100" allowAlternating="true" pagerButtons="#pagerButtons"
         url="${ctxPath}/newproductAssembly/core/kanban/annualPlanPrdListQuery.do" oncellvalidation="onCellValidation">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div type="indexcolumn" width="35" headerAlign="center" align="center">序号</div>
            <div field="signYear" width="80" headerAlign="center" align="center">年份
                <input property="editor" class="mini-combobox" style="width:100%;"
                       url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=signYear"
                       valueField="key" textField="value"/>
            </div>
            <div field="productCategory"  width="120" headerAlign="center" align="center">产品种类
                <input property="editor" class="mini-combobox" style="width:100%;"
                       url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=productCategory"
                       valueField="key" textField="value"/>
            </div>
            <div field="testQuantity" name="testQuantity" width="80" headerAlign="center" align="center">台数
                <input property="editor" class="mini-spinner" style="width:100%;" maxValue="9999999"/>
            </div>
        </div>
    </div>
</div>


<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var businessListGrid = mini.get("businessListGrid");
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    //所有非特殊文本都变成这个格式
    function render(e) {
        if (e.value != null && e.value != "") {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            return html;
        }
    }
    //..
    function removeBusiness(record) {
        var rows = [];
        if (record) {
            rows.push(record);
        } else {
            rows = businessListGrid.getSelecteds();
        }
        if (rows.length <= 0) {
            mini.alert("请至少选中一条记录");
            return;
        }
        mini.confirm("确定删除选中记录？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                var rowIds = [];
                for (var i = 0, l = rows.length; i < l; i++) {
                    var r = rows[i];
                    if (r.id == "") {
                        businessListGrid.removeRow(r, false);
                    } else {
                        rowIds.push(r.id);
                    }
                }
                if (rowIds.length > 0) {
                    _SubmitJson({
                        url: jsUseCtxPath + "/newproductAssembly/core/kanban/deleteAnnualPlanPrd.do",
                        method: 'POST',
                        data: {ids: rowIds.join(',')},
                        success: function (data) {
                            if (data) {
                                searchFrm();
                            }
                        }
                    });
                }
            }
        });
    }
    //..
    function addBusiness() {
        var newRow = {}
        businessListGrid.addRow(newRow, 0);
    }
    //..
    function saveBusiness() {
        businessListGrid.validate();
        if (!businessListGrid.isValid()) {
            var error = businessListGrid.getCellErrors()[0];
            businessListGrid.beginEditCell(error.record, error.column);
            mini.alert(error.column.header + error.errorText);
            return;
        }

        var postData = businessListGrid.getChanges();
        var postDataJson = mini.encode(postData);
        $.ajax({
            url: jsUseCtxPath + "/newproductAssembly/core/kanban/saveAnnualPlanPrd.do",
            type: 'POST',
            contentType: 'application/json',
            data: postDataJson,
            success: function (returnData) {
                if (returnData && returnData.message) {
                    mini.alert(returnData.message, '提示', function () {
                        if (returnData.success) {
                            businessListGrid.reload();
                        }
                    });
                }
            }
        });
    }
    //..
    function onCellValidation(e) {
        if (e.field == 'signYear' && (!e.value || e.value == '')) {
            e.isValid = false;
            e.errorText = '不能为空！';
        }
        if (e.field == 'productCategory' && (!e.value || e.value == '')) {
            e.isValid = false;
            e.errorText = '不能为空！';
        }
        if (e.field == 'testQuantity' && (!e.value || e.value == '')) {
            e.isValid = false;
            e.errorText = '不能为空！';
        }
    }
</script>
<redxun:gridScript gridId="businessListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>