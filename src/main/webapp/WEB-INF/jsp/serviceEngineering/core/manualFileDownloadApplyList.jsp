<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>操保手册下载申请列表</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/serviceEngineering/manualFileDownloadApplyList.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar" >
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul >
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.manualFileDownloadApplyList.name" />: </span>
                    <input class="mini-textbox" id="applyNumber" name="applyNumber" />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.manualFileDownloadApplyList.name1" />: </span>
                    <input class="mini-textbox" id="creatorName" name="creatorName" />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.manualFileDownloadApplyList.name2" />:</span>
                    <input id="creatorDeptId" name="creatorDeptId" class="mini-dep rxc" plugins="mini-dep"
                           data-options="{'single':'true','config':{'type':'','grouplevel':'','groupid':'','groupidText':''}}"
                           style="width:160px;height:34px" allowinput="false" label="部门" textname="bm_name" length="500" maxlength="500" minlen="0" single="true" required="false" initlogindep="false"
                           mwidth="160" wunit="px" mheight="34" hunit="px"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">销售型号: </span>
                    <input class="mini-textbox" id="salesModel" name="salesModel" />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">设计型号: </span>
                    <input class="mini-textbox" id="designModel" name="designModel" />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">物料编码: </span>
                    <input class="mini-textbox" id="materialCode" name="materialCode" />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">手册语言: </span>
                    <input class="mini-textbox" id="manualLanguage" name="manualLanguage" />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">手册编码: </span>
                    <input class="mini-textbox" id="manualCode" name="manualCode" />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">版本: </span>
                    <input class="mini-textbox" id="manualVersion" name="manualVersion" />
                </li>


                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.manualFileDownloadApplyList.name3" />: </span>
                    <input id="status" name="status" class="mini-combobox" style="width:120px;"  multiSelect="false"
                           textField="value" valueField="key" emptyText="<spring:message code="page.manualFileDownloadApplyList.name4" />..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.manualFileDownloadApplyList.name4" />..."
                           data="[ {'key' : 'DRAFTED','value' : '草稿'},{'key' : 'RUNNING','value' : '审批中'},{'key' : 'SUCCESS_END','value' : '批准'},{'key' : 'DISCARD_END','value' : '作废'}]"
                    />
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px"  plain="true" onclick="searchFrm()"><spring:message code="page.manualFileDownloadApplyList.name5" /></a>
                    <a class="mini-button btn-red" style="margin-right: 5px"  plain="true" onclick="clearForm()"><spring:message code="page.manualFileDownloadApplyList.name6" /></a>
                    <div style="display: inline-block" class="separator"></div>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="addManualFileDownload()"><spring:message code="page.manualFileDownloadApplyList.name7" /></a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="removeManualFileDownload()"><spring:message code="page.manualFileDownloadApplyList.name8" /></a>
                </li>
            </ul>
        </form>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="manualFileDownloadList" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onlyCheckSelection="true"
         url="${ctxPath}/serviceEngineering/core/manualFileDownloadApply/applyList.do" idField="id"
         multiSelect="true" showColumnsMenu="false" sizeList="[20,50,100]" pageSize="20" allowAlternating="true" pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div name="action" cellCls="actionIcons" width="75" headerAlign="center" align="center" renderer="onActionRenderer" cellStyle="padding:0;"><spring:message code="page.manualFileDownloadApplyList.name9" /></div>
            <div field="applyNumber" width="110"  headerAlign="center" align="center" allowSort="false"><spring:message code="page.manualFileDownloadApplyList.name" /></div>
            <div field="applyUserDeptName" width="70" headerAlign="center" align="center" allowSort="false"><spring:message code="page.manualFileDownloadApplyList.name2" /></div>
            <div field="creatorName"  width="50" headerAlign="center" align="center" allowSort="false"><spring:message code="page.manualFileDownloadApplyList.name1" /></div>
            <div field="taskName" headerAlign='center' align='center' width="120"><spring:message code="page.manualFileDownloadApplyList.name10" /></div>
            <div field="allTaskUserNames" headerAlign='center' align='center' width="80"><spring:message code="page.manualFileDownloadApplyList.name11" /></div>
            <div field="status" width="40" headerAlign="center" align="center" allowSort="true" renderer="onStatusRenderer"><spring:message code="page.manualFileDownloadApplyList.name12" /></div>
            <div field="CREATE_TIME_" dateFormat="yyyy-MM-dd" width="65" align="center" headerAlign="center" allowSort="true"><spring:message code="page.manualFileDownloadApplyList.name13" /></div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var currentUserNo="${currentUserNo}";
    var currentUserId="${currentUserId}";
    var manualFileDownloadList=mini.get("manualFileDownloadList");

    function onActionRenderer(e) {
        var record = e.record;
        var applyId = record.id;
        var instId = record.instId;
        var status = record.status;
        var s = '';
        if (status != 'DRAFTED'){
            s += '<span  title=' + manualFileDownloadApplyList_name + ' onclick="manualFileDownloadDetail(\'' + applyId+ '\',\'' + status + '\')">' + manualFileDownloadApplyList_name + '</span>';
        }
        if (record.status == 'DRAFTED') {
            s += '<span  title=' + manualFileDownloadApplyList_name1 + ' onclick="manualFileDownloadEdit(\'' + applyId + '\',\'' + instId + '\')">' + manualFileDownloadApplyList_name1 + '</span>';
        }
        if(record.status =='RUNNING'){
            if(record.myTaskId) {
                s+='<span  title=' + manualFileDownloadApplyList_name2 + ' onclick="manualFileDownloadTask(\'' + record.myTaskId + '\')">' + manualFileDownloadApplyList_name2 + '</span>';
            }
        }
        if (status == 'DRAFTED' || currentUserNo=='admin') {
            s += '<span  title=' + manualFileDownloadApplyList_name3 + ' onclick="removeManualFileDownload(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">' + manualFileDownloadApplyList_name3 + '</span>';
        }
        return s;
    }
    function onStatusRenderer(e) {
        var record = e.record;
        var status = record.status;

        var arr = [ {'key' : 'DRAFTED','value' : '草稿','css' : 'orange'},
            {'key' : 'RUNNING','value' : '审批中','css' : 'green'},
            {'key' : 'SUCCESS_END','value' : '批准','css' : 'blue'},
            {'key' : 'DISCARD_END','value' : '作废','css' : 'red'}
        ];

        return $.formatItemValue(arr,status);
    }

</script>
<redxun:gridScript gridId="manualFileDownloadList" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl="" />
</body>
</html>

