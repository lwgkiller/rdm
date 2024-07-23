<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title></title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">翻译申请单号：</span>
                    <input class="mini-textbox" id="transApplyId" name="transApplyId"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">手册类型：</span>
                    <input id="manualType" name="manualType" class="mini-combobox" style="width:98%;" enabled="true"
                           textField="value" valueField="key" emptyText="请选择..."
                           allowInput="false" showNullItem="true" nullItemText="请选择..."
                           url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=JSZLFYSQWJLX"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">销售型号：</span>
                    <input class="mini-textbox" id="saleType" name="saleType"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">设计型号：</span>
                    <input class="mini-textbox" id="designType" name="designType"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">物料编码：</span>
                    <input class="mini-textbox" id="materialCode" name="materialCode"/>
                </li>

                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">手册编码：</span>
                    <input class="mini-textbox" id="manualCode" name="manualCode"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">源手册语言：</span>
                    <input class="mini-textbox" id="sourceManualLan" name="sourceManualLan"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">翻译语言：</span>
                    <input class="mini-textbox" id="targetManualLan" name="targetManualLan"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">翻译人：</span>
                    <input class="mini-textbox" id="translator" name="translator"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">流程节点: </span>
                    <input id="currentProcessTask" name="currentProcessTask" class="mini-combobox" style="width:180px;" multiSelect="false"
                           textField="value" valueField="key" emptyText="请选择..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                           data="[ {'key' : '编辑中','value' : '编辑中'},
                                    {'key' : '翻译组审核','value' : '翻译组审核'},
                                    {'key' : '申请部门负责人审核','value' : '申请部门负责人审核'},
                                    {'key' : '服务工程所长审核','value' : '服务工程所长审核'},
                                    {'key' : '文件翻译&归档','value' : '文件翻译&归档'},
                                    {'key' : '分管领导批准','value' : '分管领导批准'},
                                    {'key' : '归档审核','value' : '归档审核'}
                                 ]"
                    />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">运行状态: </span>
                    <input id="status" name="status" class="mini-combobox" style="width:120px;" multiSelect="false"
                           textField="value" valueField="key" emptyText="请选择..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                           data="[ {'key' : 'DRAFTED','value' : '草稿'},{'key' : 'RUNNING','value' : '审批中'},{'key' : 'SUCCESS_END','value' : '批准'},{'key' : 'DISCARD_END','value' : '作废'}]"
                    />
                </li>
                <li style="margin-left: 10px">
                    <f:a alias="attacheddoctrans-searchFrm" onclick="searchFrm()" showNoRight="false" style="margin-right: 5px">查询</f:a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <f:a alias="attacheddoctrans-removeBusiness" onclick="removeBusiness()" showNoRight="false"
                         style="margin-right: 5px">删除</f:a>
                    <f:a alias="attacheddoctrans-addBusiness" onclick="addBusiness()" showNoRight="false"
                         style="margin-right: 5px">新增</f:a>
                    <f:a alias="attacheddoctrans-exportList" onclick="exportList()" showNoRight="false"
                         style="margin-right: 5px">导出</f:a>
                    <f:a alias="attacheddoctrans-exportFile" onclick="exportFile()" showNoRight="false"
                         style="margin-right: 5px">批量下载文件</f:a>
                </li>
            </ul>
        </form>
        <span class="searchSelectionBtn" onclick="no_search(this ,'searchForm')">
				<i class="icon-sc-lower"></i>
        </span>
    </div>
</div>

<div class="mini-fit" style="height: 100%;">
    <div id="businessListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         allowCellWrap="true"
         showCellTip="true" idField="id" allowCellEdit="true" allowCellSelect="true" oncellvalidation="onCellValidation"
         multiSelect="true" showColumnsMenu="false" sizeList="[20,50,100]" pageSize="20" allowAlternating="true"
         pagerButtons="#pagerButtons"
         url="${ctxPath}/serviceEngineering/core/attachedDocTranslate/dataListQuery.do">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div name="action" cellCls="actionIcons" width="150" headerAlign="center" align="center"
                 renderer="onActionRenderer"
                 cellStyle="padding:0;">操作
            </div>
            <div field="transApplyId" width="150" headerAlign="center" align="center" allowSort="true" renderer="render">翻译申请单号</div>
            <div field="manualType" width="130" headerAlign="center" align="center" allowSort="true" renderer="render">手册类型</div>
            <div field="saleType" width="130" headerAlign="center" align="center" allowSort="true" renderer="render">销售型号</div>
            <div field="designType" width="130" headerAlign="center" align="center" allowSort="true" renderer="render">设计型号</div>
            <div field="materialCode" width="130" headerAlign="center" align="center" allowSort="true" renderer="render">物料编码</div>
            <div field="manualCode" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">手册编码</div>
            <div field="manualVersion" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">手册版本</div>
            <div field="sourceManualLan" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">源手册语言</div>
            <div field="targetManualLan" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">翻译语言</div>
            <%--            <div field="mannulCE" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">是否CE版手册</div>--%>
            <div field="translator" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">翻译人</div>
            <div field="applyTime" width="100" headerAlign="center" align="center" allowSort="true" renderer="render" dateFormat="yyyy-MM-dd">申请时间
            </div>
            <div field="needTime" width="100" headerAlign="center" align="center" allowSort="true" renderer="render" dateFormat="yyyy-MM-dd">需求时间
            </div>
            <div field="endTime" width="150" headerAlign="center" align="center" allowSort="true"
                 dateFormat="yyyy-MM-dd HH:mm:ss">流程结束时间
            </div>
            <div field="creatorName" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">申请人</div>
            <div field="applyDep" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">申请部门</div>
            <div field="status" width="90" headerAlign="center" align="center"
                 renderer="onStatusRenderer">当前运行状态
            </div>
            <div field="currentProcessUser" width="100" headerAlign="center" align="center" allowSort="false" renderer="render">当前处理人</div>
            <div field="currentProcessTask" width="200" headerAlign="center" align="center" allowSort="true" renderer="render">当前流程节点</div>
        </div>
    </div>
