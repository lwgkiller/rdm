<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<title>科技论文编辑</title>
	<%@include file="/commons/edit.jsp"%>
	<link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css" />
	<script src="${ctxPath}/scripts/rdmZhgl/kjlwEdit.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}" type="text/javascript"></script>
	<script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>

<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
	<div>
		<a id="processInfo" class="mini-button" style="display: none" onclick="processInfo()">流程信息</a>
		<a id="saveKjlwWriter" class="mini-button" style="display: none" onclick="saveKjlwWriter()">作者或专利工程师保存</a>
		<a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
	</div>
</div>
<div class="mini-fit">
	<div class="form-container" style="margin: 0 auto; width: 80%;">
		<form id="formKjlw" method="post">
			<input id="kjlwId" name="kjlwId" class="mini-hidden"/>
			<input id="instId" name="instId" class="mini-hidden"/>
			<input id="CREATE_BY_" name="CREATE_BY_" class="mini-hidden"/>

			<table class="table-detail grey"  cellspacing="1" cellpadding="0">
				<caption style="font-weight: bold">
					科技论文申请
				</caption>
				<tr>
					<td style="width: 14%">编号(提交后自动生成)：</td>
					<td style="width: 30%">
						<input id="kjlwNum" name="kjlwNum"  class="mini-textbox" readonly style="width:98%;" />
					</td>
					<td style="width: 14%">论文名称：<span style="color:red">*</span></td>
					<td style="width: 30%">
						<input id="kjlwName" name="kjlwName"  class="mini-textbox" style="width:98%;" />
					</td>
				</tr>
				<tr>
					<td style="width: 14%">是否申请专利或软著：<span style="color:red">*</span></td>
					<td style="width: 30%">
						<input id="ifzlrz" name="ifzlrz" class="mini-combobox" style="width:98%;"
							   textField="value" valueField="key" emptyText="请选择..."
							   required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
							   data="[{'key' : 'yes','value' : '是'},{'key' : 'no','value' : '否'}]"/>
					</td>
					<td style="width: 14%">拟发表期刊：<span style="color:red">*</span></td>
					<td style="width: 30%;">
						<input id="fbqk" name="fbqk"  class="mini-textbox" style="width:98%;" />
					</td>
				</tr>
				<tr>
					<td style="width: 14%">作者：<span style="color:red">*</span></td>
					<td style="width: 30%">
						<input id="writerId" name="writerId" textname="writerName" class="mini-user rxc" plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="可见范围" length="1000" maxlength="1000"  mainfield="no"  single="false" />
					</td>
					<td style="width: 14%">知识产权及名称：<span style="color:red">*</span></td>
					<td style="width: 30%">
						<input id="cqmc" name="cqmc"  class="mini-textbox" style="width:98%;" />
				</td>
				</tr>
				<tr>
					<td style="width: 14%">期刊名称：</td>
					<td style="width: 30%;">
						<input id="qkmc" name="qkmc"  class="mini-textbox" style="width:98%;" />
					</td>
					<td style="width: 14%">期号：</td>
					<td style="width: 30%;">
						<input id="qNum" name="qNum"  class="mini-textbox" style="width:98%;" />
					</td>
				</tr>
				<tr>
					<td style="width: 14%">是/否涉密：<span style="color:red">*</span></td>
					<td style="width: 30%">
						<input id="ifshemi" name="ifshemi" class="mini-combobox" style="width:98%;"
							   textField="value" valueField="key" emptyText="请选择..." enabled ="false"
							   required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
							   data="[{'key' : 'yes','value' : '是'},{'key' : 'no','value' : '否'}]"/>
					</td>

				</tr>
				<tr>
					<td style="width: 14%">页码：</td>
					<td style="width: 30%;">
						<input id="yema" name="yema"  class="mini-textbox" style="width:98%;" />
					</td>
					<td style="width: 14%">发表时间：</td>
					<td style="width: 30%">
						<input id="fbTime" name="fbTime"  class="mini-datepicker" format="yyyy-MM-dd"
							   showTime="false" showOkButton="false" showClearButton="true" style="width:98%;" />
					</td>
				</tr>
				<tr>
					<td style="width: 14%">来源项目：<span style="color:red">*</span></td>
					<td style="width: 30%;" colspan="4">
						<textarea id="lyxm" name="lyxm" class="mini-textarea rxc" plugins="mini-textarea" style="width:100%;height:50px;line-height:25px;" label="" datatype="varchar" length="1000" vtype="length:1000" minlen="0" allowinput="true"
								  emptytext="xxx项目"  wunit="%" mheight="150" hunit="px"></textarea>
					</td>
				</tr>
				<tr>
					<td style="width: 14%">主要内容：<span style="color:red">*</span></td>
					<td style="width: 30%;" colspan="4">
						<textarea id="zynr" name="zynr" class="mini-textarea rxc" plugins="mini-textarea" style="width:100%;;height:150px;line-height:25px;" label="" datatype="varchar" length="1000" vtype="length:1000" minlen="0" allowinput="true"
								  emptytext="" mwidth="80" wunit="%" mheight="150" hunit="px"></textarea>
					</td>
				</tr>
				<tr>
					<td style="width: 14%;height: 300px">附件列表：</td>
					<td colspan="3">
						<div style="margin-top: 10px;margin-bottom: 2px">
							<a id="addFile" class="mini-button"  onclick="addKjlwFile()">添加附件</a>
							<span style="color: red">注：添加附件前，请先进行草稿的保存</span>
						</div>
						<div id="fileListGrid" class="mini-datagrid" style="width: 100%; height: 85%" allowResize="false"
							 idField="id" url="${ctxPath}/zhgl/core/kjlw/getKjlwFileList.do?kjlwId=${kjlwId}" autoload="true"
							 multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true" >
							<div property="columns">
								<div type="indexcolumn" align="center"  width="20">序号</div>
								<div field="fileName"  width="140" headerAlign="center" align="center" >附件名称</div>
								<div field="fileSize"  width="80" headerAlign="center" align="center" >附件大小</div>
								<div field="action" width="100" headerAlign='center' align="center" renderer="operationRenderer">操作</div>
							</div>
						</div>
					</td>
				</tr>
			</table>
		</form>
	</div>
