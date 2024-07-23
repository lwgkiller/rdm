<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>操保手册发运/需求申请</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>

<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="processInfo" class="mini-button" style="display: none" onclick="processInfo()">流程信息</a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin: 0 auto; width: 100%;">
        <form id="formBusiness" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <input id="applyUserId" name="applyUserId" class="mini-hidden"/>
            <input id="applyDepId" name="applyDepId" class="mini-hidden"/>
            <input id="businessStatus" name="businessStatus" class="mini-hidden"/>
            <input id="INST_ID_" name="INST_ID_" class="mini-hidden"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0">
                <caption style="font-weight: bold">
                    全生命周期成本清单签审归档
                </caption>
                <tr>
                    <td style="text-align: center;width: 15%">单据编号(自动生成)：</td>
                    <td style="min-width:170px">
                        <input id="busunessNo" name="busunessNo" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 15%">需求单号：</td>
                    <td style="min-width:170px">
                        <input id="demandListNo" name="demandListNo" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">销售型号：</td>
                    <td style="min-width:170px">
                        <input id="salesModel" name="salesModel" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%">设计型号：</td>
                    <td style="min-width:170px">
                        <input id="designModel" name="designModel" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">物料编码：</td>
                    <td style="min-width:170px">
                        <input id="materialCode" name="materialCode" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%">销售区域：</td>
                    <td style="min-width:170px">
                        <input id="salesArea" name="salesArea" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">申请时间：</td>
                    <td style="min-width:170px">
                        <input id="applyTime" name="applyTime" class="mini-datepicker" format="yyyy-MM-dd" allowInput="false"
                               valueType="string" showTime="false" showOkButton="false" showClearButton="true" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 20%">需求时间：</td>
                    <td style="min-width:170px">
                        <input id="demandTime" name="demandTime" class="mini-datepicker" format="yyyy-MM-dd" allowInput="false"
                               valueType="string" showTime="false" showOkButton="false" showClearButton="true" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">申请人(自动生成)：</td>
                    <td style="min-width:170px">
                        <input id="applyUser" name="applyUser" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 15%">申请部门(自动生成)：</td>
                    <td style="min-width:170px">
                        <input id="applyDep" name="applyDep" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">产品主管: </td>
                    <td style="min-width:170px">
                        <input id="cpzgId" name="cpzgId" textname="cpzgName" class="mini-user rxc"
                               plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="" length="50" maxlength="50"
                               mainfield="no"  single="true" />
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">备注：</td>
                    <td colspan="3">
						<textarea id="remark" name="remark" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:98%;height:150px;line-height:25px;" label="" datatype="varchar" length="640"
                                  vtype="length:640" minlen="0" allowinput="true" wunit="%" mheight="150" hunit="px"></textarea>
                    </td>
                </tr>
                <%--///////////////////////////////////////////////////--%>
                <tr>
                    <td style="text-align: center;height: 300px">附件列表：</td>
                    <td colspan="3">
                        <div style="margin-top: 10px;margin-bottom: 2px">
                            <a id="addFile" class="mini-button" onclick="addBusinessFile()">添加文件</a>
                            <span style="color: red">注：添加文件前，请先进行草稿的保存</span>
                        </div>
                        <div id="fileListGrid" class="mini-datagrid" style="width: 100%; height: 85%" allowResize="false"
                             idField="id"
                             url="${ctxPath}/serviceEngineering/core/fullLifeCycleCost/getFileList.do?businessId=${businessId}"
                             autoload="true"
                             multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
                            <div property="columns">
                                <div type="indexcolumn" align="center" width="20">序号</div>
                                <div field="fileName" align="center" headerAlign="center" width="150">文件名</div>
                                <div field="fileSize" align="center" headerAlign="center" width="60">文件大小</div>
                                <div field="fileDesc" align="center" headerAlign="center" width="100">备注说明</div>
                                <div field="action" width="100" headerAlign='center' align="center" renderer="operationRenderer">操作</div>
                            </div>
                        </div>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
