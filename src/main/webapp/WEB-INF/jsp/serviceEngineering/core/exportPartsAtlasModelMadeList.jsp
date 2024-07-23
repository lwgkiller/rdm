<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>产品机型图册制作申请列表</title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">机型制作编号: </span>
                    <input class="mini-textbox" id="modelMadeNum" name="modelMadeNum">
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">设计物料编码: </span>
                    <input class="mini-textbox" id="matCode" name="matCode">
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">设计型号: </span>
                    <input class="mini-textbox" id="designType" name="designType"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">申请人: </span>
                    <input id="applyUserId" name="applyUserId" textname="applyUserName" class="mini-user rxc"
                           plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="" length="50" maxlength="50"
                           mainfield="no" single="true"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">产品主管: </span>
                    <input id="modelOwnerId" name="modelOwnerId" textname="modelOwnerName" class="mini-user rxc"
                           plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="" length="50" maxlength="50"
                           mainfield="no" single="true"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">机型制作人: </span>
                    <input id="jxzzrId" name="jxzzrId" textname="jxzzrName" class="mini-user rxc"
                           plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="" length="50" maxlength="50"
                           mainfield="no" single="true"/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">流程状态: </span>
                    <input id="instStatus" name="status" class="mini-combobox" style="width:120px;"
                           textField="value" valueField="key" emptyText="请选择..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                           data="[ {'key' : 'DRAFTED','value' : '草稿'},{'key' : 'RUNNING','value' : '运行中'},{'key' : 'SUCCESS_END','value' : '成功结束'},
							   {'key' : 'DISCARD_END','value' : '作废'},{'key' : 'blank','value' : '空'}]"
                    />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">当前节点: </span>
                    <input id="taskName" name="taskName" class="mini-combobox" style="width:120px;"
                           textField="value" valueField="value"
                           required="false" allowInput="false" showNullItem="true"/>
                </li>

                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <div style="display: inline-block" class="separator"></div>
                    <a class="mini-button" id="addModelMadeBtn" style="margin-right: 5px" plain="true" onclick="addModelMade()">新增</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="delModelMade()">删除</a>
                    <a class="mini-button" id="openImportWindow" style="margin-right: 5px" plain="true" onclick="openImportWindow()">导入</a>
                    <a class="mini-button" id="exportBusiness" style="margin-right: 5px" plain="true" onclick="exportBusiness()">导出</a>
                </li>
            </ul>
        </form>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="modelMadeListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onlyCheckSelection="true"
         url="${ctxPath}/serviceEngineering/core/exportPartsAtlas/modelMadeListQuery.do" idField="id"
         multiSelect="true" showColumnsMenu="true" sizeList="[50,100,200]" pageSize="50" allowAlternating="true" pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div name="action" cellCls="actionIcons" width="100" headerAlign="center" align="center" renderer="onActionRenderer"
                 cellStyle="padding:0;">操作
            </div>
            <div field="modelMadeNum" width="80" headerAlign="center" align="center" allowSort="true">机型制作编号</div>
            <div field="matCode" width="80" headerAlign="center" align="center" allowSort="true">设计物料编码</div>
            <div field="designType" width="100" headerAlign="center" align="center" allowSort="false">设计型号</div>
            <div field="modelOwnerName" align="center" width="80" headerAlign="center" allowSort="false">产品主管</div>
            <div field="jxzzrName" align="center" width="80" headerAlign="center" allowSort="false">机型制作人</div>
            <div field="creator" width="70" headerAlign="center" align="center" allowSort="false">编制人</div>
            <div field="CREATE_TIME_" width="70" headerAlign="center" align="center" allowSort="false">创建时间</div>
            <div field="status" width="70" headerAlign="center" align="center" allowSort="true" renderer="onStatusRenderer">流程状态</div>
            <div field="currentProcessUser" sortField="currentProcessUser" width="70" align="center" headerAlign="center" allowSort="false">当前处理人
            </div>
            <div field="currentProcessTask" width="100" align="center" headerAlign="center">当前流程任务</div>
        </div>
    </div>
