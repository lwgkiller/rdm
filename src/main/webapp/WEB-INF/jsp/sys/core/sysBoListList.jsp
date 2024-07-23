<%-- 
    Document   : [单据数据列表]列表页
    Created on : 2017-05-21 12:11:18
    Author     : mansan
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
<title>[单据数据列表]列表管理</title>
<%@include file="/commons/list.jsp"%>

</head>
<body>
	<ul id="treeMenu" class="mini-contextmenu" >
		<li   onclick="addNodeList()">新增分类</li>
	    <li  onclick="editNodeList()">编辑分类</li>
	    <li  class=" btn-red" onclick="delNode">删除分类</li>
	</ul>
	<div id="layout1" class="mini-layout" style="width:100%;height:100%;">
	    <div
	    	title="列表分类"
	    	region="west"
	    	width="190"
	    	showSplitIcon="true"
	    	showCollapseButton="false"
	    	showProxy="false"
	    	class="layout-border-r"
    	>
	        <div class="treeToolBar">
				<a class="mini-button"   plain="true" onclick="addNodeList()">新增</a>
                <a class="mini-button"  plain="true" onclick="refreshSysTree()">刷新</a>
	        </div>
	        <div class="mini-fit">
		         <ul
		         	id="systree"
		         	class="mini-tree"
		         	url="${ctxPath}/sys/core/sysTree/listByCatKey.do?catKey=CAT_BO_LIST"
		         	style="width:100%;"
					showTreeIcon="true"
					textField="name"
					idField="treeId"
					resultAsTree="false"
					parentField="parentId"
					expandOnLoad="false"
	                onnodeclick="treeNodeClick"
	                contextMenu="#treeMenu">
            </ul>
            </div>
	    </div>
		<div region="center" showHeader="false" showCollapseButton="false" title="业务查询列表">
	     <div class="mini-toolbar" >
	     	 <ul id="popupAddMenu" class="mini-menu" style="display:none;">
			    <li  onclick="doExport(false)">导出选中</li>
			    <li  onclick="doExport(true)">导出全部</li>
	         </ul>
			<div class="searchBox">
				<form id="searchForm" class="search-form" >
					<ul>
						<li class="liAuto">
							<span class="text">名称：</span><input class="mini-textbox" name="Q_NAME__S_LK">
						</li>
						<li>
							<span class="text">标识键：</span><input class="mini-textbox" name="Q_KEY__S_LK">
						</li>
						<li class="liBtn">
							<a class="mini-button " onclick="searchFrm()">搜索</a>
							<a class="mini-button " onclick="clearForm()">清空</a>
						</li>
					</ul>
				</form>
			</div>
			 <ul class="toolBtnBox">
				 <li>
					 <a class="mini-button"  plain="true" onclick="addBoList">新增</a>
				 </li>
				 <li>
					 <a class="mini-button"  plain="true" onclick="edit(true)">编辑</a>
				 </li>
				 <li>
					 <a class="mini-menubutton"  plain="true" menu="#popupAddMenu">导出</a>
				 </li>
				 <li>
					 <a class="mini-button"  onclick="doImport">导入</a>
				 </li>
				 <li>
					 <a class="mini-button btn-red"  plain="true" onclick="remove()">删除</a>
				 </li>
			 </ul>
			 <span class="searchSelectionBtn" onclick="no_search(this ,'searchForm')">
				<i class="icon-sc-lower"></i>
			</span>
	    </div>
			<div class="mini-fit ">
				<div
					id="datagrid1"
					class="mini-datagrid"
					style="width: 100%; height: 100%;"
					allowResize="false"
					url="${ctxPath}/sys/core/sysBoList/listData.do"
					idField="id"
					multiSelect="true"
					showColumnsMenu="true"
					sizeList="[5,10,20,50,100,200,500]"
					pageSize="20"
					allowAlternating="true"
				>
					<div property="columns">
						<div type="checkcolumn" width="20"  headerAlign="center" align="center" ></div>
						<div name="action" cellCls="actionIcons" width="100" headerAlign="" align="" renderer="onActionRenderer" cellStyle="padding:0;">操作</div>
						<div field="name"  sortField="NAME_"  width="160" headerAlign="" renderer="onNameActionRenderer" allowSort="true">名称</div>
						<div field="key"  sortField="KEY_"  width="120" headerAlign="" allowSort="true">标识键</div>
						<div field="isLeftTree"  sortField="IS_LEFT_TREE_"  width="60" headerAlign="" renderer="onRenderer" allowSort="true">是否显示左树</div>
						<div field="createTime" dateformat="yyyy-MM-dd"  sortField="CREATE_TIME_"  width="80" headerAlign="" allowSort="true">创建时间</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<script type="text/javascript">
		//行功能按钮
		function onActionRenderer(e) {
			var record = e.record;
			var pkId = record.pkId;
			var uid=record._uid;
			var s = '<span  title="编辑" onclick="editRow(\'' + pkId + '\',true)">编辑</span>'
				+'<span class=" btn-red" title="删除" onclick="delRow(\'' + pkId + '\')">删除</span>';
				if(record.isGen=='YES'){
					s+= ' <span class="" title="预览"  onclick="preview(\'' + uid + '\')">预览</span>';
					s+= ' <span class="" title="编辑代码"  onclick="editHtml(\'' + uid + '\')">编辑代码</span>';
					s+= ' <span class="" title="发布菜单"  onclick="deployMenu(\'' + uid + '\')">发布菜单</span>';
				}
			return s;
		}

		function onNameActionRenderer(e) {
			var record = e.record;
			var pkId = record.pkId;
			var uid=record._uid;
			var name = record.name;
			return '<span title="' + name + '"  onclick="preview(\'' + uid + '\')">' + name + '</span>';
		}

	</script>
	<redxun:gridScript gridId="datagrid1" entityName="com.redxun.sys.core.entity.SysBoList" winHeight="450"
		winWidth="700" entityTitle="单据数据列表" baseUrl="sys/core/sysBoList" />
	<script type="text/javascript">
		//添加bo列表
		function addBoList(){
			_OpenWindow({
				title:'单据数据列表',
				width:800,
				height:450,
				max:true,
				url:__rootPath+'/sys/core/sysBoList/edit.do'
			});
		}

		function preview(uid){
			var row=grid.getRowByUID(uid);
			var url=__rootPath+'/sys/core/sysBoList/'+row.key+'/list.do';
			_OpenWindow({
				title: row.name+'-预览',
				max:true,
				url:url,
				height:500,
				width:800
			});
		}

		function editHtml(uid){
			var row=grid.getRowByUID(uid);
			var url=__rootPath+'/sys/core/sysBoList/edit3.do?id='+row.id;
			_OpenWindow({
				title: row.name+'-代码',
				max:true,
				url:url,
				height:500,
				width:800
			});
		}

		function deployMenu(uid){
			var row=grid.getRowByUID(uid);
			var id=row.id;
			var url='/sys/core/sysBoList/'+row.key+'/list.do';
			openDeploymenuDialog({name:row.name,key:row.key,url:url,boListId:id,showMobileIcon:true});
		}

		function onRenderer(e) {
            var record = e.record;
            var val = record[e.field];
            var arr = [ {'key' : 'YES', 'value' : '是','css' : 'green'},
			            {'key' : 'NO','value' : '否','css' : 'red'} ];
			return $.formatItemValue(arr,val);
        }
		function addNodeList(e){
			addNode('新增列表分类','CAT_BO_LIST');
	   	}
	   	function refreshSysTree(){
	   		var systree=mini.get("systree");
	   		systree.load();
	   	}
	   	function editNodeList(e){
			editNode('编辑节点');
	   	}

	   	//按分类树查找数据字典
	   	function treeNodeClick(e){
	   		var node=e.node;
	   		grid.setUrl(__rootPath+'/sys/core/sysBoList/listData.do?treeId='+node.treeId);
	   		grid.load();
	   	}

	   	/**
	   	*导出
	   	**/
	   	function doExport(flag){
	   		var rows=grid.getSelecteds();
	   		if(rows.length==0 && !flag){
	   			alert('请选择需要导出的对话框记录！')
	   			return;
	   		}
	   		if(flag){
	   			rows = grid.getData();
	   		}
	   		var ids=_GetKeys(rows);
	   		jQuery.download(__rootPath+'/sys/core/sysBoList/doExport.do?boKeys='+ids,{isDialog:true},'post');
	   	}

	   	/**
	   	 * 获得表格的行的主键key列表，并且用',’分割
	   	 * @param rows
	   	 * @returns
	   	 */
	   	function _GetKeys(rows){
	   		var ids=[];
	   		for(var i=0;i<rows.length;i++){
	   			ids.push(rows[i].key);
	   		}
	   		return ids.join(',');
	   	}
	   	/**
	   	*导入
	   	**/
	   	function doImport(){
	   		_OpenWindow({
	   			title:'对话框导入',
	   			url:__rootPath+'/sys/core/sysBoList/import1.do',
	   			height:350,
	   			width:600,
	   			ondestroy:function(action){
	   				grid.reload();
	   			}
	   		});
	   	}
	</script>
</body>
</html>