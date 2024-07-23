<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html >
<html>
<head>
    <title>仿真对象表单</title>
    <%@include file="/commons/edit.jsp"%>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css" />
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div id="detailToolBar" class="topToolBar">
    <div>
        <a id="saveFzdx" class="mini-button" onclick="saveFzdx()">保存</a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin:0 auto; width: 100%;height:98%;padding: 0">
        <form id="fzdxForm" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <input id="pid" name="pid" class="mini-hidden"/>
            <input id="path" name="path" class="mini-hidden"/>
            <table class="table-detail column-four table-align" cellspacing="1" cellpadding="0">
                <caption>仿真对象基本信息</caption>
                <tr>
                    <td>
                        名　　称<span class="star">*</span>
                    </td>
                    <td colspan="3">
                        <input id="fzdx" name="fzdx" class="mini-textbox" required="true" emptyText="请输入名称" style="width:100%"/>
                    </td>
                </tr>
                <tr>
                    <td>
                        类　　型<span class="star">*</span>
                    </td>
                    <td  colspan="3">
                        <input id="type" name="type" class="mini-combobox" style="width:100%;"
                               textField="value" valueField="key" emptyText="请选择..."
                               required="true" allowInput="false" showNullItem="true" nullItemText="请选择..."
                               data="[{key:'zj',value:'整机',enabled: false},{key:'xt',value:'系统'},{key:'bj',value:'部件'},{key:'lj',value:'零件'}]"
                        />
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>


<script type="text/javascript">
    mini.parse();
    var fzdxForm = new mini.Form("#fzdxForm");
    var jsUseCtxPath="${ctxPath}";
    var pid="${pid}";
    var path="${path}";
    var fzdxId="${fzdxId}";
    var action="${action}";

    $(function () {
        if (action == 'edit') {
            if (fzdxId) {
                var url = jsUseCtxPath + "/fzsj/core/fzdx/getFzdxById.do?fzdxId="+fzdxId;
                $.ajax({
                    url:url,
                    method:'get',
                    async: false,
                    success:function (json) {
                        fzdxForm.setData(json);
                        if(json.fzdx == '整机') {
                            fzdxForm.setEnabled(false);
                            mini.get("saveFzdx").setVisible(false);
                        }
                    }
                });
            }
        } else {
            mini.get("pid").setValue(pid);
            mini.get("path").setValue(path);
        }
    });

    function saveFzdx() {
        var fzdx = mini.get("fzdx").getValue();
        if (!fzdx) {
            mini.alert("名称不能为空");
            return
        }
        var type = mini.get("type").getValue();
        if (!type) {
            mini.alert("类型不能为空");
            return
        }
        var formData = _GetFormJsonMini("fzdxForm");
        $.ajax({
            url: jsUseCtxPath + '/fzsj/core/fzdx/saveFzdx.do',
            type: 'post',
            async: false,
            data:mini.encode(formData),
            contentType: 'application/json',
            success: function (data) {
                if (data) {
                    var message="";
                    if(data.success) {
                        message="数据保存成功";
                    } else {
                        message="数据保存失败"+data.message;
                    }
                    mini.alert(message,"提示信息",function () {
                        CloseWindow();
                    });
                }
            }
        });
    }
</script>
</body>
</html>
