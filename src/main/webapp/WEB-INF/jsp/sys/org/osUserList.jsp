<%--
    Document   : 用户列表页
    Created on : 2015-3-21, 0:11:48
    Author     : csx
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html >
<html>
<head>
	<title>用户列表管理</title>
	<%@include file="/commons/list.jsp"%>
</head>
<body>
<div id="layout1" class="mini-layout" style="width: 100%; height: 100%;">
	<div
		title="用户组维度"
	    showProxyText="true"
		region="west"
		width="180"
		expanded="true"
		showSplitIcon="false"
		showCollapseButton="false"
	>
		<div class="mini-fit">
			<ul id="leftTree" class="mini-tree" url="" showTreeIcon="true"
				textField="name"  expandOnLoad="false"
				style="width: 100%; height: 100%;" onnodeclick="groupNodeClick"
				onbeforeload="loadSubChildren">
			</ul>
		</div>
	</div>
	<div title="center" region="center">
		<div class="mini-toolbar">


			<div class="searchBox">
				<form id="searchForm" class="search-form">
					<ul>
						<li><span class="text">用户姓名：</span><input class="mini-textbox" name="Q_FULLNAME__S_LK"></li>
						<li><span class="text">账号：</span><input class="mini-textbox" name="Q_USER_NO__S_LK"></li>
						<li class="liBtn">
							<a class="mini-button " onclick="searchForm(this)">搜索</a>
							<a class="mini-button  btn-red" onclick="clearForm(this)">清空搜索</a>
							<span class="unfoldBtn" onclick="no_more(this,'moreBox')">
								<em>展开</em>
								<i class="unfoldIcon"></i>
							</span>
						</li>
					</ul>
					<div id="moreBox">
						<ul>
							<li><span class="text">性别：</span>
								<input class="mini-combobox" name="Q_SEX__S_EQ" showNullItem="true" emptyText="请选择..."  data="[{id:'Male',text:'男'},{id:'Female',text:'女'}]" /></li>
							<li><span class="text">类型：</span>
								<input  name="Q_USER_TYPE__S_EQ"
										class="mini-combobox"
										url="${ctxPath}/sys/org/osUserType/getAllTypes.do?tenantId=${param.tenantId}"
										emptyText="请选择..."
										showNullItem="true" valueField="code" textField="name" value="${osUser.userType}"/>
							</li>
						</ul>
					</div>
				</form>
			</div>
			<ul class="toolBtnBox">
				<li><a class="mini-button"  onclick="add()">新增</a></li>
				<li><a class="mini-button"   onclick="edit(true)">编辑</a></li>
				<li><a class="mini-button btn-red"   onclick="remove()">删除</a></li>
				<li><a class="mini-button "   onclick="openImportDialog()">导入用户</a></li>
				<li><c:if test="${isWxEanble}">
					<a class="mini-menubutton"  menu="#popupMenu">同步用户</a>
					<ul id="popupMenu" class="mini-menu" style="display: none;">
						<li onclick="syncOrgUser">同步所有</li>
						<li onclick="syncChoiceOrgUser">同步选中</li>
					</ul>
				</c:if></li>

			</ul>
			<span class="searchSelectionBtn" onclick="no_search(this ,'searchForm')"> <i class="icon-sc-lower"></i></span>
		</div>
		<div class="mini-fit ">
			<div id="datagrid1" class="mini-datagrid"
				 style="width: 100%; height: 100%;"
				 url="${ctxPath}/sys/org/osUser/listByGroupIdRelTypeId.do?relTypeId=1&tenantId=${param['tenantId']}"
				 allowResize="false" idField="userId" multiSelect="true"
				 showColumnsMenu="true" sizeList="[5,10,20,50,100,200,500]"
				 pageSize="20" allowAlternating="true" pagerButtons="#pagerButtons">
				<div property="columns">
					<div type="checkcolumn" width="20" headerAlign="center" align="center"></div>
					<div name="action" cellCls="actionIcons" width="130"
						  renderer="onActionRenderer"
						 cellStyle="padding:0;">操作</div>
					<div field="fullname" width="100" allowSort="true"
						 sortField="FULLNAME_">用户姓名</div>
					<div field="userNo" width="100" allowSort="true"
						 sortField="USER_NO_">账号</div>
					<div field="status" width="80" allowSort="true"
						 sortField="STATUS_">状态</div>
					<div field="mobile" width="120">手机</div>
					<div field="email" width="120">邮件</div>
					<div field="sex" width="80" allowSort="true"
						 renderer="onSexRenderer" sortField="SEX_">性别</div>
					<div field="userType" width="80" allowSort="true"
						 sortField="USER_TYPE_">类型</div>
					<div field="syncWx" width="80" renderer="onWxRenderer">同步到微信</div>
				</div>
			</div>
		</div>
	</div>
