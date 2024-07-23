<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<title>标准编辑</title>
	<%@include file="/commons/edit.jsp"%>
	<link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css" />
	<script src="${ctxPath}/scripts/standardManager/standardEdit.js?version=${static_res_version}"
			type="text/javascript"></script>
	<script src="${ctxPath}/scripts/standardManager/standardUtil.js?version=${static_res_version}"
			type="text/javascript"></script>
</head>

<body>
<div id="detailToolBar" class="topToolBar">
	<div>
		<a id="saveTxl" class="mini-button" onclick="saveTxl()">保存</a>
		<a id="closeWindow" class="mini-button btn-red" onclick="CloseWindow()">关闭</a>
	</div>
</div>
<div class="mini-fit">
	<div class="form-container" style="margin: 0 auto">
		<form id="formTxl" method="post">
			<input id="id" name="id" class="mini-hidden"/>
			<table class="table-detail" cellspacing="1" cellpadding="0">
				<tr>
					<td style="width: 10%">公司名称：<span style="color: #ff0000">*</span></td>
					<td style="min-width:170px">
						<input id="companyName" name="companyName" class="mini-combobox" style="width:98%;"
							   textField="companyName" valueField="companyName" emptyText="请选择..."
							   required="false" allowInput="false" showNullItem="false" nullItemText="请选择..."
							   onvaluechanged="companyChange"
						/>
					</td>
					<td style="width: 10%">一级部门：</td>
					<td style="min-width:170px">
						<input id="deptNameLevelOne" name="deptNameLevelOne" class="mini-combobox" style="width:98%;"
							   textField="deptNameLevelOne" valueField="deptNameLevelOne" emptyText="请选择..."
							   required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
							   onvaluechanged="deptNameLevelOneChange"
						/>
					</td>
					<td style="width: 10%">二级部门：</td>
					<td style="min-width:170px">
						<input id="deptNameLevelTwo" name="deptNameLevelTwo" class="mini-combobox" style="width:98%;"
							   textField="deptNameLevelTwo" valueField="deptNameLevelTwo" emptyText="请选择..."
							   required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
						/>
					</td>
				</tr>
				<tr>
					<td style="width: 10%">名称：<span style="color: #ff0000">*</span></td>
					<td style="min-width:170px">
						<input class="mini-textbox" style="width: 98%" id="txlName"  name="txlName" />
					</td>
					<td style="width: 10%">固定电话：</td>
					<td style="min-width:170px">
						<input class="mini-textbox" style="width: 98%" id="txlZj"  name="txlZj" />
					</td>
					<td style="width: 10%">手机号码：</td>
					<td style="min-width:170px">
						<input class="mini-textbox" style="width: 98%" id="txlSj"  name="txlSj" />
					</td>
				</tr>
				<tr>
					<td style="width: 10%">办公地点：</td>
					<td style="min-width:170px">
						<input class="mini-textbox" style="width: 98%" id="office"  name="office" />
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
    var txlObj=${txlObj};
    var id="${id}";
    var deptNameLevelOneList=${deptNameLevelOneList};
    var deptNameLevelTwoList=${deptNameLevelTwoList};

    var formTxl=new mini.Form('formTxl');

    $(function () {
        if(id) {
            formTxl.setData(txlObj);
            mini.get("deptNameLevelOne").setData(deptNameLevelOneList);
            mini.get("deptNameLevelTwo").setData(deptNameLevelTwoList);
        }
        $.ajax({
            url: "${ctxPath}/zhgl/core/txl/queryCompany.do",
            type: "get",
            success: function (result) {
                if (result) {
                    mini.get("companyName").setData(result);
                }
            }
        });
    });

    function companyChange() {
        mini.get("deptNameLevelOne").setData([]);
        mini.get("deptNameLevelOne").setValue('');
        mini.get("deptNameLevelTwo").setData([]);
        mini.get("deptNameLevelTwo").setValue('');
        $.ajax({
            url: "${ctxPath}/zhgl/core/txl/queryDeptNameLevelOne.do?companyName="+mini.get("companyName").getValue(),
            type: "get",
            success: function (result) {
                if (result) {
                    mini.get("deptNameLevelOne").setData(result);
                }
            }
        });
    }

    function deptNameLevelOneChange() {
        mini.get("deptNameLevelTwo").setData([]);
        mini.get("deptNameLevelTwo").setValue('');
        $.ajax({
            url: "${ctxPath}/zhgl/core/txl/queryDeptNameLevelTwo.do?companyName="+mini.get("companyName").getValue()+"&deptNameLevelOne="+mini.get("deptNameLevelOne").getValue(),
            type: "get",
            success: function (result) {
                if (result) {
                    mini.get("deptNameLevelTwo").setData(result);
                }
            }
        });
    }
    
    function saveTxl() {
        var dataPost={};
        var companyName=mini.get("companyName").getValue();
        if(companyName) {
            dataPost.companyName=companyName;
		}
        if(!companyName) {
            mini.alert("请选择公司名称！");
            return;
        }
        var deptNameLevelOne=mini.get("deptNameLevelOne").getValue();
        if(deptNameLevelOne) {
            dataPost.deptNameLevelOne=deptNameLevelOne;
        }
        var deptNameLevelTwo=mini.get("deptNameLevelTwo").getValue();
        if(deptNameLevelTwo) {
            dataPost.deptNameLevelTwo=deptNameLevelTwo;
        }
        var txlName=mini.get("txlName").getValue();
        if(txlName) {
            dataPost.txlName=txlName;
        }
        if(!txlName) {
            mini.alert("请填写名称！");
            return;
        }
        var txlZj=mini.get("txlZj").getValue();
        if(txlZj) {
            dataPost.txlZj=txlZj;
        }
        var txlSj=mini.get("txlSj").getValue();
        if(txlSj) {
            dataPost.txlSj=txlSj;
        }
        var office=mini.get("office").getValue();
        if(office) {
            dataPost.office=office;
        }
        var id=mini.get("id").getValue();
        if(id) {
            dataPost.id=id;
        }

        $.ajax({
            url: jsUseCtxPath+"/zhgl/core/txl/saveTxl.do",
            data: mini.encode(dataPost),
            type: "post",
            contentType: 'application/json',
            async:false,
            success: function (result) {
                if (result) {
                    mini.alert(result.message,"提示",function () {
                        if(result.success) {
                            CloseWindow();
                        }
                    });
                }
            }
        });
    }

</script>
</body>
</html>
