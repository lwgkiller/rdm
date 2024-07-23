<%-- 
    Document   : [表单权限]列表页
    Created on : 2018-02-09 15:54:25
    Author     : ray
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
<title>[表单权限]列表管理</title>
<%@include file="/commons/list.jsp"%>
</head>
<body>
	 <div class="mini-toolbar" >
		 <div class="searchBox">
			 <form id="searchForm" class="search-form" >
				 <ul>
					 <li><span>方案ID:</span><input class="mini-textbox" name="Q_SOL_ID__S_LK"></li>
					 <li><span>流程定义ID:</span><input class="mini-textbox" name="Q_ACT_DEF_ID__S_LK"></li>
					 <li class="liBtn">
						 <a class="mini-button"   plain="true" onclick="searchFrm()">查询</a>
						 <a class="mini-button btn-red"   plain="true" onclick="clearForm()">清空查询</a>
						<span class="unfoldBtn" onclick="no_more(this,'moreBox')">
							<em>展开</em>
							<i class="unfoldIcon"></i>
						</span>
					 </li>
				 </ul>
				 <div id="moreBox">
					 <ul>
						 <li><span>节点ID:</span><input class="mini-textbox" name="Q_NODE_ID__S_LK"></li>
						 <li><span>表单别名:</span><input class="mini-textbox" name="Q_FORM_ALIAS__S_LK"></li>
						 <li><span>权限JSON:</span><input class="mini-textbox" name="Q_JSON__S_LK"></li>
					 </ul>
				 </div>
			 </form>
		 </div>
		 <ul class="toolBtnBox">
			 <li> <a class="mini-button" iconCls="icon-create" plain="true" onclick="add()">增加</a></li>
			 <li> <a class="mini-button" iconCls="icon-edit" plain="true" onclick="edit()">编辑</a></li>
			 <li> <a class="mini-button btn-red" iconCls="icon-remove" plain="true" onclick="remove()">删除</a></li>
		 </ul>
	 </div>
	
	<div class="mini-fit" style="height: 100%;">
		<div id="datagrid1" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
			url="${ctxPath}/bpm/core/bpmFormRight/listData.do" idField="id"
			multiSelect="true" showColumnsMenu="true" sizeList="[5,10,20,50,100,200,500]" pageSize="20" allowAlternating="true" pagerButtons="#pagerButtons">
			<div property="columns">
				<div type="checkcolumn" width="20" headerAlign="center" align="center"></div>
				<div name="action" cellCls="actionIcons" width="100"  renderer="onActionRenderer" cellStyle="padding:0;">操作</div>
				<div field="solId"  sortField="SOL_ID_"  width="120"  allowSort="true">方案ID</div>
				<div field="actDefId"  sortField="ACT_DEF_ID_"  width="120"  allowSort="true">流程定义ID</div>
				<div field="nodeId"  sortField="NODE_ID_"  width="120"  allowSort="true">节点ID</div>
				<div field="formAlias"  sortField="FORM_ALIAS_"  width="120"  allowSort="true">表单别名</div>
				<div field="json"  sortField="JSON_"  width="120"  allowSort="true">权限JSON</div>
			</div>
		</div>
	</div>

	<script type="text/javascript">
		//行功能按钮
		function onActionRenderer(e) {
			var record = e.record;
			var pkId = record.pkId;
			var s = '<span  title="明细" onclick="detailRow(\'' + pkId + '\')">明细</span>'
					+'<span  title="编辑" onclick="editRow(\'' + pkId + '\',true)">编辑</span>'
					+'<span  title="删除" onclick="delRow(\'' + pkId + '\')">删除</span>';
			return s;
		}
	</script>
	<redxun:gridScript gridId="datagrid1" entityName="com.redxun.bpm.core.entity.BpmFormRight" winHeight="450"
		winWidth="700" entityTitle="表单权限" baseUrl="bpm/core/bpmFormRight" />
</body>
</html>