</div>
<div id="importWindow" title="用户导入窗口" class="mini-window" style="width:750px;height:280px;"
	 showModal="true" showFooter="false" allowResize="true">
	<div class="topToolBar" style="text-align:right;">
		<a id="importBtn" class="mini-button"  onclick="importItem()">导入</a>
		<a  class="mini-button btn-red" onclick="closeImportWindow()">关闭</a>
	</div>
	<div class="mini-fit" style="font-size: 14px">
		<form id="formImport" method="post">
			<table class="table-detail" cellspacing="1" cellpadding="0">
				<tr>
					<td style="width: 30%">上传模板：</td>
					<td style="width: 70%;">
						<a href="#" style="color:blue;text-decoration:underline;" onclick="downImportTemplate()">用户导入模板.xls</a>
					</td>
				</tr>
				<tr>
					<td style="width: 30%">操作：</td>
					<td>
						<input class="mini-textbox" style="width:60%;float: left" id="fileName" name="fileName" readonly />
						<input id="inputFile" style="display:none;" type="file" onchange="getSelectFile()" accept="application/xlsx" />
						<a id="uploadFileBtn" class="mini-button"  style="float: left;margin-left: 10px" onclick="uploadFile">选择文件</a>
						<a id="clearFileBtn" class="mini-button"  style="float: left;margin-left: 10px" onclick="clearUploadFile">清除</a>
					</td>
				</tr>
			</table>
		</form>
	</div>
</div>
<script src="${ctxPath}/scripts/common/list.js" type="text/javascript"></script>
<redxun:gridScript gridId="datagrid1"
				   entityName="com.redxun.sys.org.entity.OsUser"
				   tenantId="${param['tenantId']}" winHeight="450" winWidth="700"
				   entityTitle="用户" baseUrl="sys/org/osUser" />

