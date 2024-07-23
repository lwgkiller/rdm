<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title></title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<div class="mini-toolbar" >
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 10px">
            <ul >
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">事项: </span>
                    <input class="mini-textbox" id="description" name="Q_description" style="width: 300px;max-width: 300px"/>
                    <input class="mini-textbox" name="test" style="display: none"/>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px"  plain="true" onclick="searchFrm()">查询</a>
                </li>
            </ul>
        </form>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="todoDatagrid" class="mini-datagrid" style="width: 100%; height: 100%;"
         allowResize="false" url="${ctxPath}/rdmHome/core/rdmHomeTabContent.do?name=todo" autoLoad="true" onload="dataGridLoad"
         idField="id" showPager="true" multiSelect="false" showColumnsMenu="true" allowAlternating="true" sizeList="[7,15,50,100]" pageSize="7"
    >
        <div property="columns">
            <div name="action" cellCls="actionIcons" headerAlign="center" align="center" width="85" renderer="onActionRenderer"
                 cellStyle="padding:0;">操作
            </div>
            <div field="description" sortField="CREATE_TIME_" width="390" headerAlign="center" align="left" allowSort="true">
                事项
            </div>
            <div field="name" headerAlign="center" align="left">审批环节</div>
        </div>
    </div>
</div>


