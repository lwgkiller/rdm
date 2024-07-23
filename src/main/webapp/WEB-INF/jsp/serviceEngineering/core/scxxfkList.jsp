<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>操保手册信息反馈列表</title>
    <%@include file="/commons/list.jsp" %>

</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.scxxfkList.name" />: </span>
                    <input class="mini-textbox" id="applyNumber" name="applyNumber"/>
                    <span class="text" style="width:auto"><spring:message code="page.scxxfkList.name1" />: </span>
                    <input class="mini-textbox" id="creatorName" name="creatorName"/>
                    <span class="text" style="width:auto"><spring:message code="page.scxxfkList.name2" />: </span>
                    <input class="mini-textbox" id="departName" name="departName"/>
                    <span class="text" style="width:auto"><spring:message code="page.scxxfkList.name3" />: </span>
                    <input property="editor" class="mini-combobox"
                           style="width:98%;"
                           id="adoptions" name="adoptions"
                           textField="key" valueField="value" emptyText="<spring:message code="page.scxxfkList.name4" />..."
                           required="false" allowInput="false" showNullItem="true"
                           nullItemText="<spring:message code="page.scxxfkList.name4" />..."
                           data="[{'key' : '采纳','value' : '采纳'}
                                       ,{'key' : '部分采纳','value' : '部分采纳'}
                                       ,{'key' : '不采纳','value' : '不采纳'}
                                  ]"
                    />
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()"><spring:message code="page.scxxfkList.name5" /></a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()"><spring:message code="page.scxxfkList.name6" /></a>
                    <div style="display: inline-block" class="separator"></div>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="addScxxfk()"><spring:message code="page.scxxfkList.name7" /></a>
                </li>
            </ul>
        </form>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="scxxfkListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         onlyCheckSelection="true"
         url="${ctxPath}/serviceEngineering/core/scxxfk/applyList.do" idField="id"
         multiSelect="true" showColumnsMenu="true" sizeList="[20,50,100]" pageSize="20" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div type="indexcolumn" width="28" headerAlign="center" align="center"><spring:message code="page.scxxfkList.name8" /></div>
            <div name="action" cellCls="actionIcons" width="80" headerAlign="center" align="center"
                 renderer="onActionRenderer" cellStyle="padding:0;"><spring:message code="page.scxxfkList.name9" />
            </div>
            <div field="applyNumber" headerAlign="center" align="center" allowSort="false"><spring:message code="page.scxxfkList.name" /></div>
            <div field="creatorName" headerAlign="center" align="center" allowSort="false"><spring:message code="page.scxxfkList.name1" /></div>
            <div field="departName" headerAlign="center" align="center" allowSort="false"><spring:message code="page.scxxfkList.name2" /></div>
            <div field="applyTime" dateFormat="yyyy-MM-dd HH:mm:ss" headerAlign="center" align="center"
                 allowSort="false"><spring:message code="page.scxxfkList.name10" />
            </div>
            <div field="adoptions" headerAlign="center" align="center" allowSort="false"><spring:message code="page.scxxfkList.name3" /></div>
            <div field="taskName" headerAlign='center' align='center' width="60"><spring:message code="page.scxxfkList.name11" /></div>
            <div field="allTaskUserNames" headerAlign='center' align='center' width="40"><spring:message code="page.scxxfkList.name12" /></div>
            <div field="status" width="25" headerAlign="center" align="center" allowSort="true"
                 renderer="onStatusRenderer"><spring:message code="page.scxxfkList.name13" />
            </div>
            <div field="CREATE_TIME_" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" headerAlign="center"
                 allowSort="true"><spring:message code="page.scxxfkList.name14" />
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
    var scxxfkListGrid = mini.get("scxxfkListGrid");

    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var applyId = record.id;
        var instId = record.instId;
        var status = record.status;
        var s = '';
        if (status != 'DRAFTED') {
            s += '<span  title=' + scxxfkList_name + ' onclick="scxxfkDetail(\'' + applyId + '\',\'' + instId + '\')">' + scxxfkList_name + '</span>';
        }
        if (record.status == 'DRAFTED') {
            s += '<span  title=' + scxxfkList_name1 + ' onclick="scxxfkEdit(\'' + applyId + '\',\'' + instId + '\')">' + scxxfkList_name1 + '</span>';
        }

        if (status == 'RUNNING' && record.myTaskId) {
            s += '<span  title=' + scxxfkList_name2 + ' onclick="scxxfkTask(\'' + record.myTaskId + '\')">' + scxxfkList_name2 + '</span>';
        }

        if (status == 'DRAFTED' || currentUserNo == 'admin') {
            s += '<span  title=' + scxxfkList_name3 + ' onclick="removeScxxfk(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">' + scxxfkList_name3 + '</span>';
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


    function addScxxfk() {
        if (isFbr == "false" && currentUserId != "1") {
            mini.alert(scxxfkList_name4);
            return;
        }
        var url = jsUseCtxPath + "/bpm/core/bpmInst/SCXXFK/start.do";
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (scxxfkListGrid) {
                    scxxfkListGrid.reload()
                }
            }
        }, 1000);
    }

    function scxxfkEdit(applyId, instId) {
        var url = jsUseCtxPath + "/bpm/core/bpmInst/start.do?instId=" + instId + "&applyId=" + applyId;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (scxxfkListGrid) {
                    scxxfkListGrid.reload()
                }
            }
        }, 1000);
    }


    function scxxfkDetail(applyId) {
        var action = "detail";
        var url = jsUseCtxPath + "/serviceEngineering/core/scxxfk/applyEditPage.do?action=detail&applyId=" + applyId;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (scxxfkListGrid) {
                    scxxfkListGrid.reload()
                }
            }
        }, 1000);
    }

    function scxxfkTask(taskId) {
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
                            if (scxxfkListGrid) {
                                scxxfkListGrid.reload();
                            }
                        }
                    }, 1000);
                }
            }
        })
    }

    function removeScxxfk(record) {
        var rows = [];
        if (record) {
            rows.push(record);
        } else {
            rows = scxxfkListGrid.getSelecteds();
        }
        if (rows.length <= 0) {
            mini.alert(scxxfkList_name5);
            return;
        }
        mini.confirm(scxxfkList_name6, scxxfkList_name7, function (action) {
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
                    alert(scxxfkList_name8);
                }
                if (rowIds.length > 0) {
                    _SubmitJson({
                        url: jsUseCtxPath + "/serviceEngineering/core/scxxfk/deleteApply.do",
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
<redxun:gridScript gridId="scxxfkListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>

