<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title></title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>

<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="processInfo" class="mini-button" style="display: none" onclick="processInfo()">流程信息</a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin: 0 auto; width: 100%;">
        <form id="formBusiness" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <input id="businessStatus" name="businessStatus" class="mini-hidden"/>
            <input id="businessType" name="businessType" class="mini-hidden"/>
            <input id="INST_ID_" name="INST_ID_" class="mini-hidden"/>
            <input id="instId" name="instId" class="mini-hidden"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0">
                <caption style="font-weight: bold">
                    混投台账作废审批
                </caption>
                <tr>
                    <td style="text-align: center;width: 15%">申请人：</td>
                    <td style="min-width:170px">
                        <input id="applyUserId" name="applyUserId" textname="applyUser"
                               property="editor" class="mini-user rxc" plugins="mini-user"
                               style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true" enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 20%">申请时间：</td>
                    <td style="min-width:170px">
                        <input id="applyTime" name="applyTime" class="mini-datepicker" format="yyyy-MM-dd" allowInput="false"
                               valueType="string" showTime="false" showOkButton="true" showClearButton="false" style="width:98%;" enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">备注：</td>
                    <td colspan="3">
						<textarea id="remarks" name="remarks" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:99.1%;height:100px;line-height:25px;" datatype="varchar" allowinput="true"
                                  mwidth="80" wunit="%" mheight="200" hunit="px"></textarea>
                    </td>
                </tr>
                <%--itemListGrid--------------------------------------------------------------------%>
                <tr>
                    <td style="text-align: center;height: 400px">混投明细：</td>
                    <td colspan="3">
                        <div class="mini-toolbar" id="itemButtons">
                            <a id="removeItem" class="mini-button" style="margin-bottom: 5px;margin-top: 5px" onclick="removeItem" enabled="false">删除记录</a>
                            <a id="importItem" class="mini-button" style="margin-bottom: 5px;margin-top: 5px" onclick="openSelectWindow"
                               enabled="false">选择记录</a>
                        </div>
                        <div id="itemListGrid" class="mini-datagrid" style="height: 85%" allowResize="true" allowCellWrap="false"
                             showCellTip="true" autoload="true" idField="id" multiSelect="false" showPager="false"
                             showColumnsMenu="false" allowcelledit="true" allowcellselect="true" allowAlternating="true"
                             oncellvalidation="onCellValidation">
                            <div property="columns">
                                <div type="checkcolumn" width="40"></div>
                                <div field="orderNo_item" width="100" headerAlign="center" align="center" renderer="render">订单信息</div>
                                <div field="batchNo_item" width="200" headerAlign="center" align="center" renderer="render">批次号</div>
                                <div field="orderInputCount_item" width="70" headerAlign="center" align="center" renderer="render">订单数量</div>
                                <div field="materialCodeOfMachine_item" width="100" headerAlign="center" align="center" renderer="render">机型物料号</div>
                                <div field="materialDescriptionOfMachine_item" width="250" headerAlign="center" align="center" renderer="render">
                                    机型物料描述
                                </div>
                                <div field="materialCode_item" width="100" headerAlign="center" align="center" renderer="render">物料号</div>
                                <div field="materialDescription_item" width="250" headerAlign="center" align="center" renderer="render">物料描述</div>
                                <div field="materialCount_item" width="60" headerAlign="center" align="center" renderer="render">数量</div>
                                <div field="isMixedInput_item" width="70" headerAlign="center" align="center" renderer="render">是否混投</div>
                                <div field="remarks1_item" width="250" headerAlign="center" align="center" renderer="render">分配车号或异常说明</div>
                                <div field="remarks2_item" width="250" headerAlign="center" align="center" renderer="render">混投依据或实际订单数量</div>
                                <div field="repUserName_item" width="60" headerAlign="center" align="center" renderer="render">责任人</div>
                                <div field="signYear_item" width="60" headerAlign="center" align="center" renderer="render">年份</div>
                                <div field="signMonth_item" width="60" headerAlign="center" align="center" renderer="render">月份</div>
                                <div field="remarks_item" width="200" headerAlign="center" align="center" renderer="render">备注</div>
                            </div>
                        </div>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