<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var grid = mini.get("todoDatagrid");

    function dataGridLoad(e) {
        window.parent.queryTabNums();
    }

    function onActionRenderer(e) {
        debugger;
        var record = e.record;
        var solKey = record.solKey;
        if (!solKey || (solKey != 'HYGL'&&solKey != 'JXBZZB' && solKey != 'TdmTodo'
            && solKey != 'TdmMsgTodo' && solKey != 'OACWCB'&&solKey != 'TYPEFINFO'&&solKey != 'QMSTODO'&&solKey != 'GLBZFJSP')) {
            var taskId = record.id;
            var locked = record.locked;
            var belongSubSysKey = record.belongSubSysKey;
            if (!belongSubSysKey) {
                belongSubSysKey= '';
            }
            var s = '<span style="color: #409EFF;cursor: pointer " title="办理" onclick="checkAndHandTask(\'' + taskId +'\',\''+belongSubSysKey+'\')">办理</span>';
            if (locked) {
                s += '<span style="color: green;cursor: pointer " title="释放" onclick="releaseTask(\'' + taskId + '\')">释放</span>';
            }
        } else if (solKey == "JXBZZB") {
            s = '<span style="color: #409EFF;cursor: pointer " title="办理" onclick="processJxbzzb(\'' + record.id + '\',\'' + record.executionId + '\')">办理</span>';
        } else if (solKey == "TdmTodo") {
            s = '<span style="color: #409EFF;cursor: pointer " title="办理" onclick="processTdm(\'' + record.id + '\')">办理</span>';
        } else if (solKey == 'TdmMsgTodo') {
            s = '<span style="color: #409EFF;cursor: pointer " title="办理" onclick="processTdmMsg(\'' + record.id + '\')">办理</span>';
        } else if (solKey == 'OACWCB') {
            s = '<span style="color: #409EFF;cursor: pointer " title="办理" onclick="processOACWCB(\'' + record.id + '\',\'' + record.instId + '\')">办理</span>';
        }else if (solKey == 'TYPEFINFO') {
            s = '<span style="color: #409EFF;cursor: pointer " title="办理" onclick="materielTypeFEdit(\'' + record.id + '\')">办理</span>';
        }else if (solKey == 'QMSTODO') {
            s = '<span style="color: #409EFF;cursor: pointer " title="办理" onclick="qmsTodo(\'' + record.link + '\')">办理</span>';
        }else if (solKey == 'HYGL') {
            s = '<span style="color: #409EFF;cursor: pointer " title="办理" onclick="feedBackMeeting(\'' + record.id + '\')">办理</span>';
        }else if (solKey == 'GLBZFJSP') {
            s = '<span style="color: #409EFF;cursor: pointer " title="办理" onclick="processGLBZFJSP(\'' + record.id + '\')">办理</span>';
        }
        return s;
    }

    function qmsTodo(link) {
        var winObj = openNewWindow(link, "handTask");
        var loop = setInterval(function () {
            if (!winObj) {
                clearInterval(loop);
            } else if (winObj.closed) {
                clearInterval(loop);
                if (grid) {
                    grid.load();
                }
            }
        }, 1000);
    }

    function materielTypeFEdit(id) {
        var action = "edit";
        var url = jsUseCtxPath + "/materielTypeF/core/editPage.do?id=" + id+ "&action=" + action ;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (materielTypeFListGrid) {
                    materielTypeFListGrid.reload()
                }
            }
        }, 1000);
    }

    function feedBackMeeting(meetingId) {
        var url = jsUseCtxPath + "/zhgl/core/hygl/editPage.do?meetingId=" + meetingId + "&action=feedback";
        var winObj = window.open(url, '');
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (meetingListGrid) {
                    meetingListGrid.reload();
                }
            }
        }, 1000);
    }

    function checkAndHandTask(taskId,belongSubSysKey) {
        $.ajax({
            url: jsUseCtxPath + '/bpm/core/bpmTask/checkTaskLockStatus.do?taskId=' + taskId,
            async: false,
            success: function (result) {
                if (!result.success) {
                    mini.alert(result.message);
                    if (grid) {
                        grid.load();
                    }
                } else {
                    handTask(taskId,belongSubSysKey);
                }
            }

        })
    }

    /**
     * 释放任务
     */
    function releaseTask(taskId) {
        var url = jsUseCtxPath + '/bpm/core/bpmTask/releaseTask.do?taskId=' + taskId;

        var config = {};
        config.url = url;
        config.data = {'taskId': taskId};
        config.success = function (result) {
            if (result.success) {
                if (grid) {
                    grid.load();
                }
            }
        }
        _SubmitJson(config);
    }

    function handTask(taskId,belongSubSysKey) {
        var pageUrl = '/bpm/core/bpmTask/toStart.do?taskId=' + taskId;
        var url = "${ctxPath}/pageJumpRedirect.do?targetSubSysKey="+belongSubSysKey+"&targetUrl="+encodeURIComponent(pageUrl);
        var winObj = openNewWindow(url, "handTask");
        var loop = setInterval(function () {
            if (!winObj) {
                clearInterval(loop);
            } else if (winObj.closed) {
                clearInterval(loop);
                if (grid) {
                    grid.load();
                }
            }
        }, 1000);

    }

    function processJxbzzb(notMadeId, jxbzzbshInstId) {
        if (!jxbzzbshInstId || jxbzzbshInstId == '') {
            var url = jsUseCtxPath + "/bpm/core/bpmInst/JXBZZBSH/start.do?notMadeId=" + notMadeId;
            var winObj = window.open(url);
        } else {
            var url = jsUseCtxPath + "/bpm/core/bpmInst/start.do?instId=" + jxbzzbshInstId;
            var winObj = window.open(url);
        }
    }

    function processTdm(tdmBusinessId) {
        var winObj = window.open('_blank', '待办处理');
        _SubmitJson({
            //TDM代办链接获取
            url: jsUseCtxPath + "/xcmgTdm/core/requestapi/getTdmTodoUrl.do",
            method: 'POST',
            async: false,
            showMsg: false,
            data: {tdmBusinessId: tdmBusinessId},
            //TDM代办链接获取成功后直接回调打开待办页面
            success: function (data) {
                if (data) {
                    winObj.location = data.data;
                }
            }
        });
    }

    function processTdmMsg(tdmBusinessId) {
        var winObj = window.open('_blank', '待办处理');
        _SubmitJson({
            //TDM代办链接获取
            url: jsUseCtxPath + "/xcmgTdm/core/requestapi/getTdmMsgTodoUrl.do",
            method: 'POST',
            async: false,
            showMsg: false,
            data: {tdmBusinessId: tdmBusinessId},
            //TDM代办链接获取成功后直接回调打开待办页面
            success: function (data) {
                if (data) {
                    winObj.location = data.data;
                }
            }
        });
    }

    //编辑（直接跳转到详情的业务controller）
    function processOACWCB(id, oaFlowId) {
        var action = "edit";
        var url = jsUseCtxPath + "/oa/oaFinance/editPage.do?action=" + action + "&id=" + id + "&oaFlowId=" + oaFlowId;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if(!winObj) {
                clearInterval(loop);
            } else if (winObj.closed) {
                clearInterval(loop);
                if (oaFinanceFlowListGrid) {
                    oaFinanceFlowListGrid.reload();
                }
            }
        }, 1000);
    }

    function processGLBZFJSP(id) {
        var action = "review";
        mini.open({
            title: standardEdit_name13,
            url: jsUseCtxPath + "/standardManager/core/standardFileInfos/attachFileListWindow.do?action="+action+"&standardType=GL&processId="+id,
            width: 1600,
            height: 600,
            showModal: false,
            allowResize: true,
            onload: function () {
            },
            ondestroy: function () {
                grid.reload();
            }
        });
    }
</script>
</body>
</html>
