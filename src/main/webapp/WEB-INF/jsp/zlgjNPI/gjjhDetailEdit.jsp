<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<title>改进计划明细编辑</title>
	<%@include file="/commons/edit.jsp"%>
	<link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css" />
	<script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>

<body>
<div id="detailToolBar" class="topToolBar">
	<div>
		<a id="saveDetail" class="mini-button" onclick="saveDetail()">保存</a>
		<a id="closeWindow" class="mini-button btn-red" onclick="CloseWindow()">关闭</a>
	</div>
</div>
<div class="mini-fit">
	<div class="form-container" style="margin: 0 auto">
		<form id="detailForm" method="post">
			<input id="id" name="id" class="mini-hidden"/>
			<input id="belongGjjhId" name="belongGjjhId" class="mini-hidden"/>
			<input id="CREATE_BY_" name="CREATE_BY_" class="mini-hidden"/>
			<table class="table-detail" cellspacing="1" cellpadding="0">
				<tr>
					<td style="width: 11%">序号：</td>
					<td style="width: 23%;">
						<input id="xuhao" name="xuhao"  class="mini-textbox" style="width:98%;" />
					</td>
					<td style="width: 11%">机型：</td>
					<td style="width: 23%;">
						<input id="jixing" name="jixing"  class="mini-textbox" style="width:98%;" />
					</td>
					<td style="width: 11%">部件：</td>
					<td style="width: 23%;">
						<input id="bujian" name="bujian"  class="mini-textbox" style="width:98%;" />
					</td>
				</tr>
				<tr>
					<td style="width: 11%">时间：</td>
					<td style="width: 23%;">
						<input id="problemTime"  name="problemTime"  class="mini-datepicker" format="yyyy-MM-dd" style="width:98%"/>
					</td>
					<td style="width: 11%">故障信息：</td>
					<td colspan="3">
						<textarea id="problemDesc" name="problemDesc" class="mini-textarea rxc" plugins="mini-textarea" style="width:99%;;height:80px;line-height:25px;" label="" datatype="varchar" length="200" vtype="length:200" minlen="0" allowinput="true"
								  emptytext="" mwidth="80" wunit="%" mheight="110" hunit="px"></textarea>
					</td>
				</tr>
				<tr><td style="width: 11%">责任部门：</td>
					<td style="width: 23%;">
						<input id="respDeptNames" name="respDeptNames"  class="mini-textbox" style="width:98%;" />
					</td>
					<td style="width: 11%">责任人：</td>
					<td style="width: 23%;min-width:170px">
						<input id="respUserIds" name="respUserIds" textname="respUserNames" onvaluechanged="checkEditRole()"
							   property="editor" class="mini-user rxc" plugins="mini-user"  style="width:98%;height:34px;" allowinput="false"  mainfield="no"  single="false" />
					</td>
					<td style="width: 11%">完成时间：</td>
					<td style="width: 23%;">
						<input id="finishTime"  name="finishTime"  class="mini-datepicker" format="yyyy-MM-dd" style="width:98%"/>
					</td>
				</tr>
				<div id="taskContent">
					<tr>
						<td style="width: 11%">原因分析(800字以内)：</td>
						<td colspan="5">
						<textarea id="reason" name="reason" class="mini-textarea rxc" plugins="mini-textarea" style="width:99%;;height:80px;line-height:25px;" label="" datatype="varchar" length="1000" vtype="length:1000" minlen="0" allowinput="true"
								  emptytext="" mwidth="80" wunit="%" mheight="110" hunit="px"></textarea>
						</td>
					</tr>
					<tr>
						<td style="width: 11%">对策(800字以内)：</td>
						<td colspan="5">
						<textarea id="duice" name="duice" class="mini-textarea rxc" plugins="mini-textarea" style="width:99%;;height:80px;line-height:25px;" label="" datatype="varchar" length="1000" vtype="length:1000" minlen="0" allowinput="true"
								  emptytext="" mwidth="80" wunit="%" mheight="110" hunit="px"></textarea>
						</td>
					</tr>
					<tr>
						<td style="width: 11%">市场方案(800字以内)：</td>
						<td colspan="5">
						<textarea id="fangan" name="fangan" class="mini-textarea rxc" plugins="mini-textarea" style="width:99%;;height:80px;line-height:25px;" label="" datatype="varchar" length="1000" vtype="length:1000" minlen="0" allowinput="true"
								  emptytext="" mwidth="80" wunit="%" mheight="110" hunit="px"></textarea>
						</td>
					</tr>
					<tr>
						<td style="width: 11%">完成情况(800字以内)：</td>
						<td colspan="5">
						<textarea id="finishDesc" name="finishDesc" class="mini-textarea rxc" plugins="mini-textarea" style="width:99%;;height:80px;line-height:25px;" label="" datatype="varchar" length="1000" vtype="length:1000" minlen="0" allowinput="true"
								  emptytext="" mwidth="80" wunit="%" mheight="110" hunit="px"></textarea>
						</td>
					</tr>
				</div>
			</table>
		</form>
	</div>
</div>