<%-------------------------------%>
<div id="selectWindow" title="选择主数据" class="mini-window" style="width:1500px;height:650px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
    <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px" borderStyle="border-left:0;border-top:0;border-right:0;">
        <span style="font-size: 14px;color: #777">订单信息: </span>
        <input class="mini-textbox" width="100" id="orderNo" style="margin-right: 15px"/>
        <span style="font-size: 14px;color: #777">批次号: </span>
        <input class="mini-textbox" width="200" id="batchNo" style="margin-right: 15px"/>
        <span style="font-size: 14px;color: #777">机型物料号: </span>
        <input class="mini-textbox" width="100" id="materialCodeOfMachine" style="margin-right: 15px"/>
        <span style="font-size: 14px;color: #777">物料号: </span>
        <input class="mini-textbox" width="100" id="materialCode" style="margin-right: 15px"/>
        <span style="font-size: 14px;color: #777">责任人: </span>
        <input class="mini-textbox" width="60" id="repUserName" style="margin-right: 15px"/>
        <span style="font-size: 14px;color: #777">年份: </span>
        <input class="mini-textbox" width="60" id="signYear" style="margin-right: 15px"/>
        <span style="font-size: 14px;color: #777">月份: </span>
        <input class="mini-textbox" width="60" id="signMonth" style="margin-right: 15px"/>
        <a class="mini-button" plain="true" onclick="searchFrmMasteData()">查询</a>
        <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearFormMasteData()">清空查询</a>
    </div>
    <div class="mini-fit">
        <div id="masterDataGrid" idField="id" class="mini-datagrid" style="width: 100%; height: 100%;"
             allowResize="false" multiSelect="true"
             showColumnsMenu="false" sizeList="[20,50]" pageSize="20"
             allowAlternating="true" pagerButtons="#pagerButtons"
             url="${ctxPath}/serviceEngineering/core/mixedinput/masterdataListQuery.do">
            <div property="columns">
                <div type="checkcolumn" width="30"></div>
                <div field="businessStatus" width="40" headerAlign="center" align="center">状态</div>
                <div field="orderNo" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">订单信息</div>
                <div field="batchNo" width="200" headerAlign="center" align="center" allowSort="true" renderer="render">批次号</div>
                <div field="orderInputCount" width="70" headerAlign="center" align="center" allowSort="true" renderer="render">订单数量</div>
                <div field="materialCodeOfMachine" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">机型物料号</div>
                <div field="materialDescriptionOfMachine" width="250" headerAlign="center" align="center" allowSort="true" renderer="render">机型物料描述
                </div>
                <div field="materialCode" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">物料号</div>
                <div field="materialDescription" width="250" headerAlign="center" align="center" allowSort="true" renderer="render">物料描述</div>
                <div field="materialCount" width="40" headerAlign="center" align="center" allowSort="true" renderer="render">数量</div>
                <div field="isMixedInput" width="70" headerAlign="center" align="center" allowSort="true" renderer="render">是否混投</div>
                <div field="remarks1" width="250" headerAlign="center" align="center" allowSort="true" renderer="render">分配车号或异常说明</div>
                <div field="remarks2" width="250" headerAlign="center" align="center" allowSort="true" renderer="render">混投依据或实际订单数量</div>
                <div field="repUserName" width="60" headerAlign="center" align="center" allowSort="true" renderer="render">责任人</div>
                <div field="signYear" width="60" headerAlign="center" align="center" allowSort="true" renderer="render">年份</div>
                <div field="signMonth" width="60" headerAlign="center" align="center" allowSort="true" renderer="render">月份</div>
                <div field="remarks" width="200" headerAlign="center" align="center">备注</div>
            </div>
        </div>
    </div>
    <div property="footer" style="padding:5px;height: 35px">
        <table style="width:100%;height: 100%">
            <tr>
                <td style="width:120px;text-align:center;">
                    <input type="button" style="height: 25px;width: 70px" value="确定" onclick="selectMasterDataOK()"/>
                    <input type="button" style="height: 25px;width: 70px" value="取消" onclick="selectMasterDataHide()"/>
                </td>
            </tr>
        </table>
    </div>
