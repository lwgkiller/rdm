<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<title>标准编辑</title>
	<%@include file="/commons/edit.jsp"%>
	<link href="${ctxPath}/styles/formStandardlist.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css" />
	<script src="${ctxPath}/scripts/standardManager/standardEdit.js?version=${static_res_version}"
			type="text/javascript"></script>
	<script src="${ctxPath}/scripts/standardManager/standardUtil.js?version=${static_res_version}"
			type="text/javascript"></script>
</head>

<body>
<div id="detailToolBar" class="topToolBar">
	<div>
		<a id="stopAndNew" style="display: none" class="mini-button" onclick="stopAndNew()"><spring:message code="page.standardEdit.name" /></a>
		<a id="updateNote" style="display: none" class="mini-button" onclick="updateNote()"><spring:message code="page.standardEdit.name1" /></a>
		<a id="saveStandard" class="mini-button" onclick="saveStandard()"><spring:message code="page.standardEdit.name2" /></a>
		<a id="standardAttachFile" class="mini-button" onclick="showAttachDialog()"><spring:message code="page.standardEdit.name3" /></a>
		<a id="publishNotice" class="mini-button" onclick="publishNotice()"><spring:message code="page.standardEdit.name4" /></a>
		<a id="closeWindow" class="mini-button btn-red" onclick="CloseWindow()"><spring:message code="page.standardEdit.name5" /></a>
	</div>
