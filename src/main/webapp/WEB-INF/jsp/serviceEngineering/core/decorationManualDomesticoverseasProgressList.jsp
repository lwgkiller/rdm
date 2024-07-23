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
                    <span class="text" style="width:auto"><spring:message code="page.decorationManualDomesticoverseasProgressList.name" />：</span>
                    <input class="mini-textbox" id="signYear" name="signYear"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.decorationManualDomesticoverseasProgressList.name1" />：</span>
                    <input class="mini-textbox" id="planType" name="planType"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.decorationManualDomesticoverseasProgressList.name2" />：</span>
                    <input class="mini-textbox" id="materialCode" name="materialCode"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.decorationManualDomesticoverseasProgressList.name3" />：</span>
                    <input class="mini-textbox" id="salesModel" name="salesModel"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.decorationManualDomesticoverseasProgressList.name4" />：</span>
                    <input class="mini-textbox" id="designModel" name="designModel"/>
                </li>
                <li style="margin-left: 10px">
                    <f:a alias="domesticOverseasProgress-searchFrm" onclick="searchFrm()" showNoRight="false"><spring:message code="page.decorationManualDomesticoverseasProgressList.name5" /></f:a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()"><spring:message code="page.decorationManualDomesticoverseasProgressList.name6" /></a>
                    <f:a alias="domesticOverseasProgress-addBusiness" onclick="addBusiness()" showNoRight="false"><spring:message code="page.decorationManualDomesticoverseasProgressList.name7" /></f:a>
                    <f:a alias="domesticOverseasProgress-saveBusiness" onclick="saveBusiness()" showNoRight="false"><spring:message code="page.decorationManualDomesticoverseasProgressList.name8" /></f:a>
                    <f:a alias="domesticOverseasProgress-removeBusiness" onclick="removeBusiness()" showNoRight="false"><spring:message code="page.decorationManualDomesticoverseasProgressList.name9" /></f:a>

                </li>
            </ul>
        </form>
        <span class="searchSelectionBtn" onclick="no_search(this ,'searchForm')">
				<i class="icon-sc-lower"></i>
        </span>
    </div>
</div>

