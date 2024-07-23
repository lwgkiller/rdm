<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>项目成员</title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<div class="mini-toolbar">
    <ul class="toolBtnBox">
        <li style="float: left">
            <a id="addButton" class="mini-button" iconCls="icon-add" onclick="addRow()">新增</a>
            <a  class="mini-button" iconCls="icon-reload" onclick="refresh()" plain="true">刷新</a>
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
    <div id="memberGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         url="${ctxPath}/rdmZhgl/core/yfjb/memberList.do?mainId=${mainId}"
         idField="id" allowAlternating="true" showPager="true" multiSelect="true" allowCellEdit="true"
         allowCellSelect="true"
         editNextOnEnterKey="true" editNextRowCell="true">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div type="indexcolumn" headerAlign="center" align="center" width="20">序号</div>
            <div field="userId" displayfield="userName" width="100" headerAlign="center" align="center">项目成员<span
                    style="color: #ff0000">*</span>
                <input property="editor" class="mini-user" style="width:auto;"  single="true"/>
            </div>
            <div field="ratio" name="ratio"   width="100" headerAlign="center" align="center">系数<span
                    style="color: #ff0000">*</span>
                <input  property="editor"  class="mini-spinner" minValue="0" maxValue="1" style="width:100%"/>
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
    var mainId="${mainId}";
    var memberGrid = mini.get("memberGrid");
    var permission = ${permission};
    memberGrid.on("cellbeginedit", function (e) {
        var record = e.record;
    });
    if(!permission){
        mini.get('addButton').setEnabled(false);
        mini.get('delButton').setEnabled(false);
        mini.get('saveButton').setEnabled(false);
    }

    function addRow() {
        var newRow = {name: "New Row"};
        memberGrid.addRow(newRow, 0);
        memberGrid.beginEditCell(newRow, "ratingName");
    }

    function removeRow() {
        var rows = memberGrid.getSelecteds();
        if (rows.length > 0) {
            mini.showMessageBox({
                title: "提示信息！",
                iconCls: "mini-messagebox-info",
                buttons: ["ok", "cancel"],
                message: "是否确定删除！",
                callback: function (action) {
                    if (action == "ok") {
                        memberGrid.removeRows(rows, false);
                    }
                }
            });

        } else {
            mini.alert("请至少选中一条记录");
            return;
        }
    }

    function refresh() {
        memberGrid.load();
    }

    memberGrid.on("beforeload", function (e) {
        if (memberGrid.getChanges().length > 0) {
            if (confirm("有增删改的数据未保存，是否取消本次操作？")) {
                e.cancel = true;
            }
        }
    });

    function save() {
        //先验证系统
        var listData = memberGrid.getData();
        var radioSum = '';
        for(var i=0;i<listData.length;i++){
            radioSum += listData[i].ratio;
        }
        if(radioSum>1){
            alert("总系数不能大于1");
            return;
        }
        var data = memberGrid.getChanges();
        var message = "数据保存成功";
        var needReload = true;
        if (data.length > 0) {
            for (var i = 0; i < data.length; i++) {

                if (data[i]._state == 'removed') {
                    continue;
                }
                if (!data[i].userId ||!data[i].ratio ) {
                    message = "请填写必填项！";
                    needReload = false;
                    break;
                }
            }
            if (needReload) {
                var json = mini.encode(data);
                $.ajax({
                    url: jsUseCtxPath + "/rdmZhgl/core/yfjb/dealMemberData.do?mainId="+mainId,
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
                    memberGrid.reload();
                }
            }
        });
    }

    memberGrid.on("cellcommitedit", function (e) {
    });

</script>
<redxun:gridScript gridId="memberGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
