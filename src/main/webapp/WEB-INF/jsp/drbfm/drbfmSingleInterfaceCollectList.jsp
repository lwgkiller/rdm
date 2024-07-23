
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
    <title>单一项目列表管理</title>
    <%@include file="/commons/list.jsp"%>
    <script src="${ctxPath}/scripts/drbfm/singleInterfaceCollectList.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar" >
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul >
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">部件/接口名称: </span>
                    <input id="structName" name="structName" class="mini-textbox"/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">产品主管:</span>
                    <input id="creator" name="creator" class="mini-textbox"/>
                </li>

                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">部件/接口责任人: </span>
                    <input id="analyseUserName" name="analyseUserName" class="mini-textbox"/>
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
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px"  plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px"  plain="true" onclick="clearForm()">清空查询</a>
                    <div style="display: inline-block" class="separator"></div>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="removeApply()">删除</a>
                </li>

            </ul>
            <div id="moreBox">
                <ul>

                </ul>
            </div>
        </form>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="singleListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onlyCheckSelection="true"
         url="${ctxPath}/drbfm/single/getInterfaceCollectList.do" idField="id" allowCellWrap="true"
         multiSelect="true" showColumnsMenu="true" sizeList="[50,100,200]" pageSize="50" allowAlternating="true" pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div name="action" cellCls="actionIcons" width="70" headerAlign="center" align="center" renderer="onActionRenderer" cellStyle="padding:0;">操作</div>
<%--            <div field="singleNumber" width="140" headerAlign="center" align="center" allowSort="true">项目流水号</div>--%>
            <div field="structName" width="50" headerAlign="center" align="center" allowSort="true">部件/接口名称</div>
            <div field="structNumber" width="70" headerAlign="center" align="center" allowSort="true">部件/接口编号</div>
            <div field="jixing" width="50" headerAlign="center" align="center" allowSort="true">设计型号</div>
            <div field="analyseName" width="100" headerAlign="center" align="center" allowSort="true">所属总项目名称</div>
            <div field="creator" width="50" headerAlign="center" align="center" allowSort="true">流程发起人</div>
            <div field="analyseUserName" width="60" headerAlign="center" align="center" allowSort="true">部件/接口责任人</div>
            <div field="creator" sortField="creator" width="50" headerAlign="center" align="center" allowSort="true">产品主管</div>
            <div field="currentProcessUser"  sortField="currentProcessUser"  width="50" align="center" headerAlign="center" allowSort="false">当前处理人</div>
            <div field="currentProcessTask" width="100" align="center" headerAlign="center">当前流程节点</div>
            <div field="instStatus"  width="60" headerAlign="center" align="center" allowSort="true" renderer="onStatusRenderer">流程状态</div>
            <div field="CREATE_TIME_" sortField="CREATE_TIME_" width="70" headerAlign="center" dateFormat="yyyy-MM-dd" align="center" allowSort="true">创建时间</div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var singleListGrid=mini.get("singleListGrid");
    var currentUserId="${currentUserId}";
    var currentUserNo="${currentUserNo}";


    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var id = record.id;
        var instId=record.instId;
        var currentProcessUserId=record.currentProcessUserId;
        var s = '<span  title="查看" onclick="singleInterfaceDetail(\'' + id +'\',\''+record.instStatus+ '\')">查看</span>';
        if(record.instStatus=='DRAFTED' && currentUserId ==record.CREATE_BY_) {
            s += '<span  title="编辑" onclick="devTaskEdit(\'' + id + '\',\'' + instId + '\')">编辑</span>';
        }
        if(currentProcessUserId && currentProcessUserId.indexOf(currentUserId) !=-1) {
            s+='<span  title="办理" onclick="devTaskDo(\'' + record.taskId + '\')">办理</span>';
        }

        var status=record.instStatus;
        if ((status == 'DISCARD_END' || status == 'DRAFTED') && currentUserId ==record.CREATE_BY_) {
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