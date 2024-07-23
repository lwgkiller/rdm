<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>海外技术资料传递列表</title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<div class="mini-toolbar" >
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul >
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.zlcdList.wjnr" />: </span>
                    <input class="mini-textbox" id="content" name="content" />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.zlcdList.sqjg" />: </span>
                    <input class="mini-textbox" id="deptName" name="deptName" />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.zlcdList.sqr" />: </span>
                    <input class="mini-textbox" id="applyName" name="applyName" />
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px"  plain="true" onclick="searchFrm()"><spring:message code="page.zlcdList.cx" /></a>
                    <a class="mini-button btn-red" style="margin-right: 5px"  plain="true" onclick="clearForm()"><spring:message code="page.zlcdList.qkcx" /></a>
                    <div style="display: inline-block" class="separator"></div>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="addZlcd()"><spring:message code="page.zlcdList.xz" /></a>
                </li>
            </ul>
        </form>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="zlcdListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onlyCheckSelection="true"
         url="${ctxPath}/rdm/core/Zlcd/queryZlcd.do" idField="id"
         multiSelect="true" showColumnsMenu="true" sizeList="[20,50,100]" pageSize="20" allowAlternating="true" pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div type="indexcolumn" width="28" headerAlign="center" align="center"><spring:message code="page.zlcdList.xh" /></div>
            <div name="action" cellCls="actionIcons" width="40" headerAlign="center" align="center" renderer="onActionRenderer" cellStyle="padding:0;"><spring:message code="page.zlcdList.cz" /></div>
            <div field="applyName" headerAlign="center" align="center" allowSort="false" width="40"><spring:message code="page.zlcdList.sqjg" /></div>
            <div field="deptName" headerAlign="center" align="center" allowSort="false" width="50" ><spring:message code="page.zlcdList.sqr" /></div>
            <div field="taskName" headerAlign='center' align='center' width="40"><spring:message code="page.zlcdList.dqrw" /></div>
            <div field="allTaskUserNames" headerAlign='center' align='center' width="50"><spring:message code="page.zlcdList.dqclr" /></div>
            <div field="status" width="25" headerAlign="center" align="center" allowSort="true"
                 renderer="onStatusRenderer"><spring:message code="page.zlcdList.zt" />
            </div>
            <div field="CREATE_TIME_" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" width="50" headerAlign="center" allowSort="true"><spring:message code="page.zlcdList.sqsj" /></div>
        </div>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var currentUserNo="${currentUserNo}";
    var currentUserId="${currentUserId}";
    var zlcdId = "${zlcdId}";
    var zlcdListGrid=mini.get("zlcdListGrid");

    function onActionRenderer(e) {
        var record = e.record;
        var zlcdId = record.zlcdId;
        var instId = record.instId;
        var status = record.status;
        var s = '';
        if (status != 'DRAFTED'){
            s += '<span  title=' + zlcdList_ms + ' onclick="zlcdDetail(\'' + zlcdId + '\',\'' + instId + '\')"><spring:message code="page.zlcdList.ms" /></span>';
        }
        if (record.status == 'DRAFTED') {
            s += '<span  title=' + zlcdList_bj + ' onclick="zlcdEdit(\'' + zlcdId + '\',\'' + instId + '\')"><spring:message code="page.zlcdList.bj" /></span>';
        }
        if(record.status =='RUNNING'){
            var currentProcessUserId=record.currentProcessUserId;
            if(record.myTaskId) {
                s+='<span  title=' + zlcdList_bl + ' onclick="zlcdTask(\'' + record.myTaskId + '\')"><spring:message code="page.zlcdList.bl" /></span>';
            }
        }
        if ((status == 'DRAFTED' || status == 'DISCARD_END')&&currentUserNo=='admin') {
            s += '<span  title=' + zlcdList_sc + ' onclick="removeZlcd(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')"><spring:message code="page.zlcdList.sc" /></span>';
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


    function addZlcd() {
        var url = jsUseCtxPath + "/bpm/core/bpmInst/ZLCD/start.do";
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (zlcdListGrid) {
                    zlcdListGrid.reload()
                }
            }
        }, 1000);
    }

    function zlcdEdit(zlcdId,instId,status) {
        var url = jsUseCtxPath + "/bpm/core/bpmInst/start.do?instId=" + instId+ "&zlcdId=" + zlcdId+"&status="+status;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (zlcdListGrid) {
                    zlcdListGrid.reload()
                }
            }
        }, 1000);
    }

    function zlcdDetail(zlcdId) {
        var action = "detail";
        var url = jsUseCtxPath + "/rdm/core/Zlcd/editPage.do?action=" + action + "&zlcdId=" + zlcdId;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (zlcdListGrid) {
                    zlcdListGrid.reload()
                }
            }
        }, 1000);
    }
    function zlcdTask(taskId) {
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
                            if (zlcdListGrid) {
                                zlcdListGrid.reload();
                            }
                        }
                    }, 1000);
                }
            }
        })
    }

    function removeZlcd(record) {
        var rows = [];
        if (record) {
            rows.push(record);
        } else {
            rows = zlcdListGrid.getSelecteds();
        }
        if (rows.length <= 0) {
            mini.alert(zlcdList_qxz);
            return;
        }
        mini.confirm(zlcdList_qdsc, zlcdList_ts, function (action) {
            if (action != 'ok') {
                return;
            }else {
                var rowIds = [];
                for (var i = 0, l = rows.length; i < l; i++) {
                    var r = rows[i];
                    rowIds.push(r.zlcdId);
                }
                if (rowIds.length > 0) {
                    _SubmitJson({
                        url: jsUseCtxPath + "/rdm/core/Zlcd/deleteZlcd.do",
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

</script>
<redxun:gridScript gridId="zlcdListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl="" />
</body>
</html>

