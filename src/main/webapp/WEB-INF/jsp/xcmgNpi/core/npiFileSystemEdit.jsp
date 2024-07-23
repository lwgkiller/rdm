
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<title>编辑节点</title>
	<%@include file="/commons/edit.jsp"%>
	<link href="${ctxPath}/scripts/mini/miniui/themes/icons.css" rel="stylesheet" type="text/css" />
</head>

<body>
<div class="topToolBar">
	<div>
		<a class="mini-button" onclick="closeTreeEditWindow()">确定</a>
	</div>
</div>
<div class="mini-fit">
	<div class="form-container" id="editFormId" style="margin: 0 auto">
			<table class="table-detail" cellspacing="1" cellpadding="0">
				<tr>
					<td style="width: 10%">类别名称（中文）：<span style="color: #ff0000">*</span></td>
					<td style="width: 40%">
						<input name="systemName" class="mini-textbox" style="width:98%;"/>
					</td>
					<td style="width: 10%">类别名称（英文）：</td>
					<td style="width: 40%">
						<input name="systemNameEn"  class="mini-textbox" style="width:98%;" />
					</td>
				</tr>
				<tr>
					<td style="width: 10%">序号：<span style="color: #ff0000">*</span></td>
					<td style="width: 40%">
						<input name="sn" class="mini-textbox" style="width:98%;" />
					</td>
				</tr>
			</table>
	</div>
</div>


<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var form = new mini.Form("editFormId");
	var nodeInfo=null;

    function SetData(params) {
        nodeInfo=params.node;
        form.setData(nodeInfo);
		if(params.action=='detail') {
            form.setEnabled(false);
		}
    }

	function closeTreeEditWindow() {
        var options = form.getData();
        if(!$.trim(options.systemName)) {
            mini.alert("请填写类别名称（中文）");
            return;
		}
        if(!$.trim(options.sn)) {
            mini.alert("请填写序号");
            return;
        }
        window.Owner.onUpdateNode(options);
        CloseWindow();
    }

</script>
</body>
</html>