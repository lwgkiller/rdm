<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>技术交底书变更申请</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
</head>

<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="processInfo" class="mini-button" style="display: none" onclick="processInfo()">流程信息</a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin: 0 auto; width: 80%">
        <form id="formCqbg" method="post">
            <input id="flowType" name="flowType" class="mini-hidden"/>
            <input id="cqbgId" name="cqbgId" class="mini-hidden"/>
            <input id="instId" name="instId" class="mini-hidden"/>
            <input id="zllx" name="zllx" class="mini-hidden"/>
            <table class="table-detail" cellspacing="1" cellpadding="0">
                <caption style="font-weight: bold;font-size: 20px !important;">
                    技术交底书关联项目变更申请
                </caption>
                <tr>
                    <td style="width: 7%">提案名称：</td>
                    <td>
                        <input id="jsjdsId" name="jsjdsId" textname="zlName" style="width:98%;" property="editor"
                               class="mini-buttonedit" showClose="true"
                               oncloseclick="onSelectJdsCloseClick" allowInput="false"
                               onbuttonclick="selectJds()"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%">技术交底书审批完成时间：</td>
                    <td style="width: 30%;">
                        <input id="bmscwcTime" name="bmscwcTime" class="mini-datepicker"
                               style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 10%">变更后项目名称：</td>
                    <td style="width:auto">
                        <input id="project" name="projectId" textname="projectName" class="mini-buttonedit"
                               style="width:98%;height:34px"
                               allowInput="false" onbuttonclick="addRelatedProject()" showClose="true"
                               oncloseclick="relatedCloseClick()"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 10%">变更后项目成果计划：</td>
                    <td style="width:auto">
                        <input id="plan" name="planId" textname="planName" class="mini-buttonedit"
                               style="width:98%;height:34px"
                               allowInput="false" onbuttonclick="addRelatedPlan()" showClose="true"
                               oncloseclick="relatedPlanCloseClick()"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%">变更后项目开始时间：</td>
                    <td style="width: 30%;">
                        <input id="firstPlanStartTime" name="firstPlanStartTime" class="mini-datepicker"
                               style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%">变更后项目结束时间：</td>
                    <td style="width: 30%;">
                        <input id="lastPlanEndTime" name="lastPlanEndTime" class="mini-datepicker"
                               style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%">变更后项目当前阶段：</td>
                    <td style="width: 30%;">
                        <input id="currentStageName" name="currentStageName" class="mini-textbox" readonly
                               style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td>专利与项目关联性论述(不少于100字)：</td>
                    <td>
						<input  id="projectLinkReason" name="projectLinkReason" class="mini-textarea"
                                style="width:98%;height: 200px" emptytext="不少于100字"/>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
<div id="selectZlWindow" title="选择需要表更的技术交底书" class="mini-window" style="width:750px;height:450px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
    <input id="parentInputScene" style="display: none"/>
    <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px"
         borderStyle="border-left:0;border-top:0;border-right:0;">
        <span style="font-size: 14px;color: #777">提案名称: </span>
        <input class="mini-textbox" width="130" id="zlName" style="margin-right: 15px"/>
        <a class="mini-button" iconCls="icon-search" plain="true" onclick="searchJds()">查询</a>
    </div>
    <div class="mini-fit">
        <div id="zlListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
             onrowdblclick="onRowDblClick"
             idField="id" showColumnsMenu="false" sizeList="[20,50,100,200]" pageSize="20"
             allowAlternating="true" pagerButtons="#pagerButtons" url="${ctxPath}/zhgl/core/jsjds/getJsjdsList.do">
            <div property="columns">
                <div type="checkcolumn" width="25px"></div>
                <div field="zlName" sortField="zlName" width="100" headerAlign="center" align="center" allowSort="true">
                    提案名称
                </div>
                <div field="zllx" width="50" headerAlign="center" align="center" allowSort="false"
                     renderer="onLXRenderer">专利类型
                </div>
                <div field="instStatus"  width="60" headerAlign="center" align="center" allowSort="true" renderer="onJdsStatusRenderer">
                    认定状态</div>
            </div>
        </div>
    </div>
    <div property="footer" style="padding:5px;height: 35px">
        <table style="width:100%;height: 100%">
            <tr>
                <td style="width:120px;text-align:center;">
                    <input type="button" style="height: 25px;width: 70px" value="确定" onclick="selectJdsOK()"/>
                    <input type="button" style="height: 25px;width: 70px" value="取消" onclick="selectJdsHide()"/>
                </td>
            </tr>
        </table>
    </div>
