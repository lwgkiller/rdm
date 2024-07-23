x
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>测试数据信息编辑</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>

<body>
<div id="toolBar" class="topToolBar">
    <div>
        <a id="saveBusiness" class="mini-button" style="display: none" onclick="saveBusiness()">保存</a>
        <a id="closeWindow" class="mini-button btn-red" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin:0 auto; width: 100%;padding: 0">
        <form id="businessForm" method="post">
            <input class="mini-hidden" id="id" name="id"/>
            <input class="mini-hidden" id="mainId" name="mainId"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0" style="height: 100%;width: 100%">
                <caption style="font-weight: bold;font-size: 20px !important;">测试数据信息编辑</caption>
                <tr>
                    <td style="text-align: center;width: 15%">测试名称：</td>
                    <td style="min-width:170px">
                        <input id="testName" name="testName" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%">测试内容：</td>
                    <td style="min-width:170px">
                        <input id="testContent" name="testContent" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">测试时间区间：</td>
                    <td style="min-width:170px">
                        <input id="testTimeSpan" name="testTimeSpan" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%">备注：</td>
                    <td style="min-width:170px">
                        <input id="remark" name="remark" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;height: 300px">交付物列表：</td>
                    <td colspan="3">
                        <div class="mini-toolbar" id="fileButtons" style="display: none">
                            <a id="uploadFile" class="mini-button" style="margin-bottom: 5px;margin-top: 5px" onclick="uploadFile">添加附件</a>
                        </div>
                        <div id="fileListGrid" class="mini-datagrid" style="width: 100%; height: 85%" allowResize="false" idField="id"
                             url="${ctxPath}/powerApplicationTechnology/core/roadtest/getTestdataFileList.do?mainId=${businessId}"
                             multiSelect="false" showPager="true" showColumnsMenu="false" sizeList="[10,20,50,100]" pageSize="10"
                             allowAlternating="true" pagerButtons="#pagerButtons">
                            <div property="columns">
                                <div type="indexcolumn" headerAlign="center" align="center" width="20">序号</div>
                                <div field="fileType" align="center" headerAlign="center" width="60">文件类型</div>
                                <div field="fileName" align="center" headerAlign="center" width="150">文件名</div>
                                <div field="fileSize" align="center" headerAlign="center" width="60">文件大小</div>
                                <div field="fileDesc" align="center" headerAlign="center" width="100">备注说明</div>
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
    var jsUseCtxPath = "${ctxPath}";
    var businessForm = new mini.Form("#businessForm");
    var fileListGrid = mini.get("fileListGrid");
    var mainId = "${mainId}";
    var businessId = "${businessId}";
    var action = "${action}";
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var currentUserName = "${currentUserName}"
    var currentTime = new Date();
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";
    //..
    $(function () {
        if (businessId) {
            var url = jsUseCtxPath + "/powerApplicationTechnology/core/roadtest" +
                "/getTestdataDetail.do?businessId=" + businessId;
            $.ajax({
                url: url,
                method: 'get',
                success: function (json) {
                    businessForm.setData(json);
                }
            });
            fileListGrid.load();
        }
        //不同场景的处理
        if (action == 'detail') {
            businessForm.setEnabled(false);
        } else if (action == 'edit' || action == 'add') {
            mini.get("saveBusiness").show();
            mini.get("fileButtons").show();
        }
    });
    //..
    function saveBusiness() {
        var postData = {};
        postData.id = mini.get("id").getValue();
        //修改的时候直接取表单的mainId
        postData.mainId = mini.get("mainId").getValue();
        //新增的时候取传进来的mainId
        if (!postData.mainId || postData.mainId == '') {
            postData.mainId = mainId;
        }
        postData.testName = mini.get("testName").getValue();
        postData.testContent = mini.get("testContent").getValue();
        postData.testTimeSpan = mini.get("testTimeSpan").getText();
        postData.remark = mini.get("remark").getValue();
        //检查必填项
        var checkResult = saveValidCheck(postData);
        if (!checkResult.success) {
            mini.alert(checkResult.reason);
            return;
        }
        $.ajax({
            url: jsUseCtxPath + "/powerApplicationTechnology/core/roadtest/saveTestdata.do",
            type: 'POST',
            contentType: 'application/json',
            data: mini.encode(postData),
            success: function (returnData) {
                if (returnData && returnData.message) {
                    mini.alert(returnData.message, '提示', function () {
                        if (returnData.success) {
                            var url = jsUseCtxPath + "/powerApplicationTechnology/core/roadtest" +
                                "/testdataPage.do?businessId=" + returnData.data + "&action=" + action;
                            window.location.href = url;
                        }
                    });
                }
            }
        });
    }
    //..
    function saveValidCheck(postData) {
        var checkResult = {};
        if (!postData.testName) {
            checkResult.success = false;
            checkResult.reason = '请填写测试名称！';
            return checkResult;
        }
        if (!postData.testContent) {
            checkResult.success = false;
            checkResult.reason = '请填写测试内容！';
            return checkResult;
        }
        if (!postData.testTimeSpan) {
            checkResult.success = false;
            checkResult.reason = '请填写测试时间区间！';
            return checkResult;
        }
        checkResult.success = true;
        return checkResult;
    }
    //..
    function uploadFile() {
        var businessId = mini.get("id").getValue();
        if (!businessId) {
            mini.alert("请先点击‘保存’进行记录创建！")
            return;
        }
        mini.open({
            title: "文件上传",
            url: jsUseCtxPath + "/powerApplicationTechnology/core/roadtest/openTestdataFileUploadWindow.do?mainId=" + businessId,
            width: 800,
            height: 350,
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
    //..
    function operationRenderer(e) {
        var record = e.record;
        var cellHtml = '';
        cellHtml = returnPreviewSpan(record.fileName, record.id, record.mainId, coverContent);
        //和pdf预览用的同一个url
        var downLoadUrl = '/powerApplicationTechnology/core/roadtest/pdfPreviewAndAllDownload.do';
        cellHtml += '&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downLoadFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.mainId + '\',\'' + downLoadUrl + '\')">下载</span>';
        //增加删除按钮
        if (action != 'detail') {
            var deleteUrl = "/powerApplicationTechnology/core/roadtest/delTestdataFile.do"
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.mainId + '\',\'' + deleteUrl + '\')">删除</span>';
        }
        return cellHtml;
    }
    //..
    function returnPreviewSpan(fileName, fileId, formId, coverContent) {
        var fileType = getFileType(fileName);
        var s = '';
        if (fileType == 'other') {
            s = '<span  title="预览" style="color: silver" >预览</span>';
        } else if (fileType == 'pdf') {
            var url = '/powerApplicationTechnology/core/roadtest/pdfPreviewAndAllDownload.do';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        } else if (fileType == 'office') {
            var url = '/powerApplicationTechnology/core/roadtest/officePreview.do';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        } else if (fileType == 'pic') {
            var url = '/powerApplicationTechnology/core/roadtest/imagePreview.do';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        }
        return s;
    }
</script>
</body>
</html>
