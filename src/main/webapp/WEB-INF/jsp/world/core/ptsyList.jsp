<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>海外新开发配套实验列表</title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<div class="mini-toolbar" >
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul >
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.ptsyList.btcj" />: </span>
                    <input class="mini-textbox" id="company" name="company" />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.ptsyList.btjmc" />: </span>
                    <input class="mini-textbox" id="ptName" name="ptName" />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.ptsyList.syjx" />: </span>
                    <input class="mini-textbox" id="syType" name="syType" />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.ptsyList.sqr" />: </span>
                    <input class="mini-textbox" id="userName" name="userName" />
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px"  plain="true" onclick="searchFrm()"><spring:message code="page.ptsyList.cx" /></a>
                    <a class="mini-button btn-red" style="margin-right: 5px"  plain="true" onclick="clearForm()"><spring:message code="page.ptsyList.qkcx" /></a>
                    <div style="display: inline-block" class="separator"></div>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="addPtsy()"><spring:message code="page.ptsyList.xz" /></a>
                </li>
            </ul>
        </form>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="ptsyListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onlyCheckSelection="true"
         url="${ctxPath}/rdm/core/ptsy/queryPtsy.do" idField="id"
         multiSelect="true" showColumnsMenu="true" sizeList="[20,50,100]" pageSize="20" allowAlternating="true" pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div type="indexcolumn" width="28" headerAlign="center" align="center"><spring:message code="page.ptsyList.xh" /></div>
            <div name="action" cellCls="actionIcons" width="80" headerAlign="center" align="center" renderer="onActionRenderer" cellStyle="padding:0;"><spring:message code="page.ptsyList.cz" /></div>
            <div field="company" headerAlign="center" align="center" allowSort="false"><spring:message code="page.ptsyList.btcj" /></div>
            <div field="ptName" headerAlign="center" align="center" allowSort="false"><spring:message code="page.ptsyList.btjmc" /></div>
            <div field="syType" headerAlign="center" align="center" allowSort="false" width="50" ><spring:message code="page.ptsyList.syjx" /></div>
            <div field="link" headerAlign="center" align="center" allowSort="false" width="50"><spring:message code="page.ptsyList.lxr" /></div>
            <div field="timeNode" dateFormat="yyyy-MM-dd HH:mm:ss" width="50" headerAlign="center" align="center" allowSort="false"><spring:message code="page.ptsyList.sjjd" /></div>
            <div field="userName" headerAlign="center" align="center" allowSort="false" width="40"><spring:message code="page.ptsyList.sqr" /></div>
            <div field="deptName" headerAlign="center" align="center" allowSort="false" width="50" ><spring:message code="page.ptsyList.sqjg" /></div>
            <div field="taskName" headerAlign='center' align='center' width="40"><spring:message code="page.ptsyList.dqrw" /></div>
            <div field="allTaskUserNames" headerAlign='center' align='center' width="50"><spring:message code="page.ptsyList.dqclr" /></div>
            <div field="status" width="25" headerAlign="center" align="center" allowSort="true"
                 renderer="onStatusRenderer"><spring:message code="page.ptsyList.zt" />
            </div>
            <div field="CREATE_TIME_" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" width="50" headerAlign="center" allowSort="true"><spring:message code="page.ptsyList.sqsj" /></div>
        </div>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var currentUserNo="${currentUserNo}";
    var currentUserId="${currentUserId}";
    var ptsyId = "${ptsyId}";
    var ptsyListGrid=mini.get("ptsyListGrid");

    function onActionRenderer(e) {
        var record = e.record;
        var ptsyId = record.ptsyId;
        var instId = record.instId;
        var status = record.status;
        var s = '';
        if (status != 'DRAFTED'){
            s += '<span  title=' + ptsyList_mx + ' onclick="ptsyDetail(\'' + ptsyId + '\',\'' + instId + '\')"><spring:message code="page.ptsyList.mx" /></span>';
        }
        if (record.status == 'DRAFTED') {
            s += '<span  title=' + ptsyList_bj + ' onclick="ptsyEdit(\'' + ptsyId + '\',\'' + instId + '\')"><spring:message code="page.ptsyList.bj" /></span>';
        }
        if(record.status =='RUNNING'){
            var currentProcessUserId=record.currentProcessUserId;
            if(record.myTaskId) {
                s+='<span  title=' + ptsyList_bl + ' onclick="ptsyTask(\'' + record.myTaskId + '\')"><spring:message code="page.ptsyList.bl" /></span>';
            }
        }
        if (status == 'DRAFTED' || currentUserNo=='admin') {
            s += '<span  title=' + ptsyList_schu + ' onclick="removePtsy(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')"><spring:message code="page.ptsyList.sc" /></span>';
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


    function addPtsy() {
        var url = jsUseCtxPath + "/bpm/core/bpmInst/PTSY/start.do";
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (ptsyListGrid) {
                    ptsyListGrid.reload()
                }
            }
        }, 1000);
    }

    function ptsyEdit(ptsyId,instId,status) {
        var url = jsUseCtxPath + "/bpm/core/bpmInst/start.do?instId=" + instId+ "&ptsyId=" + ptsyId+"&status="+status;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (ptsyListGrid) {
                    ptsyListGrid.reload()
                }
            }
        }, 1000);
    }

    function ptsyDetail(ptsyId) {
        var action = "detail";
        var url = jsUseCtxPath + "/rdm/core/ptsy/editPage.do?action=" + action + "&ptsyId=" + ptsyId;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (ptsyListGrid) {
                    ptsyListGrid.reload()
                }
            }
        }, 1000);
    }
    function ptsyTask(taskId) {
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
                            if (ptsyListGrid) {
                                ptsyListGrid.reload();
                            }
                        }
                    }, 1000);
                }
            }
        })
    }

    function removePtsy(record) {
        var rows = [];
        if (record) {
            rows.push(record);
        } else {
            rows = ptsyListGrid.getSelecteds();
        }
        if (rows.length <= 0) {
            mini.alert(ptsyList_bgjszt);
            return;
        }
        mini.confirm(ptsyList_qdbg, ptsyList_ts, function (action) {
            if (action != 'ok') {
                return;
            }else {
                var rowIds = [];
                for (var i = 0, l = rows.length; i < l; i++) {
                    var r = rows[i];
                    rowIds.push(r.ptsyId);
                }
                if (rowIds.length > 0) {
                    _SubmitJson({
                        url: jsUseCtxPath + "/rdm/core/ptsy/deletePtsy.do",
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
<redxun:gridScript gridId="ptsyListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl="" />
</body>
</html>

