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
		<a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
	</div>
</div>
<div class="mini-fit">
	<div class="form-container" style="margin: 0 auto; width: 98%">
		<form id="formGyqr" method="post" >
			<input id="gyqrId" name="gyqrId" class="mini-hidden"/>
			<input id="instId" name="instId" class="mini-hidden"/>
			<table class="table-detail"  cellspacing="1" cellpadding="0">
				<caption style="font-weight: bold;font-size: 20px !important;">
					备件供应形式确认清单
				</caption>
				<tr>
					<td style="width: 15%">提交人：</td>
					<td>
						<input id="apply" name="applyId" textname="applyName"
							   property="editor" class="mini-user rxc" plugins="mini-user"
							   style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"
						/>
					</td>
					<td style="width: 15%">服务工程审核人：</td>
					<td>
						<input id="fw" name="fwId" textname="fwName"
							   property="editor" class="mini-user rxc" plugins="mini-user"
							   style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"
						/>
					</td>
					<td style="width: 15%">维护人：</td>
					<td>
						<input id="wh" name="whId" textname="whName"
							   property="editor" class="mini-user rxc" plugins="mini-user"
							   style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"
						/>
					</td>
				</tr>
				<tr>
					<td style="width:15%;height: 800px">详细信息表：</td>
					<td colspan="5">
						<div style="margin-bottom: 2px">
							<a id="addGyqrDetail" class="mini-button"  onclick="addGyqrDetail()">添加</a>
							<a id="removeGyqrDetail" class="mini-button"  onclick="removeGyqrDetail()">删除</a>
							<a class="mini-button" id="importId" style="margin-left: 5px" onclick="openImportWindow()">导入</a>
							<a class="mini-button" id="exportGyqrDetail" style="margin-right: 5px" plain="true" onclick="exportGyqrDetail()">导出</a>
							<span style="color: red">注：添加前请先进行表单的保存、添加或删除后请点击保存以生效</span>
						</div>
						<div id="detailListGrid" class="mini-datagrid" style="width: 100%; height: 85%" allowResize="false" allowCellWrap="true"
							 idField="id" url="${ctxPath}/rdm/core/Gyqr/getDetailList.do?belongId=${gyqrId}" autoload="true"
							 allowCellEdit="true" allowCellSelect="true" oncellbeginedit="OnCellBeginEditDetail"
							 multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true" >
							<div property="columns">
								<div type="checkcolumn" width="10"></div>
								<div field="zcNumber"  width="60" headerAlign="center" align="center" >总成物料编码
									<input property="editor" class="mini-textbox" /></div>
								<div field="zcms"  width="60" headerAlign="center" align="center" >总成物料描述
									<input property="editor" class="mini-textbox" /></div>
								<div field="wlNumber"  width="60" headerAlign="center" align="center" >物料编码
									<input property="editor" class="mini-textbox" /></div>
								<div field="wlms"  width="60" headerAlign="center" align="center" >物料描述
									<input property="editor" class="mini-textbox" /></div>
								<div field="zwName"  width="40" headerAlign="center" align="center" >中文名称
									<input property="editor" class="mini-textbox" /></div>
								<div field="ywName"  width="30" headerAlign="center" align="center" >英文名称
									<input property="editor" class="mini-textbox" /></div>
								<div field="supplier"  width="40" headerAlign="center" align="center" >供应商
									<input property="editor" class="mini-textbox" /></div>
								<div field="isBuy"  width="40" headerAlign="center" align="center" >是否可购买
									<input property="editor"  class="mini-combobox" style="width:98%;"
										   textField="value" valueField="key" emptyText="请选择..."
										   required="false" allowInput="false" showNullItem="true"
										   nullItemText="请选择..."
										   data="[
										   {'key' : '是','value' : '是'}
										   ,{'key' : '否','value' : '否'}]"
									/></div>
								<div field="note"  width="40" headerAlign="center" align="center" >备注
									<input property="editor" class="mini-textbox" /></div>
							</div>
						</div>
					</td>
				</tr>
			</table>
		</form>
	</div>
</div>
<div id="importWindow" title="备件供应形式确认清单导入窗口" class="mini-window" style="width:750px;height:280px;"
	 showModal="true" showFooter="false" allowResize="true">
	<p style="display: inline-block;color: red;font-size:15px;vertical-align: middle">
	</p>
	<div style="display: inline-block;float: right;">
		<a id="importBtn" class="mini-button" onclick="importProduct()">导入</a>
		<a id="closeWindow" class="mini-button btn-red" onclick="closeImportWindow()">关闭</a>
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
<form id="excelForm" action="${ctxPath}/rdm/core/Gyqr/exportGyqrDetail.do?belongId=${gyqrId}" method="post" target="excelIFrame">
	<input type="hidden" name="filter" id="filter"/>
