<%-- 
    Document   : 用户组维度列表页
    Created on : 2015-3-21, 0:11:48
    Author     : csx
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html >
<html >
<head>
<title>用户组维度列表管理</title>
<%@include file="/commons/list.jsp"%>
</head>
<body>
     <div class="mini-toolbar" class="search-form" >
		<div class="searchBox">
			<form id="searchForm"  >
				<ul>
					<li>
						<span class="text">维度名称：</span><input class="mini-textbox" name="Q_NAME__S_LK">
					</li>

					<li>
						<span class="text">状态：</span>
						<input 
							class="mini-combobox" 
							name="Q_STATUS__S_LK"
						    showNullItem="true"  
						    emptyText="请选择..."
							data="[{id:'ENABLED',text:'启用'},{id:'DISABLED',text:'禁用'}]"
						/>
					</li>
					<li class="liBtn">
						<a class="mini-button " onclick="searchForm(this)" >搜索</a>
						<a class="mini-button btn-red" onclick="onClearList(this)">清空搜索</a>
						<span class="unfoldBtn" onclick="no_more(this,'moreBox')">
							<em>展开</em>
							<i class="unfoldIcon"></i>
						</span>
					</li>
				</ul>
				<div id="moreBox">
					<ul>
						<li>
							<span class="text">维度业务主键：</span><input class="mini-textbox" name="Q_DIM_KEY__S_LK">
						</li>
					</ul>
				</div>
			</form>
		</div>
		 <ul class="toolBtnBox search-form" >
			 <li>
				 <a class="mini-button"  onclick="add()">新增</a>
			 </li>
			 <li>
				 <a class="mini-button"  onclick="edit()">编辑</a>
			 </li>
			 <li>
				 <a class="mini-button btn-red"  onclick="remove()">删除</a>
			 </li>
		 </ul>
		 <span class="searchSelectionBtn" onclick="no_search(this ,'searchForm')">
			<i class="icon-sc-lower"></i>
		</span>
     </div>
	<div class="mini-fit rx-grid-fit">
		<div 
			id="datagrid1" 
			class="mini-datagrid" 
			style="width: 100%; height: 100%;" 
			allowResize="false" 
			url="${ctxPath}/sys/org/osDimension/getAllByRight.do" 
			idField="dimId" 
			multiSelect="true" 
			showColumnsMenu="true" 
			sizeList="[5,10,20,50,100,200,500]" 
			pageSize="20" 
			allowAlternating="true" 
			pagerButtons="#pagerButtons"
		>
			<div property="columns">
		<!--  	<div type="checkcolumn" width="20"></div>-->	
				<div name="action" cellCls="actionIcons" width="120"   renderer="onActionRenderer" >操作</div>
				<div field="name" width="140"   allowSort="true" sortField="name_">维度名称</div>
				<div field="dimKey" width="100"   allowSort="true" sortField="DIM_KEY_">维度业务主键</div>
				<div field="status" width="80"   allowSort="true" sortField="STATUS_" renderer="onStatusRenderer">状态</div>
				<div field="isSystem" width="60"   allowSort="true" sortField="IS_SYSTEM_" renderer="onIsSystemRenderer">是否缺省</div>
				<div field="sn" width="60"   allowSort="true" sortField="SN_">排序号</div>
				<div field="showType" width="60"   allowSort="true" sortField="SHOW_TYPE_" renderer="onShowTypeRenderer">数据展示类型</div>
			</div>
		</div>
	</div>
	<script src="${ctxPath}/scripts/common/list.js" type="text/javascript"></script>
	<redxun:gridScript gridId="datagrid1" entityName="com.redxun.sys.org.entity.OsDimension" winHeight="450" winWidth="800" entityTitle="用户组维度" baseUrl="sys/org/osDimension" />
	<script type="text/javascript">
        	//编辑
	        function onActionRenderer(e) {
	            var record = e.record;
	            var pk = record.pkId;
	            var s = '<span  title="明细" onclick="detailRow(\'' + pk + '\')">明细</span>'
	            		+ ' <span  title="等级管理" onclick="manageDimRank(\'' + pk + '\')">等级管理</span>'
	            		+ ' <span title="维度关系定义" onclick="dimRelationMgr(\'' + record._uid + '\')">维度关系定义</span>';
	            if("${isSuperAdmin}"=="true"){
	            	s+=' <span  title="维度权限" onclick="DimRight(\'' + pk + '\')">维度权限</span>';
	            }
	            if(record.isSystem!='YES'){
	            	s = s + ' <span  title="编辑" onclick="editRow(\'' + pk + '\')">编辑</span>'
	            	+ ' <span  title="删除" onclick="delRow(\'' + pk + '\')">删除</span>';
	            }
	            return s;
	        }
        	
	        function manageDimRank(pkId){
	        	var row=grid.getSelected();
	        	if(!row) return;
	        	_OpenWindow({
	        		url:"${ctxPath}/sys/org/osRankType/list.do?dimId="+row.pkId,
	        		title:row.name + "-等级管理",
	        		width:600,
	        		height:350
	        	});
	        }
	        
	        function dimRelationMgr(uid){
	        	var row=grid.getRowByUID(uid);
	        	
	        	top['index'].showTabFromPage({
        			tabId:'dimRel_'+row.dimId,
        			title:row.name+'-维度关系管理',
        			url:__rootPath+'/sys/org/osDimension/relsMgr.do?dimId='+row.dimId,
        		});
	        	
	        }
	        
	        function rowEditAllow(row){
	        	return row.isSystem!='YES'?true:false;
	        }
	        
	        function rowGetAllow(row){
	        	return row.isSystem!='YES'?true:false;
	        }
	        
	        function rowRemoveAllow(){
	        	var rows=grid.getSelecteds();
	        	var allow=true;
	        	for(var i=0;i<rows.length;i++){
	        		if(rows[i].isSystem=='YES'){
	        			allow=false;
	        			break;
	        		}
	        	}
	        	if(!allow){
	        		alert("默认的系统记录不允许删除！");
	        	}
	        	return allow;
	        }
	        
	        function DimRight(pkId){
	        	var row=grid.getSelected();
	        	_OpenWindow({
	        		url:"${ctxPath}/sys/org/osDimensionRight/edit.do?dimId="+row.pkId,
	        		title:"维度授权",
	        		width:1024,
	        		height:370
	        	});
	        }
	        
	  	  function onIsSystemRenderer(e) {
	            var record = e.record;
	            var isSystem = record.isSystem;
	             var arr = [{'key' : 'YES', 'value' : '是','css' : 'green'}, 
	    			        {'key' : 'NO','value' : '否','css' : 'red'} ];
	    			return $.formatItemValue(arr,isSystem);
	        }
	        
	        function onStatusRenderer(e) {
	            var record = e.record;
	            var status = record.status;
	           var arr = [ {'key' : 'ENABLED','value' : '启用','css' : 'green'}, 
	   		               {'key' : 'DISABLED','value' : '禁用','css' : 'red'} ];
	   		return $.formatItemValue(arr,status);
	        }
	        
	        function onShowTypeRenderer(e) {
	        	var record = e.record;
	        	var showType = record.showType;
	        	var arr =[
	        		 {'key' : 'TREE','value' : '树形','css' : 'green'}, 
 		             {'key' : 'FLAT','value' : '平铺','css' : 'blue'}];
	        	return $.formatItemValue(arr,showType);
	        }
        </script>
</body>
</html>