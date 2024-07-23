
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
	<title>公共文档管理</title>
	<%@include file="/commons/list.jsp"%>
	<script src="${ctxPath}/scripts/standardManager/standardUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar" >
	<ul class="toolBtnBox">
		<li style="margin-right: 15px">
			<span class="text" style="width:auto"><spring:message code="page.standardAttachFilePage.name1" />：</span>
			<input id="systemCategoryId" name="systemCategoryId" class="mini-combobox" style="width:150px;font-size: 15px;"
				   textfield="systemCategoryName" valuefield="systemCategoryId"
				   required="false" allowInput="false" showNullItem="false" onvaluechanged="searchFrm()"
			/>
		</li>
		<li style="margin-right: 15px">
			<span class="text" style="width:auto"><spring:message code="page.standardAttachFilePage.name2" />: </span><input class="mini-textbox" id="docName" onkeyup="onKeyUp()"/>
		</li>
		<li >
			<span class="text" style="width:auto"><spring:message code="page.standardAttachFilePage.name8" />：</span><input class="mini-textbox" id="standardName" onkeyup="onKeyUp()"/>
			<a class="mini-button" iconCls="icon-search" onclick="searchAttachFileList()" ><spring:message code="page.standardAttachFilePage.name3" /></a>
			<span class="separator"></span>
			<a class="mini-button" iconCls="icon-download" onclick="clear()" ><spring:message code="page.standardAttachFilePage.name12" /></a>
			<span class="separator"></span>
			<a class="mini-button" iconCls="icon-download" onclick="exportStandard()" ><spring:message code="page.standardAttachFilePage.name4" /></a>
		</li>
	</ul>
</div>
<div class="mini-fit" style="height: 100%;">
	<div id="attachFileListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" url="${ctxPath}/standardManager/core/standardFileInfos/getAttachFileGridList.do"
		 sizeList="[50,100,200]" pageSize="50" idField="id" allowAlternating="true" multiSelect="false">
		<div property="columns">
			<div cellCls="actionIcons" width="60" headerAlign="center" align="center" renderer="targetFileInfoRenderer" cellStyle="padding:0;"><spring:message code="page.standardAttachFilePage.name5" /></div>
			<div field="fileName" headerAlign='center' align='center' width="200" ><spring:message code="page.standardAttachFilePage.name6" /></div>
			<div field="fileSize" headerAlign='center' align='center' width="60"><spring:message code="page.standardAttachFilePage.name7" /></div>
			<div field="standardName" headerAlign='center' align='center' width="100" renderer="jumpToDetail"><spring:message code="page.standardAttachFilePage.name8" /></div>
			<div field="fileDesc" headerAlign='center' align='center' width="60"><spring:message code="page.standardAttachFilePage.name9" /></div>
			<div field="creator" width="60" headerAlign='center' align="center"><spring:message code="page.standardAttachFilePage.name10" /></div>
			<div field="CREATE_TIME_" width="100" dateFormat="yyyy-MM-dd HH:mm:ss" headerAlign='center' align="center"><spring:message code="page.standardAttachFilePage.name11" /></div>
			<div field="relativePath" visible="false"></div>
		</div>
	</div>
</div>

