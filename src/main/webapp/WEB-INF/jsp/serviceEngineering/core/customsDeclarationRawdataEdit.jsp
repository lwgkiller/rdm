<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>报关信息归档</title>
    <%@include file="/commons/edit.jsp" %>
</head>
<body>
<%--工具栏--%>
<div id="toolBar" class="topToolBar">
    <div>
        <a id="saveBusiness" class="mini-button" onclick="saveBusiness()">暂存</a>
        <a id="commitBusiness" class="mini-button" onclick="commitBusiness()">提交</a>
    </div>
</div>
<%--表单视图--%>
<div class="mini-fit">
    <div class="form-container" style="margin:0 auto; width: 100%;padding: 0">
        <form id="businessForm" method="post">
            <input class="mini-hidden" id="id" name="id"/>
            <input class="mini-hidden" id="businessStatus" name="businessStatus"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0" style="height: 100%;width: 100%">
                <caption style="font-weight: bold">
                    报关信息归档
                </caption>
                <tr>
                    <td style="text-align: center;width: 15%">批次号：</td>
                    <td style="min-width:170px">
                        <input id="batchNo" name="batchNo" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%">单据号：</td>
                    <td style="min-width:170px">
                        <input id="businessNo" name="businessNo" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">提交人：</td>
                    <td style="min-width:170px">
                        <input id="submitter" name="submitter" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 20%">提交时间：</td>
                    <td style="min-width:170px">
                        <input id="submitTime" name="submitTime" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                </tr>
                <%--数据明细--%>
                <tr>
                    <td colspan="4" style="text-align: center;height: 50px">物料明细：</td>
                </tr>
                <tr>
                    <td colspan="4" style="height: 500px">
                        <div id="itemsButtons" class="mini-toolbar">
                            <a id="addItem" class="mini-button" style="margin-bottom: 5px;margin-top: 5px"
                               onclick="addItem">添加</a>
                            <a id="deleteItem" class="mini-button" style="margin-bottom: 5px;margin-top: 5px"
                               onclick="deleteItem">删除</a>
                            <a id="openImportWindow" class="mini-button" style="margin-right: 5px" plain="true" onclick="openImportWindow">导入</a>
                            <input id="machineModelFilter" class="mini-textbox" emptyText="适用机型" style="width:150px;" onenter="onKeyEnter"/>
                            <input id="materialCodeFilter" class="mini-textbox" emptyText="物料号" style="width:150px;" onenter="onKeyEnter"/>
                            <input id="elementsFilter" class="mini-textbox" emptyText="报关要素" style="width:150px;" onenter="onKeyEnter"/>
                            <input id="isSupplementFilter" class="mini-combobox" emptyText="是否已发起补充" style="width:130px;" onenter="onKeyEnter"
                                   data="[{'key':'true','value':'是'},{'key':'false','value':'否'}]" showNullItem="true"
                                   valueField="key" textField="value"/>
                            <a class="mini-button" onclick="search()">查询</a>
                            <a id="supplement" class="mini-button" style="margin-bottom: 5px;margin-top: 5px" onclick="supplementItem">发起收集</a>
                            <input id="processUser" name="processUser" showclose="true" class="mini-user rxc"
                                   plugins="mini-user" style="width:170px;" allowinput="false" emptyText="补充信息的负责人"
                                   length="50" maxlength="50" mainfield="no" single="true"/>
                        </div>
                        <div id="itemListGrid" class="mini-datagrid" style="height: 95%" allowResize="true" allowCellWrap="false"
                             showCellTip="true" autoload="true" idField="id" multiSelect="true" showPager="false"
                             showColumnsMenu="false" allowcelledit="true" allowcellselect="true" allowAlternating="true"
                             oncellvalidation="itemListGridCellValidation"
                             url="${ctxPath}/serviceEngineering/core/customsDeclarationRawdata/getItemList.do?businessId=${businessId}">
                            <div property="columns">
                                <div type="checkcolumn" width="40" trueValue="1" falseValue="0"></div>
                                <div field="machineModel" width="150" headerAlign="center" align="center" renderer="render">适用机型
                                    <input id="machineModel" property="editor" class="mini-textbox" style="width:98%;"/>
                                </div>
                                <div field="materialCode" width="100" headerAlign="center" align="center">物料号
                                    <input id="materialCode" property="editor" class="mini-textbox" style="width:98%;"/>
                                </div>
                                <div field="materialDescription" width="150" headerAlign="center" align="center" renderer="render">物料描述
                                    <input id="materialDescription" property="editor" class="mini-textbox" style="width:98%;"/>
                                </div>
                                <div field="hsCode" width="120" headerAlign="center" align="center">HS编码
                                    <input id="hsCode" property="editor" class="mini-textbox" style="width:98%;"/>
                                </div>
                                <div field="elements" width="300" headerAlign="center" align="center" renderer="render">报关要素
                                    <input id="elements" property="editor" class="mini-textarea" style="width:98%;"/>
                                </div>
                                <div field="elementsFill" width="300" headerAlign="center" align="center" renderer="render">报关要素填写</div>
                                <div field="additionalInfo" width="200" headerAlign="center" align="center" renderer="render">补充信息
                                    <input id="additionalInfo" property="editor" class="mini-textarea" style="width:98%;"/>
                                </div>
                                <div field="netWeight" width="80" headerAlign="center" align="center">净重
                                    <input id="netWeight" property="editor" class="mini-spinner" style="width:98%;" maxValue="99999"
                                           decimalPlaces="2"/>
                                </div>
                                <div field="remarks" width="200" headerAlign="center" align="center" renderer="render">备注
                                    <input id="remarks" property="editor" class="mini-textarea" style="width:98%;"/>
                                </div>
                                <div field="processUser" width="80" headerAlign="center" align="center">处理人</div>
                                <div field="processTime" width="100" headerAlign="center" align="center">处理完成时间</div>
                            </div>
                        </div>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
