
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
<title>技术数据库审批列表管理</title>
	<%@include file="/commons/list.jsp"%>
	<script src="${ctxPath}/scripts/xcmgjssjk/jssjkspList.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
	 <div class="mini-toolbar" >
	 	<div class="searchBox">
			<form id="searchForm" class="search-form" style="margin-bottom: 25px">
				<ul >
					<li style="margin-right: 15px">
						<span class="text" style="width:auto">技术编号: </span>
						<input class="mini-textbox" id="jsNum" name="jsNum">
					</li>
                    <li style="margin-right: 14px">
                        <span class="text" style="width:auto">技术名称: </span>
                        <input class="mini-textbox" id="jsName" name="jsName" />
                    </li>
					<li style="margin-right: 15px">
						<span class="text" style="width:auto">申请人: </span>
						<input id="applyUserId" name="applyUserId" textname="applyUserName" class="mini-user rxc"
							   plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="" length="50" maxlength="50"
							   mainfield="no"  single="true" />
					</li>
					<li style="margin-right: 15px"><span class="text" style="width:auto">申请部门:</span>
						<input id="applyDepId" name="applyDepId" class="mini-dep rxc" plugins="mini-dep"
							   data-options="{'single':'true','config':{'type':'','grouplevel':'','groupid':'','groupidText':''}}"
							   style="width:160px;height:34px"
							   allowinput="false" label="部门" textname="bm_name" length="500" maxlength="500" minlen="0" single="true" required="false" initlogindep="false"
							   level="" refconfig="" grouplevel="" groupid="" mwidth="160" wunit="px" mheight="34" hunit="px"/>
					</li>
					<li style="margin-right: 15px"><span class="text" style="width:auto">认定状态: </span>
						<input id="instStatus" name="status" class="mini-combobox" style="width:120px;"
							   textField="value" valueField="key" emptyText="请选择..." onvaluechanged="searchFrm"
							   required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
							   data="[ {'key' : 'DRAFTED','value' : '草稿'},{'key' : 'RUNNING','value' : '认定中'},{'key' : 'SUCCESS_END','value' : '通过认定'},
							   {'key' : 'DISCARD_END','value' : '未通过认定'}]"
						/>
					</li>

					<li style="margin-left: 10px">
						<a class="mini-button" style="margin-right: 5px"  plain="true" onclick="searchFrm()">查询</a>
						<a class="mini-button btn-red" style="margin-right: 5px"  plain="true" onclick="clearForm()">清空查询</a>
						<div style="display: inline-block" class="separator"></div>
						<a class="mini-button" style="margin-right: 5px" plain="true" onclick="addJssjksp()">新增</a>
						<a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="removeSpJssjk()">删除</a>

					</li>
				</ul>
			</form>	
		</div>
     </div>
	<div class="mini-fit" style="height: 100%;">
		<div id="spListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onlyCheckSelection="true"
			url="${ctxPath}/jssj/core/config/getSpList.do" idField="jssjkId"
			multiSelect="true" showColumnsMenu="true" sizeList="[50,100,200]" pageSize="50" allowAlternating="true" pagerButtons="#pagerButtons">
			<div property="columns">
				<div type="checkcolumn" width="20"></div>
				<div name="action" cellCls="actionIcons" width="100" headerAlign="center" align="center" renderer="onActionRenderer" cellStyle="padding:0;">操作</div>
				<div field="jsName"  width="90" headerAlign="center" align="center" allowSort="true">技术名称</div>
				<div field="jsNum"  sortField="jsNum"  width="80" headerAlign="center" align="center" allowSort="true">技术编号</div>
				<div field="sbjsNum"   width="80" headerAlign="center" align="center" allowSort="true" renderer="jumpToDetail">上一版技术编号</div>
				<div field="splx"  width="100" headerAlign="center" align="center" allowSort="true" renderer="onSpRenderer">审批类型</div>
				<div field="applyUserName"  width="60" headerAlign="center" align="center" allowSort="false">申请人</div>
				<div field="applyUserDeptName"  width="70" headerAlign="center" align="center" allowSort="false">申请人部门</div>
                <div field="jdTime" align="center" width="60" headerAlign="center" allowSort="false">鉴定日期</div>
				<div field="CREATE_TIME_" sortField="CREATE_TIME_" width="60" headerAlign="center" align="center" allowSort="false">申请时间</div>
				<div field="status"  width="60" headerAlign="center" align="center" allowSort="true" renderer="onStatusRenderer">认定状态</div>
				<div field="currentProcessUser"  sortField="currentProcessUser"  width="70" align="center" headerAlign="center" allowSort="false">当前处理人</div>
				<div field="currentProcessTask" width="80" align="center" headerAlign="center">当前任务</div>
			</div>
		</div>
	</div>

	 <!--导出Excel相关HTML-->
	 <form id="excelForm" action="${ctxPath}/zhgl/core/rjzz/exportRjzzList.do" method="post" target="excelIFrame">
		 <input type="hidden" name="filter" id="filter"/>
	 </form>
	 <iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>


	<script type="text/javascript">
		mini.parse();
		var jsUseCtxPath="${ctxPath}";
        var spListGrid=mini.get("spListGrid");
        var currentUserId="${currentUserId}";
        var currentUserRoles=${currentUserRoles};
        var currentUserNo="${currentUserNo}";
        var isZlgcsUser=${isZlgcsUser};

		//行功能按钮
		function onActionRenderer(e) {
			var record = e.record;
			var jssjkId = record.jssjkId;
			var instId=record.instId;
			var sptype=record.splx;
            var s ='<span  title="明细" onclick="jssjkspDetail(\'' + jssjkId +'\',\''+record.status +'\',\''+sptype+ '\')">明细</span>';
			if(record.status=='DRAFTED') {
			    s+='<span  title="编辑" onclick="jssjkspEdit(\'' + jssjkId +'\',\''+instId +'\',\''+sptype+ '\')">编辑</span>';
			} else {
			    var currentProcessUserId=record.currentProcessUserId;
                if(currentProcessUserId && currentProcessUserId.indexOf(currentUserId) !=-1) {
                    s+='<span  title="办理" onclick="jssjkTask(\'' + record.taskId +'\',\''+sptype+ '\')">办理</span>';
                }
			}
			var status=record.status;
			if(currentUserNo!='admin') {
                if (status == 'DRAFTED' && currentUserId ==record.CREATE_BY_) {
                    s+='<span  title="删除" onclick="removeSpJssjk('+JSON.stringify(record).replace(/"/g, '&quot;')+')">删除</span>';
                }
            } else {
                s+='<span  title="删除" onclick="removeSpJssjk('+JSON.stringify(record).replace(/"/g, '&quot;')+')">删除</span>';
			}
            return s;
		}

        function onStatusRenderer(e) {
            var record = e.record;
            var status = record.status;

            var arr = [ {'key' : 'DRAFTED','value' : '草稿','css' : 'orange'},
                {'key' : 'RUNNING','value' : '认定中','css' : 'green'},
                {'key' : 'SUCCESS_END','value' : '通过认定','css' : 'blue'},
                {'key' : 'DISCARD_END','value' : '未通过认定','css' : 'red'}
            ];

            return $.formatItemValue(arr,status);
        }
        function onSpRenderer(e) {
            var record = e.record;
            var splx = record.splx;

            var arr = [ {'key' : 'new','value' : '添加新技术','css' : 'green'},
                {'key' : 'update','value' : '修改新技术','css' : 'orange'},
                {'key' : 'delete','value' : '删除新技术','css' : 'red'}
            ];
            return $.formatItemValue(arr,splx);
        }

        function jumpToDetail(e) {
            var record = e.record;
            var sbjsNum = record.sbjsNum;
            if(!sbjsNum) {
                return '';
			}
            var s = '<a href="#" style="color:#44cef6;text-decoration:underline;" onclick="jssjkspDetail(\'' + record.sbbId +'\',\''+record.status+'\',\'new\')">'+record.sbjsNum+'</a>';
            return s;
        }
	</script>
	<redxun:gridScript gridId="spListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl="" />
</body>
</html>