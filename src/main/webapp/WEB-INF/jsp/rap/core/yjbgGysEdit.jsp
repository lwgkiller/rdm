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
            <input id="changeType" name="changeType" class="mini-textbox"  visible="false"/>
            <table class="table-detail"  cellspacing="1" cellpadding="0">
                <tr>
                    <td style="  width:12%;text-align: center">动力室室主任：</td>
                    <td style="width: 12%">
                        <input id="szrName" name="szrId" textname="szrName"
                               property="editor" class="mini-user rxc" plugins="mini-user"
                               style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"/>
                    </td>
                    <td style="text-align: center;width: 12%">动力工程师：</td>
                    <td style="width: 12%">
                        <input id="dlName" name="dlId" textname="dlName"
                               property="editor" class="mini-user rxc" plugins="mini-user"
                               style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"/>
                    </td>
                </tr>
                <tr>
                    <td style="  width: 12%;text-align: center">电气、控制、液压会签负责人：</td>
                    <td style="width: 12%">
                        <input id="hqName" name="hqId" textname="hqName"
                               property="editor" class="mini-user rxc" plugins="mini-user"
                               style="width:98%;height:34px;" allowinput="false" mainfield="no" single="false"/>
                    </td>
                    <td style="  width:12%;text-align: center">质量专员：</td>
                    <td style="width: 12%">
                        <input id="zlName" name="zlId" textname="zlName"
                               property="editor" class="mini-user rxc" plugins="mini-user"
                               style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 12%;height:40px">记录表名：</td>
                    <td style="width: 12%">
                        <input id="gridName"  name="gridName"  class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 12%;height:40px">涉及零部件名称：</td>
                    <td style="width: 12%">
                        <input id="bjName"  name="bjName"  class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td  style="text-align: center;height: 300px">SMIE表附件：</td>
                    <td colspan="5">
                        <div style="margin-top: 5px;margin-bottom: 5px">
                            <a id="uploadSEMI" class="mini-button"  onclick="uploadSmieFile()">上传文件</a>
                        </div>
                        <div id="fileSMIEListGrid" class="mini-datagrid" style="width: 100%; height: 80%" allowResize="false"
                             idField="id" url="${ctxPath}/evn/core/Yjbg/getYjbgFileList.do?fileType=smie&id=${id}" autoload="true"
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
                <tr>
                    <td style="text-align: center;width: 12%">更改通知单号：</td>
                    <td style="min-width:170px">
                        <input id="tzdName"  name="tzdName"  class="mini-textbox" style="width:98%;"/>
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
    var fileSMIEListGrid = mini.get("fileSMIEListGrid");
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
    var isDLSZR="";
    var isDLGCS="";

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
        yjbgForm.setEnabled(false);
        mini.get("uploadSEMI").setEnabled(true);
        mini.get("uploadTzdFile").setEnabled(false);
        mini.get("gridName").setEnabled(true);
        mini.get("bjName").setEnabled(true);
        mini.get("szrName").setEnabled(true);
        if (action == 'task') {
            taskActionProcess();
        } else if (action == "detail") {
            yjbgForm.setEnabled(false);
            mini.get("uploadSEMI").setEnabled(false);
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
        var formValid = validGfCG();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        window.parent.saveDraft(e);
    }
    function startYjbgProcess(e) {
        var formValid = validGfCJ();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        window.parent.startProcess(e);
    }
    function yjbgGfApprove() {
        //编制阶段的下一步需要校验表单必填字段
        if (isChuangjian == 'yes') {
            var formValid = validGfCJ();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }
        //检查通过
        window.parent.approve();
    }
    function yjbgDlszrApprove() {
        //编制阶段的下一步需要校验表单必填字段
        if (isDLSZR == 'yes') {
            var formValid = validDlszr();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }
        //检查通过
        window.parent.approve();
    }
    function yjbgDlgcsApprove() {
        //编制阶段的下一步需要校验表单必填字段
        if (isDLGCS == 'yes') {
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
        var tzdName = $.trim(mini.get("tzdName").getValue())
        if (!tzdName) {
            return {"result": false, "message": "请填写更改通知单号"};
        }
        var fileListGrid2=$.trim(mini.get("fileTzdListGrid").getData());
        if(!fileListGrid2) {
            return {"result": false, "message": "请添加更改通知单附件"};
        }
        var zlName = $.trim(mini.get("zlName").getValue());
        if (!zlName) {
            return {"result": false, "message": "请选择质量专员"};
        }
        return {"result": true};
    }

    function validGfCG() {
        var gridName = $.trim(mini.get("gridName").getValue())
        if (!gridName) {
            return {"result": false, "message": "请填写记录表名"};
        }
        var bjName = $.trim(mini.get("bjName").getValue())
        if (!bjName) {
            return {"result": false, "message": "请填写涉及零部件名称"};
        }
        var szrName = $.trim(mini.get("szrName").getValue())
        if (!szrName) {
            return {"result": false, "message": "请选择动力室主任"};
        }
        return {"result": true};
    }

    function validGfCJ() {
        var gridName = $.trim(mini.get("gridName").getValue())
        if (!gridName) {
            return {"result": false, "message": "请填写记录表名"};
        }
        var bjName = $.trim(mini.get("bjName").getValue())
        if (!bjName) {
            return {"result": false, "message": "请填写涉及零部件名称"};
        }
        var szrName = $.trim(mini.get("szrName").getValue())
        if (!szrName) {
            return {"result": false, "message": "请选择动力室主任"};
        }
        var fileSMIEListGrid=$.trim(mini.get("fileSMIEListGrid").getData());
        if(!fileSMIEListGrid) {
            return {"result": false, "message": "请添加SMIE表附件"};
        }

        return {"result": true};
    }
    
    function validDlszr() {
        var dlName = $.trim(mini.get("dlName").getValue());
        if (!dlName) {
            return {"result": false, "message": "请选择动力工程师"};
        }
        var hqName = $.trim(mini.get("hqName").getValue());
        if (!hqName) {
            return {"result": false, "message": "请选择会签负责人"};
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
            if (nodeVars[i].KEY_ == 'isDLGCS') {
                isDLGCS = nodeVars[i].DEF_VAL_;
            }
            if (nodeVars[i].KEY_ == 'isDLSZR') {
                isDLSZR = nodeVars[i].DEF_VAL_;
            }

        }
        yjbgForm.setEnabled(false);
        mini.get("uploadSEMI").setEnabled(false);
        mini.get("uploadTzdFile").setEnabled(false);

        if (isChuangjian == 'yes') {
            mini.get("uploadSEMI").setEnabled(true);
            mini.get("gridName").setEnabled(true);
            mini.get("bjName").setEnabled(true);
            mini.get("szrName").setEnabled(true);
        }
        if (isDLSZR == 'yes') {
            mini.get("hqName").setEnabled(true);
            mini.get("dlName").setEnabled(true);
        }
        if (isDLGCS == 'yes') {
            mini.get("uploadTzdFile").setEnabled(true);
            mini.get("tzdName").setEnabled(true);
            mini.get("zlName").setEnabled(true);
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
                            if(fileSMIEListGrid) {
                                fileSMIEListGrid.load();
                            }
                            if(fileTzdListGrid) {
                                fileTzdListGrid.load();
                            }
                        }
                    });
                }
            }
        );
    }
    function uploadSmieFile() {
        var id = mini.get("id").getValue();
        if (!id) {
            mini.alert("请先点击‘保存’进行表单创建！")
            return;
        }
        mini.open({
            title: "文件上传",
            url: jsUseCtxPath + "/evn/core/Yjbg/openUploadWindow.do?fileType=smie&id=" + id,
            width: 850,
            height: 550,
            showModal: true,
            allowResize: true,
            ondestroy: function () {
                if (fileSMIEListGrid) {
                    fileSMIEListGrid.load();
                }
            }
        });
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