</form>

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
    var formGyqr = new mini.Form("#formGyqr");
    var gyqrId="${gyqrId}";
    var currentUserMainDepId="${currentUserMainDepId}";

    function OnCellBeginEditDetail(e) {
        var record = e.record, field = e.field;
        e.cancel = true;
        if ((field == "zcNumber"||field == "zcms"||field == "zwName"||field == "ywName"
			||field == "wlNumber" ||field == "wlms"||field == "supplier"||field == "note")
			&& (action == "edit"||first=="yes")) {
            e.cancel = false;
        }
        if ((field == "isBuy" )&& (action == "task"&&third=="yes")) {
            e.cancel = false;
        }
    }


    var first = "";
    var second = "";
    var third = "";
    $(function () {
        if (gyqrId) {
            var url = jsUseCtxPath + "/rdm/core/Gyqr/getGyqrDetail.do";
            $.post(
                url,
                {gyqrId: gyqrId},
                function (json) {
                    formGyqr.setData(json);
                });
        }else {
            mini.get("apply").setValue(currentUserId);
            mini.get("apply").setText(currentUserName);
        }
        formGyqr.setEnabled(false);
        mini.get("exportGyqrDetail").setEnabled(false);
        mini.get("addGyqrDetail").setEnabled(true);
        mini.get("removeGyqrDetail").setEnabled(true);
        mini.get("importId").setEnabled(true);
        mini.get("apply").setEnabled(true);
        mini.get("fw").setEnabled(true);
        //变更入口
        if (action == 'task') {
            taskActionProcess();
        } else if (action == "detail") {
            formGyqr.setEnabled(false);
            mini.get("addGyqrDetail").setEnabled(false);
            mini.get("removeGyqrDetail").setEnabled(false);
            mini.get("importId").setEnabled(false);
            $("#detailToolBar").show();
            if(status!='DRAFTED') {
                $("#processInfo").show();
            }
        }
    });

    function saveGyqrInProcess() {
        // var formValid = validJsmm();
        // if (!formValid.result) {
        //     mini.alert(formValid.message);
        //     return;
        // }
        var formData = _GetFormJsonMini("formGyqr");
        formData.gyqr=detailListGrid.getChanges();
        $.ajax({
            url: jsUseCtxPath + '/rdm/core/Gyqr/saveGyqr.do',
            type: 'post',
            async: false,
            data:mini.encode(formData),
            contentType: 'application/json',
            success: function (data) {
                if (data) {
                    var message="";
                    if(data.success) {
                        window.location.reload();
                    } else {
                        mini.alert("数据保存失败"+data.message);
                    }
                }
            }
        });
    }

    function saveDetailInProcess() {
        // var formValid = validJsmm();
        // if (!formValid.result) {
        //     mini.alert(formValid.message);
        //     return;
        // }
        var formData = _GetFormJsonMini("formGyqr");
        formData.gyqr=detailListGrid.getChanges();
        $.ajax({
            url: jsUseCtxPath + '/rdm/core/Gyqr/saveGyqr.do',
            type: 'post',
            async: false,
            data:mini.encode(formData),
            contentType: 'application/json',
            success: function (data) {
                if (data) {
                    var message="";
                    if(data.success) {
                        detailListGrid.reload();
                    } else {
                        mini.alert("数据保存失败"+data.message);
                    }
                }
            }
        });
    }

    function getData() {
        var formData = _GetFormJsonMini("formGyqr");
        formData.gyqr=detailListGrid.getChanges();
        //此处用于向后台产生流程实例时替换标题中的参数时使用
        // formData.bos=[];
        // formData.vars=[{key:'companyName',val:formData.companyName}];
        return formData;
    }

    function saveGyqr(e) {
        // var formValid = validZero();
        // if (!formValid.result) {
        //     mini.alert(formValid.message);
        //     return;
        // }
        window.parent.saveDraft(e);
    }

    function validZero() {
        var apply = $.trim(mini.get("apply").getValue());
        if (!apply) {
            return {"result": false, "message": "请填写提交人"};
        }
        var wh = $.trim(mini.get("wh").getValue());
        if (!wh) {
            return {"result": false, "message": "请选择维护人"};
        }
        var fw = $.trim(mini.get("fw").getValue());
        if (!fw) {
            return {"result": false, "message": "请选择服务工程审核人"};
        }
        var detail = detailListGrid.getData();
        if(detail.length<1) {
            return {"result": false, "message": "请填写详细信息表"};
        }
        return {"result": true};
    }

    function startGyqrProcess(e) {
        var formValid = validFirst();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        window.parent.startProcess(e);
    }

    function gyqrApprove() {
        //编制阶段的下一步需要校验表单必填字段
        if (first == 'yes') {
            var formValid = validFirst();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }
        if (second == 'yes') {
            var formValid = validSecond();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }
        //检查通过
        window.parent.approve();
    }

    function validFirst() {
        var fw = $.trim(mini.get("fw").getValue());
        if (!fw) {
            return {"result": false, "message": "请选择服务工程审核人"};
        }
        var apply = $.trim(mini.get("apply").getValue());
        if (!apply) {
            return {"result": false, "message": "请填写提交人"};
        }
        var detail = detailListGrid.getData();
        if(detail.length<1) {
            return {"result": false, "message": "请填写详细信息表"};
        }else {
            var detail = detailListGrid.getData();
            for (var i = 0; i < detail.length; i++) {
                if (detail[i].wlNumber == undefined||detail[i].wlNumber == "") {
                    return {"result": false, "message": "请选择第" + (i+1) +"行物料编号！"};
                }
                if (detail[i].wlms == undefined||detail[i].wlms == "") {
                    return {"result": false, "message": "请填写第" + (i+1) +"行物料描述！"};
                }
                if (detail[i].zwName == undefined||detail[i].zwName == "") {
                    return {"result": false, "message": "请填写第" + (i+1) +"行中文名称！"};
                }

            }
		}
        return {"result": true};
    }

    function validSecond() {
        var wh = $.trim(mini.get("wh").getValue());
        if (!wh) {
            return {"result": false, "message": "请选择维护人"};
        }
        return {"result": true};
    }

    function gyqrThird() {
        if (third == 'yes') {
            var detail = detailListGrid.getData();
            for (var i = 0; i < detail.length; i++) {
                if (detail[i].isBuy == undefined||detail[i].isBuy == "") {
                    mini.alert("请选择第" + (i+1) +"行是否可购买选项！");
                    return;
                }
            }
        }
        window.parent.approve();
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
            if (nodeVars[i].KEY_ == 'first') {
                first = nodeVars[i].DEF_VAL_;
            }
            if (nodeVars[i].KEY_ == 'second') {
                second = nodeVars[i].DEF_VAL_;
            }
            if (nodeVars[i].KEY_ == 'third') {
                third  = nodeVars[i].DEF_VAL_;
            }

        }
        formGyqr.setEnabled(false);
        mini.get("addGyqrDetail").setEnabled(false);
        mini.get("removeGyqrDetail").setEnabled(false);
        mini.get("importId").setEnabled(false);
        mini.get("exportGyqrDetail").setEnabled(false);
        if (first == 'yes') {
            mini.get("apply").setEnabled(true);
            mini.get("fw").setEnabled(true);
            mini.get("importId").setEnabled(true);
            mini.get("addGyqrDetail").setEnabled(true);
            mini.get("removeGyqrDetail").setEnabled(true);
        }
        if (second == 'yes') {
            mini.get("wh").setEnabled(true);
        }if (third == 'yes') {
            mini.get("importId").setEnabled(true);
            mini.get("exportGyqrDetail").setEnabled(true);

        }


    }




    function addGyqrDetail() {
        var formId = mini.get("gyqrId").getValue();
        if (!formId) {
            mini.alert('请先点击‘保存’进行表单的保存！');
            return;
        } else {
            var row = {};
            detailListGrid.addRow(row);
        }
    }

    function removeGyqrDetail() {
        var selecteds = detailListGrid.getSelecteds();
        var deleteArr = [];
        for (var i = 0; i < selecteds.length; i++) {
            var row = selecteds[i];
            deleteArr.push(row);
        }
        detailListGrid.removeRows(deleteArr);
    }

    function openImportWindow() {
        var formId = mini.get("gyqrId").getValue();
        if (!formId) {
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
        form.attr("action", jsUseCtxPath + "/rdm/core/Gyqr/importTemplateDownload.do");
        $("body").append(form);
        form.submit();
        form.remove();
    }

    //触发文件选择
    function uploadFile() {
        $("#inputFile").click();
    }

    //文件类型判断及文件名显示
    function    getSelectFile() {
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

    //上传批量导入
    function importProduct() {
        var gyqrId=mini.get("gyqrId").getValue();;
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
            xhr.open('POST', jsUseCtxPath + '/rdm/core/Gyqr/importExcel.do?belongId='+gyqrId, false);
            xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
            var fd = new FormData();
            fd.append('importFile', file);
            xhr.send(fd);
        }
    }
    function exportGyqrDetail() {
        var parent = $(".search-form");
        var inputAry = $("input", parent);
        var params = [];
        inputAry.each(function (i) {
            var el = $(this);
            var obj = {};
            obj.name = el.attr("name");
            if (!obj.name) return true;
            obj.value = el.val();
            params.push(obj);
        });
        $("#filter").val(mini.encode(params));
        var excelForm = $("#excelForm");
        excelForm.submit();
    }


</script>
</body>
</html>
