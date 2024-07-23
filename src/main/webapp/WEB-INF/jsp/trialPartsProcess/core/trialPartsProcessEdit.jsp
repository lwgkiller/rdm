<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html >
<html>
<head>
    <title>试制零部件进度跟踪</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
</head>
<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="saveBaseInfo" class="mini-button" style="display: none" onclick="saveBaseInfo()">保存</a>
        <a id="processInfo" class="mini-button" style="display: none" onclick="processInfo()">流程信息</a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit" id="content">
    <div class="form-container" style="margin: 0 auto">
        <%--整个表单--%>
        <form id="trailPartsForm" method="post">
            <input class="mini-hidden" id="id" name="id"/>
            <input class="mini-hidden" id="CREATE_BY_" name="CREATE_BY_"/>
            <input id="instId" name="instId" class="mini-hidden"/>
            <p style="text-align: center;font-size: 20px;font-weight: bold;margin-top: 20px">试制零部件进度跟踪</p>
            <div class="fieldset-body" style="margin: 0px 0px 0px 0px">
                <table class="table-detail grey" cellspacing="1" cellpadding="0">
                    <label style="font-size:17px">
                        基础信息<span id="tips"  style="display: none;color: red">----后续节点审批人员需指定“试制批次确认人员（制造管理部）”--->“质量监督确认人员（质量保证部）”--->“故障问题确认人员（服务管理部）”，指定人员顺序对应节点审批顺序（可点击“流程图”查看详情）</span>
                    </label>
                    <tr>
                        <td style="text-align: center;width: 15%">申请人：</td>
                        <td style="width: 21%">
                            <input id="creater" name="creater" class="mini-textbox" enabled="false" required="true" style="width:99%;"/>
                        </td>
                        <td style="text-align: center;width: 15%">申请部门：</td>
                        <td style="width: 21%;min-width:170px">
                            <input id="deptName" name="deptName"  class="mini-textbox" enabled="false" required="true" style="width:99%;" />
                        </td>
                        <td style="text-align: center;width: 15%">机型：</td>
                        <td style="width: 21%;min-width:170px">
                            <input id="model" name="model"  class="mini-textbox" required="true" style="width:99%;" />
                        </td>
                    </tr>
                    <tr>
                        <td style="text-align: center;width: 15%">分类：</td>
                        <td style="width: 21%;min-width:170px">
                            <input id="category" name="category"  class="mini-combobox" style="width:99%;"
                                   data= '[{key:"电气",value:"电气"},{key:"液压",value:"液压"},{key:"动力",value:"动力"},{key:"转台",value:"转台"},{key:"底盘",value:"底盘"},{key:"覆盖件",value:"覆盖件"},{key:"工作装置",value:"工作装置"}]';
                                   textField="value" valueField="key"
                                   required="true" allowInput="false"
                                   multiSelect="false"/>
                        </td>
                        <td style="text-align: center;width: 15%">试制数量：</td>
                        <td style="width: 21%;min-width:170px">
                            <input id="trialNum" name="trialNum" vtype="int;range:1,100" class="mini-textbox" required="true" style="width:99%;" />
                        </td>
                        <td style="text-align: center;width: 15%">试制单号：</td>
                        <td style="width: 21%">
                            <input id="trialBillNum" name="trialBillNum" class="mini-textbox" required="true" style="width:99%;"/>
                        </td>
                    </tr>
                    <tr>
                        <td style="text-align: center;width: 15%">物料号<span style="color: red">(更改前)</span>：</td>
                        <td style="width: 21%;min-width:170px">
                            <input id="trialMaterielNumBe" name="trialMaterielNumBe"  class="mini-textbox" required="true" style="width:99%;" />
                        </td>
                        <td style="text-align: center;width: 15%">物料描述<span style="color: red">(更改前)</span>：</td>
                        <td style="width: 21%;min-width:170px">
                            <input id="trialMaterialDescBe" name="trialMaterialDescBe" class="mini-textbox" required="true" style="width:100%;"/>
                        </td>
                        <td style="text-align: center;width: 15%">供应商<span style="color: red">(更改前)</span>：</td>
                        <td style="width: 21%;min-width:170px">
                            <input id="supplierBe" name="supplierBe" class="mini-textbox" required="true" style="width:100%;"/>
                        </td>
                    </tr>
                    <tr>
                        <td style="text-align: center;width: 15%">物料号<span style="color: green">(更改后)</span>：</td>
                        <td style="width: 21%;min-width:170px">
                            <input id="trialMaterielNumAf" name="trialMaterielNumAf"  class="mini-textbox" required="true" style="width:99%;" />
                        </td>
                        <td style="text-align: center;width: 15%">物料描述<span style="color: green">(更改后)</span>：</td>
                        <td style="width: 21%;min-width:170px">
                            <input id="trialMaterialDescAf" name="trialMaterialDescAf" class="mini-textbox" required="true" style="width:100%;"/>
                        </td>
                        <td style="text-align: center;width: 15%">供应商<span style="color: green">(更改后)</span>：</td>
                        <td style="width: 21%;min-width:170px">
                            <input id="supplierAf" name="supplierAf" class="mini-textbox" required="true" style="width:100%;"/>
                        </td>
                    </tr>
                    <tr>
                        <td style="text-align: center;width: 15%">互换性：</td>
                        <td style="width: 21%;min-width:170px">
                            <input id="interchange" name="interchange" class="mini-textbox" required="true" style="width:100%;"/>
                        </td>
                        <td style="text-align: center;width: 15%">主要差异性：</td>
                        <td style="width: 21%;min-width:170px">
                            <input id="differ" name="differ" class="mini-textbox" required="true" style="width:100%;"/>
                        </td>
                        <td style="text-align: center;width: 15%">试制计划上线日期：</td>
                        <td style="width: 21%;min-width:170px">
                            <input id="planDate" name="planDate" class="mini-datepicker" dateFormat="yyyy-MM-dd" required="true" style="width:99%;"/>
                        </td>
                    </tr>
                    <tr>
                        <td style="text-align: center;width: 15%">后续节点审批人员：</td>
                        <td style="width: 21%;min-width:170px">
                            <input id="memberInfo" name="memberInfo" textname="memberNames"
                                   class="mini-user rxc"
                                   plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="节点审批人员"
                                   length="1000" maxlength="1000"
                                   mainfield="no" single="false"/>
                        </td>
                        <td style="text-align: center;width: 15%">故障数：</td>
                        <td style="width: 21%;min-width:170px">
                            <input id="faultNum" name="faultNum" class="mini-textbox" vtype="float" enabled="false" style="width:100%;"/>
                        </td>
                        <td style="text-align: center;width: 15%">故障率(%)：</td>
                        <td style="width: 21%;min-width:170px">
                            <input id="faultRate" name="faultRate" class="mini-textbox" vtype="float" enabled="false" style="width:100%;"/>
                        </td>
                    </tr>
                </table>
            </div>
            <div class="fieldset-body" style="margin: 5px 0px 0px 0px">
                <table>
                    <tr>
                        <td width="50%">
                            <div class="mini-toolbar" >
                                <label style="font-size:17px">
                                    试制批次
                                </label>
                                <li id="trialPartsButtons" style="display: inline-block">
                                    <a class="mini-button"   plain="true" onclick="addTrialBatch">新建</a>
                                    <a class="mini-button"   plain="true" onclick="editTrialBatch">编辑</a>
                                    <a class="mini-button btn-red"  plain="true" onclick="deleteTrialBatch">删除</a>
                                </li>
                            </div>
                            <div id="gridTrialBatchInfo" class="mini-datagrid"  allowResize="false" style="height:250px;"
                                 url="${ctxPath}/trialPartsProcess/core/trialPartsProcess/queryBatchInfo.do?applyId=${applyId}" autoload="true"
                                 idField="id" allowCellSelect="true" allowSortColumn="false" onselectionchanged="onSelectionChanged"
                                 selectOnLoad="true" showColumnsMenu="false" allowAlternating="true" showPager="false">
                                <div property="columns">
                                    <div type="checkcolumn" width="5%"></div>
                                    <div field="id"  headerAlign="left" visible="false">id</div>
                                    <div id="trialBatch" field="trialBatch" displayField="trialBatch" headerAlign="center" align="center" width="25%" >试制批次
                                    </div>
                                    <div field="trialBatchNum"  displayField="trialBatchNum" headerAlign="center" align="center" width="20%">批次试制数量
                                    </div>
                                    <div field="trialProcess"  displayField="trialProcess" headerAlign="center" align="center" width="20%">试制进度(%)
                                    </div>
                                    <div field="finishDate"  headerAlign="center" align="center" width="25%" dateFormat="yyyy-MM-dd">上线日期
                                    </div>
                                </div>
                            </div>
                        </td>
                        <td width="2%"></td>
                        <td width="48%">
                            <label style="font-size:17px">
                                附属信息
                            </label>
                            <li id="trialPartsDetail" style="display: inline-block">
                                <a class="mini-button"   plain="true" onclick="saveTrialDetail">保存</a>
                                <a class="mini-button"   plain="true" onclick="addTrialDetail">新建</a>
                                <a class="mini-button btn-red"  plain="true" onclick="deleteTrialDetail">删除</a>
                            </li>
                            <div id="gridTrialBatchDetail" class="mini-datagrid"  allowResize="false" style="height:250px;"
                                 url="${ctxPath}/trialPartsProcess/core/trialPartsProcess/queryBatchDetailInfo.do?applyId=${applyId}"
                                 idField="id" allowCellEdit="true" allowCellSelect="true" allowSortColumn="false"
                                 showColumnsMenu="false" allowAlternating="true" showPager="false">
                                <div property="columns">
                                    <div type="checkcolumn" width="5%"></div>
                                    <div field="id"  headerAlign="left" visible="false">id</div>
                                    <div id="trialBatchDetail" field="trialBatch" vtype="required" headerAlign="left"
                                         visible="false" >试制批次</div>
                                    <div field="vinCode" headerAlign="center" align="center" vtype="required" width="25%">车辆编号
                                        <input property="editor" class="mini-textbox" />
                                    </div>
                                    <div field="workDuration"  headerAlign="center" vtype="required" align="center" width="20%">工作时长(h)
                                        <input property="editor" class="mini-textbox" />
                                    </div>
                                    <div field="deadline"  headerAlign="center" align="center" vtype="required" width="25%" dateFormat="yyyy-MM-dd">截至日期
                                        <input property="editor" class="mini-datepicker" />
                                    </div>
                                </div>
                            </div>
                        </td>
                    </tr>
                </table>
            </div>
            <div class="fieldset-body" style="margin: 5px 0px 0px 0px">
                <div class="mini-toolbar" id="trailPartsFileUpload">
                    <label style="font-size:17px">
                        附件信息
                    </label>
                    <a id="addTrailPartsFile" class="mini-button" onclick="addTrailPartsFile('${applyId}')">上传附件</a>
                </div>
                <div id="fileListGrid" class="mini-datagrid"
                     allowResize="false"
                     idField="id"
                     url="${ctxPath}/trialPartsProcess/core/trialPartsProcess/fileList.do?applyId=${applyId}"
                     autoload="true"
                     multiSelect="true" showPager="false" showColumnsMenu="false"
                     allowAlternating="true"
                     style="height:200px;">
                    <div property="columns">
                        <div type="indexcolumn" width="5%" align="center">序号</div>
                        <div field="fileName" width="20%" headerAlign="center" align="center">附件名称</div>
                        <div field="fileType" width="20%" headerAlign="center" align="center">附件类型</div>
                        <div field="fileDesc" width="35" headerAlign="center" align="center">附件描述</div>
                        <div field="fileSize" width="10%" headerAlign="center" align="center">附件大小</div>
                        <div field="action" width="10%" headerAlign='center' align="center"
                             renderer="operationRenderer">操作
                        </div>
                    </div>
                </div>
            </div>
        </form>
        <div id="editWindow" class="mini-window" title="Window" style="width:1300px;"
             showModal="true" allowResize="true" allowDrag="true">
            <div id="editform" class="form" >
                <input class="mini-hidden" name="id"/>
                <table style="width:100%;">
                    <tr>
                        <td style="width:10%;">试制批次：</td>
                        <td style="width:15%;"><input name="trialBatch" class="mini-textbox" required="true" /></td>
                        <td style="width:10%;">批次试制数量：</td>
                        <td style="width:15%;"><input name="trialBatchNum" class="mini-textbox" onvaluechanged="trialProcessCal" vtype="int;range:1,100"  required="true"/></td>
                        <td style="width:10%;">试制进度(%)：</td>
                        <td style="width:15%;"><input name="trialProcess" class="mini-textbox" required="true" emptyText="填写批次试制数量后自动计算" allowInput="flase"/></td>
                        <td style="width:10%;">上线日期：</td>
                        <td style="width:15%;"><input name="finishDate" class="mini-datepicker" required="true"/></td>
                    </tr>
                    <tr>
                        <td style="text-align:right;padding-top:5px;padding-right:20px;" colspan="8">
                            <a class="mini-button" href="javascript:saveTrialBatch()">保存</a>
                            <a class="mini-button btn-red" href="javascript:cancelRow()">取消</a>
                        </td>
                    </tr>
                </table>
            </div>
        </div>
    </div>
