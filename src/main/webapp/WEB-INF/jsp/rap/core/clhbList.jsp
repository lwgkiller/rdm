<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>国三环保信息公开列表管理</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/rap/clhbList.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px;">
                    <span class="text" style="width:auto">机械型号: </span>
                    <input class="mini-textbox" id="wjModel" name="wjModel"/>
                </li>
                <li style="margin-right: 15px;">
                    <span class="text" style="width:auto">整机设计型号: </span>
                    <input class="mini-textbox" id="designXh" name="designXh"/>
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

                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <a id="editMsg" class="mini-button " style="margin-right: 5px" plain="true"
                       onclick="addNewClhb()">新增</a>
                    <a id="editOldMsg" class="mini-button " style="margin-right: 5px" plain="true"
                       onclick="addOldClhb()">引用</a>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="exportClhb()">导出</a>
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
    <div id="wjListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         sizeList="[50,100,200]" pageSize="50"
         url="${ctxPath}/environment/core/Wj/queryList.do"
         idField="id" allowAlternating="true" showPager="true" multiSelect="true">
        <div property="columns">
            <div type="checkcolumn" width="10"></div>
            <div name="action" cellCls="actionIcons" width="50" headerAlign="center" align="center"
                 renderer="onMessageActionRenderer" cellStyle="padding:0;">操作
            </div>
            <div field="zjgkh" headerAlign='center' align='center' width="60">整机环保信息公开号</div>
            <div field="designXh" headerAlign='center' align='center' width="40">整机设计型号</div>
            <div field="wjModel" headerAlign='center' align='center' width="40">机械型号</div>
            <div field="zjwlh" headerAlign='center' align='center' width="30">整机物料号</div>
            <div field="fdjzzs" headerAlign='center' align='center' width="30">发动机品牌</div>
            <div field="fdjxh" headerAlign='center' align='center' width="40">发动机型号</div>
            <div field="noteStatus" headerAlign='center' align='center' width="40" renderer="onStatusChangeRenderer">环保信息状态</div>
            <div field="status" width="25" headerAlign="center" align="center" allowSort="true"
                 renderer="onStatusRenderer">流程状态
            </div>
            <div field="applyName" headerAlign='center' width="30" align='center'>发布人</div>
            <div field="deptName" headerAlign='center' width="50" align='center'>申请部门</div>
            <div field="CREATE_TIME_" width="30" dateFormat="yyyy-MM-dd" headerAlign='center' align="center">发布时间</div>
        </div>
    </div>
</div>
<form id="excelForm" action="${ctxPath}/environment/core/Wj/exportClhbList.do" method="post" target="excelIFrame">
    <input type="hidden" name="filter" id="filter"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var wjListGrid = mini.get("wjListGrid");
    var currentUserId = "${currentUserId}";
    var currentUserDep = "${currentUserDep}";
    var currentUserNo = "${currentUserNo}";
    var currentUserRoles = ${currentUserRoles};
    var isCPZG =${isCPZG};

    function onMessageActionRenderer(e) {
        var record = e.record;
        var wjId = record.wjId;
        var instId = record.instId;
        var status = record.status;
        var noteStatus = record.noteStatus;
        var s = '';
        if (status != 'DRAFTED') {
            s += '<span  title="明细" onclick="clhbDetail(\'' + wjId + '\',\'' + instId + '\')">明细</span>';
            if (whetherIsManager(currentUserRoles)) {
                s += '<span  title="修改" onclick="clhbChangeEdit(\'' + wjId + '\',\'' + instId + '\')">修改</span>';
            }
        }
        if (record.status == 'DRAFTED') {
            s += '<span  title="编辑" onclick="clhbEdit(\'' + wjId + '\',\'' + instId + '\')">编辑</span>';
        }
        if (record.status == 'RUNNING') {
            var currentProcessUserId = record.currentProcessUserId;
            if (currentProcessUserId && currentProcessUserId.indexOf(currentUserId) != -1) {
                s += '<span  title="办理" onclick="clhbTask(\'' + record.taskId + '\')">办理</span>';
            }
        }
        if (status == 'DRAFTED') {
            s += '<span  title="删除" onclick="removeclhb(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
        }
        if (whetherIsManager(currentUserRoles)) {
            s += '<span  title="更改状态" onclick="statusChange(\'' + wjId + '\',\'' + noteStatus + '\')">更改状态</span>';
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

    function exportClhb() {
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
        if (!userRoles) {
            return false;
        }
        for (var i = 0; i < userRoles.length; i++) {
            if (userRoles[i].REL_TYPE_KEY_ == 'GROUP-USER-LEADER' || userRoles[i].REL_TYPE_KEY_ == 'GROUP-USER-BELONG') {
                if (userRoles[i].NAME_.indexOf('动力应用技术管理员') != -1) {
                    return true;
                }
            }
        }
        return false;
    }

    function downloadFile() {
        var rows = wjListGrid.getSelecteds();
        if (rows.length <= 0) {
            mini.alert("请至少选中一条记录");
            return;
        }
        var rowIds = [];
        for (var i = 0, l = rows.length; i < l; i++) {
            var r = rows[i];
            rowIds.push(r.wjId);
        }
        var url = "/environment/core/Wj/downloadFile.do";
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

    function statusChange(wjId,noteStatus) {
        mini.confirm("确定变更技术状态？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                _SubmitJson({
                    url: jsUseCtxPath + "/environment/core/Wj/statusChange.do?",
                    method: 'POST',
                    showMsg: false,
                    data: {wjId: wjId,noteStatus:noteStatus},
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
</script>
<redxun:gridScript gridId="wjListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
