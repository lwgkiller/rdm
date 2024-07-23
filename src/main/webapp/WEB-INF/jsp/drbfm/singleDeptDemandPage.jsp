
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
    <title>部门需求管理</title>
    <%@include file="/commons/list.jsp"%>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
    <style>
        .mini-grid-rows-view
        {
            background: white !important;
        }
        .mini-grid-cell-inner {
            line-height: 40px !important;
            padding: 0;
        }
        .mini-grid-cell-inner
        {
            font-size:14px !important;
        }
    </style>
</head>
<body>
<div id="loading" class="loading" style="display:none;text-align:center;"><img src="${ctxPath}/styles/images/loading.gif"></div>
<div class="topToolBar">
    <div>
        <a class="mini-button" id="addDemand" plain="true" style="float: left;display: none"
           onclick="addDeptDemand()">添加</a>
        <a class="mini-button" id="demandCollect" plain="true" style="float: left;display: none"
           onclick="deptDemandCollect()">发起需求收集</a>
        <a class="mini-button" plain="true" style="float: left;"
           onclick="deptDemandCollectSee()">查看需求收集</a>
        <a class="mini-button btn-red" id="delDemand" plain="true" style="float: left;display: none"
           onclick="removeDeptDemand()">删除</a>
        <span id="tips" style="color: red;float: left;display: none">（注：“流程收集”类型的需求不允许删除）</span>
    </div>
</div>
<div class="mini-fit" style="width: 100%; height: 85%;" id="content">
    <div id="deptDemandListGrid" class="mini-datagrid" allowResize="false" style="height: 100%" autoload="true"
         idField="id" allowCellEdit="false" allowCellSelect="true" allowSortColumn="false"
         multiSelect="true" showColumnsMenu="false" allowAlternating="true" showPager="false"
         url="${ctxPath}/drbfm/single/getSingleDeptDemandList.do?belongSingleId=${singleId}"
         allowCellWrap="false" showVGridLines="true">
        <div property="columns">
            <div type="checkcolumn" width="75px"></div>
            <div name="action" cellCls="actionIcons" width="110px" headerAlign="center" align="center"
                 renderer="onActionRenderer" cellStyle="padding:0;">操作</div>
            <div field="demandType" headerAlign="center" width="110px" align="center">需求类型
            </div>
            <div field="demandDesc" headerAlign="center" width="600px" align="left"
                 renderer="renderDemandDesc">需求描述
            </div>
            <div field="demandReason" headerAlign="center" width="400px" align="left"
                 renderer="renderReason">需求理由/来源
            </div>
            <div field="deptIds" displayField="deptNames" headerAlign="center" align="center" width="200px">需求提出部门
            </div>
            <div field="creator" headerAlign="center" align="center" width="100px">创建人
            </div>
        </div>
    </div>
</div>

<div id="demandWindow" title="部门需求" class="mini-window" style="width:880px;height:420px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
    <div class="mini-fit">
        <div class="topToolBar" style="float: right;">
            <div style="position: relative!important;">
                <a id="saveDemand" name="saveDemand" class="mini-button" onclick="saveDeptDemand()">保存</a>
                <a id="closeDemand" class="mini-button btn-red" onclick="closeDeptDemand()">关闭</a>
            </div>
        </div>
        <input id="id" name="id" class="mini-hidden"/>
        <input id="demandType" name="demandType" class="mini-hidden"/>
        <table class="table-detail"  cellspacing="1" cellpadding="0" style="width: 99%">
            <tr>
                <td style="width: 10%">需求描述(500字以内)：<span style="color:red">*</span></td>
                <td style="width: 40%;">
                    <input id="demandDesc" name="demandDesc" class="mini-textarea"
                              plugins="mini-textarea" style="width:99%;;height:120px;line-height:25px;" label="需求描述"
                               allowinput="true" emptytext="请输入需求描述..." />
                </td>
            </tr>
            <tr>
                <td style="width: 10%">需求理由/来源(500字以内)：<span style="color:red">*</span></td>
                <td style="width: 40%;">
                    <input id="demandReason" name="demandReason" class="mini-textarea"
                           plugins="mini-textarea" style="width:99%;;height:120px;line-height:25px;" label="需求理由/来源"
                           allowinput="true" emptytext="请输入需求理由/来源..." />
                </td>
            </tr>
            <tr>
                <td style="width: 10%">需求提出部门：<span style="color:red">*</span></td>
                <td style="width: 40%;">
                    <input id="deptIds" name="deptIds" class="mini-dep rxc" plugins="mini-dep"
                           style="width:98%;height:34px"
                           allowinput="false" textname="deptNames" length="500" maxlength="500"
                           minlen="0" single="false" initlogindep="false"/>
                </td>
            </tr>
            <tr>
                <td style="width: 14%;height: 300px">附件列表：</td>
                <td colspan="5">
                    <div style="margin-top:2px;margin-bottom: 2px" id="fileListToolBar">
                        <a id="addFile" class="mini-button" onclick="demandFileUpload()">添加附件</a>
                        <span style="color: red">注：添加附件前，请先点击“保存”</span>
                    </div>
                    <div id="fileListGrid" class="mini-datagrid" style="width: 100%; height: 80%"
                         allowResize="false"
                         idField="id"
                         multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
                        <div property="columns">
                            <div type="indexcolumn" align="center" width="20">序号</div>
                            <div field="fileName" width="140" headerAlign="center" align="center">附件名称</div>
                            <div field="fileSize" width="80" headerAlign="center" align="center">附件大小</div>
                            <div field="action" width="100" headerAlign='center' align="center"
                                 renderer="operationRenderer">操作
                            </div>
                        </div>
                    </div>
                </td>
            </tr>
        </table>
    </div>