</div>
<div id="projectWindow" title="关联项目信息选择窗口" class="mini-window" style="width:1200px;height:700px;"
     showModal="true" showFooter="false" allowResize="true">
    <div class="mini-toolbar">
        <div class="searchBox">
            <form id="searchGrid" class="search-form" style="margin-bottom: 25px">
                <ul>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">项目名称: </span>
                        <input class="mini-textbox" id="relatedProjectName" name="relatedProjectName"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">项目编号: </span>
                        <input class="mini-textbox" id="projectNumber" name="projectNumber">
                    </li>
                    <li style="margin-right: 15px">
                        <a class="mini-button" style="margin-right: 5px" plain="true"
                           onclick="searchProcessData()">查询</a>
                        <a class="mini-button btn-red" style="margin-right: 5px" plain="true"
                           onclick="cleanProcessData()">清空查询</a>
                    </li>
                    <li style="display: inline-block;float: right;">
                        <a id="importBtn" class="mini-button" onclick="choseRelatedProject()">确认</a>
                        <a id="closeProjectWindow" class="mini-button btn-red" onclick="closeProjectWindow()">关闭</a>
                    </li>
                </ul>
            </form>
        </div>
    </div>
    <div class="mini-fit" style="height: 100%;">
        <div id="projectListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
             onlyCheckSelection="true" idField="projectId" allowCellWrap="true"
             url="${ctxPath}/xcmgProjectManager/core/xcmgProject/getProjectList.do"
             multiSelect="false" showColumnsMenu="true" sizeList="[20,50,100,200]" pageSize="20" allowAlternating="true"
             pagerButtons="#pagerButtons">
            <div property="columns">
                <div type="checkcolumn" width="20"></div>
                <div field="projectId" visible="false"></div>
                <div type="indexcolumn" headerAlign="center" align="center" width="30">序号</div>
                <div field="projectName" sortField="projectName" width="170" headerAlign="center" align="center"
                     allowSort="true">项目名称
                </div>
                <div field="number" sortField="number" width="110" headerAlign="center" align="center" allowSort="true">
                    项目编号
                </div>
                <div field="firstPlanStartTime"  dateFormat="yyyy-MM-dd" align="center" width="80" headerAlign="center" allowSort="false">
                    计划开始时间</div>
                <div field="lastPlanEndTime"  dateFormat="yyyy-MM-dd" align="center" width="80" headerAlign="center" allowSort="false">
                    计划结束时间</div>
                <div field="currentStageName"  sortField="currentStageName"  width="100" headerAlign="center" align="center" allowSort="false">
                    项目当前阶段</div>
                <div field="respMan" sortField="respMan" width="50" headerAlign="center" align="center"
                     allowSort="false">项目负责人
                </div>
                <div field="status" sortField="status" width="50" allowSort="true" headerAlign="center" align="center"
                     renderer="onStatusRenderer">项目状态
                </div>
            </div>
        </div>
    </div>
