<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title></title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<%--工具栏--%>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right:15px">
                    <span class="text" style="width:auto">试验编号：</span>
                    <input class="mini-textbox" id="testNo" name="testNo" style="width:100%;"/>
                </li>
                <li style="margin-right:15px">
                    <span class="text" style="width:auto">专业类别：</span>
                    <input id="componentCategory" name="componentCategory"
                           class="mini-combobox" style="width:98%"
                           url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=componentCategory"
                           valueField="key" textField="value"/>
                </li>
                <li style="margin-right:15px">
                    <span class="text" style="width:auto">样品名称：</span>
                    <input class="mini-textbox" id="componentName" name="componentName" style="width:100%;"/>
                </li>
                <li style="margin-right:15px">
                    <span class="text" style="width:auto">型号规格：</span>
                    <input class="mini-textbox" id="componentModel" name="componentModel" style="width:100%;"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">物料编码：</span>
                    <input class="mini-textbox" id="materialCode" name="materialCode"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">样品类型：</span>
                    <input id="sampleType" name="sampleType"
                           class="mini-combobox" style="width:98%"
                           url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=sampleType"
                           valueField="key" textField="value"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">配套主机型号：</span>
                    <input class="mini-textbox" id="machineModel" name="machineModel"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">供应商名称：</span>
                    <input class="mini-textbox" id="supplierName" name="supplierName"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">承担单位：</span>
                    <input class="mini-textbox" id="laboratory" name="laboratory"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">试验类型：</span>
                    <input id="testType" name="testType"
                           class="mini-combobox" style="width:98%"
                           url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=testType"
                           valueField="key" textField="value"/>
                </li>
                <li class="liBtn">
					<span class="unfoldBtn" onclick="no_more(this,'moreBox')">
						<em>展开</em>
						<i class="unfoldIcon"></i>
					</span>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">审批状态: </span>
                    <input id="businessStatus" name="businessStatus" class="mini-combobox" style="width:120px;"
                           textField="value" valueField="key" emptyText="请选择..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">关联科技项目: </span>
                    <input id="projectId" name="projectId" textName="projectName" style="width:98%;height:34px;"
                           class="mini-buttonedit" showClose="true" allowInput="false"
                           oncloseclick="selectProjectCloseClick()" onbuttonclick="selectProjectClick()"/>
                </li>
                <li style="margin-left: 10px">
                    <f:a alias="componentTestApply-searchFrm" onclick="searchFrm()" showNoRight="false" style="margin-right: 5px">查询</f:a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <f:a alias="componentTestApply-addBusiness" onclick="addBusiness()" showNoRight="false" style="margin-right: 5px">新增</f:a>
                    <f:a alias="componentTestApply-removeBusiness" onclick="removeBusiness()" showNoRight="false" style="margin-right: 5px">删除</f:a>
                    <f:a alias="componentTestApply-exportBusiness" onclick="exportBusiness()" showNoRight="false" style="margin-right: 5px">导出</f:a>
                </li>
            </ul>
            <div id="moreBox">
                <ul>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">申请人：</span>
                        <input class="mini-textbox" id="applyUser" name="applyUser"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">申请部门：</span>
                        <input class="mini-textbox" id="applyDep" name="applyDep"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">试验专业负责人：</span>
                        <input class="mini-textbox" id="testMajorLeader" name="testLeader"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">试验负责人：</span>
                        <input class="mini-textbox" id="testLeader" name="testLeader"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">试验计划类型：</span>
                        <input id="testCategory" name="testCategory"
                               class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=testCategory"
                               valueField="key" textField="value"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">试验运行状态：</span>
                        <input id="testStatus" name="testStatus"
                               class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=testStatus"
                               valueField="key" textField="value"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">测试方案状态：</span>
                        <input id="testContractStatus" name="testContractStatus"
                               class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=testContractStatus"
                               valueField="key" textField="value"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">试验结果：</span>
                        <input id="testResult" name="testResult"
                               class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=testResult"
                               valueField="key" textField="value"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">计划试验开始时间：</span>
                        <input id="plannedTestMonthBegin" name="plannedTestMonthBegin" class="mini-monthpicker" allowinput="false"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text-to" style="width:auto">至：</span>
                        <input id="plannedTestMonthEnd" name="plannedTestMonthEnd" class="mini-monthpicker" allowinput="false"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">测试开始时间：</span>
                        <input id="actualTestMonthBegin" name="actualTestMonthBegin" class="mini-monthpicker" allowinput="false"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text-to" style="width:auto">至：</span>
                        <input id="actualTestMonthEnd" name="actualTestMonthEnd" class="mini-monthpicker" allowinput="false"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">测试完成时间：</span>
                        <input id="completeTestMonthBegin" name="completeTestMonthBegin" class="mini-monthpicker" allowinput="false"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text-to" style="width:auto">至：</span>
                        <input id="completeTestMonthEnd" name="completeTestMonthEnd" class="mini-monthpicker" allowinput="false"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text-to" style="width:auto">计划试验年份：</span>
                        <input id="signYear" name="signYear"
                               class="mini-combobox"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=signYear"
                               valueField="key" textField="value"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">样品来源：</span>
                        <input id="sampleSource" name="sampleSource"
                               class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=sampleSource"
                               valueField="key" textField="value"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">样品处理方式：</span>
                        <input id="sampleProcessingMethod" name="sampleProcessingMethod"
                               class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=sampleProcessingMethod"
                               valueField="key" textField="value"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">样品当前状态：</span>
                        <input id="sampleStatus" name="sampleStatus"
                               class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=sampleStatus"
                               valueField="key" textField="value"/>
                    </li>
                </ul>
            </div>
        </form>
    </div>
