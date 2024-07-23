<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title></title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<div class="mini-toolbar">
    <div style="height: 50px;line-height:50px">
        <span class="text" style="font-size:30px;font: bold">&nbsp;${salesModel}&nbsp;&nbsp;&nbsp;物料明细总表</span>
    </div>
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">预留生产订单WBS：</span>
                    <input class="mini-textbox" id="wbs" name="wbs"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">物料号：</span>
                    <input class="mini-textbox" id="materialCode" name="materialCode"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">短描述：</span>
                    <input class="mini-textbox" id="shortDescription" name="shortDescription"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">物料描述：</span>
                    <input class="mini-textbox" id="materialDescription" name="materialDescription"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">采购组：</span>
                    <input class="mini-textbox" id="procurementGroup" name="procurementGroup"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">供应商：</span>
                    <input class="mini-textbox" id="supplier" name="supplier"/>
                </li>
                <li style="margin-right: 15px">
                    <input id="others" name="others" class="mini-checkboxlist" style="width:auto;"
                           repeatItems="10" repeatLayout="flow" repeatDirection="horizontal"
                           textfield="fieldName" valuefield="field" multiSelect="false"
                           data="[{'fieldName' : '全部入库','field' : 'all'},
                           {'fieldName' : '未入库','field' : 'nothing'},
                           {'fieldName' : '部分入库','field' : 'parts'}]"/>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearFormThis()">清空查询</a>
                    <a id="addBusiness" class="mini-button" style="margin-right: 5px" plain="true" onclick="addBusiness()">新增</a>
                    <a id="copyBusiness" class="mini-button" style="margin-right: 5px" plain="true" onclick="copyBusiness()">复制</a>
                    <a id="saveBusiness" class="mini-button" style="margin-right: 5px" plain="true" onclick="saveBusiness()">保存</a>
                    <a id="removeBusiness" class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="removeBusiness()">删除</a>
                    <a id="openImportWindow" class="mini-button" style="margin-right: 5px" plain="true" onclick="openImportWindow()">导入主表</a>
                    <a id="openImportSubWindow" class="mini-button" style="margin-right: 5px" plain="true" onclick="openImportSubWindow()">导入分表</a>
                    <a id="exportBusiness" class="mini-button" style="margin-right: 5px" plain="true" onclick="exportBusiness()">导出</a>
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
         showCellTip="true" idField="id" allowCellEdit="true" allowCellSelect="true" oncellbeginedit="cellBeginEdit"
         multiSelect="true" showColumnsMenu="false" sizeList="[5000,10000]" pageSize="5000" allowAlternating="true" pagerButtons="#pagerButtons"
         url="${ctxPath}/newproductAssembly/core/material/materialListQuery.do?mainId=${mainId}" oncellvalidation="onCellValidation">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div type="indexcolumn" width="35" headerAlign="center" align="center">序号</div>
            <div field="wbs" width="180" headerAlign="center" align="center" allowSort="true">预留生产订单WBS
                <input property="editor" class="mini-textbox" style="width:100%;"/>
            </div>
            <div field="materialCode" width="80" headerAlign="center" align="center" allowSort="true">物料号
                <input property="editor" class="mini-textbox" style="width:100%;"/>
            </div>
            <div field="materialDescription" width="400" headerAlign="center" align="center" allowSort="true">物料描述
                <input property="editor" class="mini-textbox" style="width:100%;"/>
            </div>
            <div field="deliveryQuantity" width="80" headerAlign="center" align="center">配送数量
                <input property="editor" class="mini-spinner" style="width:100%;" maxValue="99999"/>
            </div>
            <div field="receivedQuantity" width="80" headerAlign="center" align="center">实收数量
                <input property="editor" class="mini-spinner" style="width:100%;" maxValue="99999"/>
            </div>
            <div field="exceptionQuantity" width="80" headerAlign="center" align="center" renderer="renderException">异常数量
            </div>
            <div field="shortDescription" width="80" headerAlign="center" align="center" allowSort="true">短描述
                <input property="editor" class="mini-textbox" style="width:100%;"/>
            </div>
            <div field="lastTime" width="100" headerAlign="center" align="center" allowSort="true">更新日期</div>
            <div field="procurementGroup" width="80" headerAlign="center" align="center" allowSort="true">采购组
                <input property="editor" class="mini-textbox" style="width:100%;"/>
            </div>
            <div field="supplier" width="80" headerAlign="center" align="center" allowSort="true">供应商
                <input property="editor" class="mini-textbox" style="width:100%;"/>
            </div>
            <div field="remark" width="200" headerAlign="center" align="center" allowSort="true">备注
                <input property="editor" class="mini-textbox" style="width:100%;"/>
            </div>
        </div>
    </div>
