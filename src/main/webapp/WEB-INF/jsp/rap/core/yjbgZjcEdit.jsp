<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html >
<html>
<head>
    <title>组件表单</title>
    <%@include file="/commons/edit.jsp"%>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}" type="text/javascript"></script>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css" />
</head>
<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="processInfo" class="mini-button" style="display: none" onclick="processInfo()">流程信息</a>
        <a id="closeWindow1" class="mini-button" onclick="CloseWindow()">关闭</a>
    </div>
</div>

<div class="mini-fit">
    <div class="form-container" style="margin: 0 auto; width: 80%;">
        <form id="yjbgForm" method="post">
            <input class="mini-hidden" id="id" name="id" />
            <input id="instId" name="instId" class="mini-hidden"/>
            <input id="changeType" name="changeType" class="mini-textbox" visible="false"  />
            <table class="table-detail"  cellspacing="1" cellpadding="0">
                <tr>
                    <td style="text-align: center;width: 16%;height:40px">记录表名：</td>
                    <td style="width: 16%">
                        <input id="gridName"  name="gridName"  class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 16%;height:40px">涉及零部件名称：</td>
                    <td style="width: 16%">
                        <input id="bjName"  name="bjName"  class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">更改通知单号：</td>
                    <td style="min-width:170px">
                        <input id="tzdName"  name="tzdName"  class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="  width: 16%;text-align: center">电气、控制、液压、质量会签负责人：</td>
                    <td style="width: 16%">
                        <input id="hqName" name="hqId" textname="hqName"
                               property="editor" class="mini-user rxc" plugins="mini-user"
                               style="width:98%;height:34px;" allowinput="false" mainfield="no" single="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;height: 300px">更改通知单附件：</td>
                    <td colspan="5">
                        <div class="mini-toolbar" id="fileButton">
                            <a id="uploadTzdFile" class="mini-button"  style="margin-bottom: 5px;margin-top: 5px" onclick="uploadTzdFile()">上传文件</a>
                        </div>
                        <div id="fileTzdListGrid" class="mini-datagrid" style="width: 100%; height: 85%" allowResize="false"
                             idField="id" url="${ctxPath}/evn/core/Yjbg/getYjbgFileList.do?fileType=tzd&id=${id}" autoload="true"
                             multiSelect="false" showPager="false" showColumnsMenu="false" allowAlternating="true">
                            <div property="columns">
                                <div type="indexcolumn" headerAlign="center" align="center"  width="20">序号</div>
                                <div field="fileName" align="center" headerAlign="center" width="150">文件名</div>
                                <div field="fileSize" align="center"  headerAlign="center"  width="60" >文件大小</div>
                                <div field="userName" align="center"  headerAlign="center"  width="60" >上传人</div>
                                <div field="CREATE_TIME_" align="center"  headerAlign="center"  width="60" >上传时间</div>
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
    var nodeVarsStr = '${nodeVars}';
    var jsUseCtxPath="${ctxPath}";
    var yjbgForm = new mini.Form("#yjbgForm");
    var fileTzdListGrid = mini.get("fileTzdListGrid");
    var id="${id}";
    var action="${action}";
    var status="${status}";
    var changeType="${changeType}";
    var currentUserId="${currentUserId}";
    var currentUserName="${currentUserName}";
    var currentTime="${currentTime}";
    var coverContent=currentUserName+"<br/>"+currentTime+"<br/>徐州徐工挖掘机械有限公司";

    var isChuangjian="";


    $(function () {
        mini.get("changeType").setValue(changeType);
        if (id) {
            var url = jsUseCtxPath + "/evn/core/Yjbg/getYjbgDetail.do";
            $.post(
                url,
                {id: id},
                function (json) {
                    yjbgForm.setData(json);
                });
        }
        if (action == 'task') {
            taskActionProcess();
        } else if (action == "detail") {
            yjbgForm.setEnabled(false);
            mini.get("uploadTzdFile").setEnabled(false);
            $("#detailToolBar").show();
            if(status!='DRAFTED') {
                $("#processInfo").show();
            }
        }

    });

    function getData() {
        var formData = _GetFormJsonMini("yjbgForm");
        //此处用于向后台产生流程实例时替换标题中的参数时使用
        // formData.bos=[];
        // formData.vars=[{key:'companyName',val:formData.companyName}];
        return formData;
    }

    function saveYjbg(e) {
        var formValid = validChuangjian();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        window.parent.saveDraft(e);
    }
    function validChuangjian() {
        var gridName = $.trim(mini.get("gridName").getValue())
        if (!gridName) {
            return {"result": false, "message": "请填写记录表名"};
        }
        var bjName = $.trim(mini.get("bjName").getValue())
        if (!bjName) {
            return {"result": false, "message": "请填写涉及零部件名称"};
        }
        var hqName = $.trim(mini.get("hqName").getValue());
        if (!hqName) {
            return {"result": false, "message": "请选择会签负责人"};
        }
        var tzdName = $.trim(mini.get("tzdName").getValue())
        if (!tzdName) {
            return {"result": false, "message": "请填写更改通知单号"};
        }
        return {"result": true};
    }

    function startYjbgProcess(e) {
        var formValid = validDlgcs();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        window.parent.startProcess(e);
    }
    function yjbgGfApprove() {
        //编制阶段的下一步需要校验表单必填字段
        if (isChuangjian == 'yes') {
            var formValid = validDlgcs();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }
        //检查通过
        window.parent.approve();
    }

    function validDlgcs() {
        var gridName = $.trim(mini.get("gridName").getValue())
        if (!gridName) {
            return {"result": false, "message": "请填写记录表名"};
        }
        var bjName = $.trim(mini.get("bjName").getValue())
        if (!bjName) {
            return {"result": false, "message": "请填写涉及零部件名称"};
        }
        var hqName = $.trim(mini.get("hqName").getValue());
        if (!hqName) {
            return {"result": false, "message": "请选择会签负责人"};
        }
        var tzdName = $.trim(mini.get("tzdName").getValue())
        if (!tzdName) {
            return {"result": false, "message": "请填写更改通知单号"};
        }
        var fileListGrid2=$.trim(mini.get("fileTzdListGrid").getData());
        if(!fileListGrid2) {
            return {"result": false, "message": "请添加更改通知单附件"};
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

   

    function downLoadYjbgFile(fileName, fileId, formId) {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + '/evn/core/Yjbg/yjbgPdfPreview.do?action=download');
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

    function returnYjbgPreviewSpan(fileName, fileId, formId, coverContent) {
        var fileType = getFileType(fileName);
        var s = '';
        if (fileType == 'other') {
            s = '<span  title="预览" style="color: silver" >预览</span>';
        } else if (fileType == 'pdf') {
            var url = '/evn/core/Yjbg/yjbgPdfPreview';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        } else if (fileType == 'office') {
            var url = '/evn/core/Yjbg/yjbgOfficePreview';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        } else if (fileType == 'pic') {
            var url = '/evn/core/Yjbg/yjbgImagePreview';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        }
        return s;
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
            if (nodeVars[i].KEY_ == 'isChuangjian') {
                isChuangjian = nodeVars[i].DEF_VAL_;
            }

        }

        if (isChuangjian != 'yes') {
            yjbgForm.setEnabled(false);
            mini.get("uploadTzdFile").setEnabled(false);
        }

    }


    
    function operationRenderer(e) {
        var record = e.record;
        var cellHtml = '';
        cellHtml=returnYjbgPreviewSpan(record.fileName,record.fileId,record.belongId);
        cellHtml+='&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downLoadYjbgFile(\''+record.fileName+'\',\''+record.fileId+'\',\''+record.belongId+'\')">下载</span>';
        //增加删除按钮
        if(record.CREATE_BY_==currentUserId && action!='detail') {
            var deleteUrl="/evn/core/Yjbg/deleteYjbgFile.do"
            cellHtml+='&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(\''+record.fileName+'\',\''+record.fileId+'\',\''+record.belongId+'\',\''+deleteUrl+'\')">删除</span>';
        }
        return cellHtml;
    }

    function deleteFile(fileName,fileId,formId,urlValue) {
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
                        url:url,
                        method:'post',
                        contentType: 'application/json',
                        data:mini.encode(data),
                        success:function (json) {

                            if(fileTzdListGrid) {
                                fileTzdListGrid.load();
                            }
                        }
                    });
                }
            }
        );
    }


    function uploadTzdFile() {
        var id = mini.get("id").getValue();
        if (!id) {
            mini.alert("请先点击‘保存’进行表单创建！")
            return;
        }
        mini.open({
            title: "文件上传",
            url: jsUseCtxPath + "/evn/core/Yjbg/openUploadWindow.do?fileType=tzd&id=" + id,
            width: 850,
            height: 550,
            showModal: true,
            allowResize: true,
            ondestroy: function () {
                if (fileTzdListGrid) {
                    fileTzdListGrid.load();
                }
            }
        });
    }
</script>
</body>
</html>