<%--操保手册弹出窗口--%>
<div id="selectManualFileWindow" title="选择操保手册" class="mini-window" style="width:1450px;height:700px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
    <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px" borderStyle="border-left:0;border-top:0;border-right:0;">
        <li style="margin-right: 15px">
            <span class="text" style="width:auto">销售型号：</span>
            <input class="mini-textbox" id="salesModelSub" name="salesModelSub" style="width: 120px"/>
            <span class="text" style="width:auto">设计型号：</span>
            <input class="mini-textbox" id="designModelSub" name="designModelSub" style="width: 120px"/>
            <span class="text" style="width:auto">整机物料编码：</span>
            <input class="mini-textbox" id="materialCodeSub" name="materialCodeSub" style="width: 120px"/>
            <span class="text" style="width:auto">文件名称：</span>
            <input class="mini-textbox" id="manualDescriptionSub" name="manualDescriptionSub" style="width: 120px"/>
            <span class="text" style="width:auto">手册语言：</span>
            <input class="mini-textbox" id="manualLanguageSub" name="manualLanguageSub" style="width: 120px"/>
            <span class="text" style="width:auto">手册编码：</span>
            <input class="mini-textbox" id="manualCodeSub" name="manualCodeSub" style="width: 120px"/>
            <span class="text" style="width:auto">是否CE：</span>
            <input class="mini-textbox" id="isCESub" name="isCESub" style="width: 120px"/>
        </li>
        <a class="mini-button" iconCls="icon-search" plain="true" onclick="searchManualFile()">查询</a>
    </div>
    <div class="mini-fit">
        <div id="manualFileListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
             idField="id" showColumnsMenu="false" sizeList="[20,50,100,200]" pageSize="20" multiSelect="true"
             allowAlternating="true" pagerButtons="#pagerButtons" url="${ctxPath}/serviceEngineering/core/maintenanceManualfile/dataListQuery.do">
            <div property="columns">
                <div type="checkcolumn" width="30"></div>
                <div field="salesModel" width="120" headerAlign="center" align="center" renderer="render" allowSort="true">销售型号</div>
                <div field="designModel" width="120" headerAlign="center" align="center" renderer="render" allowSort="true">设计型号</div>
                <div field="materialCode" width="120" headerAlign="center" align="center" renderer="render" allowSort="true">物料编码</div>
                <div field="manualDescription" width="400" headerAlign="center" align="center" renderer="render" allowSort="true">文件名称</div>
                <div field="cpzgName" width="80" headerAlign="center" align="center" renderer="render" allowSort="true">产品主管</div>
                <div field="manualLanguage" width="80" headerAlign="center" align="center" renderer="render" allowSort="true">手册语言</div>
                <div field="manualCode" width="140" headerAlign="center" align="center" renderer="render" allowSort="true">手册编码</div>
                <div field="manualVersion" width="80" headerAlign="center" align="center" renderer="render" allowSort="true">GSS版本</div>
                <div field="manualEdition" width="80" headerAlign="center" align="center" renderer="render" allowSort="true">手册版本</div>
                <div field="isCE" width="80" headerAlign="center" align="center" renderer="render" allowSort="true">是否CE版</div>
                <div field="manualStatus" width="80" headerAlign="center" align="center" renderer="render" allowSort="true">文件状态</div>
                <div field="publishTime" width="110" headerAlign="center" align="center" dateFormat="yyyy-MM-dd" renderer="render" allowSort="true">
                    发布时间
                </div>
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
    var action = "${action}";
    var status = "${status}";
    var jsUseCtxPath = "${ctxPath}";
    var fileListGrid = mini.get("fileListGrid");
    var formBusiness = new mini.Form("#formBusiness");
    var selectManualFileWindow = mini.get("selectManualFileWindow");
    var manualFileListGrid = mini.get("manualFileListGrid");
    var businessId = "${businessId}";
    var nodeVarsStr = '${nodeVars}';
    var currentTime = "${currentTime}";
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var currentUserName = "${currentUserName}";
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";
    var isBianzhi = "";
    var isQueren = "";
    var isTranslation = "";
    var isCPZGQueren = "";
    var isDayin = "";

    //..
    $(function () {
        var url = jsUseCtxPath + "/serviceEngineering/core/fullLifeCycleCost/getDetail.do";
        $.post(
            url,
            {businessId: businessId},
            function (json) {
                formBusiness.setData(json);
                if (action == 'detail') {
                    formBusiness.setEnabled(false);
                    mini.get("addFile").setEnabled(false);
                    $("#detailToolBar").show();
                    //非草稿放开流程信息查看按钮
                    if (status != 'DRAFTED') {
                        $("#processInfo").show();
                    }
                } else if (action == 'task') {
                    formBusiness.setEnabled(false);
                    mini.get("remark").setEnabled(false);
                    mini.get("addFile").setEnabled(false);
                    taskActionProcess();
                }
            });
    });

    //..流程引擎调用此方法进行表单数据的获取
    function getData() {
        var formData = _GetFormJsonMini("formBusiness");
        if (formData.SUB_fileListGrid) {
            delete formData.SUB_fileListGrid;
        }
        return formData;
    }

    //..保存草稿
    function saveBusiness(e) {
        window.parent.saveDraft(e);
    }

    //..启动流程
    function startBusinessProcess(e) {
        var formValid = validBusiness();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        saveBusinessInProcess();
        window.parent.startProcess(e);
    }

    //..流程中暂存信息（如编制阶段）
    function saveBusinessInProcess() {
        var formData = _GetFormJsonMini("formBusiness");
        if (formData.SUB_fileListGrid) {
            delete formData.SUB_fileListGrid;
        }
        $.ajax({
            url: jsUseCtxPath + '/serviceEngineering/core/fullLifeCycleCost/saveBusiness.do',
            type: 'post',
            async: false,
            data: mini.encode(formData),
            contentType: 'application/json',
            success: function (data) {
                if (data) {
                    var message = "";
                    if (data.success) {
                        message = "数据保存成功";
                    } else {
                        message = "数据保存失败" + data.message;
                    }

                    mini.alert(message, "提示信息", function () {
                        window.location.reload();
                    });
                }
            }
        });
    }

    //..启动审批或下一步
    function businessApprove() {
        var formValid = validBusiness();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        window.parent.approve();
    }

    //..添加文件
    function addBusinessFile() {
        var businessId = mini.get("id").getValue();
        if (!businessId) {
            mini.alert('请先点击‘保存草稿’进行表单的保存！');
            return;
        }
        mini.open({
            title: "文件上传",
            url: jsUseCtxPath + "/serviceEngineering/core/fullLifeCycleCost/fileUploadWindow.do?businessId=" + businessId,
            width: 750,
            height: 450,
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

    //..流程信息浏览
    function processInfo() {
        var instId = $("#INST_ID_").val();
        debugger
        var url = jsUseCtxPath + "/bpm/core/bpmInst/info.do?instId=" + instId;
        _OpenWindow({
            url: url,
            max: true,
            title: "流程图实例",
            width: 800,
            height: 600
        });
    }

    //..获取任务相关的环境变量，处理表单可见性
    function taskActionProcess() {
        //获取上一环节的结果和处理人
        bpmPreTaskTipInForm();

        //获取环境变量
        if (!nodeVarsStr) {
            nodeVarsStr = "[]";
        }
        var nodeVars = $.parseJSON(nodeVarsStr);
        for (var i = 0; i < nodeVars.length; i++) {
            if (nodeVars[i].KEY_ == 'isBianzhi') {
                isBianzhi = nodeVars[i].DEF_VAL_;
            } else if (nodeVars[i].KEY_ == 'isQueren') {
                isQueren = nodeVars[i].DEF_VAL_;
            } else if (nodeVars[i].KEY_ == 'isTranslation') {
                isTranslation = nodeVars[i].DEF_VAL_;
            } else if (nodeVars[i].KEY_ == 'isCPZGQueren') {
                isCPZGQueren = nodeVars[i].DEF_VAL_;
            } else if (nodeVars[i].KEY_ == 'isDayin') {
                isDayin = nodeVars[i].DEF_VAL_;
            }
        }
        if (isBianzhi == 'yes') {
            formBusiness.setEnabled(true);
            mini.get("remark").setEnabled(true);
            mini.get("addFile").setEnabled(true);
        }
    }

    //..检验表单是否必填
    function validBusiness() {
        var demandListNo = $.trim(mini.get("demandListNo").getValue());
        if (!demandListNo) {
            return {"result": false, "message": "请填写需求单号"};
        }
        var salesModel = $.trim(mini.get("salesModel").getValue());
        if (!salesModel) {
            return {"result": false, "message": "请填写销售型号"};
        }
        var designModel = $.trim(mini.get("designModel").getValue());
        if (!designModel) {
            return {"result": false, "message": "请填写设计型号"};
        }
        var materialCode = $.trim(mini.get("materialCode").getValue());
        if (!materialCode) {
            return {"result": false, "message": "请填写物料编码"};
        }
        var salesArea = $.trim(mini.get("salesArea").getValue());
        if (!salesArea) {
            return {"result": false, "message": "请填写销售区域"};
        }
        var applyTime = $.trim(mini.get("applyTime").getValue());
        if (!applyTime) {
            return {"result": false, "message": "请填写申请时间"};
        }
        var demandTime = $.trim(mini.get("demandTime").getValue());
        if (!demandTime) {
            return {"result": false, "message": "请填写需求时间"};
        }
        var cpzgId = $.trim(mini.get("cpzgId").getValue());
        if (!cpzgId) {
            return {"result": false, "message": "请填写产品主管"};
        }
        return {"result": true};
    }

    //..文件列表操作渲染
    function operationRenderer(e) {
        var record = e.record;
        var cellHtml = '';
        cellHtml = returnPreviewSpan(record.fileName, record.id, record.mainId, coverContent);
        //和pdf预览用的同一个url
        var downLoadUrl = '/serviceEngineering/core/fullLifeCycleCost/PdfPreviewAndAllDownload.do';
        cellHtml += '&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downLoadFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.mainId + '\',\'' + downLoadUrl + '\')">下载</span>';
        //增加删除按钮
        if (action == 'edit' || (action == 'task' && isBianzhi == 'yes')) {
            var deleteUrl = "/serviceEngineering/core/fullLifeCycleCost/delFile.do"
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.mainId + '\',\'' + deleteUrl + '\')">删除</span>';
        }
        return cellHtml;
    }

    //..文件列表预览渲染(复用接口)
    function returnPreviewSpan(fileName, fileId, formId, coverContent) {
        var fileType = getFileType(fileName);
        var s = '';
        if (fileType == 'other') {
            s = '<span  title="预览" style="color: silver" >预览</span>';
        } else if (fileType == 'pdf') {
            var url = '/serviceEngineering/core/maintenanceManualDemand/PdfPreview.do';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        } else if (fileType == 'office') {
            var url = '/serviceEngineering/core/maintenanceManualDemand/OfficePreview.do';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        } else if (fileType == 'pic') {
            var url = '/serviceEngineering/core/maintenanceManualDemand/ImagePreview.do';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        }
        return s;
    }

    //..便捷查看相关归档文件
    function showManualfile() {
        var salesModel = mini.get("salesModel").getValue();
        var designModel = mini.get("designModel").getValue();
        var materialCode = mini.get("materialCode").getValue();
        var manualLanguage = mini.get("manualLanguage").getValue();
        var isCE = mini.get("isCE").getValue();
        var realUrl = jsUseCtxPath + "/serviceEngineering/core/maintenanceManualfile/dataListPage.do?salesModel=" + salesModel +
            "&designModel=" + designModel + "&materialCode=" + materialCode + "&manualLanguage=" + manualLanguage + "&isCE=" + isCE;
        var config = {
            url: realUrl,
            max: true
        };
        _OpenWindow(config);
    }

    //..
    function manualFileRenderer(e) {
        var record = e.record;
        var cellHtml = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewManualFile(\'' + record.manualFileId + '\',\'' + coverContent + '\')">预览</span>';
        return cellHtml;
    }

    //..
    function previewManualFile(id, coverContent) {
        var previewUrl = jsUseCtxPath + "/serviceEngineering/core/maintenanceManualfile/Preview.do?id=" + id;
        window.open(jsUseCtxPath + "/pdfjs/web/viewer.html?coverContent=" + encodeURIComponent(coverContent) + "&file=" + encodeURIComponent(previewUrl));
    }

    //..
    function addManualMatch() {
        //打开选择窗口，选择后插入本表中
        selectManualFileWindow.show();
        searchManualFile();
    }

    //..
    function delManualMatch() {
        var selecteds = manualMatchListGrid.getSelecteds();
        manualMatchListGrid.removeRows(selecteds);
    }

    //..
    function searchManualFile() {
        var paramArray = [];
        paramArray.push({name: "salesModel", value: mini.get('salesModelSub').getValue()});
        paramArray.push({name: "designModel", value: mini.get('designModelSub').getValue()});
        paramArray.push({name: "materialCode", value: mini.get('materialCodeSub').getValue()});
        paramArray.push({name: "manualDescription", value: mini.get('manualDescriptionSub').getValue()});
        paramArray.push({name: "manualLanguage", value: mini.get('manualLanguageSub').getValue()});
        paramArray.push({name: "manualCode", value: mini.get('manualCodeSub').getValue()});
        paramArray.push({name: "isCE", value: mini.get('isCESub').getValue()});
        var data = {};
        data.filter = mini.encode(paramArray);
        manualFileListGrid.load(data);
    }

    //..
    function selectFileOK() {
        var rows = manualFileListGrid.getSelecteds();
        if (rows.length > 0) {
            for (var index = 0; index < rows.length; index++) {
                var row = rows[index];
                row.manualFileId = row.id;
                row.id = '';
                manualMatchListGrid.addRow(row);
            }
        }
        selectFileHide();
    }

    //..
    function selectFileHide() {
        mini.get('salesModelSub').setValue('');
        mini.get('designModelSub').setValue('');
        mini.get('materialCodeSub').setValue('');
        mini.get('manualDescriptionSub').setValue('');
        mini.get('manualLanguageSub').setValue('');
        mini.get('manualCodeSub').setValue('');
        mini.get('isCESub').setValue('');
        selectManualFileWindow.hide();
    }
</script>
</body>
</html>
