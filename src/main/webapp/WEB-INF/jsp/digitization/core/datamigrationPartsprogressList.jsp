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
                    <span class="text" style="width:auto">系统/部件物料编码：</span>
                    <input class="mini-textbox" id="subMaterial" name="subMaterial"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">绘图人员：</span>
                    <input class="mini-textbox" id="draftsmans" name="draftsmans"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">对应工程师：</span>
                    <input class="mini-textbox" id="engineers" name="engineers"/>
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
                    <span class="text" style="width:auto">关键用户：</span>
                    <input class="mini-textbox" id="keyusers" name="keyusers"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">整机/部件完成总比：</span>
                    <input id="totalPercentageTag" name="totalPercentageTag" class="mini-combobox" align="center"
                           textField="key" valueField="value" style="width: 60px;"
                           data="[ {'key' : '大于','value' : '大于'},{'key' : '小于','value' : '小于'},{'key' : '等于','value' : '等于'}]"/>
                    <input class="mini-spinner" id="totalPercentage" name="totalPercentage" minValue="0" maxValue="100"
                           decimalPlaces="2" value="100"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">整机完成总比：</span>
                    <input id="machineTotalPercentageTag" name="machineTotalPercentageTag" class="mini-combobox" align="center"
                           textField="key" valueField="value" style="width: 60px;"
                           data="[ {'key' : '大于','value' : '大于'},{'key' : '小于','value' : '小于'},{'key' : '等于','value' : '等于'}]"/>
                    <input class="mini-spinner" id="machineTotalPercentage" name="machineTotalPercentage" minValue="0" maxValue="100"
                           decimalPlaces="2" value="100"/>
                </li>

                <li style="margin-left: 10px">
                    <f:a alias="datamigrationPartsprogress-searchFrm" onclick="searchFrm()" showNoRight="false">查询</f:a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <f:a alias="datamigrationPartsprogress-addBusiness" onclick="addBusiness()" showNoRight="false">新增</f:a>
                    <f:a alias="datamigrationPartsprogress-saveBusiness" onclick="saveBusiness()" showNoRight="false">保存</f:a>
                    <f:a alias="datamigrationPartsprogress-removeBusiness" onclick="removeBusiness()" showNoRight="false">删除</f:a>
                    <f:a alias="datamigrationPartsprogress-calculateBusiness" onclick="calculateBusiness()" showNoRight="false">计算</f:a>
                    <%--<a class="mini-button" style="margin-right: 5px" plain="true" onclick="clearFilter()">清空过滤</a>--%>
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
         pagerButtons="#pagerButtons" virtualScroll="true"
    <%--showFilterRow="true"--%>
         url="${ctxPath}/digitization/core/datamigration/partsprogress/dataListQuery.do">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div field="orderNum" width="100" headerAlign="center" align="center">序号
                <input property="editor" class="mini-spinner" value="0" maxValue="1000000"/>
            </div>
            <div field="salesModel" width="80" headerAlign="center" align="center">销售型号
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
            <div field="subDescription" width="200" headerAlign="center" align="center">系统/部件描述
                <input property="editor" class="mini-textbox"/>
            </div>
            <div field="subMaterial" width="130" headerAlign="center" align="center">系统/部件物料编码
                <input property="editor" class="mini-textbox"/>
            </div>
            <div field="draftsmans" width="80" headerAlign="center" align="center">绘图人员
                <input property="editor" class="mini-textbox"/>
            </div>
            <div field="engineers" width="80" headerAlign="center" align="center">对应工程师
                <input property="editor" class="mini-textbox"/>
            </div>
            <div field="conversionPercentage" width="160" headerAlign="center" align="center">图纸转化完成百分比(%)
                <input property="editor" class="mini-spinner" style="width:100%;" minValue="0" maxValue="100" decimalPlaces="2"/>
            </div>
            <div field="proofreadingPercentage" width="170" headerAlign="center" align="center">工程师校审完成百分比(%)
                <input property="editor" class="mini-spinner" style="width:100%;" minValue="0" maxValue="100" decimalPlaces="2"/>
            </div>
            <div field="windchillPercentage" width="200" headerAlign="center" align="center">Windchill发布完成百分比(%)
                <input property="editor" class="mini-spinner" style="width:100%;" minValue="0" maxValue="100" decimalPlaces="2"/>
            </div>
            <div field="totalPercentage" width="180" headerAlign="center" align="center" enable>整机/部件完成总百分比(%)
                <input property="editor" class="mini-spinner" style="width:100%;" minValue="0" maxValue="100" decimalPlaces="2"/>
            </div>
            <div field="partsPercentage" width="180" headerAlign="center" align="center">部件完成占整机百分比(%)
                <input property="editor" class="mini-spinner" style="width:100%;" minValue="0" maxValue="100" decimalPlaces="2"/>
            </div>
            <div field="machinePercentage" width="160" headerAlign="center" align="center">整机完成百分比(%)
                <input property="editor" class="mini-spinner" style="width:100%;" minValue="0" maxValue="100" decimalPlaces="2"/>
            </div>
            <div field="machineTotalPercentage" width="140" headerAlign="center" align="center">整机完成总比(%)
                <%--<input id="machineTotalPercentageFilter" property="filter" class="mini-filteredit"--%>
                <%--filterData="machineTotalPercentageFilters" style="width:100%;"--%>
                <%--onvaluechanged="onFilterChanged" showClose="true"--%>
                <%--/>--%>
                <input property="editor" class="mini-spinner" style="width:100%;" minValue="0" maxValue="100" decimalPlaces="2"/>
            </div>
            <div field="productDirector" width="80" headerAlign="center" align="center">产品主管
                <input property="editor" class="mini-textbox"/>
            </div>

            <div field="productInstitute" width="100" headerAlign="center" align="center">产品所
                <input property="editor" class="mini-textbox"/>
            </div>
            <div field="keyusers" width="80" headerAlign="center" align="center">关键用户
                <input property="editor" class="mini-textbox"/>
            </div>
            <div field="remark" width="200" headerAlign="center" align="center">备注
                <input property="editor" class="mini-textbox"/>
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
    //    var machineTotalPercentageFilters = [{text: '大于', value: '>'}, {text: '小于', value: '<'}, {text: '等于', value: '=='}];
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
                        url: jsUseCtxPath + "/digitization/core/datamigration/partsprogress/deleteData.do",
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
        newRow.conversionPercentage = "0";
        newRow.proofreadingPercentage = "0";
        newRow.windchillPercentage = "0";
        newRow.totalPercentage = "0";
        newRow.partsPercentage = "0";
        newRow.machinePercentage = "0";
        newRow.machineTotalPercentage = "0";
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
            url: jsUseCtxPath + "/digitization/core/datamigration/partsprogress/saveBusiness.do",
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

    function calculateBusiness() {
        var postData = businessListGrid.getSelecteds();
        var postDataJson = mini.encode(postData);
        $.ajax({
            url: jsUseCtxPath + "/digitization/core/datamigration/partsprogress/calculateBusiness.do",
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
//        if (e.field == 'salesModel' && (!e.value || e.value == '')) {
//            e.isValid = false;
//            e.errorText = '不能为空！';
//        }
//        if (e.field == 'designModel' && (!e.value || e.value == '')) {
//            e.isValid = false;
//            e.errorText = '不能为空！';
//        }
        if (e.field == 'materialCode' && (!e.value || e.value == '')) {
            e.isValid = false;
            e.errorText = '不能为空！';
        }
        if (e.field == 'subMaterial' && (!e.value || e.value == '')) {
            e.isValid = false;
            e.errorText = '不能为空！';
        }
//        if (e.field == 'migrationContent' && (!e.value || e.value == '')) {
//            e.isValid = false;
//            e.errorText = '不能为空！';
//        }
//        if (e.field == 'draftsmanIds' && (!e.value || e.value == '')) {
//            e.isValid = false;
//            e.errorText = '不能为空！';
//        }
//        if (e.field == 'engineerIds' && (!e.value || e.value == '')) {
//            e.isValid = false;
//            e.errorText = '不能为空！';
//        }
//        if (e.field == 'productDirectorIds' && (!e.value || e.value == '')) {
//            e.isValid = false;
//            e.errorText = '不能为空！';
//        }
//        if (e.field == 'productInstituteId' && (!e.value || e.value == '')) {
//            e.isValid = false;
//            e.errorText = '不能为空！';
//        }
//        if (e.field == 'keyuserIds' && (!e.value || e.value == '')) {
//            e.isValid = false;
//            e.errorText = '不能为空！';
//        }
    }

    //////////////////////

    //    function onFilterChanged(e) {
    //        debugger;
    //        var machineTotalPercentageFilterbox = mini.get("machineTotalPercentageFilter");
    //
    //        var machineTotalPercentage = parseInt(machineTotalPercentageFilterbox.getValue().toLowerCase());
    //
    //        var machineTotalPercentageFilter = machineTotalPercentageFilterbox.getFilterValue().toLowerCase();
    //
    //        //多条件组合过滤
    //        businessListGrid.filter(function (row) {
    //            //machineTotalPercentageFilter
    //            var r2 = true;
    //            if (!isNaN(machineTotalPercentage) && machineTotalPercentageFilter) {
    //                r2 = false;
    //                if (machineTotalPercentageFilter == ">" && row.machineTotalPercentage > machineTotalPercentage) r2 = true;
    //                if (machineTotalPercentageFilter == "<" && row.machineTotalPercentage < machineTotalPercentage) r2 = true;
    //                if (machineTotalPercentageFilter == "==" && row.machineTotalPercentage == machineTotalPercentage) r2 = true;
    //            }
    //
    //            return r2;
    //        });
    //    }
    //
    //    function clearFilter() {
    //        var machineTotalPercentageFilterbox = mini.get("machineTotalPercentageFilter");
    //        machineTotalPercentageFilterbox.setValue("");
    //        machineTotalPercentageFilterbox.setFilterValue("");
    //        businessListGrid.clearFilter();
    //    }
</script>
<redxun:gridScript gridId="businessListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>