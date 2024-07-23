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
                    <span class="text" style="width:auto">订单信息：</span>
                    <input class="mini-textbox" id="orderNo" name="orderNo"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">批次号：</span>
                    <input class="mini-textbox" id="batchNo" name="batchNo"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">机型物料号：</span>
                    <input class="mini-textbox" id="materialCodeOfMachine" name="materialCodeOfMachine"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">物料号：</span>
                    <input class="mini-textbox" id="materialCode" name="materialCode"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">是否混投：</span>
                    <input id="isMixedInput" name="isMixedInput" class="mini-combobox"
                           textField="value" valueField="key"
                           data="[{key:'是',value:'是'},{key:'否',value:'否'}]"/>
                    </td>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">责任人：</span>
                    <input class="mini-textbox" id="repUserName" name="repUserName"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">年份：</span>
                    <input class="mini-textbox" id="signYear" name="signYear"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">月份：</span>
                    <input class="mini-textbox" id="signMonth" name="signMonth"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">状态：</span>
                    <input id="businessStatus" name="businessStatus" class="mini-combobox"
                           textField="value" valueField="key"
                           data="[{key:'有效',value:'有效'},{key:'作废',value:'作废'}]"/>
                    </td>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <a id="removeBusiness" class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="removeBusiness()">删除</a>
                    <a id="discardBusiness" class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="discardBusiness()">作废</a>
                    <a id="openImportWindow" class="mini-button" style="margin-right: 5px" plain="true" onclick="openImportWindow()">导入</a>
                    <a id="exportBusiness" class="mini-button" style="margin-right: 5px" plain="true" onclick="exportBusiness()">导出</a>
                </li>
            </ul>
        </form>
        <span class="searchSelectionBtn" onclick="no_search(this ,'searchForm')">
				<i class="icon-sc-lower"></i>
        </span>
    </div>
</div>
<%-------------------------------%>
<div class="mini-fit" style="height: 100%;">
    <div id="businessListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" allowCellWrap="true"
         showCellTip="true" idField="id" allowCellEdit="true" allowCellSelect="true" allowHeaderWrap="true"
         multiSelect="true" showColumnsMenu="false" sizeList="[50,100]" pageSize="50" allowAlternating="true" pagerButtons="#pagerButtons"
         url="${ctxPath}/serviceEngineering/core/mixedinput/masterdataListQuery.do" virtualScroll="false">
        <div property="columns">
            <div type="checkcolumn" width="40"></div>
            <div field="businessStatus" width="40" headerAlign="center" align="center">状态</div>
            <div field="orderNo" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">订单信息</div>
            <div field="batchNo" width="200" headerAlign="center" align="center" allowSort="true" renderer="render">批次号</div>
            <div field="orderInputCount" width="70" headerAlign="center" align="center" allowSort="true" renderer="render">订单数量</div>
            <div field="materialCodeOfMachine" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">机型物料号</div>
            <div field="materialDescriptionOfMachine" width="250" headerAlign="center" align="center" allowSort="true" renderer="render">机型物料描述</div>
            <div field="materialCode" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">物料号</div>
            <div field="materialDescription" width="250" headerAlign="center" align="center" allowSort="true" renderer="render">物料描述</div>
            <div field="materialCount" width="40" headerAlign="center" align="center" allowSort="true" renderer="render">数量</div>
            <div field="isMixedInput" width="70" headerAlign="center" align="center" allowSort="true" renderer="render">是否混投</div>
            <div field="remarks1" width="250" headerAlign="center" align="center" allowSort="true" renderer="render">分配车号或异常说明</div>
            <div field="remarks2" width="250" headerAlign="center" align="center" allowSort="true" renderer="render">混投依据或实际订单数量</div>
            <div field="repUserName" width="60" headerAlign="center" align="center" allowSort="true" renderer="render">责任人</div>
            <div field="signYear" width="60" headerAlign="center" align="center" allowSort="true" renderer="render">年份</div>
            <div field="signMonth" width="60" headerAlign="center" align="center" allowSort="true" renderer="render">月份</div>
            <div field="remarks" width="200" headerAlign="center" align="center">备注</div>
        </div>
    </div>
</div>
<%-------------------------------%>
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
                        <a href="#" style="color:blue;text-decoration:underline;" onclick="downImportTemplate()">混投台账导入模板.xlsx</a>
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
<!--导出Excel相关HTML-->
<form id="excelForm" action="${ctxPath}/serviceEngineering/core/mixedinput/exportListMasterdata.do" method="post" target="excelIFrame">
    <input type="hidden" name="filter" id="filter"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>
<%-------------------------------%>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var currentUserName = "${currentUserName}";
    var whatIsTheRole = "${whatIsTheRole}";//sa,apply,browse
    var importWindow = mini.get("importWindow");
    var businessListGrid = mini.get("businessListGrid");
    //..
    $(function () {
        if (whatIsTheRole == 'apply') {
            mini.get("removeBusiness").setEnabled(false);
            mini.get("discardBusiness").setEnabled(false);
        } else if (whatIsTheRole == 'browse') {
            mini.get("removeBusiness").setEnabled(false);
            mini.get("discardBusiness").setEnabled(false);
            mini.get("openImportWindow").setEnabled(false);
            mini.get("exportBusiness").setEnabled(false);
        }
    });
    //..
    function render(e) {
        if (e.value != null && e.value != "") {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            return html;
        }
    }
    //..
    function removeBusiness() {
        var rows = businessListGrid.getSelecteds();
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
                    url: jsUseCtxPath + "/serviceEngineering/core/mixedinput/deleteBusinessMasterdata.do",
                    method: 'POST',
                    data: {ids: rowIds.join(',')},
                    success: function (data) {
                        if (data) {
                            searchFrm();
                        }
                    }
                });
            }
        });
    }
    //..
    function discardBusiness() {
        var rows = businessListGrid.getSelecteds();
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
                    url: jsUseCtxPath + "/serviceEngineering/core/mixedinput/discardBusinessMasterdata.do",
                    method: 'POST',
                    data: {ids: rowIds.join(',')},
                    success: function (data) {
                        if (data) {
                            searchFrm();
                        }
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
            xhr.open('POST', jsUseCtxPath + '/serviceEngineering/core/mixedinput/importMasterdata.do', false);
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
        searchFrm();
    }
    //..
    function downImportTemplate() {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + "/serviceEngineering/core/mixedinput/importMasterdataTemplateDownload.do");
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
</script>
<redxun:gridScript gridId="businessListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>