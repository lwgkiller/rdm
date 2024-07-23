<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>硬件变更列表</title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<div class="mini-toolbar" >
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul >
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">记录表名: </span>
                    <input class="mini-textbox" id="gridName" name="gridName" />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">涉及零部件名称: </span>
                    <input class="mini-textbox" id="bjName" name="bjName" />
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px"  plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px"  plain="true" onclick="clearForm()">清空查询</a>
                    <div style="display: inline-block" class="separator"></div>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="addGys()">供应商整改</a>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="addZjc()">主机厂整改</a>
                </li>
            </ul>
        </form>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="yjbgListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onlyCheckSelection="true"
         url="${ctxPath}/evn/core/Yjbg/queryYjbg.do" idField="id"
         multiSelect="true" showColumnsMenu="true" sizeList="[20,50,100]" pageSize="20" allowAlternating="true" pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div type="indexcolumn" width="20" headerAlign="center" align="center">序号</div>
            <div name="action" cellCls="actionIcons" width="30" headerAlign="center" align="center" renderer="onActionRenderer" cellStyle="padding:0;">操作</div>
            <div field="gridName" width="50" headerAlign="center" align="center" allowSort="false">记录表名</div>
            <div field="bjName" width="40" headerAlign="center" align="center" allowSort="false">涉及零部件名称</div>
            <div field="changeType" width="30" headerAlign="center" align="center" allowSort="false" renderer="onChangeType">整改类型</div>
            <div field="userName" headerAlign="center" align="center" allowSort="false" width="40">上传人</div>
            <div field="CREATE_TIME_" width="25" headerAlign="center" align="center" allowSort="false" width="60">上传时间</div>
            <div field="taskName" headerAlign='center' align='center' width="40">当前任务</div>
            <div field="allTaskUserNames" headerAlign='center' align='center' width="40">当前处理人</div>
            <div field="status" width="25" headerAlign="center" align="center" allowSort="true"
                 renderer="onStatusRenderer">状态
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var currentUserNo="${currentUserNo}";
    var currentUserId="${currentUserId}";
    var yjbgListGrid=mini.get("yjbgListGrid");
    //操作栏
    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var id = record.id;
        var instId = record.instId;
        var status = record.status;
        var changeType = record.changeType;
        var s = '';
        if (status != 'DRAFTED'){
            s += '<span  title="明细" onclick="yjbgDetail(\'' + id + '\',\'' + instId + '\',\''+changeType+'\')">明细</span>';
        }
        if (record.status == 'DRAFTED') {
            s += '<span  title="编辑" onclick="yjbgEdit(\'' + id + '\',\'' + instId + '\',\''+changeType+'\')">编辑</span>';
        }
        if(record.status =='RUNNING'){
            if(record.myTaskId) {
                s+='<span  title="办理" onclick="yjbgTask(\'' + record.myTaskId + '\')">办理</span>';
            }
        }
        if (status == 'DRAFTED' ) {
            s += '<span  title="删除" onclick="yjbgRemove(\'' + id + '\')">删除</span>';
        }
        if (status == 'DISCARD_END' && currentUserId ==record.CREATE_BY_) {
            s += '<span  title="删除" onclick="yjbgRemove(\'' + id + '\')">删除</span>';
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
    function onChangeType(e) {
        var record = e.record;
        var changeType = record.changeType;

        var arr = [ {'key' : 'gys','value' : '供应商整改','css' : 'black'},
            {'key' : 'zjc','value' : '主机厂整改','css' : 'black'},

        ];
        return $.formatItemValue(arr,changeType);
    }


    function addGys() {
        var url = jsUseCtxPath + "/bpm/core/bpmInst/GYSZG/start.do?changeType=gys";
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (yjbgListGrid) {
                    yjbgListGrid.reload()
                }
            }
        }, 1000);
    }

    function addZjc() {
        var url = jsUseCtxPath + "/bpm/core/bpmInst/ZJCZG/start.do?";
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (yjbgListGrid) {
                    yjbgListGrid.reload()
                }
            }
        }, 1000);
    }

    function yjbgDetail(id,instId,changeType) {
        var action = "detail";
        var urlGys = jsUseCtxPath + "/evn/core/Yjbg/editGysPage.do?action=detail&id=" + id + "&changeType=" + changeType;
        var urlZjc = jsUseCtxPath + "/evn/core/Yjbg/editZjcPage.do?action=detail&id=" + id + "&changeType=" + changeType;
        if(changeType=="zjc"){
            var winObj = window.open(urlZjc);
        }else {
            var winObj = window.open(urlGys);
        }
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (yjbgListGrid) {
                    yjbgListGrid.reload()
                }
            }
        }, 1000);
    }

    function yjbgEdit(id,instId) {
        var url = jsUseCtxPath + "/bpm/core/bpmInst/start.do?instId=" + instId+ "&id=" + id;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (yjbgListGrid) {
                    yjbgListGrid.reload()
                }
            }
        }, 1000);
    }

    function yjbgTask(taskId) {
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
                            if (yjbgListGrid) {
                                yjbgListGrid.reload();
                            }
                        }
                    }, 1000);
                }
            }
        })
    }

    function yjbgRemove(id) {
        mini.confirm("确定删除选中记录？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                _SubmitJson({
                    url: jsUseCtxPath + "/evn/core/Yjbg/deleteYjbg.do",
                    method: 'POST',
                    showMsg:false,
                    data: {id: id},
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
<redxun:gridScript gridId="yjbgListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl="" />
</body>
</html>

