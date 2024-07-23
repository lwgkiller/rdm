
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
    <title>指标综合评价管理</title>
    <%@include file="/commons/list.jsp"%>
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

<div class="mini-fit" style="height: 100%;">
    <div class="mini-toolbar" >
        <div class="searchBox">
            <form id="searchForm" class="search-form" style="margin-bottom: 5px">
                <ul >
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">特性名称: </span>
                        <input id="quotaNameFilter" name="quotaName" class="mini-textbox"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">指标状态: </span>
                        <input id="validStatusFilter" name="validStatus" class="mini-combobox" style="width:120px;"
                               textField="value" valueField="key" emptyText="请选择..."
                               required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                               data="[ {'key' : '有效','value' : '有效'},{'key' : '作废','value' : '作废'}]"
                        />
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">综合验证结果: </span>
                        <input id="overallResult" name="overallResult" class="mini-combobox" style="width:120px;"
                               textField="value" valueField="key" emptyText="请选择..."
                               required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                               data='[{"text":"符合","value":"符合"},{"text":"不符合","value":"不符合"}]'
                        />
                    </li>
                    <li style="margin-left: 10px">
                        <a class="mini-button" style="margin-right: 5px"  plain="true" onclick="searchFrm()">查询</a>
                        <a class="mini-button btn-red" style="margin-right: 5px"  plain="true" onclick="clearForm()">清空查询</a>
                    </li>
                </ul>
            </form>
        </div>
    </div>
    <div id="quotaEvaluateListGrid" class="mini-datagrid" allowResize="false" style="height: 100%"
         idField="id" allowCellEdit="false" allowCellSelect="true" allowSortColumn="false"
         multiSelect="true" showColumnsMenu="false" allowAlternating="true" showPager="false"
         allowCellWrap="false" showVGridLines="true"
         url="${ctxPath}/drbfm/single/getQuotaWithEvaluateList.do?belongSingleId=${singleId}"
    >
        <div property="columns">
            <div type="checkcolumn" width="60px"></div>
            <div name="action" cellCls="actionIcons" width="130px" headerAlign="center" align="center"
                 renderer="onActionRenderer" cellStyle="padding:0;">操作</div>
            <div field="quotaName" headerAlign="center" width="200px" align="center">特性名称
            </div>
            <div field="sjStandardValue" headerAlign="center" width="100px" align="center">特性值
            </div>
            <div field="validStatus" headerAlign="center" width="80px" align="center" renderer="validStatusRenderer">指标状态
            </div>
            <div field="overallResult" headerAlign="center" width="105px" align="center" renderer="overallResultRenderer">综合验证结果
            </div>
            <div field="resultDesc" headerAlign="center" width="250px" align="center">验证结果异常说明
            </div>
            <div field="badReasonDesc" headerAlign="center" width="250px" align="center">不符合原因分析
            </div>
            <div field="badImproveDesc" headerAlign="center" width="240px" align="center">不符合改善对策
            </div>
            <div field="testResultData" headerAlign="center" width="105px" align="center" renderer="testResultDataRender">关联验证任务<br>&验证数据
            </div>
            <div field="maxlv" headerAlign="center" width="70" align="left" renderer="txRenderer">特性分类</div>

            <div field="sjStandardNames" headerAlign="center" width="230px" align="left">设计标准
            </div>
            <div field="testStandardNames" headerAlign="center" width="230px" align="left">测试标准
            </div>
            <div field="evaluateStandardNames" headerAlign="center" width="230px" align="left">评价标准
            </div>
            <div field="replaceQuotaName" headerAlign="center" width="200px" align="center">代替的旧指标
            </div>
            <div field="CREATE_TIME_" align="center" headerAlign="center" width="130px">创建时间</div>
            <div field="stopTime" align="center" headerAlign="center" width="130px">作废时间</div>
        </div>
    </div>
</div>

