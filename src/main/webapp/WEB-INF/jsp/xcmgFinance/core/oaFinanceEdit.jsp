<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>oa财务成本资料申请流程审批节点</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>

<body>
<div id="detailToolBar" class="topToolBar">
    <div>
        <p style="display: inline-block;color: red;font-size:15px;vertical-align: middle">（点击“下一步”后，OA流程会自动执行到下一步，本页面流程状态转为“已提交”，不可编辑）</p>
        <a id="nextPoint" class="mini-button" onclick="nextPoint()">下一步</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin: 0 auto; width: 100%;">
        <form id="formProject" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <input id="oaFlowId" name="oaFlowId" class="mini-hidden"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0">
                <caption style="font-weight: bold">
                    OA财务成本资料申请流程审批节点
                </caption>
                <tr>
                    <td style="text-align: center;width: 15%">主题：</td>
                    <td style="min-width:170px">
                        <input id="theme" name="theme" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 15%">申请部门：</td>
                    <td style="min-width:170px">
                        <input id="deptName" name="deptName" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">申请人：</td>
                    <td style="min-width:170px">
                        <input id="applyName" name="applyName" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 15%">流程处理人：</td>
                    <td style="min-width:170px">
                        <input id="userName" name="userName" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">创建时间：</td>
                    <td style="min-width:170px">
                        <input id="CREATE_TIME_" name="CREATE_TIME_" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 15%">类型：</td>
                    <td style="min-width:170px">
                        <input id="applyType" name="applyType" class="mini-textbox" style="width:98%;" enabled="false" renderer="onTypeRenderer"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">流程状态：</td>
                    <td style="min-width:170px">
                        <input id="submitType" name="submitType" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">申请内容及原因：</td>
                    <td colspan="3">
						<textarea id="applyText" name="applyText" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:99%;height:150px;line-height:25px;" label="" datatype="varchar" length="640"
                                  vtype="length:640" minlen="0" allowinput="true" wunit="%" mheight="150" hunit="px" enabled="false"></textarea>
                    </td>
                </tr>
                <%--///////////////////////////////////////////////////--%>
            </table>
            <div style="margin-top: 10px;margin-bottom: 2px">
                <%--                <a id="addLine" class="mini-button" onclick="addLine()">添加</a>--%>
                <a id="saveForm" class="mini-button" onclick="saveForm()">保存</a>
                <%--                <a id="removeLine" class="mini-button btn-red" onclick="removeLine()">删除</a>--%>
                <%--                <p style="display: inline-block;color: red;font-size:15px;vertical-align: middle">（编辑后都需要进行保存操作，保存后自动同步至OA）</p>--%>
            </div>
            <div id="matListGrid" class="mini-datagrid" allowResize="false" allowCellWrap="true"
                 idField="id" autoload="true" allowCellEdit="true" allowCellSelect="true"
                 multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
                <div property="columns">
                    <div type="checkcolumn" width="15"></div>
                    <div type="indexcolumn" align="center" width="20">序号</div>
                    <div field="oaFlowId" name="oaFlowId" visible="false"></div>
                    <div field="material" name="material" headerAlign="center" align="left" width="150">物料号<span style="color: red">*</span>
                        <input property="editor" class="mini-textarea" style="width: 100%;height: 50px"/>
                    </div>
                    <div field="description" name="description" headerAlign="center" align="left" width="150">物料描述<span style="color: red">*</span>
                        <input property="editor" class="mini-textarea" style="width: 100%;height: 50px"/>
                    </div>
                    <div field="typeName" name="typeName" headerAlign="center" align="left" width="150">物料名称或销售型号<span style="color: red">*</span>
                        <input property="editor" class="mini-textarea" style="width: 100%;height: 50px"/>
                    </div>
                    <div field="number" name="number" headerAlign="center" align="left" width="60">数量<span style="color: red">*</span>
                        <input property="editor" class="mini-textarea" style="width: 100%;height: 50px"/>
                    </div>
                    <div field="targetCost" name="targetCost" headerAlign="center" align="left" width="150">目标成本（不含税）<span style="color: red">*</span>
                        <input property="editor" class="mini-textarea" style="width: 100%;height: 50px"/>
                    </div>
                </div>
            </div>
        </form>
        <div style="margin-top: 10px;margin-bottom: 2px">
            <a id="addFile" class="mini-button" onclick="addOAFile()">添加附件</a>
            <%--            <a id="addToOA" class="mini-button"  onclick="addToOA()">上传至OA</a>--%>
        </div>
        <div id="fileListGrid" class="mini-datagrid" style="width: 100%; height: 85%" allowResize="false"
             idField="id" url="${ctxPath}/oa/oaFinance/oaFileList.do?oaFlowId=${oaFlowId}" autoload="true"
             multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
            <div property="columns">
                <div type="indexcolumn" align="center" width="20">序号</div>
                <div field="fileName" width="140" headerAlign="center" align="center">文件名称</div>
                <div field="fileSize" width="80" headerAlign="center" align="center">文件大小</div>
                <%--                <div field="fileDesc"  width="80" headerAlign="center" align="center" >备注</div>--%>
                <div field="action" width="100" headerAlign='center' align="center" renderer="operationRenderer">操作</div>
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var action = "${action}";
    var jsUseCtxPath = "${ctxPath}";
    var matListGrid = mini.get("matListGrid");
    var fileListGrid = mini.get("fileListGrid");
    var formProject = new mini.Form("#formProject");
    var coverContent = "<br/>徐州徐工挖掘机械有限公司";
    var id = "${id}";
    var oaFlowId = "${oaFlowId}";
    var action = "${action}";

    $(function () {
        initProjectForm();
        fileListGrid.load();
        if (action == 'detail') {
            matListGrid.setAllowCellEdit(false);
            // mini.get("addLine").setEnabled(false);
            mini.get("saveForm").setEnabled(false);
            // mini.get("removeLine").setEnabled(false);
            mini.get("nextPoint").setEnabled(false);
            mini.get("addFile").setEnabled(false);
            // mini.get("addToOA").setEnabled(false);
        }
    });

    //初始化表单数据（同步）
    function initProjectForm() {
        $.ajaxSettings.async = false;
        //根据projectId查询项目信息并填充
        if (oaFlowId) {
            var url = jsUseCtxPath + "/oa/oaFinance/getJson.do?oaFlowId=" + oaFlowId;
            $.get(url, function (data) {
                formProject.setData(data);
                var OAList = data.OAList;
                if (OAList != null && OAList.length > 0) {
                    matListGrid.setData(OAList);
                }
            });
        }
        $.ajaxSettings.async = true;
    }

    matListGrid.on("cellbeginedit", function (e) {
        var record = e.record;
        if (action == 'detail') {
            e.cancel = true;
        } else if (action = 'edit') {
            e.cancel = false;
        }
    });

    function addLine() {
        var row = {};
        matListGrid.addRow(row);
    }
    //删除计划
    function removeLine() {
        var selecteds = matListGrid.getSelecteds();
        matListGrid.removeRows(selecteds);
    }

    //保存
    function saveForm() {
        var formData = matListGrid.getData();
        var checkResult = checkStandardEditRequired(formData);
        if (!checkResult) {
            return;
        }
        $.ajax({
            url: jsUseCtxPath + '/oa/oaFinance/saveForm.do?oaFlowId=' + oaFlowId,
            type: 'post',
            async: false,
            data: mini.encode(formData),
            contentType: 'application/json',
            success: function (returnData) {
                if (returnData && returnData.message) {
                    mini.alert(returnData.message, '提示', function () {
                        if (returnData.success) {
                            initProjectForm();
                        }
                    });
                }
            }
        });
    }

    //表格字段必填校验
    function checkStandardEditRequired(formData) {
        for (var i = 0; i < formData.length; i++) {
            // if (!formData[i]) {
            //     mini.alert("表格信息为空！");
            //     return false;
            // }
            if (!$.trim(formData[i].material)) {
                mini.alert("第" + (i + 1) + "行物料号不能为空！");
                return false;
            }
            if (!$.trim(formData[i].description)) {
                mini.alert("第" + (i + 1) + "行物料描述不能为空！");
                return false;
            }
            if (!$.trim(formData[i].targetCost)) {
                mini.alert("第" + (i + 1) + "行目标成本（不含税）不能为空！");
                return false;
            }
        }
        return true;
    }

    //保存并同步至oa
    function saveFormToOA() {
        var formData = matListGrid.getData();
        var checkResult = checkStandardEditRequired(formData);
        if (!checkResult) {
            return false;
        }
        $.ajax({
            url: jsUseCtxPath + '/oa/oaFinance/saveFormToOA.do?oaFlowId=' + oaFlowId,
            type: 'post',
            async: false,
            data: mini.encode(formData),
            contentType: 'application/json',
            success: function (returnData) {
            }
        });
        return true;
    }

    function nextPoint() {
        //校验表单数据
        var formData = matListGrid.getData();
        var checkResult = checkStandardEditRequired(formData);
        if (!checkResult) {
            return false;
        }
        //保存提示
        var change = matListGrid.getChanges();
        if (change.length!=0) {
            mini.alert("请先保存表单数据再执行下一步！");
            return;
        }
        //保存内容至OA
        if (!oaFlowId) {
            mini.alert("OA流程号获取失败，请联系系统管理员！");
            return;
        }
        $.ajax({
            url: jsUseCtxPath + '/oa/oaFinance/addToOA.do?oaFlowId=' + oaFlowId,
            type: 'get',
            success: function () {
                //oa机器人流程节点唤醒至下一步
                $.ajax({
                    url: jsUseCtxPath + '/oa/oaFinance/nextPoint.do?oaFlowId=' + oaFlowId,
                    type: 'get',
                    success: function (returnData) {
                        if (returnData && returnData.message) {
                            mini.alert(returnData.message, '提示', function () {
                                if (returnData.success) {
                                    window.close();
                                }
                            });
                        }
                    }
                });
            }
        });
    }

    function onTypeRenderer(e) {
        var record = e.record;
        var applyType = record.applyType;
        var arr = [{'key': '0', 'value': '常规'}, {'key': '1', 'value': '代理商特殊需求'}];
        return $.formatItemValue(arr, applyType);
    }

    function addOAFile() {
        if (!oaFlowId) {
            mini.alert("OA流程号获取失败，请联系系统管理员！");
            return;
        }
        mini.open({
            title: "文件上传",
            url: jsUseCtxPath + "/oa/oaFinance/openOAUploadWindow.do?oaFlowId=" + oaFlowId,
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

    function addToOA() {
        if (!oaFlowId) {
            mini.alert("OA流程号获取失败，请联系系统管理员！");
            return;
        }
        $.ajax({
            url: jsUseCtxPath + '/oa/oaFinance/addToOA.do?oaFlowId=' + oaFlowId,
            type: 'get',
            success: function () {
                // mini.alert("上传OA成功！");
            }
        });
    }

    function operationRenderer(e) {
        var record = e.record;
        var cellHtml = '';
        cellHtml = returnOAPreviewSpan(record.fileName, record.id, oaFlowId, coverContent);
        //增加删除按钮
        if (action == 'edit') {
            var deleteUrl = "/oa/oaFinance/delOAUploadFile.do";
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title=' + "删除" + ' style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + oaFlowId + '\',\'' + deleteUrl + '\')">' + "删除" + '</span>';
        }
        return cellHtml;
    }

    function returnOAPreviewSpan(fileName, fileId, formId, coverContent) {
        var fileType = getFileType(fileName);
        //复用接口
        var downloadUrl = "/oa/oaFinance/oaFileDownload.do";
        var s = '';
        if (fileType == 'other') {
            s = '<span  title=' + "预览" + ' style="color: silver" >' + "预览" + '</span>';
        } else if (fileType == 'pdf') {
            var url = '/oa/oaFinance/cjzgFileDownload.do';
            s = '<span  title=' + "预览" + ' style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + downloadUrl + '\')">' + "预览" + '</span>';
        } else if (fileType == 'office') {
            var url = '/oa/oaFinance/oaOfficePreview.do';
            s = '<span  title=' + "预览" + ' style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">' + "预览" + '</span>';
        } else if (fileType == 'pic') {
            var url = '/oa/oaFinance/oaImagePreview.do';
            s = '<span  title=' + "预览" + ' style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">' + "预览" + '</span>';
        }
        s += '&nbsp;&nbsp;&nbsp;<span title=' + '下载' + ' style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downLoadFile(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + downloadUrl + '\')">' + '下载' + '</span>';
        return s;
    }

</script>
</body>
</html>
