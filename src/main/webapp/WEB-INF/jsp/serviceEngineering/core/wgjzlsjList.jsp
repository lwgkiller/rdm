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
                    <span class="text" style="width:auto">资料类型：</span>
                    <input class="mini-textbox" id="dataType" name="dataType"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">物料编码：</span>
                    <input class="mini-textbox" id="materialCode" name="materialCode"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">物料描述：</span>
                    <input class="mini-textbox" id="materialDescription" name="materialDescription"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">物料所属部门：</span>
                    <input class="mini-textbox" id="materialDepartment" name="materialDepartment"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">机型：</span>
                    <input class="mini-textbox" id="designModel" name="designModel"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">产品所责任人：</span>
                    <input class="mini-textbox" id="cpsPrincipal" name="cpsPrincipal"/>
                </li>
                <li class="liBtn">
					<span class="unfoldBtn" onclick="no_more(this,'moreBox')">
						<em>展开</em>
						<i class="unfoldIcon"></i>
					</span>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <div style="display: inline-block" class="separator"></div>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="addWgjzlsj()">新增</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="removeWgjzlsj()">删除</a>
                    <a class="mini-button" style="margin-left: 10px" plain="true" onclick="exportWgj()">导出</a>
                    <a class="mini-button" style="margin-left: 10px" plain="true" onclick="openImportWindow()">导入</a>
                </li>
            </ul>
            <div id="moreBox">
                <ul>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">供应商：</span>
                        <input class="mini-textbox" id="supplier" name="supplier"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">归档完成时间：</span>
                        <input id="filingStartTime" name="filingStartTime" class="mini-datepicker" allowInput="false"/>
                        <span class="text" style="width:auto">-</span>
                        <input id="filingEndTime" name="filingEndTime" class="mini-datepicker" allowInput="false"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">制作完成时间：</span>
                        <input id="makeStartTime" name="makeStartTime" class="mini-datepicker" allowInput="false"/>
                        <span class="text" style="width:auto">-</span>
                        <input id="makeEndTime" name="makeEndTime" class="mini-datepicker" allowInput="false"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">创建者：</span>
                        <input class="mini-textbox" id="creator" name="creator"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">项目状态: </span>
                        <input id="taskStatus" name="taskStatus" class="mini-combobox" style="width:150px;"
                               textField="value" valueField="key" emptyText="请选择..."
                               required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                               data="[ {'key' : 'DRAFTED','value' : '草稿'},{'key' : 'RUNNING','value' : '运行中'},
                               {'key' : 'SUCCESS_END','value' : '成功结束'},{'key' : 'DISCARD_END','value' : '作废'},{'key' : 'ABORT_END','value' : '异常中止结束'},{'key' : 'PENDING','value' : '挂起'}]"
                        />
                    </li>
                </ul>
            </div>
        </form>
    </div>
</div>
<!--导出Excel相关HTML-->
<form id="excelForm" action="${ctxPath}/serviceEngineering/core/wgjzlsj/exportWgj.do" method="post" target="excelIFrame">
    <input type="hidden" name="filter" id="filter"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>
