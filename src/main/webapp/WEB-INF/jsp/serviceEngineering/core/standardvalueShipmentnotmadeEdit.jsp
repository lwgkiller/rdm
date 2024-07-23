<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html >
<html>
<head>
    <title>待产品主管制作测试版</title>
    <%@include file="/commons/edit.jsp"%>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css" />
    <script src="${ctxPath}/scripts/serviceEngineering/standardvalueShipmentnotmadeEdit.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div id="toolBar" class="topToolBar">
    <div>
        <a id="submit" class="mini-button" style="display: none" onclick="submit()"><spring:message code="page.standardvalueShipmentnotmadeEdit.name" /></a>
        <a id="closeWindow" class="mini-button btn-red" onclick="CloseWindow()"><spring:message code="page.standardvalueShipmentnotmadeEdit.name1" /></a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin:0 auto; width: 100%;height:98%;padding: 0">
        <form id="zzcsbForm" method="post" style="height: 95%;width: 100%">
            <input class="mini-hidden" id="id" name="id" />
            <input class="mini-hidden" id="responseTime" name="responseTime" />
            <input class="mini-hidden" id="responseStatus" name="responseStatus" />
            <table class="table-detail grey"  cellspacing="1" cellpadding="0">
                <caption style="font-weight: bold;"><spring:message code="page.standardvalueShipmentnotmadeEdit.name2" /></caption>
                <tr>
                    <td style="text-align: center;width: 14%"><spring:message code="page.standardvalueShipmentnotmadeEdit.name3" />：</td>
                    <td style="width: 33%;min-width:170px">
                        <input id="departmentId" name="departmentId" class="mini-dep rxc" plugins="mini-dep"
                               style="width:98%;height:34px"
                               allowinput="false" textname="department" length="200" maxlength="200" minlen="0" single="true" initlogindep="false"/>
                    </td>
                    <td style="text-align: center;width: 14%"><spring:message code="page.standardvalueShipmentnotmadeEdit.name4" />：</td>
                    <td style="width: 36%;min-width:170px">
                        <input id="salesModel" name="salesModel"  class="mini-textbox" style="width:98%;" />
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 14%"><spring:message code="page.standardvalueShipmentnotmadeEdit.name5" />：</td>
                    <td style="width: 36%;min-width:170px">
                        <input id="materialCode" name="materialCode"  class="mini-textbox" style="width:98%;" />
                    </td>
                    <td style="text-align: center;width: 14%"><spring:message code="page.standardvalueShipmentnotmadeEdit.name6" />：</td>
                    <td style="width: 36%;min-width:170px">
                        <input id="materialName" name="materialName"  class="mini-textbox" style="width:98%;" />
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 14%"><spring:message code="page.standardvalueShipmentnotmadeEdit.name7" />：</td>
                    <td style="width: 36%;min-width:170px">
                        <input id="versionType" name="versionType" class="mini-combobox" style="width:98%"
                               textField="value" valueField="key" emptyText="<spring:message code="page.standardvalueShipmentnotmadeEdit.name8" />..."
                               required="false" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.standardvalueShipmentnotmadeEdit.name8" />..."
                               data="[{key:'cgb',value:'常规版'},{key:'csb',value:'测试版'},{key:'wzb',value:'完整版'}]"
                        />
                    </td>
                    <td style="text-align: center;width: 14%"><spring:message code="page.standardvalueShipmentnotmadeEdit.name9" />：</td>
                    <td style="width: 36%;min-width:170px">
                        <input id="pinFour" name="pinFour"  class="mini-textbox" style="width:98%;" />
                    </td>

                </tr>
                <tr>
                    <td style="text-align: center;width: 14%"><spring:message code="page.standardvalueShipmentnotmadeEdit.name10" />：</td>
                    <td style="width: 36%;min-width:170px">
                        <input id="principalId" name="principalId" textname="principal" class="mini-user rxc"
                               plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="<spring:message code="page.standardvalueShipmentnotmadeEdit.name10" />" length="50"
                               mainfield="no"  single="true" />
                    </td>
                    <td style="text-align: center;width: 14%"><spring:message code="page.standardvalueShipmentnotmadeEdit.name11" />：</td>
                    <td style="width: 36%;min-width:170px">
                        <input id="betaCompletion" name="betaCompletion" class="mini-combobox" style="width:98%"
                               textField="value" valueField="key" emptyText="<spring:message code="page.standardvalueShipmentnotmadeEdit.name8" />..." value="dzz"
                               required="false" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.standardvalueShipmentnotmadeEdit.name8" />..."
                               data="[{key: 'dzz',value:'待制作'},{key: 'zzing',value:'制作中'},{key: 'zzwc',value:'制作完成'}]"
                        />
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>


<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var zzcsbForm = new mini.Form("#zzcsbForm");
    var zzcsbId="${zzcsbId}";
    var action="${action}";
    var currentUserId="${currentUserId}";
    var currentUserName="${currentUserName}";
    var currentTime="${currentTime}";
    var mainGroupName='${mainGroupName}';
    //所有非特殊文本都变成这个格式
    function render(e) {
        if (e.value != null && e.value != "") {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            return html;
        }
    }
</script>
</body>
</html>
