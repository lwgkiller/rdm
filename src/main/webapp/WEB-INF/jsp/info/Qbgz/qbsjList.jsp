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
    <title>情报收集列表管理</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/qbgz/qbsjList.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">情报编号: </span>
                    <input class="mini-textbox" id="qbNumber" name="qbNumber"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">情报名称: </span>
                    <input class="mini-textbox" id="qbName" name="qbName"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">情报大类: </span>
                    <input id="bigTypeName" name="bigTypeName" class="mini-combobox" style="width:150px;" textField="text"
                           valueField="id" emptyText="请选择..."
                           data="[{id:'产品情报',text:'产品情报'},{id:'研发体系',text:'研发体系'},{id:'技术创新',text:'技术创新'},
                         {id:'海外情报专题',text:'海外情报专题'},{id:'电动挖情报专题',text:'电动挖情报专题'},{id:'全球产业布局',text:'全球产业布局'}]"
                            allowInput="false" showNullItem="true" nullItemText="请选择..."
                           onvaluechanged="bigTypeValueChanged()"/>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">情报小类: </span>
                    <input id="qbTypeId" name="qbTypeId" class="mini-combobox" style="width:150px;" textField="smallTypeName"
                           valueField="id" emptyText="请选择..."
                            allowInput="false" showNullItem="true" nullItemText="请选择..."/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">情报价值: </span>
                    <input id="qbValue" name="qbValue" class="mini-combobox" style="width:150px;" textField="text"
                           valueField="id" emptyText="请选择..."
                           data="[{id:'高',text:'高'},{id:'中',text:'中'},{id:'低',text:'低'}]"
                           allowInput="false" showNullItem="true" nullItemText="请选择..."/>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px"  plain="true" onclick="myClearForm()">清空查询</a>
                    <div style="display: inline-block" class="separator"></div>
                    <a class="mini-button " style="margin-right: 5px;" plain="true" onclick="addQbsj()">新增</a>
                </li>
            </ul>
        </form>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="qbsjListGrid" class="mini-datagrid" style="height: 100%;" allowResize="false"
         sizeList="[50,100,200]" pageSize="50" url="${ctxPath}/Info/Qbsj/queryQbsj.do" idField="id" allowAlternating="true" showPager="true" multiSelect="true">
        <div property="columns">
            <div type="checkcolumn" width="28px"></div>
            <div name="action" cellCls="actionIcons" width="150px" headerAlign="center" align="center" renderer="onQbActionRenderer" cellStyle="padding:0;">操作</div>
            <div field="qbNumber" headerAlign='center' width="125px" align='center'>情报编号</div>
            <div field="qbName" headerAlign='center' width="300px" align='center'>情报名称</div>
            <div field="bigTypeName" width="140px" headerAlign="center" align="center" >情报大类</div>
            <div field="smallTypeName" width="185px" headerAlign="center" align="center">情报小类</div>
            <div field="qbValue" width="70px" headerAlign="center" align="center" allowSort="false">情报价值</div>
            <div field="qbSource" width="150px" headerAlign="center" align="center" allowSort="false">情报来源</div>
            <div field="qbKKX" width="130px" headerAlign="center" align="center" allowSort="false">情报可靠性</div>
            <div field="qbZQD" width="140px" headerAlign="center" align="center" allowSort="false">情报准确度</div>
            <div field="userName" headerAlign='center' align='center' width="75px">提交人</div>
            <div field="deptName" headerAlign='center' align='center' width="130px">提交部门</div>
            <div field="CREATE_TIME_" width="110px" dateFormat="yyyy-MM-dd" headerAlign='center' align="center">提交时间</div>
            <div field="currentProcessTask" headerAlign='center' align='center' width="120px">当前流程任务</div>
            <div field="currentProcessUser" headerAlign='center' align='center' width="120px">当前流程处理人</div>
            <div field="status" width="120px" headerAlign="center" align="center" allowSort="true" renderer="onStatusRenderer">流程状态</div>
        </div>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var qbsjListGrid = mini.get("qbsjListGrid");
    var isQbzy =${isQbzy};
    var currentUserId ="${currentUserId}";
    var currentUserNo ="${currentUserNo}";

    qbsjListGrid.on("update", function (e) {
        handGridButtons(e.sender.el);
    });

    function onQbActionRenderer(e) {
        var record = e.record;
        var qbgzId = record.id;
        var instId = record.instId;
        var status = record.status;
        var s = '';
        if (status != 'DRAFTED'){
            s += '<span  title="查看" onclick="qbsjDetail(\'' + qbgzId + '\',\'' + instId + '\')">查看</span>';
        }
        if (record.status == 'DRAFTED') {
            s += '<span  title="编辑" onclick="qbsjEdit(\'' + qbgzId + '\',\'' + instId + '\')">编辑</span>';
        }
        if(record.status =='RUNNING'){
            var currentProcessUserId=record.currentProcessUserId;
            if(currentProcessUserId && currentProcessUserId.indexOf(currentUserId) !=-1) {
                s+='<span  title="办理" onclick="qbsjTask(\'' + record.taskId + '\')">办理</span>';
            }
        }
        if (status == 'DRAFTED' ) {
            s += '<span  title="删除" onclick="removeQbsj(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
        }
        if (status == 'DISCARD_END' &&(currentUserNo=='admin'||isQbzy)) {
            s += '<span  title="删除" onclick="removeQbsj(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
        }
        return s;
    }

    function onStatusRenderer(e) {
        var record = e.record;
        var status = record.status;
        var arr = [ {'key' : 'DRAFTED','value' : '草稿','css' : 'orange'},
            {'key' : 'RUNNING','value' : '审批中','css' : 'green'},
            {'key' : 'SUCCESS_END','value' : '批准','css' : 'blue'},
            {'key' : 'DISCARD_END','value' : '作废','css' : 'red'}
        ];
        return $.formatItemValue(arr,status);
    }

</script>
<redxun:gridScript gridId="qbsjListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
