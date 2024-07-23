
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<title>物料扩充申请单</title>
	<%@include file="/commons/edit.jsp"%>
	<script src="${ctxPath}/scripts/materielExtend/materielExtendApplyEdit.js?version=${static_res_version}"
			type="text/javascript"></script>
	<script src="${ctxPath}/scripts/materielExtend/CommonUtil.js?version=${static_res_version}"
			type="text/javascript"></script>
	<link href="${ctxPath}/scripts/mini/miniui/themes/icons.css" rel="stylesheet" type="text/css" />
</head>


<body>
<div id="toolBar" class="topToolBar">
	<div>
		<a id="saveTempMaterielApply" class="mini-button" onclick="saveTempMaterielApply()">暂存</a>
		<a id="commitMaterielApply" class="mini-button" onclick="commitMaterielApply()">提交</a>
		<a id="syncMateriels2SAP" class="mini-button" style="display: none" onclick="syncMateriels2SAP()">更新到SAP</a>
		<a id="closeWindow" class="mini-button btn-red" onclick="CloseWindow()">关闭</a>
	</div>
</div>
<div id="loading" class="loading" style="display:none;text-align:center;"><img src="${ctxPath}/styles/images/loading.gif"></div>
<div class="mini-fit" id="content">
	<div class="form-container" style="margin: 0 auto">
		<form id="materielApplyForm" method="post">
			<input id="applyNo" name="applyNo" class="mini-hidden"/>
			<p style="font-size: 16px;font-weight: bold">申请单信息</p><hr />
			<table class="table-detail grey" cellspacing="1" cellpadding="0" style="height: 200px">
				<tr>
					<td colspan="2">申请人：</td>
					<td colspan="2">
						<input name="applyUserName"  class="mini-textbox" style="width:98%;" />
						<input id="sqUserId" name="sqUserId" class="mini-hidden" />
					</td>
					<td colspan="2">申请人部门：</td>
					<td colspan="2">
						<input name="applyUserDepName"  class="mini-textbox" style="width:98%;" />
					</td>
					<td>是否紧急：</td>
					<td>
						<input id="urgent" name="urgent"  class="mini-checkbox" style="width:98%;" />
					</td>
					<td>联系电话：<span style="color:red">*</span></td>
					<td>
						<input id="applyUserMobile" name="applyUserMobile"  class="mini-textbox" style="width:98%;" />
					</td>
				</tr>
				<tr>
					<td style="width: 5%">工艺扩充状态：</td>
					<td style="width: 10%;">
						<input id="gyStatus" name="gyStatus"  class="mini-textbox" style="width:98%;" />
					</td>
					<td style="width: 5%">工艺处理人：</td>
					<td style="width: 15%;">
						<input name="gyCommitUserName"  class="mini-textbox" style="width:98%;" />
					</td>
					<td style="width: 5%">工艺完成时间：</td>
					<td style="width: 10%">
						<input name="gyCommitTime"  class="mini-textbox" style="width:98%;" />
					</td>
					<td style="width: 5%">供方扩充状态：</td>
					<td style="width: 10%;">
						<input id="gfStatus" name="gfStatus"  class="mini-textbox" style="width:98%;" />
					</td>
					<td style="width: 5%">供方处理人：</td>
					<td style="width: 15%">
						<input name="gfCommitUserName"  class="mini-textbox" style="width:98%;" />
					</td>
					<td style="width: 5%">供方完成时间：</td>
					<td style="width: 10%">
						<input name="gfCommitTime"  class="mini-textbox" style="width:98%;" />
					</td>
				</tr>
				<tr>
					<td style="width: 5%">采购扩充状态：</td>
					<td style="width: 10%;">
						<input id="cgStatus" name="cgStatus"  class="mini-textbox" style="width:98%;" />
					</td>
					<td style="width: 5%">采购处理人：</td>
					<td style="width: 15%;">
						<input name="cgCommitUserName"  class="mini-textbox" style="width:98%;" />
					</td>
					<td style="width: 5%">采购完成时间：</td>
					<td style="width: 10%">
						<input name="cgCommitTime"  class="mini-textbox" style="width:98%;" />
					</td>
					<td style="width: 5%">财务扩充状态：</td>
					<td style="width: 10%;">
						<input id="cwStatus" name="cwStatus"  class="mini-textbox" style="width:98%;" />
					</td>
					<td style="width: 5%">财务处理人：</td>
					<td style="width: 15%;">
						<input name="cwCommitUserName"  class="mini-textbox" style="width:98%;" />
					</td>
					<td style="width: 5%">财务完成时间：</td>
					<td style="width: 10%">
						<input name="cwCommitTime"  class="mini-textbox" style="width:98%;" />
					</td>
				</tr>
				<tr>
					<td style="width: 5%">物流扩充状态：</td>
					<td style="width: 10%;">
						<input id="wlStatus" name="wlStatus"  class="mini-textbox" style="width:98%;" />
					</td>
					<td style="width: 5%">物流处理人：</td>
					<td style="width: 15%;">
						<input name="wlCommitUserName"  class="mini-textbox" style="width:98%;" />
					</td>
					<td style="width: 5%">物流完成时间：</td>
					<td style="width: 10%">
						<input name="wlCommitTime"  class="mini-textbox" style="width:98%;" />
					</td>
					<td style="width: 5%">制造扩充状态：</td>
					<td style="width: 10%;">
						<input id="zzStatus" name="zzStatus"  class="mini-textbox" style="width:98%;" />
					</td>
					<td style="width: 5%">制造处理人：</td>
					<td style="width: 15%">
						<input name="zzCommitUserName"  class="mini-textbox" style="width:98%;" />
					</td>
					<td style="width: 5%">制造完成时间：</td>
					<td style="width: 10%">
						<input name="zzCommitTime"  class="mini-textbox" style="width:98%;" />
					</td>
				</tr>
			</table>
		</form>
		<p style="font-size: 16px;font-weight: bold;margin-top: 20px">物料信息</p><hr />
		<div class="mini-toolbar" id="materielProcessButtons" style="margin-bottom: 5px">
			<a class="mini-button" plain="true" id="batchImport" onclick="batchImportOpen">批量导入（新增/更新）</a>
			<a class="mini-button" plain="true" id="exportAll" onclick="exportMateriels('all')">导出全部</a>
			<a class="mini-button" plain="true" id="exportRight" onclick="exportMateriels('right')">导出正确物料</a>
			<a class="mini-button btn-yellow"  plain="true" id="exportFail" onclick="exportMateriels('fail')">导出问题物料</a>
			<div style="display: inline-block" class="separator"></div>
			<a class="mini-button"   plain="true" id="addOneMateriel" onclick="editOrViewMateriel('','edit')">单条添加</a>
			<a class="mini-button btn-red"  plain="true" id="delMateriel" onclick="deleteMateriel()">删除</a>
			<a class="mini-button"  plain="true" id="refreshMateriel" onclick="refreshMateriel()">刷新</a>
			<p style="display: inline-block;color: red;font-size:15px;vertical-align: middle">
				（“SAP校验失败”：SAP调用失败，请联系座机61937）
			</p>
			<span style="cursor:pointer;color:#44cef6;text-decoration:underline;float:right;margin-right: 20px" onclick="jumpToPropertyDesc()">物料各字段填写说明</span>
		</div>
		<div id="materielGrid" class="mini-datagrid"  allowResize="false"
			 idField="id" allowCellEdit="false" allowCellSelect="true" allowSortColumn="false"
			 multiSelect="true" showColumnsMenu="false" allowAlternating="true" showPager="false" style="height: 500px"
			 allowCellWrap="false" showVGridLines="true">
			<div property="columns">
				<div type="checkcolumn" width="30px"></div>
				<div renderer="materielOperateRenderer" width="140px" cellCls="actionIcons" headerAlign="center" >操作</div>
				<div type="indexcolumn" headerAlign="center" align="center" width="60px">序号</div>
				<div field="extendResult" headerAlign="center" width="75px" align="center" headerStyle="background:yellow">扩充结果</div>
				<div field="markError"  headerAlign="center" align="center" width="140px" headerStyle="background:yellow">是否为问题物料
					<image src="${ctxPath}/styles/images/question2.png" style="cursor: pointer;vertical-align: middle" title="标注为“是”后，该条物料将不需要继续扩充，最终也不会被更新到SAP"/>
				</div>
				<div field="markErrorReason"  headerAlign="center" align="center" width="115px" headerStyle="background:yellow">问题原因</div>
				<div field="markErrorUserName" width="85px" headerAlign="center" align="center" headerStyle="background:yellow">问题标注人</div>
				<div field="wlhm" width="120px" headerAlign="center" align="center" >物料号码</div>
				<div field="wllx" width="80px" headerAlign="center" align="center" >物料类型</div>
				<div field="wlms"  headerAlign="center" align="center" width="220px">物料描述</div>
				<div field="dw" width="70px" headerAlign="center" align="center" >单位</div>
				<div field="wlz" width="80px" headerAlign="center" align="center" >物料组</div>
				<div field="jkgc" width="70px" headerAlign="center" align="center" >进口国产</div>
				<div field="gc" width="65px" headerAlign="center" align="center" >工厂</div>
				<div field="xszz"  width="70px" headerAlign="center" align="center" >销售组织</div>
				<div field="fxqd" width="70px"  headerAlign="center" align="center" >分销渠道</div>
				<div field="cpz" width="60px"  headerAlign="center" align="center" >产品组</div>
				<div field="kmszz" width="85px"  headerAlign="center" align="center" >科目设置组</div>
				<div field="jhgc" width="70px" headerAlign="center" align="center" >交货工厂</div>
				<div field="zl" width="70px" headerAlign="center" align="center" >重量</div>
				<div field="gys" width="115px" headerAlign="center" align="center" >供应商</div>
				<div field="bzcz" width="100px" headerAlign="center" align="center" >备注（材质）</div>
				<div field="jg" width="80px" headerAlign="center" align="center" renderer="hideJg">价格（元）</div>
				<div field="mrpkzz" width="90px" headerAlign="center" align="center" >MRP控制者</div>
                <div field="jclx" width="90px" headerAlign="center" align="center" renderer="jclxRender">集采类型</div>
				<div field="cglx" width="70px" headerAlign="center" align="center" >采购类型</div>
				<div field="tscgl" width="85px" headerAlign="center" align="center" >特殊采购类</div>
				<div field="cgz" width="70px" headerAlign="center" align="center" >采购组</div>
				<div field="pslx" width="75px" headerAlign="center" align="center" >配送类型</div>
				<div field="pldx" width="75px" headerAlign="center" align="center" >批量大小</div>
				<div field="dljz" width="70px" headerAlign="center" align="center" >独立集中</div>
				<div field="cgccdd" width="100px" headerAlign="center" align="center" >采购存储地点</div>
				<div field="jhjhsj" width="100px" headerAlign="center" align="center" >计划交货时间（数字）</div>
				<div field="pgl" width="70px" headerAlign="center" align="center" >评估类</div>
				<div field="lrzx" width="70px" headerAlign="center" align="center" >利润中心</div>
				<div field="jgdw" width="70px" headerAlign="center" align="center" >价格单位</div>
				<div field="xlhcswj" width="115px" headerAlign="center" align="center" >序列号参数文件</div>
				<div field="fc" width="70px" headerAlign="center" align="center" >反冲</div>
				<div field="zzscsj" width="100px" headerAlign="center" align="center" >自制生产时间</div>
				<div field="scccdd" width="100px" headerAlign="center" align="center" >生产仓储地点</div>
				<div field="kyxjc" width="80px" headerAlign="center" align="center" >可用性检查</div>
				<div field="mrplx" width="70px" headerAlign="center" align="center" >MRP类型</div>
				<div field="kcdd1" width="80px" headerAlign="center" align="center" >库存地点1</div>
				<div field="kcdd2" width="80px" headerAlign="center" align="center" >库存地点2</div>
				<div field="kcdd3" width="80px" headerAlign="center" align="center" >库存地点3</div>
				<div field="codeName" width="80px" headerAlign="center" align="center" >关重件名称</div>
				<div field="sfwg" width="80px" headerAlign="center" align="center" >是否创建信息记录</div>
			</div>
		</div>
	</div>
