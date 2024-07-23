
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
	<title>公共文档管理</title>
	<%@include file="/commons/list.jsp"%>
</head>
<body>
<div class="mini-toolbar" id="toolbar">
	<ul class="toolBtnBox">
		<li style="float: left">
			<a id="uploadBtn" class="mini-button" iconCls="icon-upload" onclick="openDocMgrUploadWindow()" plain="true">上传文件</a>
		</li>
	</ul>
</div>
<div class="mini-fit" style="height: 100%;">
	<div id="commonDocListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" url="${ctxPath}/materielExtend/file/listData.do"
		  idField="id" allowAlternating="true" showPager="false" multiSelect="false">
		<div property="columns">
			<div cellCls="actionIcons" width="60" headerAlign="center" align="center" renderer="actionOperate" cellStyle="padding:0;">操作</div>
			<div field="fileName" headerAlign='center' align='center' width="160" >文件名称</div>
			<div field="size" headerAlign='center' align='center' width="60">文件大小</div>
			<div field="FULLNAME_" width="80" headerAlign='center' align="center">创建人</div>
			<div field="CREATE_TIME_" width="100" dateFormat="yyyy-MM-dd" headerAlign='center' align="center">创建时间</div>
		</div>
	</div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var currentUserNo="${currentUserNo}";

    var commonDocListGrid = mini.get("commonDocListGrid");
    commonDocListGrid.load();
    //操作栏
    commonDocListGrid.on("update",function(e){
        handGridButtons(e.sender.el);
    });

    if(currentUserNo!='admin') {
        mini.get("toolbar").hide();
	}

    function actionOperate(e) {
        var record = e.record;
        var fileName=record.fileName;
        if(!fileName) {
            fileName='';
        }
        var s = '';
        s+='<span  title="下载" onclick="downLoadDoc(\'' +record.id+'\',\''+fileName+ '\')">下载</span>';
        if(currentUserNo=='admin') {
            s+='<span title="删除" onclick="deleteDoc(\''+record.id+'\',\''+fileName+'\')">删除</span>';
        }
        return s;
    }

    function downLoadDoc(fileId,fileName) {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + "/materielExtend/file/download.do");
        var inputFileName = $("<input>");
        inputFileName.attr("type", "hidden");
        inputFileName.attr("name", "fileName");
        inputFileName.attr("value", fileName);
        var id = $("<input>");
        id.attr("type", "hidden");
        id.attr("name", "id");
        id.attr("value", fileId);
        $("body").append(form);
        form.append(inputFileName);
        form.append(id);
        form.submit();
        form.remove();
    }

    function deleteDoc(id,fileName) {
        mini.confirm("确定删除？", "确定？",
            function (action) {
                if (action == "ok") {
                    $.ajax({
						url:jsUseCtxPath + "/materielExtend/file/delete.do?id="+id+"&fileName="+fileName,
						success:function (data) {
                            commonDocListGrid.load();
                        }
					});
                }
            }
        );
    }

    function openDocMgrUploadWindow() {
        mini.open({
            title: "文件上传",
            url: jsUseCtxPath + "/materielExtend/file/uploadPage.do",
            width: 750,
            height: 450,
            showModal:false,
            allowResize: true,
            onload: function () {
            },
            ondestroy: function (action) {
                commonDocListGrid.load();
            }
        });
    }
</script>

</body>
</html>