</div>
<%--javascript------------------------------------------------------%>
<script type="text/javascript">
    mini.parse();
    var action = "${action}";
    var status = "${status}";
    var formBusiness = new mini.Form("#formBusiness");
    var jsUseCtxPath = "${ctxPath}";
    var itemListGrid = mini.get("itemListGrid");
    var selectWindow = mini.get("selectWindow");
    var masterDataGrid = mini.get("masterDataGrid");
    var businessId = "${businessId}";
    var nodeVarsStr = '${nodeVars}';
    var currentTime = "${currentTime}";
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var currentUserName = "${currentUserName}";
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>" + xzxgwjjxyxgs;
    var codeName = "";
    //..
    $(function () {
        document.getElementById("itemListGrid").style.width = (screen.width - 250) + 'px';
        var url = jsUseCtxPath + "/serviceEngineering/core/mixedinput/getDiscardDetail.do";
        $.post(
            url,
            {businessId: businessId},
            function (json) {
                formBusiness.setData(json);
                var recordItems = JSON.parse(json.recordItems);
                itemListGrid.setData(recordItems);
                if (action == 'detail') {
                    formBusiness.setEnabled(false);
                    $("#detailToolBar").show();
                    //非草稿放开流程信息查看按钮
                    if (status != 'DRAFTED') {
                        $("#processInfo").show();
                    }
                } else if (action == 'edit') {
                    mini.get("removeItem").setEnabled(true);
                    mini.get("importItem").setEnabled(true);
                } else if (action == 'task') {
                    taskActionProcess();
                }
            });
    });
    //..流程引擎调用此方法进行表单数据的获取
    function getData() {
        var formData = _GetFormJsonMini("formBusiness");
        if (formData.SUB_itemListGrid) {
            delete formData.SUB_itemListGrid;
        }
        data = itemListGrid.getData();
        if (data.length > 0) {
            formData.recordItems = data;
        }
        formData.bos = [];
        formData.vars = [];
        formData.businessType = 'discard';
        return formData;
    }
    //..获取任务相关的环境变量，处理表单可见性
    function taskActionProcess() {
        //获取上一环节的结果和处理人
        bpmPreTaskTipInForm();
        //获取环境变量
        if (!nodeVarsStr) {
            nodeVarsStr = "[]";
        }
        var nodeVars = $.parseJSON(nodeVarsStr);
        for (var i = 0; i < nodeVars.length; i++) {
            if (nodeVars[i].KEY_ == 'codeName') {
                codeName = nodeVars[i].DEF_VAL_;
            }
        }
        if (codeName != "A") {//不是编辑中
            formBusiness.setEnabled(false);
            itemListGrid.setAllowCellEdit(false);
        } else {
            mini.get("removeItem").setEnabled(true);
            mini.get("importItem").setEnabled(true);
        }
    }
    //..流程信息浏览
    function processInfo() {
        var instId = $("#instId").val();
        var url = jsUseCtxPath + "/bpm/core/bpmInst/info.do?instId=" + instId;
        _OpenWindow({
            url: url,
            max: true,
            title: decorationManualTopicPSEdit_name,
            width: 800,
            height: 600
        });
    }
    //..保存草稿
    function saveBusiness(e) {
        window.parent.saveDraft(e);
    }
    //..启动流程
    function startBusinessProcess(e) {
        var formValid = validBusiness();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        window.parent.startProcess(e);
    }
    //..流程中暂存信息（如编制阶段）
    function saveBusinessInProcess() {
        var formData = _GetFormJsonMini("formBusiness");
        if (formData.SUB_itemListGrid) {
            delete formData.SUB_itemListGrid;
        }
        formData.bos = [];
        formData.vars = [];
        $.ajax({
            url: jsUseCtxPath + '/serviceEngineering/core/mixedinput/saveBusinessDiscard.do',
            type: 'post',
            async: false,
            data: mini.encode(formData),
            contentType: 'application/json',
            success: function (data) {
                if (data) {
                    var message = "";
                    if (data.success) {
                        message = '数据保存成功';
                    } else {
                        message = '数据保存失败' + data.message;
                    }
                    mini.alert(message, '提示信息', function () {
                        window.location.reload();
                    });
                }
            }
        });
    }
    //..流程中的审批或者下一步
    function businessApprove(e) {
        var businessStatus = mini.get("businessStatus");
        //编制阶段的下一步需要校验表单必填字段
        if (codeName == "A") {
            var formValid = validBusiness();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }
        window.parent.approve();
    }
    //..
    function validBusiness() {
        if (itemListGrid.data.length == 0) {
            return {"result": false, "message": '混投明细不能为空'};
        }
        var alreadySet = new Set();
        for (var i = 0, l = itemListGrid.data.length; i < l; i++) {
            alreadySet.add(itemListGrid.data[i].REF_ID_item);
        }
        if (alreadySet.size != itemListGrid.data.length) {
            return {"result": false, "message": "混投明细的选择不允许出现重复"};
        }
        //明细表单验证
        itemListGrid.validate();
        if (!itemListGrid.isValid()) {
            var error = itemListGrid.getCellErrors()[0];
            itemListGrid.beginEditCell(error.record, error.column);
            return {"result": false, "message": error.column.header + error.errorText};
        }
        return {"result": true};
    }
    //..列表验证
    function onCellValidation(e) {
        if (e.field == 'orderNo_item' && !e.value) {
            e.isValid = false;
            e.errorText = '不能为空';
        }
        if (e.field == 'batchNo_item' && !e.value) {
            e.isValid = false;
            e.errorText = '不能为空';
        }
        if (e.field == 'orderInputCount_item' && !e.value) {
            e.isValid = false;
            e.errorText = '不能为空';
        }
        if (e.field == 'materialCodeOfMachine_item' && !e.value) {
            e.isValid = false;
            e.errorText = '不能为空';
        }
        if (e.field == 'materialDescriptionOfMachine_item' && !e.value) {
            e.isValid = false;
            e.errorText = '不能为空';
        }
        if (e.field == 'materialCode_item' && !e.value) {
            e.isValid = false;
            e.errorText = '不能为空';
        }
        if (e.field == 'materialDescription_item' && !e.value) {
            e.isValid = false;
            e.errorText = '不能为空';
        }
        if (e.field == 'materialCount_item' && !e.value) {
            e.isValid = false;
            e.errorText = '不能为空';
        }
        if (e.field == 'isMixedInput_item' && !e.value) {
            e.isValid = false;
            e.errorText = '不能为空';
        }
        if (e.field == 'remarks1_item' && !e.value) {
            e.isValid = false;
            e.errorText = '不能为空';
        }
        if (e.field == 'remarks2_item' && !e.value) {
            e.isValid = false;
            e.errorText = '不能为空';
        }
        if (e.field == 'repUserName_item' && !e.value) {
            e.isValid = false;
            e.errorText = '不能为空';
        }
        if (e.field == 'signYear_item' && !e.value) {
            e.isValid = false;
            e.errorText = '不能为空';
        }
        if (e.field == 'signMonth_item' && !e.value) {
            e.isValid = false;
            e.errorText = '不能为空';
        }
    }
    //..
    function render(e) {
        if (e.value != null && e.value != "") {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            return html;
        }
    }
    //..
    function removeItem() {
        var row = itemListGrid.getSelected();
        if (!row) {
            mini.alert("请选中一条记录");
            return;
        }
        delRowGrid("itemListGrid");
    }
    //..
    function openSelectWindow() {
        selectWindow.show();
        searchFrmMasteData();
    }
    //..
    function searchFrmMasteData() {
        var queryParam = [];
        var orderNo = $.trim(mini.get("orderNo").getValue());
        if (orderNo) {
            queryParam.push({name: "orderNo", value: orderNo});
        }
        var batchNo = $.trim(mini.get("batchNo").getValue());
        if (batchNo) {
            queryParam.push({name: "batchNo", value: batchNo});
        }
        var materialCodeOfMachine = $.trim(mini.get("materialCodeOfMachine").getValue());
        if (materialCodeOfMachine) {
            queryParam.push({name: "materialCodeOfMachine", value: materialCodeOfMachine});
        }
        var materialCode = $.trim(mini.get("materialCode").getValue());
        if (materialCode) {
            queryParam.push({name: "materialCode", value: materialCode});
        }
        var repUserName = $.trim(mini.get("repUserName").getValue());
        if (repUserName) {
            queryParam.push({name: "repUserName", value: repUserName});
        }
        var signYear = $.trim(mini.get("signYear").getValue());
        if (signYear) {
            queryParam.push({name: "signYear", value: signYear});
        }
        var signMonth = $.trim(mini.get("signMonth").getValue());
        if (signMonth) {
            queryParam.push({name: "signMonth", value: signMonth});
        }
        queryParam.push({name: "businessStatus", value: '有效'});
        var data = {};
        data.filter = mini.encode(queryParam);
        data.pageIndex = masterDataGrid.getPageIndex();
        data.pageSize = masterDataGrid.getPageSize();
        data.sortField = masterDataGrid.getSortField();
        data.sortOrder = masterDataGrid.getSortOrder();
        masterDataGrid.load(data);
    }
    //..
    function clearFormMasteData() {
        mini.get("orderNo").setValue("");
        mini.get("batchNo").setValue("");
        mini.get("materialCodeOfMachine").setValue("");
        mini.get("materialCode").setValue("");
        mini.get("repUserName").setValue("");
        mini.get("signYear").setValue("");
        mini.get("signMonth").setValue("");
        searchFrmMasteData();
    }
    //..
    function selectMasterDataOK() {
        var selects = masterDataGrid.getSelecteds();
        if (selects.length > 0) {
            for (var i = 0, l = selects.length; i < l; i++) {
                var newRow = {};
                newRow.orderNo_item = selects[i].orderNo;
                newRow.batchNo_item = selects[i].batchNo;
                newRow.orderInputCount_item = selects[i].orderInputCount;
                newRow.materialCodeOfMachine_item = selects[i].materialCodeOfMachine;
                newRow.materialDescriptionOfMachine_item = selects[i].materialDescriptionOfMachine;
                newRow.materialCode_item = selects[i].materialCode;
                newRow.materialDescription_item = selects[i].materialDescription;
                newRow.materialCount_item = selects[i].materialCount;
                newRow.isMixedInput_item = selects[i].isMixedInput;
                newRow.remarks1_item = selects[i].remarks1;
                newRow.remarks2_item = selects[i].remarks2;
                newRow.repUserName_item = selects[i].repUserName;
                newRow.signYear_item = selects[i].signYear;
                newRow.signMonth_item = selects[i].signMonth;
                newRow.remarks_item = selects[i].remarks;
                newRow.REF_ID_item = selects[i].id;
                itemListGrid.addRow(newRow);
            }
        }
        selectWindow.hide();
    }
    //..
    function selectMasterDataHide() {
        selectWindow.hide();
        masterDataGrid.load();
    }
</script>
</body>
</html>
