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
                    <span class="text" style="width:auto">吨位段：</span>
                    <input class="mini-textbox" id="tonnageRange" name="tonnageRange"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">销售型号：</span>
                    <input class="mini-textbox" id="salesModel" name="salesModel"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">设计型号：</span>
                    <input class="mini-textbox" id="designModel" name="designModel"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">物料编码：</span>
                    <input class="mini-textbox" id="materialCode" name="materialCode"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">产品类型：</span>
                    <input class="mini-textbox" id="productType" name="productType"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">产品主管：</span>
                    <input class="mini-textbox" id="productDirector" name="productDirector"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">责任部门：</span>
                    <input class="mini-textbox" id="productInstitute" name="productInstitute"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">整机预计完成时间：</span>
                    <input class="mini-textbox" id="planCompleteDate" name="planCompleteDate"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">开始时间：</span>
                    <input class="mini-textbox" id="beginDate" name="beginDate"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">进度评价：</span>
                    <input class="mini-textbox" id="progressEvaluation" name="progressEvaluation"/>
                </li>
                <li style="margin-left: 10px">
                    <f:a alias="datamigrationModelprogress-searchFrm" onclick="searchFrm()" showNoRight="false">查询</f:a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <f:a alias="datamigrationModelprogress-addBusiness" onclick="addBusiness()" showNoRight="false">新增</f:a>
                    <f:a alias="datamigrationModelprogress-saveBusiness" onclick="saveBusiness()" showNoRight="false">保存</f:a>
                    <f:a alias="datamigrationModelprogress-removeBusiness" onclick="removeBusiness()" showNoRight="false">删除</f:a>
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
         url="${ctxPath}/digitization/core/datamigration/modelprogress/dataListQuery.do">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div field="orderNum" width="50" headerAlign="center" align="center">序号
                <input property="editor" class="mini-spinner" value="0" maxValue="1000000"/>
            </div>
            <div field="tonnageRange" width="120" headerAlign="center" align="center">吨位段
                <input property="editor" class="mini-textbox"/>
            </div>
            <div field="salesModel" width="120" headerAlign="center" align="center">销售型号
                <input property="editor" class="mini-textbox"/>
            </div>
            <div field="designModel" width="120" headerAlign="center" align="center">设计型号
                <input property="editor" class="mini-textbox"/>
            </div>
            <div field="materialCode" width="100" headerAlign="center" align="center">物料编码
                <input property="editor" class="mini-textbox"/>
            </div>
            <div field="migrationContent" width="70" headerAlign="center" align="center">迁移内容
                <input property="editor" class="mini-textbox"/>
            </div>
            <div field="productType" width="70" headerAlign="center" align="center">产品类型
                <input property="editor" class="mini-textbox"/>
            </div>
            <div field="workloadEvaluation" width="120" headerAlign="center" align="center" numberFormat="p">工作量评价(%)
                <input property="editor" class="mini-spinner" style="width:100%;" minValue="0" maxValue="1" decimalPlaces="4"
                       format="p" increment="0.01"/>
            </div>
            <div field="subMaterial" width="130" headerAlign="center" align="center">系统/部件物料编码
                <input property="editor" class="mini-textbox"/>
            </div>
            <div field="productDirector" width="80" headerAlign="center" align="center">产品主管
                <input property="editor" class="mini-textbox"/>
            </div>
            <div field="productInstitute" width="80" headerAlign="center" align="center">产品所
                <input property="editor" class="mini-textbox"/>
            </div>
            <div field="planCompleteDate" width="120" headerAlign="center" align="center" dateFormat="yyyy-MM-dd">整机预计完成时间
                <input property="editor" class="mini-datepicker" format="yyyy-MM-dd"
                       showTime="true" showOkButton="true" showClearButton="false" style="width:98%;"/>
            </div>
            <div field="beginDate" width="100" headerAlign="center" align="center" dateFormat="yyyy-MM-dd">开始时间
                <input property="editor" class="mini-datepicker" format="yyyy-MM-dd"
                       showTime="true" showOkButton="true" showClearButton="false" style="width:98%;"/>
            </div>
            <div field="cycle" width="60" headerAlign="center" align="center">周期(周)
                <input property="editor" class="mini-spinner" style="width:100%;" minValue="0" maxValue="100" decimalPlaces="2"/>
            </div>
            <div field="currentPercentage" width="180" headerAlign="center" align="center" numberFormat="p">整机当前实际完成百分比(%)
                <input property="editor" class="mini-spinner" style="width:100%;" minValue="0" maxValue="1" decimalPlaces="4"
                       format="p" increment="0.01"/>
            </div>
            <div field="schedulePercentage" width="120" headerAlign="center" align="center" numberFormat="p">应完成进度(%)
                <input property="editor" class="mini-spinner" style="width:100%;" minValue="0" maxValue="1" decimalPlaces="4"
                       format="p" increment="0.01"/>
            </div>
            <div field="progressEvaluation" width="100" headerAlign="center" align="center">进度评价
                <input property="editor" class="mini-textbox"/>
            </div>
            <div field="actualPercentage" width="180" headerAlign="center" align="center" numberFormat="p">实际完成工作量百分比(%)
                <input property="editor" class="mini-spinner" style="width:100%;" minValue="0" maxValue="1" decimalPlaces="4"
                       format="p" increment="0.01"/>
            </div>
            <div field="planPercentage" width="180" headerAlign="center" align="center" numberFormat="p">计划完成工作量百分比(%)
                <input property="editor" class="mini-spinner" style="width:100%;" minValue="0" maxValue="1" decimalPlaces="4"
                       format="p" increment="0.01"/>
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
                        url: jsUseCtxPath + "/digitization/core/datamigration/modelprogress/deleteData.do",
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
        newRow.workloadEvaluation = "0";
        newRow.cycle = "1";
        newRow.currentPercentage = "0";
        newRow.schedulePercentage = "0";
        newRow.actualPercentage = "0";
        newRow.planPercentage = "0";
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
            url: jsUseCtxPath + "/digitization/core/datamigration/modelprogress/saveBusiness.do",
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