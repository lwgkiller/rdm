
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
    <title>试验任务及数据管理</title>
    <%@include file="/commons/list.jsp"%>
    <%--<script src="${ctxPath}/scripts/xcmgbudget/budgetMonthFlowCgTab.js?version=${static_res_version}" type="text/javascript"></script>--%>
    <script src="${ctxPath}/scripts/drbfm/testTaskList.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
    <style>
        .mini-grid-rows-view
        {
            background: white !important;
        }
        .mini-grid-cell-inner {
            line-height: 40px !important;
            padding: 0;
        }
        .mini-grid-cell-inner
        {
            font-size:14px !important;
        }
    </style>
</head>
<body>
<div class="mini-fit" style="width: 100%; height: 95%;background: #fff">
    <div class="mini-toolbar" >
        <div class="searchBox">
            <form id="searchForm" class="search-form" style="margin-bottom: 5px">
                <ul >
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">任务编号: </span>
                        <input id="testNumber" name="testNumber" class="mini-textbox"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">验证方式: </span>
                        <input id="abilityName" name="abilityName" class="mini-textbox"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">验证部门: </span>
                        <input id="respDeptName" name="respDeptName" class="mini-textbox"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">验证责任人: </span>
                        <input id="testRespUserName" name="testRespUserName" class="mini-textbox"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">流程状态: </span>
                        <input id="instStatus" name="instStatus" class="mini-combobox" style="width:120px;"
                               textField="value" valueField="key" emptyText="请选择..."
                               required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                               data="[ {'key' : 'DRAFTED','value' : '草稿'},{'key' : 'RUNNING','value' : '审批中'},{'key' : 'SUCCESS_END','value' : '已完成'},
							   {'key' : 'DISCARD_END','value' : '作废'}]"
                        />
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">验证类型: </span>
                        <input id="testType" name="testType" class="mini-combobox" style="width:120px;"
                               textField="value" valueField="key" emptyText="请选择..."
                               required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                               data="[ {'key' : '专项测试','value' : '专项测试'},{'key' : '常规附带','value' : '常规附带'}]"
                        />
                    </li>
                    <li style="margin-left: 10px">
                        <a class="mini-button" style="margin-right: 5px"  plain="true" onclick="searchFrm()">查询</a>
                        <a class="mini-button btn-red" style="margin-right: 5px"  plain="true" onclick="clearForm()">清空查询</a>
                    </li>
                    <li id="testTaskTool">
                        <div style="display: inline-block" class="separator"></div>
                        <a class="mini-button" plain="true" style="margin-right: 5px"
                           onclick="addTestTask()">新增专项测试</a>
                        <a class="mini-button" plain="true" style="margin-right: 5px"
                           onclick="addNoFlowTestTask()">新增常规附带</a>
                        <a class="mini-button btn-red" id="delFunction" plain="true" style="margin-right: 5px"
                           onclick="removeApply()">删除</a>
                    </li>
                </ul>
                <ul>
                    <li style="margin-left: 10px">
                        <span style="color: red;max-width:1000px !important;">注：专项测试会创建验证任务流程，并由平台所进行验证；常规附带不会创建流程，由部件负责人自己填写验证结果</span>
                    </li>
                </ul>
            </form>
        </div>
    </div>
    <div id="testTaskListGrid" class="mini-datagrid" allowResize="false" style="height: 90%"
         idField="id" allowCellEdit="false" allowCellSelect="true" allowSortColumn="false"
         url="${ctxPath}/drbfm/testTask/getTestTaskList.do?singleId=${singleId}" idField="id"
         autoload = "true"
         multiSelect="true" showColumnsMenu="false" allowAlternating="true" showPager="false"
         allowCellWrap="true" showVGridLines="true">
        <div property="columns">
            <div type="checkcolumn" width="60px"></div>
            <div name="action" cellCls="actionIcons" width="100px" headerAlign="center" align="center"
                 renderer="onActionRenderer" cellStyle="padding:0;">操作</div>
            <div field="testNumber" width="190" headerAlign="center" align="center" allowSort="true">任务编号</div>
            <div field="testType" width="70" headerAlign="center" align="center" allowSort="true">验证类型</div>
            <div field="abilityName" headerAlign="center" width="135px" align="center">验证方式
            </div>
            <div field="relQuotaNames" headerAlign="center" width="200px" align="center">关联的指标
            </div>
            <div field="testMethodFiles" headerAlign="center" width="90px" align="center" renderer="showFileRenderer">验证方案
            </div>
            <div field="respDeptName" headerAlign="center" width="120px" align="center">验证部门
            </div>
            <div field="testRespUserName" headerAlign="center" width="100px" align="center">验证责任人
            </div>
            <div field="testPlanStartTime" headerAlign="center" width="125px" align="center">计划开始时间
            </div>
            <div field="testPlanEndTime" headerAlign="center" width="125px" align="center">计划结束时间
            </div>
            <div field="testContent" headerAlign="center" width="230px" align="center">验证内容
            </div>
            <div field="testResultFiles" headerAlign="center" width="90px" align="center" renderer="showReportRenderer" >验证报告
            </div>
            <div field="status" sortField="status"   width="70"  allowSort="true" align="center" headerAlign="center" renderer="onStatusRenderer" >流程状态</div>
            <div field="currentProcessUser"   width="80" align="center" headerAlign="center" allowSort="false">当前处理人</div>
            <div field="currentProcessTask" width="110" align="center" headerAlign="center">当前流程节点</div>
        </div>
    </div>
