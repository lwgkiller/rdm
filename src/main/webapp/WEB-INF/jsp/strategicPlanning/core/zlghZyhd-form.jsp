<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>主要活动-新增或编辑</title>
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
            <input id="zyhdId" name="zyhdId" class="mini-hidden"/>
            <input id="_state" name="_state" class="mini-hidden"/>
            <table class="table-detail" cellspacing="0" cellpadding="0">
                <tr>
                    <td style="width: 10%">战略举措：<span style="color: #ff0000">*</span></td>
                    <td style="width: 40%" colspan="4">
                        <input id="id" name="id" class="mini-combobox" required="true" requiredErrorText="战略举措不能为空" style="width:98%;"  textField="zljcName"
                               valueField="id" emptyText="请选择..." url="${ctxPath}/strategicPlanning/core/zlghData/zljc/listText.do"
                               allowInput="false" showNullItem="true" nullItemText="请选择..." onvaluechanged="onHandlerSelectChange" />
                    </td>
                </tr>
                <tr>
                    <td style="width: 10%">战略课题：<span style="color: #ff0000">*</span></td>
                    <td style="width: 40%">
                        <%--<input id="ktName" name="ktName" required="true" requiredErrorText="战略课题不能为空" class="mini-textbox" style="width:98%;" />--%>
                        <input id="zlktId" name="zlktId" required="true" requiredErrorText="战略课题不能为空" class="mini-combobox" style="width:98%;"  textField="ktName"
                               valueField="id" emptyText="请选择..." allowInput="false" showNullItem="true" nullItemText="请选择..." />
                    </td>
                    <input id="initiatorId" name="initiatorId" class="mini-hidden"/>
                    <td style="width: 10%">牵头人：<span style="color: #ff0000">*</span></td>
                    <td style="width: 40%">
                        <input id="initiatorName" name="initiatorName" textname="initiatorName" required="true" requiredErrorText="牵头人不能为空" class="mini-user rxc" plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="牵头人" length="50" maxlength="50"  mainfield="no"  single="true" onvaluechanged="setInitiator()"/>
                        <%--<input id="initiatorName" name="initiatorName" required="true" requiredErrorText="牵头人不能为空" class="mini-textbox" style="width:98%;" />--%>
                        <%--<input id="zlktId" name="zlktId" class="mini-combobox" style="width:98%;"  textField="zljcName"--%>
                        <%--valueField="id" emptyText="请选择..." url="${ctxPath}/strategicPlanning/core/zlghData/zljc/listText.do"--%>
                        <%--allowInput="false" showNullItem="true" nullItemText="请选择..." />--%>
                    </td>
                </tr>
                <tr>
                    <td style="width: 10%">活动名称：<span style="color: #ff0000">*</span></td>
                    <td style="width: 40%">
                        <input id="moveName" name="moveName" required="true" requiredErrorText="活动名称不能为空" class="mini-textbox" style="width:98%;" />
                        <%--<input id="zlktId" name="zlktId" class="mini-combobox" style="width:98%;"  textField="zljcName"--%>
                        <%--valueField="id" emptyText="请选择..." url="${ctxPath}/strategicPlanning/core/zlghData/zljc/listText.do"--%>
                        <%--allowInput="false" showNullItem="true" nullItemText="请选择..." />--%>
                    </td>
                    <td style="width: 10%">总体目标：<span style="color: #ff0000">*</span></td>
                    <td style="width: 40%">
                        <input id="overallGoals" name="overallGoals" required="true" requiredErrorText="总体目标不能为空" class="mini-textbox" style="width:98%;" />
                        <%--<input id="zlktId" name="zlktId" class="mini-combobox" style="width:98%;"  textField="zljcName"--%>
                        <%--valueField="id" emptyText="请选择..." url="${ctxPath}/strategicPlanning/core/zlghData/zljc/listText.do"--%>
                        <%--allowInput="false" showNullItem="true" nullItemText="请选择..." />--%>
                    </td>
                </tr>
                <tr>
                    <input id="respDeptIds" name="respDeptIds" class="mini-hidden"/>
                    <td style="width: 10%">责任人：<span style="color: #ff0000">*</span></td>
                    <td colspan="4">
                        <input id="respUserNames" name="respUserNames" textname="respUserNames" required="true" requiredErrorText="责任人不能为空" class="mini-user rxc" plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="责任人" length="100" maxlength="100"  mainfield="no"  single="false" onvaluechanged="setRespUser()"/>
                        <%--<input id="respUserNames" name="respUserNames" required="true" requiredErrorText="责任人不能为空" class="mini-textbox" style="width:98%;" />--%>
                        <%--<input id="zlktId" name="zlktId" class="mini-combobox" style="width:98%;"  textField="zljcName"--%>
                        <%--valueField="id" emptyText="请选择..." url="${ctxPath}/strategicPlanning/core/zlghData/zljc/listText.do"--%>
                        <%--allowInput="false" showNullItem="true" nullItemText="请选择..." />--%>
                    </td>
                </tr>
                <tr>
                    <input id="respUserIds" name="respUserIds" class="mini-hidden"/>
                    <td style="width: 10%">责任部门：<span style="color: #ff0000">*</span></td>
                    <td style="width: 40%" colspan="4">
                        <input id="respDeptNames" name="respDeptNames" required="true" requiredErrorText="责任部门不能为空" class="mini-dep rxc" plugins="mini-dep" style="width:98%;height:34px" allowinput="false" textname="respDeptNames" length="500" maxlength="500" minlen="0" single="false" initlogindep="false"/>
                        <%--<input id="respDeptNames" name="respDeptNames" required="true" requiredErrorText="责任部门不能为空" class="mini-textbox" style="width:98%;" />--%>
                        <%--<input id="zlktId" name="zlktId" class="mini-combobox" style="width:98%;"  textField="zljcName"--%>
                        <%--valueField="id" emptyText="请选择..." url="${ctxPath}/strategicPlanning/core/zlghData/zljc/listText.do"--%>
                        <%--allowInput="false" showNullItem="true" nullItemText="请选择..." />--%>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
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
    var idSelector = mini.get("id");
    var zlktIdSelector = mini.get("zlktId");
    var editState = "added";
    var tempFormData = {};
    // 保存数据
    function SaveData() {
        form.setData(tempFormData, false);
        let param = [];
        var o = form.getData();
        o._state = editState;
        form.validate();
        if (form.isValid() == false) return;
        param.push(o);
        var json = mini.encode([o]);
        $.ajax({
            url: jsUseCtxPath+ "/strategicPlanning/core/zlghData/zyhd/batchOptions.do",
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
            // 加载当前战略举措下的所有战略课题
            onHandlerSelectChange(data.id);
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

    // 战略举措更新时更新战略课题
    function onHandlerSelectChange(zljcId) {
        if (zljcId) {
            if (zljcId.value) {
                zljcId = zljcId.value;
            }
        }
        // zlktIdSelector.setValue("");
        var url = jsUseCtxPath+ "/strategicPlanning/core/zlghData/zlkt/listText.do?zljcId=" + zljcId;
        zlktIdSelector.load(url);
        zlktIdSelector.select(1);
    }

    // 牵头人选项变化后触发
    function setInitiator(e) {
        let initiatorId = mini.get("initiatorName").getValue();
        tempFormData.initiatorId = initiatorId;
    }

    // 责任人选项变化后触发
    function setRespUser(e) {
        // 责任部门ids
        var respDeptIds;
        // 责任部门names
        var respDeptNames;
        let respUserIds = mini.get("respUserNames").getValue();
        let respDepts = mini.get("respDeptNames");
        if (respUserIds){
            let splits = respUserIds.split(",");
            for (let i = 0; i < splits.length; i++) {
                let userInfo = getUserInfoById(splits[i]);
                if (i === 0) {
                    respDeptIds = userInfo.mainDepId.toString();
                    respDeptNames = userInfo.mainDepName;
                } else {
                    respDeptIds = respDeptIds + "," +userInfo.mainDepId.toString();
                    respDeptNames = respDeptNames + "," + userInfo.mainDepName;
                }
            }
            respDepts.setValue(respDeptIds);
            respDepts.setText(respDeptNames);
        }
        tempFormData.respUserIds = respUserIds;
        tempFormData.respDeptIds = respDeptIds;
    }

</script>
</body>
</html>
