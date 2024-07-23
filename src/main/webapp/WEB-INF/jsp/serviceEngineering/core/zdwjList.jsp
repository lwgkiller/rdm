<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>再制造指导文件清单</title>
    <%@include file="/commons/list.jsp" %>

</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">申请单号: </span>
                    <input class="mini-textbox" id="applyNumber" name="applyNumber"/>
                    <
                    <span class="text" style="width:auto">零部件类型: </span>
                    <input id="partsType" name="partsType" class="mini-combobox" style="width:98%;"
                           textField="value" valueField="key" emptyText="请选择..."
                           required="false" allowInput="false" showNullItem="true"
                           nullItemText="请选择..."
                           data="[{'key' : '发动机','value' : '发动机'}
                                       ,{'key' : '主泵','value' : '主泵'}
                                       ,{'key' : '液压马达','value' : '液压马达'}
                                       ,{'key' : '油缸','value' : '油缸'}
                                       ,{'key' : '阀','value' : '阀'}
                                       ,{'key' : '电气件','value' : '电气件'}]"
                    />
                    <span class="text" style="width:auto">文件类型: </span>
                    <input id="fileType" name="fileType" class="mini-combobox" style="width:98%;"
                           textField="value" valueField="key" emptyText="请选择..."
                           required="false" allowInput="false" showNullItem="true"
                           nullItemText="请选择..." onvaluechanged="setApply()"
                           data="[{'key' : '拆解作业指导书','value' : '拆解作业指导书'}
                                       ,{'key' : '装配作业指导书','value' : '装配作业指导书'}
                                       ,{'key' : '清洗作业规范','value' : '清洗作业规范'}
                                       ,{'key' : '涂装作业规范','value' : '涂装作业规范'}
                                       ,{'key' : '试验作业规范','value' : '试验作业规范'}]"
                    />

                    <span class="text" style="width:auto">零部件型号: </span>
                    <input class="mini-textbox" id="partsModel" name="partsModel"/>
                    <span class="text" style="width:auto">申请人: </span>
                    <input class="mini-textbox" id="creatorName" name="creatorName"/>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <div style="display: inline-block" class="separator"></div>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="addZdwj()">新增</a>
                    <div style="display: inline-block" class="separator"></div>
                    <a id="downloadBtn" class="mini-button" style="margin-right: 5px"
                       onclick="clickDownload">下载</a>
                </li>
            </ul>
        </form>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="zdwjListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         onlyCheckSelection="true"
         url="${ctxPath}/serviceEngineering/core/zdwj/applyList.do" idField="id"
         multiSelect="false" showColumnsMenu="true" sizeList="[20,50,100]" pageSize="20" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div type="indexcolumn" width="28" headerAlign="center" align="center">序号</div>
            <div name="action" cellCls="actionIcons" width="80" headerAlign="center" align="center"
                 renderer="onActionRenderer" cellStyle="padding:0;">操作
            </div>
            <div field="applyNumber" headerAlign="center" align="center" allowSort="false">申请单号</div>
            <div field="creatorName" headerAlign="center" align="center" allowSort="false">申请人</div>
            <div field="partsType" headerAlign="center" align="center" allowSort="false">零部件类型</div>
            <div field="partsModel" headerAlign="center" align="center" allowSort="false">零部件型号</div>
            <div field="fileType" headerAlign="center" align="center" allowSort="false">文件类型</div>
            <div field="fileName" headerAlign="center" align="center" allowSort="false">文件名称</div>
            <div field="taskName" headerAlign='center' align='center' width="60">当前任务</div>
            <div field="allTaskUserNames" headerAlign='center' align='center' width="40">当前处理人</div>
            <div field="status" width="50" headerAlign="center" align="center" allowSort="true"
                 renderer="onStatusRenderer">状态
            </div>
            <div field="CREATE_TIME_" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" headerAlign="center"
                 allowSort="true">创建时间
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var currentUserNo = "${currentUserNo}";
    var currentUserId = "${currentUserId}";
    var zdwjListGrid = mini.get("zdwjListGrid");

    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var applyId = record.id;
        var instId = record.instId;
        var status = record.status;
        var s = '';
        if (status != 'DRAFTED') {
            s += '<span  title="明细" onclick="zdwjDetail(\'' + applyId + '\',\'' + instId + '\')">明细</span>';
        }
        if (record.status == 'DRAFTED') {
            s += '<span  title="编辑" onclick="zdwjEdit(\'' + applyId + '\',\'' + instId + '\')">编辑</span>';
        }

        if (status == 'RUNNING' && record.myTaskId) {
            s += '<span  title="办理" onclick="zdwjTask(\'' + record.myTaskId + '\')">办理</span>';
        }

        if (status == 'DRAFTED' || currentUserNo == 'admin') {
            s += '<span  title="删除" onclick="removeZdwj(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
        }
        if (status == "SUCCESS_END" && currentUserId == record.CREATE_BY_) {
            s += '<span  title="作废" onclick="discardInst(\'' + record.instId + '\')">作废</span>';
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


    function addZdwj() {
        // if (isFbr == "false" && currentUserId != "1") {
        //     mini.alert("只有通信协议更改发布人才可以新建");
        //     return;
        // }
        var url = jsUseCtxPath + "/bpm/core/bpmInst/ZYZDWJ/start.do";
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (zdwjListGrid) {
                    zdwjListGrid.reload()
                }
            }
        }, 1000);
    }

    function zdwjEdit(applyId, instId) {
        var url = jsUseCtxPath + "/bpm/core/bpmInst/start.do?instId=" + instId + "&applyId=" + applyId;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (zdwjListGrid) {
                    zdwjListGrid.reload()
                }
            }
        }, 1000);
    }


    function zdwjDetail(applyId) {
        var action = "detail";
        var url = jsUseCtxPath + "/serviceEngineering/core/zdwj/applyEditPage.do?action=detail&applyId=" + applyId;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (zdwjListGrid) {
                    zdwjListGrid.reload()
                }
            }
        }, 1000);
    }

    function zdwjTask(taskId) {
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
                            if (zdwjListGrid) {
                                zdwjListGrid.reload();
                            }
                        }
                    }, 1000);
                }
            }
        })
    }

    function removeZdwj(record) {
        var rows = [];
        if (record) {
            rows.push(record);
        } else {
            rows = zdwjListGrid.getSelecteds();
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
                var existStartInst = false;
                for (var i = 0, l = rows.length; i < l; i++) {
                    var r = rows[i];
                    // if (r.status == 'DRAFTED' && r.CREATE_BY_ == currentUserId) {
                    if ((r.status == 'DRAFTED' || r.status == 'DISCARD_END') && r.CREATE_BY_ == currentUserId) {
                        rowIds.push(r.id);
                        instIds.push(r.instId);
                    }
                    else {
                        existStartInst = true;
                        continue;
                    }
                }
                if (existStartInst) {
                    alert("仅草稿及作废状态数据可由本人删除");
                }
                if (rowIds.length > 0) {
                    _SubmitJson({
                        url: jsUseCtxPath + "/serviceEngineering/core/zdwj/deleteApply.do",
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


    function clickDownload() {
        // 下载各类型文件
        debugger;
        var record = zdwjListGrid.getSelected();
        var fileName = record.fileName;
        if (!fileName) {
            mini.alert("此记录暂无文件！");
            return;
        }
        var id = record.id;
        download(id, fileName);

    }

    function download(id, description) {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + "/serviceEngineering/core/zdwj/Download.do");
        var idAttr = $("<input>");
        idAttr.attr("type", "hidden");
        idAttr.attr("name", "id");
        idAttr.attr("value", id);
        var descriptionAttr = $("<input>");
        descriptionAttr.attr("type", "hidden");
        descriptionAttr.attr("name", "description");
        descriptionAttr.attr("value", description);
        $("body").append(form);
        form.append(idAttr);
        form.append(descriptionAttr);
        form.submit();
        form.remove();
    }


    function discardInst(instId) {

        if (!confirm('流程终止后不可恢复，确定要作废该流程吗?')) {
            return;
        }
        _SubmitJson({
            url: __rootPath + '/bpm/core/bpmInst/discardInst.do',
            data: {
                instId: instId
            },
            method: 'POST',
            success: function () {
                searchFrm();
            }
        })
    }


</script>
<redxun:gridScript gridId="zdwjListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>

