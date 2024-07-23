
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<title>编辑申请</title>
	<%@include file="/commons/edit.jsp"%>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css" />
	<script src="${ctxPath}/scripts/standardManager/jsStandardDemandEdit.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>

<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="processInfo" class="mini-button" style="display: none" onclick="processInfo()"><spring:message code="page.jsStandardDemandEdit.name" /></a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()"><spring:message code="page.jsStandardDemandEdit.name1" /></a>
    </div>
</div>
<div class="mini-fit">
	<div class="form-container" style="margin: 0 auto">
		<form id="standardDemandApply" method="post">
            <input id="instId" name="instId" class="mini-hidden"/>
            <input id="applyId" name="applyId" class="mini-hidden"/>
			<table class="table-detail" cellspacing="1" cellpadding="0">
                <tr>
                    <td style="width: 20%"><spring:message code="page.jsStandardDemandEdit.name2" />：</td>
                    <td style="min-width:170px">
                        <input class="mini-hidden" id="applyUserId" name="applyUserId" />
                        <input style="width:98%;" class="mini-textbox" id="applyUserName" name="applyUserName" readonly/>
                    </td>
                    <td style="width: 20%"><spring:message code="page.jsStandardDemandEdit.name3" />：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input style="width:98%;" class="mini-textbox" id="applyUserPhone" name="applyUserPhone"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 20%"><spring:message code="page.jsStandardDemandEdit.name4" />：</td>
                    <td style="min-width:170px">
                        <input style="width:98%;" class="mini-textbox" id="applyUserDeptName" name="applyUserDeptName" readonly/>
                    </td>
                    <td style="width: 20%"><spring:message code="page.jsStandardDemandEdit.name5" />：</td>
                    <td style="min-width:170px">
                        <input id="feedbackType" name="feedbackType" class="mini-combobox" style="width:98%;"
                               textField="key" valueField="value"  allowInput="false" showNullItem="false"
                               required="false" allowInput="false" onvaluechanged="typeChange"
                               data="[ {'key' : '技术标准需求反馈','value' : 'need'},{'key' : '标准使用问题反馈','value' : 'problem'}]"/>
                    </td>

                </tr>
                <tr>
                    <td style="width: 20%"><spring:message code="page.jsStandardDemandEdit.name6" />：</td>
                    <td style="min-width:170px">
                        <input id="doDeptIds" name="doDeptIds" class="mini-dep rxc" plugins="mini-dep"
                               style="width:98%;height:34px" allowinput="false" textname="doDeptNames" length="500" maxlength="500" minlen="0" single="false" initlogindep="false"/>
                    </td>
                    <td id="standardTd1" style="width: 20%;display: none"><spring:message code="page.jsStandardDemandEdit.name7" />：<span style="color:red">*</span></td>
                    <td id="standardTd2" style="min-width:170px;display: none">
                        <input id="problemStandardId" style="width:98%;" class="mini-buttonedit" showClose="true"
                               oncloseclick="onSelectStandardCloseClick()" name="problemStandardId" textname="problemStandardNumber"
                               allowInput="false" onbuttonclick="selectStandard()"/>
                    </td>
				</tr>
                <tr>
                    <td style="width: 20%"><spring:message code="page.jsStandardDemandEdit.name8" />：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="doDeptRespId" name="doDeptRespId" textname="doDeptRespName" class="mini-user rxc"
                               plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="" length="50" maxlength="50"
                               mainfield="no"  single="true" />
                    </td>
                    <td style="width: 20%;"><spring:message code="page.jsStandardDemandEdit.name9" />：<span style="color:red">*</span></td>
                    <td style="min-width:170px;">
                        <input id="acceptStatus" name="acceptStatus" class="mini-combobox" style="width:98%;"
                               textField="key" valueField="value"  allowInput="false" showNullItem="false"
                               required="false" allowInput="false"
                               data="[ {'key' : '采纳','value' : 'all'},{'key' : '部分采纳','value' : 'part'},{'key' : '不采纳','value' : 'no'}]"/>
                    </td>
                </tr>
				<tr>
					<td style="width: 20%"><spring:message code="page.jsStandardDemandEdit.name10" />：<span style="color:red">*</span></td>
					<td colspan="3">
						<textarea id="problemDesc" name="problemDesc" class="mini-textarea rxc" plugins="mini-textarea" style="width:99.1%;height:170px;line-height:25px;"
								  label="<spring:message code="page.jsStandardDemandEdit.name11" />" datatype="varchar" allowinput="true"
								  emptytext="<spring:message code="page.jsStandardDemandEdit.name12" />..." mwidth="80" wunit="%" mheight="200" hunit="px"></textarea>
					</td>
				</tr>
                <tr>
                    <td style="width: 20%"><spring:message code="page.jsStandardDemandEdit.name13" />：<span style="color:red">*</span></td>
                    <td colspan="3">
						<textarea id="standardUserOpinion" name="standardUserOpinion" class="mini-textarea rxc" plugins="mini-textarea" style="width:99.1%;height:170px;line-height:25px;"
                                  label="<spring:message code="page.jsStandardDemandEdit.name14" />" datatype="varchar" allowinput="true"
                                  emptytext="<spring:message code="page.jsStandardDemandEdit.name15" />..." mwidth="80" wunit="%" mheight="200" hunit="px"></textarea>
                    </td>
                </tr>
                <tr>
                    <td style="width: 20%"><spring:message code="page.jsStandardDemandEdit.name16" />：</td>
                    <td colspan="3">
						<textarea id="remark" name="remark" class="mini-textarea rxc" plugins="mini-textarea" style="width:99.1%;height:100px;line-height:25px;"
                                  label="<spring:message code="page.jsStandardDemandEdit.name17" />" datatype="varchar" allowinput="true"
                                  emptytext="<spring:message code="page.jsStandardDemandEdit.name18" />..." mwidth="80" wunit="%" mheight="200" hunit="px"></textarea>
                    </td>
                </tr>
			</table>
		</form>
	</div>
