<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>编辑申请</title>
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
        <a id="processInfo" style="display: none" class="mini-button" onclick="processInfo()">流程信息</a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin:0 auto; width: 100%;">
        <form id="productRequireForm" method="post">
            <input id="cpkfId" name="cpkfId" class="mini-hidden"/>
            <input id="instId" name="instId" class="mini-hidden"/>
            <input id="CREATE_BY_" name="CREATE_BY_" class="mini-hidden"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0">
                <caption>
                    产品开发需求审批单
                </caption>
                <tr>
                    <td style="text-align: center;width: 20%">编制人：</td>
                    <td style="min-width:170px">
                        <input id="apply" name="applyId" textname="applyName"
                               property="editor" class="mini-user rxc" plugins="mini-user"
                               style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"/>
                    </td>
                    <td style="text-align: center;width: 20%">编制部门：</td>
                    <td style="min-width:170px">
                        <input id="dept" name="deptId" textname="deptName"
                               property="editor" class="mini-dep rxc" plugins="mini-dep"
                               style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"/>
                    </td>
                <tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        文件类别<span style="color: #ff0000">*</span>：
                    </td>
                    <td align="center" rowspan="1">
                        <input id="fileType" name="fileType" class="mini-combobox rxc" plugins="mini-combobox"
                               style="width:98%;height:34px" label="文件类别："
                               length="50" onvaluechanged="fileTypeChange()"
                               required="true" allowinput="false" shownullitem="true" multiSelect="false"
                               textField="value" valueField="key"
                               data="[{'key' : '产品开发建议书','value' : '产品开发建议书'},{'key' : '产品竞争力分析报告','value' : '产品竞争力分析报告'}]"
                               nullitemtext="请选择..." emptytext="请选择..."/>
                    </td>
                    <td style="text-align: center;width: 20%">文件版本<span style="color: #ff0000">*</span>:</td>
                    <td colspan="1">
                        <input id="version" name="version" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        是否有设计型号<span style="color: #ff0000">*</span>：
                    </td>
                    <td align="center" rowspan="1">
                        <input id="ifDesignModel" name="ifDesignModel" class="mini-combobox"
                               style="width:98%;height:34px" label="文件类别："
                               length="50" onvaluechanged="ifModelChange()"
                               required="true" allowinput="false" shownullitem="true" multiSelect="false"
                               textField="value" valueField="key"
                               data="[{'key' : '是','value' : '是'},{'key' : '否','value' : '否'}]"
                               nullitemtext="请选择..." emptytext="请选择..."/>
                    </td>
                    <td style="text-align: center;width: 20%">设计型号：</td>
                    <td>
                        <input id="designModel" style="width:98%;" class="mini-buttonedit" showClose="true"
                               oncloseclick="onRelModelCloseClick()"
                               name="productIds" textname="productName" allowInput="false"
                               onbuttonclick="selectModelClick()"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">产品主管：</td>
                    <td style="min-width:170px">
                        <input id="cpzg" name="cpzgId" textname="cpzgName"
                               property="editor" class="mini-user rxc" plugins="mini-user"
                               style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"/>
                    </td>
                    <td style="text-align: center;width: 20%">产品所：</td>
                    <td style="min-width:170px">
                        <input id="modelDept" name="modelDeptId" textname="modelDeptName"
                               property="editor" class="mini-dep rxc" plugins="mini-dep"
                               style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"/>
                    </td>
                </tr>
                <tr>

                    <td style="text-align: center">备注：</td>
                    <td>
						<textarea id="remark" name="remark" class="mini-textarea rxc" plugins="mini-textarea"
                                  style="width:98%;height:100px;line-height:25px;"
                                  label="备注" datatype="varchar" length="500" vtype="length:500" minlen="0"
                                  allowinput="true"
                                  mwidth="80" wunit="%" mheight="200" hunit="px"></textarea>
                    </td>
                    <td style="text-align: center;width: 8%">关联的科技项目：<br>(<span
                            style="color: red">负责或参与的，运行中的项目</span>)
                    </td>
                    <td>
                        <input id="projectId" name="projectId" class="mini-combobox" style="width:98%;"
                               textField="projectName" valueField="projectId" emptyText="请选择..." multiSelect="false"
                               required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                               onvaluechanged="projectChange()"/>
                        <input id="projectName" name="projectName" readonly class="mini-textbox"
                               style="width:98%;display: none"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;height: 300px">附件列表：</td>
                    <td colspan="3">
                        <div style="margin-top: 10px;margin-bottom: 2px">
                            <a id="addFile" class="mini-button" onclick="addSaleFile()">添加文件</a>
                            <span style="color: red">注：添加文件前，请先进行草稿的保存</span>
                        </div>
                        <div id="fileListGrid" class="mini-datagrid" style="width: 100%; height: 85%"
                             allowResize="false"
                             idField="id"
                             url="${ctxPath}/rdm/core/ProductRequire/getProductRequireFileList.do?fileModel=sq&belongId=${cpkfId}"
                             autoload="true"
                             multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
                            <div property="columns">
                                <div type="indexcolumn" align="center" width="20">序号</div>
                                <div field="fileName" align="center" headerAlign="center" width="150">文件名</div>
                                <div field="fileSize" align="center" headerAlign="center" width="60">文件大小</div>
                                <div field="action" width="100" headerAlign='center' align="center"
                                     renderer="operationRendererSq">操作
                                </div>
                            </div>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;height: 300px">审批意见附件列表：</td>
                    <td colspan="3">
                        <div style="margin-top: 10px;margin-bottom: 2px">
                            <a id="addExamine" class="mini-button" onclick="addExamineFile()">添加文件</a>
                            <span style="color: red">注：驳回可添加审批意见附件</span>
                        </div>
                        <div id="examineListGrid" class="mini-datagrid" style="width: 100%; height: 85%"
                             allowResize="false"
                             idField="id"
                             url="${ctxPath}/rdm/core/ProductRequire/getProductRequireFileList.do?fileModel=sp&belongId=${cpkfId}"
                             autoload="true"
                             multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
                            <div property="columns">
                                <div type="indexcolumn" align="center" width="20">序号</div>
                                <div field="fileName" align="center" headerAlign="center" width="150">文件名</div>
                                <div field="fileSize" align="center" headerAlign="center" width="60">文件大小</div>
                                <div field="userName" align="center" headerAlign="center" width="100">上传人</div>
                                <div field="CREATE_TIME_" align="center" headerAlign="center" width="100">审批时间</div>
                                <div field="action" width="100" headerAlign='center' align="center"
                                     renderer="operationRendererSp">操作
                                </div>
                            </div>
                        </div>
                    </td>
                </tr>
                <tr id="devTr1">
                    <td align="center" style="white-space: nowrap;">
                        是否开发<span style="color: #ff0000">*</span>：
                    </td>
                    <td align="center" rowspan="1">
                        <input id="ifDev" name="ifDev" class="mini-combobox"
                               style="width:98%;height:34px" label="文件类别："
                               length="50"
                               required="true" allowinput="false" shownullitem="true" multiSelect="false"
                               textField="value" valueField="key"
                               data="[{'key' : '是','value' : '是'},{'key' : '否','value' : '否'}]"
                               nullitemtext="请选择..." emptytext="请选择..."/>
                    </td>
                </tr>
                <tr id="devTr2">
                    <td style="text-align: center">处理意见：</td>
                    <td colspan="3">
						<textarea id="resolution" name="resolution" class="mini-textarea"
                                  style="width:98%;height:100px;line-height:25px;"
                                  label="处理意见" datatype="varchar" length="500" vtype="length:500" minlen="0"
                                  allowinput="true"
                                  mwidth="80" wunit="%" mheight="200" hunit="px"></textarea>
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
            class="mini-textbox" style="width: 120px" id="selectdesignModel" name="selectdesignModel"/>
        <span class="text" style="width:auto">销售型号: </span><input
            class="mini-textbox" style="width: 120px" id="selectsaleModel" name="selectsaleModel"/>
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
    var productRequireForm = new mini.Form("#productRequireForm");
    var nodeVarsStr = '${nodeVars}';
    var action = "${action}";
    var status = "${status}";
    var jsUseCtxPath = "${ctxPath}";
    var currentTime = "${currentTime}";
    var currentUserId = "${currentUserId}";
    var currentUserName = "${currentUserName}";
    var deptId = "${deptId}";
    var deptName = "${deptName}";
    var fileListGrid = mini.get("fileListGrid");
    var examineListGrid = mini.get("examineListGrid");
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";
    var cpkfId = "${cpkfId}";
    var selectModelWindow = mini.get("selectModelWindow");
    var selectModelListGrid = mini.get("selectModelListGrid");

    function operationRendererSq(e) {
        var record = e.record;
        var cellHtml = '';
        cellHtml = returnSalePreviewSpan(record.fileName, record.fileId, record.belongId, coverContent);
        cellHtml += '&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downFile(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">下载</span>';
        //增加删除按钮
        if (action != "detail" && record.CREATE_BY_ == currentUserId) {
            var deleteUrl = "/rdm/core/ProductRequire/deleteProductRequireFile.do"
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(\'' + record.fileName + '\',\'' + record.fileId + '\',\'' + record.belongId + '\',\'' + deleteUrl + '\')">删除</span>';
        }
        return cellHtml;
    }

    function operationRendererSp(e) {
        var record = e.record;
        var cellHtml = '';
        cellHtml = returnSalePreviewSpan(record.fileName, record.fileId, record.belongId, coverContent);
        cellHtml += '&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downFile(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">下载</span>';
        //增加删除按钮
        if (stageName == 'forth' && record.CREATE_BY_ == currentUserId) {
            var deleteUrl = "/rdm/core/ProductRequire/deleteProductRequireFile.do"
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFileSp(\'' + record.fileName + '\',\'' + record.fileId + '\',\'' + record.belongId + '\',\'' + deleteUrl + '\')">删除</span>';
        }
        return cellHtml;
    }

    function deleteFileSp(fileName, fileId, formId, urlValue) {
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
                            if (examineListGrid) {
                                examineListGrid.load();
                            }
                        }
                    });
                }
            }
        );
    }

    var stageName = '';
    $(function () {
        mini.get("projectId").hide();
        mini.get("projectName").show();
        var queryProjectUserId = currentUserId;
        $.ajax({
            url: jsUseCtxPath + '/xcmgProjectManager/core/xcmgProject/queryParticipateProject.do?queryProjectUserId=' + queryProjectUserId,
            type: 'get',
            contentType: 'application/json',
            success: function (data) {
                if (data) {
                    mini.get("#projectId").load(data);
                }
            }
        });

        if (cpkfId) {
            var url = jsUseCtxPath + "/rdm/core/ProductRequire/getProductRequireDetail.do";
            $.ajaxSettings.async = false;
            $.post(
                url,
                {cpkfId: cpkfId},
                function (json) {
                    productRequireForm.setData(json);
                });
            $.ajaxSettings.async = true;
        } else {
            mini.get("apply").setValue(currentUserId);
            mini.get("apply").setText(currentUserName);
            mini.get("dept").setValue(deptId);
            mini.get("dept").setText(deptName);

        }
        mini.get("addExamine").setEnabled(false);
        //变更入口
        if (action == 'task') {
            taskActionProcess();
        } else if (action == "detail") {
            productRequireForm.setEnabled(false);
            mini.get("addFile").setEnabled(false);
            mini.get("addExamine").setEnabled(false);
            $("#detailToolBar").show();
            if (status != 'DRAFTED') {
                $("#processInfo").show();
            }
        } else if (action == "edit") {
            mini.get("projectName").setEnabled(false);
            mini.get("addExamine").setEnabled(false);
            mini.get("ifDev").setEnabled(false);
            mini.get("resolution").setEnabled(false);
        }
        fileTypeChange();
    });

    function projectChange() {
        var projectName = mini.get("projectId").getText();
        mini.get("projectName").setValue(projectName);
    }

    //流程实例图
    function processInfo() {
        let instId = $("#instId").val();
        let url = jsUseCtxPath + "/bpm/core/bpmInst/info.do?instId=" + instId;
        _OpenWindow({
            url: url,
            max: true,
            title: "流程图实例",
            width: 800,
            height: 600
        });
    }


    //获取环境变量
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
        productRequireForm.setEnabled(false);
        mini.get("addFile").setEnabled(false);
        mini.get("addExamine").setEnabled(false);
        if (stageName == 'first') {
            productRequireForm.setEnabled(true);
            mini.get("projectName").setEnabled(false);
            mini.get("addExamine").setEnabled(false);
            mini.get("ifDev").setEnabled(false);
            mini.get("resolution").setEnabled(false);
        } else if (stageName == 'second') {
            mini.get("cpzg").setEnabled(true);
        } else if (stageName == 'third') {
            mini.get("projectId").setEnabled(true);
            mini.get("projectId").show();
            mini.get("projectName").hide();
            mini.get("ifDev").setEnabled(true);
            mini.get("resolution").setEnabled(true);
        } else if (stageName == 'forth') {
            mini.get("addExamine").setEnabled(true);
        }


    }


    //保存草稿
    function saveApplyInfo(e) {

        window.parent.saveDraft(e);
    }

    //流程引擎调用此方法进行表单数据的获取
    function getData() {
        var formData = _GetFormJsonMini("productRequireForm");
        return formData;
    }

    //保存草稿或提交时数据是否有效
    function draftOrStartValid() {
        var apply = $.trim(mini.get("apply").getValue());
        if (!apply) {
            return {"result": false, "message": "请选择申请人"};
        }
        var dept = $.trim(mini.get("dept").getValue());
        if (!dept) {
            return {"result": false, "message": "请选择申请部门"};
        }
        let fileType = $.trim(mini.get("fileType").getValue());
        if (!fileType) {
            return {"result": false, "message": "请选择文件类型"};
        }
        let version = $.trim(mini.get("version").getValue());
        if (!version) {
            return {"result": false, "message": "请填写文件版本"};
        }
        let ifDesignModel = $.trim(mini.get("ifDesignModel").getValue());
        if (!ifDesignModel) {
            return {"result": false, "message": "请选择是否有设计型号"};
        }
        if (ifDesignModel == '是') {
            let designModel = $.trim(mini.get("designModel").getValue());
            if (!designModel) {
                return {"result": false, "message": "请选择设计型号"};
            }
            let cpzg = $.trim(mini.get("cpzg").getValue());
            if (!cpzg) {
                return {"result": false, "message": "请选择产品主管"};
            }
        }
        let modelDept = $.trim(mini.get("modelDept").getValue());
        if (!modelDept) {
            return {"result": false, "message": "请选择产品所"};
        }
        return {"result": true};
    }

    function secondValid() {
        let cpzg = $.trim(mini.get("cpzg").getValue());
        if (!cpzg) {
            return {"result": false, "message": "请选择产品主管"};
        }
        return {"result": true};
    }

    function thirdValid() {
        let fileType = $.trim(mini.get("fileType").getValue());
        if(fileType=='产品开发建议书') {
            let ifDev = $.trim(mini.get("ifDev").getValue());
            if (!ifDev) {
                return {"result": false, "message": "请选择是否开发"};
            }
            let resolution = $.trim(mini.get("resolution").getValue());
            if (!resolution) {
                return {"result": false, "message": "请填写处理意见"};
            }
        }
        return {"result": true};
    }

    //启动流程
    function startApplyProcess(e) {
        var formValid = draftOrStartValid();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        window.parent.startProcess(e);
    }

    //审批或者下一步
    function applyApprove() {
        if (stageName == 'first') {
            var formValid = draftOrStartValid();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }
        if (stageName == 'second') {
            var formValid = secondValid();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }
        if (stageName == 'third') {
            var formValid = thirdValid();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }
        window.parent.approve();
    }

    function addSaleFile() {
        var belongId = mini.get("cpkfId").getValue();
        if (!belongId) {
            mini.alert('请先点击‘保存草稿’进行表单的保存！');
            return;
        }
        mini.open({
            title: "文件上传",
            url: jsUseCtxPath + "/rdm/core/ProductRequire/openUploadWindow.do?belongId=" + belongId + "&fileModel=sq",
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

    function addExamineFile() {
        var belongId = mini.get("cpkfId").getValue();
        if (!belongId) {
            mini.alert('请先点击‘保存草稿’进行表单的保存！');
            return;
        }
        mini.open({
            title: "文件上传",
            url: jsUseCtxPath + "/rdm/core/ProductRequire/openUploadWindow.do?belongId=" + belongId + "&fileModel=sp",
            width: 750,
            height: 450,
            showModal: false,
            allowResize: true,
            onload: function () {
            },
            ondestroy: function (action) {
                if (examineListGrid) {
                    examineListGrid.load();
                }
            }
        });
    }

    function returnSalePreviewSpan(fileName, fileId, formId, coverContent, fileSize) {
        var fileType = getFileType(fileName);
        var s = '';
        if (fileType == 'other') {
            s = '<span  title="预览" style="color: silver" >预览</span>';
        } else if (fileType == 'pdf') {
            var url = '/rdm/core/ProductRequire/productRequirePdfPreview.do';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        } else if (fileType == 'office') {
            var url = '/rdm/core/ProductRequire/productRequireOfficePreview.do';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        } else if (fileType == 'pic') {
            var url = '/rdm/core/ProductRequire/productRequireImagePreview.do';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        }
        return s;
    }


    //下载文档
    function downFile(record) {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + "/rdm/core/ProductRequire/productRequirePdfPreview.do?action=download");
        var inputFileName = $("<input>");
        inputFileName.attr("type", "hidden");
        inputFileName.attr("name", "fileName");
        inputFileName.attr("value", record.fileName);
        var mainId = $("<input>");
        mainId.attr("type", "hidden");
        mainId.attr("name", "formId");
        mainId.attr("value", record.belongId);
        var fileId = $("<input>");
        fileId.attr("type", "hidden");
        fileId.attr("name", "fileId");
        fileId.attr("value", record.fileId);
        $("body").append(form);
        form.append(inputFileName);
        form.append(mainId);
        form.append(fileId);
        form.submit();
        form.remove();
    }

    function selectModelClick() {
        selectModelWindow.show();
        searchModel();
    }

    function searchModel() {
        var queryParam = [];
        //其他筛选条件
        var designModel = $.trim(mini.get("selectdesignModel").getValue());
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
        var saleModel = $.trim(mini.get("selectsaleModel").getValue());
        if (saleModel) {
            queryParam.push({name: "saleModel", value: saleModel});
        }
        var data = {};
        data.filter = mini.encode(queryParam);
        //查询
        selectModelListGrid.load(data);
    }

    function selectModelOK() {
        var rowSelected = selectModelListGrid.getSelected();
        if (rowSelected) {
            mini.get("designModel").setValue(rowSelected.id);
            mini.get("designModel").setText(rowSelected.designModel);
            mini.get("modelDept").setValue(rowSelected.departId);
            mini.get("modelDept").setText(rowSelected.departName);
            mini.get("cpzg").setValue(rowSelected.productManagerId);
            mini.get("cpzg").setText(rowSelected.productManagerName);
            selectModelHide();
        }
    }

    function selectModelHide() {
        selectModelWindow.hide();
        mini.get("selectsaleModel").setValue('');
        mini.get("productManagerName").setValue('');
        mini.get("departName").setValue('');
        mini.get("selectdesignModel").setValue('');
        selectModelListGrid.clearSelect();
    }

    function onRelModelCloseClick() {
        mini.get("designModel").setValue('');
        mini.get("designModel").setText('');
        mini.get("modelDept").setValue('');
        mini.get("modelDept").setText('');
        mini.get("cpzg").setValue('');
        mini.get("cpzg").setText('');
        selectModelListGrid.clearSelect();
    }

    function fileTypeChange() {
        var fileType = mini.get("fileType").getValue();
        if (fileType == '产品开发建议书') {
            $("#devTr1").show();
            $("#devTr2").show();
        } else {
            $("#devTr1").hide();
            $("#devTr2").hide();
        }
    }

    function ifModelChange() {
        var ifDesignModel = mini.get("ifDesignModel").getValue();
        if (ifDesignModel == '否') {
            mini.get("designModel").setValue('');
            mini.get("designModel").setText('');
            mini.get("modelDept").setValue('');
            mini.get("modelDept").setText('');
            mini.get("cpzg").setValue('');
            mini.get("cpzg").setText('');
        }
    }
</script>
</body>
</html>
