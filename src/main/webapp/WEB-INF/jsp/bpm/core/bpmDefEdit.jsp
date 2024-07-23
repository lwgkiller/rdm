<%-- 
    Document   : 流程定义编辑页
    Created on : 2015-3-21, 0:11:48
    Author     : csx
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>流程定义编辑</title>
<%@include file="/commons/edit.jsp"%>
</head>
<body>
	<rx:toolbar toolbarId="toolbar1" pkId="${bpmDef.defId}"/>
	<div class="mini-fit">
		<div class="form-container">
			<form id="form1" method="post">
				<input id="pkId" name="defId" class="mini-hidden" value="${bpmDef.defId}" />
				<table class="table-detail column-four table-align" cellspacing="1" cellpadding="0">
					<caption>流程定义基本信息</caption>
					<tr>
						<td>
							<span class="starBox">
								分　　类
								<span class="star">*</span>
							</span>
						</td>
						<td>
							 <input
								id="treeId"
								name="treeId"
								class="mini-treeselect"
								url="${ctxPath}/sys/core/sysTree/listByCatKey.do?catKey=CAT_BPM_DEF"
								multiSelect="false"
								textField="name"
								valueField="treeId"
								parentField="parentId"
								required="true"
								value="${bpmDef.treeId}"
								showFolderCheckBox="false"
								expandOnLoad="true"
								showClose="true"
								oncloseclick="onClearTree"
								popupWidth=""
								style="width:100%;"
							/>
						</td>

						<td><span class="starBox">标识Key <span class="star">*</span></span></td>
						<td >
							<input
								name="key"
								class="mini-textbox"
								vtype="isEnglishAndNumber"
								value="${bpmDef.key}"
								style="width: 100%"
								required="true"
								emptyText="请输入标识Key"
							/>
						</td>

					</tr>
					<tr>
						<td>
							<span class="starBox">标　　题 <span class="star">*</span></span>
						</td>
						<td colspan="3">
							<input
								name="subject"
								class="mini-textbox"
								vtype="maxLength:255"
								value="${bpmDef.subject}"
								style="width: 100%"
								required="true"
								emptyText="请输入标题"
							/>
						</td>

					</tr>
					<tr>
						<td>描　　述</td>
						<td colspan="3">
							<textarea
								name="descp"
								class="mini-textarea"
								vtype="maxLength:1024"
								style="width:90%"
							>${bpmDef.descp}</textarea>
						</td>
					</tr>
				</table>
			</form>
		</div>
	</div>
	<rx:formScript formId="form1" baseUrl="bpm/core/bpmDef" entityName="com.redxun.bpm.core.entity.BpmDef"/>
	<script type="text/javascript">
		addBody();
		var bpmDef=null;
		//返回成功保存的流程定义
		function getJsonData(){
			return bpmDef;
		}
		//成功回调函数，由formScript标签里定义的saveData进行回调
		function successCallback(responseText){
			var result=mini.decode(responseText);
			if(result.success){
				bpmDef=result.data;
				CloseWindow('ok');
			}else{
				CloseWindow('cancel');
			}
		}
		
		function onClearTree(e){
			var obj = e.sender;
            obj.setText("");
            obj.setValue("");
		}
	</script>
</body>
</html>