<!--导出Excel相关HTML-->
<form id="excelForm" action="${ctxPath}/standardManager/core/standardFileInfos/exportAttachFileGrid.do" method="post"
	  target="excelIFrame">
	<input type="hidden" name="pageIndex" id="pageIndex"/>
	<input type="hidden" name="pageSize" id="pageSize"/>
	<input type="hidden" name="filter" id="filter"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var currentUserId="${currentUserId}";
    var attachFileListGrid = mini.get("attachFileListGrid");
    var systemCategoryValue="${systemCategoryValue}";

	$(function () {
        $.ajax({
            url: jsUseCtxPath +'/standardManager/core/standardSystem/queryCategory.do',
            success:function (data) {
                if(data) {
                    mini.get("systemCategoryId").load(data);
                    mini.get("systemCategoryId").setValue(systemCategoryValue);
                    searchAttachFileList();
                }
            }
        });
    });

	function clear() {
		mini.get("docName").setValue("");
		mini.get("standardName").setValue("");
		mini.get("systemCategoryId").setValue("GL");
		searchAttachFileList();
	}

    function searchAttachFileList() {
        var queryParam = [];
        //其他筛选条件
        var fileName = $.trim(mini.get("docName").getValue());
        if (fileName) {
            queryParam.push({name: "fileName", value: fileName});
        }
		var standardName = $.trim(mini.get("standardName").getValue());
		if (standardName) {
			queryParam.push({name: "standardName", value: standardName});
		}
        var systemCategoryId = $.trim(mini.get("systemCategoryId").getValue());
        if (systemCategoryId) {
            queryParam.push({name: "systemCategoryId", value: systemCategoryId});
        }
        var data = {};
        data.filter = mini.encode(queryParam);
        data.pageIndex = attachFileListGrid.getPageIndex();
        data.pageSize = attachFileListGrid.getPageSize();
        data.sortField = attachFileListGrid.getSortField();
        data.sortOrder = attachFileListGrid.getSortOrder();
        //查询
        attachFileListGrid.load(data);
	}

    attachFileListGrid.on("update",function(e){
        handGridButtons(e.sender.el);
    });

    function onKeyUp(ev) {
        var ev = ev || event;
        if(ev.keyCode == 13){
            searchAttachFileList();
        }
    }

    function targetFileInfoRenderer(e) {
        var record = e.record;
        var s='<span title=' + standardAttachFilePage_name1 + ' style="color:#409EFF;cursor: pointer;" onclick="downAttachFile('+JSON.stringify(record).replace(/"/g, '&quot;')+')">' + standardAttachFilePage_name1 + '</span>';
        if(currentUserId != record.CREATE_BY_) {
            s+='<span title=' + standardAttachFilePage_name2 + ' style="color:silver;">' + standardAttachFilePage_name2 + '</span>';
        } else {
            s+='<span title=' + standardAttachFilePage_name2 + ' style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteAttachFile('+JSON.stringify(record).replace(/"/g, '&quot;')+')">' + standardAttachFilePage_name1 + '</span>';
        }
        return s;
    }

    //下载文档
    function downAttachFile(record) {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + "/standardManager/core/standardFileInfos/fileDownload.do?action=download");
        var inputFileName = $("<input>");
        inputFileName.attr("type", "hidden");
        inputFileName.attr("name", "fileName");
        inputFileName.attr("value", record.fileName);
        var standardId = $("<input>");
        standardId.attr("type", "hidden");
        standardId.attr("name", "standardId");
        standardId.attr("value", record.standardId);
        var fileId = $("<input>");
        fileId.attr("type", "hidden");
        fileId.attr("name", "fileId");
        fileId.attr("value", record.id);
        $("body").append(form);
        form.append(inputFileName);
        form.append(standardId);
        form.append(fileId);
        form.submit();
        form.remove();
    }

    //删除文档
    function deleteAttachFile(record) {
        mini.confirm(standardAttachFilePage_name3, standardAttachFilePage_name4,
            function (action) {
                if (action == "ok") {
                    var url = jsUseCtxPath + "/standardManager/core/standardFileInfos/deleteStandardFiles.do";
                    var data = {
                        standardId: record.standardId,
                        id: record.id,
                        fileName: record.fileName
                    };
                    $.post(
                        url,
                        data,
                        function (json) {
                            searchAttachFileList();
                        });
                }
            }
        );
    }

    function jumpToDetail(e) {
        var record = e.record;
        var standardId = record.standardId;
        var s = '<a href="#" style="color:#44cef6;text-decoration:underline;" onclick="openStandardEditWindow(\'' + standardId +'\',\''+record.status+ '\')">'+record.standardName+'</a>';
        return s;
    }

    function openStandardEditWindow(standardId) {
        mini.open({
            title: standardAttachFilePage_name5,
            url: jsUseCtxPath + "/standardManager/core/standard/edit.do?standardId=" + standardId + '&action=detail',
            width: 800,
            height: 600,
            showModal: true,
            allowResize: true,
            onload: function () {
            },
            ondestroy: function (action) {
                searchAttachFileList();
            }
        });
    }

    //导出标准
    function exportStandard() {
        var queryParam = [];
        var fileName = $.trim(mini.get("docName").getValue());
        if (fileName) {
            queryParam.push({name: "fileName", value: fileName});
        }
		var standardName = $.trim(mini.get("standardName").getValue());
		if (standardName) {
			queryParam.push({name: "standardName", value: standardName});
		}
        var systemCategoryId = $.trim(mini.get("systemCategoryId").getValue());
        if (systemCategoryId) {
            queryParam.push({name: "systemCategoryId", value: systemCategoryId});
        }
        $("#filter").val(mini.encode(queryParam));
        var excelForm = $("#excelForm");
        excelForm.submit();
    }
</script>
</body>
</html>