</div>
<form id="excelForm" action="${ctxPath}/serviceEngineering/core/attachedDocTranslate/exportData.do" method="post" target="excelIFrame">
    <input type="hidden" name="filter" id="filter"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var businessListGrid = mini.get("businessListGrid");
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var currentUserName = "${currentUserName}";
    var currentTime = new Date().format("yyyy-MM-dd");
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";

    //..行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var businessId = record.id;
        var instId = record.instId;
        var status = record.status;
        var s = '';
        s += '<span  title="明细" onclick="businessDetail(\'' + businessId + '\',\'' + record.status + '\')">明细</span>';


        if ((status == 'DRAFTED' || typeof(record.status) == "undefined") && (currentUserId == record.applyId || currentUserNo == 'admin')) {
            s += '<span  title="编辑" onclick="businessEdit(\'' + businessId + '\',\'' + instId + '\')">编辑</span>';
        }
        if (status == 'RUNNING') {
            if ((record.currentProcessUserId && record.currentProcessUserId.indexOf(currentUserId) != -1) || currentUserNo == 'admin') {
                s += '<span  title="办理" onclick="businessTask(\'' + record.taskId + '\')">办理</span>';
            }
        }

        if (currentUserNo != 'admin') {
            if ((record.status == 'DRAFTED' && currentUserId == record.applyId) || (record.status == 'DISCARD_END')) {
                s += '<span  title="删除" onclick="removeBusiness(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
            }
        } else {
            s += '<span  title="删除" onclick="removeBusiness(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
        }


        return s;
    }

    //..（后台根据配置的表单进行跳转）
    function addBusiness() {
        var url = jsUseCtxPath + "/bpm/core/bpmInst/attachedDocTranslate/start.do";
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (businessListGrid) {
                    businessListGrid.reload();
                }
            }
        }, 1000);
    }

    //..编辑行数据流程（后台根据配置的表单进行跳转）
    function businessEdit(businessId, instId) {
        var url = jsUseCtxPath + "/bpm/core/bpmInst/start.do?instId=" + instId;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (businessListGrid) {
                    businessListGrid.reload()
                }
            }
        }, 1000);
    }

    //..
    function businessDetail(businessId, status) {
        var action = "detail";
        var url = jsUseCtxPath + "/serviceEngineering/core/attachedDocTranslate/editPage.do?action=" + action +
            "&businessId=" + businessId + "&status=" + status;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (businessListGrid) {
                    businessListGrid.reload()
                }
            }
        }, 1000);
    }

    //..跳转到任务的处理界面
    function businessTask(taskId) {
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
                        if (winObj.closed) {
                            clearInterval(loop);
                            if (businessListGrid) {
                                businessListGrid.reload();
                            }
                        }
                    }, 1000);
                }
            }
        })
    }

    //..
    function removeBusiness(record) {
        var rows = [];
        if (record) {
            rows.push(record);
        } else {
            rows = businessListGrid.getSelecteds();
        }
        if (rows.length <= 0) {
            mini.alert("请至少选中一条记录");
            return;
        }
        mini.confirm("确定删除选中记录？", "提示", function (action) {
            debugger;
            if (action != 'ok') {
                return;
            } else {
                var rowIds = [];
                var instIds = [];
                var haveSomeRowsWrong = false;
                for (var i = 0, l = rows.length; i < l; i++) {
                    var r = rows[i];
                    if (r.status == 'DRAFTED' && currentUserId == r.applyId || (r.status == 'DISCARD_END' && currentUserId == r.applyId)) {
                        rowIds.push(r.id);
                        instIds.push(r.instId);
                    } else if (currentUserNo == 'admin') {
                        rowIds.push(r.id);
                        instIds.push(r.instId);
                    } else {
                        haveSomeRowsWrong = true;
                        break;
                    }
                }
                if (haveSomeRowsWrong) {
                    mini.alert("仅草稿和废弃状态数据可由本人删除,或者admin可以强制删除");
                    return;
                }
                _SubmitJson({
                    url: jsUseCtxPath + "/serviceEngineering/core/attachedDocTranslate/deleteBusiness.do",
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
        });
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

    //..所有非特殊文本都变成这个格式
    function render(e) {
        if (e.value != null && e.value != "") {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            return html;
        }
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
    //..
    function exportFile() {
        var rows = businessListGrid.getSelecteds();
        if (rows.length <= 0) {
            mini.alert("请至少选中一条记录");
            return;
        }
        var cnt = 0;
        var ids = "";
        for (var i = 0, l = rows.length; i < l; i++) {
            var r = rows[i];
            //用form的方式去提交
            if (r.status == "SUCCESS_END") {
                if (cnt > 0) {
                    ids += ',';
                }
                ids += r.id;
                cnt = cnt + 1;
            }
        }
        if (cnt == 0) {
            mini.alert("请选择已审核单据进行下载！");
            return;
        }
        else {
            url = "/serviceEngineering/core/attachedDocTranslate/zipFileDownload.do";
            downLoadFile("", "", ids, url);
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

</script>
<redxun:gridScript gridId="businessListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>