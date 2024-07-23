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
                    <span class="text" style="width:auto">设计型号：</span>
                    <input class="mini-textbox" id="designModel" name="designModel"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">整机物料号：</span>
                    <input class="mini-textbox" id="materialCode" name="materialCode"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">pin码第四位：</span>
                    <input class="mini-textbox" id="pin4" name="pin4"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">整机编号：</span>
                    <input class="mini-textbox" id="pin" name="pin"/>
                </li>
                <li style="margin-left: 10px">
                    <f:a alias="disassemblyPlan-searchFrm" onclick="searchFrm()" showNoRight="false">查询</f:a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <f:a alias="disassemblyPlan-addBusiness" onclick="addBusiness()" showNoRight="false">新增</f:a>
                    <f:a alias="disassemblyPlan-saveBusiness" onclick="saveBusiness()" showNoRight="false">保存</f:a>
                    <f:a alias="disassemblyPlan-removeBusiness" onclick="removeBusiness()" showNoRight="false">删除</f:a>

                </li>
            </ul>
        </form>
        <span class="searchSelectionBtn" onclick="no_search(this ,'searchForm')">
				<i class="icon-sc-lower"></i>
        </span>
    </div>
</div>

<div class="mini-fit" style="height: 100%;">
    <div id="maintainabilityDisassemblyPlanListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         allowCellWrap="false"
         showCellTip="true" idField="id" allowCellEdit="true" allowCellSelect="true" oncellvalidation="onCellValidation"
         multiSelect="true" showColumnsMenu="false" sizeList="[1000,5000]" pageSize="1000" allowAlternating="true" pagerButtons="#pagerButtons"
         url="${ctxPath}/serviceEngineering/core/maintainability/disassemblyplan/dataListQuery.do">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div field="orderNum" width="60" headerAlign="center" align="center" renderer="render">序号
                <input property="editor" class="mini-spinner" value="0" maxValue="1000000"/>
            </div>
            <div field="productInstituteId" displayField="productInstitute" width="100" headerAlign="center" align="center" renderer="render">产品所
                <input property="editor"
                       class="mini-buttonedit icon-dep-button"
                       required="true" allowInput="false"
                       onbuttonclick="selectMainDep" style="width:98%"/>
            </div>
            <div field="designModel" width="120" headerAlign="center" align="center" renderer="render">设计型号
                <input property="editor" class="mini-textbox"/>
            </div>
            <div field="materialCode" width="100" headerAlign="center" align="center" renderer="render">整机物料号
                <input property="editor" class="mini-textbox"/>
            </div>
            <div field="pin4" width="100" headerAlign="center" align="center" renderer="render">pin码第四位
                <input property="editor" class="mini-textbox"/>
            </div>
            <div field="disassemblyReason" width="200" headerAlign="center" align="center" renderer="render">拆解原因
                <input property="editor" class="mini-textbox"/>
            </div>
            <div field="pin" width="150" headerAlign="center" align="center" renderer="render">整机编号
                <input property="editor" class="mini-textbox"/>
            </div>
            <div field="productDirectorIds" displayField="productDirector" width="80" headerAlign="center" align="center" renderer="render">产品主管
                <input property="editor" class="mini-user rxc"
                       plugins="mini-user" style="width:98%;height:34px;" allowinput="false" length="80"
                       mainfield="no" single="false"/>
            </div>
            <div field="instituteDirectorIds" displayField="instituteDirector" width="80" headerAlign="center" align="center" renderer="render">所长
                <input property="editor" class="mini-user rxc"
                       plugins="mini-user" style="width:98%;height:34px;" allowinput="false" length="80"
                       mainfield="no" single="false"/>
            </div>
            <div field="state" width="60" headerAlign="center" align="center" renderer="render">状态
                <input property="editor" class="mini-combobox" style="width:98%"
                       url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=serviceEngineeringMaintainabilityDisassemblyPlanState"
                       valueField="key" textField="key"/>
            </div>
            <div field="isOnRoad" width="80" headerAlign="center" align="center" renderer="render">是否在路试
                <input property="editor" class="mini-combobox" textField="value" valueField="key" allowInput="false"
                       data="[{key: '是',value:'是'},{key: '否',value:'否'}]"/>
            </div>
            <div field="latestLocation" width="200" headerAlign="center" align="center" renderer="render">最新位置
                <input property="editor" class="mini-textbox"/>
            </div>
            <div field="isNewLook" width="80" headerAlign="center" align="center" renderer="render">是否新外观
                <input property="editor" class="mini-combobox" textField="value" valueField="key" allowInput="false"
                       data="[{key: '是',value:'是'},{key: '否',value:'否'}]"/>
            </div>
            <div field="isInDisassemblyStatus" width="120" headerAlign="center" align="center" renderer="render">是否具备拆解状态
                <input property="editor" class="mini-combobox" textField="value" valueField="key" allowInput="false"
                       data="[{key: '是',value:'是'},{key: '否',value:'否'}]"/>
            </div>
            <div field="suggestedDisassemblyDate" width="100" headerAlign="center" align="center" dateFormat="yyyy-MM-dd" renderer="render">建议拆解日期
                <input property="editor" class="mini-datepicker" format="yyyy-MM-dd"
                       showTime="false" showOkButton="true" showClearButton="false"/>
            </div>
            <div field="serviceDepartmentStaffIds" displayField="serviceDepartmentStaff" width="120" headerAlign="center" align="center"
                 renderer="render">服务部人员
                <input property="editor" class="mini-user rxc"
                       plugins="mini-user" style="width:98%;height:34px;" allowinput="false" length="80"
                       mainfield="no" single="false"/>
            </div>
            <div field="serviceEngineeringInstituteStaffIds" displayField="serviceEngineeringInstituteStaff" width="120" headerAlign="center"
                 align="center" renderer="render">服务工程所人员
                <input property="editor" class="mini-user rxc"
                       plugins="mini-user" style="width:98%;height:34px;" allowinput="false" length="80"
                       mainfield="no" single="false"/>
            </div>
            <div field="productInstituteStaffIds" displayField="productInstituteStaff" width="120" headerAlign="center" align="center"
                 renderer="render">产品所人员
                <input property="editor" class="mini-user rxc"
                       plugins="mini-user" style="width:98%;height:34px;" allowinput="false" length="80"
                       mainfield="no" single="false"/>
            </div>
            <div field="processDepartmentStaffIds" displayField="processDepartmentStaff" width="120" headerAlign="center" align="center"
                 renderer="render">工艺技术部人员
                <input property="editor" class="mini-user rxc"
                       plugins="mini-user" style="width:98%;height:34px;" allowinput="false" length="80"
                       mainfield="no" single="false"/>
            </div>
            <div field="standardsInstituteStaffIds" displayField="standardsInstituteStaff" width="120" headerAlign="center" align="center"
                 renderer="render">标准所人员
                <input property="editor" class="mini-user rxc"
                       plugins="mini-user" style="width:98%;height:34px;" allowinput="false" length="80"
                       mainfield="no" single="false"/>
            </div>
            <div field="qualityDepartmentStaffIds" displayField="qualityDepartmentStaff" width="120" headerAlign="center" align="center"
                 renderer="render">质量保证部人员
                <input property="editor" class="mini-user rxc"
                       plugins="mini-user" style="width:98%;height:34px;" allowinput="false" length="80"
                       mainfield="no" single="false"/>
            </div>
            <div field="disassemblyTeamStaffIds" displayField="disassemblyTeamStaff" width="120" headerAlign="center" align="center"
                 renderer="render">
                拆解小组人员
                <input property="editor" class="mini-user rxc"
                       plugins="mini-user" style="width:98%;height:34px;" allowinput="false" length="80"
                       mainfield="no" single="false"/>
            </div>
            <div field="siteProviderIds" displayField="siteProvider" width="120" headerAlign="center" align="center" renderer="render">场地提供人员
                <input property="editor" class="mini-user rxc"
                       plugins="mini-user" style="width:98%;height:34px;" allowinput="false" length="80"
                       mainfield="no" single="false"/>
            </div>
            <div field="remark" width="300" headerAlign="center" align="center" renderer="render">备注
                <input property="editor" class="mini-textbox"/>
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var maintainabilityDisassemblyPlanListGrid = mini.get("maintainabilityDisassemblyPlanListGrid");
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
            rows = maintainabilityDisassemblyPlanListGrid.getSelecteds();
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
                        maintainabilityDisassemblyPlanListGrid.removeRow(r, false);
                    } else {
                        rowIds.push(r.id);
                    }
                }
                if (rowIds.length > 0) {
                    _SubmitJson({
                        url: jsUseCtxPath + "/serviceEngineering/core/maintainability/disassemblyplan/deleteData.do",
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
        maintainabilityDisassemblyPlanListGrid.addRow(newRow, 0);
    }
    //..
    function saveBusiness() {
        maintainabilityDisassemblyPlanListGrid.validate();
        if (!maintainabilityDisassemblyPlanListGrid.isValid()) {
            var error = maintainabilityDisassemblyPlanListGrid.getCellErrors()[0];
            maintainabilityDisassemblyPlanListGrid.beginEditCell(error.record, error.column);
            mini.alert(error.column.header + error.errorText);
            return;
        }

        var postData = maintainabilityDisassemblyPlanListGrid.getChanges();
        var postDataJson = mini.encode(postData);
        $.ajax({
            url: jsUseCtxPath + "/serviceEngineering/core/maintainability/disassemblyplan/saveBusiness.do",
            type: 'POST',
            contentType: 'application/json',
            data: postDataJson,
            success: function (returnData) {
                if (returnData && returnData.message) {
                    mini.alert(returnData.message, '提示', function () {
                        if (returnData.success) {
                            maintainabilityDisassemblyPlanListGrid.reload();
                        }
                    });
                }
            }
        });
    }
    //
    function onCellValidation(e) {
        if (e.field == 'productInstituteId' && (!e.value || e.value == '')) {
            e.isValid = false;
            e.errorText = '不能为空！';
        }
        if (e.field == 'productDirectorIds' && (!e.value || e.value == '')) {
            e.isValid = false;
            e.errorText = '不能为空！';
        }
        if (e.field == 'instituteDirectorIds' && (!e.value || e.value == '')) {
            e.isValid = false;
            e.errorText = '不能为空！';
        }
        if (e.field == 'state' && (!e.value || e.value == '')) {
            e.isValid = false;
            e.errorText = '不能为空！';
        }
        if (e.field == 'isOnRoad' && (!e.value || e.value == '')) {
            e.isValid = false;
            e.errorText = '不能为空！';
        }
        if (e.field == 'isNewLook' && (!e.value || e.value == '')) {
            e.isValid = false;
            e.errorText = '不能为空！';
        }
        if (e.field == 'isInDisassemblyStatus' && (!e.value || e.value == '')) {
            e.isValid = false;
            e.errorText = '不能为空！';
        }
        if (e.field == 'suggestedDisassemblyDate' && (!e.value || e.value == '')) {
            e.isValid = false;
            e.errorText = '不能为空！';
        }
        if (e.field == 'serviceDepartmentStaffIds' && (!e.value || e.value == '')) {
            e.isValid = false;
            e.errorText = '不能为空！';
        }
        if (e.field == 'serviceEngineeringInstituteStaffIds' && (!e.value || e.value == '')) {
            e.isValid = false;
            e.errorText = '不能为空！';
        }
        if (e.field == 'productInstituteStaffIds' && (!e.value || e.value == '')) {
            e.isValid = false;
            e.errorText = '不能为空！';
        }
        if (e.field == 'processDepartmentStaffIds' && (!e.value || e.value == '')) {
            e.isValid = false;
            e.errorText = '不能为空！';
        }
        if (e.field == 'standardsInstituteStaffIds' && (!e.value || e.value == '')) {
            e.isValid = false;
            e.errorText = '不能为空！';
        }
        if (e.field == 'qualityDepartmentStaffIds' && (!e.value || e.value == '')) {
            e.isValid = false;
            e.errorText = '不能为空！';
        }
        if (e.field == 'disassemblyTeamStaffIds' && (!e.value || e.value == '')) {
            e.isValid = false;
            e.errorText = '不能为空！';
        }
        if (e.field == 'siteProviderIds' && (!e.value || e.value == '')) {
            e.isValid = false;
            e.errorText = '不能为空！';
        }
    }
    //
    function render(e) {
        debugger;
        if (e.cellHtml != null && e.cellHtml != "") {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.cellHtml + '</span>';
            return html;
        }
    }

</script>
<redxun:gridScript gridId="maintainabilityDisassemblyPlanListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>