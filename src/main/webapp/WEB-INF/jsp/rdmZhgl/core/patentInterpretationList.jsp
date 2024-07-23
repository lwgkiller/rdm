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
                    <span class="text" style="width:auto">专业类别：</span>
                    <input id="professionalCategory" name="professionalCategory" class="mini-combobox" style="width:98%"
                           url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=professionalCategory"
                           valueField="key" textField="value"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">专利公开号：</span>
                    <input class="mini-textbox" id="patentPublicationNo" name="patentPublicationNo"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">专利名称：</span>
                    <input class="mini-textbox" id="patentName" name="patentName"/>
                </li>
                <%--<li style="margin-right: 15px">--%>
                    <%--<span class="text" style="width:auto">公开日期：</span>--%>
                    <%--<input id="openDate" name="openDate" class="mini-datepicker" format="yyyy-MM-dd" allowInput="false"--%>
                           <%--valueType="string" showTime="false" showOkButton="false" showClearButton="false" style="width:98%;"/>--%>
                <%--</li>--%>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">公开开始日期：</span>
                    <input id="openDateBegin" name="openDateBegin" class="mini-datepicker" format="yyyy-MM-dd" allowInput="false"
                           valueType="string" showTime="false" showOkButton="false" showClearButton="false" style="width:98%;"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">公开结束日期：</span>
                    <input id="openDateEnd" name="openDateEnd" class="mini-datepicker" format="yyyy-MM-dd" allowInput="false"
                           valueType="string" showTime="false" showOkButton="false" showClearButton="false" style="width:98%;"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">专利申请人：</span>
                    <input class="mini-textbox" id="patentApplicant" name="patentApplicant"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">IPC分类号：</span>
                    <input class="mini-textbox" id="IPCNo" name="IPCNo"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">IPC主分类号：</span>
                    <input class="mini-textbox" id="IPCMainNo" name="IPCMainNo"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">侵权风险：</span>
                    <input id="tortRisk" name="tortRisk" class="mini-combobox" style="width:98%"
                           url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=tortRisk"
                           valueField="key" textField="value"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">复杂程度：</span>
                    <input id="complexity" name="complexity" class="mini-combobox" style="width:98%"
                           url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=complexity"
                           valueField="key" textField="value"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">应用价值：</span>
                    <input class="mini-textbox" id="applicationValue" name="applicationValue"/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">审批状态: </span>
                    <input id="businessStatus" name="businessStatus" class="mini-combobox" style="width:98%"
                           textField="value" valueField="key" emptyText="请选择..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                           data="[{'key': 'A-editing', 'value': '上传专利信息'},
                                  {'key': 'B-assigning', 'value': '解读助手处理中'},
                                  {'key': 'C-interpreting', 'value': '解读人解读中'},
                                  {'key': 'D-evaluating', 'value': '牵头人评价中'},
                                  {'key': 'E-assignChuangXinRen', 'value': '主管所长分配创新责任人'},
                                  {'key': 'F-chuangXin', 'value': '专利再创新中'},
                                  {'key': 'G-chuangXinEvaluating', 'value': '主管所长评价创新结果'},
                                  {'key': 'H-reviewing', 'value': '技术创新办公室主任审核中'},
                                  {'key': 'H-reviewing2', 'value': '分管领导批准中'},
                                  {'key': 'I-close', 'value': '归档'}]"
                    />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">解读助手：</span>
                    <input class="mini-textbox" id="interpreterUserAss" name="interpreterUserAss"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">解读人：</span>
                    <input class="mini-textbox" id="interpreterUser" name="interpreterUser"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">解读人部门：</span>
                    <input class="mini-textbox" id="interpreterUserDep" name="interpreterUserDep"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">牵头人：</span>
                    <input class="mini-textbox" id="leaderUser" name="leaderUser"/>
                </li>
                <li style="margin-left: 10px">
                    <f:a alias="patentInterpretation-searchFrm" onclick="searchFrm()" showNoRight="false" style="margin-right: 5px">查询</f:a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <f:a alias="patentInterpretation-addBusiness" onclick="addBusiness()" showNoRight="false" style="margin-right: 5px">新增</f:a>
                    <f:a alias="patentInterpretation-removeBusiness" onclick="removeBusiness()" showNoRight="false" style="margin-right: 5px">删除</f:a>
                    <f:a alias="patentInterpretation-openImportWindow" onclick="openImportWindow()" showNoRight="false"
                         style="margin-right: 5px">导入</f:a>
                    <%--<a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="test()">属于IPC我司专利申请</a>--%>
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
         showCellTip="true" idField="id" allowCellEdit="true" allowCellSelect="true" oncellvalidation="onCellValidation" allowHeaderWrap="true"
         multiSelect="true" showColumnsMenu="false" sizeList="[50,100]" pageSize="50" allowAlternating="true" pagerButtons="#pagerButtons"
         url="${ctxPath}/zhgl/core/patentInterpretation/dataListQuery.do">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div type="indexcolumn" width="35" headerAlign="center" align="center">序号</div>
            <div name="action" cellCls="actionIcons" width="150" headerAlign="center" align="center" renderer="onActionRenderer"
                 cellStyle="padding:0;">操作
            </div>
            <div field="professionalCategory" width="100" headerAlign="center" align="center" allowSort="true">专业类别</div>
            <div field="patentPublicationNo" width="150" headerAlign="center" align="center" allowSort="true">专利公开（公告）号</div>
            <div field="patentName" width="150" headerAlign="center" align="center" allowSort="true" renderer="render">专利名称</div>
            <div field="applicationDate" width="100" headerAlign="center" align="center" allowSort="true">申请日</div>
            <div field="openDate" width="120" headerAlign="center" align="center" allowSort="true">公开（公告）日</div>
            <div field="patentApplicant" width="100" headerAlign="center" align="center" allowSort="true">专利申请人</div>
            <div field="IPCNo" width="120" headerAlign="center" align="center" allowSort="true" renderer="render">IPC分类号</div>
            <div field="IPCMainNo" width="120" headerAlign="center" align="center" allowSort="true">IPC主分类号</div>
            <div width="150" headerAlign="center" align="center" allowSort="true" renderer="ipcRenderer">属于IPC我司专利申请</div>
            <div field="interpretationCompletionDate" width="100" headerAlign="center" align="center" allowSort="true"
                 dateFormat="yyyy-MM-dd">解读完成日期
            </div>
            <div field="schemeDescription" width="200" headerAlign="center" align="center" allowSort="true" renderer="render">方案简述</div>
            <div field="keyWords" width="150" headerAlign="center" align="center" allowSort="true" renderer="render">技术关键词</div>
            <div field="tortRisk" width="100" headerAlign="center" align="center" allowSort="true">侵权风险</div>
            <div field="tortRiskAnalysis" width="200" headerAlign="center" align="center" allowSort="true" renderer="render">侵权风险分析</div>
            <div field="riskToPro" width="200" headerAlign="center" align="center" allowSort="true">风险对应产品范围</div>
            <div field="riskToProAlready" width="150" headerAlign="center" align="center" allowSort="true">我司是否已有专利保护</div>
            <div field="patentNameMine" width="200" headerAlign="center" align="center" allowSort="true" renderer="render">我司专利名称及申请号</div>
            <div field="technologyBranch" width="150" headerAlign="center" align="center" allowSort="true">技术分支</div>
            <div field="technicalEffect" width="200" headerAlign="center" align="center" allowSort="true" renderer="render">技术效果</div>
            <div field="interpretationEvaluation" width="100" headerAlign="center" align="center" allowSort="true">解读评价</div>
            <div field="complexity" width="120" headerAlign="center" align="center" allowSort="true">复杂程度</div>
            <div field="applicationValue" width="100" headerAlign="center" align="center" allowSort="true">应用价值</div>
            <div field="applicationValueAnalysis" width="200" headerAlign="center" align="center" allowSort="true" renderer="render">应用价值分析</div>
            <%--<div field="enlightenment" width="200" headerAlign="center" align="center" allowSort="true" renderer="render">对研发工作启示</div>--%>
            <%--<div field="isDiscuss" width="150" headerAlign="center" align="center" allowSort="true" renderer="render">是否建议专业小组讨论</div>--%>
            <div field="interpreterUserAss" width="100" headerAlign="center" align="center" allowSort="true">解读助手</div>
            <div field="interpreterUser" width="100" headerAlign="center" align="center" allowSort="true">解读人</div>
            <div field="leaderUser" width="100" headerAlign="center" align="center" allowSort="true">牵头人</div>
            <div field="businessStatus" width="200" headerAlign="center" align="center" renderer="onStatusRenderer">审批状态</div>
            <div field="allTaskUserNames" sortField="allTaskUserNames" width="100" align="center" headerAlign="center"
                 allowSort="false">当前处理人
            </div>
            <%--<div field="currentProcessTask" width="80" align="center" headerAlign="center" renderer="render">当前任务</div>--%>
            <%--<div field="status" width="80" headerAlign="center" align="center" allowSort="false" renderer="render">流程状态</div>--%>
        </div>
    </div>
