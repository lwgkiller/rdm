<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<title>文件上传</title>
	<%@include file="/commons/edit.jsp"%>
	<script src="${ctxPath}/scripts/xcmgProjectManager/projectUtil.js?version=${static_res_version}" type="text/javascript"></script>
	<script src="${ctxPath}/scripts/multiFileUpload/multiuploadFile.js" type="text/javascript"></script>
	<link href="${ctxPath}/styles/css/multiupload.css" rel="stylesheet" type="text/css"/>
</head>

<body>

<div id="fileUploadDiv" class="mini-panel" style="width: 100%; height: 100%" showfooter="false" bodystyle="padding:0" borderStyle="border:0"
	 showheader="false">
	<input class="file-input" style="display:none;" type="file" name="fileselect[]" multiple accept="" />
	<div class="mini-toolbar">
		<a class="mini-button file-select" iconCls="icon-search" >浏览...</a>
		<a class="mini-button file-upload" iconCls="icon-upload" name="multiupload">开始上传</a>
		<a class="mini-button file-clear" iconCls="icon-remove" name="removeAll">清空列表</a>
	</div>
	<div id="multiuploadGrid" class="mini-datagrid"  allowResize="false" idField="id"
		 allowCellEdit="true" allowCellSelect="true"
		 multiSelect="false" showColumnsMenu="false" allowAlternating="true" showPager="false" allowCellWrap="false">
		<div property="columns">
			<div type="indexColumn"></div>
			<div field="fileName" width="110" headerAlign="center" align="center">文件名
				<input property="editor" class="mini-textbox" readonly/>
			</div>
			<div field="fileType" width="110" headerAlign="center" align="center" >文件标签<span style="color: #ff0000">*</span>
<%--				<input id="fileType" property="editor" class="mini-combobox" style="width:90%;"--%>
<%--					   textField="text" valueField="id" emptyText="请选择..."--%>
<%--					   data="[{id:'CAD相关',text:'CAD相关'},{id:'PDM相关',text:'PDM相关'}]"--%>
<%--					   allowInput="false" showNullItem="false" nullItemText="请选择..." required="true"/>--%>

				<input id="fileType" name="fileType" class="mini-combobox" style="width:98%" property="editor"
					   url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=GZKWJFL"
					   valueField="key" textField="value" nullItemText="请选择..." required="true"/>
			</div>
			<div field="fileDirection"  width="110" headerAlign="center" align="center">备注说明
				<textarea property="editor" id="fileDesc" name="fileDesc" class="mini-textarea rxc" plugins="mini-textarea"
						  style="width:98%;height:200px;line-height:25px;"
						  label="备注说明" datatype="varchar" length="500" vtype="length:500" minlen="0" allowinput="true"
						  emptytext="请输入备注说明..." mwidth="80" wunit="%" hunit="px"></textarea>
			</div>
			<div field="fileSize" width="80" headerAlign="center" align="center">文件大小
				<input property="editor" class="mini-textbox" readonly/>
			</div>
			<div field="complete" width="80" headerAlign="center" align="center" renderer="onProgressRenderer">上传进度
			</div>
			<div field="status" width="60" headerAlign="center" align="center">上传状态
				<input property="editor" class="mini-textbox" readonly/>
			</div>
			<div field="action" width="30" headerAlign="center" align="center">操作
			</div>
		</div>
	</div>
</div>

