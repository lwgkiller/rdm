<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>新品装配异常信息编辑</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>

<body>
<div id="toolBar" class="topToolBar">
    <div>
        <a id="saveBusiness" class="mini-button" onclick="saveBusiness()">保存</a>
        <a id="closeWindow" class="mini-button btn-red" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin:0 auto; width: 100%;padding: 0">
        <form id="businessForm" method="post">
            <input class="mini-hidden" id="id" name="id"/>
            <input class="mini-hidden" id="mainId" name="mainId"/>
            <input class="mini-hidden" id="REF_ID_" name="REF_ID_"/>
            <input class="mini-hidden" id="isSyn" name="isSyn"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0" style="height: 100%;width: 100%">
                <caption style="font-weight: bold;font-size: 20px !important;">新品装配异常信息编辑</caption>
                <tr>
                    <td style="text-align: center;width: 15%">本地异常号：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="indexLocal" name="indexLocal" class="mini-spinner" style="width:98%;" maxValue="999"/>
                    </td>
                    <td style="text-align: center;width: 15%">整机编号：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="pin" name="pin" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">异常类型：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="exceptionType" name="exceptionType" class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=exceptionType"
                               valueField="key" textField="value"/>
                    </td>
                    <td style="text-align: center;width: 20%">部件分类：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="partsCategory" name="partsCategory" class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=partsCategory"
                               valueField="key" textField="value"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">异常部件：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="exceptionPart" name="exceptionPart" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 20%">紧急程度：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="problemLevel" name="problemLevel" class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=newProductAssembly-problemLevel"
                               valueField="key" textField="value"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">异常描述：<span style="color:red">*</span></td>
                    <td colspan="3">
                        <textarea id="exceptionDescription" name="exceptionDescription" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:99.1%;height:150px;line-height:25px;" datatype="varchar" allowinput="true"
                                  mwidth="80" wunit="%" mheight="200" hunit="px"></textarea>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">异常节点：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="assemblyNode" name="assemblyNode" class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=exceptionNode"
                               valueField="key" textField="value"/>
                    </td>
                    <td style="text-align: center;width: 15%">工作小时：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="workingHours" name="workingHours" class="mini-spinner" minValue="0" maxValue="99999" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">施工工况：</td>
                    <td style="min-width:170px">
                        <input id="workingCondition" name="workingCondition" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%">零部件供应商：</td>
                    <td style="min-width:170px">
                        <input id="supplier" name="supplier" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">故障率：</td>
                    <td style="min-width:170px">
                        <input id="failureRate" name="failureRate" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%">故障部位：</td>
                    <td style="min-width:170px">
                        <input id="failurePosition" name="failurePosition" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">第一责任人：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="repDepLeaderId" name="repDepLeaderId" textname="repDepLeader" class="mini-user rxc"
                               plugins="mini-user" style="width:98%;height:34px;" allowinput="false" length="1000" maxlength="1000"
                               mainfield="no" single="true"/>
                    </td>
                    <td style="text-align: center;width: 15%">责任部门(自动生成)：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="repDepId" name="repDepId" class="mini-dep rxc" plugins="mini-dep"
                               data-options="{'single':'true','config':{'type':'','grouplevel':'','groupid':'','groupidText':''}}"
                               style="width:98%;height:34px" allowinput="false" label="责任部门" textname="repDep" length="500"
                               maxlength="500" minlen="0" single="true" required="false" initlogindep="false" showclose="false"
                               mwidth="160" wunit="px" mheight="34" hunit="px" enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">问题处理人(回传产生)：</td>
                    <td style="min-width:170px">
                        <input id="repUserId" name="repUserId" textname="repUser" class="mini-user rxc"
                               plugins="mini-user" style="width:98%;height:34px;" allowinput="false" length="1000" maxlength="1000"
                               mainfield="no" single="true" enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">反馈人：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="feedbackPersonId" name="feedbackPersonId" textname="feedbackPerson" class="mini-user rxc"
                               plugins="mini-user" style="width:98%;height:34px;" allowinput="false" length="1000" maxlength="1000"
                               mainfield="no" single="true"/>
                    </td>
                    <td style="text-align: center;width: 20%">反馈时间(同步后产生)：</td>
                    <td style="min-width:170px">
                        <input id="feedbackTime" name="feedbackTime" class="mini-datepicker" format="yyyy-MM-dd"
                               showTime="false" showOkButton="true" showClearButton="false" style="width:98%;" enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">问题排查过程及检测方法：</td>
                    <td style="min-width:170px">
                        <textarea id="testMethod" name="testMethod" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:99.1%;height:150px;line-height:25px;" datatype="varchar" allowinput="true"
                                  mwidth="80" wunit="%" mheight="200" hunit="px"></textarea>
                    </td>
                    <td style="text-align: center;width: 15%">现场处置方法：</td>
                    <td style="min-width:170px">
                        <textarea id="disposalMethod" name="disposalMethod" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:99.1%;height:150px;line-height:25px;" datatype="varchar" allowinput="true"
                                  mwidth="80" wunit="%" mheight="200" hunit="px"></textarea>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">问题严重度(回传同步)：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="severity" name="severity" class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=newProductAssembly-severity"
                               valueField="key" textField="value" enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 15%">是否需要改进(回传同步)：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="isImprove" name="isImprove" class="mini-combobox" style="width:98%"
                               valueField="key" textField="value" data="[{key:'是',value:'是'},{key:'否',value:'否'}]" enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">改进要求：</td>
                    <td style="min-width:170px">
                        <textarea id="improvementRequirements" name="improvementRequirements" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:99.1%;height:150px;line-height:25px;" datatype="varchar" allowinput="true"
                                  mwidth="80" wunit="%" mheight="200" hunit="px"></textarea>
                    </td>
                    <td style="text-align: center;width: 15%">不改进理由(回传产生)：</td>
                    <td style="min-width:170px">
                        <textarea id="noImproveReason" name="noImproveReason" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:99.1%;height:150px;line-height:25px;" datatype="varchar" allowinput="true"
                                  mwidth="80" wunit="%" mheight="200" hunit="px" enabled="false"></textarea>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">临时处理措施(回传产生)：</td>
                    <td style="min-width:170px">
                        <textarea id="temporaryMeasures" name="temporaryMeasures" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:99.1%;height:150px;line-height:25px;" datatype="varchar" allowinput="true"
                                  mwidth="80" wunit="%" mheight="200" hunit="px" enabled="false"></textarea>
                    </td>
                    <td style="text-align: center;width: 15%">最终解决方案(回传产生)：</td>
                    <td style="min-width:170px">
                        <textarea id="permanentMeasures" name="permanentMeasures" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:99.1%;height:150px;line-height:25px;" datatype="varchar" allowinput="true"
                                  mwidth="80" wunit="%" mheight="200" hunit="px" enabled="false"></textarea>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">临时处理时间(回传产生)：</td>
                    <td style="min-width:170px">
                        <%--<input id="temporaryTime" name="temporaryTime" class="mini-datepicker" format="yyyy-MM-dd"--%>
                        <%--showTime="false" showOkButton="true" showClearButton="false" style="width:98%;" enabled="false"/>--%>
                        <input id="temporaryTime" name="temporaryTime" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 20%">最终解决方案时间(回传产生)：</td>
                    <td style="min-width:170px">
                        <%--<input id="permanentTime" name="permanentTime" class="mini-datepicker" format="yyyy-MM-dd"--%>
                        <%--showTime="false" showOkButton="true" showClearButton="false" style="width:98%;" enabled="false"/>--%>
                        <input id="permanentTime" name="permanentTime" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">是否闭环：</td>
                    <td style="min-width:170px">
                        <input id="isClear" name="isClear" class="mini-combobox" style="width:98%"
                               valueField="key" textField="value" data="[{key:'是',value:'是'},{key:'否',value:'否'}]" enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 20%">备注：</td>
                    <td style="min-width:170px">
                        <textarea id="remark" name="remark" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:99.1%;height:150px;line-height:25px;" datatype="varchar" allowinput="true"
                                  mwidth="80" wunit="%" mheight="200" hunit="px"></textarea>
                    </td>
                </tr>
                <%---------------------------------%>
                <tr>
                    <td style="text-align: center;height: 300px">附件：</td>
                    <td colspan="3">
                        <div class="mini-toolbar" id="fileButtons" style="display: none">
                            <a id="uploadFileContract" class="mini-button" style="margin-bottom: 5px;margin-top: 5px"
                               onclick="uploadFile">添加附件</a>
                        </div>
                        <div id="fileListGrid" class="mini-datagrid" style="width: 100%; height: 85%" allowResize="false" idField="id"
                             url="${ctxPath}/newproductAssembly/core/kanban/getFileList.do?mainId=${businessId}"
                             multiSelect="false" showPager="true" showColumnsMenu="false" sizeList="[10,20,50,100]" pageSize="10"
                             allowAlternating="true" pagerButtons="#pagerButtons">
                            <div property="columns">
                                <div type="indexcolumn" headerAlign="center" align="center" width="20">序号</div>
                                <div field="fileDesc" align="center" headerAlign="center" width="100">描述</div>
                                <div field="fileName" align="center" headerAlign="center" width="150">文件名</div>
                                <div field="fileSize" align="center" headerAlign="center" width="60">文件大小</div>
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
    var pin = "${pin}";
    var businessId = "${businessId}";
    var action = "${action}";
    var currentUserId = "${currentUserId}";
    var currentUserName = "${currentUserName}"
    var currentTime = "${currentTime}";
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";
    //..
    $(function () {
        if (businessId || mainId) {
            var url = jsUseCtxPath + "/newproductAssembly/core/kanban/getExceptionDetail.do?businessId=" + businessId;
            $.ajax({
                url: url,
                method: 'get',
                success: function (json) {
                    businessForm.setData(json);
                    if (!mini.get("isSyn").getValue()) {
                        mini.get("isSyn").setValue("否");
                    }
                    if (!mini.get("problemLevel").getValue()) {
                        mini.get("problemLevel").setValue("一般");
                    }
                    if (!mini.get("severity").getValue()) {
                        mini.get("severity").setValue("C");
                    }
                    if (!mini.get("isImprove").getValue()) {
                        mini.get("isImprove").setValue("是");
                    }
                    if (!mini.get("isClear").getValue()) {
                        mini.get("isClear").setValue("否");
                    }
                    //不同场景的处理
                    if (action == 'detail') {
                        businessForm.setEnabled(false);
                    } else if (action == 'add') {
                        mini.get("pin").setValue(pin);
                        mini.get("fileButtons").show();
                        mini.get("feedbackPersonId").setValue("${feedbackPersonId}");
                        mini.get("feedbackPersonId").setText("${feedbackPerson}");
                    } else if (action == 'edit') {
                        mini.get("indexLocal").setEnabled(false);
                        mini.get("fileButtons").show();
                    }
                    fileListGrid.load();
                }
            });
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
        postData.indexLocal = mini.get("indexLocal").getValue();
        postData.pin = mini.get("pin").getValue();
        postData.exceptionType = mini.get("exceptionType").getValue();
        postData.partsCategory = mini.get("partsCategory").getValue();
        postData.exceptionPart = mini.get("exceptionPart").getValue();
        postData.problemLevel = mini.get("problemLevel").getValue();
        postData.exceptionDescription = mini.get("exceptionDescription").getValue();
        postData.assemblyNode = mini.get("assemblyNode").getValue();
        postData.workingHours = mini.get("workingHours").getValue();
        postData.workingCondition = mini.get("workingCondition").getValue();
        postData.supplier = mini.get("supplier").getValue();
        postData.failureRate = mini.get("failureRate").getValue();
        postData.failurePosition = mini.get("failurePosition").getValue();
        postData.repDepLeaderId = mini.get("repDepLeaderId").getValue();
        postData.repDepLeader = mini.get("repDepLeaderId").getText();
        postData.repDepId = mini.get("repDepId").getValue();
        postData.repDep = mini.get("repDepId").getText();
        postData.repUserId = mini.get("repUserId").getValue();
        postData.repUser = mini.get("repUserId").getText();
        postData.feedbackPersonId = mini.get("feedbackPersonId").getValue();
        postData.feedbackPerson = mini.get("feedbackPersonId").getText();
        postData.feedbackTime = mini.get("feedbackTime").getText();
        postData.testMethod = mini.get("testMethod").getText();
        postData.disposalMethod = mini.get("disposalMethod").getText();
        postData.severity = mini.get("severity").getText();
        postData.isImprove = mini.get("isImprove").getText();
        postData.improvementRequirements = mini.get("improvementRequirements").getText();
        postData.noImproveReason = mini.get("noImproveReason").getText();
        postData.severity = mini.get("severity").getValue();
        postData.isImprove = mini.get("isImprove").getValue();
        postData.temporaryMeasures = mini.get("temporaryMeasures").getValue();
        postData.temporaryTime = mini.get("temporaryTime").getText();
        postData.permanentMeasures = mini.get("permanentMeasures").getValue();
        postData.permanentTime = mini.get("permanentTime").getText();
        postData.isClear = mini.get("isClear").getValue();
        postData.remark = mini.get("remark").getValue();
        postData.isSyn = mini.get("isSyn").getValue();
        //检查必填项
        var checkResult = saveValidCheck(postData);
        if (!checkResult.success) {
            mini.alert(checkResult.reason);
            return;
        }
        $.ajax({
            url: jsUseCtxPath + "/newproductAssembly/core/kanban/saveException.do",
            type: 'POST',
            contentType: 'application/json',
            data: mini.encode(postData),
            success: function (returnData) {
                if (returnData && returnData.message) {
                    mini.alert(returnData.message, '提示', function () {
                        if (returnData.success) {
                            var url = jsUseCtxPath + "/newproductAssembly/core/kanban/exceptionPage.do?businessId=" +
                                returnData.data + "&pin=" + pin + "&action=edit";
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
        if (!postData.pin) {
            checkResult.success = false;
            checkResult.reason = '请输入车号！';
            return checkResult;
        }
        if (postData.pin.length != 17) {
            checkResult.success = false;
            checkResult.reason = '车号必须是17位！';
            return checkResult;
        }
        debugger;
        if (pin.search(postData.pin) == -1) {
            checkResult.success = false;
            checkResult.reason = '车号必须在档案范围内！';
            return checkResult;
        }
        if (!postData.indexLocal) {
            checkResult.success = false;
            checkResult.reason = '请输入本地异常号！';
            return checkResult;
        }
        if (!postData.exceptionType) {
            checkResult.success = false;
            checkResult.reason = '请选择异常类型！';
            return checkResult;
        }
        if (!postData.partsCategory) {
            checkResult.success = false;
            checkResult.reason = '请选择部件分类！';
            return checkResult;
        }
        if (!postData.exceptionPart) {
            checkResult.success = false;
            checkResult.reason = '请填写异常部件！';
            return checkResult;
        }
        if (!postData.problemLevel) {
            checkResult.success = false;
            checkResult.reason = '请选择紧急程度！';
            return checkResult;
        }
        if (!postData.exceptionDescription) {
            checkResult.success = false;
            checkResult.reason = '请填写异常描述！';
            return checkResult;
        }
        if (!postData.assemblyNode) {
            checkResult.success = false;
            checkResult.reason = '请选择异常节点！';
            return checkResult;
        }
        if (!postData.repDepLeaderId) {
            checkResult.success = false;
            checkResult.reason = '请选择责第一责任人！';
            return checkResult;
        }
        if (!postData.feedbackPersonId) {
            checkResult.success = false;
            checkResult.reason = '请选择反馈人！';
            return checkResult;
        }
        checkResult.success = true;
        return checkResult;
    }
    //..
    function uploadFile() {
        var mainId = mini.get("id").getValue();
        if (!mainId) {
            mini.alert("请先点击‘保存’进行记录创建！")
            return;
        }
        mini.open({
            title: "附件上传",
            url: jsUseCtxPath + "/newproductAssembly/core/kanban/openUploadWindow.do?mainId=" + mainId,
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
        var downLoadUrl = '/newproductAssembly/core/kanban/pdfPreviewAndAllDownload.do';
        cellHtml += '&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downLoadFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.mainId + '\',\'' + downLoadUrl + '\')">下载</span>';
        //增加删除按钮
        if (action != 'detail') {
            var deleteUrl = "/newproductAssembly/core/kanban/delFile.do"
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
            var url = '/newproductAssembly/core/kanban/pdfPreviewAndAllDownload.do';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        } else if (fileType == 'office') {
            var url = '/newproductAssembly/core/kanban/officePreview.do';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        } else if (fileType == 'pic') {
            var url = '/newproductAssembly/core/kanban/imagePreview.do';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        }
        return s;
    }
</script>
</body>
</html>
