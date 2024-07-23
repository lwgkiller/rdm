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
                    <span class="text" style="width:auto">年份(A命令展示历年)：</span>
                    <input class="mini-textbox" id="signYear" name="signYear"/>
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
                    <span class="text" style="width:auto">整机物料号：</span>
                    <input class="mini-textbox" id="materialCode" name="materialCode"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">完备性评价：</span>
                    <input class="mini-textbox" id="completenessEvaluation" name="completenessEvaluation"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">检修标准值表：</span>
                    <input class="mini-combobox" style="width:98%" id="maintenanceStandardValueTable" name="maintenanceStandardValueTable"
                           valueField="key" textField="value"
                           data="[{'key': '常规版', 'value': '常规版'},{'key': '测试版', 'value': '测试版'},
                           {'key': '完整版', 'value': '完整版'},{'key': '', 'value': ''}]"/>
                </li>
                <li style="margin-right: 15px">
                    <input id="others" name="others" class="mini-checkboxlist" style="width:auto;"
                           repeatItems="10" repeatLayout="flow" repeatDirection="horizontal"
                           textfield="fieldName" valuefield="field"
                           data="[ {'fieldName' : '零件图册','field' : 'partsAtlas'},
                           {'fieldName' : '保养件清单','field' : 'maintenancePartsList'},
                           {'fieldName' : '易损件清单','field' : 'wearingPartsList'},
                           {'fieldName' : '常规版','field' : 'regularEdition'},
                           {'fieldName' : 'CE版','field' : 'CEEdition'},
                           {'fieldName' : '装箱单','field' : 'packingList'},
                           {'fieldName' : '装修手册','field' : 'decorationManual'},
                           {'fieldName' : '分解与组装手册','field' : 'disassAndAssManual'},
                           {'fieldName' : '结构功能与原理手册','field' : 'structurefunctionAndPrincipleManual'},
                           {'fieldName' : '测试与调整手册','field' : 'testAndAdjustmentManual'},
                           {'fieldName' : '故障诊断手册','field' : 'troubleshootingManual'},
                           {'fieldName' : '力矩及工具标准值表','field' : 'torqueAndToolStandardValueTable'},
                           {'fieldName' : '发动机手册','field' : 'engineManual'},
                           {'fieldName' : '全生命周期成本清单','field' : 'lifeCycleCostList'},
                           {'fieldName' : '空调使用与维护保养手册','field' : 'airconditioningUseAndMaintenanceManual'}]"/>
                </li>

                <li style="margin-left: 10px">
                    <f:a alias="generalKanbanCompleteness-searchFrm" onclick="searchFrm()" showNoRight="false" style="margin-right: 5px">查询</f:a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearFormThis()">清空查询</a>
                    <f:a alias="generalKanbanCompleteness-addBusiness" onclick="addBusiness()" showNoRight="false" style="margin-right: 5px">新增</f:a>
                    <f:a alias="generalKanbanCompleteness-saveBusiness" onclick="saveBusiness()" showNoRight="false"
                         style="margin-right: 5px">保存</f:a>
                    <f:a alias="generalKanbanCompleteness-removeBusiness" onclick="removeBusiness()" showNoRight="false"
                         style="margin-right: 5px">删除</f:a>
                    <f:a alias="generalKanbanCompleteness-exportBusiness" onclick="exportBusiness()" showNoRight="false"
                         style="margin-right: 5px">导出</f:a>
                    <f:a alias="generalKanbanCompleteness-openImportWindow" onclick="openImportWindow()" showNoRight="false"
                         style="margin-right: 5px">导入</f:a>
                </li>
            </ul>
        </form>
        <span class="searchSelectionBtn" onclick="no_search(this ,'searchForm')">
				<i class="icon-sc-lower"></i>
        </span>
    </div>
</div>

