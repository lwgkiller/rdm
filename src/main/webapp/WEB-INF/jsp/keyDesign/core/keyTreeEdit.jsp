
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
					<td style="width: 10%">节点名称：<span style="color: #ff0000">*</span></td>
					<td style="width: 40%">
						<input name="codeName" class="mini-textbox" style="width:98%;"/>
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
        if(!$.trim(options.codeName)) {
            mini.alert('节点名称不能为空！');
            return;
		}
        window.Owner.onUpdateNode(options);
        CloseWindow();
    }


</script>
</body>
</html>