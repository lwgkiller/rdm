<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>新品装配建档信息编辑</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/standardManager/standardUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>

<body>
<div id="toolBar" class="topToolBar">
    <div>
        <a id="saveBusiness" class="mini-button" onclick="saveBusiness()">保存</a>
        <a id="closeWindow" class="mini-button btn-red" onclick="CloseWindow()">关闭</a>
    </div>
</div>

<div class="mini-fit">
    <div class="form-container" style="margin:0 auto; width: 100%;padding: 0">
        <form id="businessForm" method="post">
            <input class="mini-hidden" id="id" name="id"/>
            <input class="mini-hidden" id="componentTestAbnormalTag" name="componentTestAbnormalTag"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0" style="height: 100%;width: 100%">
                <caption style="font-weight: bold;font-size: 20px !important;">新品装配建档信息编辑</caption>
                <tr>
                    <td style="text-align: center;width: 15%">产品型号：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="designModel" name="designModel" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%">试制台数：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="testQuantity" name="testQuantity" class="mini-spinner" style="width:98%;" maxValue="9999999"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">说明：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="theExplain" name="theExplain" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%">整机编号：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="pin" name="pin" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">产品种类：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="productCategory" name="productCategory"
                               class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=productCategory"
                               valueField="key" textField="value"/>
                    </td>
                    <td style="text-align: center;width: 15%">产品部门(自动生成)：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="productDepId" name="productDepId" class="mini-dep rxc" plugins="mini-dep"
                               data-options="{'single':'true','config':{'type':'','grouplevel':'','groupid':'','groupidText':''}}"
                               style="width:98%;height:34px" allowinput="false" label="产品部门" textname="productDep" length="500"
                               maxlength="500" minlen="0" single="true" required="false" initlogindep="false" showclose="false"
                               mwidth="160" wunit="px" mheight="34" hunit="px" enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">产品主管：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="projectLeaderId" name="projectLeaderId" textname="projectLeader" class="mini-user rxc"
                               plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="申请人" length="50"
                               mainfield="no" single="true"/>
                    </td>
                    <td style="text-align: center;width: 15%">计划类型：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="planCategory" name="planCategory"
                               class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=planCategory"
                               valueField="key" textField="value"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">订单下达时间：</td>
                    <td style="min-width:170px">
                        <input id="orderReleaseTime" name="orderReleaseTime" class="mini-datepicker" format="yyyy-MM-dd"
                               showTime="false" showOkButton="false" showClearButton="false" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%">样机上线时间：</td>
                    <td style="min-width:170px">
                        <input id="prototypeOnLineTime" name="prototypeOnLineTime" class="mini-datepicker" format="yyyy-MM-dd"
                               showTime="false" showOkButton="false" showClearButton="false" style="width:98%;"/>
                    </td>
                    <%--<td style="text-align: center;width: 15%">物料部装时间：</td>--%>
                    <%--<td style="min-width:170px">--%>
                    <%--<input id="materialDepLoadingTime" name="materialDepLoadingTime" class="mini-datepicker" format="yyyy-MM-dd"--%>
                    <%--showTime="false" showOkButton="false" showClearButton="false" style="width:98%;"/>--%>
                    <%--</td>--%>
                </tr>
                <%--<tr>--%>
                <%--<td style="text-align: center;width: 15%">下车装配时间：</td>--%>
                <%--<td style="min-width:170px">--%>
                <%--<input id="downAssemblyTime" name="downAssemblyTime" class="mini-datepicker" format="yyyy-MM-dd"--%>
                <%--showTime="false" showOkButton="false" showClearButton="false" style="width:98%;"/>--%>
                <%--</td>--%>
                <%--<td style="text-align: center;width: 15%">上车装配时间：</td>--%>
                <%--<td style="min-width:170px">--%>
                <%--<input id="upAssemblyTime" name="upAssemblyTime" class="mini-datepicker" format="yyyy-MM-dd"--%>
                <%--showTime="false" showOkButton="false" showClearButton="false" style="width:98%;"/>--%>
                <%--</td>--%>
                <%--</tr>--%>
                <%--<tr>--%>
                <%--<td style="text-align: center;width: 15%">合车装配时间：</td>--%>
                <%--<td style="min-width:170px">--%>
                <%--<input id="combinedAssemblyTime" name="combinedAssemblyTime" class="mini-datepicker" format="yyyy-MM-dd"--%>
                <%--showTime="false" showOkButton="false" showClearButton="false" style="width:98%;"/>--%>
                <%--</td>--%>
                <%--<td style="text-align: center;width: 15%">工作装置装配时间：</td>--%>
                <%--<td style="min-width:170px">--%>
                <%--<input id="workingDeviceAssemblyTime" name="workingDeviceAssemblyTime" class="mini-datepicker" format="yyyy-MM-dd"--%>
                <%--showTime="false" showOkButton="false" showClearButton="false" style="width:98%;"/>--%>
                <%--</td>--%>
                <%--</tr>--%>
                <tr>
                    <%--<td style="text-align: center;width: 15%">整机调试时间：</td>--%>
                    <%--<td style="min-width:170px">--%>
                    <%--<input id="wholeCommissionTime" name="wholeCommissionTime" class="mini-datepicker" format="yyyy-MM-dd"--%>
                    <%--showTime="false" showOkButton="false" showClearButton="false" style="width:98%;"/>--%>
                    <%--</td>--%>
                    <td style="text-align: center;width: 15%">样机下线时间：</td>
                    <td style="min-width:170px">
                        <input id="prototypeOutLineTime" name="prototypeOutLineTime" class="mini-datepicker" format="yyyy-MM-dd"
                               showTime="false" showOkButton="false" showClearButton="false" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%">样机转序时间：</td>
                    <td style="min-width:170px">
                        <input id="prototypeSequenceTime" name="prototypeSequenceTime" class="mini-datepicker" format="yyyy-MM-dd"
                               showTime="false" showOkButton="false" showClearButton="false" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">实际计划年份：</td>
                    <td style="min-width:170px">
                        <input id="realYear" name="realYear"
                               class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=signYear"
                               valueField="key" textField="value"/>
                    </td>
                </tr>
                <%--///////////////////////////////////////////////////--%>
                <tr>
                    <td colspan="4" style="text-align: center">异常信息：</td>
                </tr>
            </table>
        </form>
        <div class="mini-toolbar" id="itemButtons">
            <a id="addItem" class="mini-button" style="margin-bottom: 5px;margin-top: 5px" onclick="addItem" enabled="false">添加</a>
            <a id="editItem" class="mini-button" style="margin-bottom: 5px;margin-top: 5px" onclick="editItem" enabled="false">编辑</a>
            <a id="detailItem" class="mini-button" style="margin-bottom: 5px;margin-top: 5px" onclick="detailItem" enabled="true">浏览</a>
            <a id="deleteItem" class="mini-button btn-red" style="margin-bottom: 5px;margin-top: 5px" onclick="deleteItem" enabled="false">删除</a>
            <a id="importItem" class="mini-button btn-red" style="margin-bottom: 5px;margin-top: 5px" onclick="openImportWindow"
               enabled="false">导入</a>
            <a id="dingdingItem" class="mini-button btn-red" style="margin-bottom: 5px;margin-top: 5px" onclick="sendDingDing"
               enabled="false">发送钉钉至相关责任人</a>
            <a id="synItem" class="mini-button btn-red" style="margin-bottom: 5px;margin-top: 5px" onclick="synItem"
               enabled="false">同步至新产品导入</a>
        </div>
        <div id="itemListGrid" class="mini-datagrid" style="width: 100%; height: 85%" allowResize="false"
             idField="id" url="${ctxPath}/newproductAssembly/core/kanban/getExceptionList.do?businessId=${businessId}"
             multiSelect="true" showPager="false" showColumnsMenu="false" allowcelledit="true" allowcellselect="true"
             allowAlternating="true">
            <div property="columns">
                <div type="checkcolumn" width="40" trueValue="1" falseValue="0"></div>
                <div field="indexLocal" align="center" headerAlign="center" width="80" renderer="render">本地异常号</div>
                <div field="pin" align="center" headerAlign="center" width="170" renderer="render">整机编号</div>
                <div field="isSyn" align="center" headerAlign="center" width="80" renderer="renderColor">是否同步</div>
                <div field="currentProcessTaskZlgj" align="center" headerAlign="center" width="170" renderer="renderRed">流程状态</div>
                <div field="currentProcessUserZlgj" align="center" headerAlign="center" width="170" renderer="renderRed">流程处理人</div>
                <div field="isClear" align="center" headerAlign="center" width="80" renderer="renderColor">是否闭环</div>
                <div field="exceptionType" align="center" headerAlign="center" width="150" renderer="render">异常类型</div>
                <div field="partsCategory" align="center" headerAlign="center" width="80" renderer="render">部件分类</div>
                <div field="exceptionPart" align="center" headerAlign="center" width="120" renderer="render">异常部件</div>
                <%--<div field="problemLevel" align="center" headerAlign="center" width="80" renderer="render">紧急程度</div>--%>
                <div field="exceptionDescription" align="center" headerAlign="center" width="200" renderer="render">异常描述</div>
                <div field="assemblyNode" align="center" headerAlign="center" width="80" renderer="render">异常节点</div>
                <%--<div field="workingHours" align="center" headerAlign="center" width="80" renderer="render">工作小时</div>--%>
                <%--<div field="workingCondition" align="center" headerAlign="center" width="120" renderer="render">施工工况</div>--%>
                <%--<div field="supplier" align="center" headerAlign="center" width="120" renderer="render">零部件供应商</div>--%>
                <%--<div field="failureRate" align="center" headerAlign="center" width="120" renderer="render">故障率</div>--%>
                <%--<div field="failurePosition" align="center" headerAlign="center" width="120" renderer="render">故障部位</div>--%>
                <div field="repDep" align="center" headerAlign="center" width="120" renderer="render">责任部门</div>
                <div field="repDepLeader" align="center" headerAlign="center" width="120" renderer="render">第一责任人</div>
                <%--<div field="repUser" align="center" headerAlign="center" width="80" renderer="render">问题处理人</div>--%>
                <div field="feedbackPerson" align="center" headerAlign="center" width="80" renderer="render">反馈人</div>
                <%--<div field="feedbackTime" align="center" headerAlign="center" width="100">反馈时间</div>--%>
                <%--<div field="testMethod" align="center" headerAlign="center" width="200" renderer="render">问题排查过程及检测方法</div>--%>
                <div field="disposalMethod" align="center" headerAlign="center" width="200" renderer="render">现场处置方法</div>
                <%--<div field="severity" align="center" headerAlign="center" width="200" renderer="render">问题严重度</div>--%>
                <%--<div field="isImprove" align="center" headerAlign="center" width="80" renderer="render">是否需要改进</div>--%>
                <div field="improvementRequirements" align="center" headerAlign="center" width="200" renderer="render">改进要求</div>
                <%--<div field="noImproveReason" align="center" headerAlign="center" width="200" renderer="render">不改进理由</div>--%>
                <div field="temporaryMeasures" align="center" headerAlign="center" width="200" renderer="render">临时处理措施</div>
                <div field="temporaryTime" align="center" headerAlign="center" width="100">临时处理时间</div>
                <div field="permanentMeasures" align="center" headerAlign="center" width="200" renderer="render">永久解决方案</div>
                <div field="permanentTime" align="center" headerAlign="center" width="120">永久解决方案时间</div>
                <div field="remark" align="center" headerAlign="center" width="200" renderer="render">备注</div>
            </div>
        </div>
    </div>