</div>

<div id="importWindow" title="物料导入窗口" class="mini-window" style="width:750px;height:280px;"
	 showModal="true" showFooter="false" allowResize="true">
	<div style="display: inline-block;float: right;">
		<a id="importBtn" class="mini-button"  onclick="importMateriel()">批量导入</a>
		<a class="mini-button btn-red" onclick="closeImportWindow()">关闭</a>
	</div>

	<div class="mini-fit" style="font-size: 14px;margin-top: 50px">
		<form id="formImport" method="post">
			<table class="table-detail" cellspacing="1" cellpadding="0">
				<tr>
					<td style="width: 30%">物料模板：</td>
					<td style="width: 70%;">
						<a href="#" style="color:blue;text-decoration:underline;" onclick="materielTemplateDownload()">物料批量导入模板下载.xls</a>
					</td>
				</tr>
				<tr>
					<td style="width: 30%">文件选择：</td>
					<td>
						<input class="mini-textbox" style="width:60%;float: left" id="fileName" name="fileName" readonly />
						<input id="inputFile" style="display:none;" type="file" onchange="getSelectFile()" accept="application/xls" />
						<a id="uploadFileBtn" class="mini-button"  style="float: left;margin-left: 10px" onclick="uploadFile">选择文件</a>
						<a id="clearFileBtn" class="mini-button"  style="float: left;margin-left: 10px" onclick="clearUploadFile">清除</a>
					</td>
				</tr>
			</table>
		</form>
	</div>
