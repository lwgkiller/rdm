<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html >
<html>
<head>
    <title>价格审批表单</title>
    <%@include file="/commons/edit.jsp"%>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css" />
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
    <div class="form-container" style="margin:0 auto; width: 70%;height:98%;padding: 0">
        <form id="jgspForm" method="post" style="height: 95%;width: 100%">
            <input id="instId" name="instId" class="mini-hidden"/>
            <input id="jgspId" name="jgspId" class="mini-hidden"/>
            <table class="table-detail grey"  cellspacing="1" cellpadding="0">
                <tr>
                    <td style="text-align: center;width: 25%">应用机型：</td>
                    <td style="min-width:170px;width: 25%">
                        <input id="yyjx"  name="yyjx"  class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 20%">供应商：</td>
                    <td style="min-width:170px">
                        <input id="gys"  name="gys"  class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">物料描述：</td>
                    <td style="min-width:170px">
                        <input id="wlms"  name="wlms"  class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 20%">是否紧急：</td>
                    <td>
                        <input id="sfjj" name="sfjj"  class="mini-checkbox" style="width:98%;" />
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">所别</td>
                    <td style="width: 15%;min-width:170px ">
                        <input id="dept" name="deptId" textname="deptName"
                               property="editor" class="mini-dep rxc" plugins="mini-dep"
                               style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"/>
                    </td>
                    <td style="text-align: center;width: 20%">部门负责人：</td>
                    <td style="min-width:170px">
                        <input id="res" name="resId" textname="resName"
                               property="editor" class="mini-user rxc" plugins="mini-user"
                               style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"
                        />
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">科室：</td>
                    <td style="width: 30%;">
                        <input id="ks" name="ks" class="mini-combobox" style="width:98%;"
                               textField="value" valueField="key" emptyText="请选择..."
                               required="false" allowInput="false" showNullItem="true"
                               nullItemText="请选择..."
                               data="[
                            {'key' : '液压','value' : '液压'},{'key' : '覆盖件','value' : '覆盖件'}
                           ,{'key' : '动力','value' : '动力'},{'key' : '底盘','value' : '底盘'}
                           ,{'key' : '电气','value' : '电气'},{'key' : '工装','value' : '工装'}]"
                        />
                    </td>
                    <td style="text-align: center;width: 20%">价格小组长：</td>
                    <td style="min-width:170px">
                        <input id="xzz" name="xzzId" textname="xzzName"
                               property="editor" class="mini-user rxc" plugins="mini-user"
                               style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"
                        />
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">申请人：</td>
                    <td style="min-width:170px">
                        <input id="apply" name="applyId" textname="applyName"
                               property="editor" class="mini-user rxc" plugins="mini-user"
                               style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"
                        />
                    </td>
                    <td style="text-align: center;width: 20%">小组长选择流程节点：</td>
                    <td style="width: 30%;">
                        <input id="nextNode" name="nextNode" class="mini-combobox" style="width:98%;"
                               textField="value" valueField="key" emptyText="请选择..."
                               required="false" allowInput="false" showNullItem="true"
                               nullItemText="请选择..."
                               data="[{'key' : '通过','value' : '通过'},{'key' : '上会','value' : '上会'}]"
                        />
                    </td>
                    </td>
                </tr>
                <tr>
                    <td style="width: 15%;height: 300px;text-align: center ">附件列表：</td>
                    <td colspan="3">
                        <div style="margin-top: 2px;margin-bottom: 2px">
                            <a id="addFile" class="mini-button" onclick="fjupload()">添加附件</a>
                            <a id="download" class="mini-button" onclick="downImport()">下载价格审批模板</a>
                        </div>
                        <div id="jgspFileListGrid" class="mini-datagrid" style="width: 100%; height: 85%"
                             allowResize="false"
                             idField="id" autoload="true"
                             url="${ctxPath}/rdm/core/jgsp/getJgspFileList.do?belongId=${jgspId}"
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


