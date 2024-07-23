<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
	<title>新增标准信息</title>
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
		<form id="standardForm" method="post">
			<input id="id" name="id" class="mini-hidden"/>
			<table cellspacing="1" cellpadding="0" class="table-detail column-four grey">
				<caption>
					标准信息
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
						标准编号：
					</td>
					<td align="left">
						<input name="standardCode" class="mini-textbox rxc" plugins="mini-textbox" vtype="length:50"  onvalidation=""
							   datatype="varchar" length="50" decimal="0" minnum="" maxnum="" validrule="" from="forminput" required="true" only_read="false" allowinput="true" value="" format="" emptytext="" sequence="" scripts="" mwidth="100" wunit="%" mheight="34" hunit="px" style="width:100%;height:34px"/>
					</td>
				</tr>
				<tr>
					<td align="center" style="white-space: nowrap;">
						标准名称：
					</td>
					<td align="left">
						<input name="standardName" class="mini-textbox rxc" plugins="mini-textbox" vtype="length:50"  onvalidation=""
							   datatype="varchar" length="50" decimal="0" minnum="" maxnum="" validrule="" from="forminput" required="true" only_read="false" allowinput="true" value="" format="" emptytext="" sequence="" scripts="" mwidth="100" wunit="%" mheight="34" hunit="px" style="width:100%;height:34px"/>
					</td>
					<td align="center" style="white-space: nowrap;">
						标准类别：
					</td>
					<td align="left">
						<input name="standardType" class="mini-textbox rxc" plugins="mini-textbox" vtype="length:50"  onvalidation=""
							   datatype="varchar" length="50" decimal="0" minnum="" maxnum="" validrule="" from="forminput" required="true" only_read="false" allowinput="true" value="" format="" emptytext="" sequence="" scripts="" mwidth="100" wunit="%" mheight="34" hunit="px" style="width:100%;height:34px"/>
					</td>
				</tr>
				<tr>
					<td align="center" style="white-space: nowrap;">
						发布日期：
					</td>
					<td align="left">
						<input name="publishDate" class="mini-datepicker"  style="width:100%;height:34px"/>
					</td>
					<td align="center" style="white-space: nowrap;">
						起草人：
					</td>
					<td align="left">
						<input name="author" class="mini-textbox rxc" plugins="mini-textbox" vtype="length:50"  onvalidation=""
							   datatype="varchar" length="50" decimal="0" minnum="" maxnum="" validrule="" from="forminput" required="true" only_read="false" allowinput="true" value="" format="" emptytext="" sequence="" scripts="" mwidth="100" wunit="%" mheight="34" hunit="px" style="width:100%;height:34px"/>
					</td>
				</tr>
				<tr>
					<td align="center" colspan="1" rowspan="1" style="white-space: nowrap;">
						排名：
					</td>
					<td align="center" colspan="1" rowspan="1">
						<input id="standardSort" name="standardSort" class="mini-combobox rxc" plugins="mini-combobox"
							   style="width:100%;height:34px"  label="通报编号："
							   length="50"
							   only_read="false" required="true" allowinput="false" mwidth="100"
							   wunit="%" mheight="34" hunit="px"   shownullitem="true" multiSelect="false"
							   textField="text" valueField="key_" emptyText="请选择..." onvaluechanged="onTypeChanged"
							   url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=standardRank"
							   nullitemtext="请选择..." emptytext="请选择..."/>
					</td>
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
	var standardForm = new mini.Form("#standardForm");
	var sortCombo = mini.get("standardSort");
	var score = 0;
	$(function () {
		if(action!='add'){
			standardForm.setData(applyObj);
			mini.get("recUserSelectId").setEnabled(false);
		}
		if (action == 'view') {
			standardForm.setEnabled(false);
			$('#save').hide();
		}
	})
	function saveData() {
		standardForm.validate();
		if (!standardForm.isValid()) {
			return;
		}
		var formData = standardForm.getData();
		var config = {
			url: jsUseCtxPath+"/portrait/standard/save.do",
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
	function onTypeChanged(e) {
		score = parseFloat(e.selected.value);
		mini.get('score').setValue(score.toFixed(2));
	}
</script>
</body>
</html>
