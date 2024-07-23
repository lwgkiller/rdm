
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
    <title>研发月刊</title>
    <%@include file="/commons/list.jsp"%>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar" >
    <ul class="toolBtnBox">
        <li style="float: left" id="operateTool">
            <a id="uploadBtn" class="mini-button" iconCls="icon-upload" onclick="fileWindows()" plain="true"><spring:message code="page.zlghMonthly.name" /></a>
            <span class="separator"></span>
        </li>
        <li >
            <span class="text" style="width:auto"><spring:message code="page.zlghMonthly.name1" />: </span><input class="mini-textbox" id="docName"  onenter="searchMonth()"/>
            <a class="mini-button" iconCls="icon-search" onclick="searchMonth()" plain="true"><spring:message code="page.zlghMonthly.name2" /></a>
        </li>
    </ul>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="monthlyListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="true" sizeList="[20,50,100,200]" pageSize="20"
         idField="id" allowAlternating="true" showPager="true" multiSelect="false">
        <div property="columns">
            <div cellCls="actionIcons" width="60" headerAlign="center" align="center" renderer="actionOperate" cellStyle="padding:0;"><spring:message code="page.zlghMonthly.name3" /></div>
            <div field="yk_time" headerAlign='center' align='center' width="100" dateFormat="yyyy-MM" ><spring:message code="page.zlghMonthly.name4" /></div>
            <div field="fileName" headerAlign='center' align='center' width="100" ><spring:message code="page.zlghMonthly.name5" /></div>
            <div field="fileSize" headerAlign='center' align='center' width="60"><spring:message code="page.zlghMonthly.name6" /></div>
            <div field="description" headerAlign='center' align='center' width="100"><spring:message code="page.zlghMonthly.name7" /></div>
            <div field="creator" width="60" headerAlign='center' align="center"><spring:message code="page.zlghMonthly.name8" /></div>
            <div field="CREATE_TIME_" width="100" dateFormat="yyyy-MM-dd HH:mm:ss" headerAlign='center' align="center"><spring:message code="page.zlghMonthly.name9" /></div>
            <div field="relativePath" visible="false"></div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var url="${ctxPath}/strategicplanning/core/monthly/getMonthlyList.do?docName=";
    var currentUserName="${currentUserName}";
    var currentTime="${currentTime}";
    var coverContent=currentUserName+"<br/>"+currentTime+"<br/>" + xzxgwjjxyxgs;
    var currentUserNo="${currentUserNo}";
    var currentUserId="${currentUserId}";
    var fileListGrid = mini.get("monthlyListGrid");
    var type = "${type}";
    searchMonth();

    function searchMonth() {
        fileListGrid.setUrl(url+mini.get("#docName").getValue()+"&type="+type);
        fileListGrid.load();
    }
    //操作栏
    fileListGrid.on("update",function(e){
        handGridButtons(e.sender.el);
    });

    function actionOperate(e) {
        var record = e.record;
        var fileName=record.fileName;
        if(!fileName) {
            fileName='';
        }
        var fileId=record.id;
        if(!fileId) {
            fileId='';
        }
        var downloadUrl='/strategicplanning/core/monthly/monthlyDownload.do';
        var deleteUrl='/strategicplanning/core/monthly/delMonthlyFile.do';
        var s = '';
        s+=returnGjllPreviewSpan(fileName,fileId,'',coverContent);
        /*s+='<span  title="下载" onclick="downLoadFile(\'' +fileName+'\',\''+fileId+'\',\'\',\''+downloadUrl+ '\')">下载</span>';*/
        if(currentUserNo=='admin'||record.CREATE_BY_==currentUserId) {
            s+='<span title=' + zlghMonthly_name + ' onclick="deleteFile(\''+fileName+'\',\''+fileId+'\',\'\',\''+deleteUrl+'\')">' + zlghMonthly_name + '</span>';
        }
        return s;
    }

    function returnGjllPreviewSpan(fileName,fileId,formId,coverContent) {
        var fileType=getFileType(fileName);
        var s='';
        if(fileType=='other'){
            s = '<span  title=' + zlghMonthly_name1 + ' style="color: silver" >' + zlghMonthly_name1 + '</span>';
        }else if(fileType=='pdf'){
            var url='/strategicplanning/core/monthly/monthlyPdfPreview.do';
            s = '<span  title=' + zlghMonthly_name1 + ' style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' +fileName+'\',\''+fileId+'\',\''+formId+'\',\''+coverContent+'\',\''+url+ '\')">' + zlghMonthly_name1 + '</span>';
        }/*else if(fileType=='office'){
            var url='/strategicplanning/core/monthly/monthlyPdfPreview.do';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' +fileName+'\',\''+fileId+'\',\''+formId+'\',\''+coverContent+'\',\''+url +'\')">预览</span>';
        }else if(fileType=='pic'){
            var url='/strategicplanning/core/monthly/monthlyPdfPreview.do';
            s = '<span  title="预览" style="color:#409EFF;curs' +
                'or: pointer;" onclick="previewPic(\'' +fileName+'\',\''+fileId+'\',\''+formId+'\',\''+coverContent+'\',\''+url + '\')">预览</span>';
        }*/
        return s;
    }
    //打开文件上传窗口
    function fileWindows() {
        mini.open({
            title: zlghMonthly_name2,
            url: jsUseCtxPath + "/strategicplanning/core/monthly/templateUploadWindow.do?type="+type,
            width: 850,
            height: 600,
            showModal:false,
            allowResize: true,
            onload: function () {
            },
            ondestroy: function (action) {
                searchMonth();
            }
        });
    }


</script>
</body>
</html>