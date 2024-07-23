<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>语料审核列表</title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">翻译申请单号: </span>
                    <input class="mini-textbox" id="transApplyId" name="transApplyId"/>
                    <span class="text" style="width:auto">销售型号: </span>
                    <input class="mini-textbox" id="saleType" name="saleType"/>
                    <span class="text" style="width:auto">手册类型: </span>
                    <input id="manualType" name="manualType" class="mini-combobox" style="width:98%;" enabled="true"
                           textField="value" valueField="key" emptyText="请选择..."
                           allowInput="false" showNullItem="true" nullItemText="请选择..."
                           data="[{'key' : '操保手册','value' : '操保手册'},
							   {'key' : '装修手册','value' : '装修手册'},
							   {'key' : '装箱单','value' : '装箱单'},
							   {'key' : '零件图册','value' : '零件图册'},
							   {'key' : '其他','value' : '其他'}]"/>
                    <span class="text" style="width:auto">手册编码: </span>
                    <input class="mini-textbox" id="manualCode" name="manualCode"/>
                    <span class="text" style="width:auto">源手册语言: </span>
                    <input class="mini-textbox" id="sourceManualLan" name="sourceManualLan"/>
                    <span class="text" style="width:auto">翻译语言: </span>
                    <input class="mini-textbox" id="targetManualLan" name="targetManualLan"/>
                    <span class="text" style="width:auto">申请人: </span>
                    <input class="mini-textbox" id="creatorName" name="creatorName"/>
                <li style="margin-right: 15px"><span class="text" style="width:auto">流程状态: </span>
                    <input id="instStatus" name="instStatus" class="mini-combobox" style="width:120px;"
                           textField="value" valueField="key" emptyText="请选择..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                           data="[ {'key' : 'DRAFTED','value' : '草稿'},
                           {'key' : 'RUNNING','value' : '审批中'},
                           {'key' : 'SUCCESS_END','value' : '批准'}]"/>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <div style="display: inline-block" class="separator"></div>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="addYlsh()">新增</a>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="exportList()">导出</a>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="exportFile()">下载</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="removeYlsh()">删除</a>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="upgradeYlsh()">升版</a>
                </li>
            </ul>
        </form>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="ylshListGrid" class="mini-datagrid" style="width: 2000px; height: 100%;" allowResize="false"
         onlyCheckSelection="true"
         url="${ctxPath}/serviceEngineering/core/ylsh/applyList.do" idField="id"
         multiSelect="true" showColumnsMenu="true" sizeList="[20,50,100]" pageSize="20" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div field="resFileId" visible="false" headerAlign="center" align="center" allowSort="false">已审文件id</div>
            <div field="resFileName" visible="false" headerAlign="center" align="center" allowSort="false">已审文件名</div>
            <div type="indexcolumn" width="28" headerAlign="center" align="center">序号</div>
            <div name="action" cellCls="actionIcons" width="80" headerAlign="center" align="center"
                 renderer="onActionRenderer" cellStyle="padding:0;">操作
            </div>
            <div field="transApplyId" headerAlign="center" align="center" allowSort="true">翻译单号</div>
            <div field="aimFileName" headerAlign="center" align="center" allowSort="true">文件名</div>
            <div field="saleType" headerAlign="center" align="center" allowSort="true">销售型号</div>
            <div field="manualType" headerAlign="center" align="center" allowSort="true">手册类型</div>
            <div field="manualCode" headerAlign="center" align="center" allowSort="true">手册编码</div>
            <div field="sourceManualLan" headerAlign="center" align="center" allowSort="true">源语言</div>
            <div field="targetManualLan" headerAlign="center" align="center" allowSort="true">翻译语言</div>
            <div field="version" headerAlign="center" align="center" allowSort="true">版本</div>
            <div field="wordsNum" headerAlign="center" align="center" allowSort="false">词条数</div>
            <div field="creatorName" headerAlign="center" align="center" width="40" allowSort="false">申请人</div>
            <div field="currentProcessTask" headerAlign='center' align='center' width="60">当前任务</div>
            <div field="currentProcessUser" sortField="currentProcessUser" width="100" align="center" headerAlign="center"
                 allowSort="false" renderer="render">当前处理人
            </div>
            <div field="status" width="50" headerAlign="center" align="center" allowSort="true"
                 renderer="onStatusRenderer">状态
            </div>
            <div field="CREATE_TIME_" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" headerAlign="center"
                 allowSort="true">创建时间
            </div>
        </div>
    </div>
</div>

