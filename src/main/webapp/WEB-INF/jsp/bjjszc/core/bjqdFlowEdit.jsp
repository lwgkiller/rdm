<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<title>备件供应形式确认清单流程信息</title>
	<%@include file="/commons/edit.jsp"%>
	<link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css" />
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}" type="text/javascript"></script>
	<script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>

<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
	<div>
		<a id="processInfo" class="mini-button" style="display: none" onclick="processInfo()">流程信息</a>
		<a class="mini-button" onclick="CloseWindow()">关闭</a>
	</div>
</div>
<div class="mini-fit">
	<div class="form-container" style="margin: 0 auto; width: 98%">
		<form id="formData" method="post" >
			<input id="id" name="id" class="mini-hidden"/>
			<input id="instId" name="instId" class="mini-hidden"/>
			<div style="margin-bottom: 2px">
				<a class="mini-button" id="importId" style="margin-left: 5px" onclick="openImportWindow()">可购买属性确认导入</a>
				<a class="mini-button" id="importPMS" style="margin-left: 5px" onclick="openImportWindow()">PMS价格维护确认导入</a>
				<a class="mini-button" id="removeBjqdDetail" style="margin-left: 5px" onclick="removeBjqdDetail()">删除</a>
				<a class="mini-button" id="exportBjqdFlowList" style="margin-right: 5px" plain="true" onclick="exportBjqdFlowList()">导出</a>
				<span style="color: red">注：导入后当即生效。当数据量较大时，导入导出请不要重复点击！</span>
			</div>
		</form>
		<div id="detailListGrid" class="mini-datagrid" style="width: 100%; height: 95%" allowResize="false" allowCellWrap="true"
			 idField="id" url="${ctxPath}/rdm/core/Bjqd/getBjqdDetail.do?id=${id}" autoload="true"
			 allowCellEdit="true" allowCellSelect="true" sizeList="[100,200,500,1000]" pageSize="100"
			 multiSelect="true" showPager="true" showColumnsMenu="false" allowAlternating="true"
        >
			<div property="columns">
				<div type="checkcolumn" width="10"></div>
				<div field="zcId"  width="60" headerAlign="center" align="center" >总成物料编码
					<input property="editor" class="mini-textbox" /></div>
				<div field="zcName"  width="60" headerAlign="center" align="center" >总成物料描述
					<input property="editor" class="mini-textbox" /></div>
				<div field="wlType"  width="60" headerAlign="center" align="center" >物料类型
					<input property="editor" class="mini-textbox" /></div>
				<div field="wlId"  width="60" headerAlign="center" align="center" >物料编码
					<input property="editor" class="mini-textbox" /></div>
				<div field="wlName"  width="60" headerAlign="center" align="center" >物料描述
					<input property="editor" class="mini-textbox" /></div>

				<div field="gys"  width="40" headerAlign="center" align="center" >供应商
					<input property="editor" class="mini-textbox" /></div>
				<div field="kgmsx"  width="40" headerAlign="center" align="center" >是否可购买
					<input property="editor"  class="mini-combobox" style="width:98%;"
						   textField="value" valueField="key" emptyText="请选择..."
						   required="false" allowInput="false" showNullItem="true"
						   nullItemText="请选择..."
						   data="[
										   {'key' : '是','value' : '是'}
										   ,{'key' : '否','value' : '否'}]"
					/></div>
				<div field="price"  width="40" headerAlign="center" align="center" >价格
					<input property="editor" class="mini-textbox" /></div>
				<div field="pms"  width="30" headerAlign="center" align="center" >PMS价格维护
					<input property="editor" class="mini-textbox" /></div>
                <div field="remark"  width="40" headerAlign="center" align="center" >备注
                    <input property="editor" class="mini-textbox" /></div>
			</div>
		</div>
	</div>
</div>
<div id="importWindow" title="备件供应形式清单导入窗口" class="mini-window" style="width:750px;height:280px;"
	 showModal="true" showFooter="false" allowResize="true">
	<p style="display: inline-block;color: red;font-size:15px;vertical-align: middle">
	</p>
	<div style="display: inline-block;float: right;">
		<a id="importBtn" class="mini-button" onclick="importProduct()">可购买属性确认导入</a>
		<a id="importBtnPMS" class="mini-button" onclick="importProductPMS()">PMS价格维护确认导入</a>
		<a class="mini-button btn-red" onclick="closeImportWindow()">关闭</a>
	</div>
	<div class="mini-fit" style="font-size: 14px;margin-top: 30px">
		<form id="formImport" method="post">
			<table class="table-detail" cellspacing="1" cellpadding="0">
				<tr>
					<td style="width: 30%">上传模板：</td>
					<td style="width: 70%;">
						<a href="#" style="color:blue;text-decoration:underline;" onclick="downImportOil()">备件开发申请导入模板.xlsx</a>
					</td>
				</tr>
				<tr>
					<td style="width: 30%">操作：</td>
					<td>
						<input class="mini-textbox" style="width:60%;float: left" id="fileName" name="fileName"
							   readonly/>
						<input id="inputFile" style="display:none;" type="file" onchange="getSelectFile()"
							   accept="application/xls"/>
						<a id="uploadFileBtn" class="mini-button" style="float: left;margin-left: 10px"
						   onclick="uploadFile">选择文件</a>
						<a id="clearFileBtn" class="mini-button" style="float: left;margin-left: 10px"
						   onclick="clearUploadFile">清除</a>
					</td>
				</tr>
			</table>
		</form>
	</div>
