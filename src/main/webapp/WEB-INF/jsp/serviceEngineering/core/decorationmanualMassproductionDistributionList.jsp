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
                    <span class="text" style="width:auto"><spring:message code="page.decorationmanualMassproductionDistributionList.name" />：</span>
                    <input class="mini-textbox" id="signYear" name="signYear"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.decorationmanualMassproductionDistributionList.name1" />：</span>
                    <input class="mini-textbox" id="productInstitute" name="productInstitute"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.decorationmanualMassproductionDistributionList.name2" />：</span>
                    <input class="mini-textbox" id="planType" name="planType"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.decorationmanualMassproductionDistributionList.name3" />：</span>
                    <input class="mini-textbox" id="materialCode" name="materialCode"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.decorationmanualMassproductionDistributionList.name4" />：</span>
                    <input class="mini-textbox" id="salesModel" name="salesModel"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.decorationmanualMassproductionDistributionList.name5" />：</span>
                    <input class="mini-textbox" id="designModel" name="designModel"/>
                </li>
                <li style="margin-left: 10px">
                    <f:a alias="massproductionDistribution-searchFrm" onclick="searchFrm()" showNoRight="false"><spring:message code="page.decorationmanualMassproductionDistributionList.name6" /></f:a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()"><spring:message code="page.decorationmanualMassproductionDistributionList.name7" /></a>
                    <f:a alias="massproductionDistribution-addBusiness" onclick="addBusiness()" showNoRight="false"><spring:message code="page.decorationmanualMassproductionDistributionList.name8" /></f:a>
                    <f:a alias="massproductionDistribution-saveBusiness" onclick="saveBusiness()" showNoRight="false"><spring:message code="page.decorationmanualMassproductionDistributionList.name9" /></f:a>
                    <f:a alias="massproductionDistribution-removeBusiness" onclick="removeBusiness()" showNoRight="false"><spring:message code="page.decorationmanualMassproductionDistributionList.name10" /></f:a>
                    <f:a alias="massproductionDistribution-calculateBusiness" onclick="calculateBusiness()" showNoRight="false"><spring:message code="page.decorationmanualMassproductionDistributionList.name11" /></f:a>
                </li>
            </ul>
        </form>
        <span class="searchSelectionBtn" onclick="no_search(this ,'searchForm')">
				<i class="icon-sc-lower"></i>
        </span>
    </div>
</div>

