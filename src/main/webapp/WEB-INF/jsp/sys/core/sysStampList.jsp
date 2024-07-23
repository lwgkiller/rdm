<%-- 
    Document   : [office印章]列表页
    Created on : 2018-02-01 09:41:39
    Author     : ray
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
<title>[office印章]列表管理</title>
<%@include file="/commons/list.jsp"%>
</head>
<body>
	 <div class="mini-toolbar" >
         <table style="width:100%;">
             <tr>
                 <td style="width:100%;">
                     <a class="mini-button" iconCls="icon-create" plain="true" onclick="add()">增加</a>
                     <a class="mini-button" iconCls="icon-edit" plain="true" onclick="edit()">编辑</a>
                     <a class="mini-button btn-red" iconCls="icon-remove" plain="true" onclick="remove()">删除</a>
                     <a class="mini-button"   plain="true" onclick="searchFrm()">查询</a>
                     <a class="mini-button btn-red"   plain="true" onclick="clearForm()">清空查询</a>
                 </td>
             </tr>
              <tr>
                  <td class="search-form" >
                     <ul>
						<li><span class="text">签章名称:</span><input class="mini-textbox" name="Q_NAME__S_LK"></li>
						<li><span class="text">签章用户:</span><input class="mini-textbox" name="Q_SIGN_USER__S_LK"></li>
					</ul>
                  </td>
              </tr>
         </table>
     </div>
	
	<div class="mini-fit" style="height: 100%;">
		<div id="datagrid1" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
			url="${ctxPath}/sys/core/sysStamp/listData.do" idField="id"
			multiSelect="true" showColumnsMenu="true" sizeList="[5,10,20,50,100,200,500]" pageSize="20" allowAlternating="true" pagerButtons="#pagerButtons">
			<div property="columns">
				<div type="checkcolumn" width="20"></div>
				<div name="action" cellCls="actionIcons" width="50" headerAlign="center" align="center" renderer="onActionRenderer" cellStyle="padding:0;">#</div>
				<div field="name"  sortField="NAME_"  width="120" headerAlign="center" allowSort="true">签章名称</div>
				<div field="signUser"  sortField="SIGN_USER_"  width="120" headerAlign="center" allowSort="true">签章用户</div>
				<div field="description"  sortField="DESCRIPTION_"  width="120" headerAlign="center" allowSort="true">描述</div>
			</div>
		</div>
	</div>

	<script type="text/javascript">
		//行功能按钮
		function onActionRenderer(e) {
			var record = e.record;
			var pkId = record.pkId;
			var s = '<span  title="编辑" onclick="editRow(\'' + pkId + '\',true)"></span>'
					+'<span  title="删除" onclick="delRow(\'' + pkId + '\')"></span>';
			return s;
		}
	</script>
	<redxun:gridScript gridId="datagrid1" entityName="com.redxun.sys.core.entity.SysStamp" winHeight="450"
		winWidth="700" entityTitle="office印章" baseUrl="sys/core/sysStamp" />
</body>
</html>