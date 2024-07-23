<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title></title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<div class="mini-toolbar" >
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul >
<%--                <li style="margin-right: 15px"><span class="text" style="width:auto">员工部门: </span>--%>
<%--                    <input id="userDepId" name="userDepId" class="mini-dep rxc" plugins="mini-dep"--%>
<%--                           data-options="{'single':'false','config':{'type':'','grouplevel':'','groupid':'','groupidText':''}}"--%>
<%--                           style="width:300px;height:34px"--%>
<%--                           allowinput="false" label="部门" textname="bm_name" length="500" maxlength="500" minlen="0" single="false" required="false" initlogindep="false"--%>
<%--                           level="" refconfig="" grouplevel="" groupid="" mwidth="160" wunit="px" mheight="34" hunit="px" />--%>
<%--                </li>--%>
<%--                <li style="margin-right: 15px"><span class="text" style="width:auto">类别: </span>--%>
<%--                    <input id="projectCategory" name="categoryId" class="mini-combobox" style="width:150px;"--%>
<%--                           textField="categoryName" valueField="categoryId" emptyText="请选择..."--%>
<%--                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."/>--%>
<%--                </li>--%>
<%--                <li style="margin-right: 15px">--%>
<%--                    <span class="text" style="width:auto">积分时间 从 </span>:--%>
<%--                    <input id="startTime"  name="startTime"  class="mini-datepicker" format="yyyy-MM-dd" style="width:120px"/>--%>
<%--                </li>--%>
<%--                <li style="margin-right: 15px">--%>
<%--                    <span class="text-to" style="width:auto">至: </span>--%>
<%--                    <input id="endTime" name="endTime" class="mini-datepicker" format="yyyy-MM-dd" style="width:120px" />--%>
<%--                </li>--%>
                <li style="margin-left: 10px">
<%--                    <a class="mini-button" style="margin-right: 5px"  plain="true" onclick="searchFrm()">查询</a>--%>
<%--                    <a class="mini-button btn-red" style="margin-right: 5px"  plain="true" onclick="clearForm()">清空查询</a>--%>
                    <a class="mini-button" style="margin-right: 5px"  plain="true" onclick="exportBtn()"><spring:message code="page.projectDelay.name" /></a>
                </li>
            </ul>
        </form>
        <!--导出Excel相关HTML-->
        <form id="excelForm" action="${ctxPath}/xcmgProjectManager/report/xcmgProject/exportDelayExcel.do" method="post" target="excelIFrame">
            <input type="hidden" name="pageIndex" id="pageIndex"/>
            <input type="hidden" name="pageSize" id="pageSize"/>
            <input type="hidden" name="filter" id="filter"/>
        </form>
        <iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="scoreListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         url="${ctxPath}/xcmgProjectManager/report/xcmgProject/projectDelayList.do" autoLoad="true"
         idField="id"
         multiSelect="false" showColumnsMenu="false" sizeList="[20,50,100,200]" pageSize="20" allowAlternating="true" pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="indexcolumn"></div>
            <div field="projectName" width="390" headerAlign="center" align="left" allowSort="false" renderer="descRender"><spring:message code="page.projectDelay.name1" /></div>
            <div field="delayDay"  headerAlign="center" align="center" ><spring:message code="page.projectDelay.name2" /></div>
            <div field="mainDepName"  sortField="mainDepName"  width="110" headerAlign="center" align="center" allowSort="false"><spring:message code="page.projectDelay.name3" /></div>
            <div field="respMan"  sortField="respMan"  width="80" headerAlign="center" align="center" allowSort="false"><spring:message code="page.projectDelay.name4" /></div>
            <div field="currentStageName"  sortField="currentStageName"  width="100" headerAlign="center" align="center" allowSort="false"><spring:message code="page.projectDelay.name5" /></div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var grid=mini.get("todoDatagrid");
    var currentUserId = "${currentUserId}";
    var permission = ${permission};
    function descRender(e) {
        var record = e.record;
        var projectId=record.projectId;
        var userId = record.userId;
        var s = '';
        if(userId==currentUserId||permission){
            s ='<a href="#" style="cursor: pointer;color: #0a7ac6" onclick="detailProject(\'' + projectId+ '\')">' + record.projectName + '</a>';
        }else{
            s ='<span>' + record.projectName + '</span>';
        }
        return s;
    }
    function exportBtn(){
        var params=[];
        var parent=$(".search-form");
        var inputAry=$("input",parent);
        inputAry.each(function(i){
            var el=$(this);
            var obj={};
            obj.name=el.attr("name");
            if(!obj.name) return true;
            obj.value=el.val();
            params.push(obj);
        });
        $("#filter").val(mini.encode(params));
        var excelForm = $("#excelForm");
        excelForm.submit();
    }
    function onStatusRenderer(e) {
        var record = e.record;
        var status = record.status;

        var arr = [ {'key' : 'DRAFTED','value' : '草稿','css' : 'orange'},
            {'key' : 'RUNNING','value' : '运行中','css' : 'green'},
            {'key' : 'SUCCESS_END','value' : '成功结束','css' : 'blue'},
            {'key' : 'DISCARD_END','value' : '作废','css' : 'red'},
            {'key' : 'ABORT_END','value' : '异常中止结束','css' : 'red'},
            {'key' : 'PENDING','value' : '挂起','css' : 'gray'}
        ];

        return $.formatItemValue(arr,status);
    }
    function detailProject(projectId) {
        var action = "detail";
        var url=jsUseCtxPath+"/xcmgProjectManager/core/xcmgProject/edit.do?action="+action+"&projectId=" + projectId+"&status=RUNNING";
        window.open(url);
    }


</script>
</body>
</html>
