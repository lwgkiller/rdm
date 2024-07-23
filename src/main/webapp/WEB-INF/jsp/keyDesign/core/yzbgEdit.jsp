<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<title>验证报告编辑</title>
	<%@include file="/commons/edit.jsp"%>
	<link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css" />
	<script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>

<body>
<div id="toolBar" class="topToolBar">
	<div>
		<a id="saveBtn" class="mini-button" style="display: none" onclick="saveYzbg()">保存</a>
		<a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
	</div>
</div>
<div class="mini-fit">
	<div class="form-container" style="margin: 0 auto; width: 100%">
		<form id="formYzbg" method="post" >
			<input id="bgId" name="bgId" class="mini-hidden"/>
			<table class="table-detail"  cellspacing="1" cellpadding="0">
				<caption style="font-weight: bold;font-size: 20px !important;">
					验证报告填写
				</caption>
				<tr>
					<td style="width: 12%">（子）零部件图号/型号：<span style="color: #ff0000">*</span></td>
					<td style="width: 13%">
						<input style="width:98%;" class="mini-textbox" id="code" name="code" />
					</td>
					<td style="width: 12%">零部件名称：<span style="color: #ff0000">*</span></td>
					<td style="width: 13%">
						<input style="width:98%;" class="mini-textbox" id="codeName" name="codeName" />
					</td>
					<td style="width: 12%">适用机型：<span style="color: #ff0000">*</span></td>
					<td style="width: 13%">
						<input style="width:98%;" class="mini-textbox" id="model" name="model" />
					</td>
					</td>
					<td style="width: 12%">生产供应商：<span style="color: #ff0000">*</span></td>
					<td style="width: 13%">
						<input style="width:98%;" class="mini-textbox" id="supplier" name="supplier" />
					</td>
				</tr>
				<tr>
					<td style="width: 12%;height: 750px">详细信息：</td>
					<td colspan="8">
						<div style="margin-top: 5px;margin-bottom: 2px">
							<a id="addYzbgDetail" style="margin-right: 5px;display: none" class="mini-button"  onclick="addYzbgDetail()">添加</a>
							<a id="editYzbgDetail" style="margin-right: 5px;display: none" class="mini-button"  onclick="editYzbgDetail()">编辑</a>
							<a id="removeYzbgDetail" style="margin-right: 5px;display: none" class="mini-button"  onclick="removeYzbgDetail()">删除</a>
							<span style="color: red">注：添加前请先进行表单的保存</span>
						</div>
						<div id="detailListGrid" class="mini-datagrid" style="width: 100%; height: 85%" allowResize="false" allowCellWrap="true"
							 idField="id" url="${ctxPath}/yzbg/getYzbgDetailList.do?belongBg=${bgId}" autoload="true"
							 multiSelect="false" showPager="false" showColumnsMenu="false" allowAlternating="true" >
							<div property="columns">
								<div type="checkcolumn" width="20"></div>
								<div field="bgType"  width="40" headerAlign="center" align="center" allowSort="true">报告类型</div>
								<div field="name"  width="60" headerAlign="center" align="center" allowSort="true">报告名称</div>
								<div field="checkJg"  width="50" headerAlign="center" align="center" allowSort="true">验证机构</div>
								<div field="checkTime" headerAlign='center' align='center' width="40">验证时间</div>
								<div field="note" headerAlign='center' align='center' width="40">备注</div>
								<div  align="center" headerAlign="center" width="40" renderer="fujian">附件列表</div>
							</div>
						</div>
					</td>
				</tr>
			</table>
		</form>
	</div>
</div>



