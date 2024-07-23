<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>管理改进建议提报</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/zlgjNPI/manageImproveList.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">提报单号: </span>
                    <input class="mini-textbox" id="suggestionApplynum" name="suggestionApplynum" />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">核心流程: </span>
                    <input id="coreTypeId" name="coreTypeId" class="mini-combobox" style="width:98%;"
                           textField="value" valueField="key" emptyText="请选择..."

                           data="[ {'key' : '设计门径管理','value' : '设计门径管理'},{'key' : '工艺管理','value' : '工艺管理'},
                                               {'key' : '变更管理','value' : '变更管理'},{'key' : '供应商开发评价管理','value' : '供应商开发评价管理'},
                                               {'key' : '供应商过程&质量管理','value' : '供应商过程&质量管理'},{'key' : '生产过程管控','value' : '生产过程管控'},
                                               {'key' : '客户/订单管理','value' : '客户/订单管理'},{'key' : '生产过程检查控制','value' : '生产过程检查控制'},
                                               {'key' : '产品发运交付控制','value' : '产品发运交付控制'},{'key' : '其他问题','value' : '其他问题'},]"
                           required="true" allowInput="false" showNullItem="false" nullItemText="请选择..."
                    />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">业务需求：</span>
                    <input id="businessRequest" name="businessRequest" class="mini-combobox" style="width:120px;"
                           textField="value" valueField="key" emptyText="请选择..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                           data="[{key:'部品检验',value:'上游输入需求'},{key:'业务支持',value:'业务支持'},{key:'顾客需求',value:'顾客需求'}]"
                    />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">问题类型: </span>
                    <input id="problemType" name="problemType" class="mini-combobox" style="width:120px;"
                           textField="value" valueField="key" emptyText=""
                           required="false" allowInput="false" showNullItem="true" nullItemText=""
                           data="[{key:'效率',value:'效率'},{key:'符合率',value:'符合率'}]"
                    />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">建议对策实施部门：</span>
                    <input id="improveDepartment" name="improveDepartment" class="mini-dep rxc" plugins="mini-dep"
                           style="width:98%;height:34px"
                           allowinput="false" textname="improveDepartment" length="200" maxlength="200" minlen="0" single="false"
                           initlogindep="false"/>
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
                <br/>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <div style="display: inline-block" class="separator"></div>
                    <a class="mini-button" style="margin-right: 5px" plain="true" id="addBtn" onclick="addManageImproveTb()">新增</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="delManageImproveTb()">删除</a>
                </li>
            </ul>
        </form>
    </div>
</div>


<div class="mini-fit" style="height: 100%;">
    <div id="manageImproveGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onlyCheckSelection="true"
         url="${ctxPath}/zlgjNPI/core/manageImprove/manageImproveListQuery.do" idField="id"
         multiSelect="true" showColumnsMenu="true" sizeList="[20,50,100]" pageSize="20" allowAlternating="true" pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div type="indexcolumn" width="28" headerAlign="center" align="center">序号</div>
            <div name="action" cellCls="actionIcons" width="120" headerAlign="center" align="center" renderer="onActionRenderer"
                 cellStyle="padding:0;">操作
            </div>
            <div field="suggestionApplynum" headerAlign="center" align="center" allowSort="false">提报单号</div>
            <div field="coreTypeId" headerAlign="center" align="center" allowSort="false">核心流程类型</div>
            <div field="subTypeId" headerAlign="center" align="center" allowSort="false">子流程类型</div>
            <div field="businessRequest" headerAlign="center" align="center" allowSort="false">业务需求</div>
            <div field="problemType" headerAlign="center" align="center" allowSort="false">问题类型</div>
            <div field="improveDepartment" headerAlign="center" align="center" allowSort="false">建议实施部门</div>
            <div field="problemDescription" headerAlign="center" align="center" allowSort="false">具体问题描述</div>
            <div field="taskName" headerAlign='center' align='center'>当前节点</div>
            <div field="allTaskUserNames" headerAlign='center' align='center'>当前处理人</div>
            <div field="taskStatus" headerAlign="center" align="center" renderer="taskStatusRenderer">任务状态</div>
        </div>
    </div>
</div>


<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var currentUserNo = "${currentUserNo}";
    var currentUserId = "${currentUserId}";
    var currentUserName = "${currentUserName}";
    var manageImproveGrid = mini.get("manageImproveGrid");


    //操作栏
    manageImproveGrid.on("update", function (e) {
        handGridButtons(e.sender.el);
    });
    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var s = '<span  title="明细" onclick="manageImproveDetail(\'' + record.id + '\')">明细</span>';
        if (record.taskStatus == 'DRAFTED' && record.CREATE_BY_ == currentUserId) {
            s += '<span  title="编辑" onclick="editManageImprove(\'' + record.instId + '\')">编辑</span>';
        }
        if (record.myTaskId) {
            s += '<span  title="办理" onclick="manageImproveTask(\'' + record.myTaskId + '\')">办理</span>';
        }
        if (currentUserNo != 'admin') {
            if (record.taskStatus == 'DRAFTED' && currentUserId == record.CREATE_BY_) {
                s += '<span  title="删除" onclick="delmanageImprove(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
            }
        } else {
            s += '<span  title="删除" onclick="delmanageImprove(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
        }
        return s;
    }
    function taskStatusRenderer(e) {
        var record = e.record;
        var taskStatus = record.taskStatus;
        var arr = [{'key': 'DRAFTED', 'value': '草稿', 'css': 'orange'},
            {'key': 'RUNNING', 'value': '运行中', 'css': 'green'},
            {'key': 'SUCCESS_END', 'value': '成功结束', 'css': 'blue'},
            {'key': 'DISCARD_END', 'value': '作废', 'css': 'red'},
            {'key': 'ABORT_END', 'value': '异常中止结束', 'css': 'red'},
            {'key': 'PENDING', 'value': '挂起', 'css': 'gray'}
        ];
        return $.formatItemValue(arr, taskStatus);
    }

</script>
<redxun:gridScript gridId="manageImproveGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>

