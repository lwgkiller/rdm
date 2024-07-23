<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title></title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<div style="display: none">
    <input id="textboxEditor" class="mini-textbox"/>
</div>
<%--工具栏--%>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">销售型号：</span>
                    <input class="mini-textbox" id="salesModel" name="salesModel"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">设计型号：</span>
                    <input class="mini-textbox" id="designModel" name="designModel"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">物料编码：</span>
                    <input class="mini-textbox" id="materialCode" name="materialCode"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">对接负责人：</span>
                    <input class="mini-textbox" id="responsibleName" name="responsibleName"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">销售区域：</span>
                    <input id="salesArea" name="salesArea" class="mini-combobox" style="width:auto;"
                           textField="value" valueField="key"
                           required="false" allowInput="false" showNullItem="false"
                           data="[{'key':'内销','value':'内销'},
                           {'key':'欧美澳','value':'欧美澳'},
                           {'key':'其他出口区域','value':'其他出口区域'}]"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">状态：</span>
                    <input id="businessStatus" name="businessStatus" class="mini-combobox" style="width:auto;"
                           textField="value" valueField="key"
                           required="false" allowInput="false" showNullItem="false"
                           data="[{'key':'编辑中','value':'编辑中'},
                           {'key':'审核中','value':'审核中'},
                           {'key':'已发布','value':'已发布'}]"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">类别：</span>
                    <input id="indepRechOrExtProc" name="indepRechOrExtProc" class="mini-combobox" style="width:auto;"
                           textField="value" valueField="key"
                           required="false" allowInput="false" showNullItem="false"
                           data="[{'key':'自主研发','value':'自主研发'},{'key':'外购','value':'外购'}]"/>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <a id="addBusiness" class="mini-button" style="margin-right: 5px" plain="true" onclick="addBusiness()">新增</a>
                    <a id="saveBusiness" class="mini-button" style="margin-right: 5px" plain="true" onclick="saveBusiness()">保存</a>
                    <a id="submitBusiness" class="mini-button" style="margin-right: 5px" plain="true" onclick="submitBusiness()">提交审核</a>
                    <a id="reviewBusiness" class="mini-button" style="margin-right: 5px" plain="true" onclick="reviewBusiness()">审核通过</a>
                    <a id="backBusiness" class="mini-button" style="margin-right: 5px" plain="true" onclick="backBusiness()">驳回修改</a>
                    <a id="deleteBusiness" class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="deleteBusiness()">删除</a>
                    <a id="exportBusiness" class="mini-button" style="margin-right: 5px" plain="true" onclick="exportBusiness()">导出</a>
                </li>
            </ul>
        </form>
        <span class="searchSelectionBtn" onclick="no_search(this ,'searchForm')">
				<i class="icon-sc-lower"></i>
        </span>
    </div>
