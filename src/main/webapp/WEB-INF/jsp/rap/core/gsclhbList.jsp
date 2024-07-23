<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>国四环保信息公开列表管理</title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px;">
                    <span class="text" style="width:auto">机械型号: </span>
                    <input class="mini-textbox" id="cpjxxh" name="cpjxxh"/>
                </li>
                <li style="margin-right: 15px;">
                    <span class="text" style="width:auto">机械系族名称: </span>
                    <input class="mini-textbox" id="jxxzmc" name="jxxzmc"/>
                </li>
                <li style="margin-right: 15px;">
                    <span class="text" style="width:auto">整机设计型号: </span>
                    <input class="mini-textbox" id="zjsjxh" name="zjsjxh"/>
                </li>
                <li style="margin-right: 15px;">
                    <span class="text" style="width:auto">发动机品牌: </span>
                    <input id="fdjzzs" name="fdjzzs" class="mini-combobox" style="width:98%;"
                           textField="text" valueField="textValue" nullItemText="请选择..."
                           url="${ctxPath}/environment/core/Wj/getBoxList.do?textType=pp" showNullItem="true"/>
                </li>
                <li style="margin-right: 15px;">
                    <span class="text" style="width:auto">发动机型号: </span>
                    <input class="mini-textbox" id="fdjxh" name="fdjxh"/>
                </li>
                <li style="margin-right: 15px;">
                    <span class="text" style="width:auto">发动机系族: </span>
                    <input class="mini-textbox" id="cyjxs" name="cyjxs"/>
                </li>
                <li style="margin-right: 15px;">
                    <span class="text" style="width:auto">整机物料号: </span>
                    <input class="mini-textbox" id="zjwlh" name="zjwlh"/>
                </li>
                <li style="margin-right: 15px;">
                    <span class="text" style="width:auto">发布人: </span>
                    <input class="mini-textbox" id="applyName" name="applyName"/>
                </li>
                <li style="margin-right: 15px;">
                    <span class="text" style="width:auto">申请部门: </span>
                    <input class="mini-textbox" id="deptName" name="deptName"/>
                </li>
                <li style="display: none">
                    <input class="mini-textbox" id="checkDelay" name="checkDelay"/>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <a id="editMsg" class="mini-button " style="margin-right: 5px" plain="true"
                       onclick="addNewGsClhb()">新增</a>
                    <a id="editOldMsg" class="mini-button " style="margin-right: 5px" plain="true"
                       onclick="addOldGsClhb()">引用</a>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="exportGsClhb()">导出</a>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="downloadFile()">下载</a>
                </li>
            </ul>
        </form>
        <span class="searchSelectionBtn" onclick="no_search(this ,'searchForm')">
				<i class="icon-sc-lower"></i>
			</span>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="cxListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         sizeList="[50,100,200]" pageSize="50"
         url="${ctxPath}/environment/core/Cx/queryList.do"
         idField="id" allowAlternating="true" showPager="true" multiSelect="true">
        <div property="columns">
            <div type="checkcolumn" width="10"></div>
            <div name="action" cellCls="actionIcons" width="50" headerAlign="center" align="center"
                 renderer="onMessageActionRenderer" cellStyle="padding:0;">操作
            </div>
            <div field="zjgkh" headerAlign='center' align='center' width="60">整机环保信息公开号</div>
            <div field="jxxzmc" headerAlign='center' align='center' width="35">机械系族名称</div>
            <div field="zjsjxh" headerAlign='center' align='center' width="40">整机设计型号</div>
            <div field="cpjxxh" headerAlign='center' align='center' width="40">机械型号</div>
            <div field="zjwlh" headerAlign='center' align='center' width="40">整机物料号</div>
            <div field="fdjzzs" headerAlign='center' align='center' width="30">发动机品牌</div>
            <div field="fdjxh" headerAlign='center' align='center' width="40">发动机型号</div>
            <div field="cyjxs" headerAlign='center' align='center' width="40">发动机系族</div>
            <div field="upvecc" headerAlign='center' align='center' width="40">是否上传VECC</div>
            <div field="xxfwjc" headerAlign='center' align='center' width="30">是否下线访问检查</div>
            <div field="noteStatus" headerAlign='center' align='center' width="40" renderer="onStatusChangeRenderer">环保信息状态</div>
            <div field="status" width="30" headerAlign="center" align="center" allowSort="true"
                 renderer="onStatusRenderer">流程状态
            </div>
            <div field="applyName" headerAlign='center' width="30" align='center'>发布人</div>
            <div field="deptName" headerAlign='center' width="50" align='center'>申请部门</div>
            <div field="CREATE_TIME_" width="40" dateFormat="yyyy-MM-dd" headerAlign='center' align="center">发布时间</div>
        </div>
    </div>
