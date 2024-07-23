
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
    <title>单一项目列表管理</title>
    <%@include file="/commons/list.jsp"%>
    <script src="${ctxPath}/scripts/drbfm/singleList.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar" >
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul >
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">项目流水号:</span>
                    <input class="mini-textbox" id="singleNumber" name="singleNumber" />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">设计型号: </span>
                    <input class="mini-textbox" id="jixing" name="jixing" />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">总项目名称: </span>
                    <input class="mini-textbox" id="analyseName" name="analyseName" />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">部件/接口名称: </span>
                    <input id="structName" name="structName" class="mini-textbox"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">部件/接口类型: </span>
                    <input id="structType" name="structType" class="mini-combobox" style="width:120px;"
                           textField="value" valueField="key" emptyText="请选择..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                           data="[ {'key':'整机','value':'整机'},{'key' : '新零部件','value' : '新零部件'},{'key' : '延用国三件','value' : '延用国三件'}]"
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
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="removeApply()">删除</a>
                    <div style="display: inline-block" class="separator"></div>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="exportSingle()">导出</a>
                </li>
            </ul>
            <div id="moreBox">
                <ul>
                    <li style="margin-right: 15px"><span class="text" style="width:auto">产品主管:</span>
                        <input id="creator" name="creator" class="mini-textbox"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">流程状态: </span>
                        <input id="instStatus" name="instStatus" class="mini-combobox" style="width:120px;"
                               textField="value" valueField="key" emptyText="请选择..."
                               required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                               data="[ {'key' : 'DRAFTED','value' : '草稿'},{'key' : 'RUNNING','value' : '审批中'},{'key' : 'SUCCESS_END','value' : '已完成'},
							   {'key' : 'DISCARD_END','value' : '作废'}]"
                        />
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">部件/接口责任人: </span>
                        <input id="analyseUserName" name="analyseUserName" class="mini-textbox"/>
                    </li>
                </ul>
            </div>
        </form>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="singleListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onlyCheckSelection="true"
         url="${ctxPath}/drbfm/single/getSingleList.do" idField="id" allowCellWrap="true"
         multiSelect="true" showColumnsMenu="true" sizeList="[50,100,200]" pageSize="50" allowAlternating="true" pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div name="action" cellCls="actionIcons" width="90" headerAlign="center" align="center" renderer="onActionRenderer" cellStyle="padding:0;">操作</div>
            <div field="singleNumber" width="140" headerAlign="center" align="center" allowSort="true">项目流水号</div>
            <div field="structName" width="100" headerAlign="center" align="center" allowSort="true">部件/接口名称</div>
            <div field="structNumber" width="100" headerAlign="center" align="center" allowSort="true">部件/接口编号</div>
            <div field="structType" width="70" headerAlign="center" align="center" allowSort="true">类型</div>
            <div field="parentStructName" width="100" headerAlign="center" align="center" allowSort="true">上层部件/接口名称</div>
            <div field="jixing" width="70" headerAlign="center" align="center" allowSort="true">设计型号</div>
            <div field="analyseName" width="100" headerAlign="center" align="center" allowSort="true">所属总项目名称</div>
            <div field="analyseUserName" width="70" headerAlign="center" align="center" allowSort="true">部件/接口责任人</div>
            <div field="instStatus"  width="60" headerAlign="center" align="center" allowSort="true" renderer="onStatusRenderer">流程状态</div>
            <div field="currentProcessUser"  sortField="currentProcessUser"  width="60" align="center" headerAlign="center" allowSort="false">当前处理人</div>
            <div field="currentProcessTask" width="100" align="center" headerAlign="center">当前流程节点</div>
            <div field="creator" sortField="creator" width="60" headerAlign="center" align="center" allowSort="true">产品主管</div>
            <div field="CREATE_TIME_" sortField="CREATE_TIME_" width="70" headerAlign="center" dateFormat="yyyy-MM-dd" align="center" allowSort="true">创建时间</div>
        </div>
    </div>
</div>
<!--导出Excel相关HTML-->
<form id="excelForm" action="${ctxPath}/drbfm/single/exportSingleExcel.do" method="post" target="excelIFrame">
    <input type="hidden" name="filter" id="filter"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var singleListGrid=mini.get("singleListGrid");
    var currentUserId="${currentUserId}";
    var currentUserNo="${currentUserNo}";


    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var applyId = record.id;
        var instId=record.instId;
        var currentProcessUserId=record.currentProcessUserId;
        var s = '<span  title="查看" onclick="singleDetail(\'' + applyId +'\',\''+record.status+ '\')">查看</span>';
        if(currentProcessUserId && currentProcessUserId.indexOf(currentUserId) !=-1) {
            s+='<span  title="办理" onclick="devTaskDo(\'' + record.taskId + '\')">办理</span>';
        }

        var status=record.instStatus;
        if (status == 'DISCARD_END' && (currentUserId ==record.CREATE_BY_||currentUserId=="1")) {
            s+='<span  title="删除" onclick="removeApply('+JSON.stringify(record).replace(/"/g, '&quot;')+')">删除</span>';
        }
        return s;
    }

    function onStatusRenderer(e) {
        var record = e.record;
        var status = record.instStatus;

        var arr = [ {'key' : 'DRAFTED','value' : '草稿','css' : 'orange'},
            {'key' : 'RUNNING','value' : '审批中','css' : 'green'},
            {'key' : 'SUCCESS_END','value' : '已完成','css' : 'blue'},
            {'key' : 'DISCARD_END','value' : '作废','css' : 'red'}
        ];

        return $.formatItemValue(arr,status);
    }

</script>
<redxun:gridScript gridId="singleListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl="" />
</body>
</html>