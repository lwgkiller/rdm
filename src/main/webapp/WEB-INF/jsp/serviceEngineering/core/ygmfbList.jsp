<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title></title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">厂家：</span>
                    <input class="mini-textbox" id="changjia" name="changjia"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">油缸类型：</span>
                    <input class="mini-textbox" id="ygType" name="ygType"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">油缸物料号：</span>
                    <input class="mini-textbox" id="ygMatNo" name="ygMatNo"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">油缸型号：</span>
                    <input class="mini-textbox" id="ygXinghao" name="ygXinghao"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">油缸物料描述：</span>
                    <input class="mini-textbox" id="ygMatDesc" name="ygMatDesc"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">密封包物料号：</span>
                    <input class="mini-textbox" id="mfbMatNo" name="mfbMatNo"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">密封包物料描述：</span>
                    <input class="mini-textbox" id="mfbMatDesc" name="mfbMatDesc"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">申请人：</span>
                    <input class="mini-textbox" id="applyUser" name="applyUser"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">流程状态: </span>
                    <input id="status" name="status" class="mini-combobox" style="width:120px;" multiSelect="false"
                           textField="value" valueField="key" emptyText="请选择..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                           data="[ {'key' : 'DRAFTED','value' : '草稿'},{'key' : 'RUNNING','value' : '审批中'},{'key' : 'SUCCESS_END','value' : '批准'},{'key' : 'DISCARD_END','value' : '作废'}]"
                    />
                </li>
                <li style="margin-left: 10px">
                    <f:a alias="ygmfb-searchFrm" onclick="searchFrm()" showNoRight="false" style="margin-right: 5px">查询</f:a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <f:a alias="ygmfb-removeBusiness" onclick="removeBusiness()" showNoRight="false"
                         style="margin-right: 5px">删除</f:a>
                    <f:a alias="ygmfb-addBusiness" onclick="addBusiness()" showNoRight="false"
                         style="margin-right: 5px">新增</f:a>
                </li>
            </ul>
        </form>
        <span class="searchSelectionBtn" onclick="no_search(this ,'searchForm')">
				<i class="icon-sc-lower"></i>
        </span>
    </div>
</div>

<div class="mini-fit" style="height: 100%;">
    <div id="businessListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         allowCellWrap="true"
         showCellTip="true" idField="id" allowCellEdit="true" allowCellSelect="true" oncellvalidation="onCellValidation"
         multiSelect="true" showColumnsMenu="false" sizeList="[50,100]" pageSize="50" allowAlternating="true"
         pagerButtons="#pagerButtons"
         url="${ctxPath}/serviceEngineering/core/ygmfb/dataListQuery.do">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div name="action" cellCls="actionIcons" width="150" headerAlign="center" align="center"
                 renderer="onActionRenderer"
                 cellStyle="padding:0;">操作
            </div>
            <div field="changjia" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">厂家</div>
            <div field="ygType" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">油缸类型</div>
            <div field="ygMatNo" width="150" headerAlign="center" align="center" allowSort="true" renderer="render">油缸物料号</div>
            <div field="ygXinghao" width="150" headerAlign="center" align="center" allowSort="true" renderer="render">油缸型号</div>
            <div field="ygMatDesc" width="150" headerAlign="center" align="center" allowSort="true" renderer="render">油缸物料描述</div>
            <div field="mfbMatNo" width="150" headerAlign="center" align="center" allowSort="true" renderer="render">密封包物料号</div>
            <div field="mfbMatDesc" width="150" headerAlign="center" align="center" allowSort="true" renderer="render">密封包物料描述</div>
            <div field="useDesc" width="200" headerAlign="center" align="center" allowSort="true" renderer="render">用途</div>
            <div field="applyUser" width="150" headerAlign="center" align="center" allowSort="true" renderer="render">申请人</div>
            <div field="currentProcessTask" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">当前任务</div>
            <div field="currentProcessUser" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">当前处理人</div>
            <div field="status" width="300" headerAlign="center" align="center" allowSort="true" renderer="onStatusRenderer">状态</div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var businessListGrid = mini.get("businessListGrid");
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var currentUserName = "${currentUserName}";
    var currentTime = new Date().format("yyyy-MM-dd");
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";

    //..行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var businessId = record.id;
        var instId = record.instId;
        var status = record.status;
        var s = '';
        if (status != 'DRAFTED' && (record.receiverIds.indexOf(currentUserId) != -1 ||
            record.applyUserId == currentUserId ||
            record.bjgsfzrId == currentUserId ||
            record.fwgcywyId == currentUserId ||
            record.fwgcfzrId == currentUserId ||
            record.fgldId == currentUserId)) {
            s += '<span  title="明细" onclick="businessDetail(\'' + businessId + '\',\'' + record.status + '\')">明细</span>';
        }
        if ((status == 'DRAFTED' || typeof(record.status) == "undefined") && (currentUserId == record.applyUserId || currentUserNo == 'admin')) {
            s += '<span  title="编辑" onclick="businessEdit(\'' + businessId + '\',\'' + instId + '\')">编辑</span>';
        }
        if (status == 'RUNNING') {
            if ((record.currentProcessUserId && record.currentProcessUserId.indexOf(currentUserId) != -1) || currentUserNo == 'admin') {
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
        var url = jsUseCtxPath + "/bpm/core/bpmInst/YGMFBCD/start.do";
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (businessListGrid) {
                    businessListGrid.reload();
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
        var url = jsUseCtxPath + "/serviceEngineering/core/ygmfb/editPage.do?action=" + action +
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
            async: false,
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
                        instIds.push(r.INST_ID_);
                    } else if (currentUserNo == 'admin') {
                        rowIds.push(r.id);
                        instIds.push(r.INST_ID_);
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
                    url: jsUseCtxPath + "/serviceEngineering/core/ygmfb/deleteBusiness.do",
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

    //..状态渲染
    function onStatusRenderer(e) {
        var record = e.record;
        var businessStatus = record.status;

        var arr = [{'key': 'DRAFTED', 'value': '草稿', 'css': 'orange'},
            {'key': 'RUNNING', 'value': '审批中', 'css': 'green'},
            {'key': 'SUCCESS_END', 'value': '批准', 'css': 'blue'},
            {'key': 'DISCARD_END', 'value': '作废', 'css': 'red'}
        ];
        return $.formatItemValue(arr, businessStatus);
    }

    //..所有非特殊文本都变成这个格式
    function render(e) {
        if (e.value != null && e.value != "") {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            return html;
        }
    }
</script>
<redxun:gridScript gridId="businessListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>