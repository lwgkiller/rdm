<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<title>技术参数编辑</title>
	<%@include file="/commons/edit.jsp"%>
	<link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css" />
	<script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>

<body>
<div id="toolBar" class="topToolBar">
	<div>
		<a id="saveBtn" class="mini-button" style="display: none" onclick="saveJscs()">保存</a>
		<a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
	</div>
</div>
<div class="mini-fit">
	<div class="form-container" style="margin: 0 auto; width: 100%">
		<form id="formJscs" method="post" >
			<input id="jsId" name="jsId" class="mini-hidden"/>
			<input id="oldjsId" name="oldjsId" class="mini-hidden"/>
			<input id="jstype" name="jstype" class="mini-hidden"/>
			<table class="table-detail"  cellspacing="1" cellpadding="0">
				<caption style="font-weight: bold;font-size: 20px !important;">
					技术参数填写
				</caption>
				<tr>
					<td style="width: 7%">零部代号/型号及名称：<span style="color: #ff0000">*</span></td>
					<td>
						<input style="width:98%;" class="mini-textbox" id="code" name="code" />
					</td>
					<td style="width: 7%">技术协议编号：<span style="color: #ff0000">*</span></td>
					<td>
						<input style="width:98%;" class="mini-textbox" id="numb" name="numb" />
					</td>
					<td style="width: 7%">适用机型：<span style="color: #ff0000">*</span></td>
					<td>
						<input style="width:98%;" class="mini-textbox" id="model" name="model" />
					</td>
				</tr>
<%--				<tr>--%>
<%--					<td style="width: 7%;height: 750px">详细信息：</td>--%>
<%--					<td colspan="5">--%>
<%--						<div style="margin-top: 5px;margin-bottom: 2px">--%>
<%--							<a id="addJscsDetail" style="margin-right: 5px;display: none" class="mini-button"  onclick="addJscsDetail()">添加</a>--%>
<%--							<a id="editJscsDetail" style="margin-right: 5px;display: none" class="mini-button"  onclick="editJscsDetail()">编辑</a>--%>
<%--							<a id="removeJscsDetail" style="margin-right: 5px;display: none" class="mini-button"  onclick="removeJscsDetail()">删除</a>--%>
<%--							<span style="color: red">注：添加前请先进行表单的保存</span>--%>
<%--						</div>--%>
<%--						<div id="detailListGrid" class="mini-datagrid" style="width: 100%; height: 85%" allowResize="false" allowCellWrap="true"--%>
<%--							 idField="id" url="${ctxPath}/jscs/getJscsDetailList.do?belongJs=${jsId}&belongOldJs=${oldjsId}&jstype=${jstype}" autoload="true"--%>
<%--							 multiSelect="false" showPager="false" showColumnsMenu="false" allowAlternating="true" >--%>
<%--							<div property="columns">--%>
<%--								<div type="checkcolumn" width="20"></div>--%>
<%--								<div field="type"  width="40" headerAlign="center" align="center" allowSort="true">参数类型</div>--%>
<%--								<div field="name"  width="60" headerAlign="center" align="center" allowSort="true">参数名称</div>--%>
<%--								<div field="number"  width="50" headerAlign="center" align="center" allowSort="true">参数值</div>--%>
<%--								<div field="weight" headerAlign='center' align='center' width="40">重要度</div>--%>
<%--								<div field="note" headerAlign='center' align='center' width="40">备注</div>--%>
<%--								<div  align="center" headerAlign="center" width="40" renderer="fujian">附件列表</div>--%>
<%--							</div>--%>
<%--						</div>--%>
<%--					</td>--%>
<%--				</tr>--%>
			</table>
		</form>
		<div id="systemTab" class="mini-tabs" style="width:100%;height:100%;" >
			<div title="功能&要求描述" name="functionAndRequest" refreshOnClick="true"></div>
			<div title="指标分解" name="quotaDecompose" refreshOnClick="true"></div>
		</div>
	</div>
</div>