</div>
<form id="excelForm" action="${ctxPath}/rdm/core/Bjqd/exportBjqdFlowList.do" method="post" target="excelIFrame">
	<input type="hidden" name="filter" id="filter"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>

<script type="text/javascript">
    mini.parse();
    var importWindow = mini.get("importWindow");
    var nodeVarsStr='${nodeVars}';
    var action="${action}";
    var status="${status}";
    var jsUseCtxPath="${ctxPath}";
    var currentUserId="${currentUserId}";
    var currentTime="${currentTime}";
    var currentUserName = "${currentUserName}";
	var detailListGrid=mini.get("detailListGrid");
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";
    var formData = new mini.Form("#formData");
    var id="${id}";
	var instId="${instId}";
    var currentUserMainDepId="${currentUserMainDepId}";

    var stageName = "";
    $(function () {
        if (id) {
			mini.get("id").setValue(id);
			mini.get("instId").setValue(instId);
        }
        formData.setEnabled(false);
        mini.get("removeBjqdDetail").setEnabled(true);
        mini.get("importId").setEnabled(true);
		mini.get("importPMS").setEnabled(false);
		mini.get("importBtn").setEnabled(true);
		mini.get("importBtnPMS").setEnabled(false);
		detailListGrid.setAllowCellEdit(false);
        //变更入口
        if (action == 'task') {
            taskActionProcess();
        } else if (action == "detail") {
            formData.setEnabled(false);
            mini.get("importId").setEnabled(false);
			mini.get("importPMS").setEnabled(false);
			mini.get("removeBjqdDetail").setEnabled(false);
            $("#detailToolBar").show();
            if(status!='DRAFTED') {
                $("#processInfo").show();
            }
        }
    });

	function getData() {
        var formData = _GetFormJsonMini("formData");
        formData.data = detailListGrid.getChanges();
        return formData;
    }

    function saveDraft(e) {
        window.parent.saveDraft(e);
    }

    function startProcessBjqd(e) {
		if (!id) {
			mini.alert('请先点击‘保存’进行表单的保存！');
			return;
		}
        window.parent.startProcess(e);
    }

    function projectApprove() {
        //判断必须要有关联的数据
        var url = jsUseCtxPath + "/rdm/core/Bjqd/judgeKGMDataExist.do?projectId="+id;
        $.ajax({
            url:url,
            method:'get',
            async:false,
            success:function (json) {
                if (json.success=='true'){
                    window.parent.approve();
                } else {
                    mini.alert(json.message);
                }
            }
        });
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

    function taskActionProcess() {
        //获取上一环节的结果和处理人
        bpmPreTaskTipInForm();

        //获取环境变量
        if (!nodeVarsStr) {
            nodeVarsStr = "[]";
        }
		var nodeVars = $.parseJSON(nodeVarsStr);
		for (var i = 0; i < nodeVars.length; i++) {
			if (nodeVars[i].KEY_ == 'stageName') {
				stageName = nodeVars[i].DEF_VAL_;
			}
		}
		if (stageName == 'pms') {
			mini.get("importId").setEnabled(false);
			//importPMS
			mini.get("importPMS").setEnabled(true);
			mini.get("removeBjqdDetail").setEnabled(false);
			mini.get("importBtn").setEnabled(false);
			mini.get("importBtnPMS").setEnabled(true);
		}

    }

	function openImportWindow() {
		if (!id) {
			mini.alert('请先点击‘保存’进行表单的保存！');
			return;
		}
		importWindow.show();
	}

	function closeImportWindow() {
		importWindow.hide();
		clearUploadFile();
		detailListGrid.reload();
	}

	//导入模板下载
	function downImportOil() {
		var form = $("<form>");
		form.attr("style", "display:none");
		form.attr("target", "");
		form.attr("method", "post");
		form.attr("action", jsUseCtxPath + "/rdm/core/Bjqd/importTemplateDownload.do");
		$("body").append(form);
		form.submit();
		form.remove();
	}

	//上传批量导入
	function importProduct() {
		// var gyqrId=mini.get("gyqrId").getValue();;
		var file = null;
		var fileList = $("#inputFile")[0].files;
		if (fileList && fileList.length > 0) {
			file = fileList[0];
		}
		if (!file) {
			mini.alert('请选择文件！');
			return;
		}
		//XMLHttpRequest方式上传表单
		var xhr = false;
		try {
			//尝试创建 XMLHttpRequest 对象，除 IE 外的浏览器都支持这个方法。
			xhr = new XMLHttpRequest();
		} catch (e) {
			//使用较新版本的 IE 创建 IE 兼容的对象（Msxml2.XMLHTTP）。
			xhr = ActiveXobject("Msxml12.XMLHTTP");
		}

		if (xhr.upload) {
			xhr.onreadystatechange = function (e) {
				if (xhr.readyState == 4) {
					if (xhr.status == 200) {
						if (xhr.responseText) {
							var returnObj = JSON.parse(xhr.responseText);
							var message = '';
							if (returnObj.message) {
								message = returnObj.message;
							}
							mini.alert(message);
							detailListGrid.reload();
						}
					}
				}
			};

			// 开始上传
			xhr.open('POST', jsUseCtxPath + '/rdm/core/Bjqd/importExcelFlow.do?projectId='+id, false);
			xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
			var fd = new FormData();
			fd.append('importFile', file);
			xhr.send(fd);
		}
	}

	//上传批量导入
	function importProductPMS() {
		// var gyqrId=mini.get("gyqrId").getValue();;
		var file = null;
		var fileList = $("#inputFile")[0].files;
		if (fileList && fileList.length > 0) {
			file = fileList[0];
		}
		if (!file) {
			mini.alert('请选择文件！');
			return;
		}
		//XMLHttpRequest方式上传表单
		var xhr = false;
		try {
			//尝试创建 XMLHttpRequest 对象，除 IE 外的浏览器都支持这个方法。
			xhr = new XMLHttpRequest();
		} catch (e) {
			//使用较新版本的 IE 创建 IE 兼容的对象（Msxml2.XMLHTTP）。
			xhr = ActiveXobject("Msxml12.XMLHTTP");
		}

		if (xhr.upload) {
			xhr.onreadystatechange = function (e) {
				if (xhr.readyState == 4) {
					if (xhr.status == 200) {
						if (xhr.responseText) {
							var returnObj = JSON.parse(xhr.responseText);
							var message = '';
							if (returnObj.message) {
								message = returnObj.message;
							}
							mini.alert(message);
							detailListGrid.reload();
						}
					}
				}
			};

			// 开始上传
			xhr.open('POST', jsUseCtxPath + '/rdm/core/Bjqd/importExcelFlowPMS.do?projectId='+id, false);
			xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
			var fd = new FormData();
			fd.append('importFile', file);
			xhr.send(fd);
		}
	}

	//触发文件选择
	function uploadFile() {
		$("#inputFile").click();
	}

	//文件类型判断及文件名显示
	function getSelectFile() {
		var fileList = $("#inputFile")[0].files;
		if (fileList && fileList.length > 0) {
			var fileNameSuffix = fileList[0].name.split('.').pop();
			if (fileNameSuffix == 'xls'||fileNameSuffix == 'xlsx') {
				mini.get("fileName").setValue(fileList[0].name);
			}
			else {
				clearUploadFile();
				mini.alert('请上传excel文件！');
			}
		}
	}

	//清空文件
	function clearUploadFile() {
		$("#inputFile").val('');
		mini.get("fileName").setValue('');
	}

	//导出
	function exportBjqdFlowList() {
		if (!id) {
			mini.alert('请先点击‘保存’进行表单的保存！');
			return;
		}
		var queryParam = [];
		queryParam.push({name: "projectId", value: id});
		$("#filter").val(mini.encode(queryParam));
		var excelForm = $("#excelForm");
        excelForm.submit();
	}


	//删除(还原)记录
	function removeBjqdDetail(record) {
		var rows = [];
		if (record) {
			rows.push(record);
		} else {
			rows = detailListGrid.getSelecteds();
		}
		if (rows.length <= 0) {
			mini.alert("请至少选中一条记录");
			return;
		}
		mini.confirm("删除选择行，是否继续？", "提示", function (action) {
			if (action != 'ok') {
				return;
			} else {
				//判断该行是否已经提交流程
				var ids = [];
				for (var i = 0, l = rows.length; i < l; i++) {
					var r = rows[i];
					ids.push(r.id);
				}
				if (ids.length > 0) {
					_SubmitJson({
						url: jsUseCtxPath + "/rdm/core/Bjqd/delBjqdFlow.do",
						method: 'POST',
						data: {ids: ids.join(',')},
						success: function (text) {
							detailListGrid.reload();
						}
					});
				}
			}
		});
	}
</script>
</body>
</html>