<div id="evaluateWindow" title="综合评价" class="mini-window" style="width:1150px;height:500px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
    <div class="mini-fit">
        <div class="topToolBar" style="float: right;">
            <div style="position: relative!important;">
                <a id="saveEvaluateBtn"class="mini-button" onclick="saveEvaluate()">保存</a>
                <a id="closeEvaluateBtn" class="mini-button btn-red" onclick="closeEvaluate()">关闭</a>
            </div>
        </div>
        <input id="belongQuotaId" name="belongQuotaId" class="mini-hidden"/>
        <table class="table-detail"  cellspacing="1" cellpadding="0" style="width: 99%">
            <tr>
                <td style="width: 12%">是否CTQ关键质量特性：</td>
                <td style="width: 38%;">
                    <input id="keyCTQ" name="keyCTQ" class="mini-combobox" style="width:150px;"
                           textField="value" valueField="key" emptyText="请选择..."
                           required="false" allowInput="false" showNullItem="false"
                           data="[ {'key' : '是','value' : '是'},{'key' : '否','value' : '否'}]"
                    />
                </td>
            </tr>
            <tr>
                <td style="width: 12%">综合评价记录：</td>
                <td style="width: 38%;">
                    <div class="mini-toolbar" >
                        <li id="opEvaluateButtons" style="display: inline-block">
                            <a class="mini-button"   plain="true" onclick="addEvaluate">添加综合评价</a>
                            <a class="mini-button btn-red"  plain="true" onclick="delEvaluate">删除综合评价</a>
                            <a style="color: #ff0000">（新增、修改、删除后需点击保存，只允许有一条“当前”状态的数据）</a>
                        </li>
                    </div>
                    <div id="oneQuotaEvaluate" class="mini-datagrid"  allowResize="false" style="height: 250px"
                         idField="id" allowCellEdit="true" allowCellSelect="true" allowSortColumn="false"
                         multiSelect="true" showColumnsMenu="false" allowAlternating="true" showPager="false" allowCellWrap="false" showVGridLines="true">
                        <div property="columns">
                            <div type="checkcolumn" width="25"></div>
                            <div field="id"  headerAlign="left" visible="false">id</div>
                            <div field="overallResult"  displayField="overallResult" headerAlign="center" align="center" width="90">综合验证结果
                                <input property="editor" class="mini-combobox" style="width:90%;"
                                       textField="text" valueField="value" emptyText="请选择..."
                                       allowInput="false" showNullItem="false" data='[{"text":"符合","value":"符合"},{"text":"不符合","value":"不符合"}]' />
                            </div>
                            <div field="resultDesc"  headerAlign="center" align="center" width="170">验证结果说明<span style="color:red">*</span>
                                <input property="editor" class="mini-textarea" style="overflow: auto" />
                            </div>
                            <div field="badReasonDesc"  headerAlign="center" align="center" width="170">不符合原因分析
                                <input property="editor" class="mini-textarea" style="overflow: auto" />
                            </div>
                            <div field="badImproveDesc"  headerAlign="center" align="center" width="170">不符合改善对策
                                <input property="editor" class="mini-textarea" style="overflow: auto" />
                            </div>
                            <div field="evaluateStatus"  headerAlign="center" align="center" width="70">状态
                                <input property="editor" class="mini-combobox" style="width:90%;"
                                       textField="text" valueField="value" emptyText="请选择..."
                                       allowInput="false" showNullItem="false" data='[{"text":"当前","value":"当前"},{"text":"历史","value":"历史"}]' />
                            </div>
                            <div field="updator"  headerAlign="center" align="center" width="70">操作人
                            </div>
                            <div field="UPDATE_TIME_"  headerAlign="center" align="center" width="90">操作时间
                            </div>
                        </div>
                    </div>
                </td>
            </tr>
        </table>
    </div>
</div>

