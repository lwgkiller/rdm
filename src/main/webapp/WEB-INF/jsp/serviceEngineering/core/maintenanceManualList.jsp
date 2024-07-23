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
                    <span class="text" style="width:auto"><spring:message code="page.maintenanceManualList.name" />：</span>
                    <input class="mini-textbox" id="materialCode" name="materialCode"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.maintenanceManualList.name1" />：</span>
                    <input class="mini-textbox" id="salesModel" name="salesModel"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.maintenanceManualList.name2" />：</span>
                    <input class="mini-textbox" id="designModel" name="designModel"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.maintenanceManualList.name3" />：</span>
                    <input class="mini-textbox" id="atlasStatus" name="atlasStatus"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.maintenanceManualList.name4" />：</span>
                    <input class="mini-textbox" id="storageQuantity" name="storageQuantity"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.maintenanceManualList.name5" />：</span>
                    <input class="mini-textbox" id="shipmentQuantity" name="shipmentQuantity"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.maintenanceManualList.name6" />：</span>
                    <input class="mini-textbox" id="manualCode" name="manualCode"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.maintenanceManualList.name7" />：</span>
                    <input class="mini-textbox" id="manualDescription" name="manualDescription"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.maintenanceManualList.name8" />：</span>
                    <input class="mini-textbox" id="manualLanguage" name="manualLanguage"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.maintenanceManualList.name9" />：</span>
                    <input class="mini-textbox" id="manualStatus" name="manualStatus"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.maintenanceManualList.name10" />：</span>
                    <input class="mini-textbox" id="department" name="department"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.maintenanceManualList.name11" />：</span>
                    <input class="mini-textbox" id="productSupervisor" name="productSupervisor"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.maintenanceManualList.name12" />：</span>
                    <input class="mini-textbox" id="keyUser" name="keyUser"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.maintenanceManualList.name13" />：</span>
                    <input class="mini-textbox" id="percentComplete" name="percentComplete"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.maintenanceManualList.name14" />：</span>
                    <input class="mini-textbox" id="isPrint" name="isPrint"/>
                </li>
                <li style="margin-left: 10px">
                    <f:a alias="maintenanceManual-1-searchFrm" onclick="searchFrm()" showNoRight="false"><spring:message code="page.maintenanceManualList.name15" /></f:a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()"><spring:message code="page.maintenanceManualList.name16" /></a>
                    <f:a alias="maintenanceManual-1-openImportWindow" onclick="openImportWindow()" showNoRight="false"><spring:message code="page.maintenanceManualList.name17" /></f:a>
                    <f:a alias="maintenanceManual-1-addBusiness" onclick="addBusiness()" showNoRight="false"><spring:message code="page.maintenanceManualList.name18" /></f:a>
                    <f:a alias="maintenanceManual-1-copyBusiness" onclick="copyBusiness()" showNoRight="false"><spring:message code="page.maintenanceManualList.name19" /></f:a>
                    <f:a alias="maintenanceManual-1-saveBusiness" onclick="saveBusiness()" showNoRight="false"><spring:message code="page.maintenanceManualList.name20" /></f:a>
                    <f:a alias="maintenanceManual-1-removeBusiness" onclick="removeBusiness()" showNoRight="false"><spring:message code="page.maintenanceManualList.name21" /></f:a>
                </li>
            </ul>
        </form>
        <span class="searchSelectionBtn" onclick="no_search(this ,'searchForm')">
				<i class="icon-sc-lower"></i>
        </span>
    </div>
</div>

