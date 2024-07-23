<%--
	//用户组选择器
	//Author:csx
	//Description:若传入关系类型，则显示该关系类型的另一方用户组，否则显示全部关系的用户
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<head>
    <title>用户组选择框</title>
	<%@include file="/commons/list.jsp"%>
	<style>
		.mini-layout-region{
			background: transparent;
		}
		#west{
			background: #fff;
		}
	</style>
</head>
<body>
<div id="layout1" class="mini-layout" style="width:100%;height:100%;" >
		 <div region="south" showSplit="false" showHeader="false" height="46" showSplitIcon="false"
			style="width:100%">
			 <div class="southBtn">
			     <a class="mini-button"     onclick="onOk()">确定</a>
				 <a class="mini-button btn-red"    onclick="onCancel()">取消</a>
			 </div>
		 </div>
		 
		 <c:if test="${empty param['showDimId'] or param['showDimId']=='false'}">
			 <div title="组织维度" region="west" width="140"  showSplitIcon="true" showHeader="false">
			 	<div 
			 		id="dimTree" 
			 		class="mini-tree"  
			 		url="${ctxPath}/sys/org/osDimension/jsonAll.do?tenantId=${param['tenantId']}&excludeAdmin=${param['excludeAdmin']}" 
			 		style="width:100%;" 
					showTreeIcon="true"  
					resultAsTree="false" 
					textField="name" 
					idField="dimId"  
					expandOnLoad="true"
	                onnodeclick="dimNodeClick" 
	                allowAlternating="true"
                >
		        </div>
			 </div>
		 </c:if>
		 <div 
		 	region="center" 
		 	style="padding:0;margin:0;" 
			showCollapseButton="false">
			<div class="mini-toolbar">
					<ul class="toolBtnBox toolBtnBoxTop">
								<li>
									<input class="mini-hidden" id="dimId" name="dimId" value="${param['showDimId']}"/>
								</li>
								<li>
									<input class="mini-textbox" id="name" name="name" emptyText="请输入名称" onenter="onSearch"/>
								</li>
								<li>
									<input class="mini-textbox"  id="key" name="key" emptyText="请输入标识Key" onenter="onSearch"/>
								</li>
						<li>
							<a class="mini-button " onclick="onSearch">搜索</a>
						</li>
						<li>
							<a class="mini-button  btn-red"  onclick="onClear">清空</a>
						</li>
					</ul>
					<span class="searchSelectionBtn" onclick="no_search(this ,'searchForm')">
							<i class="icon-sc-lower"></i>
						</span>
		        </div>
			<div class="mini-fit">
				<div 
					id="groupGrid"
					class="mini-treegrid" 
					style="width:100%;height:100%;"     
			        showTreeIcon="true"  
			        onbeforeload="onBeforeGridTreeLoad"
			        resultAsTree="false" 
			        treeColumn="name" 
			        idField="groupId" 
			        parentField="parentId" 
			        allowResize="true" 
			        onlyCheckSelection="true"
			        allowRowSelect="true" 
			        allowUnselect="true"   
			        onbeforeload="userLoaded"
			        allowAlternating="true"
					url="${ctxPath}/sys/org/sysOrg/search.do?showDimId=${param['showDimId']}&tenantId=${param['tenantId']}&excludeAdmin=${param['excludeAdmin']}" 
					<c:choose>
						<c:when test="${param['single']=='true'}">
							multiSelect="false"
						</c:when>
						<c:otherwise>
							multiSelect="true" onselect="selectGroup(e)"
						</c:otherwise>
					</c:choose>
				 >
					<div property="columns">
						<div type="checkcolumn" width="40"></div>
						<div field="name" name="name" displayfield="name" width="180" headerAlign="center" allowSort="true">名称</div>
						<div field="key" width="130" headerAlign="center" allowSort="true">标识Key</div>
					</div>
				</div>
			</div>
		</div><!-- end of the region center -->
		
		<c:if test="${param['single']==false}">
		
			<div region="east" title="选中用户组列表"   width="250" showHeader="false" showCollapseButton="false">
				<div class="mini-toolbar mini-toolbar-one" >
				<a class="mini-button btn-red"  onclick="removeSelectedGroup">移除</a>
				<a class="mini-button btn-red"   onclick="clearGroup">清空所有</a>
				</div>
				<div class="mini-fit form-outer4">
					<div id="selectedGroupGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" url="" 
							idField="groupId" multiSelect="true" showColumnsMenu="true" allowAlternating="true" showPager="false" onrowdblclick="removeGroup(e)">
							<div property="columns">
								<div type="checkcolumn" width="30"></div>
								<div field="name" name="name" displayfield="name" width="120" headerAlign="center" allowSort="true">名称</div>
							</div>
					</div>
				</div>
			</div>
		</c:if>
	</div>
	
	<script type="text/javascript">
		mini.parse();
		var groupGrid=mini.get("groupGrid");
		var dimTree=mini.get("dimTree");
		var tenantId="${param['tenantId']}";
		var selectedGroupGrid=mini.get("selectedGroupGrid");
		var dialogUrl = __rootPath + "/sys/org/sysOrg/searchQY.do?showDimId=${param['showDimId']}&tenantId=${param['tenantId']}&excludeAdmin=${param['excludeAdmin']}&config=${param['config']}";

		var url =  __rootPath + "/sys/org/osGradeAdmin/getListRoleByAdminId.do?adminId="+${param['usAdminId']};
		//获取已经拥有的角色
		$.getJSON(url,function callbact(json){
			for(var i=0;json&&i<json.length;i++){
				selectedGroupGrid.addRow(json[i]);
			}
		});

		function onCancel(){
			CloseWindow('cancel');
		}
		
		function onBeforeGridTreeLoad(e){
			
			var tree = e.sender;    //树控件
	        var node = e.node;      //当前节点
	        var params = e.params;  //参数对象

	        //可以传递自定义的属性
	        params.parentId = node.groupId; //后台：request对象获取"myField"
		}
		
		
		loadGroupRootNode();
		
		//当前维度一样时才切换
		function loadGroupRootNode(){
			var nodes=groupGrid.getRootNode().children;
			for(var i=0;i<nodes.length;i++){
				groupGrid.loadNode(nodes[0]);
			}
		}
		
		function onOk(){
			CloseWindow('ok');
		}
		
		var isSingle="${param['single']}";
		if(isSingle=='false'){
			selectedGroupGrid.on("drawcell", function (e) {
	            var record = e.record,
	            
		        field = e.field,
		        value = e.value;
	          
	            if(field=='name'){
	            	e.cellHtml=value+'('+record.key+')';
	            }
			});
		}
		
		function onClear(){
			$("#searchForm")[0].reset();
			groupGrid.setUrl(dialogUrl);
			
		}
		
		function removeSelectedGroup(){
			var rows=selectedGroupGrid.getSelecteds();
			selectedGroupGrid.removeRows(rows,false);
		}
		
		function clearGroup(){
			selectedGroupGrid.clearRows();
		}
		
		function userLoaded(e){
			groupGrid.deselectAll(false);
		}

		
		function selectGroup(e){
			var record=e.record;
			var group=selectedGroupGrid.findRow(function(row){
			    if(row.groupId == record.groupId){
			    	return true;
			    }
			});
			if(group) return;
				
			selectedGroupGrid.addRow($.clone(record));
		}
		
		function removeGroup(e){
			var row=e.row;
			selectedGroupGrid.removeRow(row);
		}
		
		//搜索
		function onSearch(){
			var formData=$("#searchForm").serializeArray();
			var data=jQuery.param(formData);
			groupGrid.setUrl("${ctxPath}/sys/org/sysOrg/searchQY.do?tenantId="+tenantId+"&"+data);
		}

		//返回选择用户信息
		function getGroups(){
			return selectedGroupGrid.getData();
		}

		//获得选择的维度
		function getSelectedDim(){
			var node = dimTree.getSelectedNode();
            return node;
		}
		
		function dimNodeClick(e){
			var node=e.node;
			$("#dimId").val(node.dimId);
			groupGrid.setUrl("${ctxPath}/sys/org/sysOrg/searchQY.do?dimId="+node.dimId+"&tenantId="+tenantId);
			loadGroupRootNode();
		}
		
		
	</script>
</body>
</html>