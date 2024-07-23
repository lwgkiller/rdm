<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>部门需求收集编辑页面</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
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
        <form id="demandCollectForm" method="post">
            <input class="mini-hidden" id="demandCollectId" name="id"/>
            <input class="mini-hidden" id="belongSingleId" name="belongSingleId"/>
            <input id="instId" name="instId" class="mini-hidden"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0">
                <caption style="font-weight: bold;font-size: 20px !important;">
                    部门需求收集编辑页面
                </caption>
                <tr>
                    <td style="width: 10%">部件/接口名称：</td>
                    <td style="width: 40%">
                        <input id="structName" name="structName" class="mini-textbox" style="width:98%;"
                               enabled="false"/>
                    </td>
                    <td style="width: 10%">部件/接口类型：</td>
                    <td style="width: 40%">
                        <input id="structType" name="structType" class="mini-textbox" style="width:98%;"
                               enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 10%">部件/接口验证项目编号：</td>
                    <td style="width: 40%">
                        <input id="singleNumber" name="singleNumber" class="mini-textbox" style="width:98%;"
                               enabled="false"/>
                    </td>
                    <td style="width: 10%">部件/接口编号：</td>
                    <td style="width: 40%">
                        <input id="structNumber" name="structNumber"
                               class="mini-textbox"
                               style="width:98%;" enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 10%">设计型号：</td>
                    <td style="width: 40%">
                        <input id="jixing" name="jixing" class="mini-textbox" style="width:98%;"
                               enabled="false"/>
                    </td>
                    <td style="width: 10%">需求收集发起人：</td>
                    <td style="width: 40%">
                        <input id="creator" name="creator" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 10%">需求反馈部门：</td>
                    <td style="width: 40%;">
                        <input class="mini-hidden" id="demandFeedBackDeptId" name="demandFeedBackDeptId"/>
                        <input id="demandFeedBackDeptName" name="demandFeedBackDeptName" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                    <td style="width: 10%">需求反馈人：</td>
                    <td style="width: 40%;">
                        <input id="demandFeedBackUserName" name="demandFeedBackUserName" class="mini-textbox" style="width:98%;" enabled="false"/>
                        <input class="mini-hidden" id="demandFeedBackUserId" name="demandFeedBackUserId"/>
                    </td>
                </tr>

                <tr>
                    <td style="text-align: left;width: 20%;height:500px">需求列表：</td>
                    <td colspan="3" >
                        <div class="mini-toolbar" style="margin-bottom: 5px" id="demandCollectListToolBar">
                            <a class="mini-button" plain="true" style="float: left;margin-right: 5px"
                               onclick="addDemand()">添加</a>
                            <a class="mini-button btn-red" plain="true" style="float: left"
                               onclick="removeDemand()">删除</a>
                        </div>
                        <div id="demandListGrid" class="mini-datagrid" allowResize="false" style="height: 86%"
                             idField="id" allowCellEdit="false" allowCellSelect="true" allowSortColumn="false"
                             multiSelect="true" showColumnsMenu="false" allowAlternating="true" showPager="false"
                             allowCellWrap="true" showVGridLines="true" autoload="true"
                             url="${ctxPath}/drbfm/single/getOneCollectDemandList.do?belongCollectFlowId=${applyId}">
                            <div property="columns">
                                <div type="checkcolumn" width="60px"></div>
                                <div name="action" cellCls="actionIcons" width="100px" headerAlign="center" align="center"
                                     renderer="demandActionRenderer" cellStyle="padding:0;">操作</div>
                                <div field="demandDesc" headerAlign="center" width="600px" align="left"
                                     renderer="renderDemandDesc">需求描述
                                </div>
                                <div field="demandReason" headerAlign="center" width="400px" align="left"
                                     renderer="renderReason">需求理由/来源
                                </div>
                            </div>
                        </div>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>