<%--导入窗口--%>
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
                        <a href="#" style="color:blue;text-decoration:underline;" onclick="downImportTemplate()">模板下载.xlsx</a>
                    </td>
                </tr>
                <tr>
                    <td style="width: 30%">操作：</td>
                    <td>
                        <input class="mini-textbox" style="width:60%;float: left" id="fileName" name="fileName" readonly/>
                        <input id="inputFile" style="display:none;" type="file" onchange="getSelectFile()" accept="application/xls"/>
                        <a id="uploadFileBtn" class="mini-button" style="float: left;margin-left: 10px" onclick="uploadFile()">选择文件</a>
                        <a id="clearFileBtn" class="mini-button" style="float: left;margin-left: 10px" onclick="clearUploadFile()">清除</a>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
<%--JS--%>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var businessId = "${businessId}";
    var action = "${action}";
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var currentUserName = "${currentUserName}";
    var currentTime = new Date().format("yyyy-MM-dd hh:mm:ss");
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";
    var businessForm = new mini.Form("#businessForm");
    var itemsButtons = mini.get("itemsButtons");
    var itemListGrid = mini.get("itemListGrid");
    var importWindow = mini.get("importWindow");
    //..
    $(function () {
        //锁定所有控件
        lockAll();
        if (businessId) {
            var url = jsUseCtxPath + "/serviceEngineering/core/customsDeclarationRawdata/getDataById.do?businessId=" + businessId;
            $.ajax({
                url: url,
                method: 'get',
                success: function (json) {
                    businessForm.setData(json);
                    //不同场景的处理
                    if (action == "detail") {
                        itemsButtons.show();
                        mini.get("machineModelFilter").setEnabled(true);
                        mini.get("materialCodeFilter").setEnabled(true);
                        mini.get("elementsFilter").setEnabled(true);
                        mini.get("isSupplementFilter").setEnabled(true);
                        mini.get("addItem").setEnabled(false);
                        mini.get("deleteItem").setEnabled(false);
                        mini.get("openImportWindow").setEnabled(false);
                        mini.get("supplement").setEnabled(false);
                    } else if (action == "edit") {
                        var businessStatus = mini.get("businessStatus").getValue();
                        if (businessStatus == '编辑中') {
                            unlockAll();
                            mini.get("supplement").setEnabled(false);
                        } else if (businessStatus == '已提交') {
                            unlockSave();
                            itemsButtons.show();
                            mini.get("machineModelFilter").setEnabled(true);
                            mini.get("materialCodeFilter").setEnabled(true);
                            mini.get("elementsFilter").setEnabled(true);
                            mini.get("isSupplementFilter").setEnabled(true);
                            mini.get("processUser").setEnabled(true);
                            mini.get("addItem").setEnabled(false);
                            mini.get("deleteItem").setEnabled(false);
                            mini.get("openImportWindow").setEnabled(false);
                        }
                    }
                }
            });
        } else {
            unlockAll();
        }
    });
    //..锁定所有控件
    function lockAll() {
        lockForm();
        lockSave();
        lockCommit();
    }
    //..解锁所有控件
    function unlockAll() {
        unlockForm();
        unlockSave();
        unlockCommit();
    }
    //..锁定表单
    function lockForm() {
        businessForm.setEnabled(false);
        itemsButtons.hide();
    }
    //..解锁表单
    function unlockForm() {
        businessForm.setEnabled(true);
        mini.get("submitter").setEnabled(false);
        mini.get("submitTime").setEnabled(false);
        itemsButtons.show();
    }
    //..锁定保存
    function lockSave() {
        mini.get("saveBusiness").setEnabled(false);
    }
    //..解锁保存
    function unlockSave() {
        mini.get("saveBusiness").setEnabled(true);
    }
    //..锁定提交
    function lockCommit() {
        mini.get("commitBusiness").setEnabled(false);
    }
    //..解锁提交
    function unlockCommit() {
        mini.get("commitBusiness").setEnabled(true);
    }
    //..
    function saveBusiness() {
        var postData = _GetFormJsonMini("businessForm");
        var items = itemListGrid.getChanges();
        if (items.length > 0) {
            postData.items = items;
        }
        _SubmitJson({
            url: jsUseCtxPath + "/serviceEngineering/core/customsDeclarationRawdata/saveBusiness.do",
            method: 'POST',
            data: postData,
            postJson: true,
            showMsg: false,
            success: function (returnData) {
                if (returnData.success) {
                    mini.alert(returnData.message);
                    var url = jsUseCtxPath + "/serviceEngineering/core/customsDeclarationRawdata/editPage.do?businessId=" +
                        returnData.data + "&action=edit";
                    window.location.href = url;
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
    function commitBusiness() {
        var postData = _GetFormJsonMini("businessForm");
        var items = itemListGrid.getChanges();
        if (items.length > 0) {
            postData.items = items;
        }
        //检查必填项
        var checkResult = commitValidCheck(postData);
        if (!checkResult.success) {
            mini.alert(checkResult.reason);
            return;
        }
        mini.confirm("确定提交？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                _SubmitJson({
                    url: jsUseCtxPath + "/serviceEngineering/core/customsDeclarationRawdata/commitBusiness.do",
                    method: 'POST',
                    data: postData,
                    postJson: true,
                    showMsg: false,
                    success: function (returnData) {
                        if (returnData.success) {
                            mini.alert(returnData.message);
                            window.close();
                        } else {
                            mini.alert("提交失败:" + returnData.message);
                        }
                    },
                    fail: function (returnData) {
                        mini.alert("提交失败:" + returnData.message);
                    }
                });
            }
        });
    }
    //..
    function commitValidCheck(postData) {
        var checkResult = {};
        if (!postData.batchNo) {
            checkResult.success = false;
            checkResult.reason = '批次号不能为空！';
            return checkResult;
        }
        if (!postData.businessNo) {
            checkResult.success = false;
            checkResult.reason = '单据号不能为空！';
            return checkResult;
        }
        var rows = itemListGrid.getData();
        if (rows.length == 0) {
            checkResult.success = false;
            checkResult.reason = '物料明细不能为空！';
            return checkResult;
        }
        itemListGrid.validate();
        if (!itemListGrid.isValid()) {
            var error = itemListGrid.getCellErrors()[0];
            itemListGrid.beginEditCell(error.record, error.column);
            checkResult.success = false;
            checkResult.reason = error.column.header + error.errorText;
            return checkResult;
        }
        checkResult.success = true;
        return checkResult;
    }
    //..
    function itemListGridCellValidation(e) {
        if (e.field == 'machineModel' && !e.value) {
            e.isValid = false;
            e.errorText = '不能为空';
        }
        if (e.field == 'materialCode' && !e.value) {
            e.isValid = false;
            e.errorText = '不能为空';
        }
        if (e.field == 'materialDescription' && !e.value) {
            e.isValid = false;
            e.errorText = '不能为空';
        }
        if (e.field == 'hsCode' && !e.value) {
            e.isValid = false;
            e.errorText = '不能为空';
        }
        if (e.field == 'elements' && !e.value) {
            e.isValid = false;
            e.errorText = '不能为空';
        }
    }
    //..
    function addItem() {
        var newRow = {}
        itemListGrid.addRow(newRow, itemListGrid.total - 1);
    }
    //..
    function deleteItem() {
        var rows = itemListGrid.getSelecteds();
        if (rows.length == 0) {
            mini.alert("请至少选中一条记录");
            return;
        }
        delRowGrid("itemListGrid");
    }
    //..常规渲染
    function render(e) {
        if (e.value != null && e.value != "") {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            return html;
        }
    }
    //..
    function openImportWindow() {
        if (!businessId) {
            mini.alert("请先点击暂存进行表单的保存");
            return;
        }
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
            xhr.open('POST', jsUseCtxPath +
                '/serviceEngineering/core/customsDeclarationRawdata/importItem.do?businessId=' + businessId, false);
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
        window.location.reload();
    }
    //..
    function clearUploadFile() {
        $("#inputFile").val('');
        mini.get("fileName").setValue('');
    }
    //..
    function downImportTemplate() {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + "/serviceEngineering/core/customsDeclarationRawdata/importItemTDownload.do");
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
            } else {
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
    function search() {
        var machineModel = mini.get("machineModelFilter").getValue();
        var materialCode = mini.get("materialCodeFilter").getValue();
        var elements = mini.get("elementsFilter").getValue();
        var isSupplement = mini.get("isSupplementFilter").getValue();
        itemListGrid.load({machineModel: machineModel, materialCode: materialCode, elements: elements, isSupplement: isSupplement});
    }
    //..
    function onKeyEnter(e) {
        search();
    }
    //..补充流程
    function supplementItem() {
        if (itemListGrid.getChanges().length > 0) {
            return {"result": false, "message": "明细有变化，请先点击暂存进行表单明细的更新"};
        }
        var rows = itemListGrid.getSelecteds();
        if (rows.length == 0) {
            mini.alert("请至少选中一条记录");
            return;
        }
        if (!mini.get("processUser").getValue()) {
            mini.alert("请选择收集信息的负责人");
            return;
        }
        mini.confirm("确定启动补充流程", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                _SubmitJson({
                    url: jsUseCtxPath + "/serviceEngineering/core/customsDeclarationRawdata/createSupplement.do?processUserId=" +
                    mini.get("processUser").getValue(),
                    method: 'POST',
                    data: rows,
                    postJson: true,
                    showMsg: false,
                    success: function (returnData) {
                        if (returnData.success) {
                            mini.alert(returnData.message)
                            itemListGrid.reload();
                            mini.get("processUser").setValue("");
                            mini.get("processUser").setText("");
                        } else {
                            mini.alert("启动失败:" + returnData.message);
                        }
                    },
                    fail: function (returnData) {
                        mini.alert("启动失败:" + returnData.message);
                    }
                });
            }
        });
    }
</script>
</body>
</html>