<script type="text/javascript">
    mini.parse();
    var nodeVarsStr = '${nodeVars}';
    var jsUseCtxPath="${ctxPath}";
    var jgspForm = new mini.Form("#jgspForm");
    var jgspId="${jgspId}";
    var action="${action}";
    var status="${status}";
    var currentUserId="${currentUserId}";
    var currentUserName="${currentUserName}";
    var resId="${resId}";
    var resName="${resName}";
    var deptId="${deptId}";
    var deptName="${deptName}";
    var currentTime="${currentTime}";
    var isJgsp="${isJgsp}";
    var jgspFileListGrid=mini.get("jgspFileListGrid");
    var coverContent=currentUserName+"<br/>"+currentTime+"<br/>徐州徐工挖掘机械有限公司";

    var first="";
    var second="";
    var third="";
    $(function () {
        if(jgspId) {
            var url = jsUseCtxPath + "/rdm/core/jgsp/getJgspDetail.do";
            $.post(
                url,
                {jgspId: jgspId},
                function (json) {
                    jgspForm.setData(json);
                });
        }else {
            mini.get("apply").setValue(currentUserId);
            mini.get("apply").setText(currentUserName);
            mini.get("res").setValue(resId);
            mini.get("res").setText(resName);
            mini.get("dept").setValue(deptId);
            mini.get("dept").setText(deptName);

        }
        mini.get("nextNode").setEnabled(false);
        //变更入口
        if(action=='task') {
            taskActionProcess();
        }else if(action == 'detail'){
            jgspForm.setEnabled(false);
            mini.get("download").setEnabled(false);
            mini.get("addFile").setEnabled(false);
            $("#detailToolBar").show();
            if (status != 'DRAFTED') {
                $("#processInfo").show();
            }
        }
    });

    function downImport() {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + "/rdm/core/jgsp/importDownload.do");
        $("body").append(form);
        form.submit();
        form.remove();
    }

    function operationRenderer(e) {
        var xzz = mini.get("xzz").getValue();
        var res = mini.get("res").getValue();
        var record = e.record;
        var cellHtml = '';
        if (record.CREATE_BY_==currentUserId||res==currentUserId||xzz==currentUserId||isJgsp) {
            cellHtml = returnJgspPreviewSpan(record.fileName, record.fileId, record.belongId, coverContent);
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="downLoadJgspFile(\'' + record.fileName + '\',\'' + record.fileId + '\',\'' + record.belongId + '\')">下载</span>';
        }
        if (action!='detail'&&record.CREATE_BY_==currentUserId) {
            var deleteUrl = "/rdm/core/jgsp/deleteJgspFile.do"
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteJgspFile(\'' + record.fileName + '\',\'' + record.fileId + '\',\'' + record.belongId + '\',\'' + deleteUrl + '\')">删除</span>';
        }
        if(cellHtml==''){
            cellHtml = '无操作权限'
        }
        return cellHtml;
    }

    function returnJgspPreviewSpan(fileName, fileId, formId, coverContent) {
        var fileType = getFileType(fileName);
        var s = '';
        if (fileType == 'other') {
            s = '<span  title="预览" style="color: silver" >预览</span>';
        } else if (fileType == 'pdf') {
            var url = '/rdm/core/jgsp/jgspPdfPreview';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        } else if (fileType == 'office') {
            var url = '/rdm/core/jgsp/jgspOfficePreview';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        } else if (fileType == 'pic') {
            var url = '/rdm/core/jgsp/jgspImagePreview';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        }
        return s;
    }
    
    function downLoadJgspFile(fileName, fileId, formId) {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + '/rdm/core/jgsp/jgspPdfPreview.do?action=download');
        var inputFileName = $("<input>");
        inputFileName.attr("type", "hidden");
        inputFileName.attr("name", "fileName");
        inputFileName.attr("value", fileName);
        var inputstandardId = $("<input>");
        inputstandardId.attr("type", "hidden");
        inputstandardId.attr("name", "formId");
        inputstandardId.attr("value", formId);
        var inputFileId = $("<input>");
        inputFileId.attr("type", "hidden");
        inputFileId.attr("name", "fileId");
        inputFileId.attr("value", fileId);
        $("body").append(form);
        form.append(inputFileName);
        form.append(inputstandardId);
        form.append(inputFileId);
        form.submit();
        form.remove();
    }


    function deleteJgspFile(fileName,fileId,formId,urlValue) {
        mini.confirm("确定删除？", "确定？",
            function (action) {
                if (action == "ok") {
                    var url = jsUseCtxPath + urlValue;
                    var data = {
                        fileName: fileName,
                        id: fileId,
                        formId: formId
                    };
                    $.ajax({
                        url:url,
                        method:'post',
                        contentType: 'application/json',
                        data:mini.encode(data),
                        success:function (json) {
                            if(jgspFileListGrid) {
                                jgspFileListGrid.load();
                            }
                        }
                    });
                }
            }
        );
    }
    
    function getData() {
        var formData = _GetFormJsonMini("jgspForm");
        //此处用于向后台产生流程实例时替换标题中的参数时使用
        // formData.bos=[];
        // formData.vars=[{key:'companyName',val:formData.companyName}];
        return formData;
    }
    function validFirst() {
        var yyjx=$.trim(mini.get("yyjx").getValue());
        if(!yyjx) {
            return {"result": false, "message": "请填写应用机型"};
        }
        var gys=$.trim(mini.get("gys").getValue())
        if(!gys) {
            return {"result": false, "message": "请填写供应商"};
        }
        var wlms=$.trim(mini.get("wlms").getValue())
        if(!wlms) {
            return {"result": false, "message": "请填写物料描述"};
        }
        var dept = $.trim(mini.get("dept").getValue());
        if (!dept) {
            return {"result": false, "message": "请选择责任部门"};
        }
        var ks=$.trim(mini.get("ks").getValue())
        if(!ks) {
            return {"result": false, "message": "请填写科室"};
        }
        var xzz = $.trim(mini.get("xzz").getValue());
        if (!xzz) {
            return {"result": false, "message": "请选择价格小组长"};
        }
        var apply = $.trim(mini.get("apply").getValue());
        if (!apply) {
            return {"result": false, "message": "请选择申请人"};
        }
        var res = $.trim(mini.get("res").getValue());
        if (!res) {
            return {"result": false, "message": "请选择部门负责人"};
        }
        return {"result": true};
    }

    function validSecond() {
        var nextNode=$.trim(mini.get("nextNode").getValue());
        if(!nextNode) {
            return {"result": false, "message": "请选择下一步节点"};
        }
        return {"result": true};
    }

    function saveJgsp(e) {
        // var formValid = validFirst();
        // if (!formValid.result) {
        //     mini.alert(formValid.message);
        //     return;
        // }
        window.parent.saveDraft(e);
    }

    function startJgspProcess(e) {
        var formValid = validFirst();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        window.parent.startProcess(e);
    }
    function jgspApprove() {
        //编制阶段的下一步需要校验表单必填字段
        if (first == 'yes') {
            var formValid = validFirst();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }
        if (second == 'yes') {
            var formValid = validSecond();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }
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
        var nodeVars = $.parseJSON(nodeVarsStr);
        for (var i = 0; i < nodeVars.length; i++) {
            if (nodeVars[i].KEY_ == 'first') {
                first = nodeVars[i].DEF_VAL_;
            }
            if (nodeVars[i].KEY_ == 'second') {
                second = nodeVars[i].DEF_VAL_;
            }
        }
        jgspForm.setEnabled(false);
        mini.get("download").setEnabled(false);
        mini.get("addFile").setEnabled(false);
        if(first == 'yes') {
            jgspForm.setEnabled(true);
            mini.get("download").setEnabled(true);
            mini.get("addFile").setEnabled(true);
            mini.get("nextNode").setEnabled(false);
        }
        if(second == 'yes') {
            mini.get("nextNode").setEnabled(true);
        }
    }

    function fjupload() {
        var jgspId = mini.get("jgspId").getValue();
        if (!jgspId) {
            mini.alert('请先点击‘保存草稿’进行表单的保存！');
            return;
        }
        mini.open({
            title: "添加附件",
            url: jsUseCtxPath + "/rdm/core/jgsp/openUploadWindow.do?jgspId=" + jgspId,
            width: 850,
            height: 550,
            showModal: true,
            allowResize: true,
            ondestroy: function () {
                if (jgspFileListGrid) {
                    jgspFileListGrid.load();
                }
            }
        });
    }

    //所有非特殊文本都变成这个格式
    function render(e) {
        if (e.value != null && e.value != "") {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            return html;
        }
    }
</script>
</body>
</html>