</div>



<script type="text/javascript">
    mini.parse();
    var action="${action}";
    var status="${status}";
    var jsUseCtxPath="${ctxPath}";
	var fileListGrid=mini.get("fileListGrid");
    var formKjlw = new mini.Form("#formKjlw");
    var kjlwId="${kjlwId}";
    var nodeVarsStr='${nodeVars}';
    var currentUserName="${currentUserName}";
    var currentTime="${currentTime}";
    var ifWriterUser=${ifWriterUser};
    var currentUserId="${currentUserId}";
    var currentUserRoles=${currentUserRoles};
    var coverContent=currentUserName+"<br/>"+currentTime+"<br/>徐州徐工挖掘机械有限公司";
    var isZlgcsUser=${isZlgcsUser};

	function operationRenderer(e) {
        var record = e.record;
        var cellHtml = '';
        cellHtml=returnKjlwPreviewSpan(record.fileName,record.id,record.kjlwId,coverContent);
        //增加删除按钮
        if(action=='edit' || (action=='task' && isBianzhi == 'yes')) {
            var deleteUrl="/zhgl/core/kjlw/delKjlwFile.do"
            cellHtml+='&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(\''+record.fileName+'\',\''+record.id+'\',\''+record.kjlwId+'\',\''+deleteUrl+'\')">删除</span>';
        }
        cellHtml+='&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downKjlwLoadFile('+JSON.stringify(record).replace(/"/g, '&quot;')+')">下载</span>';
        return cellHtml;
    }


</script>
</body>
</html>
