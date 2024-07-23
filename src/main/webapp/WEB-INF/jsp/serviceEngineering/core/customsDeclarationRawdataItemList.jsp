<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title></title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<%--工具栏--%>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right:15px">
                    <span class="text" style="width:auto">适用机型：</span>
                    <input class="mini-textbox" id="machineModel" name="machineModel" style="width:100%;"/>
                </li>
                <li style="margin-right:15px">
                    <span class="text" style="width:auto">物料：</span>
                    <input class="mini-textbox" id="materialCode" name="materialCode" style="width:100%;"/>
                </li>
                <li style="margin-right:15px">
                    <span class="text" style="width:auto">物料描述：</span>
                    <input class="mini-textbox" id="materialDescription" name="materialDescription" style="width:100%;"/>
                </li>
                <li style="margin-right:15px">
                    <span class="text" style="width:auto">HS编码：</span>
                    <input class="mini-textbox" id="hsCode" name="hsCode" style="width:100%;"/>
                </li>
                <li style="margin-right:15px">
                    <span class="text" style="width:auto">报关要素：</span>
                    <input class="mini-textbox" id="elements" name="elements" style="width:100%;"/>
                </li>
                <li style="margin-right:15px">
                    <span class="text" style="width:auto">报关要素填写：</span>
                    <input class="mini-textbox" id="elementsFill" name="elementsFill" style="width:100%;"/>
                </li>
                <li style="margin-right:15px">
                    <span class="text" style="width:auto">补充信息：</span>
                    <input class="mini-textbox" id="additionalInfo" name="additionalInfo" style="width:100%;"/>
                </li>
                <li style="margin-right:15px">
                    <span class="text" style="width:auto">批次号：</span>
                    <input class="mini-textbox" id="batchNo" name="batchNo" style="width:100%;"/>
                </li>
                <li style="margin-right:15px">
                    <span class="text" style="width:auto">单据号：</span>
                    <input class="mini-textbox" id="businessNo" name="businessNo" style="width:100%;"/>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="exportBusiness()">导出</a>
                    <a id="openImportWindow" class="mini-button" style="margin-right: 5px" plain="true" onclick="openImportWindow">导入</a>
                    <a id="deleteBusiness" class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="deleteBusiness()">删除</a>
                </li>
            </ul>
        </form>
    </div>
</div>
<%--列表视图--%>
<div class="mini-fit" style="height: 100%;">
    <div id="businessListGrid" idField="id" class="mini-datagrid" style="width: 100%; height: 100%;"
         allowCellWrap="true" showCellTip="true" allowCellEdit="true" allowCellSelect="true"
         allowResize="true" allowAlternating="true" showColumnsMenu="false" multiSelect="true"
         sizeList="[50,10000]" pageSize="50" pagerButtons="#pagerButtons"
         url="${ctxPath}/serviceEngineering/core/customsDeclarationRawdata/itemListQuery.do">
        <div property="columns">
            <div type="checkcolumn" width="40" trueValue="1" falseValue="0"></div>
            <div type="indexcolumn" width="45" headerAlign="center" align="center" allowSort="true">序号</div>
            <div field="machineModel" width="150" headerAlign="center" align="center" renderer="render">适用机型</div>
            <div field="materialCode" width="100" headerAlign="center" align="center">物料号</div>
            <div field="materialDescription" width="150" headerAlign="center" align="center" renderer="render">物料描述</div>
            <div field="hsCode" width="120" headerAlign="center" align="center">HS编码</div>
            <div field="elements" width="300" headerAlign="center" align="center" renderer="render">报关要素</div>
            <div field="elementsFill" width="300" headerAlign="center" align="center" renderer="render">报关要素填写</div>
            <div field="additionalInfo" width="200" headerAlign="center" align="center" renderer="render">补充信息</div>
            <div field="netWeight" width="80" headerAlign="center" align="center">净重</div>
            <div field="remarks" width="200" headerAlign="center" align="center" renderer="render">备注</div>
            <div field="processUser" width="80" headerAlign="center" align="center">处理人</div>
            <div field="processTime" width="100" headerAlign="center" align="center">处理完成时间</div>
            <div field="batchNo" width="130" headerAlign="center" align="center">批次号</div>
            <div field="businessNo" width="130" headerAlign="center" align="center">单据号</div>
        </div>
    </div>
</div>
<!--导出Excel相关HTML-->
<form id="excelForm" action="${ctxPath}/serviceEngineering/core/customsDeclarationRawdata/exportItemList.do" method="post" target="excelIFrame">
    <input type="hidden" name="filter" id="filter"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>
<%--导入窗口--%>
<div id="importWindow" title="导入窗口" class="mini-window" style="width:750px;height:280px;" showModal="true" showFooter="false" allowResize="true">
    <p style="display: inline-block;color: red;font-size:15px;vertical-align: middle">
    </p>
    <div style="display: inline-block;float: right;">
        <a id="importBtn" class="mini-button" onclick="importItem()">导入</a>
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
<%--JS--%>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var currentUserName = "${currentUserName}";
    var isCustomsDeclarationAdmin = '${isCustomsDeclarationAdmin}';
    var businessListGrid = mini.get("businessListGrid");
    var importWindow = mini.get("importWindow");
    //..
    $(function () {
        if (isCustomsDeclarationAdmin == "true" || currentUserNo == "admin") {
            mini.get("deleteBusiness").setEnabled(true);
            mini.get("openImportWindow").setEnabled(true);
        } else {
            mini.get("deleteBusiness").setEnabled(false);
            mini.get("openImportWindow").setEnabled(false);
        }
    });
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
        var excelForm = $("#excelForm");
        excelForm.submit();
    }
    //..常规渲染
    function render(e) {
        if (e.value != null && e.value != "") {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            return html;
        }
    }
    //..
    function deleteBusiness(record) {
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
                    rowIds.push(r.id);
                }
                _SubmitJson({
                    url: jsUseCtxPath + "/serviceEngineering/core/customsDeclarationRawdata/deleteItem.do",
                    method: 'POST',
                    data: {ids: rowIds.join(',')},
                    postJson: false,
                    showMsg: false,
                    success: function (returnData) {
                        if (returnData.success) {
                            mini.alert(returnData.message);
                            searchFrm();
                        } else {
                            mini.alert("删除失败:" + returnData.message);
                        }
                    },
                    fail: function (returnData) {
                        mini.alert("删除失败:" + returnData.message);
                    }
                });
            }
        });
    }
    //..
    function openImportWindow() {
        importWindow.show();
    }
    //..
    function importItem() {
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
            xhr.open('POST', jsUseCtxPath +
                '/serviceEngineering/core/customsDeclarationRawdata/importItem.do?businessId=import', false);
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
        window.location.reload();
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
        form.attr("action", jsUseCtxPath + "/serviceEngineering/core/customsDeclarationRawdata/importItemTDownload.do");
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
</script>
<redxun:gridScript gridId="businessListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>