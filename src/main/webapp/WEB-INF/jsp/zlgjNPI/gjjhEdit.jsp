<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<title>改进计划编辑</title>
	<%@include file="/commons/edit.jsp"%>
	<link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css" />
	<script src="${ctxPath}/scripts/zlgjNPI/gjjhEdit.js?version=${static_res_version}" type="text/javascript"></script>
	<script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>

<body>
<div id="toolBar" class="topToolBar">
	<div>
		<a id="saveBtn" class="mini-button" onclick="saveGjjh()">保存</a>
		<a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
	</div>
</div>
<div class="mini-fit">
	<div class="form-container" style="margin: 0 auto; width: 100%;">
		<form id="formGjjh" method="post" >
			<input id="id" name="id" class="mini-hidden"/>
			<table class="table-detail grey"  cellspacing="1" cellpadding="0">
				<caption style="font-weight: bold;font-size: 20px !important;">
					质量改进计划
				</caption>
				<tr>
					<td style="width: 7%">项目名称：<span style="color: #ff0000">*</span></td>
					<td style="width: 25%;">
						<input id="formName" name="formName"  class="mini-textbox" style="width:98%;" />
					</td>
					<td style="width: 7%">改进类型：</td>
					<td style="width: 25%;">
						<input id="formType" name="formType" class="mini-combobox" style="width:98%;"
							   textField="value" valueField="key" value="常规"
							   required="false" allowInput="false"
							   data="[ {'key' : '常规','value' : '常规'},{'key' : '新品','value' : '新品'}]"
						/>
					</td>
					<td style="width: 7%">编号：</td>
					<td style="width: 25%;">
						<input id="formNumber" name="formNumber"  class="mini-textbox" style="width:98%;" />
					</td>
				</tr>
				<tr style="display: none">

					<td style="width: 7%">召开时间：<span style="color: #ff0000">*</span></td>
					<td style="width: 25%;">
						<input id="openTime"  name="openTime"  class="mini-datepicker" format="yyyy-MM-dd" style="width:98%"/>
					</td>
					<td style="width: 7%">召开地点：<span style="color: #ff0000">*</span></td>
					<td style="width: 25%;">
						<input id="openPlace" name="openPlace"  class="mini-textbox" style="width:98%;" />
					</td>
					<td style="width: 7%">主持人：<span style="color: #ff0000">*</span></td>
					<td style="width: 25%;">
						<input id="ownerUserIds" name="ownerUserIds" textname="ownerUserNames"
							   property="editor" class="mini-user rxc" plugins="mini-user"  style="width:98%;height:34px;" allowinput="false"  mainfield="no"  single="false" />
					</td>
				</tr>
				<tr>
					<td style="width: 7%">组织部门：<span style="color: #ff0000">*</span></td>
					<td style="width: 25%;">
					<input id="ownerDeptIds" name="ownerDeptIds" class="mini-dep rxc" plugins="mini-dep"
						   data-options="{'single':'true','config':{'type':'','grouplevel':'','groupid':'','groupidText':''}}"
						   style="width:98%;height:34px" allowinput="false" label="部门" textname="ownerDeptNames" length="500" maxlength="500" minlen="0" single="false" required="false" initlogindep="false"
						   level="" refconfig="" grouplevel="" groupid="" mwidth="160" wunit="px" mheight="34" hunit="px"/>
					</td>
					<td style="width: 7%">参与人员：<span style="color: #ff0000">*</span></td>
					<td style="width: 25%;" >
						<textarea id="joinUserNames" name="joinUserNames" class="mini-textarea rxc" plugins="mini-textarea" style="width:98%;;height:80px;line-height:25px;" label="" datatype="varchar" length="200" vtype="length:200" minlen="0" allowinput="true"
								  emptytext="" mwidth="80" wunit="%" mheight="110" hunit="px"></textarea>
					</td>
					<td style="width: 7%">缺席人员：</td>
					<td style="width: 25%;" >
						<textarea id="missUserNames" name="missUserNames" class="mini-textarea rxc" plugins="mini-textarea" style="width:98%;;height:80px;line-height:25px;" label="" datatype="varchar" length="1000" vtype="length:1000" minlen="0" allowinput="true"
								  emptytext="" mwidth="80" wunit="%" mheight="110" hunit="px"></textarea>
					</td>
				</tr>
				<tr style="display: none">
					<td style="width: 7%">会议议题：<span style="color: #ff0000">*</span></td>
					<td colspan="3">
						<textarea id="formTitle" name="formTitle" class="mini-textarea rxc" plugins="mini-textarea" style="width:99%;;height:90px;line-height:25px;" label="" datatype="varchar" length="1000" vtype="length:1000" minlen="0" allowinput="true"
								  emptytext="" mwidth="80" wunit="%" mheight="110" hunit="px"></textarea>
					</td>
					<td style="width: 7%">记录人：<span style="color: #ff0000">*</span></td>
					<td style="width: 20%;" >
						<textarea id="recordUserNames" name="recordUserNames" class="mini-textarea rxc" plugins="mini-textarea" style="width:98%;height:90px;line-height:25px;" label="" datatype="varchar" length="1000" vtype="length:1000" minlen="0" allowinput="true"
								  emptytext="" mwidth="80" wunit="%" mheight="110" hunit="px"></textarea>
					</td>
				</tr>

				<tr>
					<td style="width: 7%;height: 600px">会议决议：</td>
					<td colspan="5">
						<div style="margin-top: 5px;margin-bottom: 2px">
							<a id="addFile" class="mini-button"  onclick="addGjjhDetail()">添加</a>
							<span style="color: red">注：添加前请先进行表单的保存</span>
						</div>
						<div id="detailListGrid" class="mini-datagrid" style="width: 100%; height: 85%" allowResize="false" allowCellWrap="true"
							 idField="id" url="${ctxPath}/zhgl/core/zlgj/getGjjhDetailList.do?belongGjjhId=${id}" autoload="true"
							 multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true" >
							<div property="columns">
								<div field="action" width="80" headerAlign='center' align="center" renderer="operationRenderer">操作</div>
								<div field="xuhao" align="center" headerAlign="center" width="25">序号</div>
								<div header="问题清单" headerAlign="center">
									<div property="columns">
										<div field="jixing"  width="80" headerAlign="center" align="center" >机型</div>
										<div field="bujian"  width="80" headerAlign="center" align="center" >部件</div>
										<div field="problemTime"  width="60" headerAlign="center" align="center" >时间</div>
										<div field="problemDesc"  width="120" headerAlign="center" align="center" renderer="problemDescRender">故障信息</div>
									</div>
								</div>
								<div header="责任清单" headerAlign="center">
									<div property="columns">
										<div field="respDeptNames"  width="80" headerAlign="center" align="center" >责任部门</div>
										<div field="respUserNames"  width="80" headerAlign="center" align="center" >责任人</div>
										<div field="finishTime"  width="60" headerAlign="center" align="center" >完成时间</div>
									</div>
								</div>
								<div header="任务清单" headerAlign="center">
									<div property="columns">
										<div field="reason"  width="120" headerAlign="center" align="center" renderer="reasonRender">原因分析</div>
										<div field="duice"  width="120" headerAlign="center" align="center" renderer="duiceRender">对策</div>
										<div field="fangan"  width="120" headerAlign="center" align="center" renderer="fanganRender">市场方案</div>
									</div>
								</div>
								<div field="finishDesc" align="center" headerAlign="center" width="120" renderer="finishDescRender">完成情况</div>
								<div  align="center" headerAlign="center" width="40" renderer="fileRender">附件</div>
							</div>
						</div>
						<form id="excelForm" action="${ctxPath}/zhgl/core/zlgj/exportGjjh.do" method="post" target="excelIFrame">
							<input type="hidden" name="filter" id="filter"/>
						</form>
						<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>
					</td>
				</tr>
			</table>
		</form>
	</div>
