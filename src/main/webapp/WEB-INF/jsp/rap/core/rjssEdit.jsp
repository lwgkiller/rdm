<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>新增</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
    <style>
        html, body {
            overflow: hidden;
            height: 100%;
            width: 100%;
            padding: 0;
            margin: 0;
        }

        .table-detail > tbody > tr > td {
            border: 1px solid #eee;
            background-color: #fff;
            white-space: normal;
            word-break: break-all;
            color: rgb(85, 85, 85);
            font-weight: normal;
            padding: 4px;
            height: 40px;
            min-height: 40px;
            box-sizing: border-box;
            font-size: 15px;
        }
    </style>
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
        <form id="formRjss" method="post">
            <input id="rjssId" name="rjssId" class="mini-hidden"/>
            <input id="CREATE_BY_" name="CREATE_BY_" class="mini-hidden"/>
            <input id="instId" name="instId" class="mini-hidden"/>
            <table class="table-detail" cellspacing="1" cellpadding="0">
                <caption style="font-weight: bold;font-size: 20px !important;">
                    发动机软件变更实施申请
                </caption>
                <tr>
                    <td style="  width: 20%;text-align: center">流程编号(提交后自动生成)：</td>
                    <td style="width: 20%">
                        <input id="noticeNo" name="noticeNo" class="mini-textbox" readonly style="  width:98%;"/>
                    </td>
                </tr>
                <td style="width: 20%;text-align: center">申请人：</td>
                <td>
                    <input id="apply" name="applyId" textname="applyName"
                           property="editor" class="mini-user rxc" plugins="mini-user"
                           style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"/>
                </td>
                <td style="width: 20%;text-align: center">申请部门：</td>
                <td>
                    <input id="appDept" name="appDeptId" class="mini-dep rxc" plugins="mini-dep"
                           style="width:98%;height:34px"
                           allowinput="false" textname="appDeptName" single="true" initlogindep="false"/>
                </td>
                </tr>
                <tr>
                    <td style="width: 20%;text-align: center">发动机品牌：</td>
                    <td style="width: 20%">
                        <input id="fdjGrand" name="fdjGrand" class="mini-textbox" style="  width:98%;"/>
                    </td>
                    <td style="  width: 20%;text-align: center">发动机型号：</td>
                    <td style="width: 20%">
                        <input id="fdjModel" name="fdjModel" class="mini-textbox" style="  width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="  width: 20%;text-align: center">发动机软件版本号：</td>
                    <td style="width: 20%">
                        <input id="version" name="version" class="mini-textbox" style="  width:98%;"/>
                    </td>
                    <td style="  width: 20%;text-align: center">动力室主任：</td>
                    <td style="width: 20%">
                        <input id="dlId" name="dlId" textname="dlName"
                               property="editor" class="mini-user rxc" plugins="mini-user"
                               style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">变更内容：</td>
                    <td colspan="3">
						<textarea id="content" name="content" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:99.1%;height:150px;line-height:25px;"
                                  label="变更内容" datatype="varchar" allowinput="true"
                                  emptytext="请输入变更内容..." mwidth="80" wunit="%" mheight="200" hunit="px"></textarea>
                    </td>
                </tr>
                <tr>
                    <td style="width:15%;height: 300px;text-align: center">申请原因及目的：</td>
                    <td colspan="3">
                        <div style="margin-bottom: 2px">
                            <a id="addReason" class="mini-button" onclick="addReason()">添加原因</a>
                            <a id="removeReason" class="mini-button" onclick="removeReason()">删除原因</a>
                            <span style="color: red">注：添加前请先进行表单的保存、添加或删除后请点击保存以生效</span>
                        </div>
                        <div id="reasonListGrid" class="mini-datagrid" style="width: 100%; height: 85%"
                             allowResize="false" allowCellWrap="true"
                             idField="id" url="${ctxPath}/environment/core/Rjss/queryReason.do?belongId=${rjssId}"
                             autoload="true" allowCellEdit="true" allowCellSelect="true"
                             multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
                            <div property="columns">
                                <div type="checkcolumn" width="10"></div>
                                <div field="reason" name="reason" width="60" headerAlign="center" align="center">申请原因
                                    <input property="editor" class="mini-textbox"/></div>
                                <div field="purpose" width="60" headerAlign="center" align="center">申请目的
                                    <input property="editor" class="mini-textbox"/></div>
                            </div>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td style="width: 20%;height: 200px;text-align: center">申请人附件表：</td>
                    <td colspan="3">
                        <div style="margin-top: 2px">
                            <a style="margin-top: 2px" id="addFqrFile" class="mini-button" onclick="fileuploadFqr()">添加附件</a>
                        </div>
                        <div id="fqrFileListGrid" class="mini-datagrid" style="width: 100%; height: 80%"
                             allowResize="false"
                             idField="id" autoload="true"
                             url="${ctxPath}/environment/core/Rjss/getRjssFileList.do?fileType=fqr&rjssId=${rjssId}"
                             multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
                            <div property="columns">
                                <div type="indexcolumn" align="center" width="15">序号</div>
                                <div field="fileName" width="140" headerAlign="center" align="center">附件名称</div>
                                <div field="fileSize" width="80" headerAlign="center" align="center">附件大小</div>
                                <div field="creatorName" width="40" headerAlign="center" align="center">创建人</div>
                                <div field="action" width="100" headerAlign='center' align="center"
                                     renderer="operationRendererF">操作
                                </div>
                            </div>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td style="width:15%;height: 200px;text-align: center">设计型号及产品主管：</td>
                    <td colspan="3">
                        <div style="margin-bottom: 2px">
                            <a id="addDetail" class="mini-button" onclick="addDetail()">添加</a>
                            <a id="removeDetail" class="mini-button" onclick="removeDetail()">删除</a>
                            <span style="color: red">注：添加前请先进行表单的保存、添加或删除后请点击保存以生效</span>
                        </div>
                        <div id="detailListGrid" class="mini-datagrid" style="width: 100%; height: 85%"
                             allowResize="false" allowCellWrap="true"
                             idField="id" url="${ctxPath}/environment/core/Rjss/queryDetail.do?belongId=${rjssId}"
                             autoload="true" allowCellEdit="true" allowCellSelect="true"
                             oncellbeginedit="OnCellBeginEditDetail" multiSelect="true" showPager="false"
                             showColumnsMenu="false" allowAlternating="true">
                            <div property="columns">
                                <div type="checkcolumn" width="10"></div>
                                <div type="indexcolumn" align="center" headerAlign="center" width="20">序号</div>
                                <div field="cpzgId" displayField="cpzgName" width="30" headerAlign="center"
                                     align="center">产品主管
                                    <input property="editor" class="mini-user rxc" plugins="mini-user"
                                           style="width:90%;height:34px;" allowinput="false" length="50" maxlength="50"
                                           onvaluechanged="setCpzgDept()"
                                           mainfield="no" single="true" name="cpzgId" textname="cpzgName"/></div>
                                <div field="detailDeptName" width="40" headerAlign="center"
                                     align="center">部门
                                </div>
                                <div field="modelId" displayField="modelName" width="100" headerAlign="center"
                                     align="center">设计型号
                                    <input property="editor" style="width:98%;" class="mini-buttonedit" showClose="true"
                                           oncloseclick="onRelModelCloseClick(e)"
                                           name="modelId" textname="modelName" allowInput="false"
                                           onbuttonclick="selectModelClick()"/></div>
                                <div field="isLeader" width="40" headerAlign="center" align="center">是否需要分管领导审核
                                    <input property="editor" class="mini-combobox" style="width:98%;"
                                           textField="value" valueField="key" emptyText="请选择..."
                                           required="false" allowInput="false" showNullItem="true"
                                           nullItemText="请选择..."
                                           data="[{'key' : '是','value' : '是'},{'key' : '否','value' : '否'}]"
                                    /></div>
                            </div>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td style="  width: 20%;text-align: center">质量部专员：</td>
                    <td style="width: 20%">
                        <input id="zlId" name="zlId" textname="zlName"
                               property="editor" class="mini-user rxc" plugins="mini-user"
                               style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"/>
                    </td>
                    <td style="  width: 20%;text-align: center">控制工程师：</td>
                    <td style="width: 20%">
                        <input id="kzId" name="kzId" textname="kzName"
                               property="editor" class="mini-user rxc" plugins="mini-user"
                               style="width:98%;height:34px;" allowinput="false" mainfield="no" single="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="  width: 20%;text-align: center">液压工程师：</td>
                    <td style="width: 20%">
                        <input id="yyId" name="yyId" textname="yyName"
                               property="editor" class="mini-user rxc" plugins="mini-user"
                               style="width:98%;height:34px;" allowinput="false" mainfield="no" single="false"/>
                    </td>
                    <td style="  width: 20%;text-align: center">电气工程师：</td>
                    <td style="width: 20%">
                        <input id="dqId" name="dqId" textname="dqName"
                               property="editor" class="mini-user rxc" plugins="mini-user"
                               style="width:98%;height:34px;" allowinput="false" mainfield="no" single="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 20%;height: 300px;text-align: center">产品主管附件表：</td>
                    <td colspan="3">
                        <div>
                            <a id="addCpzgFile" class="mini-button" onclick="fileuploadCpzg()">添加附件</a>
                        </div>
                        <div id="cpzgFileListGrid" class="mini-datagrid" style="width: 100%; height: 80%"
                             allowResize="false"
                             idField="id" autoload="true"
                             url="${ctxPath}/environment/core/Rjss/getRjssFileList.do?fileType=cpzg&rjssId=${rjssId}"
                             multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
                            <div property="columns">
                                <div type="indexcolumn" headerAlign="center" align="center" width="20">序号</div>
                                <div field="fileName" width="140" headerAlign="center" align="center">附件名称</div>
                                <div field="fileSize" width="80" headerAlign="center" align="center">附件大小</div>
                                <div field="creatorName" width="40" headerAlign="center" align="center">创建人</div>
                                <div field="action" width="100" headerAlign='center' align="center"
                                     renderer="operationRendererF">操作
                                </div>
                            </div>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td style="width: 20%;height: 300px;text-align: center">会签工程师附件表：</td>
                    <td colspan="3">
                        <div>
                            <a id="addSrzFile" class="mini-button" onclick="fileuploadSrz()">添加附件</a>
                        </div>
                        <div id="srzFileListGrid" class="mini-datagrid" style="width: 100%; height: 80%"
                             allowResize="false"
                             idField="id" autoload="true"
                             url="${ctxPath}/environment/core/Rjss/getRjssFileList.do?fileType=Srz&rjssId=${rjssId}"
                             multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
                            <div property="columns">
                                <div type="indexcolumn" headerAlign="center" align="center" width="20">序号</div>
                                <div field="fileName" width="140" headerAlign="center" align="center">附件名称</div>
                                <div field="fileSize" width="80" headerAlign="center" align="center">附件大小</div>
                                <div field="creatorName" width="40" headerAlign="center" align="center">创建人</div>
                                <div field="action" width="100" headerAlign='center' align="center"
                                     renderer="operationRendererF">操作
                                </div>
                            </div>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td style="  width: 20%;text-align: center">服务部专员：</td>
                    <td style="width: 20%">
                        <input id="fwId" name="fwId" textname="fwName"
                               property="editor" class="mini-user rxc" plugins="mini-user"
                               style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"/>
                    </td>
                    <td style="  width: 20%;text-align: center">制造部专员：</td>
                    <td style="width: 20%">
                        <input id="zzId" name="zzId" textname="zzName"
                               property="editor" class="mini-user rxc" plugins="mini-user"
                               style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 20%;height: 200px;text-align: center">质量保证部附件表：</td>
                    <td colspan="3">
                        <div>
                            <a id="addZlFile" class="mini-button" onclick="fileuploadZl()">添加附件</a>
                        </div>
                        <div id="zlFileListGrid" class="mini-datagrid" style="width: 100%; height: 80%"
                             allowResize="false"
                             idField="id" autoload="true"
                             url="${ctxPath}/environment/core/Rjss/getRjssFileList.do?fileType=Zl&rjssId=${rjssId}"
                             multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
                            <div property="columns">
                                <div type="indexcolumn" headerAlign="center" align="center" width="20">序号</div>
                                <div field="fileName" width="140" headerAlign="center" align="center">附件名称</div>
                                <div field="fileSize" width="80" headerAlign="center" align="center">附件大小</div>
                                <div field="creatorName" width="40" headerAlign="center" align="center">创建人</div>
                                <div field="action" width="100" headerAlign='center' align="center"
                                     renderer="operationRendererF">操作
                                </div>
                            </div>
                        </div>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