<script type="text/javascript">
    mini.parse();
    var action="${action}";
    var jsUseCtxPath="${ctxPath}";
    var currentUserId="${currentUserId}";
    var currentUserName = "${currentUserName}";
	// var detailListGrid=mini.get("detailListGrid");
    var formJscs = new mini.Form("#formJscs");
    var jsId="${jsId}";
    var type="${type}";
    var oldjsId="${oldjsId}";
    var jstype="${jstype}";
	var systemTab=mini.get("systemTab");


    function fujian(e) {
        var record = e.record;
        var csId = record.csId;
        var s = '';
        s += '<span style="color:dodgerblue" title="附件列表" onclick="jscsFile(\'' + csId + '\')">附件列表</span>';
        return s;
    }
    $(function () {
        if (jsId) {
            var url = jsUseCtxPath + "/jscs/getJscs.do";
            $.post(
                url,
                {jsId: jsId},
                function (json) {
                    formJscs.setData(json);
                });
        }else {
            if (!jsId && jstype == "old" && oldjsId) {
                var url = jsUseCtxPath + "/jscs/getJscs.do";
                $.ajaxSettings.async = false;
                $.post(
                    url,
                    {jsId: oldjsId},
                    function (json) {
                        formJscs.setData(json);
                        mini.get("jsId").setValue(jsId);
                        mini.get("jstype").setValue(jstype);
                        mini.get("oldjsId").setValue(oldjsId);
                    });
                $.ajaxSettings.async = true;
                saveOldJscs();
            }
        }
		initTabs();
        //明细入口
        if (action == 'detail') {
            formJscs.setEnabled(false);
        }else if(action == 'edit'||action == 'add'){
            $("#saveBtn").show();
            $("#addJscsDetail").show();
            $("#editJscsDetail").show();
            $("#removeJscsDetail").show();
        }
    });

	function initTabs() {
		var tab3=systemTab.getTab("functionAndRequest");
		var newUrl=jsUseCtxPath+"/drbfm/single/singleFunctionRequestJSPage.do?jsId="+jsId+"&action="+action;
		systemTab.updateTab(tab3,{url:newUrl,loadedUrl:newUrl});

		var tab4=systemTab.getTab("quotaDecompose");
		var newUrl=jsUseCtxPath+"/drbfm/single/singleQuotaDecomposeJSPage.do?jsId="+jsId+"&action="+action;
		systemTab.updateTab(tab4,{url:newUrl,loadedUrl:newUrl});

		// 激活
		systemTab.reloadTab(systemTab.getTab(0));
	}

    function valfirstIdSzh() {
        var code = $.trim(mini.get("code").getValue())
        if (!code) {
            return {"result": false, "message": "请填写零部代号/型号及名称"};
        }
        var numb = $.trim(mini.get("numb").getValue())
        if (!numb) {
            return {"result": false, "message": "请填写技术协议编号"};
        }var model = $.trim(mini.get("model").getValue())
        if (!model) {
            return {"result": false, "message": "请填写适用机型"};
        }
        return {"result": true};
    }

    function saveJscs() {
        var formValfirstId = valfirstIdSzh();
        if (!formValfirstId.result) {
            mini.alert(formValfirstId.message);
            return;
        }
        var formData = _GetFormJsonMini("formJscs");
        $.ajax({
            url: jsUseCtxPath + '/jscs/saveJscs.do?type='+type,
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
                            window.location.href = jsUseCtxPath + "/jscs/editPage.do?action=edit&jsId=" + returnObj.data;
                        });
                    } else {
                        message = "数据保存失败，" + returnObj.message;
                        mini.alert(message, "提示信息");
                    }
                }
            }
        });
    }

    function saveOldJscs() {
        var formData = _GetFormJsonMini("formJscs");
        $.ajax({
            url: jsUseCtxPath + '/jscs/saveOldJscs.do?type='+type,
            type: 'post',
            async: false,
            data: mini.encode(formData),
            contentType: 'application/json',
            success: function (returnObj) {
                if (returnObj) {
                    var message = "";
                    if (returnObj.success) {
                        message = "数据复制成功";
                        mini.alert(message, "提示信息", function () {
                            window.location.href = jsUseCtxPath + "/jscs/editPage.do?action=edit&jsId=" + returnObj.data;
                        });
                    } else {
                        message = "数据保存失败，" + returnObj.message;
                        mini.alert(message, "提示信息");
                    }
                }
            }
        });
    }
</script>
</body>
</html>
