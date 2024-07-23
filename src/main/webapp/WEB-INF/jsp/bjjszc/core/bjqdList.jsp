
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
	<title>备件供应形式确认清单列表管理</title>
	<%@include file="/commons/list.jsp"%>
</head>
<body>
<div class="mini-toolbar" >
	<div class="searchBox">
		<form id="searchForm" class="search-form" style="margin-bottom: 25px">
			<ul>
				<li style="margin-right: 15px">
					<span class="text" style="width:auto">总成物料编码: </span>
					<input class="mini-textbox" id="zcId" name="zcId" />
				</li>
				<li style="margin-right: 15px">
					<span class="text" style="width:auto">总成物料描述: </span>
					<input class="mini-textbox" id="zcName" name="zcName" />
				</li>
				<li style="margin-right: 15px">
					<span class="text" style="width:auto">物料类型: </span>
					<input class="mini-textbox" id="wlType" name="wlType" />
				</li>
				<li style="margin-right: 15px">
					<span class="text" style="width:auto">物料编码: </span>
					<input class="mini-textbox" id="wlId" name="wlId" />
				</li>
				<li style="margin-right: 15px">
					<span class="text" style="width:auto">供应商: </span>
					<input class="mini-textbox" id="gys" name="gys" />
				</li>
				<li style="margin-right: 15px">
					<span class="text" style="width:auto">可购买属性: </span>
                    <input id="kgmsx" name="kgmsx" class="mini-combobox" style="width:120px;"
                           textField="key" valueField="value" emptyText="请选择..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                           data="[ {'key' : '空','value' : '空'},{'key' : '是','value' : '是'},{'key' : '否','value' : '否'}]"
                    />
				</li>
				<li style="margin-right: 15px">
					<span class="text" style="width:auto">PMS价格维护: </span>
                    <input id="pms" name="pms" class="mini-combobox" style="width:120px;"
                           textField="key" valueField="value" emptyText="请选择..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                           data="[ {'key' : '空','value' : '空'},{'key' : '是','value' : '是'},{'key' : '否','value' : '否'}]"
                    />
				</li>
				<li style="margin-right: 15px">
					<span class="text" style="width:auto">提交人: </span>
					<input class="mini-textbox" id="applyName" name="applyName" />
				</li>
				<li style="margin-right: 15px"><span class="text" style="width:auto">流程引用状态: </span>
					<input id="projectId" name="projectId" class="mini-combobox" style="width:120px;"
						   textField="value" valueField="key" emptyText="请选择..."
						   required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
						   data="[ {'key' : 'no','value' : '未引用'},{'key' : 'yes','value' : '已引用'}]"
					/>
				</li>
				<li style="margin-right: 15px">
					<span class="text" style="width:auto">创建时间: </span>
					<input id="time" name="time" class="mini-datepicker" onfocus="this.blur()" style="width:98%;" />
				</li>
				<li style="margin-right: 15px">
					<span class="text" style="width:auto">至</span>
					<input name="createTimeTo" class="mini-datepicker" onfocus="this.blur()" style="width:98%;" />
				</li>
                <li>
                    <span class="text" style="width:auto">可购买属性维护时间从</span>
                    <input name="inputTimeFrom" class="mini-datepicker" onfocus="this.blur()" style="width:98%;" />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">至</span>
                    <input name="inputTimeTo" class="mini-datepicker" onfocus="this.blur()" style="width:98%;" />
                </li>

				<li style="margin-left: 10px">
					<a class="mini-button" style="margin-right: 5px"  plain="true" onclick="searchFrm()">查询</a>
					<a class="mini-button btn-red" style="margin-right: 5px"  plain="true" onclick="clearForm()">清空查询</a>
					<div style="display: inline-block" class="separator"></div>
					<a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="rmRow()">删除</a>
					<div style="display: inline-block" class="separator"></div>
					<a class="mini-button" id="importId" style="margin-left: 5px" onclick="openImportWindow()">导入</a>
					<a class="mini-button" id="exportBtn" style="margin-left: 5px" plain="true" onclick="exportBjqd()">导出</a>
				</li>
			</ul>
		</form>
	</div>