</div>
<%--列表视图--%>
<div class="mini-fit" style="height: 100%;">
    <div id="businessListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         allowCellWrap="true"
         showCellTip="true" idField="id" allowCellEdit="true" allowCellSelect="true" oncellvalidation="onCellValidation"
         multiSelect="true" showColumnsMenu="false" sizeList="[50,100]" pageSize="50" allowAlternating="true"
         pagerButtons="#pagerButtons"
         url="${ctxPath}/componentTest/core/apply/dataListQuery.do">
        <div property="columns">
            <div type="checkcolumn" width="40" trueValue="1" falseValue="0"></div>
            <div type="indexcolumn" width="45" headerAlign="center" align="center" allowSort="true">序号</div>
            <div name="action" cellCls="actionIcons" width="85" headerAlign="center" align="center" renderer="onActionRenderer"
                 cellStyle="padding:0;">
                操作
            </div>
            <div field="testNo" width="130" headerAlign="center" align="center" allowSort="true" renderer="render">试验编号</div>
            <div field="componentCategory" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">专业类别</div>
            <div field="componentName" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">样品名称</div>
            <div field="componentModel" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">型号规格</div>
            <div field="materialCode" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">物料编码</div>
            <div field="sampleType" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">样品类型</div>
            <div field="machineModel" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">配套主机型号</div>
            <div field="supplierName" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">供应商名称</div>
            <div field="laboratory" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">承担单位</div>
            <div field="testType" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">试验类型</div>
            <div field="testStatus" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">试验运行状态</div>
            <div field="testProgress" width="100" headerAlign="center" align="center" allowSort="true" numberFormat="p">试验进度</div>
            <div field="testResult" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">试验结果</div>
            <div field="testContract" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">测试方案</div>
            <div field="testReport" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">测试报告</div>
            <div field="testLeaderId" width="100" headerAlign="center" align="center" allowSort="true" visible="false">试验负责人id</div>
            <div field="testLeader" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">试验负责人</div>
            <div field="plannedTestMonth" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">计划试验时间</div>
            <div field="status" width="70" headerAlign="center" align="center" renderer="onStatusRenderer2">流程状态</div>
            <div field="businessStatus" width="150" headerAlign="center" align="center" renderer="onStatusRenderer">流程节点</div>
            <div field="currentProcessUser" sortField="currentProcessUser" width="100" align="center" headerAlign="center"
                 allowSort="false" renderer="render">当前处理人
            </div>
            <div field="applyUser" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">申请人</div>
            <div field="applyDep" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">申请部门</div>
            <div field="applyTime" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">申请时间</div>
        </div>
    </div>
</div>
<!--导出Excel相关HTML-->
<form id="excelForm" action="${ctxPath}/componentTest/core/apply/exportExcel.do" method="post" target="excelIFrame">
    <input type="hidden" name="filter" id="filter"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>
