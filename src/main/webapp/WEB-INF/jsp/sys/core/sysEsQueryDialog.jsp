<%-- 
    Document   : [ES自定义查询]列表页
    Created on : 2018-11-28 14:21:52
    Author     : ray
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
<title>[ES自定义查询]列表管理</title>
<%@include file="/commons/list.jsp"%>
<script type="text/javascript" src="${ctxPath }/scripts/sys/customform/sysCustomEs.js"></script>
</head>
<body>
	 <div class="titleBar mini-toolbar" >
	 	<ul>
			<li>
                 <a class="mini-button"   plain="true" onclick="searchFrm()">查询</a>
                 <a class="mini-button btn-red"   plain="true" onclick="clearForm()">清空查询</a>
			</li>
			<li class="clearfix"></li>
		</ul>
	 	<div class="searchBox">
			<form id="searchForm" class="search-form" >						
				<ul>
						<li><span class="text">名称:</span><input class="mini-textbox" name="Q_NAME__S_LK"></li>
						<li><span class="text">别名:</span><input class="mini-textbox" name="Q_ALIAS__S_LK"></li>
					
					<li class="clearfix"></li>
				</ul>
			</form>	
			<span class="searchSelectionBtn" onclick="no_search(this ,'searchForm')">
				<i class="icon-sc-lower"></i>
			</span>
		</div>
     </div>
	<div class="mini-fit" style="height: 100%;">
		<div id="datagrid1" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
			url="${ctxPath}/sys/core/sysEsQuery/listData.do" idField="id"
			multiSelect="true" showColumnsMenu="true" sizeList="[5,10,20,50,100,200,500]" pageSize="20" allowAlternating="true" pagerButtons="#pagerButtons">
			<div property="columns">
				<div type="checkcolumn" width="20"></div>
				<div field="name"  sortField="NAME_"  width="120" headerAlign="center" allowSort="true">名称</div>
				<div field="alias"  sortField="ALIAS_"  width="120" headerAlign="center" allowSort="true">别名</div>
				<div field="queryType" renderer="onQueryTypeRenderer" sortField="QUERY_TYPE_"  width="120" headerAlign="center" allowSort="true">查询类型</div>
				<div field="needPage" renderer="onNeedPageRenderer"  sortField="NEED_PAGE_"  width="120" headerAlign="center" allowSort="true">是否分页</div>
			</div>
		</div>
	</div>
	
	<div class="mini-toolbar" style="text-align: center; padding-top: 8px; padding-bottom: 8px;" borderStyle="border:0;">
		<a   class="mini-button"  onclick="onOk()">确定</a>
		<a   class="mini-button" onclick="onCancel()">取消</a>
	</div>

	<script type="text/javascript">
		
		function onQueryTypeRenderer(e){
			 var record = e.record;
	            var queryType = record.queryType;
	            var arr = [ {'key' : 1, 'value' : '基于配置','css' : 'green'}, 
				            {'key' : 2,'value' : 'SQL','css' : 'orange'} ];
				return $.formatItemValue(arr,queryType);
			
		}
		
		function onNeedPageRenderer(e){
		 	var record = e.record;
            var needPage = record.needPage;
            var arr = [ {'key' : 1, 'value' : '是','css' : 'green'}, 
			            {'key' : 2,'value' : '否','css' : 'orange'} ];
			return $.formatItemValue(arr,needPage);
			
		}
		
		function getData() {
			var grid=mini.get("datagrid1");
			var row = grid.getSelected();
			return row;
		}
		
		function onOk() {
			CloseWindow("ok");
		}
		
		function onCancel() {
			CloseWindow("cancel");
		}
	</script>
	<redxun:gridScript gridId="datagrid1" entityName="com.redxun.sys.core.entity.SysEsQuery" winHeight="450"
		winWidth="700" entityTitle="ES自定义查询" baseUrl="sys/core/sysEsQuery" />
</body>
</html>