<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>任务分解列表</title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">会议组织部门: </span>
                    <input class="mini-textbox" id="meetingOrgDepName" name="meetingOrgDepName"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">会议负责人: </span>
                    <input class="mini-textbox" id="meetingOrgUserName" name="meetingOrgUserName"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">会议纪要描述: </span>
                    <input class="mini-textbox" id="meetingContent" name="meetingContent"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">第一责任人: </span>
                    <input class="mini-textbox"  id="meetingPlanRespName" name="meetingPlanRespName"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text"style="width:auto" >责任部门: </span>
                    <input class="mini-textbox"  id="zzrDepName" name="zzrDepName"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">是否完成: </span>
                    <input id="isComplete" name="isComplete" class="mini-combobox" style="width:100px;"
                           textField="key" valueField="value" emptyText="请选择..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                           data="[ {'key' : '是','value' : 'true'},{'key' : '否','value' : 'false'}]"
                    />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">是否存在风险: </span>
                    <input id="delay" name="delay" class="mini-combobox" style="width:100px;"
                           textField="key" valueField="value" emptyText="请选择..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                           data="[ {'key' : '是','value' : 'yes'},{'key' : '否','value' : 'no'}]"
                    />
                </li>
                <li class="liBtn">
					<span class="unfoldBtn" onclick="no_more(this,'moreBox')">
						<em>展开</em>
						<i class="unfoldIcon"></i>
					</span>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                </li>
                <div id="moreBox">
                    <ul>
                        <li style="margin-right: 15px">
                            <span class="text" style="width:auto">提交时间从：</span>
                            <input class="mini-datepicker" format="yyyy-MM-dd" id="startTime" name="startTime"/>
                        </li>
                        <li style="margin-right: 15px">
                            <span class="text" style="width:auto">至：</span>
                            <input class="mini-datepicker" format="yyyy-MM-dd" id="endTime" name="endTime"/>
                        </li>
                        <li style="margin-right: 15px"><span class="text" style="width:auto">纪要状态: </span>
                            <input id="status" name="status" class="mini-combobox" style="width:150px;"
                                   textField="value" valueField="key" emptyText="请选择..."
                                   required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                                   data="[ {'key' : 'DRAFTED','value' : '草稿'},{'key' : 'RUNNING','value' : '审批中'},{'key' : 'SUCCESS_END','value' : '审批完成'},{'key' : 'DISCARD_END','value' : '作废'}]"
                            />
                        </li>
                    </ul>
                </div>
            </ul>
        </form>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="rwfjListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         onlyCheckSelection="true"
         url="${ctxPath}/rdm/core/rwfj/queryRwfj.do" idField="id" allowCellWrap="true"
         multiSelect="true" showColumnsMenu="true" sizeList="[20,50,100]" pageSize="20" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="15"></div>
            <div name="action" cellCls="actionIcons" width="30" headerAlign="center" align="center"
                 renderer="onActionRenderer" cellStyle="padding:0;">操作
            </div>
            <div field="meetingOrgDepName" width="40" headerAlign="center" align="center" renderer="render">组织部门</div>
            <div field="meetingOrgUserName" width="50" headerAlign="center" align="center" >会议负责人</div>
            <div field="meetingTheme" width="50" headerAlign="center" align="center" renderer="linkHyglAdress">会议主题</div>
            <div field="meetingContent" align="left" headerAlign="center" width="100" renderer="renderRw">会议纪要描述
            </div>
            <div field="meetingPlanRespName" align="center" headerAlign="center" width="35">第一责任人
            </div>
            <div field="zzrDepName" align="center" headerAlign="center" width="40">责任部门
            </div>
            <div field="meetingPlanEndTime" align="center" headerAlign="center" width="40"
                 dateFormat="yyyy-MM-dd">预计完成时间
            </div>
            <div field="status" width="30" headerAlign="center" align="center" allowSort="true"
                 renderer="onStatusRenderer">纪要状态
            </div>
            <div field="isComplete" align="center" headerAlign="center" width="30" renderer="isCompleteRenderer">完成自评
            </div>
            <div field="meetingPlanCompletion" align="left" headerAlign="center" width="100" renderer="renderQk">情况描述
            </div>