<div class="mini-fit" style="height: 100%;">
    <div id="decorationmanualMassproductionDistributionListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         allowCellWrap="false"
         showCellTip="true" idField="id" allowCellEdit="true" allowCellSelect="true" oncellvalidation="onCellValidation"
         multiSelect="true" showColumnsMenu="false" sizeList="[1000,5000]" pageSize="1000" allowAlternating="true" pagerButtons="#pagerButtons"
         url="${ctxPath}/serviceEngineering/core/decorationManual/massproductionDistribution/dataListQuery.do">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div field="orderNum" width="40" headerAlign="center" align="center" renderer="render"><spring:message code="page.decorationmanualMassproductionDistributionList.name12" />
                <input property="editor" class="mini-spinner" value="0" maxValue="1000000"/>
            </div>
            <div field="signYear" width="40" headerAlign="center" align="center"><spring:message code="page.decorationmanualMassproductionDistributionList.name" />
                <input property="editor" class="mini-combobox" style="width:100%;"
                       url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=signYear"
                       valueField="key" textField="value"/>
            </div>
            <div field="productInstituteId" displayField="productInstitute" width="100" headerAlign="center" align="center" renderer="render"><spring:message code="page.decorationmanualMassproductionDistributionList.name1" />
                <input property="editor"
                       class="mini-buttonedit icon-dep-button"
                       required="true" allowInput="false"
                       onbuttonclick="selectMainDep" style="width:98%"/>
            </div>
            <div field="planType" width="60" headerAlign="center" align="center"><spring:message code="page.decorationmanualMassproductionDistributionList.name2" />
                <input property="editor" class="mini-combobox" style="width:100%;"
                       url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=serviceEngineeringDecorationManualPlanType"
                       valueField="key" textField="value"/>
            </div>
            <div field="materialCode" width="80" headerAlign="center" align="center" renderer="render"><spring:message code="page.decorationmanualMassproductionDistributionList.name3" />
                <input property="editor" class="mini-textbox"/>
            </div>
            <div field="salesModel" width="80" headerAlign="center" align="center" renderer="render"><spring:message code="page.decorationmanualMassproductionDistributionList.name4" />
                <input property="editor" class="mini-textbox"/>
            </div>
            <div field="designModel" width="80" headerAlign="center" align="center" renderer="render"><spring:message code="page.decorationmanualMassproductionDistributionList.name5" />
                <input property="editor" class="mini-textbox"/>
            </div>
            <div field="decorationManual" width="60" headerAlign="center" align="center"><spring:message code="page.decorationmanualMassproductionDistributionList.name13" />
                <%--<input property="editor" class="mini-spinner" style="width:100%;" minValue="0" maxValue="100"/>--%>
            </div>
            <div field="disassemblyAndAssemblyManual" width="100" headerAlign="center" align="center"><spring:message code="page.decorationmanualMassproductionDistributionList.name14" />
                <%--<input property="editor" class="mini-spinner" style="width:100%;" minValue="0" maxValue="100"/>--%>
            </div>
            <div field="structurefunctionAndMaintenanceManual" width="120" headerAlign="center" align="center"><spring:message code="page.decorationmanualMassproductionDistributionList.name15" />
                <%--<input property="editor" class="mini-spinner" style="width:100%;" minValue="0" maxValue="100"/>--%>
            </div>
            <div field="testAndAdjustmentManual" width="90" headerAlign="center" align="center"><spring:message code="page.decorationmanualMassproductionDistributionList.name16" />
                <%--<input property="editor" class="mini-spinner" style="width:100%;" minValue="0" maxValue="100"/>--%>
            </div>
            <div field="troubleshootingManual" width="80" headerAlign="center" align="center"><spring:message code="page.decorationmanualMassproductionDistributionList.name17" />
                <%--<input property="editor" class="mini-spinner" style="width:100%;" minValue="0" maxValue="100"/>--%>
            </div>
            <div field="torqueAndToolStandardValueTable" width="120" headerAlign="center" align="center"><spring:message code="page.decorationmanualMassproductionDistributionList.name18" />
                <%--<input property="editor" class="mini-spinner" style="width:100%;" minValue="0" maxValue="100"/>--%>
            </div>
            <div field="maintenanceStandardValueTable" width="80" headerAlign="center" align="center"><spring:message code="page.decorationmanualMassproductionDistributionList.name19" />
                <%--<input property="editor" class="mini-spinner" style="width:100%;" minValue="0" maxValue="100"/>--%>
            </div>
            <div field="engineManual" width="80" headerAlign="center" align="center"><spring:message code="page.decorationmanualMassproductionDistributionList.name20" />
                <%--<input property="editor" class="mini-spinner" style="width:100%;" minValue="0" maxValue="100"/>--%>
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var decorationmanualMassproductionDistributionListGrid = mini.get("decorationmanualMassproductionDistributionListGrid");
    $(function () {
        var date = new Date();
        currentYear = date.getFullYear().toString();
        mini.get("signYear").setValue(currentYear);
        searchFrm()
    });
    //选择主部门
    function selectMainDep(e) {
        var b = e.sender;
        _TenantGroupDlg('1', true, '', '1', function (g) {
            b.setValue(g.groupId);
            b.setText(g.name);
        }, false);

    }
    //..
    function removeBusiness(record) {
        var rows = [];
        if (record) {
            rows.push(record);
        } else {
            rows = decorationmanualMassproductionDistributionListGrid.getSelecteds();
        }
        if (rows.length <= 0) {
            mini.alert(decorationmanualMassproductionDistributionList_name);
            return;
        }
        mini.confirm(decorationmanualMassproductionDistributionList_name1, decorationmanualMassproductionDistributionList_name2, function (action) {
            if (action != 'ok') {
                return;
            } else {
                var rowIds = [];
                for (var i = 0, l = rows.length; i < l; i++) {
                    var r = rows[i];
                    if (r.id == "") {
                        decorationmanualMassproductionDistributionListGrid.removeRow(r, false);
                    } else {
                        rowIds.push(r.id);
                    }
                }
                if (rowIds.length > 0) {
                    _SubmitJson({
                        url: jsUseCtxPath + "/serviceEngineering/core/decorationManual/massproductionDistribution/deleteData.do",
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
        decorationmanualMassproductionDistributionListGrid.addRow(newRow, 0);
    }
    //..
    function saveBusiness() {
        decorationmanualMassproductionDistributionListGrid.validate();
        if (!decorationmanualMassproductionDistributionListGrid.isValid()) {
            var error = decorationmanualMassproductionDistributionListGrid.getCellErrors()[0];
            decorationmanualMassproductionDistributionListGrid.beginEditCell(error.record, error.column);
            mini.alert(error.column.header + error.errorText);
            return;
        }

        var postData = decorationmanualMassproductionDistributionListGrid.getChanges();
        var postDataJson = mini.encode(postData);
        $.ajax({
            url: jsUseCtxPath + "/serviceEngineering/core/decorationManual/massproductionDistribution/saveBusiness.do",
            type: 'POST',
            contentType: 'application/json',
            data: postDataJson,
            success: function (returnData) {
                if (returnData && returnData.message) {
                    mini.alert(returnData.message, decorationmanualMassproductionDistributionList_name2, function () {
                        if (returnData.success) {
                            decorationmanualMassproductionDistributionListGrid.reload();
                        }
                    });
                }
            }
        });
    }
    //..
    function calculateBusiness() {
        var postData = decorationmanualMassproductionDistributionListGrid.data;
        var postDataJson = mini.encode(postData);
        $.ajax({
            url: jsUseCtxPath + "/serviceEngineering/core/decorationManual/massproductionDistribution/calculateBusiness.do",
            type: 'POST',
            contentType: 'application/json',
            data: postDataJson,
            success: function (returnData) {
                if (returnData && returnData.message) {
                    mini.alert(returnData.message, decorationmanualMassproductionDistributionList_name2, function () {
                        if (returnData.success) {
                            decorationmanualMassproductionDistributionListGrid.reload();
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
            e.errorText = decorationmanualMassproductionDistributionList_name3;
        }
        if (e.field == 'productInstituteId' && (!e.value || e.value == '')) {
            e.isValid = false;
            e.errorText = decorationmanualMassproductionDistributionList_name3;
        }
        if (e.field == 'planType' && (!e.value || e.value == '')) {
            e.isValid = false;
            e.errorText = decorationmanualMassproductionDistributionList_name3;
        }
        if (e.field == 'materialCode' && (!e.value || e.value == '')) {
            e.isValid = false;
            e.errorText = decorationmanualMassproductionDistributionList_name3;
        }
        if (e.field == 'salesModel' && (!e.value || e.value == '')) {
            e.isValid = false;
            e.errorText = decorationmanualMassproductionDistributionList_name3';
        }
        if (e.field == 'designModel' && (!e.value || e.value == '')) {
            e.isValid = false;
            e.errorText = decorationmanualMassproductionDistributionList_name3;
        }
    }
</script>
<redxun:gridScript gridId="decorationmanualMassproductionDistributionListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>