</div>
<%----%>
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
                        <a href="#" style="color:blue;text-decoration:underline;" onclick="downImportTemplate()">产品机型图册制作申请列表导入模板.xls</a>
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
<!--导出Excel相关HTML-->
<form id="excelForm" action="${ctxPath}/serviceEngineering/core/exportPartsAtlas/exportBusiness.do" method="post" target="excelIFrame">
    <input type="hidden" name="filter" id="filter"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var modelMadeListGrid = mini.get("modelMadeListGrid");
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var fwgcsUser =${fwgcsUser};
    var importWindow = mini.get("importWindow");
    var nodeSetListWithName = '${nodeSetListWithName}';

    $(function () {
        searchFrm();
        if (!fwgcsUser && currentUserNo != 'admin') {
            // mini.get("addModelMadeBtn").setEnabled(false);
            mini.get("openImportWindow").setEnabled(false);
        }
        mini.get("taskName").setData(mini.decode(nodeSetListWithName));
    });

    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var id = record.id;
        var instId = record.instId;
        var s = '<span  title="查看" onclick="modelMadeDetail(\'' + id + '\',\'' + record.status + '\')">明细</span>';
        if (record.status == 'DRAFTED') {
            s += '<span  title="编辑" onclick="modelMadeEdit(\'' + id + '\',\'' + instId + '\')">编辑</span>';
        } else {
            var currentProcessUserId = record.currentProcessUserId;
            if ((currentProcessUserId && currentProcessUserId.indexOf(currentUserId) != -1) || currentUserNo == 'admin') {
                s += '<span  title="办理" onclick="modelMadeTask(\'' + record.taskId + '\')">办理</span>';
            }
        }

        var status = record.status;
        if ((status == 'DRAFTED' || !status) && (currentUserId == record.CREATE_BY_ || currentUserNo == 'admin')) {
            s += '<span  title="删除" onclick="delModelMade(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
        }

        return s;
    }

    function onStatusRenderer(e) {
        var record = e.record;
        var status = record.status;
        var arr = [{'key': 'DRAFTED', 'value': '草稿', 'css': 'orange'},
            {'key': 'RUNNING', 'value': '运行中', 'css': 'green'},
            {'key': 'SUCCESS_END', 'value': '成功结束', 'css': 'blue'},
            {'key': 'DISCARD_END', 'value': '作废', 'css': 'red'}
        ];
        return $.formatItemValue(arr, status);
    }

    //新增流程（后台根据配置的表单进行跳转）
    function addModelMade() {
        var url = jsUseCtxPath + "/bpm/core/bpmInst/LJTCJXZZ/start.do";
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (modelMadeListGrid) {
                    modelMadeListGrid.reload()
                }
            }
        }, 1000);
    }

    //编辑行数据流程（后台根据配置的表单进行跳转）
    function modelMadeEdit(id, instId) {
        var url = jsUseCtxPath + "/bpm/core/bpmInst/start.do?instId=" + instId;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (modelMadeListGrid) {
                    modelMadeListGrid.reload()
                }
            }
        }, 1000);
    }

    //明细（直接跳转到详情的业务controller）
    function modelMadeDetail(id, status) {
        var action = "detail";
        var url = jsUseCtxPath + "/serviceEngineering/core/exportPartsAtlas/modelMadeEditPage.do?action=" + action + "&id=" + id + "&status=" + status;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (modelMadeListGrid) {
                    modelMadeListGrid.reload()
                }
            }
        }, 1000);
    }

    //跳转到任务的处理界面
    function modelMadeTask(taskId) {
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
                            if (modelMadeListGrid) {
                                modelMadeListGrid.reload();
                            }
                        }
                    }, 1000);
                }
            }
        })
    }

    function delModelMade(record) {
        var rows = [];
        if (record) {
            rows.push(record);
        } else {
            rows = modelMadeListGrid.getSelecteds();
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
                    if ((r.status == 'DRAFTED' || !status) && (currentUserId == r.CREATE_BY_ || currentUserNo == 'admin')) {
                        rowIds.push(r.id);
                        instIds.push(r.instId);
                    } else {
                        existStartInst = true;
                        continue;
                    }
                }
                if (existStartInst) {
                    mini.alert("仅草稿状态数据可由本人删除");
                }
                if (rowIds.length > 0) {
                    _SubmitJson({
                        url: jsUseCtxPath + "/serviceEngineering/core/exportPartsAtlas/deleteModelMade.do",
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
            xhr.open('POST', jsUseCtxPath + '/serviceEngineering/core/exportPartsAtlas/importModelMade.do', false);
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
        form.attr("action", jsUseCtxPath + "/serviceEngineering/core/exportPartsAtlas/importModelMadeTemplateDownload.do");
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
    //..
    function exportBusiness() {
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
</script>
<redxun:gridScript gridId="modelMadeListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>