</div>
<div class="mini-fit" style="height: 100%;">
	<div id="bjqdListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onlyCheckSelection="true"
		 url="${ctxPath}/rdm/core/Bjqd/queryBjqd.do" idField="id"
		 multiSelect="true" showColumnsMenu="true" sizeList="[50,100,200]" pageSize="50" allowAlternating="true" pagerButtons="#pagerButtons">
		<div property="columns">
			<div type="checkcolumn" width="10" headerAlign="center" align="center"></div>
			<div type="indexcolumn" width="15" headerAlign="center" align="center">序号</div>
			<div field="zcId"  width="50" headerAlign="center" align="center" allowSort="true">总成物料编码</div>
			<div field="zcName"  width="50" headerAlign="center" align="center" allowSort="true">总成物料描述</div>
			<div field="wlType" headerAlign='center' align='center' width="40">物料类型</div>
			<div field="wlId" headerAlign='center' align='center' width="40">物料编码</div>
			<div field="wlName" headerAlign='center' align='center' width="40">物料描述</div>
			<div field="gys" headerAlign='center' align='center' width="40">供应商</div>
			<div field="kgmsx" headerAlign='center' align='center' width="40">可购买属性</div>
            <div field="inputTime" headerAlign='center' align='center' width="40">可购买属性<br>维护时间</div>
			<div field="pms" headerAlign='center' align='center' width="40">PMS价格维护</div>
			<div field="userName" headerAlign='center' align='center' width="40">创建人</div>
			<div field="CREATE_TIME_"  width="70" headerAlign="center" align="center" allowSort="false">创建时间</div>
            <div field="status" headerAlign='center' align='center' width="40" renderer="onStatusRenderer">流程引用状态</div>
		</div>
	</div>
</div>

<div id="importWindow" title="备件供应形式清单导入窗口" class="mini-window" style="width:750px;height:280px;"
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
<form id="excelForm" action="${ctxPath}/rdm/core/Bjqd/exportBjqdList.do" method="post" target="excelIFrame">
	<input type="hidden" name="filter" id="filter"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var bjqdListGrid=mini.get("bjqdListGrid");
	var importWindow = mini.get("importWindow");
    var currentUserId="${currentUserId}";
    var currentUserNo="${currentUserNo}";

    function onStatusRenderer(e) {
        var record = e.record;
        var status = record.projectId;
		if (record.hasOwnProperty("projectId") && status != ''){
			color = 'red';
			title = '已引用';
		}else{
			color = 'orange';
			title = '未引用';
		}
		var s = '<span title= "' + title + '" style="color:' + color + '"><b>'+title+'</span>';
		return s;
    }


    $(function () {
        searchFrm();
    });

	function openImportWindow() {
		importWindow.show();
	}

	function closeImportWindow() {
		importWindow.hide();
		clearUploadFile();
		bjqdListGrid.reload();
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
                            bjqdListGrid.reload();
                        }
                    }
                }
            };

            // 开始上传
            xhr.open('POST', jsUseCtxPath + '/rdm/core/Bjqd/importExcel.do', false);
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
	function exportBjqd() {
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

	//删除记录
	function rmRow(record) {
		var rows = [];
		if (record) {
			rows.push(record);
		} else {
			rows = bjqdListGrid.getSelecteds();
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
				var existStartInst = false;
				for (var i = 0, l = rows.length; i < l; i++) {
					var r = rows[i];
					ids.push(r.id);
					if (r.projectId){
						existStartInst = true;
						continue;
					}
				}
				if (existStartInst) {
					mini.alert("仅未引用状态数据可删除！");
					return ;
				}
				if (ids.length > 0) {
					_SubmitJson({
						url: jsUseCtxPath + "/rdm/core/Bjqd/delBjqd.do",
						method: 'POST',
						data: {ids: ids.join(',')},
						success: function (text) {
							bjqdListGrid.reload();
						}
					});
				}
			}
		});
	}
</script>
<redxun:gridScript gridId="bjqdListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl="" />
</body>
</html>