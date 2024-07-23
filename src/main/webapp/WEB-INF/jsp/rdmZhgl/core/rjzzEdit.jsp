<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<title>软件著作编辑</title>
	<%@include file="/commons/edit.jsp"%>
	<link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css" />
	<script src="${ctxPath}/scripts/rdmZhgl/rjzzEdit.js?version=${static_res_version}" type="text/javascript"></script>
	<script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>

<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
	<div>
		<a id="processInfo" class="mini-button" style="display: none" onclick="processInfo()">流程信息</a>
		<a id="saveRjzzZlgcs" class="mini-button" style="display: none" onclick="saveRjzzZlgcs()">专利工程师保存</a>
		<a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
	</div>
</div>
<div class="mini-fit">
	<div class="form-container" style="margin: 0 auto; width: 80%;">
		<form id="formRjzz" method="post">
			<input id="rjzzId" name="rjzzId" class="mini-hidden"/>
			<input id="instId" name="instId" class="mini-hidden"/>
			<input id="CREATE_BY_" name="CREATE_BY_" class="mini-hidden"/>

			<table class="table-detail grey"  cellspacing="1" cellpadding="0">
				<caption style="font-weight: bold">
					软件著作权申请
				</caption>
				<tr>
					<td style="width: 17%">软件著作编号(提交后自动生成)：</td>
					<td style="width: 30%">
						<input id="rjzzNum" name="rjzzNum"  class="mini-textbox" readonly style="width:98%;" />
					</td>
					<td style="width: 14%">软件名全称：</td>
					<td style="width: 30%">
						<input id="rjmqc" name="rjmqc"  class="mini-textbox" style="width:98%;" />
					</td>
				</tr>
				<tr>
					<td style="width: 14%">软件作品说明：</td>
					<td style="width: 30%">
						<input id="zpsm" name="zpsm" class="mini-combobox" style="width:98%;"
							   textField="value" valueField="key" emptyText="请选择..."
							   required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
							   data="[{'key' : 'YC','value' : '原创'},{'key' : 'XG','value' : '修改'}]"/>
					</td>
					<td style="width: 14%">软件名简称：</td>
					<td style="width: 30%;">
						<input id="rjmjc" name="rjmjc"  class="mini-textbox" style="width:98%;" />
					</td>
				</tr>
				<tr>
					<td style="width: 14%">发表状态：</td>
					<td style="width: 30%">
						<input id="fbzt" name="fbzt" class="mini-combobox" style="width:98%;"
							   textField="value" valueField="key" emptyText="请选择..."
							   required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
							   data="[{'key' : 'YFB','value' : '已发表'},{'key' : 'WFB','value' : '未发表'}]"/>
					</td>
					<td style="width: 14%">软件版本号：</td>
					<td style="width: 30%;">
						<input id="rjbbh" name="rjbbh"  class="mini-textbox" style="width:98%;" />
					</td>
				</tr>
				<tr>
					<td style="width: 14%">软件分类号：</td>
					<td style="width: 30%;">
						<input id="rjflh" name="rjflh"  class="mini-textbox" style="width:98%;" />
					</td>
					<td style="width: 14%">开发完成日期：</td>
					<td style="width: 30%">
					<input id="wcTime" name="wcTime"  class="mini-datepicker" format="yyyy-MM-dd"
					showTime="false" showOkButton="false" showClearButton="true" style="width:98%;" />
					</td>
				</tr>
				<tr>
					<td style="width: 14%">软件开发者：</td>
					<td style="width: 30%">
					<input id="rjkfIds" name="rjkfIds" textname="rjkfNames" class="mini-user rxc" plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="可见范围" length="1000" maxlength="1000"  mainfield="no"  single="false" />
					</td>
					<td style="width: 14%">登记时间：</td>
					<td style="width: 30%;">
						<input id="djTime" name="djTime"  class="mini-datepicker" format="yyyy-MM-dd"
							   showTime="false" showOkButton="false" showClearButton="true" style="width:98%;" />
					</td>
				</tr>
				<tr>
					<td style="width: 14%">登记号：</td>
					<td style="width: 30%">
						<input id="djNum" name="djNum"  class="mini-textbox" style="width:98%;" />
					</td>
					<td style="width: 14%">证书号：</td>
					<td style="width: 30%;">
						<input id="zsNum" name="zsNum"  class="mini-textbox" style="width:98%;" />
					</td>
				</tr>
				<tr>
					<td style="width: 14%">首次发表时间/地点：</td>
					<td style="width: 30%;" colspan="4">
						<textarea id="ddsj" name="ddsj" class="mini-textarea rxc" plugins="mini-textarea" style="width:100%;height:50px;line-height:25px;" label="" datatype="varchar" length="1000" vtype="length:1000" minlen="0" allowinput="true"
								  emptytext="发表时间&#10;发表地点"  wunit="%" mheight="150" hunit="px"></textarea>
					</td>
				</tr>
				<tr>
					<td style="width: 14%">著作权人：</td>
					<td style="width: 30%;" colspan="4">
						<textarea id="zzqr" name="zzqr" class="mini-textarea rxc" plugins="mini-textarea" style="width:100%;;height:50px;line-height:25px;" label="" datatype="varchar" length="1000" vtype="length:1000" minlen="0" allowinput="true"
								  emptytext="徐州徐工挖掘机械有限公司、913203016770124928、徐州" mwidth="80" wunit="%" mheight="150" hunit="px"></textarea>
					</td>
				</tr>
				<tr>
					<td style="width: 14%">联系人/联系方式：</td>
					<td style="width: 30%;" colspan="4">
						<textarea id="lxfs" name="lxfs" class="mini-textarea rxc" plugins="mini-textarea" style="width:100%;;height:50px;line-height:25px;" label="" datatype="varchar" length="1000" vtype="length:1000" minlen="0" allowinput="true"
								  emptytext="xxx 15xxxxxx1（手动输入）" mwidth="80" wunit="%" mheight="150" hunit="px"></textarea>
					</td>
				</tr>
				<tr>
					<td style="width: 14%">软件功能：</td>
					<td style="width: 30%;" colspan="4">
						<textarea id="rjgn" name="rjgn" class="mini-textarea rxc" plugins="mini-textarea" style="width:100%;;height:50px;line-height:25px;" label="" datatype="varchar" length="1000" vtype="length:1000" minlen="0" allowinput="true"
								  emptytext="" mwidth="80" wunit="%" mheight="200" hunit="px"></textarea>
					</td>
				</tr>
				<tr>
					<td style="width: 14%">技术特点：</td>
					<td style="width: 30%;" colspan="4">
						<textarea id="jstd" name="jstd" class="mini-textarea rxc" plugins="mini-textarea" style="width:100%;;height:50px;line-height:25px;" label="" datatype="varchar" length="1000" vtype="length:1000" minlen="0" allowinput="true"
								  emptytext="" mwidth="80" wunit="%" mheight="90" hunit="px"></textarea>
					</td>
				</tr>
				<tr>
					<td style="width: 14%">硬件环境：</td>
					<td style="width: 30%;" colspan="4">
						<textarea id="yjhj" name="yjhj" class="mini-textarea rxc" plugins="mini-textarea" style="width:100%;;height:50px;line-height:25px;" label="" datatype="varchar" length="1000" vtype="length:1000" minlen="0" allowinput="true"
								  emptytext="CPU :2.4G + 、内存：8G+、硬盘300G+" mwidth="80" wunit="%" mheight="90" hunit="px"></textarea>
					</td>
				</tr>
				<tr>
					<td style="width: 14%">软件环境：</td>
					<td style="width: 30%;" colspan="4">
						<textarea id="rjhj" name="rjhj" class="mini-textarea rxc" plugins="mini-textarea" style="width:100%;;height:50px;line-height:25px;" label="" datatype="varchar" length="1000" vtype="length:1000" minlen="0" allowinput="true"
								  emptytext="操作系统：Windows 2003+或CentOs 6.5+.Tomcat 8.0+、JDK 1.8 +、MySql 5.5+" mwidth="80" wunit="%" mheight="90" hunit="px"></textarea>
					</td>
				</tr>
				<tr>
					<td style="width: 14%">编程语言及版本：</td>
					<td style="width: 30%;" colspan="4">
						<textarea id="bjyy" name="bjyy" class="mini-textarea rxc" plugins="mini-textarea" style="width:100%;;height:50px;line-height:25px;" label="" datatype="varchar" length="1000" vtype="length:1000" minlen="0" allowinput="true"
								  emptytext="Java1.8、JS、Mysql" mwidth="80" wunit="%" mheight="90" hunit="px"></textarea>
					</td>
				</tr>
				<tr>
					<td style="width: 14%">软件代码行数：</td>
					<td style="width: 30%;" colspan="4">
						<textarea id="dmhs" name="dmhs" class="mini-textarea rxc" plugins="mini-textarea" style="width:100%;;height:50px;line-height:25px;" label="" datatype="varchar" length="1000" vtype="length:1000" minlen="0" allowinput="true"
								  emptytext="" mwidth="80" wunit="%" mheight="90" hunit="px"></textarea>
					</td>
				</tr>
				<tr>
					<td style="width: 14%">应用产品：</td>
					<td style="width: 30%;" colspan="4">
						<textarea id="yycp" name="yycp" class="mini-textarea rxc" plugins="mini-textarea" style="width:100%;;height:50px;line-height:25px;" label="" datatype="varchar" length="1000" vtype="length:1000" minlen="0" allowinput="true"
								  emptytext="" mwidth="80" wunit="%" mheight="90" hunit="px"></textarea>
					</td>
				</tr>
				<tr>
					<td style="width: 14%;height: 300px">附件列表：</td>
					<td colspan="3">
						<div style="margin-top: 10px;margin-bottom: 2px">
							<a id="addFile" class="mini-button"  onclick="addRjzzFile()">添加附件</a>
							<span style="color: red">注：添加附件前，请先进行草稿的保存</span>
						</div>
						<div id="fileListGrid" class="mini-datagrid" style="width: 100%; height: 85%" allowResize="false"
							 idField="id" url="${ctxPath}/zhgl/core/rjzz/getRjzzFileList.do?rjzzId=${rjzzId}" autoload="true"
							 multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true" >
							<div property="columns">
								<div type="indexcolumn" align="center"  width="20">序号</div>
								<div field="fileName"  width="140" headerAlign="center" align="center" >附件名称</div>
								<div field="fjlx"  width="80" headerAlign="center" align="center" renderer="onFjlxRenderer">附件类别</div>
								<div field="fileSize"  width="80" headerAlign="center" align="center" >附件大小</div>
								<div field="action" width="100" headerAlign='center' align="center" renderer="operationRenderer">操作</div>
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
    var status="${status}";
    var jsUseCtxPath="${ctxPath}";
	var fileListGrid=mini.get("fileListGrid");
    var formRjzz = new mini.Form("#formRjzz");
    var rjzzId="${rjzzId}";
    var nodeVarsStr='${nodeVars}';
    var currentUserName="${currentUserName}";
    var currentTime="${currentTime}";
    var isZlgcsUser=${isZlgcsUser};
    var currentUserId="${currentUserId}";
    var currentUserRoles=${currentUserRoles};
    var coverContent=currentUserName+"<br/>"+currentTime+"<br/>徐州徐工挖掘机械有限公司";

	function operationRenderer(e) {
        var record = e.record;
        var cellHtml = '';
        if(action=='edit' || action=='task'){
            cellHtml=returnRjzzPreviewSpan(record.fileName,record.id,record.rjzzId,coverContent);
        }else if(action=='detail'){
            if(currentUserName =='admin' || currentUserId ==record.CREATE_BY_ || whetherIsFgld(currentUserRoles)
				||isZlgcsUser){
                cellHtml=returnRjzzPreviewSpan(record.fileName,record.id,record.rjzzId,coverContent);
            }else if('RJSYSC' == record.fjlx || 'RJYCX' == record.fjlx){
                cellHtml='<span  title="预览" style="color: silver" >预览</span>';
			}else if('ZS' == record.fjlx){
                cellHtml=returnRjzzPreviewSpan(record.fileName,record.id,record.rjzzId,coverContent);
			}
		}
        //增加删除按钮
        if(action=='edit' || (action=='task' && isBianzhi == 'yes')) {
            var deleteUrl="/zhgl/core/rjzz/delRjzzFile.do"
            cellHtml+='&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(\''+record.fileName+'\',\''+record.id+'\',\''+record.rjzzId+'\',\''+deleteUrl+'\')">删除</span>';
        }
        if(action=='edit' || action=='task'){
            cellHtml+='&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="downRjzzLoadFile('+JSON.stringify(record).replace(/"/g, '&quot;')+')">下载</span>';
        }else if(action=='detail'){
            if(currentUserName =='管理员' || currentUserId ==record.CREATE_BY_ || whetherIsFgld(currentUserRoles)
                ||isZlgcsUser){
                cellHtml+='&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
                    'onclick="downRjzzLoadFile('+JSON.stringify(record).replace(/"/g, '&quot;')+')">下载</span>';
            }else if('RJSYSC' == record.fjlx || 'RJYCX' == record.fjlx){
                cellHtml+='&nbsp;&nbsp;&nbsp;<span  title="下载" style="color: silver" >下载</span>';
			}else if('ZS' == record.fjlx){
                cellHtml+='&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
                    'onclick="downRjzzLoadFile('+JSON.stringify(record).replace(/"/g, '&quot;')+')">下载</span>';
			}
		}
        return cellHtml;
    }
    function onFjlxRenderer(e) {
        var record = e.record;
        var fjlx = record.fjlx;
        var arr = [ {'key' : 'RJSYSC','value' : '软件使用手册'},
            {'key' : 'RJYCX','value' : '软件源程序'},
            {'key' : 'ZS','value' : '证书'}
        ];
        return $.formatItemValue(arr,fjlx);
    }

</script>
</body>
</html>
