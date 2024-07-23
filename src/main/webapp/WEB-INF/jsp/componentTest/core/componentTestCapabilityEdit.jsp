<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html >
<html>
<head>
    <title>测试能力表单</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div id="detailToolBar" class="topToolBar">
    <div>
        <a id="saveComponentTestCapability" class="mini-button" onclick="saveComponentTestCapability()">保存</a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin:0 auto; width: 100%;height:98%;padding: 0">
        <form id="componentTestCapabilityForm" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <input id="pid" name="pid" class="mini-hidden"/>
            <input id="path" name="path" class="mini-hidden"/>
            <table class="table-detail column-four table-align" cellspacing="1" cellpadding="0">
                <caption>测试能力基本信息</caption>
                <tr>
                    <td>
                        名　　称<span class="star">*</span>
                    </td>
                    <td colspan="3">
                        <input id="capability" name="capability" class="mini-textbox" required="true" emptyText="请输入名称" style="width:100%"/>
                    </td>
                </tr>
                <tr>
                    <td>
                        类　　型<span class="star">*</span>
                    </td>
                    <td colspan="3">
                        <input id="type" name="type" class="mini-combobox" style="width:100%;"
                               textField="value" valueField="key" emptyText="请选择..."
                               required="true" allowInput="false" showNullItem="true" nullItemText="请选择..."
                               data="[{key:'测试能力',value:'测试能力',enabled: false},
                               {key:'专业',value:'专业'},
                               {key:'测试层级',value:'测试层级'},
                               {key:'测试对象',value:'测试对象'},
                               {key:'测试项目',value:'测试项目'}]"/>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var componentTestCapabilityForm = new mini.Form("#componentTestCapabilityForm");
    var jsUseCtxPath = "${ctxPath}";
    var pid = "${pid}";
    var path = "${path}";
    var componentTestCapabilityId = "${componentTestCapabilityId}";
    var action = "${action}";
    //..
    $(function () {
        if (action == 'edit') {
            if (componentTestCapabilityId) {
                var url = jsUseCtxPath + "/componentTest/core/capability/getComponentTestCapabilityById.do?" +
                    "componentTestCapabilityId=" + componentTestCapabilityId;
                $.ajax({
                    url: url,
                    method: 'get',
                    async: false,
                    success: function (json) {
                        componentTestCapabilityForm.setData(json);
                        if (json.capability == '测试能力') {
                            componentTestCapabilityForm.setEnabled(false);
                            mini.get("saveComponentTestCapability").setVisible(false);
                        }
                    }
                });
            }
        } else {
            mini.get("pid").setValue(pid);
            mini.get("path").setValue(path);
        }
    });
    //..
    function saveComponentTestCapability() {
        var capability = mini.get("capability").getValue();
        if (!capability) {
            mini.alert("名称不能为空");
            return
        }
        var type = mini.get("type").getValue();
        if (!type) {
            mini.alert("类型不能为空");
            return
        }
        var formData = _GetFormJsonMini("componentTestCapabilityForm");
        $.ajax({
            url: jsUseCtxPath + '/componentTest/core/capability/saveComponentTestCapability.do',
            type: 'post',
            async: false,
            data: mini.encode(formData),
            contentType: 'application/json',
            success: function (data) {
                if (data) {
                    var message = "";
                    if (data.success) {
                        message = "数据保存成功";
                    } else {
                        message = "数据保存失败" + data.message;
                    }
                    mini.alert(message, "提示信息", function () {
                        CloseWindow();
                    });
                }
            }
        });
    }
</script>
</body>
</html>
