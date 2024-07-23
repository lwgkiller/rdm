<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
	<title>新增论文信息</title>
	<%@include file="/commons/edit.jsp" %>
	<link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
	<script src="${ctxPath}/scripts/kindeditor/kindeditor-all-min.js" type="text/javascript"></script>
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
		<form id="newsForm" method="post">
			<input id="id" name="id" class="mini-hidden"/>
			<input id="async" name="async" value="1" class="mini-hidden"/>
			<table cellspacing="1" cellpadding="0" class="table-detail column-four grey">
				<caption>
					论文信息
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
						标题：
					</td>
					<td align="left" colspan="3">
						<input name="title" class="mini-textbox rxc" plugins="mini-textbox" vtype="length:50"  onvalidation=""
							   datatype="varchar" length="200" decimal="0" minnum="" maxnum="" validrule="" from="forminput" required="true" only_read="false" allowinput="true" value="" format="" emptytext="" sequence="" scripts="" mwidth="100" wunit="%" mheight="34" hunit="px" style="width:100%;height:34px"/>
					</td>
				</tr>
				<tr>
					<td align="center" colspan="1" rowspan="1" style="white-space: nowrap;">
						论文方向：
					</td>
					<td align="left">
						<input name="paperType" class="mini-textbox rxc" plugins="mini-textbox" vtype="length:50"  onvalidation=""
							   datatype="varchar" length="50" decimal="0" minnum="" maxnum="" validrule="" from="forminput" required="true" only_read="false" allowinput="true" value="" format="" emptytext="" sequence="" scripts="" mwidth="100" wunit="%" mheight="34" hunit="px" style="width:100%;height:34px"/>
					</td>
					<td align="center" style="white-space: nowrap;">
						发布日期：
					</td>
					<td align="left">
						<input name="publishDate" class="mini-datepicker"  style="width:100%;height:34px"/>
					</td>
				</tr>
				<tr>
					<td align="center" colspan="1" rowspan="1" style="white-space: nowrap;">
						作者：
					</td>
					<td align="left" colspan="3">
						<input name="author" class="mini-textbox rxc" plugins="mini-textbox" vtype="length:50"  onvalidation=""
							   datatype="varchar" length="50" decimal="0" minnum="" maxnum="" validrule="" from="forminput" required="true" only_read="false" allowinput="true" value="" format="" emptytext="" sequence="" scripts="" mwidth="100" wunit="%" mheight="34" hunit="px" style="width:100%;height:34px"/>
					</td>
				</tr>
				<tr>
					<td align="center" style="white-space: nowrap;">内容：</td>
					<td colspan="3">
						<input id="contentId" name="content" class="mini-hidden"/>
						<div name="editor" id="editor" rows="10" cols="95" style="height: 450px;width: 100%">
						</div>
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
	var newsForm = new mini.Form("#newsForm");
	var editor = KindEditor.create('#editor');
	function setContent(_text) {
		editor.html(_text);
	}
	function getContent() {
		return editor.html();
	}
	function getPlainTxt() {
		return editor.text();
	}
	function setDisabled() {
		editor.readonly();
	}
	function setEnabled() {
		editor.readonly(false);
	}
	$(function () {
		if(action!='add'){
			newsForm.setData(applyObj);
			setContent(applyObj.content);
		}
		if (action == 'view') {
			newsForm.setEnabled(false);
			$('#save').hide();
		}
	})
	function saveData() {
		newsForm.validate();
		if (!newsForm.isValid()) {
			return;
		}
		var formData = newsForm.getData();
		formData.content=getContent();
		var config = {
			url: jsUseCtxPath+"/info/paper/save.do",
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
