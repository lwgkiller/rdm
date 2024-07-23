<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title></title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/zlghCpxpgh/cpxpghList.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">部门：</span>
                    <input class="mini-textbox" id="department" name="department"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">销售型号：</span>
                    <input class="mini-textbox" id="salesModel" name="salesModel"/>
                </li>

                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">设计型号：</span>
                    <input class="mini-textbox" id="designModel" name="designModel"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">产品状态：</span>
                    <input id="productStatus" name="productStatus" class="mini-combobox" style="width:120px;"
                       textField="value" valueField="key" emptyText="请选择..."
                       required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                       data="[{key:'lc',value:'量产'},{key:'kfz',value:'开发中'},{key:'ztkf',value:'暂停开发'},{key:'tc',value:'停产'}]"
                    />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">可售年份：</span>
                    <input class="mini-textbox" id="salsesYear" name="salsesYear"/>
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
                <br>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <div style="display: inline-block" class="separator"></div>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="cpxpghAdd()">新增</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="cpxpghDel()">删除</a>
                </li>
            </ul>
        </form>
        <span class="searchSelectionBtn" onclick="no_search(this ,'searchForm')">
				<i class="icon-sc-lower"></i>
        </span>
    </div>
</div>

<div class="mini-fit" style="height: 100%;">
    <div id="cpxpghGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         allowCellWrap="true"
         showCellTip="true" idField="id" allowCellEdit="true" allowCellSelect="true"
         multiSelect="true" showColumnsMenu="false" sizeList="[20,50,100,200]" pageSize="20" allowAlternating="true" pagerButtons="#pagerButtons"
         url="${ctxPath}/strategicplanning/core/cpxpgh/cpxpghListQuery.do">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div type="indexcolumn" width="35" headerAlign="center" align="center">序号</div>
            <div name="action" cellCls="actionIcons" width="140" headerAlign="center" align="center" renderer="onActionRenderer" cellStyle="padding:0;">操作</div>
            <div field="department" headerAlign="center" align="center" width="100">部门</div>
            <div field="salesModel" headerAlign="center" align="center" width="100">销售型号</div>
            <div field="designModel" headerAlign="center" align="center" width="100">设计型号</div>
            <div field="productStatus" headerAlign="center" align="center" width="70" renderer="productStatusRenderer">产品状态</div>
            <div field="salsesYear" headerAlign="center" align="center" width="60">可售年份</div>
            <div field="zjwsCost" headerAlign="center" align="center" width="90">整机未税成本</div>
            <div field="zjSalePrice" headerAlign="center" align="center" width="90">整机销售价格</div>
            <div field="zjbjgxRate" headerAlign="center" align="center" width="90">整机边际贡献率</div>
            <div field="archivedFile" headerAlign="center" align="center" width="90" renderer="archivedFileRenderer">产品归档文件</div>
            <div field="hbxxgk" headerAlign="center" align="center" width="90" renderer="hbxxgkRenderer">环保信息公开</div>
            <div field="cpxssyzt" headerAlign="center" align="center" width="90" renderer="cpxssyztRenderer">产品型式试验状态</div>
            <div field="cpckrzzt" headerAlign="center" align="center" width="90" renderer="cpckrzztRenderer">产品出口认证状态</div>
            <div field="cbsc" headerAlign="center" align="center" width="90" renderer="cbscRenderer">操保手册/零件图册</div>
            <div field="cptszt" headerAlign="center" align="center" width="90" renderer="cptsztRenderer">产品调试状态</div>
            <div field="cpcskhzt" headerAlign="center" align="center" width="90" renderer="cpcskhztRenderer">产品测试考核状态</div>
            <div field="taskStatus"  headerAlign="center" align="center" width="70" renderer="cpxpghTaskStatusRenderer">任务状态</div>
            <div field="taskName" align="center" headerAlign="center" width="80">当前任务</div>
            <div field="allTaskUserNames" headerAlign='center' align='center'>当前处理人</div>
            <div field="creator" align="center" headerAlign="center" width="60">创建者</div>
            <div field="CREATE_TIME_" dateFormat="yyyy-MM-dd HH:mm:ss" width="150" align="center" headerAlign="center" allowSort="true">创建时间</div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var cpxpghGrid = mini.get("cpxpghGrid");
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var isFgld = "${isFgld}";
    var mainGroupName = "${mainGroupName}";
    //所有非特殊文本都变成这个格式
    function render(e) {
        if (e.value != null && e.value != "") {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            return html;
        }
    }

    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var s = '<span  title="明细" onclick="cpxpghDetail(\'' + record.id + '\')">明细</span>';
        if (record.taskStatus == 'DRAFTED' && record.CREATE_BY_ == currentUserId) {
            s += '<span  title="编辑" onclick="cpxpghEdit(\'' + record.id + '\',\'' + record.instId + '\')">编辑</span>';
        }
        if (record.myTaskId) {
            s += '<span  title="办理" onclick="cpxpghTask(\'' + record.myTaskId + '\',\'' + record.taskStatus + '\')">办理</span>';
        }
        if (currentUserNo != 'admin') {
            if (record.taskStatus == 'DRAFTED' && currentUserId == record.CREATE_BY_) {
                s += '<span  title="删除" onclick="cpxpghDel(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
            }
        } else {
            s += '<span  title="删除" onclick="cpxpghDel(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
        }
        return s;
    }
</script>
<redxun:gridScript gridId="cpxpghGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>