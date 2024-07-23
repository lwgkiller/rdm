<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<title>工艺反馈信息说明编辑</title>
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
	<div class="form-container" style="margin: 0 auto; width: 100%">
		<form id="formFsj" method="post" >
			<input id="fsjId" name="fsjId" class="mini-hidden"/>
			<input id="instId" name="instId" class="mini-hidden"/>
			<table class="table-detail grey"  cellspacing="1" cellpadding="0">
				<caption style="font-weight: bold;font-size: 20px !important;">
					新品附属件配置
				</caption>
				<tr>
					<td style="width: 15%">产品主管<span style="color: #ff0000">*</span>：</td>
					<td>
						<input id="cpzg" name="cpzgId" textname="cpzgName"
							   property="editor" class="mini-user rxc" plugins="mini-user"
							   style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"
							   onvaluechanged="setRespDept()"/>
					</td>
					<td style="width: 15%">部门<span style="color: #ff0000">*</span>：</td>
					<td style="min-width:170px;font-size:14pt">
						<input id="dept" name="deptId" class="mini-dep rxc" plugins="mini-dep"
							   style="width:98%;height:34px"
							   allowinput="false" textname="deptName" single="true" initlogindep="false"/>
					</td>
				</tr>
				<tr>
					<td style="width: 15%">产品型号<span style="color: #ff0000">*</span>：</td>
					<td>
						<input id="cpxh" name="cpxh" class="mini-textbox" style="width:98%;"/>
					</td>
				<td style="width: 15%">说明</td>
				<td>
					<input id="note" name="note" class="mini-textbox" style="width:98%;"/>
				</td>
				</tr>
				<tr>
					<td style="width: 15%">生产批次号<span style="color: #ff0000">*</span>：</td>
					<td>
						<input id="scpch" name="scpch" class="mini-textbox" style="width:98%;"/>
					</td>
					<td style="width: 15%">台数<span style="color: #ff0000">*</span>:</td>
					<td>
						<input id="num" name="num" class="mini-textbox" style="width:98%;"/>
					</td>
				</tr>
				<tr>
					<td style="width: 15%">排放标准<span style="color: #ff0000">*</span>：</td>
					<td>
						<input id="pfbz" name="pfbz" class="mini-textbox" style="width:98%;"/>
					</td>
					<td style="width: 15%">设计型号物料号<span style="color: #ff0000">*</span>:</td>
					<td>
						<input id="sjxhwlh" name="sjxhwlh" class="mini-textbox" style="width:98%;"/>
					</td>
				</tr>
				<tr>
					<td style="width: 15%">产品销售型号<span style="color: #ff0000">*</span>：</td>
					<td>
						<input id="cpxsxh" name="cpxsxh" class="mini-textbox" style="width:98%;"/>
					</td>
					<td style="width: 15%">整机编号(所有首制+小批车辆)<span style="color: #ff0000">*</span>:</td>
					<td>
						<input id="zjbh" name="zjbh" class="mini-textbox" style="width:98%;"/>
					</td>
				</tr>
				<tr>
					<td style="width: 15%">车辆去向<span style="color: #ff0000">*</span>：</td>
					<td>
						<input id="clqx" name="clqxId" class="mini-dep rxc" plugins="mini-dep"
							   style="width:98%;height:34px"
							   allowinput="false" textname="clqxName" single="true" initlogindep="false"/>
					</td>
				</tr>
				<tr>
					<td style="width: 15%;height: 750px">详细信息：</td>
					<td colspan="4">
						<div style="margin-top: 5px;margin-bottom: 2px">
							<a id="addFsjDetail" class="mini-button"  onclick="addFsjDetail()">添加</a>
							<a id="editFsjDetail" class="mini-button"  onclick="editFsjDetail()">编辑</a>
							<a id="removeFsjDetail" class="mini-button"  onclick="removeFsjDetail()">删除</a>
						</div>
						<div id="detailListGrid" class="mini-datagrid" style="width: 100%; height: 85%" allowResize="false" allowCellWrap="true"
							 idField="id" url="${ctxPath}/zlgjNPI/core/Fsj/getFsjDetailList.do?belongId=${fsjId}" autoload="true"
							 multiSelect="false" showPager="false" showColumnsMenu="false" allowAlternating="true" >
							<div property="columns">
								<div type="checkcolumn" width="10"></div>
								<div name="action" cellCls="actionIcons" width="20" headerAlign="center" align="center" renderer="onMessageActionRenderer" cellStyle="padding:0;">操作</div>
								<div field="bjname"  width="40" headerAlign="center" align="center" >部件名称</div>
								<div field="supplier"  width="80" headerAlign="center" align="center" >供应商名称</div>
								<div field="bten"  width="60" headerAlign="center" align="center" >设计B10寿命</div>
								<div field="sqsx"  width="60" headerAlign="center" align="center" >3000h失效率</div>
								<div field="qqsx"  width="60" headerAlign="center" align="center" >7000h失效率</div>
								<div field="zjsb"  width="60" headerAlign="center" align="center" >整机三包期</div>
								<div field="bjsb"  width="60" headerAlign="center" align="center" >部件三包期</div>
								<div field="cy"  width="60" headerAlign="center" align="center" >差异</div>
								<div field="smyz"  width="60" headerAlign="center" align="center" >寿命验证方法</div>
								<div field="wcrq" width="60" dateFormat="yyyy-MM-dd" headerAlign='center' align="center">完成日期</div>
								<div field="gzzz"  width="60" headerAlign="center" align="center" >工作装置</div>
								<%--<div field="psccj"  width="60" headerAlign="center" align="center" >破碎锤厂家</div>--%>
								<%--<div field="pscxh"  width="60" headerAlign="center" align="center" >破碎锤型号</div>--%>
								<%--<div field="psgl"  width="60" headerAlign="center" align="center" >破碎管路</div>--%>
								<%--<div field="pscwlh"  width="60" headerAlign="center" align="center" >破碎锤物料号</div>--%>
								<%--<div field="sjglwl"  width="60" headerAlign="center" align="center" >设计破碎管路物料号</div>--%>
								<%--<div field="bjglwl"  width="60" headerAlign="center" align="center" >备件破碎管路物料号</div>--%>
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
    var nodeVarsStr='${nodeVars}';
    var action="${action}";
    var status="${status}";
    var jsUseCtxPath="${ctxPath}";
    var currentUserId="${currentUserId}";
    var currentUserName = "${currentUserName}";
	var detailListGrid=mini.get("detailListGrid");
    var formFsj = new mini.Form("#formFsj");
    var fsjId="${fsjId}";
    
    function fujian(e) {
        var record = e.record;
        var detailId = record.detailId;
        var s = '';
        s += '<span style="color:dodgerblue" title="附件列表" onclick="fsjFile(\'' + detailId + '\')">附件列表</span>';
        return s;
    }

    function onMessageActionRenderer(e) {
        var record = e.record;
        var detailId = record.detailId;
        var s = '';
		s += '<span  title="明细" onclick="lookFsjDetail(\'' + detailId + '\')">明细</span>';
        return s;
    }

    function setRespDept() {
        var userId=mini.get("cpzg").getValue();
        if(!userId) {
            mini.get("dept").setValue('');
            mini.get("dept").setText('');
            return;
        }
        $.ajax({
            url: jsUseCtxPath + '/xcmgProjectManager/core/xcmgProject/' + userId + '/getUserInfoById.do',
            type: 'get',
            async: false,
            contentType: 'application/json',
            success: function (data) {
                mini.get("dept").setValue(data.mainDepId);
                mini.get("dept").setText(data.mainDepName);
            }
        });
    }

    var first = "";
    


    $(function () {
        if (fsjId) {
            var url = jsUseCtxPath + "/zlgjNPI/core/Fsj/getFsj.do";
            $.post(
                url,
                {fsjId: fsjId},
                function (json) {
                    formFsj.setData(json);
                });
        }
        if (action == 'task') {
            taskActionProcess();
        } else if (action == "detail") {
            formFsj.setEnabled(false);
            mini.get("addFsjDetail").setEnabled(false);
            mini.get("editFsjDetail").setEnabled(false);
            mini.get("removeFsjDetail").setEnabled(false);
            $("#detailToolBar").show();
            if (status != 'DRAFTED') {
                $("#processInfo").show();
            }
        }
    });

    function getData() {
        var formData = _GetFormJsonMini("formFsj");
        //此处用于向后台产生流程实例时替换标题中的参数时使用
        // formData.bos=[];
        // formData.vars=[{key:'companyName',val:formData.companyName}];
        return formData;
    }

    function saveFsj(e) {
        // var formValid = validFsjFirst();
        // if (!formValid.result) {
        //     mini.alert(formValid.message);
        //     return;
        // }
        window.parent.saveDraft(e);
    }

    function addFsjDetail() {
        var fsjId = mini.get("fsjId").getValue();
        if (!fsjId) {
            mini.alert('请先点击‘保存’进行表单的保存！');
            return;
        }
        mini.open({
            title: "计划明细",
            url: jsUseCtxPath + "/zlgjNPI/core/Fsj/editFsjDetail.do?fsjId=" + fsjId + "&action=add",
            width: 1050,
            height: 450,
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

    function editFsjDetail() {
        var fsjId = mini.get("fsjId").getValue();
        if (!fsjId) {
            mini.alert('请先点击‘保存’进行表单的保存！');
            return;
        }
        var row = detailListGrid.getSelected();
        if (row) {
            var detailId = row.detailId;
			mini.open({
				title: "计划明细",
				url: jsUseCtxPath + "/zlgjNPI/core/Fsj/editFsjDetail.do?detailId=" + detailId + "&action=edit&fsjId=" + fsjId,
				width: 1050,
				height: 450,
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

    function removeFsjDetail() {
        var fsjId = mini.get("fsjId").getValue();
        if (!fsjId) {
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
                        url: jsUseCtxPath + "/zlgjNPI/core/Fsj/deleteFsjDetail.do?fsjId=" + fsjId,
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

    function lookFsjDetail(detailId) {
        var fsjId = mini.get("fsjId").getValue();
        mini.open({
            title: "计划明细",
            url: jsUseCtxPath + "/zlgjNPI/core/Fsj/editFsjDetail.do?detailId=" + detailId + "&action=detail&fsjId=" + fsjId,
            width: 1050,
            height: 450,
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

    function fsjFile(detailId) {
        mini.open({
            title: "附件列表",
            url: jsUseCtxPath + "/zlgjNPI/core/Fsj/fileList.do?&detailId=" + detailId,
            width: 1050,
            height: 550,
            allowResize: true,
            onload: function () {
            }
        });
    }

    function startFsjProcess(e) {
        var formValid = validFsjFirst();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        var gridData = detailListGrid.getData();
        if (gridData.length<1) {
            mini.alert("请填写详细信息！");
            return;
        }
        window.parent.startProcess(e);
    }

    function validFsjFirst() {
        var cpzg = $.trim(mini.get("cpzg").getText());
        if (!cpzg) {
            return {"result": false, "message": "请选择产品主管"};
        }
        var dept = $.trim(mini.get("dept").getText());
        if (!dept) {
            return {"result": false, "message": "请选择部门"};
        }
        var cpxh=$.trim(mini.get("cpxh").getValue());
        if(!cpxh) {
            return {"result": false, "message": "请填写产品型号"};
        }
        var scpch=$.trim(mini.get("scpch").getValue());
        if(!scpch) {
            return {"result": false, "message": "请填写生产批次号"};
        }
        var num=$.trim(mini.get("num").getValue());
        if(!num) {
            return {"result": false, "message": "请填写台数"};
        }
        var pfbz=$.trim(mini.get("pfbz").getValue());
        if(!pfbz) {
            return {"result": false, "message": "请填写排放标准"};
        }
        var sjxhwlh=$.trim(mini.get("sjxhwlh").getValue());
        if(!sjxhwlh) {
            return {"result": false, "message": "请填写设计型号物料号"};
        }
        var cpxsxh=$.trim(mini.get("cpxsxh").getValue());
        if(!cpxsxh) {
            return {"result": false, "message": "请填写产品销售型号"};
        }
        var zjbh=$.trim(mini.get("zjbh").getValue());
        if(!zjbh) {
            return {"result": false, "message": "请填写整机编号"};
        }
        var clqx=$.trim(mini.get("clqx").getValue());
        if(!clqx) {
            return {"result": false, "message": "请填写车辆去向"};
        }
        return {"result": true};
    }



    function fsjApprove() {

        //编制阶段的下一步需要校验表单必填字段
        var formValid = validFsjFirst();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        var gridData = detailListGrid.getData();
        if (gridData.length<1) {
            mini.alert("请填写详细信息！");
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
            formFsj.setEnabled(false);
            mini.get("addFsjDetail").setEnabled(false);
            mini.get("editFsjDetail").setEnabled(false);
            mini.get("removeFsjDetail").setEnabled(false);
        }
    }

</script>
</body>
</html>
