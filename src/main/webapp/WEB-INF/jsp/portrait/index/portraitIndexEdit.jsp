<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
	<title>新增指标信息</title>
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
					指标信息
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
						所属年度：
					</td>
					<td align="left">
						<input id="year" name="year" class="mini-textbox" required="true" only_read="false" allowinput="true" style="width:100%;height:34px"/>
					</td>
					<td align="center" style="white-space: nowrap;">
						所属部门：
					</td>
					<td align="left">
						<input id="deptId" name="deptId" class="mini-dep rxc" plugins="mini-dep"
							   style="width:98%;height:34px" required="true"
							   allowinput="false" textname="deptName" length="200" maxlength="200" minlen="0" single="true" initlogindep="false"/>
					</td>
				</tr>
				<tr>
					<td align="center" colspan="1" rowspan="1" style="white-space: nowrap;">
						频次：
					</td>
					<td align="center" colspan="1" rowspan="1">
						<input id="indexRate" name="indexRate" class="mini-combobox rxc" plugins="mini-combobox"
							   style="width:100%;height:34px"  label="频次："
							   length="50" required="true"
							   only_read="false" allowinput="false" mwidth="100"
							   wunit="%" mheight="34" hunit="px"   shownullitem="true" multiSelect="false"
							   textField="text" valueField="key_" emptyText="请选择..."
							   url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=indexRate"
							   nullitemtext="请选择..." emptytext="请选择..."/>
					</td>
					<td align="center" style="white-space: nowrap;">
						指标名称：
					</td>
					<td align="left">
						<input name="indexName" class="mini-textbox rxc" plugins="mini-textbox" vtype="length:50"  onvalidation=""
							   datatype="varchar" length="50" decimal="0" minnum="" maxnum="" validrule="" from="forminput" required="true" only_read="false" allowinput="true" value="" format="" emptytext="" sequence="" scripts="" mwidth="100" wunit="%" mheight="34" hunit="px" style="width:100%;height:34px"/>
					</td>
				</tr>
				<tr>
					<td align="center" style="white-space: nowrap;">
						指标目标值：
					</td>
					<td>
					<input name="indexValue" class="mini-textbox rxc" plugins="mini-textbox" vtype="length:50"  onvalidation=""
						   datatype="varchar" length="50" decimal="0" minnum="" maxnum="" validrule="" from="forminput" required="true" only_read="false" allowinput="true" value="" format="" emptytext="" sequence="" scripts="" mwidth="100" wunit="%" mheight="34" hunit="px" style="width:100%;height:34px"/>
					</td>
					<td align="center" style="white-space: nowrap;">
						分值：
					</td>
					<td align="left">
						<input name="indexScore" class="mini-textbox rxc" plugins="mini-textbox" vtype="length:50"  onvalidation=""
							   datatype="varchar" length="50" decimal="0" minnum="" maxnum="" validrule="" from="forminput" required="true" only_read="false" allowinput="true" value="" format="" emptytext="" sequence="" scripts="" mwidth="100" wunit="%" mheight="34" hunit="px" style="width:100%;height:34px"/>
					</td>
				</tr>
				<tr>
					<td align="center" colspan="1" rowspan="1" style="white-space: nowrap;">
						得分标准：
					</td>
					<td align="left" colspan="3">
						<textarea class="mini-textarea" name="scoreRule" emptyText="请输入得分标准" style="width:100%;height:100px"  required="true"></textarea>
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
	var indexForm = new mini.Form("#indexForm");
	$(function () {
		if(action!='add'){
			indexForm.setData(applyObj);
		}
		if (action == 'view') {
			indexForm.setEnabled(false);
			$('#save').hide();
		}
		if(action=='add') {
		    mini.get("year").setValue(new Date().getFullYear());
		}
	})
	function saveData() {
		indexForm.validate();
		if (!indexForm.isValid()) {
			return;
		}
		var formData = indexForm.getData();
		var config = {
			url: jsUseCtxPath+"/portrait/index/save.do",
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
