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
                    <span class="text" style="width:auto"><spring:message code="page.standardvalueList.name" />：</span>
                    <input class="mini-textbox" id="signYear" name="signYear"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.standardvalueList.name1" />：</span>
                    <input class="mini-textbox" id="signMonth" name="signMonth"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.standardvalueList.name2" />：</span>
                    <input class="mini-textbox" id="signWeek" name="signWeek"/>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()"><spring:message code="page.standardvalueList.name3" /></a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()"><spring:message code="page.standardvalueList.name4" /></a>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="addBusiness()"><spring:message code="page.standardvalueList.name5" /></a>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="saveBusiness()"><spring:message code="page.standardvalueList.name6" /></a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="removeBusiness()"><spring:message code="page.standardvalueList.name7" /></a>
                </li>
            </ul>
        </form>
        <span class="searchSelectionBtn" onclick="no_search(this ,'searchForm')">
				<i class="icon-sc-lower"></i>
        </span>
    </div>
</div>

<div class="mini-fit" style="height: 100%;">
    <div id="standardvalueListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         showCellTip="true" idField="id" allowCellEdit="true" allowCellSelect="true"
         multiSelect="true" showColumnsMenu="false" sizeList="[100,500]" pageSize="100" allowAlternating="true" pagerButtons="#pagerButtons"
         url="${ctxPath}/serviceEngineering/core/standardvalue/dataListQuery.do" oncellvalidation="onCellValidation">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div type="indexcolumn" width="35" headerAlign="center" align="center"><spring:message code="page.standardvalueList.name8" /></div>
            <div field="signYear" width="80" headerAlign="center" align="center"><spring:message code="page.standardvalueList.name" />
                <input property="editor" class="mini-combobox" style="width:100%;"
                       url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=signYear"
                       valueField="key" textField="value"/>
            </div>
            <div field="signMonth" width="80" headerAlign="center" align="center"><spring:message code="page.standardvalueList.name1" />
                <input property="editor" class="mini-combobox" style="width:100%;"
                       url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=signMonth"
                       valueField="key" textField="value"/>
            </div>
            <div field="signWeek" width="80" headerAlign="center" align="center"><spring:message code="page.standardvalueList.name2" />
                <input property="editor" class="mini-combobox" style="width:100%;"
                       url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=signWeek"
                       valueField="key" textField="value"/>
            </div>
            <div field="betaTotalNumber" name="betaTotalNumber" width="120" headerAlign="center" align="center"><spring:message code="page.standardvalueList.name9" />
                <input property="editor" class="mini-spinner" style="width:100%;" maxValue="9999999"/>
            </div>
            <div field="routineTotalNumber" name="routineTotalNumber" width="120" headerAlign="center" align="center"><spring:message code="page.standardvalueList.name10" />
                <input property="editor" class="mini-spinner" style="width:100%;" maxValue="9999999"/>
            </div>
            <div field="completeTotalNumber" name="completeTotalNumber" width="120" headerAlign="center" align="center"><spring:message code="page.standardvalueList.name11" />
                <input property="editor" class="mini-spinner" style="width:100%;" maxValue="9999999"/>
            </div>
            <div field="numberOfReturnedGroupsForTest" name="numberOfReturnedGroupsForTest" width="120" headerAlign="center" align="center"><spring:message code="page.standardvalueList.name12" />
                <input property="editor" class="mini-spinner" style="width:100%;" maxValue="9999999"/>
            </div>
            <div field="numberOfReturnedGroupsForTestActual" name="numberOfReturnedGroupsForTestActual" width="140" headerAlign="center"
                 align="center"><spring:message code="page.standardvalueList.name13" />
                <input property="editor" class="mini-spinner" style="width:100%;" maxValue="9999999"/>
            </div>
            <div field="returnRateForTest" name="returnRateForTest" width="100" headerAlign="center" align="center"><spring:message code="page.standardvalueList.name14" />
                <input property="editor" class="mini-spinner" style="width:100%;" maxValue="100"/>
            </div>
            <div field="numberOfReturnedGroupsForTestWeek" name="numberOfReturnedGroupsForTestWeek" width="150" headerAlign="center" align="center">
                <spring:message code="page.standardvalueList.name15" />
                <input property="editor" class="mini-spinner" style="width:100%;" maxValue="9999999"/>
            </div>
            <div field="numberOfReturnedGroupsForTestActualWeek" name="numberOfReturnedGroupsForTestActualWeek" width="150" headerAlign="center"
                 align="center"><spring:message code="page.standardvalueList.name16" />
                <input property="editor" class="mini-spinner" style="width:100%;" maxValue="9999999"/>
            </div>
            <div field="returnRateForTestWeek" name="returnRateForTestWeek" width="120" headerAlign="center" align="center"><spring:message code="page.standardvalueList.name17" />
                <input property="editor" class="mini-spinner" style="width:100%;" maxValue="100"/>
            </div>
            <div field="numberOfReturnedGroupsForQa" name="numberOfReturnedGroupsForQa" width="120" headerAlign="center" align="center"><spring:message code="page.standardvalueList.name18" />
                <input property="editor" class="mini-spinner" style="width:100%;" maxValue="9999999"/>
            </div>
            <div field="numberOfReturnedGroupsForQaActual" name="numberOfReturnedGroupsForQaActual" width="140" headerAlign="center" align="center">
                <spring:message code="page.standardvalueList.name19" />
                <input property="editor" class="mini-spinner" style="width:100%;" maxValue="9999999"/>
            </div>
            <div field="returnRateForQa" name="returnRateForQa" width="120" headerAlign="center" align="center"><spring:message code="page.standardvalueList.name20" />
                <input property="editor" class="mini-spinner" style="width:100%;" maxValue="100"/>
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var standardvalueListGrid = mini.get("standardvalueListGrid");
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
            rows = standardvalueListGrid.getSelecteds();
        }
        if (rows.length <= 0) {
            mini.alert(standardvalueList_name);
            return;
        }
        mini.confirm(standardvalueList_name1, standardvalueList_name2, function (action) {
            if (action != 'ok') {
                return;
            } else {
                var rowIds = [];
                for (var i = 0, l = rows.length; i < l; i++) {
                    var r = rows[i];
                    if (r.id == "") {
                        standardvalueListGrid.removeRow(r, false);
                    } else {
                        rowIds.push(r.id);
                    }
                }
                if (rowIds.length > 0) {
                    _SubmitJson({
                        url: jsUseCtxPath + "/serviceEngineering/core/standardvalue/deleteData.do",
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
        newRow.id = "";
        newRow.signYear = "";
        newRow.signMonth = "";
        newRow.signWeek = "";
        newRow.betaTotalNumber = "0";
        newRow.routineTotalNumber = "0";
        newRow.completeTotalNumber = "0";
        newRow.numberOfReturnedGroupsForTest = "0";
        newRow.numberOfReturnedGroupsForTestActual = "0";
        newRow.returnRateForTest = "0";
        newRow.numberOfReturnedGroupsForTestWeek = "0";
        newRow.numberOfReturnedGroupsForTestActualWeek = "0";
        newRow.returnRateForTestWeek = "0";
        newRow.numberOfReturnedGroupsForQa = "0";
        newRow.numberOfReturnedGroupsForQaActual = "0";
        newRow.returnRateForQa = "0";
        standardvalueListGrid.addRow(newRow, 0);
    }
    //..
    function saveBusiness() {
        standardvalueListGrid.validate();
        if (!standardvalueListGrid.isValid()) {
            var error = standardvalueListGrid.getCellErrors()[0];
            standardvalueListGrid.beginEditCell(error.record, error.column);
            mini.alert(error.column.header + error.errorText);
            return;
        }

        var postData = standardvalueListGrid.getChanges();
        var postDataJson = mini.encode(postData);
        $.ajax({
            url: jsUseCtxPath + "/serviceEngineering/core/standardvalue/saveBusiness.do",
            type: 'POST',
            contentType: 'application/json',
            data: postDataJson,
            success: function (returnData) {
                if (returnData && returnData.message) {
                    mini.alert(returnData.message, standardvalueList_name2, function () {
                        if (returnData.success) {
                            standardvalueListGrid.reload();
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
            e.errorText = standardvalueList_name3;
        }
        if (e.field == 'signMonth' && (!e.value || e.value == '')) {
            e.isValid = false;
            e.errorText = standardvalueList_name3;
        }
        if (e.field == 'signWeek' && (!e.value || e.value == '')) {
            e.isValid = false;
            e.errorText = standardvalueList_name3;
        }
    }
</script>
<redxun:gridScript gridId="standardvalueListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>