<script type="text/javascript">
    mini.parse();
    var action="${action}";
    var jsUseCtxPath="${ctxPath}";
    var currentUserId="${currentUserId}";
    var currentUserName = "${currentUserName}";
	var detailListGrid=mini.get("detailListGrid");
    var formYzbg = new mini.Form("#formYzbg");
    var bgId="${bgId}";
    var type="${type}";

    function fujian(e) {
        var record = e.record;
        var detailId = record.detailId;
        var s = '';
        s += '<span style="color:dodgerblue" title="附件列表" onclick="yzbgFile(\'' + detailId + '\')">附件列表</span>';
        return s;
    }
    $(function () {
        if (bgId) {
            var url = jsUseCtxPath + "/yzbg/getYzbg.do";
            $.post(
                url,
                {bgId: bgId},
                function (json) {
                    formYzbg.setData(json);
                });
        }
        //明细入口
        if (action == 'detail') {
            formYzbg.setEnabled(false);
        }else if(action == 'edit'||action == 'add'){
            $("#saveBtn").show();
            $("#addYzbgDetail").show();
            $("#editYzbgDetail").show();
            $("#removeYzbgDetail").show();
        }
    });

    function valfirstIdSzh() {
        var code = $.trim(mini.get("code").getValue())
        if (!code) {
            return {"result": false, "message": "请填写（子）零部件图号/型号"};
        }
        var codeName = $.trim(mini.get("codeName").getValue())
        if (!codeName) {
            return {"result": false, "message": "请填写零部件名称"};
        }
        var model = $.trim(mini.get("model").getValue())
        if (!model) {
            return {"result": false, "message": "请填写适用机型"};
        }
        var supplier = $.trim(mini.get("supplier").getValue())
        if (!supplier) {
            return {"result": false, "message": "请填写生产供应商"};
        }
        return {"result": true};
    }

    function saveYzbg() {
        var formValfirstId = valfirstIdSzh();
        if (!formValfirstId.result) {
            mini.alert(formValfirstId.message);
            return;
        }
        var formData = _GetFormJsonMini("formYzbg");
        $.ajax({
            url: jsUseCtxPath + '/yzbg/saveYzbg.do?type='+type,
            type: 'post',
            async: false,
            data: mini.encode(formData),
            contentType: 'application/json',
            success: function (returnObj) {
                if (returnObj) {
                    var message = "";
                    if (returnObj.success) {
                        message = "数据保存成功";
                        mini.alert(message, "提示信息", function () {
                            window.location.href = jsUseCtxPath + "/yzbg/editPage.do?action=edit&bgId=" + returnObj.data;
                        });
                    } else {
                        message = "数据保存失败，" + returnObj.message;
                        mini.alert(message, "提示信息");
                    }
                }
            }
        });
    }

    function addYzbgDetail() {
        var bgId = mini.get("bgId").getValue();
        if (!bgId) {
            mini.alert('请先点击‘保存’进行表单的保存！');
            return;
        }
        mini.open({
            title: "计划明细",
            url: jsUseCtxPath + "/yzbg/editYzbgDetail.do?bgId=" + bgId + "&action=add",
            width: 1050,
            height: 650,
            showModal: true,
            allowResize: true,
            onload: function () {
            },
            ondestroy: function (action) {
                if (detailListGrid) {
                    detailListGrid.load();
                }
            }
        });
    }

    function editYzbgDetail() {
        var bgId = mini.get("bgId").getValue();
        if (!bgId) {
            mini.alert('请先点击‘保存’进行表单的保存！');
            return;
        }
        var row = detailListGrid.getSelected();
        if (row) {
            var detailId = row.detailId;
			mini.open({
				title: "计划明细",
				url: jsUseCtxPath + "/yzbg/editYzbgDetail.do?detailId=" + detailId + "&action=edit&bgId=" + bgId,
				width: 1050,
				height: 650,
				showModal: true,
				allowResize: true,
				onload: function () {
				},
				ondestroy: function (action) {
					if (detailListGrid) {
					    detailListGrid.load();
					}
				}
			});
        } else {
            mini.alert("请选中一条记录");
        }
    }

    function removeYzbgDetail() {
        var bgId = mini.get("bgId").getValue();
        if (!bgId) {
            mini.alert('请先点击‘保存’进行表单的保存！');
            return;
        }
        var row = detailListGrid.getSelected();
        if (row) {
            var detailId = row.detailId;
            mini.confirm("确定删除选中记录？", "提示", function (action) {
                if (action != 'ok') {
                    return;
                } else {
                    _SubmitJson({
                        url: jsUseCtxPath + "/yzbg/deleteYzbgDetail.do?bgId=" + bgId,
                        method: 'POST',
                        showMsg: false,
                        data: {detailId: detailId},
                        success: function (data) {
                            if (data) {
                                mini.alert(data.message);
                                detailListGrid.reload();
                            }
                        }
                    });
                }
            });
        } else {
            mini.alert("请选中一条记录");
        }
    }

    function yzbgFile(detailId) {
        mini.open({
            title: "附件列表",
            url: jsUseCtxPath + "/yzbg/fileList.do?&detailId=" + detailId,
            width: 1050,
            height: 550,
            allowResize: true,
            onload: function () {
            }
        });
    }

</script>
</body>
</html>
