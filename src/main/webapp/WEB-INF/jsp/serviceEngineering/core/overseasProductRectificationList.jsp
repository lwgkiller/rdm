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
                    <span class="text" style="width:auto">整改类型：</span>
                    <input id="rectificationType" name="rectificationType" class="mini-combobox" style="width:98%"
                           data="[{'key':'外购件','value':'外购件'},{'key':'装配件','value':'装配件'}]"
                           valueField="key" textField="value" multiSelect="false"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">设计人员：</span>
                    <input class="mini-textbox" id="designer" name="designer"/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">审批状态: </span>
                    <input id="businessStatus" name="businessStatus" class="mini-combobox" style="width:120px;"
                           textField="value" valueField="key" emptyText="请选择..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">整机编号：</span>
                    <input class="mini-textbox" id="pin" name="pin"/>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="addBusiness()">新增</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="removeBusiness()">删除</a>
                    <%--<a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="doTransFile()">手工转换文件</a>--%>
                    <a id="doSynFormToGss" class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="doSynFormToGss()">手工同步Gss</a>
                </li>
            </ul>
        </form>
        <span class="searchSelectionBtn" onclick="no_search(this ,'searchForm')">
				<i class="icon-sc-lower"></i>
        </span>
    </div>
</div>
<%-------------------------------%>
<div class="mini-fit" style="height: 100%;">
    <div id="businessListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" allowCellWrap="true"
         showCellTip="true" idField="id" allowCellEdit="true" allowCellSelect="true" allowHeaderWrap="true"
         multiSelect="true" showColumnsMenu="false" sizeList="[50,100]" pageSize="50" allowAlternating="true" pagerButtons="#pagerButtons"
         url="${ctxPath}/serviceEngineering/core/overseasProductRectification/dataListQuery.do">
        <div property="columns">
            <div type="checkcolumn" width="40"></div>
            <div type="indexcolumn" width="70" headerAlign="center" align="center">序号</div>
            <div name="action" cellCls="actionIcons" width="150" headerAlign="center" align="center" renderer="onActionRenderer"
                 cellStyle="padding:0;">操作
            </div>
            <div field="rectificationType" width="120" headerAlign="center" align="center" allowSort="true">整改类型</div>
            <div field="isNeedDesigner" width="150" headerAlign="center" align="center" allowSort="true">是否需要设计人员补充</div>
            <div field="designer" width="120" headerAlign="center" align="center" allowSort="true">设计人员</div>
            <div field="applyUser" width="120" headerAlign="center" align="center" allowSort="true">发起人</div>
            <div field="applyTime" width="150" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" headerAlign="center"
                 allowSort="true">发起时间
            </div>
            <div field="currentProcessUser" sortField="currentProcessUser" width="120" align="center" headerAlign="center"
                 allowSort="false" renderer="render">当前处理人
            </div>
            <div field="businessStatus" width="120" headerAlign="center" align="center" renderer="onStatusRenderer">审批状态</div>
            <div field="isSynToGss" width="150" headerAlign="center" align="center">是否同步到GSS</div>
        </div>
    </div>
</div>
<%-------------------------------%>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var businessListGrid = mini.get("businessListGrid");
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var currentUserName = "${currentUserName}";
    var currentTime = new Date().format("yyyy-MM-dd");
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>" + xzxgwjjxyxgs;
    var nodeSetListWithName = '${nodeSetListWithName}';
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
        if ((record.status == 'DRAFTED' && (currentUserId == record.applyUserId || currentUserNo == 'admin'))) {
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
    //（后台根据配置的表单进行跳转）
    function addBusiness() {
        var url = jsUseCtxPath + "/bpm/core/bpmInst/overseasProductRectification/start.do";
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
        var url = jsUseCtxPath + "/serviceEngineering/core/overseasProductRectification/editPage.do?action=" + action +
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
            mini.alert('请至少选中一条记录');
            return;
        }
        mini.confirm('确定删除选中记录', '提示', function (action) {
            if (action != 'ok') {
                return;
            } else {
                var rowIds = [];
                var instIds = [];
                var haveSomeRowsWrong = false;
                for (var i = 0, l = rows.length; i < l; i++) {
                    var r = rows[i];
                    if (r.businessStatus == 'A' && currentUserId == r.applyUserId) {
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
                    mini.alert('仅编辑状态数据可由申请人删除,或者admin可以强制删除');
                    return;
                }
                _SubmitJson({
                    url: jsUseCtxPath + "/serviceEngineering/core/overseasProductRectification/deleteBusiness.do",
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
        var businessStatus = record.businessStatus;
        var arr = mini.decode(nodeSetListWithName);
        return $.formatItemValue(arr, businessStatus);
    }
    //..所有非特殊文本都变成这个格式
    function render(e) {
        if (e.value != null && e.value != "") {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            return html;
        }
    }
    //..手工生成文件
    function doTransFile() {
        var rows = businessListGrid.getSelecteds();
        if (rows.length != 1) {
            mini.alert("手工生成一次只能生成一条记录，请选中一条记录");
            return;
        }
        mini.confirm("确定生成选中记录？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                _SubmitJson({
                    url: jsUseCtxPath + "/serviceEngineering/core/overseasProductRectification/doTransFile.do",
                    method: 'POST',
                    data: rows[0],
                    postJson: true,
                    showMsg: false,
                    success: function (returnData) {
                        if (returnData.success) {
                            mini.alert(returnData.message);
                            businessListGrid.reload();
                        } else {
                            mini.alert("转换失败:" + returnData.message);
                        }
                    },
                    fail: function (returnData) {
                        mini.alert("转换失败:" + returnData.message);
                    }
                });
            }
        });
    }
    //..手工同步Gss
    function doSynFormToGss() {
        var rows = businessListGrid.getSelecteds();
        if (rows.length != 1) {
            mini.alert("手工同步一次只能同步一条记录，请选中一条记录");
            return;
        }
        mini.confirm("确定同步选中记录？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                _SubmitJson({
                    url: jsUseCtxPath + "/serviceEngineering/core/overseasProductRectification/doSynFormToGss.do",
                    method: 'POST',
                    data: rows[0],
                    postJson: true,
                    showMsg: false,
                    success: function (returnData) {
                        if (returnData.success) {
                            mini.alert(returnData.message);
                            businessListGrid.reload();
                        } else {
                            mini.alert("同步失败:" + returnData.message);
                        }
                    },
                    fail: function (returnData) {
                        mini.alert("同步失败:" + returnData.message);
                    }
                });
            }
        });
    }
</script>
<redxun:gridScript gridId="businessListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>