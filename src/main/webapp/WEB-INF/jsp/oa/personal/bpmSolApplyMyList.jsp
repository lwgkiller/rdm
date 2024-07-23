<%-- 
    Document   : 我的流程事项申请列表页
    Created on : 2015-3-21, 0:11:48
    Author     : csx
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html >
<html >
<head>
	<title>我的流程事项申请列表</title>
	<%@include file="/commons/list.jsp"%>
	<script type="text/javascript" src="${ctxPath}/scripts/form/customFormUtil.js"></script>
	<style type="text/css">
		.mini-layout-border>#center{
			background: transparent;
		}
	</style>
</head>
<body>
<div id="layout1" class="mini-layout" style="width: 100%; height: 100%;">
	<div
			title="业务方案分类"
			region="west"
			width="180"
			showSplitIcon="true"
			showCollapseButton="false"
			showProxy="false"
	>
		<div class="mini-fit">
			<ul
					id="systree"
					class="mini-tree"
					url="${ctxPath}/bpm/core/bpmSolution/getCatTree.do?isAdmin=false"
					style="width: 100%;height:100%"
					showTreeIcon="true"
					textField="name"
					idField="treeId"
					resultAsTree="false"
					parentField="parentId"
					expandOnLoad="true"
					onnodeclick="treeNodeClick"
			>
			</ul>
		</div>
	</div>
	<div showHeader="false" showCollapseButton="false">
		<div class=" mini-toolbar">
			<div class="searchBox">
				<form id="searchForm" class="search-form">
					<ul>
						<li class="liAuto">
							<span class="text">标识键：</span><input class="mini-textbox" name="Q_KEY__S_LK"  />
						</li>
						<li class="liAuto">
							<span class="text">方案：</span><input class="mini-textbox" name="Q_NAME__S_LK"  />
						</li>
						<li class="liBtn">
							<a class="mini-button"  plain="true" onclick="searchFrm()">查询</a>
							<a class="mini-button btn-red"  plain="true" onclick="clearForm()">清空</a>
						</li>
					</ul>
				</form>
			</div>
			<span class="searchSelectionBtn" onclick="no_search(this ,'searchForm')">
						<i class="icon-sc-lower"></i>
					</span>
		</div>
		<div class="mini-fit">
			<div
					id="datagrid1"
					class="mini-datagrid"
					style="width:100%;height:100%;"
					allowResize="false"
					url="${ctxPath}/oa/personal/bpmSolApply/mySolutions.do"
					idField="solId"
					multiSelect="true"
					showColumnsMenu="true"
					sizeList="[5,10,20,50,100,200,500]"
					pageSize="20"
					allowAlternating="true"
			>
				<div property="columns">
					<div type="checkcolumn" width="20"></div>
					<div type="indexcolumn" width="20"  headerAlign=""  align="left">序号</div>
					<div name="action" cellCls="actionIcons" width="80"  renderer="onActionRenderer" cellStyle="padding:0;">操作</div>
					<div field="name" width="140" sortField="NAME_" headerAlign="" allowSort="true">方案名称</div>
					<div field="key" width="100" sortField="KEY_" headerAlign="" allowSort="true">标识键</div>
					<div
							field="createTime"
							sortField="CREATE_TIME_"
							dateFormat="yyyy-MM-dd HH:mm:ss"
							width="80"
							headerAlign="center"
							allowSort="true"
					>创建时间</div>
				</div>
			</div>
		</div>
	</div>
</div>

<script src="${ctxPath}/scripts/common/list.js" type="text/javascript"></script>
<redxun:gridScript
		gridId="datagrid1"
		entityName="com.redxun.bpm.core.entity.BpmSolution"
		winHeight="450"
		winWidth="780"
		entityTitle="业务流程解决方案"
		baseUrl="bpm/core/bpmSolution"
/>
<script type="text/javascript">
	//var grid=mini.get('datagrid1');
	grid.on("drawcell", function (e) {
		var record = e.record;
		var field= e.field;
		if(field=='name'){
			e.cellHtml= '<a href="javascript:startRow(\'' + record._uid + '\')">'+record.name+'</a>';
		}
	});

	function onSearch(){
		var form=new mini.Form('searchForm');
		grid.load(form.getData());
	}

	function clearSearch(){
		var form=new mini.Form('searchForm');
		form.clear();
		grid.setUrl('${ctxPath}/oa/personal/bpmSolApply/mySolutions.do');
		grid.load();
	}
/*行功能按钮*/
	function onActionRenderer(e) {
		var record = e.record;
		var uid = record.pkId;
		var  s = '<span  title="启动" onclick="startRow(\''+  record._uid + '\')">启动</span>';
		return s;
	}

	function treeNodeClick(e) {
		var node = e.node;
		grid.setUrl(__rootPath + '/oa/personal/bpmSolApply/mySolutions.do?treeId='+ node.treeId);
		grid.load();
	}
</script>
</body>
</html>