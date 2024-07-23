<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
	<title>新增荣誉奖项</title>
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
		<form id="honorForm" method="post">
			<input id="id" name="id" class="mini-hidden"/>
			<table cellspacing="1" cellpadding="0" class="table-detail column-four grey">
				<caption>
					荣誉奖项
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
						获奖单位（团队/个人）<span style="color: red">*</span>：
					</td>
					<td align="left" colspan="1">
						<input name="teamWork" id="teamWork"  class="mini-textbox rxc"    required="true" style="width:100%;height:34px"/>
					</td>
					<td align="center" style="white-space: nowrap;">
						人员身份证号（个人奖）：
					</td>
					<td align="left" colspan="1">
						<input name="certNo" id="certNo"  class="mini-textbox rxc"   style="width:100%;height:34px"/>
					</td>
				</tr>
				<tr>
					<td align="center" style="white-space: nowrap;">
						奖项类型<span style="color: red">*</span>：
					</td>
					<td align="left" colspan="1">
						<input id="rewardType" name="rewardType" class="mini-combobox rxc" plugins="mini-combobox"
							   style="width:100%;height:34px"  label="奖项类型："
							   length="50"
							   only_read="false" required="true" allowinput="false" mwidth="100"
							   wunit="%" mheight="34" hunit="px"   shownullitem="true" multiSelect="false"
							   textField="text" valueField="key_" emptyText="请选择..."
							   url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=rewardType"
							   nullitemtext="请选择..." emptytext="请选择...">
					</td>
					<td align="center" style="white-space: nowrap;">
						奖项名称<span style="color: red">*</span>：
					</td>
					<td align="left" colspan="1">
						<input name="rewardName" required  class="mini-textbox rxc"   style="width:100%;height:34px"/>
					</td>
				</tr>
				<tr>
					<td align="center" style="white-space: nowrap;">
						获奖时间：
					</td>
					<td align="left" colspan="1">
						<input name="rewardDate" class="mini-datepicker" allowInput="false"  required="true" style="width:100%;height:34px" >
					</td>
					<td align="center" style="white-space: nowrap;">
						评选单位：
					</td>
					<td align="left" colspan="1">
						<input name="judgeUnit" class="mini-textbox rxc"   style="width:100%;height:34px"/>
					</td>
				</tr>
				<tr>
					<td align="center" style="white-space: nowrap;">
						备注：
					</td>
					<td align="left" colspan="3">
						<input name="remark" class="mini-textbox rxc" plugins="mini-textbox" vtype="length:50"  onvalidation=""
							   datatype="varchar" length="50" decimal="0" minnum="" maxnum="" validrule="" from="forminput"  only_read="false" allowinput="true" value="" format="" emptytext="" sequence="" scripts="" mwidth="100" wunit="%" mheight="34" hunit="px" style="width:100%;height:34px"/>
					</td>
				</tr>
				<tr>
					<td style="text-align: center;height: 250px">荣誉附件：</td>
					<td colspan="3">
						<div style="margin-top: 10px;margin-bottom: 2px">
							<a id="addFile" class="mini-button" onclick="addFile()">添加附件</a>
							<span style="color: red">注：添加附件前，请先进行保存</span>
						</div>
						<div id="fileListGrid" class="mini-datagrid" style="width: 100%; height: 85%" allowResize="false"
							 idField="id" url="${ctxPath}/portrait/files/files.do?fileType=honor&mainId=${applyObj.id}"+mainId autoload="true"
							 multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
							<div property="columns">
								<div type="indexcolumn" align="center" width="20">序号</div>
								<div field="fileName" align="left" headerAlign="center" width="150">文件名</div>
								<div field="fileSize" align="center" headerAlign="center" width="60">文件大小</div>
								<div field="fileDesc" align="left" headerAlign="center" width="100">文件描述</div>
								<div field="action" width="100" headerAlign='center' align="center" renderer="fileInfoRenderer">操作</div>
							</div>
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
	var honorForm = new mini.Form("#honorForm");
	var fileListGrid = mini.get("fileListGrid");
	var currentUserId = "${currentUserId}";
	var currentUserName = "${currentUserName}";
	var currentTime = "${currentTime}";
	var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";
	var editable = ${editable};
	var url=jsUseCtxPath+"/portrait/files/files.do";
	$(function () {
		if(action!='add'){
			honorForm.setData(applyObj);
			mini.get("recUserSelectId").setEnabled(false);
		}
		if (action == 'view') {
			honorForm.setEnabled(false);
			$('#save').hide();
		}
	})
	function queryPortraitFiles() {
		var mainId = mini.get('id').getValue();
		fileListGrid.setUrl(url+"?mainId="+mainId+"&fileType=honor");
		fileListGrid.load();
	}
	function saveData() {
		honorForm.validate();
		if (!honorForm.isValid()) {
			return;
		}
		var formData = honorForm.getData();
		var config = {
			url: jsUseCtxPath+"/portrait/honor/save.do",
			method: 'POST',
			data: formData,
			success: function (result) {
				//如果存在自定义的函数，则回调
				var result=mini.decode(result);
				if(result.success){
					mini.get("id").setValue(result.data.id);
				}else{
				};
			}
		}
		_SubmitJson(config);
	}
	function addFile() {
		var mainId = mini.get("id").getValue();
		if (!mainId) {
			mini.alert('请先点击‘保存’进行表单的保存！');
			return;
		}
		mini.open({
			title: "文件上传",
			url: jsUseCtxPath + "/portrait/files/fileUploadWindow.do?mainId=" + mainId+"&fileType=honor&editable=true",
			width: 750,
			height: 450,
			showModal: false,
			allowResize: true,
			onload: function () {
			},
			ondestroy: function (action) {
				queryPortraitFiles();
			}
		});
	}
	function fileInfoRenderer(e) {
		var record = e.record;
		var s = '';
		s += returnPreviewSpan(record.fileName, record.id, record.mainId, coverContent,"portraitFileUrl");
		s+='&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
				'onclick="downFile('+JSON.stringify(record).replace(/"/g, '&quot;')+')">下载</span>';
		if(record.CREATE_BY_ != currentUserId||!editable) {
			s+='&nbsp;&nbsp;&nbsp;<span title="删除" style="color:silver;">删除</span>';
		} else {
			s+='&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
					'onclick="deleteFile('+JSON.stringify(record).replace(/"/g, '&quot;')+')">删除</span>';
		}
		return s;
	}
	function deleteFile(record) {
		mini.confirm("确定删除？", "确定？",
				function (action) {
					if (action == "ok") {
						var url = jsUseCtxPath + "/portrait/files/delFile.do";
						var data = {
							mainId: record.mainId,
							id: record.id,
							fileName: record.fileName
						};
						$.post(
								url,
								data,
								function (json) {
									fileListGrid.load();
								});
					}
				}
		);
	}
	//下载文档
	function downFile(record) {
		var form = $("<form>");
		form.attr("style", "display:none");
		form.attr("target", "");
		form.attr("method", "post");
		form.attr("action", jsUseCtxPath + "/sys/core/commonInfo/fileDownload.do?action=download");
		var inputFileName = $("<input>");
		inputFileName.attr("type", "hidden");
		inputFileName.attr("name", "fileName");
		inputFileName.attr("value", record.fileName);
		var detailId = $("<input>");
		detailId.attr("type", "hidden");
		detailId.attr("name", "formId");
		detailId.attr("value", record.mainId);
		var fileId = $("<input>");
		fileId.attr("type", "hidden");
		fileId.attr("name", "fileId");
		fileId.attr("value", record.id);
		var fileUrl = $("<input>");
		fileUrl.attr("type", "hidden");
		fileUrl.attr("name", "fileUrl");
		fileUrl.attr("value", "portraitFileUrl");
		$("body").append(form);
		form.append(inputFileName);
		form.append(detailId);
		form.append(fileId);
		form.append(fileUrl);
		form.submit();
		form.remove();
	}
</script>
</body>
</html>
