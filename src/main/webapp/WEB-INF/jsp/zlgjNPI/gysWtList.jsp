
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
<title>供应商质量改进列表</title>
	<%@include file="/commons/list.jsp"%>
	<script src="${ctxPath}/scripts/zlgjNPI/gysWtList.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
	 <div class="mini-toolbar" >
	 	<div class="searchBox">
			<form id="searchForm" class="search-form" style="margin-bottom: 25px">
				<input id="type" name="type" class="mini-hidden" value="${type}"/>
				<ul >
					<li style="margin-right: 15px">
						<span class="text" style="width:auto">物料分类: </span>
						<input id="smallTypeId" name="smallTypeId" class="mini-combobox" style="width:120px;"
							   textField="smallTypeName" valueField="smallTypeId" emptyText="请选择..."
							   required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
						/>
					</li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">不良零部件: </span>
                        <input class="mini-textbox" id="bllbj" name="bllbj">
                    </li>
                    <li style="margin-right: 15px"><span class="text" style="width:auto">涉及机型: </span>
						<input class="mini-textbox" id="jiXing" name="jiXing">
					</li>
					<li style="margin-right: 15px"><span class="text" style="width:auto">问题描述: </span>
						<input class="mini-textbox" id="wtms" name="wtms">
					</li>
					<li class="liBtn">
					<span class="unfoldBtn" onclick="no_more(this,'moreBox')">
						<em>展开</em>
						<i class="unfoldIcon"></i>
					</span>
					</li>
					<li style="margin-left: 10px">
						<a class="mini-button" style="margin-right: 5px"  plain="true" onclick="searchFrm()">查询</a>
						<a class="mini-button btn-red" style="margin-right: 5px"  plain="true" onclick="clearForm()">清空查询</a>
						<div style="display: inline-block" class="separator"></div>
						<a id="addZlgj" class="mini-button" style="margin-right: 5px" plain="true" onclick="addZlgj()">新增</a>
						<a id="removeZlgj" class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="removeZlgj()">删除</a>
					</li>
				</ul>
				<div id="moreBox">
					<ul>
						<li style="margin-right: 15px">
							<span class="text" style="width:auto">供方管理人员: </span>
							<input id="gfglgcsNames" name="gfglgcsNames"  class="mini-textbox"/>
						</li>
						<li style="margin-right: 15px"><span class="text" style="width:auto">流程状态: </span>
							<input id="instStatus" name="status" class="mini-combobox" style="width:120px;"
								   textField="value" valueField="key" emptyText="请选择..."
								   required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
								   data="[ {'key' : 'DRAFTED','value' : '草稿'},{'key' : 'RUNNING','value' : '审批中'},{'key' : 'SUCCESS_END','value' : '审批完成'},
							   {'key' : 'DISCARD_END','value' : '作废'}]"
							/>
						</li>
					</ul>
				</div>
			</form>	
		</div>
     </div>
	<div class="mini-fit" style="height: 100%;">
		<div id="zlgjListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onlyCheckSelection="true"
			url="${ctxPath}/zlgjNPI/core/gyswt/getZlgjList.do?type=${type}" idField="wtId" allowCellWrap="true"
			multiSelect="true" showColumnsMenu="true" sizeList="[50,100,200]" pageSize="50" allowAlternating="true" pagerButtons="#pagerButtons">
			<div property="columns">
				<div type="checkcolumn" width="20"></div>
				<div name="action" cellCls="actionIcons" width="110" headerAlign="center" align="center" renderer="onActionRenderer">操作</div>
                <div field="smallTypeName" sortField="smallTypeName" width="80" headerAlign="center" align="center"  >物料分类</div>
                <div field="bllbj"  allowSort="false" width="80" headerAlign="center" align="center">不良零部件</div>
				<div field="lbjThXh" allowSort="false" width="100" headerAlign="center" align="center" >零部件图号/型号</div>
				<div field="lbjgys" sortField="lbjgys" width="120" headerAlign="center" align="center" >零部件供应商</div>
                <div field="jiXing" sortField="jiXing" width="120" headerAlign="center" align="center" >涉及机型</div>
                <div field="fsqy"  allowSort="false" width="120"  headerAlign="center" align="center" >问题发生区域</div>
				<div field="wtms" allowSort="false" width="200"  headerAlign="center" align="center" >问题详细描述</div>
				<div field="gfglgcsNames" sortField="gfglgcsNames" width="80" headerAlign="center" align="center" >供方管理人员</div>
				<div field="status"  sortField="status" width="70" headerAlign="center" align="center"  renderer="onStatusRenderer">流程状态</div>
				<div field="currentProcessUser"  allowSort="false" width="70" align="center" headerAlign="center" >当前处理人</div>
				<div field="currentProcessTask" allowSort="false" width="100" align="center" headerAlign="center">当前任务</div>
				<div field="applyUserName" sortField="applyUserName" width="70" headerAlign="center" align="center" >创建人</div>
				<div field="CREATE_TIME_" sortField="CREATE_TIME_" width="90" headerAlign="center" align="center" >创建时间</div>
			</div>
		</div>
	</div>

	<script type="text/javascript">
		mini.parse();
		var jsUseCtxPath="${ctxPath}";
        var zlgjListGrid=mini.get("zlgjListGrid");
        var currentUserId="${currentUserId}";
        var currentUserNo="${currentUserNo}";
        var type="${type}";
        var smallTypeList = ${smallTypeList};

		//行功能按钮
		function onActionRenderer(e) {
			var record = e.record;
			var wtId = record.wtId;
			var instId=record.instId;
			var s = '<span  title="明细" onclick="zlgjDetail(\'' + wtId +'\',\''+record.status+ '\')">明细</span>';
            if(record.status=='DRAFTED' && record.CREATE_BY_==currentUserId) {
                s+='<span  title="编辑" onclick="zlgjEdit(\'' + wtId +'\',\''+instId+ '\')">编辑</span>';
            } else if(record.currentProcessUserId && record.currentProcessUserId.indexOf(currentUserId) !=-1) {
                    s+='<span  title="办理" onclick="zlgjTask(\'' + record.taskId + '\')">办理</span>';
			}
            //无删除权限的按钮变灰色
            if ((record.status == 'DRAFTED' || record.status == 'DISCARD_END')&& (record.CREATE_BY_==currentUserId || currentUserId == "1")) {
                s+='<span  title="删除" onclick="removeZlgj('+JSON.stringify(record).replace(/"/g, '&quot;')+')">删除</span>';
            } else {
                s+='<span  title="删除" style="color: silver">删除</span>';
            }
			return s;
		}

        function onStatusRenderer(e) {
            var record = e.record;
            var status = record.status;
            var arr = [ {'key' : 'DRAFTED','value' : '草稿','css' : 'orange'},
                {'key' : 'RUNNING','value' : '审批中','css' : 'green'},
                {'key' : 'SUCCESS_END','value' : '审批完成','css' : 'blue'},
                {'key' : 'DISCARD_END','value' : '作废','css' : 'red'}
            ];
            return $.formatItemValue(arr,status);
        }

	</script>
	<redxun:gridScript gridId="zlgjListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl="" />
</body>
</html>