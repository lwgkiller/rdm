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
                    <span class="text" style="width:auto">主题：</span>
                    <input class="mini-textbox" id="remark" name="remark"/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">审核状态: </span>
                    <input id="businessStatus" name="businessStatus" class="mini-combobox" style="width:120px;"
                           textField="value" valueField="key" emptyText="请选择..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                           data="[{'key' : 'A-editing','value' : '编制中'},
							   {'key' : 'B-proofreading','value' : '校对中'},
							   {'key' : 'C-reviewing','value' : '审核中'},
							   {'key' : 'D-close','value' : '审批通过'}]"
                    />
                </li>
                <li style="margin-left: 10px">
                    <f:a alias="disassemblyProposal-searchFrm" onclick="searchFrm()" showNoRight="false">查询</f:a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <f:a alias="disassemblyProposal-addBusiness" onclick="addBusiness()" showNoRight="false">新增</f:a>
                    <f:a alias="disassemblyProposal-removeBusiness" onclick="removeBusiness()" showNoRight="false">删除</f:a>
                </li>
            </ul>
        </form>
        <span class="searchSelectionBtn" onclick="no_search(this ,'searchForm')">
				<i class="icon-sc-lower"></i>
        </span>
    </div>
</div>

<div class="mini-fit" style="height: 100%;">
    <div id="maintainabilityDisassemblyProposalListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         allowCellWrap="false"
         showCellTip="true" idField="id" allowCellEdit="true" allowCellSelect="true" oncellvalidation="onCellValidation"
         multiSelect="true" showColumnsMenu="false" sizeList="[50,100]" pageSize="50" allowAlternating="true" pagerButtons="#pagerButtons"
         url="${ctxPath}/serviceEngineering/core/maintainability/disassemblyproposal/dataListQuery.do">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div name="action" cellCls="actionIcons" width="100" headerAlign="center" align="center" renderer="onActionRenderer"
                 cellStyle="padding:0;">操作
            </div>
            <%--            <div field="fileName" width="60" headerAlign="center" align="center">文件名</div>--%>
            <%--            <div field="firstFileId" width="60" headerAlign="center" align="center">文件id</div>--%>
            <div field="remark" width="200" headerAlign="center" align="center">主题</div>
            <div field="businessStatus" width="80" headerAlign="center" align="center" allowSort="false" renderer="onStatusRenderer">业务状态</div>
            <div field="currentProcessUser" sortField="currentProcessUser" width="80" align="center" headerAlign="center"
                 allowSort="false" renderer="render">当前处理人
            </div>
            <%--            <div field="currentProcessTask" width="80" align="center" headerAlign="center" renderer="render">当前任务</div>--%>
            <%--            <div field="status" width="80" headerAlign="center" align="center" allowSort="false" renderer="render">流程状态</div>--%>
            <div field="proofreaderUserName" width="80" headerAlign="center" align="center" allowSort="false" renderer="render">历史校对人</div>
            <div field="reviewerUserName" width="80" headerAlign="center" align="center" allowSort="false" renderer="render">历史审核人</div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var maintainabilityDisassemblyProposalListGrid = mini.get("maintainabilityDisassemblyProposalListGrid");
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var currentUserName = "${currentUserName}";
    var currentTime = new Date();
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";

    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var businessId = record.id;
        var instId = record.instId;
        var s = '';
        s += '<span  title="明细" onclick="businessDetail(\'' + businessId + '\',\'' + record.status + '\')">明细</span>';
        if (record.status == 'DRAFTED' && currentUserId == record.editorUserId) {
            s += '<span  title="编辑" onclick="businessEdit(\'' + businessId + '\',\'' + instId + '\')">编辑</span>';
        } else {
            var currentProcessUserId = record.currentProcessUserId;
            if ((currentProcessUserId && currentProcessUserId.indexOf(currentUserId) != -1) || currentUserNo == 'admin') {
                s += '<span  title="办理" onclick="businessTask(\'' + record.taskId + '\')">办理</span>';
            }
        }
        var status = record.status;
        if (currentUserNo != 'admin') {
            if (status == 'DRAFTED' && currentUserId == record.editorUserId) {
                s += '<span  title="删除" onclick="removeBusiness(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
            }
        } else {
            s += '<span  title="删除" onclick="removeBusiness(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
        }
        return s;
    }

    //..（后台根据配置的表单进行跳转）
    function addBusiness() {
        var url = jsUseCtxPath + "/bpm/core/bpmInst/disassemblyProposal/start.do";
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (maintainabilityDisassemblyProposalListGrid) {
                    maintainabilityDisassemblyProposalListGrid.reload()
                }
            }
        }, 1000);
    }

    //@lwgkiller阿诺多罗：配置url表单时，后边如果追加参数例如"/serviceEngineering/core/maintainability/disassemblyproposal/editPage.do?
    //businessId={pk}&nodeId={nodeId_}&fuck={fuck_}"
    //中的fuck={fuck_}，那么在流程引擎启动是赋值方式一定是用{fuck_}里面的fuck_做形参。通过改造的params解析后，会在真正的edit页面将fuck_的值替换为
    //fuck=fuckyou
    /*function addBusinessFuck() {
        var url = jsUseCtxPath + "/bpm/core/bpmInst/disassemblyProposal/start.do?fuck_=fuckyou";
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (maintainabilityDisassemblyProposalListGrid) {
                    maintainabilityDisassemblyProposalListGrid.reload()
                }
            }
        }, 1000);
    }*/

    //..编辑行数据流程（后台根据配置的表单进行跳转）
    function businessEdit(businessId, instId) {
        var url = jsUseCtxPath + "/bpm/core/bpmInst/start.do?instId=" + instId;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (maintainabilityDisassemblyProposalListGrid) {
                    maintainabilityDisassemblyProposalListGrid.reload()
                }
            }
        }, 1000);
    }

    //@lwgkiller阿诺多罗：配置url表单时，后边如果追加参数例如"/serviceEngineering/core/maintainability/disassemblyproposal/editPage.do?
    //businessId={pk}&nodeId={nodeId_}&fuck={fuck_}"
    //中的fuck={fuck_}，那么在流程引擎启动是赋值方式一定是用{fuck_}里面的fuck_做形参。通过改造的params解析后，会在真正的edit页面将fuck_的值替换为
    //fuck=fuckyou
    /*function businessEditFuck(businessId, instId) {
        var url = jsUseCtxPath + "/bpm/core/bpmInst/start.do?fuck_=fuckyou&instId=" + instId;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (maintainabilityDisassemblyProposalListGrid) {
                    maintainabilityDisassemblyProposalListGrid.reload()
                }
            }
        }, 1000);
    }*/

    //..明细（直接跳转到详情的业务controller）
    function businessDetail(businessId, status) {
        var action = "detail";
        var url = jsUseCtxPath + "/serviceEngineering/core/maintainability/disassemblyproposal/editPage.do?action=" + action +
            "&businessId=" + businessId + "&status=" + status;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (maintainabilityDisassemblyProposalListGrid) {
                    maintainabilityDisassemblyProposalListGrid.reload()
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
                            if (maintainabilityDisassemblyProposalListGrid) {
                                maintainabilityDisassemblyProposalListGrid.reload();
                            }
                        }
                    }, 1000);
                }
            }
        })
    }

    //..删除业务
    function removeBusiness(record) {
        var rows = [];
        if (record) {
            rows.push(record);
        } else {
            rows = maintainabilityDisassemblyProposalListGrid.getSelecteds();
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
                    if (r.status == 'DRAFTED' && r.CREATE_BY_ == currentUserId) {
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
                    url: jsUseCtxPath + "/serviceEngineering/core/maintainability/disassemblyproposal/deleteBusiness.do",
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

    //状态渲染
    function onStatusRenderer(e) {
        var record = e.record;
        var businessStatus = record.businessStatus;

        var arr = [{'key': 'A-editing', 'value': '编制中', 'css': 'blue'},
            {'key': 'B-proofreading', 'value': '校对中', 'css': 'orange'},
            {'key': 'C-reviewing', 'value': '审核中', 'css': 'red'},
            {'key': 'D-close', 'value': '审批通过', 'css': 'green'}
        ];

        return $.formatItemValue(arr, businessStatus);
    }
</script>
<redxun:gridScript gridId="maintainabilityDisassemblyProposalListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>