</div>



<script type="text/javascript">
    mini.parse();
    var action="${action}";
    var jsUseCtxPath="${ctxPath}";
	var detailListGrid=mini.get("detailListGrid");
    var formGjjh = new mini.Form("#formGjjh");
    var id="${id}";
    var action="${action}";
    var currentUserId="${currentUserId}";
    var isZLGJ =${isZLGJ};

    function problemDescRender(e) {
        var record=e.record;
        var problemDesc=record.problemDesc;
        var html = "<div style='line-height: 20px;height:100px;overflow: auto' >";
        if (!problemDesc) {
            problemDesc = "";
        }
        html += '<span style="white-space:pre-wrap" >' + problemDesc + '</span>';
        html += '</div>'
        return html;
    }

    function reasonRender(e) {
        var record=e.record;
        var reason=record.reason;
        var html = "<div style='line-height: 20px;height:100px;overflow: auto' >";
        if (!reason) {
            reason = "";
        }
        html += '<span style="white-space:pre-wrap" >' + reason + '</span>';
        html += '</div>'
        return html;
    }

    function duiceRender(e) {
        var record=e.record;
        var duice=record.duice;
        var html = "<div style='line-height: 20px;height:100px;overflow: auto' >";
        if (!duice) {
            duice = "";
        }
        html += '<span style="white-space:pre-wrap" >' + duice + '</span>';
        html += '</div>'
        return html;
    }

    function fanganRender(e) {
        var record=e.record;
        var fangan=record.fangan;
        var html = "<div style='line-height: 20px;height:100px;overflow: auto' >";
        if (!fangan) {
            fangan = "";
        }
        html += '<span style="white-space:pre-wrap" >' + fangan + '</span>';
        html += '</div>'
        return html;
    }

    function finishDescRender(e) {
        var record=e.record;
        var finishDesc=record.finishDesc;
        var html = "<div style='line-height: 20px;height:100px;overflow: auto' >";
        if (!finishDesc) {
            finishDesc = "";
        }
        html += '<span style="white-space:pre-wrap" >' + finishDesc + '</span>';
        html += '</div>'
        return html;
    }

    function fileRender(e) {
        var record=e.record;
        var detailId=record.id;
        var CREATE_BY_=record.CREATE_BY_;
        var respUserIds=record.respUserIds;
        var canEditFile=false;
        if(CREATE_BY_ ==currentUserId || (respUserIds && respUserIds.indexOf(currentUserId)>=0)) {
            canEditFile=true;
        }
        var s='<a href="#" style="color:#44cef6;text-decoration:underline;" ' +
            'onclick="showDetailFiles(\''+detailId+'\',\''+id+'\',\''+canEditFile+'\')">文件</a>';
        return s;
    }

    function operationRenderer(e) {
        var record=e.record;
        var CREATE_BY_=record.CREATE_BY_;
        var respUserIds=record.respUserIds;
        var detailId=record.id;
        var s ='<span title="查看"  style="color: #409EFF" onclick="seeGjjhDetail(\'' + detailId + '\')">查看</span>';
        if(CREATE_BY_ ==currentUserId || (respUserIds && respUserIds.indexOf(currentUserId)>=0)||isZLGJ) {
            s+='&nbsp;&nbsp;<span title="编辑" style="color: #409EFF" onclick="editGjjhDetail(\'' + detailId + '\')">编辑</span>';
        }
        if(action=='edit' && (CREATE_BY_ ==currentUserId||isZLGJ)) {
            s+='&nbsp;&nbsp;<span title="删除" style="color: #409EFF" onclick="removeGjjhDetail(\''+detailId+'\',\''+id+'\')">删除</span>';
        }

        return s;
    }

</script>
</body>
</html>
