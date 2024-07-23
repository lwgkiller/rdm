<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
	<title>授权配置</title>
	<%@include file="/commons/edit.jsp" %>
	<link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
</head>
<body>
<div id="ToolBar" class="topToolBar" style="display: block">
	<div>
		<a id="save" class="mini-button" img="${ctxPath}/scripts/mini/miniui/res/images/add.png" onclick="saveData()">保存</a>
		<a id="closeWindow" class="mini-button" img="${ctxPath}/scripts/mini/miniui/res/images/system_close.gif" onclick="CloseWindow()">关闭</a>
	</div>
</div>
<div class="mini-fit">
	<div class="form-container" style="margin:0 auto; width: 100%;">
		<form id="indexForm" method="post">
			<input id="id" name="id" class="mini-hidden"/>
			<table cellspacing="1" cellpadding="0" class="table-detail column-four grey">
				<caption>
					情报URL维护
				</caption>
				<tbody>
				<tr class="firstRow displayTr">
					<td align="center"></td>
					<td align="left"></td>
					<td align="center"></td>
					<td align="left"></td>
				</tr>
				<tr>
					<td align="center" style="white-space: nowrap;">
						业务方向：
					</td>
					<td align="left">
						<input  name="busTypeId" class="mini-combobox rxc" plugins="mini-combobox"
							   style="width:100%;height:34px"  label="业务方向："
							   length="50"
							   only_read="false" required="false" allowinput="false" mwidth="100"
							   wunit="%" mheight="34" hunit="px"   shownullitem="true" multiSelect="false"
							   textField="text" valueField="value" emptyText="请选择..."
							   url="${ctxPath}/info/busTypeConfig/getDicInfoType.do"
							   nullitemtext="请选择..." emptytext="请选择..."/>
					</td>
					<td align="center" colspan="1" rowspan="1" style="white-space: nowrap;">
						情报分类：
					</td>
					<td align="center" colspan="1" rowspan="1">
						<input id="infoTypeId" name="infoTypeId" class="mini-combobox rxc" plugins="mini-combobox"
							   style="width:100%;height:34px"  label="情报分类："
							   length="50"
							   only_read="false" required="false" allowinput="false" mwidth="100"
							   wunit="%" mheight="34" hunit="px"   shownullitem="true" multiSelect="false"
							   textField="text" valueField="value" emptyText="请选择..."
							   url="${ctxPath}/info/type/getDicInfoType.do"
							   nullitemtext="请选择..." emptytext="请选择..."/>
					</td>
				</tr>
				<tr>
					<td align="center" style="white-space: nowrap;">
						授权部门：
					</td>
					<td align="left" colspan="3">
						<input id="authDeptIds" name="authDeptIds" class="mini-dep rxc" plugins="mini-dep"
							   style="width:98%;height:34px"
							   allowinput="false" textname="authDeptName" single="false" initlogindep="false"/>
					</td>
				</tr>
				<tr>
					<td align="center" style="white-space: nowrap;">
						清理周期：
					</td>
					<td align="left"  colspan="3">
					<input name="deleteMonthNum" class="mini-textbox rxc" plugins="mini-textbox" vtype="length:50"  onvalidation=""
						   datatype="varchar" length="200" decimal="0" minnum="" maxnum="" validrule="" from="forminput" required="false"
						   only_read="false" allowinput="true" value="" format="" emptytext="" sequence="" scripts="" mwidth="100" wunit="%" mheight="34" hunit="px" style="width:100%;height:34px"/>
					</td>
				</tr>
			</table>
		</form>
	</div>
</div>
<script type="text/javascript">
	mini.parse();
	var jsUseCtxPath = "${ctxPath}";
	var applyObj = ${applyObj};
	var action = '${action}';
	var indexForm = new mini.Form("#indexForm");
	$(function () {
		if(action!='add'){
			indexForm.setData(applyObj);
		}
		if (action == 'view') {
			indexForm.setEnabled(false);
			$('#save').hide();
		}
	})
	function saveData() {
		indexForm.validate();
		if (!indexForm.isValid()) {
			return;
		}
		var formData = indexForm.getData();
		var config = {
			url: jsUseCtxPath+"/info/auth/save.do",
			method: 'POST',
			data: formData,
			success: function (result) {
				//如果存在自定义的函数，则回调
				var result=mini.decode(result);
				if(result.success){
					CloseWindow("ok");
				}else{
				};
			}
		}
		_SubmitJson(config);
	}
</script>
</body>
</html>