</div>


<div id="showFileWindow" title="附件列表" class="mini-window" style="width:950px;height:550px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
    <div class="mini-toolbar" style="text-align:center;line-height:30px;margin-bottom: 10px"
         borderStyle="border-center:0;border-top:0;border-right:0;">
    </div>
    <div class="mini-fit">
        <div id="fileListGrid" class="mini-datagrid" style="width: 100%; height: 85%"
             allowResize="false"
             idField="id"
             autoload="true"
             showPager="false" showColumnsMenu="false" allowAlternating="true">
            <div property="columns">
                <div field="fileType" width="140" headerAlign="center" renderer="fileTypeRenderer" align="center" >附件类型</div>
                <div field="fileName" width="140" headerAlign="center" align="center">附件名称</div>
                <div field="fileSize" width="80" headerAlign="center" align="center">附件大小</div>
                <div field="creator" width="70" headerAlign="center" align="center">创建人</div>
                <div field="CREATE_TIME_" width="90" headerAlign="center" align="center"
                     dateFormat="yyyy-MM-dd HH:mm:ss">创建时间
                </div>
                <div field="action" width="100" headerAlign='center' align="center"
                     renderer="operationRenderer">
                    操作
                </div>
            </div>
        </div>
    </div>
    <div property="footer" style="padding:5px;height: 35px">
        <table style="width:100%;height: 100%">
            <tr>
                <td style="width:120px;text-align:center;">
                    <input type="button" style="height: 25px;width: 70px" value="关闭" onclick="showFileHide()"/>
                </td>
            </tr>
        </table>
    </div>
</div>


