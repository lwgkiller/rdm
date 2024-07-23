<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<title>技术秘密编辑</title>
	<%@include file="/commons/edit.jsp"%>
	<link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css" />
	<script src="${ctxPath}/scripts/rdmZhgl/jsmmEdit.js?version=${static_res_version}" type="text/javascript"></script>
	<script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>

<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
	<div>
		<a id="processInfo" class="mini-button" style="display: none" onclick="processInfo()">流程信息</a>
		<a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
	</div>
</div>
<div class="mini-fit">
	<div class="form-container" style="margin: 0 auto; width: 80%;">
		<form id="formJsmm" method="post">
			<input id="jsmmId" name="jsmmId" class="mini-hidden"/>
			<input name="whetherJL" class="mini-hidden"/>
			<input name="jlTime" class="mini-hidden"/>
			<input name="rdTime" class="mini-hidden"/>
			<input id="instId" name="instId" class="mini-hidden"/>
			<input id="CREATE_BY_" name="CREATE_BY_" class="mini-hidden"/>

			<table class="table-detail grey"  cellspacing="1" cellpadding="0">
				<caption style="font-weight: bold">
					技术秘密认定申请书
				</caption>
				<tr>
					<td style="width: 17%">技术秘密编号(提交后自动生成)：</td>
					<td style="width: 33%;min-width:170px">
						<input id="jsmmNumber" name="jsmmNumber"  class="mini-textbox" readonly style="width:98%;" />
					</td>
					<td style="width: 14%">技术秘密名称：</td>
					<td style="width: 36%;min-width:170px">
						<input id="jsmmName" name="jsmmName"  class="mini-textbox" style="width:98%;" />
					</td>
				</tr>
				<tr>
					<td style="width: 14%">技术研究方向：</td>
					<td style="width: 36%;min-width:140px">
						<input id="jsfx" name="jsfx"  class="mini-textbox" style="width:98%;" />
					</td>
					<td style="width: 14%">技术研发成本：</td>
					<td style="width: 36%;">
						<input id="jscb" name="jscb"  class="mini-textbox" style="width:98%;" />
					</td>
				</tr>
				<tr>
					<td style="width: 14%">可见范围人员：</td>
					<td style="width: 36%;min-width:170px">
						<input id="readUserIds" name="readUserIds" textname="readUserNames" class="mini-user rxc" plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="可见范围" length="1000" maxlength="1000"  mainfield="no"  single="false" />
					</td>
					<td style="width: 14%">所属项目名称及编号：</td>
					<td style="width: 36%;">
						<input id="projectNameNumber" name="projectNameNumber"  class="mini-textbox" style="width:98%;" />
					</td>
				</tr>
				<tr>
					<td style="width: 14%">完成人员：</td>
					<td style="width: 36%;min-width:170px">
						<input id="finishUserIds" name="finishUserIds" textname="finishUserNames" class="mini-user rxc" plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="可见范围" length="1000" maxlength="1000"  mainfield="no"  single="false" />
					</td>
				</tr>
				<tr>
					<td style="width: 14%">所属项目基本情况：</td>
					<td  colspan="3">
						<textarea id="projectInfo" name="projectInfo" class="mini-textarea rxc" plugins="mini-textarea" style="width:98%;height:150px;line-height:25px;" label="" datatype="varchar" length="1000" vtype="length:1000" minlen="0" allowinput="true"
								  emptytext="1、项目简介&#10;2、项目进度&#10;3、项目团队成员及技术知悉范围"  wunit="%" mheight="150" hunit="px"></textarea>
					</td>
				</tr>
				<tr>
					<td style="width: 14%">技术先进性：</td>
					<td style="width: 36%;" colspan="3">
						<textarea id="jsxjx" name="jsxjx" class="mini-textarea rxc" plugins="mini-textarea" style="width:98%;;height:150px;line-height:25px;" label="" datatype="varchar" length="1000" vtype="length:1000" minlen="0" allowinput="true"
								  emptytext="1、阐述行业现状&#10;2、阐述所解决的技术问题、采用的技术方案、达到的技术效果&#10;3、阐述技术原创性、重要性及前沿性等" mwidth="80" wunit="%" mheight="150" hunit="px"></textarea>
					</td>
				</tr>
				<tr>
					<td style="width: 14%">秘密性：</td>
					<td style="width: 36%;" colspan="3">
						<textarea id="mmx" name="mmx" class="mini-textarea rxc" plugins="mini-textarea" style="width:98%;;height:150px;line-height:25px;" label="" datatype="varchar" length="1000" vtype="length:1000" minlen="0" allowinput="true"
								  emptytext="阐述反向工程获得的难易程度及原因、侵权行为发现的难易程度及原因、研发获得的难易程度及原因等" mwidth="80" wunit="%" mheight="150" hunit="px"></textarea>
					</td>
				</tr>
				<tr>
					<td style="width: 14%">产业贡献：</td>
					<td style="width: 36%;" colspan="3">
						<textarea id="cygx" name="cygx" class="mini-textarea rxc" plugins="mini-textarea" style="width:98%;;height:150px;line-height:25px;" label="" datatype="varchar" length="1000" vtype="length:1000" minlen="0" allowinput="true"
								  emptytext="阐述产业应用情况及收益，或工作效率的提升，或生产工艺改进，或成本降低等" mwidth="80" wunit="%" mheight="200" hunit="px"></textarea>
					</td>
				</tr>
				<tr>
					<td style="width: 14%">其他：</td>
					<td style="width: 36%;" colspan="3">
						<textarea id="other" name="other" class="mini-textarea rxc" plugins="mini-textarea" style="width:98%;;height:90px;line-height:25px;" label="" datatype="varchar" length="1000" vtype="length:1000" minlen="0" allowinput="true"
								  emptytext="" mwidth="80" wunit="%" mheight="90" hunit="px"></textarea>
					</td>
				</tr>
				<tr>
					<td style="width: 14%;height: 300px">附件列表：</td>
					<td colspan="3">
						<div style="margin-top: 10px;margin-bottom: 2px">
							<a id="addFile" class="mini-button"  onclick="addJsmmFile()">添加附件</a>
							<span style="color: red">注：添加附件前，请先进行草稿的保存</span>
						</div>
						<div id="fileListGrid" class="mini-datagrid" style="width: 100%; height: 85%" allowResize="false"
							 idField="id" url="${ctxPath}/zhgl/core/jsmm/getJsmmFileList.do?jsmmId=${jsmmId}" autoload="true"
							 multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true" >
							<div property="columns">
								<div type="indexcolumn" align="center"  width="20">序号</div>
								<div field="fileName"  width="140" headerAlign="center" align="center" >附件名称</div>
								<div field="typeName"  width="80" headerAlign="center" align="center" >附件类别</div>
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
    var formJsmm = new mini.Form("#formJsmm");
    var jsmmId="${jsmmId}";
    var nodeVarsStr='${nodeVars}';
    var currentUserName="${currentUserName}";
    var currentTime="${currentTime}";
    var coverContent=currentUserName+"<br/>"+currentTime+"<br/>徐州徐工挖掘机械有限公司";

	function operationRenderer(e) {
        var record = e.record;
        var cellHtml = '';
        cellHtml=returnJsmmPreviewSpan(record.fileName,record.id,record.jsmmId,coverContent);
        //增加删除按钮
        if(action=='edit' || (action=='task' && isBianzhi == 'yes')) {
            var deleteUrl="/zhgl/core/jsmm/delJsmmFile.do"
            cellHtml+='&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(\''+record.fileName+'\',\''+record.id+'\',\''+record.jsmmId+'\',\''+deleteUrl+'\')">删除</span>';
        }
        return cellHtml;
    }

</script>
</body>
</html>
