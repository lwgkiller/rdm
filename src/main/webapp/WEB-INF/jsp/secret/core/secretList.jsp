<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>保密协议列表</title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<div class="mini-toolbar" >
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul >
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">编号: </span>
                    <input class="mini-textbox" id="numinfo" name="numinfo" />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">项目: </span>
                    <input class="mini-textbox" id="projectName" name="projectName" />
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px"  plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px"  plain="true" onclick="clearForm()">清空查询</a>
                    <div style="display: inline-block" class="separator"></div>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="addSecret()">新增</a>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="exportSecret()">导出</a>
                </li>
            </ul>
        </form>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="secretListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onlyCheckSelection="true"
         url="${ctxPath}/rdm/core/secret/querySecret.do" idField="id"
         multiSelect="true" showColumnsMenu="true" sizeList="[20,50,100]" pageSize="20" allowAlternating="true" pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div type="indexcolumn" width="28" headerAlign="center" align="center">序号</div>
            <div name="action" cellCls="actionIcons" width="80" headerAlign="center" align="center" renderer="onActionRenderer" cellStyle="padding:0;">操作</div>
            <div field="numinfo" headerAlign="center" align="center" allowSort="false">编号</div>
            <div field="projectName" headerAlign="center" align="center" allowSort="false">项目</div>
            <div field="partner" headerAlign="center" align="center" allowSort="false">合作相关方</div>
            <div field="applyName" headerAlign="center" align="center" allowSort="false">经办人</div>
            <div field="taskName" headerAlign='center' align='center' width="40">当前任务</div>
            <div field="allTaskUserNames" headerAlign='center' align='center' width="40">当前处理人</div>
            <div field="status" width="25" headerAlign="center" align="center" allowSort="true"
                 renderer="onStatusRenderer">状态
            </div>
            <div field="CREATE_TIME_" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" headerAlign="center" allowSort="true">创建时间</div>
        </div>
    </div>
</div>
<form id="excelForm" action="${ctxPath}/rdm/core/secret/exportSecretList.do" method="post" target="excelIFrame">
    <input type="hidden" name="filter" id="filter"/>
</form>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var currentUserNo="${currentUserNo}";
    var currentUserId="${currentUserId}";
    var secretId = "${secretId}";
    var secretListGrid=mini.get("secretListGrid");

    function onActionRenderer(e) {
        var record = e.record;
        var secretId = record.secretId;
        var instId = record.instId;
        var status = record.status;
        var s = '';
        if (status != 'DRAFTED'){
            s += '<span  title="明细" onclick="secretDetail(\'' + secretId + '\',\'' + instId + '\')">明细</span>';
        }
        if (record.status == 'DRAFTED') {
            s += '<span  title="编辑" onclick="secretEdit(\'' + secretId + '\',\'' + instId + '\')">编辑</span>';
        }
        if(record.status =='RUNNING'){
            var currentProcessUserId=record.currentProcessUserId;
            if(record.myTaskId) {
                s+='<span  title="办理" onclick="secretTask(\'' + record.myTaskId + '\')">办理</span>';
            }
        }
        if (status == 'DRAFTED' || status == 'DISCARD_END' || currentUserNo=='admin') {
            s += '<span  title="删除" onclick="removeSecret(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
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

    
    function addSecret() {
        mini.open({
            title: "保密协议",
            url: jsUseCtxPath + "/bpm/core/bpmInst/BMXY/start.do",
            width: 850,
            height: 600,
            showModal: false,
            allowResize: true,
            onload: function () {
                secretListGrid.load();
            },
            ondestroy: function (action) {
                if (secretListGrid) {
                    secretListGrid.load();
                }
            }
        });
    }
    function secretEdit(secretId,instId) {
        mini.open({
            title: "保密协议",
            url: jsUseCtxPath + "/bpm/core/bpmInst/start.do?instId=" + instId+ "&secretId=" + secretId,
            width: 850,
            height: 600,
            showModal: false,
            allowResize: true,
            onload: function () {
                secretListGrid.load();
            },
            ondestroy: function (action) {
                if (secretListGrid) {
                    secretListGrid.load();
                }
            }
        });
    }


    function secretDetail(secretId) {
        mini.open({
            title: "保密协议",
            url: jsUseCtxPath + "/rdm/core/secret/editPage.do?action=detail&secretId=" + secretId,
            width: 850,
            height: 600,
            showModal: false,
            allowResize: true,
            onload: function () {
                secretListGrid.load();
            },
            ondestroy: function (action) {
                if (secretListGrid) {
                    secretListGrid.load();
                }
            }
        });
    }

    function secretTask(taskId) {
        $.ajax({
            url: jsUseCtxPath + '/bpm/core/bpmTask/checkTaskLockStatus.do?taskId=' + taskId,
            async:false,
            success: function (result) {
                debugger
                if (!result.success) {
                    top._ShowTips({
                        msg: result.message
                    });
                } else {
                    mini.open({
                        title: "保密协议",
                        url: jsUseCtxPath + '/bpm/core/bpmTask/toStart.do?taskId=' + taskId,
                        width: 850,
                        height: 600,
                        showModal: false,
                        allowResize: true,
                        onload: function () {
                            secretListGrid.load();
                        },
                        ondestroy: function (action) {
                            if (secretListGrid) {
                                secretListGrid.load();
                            }
                        }
                    });
                }
            }
        })
    }

    function removeSecret(record) {
        var rows = [];
        if (record) {
            rows.push(record);
        } else {
            rows = secretListGrid.getSelecteds();
        }
        if (rows.length <= 0) {
            mini.alert("请至少选中一条记录");
            return;
        }
        mini.confirm("确定删除选中记录？", "提示", function (action) {
            if (action != 'ok') {
                return;
            }else {
                var rowIds = [];
                for (var i = 0, l = rows.length; i < l; i++) {
                    var r = rows[i];
                    rowIds.push(r.secretId);
                }
                if (rowIds.length > 0) {
                    _SubmitJson({
                        url: jsUseCtxPath + "/rdm/core/secret/deleteSecret.do",
                        method: 'POST',
                        showMsg: false,
                        data: {ids: rowIds.join(',')},
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
    function exportSecret() {
        var parent = $(".search-form");
        var inputAry = $("input", parent);
        var params = [];
        inputAry.each(function (i) {
            var el = $(this);
            var obj = {};
            obj.name = el.attr("name");
            if (!obj.name) return true;
            obj.value = el.val();
            params.push(obj);
        });
        $("#filter").val(mini.encode(params));
        var excelForm = $("#excelForm");
        excelForm.submit();
    }
</script>
<redxun:gridScript gridId="secretListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl="" />
</body>
</html>

