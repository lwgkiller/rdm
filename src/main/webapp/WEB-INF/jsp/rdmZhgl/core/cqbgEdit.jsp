<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<title>技术交底书变更申请</title>
	<%@include file="/commons/edit.jsp"%>
	<link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css" />
	<script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>

<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
	<div>
		<a id="processInfo" class="mini-button" style="display: none" onclick="processInfo()">流程信息</a>
		<a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
	</div>
</div>
<div class="mini-fit">
	<div class="form-container" style="margin: 0 auto; width: 80%">
		<form id="formCqbg" method="post" >
			<input id="flowType" name="flowType" class="mini-hidden"/>
			<input id="cqbgId" name="cqbgId" class="mini-hidden"/>
			<input id="instId" name="instId" class="mini-hidden"/>
			<table class="table-detail"  cellspacing="1" cellpadding="0">
				<caption style="font-weight: bold;font-size: 20px !important;">
					技术交底书变更申请
				</caption>
				<tr>
					<td style="width: 7%">提案名称：</td>
					<td>
						<input  id="jsjdsId" name="jsjdsId" textname="zlName" style="width:98%;" property="editor"
								class="mini-buttonedit" showClose="true"
								oncloseclick="onSelectStandardCloseClick" allowInput="false"
								onbuttonclick="selectStandard()"/>
					</td>
				</tr>
				<tr>
					<td style="width: 14%">变更前发明(设计)人：</td>
					<td style="width: 30%;">
						<input id="fmsjr" name="fmsjr"  class="mini-textbox" readonly style="width:98%;" />
					</td>
				</tr>
				<tr>
					<td style="width: 14%">变更前我司发明(设计)人：</td>
					<td style="width: 30%;">
						<input  id="myfmsjId" name="myfmsjId" textname="myfmsjName" readonly class="mini-user rxc"
								plugins="mini-user" style="width:98%;height:34px;"
								allowinput="false" label="可见范围" length="1000" maxlength="1000"
								mainfield="no"  single="false" />
					</td>
				</tr>
				<tr>
					<td style="width: 14%">变更前专利申请人：</td>
					<td style="width: 30%">
						<input  readonly id="zlsqr" name="zlsqr" class="mini-textbox" style="width:98%;"  />
					</td>
				</tr>
				<tr>
					<td style="width: 14%">变更后发明(设计)人：</td>
					<td style="width: 30%;">
						<input id="nfmsjr" name="nfmsjr"  class="mini-textbox" style="width:98%;" />
					</td>
				</tr>
				<tr>
					<td style="width: 14%">变更后我司发明(设计)人：</td>
					<td style="width: 30%;">
						<input  id="nmyfmsjId" name="nmyfmsjId" textname="nmyfmsjName" class="mini-user rxc"
								plugins="mini-user" style="width:98%;height:34px;"
								allowinput="false" label="可见范围" length="1000" maxlength="1000"
								mainfield="no"  single="false" />
					</td>
				</tr>
				<tr>
					<td style="width: 14%">变更后专利申请人：</td>
					<td style="width: 30%">
						<input  id="nzlsqr" name="nzlsqr" class="mini-textbox" style="width:98%;"  />
					</td>
				</tr>
				<tr>
					<td>变更原因：</td>
					<td>
						<input  id="reason" name="reason" class="mini-textarea"
								style="width:98%;height: 200px" emptytext="不少于100字"/>
					</td>
				</tr>
			</table>
		</form>
	</div>
</div>
<div id="selectZlWindow" title="选择需要表更的技术交底书" class="mini-window" style="width:750px;height:450px;"
	 showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
	<input id="parentInputScene" style="display: none" />
	<div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px"
		 borderStyle="border-left:0;border-top:0;border-right:0;">
		<span style="font-size: 14px;color: #777">提案名称: </span>
		<input class="mini-textbox" width="130" id="zlName" style="margin-right: 15px"/>
		<a class="mini-button" iconCls="icon-search" plain="true" onclick="searchStandard()">查询</a>
	</div>
	<div class="mini-fit">
		<div id="zlListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onrowdblclick="onRowDblClick"
			 idField="id"  showColumnsMenu="false" sizeList="[20,50,100,200]" pageSize="20"
			 allowAlternating="true" pagerButtons="#pagerButtons" url="${ctxPath}/zhgl/core/jsjds/getJsjdsList.do">
			<div property="columns">
				<div type="checkcolumn" width="25px"></div>
				<div field="zlName"  sortField="zlName"  width="100" headerAlign="center" align="center" allowSort="true">提案名称</div>
				<div field="zllx"  width="50" headerAlign="center" align="center" allowSort="false"renderer="onLXRenderer">专利类型</div>
			</div>
		</div>
	</div>
	<div property="footer" style="padding:5px;height: 35px">
		<table style="width:100%;height: 100%">
			<tr>
				<td style="width:120px;text-align:center;">
					<input type="button" style="height: 25px;width: 70px" value="确定" onclick="selectStandardOK()"/>
					<input type="button" style="height: 25px;width: 70px" value="取消" onclick="selectStandardHide()"/>
				</td>
			</tr>
		</table>
	</div>
</div>