<div class="mini-fit" style="height: 100%;">
    <div id="maintenanceManualListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" allowCellWrap="false"
         showCellTip="true" idField="id" allowCellEdit="true" allowCellSelect="true" oncellvalidation="onCellValidation"
         multiSelect="true" showColumnsMenu="false" sizeList="[1000,5000]" pageSize="1000" allowAlternating="true" pagerButtons="#pagerButtons"
         url="${ctxPath}/serviceEngineering/core/maintenanceManual/dataListQuery.do">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div type="indexcolumn" width="35" headerAlign="center" align="center"><spring:message code="page.maintenanceManualList.name22" /></div>
            <div field="el" visible="false"></div>
            <div field="materialCode" width="100" headerAlign="center" align="center" renderer="render"><spring:message code="page.maintenanceManualList.name" />
                <input property="editor" class="mini-textbox"/>
            </div>
            <div field="salesModel" width="120" headerAlign="center" align="center" renderer="render"><spring:message code="page.maintenanceManualList.name1" />
                <input property="editor" class="mini-textbox"/>
            </div>
            <div field="designModel" width="150" headerAlign="center" align="center" renderer="render"><spring:message code="page.maintenanceManualList.name2" />
                <input property="editor" class="mini-textbox"/>
            </div>
            <div field="atlasStatus" width="80" headerAlign="center" align="center" renderer="render"><spring:message code="page.maintenanceManualList.name3" />
                <input property="editor" class="mini-combobox" style="width:98%"
                       url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=serviceEngineeringMaintenanceManualAtlasStatus"
                       valueField="key" textField="key"/>
            </div>
            <div field="storageQuantity" width="80" headerAlign="center" align="center" renderer="render"><spring:message code="page.maintenanceManualList.name4" />
                <input property="editor" class="mini-spinner" style="width:100%;" maxValue="9999999"/>
            </div>
            <div field="shipmentQuantity" width="80" headerAlign="center" align="center" renderer="render"><spring:message code="page.maintenanceManualList.name5" />
                <input property="editor" class="mini-spinner" style="width:100%;" maxValue="9999999"/>
            </div>
            <div field="manualCode" width="120" headerAlign="center" align="center" renderer="render"><spring:message code="page.maintenanceManualList.name6" />
                <input property="editor" class="mini-textbox"/>
            </div>
            <div field="manualDescription" width="300" headerAlign="center" align="center" renderer="render"><spring:message code="page.maintenanceManualList.name7" />
                <input property="editor" class="mini-textbox"/>
            </div>
            <div field="manualLanguage" width="80" headerAlign="center" align="center" renderer="render"><spring:message code="page.maintenanceManualList.name8" />
                <input property="editor" class="mini-combobox" style="width:98%"
                       url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=serviceEngineeringLanguage"
                       valueField="key" textField="value"/>
            </div>
            <div field="manualStatus" width="80" headerAlign="center" align="center" renderer="render"><spring:message code="page.maintenanceManualList.name9" />
                <input property="editor" class="mini-combobox" style="width:98%"
                       url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=serviceEngineeringMaintenanceManualStatus"
                       valueField="key" textField="key"/>
            </div>
            <div field="departmentId" displayField="department" width="200" headerAlign="center" align="center" renderer="render"><spring:message code="page.maintenanceManualList.name10" />
                <input property="editor"
                       class="mini-buttonedit icon-dep-button"
                       required="true" allowInput="false"
                       onbuttonclick="selectMainDep" style="width:98%"/>
            </div>
            <div field="productSupervisorId" displayField="productSupervisor" width="80" headerAlign="center" align="center" renderer="render"><spring:message code="page.maintenanceManualList.name11" />
                <input property="editor" class="mini-user rxc"
                       plugins="mini-user" style="width:98%;height:34px;" allowinput="false" length="80"
                       mainfield="no" single="true"/>
            </div>
            <div field="keyUserId" displayField="keyUser" width="80" headerAlign="center" align="center" renderer="render"><spring:message code="page.maintenanceManualList.name12" />
                <input property="editor" class="mini-user rxc"
                       plugins="mini-user" style="width:98%;height:34px;" allowinput="false" length="80"
                       mainfield="no" single="true"/>
            </div>
            <div field="percentComplete" width="80" headerAlign="center" align="center" renderer="render"><spring:message code="page.maintenanceManualList.name13" />
                <input property="editor" class="mini-spinner" style="width:100%;" maxValue="9999999" decimalPlaces="2"/>
            </div>
            <div field="isPrint" width="100" headerAlign="center" align="center" renderer="render"><spring:message code="page.maintenanceManualList.name14" />
                <input property="editor" class="mini-textbox"/>
            </div>
            <div field="isCE" width="100" headerAlign="center" align="center" renderer="render"><spring:message code="page.maintenanceManualList.name23" />
                <input property="editor" class="mini-textbox"/>
            </div>
            <div field="estimatedPrintTime" width="120" headerAlign="center" align="center" dateFormat="yyyy-MM-dd" renderer="render"><spring:message code="page.maintenanceManualList.name24" />
                <input property="editor" class="mini-datepicker" format="yyyy-MM-dd"
                       showTime="false" showOkButton="true" showClearButton="false"/>
            </div>
            <div field="remark" width="300" headerAlign="center" align="center" renderer="render"><spring:message code="page.maintenanceManualList.name25" />
                <input property="editor" class="mini-textbox"/>
            </div>
        </div>
    </div>
</div>

