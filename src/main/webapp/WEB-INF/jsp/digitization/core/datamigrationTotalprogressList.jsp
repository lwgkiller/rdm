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
                    <span class="text" style="width:auto">产品所：</span>
                    <input class="mini-textbox" id="productInstitute" name="productInstitute"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">吨位段：</span>
                    <input class="mini-textbox" id="tonnageRange" name="tonnageRange"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">转图时间：</span>
                    <input class="mini-textbox" id="transferTime" name="transferTime"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">计划进度评价：</span>
                    <input class="mini-textbox" id="scheduleEvaluation" name="scheduleEvaluation"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">序时进度评价：</span>
                    <input class="mini-textbox" id="chronologicalEvaluation" name="chronologicalEvaluation"/>
                </li>
                <li style="margin-left: 10px">
                    <f:a alias="datamigrationTotalprogress-searchFrm" onclick="searchFrm()" showNoRight="false">查询</f:a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <f:a alias="datamigrationTotalprogress-addBusiness" onclick="addBusiness()" showNoRight="false">新增</f:a>
                    <f:a alias="datamigrationTotalprogress-saveBusiness" onclick="saveBusiness()" showNoRight="false">保存</f:a>
                    <f:a alias="datamigrationTotalprogress-removeBusiness" onclick="removeBusiness()" showNoRight="false">删除</f:a>
                </li>
            </ul>
        </form>
        <span class="searchSelectionBtn" onclick="no_search(this ,'searchForm')">
				<i class="icon-sc-lower"></i>
        </span>
    </div>
</div>

<div class="mini-fit" style="height: 100%;">
    <div id="businessListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" allowCellWrap="false"
         showCellTip="true" idField="id" allowCellEdit="true" allowCellSelect="true" oncellvalidation="onCellValidation"
         multiSelect="true" showColumnsMenu="false" sizeList="[1000,5000]" pageSize="1000" allowAlternating="true"
         pagerButtons="#pagerButtons"
         url="${ctxPath}/digitization/core/datamigration/totalprogress/dataListQuery.do">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div field="orderNum" width="50" headerAlign="center" align="center">序号
                <input property="editor" class="mini-spinner" value="0" maxValue="1000000"/>
            </div>
            <div field="productInstitute" width="120" headerAlign="center" align="center">产品所
                <input property="editor" class="mini-textbox"/>
            </div>
            <div field="tonnageRange" width="120" headerAlign="center" align="center">吨位段
                <input property="editor" class="mini-textbox"/>
            </div>
            <div field="transferTime" width="80" headerAlign="center" align="center">转图时间
                <input property="editor" class="mini-textbox"/>
            </div>
            <div field="productsNumber" width="80" headerAlign="center" align="center">产品数量
                <input property="editor" class="mini-spinner" style="width:100%;" minValue="0" maxValue="1000"/>
            </div>
            <div field="totalWorkload" width="80" headerAlign="center" align="center">总工作量
                <input property="editor" class="mini-spinner" style="width:100%;" minValue="0" maxValue="1000" decimalPlaces="2"/>
            </div>
            <div field="plannedWorkload" width="120" headerAlign="center" align="center">计划完成工作量
                <input property="editor" class="mini-spinner" style="width:100%;" minValue="0" maxValue="1000" decimalPlaces="2"/>
            </div>
            <div field="actualWorkload" width="120" headerAlign="center" align="center">实际完成工作量
                <input property="editor" class="mini-spinner" style="width:100%;" minValue="0" maxValue="1000" decimalPlaces="2"/>
            </div>
            <div field="actualProgress" width="100" headerAlign="center" align="center" numberFormat="p">实际进度(%)
                <input property="editor" class="mini-spinner" style="width:100%;" minValue="0" maxValue="10" decimalPlaces="4"
                       format="p" increment="0.01"/>
            </div>
            <div field="scheduleEvaluation" width="100" headerAlign="center" align="center">计划进度评价
                <input property="editor" class="mini-textbox"/>
            </div>
            <div field="chronologicalProgress" width="100" headerAlign="center" align="center" numberFormat="p">序时进度(%)
                <input property="editor" class="mini-spinner" style="width:100%;" minValue="0" maxValue="10" decimalPlaces="4"
                       format="p" increment="0.01"/>
            </div>
            <div field="chronologicalActualProgress" width="120" headerAlign="center" align="center" numberFormat="p">序时实际进度(%)
                <input property="editor" class="mini-spinner" style="width:100%;" minValue="0" maxValue="10" decimalPlaces="4"
                       format="p" increment="0.01"/>
            </div>
            <div field="chronologicalEvaluation" width="150" headerAlign="center" align="center">序时进度评价
                <input property="editor" class="mini-textbox"/>
            </div>
            <div field="remark" width="200" headerAlign="center" align="center">备注
                <input property="editor" class="mini-textbox"/>
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var businessListGrid = mini.get("businessListGrid");

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
                        url: jsUseCtxPath + "/digitization/core/datamigration/totalprogress/deleteData.do",
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

    function addBusiness() {
        var newRow = {}
        newRow.productsNumber = "0";
        newRow.totalWorkload = "0";
        newRow.plannedWorkload = "0";
        newRow.actualWorkload = "0";
        newRow.actualProgress = "0";
        newRow.chronologicalProgress = "0";
        newRow.chronologicalActualProgress = "0";
        businessListGrid.addRow(newRow, 0);
    }

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
            url: jsUseCtxPath + "/digitization/core/datamigration/totalprogress/saveBusiness.do",
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

    function onCellValidation(e) {

    }
</script>
<redxun:gridScript gridId="businessListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>