<%--选择项目窗口--%>
<div id="selectProjectWindow" title="选择项目" class="mini-window" style="width:1300px;height:550px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
    <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px" borderStyle="border-left:0;border-top:0;border-right:0;">
        <span style="font-size: 14px;color: #777">项目名称: </span>
        <input class="mini-textbox" width="130" id="filterProjectName" style="margin-right: 15px"/>
        <span style="font-size: 14px;color: #777">项目编号: </span>
        <input class="mini-textbox" width="130" id="filterNumber" style="margin-right: 15px"/>
        <%--<span style="font-size: 14px;color: #777">项目负责人: </span>--%>
        <%--<input class="mini-textbox" width="130" id="filterRespMan" style="margin-right: 15px"/>--%>
        <a class="mini-button" plain="true" onclick="searchProject()">查询</a>
    </div>
    <div class="mini-fit">
        <div id="projectListGrid" class="mini-datagrid" style="width: 100%; height: 100%;"
             allowResize="false" idField="id" showColumnsMenu="false" sizeList="[20,50,100,200]" pageSize="20"
             allowAlternating="true" pagerButtons="#pagerButtons"
             url="${ctxPath}/xcmgProjectManager/core/xcmgProject/allProjectListQuery.do">
            <div property="columns">
                <div type="checkcolumn" width="30"></div>
                <div field="projectName" sortField="projectName" width="140" headerAlign="center" align="center"
                     align="center" allowSort="true">项目名称
                </div>
                <div field="number" sortField="number" width="160" headerAlign="center" align="center"
                     allowSort="true">项目编号
                </div>
            </div>
        </div>
    </div>
    <div property="footer" style="padding:5px;height: 35px">
        <table style="width:100%;height: 100%">
            <tr>
                <td style="width:120px;text-align:center;">
                    <input type="button" style="height: 25px;width: 70px" value="确定" onclick="selectProjectOK()"/>
                    <input type="button" style="height: 25px;width: 70px" value="取消" onclick="selectProjectHide()"/>
                </td>
            </tr>
        </table>
    </div>