</div>
<%--列表视图--%>
<div class="mini-fit" style="height: 100%;">
    <div id="businessListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" allowCellWrap="true"
         showCellTip="true" idField="id" allowCellEdit="true" allowCellSelect="true" allowHeaderWrap="true" oncellbeginedit="cellBeginEdit"
         multiSelect="true" showColumnsMenu="false" sizeList="[50,100]" pageSize="5000" allowAlternating="true" pagerButtons="#pagerButtons"
         url="${ctxPath}/productDataManagement/core/attachedtoolsSpectrum/itemListQuery.do?mainId=${mainId}"
         oncellvalidation="onCellValidation" frozenStartColumn="0" frozenEndColumn="4" sortMode="client">
        <div property="columns">
            <div type="checkcolumn" width="35"></div>
            <div type="indexcolumn" width="35" headerAlign="center" align="center">序号</div>
            <div field="businessStatus" width="60" headerAlign="center" align="center" allowSort="true">状态</div>
            <div field="attachedtoolsType2" name="attachedtoolsType2" width="120" headerAlign="center" align="center" allowSort="true">产品名称</div>
            <div field="functionIntroduction" name="functionIntroduction" width="120" headerAlign="center" align="center" renderer="render">功能简介</div>
            <div field="salesModel" width="120" headerAlign="center" align="center" allowSort="true" renderer="render">销售型号
                <input property="editor" class="mini-textbox" style="width:100%;"/>
            </div>
            <div field="designModel" width="120" headerAlign="center" align="center" allowSort="true" renderer="render">设计型号
                <input property="editor" class="mini-textbox" style="width:100%;"/>
            </div>
            <div field="materialCode" width="100" headerAlign="center" align="center" allowSort="true">物料编码
                <input property="editor" class="mini-textbox" style="width:100%;"/>
            </div>
            <div field="suitableTonnage" width="120" headerAlign="center" align="center" renderer="render">适配挖机吨位
                <input property="editor" class="mini-textbox" style="width:100%;"/>
            </div>
            <div field="responsibleId" displayField="responsibleId_name" width="80" headerAlign="center" align="center">对接负责人
                <input property="editor" class="mini-user rxc" plugins="mini-user"
                allowinput="false" single="true" mainfield="no" name="responsibleId"/>
            </div>
            <div field="reviewerName" width="80" headerAlign="center" align="center">审核人</div>
            <%--切入动态字段--%>
            <div field="salesArea" displayField="salesArea" width="100" headerAlign="center" align="center" renderer="render">销售区域
                <input property="editor" class="mini-combobox" style="width:100%;" valueField="key" textField="value"
                       multiSelect="true"
                       allowInput="false" data="[{'key':'内销','value':'内销'},
                           {'key':'欧美澳','value':'欧美澳'},
                           {'key':'其他出口区域','value':'其他出口区域'}]"/>
            </div>
            <div field="indepRechOrExtProc" displayField="indepRechOrExtProc" width="100" headerAlign="center" align="center">自主研发/外购
                <input property="editor" class="mini-combobox" style="width:100%;" valueField="key" textField="value"
                       allowInput="false" data="[{'key':'自主研发','value':'自主研发'},{'key':'外购','value':'外购'}]"/>
            </div>
            <div field="isSeparately" displayField="isSeparately" width="100" headerAlign="center" align="center">是否单独成册
                <input property="editor" class="mini-combobox" style="width:100%;" valueField="key" textField="value"
                       allowInput="false" data="[{'key':'是','value':'是'},{'key':'否','value':'否'}]"/>
            </div>
            <div field="benchmarkingBrands" width="100" headerAlign="center" align="center" allowSort="true">建议对标品牌
                <input property="editor" class="mini-textbox" style="width:100%;"/>
            </div>
            <div field="competitiveInformation" displayField="competitiveInformation" width="100" headerAlign="center" align="center">竞品资料情况
                <input property="editor" class="mini-combobox" style="width:100%;" valueField="key" textField="value"
                       allowInput="false" data="[{'key':'已有','value':'已有'},{'key':'已有部分','value':'已有部分'}
                       ,{'key':'待收集','value':'待收集'}]"/>
            </div>
            <div width="80" headerAlign="center" align="center" renderer="itemFileRenderer">附件</div>
            <div field="remarks" width="250" headerAlign="center" align="center" renderer="render">备注
                <input property="editor" class="mini-textbox" style="width:100%;"/>
            </div>
        </div>
    </div>
</div>
<!--导出Excel相关HTML-->
<form id="excelForm"
      action="${ctxPath}/productDataManagement/core/attachedtoolsSpectrum/exportItems.do?mainId=${mainId}" method="post" target="excelIFrame">
    <input type="hidden" name="filter" id="filter"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>
