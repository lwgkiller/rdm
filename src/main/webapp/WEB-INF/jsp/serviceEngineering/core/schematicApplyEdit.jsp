<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>新增</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
    <style>
        html, body {
            overflow: hidden;
            height: 100%;
            width: 100%;
            padding: 0;
            margin: 0;
        }

        .table-detail > tbody > tr > td {
            border: 1px solid #eee;
            background-color: #fff;
            white-space: normal;
            word-break: break-all;
            color: rgb(85, 85, 85);
            font-weight: normal;
            padding: 4px;
            height: 40px;
            min-height: 40px;
            box-sizing: border-box;
            font-size: 15px;
        }
    </style>
</head>
<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="processInfo" class="mini-button" style="display: none" onclick="processInfo()">流程信息</a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin: 0 auto; width: 80%;">
        <form id="formSchematicApply" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <input id="CREATE_BY_" name="CREATE_BY_" class="mini-hidden"/>
            <input id="instId" name="instId" class="mini-hidden"/>
            <table class="table-detail" cellspacing="1" cellpadding="0">
                <caption style="font-weight: bold;font-size: 20px !important;">
                    原理图需求申请
                </caption>
                <tr>
                    <td style="width: 25%;text-align: center">单据编号(提交后自动生成)：</td>
                    <td style="width: 20%">
                        <input id="noticeNo" name="noticeNo" class="mini-textbox" readonly style="width:98%;"/>
                    </td>
                </tr>
                <td style="width: 25%;text-align: center">申请人：</td>
                <td>
                    <input id="creator" name="creator" class="mini-textbox" readonly style="width:98%;"/>
                </td>
                <td style="width: 25%;text-align: center">申请时间：
                </td>
                <td>
                    <input id="CREATE_TIME_" name="CREATE_TIME_" readonly class="mini-datepicker"
                           format="yyyy-MM-dd" showTime="false" style="width:98%;"/>
                </td>
                </tr>
                <tr>
                    <td style="width: 20%;text-align: center">整机编码(17位)：</td>
                    <td style="width: 20%">
                        <input id="machineNum" name="machineNum" class="mini-textbox" style="  width:98%;"/>
                    </td>
                    <td style="width: 10%;text-align: center">原理图类型：</td>
                    <td style="width: 23%;min-width:170px;font-size:14pt">
                        <input property="editor" id="schematicType" name="schematicType" class="mini-combobox"
                               style="width:98%;"
                               data="[{id:'电气原理图',text:'电气原理图'},{id:'液压原理图',text:'液压原理图'}]"/>
                    </td>
                </tr>
                <tr>
                    <td style="  width: 20%;text-align: center">设计型号：</td>
                    <td style="width: 20%">
                        <input id="model" style="width:98%;" class="mini-buttonedit" showClose="true"
                               oncloseclick="onRelModelCloseClick()"
                               name="modelId" textname="modelName" allowInput="false"
                               onbuttonclick="selectModelClick()"/>
                    </td>
                    <td style="  width: 20%;text-align: center">销售型号：</td>
                    <td style="width: 20%">
                        <input id="saleModel" name="saleModel" class="mini-textbox" readonly style="  width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="  width: 20%;text-align: center">物料编码：</td>
                    <td style="width: 20%">
                        <input id="materialCode" name="materialCode" class="mini-textbox" readonly style="  width:98%;"/>
                    </td>
                    <td style="  width: 20%;text-align: center">责任人：</td>
                    <td style="width: 20%">
                        <input id="zrrId" name="zrrId" textname="zrrName"
                               property="editor" class="mini-user rxc" plugins="mini-user"
                               style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 20%;text-align: center">销售区域：</td>
                    <td style="width: 20%">
                        <input id="saleArea" name="saleArea" class="mini-textbox" style="  width:98%;"/>
                    </td>
                    <td style="width: 12%;text-align: center">需求时间：
                    </td>
                    <td>
                        <input id="needTime" name="needTime" class="mini-datepicker"
                               format="yyyy-MM-dd" showTime="false" style="width:98%;"/>
                    </td>
                </tr>
                <td style="width: 12%;text-align: center">备注：
                </td>
                <td colspan="3">
                    <input id="note" name="note" class="mini-textarea"
                           style="width:99%;height: 100px;"/>
                </td>
                <tr>
                    <td style="width:15%;height: 400px;text-align: center">原理图详细内容：</td>
                    <td colspan="3">
                        <div style="margin-bottom: 2px">
                            <a id="addDetail" class="mini-button" onclick="addDetail()">添加</a>
                            <a id="removeDetail" class="mini-button" onclick="removeDetail()">删除</a>
                            <span style="color: red">注：添加前请先进行表单的保存、添加或删除后请点击保存以生效</span>
                        </div>
                        <div id="detailListGrid" class="mini-datagrid" style="width: 100%; height: 85%"
                             allowResize="false" allowCellWrap="true"
                             idField="id" url="${ctxPath}/serviceEngineer/core/SchematicApply/queryDetail.do?belongId=${id}"
                             autoload="true" allowCellEdit="true" allowCellSelect="true"
                             multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
                            <div property="columns">
                                <div type="checkcolumn" width="10"></div>
                                <div type="indexcolumn" align="center" headerAlign="center" width="20">序号</div>
                                <div field="detailMaterialCode"  width="60" headerAlign="center" align="center">原理图物料编码
                                    <input property="editor" class="mini-textbox"/></div>
                                <div field="detailSchematicNo" width="60" headerAlign="center" align="center">原理图图号
                                    <input property="editor" class="mini-textbox"/></div>
                                <div  width="60" headerAlign="center" align="center" renderer="fileUpload">MDS源文件</div>
                                <div field="detailNote" width="60" headerAlign="center" align="center">备注
                                    <input property="editor" class="mini-textbox"/></div>
                            </div>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td style="width: 10%;text-align: center">原理图版本：</td>
                    <td style="width: 23%;min-width:170px;font-size:14pt">
                        <input property="editor" id="schematicVersion" name="schematicVersion" class="mini-combobox"
                               style="width:98%;"
                               data="[{id:'A',text:'A'},{id:'B',text:'B'},{id:'C',text:'C'}]"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 12%;text-align: center">更改内容：
                    </td>
                    <td colspan="5">
                        <input id="changeContent" name="changeContent" class="mini-textarea"
                               style="width:99%;height: 100px;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 20%;height: 300px;text-align: center">更改通知单号附件：</td>
                    <td colspan="3">
                        <div style="margin-top: 2px">
                            <a id="addChangeFile" class="mini-button" onclick="fileuploadchange()">添加附件</a>
                        </div>
                        <div id="fileListGrid" class="mini-datagrid" style="width: 100%; height: 80%"
                             allowResize="false"
                             idField="id" autoload="true"
                             url="${ctxPath}/serviceEngineer/core/SchematicApply/getSchematicApplyFileList.do?id=${id}"
                             multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
                            <div property="columns">
                                <div type="indexcolumn" align="center" width="15">序号</div>
                                <div field="fileName" width="140" headerAlign="center" align="center">附件名称</div>
                                <div field="fileSize" width="80" headerAlign="center" align="center">附件大小</div>
                                <div field="action" width="100" headerAlign='center' align="center"
                                     renderer="operationRenderer">操作
                                </div>
                            </div>
                        </div>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
