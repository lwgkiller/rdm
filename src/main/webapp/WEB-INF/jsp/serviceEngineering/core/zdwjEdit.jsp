<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html >
<html>
<head>
    <title>再制造技术指导文件上传</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}"
            type="text/javascript"></script>

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
        <form id="zdwjForm" method="post" style="height: 95%;width: 98%">
            <input class="mini-hidden" id="id" name="id"/>
            <input id="instId" name="instId" class="mini-hidden"/>
            <p style="text-align: center;font-size: 20px;font-weight: bold;margin-top: 20px">再制造技术指导文件上传</p>
            <table class="table-detail grey" cellspacing="1" cellpadding="0">
                <tr>
                    <td style="text-align: center;width: 20%">零部件类型：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="partsType" name="partsType" class="mini-combobox" style="width:98%;"
                               textField="value" valueField="key" emptyText="请选择..."
                               required="false" allowInput="false" showNullItem="true"
                               nullItemText="请选择..." onvaluechanged="setApply()"
                               data="[{'key' : '发动机','value' : '发动机'}
                                       ,{'key' : '主泵','value' : '主泵'}
                                       ,{'key' : '液压马达','value' : '液压马达'}
                                       ,{'key' : '油缸','value' : '油缸'}
                                       ,{'key' : '阀','value' : '阀'}
                                       ,{'key' : '电气件','value' : '电气件'}]"
                        />
                    </td>


                    <td style="text-align: center;width: 20%">零部件型号：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="partsModel" name="partsModel" class="mini-textbox" style="width:98%;"
                               enabled="true"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">文件类型：<span style="color:red">*</span></td>
                    <td>
                        <input id="fileType" name="fileType" class="mini-combobox" style="width:98%;"
                               textField="value" valueField="key" emptyText="请选择..."
                               required="false" allowInput="false" showNullItem="true"
                               nullItemText="请选择..."
                               data="[{'key' : '拆解作业指导书','value' : '拆解作业指导书'}
                                       ,{'key' : '装配作业指导书','value' : '装配作业指导书'}
                                       ,{'key' : '清洗作业规范','value' : '清洗作业规范'}
                                       ,{'key' : '涂装作业规范','value' : '涂装作业规范'}
                                       ,{'key' : '试验作业规范','value' : '试验作业规范'}]"
                        />
                    </td>

                    <td style="text-align: center;width: 20%">申请人：</td>
                    <td style="min-width:170px">
                        <input id="creatorName" name="creatorName" class="mini-textbox" style="width:98%;"
                               enabled="false"/>
                    </td>
                </tr>

                <tr>
                    <td style="text-align: center;width: 20%">会签人员：<span style="color:red">*</span></td>
                    <td>
                        <input id="cs" name="csId" textname="csName"
                               property="editor" class="mini-user rxc" plugins="mini-user"
                               style="width:98%;height:34px;" allowinput="false" mainfield="no" single="false"
                        />
                    </td>

                    <td style="text-align: center;width: 20%">审核人：<span style="color:red">*</span></td>
                    <td>
                        <input id="checker" name="checkerId" textname="checkerName"
                               property="editor" class="mini-user rxc" plugins="mini-user"
                               style="width:98%;height:34px;" allowinput="false" mainfield="no" single="fasle"
                        />
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">文档：<span style="color:red">*</span></td>
                    <td colspan="3">
                        <input class="mini-textbox" style="width:75%;float: left" id="fileName" name="fileName"
                               enabled="false"/>
                        <input id="inputFile"
                               style="display:none;"
                               type="file" onchange="getSelectFile()"/>
                        <a id="uploadFileBtn" class="mini-button" style="float: left;margin-left: 10px"
                           onclick="uploadFile">选择文件</a>
                        <a id="clearFileBtn" class="mini-button btn-red" style="float: left;margin-left: 10px"
                           onclick="clearUploadFile">清除</a>
                        <a id="previewBtn" class="mini-button" style="float: left;margin-left: 10px"
                           onclick="clickPreview">预览</a>
                        <a id="downloadBtn" class="mini-button" style="float: left;margin-left: 10px"
                           onclick="clickDownload">下载</a>

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

    var zdwjForm = new mini.Form("#zdwjForm");

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
        var url = jsUseCtxPath + "/serviceEngineering/core/zdwj/getJson.do";
        $.post(
            url,
            {id: applyId},
            function (json) {
                zdwjForm.setData(json);
            });
        if (action == 'detail') {
            zdwjForm.setEnabled(false);
            $("#detailToolBar").show();
            if (status != 'DRAFTED') {
                $("#processInfo").show();
            }
        } else if (action == 'task') {
            taskActionProcess();
        }
    });

    function getData() {

        // var file = $("#inputFile")[0].files[0];

        var formData = zdwjForm.getData();
        // formData.businessFile = file;
        zdwjForm.setData(formData);
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
        var formValid = validZdwj();
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
            var formValid = validZdwj();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }

        //检查通过
        window.parent.approve();
    }

    // 保存数据
    function saveBusiness() {
        var formData = zdwjForm.getData();
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
                                // CloseWindow();
                            });
                        }
                    }
                }
            };

            //开始上传
            xhr.open('POST', jsUseCtxPath + '/serviceEngineering/core/zdwj/saveBusiness.do', false);
            xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
            var fd = new FormData();
            if (formData) {
                for (key in formData) {
                    fd.append(key, formData[key]);
                }
            }
            fd.append('businessFile', file);
            xhr.send(fd);
        }
    }


    function validZdwj() {
        var partsType = $.trim(mini.get("partsType").getValue());
        if (!partsType) {
            return {"result": false, "message": "请选择零部件类型"};
        }
        var partsModel = $.trim(mini.get("partsModel").getValue());
        if (!partsModel) {
            return {"result": false, "message": "请填写零部件型号"};
        }
        var fileType = $.trim(mini.get("fileType").getValue());
        if (!fileType) {
            return {"result": false, "message": "请填写文件类型"};
        }
        //会签人和审核人
        var cs = $.trim(mini.get("cs").getValue());
        if (!cs) {
            return {"result": false, "message": "请选择会签人员"};
        }

        var checker = $.trim(mini.get("checker").getValue());
        if (!checker) {
            return {"result": false, "message": "请选择审核人"};
        }

        var fileName = $.trim(mini.get("fileName").getValue());
        if (!fileName) {
            return {"result": false, "message": "请上传文件"};
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
            zdwjForm.setEnabled(false);
            mini.get("uploadFileBtn").setEnabled(false);
            mini.get("clearFileBtn").setEnabled(false);
        }
    }

    function uploadFile() {
        var applyId = mini.get("id").getValue();
        if (!applyId) {
            mini.alert("请先保存草稿");
            return;
        }
        $("#inputFile").click();

    }

    function clearUploadFile() {

        if (!mini.get("fileName").getValue()) {
            mini.alert("请先上传文件！");
            return;
        }
        $("#inputFile").val('');
        mini.get("fileName").setValue('');
        saveBusiness();
    }

    function getSelectFile() {
        var fileList = $("#inputFile")[0].files;
        if (fileList && fileList.length > 0) {
            var fileNameSuffix = fileList[0].name.split('.').pop();
            mini.get("fileName").setValue(fileList[0].name);
            saveBusiness();
        }
    }

    function download(id, description) {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + "/serviceEngineering/core/zdwj/Download.do");
        var idAttr = $("<input>");
        idAttr.attr("type", "hidden");
        idAttr.attr("name", "id");
        idAttr.attr("value", id);
        var descriptionAttr = $("<input>");
        descriptionAttr.attr("type", "hidden");
        descriptionAttr.attr("name", "description");
        descriptionAttr.attr("value", description);
        $("body").append(form);
        form.append(idAttr);
        form.append(descriptionAttr);
        form.submit();
        form.remove();
    }

    function clickDownload() {
        // 下载各类型文件
        var id = mini.get("id").getValue();
        if (!id) {
            mini.alert("请先保存草稿");
        }
        var fileName = mini.get("fileName").getValue();
        if (fileName) {
            // downloadBusiness(id, row.partsAtlasName);
            download(id, fileName);
        } else {
            mini.alert("请先上传文件");
            return;
        }
    }


    function clickPreview() {
        // 预览pdf
        var id = mini.get("id").getValue();
        if (!id) {
            mini.alert("请先保存草稿");
            return;
        }
        var fileName = mini.get("fileName").getValue();
        if (fileName) {
            preview(id, fileName, coverContent);
        } else {
            mini.alert("请先上传文件");
            return;
        }
    }

    function preview(id, fileName, coverContent) {
        // 参数传过来的是字符串
        var previewUrl = jsUseCtxPath + "/serviceEngineering/core/zdwj/Preview.do?id=" + id + "&fileName=" + fileName;
        var currentWindow = window.open(jsUseCtxPath + "/pdfjs/web/viewer.html?coverContent=" + encodeURIComponent(coverContent) + "&file=" + encodeURIComponent(previewUrl));
        currentWindow.onload = function () {
            currentWindow.document.title = fileName;
        };
    }


</script>
</body>
</html>
