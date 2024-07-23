<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
	<title>新增招标规划</title>
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
		<form id="bidPlanForm" method="post">
			<input id="id" name="id" class="mini-hidden"/>
			<table cellspacing="1" cellpadding="0" class="table-detail column-four grey">
				<caption>
					招标规划信息
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
						姓名：
					</td>
					<td align="left">
						<input id="recUserSelectId" name="userId" textname="userName" class="mini-user rxc" plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="姓名"
							   mainfield="no"  single="false" />
					</td>
					<td align="center" style="white-space: nowrap;">
						项目名称：
					</td>
					<td align="left">
						<input name="projectName" class="mini-textbox rxc" plugins="mini-textbox" vtype="length:50"  onvalidation=""
							   datatype="varchar" length="50" decimal="0" minnum="" maxnum="" validrule="" from="forminput" required="true" only_read="false" allowinput="true" value="" format="" emptytext="" sequence="" scripts="" mwidth="100" wunit="%" mheight="34" hunit="px" style="width:100%;height:34px"/>
					</td>
				</tr>
				<tr>
					<td align="center" colspan="1" rowspan="1" style="white-space: nowrap;">
						类别：
					</td>
					<td align="center" colspan="1" rowspan="1">
						<input id="projectType" name="projectType" class="mini-combobox rxc" plugins="mini-combobox"
							   style="width:100%;height:34px"  label="类别："
							   length="50"
							   only_read="false" required="true" allowinput="false" mwidth="100" onvaluechanged="onValueChanged"
							   wunit="%" mheight="34" hunit="px"   shownullitem="true" multiSelect="false"
							   textField="text" valueField="key_" emptyText="请选择..."
							   url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=bidPlanType"
							   nullitemtext="请选择..." emptytext="请选择..."
						/>
					</td>
					<td align="center" colspan="1" rowspan="1" style="white-space: nowrap;">
						角色：
					</td>
					<td align="center" colspan="1" rowspan="1">
						<input id="projectRole" name="projectRole" class="mini-combobox rxc" plugins="mini-combobox"
							   style="width:100%;height:34px"  label="类别："
							   length="50"
							   only_read="false" required="true" allowinput="false" mwidth="100"
							   wunit="%" mheight="34" hunit="px"   shownullitem="true" multiSelect="false"
							   textField="text" valueField="key_" emptyText="请选择..." onvaluechanged="onValueChanged"
							   url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=bidPlanRole"
							   nullitemtext="请选择..." emptytext="请选择..."/>
					</td>
					</td>
				</tr>
				<tr>
					<td align="center" colspan="1" rowspan="1" style="white-space: nowrap;">
						得分：
					</td>
					<td align="center" colspan="1" rowspan="1">
						<input id="score"  name="score" class="mini-textbox rxc" readonly />
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
	var bidPlanForm = new mini.Form("#bidPlanForm");
	var roleCombo = mini.get("projectRole");
	var typeCombo = mini.get("projectType");
	$(function () {
		if(action!='add'){
			bidPlanForm.setData(applyObj);
			mini.get("recUserSelectId").setEnabled(false);
		}
		if (action == 'view') {
			bidPlanForm.setEnabled(false);
			$('#save').hide();
		}
	})
	function saveData() {
		bidPlanForm.validate();
		if (!bidPlanForm.isValid()) {
			return;
		}
		var formData = bidPlanForm.getData();
		var config = {
			url: jsUseCtxPath+"/portrait/bidPlan/save.do",
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
	function onValueChanged(e) {
		var projectType = typeCombo.getValue();
		var projectRole = roleCombo.getValue();
		var score = 0;
		if(projectType=="zbl"){
			if(projectRole=='fzr'){
				score = 3;
			}else if(projectRole=='zyry'){
				score = 1
			}
		}else if(projectType=="ghl"){
			if(projectRole=='fzr'){
				score = 5;
			}else if(projectRole=='zyry'){
				score = 3;
			}
		}
		mini.get('score').setValue(score);
	}
</script>
</body>
</html>
