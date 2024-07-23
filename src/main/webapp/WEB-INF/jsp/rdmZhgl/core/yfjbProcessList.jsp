<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>降本项目进度跟踪</title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<div class="mini-toolbar">
    <ul class="toolBtnBox">
        <li style="float: left">
            <a id="addButton" class="mini-button" iconCls="icon-add" onclick="addRow()">新增</a>
            <a class="mini-button" iconCls="icon-reload" onclick="refresh()" plain="true">刷新</a>
            <a id="delButton" class="mini-button btn-red" plain="true" onclick="removeRow()">删除</a>
        </li>
        <span class="separator"></span>
        <li>
            <a id="saveButton" class="mini-button" iconCls="icon-save" onclick="save()">保存</a>
            <p style="display: inline-block;color: #888;font-size:15px;vertical-align: middle">（
                <image src="${ctxPath}/styles/images/warn.png"
                       style="margin-right:5px;vertical-align: middle;height: 15px"/>
                新增、删除、编辑后都需要进行保存操作）
            </p>
        </li>

    </ul>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="processGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         url="${ctxPath}/rdmZhgl/core/yfjb/processList.do?mainId=${mainId}"
         idField="id" allowAlternating="true" showPager="true" multiSelect="true" allowCellEdit="true"
         allowCellSelect="true" ondrawcell="onDrawCell"
         editNextOnEnterKey="true" editNextRowCell="true">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div type="indexcolumn" headerAlign="center" align="center" width="30">序号</div>
            <div field="yearMonth" name="yearMonth" dateFormat="yyyy-MM" width="100" headerAlign="center"
                 align="center">年月<span
                    style="color: #ff0000">*</span>
                <input property="editor" class="mini-monthpicker" allowinput="false" name="yearMonth"/>
            </div>
            <div field="type" name="type" width="100" displayfield="typeName" headerAlign="center" align="center">进度类别
                <input property="editor" textField="text" valueField="key_" emptyText="请选择..." class="mini-combobox"
                       allowInput="false"
                       url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=YFJB-JDLB">
            </div>
            <div field="processStatus" name="processStatus" width="100" displayfield="processStatusName"
                 headerAlign="center" align="center">进度状态
                <input property="editor" textField="text" valueField="key_" emptyText="请选择..." class="mini-combobox"
                       allowInput="false"
                       url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=YFJB-JDZT">
            </div>
            <div field="content" name="content" width="200" headerAlign="center" align="center">内容<span
                    style="color: #ff0000">*</span>
                <input property="editor" class="mini-textbox"/>
            </div>
            <div field="reason" name="reason" width="200" headerAlign="center" align="center">延后原因
                <input property="editor" class="mini-textbox"/>
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

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var mainId = "${mainId}";
    var processGrid = mini.get("processGrid");
    var permission = ${permission};
    processGrid.on("cellbeginedit", function (e) {
        var record = e.record;
    });
    if(!permission){
        mini.get('addButton').setEnabled(false);
        mini.get('delButton').setEnabled(false);
        mini.get('saveButton').setEnabled(false);
    }
    function addRow() {
        var newRow = {name: "New Row"};
        processGrid.addRow(newRow, 0);
        processGrid.beginEditCell(newRow, "ratingName");
    }

    function onDrawCell(e) {
        var record = e.record;
        var field = e.field;
        if (field) {
            if (field == 'processStatus') {
                var processStatus = record.processStatus;
                if(processStatus=='tq'){
                    e.cellStyle = "background-color:green" ;
                }else if(processStatus=='zc'){
                }else if(processStatus=='yh'){
                    e.cellStyle = "background-color:red" ;
                }
            }
        }
    }

    function removeRow() {
        var rows = processGrid.getSelecteds();
        if (rows.length > 0) {
            mini.showMessageBox({
                title: "提示信息！",
                iconCls: "mini-messagebox-info",
                buttons: ["ok", "cancel"],
                message: "是否确定删除！",
                callback: function (action) {
                    if (action == "ok") {
                        processGrid.removeRows(rows, false);
                    }
                }
            });

        } else {
            mini.alert("请至少选中一条记录");
            return;
        }
    }

    function refresh() {
        processGrid.load();
    }

    processGrid.on("beforeload", function (e) {
        if (processGrid.getChanges().length > 0) {
            if (confirm("有增删改的数据未保存，是否取消本次操作？")) {
                e.cancel = true;
            }
        }
    });

    function save() {
        var data = processGrid.getChanges();
        var message = "数据保存成功";
        var needReload = true;
        if (data.length > 0) {
            for (var i = 0; i < data.length; i++) {
                if (data[i]._state == 'removed') {
                    continue;
                }
                if (!data[i].yearMonth || !data[i].type || !data[i].content) {
                    message = "请填写必填项！";
                    needReload = false;
                    break;
                }
                if(data[i].processStatus=='yh'){
                    if(!data[i].reason){
                        message = "延后项目必须填写原因！";
                        needReload = false;
                        break;
                    }
                }
            }
            if (needReload) {
                var json = mini.encode(data);
                $.ajax({
                    url: jsUseCtxPath + "/rdmZhgl/core/yfjb/dealData.do?mainId=" + mainId,
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
                    processGrid.reload();
                }
            }
        });
    }

    processGrid.on("cellcommitedit", function (e) {
    });

</script>
<redxun:gridScript gridId="processGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