</div>


<script type="text/javascript">
    var materielExtendEditAction="${action}";
    var jsUseCtxPath="${ctxPath}";
    var opRoleName="${opRoleName}";
    var applyObj=${applyObj};
    var currentUserNo="${currentUserNo}";
    var respProperties=${respProperties};
    var currentUserId="${currentUserId}";

    mini.parse();
    var materielApplyForm = new mini.Form("#materielApplyForm");
	var materielGrid = mini.get("#materielGrid");
    var importWindow=mini.get("importWindow");
    //操作栏
    materielGrid.on("update",function(e){
        handGridButtons(e.sender.el);
    });


    function hideJg(e) {
        var sqUserId=mini.get("sqUserId").getValue();
		if("view"==materielExtendEditAction && currentUserId !=sqUserId) {
			return "***";
		}
        var record=e.record;
		return record.jg;
    }
    
    
    function materielOperateRenderer(e) {
		var record=e.record;

        var s = '<span title="查看" onclick="editOrViewMateriel(\'' + record.id +'\',\'view\')">查看</span>';
        if(materielExtendEditAction!='view') {
            s+='<span title="编辑" onclick="editOrViewMateriel(\'' + record.id +'\',\'edit\')">编辑</span>';
            //只有申请人可以删除
            if (opRoleName == 'SQRKC'||currentUserNo=='admin') {
                s+='<span  title="删除" onclick="deleteMateriel('+JSON.stringify(record).replace(/"/g, '&quot;')+')">删除</span>';
            } else {
                s+='<span  title="删除" style="color: silver">删除</span>';
			}
        } else {
            s+='<span  title="编辑" style="color: silver">编辑</span>';
            s+='<span  title="删除" style="color: silver">删除</span>';
		}
        return s;
    }
    materielGrid.on("drawcell",function(e){
        var record=e.record;
		if(e.field=='extendResult') {
		    if(record.extendResult) {
                e.cellHtml=record.extendResult=='success'?'<span style="color: #39cc0b">扩充成功</span>':'<span style="color: #f30806">扩充失败</span>';
			}
		}
        if(e.field=='markError') {
            if(record.markError) {
                e.cellHtml=record.markError=='no'?'<span style="color: #39cc0b">否</span>':'<span style="color: #f30806">是</span>';
            }
        }
	});
    
    function jclxRender(e) {
        var record=e.record;
        var jclx=record.jclx;
        if(!jclx) {
            return "";
        } else if(jclx=='fjc') {
            return "非集采";
        } else if(jclx=='bsjc') {
            return "保税集采";
        } else if(jclx=='gyjc') {
            return "供应集采";
        }
    }
    function showLoading() {
        $("#loading").css('display','');
        $("#content").css('display','none');
    }

    function hideLoading() {
        $("#loading").css('display','none');
        $("#content").css('display','');
    }

</script>
</body>
</html>