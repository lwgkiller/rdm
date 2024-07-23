<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>仿真设计-个人工作台</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/fzsj/grgztList.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">任务名称：</span>
                    <input class="mini-textbox" id="questName" name="questName"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">应用机型：</span>
                    <input class="mini-textbox" id="applicationType" name="applicationType"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">任务来源：</span>
                    <input id="taskResource" name="taskResource" class="mini-combobox" style="width:120px;"
                           textField="text" valueField="id" emptyText="请选择..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                           data="[{id:'xpyf',text:'新品研发'},{id:'zyOrYycp',text:'在研/预研产品'},
                                    {id:'scgj',text:'市场改进'},{id:'hxjsyj',text:'核心技术研究'}]"
                    />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">样机状态：</span>
                    <input id="prototypeState" name="prototypeState" class="mini-combobox" style="width:120px;"
                           textField="text" valueField="id" emptyText="请选择..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                           data="[{id:'yyj',text:'有样机'},{id:'wyj',text:'无样机'}]"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">仿真分析项：</span>
                    <input class="mini-textbox" id="fzlb" name="fzlb"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">申请时间：</span>
                    <input id="applyStartTime" name="applyStartTime" format="yyyy-MM-dd" class="mini-datepicker" allowInput="false"/>
                    <span class="text" style="width:auto">-</span>
                    <input id="applyEndTime" name="applyEndTime" format="yyyy-MM-dd" class="mini-datepicker" allowInput="false"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">任务状态：</span>
                    <input id="taskStatus" name="taskStatus" class="mini-combobox" style="width:120px;"
                           textField="value" valueField="key" emptyText="请选择..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                           data="[{'key': 'DRAFTED', 'value': '草稿', 'css': 'orange'},{'key': 'RUNNING', 'value': '运行中', 'css': 'green'},
                                    {'key': 'SUCCESS_END', 'value': '成功结束', 'css': 'blue'},{'key': 'DISCARD_END', 'value': '作废', 'css': 'red'},
                                    {'key': 'ABORT_END', 'value': '异常中止结束', 'css': 'red'},{'key': 'PENDING', 'value': '挂起', 'css': 'gray'}]"
                    />
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <div style="display: inline-block" class="separator"></div>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="addGrgzt()">新增</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="delGrgzt()">删除</a>
                </li>
            </ul>
        </form>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="grgztGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onlyCheckSelection="true"
         url="${ctxPath}/fzsj/core/fzsj/grgztListQuery.do" idField="id"
         multiSelect="true" showColumnsMenu="true" sizeList="[20,50,100]" pageSize="20" allowAlternating="true" pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div type="indexcolumn" width="28" headerAlign="center" align="center">序号</div>
            <div name="action" cellCls="actionIcons" width="120" headerAlign="center" align="center" renderer="onActionRenderer"
                 cellStyle="padding:0;">操作
            </div>
            <div field="fzNumber" headerAlign="center" width="130" align="center" allowSort="false">仿真编号</div>
            <div field="questName" headerAlign="center" align="center" allowSort="false">任务名称</div>
            <div field="idUrgent" headerAlign="center" align="center" allowSort="false" width="60">是否紧急</div>
            <div field="applicationType" headerAlign="center" align="center" allowSort="false">应用机型</div>
            <div field="taskResource" headerAlign="center" align="center" allowSort="false" renderer="taskResourceRenderer" width="80">任务来源</div>
            <div field="prototypeState" headerAlign="center" align="center" allowSort="false" renderer="prototypeStateRenderer" width="60">样机状态</div>
            <div field="fzdx" headerAlign="center" align="center" allowSort="false" width="80">仿真对象</div>
            <div field="fzlb" headerAlign="center" align="center" allowSort="false" width="80">仿真分析项</div>
            <div field="applyTime" dateFormat="yyyy-MM-dd" align="center" headerAlign="center" allowSort="true" width="80">申请时间</div>
            <div field="taskName" headerAlign='center' align='center' width="80">当前节点</div>
            <div field="allTaskUserNames" headerAlign='center' align='center'>当前处理人</div>
            <div field="taskStatus" headerAlign="center" align="center" renderer="taskStatusRenderer" width="80">任务状态</div>
            <%--<div field="creator"  headerAlign="center" align="center" width="60">创建者</div>--%>
            <%--<div field="CREATE_TIME_" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" headerAlign="center" allowSort="true">创建时间</div>--%>
        </div>
    </div>
</div>


<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var currentUserNo = "${currentUserNo}";
    var currentUserId = "${currentUserId}";
    var grgztGrid = mini.get("grgztGrid");
    //操作栏
    grgztGrid.on("update", function (e) {
        handGridButtons(e.sender.el);
    });
    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var s = '<span  title="明细" onclick="toGrgztDetail(\'' + record.id + '\')">明细</span>';
        if (record.taskStatus == 'DRAFTED' && record.CREATE_BY_ == currentUserId) {
            s += '<span  title="编辑" onclick="editGrgzt(\'' + record.instId + '\')">编辑</span>';
        }
        if (record.myTaskId) {
            s += '<span  title="办理" onclick="grgztTask(\'' + record.myTaskId + '\')">办理</span>';
        }
        if (currentUserNo != 'admin') {
            if ((record.taskStatus == 'DRAFTED' || record.taskStatus == 'DISCARD_END') && currentUserId == record.CREATE_BY_) {
                s += '<span  title="删除" onclick="delGrgzt(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
            }
        } else {
            s += '<span  title="删除" onclick="delGrgzt(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
        }
        return s;
    }
</script>
<redxun:gridScript gridId="grgztGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>

