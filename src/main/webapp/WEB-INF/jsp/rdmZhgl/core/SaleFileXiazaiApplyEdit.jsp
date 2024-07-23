<%--
  Created by IntelliJ IDEA.
  User: zhangwentao
  Date: 2024/1/29
  Time: 17:18
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>售前文件下载申请表单</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
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
        <form id="saleFileXiazaiForm" method="post" style="height: 95%;width: 98%">
            <input class="mini-hidden" id="id" name="id"/>
            <input id="instId" name="instId" class="mini-hidden"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0">
                <tr>
                    <td style="text-align: center;width: 20%">申请编号：</td>
                    <td style="min-width:170px">
                        <input id="applyNumber" name="applyNumber" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 20%">申请人：</td>
                    <td style="min-width:170px">
                        <input id="CREATE_BY_" name="CREATE_BY_" class="mini-textbox" style="display: none"/>
                        <input id="creatorName" name="creatorName" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">申请部门：</td>
                    <td style="min-width:170px">
                        <input name="creatorDeptId" class="mini-textbox" style="display: none"/>
                        <input id="creatorDeptName" name="creatorDeptName" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">用途说明：（不超过1000字）</td>
                    <td colspan="3">
						<textarea id="useDesc" name="useDesc" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:99.1%;height:100px;line-height:25px;"
                                  label="说明" datatype="varchar" allowinput="true"
                                  emptytext="请输入说明..." mwidth="80" wunit="%" mheight="20000" hunit="px"></textarea>
                    </td>
                </tr>
            </table>
            <%----------------------------------------------------------------------------------------------------------%>
            <p style="font-size: 16px;font-weight: bold;margin-top: 20px">申请下载的欧美澳+类中国售前文件清单
                <span style="font-size: 14px;color:red">（仅允许申请人在流程结束后下载）</span>
            </p>
            <div class="mini-toolbar" style="margin-bottom: 5px" id="demandListToolBar">
                <a class="mini-button" id="selfAddBtn" plain="true" onclick="addManualFile()">新增</a>
                <a class="mini-button btn-red" id="delDemand" plain="true" onclick="delManualFile()">移除</a>
            </div>
            <div id="salefileOMAGrid" class="mini-datagrid" allowResize="false" style="height:340px" autoload="true"
                 idField="id" allowCellEdit="false" allowCellSelect="true" allowSortColumn="false"
                 multiSelect="true" showColumnsMenu="false" allowAlternating="true" showPager="false"
                 url="${ctxPath}/rdmZhgl/core/SaleFileOMAXiazaiApply/saleFileOMAList.do?applyId=${applyId}">
                <div property="columns">
                    <div type="checkcolumn" width="50"></div>
                    <div type="indexcolumn" headerAlign="center" width="50">序号</div>
                    <div field="saleModel" width="120" headerAlign="center" align="center" allowSort="true">销售型号</div>
                    <div field="designModel" width="120" headerAlign="center" align="center" allowSort="true">设计型号</div>
                    <div field="materialCode" width="120" headerAlign="center" align="center" allowSort="true">物料编码</div>
                    <div field="directorName" width="80" headerAlign="center" align="center" allowSort="true">产品主管</div>
                    <div field="fileName" width="500" headerAlign="center" align="center" renderer="render" allowSort="true">文件名称</div>
                    <div field="language" width="80" headerAlign="center" align="center" allowSort="true">手册语言</div>
                    <div field="fileType" width="140" headerAlign="center" align="center" allowSort="true" >文件类型</div>
                    <div field="systemType" width="80" headerAlign="center" align="center" allowSort="true">系统类型</div>
                    <div field="applicabilityDoc" width="80" headerAlign="center" align="center" allowSort="true">适用性说明</div>
                    <div field="version" width="80" headerAlign="center" align="center" allowSort="true">文件版本</div>
                    <div field="region" width="80" headerAlign="center" align="center" allowSort="true">销售区域</div>
                    <div field="fileStatus" width="100" headerAlign="center" align="center" allowSort="true">文件状态</div>
                    <div cellCls="actionIcons" width="110" headerAlign="center" align="center" renderer="saleFileRenderer" cellStyle="padding:0;">
                        操作
                    </div>
                </div>
            </div>

        </form>
    </div>