<div class="mini-fit" style="height: 100%;">
    <div id="seGeneralKanbanCompletenessListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" allowCellWrap="false"
         showCellTip="true" idField="id" allowCellEdit="true" allowCellSelect="true" oncellvalidation="onCellValidation"
         multiSelect="true" showColumnsMenu="false" sizeList="[100,5000]" pageSize="100" allowAlternating="true" pagerButtons="#pagerButtons"
         url="${ctxPath}/serviceEngineering/core/seGeneralKanbanCompleteness/dataListQuery.do" onbeforeload="beforeload"
         frozenStartColumn="0" frozenEndColumn="9">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div type="indexcolumn" width="35" headerAlign="center" align="center">序号</div>
            <div field="signYear" width="50" headerAlign="center" align="center">年
                <input property="editor" class="mini-textbox"/>
            </div>
            <div field="materialCode" width="100" headerAlign="center" align="center">物料编码
                <input property="editor" class="mini-textbox"/>
            </div>
            <div field="salesModel" width="130" headerAlign="center" align="center">销售型号
                <input property="editor" class="mini-textbox"/>
            </div>
            <div field="designModel" width="130" headerAlign="center" align="center">设计型号
                <input property="editor" class="mini-textbox"/>
            </div>
            <div field="output" width="80" headerAlign="center" align="center">产量
                <input property="editor" class="mini-spinner" minValue="0" maxValue="99999"/>
            </div>
            <div field="interpret" width="100" headerAlign="center" align="center">说明
                <input property="editor" class="mini-textbox"/>
            </div>
            <div field="completenessEvaluation" width="120" headerAlign="center" align="center">完备性评价
                <%--<input property="editor" class="mini-textbox"/>--%>
            </div>
            <div header="零件图册" headerAlign="center">
                <div property="columns">
                    <div field="partsAtlas" width="80" headerAlign="center" align="center" renderer="render">零件图册
                        <input property="editor" class="mini-combobox" style="width:98%"
                               valueField="key" textField="value"
                               data="[{'key': '√', 'value': '√'},{'key': '', 'value': ''}]"/>
                    </div>
                    <div field="maintenancePartsList" width="80" headerAlign="center" align="center" renderer="render">保养件清单
                        <input property="editor" class="mini-combobox" style="width:98%"
                               valueField="key" textField="value"
                               data="[{'key': '√', 'value': '√'},{'key': '', 'value': ''}]"/>
                    </div>
                    <div field="wearingPartsList" width="80" headerAlign="center" align="center" renderer="render">易损件清单
                        <input property="editor" class="mini-combobox" style="width:98%"
                               valueField="key" textField="value"
                               data="[{'key': '√', 'value': '√'},{'key': '', 'value': ''}]"/>
                    </div>
                </div>
            </div>
            <div header="操作保养手册" headerAlign="center">
                <div property="columns">
                    <div field="regularEdition" width="80" headerAlign="center" align="center" renderer="render">常规版
                        <input property="editor" class="mini-combobox" style="width:98%"
                               valueField="key" textField="value"
                               data="[{'key': '√', 'value': '√'},{'key': '', 'value': ''}]"/>
                    </div>
                    <div field="CEEdition" width="80" headerAlign="center" align="center" renderer="render">CE版
                        <input property="editor" class="mini-combobox" style="width:98%"
                               valueField="key" textField="value"
                               data="[{'key': '√', 'value': '√'},{'key': '', 'value': ''}]"/>
                    </div>
                </div>
            </div>
            <div field="packingList" width="80" headerAlign="center" align="center" renderer="render">装箱单
                <input property="editor" class="mini-combobox" style="width:98%"
                       valueField="key" textField="value"
                       data="[{'key': '√', 'value': '√'},{'key': '', 'value': ''}]"/>
            </div>
            <div header="装修手册" headerAlign="center">
                <div property="columns">
                    <div field="decorationManual" width="80" headerAlign="center" align="center" renderer="render">装修手册
                        <input property="editor" class="mini-combobox" style="width:98%"
                               valueField="key" textField="value"
                               data="[{'key': '√', 'value': '√'},{'key': '', 'value': ''}]"/>
                    </div>
                    <div field="disassAndAssManual" width="110" headerAlign="center" align="center" renderer="render">分解与组装手册
                        <input property="editor" class="mini-combobox" style="width:98%"
                               valueField="key" textField="value"
                               data="[{'key': '√', 'value': '√'},{'key': '', 'value': ''}]"/>
                    </div>
                    <div field="structurefunctionAndPrincipleManual" width="135" headerAlign="center" align="center" renderer="render">结构功能与原理手册
                        <input property="editor" class="mini-combobox" style="width:98%"
                               valueField="key" textField="value"
                               data="[{'key': '√', 'value': '√'},{'key': '', 'value': ''}]"/>
                    </div>
                    <div field="testAndAdjustmentManual" width="110" headerAlign="center" align="center" renderer="render">测试与调整手册
                        <input property="editor" class="mini-combobox" style="width:98%"
                               valueField="key" textField="value"
                               data="[{'key': '√', 'value': '√'},{'key': '', 'value': ''}]"/>
                    </div>
                    <div field="troubleshootingManual" width="100" headerAlign="center" align="center" renderer="render">故障诊断手册
                        <input property="editor" class="mini-combobox" style="width:98%"
                               valueField="key" textField="value"
                               data="[{'key': '√', 'value': '√'},{'key': '', 'value': ''}]"/>
                    </div>
                    <div field="torqueAndToolStandardValueTable" width="135" headerAlign="center" align="center" renderer="render">力矩及工具标准值表
                        <input property="editor" class="mini-combobox" style="width:98%"
                               valueField="key" textField="value"
                               data="[{'key': '√', 'value': '√'},{'key': '', 'value': ''}]"/>
                    </div>
                </div>
            </div>
            <div field="maintenanceStandardValueTable" width="100" headerAlign="center" align="center" renderer="render">检修标准值表
                <input property="editor" class="mini-combobox" style="width:98%"
                       valueField="key" textField="value"
                       data="[{'key': '测试版', 'value': '测试版'},{'key': '常规版', 'value': '常规版'},{'key': '', 'value': ''}]"/>
            </div>
            <div field="engineManual" width="80" headerAlign="center" align="center" renderer="render">发动机手册
                <input property="editor" class="mini-combobox" style="width:98%"
                       valueField="key" textField="value"
                       data="[{'key': '√', 'value': '√'},{'key': '', 'value': ''}]"/>
            </div>
            <div field="lifeCycleCostList" width="135" headerAlign="center" align="center" renderer="render">全生命周期成本清单
                <input property="editor" class="mini-combobox" style="width:98%"
                       valueField="key" textField="value"
                       data="[{'key': '√', 'value': '√'},{'key': '', 'value': ''}]"/>
            </div>
            <div field="airconditioningUseAndMaintenanceManual" width="160" headerAlign="center" align="center" renderer="render">空调使用与维护保养手册
                <input property="editor" class="mini-combobox" style="width:98%"
                       valueField="key" textField="value"
                       data="[{'key': '√', 'value': '√'},{'key': '', 'value': ''}]"/>
            </div>
            <div field="remark" width="300" headerAlign="center" align="center">备注
                <input property="editor" class="mini-textbox"/>
            </div>
        </div>
    </div>