<!--导入Excel相关HTML-->
<div id="importWindow" title="导入窗口" class="mini-window" style="width:750px;height:280px;"
     showModal="true" showFooter="false" allowResize="true">
    <p style="display: inline-block;color: red;font-size:15px;vertical-align: middle">
    </p>
    <div style="display: inline-block;float: right;">
        <a id="importBtn" class="mini-button" onclick="importBusiness()">导入</a>
        <a id="closeWindow" class="mini-button btn-red" onclick="closeImportWindow()">关闭</a>
    </div>
    <div class="mini-fit" style="font-size: 14px;margin-top: 30px">
        <form id="formImport" method="post">
            <table class="table-detail" cellspacing="1" cellpadding="0">
                <tr>
                    <td style="width: 30%">上传模板：</td>
                    <td style="width: 70%;">
                        <a href="#" style="color:blue;text-decoration:underline;" onclick="downImportTemplate()">外购件资料收集导入模板.xls</a>
                    </td>
                </tr>
                <tr>
                    <td style="width: 30%">操作：</td>
                    <td>
                        <input class="mini-textbox" style="width:60%;float: left" id="fileName" name="fileName"
                               readonly/>
                        <input id="inputFile" style="display:none;" type="file" onchange="getSelectFile()"
                               accept="application/xls"/>
                        <a id="uploadFileBtn" class="mini-button" style="float: left;margin-left: 10px"
                           onclick="uploadFile">选择文件</a>
                        <a id="clearFileBtn" class="mini-button" style="float: left;margin-left: 10px"
                           onclick="clearUploadFile">清除</a>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
<%----%>
<div class="mini-fit" style="height: 100%;">
    <div id="wgjzlsjGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         allowCellWrap="true"
         showCellTip="true" idField="id" allowCellEdit="true" allowCellSelect="true"
         multiSelect="true" showColumnsMenu="false" sizeList="[20,50,100,200]" pageSize="20" allowAlternating="true" pagerButtons="#pagerButtons"
         url="${ctxPath}/serviceEngineering/core/wgjzlsj/wgjzlsjListQuery.do">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div type="indexcolumn" width="35" headerAlign="center" align="center">序号</div>
            <div name="action" cellCls="actionIcons" width="130" headerAlign="center" align="center" renderer="onActionRenderer"
                 cellStyle="padding:0;">操作
            </div>
            <div field="businessNo" headerAlign="center" align="center" width="150">单据编号</div>
            <div field="dataType" headerAlign="center" align="center" width="120">资料类型</div>
            <div field="materialCode" headerAlign="center" width="120" align="center">物料编码</div>
            <div field="materialDescription" headerAlign="center" width="300" align="center">物料描述</div>
            <div field="materialDepartment" headerAlign="center" align="center">物料所属部门</div>
            <div field="designModel" headerAlign="center" width="120" align="center">机型</div>
            <div field="supplier" headerAlign="center" width="300" align="center">供应商</div>
            <div field="submitDate" dateFormat="yyyy-MM-dd" width="100" align="center" headerAlign="center">反馈日期</div>
            <div field="cpsPrincipal" headerAlign="center" align="center">产品所责任人</div>
            <div field="fwgcPrincipal" headerAlign="center" align="center">服务工程责任人</div>
            <div field="filingTime" dateFormat="yyyy-MM-dd" width="150" align="center" headerAlign="center">归档完成时间</div>
            <div field="makeTime" dateFormat="yyyy-MM-dd" width="150" align="center" headerAlign="center">制作完成时间</div>
            <div field="creator" headerAlign="center" align="center" width="100">创建者</div>
            <div field="currentProcessUser" sortField="currentProcessUser" align="center" headerAlign="center">当前处理人</div>
            <div field="currentProcessTask" align="center" headerAlign="center" width="200">当前任务</div>
            <div field="taskStatus" headerAlign="center" align="center" renderer="taskStatusRenderer">任务状态</div>
        </div>
    </div>