<div id="selectModelWindow" title="选择设计型号" class="mini-window" style="width:900px;height:550px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="true">
    <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px"
         borderStyle="border-left:0;border-top:0;border-right:0;">
        <span class="text" style="width:auto">设计型号: </span><input
            class="mini-textbox" style="width: 120px" id="designModel" name="designModel"/>
        <span class="text" style="width:auto">产品所: </span><input
            class="mini-textbox" style="width: 120px" id="departName" name="departName"/>
        <span class="text" style="width:auto">产品主管: </span><input
            class="mini-textbox" style="width: 120px" id="productManagerName" name="productManagerName"/>
        <a class="mini-button" iconCls="icon-search" plain="true" onclick="searchModel()">查询</a>
    </div>
    <div class="mini-fit">
        <div id="selectModelListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
             idField="id" showColumnsMenu="false" 
             allowAlternating="true" showPager="true" multiSelect="false"
             url="${ctxPath}/world/core/productSpectrum/dataListQuery.do?">
            <div property="columns">
                <div type="checkcolumn" width="20"></div>
                <div field="materialCode" width="150" headerAlign="center" align="center" renderer="render"
                     allowSort="true">
                    物料号
                </div>
                <div field="designModel" width="150" headerAlign="center" align="center" renderer="render"
                     allowSort="true">
                    设计型号
                </div>
                <div field="departName" width="120" headerAlign="center" align="center" renderer="render"
                     allowSort="true">
                    产品所
                </div>
                <div field="productManagerName" width="80" headerAlign="center" align="center" renderer="render"
                     allowSort="true">
                    产品主管
                </div>
                <div field="saleModel" width="120" headerAlign="center" align="center" renderer="render"
                     allowSort="true">
                    销售型号
                </div>
            </div>
        </div>
    </div>
    <div property="footer" style="padding:5px;height: 35px">
        <table style="width:100%;height: 100%">
            <tr>
                <td style="width:120px;text-align:center;">
                    <input type="button" style="height: 25px;width: 70px" value="确定" onclick="selectModelOK()"/>
                    <input type="button" style="height: 25px;width: 70px" value="取消" onclick="selectModelHide()"/>
                </td>
            </tr>
        </table>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var status = "${status}";
    var action = "${action}";
    var jsUseCtxPath = "${ctxPath}";
    var id = "${id}";
    var formSchematicApply = new mini.Form("#formSchematicApply");
    var fileListGrid = mini.get("fileListGrid");
    var detailListGrid = mini.get("detailListGrid");
    var selectModelWindow = mini.get("selectModelWindow");
    var selectModelListGrid = mini.get("selectModelListGrid");
    var currentUserName = "${currentUserName}";
    var nodeVarsStr = '${nodeVars}';
    var currentUserId = "${currentUserId}";
    var currentTime = "${currentTime}";
    var deptName = "${deptName}";
    var currentUserMainDepId = "${currentUserMainDepId}";
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";
    var oldData = '${oldData}';

    function fileUpload(e) {
        var record = e.record;
        var id = record.id;
        var s = '';
        if(id){
            s += '<span style="color:dodgerblue" title="附件列表" onclick="mdsFile(\'' + id + '\')">附件列表</span>';
        }else {
            s += '<span style="color:dodgerblue" title="请先保存">请先保存</span>';
        }

        return s;
    }

    function mdsFile(id) {
        if(id) {
            mini.open({
                title: "附件列表",
                url: jsUseCtxPath + "/serviceEngineer/core/SchematicApply/fileList.do?&id=" + id+"&action="+action+"&stageName="+stageName,
                width: 1050,
                height: 550,
                allowResize: true,
                onload: function () {
                }
            });
        }
    }

    function fileuploadchange() {
        var id = mini.get("id").getValue();
        if (!id) {
            mini.alert('请先点击‘保存草稿’进行表单的保存！');
            return;
        }
        mini.open({
            title: "添加附件",
            url: jsUseCtxPath + "/serviceEngineer/core/SchematicApply/openUploadWindow.do?id=" + id,
            width: 850,
            height: 550,
            showModal: true,
            allowResize: true,
            ondestroy: function () {
                if (fileListGrid) {
                    fileListGrid.load();
                }
            }
        });
    }

    function operationRenderer(e) {
        var record = e.record;
        var cellHtml = '';
        cellHtml = returnSchematicApplyPreviewSpan(record.fileName, record.fileId, record.belongId, coverContent);
        var downLoadUrl = '/serviceEngineer/core/SchematicApply/schematicApplyPdfPreview.do';
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="downLoadFile(\'' + record.fileName + '\',\'' + record.fileId + '\',\'' + record.belongId + '\',\'' + downLoadUrl + '\')">下载</span>';
        //增加删除按钮
        if ((record.CREATE_BY_ == currentUserId && action != 'detail')||stageName=='forth') {
            var deleteUrl = "/serviceEngineer/core/SchematicApply/deleteSchematicApplyFile.do"
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(\'' + record.fileName + '\',\'' + record.fileId + '\',\'' + record.belongId + '\',\'' + deleteUrl + '\')">删除</span>';
        }
        return cellHtml;
    }


    var stageName = "";
    $(function () {
        if (id) {
            var url = jsUseCtxPath + "/serviceEngineer/core/SchematicApply/getSchematicApplyDetail.do";
            $.post(
                url,
                {id: id},
                function (json) {
                    formSchematicApply.setData(json);
                });
        }
        if (action == 'task') {
            taskActionProcess();
        } else if (action == "detail") {
            formSchematicApply.setEnabled(false);
            detailListGrid.setAllowCellEdit(false);
            mini.get("addDetail").setEnabled(false);
            mini.get("removeDetail").setEnabled(false);
            mini.get("addChangeFile").setEnabled(false);
            $("#detailToolBar").show();
            if (status != 'DRAFTED') {
                $("#processInfo").show();
            }
        }else if (action == "edit") {
            mini.get("addChangeFile").setEnabled(false);
            formSchematicApply.setEnabled(false);
            mini.get("addDetail").setEnabled(false);
            mini.get("removeDetail").setEnabled(false);
            mini.get("machineNum").setEnabled(true);
            mini.get("schematicType").setEnabled(true);
            mini.get("note").setEnabled(true);
        }
        if(oldData){
            formSchematicApply.setData(JSON.parse(oldData));
        }
    });

    function getData() {
        var formData = _GetFormJsonMini("formSchematicApply");
        formData.detail = detailListGrid.getChanges();
        return formData;
    }

    function saveSchematicApply(e) {
        var formValid = validFirst();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        window.parent.saveDraft(e);
    }

    function saveSchematicApplyInProcess(id) {
        var formData = getData();
        $.ajax({
            url: jsUseCtxPath + '/serviceEngineer/core/SchematicApply/saveSchematicApply.do?id=' + id,
            type: 'post',
            async: false,
            data: mini.encode(formData),
            contentType: 'application/json',
            success: function (data) {
                if (data) {
                    var message = "";
                    if (data.success) {
                        message = "数据保存成功";
                    } else {
                        message = "数据保存失败" + data.message;
                    }

                    mini.alert(message, "提示信息", function () {
                        window.location.reload();
                    });
                }
            }
        });
    }

    function startSchematicApplyProcess(e) {
        var formValid = validFirst();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        window.parent.startProcess(e);
    }

    function validFirst() {
        var machineNum = $.trim(mini.get("machineNum").getValue());
        if (!machineNum) {
            return {"result": false, "message": "请填写整机编码"};
        }
        if(machineNum.length>17){
            return {"result": false, "message": "只能填写一个整机编码"};
        }
        var schematicType = $.trim(mini.get("schematicType").getValue());
        if (!schematicType) {
            return {"result": false, "message": "请填写原理图类型"};
        }
        return {"result": true};
    }

    function validSecond() {
        var model = $.trim(mini.get("model").getValue());
        if (!model) {
            return {"result": false, "message": "请选择设计型号"};
        }
        var materialCode = $.trim(mini.get("materialCode").getValue());
        if (!materialCode) {
            return {"result": false, "message": "请填写物料编码"};
        }
        var zrrId = $.trim(mini.get("zrrId").getValue());
        if (!zrrId) {
            return {"result": false, "message": "请选择责任人"};
        }
        var saleArea = $.trim(mini.get("saleArea").getValue());
        if (!saleArea) {
            return {"result": false, "message": "请填写销售区域"};
        }
        var needTime = $.trim(mini.get("needTime").getValue());
        if (!needTime) {
            return {"result": false, "message": "请选择需求时间"};
        }
        return {"result": true};
    }

    function validThird() {
        var detail = detailListGrid.getData();
        if (detail.length < 1) {
            return {"result": false, "message": "请添加原理图详细信息"};
        } else {
            for (var i = 0; i < detail.length; i++) {
                if (detail[i].detailMaterialCode == undefined || detail[i].detailMaterialCode == "") {
                    return {"result": false, "message": "请填写原理图物料编码"};
                }
                if (detail[i].detailSchematicNo == undefined || detail[i].detailSchematicNo == "") {
                    return {"result": false, "message": "请填写原理图图号"};
                }
            }
        }
        return {"result": true};
    }
    

    function schematicApplyApprove() {
        //编制阶段的下一步需要校验表单必填字段
        if (stageName == 'first') {
            var formValid = validFirst();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }
        if (stageName == 'second') {
            var formValid = validSecond();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }
        if (stageName == 'third') {
            var formValid = validThird();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }
        //检查附件
        $.ajaxSettings.async = false;
        var id = mini.get('id').getValue();
        var url = jsUseCtxPath + "/serviceEngineer/core/SchematicApply/approveValid.do?id=" + id;
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
        formSchematicApply.setEnabled(false);
        detailListGrid.setAllowCellEdit(false);
        mini.get("addChangeFile").setEnabled(false);
        mini.get("addDetail").setEnabled(false);
        mini.get("removeDetail").setEnabled(false);
        var nodeVars = $.parseJSON(nodeVarsStr);
        for (var i = 0; i < nodeVars.length; i++) {
            if (nodeVars[i].KEY_ == 'stageName') {
                stageName = nodeVars[i].DEF_VAL_;
            }
        }
        if (stageName == 'first') {
            mini.get("machineNum").setEnabled(true);
            mini.get("schematicType").setEnabled(true);
            mini.get("note").setEnabled(true);
        }
        if (stageName == 'second') {
            mini.get("saleModel").setEnabled(true);
            mini.get("materialCode").setEnabled(true);
            mini.get("model").setEnabled(true);
            mini.get("saleArea").setEnabled(true);
            mini.get("zrrId").setEnabled(true);
            mini.get("needTime").setEnabled(true);
        }
        if (stageName == 'third') {
            mini.get("addDetail").setEnabled(true);
            mini.get("removeDetail").setEnabled(true);
            detailListGrid.setAllowCellEdit(true);
            mini.get("changeContent").setEnabled(true);
            mini.get("schematicVersion").setEnabled(true);
            mini.get("addChangeFile").setEnabled(true);
        }
        if (stageName == 'forth') {
            formSchematicApply.setEnabled(true);
            detailListGrid.setAllowCellEdit(true);
            mini.get("zrrId").setEnabled(false);
            mini.get("addDetail").setEnabled(true);
            mini.get("removeDetail").setEnabled(true);
            mini.get("addChangeFile").setEnabled(true);
        }
    }


    function returnSchematicApplyPreviewSpan(fileName, fileId, formId, coverContent) {
        var fileType = getFileType(fileName);
        var s = '';
        if (fileType == 'other') {
            s = '<span  title="预览" style="color: silver" >预览</span>';
        } else if (fileType == 'pdf') {
            var url = '/serviceEngineer/core/SchematicApply/schematicApplyApplyPdfPreview';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        } else if (fileType == 'office') {
            var url = '/serviceEngineer/core/SchematicApply/schematicApplyOfficePreview';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        } else if (fileType == 'pic') {
            var url = '/serviceEngineer/core/SchematicApply/schematicApplyImagePreview';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        }
        return s;
    }

    function addDetail() {
        var formId = mini.get("id").getValue();
        if (!formId) {
            mini.alert("请先点击‘保存草稿’进行表单创建!");
            return;
        } else {
            var row = {};
            detailListGrid.addRow(row);
        }
    }

    function removeDetail() {
        var selecteds = detailListGrid.getSelecteds();
        if (selecteds.length <= 0) {
            mini.alert("请选择一条记录");
            return;
        }
        var deleteArr = [];
        for (var i = 0; i < selecteds.length; i++) {
            var row = selecteds[i];
            deleteArr.push(row);
        }
        detailListGrid.removeRows(deleteArr);
    }


    function selectModelClick() {
        selectModelWindow.show();
        searchModel();
    }

    function searchModel() {
        var queryParam = [];
        //其他筛选条件
        var designModel = $.trim(mini.get("designModel").getValue());
        if (designModel) {
            queryParam.push({name: "designModel", value: designModel});
        }
        var departName = $.trim(mini.get("departName").getValue());
        if (departName) {
            queryParam.push({name: "departName", value: departName});
        }
        var productManagerName = $.trim(mini.get("productManagerName").getValue());
        if (productManagerName) {
            queryParam.push({name: "productManagerName", value: productManagerName});
        }
        var data = {};
        data.filter = mini.encode(queryParam);
        //查询
        selectModelListGrid.load(data);
    }

    function selectModelOK() {
        var rowSelected = selectModelListGrid.getSelected();
        if (rowSelected) {
            mini.get("model").setValue(rowSelected.id);
            mini.get("model").setText(rowSelected.designModel);
            mini.get("materialCode").setValue(rowSelected.materialCode);
            mini.get("saleModel").setValue(rowSelected.saleModel);
            selectModelHide();
        }
    }

    function selectModelHide() {
        selectModelWindow.hide();
        mini.get("designModel").setValue('');
        mini.get("productManagerName").setValue('');
        mini.get("departName").setValue('');
        selectModelListGrid.clearSelect();
    }

    function onRelModelCloseClick() {
        mini.get("model").setValue('');
        mini.get("model").setText('');
        mini.get("materialCode").setValue('');
        mini.get("saleModel").setValue('');
        selectModelListGrid.clearSelect();
    }
</script>
</body>
</html>