</div>
<div class="mini-fit">
	<div class="form-container" style="margin: 0 auto">
		<form id="formStandard" method="post">
			<input id="id" name="id" class="mini-hidden"/>
			<input id="systemCategoryId" name="systemCategoryId" class="mini-hidden"/>
			<table class="table-detail" cellspacing="1" cellpadding="0">
				<tr>
					<td style="width: 14%"><spring:message code="page.standardEdit.name6" />：<span style="color: #ff0000">*</span></td>
					<td style="width: 36%;min-width:140px">
						<input id="standardCategoryId" class="mini-combobox" style="width:98%;" name="standardCategoryId"
							   textField="categoryName" valueField="id" required="false" allowInput="false" onvaluechanged="autoNumber()"
						/>
					</td>
					<td style="width: 14%"><spring:message code="page.standardEdit.name7" />：<span style="color: #ff0000">*</span></td>
					<td style="width: 36%;min-width:170px">
						<input id="standardNumber" name="standardNumber"  class="mini-textbox" style="width:98%;" />
					</td>
				</tr>
				<tr>
					<td style="width: 14%"><spring:message code="page.standardEdit.name8" />：<span style="color: #ff0000">*</span></td>
					<td style="width: 36%;min-width:140px">
						<input id="standardName" name="standardName"  class="mini-textbox" style="width:98%;" />
					</td>
					<td style="width: 14%"><spring:message code="page.standardEdit.name9" />：</td>
					<td style="width: 36%;">
						<input id="belongDepId" class="mini-combobox" style="width:98%;" name="belongDepId"
							   textField="belongDepName" valueField="id" required="false" allowInput="false"
						/>
					</td>

				</tr>
				<tr>
					<td style="width: 14%"><spring:message code="page.standardEdit.name10" />：<span style="color: #ff0000">*</span></td>
					<td style="width: 36%;min-width:170px">
						<input id="standardStatus" class="mini-combobox" style="width:98%;" name="standardStatus"
							   textField="value" valueField="key" required="false" allowInput="false"
							   data="[ {'key' : 'enable','value' : '启用'},{'key' : 'disable','value' : '废止'},{'key' : 'draft','value' : '草稿'}]"
							   onvaluechanged="setPublishOrStopTimeDefault()"
						/>
					</td>
					<td style="width: 14%"><spring:message code="page.standardEdit.name11" />：<span style="color: #ff0000">*</span></td>
					<td style="width: 36%;">
						<input id="systemTreeSelectId" style="width:98%;" class="mini-buttonedit" name="systemId" textname="systemName" allowInput="false" onbuttonclick="selectSystem()"/>
					</td>
				</tr>

				<tr>
					<td style="width: 14%"><spring:message code="page.standardEdit.name12" />：</td>
					<td style="width: 36%;">
						<input id="replaceNumberId" style="width:98%;" class="mini-buttonedit" showClose="true" oncloseclick="onSelectStandardCloseClick('replace')" name="replaceId" textname="replaceNumber" allowInput="false" onbuttonclick="selectStandard('replace')"/>
					</td>
					<td style="width: 14%"><spring:message code="page.standardEdit.name13" />：</td>
					<td style="width: 36%;">
						<input id="beReplaceNumberId" style="width:98%;" class="mini-buttonedit" showClose="true" oncloseclick="onSelectStandardCloseClick('beReplace')"name="beReplacedById" textname="beReplaceNumber" allowInput="false" onbuttonclick="selectStandard('beReplace')"/>
					</td>
				</tr>

				<tr>
					<td style="width: 14%"><spring:message code="page.standardEdit.name14" />：</td>
					<td style="width: 36%;">
						<input id="publisher" name="publisherId" textname="publisherName" property="editor" class="mini-user rxc" plugins="mini-user"  style="width:98%;height:34px;" allowinput="false" length="1000" maxlength="1000"  mainfield="no"  single="false" />
					</td>
					<td style="width: 14%"><spring:message code="page.standardEdit.name15" />：</td>
					<td style="width: 36%;">
						<input id="publishTimeId" name="publishTime"  class="mini-datepicker" format="yyyy-MM-dd HH:mm:ss" timeFormat="HH:mm:ss" showTime="true" showOkButton="true" showClearButton="false" style="width:98%"/>
					</td>
				</tr>
				<tr>
					<td style="width: 14%"><spring:message code="page.standardEdit.name16" />：</td>
					<td style="width: 36%;">
						<input id="stoper" name="stoperId" textname="stoperName" property="editor" class="mini-user rxc" plugins="mini-user"  style="width:98%;height:34px;" allowinput="false" length="50" maxlength="50"  mainfield="no"  single="true" />
					</td>
					<td style="width: 14%"><spring:message code="page.standardEdit.name17" />：</td>
					<td style="width: 36%;">
						<input id="stopTimeId" name="stopTime"  class="mini-datepicker" format="yyyy-MM-dd HH:mm:ss" timeFormat="HH:mm:ss" showTime="true" showOkButton="true" showClearButton="false" style="width:98%"/>
					</td>
				</tr>
				<tr>
					<td style="width: 14%"><spring:message code="page.standardEdit.name18" />：</td>
					<td style="width: 36%;">
						<input id="belongFieldIds" style="width:98%;" class="mini-buttonedit" name="belongFieldIds" textname="belongFieldNames" allowInput="false" onbuttonclick="selectBelongField()"/>
					</td>
					<td style="width: 14%"><spring:message code="page.standardEdit.name19" />：</td>
					<td style="width: 36%;">
						<div id="sendSupplier" name="sendSupplier" class="mini-checkbox" readOnly="false" text=""></div>
					</td>
				</tr>
				<tr>
					<td style="width: 14%"><spring:message code="page.standardEdit.name20" />：</td>
					<td style="width: 36%;min-width:170px">
						<input id="cbbh" name="cbbh"  class="mini-textbox" style="width:98%;" />
					</td>
					<td style="width: 14%"><spring:message code="page.standardEdit.name21" />：</td>
					<td style="width: 36%;min-width:140px">
						<input id="yzxcd" name="yzxcd"  class="mini-textbox" style="width:98%;" />
					</td>
				</tr>
				<tr id="banciTr" style="display: none">
					<td style="width: 14%"><spring:message code="page.standardEdit.name22" />：</td>
					<td style="width: 36%;min-width:170px">
						<input id="banci" name="banci"  class="mini-textbox" style="width:98%;" />
					</td>
				</tr>
				<tr style="height: 80px">
					<td style="width: 14%"><spring:message code="page.standardEdit.name23" />：</td>
					<td colspan="3">
						<input class="mini-textbox" style="width:70%;float: left" id="fileName" name="fileName" readonly />
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
		<div class="mini-toolbar" style="text-align:center;line-height:30px;"
			 borderStyle="border-left:0;border-top:0;border-right:0;">
			<span style="font-size: 14px;color: #777"><spring:message code="page.standardEdit.name27" />: </span>
			<input class="mini-textbox" width="130" id="filterNameId" onenter="filterSystemTree()"/>
			<a class="mini-button" iconCls="icon-search" plain="true" onclick="filterSystemTree()"></a>
		</div>
		<div class="mini-fit">
			<ul id="selectSystemTree" class="mini-tree" style="width:100%;height:100%;"
				showTreeIcon="true" textField="systemName" idField="id" expandOnLoad="0" parentField="parentId" resultAsTree="false">
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

	<div id="selectStandardWindow" title="<spring:message code="page.standardEdit.name30" />" class="mini-window" style="width:750px;height:450px;"
		 showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
		<input id="parentInputScene" style="display: none" />
		<div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px"
			 borderStyle="border-left:0;border-top:0;border-right:0;">
			<span style="font-size: 14px;color: #777"><spring:message code="page.standardEdit.name7" />: </span>
			<input class="mini-textbox" width="130" id="filterStandardNumberId" style="margin-right: 15px"/>
			<span style="font-size: 14px;color: #777"><spring:message code="page.standardEdit.name8" />: </span>
			<input class="mini-textbox" width="130" id="filterStandardNameId" style="margin-right: 15px"/>
			<a class="mini-button" iconCls="icon-search" plain="true" onclick="searchStandard()"><spring:message code="page.standardEdit.name31" /></a>
		</div>
		<div class="mini-fit">
			<div id="standardListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onrowdblclick="onRowDblClick"
				 idField="id"  showColumnsMenu="false" sizeList="[20,50,100,200]" pageSize="20"
				 allowAlternating="true" pagerButtons="#pagerButtons" url="${ctxPath}/standardManager/core/standard/queryList.do"
			>
				<div property="columns">
					<div type="checkcolumn" width="30"></div>
					<div field="systemName" sortField="systemName" width="90" headerAlign="center" align="center"
						 allowSort="true"><spring:message code="page.standardEdit.name32" />
					</div>
					<div field="categoryName" sortField="categoryName" width="60" headerAlign="center" align="center"
						 allowSort="true"><spring:message code="page.standardEdit.name6" />
					</div>
					<div field="standardNumber" sortField="standardNumber" width="140" headerAlign="center" align="center"
						 align="center" allowSort="true"><spring:message code="page.standardEdit.name33" />
					</div>
					<div field="standardName" sortField="standardName" width="180" headerAlign="center" align="center"
						 allowSort="true"><spring:message code="page.standardEdit.name27" />
					</div>
					<div field="belongDepName" sortField="belongDepName" width="80" headerAlign="center" align="center"
						 allowSort="true"><spring:message code="page.standardEdit.name9" />
					</div>
					<div field="standardStatus" sortField="standardStatus" width="60" headerAlign="center" align="center"
						 allowSort="true" renderer="statusRenderer"><spring:message code="page.standardEdit.name10" />
					</div>
				</div>
			</div>
		</div>
		<div property="footer" style="padding:5px;height: 35px">
			<table style="width:100%;height: 100%">
				<tr>
					<td style="width:120px;text-align:center;">
						<input type="button" style="height: 25px;width: 70px" value="<spring:message code="page.standardEdit.name28" />" onclick="selectStandardOK()"/>
						<input type="button" style="height: 25px;width: 70px" value="<spring:message code="page.standardEdit.name29" />" onclick="selectStandardHide()"/>
					</td>
				</tr>
			</table>
		</div>
	</div>

	<div id="selectFieldWindow" title="<spring:message code="page.standardEdit.name34" />" class="mini-window" style="width:750px;height:450px;"
		 showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
		<div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px"
			 borderStyle="border-left:0;border-top:0;border-right:0;">
			<span style="font-size: 14px;color: #777"><spring:message code="page.standardEdit.name35" />: </span>
			<input class="mini-textbox" width="130" id="filterFieldName" onenter="searchStandardBelongField()" style="margin-right: 15px"/>
			<a class="mini-button" iconCls="icon-search" plain="true" onclick="searchStandardBelongField()"><spring:message code="page.standardEdit.name31" /></a>
		</div>
		<div class="mini-fit">
			<div id="fieldListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
				 url="${ctxPath}/standardManager/core/standardField/fieldList.do" idField="fieldId" showPager="false"
				 multiSelect="true" showColumnsMenu="false" allowAlternating="true" onrowdblclick="onSelectFieldRowDblClick()">
				<div property="columns">
					<div type="checkcolumn" width="20"></div>
					<div field="fieldName" width="120" headerAlign="center" align="center" allowSort="true"><spring:message code="page.standardEdit.name35" /></div>
					<div field="systemCategoryName" width="120" headerAlign="center" align="center" allowSort="true"><spring:message code="page.standardEdit.name36" /></div>
					<div field="respUserName" width="120" headerAlign="center" align="center" allowSort="true"><spring:message code="page.standardEdit.name37" /></div>
				</div>
			</div>
		</div>
		<div property="footer" style="padding:5px;height: 35px">
			<table style="width:100%;height: 100%">
				<tr>
					<td style="width:120px;text-align:center;">
						<input type="button" style="height: 25px;width: 70px" value="<spring:message code="page.standardEdit.name28" />" onclick="selectFieldOK()"/>
						<input type="button" style="height: 25px;width: 70px" value="<spring:message code="page.standardEdit.name29" />" onclick="selectFieldHide()"/>
					</td>
				</tr>
			</table>
		</div>
	</div>
