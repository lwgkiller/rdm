<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html >
<html>
<head>
    <title>执行处理表单</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div id="detailToolBar" class="topToolBar">
    <div>
        <a id="saveFzzx" class="mini-button" onclick="saveFzzx()">保存</a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin:0 auto; width: 100%;height:98%;padding: 0">
        <form id="fzzxForm" method="post" style="height: 95%;width: 100%">
            <input class="mini-hidden" id="id" name="id"/>
            <input class="mini-hidden" id="fzsjId" name="fzsjId">
            <table class="table-detail grey" cellspacing="1" cellpadding="0">
                <tr id="szrtx">
                    <td style="text-align: center;width: 17%">执行人员：</td>
                    <td style="width: 33%;">
                        <input id="zxryId" name="zxryId" textname="zxry" class="mini-user rxc"
                               plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="指定执行人员" length="50"
                               mainfield="no" single="false"/>
                    </td>
                    <td style="text-align: center;width: 14%">时间节点：</td>
                    <td style="width: 33%;min-width:170px">
                        <input id="timeNode" name="timeNode" class="mini-datepicker" format="yyyy-MM-dd" allowInput="false" style="width:98%;"/>
                    </td>
                </tr>
                <tr id="zxrytx">
                    <td style="text-align: center;width: 14%">仿真结果及建议：</td>
                    <td colspan="3">
						<textarea id="fzjgjjy" name="fzjgjjy" class="mini-textarea rxc" plugins="mini-textarea"
                                  style="width:98%;height:150px;line-height:25px;" label="" datatype="varchar" length="1000" vtype="length:1000"
                                  minlen="0" allowinput="true"
                                  emptytext="请输入仿真结果及建议" wunit="%" mheight="150" hunit="px"></textarea>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 14%;height: 300px">附件：</td>
                    <td colspan="3">
                        <div style="margin-top: 10px;margin-bottom: 2px">
                            <a id="addFzzxFile" class="mini-button" onclick="addFzzxFile()">添加附件</a>
                        </div>
                        <div id="fileListGrid" class="mini-datagrid" style="width: 100%; height: 85%" allowResize="false"
                             idField="id" url="${ctxPath}/fzsj/core/fzsj/queryFzsjFileList.do?belongDetailId=${fzzxId}" autoload="true"
                             multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
                            <div property="columns">
                                <div type="indexcolumn" align="center" width="20">序号</div>
                                <div field="fileName" width="140" headerAlign="center" align="center">附件名称</div>
                                <div field="fileSize" width="80" headerAlign="center" align="center">附件大小</div>
                                <div field="CREATE_TIME_" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" headerAlign="center">上传时间</div>
                                <div field="action" width="100" headerAlign='center' align="center" renderer="operationRenderer">操作</div>
                            </div>
                        </div>
                    </td>
                </tr>
                <tr id="qrjg">
                    <td style="text-align: center;width: 14%">确认结果：</td>
                    <td style="width: 36%;min-width:170px" colspan="3">
                        <input id="confirmResult" name="confirmResult" class="mini-checkboxlist" multiSelect="false" style="width:98%"
                               textField="text" valueField="id"
                               data="[{id:'ty',text:'同意'},{id:'bty',text:'不同意'}]"
                        />
                    </td>
                </tr>
                <tr id="qryy">
                    <td style="text-align: center;width: 14%">确认原因：</td>
                    <td colspan="3">
						<textarea id="confirmReason" name="confirmReason" class="mini-textarea rxc" plugins="mini-textarea"
                                  style="width:98%;height:150px;line-height:25px;" label="" datatype="varchar" length="1000" vtype="length:1000"
                                  minlen="0" allowinput="true"
                                  emptytext="请输入确认原因" wunit="%" mheight="150" hunit="px"></textarea>
                    </td>
                </tr>
                <tr id="starTr">
                    <td style="text-align: center;width: 14%">打分：</td>
                    <td style="width: 36%;min-width:170px" colspan="3">
                        <input id="star" name="star" class="mini-combobox rxc" plugins="mini-combobox"
                               style="width:100%;height:34px" label="打分："
                               length="50"
                               only_read="false" required="true" allowinput="false" mwidth="100"
                               wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                               textField="text" valueField="key_" emptyText="请选择..."
                               url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=FZSJ_SCORE"
                               nullitemtext="请选择..." emptytext="请选择..."/>
                    </td>
                </tr>
                <tr id="commentTr">
                    <td style="text-align: center;width: 14%">评价：</td>
                    <td colspan="3">
						<textarea id="comment" name="comment" class="mini-textarea rxc" plugins="mini-textarea"
                                  style="width:98%;height:150px;line-height:25px;" label="" datatype="varchar" length="1000" vtype="length:1000"
                                  minlen="0" allowinput="true"
                                  emptytext="请输入评价" wunit="%" mheight="150" hunit="px"></textarea>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>