<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var singleId = "${singleId}";
    var action="${action}";
    var stageName="${stageName}";
    var applyId = "";
    var currentUserNo = "${currentUserNo}";
    var currentUserId = "${currentUserId}";
    var testTaskListGrid=mini.get("testTaskListGrid");
    var functionWindow=mini.get("functionWindow");
    var showFileWindow=mini.get("showFileWindow");
    var fileListGrid=mini.get("fileListGrid");
    var currentUserName = "${currentUserName}";
    var currentTime  = "${currentTime }";
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";


    //添加删除按钮显示

    $(function () {
        if (action != "task" || stageName != "testTaskCreateAndResult") {
            $("#testTaskTool").hide();
        }
    });


    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var applyId = record.id;
        var testType = record.testType;
        var s = '';
        if(testType=='专项测试' || !testType) {
            var instId = record.instId;
            var status = record.status;
            var currentProcessUserId = record.currentProcessUserId;
            s = '<span  title="查看" onclick="testTaskDetail(\'' + applyId + '\',\'' + status+'\',\''+testType+ '\')">查看</span>';
            if (status == 'DRAFTED') {
                if (action == "task" && stageName == "testTaskCreateAndResult") {
                    s += '<span  title="编辑" onclick="testTaskEdit(\'' + applyId + '\',\'' + instId +'\',\''+testType+ '\')">编辑</span>';
                } else {
                    s += '<span  title="编辑" style="color: silver" > 编辑 </span>';
                }
            }
            if (currentProcessUserId && currentProcessUserId.indexOf(currentUserId) != -1) {
                if (action == "task" && stageName == "testTaskCreateAndResult") {
                    s += '<span  title="办理" onclick="testTaskDo(\'' + record.taskId + '\')">办理</span>';
                } else {
                    s += '<span  title="办理" style="color: silver" > 办理 </span>';
                }
            }

            if ((status == 'DISCARD_END' || status == 'DRAFTED') && currentUserId == record.CREATE_BY_) {
                if (action == "task" && stageName == "testTaskCreateAndResult") {
                    s += '<span  title="删除" onclick="removeApply(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
                } else {
                    s += '<span  title="删除" style="color: silver" > 删除 </span>';
                }
            }
        } else if(testType=='常规附带') {
            var createBy = record.CREATE_BY_;
            s += '<span  title="查看" onclick="testTaskDetail(\'' + applyId + '\',\'\',\''+testType+ '\')">查看</span>';
            if (currentUserId ==createBy) {
                s += '<span  title="编辑" onclick="testTaskEdit(\'' + applyId + '\',\'\',\''+testType+ '\')">编辑</span>';
                s += '<span  title="删除" onclick="removeApply(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
            }
        }
        return s;
    }

    //验证方案附件
    function showFileRenderer(e) {
        var record = e.record;
        //需都可以预览，只有申请人在流程结束后，
        var cellHtml = '<span  title="附件详情" style="color:#409EFF;cursor: pointer;" onclick="showFileDetail(\'' + record.id + '\')">附件详情</span>';

        return cellHtml;
    }

    function showFileDetail(id) {
        var url="${ctxPath}/drbfm/testTask/demandList.do?fileType=scheme&applyId="+id
        fileListGrid.setUrl(url);
        fileListGrid.load();
        applyId = id;
        showFileWindow.show()

    }

    //验证报告附件

    function showReportRenderer(e) {
        var record = e.record;
        //需都可以预览，只有申请人在流程结束后，
        var cellHtml = '<span  title="附件详情" style="color:#409EFF;cursor: pointer;" onclick="showReportDetail(\'' + record.id + '\')">附件详情</span>';

        return cellHtml;
    }

    function showReportDetail(id) {
        var url="${ctxPath}/drbfm/testTask/demandList.do?fileType=report&applyId="+id
        fileListGrid.setUrl(url);
        fileListGrid.load();
        applyId = id;
        showFileWindow.show()

    }

    function onRelDemandCloseClick() {
        mini.get("relDeptDemandId").setValue('');
        mini.get("relDeptDemandId").setText('');
    }

    function addTestTask() {
        var url=jsUseCtxPath + "/bpm/core/bpmInst/drbfmTestTask/start.do?singleId="+singleId;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (testTaskListGrid) {
                    searchFrm();
                }
            }
        }, 1000);
    }

    function addNoFlowTestTask() {
        var url=jsUseCtxPath + "/drbfm/testTask/testTaskEditPage.do?singleId="+singleId+"&action=edit"+"&testType=常规附带";
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (testTaskListGrid) {
                    searchFrm();
                }
            }
        }, 1000);
    }


    function addRequest() {
        requestWindow.show();
    }

    function showFileHide() {
        showFileWindow.hide();
    }

    function operationRenderer(e) {
        var record = e.record;
        if (!record.id) {
            return "";
        }
        var cellHtml = returnPreviewSpan(record.fileName, record.id, applyId, coverContent);

        var downloadUrl = '/drbfm/testTask/fileDownload.do';
        if (record ) {
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="downLoadFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + applyId + '\',\'' + downloadUrl + '\')">下载</span>';
        } else{
            cellHtml += '&nbsp;&nbsp;&nbsp;<span  title="下载" style="color: silver" >下载</span>';
        }
        return cellHtml;
    }

    function returnPreviewSpan(fileName, fileId, formId, coverContent) {

        var s = '';
        if (fileName == "") {
            return s;
        }
        var fileType = getFileType(fileName);

        if (fileType == 'other') {
            s = '&nbsp;&nbsp;&nbsp;<span  title="预览" style="color: silver" >预览</span>';
        } else {
            var url = '/drbfm/testTask/preview.do?fileType=' + fileType;
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf6(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\',\'' + fileType + '\')">预览</span>';
        }
        return s;
    }

    function onStatusRenderer(e) {
        var record = e.record;
        var status = record.status;

        var arr = [{'key': 'DRAFTED', 'value': '草稿', 'css': 'orange'},
            {'key': 'RUNNING', 'value': '审批中', 'css': 'green'},
            {'key': 'SUCCESS_END', 'value': '已完成', 'css': 'blue'},
            {'key': 'DISCARD_END', 'value': '作废', 'css': 'red'}
        ];

        return $.formatItemValue(arr, status);
    }

    function fileTypeRenderer(e) {
        var record = e.record;
        var fileType = record.fileType;

        var arr = [{'key': 'scheme', 'value': '试验方案'},
            {'key': 'report', 'value': '试验报告',}
        ];

        return $.formatItemValue(arr, fileType);
    }

</script>
<redxun:gridScript gridId="testTaskListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
