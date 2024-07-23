<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>原理图归档</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}" type="text/javascript"></script>
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
        <form id="formBusiness" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <input id="keyUserId" name="keyUserId" class="mini-hidden"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0" style="height: 100%;width: 100%">
                <caption style="font-weight: bold;font-size: 20px !important;">原理图归档信息采集</caption>
                <tr>
                    <td style="text-align: center;width: 15%">物料编码:</td>
                    <td style="min-width:170px">
                        <input id="materialCode" name="materialCode" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%">原理图号:</td>
                    <td style="min-width:170px">
                        <input id="diagramCode" name="diagramCode" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">原理图类型:</td>
                    <td style="min-width:170px">
                        <input id="diagramType" name="diagramType" class="mini-combobox" style="width:98%;"
                               valueField="key" textField="value"
                               data="[{'key' : '电气','value' : '电气'},{'key' : '液压','value' : '液压'}]"/>
                    </td>
                    <td style="text-align: center;width: 15%">原理图负责人:</td>
                    <td>
                        <input id="repUserId" name="repUserId" showclose="true" textname="repUserName" class="mini-user rxc"
                               plugins="mini-user" style="width:98%;height:34px;" allowinput="false"
                               length="50" maxlength="50" mainfield="no" single="true"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">手册编码:</td>
                    <td style="min-width:170px">
                        <input id="manualCode" name="manualCode" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%">文件名称:</td>
                    <td style="min-width:170px">
                        <input id="manualDescription" name="manualDescription" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">文件语言:</td>
                    <td style="min-width:170px">
                        <input id="manualLanguage" name="manualLanguage" class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=serviceEngineeringLanguage"
                               valueField="key" textField="value"/>
                    </td>
                    <td style="text-align: center;width: 15%">版本:</td>
                    <td style="min-width:170px">
                        <input id="manualVersion" name="manualVersion" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">归档人:</td>
                    <td>
                        <input id="keyUser" name="keyUser" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 15%">归档时间:</td>
                    <td>
                        <input id="publishTime" name="publishTime" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">文件状态:</td>
                    <td>
                        <input id="manualStatus" name="manualStatus" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 15%">图册文档：</td>
                    <td>
                        <input class="mini-textbox" style="width:70%;float: left" id="fileName" name="fileName" readonly/>
                        <input id="inputFile" style="display:none;" type="file" onchange="getSelectFile()" accept="application/pdf"/>
                        <a id="uploadFileBtn" class="mini-button" style="float: left;margin-left: 10px" onclick="uploadFile()">选择文件</a>
                        <a id="clearFileBtn" class="mini-button" style="float: left;margin-left: 10px" onclick="clearUploadFile()">清除</a>
                    </td>
                </tr>
                <td style="width: 12%;text-align: center">备注：
                </td>
                <td colspan="3">
                    <input id="note" name="note" class="mini-textarea"
                           style="width:99%;height: 100px;"/>
                </td>
                <%--///////////////////////////////////////////////////--%>
                <tr>
                    <td style="text-align: center;height: 400px">关联明细：</td>
                    <td colspan="3">
                        <div class="mini-toolbar" id="itemButtons">
                            <a id="addItem" class="mini-button" style="margin-bottom: 5px;margin-top: 5px" onclick="addItem">添加</a>
                            <a id="deleteItem" class="mini-button btn-red" style="margin-bottom: 5px;margin-top: 5px" onclick="deleteItem">删除</a>
                        </div>
                        <div id="itemListGrid" class="mini-datagrid" style="width: 100%; height: 85%" allowResize="true" allowCellWrap="false"
                             showCellTip="true" autoload="true" idField="id" multiSelect="true" showPager="false"
                             showColumnsMenu="false" allowcelledit="true" allowcellselect="true"
                             allowAlternating="true" oncellvalidation="onCellValidation">
                            <div property="columns">
                                <div type="checkcolumn" width="20"></div>
                                <div field="salesModel_item" width="100" headerAlign="center" align="center" renderer="render">
                                    销售型号<input property="editor" class="mini-textbox" style="width: 100%"/>
                                </div>
                                <div field="designModel_item" width="100" headerAlign="center" align="center" renderer="render">
                                    设计型号<input property="editor" class="mini-textbox" style="width: 100%"/>
                                </div>
                                <div field="materialCode_item" width="100" headerAlign="center" align="center" renderer="render">
                                    物料编码<input property="editor" class="mini-textbox" style="width: 100%"/>
                                </div>
                                <div field="cpzgId_item" width="100" headerAlign="center" displayField="cpzgId_item_name" align="center" width="30">
                                    产品主管
                                    <%--lwgkiller：此处在行内用mini-user组件，textname无效，默认会以name+"_name"作为textname--%>
                                    <input property="editor" class="mini-user rxc" plugins="mini-user" allowinput="false"
                                           mainfield="no" name="cpzgId_item"/>
                                </div>
                            </div>
                        </div>
                    </td>
                </tr>
                <%--///////////////////////////////////////////////////--%>
                <tr>
                    <td style="text-align: center;height: 400px">附件列表：</td>
                    <td colspan="3">
                        <div style="margin-top: 10px;margin-bottom: 2px">
                            <a id="addFile" class="mini-button" style="display: none">添加文件</a>
                            <span style="color: red">注：添加文件前，请先进行保存</span>
                        </div>
                        <div id="fileListGrid" class="mini-datagrid" style="width: 100%; height: 85%" allowResize="false"
                             idField="id" autoload="true"
                             url="${ctxPath}/serviceEngineering/core/schematicDiagram/getFileList.do?businessId=${businessId}"
                             multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
                            <div property="columns">
                                <div type="indexcolumn" align="center" width="20">序号</div>
                                <div field="fileName" align="center" headerAlign="center" width="150">文件名</div>
                                <div field="fileSize" align="center" headerAlign="center" width="60">文件大小</div>
                                <div field="fileDesc" align="center" headerAlign="center" width="100">备注说明</div>
                                <div field="action" width="100" headerAlign='center' align="center" renderer="operationRenderer">操作</div>
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
    var action = "${action}";
    var jsUseCtxPath = "${ctxPath}";
    var obj =${obj};
    var currentTime = "${currentTime}";
    var currentUserId = "${currentUserId}";
    var currentUserName = "${currentUserName}";
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";
    var formBusiness = new mini.Form("#formBusiness");
    var businessId = "${businessId}";
    var fileListGrid = mini.get("fileListGrid");
    var itemListGrid = mini.get("itemListGrid");
    //..
    $(function () {
        formBusiness.setData(obj);
        var remarkArray = JSON.parse(obj.remark);
        itemListGrid.setData(remarkArray);
        if (action == 'detail') {
            formBusiness.setEnabled(false);
            mini.get("saveBusiness").setEnabled(false);
            mini.get("uploadFileBtn").setEnabled(false);
            mini.get("clearFileBtn").setEnabled(false);
            mini.get("addItem").setEnabled(false);
            mini.get("deleteItem").setEnabled(false);
        } else if (action == "copy") {
            mini.get("id").setValue("");
            mini.get("manualVersion").setValue("");
            mini.get("keyUserId").setValue("");
            mini.get("keyUser").setValue("");
            mini.get("publishTime").setValue("");
            mini.get("manualDescription").setValue("");
            mini.get("manualStatus").setValue("编辑中");
        }
    });
    //..
    function saveBusiness() {
        var formData = formBusiness.getData();
        var data = itemListGrid.getData();
        if (data.length > 0) {
            formData.itemChangeData = data;
        }
        //
        var checkResult = checkEditRequired(formData);
        if (!checkResult) {
            return;
        }
        var file = null;
        var fileList = $("#inputFile")[0].files;
        if (fileList && fileList.length > 0) {
            file = fileList[0];
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
                            mini.alert(message, '提示信息', function (action) {
                                if (returnObj.success) {
                                    mini.get("id").setValue(returnObj.id);
                                }
                                CloseWindow();
                            });
                        }
                    }
                }
            };
            //开始上传
            xhr.open('POST', jsUseCtxPath + '/serviceEngineering/core/schematicDiagram/saveBusiness.do', false);
            xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
            var fd = new FormData();
            if (formData) {
                for (key in formData) {
                    if (key == 'itemChangeData') {
                        fd.append(key, JSON.stringify(formData[key]));
                    } else {
                        fd.append(key, formData[key]);
                    }
                }
            }
            fd.append('businessFile', file);
            xhr.send(fd);
        }
    }
    //..
    function checkEditRequired(formData) {
        if (!$.trim(formData.materialCode)) {
            mini.alert('物料编码不能为空！');
            return false;
        }
        if (!$.trim(formData.diagramCode)) {
            mini.alert('原理图号不能为空！');
            return false;
        }
        if (!$.trim(formData.diagramType)) {
            mini.alert('原理图类型不能为空！');
            return false;
        }
        if (!$.trim(formData.repUserId)) {
            mini.alert('原理图负责人不能为空！');
            return false;
        }
        if (!$.trim(formData.manualCode)) {
            mini.alert('手册编号不能为空！');
            return false;
        }
        if (!$.trim(formData.manualLanguage)) {
            mini.alert('语言不能为空！');
            return false;
        }
        if (!$.trim(formData.manualVersion)) {
            mini.alert('版本不能为空！');
            return false;
        }
        //明细表单验证
        itemListGrid.validate();
        if (!itemListGrid.isValid()) {
            var error = itemListGrid.getCellErrors()[0];
            itemListGrid.beginEditCell(error.record, error.column);
            mini.alert(error.column.header + error.errorText);
            return;
        }
        return true;
    }
    //..
    function uploadFile() {
        $("#inputFile").click();
    }
    //..
    function getSelectFile() {
        var fileList = $("#inputFile")[0].files;
        if (fileList && fileList.length > 0) {
            var fileNameSuffix = fileList[0].name.split('.').pop();
            if (fileNameSuffix == 'pdf') {
                mini.get("fileName").setValue(fileList[0].name);
            }
            else {
                clearUploadFile();
                mini.alert('请上传pdf文件！');
            }
        }
    }
    //..
    function clearUploadFile() {
        $("#inputFile").val('');
        mini.get("fileName").setValue('');
    }
    //..文件列表操作渲染
    function operationRenderer(e) {
        var record = e.record;
        var cellHtml = '';
        cellHtml = returnPreviewSpan(record.fileName, record.id, record.mainId, coverContent);
        //增加删除按钮
        if (action == 'edit') {
            //和pdf预览用的同一个url
            var downLoadUrl = '/serviceEngineering/core/schematicDiagram/PdfPreviewAndAllDownload.do';
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="downLoadFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.mainId + '\',\'' + downLoadUrl + '\')">下载</span>';
            var deleteUrl = "/serviceEngineering/core/schematicDiagram/delFile.do"
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.mainId + '\',\'' + deleteUrl + '\')">删除</span>';
        }
        return cellHtml;
    }
    //..文件列表预览渲染
    function returnPreviewSpan(fileName, fileId, formId, coverContent) {
        var fileType = getFileType(fileName);
        var s = '';
        if (fileType == 'other') {
            s = '<span  title="预览" style="color: silver" >预览</span>';
        } else if (fileType == 'pdf') {
            var url = '/serviceEngineering/core/schematicDiagram/PdfPreview.do';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        } else if (fileType == 'office') {
            var url = '/serviceEngineering/core/schematicDiagram/OfficePreview.do';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        } else if (fileType == 'pic') {
            var url = '/serviceEngineering/core/schematicDiagram/ImagePreview.do';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        }
        return s;
    }
    //..添加文件
    function addBusinessFile() {
        var businessId = mini.get("id").getValue();
        if (!businessId) {
            mini.alert('请先点击‘保存’进行表单的保存！');
            return;
        }
        mini.open({
            title: "文件上传",
            url: jsUseCtxPath + "/serviceEngineering/core/schematicDiagram/fileUploadWindow.do?businessId=" + businessId,
            width: 750,
            height: 450,
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
    function render(e) {
        if (e.value != null && e.value != "") {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            return html;
        }
    }
    //..
    function addItem() {
        var newRow = {}
        itemListGrid.addRow(newRow, 0);
    }
    //..
    function deleteItem() {
        var row = itemListGrid.getSelected();
        if (!row) {
            mini.alert("请选中一条记录");
            return;
        }
        delRowGrid("itemListGrid");
    }
    //..
    function onCellValidation(e) {
        if (e.field == 'salesModel_item' && (!e.value || e.value == '')) {
            e.isValid = false;
            e.errorText = '不能为空！';
        }
        if (e.field == 'designModel_item' && (!e.value || e.value == '')) {
            e.isValid = false;
            e.errorText = '不能为空！';
        }
        if (e.field == 'materialCode_item' && (!e.value || e.value == '')) {
            e.isValid = false;
            e.errorText = '不能为空！';
        }
        if (e.field == 'cpzgId_item' && (!e.value || e.value == '')) {
            e.isValid = false;
            e.errorText = '不能为空！';
        }
    }
</script>
</body>
</html>
