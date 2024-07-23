<%--
    Document   : 用户列表页
    Created on : 2015-3-21, 0:11:48
    Author     : csx
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html >
<html>
<head>
	<title>监控列表管理</title>
	<%@include file="/commons/list.jsp"%>
</head>
<body>

<div class="topToolBar">
	<div>
		<span class="text">用户编码:</span><input class="mini-textbox" id="userNo" name="userNo">
		<span class="text">用户名称:</span><input class="mini-textbox" id="fullname" name="fullname">
		<a class="mini-button"  plain="true" onclick="getLoginList()">查询</a>
		<a class="mini-button"  onclick="refresh()">刷新</a>
		<a class="mini-button btn-red"  onclick="clearForm()">清空查询</a>
		<a class="mini-button btn-red"  onclick="removeUserList()">终止用户</a>
	</div>
</div>

<div style="overflow: hidden;height: 120px;">
	<div id="datagrid1" class="mini-datagrid"
		 allowResize="false"
		 url="${ctxPath}/sys/org/loginSessionUser/getLoginCount.do"
		 idField="registerId"
		 multiSelect="true"
		 showColumnsMenu="true"
		 sizeList="[5,10,20,50,100,200,500]"
		 pageSize="10"
		 showPager="false"
		 allowAlternating="true">
		<div property="columns">
			<div field="controlType" width="100" allowSort="true" headerAlign="center"
				 align="center" sortField="controlType">授权类型</div>
			<div field="controlNum" width="100" allowSort="true" headerAlign="center"
				 align="center" sortField="controlNum">授权数</div>
			<div field="occupyNum" width="100" allowSort="true" headerAlign="center"
				 align="center" sortField="occupyNum">占用数</div>
			<div field="remainderNum" width="100" allowSort="true" headerAlign="center"
				 align="center" sortField="remainderNum">剩余数</div>
			<div field="occupancyRate" width="100" allowSort="true" headerAlign="center"
				 align="center" sortField="occupancyRate">占用率</div>
		</div>
	</div>
</div>

<div class="mini-fit">
	<div id="datagrid2" class="mini-datagrid" style="margin-top: 20px;"
		 showHeader="true" title="登陆用户"
		 url="${ctxPath}/sys/org/loginSessionUser/getLoginList.do" idField="id"
		 allowResize="false"
		 idField="userId"
		 multiSelect="true"
		 showColumnsMenu="true"
		 sizeList="[5,10,20,50,100,200,500]"
		 pageSize="10"
		 allowAlternating="true"
		 pagerButtons="#pagerButtons">
		<div property="columns">
			<div type="checkcolumn" width="20"></div>
			<div name="action" cellCls="actionIcons" width="50" headerAlign="center" align="center" renderer="onActionRenderer" cellStyle="padding:0;">#</div>
			<div field="userNo"  sortField="USER_NO_"  width="120" headerAlign="center" allowSort="true">用户编码</div>
			<div field="fullname"  sortField="FULLNAME_"  width="120" headerAlign="center" allowSort="true">用户名称</div>
			<div field="sessionIp"  sortField="SESSION_IP_"  width="120" headerAlign="center" allowSort="true">用户ip地址</div>
			<div field="orgGroup"  sortField="orgGroup"  width="120" headerAlign="center" allowSort="true">登陆集团</div>
			<div field="userOrgGroup"  sortField="userOrgGroup"  width="120" headerAlign="center" allowSort="true">所属组织</div>
			<div field="loginFirstTime"  sortField="LOGIN_FIRST_TIME"  width="120" headerAlign="center" allowSort="true" dateFormat="yyyy-MM-dd HH:mm:ss">用户登陆时间</div>
		</div>
	</div>
</div>
<script src="${ctxPath}/scripts/common/list.js" type="text/javascript"></script>
<redxun:gridScript gridId="datagrid1" entityName="com.redxun.sys.org.entity.Monitor"
				   winHeight="450" winWidth="700" entityTitle="监控实例" baseUrl="/sys/org/monitor" />
<redxun:gridScript gridId="datagrid2" entityName="com.redxun.sys.org.entity.OsUser"
				   tenantId="${param['tenantId']}" entityTitle="用户" baseUrl="sys/org/osUser" />
<script type="text/javascript">
	mini.parse();
	var datagrid1 = mini.get('#datagrid1');
	var datagrid2 = mini.get('#datagrid2');
	var userNo =mini.get('userNo');
	var fullname =mini.get('fullname');
	//行功能按钮
	function onActionRenderer(e) {
		var record = e.record;
		var pkId = record.pkId;
		var s = '<span  title="终止用户" onclick="removeUser(\'' + pkId + '\')">终止用户</span>';
		return s;
	}
	function getLoginList() {
		var url=__rootPath+"/sys/org/loginSessionUser/getLoginList.do?tenantId=1";
		if(userNo.value){
			url+="&userNo="+userNo.value;
		}
		if(fullname.value){
			url+="&fullname="+fullname.value;
		}
		$.getJSON(url,function callbact(json){
			datagrid2.setData(json.data);
		});
	}
	/**
	 *清空查询
	 */
	function clearForm(){
		userNo.setValue("");
		fullname.setValue("");
		datagrid2.setUrl(__rootPath+"/sys/org/loginSessionUser/getLoginList.do");
		datagrid2.load();
	}

	//刷新
	function refresh() {
		userNo.setValue("");
		fullname.setValue("");
		refresh_end();
	}
	function refresh_end() {
		datagrid1.setUrl(__rootPath+"/sys/org/loginSessionUser/getLoginCount.do");
		datagrid1.load();
		datagrid2.setUrl(__rootPath+"/sys/org/loginSessionUser/getLoginList.do");
		datagrid2.load();
	}

	/**
	 * 终止用户
	 */
	function removeUser(pkId) {
		var pkIdsList =pkId;
		function callback(ok){
			if("ok"==ok){
				commit(pkIdsList);
			}
		}
		mini.confirm("即将终止所选用户，是否继续？", "终止用户", callback);
	}

	/**
	 * 终止用户
	 */
	function removeUserList() {
		var userList =datagrid2.getSelecteds();
		if(!userList ||userList.length==0){
			return;
		}
		var pkIdsList ="";
		for(var i=0;i<userList.length;i++){
			if(!pkIdsList){
				pkIdsList=userList[i].id
			}else {
				pkIdsList+=","+userList[i].id;
			}
		}
		function callback(ok){
			if("ok"==ok){
				commit(pkIdsList);
			}
		}
		mini.confirm("即将终止所选用户，是否继续？", "终止用户", callback);
	}

	function commit(pkIdsList) {
		var data={};
		data.pkIds=pkIdsList
		_SubmitJson({
			method : 'POST',
			url:__rootPath+'/sys/org/loginSessionUser/removeLogUser.do',
			data:data,
			success:function(text){
				refresh_end();
			}
		});
	}

</script>
</body>
</html>