<%--JS--%>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var currentUserName = "${currentUserName}";
    var mainId = "${mainId}";
    var action = "${action}";
    var isAttachedtoolsSpectrumAdmin = "${isAttachedtoolsSpectrumAdmin}";
    var currentTime = new Date().format("yyyy-MM-dd hh:mm:ss");
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";
    var businessListGrid = mini.get("businessListGrid");
    var startIndex = 10;
    var endIndex = 10;
    var isParametersNotOk = true;
    //..
    $(function () {
        if (action == "edit") {
            if (currentUserNo != "admin" && isAttachedtoolsSpectrumAdmin == "false") {
                mini.get("reviewBusiness").setEnabled(false);
                mini.get("backBusiness").setEnabled(false);
                mini.get("addBusiness").setVisible(false);
            }
        } else if (action == "browse") {
            mini.get("addBusiness").setVisible(false);
            mini.get("saveBusiness").setVisible(false);
            mini.get("submitBusiness").setVisible(false);
            mini.get("reviewBusiness").setVisible(false);
            mini.get("backBusiness").setVisible(false);
            mini.get("deleteBusiness").setVisible(false);
        }
        businessListGrid.on("load", function () {
            businessListGrid.mergeColumns(["attachedtoolsType2", "functionIntroduction"]);
            supplementGrid();
        });
    });
    //..
    function supplementGrid() {
        _SubmitJson({
            url: jsUseCtxPath + "/productDataManagement/core/attachedtoolsSpectrum/getParameterCfg.do?mainId=" + mainId,
            method: 'POST',
            postJson: true,
            showMsg: false,
            success: function (returnData) {
                if (returnData.success) {
                    if (isParametersNotOk) {
                        var columns = businessListGrid.getColumns();
                        var itemDimension = returnData.data;
                        for (var i = 0, l = itemDimension.length; i < l; i++) {
                            var column = {
                                field: itemDimension[i].key,
                                width: 100,
                                headerAlign: "center",
                                align: "center",
                                allowSort: false,
                                header: itemDimension[i].value
                            }
                            columns.splice(startIndex + i, 0, column)
                            endIndex++;
                        }
                        businessListGrid.set({
                            columns: columns
                        });
                        for (var i = startIndex, len = endIndex; i < len; i++) {
                            var column = grid.columns[i];
                            column.editor = mini.get("textboxEditor");
                        }
                        isParametersNotOk = false;
                    }
                } else {
                    mini.alert("获取参数配置失败:" + returnData.message, "提示", function (action) {
                        window.close();
                    });
                }
            },
            fail: function (returnData) {
                mini.alert("获取参数配置失败:" + returnData.message, "提示", function (action) {
                    window.close();
                });
            }
        });
    }
    //..
    function cellBeginEdit(e) {
        debugger;
        var record = e.record, field = e.field, value = e.value;
        if (action == 'edit') {
            if (record.businessStatus == "审核中" || record.businessStatus == "已发布") {
                if (currentUserNo !== 'admin' && isAttachedtoolsSpectrumAdmin == 'false') {
                    e.editor.setEnabled(false);
                } else {
                    e.editor.setEnabled(true);
                }
            } else {
                if (currentUserNo !== 'admin' && isAttachedtoolsSpectrumAdmin == 'false') {
                    if (record.responsibleId == currentUserId && record.id) {
                        if (field != 'designModel' && field != 'suitableTonnage' && field != 'responsibleId') {
                            e.editor.setEnabled(true);
                        }else{
                            e.editor.setEnabled(false);
                        }
                    }else{
                        e.editor.setEnabled(false);
                    }
                } else {
                    e.editor.setEnabled(true);
                }
            }
        } else if (action == 'browse') {
            e.editor.setEnabled(false);
        }
    }
    //..
    function addBusiness() {
        var newRow = {};
        addRowGrid("businessListGrid", newRow);
    }
    //..
    function saveBusiness() {
        businessListGrid.validate();
        if (!businessListGrid.isValid()) {
            var error = businessListGrid.getCellErrors()[0];
            businessListGrid.beginEditCell(error.record, error.column);
            mini.alert(error.column.header + error.errorText);
            return;
        }
        var postData = businessListGrid.getChanges();
        _SubmitJson({
            url: jsUseCtxPath + "/productDataManagement/core/attachedtoolsSpectrum/saveItems.do?mainId=" + mainId,
            method: 'POST',
            data: postData,
            postJson: true,
            showMsg: false,
            success: function (returnData) {
                if (returnData.success) {
                    mini.alert(returnData.message);
                    businessListGrid.reload();
                } else {
                    mini.alert("保存失败:" + returnData.message);
                }
            },
            fail: function (returnData) {
                mini.alert("保存失败:" + returnData.message);
            }
        });
    }
    //..
    function submitBusiness() {
        var rows = businessListGrid.getSelecteds();
        if (rows.length != 1) {
            mini.alert("一次只能提交一条记录");
            return;
        }
        var postData = rows[0];
        if (currentUserNo != 'admin' && isAttachedtoolsSpectrumAdmin == 'false') {
            if (postData.responsibleId != currentUserId) {
                mini.alert("只能提交自己负责的记录");
                return;
            }
        }
        if (postData.businessStatus != '编辑中') {
            mini.alert("只能提交状态为‘编辑中’的记录");
            return;
        }
        _SubmitJson({
            url: jsUseCtxPath + "/productDataManagement/core/attachedtoolsSpectrum/submitItem.do",
            method: 'POST',
            data: postData,
            postJson: true,
            showMsg: false,
            success: function (returnData) {
                if (returnData.success) {
                    mini.alert(returnData.message);
                    businessListGrid.reload();
                } else {
                    mini.alert("提交失败:" + returnData.message);
                }
            },
            fail: function (returnData) {
                mini.alert("提交失败:" + returnData.message);
            }
        });
    }
    //..
    function reviewBusiness() {
        var rows = businessListGrid.getSelecteds();
        if (rows.length != 1) {
            mini.alert("一次只能审核一条记录");
            return;
        }
        var postData = rows[0];
        if (currentUserNo != 'admin' && isAttachedtoolsSpectrumAdmin == 'false') {
            mini.alert("只有管理员能审核记录");
            return;
        }
        if (postData.businessStatus != '审核中') {
            mini.alert("只能审核状态为‘审核中’的记录");
            return;
        }
        _SubmitJson({
            url: jsUseCtxPath + "/productDataManagement/core/attachedtoolsSpectrum/reviewItem.do",
            method: 'POST',
            data: postData,
            postJson: true,
            showMsg: false,
            success: function (returnData) {
                if (returnData.success) {
                    mini.alert(returnData.message);
                    businessListGrid.reload();
                } else {
                    mini.alert("提交失败:" + returnData.message);
                }
            },
            fail: function (returnData) {
                mini.alert("提交失败:" + returnData.message);
            }
        });
    }
    //..
    function backBusiness() {
        var rows = businessListGrid.getSelecteds();
        if (rows.length != 1) {
            mini.alert("一次只能驳回一条记录");
            return;
        }
        var postData = rows[0];
        if (currentUserNo != 'admin' && isAttachedtoolsSpectrumAdmin == 'false') {
            mini.alert("只有管理员能驳回记录");
            return;
        }
        if (postData.businessStatus != '审核中') {
            mini.alert("只能驳回状态为‘审核中’的记录");
            return;
        }
        _SubmitJson({
            url: jsUseCtxPath + "/productDataManagement/core/attachedtoolsSpectrum/backItem.do",
            method: 'POST',
            data: postData,
            postJson: true,
            showMsg: false,
            success: function (returnData) {
                if (returnData.success) {
                    mini.alert(returnData.message);
                    businessListGrid.reload();
                } else {
                    mini.alert("提交失败:" + returnData.message);
                }
            },
            fail: function (returnData) {
                mini.alert("提交失败:" + returnData.message);
            }
        });
    }
    //..
    function deleteBusiness() {
        var rows = businessListGrid.getSelecteds();
        if (rows.length <= 0) {
            mini.alert("请至少选中一条记录");
            return;
        }
        if (currentUserNo != 'admin' && isAttachedtoolsSpectrumAdmin == 'false') {
            for (var i = 0, l = rows.length; i < l; i++) {
                if (rows[i].responsibleId != currentUserId || rows[i].businessStatus != '编辑中') {
                    mini.alert("非管理员只能删除自己负责的且状态处于‘编辑中’的记录");
                    return;
                }
            }
        }
        mini.confirm("确定删除选中记录？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                var rowIds = [];
                for (var i = 0, l = rows.length; i < l; i++) {
                    var r = rows[i];
                    if (!r.id) {
                        delRowGrid("businessListGrid", r);
                    } else {
                        rowIds.push(r.id);
                    }
                }
                if (rowIds.length > 0) {
                    _SubmitJson({
                        url: jsUseCtxPath + "/productDataManagement/core/attachedtoolsSpectrum/deleteItems.do",
                        method: 'POST',
                        data: {ids: rowIds.join(',')},
                        postJson: false,
                        showMsg: false,
                        success: function (returnData) {
                            if (returnData.success) {
                                mini.alert(returnData.message);
                                searchFrm();
                            } else {
                                mini.alert("删除失败:" + returnData.message);
                            }
                        },
                        fail: function (returnData) {
                            mini.alert("删除失败:" + returnData.message);
                        }
                    });
                }
            }
        });
    }
    //..
    function onCellValidation(e) {
        var record = e.record;
        if (record.responsibleId == currentUserId) {
            if (e.field == 'salesModel' && (!e.value || e.value == '')) {
                e.isValid = false;
                e.errorText = '不能为空！';
            }
            if (e.field == 'designModel' && (!e.value || e.value == '')) {
                e.isValid = false;
                e.errorText = '不能为空！';
            }
            if (e.field == 'materialCode' && (!e.value || e.value == '')) {
                e.isValid = false;
                e.errorText = '不能为空！';
            }
            if (e.field == 'suitableTonnage' && (!e.value || e.value == '')) {
                e.isValid = false;
                e.errorText = '不能为空！';
            }
            if (e.field == 'responsibleId' && (!e.value || e.value == '')) {
                e.isValid = false;
                e.errorText = '不能为空！';
            }
            if (e.field == 'salesArea' && (!e.value || e.value == '')) {
                e.isValid = false;
                e.errorText = '不能为空！';
            }
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
    //..
    function itemFileRenderer(e) {
        var record = e.record;
        var cellHtml = '';
        if (action == 'edit' && record.id) {
            cellHtml = '<span title="编辑" style="color:#409EFF;cursor: pointer;"' +
                'onclick="openFileWindow(\'' + record.id + '\',\'attachedtoolsSpectrumItemFile\',\'edit\',\'' + coverContent + '\')">编辑</span>';
            if (record.businessStatus == "审核中" || record.businessStatus == "已发布") {
                if (currentUserNo !== 'admin' && isAttachedtoolsSpectrumAdmin == 'false') {
                    cellHtml = '<span title="查看" style="color:#409EFF;cursor: pointer;"' +
                        'onclick="openFileWindow(\'' + record.id + '\',\'attachedtoolsSpectrumItemFile\',\'detail\',\'' + coverContent + '\')">查看</span>';
                }
            } else {
                if (record.responsibleId != currentUserId && currentUserNo !== 'admin' && isAttachedtoolsSpectrumAdmin == 'false') {
                    cellHtml = '<span title="查看" style="color:#409EFF;cursor: pointer;"' +
                        'onclick="openFileWindow(\'' + record.id + '\',\'attachedtoolsSpectrumItemFile\',\'detail\',\'' + coverContent + '\')">查看</span>';
                }
            }
        } else if (action == 'browse') {
            cellHtml = '<span title="查看" style="color:#409EFF;cursor: pointer;"' +
                'onclick="openFileWindow(\'' + record.id + '\',\'attachedtoolsSpectrumItemFile\',\'detail\',\'' + coverContent + '\')">查看</span>';
        }
        return cellHtml;
    }
    //..
    function openFileWindow(businessId, businessType, action, coverContent) {
        mini.open({
            title: "文件列表",
            url: jsUseCtxPath + "/productDataManagement/core/attachedtoolsSpectrum/openFileWindow.do?businessId=" +
            businessId + "&businessType=" + businessType + "&action=" + action + "&coverContent=" + coverContent,
            width: 800,
            height: 600,
            showModal: true,
            allowResize: true,
            onload: function () {
            },
            ondestroy: function (action) {
            }
        });
    }
</script>
<redxun:gridScript gridId="businessListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>