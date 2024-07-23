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
                    <span class="text" style="width:auto">替换分组：</span>
                    <input class="mini-textbox" id="busunessNo" name="busunessNo"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">替换类型：</span>
                    <input id="replaceType" name="replaceType" class="mini-combobox" style="width:98%"
                           url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=serviceEngineeringReplacementRelationshipReplaceType"
                           valueField="key" textField="value" multiSelect="false"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">信息源替换类型：</span>
                    <input id="replaceTypeOri" name="replaceTypeOri" class="mini-combobox" style="width:98%"
                           url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=serviceEngineeringReplacementRelationshipReplaceTypeOri"
                           valueField="key" textField="value" multiSelect="false"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">信息来源：</span>
                    <input class="mini-textbox" id="informationSources" name="informationSources"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">信息来源：</span>
                    <input class="mini-textbox" id="informationSources" name="informationSources"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">原车件物料：</span>
                    <input class="mini-textbox" id="materialCodeOri" name="materialCodeOri"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">替换件物料：</span>
                    <input class="mini-textbox" id="materialCodeRep" name="materialCodeRep"/>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <a id="removeBusiness" class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="removeBusiness()">删除</a>
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
         url="${ctxPath}/serviceEngineering/core/replacementRelationship/masterdataListQuery.do" virtualScroll="false">
        <div property="columns">
            <div type="checkcolumn" width="40"></div>
            <div field="busunessNo" width="150" headerAlign="center" align="center" allowSort="true" renderer="render">替换分组</div>
            <div field="busunessNoItem" width="80" headerAlign="center" align="center" allowSort="true" renderer="render">行项目号</div>
            <div field="materialCodeOri" width="140" headerAlign="center" align="center" allowSort="true" renderer="render">原车件物料</div>
            <div field="materialNameOri" width="140" headerAlign="center" align="center" allowSort="true" renderer="render">原车件名称</div>
            <div field="materialCountOri" width="140" headerAlign="center" align="center" allowSort="true" renderer="render">原车件数量</div>
            <div field="materialCodeRep" width="140" headerAlign="center" align="center" allowSort="true" renderer="render">替换件物料</div>
            <div field="materialNameRep" width="140" headerAlign="center" align="center" allowSort="true" renderer="render">替换件名称</div>
            <div field="materialCountRep" width="140" headerAlign="center" align="center" allowSort="true" renderer="render">替换件数量</div>
            <div field="replaceType" width="160" headerAlign="center" align="center" allowSort="true" renderer="render">替换类型</div>
            <div field="replaceTypeOri" width="140" headerAlign="center" align="center" allowSort="true" renderer="render">信息源替换类型</div>
            <div field="applicableModels" width="160" headerAlign="center" align="center" allowSort="true" renderer="render">适用机型</div>
            <div field="WIPHandlingComments" width="160" headerAlign="center" align="center" allowSort="true" renderer="render">制品处理意见</div>
            <div field="informationSources" width="160" headerAlign="center" align="center" allowSort="true" renderer="render">信息来源</div>
            <div field="remark" width="160" headerAlign="center" align="center" allowSort="true" renderer="render">备注</div>
            <div field="createUser" width="90" headerAlign="center" align="center">创建人</div>
            <div field="CREATE_TIME_" width="90" headerAlign="center" align="center">创建时间</div>
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
                        <a href="#" style="color:blue;text-decoration:underline;" onclick="downImportTemplate()">物料替换关系导入模板.xlsx</a>
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
<form id="excelForm" action="${ctxPath}/serviceEngineering/core/replacementRelationship/exportListMasterdata.do" method="post" target="excelIFrame">
    <input type="hidden" name="filter" id="filter"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>
<%-------------------------------%>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var businessListGrid = mini.get("businessListGrid");
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var currentUserName = "${currentUserName}";
    var whatIsTheRole = "${whatIsTheRole}";//sa,apply,browse
    var importWindow = mini.get("importWindow");
    //..
    $(function () {
        if (whatIsTheRole == 'apply') {
            mini.get("removeBusiness").setEnabled(false);
        } else if (whatIsTheRole == 'browse') {
            mini.get("removeBusiness").setEnabled(false);
            mini.get("openImportWindow").setEnabled(false);
            //mini.get("exportBusiness").setEnabled(false);
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
                    url: jsUseCtxPath + "/serviceEngineering/core/replacementRelationship/deleteBusinessMasterdata.do",
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
            xhr.open('POST', jsUseCtxPath + '/serviceEngineering/core/replacementRelationship/importMasterdata.do', false);
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
        form.attr("action", jsUseCtxPath + "/serviceEngineering/core/replacementRelationship/importMasterdataTemplateDownload.do");
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
</script>
<redxun:gridScript gridId="businessListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>