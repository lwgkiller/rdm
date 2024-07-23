
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
<title>新品量产前评审列表</title>
	<%@include file="/commons/list.jsp"%>
	<script src="${ctxPath}/scripts/zlgjNPI/newItemLcpsList.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
	 <div class="mini-toolbar" >
	 	<div class="searchBox">
			<form id="searchForm" class="search-form" style="margin-bottom: 25px">
				<ul >
					<li style="margin-right: 15px">
						<span class="text" style="width:auto">产品主管: </span>
						<input class="mini-textbox" id="cpzgName" name="cpzgName">
					</li>
					<li style="margin-right: 15px"><span class="text" style="width:auto">认定状态: </span>
						<input id="instStatus" name="instStatus" class="mini-combobox" style="width:120px;"
							   textField="value" valueField="key" emptyText="请选择..."
							   required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
							   data="[ {'key' : 'DRAFTED','value' : '草稿'},{'key' : 'RUNNING','value' : '认定中'},{'key' : 'SUCCESS_END','value' : '通过认定'},
							   {'key' : 'DISCARD_END','value' : '未通过认定'}]"
						/>
					</li>

					<li style="margin-left: 10px">
						<a class="mini-button" style="margin-right: 5px"  plain="true" onclick="searchFrm()">查询</a>
						<a class="mini-button btn-red" style="margin-right: 5px"  plain="true" onclick="clearForm()">清空查询</a>
						<div style="display: inline-block" class="separator"></div>
						<a id="addXplcps" class="mini-button" style="margin-right: 5px" plain="true" onclick="addXplcps()">新增</a>
						<a id="removeXplc" class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="removeXplc()">删除</a>
					</li>
				</ul>
			</form>	
		</div>
     </div>
	<div class="mini-fit" style="height: 100%;">
		<div id="xplcListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onlyCheckSelection="true"
			url="${ctxPath}/zhgl/core/lcps/getXplcList.do" idField="xplcId" allowCellWrap="true"
			multiSelect="true" showColumnsMenu="true" sizeList="[50,100,200]" pageSize="50" allowAlternating="true" pagerButtons="#pagerButtons">
			<div property="columns">
				<div type="checkcolumn" width="20"></div>
				<div name="action" cellCls="actionIcons" width="60" headerAlign="center" align="center" renderer="onActionRenderer" cellStyle="padding:0;">操作</div>
				<div field="cpzgName"  width="80"  headerAlign="center" align="center" >产品主管</div>
				<div field="instStatus"  width="70" headerAlign="center" align="center"  renderer="onStatusRenderer">流程状态</div>
				<div field="allTaskUserNames"  sortField="currentProcessUser"  width="70" align="center" headerAlign="center" >当前处理人</div>
				<div field="taskName" width="80" align="center" headerAlign="center">当前任务</div>
				<div field="creator"  width="70" headerAlign="center" align="center" >创建人</div>
				<div field="CREATE_TIME_" sortField="CREATE_TIME_" width="90" headerAlign="center" align="center" >创建时间</div>
			</div>
		</div>
	</div>

	<script type="text/javascript">
		mini.parse();
		var jsUseCtxPath="${ctxPath}";
        var xplcListGrid=mini.get("xplcListGrid");
        var currentUserId="${currentUserId}";
        var currentUserRoles=${currentUserRoles};
        var currentUserNo="${currentUserNo}";
        var isJsglbRespUser=${isJsglbRespUser};

		//行功能按钮
		function onActionRenderer(e) {
			var record = e.record;
			var xplcId = record.xplcId;
			var instId=record.instId;
			var s = '';
			if(record.instStatus=='SUCCESS_END') {
                //技术管理部负责人不能看，不是自己且不是技术管理部其他人的且知悉范围中没有自己的，已成功的秘密内容
                if(isJsglbRespUser && (currentUserId !=record.CREATE_BY_) && record.applyUserDeptName !='技术管理部' && record.readUserIds.indexOf(currentUserId)==-1) {
                    s+='<span  title="明细" style="color: silver">明细</span>';
				}
				 else {
                    s+='<span  title="明细" onclick="xplcDetail(\'' + xplcId +'\',\''+record.instStatus+ '\')">明细</span>';
				}

			} else{
                s+='<span  title="明细" onclick="xplcDetail(\'' + xplcId +'\',\''+record.instStatus+ '\')">明细</span>';
            }
			if(record.instStatus=='DRAFTED') {
			    s+='<span  title="编辑" onclick="xplcEdit(\'' + xplcId +'\',\''+instId+ '\')">编辑</span>';
			} else {
                if(record.myTaskId) {
                    s+='<span  title="办理" onclick="xplcTask(\'' + record.myTaskId + '\')">办理</span>';
                }
			}

			var status=record.instStatus;
			if(currentUserNo!='admin') {
                if ((status == 'DRAFTED' && currentUserId ==record.CREATE_BY_)||(status == 'DISCARD_END' && currentUserId ==record.CREATE_BY_)) {
                    s+='<span  title="删除" onclick="removeXplc('+JSON.stringify(record).replace(/"/g, '&quot;')+')">删除</span>';
                }
            } else {
                s+='<span  title="删除" onclick="removeXplc('+JSON.stringify(record).replace(/"/g, '&quot;')+')">删除</span>';
			}
            return s;
		}

        function onStatusRenderer(e) {
            var record = e.record;
            var instStatus = record.instStatus;

            var arr = [ {'key' : 'DRAFTED','value' : '草稿','css' : 'orange'},
                {'key' : 'RUNNING','value' : '认定中','css' : 'green'},
                {'key' : 'SUCCESS_END','value' : '通过认定','css' : 'blue'},
                {'key' : 'DISCARD_END','value' : '未通过认定','css' : 'red'}
            ];

            return $.formatItemValue(arr,instStatus);
        }

	</script>
	<redxun:gridScript gridId="xplcListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl="" />
</body>
</html>