<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html >
<html>
<head>
    <title>检修标准值表表单</title>
    <%@include file="/commons/edit.jsp"%>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css" />
    <script src="${ctxPath}/scripts/serviceEngineering/jxbzzbxfsqEdit.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="processInfo" class="mini-button" style="display: none" onclick="jxbzzbxfsqProcessInfo()"><spring:message code="page.jxbzzbxfsqEdit.name" /></a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()"><spring:message code="page.jxbzzbxfsqEdit.name1" /></a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin:0 auto; width: 100%;height:98%;padding: 0">
        <form id="jxbzzbxfsqForm" method="post" style="height: 95%;width: 100%">
            <input class="mini-hidden" id="id" name="id" />
            <input id="instId" name="instId" class="mini-hidden"/>
            <input id="step" name="step" class="mini-hidden"/>
            <table class="table-detail grey"  cellspacing="1" cellpadding="0">
                <tr>
                    <td style="text-align: center;width: 14%">
                        <spring:message code="page.jxbzzbxfsqEdit.name2" />：
                    </td>
                    <td style="width: 36%;min-width:170px">
                        <input id="jxbzzbshId" name="jxbzzbshId" textName="materialCode" style="width:98%;"
                               class="mini-buttonedit" onbuttonclick="onButtonEdit" allowInput="false"/>
                    </td>
                    <td style="text-align: center;width: 17%"><spring:message code="page.jxbzzbxfsqEdit.name3" />：</td>
                    <td style="width: 33%;">
                        <input id="CREATE_BY_" name="CREATE_BY_" textname="creator" class="mini-user rxc"
                               plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="<spring:message code="page.jxbzzbxfsqEdit.name3" />" length="50"
                               mainfield="no"  single="true" />
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 14%"><spring:message code="page.jxbzzbxfsqEdit.name4" />：</td>
                    <td style="width: 33%;min-width:170px">
                        <input id="applyDeptId" name="applyDeptId" class="mini-dep rxc" plugins="mini-dep"
                               style="width:98%;height:34px"
                               allowinput="false" textname="applyDept" length="200" maxlength="200" minlen="0" single="true" initlogindep="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 14%;height: 300px"><spring:message code="page.jxbzzbxfsqEdit.name5" />：</td>
                    <td colspan="3">
                        <div id="fileListGrid" class="mini-datagrid" style="width: 100%; height: 85%" allowResize="false"
                             idField="id"
                             multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true" >
                            <div property="columns">
                                <div type="indexcolumn" align="center"  width="20"><spring:message code="page.jxbzzbxfsqEdit.name6" /></div>
                                <div field="fileName"  width="140" headerAlign="center" align="center" ><spring:message code="page.jxbzzbxfsqEdit.name7" /></div>
                                <div field="fileLanguage" width="60" headerAlign="center" align="center">语言</div>
                                <div field="fileSize"  width="80" headerAlign="center" align="center" ><spring:message code="page.jxbzzbxfsqEdit.name8" /></div>
                                <div field="creator" width="100" headerAlign="center" align="center"><spring:message code="page.jxbzzbxfsqEdit.name9" /></div>
                                <div field="CREATE_TIME_" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" headerAlign="center"><spring:message code="page.jxbzzbxfsqEdit.name10" /></div>
                                <div field="action" width="100" headerAlign='center' align="center" renderer="operationRenderer"><spring:message code="page.jxbzzbxfsqEdit.name11" /></div>
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
    var jsUseCtxPath="${ctxPath}";
    var jxbzzbxfsqForm = new mini.Form("#jxbzzbxfsqForm");
    var jxbzzbxfsqId="${jxbzzbxfsqId}";
    var action="${action}";
    var currentUserId="${currentUserId}";
    var currentUserName="${currentUserName}";
    var currentTime="${currentTime}";
    var mainGroupId="${mainGroupId}";
    var mainGroupName="${mainGroupName}";
    var fileListGrid=mini.get("fileListGrid");
    var nodeVarsStr='${nodeVars}';
    var coverContent=currentUserName+"<br/>"+currentTime+"<br/>" + xzxgwjjxyxgs;
    // var importWindow = mini.get("importWindow");


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