</div>

<div id="importWindow" title="导入窗口" class="mini-window" style="width:750px;height:280px;" showModal="true" showFooter="false" allowResize="true">
    <p style="display: inline-block;color: red;font-size:15px;vertical-align: middle">
    </p>
    <div style="display: inline-block;float: right;">
        <a id="importBtn" class="mini-button" onclick="importItem()">导入</a>
        <a id="closeWindow" class="mini-button btn-red" onclick="closeImportWindow()">关闭</a>
    </div>
    <div class="mini-fit" style="font-size: 14px;margin-top: 30px">
        <form id="formImport" method="post">
            <table class="table-detail" cellspacing="1" cellpadding="0">
                <tr>
                    <td style="width: 30%">上传模板：</td>
                    <td style="width: 70%;">
                        <a href="#" style="color:blue;text-decoration:underline;" onclick="downImportTemplate()">新品装配异常信息导入模板.xls</a>
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
    var messageCounts = 0;
    var jsUseCtxPath = "${ctxPath}";
    var businessId = "${businessId}";
    var action = "${action}";
    var newproductAssemblyAdmins = "${newproductAssemblyAdmins}";
    var newproductAssemblyExceptionInputers = "${newproductAssemblyExceptionInputers}";
    var currentUserNo = "${currentUserNo}";
    var currentUserName = "${currentUserName}";
    var businessForm = new mini.Form("#businessForm");
    var itemListGrid = mini.get("itemListGrid");
    var importWindow = mini.get("importWindow");
    var currentTime = new Date().format("yyyy-MM-dd hh:mm:ss");
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";

    //..
    $(function () {
        itemListGrid.frozenColumns(0, 6);
        if (businessId) {
            var url = jsUseCtxPath + "/newproductAssembly/core/kanban/queryDataById.do?businessId=" + businessId;
            $.ajax({
                url: url,
                method: 'get',
                success: function (json) {
                    businessForm.setData(json);
                }
            });
        }
        //不同场景的处理
        if (action == 'detail') {
            businessForm.setEnabled(false);
            mini.get("saveBusiness").setEnabled(false);
            itemListGrid.load();
        } else {
            if (currentUserNo == 'admin' || newproductAssemblyAdmins.search(currentUserNo) != -1) {
                mini.get("addItem").setEnabled(true);
                mini.get("editItem").setEnabled(true);
                mini.get("deleteItem").setEnabled(true);
                mini.get("detailItem").setEnabled(true);
                mini.get("importItem").setEnabled(true);
                mini.get("dingdingItem").setEnabled(true);
                mini.get("synItem").setEnabled(true);
            } else if (newproductAssemblyExceptionInputers.search(currentUserNo) != -1) {
                mini.get("addItem").setEnabled(true);
                mini.get("editItem").setEnabled(true);
                mini.get("deleteItem").setEnabled(true);
                mini.get("importItem").setEnabled(true);
                mini.get("synItem").setEnabled(true);
            }
            if (action == 'edit') {
                itemListGrid.load();
            }
        }
    });
    //..
    function saveBusiness() {
        var postData = {};
        postData.id = action == 'copy' ? "" : mini.get("id").getValue();
        postData.designModel = mini.get("designModel").getValue();
        postData.testQuantity = mini.get("testQuantity").getValue();
        postData.theExplain = mini.get("theExplain").getValue();
        postData.pin = mini.get("pin").getValue();
        postData.productCategory = mini.get("productCategory").getValue();
        postData.productDepId = mini.get("productDepId").getValue();
        postData.productDep = mini.get("productDepId").getText();
        postData.projectLeaderId = mini.get("projectLeaderId").getValue();
        postData.projectLeader = mini.get("projectLeaderId").getText();
        postData.planCategory = mini.get("planCategory").getValue();
        postData.orderReleaseTime = mini.get("orderReleaseTime").getText();
        postData.prototypeOnLineTime = mini.get("prototypeOnLineTime").getText();
        postData.prototypeOutLineTime = mini.get("prototypeOutLineTime").getText();
//        postData.materialDepLoadingTime = mini.get("materialDepLoadingTime").getText();
//        postData.downAssemblyTime = mini.get("downAssemblyTime").getText();
//        postData.upAssemblyTime = mini.get("upAssemblyTime").getText();
//        postData.combinedAssemblyTime = mini.get("combinedAssemblyTime").getText();
//        postData.workingDeviceAssemblyTime = mini.get("workingDeviceAssemblyTime").getText();
//        postData.wholeCommissionTime = mini.get("wholeCommissionTime").getText();
        postData.prototypeSequenceTime = mini.get("prototypeSequenceTime").getText();
        postData.realYear = mini.get("realYear").getValue();
        //检查必填项
        var checkResult = saveValidCheck(postData);
        if (!checkResult.success) {
            mini.alert(checkResult.reason);
            return;
        }
        $.ajax({
            url: jsUseCtxPath + "/newproductAssembly/core/kanban/saveBusiness.do",
            type: 'POST',
            contentType: 'application/json',
            data: mini.encode(postData),
            success: function (returnData) {
                if (returnData && returnData.message) {
                    mini.alert(returnData.message, '提示', function () {
                        if (returnData.success) {
                            var url = jsUseCtxPath + "/newproductAssembly/core/kanban/editPage.do?businessId=" +
                                returnData.data + "&action=edit";
                            window.location.href = url;
                        }
                    });
                }
            }
        });
    }
    //..
    function saveValidCheck(postData) {
        var checkResult = {};
        if (!postData.designModel) {
            checkResult.success = false;
            checkResult.reason = '请填写产品型号！';
            return checkResult;
        }
        if (!postData.testQuantity) {
            checkResult.success = false;
            checkResult.reason = '请填写试验台数！';
            return checkResult;
        }
        if (!postData.theExplain) {
            checkResult.success = false;
            checkResult.reason = '请填写说明！';
            return checkResult;
        }
        if (!postData.pin) {
            checkResult.success = false;
            checkResult.reason = '请填写整机编号！';
            return checkResult;
        }
        if (!postData.productCategory) {
            checkResult.success = false;
            checkResult.reason = '请选择产品种类！';
            return checkResult;
        }
        if (!postData.projectLeaderId) {
            checkResult.success = false;
            checkResult.reason = '请选择产品主管！';
            return checkResult;
        }
        if (!postData.planCategory) {
            checkResult.success = false;
            checkResult.reason = '请选择计划类型！';
            return checkResult;
        }
        checkResult.success = true;
        return checkResult;
    }
    //..添加明细
    function addItem() {
        var mainId = mini.get("id").getValue();
        if (!mainId) {
            mini.alert('请先点击‘保存草稿’进行表单的保存！');
            return;
        }
        var url = jsUseCtxPath + "/newproductAssembly/core/kanban/exceptionPage.do?mainId=" + mainId + "&pin=" +
            mini.get("pin").getValue() + "&action=add";
        var winObj = window.open(url, '');
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (itemListGrid) {
                    itemListGrid.reload();
                }
            }
        }, 1000);
    }
    //..编辑明细
    function editItem() {
        var row = itemListGrid.getSelected();
        if (!row) {
            mini.alert("请选中一条记录");
            return;
        }
        if (row.isSyn == '是') {
            mini.alert("已同步的记录不能编辑");
            return;
        }
        var businessId = row.id;
        var url = jsUseCtxPath + "/newproductAssembly/core/kanban/exceptionPage.do?businessId=" + businessId + "&pin=" +
            mini.get("pin").getValue() + "&action=edit";
        var winObj = window.open(url, '');
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (itemListGrid) {
                    itemListGrid.reload();
                }
            }
        }, 1000);
    }
    //..浏览明细
    function detailItem() {
        var row = itemListGrid.getSelected();
        if (!row) {
            mini.alert("请选中一条记录");
            return;
        }
        var businessId = row.id;
        var url = jsUseCtxPath + "/newproductAssembly/core/kanban/exceptionPage.do?businessId=" + businessId + "&action=detail";
        var winObj = window.open(url, '');
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (itemListGrid) {
                    itemListGrid.reload();
                }
            }
        }, 1000);
    }
    //..删除明细
    function deleteItem() {
        var rows = itemListGrid.getSelecteds();
        if (rows.length <= 0) {
            mini.alert("请至少选中一条记录");
            return;
        }
        mini.confirm("确定删除选中记录？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                var rowIds = [];
                var hasSyn = false;
                for (var i = 0, l = rows.length; i < l; i++) {
                    var row = rows[i];
                    if (row.isSyn == '否') {
                        rowIds.push(row.id);
                    } else {
                        hasSyn = true;
                    }
                }
                if (hasSyn == true) {
                    mini.confirm("选中记录中的已同步记录将不被删除！", "提示", function (action) {
                        if (action != 'ok') {
                            return;
                        }
                        else {
                            _SubmitJson({
                                url: jsUseCtxPath + "/newproductAssembly/core/kanban/deleteExceptions.do",
                                method: 'POST',
                                data: {ids: rowIds.join(',')},
                                success: function (returnData) {
                                    if (returnData) {
                                        if (returnData.success) {
                                            itemListGrid.reload();
                                        }
                                    }
                                }
                            });
                        }
                    });
                } else {
                    _SubmitJson({
                        url: jsUseCtxPath + "/newproductAssembly/core/kanban/deleteExceptions.do",
                        method: 'POST',
                        data: {ids: rowIds.join(',')},
                        success: function (returnData) {
                            if (returnData) {
                                if (returnData.success) {
                                    itemListGrid.reload();
                                }
                            }
                        }
                    });
                }
            }
        });
    }
    //..同步至质量改进
    function synItem() {
        var row = itemListGrid.getSelected();
        if (!row) {
            mini.alert("请选中一条记录");
            return;
        }
        if (row.isSyn == '是') {
            mini.alert("已同步的记录不能再次同步");
            return;
        }
        mini.confirm("确定同步选中记录？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                var id = row.id
                _SubmitJson({
                    url: jsUseCtxPath + "/newproductAssembly/core/kanban/synException.do",
                    method: 'POST',
                    data: {id: id, mainId: businessId},
                    success: function (returnData) {
                        if (returnData) {
                            if (returnData.success) {
                                itemListGrid.reload();
                            }
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
    function importItem() {
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
            xhr.open('POST', jsUseCtxPath + '/newproductAssembly/core/kanban/importItem.do?mainId=' + businessId + "&pin=" +
                mini.get("pin").getValue(), false);
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
        itemListGrid.load();
    }
    //..
    function downImportTemplate() {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + "/newproductAssembly/core/kanban/importItemTemplateDownload.do");
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
    //所有非特殊文本都变成这个格式
    function render(e) {
        if (e.value != null && e.value != "") {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            return html;
        }
    }
    function renderColor(e) {
        if (e.value == '是') {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 30px;background-color: red" >' + e.value + '</span>';
        } else {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 30px" >' + e.value + '</span>';
        }
        return html;
    }
    function renderRed(e) {
        if (e.value != '未开始') {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 30px;background-color: red" >' + e.value + '</span>';
        } else {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 30px" >' + e.value + '</span>';
        }
        return html;
    }
    //..发送钉钉
    function sendDingDing() {
        var rows = itemListGrid.getSelecteds();
        if (rows.length == 0) {
            mini.alert("请选中记录");
            return;
        }
        //循环选中的明细，调用发消息接口
        for (var i = 0; i < rows.length; i++) {
            debugger;
            var userNos = '';
            var sqlparam = {};
            sqlparam.USER_ID_ = rows[i].repUserId;
            doQuery('getUserById', sqlparam, function (data) {
                userNos = data.data[0].USER_NO_;
            })

            var messageText = "产品型号:'" + mini.get("designModel").getValue() +
                "',整机编号:'" + mini.get("pin").getValue() + "'的新品装配产生异常!" +
                "异常类型:'" + rows[i].exceptionType + "'异常部件:'" + rows[i].partsCategory +
                "'异常节点:'" + rows[i].assemblyNode;
            var param = {};
            param.userNos = userNos;
//            param.userNos = 'aaa';
            param.messageText = messageText;
            $.ajax({
                url: jsUseCtxPath + '/xcmgTdm/core/requestapi/sendDingDing.do',
                type: 'post',
                async: false,
                data: mini.encode(param),
                contentType: 'application/json',
                success: function (data) {
                    if (data) {
                        if (data.result == 'success') {
                            messageCounts++;
                            if (messageCounts == rows.length) {
                                message = "发送信息成功";
                                mini.alert(message);
                            }
                        } else {
                            message = "异常类型:'" + rows[i].exceptionType + "'异常部件:'" + rows[i].partsCategory +
                                "'异常节点:'" + rows[i].assemblyNode + "'的信息发送信息失败" + data.message;
                            mini.alert(message);
                        }
                    }
                }
            });
        }
    }
</script>
</body>
</html>
