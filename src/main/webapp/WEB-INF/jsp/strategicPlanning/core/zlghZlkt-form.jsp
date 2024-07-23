<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>战略课题-新增或编辑</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/xcmgProjectManager/projectUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
</head>
<body>
<div class="topToolBar">
    <div>
        <a class="mini-button" onclick="SaveData()">保存</a>
        <a class="mini-button" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin: 0 auto">
        <form id="form" method="post">
            <input id="zlktId" name="zlktId" class="mini-hidden"/>
            <input id="_state" name="_state" class="mini-hidden"/>
            <table class="table-detail" cellspacing="1" cellpadding="0">
                <tr>
                    <td style="width: 10%">战略举措：<span style="color: #ff0000">*</span></td>
                    <td style="width: 40%">
                        <input id="id" name="id" class="mini-combobox" required="true" requiredErrorText="战略举措不能为空" style="width:98%;"  textField="zljcName"
                               valueField="id" emptyText="请选择..." url="${ctxPath}/strategicPlanning/core/zlghData/zljc/listText.do"
                               allowInput="false" showNullItem="true" nullItemText="请选择..." />
                    </td>
                </tr>
                <tr>
                    <td style="width: 10%">战略课题：<span style="color: #ff0000">*</span></td>
                    <td style="width: 40%">
                        <input id="ktName" name="ktName" required="true" requiredErrorText="战略课题不能为空" class="mini-textbox" style="width:98%;" />
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
<%--<div style="text-align:center;padding:10px;">--%>
    <%--<a class="mini-button" onclick="onOk" style="width:60px;margin-right:20px;">确定</a>--%>
    <%--<a class="mini-button" onclick="onCancel" style="width:60px;">取消</a>--%>
<%--</div>--%>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var isManager = whetherIsProjectManager(${currentUserRoles});
    var currentUserName = "${currentUserName}";
    var currentTime = "${currentTime}";
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";
    var currentUserNo = "${currentUserNo}";
    var currentUserId = "${currentUserId}";
    var isZLGHZY =${isZLGHZY};
    mini.parse();
    var form = new mini.Form("form");
    var editState = "added";
    // 保存数据
    function SaveData() {
        let param = [];
        var o = form.getData();
        o._state = editState
        form.validate();
        if (form.isValid() == false) return;
        param.push(o);
        var json = mini.encode([o]);
        $.ajax({
            url: jsUseCtxPath+ "/strategicPlanning/core/zlghData/zlkt/batchOptions.do",
            type: 'post',
            contentType:"application/json;charset=UTF-8",
            data: json,
            cache: false,
            success: function (text) {
                CloseWindow("ok");
            },
            error: function (jqXHR, textStatus, errorThrown) {
                alert(jqXHR.responseText);
                CloseWindow();
            }
        });
    }
    ////////////////////
    //标准方法接口定义
    function SetData(data) {
        editState = data.state;
        if (data.state == "modified") {
            //跨页面传递的数据对象，克隆后才可以安全使用
            data = mini.clone(data.row);
            form.setData(data);
            form.setChanged(false);
            // $.ajax({
            //     url: jsUseCtxPath+ "/strategicPlanning/core/zlghData/zljc/get.do?id=" + data.row.id,
            //     cache: false,
            //     success: function (text) {
            //         var o = mini.decode(text);
            //         form.setData(o);
            //         form.setChanged(false);
            //
            //         // onDeptChanged();
            //     }
            // });
        }
    }

    // 父页面获取数据
    function GetData() {
        var o = form.getData();
        return o;
    }

    // 关闭弹窗
    function CloseWindow(action) {
        if (action == "close" && form.isChanged()) {
            if (confirm("数据被修改了，是否先保存？")) {
                return false;
            }
        }
        if (window.CloseOwnerWindow) return window.CloseOwnerWindow(action);
        else window.close();
    }

    function onOk(e) {
        SaveData();
    }

    function onCancel(e) {
        CloseWindow("cancel");
    }
</script>
</body>
</html>