<%--
<div class="mini-fit">
	<div class="form-container" style="margin: 0 auto">
		<form id="formFile" method="post">
			<input id="id" name="id" class="mini-hidden"/>
			<table class="table-detail" cellspacing="1" cellpadding="0">
				<tr>
					<td style="width: 14%">附件：</td>
					<td colspan="3">
						<input class="mini-textbox" style="width:70%;float: left" id="fileName" name="fileName"  readonly />
						<input id="inputFile" style="display:none;" type="file" onchange="getSelectFile()"   />
						<a id="uploadFileBtn" class="mini-button"  style="float: left;margin-left: 10px" onclick="uploadFile()">选择文件</a>
						<a id="clearFileBtn" class="mini-button"  style="float: left;margin-left: 10px" onclick="clearUploadFile">清除</a>
					</td>
				</tr>

				<tr>
					<td style="width: 14%">文件标签：<span style="color: #ff0000">*</span></td>

					<td style="width: 36%;min-width:170px">
						<input id="fileType" name="fileType" class="mini-combobox" style="width:98%;"
							   textField="key" valueField="value" emptyText="请选择..."
							   required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
							   data="[ {'key' : 'PDM相关','value' : 'pdm'},{'key' : 'CAD相关','value' : 'cad'},{'key' : '其他','value' : 'other'}]"
						/>
					</td>
				</tr>

				<tr>
					<td style="width: 14%">备注说明：</td>
					<td colspan="3">
						<textarea id="fileDirection" name="fileDirection" class="mini-textarea rxc" plugins="mini-textarea" style="width:98%;"
								  label="备注" datatype="varchar" allowinput="true"
								  emptytext="请输入备注..." mwidth="80" wunit="%" mheight="200" hunit="px"></textarea>

					</td>
				</tr>
				<tr>
					<td style="width: 14%">放网盘位置：</td>
					<td colspan="3">
						<input id="filePosition" name="filePosition" class="mini-textbox"  style="width:98%;"
							   emptytext="若不上传附件，则填写存放位置"
						/>
					</td>
				</tr>

				<tr>

					<td style="width: 14%">创建人：</td>
					<td style="width: 36%;">
						<input id="creator" name="creator"  class="mini-textbox"  style="width:98%" allowinput="false"/>
					</td>
					<td style="width: 14%">创建时间：</td>
					<td style="width: 36%;">
						<input id="CREATE_TIME_" name="CREATE_TIME_"  dateFormat="yyyy-MM-dd HH-mm-ss" class="mini-textbox"  style="width:98%" allowinput="false"/>

					</td>
				</tr>
			</table>
		</form>
	</div>

</div>
--%>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
	var multiuploadGrid = mini.get("#multiuploadGrid");
	function onProgressRenderer(e) {
		var record = e.record;
		var value = e.value;
		var uid = record._uid;
		var s='<div class="progressbar">'
				+ '<div class="progressbar-percent" style="width:' + value + '%;"></div>'
				+ '<div class="progressbar-label">' + value + '%</div>'
				+ '</div>';
		return s;
	}
	$(function(){
		$('#fileUploadDiv').Huploadify({
			sendFileAttr:["fileName","fileType","fileDirection","fileSize"],
			uploadFileList:multiuploadGrid,
			url:jsUseCtxPath+'/pdm/core/PDMOpsDev/saveFile.do',
			onUploadStart:function (file) {
				var row = this.uploadFileList.findRow(function(row){
					if(row.id == file.id) return true;
				});

				if(!row.fileType) {
					return {success:false,message:"“"+row.fileName+"”没有选择文件类别，请点击单元格处选择！"};
				}
				return {success:true,message:""};
			},
			addFileToGrid: function (files) {
				for(var i=0;i<files.length;i++) {
					var file=files[i];
					var row = {id:file.id,fileName:file.name,fileType:file.fileType,fileDirection:file.fileDirection,fileSize:bytesToSize(file.size),status:file.status,complete:0};
					this.uploadFileList.addRow(row);

				}
			}


		});
	});





	function bytesToSize(bytes) {
		if (bytes === 0) return '0 B';
		var k = 1024,
				sizes = ['B', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'],
				i = Math.floor(Math.log(bytes) / Math.log(k));

		return (bytes / Math.pow(k, i)).toPrecision(3) + ' ' + sizes[i];
	}

	function getYMDHmsString(dateObj) {
		if(!dateObj) {
			return "";
		}
		var year=dateObj.getFullYear();
		var month=dateObj.getMonth()+1;
		if(month<10) {
			month='0'+month;
		}
		var date=dateObj.getDate();
		if(date<10) {
			date='0'+date;
		}
		var hour=dateObj.getHours();
		if(hour<10) {
			hour='0'+hour;
		}
		var minute=dateObj.getMinutes();
		if(minute<10) {
			minute='0'+minute;
		}
		var second=dateObj.getSeconds();
		if(second<10) {
			second='0'+second;
		}
		return year+'-'+month+'-'+date+' '+hour+':'+minute+':'+second;
	}
</script>
</body>
</html>
