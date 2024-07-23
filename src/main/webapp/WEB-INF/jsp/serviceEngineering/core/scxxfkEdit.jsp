<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html >
<html>
<head>
    <title>操保手册信息反馈</title>
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
        <a id="processInfo" class="mini-button" style="display: none" onclick="processInfo()"><spring:message code="page.scxxfkEdit.name" /></a>
        <a id="closeWindow1" class="mini-button" onclick="CloseWindow()"><spring:message code="page.scxxfkEdit.name1" /></a>
    </div>
</div>

<div class="mini-fit">
    <div class="form-container" style="margin:0 auto; width: 100%;height:98%;padding: 0">
        <form id="scxxfkForm" method="post" style="height: 95%;width: 98%">
            <input class="mini-hidden" id="id" name="id"/>
            <input id="instId" name="instId" class="mini-hidden"/>
            <p style="text-align: center;font-size: 20px;font-weight: bold;margin-top: 20px"><spring:message code="page.scxxfkEdit.name2" /></p>
            <table class="table-detail grey" cellspacing="1" cellpadding="0">
                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.scxxfkEdit.name3" />：</td>
                    <td style="min-width:170px">
                        <input id="applyNumber" name="applyNumber" class="mini-textbox" style="width:98%;"
                               enabled="false"/>
                    </td>

                    <td style="text-align: center;width: 20%"><spring:message code="page.scxxfkEdit.name4" />：</td>
                    <td style="min-width:170px">
                        <input id="applyTime" name="applyTime" class="mini-textbox" style="width:98%;"
                               enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.scxxfkEdit.name5" />：</td>
                    <td style="min-width:170px">
                        <input id="creatorName" name="creatorName" class="mini-textbox" style="width:98%;"
                               enabled="false"/>
                    </td>


                    <td style="text-align: center;width: 20%"><spring:message code="page.scxxfkEdit.name6" />：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="phone" name="phone" class="mini-textbox" style="width:98%;"
                               enabled="true"/>
                    </td>

                </tr>

                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.scxxfkEdit.name7" />：</td>
                    <td style="min-width:170px">
                        <input id="departName" name="departName" class="mini-textbox" style="width:98%;"
                               enabled="false"/>
                    </td>


                    <td style="text-align: center;width: 20%"><spring:message code="page.scxxfkEdit.name8" />：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input property="editor" class="mini-combobox"
                               style="width:98%;"
                               enabled ="false"
                               id="adoptions" name = "adoptions"
                               textField="key" valueField="value" emptyText="<spring:message code="page.scxxfkEdit.name9" />..."
                               required="false" allowInput="false" showNullItem="true"
                               nullItemText="<spring:message code="page.scxxfkEdit.name9" />..."
                               data="[{'key' : '采纳','value' : '采纳'}
                                       ,{'key' : '部分采纳','value' : '部分采纳'}
                                       ,{'key' : '不采纳','value' : '不采纳'}
                                  ]"
                               <%--disabled="true"--%>
                        />
                        <%--<input id="adoptions" name="adoptions" class="mini-textbox" style="width:98%;" enabled="false"/>--%>
                    </td>

                </tr>


                <tr>
                    <td style="text-align: center"><spring:message code="page.scxxfkEdit.name10" />：<span style="color:red">*</span></td>
                    <td colspan="3">
						<textarea id="descriptions" name="descriptions" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:98%;height:250px;line-height:25px;"
                                  label="<spring:message code="page.scxxfkEdit.name11" />" datatype="varchar" length="200" vtype="length:200" minlen="0"
                                  allowinput="true"
                                  emptytext="<spring:message code="page.scxxfkEdit.name12" />..." mwidth="80" wunit="%" mheight="150" hunit="px"></textarea>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.scxxfkEdit.name13" />：<span style="color:red">*</span></td>
                    <td colspan="3">
                        <input id="resolution" name="resolution" class="mini-textbox" style="width:98%;"
                               enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.scxxfkEdit.name14" />：<span style="color:red">*</span></td>
                    <td colspan="3">
                        <input id="leaderResolution" name="leaderResolution" class="mini-textbox" style="width:98%;"
                               enabled="fasle"/>
                    </td>
                </tr>

                <tr>
                    <td style= "text-align: center;width: 14%;height:10px"><spring:message code="page.scxxfkEdit.name15" />：</td>
                    <td colspan="3" height="60px">
                        <div id = "fileToolBar" style="margin-top: 10px;margin-bottom: 2px">
                            <a id="addFile" class="mini-button" onclick="addScxxfkFile('${applyId}')"><spring:message code="page.scxxfkEdit.name16" /></a>
                            <span style="color: red"><spring:message code="page.scxxfkEdit.name17" /></span>
                        </div>
                        <div id="fileListGrid" class="mini-datagrid"
                             allowResize="false"
                             idField="id"
                             url="${ctxPath}/serviceEngineering/core/scxxfk/demandList.do?applyId=${applyId}"
                             autoload="true"
                             multiSelect="true" showPager="false" showColumnsMenu="false"
                             allowAlternating="true"
                             style="height:150px;"
                        >
                            <div property="columns">
                                <div type="indexcolumn" align="center" width="20"><spring:message code="page.scxxfkEdit.name18" /></div>
                                <div field="fileName" width="140" headerAlign="center" align="center"><spring:message code="page.scxxfkEdit.name19" /></div>
                                <div field="fileDesc" width="80" headerAlign="center" align="center"><spring:message code="page.scxxfkEdit.name20" />
                                </div>
                                <div field="fileSize" width="80" headerAlign="center" align="center"><spring:message code="page.scxxfkEdit.name21" /></div>
                                <div field="action" width="100" headerAlign='center' align="center"
                                     renderer="operationRenderer"><spring:message code="page.scxxfkEdit.name22" />
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

    var scxxfkForm = new mini.Form("#scxxfkForm");
    var fileListGrid = mini.get("fileListGrid");


    var action = "${action}";
    var status = "${status}";
    var applyId = "${applyId}";
    var instId = "${instId}";
    var currentUserId = "${currentUserId}";
    var currentUserName = "${currentUserName}";
    var currentTime = "${currentTime}";
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>" + xzxgwjjxyxgs;



    function render(e) {
        if (e.value != null && e.value != "") {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            return html;
        }
    }

    function setApply() {
        var partsType = mini.get("partsType").getValue();
        if (!partsType) {
            mini.get("checker").setValue('');
            mini.get("checker").setText('');
            return;
        }
        $.ajax({
            url: jsUseCtxPath + '/serviceEngineering/core/scxxfk/getUserInfoByPartsType.do?partsType=' + partsType,
            type: 'get',
            async: false,
            contentType: 'application/json',
            success: function (data) {
                mini.get("checker").setValue(data.resId);
                mini.get("checker").setText(data.resName);
            }
        });
    }

    var stageName = "";
    $(function () {
        var url = jsUseCtxPath + "/serviceEngineering/core/scxxfk/getJson.do";
        $.post(
            url,
            {id: applyId},
            function (json) {
                scxxfkForm.setData(json);
            });
        if (action == 'detail') {
            scxxfkForm.setEnabled(false);
            $("#detailToolBar").show();
            $("#fileToolBar").hide();
            if (status != 'DRAFTED') {
                $("#processInfo").show();
            }
        } else if (action == 'task') {
            taskActionProcess();
        }
    });

    function getData() {
        var formData = _GetFormJsonMini("scxxfkForm");

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
        window.parent.saveDraft(e);
    }

    //发起流程
    function startProcess(e) {
        var formValid = validScxxfk();
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
            var formValid = validScxxfk();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        } else {
            var formValid = validNext();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }

            //检查通过
            window.parent.approve();
        }

    function validNext() {
        if (stageName == 'process') {
            var adoptions = $.trim(mini.get("adoptions").getValue());
            if (!adoptions) {
                return {"result": false, "message": scxxfkEdit_name};
            }
            var resolution = $.trim(mini.get("resolution").getValue());
            if (!resolution) {
                return {"result": false, "message": scxxfkEdit_name1};
            }
        }
        if (stageName == 'confirm') {
            var leaderResolution = $.trim(mini.get("leaderResolution").getValue());
            if (!leaderResolution) {
                return {"result": false, "message": scxxfkEdit_name2};
            }
        }





        return {"result": true};
    }


    function validScxxfk() {

        var phone = $.trim(mini.get("phone").getValue());
        if (!phone) {
            return {"result": false, "message": scxxfkEdit_name3};
        }
        var descriptions = $.trim(mini.get("descriptions").getValue());
        if (!descriptions) {
            return {"result": false, "message": scxxfkEdit_name4};
        }

        return {"result": true};
    }

    function processInfo() {
        var instId = $("#instId").val();
        var url = jsUseCtxPath + "/bpm/core/bpmInst/info.do?instId=" + instId;
        _OpenWindow({
            url: url,
            max: true,
            title: scxxfkEdit_name5,
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
            scxxfkForm.setEnabled(false);
            $("#fileToolBar").hide();

        }
        if (stageName == 'process') {
            mini.get("resolution").setEnabled("true");
            mini.get("adoptions").setEnabled("true");
        }
        if (stageName == 'confirm') {
            mini.get("leaderResolution").setEnabled("true");
        }

    }


    function addScxxfkFile(applyId) {

        var stageKey = "";
        if (!applyId) {
            mini.alert(scxxfkEdit_name6);
            return;
        }


        mini.open({
            title: scxxfkEdit_name7,
            url: jsUseCtxPath + "/serviceEngineering/core/scxxfk/openUploadWindow.do?applyId=" + applyId,
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
        var downloadUrl = '/serviceEngineering/core/scxxfk/fileDownload.do';
        cellHtml += '&nbsp;&nbsp;&nbsp;<span title=' + scxxfkEdit_name8 + ' style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downLoadFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.applyId + '\',\'' + downloadUrl + '\')">' + scxxfkEdit_name8 + '</span>';

        if (record && (action == "edit" || stageName == "start")) {
        // if (record ) {
            var deleteUrl = "/serviceEngineering/core/scxxfk/deleteFiles.do";
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title=' + scxxfkEdit_name9 + ' style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.applyId + '\',\'' + deleteUrl + '\')">' + scxxfkEdit_name9 + '</span>';
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
            s = '<span  title=' + scxxfkEdit_name10 + ' style="color: silver" >' + scxxfkEdit_name10 + '</span>';
        } else {
            var url = '/serviceEngineering/core/scxxfk/preview.do?fileType=' + fileType;
            s = '<span  title=' + scxxfkEdit_name10 + ' style="color:#409EFF;cursor: pointer;" onclick="previewPdf6(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\',\'' + fileType + '\')">' + scxxfkEdit_name10 + '</span>';
        }

        return s;
    }


</script>
</body>
</html>
