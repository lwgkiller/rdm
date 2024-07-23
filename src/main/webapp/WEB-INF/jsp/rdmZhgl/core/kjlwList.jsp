
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
<title>科技论文列表管理</title>
	<%@include file="/commons/list.jsp"%>
	<script src="${ctxPath}/scripts/rdmZhgl/kjlwList.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
	 <div class="mini-toolbar" >
	 	<div class="searchBox">
			<form id="searchForm" class="search-form" style="margin-bottom: 25px">
				<ul >
					<li style="margin-right: 15px">
                        <span class="text" style="width:auto">论文名称: </span>
                        <input class="mini-textbox" id="kjlwName" name="kjlwName" />
                    </li>
					<li style="margin-right: 15px">
						<span class="text" style="width:auto">作者: </span>
						<input id="writerId" name="writerId" textname="writerName" class="mini-user rxc" onvaluechanged="searchFrm"
							   plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="" length="50" maxlength="50"
							   mainfield="no"  single="true" />
					</li>
					<li style="margin-right: 15px">
						<span class="text" style="width:auto">期刊名称:</span>
						<input class="mini-textbox" id="qkmc" name="qkmc" />
					</li>
					<li style="margin-right: 15px"><span class="text" style="width:auto">认定状态: </span>
						<input id="status" name="status" class="mini-combobox" style="width:120px;"
							   textField="value" valueField="key" emptyText="请选择..." onvaluechanged="searchFrm"
							   required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
							   data="[ {'key' : 'DRAFTED','value' : '草稿'},{'key' : 'RUNNING','value' : '认定中'},{'key' : 'SUCCESS_END','value' : '通过认定'},
							   {'key' : 'DISCARD_END','value' : '未通过认定'}]"
						/>
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
						<a class="mini-button" style="margin-right: 5px" plain="true" onclick="addKjlw()">新增申请</a>
						<a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="removeKjlw()">删除</a>
						<a class="mini-button" style="margin-right: 5px" plain="true" onclick="exportKjlw()">导出</a>
					</li>
				</ul>
				<div id="moreBox">
					<ul>
						<li style="margin-right: 15px">
							<span class="text" style="width:auto">申请时间 从 </span>:<input id="CREATE_TIME_StartTime"  name="CREATE_TIME_StartTime"  class="mini-datepicker" format="yyyy-MM-dd" style="width:120px"/>
						</li>
						<li style="margin-right: 15px">
							<span class="text-to" style="width:auto">至: </span><input  id="CREATE_TIME_EndTime" name="CREATE_TIME_EndTime" class="mini-datepicker" format="yyyy-MM-dd" style="width:120px" />
						</li>
						<li style="margin-right: 15px">
							<span class="text" style="width:auto">发表时间 从 </span>:<input id="fbTimeStartTime"  name="fbTimeStartTime"  class="mini-datepicker" format="yyyy-MM-dd" style="width:120px"/>
						</li>
						<li style="margin-right: 15px">
							<span class="text-to" style="width:auto">至: </span><input  id="fbTimeEndTime" name="fbTimeEndTime" class="mini-datepicker" format="yyyy-MM-dd" style="width:120px" />
						</li>
					</ul>
				</div>
			</form>	
		</div>
     </div>
	<div class="mini-fit" style="height: 100%;">
		<div id="kjlwListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onlyCheckSelection="true"
			url="${ctxPath}/zhgl/core/kjlw/getKjlwList.do" idField="kjlwId"
			multiSelect="true" showColumnsMenu="true" sizeList="[50,100,200]" pageSize="50" allowAlternating="true" pagerButtons="#pagerButtons">
			<div property="columns">
				<div type="checkcolumn" width="20"></div>
				<div name="action" cellCls="actionIcons" width="100" headerAlign="center" align="center" renderer="onActionRenderer" cellStyle="padding:0;">操作</div>
				<div field="kjlwNum"  sortField="kjlwNum"  width="80" headerAlign="center" align="center" allowSort="true">编号</div>
				<div field="kjlwName"  width="100" headerAlign="center" align="center" allowSort="false">论文名称</div>
				<div field="writerName" align="center" width="60" headerAlign="center" allowSort="false">作者</div>
				<div field="qkmc"  width="60" headerAlign="center" align="center" allowSort="false">期刊名称</div>
				<div field="qNum" width="60"  allowSort="true" headerAlign="center" align="center" >期号</div>
				<div field="yema" width="60"  allowSort="true" headerAlign="center" align="center" >页码</div>
				<div field="fbTime" width="60"  allowSort="true" headerAlign="center" align="center" >发表时间</div>
				<div field="applyUserName"  width="70" headerAlign="center" align="center" allowSort="false">申请人</div>
				<div field="applyUserDeptName"  width="70" headerAlign="center" align="center" allowSort="false">申请人部门</div>
				<div field="CREATE_TIME_"  width="70" headerAlign="center" align="center" allowSort="false">申请时间</div>
				<div field="status"  width="60" headerAlign="center" align="center" allowSort="true" renderer="onStatusRenderer">认定状态</div>
				<div field="currentProcessUser"  sortField="currentProcessUser"  width="70" align="center" headerAlign="center" allowSort="false">当前处理人</div>
				<div field="currentProcessTask" width="80" align="center" headerAlign="center">当前任务</div>
			</div>
		</div>
	</div>

	 <!--导出Excel相关HTML-->
	 <form id="excelForm" action="${ctxPath}/zhgl/core/kjlw/exportKjlwList.do" method="post" target="excelIFrame">
		 <input type="hidden" name="filter" id="filter"/>
	 </form>
	 <iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>


	<script type="text/javascript">
		mini.parse();
		var jsUseCtxPath="${ctxPath}";
        var kjlwListGrid=mini.get("kjlwListGrid");
        var currentUserId="${currentUserId}";
        var currentUserRoles=${currentUserRoles};
        var currentUserNo="${currentUserNo}";
        var isZlgcsUser=${isZlgcsUser};

		//行功能按钮
		function onActionRenderer(e) {
			var record = e.record;
			var kjlwId = record.kjlwId;
			var instId=record.instId;
			var writerId=record.writerId;
            var s = '';
			if (record.status=='SUCCESS_END' || record.CREATE_BY_==currentUserId){
				s+='<span  title="明细" onclick="kjlwDetail(\'' + kjlwId +'\',\''+record.status+'\',\''+writerId+ '\')">明细</span>';
            } else {
                s+='<span  title="明细" style="color: silver">明细</span>';
			}
			if(record.status=='DRAFTED' && record.CREATE_BY_==currentUserId) {
			    s+='<span  title="编辑" onclick="kjlwEdit(\'' + kjlwId +'\',\''+instId+ '\')">编辑</span>';
			} else {
			    var currentProcessUserId=record.currentProcessUserId;
                if(currentProcessUserId && currentProcessUserId.indexOf(currentUserId) !=-1) {
                    s+='<span  title="办理" onclick="kjlwTask(\'' + record.taskId + '\')">办理</span>';
                }
			}
			var status=record.status;
			if(currentUserNo!='admin') {
                if (status == 'DRAFTED' && currentUserId ==record.CREATE_BY_) {
                    s+='<span  title="删除" onclick="removeKjlw('+JSON.stringify(record).replace(/"/g, '&quot;')+')">删除</span>';
                }
            } else {
                s+='<span  title="删除" onclick="removeKjlw('+JSON.stringify(record).replace(/"/g, '&quot;')+')">删除</span>';
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



	</script>
	<redxun:gridScript gridId="kjlwListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl="" />
</body>
</html>