</div>
<div id="planWindow" title="成果计划选择窗口" class="mini-window" style="width:1200px;height:700px;"
     showModal="true" showFooter="false" allowResize="true">
    <div class="mini-toolbar">
        <div class="searchBox">
            <form class="search-form" style="margin-bottom: 25px">
                <ul>
                    <li style="display: inline-block;float: right;">
                        <a id="choseRelatedPlan" class="mini-button" onclick="choseRelatedPlan()">确认</a>
                        <a id="closePlanWindow" class="mini-button btn-red" onclick="closePlanWindow()">关闭</a>
                    </li>
                </ul>
            </form>
        </div>
    </div>
    <div class="mini-fit" style="height: 100%;">
        <div id="planListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
             onlyCheckSelection="true" idField="id" allowCellWrap="true"
             url="${ctxPath}/zhgl/core/jsjds/selectProjectPlan.do"
             multiSelect="false" showColumnsMenu="true" showPager="false">
            <div property="columns">
                <div type="checkcolumn" width="20"></div>
                <div field="id" visible="false"></div>
                <div field="projectId" visible="false"></div>
                <div type="indexcolumn" headerAlign="center" align="center" width="30">序号</div>
                <div field="description" width="170" headerAlign="center" align="center"
                     allowSort="true">计划描述
                </div>
                <div field="deptName" width="110" headerAlign="center" align="center" allowSort="true">
                    部门
                </div>
                <div field="typeName" width="50" headerAlign="center" align="center"
                     allowSort="false">类别
                </div>
                <div field="output_time" headerAlign="center" align="center" dateFormat="yyyy-MM-dd" width="80">
                    预计产出时间
                </div>
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var nodeVarsStr = '${nodeVars}';
    var zlListGrid = mini.get("zlListGrid");
    var action = "${action}";
    var status = "${status}";
    var jsUseCtxPath = "${ctxPath}";
    var selectZlWindow = mini.get("selectZlWindow");
    var projectWindow = mini.get("projectWindow");
    var projectListGrid = mini.get("projectListGrid");
    var planWindow = mini.get("planWindow");
    var planListGrid = mini.get("planListGrid");
    var currentUserId = "${currentUserId}";
    var currentUserName = "${currentUserName}";
    var detailListGrid = mini.get("detailListGrid");
    var formCqbg = new mini.Form("#formCqbg");
    var cqbgId = "${cqbgId}";
    var flowType = "${flowType}";

    function onLXRenderer(e) {
        var record = e.record;
        var zllx = record.zllx;

        var arr = [{'key': 'FM', 'value': '发明'},
            {'key': 'SYXX', 'value': '实用新型'},
            {'key': 'WGSJ', 'value': '外观设计'}
        ];
        return $.formatItemValue(arr, zllx);
    }


    var first = "";

    $(function () {
        if (cqbgId) {
            var url = jsUseCtxPath + "/rdmZhgl/Cqbg/getCqbgDetail.do";
            $.post(
                url,
                {cqbgId: cqbgId},
                function (json) {
                    formCqbg.setData(json);
                });
        }else {
            mini.get("flowType").setValue(flowType);
        }
        if (action == 'task') {
            taskActionProcess();
        } else if (action == "detail") {
            formCqbg.setEnabled(false);
            $("#detailToolBar").show();
            if (status != 'DRAFTED') {
                $("#processInfo").show();
            }
        }
        mini.get("bmscwcTime").setEnabled(false);
        mini.get("firstPlanStartTime").setEnabled(false);
        mini.get("lastPlanEndTime").setEnabled(false);
    });

    function getData() {
        var formData = _GetFormJsonMini("formCqbg");
        //此处用于向后台产生流程实例时替换标题中的参数时使用
        // formData.bos=[];
        // formData.vars=[{key:'companyName',val:formData.companyName}];
        return formData;
    }

    function saveCqbg(e) {
        // var formValid = validCqbgFirst();
        // if (!formValid.result) {
        //     mini.alert(formValid.message);
        //     return;
        // }
        window.parent.saveDraft(e);
    }

    function startCqbgProcess(e) {
        var formValid = validCqbgFirst();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        $.ajaxSettings.async = false;
        var jsjdsId = mini.get('jsjdsId').getValue();
        var url = jsUseCtxPath + "/rdmZhgl/Cqbg/checkZgzl.do?jsjdsId=" + jsjdsId;
        var checkResult = {};
        $.get(
            url,
            function (json) {
                checkResult = json;
            });
        $.ajaxSettings.async = true;
        if (!checkResult.success) {
            mini.alert(checkResult.message);
            return;
        }
        window.parent.startProcess(e);
    }

    function validCqbgFirst() {
        var jsjdsId = $.trim(mini.get("jsjdsId").getValue());
        if (!jsjdsId) {
            return {"result": false, "message": "请选择需要变更的技术交底书"};
        }
        var project = $.trim(mini.get("project").getValue());
        if (!project) {
            return {"result": false, "message": "请选择关联项目"};
        }
        var plan=$.trim(mini.get("plan").getValue());
        if(!plan){
            return {"result": false, "message": "请选择成果计划"};
        }
        var firstPlanStartTime = $.trim(mini.get("firstPlanStartTime").getValue());
        var bmscwcTime = $.trim(mini.get("bmscwcTime").getValue());
        if(bmscwcTime&&firstPlanStartTime){
            var startTime = new Date(firstPlanStartTime);
            var spTime = new Date(bmscwcTime);
            var timeNow = startTime.getTime();
            var timeEnd = spTime.getTime();
            var timeDiff = (timeNow - timeEnd)/86400000;
            if (timeDiff > 90){
                return {"result": false, "message": "所选技术交底书审批完成时间不得早于项目开始时间90天以上"};
            }
        }
        var projectLinkReason = $.trim(mini.get("projectLinkReason").getValue());
        if (!projectLinkReason) {
            return {"result": false, "message": "请填写专利与项目关联性论述"};
        }
        const regex = /[^\n]/g;
        // 使用 match 方法获取匹配结果
        const matches = projectLinkReason.match(regex);
        // 计算匹配结果的长度
        const inputLength = matches ? matches.length : 0;
        // 判断长度是否超过限制
        if (inputLength < 100) {
            return {"result": false, "message": "专利与项目关联性论述不得少于100字"};
            // 如果超过限制，取消输入并恢复到上一次的内容
        }
        $.ajaxSettings.async = false;
        var jsjdsId = mini.get('jsjdsId').getValue();
        var url = jsUseCtxPath + "/rdmZhgl/Cqbg/checkZgzl.do?jsjdsId=" + jsjdsId;
        var checkResult = {};
        $.get(
            url,
            function (json) {
                checkResult = json;
            });
        $.ajaxSettings.async = true;
        if (!checkResult.success) {
            mini.alert(checkResult.message);
            return;
        }
        return {"result": true};
    }

    function cqbgApprove() {
        //编制阶段的下一步需要校验表单必填字段
        var formValid = validCqbgFirst();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        //检查通过
        window.parent.approve();
    }


    function processInfo() {
        var instId = $("#instId").val();
        var url = jsUseCtxPath + "/bpm/core/bpmInst/info.do?instId=" + instId;
        _OpenWindow({
            url: url,
            max: true,
            title: "流程图实例",
            width: 800,
            height: 600
        });
    }


    function taskActionProcess() {
        //获取上一环节的结果和处理人
        bpmPreTaskTipInForm();

        //获取环境变量
        if (!nodeVarsStr) {
            nodeVarsStr = "[]";
        }
        var nodeVars = $.parseJSON(nodeVarsStr);
        for (var i = 0; i < nodeVars.length; i++) {
            if (nodeVars[i].KEY_ == 'first') {
                first = nodeVars[i].DEF_VAL_;
            }
        }
        if (first != 'yes') {
            formCqbg.setEnabled(false);
        }
    }

    function selectJds(inputScene) {
        $("#parentInputScene").val(inputScene);
        selectZlWindow.show();
        searchJds();
    }

    //查询
    function searchJds() {
        var queryParam = [];
        //其他筛选条件
        var zlName = $.trim(mini.get("zlName").getValue());
        if (zlName) {
            queryParam.push({name: "zlName", value: zlName});
        }
        queryParam.push({name: "checkProject", value: "yes"});
        queryParam.push({name: "applyUserId", value: currentUserId});
        var data = {};
        data.filter = mini.encode(queryParam);
        data.pageIndex = zlListGrid.getPageIndex();
        data.pageSize = zlListGrid.getPageSize();
        data.sortField = zlListGrid.getSortField();
        data.sortOrder = zlListGrid.getSortOrder();
        //查询
        zlListGrid.load(data);
    }

    function onRowDblClick() {
        selectJdsOK();
    }

    function selectJdsOK() {
        var selectRow = zlListGrid.getSelected();
        if(selectRow.instStatus=='DISCARD_END'){
            mini.alert("无法选择未通过认定的交底书");
            return;
        }
        mini.get("jsjdsId").setValue(selectRow.jsjdsId);
        mini.get("zllx").setValue(selectRow.zllx);
        mini.get("jsjdsId").setText(selectRow.zlName);
        mini.get("bmscwcTime").setValue(selectRow.bmscwcTime);
        mini.get("plan").setValue('');
        mini.get("plan").setText('');
        selectJdsHide();
    }
    function selectJdsHide() {
        selectZlWindow.hide();
        mini.get("zlName").setValue('');
    }

    function onSelectJdsCloseClick(e) {
        mini.get("zllx").setValue('');
        mini.get("bmscwcTime").setValue('');
        mini.get("jsjdsId").setValue('');
        mini.get("jsjdsId").setText('');
    }

    //添加关联项目信息展开窗口
    function addRelatedProject() {
        projectWindow.show();
        searchProcessData();
    }

    //关闭关联项目选址页面
    function closeProjectWindow() {
        mini.get("relatedProjectName").setValue('');
        mini.get("projectNumber").setValue('');
        projectWindow.hide();
    }

    function cleanProcessData() {
        mini.get("relatedProjectName").setValue('');
        mini.get("projectNumber").setValue('');
        searchProcessData();
    }

    //关联项目查询
    function searchProcessData() {
        var projectName = mini.get("relatedProjectName").getValue();
        var number = mini.get("projectNumber").getValue();
        var paramArray = [{name: "projectName", value: projectName}, {name: "number", value: number}];
        var data = {};
        data.filter = mini.encode(paramArray);
        projectListGrid.load(data);
    }

    //关联项目写入
    function choseRelatedProject(e) {
        var row = projectListGrid.getSelected();
        mini.get("project").setValue(row.projectId);
        mini.get("project").setText(row.projectName);
        mini.get("firstPlanStartTime").setValue(row.firstPlanStartTime);
        mini.get("lastPlanEndTime").setValue(row.lastPlanEndTime);
        mini.get("currentStageName").setValue(row.currentStageName);
        mini.get("plan").setValue('');
        mini.get("plan").setText('');
        closeProjectWindow();
    }

    function relatedCloseClick() {
        mini.get("project").setValue("");
        mini.get("project").setText("");
        mini.get("plan").setValue("");
        mini.get("plan").setText("");
    }

    function onStatusRenderer(e) {
        var record = e.record;
        var status = record.status;
        var arr = [{'key': 'DRAFTED', 'value': '草稿', 'css': 'orange'},
            {'key': 'RUNNING', 'value': '运行中', 'css': 'green'},
            {'key': 'SUCCESS_END', 'value': '成功结束', 'css': 'blue'},
            {'key': 'DISCARD_END', 'value': '作废', 'css': 'red'},
            {'key': 'ABORT_END', 'value': '异常中止结束', 'css': 'red'},
            {'key': 'PENDING', 'value': '挂起', 'css': 'gray'}
        ];

        return $.formatItemValue(arr, status);
    }

    function addRelatedPlan() {
        var jsjdsId = mini.get("jsjdsId").getValue();
        if (!jsjdsId) {
            mini.alert("请先选择需要变更的交底书");
            return;
        }
        var projectId = mini.get("project").getValue();
        if (!projectId) {
            mini.alert("请先选择产出该专利的项目名称");
            return;
        }
        var zllx = mini.get("zllx").getValue();
        planWindow.show();
        searchPlan(projectId,zllx);
    }
    function searchPlan(projectId,zllx) {
        if(zllx=='FM'){
            var zllxName = '发明';
        }else if(zllx=='SYXX'){
            var zllxName = '实用新型';
        }else if(zllx=='WGSJ'){
            var zllxName = '外观';
        }
        var queryParam = [];
        //其他筛选条件
        var data = {};
        queryParam.push({name: "projectId", value: projectId});
        queryParam.push({name: "zllx", value: '国内'+zllxName});
        data.filter = mini.encode(queryParam);
        //查询
        planListGrid.load(data);
    }


    function choseRelatedPlan() {
        var row = planListGrid.getSelected();
        mini.get("plan").setValue(row.id);
        mini.get("plan").setText(row.description);
        closePlanWindow();
    }

    function closePlanWindow() {
        planWindow.hide();
    }

    function relatedPlanCloseClick() {
        mini.get("plan").setValue('');
        mini.get("plan").setText('');
    }

    function onJdsStatusRenderer(e) {
        var record = e.record;
        var instStatus = record.instStatus;
        if (!instStatus || instStatus=='') {
            instStatus='SUCCESS_END';
        }
        var arr = [ {'key' : 'DRAFTED','value' : '草稿','css' : 'orange'},
            {'key' : 'RUNNING','value' : '认定中','css' : 'green'},
            {'key' : 'SUCCESS_END','value' : '通过认定','css' : 'blue'},
            {'key' : 'DISCARD_END','value' : '未通过认定','css' : 'red'}
        ];

        return $.formatItemValue(arr,instStatus);
    }
</script>
</body>
</html>
