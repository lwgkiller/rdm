<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>业务交流维度配置</title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<div class="mini-toolbar">
    <ul class="toolBtnBox">
        <li style="float: left">
            <a class="mini-button" iconCls="icon-add" onclick="addData()">新增</a>
            <a class="mini-button" iconCls="icon-reload" onclick="refreshData()" plain="true">刷新</a>
            <a class="mini-button btn-red" plain="true" onclick="removeData()">删除</a>
        </li>
        <span class="separator"></span>
        <li>
            <a class="mini-button" iconCls="icon-save" onclick="saveData()">保存</a>
            <p style="display: inline-block;color: #888;font-size:15px;vertical-align: middle">（
                <image src="${ctxPath}/styles/images/notice.png" style="margin-right:5px;vertical-align: middle;height: 15px"/>
                新增、删除、编辑后都需要进行保存操作）
            </p>
        </li>

    </ul>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="templateGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         url="${ctxPath}/zhgl/core/sgyk/templateListQuery.do"
         idField="id" allowAlternating="true" showPager="false" multiSelect="true" allowCellEdit="true" allowCellSelect="true"
         editNextOnEnterKey="true" editNextRowCell="true">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div field="orderNum" name="orderNum" width="20" headerAlign="center" align="center">顺序号<span style="color: #ff0000">*</span>
                <input property="editor" class="mini-spinner" increment="1" maxValue="1000"/>
            </div>
            <div field="normClass" name="normClass" width="30" headerAlign="center" align="center">指标类别<span style="color: #ff0000">*</span>
                <input property="editor" class="mini-combobox" style="width:98%"
                       url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=SGYKZBLB"
                       valueField="key" textField="key"/>
            </div>
            <div field="normKey" name="normKey" width="80" headerAlign="center" align="center">指标key<span style="color: #ff0000">*</span>
                <input property="editor" class="mini-combobox" style="width:98%"
                       url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=SGYKZB"
                       valueField="key" textField="value" onvaluechanged="normKeyChange"/>
            </div>
            <div field="normDesp" name="normDesp" width="100" headerAlign="center" align="center">指标描述
                <input property="editor" class="mini-textbox" readonly/>
            </div>
            <div field="calculateClass" name="calculateClass" width="30" headerAlign="center" align="center">计算类别<span style="color: #ff0000">*</span>
                <input property="editor" class="mini-textbox"/>
            </div>
            <div field="calculateParms" name="calculateParms" width="60" headerAlign="center" align="center">参数列表<span style="color: #ff0000">*</span>
                <input property="editor" class="mini-textbox"/>
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var currentUserNo = "${currentUserNo}";

    var templateGrid = mini.get("templateGrid");
    templateGrid.load();

    //..
    function addData() {
        var newRow = {calculateParms: ""};
        addRowGrid("templateGrid", newRow);
    }
    //..
    function removeData() {
        delRowGrid("templateGrid");
    }
    //..
    function refreshData() {
        templateGrid.load();
    }
    //..
    templateGrid.on("beforeload", function (e) {
        if (templateGrid.getChanges().length > 0) {
            if (confirm("有增删改的数据未保存，是否取消本次操作？")) {
                e.cancel = true;
            }
        }
    });
    //..
    function saveData() {
        var data = templateGrid.getChanges();
        var message = "数据保存成功";
        var needReload = true;
        if (data.length > 0) {
            for (var i = 0; i < data.length; i++) {
                if (data[i]._state == 'removed') {
                    continue;
                }
                if (!data[i].normClass || !data[i].normKey || !data[i].orderNum || !data[i].calculateClass) {
                    message = "请填写必填项！";
                    needReload = false;
                    break;
                }
            }
            if (needReload) {
                var json = mini.encode(data);
                $.ajax({
                    url: jsUseCtxPath + "/zhgl/core/sgyk/saveTemplate.do",
                    data: json,
                    type: "post",
                    contentType: 'application/json',
                    async: false,
                    success: function (result) {
                        if (result && result.message) {
                            message = result.message;
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
                    templateGrid.reload();
                }
            }
        });
    }
    //..
    function normKeyChange(e) {
        var row = templateGrid.getSelected();
        var aaa = e.sender.text;
        row.normDesp = aaa;
    }

</script>
</body>
</html>