<%--
  Created by IntelliJ IDEA.
  User: matianyu
  Date: 2021/2/23
  Time: 14:57
  To change this template use File | Settings | File Templates.
--%>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>列表管理</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/xcmgjssjk/jssbList.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">技术名称: </span>
                    <input class="mini-textbox" id="jsName" name="jsName"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">专业方向: </span>
                    <input id="direction" name="direction" class="mini-combobox" style="width:150px;"
                           textField="value" valueField="key" emptyText="请选择..."
                           required="false" allowInput="false" showNullItem="true"
                           nullItemText="请选择..."
                           data="[
                            {'key' : '液压','value' : '液压'},{'key' : '结构','value' : '结构'}
                           ,{'key' : '动力','value' : '动力'},{'key' : '控制','value' : '控制'}
                           ,{'key' : '电气','value' : '电气'},{'key' : '仿真','value' : '仿真'}
                           ,{'key' : '测试','value' : '测试'},{'key' : '其他','value' : '其他'}]"
                    />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">申报时间 从 </span>:<input id="publishTimeStart"  name="publishTimeStart"  class="mini-datepicker" format="yyyy-MM-dd" style="width:120px"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text-to" style="width:auto">至: </span><input  id="publishTimeEnd" name="publishTimeEnd" class="mini-datepicker" format="yyyy-MM-dd" style="width:120px" />
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a id="editMsg" class="mini-button " style="margin-right: 5px" plain="true"
                       onclick="addJssb()">新增</a>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="exportJssb()">导出</a>
                </li>
            </ul>
        </form>
        <span class="searchSelectionBtn" onclick="no_search(this ,'searchForm')">
				<i class="icon-sc-lower"></i>
			</span>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="jssbListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         sizeList="[50,100,200]" pageSize="50"
         url="${ctxPath}/Jssb/queryJssb.do"
         idField="id" allowAlternating="true" showPager="true" multiSelect="true">
        <div property="columns">
            <div name="checkcolumn" field="checkcolumn" type="checkcolumn" width="10"></div>
            <div name="action" field="action" cellCls="actionIcons" width="50" headerAlign="center" align="center"
                 renderer="onMessageActionRenderer" cellStyle="padding:0;">操作
            </div>
            <div name="jsName" field="jsName" headerAlign='center' align='center' width="80">技术名称</div>
            <div name="direction" field="direction" headerAlign='center' align='center' width="40">专业方向</div>
            <div name="startTime" field="startTime"  width="40" headerAlign="center" align="center" allowSort="true" dateFormat="yyyy-MM-dd">预计开始时间</div>
            <div name="needTime" field="needTime" headerAlign='center' width="40" align='center' dateFormat="yyyy-MM-dd">预计完成时间</div>
            <div name="sbRes" field="sbRes"  width="30" headerAlign="center" align="center" allowSort="true">申报人</div>
            <div name="sbDep" field="sbDep"  width="40" headerAlign="center" align="center" allowSort="true">申报部门</div>
            <div name="sbTime" field="sbTime" width="40" dateFormat="yyyy-MM-dd" headerAlign='center' align="center">申报时间</div>
            <div name="taskName" field="taskName" headerAlign='center' align='center' width="40">当前任务</div>
            <div name="allTaskUserNames" field="allTaskUserNames" headerAlign='center' align='center' width="40">当前处理人</div>
            <div name="status" field="status" width="25" headerAlign="center" align="center" allowSort="true"
                 renderer="onStatusRenderer">状态
            </div>
        </div>
    </div>
</div>
<form id="excelForm" action="${ctxPath}/Jssb/exportJssbList.do" method="post" target="excelIFrame">
    <input type="hidden" name="filter" id="filter"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var jssbListGrid = mini.get("jssbListGrid");
    var currentUserId = "${currentUserId}";
    var currentUserDep = "${currentUserDep}";
    var currentUserNo = "${currentUserNo}";


    function onMessageActionRenderer(e) {
        var record = e.record;
        var jssbId = record.jssbId;
        var instId = record.instId;
        var status = record.status;
        var s = '';
        if (status != 'DRAFTED'){
            s += '<span  title="明细" onclick="jssbDetail(\'' + jssbId + '\',\'' + status + '\')">明细</span>';
        }
        if (record.status == 'DRAFTED') {
            s += '<span  title="编辑" onclick="jssbEdit(\'' + jssbId + '\',\'' + instId + '\')">编辑</span>';
        }
        if(record.status =='RUNNING'){
            if(record.myTaskId) {
                s+='<span  title="办理" onclick="jssbTask(\'' + record.myTaskId + '\')">办理</span>';
            }
        }
        if (status == 'DRAFTED' ) {
            s += '<span  title="删除" onclick="removeJssb(\'' + jssbId + '\')">删除</span>';
        }
        if (status == 'DISCARD_END' &&(currentUserId ==record.CREATE_BY_)) {
            s += '<span  title="删除" onclick="removeJssb(\'' + jssbId + '\')">删除</span>';
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
<redxun:gridScript gridId="jssbListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