<div id="selectModelWindow" title="选择设计型号" class="mini-window" style="width:900px;height:630px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="true">
    <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px"
         borderStyle="border-left:0;border-top:0;border-right:0;">
        <span class="text" style="width:auto">设计型号: </span><input
            class="mini-textbox" style="width: 120px" id="designModel" name="designModel"/>
        <span class="text" style="width:auto">销售型号: </span><input
            class="mini-textbox" style="width: 120px" id="saleModel" name="saleModel"/>
        <span class="text" style="width:auto">产品所: </span><input
            class="mini-textbox" style="width: 120px" id="departName" name="departName"/>
        <span class="text" style="width:auto">产品主管: </span><input
            class="mini-textbox" style="width: 120px" id="productManagerName" name="productManagerName"/>
        <a class="mini-button" iconCls="icon-search" plain="true" onclick="searchModel()">查询</a>
    </div>
    <div class="mini-fit">
        <div id="selectModelListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
             idField="id" showColumnsMenu="false" multiSelect="true"
             allowAlternating="true" showPager="true" onload="onSelectModelGridLoad"
             url="${ctxPath}/world/core/productSpectrum/dataListQuery.do?">
            <div property="columns">
                <div type="checkcolumn" width="20"></div>
                <div field="designModel" width="150" headerAlign="center" align="center" renderer="render"
                     allowSort="true">
                    设计型号
                </div>
                <div field="departName" width="120" headerAlign="center" align="center" renderer="render"
                     allowSort="true">
                    产品所
                </div>
                <div field="productManagerName" width="80" headerAlign="center" align="center" renderer="render"
                     allowSort="true">
                    产品主管
                </div>
                <div field="saleModel" width="120" headerAlign="center" align="center" renderer="render"
                     allowSort="true">
                    销售型号
                </div>
            </div>
        </div>
    </div>
    <div property="footer" style="padding:5px;height: 35px">
        <table style="width:100%;height: 100%">
            <tr>
                <td style="width:120px;text-align:center;">
                    <input type="button" style="height: 25px;width: 70px" value="确定" onclick="selectModelOK()"/>
                    <input type="button" style="height: 25px;width: 70px" value="取消" onclick="selectModelHide()"/>
                </td>
            </tr>
        </table>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var status = "${status}";
    var action = "${action}";
    var jsUseCtxPath = "${ctxPath}";
    var rjssId = "${rjssId}";
    var formRjss = new mini.Form("#formRjss");
    var fqrFileListGrid = mini.get("fqrFileListGrid");
    var cpzgFileListGrid = mini.get("cpzgFileListGrid");
    var srzFileListGrid = mini.get("srzFileListGrid");
    var zlFileListGrid = mini.get("zlFileListGrid");
    var reasonListGrid = mini.get("reasonListGrid");
    var detailListGrid = mini.get("detailListGrid");
    var selectModelWindow = mini.get("selectModelWindow");
    var selectModelListGrid = mini.get("selectModelListGrid");
    var currentUserName = "${currentUserName}";
    var nodeVarsStr = '${nodeVars}';
    var currentUserId = "${currentUserId}";
    var currentTime = "${currentTime}";
    var deptName = "${deptName}";
    var currentUserMainDepId = "${currentUserMainDepId}";
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";
    var selectRowIds = [];
    var selectRowNames = [];

    function operationRendererF(e) {
        var record = e.record;
        var cellHtml = '';
        cellHtml = returnRjssPreviewSpan(record.fileName, record.fileId, record.belongId, coverContent);
        cellHtml += '&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downLoadRjssFile(\'' + record.fileName + '\',\'' + record.fileId + '\',\'' + record.belongId + '\')">下载</span>';
        //增加删除按钮
        if (record.CREATE_BY_ == currentUserId && action!="detail") {
            var deleteUrl = "/environment/core/Rjss/deleteRjssFile.do"
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteSmFile(\'' + record.fileName + '\',\'' + record.fileId + '\',\'' + record.belongId + '\',\'' + deleteUrl + '\')">删除</span>';
        }
        return cellHtml;
    }


    var stageName = "";
    $(function () {
        if (rjssId) {
            var url = jsUseCtxPath + "/environment/core/Rjss/getRjssDetail.do";
            $.post(
                url,
                {rjssId: rjssId},
                function (json) {
                    formRjss.setData(json);
                });
        } else {
            mini.get("apply").setValue(currentUserId);
            mini.get("apply").setText(currentUserName);
            mini.get("appDept").setValue(currentUserMainDepId);
            mini.get("appDept").setText(deptName);
        }
        //变更入口
        if (action == 'task') {
            taskActionProcess();
        } else if (action == "detail") {
            formRjss.setEnabled(false);
            reasonListGrid.setAllowCellEdit(false);
            mini.get("addReason").setEnabled(false);
            mini.get("removeReason").setEnabled(false);
            mini.get("addFqrFile").setEnabled(false);
            mini.get("addCpzgFile").setEnabled(false);
            mini.get("addSrzFile").setEnabled(false);
            mini.get("addZlFile").setEnabled(false);
            mini.get("addDetail").setEnabled(false);
            mini.get("removeDetail").setEnabled(false);
            $("#detailToolBar").show();
            if (status != 'DRAFTED') {
                $("#processInfo").show();
            }
        } else if (action == "edit") {
            mini.get("zlId").setEnabled(false);
            mini.get("kzId").setEnabled(false);
            mini.get("dqId").setEnabled(false);
            mini.get("yyId").setEnabled(false);
            mini.get("fwId").setEnabled(false);
            mini.get("zzId").setEnabled(false);
            mini.get("addCpzgFile").setEnabled(false);
            mini.get("addSrzFile").setEnabled(false);
            mini.get("addZlFile").setEnabled(false);
            mini.get("addDetail").setEnabled(false);
            mini.get("removeDetail").setEnabled(false);
        }
    });

    function getData() {
        var formData = _GetFormJsonMini("formRjss");
        formData.reason = reasonListGrid.getChanges();
        formData.detail = detailListGrid.getChanges();
        formData.detailAll = detailListGrid.getData();
        return formData;
    }

    function saveRjss(e) {
        // var formValid = validFirst();
        // if (!formValid.result) {
        //     mini.alert(formValid.message);
        //     return;
        // }
        window.parent.saveDraft(e);
    }

    function saveChange(rjssId) {
        var formData = _GetFormJsonMini("formRjss");
        formData.reason = reasonListGrid.getChanges();
        formData.detail = detailListGrid.getChanges();
        formData.detailAll = detailListGrid.getData();
        $.ajax({
            url: jsUseCtxPath + '/environment/core/Rjss/saveRjss.do?rjssId=' + rjssId,
            type: 'post',
            async: false,
            data: mini.encode(formData),
            contentType: 'application/json',
            success: function (data) {
                if (data) {
                    var message = "";
                    if (data.success) {
                        message = "数据变更成功";
                    } else {
                        message = "数据变更失败" + data.message;
                    }

                    mini.showMessageBox({
                        title: "提示信息",
                        iconCls: "mini-messagebox-info",
                        buttons: ["ok"],
                        message: message,
                        callback: function (action) {
                            if (action == "ok") {
                                window.location.reload();
                            }
                        }
                    });
                }
            }
        });
    }


    function startRjssProcess(e) {
        var formValid = validFirst();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        window.parent.startProcess(e);
    }

    function validFirst() {
        var apply = $.trim(mini.get("apply").getValue());
        if (!apply) {
            return {"result": false, "message": "请填写申请人"};
        }
        var appDept = $.trim(mini.get("appDept").getValue());
        if (!appDept) {
            return {"result": false, "message": "请填写申请部门"};
        }
        var fdjGrand = $.trim(mini.get("fdjGrand").getValue());
        if (!fdjGrand) {
            return {"result": false, "message": "请填写发动机品牌"};
        }
        var fdjModel = $.trim(mini.get("fdjModel").getValue());
        if (!fdjModel) {
            return {"result": false, "message": "请填写发动机型号"};
        }
        var version = $.trim(mini.get("version").getValue());
        if (!version) {
            return {"result": false, "message": "请填写发动机软件版本号"};
        }
        var dlId = $.trim(mini.get("dlId").getValue());
        if (!dlId) {
            return {"result": false, "message": "请选择动力室主任"};
        }
        var content = $.trim(mini.get("content").getValue());
        if (!content) {
            return {"result": false, "message": "请填写变更内容"};
        }

        var reason = reasonListGrid.getData();
        if (reason.length < 1) {
            return {"result": false, "message": "请添加申请原因及目的"};
        } else {
            for (var i = 0; i < reason.length; i++) {
                if (reason[i].reason == undefined || reason[i].reason == "") {
                    return {"result": false, "message": "请填写申请原因"};
                }
                if (reason[i].purpose == undefined || reason[i].purpose == "") {
                    return {"result": false, "message": "请填写申请目的"};
                }
            }
        }
        var fqrFileListGrid = $.trim(mini.get("fqrFileListGrid").getData());
        if (!fqrFileListGrid) {
            return {"result": false, "message": "请上传发动机号实施明细"};
        }
        return {"result": true};
    }

    function validSecond() {
        var detail = detailListGrid.getData();
        if (detail.length < 1) {
            return {"result": false, "message": "请选择产品主管"};
        } else {
            for (var i = 0; i < detail.length; i++) {
                if (detail[i].cpzgId == undefined || detail[i].cpzgId == "") {
                    return {"result": false, "message": "请选择产品主管"};
                }
            }
        }
        return {"result": true};
    }

    function validThird() {
        var detail = detailListGrid.getData();
        for (var i = 0; i < detail.length; i++) {
            if ((detail[i].modelId == undefined || detail[i].modelId == "") && detail[i].cpzgId == currentUserId) {
                return {"result": false, "message": "请选择设计型号"};
            }
        }
        return {"result": true};
    }

    function validForth() {
        var detail = detailListGrid.getData();
        for (var i = 0; i < detail.length; i++) {
            if ((detail[i].isLeader == undefined || detail[i].isLeader == "")&& detail[i].detailDeptName == deptName) {
                return {"result": false, "message": "请选择是否需要分管领导审批"};
            }
        }
        return {"result": true};
    }

    function validFifth() {
        var zlId = $.trim(mini.get("zlId").getValue());
        if (!zlId) {
            return {"result": false, "message": "请选择质量部专员"};
        }
        var dqId = $.trim(mini.get("dqId").getValue());
        if (!dqId) {
            return {"result": false, "message": "请选择电气工程师"};
        }
        var kzId = $.trim(mini.get("kzId").getValue());
        if (!kzId) {
            return {"result": false, "message": "请选择控制工程师"};
        }
        var yyId = $.trim(mini.get("yyId").getValue());
        if (!yyId) {
            return {"result": false, "message": "请选择液压工程师"};
        }
        var isUpload = false;
        var cpzgFileList = cpzgFileListGrid.getData();
        for (var i = 0; i < cpzgFileList.length; i++) {
            if (cpzgFileList[i].CREATE_BY_ == currentUserId) {
                isUpload = true;
                break;
            }
        }
        if (!isUpload) {
            return {"result": false, "message": "请上传发动机软件测试报告或发动机软件开发流程号"};
        }
        return {"result": true};
    }

    function validSeventh() {
        var fwId = $.trim(mini.get("fwId").getValue());
        if (!fwId) {
            return {"result": false, "message": "请选择服务部专员"};
        }
        var zzId = $.trim(mini.get("zzId").getValue());
        if (!zzId) {
            return {"result": false, "message": "请选择制造部专员"};
        }
        var zlFileListGrid = $.trim(mini.get("zlFileListGrid").getData());
        if (!zlFileListGrid) {
            return {"result": false, "message": "请上传整机整改明细表"};
        }
        return {"result": true};
    }

    function rjssApprove() {
        //编制阶段的下一步需要校验表单必填字段
        if (stageName == 'first') {
            var formValid = validFirst();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }
        if (stageName == 'second') {
            var formValid = validSecond();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }
        if (stageName == 'third') {
            var formValid = validThird();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }
        if (stageName == 'forth') {
            var formValid = validForth();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }
        if (stageName == 'fifth') {
            var formValid = validFifth();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }
        if (stageName == 'seventh') {
            var formValid = validSeventh();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }
        //检查通过
        window.parent.approve();
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


    function fileuploadCpzg() {
        var rjssId = mini.get("rjssId").getValue();
        if (!rjssId) {
            mini.alert('请先点击‘保存草稿’进行表单的保存！');
            return;
        }
        mini.open({
            title: "添加附件",
            url: jsUseCtxPath + "/environment/core/Rjss/openUploadWindow.do?fileType=cpzg&rjssId=" + rjssId,
            width: 850,
            height: 550,
            showModal: true,
            allowResize: true,
            ondestroy: function () {
                if (cpzgFileListGrid) {
                    cpzgFileListGrid.load();
                }
            }
        });
    }


    function fileuploadFqr() {
        var rjssId = mini.get("rjssId").getValue();
        if (!rjssId) {
            mini.alert('请先点击‘保存草稿’进行表单的保存！');
            return;
        }
        mini.open({
            title: "添加附件",
            url: jsUseCtxPath + "/environment/core/Rjss/openUploadWindow.do?fileType=fqr&rjssId=" + rjssId,
            width: 850,
            height: 550,
            showModal: true,
            allowResize: true,
            ondestroy: function () {
                if (fqrFileListGrid) {
                    fqrFileListGrid.load();
                }
            }
        });
    }

    function fileuploadSrz() {
        var rjssId = mini.get("rjssId").getValue();
        if (!rjssId) {
            mini.alert('请先点击‘保存草稿’进行表单的保存！');
            return;
        }
        mini.open({
            title: "添加附件",
            url: jsUseCtxPath + "/environment/core/Rjss/openUploadWindow.do?fileType=srz&rjssId=" + rjssId,
            width: 850,
            height: 550,
            showModal: true,
            allowResize: true,
            ondestroy: function () {
                if (srzFileListGrid) {
                    srzFileListGrid.load();
                }
            }
        });
    }

    function fileuploadZl() {
        var rjssId = mini.get("rjssId").getValue();
        if (!rjssId) {
            mini.alert('请先点击‘保存草稿’进行表单的保存！');
            return;
        }
        mini.open({
            title: "添加附件",
            url: jsUseCtxPath + "/environment/core/Rjss/openUploadWindow.do?fileType=zl&rjssId=" + rjssId,
            width: 850,
            height: 550,
            showModal: true,
            allowResize: true,
            ondestroy: function () {
                if (zlFileListGrid) {
                    zlFileListGrid.load();
                }
            }
        });
    }

    function taskActionProcess() {
        //获取上一环节的结果和处理人
        bpmPreTaskTipInForm();

        //获取环境变量
        if (!nodeVarsStr) {
            nodeVarsStr = "[]";
        }
        formRjss.setEnabled(false);
        reasonListGrid.setAllowCellEdit(false);
        detailListGrid.setAllowCellEdit(false);
        mini.get("addCpzgFile").setEnabled(false);
        mini.get("addFqrFile").setEnabled(false);
        mini.get("addSrzFile").setEnabled(false);
        mini.get("addZlFile").setEnabled(false);
        mini.get("addReason").setEnabled(false);
        mini.get("removeReason").setEnabled(false);
        mini.get("addDetail").setEnabled(false);
        mini.get("removeDetail").setEnabled(false);
        var nodeVars = $.parseJSON(nodeVarsStr);
        for (var i = 0; i < nodeVars.length; i++) {
            if (nodeVars[i].KEY_ == 'stageName') {
                stageName = nodeVars[i].DEF_VAL_;
            }
        }
        if (stageName == 'first') {
            mini.get("addFqrFile").setEnabled(true);
            reasonListGrid.setAllowCellEdit(true);
            formRjss.setEnabled(true);
            mini.get("zlId").setEnabled(false);
            mini.get("kzId").setEnabled(false);
            mini.get("dqId").setEnabled(false);
            mini.get("yyId").setEnabled(false);
            mini.get("fwId").setEnabled(false);
            mini.get("zzId").setEnabled(false);
            mini.get("addReason").setEnabled(true);
            mini.get("removeReason").setEnabled(true);
        }

        if (stageName == 'second') {
            detailListGrid.setAllowCellEdit(true);
            mini.get("addDetail").setEnabled(true);
            mini.get("removeDetail").setEnabled(true);
        }
        if (stageName == 'third') {
            detailListGrid.setAllowCellEdit(true);
        }
        if (stageName == 'forth') {
            detailListGrid.setAllowCellEdit(true);
        }
        if (stageName == 'fifth') {
            mini.get("addCpzgFile").setEnabled(true);
            mini.get("zlId").setEnabled(true);
            mini.get("kzId").setEnabled(true);
            mini.get("dqId").setEnabled(true);
            mini.get("yyId").setEnabled(true);
        }
        if (stageName == 'sixth') {
            mini.get("addSrzFile").setEnabled(true);
        }
        if (stageName == 'seventh') {
            mini.get("fwId").setEnabled(true);
            mini.get("zzId").setEnabled(true);
            mini.get("addZlFile").setEnabled(true);
        }
    }


    function returnRjssPreviewSpan(fileName, fileId, formId, coverContent) {
        var fileType = getFileType(fileName);
        var s = '';
        if (fileType == 'other') {
            s = '<span  title="预览" style="color: silver" >预览</span>';
        } else if (fileType == 'pdf') {
            var url = '/environment/core/Rjss/rjssPdfPreview';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        } else if (fileType == 'office') {
            var url = '/environment/core/Rjss/rjssOfficePreview';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        } else if (fileType == 'pic') {
            var url = '/environment/core/Rjss/rjssImagePreview';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        }
        return s;
    }

    function downLoadRjssFile(fileName, fileId, formId) {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + '/environment/core/Rjss/rjssPdfPreview.do?action=download');
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


    function deleteSmFile(fileName, fileId, formId, urlValue) {
        mini.confirm("确定删除？", "确定？",
            function (action) {
                if (action == "ok") {
                    var url = jsUseCtxPath + urlValue;
                    var data = {
                        fileName: fileName,
                        id: fileId,
                        formId: formId
                    };
                    $.ajax({
                        url: url,
                        method: 'post',
                        contentType: 'application/json',
                        data: mini.encode(data),
                        success: function (json) {
                            if (fqrFileListGrid) {
                                fqrFileListGrid.load();
                            }
                            if (cpzgFileListGrid) {
                                cpzgFileListGrid.load();
                            }
                            if (zlFileListGrid) {
                                zlFileListGrid.load();
                            }
                            if (srzFileListGrid) {
                                srzFileListGrid.load();
                            }
                        }
                    });
                }
            }
        );
    }

    function addReason() {
        var formId = mini.get("rjssId").getValue();
        if (!formId) {
            mini.alert("请先点击‘保存草稿’进行表单创建!");
            return;
        } else {
            var row = {};
            reasonListGrid.addRow(row);
        }
    }

    function removeReason() {
        var selecteds = reasonListGrid.getSelecteds();
        if (selecteds.length <= 0) {
            mini.alert("请选择一条记录");
            return;
        }
        var deleteArr = [];
        for (var i = 0; i < selecteds.length; i++) {
            var row = selecteds[i];
            deleteArr.push(row);
        }
        reasonListGrid.removeRows(deleteArr);
    }

    function addDetail() {
        var formId = mini.get("rjssId").getValue();
        if (!formId) {
            mini.alert("请先点击‘保存草稿’进行表单创建!");
            return;
        } else {
            var row = {modelId:'',modelName:''};
            detailListGrid.addRow(row);
        }
    }

    function removeDetail() {
        var selecteds = detailListGrid.getSelecteds();
        if (selecteds.length <= 0) {
            mini.alert("请选择一条记录");
            return;
        }
        var deleteArr = [];
        for (var i = 0; i < selecteds.length; i++) {
            var row = selecteds[i];
            deleteArr.push(row);
        }
        detailListGrid.removeRows(deleteArr);
    }

    function selectModelClick() {
        var rec = detailListGrid.getSelected();
        var modelId = rec.modelId;
        var modelName = rec.modelName;
            selectRowIds = modelId.split(',');
            selectRowNames = modelName.split(',');
        selectModelWindow.show();
        searchModel();
    }

    function searchModel() {
        var queryParam = [];
        //其他筛选条件
        var designModel = $.trim(mini.get("designModel").getValue());
        if (designModel) {
            queryParam.push({name: "designModel", value: designModel});
        }
        var departName = $.trim(mini.get("departName").getValue());
        if (departName) {
            queryParam.push({name: "departName", value: departName});
        }
        var productManagerName = $.trim(mini.get("productManagerName").getValue());
        if (productManagerName) {
            queryParam.push({name: "productManagerName", value: productManagerName});
        }
        var saleModel = $.trim(mini.get("saleModel").getValue());
        if (saleModel) {
            queryParam.push({name: "saleModel", value: saleModel});
        }
        var data = {};
        data.filter = mini.encode(queryParam);
        //查询
        selectModelListGrid.load(data);
    }

    function selectModelOK() {
        var row = detailListGrid.getSelected();
        if (selectRowIds) {
            detailListGrid.updateRow(row, {modelId: selectRowIds.toString(), modelName: selectRowNames.toString()});
        }
        selectModelHide();
    }

    function selectModelHide() {
        selectModelWindow.hide();
        mini.get("saleModel").setValue('');
        mini.get("productManagerName").setValue('');
        mini.get("departName").setValue('');
        mini.get("designModel").setValue('');
        selectRowIds.splice(0, selectRowIds.length);
        selectRowNames.splice(0, selectRowNames.length);
        selectModelListGrid.clearSelect();
    }

    function onRelModelCloseClick(e) {
        var obj = e.sender;
        obj.setValue("");
        obj.setText("");
        var row = detailListGrid.getSelected();
        detailListGrid.updateRow(row, {modelId: '', modelName: ''});
        selectRowIds.splice(0, selectRowIds.length);
        selectRowNames.splice(0, selectRowNames.length);
        selectModelListGrid.clearSelect();
    }

    function onSelectModelGridLoad(e) {
        if (selectRowIds.length > 0) {
            for (var i = 0; i < selectModelListGrid.data.length; i++) {
                if (selectRowIds.indexOf(selectModelListGrid.data[i].id) != -1) {
                    selectModelListGrid.setSelected(selectModelListGrid.getRow(i));
                }
            }
        }
    }


    selectModelListGrid.on("select", function (e) {
        var rec = e.record;
        var designModel = rec.designModel;
        var id = rec.id;
        if (selectRowIds.indexOf(id) == -1) {
            selectRowIds.push(id);
            selectRowNames.push(designModel);
        }
    });

    selectModelListGrid.on("deselect", function (e) {
        var rec = e.record;
        var designModel = rec.designModel;
        var id = rec.id;
        delItem(designModel, selectRowNames);
        delItem(id, selectRowIds);
    });

    function delItem(item, list) {
        // 表示先获取这个元素的下标，然后从这个下标开始计算，删除长度为1的元素
        list.splice(list.indexOf(item), 1)
    }


    function OnCellBeginEditDetail(e) {
        var record = e.record, field = e.field;
        var cpzgId = record.cpzgId;
        var gridName = record.detailDeptName;
        e.cancel = true;
        if (field == "cpzgId" && stageName == "second") {
            e.cancel = false;
        }
        if (field == "modelId" && stageName == "third" && (cpzgId == currentUserId || currentUserId == '1')) {
            e.cancel = false;
        }
        if (field == "isLeader" && stageName == "forth" && (gridName == deptName || currentUserId == '1')) {
            e.cancel = false;
        }
    }


    detailListGrid.on("cellcommitedit", function (e) {
        var record = e.record;
        if (e.field == "cpzgId") {
            var cpzgId = e.value;
            var grid = e.sender;
            var userInfo = getUserInfoById(cpzgId);
            grid.updateRow(record, {detailDeptName: userInfo.mainDepName});
        }
    });

    function getUserInfoById(userId) {
        var userInfo = "";
        $.ajax({
            url: jsUseCtxPath + '/xcmgProjectManager/core/xcmgProject/' + userId + '/getUserInfoById.do',
            type: 'get',
            async: false,
            contentType: 'application/json',
            success: function (data) {
                userInfo = data;
            }
        });
        return userInfo;
    }
</script>
</body>
</html>