<script type="text/javascript">
	var noSwtich=${cookie.switchUser==null  };
	var tenantId='${param.tenantId}';
	var importWindow=mini.get("importWindow");
	var leftTree=mini.get('#leftTree');
	var jsUseCtxPath = "${ctxPath}";
	function dimNodeClick(){
		var dimId ="1";//默认获得行政维度下的用户
		leftTree.setUrl('${ctxPath}/sys/org/sysOrg/listByDimId.do?dimId='+dimId+'&tenantId='+tenantId);
		//loadGroupRootNode();
		loadTreeRootNode();
	}
	//加载左树节点
	function loadTreeRootNode(){
		var nodes=leftTree.getRootNode().children;
		for(var i=0;i<nodes.length;i++){
			leftTree.loadNode(nodes[0]);
		}
	}

	dimNodeClick();

	//加载子树数据
	function loadSubChildren(e){
		var tree = e.sender;    //树控件
		var node = e.node;      //当前节点
		var params = e.params;  //参数对象
		//可以传递自定义的属性
		params.parentId = node.groupId; //后台：request对象获取"myField"
	}

	function groupNodeClick(e){

		var record=e.record;
		var groupId=record.groupId;

		if(!groupId) return;

		var datagrid1=mini.get('#datagrid1');
		datagrid1.setUrl(__rootPath+'/sys/org/osUser/listByGroupIdRelTypeId.do?groupId='+groupId+'&relTypeId=1');
		datagrid1.load();

	}


	//编辑
	function onActionRenderer(e) {
		var record = e.record;
		var uid = record.pkId;
		var s = ' <span  title="编辑" onclick="editRow(\'' + uid + '\',true)">编辑</span>'
				+ '<span  title="明细" onclick="detailRow(\'' + uid + '\')">明细</span>'
				+ ' <span  title="删除" onclick="delRow(\'' + uid + '\')">删除</span>'
				+ ' <span  title="拓展属性" onclick="grantNewAttribute(\'' + record.userId + '\')">拓展属性</span>';

		if(noSwtich){
			s+= '<f:a alias="OsUser_Switch"  tag="span" onclick="switchUser(\\'' + uid + '\\')" title="切换用户" showNoRight="false" >切换用户</f:a>';
		}
		return s;
	}
	function _add(){
		_OpenWindow({
			title:'新增用户',
			height:560,
			width:1024,
			url:__rootPath+'/sys/org/osUser/edit.do?tenantId='+tenantId,
			ondestroy: function(action) {
				if (action != 'ok') return;
				grid.reload();
			}
		});
	}

	function grantNewAttribute(userId){
		_OpenWindow({
			title:'用户属性管理',
			url:__rootPath+'/sys/org/osCustomAttribute/grantGroupAttribute.do?attributeType=User&targetId='+userId,
			width:900,
			height:600
		});
	}

	function _eidtRow(uid){

	}

	grid.on('drawcell',function(e){
		var record = e.record,
				field = e.field,
				value = e.value;
		if(field=='status'){
			if(value=='IN_JOB'){
				e.cellHtml='<font color="green">在职 </font>';
			}else if(value=='OUT_JOB'){
				e.cellHtml='<font color="blue">离职 </font>';
			}else{
				e.cellHtml='<font color="red">删除</font>';
			}

		}

	});

	function switchUser(userId){
		mini.confirm("确定切换用户?", "提示信息", function(action){
			if (action != "ok") return;
			location.href=__rootPath +"/j_spring_security_switch_user?userId=" + userId;
		});
	}

	function onSexRenderer(e){
		var record = e.record;
		var sex = record.sex;
		var arr = [ {'key' : 'Male', 'value' : '男(Male)','css' : 'green'},
			{'key' : 'Female','value' : '女(Female)','css' : 'red'} ];
		return $.formatItemValue(arr,sex);
	}

	function onWxRenderer(e) {
		var record = e.record;
		var syncWx = record.syncWx;

		var arr = [ {'key' : 1, 'value' : '是','css' : 'green'},
			{'key' : 0,'value' : '否','css' : 'red'} ];

		return $.formatItemValue(arr,syncWx);
	}

	/**
	 *同步用户和组织数据到企业微信。
	 */
	function syncOrgUser(){
		mini.confirm("确定同步用户到企业微信吗?", "提示信息", function(action){
			if (action != "ok") return;
			var url=__rootPath +"/wx/ent/wxEntOrg/syncOrgUser.do";
			_SubmitJson({url:url,success:function(data){
					grid.load();
				}});
		});
	}

	/**
	 *同步选中用户和组织数据到企业微信。
	 */
	function syncChoiceOrgUser(){
		var choiceData = grid.getSelecteds();
		var userIds = [];
		for(var i = 0; i<choiceData.length; i++){
			userIds.push(choiceData[i].userId);
		}

		if(choiceData.length==0){
			mini.alert("请选择用户");
			return;
		}
		mini.confirm("确定同步("+choiceData.length+")个用户到企业微信吗?", "提示信息", function(action){
			if (action != "ok") return;
			var url=__rootPath +"/wx/ent/wxEntOrg/syncOrgUser.do";
			_SubmitJson({url:url,data:{"userIds":userIds.join(',')},success:function(data){
					grid.load();
				}});
		});

	}

	function switchUser(userId){
		mini.confirm("确定切换用户?", "提示信息", function(action){
			if (action != "ok") return;
			location.href=__rootPath +"/j_spring_security_switch_user?userId=" + userId;
		});
	}

	function importUser() {
        $.ajax({
            url: "${ctxPath}" + '/sys/org/osUser/importUserTest.do',
            async: false,
            contentType: 'application/json',
            success: function (data) {
                mini.alert(data);
            }
        });
    }
	//导入
	function openImportDialog() {
		importWindow.show();
	}
	//上传批量导入
	function importItem() {
		var file = null;
		var fileList = $("#inputFile")[0].files;
		if (fileList && fileList.length > 0) {
			file = fileList[0];
		}
		if(!file) {
			mini.alert('请选择文件！');
			return;
		}
		//XMLHttpRequest方式上传表单
		var xhr = false;
		try {
			//尝试创建 XMLHttpRequest 对象，除 IE 外的浏览器都支持这个方法。
			xhr = new XMLHttpRequest();
		} catch (e) {
			//使用较新版本的 IE 创建 IE 兼容的对象（Msxml2.XMLHTTP）。
			xhr = ActiveXobject("Msxml12.XMLHTTP");
		}

		if (xhr.upload) {
			xhr.onreadystatechange = function (e) {
				if (xhr.readyState == 4) {
					if (xhr.status == 200) {
						if(xhr.responseText) {
							var returnObj=JSON.parse(xhr.responseText);
							var message='';
							if(returnObj.message) {
								message=returnObj.message;
							}
							mini.alert(message);
							closeImportWindow();
						}
					}
				}
			};
			// 开始上传
			xhr.open('POST', jsUseCtxPath + '/user/core/import/importExcel.do', false);
			xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
			var fd = new FormData();
			fd.append('importFile', file);
			xhr.send(fd);
		}
	}
	function closeImportWindow() {
		importWindow.hide();
		clearUploadFile();
		loadGrid();
	}
	//清空文件
	function clearUploadFile() {
		$("#inputFile").val('');
		mini.get("fileName").setValue('');
	}
	//导入模板下载
	function downImportTemplate() {
		var form = $("<form>");
		form.attr("style", "display:none");
		form.attr("target", "");
		form.attr("method", "post");
		form.attr("action", jsUseCtxPath + "/user/core/import/importTemplateDownload.do");
		$("body").append(form);
		form.submit();
		form.remove();
	}
	//触发文件选择
	function uploadFile() {
		$("#inputFile").click();
	}
	//文件类型判断及文件名显示
	function getSelectFile() {
		var fileList = $("#inputFile")[0].files;
		if (fileList && fileList.length > 0) {
			var fileNameSuffix = fileList[0].name.split('.').pop();
			if (fileNameSuffix == 'xls') {
				mini.get("fileName").setValue(fileList[0].name);
			}
			else {
				clearUploadFile();
				mini.alert('请上传xls文件！');
			}
		}
	}

</script>
</body>
</html>
