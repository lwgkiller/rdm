<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html >
<html>
<head>
    <title>管理改进建议提报表单</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/zlgjNPI/manageImproveEdit.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}" type="text/javascript"></script>
    <style type="text/css">
        fieldset {
            border: solid 1px #aaaaaab3;
            min-width: 920px;
        }

        .hideFieldset {
            border-left: 0;
            border-right: 0;
            border-bottom: 0;
        }

        .hideFieldset .fieldset-body {
            display: none;
        }
    </style>
</head>
<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="processInfo" class="mini-button" style="display: none" onclick="manageImproveProcessInfo()">流程信息</a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin:0 auto; width: 100%;height:98%;padding: 0">
        <form id="manageImproveForm" method="post" style="height: 95%;width: 100%">
            <input id="id" name="id" class="mini-hidden"/>
            <input id="suggestionApplynum" name="suggestionApplynum" class="mini-hidden"/>
            <input id="instId" name="instId" class="mini-hidden"/>
            <input id="step" name="step" class="mini-hidden"/>
            <fieldset id="wttb">
                <legend>
                    <label style="font-size:17px">
                        <input type="checkbox" checked id="cpxhCheckbox" onclick="toggleManageImproveFieldset(this, 'wttb')" hideFocus/>
                        问题提报
                    </label>
                </legend>
                <div class="fieldset-body" style="margin: 10px 10px 10px 10px">
                    <table class="table-detail grey" cellspacing="1" cellpadding="0">

                        <tr>
                            <td style="text-align: center;width: 17%">提报人：</td>
                            <td style="width: 33%;">
                                <input id="CREATE_BY_" name="CREATE_BY_" textname="creator" class="mini-user rxc"
                                       plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="提报人" length="50"
                                       mainfield="no" single="true"/>
                            </td>
                            <td style="text-align: center;width: 17%">提报部门：</td>
                            <td style="width: 33%;">
                                <input id="applicationUnitId" name="applicationUnitId" class="mini-dep rxc" plugins="mini-dep"
                                       style="width:98%;height:34px"
                                       allowinput="false" textname="applicationUnit" length="200" maxlength="200" minlen="0" single="true"
                                       initlogindep="false"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="text-align: center;width: 17%">核心流程：</td>
                            <td style="width: 33%;">
                                <input id="coreTypeId" name="coreTypeId" class="mini-combobox" style="width:98%;"
                                       textField="value"
                                       valueField="key" emptyText="请选择..."
                                       data="[ {'key' : '设计门径管理','value' : '设计门径管理'},{'key' : '工艺管理','value' : '工艺管理'},
                                               {'key' : '变更管理','value' : '变更管理'},{'key' : '供应商开发评价管理','value' : '供应商开发评价管理'},
                                               {'key' : '供应商过程＆质量管理','value' : '供应商过程＆质量管理'},{'key' : '生产过程管控','value' : '生产过程管控'},
                                               {'key' : '客户/订单管理','value' : '客户/订单管理'},{'key' : '生产过程检查控制','value' : '生产过程检查控制'},
                                               {'key' : '产品发运交付控制','value' : '产品发运交付控制'},{'key' : '其他问题','value' : '其他问题'},]"
                                       required="true" allowInput="false" showNullItem="false" nullItemText="请选择..."
                                       onvaluechanged="coreTypeValueChanged()"
                                />
                            </td>
                            <td style="text-align: center;width: 17%">子流程：</td>
                            <td style="width: 33%;">
                                <input id="subTypeId" name="subTypeId" class="mini-combobox" style="width:98%;"
                                       textField="subTypeName" valueField="subTypeName"  emptyText="请选择..."
                                       allowInput="false" showNullItem="false" nullItemText="请选择..."

                                />
                            </td>
                        </tr>

                        <tr>
                            <td style="text-align: center;width: 17%">业务需求：</td>
                            <td style="width: 33%;">
                                <input id="businessRequest" name="businessRequest" class="mini-checkboxlist" multiSelect="false" style="width:98%"
                                       textField="value" valueField="key" emptyText="请选择..."
                                       data="[{key:'上游输入需求',value:'上游输入需求'}
                                       ,{key:'业务支持',value:'业务支持'}
                                       ,{key:'顾客需求',value:'顾客需求'}]"
                                />
                            </td>
                            <td style="text-align: center;width: 17%">问题类型：</td>
                            <td style="width: 33%;">
                                <input id="problemType" name="problemType" class="mini-checkboxlist" multiSelect="false" style="width:98%"
                                       textField="value" valueField="key" emptyText="请选择..."
                                       data="[{key:'效率',value:'效率'}
                                       ,{key:'符合率',value:'符合率'}]"
                                />
                            </td>
                        </tr>
                        <tr>
                            <td style="text-align: center;width: 17%">具体问题描述：</td>
                            <td style="width: 33%;" >
                                <textarea id="problemDescription" name="problemDescription" class="mini-textarea rxc" plugins="mini-textarea"
                                          style="width:98%;;height:150px;line-height:25px;" label="" datatype="varchar" length="1000"
                                          vtype="length:1000" minlen="0" allowinput="true"
                                          emptytext="请输入问题描述" mwidth="80" wunit="%" mheight="150" hunit="px"></textarea>
                            </td>
                            <td style="text-align: center;width: 17%">改进建议：</td>
                            <td style="width: 33%;" >
                                <textarea id="improveSuggestion" name="improveSuggestion" class="mini-textarea rxc" plugins="mini-textarea"
                                          style="width:98%;;height:150px;line-height:25px;" label="" datatype="varchar" length="1000"
                                          vtype="length:1000" minlen="0" allowinput="true"
                                          emptytext="请输入改进建议" mwidth="80" wunit="%" mheight="150" hunit="px"></textarea>
                            </td>
                        </tr>
                        <tr>
                            <td style="text-align: center;width: 17%">选择建议对策实施部门：</td>
                            <td>
                                <input id="improveDepartmentId" name="improveDepartmentId" class="mini-dep rxc" plugins="mini-dep"
                                       style="width:98%;height:34px"
                                       allowinput="false" textname="improveDepartment" length="200" maxlength="200" minlen="0" single="true"
                                       initlogindep="false"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="text-align: center;width: 17%;height: 300px">问题提报材料：</td>
                            <td colspan="3">
                                <div style="margin-top: 10px;margin-bottom: 2px">
                                    <a id="wtAddFile" class="mini-button" onclick="wtAddFile()">添加附件</a>
                                    <span style="color: red">注：添加附件前，请先进行草稿的保存</span>
                                </div>
                                <div id="wtFileListGrid" class="mini-datagrid" style="width: 100%; height: 85%" allowResize="false"
                                     idField="id" url="${ctxPath}/zlgjNPI/core/manageImprove/queryManageImproveFileList.do?filetype=wenti&belongId=${manageImproveId}"
                                     autoload="true"
                                     multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
                                    <div property="columns">
                                        <div type="indexcolumn" align="center" width="20">序号</div>
                                        <div field="fileName" width="140" headerAlign="center" align="center">附件名称</div>
                                        <div field="fileSize" width="80" headerAlign="center" align="center">附件大小</div>
                                        <div field="creator" width="100" headerAlign="center" align="center">上传者</div>
                                        <div field="CREATE_TIME_" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" headerAlign="center">上传时间</div>
                                        <div field="action" width="100" headerAlign='center' align="center" renderer="wtoperationRenderer">操作</div>
                                    </div>
                                </div>
                            </td>
                        </tr>
                    </table>
                </div>
            </fieldset>
            <br>
            <fieldset id="dcbmss">
                <legend>
                    <label style="font-size:17px">
                        <input type="checkbox" checked id="dcbmssCheckbox" onclick="toggleManageImproveFieldset(this, 'dcbmss')" hideFocus/>
                        对策部门实施
                    </label>
                </legend>
                <div class="fieldset-body" style="margin: 10px 10px 10px 10px">
                    <table class="table-detail grey" cellspacing="1" cellpadding="0">
                        <tr>
                            <td style="text-align: center;width: 17%">是否采纳：</td>
                            <td style="width: 33%">
                                <input id="canOrNot" name="canOrNot" class="mini-checkboxlist" multiSelect="false" style="width:98%"
                                       textField="value" valueField="key" emptyText="请选择..." onvaluechanged="ifNot"
                                       data="[{key:'采纳',value:'采纳'}
                                       ,{key:'不采纳',value:'不采纳'}]"
                                />
                            </td>

                            <td style="text-align: center;width: 17%">不采纳原因：</td>
                            <td style="width: 33%;" >
                                <textarea id="noReason" name="noReason" class="mini-textarea rxc" plugins="mini-textarea"
                                          style="width:98%;height:150px;line-height:25px;" label="" datatype="varchar" length="200"
                                          vtype="length:200" minlen="0" allowinput="true"
                                          emptytext="请输入不采纳原因" mwidth="80" wunit="%" mheight="150" hunit="px"></textarea>
                            </td>

                        </tr>
                        <tr>
                            <td style="text-align: center;width: 17%">选择责任人：</td>
                            <td style="width: 33%;">
                                <input id="responseManId" name="responseManId" textname="responsManName" class="mini-user rxc"
                                       plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="责任人" length="50"
                                       mainfield="no" single="true"/>
                            </td>

                            <td style="text-align: center;width: 17%">改进对策：</td>
                            <td style="width: 33%;">
                                <textarea id="improveMethod" name="improveMethod" class="mini-textarea rxc" plugins="mini-textarea"
                                          style="width:98%;;height:150px;line-height:25px;" label="" datatype="varchar" length="1000"
                                          vtype="length:1000" minlen="0" allowinput="true"
                                          emptytext="请输入改进对策" mwidth="80" wunit="%" mheight="150" hunit="px"></textarea>
                            </td>
                        </tr>
                        <tr>
                            <td style="text-align: center;width: 17%">计划完成时间：</td>
                            <td>
                                <input id="planFinishTime" name="planFinishTime" class="mini-datepicker" allowInput="false" style="width:98%;"/>
                            </td>

                            <td style="text-align: center;width: 17%">完成标志：</td>
                            <td style="width: 33%;" >
                                <textarea id="doneFlag" name="doneFlag" class="mini-textarea rxc" plugins="mini-textarea"
                                          style="width:98%;;height:150px;line-height:25px;" label="" datatype="varchar" length="500"
                                          vtype="length:500" minlen="0" allowinput="true"
                                          emptytext="请输入完成标志" mwidth="80" wunit="%" mheight="150" hunit="px"></textarea>
                            </td>
                        </tr>
                        <tr>
                            <td style="text-align: center;width: 17%;height: 300px">改进证明材料：</td>
                            <td colspan="3">
                                <div style="margin-top: 10px;margin-bottom: 2px">
                                    <a id="gjAddFile" class="mini-button" onclick="gjAddFile()">添加附件</a>
                                </div>
                                <div id="gjFileListGrid" class="mini-datagrid" style="width: 100%; height: 85%" allowResize="false"
                                     idField="id" url="${ctxPath}/zlgjNPI/core/manageImprove/queryManageImproveFileList.do?filetype=gaijin&belongId=${manageImproveId}"
                                     autoload="true"
                                     multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
                                    <div property="columns">
                                        <div type="indexcolumn" align="center" width="20">序号</div>
                                        <div field="fileName" width="140" headerAlign="center" align="center">附件名称</div>
                                        <div field="fileSize" width="80" headerAlign="center" align="center">附件大小</div>
                                        <div field="creator" width="100" headerAlign="center" align="center">上传者</div>
                                        <div field="CREATE_TIME_" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" headerAlign="center">上传时间</div>
                                        <div field="action" width="100" headerAlign='center' align="center" renderer="gjoperationRenderer">操作</div>
                                    </div>
                                </div>
                            </td>
                        </tr>
                    </table>
                </div>
            </fieldset>

        </form>
    </div>
