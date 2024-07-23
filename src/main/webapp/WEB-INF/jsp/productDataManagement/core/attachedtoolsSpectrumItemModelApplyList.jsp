<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>属具型号申请列表</title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<%----%>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">申请编号：</span>
                    <input class="mini-textbox" id="businessNo" name="businessNo"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">设计型号：</span>
                    <input class="mini-textbox" id="designModel" name="designModel"/>
                </li>
                <li style="margin-right:15px">
                    <span class="text" style="width:auto">属具类型：</span>
                    <input id="attachedtoolsType" name="attachedtoolsType"
                           class="mini-combobox" style="width:98%"
                           url="${ctxPath}/sys/core/sysDic/getByTreeKeyTopOne.do?dicKey=attachedtoolsType"
                           valueField="key" textField="value" onvaluechanged="attachedtoolsTypeChanged"/>
                </li>
                <li style="margin-right:15px">
                    <span class="text" style="width:auto">属具子类型：</span>
                    <input id="attachedtoolsType2" name="attachedtoolsType2"
                           class="mini-combobox" style="width:98%" showNullItem="true"
                           valueField="key" textField="value"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">申请人：</span>
                    <input class="mini-textbox" id="applyUserName" name="applyUserName"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">申请部门：</span>
                    <input class="mini-textbox" id="applyUserDeptName" name="applyUserDeptName"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">当前节点：</span>
                    <input id="businessStatus" name="businessStatus" class="mini-combobox" style="width:98%"
                           valueField="key" textField="value"/>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <div style="display: inline-block" class="separator"></div>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="addBusiness()">新增</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="removeBusiness()">删除</a>
                </li>
            </ul>
        </form>
    </div>
</div>
<%----%>
<div class="mini-fit" style="height: 100%;">
    <div id="businessListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         allowCellWrap="false"
         showCellTip="true" idField="id" allowCellEdit="true" allowCellSelect="true"
         multiSelect="true" showColumnsMenu="false" sizeList="[50,100]" pageSize="50" allowAlternating="true"
         pagerButtons="#pagerButtons" sortField="applyTime" sortOrder="desc"
         url="${ctxPath}/productDataManagement/core/attachedtoolsSpectrumItemModelApply/dataListQuery.do">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div name="action" cellCls="actionIcons" width="75" headerAlign="center" align="center" renderer="onActionRenderer"
                 cellStyle="padding:0;">操作
            </div>
            <div field="status" width="70" headerAlign="center" align="center" renderer="onStatusRenderer2">流程状态</div>
            <div field="businessNo" width="150" headerAlign="center" align="center" allowSort="true">申请编号</div>
            <div field="applyUserDeptName" width="70" headerAlign="center" align="center" allowSort="true">申请部门</div>
            <div field="designModel" width="70" headerAlign="center" align="center" allowSort="true">设计型号</div>
            <div field="suitableTonnage" width="70" headerAlign="center" align="center" allowSort="true">适配挖机吨位</div>
            <div field="abroad" width="70" headerAlign="center" align="center" allowSort="true">国内外</div>
            <div field="region" width="70" headerAlign="center" align="center" allowSort="true">销售区域</div>
            <div field="applyUserName" width="50" headerAlign="center" align="center" allowSort="true">申请人</div>
            <div field="applyTime" width="120" headerAlign="center" align="center" allowSort="true">申请时间</div>
            <div field="businessStatus" width="120" headerAlign="center" align="center" renderer="onStatusRenderer">当前节点</div>
            <div field="currentProcessUser" sortField="currentProcessUser" width="100" align="center" headerAlign="center" allowSort="false">当前处理人</div>
        </div>
    </div>
</div>
<%----%>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var currentUserName = "${currentUserName}";
    var nodeSetListWithName = '${nodeSetListWithName}';
    var currentTime = new Date().format("yyyy-MM-dd hh:mm:ss");
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";
    var businessListGrid = mini.get("businessListGrid");
    //..
    $(function () {
        mini.get("businessStatus").setData(mini.decode(nodeSetListWithName));
    });
    //..行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var businessId = record.id;
        var instId = record.instId;
        var s = '';
        s += '<span  title="明细" onclick="businessDetail(\'' + businessId + '\',\'' + record.status + '\')">明细</span>';
        if (record.status == 'DRAFTED' && (currentUserId == record.applyUserId || currentUserNo == 'admin')) {
            s += '<span  title="编辑" onclick="businessEdit(\'' + businessId + '\',\'' + instId + '\')">编辑</span>';
        } else {
            var currentProcessUserId = record.currentProcessUserId;
            if ((currentProcessUserId && currentProcessUserId.indexOf(currentUserId) != -1) || currentUserNo == 'admin') {
                s += '<span  title="办理" onclick="businessTask(\'' + record.taskId + '\')">办理</span>';
            }
        }
        if (currentUserNo != 'admin') {
            if (record.status == 'DRAFTED' && currentUserId == record.applyUserId) {
                s += '<span  title="删除" onclick="removeBusiness(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
            }
        } else {
            s += '<span  title="删除" onclick="removeBusiness(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
        }
        return s;
    }
    //..（后台根据配置的表单进行跳转）
    function addBusiness() {
        var url = jsUseCtxPath + "/bpm/core/bpmInst/attachedtoolsSpectrumItemModelApply/start.do";
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
    //..
    function businessDetail(businessId, status) {
        var action = "detail";
        var url = jsUseCtxPath + "/productDataManagement/core/attachedtoolsSpectrumItemModelApply/editPage.do?action=" + action +
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
                    } else if (currentUserNo == 'admin') {
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
                    url: jsUseCtxPath + "/productDataManagement/core/attachedtoolsSpectrumItemModelApply/deleteBusiness.do",
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
</script>
<redxun:gridScript gridId="businessListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