<div id="demandEditWindow" title="需求编辑" class="mini-window" style="width:750px;height:400px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
    <div class="mini-fit">
        <div class="topToolBar" style="float: right;">
            <div style="position: relative!important;">
                <a id="saveDemand" name="saveDemand" class="mini-button" onclick="saveDeptDemand()">保存</a>
                <a id="closeDemand" class="mini-button btn-red" onclick="closeDeptDemand()">关闭</a>
            </div>
        </div>
        <input id="id" name="id" class="mini-hidden"/>
        <table class="table-detail"  cellspacing="1" cellpadding="0" style="width: 99%">
            <tr>
                <td style="width: 12%">需求描述(500字以内)：<span style="color:red">*</span></td>
                <td colspan="3">
                    <input id="demandDesc" name="demandDesc" class="mini-textarea"
                           plugins="mini-textarea" style="width:99%;;height:120px;line-height:25px;" label="需求描述"
                           allowinput="true" emptytext="请输入需求描述..." />
                </td>
            </tr>
            <tr>
                <td style="width: 10%">需求理由/来源(500字以内)：<span style="color:red">*</span></td>
                <td colspan="3">
                    <input id="demandReason" name="demandReason" class="mini-textarea"
                           plugins="mini-textarea" style="width:99%;;height:120px;line-height:25px;" label="需求理由/来源"
                           allowinput="true" emptytext="请输入需求理由/来源..." />
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
                            <div type="indexcolumn" align="center" width="30">序号</div>
                            <div field="fileName" width="140" headerAlign="center" align="center">附件名称</div>
                            <div field="fileSize" width="60" headerAlign="center" align="center">附件大小</div>
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

