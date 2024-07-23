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
                    <span class="text" style="width:auto">是否包含流程：</span>
                    <input id="isInst" name="isInst" enabled="false"
                           class="mini-combobox" style="width:98%"
                           data="[{key:'yes',value:'是'},{key:'no',value:'否'}]"
                           valueField="key" textField="value" value="no"/>
                </li>
                <li style="margin-right:15px">
                    <span class="text" style="width:auto">试验编号：</span>
                    <input class="mini-textbox" id="testNo" name="testNo" style="width:100%;"/>
                </li>
                <li style="margin-right:15px">
                    <span class="text" style="width:auto">零部件类别：</span>
                    <input id="componentCategory" name="componentCategory"
                           class="mini-combobox" style="width:98%"
                           url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=componentCategory"
                           valueField="key" textField="value"/>
                </li>
                <li style="margin-right:15px">
                    <span class="text" style="width:auto">零部件名称：</span>
                    <input class="mini-textbox" id="componentName" name="componentName" style="width:100%;"/>
                </li>
                <li style="margin-right:15px">
                    <span class="text" style="width:auto">零部件型号：</span>
                    <input class="mini-textbox" id="componentModel" name="componentModel" style="width:100%;"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">物料编码：</span>
                    <input class="mini-textbox" id="materialCode" name="materialCode"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">样品类型：</span>
                    <input id="sampleType" name="sampleType"
                           class="mini-combobox" style="width:98%"
                           url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=sampleType"
                           valueField="key" textField="value"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">配套主机型号：</span>
                    <input class="mini-textbox" id="machineModel" name="machineModel"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">供应商名称：</span>
                    <input class="mini-textbox" id="supplierName" name="supplierName"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">承担单位：</span>
                    <input class="mini-textbox" id="laboratory" name="laboratory"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">试验类型：</span>
                    <input id="testType" name="testType"
                           class="mini-combobox" style="width:98%"
                           url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=testType"
                           valueField="key" textField="value"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">申请人：</span>
                    <input class="mini-textbox" id="applyUser" name="applyUser"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">申请部门：</span>
                    <input class="mini-textbox" id="applyDep" name="applyDep"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">试验负责人：</span>
                    <input class="mini-textbox" id="testLeader" name="testLeader"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">试验计划类型：</span>
                    <input id="testCategory" name="testCategory"
                           class="mini-combobox" style="width:98%"
                           url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=testCategory"
                           valueField="key" textField="value"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">试验运行状态：</span>
                    <input id="testStatus" name="testStatus"
                           class="mini-combobox" style="width:98%"
                           url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=testStatus"
                           valueField="key" textField="value"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">检测合同状态：</span>
                    <input id="testContractStatus" name="testContractStatus"
                           class="mini-combobox" style="width:98%"
                           url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=testContractStatus"
                           valueField="key" textField="value"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">试验结果：</span>
                    <input id="testResult" name="testResult"
                           class="mini-combobox" style="width:98%"
                           url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=testResult"
                           valueField="key" textField="value"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">不合格零部件当前状态：</span>
                    <input id="unqualifiedStatus" name="unqualifiedStatus"
                           class="mini-combobox" style="width:98%"
                           url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=unqualifiedStatus"
                           valueField="key" textField="value"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">计划试验时间：</span>
                    <input id="plannedTestMonthBegin" name="plannedTestMonthBegin" class="mini-monthpicker" allowinput="false"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text-to" style="width:auto">至：</span>
                    <input id="plannedTestMonthEnd" name="plannedTestMonthEnd" class="mini-monthpicker" allowinput="false"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">实际试验时间：</span>
                    <input id="actualTestMonthBegin" name="actualTestMonthBegin" class="mini-monthpicker" allowinput="false"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text-to" style="width:auto">至：</span>
                    <input id="actualTestMonthEnd" name="actualTestMonthEnd" class="mini-monthpicker" allowinput="false"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">试验完成时间：</span>
                    <input id="completeTestMonthBegin" name="completeTestMonthBegin" class="mini-monthpicker" allowinput="false"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text-to" style="width:auto">至：</span>
                    <input id="completeTestMonthEnd" name="completeTestMonthEnd" class="mini-monthpicker" allowinput="false"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text-to" style="width:auto">计划试验年份：</span>
                    <input id="signYear" name="signYear"
                           class="mini-combobox"
                           url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=signYear"
                           valueField="key" textField="value"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">样件来源：</span>
                    <input id="sampleSource" name="sampleSource"
                           class="mini-combobox" style="width:98%"
                           url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=sampleSource"
                           valueField="key" textField="value"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">样件处理方式：</span>
                    <input id="sampleProcessingMethod" name="sampleProcessingMethod"
                           class="mini-combobox" style="width:98%"
                           url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=sampleProcessingMethod"
                           valueField="key" textField="value"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">样件当前状态：</span>
                    <input id="sampleStatus" name="sampleStatus"
                           class="mini-combobox" style="width:98%"
                           url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=sampleStatus"
                           valueField="key" textField="value"/>
                </li>
                <li style="margin-left: 10px">
                    <f:a alias="componentTestKanban-searchFrm" onclick="searchFrm()" showNoRight="false" style="margin-right: 5px">查询</f:a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <%--<f:a alias="componentTestKanban-addBusiness" onclick="addBusiness()" showNoRight="false" style="margin-right: 5px">新增</f:a>--%>
                    <f:a alias="componentTestKanban-removeBusiness" onclick="removeBusiness()" showNoRight="false" style="margin-right: 5px">删除</f:a>
                    <f:a alias="componentTestKanban-openImportWindow" onclick="openImportWindow()" showNoRight="false"
                         style="margin-right: 5px">导入</f:a>
                    <f:a alias="componentTestKanban-editBusiness" onclick="editBusiness()" showNoRight="false" style="margin-right: 5px">编辑</f:a>
                    <f:a alias="componentTestKanban-exportBusiness" onclick="exportBusiness()" showNoRight="false" style="margin-right: 5px">导出</f:a>
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
         showCellTip="true" idField="id" allowCellEdit="true" allowCellSelect="true" oncellvalidation="onCellValidation"
         multiSelect="true" showColumnsMenu="false" sizeList="[50,100]" pageSize="50" allowAlternating="true"
         pagerButtons="#pagerButtons"
         url="${ctxPath}/componentTest/core/kanban/dataListQuery.do">
        <div property="columns">
            <div type="checkcolumn" width="40" trueValue="1" falseValue="0"></div>
            <div type="indexcolumn" width="45" headerAlign="center" align="center" allowSort="true">序号</div>
            <div name="action" cellCls="actionIcons" width="85" headerAlign="center" align="center" renderer="onActionRenderer"
                 cellStyle="padding:0;">
                操作
            </div>
            <div field="testNo" width="130" headerAlign="center" align="center" allowSort="true" renderer="render">试验编号</div>
            <div field="componentCategory" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">零部件类别</div>
            <div field="componentName" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">零部件名称</div>
            <div field="componentModel" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">零部件型号</div>
            <div field="materialCode" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">物料编码</div>
            <div field="sampleType" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">样品类型</div>
            <div field="machineModel" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">配套主机型号</div>
            <div field="machineName" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">配套主机名称</div>
            <div field="machineCompany" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">配套主机单位</div>
            <div field="supplierName" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">供应商名称</div>
            <div field="laboratory" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">承担单位</div>
            <div field="testType" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">试验类型</div>
            <div field="plannedTestMonth" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">计划试验时间</div>
            <div field="applyUser" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">申请人</div>
            <div field="applyDep" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">申请部门</div>
            <div field="testCategory" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">试验计划类型</div>
            <div field="testLeaderId" width="100" headerAlign="center" align="center" allowSort="true" visible="false">试验负责人id</div>
            <div field="testLeader" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">试验负责人</div>
            <div field="testStatus" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">试验运行状态</div>
            <div field="actualTestMonth" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">实际试验时间</div>
            <div field="testProgress" width="100" headerAlign="center" align="center" allowSort="true" numberFormat="p">试验进度</div>
            <div field="completeTestMonth" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">试验完成时间</div>
            <div field="testResult" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">试验结果</div>
            <div field="nonconformingDescription" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">不合格项说明</div>
            <div field="unqualifiedStatus" width="150" headerAlign="center" align="center" allowSort="true" renderer="render">不合格零部件当前状态</div>
            <div field="testReport" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">检测报告</div>
            <div field="testContract" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">检测合同</div>
            <div field="testContractStatus" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">检测合同状态</div>
            <div field="testCost" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">检测费用</div>
            <div field="testRounds" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">试验次数</div>
            <div field="sampleSource" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">样件来源</div>
            <div field="sampleProcessingMethod" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">样件处理方式</div>
            <div field="sampleStatus" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">样件当前状态</div>
            <div field="remark" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">备注</div>
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
                        <a href="#" style="color:blue;text-decoration:underline;" onclick="downImportTemplate()">零部件试验项目导入模板.xlsx</a>
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
<form id="excelForm" action="${ctxPath}/componentTest/core/kanban/exportExcel.do" method="post" target="excelIFrame">
    <input type="hidden" name="filter" id="filter"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var businessListGrid = mini.get("businessListGrid");
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var importWindow = mini.get("importWindow");
    //..
    $(function () {
        searchFrm();
    });
    //..
    function onActionRenderer(e) {
        var record = e.record;
        var businessId = record.id;
        var s = '<span  title="明细" onclick="detailBusiness(\'' + businessId + '\')">明细</span>';
        //todo:由于新的权限控制更严谨，案负责人渲染的编辑权限取消
//        if (record.testLeaderId == currentUserId) {
//            s += '<span  title="编辑" onclick="editBusiness(\'' + businessId + '\')">编辑</span>';
//        }
        return s;
    }
    //..所有非特殊文本都变成这个格式
    function render(e) {
        if (e.value != null && e.value != "") {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            return html;
        }
    }
    //..
    function addBusiness() {
        var url = jsUseCtxPath + "/componentTest/core/kanban/editPage.do?businessId=&action=add";
        var winObj = window.open(url, '');
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (businessListGrid) {
                    businessListGrid.reload();
                }
            }
        }, 1000);
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
                for (var i = 0, l = rows.length; i < l; i++) {
                    var r = rows[i];
                    rowIds.push(r.id);
                }
                _SubmitJson({
                    url: jsUseCtxPath + "/componentTest/core/kanban/deleteBusiness.do",
                    method: 'POST',
                    data: {ids: rowIds.join(',')},
                    success: function (data) {
                        if (data) {
                            searchFrm();
                        }
                    }
                });
            }
        });
    }
    //..
    function detailBusiness(businessId) {
        var url = jsUseCtxPath + "/componentTest/core/kanban/editPage.do?businessId=" + businessId + "&action=detail";
        var winObj = window.open(url, '');
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (businessListGrid) {
                    businessListGrid.reload();
                }
            }
        }, 1000);
    }
    //..
    function editBusiness(businessId) {
        var url = jsUseCtxPath + "/componentTest/core/kanban/editPage.do?businessId=" + businessId + "&action=edit";
        var winObj = window.open(url, '');
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (businessListGrid) {
                    businessListGrid.reload();
                }
            }
        }, 1000);
    }
    //
    function editBusiness() {
        var row = businessListGrid.getSelected();
        if (row == null) {
            mini.alert("请至少选中一条记录");
            return;
        }
        var url = jsUseCtxPath + "/componentTest/core/kanban/editPage.do?businessId=" + row.id + "&action=edit";
        var winObj = window.open(url, '');
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (businessListGrid) {
                    businessListGrid.reload();
                }
            }
        }, 1000);
    }
    //..
    function openImportWindow() {
        importWindow.show();
    }
    //..
    function closeImportWindow() {
        importWindow.hide();
        clearUploadFile();
        searchFrm();
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
            xhr.open('POST', jsUseCtxPath + '/componentTest/core/kanban/importExcel.do', false);
            xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
            var fd = new FormData();
            fd.append('importFile', file);
            xhr.send(fd);
        }
    }
    //..
    function downImportTemplate() {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + "/componentTest/core/kanban/importTemplateDownload.do");
        $("body").append(form);
        form.submit();
        form.remove();
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
    //..导出
    function exportBusiness() {
        var rows = businessListGrid.getSelecteds();
        if (rows.length <= 0) {
            mini.alert("请至少选中一条记录");
            return;
        }
        var params = [];
        for (var i = 0; i < rows.length; i++) {
            var obj = {};
            obj.name = "id";
            obj.value = rows[i].id
            params.push(obj);
        }
        $("#filter").val(mini.encode(params));
        var excelForm = $("#excelForm");
        excelForm.submit();
    }
    //..
    function clearForm() {
        if(typeof (group_userInfo) != "undefined" && group_userInfo){
            group_userInfo = {
                groupId:0,
                userInfoList:[],
                isOsUserManager:false
            };
        }
        var parent=$(".search-form");
        var inputAry=$("input",parent);
        inputAry.each(function(i){
            if(i!=0 && i!=1){
                var el=$(this);
                el.val("");
            }
        });
        mini.get("isInst").setValue("no");
        searchFrm();
    }
</script>
<redxun:gridScript gridId="businessListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>