</div>
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
                        <a href="#" style="color:blue;text-decoration:underline;" onclick="downImportTemplate()">专利解读导入模板.xls</a>
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

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var businessListGrid = mini.get("businessListGrid");
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var currentUserName = "${currentUserName}";
    var currentTime = new Date();
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";
    var importWindow = mini.get("importWindow");

    //..行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var businessId = record.id;
        var instId = record.instId;
        var s = '';
        s += '<span  title="明细" onclick="businessDetail(\'' + businessId + '\',\'' + record.status + '\')">明细</span>';
        if ((record.status == 'DRAFTED' || typeof(record.status) == "undefined") && (currentUserId == record.applyUserId || currentUserNo == 'admin')) {
            s += '<span  title="编辑" onclick="businessEdit(\'' + businessId + '\',\'' + instId + '\')">编辑</span>';
        } else {
            if (record.myTaskId || currentUserNo == 'admin') {
                s += '<span  title="办理" onclick="businessTask(\'' + record.myTaskId + '\')">办理</span>';
            }
        }
        if (currentUserNo != 'admin') {
            if (record.status == 'DRAFTED' && currentUserId == record.applyUserId) {
                s += '<span  title="删除" onclick="removeBusiness(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
            }
        } else {
            s += '<span  title="删除" onclick="removeBusiness(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
        }
        return s;
    }

    //..（后台根据配置的表单进行跳转）
    function addBusiness() {
        var url = jsUseCtxPath + "/bpm/core/bpmInst/patentInterpretation/start.do";
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

    //..编辑行数据流程（后台根据配置的表单进行跳转）
    function businessEdit(businessId, instId) {
        if (instId != '' && instId != "undefined") {
            var url = jsUseCtxPath + "/bpm/core/bpmInst/start.do?instId=" + instId;
        } else {//todo：重点测测
            var url = jsUseCtxPath + "/bpm/core/bpmInst/patentInterpretation/start.do?businessId_=" + businessId;
//            var url = jsUseCtxPath + "/bpm/core/bpmInst/patentInterpretation/start.do";
        }
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
        var url = jsUseCtxPath + "/zhgl/core/patentInterpretation/editPage.do?action=" + action +
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
            if (action != 'ok') {
                return;
            } else {
                var rowIds = [];
                var instIds = [];
                var haveSomeRowsWrong = false;
                for (var i = 0, l = rows.length; i < l; i++) {
                    var r = rows[i];
                    if (r.businessStatus == 'A-editing' && currentUserId == r.applyUserId) {
                        rowIds.push(r.id);
                        instIds.push(r.INST_ID_);
                    } else if (currentUserNo == 'admin') {
                        rowIds.push(r.id);
                        instIds.push(r.INST_ID_);
                    } else {
                        haveSomeRowsWrong = true;
                        break;
                    }
                }
                if (haveSomeRowsWrong) {
                    mini.alert("仅草稿状态数据可由专利工程师删除,或者admin可以强制删除");
                    return;
                }
                _SubmitJson({
                    url: jsUseCtxPath + "/zhgl/core/patentInterpretation/deleteBusiness.do",
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
            xhr.open('POST', jsUseCtxPath + '/zhgl/core/patentInterpretation/importExcel.do', false);
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
        form.attr("action", jsUseCtxPath + "/zhgl/core/patentInterpretation/importTemplateDownload.do");
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

    //..状态渲染
    function onStatusRenderer(e) {
        var record = e.record;
        var businessStatus = record.businessStatus;
        var arr = [{'key': 'A-editing', 'value': '上传专利信息'},
            {'key': 'B-assigning', 'value': '解读助手处理中'},
            {'key': 'C-interpreting', 'value': '解读人解读中'},
            {'key': 'D-evaluating', 'value': '牵头人评价中'},
            {'key': 'E-assignChuangXinRen', 'value': '主管所长分配创新责任人'},
            {'key': 'F-chuangXin', 'value': '专利再创新中'},
            {'key': 'G-chuangXinEvaluating', 'value': '主管所长评价创新结果'},
            {'key': 'H-reviewing', 'value': '技术创新办公室主任审核中'},
            {'key': 'H-reviewing2', 'value': '分管领导批准中'},
            {'key': 'I-close', 'value': '归档'}];
        return $.formatItemValue(arr, businessStatus);
    }

    //..所有非特殊文本都变成这个格式
    function render(e) {
        if (e.value != null && e.value != "") {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            return html;
        }
    }

    //..
    function test() {
        var row = businessListGrid.getSelected();
        if (row == null) {
            mini.alert("请至选中一条记录");
            return;
        }
        var ipcZflh = row.IPCMainNo;
//        var realUrl = jsUseCtxPath + "/zhgl/core/zlgl/zgzlListPage.do?ipcZflh=" + ipcZflh;
//        var config = {
//            url: realUrl,
//            max: true
//        };
//        _OpenWindow(config);
        //@lwgkiller:最快捷的方式调用热配对话框，而且此处还带默认筛参。
        //因此，上面的就不需要了，对zgzlListPage的改造还保留吧，也没啥影响。
        var config = {
            dialogKey: "我司专利名称-申请号-提案号-IPC主分类号-单选带参",
            params: "ipcZflh=" + ipcZflh,
            title: "属于IPC我司专利申请",
            height: 600,
            width: 1200
        };
        _CommonDialogExt(config);
    }

    //..
    function ipcRenderer(e) {
        return '<a class="mini-button btn-red" style="width: 100%;height: 100%" plain="true" onclick="test()">点击查看</a>'
    }
</script>
<redxun:gridScript gridId="businessListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>