</div>
<script>
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var nodeVarsStr = '${nodeVars}';
    var action = "${action}";
    var status = "${status}";
    var trailPartsForm = new mini.Form("trailPartsForm");
    var gridTrialBatchInfo = mini.get("gridTrialBatchInfo");
    var gridTrialBatchDetail = mini.get("gridTrialBatchDetail");
    var fileListGrid = mini.get("fileListGrid");
    var editWindow = mini.get("editWindow");
    var applyId = "${applyId}";
    var instId = "${instId}";
    var currentUserId = "${currentUserId}";
    var currentUserName = "${currentUserName}";
    var currentTime = "${currentTime}";
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>" + xzxgwjjxyxgs;
    var stageName = "";
    $(function () {
        var url = jsUseCtxPath + "/trialPartsProcess/core/trialPartsProcess/getJson.do";
        $.post(
            url,
            {id: applyId},
            function (json) {
                trailPartsForm.setData(json);
            });
        if (action == 'detail') {
            trailPartsForm.setEnabled(false);
            $("#detailToolBar").show();
            $("#processInfo").show();
            $("#trialPartsButtons").hide();
            $("#trialPartsDetail").hide();
            $("#addTrailPartsFile").hide();
            gridTrialBatchDetail.setAllowCellEdit(false);
        } else if (action == 'task') {
            taskActionProcess();
        }else if (action == "edit") {
            $("#trialPartsButtons").hide();
            $("#trialPartsDetail").hide();
            $("#addTrailPartsFile").hide();
            $("#tips").show();
        }else if (action == 'maintenance') {
            trailPartsForm.setEnabled(false);
            $("#detailToolBar").show();
            $("#processInfo").show();
            $("#saveBaseInfo").show();
            $("#trialPartsButtons").hide();
            // 故障数&故障率可填写
            mini.get("faultNum").setEnabled(true);
            mini.get("faultRate").setEnabled(true);
        }
    });

    //试制批次数量变化时设置试制进度
    function trialProcessCal(e) {
        if (e.value) {
            var form = new mini.Form("#editform");
            var data = form.getData();
            var total = trailPartsForm.getData().trialNum;
            if (total && total !=0) {
                var processPer = parseInt(e.value) / parseInt(total) * 100;
                data.trialProcess = processPer.toFixed(2);
                form.setData(data);
            }
        }
    }
    function getData() {
        var formData = _GetFormJsonMini("trailPartsForm");
        if (formData.SUB_fileListGrid) {
            if (formData.SUB_fileListGrid) {
                delete formData.SUB_fileListGrid;
            }
            if (formData.SUB_demandGrid) {
                delete formData.SUB_demandGrid;
            }
            formData.stageName = stageName;
            return formData;
        }
    }

    //保存草稿
    function saveDraft(e) {
        var resultData = startCheck();
        if (!resultData.result) {
            mini.alert(resultData.message);
            return;
        }
        window.parent.saveDraft(e);
    }

    //发起流程
    function startProcess(e) {
        var resultData = startCheck();
        if (!resultData.result) {
            mini.alert(resultData.message);
            return;
        }
        window.parent.startProcess(e);
    }

    //下一步审批
    function projectApprove() {
        //编制阶段的下一步需要校验表单必填字段
        if (stageName === 'stage_sec') {
            var data = gridTrialBatchInfo.getData();
            if (data.length == 0) {
                mini.alert("请填写试制批次信息!");
                return;
            }
        //     验证试制数量和批次试制数量之间的关系是否正确（2节点驳回1修改试制数量情况下可能发生）
            var total = parseInt(trailPartsForm.getData().trialNum);
            var rows = gridTrialBatchInfo.getData();
            var now_total=0;
            for (i=0;i<rows.length;i++) {
                var row = rows[i];
                var trialBatchNum = parseInt(row.trialBatchNum);
                var temp = (trialBatchNum / total).toFixed(2)*100;
                if (temp != parseInt(row.trialProcess)) {
                    mini.alert("试制批次数量有误，请重新编辑！");
                    return;
                }
                now_total += trialBatchNum;
            }

            if (now_total >total) {
                mini.alert("试制批次数量大于总试制数量，请重新编辑！");
                return;
            }
        }

        if (stageName === 'stage_thr') {
            var formValid = validCaseFile();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }

        // 节点1和节点四下一步后保存节点信息
        if (stageName === 'stage_fir'||stageName === 'stage_for') {
            var resultData = startCheck();
            if (!resultData.result) {
                mini.alert(resultData.message);
                return;
            }
            saveBaseInfo();
        }

        //检查通过
        window.parent.approve();
    }

    function saveBaseInfo() {
        //后期维护过程中附属信息有变动，提升先保存附属信息再保存表单
        var change = gridTrialBatchDetail.getChanges();
        if (change.length != 0) {
            mini.alert("请先保存附属信息！");
            return;
        }
        var form = new mini.Form("#trailPartsForm");
        var data = form.getData();
        $.ajax({
            url: jsUseCtxPath+"/trialPartsProcess/core/trialPartsProcess/saveBaseInfo.do",
            method: 'post',
            contentType: 'application/json',
            data: mini.encode(data),
            success: function (json) {
            }
        });
    }

    // 附件上传约束，暂未指定节点
    function validCaseFile() {
        var demandGridData = fileListGrid.getData();
        if (demandGridData.length != 0) {
            var isUpload = demandGridData.filter(oneRow=> oneRow.fileType==="实物质量评价报告");
            if (isUpload.length != 0) {
                return {"result": true};
            }
        }
        return {"result": false, "message": "请上传实物质量评价报告！"};
    }

    // 流程信息
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
            if (nodeVars[i].KEY_ == 'stageName') {
                stageName = nodeVars[i].DEF_VAL_;
            }
        }
        trailPartsForm.setEnabled(false);
        $("#trialPartsButtons").hide();
        $("#trialPartsDetail").hide();
        $("#addTrailPartsFile").hide();
        gridTrialBatchDetail.setAllowCellEdit(false);
        if (stageName === 'stage_fir') {
            trailPartsForm.setEnabled(true);
            mini.get("faultNum").setEnabled(false);
            mini.get("faultRate").setEnabled(false);
            $("#tips").show();
        }
        if (stageName === 'stage_sec') {
            $("#trialPartsButtons").show();
        }
        if (stageName === 'stage_thr') {
            gridTrialBatchDetail.setAllowCellEdit(true);
            $("#addTrailPartsFile").show();
        }
        if (stageName === 'stage_for') {
            gridTrialBatchDetail.setAllowCellEdit(true);
            $("#addTrailPartsFile").show();
            // 故障数&故障率可填写
            mini.get("faultNum").setEnabled(true);
            mini.get("faultRate").setEnabled(true);
        }
    }

    //新增试制批次信息
    function addTrialBatch() {
        if (!applyId) {
            mini.alert("请先点击‘保存草稿’进行表单的保存！");
            return;
        }
        var row = {};
        gridTrialBatchInfo.addRow(row, 0);
        gridTrialBatchInfo.deselectAll();
        gridTrialBatchInfo.select(row);
        editTrialBatch(row._uid);
    }

    // 编辑试制批次信息
    function editTrialBatch(row_uid) {
        if (!isNaN(row_uid)) {
            editWindow.show();
            var form = new mini.Form("#editform");
            form.clear();
        }else {
            var row = gridTrialBatchInfo.getSelected();
            if (!row) {
                mini.alert("请选择一行数据！");
                return;
            }
            editWindow.show();
            var form = new mini.Form("#editform");
            form.clear();
            form.loading();
            $.ajax({
                url: jsUseCtxPath+"/trialPartsProcess/core/trialPartsProcess/queryBatchInfoById.do?id=" + row.id,
                success: function (text) {
                    form.unmask();
                    var o = mini.decode(text);
                    form.setData(o);
                },
                error: function () {
                    alert("表单加载错误");
                    form.unmask();
                }
            });
        }
    }

    // 取消编辑
    function cancelRow() {
        gridTrialBatchInfo.reload();
        editWindow.hide();
    }

    function saveTrialBatch() {
        var form = new mini.Form("#editform");
        form.validate();
        if (form.isValid()===false) {
            mini.alert("请完整填写表单信息!");
            return;
        }
        var data = form.getData();
        if (data) {
            $.ajax({
                url: jsUseCtxPath + "/trialPartsProcess/core/trialPartsProcess/saveBatchInfo.do?applyId="+applyId,
                method: 'post',
                contentType: 'application/json',
                data: mini.encode(data),
                success: function (result) {
                    if (result.success) {
                        //加载之前，更新附属信息的试制批次信息
                        var rows = gridTrialBatchDetail.findRows();
                        for (let i = 0; i < rows.length; i++) {
                            gridTrialBatchDetail.updateRow(rows[i],{trialBatch:data.trialBatch});
                        }
                        gridTrialBatchDetail.updateRow()
                        saveTrialDetail();
                        gridTrialBatchInfo.load();
                        editWindow.hide();
                    }
                }
            });
        }
    }

    // 编辑试制批次关联附属信息
    function addTrialDetail() {
        var selected = gridTrialBatchInfo.getSelected();
        if (!selected) {
            mini.alert("请先创建试制批次！");
            return;
        }
        var trialBatch = selected.trialBatch;
        if (!trialBatch) {
            mini.alert("请先填写试制信息中试制批次！");
            return;
        }
        var row = {trialBatch:trialBatch};
        gridTrialBatchDetail.addRow(row, 0);
        gridTrialBatchDetail.deselectAll();
        gridTrialBatchDetail.select(row);
    }

    // 保存试制批次关联附属信息
    function saveTrialDetail() {
        gridTrialBatchDetail.validate();
        if (gridTrialBatchDetail.isValid()===false) {
            mini.alert("请完整填写表单信息!");
            return;
        }
        var url = jsUseCtxPath + "/trialPartsProcess/core/trialPartsProcess/saveBatchDetail.do?applyId="+applyId;
        var data = gridTrialBatchDetail.getChanges();
        if (data.length>0) {
            $.ajax({
                url: url,
                method: 'post',
                contentType: 'application/json',
                data: mini.encode(data),
                success: function (json) {
                    var selected = gridTrialBatchInfo.getSelected();
                    if (selected) {
                        gridTrialBatchDetail.load({trialBatch:selected.trialBatch});
                    }
                }
            });
        }
    }

    // 删除试制批次信息
    function deleteTrialBatch() {
        var selected = gridTrialBatchInfo.getSelected();
        if (!selected || selected.length <= 0) {
            mini.alert('请选择一条数据!');
            return;
        }
        var id = selected.id;
        var trialBatch = selected.trialBatch;

        $.ajax({
                url:jsUseCtxPath+"/trialPartsProcess/core/trialPartsProcess/deleteBatchInfo.do?id="+id+"&applyId="+applyId+"&trialBatch="+trialBatch,
                success: function (json) {
                    gridTrialBatchInfo.removeRow(selected);
                    gridTrialBatchInfo.reload();
                }
            }
        );
    }

    // 删除试制批次附属信息
    function deleteTrialDetail() {
        var selected = gridTrialBatchDetail.getSelected();
        if (!selected || selected.length <= 0) {
            mini.alert('请选择一条数据!');
            return;
        }
        var id = selected.id;
        $.ajax({
                url:jsUseCtxPath+"/trialPartsProcess/core/trialPartsProcess/deleteDetailInfo.do?id="+id,
                success: function (json) {
                    gridTrialBatchDetail.removeRow(selected);
                }
            }
        );
    }

    //试制批次切换，请求对应批次的试制附属信息
    function onSelectionChanged(e) {
        var data = gridTrialBatchDetail.getChanges();
        if (data.length > 0) {
            mini.alert("附属信息未保存！");
            return;
        }
        var grid = e.sender;
        var record = grid.getSelected();
        if (record) {
            gridTrialBatchDetail.load({trialBatch: record.trialBatch});
        }
    }

    // 文件上传
    function addTrailPartsFile(applyId) {
        if (!applyId) {
            mini.alert("请先点击‘保存草稿’进行表单的保存！");
            return;
        }
        mini.open({
            title: "文件上传",
            url: jsUseCtxPath + "/trialPartsProcess/core/trialPartsProcess/openUploadWindow.do?applyId=" + applyId,
            width: 750,
            height: 450,
            showModal: false,
            allowResize: true,
            onload: function () {
                var iframe = this.getIFrameEl();
                var projectParams = {};
                projectParams.applyId = applyId;
                var data = {projectParams: projectParams};  //传递上传参数
                iframe.contentWindow.SetData(data);
            },
            ondestroy: function (action) {
                fileListGrid.load();
            }
        });
    }

    function operationRenderer(e) {
        var record = e.record;
        if (!record.id) {
            return "";
        }
        var cellHtml = returnPreviewSpan(record.fileName, record.id, record.applyId, coverContent);
        var downloadUrl = '/trialPartsProcess/core/trialPartsProcess/fileDownload.do';
        if (record) {
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title=' + "下载" + ' style="color:#409EFF;cursor: pointer;" ' +
                'onclick="downLoadFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.applyId + '\',\'' + downloadUrl + '\')">' + "下载" + '</span>';
        } else {
            cellHtml += '&nbsp;&nbsp;&nbsp;<span  title=' + "下载" + ' style="color: silver" >' + "下载" + '</span>';
        }
        if (record && (currentUserId == "1" || record.CREATE_BY_ == currentUserId) && (action == "edit" || stageName == "stage_for" ||stageName == "stage_thr" || action == "maintenance")) {
            var deleteUrl = "/trialPartsProcess/core/trialPartsProcess/deleteFiles.do";
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title=' + "删除" + ' style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.applyId + '\',\'' + deleteUrl + '\')">' + "删除" + '</span>';
        } else {
            cellHtml += '&nbsp;&nbsp;&nbsp;<span  title=' + "删除" + ' style="color: silver" > ' + "删除" + ' </span>';
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
            s = '&nbsp;&nbsp;&nbsp;<span  title=' + "预览" + ' style="color: silver" >' + "预览" + '</span>';
        } else {
            var url = '/trialPartsProcess/core/trialPartsProcess/preview.do?fileType=' + fileType;
            s = '<span  title=' + "预览" + ' style="color:#409EFF;cursor: pointer;" onclick="previewPdf6(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\',\'' + fileType + '\')">' + "预览" + '</span>';
        }
        return s;
    }

    // 删除文件
    function deleteFile(fileName, fileId, formId, urlValue) {
        mini.confirm("确定删除？", "提示",
            function (action) {
                if (action == "ok") {
                    var url = jsUseCtxPath + urlValue;
                    var data = {
                        fileName: fileName,
                        id: fileId,
                        formId: formId
                    };
                    $.ajax({
                        url: url,
                        method: 'post',
                        contentType: 'application/json',
                        data: mini.encode(data),
                        success: function (json) {
                            fileListGrid.load();
                        }
                    });
                }
            }
        );
    }

    // 表单验证
    function startCheck() {
        var resultData = {"result": false, "message": ""};
        var form = new mini.Form("#trailPartsForm");
        var data = form.getData();
        form.validate();
        if (form.isValid()===false) {
            resultData.message = "请完整填写表单信息！";
            return resultData;
        }
        if (data) {
            var members = data.memberNames;
            if (members.split(",").length != 3) {
                resultData.message = "必须指定三个人员进行后续节点审核！";
                return resultData;
            }
        }
        resultData.result = true;
        return resultData;
    }
</script>
</body>
</html>
