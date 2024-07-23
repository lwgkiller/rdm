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
                    <span class="text" style="width:auto">名称：</span>
                    <input class="mini-textbox" id="description" name="description"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">属性：</span>
                    <input class="mini-textbox" id="attribute" name="attribute"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">内容简述：</span>
                    <input class="mini-textbox" id="abstract" name="abstract"/>
                </li>
                <li style="margin-left: 10px">
                    <f:a alias="PartsToBeWritten-searchFrm" onclick="searchFrm()" showNoRight="false" style="margin-right: 5px">查询</f:a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <f:a alias="PartsToBeWritten-addBusiness" onclick="addBusiness()" showNoRight="false" style="margin-right: 5px">新增</f:a>
                    <f:a alias="PartsToBeWritten-saveBusiness" onclick="saveBusiness()" showNoRight="false" style="margin-right: 5px">保存</f:a>
                    <f:a alias="PartsToBeWritten-removeBusiness" onclick="removeBusiness()" showNoRight="false" style="margin-right: 5px">删除</f:a>

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
         allowCellWrap="false"
         showCellTip="true" idField="id" allowCellEdit="true" allowCellSelect="true" oncellvalidation="onCellValidation"
         multiSelect="true" showColumnsMenu="false" sizeList="[20,50]" pageSize="20" allowAlternating="true" pagerButtons="#pagerButtons"
         url="${ctxPath}/powerApplicationTechnology/core/partsIntegration/partsToBeWritten/dataListQuery.do">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div type="indexcolumn" width="20" headerAlign="center" align="center">序号</div>
            <div field="description" width="100" headerAlign="center" align="center" renderer="render">名称
                <input property="editor" class="mini-textbox"/>
            </div>
            <div field="attribute" width="40" headerAlign="center" align="center">属性
                <input property="editor" class="mini-combobox" style="width:100%;"
                       url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=PartsModelType"
                       valueField="key" textField="value"/>
            </div>
            <div field="abstract" width="200" headerAlign="center" align="center" renderer="render">内容简述
                <input property="editor" class="mini-textbox"/>
            </div>
            <div field="completionTime" width="40" headerAlign="center" align="center" dateFormat="yyyy-MM-dd">预计完成时间
                <input property="editor" class="mini-datepicker" format="yyyy-MM-dd"
                       showTime="false" showOkButton="true" showClearButton="false" style="width:98%;"/>
            </div>
            <div field="completedByIds" displayField="completedBy" width="80" headerAlign="center" align="center" renderer="render">主要完成人
                <input property="editor" class="mini-user rxc"
                       plugins="mini-user" style="width:98%;height:34px;" allowinput="false" length="80"
                       mainfield="no" single="false"/>
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var businessListGrid = mini.get("businessListGrid");
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
                        url: jsUseCtxPath + "/powerApplicationTechnology/core/partsIntegration/partsToBeWritten/deleteData.do",
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
        newRow.description = "";
        newRow.attribute = "";
        newRow.abstract = "";
        newRow.completionTime = "";
        newRow.completedByIds = "";
        newRow.completedBy = "";
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
            url: jsUseCtxPath + "/powerApplicationTechnology/core/partsIntegration/partsToBeWritten/saveBusiness.do",
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
        if (e.field == 'description' && (!e.value || e.value == '')) {
            e.isValid = false;
            e.errorText = '不能为空！';
        }
        if (e.field == 'attribute' && (!e.value || e.value == '')) {
            e.isValid = false;
            e.errorText = '不能为空！';
        }
        if (e.field == 'abstract' && (!e.value || e.value == '')) {
            e.isValid = false;
            e.errorText = '不能为空！';
        }
        if (e.field == 'completionTime' && (!e.value || e.value == '')) {
            e.isValid = false;
            e.errorText = '不能为空！';
        }
        if (e.field == 'completedByIds' && (!e.value || e.value == '')) {
            e.isValid = false;
            e.errorText = '不能为空！';
        }
    }
</script>
<redxun:gridScript gridId="businessListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>