<%--            <div width="27" align="center" headerAlign="center" renderer="onRiskRenderer">进度风险</div>--%>
            <div width="27" align="center" headerAlign="center" renderer="onRiskStatusRenderer">进度风险</div>
            <div field="taskName" width="40" headerAlign='center' align='center' width="40">当前任务</div>
            <div field="allTaskUserNames" width="30" headerAlign='center' align='center' width="40">处理人</div>
            <div field="finishTime" width="40" dateFormat="yyyy-MM-dd" align="center" headerAlign="center"
                 allowSort="true">实际完成时间
            </div>
            <div width="35" align="center" headerAlign="center" renderer="onFinishRenderer">完成延误状态</div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var currentUserNo = "${currentUserNo}";
    var currentUserId = "${currentUserId}";
    var deptName = "${deptName}";
    var zrDeptName = "${zrDeptName}";
    var delayFlag = "${delayFlag}";
    var finishFlag = "${finishFlag}";
    var startTime = "${startTime}";
    var endTime = "${endTime}";
    var rwfjListGrid = mini.get("rwfjListGrid");

    $(function () {
        if (deptName){
            mini.get("meetingOrgDepName").setText(deptName);
            mini.get("meetingOrgDepName").setValue(deptName);
        }
        //zzrDepName
        if (zrDeptName){
            mini.get("zzrDepName").setText(zrDeptName);
            mini.get("zzrDepName").setValue(zrDeptName);
        }
        if (delayFlag){
            mini.get("delay").setText("是");
            mini.get("delay").setValue("yes");
        }
        if (finishFlag){
            mini.get("status").setText("审批中");
            mini.get("status").setValue("RUNNING");
        }
        if (startTime){
            mini.get("startTime").setText(startTime);
            mini.get("startTime").setValue(startTime);
        }
        if (endTime){
            mini.get("endTime").setText(endTime);
            mini.get("endTime").setValue(endTime);
        }
        if (startTime && endTime){
            no_more(this,'moreBox');
        }
        searchFrm();
    });

    function onActionRenderer(e) {
        var record = e.record;
        var id = record.id;
        var instId = record.instId;
        var status = record.status;
        var s = '';
        if (status != 'DRAFTED') {
            s += '<span  title="明细" onclick="rwfjDetail(\'' + id + '\',\'' + instId + '\')">明细</span>';
        }
        if (record.status == 'RUNNING') {
            var currentProcessUserId = record.currentProcessUserId;
            if (record.myTaskId) {
                s += '<span  title="办理" onclick="rwfjTask(\'' + record.myTaskId + '\')">办理</span>';
            }
        }
        return s;
    }

    function linkHyglAdress(e) {
        var record = e.record;
        var meetingTheme = record.meetingTheme;
        var meetingId = record.meetingId;
        var url = jsUseCtxPath + "/zhgl/core/hygl/editPage.do?meetingId=" + meetingId + "&action=detail";
        var linkStr = '<a href="#" style="color:#2ca9f6;" onclick="window.open(\'' + url + '\')">' + meetingTheme + '</a>';
        return linkStr;
    }

    function onRiskRenderer(e) {
        var record = e.record;
        var status = record.status;
        var meetingPlanEndTime = new Date(record.meetingPlanEndTime);
        var nowDate = new Date();
        var nowYear = nowDate.getFullYear();
        var month = nowDate.getMonth() + 1;
        var day = nowDate.getDate();
        var nowDateN = new Date(nowYear + "-" + month + "-" + day);
        var color = '#32CD32';
        var title = '未延误';
        if (status == 'SUCCESS_END') {
            return;
        }
        if (nowDateN.getTime() > meetingPlanEndTime.getTime() && status != 'SUCCESS_END') {
            color = '#fb0808';
            title = '已延误';
        }
        var s = '<span title= "' + title + '" style="display:inline-block;margin-right:5px;border-radius:10px;width:9px;height:9px;background-color:' + color + '"></span>';
        return s;
    }

    function onRiskStatusRenderer(e) {
        var record = e.record;
        var status = record.status;
        var meetingPlanEndTime = new Date(record.meetingPlanEndTime);
        var nowDate = new Date();
        var nowYear = nowDate.getFullYear();
        var month = nowDate.getMonth() + 1;
        var day = nowDate.getDate();
        var nowDateN = new Date(nowYear + "-" + month + "-" + day);
        var color = '#32CD32';
        var title = '正常';
        if (status == 'SUCCESS_END') {
            return;
        }
        if (nowDateN.getTime() > meetingPlanEndTime.getTime() && status != 'SUCCESS_END') {
            var timeNow = nowDateN.getTime();
            var timeEnd = meetingPlanEndTime.getTime();
            var timeDiff = (timeNow - timeEnd)/86400000;
            if (timeDiff <= 7){
                color = '#E0EAAA';
                title = '轻微';
            }else if(timeDiff > 7 && timeDiff <= 14){
                color = '#ff8c00';
                title = '一般';
            }else if(timeDiff > 14){
                color = '#E32121';
                title = '严重';
            }
            // color = '#fb0808';
            // title = '已延误';
        }
        var s = '<span title= "' + title + '" style="color:' + color + '"><b>'+title+'</span>';
        return s;
    }

    function onFinishRenderer(e) {
        var record = e.record;
        var status = record.status;
        var meetingPlanEndTime = new Date(record.meetingPlanEndTime);
        var finishTime = new Date(record.finishTime + " 00:00:00");
        var nowDate = new Date();
        var nowYear = nowDate.getFullYear();
        var month = nowDate.getMonth() + 1;
        var day = nowDate.getDate();
        var nowDateN = new Date(nowYear + "-" + month + "-" + day);
        var color = '#32CD32';
        var title = '未延误';
        if (nowDateN.getTime() > meetingPlanEndTime.getTime()&& status != 'SUCCESS_END') {
            color = '#fb0808';
            title = '已延误';
        }else if(meetingPlanEndTime.getTime()<finishTime.getTime()&& status == 'SUCCESS_END'){
            color = '#fb0808';
            title = '已延误';
        }
        var s = '<span title= "' + title + '" style="display:inline-block;margin-right:5px;border-radius:10px;width:9px;height:9px;background-color:' + color + '"></span>';
        return s;
    }

    function onStatusRenderer(e) {
        var record = e.record;
        var status = record.status;

        var arr = [{'key': 'DRAFTED', 'value': '草稿', 'css': 'orange'},
            {'key': 'RUNNING', 'value': '审批中', 'css': 'green'},
            {'key': 'SUCCESS_END', 'value': '审批完成', 'css': 'blue'},
            {'key': 'DISCARD_END', 'value': '作废', 'css': 'red'}
        ];

        return $.formatItemValue(arr, status);
    }

    function isCompleteRenderer(e) {
        var record = e.record;
        var isComplete = record.isComplete;
        var arr = [{'key': 'true', 'value': '是', 'css': 'green'},
            {'key': 'false', 'value': '否', 'css': 'red'}];
        return $.formatItemValue(arr, isComplete);
    }

    function renderRw(e) {
        var record = e.record;
        var html = "<div style='line-height: 20px;height:100px;overflow: auto' >";
        var planAndResult = record.meetingContent;
        if (planAndResult == null) {
            planAndResult = "";
        }
        html += '<span style="white-space:pre-wrap" >' + planAndResult + '</span>';
        html += '</div>';
        return html;
    }

    function renderQk(e) {
        var record = e.record;
        var html = "<div style='line-height: 20px;height:100px;overflow: auto' >";
        var planAndResult = record.meetingPlanCompletion;
        if (planAndResult == null) {
            planAndResult = "";
        }
        html += '<span style="white-space:pre-wrap" >' + planAndResult + '</span>';
        html += '</div>';
        return html;
    }

    $(function () {
        searchFrm();
    });


    function rwfjDetail(id) {
        var action = "detail";
        var url = jsUseCtxPath + "/rdm/core/rwfj/editPage.do?action=" + action + "&id=" + id;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (rwfjListGrid) {
                    rwfjListGrid.reload()
                }
            }
        }, 1000);
    }

    function rwfjTask(taskId) {
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
                        if (!winObj) {
                            clearInterval(loop);
                        } else if (winObj.closed) {
                            clearInterval(loop);
                            if (rwfjListGrid) {
                                rwfjListGrid.reload();
                            }
                        }
                    }, 1000);
                }
            }
        })
    }

    function removeRwfj(record) {
        var rows = [];
        if (record) {
            rows.push(record);
        } else {
            rows = rwfjListGrid.getSelecteds();
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
                for (var i = 0, l = rows.length; i < l; i++) {
                    var r = rows[i];
                    rowIds.push(r.id);
                    instIds.push(r.instId);
                }
                if (rowIds.length > 0) {
                    _SubmitJson({
                        url: jsUseCtxPath + "/rdm/core/rwfj/deleteRwfj.do",
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
            }
        });
    }

</script>
<redxun:gridScript gridId="rwfjListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>