</div>
<%--导入窗口--%>
<div id="importWindow" title="导入窗口" class="mini-window" style="width:750px;height:280px;" showModal="true" showFooter="false" allowResize="true">
    <p style="display: inline-block;color: red;font-size:15px;vertical-align: middle">
    </p>
    <div style="display: inline-block;float: right;">
        <a id="importBtn" class="mini-button" onclick="importMaterial()">导入</a>
        <a id="closeWindow" class="mini-button btn-red" onclick="closeImportWindow()">关闭</a>
    </div>
    <div class="mini-fit" style="font-size: 14px;margin-top: 30px">
        <form id="formImport" method="post">
            <table class="table-detail" cellspacing="1" cellpadding="0">
                <tr>
                    <td style="width: 30%">上传模板：</td>
                    <td style="width: 70%;">
                        <a href="#" style="color:blue;text-decoration:underline;" onclick="downImportTemplate()">模板下载.xlsx</a>
                    </td>
                </tr>
                <tr>
                    <td style="width: 30%">操作：</td>
                    <td>
                        <input class="mini-textbox" style="width:60%;float: left" id="fileName" name="fileName" readonly/>
                        <input id="inputFile" style="display:none;" type="file" onchange="getSelectFile()" accept="application/xls"/>
                        <a id="uploadFileBtn" class="mini-button" style="float: left;margin-left: 10px" onclick="uploadFile()">选择文件</a>
                        <a id="clearFileBtn" class="mini-button" style="float: left;margin-left: 10px" onclick="clearUploadFile()">清除</a>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
