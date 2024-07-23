x
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>零部件研究方向信息采集</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>

<body>
<div id="toolBar" class="topToolBar">
    <div>
        <a id="saveBusiness" class="mini-button" style="display: none" onclick="saveBusiness()">保存</a>
        <a id="closeWindow" class="mini-button btn-red" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin:0 auto; width: 100%;padding: 0">
        <form id="businessForm" method="post">
            <input class="mini-hidden" id="id" name="id"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0" style="height: 100%;width: 100%">
                <caption style="font-weight: bold;font-size: 20px !important;">零部件研究方向信息采集</caption>
                <tr>
                    <td style="text-align: center;width: 15%">零部件名称：</td>
                    <td style="min-width:170px">
                        <input id="partsDescription" name="partsDescription" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%">研究方向：</td>
                    <td style="min-width:170px">
                        <input id="researchDirection" name="researchDirection"
                               class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=PartsResearchDirection"
                               valueField="key" textField="value"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">涉及整机型号：</td>
                    <td style="min-width:170px">
                        <input id="model" name="model" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%">研究方向来源：</td>
                    <td style="min-width:170px">
                        <input id="researchDirectionSource" name="researchDirectionSource" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">研究目标：</td>
                    <td style="min-width:170px">
                        <input id="researchObjective" name="researchObjective" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%">预计完成时间：</td>
                    <td style="min-width:170px">
                        <input id="completionTime" name="completionTime" class="mini-datepicker" format="yyyy-MM-dd"
                               showTime="false" showOkButton="true" showClearButton="false" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">主要完成人</td>
                    <td colspan="3">
                        <input id="completedByIds" name="completedByIds" textname="completedBy" class="mini-user rxc"
                               plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="主要完成人" length="50"
                               mainfield="no" single="false"/>
                    </td>
                </tr>
                <%----%>
                <tr>
                    <td style="text-align: center;height: 400px">零部研究方向月度工作内容：</td>
                    <td colspan="3">
                        <div class="mini-toolbar" id="itemButtons" style="display: none">
                            <a id="addItem" class="mini-button" style="margin-bottom: 5px;margin-top: 5px" onclick="addItem()">添加</a>
                            <a id="saveItem" class="mini-button btn-yellow" style="margin-bottom: 5px;margin-top: 5px" onclick="saveItem()">保存</a>
                            <a id="deleteItem" class="mini-button btn-red" style="margin-bottom: 5px;margin-top: 5px" onclick="deleteItem()">删除</a>
                        </div>

                        <div id="itemListGrid" class="mini-datagrid" style="width: 100%; height: 85%" allowResize="false" allowCellWrap="true"
                             idField="id"
                        <%--todo:测--%>
                             url="${ctxPath}/powerApplicationTechnology/core/partsIntegration/partsResearchDirection/getItemList.do?businessId=${businessId}"
                             multiSelect="false" showPager="false" showColumnsMenu="false" allowcelledit="true" allowcellselect="true"
                             allowAlternating="true" oncellvalidation="onCellValidation">
                            <div property="columns">
                                <div type="checkcolumn" width="20"></div>
                                <div type="indexcolumn" headerAlign="center" align="center" width="30">序号</div>
                                <div field="timeSlot" align="center" headerAlign="center" width="200" renderer="render">时间段
                                    <input property="editor" class="mini-textbox">
                                </div>
                                <div field="taskContent" align="center" headerAlign="center" width="300" renderer="render">任务内容
                                    <input property="editor" class="mini-textbox">
                                </div>
                                <div field="outputName" align="center" headerAlign="center" width="200" renderer="render">输出物名称
                                    <input property="editor" class="mini-textbox">
                                </div>
                            </div>
                        </div>
                    </td>
                </tr>
                <%----%>
                <tr>
                    <td style="text-align: center;height: 300px">零部件研究方向输出物列表：</td>
                    <td colspan="3">
                        <div class="mini-toolbar" id="fileButtons" style="display: none">
                            <a id="uploadFile" class="mini-button" style="margin-bottom: 5px;margin-top: 5px" onclick="uploadFile">添加附件</a>
                        </div>
                        <%--todo:测--%>
                        <div id="fileListGrid" class="mini-datagrid" style="width: 100%; height: 85%" allowResize="false" idField="id"
                             url="${ctxPath}/powerApplicationTechnology/core/partsIntegration/partsResearchDirection/getFileList.do?businessId=${businessId}"
                             multiSelect="false" showPager="true" showColumnsMenu="false" sizeList="[10,20,50,100]" pageSize="10"
                             allowAlternating="true" pagerButtons="#pagerButtons">
                            <div property="columns">
                                <div type="indexcolumn" headerAlign="center" align="center" width="20">序号</div>
                                <div field="fileName" align="center" headerAlign="center" width="150">文件名</div>
                                <div field="fileSize" align="center" headerAlign="center" width="60">文件大小</div>
                                <div field="fileDesc" align="center" headerAlign="center" width="100">备注说明</div>
                                <div field="action" width="80" headerAlign='center' align="center" renderer="operationRenderer">操作</div>
                            </div>
                        </div>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var businessForm = new mini.Form("#businessForm");
    var fileListGrid = mini.get("fileListGrid");
    var itemListGrid = mini.get("itemListGrid");
    var businessId = "${businessId}";
    var action = "${action}";
    var currentUserId = "${currentUserId}";
    var currentUserName = "${currentUserName}"
    var currentTime = "${currentTime}";
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";

    //..
    $(function () {
        if (businessId) {
            var url = jsUseCtxPath + "/powerApplicationTechnology/core/partsIntegration/partsResearchDirection" +
                "/queryDataById.do?businessId=" + businessId;
            $.ajax({
                url: url,
                method: 'get',
                success: function (json) {
                    businessForm.setData(json);
                }
            });
            fileListGrid.load();
            itemListGrid.load();
        }
        //不同场景的处理
        if (action == 'detail') {
            businessForm.setEnabled(false);
        } else if (action == 'edit' || action == 'add') {
            mini.get("saveBusiness").show();
            mini.get("fileButtons").show();
            mini.get("itemButtons").show();
        }
    });
    //..
    function saveBusiness() {
        var postData = {};
        postData.id = mini.get("id").getValue();
        postData.partsDescription = mini.get("partsDescription").getValue();
        postData.researchDirection = mini.get("researchDirection").getValue();
        postData.model = mini.get("model").getValue();
        postData.researchDirectionSource = mini.get("researchDirectionSource").getValue();
        postData.researchObjective = mini.get("researchObjective").getValue();
        postData.completionTime = mini.get("completionTime").getText();
        postData.completedByIds = mini.get("completedByIds").getValue();
        postData.completedBy = mini.get("completedByIds").getText();
        //检查必填项
        var checkResult = saveValidCheck(postData);
        if (!checkResult.success) {
            mini.alert(checkResult.reason);
            return;
        }
        $.ajax({
            url: jsUseCtxPath + "/powerApplicationTechnology/core/partsIntegration/partsResearchDirection/saveData.do",
            type: 'POST',
            contentType: 'application/json',
            data: mini.encode(postData),
            success: function (returnData) {
                if (returnData && returnData.message) {
                    mini.alert(returnData.message, '提示', function () {
                        if (returnData.success) {
                            var url = jsUseCtxPath + "/powerApplicationTechnology/core/partsIntegration/partsResearchDirection" +
                                "/editPage.do?businessId=" + returnData.data + "&action=edit";
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
        if (!postData.partsDescription) {
            checkResult.success = false;
            checkResult.reason = '请填写零部件名称！';
            return checkResult;
        }
        if (!postData.researchDirection) {
            checkResult.success = false;
            checkResult.reason = '请选择研究方向！';
            return checkResult;
        }
        if (!postData.model) {
            checkResult.success = false;
            checkResult.reason = '请填写涉及整机型号！';
            return checkResult;
        }
        if (!postData.researchDirectionSource) {
            checkResult.success = false;
            checkResult.reason = '请填写研究方向来源！';
            return checkResult;
        }
        if (!postData.researchObjective) {
            checkResult.success = false;
            checkResult.reason = '请填写研究目标！';
            return checkResult;
        }
        if (!postData.completionTime) {
            checkResult.success = false;
            checkResult.reason = '请填写预计完成时间！';
            return checkResult;
        }
        if (!postData.completedByIds) {
            checkResult.success = false;
            checkResult.reason = '请选择主要完成人！';
            return checkResult;
        }
        checkResult.success = true;
        return checkResult;
    }
    //..
    function addItem() {
        var newRow = {}
        newRow.id = "";
        newRow.businessId = mini.get("id").getValue();
        newRow.timeSlot = "";
        newRow.taskContent = "";
        newRow.outputName = "";
        addRowGrid("itemListGrid", newRow);
    }
    //..
    function saveItem() {
        var businessId = mini.get("id").getValue();
        if (!businessId) {
            mini.alert("请先点击‘保存’进行主记录创建！");
            return;
        }
        itemListGrid.validate();
        if (!itemListGrid.isValid()) {
            var error = itemListGrid.getCellErrors()[0];
            itemListGrid.beginEditCell(error.record, error.column);
            mini.alert(error.column.header + error.errorText);
            return;
        }
        var postData = itemListGrid.data;
        var postDataJson = mini.encode(postData);
        $.ajax({
            url: jsUseCtxPath + "/powerApplicationTechnology/core/partsIntegration/partsResearchDirection/saveItems.do",
            type: 'POST',
            contentType: 'application/json',
            data: postDataJson,
            success: function (returnData) {
                if (returnData && returnData.message) {
                    mini.alert(returnData.message, '提示', function () {
                        if (returnData.success) {
                            itemListGrid.reload();
                        }
                    });
                }
            }
        });
    }
    //..
    function deleteItem() {
        var row = itemListGrid.getSelected();
        if (!row) {
            mini.alert("请选中一条记录");
            return;
        }
        if (row.id == "") {
            delRowGrid("itemListGrid");
            return;
        }
        mini.confirm("确定删除选中记录？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                var id = row.id;
                _SubmitJson({
                    url: jsUseCtxPath + "/powerApplicationTechnology/core/partsIntegration/partsResearchDirection/deleteItem.do",
                    method: 'POST',
                    data: {id: id},
                    success: function (returnData) {
                        if (returnData) {
                            if (returnData && returnData.message) {
                                mini.alert(returnData.message, '提示', function () {
                                    if (returnData.success) {
                                        itemListGrid.reload();
                                    }
                                });
                            }
                        }
                    }
                });
            }
        });
    }
    //..
    function onCellValidation(e) {
        if (e.field == 'timeSlot' && (!e.value || e.value == '')) {
            e.isValid = false;
            e.errorText = '不能为空！';
        }
        if (e.field == 'taskContent' && (!e.value || e.value == '')) {
            e.isValid = false;
            e.errorText = '不能为空！';
        }
    }
    //..
    function uploadFile() {
        var businessId = mini.get("id").getValue();
        if (!businessId) {
            mini.alert("请先点击‘保存’进行记录创建！")
            return;
        }
        mini.open({
            title: "文件上传",
            url: jsUseCtxPath + "/powerApplicationTechnology/core/partsIntegration/partsResearchDirection/openUploadWindow.do?" +
            "businessId=" + businessId,
            width: 800,
            height: 350,
            showModal: false,
            allowResize: true,
            onload: function () {
            },
            ondestroy: function (action) {
                if (fileListGrid) {
                    fileListGrid.load();
                }
            }
        });
    }
    //..
    function operationRenderer(e) {
        var record = e.record;
        var cellHtml = '';
        cellHtml = returnPreviewSpan(record.fileName, record.id, record.businessId, coverContent);
        //和pdf预览用的同一个url
        var downLoadUrl = '/powerApplicationTechnology/core/partsIntegration/partsResearchDirection/pdfPreviewAndAllDownload.do';
        cellHtml += '&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downLoadFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.businessId + '\',\'' + downLoadUrl + '\')">下载</span>';
        //增加删除按钮
        if (action != 'detail') {
            var deleteUrl = "/powerApplicationTechnology/core/partsIntegration/partsResearchDirection/delFile.do"
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.businessId + '\',\'' + deleteUrl + '\')">删除</span>';
        }
        return cellHtml;
    }
    //..
    function returnPreviewSpan(fileName, fileId, formId, coverContent) {
        var fileType = getFileType(fileName);
        var s = '';
        if (fileType == 'other') {
            s = '<span  title="预览" style="color: silver" >预览</span>';
        } else if (fileType == 'pdf') {
            var url = '/powerApplicationTechnology/core/partsIntegration/partsResearchDirection/pdfPreviewAndAllDownload.do';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        } else if (fileType == 'office') {
            var url = '/powerApplicationTechnology/core/partsIntegration/partsResearchDirection/officePreview.do';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        } else if (fileType == 'pic') {
            var url = '/powerApplicationTechnology/core/partsIntegration/partsResearchDirection/imagePreview.do';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        }
        return s;
    }

</script>
</body>
</html>
