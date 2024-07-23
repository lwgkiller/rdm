
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
    <title>询比价招标列表管理</title>
    <%@include file="/commons/list.jsp"%>
    <script src="${ctxPath}/scripts/rdmZhgl/jszbList.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar" >
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul >
                <input id="gslclb" name="gslclb" value="询比价流程" class="mini-hidden"/>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">询比价部门: </span>
                    <input class="mini-textbox" id="zbbmName" name="zbbmName" />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">经办人: </span>
                    <input class="mini-textbox" id="jbrName" name="jbrName" />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">询比价项目名称: </span>
                    <input class="mini-textbox" id="zbName" name="zbName" />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">项目编号:</span>
                    <input class="mini-textbox" id="xmNum" name="xmNum" />
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">流程状态: </span>
                    <input id="status" name="status" class="mini-combobox" style="width:120px;"
                           textField="value" valueField="key" emptyText="请选择..." onvaluechanged="searchFrm"
                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                           data="[ {'key' : 'DRAFTED','value' : '草稿'},{'key' : 'RUNNING','value' : '审批中'},{'key' : 'SUCCESS_END','value' : '成功结束'},
							   {'key' : 'DISCARD_END','value' : '流程作废'}]"
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
                    <a class="mini-button btn-red" style="margin-right: 5px"  plain="true" onclick="jszbClearForm()">清空查询</a>
                    <div style="display: inline-block" class="separator"></div>
                    <a class="mini-button" style="margin-right: 5px" plain="true" enabled="true" onclick="addJszblc()">新增流程</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="removeJszb()">删除</a>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="exportJszb()">导出</a>
                </li>
            </ul>
            <div id="moreBox">
                <ul>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">立项时间 从 </span>:<input id="lxTimeStartTime"  name="lxTimeStartTime"  class="mini-datepicker" format="yyyy-MM-dd" style="width:120px"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text-to" style="width:auto">至: </span><input  id="lxTimeEndTime" name="lxTimeEndTime" class="mini-datepicker" format="yyyy-MM-dd" style="width:120px" />
                    </li>
                </ul>
            </div>
        </form>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="jszbListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onlyCheckSelection="true"
         url="${ctxPath}/zhgl/core/jszb/getJszbList.do" idField="jszbId" allowCellWrap="true"
         multiSelect="true" showColumnsMenu="true" sizeList="[20,50,100,200]" pageSize="20" allowAlternating="true" pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div name="action" cellCls="actionIcons" width="90" headerAlign="center" align="center" renderer="onActionRenderer" cellStyle="padding:0;">操作</div>
            <div field="zbbmName"  width="70" headerAlign="center" align="center" allowSort="false">询比价部门</div>
            <div field="jbrName"  width="60" headerAlign="center" align="center" allowSort="false">经办人</div>
            <div field="zbName" align="center" width="80" headerAlign="center" allowSort="false">询比价项目名称</div>
            <div field="xmNum"  width="80" headerAlign="center" align="center" allowSort="false">项目编号</div>
            <div field="zbjg" width="80"  allowSort="true" headerAlign="center" align="center" >询比价总价或项目预算</div>
            <div field="publishType"  width="70" headerAlign="center" align="center" allowSort="false">信息发布方式</div>
            <div field="lxTime" width="60"  allowSort="true" headerAlign="center" align="center" >立项时间</div>
            <div field="yjFinishTime" width="60"  allowSort="true" headerAlign="center" align="center" >预计完成时间</div>
            <div field="status"  width="60" headerAlign="center" align="center" allowSort="true" renderer="onStatusRenderer">流程状态</div>
            <div field="currentProcessUser"  sortField="currentProcessUser"  width="60" align="center" headerAlign="center" allowSort="false">当前处理人</div>
            <div field="currentProcessTask" width="80" align="center" headerAlign="center">当前流程任务</div>
        </div>
    </div>
</div>

<!--导出Excel相关HTML-->
<form id="excelForm" action="${ctxPath}/zhgl/core/jszb/exportJszbExcel.do" method="post" target="excelIFrame">
    <input type="hidden" name="filter" id="filter"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var jszbListGrid=mini.get("jszbListGrid");
    var currentUserId="${currentUserId}";
    var currentUserNo="${currentUserNo}";
    var type="询比价流程";
    var isZbzy="${isZbzy}";

    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var jszbId = record.jszbId;
        var instId=record.instId;
        var s = '';
        s+='<span  title="明细" onclick="jszbDetail(\'' + jszbId +'\',\''+record.status+ '\')">明细</span>';
        if(record.status=='DRAFTED' && record.CREATE_BY_==currentUserId) {
            s+='<span  title="编辑" onclick="jszbEdit(\'' + jszbId +'\',\''+instId+ '\')">编辑</span>';
        } else {
            var currentProcessUserId=record.currentProcessUserId;
            if(currentProcessUserId && currentProcessUserId.indexOf(currentUserId) !=-1) {
                s+='<span  title="办理" onclick="jszbTask(\'' + record.taskId + '\')">办理</span>';
            }
            if(isZbzy=='true') {
                s+='<span  title="变更" onclick="jszbChange(\'' + jszbId + '\')">变更</span>';
            }
        }
        var status=record.status;
        if(currentUserNo!='admin') {
            if ((status == 'DRAFTED' && currentUserId ==record.CREATE_BY_)||(status == 'DISCARD_END' && currentUserId ==record.CREATE_BY_)) {
                s+='<span  title="删除" onclick="removeJszb('+JSON.stringify(record).replace(/"/g, '&quot;')+')">删除</span>';
            }
        } else {
            s+='<span  title="删除" onclick="removeJszb('+JSON.stringify(record).replace(/"/g, '&quot;')+')">删除</span>';
        }
        return s;
    }

    function onStatusRenderer(e) {
        var record = e.record;
        var status = record.status;
        if (!status){
            status="SUCCESS_END";
        }
        var arr = [ {'key' : 'DRAFTED','value' : '草稿','css' : 'orange'},
            {'key' : 'RUNNING','value' : '审批中','css' : 'green'},
            {'key' : 'SUCCESS_END','value' : '成功结束','css' : 'blue'},
            {'key' : 'DISCARD_END','value' : '流程作废','css' : 'red'}
        ];

        return $.formatItemValue(arr,status);
    }

</script>
<redxun:gridScript gridId="jszbListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl="" />
</body>
</html>