<div class="mini-fit" style="height: 100%;">
    <div id="decorationManualDomesticoverseasProgressListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         allowCellWrap="false"
         showCellTip="true" idField="id" allowCellEdit="true" allowCellSelect="true" oncellvalidation="onCellValidation"
         multiSelect="true" showColumnsMenu="false" sizeList="[1000,5000]" pageSize="1000" allowAlternating="true" pagerButtons="#pagerButtons"
         url="${ctxPath}/serviceEngineering/core/decorationManual/domesticOverseasProgress/dataListQuery.do">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div field="orderNum" width="40" headerAlign="center" align="center" renderer="render"><spring:message code="page.decorationManualDomesticoverseasProgressList.name10" />
                <input property="editor" class="mini-spinner" value="0" maxValue="1000000"/>
            </div>
            <div field="signYear" width="40" headerAlign="center" align="center"><spring:message code="page.decorationManualDomesticoverseasProgressList.name" />
                <input property="editor" class="mini-combobox" style="width:100%;"
                       url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=signYear"
                       valueField="key" textField="value"/>
            </div>
            <div field="planType" width="60" headerAlign="center" align="center"><spring:message code="page.decorationManualDomesticoverseasProgressList.name1" />
                <input property="editor" class="mini-combobox" style="width:100%;"
                       url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=serviceEngineeringDecorationManualPlanType"
                       valueField="key" textField="value"/>
            </div>
            <div field="materialCode" width="60" headerAlign="center" align="center" renderer="render"><spring:message code="page.decorationManualDomesticoverseasProgressList.name2" />
                <input property="editor" class="mini-textbox"/>
            </div>
            <div field="salesModel" width="80" headerAlign="center" align="center" renderer="render"><spring:message code="page.decorationManualDomesticoverseasProgressList.name3" />
                <input property="editor" class="mini-textbox"/>
            </div>
            <div field="designModel" width="80" headerAlign="center" align="center" renderer="render"><spring:message code="page.decorationManualDomesticoverseasProgressList.name4" />
                <input property="editor" class="mini-textbox"/>
            </div>
            <div field="decorationManual" width="40" headerAlign="center" align="center"><spring:message code="page.decorationManualDomesticoverseasProgressList.name11" />
                <input property="editor" class="mini-spinner" style="width:100%;" minValue="0" maxValue="100"/>
            </div>
            <div field="disassemblyAndAssemblyManual" width="70" headerAlign="center" align="center"><spring:message code="page.decorationManualDomesticoverseasProgressList.name12" />
                <input property="editor" class="mini-spinner" style="width:100%;" minValue="0" maxValue="100"/>
            </div>
            <div field="structurefunctionAndMaintenanceManual" width="90" headerAlign="center" align="center"><spring:message code="page.decorationManualDomesticoverseasProgressList.name13" />
                <input property="editor" class="mini-spinner" style="width:100%;" minValue="0" maxValue="100"/>
            </div>
            <div field="testAndAdjustmentManual" width="70" headerAlign="center" align="center"><spring:message code="page.decorationManualDomesticoverseasProgressList.name14" />
                <input property="editor" class="mini-spinner" style="width:100%;" minValue="0" maxValue="100"/>
            </div>
            <div field="troubleshootingManual" width="60" headerAlign="center" align="center"><spring:message code="page.decorationManualDomesticoverseasProgressList.name15" />
                <input property="editor" class="mini-spinner" style="width:100%;" minValue="0" maxValue="100"/>
            </div>
            <div field="torqueAndToolStandardValueTable" width="90" headerAlign="center" align="center"><spring:message code="page.decorationManualDomesticoverseasProgressList.name16" />
                <input property="editor" class="mini-spinner" style="width:100%;" minValue="0" maxValue="100"/>
            </div>
            <div field="maintenanceStandardValueTable" width="60" headerAlign="center" align="center"><spring:message code="page.decorationManualDomesticoverseasProgressList.name17" />
                <input property="editor" class="mini-spinner" style="width:100%;" minValue="0" maxValue="100"/>
            </div>
            <div field="engineManual" width="60" headerAlign="center" align="center"><spring:message code="page.decorationManualDomesticoverseasProgressList.name18" />
                <input property="editor" class="mini-spinner" style="width:100%;" minValue="0" maxValue="100"/>
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var decorationManualDomesticoverseasProgressListGrid = mini.get("decorationManualDomesticoverseasProgressListGrid");
    $(function () {
        var date = new Date();
        currentYear = date.getFullYear().toString();
        mini.get("signYear").setValue(currentYear);
        searchFrm()
    });
    //..
    function removeBusiness(record) {
        var rows = [];
        if (record) {
            rows.push(record);
        } else {
            rows = decorationManualDomesticoverseasProgressListGrid.getSelecteds();
        }
        if (rows.length <= 0) {
            mini.alert(decorationManualDomesticoverseasProgressList_name);
            return;
        }
        mini.confirm(decorationManualDomesticoverseasProgressList_name1, decorationManualDomesticoverseasProgressList_name2, function (action) {
            if (action != 'ok') {
                return;
            } else {
                var rowIds = [];
                for (var i = 0, l = rows.length; i < l; i++) {
                    var r = rows[i];
                    if (r.id == "") {
                        decorationManualDomesticoverseasProgressListGrid.removeRow(r, false);
                    } else {
                        rowIds.push(r.id);
                    }
                }
                if (rowIds.length > 0) {
                    _SubmitJson({
                        url: jsUseCtxPath + "/serviceEngineering/core/decorationManual/domesticOverseasProgress/deleteData.do",
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
        newRow.decorationManual = "0";
        newRow.disassemblyAndAssemblyManual = "0";
        newRow.structurefunctionAndMaintenanceManual = "0";
        newRow.testAndAdjustmentManual = "0";
        newRow.troubleshootingManual = "0";
        newRow.torqueAndToolStandardValueTable = "0";
        newRow.maintenanceStandardValueTable = "0";
        newRow.engineManual = "0";
        decorationManualDomesticoverseasProgressListGrid.addRow(newRow, 0);
    }
    //..
    function saveBusiness() {
        decorationManualDomesticoverseasProgressListGrid.validate();
        if (!decorationManualDomesticoverseasProgressListGrid.isValid()) {
            var error = decorationManualDomesticoverseasProgressListGrid.getCellErrors()[0];
            decorationManualDomesticoverseasProgressListGrid.beginEditCell(error.record, error.column);
            mini.alert(error.column.header + error.errorText);
            return;
        }

        var postData = decorationManualDomesticoverseasProgressListGrid.getChanges();
        var postDataJson = mini.encode(postData);
        $.ajax({
            url: jsUseCtxPath + "/serviceEngineering/core/decorationManual/domesticOverseasProgress/saveBusiness.do",
            type: 'POST',
            contentType: 'application/json',
            data: postDataJson,
            success: function (returnData) {
                if (returnData && returnData.message) {
                    mini.alert(returnData.message, decorationManualDomesticoverseasProgressList_name2, function () {
                        if (returnData.success) {
                            decorationManualDomesticoverseasProgressListGrid.reload();
                        }
                    });
                }
            }
        });
    }
    //
    function onCellValidation(e) {
        if (e.field == 'signYear' && (!e.value || e.value == '')) {
            e.isValid = false;
            e.errorText = decorationManualDomesticoverseasProgressList_name3;
        }
        if (e.field == 'planType' && (!e.value || e.value == '')) {
            e.isValid = false;
            e.errorText = decorationManualDomesticoverseasProgressList_name3;
        }
        if (e.field == 'materialCode' && (!e.value || e.value == '')) {
            e.isValid = false;
            e.errorText = decorationManualDomesticoverseasProgressList_name3;
        }
        if (e.field == 'salesModel' && (!e.value || e.value == '')) {
            e.isValid = false;
            e.errorText = decorationManualDomesticoverseasProgressList_name3;
        }
        if (e.field == 'designModel' && (!e.value || e.value == '')) {
            e.isValid = false;
            e.errorText = decorationManualDomesticoverseasProgressList_name3;
        }
    }
</script>
<redxun:gridScript gridId="decorationManualDomesticoverseasProgressListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>