</div>
<form id="excelForm" action="${ctxPath}/environment/core/Cx/exportGsClhbList.do" method="post" target="excelIFrame">
    <input type="hidden" name="filter" id="filter"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var cxListGrid = mini.get("cxListGrid");
    var currentUserId = "${currentUserId}";
    var currentUserDep = "${currentUserDep}";
    var currentUserNo = "${currentUserNo}";
    var deptName = "${deptName}";
    var checkDelay = "${checkDelay}";
    var currentUserRoles = ${currentUserRoles};
    var isCPZG=${isCPZG};
    function onMessageActionRenderer(e) {
        var record = e.record;
        var cxId = record.cxId;
        var instId = record.instId;
        var status = record.status;
        var noteStatus = record.noteStatus;
        var s = '';
        if (status != 'DRAFTED'){
            s += '<span  title="明细" onclick="gsclhbDetail(\'' + cxId + '\',\'' + status + '\')">明细</span>';
            if (whetherIsManager(currentUserRoles)) {
                s += '<span  title="修改" onclick="gsclhbChangeEdit(\'' + cxId + '\',\'' + status + '\')">修改</span>';
            }
        }
        if (record.status == 'DRAFTED') {
            s += '<span  title="编辑" onclick="gsclhbEdit(\'' + cxId + '\',\'' + instId + '\')">编辑</span>';
        }
        if(record.status =='RUNNING'){
            var currentProcessUserId=record.currentProcessUserId;
            if(currentProcessUserId && currentProcessUserId.indexOf(currentUserId) !=-1) {
                s+='<span  title="办理" onclick="gsclhbTask(\'' + record.taskId + '\')">办理</span>';
            }
        }

        if (status == 'DRAFTED' ) {
            s += '<span  title="删除" onclick="removeGsclhb(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
        }
        if (whetherIsManager(currentUserRoles)) {
            s += '<span  title="更改状态" onclick="statusChange(\'' + cxId + '\',\'' + noteStatus + '\')">更改状态</span>';
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
        if (deptName) {
            mini.get('deptName').setValue(deptName);
        }
        if (checkDelay) {
            mini.get('checkDelay').setValue(checkDelay);
        }
        searchFrm();
    });



    function addNewGsClhb() {
        var url = jsUseCtxPath + "/bpm/core/bpmInst/GSCLHBXXGK/start.do?type=new";
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (cxListGrid) {
                    cxListGrid.reload()
                }
            }
        }, 1000);
    }

    function addOldGsClhb() {
        var selectRow = cxListGrid.getSelected();
        if (!selectRow) {
            mini.alert("请至少选中一条记录");
            return;
        }
        var cxId = selectRow.cxId;
        var url = jsUseCtxPath + "/bpm/core/bpmInst/GSCLHBXXGK/start.do?type=old&oldCxId="+cxId;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (cxListGrid) {
                    cxListGrid.reload()
                }
            }
        }, 1000);
    }
    function gsclhbEdit(cxId,instId) {
        var url = jsUseCtxPath + "/bpm/core/bpmInst/start.do?instId=" + instId+ "&cxId=" + cxId;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (cxListGrid) {
                    cxListGrid.reload()
                }
            }
        }, 1000);
    }
    function gsclhbChangeEdit(cxId,status) {
        var url = jsUseCtxPath + "/environment/core/Cx/edit.do?action=change&cxId=" + cxId+"&status=" + status;
        var winObj = window.open(jsUseCtxPath + "/rdm/core/noFlowFormIframe?url="+encodeURIComponent(url));
    //    var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (cxListGrid) {
                    cxListGrid.reload()
                }
            }
        }, 1000);
    }
    function gsclhbDetail(cxId,status) {
        var action = "detail";
        var url = jsUseCtxPath + "/environment/core/Cx/edit.do?action="+action+"&cxId=" + cxId+"&status=" + status;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (cxListGrid) {
                    cxListGrid.reload()
                }
            }
        }, 1000);
    }

    function gsclhbFile(cxId) {
        mini.open({
            title: "附件列表",
            url: jsUseCtxPath + "/environment/core/Cx/fileList.do?&cxId=" + cxId,
            width: 1050,
            height: 550,
            allowResize: true,
            onload: function () {
                cxListGrid.reload();
            }
        });
    }

    function gsclhbTask(taskId) {
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
                            if (cxListGrid) {
                                cxListGrid.reload();
                            }
                        }
                    }, 1000);
                }
            }
        })
    }

    function removeGsclhb(record) {
        var rows = [];
        if (record) {
            rows.push(record);
        } else {
            rows = cxListGrid.getSelecteds();
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
                var instIds = [];
                for (var i = 0, l = rows.length; i < l; i++) {
                    var r = rows[i];
                    rowIds.push(r.cxId);
                    instIds.push(r.instId);
                }
                if (rowIds.length > 0) {
                    _SubmitJson({
                        url: jsUseCtxPath + "/environment/core/Cx/deletecx.do",
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
    function exportGsClhb() {
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
    //是否是动力应用管理人员
    function whetherIsManager(userRoles) {
        if(!userRoles) {
            return false;
        }
        for (var i = 0; i < userRoles.length; i++) {
            if (userRoles[i].REL_TYPE_KEY_ == 'GROUP-USER-LEADER'||userRoles[i].REL_TYPE_KEY_ == 'GROUP-USER-BELONG') {
                if(userRoles[i].NAME_.indexOf('动力应用技术管理员')!=-1) {
                    return true;
                }
            }
        }
        return false;
    }

    function statusChange(cxId,noteStatus) {
        mini.confirm("确定变更环保信息状态？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                _SubmitJson({
                    url: jsUseCtxPath + "/environment/core/Cx/statusChange.do?",
                    method: 'POST',
                    showMsg: false,
                    data: {cxId: cxId,noteStatus:noteStatus},
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

    function onStatusChangeRenderer(e) {
        var record = e.record;
        var noteStatus = record.noteStatus;

        var arr = [{'key': '有效', 'value': '有效', 'css': 'green'},
            {'key': '作废', 'value': '作废', 'css': 'red'}
        ];
        return $.formatItemValue(arr, noteStatus);
    }

    function downloadFile() {
        var rows = cxListGrid.getSelecteds();
        if (rows.length <= 0) {
            mini.alert("请至少选中一条记录");
            return;
        }
        var rowIds = [];
        for (var i = 0, l = rows.length; i < l; i++) {
            var r = rows[i];
            rowIds.push(r.cxId);
        }
        var url = "/environment/core/Cx/downloadFile.do";
        downLoad(rowIds, url);
    }
    function downLoad(formId, urlValue) {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + urlValue);
        var inputFormId = $("<input>");
        inputFormId.attr("type", "hidden");
        inputFormId.attr("name", "formId");
        inputFormId.attr("value", formId);
        $("body").append(form);
        form.append(inputFormId);
        form.submit();
        form.remove();
    }
</script>
<redxun:gridScript gridId="cxListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