</div>
<%----------------------------------------------------------------------------------------------------------%>
<div id="selectManualFileWindow" title="选择欧美澳类中国售前文件" class="mini-window" style="width:1450px;height:700px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
    <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px" borderStyle="border-left:0;border-top:0;border-right:0;">
        <li style="margin-right: 15px">
            <span class="text" style="width:auto">销售型号：</span>
            <input class="mini-textbox" id="salesModel" name="salesModel" style="width: 120px"/>
            <span class="text" style="width:auto">设计型号：</span>
            <input class="mini-textbox" id="designModel" name="designModel" style="width: 120px"/>
            <span class="text" style="width:auto">物料编码：</span>
            <input class="mini-textbox" id="materialCode" name="materialCode" style="width: 120px"/>
            <span class="text" style="width:auto">文件名称：</span>
            <input class="mini-textbox" id="fileName" name="fileName" style="width: 120px"/>
            <span class="text" style="width:auto">语种：</span>
            <input class="mini-textbox" id="language" name="language" style="width: 120px"/>
        </li>
        <a class="mini-button" iconCls="icon-search" plain="true" onclick="searchManualFile()">查询</a>
        <span style="color: red;font-size: 15px">（仅展示状态为“已发布”的数据）</span>
    </div>
    <div class="mini-fit">
        <div id="manualFileListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
             idField="id" showColumnsMenu="false" sizeList="[20,50,100,200]" pageSize="20" multiSelect="true"
             allowAlternating="true" pagerButtons="#pagerButtons" url="${ctxPath}/rdmZhgl/core/SaleFileOMAXiazaiApply/querySaleFileOMAList.do">
            <div property="columns">
                <div type="checkcolumn" width="30"></div>
                <div field="saleModel" width="120" headerAlign="center" align="center" allowSort="true">销售型号</div>
                <div field="designModel" width="120" headerAlign="center" align="center" allowSort="true">设计型号</div>
                <div field="materialCode" width="120" headerAlign="center" align="center" allowSort="true">物料编码</div>
                <div field="fileName" width="400" headerAlign="center" align="center"  allowSort="true">文件名称</div>
                <div field="directorName" width="80" headerAlign="center" align="center" allowSort="true">产品主管</div>
                <div field="language" width="80" headerAlign="center" align="center" allowSort="true">手册语言</div>
                <div field="systemType" width="140" headerAlign="center" align="center" allowSort="true">系统类型</div>
                <div field="fileType" width="140" headerAlign="center" align="center" allowSort="true">文件类型</div>
                <div field="version" width="80" headerAlign="center" align="center" allowSort="true">文件版本</div>
                <div field="region" width="80" headerAlign="center" align="center" allowSort="true">销售区域</div>
                <div field="applicabilityDoc" width="80" headerAlign="center" align="center" allowSort="true">适用性说明</div>
                <div field="fileStatus" width="100" headerAlign="center" align="center" allowSort="true">文件状态</div>
            </div>
        </div>
    </div>
    <div property="footer" style="padding:5px;height: 35px">
        <table style="width:100%;height: 100%">
            <tr>
                <td style="width:120px;text-align:center;">
                    <input type="button" style="height: 25px;width: 70px" value="确定" onclick="selectFileOK()"/>
                    <input type="button" style="height: 25px;width: 70px" value="取消" onclick="selectFileHide()"/>
                </td>
            </tr>
        </table>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var nodeVarsStr = '${nodeVars}';
    var jsUseCtxPath = "${ctxPath}";
    var saleFileXiazaiForm = new mini.Form("#saleFileXiazaiForm");
    var salefileOMAGrid = mini.get("salefileOMAGrid");
    var selectManualFileWindow = mini.get("selectManualFileWindow");
    var manualFileListGrid = mini.get("manualFileListGrid");
    var action = "${action}";
    var status = "${status}";
    var applyId = "${applyId}";
    var currentUserId = "${currentUserId}";
    var currentUserName = "${currentUserName}";
    var currentTime = "${currentTime}";
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>" + xzxgwjjxyxgs;
    var stageName = "";
    var typeList = getDics("sailFileOMA_WJFL");
    var systemTypelist =getDics("sailFileOMA_XTFL");
    $(function () {
        var url = jsUseCtxPath + "/rdmZhgl/core/SaleFileOMAXiazaiApply/getJson.do";
        $.post(
            url,
            {id: applyId},
            function (json) {
                saleFileXiazaiForm.setData(json);
            });
        if (action == 'detail') {
            saleFileXiazaiForm.setEnabled(false);
            mini.get("demandListToolBar").hide();
            $("#detailToolBar").show();
            if (status != 'DRAFTED') {
                $("#processInfo").show();
            }
        } else if (action == 'task') {

            taskActionProcess();
        }
    });

    function addManualFile() {
        var meetingId = mini.get("id").getValue();
        if (!meetingId) {
            mini.alert("请先点击‘保存草稿’进行表单创建！")
            return;
        }
        //打开选择窗口，选择后插入本表中
        selectManualFileWindow.show();
        searchManualFile();
    }
    function delManualFile() {
        var selecteds = salefileOMAGrid.getSelecteds();
        salefileOMAGrid.removeRows(selecteds);
    }
    function searchManualFile() {
        var paramArray = [];
        paramArray.push({name: "saleModel", value: mini.get('salesModel').getValue()});
        paramArray.push({name: "designModel", value: mini.get('designModel').getValue()});
        paramArray.push({name: "materialCode", value: mini.get('materialCode').getValue()});
        paramArray.push({name: "fileName", value: mini.get('fileName').getValue()});
        paramArray.push({name: "language", value: mini.get('language').getValue()});
        var data = {};
        data.filter = mini.encode(paramArray);
        manualFileListGrid.load(data);
    }

    function selectFileOK() {
        var rows = manualFileListGrid.getSelecteds();
        if (rows.length > 0) {
            for (var index = 0; index < rows.length; index++) {
                var row = rows[index];
                if (row.id) {
                    row.salefileId = row.id;
                    salefileOMAGrid.addRow(row);
                }
            }
        }
        selectFileHide();
    }
    function selectFileHide() {
        mini.get('salesModel').setValue('');
        mini.get('designModel').setValue('');
        mini.get('materialCode').setValue('');
        mini.get('language').setValue('');
        mini.get('fileName').setValue('');
        selectManualFileWindow.hide();
    }

    function saleFileRenderer(e){
        var record = e.record;
        debugger
        //需都可以预览，只有申请人在流程结束后，通过detail查看才能下载
        var cellHtml = '<span  title=' + "预览" + ' style="color:#409EFF;cursor: pointer;" onclick="previewManualFile(\'' + record.salefileId + '\',\'' + record.fileName + '\',\''+record.fileId + '\',\''+coverContent + '\')">' + "预览" + '</span>';
        var creatorId = mini.get("CREATE_BY_").getValue();
        if (action == 'detail' && status == 'SUCCESS_END' && currentUserId == creatorId) {
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title=' + "下载" + ' style="color:#409EFF;cursor: pointer;" ' +
                'onclick="downloadManualFile(\'' + record.salefileId + '\',\'' + record.fileName + '\',\''+record.fileId+'\')">' + "下载" + '</span>';
        }

        return cellHtml;
    }
    //..
    function previewManualFile(id,fileName, fileId,coverContent) {
        var previewUrl = jsUseCtxPath + "/rdmZhgl/core/SaleFileOMAXiazaiApply/preview.do?id=" + id+"&fileName=" + encodeURIComponent(fileName) +"&fileId=" + fileId;
        window.open(jsUseCtxPath + "/pdfjs/web/viewer.html?coverContent=" + encodeURIComponent(coverContent) + "&file=" + encodeURIComponent(previewUrl));
    }
    //..
    function downloadManualFile(id, description,fileId) {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + "/rdmZhgl/core/SaleFileOMAXiazaiApply/download.do");
        var idAttr = $("<input>");
        idAttr.attr("type", "hidden");
        idAttr.attr("name", "id");
        idAttr.attr("value", id);
        var descriptionAttr = $("<input>");
        descriptionAttr.attr("type", "hidden");
        descriptionAttr.attr("name", "description");
        descriptionAttr.attr("value", description);
        var fileIdAttr = $("<input>");
        fileIdAttr.attr("type", "hidden");
        fileIdAttr.attr("name", "fileId");
        fileIdAttr.attr("value", fileId);
        $("body").append(form);
        form.append(idAttr);
        form.append(descriptionAttr);
        form.append(fileIdAttr);
        form.submit();
        form.remove();
    }
    function getData() {
        var formData = _GetFormJsonMini("saleFileXiazaiForm");

        if (formData.SUB_salefileOMAGrid) {
            delete formData.SUB_salefileOMAGrid;
        }
        if (salefileOMAGrid.getChanges().length > 0) {
            formData.changeSalefileOMAGrid = salefileOMAGrid.getChanges();
        }
        return formData;
    }
    //..保存草稿
    function saveDraft(e) {
        window.parent.saveDraft(e);
    }
    //..发起流程
    function startProcess(e) {
        var formValid = validApply();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        window.parent.startProcess(e);
    }
    //..下一步审批
    function applyApprove() {
        //编制阶段的下一步需要校验表单必填字段
        if (stageName == 'start') {
            var formValid = validApply();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }
        //检查通过
        window.parent.approve();
    }
    //..
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
            saleFileXiazaiForm.setEnabled(false);
            mini.get("demandListToolBar").hide();
        }

    }
    //..
    function validApply() {
        var salefileOMAGridData = salefileOMAGrid.getData();
        if (salefileOMAGridData.length == 0) {
            return {"result": false, "message": "请添加申请下载的售前文件"};
        }
        return {"result": true};
    }
    //..暂存信息
    function saveInProcess() {
        var formData = getData();
        var json = mini.encode(formData);
        $.ajax({
            url: jsUseCtxPath + '/rdmZhgl/core/SaleFileOMAXiazaiApply/saveInProcess.do',
            type: 'post',
            async: false,
            data: json,
            contentType: 'application/json',
            success: function (data) {
                if (data) {
                    var message = "";
                    if (data.success) {
                        window.location.reload();
                    } else {
                        mini.alert("数据保存失败" + data.message);
                    }
                }
            }
        });
    }

    function onSwitchType(e) {
        var record = e.record;
        var fileType = record.fileType;
        var typeText = '';
        for (var i = 0; i < typeList.length; i++) {
            if (typeList[i].key_ == fileType) {
                typeText = typeList[i].text;
                break
            }
        }
        return typeText;
    }
    function onSwitchSystemType(e) {
        var record = e.record;
        var systemType = record.systemType;
        var typeText = '';
        for (var i = 0; i < systemTypelist.length; i++) {
            if (systemTypelist[i].key_ == systemType) {
                typeText = systemTypelist[i].text;
                break
            }
        }
        return typeText;
    }
</script>
</body>
</html>
