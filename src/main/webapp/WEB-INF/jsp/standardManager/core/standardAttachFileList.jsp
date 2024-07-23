
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
	<title>标准附表材料</title>
	<%@include file="/commons/list.jsp"%>
	<script src="${ctxPath}/scripts/standardManager/standardAttachFileUtil.js?version=${static_res_version}"
			type="text/javascript"></script>
</head>
<body>
	<div class="mini-toolbar" id="fileButtons">
		<a id="uploadFile" class="mini-button"  style="margin-bottom: 5px" onclick="uploadCompleteFile">上传文件</a>
		<a id="toReview" class="mini-button"  style="margin-bottom: 5px;display: none" onclick="reviewAttachFiles">审批通过</a>
		<a id="toReject" class="mini-button btn-red"  style="margin-bottom: 5px;display: none" onclick="rejectAttachFiles">驳回</a>
	</div>
	<div class="mini-fit" style="height: 100%;">
		<div id="targetFileInfoGrid" class="mini-datagrid" style="height: 100%;" allowResize="false"
			 idField="id" allowCellEdit="true" allowCellSelect="true" allowSortColumn="false"
			 multiSelect="true" showColumnsMenu="false" allowAlternating="true" showPager="false" allowCellWrap="false" showVGridLines="true">
			<div property="columns">
				<div field="id"  headerAlign="left" visible="false">id</div>
				<div name="checkcolumn" type="checkcolumn" visible="false" width="30"></div>
				<div type="indexcolumn" headerAlign="center">序号</div>
				<div field="fileName" align="center" headerAlign="center" width="150">文件名</div>
				<div field="fileSize" align="center"  headerAlign="center"  width="60" >文件大小</div>
				<div field="fileDesc" align="center"  headerAlign="center"  width="100">备注说明</div>
				<div field="creator" align="center"  headerAlign="center" width="60" >创建人</div>
				<div field="processer" name="processer" align="center" visible="false" headerAlign="center" width="60" >流程处理人</div>
				<div field="standardName" name="standardName" align="center"  visible="false" headerAlign="center" width="160" renderer="standardInfoRenderer" >标准名称</div>
				<div field="CREATE_TIME_" align="center" dateFormat="yyyy-MM-dd HH:mm:ss" headerAlign="center"  width="60">创建时间</div>
				<div field="status" name="status" align="center"  headerAlign="center" visible="false" width="60" renderer="processStatusRenderer">流程状态</div>
				<div renderer="targetFileInfoRenderer" align="center" headerAlign="center" width="100" >操作</div>
			</div>
		</div>
	</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var targetFileInfoGrid=mini.get("targetFileInfoGrid");
    var standardId = "${standardId}";
    var processId = "${processId}";
   	var coverContent="${coverContent}";
    var standardType="${standardType}";
    var canOperateFile="${canOperateFile}";
    var currentUserId="${currentUserId}";
	var currentUserRoles =${currentUserRoles};
	var action = "${action}";
	var url=jsUseCtxPath+"/standardManager/core/standardFileInfos/getFileList.do?standardId="+standardId;
	//是否是管理标准附件审批人员
	var GLBZFBSPRYSystemStandardManager = whetherIsPointStandardManager('GLBZFBSPRY', currentUserRoles);
	var GLSystemStandardManager = whetherIsPointStandardManager('GL', currentUserRoles);
	$(function () {
        if(canOperateFile=="false") {
            mini.get("uploadFile").setEnabled(false);
		}
		// 管理标准显示涉及流程信息列
		if (standardType == "GL") {
			// 管理标准列表-更多-附表能查看的数据
			var showType = "single";
			targetFileInfoGrid.showColumn('processer');
			targetFileInfoGrid.showColumn('status');
			url=jsUseCtxPath+"/standardManager/core/standardFileInfos/getFileList.do?standardId="+standardId+"&showType="+showType;
		}
		// 管理标准管理人员审批时查看
		if (action == "review") {
			// 管理标准列表-标准附件审批能查看的数据
			var status = "unReview";
			url = jsUseCtxPath + "/standardManager/core/standardFileInfos/getFileList.do?status="+status+"&processId="+processId;
			$("#uploadFile").hide();
			$("#toReview").show();
			$("#toReject").show();
			targetFileInfoGrid.showColumn('checkcolumn');
			targetFileInfoGrid.showColumn('standardName');
		}
		queryProjectFiles();
	});

	function queryProjectFiles() {
        targetFileInfoGrid.setUrl(url);
        targetFileInfoGrid.load();
    }
	function reviewAttachFiles() {
		var processType = "reviewed";
		updateAttachFileStatus(processType);
	}

	function rejectAttachFiles() {
		var processType = "rejected";
		updateAttachFileStatus(processType);
	}

	function updateAttachFileStatus(processType) {
		var selecteds = targetFileInfoGrid.getSelecteds();
		if (selecteds.length == 0) {
			mini.alert("请至少选择一条数据！");
			return;
		}
		var ids = [];
		for (let i = 0; i < selecteds.length; i++) {
			ids.push(selecteds[i].id);
		}
		var formData = {};
		formData.ids = ids;
		formData.processType = processType;
		$.ajax({
			url: jsUseCtxPath + '/standardManager/core/standardFileInfos/updateAttachFileStatus.do',
			type: 'post',
			async: false,
			data: mini.encode(formData),
			contentType: 'application/json',
			success: function (data) {
				queryProjectFiles();
			}
		});
	}

    function uploadCompleteFile() {
        mini.open({
            title: "文件上传",
            url: jsUseCtxPath + "/standardManager/core/standardFileInfos/openUploadWindow.do",
            width: 650,
            height: 350,
            showModal:false,
            allowResize: true,
            onload: function () {
                var iframe = this.getIFrameEl();
                var projectParams={};
                projectParams.standardId = standardId;
                projectParams.standardType = standardType;
                var data = { projectParams: projectParams };  //传递上传参数
                iframe.contentWindow.SetData(data);
            },
            ondestroy: function (action) {
                queryProjectFiles();
            }
        });
    }

    function targetFileInfoRenderer(e) {
		var record = e.record;
		var s='&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
			'onclick="downProjectLoadFile('+JSON.stringify(record).replace(/"/g, '&quot;')+')">下载</span>';
		if(canOperateFile == 'false' || ((record.status == "unReview"||record.status == "reviewed")&&!GLBZFBSPRYSystemStandardManager&&!GLSystemStandardManager)) {
			s+='&nbsp;&nbsp;&nbsp;<span title="删除" style="color:silver;">删除</span>';
		} else {
			s+='&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
				'onclick="deleteProjectFile('+JSON.stringify(record).replace(/"/g, '&quot;')+')">删除</span>';
		}
		return s;
    }

	function processStatusRenderer(e) {
		var record = e.record;
		if (record.status == "unReview") {
			return '<span style="color:black;">待审批</span>';
		} else if (record.status == "reviewed") {
			return '<span style="color:green;">审批通过</span>';
		} else if (record.status == "rejected") {
			return '<span style="color:red;">未通过</span>';
		} else {
			return "";
		}
	}

	function standardInfoRenderer(e) {
		var record = e.record;
		debugger;
		var standardId = record.standardId;
		return '&nbsp;&nbsp;&nbsp;<span title='+record.standardName+' style="color:#409EFF;cursor: pointer;" ' +
				'onclick="openStandardDetailInfo(\''+encodeURIComponent(standardId)+'\')">'+record.standardName+'</span>';
	}
	// 打开标准详情
	function openStandardDetailInfo(standardId) {
		debugger;
		mini.open({
			title: "标准详情",
			url: jsUseCtxPath + "/standardManager/core/standard/edit.do?standardId=" + standardId + '&action=detail&systemCategoryId=GL',
			width: 800,
			height: 600,
			showModal: true,
			allowResize: true
		});
	}
</script>
<redxun:gridScript gridId="targetFileInfoGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl="" />
</body>
</html>
