<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html >
<html>
<head>
    <title>组件表单</title>
    <%@include file="/commons/edit.jsp"%>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css" />
    <script src="${ctxPath}/scripts/serviceEngineering/jxxqxfEdit.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div id="toolBar" class="topToolBar">
    <div>
        <a id="submit" class="mini-button" style="display: none" onclick="submit()"><spring:message code="page.jxxqxfEdit.name" /></a>
        <a id="closeWindow" class="mini-button btn-red" onclick="CloseWindow()"><spring:message code="page.jxxqxfEdit.name1" /></a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin:0 auto; width: 100%;height:98%;padding: 0">
        <form id="jxxqxfForm" method="post" style="height: 95%;width: 100%">
            <input class="mini-hidden" id="id" name="id" />
            <table class="table-detail grey"  cellspacing="1" cellpadding="0">
                <caption style="font-weight: bold;"><spring:message code="page.jxxqxfEdit.name2" /></caption>
                <tr>
                    <td style="text-align: center;width: 14%"><spring:message code="page.jxxqxfEdit.name3" />：</td>
                    <td style="width: 33%;min-width:170px">
                        <input id="issueDepartmentId" name="issueDepartmentId" class="mini-dep rxc" plugins="mini-dep"
                               style="width:98%;height:34px"
                               allowinput="false" textname="issueDepartment" length="200" maxlength="200" minlen="0" single="true" initlogindep="false"/>
                    </td>
                    <td style="text-align: center;width: 14%"><spring:message code="page.jxxqxfEdit.name4" />：</td>
                    <td style="width: 33%;min-width:170px">
                        <input id="productDepartmentId" name="productDepartmentId" class="mini-dep rxc" plugins="mini-dep"
                               style="width:98%;height:34px"
                               allowinput="false" textname="productDepartment" length="200" maxlength="200" minlen="0" single="true" initlogindep="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 14%"><spring:message code="page.jxxqxfEdit.name5" />：</td>
                    <td style="width: 36%;min-width:170px">
                        <input id="versionType" name="versionType" class="mini-combobox" style="width:98%"
                               textField="value" valueField="key" emptyText="<spring:message code="page.jxxqxfEdit.name6" />..."
                               required="false" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.jxxqxfEdit.name6" />..."
                               data="[{key:'cgb',value:'常规版'},{key:'wzb',value:'完整版'}]"
                        />
                    </td>
                    <td style="text-align: center;width: 14%"><spring:message code="page.jxxqxfEdit.name7" />：</td>
                    <td style="width: 36%;min-width:170px">
                        <input id="productType" name="productType" class="mini-combobox" style="width:98%"
                               textField="value" valueField="key" emptyText="<spring:message code="page.jxxqxfEdit.name6" />..."
                               required="false" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.jxxqxfEdit.name6" />..."
                               data="[{key:'lunWa',value:'轮挖'},{key:'lvWa',value:'履挖'},{key:'teWa',value:'特挖'},{key:'dianWa',value:'电挖'}]"
                        />
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 14%"><spring:message code="page.jxxqxfEdit.name8" />：</td>
                    <td style="width: 36%;min-width:170px">
                        <input id="materialCode" name="materialCode"  class="mini-textbox" style="width:98%;" />
                    </td>
                    <td style="text-align: center;width: 14%"><spring:message code="page.jxxqxfEdit.name9" />：</td>
                    <td style="width: 36%;min-width:170px">
                        <input id="salesModel" name="salesModel"  class="mini-textbox" style="width:98%;" />
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 14%"><spring:message code="page.jxxqxfEdit.name10" />：</td>
                    <td style="width: 36%;min-width:170px">
                        <input id="designModel" name="designModel"  class="mini-textbox" style="width:98%;" />
                    </td>
                    <td style="text-align: center;width: 14%"><spring:message code="page.jxxqxfEdit.name11" />：</td>
                    <td style="width: 36%;min-width:170px">
                        <input id="pinCode" name="pinCode"  class="mini-textbox" style="width:98%;" />
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 14%"><spring:message code="page.jxxqxfEdit.name12" />：</td>
                    <td style="width: 36%;min-width:170px">
                        <input id="priority" name="priority" class="mini-combobox rxc" plugins="mini-combobox"
                               style="width:98%;height:34px" label="<spring:message code="page.jxxqxfEdit.name12" />："
                               length="50"
                               only_read="false" required="false" allowinput="false" mwidth="100"
                               wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                               textField="text" valueField="key_" emptyText="<spring:message code="page.jxxqxfEdit.name6" />..."
                               url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=ZYD"
                               nullitemtext="<spring:message code="page.jxxqxfEdit.name6" />..." emptytext="<spring:message code="page.jxxqxfEdit.name6" />..."/>
                    </td>
                    <td style="text-align: center;width: 14%"><spring:message code="page.jxxqxfEdit.name13" />：</td>
                    <td style="width: 36%;min-width:170px">
                        <input id="passBack" name="passBack" class="mini-combobox" style="width:98%"
                               textField="value" valueField="key" emptyText="<spring:message code="page.jxxqxfEdit.name6" />..."
                               required="false" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.jxxqxfEdit.name6" />..."
                               data="[{key:'hcwc',value:'回传完成'},{key:'hcz',value:'回传中'},{key:'whc',value:'未回传'}]"
                        />
                    </td>
                </tr>
                <tr>
                    <td style="width: 36%;text-align: center;width: 14%"><spring:message code="page.jxxqxfEdit.name14" />：</td>
                    <td style="min-width:170px">
                        <input id="passBackNum" name="passBackNum"  class="mini-spinner" miniValue="0" maxValue="30" style="width:98%;" />
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 14%"><spring:message code="page.jxxqxfEdit.name15" />：</td>
                    <td  colspan="3">
						<textarea id="remark" name="remark" class="mini-textarea rxc" plugins="mini-textarea" style="width:99%;height:150px;line-height:25px;" label="" datatype="varchar" length="1000" vtype="length:1000" minlen="0" allowinput="true"
                                  emptytext=""  wunit="%" mheight="150" hunit="px"></textarea>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>


<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var jxxqxfForm = new mini.Form("#jxxqxfForm");
    var jxxqxfId="${jxxqxfId}";
    var action="${action}";
    var currentUserId="${currentUserId}";
    var currentUserName="${currentUserName}";
    var currentTime="${currentTime}";

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
