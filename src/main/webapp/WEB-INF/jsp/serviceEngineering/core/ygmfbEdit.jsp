<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>油缸密封包图纸需求传递表单</title>
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
    <div class="form-container" style="margin: 0 auto; width: 80%;">
        <form id="formBusiness" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <input id="applyUserId" name="applyUserId" class="mini-hidden"/>
            <input id="applyDepId" name="applyDepId" class="mini-hidden"/>
            <input id="bjgsfzrId" name="bjgsfzrId" class="mini-hidden"/>
            <input id="bjgsfzr" name="bjgsfzr" class="mini-hidden"/>
            <input id="fwgcywyId" name="fwgcywyId" class="mini-hidden"/>
            <input id="fwgcywy" name="fwgcywy" class="mini-hidden"/>
            <input id="fwgcfzrId" name="fwgcfzrId" class="mini-hidden"/>
            <input id="fwgcfzr" name="fwgcfzr" class="mini-hidden"/>
            <input id="fgldId" name="fgldId" class="mini-hidden"/>
            <input id="fgld" name="fgld" class="mini-hidden"/>
            <input id="INST_ID_" name="INST_ID_" class="mini-hidden"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0">
                <caption style="font-weight: bold">
                    油缸密封包图纸需求传递表单
                </caption>
                <tr>
                    <td style="text-align: center;width: 15%">厂家：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="changjia" name="changjia" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%">油缸类型:<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="ygType" name="ygType" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">油缸物料号：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="ygMatNo" name="ygMatNo" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%">油缸型号：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="ygXinghao" name="ygXinghao" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">油缸物料描述：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="ygMatDesc" name="ygMatDesc" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%">密封包物料号：</td>
                    <td style="min-width:170px">
                        <input id="mfbMatNo" name="mfbMatNo" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">密封包物料描述：</td>
                    <td style="min-width:170px">
                        <input id="mfbMatDesc" name="mfbMatDesc" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 20%">用途：<span style="color:red">*</span>（不超过800字）</td>
                    <td style="min-width:170px">
                        <textarea id="useDesc" name="useDesc" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:98%;height:60px;line-height:25px;"
                                  label="说明" datatype="varchar" allowinput="true"
                                  emptytext="请输入用途说明..." mwidth="80" wunit="%" mheight="20000" hunit="px"></textarea>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">需求时间：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="needTime" name="needTime" class="mini-datepicker" allowInput="false" style="width:98%;height:34px">
                    </td>
                    <td style="text-align: center;width: 20%">备件公司接收业务员：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="receiverIds" name="receiverIds" textname="receivers" class="mini-user rxc"
                               plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="备件公司接收业务员" showclose="true"
                               length="200" maxlength="200" mainfield="no" single="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">申请人(自动生成)：</td>
                    <td style="min-width:170px">
                        <input name="applyUser" class="mini-textbox" enabled="false" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 20%">申请部门（自动生成）：</td>
                    <td style="min-width:170px">
                        <input name="applyDep" class="mini-textbox" enabled="false" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%;height: 150px">备注：（不超过1000字）</td>
                    <td colspan="3">
						<textarea id="remark" name="remark" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:99.1%;height:99%;line-height:25px;"
                                  label="说明" datatype="varchar" allowinput="true"
                                  emptytext="请输入备注说明..." mwidth="80" wunit="%" mheight="20000" hunit="px"></textarea>
                    </td>
                </tr>

                <tr>
                    <td style="text-align: center;height: 300px">附件列表：</td>
                    <td colspan="3">
                        <div style="margin-top: 10px;margin-bottom: 2px">
                            <a id="addFile" class="mini-button" onclick="addBusinessFile()">添加文件</a>
                            <span style="color: red">注：添加文件前，请先进行草稿的保存</span>
                        </div>
                        <div id="fileListGrid" class="mini-datagrid" style="width: 100%; height: 85%" allowResize="false"
                             idField="id"
                             url="${ctxPath}/serviceEngineering/core/ygmfb/getFileList.do?businessId=${businessId}"
                             autoload="true"
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
    var status = "${status}";
    var jsUseCtxPath = "${ctxPath}";
    var fileListGrid = mini.get("fileListGrid");
    var formBusiness = new mini.Form("#formBusiness");
    var businessId = "${businessId}";
    var nodeVarsStr = '${nodeVars}';
    var currentTime = "${currentTime}";
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var currentUserName = "${currentUserName}";
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";
    var stageName = "";

    //..
    $(function () {
        var url = jsUseCtxPath + "/serviceEngineering/core/ygmfb/getDetail.do";
        $.post(
            url,
            {businessId: businessId},
            function (json) {
                formBusiness.setData(json);
                if (action == 'detail') {
                    formBusiness.setEnabled(false);
                    mini.get("addFile").setEnabled(false);
                    $("#detailToolBar").show();
                    //非草稿放开流程信息查看按钮
                    if (status != 'DRAFTED') {
                        $("#processInfo").show();
                    }
                } else if (action == 'task') {
                    taskActionProcess();
                }
            });
    });

    //..流程引擎调用此方法进行表单数据的获取
    function getData() {
        var formData = _GetFormJsonMini("formBusiness");
        if (formData.SUB_fileListGrid) {
            delete formData.SUB_fileListGrid;
        }
        formData.bos = [];
        return formData;
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
        var formValid = validBusiness();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        var formData = _GetFormJsonMini("formBusiness");
        if (formData.SUB_fileListGrid) {
            delete formData.SUB_fileListGrid;
        }
        $.ajax({
            url: jsUseCtxPath + '/serviceEngineering/core/ygmfb/saveBusiness.do',
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

    //..流程中的审批或者下一步
    function businessApprove() {
        //编制阶段的下一步需要校验表单必填字段
        if (stageName == 'start') {
            var formValid = validBusiness();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }
        //检查通过
        window.parent.approve();
    }

    //..添加文件
    function addBusinessFile() {
        var businessId = mini.get("id").getValue();
        if (!businessId) {
            mini.alert('请先点击‘保存草稿’进行表单的保存！');
            return;
        }
        mini.open({
            title: "文件上传",
            url: jsUseCtxPath + "/serviceEngineering/core/ygmfb/fileUploadWindow.do?businessId=" + businessId,
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

    //..流程信息浏览
    function processInfo() {
        var instId = $("#INST_ID_").val();
        var url = jsUseCtxPath + "/bpm/core/bpmInst/info.do?instId=" + instId;
        _OpenWindow({
            url: url,
            max: true,
            title: "流程图实例",
            width: 800,
            height: 600
        });
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
            if (nodeVars[i].KEY_ == 'stageName') {
                stageName = nodeVars[i].DEF_VAL_;
            }
        }
        if (stageName != 'start') {
            formBusiness.setEnabled(false);
            mini.get("addFile").setEnabled(false);
            if (stageName == 'applyProcess') {
                mini.get("addFile").setEnabled(true);
            }
        }
    }

    //..检验表单是否必填
    function validBusiness() {
        var changjia = $.trim(mini.get("changjia").getValue());
        if (!changjia) {
            return {"result": false, "message": "请填写厂家信息"};
        }
        var ygType = $.trim(mini.get("ygType").getValue());
        if (!ygType) {
            return {"result": false, "message": "请填写油缸类型"};
        }
        var ygMatNo = $.trim(mini.get("ygMatNo").getValue());
        if (!ygMatNo) {
            return {"result": false, "message": "请填写油缸物料号"};
        }
        var ygXinghao = $.trim(mini.get("ygXinghao").getValue());
        if (!ygXinghao) {
            return {"result": false, "message": "请填写油缸型号"};
        }
        var ygMatDesc = $.trim(mini.get("ygMatDesc").getValue());
        if (!ygMatDesc) {
            return {"result": false, "message": "请填写油缸物料描述"};
        }
        var useDesc = $.trim(mini.get("useDesc").getValue());
        if (!useDesc) {
            return {"result": false, "message": "请填写用途"};
        }
        var needTime = $.trim(mini.get("needTime").getValue());
        if (!needTime) {
            return {"result": false, "message": "请确定时间"};
        }
        var receiverIds = $.trim(mini.get("receiverIds").getValue());
        if (!receiverIds) {
            return {"result": false, "message": "请填写接收业务员"};
        }
        if (fileListGrid.getData().length == 0) {
            return {"result": false, "message": "请上传相关附件!"};
        }
        return {"result": true};
    }

    //..文件列表操作渲染
    function operationRenderer(e) {
        var record = e.record;
        var cellHtml = '';
        cellHtml = returnPreviewSpan(record.fileName, record.id, record.mainId, coverContent);
        //和pdf预览用的同一个url
        debugger;
        if (status == 'SUCCESS_END') {
            var downLoadUrl = '/serviceEngineering/core/ygmfb/PdfPreviewAndAllDownload.do';
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="downLoadFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.mainId + '\',\'' + downLoadUrl + '\')">下载</span>';
        }
        //增加删除按钮
        if (action == 'edit' || (action == 'task' && stageName == 'start')) {
            var deleteUrl = "/serviceEngineering/core/ygmfb/delFile.do"
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
            var url = '/serviceEngineering/core/ygmfb/PdfPreviewAndAllDownload.do';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        } else if (fileType == 'office') {
            var url = '/serviceEngineering/core/ygmfb/OfficePreview.do';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        } else if (fileType == 'pic') {
            var url = '/serviceEngineering/core/ygmfb/ImagePreview.do';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        }
        return s;
    }
</script>
</body>
</html>
