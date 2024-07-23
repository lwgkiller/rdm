
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
    <title>部门需求收集列表管理</title>
    <%@include file="/commons/list.jsp"%>
</head>
<body>
<div class="mini-toolbar" >
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul >
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">设计型号: </span>
                    <input class="mini-textbox" id="jixing" name="jixing" />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">部件/接口名称: </span>
                    <input class="mini-textbox" id="structName" name="structName" />
                    <input id="belongSingleId" name="belongSingleId" class="mini-hidden" value="${belongSingleId}"/>
                </li>

                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">需求反馈部门: </span>
                    <input id="demandFeedBackDeptName" name="demandFeedBackDeptName" class="mini-textbox"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">需求反馈人: </span>
                    <input id="demandFeedBackUserName" name="demandFeedBackUserName" class="mini-textbox"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">流程状态: </span>
                    <input id="instStatus" name="instStatus" class="mini-combobox" style="width:120px;"
                           textField="value" valueField="key" emptyText="请选择..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                           data="[ {'key' : 'DRAFTED','value' : '草稿'},{'key' : 'RUNNING','value' : '审批中'},{'key' : 'SUCCESS_END','value' : '已完成'},
							   {'key' : 'DISCARD_END','value' : '作废'}]"
                    />
                </li>

                <li class="liBtn">
					<span class="unfoldBtn" onclick="no_more(this,'moreBox')">
						<em>展开</em>
						<i class="unfoldIcon"></i>
					</span>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px"  plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px"  plain="true" onclick="clearForm()">清空查询</a>
                    <div style="display: inline-block" class="separator"></div>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="removeApply()">删除</a>
                </li>
            </ul>
            <div id="moreBox">
                <ul>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">部件/接口验证项目编号: </span>
                        <input id="singleNumber" name="singleNumber" class="mini-textbox"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">需求发起人: </span>
                        <input id="creator" name="creator" class="mini-textbox"/>
                    </li>
                </ul>
            </div>
        </form>
    </div>
</div>
<div class="mini-fit" style="height: 99%;">
    <div id="demandCollectListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onlyCheckSelection="true"
         url="${ctxPath}/drbfm/single/getDeptDemandCollectList.do" idField="id" autoload="false"
         multiSelect="true" showColumnsMenu="true" sizeList="[50,100,200]" pageSize="50" allowAlternating="true" pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="25"></div>
            <div name="action" cellCls="actionIcons" width="100" headerAlign="center" align="center" renderer="onActionRenderer" cellStyle="padding:0;">操作</div>
            <div field="jixing" headerAlign="center" align="center" width="145px">设计型号</div>
            <div field="structName" headerAlign="center" align="center" width="200px">部件/接口名称</div>
            <div field="structType" headerAlign="center" align="center" width="120px">部件/接口类型</div>
            <div field="singleNumber" headerAlign="center" align="center" width="200px">部件/接口验证项目编号</div>
            <div field="demandFeedBackDeptName" headerAlign="center" align="center" width="130px">需求反馈部门</div>
            <div field="demandFeedBackUserName" headerAlign="center" align="center" width="90px">需求反馈人</div>
            <div field="instStatus"  width="80px" headerAlign="center" align="center" allowSort="false" renderer="onStatusRenderer">流程状态</div>
            <div field="currentProcessUser"  sortField="currentProcessUser"  width="70" align="center" headerAlign="center" allowSort="false">当前处理人</div>
            <div field="currentProcessTask" width="150" align="center" headerAlign="center">当前流程节点</div>
            <div field="creator" headerAlign="center" align="center" width="90px">需求发起人</div>
            <div field="CREATE_TIME_" sortField="CREATE_TIME_" width="80" headerAlign="center" dateFormat="yyyy-MM-dd" align="center" allowSort="true">需求发起时间</div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var demandCollectListGrid=mini.get("demandCollectListGrid");
    var currentUserId="${currentUserId}";

    $(function () {
        searchFrm();
    });


    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var applyId = record.id;
        var instId=record.instId;
        var currentProcessUserId=record.currentProcessUserId;
        var s = '<span  title="查看" onclick="demandCollectDetail(\'' + applyId +'\',\''+record.instStatus+ '\')">查看</span>';
        if(currentProcessUserId && currentProcessUserId.indexOf(currentUserId) !=-1) {
            s+='<span  title="办理" onclick="demandCollectTaskDo(\'' + record.taskId + '\')">办理</span>';
        }

        var status=record.instStatus;
        if (status == 'DISCARD_END' && currentUserId ==record.CREATE_BY_) {
            s+='<span  title="删除" onclick="removeApply('+JSON.stringify(record).replace(/"/g, '&quot;')+')">删除</span>';
        }
        return s;
    }


    function onStatusRenderer(e) {
        var record = e.record;
        var status = record.instStatus;

        var arr = [ {'key' : 'DRAFTED','value' : '草稿','css' : 'orange'},
            {'key' : 'RUNNING','value' : '审批中','css' : 'green'},
            {'key' : 'SUCCESS_END','value' : '已完成','css' : 'blue'},
            {'key' : 'DISCARD_END','value' : '作废','css' : 'red'}
        ];

        return $.formatItemValue(arr,status);
    }

    //明细（直接跳转到详情的业务controller）
    function demandCollectDetail(applyId, status) {
        var action = "detail";
        var url = jsUseCtxPath + "/drbfm/single/demandCollectPage.do?action=" + action + "&applyId=" + applyId + "&status=" + status;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (demandCollectListGrid) {
                    demandCollectListGrid.reload();
                }
            }
        }, 1000);
    }

    //跳转到任务的处理界面
    function demandCollectTaskDo(taskId) {
        $.ajax({
            url: jsUseCtxPath + '/bpm/core/bpmTask/checkTaskLockStatus.do?taskId=' + taskId,
            async:false,
            success: function (result) {
                if (!result.success) {
                    top._ShowTips({
                        msg: result.message
                    });
                } else {
                    var url = jsUseCtxPath + '/bpm/core/bpmTask/toStart.do?taskId=' + taskId;
                    var winObj = openNewWindow(url, "handTask");
                    var loop = setInterval(function () {
                        if(!winObj) {
                            clearInterval(loop);
                        } else if (winObj.closed) {
                            clearInterval(loop);
                            if (demandCollectListGrid) {
                                demandCollectListGrid.reload();
                            }
                        }
                    }, 1000);
                }
            }
        })
    }

    function removeApply(record) {
        var rows = [];
        if (record) {
            rows.push(record);
        } else {
            rows = demandCollectListGrid.getSelecteds();
        }
        if (rows.length <= 0) {
            mini.alert("请至少选中一条记录");
            return;
        }
        mini.confirm("确定删除选中记录（仅作废状态且本人发起的流程可以删除）？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                var rowIds = [];
                var instIds = [];
                for (var i = 0, l = rows.length; i < l; i++) {
                    var r = rows[i];
                    if(r.instStatus=='DISCARD_END' && r.CREATE_BY_==currentUserId) {
                        rowIds.push(r.id);
                        instIds.push(r.instId);
                    }
                }

                if (rowIds.length > 0) {
                    _SubmitJson({
                        url: jsUseCtxPath + "/drbfm/single/deleteDemandCollect.do",
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
                } else {
                    mini.alert("请选择已作废且本人发起的流程");
                }
            }
        });
    }
</script>
<redxun:gridScript gridId="demandCollectListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl="" />
</body>
</html>