</div>

<div id="selectStandardWindow" title="<spring:message code="page.jsStandardDemandEdit.name19" />" class="mini-window" style="width:750px;height:450px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
    <input id="parentInputScene" style="display: none" />
    <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px"
         borderStyle="border-left:0;border-top:0;border-right:0;">
        <span style="font-size: 14px;color: #777"><spring:message code="page.jsStandardDemandEdit.name20" />: </span>
        <input class="mini-textbox" width="130" id="filterStandardNumberId" style="margin-right: 15px"/>
        <span style="font-size: 14px;color: #777"><spring:message code="page.jsStandardDemandEdit.name21" />: </span>
        <input class="mini-textbox" width="130" id="filterStandardNameId" style="margin-right: 15px"/>
        <a class="mini-button" iconCls="icon-search" plain="true" onclick="searchStandard()"><spring:message code="page.jsStandardDemandEdit.name22" /></a>
    </div>
    <div class="mini-fit">
        <div id="standardListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onrowdblclick="onRowDblClick"
             idField="id"  showColumnsMenu="false" sizeList="[20,50,100,200]" pageSize="20"
             allowAlternating="true" pagerButtons="#pagerButtons" url="${ctxPath}/standardManager/core/standard/queryList.do"
        >
            <div property="columns">
                <div type="checkcolumn" width="30"></div>
                <div field="systemName" sortField="systemName" width="90" headerAlign="center" align="center"
                     allowSort="true"><spring:message code="page.jsStandardDemandEdit.name23" />
                </div>
                <div field="categoryName" sortField="categoryName" width="60" headerAlign="center" align="center"
                     allowSort="true"><spring:message code="page.jsStandardDemandEdit.name24" />
                </div>
                <div field="standardNumber" sortField="standardNumber" width="140" headerAlign="center" align="center"
                     align="center" allowSort="true"><spring:message code="page.jsStandardDemandEdit.name25" />
                </div>
                <div field="standardName" sortField="standardName" width="180" headerAlign="center" align="center"
                     allowSort="true"><spring:message code="page.jsStandardDemandEdit.name26" />
                </div>
                <div field="belongDepName" sortField="belongDepName" width="80" headerAlign="center" align="center"
                     allowSort="true"><spring:message code="page.jsStandardDemandEdit.name27" />
                </div>
                <div field="standardStatus" sortField="standardStatus" width="60" headerAlign="center" align="center"
                     allowSort="true" renderer="statusRenderer"><spring:message code="page.jsStandardDemandEdit.name28" />
                </div>
            </div>
        </div>
    </div>
    <div property="footer" style="padding:5px;height: 35px">
        <table style="width:100%;height: 100%">
            <tr>
                <td style="width:120px;text-align:center;">
                    <input type="button" style="height: 25px;width: 70px" value="<spring:message code="page.jsStandardDemandEdit.name29" />" onclick="selectStandardOK()"/>
                    <input type="button" style="height: 25px;width: 70px" value="<spring:message code="page.jsStandardDemandEdit.name30" />" onclick="selectStandardHide()"/>
                </td>
            </tr>
        </table>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var nodeVarsStr='${nodeVars}';
    var action="${action}";
    var jsUseCtxPath="${ctxPath}";
    var formStatus="${status}";
    var standardDemandObj=${standardDemandObj};
    var standardDemandApply = new mini.Form("#standardDemandApply");

    var selectStandardWindow=mini.get("selectStandardWindow");
    var standardListGrid=mini.get("standardListGrid");

    function statusRenderer(e) {
        var record = e.record;
        var status = record.standardStatus;

        var arr = [{'key': 'draft', 'value': '草稿', 'css': 'orange'},
            {'key': 'enable', 'value': '有效', 'css': 'green'},
            {'key': 'disable', 'value': '已废止', 'css': 'red'}
        ];

        return $.formatItemValue(arr, status);
    }
</script>
</body>
</html>