</div>
<%--JS--%>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var businessListGrid = mini.get("businessListGrid");
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var currentUserName = "${currentUserName}";
    var nodeSetListWithName = '${nodeSetListWithName}';
    var isComponentTestAdmin = "${isComponentTestAdmin}";
    var currentTime = new Date().format("yyyy-MM-dd hh:mm:ss");
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>" + xzxgwjjxyxgs;
    var selectProjectWindow = mini.get("selectProjectWindow");
    var projectListGrid = mini.get("projectListGrid");
    //..
    $(function () {
        mini.get("businessStatus").setData(mini.decode(nodeSetListWithName));
    });
    //..
    function onActionRenderer(e) {
        var record = e.record;
        var businessId = record.id;
        var instId = record.instId;
        var s = '';
        s += '<span title="明细" onclick="businessDetail(\'' + businessId + '\',\'' + record.status + '\')">明细</span>';
        if (record.status == 'DRAFTED' && (currentUserId == record.applyUserId || currentUserNo == 'admin')) {
            s += '<span  title="编辑" onclick="businessEdit(\'' + businessId + '\',\'' + instId + '\')">编辑</span>';
        } else {
            var currentProcessUserId = record.currentProcessUserId;
            if ((currentProcessUserId && currentProcessUserId.indexOf(currentUserId) != -1) || currentUserNo == 'admin') {
                s += '<span  title="办理" onclick="businessTask(\'' + record.taskId + '\')">办理</span>';
            }
        }
        var status = record.status;
        if (currentUserNo != 'admin' && isComponentTestAdmin != 'true') {
            if (status == 'DRAFTED' && currentUserId == record.applyUserId) {
                s += '<span  title="删除" onclick="removeBusiness(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
            }
        } else {
            s += '<span  title="删除" onclick="removeBusiness(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
        }
        return s;
    }
    //..所有非特殊文本都变成这个格式
    function render(e) {
        if (e.value != null && e.value != "") {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            return html;
        }
    }
    //..（后台根据配置的表单进行跳转）
    function addBusiness() {
        var url = jsUseCtxPath + "/bpm/core/bpmInst/componentTestApply/start.do";
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (businessListGrid) {
                    businessListGrid.reload()
                }
            }
        }, 1000);
    }
    //..
    function removeBusiness(record) {
        var rows = [];
        if (record) {
            rows.push(record);
        } else {
            rows = businessListGrid.getSelecteds();
        }
        if (rows.length <= 0) {
            mini.alert("请至少选中一条记录");
            return;
        }
        mini.confirm("确定删除选中记录？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                var rowIds = [];
                var instIds = [];
                var haveSomeRowsWrong = false;
                for (var i = 0, l = rows.length; i < l; i++) {
                    var r = rows[i];
                    if (r.status == 'DRAFTED' && currentUserId == r.applyUserId) {
                        rowIds.push(r.id);
                        instIds.push(r.instId);
                    } else if (currentUserNo == 'admin' || isComponentTestAdmin == 'true') {
                        rowIds.push(r.id);
                        instIds.push(r.instId);
                    } else {
                        haveSomeRowsWrong = true;
                        break;
                    }
                }
                if (haveSomeRowsWrong) {
                    mini.alert("仅草稿状态数据可由本人删除,或者admin可以强制删除");
                    return;
                }
                _SubmitJson({
                    url: jsUseCtxPath + "/componentTest/core/apply/deleteBusiness.do",
                    method: 'POST',
                    showMsg: false,
                    data: {ids: rowIds.join(','), instIds: instIds.join(',')},
                    success: function (data) {
                        if (data) {
                            mini.alert(data.message);
                            searchFrm();
                        }
                    }
                });
            }
        });
    }
    //..
    function businessDetail(businessId, status) {
        var action = "detail";
        var url = jsUseCtxPath + "/componentTest/core/apply/editPage.do?action=" + action +
            "&businessId=" + businessId + "&status=" + status;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (businessListGrid) {
                    businessListGrid.reload()
                }
            }
        }, 1000);
    }
    //..编辑行数据流程（后台根据配置的表单进行跳转）
    function businessEdit(businessId, instId) {
        var url = jsUseCtxPath + "/bpm/core/bpmInst/start.do?instId=" + instId;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (businessListGrid) {
                    businessListGrid.reload()
                }
            }
        }, 1000);
    }
    //..跳转到任务的处理界面
    function businessTask(taskId) {
        $.ajax({
            url: jsUseCtxPath + '/bpm/core/bpmTask/checkTaskLockStatus.do?taskId=' + taskId,
            success: function (result) {
                if (!result.success) {
                    top._ShowTips({
                        msg: result.message
                    });
                } else {
                    var url = jsUseCtxPath + '/bpm/core/bpmTask/toStart.do?taskId=' + taskId;
                    var winObj = openNewWindow(url, "handTask");
                    var loop = setInterval(function () {
                        if (winObj.closed) {
                            clearInterval(loop);
                            if (businessListGrid) {
                                businessListGrid.reload();
                            }
                        }
                    }, 1000);
                }
            }
        })
    }
    //..状态渲染
    function onStatusRenderer(e) {
        var record = e.record;
        var businessStatus = record.businessStatus;
        var arr = mini.decode(nodeSetListWithName);
        return $.formatItemValue(arr, businessStatus);
    }
    //..流程状态渲染
    function onStatusRenderer2(e) {
        var record = e.record;
        var status = record.status;

        var arr = [{'key': 'DRAFTED', 'value': '草稿', 'css': 'orange'},
            {'key': 'RUNNING', 'value': '运行中', 'css': 'green'},
            {'key': 'SUCCESS_END', 'value': '成功结束', 'css': 'blue'},
            {'key': 'DISCARD_END', 'value': '作废', 'css': 'red'},
            {'key': 'ABORT_END', 'value': '异常终止', 'css': 'red'}
        ];

        return $.formatItemValue(arr, status);
    }
    //..导出
    function exportBusiness() {
        var rows = businessListGrid.getSelecteds();
        if (rows.length <= 0) {
            mini.alert("请至少选中一条记录");
            return;
        }
        var params = [];
        for (var i = 0; i < rows.length; i++) {
            var obj = {};
            obj.name = "id";
            obj.value = rows[i].id
            params.push(obj);
        }
        $("#filter").val(mini.encode(params));
        var excelForm = $("#excelForm");
        excelForm.submit();
    }
    //..以下科技项目相关
    function searchProject() {
        var queryParam = [];
        var projectName = $.trim(mini.get("filterProjectName").getValue());
        if (projectName) {
            queryParam.push({name: "projectName", value: projectName});
        }
        var number = $.trim(mini.get("filterNumber").getValue());
        if (number) {
            queryParam.push({name: "number", value: number});
        }
//        var respMan = $.trim(mini.get("filterRespMan").getValue());
//        if (respMan) {
//            queryParam.push({name: "respMan", value: respMan});
//        }
        var data = {};
        data.filter = mini.encode(queryParam);
        data.pageIndex = projectListGrid.getPageIndex();
        data.pageSize = projectListGrid.getPageSize();
        data.sortField = projectListGrid.getSortField();
        data.sortOrder = projectListGrid.getSortOrder();
        //查询
        projectListGrid.load(data);
    }
    //..
    function selectProjectClick() {
        selectProjectWindow.show();
        searchProject();
    }
    //..
    function selectProjectCloseClick() {
        mini.get("projectId").setValue("");
        mini.get("projectId").setText("");
    }
    //..
    function selectProjectOK() {
        var selectRow = projectListGrid.getSelected();
        if (selectRow) {
            mini.get("projectId").setValue(selectRow.projectId);
            mini.get("projectId").setText(selectRow.projectName);
        } else {
            mini.alert("请选择一条数据！");
            return;
        }
        selectProjectHide();
    }
    //..
    function selectProjectHide() {
        selectProjectWindow.hide();
        mini.get("filterProjectName").setValue("");
        mini.get("filterNumber").setValue("");
    }
</script>
<redxun:gridScript gridId="businessListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>