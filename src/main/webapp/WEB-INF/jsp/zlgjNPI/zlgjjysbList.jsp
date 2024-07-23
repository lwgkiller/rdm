<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>质量改进建议申报</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/zlgjNPI/zlgjjysbList.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">项目名称：</span>
                    <input class="mini-textbox" id="projectName" name="projectName"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">申报单位：</span>
                    <input class="mini-textbox" style="width:80px" id="applicationUnit" name="applicationUnit"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">申报人：</span>
                    <input class="mini-textbox" style="width:80px" id="creator" name="creator"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">建议对策部门：</span>
                    <input class="mini-textbox" id="suggestDepartment" name="suggestDepartment"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">问题大类：</span>
                    <input id="wtdl" name="wtdl" class="mini-combobox" style="width:120px;"
                           textField="value" valueField="key" emptyText="请选择..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                           data="[{key:'部品检验',value:'部品检验'},{key:'结构检验',value:'结构检验'},{key:'装配',value:'装配'},
                                              {key:'焊接',value:'焊接'},{key:'涂装',value: '涂装'},{key:'产品建议',value:'产品建议'}]"
                    />
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
                <li style="margin-right: 5px">
                    <span class="text" style="width:auto">结束时间 </span>:<input id="endTimeStart"  name="endTimeStart"  class="mini-datepicker" format="yyyy-MM-dd" style="width:120px"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text-to" style="width:auto">至</span><input  id="endTimeEnd" name="endTimeEnd" class="mini-datepicker" format="yyyy-MM-dd" style="width:120px" />
                </li>
                <br/>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <div style="display: inline-block" class="separator"></div>
                    <a class="mini-button" style="margin-right: 5px" plain="true" id="addBtn" onclick="addZlgjjysb()">新增</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="delZlgjjysb()">删除</a>
                    <a class="mini-button" style="margin-left: 10px" plain="true" onclick="exportZlgjjysb()">导出</a>
                    <div style="display: inline-block" class="separator"></div>
                    <a id="lockAddBtn" class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="addBtnProcess('lock')">锁定</a>
                    <a id="unlockAddBtn" class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="addBtnProcess('unlock')">解锁</a>
                    <div style="display: inline-block" class="separator"></div>
                    <a id="callBackTest" class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="callBackTest()">手工回调CRM-打作废</a>
                    <div style="width:500px;display:inline-block;">
                        <input class="mini-textbox" id="zlshshyjMock" name="zlshshyjMock" style="width:100%"
                               emptyText="作废原因请在此输入"/>
                    </div>
                    <%--<a id="lockAddBtnttt" class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="tttt()">ttt</a>--%>
                </li>
            </ul>
        </form>
    </div>
</div>
<!--导出Excel相关HTML-->
<form id="excelForm" action="${ctxPath}/zlgjNPI/core/zlgjjysb/exportZlgjjysb.do" method="post" target="excelIFrame">
    <input type="hidden" name="filter" id="filter"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>
<div class="mini-fit" style="height: 100%;">
    <div id="zlgjjysbGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onlyCheckSelection="true"
         url="${ctxPath}/zlgjNPI/core/zlgjjysb/zlgjjysbListQuery.do" idField="id"
         multiSelect="true" showColumnsMenu="true" sizeList="[20,50,100]" pageSize="20" allowAlternating="true" pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div type="indexcolumn" width="30" headerAlign="center" align="center">序号</div>
            <div name="action" cellCls="actionIcons" width="120" headerAlign="center" align="center" renderer="onActionRenderer"
                 cellStyle="padding:0;">操作
            </div>
            <div field="projectName" width="200" headerAlign="center" align="center" allowSort="false">项目名称</div>
            <div field="creator" width="80" headerAlign="center" align="center" allowSort="false">申报人</div>
            <div field="applicationUnit" width="90" headerAlign="center" align="center" allowSort="false">申报单位</div>
            <div field="phoneNumber" width="100" headerAlign="center" align="center" allowSort="false">电话</div>
            <div field="declareTime" headerAlign="center" align="center" allowSort="false">申报日期</div>
            <div field="wtdl" headerAlign="center" align="center" allowSort="false">问题大类</div>
            <div field="wtxl" headerAlign="center" align="center" allowSort="false">问题小类</div>
            <div field="suggestDepartment" headerAlign="center" align="center" allowSort="false">建议对策部门</div>
            <div field="taskName" headerAlign='center' align='center'>当前节点</div>
            <div field="allTaskUserNames" headerAlign='center' align='center'>当前处理人</div>
            <div field="taskStatus" headerAlign="center" align="center" renderer="taskStatusRenderer">任务状态</div>
            <div name="callBackResult" field="callBackResult" headerAlign="center" align="center">是否回传CRM</div>
            <div field="endTime" dateFormat="yyyy-MM-dd" align="center" headerAlign="center" allowSort="true">结束时间</div>
        </div>
    </div>