</div>
<%--JS--%>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var wgjzlsjGrid = mini.get("wgjzlsjGrid");
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var isSa = "${isSa}";
    var importWindow = mini.get("importWindow");
    //..
    $(function () {
        searchFrm();
    });
    //..
    function taskStatusRenderer(e) {
        var record = e.record;
        var taskStatus = record.taskStatus;
        var arr = [{'key': 'DRAFTED', 'value': '草稿', 'css': 'orange'},
            {'key': 'RUNNING', 'value': '运行中', 'css': 'green'},
            {'key': 'SUCCESS_END', 'value': '成功结束', 'css': 'blue'},
            {'key': 'DISCARD_END', 'value': '作废', 'css': 'red'},
            {'key': 'ABORT_END', 'value': '异常中止结束', 'css': 'red'},
            {'key': 'PENDING', 'value': '挂起', 'css': 'gray'}
        ];
        return $.formatItemValue(arr, taskStatus);
    }
    //..新增流程（后台根据配置的表单进行跳转）
    function addWgjzlsj() {
        var url = jsUseCtxPath + "/bpm/core/bpmInst/WGJZLSJ/start.do";
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (wgjzlsjGrid) {
                    wgjzlsjGrid.reload()
                }
            }
        }, 1000);
    }
    //..
    function removeWgjzlsj(record) {
        var rows = [];
        if (record) {
            rows.push(record);
        } else {
            rows = wgjzlsjGrid.getSelecteds();
        }
        if (rows.length <= 0) {
            mini.alert("请至少选中一条记录");
            return;
        }
        mini.confirm("确定删除选中记录？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                var ids = [];
                var instIds = [];
                // var existStartInst = false;
                for (var i = 0, l = rows.length; i < l; i++) {
                    var r = rows[i];
                    // if (r.taskStatus == 'DRAFTED') {
                    ids.push(r.id);
                    instIds.push(r.instId);
                    // } else {
                    //     existStartInst = true;
                    //     continue;
                    // }
                }
                // if (existStartInst) {
                //     alert("仅草稿状态数据可由本人删除");
                // }
                if (ids.length > 0) {
                    _SubmitJson({
                        url: jsUseCtxPath + "/serviceEngineering/core/wgjzlsj/deleteWgjzlsj.do",
                        method: 'POST',
                        data: {ids: ids.join(','), instIds: instIds.join(',')},
                        success: function (data) {
                            if (data) {
                                searchFrm();
                            }
                        }
                    });
                }
            }
        });
    }
    //..导出
    function exportWgj() {
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
    //..明细（直接跳转到详情的业务controller）
    function wgjzlsjDetail(wgjzlsjId, taskStatus) {
        var action = "detail";
        var url = jsUseCtxPath + "/serviceEngineering/core/wgjzlsj/wgjzlsjEditPage.do?action=" +
            action + "&wgjzlsjId=" + wgjzlsjId + "&taskStatus=" + taskStatus;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (wgjzlsjGrid) {
                    wgjzlsjGrid.reload()
                }
            }
        }, 1000);
    }
    //..编辑行数据流程（后台根据配置的表单进行跳转）
    function wgjzlsjEdit(wgjzlsjId, instId) {
        var url = jsUseCtxPath + "/bpm/core/bpmInst/start.do?instId=" + instId;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (wgjzlsjGrid) {
                    wgjzlsjGrid.reload()
                }
            }
        }, 1000);
    }
    //..点击办理是跳转
    function wgjzlsjTask(taskId) {
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
                            if (wgjzlsjGrid) {
                                wgjzlsjGrid.reload();
                            }
                        }
                    }, 1000);
                }
            }
        })
    }
    //..修改网盘路径
    function toEditDiskPath(wgjzlsjId) {
        var action = "editDiskPath";
        var url = jsUseCtxPath + "/serviceEngineering/core/wgjzlsj/wgjzlsjEditPage.do?action=" +
            action + "&wgjzlsjId=" + wgjzlsjId;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (wgjzlsjGrid) {
                    wgjzlsjGrid.reload()
                }
            }
        }, 1000);
    }
    //..所有非特殊文本都变成这个格式
    function render(e) {
        if (e.value != null && e.value != "") {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            return html;
        }
    }
    //..行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var wgjzlsjId = record.id;
        var instId = record.instId;
        var s = '';
        s += '<span  title="明细" onclick="wgjzlsjDetail(\'' + wgjzlsjId + '\',\'' + record.taskStatus + '\')">明细</span>';
        //任何人都能编辑但只有创建者能删除
        if (record.taskStatus == 'DRAFTED' && record.CREATE_BY_ == currentUserId) {
            s += '<span  title="编辑" onclick="wgjzlsjEdit(\'' + wgjzlsjId + '\',\'' + instId + '\')">编辑</span>';
        } else {
            var currentProcessUserId = record.currentProcessUserId;
            if (currentProcessUserId && currentProcessUserId.indexOf(currentUserId) != -1) {
                s += '<span  title="办理" onclick="wgjzlsjTask(\'' + record.taskId + '\')">办理</span>';
            }
            if (record.taskStatus == 'RUNNING') {
                var cpsPrincipalId = record.cpsPrincipalId;
                var fwgcPrincipalId = record.fwgcPrincipalId;
                if ((cpsPrincipalId && cpsPrincipalId.indexOf(currentUserId) != -1) || (fwgcPrincipalId && fwgcPrincipalId.indexOf(currentUserId) != -1)) {
                    s += '<span  title="修改网盘路径" onclick="toEditDiskPath(\'' + wgjzlsjId + '\')">修改网盘路径</span>';
                }
            }
        }
        if (currentUserNo != 'admin' && isSa == 'false') {
            if (record.taskStatus == 'DRAFTED' && currentUserId == record.CREATE_BY_) {
                s += '<span  title="删除" onclick="removeWgjzlsj(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
            }
        } else {
            s += '<span  title="删除" onclick="removeWgjzlsj(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
        }
        return s;
    }
    //..
    function openImportWindow() {
        importWindow.show();
    }
    //..
    function importBusiness() {
        var file = null;
        var fileList = $("#inputFile")[0].files;
        if (fileList && fileList.length > 0) {
            file = fileList[0];
        }
        if (!file) {
            mini.alert('请选择文件！');
            return;
        }
        //XMLHttpRequest方式上传表单
        var xhr = false;
        try {
            //尝试创建 XMLHttpRequest 对象，除 IE 外的浏览器都支持这个方法。
            xhr = new XMLHttpRequest();
        } catch (e) {
            //使用较新版本的 IE 创建 IE 兼容的对象（Msxml2.XMLHTTP）。
            xhr = ActiveXobject("Msxml12.XMLHTTP");
        }

        if (xhr.upload) {
            xhr.onreadystatechange = function (e) {
                if (xhr.readyState == 4) {
                    if (xhr.status == 200) {
                        if (xhr.responseText) {
                            var returnObj = JSON.parse(xhr.responseText);
                            var message = '';
                            if (returnObj.message) {
                                message = returnObj.message;
                            }
                            mini.alert(message);
                        }
                    }
                }
            };
            //开始上传
            xhr.open('POST', jsUseCtxPath + '/serviceEngineering/core/wgjzlsj/importExcel.do', false);
            xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
            var fd = new FormData();
            fd.append('importFile', file);
            xhr.send(fd);
        }
    }
    //..
    function closeImportWindow() {
        importWindow.hide();
        clearUploadFile();
        searchFrm();
    }
    //..
    function downImportTemplate() {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + "/serviceEngineering/core/wgjzlsj/importTemplateDownload.do");
        $("body").append(form);
        form.submit();
        form.remove();
    }
    //..
    function getSelectFile() {
        var fileList = $("#inputFile")[0].files;
        if (fileList && fileList.length > 0) {
            var fileNameSuffix = fileList[0].name.split('.').pop();
            if (fileNameSuffix == 'xls' || fileNameSuffix == 'xlsx') {
                mini.get("fileName").setValue(fileList[0].name);
            }
            else {
                clearUploadFile();
                mini.alert('请上传excel文件！');
            }
        }
    }
    //..
    function uploadFile() {
        $("#inputFile").click();
    }
    //..
    function clearUploadFile() {
        $("#inputFile").val('');
        mini.get("fileName").setValue('');
    }
</script>
<redxun:gridScript gridId="wgjzlsjGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>