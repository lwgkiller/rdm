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
                    <span class="text" style="width:auto">项目名称：</span>
                    <input class="mini-textbox" id="projectName" name="projectName"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">首席工程师：</span>
                    <input class="mini-textbox" id="chiefEngineer" name="chiefEngineer"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">项目起止日期：</span>
                    <input class="mini-textbox" id="beginEndDate" name="beginEndDate"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">资本化年份：</span>
                    <input class="mini-textbox" id="capitalizationYear" name="capitalizationYear"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">专利号：</span>
                    <input class="mini-textbox" id="patentNumber" name="patentNumber"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">专利名称：</span>
                    <input class="mini-textbox" id="patentName" name="patentName"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">专利类型：</span>
                    <input class="mini-textbox" id="patentType" name="patentType"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">受理情况：</span>
                    <input class="mini-textbox" id="acceptance" name="acceptance"/>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="removeBusiness()">删除</a>
                    <a class="mini-button" id="importId" style="margin-left: 10px" onclick="openImportWindow()">导入</a>
                </li>
            </ul>
        </form>
        <span class="searchSelectionBtn" onclick="no_search(this ,'searchForm')">
				<i class="icon-sc-lower"></i>
        </span>
    </div>
</div>

<div class="mini-fit" style="height: 100%;">
    <div id="SgykCapitalizationProjectListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" allowCellWrap="true"
         showCellTip="true" idField="id" allowCellEdit="true" allowCellSelect="true"
         multiSelect="true" showColumnsMenu="false" sizeList="[100,200]" pageSize="100" allowAlternating="true" pagerButtons="#pagerButtons"
         url="${ctxPath}/zhgl/core/sgykCapitalizationProject/dataListQuery.do"
         ondrawsummarycell="onDrawSummaryCell" showSummaryRow="true">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div field="orderNum" name="orderNum" width="40" headerAlign="center" align="center">序号</div>
            <div field="projectName" name="projectName" width="300" headerAlign="center" align="center">项目名称</div>
            <div field="chiefEngineer" width="120" headerAlign="center" align="center">首席工程师</div>
            <div field="beginEndDate" width="150" headerAlign="center" align="center">项目起止日期</div>
            <div field="capitalizationYear" width="80" headerAlign="center" align="center">资本化年份</div>
            <div field="patentNumber" width="150" headerAlign="center" align="center">专利号</div>
            <div field="patentName" width="300" headerAlign="center" align="center">专利名称</div>
            <div field="patentType" width="80" headerAlign="center" align="center">专利类型</div>
            <div field="acceptance" width="80" headerAlign="center" align="center">受理情况</div>
            <div field="capitalizationAmount" name="capitalizationAmount" width="150" headerAlign="center"
                 align="center" numberFormat="c">资本化金额
            </div>
            <div field="remark" name="orderNum" width="300" headerAlign="center" align="center">说明</div>
        </div>
    </div>
</div>

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
                        <a href="#" style="color:blue;text-decoration:underline;" onclick="downImportTemplate()">研发资本化项目导入模板.xlsx</a>
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
    var SgykCapitalizationProjectListGrid = mini.get("SgykCapitalizationProjectListGrid");
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var importWindow = mini.get("importWindow");
    SgykCapitalizationProjectListGrid.frozenColumns(0, 2);
    SgykCapitalizationProjectListGrid.on("load", function () {
        SgykCapitalizationProjectListGrid.mergeColumns(["orderNum", "projectName", "capitalizationAmount"]);
    });
    //所有非特殊文本都变成这个格式
    function render(e) {
        if (e.value != null && e.value != "") {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            return html;
        }
    }
    //..
    function removeBusiness(record) {
        var rows = [];
        if (record) {
            rows.push(record);
        } else {
            rows = SgykCapitalizationProjectListGrid.getSelecteds();
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
                    url: jsUseCtxPath + "/zhgl/core/sgykCapitalizationProject/deleteData.do",
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

            // 开始上传
            xhr.open('POST', jsUseCtxPath + '/zhgl/core/sgykCapitalizationProject/importExcel.do', false);
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
        form.attr("action", jsUseCtxPath + "/zhgl/core/sgykCapitalizationProject/importTemplateDownload.do");
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
                mini.alert('请上传excel文件！');
            }
        }
    }
    //..
    function onDrawSummaryCell(e) {
        if (e.field == "acceptance") {
            e.cellHtml = '<span style="text-align:center;display:block;font-size:15px;color:#ef1b01" >金额总计：</span>';
        }
        if (e.field == "capitalizationAmount") {
            var rows = e.data;
            var total = 0;
            var map = new Map();
            debugger;
            for (var i = 0, l = rows.length; i < l; i++) {
                var row = rows[i];
                map.set(row["projectName"] + row["patentNumber"], row[e.field])
            }
            map.forEach(function (key) {
                total += parseFloat(key);
            })
            e.cellHtml = '<span style="text-align:center;display:block;font-size:15px;color:#ef1b01">￥' + total.toFixed(2) + '</span>';
        }
    }
</script>
<redxun:gridScript gridId="SgykCapitalizationProjectListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>