</div>

<script type="text/javascript">
    mini.parse();
    var action="${action}";
    var type="${type}";
    var jsUseCtxPath="${ctxPath}";
    var standardObj=${standardObj};
    var currentUserId="${currentUser.userId}";
    var currentUserName="${currentUser.fullname}";
	var systemCategoryId="${systemCategoryId}";

    var formStandard = new mini.Form("#formStandard");
    var selectSystemTree=mini.get("selectSystemTree");
    var selectSystemWindow=mini.get("selectSystemWindow");
    var selectStandardWindow=mini.get("selectStandardWindow");
	var standardListGrid=mini.get("standardListGrid");

	var selectFieldWindow=mini.get("selectFieldWindow");
    var fieldListGrid=mini.get("fieldListGrid");

	var currentTime="${currentTime}";
	var coverContent=currentUserName+"<br/>"+currentTime+"<br/>" + xzxgwjjxyxgs;

	//当前是否为大管理员
	var isPointManager="";
	//当前子管理员对应的体系
	var currentUserSubManager="";
	//标准开发任务id
	var standardTaskId="${standardTaskId}";

	if(type=="修订"){
        $('#stopAndNew').show();
        $('#saveStandard').hide();
	}
	if(action=="add"){
        $('#updateNote').hide();
	}

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

    fieldListGrid.on("update",function(){
        selectCurrentField();
	});

    function updateNote() {
        var belongbz = mini.get("id").getValue();
        mini.open({
            title: standardEdit_name,
            url: jsUseCtxPath + "/gzjl/gzjlListPage.do?belongbz="+belongbz+"&action="+action,
            width: 750,
            height: 500,
            allowResize: true,
            onload: function () {
            },
            ondestroy:function () {
            }
        });
    }
</script>
</body>
</html>
