<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>仿真任务列表</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/fzsj/fzrwhztList.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">仿真编号：</span>
                    <input class="mini-textbox" id="fzNumber" name="fzNumber"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">任务名称：</span>
                    <input class="mini-textbox" id="questName" name="questName"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">申请人：</span>
                    <input class="mini-textbox" id="creator" name="creator"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">申请部门：</span>
                    <input class="mini-textbox" id="department" name="department"/>
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
                    <span class="text" style="width:auto">仿真对象：</span>
                    <input class="mini-textbox" id="fzdx" name="fzdx"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">仿真领域：</span>
                    <input id="field" name="field" class="mini-combobox" style="width:120px;"
                           textField="text" valueField="key_" emptyText="请选择..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">改进意见：</span>
                    <input id="gjyj" name="gjyj" class="mini-combobox" style="width:120px;"
                           textField="text" valueField="id" emptyText="请选择..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                           data="[{id:'tygj',text:'同意改进'},{id:'bfgj',text:'部分改进'},{id:'btygj',text:'不同意改进'}]"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">当前节点: </span>
                    <input id="taskName" name="taskName" class="mini-combobox" style="width:120px;"
                           textField="value" valueField="value"
                           required="false" allowInput="false" showNullItem="true"/>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                </li>
            </ul>
        </form>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="fzrwhzGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onlyCheckSelection="true"
         url="${ctxPath}/fzsj/core/fzsj/fzrwhzListQuery.do" idField="id"
         multiSelect="true" showColumnsMenu="true" sizeList="[20,50,100]" pageSize="20" allowAlternating="true" pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div type="indexcolumn" width="28" headerAlign="center" align="center">序号</div>
            <div name="action" cellCls="actionIcons" width="50" headerAlign="center" align="center" renderer="onActionRenderer"
                 cellStyle="padding:0;">操作
            </div>
            <div field="fzNumber" headerAlign="center" width="130" align="center" allowSort="false">仿真编号</div>
            <div field="questName" headerAlign="center" align="center" allowSort="false">任务名称</div>
            <div field="applicationType" headerAlign="center" align="center" allowSort="false">应用机型</div>
            <div field="taskResource" headerAlign="center" align="center" allowSort="false" renderer="taskResourceRenderer" width="80">任务来源</div>
            <div field="prototypeState" headerAlign="center" align="center" allowSort="false" renderer="prototypeStateRenderer" width="50">样机状态</div>
            <div field="fzdx" headerAlign="center" align="center" allowSort="false" width="60">仿真对象</div>
            <div field="field" headerAlign="center" align="center" width="80" renderer="fieldRenderer">仿真领域</div>
            <div field="fzlb" headerAlign="center" align="center" allowSort="false" width="80">仿真分析项</div>
            <div field="zxry" headerAlign="center" align="center" allowSort="false">所有仿真执行人员</div>
            <div field="gjyj" headerAlign="center" align="center" allowSort="false" renderer="gjztRender">改进状态</div>
            <div field="ldjsspsj" dateFormat="yyyy-MM-dd" align="center" headerAlign="center" allowSort="true">领导审批结束时间</div>
            <div field="creator" headerAlign="center" align="center" width="50">申请人</div>
            <div field="department" headerAlign="center" align="center" width="60">申请部门</div>
            <div field="applyTime" dateFormat="yyyy-MM-dd" align="center" headerAlign="center" allowSort="true" width="80">申请时间</div>
            <div field="taskName" headerAlign='center' align='center' width="80">当前节点</div>
            <div field="allTaskUserNames" headerAlign='center' align='center' width="80">当前处理人</div>
            <%--<div field="taskStatus"  headerAlign="center" align="center" renderer="taskStatusRenderer" width="60">任务状态</div>--%>
            <%--<div field="CREATE_TIME_" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" headerAlign="center" allowSort="true">创建时间</div>--%>
        </div>
    </div>
</div>


<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var currentUserNo = "${currentUserNo}";
    var currentUserId = "${currentUserId}";
    var fzrwhzGrid = mini.get("fzrwhzGrid");
    var fzlyList = getDics("FZLY");
    var nodeSetListWithName = '${nodeSetListWithName}';
    mini.get("field").setData(fzlyList);
    //操作栏
    fzrwhzGrid.on("update", function (e) {
        handGridButtons(e.sender.el);
    });
    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var s = '<span  title="明细" onclick="toFzrwhzDetail(\'' + record.id + '\')">明细</span>';
        return s;
    }

    function fieldRenderer(e) {
        for (var i = 0; i < fzlyList.length; i++) {
            if (e.value == fzlyList[i].key_) {
                return fzlyList[i].text;
            }
        }
        return '';
    }

</script>
<redxun:gridScript gridId="fzrwhzGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>

