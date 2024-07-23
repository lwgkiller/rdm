<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html >
<html>
<head>
    <title>集团内部外出交流审批单</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
    <link href="${ctxPath}/styles/css/multiupload.css" rel="stylesheet" type="text/css"/>
</head>
<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="processInfo" class="mini-button" style="display: none" onclick="processInfo()">流程信息</a>
        <a id="closeWindow1" class="mini-button" onclick="CloseWindow()">关闭</a>
    </div>
</div>

<div class="mini-fit">
    <div class="form-container" style="margin:0 auto; width: 100%;height:98%;padding: 0">
        <form id="nbggForm" method="post" style="height: 95%;width: 98%">
            <input class="mini-hidden" id="id" name="id"/>
            <input id="instId" name="instId" class="mini-hidden"/>
            <input id="CREATE_BY_" name="CREATE_BY_" class="mini-hidden"/>
            <p style="text-align: center;font-size: 20px;font-weight: bold;margin-top: 0px;margin-bottom: 10px">集团内部外出交流审批单</p>
            <table class="table-detail grey" cellspacing="1" cellpadding="0">
                <tr>
                    <td style="text-align: center;width: 20%">申请人：</td>
                    <td style="min-width:170px">
                        <input id="creatorName" name="creatorName" class="mini-textbox" style="width:98%;"
                               enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 20%">申请人部门：</td>
                    <td style="min-width:170px">
                        <input id="creatorDeptName" name="creatorDeptName" class="mini-textbox" style="width:98%;"
                               enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">同行人员：</td>
                    <td>
                        <input id="followId" name="followId" textname="followName"
                               property="editor" class="mini-user rxc" plugins="mini-user"
                               style="width:98%;height:34px;" allowinput="false" mainfield="no" single="false"
                        />
                    </td>

                    <td style="text-align: center;width: 20%">交流地点：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="aimLocation" name="aimLocation" class="mini-textbox" style="width:98%;"
                               enabled="true"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center">预计开始时间：<span style="color:red">*</span></td>
                    <td>
                        <input id="outStartTime" name="outStartTime" class="mini-datepicker" format="yyyy-MM-dd H:mm"
                               allowInput="false"
                               showTime="true" showOkButton="false" showClearButton="true" style="width:98%;"/>
                    </td>
                    <td style="text-align: center">预计结束时间：<span style="color:red">*</span></td>
                    <td>
                        <input id="outEndTime" name="outEndTime" class="mini-datepicker" format="yyyy-MM-dd H:mm"
                               allowInput="false"
                               showTime="true" showOkButton="false" showClearButton="true" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center">主题描述：<span style="color:red">*</span></td>
                    <td colspan="3">
                        <input id="outSubject" name="outSubject" class="mini-textbox" style="width:98%;"
                               enabled="true"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center">外出描述：<span style="color:red">*</span></td>
                    <td colspan="3">
						<textarea id="outDesc" name="outDesc" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:98%;height:125px;line-height:25px;"
                                  label="外出描述" datatype="varchar" length="2000" vtype="length:2000" minlen="0"
                                  allowinput="true"
                                  emptytext="请输入外出描述..." mwidth="80" wunit="%" mheight="125" hunit="px"></textarea>
                    </td>
                </tr>

                <tr>
                    <td style="text-align: center">交流总结：<span style="color:red">*</span></td>
                    <td colspan="3">
						<textarea id="outSummary" name="outSummary" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:98%;height:125px;line-height:25px;"
                                  label="交流总结" datatype="varchar" length="2000" vtype="length:2000" minlen="0"
                                  allowinput="false"
                                  emptytext="请输入交流总结..." mwidth="80" wunit="%" mheight="125" hunit="px"></textarea>
                    </td>
                </tr>
                <tr>
                    <td style= "text-align: center;width: 14%;height:10px">附件列表：</td>
                    <td colspan="3" height="60px">
                        <div style="margin-top: 10px;margin-bottom: 2px">
                            <a id="addFile" class="mini-button" onclick="addNbggFile('${applyId}')">添加附件</a>
                            <span style="color: red">注：添加附件前，请先进行草稿的保存</span>
                        </div>
                        <div id="fileListGrid" class="mini-datagrid"
                             allowResize="false"
                             idField="id"
                             url="${ctxPath}/zhgl/core/nbgg/demandList.do?applyId=${applyId}"
                             autoload="true"
                             multiSelect="true" showPager="false" showColumnsMenu="false"
                             allowAlternating="true"
                             style="height:150px;"
                        >
                            <div property="columns">
                                <div type="indexcolumn" align="center" width="20">序号</div>
                                <div field="fileName" width="140" headerAlign="center" align="center">附件名称</div>
                                <div field="fileDesc" width="80" headerAlign="center" align="center">附件描述
                                </div>
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
    var jsUseCtxPath = "${ctxPath}";
    var nodeVarsStr = '${nodeVars}';
    var nbggForm = new mini.Form("#nbggForm");
    var fileListGrid = mini.get("fileListGrid");


    var action = "${action}";
    var status = "${status}";
    var applyId = "${applyId}";
    var instId = "${instId}";
    var currentUserId = "${currentUserId}";
    var currentUserName = "${currentUserName}";
    var currentTime = "${currentTime}";
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";


    function render(e) {
        if (e.value != null && e.value != "") {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            return html;
        }
    }

    var stageName = "";
    $(function () {
        var url = jsUseCtxPath + "/zhgl/core/nbgg/getJson.do";
        $.post(
            url,
            {id: applyId},
            function (json) {
                nbggForm.setData(json);
            });
        if (action == 'detail') {
            nbggForm.setEnabled(false);
            mini.get("addFile").setEnabled(false);
            $("#detailToolBar").show();
            if (status != 'DRAFTED') {
                $("#processInfo").show();
            }
        } else if (action == 'task') {
            taskActionProcess();
        }
    });

    function getData() {
        var formData = _GetFormJsonMini("nbggForm");
        if (formData.SUB_fileListGrid) {
            delete formData.SUB_fileListGrid;
        }
        if (formData.SUB_demandGrid) {
            delete formData.SUB_demandGrid;
        }
        return formData;
    }

    //保存草稿
    function saveDraft(e) {
        var outStartTime = $.trim(mini.get("outStartTime").getValue());
        if (!outStartTime) {
            mini.alert("请选择预计开始时间");
            return;
        }
        var outEndTime = $.trim(mini.get("outEndTime").getValue());
        if (!outEndTime) {
            mini.alert("请选择预计结束时间");
            return;
        }
        window.parent.saveDraft(e);
    }

    //发起流程
    function startProcess(e) {
        var formValid = validNbgg();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        window.parent.startProcess(e);
    }

    //下一步审批
    function projectApprove() {
        //编制阶段的下一步需要校验表单必填字段
        if (stageName == 'start') {
            var formValid = validNbgg();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }
        if(stageName=='summary') {
            var outSummary = mini.get("outSummary").getValue();
            if(!outSummary) {
                mini.alert("请输入交流总结");
                return;
            }
        }

        //检查通过
        window.parent.approve();
    }


    function validNbgg() {
        // var followId = $.trim(mini.get("followId").getValue());
        // if (!followId) {
        //     return {"result": false, "message": "请选择同行人员"};
        // }
        var aimLocation = $.trim(mini.get("aimLocation").getValue());
        if (!aimLocation) {
            return {"result": false, "message": "请填写交流地点"};
        }
        var outStartTime = $.trim(mini.get("outStartTime").getValue());
        if (!outStartTime) {
            return {"result": false, "message": "请选择预计开始时间"};
        }
        var outEndTime = $.trim(mini.get("outEndTime").getValue());
        if (!outEndTime) {
            return {"result": false, "message": "请选择预计结束时间"};
        }
        var outSubject = $.trim(mini.get("outSubject").getValue());
        if (!outSubject) {
            return {"result": false, "message": "请填写主题描述"};
        }
        var outDesc = $.trim(mini.get("outDesc").getValue());
        if (!outDesc) {
            return {"result": false, "message": "请填写外出描述"};
        }
        return {"result": true};
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
            if (nodeVars[i].KEY_ == 'stageName') {
                stageName = nodeVars[i].DEF_VAL_;
            }
        }
        if (stageName != 'start') {
            nbggForm.setEnabled(false);
            mini.get("addFile").setEnabled(false);
        }
        if (stageName == 'summary') {
            nbggForm.setEnabled(false);
            mini.get("addFile").setEnabled(true);
            mini.get("outSummary").setAllowInput(true);
            mini.get("outSummary").setEnabled(true);


        }

    }


    function addNbggFile(applyId) {

        var stageKey = "";
        if (!applyId) {
            mini.alert('请先点击‘保存草稿’进行表单的保存！');
            return;
        }


        mini.open({
            title: "文件上传",
            url: jsUseCtxPath + "/zhgl/core/nbgg/openUploadWindow.do?applyId=" + applyId,
            width: 750,
            height: 450,
            showModal: false,
            allowResize: true,
            onload: function () {
                var iframe = this.getIFrameEl();
                var projectParams = {};
                projectParams.applyId = applyId;
                var data = {projectParams: projectParams};  //传递上传参数
                iframe.contentWindow.SetData(data);
            },
            ondestroy: function (action) {
                fileListGrid.load();
            }
        });
    }

    function operationRenderer(e) {
        var record = e.record;
        //预览、下载和删除
        if (!record.id) {
            return "";
        }
        var cellHtml = returnPreviewSpan(record.fileName, record.id, record.applyId, coverContent);
        var downloadUrl = '/zhgl/core/nbgg/fileDownload.do';
        cellHtml += '&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downLoadFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.applyId + '\',\'' + downloadUrl + '\')">下载</span>';

        if (record && (action == "edit" || stageName == "start" ||stageName =="summary")) {
            var deleteUrl = "/zhgl/core/nbgg/deleteFiles.do";
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.applyId + '\',\'' + deleteUrl + '\')">删除</span>';
        }
        return cellHtml;
    }

    function returnPreviewSpan(fileName, fileId, formId, coverContent) {
        var s = '';
        if (fileName == "") {
            return s;
        }
        var fileType = getFileType(fileName);

        if (fileType == 'other') {
            s = '<span  title="预览" style="color: silver" >预览</span>';
        } else {
            var url = '/zhgl/core/nbgg/preview.do?fileType=' + fileType;
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf6(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\',\'' + fileType + '\')">预览</span>';
        }

        return s;
    }


</script>
</body>
</html>