<div id="testDataWindow" title="验证数据" class="mini-window" style="width:820px;height:250px;"
     showModal="true" showFooter="false" allowResize="true" showCloseButton="true">
    <div class="mini-fit">
        <div id="testDataListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
             idField="id" showColumnsMenu="false"
             allowAlternating="true" showPager="false">
            <div property="columns">
                <div field="relTestTaskId" headerAlign="center" width="180px" align="center" renderer="relTestTaskRenderer">关联试验任务
                </div>
                <div field="testType" width="70" headerAlign="center" align="center" allowSort="true">验证类型</div>
                <div headerAlign="center" width="100px" align="center" renderer="testTaskStatusRenderer">试验任务状态
                </div>
                <div field="quotaTestValue" headerAlign="center" width="110px" align="center">指标实测值
                </div>
                <div field="testResult" headerAlign="center" width="125px" align="center" renderer="testResultRenderer">验证部门评判结果
                </div>
                <div field="testRespUserName" headerAlign="center" width="80px" align="center">验证人员
                </div>
                <div field="testActualEndTime" headerAlign="center" width="115px" align="center">验证时间
                </div>
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var singleId = "${singleId}";
    var action="${action}";
    var stageName = "${stageName}";
    var quotaEvaluateListGrid=mini.get("quotaEvaluateListGrid");
    var evaluateWindow=mini.get("evaluateWindow");
    var oneQuotaEvaluate=mini.get("oneQuotaEvaluate");

    var testDataWindow=mini.get("testDataWindow");
    var testDataListGrid = mini.get("testDataListGrid");

    //行功能按钮
    function onActionRenderer(e) {
        //作废&创建新指标，指标综合评价
        var record = e.record;
        var s ='';
        if(action!='task' || stageName !='testTaskCreateAndResult') {
            s+='<span   style="color: silver" >综合评价</span>';
            return s;
        }
        s = '<span  title="综合评价" onclick="openEvaluateWindow(' +JSON.stringify(record).replace(/"/g, '&quot;')+')">综合评价</span>';
        return s;
    }

    function testResultDataRender(e) {
        var record = e.record;
        var quotaId = record.id;
        var hasValidTestTask = record.hasValidTestTask;
        if(hasValidTestTask) {
            return '<span   style="cursor: pointer;color: #0a7ac6" onclick="openTestData(\'' + quotaId + '\')">查看</span>';
        }
    }

    function openTestData(quotaId) {
        testDataWindow.show();
        var url=jsUseCtxPath+"/drbfm/testTask/queryQuotaTestData.do?belongSingleId="+singleId+"&relQuotaId="+quotaId;
        testDataListGrid.setUrl(url);
        testDataListGrid.load();
    }

    function addEvaluate() {
        var row = {'overallResult':'符合','evaluateStatus':'当前'};
        oneQuotaEvaluate.addRow(row, 0);
    }
    
    function delEvaluate() {
        var selecteds = oneQuotaEvaluate.getSelecteds();
        if (!selecteds || selecteds.length <= 0) {
            mini.alert('请至少选择一条数据');
            return;
        }
        mini.confirm("确定删除？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                var deleteArr = [];
                for (var i = 0; i < selecteds.length; i++) {
                    var row = selecteds[i];
                    deleteArr.push(row);
                }
                oneQuotaEvaluate.removeRows(deleteArr);
            }
        });
    }

    function openEvaluateWindow(record) {
        evaluateWindow.show();
        // 表单及表格赋值
        var keyCTQ= record.keyCTQ;
        if(!keyCTQ) {
            keyCTQ='是';
        }
        mini.get("keyCTQ").setValue(keyCTQ);
        mini.get("belongQuotaId").setValue(record.id);
        var url="${ctxPath}/drbfm/single/getOneQuotaEvaluateList.do?belongSingleId="+record.belongSingleId+"&belongQuotaId="+record.id;
        oneQuotaEvaluate.setUrl(url);
        oneQuotaEvaluate.load();
    }

    function saveEvaluate() {
        var formData = {};
        formData.belongQuotaId=mini.get("belongQuotaId").getValue();
        formData.belongSingleId=singleId;
        formData.keyCTQ=mini.get("keyCTQ").getValue();
        var oneQuotaEvaluateData = oneQuotaEvaluate.getData();
        if(oneQuotaEvaluateData.length>0) {
            for(var index=0;index<oneQuotaEvaluateData.length;index++) {
            //    校验字段
                if(!$.trim(oneQuotaEvaluateData[index].resultDesc)) {
                    mini.alert("请填写“验证结果说明”！");
                    return;
                }
                if(oneQuotaEvaluateData[index].overallResult=='不符合' && !$.trim(oneQuotaEvaluateData[index].badReasonDesc)) {
                    mini.alert("验证结果为不符合情况，需填写“不符合原因分析”！");
                    return;
                }
                if(oneQuotaEvaluateData[index].overallResult=='不符合' && !$.trim(oneQuotaEvaluateData[index].badImproveDesc)) {
                    mini.alert("验证结果为不符合情况，需填写“不符合改善对策”！");
                    return;
                }
            }
        }

        if (oneQuotaEvaluate.getChanges().length > 0) {
            //检查是否有超过1条的“当前”状态的数据
            if(oneQuotaEvaluateData.length>0) {
                var hasCurrent=false;
                for(var index=0;index<oneQuotaEvaluateData.length;index++) {
                    if(oneQuotaEvaluateData[index].evaluateStatus=='当前' && !hasCurrent) {
                        hasCurrent=true;
                    } else if (oneQuotaEvaluateData[index].evaluateStatus=='当前' && hasCurrent) {
                        mini.alert("存在多条状态为“当前”的评价数据，请修改后保存！");
                        return;
                    }
                }
            }
            formData.changeQuotaEvaluateData = oneQuotaEvaluate.getChanges();
        }

        var json = mini.encode(formData);
        $.ajax({
            url: jsUseCtxPath + "/drbfm/single/saveOneQuotaEvaluate.do",
            type: 'post',
            async: false,
            data: json,
            contentType: 'application/json',
            success: function (data) {
                if (data) {
                    if (data.success) {
                        closeEvaluate();
                    } else {
                        mini.alert("数据保存失败，" + data.message);
                    }
                }
            }
        });
    }
    
    function closeEvaluate() {
        evaluateWindow.hide();
        quotaEvaluateListGrid.reload();
    }

    function testResultRenderer(e) {
        var record = e.record;
        var testResult = record.testResult;
        if(!testResult) {
            return "";
        }
        if (testResult=='合格') {
            return '<span style="color:green">合格</span>'
        }  else if (testResult=='不合格') {
            return '<span style="color:red">不合格</span>'
        }
    }

    function relTestTaskRenderer(e) {
        var record = e.record;
        var relTestTaskId = record.relTestTaskId;
        var testNumber = record.testNumber;
        if(!testNumber) {
            return "";
        }
        var testType = record.testType;
        if(!testType) {
            testType='专项测试'
        }
        return '<span style="cursor: pointer;color: #0a7ac6" onclick="jumpTestTaskDetail(\'' + relTestTaskId+'\',\''+ testType + '\')">' + testNumber + '</span>';
    }

    function jumpTestTaskDetail(relTestTaskId,testType) {
        var action = "detail";
        var url = jsUseCtxPath + "/drbfm/testTask/testTaskEditPage.do?action=" + action + "&applyId=" + relTestTaskId + "&status=SUCCESS_END"+"&testType="+testType;
        var winObj = window.open(url);
    }

    function validStatusRenderer(e) {
        var record = e.record;
        var validStatus = record.validStatus;
        if(!validStatus) {
            return "";
        }
        if (validStatus=='有效') {
            return '<span style="color:green">有效</span>'
        }  else if (validStatus=='作废') {
            return '<span style="color:red">作废</span>'
        }
    }

    function overallResultRenderer(e) {
        var record = e.record;
        var overallResult = record.overallResult;
        if(!overallResult) {
            return "";
        }
        if (overallResult=='符合') {
            return '<span style="color:green">符合</span>'
        }  else if (overallResult=='不符合') {
            return '<span style="color:red">不符合</span>'
        }
    }

    function testTaskStatusRenderer(e) {
        var record = e.record;
        var status = record.status;

        var arr = [ {'key' : 'DRAFTED','value' : '草稿','css' : 'orange'},
            {'key' : 'RUNNING','value' : '审批中','css' : 'green'},
            {'key' : 'SUCCESS_END','value' : '审批完成','css' : 'blue'},
            {'key' : 'DISCARD_END','value' : '申请作废','css' : 'red'}
        ];

        return $.formatItemValue(arr,status);
    }


    function txRenderer(e) {
        var record = e.record;
        var txLevel = record.maxlv;
        var res = "";
        if (txLevel == "1") {
            res = "专控特性";
        } else if (txLevel == "2") {
            res = "一般特性";
        } else if (txLevel == "3") {
            res = "重要特性";
        }else if (txLevel == "4") {
            res = "关键特性";
        }
        return res;
    }
</script>
<redxun:gridScript gridId="quotaEvaluateListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl="" />
</body>
</html>