<script type="text/javascript">
    mini.parse();
    var action = "${action}";
    var status = "${status}";
    var jsUseCtxPath = "${ctxPath}";
    var demandCollectForm = new mini.Form("#demandCollectForm");
    var stageName = "${stageName}";
    var applyId = "${applyId}";
    var demandListGrid = mini.get("demandListGrid");
    var demandEditWindow=mini.get("demandEditWindow");
    var currentUserId = "${currentUserId}";
    var currentUserName = "${currentUserName}";
    var currentTime = "${currentTime}";
    var fileListGrid = mini.get("fileListGrid");
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";


    $(function () {
        var url = jsUseCtxPath + "/drbfm/single/getDeptDemandCollectJson.do";
        if(applyId) {
            $.post(
                url,
                {id: applyId},
                function (json) {
                    demandCollectForm.setData(json);
                    if (action == 'detail') {
                        demandCollectForm.setEnabled(false);
                        $("#detailToolBar").show();
                        if (status != 'DRAFTED') {
                            $("#processInfo").show();
                        }
                        $("#demandCollectListToolBar").hide();
                    } else if (action == 'task') {
                        taskActionProcess();
                    }
                }
            );
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
        if (record && record.CREATE_BY_ == currentUserId&&action!="detail") {
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

    function getData() {
        var formData = _GetFormJsonMini("demandCollectForm");
        return formData;

    }

    function demandCollectApprove() {
        var formValid = validForm();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        window.parent.approve();
    }

    function validForm() {
        var demandListData = demandListGrid.getData();
        if(demandListData.length==0) {
            return {"result": false, "message": "请增加需求！"};
        }

        return {"result": true};
    }

    function taskActionProcess() {
        //获取上一环节的结果和处理人
        bpmPreTaskTipInForm();
        //获取环境变量
        if (action=='task' && stageName == 'bianzhi') {
            $("#demandCollectListToolBar").show();
        }
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

    function renderDemandDesc(e) {
        var record = e.record;
        var html = "<div style='line-height: 20px;height:120px;overflow: auto' >";
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
        var html = "<div style='line-height: 20px;height:120px;overflow: auto' >";
        var demandReason = record.demandReason;
        if (!demandReason) {
            demandReason = "";
        }
        html += '<span style="white-space:pre-wrap" >' + demandReason + '</span>';
        html += '</div>'
        return html;
    }

    function demandActionRenderer(e) {
        var record = e.record;
        var s = '';
        s+='<span  style="color:#2ca9f6;cursor: pointer" title="明细" onclick="detailDeptDemand('+JSON.stringify(record).replace(/"/g, '&quot;')+')">明细</span>';
        s+='<span style="display: inline-block" class="separator"></span>';
        if(action!='task' || stageName !='bianzhi') {
            s+='<span   style="color: silver" >编辑</span>';
            return s;
        }
        s+='<span  style="color:#2ca9f6;cursor: pointer" title="编辑" onclick="editDeptDemand('+JSON.stringify(record).replace(/"/g, '&quot;')+')">编辑</span>';
        return s;
    }

    function detailDeptDemand(record) {
        demandEditWindow.show();
        $("#fileListToolBar").hide();
        $("#saveDemand").hide();
        mini.get("demandDesc").setEnabled(false);
        mini.get("demandReason").setEnabled(false);
        if (record) {
            mini.get("id").setValue(record.id);
            mini.get("demandDesc").setValue(record.demandDesc);
            mini.get("demandReason").setValue(record.demandReason);
            var url=jsUseCtxPath+"/drbfm/single/demandFileList.do?relDemandId="+record.id;
            fileListGrid.setUrl(url);
            fileListGrid.load();
        }
    }

    function editDeptDemand(record) {
        demandEditWindow.show();
        $("#fileListToolBar").show();
        $("#saveDemand").show();
        if (record) {
            mini.get("id").setValue(record.id);
            mini.get("demandDesc").setValue(record.demandDesc);
            mini.get("demandReason").setValue(record.demandReason);
            var url=jsUseCtxPath+"/drbfm/single/demandFileList.do?relDemandId="+record.id;
            fileListGrid.setUrl(url);
            fileListGrid.load();
        }
    }

    function saveDeptDemand() {
        var demandDesc = $.trim(mini.get("demandDesc").getValue())
        if (!demandDesc) {
            mini.alert("请填写需求描述");
            return;
        }
        var demandReason = $.trim(mini.get("demandReason").getValue())
        if (!demandReason) {
            mini.alert("请填写需求理由/来源");
            return;
        }
        var id = mini.get("id").getValue();
        if(!id) {
            id='';
        }
        var demandType = '流程收集';
        var belongSingleId = mini.get("belongSingleId").getValue();
        var belongCollectFlowId = mini.get("demandCollectId").getValue();
        var deptIds = mini.get("demandFeedBackDeptId").getValue();
        var deptNames = mini.get("demandFeedBackDeptName").getValue();
        var data = {id:id,demandDesc:demandDesc,deptIds:deptIds,deptNames:deptNames,belongSingleId:belongSingleId,demandReason:demandReason,demandType:demandType,belongCollectFlowId:belongCollectFlowId};
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
                            demandListGrid.reload();
                            mini.get("id").setValue(returnData.data);
                        }
                    });
                }
            }
        });
    }

    function closeDeptDemand() {
        mini.get("demandReason").setValue('');
        mini.get("demandDesc").setValue('');
        mini.get("id").setValue('');
        demandEditWindow.hide();
    }

    function addDemand() {
        demandEditWindow.show();
        $("#fileListToolBar").show();
        var url=jsUseCtxPath+"/drbfm/single/demandFileList.do?relDemandId=";
        fileListGrid.setUrl(url);
        fileListGrid.load();
    }

    function removeDemand() {
        var rows = demandListGrid.getSelecteds();
        if (rows.length <= 0) {
            mini.alert("请至少选中一条记录");
            return;
        }
        mini.confirm("确定删除选中记录？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                var rowIds = [];
                for (var i = 0, l = rows.length; i < l; i++) {
                    var r = rows[i];
                    rowIds.push(r.id);
                }

                _SubmitJson({
                    url: jsUseCtxPath + "/drbfm/single/deleteDeptDemand.do",
                    method: 'POST',
                    showMsg:false,
                    data: {ids: rowIds.join(',')},
                    success: function (data) {
                        if (data) {
                            mini.alert(data.message);
                            demandListGrid.reload();
                        }
                    }
                });
            }
        });
    }

    function demandFileUpload() {
        var id = mini.get("id").getValue();
        var belongSingleId = mini.get("belongSingleId").getValue();
        if(!id){
            mini.alert("请先保存!");
            return;
        }
        mini.open({
            title: "添加附件",
            url: jsUseCtxPath + "/drbfm/single/openDemandWindow.do?relDemandId="+id+ "&belongSingleId=" + belongSingleId,
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
