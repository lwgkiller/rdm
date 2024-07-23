<%--
  Created by IntelliJ IDEA.
  User: zhangwentao
  Date: 2023/9/23
  Time: 16:45
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>PDM故障知识库</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <%--                <a id="uploadFile" class="mini-button"  style="margin-bottom: 5px" onclick="uploadMethodFile">上传文件</a>--%>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">文件名称: </span>
                    <input class="mini-textbox" id="fileName" name="fileName"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">文件类型: </span>
<%--                    <input class="mini-textbox" id="fileType" name="fileType"/>--%>
                    <input id="fileType" name="fileType" class="mini-combobox" style="width:98%"
                           url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=GZKWJFL"
                           valueField="key" textField="value" nullItemText="请选择..." required="true"/>
                </li>

                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true"
                       onclick="clearForm()">清空查询</a>
                    <div style="display: inline-block" class="separator"></div>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="addMethod('add','')">新增</a>
                    <f:a alias="pdmOps_delete" id="deletedId" style="margin-right: 5px" onclick="removeMethod()"
                         showNoRight="false">删除</f:a>
                </li>
            </ul>
        </form>
        <span class="searchSelectionBtn" onclick="no_search(this ,'searchForm')">
				<i class="icon-sc-lower"></i>
			</span>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="PDMOpsDevListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         sizeList="[50,100,200]" pageSize="50"
         url="${ctxPath}/pdm/core/PDMOpsDev/applyList.do"
         idField="id" allowAlternating="true" showPager="true" multiSelect="true">
        <div property="columns">
            <div type="checkcolumn" width="10"></div>
            <div name="action" cellCls="actionIcons" width="100" headerAlign="center" align="center"
                 renderer="onActionRenderer" cellStyle="padding:0;">操作
            </div>
            <div field="fileName" width="80" headerAlign='center' align='left'>文件名称</div>
            <div field="fileType" width="30" headerAlign="center" align="left" allowSort="true">文件标签</div>
            <div field="fileDirection" headerAlign='center' align='left'>用途说明</div>
<%--            <div field="filePosition" headerAlign='center' align='left' width="50" renderer="downloadAdress">网址链接</div>--%>
            <div field="fileSize" width="30" headerAlign="center" align="left" allowSort="true">文件大小</div>
            <div field="creator" width="50" headerAlign='center' align='left'>创建人</div>
            <div field="CREATE_TIME_" width="50"  headerAlign='center' align="left">创建时间</div>
        </div>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var PDMOpsDevListGrid = mini.get("PDMOpsDevListGrid");
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var currentTime = "${currentTime}";
    var coverContent = currentUserNo + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";
    function uploadMethodFile() {
        mini.open({
            title: "文件上传",
            url: jsUseCtxPath + "/pdm/core/PDMOpsDev/openUploadWindow.do" ,
            width: 930,
            height: 450,
            showModal: false,
            allowResize: true,
            onload:function () {
            },
            ondestroy: function (action) {
                searchFrm();
            }
        });
    }

    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var s = '';
        s = returnPreviewSpan(record.fileName, record.id,record.id, coverContent);
        var downLoadUrl = '/pdm/core/PDMOpsDev/pdfPreviewAndAllDownload.do';
        s +='&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
        'onclick="downLoadFile(\'' + record.fileName + '\',\''  + record.id + '\',\'' + record.id + '\',\''+ downLoadUrl + '\')">下载</span>';
        if(currentUserId ==record.CREATE_BY_ ||currentUserNo =='admin'){
            s += '<span  title="删除" onclick="removeMethod(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
        }
        return s;
    }
    function returnPreviewSpan(fileName, fileId, formId, coverContent) {
        var fileType = getFileType(fileName);
        var s = '';
         if (fileType == 'pdf') {
            var url = '/pdm/core/PDMOpsDev/pdfPreviewAndAllDownload.do';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        } else if (fileType == 'office') {
            var url = '/pdm/core/PDMOpsDev/officePreview.do';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        } else if (fileType == 'pic') {
            var url = '/pdm/core/PDMOpsDev/imagePreview.do';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        }
        return s;
    }
/*
* 清空查询
* */
    function clearForm(){

        mini.get("fileName").setValue("");
        mini.get("fileType").setValue("");
        PDMOpsDevListGrid.load();

    }
    function downloadAdress(e) {
        var record = e.record;
        var url = record.filePosition;
        var linkStr='<a href="#" style="color:#0044BB;" onclick="window.open(\'' + url+ '\')">'+url+'</a>';
        return linkStr;
    }
    /**
     * 添加弹窗
     */
    function addMethod(action, id){

        var title = "";
        if (action == "add") {
            title = "文档添加"
        }else {
            title = "文档修改"
        }
        mini.open({
            title: title,
            url: jsUseCtxPath + "/pdm/core/PDMOpsDev/edit.do?id=" + id + '&action='+ action ,
            width: 800,
            height: 600,
            showModal: true,
            allowResize: true,
            onload: function () {
            },
            ondestroy: function (action) {
                searchFrm();
            }
            }
        );
    }


    /**
     * 删除
     * @param record
     */
    function removeMethod(record) {

        var rows = [];
        if (record) {
            rows.push(record);
        } else {
            rows = PDMOpsDevListGrid.getSelecteds();
        }
        if (rows.length <= 0) {
            mini.alert("请至少选中一条记录");
            return;
        }
        mini.confirm("确定删除选中记录？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                var rowIds = [];
                var fileIds = [];
                for (var i = 0, l = rows.length; i < l; i++) {
                    var r = rows[i];
                    rowIds.push(r.id);
                }

                _SubmitJson({
                    url: jsUseCtxPath + "/pdm/core/PDMOpsDev/deleteFile.do",
                    method: 'POST',
                    showMsg: false,
                    data: {ids: rowIds.join(',')},
                    success: function (data) {
                        if (data) {
                            mini.alert(data.message);
                            PDMOpsDevListGrid.load();
                        }
                    }
                });
            }
        });
    }

</script>
<redxun:gridScript gridId="PDMOpsDevListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