</div>
<!--导出Excel相关HTML-->
<form id="excelForm" action="${ctxPath}/serviceEngineering/core/seGeneralKanbanCompleteness/exportList.do" method="post" target="excelIFrame">
    <input type="hidden" name="filter" id="filter"/>
    <input type="hidden" name="pageIndex" id="pageIndex"/>
    <input type="hidden" name="pageSize" id="pageSize"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>
<%--导入窗口--%>
<div id="importWindow" title="导入窗口" class="mini-window" style="width:750px;height:280px;"
     showModal="true" showFooter="false" allowResize="true">
    <p style="display: inline-block;color: red;font-size:15px;vertical-align: middle">
    </p>
    <div style="display: inline-block;float: right;">
        <a id="importBtn" class="mini-button" onclick="importBusiness()">导入</a>
        <a id="closeWindow" class="mini-button btn-red" onclick="closeImportWindow()">关闭</a>
    </div>
    <div class="mini-fit" style="font-size: 14px;margin-top: 30px">
        <form id="formImport" method="post">
            <table class="table-detail" cellspacing="1" cellpadding="0">
                <tr>
                    <td style="width: 30%">上传模板：</td>
                    <td style="width: 70%;">
                        <a href="#" style="color:blue;text-decoration:underline;" onclick="downImportTemplate()">完备性看板导入模板.xls</a>
                    </td>
                </tr>
                <tr>
                    <td style="width: 30%">操作：</td>
                    <td>
                        <input class="mini-textbox" style="width:60%;float: left" id="fileName" name="fileName"
                               readonly/>
                        <input id="inputFile" style="display:none;" type="file" onchange="getSelectFile()"
                               accept="application/xls"/>
                        <a id="uploadFileBtn" class="mini-button" style="float: left;margin-left: 10px"
                           onclick="uploadFile">选择文件</a>
                        <a id="clearFileBtn" class="mini-button" style="float: left;margin-left: 10px"
                           onclick="clearUploadFile">清除</a>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var seGeneralKanbanCompletenessListGrid = mini.get("seGeneralKanbanCompletenessListGrid");
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var importWindow = mini.get("importWindow");
    //..
    function beforeload() {
        var column = seGeneralKanbanCompletenessListGrid.getColumn(2);
        if (mini.get("signYear").getValue() == '' || mini.get("signYear").getValue() == null) {
            seGeneralKanbanCompletenessListGrid.hideColumn(column);
        } else {
            seGeneralKanbanCompletenessListGrid.showColumn(column);
        }
    }
    //..
    function addBusiness() {
        var newRow = {}
        newRow.id = "";
        newRow.signYear = "";
        newRow.materialCode = "";
        newRow.salesModel = "";
        newRow.designModel = "";
        newRow.output = "";
        newRow.interpret = "";
        newRow.completenessEvaluation = "";
        newRow.partsAtlas = "";
        newRow.maintenancePartsList = "";
        newRow.wearingPartsList = "";
        newRow.regularEdition = "";
        newRow.CEEdition = "";
        newRow.packingList = "";
        newRow.decorationManual = "";
        newRow.disassAndAssManual = "";
        newRow.structurefunctionAndPrincipleManual = "";
        newRow.testAndAdjustmentManual = "";
        newRow.troubleshootingManual = "";
        newRow.torqueAndToolStandardValueTable = "";
        newRow.maintenanceStandardValueTable = "";
        newRow.engineManual = "";
        newRow.lifeCycleCostList = "";
        newRow.airconditioningUseAndMaintenanceManual = "";
        newRow.remark = "";
        seGeneralKanbanCompletenessListGrid.addRow(newRow, 0);
    }
    //..
    function removeBusiness(record) {
        var rows = [];
        if (record) {
            rows.push(record);
        } else {
            rows = seGeneralKanbanCompletenessListGrid.getSelecteds();
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
                        seGeneralKanbanCompletenessListGrid.removeRow(r, false);
                    } else {
                        rowIds.push(r.id);
                    }
                }
                if (rowIds.length > 0) {
                    _SubmitJson({
                        url: jsUseCtxPath + "/serviceEngineering/core/seGeneralKanbanCompleteness/deleteData.do",
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
    function saveBusiness() {
        if (seGeneralKanbanCompletenessListGrid.getColumn(2).visible == false) {
            mini.alert("必须在有年份的状态下新增!");
            return;
        }
        seGeneralKanbanCompletenessListGrid.validate();
        if (!seGeneralKanbanCompletenessListGrid.isValid()) {
            var error = seGeneralKanbanCompletenessListGrid.getCellErrors()[0];
            seGeneralKanbanCompletenessListGrid.beginEditCell(error.record, error.column);
            mini.alert(error.column.header + error.errorText);
            return;
        }

        var postData = seGeneralKanbanCompletenessListGrid.getChanges();
        debugger;
        if (postData.length > 0) {
            var postDataJson = mini.encode(postData);
            $.ajax({
                url: jsUseCtxPath + "/serviceEngineering/core/seGeneralKanbanCompleteness/saveBusiness.do",
                type: 'POST',
                contentType: 'application/json',
                data: postDataJson,
                success: function (returnData) {
                    if (returnData && returnData.message) {
                        mini.alert(returnData.message, '提示', function () {
                            if (returnData.success) {
                                seGeneralKanbanCompletenessListGrid.reload();
                            }
                        });
                    }
                }
            });
        }
    }
    //..
    function onCellValidation(e) {
        if (e.field == 'materialCode' && (!e.value || e.value == '')) {
            e.isValid = false;
            e.errorText = '不能为空！';
        }
        if (e.field == 'signYear' && (!e.value || e.value == '')) {
            e.isValid = false;
            e.errorText = '不能为空！';
        }
    }
    //..
    function clearFormThis() {
        mini.get("signYear").setValue("");
        mini.get("salesModel").setValue("");
        mini.get("designModel").setValue("");
        mini.get("materialCode").setValue("");
        mini.get("completenessEvaluation").setValue("");
        mini.get("maintenanceStandardValueTable").setValue("");
        mini.get("others").setValue("");
        seGeneralKanbanCompletenessListGrid.load();
    }
    //..
    function exportBusiness() {
        var parent = $(".search-form");
        var inputAry = $("input", parent);
        var params = [];
        inputAry.each(function (i) {
            var el = $(this);
            var obj = {};
            obj.name = el.attr("name");
            if (!obj.name) return true;
            obj.value = el.val();
            params.push(obj);
        });
        $("#filter").val(mini.encode(params));
        $("#pageIndex").val(seGeneralKanbanCompletenessListGrid.getPageIndex());
        $("#pageSize").val(seGeneralKanbanCompletenessListGrid.getPageSize());
        var excelForm = $("#excelForm");
        excelForm.submit();
    }
    //..
    function importBusiness() {
        var file = null;
        var fileList = $("#inputFile")[0].files;
        if (fileList && fileList.length > 0) {
            file = fileList[0];
        }
        if (!file) {
            mini.alert('请选择文件！');
            return;
        }
        //XMLHttpRequest方式上传表单
        var xhr = false;
        try {
            //尝试创建 XMLHttpRequest 对象，除 IE 外的浏览器都支持这个方法。
            xhr = new XMLHttpRequest();
        } catch (e) {
            //使用较新版本的 IE 创建 IE 兼容的对象（Msxml2.XMLHTTP）。
            xhr = ActiveXobject("Msxml12.XMLHTTP");
        }

        if (xhr.upload) {
            xhr.onreadystatechange = function (e) {
                if (xhr.readyState == 4) {
                    if (xhr.status == 200) {
                        if (xhr.responseText) {
                            var returnObj = JSON.parse(xhr.responseText);
                            var message = '';
                            if (returnObj.message) {
                                message = returnObj.message;
                            }
                            mini.alert(message);
                        }
                    }
                }
            };
            //开始上传
            xhr.open('POST', jsUseCtxPath + '/serviceEngineering/core/seGeneralKanbanCompleteness/importExcel.do', false);
            xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
            var fd = new FormData();
            fd.append('importFile', file);
            xhr.send(fd);
        }
    }
    //..
    function openImportWindow() {
        importWindow.show();
    }
    //..
    function closeImportWindow() {
        importWindow.hide();
        clearUploadFile();
        searchFrm();
    }
    //..
    function downImportTemplate() {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + "/serviceEngineering/core/seGeneralKanbanCompleteness/importTemplateDownload.do");
        $("body").append(form);
        form.submit();
        form.remove();
    }
    //..
    function getSelectFile() {
        var fileList = $("#inputFile")[0].files;
        if (fileList && fileList.length > 0) {
            var fileNameSuffix = fileList[0].name.split('.').pop();
            if (fileNameSuffix == 'xls' || fileNameSuffix == 'xlsx') {
                mini.get("fileName").setValue(fileList[0].name);
            }
            else {
                clearUploadFile();
                mini.alert('请上传excel文件！');
            }
        }
    }
    //..
    function uploadFile() {
        $("#inputFile").click();
    }
    //..
    function clearUploadFile() {
        $("#inputFile").val('');
        mini.get("fileName").setValue('');
    }
    //..
    function render(e) {
        if (e.value != null && e.value != '') {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 30px;background-color: #0bb20c" >' + e.value + '</span>';
        }
        return html;
    }
</script>
<redxun:gridScript gridId="seGeneralKanbanCompletenessListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>