<!--导出Excel相关HTML-->
<form id="excelForm" action="${ctxPath}/newproductAssembly/core/material/exportExcel.do?mainId=${mainId}&salesModel=${salesModel}" method="post"
      target="excelIFrame">
    <input type="hidden" name="filter" id="filter"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var businessListGrid = mini.get("businessListGrid");
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var newproductAssemblyAdmins = "${newproductAssemblyAdmins}";
    var newproductAssemblyMaterialReceivers = "${newproductAssemblyMaterialReceivers}";
    var mainId = "${mainId}";
    var salesModel = "${salesModel}";
    var importWindow = mini.get("importWindow");
    var thatType = "";
    //..
    $(function () {
        if (currentUserNo != 'admin' && newproductAssemblyAdmins.search(currentUserNo) == -1) {
            if (newproductAssemblyMaterialReceivers.search(currentUserNo) != -1) {
                mini.get("addBusiness").setEnabled(false);
                mini.get("copyBusiness").setEnabled(false);
                mini.get("removeBusiness").setEnabled(false);
                mini.get("openImportWindow").setEnabled(false);
            } else {
                mini.get("addBusiness").setEnabled(false);
                mini.get("copyBusiness").setEnabled(false);
                mini.get("saveBusiness").setEnabled(false);
                mini.get("removeBusiness").setEnabled(false);
                mini.get("openImportWindow").setEnabled(false);
                mini.get("openImportSubWindow").setEnabled(false);
                mini.get("exportBusiness").setEnabled(false);
            }
        }
    });
    //..
    function cellBeginEdit(sender) {
        if (currentUserNo != 'admin' && newproductAssemblyAdmins.search(currentUserNo) == -1) {
            sender.editor.setEnabled(false);
            if (newproductAssemblyMaterialReceivers.search(currentUserNo) != -1 &&
                (sender.field == "receivedQuantity" || sender.field == "remark")) {
                sender.editor.setEnabled(true);
            }
        }
    }
    //..
    function addBusiness() {
        var newRow = {}
        newRow.deliveryQuantity = '0';
        newRow.receivedQuantity = '0';
        newRow.exceptionQuantity = '0';
        businessListGrid.addRow(newRow, 0);
    }
    //..
    function copyBusiness(record) {
        var row;
        if (record) {
            row = record;
        } else {
            row = businessListGrid.getSelected();
        }
        if (row == null) {
            mini.alert("请选中一条记录");
            return;
        }
        var newRow = {}
        newRow.wbs = row.wbs
        newRow.materialCode = row.materialCode
        newRow.materialDescription = row.materialDescription
        newRow.deliveryQuantity = row.deliveryQuantity
        newRow.receivedQuantity = row.receivedQuantity
        newRow.exceptionQuantity = row.exceptionQuantity
        newRow.shortDescription = row.shortDescription
        newRow.procurementGroup = row.procurementGroup
        newRow.supplier = row.supplier
        newRow.remark = row.remark
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
            url: jsUseCtxPath + "/newproductAssembly/core/material/saveMaterial.do?mainId=" + mainId,
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
                        url: jsUseCtxPath + "/newproductAssembly/core/material/deleteMaterial.do",
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
    function onCellValidation(e) {
        if (e.field == 'deliveryQuantity' && (!e.value || e.value == '')) {
            e.isValid = false;
            e.errorText = '不能为空！';
        }
        if (e.field == 'shortDescription' && (!e.value || e.value == '')) {
            e.isValid = false;
            e.errorText = '不能为空！';
        }
    }
    //..
    function openImportWindow() {
//        if (businessListGrid.data.length > 0) {
//            mini.alert("主表只允许导入一次，再次导入请先清空主表信息！");
//            return;
//        }
        whatType = "main";
        importWindow.show();
    }
    //..
    function openImportSubWindow() {
        whatType = "sub";
        importWindow.show();
    }
    //..
    function importMaterial() {
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
            if (whatType == 'main') {
                xhr.open('POST', jsUseCtxPath + '/newproductAssembly/core/material/importMaterial.do?mainId=' + mainId, false);
            } else if (whatType == 'sub') {
                xhr.open('POST', jsUseCtxPath + '/newproductAssembly/core/material/importSubMaterial.do?mainId=' + mainId, false);
            }

            xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
            var fd = new FormData();
            fd.append('importFile', file);
            xhr.send(fd);
        }
    }
    //..
    function closeImportWindow() {
        importWindow.hide();
        clearUploadFile();
        businessListGrid.load();
    }
    //..
    function clearUploadFile() {
        $("#inputFile").val('');
        mini.get("fileName").setValue('');
    }
    //..
    function downImportTemplate() {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        //开始上传
        if (whatType == 'main') {
            form.attr("action", jsUseCtxPath + "/newproductAssembly/core/material/importMaterialTDownload.do");
        } else if (whatType == 'sub') {
            form.attr("action", jsUseCtxPath + "/newproductAssembly/core/material/importSubMaterialTDownload.do");
        }
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
            } else {
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
    function renderException(e) {
        if (e.record.exceptionQuantity == 0) {//全收到
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 35px" >' + e.value + '</span>';
        } else if (e.record.receivedQuantity == 0) {//没收到
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 35px;background-color: red" >' + e.value + '</span>';
        } else {//收到一部分
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 35px;background-color: orange" >' + e.value + '</span>';
        }
        return html;
    }
    //..
    function clearFormThis() {
        mini.get("wbs").setValue("");
        mini.get("materialCode").setValue("");
        mini.get("shortDescription").setValue("");
        mini.get("materialDescription").setValue("");
        mini.get("others").setValue("");
        businessListGrid.load();
    }
    //..导出
    function exportBusiness() {
        var rows = businessListGrid.getSelecteds();
        if (rows.length <= 0) {
            mini.alert("请至少选中一条记录");
            return;
        }
        var params = [];
        for (var i = 0; i < rows.length; i++) {
            var obj = {};
            obj.name = "id";
            obj.value = rows[i].id
            params.push(obj);
        }
        $("#filter").val(mini.encode(params));
        var excelForm = $("#excelForm");
        excelForm.submit();
    }
</script>
<redxun:gridScript gridId="businessListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>