<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var fzzxForm = new mini.Form("#fzzxForm");
    var fzsjId = "${fzsjId}";
    var fzzxId = "${fzzxId}";
    var fzzxAction = "${fzzxAction}";
    var step = "${step}";
    var fileListGrid = mini.get("fileListGrid");
    var currentUserId = "${currentUserId}";
    var currentUserName = "${currentUserName}";
    var currentTime = "${currentTime}";
    var downloadPermission = "${downloadPermission}";
    var detailType = "${detailType}";
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";

    $(function () {
        mini.get("fzsjId").setValue(fzsjId);
        if (fzzxId) {
            var url = jsUseCtxPath + "/fzsj/core/fzsj/getFzzxDetail.do?fzzxId=" + fzzxId;
            $.ajax({
                url: url,
                method: 'get',
                async: false,
                success: function (json) {
                    fzzxForm.setData(json);
                }
            });
        }
        fileListGrid.load();
        fzzxForm.setEnabled(false);
        mini.get("addFzzxFile").setEnabled(false);
        if (fzzxAction == 'detail') {
            mini.get("saveFzzx").setVisible(false);
            if (detailType == 'ck') {
                $("#szrtx").hide();
                $("#zxrytx").hide();
                $("#qrjg").hide();
                $("#qryy").hide();
                $("#starTr").hide();
                $("#comentTr").hide();
            }
        } else if (fzzxAction == 'add') {
            mini.get("zxryId").setEnabled(true);
            mini.get("timeNode").setEnabled(true);
        } else {
            if (step == 'fzszrshApply' || step == 'fzszrshApprove') {
                mini.get("zxryId").setEnabled(true);
                mini.get("timeNode").setEnabled(true);
            } else if (step == 'fzrwzx') {
                mini.get("fzjgjjy").setEnabled(true);
                mini.get("addFzzxFile").setEnabled(true);
            } else {
                mini.get("confirmResult").setEnabled(true);
                mini.get("confirmReason").setEnabled(true);
                mini.get("star").setEnabled(true);
                mini.get("comment").setEnabled(true);
            }
        }
    })

    function operationRenderer(e) {
        var record = e.record;
        var cellHtml = '';
        cellHtml = returnFzsjPreviewSpan(record.fileName, record.id, record.belongDetailId, coverContent);
        if (fzzxAction == 'edit' && step == 'fzrwzx') {
            var deleteUrl = "/fzsj/core/fzsj/delFzsjFile.do";
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.belongDetailId + '\',\'' + deleteUrl + '\')">删除</span>';
        }
        return cellHtml;
    }

    function returnFzsjPreviewSpan(fileName, fileId, formId, coverContent) {
        var fileType = getFileType(fileName);
        var downloadUrl = "/fzsj/core/fzsj/fzsjFileDownload.do";
        var s = '';
        if (fileType == 'other') {
            s = '<span  title="预览" style="color: silver" >预览</span>';
        } else if (fileType == 'pdf') {
            var url = '/fzsj/core/fzsj/fzsjFileDownload.do';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + downloadUrl + '\')">预览</span>';
        } else if (fileType == 'office') {
            var url = '/fzsj/core/fzsj/fzsjOfficePreview.do';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        } else if (fileType == 'pic') {
            var url = '/fzsj/core/fzsj/fzsjImagePreview.do';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        }
        if (downloadPermission == 'true') {
            s += '&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="downLoadFile(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + downloadUrl + '\')">下载</span>';
        }
        return s;
    }

    function addFzzxFile() {
        mini.open({
            title: "文件上传",
            url: jsUseCtxPath + "/fzsj/core/fzsj/openFzsjUploadWindow.do?belongDetailId=" + fzzxId,
            width: 800,
            height: 350,
            showModal: false,
            allowResize: true,
            onload: function () {
            },
            ondestroy: function (action) {
                fileListGrid.load();
            }
        });
    }

    function saveFzzx() {
        if (step == 'fzszrshApply' || step == 'fzszrshApprove' || step == 'fzrwfp') {//..增加仿真任务分配节点
            var zxryId = mini.get("zxryId").getValue();
            if (!zxryId) {
                mini.alert("请选择执行人员");
                return;
            }
            var timeNode = mini.get("timeNode").getValue();
            if (!timeNode) {
                mini.alert("请选择时间节点");
                return;
            }
        } else if (step == 'fzrwzx') {
            var fzjgjjy = mini.get("fzjgjjy").getValue();
            if (!fzjgjjy) {
                mini.alert("请输入仿真结果及建议");
                return;
            }
        } else {
            var confirmResult = mini.get("confirmResult").getValue();
            if (!confirmResult) {
                mini.alert("请选择确认结果");
                return;
            }
            if (confirmResult == 'bty') {
                var confirmReason = mini.get("confirmReason").getValue();
                if (!confirmReason) {
                    mini.alert("请输入确认原因");
                    return;
                }
            }
            var star = mini.get("star").getValue();
            if (!star) {
                mini.alert("请打分");
                return;
            }
        }
        var formData = _GetFormJsonMini("fzzxForm");
        if (formData.SUB_fileListGrid) {
            delete formData.SUB_fileListGrid;
        }
        $.ajax({
            url: jsUseCtxPath + '/fzsj/core/fzsj/saveFzzx.do',
            type: 'post',
            async: false,
            data: mini.encode(formData),
            contentType: 'application/json',
            success: function (data) {
                if (data) {
                    var message = "";
                    if (data.success) {
                        message = "保存成功";
                    } else {
                        message = "保存失败" + data.message;
                    }
                    mini.alert(message, "提示信息", function () {
                        CloseWindow();
                    });
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
