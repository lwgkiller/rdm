<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
	<title>新增人才简历</title>
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
		<form id="recruitmentForm" method="post">
			<input id="id" name="id" class="mini-hidden"/>
			<table cellspacing="1" cellpadding="0" class="table-detail column-four grey">
				<caption>
					人才招聘
				</caption>
				<tbody>
				<tr class="firstRow displayTr">
					<td align="center"></td>
					<td align="left"></td>
					<td align="center"></td>
					<td align="left"></td>
				</tr>
				<tr>
					<td style="text-align: center;width: 20%">推荐人：</td>
					<td style="min-width:170px">
						<input id="userId" name="userId" textname="userName"
							   class="mini-user rxc" plugins="mini-user" style="width:98%;height:34px;" allowinput="false"
							   label="申请人" length="50" mainfield="no" single="true" enabled="false"/>
					</td>
					<td style="text-align: center;width: 20%">推荐人部门：</td>
					<td style="min-width:170px">
						<input id="deptId" name="deptId" class="mini-dep rxc" plugins="mini-dep"
							   data-options="{'single':'true','config':{'type':'','grouplevel':'','groupid':'','groupidText':''}}"
							   style="width:98%;height:34px" allowinput="false" label="部门" textname="deptName" length="500"
							   maxlength="500" minlen="0" single="true" required="false" initlogindep="false" showclose="false"
							   mwidth="160" wunit="px" mheight="34" hunit="px" enabled="false"/>
					</td>
				<tr>
				<tr>
					<td align="center" style="white-space: nowrap;">
						简历状态<span style="color: red">*</span>：
					</td>
					<td align="left" colspan="1">
						<input id="status" name="status" class="mini-combobox rxc" plugins="mini-combobox"
							   style="width:100%;height:34px"  label="简历状态："
							   length="50"
							   only_read="false" required="true" allowinput="false" mwidth="100"
							   wunit="%" mheight="34" hunit="px"   shownullitem="true" multiSelect="false"
							   textField="text" valueField="key_" emptyText="请选择..."
							   url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=resumeStatus"
							   nullitemtext="请选择..." emptytext="请选择...">
					</td>
				</tr>
				<tr>
					<td align="center" style="white-space: nowrap;">
						人才名称<span style="color: red">*</span>：
					</td>
					<td align="left" colspan="1">
						<input name="resumeName" required  class="mini-textbox rxc"   style="width:100%;height:34px"/>
					</td>
					<td align="center" style="white-space: nowrap;">
						专业<span style="color: red">*</span>：
					</td>
					<td align="left" colspan="1">
						<input name="major" required  class="mini-textbox rxc"   style="width:100%;height:34px"/>
					</td>
				</tr>
				<tr>
					<td align="center" style="white-space: nowrap;">
						简历描述：
					</td>
					<td align="left" colspan="3">
						<textarea id="remark" name="remark" class="mini-textarea rxc" plugins="mini-textarea"
								  style="width:99%;height:80px;line-height:25px;"
								  label="简历描述" datatype="varchar" length="500" vtype="length:500" minlen="0"
								  allowinput="true"
								  emptytext="请输入简历描述..." mwidth="80" wunit="%" mheight="200" hunit="px"></textarea>
					</td>
				</tr>
				<tr>
					<td style="text-align: center;height: 250px">简历附件：</td>
					<td colspan="3">
						<div style="margin-top: 10px;margin-bottom: 2px">
							<a id="addFile" class="mini-button" onclick="addFile()">添加附件</a>
							<span style="color: red">注：添加附件前，请先进行保存</span>
						</div>
						<div id="fileListGrid" class="mini-datagrid" style="width: 100%; height: 85%" allowResize="false"
							 idField="id" url="${ctxPath}/portrait/files/files.do?fileType=recruitment&mainId=${applyObj.id}"+mainId autoload="true"
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
	var recruitmentForm = new mini.Form("#recruitmentForm");
	var fileListGrid = mini.get("fileListGrid");
	var currentUserId = "${currentUserId}";
	var currentUserName = "${currentUserName}";
	var currentTime = "${currentTime}";
	var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";
	var editable = ${editable};
	var url=jsUseCtxPath+"/portrait/files/files.do";
	$(function () {
		recruitmentForm.setData(applyObj);
		if (action == 'view') {
			recruitmentForm.setEnabled(false);
			$('#save').hide();
		}
		if(action == 'add' ||action == 'edit'){
			mini.get('status').setEnabled(false);
		}
		if(action == "manage"){
			recruitmentForm.setEnabled(false);
			mini.get('status').setEnabled(true);
		}
	})
	function queryPortraitFiles() {
		var mainId = mini.get('id').getValue();
		fileListGrid.setUrl(url+"?mainId="+mainId+"&fileType=recruitment");
		fileListGrid.load();
	}
	function saveData() {
		recruitmentForm.validate();
		if (!recruitmentForm.isValid()) {
			return;
		}
		var formData = recruitmentForm.getData();
		var config = {
			url: jsUseCtxPath+"/portrait/recruitment/save.do",
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
			url: jsUseCtxPath + "/portrait/files/fileUploadWindow.do?mainId=" + mainId+"&fileType=recruitment&editable=true",
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
