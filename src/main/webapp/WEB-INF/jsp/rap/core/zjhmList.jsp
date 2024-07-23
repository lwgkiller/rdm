<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>整机豁免信息收集列表管理</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/rap/zjhmList.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">机械型号: </span>
                    <input class="mini-textbox" id="jxxh" name="jxxh"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">产品主管: </span>
                    <input class="mini-textbox" id="cpzzName" name="cpzzName"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">动力工程师: </span>
                    <input class="mini-textbox" id="dlgcsName" name="dlgcsName"/>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a id="editMsg" class="mini-button " style="margin-right: 5px" plain="true"
                       onclick="addZjhm()">新增</a>
                </li>
            </ul>
        </form>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="zjhmListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         sizeList="[50,100,200]" pageSize="50"
         url="${ctxPath}/environment/core/Zjhm/queryZjhm.do"
         idField="id" allowAlternating="true" showPager="true" multiSelect="true">
        <div property="columns">
            <div type="checkcolumn" width="10"></div>
            <div name="action" cellCls="actionIcons" width="50" headerAlign="center" align="center"
                 renderer="onMessageActionRenderer" cellStyle="padding:0;">操作
            </div>
            <div field="jxxh" headerAlign='center' align='center' width="40">机械型号</div>
            <div field="cpzgName" headerAlign='center' align='center' width="40">产品主管</div>
            <div field="dlgcsName"  width="80" headerAlign="center" align="center">动力工程师</div>
            <div field="fdjgkbh" headerAlign='center' width="50" align='center'>发动机信息公开编号</div>
            <div field="fdjzzs"  width="80" headerAlign="center" align="center" allowSort="true">发动机制造商</div>
            <div field="fdjxh"  width="80" headerAlign="center" align="center" allowSort="true">发动机型号</div>
            <div field="CREATE_TIME_" width="30" dateFormat="yyyy-MM-dd" headerAlign='center' align="center">创建时间</div>
            <div field="taskName" headerAlign='center' align='center' width="40">当前任务</div>
            <div field="allTaskUserNames" headerAlign='center' align='center' width="40">当前处理人</div>
            <div field="status" width="25" headerAlign="center" align="center" allowSort="true"
                 renderer="onStatusRenderer">状态
            </div>

        </div>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var zjhmListGrid = mini.get("zjhmListGrid");
    var currentUserId = "${currentUserId}";
    <%--var currentUserDep = "${currentUserDep}";--%>
    var currentUserNo = "${currentUserNo}";

    zjhmListGrid.on("update", function (e) {
        handGridButtons(e.sender.el);
    });

    function onMessageActionRenderer(e) {
        var record = e.record;
        var projectId = record.projectId;
        var instId = record.instId;
        var status = record.status;
        var s = '';
        if (status != 'DRAFTED'){
            s += '<span  title="明细" onclick="zjhmDetail(\'' + projectId + '\',\'' + status + '\')">明细</span>';
        }
        if (record.status == 'DRAFTED') {
            s += '<span  title="编辑" onclick="zjhmEdit(\'' + projectId + '\',\'' + instId + '\')">编辑</span>';
        }
        if(record.status =='RUNNING'){
            if(record.myTaskId) {
                s+='<span  title="办理" onclick="zjhmTask(\'' + record.myTaskId + '\')">办理</span>';
            }
        }
        if (status == 'DRAFTED' ) {
            s += '<span  title="删除" onclick="removezjhm(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
        }
        if (status == 'DISCARD_END' &&(currentUserId ==record.CREATE_BY_)) {
            s += '<span  title="删除" onclick="removezjhm(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
        }
        return s;
    }
    

    function onStatusRenderer(e) {
        var record = e.record;
        var status = record.status;

        var arr = [
            {'key' : 'DRAFTED','value' : '草稿','css' : 'orange'},
            {'key' : 'RUNNING','value' : '审批中','css' : 'green'},
            {'key' : 'SUCCESS_END','value' : '批准','css' : 'blue'},
            {'key' : 'DISCARD_END','value' : '作废','css' : 'red'}
        ];

        return $.formatItemValue(arr,status);
    }

</script>
<redxun:gridScript gridId="zjhmListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
