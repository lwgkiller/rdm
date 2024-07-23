<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<title>备件开发申请流程信息</title>
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
		<form id="formKfsq" method="post" >
			<input id="kfsqId" name="kfsqId" class="mini-hidden"/>
			<input id="instId" name="instId" class="mini-hidden"/>
			<table class="table-detail"  cellspacing="1" cellpadding="0">
				<caption style="font-weight: bold;font-size: 20px !important;">
					开发申请审批
				</caption>
				<tr>
					<td style="width: 15%">备件品类：</td>
					<td>
						<input id="bjType" name="bjType" class="mini-combobox" style="width:98%;"
							   textField="value" valueField="key" emptyText="请选择..."
							   required="false" allowInput="false" showNullItem="true"
							   nullItemText="请选择..." onvaluechanged="setRespFw()"
							   data="[
                            {'key' : '油品','value' : '油品'},{'key' : '钎杆','value' : '钎杆'}
                           ,{'key' : '滤芯','value' : '滤芯'},{'key' : '属具','value' : '属具'}
                           ,{'key' : '斗齿','value' : '斗齿'},{'key' : '液压件','value' : '液压件'}
                           ,{'key' : '动力件','value' : '动力件'},{'key' : '底盘件','value' : '底盘件'}
                           ,{'key' : '电器件','value' : '电器件'},{'key' : '橡胶件','value' : '橡胶件'}
                           ,{'key' : '其他','value' : '其他'}]"
						/>
					</td>
					<td style="width: 15%">提交人：</td>
					<td>
						<input id="apply" name="applyId" textname="applyName"
							   property="editor" class="mini-user rxc" plugins="mini-user"
							   style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"
						/>
					</td>
					<td style="width: 15%">服务工程师：</td>
					<td>
						<input id="fw" name="fwId" textname="fwName"
							   property="editor" class="mini-user rxc" plugins="mini-user"
							   style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"
						/>
					</td>
				</tr>
				<tr>
					<td style="width:15%;height: 800px">详细信息表：</td>
					<td colspan="5">
						<div style="margin-bottom: 2px">
							<a id="addKfsqDetail" class="mini-button"  onclick="addKfsqDetail()">添加</a>
							<a id="removeKfsqDetail" class="mini-button"  onclick="removeKfsqDetail()">删除</a>
							<a class="mini-button" id="importId" style="margin-left: 5px" onclick="openImportWindow()">导入</a>
							<span style="color: red">注：添加前请先进行表单的保存、添加或删除后请点击保存以生效</span>
						</div>
						<div id="detailListGrid" class="mini-datagrid" style="width: 100%; height: 85%" allowResize="false" allowCellWrap="true"
							 idField="id" url="${ctxPath}/rdm/core/Kfsq/getDetailList.do?belongId=${kfsqId}" autoload="true"
							 allowCellEdit="true" allowCellSelect="true" oncellbeginedit="OnCellBeginEditDetail"
							 multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true" >
							<div property="columns">
								<div type="checkcolumn" width="10"></div>
								<div field="xqType"  width="40" headerAlign="center" align="center" >需求类型
									<input property="editor"  class="mini-combobox" style="width:98%;"
										   textField="value" valueField="key" emptyText="请选择..."
										   required="false" allowInput="false" showNullItem="true"
										   nullItemText="请选择..."
										   data="[
										   {'key' : '备件开发件改进','value' : '备件开发件改进'}
										   ,{'key' : '新品备件开发','value' : '新品备件开发'}]"
									/></div>
								<div field="sjjx"  width="40" headerAlign="center" align="center" >设计机型
									<input property="editor" class="mini-textbox" /></div>
								<div field="bjName"  width="30" headerAlign="center" align="center" >名称
									<input property="editor" class="mini-textbox" /></div>
								<div field="kfReason"  width="60" headerAlign="center" align="center" >开发原因
									<input property="editor" class="mini-textbox" /></div>
								<div field="wlNumber"  width="60" headerAlign="center" align="center" >原厂件物料编码
									<input property="editor" class="mini-textbox" /></div>
								<div field="wlms"  width="60" headerAlign="center" align="center" >原厂件物料描述
									<input property="editor" class="mini-textbox" /></div>
								<div field="supplier"  width="40" headerAlign="center" align="center" >计划供应商
									<input property="editor" class="mini-textbox" /></div>
								<div field="cpsId" displayField ="cpsName" width="50" headerAlign="center" align="center" >产品所
									<input  property="editor" class="mini-dep rxc" plugins="mini-dep"
											style="width:90%;height:34px;" allowinput="false" length="50" maxlength="50"
											mainfield="no" single="true" name="cpsId" textname="cpsName" /></div>
								<div field="resId" displayField ="resName" width="30" headerAlign="center" align="center" >产品所对接人
									<input  property="editor" class="mini-user rxc" plugins="mini-user"
											style="width:90%;height:34px;" allowinput="false" length="50" maxlength="50"
											mainfield="no" single="true" name="resId" textname="resName" /></div>
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
<div id="importWindow" title="备件开发申请导入窗口" class="mini-window" style="width:750px;height:280px;"
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
    var formKfsq = new mini.Form("#formKfsq");
    var kfsqId="${kfsqId}";
    var currentUserMainDepId="${currentUserMainDepId}";

    function OnCellBeginEditDetail(e) {
        var record = e.record, field = e.field;
        e.cancel = true;
        if ((field == "xqType"||field == "sjjx"||field == "bjName"||field == "kfReason"
			||field == "wlNumber" ||field == "wlms"||field == "supplier"||field == "note")
			&& (action == "edit"||first=="yes")) {
            e.cancel = false;
        }
        if ((field == "cpsId" )&& (action == "task"&&second=="yes")) {
            e.cancel = false;
        }
        if ((field == "resId" && (action == "task"&&third=="yes"))&&record.cpsId==currentUserMainDepId) {
            e.cancel = false;
        }
    }

    function setRespFw() {
        var bjType=mini.get("bjType").getValue();
        if(!bjType) {
            mini.get("fw").setValue('');
            mini.get("fw").setText('');
            return;
        }
        $.ajax({
            url: jsUseCtxPath + '/rdm/core/Kfsq/getUserInfoByBj.do?bjType='+bjType,
            type: 'get',
            async: false,
            contentType: 'application/json',
            success: function (data) {
                mini.get("fw").setValue(data.resId);
                mini.get("fw").setText(data.resName);
            }
        });
    }

    var first = "";
    var second = "";
    var third = "";
    $(function () {
        if (kfsqId) {
            var url = jsUseCtxPath + "/rdm/core/Kfsq/getKfsqDetail.do";
            $.post(
                url,
                {kfsqId: kfsqId},
                function (json) {
                    formKfsq.setData(json);
                });
        }else {
            mini.get("apply").setValue(currentUserId);
            mini.get("apply").setText(currentUserName);
        }
        //变更入口
        if (action == 'task') {
            taskActionProcess();
        } else if (action == "detail") {
            formKfsq.setEnabled(false);
            mini.get("addKfsqDetail").setEnabled(false);
            mini.get("removeKfsqDetail").setEnabled(false);
            mini.get("importId").setEnabled(false);
            $("#detailToolBar").show();
            if(status!='DRAFTED') {
                $("#processInfo").show();
            }
        }
    });

    function saveKfsqInProcess() {
        // var formValid = validJsmm();
        // if (!formValid.result) {
        //     mini.alert(formValid.message);
        //     return;
        // }
        var formData = _GetFormJsonMini("formKfsq");
        formData.kfsq=detailListGrid.getChanges();
        $.ajax({
            url: jsUseCtxPath + '/rdm/core/Kfsq/saveKfsq.do',
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
        var formData = _GetFormJsonMini("formKfsq");
        formData.kfsq=detailListGrid.getChanges();
        $.ajax({
            url: jsUseCtxPath + '/rdm/core/Kfsq/saveKfsq.do',
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
        var formData = _GetFormJsonMini("formKfsq");
        formData.kfsq=detailListGrid.getChanges();
        //此处用于向后台产生流程实例时替换标题中的参数时使用
        // formData.bos=[];
        // formData.vars=[{key:'companyName',val:formData.companyName}];
        return formData;
    }

    function saveKfsq(e) {
        // var formValid = validZero();
        // if (!formValid.result) {
        //     mini.alert(formValid.message);
        //     return;
        // }
        window.parent.saveDraft(e);
    }

    function validZero() {
        var bjType = $.trim(mini.get("bjType").getValue());
        if (!bjType) {
            return {"result": false, "message": "请选择备件品类"};
        }
        var apply = $.trim(mini.get("apply").getValue());
        if (!apply) {
            return {"result": false, "message": "请填写提交人"};
        }
        var fw = $.trim(mini.get("fw").getValue());
        if (!fw) {
            return {"result": false, "message": "请选择服务工程师"};
        }
        var detail = detailListGrid.getData();
        if(detail.length<1) {
            return {"result": false, "message": "请填写详细信息表"};
        }
        return {"result": true};
    }

    function startKfsqProcess(e) {
        var formValid = validFirst();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        window.parent.startProcess(e);
    }

    function kfsqApprove() {
        //编制阶段的下一步需要校验表单必填字段
        if (first == 'yes') {
            var formValid = validFirst();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }
        //检查通过
        window.parent.approve();
    }

    function validFirst() {
        var bjType = $.trim(mini.get("bjType").getValue());
        if (!bjType) {
            return {"result": false, "message": "请选择备件品类"};
        }
        var apply = $.trim(mini.get("apply").getValue());
        if (!apply) {
            return {"result": false, "message": "请填写提交人"};
        }
        var fw = $.trim(mini.get("fw").getValue());
        if (!fw) {
            return {"result": false, "message": "请选择服务工程师"};
        }
        var detail = detailListGrid.getData();
        if(detail.length<1) {
            return {"result": false, "message": "请填写详细信息表"};
        }else {
            var detail = detailListGrid.getData();
            for (var i = 0; i < detail.length; i++) {
                if (detail[i].xqType == undefined||detail[i].xqType == "") {
                    return {"result": false, "message": "请选择第" + (i+1) +"行需求类型！"};
                }
                if (detail[i].sjjx == undefined||detail[i].sjjx == "") {
                    return {"result": false, "message": "请填写第" + (i+1) +"行设计机型！"};
                }
                if (detail[i].bjName == undefined||detail[i].bjName == "") {
                    return {"result": false, "message": "请填写第" + (i+1) +"行名称！"};
                }
                if (detail[i].kfReason == undefined||detail[i].kfReason == "") {
                    return {"result": false, "message": "请填写第" + (i+1) +"行开发原因！"};
                }
                // if (detail[i].wlNumber == undefined||detail[i].wlNumber == "") {
                //     return {"result": false, "message": "请填写第" + (i+1) +"行原厂件物料编码！"};
                // }
                // if (detail[i].wlms == undefined||detail[i].wlms == "") {
                //     return {"result": false, "message": "请填写第" + (i+1) +"行原厂件物料描述！"};
                // }
                // if (detail[i].supplier == undefined||detail[i].supplier == "") {
                //     return {"result": false, "message": "请填写第" + (i+1) +"行计划供应商！"};
                // }
            }
		}
        return {"result": true};
    }

    function kfsqThird() {
        if (third == 'yes') {
            var detail = detailListGrid.getData();
            for (var i = 0; i < detail.length; i++) {
                if ((detail[i].cpsId != undefined||detail[i].cpsId != "")&&(detail[i].resId == undefined||detail[i].resId == "")
                    &&detail[i].cpsId==currentUserMainDepId) {
                    mini.alert("请选择"+detail[i].cpsName+"对接人！");
                    return;
                }
            }
        }
        window.parent.approve();
    }



    function kfsqForth() {
        //编制阶段的下一步需要校验表单必填字段
        // if (third == 'yes') {
        //     var formValid = validThird();
        //     if (!formValid.result) {
        //         mini.alert(formValid.message);
        //         return;
        //     }
        // }
        //检查通过
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

        if (first != 'yes') {
            // mini.get("bjType").setEnabled(true);
            // mini.get("apply").setEnabled(true);
            // mini.get("fw").setEnabled(true);
            // mini.get("addKfsqDetail").setEnabled(true);
            // mini.get("removeKfsqDetail").setEnabled(true);
            formKfsq.setEnabled(false);
            mini.get("importId").setEnabled(false);
            mini.get("addKfsqDetail").setEnabled(false);
            mini.get("removeKfsqDetail").setEnabled(false);
        }


    }




    function addKfsqDetail() {
        var formId = mini.get("kfsqId").getValue();
        if (!formId) {
            mini.alert('请先点击‘保存’进行表单的保存！');
            return;
        } else {
            var row = {};
            detailListGrid.addRow(row);
        }
    }

    function removeKfsqDetail() {
        var selecteds = detailListGrid.getSelecteds();
        var deleteArr = [];
        for (var i = 0; i < selecteds.length; i++) {
            var row = selecteds[i];
            deleteArr.push(row);
        }
        detailListGrid.removeRows(deleteArr);
    }

    function openImportWindow() {
        var formId = mini.get("kfsqId").getValue();
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
        form.attr("action", jsUseCtxPath + "/rdm/core/Kfsq/importTemplateDownload.do");
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
        var kfsqId=mini.get("kfsqId").getValue();;
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
            xhr.open('POST', jsUseCtxPath + '/rdm/core/Kfsq/importExcel.do?belongId='+kfsqId, false);
            xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
            var fd = new FormData();
            fd.append('importFile', file);
            xhr.send(fd);
        }
    }



</script>
</body>
</html>