<div id="importWindow" title="<spring:message code="page.maintenanceManualList.name26" />" class="mini-window" style="width:750px;height:280px;"
     showModal="true" showFooter="false" allowResize="true">
    <p style="display: inline-block;color: red;font-size:15px;vertical-align: middle">
    </p>
    <div style="display: inline-block;float: right;">
        <a id="importBtn" class="mini-button" onclick="importBusiness()"><spring:message code="page.maintenanceManualList.name17" /></a>
        <a id="closeWindow" class="mini-button btn-red" onclick="closeImportWindow()"><spring:message code="page.maintenanceManualList.name27" /></a>
    </div>
    <div class="mini-fit" style="font-size: 14px;margin-top: 30px">
        <form id="formImport" method="post">
            <table class="table-detail" cellspacing="1" cellpadding="0">
                <tr>
                    <td style="width: 30%"><spring:message code="page.maintenanceManualList.name28" />：</td>
                    <td style="width: 70%;">
                        <a href="#" style="color:blue;text-decoration:underline;" onclick="downImportTemplate()"><spring:message code="page.maintenanceManualList.name29" />.xls</a>
                    </td>
                </tr>
                <tr>
                    <td style="width: 30%"><spring:message code="page.maintenanceManualList.name30" />：</td>
                    <td>
                        <input class="mini-textbox" style="width:60%;float: left" id="fileName" name="fileName"
                               readonly/>
                        <input id="inputFile" style="display:none;" type="file" onchange="getSelectFile()"
                               accept="application/xls"/>
                        <a id="uploadFileBtn" class="mini-button" style="float: left;margin-left: 10px"
                           onclick="uploadFile"><spring:message code="page.maintenanceManualList.name31" /></a>
                        <a id="clearFileBtn" class="mini-button" style="float: left;margin-left: 10px"
                           onclick="clearUploadFile"><spring:message code="page.maintenanceManualList.name32" /></a>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var maintenanceManualListGrid = mini.get("maintenanceManualListGrid");
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var importWindow = mini.get("importWindow");
    //所有非特殊文本都变成这个格式
    function render(e) {
        var record = e.record;
        var el = record.el;
        if (el == "红") {
            var s = '<div style="color:red">' + e.cellHtml + '</div>'
        } else if (el == '橙') {
            var s = '<div style="color:orange">' + e.cellHtml + '</div>'
        } else if (el == '蓝') {
            var s = '<div style="color:blue">' + e.cellHtml + '</div>'
        } else {
            s = e.cellHtml;
        }
        return s;
    }
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
            rows = maintenanceManualListGrid.getSelecteds();
        }
        if (rows.length <= 0) {
            mini.alert(maintenanceManualList_name);
            return;
        }
        mini.confirm(maintenanceManualList_name1, maintenanceManualList_name2, function (action) {
            if (action != 'ok') {
                return;
            } else {
                var rowIds = [];
                for (var i = 0, l = rows.length; i < l; i++) {
                    var r = rows[i];
                    if (r.id == "") {
                        maintenanceManualListGrid.removeRow(r, false);
                    } else {
                        rowIds.push(r.id);
                    }
                }
                if (rowIds.length > 0) {
                    _SubmitJson({
                        url: jsUseCtxPath + "/serviceEngineering/core/maintenanceManual/deleteData.do",
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
    function clearUploadFile() {
        $("#inputFile").val('');
        mini.get("fileName").setValue('');
    }
    //..
    function importBusiness() {
        var file = null;
        var fileList = $("#inputFile")[0].files;
        if (fileList && fileList.length > 0) {
            file = fileList[0];
        }
        if (!file) {
            mini.alert(maintenanceManualList_name3);
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

            // 开始上传
            xhr.open('POST', jsUseCtxPath + '/serviceEngineering/core/maintenanceManual/importExcel.do', false);
            xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
            var fd = new FormData();
            fd.append('importFile', file);
            xhr.send(fd);
        }
    }
    //..
    function downImportTemplate() {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + "/serviceEngineering/core/maintenanceManual/importTemplateDownload.do");
        $("body").append(form);
        form.submit();
        form.remove();
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
    function getSelectFile() {
        var fileList = $("#inputFile")[0].files;
        if (fileList && fileList.length > 0) {
            var fileNameSuffix = fileList[0].name.split('.').pop();
            if (fileNameSuffix == 'xls' || fileNameSuffix == 'xlsx') {
                mini.get("fileName").setValue(fileList[0].name);
            }
            else {
                clearUploadFile();
                mini.alert(maintenanceManualList_name4);
            }
        }
    }
    //..
    function addBusiness() {
        var newRow = {}
        newRow.id = "";
        newRow.materialCode = "";
        newRow.salesModel = "";
        newRow.designModel = "";
        newRow.atlasStatus = "";
        newRow.storageQuantity = "0";
        newRow.shipmentQuantity = "0";
        newRow.manualCode = "";
        newRow.manualDescription = "";
        newRow.manualLanguage = "";
        newRow.manualStatus = "";
        newRow.departmentId = "";
        newRow.productSupervisorId = "";
        newRow.keyUserId = "";
        newRow.percentComplete = "0";
        newRow.isPrint = "";
        newRow.isCE = "";
        newRow.estimatedPrintTime = "";
        newRow.remark = "";
        maintenanceManualListGrid.addRow(newRow, 0);
    }
    //..
    function copyBusiness(record) {
        var row = maintenanceManualListGrid.getSelected();
        if (!row) {
            mini.alert(maintenanceManualList_name5);
            return;
        }
        var newRow = {}
        newRow.id = "";
        newRow.materialCode = row.materialCode;
        newRow.salesModel = row.salesModel;
        newRow.designModel =row.designModel;
        newRow.atlasStatus =row.atlasStatus;
        newRow.manualCode = row.manualCode;
        newRow.manualDescription = row.manualDescription;
        newRow.manualLanguage = row.manualLanguage;
        newRow.manualStatus = row.manualStatus;
        newRow.departmentId = row.departmentId;
        newRow.department = row.department;
        newRow.productSupervisorId = row.productSupervisorId;
        newRow.productSupervisor = row.productSupervisor;
        newRow.keyUserId = row.keyUserId;
        newRow.keyUser = row.keyUser;
        newRow.percentComplete = row.percentComplete;
        newRow.isPrint = row.isPrint;
        newRow.isCE = row.isCE;
        newRow.estimatedPrintTime = row.estimatedPrintTime;
        newRow.remark = row.remark;
        debugger;
        maintenanceManualListGrid.addRow(newRow, 0);
    }
    //..
    function saveBusiness() {
        maintenanceManualListGrid.validate();
        if (!maintenanceManualListGrid.isValid()) {
            var error = maintenanceManualListGrid.getCellErrors()[0];
            maintenanceManualListGrid.beginEditCell(error.record, error.column);
            mini.alert(error.column.header + error.errorText);
            return;
        }
        var postData = maintenanceManualListGrid.getChanges();
        var postDataJson = mini.encode(postData);
        $.ajax({
            url: jsUseCtxPath + "/serviceEngineering/core/maintenanceManual/saveBusiness.do",
            type: 'POST',
            contentType: 'application/json',
            data: postDataJson,
            success: function (returnData) {
                if (returnData && returnData.message) {
                    mini.alert(returnData.message, maintenanceManualList_name2, function () {
                        if (returnData.success) {
                            maintenanceManualListGrid.reload();
                        }
                    });
                }
            }
        });
    }
    //..
    function onCellValidation(e) {
        if (e.field == 'materialCode' && (!e.value || e.value == '')) {
            e.isValid = false;
            e.errorText = maintenanceManualList_name7;
        }
        if (e.field == 'salesModel' && (!e.value || e.value == '')) {
            e.isValid = false;
            e.errorText = maintenanceManualList_name7;
        }
        if (e.field == 'designModel' && (!e.value || e.value == '')) {
            e.isValid = false;
            e.errorText = maintenanceManualList_name7;
        }
        if (e.field == 'atlasStatus' && (!e.value || e.value == '')) {
            e.isValid = false;
            e.errorText = maintenanceManualList_name7;
        }
        if (e.field == 'manualStatus' && (!e.value || e.value == '')) {
            e.isValid = false;
            e.errorText = maintenanceManualList_name7;
        }
        if (e.field == 'departmentId' && (!e.value || e.value == '')) {
            e.isValid = false;
            e.errorText = maintenanceManualList_name7;
        }
        if (e.field == 'productSupervisorId' && (!e.value || e.value == '')) {
            e.isValid = false;
            e.errorText = maintenanceManualList_name7;
        }
        if (e.field == 'isPrint' && (!e.value || e.value == '')) {
            e.isValid = false;
            e.errorText = maintenanceManualList_name7;
        }
        if (e.field == 'estimatedPrintTime' && (!e.value || e.value == '')) {
            e.isValid = false;
            e.errorText = maintenanceManualList_name7;
        }
    }
</script>
<redxun:gridScript gridId="maintenanceManualListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>