</div>


<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var manageImproveForm = new mini.Form("#manageImproveForm");
    var manageImproveId = "${manageImproveId}";
    var action = "${action}";
    var belongId = "${belongId}";
    var currentUserId = "${currentUserId}";
    var currentUserName = "${currentUserName}";
    var currentTime = "${currentTime}";
    var mainGroupId = "${mainGroupId}";
    var mainGroupName = "${mainGroupName}";
    var wtFileListGrid = mini.get("wtFileListGrid");
    var gjFileListGrid = mini.get("gjFileListGrid");
    var nodeVarsStr = '${nodeVars}';
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";


    //所有非特殊文本都变成这个格式
    function render(e) {
        if (e.value != null && e.value != "") {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            return html;
        }
    }

    function wtoperationRenderer(e) {
        var record = e.record;
        var cellHtml = returnManageImprovePreviewSpan(record.fileName, record.id, record.belongId, coverContent);

        //增加删除按钮
        if (action == 'edit' || (action == 'task' && (step == 'bianZhi' || step == 'fzrgj'))) {
            var deleteUrl = "/zlgjNPI/core/manageImprove/delManageImproveFileById.do"
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="wtDeleteFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.belongId + '\',\'' + deleteUrl + '\')">删除</span>';
        }
        return cellHtml;
    }
    function gjoperationRenderer(e) {
        var record = e.record;
        var cellHtml = returnManageImprovePreviewSpan(record.fileName, record.id, record.belongId, coverContent);

        //增加删除按钮
        if (action == 'edit' || (action == 'task' && (step == 'bianZhi' || step == 'fzrgj'))) {
            var deleteUrl = "/zlgjNPI/core/manageImprove/delManageImproveFileById.do"
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="gjDeleteFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.belongId + '\',\'' + deleteUrl + '\')">删除</span>';
        }
        return cellHtml;
    }
    // function operationRenderer(e) {
    //     var record = e.record;
    //     var cellHtml = '';
    //     cellHtml = returnManageImprovePreviewSpan(record.fileName, record.id, record.manageImproveId, coverContent);
    //     //编辑、产品主管填写、编制可以删除
    //     if (action == 'edit' || (action == 'task' && (step == 'bianZhi' || step == 'fzrgj'))) {
    //         var deleteUrl = "/zlgjNPI/core/manageImprove/delManageImproveFileById.do";
    //         cellHtml += '&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
    //             'onclick="deleteFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.manageImproveId + '\',\'' + deleteUrl + '\')">删除</span>';
    //     }
    //     return cellHtml;
    // }
    //删除文档
    function wtDeleteFile(fileName, fileId, formId, urlValue) {
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
                        url: url,
                        method: 'post',
                        contentType: 'application/json',
                        data: mini.encode(data),
                        success: function (json) {
                            if (wtFileListGrid) {
                                wtFileListGrid.load();
                            }
                        }
                    });
                }
            }
        );
    }
    //删除文档
    function gjDeleteFile(fileName, fileId, formId, urlValue) {
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
                        url: url,
                        method: 'post',
                        contentType: 'application/json',
                        data: mini.encode(data),
                        success: function (json) {
                            if (gjFileListGrid) {
                                gjFileListGrid.load();
                            }
                        }
                    });
                }
            }
        );
    }
    function  returnManageImprovePreviewSpan(fileName, fileId, formId, coverContent){
        var fileType = getFileType(fileName);
        var downloadUrl = "/zlgjNPI/core/manageImprove/manageImproveDownload.do";
        var s = '';
        if (fileType == 'other') {
            s = '<span  title="预览" style="color: silver" >预览</span>';
        } else if (fileType == 'pdf') {
            var url = '/zlgjNPI/core/manageImprove/manageImproveDownload.do';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + downloadUrl + '\')">预览</span>';
        } else if (fileType == 'office') {
            var url = '/zlgjNPI/core/manageImprove/manageImproveOfficePreview.do';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        } else if (fileType == 'pic') {
            var url = '/zlgjNPI/core/manageImprove/manageImproveImagePreview.do';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        }
        s += '&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downLoadFile(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + downloadUrl + '\')">下载</span>';
        return s;
    }

    function ifNot() {
        var canOrNot = mini.get("canOrNot").getValue();
        if (canOrNot == '不采纳') {
            mini.get("noReason").setEnabled(true);
            mini.get("responseManId").setEnabled(false);
            mini.get("responseManId").setText("");
            mini.get("responseManId").setValue("");
            mini.get("improveMethod").setEnabled(false);
            mini.get("improveMethod").setEmptyText("");
            mini.get("planFinishTime").setEnabled(false);
            mini.get("planFinishTime").setValue("");
            mini.get("planFinishTime").setText("");
            mini.get("doneFlag").setEnabled(false);
            mini.get("doneFlag").setEmptyText("");
            // $("#responseManId").css("background-color", "rgb(226 53 53)");

        } else {
            mini.get("noReason").setEnabled(false);
            mini.get("noReason").setValue("");
            mini.get("noReason").setEmptyText("");
            mini.get("responseManId").setEnabled(true);

        }
    }
</script>
</body>
</html>
