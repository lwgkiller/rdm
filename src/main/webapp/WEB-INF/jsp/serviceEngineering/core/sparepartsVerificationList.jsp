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
                    <span class="text" style="width:auto"><spring:message code="page.sparepartsVerificationList.name" />：</span>
                    <input class="mini-textbox" id="signYear" name="signYear"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.sparepartsVerificationList.name1" />：</span>
                    <input class="mini-textbox" id="signMonth" name="signMonth"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.sparepartsVerificationList.name2" />：</span>
                    <input class="mini-textbox" id="signWeek" name="signWeek"/>
                </li>
                <li style="margin-left: 10px">
                    <f:a alias="sparepartsVerification-search" onclick="searchFrm()" showNoRight="false" style="margin-right: 5px"><spring:message code="page.sparepartsVerificationList.name3" /></f:a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()"><spring:message code="page.sparepartsVerificationList.name4" /></a>
                    <f:a alias="sparepartsVerification-add" onclick="addBusiness()" showNoRight="false" style="margin-right: 5px"><spring:message code="page.sparepartsVerificationList.name5" /></f:a>
                    <f:a alias="sparepartsVerification-save" onclick="saveBusiness()" showNoRight="false" style="margin-right: 5px"><spring:message code="page.sparepartsVerificationList.name6" /></f:a>
                    <f:a alias="sparepartsVerification-remove" onclick="removeBusiness()" showNoRight="false" style="margin-right: 5px"><spring:message code="page.sparepartsVerificationList.name7" /></f:a>
                </li>
            </ul>
        </form>
        <span class="searchSelectionBtn" onclick="no_search(this ,'searchForm')">
				<i class="icon-sc-lower"></i>
        </span>
    </div>
</div>

<div class="mini-fit" style="height: 100%;">
    <div id="sparepartsVerificationListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         showCellTip="true" idField="id" allowCellEdit="true" allowCellSelect="true"
         multiSelect="true" showColumnsMenu="false" sizeList="[100,500]" pageSize="100" allowAlternating="true" pagerButtons="#pagerButtons"
         url="${ctxPath}/serviceEngineering/core/sparepartsVerification/dataListQuery.do" oncellvalidation="onCellValidation">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div type="indexcolumn" width="35" headerAlign="center" align="center"><spring:message code="page.sparepartsVerificationList.name8" /></div>
            <div field="signYear" width="80" headerAlign="center" align="center"><spring:message code="page.sparepartsVerificationList.name" />
                <input property="editor" class="mini-combobox" style="width:100%;"
                       url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=signYear"
                       valueField="key" textField="value"/>
            </div>
            <div field="signMonth" width="80" headerAlign="center" align="center"><spring:message code="page.sparepartsVerificationList.name1" />
                <input property="editor" class="mini-combobox" style="width:100%;"
                       url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=signMonth"
                       valueField="key" textField="value"/>
            </div>
            <div field="signWeek" width="80" headerAlign="center" align="center"><spring:message code="page.sparepartsVerificationList.name2" />
                <input property="editor" class="mini-combobox" style="width:100%;"
                       url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=signWeek"
                       valueField="key" textField="value"/>
            </div>
            <div field="verificationAmount" name="verificationAmount" width="120" headerAlign="center" align="center"><spring:message code="page.sparepartsVerificationList.name9" />
                <input property="editor" class="mini-spinner" style="width:100%;" maxValue="9999999"/>
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var sparepartsVerificationListGrid = mini.get("sparepartsVerificationListGrid");
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
            rows = sparepartsVerificationListGrid.getSelecteds();
        }
        if (rows.length <= 0) {
            mini.alert(sparepartsVerificationList_name);
            return;
        }
        mini.confirm(sparepartsVerificationList_name1, sparepartsVerificationList_name2, function (action) {
            if (action != 'ok') {
                return;
            } else {
                var rowIds = [];
                for (var i = 0, l = rows.length; i < l; i++) {
                    var r = rows[i];
                    if (r.id == "") {
                        sparepartsVerificationListGrid.removeRow(r, false);
                    } else {
                        rowIds.push(r.id);
                    }
                }
                if (rowIds.length > 0) {
                    _SubmitJson({
                        url: jsUseCtxPath + "/serviceEngineering/core/sparepartsVerification/deleteData.do",
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
        newRow.verificationAmount = "0";
        sparepartsVerificationListGrid.addRow(newRow, 0);
    }
    //..
    function saveBusiness() {
        sparepartsVerificationListGrid.validate();
        if (!sparepartsVerificationListGrid.isValid()) {
            var error = sparepartsVerificationListGrid.getCellErrors()[0];
            sparepartsVerificationListGrid.beginEditCell(error.record, error.column);
            mini.alert(error.column.header + error.errorText);
            return;
        }

        var postData = sparepartsVerificationListGrid.getChanges();
        var postDataJson = mini.encode(postData);
        $.ajax({
            url: jsUseCtxPath + "/serviceEngineering/core/sparepartsVerification/saveBusiness.do",
            type: 'POST',
            contentType: 'application/json',
            data: postDataJson,
            success: function (returnData) {
                if (returnData && returnData.message) {
                    mini.alert(returnData.message, sparepartsVerificationList_name2, function () {
                        if (returnData.success) {
                            sparepartsVerificationListGrid.reload();
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
            e.errorText = sparepartsVerificationList_name3;
        }
        if (e.field == 'signMonth' && (!e.value || e.value == '')) {
            e.isValid = false;
            e.errorText = sparepartsVerificationList_name3;
        }
        if (e.field == 'signWeek' && (!e.value || e.value == '')) {
            e.isValid = false;
            e.errorText = sparepartsVerificationList_name3;
        }
    }
</script>
<redxun:gridScript gridId="sparepartsVerificationListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>