</div>


<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var currentUserNo = "${currentUserNo}";
    var currentUserId = "${currentUserId}";
    var zlgjjysbGrid = mini.get("zlgjjysbGrid");
    var isGJJYZY =${isGJJYZY};
    var crmToZlgjjyAgent = "${crmToZlgjjyAgent}";

    //操作栏
    zlgjjysbGrid.on("update", function (e) {
        handGridButtons(e.sender.el);
    });
    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var s = '<span  title="明细" onclick="toZlgjjysbDetail(\'' + record.id + '\')">明细</span>';
        if (record.taskStatus == 'DRAFTED' && record.CREATE_BY_ == currentUserId) {
            s += '<span  title="编辑" onclick="editZlgjjysb(\'' + record.instId + '\')">编辑</span>';
        }
        if (record.myTaskId) {
            s += '<span  title="办理" onclick="zlgjjysbTask(\'' + record.myTaskId + '\')">办理</span>';
        }
        if (currentUserNo != 'admin') {
            if (record.taskStatus == 'DRAFTED' && currentUserId == record.CREATE_BY_) {
                s += '<span  title="删除" onclick="delZlgjjysb(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
            }
        } else {
            s += '<span  title="删除" onclick="delZlgjjysb(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
        }
        return s;
    }

    function addBtnProcess(status) {
        if (status == 'lock') {
            mini.get("addBtn").setEnabled(false);
        }
        if (status == 'unlock') {
            mini.get("addBtn").setEnabled(true);
        }
    }

    //..手工回调
    function callBackTest() {
        var rows = zlgjjysbGrid.getSelecteds();
        if (rows.length != 1) {
            mini.alert("只能选中一条记录!");
            return;
        }
        if (rows[0].taskName != '改进建议编制' && rows[0].taskStatus != 'DRAFTED' && rows[0].taskStatus != 'DISCARD_END') {
            mini.alert("只有'改进建议编制','草稿','作废'状态的记录能够手工回调CRM!");
            return;
        }
        if (!mini.get("zlshshyjMock").getValue() ||
            mini.get("zlshshyjMock").getValue().trim() == '') {
            mini.alert("请输入作废的原因!");
            return;
        }
        mini.confirm("此功能仅为特殊情况下进行发送，用来标记CRM端的作废，轻易不要使用！！！确认发送吗？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                $.ajax({
                    url: jsUseCtxPath + '/zlgjNPI/core/zlgjjysb/callBackOutSystemTest.do?businessId=' +
                    rows[0].id + '&okOrNot=3&zlshshyjMock=' + mini.get("zlshshyjMock").getValue(),
                    async: true,
                    success: function (result) {
                        mini.alert(result.message);
                        searchFrm();
                    }
                })
            }
        });
    }

/*    function tttt() {
        $.ajax({
            url: jsUseCtxPath + '/zlgjNPI/core/zlgjjysb/test.do',
            async: true,
            success: function (result) {

            }
        })
    }*/
</script>
<redxun:gridScript gridId="zlgjjysbGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>