</div>

<div id="demandCollectWindow" title="发起需求收集流程" class="mini-window" style="width:880px;height:300px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
    <div class="mini-fit">
        <div class="topToolBar">
            <div style="position: relative!important;">
                <a id="createDemandCollect" class="mini-button" onclick="createDemandCollect()">创建流程</a>
                <a id="closeDemandCollect" class="mini-button btn-red" onclick="closeDemandCollect()">关闭</a>
                <span style="color: red;float: left">（注：创建流程后，请在“可靠性增长”--“APQP”--“部门需求收集列表”菜单中查看）</span>
            </div>
        </div>
        <table class="table-detail"  cellspacing="1" cellpadding="0" style="width: 99%">
            <tr>
                <td style="width: 10%;height:200px">需求反馈人：<span style="color:red">*</span></td>
                <td style="width: 40%;">
                    <input id="feedbackUserIds" textname="feedbackUserNames" class="mini-user rxc"
                           plugins="mini-user" style="width:98%;" allowinput="false" label="" length="500" maxlength="500"
                           mainfield="no" single="false"/>
                </td>
            </tr>
        </table>
    </div>
</div>


<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var singleId = "${singleId}";
    var action="${action}";
    var stageName = "${stageName}";
    var currentUserId = "${currentUserId}";
    var currentUserName = "${currentUserName}";
    var currentTime = "${currentTime}";
    var fileListGrid = mini.get("fileListGrid");
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";
    var deptDemandListGrid=mini.get("deptDemandListGrid");
    var demandWindow=mini.get("demandWindow");
    var demandCollectWindow=mini.get("demandCollectWindow");


    $(function () {
        if (action=='edit'||(action == 'task' && stageName == 'bjfzrfxfx')) {
        // if(action=='task'&&stageName=='bjfzrfxfx'){
            mini.get("addDemand").show();
            mini.get("demandCollect").show();
            mini.get("delDemand").show();
            $("#tips").show();
        }
    });

    function operationRenderer(e) {
        var record = e.record;
        if (!record.id) {
            return "";
        }
        var cellHtml = returnPreviewSpan(record.fileName, record.id, record.relDemandId, coverContent);
        var downloadUrl = '/drbfm/testTask/fileDownload.do';
        if (record) {
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="downLoadFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.relDemandId + '\',\'' + downloadUrl + '\')">下载</span>';
        }
        if (record && record.CREATE_BY_ == currentUserId) {
            var deleteUrl = "/drbfm/testTask/deleteFiles.do";
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.relDemandId + '\',\'' + deleteUrl + '\')">删除</span>';
        }
        return cellHtml;
    }

    function returnPreviewSpan(fileName, fileId, formId, coverContent) {
        var s = '';
        if (fileName == "") {
            return s;
        }
        var fileType = getFileType(fileName);
        if (fileType == 'other') {
            s = '&nbsp;&nbsp;&nbsp;<span  title="预览" style="color: silver" >预览</span>';
        } else {
            var url = '/drbfm/testTask/preview.do?fileType=' + fileType;
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf6(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\',\'' + fileType + '\')">预览</span>';
        }
        return s;
    }

    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var id = record.id;
        var CREATE_BY_ = record.CREATE_BY_;
        var s = '';
        s+='<span  style="color:#2ca9f6;cursor: pointer" title="查看" onclick="detailDeptDemand(\'' + id +'\')">查看</span>';
        if(action=='task'&&CREATE_BY_==currentUserId&&stageName=='bjfzrfxfx' && record.demandType=='自增'){
            s+='<span style="display: inline-block" class="separator"></span>';
            s+='<span  style="color:#2ca9f6;cursor: pointer" title="编辑" onclick="editDeptDemand(\'' + id +'\')">编辑</span>';
        }
        return s;
    }

    function renderDemandDesc(e) {
        var record = e.record;
        var html = "<div style='line-height: 20px;height:100px;overflow: auto' >";
        var demandDesc = record.demandDesc;
        if (demandDesc == null) {
            demandDesc = "";
        }
        html += '<span style="white-space:pre-wrap" >' + demandDesc + '</span>';
        html += '</div>'
        return html;
    }

    function renderReason(e) {
        var record = e.record;
        var html = "<div style='line-height: 20px;height:100px;overflow: auto' >";
        var demandReason = record.demandReason;
        if (!demandReason) {
            demandReason = "";
        }
        html += '<span style="white-space:pre-wrap" >' + demandReason + '</span>';
        html += '</div>'
        return html;
    }

    function addDeptDemand() {
        mini.get("demandReason").setValue('');
        mini.get("demandDesc").setValue('');
        mini.get("deptIds").setValue('');
        mini.get("deptIds").setText('');
        mini.get("id").setValue('');
        mini.get("demandType").setValue('');
        mini.get("demandDesc").setEnabled(true);
        mini.get("demandReason").setEnabled(true);
        mini.get("deptIds").setEnabled(true);
        mini.get("demandType").setValue("自增");
        $("#fileListToolBar").show();
        $("#saveDemand").show();
        demandWindow.show();
        var url=jsUseCtxPath+"/drbfm/single/demandFileList.do?relDemandId=";
        fileListGrid.setUrl(url);
        fileListGrid.load();
    }

    function deptDemandCollect() {
        mini.get("feedbackUserIds").setValue('');
        mini.get("feedbackUserIds").setText('');
        demandCollectWindow.show();
    }
    
    function deptDemandCollectSee() {
        var url = jsUseCtxPath + "/drbfm/single/deptDemandCollectListPage.do?belongSingleId=" + singleId;
        window.open(url);
    }

    function createDemandCollect() {
        var feedbackUserIds = mini.get("feedbackUserIds").getValue();
        if(!feedbackUserIds) {
            mini.alert("请选择需求反馈人！");
            return;
        }
        mini.confirm("流程创建后，每个反馈人将收到一个需求收集流程，只有需求收集流程成功结束或者作废，当前主流程才能进行下一步，确定继续？", "提醒",
            function (action) {
                if (action == "ok") {
                    closeDemandCollect();
                    showLoading();
                    var feedbackUserIds = mini.get("feedbackUserIds").getValue();
                    $.ajax({
                        url: jsUseCtxPath + '/drbfm/single/demandCollectStart.do?singleId=' + singleId
                            + "&feedbackUserIds=" + feedbackUserIds,
                        type: 'get',
                        contentType: 'application/json',
                        success: function (result) {
                            if (result) {
                                if(!result.success) {
                                    mini.alert(result.message);
                                } else {
                                    mini.confirm(result.message+"，是否查看流程？", "提示", function (action) {
                                        if (action != 'ok') {
                                            return;
                                        } else {
                                            var url = jsUseCtxPath + "/drbfm/single/deptDemandCollectListPage.do?belongSingleId=" + singleId;
                                            window.open(url);
                                        }
                                    });
                                }
                            }
                        },
                        complete: function () {
                            hideLoading();
                        }
                    });
                }
            }
        );
    }

    function showLoading() {
        $("#loading").css('display','');
        $("#content").css('display','none');
        mini.get("addDemand").hide();
        mini.get("demandCollect").hide();
        mini.get("delDemand").hide();
        $("#tips").hide();
    }

    function hideLoading() {
        $("#loading").css('display','none');
        $("#content").css('display','');
        mini.get("addDemand").show();
        mini.get("demandCollect").show();
        mini.get("delDemand").show();
        $("#tips").show();
    }

    function closeDemandCollect() {
        demandCollectWindow.hide();
    }

    function validDeptDemand() {
        var demandDesc = $.trim(mini.get("demandDesc").getValue())
        if (!demandDesc) {
            return {"result": false, "message": "请填写需求描述"};
        }
        var demandReason = $.trim(mini.get("demandReason").getValue())
        if (!demandReason) {
            return {"result": false, "message": "请填写需求理由/来源"};
        }
        var deptIds = $.trim(mini.get("deptIds").getValue())
        if (!deptIds) {
            return {"result": false, "message": "请选择需求提出部门"};
        }
        return {"result": true};
    }

    function saveDeptDemand() {
        var formValid = validDeptDemand();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        var demandDesc = mini.get("demandDesc").getValue();
        var id = mini.get("id").getValue();
        var deptIds = mini.get("deptIds").getValue();
        var deptNames = mini.get("deptIds").getText();
        var demandReason = mini.get("demandReason").getValue();
        var demandType = mini.get("demandType").getValue();
        var data = {id:id,demandDesc:demandDesc,deptIds:deptIds,deptNames:deptNames,belongSingleId:singleId,demandReason:demandReason,demandType:demandType};
        var json = mini.encode(data);
        $.ajax({
            url: jsUseCtxPath + '/drbfm/single/saveDeptDemand.do',
            type: 'POST',
            data: json,
            async: false,
            contentType: 'application/json',
            success: function (returnData) {
                if (returnData && returnData.message) {
                    mini.alert(returnData.message, '提示', function () {
                        if (returnData.success) {
                            deptDemandListGrid.reload();
                            mini.get("id").setValue(returnData.data);
                        }
                    });
                }
            }
        });
    }

    function detailDeptDemand(id) {
        demandWindow.show();
        if (id) {
            var url = jsUseCtxPath + "/drbfm/single/getSingleDeptDemandDetail.do";
            $.ajaxSettings.async = false;
            $.post(
                url,
                {id: id},
                function (json) {
                    mini.get("demandReason").setValue(json.demandReason);
                    mini.get("demandDesc").setValue(json.demandDesc);
                    mini.get("deptIds").setValue(json.deptIds);
                    mini.get("deptIds").setText(json.deptNames);
                    mini.get("id").setValue(id);
                    mini.get("demandType").setValue(json.demandType);
                });
            $.ajaxSettings.async = true;
        }
        mini.get("demandDesc").setEnabled(false);
        mini.get("demandReason").setEnabled(false);
        mini.get("deptIds").setEnabled(false);
        $("#saveDemand").hide();
        $("#fileListToolBar").hide();
        var url=jsUseCtxPath+"/drbfm/single/demandFileList.do?relDemandId="+id;
        fileListGrid.setUrl(url);
        fileListGrid.load();
    }

    function editDeptDemand(id) {
        demandWindow.show();
        if (id) {
            var url = jsUseCtxPath + "/drbfm/single/getSingleDeptDemandDetail.do";
            $.ajaxSettings.async = false;
            $.post(
                url,
                {id: id},
                function (json) {
                    mini.get("demandReason").setValue(json.demandReason);
                    mini.get("demandDesc").setValue(json.demandDesc);
                    mini.get("deptIds").setValue(json.deptIds);
                    mini.get("deptIds").setText(json.deptNames);
                    mini.get("id").setValue(id);
                    mini.get("demandType").setValue(json.demandType);
                });
            $.ajaxSettings.async = true;
        }
        mini.get("demandDesc").setEnabled(true);
        mini.get("demandReason").setEnabled(true);
        mini.get("deptIds").setEnabled(true);
        $("#saveDemand").show();
        $("#fileListToolBar").show();
        var url=jsUseCtxPath+"/drbfm/single/demandFileList.do?relDemandId="+id;
        fileListGrid.setUrl(url);
        fileListGrid.load();
    }

    function closeDeptDemand() {
        mini.get("demandReason").setValue('');
        mini.get("demandDesc").setValue('');
        mini.get("deptIds").setValue('');
        mini.get("deptIds").setText('');
        mini.get("id").setValue('');
        mini.get("demandType").setValue('');
        demandWindow.hide();
    }

    function removeDeptDemand() {
        var rows = deptDemandListGrid.getSelecteds();
        if (rows.length <= 0) {
            mini.alert("请至少选中一条记录");
            return;
        }
        mini.confirm("确定删除选中记录（“流程收集”类型的需求不允许删除）？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                var rowIds = [];
                for (var i = 0, l = rows.length; i < l; i++) {
                    var r = rows[i];
                    if(r.demandType=='自增') {
                        rowIds.push(r.id);
                    }
                }
                if(rowIds.length>0) {
                    _SubmitJson({
                        url: jsUseCtxPath + "/drbfm/single/deleteDeptDemand.do",
                        method: 'POST',
                        showMsg:false,
                        data: {ids: rowIds.join(',')},
                        success: function (data) {
                            if (data) {
                                mini.alert(data.message);
                                deptDemandListGrid.reload();
                            }
                        }
                    });
                }
            }
        });
    }

    function demandFileUpload() {
        var id = mini.get("id").getValue();
        if(!id){
            mini.alert("请先保存!");
            return;
        }
        mini.open({
            title: "添加附件",
            url: jsUseCtxPath + "/drbfm/single/openDemandWindow.do?relDemandId="+id+ "&belongSingleId=" + singleId,
            width: 850,
            height: 550,
            showModal:true,
            allowResize: true,
            ondestroy: function () {
                if(fileListGrid) {
                    var url=jsUseCtxPath+"/drbfm/single/demandFileList.do?relDemandId="+id;
                    fileListGrid.setUrl(url);
                    fileListGrid.load();
                }
            }
        });
    }
</script>
</body>
</html>
