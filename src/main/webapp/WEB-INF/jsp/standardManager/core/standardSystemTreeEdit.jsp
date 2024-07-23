
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
		<a class="mini-button" onclick="closeTreeEditWindow()"><spring:message code="page.standardSystemTreeEdit.name" /></a>
	</div>
</div>
<div class="mini-fit">
	<div class="form-container" id="editFormId" style="margin: 0 auto">
			<table class="table-detail" cellspacing="1" cellpadding="0">
				<tr>
					<td style="width: 10%"><spring:message code="page.standardSystemTreeEdit.name1" />：<span style="color: #ff0000">*</span></td>
					<td style="width: 40%">
						<input name="systemName" class="mini-textbox" style="width:98%;"/>
					</td>
					<td style="width: 10%"><spring:message code="page.standardSystemTreeEdit.name2" />：</td>
					<td style="width: 40%">
						<input name="systemCode"  class="mini-textbox" style="width:98%;" />
					</td>
				</tr>
				<tr>
					<td style="width: 10%"><spring:message code="page.standardSystemTreeEdit.name3" />：<span id="needSystemNumber" style="display:none;color: #ff0000">*</span></td>
					<td style="width: 40%">
						<input name="systemNumber" class="mini-textbox" style="width:98%;" />
					</td>
				</tr>
				<tr>
					<td style="width: 10%"><spring:message code="page.standardSystemTreeEdit.name4" />：
						<image src="${ctxPath}/styles/images/warn.png" style="margin-right:5px;vertical-align: middle;height: 15px" title="<spring:message code="page.standardSystemTreeEdit.name5" />"/>
					</td>
					<td style="width: 95%" colspan="3">
						<input id="visibleGroupIds" name="visibleGroupIds" allowInput="false" class="mini-textboxlist" style="width: 80%; min-height:32px;" /> <a class="mini-button"   plain="true" onclick="selectCanGroups()">添加</a>
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
        //叶子节点需要填写体系编号
        if(!nodeInfo.children) {
            $("#needSystemNumber").show();
		}
		if(params.action=='detail') {
            form.setEnabled(false);
		}

		mini.get("visibleGroupIds").setText(nodeInfo.visibleGroupNames);
    }
	function closeTreeEditWindow() {
        var options = form.getData();
        if(!$.trim(options.systemName)) {
            mini.alert(standardSystemTreeEdit_name);
            return;
		}
		if(!nodeInfo.children && !$.trim(options.systemNumber)) {
            mini.alert(standardSystemTreeEdit_name1);
            return;
		}
        window.Owner.onUpdateNode(options);
        CloseWindow();
    }
    function selectCanGroups(){
        var visibleGroupIds=mini.get('visibleGroupIds');

        _GroupCanDlg({
            tenantId:'1',
            single:false,
            width:900,
            height:500,
            title:'用户组',
            callback:function(groups){
                var uIds=[];
                var uNames=[];
                for(var i=0;i<groups.length;i++){
                    uIds.push(groups[i].groupId);
                    uNames.push(groups[i].name);
                }
                if(visibleGroupIds.getValue()!=''){
                    uIds.unshift(visibleGroupIds.getValue().split(','));
                }
                if(visibleGroupIds.getText()!=''){
                    uNames.unshift(visibleGroupIds.getText().split(','));
                }
                visibleGroupIds.setValue(uIds.join(','));
                visibleGroupIds.setText(uNames.join(','));
            }
        });
    }

</script>
</body>
</html>