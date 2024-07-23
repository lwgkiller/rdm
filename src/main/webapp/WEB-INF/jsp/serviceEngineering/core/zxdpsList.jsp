<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>手册信息反馈列表</title>
    <%@include file="/commons/list.jsp" %>

</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.zxdpsList.name" />: </span>
                    <input class="mini-textbox" id="applyNumber" name="applyNumber"/>
                    <span class="text" style="width:auto"><spring:message code="page.zxdpsList.name1" />: </span>
                    <input class="mini-textbox" id="creatorName" name="creatorName"/>
                    <span class="text" style="width:auto"><spring:message code="page.zxdpsList.name2" />: </span>
                    <input class="mini-textbox" id="topicName" name="topicName"/>
                    <span class="text" style="width:auto"><spring:message code="page.zxdpsList.name3" />: </span>
                    <input class="mini-textbox" id="topicId" name="topicId"/>
                    <span class="text" style="width:auto"><spring:message code="page.zxdpsList.name4" />: </span>
                    <input class="mini-textbox" id="topicType" name="topicType"/>
                    <span class="text" style="width:auto"><spring:message code="page.zxdpsList.name5" />: </span>
                    <input class="mini-textbox" id="topicTextName" name="topicTextName"/>

                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()"><spring:message code="page.zxdpsList.name6" /></a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()"><spring:message code="page.zxdpsList.name7" /></a>
                    <div style="display: inline-block" class="separator"></div>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="addZxdps()"><spring:message code="page.zxdpsList.name8" /></a>
                </li>
            </ul>
        </form>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="zxdpsListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         onlyCheckSelection="true"
         url="${ctxPath}/serviceEngineering/core/zxdps/applyList.do" idField="id"
         multiSelect="true" showColumnsMenu="true" sizeList="[20,50,100]" pageSize="20" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div type="indexcolumn" width="45" headerAlign="center" align="center"><spring:message code="page.zxdpsList.name9" /></div>
            <div name="action" cellCls="actionIcons" width="100" headerAlign="center" align="center"
                 renderer="onActionRenderer" cellStyle="padding:0;"><spring:message code="page.zxdpsList.name10" />
            </div>
            <div field="applyNumber" width="200" headerAlign="center" align="center" allowSort="false"><spring:message code="page.zxdpsList.name" /></div>
            <div field="creatorName" headerAlign="center" align="center" allowSort="false"><spring:message code="page.zxdpsList.name1" /></div>
            <div field="applyTime" width="150" dateFormat="yyyy-MM-dd HH:mm:ss" headerAlign="center" align="center" allowSort="false"><spring:message code="page.zxdpsList.name11" /></div>

            <div field="taskName" headerAlign='center' align='center' width="100"><spring:message code="page.zxdpsList.name12" /></div>
            <div field="allTaskUserNames" headerAlign='center' align='center' width="100"><spring:message code="page.zxdpsList.name13" /></div>
            <div field="status" width="50" headerAlign="center" align="center" allowSort="true"
                 renderer="onStatusRenderer"><spring:message code="page.zxdpsList.name14" />
            </div>
            <div field="CREATE_TIME_" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" headerAlign="center"
                 allowSort="true"><spring:message code="page.zxdpsList.name15" />
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var currentUserNo = "${currentUserNo}";
    var currentUserId = "${currentUserId}";
    var isZks = "${isZks}";
    var isFbr = "${isFbr}";
    var zxdpsListGrid = mini.get("zxdpsListGrid");

    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var applyId = record.id;
        var instId = record.instId;
        var status = record.status;
        var s = '';
        if (status != 'DRAFTED') {
            s += '<span  title=' + zxdpsList_name + ' onclick="zxdpsDetail(\'' + applyId + '\',\'' + instId + '\')">' + zxdpsList_name + '</span>';
        }
        if (record.status == 'DRAFTED') {
            s += '<span  title=' + zxdpsList_name1 + ' onclick="zxdpsEdit(\'' + applyId + '\',\'' + instId + '\')">' + zxdpsList_name1 + '</span>';
        }

        if (status == 'RUNNING' && record.myTaskId) {
            s += '<span  title=' + zxdpsList_name2 + ' onclick="zxdpsTask(\'' + record.myTaskId + '\')">' + zxdpsList_name2 + '</span>';
        }

        if (status == 'DRAFTED' || currentUserNo == 'admin') {
            s += '<span  title=' + zxdpsList_name3 + ' onclick="removeZxdps(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">' + zxdpsList_name3 + '</span>';
        }
        return s;
    }

    function onStatusRenderer(e) {
        var record = e.record;
        var status = record.status;

        var arr = [{'key': 'DRAFTED', 'value': '草稿', 'css': 'orange'},
            {'key': 'RUNNING', 'value': '审批中', 'css': 'green'},
            {'key': 'SUCCESS_END', 'value': '批准', 'css': 'blue'},
            {'key': 'DISCARD_END', 'value': '作废', 'css': 'red'}
        ];

        return $.formatItemValue(arr, status);
    }

    $(function () {
        searchFrm();
    });


    function addZxdps() {
        if (isFbr == "false" && currentUserId != "1") {
            mini.alert(zxdpsList_name4);
            return;
        }
        var url = jsUseCtxPath + "/bpm/core/bpmInst/ZXDPS/start.do";
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (zxdpsListGrid) {
                    zxdpsListGrid.reload()
                }
            }
        }, 1000);
    }

    function zxdpsEdit(applyId, instId) {
        var url = jsUseCtxPath + "/bpm/core/bpmInst/start.do?instId=" + instId + "&applyId=" + applyId;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (zxdpsListGrid) {
                    zxdpsListGrid.reload()
                }
            }
        }, 1000);
    }


    function zxdpsDetail(applyId) {
        var action = "detail";
        var url = jsUseCtxPath + "/serviceEngineering/core/zxdps/applyEditPage.do?action=detail&applyId=" + applyId;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (zxdpsListGrid) {
                    zxdpsListGrid.reload()
                }
            }
        }, 1000);
    }


    function zxdpsTask(taskId) {
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
                            if (zxdpsListGrid) {
                                zxdpsListGrid.reload();
                            }
                        }
                    }, 1000);
                }
            }
        })
    }

    function removeZxdps(record) {
        var rows = [];
        if (record) {
            rows.push(record);
        } else {
            rows = zxdpsListGrid.getSelecteds();
        }
        if (rows.length <= 0) {
            mini.alert(zxdpsList_name5);
            return;
        }
        mini.confirm(zxdpsList_name6, zxdpsList_name7, function (action) {
            if (action != 'ok') {
                return;
            } else {
                var rowIds = [];
                var instIds = [];
                var existStartInst = false;
                for (var i = 0, l = rows.length; i < l; i++) {
                    var r = rows[i];
                    if (r.status == 'DRAFTED' && r.CREATE_BY_ == currentUserId) {
                        rowIds.push(r.id);
                        instIds.push(r.instId);
                    }
                    else {
                        existStartInst = true;
                        continue;
                    }
                }
                if (existStartInst) {
                    alert(zxdpsList_name8);
                }
                if (rowIds.length > 0) {
                    _SubmitJson({
                        url: jsUseCtxPath + "/serviceEngineering/core/zxdps/deleteApply.do",
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

    function copyBusiness() {
        var row = businessListGrid.getSelected();
        if (!row) {
            mini.alert(zxdpsList_name5);
            return;
        } else if (row.manualStatus != "可打印") {
            mini.alert(zxdpsList_name9);
            return;
        }
        var id = row.id;
        var url = jsUseCtxPath + "/serviceEngineering/core/zxdps/applyEditPage.do?id=" + id + '&action=copy';
        var winObj = window.open(url, '');
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (businessListGrid) {
                    businessListGrid.reload();
                }
            }
        }, 1000);
    }


</script>
<redxun:gridScript gridId="zxdpsListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>

