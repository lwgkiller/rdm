<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>认证资料</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/wwrz/wwrzDocList.js?version=${static_res_version}"
            type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px"><span class="text" style="width:auto">资料名称: </span><input class="mini-textbox" style="width: 200px" name="docName"/></li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">分类: </span>
                    <input id="docType" name="docType" class="mini-combobox rxc" plugins="mini-combobox"
                           style="width:200px;height:34px" label="分类："
                           length="50" onvaluechanged="searchFrm()"
                           only_read="false" required="false" allowinput="false" mwidth="100"
                           wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                           textField="text" valueField="text" emptyText="请选择..."
                           url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=RZZLLX"
                           nullitemtext="请选择..." emptytext="请选择..."/>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                </li>
            </ul>
        </form>
        <!--导出Excel相关HTML-->
        <form id="excelForm" action="${ctxPath}/wwrz/core/money/exportExcel.do" method="post"
              target="excelIFrame">
            <input type="hidden" name="pageIndex" id="pageIndex"/>
            <input type="hidden" name="pageSize" id="pageSize"/>
            <input type="hidden" name="filter" id="filter"/>
        </form>
        <iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>
        <ul class="toolBtnBox">
            <li style="float: left">
                <a id="addButton" class="mini-button" iconCls="icon-add" onclick="addRow()">新增</a>
                <a id="refreshButton" class="mini-button" iconCls="icon-reload" onclick="refresh()" plain="true">刷新</a>
                <a id="delButton" class="mini-button btn-red" plain="true" onclick="removeRow()">删除</a>
            </li>
            <span class="separator"></span>
            <li>
                <a id="saveButton" class="mini-button" iconCls="icon-save" onclick="save()">保存</a>
                <a class="mini-button" id="importId" style="margin-left: 10px" img="${ctxPath}/scripts/mini/miniui/res/images/textfield_add.png"
                   onclick="openImportWindow()">导入</a>
                <p style="display: inline-block;color: #888;font-size:15px;vertical-align: middle">（
                    <image src="${ctxPath}/styles/images/warn.png"
                           style="margin-right:5px;vertical-align: middle;height: 15px"/>
                    新增、删除、编辑后都需要进行保存操作）
                </p>
            </li>

        </ul>
    </div>

</div>
<div class="mini-fit" style="height: 100%;">
    <div id="listGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         url="${ctxPath}/wwrz/core/doc/listData.do" pageSize="15"
         idField="id" allowAlternating="true" showPager="true" multiSelect="true" allowCellEdit="true"
         allowCellSelect="true"
         editNextOnEnterKey="true" editNextRowCell="true">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div type="indexcolumn" headerAlign="center" align="center" width="20">序号</div>
            <div field="docType"    width="100" headerAlign="center" align="center">资料类型<span
                    style="color: #ff0000">*</span>
                <input  property="editor"  name="docType" class="mini-combobox rxc" plugins="mini-combobox"
                       style="width:100%;height:34px"  label="年度："
                       length="50"
                       only_read="false" required="true" allowinput="false" mwidth="100"
                       wunit="%" mheight="34" hunit="px"   shownullitem="true" multiSelect="false"
                       textField="text" valueField="value" emptyText="请选择..."
                        url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=RZZLLX"
                       nullitemtext="请选择..." emptytext="请选择..."/>
            </div>
            <div field="docName"  width="200" headerAlign="center" align="center">资料名称<span
                    style="color: #ff0000">*</span>
                <input property="editor"  class="mini-textbox"/>
            </div>
            <div field="remark"  width="100" headerAlign="center" align="center">备注
                <input property="editor"  class="mini-textbox"/>
            </div>
            <div field="creator" width="80" headerAlign="center" align="center">创建人
                <input property="editor" class="mini-textbox" readonly/>
            </div>
            <div field="CREATE_TIME_" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" width="120" headerAlign="center">
                创建时间
                <input property="editor" class="mini-textbox" readonly/>
            </div>
        </div>
    </div>
</div>
<div id="importWindow" title="认证资料导入窗口" class="mini-window" style="width:750px;height:280px;"
     showModal="true" showFooter="false" allowResize="true">
    <p style="display: inline-block;color: red;font-size:15px;vertical-align: middle">
    </p>
    <div style="display: inline-block;float: right;">
        <a id="importBtn" class="mini-button" onclick="importDocument()">导入</a>
        <a id="closeWindow" class="mini-button btn-red" onclick="closeImportWindow()">关闭</a>
    </div>
    <div class="mini-fit" style="font-size: 14px;margin-top: 30px">
        <form id="formImport" method="post">
            <table class="table-detail" cellspacing="1" cellpadding="0">
                <tr>
                    <td style="width: 30%">上传模板：</td>
                    <td style="width: 70%;">
                        <a href="#" style="color:blue;text-decoration:underline;" onclick="downImportTemplate()">认证资料导入模板.xls</a>
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
    var listGrid = mini.get("listGrid");
    var importWindow = mini.get("importWindow");
    listGrid.on("cellbeginedit", function (e) {
        var record = e.record;
    });

    function addRow() {
        var newRow = {name: "New Row"};
        listGrid.addRow(newRow, 0);
        listGrid.beginEditCell(newRow, "docType");
    }

    function removeRow() {
        var rows = listGrid.getSelecteds();
        if (rows.length > 0) {
            mini.showMessageBox({
                title: "提示信息！",
                iconCls: "mini-messagebox-info",
                buttons: ["ok", "cancel"],
                message: "是否确定删除！",
                callback: function (action) {
                    if (action == "ok") {
                        listGrid.removeRows(rows, false);
                    }
                }
            });

        } else {
            mini.alert("请至少选中一条记录");
            return;
        }
    }

    function refresh() {
        listGrid.load();
    }

    listGrid.on("beforeload", function (e) {
        if (listGrid.getChanges().length > 0) {
            if (confirm("有增删改的数据未保存，是否取消本次操作？")) {
                e.cancel = true;
            }
        }
    });

    function save() {
        var data = listGrid.getChanges();
        var message = "数据保存成功";
        var needReload = true;
        if (data.length > 0) {
            for (var i = 0; i < data.length; i++) {
                if (data[i]._state == 'removed') {
                    continue;
                }
                if (!data[i].docType ||!data[i].docName) {
                    message = "请填写必填项！";
                    needReload = false;
                    break;
                }
            }
            if (needReload) {
                var json = mini.encode(data);
                $.ajax({
                    url: jsUseCtxPath + "/wwrz/core/doc/dealData.do",
                    data: json,
                    type: "post",
                    contentType: 'application/json',
                    async: false,
                    success: function (text) {
                        if (text && text.message) {
                            message = text.message;
                        }
                    }
                });
            }
        }
        mini.showMessageBox({
            title: "提示信息",
            iconCls: "mini-messagebox-info",
            buttons: ["ok"],
            message: message,
            callback: function (action) {
                if (action == "ok" && needReload) {
                    listGrid.reload();
                }
            }
        });
    }

    listGrid.on("cellcommitedit", function (e) {
    });

</script>
<redxun:gridScript gridId="listGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
