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
    <div id="itemGrid" class="mini-datagrid" style=" height: 100%;" allowResize="false"
         url="${ctxPath}/rdmZhgl/core/gzxm/project/items.do?mainId=${mainId}"
         idField="id" allowAlternating="true" showPager="false" multiSelect="true" allowCellEdit="true"
         allowCellSelect="true" allowCellWrap="true"
         editNextOnEnterKey="true" editNextRowCell="true">
        <div property="columns">
            <div type="checkcolumn" width="30px"></div>
            <div type="indexcolumn" headerAlign="center" align="center" width="40px">序号</div>
            <div field="indexSort" displayfield="indexSort" width="80px" headerAlign="center" align="center">
                编号<span style="color: #ff0000">*</span>
                <input property="editor" class="mini-textbox" allowLimitValue="false"
                       required="true" only_read="false" allowinput="true" allowNull="true" value="null"
                       decimalPlaces="0" style="width:100%;height:34px"/>
            </div>
            <div field="important" width="60px" headerAlign="center" renderer="onImportant" align="center">
                重要度<span
                    style="color: #ff0000">*</span>
                <input property="editor" class="mini-combobox" textField="text" valueField="key_"
                       emptyText="请选择..." required
                       showNullItem="false" nullItemText="请选择..."
                       url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=ZDXMZYJD"/>

            </div>
            <div field="taskName" displayfield="taskName" width="150px" headerAlign="center" align="left">
                任务名称<span
                    style="color: #ff0000">*</span>
                <input property="editor" class="mini-textbox" allowLimitValue="false"
                       required="true" only_read="false" allowinput="true" allowNull="true" value="null"
                       decimalPlaces="0" style="width:100%;height:34px"/>
            </div>
            <div field="taskTarget" name="taskTarget" displayfield="taskTarget" width="200px" headerAlign="center"
                 align="left">
                任务目标<span
                    style="color: #ff0000">*</span>
                <input property="editor" class="mini-textarea" allowLimitValue="false"
                       required="true" only_read="false" allowinput="true" allowNull="true" value="null"
                       decimalPlaces="0" style="width:100%;height:34px"/>
            </div>
            <div field="outputFile" displayfield="outputFile" width="200px" headerAlign="center" align="left">
                输出物<span
                    style="color: #ff0000">*</span>
                <input property="editor" class="mini-textarea" allowLimitValue="false"
                       required="true" only_read="false" allowinput="true" allowNull="true" value="null"
                       decimalPlaces="0" style="width:100%;height:34px"/>
            </div>
            <div  renderer="detailAttach"  align="center" width="80" headerAlign="center">
                输出物文件
            </div>
            <div field="resDeptIds" displayField="resDeptIds" align="center" width="100px" headerAlign="center">
                责任部门<span
                    style="color: #ff0000">*</span>
                <input property="editor" class="mini-textbox" allowLimitValue="false"
                       required="true" only_read="false" allowinput="true" allowNull="true" value="null"
                       decimalPlaces="0" style="width:100%;height:34px"/>
            </div>
            <div field="resUserIds" displayfield="resUserIds" width="100px" headerAlign="center" align="center">责任人<span
                    style="color: #ff0000">*</span>
                <input property="editor" class="mini-textbox" allowLimitValue="false"
                       required="true" only_read="false" allowinput="true" allowNull="true" value="null"
                       decimalPlaces="0" style="width:100%;height:34px"/>
            </div>
            <div field="planStartDate" width="100px" headeralign="center" allowsort="true" align="center"
                 dateFormat="yyyy-MM-dd">
                计划开始时间<span
                    style="color: #ff0000">*</span>
                <input property="editor" required="true" class="mini-datepicker" required style="width:100%;"/>
            </div>
            <div field="planEndDate" width="100px" headeralign="center" allowsort="true" align="center"
                 dateFormat="yyyy-MM-dd">
                计划结束时间<span
                    style="color: #ff0000">*</span>
                <input property="editor" required="true" class="mini-datepicker" style="width:100%;"/>
            </div>
            <div field="actStartDate" width="90px" headeralign="center" allowsort="true" align="center"
                 dateFormat="yyyy-MM-dd">
                实际开始时间
                <input property="editor" class="mini-datepicker" style="width:100%;"/>
            </div>
            <div field="actEndDate" width="90px" headeralign="center" allowsort="true" align="center"
                 dateFormat="yyyy-MM-dd">
                实际结束时间
                <input property="editor" class="mini-datepicker" style="width:100%;"/>
            </div>
            <div field="reason" displayfield="reason" width="150px" headerAlign="center" align="center">
                延期原因与补救措施
                <input property="editor" class="mini-textbox" allowLimitValue="false"
                       required="false" only_read="false" allowinput="true" allowNull="true" value="null"
                       decimalPlaces="0" style="width:100%;height:34px"/>
            </div>
            <div field="remark" displayfield="remark" width="250px" headerAlign="center" align="center">
                备注
                <input property="editor" class="mini-textbox" allowLimitValue="false" emptyText="请填论文、专利等任务目标的当前进展"
                       required="false" only_read="false" allowinput="true" allowNull="true" value="null"
                       decimalPlaces="0" style="width:100%;height:34px"/>
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var mainId = "${mainId}";
    var itemGrid = mini.get("itemGrid");
    var gzxmAdmin = ${gzxmAdmin};
    var isReporter = ${isReporter};
    var importantList = getDics("ZDXMZYJD");
    var currentUserId = "${currentUser.userId}";
    itemGrid.on("cellbeginedit", function (e) {
        var field = e.field;
        if(!gzxmAdmin){
            if(isReporter){
                if(field!='actStartDate'&&field!='actEndDate'&&field!='reason'&&field!='remark'){
                    e.cancel = true;
                }
            }
        }
    });
    itemGrid.on("load", function () {
        itemGrid.mergeColumns(["taskTarget"]);
    });
    if (!gzxmAdmin) {
        mini.get('addButton').setEnabled(false);
        mini.get('delButton').setEnabled(false);
        mini.get('saveButton').setEnabled(false);
    }
    if (isReporter) {
        mini.get('saveButton').setEnabled(true);
    }
    function addRow() {
        var newRow = {name: "New Row"};
        itemGrid.addRow(newRow, 0);
        itemGrid.beginEditCell(newRow, "indexSort");
    }

    function removeRow() {
        var rows = itemGrid.getSelecteds();
        if (rows.length > 0) {
            mini.showMessageBox({
                title: "提示信息！",
                iconCls: "mini-messagebox-info",
                buttons: ["ok", "cancel"],
                message: "是否确定删除！",
                callback: function (action) {
                    if (action == "ok") {
                        itemGrid.removeRows(rows, false);
                    }
                }
            });

        } else {
            mini.alert("请至少选中一条记录");
            return;
        }
    }

    function refresh() {
        itemGrid.load();
    }

    itemGrid.on("beforeload", function (e) {
        if (itemGrid.getChanges().length > 0) {
            if (confirm("有增删改的数据未保存，是否取消本次操作？")) {
                e.cancel = true;
            }
        }
    });

    function save() {
        var data = itemGrid.getChanges();
        var message = "数据保存成功";
        var needReload = true;
        if (data.length > 0) {
            for (var i = 0; i < data.length; i++) {
                data[i].mainId = mainId;
                if (data[i]._state == 'removed') {
                    continue;
                }
                if (!data[i].indexSort || !data[i].important || !data[i].taskName || !data[i].taskTarget
                    || !data[i].outputFile || !data[i].resDeptIds || !data[i].resUserIds || !data[i].planStartDate || !data[i].planEndDate) {
                    message = "请填写必填项！";
                    needReload = false;
                    break;
                }
            }
            if (needReload) {
                var json = mini.encode(data);
                $.ajax({
                    url: jsUseCtxPath + "/rdmZhgl/core/gzxm/project/dealData.do",
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
                    itemGrid.reload();
                }
            }
        });
    }

    itemGrid.on("cellcommitedit", function (e) {
    });
    function onImportant(e) {
        var record = e.record;
        var value = record.important;
        var resultText = '';
        for (var i = 0; i < importantList.length; i++) {
            if (importantList[i].key_ == value) {
                resultText = importantList[i].text;
                break
            }
        }
        return resultText;
    }
    function detailAttach(e) {
        var record = e.record;
        var taskId = record.id;
        var editable = "false";
        if(record.CREATE_BY_ == currentUserId){
            editable = "true"
        }
        var s = '';
        if(taskId==''||taskId=='undefined'||taskId==undefined){
            s += '<span  title="文件列表" style="color: grey"">文件列表</span>';
        }else{
            s += '<span  title="文件列表" style="cursor: pointer;color: #0a7ac6" onclick="showFilePage(\'' + taskId + '\',\'' + editable + '\')">文件列表</span>';
        }
        return s;
    }
    function showFilePage(taskId,editable) {
        mini.open({
            title: "文件列表",
            url: jsUseCtxPath + "/rdmZhgl/core/gzxm/project/fileWindow.do?taskId=" + taskId + "&fileType=1&editable="+editable+"&gzxmAdmin="+gzxmAdmin+"&isReporter="+isReporter,
            width: 1000,
            height: 500,
            showModal: false,
            allowResize: true,
            onload: function () {
            },
            ondestroy: function (action) {
            }
        });
    }
</script>
<redxun:gridScript gridId="itemGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
