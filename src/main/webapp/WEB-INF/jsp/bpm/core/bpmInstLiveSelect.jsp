<%-- 
    Document   : [BpmInstCtl]列表页
    Created on : 2015-3-21, 0:11:48
    Author     : csx
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<%@include file="/commons/edit.jsp"%>
<script src="${ctxPath }/scripts/flow/inst/bpmtask.js?version=${static_res_version}"></script>
<script src="${ctxPath }/scripts/flow/inst/opinions.js?version=${static_res_version}"></script>
</head>

<body>

<div class="mini-toolbar  topToolBar">
	<div>
	    <a class="mini-button"  onclick="onOk()">确定 </a>
	    <a class="mini-button btn-red"   onclick="CloseWindow('cancel')">关闭</a>
    </div>
</div>

<div class="mini-fit form-outer shadowBox90" style="height: 100%;">
			<div id="nodeUsersContainer" style="background-color: white;border-bottom: none"></div>
		
			<table  class="table-detail column_2_m" cellpadding="0" cellspacing="1" style="width:100%" >
				<tr>
					<td>指定节点</td>
					<td>
						<input class="mini-combobox" id="destNodeId" name="destNodeId" textField="nodeName" valueField="nodeId"
								 url="${ctxPath}/bpm/core/bpmRuPath/getBackNodes/${param.instId}.do" required="true" style="width:80%;"/>
					</td>
				</tr>
				<tr>
					<td>审批人</td>
					<td>
						<div id="nodeUsers" name="nodeUsers" class="mini-user"  single="false"  style="width:80%;"   ></div> 
					</td>
				</tr>
				<tr>
					<td>审批组</td>
					<td>
						<div id="nodeGroups" name="nodeGroups" class="mini-dep"  single="false"  style="width:80%;"   ></div> 
					</td>
				</tr>
				<tr>
					<td>意见</td>
					<td>
						<div style="clear:both;width:100%;margin:4px 0px;">
						<input name="opinionSelect"  id="opinionSelect" class="mini-combobox"  emptyText="常用处理意见..." style="width:80%;" minWidth="120" 
							url="${ctxPath}/bpm/core/bpmOpinionLib/getUserText.do" valueField="opId" textField="opText" 
						 	onvaluechanged="showOpinion()" ondrawcell="onDrawCells"   allowInput="false"/>
						<a class="mini-button"  onclick="saveOpinionLib()" >保存意见</a>
						</div>
						<textarea class="mini-textarea" id="opinion" name="opinion" style="width:80%;height:150px;" value="" emptyText="请填写审批意见！"></textarea>
					</td>
				</tr>
				<tr>
					<td>附件</td>
					<td>
						<div id="opFiles" name="opFiles" class="upload-panel"  style="width:auto;" isDown="false" isPrint="false"  readOnly="false" value='' ></div> 
					</td>
				</tr>
			</table>
	</div>

	<script type="text/javascript">
        	mini.parse();
        	var tree=mini.get("treegrid1");
        	function onOk(){
        		CloseWindow('ok');
        	}
        	
        	function getData(){
        		var result={};
        		var opinion=mini.get("opinion").getValue();
        		var opFiles=mini.get("opFiles").getValue();
        		result.opinion=opinion;
        		result.opFiles=opFiles;
        		
        		if(!mini.get("destNodeId").validate())return;
        		
        		var nodeId=mini.get("destNodeId").getValue();
        		var objUser=mini.get("nodeUsers");
        		var objGroup=mini.get("nodeGroups");
        		var user={nodeId:nodeId,userIds:objUser.getValue(),groupIds:objGroup.getValue()};
        		var aryUser=[];
    			aryUser.push(user);
        		result.destNodeUsers=JSON.stringify(aryUser);
        		return result;
        	}
        	
        	
        </script>
</body>
</html>