<script type="text/javascript">
    mini.parse();
    var nodeVarsStr='${nodeVars}';
    var action="${action}";
    var status="${status}";
    var jsUseCtxPath="${ctxPath}";
    var selectZlWindow = mini.get("selectZlWindow");
    var zlListGrid=mini.get("zlListGrid");
    var currentUserId="${currentUserId}";
    var currentUserName = "${currentUserName}";
	var detailListGrid=mini.get("detailListGrid");
    var formCqbg = new mini.Form("#formCqbg");
    var cqbgId="${cqbgId}";
    var flowType = "${flowType}";

    function onLXRenderer(e) {
        var record = e.record;
        var zllx = record.zllx;

        var arr = [ {'key' : 'FM','value' : '发明'},
            {'key' : 'SYXX','value' : '实用新型'},
            {'key' : 'WGSJ','value' : '外观设计'}
        ];
        return $.formatItemValue(arr,zllx);
    }


    var first = "";

    $(function () {
        if (cqbgId) {
            var url = jsUseCtxPath + "/rdmZhgl/Cqbg/getCqbgDetail.do";
            $.post(
                url,
                {cqbgId: cqbgId},
                function (json) {
                    formCqbg.setData(json);
                });
        }else {
            mini.get("flowType").setValue(flowType);
		}
        if (action == 'task') {
            taskActionProcess();
        } else if (action == "detail") {
            formCqbg.setEnabled(false);
            $("#detailToolBar").show();
            if (status != 'DRAFTED') {
                $("#processInfo").show();
            }
        }
    });

    function getData() {
        var formData = _GetFormJsonMini("formCqbg");
        //此处用于向后台产生流程实例时替换标题中的参数时使用
        // formData.bos=[];
        // formData.vars=[{key:'companyName',val:formData.companyName}];
        return formData;
    }

    function saveCqbg(e) {
        var formValid = validCqbgFirst();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        window.parent.saveDraft(e);
    }

    function startCqbgProcess(e) {
        var formValid = validCqbgFirst();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }

        window.parent.startProcess(e);
    }

    function validCqbgFirst() {
        var jsjdsId = $.trim(mini.get("jsjdsId").getText())
        if (!jsjdsId) {
            return {"result": false, "message": "请选择需要变更的技术交底书"};
        }
        var nfmsjr = $.trim(mini.get("nfmsjr").getText())
        if (!nfmsjr) {
            return {"result": false, "message": "请填写变更后发明(设计)人"};
        }
        var nmyfmsjId = $.trim(mini.get("nmyfmsjId").getText())
        if (!nmyfmsjId) {
            return {"result": false, "message": "请填写变更后我司发明(设计)人"};
        }
        var nzlsqr = $.trim(mini.get("nzlsqr").getText())
        if (!nzlsqr) {
            return {"result": false, "message": "请填写变更后专利申请人"};
        }
        var reason = $.trim(mini.get("reason").getText())
        if (!reason) {
            return {"result": false, "message": "请填写变更原因"};
        }
        const regex = /[^\n]/g;
        // 使用 match 方法获取匹配结果
        const matches = reason.match(regex);
        // 计算匹配结果的长度
        const inputLength = matches ? matches.length : 0;
        // 判断长度是否超过限制
        if (inputLength < 100) {
            return {"result": false, "message": "变更原因不得少于100字"};
            // 如果超过限制，取消输入并恢复到上一次的内容
        }
        return {"result": true};
    }
    function cqbgApprove() {
        //编制阶段的下一步需要校验表单必填字段
        var formValid = validCqbgFirst();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
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
        }
        if (first != 'yes') {
            formCqbg.setEnabled(false);
        }
    }


    function selectStandard(inputScene){
        $("#parentInputScene").val(inputScene);
        selectZlWindow.show();
        searchStandard();
    }
    //查询标准
    function searchStandard() {
        var queryParam = [];
        //其他筛选条件
        var zlName = $.trim(mini.get("zlName").getValue());
        if (zlName) {
            queryParam.push({name: "zlName", value: zlName});
        }
        queryParam.push({name: "instStatus", value: "RUNNING"});
        queryParam.push({name: "applyUserId", value: currentUserId});
        var data = {};
        data.filter = mini.encode(queryParam);
        data.pageIndex = zlListGrid.getPageIndex();
        data.pageSize = zlListGrid.getPageSize();
        data.sortField = zlListGrid.getSortField();
        data.sortOrder = zlListGrid.getSortOrder();
        //查询
        zlListGrid.load(data);
    }

    function onRowDblClick() {
        selectStandardOK();
    }

    function selectStandardOK() {
        var selectRow = zlListGrid.getSelected();
        mini.get("jsjdsId").setValue(selectRow.jsjdsId);
        mini.get("jsjdsId").setText(selectRow.zlName);
        mini.get("fmsjr").setValue(selectRow.fmsjr);
        mini.get("myfmsjId").setValue(selectRow.myfmsjId);
        mini.get("myfmsjId").setText(selectRow.myfmsjName);
        mini.get("zlsqr").setValue(selectRow.zlsqr);
        selectStandardHide();
    }

    function selectStandardHide() {
        selectZlWindow.hide();
        mini.get("zlName").setValue('');
    }

    function onSelectStandardCloseClick(e) {
        mini.get("jsjdsId").setValue('');
        mini.get("jsjdsId").setText('');
        mini.get("fmsjr").setValue('');
        mini.get("myfmsjId").setValue('');
        mini.get("myfmsjId").setText('');
        mini.get("zlsqr").setValue('');
    }

</script>
</body>
</html>
