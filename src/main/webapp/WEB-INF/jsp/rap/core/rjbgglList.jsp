<%--
  Created by IntelliJ IDEA.
  User: matianyu
  Date: 2021/2/23
  Time: 14:57
  To change this template use File | Settings | File Templates.
--%>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>列表管理</title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">设计型号: </span>
                    <input class="mini-textbox" id="modelName" name="modelName"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">申请人: </span>
                    <input class="mini-textbox" id="applyName" name="applyName"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">申请部门: </span>
                    <input class="mini-textbox" id="appDeptName" name="appDeptName"/>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px"  plain="true" onclick="clearForm()">清空查询</a>
                    <a id="editMsg" class="mini-button " style="margin-right: 5px" plain="true"
                       onclick="addRjbggl()">新增</a>
                </li>
            </ul>
        </form>
        <span class="searchSelectionBtn" onclick="no_search(this ,'searchForm')">
				<i class="icon-sc-lower"></i>
			</span>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="rjbgglListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         sizeList="[50,100,200]" pageSize="50"
         url="${ctxPath}/environment/core/Rjbggl/queryRjbggl.do"
         idField="id" allowAlternating="true" showPager="true" multiSelect="true">
        <div property="columns">
            <div type="checkcolumn" width="10"></div>
            <div name="action" cellCls="actionIcons" width="50" headerAlign="center" align="center"
                 renderer="onMessageActionRenderer" cellStyle="padding:0;">操作
            </div>
            <div field="noticeNo" headerAlign='center' align='center' width="40">流程编号</div>
            <div field="bjName"  width="40" headerAlign="center" align="center" allowSort="true">部件名称及型号</div>
            <div field="wlNumber"  width="40" headerAlign="center" align="center" allowSort="true">物料号</div>
            <div field="supplier"  width="40" headerAlign="center" align="center" allowSort="true">供应商公司名称</div>
            <div field="applyName" headerAlign='center' width="20" align='center'>申请人</div>
            <div field="appDeptName"  width="50" headerAlign="center" align="center" allowSort="true">申请部门</div>
            <div field="taskName" headerAlign='center' align='center' width="60">当前任务</div>
            <div field="allTaskUserNames" headerAlign='center' align='center' width="40">当前处理人</div>
            <div field="status" width="25" headerAlign="center" align="center" allowSort="true"
                 renderer="onStatusRenderer">状态
            </div>
            <div field="CREATE_TIME_" width="30" dateFormat="yyyy-MM-dd" headerAlign='center' align="center">申请时间</div>
        </div>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var rjbgglListGrid = mini.get("rjbgglListGrid");
    var currentUserId = "${currentUserId}";
    var currentUserDep = "${currentUserDep}";
    var currentUserNo = "${currentUserNo}";


    function onMessageActionRenderer(e) {
        var record = e.record;
        var id = record.id;
        var instId = record.instId;
        var status = record.status;
        var s = '';
        if (status != 'DRAFTED'){
            s += '<span  title="明细" onclick="rjbgglDetail(\'' + id + '\',\'' + status + '\')">明细</span>';
        }
        if (record.status == 'DRAFTED') {
            s += '<span  title="编辑" onclick="rjbgglEdit(\'' + id + '\',\'' + instId + '\')">编辑</span>';
        }
        if(record.status =='RUNNING'){
            if(record.myTaskId) {
                s+='<span  title="办理" onclick="rjbgglTask(\'' + record.myTaskId + '\')">办理</span>';
            }
        }
        if (status == 'DRAFTED' ) {
            s += '<span  title="删除" onclick="removeRjbggl(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
        }
        if (status == 'DISCARD_END' &&(currentUserId ==record.CREATE_BY_)) {
            s += '<span  title="删除" onclick="removeRjbggl(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
        }
        return s;
    }
    

    function onStatusRenderer(e) {
        var record = e.record;
        var status = record.status;

        var arr = [ {'key' : 'DRAFTED','value' : '草稿','css' : 'orange'},
            {'key' : 'RUNNING','value' : '审批中','css' : 'green'},
            {'key' : 'SUCCESS_END','value' : '批准','css' : 'blue'},
            {'key' : 'DISCARD_END','value' : '作废','css' : 'red'}
        ];

        return $.formatItemValue(arr,status);
    }

    $(function () {
        searchFrm();
    });


    function addRjbggl() {
        var url = jsUseCtxPath + "/bpm/core/bpmInst/RJBGGL/start.do";
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (rjbgglListGrid) {
                    rjbgglListGrid.reload()
                }
            }
        }, 1000);
    }
    function rjbgglEdit(id,instId) {
        var url = jsUseCtxPath + "/bpm/core/bpmInst/start.do?instId=" + instId+ "&id=" + id;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (rjbgglListGrid) {
                    rjbgglListGrid.reload()
                }
            }
        }, 1000);
    }


    function rjbgglDetail(id,status) {
        var action = "detail";
        var url = jsUseCtxPath + "/environment/core/Rjbggl/editPage.do?action=" + action + "&id=" + id+ "&status=" + status;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (rjbgglListGrid) {
                    rjbgglListGrid.reload()
                }
            }
        }, 1000);
    }

    function rjbgglTask(taskId) {
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
                            if (rjbgglListGrid) {
                                rjbgglListGrid.reload();
                            }
                        }
                    }, 1000);
                }
            }
        })
    }

    function removeRjbggl(record) {
        var rows = [];
        if (record) {
            rows.push(record);
        } else {
            rows = rjbgglListGrid.getSelecteds();
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
                for (var i = 0, l = rows.length; i < l; i++) {
                    var r = rows[i];
                    rowIds.push(r.id);
                }
                _SubmitJson({
                    url: jsUseCtxPath + "/environment/core/Rjbggl/deleteRjbggl.do",
                    method: 'POST',
                    showMsg:false,
                    data: {ids: rowIds.join(',')},
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


</script>
<redxun:gridScript gridId="rjbgglListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