<form id="excelForm" action="${ctxPath}/serviceEngineering/core/ylsh/exportData.do" method="post" target="excelIFrame">
    <input type="hidden" name="filter" id="filter"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var currentUserNo = "${currentUserNo}";
    var currentUserId = "${currentUserId}";
    var ylshListGrid = mini.get("ylshListGrid");
    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var applyId = record.id;
        var instId = record.instId;
        var status = record.status;
        var s = '';
        if (status != 'DRAFTED') {
            s += '<span  title="明细" onclick="ylshDetail(\'' + applyId + '\',\'' + instId + '\')">明细</span>';
        }
        if (record.status == 'DRAFTED') {
            s += '<span  title="编辑" onclick="ylshEdit(\'' + applyId + '\',\'' + instId + '\')">编辑</span>';
        }
        var currentProcessUserId = record.currentProcessUserId;
        if (status == 'RUNNING' && ((currentProcessUserId && currentProcessUserId.indexOf(currentUserId) != -1) || currentUserNo == 'admin')) {
            s += '<span  title="办理" onclick="ylshTask(\'' + record.taskId + '\')">办理</span>';
        }

        if (status == 'DRAFTED' || currentUserNo == 'admin') {
            s += '<span  title="删除" onclick="removeYlsh(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
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

    function addYlsh() {

        var url = jsUseCtxPath + "/bpm/core/bpmInst/FYYLKSXGD/start.do";
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (ylshListGrid) {
                    ylshListGrid.reload()
                }
            }
        }, 1000);
    }

    function ylshEdit(applyId, instId) {
        var url = jsUseCtxPath + "/bpm/core/bpmInst/start.do?instId=" + instId + "&applyId=" + applyId;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (ylshListGrid) {
                    ylshListGrid.reload()
                }
            }
        }, 1000);
    }

    function ylshDetail(applyId) {
        var action = "detail";
        var url = jsUseCtxPath + "/serviceEngineering/core/ylsh/applyEditPage.do?action=detail&applyId=" + applyId;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (ylshListGrid) {
                    ylshListGrid.reload()
                }
            }
        }, 1000);
    }

    function ylshTask(taskId) {
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
                            if (ylshListGrid) {
                                ylshListGrid.reload();
                            }
                        }
                    }, 1000);
                }
            }
        })
    }

    function removeYlsh(record) {
        var rows = [];
        if (record) {
            rows.push(record);
        } else {
            rows = ylshListGrid.getSelecteds();
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
                var aimFileIds = [];
                var aimFileNames = [];
                var resFileIds = [];
                var resFileNames = [];
                var existStartInst = false;
                for (var i = 0, l = rows.length; i < l; i++) {
                    var r = rows[i];
                    if ((r.status == 'DRAFTED' && r.CREATE_BY_ == currentUserId) || currentUserNo == 'admin') {
                        rowIds.push(r.id);
                        instIds.push(r.instId);
                        aimFileIds.push(r.aimFileId);
                        aimFileNames.push(r.aimFileName);
                        resFileIds.push(r.resFileId);
                        resFileNames.push(r.resFileName);
                    }
                    else {
                        existStartInst = true;
                        continue;
                    }
                }
                if (existStartInst) {
                    alert("仅草稿状态数据可由本人删除");
                    return;
                }
                if (rowIds.length > 0) {
                    _SubmitJson({
                        url: jsUseCtxPath + "/serviceEngineering/core/ylsh/deleteApply.do",
                        method: 'POST',
                        showMsg: false,
                        data: {
                            ids: rowIds.join(',')
                            , instIds: instIds.join(',')
                            , aimFileIds: aimFileIds.join(',')
                            , aimFileNames: aimFileNames.join(',')
                            , resFileIds: resFileIds.join(',')
                            , resFileNames: resFileNames.join(',')
                        },
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

    function exportList() {
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

    function exportFile() {
        var rows = ylshListGrid.getSelecteds();
        var downloadUrl = '/serviceEngineering/core/ylsh/fileDownload.do';
        if (rows.length <= 0) {
            mini.alert("请至少选中一条记录");
            return;
        }
        var fileData = [];
        var obj = {};
        var cnt = 0;
        var resFileIds = "";
        var resFileNames = "";
        var ids = "";
        for (var i = 0, l = rows.length; i < l; i++) {
            var r = rows[i];
            if (r.resFileId && r.status == "SUCCESS_END") {
                fileData.push({"resFileName": r.resFileName, "resFileId": r.resFileId, "id": r.id});
                // 用form的方式去提交
                if (cnt > 0) {
                    ids += ',';
                    resFileIds += ",";
                    resFileNames += ",";
                }
                ids += r.id;
                resFileIds += r.resFileId;
                resFileNames += r.resFileName;
                cnt = cnt + 1;
            }

        }
        if (cnt == 0) {
            mini.alert("请选择已审核单据进行下载！");
            return;
        } else if (cnt == 1) {
            downLoadFile(r.resFileName, r.resFileId, r.id, downloadUrl);
        } else {
            url = "/serviceEngineering/core/ylsh/zipFileDownload.do";
            downLoadFile(resFileNames, resFileIds, ids, url);
        }
    }

    function downLoadFile(fileName, fileId, formId, urlValue) {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + urlValue);
        var inputFileName = $("<input>");
        inputFileName.attr("type", "hidden");
        inputFileName.attr("name", "fileName");
        inputFileName.attr("value", fileName);
        var inputFileId = $("<input>");
        inputFileId.attr("type", "hidden");
        inputFileId.attr("name", "fileId");
        inputFileId.attr("value", fileId);
        var inputFormId = $("<input>");
        inputFormId.attr("type", "hidden");
        inputFormId.attr("name", "formId");
        inputFormId.attr("value", formId);
        $("body").append(form);
        form.append(inputFileName);
        form.append(inputFileId);
        form.append(inputFormId);
        form.submit();
        form.remove();
    }
    //..
    function upgradeYlsh() {
        var row = ylshListGrid.getSelected();
        if (row == null) {
            mini.alert("请选中一条记录");
            return;
        }
        _SubmitJson({
            url: jsUseCtxPath + "/serviceEngineering/core/ylsh/copyBusiness.do",
            method: 'POST',
            showMsg: false,
            data: {businessId: row.id},
            success: function (data) {
                if (data) {
                    searchFrm();
                }
            }
        });
    }
</script>
<redxun:gridScript gridId="ylshListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>

