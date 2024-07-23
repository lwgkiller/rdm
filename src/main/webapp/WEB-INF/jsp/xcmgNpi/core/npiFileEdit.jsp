<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<title>NPI文件资源编辑</title>
	<%@include file="/commons/edit.jsp"%>
	<script src="${ctxPath}/scripts/xcmgNpi/npiFileEdit.js?version=${static_res_version}"
			type="text/javascript"></script>
</head>

<body>
<div id="detailToolBar" class="topToolBar">
	<div>
		<a id="saveStandard" class="mini-button" onclick="saveNpiFile()">保存</a>
		<a id="closeWindow" class="mini-button btn-red" onclick="CloseWindow()">关闭</a>
	</div>
</div>
<div class="mini-fit">
	<div class="form-container" style="margin: 0 auto">
		<form id="formStandard" method="post">
			<input id="id" name="id" class="mini-hidden"/>
			<table class="table-detail" cellspacing="1" cellpadding="0">
				<tr>
					<td style="width: 14%">活动名称（中文）：<span style="color: #ff0000">*</span></td>
					<td style="width: 36%;min-width:140px">
                        <input id="fileName" name="fileName"  class="mini-textbox" style="width:98%;" />
					</td>
                </tr>
                <tr>
                    <td style="width: 14%">活动名称（英文）：</td>
                    <td style="width: 36%;min-width:140px">
                        <input id="fileNameEn" name="fileNameEn"  class="mini-textbox" style="width:98%;" />
                    </td>
				</tr>
				<tr>
					<td style="width: 14%">流程阶段：<span style="color: #ff0000">*</span></td>
					<td style="width: 36%;min-width:170px">
                        <input id="stageId" name="stageDicId" class="mini-combobox" style="width:98%;"
                               textField="name" valueField="dicId" emptyText="请选择..."
                               required="false" allowInput="false" showNullItem="false" nullItemText="请选择..."
                        />
					</td>
                </tr>
                <tr>
					<td style="width: 14%">活动类型：<span style="color: #ff0000">*</span></td>
					<td style="width: 36%;">
						<input id="systemTreeSelectId" style="width:98%;" class="mini-buttonedit" name="systemId" textname="systemName" allowInput="false" onbuttonclick="selectSystem()"/>
					</td>
				</tr>

				<tr style="height: 80px">
					<td style="width: 14%">附件：<span style="color: #ff0000">*</span></td>
					<td >
						<input class="mini-textbox" style="width:70%;float: left" id="fileObjName" name="fileObjName" readonly />
						<input id="inputFile" style="display:none;" type="file" onchange="getSelectFile()" accept="application/pdf" />
						<a id="uploadFileBtn" class="mini-button"  style="float: left;margin-left: 10px" onclick="uploadFile"><spring:message code="page.standardEdit.name24" /></a>
						<a id="clearFileBtn" class="mini-button"  style="float: left;margin-left: 10px" onclick="clearUploadFile"><spring:message code="page.standardEdit.name25" /></a>
					</td>
				</tr>
			</table>
		</form>
	</div>
	<div id="selectSystemWindow" title="<spring:message code="page.standardEdit.name26" />" class="mini-window" style="width:750px;height:450px;"
		 showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
		<div class="mini-toolbar" style="text-align:left;line-height:30px;"
			 borderStyle="border-left:0;border-top:0;border-right:0;">
            <span style="font-size: 14px;color: #777">名称: </span>
            <input class="mini-textbox" width="200" id="filterNameId" onenter="filterSystemTree()"/>
            <a class="mini-button"plain="true" onclick="filterSystemTree()">搜索</a>
            <a class="mini-button"  onclick="expandFileAll()">展开</a>
            <a class="mini-button"  onclick="refreshSystemTree()">刷新</a>
		</div>
		<div class="mini-fit">
			<ul id="selectSystemTree" class="mini-tree" style="width:100%;height:100%;"
                url="${ctxPath}/xcmgNpi/core/npiFile/treeQuery.do" ondrawnode="onDrawNode" onnodedblclick="leafNodeDbClick()"
                showTreeIcon="true" textField="systemName" expandOnLoad="1" idField="id" parentField="parentId"
                resultAsTree="false">
			</ul>
		</div>
		<div property="footer" style="padding:5px;height: 40px">
			<table style="width:100%;height: 100%">
				<tr>
					<td style="width:120px;text-align:center;">
						<input type="button" style="height: 25px;width: 70px" value="<spring:message code="page.standardEdit.name28" />" onclick="selectSystemOK()"/>
						<input type="button" style="height: 25px;width: 70px" value="<spring:message code="page.standardEdit.name29" />" onclick="selectSystemHide()"/>
					</td>
				</tr>
			</table>
		</div>
	</div>
</div>

<script type="text/javascript">
    mini.parse();
    var action="${action}";
    var jsUseCtxPath="${ctxPath}";
    var formStandard = new mini.Form("#formStandard");
    var selectSystemTree=mini.get("selectSystemTree");
    var selectSystemWindow=mini.get("selectSystemWindow");
    var recordId = "${recordId}";


    function setData(data) {
		if(data.systemTreeData) {
            treeData = mini.clone(data.systemTreeData);
            selectSystemTree.loadData(treeData);
		} else {
		    var url=jsUseCtxPath+'/standardManager/core/standardSystem/treeQuery.do?systemCategoryId='+systemCategoryId;
            selectSystemTree.setUrl(url);
            selectSystemTree.load();
		 }
        isPointManager=data.isPointManager;
        currentUserSubManager=data.currentUserSubManager;
    }

    function statusRenderer(e) {
        var record = e.record;
        var status = record.standardStatus;

        var arr = [{'key': 'draft', 'value': '草稿', 'css': 'orange'},
            {'key': 'enable', 'value': '有效', 'css': 'green'},
            {'key': 'disable', 'value': '已废止', 'css': 'red'}
        ];

        return $.formatItemValue(arr, status);
    }

    function onDrawNode(e) {
        var node = e.node;
        e.nodeHtml = node.systemName;
        if (node.systemNameEn) {
            e.nodeHtml+="【"+node.systemNameEn+"】";
        }
    }

    function expandFileAll() {
        selectSystemTree.expandAll();
    }

    function refreshSystemTree() {
        selectSystemTree.load();
    }


    function leafNodeDbClick() {
        selectSystemOK();
    }
</script>
</body>
</html>