<script type="text/javascript">
    mini.parse();
    var action="${action}";
    var jsUseCtxPath="${ctxPath}";
    var detailForm = new mini.Form("#detailForm");
    var id="${id}";
    var formId="${formId}";
    var currentUserId="${currentUserId}";
    var isZLGJ =${isZLGJ};

    $(function () {
        if(id) {
            var url = jsUseCtxPath + "/zhgl/core/zlgj/queryGjjhDetailById.do";
            $.ajaxSettings.async = false;
            $.post(
                url,
                {gjjhDetailId: id},
                function (json) {
                    detailForm.setData(json);
                });
            $.ajaxSettings.async = true;
        }
        if(action=='detail') {
            detailForm.setEnabled(false);
            $("#saveDetail").hide();
        }else if(action=='add') {
            mini.get("belongGjjhId").setValue(formId);
            mini.get("reason").setEnabled(false);
            mini.get("duice").setEnabled(false);
            mini.get("fangan").setEnabled(false);
            mini.get("finishDesc").setEnabled(false);
        } else if(action=='edit') {
            var CREATE_BY_=mini.get("CREATE_BY_").getValue();
            if(CREATE_BY_ !=currentUserId&&!isZLGJ) {
                mini.get("xuhao").setEnabled(false);
                mini.get("jixing").setEnabled(false);
                mini.get("bujian").setEnabled(false);
                mini.get("problemTime").setEnabled(false);
                mini.get("problemDesc").setEnabled(false);
                mini.get("respDeptNames").setEnabled(false);
                mini.get("respUserIds").setEnabled(false);
                mini.get("finishTime").setEnabled(false);
            }
            var respUserIds=mini.get("respUserIds").getValue();
            if(!respUserIds || respUserIds.indexOf(currentUserId)<0) {
                mini.get("reason").setEnabled(false);
                mini.get("duice").setEnabled(false);
                mini.get("fangan").setEnabled(false);
                mini.get("finishDesc").setEnabled(false);
            }
        }
    });

    function saveDetail() {
        if(action=='add') {
            var checkResult=problemAndRespCheck();
            if(!checkResult.result) {
                mini.alert(checkResult.message);
                return;
            }
        } else if(action=='edit') {
            var CREATE_BY_=mini.get("CREATE_BY_").getValue();
            var respUserIds=mini.get("respUserIds").getValue();
            if(CREATE_BY_ ==currentUserId) {
                var checkResult=problemAndRespCheck();
                if(!checkResult.result) {
                    mini.alert(checkResult.message);
                    return;
                }
            }
            if(respUserIds && respUserIds.indexOf(currentUserId)>=0) {
                var checkResult=taskCheck();
                if(!checkResult.result) {
                    mini.alert(checkResult.message);
                    return;
                }
            }
        }
        saveDetailDo();
    }

    function problemAndRespCheck() {
        var xuhao=$.trim(mini.get("xuhao").getValue())
        if(!xuhao) {
            return{"result":false,"message":"请填写序号"};
        }
        var jixing=$.trim(mini.get("jixing").getValue())
        if(!jixing) {
            return{"result":false,"message":"请填写机型"};
        }
        var bujian=$.trim(mini.get("bujian").getValue())
        if(!bujian) {
            return{"result":false,"message":"请填写部件"};
        }
        var problemTime=$.trim(mini.get("problemTime").getValue())
        if(!problemTime) {
            return{"result":false,"message":"请填写时间"};
        }
        var problemDesc=$.trim(mini.get("problemDesc").getValue())
        if(!problemDesc) {
            return{"result":false,"message":"请填写故障信息"};
        }
        var respDeptNames=$.trim(mini.get("respDeptNames").getValue())
        if(!respDeptNames) {
            return{"result":false,"message":"请填写责任部门"};
        }
        var respUserIds=$.trim(mini.get("respUserIds").getValue())
        if(!respUserIds) {
            return{"result":false,"message":"请填写责任人"};
        }
        var finishTime=$.trim(mini.get("finishTime").getValue())
        if(!finishTime) {
            return{"result":false,"message":"请填写完成时间"};
        }
        return{"result":true};
    }

    function taskCheck() {
        var reason=$.trim(mini.get("reason").getValue());
        if(!reason) {
            return{"result":false,"message":"请填写原因分析"};
        }
        var duice=$.trim(mini.get("duice").getValue());
        if(!duice) {
            return{"result":false,"message":"请填写对策"};
        }
        var fangan=$.trim(mini.get("fangan").getValue());
        if(!fangan) {
            return{"result":false,"message":"请填写市场方案"};
        }
        /*        var finishDesc=$.trim(mini.get("finishDesc").getValue());
                if(!finishDesc) {
                    return{"result":false,"message":"请填写完成情况"};
                }*/

        return{"result":true};
    }

    function saveDetailDo() {
        var formData = _GetFormJsonMini("detailForm");
        $.ajax({
            url: jsUseCtxPath + '/zhgl/core/zlgj/saveGjjhDetail.do',
            type: 'post',
            async: false,
            data:mini.encode(formData),
            contentType: 'application/json',
            success: function (returnObj) {
                CloseWindow();
            }
        });
    }

    function checkEditRole() {
        var respUserIds=mini.get("respUserIds").getValue();
        if(respUserIds && respUserIds.indexOf(currentUserId)>=0) {
            mini.get("reason").setEnabled(true);
            mini.get("duice").setEnabled(true);
            mini.get("fangan").setEnabled(true);
            mini.get("finishDesc").setEnabled(true);
        } else {
            mini.get("reason").setEnabled(false);
            mini.get("duice").setEnabled(false);
            mini.get("fangan").setEnabled(false);
            mini.get("finishDesc").setEnabled(false);
        }
    }

</script>
</body>
</html>
