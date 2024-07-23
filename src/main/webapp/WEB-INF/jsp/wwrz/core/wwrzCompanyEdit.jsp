<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>认证公司信息维护</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/wwrz/wwrzCompanyEdit.js?version=${static_res_version}"
            type="text/javascript"></script>
</head>
<body>
<div id="ToolBar" class="topToolBar" style="display: block">
    <div>
        <a id="save" class="mini-button" img="${ctxPath}/scripts/mini/miniui/res/images/add.png"
           onclick="saveData()">保存</a>
        <a id="closeWindow" class="mini-button" img="${ctxPath}/scripts/mini/miniui/res/images/system_close.gif"
           onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin:0 auto; width: 100%;">
        <form id="companyForm" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <table cellspacing="1" cellpadding="0" class="table-detail column-four grey">
                <caption>
                    认证公司信息维护
                </caption>
                <tbody>
                <tr class="firstRow displayTr">
                    <td align="center"></td>
                    <td align="left"></td>
                    <td align="center"></td>
                    <td align="left"></td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">认证公司代号<span style="color: #ff0000">*</span>：</td>
                    <td align="center" rowspan="1">
                        <input   name="companyCode" required class="mini-textbox" style="width:100%;"/>
                    </td>
                    <td align="center" style="white-space: nowrap;">纳税人识别号：</td>
                    <td align="center" rowspan="1">
                        <input   name="taxCode" class="mini-textbox" style="width:100%;"/>
                    </td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        公司名称<span style="color: #ff0000">*</span>：
                    </td>
                    <td align="center" rowspan="1" colspan="3">
                        <input   name="companyName" required class="mini-textbox" style="width:100%;"/>
                    </td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        地址：
                    </td>
                    <td align="center" rowspan="1">
                        <input   name="address" class="mini-textbox" style="width:100%;"/>
                    </td>
                    <td align="center" style="white-space: nowrap;">电话：</td>
                    <td align="center" rowspan="1">
                        <input   name="telphone" class="mini-textbox" style="width:100%;"/>
                    </td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        开户行：
                    </td>
                    <td align="center" rowspan="1">
                        <input   name="bank" class="mini-textbox" style="width:100%;"/>
                    </td>
                    <td align="center" style="white-space: nowrap;">账号：</td>
                    <td align="center" rowspan="1">
                        <input   name="account" class="mini-textbox" style="width:100%;"/>
                    </td>
                </tr>
                </tbody>
            </table>
        </form>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var applyObj = ${applyObj};
    var action = '${action}';
    var companyForm = new mini.Form("#companyForm");
    $(function () {
        companyForm.setData(applyObj);
    })

    function saveData() {
        companyForm.validate();
        if (!companyForm.isValid()) {
            return;
        }
        var formData = companyForm.getData();
        var config = {
            url: jsUseCtxPath + "/wwrz/core/company/save.do",
            method: 'POST',
            data: formData,
            success: function (result) {
                //如果存在自定义的函数，则回调
                var result = mini.decode(result);
                if (result.success) {
                    CloseWindow('ok');
                } else {
                }
                ;
            }
        }
        _SubmitJson(config);
    }
</script>
</body>
</html>
